/**
 * SRMServerV1TestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package srm.dcachev1.stubs;

public class SRMServerV1TestCase extends junit.framework.TestCase
{
    public SRMServerV1TestCase(java.lang.String name)
    {
        super(name);
    }

    public void testISRMWSDL() throws Exception
    {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new srm.dcachev1.stubs.SRMServerV1Locator().getISRMAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new srm.dcachev1.stubs.SRMServerV1Locator()
                .getServiceName());
        assertTrue(service != null);
    }

    public void test1ISRMPut() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.put(new java.lang.String[0], new java.lang.String[0], new long[0], new boolean[0],
                new java.lang.String[0]);
        // TBD - validate results
    }

    public void test2ISRMGet() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.get(new java.lang.String[0], new java.lang.String[0]);
        // TBD - validate results
    }

    public void test3ISRMCopy() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.copy(new java.lang.String[0], new java.lang.String[0], new boolean[0]);
        // TBD - validate results
    }

    public void test4ISRMPing() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        boolean value = false;
        value = binding.ping();
        // TBD - validate results
    }

    public void test5ISRMPin() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.pin(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test6ISRMUnPin() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.unPin(new java.lang.String[0], 0);
        // TBD - validate results
    }

    public void test7ISRMSetFileStatus() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.setFileStatus(0, 0, new java.lang.String());
        // TBD - validate results
    }

    public void test8ISRMGetRequestStatus() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.getRequestStatus(0);
        // TBD - validate results
    }

    public void test9ISRMGetFileMetaData() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.FileMetaData[] value = null;
        value = binding.getFileMetaData(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test10ISRMMkPermanent() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.mkPermanent(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test11ISRMGetEstGetTime() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.getEstGetTime(new java.lang.String[0], new java.lang.String[0]);
        // TBD - validate results
    }

    public void test12ISRMGetEstPutTime() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        srm.dcachev1.stubs.RequestStatus value = null;
        value = binding.getEstPutTime(new java.lang.String[0], new java.lang.String[0], new long[0], new boolean[0],
                new java.lang.String[0]);
        // TBD - validate results
    }

    public void test13ISRMAdvisoryDelete() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        binding.advisoryDelete(new java.lang.String[0]);
        // TBD - validate results
    }

    public void test14ISRMGetProtocols() throws Exception
    {
        srm.dcachev1.stubs.ISRMStub binding;
        try
        {
            binding = (srm.dcachev1.stubs.ISRMStub) new srm.dcachev1.stubs.SRMServerV1Locator().getISRM();
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
        java.lang.String[] value = null;
        value = binding.getProtocols();
        // TBD - validate results
    }

}
