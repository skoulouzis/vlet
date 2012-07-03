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
 * $Id: SRMFSFactory.java,v 1.4 2011-09-26 14:07:41 ptdeboer Exp $  
 * $Date: 2011-09-26 14:07:41 $
 */ 
// source: 

package nl.uva.vlet.vfs.srm;

import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import nl.vlet.uva.grid.globus.GlobusUtil;

public class SRMFSFactory extends VFSFactory
{
    public static final String ATTR_SRM_REQUEST_TIMEOUT="srmRequestTimeOut"; 
    
	public static final String SRM_SCHEME = "srm";

	public final static String schemes[]={SRM_SCHEME};
	
	public SRMFSFactory()
	{
	    // make Globus Bindings are initialized 
	    GlobusUtil.init(); 
	}
	
//	@Override
//	public VFileSystem getFileSystem(VRSContext context, VRL location) throws VlException
//	{
//		SRMFileSystem srmClient=SRMFileSystem.getClientFor(context,location);	
//		srmClient.connect(); 
//		
//		return srmClient; 
//	}
	
	@Override
	public void clear()
	{
		SRMFileSystem.clearClass(); 
	}

	@Override
	public String getName()
	{
		return "SRM";
	}
	
	@Override
	public String[] getSchemeNames()
	{
		return schemes; 
	}
	
	@Override
	public VFileSystem createNewFileSystem(VRSContext context, ServerInfo info,
			VRL location) throws VlException 
	{
		// auto update port:  
		if (location.getPort() <= 0)
		{
		    location = SRMFileSystem.resolveToV22SRM(context, location);
		    info.setPort(location.getPort()); 
		    info.store(); 
		}
		return new SRMFileSystem(context,info,location); 
	}
	
	@Override
	public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
	{
		if (info==null) 
		{
			info=ServerInfo.createFor(context, loc); 
		}
		
		VAttributeSet tmpSet=new VAttributeSet();
		
		tmpSet.set(VAttributeConstants.ATTR_PORT,8443); 
		tmpSet.set(VAttributeConstants.ATTR_HOSTNAME,"SRMHOST"); 
		// inheret from config manager 
		tmpSet.set(ATTR_SRM_REQUEST_TIMEOUT,context.getConfigManager().getServerRequestTimeOut()); 
		
		info.matchTemplate(tmpSet,true); 
		
		// Do not allow 0 or 'default' ports 
		if (info.getPort()<=0)
			info.setPort(8443); 
		
		// explicit set/update to use GSI authentication. 
		info.setUseGSIAuth();
		info.setSupportURIAtrributes(true); // srmCount and srmOffset enabled ! 
		// Store in persistant registry; 
		info.store(); // !
		return info ;    
	}
	
}
