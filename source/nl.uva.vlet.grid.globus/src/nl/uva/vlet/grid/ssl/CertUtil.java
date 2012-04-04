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
 * $Id: CertUtil.java,v 1.4 2011-05-03 15:02:47 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:47 $
 */ 
// source: 

package nl.uva.vlet.grid.ssl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;


/**
 * Some useful refactored code from Glite.
 * Loads a proxy and decomposes the Certificate Chain as provided by the Proxy ! 
 */

public class CertUtil
{
    private static ClassLogger logger; 

    static
    {
        logger=ClassLogger.getLogger(CertUtil.class);
        //logger.setLevelToDebug(); 
    }

    // === Instance === //

    private CertificateFactory certFactory;

    public CertUtil() throws CertificateException
    {
        try
        {
            certFactory = CertificateFactory.getInstance("X.509", "BC");
        }
        catch(NoSuchProviderException e)
        {
            logger.errorPrintf("Error while creating a FileCertReader: %s \n",e.getMessage()); 
            throw new CertificateException("Error while creating a FileCertReader: "+e.getMessage()); 
        }
    }

    public KeyStore readProxy(String proxyFilename,String keyAlias, String keystorePasswd) throws IOException
    {
       BufferedInputStream bis = new BufferedInputStream(new FileInputStream(proxyFilename));
       return readProxy(bis, keyAlias,keystorePasswd);
    }
        
//  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(proxyFilename));
//  CertUtil reader = new CertUtil();
//  
//  this.privateKeystore = reader.readProxy(bis, alias,passwd);
//  
//  return createSingleKeyManager(privateKeystore,alias,passwd);
    
    /** Read proxy and put key+certificate chain into key store */ 
    public KeyStore readProxy(BufferedInputStream stream, String alias,String storePasswd) throws IOException
    {
        try
        {
            KeyStore store;
            stream.mark(10000);
            X509Certificate proxyCert = (X509Certificate)objectReader(stream, 102);
            stream.mark(10000);
            java.security.PrivateKey privateKey = PrivateKeyReader.read(stream);
            Vector<X509Certificate> beforeChain = readCertChain(stream);
            store = KeyStore.getInstance("JKS");
            beforeChain.insertElementAt(proxyCert, 0);
            X509Certificate chain[] = (X509Certificate[])(X509Certificate[])beforeChain.toArray(new X509Certificate[0]);
            store.load(null, null);
            store.setKeyEntry(alias, privateKey, storePasswd.toCharArray(), chain);
            return store;
        }
        catch (IOException e)
        {
            String msg="Proxy certificate reading failed: "+e.getMessage(); 
            logger.errorPrintf("%s\n",msg); 
            throw new IOException(msg,e);
        }
        catch (KeyStoreException e)
        {
            String msg="Keystore generation failure: "+e.getMessage();
            logger.errorPrintf("%s\n",msg); 
            throw new IOException(msg,e);
        }
        catch (NoSuchAlgorithmException e)
        {
            String msg="Unsupported algorithtm used in a proxy: "+e.getMessage(); 
            logger.errorPrintf("%s\n",msg); 
            throw new IOException(msg,e);
        }
        catch (CertificateException e)
        {
            String msg="Invalid certificate in a proxy: "+e.getMessage(); 
            logger.errorPrintf("%s\n",msg); 
            throw new IOException(msg,e);
        }

    }

    public Vector<X509Certificate> readCertChain(BufferedInputStream stream)
    throws IOException
    {
        Vector<X509Certificate> chain = new Vector<X509Certificate>();
        try
        {
            do
            {
                X509Certificate userCert = (X509Certificate)objectReader(stream, 102);
                if(userCert != null)
                    chain.add(userCert);
            } while(stream.available() > 0);
        }
        catch(Exception e)
        {
            //logger.warn((new StringBuilder()).append("Invalid certificate chain, certificate unreadable. Cause: ").append(e.getMessage()).toString());
            throw new IOException((new StringBuilder()).append("Invalid certificate chain, certificate unreadable. Cause: ").append(e.getMessage()).toString());
        }
        return chain;
    }

    public Object objectReader(BufferedInputStream binStream, int type)  throws CertificateException, IOException
    {
        Object object = null;
        int errors = 0;
        binStream.mark(10000);
        do
        {
            try
            {
                    if(errors == 1)
                    {
                        errors = 2;
                        skipToCertBeginning(binStream);
                    }
                    binStream.mark(100000);
                    binStream.reset();
                    object = readObject(binStream, type);
            }
            catch(Exception e)
            {
                if(errors != 0)
                {
                    String msg="Certificate or CRL reading failed: "+e.getMessage(); 
                    logger.errorPrintf("%s\n",msg); 
                    throw new CertificateException(msg,e);
                }
                errors = 1;
                binStream.reset();
            }
        }while(errors == 1);
        
        return object;
    }

    public static void skipToCertBeginning(BufferedInputStream stream)
    throws IOException
    {
        byte b[] = new byte[1000];
        stream.mark(1002);
        while(stream.available() > 0) 
        {
            int num = stream.read(b);
            String buffer = new String(b, 0, num);
            int index = buffer.indexOf("----BEGIN");
            if(index == -1)
            {
                stream.reset();
                stream.skip(900L);
                stream.mark(1002);
            } else
            {
                while(buffer.charAt(index - 1) == '-' && index > 0 && --index != 0) ;
                stream.reset();
                stream.skip(index);
                stream.mark(10000);
                return;
            }
        }
    }
    
    public Object readObject(BufferedInputStream binStream, int type) throws CertificateException
    {
        Object obj;
        if(type == 101)
        {
            try
            {
                obj = certFactory.generateCRL(binStream);
            }
            catch(CRLException e)
            {
                String msg="CRL loading failed: "+e.getMessage(); 
                logger.errorPrintf("%s\n",msg);
                throw new CertificateException(e.getMessage());
            }
        }
        else
        {
            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(binStream);
            if(cert == null)
                return null;
            if(type == 100)
                obj = new TrustAnchor(cert, null);
            else
            {
                if(type == 102)
                {
                    obj = cert;
                } 
                else
                {
                    logger.fatal((new StringBuilder()).append("Internal error: Invalid data type ").append(type).append(" when trying to read certificate").toString());
                    throw new CertificateParsingException((new StringBuilder()).append("Internal error: Invalid data type ").append(type).append(" when trying to read certificate").toString());
                }
            }
        }
        return obj;
    }
}
