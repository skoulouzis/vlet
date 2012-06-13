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
 * $Id: VAttribute.java,v 1.9 2011-06-07 15:41:29 ptdeboer Exp $  
 * $Date: 2011-06-07 15:41:29 $
 */
// source: 

package nl.uva.vlet.data;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;

/**
 * This class provides a high level interface to resource Attributes.
 * <p>
 * It is implemented using a {<code>type</code>, <code>name</code>,
 * <code>value</code> triple, so that runtime type and name checking can be
 * performed.<br>
 * The VAttributes does not do any type checking, so casting is possible. a
 * getStringValue() after a setValue(int) will return the string representation
 * of the int. Currently the attributes are stored as (java) Strings so this
 * class is not efficient for large binary attributes.<br>
 * <br>
 * 
 * @author P.T. de Boer
 */

public class VAttribute implements Cloneable, Serializable, Duplicatable<VAttribute>
{
    private static final long serialVersionUID = 6729623290452678587L;

    private static String booleanEnumValues[] =
    { "false", "true" };

    // ========================================================================
    // Class Methods
    // ========================================================================
    /**
     * Parses a name=value statement Optionally this supports a type
     * 
     * @param stat
     * @return
     */
    public static VAttribute createFromAssignment(String stat)
    {
        String strs[] = stat.split("[ ]*=[ ]*");

        if ((strs == null) || (strs.length < 2))
            return null;

        // parse result
        return createFrom(strs[0], strs[1]);
    }

    /**
     * VAtribute Factory: inspect object and create Typed VAttribute.
     * Performance note: since this factory just tries a lot of different
     * methods to parse/check the object value, this method is not efficient.
     */
    public static VAttribute createFrom(String keystr, Object value)
    {
        return new VAttribute(keystr, value);
    }

    public static VAttribute createFromString(VAttributeType attrType, String attrName, String valueStr)
    {
        return new VAttribute(attrType,attrName,valueStr); 
    }
    
    /**
     * Type safe factory method. Object must have specified type
     */
    public static VAttribute createFrom(VAttributeType type, String name, Object value)
    {
        // null value is allowed: 
        if (value==null)
            return new VAttribute(type,name, null); 
        
        VAttributeType objType = VAttributeType.getObjectType(value, null);
        if (objType != type)
            throw new Error("Incompatible Object Type. Specified type=" + type + ", object type=" + objType);

        return new VAttribute(type, name, value.toString());
    }

    public static VAttribute createEnumerate(String name, String values[], String value)
    {
        return new VAttribute(name, values, value);
    }

    /**
     * Create From {Name,Type,Value} triple. Type string must match on of
     * VAttributeType !
     */

    public static VAttribute createFromString(String type, String name, String value)
    {
        VAttributeType aType = VAttributeType.valueOf(type);
        return new VAttribute(aType, name, value);
    }

    /**
     * Create DateTime Attribute from nr of millis since Epoch.
     */
    public static VAttribute createDateSinceEpoch(String name, long millis)
    {
        // store as normalized time string:
        String timeStr = Presentation.createNormalizedDateTimeString(millis);
        return new VAttribute(VAttributeType.TIME, name, timeStr);
    }

    /**
     * Create DateTime Attribute from Unix Time using Milli Seconds !
     */
    public static VAttribute createUnixTimeAttribute(String name, long millis)
    {
        return createDateSinceEpoch(name, millis);
    }

    /**
     * Create Date Attribute
     */
    public static VAttribute createFromDate(String name, Date date)
    {
        return new VAttribute(name, date);
    }

    // ========================================================================
    // Instance
    // ========================================================================

    /** The type of the VAttribute */
    private VAttributeType type = null;

    /** The name of the attribute */
    private String name = null;

    /** Current implementation is to store the VAttribute value as a String */
    // Optional implementation: int intVal long longVal float floatVal double
    // doubleVal etc...
    private String value = null;

    /** Whether attribute is editable. */
    private boolean editable = false;

    /** List of enum values, index enumValue determines which enum value is set */
    private StringList enumValues = null;

    /** index into enumValues[] so that: enumAvalues[enumIndex]==value */
    private int enumIndex = 0;

    /** Optional dynamic help text */
    private String helpText;

    private boolean changed;

    /**
     * Main init method to be called by other constructors. <br>
     * This method may only be used by contructors.
     */
    protected int init(VAttributeType type, String name, String value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
        return 0;
    }

