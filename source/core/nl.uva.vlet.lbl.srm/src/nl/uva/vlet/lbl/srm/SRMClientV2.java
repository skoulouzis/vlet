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
 * $Id: SRMClientV2.java,v 1.50 2011-10-03 14:40:10 ptdeboer Exp $  
 * $Date: 2011-10-03 14:40:10 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

import gov.lbl.srm.v22.stubs.ArrayOfAnyURI;
import gov.lbl.srm.v22.stubs.ArrayOfString;
import gov.lbl.srm.v22.stubs.ArrayOfTCopyFileRequest;
import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.ArrayOfTGetFileRequest;
import gov.lbl.srm.v22.stubs.ArrayOfTGroupPermission;
import gov.lbl.srm.v22.stubs.ArrayOfTMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.ArrayOfTPutFileRequest;
import gov.lbl.srm.v22.stubs.ArrayOfTSURLReturnStatus;
import gov.lbl.srm.v22.stubs.ArrayOfTUserPermission;
import gov.lbl.srm.v22.stubs.ISRM;
import gov.lbl.srm.v22.stubs.SRMServiceLocator;
import gov.lbl.srm.v22.stubs.SrmAbortRequestRequest;
import gov.lbl.srm.v22.stubs.SrmAbortRequestResponse;
import gov.lbl.srm.v22.stubs.SrmCopyRequest;
import gov.lbl.srm.v22.stubs.SrmCopyResponse;
import gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest;
import gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse;
import gov.lbl.srm.v22.stubs.SrmLsRequest;
import gov.lbl.srm.v22.stubs.SrmLsResponse;
import gov.lbl.srm.v22.stubs.SrmMkdirRequest;
import gov.lbl.srm.v22.stubs.SrmMkdirResponse;
import gov.lbl.srm.v22.stubs.SrmMvRequest;
import gov.lbl.srm.v22.stubs.SrmMvResponse;
import gov.lbl.srm.v22.stubs.SrmPingRequest;
import gov.lbl.srm.v22.stubs.SrmPingResponse;
import gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest;
import gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse;
import gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest;
import gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse;
import gov.lbl.srm.v22.stubs.SrmPutDoneRequest;
import gov.lbl.srm.v22.stubs.SrmPutDoneResponse;
import gov.lbl.srm.v22.stubs.SrmRmRequest;
import gov.lbl.srm.v22.stubs.SrmRmResponse;
import gov.lbl.srm.v22.stubs.SrmRmdirRequest;
import gov.lbl.srm.v22.stubs.SrmRmdirResponse;
import gov.lbl.srm.v22.stubs.SrmSetPermissionRequest;
import gov.lbl.srm.v22.stubs.SrmSetPermissionResponse;
import gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest;
import gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse;
import gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest;
import gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse;
import gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest;
import gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse;
import gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest;
import gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse;
import gov.lbl.srm.v22.stubs.TAccessPattern;
import gov.lbl.srm.v22.stubs.TCopyFileRequest;
import gov.lbl.srm.v22.stubs.TDirOption;
import gov.lbl.srm.v22.stubs.TExtraInfo;
import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TFileType;
import gov.lbl.srm.v22.stubs.TGetFileRequest;
import gov.lbl.srm.v22.stubs.TGroupPermission;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TOverwriteMode;
import gov.lbl.srm.v22.stubs.TPermissionMode;
import gov.lbl.srm.v22.stubs.TPermissionType;
import gov.lbl.srm.v22.stubs.TPutFileRequest;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;
import gov.lbl.srm.v22.stubs.TReturnStatus;
import gov.lbl.srm.v22.stubs.TStatusCode;
import gov.lbl.srm.v22.stubs.TSupportedTransferProtocol;
import gov.lbl.srm.v22.stubs.TTransferParameters;
import gov.lbl.srm.v22.stubs.TUserPermission;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

import nl.uva.vlet.lbl.srm.status.IFileStatus;
import nl.uva.vlet.lbl.srm.status.ISRMRequestStatusOfRequest;
import nl.uva.vlet.lbl.srm.status.ISRMResponse;
import nl.uva.vlet.lbl.srm.status.ISRMStatusOfRequestResponse;
import nl.uva.vlet.lbl.srm.status.SRMCpResponse;
import nl.uva.vlet.lbl.srm.status.SRMGetResponse;
import nl.uva.vlet.lbl.srm.status.SRMPutRequest;
import nl.uva.vlet.lbl.srm.status.SRMPutResponse;
import nl.uva.vlet.lbl.srm.status.SRMRequestStatusOfCpRequest;
import nl.uva.vlet.lbl.srm.status.SRMRequestStatusOfGetRequest;
import nl.uva.vlet.lbl.srm.status.SRMRequestStatusOfPutRequest;
import nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestCpResponse;
import nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestGetResponse;
import nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestPutResponse;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.axis.types.UnsignedLong;
import org.globus.axis.transport.GSIHTTPSender;
import org.globus.axis.transport.GSIHTTPTransport;
import org.globus.axis.util.Util;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;


/**
 * 
 * @author S.Koulouzis, Piter T. de Boer
 */
public class SRMClientV2 extends SRMClient
{
    // ========================================================================
    // Class 
    // ========================================================================

    private static java.util.logging.Logger logger;

    private static SRMServiceLocator srmServiceLocator;

    /**
     * Creates a path array from a given uri array.
     * 
     * @param suri
     *            the srm uri (i.e.
     *            srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/file.txt)
     * @return the paths
     */
    public static String[] createPathsArray(org.apache.axis.types.URI[] suri)
    {

        String[] paths = new String[suri.length];
        for (int i = 0; i < paths.length; i++)
        {
            paths[i] = suri[i].getPath();
        }
        return paths;
    }
    
    /** set Class Logger */ 
    public static void setLogger(java.util.logging.Logger srmLogger)
    {
        logger=srmLogger;
    }
    
    /** Get Class Logger */ 
    public static java.util.logging.Logger getLogger()
    {
        return logger; 
    }

    
    static
    {
    	logger=java.util.logging.Logger.getLogger("nl.uva.vlet.lbl.srm.SRMClientV2"); 
    	logger.log(Level.CONFIG,"--->>> Init SRM v22 Transport (12) <<< ---"); 
        
        // Register new protocol HTTPG
        SimpleProvider lprovider = new SimpleProvider();
        lprovider.deployTransport("httpg", new SimpleTargetedChain(new GSIHTTPSender()));

        boolean useGlobusUtil=true;
        
        if (useGlobusUtil)
        {
        	Util.registerTransport();
        }
        else
        {
            // Explicit (minimal) initialization. Does the same as Util.registerTransport(); 
	        Call.initialize();
	        Call.addTransportPackage("org.globus.net.protocol");
	        Call.setTransportForProtocol("httpg", org.globus.axis.transport.GSIHTTPTransport.class);
        }
        
        //SRMServiceLocator srmServiceLocator = new SRMServiceLocator(provider); 

        AxisClient axis=new AxisClient(lprovider); 
        srmServiceLocator = new SRMServiceLocator();
        srmServiceLocator.setEngine(axis); 
    }
    
    // ========================================================================
    // Instance
    // ========================================================================

    private ISRM _srmService = null;

    private int initialWaitTime = 100;
    
    private int incrementalWaitTime = 100;

    private SRMFileOptions srmFileOptions = new SRMFileOptions();

    private GlobusCredential globusCredential;

    private String versionInfo=null; 

    /**
     * Initialize stubs and transport.  
     * 
     * @throws SRMException
     */
    private void initTransport() throws SRMException
    {   
        // static initialization moved to static{...} ! 
    	
        try
        {
            // 
            java.net.URL url;
            
            // following code doesn't matter: if this works, the URL will fail later anyway. 
//            url=new java.net.URL("httpg",
//                        srmUri.getHost(),
//                        srmUri.getPort(),
//                        srmUri.getPath(),
//                        new org.globus.net.protocol.httpg.Handler());
//            

            // must work or else httpg protocol will fail later anyway!  
            url=srmUri.toURL(); 
            
            this._srmService = srmServiceLocator.getsrm(url);

            // Set Axis socket connection timeout
            ((Stub) this._srmService).setTimeout(connectionTimeout);

            if ( (this._srmService instanceof Stub)==false)
                throw new SRMException("Internal error: SRMService Interface is not an (Axis)Stub!:"+_srmService); 
            

            Stub axis_isrm_as_stub = (Stub) this._srmService;

            // =========================================================
            // Make sure the custom Globus Credential is used to allow
            // for multi credential (=multi user) authentication!
            // =========================================================
            
            if (this.globusCredential!=null)
            {
                GlobusGSSCredentialImpl gssCredential;
                // convert globus credential to gss credential:
                gssCredential=new GlobusGSSCredentialImpl(globusCredential, GSSCredential.DEFAULT_LIFETIME); 
                axis_isrm_as_stub._setProperty(GSIHTTPTransport.GSI_CREDENTIALS, gssCredential);
                
                // Not needed:  
                //GSSName name=null; // tbi
                // set custom context on stub ! 
                //GlobusGSSContextImpl context=new GlobusGSSContextImpl(name,gssCredential); 
                //axis_isrm_as_stub._setProperty(GSIHTTPTransport.GSI_CONTEXT, context);
            }

            // axis_isrm_as_stub._setProperty("org.globus.gsi.credentials", user_cred);
            // axis_isrm_as_stub._setProperty("org.globus.gsi.authorization",
            // new HostAuthorization(gss_expected_name));
            
            boolean gsifull=true;  
            boolean gsilimited=false; 
            
            if (gsifull)
                axis_isrm_as_stub._setProperty("org.globus.gsi.mode", "gsifull");
            else if (gsilimited) 
                axis_isrm_as_stub._setProperty("org.globus.gsi.mode", "gsilimited");
            else 
                axis_isrm_as_stub._setProperty("org.globus.gsi.mode", "gsi");
        }
        catch (java.net.MalformedURLException e)
        {
        	String errorTxt=e.getMessage();
        	if (errorTxt==null)
        		errorTxt=""; 
        		
        	if (errorTxt.toLowerCase().contains("unknown protocol"))  
        	{
        		throw SRMException.createConnectionException("Unknown protocol exception!\n"
        				+"Tomcat hint:\n"
        				+"When deployed inside tomcat, make sure 'cog-url.jar' is in TOMCAT_INSTALL/endorsed/ (preferred) "
        				+"or TOMCAT_INSTALL/lib/.\n---\n" 
        				+"Reason="+errorTxt,e);
        	}
        	else
        	{
        		throw SRMException.createConnectionException("MalformedURLException. Connection failed to:" + srmUri
        		        +"\nReason="+e.getMessage(), e);
        	}
        }
        catch (Exception e)
        {
            throw SRMException.createConnectionException("SRM Connection Error. Connection failed to:" + srmUri
                    +"\nReason="+e.getMessage(), e);
        }
    }

