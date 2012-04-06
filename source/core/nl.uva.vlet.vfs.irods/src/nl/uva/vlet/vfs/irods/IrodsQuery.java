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
 * $Id: IrodsQuery.java,v 1.3 2011-06-20 09:49:12 ptdeboer Exp $  
 * $Date: 2011-06-20 09:49:12 $
 */ 
// source: 

package nl.uva.vlet.vfs.irods;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;

import java.io.IOException;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.vrs.VRSContext;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataField;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.irods.IRODSMetaDataSet;

/**
 * Irods MetaData Query class.  
 * Converts IRODS MeteData to VAttribute[Set] objects and vice versa.
 * 
 * This class in the bridge between VRS MetaData and IRODS MetaData. 
 *   
 */ 
public class IrodsQuery
{
    private static ClassLogger logger;
    
    {
        logger=ClassLogger.getLogger(IrodsQuery.class);
        //logger.setLevelToDebug(); 
    }
    
    /** Convert MetaData field to VAttribute */
    private static VAttribute convertToVAttribute(MetaDataField field,Object o)
    {

      String fieldName=field.getName(); 
      String attrName=IrodsMetaData.metaDataName2AttributeName(fieldName);
      
      if (attrName==null)
          attrName=fieldName; // no conversion: use fieldName !
      

      if (o!=null)
      {
          String value=o.toString();
          //logger.debugPrintln("Value["+i+","+j+"]="+value);
          //logger.debugPrintln("Value.class="+o.getClass().getCanonicalName());
          
          VAttribute attr=null; 
          
          // Add type for known meta data. 
          if (attrName.compareTo(ATTR_LENGTH)==0)
          {
              attr=new VAttribute(attrName,Long.valueOf(value));    
          }
          else
          {
              attr=new VAttribute(attrName,value);
          }
          
          return attr;     
      }

      return null ;
  }
    // ========================================================================
    // Instance
    // ========================================================================
    
    // filesystem to perform queries on 
    private IRODSFileSystem irodsfs;
    
    private VRSContext vrsContext;

    public IrodsQuery(VRSContext context,IRODSFileSystem irodsfs)
    {
        this.irodsfs=irodsfs; 
        this.vrsContext=context; 
    }
    
    /** Simple file query on directory */ 
    public VAttributeSet[] listDirs(String iroddirpath) throws VlIOException
    {
        return listDirs(iroddirpath,null); 
    }
    
    
   	/** Simple directory query  */ 
    public VAttributeSet[] listDirs(String dirpath,String selects[][]) throws VlIOException
    {
    	 logger.debugPrintf("listDirs:%s\n",dirpath); 
         
         MetaDataSelect mselects[] =new MetaDataSelect[2];
      
         // select dirname + file_name (together is full filepath 
         mselects[0]=MetaDataSet.newSelection(IRODSMetaDataSet.PARENT_DIRECTORY_NAME);
         mselects[1]=MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_NAME);
         //selects[2]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
         
         
         int nrConditions=(dirpath==null)?(0):(1);
         
         if (selects!=null) 
        	 nrConditions+=selects.length; 
         
         
         //  Query parent directory: all files 
         Vector<MetaDataCondition> conditions = new Vector<MetaDataCondition>();
         
         //conditions[0] = MetaDataSet.newCondition(
         //        IRODSMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
         //        iroddirpath);
        
         int index=0; 
         if (dirpath!=null)
         {
        	 conditions.add(MetaDataSet.newCondition(
                 IRODSMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,
                 dirpath));
         }
         
         if (selects!=null)
         {
        	 for (String sel[]:selects)
        	 {
        		 if (sel==null) 
        			 continue; 
        		 
        		 String name=sel[0];
        		 String value=sel[1]; 
             
        		 String irodFieldName= IrodsMetaData.attributeName2MetaDataName(name);
        		 
        		 if (irodFieldName==null)
        		 {
        			 logger.warnPrintf("Field name not recognized:%s\n",name);
        			 irodFieldName=name; // keep name 'as is'
        		 } 
        		 else
        			 conditions.add(MetaDataSet.newCondition(irodFieldName,MetaDataCondition.EQUAL,value));
        	 }
         }
         
