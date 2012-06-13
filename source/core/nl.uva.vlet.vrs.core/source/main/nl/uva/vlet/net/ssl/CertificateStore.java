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
 * $Id: CertificateStore.java,v 1.16 2011-06-24 13:05:40 ptdeboer Exp $  
 * $Date: 2011-06-24 13:05:40 $
 */ 
// source: 

package nl.uva.vlet.net.ssl;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.GlobalUtil;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.exception.VlPasswordException;
import nl.uva.vlet.vrl.VRL;

/**
 * Custom CertificateStore class for X509Certificates.  
 * This class manages a JavaKeystore and saves it. 
 * Added support for PEM and DER Certificates. 
 *  
 */
public class CertificateStore
{
    // ==== Class (static) === //
    
    //private static final int DEFAULT_TIMEOUT = 10*1000;  
    
    private static final String DEFAULT_PASSPHRASE = "changeit";
    
    //private static final String DEFAULT_PRIVATE_KEY_ALIAS = "myprivatekey";

    private static CertificateStore instance=null; 
    
	/** ssl.cacerts.policy */ 
	public static final String SSL_CACERT_POLICY_PROP=GlobalConfig.PROP_SSL_CACERT_POLICY;
	
	public static final String OPT_INTERACTIVE="interactive";
	
	public static final String OPT_STOREACCEPTED="storeAccepted";
	
	public static final String OPT_ALWAYSACCEPT="alwaysAccept";

	/** ssl.cacerts.policy.interactive */
	public static final String SSL_CACERT_POLICY_INTERACTIVE=SSL_CACERT_POLICY_PROP+"."+OPT_INTERACTIVE;
	
	/** ssl.cacerts.policy.storeAccepted */
	public static final String SSL_CACERT_POLICY_STOREACCEPTED=SSL_CACERT_POLICY_PROP+"."+OPT_STOREACCEPTED;
	
	/** ssl.cacerts.policy.alwaysAccept*/
	public static final String SSL_CACERT_POLICY_ALWAYSACCEPT=SSL_CACERT_POLICY_PROP+"."+OPT_ALWAYSACCEPT; 

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
	
	private static ClassLogger logger=null;

    static
    {
        // ClassLoggers: 
        logger=ClassLogger.getLogger(CertificateStore.class);
       //logger.setLevelToDebug();
    }

	/** CaCert handling options */ 
    public static class CaCertOptions
    {
        /** 
         * Whether to ask user. If interactive==false, the fields  "alwaysAccept" 
         * and "storeAccepted" are use control whether to accept and store the certificate. 
         */ 
        public boolean interactive=true;

        /** If interactive==false, alwaysAccept is used */  
        public boolean alwaysAccept=true;
        
        /** If interactive==false, storeAccepted is used */  
        public boolean storeAccepted=true; 

        public CaCertOptions()
        {
             ; //  default 
        }
    }

   static String toHexString(byte[] bytes)
   {
       StringBuilder sb = new StringBuilder(bytes.length * 3);
       for (int b : bytes)
       {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
       }
       
       return sb.toString();
   }

   /**
    * Get default keystore: $HOME/.vletrc/cacerts. 
    * Creates a default keystore file if it doesn't exists
    */ 
   public static synchronized CertificateStore getDefault() throws VlException 
   {
       if (instance==null)
       {
           CertificateStore certStore=new CertificateStore();
           certStore.loadKeystore(getDefaultUserCACertsLocation(),DEFAULT_PASSPHRASE,true);
           // assign only AFTER Succesfull creation ! 
           instance=certStore; 
       }
        
       return instance;
   }

   //   ==== class methods === // 
   
   /** Returns ~/.vletrc/cacerts location */  
   public static String getDefaultUserCACertsLocation()
   {
       return Global.getUserConfigDir().appendPath("cacerts").getPath(); 
   }

   /** Initialize empty keystore */ 
   public static KeyStore createKeystore(String passwd) throws VlException 
   {
       if (passwd==null)
           throw new NullPointerException("Keystore cannot have NULL password.");
       
       try
       {
           KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
           keystore.load(null,passwd.toCharArray());
           return keystore; 
       }
       catch (Exception e)
       {
           throw convertException("Couldn't get create default (empty) keystore.",e); 
       }
   }