    /**
     * Creates an SRMClientV2.
     * 
     * @param srmUri the service URI, usually obtained by the BDII service.
     * @param connect whether to connect to the server.
     * @throws SRMException
     * @throws Exception
     */
    public SRMClientV2(java.net.URI srmUri, boolean connect) throws SRMException
    {
        super(srmUri);

        if (connect)
            connect();
    }

    /**
     * Creates an SRMClientV2.
     * 
     * @param host the host name
     * @param port the port number
     * @param connect if true connects after instantiating the client.
     * @throws SRMException
     * @throws URISyntaxException
     */
    public SRMClientV2(String host, int port, boolean connect) throws SRMException, URISyntaxException
    {
        super(createV2ServiceURI(host, port));

        if (connect)
            connect();
    }
    
    /**
     * Explicit set Credential Object to use. 
     * Needs to be checked whether GSS or GLOBUS is needed here. 
     */ 
    public void setGlobusCredential(GlobusCredential globusCredential)
    {
        this.globusCredential=globusCredential; 
    }
    
    @Override
    public void connect() throws SRMException
    {
        if (!isConnected())
        {
            initTransport();
            String version=getVersion();
            debugPrintf("Connected to SRM Version:%s\n",version); 
        }
    }

    /**
     * Disconnects from service and cleans up resources. Use connect() first
     * after disconnect() to continue communications.
     * 
     * @throws SRMException
     */
    public void disconnect() throws SRMException
    {
        this._srmService = null;
    }

    @Override
    public boolean isConnected()
    {
        if (_srmService != null)
            return true;

        return false;
    }
    
    public void setConnectionTimeout(int time)
    {
        this.connectionTimeout = time;
        // Update Stub ! 
        if (_srmService!=null)
            ((Stub) this._srmService).setTimeout(connectionTimeout);
    }
    

    /**
     * Create SRM compatible (Axis) URI.
     * 
     * @throws MalformedURIException
     */
    public org.apache.axis.types.URI createPathSurlURI(String path) throws SRMException
    {
        if (path.startsWith("/") == false)
            path = "/" + path;

        String uristr = "srm://" + getHostname() + ":" + getPort() + path;
        try
        {
            return new org.apache.axis.types.URI(uristr);
        }
        catch (MalformedURIException e) 
        {
            throw new SRMException("Invalid SRM URI. Path contains unsupported characters:'"+path+"'",e);
        }
    }

    // ========================================================================
    // ========================================================================
    //
    // *** SRM Service Methods ***
    // Actual Calls to the Service Stubs!
    // 
    // ========================================================================
    // ========================================================================

    /**
     * Wrapper method to get service stub.
     */
    private ISRM getISRM()
    {
        if (_srmService == null)
            throw new NullPointerException("SRM Service not initialized. Please use connect() first for:" + srmUri);

        return _srmService;
    }

    /**
     * Master method for interacting with SRM service. Returns metadata,
     * sub-directories for files and/or directories
     * 
     * @param numOfLevels
     *            1 for a single file, 2 for directory contents.
     * @param uris
     *            Array of SRM Compatible URIs.
     * @param fullDetails
     *            whether to include file meta data.
     * @param allLevelRecursive
     * 
     * @return flattened array of all path details
     * 
     * @throws SRMException
     */
    public ArrayOfTMetaDataPathDetail srmGetMetaDataPathDetails(int numOfLevels, 
            org.apache.axis.types.URI[] uris,
            boolean fullDetails, 
            boolean allLevelRecursive,
            int offset,
            int count) throws SRMException
    {
        long startTime = System.currentTimeMillis();

        SrmLsResponse lsResponse = null;
        ArrayOfTMetaDataPathDetail details = null;
        try
        {
            // Connect if not connected
            connect();
            ArrayOfAnyURI arrayOfURI = new ArrayOfAnyURI(uris);

            debugPrintf(">>> Querying path(s):%s\n",concatinateURI(arrayOfURI));

            SrmLsRequest lsRequest = new SrmLsRequest();
            lsRequest.setFullDetailedList(fullDetails);
            lsRequest.setArrayOfSURLs(arrayOfURI);
            
            // specify count+offset; 
            if (count>0)
                lsRequest.setCount(count);
            
            // offset can be 0! 
            if (offset>=0)
                lsRequest.setOffset(offset);
            
            // =========================================
            // Either full recursion or number of levels
            // =========================================

            if (allLevelRecursive)
            {
                // as reported by an Castor Exception!
                if (numOfLevels > 0)
                    throw new SRMException(
                            "Full recursion and specifying numOfLevels (>0) are mutual exlusive. Use one or the other.",
                            null);

                lsRequest.setAllLevelRecursive(Boolean.valueOf(allLevelRecursive));
                lsRequest.setNumOfLevels(0);// default ?
            }
            else
            {
                lsRequest.setAllLevelRecursive(false);
                lsRequest.setNumOfLevels(Integer.valueOf(numOfLevels));
            }

            lsResponse = getISRM().srmLs(lsRequest);

            String token = lsResponse.getRequestToken();
            debug("Request Token: " + token);
            details = lsResponse.getDetails();

            
            if (lsResponse.getReturnStatus().getStatusCode() == TStatusCode.SRM_SUCCESS)
            {
                // Log with INFO level current debugging purposes.  
                debugPrintf("srmLs: synchronous mode returned SRM_SUCCESS.\n"); 
                // >>> synchronous mode ok.
            }
            else if (lsResponse.getReturnStatus().getStatusCode() == TStatusCode.SRM_FAILURE)
            {
                debugPrintf("srmLs: SRM_FAILURE! (path not found?)\n"); 
                throw createSRMExceptionFromStatusCode("Error while getting " + lsResponse.getClass().getName(), lsResponse
                        .getReturnStatus());
            }
            else if (lsResponse.getReturnStatus().getStatusCode() != TStatusCode.SRM_REQUEST_QUEUED)
            {
                infoPrintf("srmLs: UNKNOWN STATUS !\n"); 
                throw createSRMExceptionFromStatusCode("UNKNOWN SRM STATUS: while getting " + lsResponse.getClass().getName(), 
                        lsResponse.getReturnStatus());
            }
            else // if (lsResponse.getReturnStatus().getStatusCode() != TStatusCode.SRM_REQUEST_QUEUED)
            {
                // boolean enableAsync=false; 
                // Log with INFO level current debugging purposes.  
                debugPrintf("srmLs: synchronous call returned: SRM_REQUEST_QUEUED -> Polling!\n");
                
                //if (enableAsync==false)
                //    throw createSRMExceptionFromStatusCode("Error while getting " + lsResponse.getClass().getName(), lsResponse
                //             .getReturnStatus());
              
                // ---------------------- 
                // Asynchronous mode !!! 
                // ----------------------
                
                // could be integrated with "pollStatus()" method. 
                
                TReturnStatus stat; 
                SrmStatusOfLsRequestResponse response; 
                
                long waitTimeIncrement=100; 
                long waitTime=100;
                long totalWaitTime=0; 
                
                
                do
                {   
                    debugPrintf("srmLs: polling!:%d\n",waitTime);  
                    
                    try
                    {
                        Thread.sleep(waitTime);
                        totalWaitTime+=waitTime;
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } 
 
                    
                    SrmStatusOfLsRequestRequest srmStatusOfLsRequestRequest = new SrmStatusOfLsRequestRequest();
                    srmStatusOfLsRequestRequest.setRequestToken(token);
                    
                    response = getISRM().srmStatusOfLsRequest(srmStatusOfLsRequestRequest);
                    stat = response.getReturnStatus();
                    
                    debugPrintf("srmLs: status=%s (waitTime=%d)\n",stat.getStatusCode().getValue(),waitTime);   
                    waitTime+=waitTimeIncrement; 
                    int maxTime=getSRMRequestTimeout();
                    if (totalWaitTime > maxTime)
                    {
                        debug("TIMEOUT: SleepTime=" + totalWaitTime + " getSRMRequestTimeout: " + maxTime
                                + "\n Must abort files. Status: " + stat.getStatusCode().getValue() + " " + stat.getExplanation());
                        
                        errorPrintf("Timeout: totalWaitTime > srmRequestTimeOut: %d>%d\n",totalWaitTime,maxTime);
                        
                        throw createSRMExceptionFromStatusCode("Timeout Error ("+totalWaitTime+"ms) while getting " + response.getClass().getName() + ". Request aborted",
                                response.getReturnStatus());
                    }
                    
                    if (stat.getStatusCode() == TStatusCode.SRM_FAILURE)
                    {
                        throw createSRMExceptionFromStatusCode("Error while getting " + lsResponse.getClass().getName(), 
                                stat);
                    }
                        
                    
                } while (stat.getStatusCode()==TStatusCode.SRM_REQUEST_QUEUED); // (stat.getStatusCode()!=TStatusCode.SRM_SUCCESS);  // or aborted! 
                    
                details=response.getDetails();

                if (stat.getStatusCode()!=TStatusCode.SRM_SUCCESS)
                {
                    infoPrintf(">>> *** srmLs: Asynchronous mode FAILED: status=%s ***\n",stat.getStatusCode().getValue()); 
                    throw createSRMExceptionFromStatusCode("Error while getting " + lsResponse.getClass().getName(), lsResponse
                            .getReturnStatus());
                }
                
                infoPrintf(">>> srmLs: Asynchronous mode returned: SRM_SUCCESS (waitTime=%d)\n",waitTime);
            }
           
            
        }
        catch (RemoteException e)
        {
            throw convertException("Failed to query location(s):\n" + concatinateURI("-",uris), e);
        }
        finally
        {
            debug("Query time: " + (System.currentTimeMillis() - startTime));
        }
        return details;
    }

    
   
    
    /**
     * Creates directory. Parent directory must exists.
     * 
     * @param surl - the uri to create.
     * @return true if directory is created.
     * @throws SRMException
     */
    public boolean srmMkdir(org.apache.axis.types.URI surl) throws SRMException
    {
        SrmMkdirRequest srmMkdirReq = new SrmMkdirRequest();
        srmMkdirReq.setSURL(surl);

        SrmMkdirResponse response;
        try
        {
            response = getISRM().srmMkdir(srmMkdirReq);
            TReturnStatus returnStatus = response.getReturnStatus();

            if (returnStatus.getStatusCode() != TStatusCode.SRM_SUCCESS)
            {
                throw createSRMExceptionFromStatusCode("Error while getting " + srmMkdirReq.getClass().getName(), returnStatus);
            }
        }
        catch (RemoteException e)
        {
            throw convertException("Could not create directory.", e);
        }
        return true;
    }

