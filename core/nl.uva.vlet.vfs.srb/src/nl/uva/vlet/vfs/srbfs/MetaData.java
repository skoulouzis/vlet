/*
 * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: MetaData.java,v 1.2 2011-06-01 12:05:03 ptdeboer Exp $  
 * $Date: 2011-06-01 12:05:03 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;



import java.io.PrintStream;
import java.util.Hashtable;

import nl.uva.vlet.Global;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;
import static nl.uva.vlet.data.VAttributeConstants.*;
/**
 *  Experimental MetaData class 
 */ 

public class MetaData
{
    /**
     * Map between VAttribute names and METADATA names. 
     * Sadly not all names used in the MetaData Groups can be resused! 
     * For example MODIFICIATION_DATE is defined as "modification date" with 
     * a SPACE in it. The VAttribute names cannot have spaces in them since
     * it must be possible to use them as Parameter Names:  Query="modificationTime&lt;1000"  
     * Also some names MetaData names are ambigious (Dirname!) and some VAttributes are
     * aliases (modification time as 'String' vs modification time as 'long' !) 
     * Also beware that directories don't have metada attributes, thus a query
     * for (sub)directories with metadata will result in a null result.  
     **/ 
    private static String fileAttributeNameMap[][]=
       {
            // SRB MetaData attribute name    |  VRS Attribute Name        //
            // -------------------------------|------------------------------
            { MetaDataSet.FILE_NAME           ,ATTR_NAME }, 
            // SRBPATH ??? { SRBMetaDataSet.PATH_NAME              ,VFSNode.ATTR_PATH }, 
            
            // Parent dir of files:
            { SRBMetaDataSet.PARENT_DIRECTORY_NAME ,ATTR_PARENT_DIRNAME }, 
            { MetaDataSet.DIRECTORY_NAME      ,ATTR_DIRNAME }, 
            { MetaDataSet.SIZE                ,ATTR_LENGTH }, 
            { MetaDataSet.MODIFICATION_DATE   ,ATTR_MODIFICATION_TIME },
            //{ MetaDataSet.MODIFICATION_DATE   ,ATTR_MODIFICATION_TIME_STRING },
            { MetaDataSet.CREATION_DATE       ,ATTR_CREATION_TIME },
            //{ MetaDataSet.CREATION_DATE       ,ATTR_CREATION_TIME_STRING },
            { MetaDataSet.OWNER               ,ATTR_OWNER},
            { SRBMetaDataSet.RESOURCE_NAME    ,SrbConfig.ATTR_RESOURCE},
            { SRBMetaDataSet.USER_NAME        ,ATTR_USERNAME},
            { SRBMetaDataSet.FILE_TYPE_NAME   ,ATTR_DATA_TYPE}
       };
    
    // class object
    private static MetaData classObject=new MetaData(); 
    
    // *** 
    // 
    // *** 
    
    private Hashtable<String, String> attributeName2MetaDataName = new Hashtable<String, String>();
    
    private Hashtable<String, String> metaDataName2AttributeName = new Hashtable<String, String>();

    
    // Class Initializer: 
    private void initFileAttributeMaps()
    {
        Debug("Initializing SRB File Meta Data Attribute map"); 
                
        for (int i=0;i<fileAttributeNameMap.length;i++)
        {
            String srbname=fileAttributeNameMap[i][0];
            String vrsname=fileAttributeNameMap[i][1]; 
                                                     
            metaDataName2AttributeName.put(srbname,vrsname);
            attributeName2MetaDataName.put(vrsname,srbname); 
        }
    }
    
    private MetaData()
    {
        initFileAttributeMaps(); 
    }
    
    
    // *** 
    // Class Misc. 
    // ***
    
    
    private static void Debug(String msg)
    {
        Global.debugPrintln(MetaData.class,msg); 
    }
    
   public static void printMaps(PrintStream out)
   {
       for (int i=0;i<MetaData.fileAttributeNameMap.length;i++)
       {
           out.println("nameMap["+i+"]="+MetaData.fileAttributeNameMap[i][0]+","+MetaData.fileAttributeNameMap[i][1]); 
           String vname=classObject.metaDataName2AttributeName.get(MetaData.fileAttributeNameMap[i][0]);
           String mname=classObject.attributeName2MetaDataName.get(MetaData.fileAttributeNameMap[i][1]);
           
           out.println("metaDataName="+mname); 
           out.println("    attrName="+vname);            
       }
   }

   
 public static String metaDataName2AttributeName(String fieldName)
 {
     String val=classObject.metaDataName2AttributeName.get(fieldName);
      
     if (val==null) 
         Debug("Warning: no a recognised SRB meta data field:"+fieldName);
     
     return val;  
 }

 public static String attributeName2MetaDataName(String name)
 {
    String val=classObject.attributeName2MetaDataName.get(name);
    
    if (val==null) 
        Debug("Warning: VAttribute name not supported by SRB metadata :"+name);
    
    return val; 
 }
 
}