    protected void init(VAttributeType type, String name, long value)
    {
        this.type = type;
        this.name = name;
        this.value = "" + value;
    }

    protected void init(String name, boolean b_value)
    {
        init(VAttributeType.BOOLEAN, name, "" + new Boolean(b_value));
    }

    protected void init(String name, String s_value)
    {
        init(VAttributeType.STRING, name, s_value);
    }

    protected void init(String name, int int_value)
    {
        init(VAttributeType.INT, name, "" + new Integer(int_value));
    }

    /** Initialize as Enum Type */
    protected void init(String name, StringList enumValues, int enumIndex)
    {
        this.name = name;
        this.type = VAttributeType.ENUM;
        this.enumValues = enumValues;
        this.enumIndex = enumIndex;

        if ((enumValues != null) && (enumIndex >= 0) && (enumIndex < enumValues.size()))
        {
            this.value = enumValues.get(enumIndex);
        }
        else
        {
            Global.errorPrintf(this, "Error enumIndex out of bound:%d\n", enumIndex);
            value = "";
        }
    }

    protected void init(String name, long long_value)
    {
        Long l = new Long(long_value);

        this.value = l.toString();
        this.name = name;
        this.type = VAttributeType.LONG;
    }

    protected void init(String name, float float_value)
    {
        Float f = new Float(float_value);

        this.value = f.toString();
        this.name = name;
        this.type = VAttributeType.FLOAT;
    }

    protected void init(String name, double val)
    {
        Double d = new Double(val);

        this.value = d.toString();
        this.name = name;
        this.type = VAttributeType.DOUBLE;
    }

    protected void init(String name, Date date)
    {
        // store as normalized time string:
        String timeStr = Presentation.createNormalizedDateTimeString(date);
        init(VAttributeType.TIME, name, timeStr);
    }

    /** Copy Constructor */
    public VAttribute(VAttribute source)
    {
        copyFrom(source);
    }

    protected void copyFrom(VAttribute source)
    {
        // Duplicate String objects: !

        init(source.type, (source.name != null) ? new String(source.name) : "", (source.value != null) ? new String(
                source.value) : "");

        this.editable = source.editable;
        this.enumIndex = source.enumIndex;
        this.enumValues = source.enumValues;
        this.helpText = source.helpText;
        this.changed = false; // new Attribute: reset 'changed' flag.
    }
   
    /** Constructor to create a 'int' typed and named VAttribute */
    public VAttribute(String name, int val)
    {
        init(name, val);
    }

    /** Constructor to create a 'long' typed and named VAttribute */
    public VAttribute(String name, long val)
    {
        init(name, val);
    }

    /** Constructor to create a 'float' typed and named VAttribute */
    public VAttribute(String name, float val)
    {
        init(name, val);
    }

    /** Constructor to create a 'double' typed and named VAttribute */
    public VAttribute(String name, double val)
    {
        init(name, val);
    }

    /** Constructor to create a enum list of string */
    public VAttribute(String name, String enumValues[], int enumVal)
    {
        init(name, new StringList(enumValues), enumVal);
    }

    /** Constructor to create a enum list of string */
    public VAttribute(String name, StringList enumValues, int enumVal)
    {
        init(name, enumValues, enumVal);
    }

    public VAttribute(String name, Object value)
    {
        this.name = name;
        initValue(value);
    }

