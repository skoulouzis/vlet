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

import nl.uva.vlet.ClassLogger;
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
import nl.uva.vlet.vfs.VFSClient;
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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

/**
 * SFTP FileSystem implementation. The jsch session can provide a FTP channel
 * and a command line channel. Also tunneling is supported by the API.
 */
public class SftpFileSystem extends FileSystemNode implements VOutgoingTunnelCreator, VShellChannelCreator
{
    // === class stuff
    private static ClassLogger logger;
    
//    /** VRSContext mapped JCraftClients */ 
//    private static Map<String,JCraftClient> jcraftClients=new Hashtable<String,JCraftClient>();

    static
    {
        logger = ClassLogger.getLogger(SftpFileSystem.class);
        JCraftClient.getLogger().setParent(logger); 
        //logger.setLevelToDebug();
    }
    
    ClassLogger getLogger()
    {
        return logger; 
    }
    
    static String createServerID(String host, int port, String user)
    {
        // must use default port in ServerID !
        if (port <= 0)
            port = VRS.DEFAULT_SSH_PORT;

        return "serverid:ssh:"+user + "@" + host + ":" + port;
    }

    public class VLUserInfo implements UserInfo
    {
        public boolean isProxy=false;
        
        public VLUserInfo()
        {
            super();
        }

        public VLUserInfo(boolean isProxy)
        {
            super();
            this.isProxy=isProxy;
        }

        public String getUsername()
        {
            if (isProxy)
                return getProxyUser(); 
                
            return SftpFileSystem.this.getUsername();
        }

        public String getPassphrase()
        {
            if (isProxy)
                return getServerInfo().getPassphrase("PROXY");
            else
                return getServerInfo().getPassphrase();
        }

        public String getPassword()
        {
            if (isProxy)
                return getServerInfo().getPassword("PROXY");
            else        
                return getServerInfo().getPassword();
        }

        public String getUserHostIDString()
        {
            String str;
            
            if (isProxy==false)
            {
                str=getUsername()+"@"+getHostname()+":"+getPort(); 
            }
            else
            {
                str=getProxyUser()+"@"+getProxyHost()+":"+getProxyPort(); 
            }
            return str; 
        }
             
        public boolean promptPassword(String message)
        {
            if (getAllowUserInterAction() == false)
            {
                // use store password:
                if (StringUtil.isEmpty(getPassword()) == false)
                    return true;
                return false;
            }

            logger.debugPrintf("promptPassword(old):%s\n",message); 
            // jSch doesn't provide username in message !
            message = "Password needed for:"+this.getUserHostIDString()+"\n"; //+ message;
            logger.debugPrintf("promptPassword(new):%s\n",message); 
            // getVRSContext().getConfigManager().getHasUI();
            String field = uiPromptPassfield(message);
            
            if (field != null)
            {
                if (isProxy==false)
                    getServerInfo().setPassword(field);
                else
                    getServerInfo().setPassword("PROXY",field); 
                
                field = null;
                return true;
            }

            return false;
        }
        
        public boolean promptPassphrase(String message)
        {
            if (getAllowUserInterAction() == false)
            {
                // use store password:
                if (StringUtil.isEmpty(getPassphrase()) == false)
                    return true;
                return false;
            }
            
            logger.debugPrintf("promptPassphrase(old):%s\n",message); 
            // jSch doesn't provide username in message !
            message = "Passphrase needed for:"+this.getUserHostIDString()+"\n"; // + message;
            logger.debugPrintf("promptPassphrase(new):%s\n",message); 
            // getVRSContext().getConfigManager().getHasUI();
            String field = uiPromptPassfield(message);

            if (field != null)
            {
                getServerInfo().setPassphrase(field);
                field = null;
                return true;
            }

            return false;
        }

        public String uiPromptPassfield(String message)
        {
            StringHolder secretHolder = new StringHolder(null);
          
            boolean result = getVRSContext().getUI().askAuthentication(message, secretHolder);

            if (result == true)
                return secretHolder.value;
            else
                return null;
        }

        public void showMessage(String message)
        {
            if (getAllowUserInterAction() == false)
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
            if (getAllowUserInterAction() == false)
            {
                return getDefaultYesNoAnswer(message);
            }
            else
            {
                return getVRSContext().getUI().askYesNo("Yes or No?", message, false);
            }
        }
    }

//    public static JCraftClient getJCraftClient(VRSContext vrsContext) throws JSchException
//    {
//        synchronized(jcraftClients)
//        {
//            String id=""+vrsContext.getID();
//            JCraftClient jcrft=jcraftClients.get(id);
//            if (jcrft==null)
//            {
//                logger.infoPrintf(">>> New JCraftClient() for VRSContext:#%s\n",id);
//                jcrft=new JCraftClient();
//                jcraftClients.put(id, jcrft); 
//            }
//            else
//            {
//                logger.infoPrintf(">>> Returning cached JCraftClient for VRSContext:#%s\n",id);
//            }
//            return jcrft; 
//        }
//    }
    
