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
 * $Id: SRMClient.java,v 1.15 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.axis.client.Stub;
import org.apache.axis.types.URI.MalformedURIException;

/**
 * 
 * Abstract SRMClient for V1 and V2. Contains Factory method to create a V1 or
 * V2 SRMClient.
 * 
 * @author S.Koulouzis, Piter T. de Boer
 * 
 */
public abstract class SRMClient
{
    // ========================================================================
    // Static interface
    // ========================================================================

    /**
     * Creates SRM Service URI. For
     * example:httpg://srm.grid.sara.nl:8443/srm/managerv2
     * 
     * @param host
     *            hostname of remote SRM service.
     * @param port
     *            port number.
     * @return Endpoint for the srm v2.2 service
     * @throws URISyntaxException
     */
    public static URI createV2ServiceURI(String host, int port) throws URISyntaxException
    {
        return new URI("httpg", null, host, port, SRMConstants.SRM_SERVICE_V2_URL_PATH, null, null);
    }

    /**
     * Creates SRM Service URI. For
     * example:httpg://srm.grid.sara.nl:8443/srm/managerv1
     * 
     * @param host
     *            hostname of remote SRM service.
     * @param port
     *            port number.
     * @return Endpoint for the srm v1.1 service
     * @throws URISyntaxException
     */
    public static URI createV1ServiceURI(String host, int port) throws URISyntaxException
    {
        return new URI("httpg", null, host, port, SRMConstants.SRM_SERVICE_V1_URL_PATH, null, null);
    }

    /**
     * Create V1 SRMClient.
     * 
     * @param host
     *            hostname of remote SRM service.
     * @param port
     *            port number.
     * @param connect
     *            connect after creating the client instance.
     * @return SRMClient v1.1
     * @throws SRMException
     * @throws URISyntaxException
     */
    public static SRMClientV1 createSRMClientV1(String host, int port, boolean connect) throws SRMException,
            URISyntaxException
    {
        if (port <= 0)
            port = SRMConstants.DEFAULT_SRM_PORT;

        return new SRMClientV1(createV1ServiceURI(host, port), connect);
    }

    /**
     * Create V2 SRMClient.
     * 
     * @param host
     *            hostname of remote SRM service.
     * @param port
     *            port number.
     * @param connect
     *            connect after creating the client instance.
     * @return SRMClient v2.2
     * @throws SRMException
     * @throws URISyntaxException
     */
    public static SRMClientV2 createSRMClientV2(String host, int port, boolean connect) throws SRMException,
            URISyntaxException
    {
        if (port <= 0)
            port = SRMConstants.DEFAULT_SRM_PORT;

        return new SRMClientV2(createV2ServiceURI(host, port), connect);
    }

    // ========================================================================
    // Instance
    // ========================================================================

    /** Service endpoint */
    protected URI srmUri = null;

    /** Axis socket connection setup timeout */
    protected int connectionTimeout = 60 * 1000;
    
    /** Time to wait for an SRM Request to reply */ 
    protected int srmRequesTimeout = 60 * 1000;

    /** Available transport protocols */
    protected String[] transportProtocols;

    /**
     * Base class constructor.
     * 
     * @param srmUri
     *            The SRM service URI, usually obtained by the BDII service.
     *            This method does not call connect().
     * 
     * @throws SRMException
     */
    protected SRMClient(URI uri) throws SRMException
    {
        setURI(uri);
    }

    /**
     * Set connection timeout int milliseconds. Use this method before calling
     * connect()!
     */
    public void setConnectionTimeout(int time)
    {
        this.connectionTimeout = time;
        //((Stub) this._srmService).setTimeout(connectionTimeout);
    }

    /**
     * Set SRM Request timeout in milliseconds. This is how long the client
     * will wait for a reply from the SRM Server. 
     */
    public void setSRMRequestTimeout(int time)
    {
        this.srmRequesTimeout = time;
    }
    
    /**
     * Get SRM Request timeout in milliseconds. This is how long the client
     * will wait for a reply from the SRM Server. 
     */
    public int getSRMRequestTimeout()
    {   
        return this.srmRequesTimeout; 
    }
    
    /**
     * Sets SRM service URI. Must be called before connect().
     * 
     * @param uri
     *            the service endpoint.
     */
    protected void setURI(URI uri)
    {
        this.srmUri = uri;
    }

    /**
     * Returns SRM service URI.
     * 
     * @return the service endpoint.
     */
    public URI getURI()
    {
        return this.srmUri;
    }

    /**
     * Gets the host name.
     * 
     * @return the host name.
     */
    public String getHostname()
    {
        return this.srmUri.getHost();
    }

    /**
     * Returns the remote port to which this client is connected.
     * 
     * @return the port number.
     */
    public int getPort()
    {
        return this.srmUri.getPort();
    }

    // ========================================================================
    // API
    // ========================================================================

    /**
     * Initializes transport and connects to the service.
     * 
     * @throws SRMException
     */
    abstract public void connect() throws SRMException;

    /**
     * Returns the connected state of the srm client.
     * 
     * @return true if the client has been connected.
     */
    abstract public boolean isConnected();

    /**
     * Disconnects the this client from the srm service.
     * 
     * @throws SRMException
     */
    abstract public void disconnect() throws SRMException;

    /**
     * Get SRM version (1.1 or 2.2). For v2.2 it's the result of the ping, for
     * v1.1 it returns 'V1' if the ping returns true.
     * 
     * @return version number.
     * @throws SRMException
     */
    abstract public String getVersion() throws SRMException;

    /**
     * Gets supported transport protocols. i.e. gsftp, rfio, etc
     * 
     * @return supported transport protocols.
     * @throws SRMException
     */
    abstract String[] getTransportProtocols() throws SRMException;

    /**
     * Resolve Storage URLs (SURLS) and return transport URLs (TURLS). Usually
     * the default protocol requested is gsiftp.
     * 
     * @param sourceSURLs
     *            SRM Storage URLs of exiting files.
     * @return array of Transport URLs
     * @throws SRMException
     */
    abstract public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs)
            throws SRMException;

    /**
     * Blocking method for getting transport URIs. Resolve Storage URLs (SURLS)
     * and return transport URLs (TURLS) according to the requested transport
     * protocol. Supported transport protocols can be obtained either by
     * <code>getTransportProtocols()</code> or from SRMConstants.
     * 
     * @param sourceSURLs
     * @param transportProtocols
     *            transport protocols reuested.
     * @return array of Transport URLs
     * @throws SRMException
     */
    abstract public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs,
            String[] transportProtocols) throws SRMException;

    /**
     * Query a single path.
     * 
     * @param path
     *            the file's/folder's path
     * @param fulldetails
     *            if true it returns all the available metadata.
     * @return the path metadata details (i.e. size, etc).
     * @throws SRMException
     * @throws MalformedURIException
     */
    abstract public TMetaDataPathDetail statPath(String path, Boolean fulldetails) throws SRMException,
            MalformedURIException;

    /**
     * Query a set of paths.
     * 
     * @param paths
     *            the file's/folder's path
     * @param fulldetails
     *            if true it returns all the available metadata.
     * @return the paths metadata details (i.e. size, etc).
     * @throws SRMException
     * @throws MalformedURIException
     */
    abstract public ArrayList<TMetaDataPathDetail> statPaths(String paths[], Boolean fulldetails) throws SRMException,
            MalformedURIException;
}
