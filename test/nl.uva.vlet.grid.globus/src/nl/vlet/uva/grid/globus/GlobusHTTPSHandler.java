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
 * $Id: GlobusHTTPSHandler.java,v 1.2 2011-04-18 12:08:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:08:38 $
 */ 
// source: 

package nl.vlet.uva.grid.globus;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;

import org.globus.net.GSIHttpURLConnection;

/**
 * Globus HTTPS Handler 
 */ 
public class GlobusHTTPSHandler extends URLStreamHandler
{

    public GSIHttpURLConnection openConnection(URL url, Proxy proxy) throws IOException 
    {
        return createConnection(url,proxy); 
    }
    
    @Override
    protected GSIHttpURLConnection openConnection(URL url) throws IOException
    {
        return createConnection(url,null); 
    }           
    
    public GSIHttpURLConnection createConnection(URL url,Proxy proxy) throws IOException
    {
        GSIHttpURLConnection   conn = new GSIHttpURLConnection(url); 

        // conn = new GSIHttpURLConnection(new
        // URL("https://grasveld.nikhef.nl:9000/"));
        conn.setRequestProperty("gssMode", "ssl");
        // conn.connect(); 
        
        return conn; 
    }


}
