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
 * $Id: SslUtil.java,v 1.14 2011-06-07 15:41:29 ptdeboer Exp $  
 * $Date: 2011-06-07 15:41:29 $
 */ 
// source: 

package nl.uva.vlet.net.ssl;


import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLStreamHandler;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.dialog.CaCertDialog;
import nl.uva.vlet.dialog.WarningDialog;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.net.ssl.CertificateStore.CaCertOptions;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrl.VRLUtil;
import nl.uva.vlet.vrs.VRSContext;

/**
 * 
 * SslUtil Garbage class for all global SSL configuration methods.
 * Todo: Cleanup; 
 *  
 */
public class SslUtil
{
    private static ClassLogger logger;  
    private static final int DEFAULT_TIMEOUT = 10*1000;  
    private static Hashtable<String,Boolean> allowedHosts=new Hashtable<String,Boolean>(); 

    /**
     * Static initialisation 
     */
    static
    {
        logger=ClassLogger.getLogger(SslUtil.class); 

        // Install My SSLContext:  
        updateDefaultCertificateStore();
    }

    static protected void updateDefaultCertificateStore()
    {
        try
        {
            // Use Default CaCerts instance to initialize default SSL Context. 
            SslUtil.setDefaultHttpsSslContext(CertificateStore.getDefault().createSSLContext("SSLv3"));
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"*** Initialisation Exception ***\n"); 
        }