   public static VlException convertException(String message,Throwable t)
   {
       String sepchar=""; 
       if (message.endsWith("\n")==false)
               sepchar="\n"; 
       return new VlInternalError(message+sepchar+t.getMessage(),t);
   }
   
   // ============================= //
   // Inner Classes  
   // ============================= //
   
    /** TrustManager which saves the accepted certificate chain */
    public class SavingTrustManager implements X509TrustManager
    {
        // use Default one from Outer class  
        //private final X509TrustManager tm;

        private X509Certificate[] chain;

        SavingTrustManager() 
        {
            // this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
            this.chain = chain;
            
            // auto initialize: 
            try
            {
                getTrustManager(false).checkClientTrusted(chain, authType);
            }
            catch(Exception e)
            {
                throw new CertificateException(e); 
            }
        }

        public X509Certificate[] getChain()
        {
            return chain; 
        }
    }
    
    public static CertificateStore loadCertificateStore(String keyStoreLocation,String passthing, boolean autoInitiliaze) throws VlException
    {
        CertificateStore certStore=new CertificateStore();
        certStore.loadKeystore(keyStoreLocation,passthing,autoInitiliaze);
        
        return certStore; 
    }
    
    /**
     * Create non persistant interal CertificateStore. Does not load automatically 
     * configured certificates not stores the data in a file. 
     */
    public static CertificateStore createInternal(String passwd) throws VlException 
    {
        if (passwd==null)
            throw new NullPointerException("Keystore cannot have NULL password.");
        
        CertificateStore certStore=new CertificateStore(); 
        // Default empty non persistante key store!
        certStore.setKeyStore(createKeystore(passwd),passwd);     
        //certStore.setPassphrase(passwd); 
        certStore.setAutoSave(false); 
        certStore.setAutoLoadCACertificates(false); 
        
        return certStore;
    }
    
    // ============================= //
    // Instance Members
    // ============================= // 
	
    public void setKeyStore(KeyStore keystore, String passwd)
    {
        this._keyStore=keystore;
        this.keystorePassword=passwd;  
    }

    /** Default Options */
	private CaCertOptions cacertOptions=new CaCertOptions(); 
	
	/** Auto initializing default Session keystore */ 
	private KeyStore _keyStore=null; 
	
	/** Auto initializing default TrustManager */ 
	private X509TrustManager defaultTrustManager=null;
	
	//private SSLContext sslContext=null;
	
	private SavingTrustManager savingTrustManager=null;
	
	private String keystorePassword=DEFAULT_PASSPHRASE;

	private String keyStoreLocation=null; 

	private boolean autosave=true;
	
	private Object keyStoreMutex=new Object();

    private String userPrivateKeyAlias=null;  
	   
    private boolean autoLoadCustomCertificates=true;

    //private KeyManager[] myKeymanagers;
    
	protected CertificateStore() 
	{
	    init();
	}
	
	private void init()   
	{
	    // initialize empty store; 
	    keyStoreLocation=null; 
	    userPrivateKeyAlias=null;
	    this._keyStore=null;
 	}

	
	public void setPassphrase(String passp)
	{
	    this.keystorePassword=passp; 
	}
	
	/** Returns ~/.vletrc/cacerts location */  
	public String getKeyStoreLocation()
	{
	    return keyStoreLocation; 
	}
	
    public void setAutoLoadCACertificates(boolean val)
    {
        this.autoLoadCustomCertificates=val; 
    }
    
    public void setAutoSave(boolean val)
    {
        this.autosave=val; 
    }
	
	/**
	 * Whether key store is persistant. A KeyStore is persistant
	 * if it has a storage location. 
	 */
	public boolean isPersistant()
	{
	    return (this.keyStoreLocation!=null);
	}
	
	/** Load or reload keystore */ 
	public boolean loadKeystore() throws VlException
	{
	    return this.loadKeystore(this.getKeyStoreLocation(),this.getPassphrase(),true);  
	}
   