    // =======================================================================
    // Instance
    // =======================================================================

    private JCraftClient jcraftClient;

    private ChannelSftp sftpChannel;

    private VLUserInfo proxyUserInfo;
    private Session proxySession;

    private VLUserInfo userInfo;
    private Session session;

    final Boolean serverMutex = new Boolean(true);

    private String defaultHome = "/";

    private String userSubject;

    // =======================================================================
    // Server Critical
    // =======================================================================

    
    private void init(ServerInfo info) throws VlException
    {
        logger.debugPrintf(">>> init for: %s@%s:%d\n", info.getUsername(), getHostname(), getPort());

        this.userInfo = new VLUserInfo();
        this.serverID = createServerID(getHostname(), getPort(), info.getUsername());

        try
        {
            JCraftClient.SSHConfig sshConf=new JCraftClient.SSHConfig(); 
            sshConf.sshKnownHostsFile=this.getKnownHostsFile(); 
            jcraftClient = new JCraftClient(sshConf); // vrsContext);

        }
        catch (JSchException e)
        {
            throw convertException(e, "Failed to set SSH Configuration");
        }
        
        try
        {
            // now set ids: 
            String ids[] = getSSHIdentities();
            VFSClient vfs = new VFSClient(vrsContext);  
            VDir home=vfs.getUserHome(); 
            String idPaths[]=new String[ids.length]; 
            
            for (int i=0;i<ids.length;i++)
            {
                String idFile=home.resolvePath(getSSHConfigDir()+"/"+ids[i]);
                idPaths[i]=idFile;
            }
            jcraftClient.setSSHIdentityFiles(idPaths);  
            
        }
        catch (JSchException e)
        {
            throw convertException(e, "Failed to set SSH identities.");
        }

        this.connect();
    }

    public SftpFileSystem(VRSContext context, ServerInfo info, VRL location) throws VlException
    {
        super(context, info);
        init(info);
        logger.debugPrintf("new SftpFileSystem():%s\n", this);
    }

    public String getUsername()
    {
        return getServerInfo().getUsername();
    }

    
    public JCraftClient getJCraftClient()
    {
        return this.jcraftClient; 
    }
//    public String getHostname()
//    {
//        return getServerInfo().getHostname();
//    }
//    
//    public int getPort()
//    {
//        return getServerInfo().getPort();
//    }
    
    // =======================================================================
    // Authentication
    // =======================================================================

    public boolean getDefaultYesNoAnswer(String optMessage)
    {
        return this.getServerInfo().getBoolProperty(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER, false);
    }

    public void setDefaultYesNoAnswer(boolean value)
    {
        ServerInfo info = getServerInfo();
        info.setAttribute(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER, value);
        info.store();
    }

    // =======================================================================
    // === synchronized methods ===
    // =======================================================================
    public VFSNode openLocation(VRL vrl) throws VlException
    {
        // No Exceptions: store ServerInfo !

        String path = vrl.getPath();

        if ((path == null) || (path.compareTo("") == 0))
            path = getHomeDir();

        // ~/ home expansion
        if (path.startsWith("~"))
            path = getHomeDir() + path.substring(1);

        if (path.startsWith("/~"))
            path = getHomeDir() + path.substring(2);

        return getPath(path);
    }

    public VFSNode getPath(String path) throws VlException
    {
        String user = getUsername();
        int port = getPort();
        String host = getHostname();

        // '~' expansion -> default home

        if (path.startsWith("~"))
            path = defaultHome + "/" + path.substring(1, path.length());
        else if (path.startsWith("/~"))
            path = defaultHome + "/" + path.substring(2, path.length());
        //
        // hide default port from location
        // IMPORTANT: must match accountID in ServerInfo !
        //

        if (port == VRS.DEFAULT_SSH_PORT)
            port = 0;

        VRL vrl = new VRL(VRS.SFTP_SCHEME, user, host, port, path);

        logger.debugPrintf("getPath():%s\n", path);

        SftpATTRS attrs = null;

        try
        {
            synchronized (serverMutex)
            {
                checkState();

                // Must throw the right Exception:

                /*
                 * // cd to dirname to speed up stat String
                 * pwd=sftpChannel.pwd(); String dirname=VRL.dirname(path);
                 * 
                 * if (pwd.compareTo(dirname)!=0) { Debug("cd to:"+dirname);
                 * sftpChannel.cd(dirname); }
                 * 
                 * attrs = sftpChannel.lstat(VRL.basename(path));
                 * 
                 * //attrs = sftpChannel.stat(VRL.basename(path));
                 */
                // do not resolve (possivle errornous) link:
                attrs = this.getSftpAttrs(path, false);
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
            SftpATTRS targetAttrs = null;

            try
            {
                targetAttrs = getSftpAttrs(path, true);

                // Link Target Error: return as file
                if (targetAttrs != null)
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
                logger.logException(ClassLogger.WARN, e, "Exception when resolving link:%s\n", vrl);
            }
        }
        // default: return as file
        return new SftpFile(this, vrl);
    }

    public void myDispose()
    {
        synchronized (serverMutex)
        {
            try
            {
                disconnect();
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.INFO, e, "Exception during disconnect().\n");
            }

        }
    }

