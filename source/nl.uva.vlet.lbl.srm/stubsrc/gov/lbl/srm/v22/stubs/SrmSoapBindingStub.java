/**
 * SrmSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class SrmSoapBindingStub extends org.apache.axis.client.Stub implements gov.lbl.srm.v22.stubs.ISRM
{
    private java.util.Vector cachedSerClasses = new java.util.Vector();

    private java.util.Vector cachedSerQNames = new java.util.Vector();

    private java.util.Vector cachedSerFactories = new java.util.Vector();

    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc[] _operations;

    static
    {
        _operations = new org.apache.axis.description.OperationDesc[39];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
    }

    private static void _initOperationDesc1()
    {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmReserveSpace");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmReserveSpaceRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmReserveSpaceRequest"),
                gov.lbl.srm.v22.stubs.SrmReserveSpaceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReserveSpaceResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmReserveSpaceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfReserveSpaceRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfReserveSpaceRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfReserveSpaceRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfReserveSpaceRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfReserveSpaceRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmReleaseSpace");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmReleaseSpaceRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmReleaseSpaceRequest"),
                gov.lbl.srm.v22.stubs.SrmReleaseSpaceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReleaseSpaceResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmReleaseSpaceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmUpdateSpace");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmUpdateSpaceRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmUpdateSpaceRequest"),
                gov.lbl.srm.v22.stubs.SrmUpdateSpaceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmUpdateSpaceResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmUpdateSpaceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfUpdateSpaceRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfUpdateSpaceRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfUpdateSpaceRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfUpdateSpaceRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfUpdateSpaceRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetSpaceMetaData");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmGetSpaceMetaDataRequest"),
                org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetSpaceMetaDataRequest"),
                gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetSpaceMetaDataResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetSpaceMetaDataResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmChangeSpaceForFiles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmChangeSpaceForFilesRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmChangeSpaceForFilesRequest"), gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmChangeSpaceForFilesResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmChangeSpaceForFilesResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfChangeSpaceForFilesRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfChangeSpaceForFilesRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfChangeSpaceForFilesRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfChangeSpaceForFilesRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfChangeSpaceForFilesRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmExtendFileLifeTimeInSpace");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmExtendFileLifeTimeInSpaceRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmExtendFileLifeTimeInSpaceRequest"),
                gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeInSpaceResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmExtendFileLifeTimeInSpaceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmPurgeFromSpace");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmPurgeFromSpaceRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPurgeFromSpaceRequest"),
                gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPurgeFromSpaceResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmPurgeFromSpaceResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2()
    {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetSpaceTokens");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmGetSpaceTokensRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetSpaceTokensRequest"),
                gov.lbl.srm.v22.stubs.SrmGetSpaceTokensRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetSpaceTokensResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetSpaceTokensResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmSetPermission");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmSetPermissionRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSetPermissionRequest"),
                gov.lbl.srm.v22.stubs.SrmSetPermissionRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmSetPermissionResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmSetPermissionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmSetPermissionResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmCheckPermission");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmCheckPermissionRequest"),
                org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCheckPermissionRequest"),
                gov.lbl.srm.v22.stubs.SrmCheckPermissionRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmCheckPermissionResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmCheckPermissionResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetPermission");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmGetPermissionRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetPermissionRequest"),
                gov.lbl.srm.v22.stubs.SrmGetPermissionRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetPermissionResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetPermissionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetPermissionResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmMkdir");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmMkdirRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmMkdirRequest"),
                gov.lbl.srm.v22.stubs.SrmMkdirRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmMkdirResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmMkdirResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmMkdirResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmRmdir");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmRmdirRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmRmdirRequest"),
                gov.lbl.srm.v22.stubs.SrmRmdirRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmRmdirResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmRmdirResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmRmdirResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmRm");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmRmRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmRmRequest"),
                gov.lbl.srm.v22.stubs.SrmRmRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmRmResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmRmResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmLs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmLsRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmLsRequest"),
                gov.lbl.srm.v22.stubs.SrmLsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmLsResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmLsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmLsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfLsRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfLsRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfLsRequestRequest"), gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest.class, false,
                false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfLsRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfLsRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmMv");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmMvRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmMvRequest"),
                gov.lbl.srm.v22.stubs.SrmMvRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMvResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmMvResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmMvResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3()
    {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmPrepareToGet");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmPrepareToGetRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmPrepareToGetRequest"),
                gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPrepareToGetResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmPrepareToGetResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfGetRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfGetRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfGetRequestRequest"), gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfGetRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfGetRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmBringOnline");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmBringOnlineRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmBringOnlineRequest"),
                gov.lbl.srm.v22.stubs.SrmBringOnlineRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmBringOnlineResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmBringOnlineResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmBringOnlineResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfBringOnlineRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfBringOnlineRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfBringOnlineRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfBringOnlineRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfBringOnlineRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmPrepareToPut");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmPrepareToPutRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmPrepareToPutRequest"),
                gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPrepareToPutResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmPrepareToPutResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfPutRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfPutRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfPutRequestRequest"), gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfPutRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfPutRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmCopy");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmCopyRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmCopyRequest"),
                gov.lbl.srm.v22.stubs.SrmCopyRequest.class, false, false);
        oper.addParameter(param);
        oper
                .setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmCopyResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmCopyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmCopyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmStatusOfCopyRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmStatusOfCopyRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmStatusOfCopyRequestRequest"), gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfCopyRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmStatusOfCopyRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmReleaseFiles");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmReleaseFilesRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmReleaseFilesRequest"),
                gov.lbl.srm.v22.stubs.SrmReleaseFilesRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReleaseFilesResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmReleaseFilesResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmPutDone");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmPutDoneRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmPutDoneRequest"),
                gov.lbl.srm.v22.stubs.SrmPutDoneRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPutDoneResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmPutDoneResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmPutDoneResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4()
    {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmAbortRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmAbortRequestRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                "http://srm.lbl.gov/StorageResourceManager", "srmAbortRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmAbortRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmAbortRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmAbortRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmAbortRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmAbortFiles");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmAbortFilesRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmAbortFilesRequest"),
                gov.lbl.srm.v22.stubs.SrmAbortFilesRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmAbortFilesResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmAbortFilesResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmAbortFilesResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmSuspendRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmSuspendRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSuspendRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmSuspendRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmSuspendRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmSuspendRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmResumeRequest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmResumeRequestRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmResumeRequestRequest"),
                gov.lbl.srm.v22.stubs.SrmResumeRequestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmResumeRequestResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmResumeRequestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmResumeRequestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[33] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetRequestSummary");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmGetRequestSummaryRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmGetRequestSummaryRequest"), gov.lbl.srm.v22.stubs.SrmGetRequestSummaryRequest.class, false,
                false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestSummaryResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetRequestSummaryResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[34] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmExtendFileLifeTime");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmExtendFileLifeTimeRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmExtendFileLifeTimeRequest"), gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmExtendFileLifeTimeResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[35] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetRequestTokens");
        param = new org.apache.axis.description.ParameterDesc(
                new javax.xml.namespace.QName("", "srmGetRequestTokensRequest"),
                org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetRequestTokensRequest"),
                gov.lbl.srm.v22.stubs.SrmGetRequestTokensRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestTokensResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetRequestTokensResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[36] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmGetTransferProtocols");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("",
                "srmGetTransferProtocolsRequest"), org.apache.axis.description.ParameterDesc.IN,
                new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmGetTransferProtocolsRequest"), gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest.class,
                false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetTransferProtocolsResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmGetTransferProtocolsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[37] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("srmPing");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "srmPingRequest"),
                org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
                        "http://srm.lbl.gov/StorageResourceManager", "srmPingRequest"),
                gov.lbl.srm.v22.stubs.SrmPingRequest.class, false, false);
        oper.addParameter(param);
        oper
                .setReturnType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmPingResponse"));
        oper.setReturnClass(gov.lbl.srm.v22.stubs.SrmPingResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "srmPingResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[38] = oper;

    }

    public SrmSoapBindingStub() throws org.apache.axis.AxisFault
    {
        this(null);
    }

    public SrmSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault
    {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public SrmSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault
    {
        if (service == null)
        {
            super.service = new org.apache.axis.client.Service();
        }
        else
        {
            super.service = service;
        }
        ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    private void addBindings0()
    {
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfAnyURI");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfAnyURI.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfString");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfString.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTBringOnlineRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTBringOnlineRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTCopyFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTCopyFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTCopyRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTCopyRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTExtraInfo");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTGetFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTGetFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTGetRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTGetRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTGroupPermission");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTGroupPermission.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTMetaDataPathDetail");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTMetaDataPathDetail.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTMetaDataSpace");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTMetaDataSpace.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTPermissionReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTPermissionReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTPutFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTPutFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTPutRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTPutRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTRequestSummary");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTRequestSummary.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTRequestTokenReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTRequestTokenReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTSupportedTransferProtocol");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTSupportedTransferProtocol.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTSURLLifetimeReturnStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTSURLLifetimeReturnStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTSURLPermissionReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTSURLPermissionReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTSURLReturnStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTSURLReturnStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfTUserPermission");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfTUserPermission.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfUnsignedLong");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.ArrayOfUnsignedLong.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmAbortFilesRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmAbortFilesRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmAbortFilesResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmAbortFilesResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmAbortRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmAbortRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmAbortRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmAbortRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmBringOnlineRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmBringOnlineRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmBringOnlineResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmBringOnlineResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmChangeSpaceForFilesRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmChangeSpaceForFilesResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCheckPermissionRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmCheckPermissionRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCheckPermissionResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCopyRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmCopyRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCopyResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmCopyResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeInSpaceRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeInSpaceResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetPermissionRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetPermissionRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetPermissionResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetPermissionResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestSummaryRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetRequestSummaryRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestSummaryResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetRequestTokensRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetRequestTokensRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestTokensResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetSpaceMetaDataRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetSpaceMetaDataResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetSpaceTokensRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetSpaceTokensRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmGetSpaceTokensResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetTransferProtocolsRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetTransferProtocolsResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmLsRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmLsRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmLsResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmLsResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMkdirRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmMkdirRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMkdirResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmMkdirResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMvRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmMvRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMvResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmMvResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPingRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPingRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPingResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPingResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPrepareToGetRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPrepareToGetResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPrepareToPutRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPrepareToPutResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPurgeFromSpaceRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPurgeFromSpaceResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPutDoneRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPutDoneRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPutDoneResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmPutDoneResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReleaseFilesRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReleaseFilesRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReleaseFilesResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReleaseSpaceRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReleaseSpaceRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReleaseSpaceResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReserveSpaceRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReserveSpaceRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmReserveSpaceResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmResumeRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmResumeRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmResumeRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmResumeRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmdirRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmRmdirRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmdirResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmRmdirResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmRmRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmRmResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSetPermissionRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmSetPermissionRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSetPermissionResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmSetPermissionResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfBringOnlineRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfBringOnlineRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfChangeSpaceForFilesRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfChangeSpaceForFilesRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfCopyRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfCopyRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfGetRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfGetRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfLsRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfLsRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfPutRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfPutRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfReserveSpaceRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfReserveSpaceRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfUpdateSpaceRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfUpdateSpaceRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSuspendRequestRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmSuspendRequestRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmSuspendRequestResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmUpdateSpaceRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmUpdateSpaceRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmUpdateSpaceResponse");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

    }

    private void addBindings1()
    {
        java.lang.Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TAccessLatency");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TAccessLatency.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TAccessPattern");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TAccessPattern.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TBringOnlineRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TBringOnlineRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TConnectionType");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TConnectionType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TCopyFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TCopyFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TCopyRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TCopyRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TDirOption");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TDirOption.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TExtraInfo");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TExtraInfo.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TFileLocality");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TFileLocality.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TFileStorageType");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TFileStorageType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TFileType");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TFileType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TGetFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TGetFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TGetRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TGetRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TGroupPermission");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TGroupPermission.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TMetaDataPathDetail");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TMetaDataPathDetail.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TMetaDataSpace");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TMetaDataSpace.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TOverwriteMode");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TOverwriteMode.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TPermissionMode");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TPermissionMode.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TPermissionReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TPermissionReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TPermissionType");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TPermissionType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TPutFileRequest");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TPutFileRequest.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TPutRequestFileStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TPutRequestFileStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TRequestSummary");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TRequestSummary.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TRequestTokenReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TRequestTokenReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TRequestType");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TRequestType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TRetentionPolicy");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TRetentionPolicy.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TRetentionPolicyInfo");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TRetentionPolicyInfo.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TReturnStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TReturnStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TStatusCode");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TStatusCode.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TSupportedTransferProtocol");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TSupportedTransferProtocol.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TSURLLifetimeReturnStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TSURLLifetimeReturnStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TSURLPermissionReturn");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TSURLPermissionReturn.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TSURLReturnStatus");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TSURLReturnStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TTransferParameters");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TTransferParameters.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TUserPermission");
        cachedSerQNames.add(qName);
        cls = gov.lbl.srm.v22.stubs.TUserPermission.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException
    {
        try
        {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet)
            {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null)
            {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null)
            {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null)
            {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null)
            {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null)
            {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements())
            {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this)
            {
                if (firstCall())
                {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i)
                    {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class)
                        {
                            java.lang.Class sf = (java.lang.Class) cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class) cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory)
                        {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
                                    .get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
                                    .get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Exception _t)
        {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse srmReserveSpace(
            gov.lbl.srm.v22.stubs.SrmReserveSpaceRequest srmReserveSpaceRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReserveSpace"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmReserveSpaceRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse srmStatusOfReserveSpaceRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestRequest srmStatusOfReserveSpaceRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfReserveSpaceRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfReserveSpaceRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse srmReleaseSpace(
            gov.lbl.srm.v22.stubs.SrmReleaseSpaceRequest srmReleaseSpaceRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReleaseSpace"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmReleaseSpaceRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse srmUpdateSpace(
            gov.lbl.srm.v22.stubs.SrmUpdateSpaceRequest srmUpdateSpaceRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmUpdateSpace"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmUpdateSpaceRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse srmStatusOfUpdateSpaceRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestRequest srmStatusOfUpdateSpaceRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfUpdateSpaceRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfUpdateSpaceRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse srmGetSpaceMetaData(
            gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataRequest srmGetSpaceMetaDataRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetSpaceMetaData"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetSpaceMetaDataRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse srmChangeSpaceForFiles(
            gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesRequest srmChangeSpaceForFilesRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmChangeSpaceForFiles"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmChangeSpaceForFilesRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse srmStatusOfChangeSpaceForFilesRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestRequest srmStatusOfChangeSpaceForFilesRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfChangeSpaceForFilesRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call
                    .invoke(new java.lang.Object[] { srmStatusOfChangeSpaceForFilesRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse srmExtendFileLifeTimeInSpace(
            gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceRequest srmExtendFileLifeTimeInSpaceRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTimeInSpace"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmExtendFileLifeTimeInSpaceRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse srmPurgeFromSpace(
            gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceRequest srmPurgeFromSpaceRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPurgeFromSpace"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmPurgeFromSpaceRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse srmGetSpaceTokens(
            gov.lbl.srm.v22.stubs.SrmGetSpaceTokensRequest srmGetSpaceTokensRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetSpaceTokens"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetSpaceTokensRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmSetPermissionResponse srmSetPermission(
            gov.lbl.srm.v22.stubs.SrmSetPermissionRequest srmSetPermissionRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmSetPermission"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmSetPermissionRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmSetPermissionResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmSetPermissionResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmSetPermissionResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse srmCheckPermission(
            gov.lbl.srm.v22.stubs.SrmCheckPermissionRequest srmCheckPermissionRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmCheckPermission"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmCheckPermissionRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetPermissionResponse srmGetPermission(
            gov.lbl.srm.v22.stubs.SrmGetPermissionRequest srmGetPermissionRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetPermission"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetPermissionRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetPermissionResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetPermissionResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmGetPermissionResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmMkdirResponse srmMkdir(gov.lbl.srm.v22.stubs.SrmMkdirRequest srmMkdirRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMkdir"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmMkdirRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmMkdirResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmMkdirResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmMkdirResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmRmdirResponse srmRmdir(gov.lbl.srm.v22.stubs.SrmRmdirRequest srmRmdirRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmdir"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmRmdirRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmRmdirResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmRmdirResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmRmdirResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmRmResponse srmRm(gov.lbl.srm.v22.stubs.SrmRmRequest srmRmRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRm"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmRmRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmRmResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmRmResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmRmResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmLsResponse srmLs(gov.lbl.srm.v22.stubs.SrmLsRequest srmLsRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmLs"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmLsRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmLsResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmLsResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmLsResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse srmStatusOfLsRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest srmStatusOfLsRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfLsRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfLsRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmMvResponse srmMv(gov.lbl.srm.v22.stubs.SrmMvRequest srmMvRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmMv"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmMvRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmMvResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmMvResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmMvResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse srmPrepareToGet(
            gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest srmPrepareToGetRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPrepareToGet"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmPrepareToGetRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse srmStatusOfGetRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest srmStatusOfGetRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfGetRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfGetRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmBringOnlineResponse srmBringOnline(
            gov.lbl.srm.v22.stubs.SrmBringOnlineRequest srmBringOnlineRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmBringOnline"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmBringOnlineRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmBringOnlineResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmBringOnlineResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmBringOnlineResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse srmStatusOfBringOnlineRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestRequest srmStatusOfBringOnlineRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfBringOnlineRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfBringOnlineRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse srmPrepareToPut(
            gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest srmPrepareToPutRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmPrepareToPut"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmPrepareToPutRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse srmStatusOfPutRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest srmStatusOfPutRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfPutRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfPutRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmCopyResponse srmCopy(gov.lbl.srm.v22.stubs.SrmCopyRequest srmCopyRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmCopy"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmCopyRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmCopyResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmCopyResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmCopyResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse srmStatusOfCopyRequest(
            gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest srmStatusOfCopyRequestRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmStatusOfCopyRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmStatusOfCopyRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse srmReleaseFiles(
            gov.lbl.srm.v22.stubs.SrmReleaseFilesRequest srmReleaseFilesRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmReleaseFiles"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmReleaseFilesRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmPutDoneResponse srmPutDone(gov.lbl.srm.v22.stubs.SrmPutDoneRequest srmPutDoneRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call
                .setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                        "srmPutDone"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmPutDoneRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmPutDoneResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmPutDoneResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmPutDoneResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmAbortRequestResponse srmAbortRequest(
            gov.lbl.srm.v22.stubs.SrmAbortRequestRequest srmAbortRequestRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmAbortRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmAbortRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmAbortRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmAbortRequestResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmAbortRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmAbortFilesResponse srmAbortFiles(
            gov.lbl.srm.v22.stubs.SrmAbortFilesRequest srmAbortFilesRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmAbortFiles"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmAbortFilesRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmAbortFilesResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmAbortFilesResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmAbortFilesResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse srmSuspendRequest(
            gov.lbl.srm.v22.stubs.SrmSuspendRequestRequest srmSuspendRequestRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmSuspendRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmSuspendRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmResumeRequestResponse srmResumeRequest(
            gov.lbl.srm.v22.stubs.SrmResumeRequestRequest srmResumeRequestRequest) throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[33]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmResumeRequest"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmResumeRequestRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmResumeRequestResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmResumeRequestResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmResumeRequestResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse srmGetRequestSummary(
            gov.lbl.srm.v22.stubs.SrmGetRequestSummaryRequest srmGetRequestSummaryRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[34]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestSummary"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetRequestSummaryRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse srmExtendFileLifeTime(
            gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeRequest srmExtendFileLifeTimeRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[35]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmExtendFileLifeTime"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmExtendFileLifeTimeRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse srmGetRequestTokens(
            gov.lbl.srm.v22.stubs.SrmGetRequestTokensRequest srmGetRequestTokensRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[36]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetRequestTokens"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetRequestTokensRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse) org.apache.axis.utils.JavaUtils.convert(
                            _resp, gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse srmGetTransferProtocols(
            gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest srmGetTransferProtocolsRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[37]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "srmGetTransferProtocols"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmGetTransferProtocolsRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse) org.apache.axis.utils.JavaUtils
                            .convert(_resp, gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

    public gov.lbl.srm.v22.stubs.SrmPingResponse srmPing(gov.lbl.srm.v22.stubs.SrmPingRequest srmPingRequest)
            throws java.rmi.RemoteException
    {
        if (super.cachedEndpoint == null)
        {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[38]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmPing"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try
        {
            java.lang.Object _resp = _call.invoke(new java.lang.Object[] { srmPingRequest });

            if (_resp instanceof java.rmi.RemoteException)
            {
                throw (java.rmi.RemoteException) _resp;
            }
            else
            {
                extractAttachments(_call);
                try
                {
                    return (gov.lbl.srm.v22.stubs.SrmPingResponse) _resp;
                }
                catch (java.lang.Exception _exception)
                {
                    return (gov.lbl.srm.v22.stubs.SrmPingResponse) org.apache.axis.utils.JavaUtils.convert(_resp,
                            gov.lbl.srm.v22.stubs.SrmPingResponse.class);
                }
            }
        }
        catch (org.apache.axis.AxisFault axisFaultException)
        {
            throw axisFaultException;
        }
    }

}
