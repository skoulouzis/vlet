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
 * $Id: SftpFileSystem.java,v 1.2 2011-12-07 10:19:58 ptdeboer Exp $  
 * $Date: 2011-12-07 10:19:58 $
 */ 
// source: 

package nl.uva.vlet.vfs.jcraft.ssh;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ACCESS_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlAuthenticationException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.io.StreamUtil;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.jcraft.ssh.SSHChannel.SSHChannelOptions;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VShellChannelCreator;
import nl.uva.vlet.vrs.net.VOutgoingTunnelCreator;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

/**
 *  SFTP FileSystem implementation. 
 *  The jsch session can provide a FTP channel and a command line channel. 
 *  Also tunneling is supported by the API. 
 */
public class SftpFileSystem extends FileSystemNode implements VOutgoingTunnelCreator, VShellChannelCreator
{
    // === class stuff
    
    private static Hashtable<String, SftpFileSystem> servers = new Hashtable<String, SftpFileSystem>();

    private static String createServerID(String host, int port, String user)
    {
        // must use default port in ServerID ! 
        if (port <= 0)
            port = VRS.DEFAULT_SSH_PORT;

        return user + "@" + host + ":" + port;
    }

    public class VLUserInfo implements UserInfo
    {
        public VLUserInfo()
        {
            super();
        }

 
        public String getUsername()
        {
            return getServerInfo().getUsername(); 
        }

        public String getPassphrase()
        {
            return getServerInfo().getPassphrase(); 
        }

        public String getPassword()
        {	
            return getServerInfo().getPassword();
        }

        // this object has no acces to the gui... 
        public boolean promptPassword(String message)
        {
        	if (getAllowUserInterAction()==false)
        	{
        		// use store password: 
        		if (StringUtil.isEmpty(getPassword())==false) 			
        			return true;
        		return false; 
        	}
        	
        	// getVRSContext().getConfigManager().getHasUI(); 
        	String field=uiPromptPassfield(message);
        	
        	if (field!=null)
        	{
        		getServerInfo().setPassword(field); 
        		field=null; 
        		return true;
        	}
        	
        	return false;
        }

        public boolean promptPassphrase(String message)
        {
        	if (getAllowUserInterAction()==false)
        	{
        		// use store password: 
        		if (StringUtil.isEmpty(getPassphrase())==false) 			
        			return true;
        		return false; 
        	}

        	
        	// jSch doesn't provide username in message ! 
        	message="Authentication needed for: "+getUsername()+"@"+getHostname()+"\n"
        	        +message; 
        	
        	// getVRSContext().getConfigManager().getHasUI(); 
        	String field=uiPromptPassfield(message);
        	
        	if (field!=null)
        	{
        		getServerInfo().setPassphrase(field); 
        		field=null; 
        		return true;
        	}
        	
        	return false;
        }

        public String uiPromptPassfield(String message)
    	{
           
            StringHolder secretHolder=new StringHolder(null); 
            
            boolean result=getVRSContext().getUI().askAuthentication(message,
                    secretHolder); 
             
            if (result == true) 
                return secretHolder.value; 
            else
                return null;
        }


        public void showMessage(String message)
        {
            if (getAllowUserInterAction()==false)
            {
                return;
            }
            else
            {
                getVRSContext().getUI().showMessage(message);
            }
            
        }
        
        public boolean promptYesNo(String message)
        {
            if (getAllowUserInterAction()==false)
            {
                return getDefaultYesNoAnswer(message); 
            }
            else
            {
                return getVRSContext().getUI().askYesNo("Yes or No?",message, false);
            }
        	
        }
    }
    
    // =============================================================================
    
    // =============================================================================

    private static Random portRandomizer=new Random(); 
    
    private static Vector<Integer> usedLocalPorts=new Vector<Integer>();
    
    public VRL getServerVRL()
    {
        return new VRL(VRS.SFTP_SCHEME,null,this.getHostname(),getPort(),null);  
    }
    /*
     public static final int SSH_FX_OK = 0;
     
     public static final int SSH_FX_EOF = 1;
     
     public static final int SSH_FX_NO_SUCH_FILE = 2;
     
     public static final int SSH_FX_PERMISSION_DENIED = 3;
     
     public static final int SSH_FX_FAILURE = 4;
     
     public static final int SSH_FX_BAD_MESSAGE = 5;
     
     public static final int SSH_FX_NO_CONNECTION = 6;
     
     public static final int SSH_FX_CONNECTION_LOST = 7;
     
     public static final int SSH_FX_OP_UNSUPPORTED = 8;
     */
    public static String[] errorStringsList =
        {       
            "SSH:OK",                   // 0=No error SSH_FX_OK 
            "SSH:End of file reached",  // 1=SSH_FX_EOF
            "SSH:No such file",         // 2=SSH_FX_NO_SUCH_FILE
            "SSH:Permission denied",    // 3=...
            "SSH:General failure",      // 4
            "SSH:Bad message",          // 5
            "SSH:No connection",        // 6
            "SSH:Connection lost",      // 7
            "SSH:Unsupported operation" // 8
        };
    
    // =======================================================================
    // Instance 
    // ======================================================================= 
    
    // === instanceSSH_FX_OK

    //private String hostname = null;
    //private int port = DEFAULT_SSH_PORT;
    

    private ChannelSftp sftpChannel;

