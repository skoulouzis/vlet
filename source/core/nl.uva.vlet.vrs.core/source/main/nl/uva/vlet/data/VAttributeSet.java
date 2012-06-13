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
 * $Id: VAttributeSet.java,v 1.9 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.xml.XMLData;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlXMLDataException;
import nl.uva.vlet.vrl.VRL;

/**
 *  A VAttributeSet is implemented as an OrdenedHashtable with extra
 *  set manipulation methods.
 *  Note that the order of the entries in the Hashtable is now kept since 
 *  this class is (now) a subclass of OrdenedHashtable (custom data type).
 *  <p>
 *  About the he set() methods:<br>
 *  The set methods only add a new value (using put) to the Hashtable if 
 *  the VAttribute object entry wasn't stored yet. 
 *  If the VAttribute object already exists, the Value of that VAttribute will
 *  be changed, keeping the original VAttribute object in the Set.
 *  The VAttribute has to be Editable.<br>
 *  This way it is possible to keep references to the stored VAttribute for
 *  advanced manipulation methods.   
 *  
 *  @see OrdenedHashtable
 */

public class VAttributeSet extends OrdenedHashtable<String,VAttribute> 
    implements Serializable, Cloneable, Iterable<VAttribute>,Duplicatable<VAttributeSet>
{
    // ========================================================================
    // Class
    // ========================================================================
    
    // = serializable 
    private static final long serialVersionUID = -4407735257687000157L;

    public static final String ATTR_SETNAME = "setName";
         
    private static ClassLogger logger=null; 
    
    static
    {
        logger=ClassLogger.getLogger(VAttributeSet.class); 
    }
    
    /** Create VAttributeSet from Properties */ 
    public static VAttributeSet createFrom(Properties properties)
    {
        return new VAttributeSet(properties); 
    }

    /**
     * Create new set by reading from inputstream. 
     * @throws VlXMLDataException 
     */ 
    public static VAttributeSet readFromXMLStream(InputStream inps) throws VlXMLDataException
    {
        XMLData data=new XMLData();
        VAttributeSet attrSet = data.parseVAttributeSet(inps);
        return attrSet; 
    }

    /**@deprecated: Will be replaced by readFromXMLStream  */ 
    public static VAttributeSet readFrom(InputStream inps) throws VlIOException
    {
        VAttributeSet aset=new VAttributeSet();
        aset.loadOld(inps); 
        return aset; 
    }
    
    public static Vector<VAttribute> createVector(VAttribute[] attrs)
    {
        return new VAttributeSet(attrs).toVector(); 
     }
    
    public static Vector<Object> createObjectVector(VAttribute[] attrs)
    {
        return new VAttributeSet(attrs).toObjectVector();
    }

    // ========================================================================
    // Instance
    // ========================================================================
    
    /** Optional set Name */ 
    protected String setName=""; 
    
    // List<?> also matches Vector and ArrayList !  
    protected void init(List<VAttribute> attrs)
    {
       if (attrs==null)
       {
           this.clear(); 
           return; // empty set
       }
        
       for (VAttribute attr:attrs)
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr.getName(),attr); 
    }
    
    protected void init(VAttribute[] attrs)
    {
        if (attrs==null)
        {
            this.clear();
            return; // empty set
        }
        
       for (VAttribute attr:attrs)
       { 
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr); 
       }
    }
    
    public VAttributeSet()
    {
        super(); //empty hastable     
    }
    
    /** Named Attribute Set */ 
    public VAttributeSet(String name)
    {
      super(); //empty hastable
      this.setName=name; 
    }
        
    /**
     * Create from Vector. Duplicate entries
     * are overwritten. Last entry is kept.
     */ 
    public VAttributeSet(Vector<VAttribute> attrs)
    {
        init(attrs); 
    }

    public VAttributeSet(String nname, VAttribute[] attrs)
    {
        setName(nname); 
        init(attrs); 
    }
    
    /**
     * Constructs an VAttributeSet from the Map. 
     * Note that VAttributeSet is a map as well, so this contructor
     * can be used as an Copy Constructor. 
     * 
     * @param map source map. 
     */
    public VAttributeSet(Map<? extends Object,? extends Object> map)
    {
        init(map);
    }
    
    /*public VAttributeSet(Map map,boolean deepcopy)
    { 
        init(map,deepcopy);
    }*/
   
    /**
     *  Create attribute set from generic <Key,Value> Map.   
     *  As key value, the STRING representation of the Key object
     *  is used. 
     *  As value the VAttribute factory createFrom(object) is used.
     *  @see nl.uva.vlet.data.VAttribute#createFrom(String, Object) 
     */
    private void init(Map<? extends Object,? extends Object> map)
    {
         //int index=0; 
         
         Set<? extends Object> keys =map.keySet();

         for (Iterator<? extends Object> iterator = keys.iterator(); iterator.hasNext();) 
         {
            Object key = iterator.next();
            // Use STRING representation of Key Object ! 
            String keystr=key.toString(); 
            Object value=map.get(key);
            
            VAttribute attr; 
            if (value instanceof VAttribute)
            {
                attr=((VAttribute)value).duplicate();
            }
            else
            {
                // Use VAtribute Factory: 
                attr = VAttribute.createFrom(keystr,value);
            }
            
            this.put(attr); 
            //index++; 
         }
    }
    
    // ========================================================================
    // Getters/Setters
    // ========================================================================
  
    /** Sets optional name. null name is allowed */ 
    public void setName(String newName)
    {
        setName=newName; 
    }

    /** Returns optional name. Can be null */
    public String getName()
    {
        return setName; 
    }
    
    /**
     * Ordened Put.
     * This method will add the attribute to the hashtable 
     * and keep the order in which it is put. 
     * If the attribute already has been added the order
     * will be kept. 
     */  
    public void put(VAttribute attr)
    {
    	if (attr==null)
    	{
    		Global.warnPrintf(this,"Attribute is NULL!\n");
    		return; 
    	}
        this.put(attr.getName(),attr); 
    }
    
    /** Combined put() and setEditable() */ 
    public void put(VAttribute attr, boolean editable)
    {
    	attr.setEditable(editable); 
    	this.put(attr); 
    }
    
    public void set(VAttribute attribute)
    {
        this.put(attribute); 
    }
    
    /**
     * Create from Array. Duplicate entries
     * are overwritten. Last entry is kept, NULL entries are skipped. 
     */ 
    public VAttributeSet(VAttribute[] attrs)
    {
        if (attrs==null) 
            return; // empty set 
        
       for (VAttribute attr:attrs)
           // filter bogus attributes! 
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr); 
    }

    @Override
    public VAttributeSet duplicate(boolean shallow)
    {
        VAttributeSet newset = new VAttributeSet(this);
        newset.setName(this.getName()); 
        newset.setKeyOrder(this.getOrdenedKeyArray(), true); 
        
        return newset;
    }
    @Override
    public VAttributeSet duplicate()
    {
        return duplicate(false); 
    }

    @Override
    public boolean shallowSupported() 
    {
        return false;
    }
    
    /** Returns array of attribute names of the key set. */
    public String[] getAttributeNames()
    {
        return this.getOrdenedKeyArray(); 
    }

    /**
     * Returns Object value of Attribute with name 'name'
     * Returns null if the attribute is not in the set. 
     */ 
    public Object getValue(String name)
    {
        VAttribute attr=get(name);
        
        if (attr==null) 
        {
            logger.debugPrintf("*** Warning: null attribute for:%s\n",name); 
            return null;
        }
        else
        {
            logger.debugPrintf("Returning:%s=%s\n",name,attr.getValue());
        }
        
        return attr.getValue();
    }

    /**
     * Returns String value of Attribute with name 'name'
     * Returns null if the attribute is not in the set. 
     */ 
    public String getStringValue(String name)
    {
        VAttribute attr=get(name);
        
        if (attr==null) 
        {
            logger.debugPrintf("*** Warning: null attribute for:%s\n",name); 
            return null;
        }
        else
        {
            logger.debugPrintf("Returning:%s=%s\n",name,attr.getStringValue());
        }
        
        return attr.getStringValue();
    }
    
    /** 
     * Returns String value of Attribute 
     * @param defVal default value if attribute is not in this set */ 
    public int getIntValue(String name, int defVal)
    {
        VAttribute attr=get(name);
        
        if (attr==null) 
            return defVal;
        
        return attr.getIntValue();
    }
    
    public long getLongValue(String name,long defVal)
    {
        VAttribute attr=get(name);
            
        if (attr==null) 
            return defVal;
            
        return attr.getLongValue();
    }
    
    public VRL getVRLValue(String name) throws VRLSyntaxException
    {
        VAttribute attr=get(name);
        
        if (attr==null) 
            return null;
            
        return attr.getVRL(); 
    }
    
    /**
     * Returns String value of Attribute 
     * @param defVal default value if attribute is not in this set */ 
    public int getIntValue(String name)
    {
        VAttribute attr=get(name);
        
        if (attr==null) 
            return -1; 
        
        return attr.getIntValue();
    }
    
    public long getLongValue(String name)
    {
        VAttribute attr=get(name);
            
        if (attr==null) 
            return -1; 
            
        return attr.getLongValue();
    }
    
    /**
     * Helper method used by the set() methods. 
     * Returns old value. 
     */ 
    private Object _set(VAttributeType optNewType, String name, Object val)
    {
        VAttribute orgAttr = this.get(name); 
        
        if (orgAttr==null)
        {
            // set: put new Editable Attribute with specified type: 
            VAttribute attr = VAttribute.createFrom(optNewType,name,val);
            attr.setEditable(true);
            this.put(attr); 
            return null; 
        }
        else
        {
            Object oldValue=orgAttr.getValueObject(); 
            
            // Update value only. 
            orgAttr.setValue(val); 
            
            /** Return Value as Object which type matches the VAttribute Type */ 
            return oldValue; 
        }
    }
    
    /**
     * Set Attribute Value. Returns previous value if any.  
     * The difference between put and set is that this method changes the 
     * stored Attribute in the hashtable by using VAttribute.setValue(). 
     * It does NOT put a new VAttribute into the hashtable. <br>
     * This means the already stored VAttribute has to be editable !
     * This way the 'changed' flag is updated from the VAttribute. 
     * If the named attribute isn't stored, a new attribute will be created
     * and the behaviour is similar to put().  
     */
    public String set(String name, String val) 
    {
        String oldvalue=getStringValue(name); 
        _set(VAttributeType.STRING,name,val);
        return oldvalue; 
    }
    
    public void set(String attrName, boolean val)
    {
        _set(VAttributeType.BOOLEAN,attrName,new Boolean(val)); 
    }
    
    public void set(String attrName, int val) 
    {
        _set(VAttributeType.INT,attrName,new Integer(val)); 
    }
    
    public void set(String attrName, long val)
    {
        _set(VAttributeType.LONG,attrName,new Long(val));
    }
    
    public void set(String attrName, VRL vrl) 
    {
    	_set(VAttributeType.VRL,attrName,vrl);
    }
    
    public boolean getBooleanValue(String name,boolean defaultValue)
    {
       VAttribute attr=get(name);
            
       if (attr==null) 
          return defaultValue;  
            
       return attr.getBooleanValue();
    }
