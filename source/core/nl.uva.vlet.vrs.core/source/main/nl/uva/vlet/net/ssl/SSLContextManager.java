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
 * $Id: SSLContextManager.java,v 1.7 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.net.ssl;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.cog.GridProxy;

/** 
 * Creates GLite SSLv3 Context and SocketFactory need to access OGSA/GSI servers. 
 * Based upon GLite code. 
 * SSLContextManager creates and manages one SSLContext.
 *  
 */
public class SSLContextManager
{
    private static ClassLogger logger; 

    {
        logger =ClassLogger.getLogger(SSLContextManager.class);
        //logger.setLevelToDebug();
    }
    
    // === Configurable Properties === //
    
    public static final String PROP_CREDENTIALS_PROXY_FILE     = "gridProxyFile";
    public static final String PROP_SSL_PROTOCOL               = "sslProtocol";
    public static final String PROP_PRIVATE_KEYSTORE_PASSWD    = "privateKeystorePassword"; 
    public static final String PROP_PRIVATE_KEYSTORE_KEY_ALIAS = "privateKeystoreKeyAlias";
    public static final String PROP_PRIVATE_KEYSTORE_LOCATION  = "privateKeystoreLocation";
    public static final String PROP_CACERTS_LOCATION           = "cacertsLocation";
    public static final String PROP_CACERTS_PASSWORD           = "cacertsPassword";
    public static final String PROP_USE_PROXY_AS_IDENTITY      = "enableProxyIdentity"; 
    
    // === DEFAULT VALUES === //
    public static final String SSL_PROTOCOL_DEFAULT_VALUE = "SSLv3";    
    public static final String KEYSTORE_DEFAULT_PASSWD = "internal";
    public static final String KEYSTORE_DEFAULT_PRIVATE_KEY_ALIAS = "privatekey";
 
    // ==== //
    private Properties config;
    
    // initialized values:
    private SSLContext sslContext=null; 
    private SSLServerSocketFactory serverSocketFactory=null;
    private ExtSSLSocketFactory clientSocketFactory=null;
    
    private KeyStore _privateKeystore=null;
    private KeyManager identityKeyManager=null;
    private CertificateStore caCertificateStore; 
    
    public SSLContextManager(Properties inputConfig) throws Exception
    {
        serverSocketFactory = null;
        clientSocketFactory = null;
        config=inputConfig;  
        initSSLContext(); 
    }

    /**
     * Return Initialized Context. Does not throw exception, so it can be reused
     * once the SSLContext has been created using initSSLContext().
     */
    public SSLContext getContext()
    {
        return sslContext;
    }

    // =======================================================================
    // Setter/Getters
    // =======================================================================
    
    
    private String getPrivateKeystorePasswd()
    {
        return getConfigProperty(PROP_PRIVATE_KEYSTORE_PASSWD,KEYSTORE_DEFAULT_PASSWD); 
    }

    public String getPrivateKeystoreLocation()
    {
        return getConfigProperty(PROP_PRIVATE_KEYSTORE_LOCATION,null); 
    }
    
    public String getPrivateKeyAlias()
    {
        return getConfigProperty(PROP_PRIVATE_KEYSTORE_KEY_ALIAS,KEYSTORE_DEFAULT_PRIVATE_KEY_ALIAS);    
    }

    public String getProxyFilename()
    {
        return getConfigProperty(PROP_CREDENTIALS_PROXY_FILE,"/tmp/x509up_u1000");   
    }
    
    public String getProtocol()
    {
        return getConfigProperty(PROP_SSL_PROTOCOL,SSL_PROTOCOL_DEFAULT_VALUE);
    }    

    public String getCaCertsLocation()
    {
        return getConfigProperty(PROP_CACERTS_LOCATION,null); 
    }
    
    public String getCaCertsPassword()
    {
        return getConfigProperty(PROP_CACERTS_PASSWORD,"changeit"); 
    }

