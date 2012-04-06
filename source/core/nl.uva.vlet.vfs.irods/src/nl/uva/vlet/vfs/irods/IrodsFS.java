package nl.uva.vlet.vfs.irods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlServerException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import nl.vlet.uva.grid.globus.GlobusUtil;

import org.ietf.jgss.GSSCredential;

import edu.sdsc.grid.io.irods.IRODSAccount;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileInputStream;
import edu.sdsc.grid.io.irods.IRODSFileOutputStream;
import edu.sdsc.grid.io.irods.IRODSFileSystem;

/**
 *  IrodsFS. Minimalistic implementation. 
 *  
 *  @author Piter T. de Boer 
 */ 
public class IrodsFS extends FileSystemNode
{
    // ===
    // Class Methods
    // ===

    static ClassLogger logger;

    
    static
    {
        logger=ClassLogger.getLogger(IrodsFS.class); 
        //logger.setLevelToDebug();
    }
    
    // Helper method 
    public static IrodsFS createFS(String host, int port,String user,String home,String zone,String defaultResource) throws VlException
    {
        VRSContext ctx = VRSContext.getDefault();
        
        VRL vrl=new VRL("irods",host,port,home);  
        ServerInfo info=ctx.getServerInfoFor(vrl, true); 
        info.setAttribute(IRODSConfig.ATTR_IRODS_DEFAULT_RESOURCE,defaultResource);
        info.setAttribute(IRODSConfig.ATTR_IRODS_ZONE,zone); 
        info.setUsername(user); 
        
        return new IrodsFS(ctx,info,vrl); 
    }
    
    public static final String ATTR_IRODS_RESOURCE="irodsResource"; 
    
	public static String[] irodsFileAttributeNames=
		{
			ATTR_IRODS_RESOURCE
		};

	public static String[] irodsDirAttributeNames=
		{
	
		};

    // =================================================================
	// Instance
	// =================================================================
    
    private IRODSAccount irodsAccount;
    
    private IRODSFileSystem irodsFS;

	private Presentation filePresentation;

	private Presentation dirPresentation; 
	
	public IrodsFS(VRSContext context, ServerInfo info,VRL location) throws VlException 
	{
		super(context, info);
		
		//auto connect: 
		connect();
	}
	
	/** Return Jargon FileSystem */ 
	public IRODSFileSystem getIRODSFileSystem()
	{
	    return this.irodsFS; 
	}
	
    @Override
    public IrodsDir newDir(VRL vrl) throws VlException
    {
        // create object only, do not resolve here !
        return new IrodsDir(this,newIrodsFile(vrl.getPath()),vrl);
    }

    @Override
    public IrodsFile newFile(VRL vrl) throws VlException
    {
        // create object only, do not resolve here !
        return new IrodsFile(this,newIrodsFile(vrl.getPath()),vrl);  
    }
    
