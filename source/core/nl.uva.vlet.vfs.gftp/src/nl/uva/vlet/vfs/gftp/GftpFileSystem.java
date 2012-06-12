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
 * $Id: GftpFileSystem.java,v 1.4 2011-06-07 14:31:44 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:44 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ALLOW_3RD_PARTY;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CREATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GROUP;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_OWNER;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PASSIVE_MODE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIQUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlAuthenticationException;
import nl.uva.vlet.exception.VlConnectionException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlInterruptedException;
import nl.uva.vlet.exception.VlServerException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.vlet.uva.grid.globus.GlobusUtil;

import org.globus.ftp.Buffer;
import org.globus.ftp.ChecksumAlgorithm;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.DataSource;
import org.globus.ftp.FileInfo;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.MlsxEntry;
import org.globus.ftp.OutputStreamDataSource;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.FTPException;
import org.globus.ftp.exception.ServerException;
import org.globus.ftp.vanilla.TransferState;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.io.streams.GridFTPInputStream;
import org.globus.io.streams.GridFTPOutputStream;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/**
 * GridFTP FileSystem implementation. 
 * Beware: The used GridFTP client from Globus is NOT Thread Save. 
 * All methods which interact with the <i>same</i> GridFTPClient client MUST
 * be synchronized. <br>
 * Some multithreaded solutions used are: 
 * <br> 
 * - Creating separate GridFTPClient
 * for put/get calls. Notes:<br> 
 * - GridFTP requires to set the Active/Passive  mode for EACH call which used the 
 *   datachannel. These are: get, put, and msld/list commands
 * <p> 
 * @see mslx entries:
 *      http://bgp.potaroo.net/ietf/all-ids/draft-ietf-ftpext-mlst-16.txt
 * @see gridftp : <A
 *      href="http://www.globus.org/cog/jftp/guide.html">http://www.globus.org/cog/jftp/guide.html</a>
 *      <p>
 *      Backwards compatibily notes:<br>
 *      <li> Mlsx entries are not support for old (1.0) gridftp server. The
 *      methods fakeMsl[dt] emulates these calls.
 *      <li> DataChannelAuthentication is not always support. The feature list
 *      from the remote GridFTP server is checked.
 *      <li> 'blindMode' is used for servers which do not allow listing of
 *      directories, Like SRM Storage Elements.
 * 
 * @author P.T. de Boer
 */
public class GftpFileSystem extends FileSystemNode
{
    // ============================================================================
    // Class stuff
    // ============================================================================
    
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(GftpFileSystem.class); 
        //logger.setLevelToDebug(); 
    }
    
    /** EDG SE backwards compatibility options (HACK) attribute */
    public static final String ATTR_GFTP_BLIND_MODE = "blindMode";

    /** ServerInfo Attribute localDataChannelAuthentication */
    public static final String ATTR_GFTP_DATA_CHANNEL_AUTHENTICATION = "dataChannelAuthentication";

    // maximum nr. of servers in the same pool
    //private static final int maximumServerPoolCount = 10;

    private static final String UNIX_GROUP = "unix.group";
    private static final String UNIX_OWNER = "unix.owner";
    //private static final String UNIX_MODE_STRING = "unix.mode.string";
    private static final String UNIX_MODE = "unix.mode";
    //private static final String GFTP_UNIQUE = "unique";

    // default server settings:
    //private static boolean default_local_data_channel_authentication = true;
    private static boolean default_usepassivemode = true;
    private static boolean default_allow3rdParty = true;
    private static boolean default_useblindmode = false;

    // private static Hashtable<String, GftpServer> servers = new
    // Hashtable<String, GftpServer>();

    public static String[] attributeNames =
        { ATTR_PASSIVE_MODE };

    /** Simple Data Source which produces 0 bytes ! */
    public static class NilSource implements DataSource
    {
        public NilSource()
        {
            
        }
        public void close() throws IOException
        {
        }

        public Buffer read() throws IOException
        {
            return null;// buf;
        }

        public long totalSize() throws IOException
        {
            return 0;
        }
    }
    

    static private Object staticServerMutex = new Object();

    public static GftpFileSystem getOrCreateServerFor(VRSContext context,
            VRL loc) throws VlException
    {
        GftpFileSystem server = null;

        synchronized (staticServerMutex)
        {
            String serverid = createServerID(loc);

            if ((server = (GftpFileSystem) context.getServerInstance(serverid,
                    GftpFileSystem.class)) != null)
            {
                // check connected server ? 
            }
            else
            {
                ServerInfo info = context.getServerInfoFor(loc, true);

                server = new GftpFileSystem(context, info,loc);
                // server.serverInfo=info;
            
                server.setID(serverid); 
                
                context.putServerInstance(server);
            }
        }

        // =======================================
        // Check Global Configuration !
        // =======================================

        // VAttribute attr = null;

        // Now threads will be synchronized about same server object

        try
        {
            synchronized(server.serverMutex)
            {
                // check if I can have access:
                if (server.client == null)
                {
                    // client should not be null after this code:
                    server.connect();
                }
            }
        }
        catch (Throwable e)
        {
            // must remove faulty server:
            context.removeServerInstance(server); // synchronized remove

            if (e instanceof VlException)
                throw ((VlException) e);
            else
                throw new VlException(e.getClass().getName(), e);
        }

        return server;
    }

    // ============================================================================
    // Instance Stuff
    // ============================================================================
    private GridFTPClient client = null;

    private String hostname = null;

    private int port = 0;

    /** Last occured serverException */
    private boolean serverException = false;

    // private volatile int waitCounter;

    private String homeDir;

    // Whether msld/mslt is supported. (Gsftp v1. does not support msld/mslt)
    Boolean protocol_v1 = false;

    Boolean serverMutex = new Boolean(true);

    /** For old GFTP server, perform extrqa ls -d .* command: */
    private boolean explicitListHidden = true;
    

    private GftpFeatureList features;

   // private URI sshTunnelUri;
    
    GftpFileSystem(VRSContext context, ServerInfo info, VRL location)
        throws VlException
    {
        super(context, info);
        init(info.getHostname(), info.getPort());
        connect(); 
    }
    
    /**
     * Create New Globus GFTPClient using Server's defaults (from
     * this.serverInfo) serverInfo,hostname and port MUST be set before calling
     * the method.
     * 
     */
    public GridFTPClient createGFTPClient() throws VlException
    {
        // load CA certs (trusted) from resources classpath
        // proxy created with Grid -proxy-init cmd

        // Globus Credential, will move to CogUtils

        GlobusCredential globusCred = null;
        // use NEW client !!!

        GridFTPClient newClient = null;

        if (port <= 0)
            port = VRS.DEFAULT_GRIDFTP_PORT;

        if (hostname == null)
            hostname = ""; // no default host

        //
        // update firewall portrange for Globus:
        //
        GlobalConfig.setSystemProperty("org.globus.tcp.port.range", 
                Global.getFirewallPortRangeString());

        // currently proxy must be created...

        try
        {
            GSSCredential cred = getValidGSSCredential(vrsContext);

            if (cred == null)
            {
                logger.warnPrintf("Warning: NULL Credentials !\n");
            }

            // ***
            // GSI authentication doesn like SSH tunnel (host mismatch!) 
            // ***
            // if (this.sshTunnelUri!=null)
            //      newClient = new GridFTPClient(sshTunnelUri.getHost(),
            //                                    sshTunnelUri.getPort());
            // else
            newClient = new GridFTPClient(hostname, port);
            
            newClient.authenticate(cred);

            // create Util Class around GridFeatureList :
            this.features = new GftpFeatureList(newClient
                    .getFeatureList());

            logger.infoPrintf("Server Feature list '%s'=%s\n",this,features);
            
            this.putInstanceAttribute("gftpFeatureList",features.toCommaString()); 
            
            // set type only once
            // don't set mode, must be done before each call
            // _setCheckMode(client,true,false);
            // set default to IMAGE,only use ASCII when REALLY needed

            // default to Image:
            newClient.setType(Session.TYPE_IMAGE);
        
            // disabled for old gridftp servers:
            boolean ldca=useDataChannelAuthentication();
            
            if (ldca==false)
                newClient.setLocalNoDataChannelAuthentication();
            
            // update instance (debug) information.
            this.putInstanceAttribute(new VAttribute(ATTR_GFTP_DATA_CHANNEL_AUTHENTICATION,ldca)); 

            logger.infoPrintf(">>> GftpServer: new gftp client:%s:%d\n",hostname,port); 

        }
        catch (UnknownHostException e)
        {
            throw new VlConnectionException("Unknown hostname or server:"
                    + e.getMessage(), e);
        }
        catch (ServerException e)
        {
            throw new VlServerException(e.getMessage(), e);

        }
        catch (ConnectException e)
        {
            throw new VlConnectionException(
                    "Connection error when connecting to:" + this
                            + ".\nReason=" + e.getMessage(), e);
        }
        catch (Exception e)
        {
            /// Globus/Authentication: 
            
            VlException globusEx=GlobusUtil.checkException("Couldn't connect to:"+this,e);
            
            if (globusEx!=null)
                throw globusEx; 
            
            throw new VlServerException("Coudln't connect to:"+this+"\nReason="+e.getMessage(), e);
        }

        return newClient;
    }

    
    // ========================================================================
    // Critical Methods
    // ========================================================================
    // These methods interface with the Globus GridFTPFS client.
    //

    private void init(String hostname, int port) throws VlException
    {
        this.hostname = hostname;

        if ((hostname == null) || (hostname.compareTo("") == 0))
        {
            throw new VlServerException("Hostname is not specified");
        }

        if (port <= 0)
            this.port = VRS.DEFAULT_GRIDFTP_PORT;
        else
            this.port = port;

        logger.debugPrintf("New GFTP Server:%s:%d\n",hostname,port);
        
       // initShhTunnel(); 
    }

