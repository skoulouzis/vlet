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
 * $Id: VFSNode.java,v 1.12 2011-06-07 14:30:45 ptdeboer Exp $  
 * $Date: 2011-06-07 14:30:45 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_EXISTS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISDIR;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISFILE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISHIDDEN;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISREADABLE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISSYMBOLICLINK;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISWRITABLE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NRCHILDS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PARENT_DIRNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SYMBOLICLINKTARGET;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_USERNAME;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VDeletable;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRenamable;
import nl.uva.vlet.vrs.io.VRandomAccessable;

/**
 * Super class of VDir and VFile. 
 * Represents shared methods for (Virtual) Directories and Files.
 * 
 * @see VDir
 * @see VFile
 * 
 * @author P.T. de Boer
 */

public abstract class VFSNode extends VNode implements VRenamable, VEditable, VDeletable,VACL
{
    // ========================================================================
    // Class Fields: list of default VFS attributes.
    // ========================================================================

    /** Default attributes names for all VFSNodes */

    public static final String[] attributeNames =
    { 
    	ATTR_TYPE,
    	ATTR_NAME,
    	ATTR_SCHEME,
    	ATTR_HOSTNAME,
    	ATTR_PORT,
        ATTR_MIMETYPE, 
        ATTR_ISREADABLE, 
        ATTR_ISWRITABLE, 
        ATTR_ISHIDDEN,
        ATTR_ISFILE, 
        ATTR_ISDIR, 
        ATTR_NRCHILDS, 
        ATTR_LENGTH,
        // minimal time wich must be supported
        ATTR_MODIFICATION_TIME,
        // stringifying is now done in GUI !  
        //ATTR_MODIFICATION_TIME_STRING, 
        ATTR_PARENT_DIRNAME,
        // not all implementation support the creation time attribute
        // ATTR_CREATION_TIME_STRING,
        // not all implementation support the OWNER attribute
        //ATTR_OWNER, 
        ATTR_ISSYMBOLICLINK,
        ATTR_PERMISSIONS_STRING // implementation specific permissions string
    };

    public static final  String linkAttributeNames[]=
    {
            ATTR_SYMBOLICLINKTARGET
    };

    //private VFSTransfer transferInfo=null;



    /** Default buffer for streamCopy to use */ 
    public static int defaultStreamBufferSize = 2 * 1024 * 1024;

    // ========================================================================
    // Static helper methods 
    // ========================================================================

    public static VFSNode[] returnAsArray(Vector<VFSNode> nodes)
    {
        if (nodes==null)
            return null; 

        VFSNode array[]=new VFSNode[nodes.size()];
        array=nodes.toArray(array); 


        return array; 
    }

  

    // ========================================================================
    // Constructor
    // ========================================================================
    
    VFileSystem vfsSystem=null;
    
//    /** @deprecated Will switch to VFSNode(VFileSystem,...) ! */
//    public VFSNode(VRSContext context, VRL vrl)
//    {
//        super(context, vrl);
//    }
//    
    public VFSNode(VFileSystem vfs, VRL vrl)
    {
        super(vfs.getVRSContext(), vrl);
        vfsSystem=vfs;
    }
    
    /** Returns File System this VFSNode belongs to */ 
    public VFileSystem getFileSystem()
    {
        return vfsSystem;
    }
    // ========================================================================
    // VFSNode interface 
    // ========================================================================

    /**
     * Return basename with or without extension. 
     * call getVRL().getBasename(withExtension)  
     */ 
    public String getBasename(boolean withExtension)
    {
        return getVRL().getBasename(withExtension); 
    }
    
    public VFSNode getNode(String path) throws VlException
    {
        return getPath(path); 
    }
    
    /**
     * Create this file or directory. 
     * @see #create(boolean) 
     *
     * @return true if resource was created or already existed   
     */
    public boolean create() throws VlException
    {
        return create(true); 
    }
    

    /** Returns root directory of this directory/file system */ 
    public VDir getRoot() throws VlException
    {
        VNode node=this.getPath("/"); 
        
        if (node instanceof VDir)
            return (VDir)node;
            
        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Root path is not a directory:"+node); 
    }

    /** 
     * Fetch any VFSNode (VFile or VDir) with the specified absolute
     * or relative path
     */ 
    public VFSNode getPath(String path) throws VlException
    {
        // resolve absolute or relative path: 
        VRL loc=this.getLocation().resolvePath(path); 
        VNode node=this.vrsContext.openLocation(loc);
        
        if (node instanceof VFSNode)
            return (VFSNode)node;
            
        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Path is not a File path:"+loc); 
    }


