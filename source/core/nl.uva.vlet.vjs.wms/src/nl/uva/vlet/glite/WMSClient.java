/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: WMSClient.java,v 1.28 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package nl.uva.vlet.glite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.glite.WMLBConfig.WMSConfig;
import nl.uva.vlet.net.ssl.SSLContextManager;
import nl.uva.vlet.util.ResourceLoader;

import org.apache.axis.SimpleChain;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.glite.jdl.JobAd;
import org.glite.security.delegation.GrDProxyGenerator;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wms.wmproxy.ProxyInfoStructType;
import org.glite.wms.wmproxy.StringAndLongType;
import org.glite.wms.wmproxy.WMProxyLocator;
import org.glite.wms.wmproxy.WMProxyStub;
import org.glite.wsdl.types.lb.Event;
import org.globus.axis.util.Util;
import org.gridsite.www.namespaces.delegation_1.DelegationSoapBindingStub;

/**
 * Glite WMS API. 
 *
 * @author Spirous Koulouzis, Piter T. de Boer
 */
public class WMSClient
{
    static
    {
        // shutup logger:
        Logger logger = Logger.getLogger(org.glite.security.trustmanager.ContextWrapper.class);
        logger.setLevel(Level.FATAL);
    }

    private WMProxyLocator wmsServiceLocator;

    private WMProxyStub wmsProxyStub;

    private WMSConfig wmsInfo;

    private String delegationID;

    /** Hostname to LBClient cache */
    private Map<String, LBClient> LBClientCache = new Hashtable<String, LBClient>();

    private DelegationSoapBindingStub wmsDelegationStub;

    private Long maxInputSandboxSize;

    public WMSClient(WMSConfig wmsInfo) throws WMSException
    {
        this.wmsInfo = wmsInfo;
        connect();
    }

    private void initTransport() throws WMSException
    {
        try
        {
            SimpleProvider provider;

            // Use User Configuration !
            String proxyFile = this.wmsInfo.getProxyFilename();
            String cacertsDir = GlobalConfig.getUserCertificatesDir().getDirPath();

            // Still needed ?
            // Older Glite implementation needed GLOBAL defined Proxy Variable!
            // System.setProperty( "X509_PROXY_FILE", proxyFile );

            debug("initTransport: using proxy from:" + proxyFile);
            debug("initTransport: certspath =" + cacertsDir);

            // ==================================================================
            // Register new protocol HTTPG
            Properties sslProps = new Properties();

            sslProps.setProperty(SSLContextManager.PROP_SSL_PROTOCOL, "SSLv3");
            sslProps.setProperty(SSLContextManager.PROP_CREDENTIALS_PROXY_FILE, proxyFile);
            
            // Use Proxy for identification purposes ! (default is true anyway) 
            sslProps.setProperty(SSLContextManager.PROP_USE_PROXY_AS_IDENTITY,"true"); 
            
            // ====================================================================

            provider = new SimpleProvider();

            /** Use New Configurable SSLHTTPSender ! */
            org.apache.axis.Handler sslHandler = new nl.uva.vlet.grid.ssl.SSLHTTPSender(sslProps);
            org.apache.axis.Handler transport = new SimpleTargetedChain(new SimpleChain(), sslHandler,
                    new SimpleChain());
            provider.deployTransport("https", transport);

            SimpleTargetedChain chain = new SimpleTargetedChain(new HTTPSender());
            provider.deployTransport("http", chain);

            Util.registerTransport();

            wmsServiceLocator = new WMProxyLocator();
            wmsServiceLocator.setEngine(new AxisClient(provider));

            // Create Stubs:
            wmsProxyStub = (WMProxyStub) wmsServiceLocator.getWMProxy_PortType(wmsInfo.getWMSUri().toURL());
            wmsDelegationStub = (DelegationSoapBindingStub) wmsServiceLocator.getWMProxyDelegation_PortType(wmsInfo
                    .getWMSUri().toURL());
        }
        catch (Exception e)
        {
            throw new WMSException("Couldn't initialize WMS service:" + wmsInfo.getWMSUri(), e);
        }
    }

    public void connect() throws WMSException
    {
        initTransport();
    }

    public void disconnect()
    {
        this.wmsServiceLocator = null;
        this.wmsProxyStub = null;
        this.wmsDelegationStub = null;
    }

    public String getHostname()
    {
        return this.wmsInfo.getHostname();
    }

    public int getPort()
    {
        return this.wmsInfo.getPort();
    }

    public void doDelegation(String delID) throws WMSException
    {
        info("doDelegation:" + delID);

        this.delegationID = delID;

        // Get proxy String
        String proxy;

        try
        {
            // proxy = this.wmsProxyStub.getProxyReq(delID);
            proxy = this.wmsDelegationStub.getProxyReq(delID);

            // send the signed proxy certificate to the server:
            putProxy(delID, proxy);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Exception when performing delegation to:" + wmsInfo.getWMSUri(), e);
        }
    }

