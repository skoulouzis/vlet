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
 * $Id: IrodsMetaData.java,v 1.1 2011-06-07 10:40:06 ptdeboer Exp $  
 * $Date: 2011-06-07 10:40:06 $
 */ 
// source: 

package nl.uva.vlet.vfs.irods;



import java.io.PrintStream;
import java.util.Hashtable;

import nl.uva.vlet.ClassLogger;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.irods.IRODSMetaDataSet;
import static nl.uva.vlet.data.VAttributeConstants.*;

/**
 *  Experimental MetaData class 
 */ 
public class IrodsMetaData
{
    private static ClassLogger logger;
    
    {
        logger=ClassLogger.getLogger(IrodsMetaData.class);
        logger.setLevelToDebug(); 
    }
    
    /**
     * Maps between VAttribute names and IRODS MetaData names. 
     * Sadly not all names used in the MetaData Groups can be reused! 
     * For example MODIFICIATION_DATE is defined as "modification date" with 
     * a SPACE in it. 
     * The VAttribute names cannot have spaces in them since it must be possible
     * to use them as Parameter Names:  <br>
     *    Query = "modificationTime &lt; 1000" <br>  
     * Also some names MetaData names are ambiguous (like dirname!) and some VAttributes are
     * aliases (modification time as 'String' vs modification time as 'long' !) 
     * Also beware that directories don't have MetaData attributes, thus a query
     * for (sub)directories with MetaData will result in a null result.  
     **/ 
    private static String fileAttributeNameMap[][]=
       {
            // IORODS MetaData attribute name        |  VRS Attribute Name        //
            // -------------------------------    |  ------------------------------
            { MetaDataSet.FILE_NAME                   ,ATTR_NAME }, 
            // Parent dir of files:
            { IRODSMetaDataSet.PARENT_DIRECTORY_NAME ,ATTR_PARENT_DIRNAME }, 
            { MetaDataSet.DIRECTORY_NAME             ,ATTR_DIRNAME }, 
            { MetaDataSet.SIZE                       ,ATTR_LENGTH }, 
            { MetaDataSet.MODIFICATION_DATE          ,ATTR_MODIFICATION_TIME },
            //{ MetaDataSet.MODIFICATION_DATE        ,ATTR_MODIFICATION_TIME_STRING },
            { MetaDataSet.CREATION_DATE              ,ATTR_CREATION_TIME },
            //{ MetaDataSet.CREATION_DATE            ,ATTR_CREATION_TIME_STRING },
            { MetaDataSet.OWNER                      ,ATTR_OWNER},
            { IRODSMetaDataSet.RESOURCE_NAME         ,IrodsFS.ATTR_IRODS_RESOURCE},
            { IRODSMetaDataSet.USER_NAME             ,ATTR_USERNAME}
            //{ IRODSMetaDataSet.FILE_TYPE_NAME      ,ATTR_DATA_TYPE}
       };
    
    // class object
    private static IrodsMetaData instance=null; 
    
    public static synchronized IrodsMetaData getInstance()
    {
        if (instance==null)
            instance=new IrodsMetaData();
        
        return instance; 
    }
    // =========================================================================
    // Instance 
    // =========================================================================
    
    private Hashtable<String, String> attributeName2MetaDataName = new Hashtable<String, String>();
    
    private Hashtable<String, String> metaDataName2AttributeName = new Hashtable<String, String>();


    public IrodsMetaData()
    {
        init(); 
    }
    // *** 

    private void init()
    {
        initFileAttributeMaps(); 
    }
    
    // Class Initializer: 
    private void initFileAttributeMaps()
    {
        // logger.debugPrintf("Initializing IRODS Meta Data Attribute map\n"); 
                
        for (int i=0;i<fileAttributeNameMap.length;i++)
        {
            String irodsname=fileAttributeNameMap[i][0];
            String vrsname=fileAttributeNameMap[i][1]; 
                                                     
            metaDataName2AttributeName.put(irodsname,vrsname);
            attributeName2MetaDataName.put(vrsname,irodsname); 
        }
    }
    
    // Class Misc. 
    // ***
    
   public static void printMaps(PrintStream out)
   {
       for (int i=0;i<IrodsMetaData.fileAttributeNameMap.length;i++)
       {
           out.println("nameMap["+i+"]="+IrodsMetaData.fileAttributeNameMap[i][0]+","+IrodsMetaData.fileAttributeNameMap[i][1]); 
           String vname=getInstance().metaDataName2AttributeName.get(IrodsMetaData.fileAttributeNameMap[i][0]);
           String mname=getInstance().attributeName2MetaDataName.get(IrodsMetaData.fileAttributeNameMap[i][1]);
           
           out.println("metaDataName="+mname); 
           out.println("    attrName="+vname);            
       }
   }

   
 public static String metaDataName2AttributeName(String fieldName)
 {
     String val=getInstance().metaDataName2AttributeName.get(fieldName);
      
     if (val==null) 
         logger.warnPrintf("metaDataName2AttributeName(): Not a recognised IRODS MetaData field:%s\n",fieldName);
     
     return val;  
 }

 public static String attributeName2MetaDataName(String name)
 {
    String val=getInstance().attributeName2MetaDataName.get(name);
    
    if (val==null) 
        logger.warnPrintf("attributeName2MetaDataName(): VAttribute name not supported by IRODS MetaData:%s\n",name);
    
    return val; 
 }
 
}
