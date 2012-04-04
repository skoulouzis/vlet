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
 * $Id: VDir.java,v 1.10 2011-09-26 14:33:46 ptdeboer Exp $  
 * $Date: 2011-09-26 14:33:46 $
 */ 
// source: 

package nl.uva.vlet.vfs;


import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.IntegerHolder;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.QSort;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.NodeFilter;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VCompositeDeletable;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRenamable;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;


/**
 * Super class of the VFS Directory implementation. 
 * Represents an abstract interface to a Directory implementation. 
 *  
 * @see VFile 
 * @see VFSNode
 * @author P.T. de Boer
 */
public abstract class VDir extends VFSNode implements VComposite,VRenamable,
      VCompositeDeletable
{
    private static Random dirRandomizer=new Random();
    
    static
    {
        dirRandomizer.setSeed(System.currentTimeMillis());
    }
        
    private static String[] childTypes={VFS.FILE_TYPE,VFS.DIR_TYPE};
    
   
    /** 
     * Default Recursive delete: lists children 
     * and perform delete() on child list. 
     * This method does NOT delete the parent node itself !  
     * @throws VlException 
     */
    protected static boolean defaultRecursiveDeleteChildren(ITaskMonitor  monitor,VDir dir) throws VlException
    {
    	int len=0; 
        VFSNode nodes[]=dir.list(); 
        if (nodes!=null)
        	len=nodes.length; 

    	if (monitor==null)
    		monitor = ActionTask.getCurrentThreadTaskMonitor("Deleting contents of:"+dir,len);

    	monitor.logPrintf("Deleting contents of:"+dir.getPath()+"\n"); 
        for (int i=0;(nodes!=null) && (i<nodes.length);i++)
        {
            if (nodes[i] instanceof VCompositeDeletable)
            {
                ((VCompositeDeletable)nodes[i]).delete(true);
            }
            else
            {
            	monitor.startSubTask("deleting:"+nodes[i].getBasename(),1); 
            	monitor.logPrintf(" - deleting:"+nodes[i]+"\n"); 
                nodes[i].delete();
            	monitor.endSubTask("deleting:"+nodes[i].getBasename()); 
            }
            
            // new asynchronous update to the VBrowser: 
            dir.getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(nodes[i].getVRL()));  
                    
        }
        
        return true; // if no exception occured, the result=true
    }
    
    public static VFSNode[] sortVNodes(VFSNode[] nodes,boolean typeFirst,boolean ignoreCase)
    {
        QSort.sortVNodesByTypeName(nodes,typeFirst,ignoreCase);
        return nodes; 
    }
    
    //  ==========================================================================
    //  VNode interface 
    //  ==========================================================================
     
    public VDir(VFileSystem vfsSystem, VRL vrl)
    {
        super(vfsSystem, vrl);
    }
    
//    /** @deprecated Will switch to VFSNode(VFileSystem,...) ! */
//    public VDir(VRSContext context, VRL vrl) 
//    {
//        super(context,vrl); 
//    }

    @Override
    public VRL getHelp()
    {
        return Global.getHelpUrl("VDir"); 
    }
    
    @Override
    public String getType()
    {
        return VFS.DIR_TYPE;
    }
    
    @Override
    public String getMimeType()
    {
        return null; 
    }
    
    /**
     * Returns allowed child types for VDir.<br>
     * <br>
     * The default types for VDir are 'File' and 'Dir' type<br>
     */
    public String[] getResourceTypes()
    {
        return childTypes; 
    }
    