//    /**
//     * Old method to store VAttributeSets are flat property files with 
//     * extra type information (%type and %enum).
//     * 
//     * @deprecated. Will switch to storeAsXML soon !
//     */ 
//    public void store(OutputStream outp, String comments) throws VlIOException
//    {
//        //storeAsXML(outp,comments);
//         storeOld(outp,comments); 
//    }
    /**
     * as XML file. 
     */
    public void storeAsXML(OutputStream outp, String comments) throws VlXMLDataException
    {
        XMLData xmlData=new XMLData();
        xmlData.writeAsXML(outp,this,comments); 
    }
   
        
    /**
     * Read VAttributeSet from InputStream. 
     * Uses Properties.
     * @see #Properties.load(InputStream)
     */
    public void loadOld(InputStream inps) throws VlIOException
    {
        this.clear();
        
        if (inps==null)
        {
            throw new VlIOException("NULL inputstream:"+inps); 
        }
        
        Properties props=new Properties(); 
        
        try
        {
            props.load(inps);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new VlIOException("IOException:"+e,e);  
        }
        
        setName=props.getProperty(ATTR_SETNAME); 
        
        for (Enumeration<Object> keys =props.keys();keys.hasMoreElements();) 
        {
            String name=(String)keys.nextElement(); 
            String value=props.getProperty(name); 
            if ((name.endsWith("%type")==false) && (name.endsWith("%enumValues")==false))
            {
                String type=props.getProperty(name+"%type");
                  
                //optional editable type
                //boolean editable=true; 
                //String editstr=props.getProperty(name+"%isEditable");
                //if (editstr!=null)
                //     if (editstr.compareToIgnoreCase("false")==0)
                //         editable=false;
                        
                if (type!=null)
                {
                   VAttributeType atype=VAttributeType.valueOf(VAttributeType.class,type); 
                
                   if (atype==VAttributeType.ENUM) 
                   {
                       String str=props.getProperty(name+"%enumValues");
                       
                       if (str!=null)
                       {
                         String vals[]=str.split(",");  
                        
                         put(new VAttribute(name,vals,value));
                       }
                   }
                   else
                   {
                      put(new VAttribute(atype,name,value));
                   }
                }
                else
                {
                   Global.warnPrintf(this,"Error reading property type:%s\n",name);
                }
            }
        }
        
        // update stored order: 
        // Backwards compatibility: Might not be present!
        // 
        
        String str=(String)props.get("%attributeOrder");
        if (str!=null)
        {
            // reorder keynames and be strict ! 
            this.setKeyOrder(StringList.createFrom(str,",").toArray(),true);
        }
        else
            logger.debugPrintf(">>> Warning: attributeOrder is not present");
    }

    public String toString()
    {
        VAttribute[] attrs = toArray(); 
    
        String str = "VAttributeSet:"+this.setName+":{";
        
        if (attrs!=null)
            for (int i=0;i<attrs.length;i++)
            {
                str+=attrs[i]+(i<(attrs.length-1)?",":"");
            }
        
        str+="}";
        
        return str; 
    }
    
   /** Creates deep copy */ 
    public VAttributeSet clone()
    {
        return duplicate(); 
    }
    
    /**
     * Stored new String Attribute, replacing already stored
     * VAttribute if it already exists. 
     * @see #set(String, String) Use set() method to keep already stored VAttributes.
     */  
    public void put(String name,String value) 
    {
    	put(new VAttribute(name,value));
    }
    
    /**
     * Stored new Integer Attribute, replacing already stored
     * VAttribute if it already exists. 
     * @see #set(String, int) Use set() method to keep already stored VAttributes.
     */  
    public void put(String attrName, int val)
    {
        put (new VAttribute(attrName,val)); 
    }
    
     /**
     * Stored new boolean Attribute, replacing already stored
     * VAttribute if it already exists. 
     * @see #set(String, boolean) Use set() method to keep already 
     *      stored VAttributes.
     */  
    public void put(String attrName, boolean val)
    {
        put (new VAttribute(attrName,val)); 
    }

    /** Returns changed attributes as array */ 
    public synchronized VAttribute[] getChangedAttributesArray()
    {
        int numChanged=0; 
        int index=0; 
        
        for (int i=0;i<this.size();i++)
            if (this.elementAt(i).hasChanged()==true)
                numChanged++;
        
        VAttribute attrs[]=new VAttribute[numChanged];
        
        for (int i=0;i<this.size();i++)
            if (this.elementAt(i).hasChanged()==true)
                attrs[index++]=this.elementAt(i);
        
        return attrs; 
    }

    /** Set Editable flag of attribute */ 
    public void setEditable(String name, boolean val)
    {
        VAttribute attr = this.get(name);
        
        if (attr==null)
            return;
        
        attr.setEditable(val); 
    }

    public StringList getOrdenedKeyList()
    {
        return new StringList(this.getOrdenedKeyArray());
    }

    /**
     * De-Generalized remove method (downcast) 
     * for strict type matching
     */  
    public VAttribute remove(String name)
    {
        return super.remove((Object)name); 
    }

    /** Remove attribute if name isn't in the key list */ 
    public void removeIfNotIn(StringList keylist)
    {
        StringList names=this.getOrdenedKeyList(); 

        // match current attribute against newlist; 
        for (String name:names)
        {
            if (keylist.contains(name)==false)
            {
                this.remove(name); 
            }
        }
    }

   /**
    * Match this VAttributeSet with template set. 
    * If attribute in templateSet is not in this set, it will be copied.   
    * If attribute exists in this set, it's type and flags will be copied, but
    * not the actual value unless it is NULL or empty. 
    * This allows updating of VAttributeSet while keeping their value. 
    * Set boolean remoteOthers to true to remove attribute not in the template set. 
    * Is used by ServerInfo to update Server Attributes. 
    */ 
   public void matchTemplate(VAttributeSet templateSet, boolean removeOthers)
   {
       StringList names=templateSet.getOrdenedKeyList(); 

       for (String name:names)
       {
          if (this.containsKey(name)==false) 
          {
              this.put(templateSet.get(name)); 
          }
          else
          {
              // update attribute type and attribute flags. 
              // just copy old value into new Attribute 
              VAttribute newAttr=templateSet.get(name); 
              VAttribute oldAttr=get(name);
              // force set: 
              Object oldVal=oldAttr.getValue();
              
              if ((oldVal!=null) && StringUtil.isEmpty(oldVal.toString())==false)
              {
                  // update value, but use new type (and attributes) 
                  // this will cause an implicit cast from old value to new value! 
                  newAttr.setValueFrom(oldAttr); // (oldAttr.getValueObject()); 
              }
              // else keep new non empty value ! 
              // Overwrite:
              put(newAttr); 
          }
       }

       if (removeOthers) 
           removeIfNotIn(names); 
   }

   /** Returns sub set of attributes */ 
   public VAttributeSet getAttributes(String[] names)
   {
       VAttributeSet subset=new VAttributeSet(); 
       for (String name:names)
       {    
           VAttribute attr=this.get(name);
            if (attr!=null)
                subset.put(attr.duplicate()); 
       }
       return subset; 
   }

   public void add(VAttribute[] attrs)
   {
       for (VAttribute attr:attrs)
            this.put(attr); 
   }


 

}