        // Update Certificates from GridProxyProvider ! 
        GridProxy.getDefault().loadCertificates(); 

    }

    public static void init()
    {
        // static class initialization code (above) does it all. 
        logger.debugPrintf("--- Initialized ---\n"); 
    }

    private static class DummyTrustManager implements javax.net.ssl.X509TrustManager
    {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() 
        {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) 
        {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) 
        {
        }
    }

    public static void initSslHostnameVerifier()
    {

        javax.net.ssl.HostnameVerifier hv=new javax.net.ssl.HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                String peerHost=session.getPeerHost();
                String reverseHost=VRLUtil.resolveHostname(peerHost); 
                String reverseHostOrg=VRLUtil.resolveHostname(hostname); 

                Boolean val1=allowedHosts.get(hostname);  
                Boolean val2=allowedHosts.get(reverseHost);

                // hostname + reverse DNS hostname known ? 
                if ((val1!=null) &&  (val2!=null)) 
                {
                    // they agree ? 
                    if (val1==val2)	
                    {
                        if (val1 && val2)
                            return true;	// both are true => allow 
                        else
                            return false; // both are false => disallow
                    }

                }

                logger.warnPrintf("Checking URL Hostname, PeerHost=%s,%s\n",hostname,reverseHost);
                logger.warnPrintf("Checking URL Hostname, reverse URL Hostname=%s,%s\n",hostname,reverseHostOrg);

                // else ask:

                String buts[]={"Yes","No","Cancel"}; 

                if (GlobalConfig.getGlobalAllowUserInteraction())
                {
                    String message=
                        "Do you want to allow a secure connection to: "+hostname+"?\n";

                    if (hostname.compareTo(reverseHost)!=0)
                        message+="Warning: The resolved hostname differs from the original hostname: "+reverseHost;

                    String value=WarningDialog.showMessage("Allow SSL Connection ?",message,buts); 


                    if (value.compareToIgnoreCase("yes")==0)
                    {
                        allowedHosts.put(hostname,true); 
                        allowedHosts.put(reverseHost,true); 

                        return true;
                    }
                    else if (value.compareToIgnoreCase("no")==0)
                    {
                        allowedHosts.put(hostname,false); 
                        allowedHosts.put(reverseHost,false);
                        return false; 
                    }

                    return false; 
                }
                return true;
            }
        };

        sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultHostnameVerifier(hv); 
    }

    /**
     * Call this to eliminate SSL Certificate Checking 
     */ 

    public static void setNoSslValidation()
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts =  new DummyTrustManager[1];
        trustAllCerts[0]=new DummyTrustManager(); 

        // Install the all-trusting trust manager
        try 
        {
            SSLContext sc = SSLContext.getInstance("SSLv3");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultAllowUserInteraction(true); 
        } 
        catch (Exception e) 
        {
            logger.logException(ClassLogger.ERROR,e,"Failed to create dummy SSL Context\n"); 
        }
    }

    public static void setDefaultHttpsSslContext(SSLContext context)
    {
        try
        {
            sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultSSLSocketFactory(new ExtSSLSocketFactory(context,context.getSocketFactory()));
            //sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultSSLSocketFactory(context.getSocketFactory());
            // sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultAllowUserInteraction(true); 
        } 
        catch (Exception e) 
        {
            logger.logException(ClassLogger.ERROR,e,"Failed to initialize SSLSocketFactory\n"); 
        }	
    }
    //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionImpl.setDefaultHostnameVerifier(hv);

    /** 
     * Create HttpsHandler which conforms to the classes
     * used in the above methods 
     */
    public static URLStreamHandler createHttpsHandler()
    {
        // return class same as initialized above! 
        return new sun.net.www.protocol.https.Handler();
    }

    public static Socket openSSLSocket(String host, int port) throws Exception
    {
        return openSSLSocket(host,port,-1);
    }

    /** Open SSL socket using current keyStore */ 
    public static SSLSocket openSSLSocket(String host, int port,int timeOut) throws Exception
    {
        SSLContext context = CertificateStore.getDefault().createSSLContext("SSLv3");
        return openSSLSocket(context,host,port,timeOut); 
    }

    /** Open SSL socket using current keyStore */ 
    public static SSLSocket openSSLSocket(SSLContext context,String host, int port,int timeOut) throws Exception
    {
        SSLSocketFactory factory = context.getSocketFactory();

        logger.debugPrintf("Opening connection to %s:%d...\n",host,port); 
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);

        if (timeOut<=0) 
            timeOut=DEFAULT_TIMEOUT; 
        socket.setSoTimeout(timeOut);

        logger.debugPrintf("Starting SSL handshake...\n");
        socket.startHandshake();
        logger.debugPrintf("No errors, certificate is already trusted for: %s:%d\n",host,port);

        return socket; 
    }


    public static SSLSocket openSSLv3Socket(String host, int port) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, VlException, Exception 
    {
        return openSSLv3Socket(CertificateStore.getDefault().createSSLContext("SSLv3"),host,port,-1); 
    }

    public static SSLSocket openSSLv3Socket(SSLContext context,String host, int port,int timeout) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, VlException, Exception 
    {
        SSLSocketFactory sslFactory = context.getSocketFactory(); 

        int i;

        logger.debugPrintf("create(%s:%d)",host,port);

        //create SSL socket
        SSLSocket socket = (SSLSocket) sslFactory.createSocket();

        //enable only SSLv3
        socket.setEnabledProtocols(new String[]{"SSLv3"});   //  SSLv2Hello, SSLv3,TLSv1
        //enable only ciphers without RC4 (some bug, probably in older globus)
        String[] ciphers = socket.getEnabledCipherSuites();
        ArrayList<String> al = new ArrayList<String>(ciphers.length);
        for (i = 0; i < ciphers.length; i++) 
        {
            if (ciphers[i].indexOf("RC4") == -1) al.add(ciphers[i]);
        }
        socket.setEnabledCipherSuites((String [])al.toArray(new String[al.size()]));
        //connect as client
        socket.setUseClientMode(true);

        if (timeout<0)
            timeout=10000; 

        socket.setSoTimeout(timeout); //read timeout
        socket.connect(new InetSocketAddress(host, port), 3000); //connect timeout

        return socket; 

        // Do No Use GetSession() ! It starts a handshake!
        // Create or join a SSL session
        //        SSLSession sess = socket.getSession();
        //        //
        //        
        //        if (sess == null) 
        //        {
        //            logger.debugPrintf("sessions is null\n");
        //            return socket;
        //        }

        //        if (logger.isLevelDebug()) //isLoggable(ClassLogger.DEBUG)) 
        //        {
        //            //print all we know
        //            byte[] id = sess.getId();
        //            StringBuffer sb = new StringBuffer(id.length * 2);
        //            for (i = 0; i < id.length; i++) 
        //            {
        //                sb.append(Integer.toHexString(id[i] < 0 ? 256 - id[i] : id[i]));
        //            }
        //                
        //            logger.debugPrintf("\n--- socket ---"
        //                        +"\nSSLSession.id = " + sb.toString()
        //                        +"\ncipherSuite   = " + sess.getCipherSuite()
        //                        +"\nprotocol      = " + sess.getProtocol()
        //                        +"\ncreationTime    = " + (new Date(sess.getCreationTime()))
        //                        +"\nlastAccessedTime= " + (new Date(sess.getLastAccessedTime())) 
        //                        +"\n");
        //                        
        ////                 log.debug("applicationBufferSize= " + sess.getApplicationBufferSize());
        ////                 log.debug("packetBufferSize= " + sess.getPacketBufferSize());
        ////                 log.debug("localPrincipal= " + sess.getLocalPrincipal());
        ////                 log.debug("peerPrincipal= " + sess.getPeerPrincipal());
        //            }
        //            return socket;
    }

    public static void main(String args[])
    {
        // dummy 
    }

    /**
     * Check whether Exception was caused by a certificate error. 
     * Check nested exception stack. */ 
    public static CertificateException getCertificateException(Exception e)
    {
        Throwable cause = e; 
        // analyse exception stack: 
        while(cause.getCause()!=null)
        {
            cause=cause.getCause();
            if (cause instanceof java.security.cert.CertificateException)
            {
                return (java.security.cert.CertificateException)cause;  
            }
        }

        return null; 
    }

    /**
     * Returns default Global user certificate store. 
     * Default location is ~/.vletrc/cacerts or if non existant, the cacerts
     * file from $VLET_INSTALL/etc/cacerts will be copied to ~/.vletrc and that one
     * will be returned. 
     */
    public static CertificateStore getUserCertificateStore() throws VlException
    {
        return CertificateStore.getDefault(); 
    }

    /** 
     * Install certificate from remote host:port destination into the default
     * user's certificate store file in ~/.vletrc/cacerts.  
     */
    public static boolean installCert(String host, int port)  throws Exception
    {
        CertificateStore caCert = getUserCertificateStore(); 
        return installCert(caCert,host,port,null,null);
    }

    /** 
     * Install certificate from remote host:port destination into custom
     * certificate store.   
     */
    public static boolean installCert(CertificateStore cacerts,String host, int port, String optPassphrase,CaCertOptions options)
        throws Exception
    {
        boolean result=_installCert(cacerts,host,port,optPassphrase,options);
        // Update!
        updateDefaultCertificateStore();
        return result; 
    }

    /** Implemenation  */ 
    private static boolean _installCert(CertificateStore certStore,String host, int port, String optPassphrase,CaCertOptions options)
        throws Exception
    {
        // use defaults; 

        if (options==null)
            options=certStore.getOptions();  

        if (port<=0) 
            port=443; 

        //  connect
        SSLContext context = certStore.createSSLContext("SSLv3"); 

        String sslErrorMessage=null;

        // SSLv3 is used by WMS and LB 
        SSLSocket socket=SslUtil.openSSLv3Socket(context,host,port,10000); 

        try
        {
            logger.debugPrintf("--- Starting SSL handshake...\n");
            socket.startHandshake();
            socket.close();
            logger.debugPrintf("<<< No errors, certificate is already trusted\n");
            return true; 
        }
        catch (Exception e)
        {
            Exception certificateException=SslUtil.getCertificateException(e); 

            sslErrorMessage=e.getMessage();
            logger.logException(ClassLogger.DEBUG,e,"<<< Initial SSL Handshake failed. Exception=%s\n",sslErrorMessage);
            logger.debugPrintf("Certificate Exception= %s\n",certificateException); 

            if (certificateException==null)
                throw e; 
        }

        // get the key chain! 
        X509Certificate[] chain = certStore.getSavingTrustManager().getChain(); 

        if (chain == null)
        {
            logger.warnPrintf("Could not obtain server certificate chain\n");
            return false;
        }

        logger.debugPrintf("Server sent " + chain.length + " certificate(s):\n");

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String chainMessage="";//sslerrorMessage; 

        int nrKeys=chain.length; 

        String keySubjects[]=new String[nrKeys]; 
        String keyIssuers[]=new String[nrKeys]; 

        logger.debugPrintf("Total key chain length=%d\n",chain.length);


        for (int i = 0; i < nrKeys; i++)
        {
            X509Certificate cert = chain[i];
            keySubjects[i]=cert.getSubjectDN().toString(); 
            keyIssuers[i]=cert.getIssuerDN().toString(); 

            chainMessage+=" --- Certificate [" + (i + 1) + "] ---\n";  
            chainMessage+="    Subject : " + cert.getSubjectDN() + "\n"; 
            chainMessage+="    Issuer  : " + cert.getIssuerDN()+ "\n";
            sha1.update(cert.getEncoded());
            chainMessage+="    sha1    : " + CertificateStore.toHexString(sha1.digest())+ "\n";
            md5.update(cert.getEncoded());
            chainMessage+="    md5     : " + CertificateStore.toHexString(md5.digest())+ "\n";
        }

        //String options[]={"yes","no","temporary"};
        int opt=0; 

        if (options.interactive==true)
        {
            logger.infoPrintf("Asking interactive for:%s\n",host); 

            opt=CaCertDialog.showDialog(
                    "Certificate Received from: "+host+"\n"
                    +"Accept certificate ?",chainMessage); 

            if ((opt==CaCertDialog.NO) || (opt==CaCertDialog.CANCEL)) 
                return false;
        }
        else if (options.alwaysAccept==false)
        {
            logger.infoPrintf("Rejecting Cert. Interactive==false and alwaysAccept==false for host:%s\n",host); 
            return false; 
        }
        else
        {
            logger.infoPrintf("Accepting Certificate. Interactive==false and alwaysAccept==true for host:%s\n",host); 
            // continue
        }


        /*int opt=doDailog3("Add certificate to add to trusted keystore\n"+chainMessage,options);

    if ((opt==1) || (opt<0))
        return false;
         */
        for (int k=0;k<nrKeys;k++)
        {
            X509Certificate cert = chain[k];
            String alias = createServerKeyID(host,port,k); // host  + "-" + (k + 1);
            logger.debugPrintf("+++ Adding Key: %s +++ \n",alias); 
            logger.debugPrintf(" -  Subject =%s\n",keySubjects[k]); 
            logger.debugPrintf(" -  Issuer  =%s\n",keyIssuers[k]); 

            certStore.addCertificate(alias, cert,false);

        }

        // updateContext(); 
        //SslUtil.setMySslValidation(context);
        // Save:

        // interactive save 
        if (options.interactive==true)
        {
            if (opt!=CaCertDialog.TEMPORARY) 
            {
                logger.infoPrintf("Accepting Certificate. Interactive==false and alwaysAccept==true for host:%s\n",host); 
                certStore.saveKeystore(); // saveKeystore(optPassphrase);
            }
        }
        // not interactive: 
        else if (options.storeAccepted==true)
        {
            logger.infoPrintf("Saving keystore after (default) accepting certificate from host:%s\n",host); 
            certStore.saveKeystore();
        }

        // ===
        // bug: must recreate/reinitialize trustManager, when 
        // updating keyStore.
        // ===
        // FIXED in addCertificatecertStore.getTrustManager(true); 

        return true; 
    }

    private static String createServerKeyID(String host, int port, int k)
    {
        return ""+host+":"+port+"-"+k;
    }
    
    public static ExtSSLSocketFactory createGSSSocketFactory(VRSContext context) throws Exception
    {
        // ---
        // try to create GSS compatible SSL Socket Factory. 
        // ---
        
        Properties sslProps = new Properties();
        sslProps.setProperty(SSLContextManager.PROP_SSL_PROTOCOL, "SSLv3");
        // if identification is needed. 
        sslProps.setProperty(SSLContextManager.PROP_USE_PROXY_AS_IDENTITY,"true"); 
        
       // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY, "false");
       // sslProps.setProperty("axis.socketSecureFactory", "org.glite.security.trustmanager.axis.AXISSocketFactory");
       // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY, "false");

        // sslProps.setProperty("axis.socketSecureFactory",
        // "org.glite.security.trustmanager.axis.AXISSocketFactory");
        // if needed: 
        String proxyFilename=context.getConfigManager().getProxyFilename(); 
        CertificateStore cacert = context.getConfigManager().getCertificateStore();
        
        sslProps.setProperty(SSLContextManager.PROP_CACERTS_LOCATION,cacert.getKeyStoreLocation());
        sslProps.setProperty(SSLContextManager.PROP_CREDENTIALS_PROXY_FILE, proxyFilename);
        
        // init context and return ssl factory. 
        SSLContextManager ctxManager = new SSLContextManager(sslProps);
        ctxManager.initSSLContext(); 
        
        return ctxManager.getSocketFactory(); 
    }

}