    /** Set Value by Object ! */
    protected void initValue(Object value)
    {
        // ===
        // VAttribute itself !
        // ===
        if (value instanceof VAttribute)
        {
            String orgName = this.name;
            this.copyFrom((VAttribute) value);
            this.name = orgName; // Keep Name !
            return;
        }

        if ((value instanceof VRL) || (value instanceof URI) || (value instanceof URL))
        {
            init(VAttributeType.VRL, name, value.toString());
            return;
        }

        boolean recognized = true;
        //
        // try java.lang classes:
        //
        if (value instanceof String)
            init(name, value.toString());
        else if (value instanceof Boolean)
            init(VAttributeType.BOOLEAN, name, value.toString());
        else if (value instanceof Byte)
            init(VAttributeType.INT, name, value.toString());
        else if (value instanceof Integer)
            init(VAttributeType.INT, name, value.toString());
        else if (value instanceof Long)
            init(VAttributeType.LONG, name, value.toString());
        else if (value instanceof java.lang.Character)
            init(VAttributeType.STRING, name, "" + value);
        else if (value instanceof Float)
            init(name, ((Float) value).doubleValue());
        else if (value instanceof Double)
            init(name, ((Double) value).doubleValue());
        else if (value instanceof Date)
            init(name, (Date) value);
        else
        {
            recognized = false;
        }

        if (recognized == true)
            return;

        // Can not use Boolean.parseBoolean() since that returns
        // the value 'false' for anything other than the string value 'true' !
        String str = value.toString();

        if (str.compareToIgnoreCase("true") == 0)
        {
            init(name, true);
            return;
        }
        else if (str.compareToIgnoreCase("false") == 0)
        {
            init(name, false);
            return;
        }

        try
        {
            // try to parse the object value string:
            Long lVal = Long.parseLong(value.toString());
            if (lVal != null)
            {
                init(name, lVal);
                return;
            }
        }
        catch (Throwable e)
        {
            ;
        } // not Long

        try
        {
            // try to parse the object value string:
            Double dVal = Double.parseDouble(value.toString());
            if (dVal != null)
            {
                init(name, dVal);
                return;
            }
        }
        catch (Throwable e)
        {
            ;
        }

        // VRL/URI
        String uristr = value.toString();
        try
        {
            VRL vrl = new VRL(uristr);
            init(VAttributeType.VRL, name, vrl.toString());
            return;
        }
        catch (VRLSyntaxException e)
        {
            ;//
        }

        init(VAttributeType.STRING, name, value.toString());
    }

    /**
     * Create new Enumerated VAttribute with enumVals as possible values and
     * defaultVal (which must be element of enumVals) as default
     */
    public VAttribute(String name, String[] enumVals, String defaultVal)
    {
        int index = 0; // use default of 0!

        if ((enumVals == null) || (enumVals.length <= 0))
            throw new NullPointerException("Cannot not have empty enum value list !");
        StringList enums = new StringList(enumVals);

        // robuustness! add defaultVal if not in enumVals!
        enums.add(defaultVal, true);

        index = enums.find(defaultVal);

        if (index < 0)
            index = 0;

        init(name, enums, index);
    }

    /** Anonymous String typed Value */
    /*
     * public VAttribute(String value) { init(VAttributeType.STRING, null,
     * value); }
     */

    /** Named String typed Value */
    public VAttribute(String name, String value)
    {
        init(VAttributeType.STRING, name, value);
    }

    /** Custom named & typed Attribute */
    public VAttribute(VAttributeType type, String name, String value)
    {
        init(type, name, value);
    }

    /** Custom named & typed Attribute */
    public VAttribute(VAttributeType type, String name, long value)
    {
        init(type, name, value);
    }

    public VAttribute(String name, VRL url)
    {
        init(VAttributeType.VRL, name, "" + url);
    }

    public VAttribute(String keystr, Date date)
    {
        init(keystr, date);
    }

    /**
     * Return duplicate of this object. This method returns the same class
     * instead of the object.clone() method All values are copied.
     * 
     * @return
     */

    public VAttribute clone()
    {
        return duplicate(false);
    }

    public VAttribute duplicate()
    {
        return duplicate(false);
    }

    @Override
    public boolean shallowSupported()
    {
        return false;
    }

    @Override
    public VAttribute duplicate(boolean shallow)
    {
        return new VAttribute(this);
    }

    // =================================================================
    // Instance Getters/Setters
    // =================================================================

    /**
     * Get Name of Attribute. Note that the Name may never change during the
     * lifetime of an VAttribute !
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get Type of Attribute. Note that the Type may never change during the
     * lifetime of an VAttribute !
     */
    public VAttributeType getType()
    {
        return type;
    }

    /**
     * Return value as actual Java Object with the specified type. This will
     * return an object with a type compatible with VAttributeType class.
     */
    public Object getValueObject()
    {
        if (value==null)
            return null; 
        
        try
        {
            switch (this.type)
            {
                case BOOLEAN:
                    return new Boolean(value);
                case INT:
                    return new Integer(value);
                case LONG:
                    return new Long(value);
                case FLOAT:
                    return new Float(value);
                case DOUBLE:
                    return new Double(value);
                case ENUM:
                    return new String(value);
                case STRING:
                    return new String(value);// duplicate !
                case VRL:
                    return new VRL(value);
                case TIME:
                    return getDateValue();
                default:
                {
                    // OOpsy
                    Global.errorPrintf(this, "***Error: toObject(): Attribute type not supported:%s\n", this);
                    return null;
                }

            }
        }
        // interpret errors:
        catch (VRLSyntaxException e)
        {
            // low level error:
            Global.errorPrintf(this, "***Error: toObject(): Syntax Error for VRL:%s\n", this);
            return null;
        }
        catch (NumberFormatException e)
        {
            // low level error:
            Global.errorPrintf(this, "***Error: toObject(): Syntax Error for:%s\n", value);
            Global.errorPrintf(this, "*** Error: toObject():%s\n", e.getMessage());
            return null;

        }
        catch (Throwable e)
        {
            // low level error:
            Global.errorPrintf(this, "***Error: toObject(): Object conversion error for:%s", value);
            Global.errorPrintStacktrace(e);
            return null;
        }
    }
    