    /** 
     * Create SSLContext. Use getSSLContext to get created object. 
     * @throws Exception 
     */ 
    public void initSSLContext() throws Exception
    {
        try
        {
            // Must be initialized: 
            initPrivateKeyManagers();

            // Also Must be configured: 
            initCertificateStore(); 
            
            String protocol=getProtocol(); 
            logger.infoPrintf("Configuring SSLContext with protocol:%s\n",protocol); 
            this.sslContext=caCertificateStore.createSSLContext(protocol);
            logger.infoPrintf("Actually protocl from SSLContext is:%s\n",sslContext.getProtocol()); 

            this.sslContext.getProtocol(); 
            TrustManager managerArray[];

            X509TrustManager mymanager = caCertificateStore.getTrustManager(true); 
            managerArray = (new TrustManager[] {
                    mymanager
            });

            sslContext.init(new KeyManager[]{identityKeyManager}, managerArray, new SecureRandom());

        }
        catch(Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"ContextWrapper initialization failed.\n");
            throw e;
        }
    }
    
    protected void initCertificateStore() throws VlException
    {
        logger.debugPrintf("--- initCertificateStore() ---\n");

        String loc=this.getCaCertsLocation();
        String passwd=this.getCaCertsPassword(); 
            
        if (StringUtil.isEmpty(loc)==false)
        {
            this.caCertificateStore=CertificateStore.loadCertificateStore(loc,passwd,false);
            logger.infoPrintf("Loaded custom cacerts file from:%s\n",loc); 
        }
        else
        {
            this.caCertificateStore=CertificateStore.getDefault();
            logger.infoPrintf("Loaded default cacerts file from:%s\n",caCertificateStore.getKeyStoreLocation());
        }
        
    }

    public SSLServerSocketFactory getServerSocketFactory() throws SSLException
    {
        if(clientSocketFactory != null)
        {
            String msg="Trying to use a client initialized ContextWrapper to create server socket factory.\n";
            logger.errorPrintf("%s\n",msg); 
            throw new SSLException(msg); 
        }
        if(serverSocketFactory == null)
            serverSocketFactory = sslContext.getServerSocketFactory();
        return serverSocketFactory;
    }

    public ExtSSLSocketFactory getSocketFactory() throws SSLException
    {
        if(serverSocketFactory != null)
        {
            String msg="Trying to use a server-use ContextWrapper to create client socket factory.";
            logger.errorPrintf("%s\n",msg); 
            throw new SSLException(msg); 
        }
        
        if(clientSocketFactory == null)
            //socketFactory = new TimeoutSSLSocketFactory(sslContext.getSocketFactory(), null);
            clientSocketFactory = new ExtSSLSocketFactory(sslContext,sslContext.getSocketFactory());
        return clientSocketFactory;
    }

    
    protected void initPrivateKeyManagers() throws Exception 
    {
        try
        {
            logger.debugPrintf("--- initPrivateKeyManagers() ---\n");
            
            // === //
            this.identityKeyManager=null;
            
            this.getPrivateKeyStore(); 

            KeyStore keyStore=this.getPrivateKeyStore(); 
            String privKeyAlias=this.getPrivateKeyAlias();
            String privKeyPasswd=this.getPrivateKeystorePasswd();
            
            if (keyStore!=null)
                identityKeyManager=createSingleKeyManager(keyStore,privKeyAlias,privKeyPasswd); 
            
        }
        catch(CertificateException e)
        {
            logger.logException(ClassLogger.ERROR,e,"CertificateException: The credentials reading failed.\n");  
            throw e;
        }
        catch(NoSuchAlgorithmException e)
        {
            logger.logException(ClassLogger.ERROR,e,"NoSuchAlgorithmException: while reading credentials.\n"); 
            throw e;
        }
        catch(Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"Exception while reading credentialss.\n");  
            throw e;
        }
    }
    
    /**
     * Returns private key manager if not yet initialized.
     */  
    public KeyManager getPrivateKeyManager() throws Exception
    {
        if (this.identityKeyManager==null)
            initPrivateKeyManagers();
        
        return this.identityKeyManager;
    }
    
    public void setPrivateKeystore(KeyStore keystore,String privateKeyAlias,String passwd)
    {
        _updatePrivateKeyStore(keystore,privateKeyAlias,passwd,null); 
    }
    
    /**
     *  Initialize Private Key store with one private Key
     */ 
    public void setPrivateKey(PrivateKey privKey, Certificate[] certificates, String alias,
            String passwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore keyStore=newKeyStore(passwd); 
        keyStore.setKeyEntry(alias,privKey,passwd.toCharArray(),certificates);  
        this._updatePrivateKeyStore(keyStore,alias,passwd,null); 
    }

    public static KeyStore newKeyStore(String passwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null,passwd.toCharArray());
        return keyStore; 
    }
    
    public void setPrivateKeystore(String keystoreLocation,String privateKeyAlias,String passwd) throws Exception
    {
        CertificateStore certStore = CertificateStore.loadCertificateStore(keystoreLocation,passwd,false);
        this._updatePrivateKeyStore(certStore.getKeyStore(),privateKeyAlias,passwd,keystoreLocation);  
    }
    
    /**
     * Get current configured private keystore. 
     * Loads/creates keyStore if not yet initialized. 
     */ 
    public KeyStore getPrivateKeyStore() throws Exception
    {
        if (this._privateKeystore==null)
        {
            //String alias=this.getPrivateKeyAlias(); 
            String keystoreLocation=this.getPrivateKeystoreLocation();
            String passwd=this.getPrivateKeystorePasswd();
            String keyalias=this.getPrivateKeyAlias(); 
            
            // If there is a private keystore and Proxy Identity is not enabled: load keystore: 
            if ( (StringUtil.isEmpty(keystoreLocation)==false) && (getEnableProxyIdentity()==false) ) 
            {
                CertificateStore certStore = CertificateStore.loadCertificateStore(keystoreLocation,passwd,false);
                this._privateKeystore=certStore.getKeyStore();
            }
            // must be enabled!
            else if (getEnableProxyIdentity()==true)
            {
                this._privateKeystore=this.loadProxy().getProxyAsKeystore(keyalias,passwd);  
            }
            else
            {
                // this._privateKeystore=null; // bogus 
            }
        }
        
        return _privateKeystore; 
    }

    /** Whether to use the proxy as identity when contacting remote services.*/ 
    public boolean getEnableProxyIdentity()
    {
        return this.getConfigBool(PROP_USE_PROXY_AS_IDENTITY,false); 
    }

    /** Create key manager which manages one single key */ 
    private KeyManager createSingleKeyManager(KeyStore keyStore,String alias,String passwd) throws Exception
    {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        keyManagerFactory.init(keyStore, passwd.toCharArray());
        X509KeyManager managerImpl = (X509KeyManager)keyManagerFactory.getKeyManagers()[0];
        X509Certificate[] chain = managerImpl.getCertificateChain(alias); 

        // === //
        
        PrivateKey privKey ;
        privKey=(PrivateKey)keyStore.getKey(alias,passwd.toCharArray());
        
        MyX509KeyManager manager=new MyX509KeyManager(chain,privKey); 
       
        for (X509Certificate cert:chain)
        {
            logger.debugPrintf("--- my cert chain ---\n"); 
            logger.debugPrintf(" - subject %s\n",cert.getSubjectDN()); 
        }

        return manager;
    }

    public String getConfigProperty(String key, String defaultValue)
    {
        String value=this.config.getProperty(key); 
        if (value!=null)
            return value;
        return defaultValue; 
    }

    public boolean getConfigBool(String key, boolean defaultValue)
    {
        String value=this.config.getProperty(key);
        if (StringUtil.isNonWhiteSpace(value)==false)
            return defaultValue; 
        
        return Boolean.parseBoolean(value);
    }

    public GridProxy loadProxy() throws VlException
    {
        return GridProxy.loadFrom(this.getProxyFilename());  
    }
    // ========================================================================
    // Private and mutex protected setters/getters!  
    // ========================================================================

    private void _updatePrivateKeyStore(KeyStore keystore,String privateKeyAlias,String passwd,String keystoreLocation) 
    {
        this.config.setProperty(PROP_PRIVATE_KEYSTORE_KEY_ALIAS,privateKeyAlias);
        this.config.setProperty(PROP_PRIVATE_KEYSTORE_PASSWD,passwd);
        if (StringUtil.isEmpty(keystoreLocation)) 
            this.config.remove(PROP_PRIVATE_KEYSTORE_LOCATION); 
        else
            this.config.setProperty(PROP_PRIVATE_KEYSTORE_LOCATION,keystoreLocation);
        
        this._privateKeystore=keystore;
    }

    /** Set/Change property. Make sure to call initSSLContext() after changing properties ! */
    public void setProperty(String name,String value) 
    {
        if (value==null)
        {
            this.config.remove(name);
        }
        else
        {
            this.config.setProperty(name,value);
        }
    }

    
}