//    private void initShhTunnel() throws VlException
//    {
//        if (this.getVRSContext().getConfigManager().getUseSSHTunnel(
//            getScheme(),
//            hostname,
//            port))
//        {
//            int lport=SSHUtil.createSSHTunnel(getVRSContext(),
//                    getScheme(),
//                    hostname,
//                    port);
//        
//            try
//            {
//                sshTunnelUri=new URI(getScheme()+"://localhost:"+lport); 
//            }
//            catch (URISyntaxException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
    
    public void connect() throws VlException
    {
        logger.infoPrintf("Connecting to:%s\n",this); 
        
        synchronized (serverMutex)
        {
            // initClient() and update settings this this.serverInfo !

            this.client = createGFTPClient();

            String cwd = null;

            // *** Service Security ***
            // Extra check to check if the users proxy subject hasn't changed.
            // this is possible is a Grid Service Context !!!
            // check current subject from Grid Proxy.
            

            try
            {
                cwd = this.homeDir = client.getCurrentDir();
            }
            catch (ServerException e)
            {
                throw new nl.uva.vlet.exception.VlServerException(
                        "GridFTPFS Server Exception:"
                                + e.getMessage(), e);
            }
            catch (IOException e)
            {
                throw new nl.uva.vlet.exception.VlIOException(
                        "GridFTPFS IO Exception:" + e.getMessage(), e);
            }

            try
            {
                // / check
                if (this.protocol_v1 == false)
                    this.client.mlst(cwd);
            }
            catch (ServerException e)
            {
                logger.warnPrintf("GFTP: 'mslt' not understood. Setting protocol to version 1.0 for:%s\n",this); 
                this.protocol_v1 = true;
            }
            catch (IOException e)
            {
                logger.warnPrintf("GFTP: 'mslt' not understood. Setting protocol to version 1.0 for:%s\n",this); 
                this.protocol_v1 = true;
            }
            
            // inform ServerInfo: 
            {
                // debug option: 
                VAttribute attr=new VAttribute("usingProtocolV1",protocol_v1); 
                attr.setEditable(false); 
                this.putInstanceAttribute(attr);  
            }
        }
    }

    boolean usePassive()
    {
        // 
        // use passive is different: if set as Global option
        // this means block all active traffic ! 
        // 
    
        // Setting this properties means setting this for ALL servers !
        boolean val = this.vrsContext.getBoolProperty(
                ATTR_PASSIVE_MODE, false);

        // the default is 'false', Global specified means override !
        if (val == true)
        {
            logger.debugPrintf("Using passiveMode=true from Global Settings:\n"); 
            // keep: val=true;
        }
        else
        {
            // check for serverInfo and "gtfp.usePassiveMode" options 
            val=this.getBooleanServerOption(ATTR_PASSIVE_MODE,default_usepassivemode); 
        }

        // dynamic update ! 
        this.putInstanceAttribute(new VAttribute(ATTR_PASSIVE_MODE,val)); 
        logger.debugPrintf("passiveMode=%s\n",(val?"true":"false"));
        
        return val;
    }

    /*
     * private void release() {
     * 
     * releaseBusy(); }
     */

    /**
     * Set state of connection and check for exceptions.
     * 
     * @throws IOException
     * @throws ServerException
     * @throws ClientException
     * @throws VlException
     */
    private void setCheckMode2(boolean _binMode,
            boolean updatePassiveMode) throws VlException
    {
        long id = Thread.currentThread().getId();

//        debug("setCheckMode: Thread[" + id + "]="
//                + Thread.currentThread().getName());

        if (client == null)
        {
            //debug("setCheckMode: client= null!!!");
            throw new VlServerException("Server not connected:"
                    + this);
        }

        // claimBusy();
        synchronized (serverMutex)
        {
            {
                // test if the client is still alive
                try
                {
                    // client.setType(Session.TYPE_IMAGE);
                    // _setCheckMode(client,false,false);
                    String str = client.getCurrentDir();
                    //debug("setCheckMode: getCUrrentDir()=" + str);

                }
                catch (Exception e)
                {
                    logger.errorPrintf("setCheckMode: Client check failed:resetting client:%s\n",this); 
                    reset();
                    this.serverException = false;
                }
            }

            try
            {
                // if (client.isPassiveMode()==getUsePassiveMode)
                // return;
                //debug("setCheckMode: PRE setting mode...");

                if (usePassive() == false)
                {
                    // not needed, just use default image type:

                    /*
                     * if (binMode) client.setType(Session.TYPE_IMAGE); else
                     * client.setType(Session.TYPE_ASCII);
                     */
                    client.setPassiveMode(false);
                }
                else
                {
                    // not needed, just use default image type:

                    /*
                     * if (binMode) client.setType(Session.TYPE_IMAGE); else
                     * client.setType(Session.TYPE_ASCII);
                     */

                    // only set PassiveMode if needed
                    // this command must be performed before opening
                    // a datachannel and may not performed twice.
                    if (updatePassiveMode)
                        client.setPassiveMode(true);

                    // _setCheckMode(client,binMode,updatePassiveMode);
                }

                //debug("setCheckMode: POST setting mode...");

            }
            catch (ServerException e)
            {
                logger.logException(ClassLogger.ERROR,e,"*** ServerException in claimMode!!!***\n");
                // convertException also set's serverException==true
                throw convertException(e);
            }
            catch (Exception e)
            {
                // convertException also set's serverException==true
                throw convertException(e);
            }
        }
    }

    public void disconnect() throws VlException
    {
        logger.infoPrintf("disconnecting:%s\n",this); 
        
        if (client==null)
            return; 
        
        try
        {
            features = null; // Clear features, reconnect should refrehs/update them !
            client.close();
            client = null;
        }
        catch (Exception e)
        {
            throw convertException(e);
        }
    }

    /** Reset the connection to the server */
    private void reset()
    {
        logger.infoPrintf("resetting connection to:%s\n", this);

        synchronized (serverMutex)
        {
            try
            {
                disconnect();
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.WARN,e,"Exception when disconnecting:%s\n",this); 
            }

            client = null;

            try
            {
                connect();
                // reset exception after succesful connect. 
                this.serverException = false;
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.ERROR,e,"Could not reconnect GFTP Server:%s\n",this); 
            }
        }
    }
    // =================
    // GridServer Options 
    // =================
    
    /**
     * Blind mode is for 'crippled' Grid FTP server like the some SEs used in
     * EDG. The idea is that nothing works (list(),exists(),'stat' command,etc)
     * except downloading a file and maybe uploading. 
     */

    boolean useBlindMode()
    {
        boolean val=getBooleanServerOption(GftpFileSystem.ATTR_GFTP_BLIND_MODE,default_useblindmode);
        // dynamic update ! 
        this.putInstanceAttribute(new VAttribute(GftpFileSystem.ATTR_GFTP_BLIND_MODE,val));
        
        return val; 
    }
    
    public Boolean getBooleanServerOption(String optName,boolean defaultValue)
    {
        if (optName==null)
            return false; 
        
        VAttribute attr=getServerInfo().getAttribute(optName); 
        
        if (attr!=null)
            return attr.getBooleanValue(); 
        
        // Check Context, will check Global Option as well 
        // return default value if not found
        return this.vrsContext.getBoolProperty("gftp."+optName,defaultValue); 
        
    }
    
    /** Return NULL if property isn't defined */ 
    public Boolean getServerOrContextBooleanProperty(String name)
    {
        if (name==null)
            return null; 
        
        VAttribute attr=getServerInfo().getAttribute(name); 
        
        if (attr!=null)
            return new Boolean(attr.getBooleanValue());  
        
        // Check Context, will check Global Option as well 
        // return default value if not found
        String propstr=this.vrsContext.getProperty(name).toString();   
        
        if (propstr!=null)
            return Boolean.parseBoolean(propstr);
        
        return null; 
        
    }
    
    /**
     * LocalDataChannelAuthentication: Backwards (old grdftp option)
     * compatibility option.
     */
    boolean useDataChannelAuthentication()
    {
        boolean val = this.features.hasDataChannelAuthentication();

        // not stored yet:
        if (this.instanceAttributes.containsKey(GftpFileSystem.ATTR_GFTP_DATA_CHANNEL_AUTHENTICATION)==false)
        {
            if (val==false)
            {
                // Warn and store: 
                logger.warnPrintf("DataChannelAuthentication NOT supported for:%s\n",this);
            }
            
        }
        // store to block further warnings! 
        this.putInstanceAttribute(new VAttribute(GftpFileSystem.ATTR_GFTP_DATA_CHANNEL_AUTHENTICATION,val));
        
        return val;    
    }
    
    boolean getAllow3rdParty()
    {
        return getBooleanServerOption(ATTR_ALLOW_3RD_PARTY,default_allow3rdParty); 
    }
    
    
    /**
     * Optimized GRidFTP upload. Performs asyncPut and monitors the datastream.
     */
    public void uploadFile(VFSTransfer transfer,
            String localfilepath, String remotefilepath)
            throws VlException
    {
        logger.debugPrintf("updloadFile(): %s -> %s\n",localfilepath,remotefilepath);

        try
        {

            java.io.File lfile = new File(localfilepath);
            RandomAccessFile rfile = new RandomAccessFile(lfile, "r");
            TransferRandomIO riofile = new TransferRandomIO(rfile);

            transfer.startSubTask("Uploading GridFTP file.",lfile.length());
            transfer.logPrintf("Uploading file:"+localfilepath+"\n");
            TransferState transferState = null;

            GridFTPClient myclient = null;
            MarkerListener markerListener = new GftpMarkerListener(); // new
                                                                        // GftpMarker();
            //
            // update mode before PUT call !!
            //

            if (usePassive())
            {
                // use private client for passive mode
                myclient = this.createGFTPClient();
                myclient.setPassiveMode(usePassive());
                // _setCheckMode(myclient,true,true);
                transferState = myclient.asynchPut(remotefilepath,
                        riofile, markerListener);
                
              
            }
            else
                synchronized (serverMutex)
                {
                    setCheckMode2(true, usePassive());
                    transferState = client.asynchPut(remotefilepath,
                            riofile, markerListener);
                }

            while (transferState.isDone() == false)
            {
                if (transfer.getMustStop())
                    riofile.setMustStop();

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    logger.logException(ClassLogger.ERROR,e,"Interrrupted!\n") ; 
                    throw new VlInterruptedException("Interrupted!");
                }

                // Gftp does not provide transfer count:
                long size = riofile.getNrRead();
                //debug("- current transferred size=" + size);
                transfer.updateSubTaskDone(size);

                if (transferState.getError() != null)
                {
                    Exception e = transferState.getError();
                    throw new VlException("TransferError", e
                            .getMessage(), e);
                }
            }

            this.finalize(myclient); 
            myclient=null; 
            
            // POST: Gftp does not provide transfer count:
            transfer.updateSubTaskDone(riofile.getNrRead());
            transfer.endSubTask("Upload GridFTP File"); 
            //debug("after put");

        }
        catch (ServerException e)
        {
            this.serverException = true;
            throw new nl.uva.vlet.exception.VlServerException(
                    "GridFTPFS Server Exception:" + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlIOException("GridFTPFS Server Exception:"
                    + e.getMessage(), e);
        }
        catch (ClientException e)
        {
            throw new VlIOException("GridFTPFS Client Exception:"
                    + e.getMessage(), e);
        }

    }

    /**
     * Optimized GRidFTP download. Performs asyncGet and monitors the
     * datastream.
     */
    public void downloadFile(VFSTransfer transfer,
            String remotefilepath, String toLocalfilepath)
            throws VlException
    {
        logger.debugPrintf("downloadFile():%s: from '%s' to '%s'\n",this,remotefilepath,toLocalfilepath);
        
        //transfer.setCurrentSubTask("Downloading file."); 
        
        try
        {
            // only check size if not blindMode !
            transfer.startSubTask("Downloading GridFTP file.",getSize(remotefilepath));

            File lfile = new File(toLocalfilepath);

            // default: delete existing
            if (lfile.exists())
                lfile.delete();

            // (re)create:
            lfile.createNewFile();
            RandomAccessFile rfile = new RandomAccessFile(lfile, "rw");
            TransferRandomIO riofile = new TransferRandomIO(rfile);

            transfer.logPrintf("Downloading file.\n");

            GridFTPClient myclient = null;
            TransferState transferState = null;
            MarkerListener markerListener = new GftpMarkerListener(); 
            // new GftpMarker();

            if (usePassive())
            {
                // Passive parallel asynchronous mode:
                // use private client for passive mode
                myclient = this.createGFTPClient();
                myclient.setPassiveMode(true);
                // _setCheckMode(myclient,true,true);
                transferState = myclient.asynchGet(remotefilepath,
                        riofile, markerListener);
            }
            else synchronized (serverMutex)
            {
                // Active asynchronous:
                setCheckMode2(true, usePassive());
                transferState = client.asynchGet(remotefilepath,
                            riofile, markerListener);
            }
            
            // ===
            // LOOP
            // ===
            while ((transferState != null)
                    && (transferState.isDone() == false))
            {
                if (transfer.getMustStop())
                    riofile.setMustStop();

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    logger.logException(ClassLogger.ERROR,e,"Interrupted!\n"); 
                }

                // Gftp does not provide transfer count:
                transfer.updateSubTaskDone(riofile.getNrWritten());

                if (transfer.getMustStop() == true)
                {
                    // stop ?
                    transferState.transferError(new VlInterruptedException(
                                    "InterruptedException"));
                }
            }
            // ===
            // POST
            // ===

            if (myclient != null)
                myclient.close();

            if ((transferState != null)
                    && (transferState.getError() != null))
            {
                Exception e = transferState.getError();
                throw new VlException("TransferError",
                        e.getMessage(), e);
            }

            // Gftp does not provide transfer count: update using (Assumed) file
            // length
            transfer.updateSubTaskDone(lfile.length());
            transfer.endSubTask("Downloading GridFTP File"); 
        }
        catch (ServerException e)
        {
            logger.logException(ClassLogger.DEBUG,e,"ServerException:%s\n",e); 
            this.serverException = true;
            throw new nl.uva.vlet.exception.VlServerException(
                    "GridFTP:ServerException:" + e.getMessage(), e);
        }
        catch (IOException e)
        {
            logger.logException(ClassLogger.DEBUG,e,"IOException:%s\n",e); 
            throw new VlIOException("GridFTP:IOException:"
                    + e.getMessage(), e);
        }
        catch (ClientException e)
        {
            logger.logException(ClassLogger.DEBUG,e,"ClientException:%s\n",e); 
            throw new VlIOException("GridFTP:ClientException:"
                    + e.getMessage(), e);
        }

    }

    public String rename(String filepath, String newName,
            boolean nameIsPath) throws VlException
    {
        logger.debugPrintf("rename:%s->%s\n",filepath,newName);

        String newpath = null;

        if (nameIsPath)
            newpath = newName;
        else
            newpath = VRL.dirname(filepath) + VRL.SEP_CHAR + newName;

        try
        {
            // Returns no information nor throws exceptions !!!:
            // this.fileInfo.setName(newName);

            synchronized (serverMutex)
            {
                // rename does no use datachannel (passive=false);
                this.setCheckMode2(false, false);
                client.rename(filepath, newpath);
            }

            return newpath;
        }
        catch (ServerException e)
        {
            this.serverException = true;
            throw new VlException("GFTP VlServerException", e
                    .getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlException("GFTP IOException", e.getMessage(),
                    e);
        }

    }

    public boolean delete(boolean isDir, String path)
            throws VlException
    {
        logger.debugPrintf("delete:%s\n",path);

        try
        {
            // Returns no information nor throws exceptions !!!:
            // this.fileInfo.setName(newName);

            String parentDir = VRL.dirname(path);
            String name = VRL.basename(path);

            synchronized (serverMutex)
            {
                // changedir/delete doesn't need data channel:
                setCheckMode2(false, false);
                client.changeDir(parentDir);

                if (isDir)
                    client.deleteDir((name));
                else
                    client.deleteFile(name);
            }

            return true;

        }
        catch (ServerException e)
        {
            this.serverException = true;
            throw new VlException("GFTP VlServerException", e
                    .getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlException("GFTP IOException", e.getMessage(),
                    e);
        }
    }

    public long getModificationTime(String path) throws VlException
    {
        //debug("getModificationTime:" + path);

        // Date date = null;

        synchronized (serverMutex)
        {
            MlsxEntry mlst = this.mlst(path);
            return _getModificationTime(mlst);
        }
    }

    public InputStream createInputStream(String filepath)
            throws VlException
    {
        // Create new GridFTP client for the inputstream for multithreaded
        // access !
        // this way multiple streams can be openend !

        GridFTPInputStream inps;
        try
        {
            inps = new GridFTPInputStream(
                    getValidGSSCredential(this.vrsContext),
                    getAuthorization(),
                    this.hostname, this.port, filepath,
                    usePassive(), // always passive ?
                    Session.TYPE_IMAGE, 
                    useDataChannelAuthentication());
        }
        catch (Exception e)
        {
            throw convertException(e);
        }

        return inps;
    }

    private Authorization getAuthorization()
    {
        return client.getAuthorization();
    }

    private static GSSCredential getValidGSSCredential(
            VRSContext context) throws GlobusCredentialException,
            GSSException, VlAuthenticationException
    {
        //debug("getGlobusCredentials");

        GridProxy proxy = context.getGridProxy();

        if (proxy==null)
        {
            throw new nl.uva.vlet.exception.VlAuthenticationException(
                    "NULL grid credential (Not set for this context)");
        }
        
        if (proxy.isValid() == false)
        {
            throw new nl.uva.vlet.exception.VlAuthenticationException(
                    "No valid grid credential found");
        }

        GlobusCredential globusCred = GlobusUtil.getGlobusCredential(proxy); 
        
        if (globusCred != null)
        {
            GlobusGSSCredentialImpl cred = new GlobusGSSCredentialImpl(
                    globusCred, GSSCredential.DEFAULT_LIFETIME);
            return cred;
        }

        throw new VlAuthenticationException(
                "Couldn't find proper GSI credentials (no proxy?)");

        // return null;
    }

    public OutputStream createOutputStream(String filepath)
            throws VlException
    {
        GridFTPOutputStream outps;

        try
        {
            /*
             * ( org.ietf.jgss.GSSCredential cred,
             * org.globus.gsi.gssapi.auth.Authorization auth, java.lang.String
             * host, int port, java.lang.String file, boolean passive, int type,
             * boolean reqDCAU );
             */

            outps = new GridFTPOutputStream(
                    getValidGSSCredential(this.vrsContext),
                    getAuthorization(), this.hostname, this.port,
                    filepath, false,// do not append !
                    usePassive(), // always passive
                    Session.TYPE_IMAGE, false);
            
//            outps = new GridFTPOutputStream(
//                    getValidGSSCredential(this.vrsContext),
//                    this.hostname, this.port,
//                    filepath, false);
            
        }
        catch (Exception e)
        {
            throw convertException(e);
        }

        return outps;
    }

    
    public VDir createDir(String dirpath, boolean force)
            throws VlException
    {
        logger.debugPrintf("createDir:%s\n",dirpath);

        String parentDir=VRL.dirname(dirpath); 
        
        // Check/Create parent first: 
        MlsxEntry parentEntry = mlst(parentDir);
        
        if (parentEntry==null)
        {
            if (force==true)
            {
                createDir(parentDir,true);
            }
            else
            {
                throw new ResourceCreationFailedException("Parent directory doesn't exist (use force==true) for directory:"+dirpath); 
            }
        }
        
        // create child
        MlsxEntry entry = mlst(dirpath);

        if (entry != null)
        {
            // exists

            if (_isDir(entry) == false)
            {
                throw new ResourceAlreadyExistsException(
                        "path already exists but is not a directory:"
                                + dirpath);
            }

            if (force == false)
            {
                throw new ResourceAlreadyExistsException(
                        "Directory already exists:" + dirpath);
            }
            else
            {
                // else keep existing

                return  getDir(dirpath); 
            }
        }

        try
        {
            /*
             * Note: makeDir needs PASSIVE (=GetMode) not PutMode.
             */
            synchronized (serverMutex)
            {
                // mkdir doesn't need data channel
                setCheckMode2(false, false);
                client.makeDir(dirpath);
            }

            logger.debugPrintf("created directory:%s\n",dirpath);

            return  getDir(dirpath); 
        }
        catch (ServerException e)
        {
            this.serverException = true;

            throw new VlServerException("GFTP ServerException\n"
                    + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlIOException("GFTP IOException\n"
                    + e.getMessage(), e);
        }

    }

    public VFSNode openLocation(VRL loc) throws VlException
    {
        logger.debugPrintf("openLocation:%s\n",loc); 
        
        try
        {
            String path = loc.getPath();

            if ((path == null) || (path.equalsIgnoreCase("")))
            {
                synchronized (serverMutex)
                {
                    // cd doesn't need data channel
                    setCheckMode2(false, false);

                    // empty path = default path which gftp server sets
                    path = client.getCurrentDir();
                }

            }
            else if (path.startsWith("~"))
            {
                path = this.homeDir + path.substring(1);
            }
            else if (path.startsWith("/~"))
            {
                path = this.homeDir + path.substring(2);
            }
            
            logger.debugPrintf("Open actual path=%s\n",path); 

            MlsxEntry entry = mlst(path); // ask for single fileinfo
            VFSNode node = null;

            if (entry == null)
            {
                throw new ResourceNotFoundException(
                        "Couldn't get path:'" + path + "'");
            }
            else if (_isDir(entry))
                node = new GftpDir(this, path, entry);
            else if (_isFile(entry))
                node = new GftpFile(this, path, entry);
            /*
             * else if (entry.isSoftLink()) { //remote GFTP does not resolve
             * link, show as file ! return new GftpFile(this, path, entry); }
             */
            else
                node = new GftpDir(this, path, entry);

            return node;
        }
        catch (ServerException e) // org.globus.ftp.exception.ServerException
        {
            this.serverException = true;
            throw new VlException("GFTP VlServerException",
                    "When accessing:" + loc + "\nMessage="
                            + e.getMessage(), e);
        }
        catch (IOException e) // java.io.IOException
        {
            throw new VlException("GFTP IOException",
                    "When accessing:" + loc + "\nMessage="
                            + e.getMessage(), e);
        }
    }

    public Vector<?> mlsd(String dirpath) throws VlException
    {
        // new feature: Blind As A Bat !
        if (useBlindMode() == true)
        {
            throw new nl.uva.vlet.exception.VlServerException(
                    "Remote Server Doesn't suport listing of directories()");
        }

        if (this.protocol_v1 == true)
            return fakeMlsd(dirpath);

        logger.debugPrintf("mlsd:%s\n",dirpath);

        // System.err.println("mlsd:"+dirpath);

        try
        {
            Vector<?> retval = null;
            // MSLD uses datachannel
            // bin? channel and check passive mode

            // _setCheckMode(myclient,false,false);
            if (client.exists(dirpath) == false)
            {
                throw new ResourceNotFoundException(
                        "Directory doesn't exists:" + dirpath);
            }

            if (usePassive())
            {
                GridFTPClient myclient = this.createGFTPClient();
                myclient.setPassiveMode(true);

                // _setCheckMode(myclient,true,true);
                retval = myclient.mlsd(dirpath);
                this.finalize(myclient); 
            }
            else
                synchronized (serverMutex)
                {
                    setCheckMode2(true, usePassive());
                    retval = client.mlsd(dirpath);
                }

            return retval;
        }
        catch (Exception e)
        {
            Global.debugPrintStacktrace(e);
            throw convertException(e);
        }

    }

    /** GridFTP V1.0 compatible list commando which mimics mlsd */
    private Vector<MlsxEntry> fakeMlsd(String path)
            throws VlException
    {
        logger.debugPrintf("fakeMlsd:%s\n",path);
        GridFTPClient myclient=null; 
        
        try
        {
            Vector<?> list1 = null;
            Vector<?> list2 = null;

            // old server: create private client for robuustness
            // slow but allows multithreaded browsing !
            myclient = this.createGFTPClient();
            myclient.setPassiveMode(usePassive());
            // _setCheckMode(myclient,false,false);

            if (myclient.exists(path) == false)
            {
                // return null;
                // msld must have existing directory:
                throw new ResourceNotFoundException("Couldn't stat:"
                        + path);
            }
            myclient.changeDir(path);
            // _setCheckMode(myclient,true,getUsePassive());
            // normal list
            list1 = myclient.list();

            // ***
            // GridFTP 1.0 PATCH
            // The normal list doens't always return hidden files.
            // Add extra query which return hidden files as well
            // (and only hidden files) if no .files are in list1
            // ***
            // update PASS

            boolean listHidden = false;

            if (explicitListHidden == true)
            {
                listHidden = true;

                if (list1 != null)
                {
                    for (Object o : list1)
                    {
                        FileInfo finf = (FileInfo) o;
                        String name = finf.getName();
                        if ((name != null)
                                && (_isXDir(name) == false)
                                && (name.startsWith(".") == true))
                        {
                            // System.err.println("Will not list hidden
                            // files:"+path);
                            logger.warnPrintf("List command returned hidden files. Will not  perform extra ls -ld .* !\n");
                            listHidden = false;
                            break;
                        }
                    }
                }
            }

            if (listHidden)
            {
                logger.debugPrintf("Listing hidden files:%s\n",path);

                myclient.setPassiveMode(usePassive());
                list2 = myclient.list("-d .*");
            }

            if ((list1 == null) || (list1.size() == 0))
            {
                if ((listHidden = false)
                        || ((listHidden == true) && ((list2 == null) || (list2
                                .size() == 0))))
                {
                    logger.debugPrintf("fakeMlsd(): NULL or empty list() for:%s\n",path); 
                    return null;
                }
            }

            // Use hashmap for sorted list
            Hashtable<String, MlsxEntry> entrys = new Hashtable<String, MlsxEntry>();

            for (Object o : list1)
            {
                FileInfo finf = (FileInfo) o;
                addHashedMslx(entrys, path, finf);
            }

            if (list2 != null)
            {
                for (Object o : list2)
                {
                    FileInfo finf = (FileInfo) o;
                    addHashedMslx(entrys, path, finf);
                }
            }

            // hashtable to vector:
            Vector<MlsxEntry> entrysv = new Vector<MlsxEntry>();

            for (Enumeration<String> keys = entrys.keys(); keys
                    .hasMoreElements();)
            {
                String key = (String) keys.nextElement();
                entrysv.add(entrys.get(key));
            }
            return entrysv;

        }
        catch (Exception e)
        {
            throw convertException(e);
        }
        finally
        {
            // ALWAYS CLEANUP
            this.finalize(myclient); 
        }
    }

    private static void addHashedMslx(
            Hashtable<String, MlsxEntry> entrys, String path,
            FileInfo finf)
    {
        String name = finf.getName();
        String filepath = path + "/" + name;

        MlsxEntry mslx = createMslx(filepath, finf);
        // put info there overwriting optional previous with same name:
        if (mslx != null)
            entrys.put(name, mslx);
        else
        {
            logger.errorPrintf("fakeMlsd(): NULL Mslx for:%s\n",filepath);
        }

    }

    /**
     * Synchronized (but multihreaded compatible) read from remote gftp file.
     * This read can be done in parallel (multithreaded) since 'extendedGet' is
     * used. GridFTP protocol specifies that multile extendedGets can de done on
     * the same file speeding up transfer. This protocol is used in parallel
     * (striped) transfer mode. This has not been tested.
     * 
     * @throws VlException
     * @throws IOException
     * @throws ClientException
     * @throws ServerException
     * @throws VlException
     */

    public int syncRead(String filepath, long fileOffset,
            byte buffer[], int off, int len) throws VlException
    {
//        debug("syncRead: " + len + " from:" + filepath + "#"
//                + fileOffset + "into: buffer[" + buffer.length + "]");

        // create prive client for extended read (parallel mode).

        try
        {
            GridFTPClient myclient = this.createGFTPClient();
            myclient.setPassiveMode(usePassive());

            // _setCheckMode(myclient,false,true);

            long size = myclient.getSize(filepath);

            if (fileOffset >= size)
                return -1;
            // return EOF (no Exception!)
            // throw new IOException("Trying to read beyond end of
            // file:"+filepath);

            // Create DataSink from buffer directly from provided byte buffer.

            // truncate to real file size:

            if (fileOffset + len > size)
                len = (int) (size - fileOffset);

            DataSinkBuffer dataSink = new DataSinkBuffer(buffer, off,
                    len);
            // Listen to Mark ?
            MarkerListener markerListener = null;
            // this.client.setMode(Session.TYPE_IMAGE);
            // client.setPassiveMode(true);

            logger.debugPrintf("syncRead: Starting streamreader: size,off,len=%d,%d,%d\n",
                                size,off,len);

            //
            // mode for ExtendedGet :
            //
            // _setCheckMode(myclient,true,getUsePassive());
            myclient.extendedGet(filepath, fileOffset, len, dataSink,
                    markerListener);

            // current written = read;
            int nrRead = dataSink.getNrOfBytesWritten();
            this.finalize(myclient); 

            if (nrRead!=len)
                throw new IOException("Invalid number of bytes read!!!"); 
            
            // Following is the same as (nrRead!=len) 
            // if (dataSink.isDone()==false)
            //    throw new IOException("Sychronisation Error. DataSink not done yet! \n"); 
                	
            
            logger.debugPrintf("syncRead: nrRead=%d\n", nrRead);
            
            return nrRead;
        }
        catch (ServerException e)
        {
            this.serverException = true;
            throw new VlServerException("GridFTPFS Server Exception:"
                    + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlIOException("GridFTPFS Server Exception:"
                    + e.getMessage(), e);
        }
        catch (ClientException e)
        {
            throw new VlIOException("GridFTPFS Client Exception:"
                    + e.getMessage(), e);
        }

    }

    /**
     * Synchronized (but multihreaded compatible) write to remote gftp file.
     * This write can be done in parallel (multithreaded) since 'extendedPut' is
     * used. GridFTP protocol specifies that multile extendedPuts can de done on
     * the same file speeding up transfer. This protocol is used in parallel
     * (striped) transfer mode. This has not been tested.
     * 
     * @throws VlException
     * @throws IOException
     * @throws ClientException
     * @throws ServerException
     * @throws VlException
     */
    public void syncWrite(String filepath, long fileOffset,
            byte buffer[], int off, int len) throws VlException
    {
        MarkerListener markerListener = new GftpMarkerListener(); // new
                                                                    // GftpMarker();

        // Create StreamReader:
        try
        {
            // Always give a syncWrite a private GridFTP client
            // Multiple writes can be done in parralel since
            // 'extendedPut' is used.

            OutputStreamDataSource dataSource = new OutputStreamDataSource(
                    len); // VFile.defaultStreamBufferSize);
            OutputStream outputStream = dataSource.getOutputStream();

            outputStream.write(buffer, off, len);
            // outputStream.flush();
            outputStream.close();// force EOF (?)

            // private client for extendedPut mode:
            GridFTPClient myclient = this.createGFTPClient();
            myclient.setPassiveMode(usePassive());
            myclient.extendedPut(filepath, fileOffset, dataSource,
                    markerListener);

            this.finalize(myclient); 

        }
        catch (ServerException e)
        {
            this.serverException = true;
            throw new VlServerException("GridFTPFS Server Exception:"
                    + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlIOException("GridFTPFS Server Exception:"
                    + e.getMessage(), e);
        }
        catch (ClientException e)
        {
            throw new VlIOException("GridFTPFS Client Exception:"
                    + e.getMessage(), e);
        }
    }

    /** Returns Mlsx Entry of (remote) filepath */
    public MlsxEntry mlst(String filepath) throws VlException
    {
        if (this.protocol_v1 == true)
            return fakeMlst(filepath);

        logger.debugPrintf("mlst:%s\n",filepath);

        // String dirpath = VRL.dirname(filepath);
        // String name = VRL.basename(filepath);

        try
        {
            MlsxEntry entry = null;

            synchronized (serverMutex)
            {
                // extra precausion to avoid upsetting the GridFTPClient
                setCheckMode2(false, false);
                if (this.client.exists(filepath) == false)
                {
                    return null;// mslt is also used in 'exists' // throw new
                                // ResourceNotFoundException("Couldn't
                                // stat:"+filepath);
                }

                // update passive for DATA channel mslt
                setCheckMode2(true, usePassive());
                entry = client.mlst(filepath);

                return entry;
            }

        }
        catch (Exception e)
        {
            this.serverException = true;
            Global.debugPrintStacktrace(e);
            throw convertException(e);
        }

    }

    /**
     * V1.0 compatible 'mlst' method Tries to get the FileInfo and converts it
     * to a MlsxEntry object.
     */
    private MlsxEntry fakeMlst(String filepath) throws VlException
    {
        logger.debugPrintf("fakeMlst:%s\n",filepath);
        // System.err.println("fakeMlst:"+filepath);
        boolean isRoot = false;

        String name = VRL.basename(filepath);
        String dirname = VRL.dirname(filepath);

        // root directory hack: stat "." directory of 'parent' directory "/";

        if (filepath.compareTo("/") == 0)
        {
            name = ".";
            dirname = "/";
            isRoot = true;
        }

        try
        {
            setCheckMode2(false, false);

            // use current client for existance to speed up 'exists'
            if (client.exists(filepath) == false)
            {
                logger.debugPrintf("fakeMlst: exists=false for:%s\n",filepath);
                // mslt is also used in 'exists' dont'// throw new
                // ResourceNotFoundException("Couldn't stat:"+filepath);

                // old servers aren't realiable: Return Dummy Root directory :
                if (isRoot)
                {
                    return this.createDummyMslx("/", true);
                }

                if (useBlindMode())
                {
                    logger.debugPrintf("BLINDMODE: returning dummy file object!\n");

                    //
                    // blind GFTP Server: return dummy file object.
                    // don't know if this is a directory or not
                    // 
                    return this.createDummyMslx(filepath, false);
                    // continue ?
                }
                else
                {
                    // mlst is used as 'stat' in exists.
                    // don't throw error but return null:
                    return null;
                }
            }

            // old server: create private client for robuustness
            GridFTPClient myclient;
            myclient = this.createGFTPClient();
            myclient.setPassiveMode(usePassive());

            myclient.changeDir(dirname);

            //debug("fakeMlst:before list:" + dirname);

            Vector<?> files = null;

            try
            {
                files = myclient.list(name);
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.WARN,e,"GFTP list() (I) failed.Retrying!\n"); 
            }

            if ((files == null) || (files.size() <= 0))
            {
                myclient.setPassiveMode(usePassive());

                // Gftp PATCH:
                // must use ls -d .* for hidden files
                // hidden files listing !

                if (name.startsWith("."))
                    files = myclient.list("-d .*");
                else
                    files = myclient.list();
                myclient.close();
            }

            if ((files == null) || (files.size() <= 0))
            {
                logger.errorPrintf("GFTP list() (II) failed again for %s\n",dirname);  
                return null;
            }

            for (Object o : files)
            {
                FileInfo finfo = (FileInfo) o;

                if (finfo.getName().compareTo(name) == 0)
                    return createMslx(filepath, finfo);
            }

            // since 'exists' returned true, this is an error
            // might be a bug in old v1.0 servers:
            logger.errorPrintf("fakeMlst: server says file exists, but didn't return it:%s",filepath);

            int i = 0;
            for (Object o : files)
            {
                FileInfo finfo = (FileInfo) o;
                i++;

                if (i > 3)
                    break;
            }
            return null;

        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN,e,"fakeMlst failed\n");
            throw convertException(e);
        }
    }

    /** Create simple directory or file Mslx without any other file attributes */
    private MlsxEntry createDummyMslx(String filepath, boolean isDir)
    {
        String typestr = MlsxEntry.TYPE_FILE;

        if (isDir)
            typestr = MlsxEntry.TYPE_DIR;

        String mstr = "mslx=" + "unix.owner=;" + "unix.group=;"
                + "type=" + typestr + ";" + "size=0;" + "unix.mode=;"
                + " " + filepath;

        // System.err.println("mslx="+mstr);
        // System.err.println("modify="+finfo.gegetTime());
        MlsxEntry entry = null;

        try
        {
            entry = new MlsxEntry(mstr);
            // extra Attributes ? :
            // entry.set(UNIX_MODE_STRING,finfo.getModeAsString());
            // entry.set(UNIX_MODE,finfo.getModeAsString());
            return entry;

        }
        catch (FTPException e)
        {
            logger.warnPrintf(">>>***Error: Exception:%s",e);
        }

        return null;

    }

    private static MlsxEntry createMslx(String filepath,
            FileInfo finfo)
    {
        /*
         * example Mslx. Note space before filepath:
         * mslx=unix.owner=ptdeboer;
         * unix.mode=0755;size=4096; perm=cfmpel;type=dir; unix.group=uva;
         * unique=fd06-1ee8001; modify=20070402124136; /var/scratch/ptdeboer
         */

        String typestr = MlsxEntry.TYPE_FILE;
        // make sure LAST part of name is returned !
        String fname = VRL.basename(finfo.getName());

        if (finfo.isDirectory() == true)
            typestr = MlsxEntry.TYPE_DIR;

        if (fname.compareTo(".") == 0)
            typestr = MlsxEntry.TYPE_CDIR;

        if (fname.compareTo("..") == 0)
            typestr = MlsxEntry.TYPE_PDIR;

        String mstr = "mslx="

                // null entries or 'type=' is NOT parsed (?)
                // +";"
                + "unix.owner=;" + "unix.group=;"

                + "type=" + typestr + ";" + "size=" + finfo.getSize()
                + ";" + "unix.mode=" + finfo.getModeAsString() + ";"
                // +"modify="+finfo.getTime()+";"
                + " " + filepath;

        // System.err.println("mslx="+mstr);
        // System.err.println("modify="+finfo.gegetTime());
        MlsxEntry entry = null;

        try
        {
            entry = new MlsxEntry(mstr);
            // extra Attributes ? :
            // entry.set(UNIX_MODE_STRING,finfo.getModeAsString());
            // entry.set(UNIX_MODE,finfo.getModeAsString());
        }
        catch (FTPException e)
        {
            logger.warnPrintf(">>>***Error: Exception:%s",e);
        }

        return entry;
    }

    public long getSize(String path) throws VlException
    {
        long size = 0;

        try
        {
            synchronized (serverMutex)
            {
                setCheckMode2(false, false);
                size = this.client.getSize(path);
                return size;
            }
        }
        catch (Exception e)
        {
            this.serverException = true;
            throw new VlServerException("Couldn't get size of file ("+this.getHostname()+"):"+path
                    +"\nError:" + e.getMessage(), 
                    e);
        }
    }

    // ========================================================================
    // Non Critical Methods
    // ========================================================================

    public boolean existsFile(String path)
    {
        try
        {
            VFile file = getFile(path);

            if (file != null)
                return true;
        }
        catch (VlException e)
        {
            logger.logException(ClassLogger.WARN,e,"existsFile exception\n");
        }

        return false;
    }

    public boolean existsDir(String path)
    {
        boolean retval = false;

        MlsxEntry entry;

        try
        {
            entry = mlst(path);

            if (entry != null)
            {
                retval = _isDir(entry);
            }
            else
            {
                logger.debugPrintf("existsDir: got NULL fileinfo for:%s\n",path);
            }

            // debug("existsDir Returning:" + retval);
        }
        catch (VlException e)
        {
            logger.logException(ClassLogger.WARN,e,"existsDir Exception\n");
        }

        return retval;
    }

    public String getHostname()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }

    /** Create remote file, by putting zero bytes in new file */

    public VFile createFile(String path, boolean force)
            throws VlException
    {
        String fullPath=resolvePath(path); 
        
        MlsxEntry entry = mlst(fullPath);
        
        if (entry != null)
        {
            // exists

            if (_isFile(entry) == false)
            {
                throw new ResourceAlreadyExistsException(
                        "Filepath already exists but is a file:"
                                + fullPath);
            }

            if (force == false)
            {
                throw new ResourceAlreadyExistsException(
                        "File already exists:" + fullPath);
            }

            delete(false, fullPath); // delete existing
        }
        // gftp doesn't have a createFile !
        // just write zero bytes to create the file

        try
        {
            NilSource nilsource = new NilSource();

            if (usePassive())
            {
                // give passive mode it's private client

                GridFTPClient myclient = this.createGFTPClient();
                myclient.setPassiveMode(true);
                myclient.put(fullPath, nilsource, null);
                finalize(myclient);

            }
            else
                synchronized (this.serverMutex)
                {
                    setCheckMode2(true, usePassive());
                    this.client.put(fullPath, nilsource, null);
                }
        }
        catch (Exception e)
        {
            throw new ResourceCreationFailedException("Couldn't create new file:"+fullPath,e); 
        }
        
        return getFile(fullPath);
    }


    public boolean isGftpCompatibleName(String name)
    {
        if ((name.compareTo(".") == 0) || (name.compareTo("..") == 0))
            return true;

        // No spaces allowed in some GFTP file/list commands or the GFTP server
        // chookes !
        for (int i = 0; i < name.length(); i++)
            if (name.charAt(i) == ' ')
                return false;

        return true;
    }

    
    @Override
    public GftpFile openFile(VRL vrl) throws VlException
    {
        return getFile(vrl.getPath());
    }
    
    public GftpFile getFile(String filepath) throws VlException
    {

        MlsxEntry entry = mlst(filepath);

        if ((entry == null) || (_isFile(entry) == false))
            return null;

        return new GftpFile(this, filepath, entry);
    }

    public VFSNode getChild(String filepath) throws VlException
    {
        MlsxEntry entry = mlst(filepath);

        if (entry == null)
            return null;

        if (_isDir(entry) == true)
        {
            return new GftpDir(this, filepath, entry);
        }
        else
        {
            return new GftpFile(this, filepath, entry);
        }
    }

    public boolean isFile(String filepath) throws VlException
    {

        MlsxEntry entry = mlst(filepath);

        if ((entry == null) || (_isFile(entry) == false))
            return false;

        return true;
    }

    public boolean isDir(String filepath) throws VlException
    {

        MlsxEntry entry = mlst(filepath);

        if ((entry == null) || (_isDir(entry) == false))
            return false;

        return true;
    }
    
    @Override
    public GftpDir openDir(VRL vrl) throws VlException
    {
        return getDir(vrl.getPath());
    }
    
    public GftpDir getDir(String dirpath) throws VlException
    {
        MlsxEntry entry = mlst(dirpath);

        if ((entry == null) || (_isDir(entry) == false))
        {
            logger.debugPrintf("getDir() failed for:%s, Entry=%s\n",dirpath,entry);
            return null;
        }

        return new GftpDir(this, dirpath, entry);

    }

    public GftpDir getParentDir(String dirPath) throws VlException
    {
        // Use '..' to get to parent dir. Is root ("/") save.

        // String fakepath = dirPath + VRL.sepChar + ".."; // cd ..
        String parentpath = VRL.dirname(dirPath);

        // Fetch:'..' which on unix filesystems is the Parent!
        MlsxEntry entry = mlst(parentpath);

        if (entry == null)
            return null;

        return new GftpDir(this, parentpath, entry);
    }

    public String toString()
    {
        return VRS.GFTP_SCHEME + "://" + this.getHostname() + ":"
                + this.getPort();
    }

    public void dispose()
    {
        try
        {
            this.disconnect();
        }
        catch (VlException e)
        {
            logger.logException(ClassLogger.WARN,e,"Exception during disconnect()\n"); 
        } 
    }

    // ========================================================================
    // Miscellanous
    // ========================================================================

    private VlException convertException(Throwable e)
            throws VlException
    {
        logger.logException(ClassLogger.DEBUG,e,"Converting Exception:%s",e); 
        
        String ename = e.getClass().getName();

        if (e instanceof VlException)
            ename = ((VlException) e).getName();

        String errtxt = e.getMessage();

        if (errtxt == null)
            errtxt = "";

        // Extra message:

        if ((errtxt.startsWith("Reply wait timeout") && (this
                .usePassive() == false)))
            errtxt = "Connection setup timed out when using Active Mode: Maybe active mode isn't possible.\n---\nMessage="
                    + errtxt;

        if (e instanceof ServerException)
        {
            // System.err.println("Custom
            // message="+((ServerException)e).getCustomMessage());
            // System.err.println(" cause e="+((ServerException)e).getCause());
            // Global.errorPrintln(this," cause
            // e="+((ServerException)e).getCause());

            // important: set status to Error so a reconnect will happen!
            serverException = true;
            return new VlServerException(errtxt, e);
        }
        else if (e instanceof ClientException)
        {
            return new VlException(ename, errtxt, e);
        }
        else if (e instanceof IOException)
        {
            return new VlException(ename, errtxt, e);
        }
        else if (e instanceof FTPException)
        {
            logger.errorPrintf("FTPException:%s\n",e); 
            return new VlException("IOException:" + ename, errtxt, e);
        }
        // pass my own:
        else if (e instanceof VlException)
        {
            return (VlException) e;
        }
        else
        {
            // Generic VlException wrapper:
            return new VlException(ename, errtxt, e);
        }
    }

    /**
     * From:
     * http://www.nextgen6.net/docs/proftpd/rfc/draft-ietf-ftpext-mlst-12.txt
     * 
     * <pre>
     * 7.5.5. The perm Fact
     * 
     *     The perm fact is used to indicate access rights the current FTP user
     *     has over the object listed.  Its value is always an unordered
     *     sequence of alphabetic characters.
     * 
     *         perm-fact    = &quot;Perm&quot; &quot;=&quot; *pvals
     *         pvals        = &quot;a&quot; / &quot;c&quot; / &quot;d&quot; / &quot;e&quot; / &quot;f&quot; /
     *                        &quot;l&quot; / &quot;m&quot; / &quot;p&quot; / &quot;r&quot; / &quot;w&quot;
     * 
     *     There are ten permission indicators currently defined.  Many are
     *     meaningful only when used with a particular type of object.  The
     *     indicators are case independent, &quot;d&quot; and &quot;D&quot; are the same indicator.
     * 
     *     The &quot;a&quot; permission applies to objects of type=file, and indicates
     *     that the APPE (append) command may be applied to the file named.
     * 
     *     The &quot;c&quot; permission applies to objects of type=dir (and type=pdir,
     *     type=cdir).  It indicates that files may be created in the directory
     *     named.  That is, that a STOU command is likely to succeed, and that
     *     STOR and APPE commands might succeed if the file named did not
     *     previously exist, but is to be created in the directory object that
     *     has the &quot;c&quot; permission.  It also indicates that the RNTO command is
     *     likely to succeed for names in the directory.
     * 
     *     The &quot;d&quot; permission applies to all types.  It indicates that the
     *     object named may be deleted, that is, that the RMD command may be
     *     applied to it if it is a directory, and otherwise that the DELE
     *     command may be applied to it.
     * 
     *     The &quot;e&quot; permission applies to the directory types.  When set on an
     *     object of type=dir, type=cdir, or type=pdir it indicates that a CWD
     *     command naming the object should succeed, and the user should be able
     *     to enter the directory named.  For type=pdir it also indicates that
     *     the CDUP command may succeed (if this particular pathname is the one
     *     to which a CDUP would apply.)
     * 
     *     The &quot;f&quot; permission for objects indicates that the object named may be
     *     renamed - that is, may be the object of an RNFR command.
     * 
     *     The &quot;l&quot; permission applies to the directory file types, and indicates
     *     that the listing commands, LIST, NLST, and MLSD may be applied to the
     *     directory in question.
     * 
     *     The &quot;m&quot; permission applies to directory types, and indicates that the
     *     MKD command may be used to create a new directory within the
     *     directory under consideration.
     * 
     *     The &quot;p&quot; permission applies to directory types, and indicates that
     *     objects in the directory may be deleted, or (stretching naming a
     *     little) that the directory may be purged.  Note: it does not indicate
     *     that the RMD command may be used to remove the directory named
     *     itself, the &quot;d&quot; permission indicator indicates that.
     * 
     *     The &quot;r&quot; permission applies to type=file objects, and for some
     *     systems, perhaps to other types of objects, and indicates that the
     *     RETR command may be applied to that object.
     * 
     *     The &quot;w&quot; permission applies to type=file objects, and for some
     *     systems, perhaps to other types of objects, and indicates that the
     *     STOR command may be applied to the object named.
     *     
     * </pre>
     */

    public static class PermissionInfo
    {
        /** File permission: 'a' for appendable */
        boolean appendable = false;

        /** Directory permission: 'c' for can create files */
        boolean cancreatefiles = false;

        /** File/Directory permission: 'd' for deletable (itself) */
        boolean deletable = false;

        /** Directory permission: 'e' enterable (cd into is allowed */
        boolean enterable = false;

        /** File/Directory permission: 'f' for renamable (itself) */
        boolean renamable = false;

        /** Directory permission: 'l' for listable */
        boolean listable = false;

        /** Directory permission: 'm' for can create dir (mkdir) */
        boolean cancreatedirs = false;

        /** Dir permission: 'p' for files subdirs can be deleted (purged) */
        boolean canbepurged = false;

        /** File permission: 'r' for readable */
        boolean readable = false;

        /** File permission: 'w' for writable */
        boolean writable = false;

        public PermissionInfo(String str)
        {
            if (str == null)
                return;

            str = str.toLowerCase();

            for (int i = 0; i < str.length(); i++)
            {
                char c = str.charAt(i);
                switch (c)
                {
                    case 'a':
                        appendable = true;
                        break; // file
                    case 'c':
                        cancreatefiles = true;
                        break; // dir
                    case 'd':
                        deletable = true;
                        break; // dir
                    case 'e':
                        enterable = true;
                        break; // file/dir
                    case 'f':
                        renamable = true;
                        break; // file/dir
                    case 'l':
                        listable = true;
                        break; // dir
                    case 'm':
                        cancreatedirs = true;
                        break; // dir
                    case 'p':
                        canbepurged = true;
                        break; // dir
                    case 'r':
                        readable = true;
                        break; // file
                    case 'w':
                        writable = true;
                        break; // file
                    default:
                        break; // ignore
                }

            }
        }

        public static PermissionInfo fromString(String str)
        {
            // Null in Null out 
            if (str == null)
                return null;

            PermissionInfo perm = new PermissionInfo(str);

            return perm;
        }

        public String toString()
        {
            return "" + ((appendable) ? "a" : "-")
                    + ((cancreatefiles) ? "c" : "-")
                    + ((deletable) ? "d" : "-")
                    + ((enterable) ? "e" : "-")
                    + ((renamable) ? "f" : "-")
                    + ((listable) ? "l" : "-")
                    + ((canbepurged) ? "p" : "-")
                    + ((cancreatedirs) ? "m" : "-")
                    + ((appendable) ? "r" : "-")
                    + ((writable) ? "w" : "-");

        }

    }

    public static boolean _isReadable(MlsxEntry entry)
    {
        PermissionInfo perm = PermissionInfo.fromString(entry
                .get(MlsxEntry.PERM));

        if (perm == null)
            return false;

        // files must be 'readable' and directories must be 'listable'

        return (perm.readable || perm.listable);

    }

    public static boolean _isAccessable(MlsxEntry entry)
    {
        PermissionInfo perm = PermissionInfo.fromString(entry
                .get(MlsxEntry.PERM));

        if (perm == null)
            return false;

        // files must be 'readable' and directories must be 'listable'

        return (perm.readable || perm.listable);
    }

    public static boolean _isWritable(MlsxEntry entry)
    {
        PermissionInfo perm = PermissionInfo.fromString(entry
                .get(MlsxEntry.PERM));

        if (perm == null)
            return false;

        // files must be 'readable' and directories must be able to create new
        // nodes

        return (perm.writable || (perm.cancreatedirs && perm.cancreatefiles));
    }

    static boolean _isDir(MlsxEntry entry)
    {
        String val = entry.get(MlsxEntry.TYPE);

        if (val == null)
            return false;

        return (val.compareTo(MlsxEntry.TYPE_DIR) == 0);
    }

    static boolean _isXDir(String dirName)
    {
        if (dirName.compareTo(".") == 0) // Current Dir
            return true;

        if (dirName.compareTo("..") == 0) // Parent Dir
            return true;

        return false;
    }

    static boolean _isXDir(MlsxEntry entry)
    {
        String val = entry.get(MlsxEntry.TYPE);

        if (val == null)
            return false;

        if (val.compareTo(MlsxEntry.TYPE_CDIR) == 0) // Current Dir
            return true;

        if (val.compareTo(MlsxEntry.TYPE_PDIR) == 0) // Parent Dir
            return true;

        return false;
    }

    static boolean _isFile(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return true;

        String val = entry.get(MlsxEntry.TYPE);

        if (val == null)
            return false;

        return (val.compareTo(MlsxEntry.TYPE_FILE) == 0);
    }

    public static long _getLength(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return -1;

        String val = entry.get(MlsxEntry.SIZE);
        //debug("_getLength val=" + val);

        if (val == null)
        {
            //debug("SIZE=null in entry:" + entry);
            return 0;
        }

        long lval = Long.valueOf(val);

        return lval;
    }

    public static String _getUnique(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return "";

        String val = entry.get(MlsxEntry.UNIQUE);
        //debug("_getUnique val=" + val);

        if (val == null)
        {
            //debug("UNIQUE=null in entry:" + entry);
            return null;
        }

        return val;

        // long lval = Long.valueOf(val);
        // return lval;
    }

    public static long _getModificationTime(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return -1;

        String val = entry.get(MlsxEntry.MODIFY);
        ///debug("_getModificationTime val=" + val);

        if (val == null)
        {
            //debug("SIZE=null in entry:" + entry);
            return 0;
        }

        return timeStringToMillis(val);
    }

    public static long _getCreationTime(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return -1;

        String val = entry.get(MlsxEntry.CREATE);
        //debug("_getCreationTime val=" + val);

        if (val == null)
        {
            //debug("CREATE=null in entry:" + entry);
            return 0;
        }

        return timeStringToMillis(val);
    }

    public static long timeStringToMillis(String val)
    {
        // value is in YYYYMMDDhhmmss
        int YYYY = Integer.valueOf(val.substring(0, 4));
        int MM = Integer.valueOf(val.substring(4, 6));
        int DD = Integer.valueOf(val.substring(6, 8));
        int hh = Integer.valueOf(val.substring(8, 10));
        int mm = Integer.valueOf(val.substring(10, 12));
        int ss = Integer.valueOf(val.substring(12, 14));

        // GMT TIMEZONE:
        TimeZone gmtTZ = TimeZone.getTimeZone("GMT-0:00");

        Calendar cal = new GregorianCalendar(gmtTZ);
        // O-be-1-kenobi: month nr in GregorianCalendar is zero-based

        cal.set(YYYY, MM - 1, DD, hh, mm, ss);
        // TimeZone localTZ=Calendar.getInstance().getTimeZone();
        // cal.setTimeZone(localTZ);

        return cal.getTimeInMillis();
    }

    private static String _getGroup(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return null;

        String val = entry.get(UNIX_GROUP);
        //debug("_getModificationTime val=" + val);

        if (val == null)
        {
            //debug("UNIX_GROUP=null in entry:" + entry);
            return null;
        }

        return val;
    }

    private static String _getOwner(MlsxEntry entry)
    {
        // gftp 1.0 hack:
        if (entry == null)
            return null;

        String val = entry.get(UNIX_OWNER);
        //debug("_getModificationTime val=" + val);

        if (val == null)
        {
            //debug("UNIX_GROUP=null in entry:" + entry);
            return null;
        }

        return val;
    }

    /**
     * Optimized getAttribute. Will use MslxEntry or update MlsxEntry of the
     * specified GridFTP Node. The update parameter is used when fetching
     * muliple attributes.
     * 
     * Method will return null if attribute isn't an (optimized) GridFTP
     * attribute
     * 
     */
    public static VAttribute getAttribute(MlsxEntry entry, String name)
            throws VlException
    {

        if (name.compareTo(ATTR_PERMISSIONS_STRING) == 0)
        {
            boolean isDir = _isDir(entry);

            // My added attribute:

            String val = entry.get(UNIX_MODE);
            if (isEmpty(val) == false)
            {
                //debug("> unix_mode=" + val);
                // Parse Octal String:
                int mode = Integer.parseInt(val, 8);

                val = VFS.modeToString(mode, isDir);
            }
            else
            {
                String permstr = entry.get(MlsxEntry.PERM);
                //debug("> permstr=" + permstr);
                val = (isDir == true ? "d" : "-")
                        + new PermissionInfo(permstr).toString();
            }
            // append extra '-'

            for (int i = 0; i < 5; i++)
                if (val.length() < i)
                    val = val + "-";

            return new VAttribute(name, val);
        }
        else if (name.compareTo(ATTR_MODIFICATION_TIME) == 0)
        {
            return VAttribute.createDateSinceEpoch(name, GftpFileSystem
                    ._getModificationTime(entry));

        }
        else if (name.compareTo(ATTR_CREATION_TIME) == 0)
        {
            return new VAttribute(VAttributeType.TIME, name,
                    GftpFileSystem._getCreationTime(entry));

        }
        /***********************************************************************
         * else if (name.compareTo(ATTR_MODIFICATION_TIME_STRING)==0) { return
         * new VAttribute(name,millisToDateTimeString(
         * GftpServer._getModificationTime(entry))); }
         **********************************************************************/
        else if (name.compareTo(ATTR_LENGTH) == 0)
        {
            return new VAttribute(name, GftpFileSystem._getLength(entry));

        }
        else if (name.compareTo(ATTR_OWNER) == 0)
        {
            return new VAttribute(name, GftpFileSystem._getOwner(entry));

        }
        else if (name.compareTo(ATTR_GROUP) == 0)
        {
            return new VAttribute(name, GftpFileSystem._getGroup(entry));

        }
        else if (name.compareTo(ATTR_UNIQUE) == 0)
        {
            return new VAttribute(name, GftpFileSystem._getUnique(entry));

        }

        return null;
    }

    private static boolean isEmpty(String val)
    {
        if (val == null)
            return true;

        if (val.compareTo("") == 0)
            return true;

        return false;
    }

    public VRL getServerVRL()
    {
        // IMPORTANT: Only specify port in VRL if NOT the default port.
        // this to avoid mismatched in the created accountID in ServerInfo
        // As a 'feature' users can leave out the port number, but then
        // a differenct accountID is also created in ServerInfo !
        int lport = 0;

        if (port == VRS.DEFAULT_GRIDFTP_PORT)
            lport = 0;
        else
            lport = port;

        return new VRL(VRS.GFTP_SCHEME, null, hostname, lport, null);
    }

    public String getScheme()
    {
        return VRS.GFTP_SCHEME;
    }

    public boolean isConnected()
    {
        // whether the client really is alive shouldn't be checked here
        return client != null;
    }
    

    @Override
    public VDir newDir(VRL dirVrl) throws VlException 
    {
        return new GftpDir(this,dirVrl.getPath()); 
    }

    @Override
    public VFile newFile(VRL filePath) throws VlException 
    {
        return new GftpFile(this,filePath.getPath());
    }

    public VFile do3rdPartyTransferToOther(ITaskMonitor monitor,GftpFile fromGftpFile, VRL toTargetLocation) 
           throws VlException 
    {
        GftpFileSystem targetServer = GftpFileSystem.getOrCreateServerFor(this.vrsContext,toTargetLocation); 
    
        // Do Server to Server 3rd Party Transfer
        return doActive3rdPartyTransfer(monitor,
                                        fromGftpFile.gftpServer,
                                        fromGftpFile.getVRL(),
                                        targetServer,
                                        toTargetLocation);
    }

    public VFile do3rdPartyTransferFromOther(ITaskMonitor monitor,VRL fromRemoteSource, GftpFile toGftpFile) 
           throws VlException
    {
        GftpFileSystem sourceServer = GftpFileSystem.getOrCreateServerFor(this.vrsContext,fromRemoteSource); 
        
        // Do Server to Server 3rd Party Transfer: Source is Active party: 
        return doActive3rdPartyTransfer(monitor,
                                        sourceServer,
                                        fromRemoteSource,
                                        toGftpFile.gftpServer,
                                        toGftpFile.getVRL()); 
    }
    
    /**
     * Initiate 3rd party transfer from sourceServer to targetServer . 
     * 
     * @param sourceServer   source GFTP FileSystem 
     * @param sourceVrl      source VRL of file
     * @param targetServer   target GFTP FileSystem 
     * @param targetVrl      target VRL of file
     * @return new Target VFile object 
     * @throws VlException 
     */
    public VFile doActive3rdPartyTransfer(ITaskMonitor monitor,GftpFileSystem sourceServer,VRL sourceVrl, 
            GftpFileSystem targetServer,VRL targetVrl) 
            throws VlException
    {
 
        String formatstr="GFTP: Performing 3rd party transfer:%s:%d => %s:%d\n";
                           
        monitor.logPrintf(formatstr,sourceServer.getHostname(),sourceServer.getPort(),
                targetServer.getHostname(),targetServer.getPort() );
        logger.debugPrintf(formatstr,sourceServer.getHostname(),sourceServer.getPort(),
                targetServer.getHostname(),targetServer.getPort() );
        
        String sourcefilepath = sourceVrl.getPath();
        String destfilepath = targetVrl.getPath();
        
        // Create private GridFTP clients ! 
        GridFTPClient privateSourceClient = sourceServer.createGFTPClient(); 
        GridFTPClient privateTargetClient = targetServer.createGFTPClient();
        
        // gsiftp/gftp 1.0 compatibility
        // Both must support DCAU. If one of them doesn't: disable at the other ! 
        //  
        boolean sourceDCAU=sourceServer.useDataChannelAuthentication(); 
        boolean destDCAU=targetServer.useDataChannelAuthentication(); 
        
        //debug("sourceDCAU="+sourceDCAU);
        //debug("destDCAU="+destDCAU);
        
        long sourceSize=-1;
        // not all storage elements like this: 
        try
        {
            sourceSize=sourceServer.getSize(sourcefilepath); 
        }
        catch (Exception e)
        {
            logger.warnPrintf("Warning could not get size of file:%s\n",sourcefilepath); 
        }
        
        // both must be true or false. 
        if (sourceDCAU!=destDCAU) 
        {
            monitor.logPrintf("---\nGFTP: Warning: Data Channel Authentication mismatch between source: "+sourceServer.getHostname()+" and destination:"+targetServer.getHostname()+"\n---\n");
            logger.warnPrintf("DCAU Mismatch between source and target: Disabling DCAU");
            
            privateSourceClient.setLocalNoDataChannelAuthentication();
            privateTargetClient.setLocalNoDataChannelAuthentication();
            
            if ((sourceDCAU==false) && (destDCAU==true))
            {
                try
                {
                    privateTargetClient.setDataChannelAuthentication(DataChannelAuthentication.NONE);
                }
                catch (ServerException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
                
            if ((destDCAU==false) && (sourceDCAU==true))
            {
                try
                {
                    privateSourceClient.setDataChannelAuthentication(DataChannelAuthentication.NONE);
                }
                catch (ServerException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        ActionTask monitorTask=null; 
        //privateSourceClient.setLocalNoDataChannelAuthentication();
        //privateTargetClient.setLocalNoDataChannelAuthentication();
        VlException transferEx=null; 
        try
        {
            // Todo: remote file stats ! (just use 1 for now) 
            monitor.startSubTask("GFTP: Performing 3rd party transfer",sourceSize); 
            
            //MarkerListener markerListener = new GftpMarkerListener(); // new        // GftpMarker();

            // can we used striped mode here ?
            // see: http://www.globus.org/cog/jftp/guide.html
            // do NOT set Passive/Active mode the servers will do that themselves 
            
            // sourceClient.setPassive();
            // sourceClient.setLocalActive();

            // targetClient.setLocalPassive();
            // targetClient.setActive();

            // ============
            //
            // Passive/Active mode:
            // Transfers are initiated from the source and the
            // destination MUST support active mode or else the 3rd party
            // transfer doesn't work and the direction must be reversed.
            // In that case the other server must allow active mode because if
            // neither of the two servers allow active mode no transfer can 
            // be made at all!
            //
            // (If both client and server are passive, none of them is active,
            // duh!)
            // ============
            
            monitorTask=start3rdPartyMonitor(targetServer,monitor,targetVrl);
            
            TransferMarkerlistener listener=new TransferMarkerlistener(this); 
            privateSourceClient.transfer(sourcefilepath, privateTargetClient, destfilepath, false, listener);
        }
        catch (Throwable e)
        {
            logger.logException(ClassLogger.ERROR,e,"Exception during 3rd party transfer\n"); 
            monitor.logPrintf("*** Error: Exception during 3rd party transfer:"+e+"\n");
            VlException vle = convertException(e);
            monitor.setException(vle);
            // keep exception. 
            transferEx=vle; 
        }
        
        if (monitorTask!=null)
        {
            monitorTask.signalTerminate();
        }
        
        // CLEANUP !
        finalize(privateTargetClient);
        finalize(privateSourceClient);
        
        monitor.endSubTask(null);
        
        if (transferEx!=null)
            throw transferEx; 
        
        monitor.logPrintf("GFTP: Finished 3rd party transfer.\n");
       
        // should be there: 
        
        return targetServer.openFile(targetVrl);
    }
    
    /** 
     * Start 3rd party transfer monitor which cehcks the size of the targetfile
     */  
    private ActionTask start3rdPartyMonitor(GftpFileSystem targetServer,
            final ITaskMonitor monitor,final VRL targetVrl)
    {
        
        final GridFTPClient privateClient;
        
        try
        {
            privateClient = targetServer.createGFTPClient();
        }
        catch (VlException e1)
        {
            logger.logException(ClassLogger.ERROR,e1,"Failed to start background monitor for third party transfer to:%s\n",targetVrl); 
            return null;  
        }
        
        ActionTask monitorTask=new ActionTask(null,"GftpFileSystem: Monitoring 3d party transfer to:"+targetVrl)
        {
            boolean mustStop=false; 
           
            @Override
            protected void doTask() throws VlException
            {
                int numTries=10; 
                
                while(mustStop==false)
                {
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    
                    try
                    {
                        long size=privateClient.getSize(targetVrl.getPath());
                        monitor.updateSubTaskDone(size); 
                    }
                    catch (Exception e)
                    {
                        numTries--; 
                        
                        // total try time = 10 x 5s = 50 seconds:
                        
                        if (numTries<=0)
                        {
                            this.setException(e); 
                            logger.logException(ClassLogger.ERROR,e,"Failed to start background monitor for third party transfer to:%s",targetVrl);
                            mustStop=true;
                        }
                    }
                }
            }

            @Override
            public void stopTask()
            {
                mustStop=true; 
            }
            
        };
        
        monitorTask.startTask(); 
        
        return monitorTask; 
    }

    public void finalize(GridFTPClient gftpClient)
    {
        if (gftpClient==null)
            return; 
        
        try
        {
            gftpClient.close();
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN,e,"Exception when closing target server %s:%i\n",gftpClient.getHost(),gftpClient.getPort()); 
        }
    }
    
    /**
     * Returns the checksum of a file.
     * 
     * @param algorithm, the checksume algorithm 
     * @param offset
     * @param length
     * @param file
     * @return
     * @throws VlException
     */
    public String getChecksum(String algorithm,long offset,long length, String file) throws VlException
    {
        
        if (algorithm==null || algorithm.equalsIgnoreCase(""))
        {
            return null;
        }
        
        ChecksumAlgorithm checksumAlgorithm = new ChecksumAlgorithm(algorithm);
        
        try
        {
            return client.checksum(checksumAlgorithm, offset, length, file);
        }
        catch (ServerException e)
        {
            throw new VlServerException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
    }
}
