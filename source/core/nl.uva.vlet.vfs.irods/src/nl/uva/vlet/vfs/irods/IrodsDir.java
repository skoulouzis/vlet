package nl.uva.vlet.vfs.irods;

import java.io.IOException;

import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.irods.IRODSFile;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;

/**
 * Irods Dir. 
 */
public class IrodsDir extends VDir implements VPresentable 
{ 
	protected IRODSFile irodsFile;

    public IrodsDir(IrodsFS irodsfs, IRODSFile _file, VRL vrl)
	{
		super(irodsfs, vrl);
		this.irodsFile=_file; 
	}
	
    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,IrodsFS.irodsDirAttributeNames); 
    }
    
	@Override
	public boolean create(boolean ignoreExisting) throws VlException
	{
	    if (this.irodsFile.exists())
	    {
	        if (this.irodsFile.isFile())
	            throw new ResourceAlreadyExistsException("A file withe the same name already exists and is not a directory"+getPath());
	        
	        if (ignoreExisting)
	            return true;
	        else
	            throw new ResourceAlreadyExistsException("Directory already exists:"+this); 
	    }

	    if (this.irodsFile.getParentFile().exists()==false)
	    {
	        IrodsFS.logger.warnPrintf("IrodsDir.mkdir(): parent directory doesn't exists:"+getPath()); 
	    }
	    
        // create subdir: 
        boolean result=this.irodsFile.mkdir();
        
        if (result==false)
            throw new ResourceCreationFailedException("mkdir() returned FALSE for:"+getPath());
  
        return true; 
	}
	
	@Override
	public boolean exists() throws VlException
	{
	    if (this.irodsFile.exists()==false)
	        return  false;
	    
	    return this.irodsFile.isDirectory(); 
	}
	
	@Override
	public VFSNode[] list() throws VlException 
	{
	    // full query! 
	    GeneralFile[] files = irodsFile.listFiles(); 
	    
	    if (files==null)
	        return null;
	    
	    IrodsFS irodsfs = getFS(); 
	    
	    VFSNode nodes[]=new VFSNode[files.length];
	    int index=0;
	    
	    for (GeneralFile file:files)
	    {
	        VFSNode node=null; 
	        
	        String absPath=file.getAbsolutePath();
	        
	        VRL vrl=this.resolvePathVRL(absPath);
	        
	        if (file.isDirectory())
	        {
	            node=new IrodsDir(irodsfs,(IRODSFile)file,vrl);
	        }
	        else
	        {
	            // treat other as file 
	            node=new IrodsFile(irodsfs,(IRODSFile)file,vrl);
	        }
	        
	        nodes[index++]=node; 
	        
	    }
	    
	    return nodes; 
	}
	
	public IrodsFS getFileSystem()
	{
	    // Down Cast: 
	    return (IrodsFS)super.getFileSystem(); 
	}
	
	@Override
	public long getModificationTime() throws VlException
	{
	    return this.irodsFile.lastModified();  
	}

	@Override
	public boolean isReadable() throws VlException 
	{
		return irodsFile.canRead();
	}

	@Override
	public boolean isWritable() throws VlException
	{
		return irodsFile.canWrite(); 
	}

	@Override
	public boolean isHidden() 
	{
		return irodsFile.isHidden(); 
	}

	public long getNrOfNodes() throws VlException
	{
	    String files[]=this.irodsFile.list();
		
		if (files==null)
			return 0; 
		
		return files.length; 
	}

	public VRL rename(String newName, boolean renameFullPath)
			throws VlException
	{
	    return this.getFileSystem().rename(this,newName,renameFullPath); 
	}

	public boolean delete(boolean recurse) throws VlException
	{
	    // check recurse: 
	    return this.irodsFile.delete();
	}
	
	protected IrodsFS getFS()
    {
    	return ((IrodsFS)this.getFileSystem()); 
    }

	
	 @Override
	 public String getPermissionsString() throws VlException
	 {
		 return this.getFS().getPermissionsString(this.irodsFile,true);
	 }
	 
	 @Override
	 public Presentation getPresentation()
	 {
		 return getFS().getPresentation(true); 
	 }
	 
	// =============================
	// Todo: Bulk Uploads/downloads: 
	// =============================

}
