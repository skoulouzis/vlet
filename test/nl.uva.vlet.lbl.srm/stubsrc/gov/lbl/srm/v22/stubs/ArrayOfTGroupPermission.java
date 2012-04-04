/**
 * ArrayOfTGroupPermission.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class ArrayOfTGroupPermission implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TGroupPermission[] groupPermissionArray;

    public ArrayOfTGroupPermission()
    {
    }

    public ArrayOfTGroupPermission(gov.lbl.srm.v22.stubs.TGroupPermission[] groupPermissionArray)
    {
        this.groupPermissionArray = groupPermissionArray;
    }

    /**
     * Gets the groupPermissionArray value for this ArrayOfTGroupPermission.
     * 
     * @return groupPermissionArray
     */
    public gov.lbl.srm.v22.stubs.TGroupPermission[] getGroupPermissionArray()
    {
        return groupPermissionArray;
    }

    /**
     * Sets the groupPermissionArray value for this ArrayOfTGroupPermission.
     * 
     * @param groupPermissionArray
     */
    public void setGroupPermissionArray(gov.lbl.srm.v22.stubs.TGroupPermission[] groupPermissionArray)
    {
        this.groupPermissionArray = groupPermissionArray;
    }

    public gov.lbl.srm.v22.stubs.TGroupPermission getGroupPermissionArray(int i)
    {
        return this.groupPermissionArray[i];
    }

    public void setGroupPermissionArray(int i, gov.lbl.srm.v22.stubs.TGroupPermission _value)
    {
        this.groupPermissionArray[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof ArrayOfTGroupPermission))
            return false;
        ArrayOfTGroupPermission other = (ArrayOfTGroupPermission) obj;
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
        _equals = true && ((this.groupPermissionArray == null && other.getGroupPermissionArray() == null) || (this.groupPermissionArray != null && java.util.Arrays
                .equals(this.groupPermissionArray, other.getGroupPermissionArray())));
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
        if (getGroupPermissionArray() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getGroupPermissionArray()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getGroupPermissionArray(), i);
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
            ArrayOfTGroupPermission.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTGroupPermission"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupPermissionArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupPermissionArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TGroupPermission"));
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