    private SrmRmdirResponse _srmRmDir(URI suri, boolean recursive, ArrayOfTExtraInfo storageSystemInfo)
            throws RemoteException, SRMException
    {
        SrmRmdirRequest request = new SrmRmdirRequest();
        request.setRecursive(Boolean.valueOf(recursive));
        request.setSURL(suri);
        if (storageSystemInfo != null)
            request.setStorageSystemInfo(storageSystemInfo);
        return getISRM().srmRmdir(request);
    }

    /**
     * Removes directory from srm service.
     * 
     * @param suri
     *            the directory uri
     * @param recursive
     *            if true deletes all sub folders and files.
     * @return true if directory is successfully removed.
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean srmRmdir(org.apache.axis.types.URI suri, boolean recursive) throws SRMException
    {
        try
        {

            SrmRmdirResponse response = _srmRmDir(suri, recursive, null);
            TReturnStatus returnStatus = response.getReturnStatus();

            if (!didRequestSucceed(returnStatus.getStatusCode()))
            {
                if (recursive)
                {
                    // If folder has files get their paths
                    ArrayOfTMetaDataPathDetail dirMetaDataDetail = srmGetMetaDataPathDetails(0,
                            new org.apache.axis.types.URI[] { suri }, false, true,-1,-1);
                    // make a flat array to first get rid of the files.....
                    ArrayList<TMetaDataPathDetail> flatDetails = unwrapDetails(dirMetaDataDetail.getPathDetailArray());
                    ArrayList<String> filePaths = new ArrayList<String>();
                    for (int i = 0; i < flatDetails.size(); i++)
                    {
                        if (flatDetails.get(i).getType() != TFileType.DIRECTORY)
                        {
                            if (!filePaths.contains(flatDetails.get(i).getPath()))
                                filePaths.add(flatDetails.get(i).getPath());
                        }
                    }

                    String[] filePathsArray = new String[filePaths.size()];
                    filePathsArray = filePaths.toArray(filePathsArray);

                    // rm files and links paths
                    if (filePathsArray.length > 0)
                        srmRm(this.createURIArray(filePathsArray));

                    // now the directory should be empty
                    response = _srmRmDir(suri, recursive, null);
                    returnStatus = response.getReturnStatus();

                    // we had a problem. Probably we are talking with a castor
                    // backend.
                    if (!didRequestSucceed(returnStatus.getStatusCode()) && recursive)
                    {
                        dirMetaDataDetail = srmGetMetaDataPathDetails(0, new org.apache.axis.types.URI[] { suri },
                                false, true,-1,-1);

                        // make a flat array with what's left, and delete them.
                        flatDetails = unwrapDetails(dirMetaDataDetail.getPathDetailArray());
                        StringBuffer rmDirError = new StringBuffer();

                        for (int i = (flatDetails.size() - 1); i >= 0; i--)
                        {
                            suri = createPathSurlURI(flatDetails.get(i).getPath());

                            debugPrintf("Will delete : %s\n", suri);

                            response = _srmRmDir(suri, false, null);
                            returnStatus = response.getReturnStatus();

                            // keep them for later report if errors show up
                            if (!didRequestSucceed(returnStatus.getStatusCode()))
                            {
                                rmDirError.append(returnStatus.getStatusCode() + ". while deleteing " + suri
                                        + ". Reason" + returnStatus.getExplanation() + "\n");

                            }
                        }

                        // If the buffer has more than the
                        if (rmDirError.length() > 0)
                        {
                            throw new SRMException(rmDirError.toString(), null);
                        }

                    }

                }
                else
                {
                    throw createSRMExceptionFromStatusCode("Error while getting " + response.getClass().getName(), returnStatus);
                }

            }
        }
        catch (RemoteException e)
        {
            throw convertException("Could not remove directory:" + suri, e);
        }

        return true;
    }

    private void debugPrintf(String format, Object... args)
    {
        //System.out.printf(">>>OUT:"+format,args); 
        //System.err.printf(format,args); 
        logger.log(Level.FINE,format,args);      
    }
     
    private void infoPrintf(String format, Object... args)
    {
        //System.out.printf(">>>ERR:"+format,args); 
        //System.err.printf(format,args); 
        logger.log(Level.INFO,format,args);   
    }
    
    private void errorPrintf(String format, Object... args)
    {
        //System.out.printf(">>>ERR:"+format,args); 
        //System.err.printf(format,args); 
        logger.log(Level.SEVERE,format,args);   
    }
    /**
     * Removes a set of files or links.
     * 
     * @param arrayOfSURLs
     *            array of uris.
     * @return true if delete is successful.
     * @throws SRMException
     */
    public boolean srmRm(org.apache.axis.types.URI[] suris) throws SRMException
    {

        ArrayOfAnyURI arrayOfURI = new ArrayOfAnyURI(suris);

        SrmRmRequest request = new SrmRmRequest();
        request.setArrayOfSURLs(arrayOfURI);

        try
        {
            SrmRmResponse response = getISRM().srmRm(request);
            TReturnStatus returnStatus = response.getReturnStatus();
            if (!didRequestSucceed(returnStatus.getStatusCode()))
            {
                throw createSRMExceptionFromStatusCode("Error while getting " + response.getClass().getName(), returnStatus);
            }

        }
        catch (RemoteException e)
        {
            throw convertException("Failed to perform remove.", e);
        }

        return true;
    }

    /**
     * Copies a set of uris from suris to arrayOfTargetSURLs. Note! This method
     * is not working with srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home.
     * 
     * @param suris
     *            Array of source uris
     * @param arrayOfTargetSURLs
     *            Array of target uris.
     * @param dirOptions
     *            .Dir options i.e. all levels recursive, number of levels
     * @param overwriteOption
     *            . if path will be overwritten (i.e. TOverwriteMode.ALWAYS)
     * @param targetFileRetentionPolicyInfo
     *            see https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html#_Toc241633046
     * @param targetFileStorageType
     *            the storage type of path (i.e. TFileStorageType.PERMANENT)
     * @return true if copy is successful
     * @throws SRMException
     */
    public boolean srmCp(org.apache.axis.types.URI[] suris, org.apache.axis.types.URI[] arrayOfTargetSURLs,
            TDirOption[] dirOptions, TOverwriteMode overwriteOption,
            TRetentionPolicyInfo targetFileRetentionPolicyInfo, TFileStorageType targetFileStorageType)
            throws SRMException
    {
        // ArrayOfAnyURI arrayOfURI = new ArrayOfAnyURI(suris);

        if (suris.length != arrayOfTargetSURLs.length)
        {
            throw new SRMException("Source and target ArrayOfAnyURI must be the same size", null);
        }

        if (dirOptions.length != suris.length)
        {
            throw new SRMException("TDirOptions and ArrayOfAnyURI must be the same size", null);
        }

        TCopyFileRequest[] requestArray = new TCopyFileRequest[suris.length];

        for (int i = 0; i < requestArray.length; i++)
        {
            requestArray[i] = new TCopyFileRequest(suris[i], arrayOfTargetSURLs[i], dirOptions[i]);
        }

        ArrayOfTCopyFileRequest arrayOfFileRequests = new ArrayOfTCopyFileRequest(requestArray);

        // build request
        SrmCopyRequest request = new SrmCopyRequest();

        request.setArrayOfFileRequests(arrayOfFileRequests);

        // Integer desiredTargetSURLLifeTime = null;
        // request.setDesiredTargetSURLLifeTime(desiredTargetSURLLifeTime);

        // Integer desiredTotalRequestTime = null;
        // request.setDesiredTotalRequestTime(desiredTotalRequestTime);

        request.setOverwriteOption(overwriteOption);

        // The retination policy decribes the quality of the storage sevice.
        // TRetentionPolicy.CUSTODIAL. High e.g. tape storge
        // TRetentionPolicy.OUTPUT. Output retention policy will possibly be
        // used for files managed by Resilient Manager, which will make several
        // internal copies of each file, distributed on distinct instances of
        // hardware
        request.setTargetFileRetentionPolicyInfo(targetFileRetentionPolicyInfo);

        request.setTargetFileStorageType(targetFileStorageType);

        try
        {
            SrmCopyResponse cpResponse = getISRM().srmCopy(request);

            SRMCpResponse response = new SRMCpResponse(cpResponse, null);
            String reuestToken = response.getRequestToken();

            debug("Request Token: " + reuestToken);

            TReturnStatus returnStatus = response.getReturnStatus();

            debug("Status: " + returnStatus.getStatusCode().getValue() + " " + returnStatus.getExplanation());

            // ArrayOfTCopyRequestFileStatus fileStatuses = response
            // .getArrayOfFileStatuses();

            // debug("Copy Request File Status: ");
            // for (int i = 0; i < fileStatuses.getStatusArray().length; i++)
            // {
            // debug("Estimated Wait Time: "
            // + fileStatuses.getStatusArray(i).getEstimatedWaitTime());
            // debug("Remaining File Lifetime: "
            // + fileStatuses.getStatusArray(i)
            // .getRemainingFileLifetime());
            // debug("File Size: "
            // + fileStatuses.getStatusArray(i).getFileSize());
            // debug("Source SURL: "
            // + fileStatuses.getStatusArray(i).getSourceSURL());
            // debug("Status: "
            // + fileStatuses.getStatusArray(i).getStatus()
            // .getStatusCode()
            // + " "
            // + fileStatuses.getStatusArray(i).getStatus()
            // .getExplanation());
            // debug("Target SURL: "
            // + fileStatuses.getStatusArray(i).getTargetSURL());
            // }

            if (returnStatus.getStatusCode() != TStatusCode.SRM_SUCCESS)
            {
                if (returnStatus.getStatusCode() != TStatusCode.SRM_REQUEST_QUEUED)
                {
                    throw createSRMExceptionFromStatusCode("Error while getting " + cpResponse.getClass().getName(), returnStatus);
                }
            }

            pollStatus(response);
        }
        catch (RemoteException e)
        {
            throw convertException("Couldn't copy some or all of:\n" + concatinateURI("-",suris), e);
        }

        return true;
    }

    /**
     * Pings the srm service.
     * 
     * @return the ping responce. Includes srm version and backend type (i.e.
     *         dCache, storm, etc).
     * @throws SRMException
     */
    public SrmPingResponse srmPing() throws SRMException
    {
        SrmPingRequest srmPingRequest = new SrmPingRequest();
        try
        {
            return getISRM().srmPing(srmPingRequest);
        }
        catch (RemoteException e)
        {
            throw convertException("Failed to ping:" + srmUri, e);
        }
    }