    /**
     * Return Value as String. To Actual get the typed object used
     * getValueObject()
     */
    public String getValue()
    {
        return value;
    }

    /** Explicit return Value as String */
    public String getStringValue()
    {
        return value;
    }

    /** Get String list of enumeration types */
    public String[] getEnumValues()
    {
        if (type == VAttributeType.ENUM)
        {
            if (enumValues == null)
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

    /** For (debug) printing to stdout only. This is NOT a serializer */
    public String toString()
    {
        String enumStr = "";

        if (isEnumType())
        {
            enumStr = ",{";

            if (this.enumValues != null)
                for (int i = 0; i < this.enumValues.size(); i++)
                {
                    enumStr = enumStr + enumValues.get(i);
                    if (i + 1 < enumValues.size())
                        enumStr = enumStr + ",";
                }

            enumStr = enumStr + "}";

        }

        // convert to VAttribute Triplet
        return "{" + type + "," + name + "," + value + enumStr + ",[" + ((isEditable()) ? "E" : "")
                + ((hasChanged()) ? "C" : "") + "]}";
    }
    
    public String setValue(String val)
    {
        String oldval=value; 
        _setValue(val);  
        return oldval; 
    }

    /** Set value by object */ 
    public Object setValue(Object obj)
    {
        Object prev=this.value;
        if (obj==null)
        {
            _setValue(null);
        }
        else
        {
            String strvalue=null; 
        
            if (obj instanceof String)
                strvalue=(String)obj;
            else
                strvalue=obj.toString(); 
            
            _setValue(strvalue);
        }
        
        return prev; 
    }
    
    /** Copy value form other VAttribute, but keep current type and flags */
    public void setValueFrom(VAttribute otherAttr)
    {
        _setValue(otherAttr.value); // private copy 
    }

    /** Actual setter implemenetation */
    protected String _setValue(String val)
    {
        String orgVal = value;
        if (val == null)
            value = ""; // null not allowed => auto cast
        else
            value = new String(val); // create private copy

        // update enum index:
        if (type == VAttributeType.ENUM)
        {
            // ? auto set, checks should already have been done before this
            // code.
            // if (enumValues==null)
            // {
            // enumValues=new StringList(val);
            // }

            this.enumIndex = enumValues.find(value);
        }

        this.changed = true;
        return orgVal;
    }
    
    public boolean isType(VAttributeType otherType)
    {
        return this.getType().equals(otherType);
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

    public void setValue(int val)
    {
        setValue("" + val);
    }

    /**
     * Return true if this VAttribute is editable. 
     * No checking is done when a value is set.
     * Even if an attribute is not editable, setValue(...) still can be called.  
     */
    public boolean isEditable()
    {
        return this.editable;
    }

    public void setEditable(boolean b)
    {
        this.editable = b;
    }

    // ========================================================================
    // Class Methods
    // ========================================================================

    /** Create a deep copy of Attribute Array */
    public static VAttribute[] duplicateArray(VAttribute[] attrs)
    {
        if (attrs == null)
            return null;

        VAttribute newAttrs[] = new VAttribute[attrs.length];

        for (int i = 0; i < attrs.length; i++)
        {
            if (attrs[i] != null)
                newAttrs[i] = attrs[i].clone();
            else
                newAttrs[i] = null; // be robuust: accept null attributes
        }

        return newAttrs;
    }

    public static VAttribute[] convertVectorToArray(Vector<VAttribute> attributes)
    {
        VAttribute newAttrs[] = new VAttribute[attributes.size()];

        for (int i = 0; i < newAttrs.length; i++)
        {
            if (attributes.elementAt(i) != null)
                newAttrs[i] = attributes.elementAt(i).clone();
            else
                newAttrs[i] = null; // be robuust: accept null attributes
        }

        return newAttrs;
    }

    public int getIntValue()
    {
        if ((value == null) || (value.compareTo("") == 0))
            return -1;

        return new Integer(value); // auto cast !
    }

    public long getLongValue()
    {
        if ((value == null) || (value.compareTo("") == 0))
            return -1;

        // auto cast to millis !
        if (this.type == VAttributeType.TIME)
        {
            return Presentation.createMillisFromNormalizedDateTimeString(value);
        }

        return new Long(value); // auto cast !
    }

    public VRL getVRL() throws VRLSyntaxException
    {
        return new VRL(value);
    }

    public double getDoubleValue()
    {
        return new Double(value); // auto cast !
    }

    public float getFloatValue()
    {
        return new Float(value); // auto cast !
    }

    public boolean getBooleanValue()
    {
        return new Boolean(value); // auto cast !
    }

    /** Ignore case only makes sense for String like Attributes */
    public int compareToIgnoreCase(VAttribute attr2)
    {
        return compareTo(attr2, true);
    }

    public int compareTo(VAttribute attr2)
    {
        return compareTo(attr2, false);
    }

    /**
     * Compares this value to value of other VAttribute 'attr'. The type of this
     * attribute is used and the other attribute is converted (casted) to this
     * type.
     * 
     * @param attr
     * @return
     */
    public int compareTo(VAttribute attr, boolean ignoreCase)
    {
        return compare(this,attr,ignoreCase); 
    }
    
    public static int compare(VAttribute a1,VAttribute a2,boolean ignoreCase)
    {
        Object value1=null;
        Object value2=null; 
        if (a1!=null)
            value1=a1.value; 
        if (a2!=null)
            value2=a2.value; 
        
        // todo: better NULL compare 
        if ((value1==null) && (value2==null))
            return 0; 
        
        // NULL compare: must be equivalent with StringUtil.compare() ! 
        if ((value1==null) && (value2!=null))
            return StringUtil.compare(null,""); 
        if (value2==null) 
            return StringUtil.compare("",null); 
        
        // equals() method!
        if (value1==value2)
            return 0; 
        
        // PRE: neither a1 nor a2 is null or has null value
        switch (a1.type)
        {
            case INT:
            case LONG:
            case TIME:
                // use long both for int,long and time (millis!)
                if (a1.getLongValue() < a2.getLongValue())
                    return -1;
                else if (a1.getLongValue() > a2.getLongValue())
                    return 1;
                else
                    return 0;
                // break;
            case FLOAT:
                if (a1.getFloatValue() < a2.getFloatValue())
                    return -1;
                else if (a1.getFloatValue() > a2.getFloatValue())
                    return 1;
                else
                    return 0;
                // break;
            case DOUBLE:
                if (a1.getDoubleValue() < a2.getDoubleValue())
                    return -1;
                else if (a1.getDoubleValue() > a2.getDoubleValue())
                    return 1;
                else
                    return 0;
                // break;
            default:
                // default use string representation
                return StringUtil.compare(a1.getStringValue(),a2.getStringValue(), ignoreCase); 
                // break;
        }
    }

    public boolean hasName(String nname)
    {
        return (this.name.compareTo(nname) == 0);
    }

    public void setValue(boolean b)
    {
        setValue((b == true) ? "true" : "false");
    }
    
    /** Will be changed to VAttribute help data base */
    public void setHelpText(String str)
    {
        helpText = str;
    }

    /**
     * Return (mini) help text for ToolTip text Implementation.
     * Will be moved to VAttribute help database. 
     */
    public String getHelpText()
    {
        return helpText;
    }

    /** Return value as Date Object */
    public Date getDateValue()
    {
        return Presentation.createDateFromNormalizedDateTimeString(value);
    }

    public boolean isEnumType()
    {
        if (this.type == VAttributeType.ENUM)
            return true;

        return false;
    }

    public static boolean isSectionName(String name)
    {
        if (name == null)
            return false;

        return (name.startsWith("[") || name.startsWith("-["));
    }

    // ========================================================================
    // Aggregate interface (Under Construction)
    // ========================================================================

    /** Returns optional comma seperated value list as StringList. */
    public StringList getStringListValue()
    {
        return getStringListValue("[,]");
    }

    /**
     * Returns value list as StringList using regular expression regExp to split
     * the values.
     */
    public StringList getStringListValue(String regExp)
    {
        return StringList.createFrom(getStringValue(), regExp);
    }

}
