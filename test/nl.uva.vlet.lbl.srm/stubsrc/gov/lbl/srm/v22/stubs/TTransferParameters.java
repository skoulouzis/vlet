/**
 * TTransferParameters.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class TTransferParameters implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TAccessPattern accessPattern;

    private gov.lbl.srm.v22.stubs.TConnectionType connectionType;

    private gov.lbl.srm.v22.stubs.ArrayOfString arrayOfClientNetworks;

    private gov.lbl.srm.v22.stubs.ArrayOfString arrayOfTransferProtocols;

    public TTransferParameters()
    {
    }

    public TTransferParameters(gov.lbl.srm.v22.stubs.TAccessPattern accessPattern,
            gov.lbl.srm.v22.stubs.TConnectionType connectionType,
            gov.lbl.srm.v22.stubs.ArrayOfString arrayOfClientNetworks,
            gov.lbl.srm.v22.stubs.ArrayOfString arrayOfTransferProtocols)
    {
        this.accessPattern = accessPattern;
        this.connectionType = connectionType;
        this.arrayOfClientNetworks = arrayOfClientNetworks;
        this.arrayOfTransferProtocols = arrayOfTransferProtocols;
    }

    /**
     * Gets the accessPattern value for this TTransferParameters.
     * 
     * @return accessPattern
     */
    public gov.lbl.srm.v22.stubs.TAccessPattern getAccessPattern()
    {
        return accessPattern;
    }

    /**
     * Sets the accessPattern value for this TTransferParameters.
     * 
     * @param accessPattern
     */
    public void setAccessPattern(gov.lbl.srm.v22.stubs.TAccessPattern accessPattern)
    {
        this.accessPattern = accessPattern;
    }

    /**
     * Gets the connectionType value for this TTransferParameters.
     * 
     * @return connectionType
     */
    public gov.lbl.srm.v22.stubs.TConnectionType getConnectionType()
    {
        return connectionType;
    }

    /**
     * Sets the connectionType value for this TTransferParameters.
     * 
     * @param connectionType
     */
    public void setConnectionType(gov.lbl.srm.v22.stubs.TConnectionType connectionType)
    {
        this.connectionType = connectionType;
    }

    /**
     * Gets the arrayOfClientNetworks value for this TTransferParameters.
     * 
     * @return arrayOfClientNetworks
     */
    public gov.lbl.srm.v22.stubs.ArrayOfString getArrayOfClientNetworks()
    {
        return arrayOfClientNetworks;
    }

    /**
     * Sets the arrayOfClientNetworks value for this TTransferParameters.
     * 
     * @param arrayOfClientNetworks
     */
    public void setArrayOfClientNetworks(gov.lbl.srm.v22.stubs.ArrayOfString arrayOfClientNetworks)
    {
        this.arrayOfClientNetworks = arrayOfClientNetworks;
    }

    /**
     * Gets the arrayOfTransferProtocols value for this TTransferParameters.
     * 
     * @return arrayOfTransferProtocols
     */
    public gov.lbl.srm.v22.stubs.ArrayOfString getArrayOfTransferProtocols()
    {
        return arrayOfTransferProtocols;
    }

    /**
     * Sets the arrayOfTransferProtocols value for this TTransferParameters.
     * 
     * @param arrayOfTransferProtocols
     */
    public void setArrayOfTransferProtocols(gov.lbl.srm.v22.stubs.ArrayOfString arrayOfTransferProtocols)
    {
        this.arrayOfTransferProtocols = arrayOfTransferProtocols;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof TTransferParameters))
            return false;
        TTransferParameters other = (TTransferParameters) obj;
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
                && ((this.accessPattern == null && other.getAccessPattern() == null) || (this.accessPattern != null && this.accessPattern
                        .equals(other.getAccessPattern())))
                && ((this.connectionType == null && other.getConnectionType() == null) || (this.connectionType != null && this.connectionType
                        .equals(other.getConnectionType())))
                && ((this.arrayOfClientNetworks == null && other.getArrayOfClientNetworks() == null) || (this.arrayOfClientNetworks != null && this.arrayOfClientNetworks
                        .equals(other.getArrayOfClientNetworks())))
                && ((this.arrayOfTransferProtocols == null && other.getArrayOfTransferProtocols() == null) || (this.arrayOfTransferProtocols != null && this.arrayOfTransferProtocols
                        .equals(other.getArrayOfTransferProtocols())));
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
        if (getAccessPattern() != null)
        {
            _hashCode += getAccessPattern().hashCode();
        }
        if (getConnectionType() != null)
        {
            _hashCode += getConnectionType().hashCode();
        }
        if (getArrayOfClientNetworks() != null)
        {
            _hashCode += getArrayOfClientNetworks().hashCode();
        }
        if (getArrayOfTransferProtocols() != null)
        {
            _hashCode += getArrayOfTransferProtocols().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            TTransferParameters.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TTransferParameters"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessPattern");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accessPattern"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TAccessPattern"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectionType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "connectionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TConnectionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrayOfClientNetworks");
        elemField.setXmlName(new javax.xml.namespace.QName("", "arrayOfClientNetworks"));
        elemField
                .setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfString"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrayOfTransferProtocols");
        elemField.setXmlName(new javax.xml.namespace.QName("", "arrayOfTransferProtocols"));
        elemField
                .setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfString"));
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
