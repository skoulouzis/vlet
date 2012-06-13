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
 * $Id: LocalFSFactory.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vfs.localfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_GID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SYMBOLICLINKTARGET;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/**
 * Local VFSClient implementation of the VFS interface.
 * 
 * Note that the LocalFS implementation can have only ONE INSTANCE !!!
 * This to avoid concurrency problems (these are not handled in this class)  
 * 
 * @author P.T. de Boer
 * @see nl.uva.vlet.vfs.VFS
 *  
 */

public class LocalFSFactory extends VFSFactory
{
	// ======================================================================
	// Class
    // =======================================================================
	
	
    //	*** Class attributes ***
    
    /**
     * Service name is 'localfs', not 'file' although 'file' is allowed
     * since this is a default www scheme ("file://")
     * 
     * @see #supportedTypes 
     */ 
    
    //final static String TYPE_LOCALFS=VRS.FILE_SCHEME;
    
    /**
     * Supported service protocols, like 'file://','Dir://' and 'File://'.
     * File and Dir (note capital first letter) could be used to explicitly
     * specify a File or a Directory (Not used yet) 
     */
    public static String schemes[]={VRS.FILE_SCHEME,VFS.DIR_TYPE,VFS.FILE_TYPE,"localfs"};
   
	public static String unixFSAttributeNames[]=
	{
		ATTR_UNIX_FILE_MODE,
		ATTR_SYMBOLICLINKTARGET,
		ATTR_GID,
		ATTR_UID
	};
	
	public static String serverAttributes[]=
		{
			ATTR_SCHEME,
			ATTR_PATH
		}; 
	
	// =======================================================================
	// Instance
    // =======================================================================
    
    /**
     * Contructor is called in the VRS Registry. 
     */ 
    public LocalFSFactory()
    {
    	// initFS(); 
    }
   
    public String[] getSchemeNames()
    {
        return schemes; 
    }
    
    /** Return name of service */ 
    public final String getName()
    {
        return "LocalFS";
    }

    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
	{
    	// defaults: 
    	info=super.updateServerInfo(context, info, loc); 
	    
	    info.removeAttributesIfNotIn(new StringList(serverAttributes));
	    
	    info.setNeedHostname(false); 
	    info.setNeedPort(false); 
	    info.store(); 
	    
	    return info; 
	}
    
	@Override
	public void clear() 
	{
		
	}
	

	@Override
	public VFileSystem createNewFileSystem(VRSContext context,
			ServerInfo info, VRL location) 
	{
		return new LocalFilesystem(context,info,location); 
	}
	
}
