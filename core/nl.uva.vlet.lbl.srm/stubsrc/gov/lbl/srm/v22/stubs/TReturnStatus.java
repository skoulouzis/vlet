/**
 * TReturnStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class TReturnStatus implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TStatusCode statusCode;

    private java.lang.String explanation;

    public TReturnStatus()
    {
    }

    public TReturnStatus(gov.lbl.srm.v22.stubs.TStatusCode statusCode, java.lang.String explanation)
    {
        this.statusCode = statusCode;
        this.explanation = explanation;
    }

    /**
     * Gets the statusCode value for this TReturnStatus.
     * 
     * @return statusCode
     */
    public gov.lbl.srm.v22.stubs.TStatusCode getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets the statusCode value for this TReturnStatus.
     * 
     * @param statusCode
     */
    public void setStatusCode(gov.lbl.srm.v22.stubs.TStatusCode statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * Gets the explanation value for this TReturnStatus.
     * 
     * @return explanation
     */
    public java.lang.String getExplanation()
    {
        return explanation;
    }

    /**
     * Sets the explanation value for this TReturnStatus.
     * 
     * @param explanation
     */
    public void setExplanation(java.lang.String explanation)
    {
        this.explanation = explanation;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof TReturnStatus))
            return false;
        TReturnStatus other = (TReturnStatus) obj;
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
                && ((this.statusCode == null && other.getStatusCode() == null) || (this.statusCode != null && this.statusCode
                        .equals(other.getStatusCode())))
                && ((this.explanation == null && other.getExplanation() == null) || (this.explanation != null && this.explanation
                        .equals(other.getExplanation())));
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
        if (getStatusCode() != null)
        {
            _hashCode += getStatusCode().hashCode();
        }
        if (getExplanation() != null)
        {
            _hashCode += getExplanation().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            TReturnStatus.class, true);

    static
    {
        typeDesc
                .setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TReturnStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statusCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "statusCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "TStatusCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("explanation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "explanation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
