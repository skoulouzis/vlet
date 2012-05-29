///*
// * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: VAttribute2.java,v 1.2 2011-04-18 12:00:35 ptdeboer Exp $  
// * $Date: 2011-04-18 12:00:35 $
// */ 
//// source: 
//
//package nl.uva.vlet.data;
//
//import java.net.URI;
//import java.util.Date;
//
//import nl.uva.vlet.presentation.Presentation;
//import nl.uva.vlet.vrl.VRL;
// 
///**
// * This class provides a high level interface to resource Attributes.
// * <p> 
// * It is implemented using a {<code>type</code>, <code>name</code>,
// * <code>value</code>} triple, so that runtime type and name checking can 
// * be performed.<br>
// * Casting values is possible though the set/get interface.  
// * For example a getStringValue() after a setValue(int) will return the string representation 
// * of the integer. 
// * <p>
// * The actual value is stored as Object. For example as an Integer or Double object. 
// * 
// * @author P.T. de Boer
// */
//
//public class VAttribute extends Attribute 
//{
//    private static final long serialVersionUID = 6729623290452678587L;
//
//
//// ========================================================================
//// Class Methods 
//// ========================================================================
//    
//    public static VAttribute createFromAssignment(String stat)
//    {
//        String strs[] = stat.split("[ ]*=[ ]*");
//
//        if ((strs == null) || (strs.length < 2))
//            return null;
//
//        // parse result 
//        return createFromString(VAttributeType.STRING,strs[0], strs[1]);
//    }
//    
//    public static VAttribute createEnumerate(String name, String values[],
//            String value)
//    {
//        return new VAttribute(name, values, value);
//    }
//
////    /**
////     * Create From {Name,Type,Value} triple.
////     * Type string must match on of VVAttributeType !
////     */
////    public static VAttribute createFrom(String type, String name, String value)
////    {
////        VAttributeType aType=VAttribute.getTypeFromName(type);
////        return createFromString(aType,name,value); 
////    }
//
////    public static VAttributeType getTypeFromName(String type)
////    {
////        // backward compatibility: 
////        if ( (type.equalsIgnoreCase("time"))   
////             ||(type.equalsIgnoreCase("datetime")) 
////             ||(type.equalsIgnoreCase("timedate")) 
////             ||(type.equalsIgnoreCase("date")) )
////        {
////            return VAttributeType.TIME;  
////        }
////       
////        return  VAttributeType.valueOf(VAttributeType.class,type);
////    }
//    
//    /**
//     * Create DateTime Attribute from nr of millis since Epoch. */
//    public static VAttribute createDateSinceEpoch(String name, long millis)
//    {
//        // store as normalized time string: 
//        String timeStr = Presentation.createNormalizedDateTimeString(millis);
//        return new VAttribute(VAttributeType.TIME, name, timeStr);
//    }
////  
//    /**
//     * Create DateTime Attribute from Unix Time using Milli Seconds !  
//     */
//    public static VAttribute createUnixTimeAttribute(String name, long millis)
//    {
//        return createDateSinceEpoch(name,millis); 
//    }
//    
//    // === Factory Methods: 
//    
//    public static VAttribute createFromString(VAttributeType type,String name,String valueAsString)
//    {
//        VAttribute attr=new VAttribute(name);
//        attr.setValueFromString(type,valueAsString);
//        return attr;
//    }
//    
//    public static VAttribute create(String name, long val)
//    {
//        return new VAttribute(name,val);  
//    }
//    
//    public static VAttribute create(String name, boolean val)
//    {
//        return new VAttribute(name,val); 
//    }
//
//    public static VAttribute createFrom(String name, String stringValue)
//    {
//        return createFromString(VAttributeType.STRING,name,stringValue); 
//    }
//    
//    public static VAttribute createFrom(String name, Object value)
//    {
//        VAttributeType type=VAttributeType.getObjectType(value,null);
//        if (type!=null)
//            return new VAttribute(type,name,value); 
//        // default to string type: 
//        return new VAttribute(VAttributeType.STRING,name,value.toString()); 
//    }
//    
//    public static VAttribute createFrom(VAttributeType type, String name, Object value)
//    {
//        return new VAttribute(type,name,value); 
//    }
//    
//    /** Create a deep copy of Attribute Array */
//    public static VAttribute[] duplicateArray(VAttribute[] attrs)
//    {
//        if (attrs == null)
//            return null;
//
//        VAttribute newAttrs[] = new VAttribute[attrs.length];
//
//        for (int i = 0; i < attrs.length; i++)
//        {
//            if (attrs[i] != null)
//                newAttrs[i] = attrs[i].duplicate();
//            else
//                newAttrs[i] = null; // be robuust: accept null attributes 
//        }
//
//        return newAttrs;
//    }
//
//    // ========================================================================
//    // Fields
//    // ========================================================================
//
//    private String helpText;
//
//    // ========================================================================
//    // Instance
//    // ========================================================================
//    
//    /** Copy Constructor */
//    public VAttribute(VAttribute source)
//    {
//        super(source.getName()); 
//        copyFrom(source);   
//    }
//    
//    /** DownCast! Constructor */
//    public VAttribute(Attribute source)
//    {
//        super(source.getName()); 
//        copyFrom(source);   
//    }
//    
//    protected VAttribute(String name)
//    {
//        super(name); 
//    }
//    
//    public VAttribute(String name, Boolean val)
//    {
//        super(VAttributeType.BOOLEAN,name,val); 
//    }
//    
//    public VAttribute(String name, short val)
//    {
//        super(VAttributeType.INT,name, val); 
//    }
//    
//    public VAttribute(String name, byte val)
//    {
//        super(VAttributeType.INT,name, val); 
//    }
//    
//    public VAttribute(String name, Integer val)
//    {
//        super(VAttributeType.INT,name, val); 
//    }
//    
//    public VAttribute(String name, Long val)
//    {
//        super(VAttributeType.LONG,name,val); 
//    }
//    
//    public VAttribute(String name, Float val)
//    {
//        super(VAttributeType.FLOAT,name,val); 
//    }
//    
//    public VAttribute(String name, Double val)
//    {
//        super(VAttributeType.DOUBLE,name,val); 
//    }
//    
//    public VAttribute(VAttributeType type,String name, Object value)
//    {
//        super(type,name,value);  
//    } 
//    
//    public VAttribute(String name, String[] vals, int index)
//    {
//        super(name,vals,index); 
//    }
//    
//    public VAttribute(String name, String[] vals, String enumValue)
//    {
//        super(name,vals,enumValue); 
//    }
//
//    public VAttribute(String name,String value) 
//    {
//        super(VAttributeType.STRING,name,value);
//    }
//    
//    public VAttribute(String name, VRL vrl)
//    {
//        super(VAttributeType.STRING,name,vrl.toString()); 
//    }
//    
//    public VAttribute(String name, StringList list)
//    {
//        super(VAttributeType.STRING,name,list.toString(",")); 
//    }
//    
//    public VAttribute(String name, URI uri)
//    {
//        super(VAttributeType.STRING,name,uri.toString()); 
//    }
//    
//    public VAttribute(VAttributeType type, String name, long longValue)
//    {
//        super(type,name,new Long(longValue));
//    }
//
//    public VAttribute(String name, Date value)
//    {
//        super(name,value); 
//    }
//
//    public VAttribute duplicate()
//    {
//        return new VAttribute(this);  
//    }
//    
//    protected void copyFrom(VAttribute source)
//    {
//        super.copyFrom(source);
//        //
//        if (source.helpText!=null)
//            this.helpText=new String(source.helpText); // new object! 
//    }
//    
//    // convert Attribute's Object value to String value;  
//    public Object getValue()
//    {
//        return getStringValue(); 
//    }
//
//    public Object toObject()
//    {
//        return super.getValue(); 
//    }
//
//    public VRL getVRL() // throws VlURISyntaxException
//    {
//        return getVRL(); 
//    }
//    
//    public void forceSetValue(String strval)
//    {
//        this.setValueFromString(getType(),strval);  
//    }
//    
//    public String getHelpText()
//    {
//        return helpText;
//    }
//    
//    /**
//     * return true whether object is an actual VRL or the String representation is a VRL.  
//     */
//    public boolean isVRL()
//    {
//        if (isType(VAttributeType.VRL))
//            return true;
//        // backwards compatibility: if String Value is VRL then it is a VRL 
//        return VRL.isVRL(getStringValue()); 
//    }
//
//    public void setValue(String strVal)
//    {
//        this.setValueFromString(getType(),strVal); 
//    }
//
//    public void setHelpText(String text)
//    {
//        this.helpText=text; 
//    }
//
//}
////