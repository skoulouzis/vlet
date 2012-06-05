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
 * $Id: VFSClient.java,v 1.11 2011-06-07 15:13:52 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:52 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import java.io.Serializable;
import java.util.regex.Pattern;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;

/**
 * VFSClient class is a client interface to the Virtual File System classes and methods.
 * It provides a stable interface to the methods of the different Filesystem  
 * implementations.
 * <p>
 * Use  method openLocation(VRL) to get any file/directory 
 * anywhere on the grid.
 * To start using the VFS, create your own VFSClient as follows:<br> 
 * <pre> 
 * VFSClient vfs=new VFSClient();
 * //use vfs methods 
 * </pre>
 * Or:
 * <pre>
 * VRSContect contex=new VRSContext(); 
 * // use context to set/configure your environment
 * VFSClient vfs=new VFSClient(context); 
 * </pre>
 *  
 * @author P.T. de Boer
 * @see nl.uva.vlet.vfs.VFS
 * @see nl.uva.vlet.vfs.VFileSystem
 * @see nl.uva.vlet.vfs.VFSNode
 * @see nl.uva.vlet.vfs.VDir
 * @see nl.uva.vlet.vfs.VFile
 *  
 */
public final class VFSClient extends VRSClient implements Serializable 
{
    // ========================================================================
    // Class
    // ========================================================================
    
    // needed for Serializable      
    private static final long serialVersionUID = -2705281290761113901L;

    /** class object */ 
    private static VFSClient defaultVFSClient=new VFSClient();
    
    /** Returns default class object */ 
    public static VFSClient getDefault()
    {
        return defaultVFSClient;
    }
    
    // ========================================================================
    // Instance: Members must be Serializable !
    // ========================================================================
    
     private VRL tempDirLocation=null; 
    
    /** Create VFS Client object */
    public VFSClient()
    {
        super(); 
        init();
    }
    
    private void init()
    {
        // current default tempdir 
        tempDirLocation=Global.getDefaultTempDir();
    }
    
    /** Create VFS Client object using the specified VRS Context. */
    public VFSClient(VRSContext context)
    {
        super(context); 
        this.tempDirLocation=Global.getDefaultTempDir(); 
    }
    
    /** Returns VFSNode pointing to the specified location */ 
    public  VFSNode openLocation(String location) throws VlException
    {
        return openLocation(resolve(location));
    }
    
    /** Returns VFSNode pointing to the specified location */ 
    public  VFSNode openLocation(VRL location) throws VlException
    {
        if (location.isAbsolute()==false)
            location=resolve(location); 
        
        VNode node=getVRSContext().openLocation(location); 
        
        if (node==null)
            throw new nl.uva.vlet.exception.VlInternalError("Couldn open location. Get NULL object for location:"+location); 
         
        if (node instanceof VFSNode) 
            return ((VFSNode)node);
      
        throw new ResourceTypeNotSupportedException("VRL is not a VFS location:"+location);
    }
    
    /** @see #openFile(VRL) */ 
    public  VFile getFile(String locStr) throws VlException
    {
        return openFile(resolve(locStr)); 
    }
    
    /**
     * Resolve optional relative location string (URI/VRL) to 
     * current working directory of this VFSClient. 
     * @throws VRLSyntaxException 
     */
    public VRL resolve(String locStr) throws VRLSyntaxException
    {
        VRL vrl=new VRL(locStr);
        
        if (vrl.isAbsolute()==true)
            return vrl; 
        
        // check relative locations against current working dir. 
        return this.getWorkingDir().resolvePath(locStr); 
    }

    /**
     * Resolve optional relative location string (URI/VRL) to 
     * current working directory of this VFSClient. 
     * @throws VRLSyntaxException 
     */
    public VRL resolve(VRL relLoc) throws VRLSyntaxException
    {
        if (relLoc==null)
            return this.getWorkingDir(); 
        
        if (relLoc.isAbsolute()==true)
            return this.getWorkingDir();
        
        // check relative locations against current working dir. 
            
        return this.getWorkingDir().resolve(relLoc);  
    }