         if (conditions.size()<=0)
         	return null; 
         
         // qeury attributes and return VAttributeSets 
         MetaDataCondition condarr[]=new MetaDataCondition[conditions.size()]; 
         condarr=conditions.toArray(condarr); 
         return doQuery(condarr,mselects);
    }
    
    /** Simple file query on directory */ 
    public VAttributeSet[] listFiles(String dirpath) throws VlIOException
    {
        return listFiles(dirpath,null); 
    }
    
    /** Simple file query on directory */ 
    public VAttributeSet[] listFiles(String dirpath,String selects[][]) throws VlIOException
    {
         logger.debugPrintf("queryFiles=:%s\n",dirpath); 
         
         int i=0; 
         MetaDataSelect mselects[] =new MetaDataSelect[2];
      
         // select dirname + file_name (together is full filepath 
         //selects[i++]=MetaDataSet.newSelection(IRODSMetaDataSet.PARENT_DIRECTORY_NAME);
         mselects[i++]=MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_NAME);
         mselects[i++]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
         
         int nrConditions=(dirpath==null)?(0):(1);
         
         if (selects!=null) 
             nrConditions+=selects.length; 
         
         //  Query parent directory: all files 
         Vector<MetaDataCondition> conditions = new Vector<MetaDataCondition>();
      
         //conditions[0] = MetaDataSet.newCondition(
         //        IRODSMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
         //        iroddirpath);
         int index=0; 
         
         if (dirpath!=null)
         {
             conditions.add(MetaDataSet.newCondition(
                 IRODSMetaDataSet.DIRECTORY_NAME, 
                 MetaDataCondition.EQUAL,
                 dirpath));
         }
         
         if (selects!=null)
             for (String sel[]:selects)
             {
                 if (sel==null) 
                     continue; 
                 
                 String name=sel[0];
                 String value=sel[1]; 
             
                 String irodFieldName=IrodsMetaData.attributeName2MetaDataName(name);
                 
                 if (irodFieldName==null)
                 {
                     logger.warnPrintf("Unknown IRODS MetaData Fieldname:%s",name); 
                 }
                 else
                 {
                     conditions.add(MetaDataSet.newCondition(irodFieldName,MetaDataCondition.EQUAL,value));
                 }
                 
             }
         // qeury attributes and return VAttributeSets 
         MetaDataCondition condarr[]=new MetaDataCondition[conditions.size()]; 
         condarr=conditions.toArray(condarr); 
         return doQuery(condarr,mselects);
    }
    
    /** 
     * Perform  Query and return VAttributeSets array  
     * 
     * @param irodfs
     * @param conditions
     * @param selects
     * @return
     * @throws VlIOException
     */
    public VAttributeSet[] doQuery(MetaDataCondition[] conditions, 
            MetaDataSelect[] selects) throws VlIOException 
    {
         MetaDataRecordList[] results = null;
         VAttributeSet attrSets[]=null;
         
        try
        { 
            results = irodsfs.query(conditions, selects, 300);
            results = MetaDataRecordList.getAllResults(results);
           
           if (results==null)
           {
               logger.debugPrintf("Query resulted on NULL values !\n"); 
               return null;
           }

           attrSets=new VAttributeSet[results.length];
           
           for (int i=0;i<results.length;i++)
           {
               String value=null; 
               
               MetaDataField[] fields = results[i].getFields();
               int numFields=fields.length; 
 
               attrSets[i]=new VAttributeSet(); // numFields
               
               for (int j=0;j<numFields;j++)
               {
                   VAttribute attr=null;
                   attrSets[i].put(attr=convertToVAttribute(fields[j],results[i].getValue(j)));
               }
               // .getValue(IRODSMetaDataSet.FILE_NAME);
               
           }
           
        }
        catch (IOException e)
        {
            throw new VlIOException("Failed to perform query:IOException",e);
        }

        return attrSets; 
    }
        
   

    
    
    /**
     *  Query a set of attributes for the a single path or complete directory.
     *  If a single file needs to be queries, set fileName to the fullpath of the file. 
     *  In that case the VAttributeSet array *should* contain 1 VAttributeSet. 
     *  Set direcotyQuery when querying a complete directory (all files).  
     *  
     *  @param directoryQuery - set to true when querying the directory itself and not its contents. 
     *  @param query[][] - extra query selection parameters 
     *  @throws VlIOException
     */ 
     
    public VAttributeSet[] doQuery(
            String parentDirname,
            String optFileName,
            boolean directoryQuery,
            String attributeNames[],
            String query[][]) throws VlIOException
    {
        logger.debugPrintf("parentDirname=%s\n",parentDirname); 
        int querylen=0; 
        
        if (query!=null)
            querylen=query.length; 
        
        int numAttrs=0;
        
        if (attributeNames!=null)
           numAttrs=attributeNames.length; 
         
        String metaFields[]=new String[numAttrs+querylen];
        int    metaIndex[]=new int[numAttrs];
        
        
        // convert and check Attribute Names to SRBQuery fields
        
        int numFields=0;
        boolean convertTimeString=false; 
        
        for (int i=0;i<numAttrs;i++)
        {
            // optional Attribute Mapping,renaming VAttribute name to MetaData name
            String name=attributeNames[i]; 
            String alias=null;
            
            if (name!=null)
            {
                /**
                // convert millis attribute to request for time string: 
                if (name.compareTo(VFSNode.ATTR_MODIFICATION_TIME)==0)
                {
                    // convert date string to time millis
                   alias=MetaDataSet.MODIFICATION_DATE;
                   convertTimeString=true; 
                } 
                else **/
                
                alias=IrodsMetaData.attributeName2MetaDataName(name);
            }
            
            //MetaDataGroup[] groups = MetaDataSet.getMetaDataGroups();
            // check alias:
            
            
            if ((alias!=null)  && (MetaDataSet.getField(alias)!=null))
            {
                name=alias; // valid alias 
            }
            
            else if (name!=null)
            {
                try
                {
                    if (MetaDataSet.getField(name)==null) 
                        name=null; // not a valid field 
                }
                // new SRB code throws an Exception!: 
                catch (IllegalArgumentException e)
                {
                     name=null; // not a valid field  
                }
            }
             
            
            // check name
            
            if (name!=null)
            {
                logger.debugPrintf("MetaDataSet.getField(%s)=%s\n",name,MetaDataSet.getField(name));
                // store field 
                metaFields[numFields]=name;
                metaIndex[i]=numFields++;
            }
            else
            {
                metaIndex[i]=-1; // keep index between attributeNames[] and metaFields[]
            }
        }
        
        MetaDataSelect selects[] =new MetaDataSelect[numFields+2+querylen];
        
        for (int i=0;i<numFields;i++)
        {
            logger.debugPrintf("MetaDataField[%d]=%s\n",i,metaFields[i]); 
            selects[i]=MetaDataSet.newSelection(metaFields[i]);
        }
        
        for (int i=0;i<querylen;i++)
        {
            logger.debugPrintf("MetaDataField[%d]=%s\n",numFields+i,query[i][0]); 
            selects[i]=MetaDataSet.newSelection(query[i][0]);
        }

        /* Add file name and directory so that the path can be reconstructed
         * this is only needed when querying a directory for files.  
         */
        selects[numFields++]=MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_NAME);
        
        if (directoryQuery==false)
        {
           selects[numFields++]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
        }
        else
        {
           selects[numFields++]=MetaDataSet.newSelection(IRODSMetaDataSet.PARENT_DIRECTORY_NAME);
        }
        
        
        MetaDataCondition conditions[] = new MetaDataCondition[2+querylen];
        
        // Query single file: 
        // TODO: combine query with dirfilepath options ! 
        if (querylen>0)
        {
            conditions[0]= MetaDataSet.newCondition(
                    query[0][0], MetaDataCondition.EQUAL,
                    query[0][1]);
            
            // NOTE: no other options are regarded!
        }
        else if ((optFileName==null) && (directoryQuery==false))
        {
            //  Query parent directory: all files 
            conditions[0] = MetaDataSet.newCondition(
                    IRODSMetaDataSet.DIRECTORY_NAME, 
                    MetaDataCondition.EQUAL,
                    parentDirname);
            
            conditions[1]=null; 
        }
        else if ((optFileName!=null) && (directoryQuery==true))  
        {
            // single subdir
            conditions[0] = MetaDataSet.newCondition(
                    IRODSMetaDataSet.DIRECTORY_NAME, 
                    MetaDataCondition.EQUAL,
                    parentDirname+"/"+optFileName);
            
            // query subdirectories in parent directory 
            conditions[1] = MetaDataSet.newCondition(
                    IRODSMetaDataSet.PARENT_DIRECTORY_NAME, 
                    MetaDataCondition.EQUAL,
                    parentDirname);
        }
        else if (directoryQuery==true)
        {
            // query subdirectories in parent directory 
            conditions[0] = MetaDataSet.newCondition(
                    IRODSMetaDataSet.PARENT_DIRECTORY_NAME, 
                    MetaDataCondition.EQUAL,
                    parentDirname);
            
            conditions[1]=null; 
            
        }
        else // filename!=null and directroyQuery==false 
        {
            // query single filename 
            conditions[0] = MetaDataSet.newCondition(
                    IRODSMetaDataSet.DIRECTORY_NAME, 
                    MetaDataCondition.EQUAL,
                    parentDirname);
           
            conditions[1] = MetaDataSet.newCondition(
                  MetaDataSet.FILE_NAME, 
                  MetaDataCondition.EQUAL,
                  optFileName);
        }
       
        
        
        return doQuery(conditions,selects);
    }
