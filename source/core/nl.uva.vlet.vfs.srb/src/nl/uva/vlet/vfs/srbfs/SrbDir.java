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
 * $Id: SrbDir.java,v 1.5 2011-11-25 13:43:43 ptdeboer Exp $  
 * $Date: 2011-11-25 13:43:43 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_DIRNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NRACLENTRIES;

import java.io.IOException;
import java.net.URI;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;
/**
 * Implementation of SrbDir
 * 
 * @author P.T. de Boer
 */

public class SrbDir extends VDir 
{
    /** SRB Directory DIR and FILE type ! */
    SrbFileSystem server=null; 
    SRBFile srbnode = null;
    private String[][] query=null;
    
    //SRBFileSystem srbfs = null;
    /** Create new VDir subtype SrbDir from SRBFile information 
     * @throws VlException */ 
    public SrbDir(SrbFileSystem server,SRBFile gfile) throws VlException
    {
        super(server,null); // will set location later! 
        init(server,gfile);
    }
    
    public SrbDir(SrbFileSystem server, String dirpath) throws VlException
    {
    	super(server,null); // will set location later! 
        init(server,new SRBFile(server.getSRBFileSystem(),dirpath));  
    }
    
    public SrbDir(SrbFileSystem server, String path, String qstr,VRL vrl) throws VlException
    {
    	super(server,null); // will set location later! 
        init(server,new SRBFile(server.getSRBFileSystem(),path));
        
        // must seperate query string from other arguments
        if (qstr!=null)
        {
        	String qstrs[]=getQueryStrings(qstr); 
        	initQuery(qstrs);
        }
    } 

    private String[] getQueryStrings(String qstr)
	{
    	// simple fetch query: Todo: better syntax+scanner/parsers
    	return qstr.split("&"); 
	}
    
    /** Query is a list of conditions for example: {"owner==ptdeboer","length==100",...} */  
    private void initQuery(String[] qstrs)
    {
        if (qstrs==null)
            return; 
        
        this.query=new String[qstrs.length][];
        
        int nrConditions=0;
        
        for (int i=0;i<qstrs.length;i++)
        {
            // split on char set:  '[',']','<','>','=','!'
            String stats[]=qstrs[i].split("[\\[\\]<>=!]",2);
            
            if ((stats!=null) && (stats.length>1))
            {
            	String fieldName=stats[0]; 
            	 
            	// patch: server atributes interfere with
            	// query. Must implement real query syntax
            	if (this.server.isServerAttribute(fieldName)==false)
            	{
            		this.query[nrConditions++]=stats; 
            		Debug("query key="+fieldName+"query value="+query[i][1]);
            	}
            }
        }
        
        // no usable query conditions 
        if (nrConditions==0)
        	this.query=null; 
    }
    
    private void init(SrbFileSystem server,SRBFile gfile) throws VlException
    {
        this.server=server; 
        this.srbnode = gfile;
        URI uri=gfile.toURI();
        
        if (uri==null)
            throw new VlException("URI creation Error.","Couldn't create URI from:"+gfile.toString()); 
        
        this.setLocation(new VRL(uri));
    }
   
    public VFSNode[] list() throws VlException
    {
    	// optimized srb query: 
    	
        this.toString();
        
        Debug("list:"+this);
        VAttributeSet[] dirSets = null; 
        VAttributeSet[] fileSets = null;
        
        SRBFileSystem srbfs=(SRBFileSystem)srbnode.getFileSystem();
        
        // file query ! 
        if (query!=null)
        {
        	// do not use a path, but provide query options:
        	dirSets = SrbQuery.listDirs(srbfs,null,query);
            fileSets = SrbQuery.listFiles(srbfs,(String)null,query);
        }
        else
        {
            dirSets = SrbQuery.listDirs(srbfs,this.getPath());
            fileSets = SrbQuery.listFiles(srbfs,this.getPath());
        }
        
        int numDirs=0; 
        int numFiles=0;
        
        if (dirSets!=null)
            numDirs=dirSets.length;
        
        if (fileSets!=null)
            numFiles=fileSets.length;
        
        
        // SRBQuery.Query(srbnode.getFileSystem())
        VFSNode nodes[] = null;
        
        nodes = new VFSNode[numDirs+numFiles]; 
        
        for (int i = 0; i < numDirs; i++)
        {
            String dirpath=null; 
            // dirname is name of directory 
            VAttribute attr=dirSets[i].get(ATTR_DIRNAME);
            
            if (attr==null) 
                Global.errorPrintln(this,"null dirname attribute!");
                
            dirpath=attr.getStringValue(); 
            
            // default to directory 
            nodes[i] = new SrbDir(server,dirpath); 
        }
        
        for (int i = 0; i < numFiles; i++)
        {
        	// dirname is name of parent directory of file 
            String dirpath=fileSets[i].get(ATTR_DIRNAME).getStringValue(); 
            VAttribute nameAttr=fileSets[i].get(ATTR_NAME);
            String name="?";
           
            if (nameAttr==null) 
            {
                Global.errorPrintln(this,"null Name Attribute !");
                Global.errorPrintln(this,"fileSet="+fileSets[i]); 
            }
            else
            {
            	name=nameAttr.getStringValue();
            	 //new file:
                nodes[numDirs+i] = new SrbFile(server,dirpath+VRL.SEP_CHAR+name); 
            }
        }
        return nodes;
    }
    