    protected boolean loadKeystore(String passphrasestr) throws VlException
    {
        return this.loadKeystore(this.getKeyStoreLocation(),passphrasestr,true); 
    }
    
    /** Returns configured passphrase or return default value given as argument */ 
    private String getPassphrase()
    {
        return this.keystorePassword; 
    }
    
    public boolean loadKeystore(String keyStoreLoc,String passphrasestr, boolean autoInitialize) throws VlException
    {
        if (passphrasestr==null)
            throw new NullPointerException("Password can not be null."); 

        this.keystorePassword=passphrasestr;
        this.keyStoreLocation=keyStoreLoc;

        // thread save! 
        synchronized(keyStoreMutex)
        {
            this.keyStoreLocation=keyStoreLoc;
		
            if (keyStoreLoc==null)
                throw new NullPointerException("NULL KeyStore Location"); 
            
            char[] passphrase = passphrasestr.toCharArray();
		
            String usercacertsPath=keyStoreLoc;
            String syscacertsPath=Global.getInstallationConfigDir().appendPath("cacerts").getPath(); 
		
    		// check user copy of cacerts
    		if (GlobalUtil.existsFile(keyStoreLoc,true)==false) 
    		{
    			// check installation cacerts 
    			if (autoInitialize)
    			{
    			    if (GlobalUtil.existsFile(syscacertsPath,true))
        			{
        			    try
        			    {
        			        logger.infoPrintf("Using cacerts file from instalation path:%s\n",syscacertsPath);
        			        // make sure .vletrc exists: 
        			        GlobalUtil.mkdir(Global.getUserConfigDir().getPath()); 
        			        GlobalUtil.copyFile(syscacertsPath,usercacertsPath); 
        			        logger.infoPrintf("Copying installation cacerts file to:%s\n",usercacertsPath);
        			    }
        			    catch (Exception e)
        			    {
        	                 logger.logException(ClassLogger.WARN,e,"Copying system cacerts file to user directory FIALED\n");
        			    }
        			}
    			    else
    			    {
                         // create new 
                        logger.infoPrintf("Couldn't create new user cacerts, default cacerts file missing:%s\n",syscacertsPath);    
    			    }
    			}
    			else
    			{
    			    logger.infoPrintf("No user cacerts file. Creating empty one\n");     
    			}
    		}
    		else
    		{
    			logger.debugPrintf("Using user cacerts file:%s\n",usercacertsPath);
    		}
    		
    		logger.debugPrintf("Loading KeyStore: %s\n",usercacertsPath);
    		
    		_keyStore=null; 
    		
    		// Try to load: 
    		if (GlobalUtil.existsFile(usercacertsPath,true))
    		{
    		    InputStream in=null; 
                
                try
                {
                    _keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    
                    in = GlobalUtil.getFileInputStream(usercacertsPath); 
                    _keyStore.load(in, passphrase);
                    in.close();
                }
                catch (Exception e1)
                {
                    logger.logException(ClassLogger.WARN,e1,"Warning: Couldn't read keystore\n");
                    
                    // password error: DO NOT AUTOINITIALIZE; 
                    _keyStore=null; 
                    
                    if (isPasswordException(e1))
                    {
                        throw new VlPasswordException("Invalid password for keystore. Please update password or remove keystore file:"+usercacertsPath,e1);
                    }
                }
    		}
    		
    		if (_keyStore==null)
    		{
                // EMPTY keystore ! 
                try
                {
                    logger.warnPrintf("Will try to creat empty keystore:%s\n",usercacertsPath); 
                    _keyStore=createKeystore(passphrasestr);
                }
                catch (Exception e2)
                {
                    logger.logException(ClassLogger.ERROR,e2,"Couldn't create empty keystore.\n");
                    throw convertException("Couldn't create empty keystore!",e2);
                } 
    		}
    		
        } // END sychronized(keyStoreMutex) // 
        
        if (this.autoLoadCustomCertificates)
        {
            loadCustomCertificates();
        }
        
        checkKeyStore(); 
        
		return true;  
	}
	