    private nl.uva.vlet.vfs.jcraft.ssh.SftpFileSystem.VLUserInfo userInfo;

    private JSch jschInstance;

    private Session session;

    final Boolean serverMutex=new Boolean(true);

	private String defaultHome="/";
	
	private String userSubject;
    
    // ======================================================================= 
    // Server Critical
    // =======================================================================
    
    private void init(ServerInfo info) throws VlException
    {
        Debug("init for:" + info.getUsername() + "@" + getHostname() + ":" + getPort());

        this.userInfo = new VLUserInfo(); 
        
        this.serverID = createServerID(getHostname(), getPort(), info.getUsername());

        this.connect(); 
    }

    /**
     *  
     */
    public SftpFileSystem(VRSContext context,ServerInfo info, VRL location) throws VlException
    {
    	super(context,info);
    	
        init(info);
        Debug("New:" + this);
    }

    public String getUsername()
    {
        return this.userInfo.getUsername();
    }

    // =======================================================================
    // Authentication 
    // =======================================================================
    
    public boolean getDefaultYesNoAnswer(String optMessage)
    {
        return this.getServerInfo().getBoolProperty(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER, false); 
    }
    
    public void setDefaultYesNoAnswer(boolean value)
    {
        ServerInfo info=getServerInfo(); 
        info.setAttribute(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER, value);
        info.store();
    }
    
    // =======================================================================
    // === synchronized methods === 
    // =======================================================================
    public VFSNode openLocation(VRL vrl) throws VlException
    {
    	// No Exceptions: store ServerInfo !
        
        String path=vrl.getPath();
        
        if ((path==null) || (path.compareTo("")==0))
            path=getHomeDir();

        // ~/ home expansion 
        if (path.startsWith("~")) 
            path=getHomeDir()+path.substring(1);
        
        if (path.startsWith("/~")) 
            path=getHomeDir()+path.substring(2); 
        
        return getPath(path);   
    }
    
    public VFSNode getPath(String path) throws VlException
    {
        String user = getUsername();
        int port = getPort();
        String host = getHostname();

        // '~' expansion -> default home 
        
        if (path.startsWith("~"))
        	path=defaultHome+"/"+path.substring(1,path.length());
        else if (path.startsWith("/~"))
       	 	path=defaultHome+"/"+path.substring(2,path.length()); 
        //
        // hide default port from location 
        // IMPORTANT: must match accountID in ServerInfo ! 
        // 
        
        if (port == VRS.DEFAULT_SSH_PORT)
            port = 0;

        VRL vrl = new VRL(VRS.SFTP_SCHEME, user, host, port, path);

        Debug("getPath:" + path);
        
        SftpATTRS attrs = null;
        
        try
        {
            synchronized(serverMutex)
            {
                checkState();
                
            	// Must throw the right Exception: 
            	
            	
                /*// cd to dirname to speed up stat 
                String pwd=sftpChannel.pwd();
                String dirname=VRL.dirname(path);
                
                if (pwd.compareTo(dirname)!=0) 
                {
                    Debug("cd to:"+dirname); 
                   sftpChannel.cd(dirname); 
                }
                
                attrs = sftpChannel.lstat(VRL.basename(path));

                //attrs = sftpChannel.stat(VRL.basename(path));
                */
                // do not resolve (possivle errornous) link:
                attrs=this.getSftpAttrs(path,false);
            }
        }
        catch (Exception e)
        {
            throw convertException(e);
        }
        
        if (attrs == null)
        {
             throw new ResourceNotFoundException("Couldn't stat:" + path);
        }
        else if (attrs.isDir())
        {
             return new SftpDir(this, vrl);
        }
        else if (attrs.isLink())
        {
             // resolve link: check attributes 
             SftpATTRS targetAttrs=null; 
            
             try
             {
                 targetAttrs=getSftpAttrs(path,true);
                
                 // Link Target Error: return as file 
                 if (targetAttrs!=null)
                 {
                     // return linked directory as VDir ! (not file)
                     if (targetAttrs.isDir())
                     {
                         return new SftpDir(this, vrl);
                     }
                 }
                 
             }
             catch (Exception e)
             {
                 Debug("??? Exception when resolving link:"+vrl+" Exception="+e); 
             }
        }
        
        // default: return as file
        return new SftpFile(this, vrl);
       
    }

    public void myDispose()
    {
        synchronized(serverMutex)
        {
          try 
          {
			disconnect();
          }
          catch (VlException e) 
          {
			// TODO Auto-generated catch block
			e.printStackTrace();
          }
          
          servers.remove(this);
          
        }
    }