    public long getLength()
    {
        return srbnode.length();
    }
    
    public String getBasename()
    {
        return srbnode.getName();
    }
    
    public boolean exists()
    {
        return srbnode.exists();
    }
    
    public boolean isAccessable()
    {
        // Hack: SRB directories might not exist but can be queried ! 
        if (this.exists()==false)
            return true; 
        
        return true; 
    }
    
    public boolean isReadable() throws VlException
    {
        //return this.srbnode.canRead(); 
        
        return this.server.isReadable(srbnode,true); 
    }
    
    public boolean isWritable() throws VlException
    {
        //return this.srbnode.canWrite(); 
        
        return this.server.isWritable(srbnode,true); 
    }
    
    public boolean create(boolean ignoreExisting) throws VlException
    {
        return srbnode.mkdir();
    }
    
    public VRL rename(String name,boolean nameIsPath) throws VlException
    {
        try
        {
            SRBFile newSrbFile=SrbFSFactory.srbRename(srbnode,name,nameIsPath);
            
            if (newSrbFile!=null)
                return new VRL(newSrbFile.toURI()); 
        }
        catch (IOException e)
        {
            throw new VlIOException(e.getMessage()); 
        } 
        
        return null; 
    }
    
    public VDir getParentDir() throws VlException
    {
        return new SrbDir(server,(SRBFile)srbnode.getParentFile());
    }
    
    public VFile getFile(String name) throws VlException
    {
    	return server.getFile(this.getPath()+"/"+name); 
    }
    
    public VDir getDir(String name) throws VlException
    {
    	return server.getDir(this.getPath()+"/"+name); 
    }
    
    public boolean setAttribute(VAttribute attr) throws NotImplementedException
    {
        return false;
    }
    
    public boolean delete(boolean recurse) throws VlException
    {
    	// Anti Matthan hacks (he did this)  
    	if (this.getPath().equals(this.server.getHomeDirectory()))
    	{
    		throw new nl.uva.vlet.exception.ResourceDeletionFailedException("I'm refusing to delete you home directory (or it's parent):"+getPath());
    	}
    	
    	if (this.getPath().equals(this.server.getHomeDirectory()+VRL.SEP_CHAR_STR+server.getUserAndDomain()))
    	{
    		throw new nl.uva.vlet.exception.ResourceDeletionFailedException("I'm refusing to delete you home directory (or it's parent):"+getPath());
    	}
    	    	 
    	boolean childResult=false;
    	
    	ITaskMonitor  monitor = ActionTask.getCurrentThreadTaskMonitor("Deleting (GFTP) directory:"+this.getPath(),1); 
        
        // delete chidren 
        if (recurse=true) 
        {
        	childResult=VDir.defaultRecursiveDeleteChildren(monitor,this);
        }
        
        
        boolean result=srbnode.delete(true); //Does this mean force==true ???
    	
    	if (result==false) 
    	{
    		if (childResult==true)
    			throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Deletion of directory failed, but contents could be deleted. No reason given by server for location:"+this);
    		else
    			throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Deletion failed of directory and it's contents. No reason given by server for location:"+this);
    	}
    	
    	return result; 
    }
    
    
    public long getNrOfNodes()
    {
        return (srbnode.list().length);
    }
    
    public boolean existsFile(String fileName) throws VlException
    {
        SRBFile file = new SRBFile(srbnode,fileName);
        
        return (file.exists() && file.isFile());
    }
    
    public boolean existsDir(String fileName) throws VlException
    {
        SRBFile dir = new SRBFile(srbnode,fileName);
        
        return (dir.exists() && (dir.isAbsolute()));
    }
    
    /** return -1: SRB directories don't have Modifications Times ! */ 
    public long getModificationTime()
    {
        // time is in seconds, must return in millis 
        long val=srbnode.lastModified();
        
        // SRB Dir returns 0 for not know, but 0 = 1 jan 1970 GMT ! 
        if (val==0)
        	return -1;
        
        return val; 
    }
    
