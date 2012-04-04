/**
 * ArrayOfString.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class ArrayOfString implements java.io.Serializable
{
    private java.lang.String[] stringArray;

    public ArrayOfString()
    {
    }

    public ArrayOfString(java.lang.String[] stringArray)
    {
        this.stringArray = stringArray;
    }

    /**
     * Gets the stringArray value for this ArrayOfString.
     * 
     * @return stringArray
     */
    public java.lang.String[] getStringArray()
    {
        return stringArray;
    }

    /**
     * Sets the stringArray value for this ArrayOfString.
     * 
     * @param stringArray
     */
    public void setStringArray(java.lang.String[] stringArray)
    {
        this.stringArray = stringArray;
    }

    public java.lang.String getStringArray(int i)
    {
        return this.stringArray[i];
    }

    public void setStringArray(int i, java.lang.String _value)
    {
        this.stringArray[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof ArrayOfString))
            return false;
        ArrayOfString other = (ArrayOfString) obj;
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
        _equals = true && ((this.stringArray == null && other.getStringArray() == null) || (this.stringArray != null && java.util.Arrays
                .equals(this.stringArray, other.getStringArray())));
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
        if (getStringArray() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getStringArray()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getStringArray(), i);
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
            ArrayOfString.class, true);

    static
    {
        typeDesc
                .setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager", "ArrayOfString"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stringArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "stringArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