    /**
     * Gets transport uris for a set uris located in the srm service
     * 
     * @param sourceSURLs
     *            the uris located on the srm service.
     * @param accessPattern
     *            .The access pattern (i.e. TAccessPattern.TRANSFER_MODE)
     * @param transportProtocol
     *            . Requested transport protocol. Avelable protocols can be
     *            obtained from <code>getTransportProtocols()</code>
     * @return the transport uris
     * @throws SRMException
     */
    public org.apache.axis.types.URI[] srmGetTransportURIs(org.apache.axis.types.URI[] sourceSURLs,
            TAccessPattern accessPattern, String[] transportProtocol) throws SRMException
    {
        long startTime = System.currentTimeMillis();

        // send prepere to get request
        SrmPrepareToGetResponse response = sendSRMPrepareToGetRequest(sourceSURLs, accessPattern, transportProtocol);
        // Can happend when communicating with a V1 client! => throw error
        if (response == null)
            throw new SRMException("SRMPrepareToGetRequest: Got NULL response from SRM Service (possible V11 service):"
                    + this, new NullPointerException("SrmPrepareToGetResponse==NULL"));

        TReturnStatus returnStatus = response.getReturnStatus();

        debug("SrmPrepareToGetResponse status: " + returnStatus.getStatusCode().getValue());
        // if the get request is not queued something whent wrong
        if (didRequestFail(returnStatus.getStatusCode()))
        {
            throw createSRMExceptionFromStatusCode("Error while getting " + response.getClass().getName(), returnStatus);
        }

        // SrmStatusOfGetRequestResponse statusResponse =
        // sendSRMStatusOfGetRequest(
        // response, sourceSURLs);

        // start pooling for the status of the request
        ISRMResponse getResponse = new SRMGetResponse(response, sourceSURLs);
        ISRMStatusOfRequestResponse statusResponse = pollStatus(getResponse);

        IFileStatus[] statusArray = statusResponse.getStatusArray();
        org.apache.axis.types.URI[] tUris = new org.apache.axis.types.URI[statusArray.length];

        for (int i = 0; i < statusArray.length; i++)
        {

            debug("Estimated WaitTimel: " + statusArray[i].getEstimatedWaitTime());
            debug("RemainingPinTime: " + statusArray[i].getRemainingPinTime());
            debug("FileSize: " + statusArray[i].getFileSize());
            debug("getStatusCode: " + statusArray[i].getStatus().getStatusCode());

            debug("TURI for: " + statusArray[i].getSURL() + " is: " + statusArray[i].getTransferURL());
            tUris[i] = statusArray[i].getTransferURL();

            if (statusArray[i].getTransferProtocolInfo() != null)
            {
                TExtraInfo[] transferProtocolInfo = ((ArrayOfTExtraInfo) statusArray[i].getTransferProtocolInfo())
                        .getExtraInfoArray();
                for (int j = 0; j < transferProtocolInfo.length; j++)
                {
                    debug("transfer Protocol Info: " + transferProtocolInfo[i].getKey() + " : "
                            + transferProtocolInfo[i].getValue());
                }
            }
        }

        debug("GetTransportURIs request time " + (System.currentTimeMillis() - startTime));

        return tUris;
    }

