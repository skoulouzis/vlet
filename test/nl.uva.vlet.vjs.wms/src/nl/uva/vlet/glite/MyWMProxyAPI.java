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
 * $Id: MyWMProxyAPI.java,v 1.5 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

//package nl.uva.vlet.glite;
//
///*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
////Jad home page: http://www.geocities.com/kpdus/jad.html
////Decompiler options: packimports(3) radix(10) lradix(10) 
////Source File Name:   WMProxyAPI.java
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.rmi.RemoteException;
//import java.security.cert.CertificateFactory;
//import java.security.cert.X509Certificate;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Properties;
//import java.util.TimeZone;
//
//import javax.xml.rpc.ServiceException;
//import javax.xml.rpc.holders.LongHolder;
//
//import org.apache.axis.client.AxisClient;
//import org.apache.axis.configuration.SimpleProvider;
//import org.apache.log4j.Logger;
//import org.glite.security.delegation.GrDProxyGenerator;
//import org.glite.security.trustmanager.axis.SSLConfigSender;
//import org.glite.wms.wmproxy.AuthenticationFaultException;
//import org.glite.wms.wmproxy.AuthenticationFaultType;
//import org.glite.wms.wmproxy.AuthorizationFaultException;
//import org.glite.wms.wmproxy.AuthorizationFaultType;
//import org.glite.wms.wmproxy.BaseFaultType;
//import org.glite.wms.wmproxy.CredentialException;
//import org.glite.wms.wmproxy.GenericFaultType;
//import org.glite.wms.wmproxy.GetQuotaManagementFaultException;
//import org.glite.wms.wmproxy.GetQuotaManagementFaultType;
//import org.glite.wms.wmproxy.InvalidArgumentFaultException;
//import org.glite.wms.wmproxy.InvalidArgumentFaultType;
//import org.glite.wms.wmproxy.JobIdStructType;
//import org.glite.wms.wmproxy.JobUnknownFaultException;
//import org.glite.wms.wmproxy.JobUnknownFaultType;
//import org.glite.wms.wmproxy.NoSuitableResourcesFaultException;
//import org.glite.wms.wmproxy.OperationNotAllowedFaultException;
//import org.glite.wms.wmproxy.OperationNotAllowedFaultType;
//import org.glite.wms.wmproxy.ProxyInfoStructType;
//import org.glite.wms.wmproxy.ServerOverloadedFaultException;
//import org.glite.wms.wmproxy.ServerOverloadedFaultType;
//import org.glite.wms.wmproxy.ServiceURLException;
//import org.glite.wms.wmproxy.StringAndLongType;
//import org.glite.wms.wmproxy.WMProxyLocator;
//import org.glite.wms.wmproxy.WMProxyStub;
//import org.gridsite.www.namespaces.delegation_1.DelegationExceptionType;
//import org.gridsite.www.namespaces.delegation_1.DelegationSoapBindingStub;
//
////Referenced classes of package org.glite.wms.wmproxy:
////         ServiceURLException, AuthenticationFaultType, AuthenticationFaultException, AuthorizationFaultType, 
////         AuthorizationFaultException, ServerOverloadedFaultType, ServerOverloadedFaultException, GenericFaultType, 
////         ServiceException, CredentialException, InvalidArgumentFaultType, InvalidArgumentFaultException, 
////         OperationNotAllowedFaultType, OperationNotAllowedFaultException, JobUnknownFaultType, JobUnknownFaultException, 
////         GetQuotaManagementFaultType, GetQuotaManagementFaultException, NoSuitableResourcesFaultType, NoSuitableResourcesFaultException, 
////         WMProxyLocator, WMProxyStub, BaseFaultType, ProxyInfoStructType, 
////         JobIdStructType, StringList, DestURIsStructType, StringAndLongList, 
////         JobTypeList, GraphStructType, JdlType
//
//public class MyWMProxyAPI
//{
//    private static WMProxyLocator serviceLocator;
//
//    static
//    {
//        initTransport();
//    }
//
//    private static void initTransport()
//    {
//        String proxyFile = "/tmp/x509up_u601";
//        System.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");
//        System.setProperty("gridProxyStream", proxyFile);
//        System.setProperty("sslCAFiles", "/etc/grid-security/certificates");
//
//        System.setProperty("X509_PROXY_FILE", proxyFile);
//
//        try
//        {
//            // if (false)
//            {
//                serviceLocator = new WMProxyLocator();
//                // Register new protocol HTTPG
//                Properties sslProps = new Properties();
//                sslProps.setProperty("sslProtocol", "SSLv3");
//                sslProps.setProperty("axis.socketSecureFactory",
//                        "org.glite.security.trustmanager.axis.AXISSocketFactory");
//
//                SimpleProvider provider = SSLConfigSender.getTransportProvider(sslProps);
//
//                // //SimpleProvider provider = new SimpleProvider();
//                // SimpleTargetedChain chain = new SimpleTargetedChain(new
//                // HTTPSSender());
//                //                provider.deployTransport("https", chain); //$NON-NLS-1$
//                // chain = new SimpleTargetedChain(new HTTPSender());
//                //                provider.deployTransport("http", chain); //$NON-NLS-1$
//                // // chain = new SimpleTargetedChain(new GSIHTTPSender());
//                //                // provider.deployTransport("httpg", chain); //$NON-NLS-1$
//                // Util.registerTransport();
//
//                serviceLocator.setEngine(new AxisClient(provider));
//                // wmsServiceLocator.setEngine(AxisUtil.getOGSA_AxisEngine());
//            }
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private URL serviceURL;
//
//    private String proxyFile;
//
//    private String certsPath;
//
//    private WMProxyStub serviceStub;
//
//    private DelegationSoapBindingStub grstStub;
//
//    private Logger logger;
//
//    public MyWMProxyAPI(String url, String proxyFile) throws org.glite.wms.wmproxy.ServiceException,
//            ServiceURLException, CredentialException
//    {
//        serviceURL = null;
//        this.proxyFile = null;
//        certsPath = null;
//        // serviceLocator = null;
//        serviceStub = null;
//        grstStub = null;
//        logger = null;
//        FileInputStream proxyStream = null;
//        try
//        {
//            proxyStream = new FileInputStream(proxyFile);
//        }
//        catch (FileNotFoundException e)
//        {
//            System.out.println("Failed retrieving proxy from path:" + e.toString());
//        }
//        certsPath = "";
//        WMProxyAPIConstructor(url, proxyStream, certsPath);
//    }
//
//    public MyWMProxyAPI(String url, String proxyFile, String certsPath) throws org.glite.wms.wmproxy.ServiceException,
//            ServiceURLException, CredentialException
//    {
//        serviceURL = null;
//        this.proxyFile = null;
//        this.certsPath = null;
//        // serviceLocator = null;
//        serviceStub = null;
//        grstStub = null;
//        logger = null;
//        FileInputStream proxyStream = null;
//        try
//        {
//            proxyStream = new FileInputStream(proxyFile);
//        }
//        catch (FileNotFoundException e)
//        {
//            System.out.println("Failed retrieving proxy from path:" + e.toString());
//        }
//        WMProxyAPIConstructor(url, proxyStream, certsPath);
//    }
//
//    public MyWMProxyAPI(String url, InputStream proxyFile) throws org.glite.wms.wmproxy.ServiceException,
//            ServiceURLException, CredentialException
//    {
//        serviceURL = null;
//        this.proxyFile = null;
//        this.certsPath = null;
//        // serviceLocator = null;
//        serviceStub = null;
//        grstStub = null;
//        logger = null;
//        String certsPath = "";
//        WMProxyAPIConstructor(url, proxyFile, certsPath);
//    }
//
//    public MyWMProxyAPI(String url, InputStream proxyFile, String certsPath)
//            throws org.glite.wms.wmproxy.ServiceException, ServiceURLException, CredentialException
//    {
//        serviceURL = null;
//        this.proxyFile = null;
//        this.certsPath = null;
//        // serviceLocator = null;
//        serviceStub = null;
//        grstStub = null;
//        logger = null;
//        WMProxyAPIConstructor(url, proxyFile, certsPath);
//    }
//
//    private void WMProxyAPIConstructor(String url, InputStream proxyFile, String certsPath)
//            throws org.glite.wms.wmproxy.ServiceException, ServiceURLException, CredentialException
//    {
//        logger = Logger.getLogger(org.glite.wms.wmproxy.WMProxyAPI.class);
//        logger.debug("INPUT: url=[" + url + "] - proxyFile = [" + proxyFile.toString() + "] - certsPath=[" + certsPath
//                + "]");
//        try
//        {
//            serviceURL = new URL(url);
//        }
//        catch (MalformedURLException exc)
//        {
//            throw new ServiceURLException(exc.getMessage());
//        }
//        try
//        {
//            BufferedReader in = new BufferedReader(new InputStreamReader(proxyFile));
//            String s;
//            String s2;
//            for (s2 = new String(); (s = in.readLine()) != null; s2 = s2 + s + "\n")
//                ;
//            in.close();
//            String proxyString = s2;
//            this.proxyFile = proxyString;
//        }
//        catch (FileNotFoundException e)
//        {
//            System.out.println("Failed retrieving proxy:" + e.toString());
//        }
//        catch (IOException ioe)
//        {
//            System.out.println("Failed reading proxy:" + ioe.toString());
//        }
//        this.certsPath = certsPath;
//        setUpService();
//    }
//
//    public String getProxyReq(String delegationId) throws Exception
//    {
//        logger.debug("INPUT: delegationId=[" + delegationId + "]");
//        return serviceStub.getProxyReq(delegationId);
//    }
//
//    public String grstGetProxyReq(String delegationId) throws Exception
//    {
//        logger.debug("INPUT: delegationId=[" + delegationId + "]");
//        return grstStub.getProxyReq(delegationId);
//    }
//
//    public void putProxy(String delegationId, String cert) throws Exception
//    {
//        logger.debug("INPUT: cert=[" + cert + "]");
//        logger.debug("INPUT: delegationId=[" + delegationId + "]");
//        logger.debug("Creating proxy from certificate (CreateProxyfromCertReq)");
//        String proxy = createProxyfromCertReq(cert);
//        try
//        {
//            logger.debug("Delegating credential (putProxy)");
//            serviceStub.putProxy(delegationId, proxy);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public void grstPutProxy(String delegationId, String cert) throws CredentialException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        logger.debug("INPUT: cert=[" + cert + "]");
//        logger.debug("INPUT: delegationId=[" + delegationId + "]");
//        logger.debug("Creating proxy from certificate (CreateProxyfromCertReq)");
//        String proxy = createProxyfromCertReq(cert);
//        logger.debug("Delegating credential (putProxy)");
//        try
//        {
//            serviceStub.putProxy(delegationId, proxy);
//        }
//        catch (DelegationExceptionType exc)
//        {
//            throw new CredentialException(exc.getMessage1());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public ProxyInfoStructType getDelegatedProxyInfo(String delegationId) throws AuthorizationFaultException,
//            AuthenticationFaultException, InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//            ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: delegationId=[" + delegationId + "]");
//            return serviceStub.getDelegatedProxyInfo(delegationId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public ProxyInfoStructType getJobProxyInfo(String jobId) throws AuthorizationFaultException,
//            AuthenticationFaultException, InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//            ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: jobId=[" + jobId + "]");
//            return serviceStub.getJobProxyInfo(jobId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public String getVersion() throws Exception
//    {
//        try
//        {
//            return serviceStub.getVersion();
//
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new Exception("Remote Exception:" + exc.getMessage(), exc);
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public JobIdStructType jobRegister(String jdl, String delegationId) throws Exception
//    {
//        try
//        {
//            logger.debug("INPUT: JDL=[" + jdl + "]");
//            logger.debug("INPUT: delegationId=[" + delegationId + "]");
//            return serviceStub.jobRegister(jdl, delegationId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//
//        // exc;
//        // throw new AuthenticationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    }
//
//    public void jobStart(String jobId) throws AuthorizationFaultException, AuthenticationFaultException,
//            OperationNotAllowedFaultException, InvalidArgumentFaultException, JobUnknownFaultException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        logger.debug("INPUT: jobid=[" + jobId + "]");
//        try
//        {
//            serviceStub.jobStart(jobId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (OperationNotAllowedFaultType exc)
//        {
//            throw new OperationNotAllowedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public JobIdStructType jobSubmit(String jdl, String delegationId) throws AuthorizationFaultException,
//            AuthenticationFaultException, InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//            ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: JDL=[" + jdl + "]\n");
//            logger.debug("INPUT: delegationId=[" + delegationId + "]");
//            return serviceStub.jobSubmit(jdl, delegationId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        //     
//        // AuthenticationFaultType exc;
//        // exc;
//        // throw new AuthenticationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new AuthorizationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // ServerOverloadedFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    }
//
//    public void jobCancel(String jobId) throws AuthorizationFaultException, AuthenticationFaultException,
//            OperationNotAllowedFaultException, InvalidArgumentFaultException, JobUnknownFaultException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        logger.debug("INPUT: jobid=[" + jobId + "]");
//        try
//        {
//            serviceStub.jobCancel(jobId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (OperationNotAllowedFaultType exc)
//        {
//            throw new OperationNotAllowedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    // public double getMaxInputSandboxSize()
//    // throws AuthenticationFaultException,
//    // org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    // {
//    // return (double)serviceStub.getMaxInputSandboxSize();
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public StringList getTransferProtocols()
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    // {
//    // return serviceStub.getTransferProtocols();
//    // AuthorizationFaultType exc;
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public StringList getSandboxDestURI(String jobId, String protocol)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // OperationNotAllowedFaultException, InvalidArgumentFaultException,
//    // JobUnknownFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: jobid=[" + jobId + "] - protocol [" + protocol +
//    // "]");
//    // return serviceStub.getSandboxDestURI(jobId, protocol);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new OperationNotAllowedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new JobUnknownFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//
//    // public DestURIsStructType getSandboxBulkDestURI(String jobId, String
//    // protocol)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // OperationNotAllowedFaultException, InvalidArgumentFaultException,
//    // JobUnknownFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: jobid=[" + jobId + "] - protocol [" + protocol +
//    // "]");
//    // return serviceStub.getSandboxBulkDestURI(jobId, protocol);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new OperationNotAllowedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new JobUnknownFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//
//    public void getTotalQuota(LongHolder softLimit, LongHolder hardLimit) throws AuthenticationFaultException,
//            GetQuotaManagementFaultException, org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        try
//        {
//            serviceStub.getTotalQuota(softLimit, hardLimit);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (GetQuotaManagementFaultType exc)
//        {
//            throw new GetQuotaManagementFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public void getFreeQuota(LongHolder softLimit, LongHolder hardLimit) throws AuthenticationFaultException,
//            GetQuotaManagementFaultException, org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        try
//        {
//            serviceStub.getFreeQuota(softLimit, hardLimit);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (GetQuotaManagementFaultType exc)
//        {
//            throw new GetQuotaManagementFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public void jobPurge(String jobId) throws AuthorizationFaultException, AuthenticationFaultException,
//            OperationNotAllowedFaultException, InvalidArgumentFaultException, JobUnknownFaultException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        logger.debug("INPUT: jobid=[" + jobId + "]");
//        try
//        {
//            serviceStub.jobPurge(jobId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (OperationNotAllowedFaultType exc)
//        {
//            throw new OperationNotAllowedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public StringAndLongType[] getOutputFileList(String jobId, String protocol) throws AuthorizationFaultException,
//            AuthenticationFaultException, OperationNotAllowedFaultException, InvalidArgumentFaultException,
//            JobUnknownFaultException, org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: jobid=[" + jobId + "] - protocol [" + protocol + "]");
//            return serviceStub.getOutputFileList(jobId, protocol);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//
//        // AuthenticationFaultType exc;
//        // exc;
//        // throw new AuthenticationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new AuthorizationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // ServerOverloadedFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // OperationNotAllowedFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new JobUnknownFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    }
//
//    public StringAndLongType[] jobListMatch(String jdl, String delegationId) throws AuthorizationFaultException,
//            AuthenticationFaultException, InvalidArgumentFaultException, NoSuitableResourcesFaultException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: JDL=[" + jdl + "] - deleagtionId=[" + delegationId + "]");
//            return serviceStub.jobListMatch(jdl, delegationId);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//
//        // AuthenticationFaultType exc;
//        // exc;
//        // throw new AuthenticationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new AuthorizationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // ServerOverloadedFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // NoSuitableResourcesFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    }
//
//    public void enableFilePerusal(String jobId, String[] fileList) throws AuthorizationFaultException,
//            AuthenticationFaultException, InvalidArgumentFaultException, JobUnknownFaultException,
//            org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        logger.debug("INPUT: jobId=[" + jobId + "]");
//        try
//        {
//            serviceStub.enableFilePerusal(jobId, fileList);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//    }
//
//    public String[] getPerusalFiles(String jobId, String file, boolean allchunks, String protocol)
//            throws AuthorizationFaultException, AuthenticationFaultException, InvalidArgumentFaultException,
//            JobUnknownFaultException, org.glite.wms.wmproxy.ServiceException, ServerOverloadedFaultException
//    {
//        try
//        {
//            logger.debug("INPUT: jobId=[" + jobId + "] - file=[" + file + "] - allchunck=[" + allchunks
//                    + "] - protocol [" + protocol + "]");
//            return serviceStub.getPerusalFiles(jobId, file, allchunks, protocol);
//        }
//        catch (AuthenticationFaultType exc)
//        {
//            throw new AuthenticationFaultException(createExceptionMessage(exc));
//        }
//        catch (AuthorizationFaultType exc)
//        {
//            throw new AuthorizationFaultException(createExceptionMessage(exc));
//        }
//        catch (ServerOverloadedFaultType exc)
//        {
//            throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//        }
//        catch (InvalidArgumentFaultType exc)
//        {
//            throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        }
//        catch (JobUnknownFaultType exc)
//        {
//            throw new JobUnknownFaultException(createExceptionMessage(exc));
//        }
//        catch (GenericFaultType exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        }
//        catch (RemoteException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        catch (Exception exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        // AuthenticationFaultType exc;
//        // exc;
//        // throw new AuthenticationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new AuthorizationFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // ServerOverloadedFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new JobUnknownFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//        // exc;
//        // throw new
//        // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        // exc;
//        // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    }
//
//    // public String getJobTemplate(JobTypeList jobType, String executable,
//    // String arguments, String requirements, String rank)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: executable=[" + executable + "] - arguments=[" +
//    // arguments + "]");
//    // logger.debug("INPUT: requirements=[" + requirements + "] - rank=[" + rank
//    // + "]");
//    // return serviceStub.getJobTemplate(jobType, executable, arguments,
//    // requirements, rank);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public String getDAGTemplate(GraphStructType dependencies, String
//    // requirements, String rank)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: requirements=[" + requirements + "] - rank=[" + rank
//    // + "]");
//    // return serviceStub.getDAGTemplate(dependencies, requirements, rank);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public String getCollectionTemplate(int jobNumber, String requirements,
//    // String rank)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: requirements=[" + requirements + "] - rank=[" + rank
//    // + "]");
//    // return serviceStub.getCollectionTemplate(jobNumber, requirements, rank);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public String getIntParametricJobTemplate(StringList attributes, int
//    // param, int parameterStart, int parameterStep, String requirements, String
//    // rank)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: param=[" + param + "] - parameterStart=[" +
//    // parameterStart + "] - parameterStep=[" + parameterStep + "]");
//    // logger.debug("INPUT: requirements=[" + requirements + "] - rank=[" + rank
//    // + "]");
//    // return serviceStub.getIntParametricJobTemplate(attributes, param,
//    // parameterStart, parameterStep, requirements, rank);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public String getStringParametricJobTemplate(StringList attributes,
//    // StringList param, String requirements, String rank)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // return serviceStub.getStringParametricJobTemplate(attributes, param,
//    // requirements, rank);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//    //
//    // public String getJDL(String jobId, JdlType type)
//    // throws AuthorizationFaultException, AuthenticationFaultException,
//    // InvalidArgumentFaultException, org.glite.wms.wmproxy.ServiceException,
//    // ServerOverloadedFaultException
//    // {
//    // logger.debug("INPUT: jobId=[" + jobId + "] - JdlType=[" + type + "]");
//    // return serviceStub.getJDL(jobId, type);
//    // AuthenticationFaultType exc;
//    // exc;
//    // throw new AuthenticationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new AuthorizationFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new ServerOverloadedFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new InvalidArgumentFaultException(createExceptionMessage(exc));
//    // exc;
//    // throw new
//    // org.glite.wms.wmproxy.ServiceException(createExceptionMessage(exc));
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // exc;
//    // throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//    // }
//
//    private String createProxyfromCertReq(String certReq) throws CredentialException
//    {
//        byte proxy[];
//        proxy = null;
//        ByteArrayInputStream stream = null;
//        CertificateFactory cf = null;
//        X509Certificate cert = null;
//        long lifetime = 0L;
//        GrDProxyGenerator generator = new GrDProxyGenerator();
//        String proxyStream = System.getProperty("gridProxyStream");
//        if (proxyStream == null)
//            throw new CredentialException("proxy file not found at: " + proxyFile);
//
//        try
//        {
//            proxy = proxyStream.getBytes();
//            stream = new ByteArrayInputStream(proxy);
//            cf = CertificateFactory.getInstance("X.509");
//            cert = (X509Certificate) cf.generateCertificate(stream);
//            stream.close();
//            Date now = new Date();
//            lifetime = (cert.getNotAfter().getTime() - now.getTime()) / 3600000L;
//        }
//        catch (Exception exc)
//        {
//            String errmsg = "an error occured while loading the local proxy  (" + proxyFile + "): \n";
//            errmsg = errmsg + exc.toString();
//            throw new CredentialException(errmsg);
//        }
//
//        if (lifetime < 0L)
//            throw new CredentialException("the local proxy has expired (" + proxyFile + ")");
//
//        try
//        {
//            generator.setLifetime((int) lifetime);
//            proxy = generator.x509MakeProxyCert(certReq.getBytes(), proxy, "");
//
//            return new String(proxy);
//        }
//        catch (Exception exc)
//        {
//            throw new CredentialException(exc.getMessage());
//        }
//    }
//
//    private void setUpService() throws org.glite.wms.wmproxy.ServiceException, CredentialException
//    {
//        String protocol = "";
//        try
//        {
//            // serviceLocator = new WMProxyLocator();
//            serviceStub = (WMProxyStub) serviceLocator.getWMProxy_PortType(serviceURL);
//            grstStub = (DelegationSoapBindingStub) serviceLocator.getWMProxyDelegation_PortType(serviceURL);
//        }
//        catch (ServiceException exc)
//        {
//            throw new org.glite.wms.wmproxy.ServiceException(exc.getMessage());
//        }
//        protocol = serviceURL.getProtocol().trim();
//        logger.debug(protocol);
//        if (protocol.compareTo("https") == 0)
//        {
//            System.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");
//            System.setProperty("gridProxyStream", proxyFile);
//        }
//        if (certsPath.length() > 0)
//            System.setProperty("sslCAFiles", certsPath);
//    }
//
//    private String createExceptionMessage(BaseFaultType exc)
//    {
//        String message = "";
//        String date = "";
//        int hours = 0;
//        String ec = exc.getErrorCode();
//        String cause[] = (String[]) exc.getFaultCause();
//        String desc = exc.getDescription();
//        if (desc.length() > 0)
//            message = desc + "\n";
//        String meth = exc.getMethodName();
//        if (meth.length() > 0)
//            message = message + "Method: " + meth + "\n";
//        Calendar calendar = exc.getTimestamp();
//        // Spiros: Dead variable
//        // hours = calendar.get(11) - calendar.get(15) / 3600000;
//        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
//        date = dayStr[calendar.get(7)] + " " + monthStr[calendar.get(2)] + " " + twodigits(calendar.get(5)) + " "
//                + calendar.get(1) + " ";
//        date = date + twodigits(calendar.get(11)) + ":" + twodigits(calendar.get(12)) + ":"
//                + twodigits(calendar.get(13));
//        date = date + " " + calendar.getTimeZone().getID();
//        if (date.length() > 0)
//            message = message + "TimeStamp: " + date + "\n";
//        if (ec.length() > 0)
//            message = message + "ErrorCode: " + ec + "\n";
//        for (int i = 0; i < cause.length; i++)
//            if (i == 0)
//                message = message + "Cause: " + cause[i] + "\n";
//            else
//                message = message + cause[i] + "\n";
//
//        return message;
//    }
//
//    private String twodigits(int n)
//    {
//        String td = "";
//        if (n >= 0 && n < 10)
//            td = "0" + n;
//        else
//            td = "" + n;
//        return td;
//    }
//
//    private static final String monthStr[] = { "Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sept",
//            "Oct", "Nov", "Dec" };
//
//    private static final String dayStr[] = { "Sun", "Mon", "Tue", "Wedn", "Thu", "Fri", "Sat" };
//
//}
//
///*
// * DECOMPILATION REPORT
// * 
// * Decompiled from:
// * /home/ptdeboer/workspace/auxlibs/lib/jglite/glite-wms-wmproxy-api-java.jar
// * Total time: 58 ms Jad reported messages/errors: The class file version is
// * 48.0 (only 45.3, 46.0 and 47.0 are supported) Couldn't fully decompile method
// * getProxyReq Couldn't resolve all exception handlers in method getProxyReq
// * Couldn't fully decompile method grstGetProxyReq Couldn't resolve all
// * exception handlers in method grstGetProxyReq Couldn't fully decompile method
// * getDelegatedProxyInfo Couldn't resolve all exception handlers in method
// * getDelegatedProxyInfo Couldn't fully decompile method getJobProxyInfo
// * Couldn't resolve all exception handlers in method getJobProxyInfo Couldn't
// * fully decompile method getVersion Couldn't resolve all exception handlers in
// * method getVersion Couldn't fully decompile method jobRegister Couldn't
// * resolve all exception handlers in method jobRegister Couldn't fully decompile
// * method jobSubmit Couldn't resolve all exception handlers in method jobSubmit
// * Couldn't fully decompile method getMaxInputSandboxSize Couldn't resolve all
// * exception handlers in method getMaxInputSandboxSize Couldn't fully decompile
// * method getTransferProtocols Couldn't resolve all exception handlers in method
// * getTransferProtocols Couldn't fully decompile method getSandboxDestURI
// * Couldn't resolve all exception handlers in method getSandboxDestURI Couldn't
// * fully decompile method getSandboxBulkDestURI Couldn't resolve all exception
// * handlers in method getSandboxBulkDestURI Couldn't fully decompile method
// * getOutputFileList Couldn't resolve all exception handlers in method
// * getOutputFileList Couldn't fully decompile method jobListMatch Couldn't
// * resolve all exception handlers in method jobListMatch Couldn't fully
// * decompile method getPerusalFiles Couldn't resolve all exception handlers in
// * method getPerusalFiles Couldn't fully decompile method getJobTemplate
// * Couldn't resolve all exception handlers in method getJobTemplate Couldn't
// * fully decompile method getDAGTemplate Couldn't resolve all exception handlers
// * in method getDAGTemplate Couldn't fully decompile method
// * getCollectionTemplate Couldn't resolve all exception handlers in method
// * getCollectionTemplate Couldn't fully decompile method
// * getIntParametricJobTemplate Couldn't resolve all exception handlers in method
// * getIntParametricJobTemplate Couldn't fully decompile method
// * getStringParametricJobTemplate Couldn't resolve all exception handlers in
// * method getStringParametricJobTemplate Couldn't fully decompile method getJDL
// * Couldn't resolve all exception handlers in method getJDL Couldn't fully
// * decompile method createProxyfromCertReq Couldn't resolve all exception
// * handlers in method createProxyfromCertReq Exit status: 0 Caught exceptions:
// */