//  ==========================================================================
//  VComposite Interface 
//  ==========================================================================
    
    /** 
     * Add (VFS)Node to this directory location. 
     * Node must be of type VFSNode since this method calls addNode(VFSNode...) 
     */  
    public VFSNode addNode(VNode node,boolean isMove) throws VlException
    {
        return addNode(node,(String)null,isMove); 
    }
    
    public VFSNode addNode(VNode node,String optNewName,boolean isMove) throws VlException
    {
        if (node instanceof VFSNode) 
        {
            return addNode((VFSNode)node,optNewName,isMove);
        }
        else if (node instanceof VStreamReadable)
        {
            return putAnyNode(node,optNewName,isMove); 
        }
        else
        {
            throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+node);
        }
    }
    
    /** 
     * Create new VFile and copy contents from (VStreamReadable) vnode.
     * Tries to smartly create a new file based on the source Node. 
     * <br>
     * Custom method currently used by the VBrowser to 'drop' just any kind of node
     * into a Directory. 
     *  
     */ 
    public VFile putAnyNode(VNode sourceNode, String optNewName, boolean isMove) throws VlException
    {
        return vrsContext.getTransferManager().putAnyNode(this,sourceNode,optNewName,isMove); 
    }

    public VFSNode addNode(VFSNode node,String optNewName,boolean isMove) throws VlException
    {
        if (node instanceof VFile) 
            return ((VFile)node).doCopyMoveTo(this,optNewName,isMove);
        else if (node instanceof VDir)
            return ((VDir)node).doCopyMoveTo(this,optNewName,isMove);
        else
        {
            // should be eithger VDir or VFile
            throw new ResourceTypeNotSupportedException("Type of VFS Resource not supported:"+node);
        }
    }
    
    /** 
     * Add multiple (VFS)Nodes to this directory location. 
     * Nodes must be of type VFSNode since this method calls addNodes(VFSNode[]...) 
     */  
    public VNode[] addNodes(VNode[] nodes,boolean isMove) throws VlException
    {
        if (nodes==null)
            return null;
        
        // downcast to VFSNodes. Currenlty Only VFSNodes are supported.
        
        if (nodes instanceof VFSNode[])
        {
            return addNodes((VFSNode[])nodes,isMove);
        }
        else
        {
            // just try to add them all 
            VNode[] results=new VNode[nodes.length];
              
            for (int i=0; i<nodes.length; i++)
            {
                 results[i]=addNode(((VFSNode)nodes[i]),null,isMove); 
            }
              
            return results; 
        }
    }
    
    public VFSNode[] addNodes(VFSNode[] nodes,boolean isMove) throws VlException
    {
        VFSNode[] vfsNodes=new VFSNode[nodes.length];
        
        for (int i=0; i<nodes.length; i++)
        {
            if (nodes[i] instanceof VFSNode)
            {
                vfsNodes[i]=addNode(((VFSNode)nodes[i]),null,isMove); 
            }
        }
        
        return vfsNodes;
    }

    /** Delete node. Node must be of type VFSNode */ 
    public boolean delNode(VNode childNode) throws VlException
    {
        if (childNode instanceof VFSNode)
            return ((VFSNode)childNode).delete();
        else
        {
            throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+childNode);
        }
    }

    /** Delete nodes. Nodes must be of type VFSNode */ 
    public boolean delNodes(VNode[] childNodes) throws VlException
    {
        boolean status=true; 
        
        for (int i=0;i<childNodes.length;i++)
        {
            if (childNodes[i] instanceof VFSNode)
                status&= ((VFSNode)childNodes[i]).delete();
            else
            {
                throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+childNodes[i]);
            }
        }
        
        return status; 
    }
 
