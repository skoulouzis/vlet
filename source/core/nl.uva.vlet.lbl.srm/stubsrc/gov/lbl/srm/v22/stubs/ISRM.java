/**
 * ISRM.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public interface ISRM extends java.rmi.Remote
{
    public gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse srmReserveSpace(
            gov.lbl.srm.v22.stubs.SrmReserveSpaceRequest srmReserveSpaceRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse srmStatusOfReserveSpaceRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestRequest srmStatusOfReserveSpaceRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse srmReleaseSpace(
            gov.lbl.srm.v22.stubs.SrmReleaseSpaceRequest srmReleaseSpaceRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse srmUpdateSpace(
            gov.lbl.srm.v22.stubs.SrmUpdateSpaceRequest srmUpdateSpaceRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse srmStatusOfUpdateSpaceRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestRequest srmStatusOfUpdateSpaceRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse srmGetSpaceMetaData(
            gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataRequest srmGetSpaceMetaDataRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse srmChangeSpaceForFiles(
            gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesRequest srmChangeSpaceForFilesRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse srmStatusOfChangeSpaceForFilesRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestRequest srmStatusOfChangeSpaceForFilesRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse srmExtendFileLifeTimeInSpace(
            gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceRequest srmExtendFileLifeTimeInSpaceRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse srmPurgeFromSpace(
            gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceRequest srmPurgeFromSpaceRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse srmGetSpaceTokens(
            gov.lbl.srm.v22.stubs.SrmGetSpaceTokensRequest srmGetSpaceTokensRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmSetPermissionResponse srmSetPermission(
            gov.lbl.srm.v22.stubs.SrmSetPermissionRequest srmSetPermissionRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse srmCheckPermission(
            gov.lbl.srm.v22.stubs.SrmCheckPermissionRequest srmCheckPermissionRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetPermissionResponse srmGetPermission(
            gov.lbl.srm.v22.stubs.SrmGetPermissionRequest srmGetPermissionRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmMkdirResponse srmMkdir(gov.lbl.srm.v22.stubs.SrmMkdirRequest srmMkdirRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmRmdirResponse srmRmdir(gov.lbl.srm.v22.stubs.SrmRmdirRequest srmRmdirRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmRmResponse srmRm(gov.lbl.srm.v22.stubs.SrmRmRequest srmRmRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmLsResponse srmLs(gov.lbl.srm.v22.stubs.SrmLsRequest srmLsRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse srmStatusOfLsRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest srmStatusOfLsRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmMvResponse srmMv(gov.lbl.srm.v22.stubs.SrmMvRequest srmMvRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse srmPrepareToGet(
            gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest srmPrepareToGetRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse srmStatusOfGetRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest srmStatusOfGetRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmBringOnlineResponse srmBringOnline(
            gov.lbl.srm.v22.stubs.SrmBringOnlineRequest srmBringOnlineRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse srmStatusOfBringOnlineRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestRequest srmStatusOfBringOnlineRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse srmPrepareToPut(
            gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest srmPrepareToPutRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse srmStatusOfPutRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest srmStatusOfPutRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmCopyResponse srmCopy(gov.lbl.srm.v22.stubs.SrmCopyRequest srmCopyRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse srmStatusOfCopyRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest srmStatusOfCopyRequestRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse srmReleaseFiles(
            gov.lbl.srm.v22.stubs.SrmReleaseFilesRequest srmReleaseFilesRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmPutDoneResponse srmPutDone(gov.lbl.srm.v22.stubs.SrmPutDoneRequest srmPutDoneRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmAbortRequestResponse srmAbortRequest(
            gov.lbl.srm.v22.stubs.SrmAbortRequestRequest srmAbortRequestRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmAbortFilesResponse srmAbortFiles(
            gov.lbl.srm.v22.stubs.SrmAbortFilesRequest srmAbortFilesRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse srmSuspendRequest(
            gov.lbl.srm.v22.stubs.SrmSuspendRequestRequest srmSuspendRequestRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmResumeRequestResponse srmResumeRequest(
            gov.lbl.srm.v22.stubs.SrmResumeRequestRequest srmResumeRequestRequest) throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse srmGetRequestSummary(
            gov.lbl.srm.v22.stubs.SrmGetRequestSummaryRequest srmGetRequestSummaryRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse srmExtendFileLifeTime(
            gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeRequest srmExtendFileLifeTimeRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse srmGetRequestTokens(
            gov.lbl.srm.v22.stubs.SrmGetRequestTokensRequest srmGetRequestTokensRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse srmGetTransferProtocols(
            gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest srmGetTransferProtocolsRequest)
            throws java.rmi.RemoteException;

    public gov.lbl.srm.v22.stubs.SrmPingResponse srmPing(gov.lbl.srm.v22.stubs.SrmPingRequest srmPingRequest)
            throws java.rmi.RemoteException;
}
