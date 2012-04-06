/**
 * SrmRmRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class SrmRmRequest implements java.io.Serializable
{
    private java.lang.String authorizationID;

    private gov.lbl.srm.v22.stubs.ArrayOfAnyURI arrayOfSURLs;

    private gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo storageSystemInfo;

    public SrmRmRequest()
    {
    }

    public SrmRmRequest(java.lang.String authorizationID, gov.lbl.srm.v22.stubs.ArrayOfAnyURI arrayOfSURLs,
            gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo storageSystemInfo)
    {
        this.authorizationID = authorizationID;
        this.arrayOfSURLs = arrayOfSURLs;
        this.storageSystemInfo = storageSystemInfo;
    }

    /**
     * Gets the authorizationID value for this SrmRmRequest.
     * 
     * @return authorizationID
     */
    public java.lang.String getAuthorizationID()
    {
        return authorizationID;
    }

    /**
     * Sets the authorizationID value for this SrmRmRequest.
     * 
     * @param authorizationID
     */
    public void setAuthorizationID(java.lang.String authorizationID)
    {
        this.authorizationID = authorizationID;
    }

    /**
     * Gets the arrayOfSURLs value for this SrmRmRequest.
     * 
     * @return arrayOfSURLs
     */
    public gov.lbl.srm.v22.stubs.ArrayOfAnyURI getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    /**
     * Sets the arrayOfSURLs value for this SrmRmRequest.
     * 
     * @param arrayOfSURLs
     */
    public void setArrayOfSURLs(gov.lbl.srm.v22.stubs.ArrayOfAnyURI arrayOfSURLs)
    {
        this.arrayOfSURLs = arrayOfSURLs;
    }

    /**
     * Gets the storageSystemInfo value for this SrmRmRequest.
     * 
     * @return storageSystemInfo
     */
    public gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    /**
     * Sets the storageSystemInfo value for this SrmRmRequest.
     * 
     * @param storageSystemInfo
     */
    public void setStorageSystemInfo(gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo storageSystemInfo)
    {
        this.storageSystemInfo = storageSystemInfo;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof SrmRmRequest))
            return false;
        SrmRmRequest other = (SrmRmRequest) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null)
        {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true
                && ((this.authorizationID == null && other.getAuthorizationID() == null) || (this.authorizationID != null && this.authorizationID
                        .equals(other.getAuthorizationID())))
                && ((this.arrayOfSURLs == null && other.getArrayOfSURLs() == null) || (this.arrayOfSURLs != null && this.arrayOfSURLs
                        .equals(other.getArrayOfSURLs())))
                && ((this.storageSystemInfo == null && other.getStorageSystemInfo() == null) || (this.storageSystemInfo != null && this.storageSystemInfo
                        .equals(other.getStorageSystemInfo())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode()
    {
        if (__hashCodeCalc)
        {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAuthorizationID() != null)
        {
            _hashCode += getAuthorizationID().hashCode();
        }
        if (getArrayOfSURLs() != null)
        {
            _hashCode += getArrayOfSURLs().hashCode();
        }
        if (getStorageSystemInfo() != null)
        {
            _hashCode += getStorageSystemInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            SrmRmRequest.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "srmRmRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authorizationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrayOfSURLs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "arrayOfSURLs"));
        elemField
                .setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfAnyURI"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storageSystemInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "storageSystemInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTExtraInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc()
    {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
            java.lang.Class _javaType, javax.xml.namespace.QName _xmlType)
    {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
            java.lang.Class _javaType, javax.xml.namespace.QName _xmlType)
    {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
