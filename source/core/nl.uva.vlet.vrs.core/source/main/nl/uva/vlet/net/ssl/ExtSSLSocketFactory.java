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
 * $Id: ExtSocketFactory.java,v 1.7 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;

/** 
 * Extended (SSL) SocketFactory. Takes Default (Java) SSLSocketFactory, but enables it to 
 * work with custom SSLContext and settings. 
 * 
 * @author Piter T. de Boer
 */
public class ExtSSLSocketFactory extends SSLSocketFactory 
{
    static ClassLogger logger;
    
    static
    {
        logger = ClassLogger.getLogger(ExtSSLSocketFactory.class);
        //logger.setLevelToDebug(); 
    }

    /** Example static method to create a custom SSL v3 socket with "RC4" protocol. */ 
    public static Socket createSSLV3SocketNoRC4(String host, int port) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, VlException, Exception 
    {
        // Old Code: 
        String protocol="SSLv3"; 
       
        SSLContext context = CertificateStore.getDefault().createSSLContext(protocol);
        SSLSocketFactory sslFactory = context.getSocketFactory(); 
        
        int i;

        logger.debugPrintf("createSocket(): %s:%d",host,port);
            
        //create SSL socket
        SSLSocket socket = (SSLSocket) sslFactory.createSocket();
            //enable only SSLv3
        socket.setEnabledProtocols(new String[]{protocol});   //  SSLv2Hello, SSLv3,TLSv1
        //enable only ciphers without RC4 (some bug, probably in older globus)
        String[] ciphers = socket.getEnabledCipherSuites();
        ArrayList<String> cl = new ArrayList<String>(ciphers.length);
        for (i = 0; i < ciphers.length; i++) 
        {
                if (ciphers[i].indexOf("RC4") == -1) 
                    cl.add(ciphers[i]);
        }
        socket.setEnabledCipherSuites(cl.toArray(new String[cl.size()]));
        //connect as client
        socket.setUseClientMode(true);
        socket.setSoTimeout(30000); //read timeout
        socket.connect(new InetSocketAddress(host, port), 3000); //connect timeout
        
        return socket;
    }

    // === Instance === 
    
    protected SSLSocketFactory sslFactory = null;
    
    private SSLContext sslContext;
    
    private boolean enableRC4=false;

    public ExtSSLSocketFactory(SSLContext sslContext,SSLSocketFactory socketFactory)
    {
        this.sslFactory=socketFactory;
        this.sslContext=sslContext; 
    }

    public void setEnableRC4(boolean enable)
    {
        this.enableRC4=enable; 
    }
        
    @Override
    public Socket createSocket(Socket plainSocket, String host,int port, boolean flag) throws IOException
    {
        logger.debugPrintf("createSocket:%s:%s:%d (flag=%s)\n",plainSocket,host,port,flag); 
        
        SSLSocket socket=(SSLSocket) sslFactory.createSocket(plainSocket,host,port,flag); 
        
        socket=this.initSocket(socket);
        
        socket.connect(new InetSocketAddress(host, port), 3000); //connect timeout
        //create or join a SSL session
        SSLSession sess = socket.getSession();
        if (sess == null) 
        {
            logger.debugPrintf("sessions is null\n");
            return socket;
        }

        if (logger.isLoggable(ClassLogger.DEBUG)) 
        {
            //print all we know
            byte[] id = sess.getId();
            StringBuffer sb = new StringBuffer(id.length * 2);
            for (int i = 0; i < id.length; i++) 
            {
                sb.append(Integer.toHexString(id[i] < 0 ? 256 - id[i] : id[i]));
            }
                
            logger.debugPrintf("\n--- socket ---"
                        +"\nSSLSession.id = " + sb.toString()
                        +"\ncipherSuite   = " + sess.getCipherSuite()
                        +"\nprotocol      = " + sess.getProtocol()
                        +"\ncreationTime    = " + (new Date(sess.getCreationTime()))
                        +"\nlastAccessedTime= " + (new Date(sess.getLastAccessedTime())) 
                        +"\n");
                        
//                 log.debug("applicationBufferSize= " + sess.getApplicationBufferSize());
//                 log.debug("packetBufferSize= " + sess.getPacketBufferSize());
//                 log.debug("localPrincipal= " + sess.getLocalPrincipal());
//                 log.debug("peerPrincipal= " + sess.getPeerPrincipal());
            }
            return socket;
     }

    private SSLSocket initSocket(SSLSocket socket)
    {
        socket.setEnabledProtocols(new String[]{sslContext.getProtocol() });   //  SSLv2Hello, SSLv3,TLSv1
        
        // =======================================================================
        // Server Side bug! Confirmed with wms.grid.sara.nl! 
        // Original comments: 
        // Enable only ciphers without RC4: bug probably in older globus.
        // 
        String[] ciphers = socket.getEnabledCipherSuites();
        List<String> cl = new ArrayList<String>(ciphers.length);
        
        if (this.enableRC4==false)
        {
            for (int i = 0; i < ciphers.length; i++)
            {
                if (ciphers[i].indexOf("RC4") == -1)
                {
                    //logger.debugPrintf(" - adding cypher:%s\n",ciphers[i]); 
                    cl.add(ciphers[i]);
                }
            }
            
            socket.setEnabledCipherSuites(cl.toArray(new String[cl.size()]));
        }
        // ==========================================================================
        
        //connect as client
        //socket.setUseClientMode(true);
        // socket.setSoTimeout(30000); //read timeout
        //socket.connect(new InetSocketAddress(host, port), 3000); //connect timeout
        //create or join a SSL session
                    
        return socket; 
    }

    @Override
    public String[] getDefaultCipherSuites()
    {
        return sslFactory.getDefaultCipherSuites(); 
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        return sslFactory.getSupportedCipherSuites(); 
    }
    
    @Override
    public SSLSocket createSocket(String host,int port) throws IOException, UnknownHostException
    {
        logger.debugPrintf("createSocket (I): %s:%d\n",host,port);

    	SSLSocket sock = initSocket((SSLSocket)this.sslFactory.createSocket(host,port));
        
        return sock; 
    }

    @Override
    public SSLSocket createSocket(InetAddress inetaddress, int port) throws IOException
    {
        logger.debugPrintf("createSocket(II): (InterAddress)%s:%d\n",inetaddress,port);
        return initSocket((SSLSocket)this.sslFactory.createSocket(inetaddress,port)); 
    }

    @Override
    public SSLSocket createSocket(String s, int i, InetAddress inetaddress, int j) throws IOException,
            UnknownHostException
    {
        logger.debugPrintf("createSocket(III): %s:%d/%s:%d\n",s,i,inetaddress,j);
        return initSocket((SSLSocket)this.sslFactory.createSocket(s,i,inetaddress,j));  
    }

    @Override
    public SSLSocket createSocket(InetAddress inetaddr1, int i, InetAddress inetaddr2, int j) throws IOException
    {
        logger.debugPrintf("createSocket(IV): %s:%d/%s:%d\n",inetaddr1,i,inetaddr2,j);
        return initSocket((SSLSocket)this.sslFactory.createSocket(inetaddr1,i,inetaddr2,j));
    }
 
 }

