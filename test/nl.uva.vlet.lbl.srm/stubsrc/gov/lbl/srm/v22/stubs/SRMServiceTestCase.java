/**
 * SRMServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class SRMServiceTestCase extends junit.framework.TestCase
{
    public SRMServiceTestCase(java.lang.String name)
    {
        super(name);
    }

    public void testsrmWSDL() throws Exception
    {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrmAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new gov.lbl.srm.v22.stubs.SRMServiceLocator()
                .getServiceName());
        assertTrue(service != null);
    }

    /**
     * the following location of the service is specific to the particular
     * deployment and is not part of the specification
     */
    public void test1srmSrmReserveSpace() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmReserveSpaceResponse value = null;
        value = binding.srmReserveSpace(new gov.lbl.srm.v22.stubs.SrmReserveSpaceRequest());
        // TBD - validate results
    }

    public void test2srmSrmStatusOfReserveSpaceRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestResponse value = null;
        value = binding
                .srmStatusOfReserveSpaceRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfReserveSpaceRequestRequest());
        // TBD - validate results
    }

    public void test3srmSrmReleaseSpace() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmReleaseSpaceResponse value = null;
        value = binding.srmReleaseSpace(new gov.lbl.srm.v22.stubs.SrmReleaseSpaceRequest());
        // TBD - validate results
    }

    public void test4srmSrmUpdateSpace() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmUpdateSpaceResponse value = null;
        value = binding.srmUpdateSpace(new gov.lbl.srm.v22.stubs.SrmUpdateSpaceRequest());
        // TBD - validate results
    }

    public void test5srmSrmStatusOfUpdateSpaceRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestResponse value = null;
        value = binding.srmStatusOfUpdateSpaceRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfUpdateSpaceRequestRequest());
        // TBD - validate results
    }

    public void test6srmSrmGetSpaceMetaData() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataResponse value = null;
        value = binding.srmGetSpaceMetaData(new gov.lbl.srm.v22.stubs.SrmGetSpaceMetaDataRequest());
        // TBD - validate results
    }

    public void test7srmSrmChangeSpaceForFiles() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesResponse value = null;
        value = binding.srmChangeSpaceForFiles(new gov.lbl.srm.v22.stubs.SrmChangeSpaceForFilesRequest());
        // TBD - validate results
    }

    public void test8srmSrmStatusOfChangeSpaceForFilesRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestResponse value = null;
        value = binding
                .srmStatusOfChangeSpaceForFilesRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfChangeSpaceForFilesRequestRequest());
        // TBD - validate results
    }

    public void test9srmSrmExtendFileLifeTimeInSpace() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceResponse value = null;
        value = binding.srmExtendFileLifeTimeInSpace(new gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeInSpaceRequest());
        // TBD - validate results
    }

    public void test10srmSrmPurgeFromSpace() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceResponse value = null;
        value = binding.srmPurgeFromSpace(new gov.lbl.srm.v22.stubs.SrmPurgeFromSpaceRequest());
        // TBD - validate results
    }

    public void test11srmSrmGetSpaceTokens() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetSpaceTokensResponse value = null;
        value = binding.srmGetSpaceTokens(new gov.lbl.srm.v22.stubs.SrmGetSpaceTokensRequest());
        // TBD - validate results
    }

    public void test12srmSrmSetPermission() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmSetPermissionResponse value = null;
        value = binding.srmSetPermission(new gov.lbl.srm.v22.stubs.SrmSetPermissionRequest());
        // TBD - validate results
    }

    public void test13srmSrmCheckPermission() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmCheckPermissionResponse value = null;
        value = binding.srmCheckPermission(new gov.lbl.srm.v22.stubs.SrmCheckPermissionRequest());
        // TBD - validate results
    }

    public void test14srmSrmGetPermission() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetPermissionResponse value = null;
        value = binding.srmGetPermission(new gov.lbl.srm.v22.stubs.SrmGetPermissionRequest());
        // TBD - validate results
    }

    public void test15srmSrmMkdir() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmMkdirResponse value = null;
        value = binding.srmMkdir(new gov.lbl.srm.v22.stubs.SrmMkdirRequest());
        // TBD - validate results
    }

    public void test16srmSrmRmdir() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmRmdirResponse value = null;
        value = binding.srmRmdir(new gov.lbl.srm.v22.stubs.SrmRmdirRequest());
        // TBD - validate results
    }

    public void test17srmSrmRm() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmRmResponse value = null;
        value = binding.srmRm(new gov.lbl.srm.v22.stubs.SrmRmRequest());
        // TBD - validate results
    }

    public void test18srmSrmLs() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmLsResponse value = null;
        value = binding.srmLs(new gov.lbl.srm.v22.stubs.SrmLsRequest());
        // TBD - validate results
    }

    public void test19srmSrmStatusOfLsRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse value = null;
        value = binding.srmStatusOfLsRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestRequest());
        // TBD - validate results
    }

    public void test20srmSrmMv() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmMvResponse value = null;
        value = binding.srmMv(new gov.lbl.srm.v22.stubs.SrmMvRequest());
        // TBD - validate results
    }

    public void test21srmSrmPrepareToGet() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse value = null;
        value = binding.srmPrepareToGet(new gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest());
        // TBD - validate results
    }

    public void test22srmSrmStatusOfGetRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse value = null;
        value = binding.srmStatusOfGetRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest());
        // TBD - validate results
    }

    public void test23srmSrmBringOnline() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmBringOnlineResponse value = null;
        value = binding.srmBringOnline(new gov.lbl.srm.v22.stubs.SrmBringOnlineRequest());
        // TBD - validate results
    }

    public void test24srmSrmStatusOfBringOnlineRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestResponse value = null;
        value = binding.srmStatusOfBringOnlineRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfBringOnlineRequestRequest());
        // TBD - validate results
    }

    public void test25srmSrmPrepareToPut() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse value = null;
        value = binding.srmPrepareToPut(new gov.lbl.srm.v22.stubs.SrmPrepareToPutRequest());
        // TBD - validate results
    }

    public void test26srmSrmStatusOfPutRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse value = null;
        value = binding.srmStatusOfPutRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestRequest());
        // TBD - validate results
    }

    public void test27srmSrmCopy() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmCopyResponse value = null;
        value = binding.srmCopy(new gov.lbl.srm.v22.stubs.SrmCopyRequest());
        // TBD - validate results
    }

    public void test28srmSrmStatusOfCopyRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse value = null;
        value = binding.srmStatusOfCopyRequest(new gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest());
        // TBD - validate results
    }

    public void test29srmSrmReleaseFiles() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmReleaseFilesResponse value = null;
        value = binding.srmReleaseFiles(new gov.lbl.srm.v22.stubs.SrmReleaseFilesRequest());
        // TBD - validate results
    }

    public void test30srmSrmPutDone() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmPutDoneResponse value = null;
        value = binding.srmPutDone(new gov.lbl.srm.v22.stubs.SrmPutDoneRequest());
        // TBD - validate results
    }

    public void test31srmSrmAbortRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmAbortRequestResponse value = null;
        value = binding.srmAbortRequest(new gov.lbl.srm.v22.stubs.SrmAbortRequestRequest());
        // TBD - validate results
    }

    public void test32srmSrmAbortFiles() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmAbortFilesResponse value = null;
        value = binding.srmAbortFiles(new gov.lbl.srm.v22.stubs.SrmAbortFilesRequest());
        // TBD - validate results
    }

    public void test33srmSrmSuspendRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmSuspendRequestResponse value = null;
        value = binding.srmSuspendRequest(new gov.lbl.srm.v22.stubs.SrmSuspendRequestRequest());
        // TBD - validate results
    }

    public void test34srmSrmResumeRequest() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmResumeRequestResponse value = null;
        value = binding.srmResumeRequest(new gov.lbl.srm.v22.stubs.SrmResumeRequestRequest());
        // TBD - validate results
    }

    public void test35srmSrmGetRequestSummary() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetRequestSummaryResponse value = null;
        value = binding.srmGetRequestSummary(new gov.lbl.srm.v22.stubs.SrmGetRequestSummaryRequest());
        // TBD - validate results
    }

    public void test36srmSrmExtendFileLifeTime() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeResponse value = null;
        value = binding.srmExtendFileLifeTime(new gov.lbl.srm.v22.stubs.SrmExtendFileLifeTimeRequest());
        // TBD - validate results
    }

    public void test37srmSrmGetRequestTokens() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetRequestTokensResponse value = null;
        value = binding.srmGetRequestTokens(new gov.lbl.srm.v22.stubs.SrmGetRequestTokensRequest());
        // TBD - validate results
    }

    public void test38srmSrmGetTransferProtocols() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsResponse value = null;
        value = binding.srmGetTransferProtocols(new gov.lbl.srm.v22.stubs.SrmGetTransferProtocolsRequest());
        // TBD - validate results
    }

    public void test39srmSrmPing() throws Exception
    {
        gov.lbl.srm.v22.stubs.SrmSoapBindingStub binding;
        try
        {
            binding = (gov.lbl.srm.v22.stubs.SrmSoapBindingStub) new gov.lbl.srm.v22.stubs.SRMServiceLocator().getsrm();
        }
        catch (javax.xml.rpc.ServiceException jre)
        {
            if (jre.getLinkedCause() != null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        gov.lbl.srm.v22.stubs.SrmPingResponse value = null;
        value = binding.srmPing(new gov.lbl.srm.v22.stubs.SrmPingRequest());
        // TBD - validate results
    }

}
