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
 * $Id: LFCFSConfig.java,v 1.2 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlConfigurationError;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class LFCFSConfig
{
    public static final String ATTR_PREFERREDSSES = "listPreferredSEs";

    public static final String ATTR_GENERATED_DIRNAME = "generatedDirname";

    public static final String ATTR_GENERATED_SUBDIR_DATE_SCHEME = "subDirDateScheme";
    
    public static final String ATTR_REPLICA_NAME_CREATION_POLICY= "replicaNamePolicy";
    
    public static final String ATTR_REPLICA_NR_OF_TRIES = "replicaNrOfTries";
 
    public static final String REPLICA_NAME_POLICY_RANDOM="Random"; 
    
    public static final String REPLICA_NAME_POLICY_SIMILAR="Similar"; 
    
    public static final String REPLICA_NAME_POLICIES[] = 
        {
            REPLICA_NAME_POLICY_SIMILAR,// entry[0] is default !
            REPLICA_NAME_POLICY_RANDOM
        }; 
    
    public static final String[] DEFAULT_LIST_PREFERRED_SES=
        {
            "srm.grid.sara.nl",
            "tbn18.nikhef.nl"
        };
    
    // === Default Values ===
    
    public static final String DEFAULT_GENERATED_DIRNAME_VALUE="vletgenerated"; 

    /**
     * See java.text.SimpleDateFormat  
     */
    public static final String DEFAULT_GENERATED_SUBDIR_DATE_SCHEME="yyyy-MM-dd";

    public static final String ATTR_REPLICA_SELECTION_MODE = "replicaSelectionMode";
    
    public static final String ATTR_REPLICA_CREATION_MODE = "replicaCreationMode"; 
    
    public static enum ReplicaSelectionMode
    {
        // use attribute value for string comparison instead of Enum Value !
        PREFERRED("Preferred"),
        PREFERRED_RANDOM("PreferredRandom"),
        ALL_SEQUENTIAL("AllSequential"),
        ALL_RANDOM("AllRandom");
        
        String attrValue; 
        
        private ReplicaSelectionMode(String attrVal)
        {
            this.attrValue=attrVal;
        }
        
        public String getValue()
        {
            return this.attrValue; 
        }
        
        public static ReplicaSelectionMode createFromAttributeValue(String valstr) throws VlConfigurationError
        {
            for (ReplicaSelectionMode mode:ReplicaSelectionMode.values())
            {
                if (StringUtil.equals(valstr,mode.getValue()))
                    return mode; 
            }
            
            throw new nl.uva.vlet.exception.VlConfigurationError("Invalid Replica Selection Mode:"+valstr);
        }
    }
    
    public static enum ReplicaCreationMode
    {
        // use attribute value for string comparison instead of Enum Value !
        PREFERRED("Preferred"),
        PREFERRED_RANDOM("PreferredRandom"),
        //DEFAULT_VO("DefaultVO"),
        VORANDOM("DefaultVORandom");
        
        String attrValue; 
        
        private ReplicaCreationMode(String attrVal)
        {
            this.attrValue=attrVal;
        }
        
        public String getValue()
        {
            return this.attrValue;  
        }
        
        public static ReplicaCreationMode createFromAttributeValue(String valstr) throws VlConfigurationError
        {
            for (ReplicaCreationMode mode:ReplicaCreationMode.values())
            {
                if (StringUtil.equals(valstr,mode.getValue()))
                    return mode; 
            }
            
            throw new nl.uva.vlet.exception.VlConfigurationError("Invalid Replica Creation Mode:"+valstr);  
        }
    }

    // default value; 
    private static int replicaSelectionModeDefault=3;  

    public static final String[] REPLICAS_SELECTIONMODE_VALUES = 
        {
            ReplicaSelectionMode.PREFERRED.getValue(), // first is default  
            ReplicaSelectionMode.PREFERRED_RANDOM.getValue(),   
            ReplicaSelectionMode.ALL_SEQUENTIAL.getValue(),  
            ReplicaSelectionMode.ALL_RANDOM.getValue()
        };

    // default value; 
    private static int replicaCreationModeDefault=2;  
    
    public static final String[] REPLICAS_CREATIONMODE_VALUES = 
        {
            ReplicaCreationMode.PREFERRED.getValue(), // first is default  
            ReplicaCreationMode.PREFERRED_RANDOM.getValue(),  
            //ReplicaCreationMode.DEFAULT_VO.getAttrValue(),
            ReplicaCreationMode.VORANDOM.getValue()
        };

    public static VAttributeSet createDefaultServerAttributes(VRSContext context, VAttributeSet uriAttrs)
        {
           VAttributeSet set=new VAttributeSet(); 
           VAttribute attr=null;
           
           
           //Spiros non read variable 
//           ConfigManager confMan=context.getConfigManager();
           
           // ===
           // initialize with hardcoded defaults
           // === 
           
           set.put(attr=new VAttribute(VAttributeConstants.ATTR_HOSTNAME,"LFCHOST")); 
           attr.setEditable(true);
           
           set.put(attr=new VAttribute(VAttributeConstants.ATTR_PORT,5010)); 
           attr.setEditable(true); 
          
           //use configuration property
           String val=context.getStringProperty("lfc."+ATTR_PREFERREDSSES);
           attr=new VAttribute(ATTR_PREFERREDSSES,val);
            
           attr.setEditable(true); 
           set.put(attr); 
    
           //String modes[]={"AlwaysFirst,MatchPreferredFirst,MatchPreferredInOrder,Random,Parralel"}; 
           String modes[]=REPLICAS_SELECTIONMODE_VALUES; 
           attr=new VAttribute(ATTR_REPLICA_SELECTION_MODE,modes,replicaSelectionModeDefault);  
           attr.setEditable(true); 
           set.put(attr);
           
           modes=REPLICAS_CREATIONMODE_VALUES; 
           attr=new VAttribute(ATTR_REPLICA_CREATION_MODE,modes,replicaCreationModeDefault);  
           attr.setEditable(true); 
           set.put(attr);
           
           set.put(attr=new VAttribute(LFCFSConfig.ATTR_REPLICA_NR_OF_TRIES,5)); 
           attr.setEditable(true);
           
           attr=new VAttribute(ATTR_GENERATED_SUBDIR_DATE_SCHEME,
                                       DEFAULT_GENERATED_SUBDIR_DATE_SCHEME);
           attr.setEditable(false); // not editable for now ! 
           set.put(attr); 
    
           attr=new VAttribute(ATTR_GENERATED_DIRNAME,DEFAULT_GENERATED_DIRNAME_VALUE);
           attr.setEditable(false); // not editable for now ! 
           set.put(attr); 
                
           modes=LFCFSConfig.REPLICA_NAME_POLICIES;  
           attr=new VAttribute(ATTR_REPLICA_NAME_CREATION_POLICY,modes,0);  
           attr.setEditable(true); 
           set.put(attr);

    //
    //       attr=new VAttribute("replicaSelectionNrOfTries",3);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("replicaSelectionTimeOut",5);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       String modes[]={"PreferredInOrder,PreferredRandom,VOAllowedFirst,VOAllowedInOrder,VOAllowedRandom"};
    //       attr=new VAttribute("replicaCreationMode",modes,0);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("defaultNrOfReplicas",3);  
    //       attr.setEditable(true); 
    //       set.put(attr); 
    //
    //       attr=new VAttribute("replicaAutoUseSRMV22URI",true); 
    //       attr.setEditable(true); 
    //       set.put(attr); 
    
           // update default from Context and optional URI Attribute
           // Overriding hard coded defaults ! 
           for (String key:set.keySet()) 
           {
               VAttribute orgAttr=set.get(key); 
    
               // check global and context properties: 
               Object obj=context.getProperty("lfc."+key); 
               
               if (obj!=null) 
               {
                   orgAttr.setValue(obj.toString()); // store as String.  
                   Global.infoPrintln(LFCFSConfig.class,"Using context property:"+key+"="+obj); 
               }
               
               // get attribute from optional URI attribute set:  
               VAttribute uriAttr=null; 
               if (uriAttrs!=null)
               {
                      uriAttr=uriAttrs.get("lfc."+key);
               }
               
               // get attribute from context: 
               if (uriAttr!=null)
               {
                   Global.infoPrintln(LFCFSConfig.class,"Using URI attribute:"+key+"="+obj); 
                   orgAttr.setValue(uriAttr.getValue());
               }
           }
           // return updated set: 
           return set; 
        }

    public static void updateURIAttributes(ServerInfo lfcInfo, VAttributeSet uriAttrs)
    {
        if ((uriAttrs==null) || (lfcInfo==null))
            return; 
        
     // Overriding hard coded defaults ! 
        for (String key:lfcInfo.getAttributeNames()) 
        {
            VAttribute orgAttr=lfcInfo.getAttribute(key); 
            
            // get attribute from optional URI attribute set:  
            VAttribute uriAttr=uriAttrs.get("lfc."+key);
            
            // get attribute from context: 
            if (uriAttr!=null)
            {
                Global.infoPrintln(LFCFSConfig.class,"Using URI attribute:"+key+"="+uriAttr); 
                orgAttr.setValue(uriAttr.getValue());
            }
        }
    }

    public static ReplicaSelectionMode string2ReplicaSelectionMode(String value) throws VlConfigurationError
    {
        return ReplicaSelectionMode.createFromAttributeValue(value); 
    }
    
    public static ReplicaCreationMode string2ReplicaCreationMode(String value) throws VlConfigurationError
    {
        return ReplicaCreationMode.createFromAttributeValue(value); 
    }

    /** convenience method to check whether property string is on of the "Preferred" modes */ 
    public static boolean isPreferredMode(String valstr)
    {
        if (valstr==null)
            return false; 
        
        try
        {
            ReplicaSelectionMode mode = string2ReplicaSelectionMode(valstr);
            switch(mode)
            {
                case PREFERRED:
                case PREFERRED_RANDOM:
                    return true;
                default: 
                    return false;  
            }
        }
        catch (VlConfigurationError e)
        {
            // e.printStackTrace();
        }
        
        try
        {
            ReplicaCreationMode mode = string2ReplicaCreationMode(valstr);
            switch(mode)
            {
                case PREFERRED:
                case PREFERRED_RANDOM:
                    return true;
                default: 
                    return false;  
            }
        }
        catch (VlConfigurationError e)
        {
            // e.printStackTrace();
        } 
     
        return false; 
        
    }

}
