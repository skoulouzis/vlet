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
 * $Id: SkelFSFactory.java,v 1.5 2011-05-02 13:36:11 ptdeboer Exp $  
 * $Date: 2011-05-02 13:36:11 $
 */ 
// source: 

package nl.uva.vlet.vlet.vfs.skelfs;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;


/**
 *  Example dummy Skeleton VFS Factory
 *  Creates FileSystem resources implementing the VFileSystem interface.  
 */ 
public class SkelFSFactory extends VFSFactory
{
	// ========================================================================
	// Static 
	// ========================================================================
	
	public static final int DEFAULT_PORT = 12345;
	
	// Example schemes. A FileSystem implementation can support mutliple schemes. 
	static private String schemes[]=
		{
			"skfs",
			"skelfs"
		}; 

	// ========================================================================
	// Instance
	// ========================================================================

	@Override
	public VFileSystem openFileSystem(VRSContext context, VRL location)
			throws VlException 
	{
		// 
		VFileSystem fs=super.openFileSystem(context,location);
		return fs; 
	}
	
	public SkelFS createNewFileSystem(VRSContext context,ServerInfo info, VRL location)
	{
		// Create new FileSystem instance 
		// Use VRSContext for User Context specific settings. 
		// Check ServerInfo for Resource Info settings and propertie. 
		return new SkelFS(context,info,location);
	}
	
	
	@Override
	public void clear() 
	{
		// clear class, clean up cached objects and close filesystems. 
	}

	@Override
	public String getName() 
	{
		return "SkelFS";
	}

	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}
	
	
	// See super method 
	public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
	{
		// defaults: 
		info=super.updateServerInfo(context, info, loc); 
		int port=info.getPort();
		
		// update default port. 
		if (port<=0) 
		    info.setPort(DEFAULT_PORT);
		
		// === 
		// Check global properties from Context (AND System Properties) 
		// === 
		String par1=context.getStringProperty("skelfs.defaultParameter1");
		
		if (StringUtil.isEmpty(par1)==false)
		    info.setIfNotSet(new VAttribute("parameter1",par1), true);
		else
		    info.setIfNotSet(new VAttribute("parameter1","value1"), true);
		
        info.setIfNotSet(new VAttribute("parameter2","value2"), true);
        
		info.store(); //explicit update in registry after changing ! 
		
		return info; 
	}
}
