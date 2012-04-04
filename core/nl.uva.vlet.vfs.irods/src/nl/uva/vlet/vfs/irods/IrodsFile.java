package nl.uva.vlet.vfs.irods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileSystem;

/**
 * Irods File. 
 */
public class IrodsFile extends VFile implements VPresentable
{
	protected IRODSFile irodsFile;

    public IrodsFile(IrodsFS fs, IRODSFile _file, VRL vrl)
	{
		super(fs, vrl);
		this.irodsFile=_file; 
	}

    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,IrodsFS.irodsFileAttributeNames); 
    }
    
//     Under construction:   
//
//    /*
//     * Optimized Iriods Query. 
//     * Merges Irods MetaData query with (default) VFS Attributes. 
//     */
//    @Override
//    public VAttribute[] getAttributes(String attrNames[]) throws VlException
//    {
//        // optimized metadata query:
//        
//        VAttributeSet attrSet=new VAttributeSet();
//        StringList queryAttrs=new StringList(); 
//        
//        // 1) Resolve nonmutable attributes 
//        for (String name:attrNames)
//        {
//            VAttribute attr=super.getNonmutableAttribute(name);
//            if (attr!=null)
//                attrSet.put(attr); 
//            else
//                queryAttrs.add(name); 
//        }
//        
//        
//       // 2) Query Irods Attributes:  
//       IrodsQuery query=new IrodsQuery(vrsContext,this.getIrodsFS()); 
//        
//       VAttributeSet[] attrSets = query.doQuery(getLocation().getDirname(),this.getName(),false,attrNames,null);
//       VAttributeSet querySet=null; 
//       
//       if (attrSets==null)
//       {
//           IrodsFS.logger.warnPrintf("IrodsFile: Query resulted in NULL attribute sets:%s\n"+this);
//       }
//       // Query duplication (need equivalent of UNIQUE)  
//       else if (attrSets.length>1)
//       {
//           querySet=attrSets[0];
//           IrodsFS.logger.warnPrintf("IrodsFile: Query for single file resulted in multiple attribute sets:%s\n",this);
//       }
//       else
//       {
//           querySet=attrSets[0];
//       }
//       
//       // 3) check query: merge attributes. 
//       for (String name:queryAttrs)
//       {
//           VAttribute attr=null; 
//           
//           // check Irods Query: 
//           if (querySet!=null)
//               attr=querySet.get(name);
//           
//           // default: get parent: 
//           if (attr==null)
//               super.getAttribute(name);
//          
//           if (attr!=null)
//               attrSet.put(attr); 
//       }
//       
//       return attrSet.toArray(); 
//    }
    
    private IRODSFileSystem getIrodsFS()
    {
        return (IRODSFileSystem) this.irodsFile.getFileSystem();
    }
    
    public VAttribute getAttribute(String name) throws VlException
    {
      if (name==null) 
          return null; 
        
      if (name.compareTo(IrodsFS.ATTR_IRODS_RESOURCE)==0) 
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
    
	public boolean create(boolean ignoreExisting) throws VlException
	{
	    // check existing: 
	    if (irodsFile.exists())
	    {
	        if (irodsFile.isDirectory()) 
	            throw new ResourceAlreadyExistsException("Can't create file, a directory with the same name already exists:"+this); 
	        
	        if (ignoreExisting)
	            return true; // alread exists; 
	        else
	            throw new ResourceAlreadyExistsException("File already exists:"+this);
	    }
	    
	    boolean result=false; 
	    
	    // create:
	    try
        {
	        result=this.irodsFile.createNewFile();
        }
        catch (IOException e)
        {
            throw new ResourceCreationFailedException("Couldn't create new file:"+this); 
        }
	    
        if (this.irodsFile.getParentFile().exists()==false)
        {
            IrodsFS.logger.warnPrintf("IrodsDir.mkdir(): parent directory doesn't exists:"+getPath()); 
        }
        
        if (result==false)
            throw new ResourceCreationFailedException("IrodsFile.createNewFile(): returned FALSE for:"+getPath());
    
        return true; 
	}
	
	
	@Override
	public long getLength() throws VlException 
	{
	    return this.irodsFile.length();  
	}

	@Override
	public long getModificationTime() throws VlException
	{
	    return this.irodsFile.lastModified(); 
	}

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
	
	public InputStream getInputStream() throws VlException
	{
	    return this.getFileSystem().openInputStream(this.irodsFile); 
	}

	public IrodsFS getFileSystem()
    {
        // Down Cast: 
        return (IrodsFS)super.getFileSystem(); 
    }
	
	public OutputStream getOutputStream() throws VlException 
	{
	    return this.getFileSystem().openOutputStream(this.irodsFile);
	}

	public VRL rename(String newName, boolean renameFullPath)
			throws VlException 
	{
	    return this.getFS().rename(false, this.irodsFile, newName, renameFullPath);
	}

	public boolean delete() throws VlException
	{
	    return this.irodsFile.delete(); 
	}

	@Override
	public boolean exists() throws VlException
	{
		if (this.irodsFile.exists()==false)
		    return false;
		    
	    return this.irodsFile.isFile(); 
	}

	// === 
	// Optimization methods  
	// Override the following methods if this File implementation 
	// can do them faster 
	// ===

    protected void uploadFrom(VFSTransfer transferInfo, VFile localSource) throws VlException 
    {
        // localSource is a file on the local filesystem 
        super.uploadFrom(transferInfo,localSource); 
         
    }

    protected void downloadTo(VFSTransfer transfer,VFile targetLocalFile) throws VlException
    {
        // copy contents into local file:
        super.downloadTo(transfer, targetLocalFile); 
    }

    protected IrodsFS getFS()
    {
    	return ((IrodsFS)this.getFileSystem());  
    }

    public String getResource() throws VlException 
    {
        try
        {
            return irodsFile.getResource();
        }
        catch (IOException e)
        {
        	throw IrodsFS.convertException("Couldn't get resource:"+this,e); 
        } 
    }
    
    public void setResource(String resource) throws VlException 
    {
        try
        {
        	irodsFile.setResource(resource); 
        }
        catch (IOException e)
        {
            throw IrodsFS.convertException("Couldn't set resource:"+this,e); 
        } 
        
    }
    
    @Override
    public String getPermissionsString() throws VlException
    {
    	return this.getFS().getPermissionsString(this.irodsFile,false);
    }

	@Override
	public Presentation getPresentation()
	{
		return getFS().getPresentation(false); 
	}
    
}