//    
//    
//     /** Select fields from irodfs */   
//     
//     
//    public static VAttributeSet[] simpleQuery(IRODSFileSystem irodfs,String fields[]) throws VlIOException
//    {
//        return simpleQuery(irodfs,fields,null); 
//    }
//    
//    /** Select fields[] from irodfs where conditions[][] yield */  
//    
//    static VAttributeSet[] simpleQuery(IRODSFileSystem irodfs,String fields[],
//            String conditions[][]) throws VlIOException
//    {
//
//        MetaDataRecordList[] rl1 = null;
//
//        // Select <Fields> 
//        
//        int numFields=fields.length; 
//        
//        MetaDataSelect selects[] =new MetaDataSelect[numFields];
//        
//        for (int i=0;i<numFields;i++)
//        {
//            logger.debugPrintf("MetaDataField[%d]=%s\n",i,fields[i]); 
//            selects[i]=MetaDataSet.newSelection(fields[i]);
//        }
//         
//        // Where <Conditions>  
//        
//        MetaDataCondition metaConditions[] = null; //new MetaDataCondition[numFields];
//        
//        if (conditions!=null)
//        {
//            int numConditions=conditions.length;
//            
//            metaConditions=new MetaDataCondition[numConditions]; 
//        
//            for (int i=0;i<numConditions;i++)
//            {
//                if (conditions[i].length>2)
//                {
//                    // 3rd is optional operator,default is EQUAL
//                    String opStr=conditions[i][2]; 
//                    
//                }
//                
//                metaConditions[i] = MetaDataSet.newCondition(
//                      conditions[i][0], MetaDataCondition.EQUAL,
//                       conditions[i][1]);
//            }
//        }
//        
//        // return doQuery(irodfs,metaConditions,selects); 
//        
//       
//        VAttributeSet attrSets[]=null;
//        
//        try
//        { 
//           rl1 = irodfs.query(metaConditions, selects, 300);
//           rl1 = MetaDataRecordList.getAllResults(rl1);
//           
//           if (rl1==null)
//           {
//               logger.debugPrintf("Query resulted on NULL values !\n"); 
//               return null;
//           }
//
//           attrSets=new VAttributeSet[rl1.length];
//           
//           // Convert to Attributes 
//           
//           for (int i=0;i<rl1.length;i++)
//           {
//               MetaDataField[] resultFields = rl1[i].getFields();
//               numFields=resultFields.length; 
// 
//               attrSets[i]=new VAttributeSet(); // numFields
//              
//               
//               for (int j=0;j<resultFields.length;j++)
//               {
//                   String value=rl1[i].getValue(j).toString();
//                   String name=resultFields[j].getName();
//                   VAttribute attr=new VAttribute(name,value);
//                   attrSets[i].put(attr); // attr=convertToAttribute(resultFields[j],rl1[i].getValue(j)));
//                   logger.debugPrintf("Attribute[%d][%d]=%s\n",i,j,attr);
//               }               
//               // .getValue(IRODSMetaDataSet.FILE_NAME);
//               
//           }
//           
//        }
//        catch (IOException e)
//        {
//            throw new VlIOException("SRB IOException",e);
//        }
//
//        return attrSets; 
//       
//    }
//    
//    
//    /** Convert SRB meta data field to VAttribute     */
//    private static VAttribute convertToVAttribute(MetaDataField field,Object o)
//    {
//
//        String fieldName=field.getName(); 
//        String attrName=MetaData.metaDataName2AttributeName(fieldName);
//        
//        if (attrName==null)
//            attrName=fieldName; // no conversion: use fieldName !
//        
//
//        if (o!=null)
//        {
//            String value=o.toString();
//            //logger.debugPrintln("Value["+i+","+j+"]="+value);
//            //logger.debugPrintln("Value.class="+o.getClass().getCanonicalName());
//            
//            VAttribute attr=null; 
//            
//            // add type: 
//            if (attrName.compareTo(ATTR_LENGTH)==0)
//            {
//                attr=new VAttribute(attrName,Long.valueOf(value));    
//            }
//            else
//            {
//                attr=new VAttribute(attrName,value);
//            }
//            
//            return attr;     
//        }
//
//        return null ;
//    }
//
//    public static void setMetaData(IRODSFile irodFile,String fields[],
//            VAttribute data[][]) throws VlIOException
//    {
//     
//    }
//    // does not work (yet)
//    
//    private static void setMetaData(IRODSFile irodFile,
//                VAttribute data[][]) throws VlIOException
//    {
// 
//        //IRODSFileSystem irodfs = (IRODSFileSystem) irodFile.getFileSystem(); 
//            
//        if (data==null) 
//            return;
//        
//        // set Data
//        
//        int numRecords=data.length;
//            
//        MetaDataRecordList records[]=new MetaDataRecordList[numRecords];  
//             
//        
//        for (int i=0;i<numRecords;i++)
//        {
//            int numDataFields=data[i].length;
//            
//            int j=0; 
//            
//            records[i] = new IRODSMetaDataRecordList(
//                    IRODSMetaDataSet.getField(data[i][j].getName()),
//                    data[i][j].getValue()); 
//            
//              // IRODSMetaDataSet.getField(IRODSMetaDataSet.SIZE ),numDataFields);
//       
//            for (j=1;j<numDataFields;j++)
//            {
//                 
//                records[i].addRecord(IRODSMetaDataSet.getField(data[i][j].getName()),
//                        data[i][j].getValue());
//                
//                
//            }
//            
//            for (j=0;j<numDataFields;j++)
//            {
//               logger.debugPrintf(">>> setting Meta Data Record: %s=%s\n",records[i].getFieldName(j),records[i].getStringValue(j));
//            }
//            
//            try
//            {
//                irodFile.modifyMetaData(records[i]);
//            }
//            catch (IOException e)
//            {
//                throw new VlIOException(e); 
//            }
//        }
//    }
//     
//    /**
//     * A SRB directory doesn't have attributes which can be queried 
//     * So either FILES are returned or nothing at all!
//     * 
//     */ 
//    public static VAttributeSet[] queryDir(IRODSFileSystem irodfs, String path, String[] attributeNames) throws VlIOException
//    {
//        // Query single dir: 
//        return Query(irodfs,VRL.dirname(path),VRL.basename(path),true,attributeNames,null);
//    }

}
