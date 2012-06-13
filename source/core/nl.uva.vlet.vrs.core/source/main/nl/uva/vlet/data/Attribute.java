/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: Attribute.java,v 1.4 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

package nl.uva.vlet.data;

 
import java.io.Serializable;
import java.util.Date;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;


/**
 * This class provides a high level interface to resource Attributes.
 * <p> 
 * It is implemented using a {<code>type</code>, <code>name</code>,
 * <code>value</code>} triple, so that runtime type and name checking can 
 * be performed.<br>
 * The VAttributes does not do any type checking, so casting is possible. 
 * a getStringValue() after a setValue(int) will return the string representation of the int.
 * <b>
 * Under Construction: This is a new type which stores values as Objects in stead of Strings.    
 * <br>
 * 
 * @author P.T. de Boer
 */
public class Attribute  implements Cloneable, Serializable,Duplicatable<Attribute>//,Triple<VAttributeType,String,Object> 
{
	private static final long serialVersionUID = 7275104309580585823L;

	private static String booleanEnumValues[] = { "false", "true" };

	private static ClassLogger logger; 
	{
		logger=ClassLogger.getLogger(Attribute.class); 
	}
	// ========================================================================
	// Class Methods 
	// ========================================================================

    public static Attribute createFrom(String name, Object value)
    {
        VAttributeType type=VAttributeType.getObjectType(value,null); 
       
        if (type!=null)
            return new Attribute(type,name,value);
        
        // support for Any ? 
        return new Attribute(VAttributeType.STRING,name,value.toString());     
    }

//	/**
//	 * Parses a name=value statement 
//	 * Optionally this supports a type
//	 * 
//	 * @param stat
//	 * @return
//	 */
//	public staticVAttribute parseAssignment(String stat)
//	{
//		String strs[] = stat.split("[ ]*=[ ]*");
//
//		if ((strs == null) || (strs.length < 2))
//			return null;
//
//		// parse result 
//		return createFrom(strs[0], strs[1]);
//	}
//
//	/**
//	 *  VAtribute Factory: inspect object and create Typed VAttribute.
//	 *  Performance note: since this factory just tries a lot of different
//	 *  methods to parse/check the object value, this method is not efficient. 
//	 */
//	public staticVAttribute createFrom(String keystr, Object value)
//	{
//	    return new Attribute(keystr,value); 
//	}
//	
//
//	public static Attribute createEnumerate(String name, String values[],
//			String value)
//	{
//		return new Attribute(name, values, value);
//	}
//
//	/** Create From {Name,Type,Value} triple.
//	 * Type string must match on of VAttributeType !*/
//
//	public static Attribute createFrom(String type, String name, String value)
//	{
//		VAttributeType aType = VAttributeType.valueOf(type);
//		return new Attribute(aType, name, value);
//	}
//
//	/**
//	 * Create DateTime Attribute from nr of millis since Epoch. */
//	public static Attribute createDateSinceEpoch(String name, long millis)
//	{
//		// store as normalized time string: 
//		String timeStr = Presentation.createNormalizedDateTimeString(millis);
//		return new Attribute(VAttributeType.DATETIME, name, timeStr);
//	}
//	
//	/**
//	 * Create DateTime Attribute from Unix Time using Milli Seconds !  
//	 */
//	public static Attribute createUnixTimeAttribute(String name, long millis)
//	{
//		return createDateSinceEpoch(name,millis); 
//	}
//
//	/**
//	 * Create Date Attribute. Date is converted to a normalized
//	 * time date String. 
//	 */
//	public static Attribute createDate(String name, Date date)
//	{
//		return new Attribute(name,date);
//	}
//
	
	/** Factory Method: Convert String type to Object */ 
	public static Object stringValueToObject(VAttributeType toType, String strValue)
    {
        switch(toType)
        {
            case BOOLEAN: 
                return Boolean.parseBoolean(strValue); 
            case INT: 
                return Integer.parseInt(strValue); 
            case LONG:
                return Long.parseLong(strValue);
            case FLOAT:
                return Float.parseFloat(strValue);
            case DOUBLE:
                return Double.parseDouble(strValue);
            case STRING:
                return strValue; 
            case ENUM:
                return strValue; 
            case TIME:
                return strValue; //keep DateTime String "as is".    
            case VRL: 
                return strValue;     
            default:
                throw new Error("Unsupported Type: Cannot convert String:'"+strValue+"' to type:"+toType); 
        }
    }
    
