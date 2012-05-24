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
 * $Id: SrbQuery.java,v 1.1 2010-01-11 11:05:09 ptdeboer Exp $  
 * $Date: 2010-01-11 11:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;

import java.io.IOException;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.vrl.VRL;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataField;
import edu.sdsc.grid.io.MetaDataGroup;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;
import edu.sdsc.grid.io.srb.SRBMetaDataRecordList;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;
/**
 *  SRBQuery Query Class. 
 *  This class is under construction and therefore not well structurized...
 */ 

public class SrbQuery
{
    /** Class Object */
    
    //private static SRBQuery defaultQuerySettings=new SRBQuery(); 

    // === Instance === 
    
    /** Private Class Object constructor */ 
    //private SRBQuery()
    //{
    //    initMaps(); 
    // }
  
    // === Class Stuff ==== 
      
	/** Simple file query on directory */ 
    public static VAttributeSet[] listDirs(SRBFileSystem srbfs,String srbdirpath) throws VlIOException
    {
    	return listDirs(srbfs,srbdirpath,null); 
    }
    
   	/** Simple directory query  */ 
    public static VAttributeSet[] listDirs(SRBFileSystem srbfs, String dirpath,String selects[][]) throws VlIOException
    {
    	 Debug("listDirs:"+dirpath); 
         
         MetaDataSelect mselects[] =new MetaDataSelect[2];
      
         // select dirname + file_name (together is full filepath 
         mselects[0]=MetaDataSet.newSelection(SRBMetaDataSet.PARENT_DIRECTORY_NAME);
         mselects[1]=MetaDataSet.newSelection(SRBMetaDataSet.DIRECTORY_NAME);
         //selects[2]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
         
         
         int nrConditions=(dirpath==null)?(0):(1);
         
         if (selects!=null) 
        	 nrConditions+=selects.length; 
         
         
         //  Query parent directory: all files 
         Vector<MetaDataCondition> conditions = new Vector<MetaDataCondition>();
         
         //conditions[0] = MetaDataSet.newCondition(
         //        SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
         //        srbdirpath);
        
         int index=0; 
         if (dirpath!=null)
        	 conditions.add(MetaDataSet.newCondition(
                 SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,
                 dirpath));
         
         if (selects!=null)
        	 for (String sel[]:selects)
        	 {
        		 if (sel==null) 
        			 continue; 
        		 
        		 String name=sel[0];
        		 String value=sel[1]; 
             
        		 String srbFieldName= MetaData.attributeName2MetaDataName(name);
        		 
        		 if (srbFieldName==null)
        		 {
        			 Global.warnPrintln(SrbQuery.class,"Field name not recognized:"+name);
        			 srbFieldName=name; // keep name 'as is'
        		 } 
        		 else
        			 conditions.add(MetaDataSet.newCondition(srbFieldName,MetaDataCondition.EQUAL,value));
        	 }
         
         if (conditions.size()<=0)
         	return null; 
         
         // qeury attributes and return VAttributeSets 
         MetaDataCondition condarr[]=new MetaDataCondition[conditions.size()]; 
         condarr=conditions.toArray(condarr); 
         return doQuery(srbfs,condarr,mselects);

    }

    /** Simple file query on directory */ 
    public static VAttributeSet[] listFiles(SRBFileSystem srbfs,String srbdirpath) throws VlIOException
    {
    	return listFiles(srbfs,srbdirpath,null); 
    }
    