    /** Returns remote File or Directory specified by the location*/ 
    public VFSNode getVFSNode(String vrl) throws VRLSyntaxException, VlException
    {
        return getVFSNode(new VRL(vrl)); 
    }
    
    /** Returns remote File or Directory specified by the location */ 
    public  VFSNode getVFSNode(VRL location) throws VlException
    {
        VFSNode node=openLocation(location); 
                
        if (node instanceof VFSNode) 
            return (VFSNode)node; 
      
        throw new ResourceTypeNotSupportedException("VRL is not a VFSnode:"+location);
    }
    
    /** @see #openFile(VRL) */ 
    public VFile getFile(VRL location) throws VlException
    {
    	return openFile(location); 
    }
    
    /** 
     * Open remote file location and return VFile. 
     * The (remote) file must exist. 
     */  
    public  VFile openFile(VRL location) throws VlException
    {
        VFSNode node=openLocation(location); 

        // extra checks: (Exceptions should already have been thrown:) 
        if (node==null)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't get file:"+location);
        
        //
        if (node.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("File doesn't exist:"+location);
        
        if (node instanceof VDir)
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource is not file, but a directory:"+location);
        
        if (node instanceof VFile) 
            return (VFile)node;
        
        throw new ResourceTypeNotSupportedException("Resource is not a File:"+location);
        
    }
    
    /** 
     * Resolve (optional relative) location to current workind directory and return VDir. 
     * The resolved directory location must exist. 
     */  
    public  VDir getDir(String locStr) throws VlException
    {
        return openDir(resolve(locStr)); 
    }

    /** @see #openDir(VRL) */ 
    public  VDir getDir(VRL location) throws VlException
    {
    	return openDir(location);
    }
    
    /** 
     * Open remote directory location and return VDir. 
     * The (remote) directory must exist. 
     */ 
    public  VDir openDir(VRL location) throws VlException
    {
        VFSNode node=openLocation(location); 
        
        if (node instanceof VDir) 
            return (VDir)node; 
      
        throw new ResourceTypeNotSupportedException("VRL is not VDir:"+location);
        
    }
    
    /** 
     * Move VFile to remote (VDir) destination.
     * The default implementation will always overwrite existing file(s). 
     * <p> 
     * Calls VFile.moveTo()
     * @return new VFile node if move succeeded.    
     * @throws VlException
     */
    public  VFile move(VFile file, VDir dest) throws VlException
    {
        return file.moveTo(dest); 
    }
    
    /** Move VFile to target file. */ 
    public VFile move(VFile sourceFile,VFile targetFile) throws VlException 
    {
        return sourceFile.moveTo(targetFile); 
    }

    
    /** 
     * Copy VFile to remote (VDir) destination.
     * The default implementation will always overwrite existing file(s).
     * <p> 
     * Calls VFile.copyTo()
     * @return new VFile node if move succeeded.    
     * @throws VlException
     */
    
    public  VFile copy(VFile vfile, VDir parentDir) throws VlException
    {
        return vfile.copyTo(parentDir); 
    }
    
    public void copy(VFile file, VFile targetFile) throws VlException
    {
        file.copyTo(targetFile); 
    }

    
    /**
     * Copy single VDir.
     * New Directory will be created as child of parentDir. 
     * The default implementation will always overwrite existing file(s) 
     */ 
    public  VDir copy(VDir dir, VDir parentDir) throws VlException
    {
        return dir.copyTo(parentDir); 
    }
    
    /** 
     * Copy single VDir in the background. 
     * New Directory will be created as child of parentDir. 
     * Returns VFSTransfer object which can be used to monitor the result. 
     */ 
    public  VFSTransfer asyncCopy(VDir dir, VDir parentDir) throws VlException
    {
        // create new director as sub directory of parentDirectory: 
        VRL targetVRL=parentDir.resolvePathVRL(dir.getBasename()); 
        return dir.asyncCopyMoveTo(parentDir.getFileSystem(),targetVRL,false);
         
    }

    /**
     * Copy single VFile in the background. 
     * The default implementation will always overwrite existing file(s).
     * Returns VFSTransfer object which can be used to monitor the result.
     */ 
    public  VFSTransfer asyncCopy(VFile file, VDir parentDir) throws VlException
    {
        // create new file in parent directory 
        VRL targetVRL=parentDir.resolvePathVRL(file.getBasename()); 
        return file.asyncCopyMoveTo(parentDir.getFileSystem(),targetVRL,false);
    }
    
    /** 
     * Move a single directory in the background. 
     * New directory will be created as subdirectory of the parentDir. 
     * 
     * Returns VFSTransfer object which can be used to monitor the result.
     */ 
    public  VFSTransfer asyncMove(VDir dir, VDir parentDir) throws VlException
    {
        // create new file in parent directory 
        VRL targetVRL=parentDir.resolvePathVRL(dir.getBasename()); 
        return dir.asyncCopyMoveTo(parentDir.getFileSystem(),targetVRL,true);
    }

    /**
     * Move a single VFile in background to the target directory. 
     * Returns VFSTransfer object which can be used to monitor the result.
     */ 
    public  VFSTransfer asyncMove(VFile file, VDir parentDir) throws VlException
    {
        // create new file in parent directory 
        VRL targetVRL=parentDir.resolvePathVRL(file.getBasename()); 
        return file.asyncCopyMoveTo(parentDir.getFileSystem(),targetVRL,true);
    }
   
    /**
     * Move directory to destination directory. 
     * The new directory will always be created as subdirectory of the destination
     * directory. 
     *  
     * @param dir  the source directory 
     * @param dest the destination directory which is the parent of the new directory. 
     * @return new directory which is created after the move and is a subdirectory of destDir 
     * @throws VlException
     */
    public  VDir move(VDir sourceDir, VDir destDir) throws VlException
    {
        return sourceDir.moveTo(destDir);
    }
    
    /**
     * Generic rename method. 
     * If the newName starts with a '/' then the full path of the file 
     * or directory will be renamed. 
     * 
     * @param VRL original file or directory
     * @param pathOrName is new name or complete path 
     * @return true if rename was successful  
     * @throws VlException
     */
    public  boolean rename(VRL vrl,String pathOrName) throws VlException
    {
        boolean nameIsPath=false; 
        
        VFSNode node=getVFSNode(vrl);
        
        if ((pathOrName!=null) && (pathOrName.charAt(0)==VRL.SEP_CHAR))
            nameIsPath=true; 
        
        return node.renameTo(pathOrName,nameIsPath); 
    }
    
    /** 
     * Returns true if location is a directory and it exists. 
     * Returns false if location doesn't exists or isn't a directory. 
     * @throws VlException 
     */ 
     public boolean existsDir(VRL location) throws VlException
     {
         return openFileSystem(location).newDir(location).exists();   
     }
     
     /** 
      * Returns true if location is a directory and it exists. 
      * Returns false if location doesn't exists or an exception occurred.  
      * @throws VlException 
      */ 
     public boolean existsDir(String location) throws VlException
     {
         return existsDir(new VRL(location)); 
     }
    
    /** 
     * Returns true if location is a File and it exists. 
     * Returns false if location doesn't exists   
     * @throws VlException 
     */ 
     public boolean existsFile(VRL location) throws VlException
     {
        return newFile(location).exists(); 
     }
    
    /** 
     * Returns true if location is a File and it exists. 
     * Returns false if location doesn't exists. 
     * <p>
     * Implementation note: if a File does not exists throw 
     * nl.uva.vlet.exception.ResourceNotFoundException because 
     * this method will catch that exception. 
     *  
     * @throws VlException 
     */ 
     public boolean existsFile(String location) throws VlException
     {
        return existsFile(new VRL(location)); 
     }
     
     /** Returns true if path is either a file or an directory */ 
     public boolean existsPath(VRL location) throws VlException
     {
    	 VFileSystem vfs = openFileSystem(location); 
    	 if (vfs.newFile(location).exists())
    		 return true; 
    	 if (vfs.newDir(location).exists())
    		 return true; 
    	 return false; 
     }
    
    /** Close this client and release resources */ 
    public void close()
    {
        // currenlty no cleanup has to be done
    }

    /**
     * Tries to create the directory. Parent directory must exist. 
     * For full path creation call: mkdirRecursive().
     */ 
    public VDir mkdir(VRL loc) throws VlException
    {
        return mkdir(loc,true); 
    }
    
    /**
     * Tries to create the directory. Parent directory must exist. 
     * For full path creation call: mkdirs().
     */ 
    public VDir mkdir(String loc) throws VlException
    {
        return mkdir(new VRL(loc),true); 
    }

    /**
     * mkdir: Create single directory. 
     * Parent Directory must exist. 
     * For full path creation call: mkdirs().
     *  
     * @see #mkdirs(VRL, boolean);  
     */ 
    public VDir mkdir(VRL loc,boolean ignoreExisting) throws VlException
    {
    	VDir dir=openFileSystem(loc).newDir(loc);
    	dir.create(ignoreExisting); 
    	return dir; 
    }
    
    /**
     * Recursive mkdir. Creates full directory path.  
     * @see mkdirs(VRL,boolean) 
     */ 
    public VDir mkdirs(VRL loc) throws VlException
    {
	   return mkdirs(loc,true); 
    }

    /**
     * Recursive mkdir. 
     * Tries to create the full path of the location VRL by creating each
     * subdirectory one by one.  
     * @param VRL full path of the new Directory. 
     * @param ignoreExisting if ignoreExisting==false, only create new directory when 
     *        it doesn't exist yet. Throw ResourceException when it does exist. 
     */ 
    public VDir mkdirs(VRL loc,boolean ignoreExisting) throws VlException
    {
        // check parent:
        VRL parentLoc=loc.getParent();
        
        // use new (optimized?) filesystem methods: 
        VFileSystem fs = this.openFileSystem(loc);        
        
        boolean parentExists=fs.newDir(parentLoc).exists();  
    
        // check parent: 
        if (parentExists==false)
        {
            if (loc.isRootPath()==true)
            {
                throw new ResourceCreationFailedException("Cannot Create Directory. Root path doesn't seem to exist:"+loc);
            }
            else
            {
                // recurse create parent: 
                mkdirs(parentLoc,ignoreExisting);     // ignore=true it doesn't exist
            }    
        }
        
        // check/create full path 
        VDir newDir=null; 
        
        if (fs.newDir(loc).exists())
        {
               if (ignoreExisting==false)
                throw new nl.uva.vlet.exception.ResourceAlreadyExistsException("Directory already exists:"+loc);
            else
                newDir=fs.openDir(loc);
        }
        else
        {
            // parent exist: create new subdirectory: 
            newDir=fs.newDir(loc);
            newDir.create(true); // skip ignore existing: already checked !     
        }
        
        return newDir; 
    } // END: public VDir createDirPath

   
    /** Get local temp directory. On Unix this is "/tmp" */
    public VDir getTempDir() throws VlException 
    {
        return this.getDir(tempDirLocation); 
    }

    /**
     * Create unique temp directory. 
     * This method creates a hash from the current time, which 
     * is in practise unique
     * @see VFSClient#createFile(VRL, boolean)
     */ 
    public VDir createUniqueTempDir() throws VlException 
    {
        return createUniqueTempDir("vfs-",""); 
    }
    
    /**
     * Create unique temporal directory with specified pre- 
     * and post- fixed strings 
     */  
    public VDir createUniqueTempDir(String prefix,String postfix) throws VlException 
    {
        return getTempDir().createUniqueDir(prefix,postfix);
    }
    
    /**
     * Specify location which can be used a temp directory instead
     * of the System's default. 
     */  
    public boolean setTempDir(VRL loc) 
    {
        this.tempDirLocation=loc;  
        return true; 
    }

    /**
     *  Create new file specified by the VRL. 
     *  If ignoreExisting is false, already existing file won't be created. 
     *  In that case an Exception is thrown. 
     */ 
    public VFile createFile(VRL filepath, boolean ignoreExisting) throws VlException
    {
    	VFile file=openFileSystem(filepath).newFile(filepath); 
    	file.create(ignoreExisting);
    	return file; 
    }
    
    /**
     * Create full directory path (parent directory may exist or not) 
     */ 
    public VDir createDir(VRL dirpath, boolean ignoreExisting) throws VlException
    {
    	VDir dir=openFileSystem(dirpath).newDir(dirpath); 
    	dir.create(ignoreExisting); 
    	return dir; 
    }

    /**
     * Return grid enabled home of user, might be on a 
     * different host then the current host !
     * Returns VRSContext.getUserHomeLocation(); 
     * <p>  
     * To set the UserHomeLocation, create a new VRSContext and set
     * the host to be used. Then create a new VFSClient with the 
     * new VRSContext. 
     * <pre>
     * VRSContext context=new VRSContext();
     * vrs.setUserHomeLocation([home_url]); 
     * VFSClient vfs=new VFSClient(vrs); 
     * </pre>
     */
    public VRL getUserHomeLocation()
    {
        return getVRSContext().getUserHomeLocation(); 
    }

    /**
     * Set current Working Directory to which relative VRLs will
     * be resolved.  
     * 
     * @param dir
     */
    public void setWorkingDir(VDir dir)
    {
        this.setWorkingDir(dir.getVRL()); 
    }

     /** 
      * Returns current working directory for relative URLs
      * as specified in the use VRSContext. 
      */
     public VRL getWorkingDir()
     {
         return getVRSContext().getWorkingDir(); 
     }
          
     /**
      * Specify current working directory to which relative paths 
      * can be resolved.
      * The specified location can be any VRL.  
      */
     public void setWorkingDir(VRL vrl)
     {
         getVRSContext().setWorkingDir(vrl);     
     }
     
     public VFSNode[] list(VRL path) throws VlException
     {
        return this.getDir(path).list();     
     }
     
     public VFSNode[] list(VRL path,String regexpFilter) throws VlException
     {
         return this.getDir(path).list(regexpFilter,true); 
     }
     
     public VFSNode[] list(VRL path,Pattern regexpPattern) throws VlException
     {
         return this.getDir(path).list(regexpPattern); 
     }
     
     /**
      * Create new VFile object. 
      * The location doesn't have to exist, only the actual VFile object is created.  
      */
     public VFile newFile(VRL location) throws VlException
     {
        VFileSystem fs=openFileSystem(location);
        VFile file=fs.newFile(location);
        
        if (file==null)
            throw new nl.uva.vlet.exception.VlInternalError("FileSystem method newFile() returned NULL for:"+location);
        
        return file; 
     }
     
     public VFile newFile(String path) throws VRLSyntaxException, VlException
     {
         return newFile(resolve(path)); 
     }

     /**
      * Create new VDir object. 
      * The location doesn't have to exist, only the actual VDir object is created.  
      */
     public VDir newDir(VRL location) throws VlException
     {
         VFileSystem fs=openFileSystem(location);
         VDir dir=fs.newDir(location);
         
         if (dir==null)
             throw new nl.uva.vlet.exception.VlInternalError("FileSystem method newDir() returned NULL for:"+location); 
         
         return dir; 
     }

     public VDir newDir(String path) throws VRLSyntaxException, VlException
     {
         return newDir(resolve(path)); 
     }
     
     /** Returns new FileSystem or throws Exception */ 
    public VFileSystem openFileSystem(VRL location) throws VlException
    {
        return this.getVRSContext().openFileSystem(location);  
    }

    /**
     * Returns user home location. When running in service or applet mode, this might not always
     * be a user writeable location. 
     */
    public VDir getUserHome() throws VlException
    {
        return getDir(getUserHomeLocation()); 
    }


    
}