	private static Object duplicateValue(VAttributeType type,Object object)
	{
	    if (object==null)
	        return null; 
	    
		switch(type)
		{
            case BOOLEAN:
                return new Boolean((Boolean)object);
			case INT:
				return new Integer((Integer)object);
			case LONG:
				return new Long((Long)object); 
			case FLOAT:
				return new Float((Float)object); 
			case DOUBLE:
				return new Double((Double)object); 
			case ENUM: // enums are stored as String 
			case TIME: // normalized time string !  
			case STRING:
				return new String((String)object);
			default:
			{
			    // works for VRL:
				if (object instanceof Duplicatable)
					return ((Duplicatable<?>)object).duplicate(false); 
					
				throw new Error("Cannot clone/duplicate value object:"+object); 
			}
		}
	}
	
	// ========================================================================
	// Class Methods 
	// ========================================================================

//	/** Create a deep copy of Attribute Array */
//	public static Attribute[] duplicateArray(Attribute[] attrs)
//	{
//		if (attrs == null)
//			return null;
//
//		Attribute newAttrs[] = new Attribute[attrs.length];
//
//		for (int i = 0; i < attrs.length; i++)
//		{
//			if (attrs[i] != null)
//				newAttrs[i] = attrs[i].clone();
//			else
//				newAttrs[i] = null; // be robuust: accept null attributes 
//		}
//
//		return newAttrs;
//	}
//
//	public static Attribute[] convertVectorToArray(
//			Vector<Attribute> attributes)
//	{
//
//		Attribute newAttrs[] = new Attribute[attributes.size()];
//
//		for (int i = 0; i < newAttrs.length; i++)
//		{
//			if (attributes.elementAt(i) != null)
//				newAttrs[i] = attributes.elementAt(i).clone();
//			else
//				newAttrs[i] = null; // be robuust: accept null attributes 
//		}
//
//		return newAttrs;
//	}
	// ========================================================================
	// Instance
	// ========================================================================

	/** The resolved type of the VAttribute */
	private VAttributeType type = null;

	/** The name of the attribute */
	private String name = null;

	/** Store value as Generic object. */ 
	private Object value=null;

	/** Whether attribute is editable.*/
	private boolean editable = false;

	/** List of enum values, index enumValue determines which enum value is set */
	private StringList enumValues = null;

	/** index into enumValues[] so that: enumAvalues[enumIndex]==value */
	private int enumIndex = 0;

	private boolean changed=false;

	/** 
	 * Main init method to be called by other constructors. <br>
	 * This method may only be used by contructors. 
	 * Object value must be a PRIVATE copy! (deep copy) 
	 */
	protected void init(VAttributeType type, String name, Object value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		checkValueType(type,value); 
	}

	private void checkValueType(VAttributeType type,Object value)
	{
	    VAttributeType objType=VAttributeType.getObjectType(value,VAttributeType.STRING);  
	    
        // DateTime is stored as String; 
        if (type==VAttributeType.TIME && objType==VAttributeType.STRING)
            return;
        
        // Same for ENUM  
        if (type==VAttributeType.ENUM && objType==VAttributeType.STRING)
            return; 
	    
	    if (objType!=type) 
	        throw new Error("Object type is not the same as expected. Expected="+type+",parsed="+objType); 
	    
	    // no nesting!
	    if (value instanceof Attribute)
	        throw new Error("Attribute as value!"); 

	    if (value instanceof VAttribute)
	        throw new Error("VAttribute as value!"); 
	}
	
	/** Initialize as Enum Type */
	protected void init(String name, StringList enumValues, int enumIndex)
	{
		this.name = name;
		this.type = VAttributeType.ENUM;
		this.enumValues = enumValues;
		this.enumIndex = enumIndex;
		
		if ((enumValues!=null) && (enumIndex>=0) && (enumIndex<enumValues.size()) )
		{
			this.value = enumValues.get(enumIndex);
		}
		else
		{
			logger.errorPrintf("Error enumIndex out of bound:%d\n",enumIndex); 
			value="";
		}
	}
	   
	/** Copy Constructor */
	public Attribute(Attribute source)
	{
	    copyFrom(source); 
	}

	protected Attribute(String name)
	{
	    this.name=name; 
	}
	
