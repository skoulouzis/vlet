/**
 * SRMServerV1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package srm.dcachev1.stubs;

public interface SRMServerV1 extends javax.xml.rpc.Service
{

    /**
     * diskCacheV111.srm.server.SRMServerV1 web service
     */
    public java.lang.String getISRMAddress();

    public srm.dcachev1.stubs.ISRM getISRM() throws javax.xml.rpc.ServiceException;

    public srm.dcachev1.stubs.ISRM getISRM(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
