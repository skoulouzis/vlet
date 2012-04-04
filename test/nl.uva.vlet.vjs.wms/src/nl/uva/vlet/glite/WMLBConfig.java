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
 * $Id: WMLBConfig.java,v 1.8 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package nl.uva.vlet.glite;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vrl.VRL;

/**
 * WMS and LB Configurations.
 */
public class WMLBConfig
{
    public static final int WMS_DEFAULT_PORT = 7443;

    /**
     * Default LB Service port. Note that ports in jobs URIs use 9000 as value
     * which is the web service port number.
     */
    public static final int LB_DEFAULT_PORT = 9003;

    public static class WMSConfig extends WMLBConfig
    {
        public WMSConfig(URI uri, String proxyPath, List<String> cacertsPath)
        {
            super(uri, proxyPath, cacertsPath);
        }

        public java.net.URI getWMSUri()
        {
            return getServiceUri();
        };
    }

    public static class LBConfig extends WMLBConfig
    {
        public LBConfig(URI uri, String proxyPath, List<String> cacertsPath)
        {
            super(uri, proxyPath, cacertsPath);
        }

        public java.net.URI getLBUri()
        {
            return getServiceUri();
        }

    }

    public static java.net.URI createLBServiceUri(String host, int port) throws URISyntaxException
    {
        if (port <= 0)
            port = LB_DEFAULT_PORT;

        return new java.net.URI("https", null, host, port, "/", null, null);
    }

    public static URI createWMSUri(String hostname, int port) throws URISyntaxException
    {
        if (port <= 0)
            port = WMS_DEFAULT_PORT;

        return new URI("https", null, hostname, port, "/glite_wms_wmproxy_server", null, null);
    }

    public static LBConfig createLBConfig(String hostname, int port) throws URISyntaxException
    {
        return new LBConfig(createLBServiceUri(hostname, port), null, null);
    }

    public static WMSConfig createWMSConfig(String hostname, int port) throws URISyntaxException
    {
        return new WMSConfig(createWMSUri(hostname, port), null, null);
    }

    public static VRL createWMSVrl(URI wmsEndpointUri)
    {
        String host = wmsEndpointUri.getHost();        
        int port = wmsEndpointUri.getPort();

        if (port <= 0)
            port = WMS_DEFAULT_PORT;

        return new VRL(VJS.WMS_SCHEME, null, host, port, "/");
    }

    // ========================================================================
    // 
    // ========================================================================

    protected URI serviceUri;

    protected String proxyFileName;

    protected List<String> caCertsPath;

    protected WMLBConfig(URI uri, String proxyPath, List<String> cacertsPath)
    {
        if (uri == null)
            throw new NullPointerException("Service URI can not be null!");

        // Avoid lb:/// and wms:/// URIs from VRLs
        if (uri.getScheme().compareToIgnoreCase("https") != 0)
            throw new java.lang.AssertionError("Service URI MUST have https scheme!");

        this.serviceUri = uri;
        this.proxyFileName = proxyPath;
        this.caCertsPath = cacertsPath;
    }

    public URI getServiceUri()
    {
        return serviceUri;
    }

    public String getProxyFilename()
    {
        return proxyFileName;
    }

    public List<String> getCACertificateLocations()
    {
        return caCertsPath;
    }

    public String getHostname()
    {
        return serviceUri.getHost();
    }

    public int getPort()
    {
        return serviceUri.getPort();
    }

    public void setProxyfilename(String proxyFilename)
    {
        this.proxyFileName = proxyFilename;
    }
}
