/**
 * ArrayOfTMetaDataSpace.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class ArrayOfTMetaDataSpace implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TMetaDataSpace[] spaceDataArray;

    public ArrayOfTMetaDataSpace()
    {
    }

    public ArrayOfTMetaDataSpace(gov.lbl.srm.v22.stubs.TMetaDataSpace[] spaceDataArray)
    {
        this.spaceDataArray = spaceDataArray;
    }

    /**
     * Gets the spaceDataArray value for this ArrayOfTMetaDataSpace.
     * 
     * @return spaceDataArray
     */
    public gov.lbl.srm.v22.stubs.TMetaDataSpace[] getSpaceDataArray()
    {
        return spaceDataArray;
    }

    /**
     * Sets the spaceDataArray value for this ArrayOfTMetaDataSpace.
     * 
     * @param spaceDataArray
     */
    public void setSpaceDataArray(gov.lbl.srm.v22.stubs.TMetaDataSpace[] spaceDataArray)
    {
        this.spaceDataArray = spaceDataArray;
    }

    public gov.lbl.srm.v22.stubs.TMetaDataSpace getSpaceDataArray(int i)
    {
        return this.spaceDataArray[i];
    }

    public void setSpaceDataArray(int i, gov.lbl.srm.v22.stubs.TMetaDataSpace _value)
    {
        this.spaceDataArray[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof ArrayOfTMetaDataSpace))
            return false;
        ArrayOfTMetaDataSpace other = (ArrayOfTMetaDataSpace) obj;
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
        _equals = true && ((this.spaceDataArray == null && other.getSpaceDataArray() == null) || (this.spaceDataArray != null && java.util.Arrays
                .equals(this.spaceDataArray, other.getSpaceDataArray())));
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
        if (getSpaceDataArray() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getSpaceDataArray()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getSpaceDataArray(), i);
                if (obj != null && !obj.getClass().isArray())
                {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            ArrayOfTMetaDataSpace.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTMetaDataSpace"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("spaceDataArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "spaceDataArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TMetaDataSpace"));
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