    /**
     * Returns true if the request faild in any way
     * 
     * @param statusCode
     *            the returnd status code.
     * @return true only if request faild
     */
    private boolean didRequestFail(TStatusCode statusCode)
    {
        if (statusCode == TStatusCode.SRM_REQUEST_QUEUED)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_REQUEST_INPROGRESS)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_DONE)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_FILE_IN_CACHE)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_FILE_PINNED)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_SUCCESS)
        {
            return false;
        }
        if (statusCode == TStatusCode.SRM_FAILURE)
        {
            return true;
        }
        
        debugPrintf("***WARNING: status code NOT recognized:"+statusCode.getValue());
        
        return true;
    }

    /**
     * Abort pending SRM request.
     * 
     * @param token
     *            the request tocken
     * @throws SRMException
     */
    public void abortSRMRequests(String token) throws SRMException
    {
        debug("Aborting request: " + token);
        // Abort all files in this request regardless of the state. Expired
        // files are released.
        SrmAbortRequestRequest req = new SrmAbortRequestRequest();
        req.setRequestToken(token);

        SrmAbortRequestResponse res;
        try
        {
            res = getISRM().srmAbortRequest(req);

            if (didRequestFail(res.getReturnStatus().getStatusCode()))
            {
                throw createSRMExceptionFromStatusCode("Error while getting " + res.getClass().getName(), res.getReturnStatus());
            }
        }
        catch (RemoteException e)
        {
            throw convertException("Error while aborting request", e);
        }
    }


    // private SrmStatusOfGetRequestResponse sendSRMStatusOfGetRequest(
    // SrmPrepareToGetResponse response,
    // org.apache.axis.types.URI[] urlArray) throws SRMException
    // {
    // // check req progress
    // SrmStatusOfGetRequestRequest statusGetRequest = new
    // SrmStatusOfGetRequestRequest();
    // String token = response.getRequestToken();
    // statusGetRequest.setRequestToken(token);
    //
    // statusGetRequest.setArrayOfSourceSURLs(new ArrayOfAnyURI(urlArray));
    //
    // SrmStatusOfGetRequestResponse statusResponse = new
    // SrmStatusOfGetRequestResponse();
    // try
    // {
    // statusResponse = getISRM().srmStatusOfGetRequest(statusGetRequest);
    // }
    // catch (RemoteException e)
    // {
    // throw convertException("Couldn't send StatusOfGetRequestRequest", e);
    // }
    //
    // TReturnStatus returnStatus = statusResponse.getReturnStatus();
    //
    // debug("SrmStatusOfGetRequestResponse: "
    // + returnStatus.getStatusCode().getValue());
    //
    // if (!returnStatus.getStatusCode()==(
    // TStatusCode.SRM_REQUEST_INPROGRESS))
    // {
    // if (!returnStatus.getStatusCode()==(TStatusCode.SRM_SUCCESS))
    // {
    // throw handleStatusCode(returnStatus);
    // }
    // }
    //        
    // return statusResponse;
    // }

    private SrmPrepareToGetResponse sendSRMPrepareToGetRequest(org.apache.axis.types.URI[] sourceSURLs,
            TAccessPattern accessPattern, String[] transportProtocols) throws SRMException
    {
        TGetFileRequest[] requestArray = new TGetFileRequest[sourceSURLs.length];

        debug("Sending SRM Prepare To Get Request for: ");
        for (int i = 0; i < sourceSURLs.length; i++)
        {
            requestArray[i] = new TGetFileRequest();
            requestArray[i].setSourceSURL(sourceSURLs[i]);
            debug("\t" + sourceSURLs[i]);
        }

        ArrayOfTGetFileRequest arrayOfFileRequests = new ArrayOfTGetFileRequest();
        arrayOfFileRequests.setRequestArray(requestArray);

        SrmPrepareToGetRequest request = new SrmPrepareToGetRequest();
        request.setArrayOfFileRequests(arrayOfFileRequests);

        TTransferParameters transferParameters = new TTransferParameters();
        if (accessPattern != null)
        {
            transferParameters.setAccessPattern(accessPattern);
            debug("TransferParameters AccessPattern: " + accessPattern.getValue());
        }

        if (transportProtocols == null)
        {
            transportProtocols = getTransportProtocols();
        }

        transferParameters.setArrayOfTransferProtocols(new ArrayOfString(transportProtocols));
        for (int i = 0; i < transportProtocols.length; i++)
        {
            debug("TransferParameters ArrayOfTransferProtocols: " + transportProtocols[i]);
        }

        request.setTransferParameters(transferParameters);

        SrmPrepareToGetResponse response = null;
        try
        {
            response = getISRM().srmPrepareToGet(request);
        }
        catch (RemoteException e)
        {
            convertException("Couldn't send SrmPrepareToGetRequest", e);
        }

        return response;
    }

    @Override
    public String[] getTransportProtocols() throws SRMException
    {
        if (this.transportProtocols == null)
        {
            SrmGetTransferProtocolsRequest srmGetTransferProtocolsRequest = new SrmGetTransferProtocolsRequest();
            try
            {
                SrmGetTransferProtocolsResponse response = getISRM().srmGetTransferProtocols(
                        srmGetTransferProtocolsRequest);

                TSupportedTransferProtocol[] supportedProtocols = response.getProtocolInfo().getProtocolArray();
                String[] stringArray = new String[supportedProtocols.length];
                for (int i = 0; i < supportedProtocols.length; i++)
                {
                    debug("supported: " + supportedProtocols[i].getTransferProtocol());
                    stringArray[i] = supportedProtocols[i].getTransferProtocol();
                }

                transportProtocols = stringArray;
            }
            catch (RemoteException e)
            {
                throw convertException("Couldn't get transfer protocols", e);
            }
        }

        return transportProtocols;
    }

    /**
     * Creates a put request so a file can be placed in the srm service.
     * 
     * @param suris
     *            the target uri.
     * @param overwriteOption
     *            Overwrite option (i.e. TOverwriteMode.NEVER)
     * @param transferParameters
     *            . The transfer parameters (the most important in these options
     *            is the transport protocol )
     * @return the srm put request, from which the transport uris and request
     *         token can be obatined
     * @throws SRMException
     */
    public SRMPutRequest srmCreatePutRequest(org.apache.axis.types.URI[] suris, TOverwriteMode overwriteOption,
            TTransferParameters transferParameters) throws SRMException
    {
        return srmCreatePutRequest(suris, null, null, null, null, null, overwriteOption, null, null, null,
                transferParameters);

    }

    /**
     * Creates a put request so a file can be placed in the srm service. The
     * default transport protocol is gsiftp
     * 
     * @param suris
     *            the target uri.
     * @param overwriteOption
     *            Overwrite option (i.e. TOverwriteMode.NEVER)
     * @return the srm put request, from which the transport uris and request
     *         tocken can be obatined
     * @throws SRMException
     */
    public SRMPutRequest srmCreatePutRequest(org.apache.axis.types.URI[] suris, TOverwriteMode overwriteOption)
            throws SRMException
    {
        TTransferParameters transferParameters = new TTransferParameters();
        transferParameters
                .setArrayOfTransferProtocols(new ArrayOfString(new String[] { SRMConstants.GSIFTP_PROTOCOL }));

        return srmCreatePutRequest(suris, null, null, null, null, null, overwriteOption, null, null, null,
                transferParameters);
    }

    /**
     * Creates a put request so a file can be placed in the srm service.
     * 
     * @param suris
     *            the target uri.
     * @param expectedFileSizes
     *            the expected file sizes
     * @param desiredFileLifeTime
     *            the desired file life time
     * @param desiredFileStorageType
     *            the desired storage type (i.e.
     *            TfileStorageType.PERMANEN,TFileStorageType.VOLATILE)
     * @param desiredPinLifeTime
     *            pin life time (the time the file will remain in the dick
     *            cache)
     * @param desiredTotalRequestTime
     *            desired total request time
     * @param overwriteOption
     *            overwrite mode (i.e. TOverwriteMode.ALWAYS)
     * @param storageSystemInfo
     *            array of extra info
     * @param targetFileRetentionPolicyInfo
     *            retention policy info (see
     *            https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html#_Toc241633046 )
     * @param targetSpaceToken
     *            the target space token (see
     *            https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html#_Toc241633096)
     * @param transferParameters
     *            The transfer parameters (the most important in these options
     *            is the transport protocol )
     * @return the srm put request, from which the transport uris and request
     *         token can be obatined
     * @throws SRMException
     */
    public SRMPutRequest srmCreatePutRequest(org.apache.axis.types.URI[] suris, UnsignedLong[] expectedFileSizes,
            Integer desiredFileLifeTime, TFileStorageType desiredFileStorageType, Integer desiredPinLifeTime,
            Integer desiredTotalRequestTime, TOverwriteMode overwriteOption, ArrayOfTExtraInfo storageSystemInfo,
            TRetentionPolicyInfo targetFileRetentionPolicyInfo, String targetSpaceToken,
            TTransferParameters transferParameters) throws SRMException
    {
        if (expectedFileSizes != null)
        {
            if (suris.length != expectedFileSizes.length)
            {
                throw new SRMException("URIs and the size arry must be the same size", null);
            }
        }

        TPutFileRequest[] requestArray = new TPutFileRequest[suris.length];

        for (int i = 0; i < requestArray.length; i++)
        {
            requestArray[i] = new TPutFileRequest();
            requestArray[i].setTargetSURL(suris[i]);

            if (expectedFileSizes != null && expectedFileSizes[i] != null)
            {
                requestArray[i].setExpectedFileSize(expectedFileSizes[i]);
            }
        }
        ArrayOfTPutFileRequest arrayOfFileRequests = new ArrayOfTPutFileRequest();
        arrayOfFileRequests.setRequestArray(requestArray);

        SrmPrepareToPutRequest srmPrepareToPutRequest = createSrmPrepareToPutRequest(arrayOfFileRequests,
                desiredFileLifeTime, desiredFileStorageType, desiredPinLifeTime, desiredTotalRequestTime,
                overwriteOption, storageSystemInfo, targetFileRetentionPolicyInfo, targetSpaceToken, transferParameters);
        SRMPutRequest putRequest = null;

        try
        {

            SrmPrepareToPutResponse prepareToPutResponse = getISRM().srmPrepareToPut(srmPrepareToPutRequest);

            if (didRequestFail(prepareToPutResponse.getReturnStatus().getStatusCode()))
            {

                throw createSRMExceptionFromStatusCode("Error while getting SrmPrepareToPutResponse", prepareToPutResponse
                        .getReturnStatus());
            }

            // if there where no errors start polling
            ISRMResponse response = new SRMPutResponse(prepareToPutResponse, suris);
            ISRMStatusOfRequestResponse statusPutRequest = pollStatus(response);

            if (statusPutRequest==null)
                debugPrintf("After polling, status is NULL!\n"); 
            else
                debugPrintf("After polling, status is: %s (expl=%s)\n", 
                        statusPutRequest.getReturnStatus().getStatusCode().getValue(), 
                        statusPutRequest.getReturnStatus().getExplanation());

            IFileStatus[] statusArray = statusPutRequest.getStatusArray();

            org.apache.axis.types.URI[] surls = new org.apache.axis.types.URI[statusArray.length];
            org.apache.axis.types.URI[] turls = new org.apache.axis.types.URI[statusArray.length];

            for (int i = 0; i < statusArray.length; i++)
            {
                surls[i] = statusArray[i].getSURL();
                turls[i] = statusArray[i].getTransferURL();
            }

            // SrmPutDoneRequest putDoneRequest = new SrmPutDoneRequest();
            putRequest = new SRMPutRequest();
            putRequest.setToken(response.getRequestToken());
            putRequest.setSURLs(new ArrayOfAnyURI(suris));
            putRequest.setTURLs(new ArrayOfAnyURI(turls));

        }
        catch (RemoteException e)
        {
            convertException("Couldn't create put request for on or more of:\n" + concatinateURI("-",suris), e);
        }

        return putRequest;
    }

    /**
     * Finalizes the put request. After the srm service has successfully
     * returned the transport uri and the files have been transferred, use this
     * method to register the file paths to the service.
     * 
     * @param putRequest
     *            the put request (see <code>srmCreatePutRequest</code>)
     * @param succeeded  Whether the put reques succeed or the new entry should be discarded!
     * @return true if the files have being successfully registered
     * @throws SRMException
     */
    public boolean finalizePutRequest(SRMPutRequest putRequest, boolean succeeded) throws SRMException
    {

        SrmPutDoneRequest srmPutDoneRequest = new SrmPutDoneRequest();
        srmPutDoneRequest.setArrayOfSURLs(putRequest.getSURLs());
        srmPutDoneRequest.setRequestToken(putRequest.getToken());

        SrmPutDoneResponse srmPutDoneResponse = null;
        try
        {
            srmPutDoneResponse = getISRM().srmPutDone(srmPutDoneRequest);
        }
        catch (RemoteException e)
        {
            throw convertException("Couldn't finalize put request", e);
        }

        if (!didRequestSucceed(srmPutDoneResponse.getReturnStatus().getStatusCode()))
        {

            throw handleStatusCode("Error while getting srmPutDoneResponse", srmPutDoneResponse.getReturnStatus(),
                    srmPutDoneResponse.getArrayOfFileStatuses());
        }

        return true;

    }

    /**
     * Move a source uri to a destination uri.(on the sane srm service)
     * 
     * @param sourceURI
     *            the source uri
     * @param destinationURI
     *            the destination uri
     * @return true on success
     * @throws SRMException
     */
    public boolean srmMv(org.apache.axis.types.URI sourceURI, org.apache.axis.types.URI destinationURI)
            throws SRMException
    {
        SrmMvRequest mvRequest = new SrmMvRequest();
        mvRequest.setFromSURL(sourceURI);
        mvRequest.setToSURL(destinationURI);

        try
        {
            SrmMvResponse mvResonce = getISRM().srmMv(mvRequest);

            if (!didRequestSucceed(mvResonce.getReturnStatus().getStatusCode()))
            {
                throw createSRMExceptionFromStatusCode("Error while getting " + mvResonce.getClass().getName() + ". to move from "
                        + sourceURI + " to " + destinationURI, mvResonce.getReturnStatus());
            }
        }
        catch (RemoteException e)
        {
            throw convertException("Couldn't mv " + sourceURI + " to " + destinationURI, e);
        }

        return true;
    }

    /**
     * Returns true only if the request is successful (i.e. if queued => false )
     * 
     * @param statusCode
     *            the returned status code
     * @return true if request is successful
     */
    private boolean didRequestSucceed(TStatusCode statusCode)
    {
        debugPrintf("Status Code:%s\n", statusCode.getValue());

        if (statusCode == TStatusCode.SRM_SUCCESS)
        {
            return true;
        }
        if (statusCode == TStatusCode.SRM_DONE)
        {
            return true;
        }
        return false;
    }

    private SrmPrepareToPutRequest createSrmPrepareToPutRequest(ArrayOfTPutFileRequest arrayOfFileRequests,
            Integer desiredFileLifeTime, TFileStorageType desiredFileStorageType, Integer desiredPinLifeTime,
            Integer desiredTotalRequestTime, TOverwriteMode overwriteOption, ArrayOfTExtraInfo storageSystemInfo,
            TRetentionPolicyInfo targetFileRetentionPolicyInfo, String targetSpaceToken,
            TTransferParameters transferParameters)
    {

        SrmPrepareToPutRequest srmPrepareToPutRequest = new SrmPrepareToPutRequest();
        // set necessary options to the request
        srmPrepareToPutRequest.setArrayOfFileRequests(arrayOfFileRequests);
        srmPrepareToPutRequest.setTransferParameters(transferParameters);

        // set extra options if they exist or there is a default option
        if (desiredFileLifeTime != null)
        {
            srmPrepareToPutRequest.setDesiredFileLifeTime(desiredFileLifeTime);
        }
        else if (this.getSrmFileOptions().getDesiredFileLifeTime() != null)
        {
            srmPrepareToPutRequest.setDesiredFileLifeTime(getSrmFileOptions().getDesiredFileLifeTime());
        }

        if (desiredFileStorageType != null)
        {
            srmPrepareToPutRequest.setDesiredFileStorageType(desiredFileStorageType);
        }
        else if (this.getSrmFileOptions().getDesiredFileStorageType() != null)
        {
            srmPrepareToPutRequest.setDesiredFileStorageType(getSrmFileOptions().getDesiredFileStorageType());
        }

        if (desiredPinLifeTime != null)
        {
            srmPrepareToPutRequest.setDesiredPinLifeTime(desiredPinLifeTime);
        }
        else if (getSrmFileOptions().getDesiredTotalRequestTime() != null)
        {
            srmPrepareToPutRequest.setDesiredPinLifeTime(getSrmFileOptions().getDesiredPinLifeTime());
        }

        if (desiredTotalRequestTime != null)
        {
            srmPrepareToPutRequest.setDesiredTotalRequestTime(desiredTotalRequestTime);
        }
        else if (getSrmFileOptions().getDesiredTotalRequestTime() != null)
        {
            srmPrepareToPutRequest.setDesiredTotalRequestTime(getSrmFileOptions().getDesiredTotalRequestTime());
        }

        if (overwriteOption != null)
        {
            srmPrepareToPutRequest.setOverwriteOption(overwriteOption);
        }
        else if (getSrmFileOptions().getOverwriteOption() != null)
        {
            srmPrepareToPutRequest.setOverwriteOption(getSrmFileOptions().getOverwriteOption());
        }

        if (storageSystemInfo != null)
        {
            srmPrepareToPutRequest.setStorageSystemInfo(storageSystemInfo);
        }
        else if (getSrmFileOptions().getStorageSystemInfo() != null)
        {
            srmPrepareToPutRequest.setStorageSystemInfo(getSrmFileOptions().getStorageSystemInfo());
        }

        if (targetFileRetentionPolicyInfo != null)
        {
            srmPrepareToPutRequest.setTargetFileRetentionPolicyInfo(targetFileRetentionPolicyInfo);
        }
        else if (getSrmFileOptions().getTargetFileRetentionPolicyInfo() != null)
        {
            srmPrepareToPutRequest.setTargetFileRetentionPolicyInfo(getSrmFileOptions()
                    .getTargetFileRetentionPolicyInfo());
        }

        if (targetSpaceToken != null)
        {
            srmPrepareToPutRequest.setTargetSpaceToken(targetSpaceToken);
        }
        else if (getSrmFileOptions().getTargetSpaceToken() != null)
        {
            srmPrepareToPutRequest.setTargetSpaceToken(getSrmFileOptions().getTargetSpaceToken());
        }
        return srmPrepareToPutRequest;
    }

    /**
     * Blocks until the service response is successful or times out
     * 
     * @param response
     *            the response from the service
     * @return the status of the request
     * @throws SRMException
     */
    private ISRMStatusOfRequestResponse pollStatus(ISRMResponse response) throws SRMException
    {
        TReturnStatus status = response.getReturnStatus();
        ISRMStatusOfRequestResponse requestResponse = null;
        
        if (didRequestFail(status.getStatusCode()))
        {            
            throw createSRMExceptionFromStatusCode("Error while getting " + ISRMStatusOfRequestResponse.class.getName(), status);
        }
        
        try
        {
            ISRMRequestStatusOfRequest statusOfRequest = createISRMRequestStatusOfRequest(response);
            
            Integer sleepTime = null;
            int tryCount = 1;
            long totalWaitTime=0; 
             
            
            do
            {
                // Pre check: can be optimized: 
                if (didRequestSucceed(status.getStatusCode()))
                {
                    debugPrintf(">>>\n>>> REQUEST SUCCEEDED <<<\n>>>");
                    // DOES SRM AGAIN CALL ! 
                    requestResponse = getSrmStatusOfRequest(statusOfRequest);
                    return requestResponse; 

                }
                
                requestResponse = getSrmStatusOfRequest(statusOfRequest);

                tryCount++;
                debug("Polling....Token: " + requestResponse.getToken() + " ReturnStatus: "
                        + requestResponse.getReturnStatus().getStatusCode().getValue() + " "
                        + requestResponse.getReturnStatus().getExplanation());

                status = requestResponse.getReturnStatus();

                // try to use srm time estimate
                sleepTime = requestResponse.getRemainingTotalRequestTime();
                if (sleepTime == null || sleepTime <= 0)
                {
                    sleepTime = initialWaitTime + this.incrementalWaitTime * tryCount;
                }

                totalWaitTime+=sleepTime;
                
                debug("Sleeping: " + sleepTime +" (total="+totalWaitTime+")");
                    Thread.sleep(sleepTime);
                    
                    
                int maxTime=getSRMRequestTimeout();
                // timeout or error...
                if (totalWaitTime > maxTime)
                {
                    debug("TIMEOUT: SleepTime=" + sleepTime + " getSRMRequestTimeout: " + maxTime
                            + "\n Must abort files. Status: " + status.getStatusCode().getValue() + " " + status.getExplanation());
                    
                    errorPrintf("Timeout: totalWaitTime > srmRequestTimeOut: %d>%d\n",totalWaitTime,maxTime);

                    throw createSRMExceptionFromStatusCode("Timeout Error ("+totalWaitTime+"ms) while getting " + requestResponse.getClass().getName() + ". Request aborted",
                            response.getReturnStatus());
                }
                        
                if (didRequestFail(status.getStatusCode()))
                {
                    debug("Request Failed. SleepTime=" + sleepTime + " getSRMRequestTimeout: " + getSRMRequestTimeout()
                            + "\n Must abort files. Status: " + status.getStatusCode().getValue() + " " + status.getExplanation());

                    abortSRMRequests(requestResponse.getToken());

                    // .. and throw exception
                    throw handleFailedRequest(requestResponse, response);
                }

            } while (tryCount<100);

            throw createSRMExceptionFromStatusCode("Error while getting " + ISRMStatusOfRequestResponse.class.getName(), status);
            
        }
        catch (RemoteException e)
        {
            throw convertException("Couldn't poll for status", e);
        }
        catch (InterruptedException e)
        {
            throw convertException("Interrupted", e);
        }
        
    }

    private SRMException handleFailedRequest(ISRMStatusOfRequestResponse requestResponse,
            ISRMResponse response)
    {
        TReturnStatus returnStatus = requestResponse.getReturnStatus();
        
        if (requestResponse instanceof SRMStatusOfRequestPutResponse)
        {
            //
            // PTdB: check failed if previous PutRequest but wasn't abort correctly! 
            // The SRM database should be purged/updated somehow. 
            String explStr=""+returnStatus.getExplanation();
            if (explStr.contains("Already have 1 record"))
            {
                // previous PutRequest was already performed but not finished!
                return  createSRMExceptionFromStatusCode("Previous PutRequest probably failed. Cannot continue!"
                        + "\nRequestType="+requestResponse.getClass().getName() + ". Request aborted.",
                        returnStatus);
            }
            
            debug("getRequestToken: " + response.getRequestToken());
            debug("getValue: " + response.getReturnStatus().getStatusCode().getValue());

            for (int i = 0; i < response.getSURIs().length; i++)
            {
                debug("getSURIs: " + response.getSURIs()[i]);
            }

            // following code can fail also!
            try
            {
                // check status or putrequests: 
                ArrayOfTMetaDataPathDetail details = srmGetMetaDataPathDetails(0, response.getSURIs(), false, false,-1,-1);
                
                String[] requestedPaths = createPathsArray(response.getSURIs());
                java.util.List<String> listRequestedPaths = Arrays.asList(requestedPaths);

                returnStatus = requestResponse.getReturnStatus();
                for (int i = 0; i < details.getPathDetailArray().length; i++)
                {
                    if (listRequestedPaths.contains(details.getPathDetailArray()[i].getPath()))
                    {
                        returnStatus = new TReturnStatus(TStatusCode.SRM_DUPLICATION_ERROR,
                                "One or more of the files requested to put on the server already exists");
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                // something else is wrong: 
                logger.log(Level.FINE,"Caught (Another) exception when trying to handle failed PutRequest. Exception=e",e);
            }
            
        }

        return  createSRMExceptionFromStatusCode("Error while getting " + requestResponse.getClass().getName() + ". Request aborted",
                returnStatus);
    }

    private ISRMRequestStatusOfRequest createISRMRequestStatusOfRequest(ISRMResponse response)
    {
        ISRMRequestStatusOfRequest statusOfRequest = null;

        if (response instanceof SRMGetResponse)
        {
            statusOfRequest = new SRMRequestStatusOfGetRequest((SRMGetResponse) response);
        }
        if (response instanceof SRMPutResponse)
        {
            statusOfRequest = new SRMRequestStatusOfPutRequest((SRMPutResponse) response);
        }
        if (response instanceof SRMCpResponse)
        {
            statusOfRequest = new SRMRequestStatusOfCpRequest((SRMCpResponse) response);
        }
        return statusOfRequest;
    }

    private ISRMStatusOfRequestResponse getSrmStatusOfRequest(ISRMRequestStatusOfRequest statusOfRequest)
            throws RemoteException
    {
        ISRMStatusOfRequestResponse response = null;

        if (statusOfRequest instanceof SRMRequestStatusOfGetRequest)
        {
            SrmStatusOfGetRequestRequest statusGetRequest = new SrmStatusOfGetRequestRequest();

            statusGetRequest.setRequestToken(statusOfRequest.getRequestToken());
            statusGetRequest.setArrayOfSourceSURLs(new ArrayOfAnyURI(statusOfRequest.getSURIs()));

            SrmStatusOfGetRequestResponse getResponse = getISRM().srmStatusOfGetRequest(statusGetRequest);
            response = new SRMStatusOfRequestGetResponse(getResponse);

        }
        if (statusOfRequest instanceof SRMRequestStatusOfPutRequest)
        {
            SrmStatusOfPutRequestRequest statusPutRequest = new SrmStatusOfPutRequestRequest();

            // org.apache.axis.types.URI[] uri =
            // ((SRMRequestStatusOfPutRequest)statusOfRequest).getTargetSURLs();
            // statusPutRequest.setArrayOfTargetSURLs(new ArrayOfAnyURI(uri));
            statusPutRequest.setRequestToken(statusOfRequest.getRequestToken());
            SrmStatusOfPutRequestResponse putResponse = getISRM().srmStatusOfPutRequest(statusPutRequest);
            response = new SRMStatusOfRequestPutResponse(putResponse);
        }
        if (statusOfRequest instanceof SRMRequestStatusOfCpRequest)
        {
            SrmStatusOfCopyRequestRequest srmStatusOfCopyRequestRequest = new SrmStatusOfCopyRequestRequest();
            srmStatusOfCopyRequestRequest.setRequestToken(statusOfRequest.getRequestToken());

            SrmStatusOfCopyRequestResponse cpResponse = getISRM().srmStatusOfCopyRequest(srmStatusOfCopyRequestRequest);

            response = new SRMStatusOfRequestCpResponse(cpResponse);
        }
        response.setToken(statusOfRequest.getRequestToken());
        return response;
    }

    // ========================================================================
    // ========================================================================
    //
    // SRM Path methods
    // 
    // ========================================================================
    // ========================================================================

    /**
     * Creates new directory.
     * 
     * @param path
     *            the path to create
     * @return true on success
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean mkdir(String path) throws SRMException
    {
        return srmMkdir(createPathSurlURI(path));
    }

    /**
     * Removes directory along with all its contents (force) on SRM Server.
     * 
     * @param path
     *            the path to remove
     * @param recursive
     *            recursion mode
     * @return true on success
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean rmdir(String path, boolean recursive) throws SRMException
    {
        return srmRmdir(createPathSurlURI(path), recursive);
    }

    /**
     * Delete single path.
     * 
     * @param path
     *            the path to remove
     * @return true on success
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean rm(String path) throws SRMException, MalformedURIException
    {
        return srmRm(this.createURIArray(new String[] { path }));
    }

    /**
     * Delete set of paths.
     * 
     * @param paths
     *            the paths to remove
     * @return true on success
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean rm(String paths[]) throws SRMException, MalformedURIException
    {
        return srmRm(this.createURIArray(paths));
    }

    /**
     * Move a source path to a destination path.(on the sane srm service)
     * 
     * @param source
     *            the source path
     * @param dest
     *            the destination path
     * @return true on success
     * @throws SRMException
     * @throws MalformedURIException
     */
    public boolean mv(String source, String dest) throws SRMException
    {
        org.apache.axis.types.URI sourceSurlURI = createPathSurlURI(source);
        org.apache.axis.types.URI destSurlURI = createPathSurlURI(dest);
        return srmMv(sourceSurlURI, destSurlURI);
    }

    // /**
    // * Use this method with caution!!!Forces delete all files provided in
    // suris.
    // * if suris contains a folder this method will do a force recursive
    // delete.
    // *
    // * @param suris
    // * the array of files and/or folders to delete
    // * @return true if all succeed
    // * @throws SRMException
    // */
    // public boolean rmDirBulk(org.apache.axis.types.URI[] suris) throws
    // SRMException
    // {
    // boolean allLevelRecursive = true;
    // boolean fullDetails = false;
    // ArrayOfAnyURI arrayOfAnyURI = new ArrayOfAnyURI(suris);
    // // srmRm(arrayOfSURLs);
    // ArrayOfTMetaDataPathDetail details = srmGetMetaDataPathDetails(1,
    // arrayOfAnyURI, fullDetails, allLevelRecursive);
    // // details.getPathDetailArray()[0].
    // return false;
    // }

    @Override
    public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs) throws SRMException
    {
        return this.srmGetTransportURIs(sourceSURLs, TAccessPattern.TRANSFER_MODE,
                new String[] { SRMConstants.GSIFTP_PROTOCOL });

    }

    @Override
    public org.apache.axis.types.URI[] getTransportURIs(org.apache.axis.types.URI[] sourceSURLs,
            String[] transportProtocols) throws SRMException
    {
        return this.srmGetTransportURIs(sourceSURLs, null, transportProtocols);

    }

    // ========================================================================
    // ========================================================================
    //
    // Resource Query/Stat methods.
    //
    // Use srm...()methods.
    // 
    // ========================================================================
    // ========================================================================

    public ArrayList<TMetaDataPathDetail> queryPaths(
            int numOfLevels, 
            org.apache.axis.types.URI[] suris,
            boolean fullDetails, 
            boolean allLevelRecursive) throws SRMException
    {
        return queryPaths(numOfLevels,suris,fullDetails,allLevelRecursive,-1,-1);    
    }
    

    /**
     * Full query method. Queries an array of paths.
     * 
     * @param numOfLevels
     *            The number of levels to return. 1 for a single path, 2 for
     *            directory contents.
     * @param uris
     *            array of paths to query.
     * @param fullDetails
     *            include metadata (e.g. checksum, modification time etc.)
     * @param allLevelRecursive
     *            use recursion when listing directories. (See numOfLevels)
     * @param offset
     *            offset to start listing files from. 
     * @param count
     *            limit number of result to this value. 
     * @return mulilevel MetaDataPathDetail array
     * @throws SRMException
     */
    public ArrayList<TMetaDataPathDetail> queryPaths(
            int numOfLevels, org.apache.axis.types.URI[] suris,
            boolean fullDetails, 
            boolean allLevelRecursive,
            int offset,
            int count) throws SRMException
    {
        ArrayOfTMetaDataPathDetail metaDataPathDetails = srmGetMetaDataPathDetails(numOfLevels, suris, fullDetails,
                allLevelRecursive,offset,count);

        ArrayList<TMetaDataPathDetail> details = new ArrayList<TMetaDataPathDetail>();

        TMetaDataPathDetail[] element = metaDataPathDetails.getPathDetailArray();

        Collection<TMetaDataPathDetail> list = Arrays.asList(element);

        details.addAll(list);

        return details;
    }

    /**
     * Query a single path.
     * 
     * @param path
     * @param fulldetails
     * @return
     * @throws SRMException
     * @throws MalformedURIException
     */
    public TMetaDataPathDetail statPath(String path, Boolean fulldetails) throws SRMException, MalformedURIException
    {
        // One level deep path query:
        ArrayList<TMetaDataPathDetail> details = queryPaths(0, this.createURIArray(new String[] { path }), fulldetails,
                false);

        if ((details == null) || (details.size() <= 0))
            return null; // empty/non existant

        return details.get(0);
    }

    /**
     * Query a set of paths
     * 
     * @param paths
     * @param fulldetails
     * @return
     * @throws SRMException
     * @throws MalformedURIException
     */
    public ArrayList<TMetaDataPathDetail> statPaths(String paths[], Boolean fulldetails) throws SRMException,
            MalformedURIException
    {
        // One level deep path query:
        ArrayList<TMetaDataPathDetail> details = queryPaths(0, this.createURIArray(paths), fulldetails, false);

        if ((details == null) || (details.size() <= 0))
            return null; // empty/non existant

        return details;
    }

    /**
     * Simple query method. Queries an array of paths with 1 level and recursive
     * set to false.
     * 
     * @param paths
     *            the SRM paths to get details from
     * @param fullDetails
     *            include get back metadata (e.g. checksum, modification time
     *            etc.)
     * @return flattened MetaDataPathDetail array
     * @throws MalformedURIException
     * @throws Exception
     */
    public ArrayList<TMetaDataPathDetail> queryPaths(String paths[], boolean fulldetails) throws SRMException,
            MalformedURIException
    {

        // One level deep multi path query:
        ArrayList<TMetaDataPathDetail> details = queryPaths(0, this.createURIArray(paths), fulldetails, false);

        if ((details == null) || (details.size() <= 0))
            return null; // empty/non existant

        return details;
    }

    /**
     * List contents of a single directory.
     * 
     * @throws MalformedURIException
     */
    public ArrayList<TMetaDataPathDetail> listPath(String path, boolean fulldetails) throws SRMException,
            MalformedURIException
    {
        return listPaths(new String[] { path }, fulldetails);
    }

    /**
     * List contents of a single directory.
     * 
     * @throws MalformedURIException
     */
    public ArrayList<TMetaDataPathDetail> listPath(String path, boolean fulldetails,int offset,int count) throws SRMException,
            MalformedURIException
    {
        return listPaths(new String[] { path }, fulldetails,offset,count);
    }
    
    /**
     * List contents of a set of directory paths. Results are merged into a
     * single Array.
     * 
     * @throws SRMException
     * @throws MalformedURIException
     */
    public ArrayList<TMetaDataPathDetail> listPaths(String paths[], boolean fulldetails) throws SRMException,
            MalformedURIException
    {
        org.apache.axis.types.URI[] uris = this.createURIArray(paths);
        return queryPaths(1, uris, fulldetails, false,-1,-1);
    }

    /**
     * List contents of a set of directory paths. Results are merged into a
     * single Array.
     * 
     * @throws SRMException
     * @throws MalformedURIException
     */
    public ArrayList<TMetaDataPathDetail> listPaths(String paths[], 
            boolean fulldetails,
            int offset,
            int count) throws SRMException, MalformedURIException
    {
        org.apache.axis.types.URI[] uris = this.createURIArray(paths);
        return queryPaths(1, uris, fulldetails, false,offset,count);
    }
    
    /**
     * Convert array of paths into SRM compatible SURLS
     * 
     * @param paths
     *            the paths
     * @return an array of uris
     * @throws SRMException
     * @throws MalformedURIException
     */
    public ArrayOfAnyURI createSurlArray(String[] paths) throws SRMException
    {
        if (paths == null)
            return null;

        int len = paths.length;
        org.apache.axis.types.URI uris[] = new org.apache.axis.types.URI[len];

        for (int i = 0; i < len; i++)
                uris[i] = createPathSurlURI(paths[i]);
            
        return new ArrayOfAnyURI(uris);
    }

    /**
     * Convert array of paths into SRM compatible SURLS
     * 
     * @param paths
     *            the paths
     * @return the uris
     * @throws SRMException
     * @throws MalformedURIException
     */
    public org.apache.axis.types.URI[] createURIArray(String[] paths) throws SRMException
    {
        if (paths == null)
            return null;

        int len = paths.length;
        org.apache.axis.types.URI uris[] = new org.apache.axis.types.URI[len];

        for (int i = 0; i < len; i++)
            uris[i] = createPathSurlURI(paths[i]);

        return uris;
    }

    private String concatinateURI(String prefix,org.apache.axis.types.URI[] surls)
    {
        if (prefix==null)
            prefix="";
        
        // concatinate uris
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < surls.length; ++i)
        {
            buf.append(prefix);
            buf.append(surls[i].toString() + "\n");
        }
        return buf.toString();
    }

    private ArrayList<TMetaDataPathDetail> unwrapDetails(TMetaDataPathDetail[] metaDataPathDetail)
    {
        // empty directory!
        if (metaDataPathDetail == null)
            return null;

        ArrayList<TMetaDataPathDetail> flatDetails = new ArrayList<TMetaDataPathDetail>();
        ArrayList<TMetaDataPathDetail> subDetails = null;
        ArrayOfTMetaDataPathDetail subPath = null;
        for (int i = 0; i < metaDataPathDetail.length; i++)
        {
            debug(i + ": " + metaDataPathDetail[i].getPath());

            if (!flatDetails.contains(metaDataPathDetail[i]))
            {
                flatDetails.add(metaDataPathDetail[i]);
            }
            subPath = metaDataPathDetail[i].getArrayOfSubPaths();
            if (subPath != null)
            {
                subDetails = unwrapDetails(subPath.getPathDetailArray());
            }
            // if (subDetails != null && !flatDetails.containsAll(subDetails))
            if (subDetails != null)
            {
                flatDetails.addAll(subDetails);
            }
        }

        return flatDetails;
    }

    private String concatinateURI(ArrayOfAnyURI uris)
    {
        return concatinateURI(" - ", uris.getUrlArray());
    }

    // ========================================================================
    //
    // Debuging/Exceptions/Misc.
    //
    // ========================================================================

    public void printDetails(ArrayList<TMetaDataPathDetail> details)
    {
        for (int i = 0; i < details.size(); i++)
        {
            debug("------------------------------------");
            debug("Path: " + details.get(i).getPath());
            debug("CheckSumType: " + details.get(i).getCheckSumType());
            debug("Lifetime: " + details.get(i).getLifetimeAssigned());
            debug("LifetimeLeft: " + details.get(i).getLifetimeLeft());
            debug("CreatedAtTime: " + details.get(i).getCreatedAtTime());
            debug("LastModificationTime: " + details.get(i).getLastModificationTime());
            if (details.get(i).getFileLocality() != null)
            {
                debug("FileLocality: " + details.get(i).getFileLocality().getValue());
            }
            if (details.get(i).getFileStorageType() != null)
            {
                debug("FileStorageType: " + details.get(i).getFileStorageType().getValue());
            }
            if (details.get(i).getGroupPermission() != null)
            {
                debug("GroupPermission: " + details.get(i).getGroupPermission().getMode().getValue());
            }

            if (details.get(i).getOtherPermission() != null)
            {
                debug("OtherPermission: " + details.get(i).getOtherPermission().getValue());
            }

            if (details.get(i).getOwnerPermission() != null)
            {
                debug("OwnerPermission: " + details.get(i).getOwnerPermission().getMode().getValue());
            }

            if (details.get(i).getRetentionPolicyInfo() != null)
            {
                debug("RetentionPolicyInfo: " + details.get(i).getRetentionPolicyInfo().getRetentionPolicy().getValue());
            }

            debug("Size: " + details.get(i).getSize());

            if (details.get(i).getStatus() != null)
            {
                debug("Status: " + details.get(i).getStatus().getExplanation());
            }

            if (details.get(i).getType() != null)
            {
                debug("Type: " + details.get(i).getType().getValue());
            }
            debug("------------------------------------");

            // next level
            ArrayOfTMetaDataPathDetail subPathArray = details.get(i).getArrayOfSubPaths();
            if (subPathArray != null)
            {
                Collection<TMetaDataPathDetail> list = Arrays.asList(subPathArray.getPathDetailArray());
                ArrayList<TMetaDataPathDetail> subDetails = new ArrayList<TMetaDataPathDetail>();
                subDetails.addAll(list);
                printDetails(subDetails);
            }

        }
    }

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
            return SRMException.createConnectionException(message + "\nConnection error, server might be down or unreachable:" + this.srmUri + "\nReason="
                    + cause.getMessage(), cause);
        }
        
        if (causeStr.contains("Connect timed out") )
        {
            return SRMException.createConnectionException(message + "\nConnection setup timed out, server might be down or unreachable." + this.srmUri + "\nReason="
                    + cause.getMessage(), cause);
        }

        return new SRMException(message + "\nReason=" + cause.getMessage(), cause);
    }

    private SRMException handleStatusCode(String whatWasExecuted, TReturnStatus returnStatus,
            ArrayOfTSURLReturnStatus arrayOfFileStatuses)
    {
        StringBuffer errorMsgBuf = new StringBuffer();
        for (int i = 0; i < arrayOfFileStatuses.getStatusArray().length; i++)
        {
            if (!didRequestSucceed(arrayOfFileStatuses.getStatusArray()[i].getStatus().getStatusCode()))
            {
                errorMsgBuf.append(arrayOfFileStatuses.getStatusArray()[i].getStatus().getStatusCode().getValue()
                        + " for, " + arrayOfFileStatuses.getStatusArray()[i].getSurl() + ". Ditails"
                        + arrayOfFileStatuses.getStatusArray()[i].getStatus().getExplanation() + "\n");
            }

        }
        return createSRMExceptionFromStatusCode(whatWasExecuted + "\n" + errorMsgBuf.toString(), returnStatus);
    }

    private SRMException createSRMExceptionFromStatusCode(String whatWasExecuted, TReturnStatus returnStatus)
    {
        debugPrintf("------------------ERROR!!!---------------------\n");
        debugPrintf("What was executed : %s\n", whatWasExecuted);
        debugPrintf("TReturnStatus     : %s\n", returnStatus.getStatusCode().getValue());
        debugPrintf("Explanation       : %s\n", returnStatus.getExplanation());
        debugPrintf("-----------------------------------------------\n");

        // Generic Exception:
        return new SRMException(whatWasExecuted + "\n" + "SRM Status Code =" + returnStatus.getStatusCode().getValue()
                + " SRM Explanation =" + returnStatus.getExplanation(), returnStatus, null);
    }

    // private SRMException handleStatusCodeOld(String whatWasExecuted,
    // TReturnStatus returnStatus)
    // {
    // ErrorType errorCode = SRMException.ErrorType.GENERAL_ERROR;
    // String explStr=returnStatus.getExplanation();
    //        
    // // No Error!
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_DONE))
    // {
    // return null; // indicates no error!
    // }
    //
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_SUCCESS))
    // {
    // return null; // indicates no error!
    // }
    //
    // // Filter out specific ones
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_INVALID_PATH))
    // {
    // errorCode = SRMException.ErrorType.PATH_ALREADY_EXISTS;
    // }
    //
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_AUTHENTICATION_FAILURE))
    // {
    // errorCode = SRMException.ErrorType.AUTHENTICATION_ERROR;
    // }
    //
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_AUTHORIZATION_FAILURE))
    // {
    // errorCode = SRMException.ErrorType.AUTHENTICATION_ERROR;
    // }
    //
    // // Keep the rest:
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_ABORTED))
    // {
    //
    // }
    //
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_CUSTOM_STATUS))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_DUPLICATION_ERROR))
    // {
    // errorCode = SRMException.ErrorType.PATH_ALREADY_EXISTS;
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_EXCEED_ALLOCATION))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_FATAL_INTERNAL_ERROR))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_FILE_BUSY))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_FILE_IN_CACHE))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_FILE_LIFETIME_EXPIRED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_FILE_LOST))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_FILE_PINNED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_FILE_UNAVAILABLE))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_INTERNAL_ERROR))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()
    // ==(TStatusCode.SRM_INVALID_REQUEST))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_LAST_COPY))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_LOWER_SPACE_GRANTED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_NO_FREE_SPACE))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_NO_USER_SPACE))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_NON_EMPTY_DIRECTORY))
    // {
    // errorCode = SRMException.ErrorType.PATH_ALREADY_EXISTS;
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_NOT_SUPPORTED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()
    // ==(TStatusCode.SRM_PARTIAL_SUCCESS))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_RELEASED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_REQUEST_INPROGRESS))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(TStatusCode.SRM_REQUEST_QUEUED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_REQUEST_SUSPENDED))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_REQUEST_TIMED_OUT))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()
    // ==(TStatusCode.SRM_SPACE_AVAILABLE))
    // {
    //
    // }
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_SPACE_LIFETIME_EXPIRED))
    // {
    //
    // }
    //
    // if (returnStatus.getStatusCode()==(
    // TStatusCode.SRM_TOO_MANY_RESULTS))
    // {
    //
    // }
    //
    // // Generic Exception:
    // return new SRMException(whatWasExecuted + "\n" + "SRM Status Code ="
    // + returnStatus.getStatusCode().getValue()
    // + " SRM Explanation =" + returnStatus.getExplanation(),
    // errorCode, returnStatus.getStatusCode(), null);
    // }

    private void debug(String msg)
    {
        logger.log(Level.FINE,msg+"\n");
    }

    @Override
    public String getVersion() throws SRMException
    {
        // cache version: doesn't change during lifetime. 
        
        if (this.versionInfo==null)
        {
            this.versionInfo=srmPing().getVersionInfo();
        }
        
        return this.versionInfo; 
    }

    public String getBackendType() throws SRMException
    {
        ArrayOfTExtraInfo info = srmPing().getOtherInfo();
        String backendType = null;
        for (int i = 0; i < info.getExtraInfoArray().length; i++)
        {
            if (info.getExtraInfoArray()[i].getKey().startsWith("backend_type"))
            {
                backendType = info.getExtraInfoArray()[i].getValue();
                break;
            }
        }

        return backendType.toUpperCase();
    }

    public String getBackendVersion() throws SRMException
    {
        ArrayOfTExtraInfo info = srmPing().getOtherInfo();
        String backendType = null;
        for (int i = 0; i < info.getExtraInfoArray().length; i++)
        {
            if (info.getExtraInfoArray()[i].getKey().startsWith("backend_version"))
            {
                backendType = info.getExtraInfoArray()[i].getValue();
                break;
            }
        }

        return backendType;
    }

    public void srmSetPermitions(ArrayOfTUserPermission arrayOfUserPermissions,
            ArrayOfTGroupPermission arrayOfGroupPermissions, TPermissionMode otherPermission,
            TPermissionMode ownerPermission, TPermissionType permissionType, URI SURL) throws SRMException
    {
        SrmSetPermissionRequest srmSetPermissionRequest = new SrmSetPermissionRequest();

        if (arrayOfUserPermissions != null)
        {
            srmSetPermissionRequest.setArrayOfUserPermissions(arrayOfUserPermissions);
        }
        if (arrayOfGroupPermissions != null)
        {
            srmSetPermissionRequest.setArrayOfGroupPermissions(arrayOfGroupPermissions);
        }
        if (otherPermission != null)
        {
            srmSetPermissionRequest.setOtherPermission(otherPermission);
        }
        if (ownerPermission != null)
        {
            srmSetPermissionRequest.setOwnerPermission(ownerPermission);
        }
        srmSetPermissionRequest.setPermissionType(permissionType);
        srmSetPermissionRequest.setSURL(SURL);

        try
        {
            SrmSetPermissionResponse responce = this.getISRM().srmSetPermission(srmSetPermissionRequest);

            if (!didRequestSucceed(responce.getReturnStatus().getStatusCode()))
            {
                throw createSRMExceptionFromStatusCode("srmSetPermitions", responce.getReturnStatus());
            }

            System.err.println("responce:   " + responce.getReturnStatus());
        }
        catch (RemoteException e)
        {
            throw convertException("Couldn't send permition change request ", e);
        }
    }

    public void setPermitions(URI SURL, TMetaDataPathDetail srmDetails, String srmPermitionString) throws SRMException
    {

        String userPermitionString = srmPermitionString.substring(0, 3).replaceAll("-", "");
        String grupPermitionString = srmPermitionString.substring(4, 6).replaceAll("-", "");
        String otherPermitionString = srmPermitionString.substring(7, srmPermitionString.length()).replaceAll("-", "");
        debug("srmPermitionString:   " + srmPermitionString);
        debug("userPermitionString:   " + userPermitionString);
        debug("grupPermitionString:   " + grupPermitionString);
        debug("otherPermitionString:   " + otherPermitionString);

        debug("userPermitionString String value " + TPermissionMode.fromString(userPermitionString));
        debug("grupPermitionString String value " + TPermissionMode.fromString(grupPermitionString));
        debug("otherPermitionString String value " + TPermissionMode.fromString(otherPermitionString));

        ArrayOfTUserPermission arrayOfUserPermissions = new ArrayOfTUserPermission();
        TUserPermission[] userPermissionArray = new TUserPermission[1];
        userPermissionArray[0] = new TUserPermission();
        userPermissionArray[0].setMode(TPermissionMode.fromString(userPermitionString));
        userPermissionArray[0].setUserID("USER-ID");
        arrayOfUserPermissions.setUserPermissionArray(userPermissionArray);

        ArrayOfTGroupPermission arrayOfGroupPermissions = new ArrayOfTGroupPermission();
        TGroupPermission[] groupPermissionArray = new TGroupPermission[1];
        groupPermissionArray[0] = new TGroupPermission();
        groupPermissionArray[0].setMode(TPermissionMode.fromString(grupPermitionString));
        groupPermissionArray[0].setGroupID("GRUP-ID");
        arrayOfGroupPermissions.setGroupPermissionArray(groupPermissionArray);

        // TPermissionMode otherPermission = tMode;
        //        
        // TPermissionMode ownerPermission = tMode;
        //                
        this.srmSetPermitions(arrayOfUserPermissions, arrayOfGroupPermissions, TPermissionMode
                .fromString(otherPermitionString), null, TPermissionType.CHANGE, SURL);
    }

    public void setSrmFileOptions(SRMFileOptions srmFileOptions)
    {
        this.srmFileOptions = srmFileOptions;
    }

    public SRMFileOptions getSrmFileOptions()
    {
        return srmFileOptions;
    }


}