    /**
     * Optional method for filesystems who support hidden files. 
     * Note that the implementation of hidden files on filesystems
     * might differ!
     * Default implemententation is to return true for 'dot' files. 
     */

    public boolean isHidden()
    {
        if (getBasename().startsWith("."))
            return true;

        return false; 
    }

    /**
     * Optional method for filesystems who support symbolic links
     * or File Aliases (LFC). 
     * Default this method return false. 
     * Note that implementations of links on filesystems might differ!
     * @throws VlException 
     */
    public boolean isSymbolicLink() throws VlException
    {
        return false;
    };

    /**
     * Optional method to resolve  links if this VFS Implementation
     * supports it. Use isSymbolicLink() first to check whether this file is a
     * (soft) link or a windows shortcut. 
     * Filesystem implementations might differ how they handle symbolic links.  
     * @throws VlException 
     */
    public String getSymbolicLinkTarget() throws VlException
    {
        return null;
    };

    /**
     * Returns Symbolic LinkTarget as VRL 
     * (if this resource is an symbolic link) NULL otherwise.  
     *  
     * Filesystem implementations might differ how they handle symbolic links.
     *   
     * @throws VlException 
     */
    public VRL getSymbolicLinkTargetVRL() throws VlException
    {
        String targetPath=this.getSymbolicLinkTarget();
        
        if (targetPath==null)
            return null; 
    
        return this.getLocation().copyWithNewPath(targetPath); 
    };

    /**
     * Whether the location points to a local available path ! <br>
     * To get the actual local path, do a getPath().<br>
     */
    public boolean isLocal()
    {
        return false;
    }
    
    public String[] getResourceAttributeNames()
    {   
        return null; //getVFSAttributeNames()
    }
    
    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        StringList list=new StringList(super.getAttributeNames()); 

        boolean isSoftlink=false;

        try
        {
            isSoftlink=isSymbolicLink();
        }
        catch (VlException e1)
        {
            Global.debugPrintf(this,"***Error: isSymbolicLink() Exception:%s\n",e1); 
        } 


        list.merge(attributeNames);
        if (isSoftlink)
            list.merge(linkAttributeNames); 
        