    private void Debug(String str)
    {
        Global.debugPrintln(this,str);
    }
    
    /*public boolean isHidden()
    {
        return srbnode.isHidden();
    }*/
    
    
    public SRBFileSystem getSRBFileSystem()
    {
        return (SRBFileSystem)srbnode.getFileSystem();
    }
    
//    
 //    
  //  **************************** 
   // Bulk methods disabled: buggy
  //  ****************************       
 //   
//   /** 
//     * Copy or move this directory to remote destination directory. 
//     * 
//     * This directory will be copied to the destinationDir.
//     * The destination directory MUST Exists and this directory
//     * will be created as a subdirectory in the destination directory. 
//     * 
//     * @param destDir the PARENT destination dir. 
//     * @param optNewName optional new name of (sub) directory. 
//     * @param isMove used for move optimization
//     * @throws VlException 
//     */
//    
//    @Override
//    protected VDir doCopyMoveTo(VFSTransfer transfer, VFileSystem targetFS,VRL targetDirVrl,boolean isMove) throws VlException 
//     {
//        //
//        // Pre: very important, Jargon might f** up
//        // SRB Files can get overwritten by Directory entries and vice versa ! 
//    	// 
//    	
//    	VDir targetParentDir=targetFS.getDir(targetDirVrl.getParent()); 
//    	
//        if (targetParentDir.exists()==false)
//        {
//            throw new ResourceNotFoundException("Target directory does not exist:"+targetParentDir);
//        }
//        
//        if (targetParentDir.isDir()==false)
//        {
//            throw new ResourceNotFoundException("Destination path is not a directory:"+targetParentDir);
//        }
//        
//        // ===
//        // Have only optimisations for SRB files: and bulk Download: 
//        // === 
//
//        boolean destIsLocal=targetParentDir.isLocal();
//
//        
//        if (((targetParentDir instanceof SrbDir)==false) && (destIsLocal))
//        {
//        	return super.doCopyMoveTo(transfer,targetFS,targetDirVrl,isMove); 
//        }
//        
//        // 
//        // SRB <
//        //
//        
//        
//       // two cases left:
//        //  I) copy at same srb server 
//        //  II) copy or move to local directory 
//        
//        try
//        {
//        	String baseName=targetDirVrl.getBasename(); 
//        	
//            GeneralFile srbParentDir = null; 
//            GeneralFile newDir = null; 
//             
//            // TODO:downloadTo method
//            // move to localfile ! 
//            if (destIsLocal==true)
//            {
//                srbParentDir=new LocalFile(targetParentDir.getPath());
//                newDir=new LocalFile(targetParentDir.getPath(),baseName);
//              
//            }
//            else
//            {
//               // copy/move to SRB directory 
//                srbParentDir=((SrbDir)targetParentDir).srbnode;
//                newDir=new SRBFile(((SrbDir)targetParentDir).srbnode,baseName);
//                
//            }
//            // ====
//            // SRB Copy to SRB or LocalFS 
//            // ====
//            
//            // Debug("local dest="+newDir);
//            
//            // check if it already exists 
//            if (newDir.exists())
//            {
//                if (newDir.isDirectory()==false)
//                {
//                    throw new ResourceCreationFailedException("Destination path exists but is not a directory:"+newDir);
//                }
//                else
//                {
//                    // is allowed
//                    // throw new ResourceAlreadyExistsException("Destination directory already exists:"+newDir);
//                }
//            }
//            else
//            {
//                // do not create directory 
//                //newDir.mkdir(); // create new directory
//            }
//            
//            // *** 
//            // Perform SRB copy 
//            // ***  
//            
//            /*
//             if (newDir.exists()==false)
//             {
//             throw new ResourceCreationFailedException("The new directory doesn't exist");
//             } */ 
//            
//            // do the actual copy
//            // start progress task watcher: In bulk mode the progress doesn't work.
//            ActionTask progressTask=server.createProgressWatcher(transfer,srbnode,newDir,true,Thread.currentThread());
//            
//            progressTask.startTask(); 
//
//            // *******************************************************************************
//            // JArgon is not consistant when copying directory. 
//            // Sometimes the directory is the parent directory sometimes it is the actual 
//            // new Directory !!!
//            // *******************************************************************************
//            
//            // AARG srb does not do the same for local and srb directory copy ! 
//            if (destIsLocal)
//            {
//                //  strange: here we must specify the parent directory as target copy                    
//                srbParentDir.copyFrom(srbnode); 
//            }
//            else
//            {
//                newDir.copyFrom(srbnode);  //GeneralFile.copyFrom
//            }
//            
//            progressTask.stopTask();
//              
//            //targetParent.copyFrom(srbnode); 
//            
//            
//            if (isMove)
//                this.delete();
//            
//            // sadly the 'new'srb object is now invalid. Recreate object: 
//            
//            if (destIsLocal==true)
//            {
//                return new LDir(server.getLocalFS(),newDir.getAbsolutePath());
//            }
//            else
//            {
//                return new SrbDir(server,(SRBFile)newDir);
//            }
//            
//        }
//        catch (Exception e)
//        {
//            throw SrbFileSystem.convertException("Could not copy/move file:" + this + "\n",e);
//        }
//    }
    
  
   
//    /** 
//     * Copy/move local directory  to this location.
//     * This method is used by the localfile system when it is requested to upload/copy
//     * a file to this implementation.
//     *  
//     * @throws VlException 
//     */
//    @Override
//    protected VDir uploadLocalDir(final VFSTransfer transfer,VDir dir,String optNewName, boolean isMove) throws VlException
//    {
//        if (this.server.getUsePassiveMode())
//        {
//            // To force Jargon to use single streams, each file has 
//            // to be uploaded one by one (disable bulk mode). 
//            // The default copyMove method of the VFS does this. 
//            // default recursive copy:
//            return defaultCopyMove(transfer,dir,this,optNewName,isMove); 
//        }
//
//            
//        Debug("cmoveFromLocal:this="+this+",dir="+dir);
//        String newName=optNewName;
//        
//        if (newName==null) 
//            newName=dir.getBasename(); 
//        
//        
//        // use Jargon/SRB interface to copy from local 
//        // SRBFile destParentDir = srbnode; // target Directory !
//        
//        SRBFile newDir = new SRBFile(srbnode,newName); // new directory 
//        
//        LocalFile source = new LocalFile(dir.getPath()); // source directory 
//        
//        /* Extra checking: 
//         * target directory MUST exist and MUST be a directory 
//         * or else jargon might do unspecified thingies 
//         */ 
//        if ((srbnode.exists()==false) || (srbnode.isDirectory()==false))
//        {
//            throw new ResourceNotFoundException("Can't copy directory. Invalid target directory:"+this);
//        }
//        
//        if (newDir.exists())
//        {
//            if (newDir.isDirectory()==false)
//            {
//                throw new ResourceCreationFailedException("Destination path exists but is not directory:"+newDir);
//            }
//            else
//            {
//                Debug("exists dest:"+newDir);
//            }
//            
//        }
//        else
//        {
//            // boolean ret=newDir.mkdir(); 
//            
//            //Debug("created dest:"+ret);
//        }
//        
//        try
//        {
//           
//            {
//                // start progress task watcher: In bulk mode the progress doesn't work.
//                ActionTask progressTask=server.createProgressWatcher(transfer,source,newDir,true,Thread.currentThread());
//                
//                progressTask.startTask(); 
//                
//                //  strange: here we must specify the parent directory as target copy                    
//                srbnode.copyFrom(source,true);// alway overwrite
//                    
//                progressTask.stopTask();
//            }
//            
//            // newDir.copyFrom(source);
//            
//            if (isMove)
//            {
//                dir.delete(); // VDir.delete()
//            }
//            
//            // rescan object:
//            
//            newDir = new SRBFile(srbnode,newName); // new directory 
//            
//            // return new local file instance
//            return new SrbDir(server,newDir);  
//            
//        }
//        catch (Exception e)
//        {
//        	throw SrbFileSystem.convertException("Couldn't copy local directory:"+dir+"\nto parent location:"+this,
//        			e); 
// 
//        }
//        
//    }
    
    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
        
