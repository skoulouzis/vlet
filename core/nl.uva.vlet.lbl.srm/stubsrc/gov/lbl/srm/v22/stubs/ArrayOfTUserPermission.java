/**
 * ArrayOfTUserPermission.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 May 16, 2006 (03:42:07 EDT) WSDL2Java emitter.
 */

package gov.lbl.srm.v22.stubs;

public class ArrayOfTUserPermission implements java.io.Serializable
{
    private gov.lbl.srm.v22.stubs.TUserPermission[] userPermissionArray;

    public ArrayOfTUserPermission()
    {
    }

    public ArrayOfTUserPermission(gov.lbl.srm.v22.stubs.TUserPermission[] userPermissionArray)
    {
        this.userPermissionArray = userPermissionArray;
    }

    /**
     * Gets the userPermissionArray value for this ArrayOfTUserPermission.
     * 
     * @return userPermissionArray
     */
    public gov.lbl.srm.v22.stubs.TUserPermission[] getUserPermissionArray()
    {
        return userPermissionArray;
    }

    /**
     * Sets the userPermissionArray value for this ArrayOfTUserPermission.
     * 
     * @param userPermissionArray
     */
    public void setUserPermissionArray(gov.lbl.srm.v22.stubs.TUserPermission[] userPermissionArray)
    {
        this.userPermissionArray = userPermissionArray;
    }

    public gov.lbl.srm.v22.stubs.TUserPermission getUserPermissionArray(int i)
    {
        return this.userPermissionArray[i];
    }

    public void setUserPermissionArray(int i, gov.lbl.srm.v22.stubs.TUserPermission _value)
    {
        this.userPermissionArray[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj)
    {
        if (!(obj instanceof ArrayOfTUserPermission))
            return false;
        ArrayOfTUserPermission other = (ArrayOfTUserPermission) obj;
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
        _equals = true && ((this.userPermissionArray == null && other.getUserPermissionArray() == null) || (this.userPermissionArray != null && java.util.Arrays
                .equals(this.userPermissionArray, other.getUserPermissionArray())));
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
        if (getUserPermissionArray() != null)
        {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getUserPermissionArray()); i++)
            {
                java.lang.Object obj = java.lang.reflect.Array.get(getUserPermissionArray(), i);
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
            ArrayOfTUserPermission.class, true);

    static
    {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "ArrayOfTUserPermission"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userPermissionArray");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userPermissionArray"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://srm.lbl.gov/StorageResourceManager",
                "TUserPermission"));
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
