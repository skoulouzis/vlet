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
 * $Id: SrbFile.java,v 1.5 2011-11-25 13:43:43 ptdeboer Exp $  
 * $Date: 2011-11-25 13:43:43 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_DATA_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NRACLENTRIES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRenamable;
import nl.uva.vlet.vrs.io.VRandomAccessable;
import nl.uva.vlet.vrs.io.VResizable;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;
import edu.sdsc.grid.io.local.LocalFile;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileOutputStream;
import edu.sdsc.grid.io.srb.SRBFileSystem;
import edu.sdsc.grid.io.srb.SRBRandomAccessFile;
/** 
 * 
 * Implementation of SrbFile (name 'SRBFile' is already taken). 
 * 
 */

public class SrbFile extends VFile implements VStreamReadable,
VStreamWritable,VRandomAccessable,VRenamable,VResizable 
{
    SrbFileSystem server=null; 
    
    /** Jargon GeneralFile interface */

    SRBFile srbnode = null;

   
    
    public SrbFile(SrbFileSystem server,SRBFile gfile)
    {
        super(server,null); // will set location init! 
        init(server,gfile);
    }
    
    public SrbFile(SrbFileSystem server,String filepath)
    {
    	 super(server,null); // will set location init! 
        
        SRBFileSystem srbfs=server.getSRBFileSystem(); 
        
        init(server,new SRBFile(srbfs,filepath));  
    }

    private void init(SrbFileSystem server,SRBFile gfile)
    {
        this.server=server; 
        this.srbnode = gfile;
        // use Jargon's toURI
        this.setLocation(new VRL(gfile.toURI())); 
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
    
    public void setResource(String resource) throws VlIOException 
    {
        try
        {
            srbnode.setResource(resource); 
        }
        catch (IOException e)
        {
            throw new VlIOException("SRB IOException",e); 
        } 
    }
    
    @Override 
    public boolean setAttribute(VAttribute attr) throws VlException
    {
    	String name=attr.getName(); 

    	try
    	{

    		if (name.compareTo(SrbConfig.ATTR_RESOURCE)==0)
    		{
    			setResource(attr.getStringValue());
    			return true; 
    		}
    		else if (name.compareTo(ATTR_DATA_TYPE)==0)
    		{
    			this.srbnode.setDataType(attr.getStringValue());
    			return true; 
    		}
    	}
    	catch (IOException e)
    	{
    		throw new VlIOException("SRB IOException",e); 
    	} 

    	return super.setAttribute(attr); 
    }
    
    public boolean isReadable() throws VlException
    {
        //return this.srbnode.canRead(); 
        
        return this.server.isReadable(srbnode,false); 
    }
    
    public boolean isWritable() throws VlException
    {
        //return this.srbnode.canWrite(); 
        
        return this.server.isWritable(srbnode,true); 
    }
    
    @Override
    public boolean create(boolean ignoreExisting) throws VlException
    {
        try
        {
            return srbnode.createNewFile();
        }
        catch (IOException e)
        {
            throw new ResourceCreationFailedException("Could not create file:"
                    + this,e);
        }
    }

    public VRL rename(String name,boolean nameIsPath) throws VlException
    {
        try
        {
            SRBFile newSrbFile=SrbFSFactory.srbRename(srbnode,name,nameIsPath);
            
            if (newSrbFile!=null)
                new VRL(newSrbFile.toURI()); 
        }
        catch (IOException e)
        {
            throw new VlIOException(e.getMessage()); 
        } 
        
        return null; 
    }
    
    //@Override
    public boolean delete() throws VlException
    {
    	boolean result=srbnode.delete(true); //Does this mean force==true ???
    	
    	if (result==false) 
    		throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Deletion failed. No reason given by server for:"+this);
    	
    	return result; 
    }
    
    @Override
    public long getModificationTime()
    {
        return srbnode.lastModified();
    }
    /*@Override
    public boolean isHidden()
    {
        return srbnode.isHidden();

    }*/

//    /**
//     * Copy/Move SRB File to destination directory.
//     * TODO: Cleanup code and check with super.doCopyMoveTo
//     */
//
//    public VFile doFileTransferToOld(VFSTransfer transfer,VDir targetParentDir, String optNewName,boolean isMove) throws VlException
//    {
//        boolean isSrbMove=false;
//        boolean destIsLocal=false; 
//        boolean force=true; // default is to overwite existing files.
//        
//        String newName=optNewName; 
//        
//        if (newName==null) 
//            newName=getBasename(); 
//        
//        if (targetParentDir.exists()==false)
//        {
//          /* Parent dir must exist, or else JArgon 'might' use non existing
//           * path as destination file name (AARG), thus performing a RENAME
//           */  
// 
//           throw new ResourceNotFoundException("destination directory does not exist:"+targetParentDir); 
//        }
//        
//        try
//        {
//            GeneralFile dest=null; 
//            GeneralFile srbParentDir=null; 
//    
//            if (targetParentDir.isLocal())
//            {
//               
//                // copy/move to local path:
//                
//                srbParentDir=new LocalFile(targetParentDir.getPath());
//                // destination file:
//                dest=new LocalFile(targetParentDir.getPath(),newName);
//                destIsLocal=true; 
//            }
//            else if ( (targetParentDir instanceof SrbDir) 
//                      && (this.getVRL().hasSameServer(targetParentDir.getLocation())) )
//            {
//                // SRB2SRB copy/move on same server !
//                
//                srbParentDir=((SrbDir)targetParentDir).srbnode;
//                dest=new SRBFile(((SrbDir)targetParentDir).srbnode,newName);
//                
//                if (isMove)
//                {
//                   
//                    // move 
//                    SRBFile srbFile=SrbFSFactory.srbMove(srbnode,(SRBFile)srbParentDir,newName); 
//                    return new SrbFile(server,srbFile);
//                }
//                else
//                {
//                 
//                }
//            }
//            else
//            {
//                // copy to another SRB Server or other VFS implementation
//                // Use Default Stream Based  Copy
//            	VRL targetFileVRL=targetParentDir.getVRL(); 
//            	VFile targetFile=targetParentDir.getFileSystem().newFile(targetFileVRL); 
//            	
//                fileStreamCopy(transfer,this,targetFile,this.getLength()); 
//                
//            	if (isMove)
//            		this.delete(); 
//
//            	
//                return targetFile; 
//            }
//            
//            Debug("local dest="+dest);
//             
//            // check if it already exists 
//             
//            if (dest.exists())
//            {
//                if  (force==false)
//                {
//                    if (dest.isFile()==false)
//                    {
//                        throw new ResourceCreationFailedException("Destination path exists but is not a file:"+dest);
//                    }
//                    else
//                    {
//                        throw new ResourceAlreadyExistsException("Destination File already exists:"+dest);
//                    }
//                 }
//                 else
//                 {
//                	 boolean ret=false;
//                	 
//                	 if (dest instanceof SRBFile)
//                     // must delete it, jargon does not delete if for us.
//                		 ret=((SRBFile)dest).delete(true);
//                	 else
//                		 ret=dest.delete(); 
//                	 
//                     if (ret==false) 
//                          throw new ResourceDeletionFailedException("Could not delete existing location:"+dest);
//                 }
//             }
//              
//        
//                    
//             
//             // When creating the file first, jargon is triggered
//             // to use a SINGLE upload stream to the new file.  
//             // If you want to use parallel (or bulk) load, do NOT 
//             // create the file first. For big files this is more efficient.
//            
//             // see: https://lists.sdsc.edu/pipermail/srb-chat/2004-December/001480.html
//             /* it says:  
//                You can force a serial transfer. Insert one more line in front of your
//                copyTO or copyFrom method calls. so if your code previously read
//
//                local.copyTo(srbFile)
//
//                insert one line
//
//                srbFile.createNewFile();
//                local.copyTo(srbFile);
//             */
//            
//             //NOT: boolean ret = dest.createNewFile();
//             //Debug("created dest:"+ret);
//            
//            // This method doesn NOT do upload of files, since this 
//            // is a SrbFile ! (LocalFile call VDir.uploadFile(...)) 
//
//            {
//                // start progress task watcher: Does not work for GeneralFile ??
//                ActionTask progressTask=null;
//                 
//               
//                progressTask=server.createProgressWatcher(transfer,srbnode,dest,false,Thread.currentThread());
//                
//                progressTask.startTask(); 
//                
//                srbnode.copyTo(dest);
//                
//                progressTask.stopTask();
//            }
//             
//             if (isMove)
//                 this.delete();
//             
//             if (destIsLocal==true)
//             {
//                 return new LFile(server.getLocalFS(),dest.getAbsolutePath());
//             }
//             else
//             {
//                 return new SrbFile(server,(SRBFile)dest);
//             }
//        }
//        catch (SRBException e)
//        {
//            throw new VlIOException("Could not copy/move file:" + this + "\n"
//                    + "to:"+targetParentDir+"\n"
//                    + "Message from SRB server:" + e.getStandardMessage(), e);
//        }
//        catch (IOException e)
//        {
//            throw new VlIOException("Could not copy/move file:" + this + "\n"
//                    + "to:"+targetParentDir+"\n"
//                    +"Message="+e.getMessage(), e);
//        }
//    }
    
    // Bulk Download b0rken 
//    protected VFile downloadTo(VFSTransfer transfer,VDir localDir,String optNewName,boolean isMove)
//    throws VlException
//    {
//    	return this.doFileTransferTo(transfer, localDir, optNewName, isMove);
//    }
    
    /** 
     * Copy/move local file to this location.<br>
     * This method is used by the localfile system when it is requested 
     * to upload/copy a file to this implementation. 
     * It is more efficient to let the SRB implementation do the 
     * local file access because it has bulk up- and download methods. 
     */
    @Override
    public void uploadFrom(VFSTransfer transfer,VFile file) throws VlException
    {
        // use Jargon/SRB interface to copy from local  
        Debug("cmoveFromLocal:"+file+"to:"+this); 
        
        SRBFile destFile = this.srbnode; 
        LocalFile source = new LocalFile(file.getPath()); // file MUST be local ! 
        
        /* Extra checking: 
         * target directory MUST exist and MUST be a directory 
         * or else jargon might do unspecified thingies 
         */ 
        if (srbnode.getParentFile().exists()==false)
        {
            throw new ResourceNotFoundException("Can't copy file. Invalid parent target directory:"+this);
        }
        
        ActionTask progressTask=null;
        
        try
        {
            if (destFile.exists()==true)
            {
                if (destFile.isFile()==false)
                {
                    throw new ResourceCreationFailedException("Target destination already exists, but is not a file:"+destFile); 
                }
                else
                {
                    // delete EXISTING so that  bulkload can go parallel: 
                    destFile.delete(true); 
                }
            }
            else
            {
                // try to create it 
                //dest.createNewFile(); // srb does this automatically !  
            }
            
            // make sure destination is a file, or else Jargon makes a directory out of it !
            
            //destDir.copyFrom(source);  // copy to this directory
            Debug("cmoveFromLocal:starting"); 
            
            // === 
            // Method One 
            // === 
            
            // destFile.copyFrom(source);  // copy to target file
            
            // === 
            // Method Two 
            // see: https://lists.sdsc.edu/pipermail/srb-chat/2004-December/001480.html
            // ===
            
            // From the above linke, add this statement forces serial tranfer ??? 
            //destFile.createNewFile();
            
            if (this.server.getUsePassiveMode())
                destFile.createNewFile();
            {
                // start progress task watcher 
                progressTask=server.createProgressWatcher(transfer,source,destFile,false,Thread.currentThread());
                
                progressTask.startTask(); 
                
                source.copyTo(destFile); 
                    
                // Argg. copyTo *might* be asychronous
                while(destFile.length()<source.length())
                {
                    System.err.println("*** Copy NOT yet finished:"+destFile);
                    
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        System.out.println("***Error: Exception:"+e); 
                        e.printStackTrace();
                    }
                    
                }
                // stop the progress watcher
                progressTask.stopTask();
            }
            
            // === 
            //dest.createNewFile(); 
            //srbnode.copyTo(dest); 
            
            Debug("cmoveFromLocal:ending"); 
            
            return; // ok ? 
        }
        catch (IOException e)
        {
            if (progressTask!=null)
            {
                progressTask.stopTask();
            }
            
            throw SrbFileSystem.convertException("Couldn't copy local file:"+file+"to SRB destination:"+destFile+"\n",e); 
        }
    }
    
    
    // === 
    // Misc ...
    // ===
    
    private static void Debug(String string)
    {
       Global.debugPrintln("SrbFile",string);
    }

    /** Returns input stream to remote SRBFile ! */ 
    
    public InputStream getInputStream() throws VlException
    {
        try
        {
            // Patched Version: !
            return new PATCHEDSRBFileInputstream(srbnode);
            //return new SRBFileInputStream(srbnode);
        }
        catch (IOException e)
        {
            throw new VlIOException("Could not get InputStream of:"+this+
                    "\nMessage="+e.getMessage(),e); 
        } 
    }
    
    public OutputStream getOutputStream() throws VlException
    {
        return getOutputStream(true);
    }
    
    public OutputStream getOutputStream(boolean truncate) throws VlException
    {
        try
        {
            if (truncate)
            {
                // truncate file BEFORE writing. 
                // SBR emulated an outputstream by doing random access
                // writes which do not truncate an existing file.
                this.srbnode.delete(); 
            }
            return new SRBFileOutputStream(srbnode); 
        }
        catch (IOException e)
        {
            throw new VlIOException("Could not get OutputStream of:"+this,e); 
        } 
    }

    public void setLength(long len) throws VlException
    {
        SRBRandomAccessFile afile=null; 
        
        try
        {
            afile = new SRBRandomAccessFile(srbnode,"rw");
            afile.setLength(len);
            afile.close(); 
            //afile.finalize();
        }
        catch (Exception e)
        {
        	throw convertException(e); 
        }
    }

    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws VlException
    {
        SRBRandomAccessFile afile=null; 
        
        try
        {
            afile = new SRBRandomAccessFile(srbnode,"r");
            afile.seek(fileOffset); 
            int numread=afile.read(buffer,bufferOffset,nrBytes);
            afile.close(); // MUST CLOSE ! 
            //afile.finalize(); // destroy resources 
            afile=null;
            
            return numread; 
        }
        catch (Exception e)
        {
        	throw convertException(e); 
        }
    }
    
    /**
     * From VRandomAccessable Interface  
     * 
     * @param fileOffset  
     * @param buffer
     * @param bufferOffset
     * @param nrBytes
     * @param makeEmpty
     * @throws VlException
     */

    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws VlException
    {
        SRBRandomAccessFile afile=null;
        
        try
        {
            // NOT YET IMPLEMENTED BY JARGON !!! AARRGGGSSSS 
            //if (truncate) 
            //    afile.setLength(fileOffset+nrBytes);
            // Currently truncate ONLY works when starting to append to the beginning !
            // since jargon doesn't yet implement the setLength method !!!
        
            afile = new SRBRandomAccessFile(srbnode,"rw");
            afile.seek(fileOffset); 
            afile.write(buffer,bufferOffset,nrBytes);
            afile.close(); // MUST CLOSE ! 
            //afile.finalize(); 
            
            return; 
        }
        catch (Exception e)
        {
        	throw convertException(e); 
        }
    }
    /** Converts: 
     * <li>IllegalArgumentException
     * <li>FileNotFoundException
     * <li>SecurityException 
     * <li>IOException 
     *    
     * @param e
     * @return
     */
    private VlException convertException(Exception e)
    {
    	if (e instanceof IllegalArgumentException)
        {
            return new VlException("SRB IllegalArgumentException",e.getMessage(),e); 
        }
    	if (e instanceof FileNotFoundException)
        {
        	return new ResourceNotFoundException("SRB FileNotFoundException",e); 
        }
    	if (e instanceof SecurityException)
    	{
        	return new VlException("SRB SecurityException",e.getMessage(),e); 
        }
    	if (e instanceof IOException)
        {
        	return new VlIOException("SRB IOException",e);
        }
    	
    	return new VlException("SRB Exception",e.getMessage(), e); 
	}

	/** From VRandomAccessable interface */ 

    public void setLengthToZero() throws VlException
    {
       try
       {
           srbnode.delete(true);
           srbnode.createNewFile();
       }
       catch (IOException e)
       {
          throw new VlIOException("SRB IOException when setting file length to zero:"+this,e); 
       }
    }
    
    /**
     * Fetch atrributes which are not in the Attribute Set. 
     * This makes it possible (for subclasses) to merge several getAttributes() calls 
     * into one AttributeSet without having to do the attribute checking themselves.
     * <b> 
     * The returned array matches the length and order of attrNames[].<br>
     * Entry may be null if VAttribute couln't be fetched.
     * @param attrSet
     * @param attrNames
     * @return
     * @throws VlException
     */
    public VAttribute[] mergedGetAttributes(VAttributeSet attrSet,String attrNames[]) throws VlException
    {
       VAttribute attrs[]=new VAttribute[attrNames.length];  
       String missingNames[]=new String[attrNames.length];
     
       // get attribute names which are not in the given set: 
       for (int i=0;i<attrNames.length;i++)
       {
           // check Set for match 
           if (attrNames[i]!=null)
               if (attrSet!=null)
                   attrs[i]=attrSet.get(attrNames[i]);

           // attribute not in set: 
           if (attrs[i]==null) 
           {
               Global.debugPrintln(this,"mergedGetAttributes: fetching Atributename="+attrNames[i]);
               missingNames[i]=attrNames[i];
                   
               //optional filter missingNames[i]...
               
               // use default method:
               if (missingNames[i]!=null)
                   attrs[i]=getAttribute(missingNames[i]);
           }
           else
           {
               missingNames[i]=null; // do not fetch
           }
       }
       
       return attrs; // merged attributes
    }
    
    
    /**
     * For SRB Optimized getAttributes 
     */
    @Override
    public VAttribute[] getAttributes(String attrNames[]) throws VlException
    {
       // Query SRB Attributes:  
       
       VAttributeSet attrSets[]=SrbQuery.Query((SRBFileSystem)srbnode.getFileSystem(),
               this.getLocation().getDirname(),this.getName(),false,attrNames,null);
      

       if (attrSets==null)
       {
          Global.errorPrintln("SRBFile","Error: SRB Query resulted in NULL attribute sets:"+this);
       }
       // Query duplication (need equivalent of UNIQUE)  
       else if (attrSets.length!=1)
       {
         Global.errorPrintln("SRBFile","Error: SRB Query for single file resulted in multiple attribute sets:"+this);
       }
       
       VAttribute[] attrs=null;
       
       if (attrSets==null) 
           attrs = mergedGetAttributes(null,attrNames);
       else
           attrs = mergedGetAttributes(attrSets[0],attrNames);
       
       // update editable flag on attribute list 
       setEditableAttributes(attrs);
       
       return attrs; 
    }
   
    
    private void setEditableAttributes(VAttribute[] attrs)
    {
        if (attrs==null) 
            return; 
        
        for (VAttribute attr:attrs)
        {
            // attributes can be null ! 
            if (attr!=null)
            {
               String name=attr.getName();
               if (name.compareTo(SrbConfig.ATTR_RESOURCE)==0)
                  attr.setEditable(true);
               else if (name.compareTo(ATTR_DATA_TYPE)==0)
                   attr.setEditable(true);
            }
        }
        
    }

    public VAttribute getAttribute(String name) throws VlException
    {
      if (name==null) 
          return null; 
        
     
      if (name.compareTo(ATTR_NRACLENTRIES)==0)
      {
          return new VAttribute(ATTR_NRACLENTRIES,server.getNrACLEntries(this.srbnode,false));
      }
      else if (name.compareTo(ATTR_PERMISSIONS_STRING)==0) 
      {
          // get optimized and custom SRB String: 
            return new VAttribute(name,getPermissionsString());  
      }
      else if (name.compareTo(SrbConfig.ATTR_RESOURCE)==0) 
      {
            VAttribute attr = new VAttribute(name,getResource());
            // Resource should be editable 
            attr.setEditable(true); 
            return attr;
      }

      
      // check super object: 
      
      VAttribute attr=super.getAttribute(name);
      
      if (attr!=null) 
          return attr;
            
       return null; 
    }
    
    public String getPermissionsString() throws VlException
    {
        return this.server.getPermissionsString(srbnode,false); 
    }
    
    /*** // TODO: getAttributes(Nodes[],Names[]) => Query metadata
    public VAttribute[][] getAttributes(VNode nodes[],String attrNames[]) throws VlException
    {
      return null; 
    }
    ***/
   
    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,SrbFSFactory.srbFileAttributeNames); 
    }
   
   
    public VAttribute[][] getACL() throws VlIOException
    {
        return server.getACL(this.srbnode,false);
    }
    
    public void setACL(VAttribute acl[][]) throws VlException
    {
        server.setACL(this.srbnode,acl,false);
    }   
    
    public VAttribute[] getACLEntities() throws VlIOException
    {
        SRBFileSystem srbfs=(SRBFileSystem)srbnode.getFileSystem(); 
        
        return SrbFileSystem.getACLEntities(srbfs); 
    }   
    
    public VAttribute[] createACLRecord(VAttribute entity, boolean writeThrough) throws VlException
    {
        SRBFileSystem srbfs=(SRBFileSystem)srbnode.getFileSystem(); 
        
        return SrbFileSystem.createACLRecord(srbfs,entity,false);  
    }
    
    public boolean deleteACLEntity(VAttribute entity) throws VlException
    {
        return SrbFileSystem.deleteACLEntry(this.srbnode,entity);
    }
    
}
