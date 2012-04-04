/**
 * SRMService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public interface SRMService extends javax.xml.rpc.Service
{
    public java.lang.String getsrmAddress();

    public gov.lbl.srm.v22.stubs.ISRM getsrm() throws javax.xml.rpc.ServiceException;

    public gov.lbl.srm.v22.stubs.ISRM getsrm(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