    private void putProxy(String delegationId, String cert) throws Exception
    {
        String proxy = createProxyfromCertReq(cert);
        wmsProxyStub.putProxy(delegationId, proxy);
    }

    private String createProxyfromCertReq(String certReq) throws Exception
    {
        GrDProxyGenerator generator = new GrDProxyGenerator();

        String proxyString = getProxyAsString();

        if (proxyString == null)
            throw new Exception("NULL Proxy");

        long lifetime = 0;
        byte[] proxy = null;

        try
        {

            proxy = proxyString.getBytes();
            ByteArrayInputStream stream = new ByteArrayInputStream(proxy);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
            stream.close();
            Date now = new Date();
            lifetime = (cert.getNotAfter().getTime() - now.getTime()) / 3600000L;
        }
        catch (Exception exc)
        {
            throw new WMSException("Couldn't create delegated proxy from:" + proxyString, exc);
        }

        if (lifetime < 0L)
            throw new WMSException("Grid Proxy has expired!");

        generator.setLifetime((int) lifetime);
        byte[] delProxy = generator.x509MakeProxyCert(certReq.getBytes(), proxy);
        return new String(delProxy);
    }

    // Return grid proxy as string:
    private String getProxyAsString() throws WMSException
    {
        File file = new File(this.wmsInfo.getProxyFilename());
        InputStream inps;
        try
        {
            inps = new FileInputStream(file);
            String str = ResourceLoader.getDefault().getText(inps, ResourceLoader.DEFAULT_CHARSET);
            return str;
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Could read proxy file:" + this.wmsInfo.getProxyFilename(), e);
        }
    }

    public String getVersion() throws WMSException
    {
        try
        {
            return this.wmsProxyStub.getVersion();
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Exception when calling getVersion() to:" + wmsInfo.getWMSUri(), e);
        }
    }

    /**
     * Starts the test
     * 
     * @param url
     *            service URL
     * @param jdlFile
     *            the path location of the JDL file
     * @param delegationID
     *            the id to identify the delegation
     * @param propFile
     *            the path location of the configuration file
     * @param proxyFile
     *            the path location of the user proxy file
     * @param certsPath
     *            the path location of the directory containing all the
     *            Certificate Authorities files
     * @return
     * @throws.Exception if any error occurs
     */
    public JobIdStructType submitJdlFile(String jdlFile) throws WMSException
    {
        if (delegationID == null)
            throw new NullPointerException("No delegation ID given yet: perform Delegation first");

        // reads jdl
        JobAd jad = new JobAd();

        try
        {
            jad.fromFile(jdlFile);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't create JDL String from file:" + jdlFile, e);
        }
        String jdlString = jad.toString();

        return submitJdlString(jdlString);
    }

    public void cancelJob(String jobId) throws WMSException
    {
        try
        {
            wmsProxyStub.jobCancel(jobId);
        }
        catch (Exception ex)
        {
            throw WMSUtil.convertException("Couldn't cancel job with ID:" + jobId, ex);
        }

    }

    public JobIdStructType submitJdlString(String jdlString) throws WMSException
    {

        // result = wmsProxyStub.jobRegister(jdlString, this.delegationID);

        // transfer input data

        // wmsProxyStub.jobStart(result.getId());
        // if (true)
        // return null;
        // Prints out the input parameters
        // info ("JobSubmit");
        // info
        // ("************************************************************************************************************************************");
        // info ("WS URL         = [" + wmsInfo.getWMSUri() + "]" );
        // info
        // ("--------------------------------------------------------------------------------------------------------------------------------");
        // info ("proxy          = [" + wmsInfo.getProxyFilename()+ "]" );
        // info
        // ("--------------------------------------------------------------------------------------------------------------------------------");
        // info ("DELEGATION-ID  = [" +delegationID+ "]" );
        // info
        // ("--------------------------------------------------------------------------------------------------------------------------------");
        // info ("=== JDL Start ===\n"+ jdlString+ "\n=== JDL End ===" );
        // info
        // ("--------------------------------------------------------------------------------------------------------------------------------");
        // info ("JDL            = [" + jdlString + "  ]" );
        // info
        // ("--------------------------------------------------------------------------------------------------------------------------------");
        // test

        JobIdStructType result;
        try
        {
            result = this.wmsProxyStub.jobSubmit(jdlString, delegationID);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Failed to submit JDL string", e);
        }

        // test results
        if (result != null)
        {
            info("RESULT:");
            info("=======================================================================");
            info("Your job has been successfully submitted:");
            WMSUtil.printResult(System.out,result);
            info("=======================================================================");
        }
        // end
        info("End of the test");

        return result;
    }

