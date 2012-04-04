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
 * $Id: SRMClientV1.java,v 1.19 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

import gov.lbl.srm.v22.stubs.TGroupPermission;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TPermissionMode;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.types.UnsignedLong;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.axis.transport.GSIHTTPSender;
import org.globus.axis.transport.HTTPSSender;
import org.globus.axis.util.Util;

import srm.dcachev1.stubs.FileMetaData;
import srm.dcachev1.stubs.ISRM;
import srm.dcachev1.stubs.RequestFileStatus;
import srm.dcachev1.stubs.RequestStatus;
import srm.dcachev1.stubs.SRMServerV1Locator;

/**
 * SRMClient for V1.1 SRM Servers.
 * 
 * Provide miminum functionality for V1.1 servers.
 * 
 * @author Spiros Koulouzis
 * 
 */
public class SRMClientV1 extends SRMClient
{

    private SRMServerV1Locator srmServiceLocator;

    private ISRM _srmService;

    private int initialWaitTime=100;
    
    private int incrementalWaitTime=200;

    /**
     * Creates an SRMClientV1.
     * 
     * @param srmUri
     *            the service URI, usually obtained by the BDII service.
     * @param connect
     *            whether to connect to the server.
     * @throws SRMException
     * @throws Exception
     */
    public SRMClientV1(URI srmUri, boolean connect) throws SRMException
    {
        super(srmUri);

        if (connect)
            connect();
    }

    @Override
    public void connect() throws SRMException
    {
        if (!isConnected())
        {
            initTransport();
        }
    }

    @Override
    public void disconnect()
    {
        this._srmService = null;
    }

    @Override
    public boolean isConnected()
    {
        if (getISRM() != null)
            return true;

        return false;
    }

    /**
     * Initialize transport for axis (add httpg)
     * 
     * @throws SRMException
     */
    private void initTransport() throws SRMException
    {
        //logger.log(Level.INFO,"--->>> Init SRM v11 Transport <<< ---");
        
        srmServiceLocator = new SRMServerV1Locator();

        // Register new protocol HTTPG
        SimpleProvider provider = new SimpleProvider();

        // Uses Globus Proxy !
        GSIHTTPSender gsisender = new GSIHTTPSender();

        SimpleTargetedChain chain = new SimpleTargetedChain(gsisender);
        provider.deployTransport("httpg", chain);

        Util.registerTransport();
        // ===========================================
        // ===========================================
        
        // connectiont time out ?
        srmServiceLocator.setEngine(new AxisClient(provider));

        try
        {
            this._srmService = srmServiceLocator.getISRM(srmUri.toURL());

            // Set Axis socket connection timeout
            ((Stub) this._srmService).setTimeout(connectionTimeout);
        }
        catch (Exception e)
        {
            throw SRMException.createConnectionException("SRM Connection Error. Connection failed to:" + srmUri, e);
        }

    }
    
    public void setConnectionTimeout(int time)
    {
        this.connectionTimeout = time;
        
        // Update Stub ! 
        if (_srmService!=null)
            ((Stub) this._srmService).setTimeout(connectionTimeout);
    }
    
