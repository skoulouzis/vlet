/**
 * ISRM.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package srm.dcachev1.stubs;

public interface ISRM extends java.rmi.Remote
{
    public srm.dcachev1.stubs.RequestStatus put(java.lang.String[] arg0, java.lang.String[] arg1, long[] arg2,
            boolean[] arg3, java.lang.String[] arg4) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus get(java.lang.String[] arg0, java.lang.String[] arg1)
            throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus copy(java.lang.String[] arg0, java.lang.String[] arg1, boolean[] arg2)
            throws java.rmi.RemoteException;

    public boolean ping() throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus pin(java.lang.String[] arg0) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus unPin(java.lang.String[] arg0, int arg1) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus setFileStatus(int arg0, int arg1, java.lang.String arg2)
            throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus getRequestStatus(int arg0) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.FileMetaData[] getFileMetaData(java.lang.String[] arg0) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus mkPermanent(java.lang.String[] arg0) throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus getEstGetTime(java.lang.String[] arg0, java.lang.String[] arg1)
            throws java.rmi.RemoteException;

    public srm.dcachev1.stubs.RequestStatus getEstPutTime(java.lang.String[] arg0, java.lang.String[] arg1,
            long[] arg2, boolean[] arg3, java.lang.String[] arg4) throws java.rmi.RemoteException;

    public void advisoryDelete(java.lang.String[] arg0) throws java.rmi.RemoteException;

    public java.lang.String[] getProtocols() throws java.rmi.RemoteException;
}