    /** Simple file query on directory */ 
    public static VAttributeSet[] listFiles(SRBFileSystem srbfs,String srbdirpath,String selects[][]) throws VlIOException
    {
    	 Debug("queryFiles=:"+srbdirpath); 
         
    	 int i=0; 
         MetaDataSelect mselects[] =new MetaDataSelect[2];
      
         // select dirname + file_name (together is full filepath 
         //selects[i++]=MetaDataSet.newSelection(SRBMetaDataSet.PARENT_DIRECTORY_NAME);
         mselects[i++]=MetaDataSet.newSelection(SRBMetaDataSet.DIRECTORY_NAME);
         mselects[i++]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
         
         int nrConditions=(srbdirpath==null)?(0):(1);
         
         if (selects!=null) 
        	 nrConditions+=selects.length; 
         
         //  Query parent directory: all files 
         Vector<MetaDataCondition> conditions = new Vector<MetaDataCondition>();
      
         //conditions[0] = MetaDataSet.newCondition(
         //        SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
         //        srbdirpath);
         int index=0; 
         
         if (srbdirpath!=null)
         {
        	 conditions.add(MetaDataSet.newCondition(
                 SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
                 srbdirpath));
         }
         
         if (selects!=null)
        	 for (String sel[]:selects)
        	 {
        		 if (sel==null) 
        			 continue; 
        		 
        		 String name=sel[0];
        		 String value=sel[1]; 
             
        		 String srbFieldName=MetaData.attributeName2MetaDataName(name);
        		 
        		 if (srbFieldName==null)
        		 {
        			 Global.warnPrintln(SrbQuery.class,"Unknown SRB Fieldname :"+name); 
        		 }
        		 else
        		 {
        			 conditions.add(MetaDataSet.newCondition(srbFieldName,MetaDataCondition.EQUAL,value));
        		 }
        		 
        	 }
         // qeury attributes and return VAttributeSets 
         MetaDataCondition condarr[]=new MetaDataCondition[conditions.size()]; 
         condarr=conditions.toArray(condarr); 
         return doQuery(srbfs,condarr,mselects);
    }

    
    
    /**
     *  Queries a set of attributes for the specified SRB Directory.
     *  Optionally a fileName can be set if one file needs
     *  to be queried. In that case the VAttributeSet array *should* contain 1 
     *  VAttributeSet. Set subdirs to true if quering for directories ! 
     *  
     *  @param srbdirpath the parent directory 
     *  @param fileName optional single filename. 
     *  @throws VlIOException
     */ 
     
    public static VAttributeSet[] Query(
            SRBFileSystem srbfs,
            String srbdirpath,
            String fileName,
            boolean directoryQuery,
            String attributeNames[],
            String query[][]) throws VlIOException
    {
        Debug("srbdirpath="+srbdirpath); 
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
                
                alias=MetaData.attributeName2MetaDataName(name);
            }
            