    @Override
    public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs) throws SRMException
    {
        return this.srmGetTransportURIs(sourceSURLs, new String[] { SRMConstants.GSIFTP_PROTOCOL });
    }

    @Override
    public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs,
            String[] transportProtocols) throws SRMException
    {

        return this.srmGetTransportURIs(sourceSURLs, transportProtocols);
    }

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
    private org.apache.axis.types.URI[] srmGetTransportURIs(org.apache.axis.types.URI[] sourceSURLs,
            String[] transportProtocols) throws SRMException
    {
        String[] paths = new String[sourceSURLs.length];
        for (int i = 0; i < paths.length; i++)
        {
            paths[i] = sourceSURLs[i].toString();
        }
        try
        {
            RequestStatus requestStatus = this.getISRM().get(paths, transportProtocols);

            requestStatus = pollStatus(requestStatus);

            RequestFileStatus[] fileStatuse = requestStatus.getFileStatuses();

            org.apache.axis.types.URI uris[] = new org.apache.axis.types.URI[fileStatuse.length];

            for (int i = 0; i < fileStatuse.length; i++)
            {
                debug("TURI: " + fileStatuse[i].getTURL());
                uris[i] = new org.apache.axis.types.URI(fileStatuse[i].getTURL());
            }
            return uris;
        }
        catch (RemoteException e)
        {
            throw convertException("Error while getting transport URIs", e);
        }
        catch (MalformedURIException e)
        {
            throw convertException("Error while getting transport URIs", e);
        }

    }

    /**
     * Block till the status of request is not 'Pending' or the request times
     * out.
     * 
     * @param requestStatus
     *            the status of the request.
     * @return the final request status.
     * @throws SRMException
     */
    private RequestStatus pollStatus(RequestStatus requestStatus) throws SRMException
    {
        int requestID;
        int tryCount = 0;
        long totalTime=0; 
        while (requestStatus.getState().equalsIgnoreCase("Pending"))
        {
            requestID = requestStatus.getRequestId();
            try
            {
                requestStatus = getISRM().getRequestStatus(requestID);
            }
            catch (RemoteException e1)
            {
                throw convertException("Error while waiting for SRM response", e1);
            }

            debug("State: " + requestStatus.getState());
            debug("Error msg: " + requestStatus.getErrorMessage());
            debug("Time to stat: " + requestStatus.getEstTimeToStart());
            debug("Type: " + requestStatus.getType());

            int sleepTime=this.initialWaitTime+tryCount*this.incrementalWaitTime;
            totalTime+=sleepTime; 
            
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
                throw convertException("Error while waiting for SRM response", e);
            }

            tryCount++;
            
            if ( totalTime >= getSRMRequestTimeout())
            {
                throw handleStatusCode("Request Timeout Error while waiting for the status of a '" + requestStatus.getType() + "' Request",
                        requestStatus);
            }
            
            if (requestStatus.getState().equalsIgnoreCase("Failed"))
            {
                throw handleStatusCode("Error while waiting for the status of a '" + requestStatus.getType() + "' Request",
                        requestStatus);
            }
        }

        debug("State: " + requestStatus.getState());
        debug("Error msg: " + requestStatus.getErrorMessage());
        debug("Time to stat: " + requestStatus.getEstTimeToStart());
        debug("Type: " + requestStatus.getType());
        return requestStatus;
    }

    private SRMException handleStatusCode(String whatWasExecuted, RequestStatus requestStatus)
    {
        // ErrorType errorCode = SRMException.ErrorType.GENERAL_ERROR;
        // Generic Exception:
        return new SRMException(whatWasExecuted + "\n" + "SRM Status Code =" + requestStatus.getState()
                + " SRM Explanation =" + requestStatus.getErrorMessage(), null);
    }

    @Override
    public String[] getTransportProtocols() throws SRMException
    {
        if (this.transportProtocols == null)
        {
            try
            {
                transportProtocols = this.getISRM().getProtocols();
            }
            catch (RemoteException e)
            {
                throw convertException("Error while getting transport protocols", e);
            }
        }
        return this.transportProtocols;
    }

    /**
     * Pings the srm service
     * 
     * @return true if the services is there
     * @throws SRMException
     */
    public boolean srmPing() throws SRMException
    {
        try
        {
            return this.getISRM().ping();
        }
        catch (RemoteException e)
        {

            throw convertException("Error while doing a ping", e);
        }
    }

    /**
     * Gets path metadata and wraps them into <code>TMetaDataPathDetail</code>
     * 
     * @param paths
     *            the path (i.e. /pnfs/grid.sara.nl/data/pvier/file.txt)
     * @return the available metadta (i.e size, CheckSum Type,CheckSum
     *         Value,etc).
     * @throws SRMException
     */
    private ArrayList<TMetaDataPathDetail> srmQueryPaths(String[] paths) throws SRMException
    {
        FileMetaData[] metaDataPathDetails;
        try
        {
            metaDataPathDetails = getISRM().getFileMetaData(paths);
        }
        catch (RemoteException e)
        {
            throw convertException("Error while querying paths", e);
        }

        ArrayList<TMetaDataPathDetail> details = new ArrayList<TMetaDataPathDetail>();

        TMetaDataPathDetail[] element = new TMetaDataPathDetail[metaDataPathDetails.length];

        TGroupPermission groupPermission = null;
        UnsignedLong size = null;
        for (int i = 0; i < metaDataPathDetails.length; i++)
        {
            // debug("getSURL: "+metaDataPathDetails[i].getSURL());
            // debug("getOwner: "+metaDataPathDetails[i].getOwner());
            // debug("getPermMode: "+metaDataPathDetails[i].getPermMode());
            // debug("getSize: "+metaDataPathDetails[i].getSize());
            // debug("get ChecksumType: "+metaDataPathDetails[i].getChecksumType());

            element[i] = new TMetaDataPathDetail();
            element[i].setCheckSumType(metaDataPathDetails[i].getChecksumType());
            element[i].setCheckSumValue(metaDataPathDetails[i].getChecksumValue());
            groupPermission = new TGroupPermission(metaDataPathDetails[i].getGroup(), TPermissionMode.NONE);
            element[i].setGroupPermission(groupPermission);

            size = new UnsignedLong(metaDataPathDetails[i].getSize());
            element[i].setSize(size);
            try
            {
                element[i].setPath(new org.apache.axis.types.URI(metaDataPathDetails[i].getSURL()).getPath());
            }
            catch (MalformedURIException e)
            {
                throw convertException("Error while converting URI to path", e);
            }
        }

        Collection<TMetaDataPathDetail> list = Arrays.asList(element);

        details.addAll(list);

        return details;
    }

    /**
     * Gets path metadata and wraps them into <code>TMetaDataPathDetail</code>
     * 
     * @param uris
     *            the uris (i.e.
     *            srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data
     *            /pvier/file.txt)
     * @return the available metadta (i.e size, CheckSum Type,CheckSum
     *         Value,etc).
     * @throws SRMException
     */
    public ArrayList<TMetaDataPathDetail> srmQueryPaths(org.apache.axis.types.URI[] uris) throws SRMException
    {

        String[] paths = getPaths(uris);

        return this.srmQueryPaths(paths);
    }

    /**
     * Gets from uris the paths
     * 
     * @param uris
     *            the uri array
     * @return the paths
     */
    private String[] getPaths(org.apache.axis.types.URI[] uris)
    {

        String[] paths = new String[uris.length];
        for (int i = 0; i < paths.length; i++)
        {
            paths[i] = uris[i].toString();
            debug("Will query: " + paths[i]);
        }
        return paths;
    }

    /**
     * @return the _srmService
     */
    private ISRM getISRM()
    {
        return _srmService;
    }

    /**
     * Convert exceptions to SRMExceptions.
     * 
     * @param message
     *            the message.
     * @param exception
     *            the exception.
     * @return the converted SRMException.
     */
    private SRMException convertException(String message, Throwable exception)
    {
        Throwable cause = exception;

        // Resolve Cause in the case of an RemoteException !
        if (exception instanceof RemoteException)
        {
            cause = exception.getCause();
            // cause may not be null!
            if (cause == null)
                cause = exception;
        }

        // Get Cause error text:
        String causeStr = cause.getMessage();
        if (causeStr == null)
            causeStr = "";

        // Connection Error;
        if ((causeStr.contains("Connection refused")) || (causeStr.contains("No route to host")))
        {
            return SRMException.createConnectionException(message + "\nConnection Error:" + this.srmUri + "\nReason="
                    + cause.getMessage(), cause);
        }

        return new SRMException(message + "\nReason=" + cause.getMessage(), cause);
    }

    private void debug(String msg)
    {
        // todo Logging:debugger
        // System.err.println(this.getClass().getName() + ": " + msg);
    }

    @Override
    public String getVersion() throws SRMException
    {
        if (this.srmPing())
        {
            return "V1";
        }
        else
        {
            throw new SRMException("Ping failed", null);
        }
    }

    @Override
    public TMetaDataPathDetail statPath(String path, Boolean fulldetails) throws SRMException, MalformedURIException
    {
        return srmQueryPaths(new String[] { path }).get(0);
    }

    @Override
    public ArrayList<TMetaDataPathDetail> statPaths(String[] paths, Boolean fulldetails) throws SRMException,
            MalformedURIException
    {
        return srmQueryPaths(paths);
    }

}