    public static boolean isPasswordException(Exception e)
    {
        Throwable cause=e; 
        
        while(cause!=null)
        {
            String msg=cause.getMessage(); 
        
            if ((msg!=null) && (cause instanceof java.security.UnrecoverableKeyException))
            {
                msg=msg.toLowerCase(); 
                if (msg.contains("password verification failed"))
                    return true; 
            }
            
           cause=cause.getCause(); 
        }
        
        return false; 
    }

    /** Add extra certificates from VLET_INSTALL/etc/certificates and ~/.vletrc/certificates */ 
    protected void loadCustomCertificates()
    {
        VRL vrls[]=GlobalConfig.getCACertificateLocations(); 
        
        for (VRL vrl:vrls)
        {
            String dir=vrl.getPath();

            if (GlobalUtil.existsDir(dir))
            {
                logger.infoPrintf("Checking custom certificate folder:%s\n",dir); 

                String files[]=GlobalUtil.list(dir);
                if (files!=null) 
                    for (String file:files)
                    {   
                        if (hasCertExtension(file))
                           try
                           {
                               this.addPEMCertificate(file,false);
                               logger.infoPrintf("Added Custom Certificate:%s\n",file); 
                           }
                           catch (Exception e)
                           {
                                logger.logException(ClassLogger.INFO,e,"Warning: Failed to load Custom Certificate (ignoring):%s\n",file);
                           }
                    }
            }
            else
            {
                logger.infoPrintf("Ignoring non existing custom certificate folder:%s\n",dir); 
            }
        }
    }
    
    public static boolean hasCertExtension(String filename)
    {
        if (StringUtil.isEmpty(filename))
            return false;
        filename=filename.toLowerCase(); 
        if (filename.endsWith(".0"))
            return true;
        if (filename.endsWith(".crt"))
            return true;
        if (filename.endsWith(".pem"))
            return true;
        if (filename.endsWith(".der"))
            return true;
        return false; 
    }
    
    public KeyStore getKeyStore()
    {
        return this._keyStore; 
    }
    
    /**
     * Gets current trust manager. 
     * When certificates have changed, the trust manager must be recreated.
     * Set reload==true to trigger reinitialization. 
     */ 
	public X509TrustManager getTrustManager(boolean reinit) throws VlException
	{
	    try
	    {
    		if ((reinit==false) && (defaultTrustManager!=null))
    			return defaultTrustManager; 
    		
    		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    		// INIT
    		if (this._keyStore==null)
    		   loadKeystore(); 
    		
    		tmf.init(getKeyStore()); 
    		
    		defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
    		
    		return defaultTrustManager;
	    }
	    catch (Exception e)
	    {
	        throw convertException("Failure to create new TrustManager.",e); 
	    }
	}
	
	/**
	 *  Conveniance method to create a custom SSLContext using this certificate store.
	 */
	public SSLContext createSSLContext(String sslProtocol) throws VlException
	{
	    
	    String userKeyAlias=getFirstKeyAlias();
	    
	    //KeyManager[] myKeymanagers=null;
	    KeyManager myKeymanager=null;
        		
		if (userKeyAlias!=null)
		{
		    //myKeymanager=this.createPrivateKeyManager(getFirstKeyAlias());
		    
		    Certificate[] certificateChain = this.getCertificateChain(userKeyAlias); 
		    PrivateKey privateKey = this.getPrivateKey(getFirstKeyAlias()); 
        
		    // Multiple private keys could be added here! 
		    if ((certificateChain!=null) && (privateKey!=null))
		    {
		        logger.infoPrintf("Using default user private key:%s\n",userKeyAlias); 
		        myKeymanager=new MyX509KeyManager(certificateChain, privateKey);
		    }
		    else
		    {
		        logger.warnPrintf("Couldn't find user private key alias:%s",userKeyAlias);  
		    }
		}
		else
		{
		    logger.infoPrintf("NO user private key alias specified. Will not use (private key) user authentication !\n"); 
		}
		 
		return createSSLContext(myKeymanager,sslProtocol);
	}
	