        return StringList.merge(superNames,SrbFSFactory.srbDirAttributeNames); 
    }
    
    
    /** Extra SRB Directory attributes */ 
    /*
     @Override
     public VAttribute[] getAttributes(String attrNames[]) throws VlException
     {
     VAttribute attrs[]=new VAttribute[attrNames.length];  
     
     // Query SRB Attributes:  
      
      VAttributeSet attrSets[]=SRBQuery.Query((SRBFileSystem)srbnode.getFileSystem(),
      this.getLocation().getDirname(),this.getName(),true,attrNames,null);
      
      // querying one file ! 
       
       if (attrSets==null)
       {
       Global.errorPrintln("SRBFile","Error: SRB Query for single file result in NULL attribute set:"+this);
       attrSets=new VAttributeSet[0];
       }
       else if (attrSets.length!=1)
       {
       Global.errorPrintln("SRBFile","Error: SRB Query for single file did not result in single attribute set:"+this);
       }
       
       String superAttrNames[]=new String[attrNames.length];
       
       // get attribute names which weren't resolved: 
        for (int i=0;i<attrNames.length;i++)
        {
        if ((attrNames[i]!=null) && (attrSets!=null) && (attrSets.length>0))
        attrs[i]=attrSets[0].get(attrNames[i]);
        
        if (attrs[i]==null) 
        {
        Debug("superAttrname="+attrNames[i]);
        superAttrNames[i]=attrNames[i];
        }
        else
        superAttrNames[i]=null; 
        }
        
        // fetch super attributes, return null attribute for null attributename !
         
         VAttribute superAttrs[]=super.getAttributes(superAttrNames);
         
         // merge superAttributes with SRB atributes 
          
          for (int i=0;i<attrNames.length;i++)
          {
          if (attrs[i]==null)
          attrs[i]=superAttrs[i];
          
          // after merge all attributes should be filled
           
           if (attrs[i]==null)
           {
           Debug("Error: null attribute value for:"+attrNames[i]);
           }
           }
           
           return attrs; // merged attributes
           }*/
    
  
    public VAttribute getAttribute(String name) throws VlException
    {
        VAttribute attr=super.getAttribute(name);
        
        if (attr!=null) 
            return attr;
        
        if (name.compareTo(ATTR_NRACLENTRIES)==0)
        {
            return new VAttribute(ATTR_NRACLENTRIES,server.getNrACLEntries(this.srbnode,false));
        }
        /* already covered by super.getAttribute: 
        else if (name.compareTo(VFSNode.ATTR_PERMISSIONS_STRING)==0) 
        {
            // get optimized and custom SRB String: 
              return new VAttribute(name,getPermissionsString());  
        }
        */
        else if (name.compareTo(SrbConfig.ATTR_RESOURCE)==0) 
        {
              return new VAttribute(name,getResource());  
        }
        
        return null; 
    }
    /**
     * For SRB Optimized getAttributes 
     */
    /* No optimized Directory attributes 
    @Override
    public VAttribute[] getAttributes(String attrNames[]) throws VlException
    {
       // Query SRB Attributes:  
       
       VAttributeSet attrSets[]=SRBQuery.Query((SRBFileSystem)srbnode.getFileSystem(),
               this.getLocation().getPath(),null,true,attrNames,null);
      
       VAttributeSet set=null; 
       
       if ((attrSets==null) || (attrSets.length<=0))
       {
           Global.errorPrintln("SRBFile","Error: SRB Query resulted in NULL attribute sets:"+this);
           set=null;  
       }
       // Query duplication (need equivalent of UNIQUE)  
       else if (attrSets.length!=1)
       {
    	   set=attrSets[0];
           Global.errorPrintln("SRBFile","Error: SRB Query for single file resulted in multiple attribute sets:"+this);
       }
       else
       {
    	   set=attrSets[0]; 
       }
     
       VAttribute[] attrs = mergedGetAttributes(set,attrNames);
       
       // update editable flag on attribute list (non for directories, yet) 
       // setEditableAttributes(attrs);
       
       return attrs; 
    }*/ 
    
    
    public String getPermissionsString() throws VlException
    {
        return this.server.getPermissionsString(srbnode,true); 
    }
   
    public String getResource() throws VlIOException 
    {
        try
        {
            return srbnode.getResource();
        }
        catch (IOException e)
        {
            throw new VlIOException("SRB IOException",e); 
        } 
    }
    
    
    public VAttribute[][] getACL() throws VlIOException
    {
        return this.server.getACL(this.srbnode,true);
    }
    
    public VAttribute[] getACLEntities() throws VlIOException
    {
        SRBFileSystem srbfs=(SRBFileSystem)srbnode.getFileSystem(); 
        
        return SrbFileSystem.getACLEntities(srbfs); 
    }   
    
    public VAttribute[] createACLRecord(VAttribute entity, boolean writeThrough) throws VlException
    {
        SRBFileSystem srbfs=(SRBFileSystem)srbnode.getFileSystem(); 
        
        return SrbFileSystem.createACLRecord(srbfs,entity,true);  
    }
    
    public void setACL(VAttribute acl[][]) throws VlException
    {
    	 server.setACL(this.srbnode,acl,true);
    }   
    
    public boolean deleteACLEntity(VAttribute entity) throws VlException
    {
        return SrbFileSystem.deleteACLEntry(this.srbnode,entity);
    }
    
    
}