    public VRL getServerVRL()
    {
        return new VRL(VRS.SFTP_SCHEME, null, this.getHostname(), getPort(), null);
    }

    
    public String[] list(String path) throws VlException
    {
        logger.debugPrintf("listing:%s\n", path);

        try
        {
            java.util.Vector<?> dirList;

            synchronized (serverMutex)
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
                    logger.warnPrintf("ls() returned unknown entry[%d]=%s\n", index, entry.getClass()
                            .getCanonicalName());
                    childs[index] = null;
                }

                index++;
            }

            return childs;
        }
        catch (SftpException e)
        {
            throw convertException(e, "Could not list contents of remote path:" + path);
        }
    }

    public boolean existsPath(String path, boolean checkDir) throws VlException
    {
        try
        {
            synchronized (serverMutex)
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

                // sadly stat generates an exception when the path doesn't
                // exists,
                // so the following code will not be executed.
                if (attrs == null)
                {
                    return false;
                }
                else
                {
                    if (checkDir == true)
                    {
                        if (attrs.isDir() == true)
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
                        if (attrs.isDir() == false)
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
            } // synchronized
        }
        catch (Exception e)
        {
            if (e instanceof SftpException)
            {
                SftpException ex = (SftpException) e;

                // logger.messagePrintln(this,"after existsPath, session="+this.session.isConnected());
                // logger.messagePrintln(this,"after existsPath, channel="+this.sftpChannel.isConnected());

                // SftpException reason 2=no such file !
                if (ex.id == 2)
                    return false;
            }
            // other reason:
            throw convertException(e, "Could not stat remote path:" + path);
        }
    }

    public void uploadFile(VFSTransfer transfer, String localfilepath, String remotefilepath) throws VlException
    {
        try
        {
            synchronized (serverMutex)
            {
                checkState();
                // jCraft has a tranfer interface !
                SftpTransferMonitor monitor = new SftpTransferMonitor(transfer);
                this.sftpChannel.put(localfilepath, remotefilepath, monitor);
            }
        }
        catch (Exception e)
        {
            throw convertException(e, "Error when uploading file to remote paths:" + remotefilepath);
        }

    }

    public void downloadFile(VFSTransfer transfer, String remotefilepath, String localfilepath) throws VlException
    {
        try
        {
            synchronized (serverMutex)
            {
                checkState();
                // jCraft has a tranfer interface !
                SftpTransferMonitor monitor = new SftpTransferMonitor(transfer);
                this.sftpChannel.get(remotefilepath, localfilepath, monitor);
            }
        }
        catch (Exception e)
        {
            throw convertException(e, "Error when downloading file to local path:" + localfilepath);
        }
    }

    public VDir createDir(String dirpath, boolean ignoreExisting) throws VlException
    {
        // check existing!
        SftpATTRS attrs = null;

        // checkAndCreateParentDir(VRL.dirname(dirpath),force);

        try
        {
            // check existing!
            attrs = this.getSftpAttrs(dirpath, ignoreExisting);

        }
        catch (Exception e)
        {
            // ok.
            logger.logException(ClassLogger.DEBUG, e, "No attributes for (direectory doesn't exists):%s\n", dirpath);
        }

        // exists:
        if (attrs != null)
        {
            if (attrs.isDir() == false)
                throw new ResourceCreationFailedException("path already exists but is not a directory:" + this);

            if (ignoreExisting == false)
                throw new ResourceAlreadyExistsException(
                        "path already exists (use ignoreExisting==true to overwrite) :" + this);
            else
                return (VDir) getPath(dirpath);
        }

        try
        {
            synchronized (serverMutex)
            {
                checkState();
                sftpChannel.mkdir(dirpath);
            }

            return (VDir) getPath(dirpath);
        }
        catch (SftpException e)
        {
            throw convertException(e, "Error when creating remote directory:" + dirpath);
        }
    }

    public VFile createFile(String filepath, boolean force) throws VlException
    {
        SftpATTRS attrs = null;
        // checkAndCreateParentDir(VRL.dirname(filepath),force);

        try
        {
            // stat:
            attrs = this.getSftpAttrs(filepath);
        }
        catch (Exception e)
        {
            ; // ignore;
        }

        if (attrs != null)
        {
            // exists:
            if (force == false)
                throw new ResourceAlreadyExistsException("path already exists:" + this);

            if (attrs.isDir() == true)
                throw new ResourceAlreadyExistsException("path already exists but is a directory:" + this);

            if (attrs.isLink() == true)
                throw new ResourceAlreadyExistsException("path already exists but is a symbolic link:" + this);

            // delete existing:
            delete(filepath, false);
        }

        // create new:
        // write null bytes
        byte nulbuf[] = new byte[0];

        try
        {
            OutputStream output;

            synchronized (this.serverMutex)
            {
                checkState();

                output = this.sftpChannel.put(filepath, ChannelSftp.OVERWRITE);
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
                throw new ResourceCreationFailedException("path exists, but is not a file:" + filepath);
            }
        }
        catch (Exception e)
        {
            throw this.convertException(e, "Could not create file:" + filepath);
        }
    }

    public SftpATTRS getSftpAttrs(String filepath) throws VlException
    {
        return getSftpAttrs(filepath, false);
    }

    public boolean delete(String path, boolean isDir) throws VlException
    {
        try
        {
            synchronized (serverMutex)
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

            return (existsPath(path, isDir) == false);
        }
        catch (Exception e)
        {
            throw convertException(e);
        }
    }

    public SftpATTRS getSftpAttrs(String path, boolean resolveLink) throws VlException
    {
        try
        {
            synchronized (serverMutex)
            {
                try
                {
                    checkState();

                    if (resolveLink == false)
                        return sftpChannel.lstat(path);
                    else
                        return sftpChannel.stat(path);
                }
                catch (Exception e)
                {
                    checkState();

                    if (resolveLink == false)
                        return sftpChannel.lstat(path);
                    else
                        return sftpChannel.stat(path);
                }
            }
        }
        catch (Exception e)
        {
            // System.err.println("Exception when statting:"+path);
            throw convertException(e, "Could not stat remote path:" + path);
        }
    }

    private ChannelSftp createNewFTPChannel() throws VlException
    {
        try
        {
            ChannelSftp channel;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            return channel;

        }
        catch (JSchException e)
        {
            throw convertException(e, "Couldn't create new channel to:" + this);
        }
    }

    public void setSftpAttrs(String path, boolean isDir, SftpATTRS attrs) throws VlException
    {
        try
        {
            synchronized (serverMutex)
            {
                checkState();
                sftpChannel.setStat(path, attrs);
            }
        }
        catch (SftpException e)
        {
            // find out what is wrong:

            if (this.existsPath(path, isDir) == false)
            {
                // Error Handling: return better Exception
                throw new ResourceNotFoundException("Path doesn't exists:" + path);
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
            ChannelSftp newChannel = null;

            synchronized (serverMutex)
            {
                checkState();
                // old:
                // return this.sftpChannel.get(path);
                // open up a new channel for multithreaded viewing !
                newChannel = (ChannelSftp) this.session.openChannel("sftp");

            }
            newChannel.connect();
            InputStream inps = newChannel.get(path);

            return new InputStreamWatcher(newChannel, inps);

        }
        catch (Exception e)
        {
            throw convertException(e);
        }
        // return null;
    }

    public int readBytes(String path, long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws VlException
    {
        try
        {

            /*
             * if (true) { ByteArrayOutputStream outpBuffer=new
             * ByteArrayOutputStream(nrBytes);
             * 
             * synchronized(serverMutex) { sftpChannel.get(path, outpBuffer,
             * null,nrBytes,fileOffset); byte[] sourcebuf =
             * outpBuffer.toByteArray();
             * 
             * System.arraycopy(sourcebuf,0,buffer,bufferOffset,nrBytes);
             * 
             * int numread=nrBytes; return numread; }
             * 
             * } else
             */
            {
                //
                // Emulate Random Read by using StreamRead:
                // This method is also used in VFile.read(...)
                //

                InputStream inps;
                synchronized (serverMutex)
                {
                    checkState();
                    inps = sftpChannel.get(path);
                }

                // DONE BY SYNC READ: inps.skip(fileOffset);
                int numread = StreamUtil.syncReadBytes(inps, fileOffset, buffer, bufferOffset, nrBytes);
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

    public void writeBytes(String path, long fileOffset, byte[] buffer, int bufferOffset, int nrBytes)
            throws VlException
    {
        OutputStream outps;

        // int mode=ChannelSftp.RESUME;
        int mode = ChannelSftp.OVERWRITE;

        // must currently write in 32000 byte sized chunks
        int chunksize = 32000;

        // skip existing bytes:
        if (fileOffset > 0)
            mode = ChannelSftp.RESUME;

        try
        {
            synchronized (serverMutex)
            {
                checkState();

                // TODO: MODE !!!
                outps = sftpChannel.put(path, (SftpProgressMonitor) null, mode, fileOffset);

                // chunk write:
                {
                    // write in chunks:
                    for (int i = 0; i < nrBytes; i += chunksize)
                    {
                        if (i + chunksize > nrBytes)
                            chunksize = nrBytes - i;

                        outps.write(buffer, bufferOffset + i, chunksize);

                    }
                }

                /**
                 * { // write all at once: BUGGY ! //
                 * outps.write(buffer,bufferOffset,nrBytes); }
                 */
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
            synchronized (serverMutex)
            {
                checkState();

                // create private channel:

                ChannelSftp outputChannel = (ChannelSftp) session.openChannel("sftp");
                outputChannel.connect();

                int mode = ChannelSftp.APPEND;
                if (append == false)
                    mode = ChannelSftp.OVERWRITE;

                OutputStream outps = outputChannel.put(path, mode);
                return new OutputStreamWatcher(outps, outputChannel);

                // return this.sftpChannel.put(path,ChannelSftp.OVERWRITE);
            }
        }
        catch (Exception e)
        {
            throw convertException(e);
        }

    }

    public String rename(String path, String newName, boolean nameIsPath) throws VlException
    {

        String newPath = null;

        if (nameIsPath == false)
            newPath = VRL.dirname(path) + VRL.SEP_CHAR + newName;
        else
            newPath = newName;

        logger.infoPrintf("rename:'%s' -> '%s'\n", path, newPath);

        try
        {
            synchronized (serverMutex)
            {
                checkState();

                sftpChannel.rename(path, newPath);
            }
        }
        catch (SftpException e)
        {
            boolean destExists = false;
            try
            {
                if (sftpChannel.stat(newPath) != null)
                    destExists = true;
            }
            catch (Exception e2)
            {
                destExists = false; // ignore
            }

            if (destExists == true)
                throw new nl.uva.vlet.exception.ResourceAlreadyExistsException(
                        "Couldn't rename: destination already exists:" + newPath);

            throw convertException(e);
        }

        return newPath;
    }

    // =======================================================================
    // Misc.
    // =======================================================================

    /** Convert Exceptions to something more human readable */
    private VlException convertException(Exception e)
    {
        return convertException(e, null);
    }

    private VlException convertException(Exception e, String optMessage)
    {
        if (e instanceof VlException)
            return (VlException) e; // keep my own;

        // extra message:
        String message = "";

        if (optMessage != null)
            message = optMessage + "\n";

        String emsg = e.getMessage();
        // Make messages more descriptive. 
        if (emsg.startsWith("Auth fail"))
        {
            message += "Authentication failure\n" + "\n("+emsg+")\n";
            return new nl.uva.vlet.exception.VlAuthenticationException(message, e);
        }
        else if (emsg.startsWith("Auth cancel"))
        {
            message += "Authentication cancelled\n" + "\n("+emsg+")\n";
            return new nl.uva.vlet.exception.VlAuthenticationException(message, e);
        }
        else if (e instanceof SftpException)
        {
            SftpException ex = (SftpException) e;

            String reason = "Error   =" + ex.id + ":" + JCraftClient.getJschErrorString(ex.id) + "\n" + "message ="
                    + ex.getMessage() + "\n" + "Channel connected=" + sftpChannel.isConnected() + "\n"
                    + "Session connected=" + session.isConnected() + "\n";

            // logger.messagePrintln(this,"sftp error="+getJschErrorString(ex.id));

            if (ex.id == 1)
                return new VlException(message + "End of file error", reason, e);

            if (ex.id == 2) // SftpException reason 2=no such file !
                return new ResourceNotFoundException(message + "StfpException:" + reason, e);

            if (ex.id == 3)
                // don't know whether it is read or write
                return new VlException("AccessDenied", message + reason, e);

            return new VlException("SFTP Exception", message + reason, e);
        }
        else
        {
            return new VlException("Exception", message + e.getMessage(), e);
        }
    }

    // ===
    // Factory methods
    // ===

  


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
        SftpATTRS attrs = getSftpAttrs(path);
        int mod = attrs.getPermissions();

        return ((mod & 0200) > 0);
    }

    public boolean isReadable(String path) throws VlException
    {
        // check user readable:
        SftpATTRS attrs = getSftpAttrs(path);
        int mod = attrs.getPermissions();

        return ((mod & 0400) > 0);

    }

    public boolean isLink(String path) throws VlException
    {
        SftpATTRS attrs = getSftpAttrs(path);

        boolean val = attrs.isLink();
        if (val)
        {
            String strs[] = attrs.getExtended();
            if (strs != null)
            {
                int i = 0;
                for (String str : strs)
                {
                    System.err.println("" + (i++) + ":" + str);
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

    public void setACL(String path, VAttribute[][] acl, boolean isDir) throws VlException
    {
        int mode = VFS.convertACL2FileMode(acl, isDir);

        if (mode < 0)
            throw new VlException("Error converting ACL list");

        setPermissions(path, isDir, mode);
    }

    private void setPermissions(String path, boolean isDir, int mode) throws VlException
    {
        // SftpATTRS attrs=new SftpATTRS();
        SftpATTRS attrs = getSftpAttrs(path);
        attrs.setPERMISSIONS(mode);

        setSftpAttrs(path, isDir, attrs);
    }

    public int getOwnerID(String path) throws VlException
    {
        // SftpATTRS attrs=new SftpATTRS();
        SftpATTRS attrs = getSftpAttrs(path);
        return attrs.getUId();
    }

    public int getGroupID(String path) throws VlException
    {
        // SftpATTRS attrs=new SftpATTRS();
        SftpATTRS attrs = getSftpAttrs(path);
        return attrs.getGId();
    }

    SftpATTRS attrs = null;

    public VAttribute getAttribute(VFSNode node, SftpATTRS attrs, String name, boolean isDir, boolean update)
            throws VlException
    {

        // Optimization: only update if a SftpAttribute AND an update is
        // requested:
        if (name == null)
            return null;

        // initialize attributes if not yet fetched!
        if (attrs == null)
        {
            attrs = this.getSftpAttrs(node.getPath());
        }

        // get attributes from same holder:

        if (name.compareTo(ATTR_MODIFICATION_TIME) == 0)
        {
            return VAttribute.createDateSinceEpoch(name, getModificationTime(attrs));
        }
        else if (name.compareTo(ATTR_ACCESS_TIME) == 0)
        {
            return new VAttribute(VAttributeType.TIME, name, getAccessTime(attrs));
        }
        else if (name.compareTo(ATTR_LENGTH) == 0)
        {
            return new VAttribute(name, attrs.getSize());
        }
        else if (name.compareTo(ATTR_UID) == 0)
        {
            return new VAttribute(name, attrs.getUId());
        }
        else if (name.compareTo(ATTR_GID) == 0)
        {
            return new VAttribute(name, attrs.getGId());
        }
        else if (name.compareTo(ATTR_UNIX_FILE_MODE) == 0)
        {
            // note sftp attributes return higher value the (8)07777 (isdir and
            // islink)
            return new VAttribute(name, "0" + Integer.toOctalString(attrs.getPermissions() % 07777));
        }

        return null;
    }

    public VAttribute[] getAttributes(VFSNode node, SftpATTRS holder, String[] names, boolean isDir) throws VlException
    {
        if (names == null)
            return null;

        VAttribute attrs[] = new VAttribute[names.length];

        for (int i = 0; i < names.length; i++)
        {
            attrs[i] = getAttribute(node, holder, names[i], isDir, (i == 0));
        }

        return attrs;

    }

    public String getPermissionsString(SftpATTRS attrs, boolean isDir) throws VlException
    {
        return attrs.getPermissionsString();
    }

    public int getUnixMode(SftpATTRS attrs, boolean isDir) throws VlException
    {
        return attrs.getPermissions();
    }

    public int getPermissions(SftpATTRS attrs) throws VlException
    {
        return attrs.getPermissions();
    }

    public long getModificationTime(SftpATTRS attrs)
    {
        return attrs.getMTime() * 1000L;
    }

    public long getAccessTime(SftpATTRS attrs)
    {
        return attrs.getATime() * 1000L;
    }

    public long getLength(SftpATTRS attrs)
    {
        return attrs.getSize();
    }

    // =======================================================================
    // VServer interface
    // =======================================================================

    public void connect() throws VlException
    {
        ServerInfo info = this.getServerInfo();

        // System.out.println("s:" + server + " p:" + port + " u:" + user);
        try
        {

            initSession();
            initSftpChannel();

            logger.debugPrintf("Connected to:%s:%d\n", getHostname(), getPort());

            // valid authentication
            info.setHasValidAuthentication(true);
            info.store(); // update in registry !
            
            try
            {
                defaultHome = this.sftpChannel.pwd();
            }
            catch (SftpException e)
            {
                // can not get pwd() ?
                throw convertException(e);
            }
            logger.debugPrintf("defaultHome=%s\n", defaultHome);
        }
        catch (JSchException e)
        {
            info.setHasValidAuthentication(false); // invalidize authentication
                                                   // info !
            info.store(); // Update in registry !
            throw convertException(e);
        }

        try
        {
            // ~/.ssh/known_hosts
            // HostKeyRepository hkr=jschInstance.getHostKeyRepository();
            HostKey hk = session.getHostKey();
            logger.debugPrintf("Got hostkey for host %s: <%s>:%s='%s'\n", getHostname(),hk.getType(), hk.getHost(),hk.getKey());
            // check hostkey ?
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Error initializing jCraft SSH:" + e);
        }
    }

    private void initSession() throws JSchException
    {
        int port = getPort();
        if (port <= 0)
            port = VRS.DEFAULT_SSH_PORT;

        
        if (getUseProxy()==false)
        { 
            
            this.session = jcraftClient.getSession(userInfo.getUsername(), getHostname(), port);
            // ssh.setSocketTimeout(timeout);

            session.setUserInfo(userInfo);
            session.connect();
        }
        else
        {
            int proxyPort=getProxyPort(); 
            if (proxyPort<=0)
                proxyPort=VRS.DEFAULT_SSH_PORT;

            // get custom port (if defined) 
            int proxyLocalPort=getProxyLocalPort(); 
            
            // ---
            // JCraft (and SSH) keep record of hostkeys when connecting to "localhost"
            // When using tunnels to "localhost" where the hostname is always "localhost" but the 
            // local proxy port changes, each time the remote host key is stored for that localhost+port combination. 
            // so if the same (local) port is used for different remote hosts, a host key conflict is detected! 
            //
            // Must use some kind of hash mapping to get the same (local) port number for the same (remote) user+host+port 
            // combination so that the right host key is stored. 
            //
            
            if (proxyLocalPort<=0)
            {
                // map to hash port. Do not register port yet. 
                proxyLocalPort=jcraftClient.createLocalProxyHashPort(getUsername(),getHostname(),getPort(),false);  
            }
            
            logger.infoPrintf("New local proxy port=%d\n",proxyLocalPort); 
            
            // can not create custom ports < 1024 
            if (proxyLocalPort<=1024)
            {
                logger.warnPrintf("*** Warning: hashed port number < 1024:%d\n",proxyLocalPort); 
                proxyLocalPort=JCraftClient.SSH_DEFAULT_LOCAL_PORT; 
            }
            
            String localHost="localhost"; 
            
            this.proxySession=jcraftClient.getSession(getProxyUser(),getProxyHost(),getProxyPort()); 
            this.proxyUserInfo=new VLUserInfo(true); 
            this.proxySession.setUserInfo(proxyUserInfo); 
            this.proxySession.connect(); 
            
            this.jcraftClient.createOutgoingTunnel(proxySession, proxyLocalPort, getHostname(),port); 
            this.session = jcraftClient.getSession(userInfo.getUsername(),localHost,proxyLocalPort); 

            session.setUserInfo(userInfo);
            session.connect();
            
            // now claim port;  
            jcraftClient.registerUsedLocalPort(proxyLocalPort);
        }
    }

    
    public boolean getUseProxy()
    {
        return this.getServerInfo().getBoolProperty(ServerInfo.ATTR_SSH_USE_PROXY, false);  
    }
    
    public String getProxyUser()
    {
        return this.getServerInfo().getStringProperty(ServerInfo.ATTR_SSH_PROXY_USERNAME,getUsername()); 
    }
    
    public String getProxyHost()
    {
        return this.getServerInfo().getStringProperty(ServerInfo.ATTR_SSH_PROXY_HOSTNAME,null); 
    }

    public int getProxyPort()
    {
        return this.getServerInfo().getIntProperty(ServerInfo.ATTR_SSH_PROXY_PORT,0); 
    }

    /**
     * Return specific port to be used as local proxy port. 
     * <=0 is auto-generate. 
     * 
     * @return
     */
    public int getProxyLocalPort()
    {
        // return 0 -> auto configure!
        return this.getServerInfo().getIntProperty(ServerInfo.ATTR_SSH_LOCAL_PROXY_PORT,0); 
    }
    
    
    private void initSftpChannel() throws VlException
    {
        // channel.setExtOutputStream(new OutputLog());
        // channel.setOutputStream(new OutputLog());
        this.sftpChannel = createNewFTPChannel();
    }

    private void checkState() throws VlException
    {
        try
        {
            synchronized (serverMutex)
            {
                if (this.session.isConnected() == false)
                {
                    // not really an error, reconnect usually succeeds. If
                    // reconnect fails -> throw exception
                    logger.errorPrintf("*** Error: Session disconnected: reconnecting:%s\n", this);

                    initSession(); 
                    // recreate sftpChannel: 
                    
                    if (sftpChannel.isConnected() == true)
                        sftpChannel.disconnect();

                    sftpChannel = null;
                }

                if ((this.sftpChannel == null) || this.sftpChannel.isConnected() == false)
                {
                    logger.errorPrintf("*** Error: SFTP Channel closed: reconnecting:%s\n", this);
                    initSftpChannel(); 
                }
            }
        }
        catch (JSchException e)
        {
            throw convertException(e);
        }
    }

    public String[] getSSHIdentities()
    {
        String idStr = getServerInfo().getStringProperty(ServerInfo.ATTR_SSH_IDENTITY);
        logger.debugPrintf("sshIdentities=%s\n",idStr); 
        
        // split optional comma separated list
        if (StringUtil.isNonWhiteSpace(idStr))
        {
            String strs[]=idStr.split(","); 
            if ((strs!=null) && (strs.length>0)) 
                return strs;
        }
        
        return new String[]{JCraftClient.SSH_DEFAULT_ID_RSA};
    }

    public void disconnect() throws VlException
    {
        synchronized (serverMutex)
        {
            this.sftpChannel.disconnect();
            this.session.disconnect();
            this.session = null;
            this.sftpChannel = null;
        }
    }

    public String getID()
    {
        return this.serverID;
    }

    public String getScheme()
    {
        return VFS.SFTP_SCHEME;
    }

    public boolean isConnected()
    {
        if (this.sftpChannel != null)
            return this.sftpChannel.isConnected();

        return false;
    }

    private String getKnownHostsFile()
    {
        ServerInfo info = this.getServerInfo();

        VAttribute attr = info.getAttribute(SftpFSFactory.ATTR_KNOWN_HOSTS_FILE);

        if (attr == null)
            return getDefaultKnownHostsFile();
        else
            return attr.getStringValue();
    }

    public String getSSHConfigDir()
    {
        ServerInfo info = this.getServerInfo();

        VAttribute attr = info.getAttribute(SftpFSFactory.ATTR_SSH_CONFIG_DIR);

        if (attr == null)
            return getDefaultSSHDir();
        else
            return attr.getStringValue();
    }

    public boolean getAllowUserInterAction()
    {
        return this.vrsContext.getConfigManager().getAllowUserInteraction();
    }

    public String getDefaultKnownHostsFile()
    {
        return getDefaultSSHDir() + VRL.SEP_CHAR_STR+JCraftClient.SSH_KNOWN_HOSTS;
    }

    public String getDefaultSSHDir()
    {
        return vrsContext.getLocalUserHome() + VRL.SEP_CHAR_STR + JCraftClient.SSH_CONFIG_SIBDUR;
    }

    void setFinalUserSubject(VRSContext newContext) throws VlAuthenticationException
    {
        String newSubject = newContext.getGridProxy().getSubject();

        if (this.userSubject != null)
        {
            if (this.userSubject.compareTo(newSubject) != 0)
            {
                // OOPSY!! 
                throw new nl.uva.vlet.exception.VlAuthenticationException(
                        "Illegal User. Current server is not authenticated for new user:" + newSubject);
            }
        }
        else
            // May Be Set Only Once !!!
            this.userSubject = newSubject;
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
        return new SftpDir(this, path);
    }

    @Override
    public VFile newFile(VRL path) throws VlException
    {
        return new SftpFile(this, path);
    }

    /**
     * Create localPort which connected to remoteHost:remotePort
     * 
     * @throws VlException
     */
    public void createOutgoingTunnel(int localPort, String remoteHost, int remotePort) throws VlException
    {
        try
        {
            this.jcraftClient.createOutgoingTunnel(session,localPort,remoteHost,remotePort); 
        }
        catch (JSchException e)
        {
            throw this.convertException(e, "Couldn't create remote port forwarding:" + localPort + ":" + remoteHost
                    + ":" + remotePort);
        }
        
    }

    /**
     * Create localPort which connected to remoteHost:remotePort
     * 
     * @throws VlException
     */
    public void createIncomingTunnel(int remotePort, String localHost, int localPort) throws VlException
    {
        try
        {
            this.jcraftClient.createIncomingTunnel(session,remotePort,localHost,localPort); 
 
        }
        catch (JSchException e)
        {
            throw this.convertException(e, "Couldn't create incoming port forwarding:" + remotePort + ":" + localHost
                    + ":" + localPort);
        }

    }

    /** Creates a new outgoing SSH tunnel and return the local tunnel port. */
    public int createOutgoingTunnel(String remoteHost, int remotePort) throws VlException
    {
        VlException lastEx = null;

        for (int i = 0; i < 32; i++)
        {
            int lport = jcraftClient.getFreeLocalPort(false);

            try
            {
                this.createOutgoingTunnel(lport, remoteHost, remotePort);
                // succesful: claim port
                jcraftClient.registerUsedLocalPort(lport);
                return lport;
            }
            catch (VlException e)
            {
                lastEx = e;
                logger.logException(ClassLogger.ERROR, e, "Failed to create new local tunnel port:%d\n", lport);
            }
        }

        if (lastEx != null)
            throw lastEx;

        throw new VlException("Failed to create new local tunnel port");
    }

   

    public SSHChannel createShellChannel(VRL optLocation) throws VlException
    {
        SSHChannelOptions options = new SSHChannelOptions();
        SSHChannel sshChannel = new SSHChannel(vrsContext, getUsername(), getHostname(), getPort(), options);

        String path = optLocation.getPath();

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

    public static void clearServers()
    {   
        //jcraftClients.clear(); 
    }

}
