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
 * $Id: SftpFSFactory.java,v 1.1 2011-11-25 13:20:38 ptdeboer Exp $  
 * $Date: 2011-11-25 13:20:38 $
 */ 
// source: 

package nl.uva.vlet.vfs.jcraft.ssh;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ACCESS_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;
import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class SftpFSFactory extends VFSFactory
{
    // ========================================================================
    // class 
    // ========================================================================

	  public static final String ATTR_KNOWN_HOSTS_FILE = "sshKnownHostsFile";
	  public static final String ATTR_SSH_CONFIG_DIR = "sshConfigDir";
	 

	static String sftpFileAttributeNames[]=
        {
            ATTR_ACCESS_TIME,
            ATTR_UID,
            ATTR_GID,
            ATTR_UNIX_FILE_MODE
        };
    
    static String sftpDirAttributeNames[]=
    {
        ATTR_ACCESS_TIME,
        ATTR_UID,
        ATTR_GID,
        ATTR_UNIX_FILE_MODE
    };
    // ========================================================================
    // instance
    // ========================================================================
    
    private String schemeNames[]=
        {VFS.SFTP_SCHEME,"sshftp"}; 
    
    @Override
    public String getName()
    {
        return "SFTP"; 
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemeNames;  
    }

    private void debugPrintf(String format,Object... args)
    {
        ClassLogger.getLogger(SftpFSFactory.class).debugPrintf(format,args); 
    }
    
    /**
     * Return authentication information object for this location. 
     * The default implementation is to check the Authentication Store.
     * If non is available the default for ssh/sftp is returned. 
     */ 
    @Override
    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL location)
    {
    	if (info==null)
    		info=ServerInfo.createFor(context,location); 

    	String user=info.getUsername(); 
        
        String defaultUser=Global.getUsername();
      	// sftp MUST have username: set current to default ! 
        if ((user==null) || (user.compareTo("")==0)) 
        {
            info.setUsername(defaultUser);
        }
        info.setNeedUserinfo(true);
        info.setIfNotSet(VAttributeConstants.ATTR_HOSTNAME,"hostname"); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_IDENTITY,"id_rsa"); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_USE_PROXY,false); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_PROXY_USERNAME,info.getUsername()); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_PROXY_HOSTNAME,"<none>"); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_PROXY_PORT,22); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_LOCAL_PROXY_PORT,0); 
        
        // always use password authentication: 
        info.setUsePasswordAuth();
        info.store(); // copy into registry ! 
        
    	//info.setServerAttributes(this.getDefaultServerAttributes());
    	
        return info; 
    }

    @Override
    public void clear()
    {
        SftpFileSystem.clearServers(); 
    }
    
        
    public String getAbout()
    {
        return "<html><body> <center> <table border=0 cellspacing=4 width=400>"
                +"<tr bgcolor=#c0c0c0><td><h3>SFTP VFS plugin based on JCraft</h3></td></tr>"
                +"<tr bgcolor=#f0f0f0><td>More information: <a href=http://www.jcraft.com>www.jcraft.com</a></td></tr>"
                + "</table></center></body></html>";  
    }
    
    @Override
    public VFileSystem openFileSystem(VRSContext context, VRL location) throws VlException
    {
     // Enter critical region to avoid multithreaded duplication
        // of server objects 
        ServerInfo info=context.getServerInfoFor(location,true);
        // default serverid is created from location
        // Create new Default Server.
        
        // MUST match actual server id in cache!
        String serverid=SftpFileSystem.createServerID(info.getHostname(),info.getPort(),info.getUsername()); 
        
        SftpFileSystem server = (SftpFileSystem) context.getServerInstance(serverid,SftpFileSystem.class); 
        
        if (server==null)
        {
            Global.infoPrintf(this,"Creating new ResourceSystem:%s\n",serverid); 
            server=createNewFileSystem(context,info,location);
            
            if (server.getServerID().equals(serverid)==false)
                throw new VlInternalError("Server IDs don't match!. "+serverid+" <==> "+server.getServerID()); 
            
            // server.setID(serverid);
            //
            // Store server in Context depened ServerRegistry 
            // for later use 
            context.putServerInstance(serverid,server); 
        }
        
        return server; 
        
    }
    
	@Override
	public SftpFileSystem createNewFileSystem(VRSContext context, ServerInfo info,
			VRL loc) throws VlException 
	{
        debugPrintf("ServerInfo host:port =%s\n",info.getHostname()+":"+info.getPort());
        debugPrintf("ServerInfo username   =%s\n",info.getUsername()); 
        debugPrintf("Location   host:port =%s\n",loc.getHostname()+":"+loc.getPort());
        debugPrintf("Location   username   =%s\n",loc.getUsername()); 

	    // create new  
	    int port=loc.getPort(); 

	    if (port<=0)
	        port=info.getPort(); 
	    
	    // update port
	    if (port<=0) 
	        port=VRS.DEFAULT_SSH_PORT;
        
        //***
        // be carefull with the passwd and passphrase fields ! 
        //***
        
        debugPrintf("passwd     =%s\n",((info.getPassword()!=null)?"yes":"no"));
        debugPrintf("passphrase =%s\n",((info.getPassphrase()!=null)?"yes":"no"));
        

        SftpFileSystem server = new SftpFileSystem(context, info, info.getServerVRL());
        // match user !
        server.setFinalUserSubject(context);
            
        return server;
        
	}
    
}