    @Override
    public IrodsDir openDir(VRL vrl) throws VlException
    {
        IrodsDir dir=newDir(vrl); 
        
        // getDir must return Existing directory: 
        if (dir.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Directory doesn't exist or is a invalid path:"+vrl.getPath()); 
        
        return dir; 
    }

    @Override
    public IrodsFile openFile(VRL vrl) throws VlException
    {
        IrodsFile file=newFile(vrl); 
        
        // getDir must return Existing directory: 
        if (file.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("File doesn't exists or is an invalid path:"+vrl.getPath()); 
        
        return file; 
    }


	public void connect() throws VlException 
	{
	    ServerInfo info = this.getServerInfo();
	    
	    logger.debugPrintf("Connecting to:%s\n",this.getServerVRL());
        logger.debugPrintf(" - irods user            ='%s'\n",getUser()); 
        logger.debugPrintf(" - irods home            ='%s'\n",getHomePath()); 
        logger.debugPrintf(" - irods zone            ='%s'\n",getZone()); 
        logger.debugPrintf(" - irods defaultResource ='%s'\n",getDefaultResource()); 
        logger.debugPrintf(" - irods AUTH method     ='%s'\n",info.getAuthScheme()); 
        
        // either one: 
        String passwd=null;
	    GSSCredential gssCred;  
	        
	    if (info.useGSIAuth())
	    {
	        GridProxy proxy = this.vrsContext.getGridProxy();
	        if (proxy.isValid()==false)
	            throw new nl.uva.vlet.exception.VlAuthenticationException("Invalid Grid Proxy. Please create one.");
	        
	        gssCred = GlobusUtil.createGSSCredential(proxy);
	        
	        if (gssCred==null)
	            throw new nl.uva.vlet.exception.VlAuthenticationException("Failed to get GSS Credential. Got null");
	        
	        passwd=proxy.getProxyFilename(); 
	    }
	    else
	    {
	        gssCred=null;  
	        passwd=info.getPassword();
	    }
	    
	    // causes null pointer exceptions.
	    if (passwd==null)
	        passwd="";  
	    
	    this.irodsAccount=new IRODSAccount(
                    getHostname(),
                    getPort(),
                    getUser(),
                    passwd,
                    getHomePath(),  
                    getZone(),
                    getDefaultResource());
	    
	    // Method auto enables GSI authentication. 
	    // Set to NULL to switch to password authentication. 
	    
	    this.irodsAccount.setGSSCredential(gssCred);
	    
        try
        {
            this.irodsFS=new IRODSFileSystem(irodsAccount);
            
            logger.infoPrintf("Connected to irods server %s:%d. Server Version='%s'\n",
                    getHostname(),
                    getPort(),
                    this.irodsFS.getVersion());  
            
            // POST update valid Server Info ! 
            info.setHasValidAuthentication(true);
            
//            String[] dirs = this.irodsFS.getRootDirectories();
//            if (dirs!=null)
//                for (String dir:dirs)
//                {
//                    System.err.println(" - "+dir); 
//                }
            
         
        }
        catch (NullPointerException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Connecting failed to: %s\n",this);
            throw new VlServerException("Couldn't connect to:"+this+"\n"
                                        +e.getMessage(),e); 
        }
        catch (IOException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Connecting failed to: %s\n",this);
            throw new VlServerException("Couldn't connect to:"+this+"\n"
                                        +e.getMessage(),e); 
        } 
	}
	
    public String getUser()
    {
        return this.getServerInfo().getUsername(); 
    }
    
    public String getZone()
    {
        return this.getServerInfo().getAttributeValue(IRODSConfig.ATTR_IRODS_ZONE);  
    }
    
    public String getDefaultResource()
    {
        return this.getServerInfo().getAttributeValue(IRODSConfig.ATTR_IRODS_DEFAULT_RESOURCE);  
    }
    
    public String getHomePath()
    {
        // return this.getServerInfo().getAttributeValue(IRODSConfig.IRODS_HOME_PATH);
        return this.getPath();// return default path from ServerInfo !  
    }
	
	public void disconnect() throws VlException
	{
	    try
        {
            this.irodsFS.close();
        }
        catch (IOException e)
        {
            logger.logException(ClassLogger.WARN,e,"Exception while closing irods Filesystem:s\n",this);  
        } 
	    this.irodsFS=null;
	}

	public boolean isConnected() 
	{
		return (this.irodsFS!=null);
	}

    public IRODSFile newIrodsFile(String dirname,String filename)
    {
        return new IRODSFile(this.irodsFS,dirname,filename);   
    }
    
    public IRODSFile newIrodsFile(String fullpath)
    {
        return new IRODSFile(this.irodsFS,fullpath);  
    }
    

    public IRODSFile newIrodsFile(VRL vrl)
    {
        return new IRODSFile(this.irodsFS,vrl.getPath());
    }
	
	@Override
	public VFSNode openLocation(VRL vrl) throws VlException
	{
	    String path=vrl.getPath();
	    
        if (path.startsWith("~"))
            path=path.replace("~",this.getDefaultUserHomePath()); 

        if (path.startsWith("/~"))
            path=path.replace("/~",this.getDefaultUserHomePath());
        
	    vrl=vrl.copyWithNewPath(path); 
	    
	    IRODSFile file = newIrodsFile(vrl); 
	     
	    if (file.exists()==false)
	        logger.warnPrintf("***Warning: Filepath doesn't exist!:%s\n",file.getPath()); 
	    
        if (file.isDirectory())
        {
            return new IrodsDir(this,file,vrl);            
        }
        
        if (file.isFile())
        {
            return new IrodsFile(this,file,vrl);            
        }
	    
	    // treat everything else as directory 
	    return new IrodsDir(this,file,vrl);
		//throw new nl.uva.vlet.exception.ResourceNotFoundException("Don't know what this is:"+vrl);
	}


	/**
	 * Returns Default User home := HOMES_PREFIX +"/"+ USERNAME 
	 */  
    public String getDefaultUserHomePath()
    {
        String homesPath=this.getServerInfo().getAttributeValue(IRODSConfig.ATTR_IRODS_HOMES_PREFIX);
        String user=this.getServerInfo().getUsername();
        if (user==null)
            user=Global.getUsername(); 
        
        if (homesPath==null)
            homesPath="/"; 
                
        return VRL.uripath(homesPath+"/"+user); // normalize  
    }

    public VRL rename(IrodsDir irodsDir, String newName, boolean renameFullPath)
    {
        return rename(true,irodsDir.irodsFile,newName,renameFullPath); 
    }
    
    public VRL rename(IrodsFile irodsFile, String newName, boolean renameFullPath)
    {
        return rename(true,irodsFile.irodsFile,newName,renameFullPath); 
    }
    
    public VRL rename(boolean isDir,IRODSFile irodsFile,String newName, boolean renameFullPath)
    {
        String oldPath=irodsFile.getAbsolutePath(); 
        String newPath;
        
        // relative names: 
        if ( (renameFullPath==false) || (newName.startsWith("/")==false))
        {
            //resolve against parent directory 
            newPath=VRL.resolvePaths(VRL.dirname(oldPath),newName); 
        }
        else
        {
            newPath=newName; 
        }
        
        IRODSFile newDir = this.newIrodsFile(newPath);
        
        logger.debugPrintf("Rename:(%s) '%s' -> '%s'\n",
                (isDir?"dir":"file"),oldPath,newPath);
        
        if (irodsFile.renameTo(newDir)==false)
            return null; 
        
        // use URI from IRODS ! 
        return new VRL(newDir.toURI());   
    }

    
    // ========================================================================
    // Input/Output
    // ========================================================================
    
    
    public InputStream openInputStream(IRODSFile irodsFile) throws VlException
    {
        java.net.URI uri=irodsFile.toURI(); 
        
        try
        {
            IRODSFileInputStream inps;
            inps = new IRODSFileInputStream(irodsFile);
            return inps;
        }
        catch (IOException e)
        {
            throw convertException("Couldn't open inputstream to:"+uri,e); 
        } 
    }

    public InputStream openInputStream(VRL location) throws VlException
    {
        return openInputStream(newIrodsFile(location));   
    }
    
    public OutputStream createOutputStream(VRL location) throws VlException
    {
        return openOutputStream(newIrodsFile(location)); 
    }

    public OutputStream openOutputStream(VRL location) throws VlException
    {
        return openOutputStream(newIrodsFile(location)); 
    }

    public OutputStream openOutputStream(IRODSFile irodsFile) throws VlException
    {
         java.net.URI uri=irodsFile.toURI(); 
         
         try
         {
             IRODSFileOutputStream outps = new IRODSFileOutputStream(irodsFile);
             return outps;
         }
         catch (IOException e)
         {
             throw convertException("Couldn't open outputstream to:"+uri,e); 
         } 
    }

    public static VlException convertException(String msg, Exception e)
    {
    	if (e instanceof IOException)
    	{
    		return new VlIOException(msg,e); 
    	}
         
    	return new VlException(msg,e); 
    }

	public String getPermissionsString(IRODSFile irodsFile, boolean isDir) throws VlException 
	{
		try 
		{
			return irodsFile.getPermissions();
		}
		catch (IOException e)
		{
			throw IrodsFS.convertException("Couldn't get permissions:"+irodsFile.getAbsolutePath(),e); 
		} 
    }

	public Presentation getPresentation(boolean isDir) 
	{
		String type=VFS.FILE_TYPE;

		if (isDir)
			type=VFS.DIR_TYPE; 
		
		if ( (isDir==true) && (dirPresentation!=null) )
				return dirPresentation; 
		
		if ( (isDir==false) && (filePresentation!=null) )
				return filePresentation; 
		
		// Use presentation store: 
		Presentation pres=Presentation.getPresentationFor(getServerVRL(), type,true); 
		StringList list = new StringList(pres.getChildAttributeNames());  
		list.addUnique(IrodsFS.ATTR_IRODS_RESOURCE);
		pres.setChildAttributeNames(list.toArray()); 
		
		if (isDir)
			dirPresentation=pres; 
		else
			filePresentation=pres; 
		
		return pres; 
	}

    public IrodsQuery createQuery()
    {
        return new IrodsQuery(this.vrsContext,this.irodsFS);
    }

}	
