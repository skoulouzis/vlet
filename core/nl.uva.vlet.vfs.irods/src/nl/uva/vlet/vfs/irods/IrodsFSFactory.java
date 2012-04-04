package nl.uva.vlet.vfs.irods;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

/**
 *  Example dummy Skeleton VFS Factory 
 */ 

public class IrodsFSFactory extends VFSFactory
{
	
	static private String schemes[]=
		{
			"irods"
		}; 
		

	@Override
	public void clear() 
	{
		// clear class 
	}

	@Override
	public String getName() 
	{
		return "IRODS";
	}

	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}
	
	public VFileSystem createNewFileSystem(VRSContext context,ServerInfo info, VRL location) throws VlException
	{
		return new IrodsFS(context,info,location); 
	}
	
	// See super method 
	public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
	{
		// initialize defaults from super  
		info=super.updateServerInfo(context, info, loc); 
		
		// Delegate to IRODSConfig helper class: 
		IRODSConfig.updateServerInfo(context,info,loc); 
		        
		info.store(); //update in registry ! 
		
		return info; 
	}
}
