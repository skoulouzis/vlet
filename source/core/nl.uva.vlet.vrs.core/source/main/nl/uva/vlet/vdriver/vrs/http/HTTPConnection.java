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
 * $Id: HTTPConnection.java,v 1.10 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.net.ssl.CertificateStore;

/**
 * Wrapper around URLConnection
 */
public class HTTPConnection
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(HTTPConnection.class);
        //logger.setLevelToDebug();
    }
    
    private HTTPNode httpNode;
    private URLConnection connection=null; 
    private boolean isHTTPS; 
    
    public HTTPConnection(HTTPNode httpNode, boolean isHTTPS)
    {
        this.httpNode=httpNode;
        this.isHTTPS=isHTTPS;
    }
    
    Object connectionMutex=new Object(); 
    
    public void checkConnection() throws VlException
    {
        synchronized(connectionMutex)
        {
            if (this.connection==null)
                connect();
        }
    }
    
    public void disconnect()
    {
        synchronized(connectionMutex)
        {
           if (connection instanceof HttpURLConnection)                                   
                ((HttpURLConnection)connection).disconnect(); 
    
           this.connection=null;
        }
    }
    
    public void connect() throws VlException
    {
        synchronized(connectionMutex)
        {
            doConnect(false,false);// read only
        }
    }
    
    protected void doConnect(boolean closeFirst,boolean initOutputstream) throws VlException
    {
        synchronized(connectionMutex)
        {
            logger.debugPrintf("doConnection %s,%s for: %s\n",StringUtil.boolString(closeFirst),
                    StringUtil.boolString(initOutputstream),getUrl()); 
            
            if (connection!=null) 
            {
                if (closeFirst)
                    this.disconnect(); 
                else
                    return;
            }
            // continue 
            
            
            Exception connectException=null; 
            
            try
            {
                connection= getUrl().openConnection(httpNode.getHTTPRS().getHTTPProxy(isHTTPS));
                //connection.connect();
            }
            catch (Exception e)
            {
                connectException=e; 
            }
            
           
            if (connectException instanceof javax.net.ssl.SSLException)
            {
                if (isHTTPS)
                {
                    try
                    {
                       logger.debugPrintf("doConnection: check certificates for:%s:%d\n",httpNode.getHostname(),httpNode.getPort());
                       
                       CertificateStore certStore=this.httpNode.getVRSContext().getConfigManager().getCertificateStore();
                       // check+install certificate: 
                       certStore.installCert(httpNode.getHostname(),httpNode.getPort());
                    }
                    catch (Exception e)
                    {
                        logger.logException(ClassLogger.ERROR,e,"Exception:%s\n",e);
                        throw new nl.uva.vlet.exception.VlAuthenticationException("Couldn't connect to :"+this,e); 
                    }
                }
                
                // Try II:
                
                try
                {
                    connection= getUrl().openConnection(httpNode.getHTTPRS().getHTTPProxy(isHTTPS));
                    //connection.connect();
                }
                catch (Exception e)
                {
                    throw new nl.uva.vlet.exception.VlConnectionException("Couldn't connect to :"+this,e); 
                }
                
            }
            
            // prefetch mimetype and cache: these don't change during connections. 
            // also getMimeType() reconnects 
            if (connection==null) 
                throw new VlIOException("NULL connection for:"+this);
             
            if (initOutputstream)
            {
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);        
                if (connection instanceof HttpURLConnection)
                    ((HttpURLConnection)connection).setChunkedStreamingMode(2048);
                connection.setRequestProperty("Transfer-Encoding", "chunked" );
            }
            else
            {
                connection.setDoOutput(true);
                connection.setDoInput(true);
            }
        }
    }

    private java.net.URL getUrl() throws VlException
    {   
        return this.httpNode.getURL(); 
    }

    public InputStream getInputStream() throws VlException
    {
        checkConnection();
        
        try
        {
            return this.connection.getInputStream();
        }
        catch (IOException e)
        {
            throw new VlIOException("Couldn't connect to:"+getUrl(),e);  
        }
    }

    public OutputStream getOutputStream() throws VlException
    {
        doConnect(true,true); //reconnect for outputstream 
        try
        {
            return this.connection.getOutputStream();
        }
        catch (IOException e)
        {
            throw new VlIOException("Couldn't connect to:"+getUrl(),e);  
        }
    }
    
    public String getContentType() throws VlException
    {
        checkConnection();
        
        return this.connection.getContentType(); 
    }
}