            MetaDataGroup[] groups = MetaDataSet.getMetaDataGroups();
           
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
                Debug("MetaDataSet.getField("+name+")="+MetaDataSet.getField(name));
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
            Debug("MetaDataField["+i+"]="+metaFields[i]); 
            selects[i]=MetaDataSet.newSelection(metaFields[i]);
        }
        
        for (int i=0;i<querylen;i++)
        {
            Debug("MetaDataField["+numFields+i+"]="+query[i][0]); 
            selects[i]=MetaDataSet.newSelection(query[i][0]);
        }

        /* Add file name and directory so that the path can be reconstructed
         * this is only needed when querying a directory for files.  
         */
        selects[numFields++]=MetaDataSet.newSelection(SRBMetaDataSet.DIRECTORY_NAME);
        
        if (directoryQuery==false)
        {
           selects[numFields++]=MetaDataSet.newSelection(MetaDataSet.FILE_NAME);
        }
        else
        {
           selects[numFields++]=MetaDataSet.newSelection(SRBMetaDataSet.PARENT_DIRECTORY_NAME);
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
            
            
          /*
           * Does not work:  
            // use parent dir as anchestor dir: this resursively lists all 
            // files in the file tree!
            
           if (srbdirpath!=null)
           {
               conditions[1] = MetaDataSet.newCondition(
                    SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath);
          
           }
           */
            
            // conditions[1]=null; 
        }
        else if ((fileName==null) && (directoryQuery==false))
        {
            //  Query parent directory: all files 
            
            conditions[0] = MetaDataSet.newCondition(
                    SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath);
            
            conditions[1]=null; 
        }
        else if ((fileName!=null) && (directoryQuery==true))  
        {
          // single subdir
            conditions[0] = MetaDataSet.newCondition(
                    SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath+"/"+fileName);
            
            // query subdirectories in parent directory 
            conditions[1] = MetaDataSet.newCondition(
                    SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath);
        }
        else if (directoryQuery==true)
        {
            // query subdirectories in parent directory 
            conditions[0] = MetaDataSet.newCondition(
                    SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath);
            
            conditions[1]=null; 
            
        }
        else // filename!=null
        {
            // query single filename 
            conditions[0] = MetaDataSet.newCondition(
                    SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
                    srbdirpath);
           
            conditions[1] = MetaDataSet.newCondition(
                  MetaDataSet.FILE_NAME, MetaDataCondition.EQUAL,
                  fileName);
        }
       
        
        
        return doQuery(srbfs,conditions,selects);
    }
    
    /** 
     * Perform  Query and return VAttributeSets array  
     * 
     * @param srbfs
     * @param conditions
     * @param selects
     * @return
     * @throws VlIOException
     */
    static VAttributeSet[] doQuery( SRBFileSystem srbfs,
    		MetaDataCondition[] conditions, MetaDataSelect[] selects) throws VlIOException 
    {
    	 MetaDataRecordList[] rl1 = null;
         MetaDataRecordList[] rl2 = null;
         VAttributeSet attrSets[]=null;
         int numFields=0; 
         
        try
        { 
           rl1 = srbfs.query(conditions, selects, 300);
           rl1 = MetaDataRecordList.getAllResults(rl1);
           
           if (rl1==null)
           {
               Debug("Query resulted on NULL values !!!"); 
               return null;
           }

           attrSets=new VAttributeSet[rl1.length];
           
           for (int i=0;i<rl1.length;i++)
           {
               String value=null; 
               
               MetaDataField[] fields = rl1[i].getFields();
               numFields=fields.length; 
 
               attrSets[i]=new VAttributeSet(); // numFields
               
               for (int j=0;j<fields.length;j++)
               {
                   VAttribute attr=null;
                   attrSets[i].put(attr=convertToVAttribute(fields[j],rl1[i].getValue(j)));
               }
               // .getValue(SRBMetaDataSet.FILE_NAME);
               
           }
           
        }
        catch (IOException e)
        {
            throw new VlIOException("SRB IOException",e);
        }

        return attrSets; 
    }
    
    
     /** Select fields from srbfs */   
     
     
    public static VAttributeSet[] simpleQuery(SRBFileSystem srbfs,String fields[]) throws VlIOException
    {
        return simpleQuery(srbfs,fields,null); 
    }
    
    /** Select fields[] from srbfs where conditions[][] yield */  
    
    static VAttributeSet[] simpleQuery(SRBFileSystem srbfs,String fields[],
            String conditions[][]) throws VlIOException
    {

        MetaDataRecordList[] rl1 = null;

        // Select <Fields> 
        
        int numFields=fields.length; 
        
        MetaDataSelect selects[] =new MetaDataSelect[numFields];
        
        for (int i=0;i<numFields;i++)
        {
            Debug("MetaDataField["+i+"]="+fields[i]); 
            selects[i]=MetaDataSet.newSelection(fields[i]);
        }
         
        // Where <Conditions>  
        
        MetaDataCondition metaConditions[] = null; //new MetaDataCondition[numFields];
        
        if (conditions!=null)
        {
            int numConditions=conditions.length;
            
            metaConditions=new MetaDataCondition[numConditions]; 
        
            for (int i=0;i<numConditions;i++)
            {
                if (conditions[i].length>2)
                {
                    // 3rd is optional operator,default is EQUAL
                    String opStr=conditions[i][2]; 
                    
                }
                
                metaConditions[i] = MetaDataSet.newCondition(
                      conditions[i][0], MetaDataCondition.EQUAL,
                       conditions[i][1]);
            }
        }
        
        // return doQuery(srbfs,metaConditions,selects); 
        
       
        VAttributeSet attrSets[]=null;
        
        try
        { 
           rl1 = srbfs.query(metaConditions, selects, 300);
           rl1 = MetaDataRecordList.getAllResults(rl1);
           
           if (rl1==null)
           {
               Debug("Query resulted on NULL values !!!"); 
               return null;
           }

           attrSets=new VAttributeSet[rl1.length];
           
           // Convert to Attributes 
           
           for (int i=0;i<rl1.length;i++)
           {
               MetaDataField[] resultFields = rl1[i].getFields();
               numFields=resultFields.length; 
 
               attrSets[i]=new VAttributeSet(); // numFields
              
               
               for (int j=0;j<resultFields.length;j++)
               {
                   String value=rl1[i].getValue(j).toString();
                   String name=resultFields[j].getName();
                   VAttribute attr=new VAttribute(name,value);
                   attrSets[i].put(attr); // attr=convertToAttribute(resultFields[j],rl1[i].getValue(j)));
                   Debug("Attribute["+i+","+j+"]="+attr);
               }               
               // .getValue(SRBMetaDataSet.FILE_NAME);
               
           }
           
        }
        catch (IOException e)
        {
            throw new VlIOException("SRB IOException",e);
        }

        return attrSets; 
       
    }
    
    
    /** Convert SRB meta data field to VAttribute     */
    private static VAttribute convertToVAttribute(MetaDataField field,Object o)
    {

        String fieldName=field.getName(); 
        String attrName=MetaData.metaDataName2AttributeName(fieldName);
        
        if (attrName==null)
            attrName=fieldName; // no conversion: use fieldName !
        

        if (o!=null)
        {
            String value=o.toString();
            //Debug("Value["+i+","+j+"]="+value);
            //Debug("Value.class="+o.getClass().getCanonicalName());
            
            VAttribute attr=null; 
            
            // add type: 
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

    public static void setMetaData(SRBFile srbFile,String fields[],
            VAttribute data[][]) throws VlIOException
    {
     
    }
    // does not work (yet)
    
    private static void setMetaData(SRBFile srbFile,
                VAttribute data[][]) throws VlIOException
    {
 
        //SRBFileSystem srbfs = (SRBFileSystem) srbFile.getFileSystem(); 
            
        if (data==null) 
            return;
        
        // set Data
        
        int numRecords=data.length;
            
        MetaDataRecordList records[]=new MetaDataRecordList[numRecords];  
             
        
        for (int i=0;i<numRecords;i++)
        {
            int numDataFields=data[i].length;
            
            int j=0; 
            
            records[i] = new SRBMetaDataRecordList(
                    SRBMetaDataSet.getField(data[i][j].getName()),
                    data[i][j].getStringValue()); 
            
              // SRBMetaDataSet.getField(SRBMetaDataSet.SIZE ),numDataFields);
       
            for (j=1;j<numDataFields;j++)
            {
                 
                records[i].addRecord(SRBMetaDataSet.getField(data[i][j].getName()),
                        data[i][j].getStringValue());
                
                
            }
            
            for (j=0;j<numDataFields;j++)
            {
               Debug(">>> setting Meta Data Record:"+records[i].getFieldName(j)+"="+records[i].getStringValue(j));
            }

            
            try
            {
                srbFile.modifyMetaData(records[i]);
            }
            catch (IOException e)
            {
                throw new VlIOException(e); 
            }
        }
    }
   
    
    private static void Debug(String str)
    {
        Global.debugPrintln(SrbQuery.class,str); 
    }
    
    /*
     * Code from Bart Heupers: 
    public static Object[] Query(GeneralFile file)
    {
        GeneralFile files[] = null;
        if (file instanceof SRBFile)
        {
            SRBFile srbFile = (SRBFile) file;
            MetaDataCondition conditions[] = new MetaDataCondition[1];
            MetaDataRecordList[] rl1 = null;
            MetaDataRecordList[] rl2 = null;
            String path = null;

            try
            {
                // Have to do two queries, one for files and one for
                // directories.
                // This should always be a directory
                path = file.getAbsolutePath();

                MetaDataSelect selects_files[] =
                {
                        MetaDataSet.newSelection(SRBMetaDataSet.FILE_NAME),
                        MetaDataSet.newSelection(SRBMetaDataSet.SIZE),
                        MetaDataSet
                                .newSelection(SRBMetaDataSet.MODIFICATION_DATE) };

                // get all the files
                conditions[0] = MetaDataSet.newCondition(
                        SRBMetaDataSet.DIRECTORY_NAME, MetaDataCondition.EQUAL,
                        path);
                rl1 = file.getFileSystem()
                        .query(conditions, selects_files, 300);
                rl1 = MetaDataRecordList.getAllResults(rl1);

                MetaDataSelect selects_directories[] =
                { MetaDataSet.newSelection(SRBMetaDataSet.DIRECTORY_NAME), };

                // get all the sub-directories
                conditions[0] = MetaDataSet.newCondition(
                        SRBMetaDataSet.PARENT_DIRECTORY_NAME,
                        MetaDataCondition.EQUAL, path);
                rl2 = file.getFileSystem().query(conditions,
                        selects_directories, 300);
                rl2 = MetaDataRecordList.getAllResults(rl2);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
            if (rl1 == null && rl2 == null)
                return null;
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd-HH.mm.ss");
            Vector filesVector = new Vector();
            if (rl1 != null)
            {
                for (int i = 0; i < rl1.length; i++)
                {
                    String strName = rl1[i].getValue(SRBMetaDataSet.FILE_NAME)
                            .toString();
                    if (strName != null)
                    {
                        String strSize = rl1[i].getValue(SRBMetaDataSet.SIZE)
                                .toString();
                        String strLastModified = rl1[i].getValue(
                                SRBMetaDataSet.MODIFICATION_DATE).toString();
                        SRBFile file = new SRBFile(srbFile, strName);
                        file.setFile();
                        long lSize = Long.parseLong(strSize);
                        long lLastModified = 0L;
                        try
                        {
                            lLastModified = formatter.parse(strLastModified)
                                    .getTime();
                        }
                        catch (ParseException pe)
                        {
                            pe.printStackTrace();
                        }
                        this.setLastModified(lLastModified);
                        setLength(lSize);
                        filesVector.add(file);
                    }
                }
            }
            if (rl2 != null)
            {
                String absolutePath = null;
                String relativePath = null;
                for (int i = 0; i < rl2.length; i++)
                {
                    absolutePath = rl2[i].getValue(
                            SRBMetaDataSet.DIRECTORY_NAME).toString();
                    if (absolutePath != null)
                    {
                        relativePath = absolutePath.substring(absolutePath
                                .lastIndexOf("/") + 1);
                        SRBFile file = new SRBFile(srbFile, relativePath);
                        file.setDirectory();
                        filesVector.add(file);
                    }
                }
            }

            files = (SRBFile[]) filesVector.toArray(new SRBFile[0]);
        }
        else
        {
            files = file.listFiles();
        }
        if (files == null)
            return null;
        FileElement[] result = new FileElement[files.length];
        for (int i = 0; i < files.length; i++)
            result[i] = new FileElement(files[i]);
        return result;
    }
    */
    
   
    
    /**
     * A SRB directory doesn't have attributes which can be queried 
     * So either FILES are returned or nothing at all!
     * 
     */ 
    public static VAttributeSet[] queryDir(SRBFileSystem srbfs, String path, String[] attributeNames) throws VlIOException
    {
        // Query single dir: 
        return Query(srbfs,VRL.dirname(path),VRL.basename(path),true,attributeNames,null);
      
    }

}