	public SSLContext createSSLContext(KeyManager privateKeyManager,String sslProtocol) throws VlException
    {
	    try
	    {
    	    SSLContext sslContext = SSLContext.getInstance(sslProtocol);
    
    		defaultTrustManager = getTrustManager(false);
    		
    		savingTrustManager = new SavingTrustManager();
    				
    		sslContext.init(
    		        new KeyManager[]{privateKeyManager}, 
    		        new TrustManager[] { savingTrustManager },
    		        null);
    
    		return sslContext;
	    }
	    catch (Exception e)
	    {
	        throw convertException("Failure to intialize SSLContext.",e);
	    }
    		    
	}
	
	// === //
	
	// === // 

	public boolean installCert(String hoststr, String passphrasestr)
			throws VlException
	{
		String host;
		int port;
	
		if (hoststr != null)
		{
			String[] c = hoststr.split(":");
			host = c[0];
			port = (c.length == 1) ? 443 : Integer.parseInt(c[1]);
		}
		else
	    {
		    logger.warnPrintf("Got NULL Hostname. Hostname must be:<host>[:port] [passphrase]\n");
			return false;
		}
		
		return installCert(host,port,passphrasestr,null); 
	}
		
	public boolean installCert(String host, int port, String optPassphrase,CaCertOptions options)
		throws VlException
	{
	    try
	    {
	        return SslUtil.installCert(this, host, port, optPassphrase, options);
	    }
	    catch (Exception e)
	    {
	        throw convertException("Couldn't install host certificate from:"+host+":"+port,e); 
	    }
	}

    public void saveKeystore() throws VlException
    {
        saveKeystore(null,null); 
    }
    
    /** Saves keystore to HOME/.vletrc/cacerts */
    public void saveKeystore(String passwd) throws VlException
    {
        saveKeystore(null,passwd); 
    }
    
    public void saveKeystore(String location, String passwd) throws VlException
    {
        if (autosave==false)
        {
            logger.debugPrintf("saveKeyStore: autosave turned off, not presistant\n");  
            return;
        }
        
        if ((location==null) && (this.keyStoreLocation==null))
        {
            logger.warnPrintf("saveKeyStore: couldn't save keystore: No location defined!\n"); 
            return; 
        }
        
	    if (passwd==null)
	        passwd=this.keystorePassword; 
	    
		char[] passphrase=passwd.toCharArray(); 
		
		if (location==null)
		    location=this.getKeyStoreLocation();
		
		logger.infoPrintf("Saving keyStore to:%s\n",location);
		
		try
		{
    		FileOutputStream fout=new FileOutputStream(new java.io.File(location)); 
    		
    		synchronized(keyStoreMutex)
    		{
    		    _keyStore.store(fout, passphrase);
    		}
    		
    		try { fout.close(); }  catch (Exception e) { ; }
		}
        catch (IOException e)
        {
            throw convertException("IO Error when saving keystore to file:"+location,e); 
        }
        catch (Exception e)
        {
            throw convertException("IO Error when saving keystore to file:"+location,e); 
        }
	}
	
	public void installCert(String hostname, int port) throws VlException
	{
		installCert(hostname,port,null,null);
	}
 
	public X509Certificate[] getX509Certificates() throws VlException
	{
	    return getTrustManager(false).getAcceptedIssuers(); 
	}
	
    /** DER encoded Certificate .cer .crt .der 
     * @return */
    public static X509Certificate loadDERCertificate(String filename) throws Exception
    {
        logger.debugPrintf("Loading (DER ENCODED) Certificate :%s\n",filename);
        
        FileInputStream finps=GlobalUtil.getFileInputStream(filename);
        
        sun.security.x509.X509CertImpl x590=new sun.security.x509.X509CertImpl(finps); 
        String alias=VRL.basename(filename);
       
        logger.debugPrintf("+++ Adding cert file: %s\n",filename);  
        logger.debugPrintf(" -  Alias    = %s\n",alias); 
        logger.debugPrintf(" -  Subject  = %s\n",x590.getSubjectDN().toString()); 
        logger.debugPrintf(" -  Issuer   = %s\n",x590.getIssuerDN().toString());
        return x590; 
    }
    