	protected void copyFrom(Attribute source)
	{
		// Duplicate String objects: !
		init(source.type, source.name,duplicateValue(source.type,source.value)); 
		
		this.editable = source.editable;
		this.enumIndex = source.enumIndex;
		this.enumValues = source.enumValues;
		this.changed = false; // new Attribute: reset 'changed' flag.  
	}

	/** Constructor to create a enum list of string */
	public Attribute(String name, String enumValues[], int enumVal)
	{
		init(name,new StringList(enumValues), enumVal);
	}

	   /** Constructor to create a enum list of string */
    public Attribute(String name, StringList enumValues, int enumVal)
    {
        init(name,enumValues, enumVal);
    }
    
    public Attribute(String name, Boolean val)
    {
        init(VAttributeType.BOOLEAN,name,val); 
    }
    
    public Attribute(String name, Integer val)
    {
        init(VAttributeType.INT,name, val); 
    }
    
    public Attribute(String name, Long val)
    {
        init(VAttributeType.LONG,name,val); 
    }
    
    public Attribute(String name, Float val)
    {
        init(VAttributeType.FLOAT,name,val); 
    }
    
    public Attribute(String name, Double val)
    {
        init(VAttributeType.DOUBLE,name,val); 
    }

	/** Create new Enumerated VAttribute with enumVals as possible values and
	 * defaultVal (which must be element of enumVals) as default */
	public Attribute(String name, String[] enumVals, String defaultVal)
	{
		int index = 0; // use default of 0! 

		if ((enumVals==null) || (enumVals.length<=0))
			throw new NullPointerException("Cannot not have empty enum value list !");
		StringList enums=new StringList(enumVals); 
		
		// robuustness! add defaultVal if not in enumVals! 
		enums.add(defaultVal,true); 
		
		index = enums.find(defaultVal);

		if (index < 0)
			index = 0;

		init(name, enums, index);
	}

	/** Named String {Type,Value} Tuple */
	public Attribute(String name, String value)
	{
		init(VAttributeType.STRING, name, value);
	}

    /** Custom named & explicit typed Attribute */
    public Attribute(VAttributeType type, String name, Object value)
    {
        init(type, name, value);
    }

	public Attribute(String name, Date date)
	{
	    init(VAttributeType.TIME,name,Presentation.createNormalizedDateTimeString(date));
	}

	/** 
	 * Return duplicate of this object. 
	 * This method returns the same class instead of the object.clone() method 
	 * All values are copied.  
	 * @return
	 */

	public Attribute clone()
	{
		return new Attribute(this);
	}

	public Attribute duplicate()
	{
		return clone();
	}

	//  =================================================================
	//  Instance Getters/Setters 
	//  =================================================================

	/** 
	 * Get Name of Attribute. 
	 * Note that the Name may never change during the lifetime of an VAttribute ! 
	 */
	public String getName()
	{
		return this.name;
	}

	// type+name should never change in the lifetime of an Attribute  
	/*private void setType(VAttributeType type)
	 {
	 this.type = type;
	 }*/

	/** 
	 * Get Type of Attribute. 
	 * Note that the Type may never change during the lifetime of an VAttribute !
	 */

	public VAttributeType getType()
	{
		return type;
	}

	/**
	 * Return Value as String. To Actual get the typed object
	 * used getValueObject() 
	 */
	public Object getValue()
	{
		return value;
	}

	/** Explicit return Value as String */
	public String getStringValue()
	{ 
		if (value == null)
			return null; 
		
		if (value instanceof String)
			return (String)value; 
		
		return value.toString(); 
	}

	/** Get String list of enumeration types */
	public String[] getEnumValues()
	{
		if (type == VAttributeType.ENUM)
		{
		    if (enumValues==null)
		        return null;
		    else
		        return enumValues.toArray();
		}
		
		if (type == VAttributeType.BOOLEAN)
			return booleanEnumValues;

		return null;
	}

	/** Return enum order of current value */
	public int getEnumIndex()
	{
		if (type == VAttributeType.ENUM)
			return enumIndex;

		if (type == VAttributeType.BOOLEAN)
			return (getBooleanValue() ? 1 : 0);

		return 0;
	}

	public boolean hasEnumValue(String val)
	{
		return this.enumValues.contains(val);   
	}