    public ProxyInfoStructType getJobProxyInfo(java.net.URI jobUri) throws WMSException
    {
        try
        {
            return this.wmsProxyStub.getJobProxyInfo(jobUri.toString());
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Error during getJobProxyInfo for jobid:" + jobUri, e);
        }
    }

    /**
     * Set the delegation ID. Does NOT do any delegation !
     */
    public void setDelegationID(String delID)
    {
        this.delegationID = delID;
    }

    protected boolean isConnected()
    {
        return (wmsProxyStub != null);
    }

    /**
     * Returns LBClient from joburi. Uses host and port information from Job
     * URI!
     */
    public LBClient getLBClient(URI joburi) throws WMSException
    {
        synchronized (this.LBClientCache)
        {
            String jobserverid = createLBServerid(joburi);

            LBClient client = this.LBClientCache.get(jobserverid);
            if (client != null)
                return client;

            try
            {
                client = LBClient.createServiceFromJobUri(joburi);

                LBClientCache.put(jobserverid, client);

                debug("LB Client version is:" + client.getVersion());
            }
            catch (Exception e)
            {
                throw WMSUtil.convertException("Couldn't create LBCLient for job:" + joburi, e);
            }

            return client;
        }
    }

    public void clearLBClientCache()
    {
        this.LBClientCache.clear();
    }

    private static String createLBServerid(URI uri)
    {
        return uri.getHost() + ":" + uri.getPort();
    }

    public ArrayList<OutputInfo> getJobOutputs(java.net.URI jobUri) throws WMSException
    {
        try
        {
            StringAndLongType files[];
            files = getOutputFileList(jobUri, "gsiftp");

            if (files == null)
                return null;

            ArrayList<OutputInfo> outputs = new ArrayList<OutputInfo>(files.length);

            for (StringAndLongType file : files)
            {
                try
                {
                    outputs.add(new OutputInfo(file.getName(), file.getSize()));
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
            }

            return outputs;
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't get JobOutputs for job:" + jobUri, e);
        }
    }

    private void debug(String msg)
    {
        Global.debugPrintf(this, "%s\n", msg);
    }

    void info(String msg)
    {
        Global.infoPrintf(this, "%s\n", msg);
    }

    private void error(String msg)
    {
        Global.errorPrintf(this, "%s\n", msg);
    }

    public StringAndLongType[] getOutputFileList(java.net.URI jobUri, String protocol) throws WMSException
    {
        try
        {
            debug("INPUT: jobid=[" + jobUri + "] - protocol [" + protocol + "]");
            return this.wmsProxyStub.getOutputFileList(jobUri.toString(), protocol);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't get output filelist for job:" + jobUri, e);
        }
    }

    public String getCollectionTemplate(int jobNumber, String requirements, String rank) throws WMSException
    {
        try
        {
            return wmsProxyStub.getCollectionTemplate(jobNumber, requirements, rank);
        }

        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't get Collection JDL Template.", e);
        }
    }

    public Event[] getJobEvents(java.net.URI jobUri) throws WMSException
    {
        Event[] events = null;
        try
        {
            events = getLBClient(jobUri).getAllJobEvents(jobUri);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            throw WMSUtil.convertException("Couldn't get job events for:" + jobUri, e);
        }

        return events;
    }

    public void purgelJob(String jobId) throws WMSException
    {
        try
        {
            wmsProxyStub.jobPurge(jobId);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't purge job :" + jobId, e);
        }
    }

    public Long getMaxInputSandboxSize() throws WMSException
    {
        try
        {
            if (this.maxInputSandboxSize == null)
            {
                this.maxInputSandboxSize = wmsProxyStub.getMaxInputSandboxSize();
            }

            return maxInputSandboxSize;
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't get max size for Input Sandbox.", e);
        }
    }

    public JobIdStructType registerJbo(String jdl, String delegationId) throws WMSException
    {
        try
        {
            return wmsProxyStub.jobRegister(jdl, delegationId);
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't register job: ", e);
        }

    }

    public void runJob(String jobId) throws WMSException
    {
        try
        {
            wmsProxyStub.jobStart(jobId);            
            
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't register job: ", e);
        }
    }

    public String[] jobListMatch(String jdlText) throws WMSException
    {
        
        String[] ce; 
        try
        {
            StringAndLongType[] res = wmsProxyStub.jobListMatch(jdlText, delegationID);
            ce = new String[res.length];
            
            
            for(int i=0;i<res.length;i++){
                ce[i] = res[i].getName();
            }
        }
        catch (Exception e)
        {
            throw WMSUtil.convertException("Couldn't get matching CE ", e);
        }
        return ce;
    }
}