    /** DER encoded Certificate .cer .crt .der */
    public void addDERCertificate(String filename,boolean save) throws Exception
    {
        X509Certificate x590 = loadDERCertificate(filename); 
        String alias=VRL.basename(filename);
        
        addCertificate(alias, x590,save);
    }
    
    /** Load .pem Certificate file */ 
    public static X509Certificate loadPEMCertificate(String filename) throws Exception
    {
        logger.debugPrintf("Loading (PEM) Certificate :%s\n",filename);  
        
        String pemStr=GlobalUtil.readText(filename);
        
        int index=pemStr.indexOf("-----BEGIN CERTIFICATE"); 
        
        if (index<0)
            throw new IOException("Couldn't find start of (DER) certificate!\n---\n"+pemStr);  
        
        // Get DER part 
        String derStr=pemStr.substring(index);
        return createDERCertificateFromString(derStr); 
    }
    
    /** Load .pem Certificate file 
     * @param save */ 
    public void addPEMCertificate(String filename, boolean save) throws Exception
    {
        X509Certificate x590=loadPEMCertificate(filename);
        String alias=VRL.basename(filename);  
        addCertificate(alias, x590,save);
    }
    
    /**
     * Create DER Encoded Certificate from String.  
     * Certificate String needs to be between:
     * <pre> 
     * -----BEGIN CERTIFICATE-----
     * ...  
     * -----END CERTIFICATE-----
     *</pre>    
     */  
    public static X509Certificate createDERCertificateFromString(String derEncodedString) throws Exception
    {
        byte bytes[] = derEncodedString.getBytes("ASCII"); //plain aksii
        ByteArrayInputStream binps = new ByteArrayInputStream(bytes); 
        
        sun.security.x509.X509CertImpl x590=new sun.security.x509.X509CertImpl(binps); 

        return x590;
    }
    
    /** Add String encoded DER certificate to certificate store. */  
    public void addDERCertificate(String alias,String derEncodedString,boolean save) throws Exception
    {
        X509Certificate x590 = createDERCertificateFromString(derEncodedString); 
        addCertificate(alias,x590,save); 
    }
        
    /** Thread save addCertificate method. Needs alias to store Certificate 
     * @param save */ 
    public void addCertificate(String alias, X509Certificate x590, boolean save) throws Exception
    {
        _masterAddCertificate(alias,x590,save); 
    }
    
    protected void _masterAddCertificate(String alias, X509Certificate x590, boolean save) throws Exception
    {
        logger.debugPrintf("+++ Adding cert +++\n");  
        logger.debugPrintf(" -  Alias    = %s\n",alias); 
        logger.debugPrintf(" -  Subject  = %s\n",x590.getSubjectDN().toString()); 
        logger.debugPrintf(" -  Issuer   = %s\n",x590.getIssuerDN().toString()); 

        synchronized(keyStoreMutex)
        {
            _keyStore.setCertificateEntry(alias, x590);
        }
        
        // Re-initialize! 
        this.defaultTrustManager=null; 
        this.getTrustManager(true);
        
        if (save)
            saveKeystore();
        
        
    }
    

	/** Delete cacerts file. */ 
    public void delete()
    {
        GlobalUtil.deleteFile(this.getKeyStoreLocation());  
    }
    
    /** Returns snapshot of aliases */ 
    public List<String> getAliases() throws Exception
    {
        synchronized(keyStoreMutex)
        {
           Enumeration<String> alss = _keyStore.aliases();
           StringList list=new StringList();
           
           while(alss.hasMoreElements())
           {
               String alias=alss.nextElement();
               list.add(alias);
           }
           
           return list; 
        } 
    }
    
    /** 
     * Return first key alias. Typically this is a private user key.
     * Returns NULL if no alias is configured!  
     */ 
    public String getFirstKeyAlias() throws VlException
    {
        if (this.userPrivateKeyAlias!=null)
            return userPrivateKeyAlias; 
        
        if (this._keyStore==null)
            return null; 

        try
        {
            String alias=null;
            
            for (Enumeration<String> alss = _keyStore.aliases(); alss.hasMoreElements();) 
            {
                alias = alss.nextElement();
                if (_keyStore.isKeyEntry(alias)) 
                    break;
                else 
                    alias = null;
            }
            
            return alias;
        }
        catch (Exception e)
        {
            throw convertException("Couldn't access keystore while searching for aliasses.",e); 
        }
    }
    