	public boolean hasSetValue(String val)
	{
		// Set is Enum:
		return this.hasEnumValue(val); 
	}

	// Formatters/Stringifiers 

	/** For printing to stdout only. This is NOT a serializer */
	public String toString()
	{
		String enumStr = "";

		if (isEnumType())
		{
			enumStr = ",{";

			if (this.enumValues!=null)
				for (int i = 0; i < this.enumValues.size(); i++)
				{
					enumStr = enumStr + enumValues.get(i);
					if (i + 1 < enumValues.size())
						enumStr = enumStr + ",";
				}

			enumStr = enumStr + "}";
		}

		// convert to VAttribute Triplet
		return "{" + type + "," + name + "," + value + enumStr + ",["
		       + ((isEditable()) ? "E" : "") + ((hasChanged()) ? "C" : "")
		       + "]}";
	}
	
	/** Convert String value to desired type */ 
    public void setValueFromString(VAttributeType type,String stringValue)
    {
        this.value=stringValueToObject(type,stringValue);
        this.type=type; 
    }
    
    /** 
     * Master _setValue method. All set* methods must call this one.  
     */
    private void _setValue(VAttributeType type,Object object) 
    {
        this.checkValueType(type,object);
        this.type=type;
        this.value=object;
        this.changed=true;
    }
    
    public void setValue(Object newValue)
    {
        _setValue(type,newValue); 
    }
    public void setValue(int intVal)  
    {
        _setValue(VAttributeType.INT,new Integer(intVal));  
    }
    
    public void setValue(long longVal)  
    {
        _setValue(VAttributeType.LONG,new Long(longVal));  
    }
    
    public void setValue(float floatVal)  
    {
        _setValue(VAttributeType.FLOAT,new Float(floatVal));  
    }
    
    public void setValue(double doubleVal)  
    {
        _setValue(VAttributeType.DOUBLE,new Double(doubleVal));  
    }
    
    /** Reset changed flag. */
	public void setNotChanged()
	{
		this.changed = false;
	}

	/** Whether the value has changed since the last setNotChanged() */
	public boolean hasChanged()
	{
		return changed;
	}
	
	public boolean isType(VAttributeType otherType)
	{
	    return this.type.equals(otherType); 
	}

	/** 
	 * Return true if this VAttribute is editable. 
	 * This mean that the setValue() methods can be 
	 * used to change the value. 
	 * Note that VAttribute by default or NOT editable. 
	 * use setEditable() to change this. 
	 */
	public boolean isEditable()
	{
		return this.editable;
	}

	public void setEditable(boolean b)
	{
		this.editable = b;
	}