    public String[] list(String path) throws VlException
    {
        Debug("listing:" + path);
        
        try
        {
            java.util.Vector<?> dirList;

            synchronized(serverMutex)
            {
            	try
            	{
                	checkState();
            		dirList = sftpChannel.ls(path);
            	}
            	catch (Exception e)
            	{
                	checkState();
            		dirList = sftpChannel.ls(path);
            	}

            }

            int index = 0;
            String childs[] = new String[dirList.size()];

            for (int i = 0; i < dirList.size(); i++)
            {
                Object entry = dirList.elementAt(i);

                if (entry instanceof com.jcraft.jsch.ChannelSftp.LsEntry)
                {
                    ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) entry;
                    childs[index] = lsEntry.getFilename();
                }
                else
                {
                    Global.errorPrintln(this,"ls() returned unknown entry[" + index + "]="
                            + entry.getClass().getCanonicalName());
                    childs[index] = null;
                }

                index++;
            }

            return childs;

        }
        catch (SftpException e)
        {
            throw convertException(e,"Could not list contents of remote path:"+path);
        }

    }

    private void checkState() throws VlException
    {
        try
        {
            synchronized(serverMutex)
            {
               if (this.session.isConnected() == false)
               {
                   Global.errorPrintln(this, "Session disconnected: reconnecting:"
                           + this);
                 
                   session = jschInstance.getSession(userInfo.getUsername(), getHostname(),
                           getPort());
                   session.setUserInfo(userInfo);
                   
                   session.connect();
                   
                   if (sftpChannel.isConnected()==true)
                       sftpChannel.disconnect();
                   
                   sftpChannel=null; 
               }
                       
               if  ((this.sftpChannel==null) || this.sftpChannel.isConnected()==false)
               {
                   Global.errorPrintln(this, "Channel closed: reconnecting:"
                           + this);
 
                   this.sftpChannel = (ChannelSftp) session.openChannel("sftp");
                   sftpChannel.connect();
                }
            }
        }
        /*catch (SftpException e1)
         {
         throw convertException(e1);  
         }*/
        catch (JSchException e)
        {
            throw convertException(e);
        }
    }

    public boolean existsPath(String path, boolean checkDir) throws VlException
    {
    	Debug("existsPath:"+path); 
    	
        try
        {
            synchronized(serverMutex)
            {
            	SftpATTRS attrs;
            	
            	try
            	{
            		checkState();
           			attrs = sftpChannel.lstat(path);
            	}
            	catch (Exception e)
            	{
            		checkState();
           			attrs = sftpChannel.lstat(path);
            	}

		         // sadly stat generates an exception when the path doesn't exists,
                 // so the following code will not be executed.
                if (attrs == null)
                {
                    return false;
                }
                else
                {
                	if (checkDir==true)
                	{
                		if (attrs.isDir()==true)
                		{
                			return true;
                		}
                		else
                		{
                			// exists but is NOT a directory ! 
                			return false; 
                		}
                	}
                	else
                	{
                		if (attrs.isDir()==false)
                		{
                			return true;
                		}
                		else
                		{
                			// exists but is NOT a file ! 
                			return false; 
                		}
                	}
                }
            } 
        }
        
        catch (Exception e)
        {
            if (e instanceof SftpException)
            {
               SftpException ex=(SftpException)e;
               
               //Global.messagePrintln(this,"after existsPath, session="+this.session.isConnected());
               //Global.messagePrintln(this,"after existsPath, channel="+this.sftpChannel.isConnected());
               
               // SftpException reason 2=no such file ! 
               if (ex.id==2) 
                   return false;
            }
            // other reason: 
            throw convertException(e,"Could not stat remote path:"+path); 
        }
    }

	public void uploadFile(VFSTransfer transfer,String localfilepath, String remotefilepath) throws VlException
    {
        try
        {
            
           synchronized(serverMutex)
           {
        	   checkState(); 
               // jCraft has a tranfer interface ! 
               SftpTransferMonitor monitor=new SftpTransferMonitor(transfer); 
              this.sftpChannel.put(localfilepath,remotefilepath,monitor);
           }
        }
        catch(Exception e)
        {
            throw convertException(e,"Error when uploading file to remote paths:"+remotefilepath);
        }
        
    }
    
    public void downloadFile(VFSTransfer transfer, String remotefilepath, String localfilepath) throws VlException
    {
        try
        {
           synchronized(serverMutex)
           {
               checkState(); 
               // jCraft has a tranfer interface ! 
               SftpTransferMonitor monitor=new SftpTransferMonitor(transfer); 
               this.sftpChannel.get(remotefilepath,localfilepath,monitor);
           }
        }
        catch(Exception e)
        {
            throw convertException(e,"Error when downloading file to local path:"+localfilepath);
        }
    }
    
    public VDir createDir(String dirpath, boolean ignoreExisting)
            throws VlException
    {
    	//	 check existing! 
        SftpATTRS attrs=null;
        
        //checkAndCreateParentDir(VRL.dirname(dirpath),force); 
        
        try
        {
        	// check existing! 
            attrs = this.getSftpAttrs(dirpath,ignoreExisting);
          
        }
        catch (Exception e)
        {
        	; // ignore  
        }

        // exists:  
        if (attrs!=null)
        {
           	if (attrs.isDir()==false)
        		throw new ResourceCreationFailedException("path already exists but is not a directory:"+this);
           	
        	if (ignoreExisting == false)
        		throw new ResourceAlreadyExistsException("path already exists (use ignoreExisting==true to overwrite) :"+this);
        	else
        		return (VDir) getPath(dirpath);
        }

        try
        {
            synchronized(serverMutex)
            {
                checkState();
                sftpChannel.mkdir(dirpath);
            }

            return (VDir) getPath(dirpath);
        }
        catch (SftpException e)
        {
            throw convertException(e,"Error when creating remote directory:"+dirpath);
        }
    }

    private void checkAndCreateParentDir(String dirname, boolean force) throws VlException
	{
		SftpATTRS parentAttrs=null; 
		
		try
        {
        	// check existing parent: 
            parentAttrs = this.getSftpAttrs(dirname,force);
        }
        catch (Exception e)
        {
        	; // ignore 
        }
        
        // create parent first 
        if (parentAttrs==null)
        	createDir(dirname,force); 
	}

	public VFile createFile(String filepath, boolean force) throws VlException
    {
        SftpATTRS attrs=null;
        
        // checkAndCreateParentDir(VRL.dirname(filepath),force); 
        
        try
        {
        	// stat:
             attrs= this.getSftpAttrs(filepath);
        }
        catch (Exception e)
        {
        	; // ignore; 
        }
        
        if (attrs!=null)
        {
            // exists:  
            if (force == false)
                throw new ResourceAlreadyExistsException("path already exists:"+this);
            
            if (attrs.isDir()==true)
                throw new ResourceAlreadyExistsException("path already exists but is a directory:"+this);
            
            if (attrs.isLink()==true)
                throw new ResourceAlreadyExistsException("path already exists but is a symbolic link:"+this);
             
            // delete existing:
            delete(filepath, false);
        }
        
        // create new: 
        // write null bytes
        byte nulbuf[] = new byte[0];

        OutputStream out;
        
        try
        {
        	synchronized(this.serverMutex)
        	{
        		checkState(); 

        		OutputStream output= this.sftpChannel.put(filepath,ChannelSftp.OVERWRITE);
            	output.write(nulbuf);
            	output.flush();
            	output.close();
        	}
        	
            VFSNode node = getPath(filepath);

            if (node instanceof VFile)
            {
                return (VFile) node;
            }
            else
            {
                throw new ResourceCreationFailedException(
                        "path exists, but is not a file:" + filepath);
            }
        }
        catch (Exception e) 
        {
			throw this.convertException(e,"Could not create file:"+filepath);
		}

    }
   
    public SftpATTRS getSftpAttrs(String filepath) throws VlException
    {
        return getSftpAttrs(filepath,false); 
    }

    public boolean delete(String path, boolean isDir) throws VlException
    {
        try
        {
            synchronized(serverMutex)
            {
                checkState(); 

                if (isDir == false)
                {
                    sftpChannel.rm(path);
                }
                else
                {
                    sftpChannel.rmdir(path);
                }
            }

            return (existsPath(path,isDir) == false);
        }
        catch (Exception e)
        {
            throw convertException(e);
        }
    }

    /*public int isOwner()
     {
     SftpATTRS attrs=getSftpAttrs(path);
     attrs.getPermissions();
     attrs.getUId(); 

     }*/

    public SftpATTRS getSftpAttrs(String path,boolean resolveLink) throws VlException
    {
    	Debug("getSftpAttrs:"+path); 
    	
        try 
        {
            synchronized(serverMutex)
            {
            	try
            	{
            		checkState();
                	
            		if (resolveLink==false)
            			return sftpChannel.lstat(path);
            		else
            			return sftpChannel.stat(path);
            	}
            	catch (Exception e)
            	{
            		checkState();
                	
            		if (resolveLink==false)
            			return sftpChannel.lstat(path);
            		else
            			return sftpChannel.stat(path);
            	}

            }
        }
        catch (Exception e)
        {
        	
        	//System.err.println("Exception when statting:"+path);
            throw convertException(e,"Could not stat remote path:"+path);
        }
    }

    private ChannelSftp createNewFTPChannel() throws VlException
	{
    	Debug("createNewChannel:"+this); 
    	
		try
		{
	    	ChannelSftp channel;
			channel = (ChannelSftp)session.openChannel("sftp");
	    	channel.connect();
	    	return channel; 

		}
		catch (JSchException e)
		{
			throw convertException(e,"Couldn't create new channel to:"+this); 
		}
	}


	public void setSftpAttrs(String path,boolean isDir, SftpATTRS attrs) throws VlException
    {
        try
        {
            synchronized(serverMutex)
            {
                checkState(); 
                sftpChannel.setStat(path, attrs);
            }
        }
        catch (SftpException e)
        {
            // find out what is wrong: 

            if (this.existsPath(path,isDir) == false)
            {
                // Error Handling: return better Exception
                throw new ResourceNotFoundException("Path doesn't exists:"
                        + path);
            }
            else
            {
                throw convertException(e);
            }
        }
    }

    

    public InputStream createInputStream(String path) throws VlException
    {
        try
        {
        	ChannelSftp newChannel=null; 
        	  
            synchronized(serverMutex)
            { 
            	checkState(); 
            	// old:
                // return this.sftpChannel.get(path);	
                // open up a new channel for multithreaded viewing ! 
                newChannel =(ChannelSftp) this.session.openChannel("sftp"); 
                
            }
            newChannel.connect(); 
            InputStream inps=newChannel.get(path);
            
            return new InputStreamWatcher(newChannel,inps); 

        }
        catch (Exception e)
        {
            throw convertException(e);
        }
        //return null; 
    }
    public int readBytes(String path, long fileOffset, byte[] buffer,
            int bufferOffset, int nrBytes) throws VlException
    {
        try
        {
        	
        	/*if (true)
        	{
        		ByteArrayOutputStream outpBuffer=new ByteArrayOutputStream(nrBytes);
        		
                synchronized(serverMutex)
                {        	 
                	sftpChannel.get(path, outpBuffer, null,nrBytes,fileOffset);  
                	byte[] sourcebuf = outpBuffer.toByteArray(); 
                    
                	System.arraycopy(sourcebuf,0,buffer,bufferOffset,nrBytes); 
                    
                    int numread=nrBytes; 
                    return numread;
                }
                
        	} else */
        	{
        		//
        		// Emulate Random Read by using StreamRead: 
        		// This method is also used in VFile.read(...) 
        		// 
        		
        		InputStream inps;
        		synchronized(serverMutex)
        		{  	      	 
        			checkState();
        			inps = sftpChannel.get(path);
        		}
            
        		//DONE BY SYNC READ: inps.skip(fileOffset);
        		int numread = StreamUtil.syncReadBytes(inps, fileOffset, buffer, bufferOffset,nrBytes);
        		inps.close();
        		
        		return numread;
        	}
        }
        catch (Exception e)
        {
            throw convertException(e);
        }

    }

    /**
     * TODO: Still buggy 
     */ 
    
    public void writeBytes(String path, long fileOffset, byte[] buffer,
            int bufferOffset, int nrBytes) throws VlException
    {
        OutputStream outps;

        //int mode=ChannelSftp.RESUME;
        int mode=ChannelSftp.OVERWRITE; 
        
        // must currently write in 32000 byte sized chunks
        int chunksize=32000; 
        
        // skip existing bytes: 
        if (fileOffset>0)
           mode=ChannelSftp.RESUME;
       
        try
        {
            synchronized(serverMutex)
            {
            	checkState();
            	
                // TODO: MODE !!! 
                outps = sftpChannel.put(path, (SftpProgressMonitor)null,mode,
                        fileOffset);

                // chunk write: 
                {
                   // write in chunks:
                   for (int i=0;i<nrBytes;i+=chunksize)
                   {
                      if (i+chunksize>nrBytes)
                          chunksize=nrBytes-i; 
                
                      outps.write(buffer,bufferOffset+i,chunksize);
                    
                   }
                }
                
                /** 
                {
                   // write all at once: BUGGY ! 
                   // outps.write(buffer,bufferOffset,nrBytes);    
                }*/
                outps.flush(); 
                outps.close(); 
            }
        }
        catch (Exception e)
        {
            throw convertException(e);
        }
    }
    public OutputStream createOutputStream(String path, boolean append) throws VlException
    { 
        try
        {
            synchronized(serverMutex)
            {
                checkState(); 

                // create private channel:
            	
                ChannelSftp outputChannel = (ChannelSftp)session.openChannel("sftp");
                outputChannel.connect();
                
                int mode = ChannelSftp.APPEND;
                if (append==false)
                    mode=ChannelSftp.OVERWRITE; 
                
                OutputStream outps = outputChannel.put(path,mode);
                return new OutputStreamWatcher(outps,outputChannel);
  
                // return this.sftpChannel.put(path,ChannelSftp.OVERWRITE);
            }
        }
        catch (Exception e)
        {
            throw convertException(e);
        }

    }
    public String rename(String path, String newName,
            boolean nameIsPath) throws VlException
    {
        
        String newPath = null;

        if (nameIsPath == false)
            newPath = VRL.dirname(path) + VRL.SEP_CHAR + newName;
        else
            newPath = newName;

        Global.infoPrintln(this,"rename:" + path + "->" + newPath);
        
        Debug("rename:" + path + "->" + newPath);

        try
        {
            synchronized(serverMutex)
            {
            	checkState(); 
            	
                sftpChannel.rename(path, newPath);
            }
        }
        catch (SftpException e)
        {
        	boolean destExists=false; 
        	try
        	{
        		if (sftpChannel.stat(newPath)!=null)
        			destExists=true; 
        	}
        	catch (Exception e2)
        	{
        		destExists=false ; //ignore 
        	}
        	
        	if (destExists==true)
    			throw new nl.uva.vlet.exception.ResourceAlreadyExistsException("Couldn't rename: destination already exists:"+newPath);
        	
            throw convertException(e);
        }

        return newPath;
    }
    
    // ======================================================================= 
    // Misc. 
    // =======================================================================

    private static void Debug(String msg)
    {
    	//Global.errorPrintln(SFTPServer.class,msg);
        Global.debugPrintln(SftpFileSystem.class,msg);
    }
    
    
    private static String getJschErrorString(int id)
    {
        // nr matches index in error list 
        if ((id >= 0) && (id < errorStringsList.length))
            return errorStringsList[id];

        return "Unknown Error";
    }

    /** Convert Exceptions to something more human readable */ 
    private VlException convertException(Exception e)
    {
    	return convertException(e,null); 
    }
    
    private VlException convertException(Exception e, String optMessage)
	{
    	if (e instanceof VlException)
    		return (VlException)e; // keep my own; 
    	
    	// extra message: 
    	String message="";
    	
    	if (optMessage!=null) 
    		message=optMessage+"\n";
    	
    	String emsg=e.getMessage();
		// Translate japanese enklish to Dutch Enklish
		if (emsg.startsWith("Auth fail"))
		{
			message+="Authentication failure\n"
				 + "---\n--- sftp message --- \n"+emsg;
			 return new nl.uva.vlet.exception.VlAuthenticationException(message,e); 
		}
		else if (e instanceof SftpException)
        {
           SftpException ex=(SftpException)e; 
          
           String reason = 
                  "Error   =" + ex.id + ":" + getJschErrorString(ex.id) + "\n"
                + "message =" + ex.getMessage() + "\n"
                + "Channel connected=" + sftpChannel.isConnected() + "\n"
                + "Session connected=" + session.isConnected() + "\n";
           
            //Global.messagePrintln(this,"sftp error="+getJschErrorString(ex.id));
           
           if (ex.id==1)
               return new VlException(message+"End of file error",reason,e); 
            
           if (ex.id==2) //  SftpException reason 2=no such file ! 
               return new ResourceNotFoundException(message+"StfpException:"+reason,e);
            
           if (ex.id==3)
               // don't know whether it is read or write
               return new VlException("AccessDenied",message+reason,e); 
            
            return new VlException("SFTP Exception",message+reason,e);  
        }
        else
        { 
            return new VlException("Exception",message+e.getMessage(),e); 
        }
    }
    // === 
    // Factory methods 
    // ===

    public static SftpFileSystem createFor(VRSContext context,ServerInfo info) throws VlException
    {
        SftpFileSystem server = null;

        int port = info.getPort();

        // update port 
        if (info.getPort() <= 0)
            port = VRS.DEFAULT_SSH_PORT;

        String serverID = createServerID(info.getHostname(), port, info
                .getUsername());

        synchronized (servers)
        {
            if ((server = servers.get(serverID)) == null)
            {
                Debug("Adding new SFTP Server:" + info.getID());

                //Global.messagePrint("SFTPServer", ">>> Adding new SFTP Server:"
                //        + info);

                server = new SftpFileSystem(context,info,info.getServerVRL());

                servers.put(server.getServerID(), server);
            }
            
            server.setFinalUserSubject(context); 
        }
        return server;
    }

    public static boolean existsServerFor(ServerInfo info)
    {
        if (info == null)
            return false; 

        return existsServerFor(info.getHostname(), info.getPort(), info
                .getUsername());
    }

    public static boolean existsServerFor(String host, int port2, String user)
    {
        String serverID = createServerID(host, port2, user);
        return (servers.get(serverID) != null);
    }

    public String toString()
    {
        return getServerID();
    }

    public String getServerID()
    {
        return this.serverID;
    }
    
    public boolean isWritable(String path) throws VlException
    {
        // check user writable: 
        SftpATTRS attrs=getSftpAttrs(path);
        int mod=attrs.getPermissions();
        
        return ((mod & 0200)>0); 
    }

    public boolean isReadable(String path) throws VlException
    {
        // check user readable: 
        SftpATTRS attrs=getSftpAttrs(path);
        int mod=attrs.getPermissions();
        
        return ((mod & 0400)>0); 
         
    }
    
    public boolean isLink(String path) throws VlException
    {
        SftpATTRS attrs=getSftpAttrs(path);
       
        boolean val = attrs.isLink();
        if (val)
        { 
            String strs[]=attrs.getExtended(); 
            if (strs!=null) 
            {
                int i=0; 
                for (String str:strs)
                {
                    System.err.println(""+(i++)+":"+str); 
                }
            }
            
        }
        return val;
    }

    /** Returns/home/<username> */
    public String getHomeDir()
    {
    	return defaultHome;    
    }

    public VAttribute[][] getACL(String path, boolean isDir) throws VlException
    {
        SftpATTRS attrs = getSftpAttrs(path);
        // sftp support unix styl file permissions
        int mode = attrs.getPermissions();
        return VFS.convertFileMode2ACL(mode, isDir);

    }

    public void setACL(String path, VAttribute[][] acl, boolean isDir)
            throws VlException
    {
        int mode = VFS.convertACL2FileMode(acl, isDir);

        if (mode < 0)
            throw new VlException("Error converting ACL list");

        setPermissions(path, isDir,mode);
    }

    private void setPermissions(String path,boolean isDir,int mode) throws VlException
    {
        //SftpATTRS attrs=new SftpATTRS(); 
        SftpATTRS attrs = getSftpAttrs(path);
        attrs.setPERMISSIONS(mode);
       
        setSftpAttrs(path, isDir,attrs);
    }
    
   
    public int getOwnerID(String path) throws VlException
    {
        //SftpATTRS attrs=new SftpATTRS(); 
        SftpATTRS attrs = getSftpAttrs(path);
        return attrs.getUId(); 
    }
    
   
    public int getGroupID(String path) throws VlException
    {
        //SftpATTRS attrs=new SftpATTRS(); 
        SftpATTRS attrs = getSftpAttrs(path);
        return attrs.getGId(); 
    }
    
    SftpATTRS attrs=null;
        
    
    public VAttribute getAttribute(VFSNode node,SftpATTRS attrs, String name, boolean isDir, boolean update) throws VlException
    {
        Debug("getAttribute for:"+node.getPath()+":"+name); 
        
        //Optimization: only update if a SftpAttribute AND an update is requested: 
        if (name==null) 
            return null;
        
        // initialize attributes if not yet fetched! 
        if (attrs==null)
        {
            attrs=this.getSftpAttrs(node.getPath()); 
        }
        
        // get attributes from same holder:
      
        if (name.compareTo(ATTR_MODIFICATION_TIME)==0)
        {
            return VAttribute.createDateSinceEpoch(name,getModificationTime(attrs));
        }
        else if (name.compareTo(ATTR_ACCESS_TIME)==0)
        {
            return new VAttribute(VAttributeType.TIME,name,getAccessTime(attrs));
        }
        else if (name.compareTo(ATTR_LENGTH)==0)
        {
            return new VAttribute(name,attrs.getSize());
        }
        else if (name.compareTo(ATTR_UID)==0)
        {
            return new VAttribute(name,attrs.getUId());
        }
        else if (name.compareTo(ATTR_GID)==0)
        {
            return new VAttribute(name,attrs.getGId());
        }
        else if (name.compareTo(ATTR_UNIX_FILE_MODE)==0)
        {
            // note sftp attributes return higher value the (8)07777 (isdir and islink)
            return new VAttribute(name,"0"+Integer.toOctalString(attrs.getPermissions()%07777));
        }
        
        return null; 
    }

    
    public VAttribute[] getAttributes(VFSNode node,SftpATTRS holder,String[] names, boolean isDir) throws VlException
    {
        if (names==null) 
            return null;
        
        VAttribute attrs[]=new VAttribute[names.length];
        
        for (int i=0;i<names.length;i++)
        {
            attrs[i]=getAttribute(node,holder,names[i],isDir,(i==0)); 
        }
        
        return attrs; 
            
    }
    
    public String getPermissionsString(SftpATTRS attrs, boolean isDir)
    throws VlException  
    {
    	return attrs.getPermissionsString();
    }
    
    public int getUnixMode(SftpATTRS attrs, boolean isDir)
    throws VlException  
    {
    	return attrs.getPermissions(); 
    }
    
    public int getPermissions(SftpATTRS attrs)
        throws VlException  
    {
        return attrs.getPermissions();
    }

    public long getModificationTime(SftpATTRS attrs)
    {
        return attrs.getMTime()*1000L; 
    }
    
    public long getAccessTime(SftpATTRS attrs)
    {
        return attrs.getATime()*1000L; 
    } 

    
    public long getLength(SftpATTRS attrs)
    {
        return attrs.getSize(); 
    }

    //For the new clear() method:
    public static void clearServers()
    {
        for (Enumeration<String> keys = servers.keys();keys.hasMoreElements();) 
        {
            String key=(String)keys.nextElement(); 
            SftpFileSystem server = servers.get(key); 
            try 
            {
				server.disconnect();
			}
            catch (VlException e) 
            {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        }
        
        servers.clear();
    }

    // =======================================================================
    // VServer interface
    // =======================================================================
    
	public void connect() throws VlException 
	{
        this.jschInstance = new JSch();
        ServerInfo info=this.getServerInfo();
        String sshdir=Global.getUserHome()+"/.ssh";
        String idFile=sshdir+"/"+getSSHIndentity();// check multiple files ? 
        try
        {
            if (new java.io.File(idFile).exists()==false)
                idFile=null; 
            
        	jschInstance.setKnownHosts(sshdir+"/known_hosts");
        	if (idFile!=null) 
        	    jschInstance.addIdentity(idFile); 
        	
        	jschInstance.setKnownHosts(getKnowHostsFile());
        }
        catch (Exception e)
        {
        	Global.errorPrintln(this,"Error initializing jCraft SSH:"+e);
        	Global.errorPrintStacktrace(e); 
        }
        
        // System.out.println("s:" + server + " p:" + port + " u:" + user);
        try
        {
        	int port=getPort(); 
        	if (port<=0) 
        		port=VRS.DEFAULT_SSH_PORT; 
        	
            this.session = jschInstance.getSession(userInfo.getUsername(), getHostname(),port);

            // ssh.setSocketTimeout(timeout);

            session.setUserInfo(userInfo);
            session.connect();

          
            //channel.setExtOutputStream(new OutputLog());
            //channel.setOutputStream(new OutputLog());
            this.sftpChannel = createNewFTPChannel(); 
            
            Debug("Connected to:" + getHostname() + ":" + getPort());
         
            // valid authentication 
            info.setHasValidAuthentication(true);
            info.store(); // update in registry ! 
            try
            {
            	defaultHome=this.sftpChannel.pwd();
            }
            catch (SftpException e)
    		{
            	// can not get pwd() ? 
    	         throw convertException(e);
    		}
            Debug("defaultHome="+defaultHome); 
        }
        catch (JSchException e)
        {
            info.setHasValidAuthentication(false); // invalidize authentication info !
            info.store(); // Update in registry !
            throw convertException(e);
        }
		
        
        try
        {
      	   HostKeyRepository hkr=jschInstance.getHostKeyRepository();
       	   HostKey hk=session.getHostKey();
        }
        catch (Exception e)
        {
           Global.errorPrintln(this,"Error initializing jCraft SSH:"+e);
      	   Global.errorPrintStacktrace(e); 
        }
	}

	private String getSSHIndentity()
	{	
		String idStr=getServerInfo().getStringProperty(ServerInfo.ATTR_SSH_IDENTITY); 
		if (idStr!=null)
			return idStr; 
		return "id_rsa"; 
	}


	public void disconnect() throws VlException 
	{
       synchronized(serverMutex)
       {
          this.sftpChannel.disconnect();
          this.session.disconnect();
         
          this.jschInstance=null; 
          this.session=null;
          this.sftpChannel=null;
       }
	}


	public String getID() 
	{
		return this.serverID; 
	}


	public String getScheme()
	{
		return  VFS.SFTP_SCHEME;
	}


	public boolean isConnected() 
	{
		if (this.sftpChannel!=null) 
			return this.sftpChannel.isConnected(); 
		
		return false; 
	}
	
	private String getKnowHostsFile()
	{
		ServerInfo info=this.getServerInfo(); 
		   
		VAttribute attr=info.getAttribute(SftpFSFactory.ATTR_KNOWN_HOSTS_FILE);
		
		if (attr==null) 
			return getDefaultKnownHostsFile();
		else
			return attr.getValue();
	}
	
	public String getSSHConfigDir()
	{
		ServerInfo info=this.getServerInfo(); 
		   
		VAttribute attr=info.getAttribute(SftpFSFactory.ATTR_SSH_CONFIG_DIR);
		
		if (attr==null) 
			return getDefaultSSHDir();
		else
			return attr.getValue();
	}
	
	public boolean getAllowUserInterAction()
	{
		return this.vrsContext.getConfigManager().getAllowUserInteraction();  
	}
	
	public String getDefaultKnownHostsFile()
	{
		return getDefaultSSHDir()+"/known_hosts"; 
	}


	public String getDefaultSSHDir()
	{
		return vrsContext.getLocalUserHome()+VRL.SEP_CHAR_STR+".ssh"; 
	}

	private void setFinalUserSubject(VRSContext newContext) throws VlAuthenticationException
	{
		String newSubject=newContext.getGridProxy().getSubject();
		
		if (this.userSubject!=null)
		{
			if (this.userSubject.compareTo(newSubject)!=0)
			{
				// OOPSY!! 
				throw new nl.uva.vlet.exception.VlAuthenticationException("Illegal User. Current server is not authenticated for new user:"+newSubject); 
			}
		}
		else
			// May Be Set Only Once !!! 
			this.userSubject=newSubject;
	}

	public String getUserSubject()
	{
		return this.userSubject; 
	}
		
	public String getLinkTarget(String path)
	{
		return null;
	}


	@Override
	public VDir newDir(VRL path) throws VlException 
	{
		return new SftpDir(this,path);  
	}

	@Override
	public VFile newFile(VRL path) throws VlException 
	{
		return new SftpFile(this,path); 
	}
	
    /** 
     * Create localPort which connected to remoteHost:remotePort 
     * @throws VlException 
     */ 
    public void createOutgoingTunnel(int localPort,String remoteHost,int remotePort) throws VlException
    {
        try
        {
            String[] list = this.session.getPortForwardingL();
            String optStr=""+localPort+":"+remoteHost.toLowerCase()+":"+remotePort; 
            for (String s:list)
            {
                if (StringUtil.equalsIgnoreCase(optStr,s))
                {
                    Global.warnPrintln(this,"Outgoing port forwarding already exists:"+s); 
                    return; 
                }
            }
            
            //jsch doesn it all: 
            this.session.setPortForwardingL(localPort,remoteHost,remotePort);
            Global.errorPrintln(this,"New SSH tunnel="+optStr); 
            
        }
        catch (JSchException e)
        {
            throw this.convertException(e,"Couldn't create remote port forwarding:"+localPort+":"+remoteHost+":"+remotePort); 
        } 
    }
    
    /** Create localPort which connected to remoteHost:remotePort 
     * @throws VlException */ 
    public void createIncomingTunnel(int remotePort,String localHost,int localPort) throws VlException
    {
        try
        {
            //jsch doesn it all: 
            this.session.setPortForwardingR(remotePort,localHost,localPort);
        }
        catch (JSchException e)
        {
            throw this.convertException(e,"Couldn't create incoming port forwarding:"+remotePort+":"+localHost+":"+localPort); 
        } 
        
    }
     
    
    /** Creates a new outgoing SSH tunnel and return the local tunnel port 
     * @throws VlException */ 
    public int createOutgoingTunnel(String bdiiHost, int bdiiPort) throws VlException
    {
        VlException lastEx=null;  
        
        for (int i=0;i<32;i++)
        {
            int lport=getFreeLocalPort();
            
            try
            {
                this.createOutgoingTunnel(lport,bdiiHost, bdiiPort);
                usedLocalPorts.add(new Integer(lport)); 
                return lport; 
            }
            catch (VlException e)
            {
                lastEx=e; 
                Global.errorPrintln(this,"Failed to create new local tunnel port:"+lport+"\n"+e); 
            }
        }
        
        if (lastEx!=null)
            throw lastEx;
        
        throw new VlException("Failed to create new local tunnel port");
    }
    
    public static int getFreeLocalPort()
    {
        // use randomizer for now: (is much faster!) 
        int offset=10000; 
        int max=65535;
        int port=0;
        
        for (int i=0;i<100;i++)
        {
            port=offset+portRandomizer.nextInt(max-offset);
            // Integer(A) equals Integer(B) when (int)A==(int)B.  
            if (usedLocalPorts.contains(new Integer(port))==false)
                return port; 
        }
        
        return -1; 
    }

    public SSHChannel createShellChannel(VRL optLocation) throws VlException
    {
        SSHChannelOptions options = new SSHChannelOptions();
         
        
        SSHChannel sshChannel = new SSHChannel(vrsContext,
                getUsername(),
                getHostname(),
                getPort(), 
                options);
        
        String path=optLocation.getPath();

        try
        {
            // connect but re-use current session and options;
            sshChannel.connectTo(session);
            sshChannel.setCWD(path); 
            return sshChannel;
        }
        catch (IOException e)
        {
            throw new VlException(e); 
        }
    }

 
}