//  ==========================================================================
//  VFSNode interface 
//  ==========================================================================
    
    
    /** return true if the VFSNode is a (V)File */
    public boolean isFile()
    {
        return false;
    };
    
    /** return true if the VFSNode is a (V)Directory */ 
    public boolean isDir()
    {
        return true;
    };
    
    /** 
     * Default implementation calls the VDir method list() 
     */ 
    public VFSNode[] getNodes() throws VlException
    {
        return list();
    }
    
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException /// Tree,Graph, Composite etc.
    {
        return list(offset,maxNodes,totalNumNodes); 
    }
    
    /** 
     * VDir implements createChild by calling createFile or createDir,
     * depending on the type.  
     */ 
    public VFSNode createNode(String type,String name, boolean ignoreExisting) throws VlException
    {
        if (type==null)
        {
            throw new ResourceTypeNotSupportedException("Cannot create child type: NULL");
        }
        
        if (type.equalsIgnoreCase(VFS.FILE_TYPE))
        {
            return createFile(name,ignoreExisting);
        }
        else if (type.equalsIgnoreCase(VFS.DIR_TYPE))
        {
            return createDir(name,ignoreExisting);
        }
        else
        {
            throw new ResourceTypeNotSupportedException("Cannot create child type:"+type);
        }
    }
    
    /**
     * Returns new VFile object. Path may or may not exist. 
     * This is not checked. 
     * Call VFileSystem.newFile(); 
     * @param path absolute or relative path for new VFile object 
     * @return new VFile object.
     * @throws VlException 
     * @see VFileSystem#newFile(VRL); 
     */
    public VFile newFile(String path) throws VlException
    {
        return this.getFileSystem().newFile(resolvePathVRL(path)); 
    }
    
    /**
     * Returns new VDir object. Path may or may not exist. 
     * This is not checked. 
     * Call VFileSystem.newDir(); 
     * @param path absolute or relative path for new VDir object 
     * @return new VDir object.
     * @throws VlException 
     * @see VFileSystem#newFile(VRL); 
     */
    public VDir newDir(String path) throws VlException
    {
        return this.getFileSystem().newDir(resolvePathVRL(path)); 
    }
    
    /**
     * Create new file in this directory. After creation the file 
     * will exist on this filesystem.
     * To get a new VFile object: use newFile()
     */  
    public VFile createFile(String name) throws VlException
    {
        return createFile(resolvePath(name),true);   
    }
    
    /**
     * Create sub directory in this directory or use full path 
     * to create a new directory.  
     * @throws VlException
     */
    public VDir createDir(String dirName) throws VlException
    {
    	VDir dir=getFileSystem().newDir(resolvePathVRL(dirName)); 
    	dir.create(true);
    	return dir; 
    }
     
    /**
     * Put file (copy) to this directory. 
     * Same as sourceFile.copyTo(this,optNewName);  
     * @throws VlException 
     */
    public final VFile putFile(VFile sourceFile) throws VlException
    {
        return (VFile)sourceFile.doCopyMoveTo(this,null ,false); 
    }
    
    /**
     * Put file (copy) to this directory. 
     * Same as sourceFile.copyTo(this,optNewName);  
     * @throws VlException 
     */
    public final VFile putFile(VFile sourceFile,String optNewName) throws VlException
    {
        return (VFile)sourceFile.doCopyMoveTo(this,optNewName,false); 
    }
    
    /**
     * Put directory (copy) to this directory. 
     * Same as sourceDir.copyTo(this,optNewName);  
     * @throws VlException 
     */
    public final VDir putDir(VDir sourceDir) throws VlException
    {
        return (VDir)sourceDir.doCopyMoveTo(this,null ,false); 
    }
    /**
     * Put directory (copy) to this directory. 
     * Same as sourceDir.copyTo(this,optNewName);  
     * @throws VlException 
     */
    public final VDir putDir(VDir sourceDir,String optNewName) throws VlException
    {
        return (VDir)sourceDir.doCopyMoveTo(this,optNewName ,false); 
    }
    
    /**
     * Copy to specified parent directory location.  
     * @throws VlException 
     */
    public final VDir copyTo(VDir parentDir) throws VlException
    {
        return (VDir)doCopyMoveTo(parentDir,null ,false); 
    }
    
    /**
     * Copy to specified parent directory. 
     * @param destinationDir new parent directory. New Directory 
     *        will be created as subdirectory of this parent. 
     * @param optNewName optional newname. If null basenames 
     *        of source directory will be used.   
     * @throws VlException 
     */
    public final VDir copyTo(VDir dest,String newName) throws VlException
    {
        return (VDir)doCopyMoveTo(dest, newName,false); 
    }
    
    /**
     * Move to specified parent directory.
     * @see #moveTo(VDir, String); 
     */
    public final VDir moveTo(VDir destinationDir) throws VlException
    {
        return (VDir)doCopyMoveTo(destinationDir,null,true);
    }
    
    /**
     * Move to specified VDir location.
     * @param destinationDir new parent directory. New Directory 
     *        will be creaates as subdirectory of this parent. 
     * @param optNewName optional newname. If null basename 
     *        of source directory will be used.   
     * @throws VlException 
     */
    public final VDir moveTo(VDir destinationDir,String optNewName) throws VlException
    {
        return (VDir)doCopyMoveTo(destinationDir,optNewName,true);
    }
    
    /**
     * Non-recursive Delete.<br>
     * Calles recursive delete from VComposite with resurse=false.  
     */
    public boolean delete() throws VlException
    {
        return delete(false);
    }
    
    /** Deletes file */ 
    public boolean deleteFile(String name) throws VlException
    {
        return this.getFile(name).delete(); 
    }
    
    /** Deleted (sub)directory */ 
    public boolean deleteDir(String name,boolean recursive) throws VlException
    {
        return this.getDir(name).delete(recursive); 
    }
    
    /** Get subdirectory or if dirname is absolute get the directory using the absolute path */  
    public VDir getDir(String dirname) throws VlException
    {
        return this.vfsSystem.openDir(resolvePathVRL(dirname)); 
    }

    /** Get file in this directory using the relative or absolute path */   
    public VFile getFile(String filename) throws VlException
    {
        return this.vfsSystem.openFile(resolvePathVRL(filename)); 
    }
    
    
    public boolean hasNode(String name) throws VlException
    {
        // todo: more efficient method 
        if (this.existsFile(resolvePath(name))==true)
            return true; 
        if (this.existsDir(resolvePath(name))==true)
            return true; 

        return false; 
    }
    
    public VFSNode getNode(String path) throws VlException
    {
        return this.vfsSystem.openLocation(resolvePathVRL(path)); 
    }
    
    
    /**
     * Return attribute matrix for given nodes. 
     * The matrix should be in the form: VAttribute[node][attrname].<br>
     * <b>Developers note:</b><br>
     * Override this method for a faster getall attributes. 
     * Also allow for entries in the name and node list to be null !
     * This is for attribute list merging (Ritsen)! 
     * 
     * @param childNames array of VNodes names
     * @param names VAttribute names to fetch. 
     * @return VAttibute matrix [rows][columns]. Rows match nodes, columns match attributes. 
     * @throws VlException
     */
    
    //@Override
    public VAttribute[][] getNodeAttributes(String childNames[], String names[]) throws VlException 
    {
        VNode nodes[]=new VNode[childNames.length]; 
        
        VAttribute attrs[][] = new VAttribute[nodes.length][];
        
        for (int i = 0; i < childNames.length; i++)
        {
            nodes[i]=getNode(childNames[i]); 
            
            if (nodes[i]!=null)
                attrs[i]=nodes[i].getAttributes(names);
            else
                attrs[i]=null;
        }
        
        return attrs;
    }
    
    public VAttribute[][] getNodeAttributes(String names[]) throws VlException 
    {
        VNode nodes[]=getNodes(); 
        
        VAttribute attrs[][] = new VAttribute[nodes.length][];
        
        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i]!=null)
                attrs[i]=nodes[i].getAttributes(names);
            else
                attrs[i]=null;
        }
        return attrs;
    }
    
    /**
     * For unix fileystem this means the 'x' bit should be enabled. 
     */
    public boolean isAccessable() throws VlException
    {
         return isReadable();
    }
    
    /** Class method to filter out childs of type VNode */ 
    public static VNode[] applyFilter(VNode[] nodes, NodeFilter filter)
    {
        if (nodes==null)
            return null;
        
        if (filter==null)
            return nodes; 
        
        Vector<VNode>filtered=new Vector<VNode>();
        
        for (VNode node:nodes) 
            if (filter.accept(node))
                filtered.add(node); 

        VNode _nodes[]=new VNode[filtered.size()];
        _nodes=filtered.toArray(_nodes); 
        return _nodes; 
    }
    
 
    /** 
     * The length() attribute for directories is system depended and really 
     * not usuable in a Virtual environment. 
     * This method will return -1 if not implemented by the File System 
     * On unix filesystems this method provides the size of the directory
     * object needed to store the file information.
     */
    public long getLength() throws VlException 
    {
        return -1; 
    }
    
    /**
     * List the chidren and sort them. 
     * @param typeFirst  if true return directories first, then files. 
     * @param ignoreCase ignore case when sorting 
     * @return Sorted VFSNode[] array
     * @throws VlException 
     */
    public VFSNode[] listSorted(boolean typeFirst,boolean ignoreCase) throws VlException
    {
        return sortVNodes(list(),typeFirst,ignoreCase); 
    }
    
    /**
     * Returns filtered childs with specified wildcard pattern.
     * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(String pattern) throws VlException
    {
        return list(new NodeFilter(pattern,false),0,-1,null); 
    }
    
    /**
     * Returns filtered childs with specified wildcard pattern or
     * Regular Expression.  
     * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(String pattern,boolean isRegularExpression) throws VlException
    {
         return list(new NodeFilter(pattern,isRegularExpression),0,-1,null); 
    }
    
    /**
     * Returns filtered childs with specified wildcard pattern or
     * Regular Expression.  
      * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */
    public VFSNode[] list(Pattern pattern) throws VlException
    {
         return list(new NodeFilter(pattern),0,-1,null); 
    }
    
    /**
     * Returns filtered childs using the specified Node Filter.
     * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */
    public VFSNode[] list(NodeFilter filter) throws VlException
    {
        return list(filter,0,-1,null); 
    }
    
    /**
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */
    public VFSNode[] list(Pattern pattern,int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException
    {
        return list(new NodeFilter(pattern),offset,maxNodes,totalNumNodes); 
    }
    
    /**
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException /// Tree,Graph, Composite etc.
    {
        return list((NodeFilter)null,offset,maxNodes,totalNumNodes); 
    }
    
    /**
     * Returns subset of list() starting at offset with a maximum of maxNodes. 
     * Implement this method to allow for really long listings and optimized filtering !
     *  
     * @param filter          NodeFilter  
     * @param offset          Starting offset
     * @param maxNodes        Maximum size of returned node array
     * @param totalNumNodes   Total number of nodes returned by list(). Value of -1 means not known or not supported!  
     * @return                Subset of list(). 
     * @throws VlException
     */
    public VFSNode[] list(NodeFilter filter,int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException
    {
        // use default list() ! 
        
        VFSNode nodes[] =list();

        Global.debugPrintf(this,"listFiltered(): nr of UNfiltered nodes=%d\n",nodes.length);
            
        if (filter!=null)
        {
            nodes=NodeFilter.filterNodes(nodes,filter); 
        }
            
        Global.debugPrintf(this,"listFiltered(): nr of filtered nodes=%d\n",nodes.length); 
        
        if (totalNumNodes==null)
            totalNumNodes=new IntegerHolder(); 

        // method nodesSubSet allocated correct (VFSNodes[]) Array Type: 
        return (VFSNode[])VCompositeNode.nodesSubSet(nodes,offset,maxNodes,totalNumNodes); 
    }

    // ========================================================================

    public VDir createUniqueDir(String prefix, String postfix) throws VlException
    {
        if (prefix==null) 
            prefix="";
        
        if (postfix==null) 
            postfix="";
        
        String dirName=null;
        
        // 
        // synchronize: Race Condition if two thread execute the following
        // code during the SAME system milli second ! 
        // 
        synchronized (this)
        {
            do
            {
                String randStr=""+dirRandomizer.nextLong(); 
                dirName= prefix+ randStr+postfix;
            }
            
            while (existsDir(dirName)==true);
            
            return createDir(dirName);
        }
    }
    
    /**
     * Create new file and return OutputStream to write to.  
     * Call: createNewFileOutputStream()
     */
    public OutputStream putFile(String fileName) throws VlException 
    {
        return createFileOutputStream(fileName,true); 
    } 
    
    public OutputStream putFile(String fileName,boolean force) throws VlException 
    {
        return createFileOutputStream(fileName,force); 
    } 
    
    /**
     * Create new File object and return outputstream to write to. 
     * 
     * @param  fileName relative or absolute path to new file.   
     * @param  force overwrite existing or create new file if it doesn't exists. 
     * @return  OutputStream to the new VFile.  
     * @throws VlException
     */
    public OutputStream createFileOutputStream(String fileName, boolean force) throws VlException
    {
        // ===
        // Since 0.9.2 ! 
        // use new newFile interface. Create object and call getOutputStream() 
        // ===
        
        VFile file=this.getFileSystem().newFile(resolvePathVRL(fileName));
        
        if (file!=null)
        {
            return file.getOutputStream(); 
        }
        
        //
        // Backup Mechanisme: Old way: first call createFile(), then getOutputStream. 
        //
        
        VFile newFile=this.createFile(fileName,force);
        
        if (newFile==null)
            throw new nl.uva.vlet.exception.ResourceCreationFailedException("Couldn't create new file:"+fileName);
        
        if ((newFile instanceof VStreamWritable)==false)
        {
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Create file resource is not StreamWritable:"+newFile); 
        }
        
        return newFile.getOutputStream(); 
    }

    // ========================================================================
    // New Implemented Methods by using VFileSystem interface ! 
    // ========================================================================

    /**
     * Create new directory or subdirectory.  
     * 
     * @param name filename to create. If the path is absolute (starting with '/') 
     *         the full path is be used to create the new directory.
     * @param ignoreExisting if ignoreExisting==false this methods will throw an Exception when a file 
     *         already exists.<br>
     *         If ignoreExisting==true, ignore existing file. 
     *        
     * @return new created or existing VDir 
     * 
     * @throws VlException
     */
    public VDir createDir(String name, boolean ignoreExisting)
            throws VlException
    {
         VDir dir=getFileSystem().newDir(resolvePathVRL(name));
         dir.create(ignoreExisting);
         return dir;
    }
    
    /**
     * Create file in this directory or create the full (absolute) path if fileName 
     * is an absolute path. 
     * 
     * @param fileName the filename to create. If the path is absolute (starting with '/') 
     *                 the full path is be used to create the new file.
     * @param ignoreExisting if ignoreExisting==false this methods will throw an Exception when a directory 
     *        already exists.<br>
     *        If ignoreExisting==true, ignore existing file. 
     */
    public VFile createFile(String fileName, boolean ignoreExisting) throws VlException
    {
    	VFile file=getFileSystem().newFile(resolvePathVRL(fileName));
    	file.create(ignoreExisting);
    	return file; 
    }
    
    /** 
     * Returns true whether (child) filename exists and is a VFile.
     * Parameter fileName can be an absolute path or relative path starting from this 
     * directory.   
     */ 
    public boolean existsFile(String fileName) throws VlException
    {
        return this.getFileSystem().newFile(resolvePathVRL(fileName)).exists();  
    }
    
    /**
     * Returns true whether (child) directory exists and is a VDir.
     * Parameter dirName can be an absolute path or relative path starting from this 
     * directory.   
     */
    public boolean existsDir(String dirName) throws VlException
    {
        return this.getFileSystem().newDir(resolvePathVRL(dirName)).exists(); 
    }
    
     /** Alias for existsFile 
     *  @see VDir#existsFile(String) */  
    public boolean hasFile(String fileName) throws VlException {return existsFile(fileName);} 

    /** Alias for existsDir 
     * @see VDir#existsDir(String) */  
    public boolean hasDir(String dirName) throws VlException {return existsDir(dirName);}

    // ========================================================================
    // Abstract Interface Methods 
    // ========================================================================
         
    /**
     * Return listed contents of Directory.
     * <p> 
     * For large Directories and for optimized filtering it is recommended that
     * the method {@link #list(NodeFilter, int, int, IntegerHolder)} is also
     * overriden. 
     * 
     * @return array of VFSNodes  
     * @throws VlException
     */
    public abstract VFSNode[] list() throws VlException;
 
}