        if (this instanceof VChecksum)
        {
            list.add(VAttributeConstants.ATTR_CHECKSUM);
            list.add(VAttributeConstants.ATTR_CHECKSUM_TYPE);
            list.add(VAttributeConstants.ATTR_CHECKSUM_TYPES);
        }
        return list.toArray(); 
    }
    
    /**
     * Returns single File Resource Attribute. 
     * For optimized fetching of Attributes use getAttributes(String names[]) 
     */ 
    public VAttribute getAttribute(String name) throws VlException
    {
        if (name==null) 
            return null; 

        // Check if super class has this attribute
        VAttribute supervalue = super.getAttribute(name);

        // Super class has this attribute, and since I do not overide
        // any attribute, return this one:
        if (supervalue != null)
            return supervalue;

        if (name.compareTo(ATTR_EXISTS) == 0)
            return new VAttribute(name, exists());
        else if (name.compareTo(ATTR_PARENT_DIRNAME) == 0)
            return new VAttribute(name, getLocation().getDirname());
        else if (name.compareTo(ATTR_PATH) == 0)
            return new VAttribute(name, getLocation().getPath());
        else if (name.compareTo(ATTR_ISDIR) == 0)
            return new VAttribute(name, isDir());
        else if (name.compareTo(ATTR_ISFILE) == 0)
            return new VAttribute(name, isFile());
        else if (name.compareTo(ATTR_LENGTH) == 0)
        {
            if (this instanceof VFile)
                return new VAttribute(name, ((VFile) this).getLength());
            // getLength for VDir not supported
            return new VAttribute(name, 0);
        }
        else if (name.compareTo(ATTR_GID) == 0)
        {
            if (this instanceof VUnixGroupMode)
                return new VAttribute(name, ((VUnixGroupMode) this).getGid());
            // getLength for VDir not supported
            return new VAttribute(name, "");
        }
        else if (name.compareTo(ATTR_UID) == 0)
        {
            if (this instanceof VUnixUserMode)
                return new VAttribute(name, ((VUnixUserMode) this).getUid());
            // getLength for VDir not supported
            return new VAttribute(name, "");
        }
        else if (name.compareTo(ATTR_ISREADABLE) == 0)
            return new VAttribute(name, isReadable());
        /* Poor man's Permissions: */
        else if (name.compareTo(ATTR_ISWRITABLE) == 0)
            return new VAttribute(name, isWritable());
        else if (name.compareTo(ATTR_ISHIDDEN) == 0)
            return new VAttribute(name, isHidden());

        // VComposite attributes
        else if (name.compareTo(ATTR_NRCHILDS) == 0)
        {
            if (this instanceof VDir)
            {
                VDir vdir = (VDir) this;
                return new VAttribute(name, vdir.getNrOfNodes());
            }
            return new VAttribute(name, 0);
        }
        else if (name.compareTo(ATTR_MODIFICATION_TIME) == 0)
        {
            // New TIME Type ! 
            return VAttribute.createDateSinceEpoch(name,getModificationTime());
        }
        /*else if (name.compareTo(ATTR_MODIFICATION_TIME_STRING) == 0)
        {
            return new VAttribute(name,millisToDateTimeString(getModificationTime())); 
        }*/
        else if (name.compareTo(ATTR_PERMISSIONS_STRING) == 0)
        {
            return new VAttribute(name,getPermissionsString()); 
        }
        else if (name.compareTo(ATTR_ISSYMBOLICLINK) == 0)
            return new VAttribute(name, this.isSymbolicLink()); 
        else if (name.compareTo(ATTR_SYMBOLICLINKTARGET) == 0)
            return new VAttribute(name, getSymbolicLinkTarget());
     
        else if ( (name.compareTo(ATTR_CHECKSUM) == 0) &&   (this instanceof VChecksum) )
        {
        	String types[]=((VChecksum)this).getChecksumTypes();
        	// return first checksum type; 
        	if ((types==null) || (types.length<=0))
        		return null; 
        	
    		return new VAttribute(name, ((VChecksum)this).getChecksum(types[0]));
        }
        else if ( (name.compareTo(ATTR_CHECKSUM_TYPE) == 0) &&   (this instanceof VChecksum) )
        {
            String types[]=((VChecksum)this).getChecksumTypes();
            if ((types==null) || (types.length<=0))
                return null; 
            
            return new VAttribute(name,types [0]);  
        }
        else if ( (name.compareTo(ATTR_CHECKSUM_TYPES) == 0) &&   (this instanceof VChecksum) )
        {
            String types[]=((VChecksum)this).getChecksumTypes();
            return new VAttribute(name,new StringList(types).toString(","));  
        }
        /*
         * java support for local filesystem attributes is rather limited. Maybe
         * a JNI implemention in C++ is required.
         */
        // return null;
        return null; // 
    }


    /**
     * Returns Permissions in Unix like String.  
     * For example "-rwxr-xr-x" for a linux file.
     * This method checks whether this resource implements VUnixFileMode 
     * and used the Unix File Mode to generate the permissions string. 
     * @see nl.uva.vlet.vfs.VUnixFileMode
     */ 
    public String getPermissionsString() throws VlException
    {
        if (this instanceof VUnixFileMode)
        {
            int mode=((VUnixFileMode)this).getMode();
            return VFS.modeToString(mode, isDir()); 
        }
        
        String str = (this.isDir() ? "d" : "-")
        + (this.isReadable() ? "r" : "-")
        + (this.isWritable() ? "w" : "-")
        // append extra Non-Unix attributes/permissions
        + " [" 
                + (this.isHidden() ? "H" : "") 
                + (this.isSymbolicLink() ? "L" : "")
          + "]";// + "?";

        return str;
    }

    /**
     * VFSnode implements getParent by calling VFSNode.getParentDir
     * 
     * @throws VlException
     */

    public VDir getParent() throws VlException
    {
        VNode vnode=this.vrsContext.openLocation(this.getParentLocation()); 
        
        if (vnode instanceof VDir) 
            return (VDir)vnode;
            
        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Parent of VFSNode is not of VDir type:"+this);
    }


    /**
     * Returns array whith one parent. 
     * 
     * @throws VlException
     */

    public VNode[] getParents() throws VlException
    {
        VNode parents[] = new VNode[1];
        parents[0] = getParent();
        return parents;
    }

    /**
     * RandomAccessable methods:
     */
    public boolean isRandomAccessable()
    {
        return (this instanceof VRandomAccessable);
    }

    //=========================================================================
    // VEditable interface
    //=========================================================================

    /**
     * Default File implementions has editable attributes.
     * Default return value is true; 
     * This differs from isWritable as file persmissions are 'editable' 
     * even if the file is not writable. 
     */
    public boolean isEditable() throws VlException
    {
        return true;
    }

    public boolean isDeletable() throws VlException
    {
        return true; 
    }

    public boolean setAttributes(VAttribute[] attrs) throws VlException
    {
        boolean result = true;

        for (VAttribute attr:attrs) 
        {
            Boolean res2=true;

            if (attr==null) 
                continue;  // filter out null attributes 

            // filter out non-editable attributes ! 
            if (attr.isEditable()==true)
            {
                res2 = setAttribute(attr);
            }
            else
            {
                Global.warnPrintf(this,"*** Warning: VFSNode.setAttributes(): Received non-editable attribute:%s\n",attr);
            }

            result = result && res2;
        }

        return result;
    }

    /**
     * Set atribute. Not much attributes can be set currently by the VFSNode
     * super class. To add extra attributes in a subclass do a
     * super.setAttributes(attr) first check the return value and if it is false
     * add your own. For example:
     * 
     * <pre>
     *  SubClass.setAttribute(VAtribute attr) 
     *  {
     *     if (super.setAttribute(attr)==true) 
     *         return true;
     *     
     *     if (isMyAttribute(attr)) 
     *        return setMyAttribute(attr); 
     *     else   
     *        return false; 
     *  } 
     *  </pre>
     * 
     */
    public boolean setAttribute(VAttribute attr) throws VlException
    {
        String name = attr.getName();

        if (name.compareTo(ATTR_NAME) == 0)
            return renameTo(attr.getStringValue(), false); // default Name
        // attribute = Basename
        // !
        else if (name.compareTo(ATTR_PATH) == 0)
            return renameTo(attr.getStringValue(), true); // default Name attribute =
        // Basename !
        else
            return false;
    }

    //=========================================================================
    // VRenamable interface
    //=========================================================================

    public boolean isRenamable() throws VlException
    {
        return isWritable(); 
    }

    

    // ========================
    // ACL interface : Under Construction  
    // =========================

    public void setACL(VAttribute[][] acl) throws VlException
    {
        if (this instanceof VUnixFileMode)
        {
             this.setUXACL(acl); 
        }
        else
        {
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource doensn't support ACL:"+this); 
        }
    }
    
    /**
     * Universal Access Control List to support multiple permission schemes.
     * The returned matrix ACL[N][M] is a list of N entities specifying M permissions
     * for Entity N. 
     * For more details {@linkplain VACL}.  
     * @see VACL 
     */ 
    public VAttribute[][] getACL() throws VlException
    {
        if (this instanceof VUnixFileMode)
        {
            return getUXACL(); 
        }
        else
        {
            // default user readable writable list:
            VAttribute attrs[][]=new VAttribute[1][]; 
            attrs[0]=new VAttribute[3]; 

            attrs[0][0]=new VAttribute(ATTR_USERNAME,"current");
            attrs[0][0].setEditable(false);
            attrs[0][1]=getAttribute(ATTR_ISREADABLE); 
            attrs[0][1].setEditable(false); 
            attrs[0][2]=getAttribute(ATTR_ISWRITABLE);
            attrs[0][2].setEditable(false);

            return attrs;
        }
    }

    public VAttribute[][] getUXACL() throws VlException
    {
        if ((this instanceof VUnixFileMode)==false)
        {
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource doensn't support unix style file permissions:"+this); 
        }
        else
        {
            int mode = ((VUnixFileMode)this).getMode(); 
            return VFS.convertFileMode2ACL(mode, this.isDir());
        }
    }
    
    /**
     * Converts the "user,group,other" permissions attribute list 
     * to a Unix file mode and changes this if this file supported
     * Unix style permission rights.
     *   
     * @see VFS.convertACL2FileMode
     * 
     * @throws VlException
     */
    public void setUXACL(VAttribute[][] acl)
                throws VlException
    {
        if ((this instanceof VUnixFileMode)==false)
        {
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource doensn't support unix style file permissions:"+this); 
        }
        else
        {
            int mode = VFS.convertACL2FileMode(acl, this.isDir());

            if (mode < 0)
                throw new VlException("Error converting ACL list");
            
            ((VUnixFileMode)this).setMode(mode); 
        }
    }

    /** 
     * Returns all possible ACL entities (users,groups, etc); 
     * @throws VlIOException 
     */ 
    public VAttribute[] getACLEntities() throws VlIOException
    {
        return null; 
    }

    /**
     *  Create a new ACL Record for the given ACL Entry, that is, a new row
     *  in the ACL[][] matrix returned in getACL(). 
     *  The nr of- and types in this row must match. 
     * @param writeThrough 
     * 
     * @return
     * @throws NotImplementedException 
     */
    public VAttribute[] createACLRecord(VAttribute entity, boolean writeThrough) throws VlException
    {
        throw new NotImplementedException("Create new ACL Record not supported");
    }

    /** Delete entry in the ACL list or set permissions to none */ 
    public boolean deleteACLEntity(VAttribute entity) throws VlException
    {
        throw new NotImplementedException("Entities can't be deleted");
    }

    // ========================================================================
    // VFS Transfer Interface
    // ========================================================================
    final protected VRSTransferManager getTransferManager()
    {
        return vrsContext.getTransferManager(); 
    }
    /** 
     * Asynchronous transfer method to iniate a copy or a move. 
     * Returns VFSTransfer info object so the transfer can be monitored. 
     * All background copy or move methods are started by this method. 
     */
    final public VFSTransfer asyncCopyMoveTo(final VFileSystem targetFS,final VRL targetVRL, final boolean isMove) throws VlException
    {
        return this.getTransferManager().asyncCopyMoveTo(null, this,targetFS,targetVRL,isMove,null); 
    }
    
    /**
     * Generic method to either perform a copy or a move. 
     * All other copyTo and moveTo methods call this method. 
     *
     * @param parenDir target parent directory. New file or directory will be create in this directory
     * @param optNewName optional new name. If null this.getBasename() will be used  
     */
    final public VFSNode doCopyMoveTo(VDir parentDir, String optNewName, boolean isMove) throws VlException
    {
       return this.getTransferManager().doCopyMove(this,parentDir,optNewName,isMove); 
    }
    
    public boolean renameTo(String newNameOrPath) throws VlException
    {
        boolean fullpath=false;
        
        if (newNameOrPath.startsWith(VRL.SEP_CHAR_STR)==true); 
            fullpath=true; 
        
       return (this.rename(newNameOrPath,fullpath)!=null);
    }
    
    public boolean renameTo(String newNameOrPath,boolean nameIsPath) throws VlException
    {
        boolean fullpath=false;
        
        if (newNameOrPath.startsWith(VRL.SEP_CHAR_STR)==true)
            fullpath=true; 
        
       return (this.rename(newNameOrPath,fullpath)!=null); 
    }

    /** If this VFSNode is a VDir, return as VDir, else return null */ 
    public VDir toDir()
    {
        if (isDir())
            return (VDir)this;
        return null; 
    }
    
    /** If this VFSNode is a VFile, return as VFile, else return null */ 
    public VFile toFile()
    {
        if (isFile())
            return (VFile)this;
        return null; 
    }

    
    // ========================================================================
    // Abstract Interface Methods 
    // ========================================================================

    abstract public VRL rename(String newNameOrPath,boolean nameIsPath) throws VlException; 

    public abstract VFSNode copyTo(VDir dest) throws VlException;

    public abstract VFSNode copyTo(VDir dest, String newName) throws VlException;

    public abstract VFSNode moveTo(VDir dest) throws VlException;

    public abstract VFSNode moveTo(VDir dest, String newName) throws VlException; 

    /**
     * Create this Resource. 
     * <p>
     * If ignoreExisting==true, ignore if this path already exists.  
     * <br>
     * If ignoreExisting==false, do not ignore if this resource already exists 
     *    and throw a ResourceAlreadyExistsException to indicate to
     *    that the resource was already created. 
     *    
     * @param ignoreExisting 
     *         Safeguard whether to check if this resource was already created. 
     * @throws VlException if resource couldn't be created or when ignoreExisting==false and resource already exist.
     * 
     */
    abstract public boolean create(boolean ignoreExisting) throws VlException ; 
    
    /**
     * Returns true if the node is a file.
     * @see VFile 
     */
    public abstract boolean isFile();

    /**
     * Returns true if the node is a Directory 
     * @see VDir
     */
    public abstract boolean isDir();

    /**
     * Returns true if the this object represents an existing file
     * for VFile object or an existing directory for VDir objects. 
     * @throws VlException 
     */
    public abstract boolean exists() throws VlException;


    /**
     * Return time of last modification in milli seconds after 'epoch'
     * epoch = (1-jan-1970 GMT). 
     * @throws VlException
     */
    public abstract long getModificationTime() throws VlException;

    /**
     * Returns whether the object is readable using current user credentials.
     * <br>
     * For a Directory isReadable means it must have r-x permissions !
     * 
     * @throws VlException
     * @see exists
     * @see isWritable
     */
    public abstract boolean isReadable() throws VlException;

    /**
     * Returns whether the object is writable using current user credentials.
     * Note that some implementations make a difference between 
     * 'deletable' 'appendable' and 'can create directories' (GridFTP). 
     *
     * @see exists
     * @see isReadable
     */
    public abstract boolean isWritable() throws VlException;



}
