/**
 * ArrayOfTMetaDataPathDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class ArrayOfTMetaDataPathDetail implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TMetaDataPathDetail[] pathDetailArray;

    public ArrayOfTMetaDataPathDetail()
    {
    }

    public ArrayOfTMetaDataPathDetail(gov.lbl.srm.v22.stubs.TMetaDataPathDetail[] pathDetailArray)
    {
        this.pathDetailArray = pathDetailArray;
    }

    /**
     * Gets the pathDetailArray value for this ArrayOfTMetaDataPathDetail.
     * 
     * @return pathDetailArray
     */
    public gov.lbl.srm.v22.stubs.TMetaDataPathDetail[] getPathDetailArray()
    {
        return pathDetailArray;
    }

    /**
     * Sets the pathDetailArray value for this ArrayOfTMetaDataPathDetail.
     * 
     * @param pathDetailArray
     */
    public void setPathDetailArray(gov.lbl.srm.v22.stubs.TMetaDataPathDetail[] pathDetailArray)
    {
        this.pathDetailArray = pathDetailArray;
    }

    public gov.lbl.srm.v22.stubs.TMetaDataPathDetail getPathDetailArray(int i)
    {
        return this.pathDetailArray[i];
    }

    public void setPathDetailArray(int i, gov.lbl.srm.v22.stubs.TMetaDataPathDetail _value)
    {
        this.pathDetailArray[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof ArrayOfTMetaDataPathDetail))
            return false;
        ArrayOfTMetaDataPathDetail other = (ArrayOfTMetaDataPathDetail) obj;
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
        _equals = true && ((this.pathDetailArray == null && other.getPathDetailArray() == null) || (this.pathDetailArray != null && java.util.Arrays
                .equals(this.pathDetailArray, other.getPathDetailArray())));
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
        if (getPathDetailArray() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getPathDetailArray()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getPathDetailArray(), i);
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
            ArrayOfTMetaDataPathDetail.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTMetaDataPathDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pathDetailArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pathDetailArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TMetaDataPathDetail"));
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
