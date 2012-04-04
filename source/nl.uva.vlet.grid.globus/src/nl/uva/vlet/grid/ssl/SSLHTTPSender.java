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
 * $Id: SSLHTTPSender.java,v 1.5 2011-05-03 15:02:47 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:47 $
 */ 
// source: 

package nl.uva.vlet.grid.ssl;


import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.net.ssl.SSLContextManager;

import org.apache.axis.MessageContext;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.SocketHolder;

/**
 * SSLHTTPSender uses configurable SSLContextManager to create custom SSLContexts.  
 * Currently needed for Glite SSL setup. 
 */ 
public class SSLHTTPSender extends HTTPSender
{
    // ======================================================================== //
    
    private static final long serialVersionUID = -5297557587240691388L;

    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(SSLHTTPSender.class);
        //logger.setLevelToDebug();
    }
    
    // ======================================================================== //
    
    // ======================================================================== //
    
    private Properties sslConfig;
    private SSLContextManager contextWrapper;
    private SSLSocketFactory socketFactory;

    public SSLHTTPSender(Properties properties) throws Exception
    {
        sslConfig = null;
        sslConfig = properties;
        contextWrapper = new SSLContextManager(sslConfig);

        // re-initialize context with new proxy credentials 
        contextWrapper.initSSLContext(); 
        socketFactory = contextWrapper.getSocketFactory();
    }

    protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, 
            BooleanHolder useFullURL)
        throws IOException, GeneralSecurityException, Exception
    {
        logger.debugPrintf("Connecting to: %s:%d\n",host,port); 
        
        if(protocol.equalsIgnoreCase("https"))
        {
            SSLSocket socket;
           
            if(timeout >= 0)
            {
                socket = (SSLSocket)socketFactory.createSocket(host,port);
                socket.setSoTimeout(timeout);
            }
            else
            {
                socket = (SSLSocket)socketFactory.createSocket(InetAddress.getByName(host), port);
            }
            
            // set protocols
            {
                socket.setEnabledProtocols(new String[]{
                    contextWrapper.getContext().getProtocol()
                    });
            }
            
            socket.setUseClientMode(true);
            sockHolder.setSocket(socket);
        } 
        else
        {
            super.getSocket(sockHolder, msgContext, protocol, host, port, timeout, otherHeaders, useFullURL);
        }
    }


}