    /** Set key alias for the default user. */ 
    public void setUserPrivateKeyAlias(String alias)
    {
        this.userPrivateKeyAlias=alias; 
    }
    
    public Certificate[] getCertificateChain(String alias) throws VlException
    {
        synchronized(this._keyStore)
        {
            try
            {
                return this._keyStore.getCertificateChain(alias);
            }
            catch (Exception e)
            {
                throw convertException("Error accessing KeyStore while getting certificate chain for alias:"+alias,e); 
            }
        }
    }
    
    public PrivateKey getPrivateKey(String alias) throws VlException 
    {
        synchronized(this._keyStore)
        {
            try
            {
                String passwd=this.keystorePassword;
                PrivateKey key = (PrivateKey) this._keyStore.getKey(alias, passwd.toCharArray());
                return key;
            }
            catch (Exception e)
            {
                throw convertException("Error accessing KeyStore while looking for alias"+alias,e); 
            }
        }
    }
    
    private void checkKeyStore()
    {
        try
        {
            String alias=getFirstKeyAlias(); 
            if (alias==null)
            {
                logger.infoPrintf("KeyStore: No Private key alias found.\n");
                return;
            }
            logger.infoPrintf("KeyStore: Found private key alias=%s\n",alias);            
           
            if (getPrivateKey(alias)==null)
            {
                logger.infoPrintf("KeyStore: Warning: No Private key detected for alias:%s\n",alias);
                return; 
            }
            if (_keyStore.getCertificateChain(alias)==null)
            {
                logger.infoPrintf("KeyStore: Error: Private key found, but has no Certificate Chaing for alias:%s\n",alias); 
                return; 
            }
         
            logger.infoPrintf("KeyStore: Found Certificate chain for Private Key:%s\n",alias); 
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"Exception when checking keystore!\n");
        }
    }

    public void setUserPrivateKey(String alias,PrivateKey privKey, Certificate chain[]) throws VlException
    {
        this.addUserPrivateKey(alias, privKey, chain); 
    }
    
    public void addUserPrivateKey(String alias,PrivateKey privKey, Certificate[] chain) throws VlException
    {
        if (alias==null) 
            alias="mykey";

            synchronized(this._keyStore)
            {
                try
                {
                    this._keyStore.setKeyEntry(alias, privKey,this.getPassphrase().toCharArray(), chain);
                }
                catch (KeyStoreException e)
                {
                    throw convertException("Couldn't set private key entry for alias:"+alias,e); 
                }
            }
        
        this.saveKeystore();
    }
    
    public void changePassword(String oldpasswd,String newpasswd) throws VlException
    {
        this.loadKeystore(oldpasswd);
        this.saveKeystore(newpasswd);  
    }

    public KeyManager createPrivateKeyManager(String alias) throws VlException  
    {
        if (alias==null)
            alias=getFirstKeyAlias(); 
        
        Certificate[] certificateChain = this.getCertificateChain(alias); 
        PrivateKey privateKey = this.getPrivateKey(getFirstKeyAlias()); 
        
        // Multiple private keys could be added here! 
        if (privateKey==null)
        {
            logger.infoPrintf("Private key not found:%s\n",alias); 
            throw new VlInternalError("Couldn't find private key:"+alias);  
        }
        
        // Multiple private keys could be added here! 
        if (certificateChain==null)
        {
            logger.infoPrintf("Private key not found:%s\n",alias); 
            throw new VlInternalError("Couldn't find Certificate Chain for private key alias:"+alias); 
        }
        
        logger.infoPrintf("Using default user private key:%s\n",alias); 
        
        return new MyX509KeyManager(certificateChain, privateKey);
    }

    public CaCertOptions getOptions()
    {
        return this.cacertOptions; 
    }

    public SavingTrustManager getSavingTrustManager()
    {
        return this.savingTrustManager; 
    }

    
}