	public int getIntValue()
	{
		if (value==null)
			return 0; // by definition;  
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).intValue(); 
			case LONG:
				return ((Long)value).intValue(); 
			case FLOAT:
				return ((Float)value).intValue(); 
			case DOUBLE: 				
				return ((Double)value).intValue(); 
			default:
				return Integer.parseInt(getStringValue());
		}
	}

	public long getLongValue()
	{
		if (value==null)
			return 0; // by definition;  
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).longValue(); 
			case LONG:
				return ((Long)value).longValue(); 
			case FLOAT:
				return ((Float)value).longValue(); 
			case DOUBLE: 				
				return ((Double)value).longValue(); 
			default:
				return Long.parseLong(getStringValue());
		}
	}


	public float getFloatValue()
	{
		if (value==null)
			return Float.NaN; 

		switch (this.type)
		{
			case INT:
				return ((Integer)value).floatValue(); 
			case LONG:
				return ((Long)value).floatValue(); 
			case FLOAT:
				return ((Float)value).floatValue(); 
			case DOUBLE: 				
				return ((Double)value).floatValue(); 
			default:
				return Float.parseFloat(getStringValue()); // auto cast !
		}
	}

	public double getDoubleValue()
	{
		if (value==null)
			return Double.NaN; 
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).doubleValue(); 
			case LONG:
				return ((Long)value).doubleValue(); 
			case FLOAT:
				return ((Float)value).doubleValue(); 
			case DOUBLE: 				
				return ((Double)value).doubleValue(); 
			default:
				return Long.parseLong(getStringValue());
		}
	}

	public boolean getBooleanValue()
	{
		if (value==null)
			return false; // by definition 
		
		switch (this.type)
		{
			case INT:
				return (((Integer)value)!=0);  
			case LONG:
				return (((Long)value)!=0); 
			case FLOAT:
				return (((Float)value)!=0);  
			case DOUBLE: 				
				return (((Double)value).doubleValue()!=0);  
			default:
				return Boolean.parseBoolean(getStringValue());
		}
	}
	
   public VRL getVRL() throws VRLSyntaxException
   {
       if (this.value==null)
           return null; 
       
       if (this.type==VAttributeType.VRL)
           return (VRL)value; 
       // auto cast 
       return new VRL(this.getStringValue()); 
   }
	
	/** Ignore case only makes sense for String like Attributes */ 
	public int compareToIgnoreCase(Attribute attr2) 
	{
		return compareTo(attr2,true); 
	}
	
	public int compareTo(Attribute attr2) 
	{
		return compareTo(attr2,false); 
	}
	/**
	 * Compares this value to value of other VAttribute 'attr'.  
	 * The type of this attribute is used and the other
	 * attribute is converted (casted) to this type. 
	 * 
	 * @param attr
	 * @return
	 */
	public int compareTo(Attribute attr,boolean ignoreCase)
	{
		if (this.value == null)
		{
			if ((attr != null) && (attr.getValue() != null))
				return 1;
			else
				return 0; // null equals null
		}

		switch (this.type)
		{
			case INT:
			case LONG:
			 // use long both for int,long and time (millis!) 
                if (this.getLongValue() < attr.getLongValue())
                    return -1;
                else if (this.getLongValue() > attr.getLongValue())
                    return 1;
                else
                    return 0;
			case TIME:
			    // compare using normalized data time string (are sortable) 
			    String dts1=this.getNormalizedDateTimStringValue(); 
			    String dts2=this.getNormalizedDateTimStringValue();
			    return dts1.compareTo(dts2); 
				//break;
			case FLOAT:
				if (this.getFloatValue() < attr.getFloatValue())
					return -1;
				else if (this.getFloatValue() > attr.getFloatValue())
					return 1;
				else
					return 0;
				//break;
			case DOUBLE:
				if (this.getDoubleValue() < attr.getDoubleValue())
					return -1;
				else if (this.getDoubleValue() > attr.getDoubleValue())
					return 1;
				else
					return 0;
				//break;
			default:
			{
				String s1=this.toString(); 
				String s2=attr.toString(); 
				
				// Default use string reprentation 
				if (attr.getValue() != null)
				{
					if (ignoreCase)
					{
						return s1.compareToIgnoreCase(s2);
					}
					else
					{
						return s1.compareTo(s2);
					}
				}
				else
					return 1; // this >> null
				//break;
			}
		}
	}

	public boolean hasName(String nname)
	{
		return (this.name.compareTo(nname) == 0);
	}

	public void setValue(boolean b) 
	{
	    _setValue(VAttributeType.BOOLEAN,new Boolean(b)); 
	}
	
    public void setValue(VAttributeType type, Object value)
    {
        _setValue(type,value); 
    }
    
	public Date getDateValue()
	{
		return Presentation.createDateFromNormalizedDateTimeString(getStringValue());
	}

	/** Return normalized date time string value as defined in Presentation. */ 
	public String getNormalizedDateTimStringValue()
    {
	    return getStringValue();
    }
	
	public boolean isEnumType()
	{
		if (this.type==VAttributeType.ENUM) 
			return true; 

		return false;
	}

    public static boolean isSectionName(String name)
    {
       if (name==null)
          return false; 
       
       return ( name.startsWith("[") ||  name.startsWith("-["));
    }

	@Override
	public boolean shallowSupported() 
	{
		return false;
	}

	@Override
	public Attribute duplicate(boolean shallow) 
	{
		if (shallow)
			logger.warnPrintf("Asked for a shallow copy when this isn't supported\n"); 
		return new Attribute(this); 
	}
	
	// ========================================================================
    // Aggregate interface (Under Construction) 
    // ========================================================================
    
    /** Returns optional comma seperated value list as StringList.*/ 
    public StringList getStringListValue()
    {
        return getStringListValue("[,]");  
    }
    
    /**
     * Returns value list as StringList using regular expression regExp to
     * split the values.
     */ 
    public StringList getStringListValue(String regExp)
    {
        return StringList.createFrom(getStringValue(),regExp);
    }

}
