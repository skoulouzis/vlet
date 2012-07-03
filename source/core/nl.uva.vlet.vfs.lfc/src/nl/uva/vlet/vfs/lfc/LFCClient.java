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
 * $Id: LFCClient.java,v 1.10 2011-06-07 15:15:11 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:11 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.LongHolder;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VRLList;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceException;
import nl.uva.vlet.exception.ResourceLinkIsBorkenException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.ResourceWriteAccessDeniedException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.exception.VlInterruptedException;
import nl.uva.vlet.glite.lfc.LFCConfig;
import nl.uva.vlet.glite.lfc.LFCException;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.SSHUtil;
import nl.uva.vlet.util.bdii.BdiiException;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VFileActiveTransferable;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vfs.lfc.LFCFSConfig.ReplicaCreationMode;
import nl.uva.vlet.vfs.lfc.LFCFSConfig.ReplicaSelectionMode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VActiveTransferable;
import nl.uva.vlet.vrs.VDeletable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VResourceSystem;
import nl.uva.vlet.vrs.io.VStreamAccessable;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.vlet.uva.grid.globus.GlobusUtil;


/** 
 */
public class LFCClient
{
    private static ClassLogger lfcLogger;

    private static ClassLogger logger;

    static
    {
        // Override LFC Logger with my Logger!
        lfcLogger = ClassLogger.getLogger("nl.uva.vlet.vfs.lfc");
        LFCServer.setLogger(lfcLogger);

        logger = ClassLogger.getLogger(LFCClient.class);
    }

    protected static Random randomizer = new Random();

    static String[] lfcDirAttributeNames =
    { VAttributeConstants.ATTR_GID, VAttributeConstants.ATTR_UID, VAttributeConstants.ATTR_GRIDUID,
            VAttributeConstants.ATTR_UNIX_FILE_MODE, VAttributeConstants.ATTR_PERMISSIONS_STRING,
            VAttributeConstants.ATTR_CREATION_TIME, VAttributeConstants.ATTR_MODIFICATION_TIME,
            VAttributeConstants.ATTR_ACCESS_TIME, LFCFSFactory.ATTR_LFC_FILEID, LFCFSFactory.ATTR_LFC_FILECLASS,
            LFCFSFactory.ATTR_LFC_ULINK, LFCFSFactory.ATTR_LFC_STATUS, LFCFSFactory.ATTR_LFC_COMMENT, };

    static String[] lfcFileAttributeNames =
    { VAttributeConstants.ATTR_GID, VAttributeConstants.ATTR_UID, VAttributeConstants.ATTR_GRIDUID,
            VAttributeConstants.ATTR_UNIX_FILE_MODE, VAttributeConstants.ATTR_PERMISSIONS_STRING,
            VAttributeConstants.ATTR_CREATION_TIME, VAttributeConstants.ATTR_MODIFICATION_TIME,
            VAttributeConstants.ATTR_ACCESS_TIME, LFCFSFactory.ATTR_LFC_FILEID, LFCFSFactory.ATTR_LFC_FILECLASS,
            LFCFSFactory.ATTR_LFC_ULINK, LFCFSFactory.ATTR_LFC_STATUS, LFCFSFactory.ATTR_LFC_COMMENT,
            // LFCFSFactory.ATTR_LFC_CHECKSUMTYPE,
            // LFCFSFactory.ATTR_LFC_CHECKSUMVALUE,
            LFCFSFactory.ATTR_NUM_REPLICAS, LFCFSFactory.ATTR_LFC_REPLICAHOSTS };

    // ========================================================================
    //
    // ========================================================================
    // private MLFCServer server;
    private URI serverUri;

    protected LFCFileSystem lfcServerNode;

    VFSClient vfsClient;

    private String cachedVO;

    private Map<String, StorageArea> cachedSAs;

    private LFCConfig lfcConfig = new LFCConfig();

    private URI sshTunnelUri = null;

    public LFCClient(String uriStr) throws VlException
    {
        try
        {
            serverUri = createServerURI(uriStr);
            if (serverUri.getPort() <= 0)
            {
                serverUri = new URI(serverUri.getScheme(), serverUri.getHost(), serverUri.getUserInfo(), 5010,
                        serverUri.getPath(), null, null);
            }

            // server = new MLFCServer(serverUri);
        }
        catch (URISyntaxException ex)
        {
            throw new VRLSyntaxException(ex);
        }
        init();
    }

    public LFCClient(LFCFileSystem node, String host, int port) throws VlException
    {
        this.lfcServerNode = node;

        if (port <= 0)
            port = VRS.DEFAULT_LFC_PORT;

        try
        {
            serverUri = createServerURI(host, port);
            // serverUri = createServerURI(host);

            // server = new MLFCServer(serverUri);

            logger.debugPrintf("New server:%s host:%s port:%d\n", serverUri, host, port);

        }
        catch (URISyntaxException e)
        {
            throw new nl.uva.vlet.exception.VRLSyntaxException(e);
        }

        init();
    }

    private void init() throws VlException
    {
        initShhTunnel();
    }

    private void initShhTunnel() throws VlException
    {
        if (this.getVRSContext().getConfigManager().getUseSSHTunnel(VRS.LFN_SCHEME, serverUri.getHost(),
                serverUri.getPort()))
        {
            int lport = SSHUtil.createSSHTunnel(getVRSContext(), VRS.LFN_SCHEME, serverUri.getHost(), serverUri
                    .getPort());

            try
            {
                sshTunnelUri = new URI(serverUri.getScheme() + "://localhost:" + lport);
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
    }

    private URI createServerURI(String host) throws URISyntaxException
    {
        return new URI(host);
    }

    private URI createServerURI(String host, int port) throws URISyntaxException
    {
        return new URI("lfn://" + host + ":" + port + "/");
    }

    private void debug(String msg)
    {
        logger.debugPrintf(msg + "\n");
    }

    private void info(String msg)
    {
        logger.infoPrintf(msg + "\n");
    }

    public void connect() throws VlException
    {

        if (!isConnected())
        {
            // try
            // {
            // server.connect();
            // }
            // catch (LFCException e)
            // {
            // throw new nl.uva.vlet.exception.VlServerException(
            // "Connection to: " + serverUri + " failed.", e);
            // }
        }
    }

    public void connect(boolean closeFirst) throws VlException
    {
        // if (server != null)
        // {
        // if (closeFirst)
        // {
        // disconnect();
        // connect();
        // }
        // else
        // {
        // connect();
        // }
        //
        // }

        // if (this.getVRSContext().getGridProxy().isValid() == false)
        // throw new nl.uva.vlet.exception.AuthenticationException(
        // "Invalid grid proxy");

    }

    public void disconnect() throws VlException
    {
        // nothing to disconnect. For each connection a new LFCServer is created. 
        if (isConnected())
        {
            // try
            // {
            // dispose(server);
            // }
            // catch (LFCException e)
            // {
            // throw new VlException(e);
            // }
        }
    }

    public boolean isConnected()
    {
        return true;
        // return server.isConnected();
    }

    String getServerProperty(String name)
    {
        // get from Parent (ServerNode) and use ServerInfo Description
        // to get ServerProperty from VBrowser configured Resource.
        // 
        return this.lfcServerNode.getServerInfo().getProperty(name);
    }

    public VFSNode openLocation(VRL loc) throws VlException
    {
        logger.debugPrintf("--- Open location:%s\n", loc);
        connect(false);

        if (getVRSContext().getGridProxy().isValid() == false)
        {
            throw new nl.uva.vlet.exception.VlAuthenticationException("Invalid Grid Proxy. Please create one first");
        }

        if (loc.hasScheme(VRS.GUID_SCHEME))
        {
            return getFileByGUID(loc.getPath());
        }
        else
        {
            return getPath(loc.getPath());
        }
    }

    public VFSNode getPath(String path) throws VlException
    {
        LFCServer server = createServer();
        VFSNode node=getPath(path, server);
        dispose(server);
        return node; 
    }

    public VFSNode getFileByGUID(String guid) throws VlException
    {
        LFCServer server = createServer();
        VFSNode node=getFileByGuid(guid, server);
        dispose(server);
        return node; 
    }

    protected VFSNode getPath(String path, LFCServer server) throws VlException
    {
        FileDescWrapper wrapper = null;
        if (path.startsWith("/~"))
        {
            // wrapper = getHome(server);
            String voPath = getDefaultVOHome();
            if (voPath != null)
            {
                path = path.replaceFirst("/~", voPath);
            }
            else
            {
                // remove '~'
                path = "/" + path.substring(2);
            }

        }
        // PTdB: Link Handling: Since the caller might be interested
        // whether this path is a link, do not resolve links here.
        // other method have been updated to check whether to resolve
        // the link or not!
        wrapper = queryPath(server,path, false);

        if (wrapper.getFileDesc().isDirectory())
        {
            return new LFCDir(lfcServerNode, wrapper);
        }
        else
        {
            try
            {
                return new LFCFile(lfcServerNode, wrapper);
            }
            catch (Throwable t)
            {
                logger.errorPrintf("Error:%s\n", t);
                t.printStackTrace();
                throw new VlInternalError(t); // wrap
            }
        }

    }

    protected LFCFile getFileByGuid(String guid, LFCServer server) throws VlException
    {
        if (guid == null)
            throw new NullPointerException("GUID parameter can not be null");

        if (guid.startsWith("/"))
        {
            guid = guid.replace("/", "");
        }

        // ===
        // get file by guid: returned file description is
        // always a resolved link and never a directory.
        // ===

        try
        {
            FileDesc fdesc = server.fetchFileDesc(guid, true);
            logger.debugPrintf("File name of GUID '%s' = %s\n", guid, fdesc.getFileName());

            FileDescWrapper wrapper = new FileDescWrapper(fdesc, guid);

            LFCFile file = new LFCFile(lfcServerNode, wrapper);
            file.setGUIDVRL(new VRL(VRS.GUID_SCHEME + ":" + guid));
            return file;
        }
        catch (VlException e)
        {
            throw new ResourceException("LFC Exception", "Couldn't fetch file with guid='" + guid + "'", e);
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
            // throw new LFCExceptionWapper("Faild to query " + path, e);
        }

    }

    // /**
    // * Returns file description.
    // * This might contain a symbolic link if resolveLinks==false;
    // */
    // protected FileDescWrapper queryPath(ILFCLocation path,boolean
    // resolveLinks)
    // throws VlException
    // {
    // LFCServer server=createServer();
    // return queryPath(path.getPath(),server,resolveLinks);
    // }

    /**
     * Returns file description. This might contain a symbolic link if
     * resolveLinks==false;
     */
    public FileDescWrapper queryPath(String path, boolean resolveLinks) throws VlException
    {
        LFCServer server = createServer();
        return queryPath(server, path,resolveLinks);
    }

    /**
     * Master query path, optionally resolves links and does extra broken link
     * detection ! Warning: if resolveLink==false this method does a link stat
     * which sadly does not return the GUID in the FileDescription. Use
     * LFCFile.getGuid() to fetch a GUID from an LFCFile.
     * 
     * Updated code by PtdB.
     */
    private FileDescWrapper queryPath(LFCServer server,String path, boolean resolveLink) throws VlException
    {
        info("queryPath (resolveLinks=" + resolveLink + "):" + path + "");
        //
        // Link Handling:
        // LinkStat returns normal description for non Link Files.
        // But does NOT return the GUID in the file description.
        // An extra 'statg' is needed for that.
        // Currently this is handled in getGuid() !
        // 

        try
        {
            FileDesc desc = null;

            // plain link stat:
            if (resolveLink == false)
            {
                // beware that linkstat does not return the GUID:
                desc = server.fetchLinkDesc(path);
            }
            else
            {
                try
                {
                    // this will auto resolve a link !
                    desc = server.fetchFileDesc(path);
                }
                catch (LFCException e)
                {
                    // ===
                    // Broken Link Handling !!!
                    // ===

                    FileDesc linkDesc = server.fetchLinkDesc(path);

                    // if a linkstat works, but a statg doesn't:
                    // this might be a borken link !
                    if (linkDesc.isSymbolicLink() == true)
                    {
                        String linktarget = null;

                        try
                        {
                            // extra: try to resolve link:
                            linktarget = getLinkTarget(path);
                        }
                        catch (Exception ex2)
                        {
                            ;// ignore. This error handling already.
                        }
                        throw new ResourceLinkIsBorkenException("Error querying a link. Broken Link ?\n" + "LFN path="
                                + path + ((linktarget != null) ? "\nlink target path=" + linktarget : ""));
                    }
                    // other error:
                    throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
                }
            }

            FileDescWrapper wrapp = new FileDescWrapper(desc, path);
            return wrapp;
        }
        catch (LFCException e)
        {
            throw convertException("Couldn't query path:" + path, e);
            // throw new LFCExceptionWapper("Faild to query " + path, e);
        }
    }

    //
    // Creates new server or reuses old one
    // To be investigated whether this is the good thing to do.
    // Currently this is needed to give any thread it's own
    // server object to avoid concurrency problems !
    // (Geclipse and Javagat do it this way either)

    private VlException convertException(String message, LFCException e)
    {
        // Filter out standard GlobusExceptions:
        VlException globusEx = GlobusUtil.checkException(message, e.getCause());
        if (globusEx != null)
            return globusEx;

        return LFCExceptionWapper.getVlException(e.getErrorCode(), e);
    }
    
    // do not cache: 
    // private LFCServer _server=null; 
    
    private LFCServer createServer() throws VlException
    {
        // 
        // Just create new Server.
        // For most methods the connections have to be setup again, 
        // so reusing the same LFCServer can only create threading problems. 
        // 
        
        //if (_server==null)
        {
            // Update Credential, it might have changed
            this.lfcConfig.globusCredential = GlobusUtil.getGlobusCredential(getVRSContext().getGridProxy());
    
            if (this.sshTunnelUri != null)
                return new LFCServer(lfcConfig, this.sshTunnelUri);
            else
                return new LFCServer(lfcConfig, this.serverUri);
        }
        
//        if (_server.isConnected()==false)
//        {
//            try
//            {
//                _server.connect();
//            }
//            catch (LFCException e)
//            {
//               throw new VlException("Could not (re)connect to LFC server!",e);
//            } 
//        }
//        
//        return _server; 
    }
 
    // Although eventually the garbage collector will call finalize() which closes
    // connections, better call it after use of a LFCServer. 
    protected void dispose(LFCServer server)
    {
//        if (server==this._server)
//        {
//            // do NOT dispose!
//        }
//        else
        {
            server.dispose(); 
        }
    }

    private ILFCLocation[] list(ILFCLocation lfcLoc, LFCServer server) throws VlException
    {
        String path = lfcLoc.getPath();
        ILFCLocation nodeArr[] = null;
        ArrayList<FileDesc> descList;
        FileDescWrapper wrapper;
        try
        {
            //
            // The File Description returned here can contain
            // unresolved link nodes
            //

            descList = server.listDirectory(path);

            logger.debugPrintf("--- descList.size():%s\n", descList.size());

            FileDesc desc = server.fetchFileDesc(path);

            logger.debugPrintf(" --- desc.getULink()::%s\n", desc.getULink());

            Vector<ILFCLocation> nodes = new Vector<ILFCLocation>(descList.size());

            for (FileDesc file : descList)
            {
                wrapper = new FileDescWrapper();
                wrapper.setFileDesc(file);

                String fullpath = path + "/" + file.getFileName();
                wrapper.setNameAndPath(fullpath);

                // debug("=========== Full atrributes: "
                // + wrapper.getAllAttributesAsString());
                if (file.getFileName() == null)
                {
                    logger.debugPrintf("*** Error: passing null fileName!!!\n");
                }
                if (file.isDirectory())
                {
                    nodes.add(new LFCDir(this.lfcServerNode, wrapper));
                }
                else if (file.isFile() || file.isSymbolicLink())
                {
                    nodes.add(new LFCFile(this.lfcServerNode, wrapper));
                }
                else
                {
                    // add as plain file:
                    nodes.add(new LFCFile(this.lfcServerNode, wrapper));
                }

                // wrapper.printTo(Global.getDebugStream());
            }

            nodeArr = new ILFCLocation[nodes.size()];
            nodeArr = nodes.toArray(nodeArr);

        }
        catch (LFCException e)
        {
            throw new VlException(e);
        }

        return nodeArr;

    }

    public ILFCLocation[] list(ILFCLocation path) throws VlException
    {
        LFCServer server = createServer();
        ILFCLocation[] result=list(path, server);
        dispose(server);
        return result; 
    }

    public VFSNode[] listNodes(ILFCLocation path) throws VlException
    {
        LFCServer server = createServer();
        ILFCLocation locs[] = list(path, server);
        VFSNode arr[];
                
        if (locs == null)
        {
            arr=null; 
        }
        else
        {
            arr= new VFSNode[locs.length];
            for (int i = 0; i < locs.length; i++)
                arr[i] = (VFSNode) locs[i];
        }
        
        dispose(server);
        return arr;
    }

    public VRSContext getVRSContext()
    {
        if (lfcServerNode == null)
        {
            logger.errorPrintf("FIXME: ServerNode not initialized!!!\n");
            return VRSContext.getDefault();
        }
        return lfcServerNode.getContext();
    }

    public VRL createPathVRL(String path) throws VlException
    {
        return new VRL(VRS.LFN_SCHEME, getHostname(), getPort(), path);
    }

    public FileDesc mkdir(String path, boolean ignoreExisting) throws VlException
    {
        LFCServer server = createServer();

        FileDesc fileDesc = null;
        try
        {
            fileDesc = server.mkdir(path);
        }
        catch (LFCException e)
        {
            VlException ex = LFCExceptionWapper.getVlException(e.getErrorCode(), e);
            if (ex instanceof ResourceAlreadyExistsException && ignoreExisting)
            {
                try
                {
                    return server.fetchFileDesc(path);
                }
                catch (LFCException e1)
                {
                    throw LFCExceptionWapper.getVlException(e1.getErrorCode(), e1);
                }
            }
            else
            {
                throw ex;
            }
        }

        return fileDesc;
    }

    private String getHostname() throws VlException
    {
        return serverUri.getHost();
    }

    private int getPort() throws VlException
    {
        return serverUri.getPort();
    }

    public boolean exists(String path, BooleanHolder isDir) throws VlException
    {
        LFCServer server = createServer();
        boolean val=exists(server,path,isDir); 
        dispose(server);
        return val; 
    }
    
    public boolean exists(LFCServer server,String path, BooleanHolder isDir) throws VlException
    {
        try
        {
            // LinkHandling: resolve link, both link AND target must exist:
            FileDesc fileDesc = server.fetchFileDesc(path);
            if (isDir!=null)
                isDir.value = fileDesc.isDirectory();
        }
        catch (LFCException e)
        {
            boolean isBorkenLink = false;

            VlException ex = LFCExceptionWapper.getVlException(e.getErrorCode(), e);

            if (ex instanceof ResourceNotFoundException)
            {
                return false;
            }
            else
            {
                throw ex;
            }
        }
        return true;
    }

    /**
     * Return list of replicas or a zero sized array if there are no replicas.
     * Never returns NULL. PTdB: changed method to use GUID.
     */
    private ReplicaDesc[] listReplicasByGuid(String guid, LFCServer server) throws VlException
    {

        logger.debugPrintf(">>> listReplicasByGuid:%s\n", guid);

        // Assertion !
        if (StringUtil.isEmpty(guid))
            throw new VlInternalError("Empty GUID provided in method: listReplicas:" + this);

        ArrayList<ReplicaDesc> replicas;
        ReplicaDesc[] replicaDesc;
        try
        {
            // PTdB: use Guid !
            replicas = server.getReplicas(guid);

            // Array is never null:
            replicaDesc = new ReplicaDesc[replicas.size()];
            replicaDesc = replicas.toArray(replicaDesc);

            // lfcFile.getWrapperDesc().setReplicas(replicaDesc);
            // for (ReplicaDesc rep : replicas)
            // {
            // debug(" getFs: "+rep.getFs());
            // debug(" getHost: "+rep.getHost());
            // debug(" getNbaccesses: "+rep.getNbaccesses());
            // debug(" getPoolName: "+rep.getPoolName());
            // debug(" getSfn: " + rep.getSfn());
            // }
        }
        catch (LFCException e)
        {
            // throw new LFCExceptionWapper("Faild to get replicas for: " +
            // path,
            // e);
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
        return replicaDesc;

    }

    // private ReplicaDesc[] listReplicas(String path, LFCServer server)
    // throws VlException
    // {
    // return listReplicas(lfcFile, server);
    // }

    /**
     * Return list of replicas or a zero sized array if there are no replicas.
     * Never returns NULL
     */
    public ReplicaDesc[] listReplicasByGuid(String guid) throws VlException
    {
        LFCServer server = createServer();
        ReplicaDesc reps[]= listReplicasByGuid(guid, server);
        dispose(server);
        return reps; 
    }

    // public VRL[] getReplicaVRLS(ReplicaDesc[] replicaDesc) throws
    // VlURISyntaxException{
    // VRL[] repVRLS = new VRL[replicaDesc.length];
    // int i = 0;
    // for(ReplicaDesc rep: replicaDesc){
    // repVRLS[i] = new VRL(rep.getSfn());
    // i++;
    // }
    // return repVRLS;
    // }

    /**
     * Replicas selection. Used 'replicaSelectionMode' from Server properties.
     * <p>
     * Specify 'tryNr' to get the 'next' replica which would be selected: For
     * 'First' will return replica[tryNr%numberOfReplicas] thus sequencially try
     * each replica.<br>
     * For 'Random' this value is ignored.<br>
     * For 'MatchPreferred' this specifies which one will be used from the list
     * of matched replicas. If tryNr is higher then the nr of matched replicas,
     * the modulo of that value will be used.
     */
    public VRL replicaSelection(ITaskMonitor monitor, ReplicaDesc[] replicas, int tryNr) throws VlException
    {
        ReplicaSelectionMode selMode = this.lfcServerNode.getReplicaSelectionMode();

        if ((replicas == null) || (replicas.length <= 0))
        {
            monitor.logPrintf("*** Error: can not select replica from empty list\n");
            throw new nl.uva.vlet.exception.ResourceException("LFC file has no replicas");
        }
        int numReplicas=replicas.length; 
        
        logger.debugPrintf("replicaSelection:Using replicas selection mode=%s, tryNr=%s\n", selMode, tryNr);

        for (ReplicaDesc replica : replicas)
        {
            logger.debugPrintf(" --- replica ---\n %s\n", replica);
        }

        // ===
        // Preferred Sequential or Preferred Random
        // Find replica which matches the SE in the Preferred SE list.
        // ===

        if ((selMode == ReplicaSelectionMode.PREFERRED) || (selMode == ReplicaSelectionMode.PREFERRED_RANDOM))
        {
            ArrayList<VRL> matchedVRLs = new ArrayList<VRL>();

            // check SE using the order in the preferred SE list:
            StringList ses = this.lfcServerNode.getPreferredSEHosts();
            // loop over preferred:
            if (ses != null)
            {
                for (String se : ses)
                {
                    SEInfo seinfo = new SEInfo(se);

                    // loop over replicas and add replica that matches storage
                    // element
                    for (ReplicaDesc replica : replicas)
                    {
                        VRL replicaVRL = new VRL(replica.getSfn());
                        // match Hostname
                        if (replicaVRL.hasHostname(seinfo.getHostname()))
                            matchedVRLs.add(replicaVRL);
                    }

                    if (matchedVRLs.size() > 0)
                    {
                        int num = 0;

                        if (selMode == ReplicaSelectionMode.PREFERRED_RANDOM)
                            num = randomizer.nextInt(matchedVRLs.size());
                        else
                            // return matched VRL using tryNr as index:
                            num = (tryNr % matchedVRLs.size());

                        VRL vrl = matchedVRLs.get(num);
                        monitor.logPrintf("LFC: Using preferred replica #%d (out of %d) from matched SE:\n - %s\n",num,numReplicas,vrl); 
                        logger.debugPrintf("replicaSelection:returning matching SE (updated) Replica VRL:%s\n", vrl);
                        return vrl;
                    }
                }
            }
            logger.debugPrintf("Match PreferredSE: Did NOT find matching SE for replica\n");
        }

        // == Random === //
        if (selMode == ReplicaSelectionMode.ALL_RANDOM)
        {
            int val = randomizer.nextInt(numReplicas);

            logger.debugPrintf("replicaSelection: Random replica #%d (out of %d):%s\n", val, numReplicas,replicas[val].getSfn());
            VRL vrl = new VRL(replicas[val].getSfn());
            monitor.logPrintf("LFC: Using random chosen replica #%d (out of %d):\n - %s\n",val,numReplicas,vrl); 
            return vrl;
        }

        // ===
        // Default Method : Sequential:
        // ===

        // if
        // (StringUtil.equals(selMode,LFCFSConfig.REPLICAS_SELECT_SEQUENTIAL))
        {
            int num = (tryNr % replicas.length);
            logger.debugPrintf("replicaSelection: Sequencial replica selection #%d : %s\n" + num, replicas[num]
                    .getSfn());

            VRL vrl = new VRL(replicas[num].getSfn());
            monitor.logPrintf("LFC: Using sequencial chosen replica #" + num + ":\n - " + vrl + "\n");
            return vrl;
        }

    }

    public InputStream getInputStream(ITaskMonitor monitor, LFCFile path) throws VlException
    {
        int numTries = this.lfcServerNode.getReplicasNrOfTries();
        String errorText = ""; // cumulative error text
        Throwable lastException = null;

        ReplicaDesc[] reps = path.getReplicaDescriptions();

        if ((reps == null) || (reps.length <= 0))
        {
            throw new nl.uva.vlet.exception.ResourceReadException("File doesn't have any replicas:" + path);
        }

        for (int tryNr = 0; tryNr < numTries; tryNr++)
        {
            VRL replicaVRL = null;

            try
            {
            	// ===
                // Use selection algorithm to get replica
            	// Monitoring: Method printout verbose message about replica
                replicaVRL = path.getSelectedReplicaVRL(monitor, tryNr);

                VNode node = this.getVNodeFrom(replicaVRL);

                // ==========
                // Bug #:285 Sometimes the LFC length doesn't match the Replica
                // length:
                // Throw exception to inform calling method!
                // ==========
                if (node instanceof VFile)
                {
                    checkReplicaLength(path, (VFile) node);
                }
                // else other node cannot be checked (currently not possible)

                // VNode might not be readable or result in Exceptions !
                if (node instanceof VStreamReadable)
                {
                	//superfluous: 
                	monitor.logPrintf("LFC: Trying to read from replica (try #%d):\n - %s \n",tryNr,node.getVRL()); 
                	VStreamAccessable resource = (VStreamAccessable) node;
                    return resource.getInputStream();
                }
                String text = "*** Error: Can not handle replica (Unknown resource type):" + node + "\n";
                monitor.logPrintf(text);
                errorText += text;
            }
            catch (Exception e)
            {
                String text = createReplicaErrorText(replicaVRL, e);
                lastException = e;
                logger.logException(ClassLogger.ERROR,e,"%s\n",text);
                Global.errorPrintStacktrace(e);
                errorText+=text;// cumulative error text;  
            }
        }

        throw new nl.uva.vlet.exception.ResourceReadException(
                "Couln't get valid replica to read from\nEncountered Errors:\n" + errorText, lastException);
    }

    protected void checkReplicaLength(LFCFile lfcFile, VFile replicaFile) throws VlException
    {
        long lfcLen = lfcFile.getLength();
        long repLen = replicaFile.getLength();

        if (lfcLen != repLen)
        {
            if (this.lfcServerNode.hasStrictReplicaPolicy())
                throw new VlIOException("IOError: LFC File length doesn't match Replica length:" + lfcLen + "<>"
                        + repLen + " for path:" + lfcFile.getPath());
            else
                logger.errorPrintf("IOError: LFC File length doesn't match Replica length:" + lfcLen + "<>" + repLen
                        + " for path:%s\n", lfcFile.getPath());
        }

    }

    /**
     * Helper method: Add exception to errorText and write small report to
     * monitor
     */
    private String createReplicaErrorText(VRL replicaVRL, Exception e)
    {
        String indentStr="  "; 
        
        String exName = "";
        if (e instanceof VlException)
            exName = ((VlException) e).getName();
        String text = "*** Exception:" + e.getClass().getSimpleName() + ":" + exName + "\n"
                      +" - replica=" + replicaVRL + "\n"
                      +e.getMessage();
        // New beautification of message: add spaces before each line:
        text = StringUtil.insertIndentation(text,indentStr) + "\n";
        return text;
    }

    protected VNode getVNodeFrom(VRL replicaVRL) throws VlException
    {
        // ===
        // Important: Make sure the SRM VRL points to the right V2.2 interface !
        // ===

        replicaVRL = getSRMVRLfromSfn(replicaVRL);

        // query ResourceSystem for more control over the object:
        VResourceSystem rs = this.getVRSContext().openResourceSystem(replicaVRL);

        VNode node = null;

        if (rs instanceof VFileSystem)
        {
            // Blind fetch: just create the file object !
            VFileSystem vfs = (VFileSystem) rs;
            node = vfs.newFile(replicaVRL);
        }
        else
        {
            // use default openLocation, but problems might occure later if this
            // isn't a 'proper' file system.
            rs.openLocation(replicaVRL);
        }

        return node;
    }

    // can be used as a rename. Full pathnames are needed. 
    public VRL mv(String oldPath, String newPath) throws VlException
    {
        LFCServer server = createServer();
        try
        {
            BooleanHolder isDirH=null; 
            
            if (exists(server,newPath,isDirH)) 
                throw new nl.uva.vlet.exception.ResourceAlreadyExistsException("Can not rename/move to new distination.\n"
                        + "Destination resource already exists or is considered 'equivalent' :'"+oldPath+"' <=> '"+newPath+ "'."); 
            
            server.move(oldPath, newPath);
            return this.createVRL(newPath);
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }

    }

    public VRL createVRL(String path) throws VRLSyntaxException
    {
        return this.lfcServerNode.resolvePathVRL(path);
    }
    
    /**
     * Master method to remove LFCEntries: File, Link and Directory.
     * Removed replicas as well. 
     */ 
    private boolean recurseDelete(ITaskMonitor monitor, LFCServer server, ILFCLocation path, boolean forceDelete)
            throws VlException
    {
        // monitor can be optional! 
        if (monitor == null)
            monitor = ActionTask.getCurrentThreadTaskMonitor("recurseDelete():(force="+forceDelete+"):"+path,-1); 
        
        String taskStr="Recursive deleting:" + path.getName(); 
        monitor.startSubTask(taskStr, -1);
        monitor.logPrintf("LFC: Recursive deleting:" + path + "\n");

        boolean isLink = path.isSymbolicLink();
        boolean isDir = path.isDir();

        // ==================
        // unlink link/alias
        // ==================

        if (isLink == true)
        {
            unregister(monitor, path, false, server);
            // Asynchronous update!
            this.getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(path.getVRL()));
            return true;
        }

        // ==============
        // Check Directory: delete childs first:
        // ==============
        if (isDir == true)
        {
            VFSNode nodes[] = ((LFCDir) path).list();
            for (VFSNode node : nodes)
            {
                // Save cancel >>>here<<<
                if (monitor.isCancelled())
                    throw new VlInterruptedException("Deep delete cancelled!");

                recurseDelete(monitor, server, ((ILFCLocation) node), forceDelete);
            }

            unregister(monitor, path, false, server);

        }

        // =======================================
        // Delete replica of NON link/alias file !
        // =======================================

        // only delete replicas if deleting a real file:
        if ((isLink == false) && (isDir == false))
        {
            deleteReplicas(monitor, server, path, forceDelete);
            unregister(monitor, path, false, server);
        }

        // deletion succesful: fire event:
        this.getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(path.getVRL()));

        monitor.endSubTask(taskStr);
        logger.debugPrintf("Done deleting:%s\n",path); 
        return true;
    }

    private void deleteReplicas(ITaskMonitor monitor, LFCServer server, ILFCLocation path, boolean forceDelete)
            throws VlException
    {
        Vector<ReplicaDesc> deletedReplicas = new Vector<ReplicaDesc>();
        ReplicaDesc[] replicas = null;

        int numReplicas = 0;
        int replicasUnregistered = 0;
        int replicasError = 0;

        int subTaskDone = 0;
        int subTaskTodo = 0;

        String guid = path.getGUID();
        replicas = listReplicasByGuid(guid, server);
        monitor.logPrintf("LFC: Number of replicas to delete:%d\n", replicas.length);
        subTaskTodo = replicas.length;
        numReplicas = replicas.length;
        String taskStr="Deleting replicas"; 
        monitor.startSubTask(taskStr, subTaskTodo);
        logger.debugPrintf("Will try to delete %d replicas\n", numReplicas);

        // Save cancel >>>here<<<
        if (monitor.isCancelled())
            throw new VlInterruptedException("Recursive delete cancelled!");

        // ==============================
        // Delete Replicas: Removed from SE and unregister from LFC
        // ==============================
        int n = 0;
        boolean error = false;
        if (replicas != null)
            for (ReplicaDesc rep : replicas) // guarded loop
            {
                logger.infoPrintf("LFC: deleting replica #%d: %s\n", n, rep.getSfn());
                monitor.logPrintf("LFC: deleting replica #%d: %s\n", n, rep.getSfn());

                // Save cancel >>>here<<<
                if (monitor.isCancelled())
                {
                    logger.infoPrintf("Cancelled! Must unregister already deleted replicas.\n");
                    monitor.logPrintf("Cancelled! Must unregister already deleted replicas.\n");
                    forceDelete = false; // disable force delete!
                    error = true;
                    break; // must break: already deleted replicas MUST be
                           // unregistered!
                }

                // ===
                // Delete Replica
                // ===

                // node = getVNodeFromSfn(rep.getSfn());
                VRL vrl = getSRMVRLfromSfn(new VRL(rep.getSfn()));
                VNode node = null;
                try
                {
                    node = this.getVNodeFrom(vrl);
                    deleteFromSE(node, true);
                    monitor.logPrintf(" - deletion from SE succeed\n");
                    logger.debugPrintf(" - deletion from SE succeed\n");

                    deletedReplicas.add(rep);
                }
                catch (Exception e)
                {
                    if (e instanceof ResourceNotFoundException)
                    {
                        logger.debugPrintf(" - replica %s was not there anyway. Considered delete\n", vrl);
                        monitor.logPrintf(" - replica doesn't exist (anymore). Assuming already deleted:\n" + "   - "
                                + vrl + "\n");

                        deletedReplicas.add(rep);
                    }
                    else
                    {
                        if ((node != null) && (node.exists() == false))
                        {
                            logger.debugPrintf(" - replica doesn't exists (deletion confirmed) :%s\n", vrl);
                            monitor.logPrintf(" - replica doesn't exist anymore. Assuming already deleted:\n" + "   - "
                                    + vrl + "\n");

                            deletedReplicas.add(rep);
                        }
                        else
                        {
                            logger.warnPrintf(" - error while deleting replica: %s\n", vrl);
                            logger.logException(ClassLogger.ERROR, e, " - exception while deleting file: %s\n", vrl);

                            monitor.logPrintf(" - failed to delete replica:" + vrl + "\n");
                            monitor.logPrintf(" - Exception = " + e + "\n");

                            error = true;
                            replicasError++;
                        }
                    }
                }
                monitor.updateSubTaskDone(++subTaskDone);
                n++;
            } // Replica Loop

        // ====
        // If some replica's couldn't be deleted only unregister
        // those replica's which have been succesful deleted.
        // Otherwise delete complete file entry including replicas;
        // ====
        if ((forceDelete == false) && (error == true))
        {
            logger.debugPrintf("LFC: Failed to delete all replicas, trying to unregister succesful deleted replicas.\n");
            monitor.logPrintf(
                            "Failed to delete all replicas, trying to unregister succesful deleted replicas. num replicas=%d out of %d\n",
                            deletedReplicas.size(), numReplicas);

            // unregister succesfull deleted replicas
            for (ReplicaDesc rep : deletedReplicas)
            {
                try
                {
                    logger.debugPrintf("unregistering: %s\n", rep.getSfn());
                    server.delReplica(guid, new URI(rep.getSfn()));
                    monitor.logPrintf(" - unregistering: %s\n", rep.getSfn());

                    replicasUnregistered++;
                }
                catch (URISyntaxException e)
                {
                    throw new VRLSyntaxException(e);
                }
                catch (LFCException e)
                {
                    // throw new LFCExceptionWapper("Error while unregistering
                    // replica",e);
                    throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
                }
            }
        }

        monitor.endSubTask(taskStr); // end current

        // ========================================
        // POST:Check errors and stats of replicas!
        // ========================================

        logger.debugPrintf("No errors or replicas. Will unregister: %s\n", path);

        dispose(server); 
        
        // delete this entry if forceDelete==true or there have been no errors!
        if (error==true) 
        {
            // Error report:
            String errorTxt = "Failed to delete replicas. Total number of replica:"+numReplicas 
                    + "\nNumber of replica's deleted     =" + deletedReplicas.size() 
                    + "\nNumber of replicas unregistered =" + replicasUnregistered
                    + "\nNumber of replicas failed       =" + replicasError;

            if (forceDelete == true)
            {
                logger.warnPrintf("%s\n", replicasError, numReplicas, errorTxt);
                monitor.logPrintf("LFC: Warning: %s\n Will (force) delete entry anyway.", replicasError, numReplicas, errorTxt);
            }
            else
            {
                // Error!
                monitor.logPrintf("LFC:%s\n", errorTxt);
                logger.errorPrintf("%s\n", errorTxt);
                throw new nl.uva.vlet.exception.ResourceDeletionFailedException(errorTxt);
            }
        }
        
        return; 
    }
    
    /** 
     * Delete replicas which matched the provided storageElement 
     * @param monitor optional TaskMonitor 
     * @param file the LFCFile
     * @param storageElement hostname of storageElement 
     * @return true replica at specified storageElement could be deleted, 
     *         false if LFCFile has no replica at specified storageElement.  
     * @throws VlException
     */
    public boolean deleteReplica(ITaskMonitor monitor, LFCFile file, String storageElement) throws VlException
    {
        ReplicaDesc rep = file.getReplicaDescription(storageElement);
        if (rep == null)
            throw new ResourceNotFoundException("Couldn't find replica at storage element:" + storageElement);

        if (monitor.isCancelled())
            throw new VlInterruptedException("Interrupted");

        String sfn = rep.getSfn();
        monitor.logPrintf("LFC: Deleting replica:\n - " + sfn + "\n");
        VRL repVrl = new VRL(sfn);
        VNode node = this.getVNodeFrom(repVrl);

        if (monitor.isCancelled())
            throw new VlInterruptedException("Interrupted");

        boolean result = false;

        // Be Robuust
        // don't delete is node doesn't exist!
        // ---
        try
        {
            if (node instanceof VFile)
                result = ((VFile) node).delete();
            else if (node instanceof VDeletable)
                result = ((VDeletable) node).delete();
            else
                // rare if not impossible
                throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Don't know how to delete resource:"
                        + node);

            if (result == true)
                monitor.logPrintf("LFC: Replica Deleted. Unregistering replica\n");
        }
        catch (nl.uva.vlet.exception.ResourceNotFoundException e)
        {
            result = true; // not found -> is already deleted!
        }
        catch (VlException e)
        {
            // extra check. SRM doesn't always correct Exception
            if (node.exists() == true)
                throw e;
            // else replica doesn't exist-> unregister!
            monitor.logPrintf("LFC: Replica doesn't exist anymore! Unregistering replica\n");
            result = true; // 'delete' sucessful.
        }

        if (result == false)
            throw new VlException("Failed to delete:" + node);

        file.unregisterReplicas(new VRL[] { repVrl });
        return result;
    }

    // /**
    // * Deletes all files physically from EVERY SE and then unregisters EVERY
    // * replica
    // *
    // * @param path
    // * @return
    // * @throws ResourceNotFoundException
    // * @throws VlException
    // */
    // public boolean deepDelete(ITaskMonitor monitor, ILFCLocation path)
    // throws VlException
    // {
    // LFCServer server = createServer();
    // return deepDelete(monitor, path, server);
    // }

    /**
     * Deletes all files physically from EVERY SE and then unregisters EVERY
     * replica
     * 
     * @param path
     * @return
     * @throws ResourceNotFoundException
     * @throws VlException
     */
    public boolean recurseDelete(ITaskMonitor monitor, ILFCLocation path, boolean forceDelete) throws VlException
    {
        LFCServer server = createServer();
        return recurseDelete(monitor, server, path, forceDelete);
    }

    /**
     * Returns Storage Element VRL including VO Path allowed for writing. Might
     * return NULL !
     * 
     * @param voStorageAreas
     * @throws BdiiException
     */
    public VRL selectSALocationForWriting(ITaskMonitor monitor, Map<String, StorageArea> voStorageAreas, int tryNr)
            throws VlException
    {

        logger.debugPrintf("--- getSEVOLocationForWriting try #%d\n", tryNr);

        if ((voStorageAreas == null) || (voStorageAreas.size() <= 0))
            return null;

        StorageArea selectedSA = null;

        ReplicaCreationMode mode = this.lfcServerNode.getReplicaCreationMode();
        logger.debugPrintf("getSEVOLocationForWriting(): Replica Creation Mode,tryNry=%s, %d\n", mode, tryNr);

        if ((mode == ReplicaCreationMode.PREFERRED_RANDOM) || (mode == ReplicaCreationMode.PREFERRED))
        {
            // Get matching StorageAreas and StorageElements:
            StringList prefSEs = this.lfcServerNode.getPreferredSEHosts();

            // matched allowed SAs with preferred SEs:

            ArrayList<StorageArea> matchedSAs = new ArrayList<StorageArea>();

            for (String sestr : prefSEs)
            {
                // AAARG
                if (sestr == null)
                {
                    logger.errorPrintf("*** Error: NULL Storage info string\n");
                    continue;
                }

                SEInfo seinfo = new SEInfo(sestr);

                StorageArea sa = voStorageAreas.get(seinfo.getHostname().toLowerCase());

                if (sa != null)
                {
                    logger.debugPrintf("Adding Matched StorageElement:%s\n", sestr);
                    matchedSAs.add(sa);
                }
                else
                    logger.errorPrintf("*** Error:couldn't resolve StorageElement:%s\n", sestr);
            }

            if (matchedSAs.size() > 0)
            {
                int num = 0;
                // Preferred Random or Preferred Sequential
                if (mode == ReplicaCreationMode.PREFERRED_RANDOM)
                    num = randomizer.nextInt(matchedSAs.size());
                else
                    num = tryNr;

                selectedSA = matchedSAs.get(num % matchedSAs.size());
                VRL seVRL = selectedSA.getVOStorageLocation();

                logger.infoPrintf("CreateReplica: Used mode=%s, new Replica VRL=%s\n", mode, seVRL);
                return seVRL;
            }

            logger.warnPrintf("Couldn't find a match between Preferred SEs and available SEs\n");
            monitor
                    .logPrintf("*** Warning: Couldn't find a match between available Storage Elements and Preferred ones (as specified in listPreferredSEs).\n");

            // Be Robust: Continue with RANDOM mode!
            if (this.lfcServerNode.getStrictPreferredMode())
            {
                throw new nl.uva.vlet.exception.ResourceCreationFailedException(
                        "Couldn't find a match between preferred storage elements and available ones.");
            }

        }

        // =============================================
        // Default SE Matchnig: Randomize Default ones:
        // =============================================

        int num = 0;

        // default to VO Random !
        num = randomizer.nextInt(voStorageAreas.size());

        num = num % voStorageAreas.size();
        Set<String> keys = voStorageAreas.keySet();
        String keyArr[] = new String[keys.size()];
        keyArr = keys.toArray(keyArr);
        selectedSA = voStorageAreas.get(keyArr[num]);

        VRL seVRL = selectedSA.getVOStorageLocation();

        monitor.logPrintf("LFC: Trying StorageArea:" + selectedSA.getHostname() + ":/" + selectedSA.getStoragePath()
                + " using location:");
        monitor.logPrintf("\n - " + seVRL + "\n");

        logger.infoPrintf("CreateReplica: Used mode=%s, created Replica VRL=%s\n", mode, seVRL);

        return seVRL;
    }

    /**
     * Returns map of all Storage Areas allowed for the current VO. Queries the
     * BDII service. <br>
     * Note: since Map&lt;,...&gt; is case SENSITIVE the hostnames are
     * normalized to LOWER case.
     */
    public Map<String, StorageArea> getVOStorageAreas() throws VlException
    {
        if (this.getVRSContext().getGridProxy().isVOEnabled() == false)
            throw new nl.uva.vlet.exception.VlAuthenticationException(
                    "No VO enabled Grid Proxy. Please create VO enabled Grid Proxy.");

        String vo = this.getVRSContext().getVO();

        // empty vo ? Can result to trouble ...
        if (StringUtil.isEmpty(vo))
            throw new nl.uva.vlet.exception.VlAuthenticationException("No VO specified. Cannot locate storage areas.");

        // check if SAs are already fetched for the current VO:
        if (StringUtil.equals(cachedVO, vo))
        {
            if (cachedSAs != null)
                return cachedSAs;
        }

        Map<String, StorageArea> saMap = new Hashtable<String, StorageArea>();

        // Returns optional caches SAs
        try
        {
            ArrayList<StorageArea> sas = this.getBdiiService().getSRMv22SAsforVO(vo);

            for (StorageArea sa : sas)
            {
                saMap.put(sa.getHostname().toLowerCase(), sa);
                cachedSAs = saMap;
                cachedVO = vo;
            }
        }
        catch (Exception e)
        {
            logger.errorPrintf("Failed to query BDII:%s\n", e);
            Global.errorPrintStacktrace(e);
        }

        // =========================================================
        // Backup Mechanism: In case the VO Storage Area have failed:
        // use SRM VRLs specified in the listPreferredSEs, if any specified !
        // Also SRM VRls overrides StorageArea settings, so just overwrite the
        // location,
        // by putting new StorageArea definition into the map.
        // =========================================================
        VRLList seVrls = this.lfcServerNode.getPreferredSEVRLs();

        if (seVrls != null)
            for (VRL vrl : seVrls)
            {
                // Overwrite StorageArea information with the SRM URIs.
                // Note that the SRM Uris might point to another VO Writable
                // then is allowed by the current used VO.
                // This can not be checked here.
                saMap.put(vrl.getHostname(), StorageArea.createSRM22StorageArea(vrl, vo));
            }

        return cachedSAs;
    }

    /** Returns "<srmpath>/generated/yyyy-MM-dd" path for specified SE */
    public String createGeneratedDatePathForSELocation(VRL se)
    {
        VRL seVRL = se;

        debug("BASE VO PATH: " + seVRL);

        // Timezone ?
        Calendar cal = Calendar.getInstance();

        // Create yyyy-MM-dd subdir:
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(lfcServerNode.getGeneratedSubDirDateScheme());
        String timeDateSubdir = sdf.format(cal.getTime());
        // 'generated/' subdir name:
        String generatedSubDirname = lfcServerNode.getGeneratedSubdirName();

        // Create: srm://.../home/pvier/generated/yyyy-MM-dd
        return seVRL.getPath() + "/" + generatedSubDirname + "/" + timeDateSubdir;
    }

    public FileDescWrapper registerEntry(String lfn) throws VlException
    {
        LFCServer server = createServer();
        return registerEntry(lfn, server);
    }

    /**
     * Create a new "Generated" file in a Storage Element which can be used as
     * Replica. This file is NOT an LFC file !
     */
    public VFile generateNewReplica(ITaskMonitor monitor, String lfname, int tryNr) throws VlException
    {
        return generateNewReplica(monitor, null, lfname, tryNr);
    }

    /**
     * Create a new "Generated" file in a Storage Element which can be used as
     * Replica. Specify storageElement as preferred SE, or leave to null auto
     * select SE. The returned file is a replica. It is NOT an LFC file !
     * 
     * @param storageElement
     *            preferred storage element. Leave to null for autoselection. If
     *            specified this method will only try that Storage Element.
     * @param lfcname
     *            LFC name which might be used in the Replica name creation.
     * @param tryNr if optStorateElement == null, use tryNr as replica selection parameter.  
     */
    public VFile generateNewReplica(ITaskMonitor monitor, String optStorageElement, String lfcname, int tryNr)
            throws VlException
    {
        debug("generateNewReplicaInSelectedSE:" + lfcname);

        Map<String, StorageArea> voStorageAreas = getVOStorageAreas();
        if ((voStorageAreas == null) || (voStorageAreas.size() <= 0))
        {
            throw new ResourceCreationFailedException("No VO Writable Storage Elements could be found."
                    + "Either BDII query failed or no known Storage Element has been configured");
        }

        VRL seVOLoc = null;

        // ====================================
        // Match Preferred Storage Element against VO Storage Areas (if any)
        // ====================================
        if ((optStorageElement != null) && (optStorageElement!="")) 
        {
            StorageArea area = voStorageAreas.get(optStorageElement);
            if (area != null)
                seVOLoc = area.getVOStorageLocation();
            else
            {
                monitor.logPrintf("LFC: Error could not find Storage Element:" + optStorageElement);
                throw new nl.uva.vlet.exception.ResourceNotFoundException(
                        "Could not find configuration for storage element:" + optStorageElement);
            }

            logger.infoPrintf("Using explicit defined StorageElement:%s\n", seVOLoc);
        }

        // =============================
        // No Preferred Storage Element
        // =============================
        if ((seVOLoc == null) && (voStorageAreas != null))
        {
            // return default
            seVOLoc = this.selectSALocationForWriting(monitor, voStorageAreas, tryNr);

            logger.infoPrintf("Using selected StorageElement:%s\n" + seVOLoc);
        }

        // FAIL
        if (seVOLoc == null)
        {
            throw new ResourceCreationFailedException("Error no valid Storage Area location found for selection try#"
                    + tryNr);
        }

        try
        {
            // returns SRM://host:port/<VO home>/vletgenerated/yyyy-MM-dd path
            String vopath = createGeneratedDatePathForSELocation(seVOLoc);

            // filter out space and other strange characters

            if (this.lfcServerNode.getUseSimilarReplicaNames())
                lfcname = sanitizeSEName(lfcname);
            else
                lfcname = "vletfile";

            String sePath = vopath + "/" + lfcname + "_" + createNewUniqueFileID();

            // replica VRL
            VRL srmFileVrl = seVOLoc.copyWithNewPath(sePath);

            logger.infoPrintf("Creating new SRM Replica URI:%s\n", srmFileVrl);

            // open FileSystem using same context as this client !

            VFSClient cl = getVFSClient();

            // Make full path of parent directory !
            if (!cl.existsDir(srmFileVrl.getParent()))
            {
                cl.mkdirs(srmFileVrl.getParent(), true);
            }
            // Create File Object only
            return cl.newFile(srmFileVrl);
        }
        // Robustness: catch ALL Exception here !!
        catch (Throwable e)
        {
            throw new ResourceCreationFailedException("Failed to create new Replica location at:" + seVOLoc, e);
        }

    }

    private String createNewUniqueFileID()
    {
        UUID id = UUID.randomUUID();
        return id.toString();
    }

    public static String sanitizeSEName(String sePath)
    {
        // include slash in allowed characters ?
        return sePath.replaceAll("[^a-zA-Z0-9_/]", "_");
    }

    private VFSClient getVFSClient()
    {
        if (vfsClient == null)
            vfsClient = new VFSClient(getVRSContext());
        return vfsClient;
    }

    /**
     * Add unchecked replica, optionally updates Replica MetaData
     * 
     * @param monitor
     * @param updateMetaData
     */
    public void addReplica(ITaskMonitor monitor, LFCFile file, VFile replica, boolean updateReplicaMetaData)
            throws VlException
    {
        this.addReplica(monitor, file, replica.getVRL());

        if (updateReplicaMetaData)
        {
            this.updateReplicaMetaData(monitor, file, replica);
        }
    }

    public void updateReplicaMetaData(ITaskMonitor monitor, LFCFile file, VFile replica) throws VlException
    {
        // update size:
        long size = replica.getLength();
        debug(" --- Replica size is " + size + " bytes");
        // FileDesc desc = file.getWrapperDesc().getFileDesc();
        setFileSize(file, size);
        monitor.logPrintf("LFC: New size of LFCfile (updated from replica) =" + size + "\n");
    }

    /** Add Replica VRL. Does not update Meta Data */
    public void addReplica(ITaskMonitor monitor, LFCFile lfcFile, VRL replicaVRL) throws VlException
    {
        LFCServer server = createServer();
        addReplica(monitor, lfcFile, replicaVRL, server);
        dispose(server); 
    }

    /**
     * Add replica to LFCFile. If lfcFile doesn't not exist: the LFCFile will be
     * registered.
     */
    private boolean addReplica(ITaskMonitor monitor, LFCFile lfcFile, VRL replicaVRL, LFCServer server)
            throws VlException
    {
        lfcFile.clearCachedReplicas();

        // ==============================================
        // Duplicate and REMOVE port information !
        // ========================================

        URI replicaURI = replicaVRL.copyWithNewPort(-1).toURI();
        VlException lastEx = null;
        boolean registerFile = false;
        FileDescWrapper wrapp = null;

        // try first: register
        try
        {
            // LinkHandling: refetch resolved File Description !
            // update file info: Always resolve Path, since we need
            // the actual file for replica handling:
            wrapp = queryPath(server,lfcFile.getPath(),  true);
            server.addReplica(wrapp.getFileDesc(), replicaURI);
            fireReplicasChanged(lfcFile);

            return true;
        }
        // can't have a replica without an LFN. If it's not there register a
        // new entry
        catch (LFCException e)
        {
            VlException ex = LFCExceptionWapper.getVlException(e.getErrorCode(), e);
            // if (ex instanceof ResourceNotFoundException)
            // {
            // registerEntry(lfn, server);
            // addReplica(lfn, replicaURI, server);
            // }
            // else
            {
                throw ex;
            }
        }
        catch (VlException ex)
        {
            if ((ex instanceof ResourceNotFoundException) == false)
            {
                throw ex;
            }

            // Spiros: Non read variable
            // lastEx = ex;
        }

        // register file:
        try
        {
            wrapp = registerEntry(lfcFile.getPath(), server);
            server.addReplica(wrapp.getFileDesc(), replicaURI);
            fireReplicasChanged(lfcFile);
            return true;
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
    }

    /** Fire asynchronous update! */
    protected void fireReplicasChanged(LFCFile lfcFile)
    {
        String attrNames[] =
        { LFCFSFactory.ATTR_NUM_REPLICAS, LFCFSFactory.ATTR_LFC_REPLICAHOSTS };

        try
        {
            VAttribute[] attrs = lfcFile.getAttributes(attrNames);
            ResourceEvent event = ResourceEvent.createAttributesChangedEvent(lfcFile.getVRL(), attrs);
            this.getVRSContext().fireEvent(event);
        }
        catch (Exception e)
        {
            logger.warnPrintf("Failed to fire event:%s\n", e);
        }
    }

    private FileDescWrapper registerEntry(String lfn, LFCServer server) throws VlException
    {
        debug(">>> registerEntry:" + lfn);

        FileDescWrapper wrapper = null;
        try
        {
            server.registerEntry(lfn);
            wrapper = new FileDescWrapper();
            FileDesc fileDesc;
            fileDesc = server.fetchFileDesc(lfn);
            wrapper.setFileDesc(fileDesc);
            wrapper.setNameAndPath(lfn);
        }
        catch (LFCException e)
        {

            // throw new LFCExceptionWapper("Failed to register " + path
            // + " to the catalogue", e);
            VlException ex = LFCExceptionWapper.getVlException(e.getErrorCode(), e);
            if (ex instanceof ResourceTypeMismatchException)
            {
                throw new ResourceCreationFailedException(ex.getMessage());
            }
            else
            {
                throw ex;
            }
        }

        return wrapper;

    }

    // private void deleteFromSE(VRL[] replicas, boolean recurse)
    // throws VlException
    // {
    //
    // VNode node = null;
    // node = getVRSContext().openLocation(replicas[0]);
    //
    // // first check VFSNodes:
    // if (node instanceof VFile)
    // {
    // ((VFile) node).delete();
    // }
    //
    // else if (node instanceof VDir)
    // {
    // ((VDir) node).delete(recurse);
    // }
    // // try generic VDeletable
    // else if (node instanceof VDeletable)
    // {
    // ((VDeletable) node).delete();
    // }
    // else
    // {
    // throw new nl.uva.vlet.exception.ResourceDeletionFailedException(
    // "Faild to delete " + replicas[0]
    // + ". Resource is not a file or directory");
    // }
    //
    // }

    public void deleteFromSE(VNode node, boolean recurse) throws VlException
    {
        // first check VFSNodes:
        if (node instanceof VFile)
        {
            ((VFile) node).delete();
        }
        else if (node instanceof VDir)
        {
            ((VDir) node).delete(recurse);
        }
        // try generic VDeletable
        else if (node instanceof VDeletable)
        {
            ((VDeletable) node).delete();
        }
        else
        {
            throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Faild to delete " + node
                    + ". Resource is not a file or directory");
        }

    }

    /**
     * Match Replica VRL against configured Storage Elements. Uses BdiiService
     * to query SRM Services or full specified SRM URis from listPreferredSEs
     * Tries to get SRM V2.2 first then SRM V1.1 later.
     */
    public VRL getSRMVRLfromSfn(VRL sfn)
    {
        ServiceInfo info = null;
        VRL newVRL = sfn;

        // ===================================
        // Get full specified locations in prefferedSEs.
        // This overrides the BDII service!
        // ===================================
        VRL srmVrl = null;
        VRLList vrls = this.lfcServerNode.getPreferredSEVRLs();

        if (vrls != null)
            srmVrl = vrls.findHostname(sfn.getHostname());

        if (srmVrl != null)
        {
            // srm URI overrides BDII: copy VRL arguments from preferred URI:
            VRL newVrl = new VRL(srmVrl.getScheme(), // copy scheme as well
                    null, srmVrl.getHostname(), // use hostname
                    srmVrl.getPort(), // overrides port
                    sfn.getPath()); // keep SFN path!
            debug(">>> updated SRM VRLs <<<\n - " + sfn + "\n - " + newVrl);
            return newVrl;
        }

        try
        {
            info = getBdiiService().getSRMv22ServiceForHost(sfn.getHostname());
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Error: could not query BDII Service\n");
        }

        // ==================================
        // TRY SRM V1.1 !!! (experimental!)
        // ==================================
        if (info == null)
        {
            logger.warnPrintf("Querying SRM v2.2 failed trying SRM v1.1 for sfn:%s\n", sfn);

            try
            {
                info = getBdiiService().getSRMV11ServiceForHost(sfn.getHostname());
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR, e, "Error: could not query BDII Service.\n");
            }
        }
        // backup: check preferredSEs list if there is a port number

        if (info == null)
        {
            logger.warnPrintf("Couldn't get SRM v2.2 nor SRM v1.1 information for vrl:%s\n", sfn);
        }
        else
        {
            int port = info.getPort();

            if (sfn.getPort() != port)
            {
                // update port information only to get SRM v2.2 interface:
                newVRL = sfn.copyWithNewPort(port);
            }
        }

        debug("getSRMVRL(): Returning replica VRL=" + newVRL);

        return newVRL;
    }

    private BdiiService getBdiiService() throws VlException
    {
        // will auto create one:
        return this.getVRSContext().getBdiiService();
    }

    OutputStream createOutputStream(ITaskMonitor monitor, LFCFile file) throws VlException
    {
        logger.debugPrintf("createOutputStream to LFCFile:%s\n", file);

        ReplicaDesc[] replicas = null;
        try
        {
            replicas = file.getReplicaDescriptions(); // this.listReplicas(file);
        }
        catch (VlException ex)
        {
            if (!(ex instanceof ResourceNotFoundException))
            {
                throw ex;
            }
        }

        String sfn = null;

        if ((replicas == null) || (replicas.length == 0))
        {
            int maxNrTries = this.lfcServerNode.getReplicasNrOfTries();
            Throwable lastEx = null;
            // ===
            // Do Create new Replica+getOutputStream loop
            // ===
            for (int tryNr = 0; tryNr < maxNrTries; tryNr++)
            {
                try
                {
                    // Create new File and return OutputStream
                    // replica VFile might not exist yet:
                    VFile replica = generateNewReplica(monitor, file.getName(), tryNr);

                    String txt = "LFC: Creating new replica (mode=" + this.lfcServerNode.getReplicaCreationMode()
                            + ") for writing:\n - " + replica + "\n";

                    monitor.logPrintf(txt);
                    info(txt);

                    // LFCServer server = createServer();
                    // if u add a replica the LFN MUST be there. Should register
                    // it???
                    // addReplica(orgPath, replica.getVRL().toURI(), server);
                    // add some kind of check or move the addrepplica code after
                    // the
                    // replica was created
                    LFCOutputStream out = new LFCOutputStream(monitor, file, replica, this, true);
                    return out;
                }
                catch (Throwable t)
                {
                    monitor.logPrintf("LFC: try#" + tryNr + ": Failed to create output stream:" + t + "\n");
                    debug("***Warning, createOutputStream(): Exception:" + t);
                    lastEx = t;
                }
            }

            throw new ResourceCreationFailedException(
                    "Couldn't create new replica to write to at any of the storage elements", lastEx);

        }

        if (replicas.length == 1)
        {

            // get first and only: VFileSystem will replace replica with new
            // content!
            sfn = replicas[0].getSfn();
            monitor.logPrintf("Will overwrite 1 replica:" + sfn + "\n");
            info("Overwriting existing replica:" + sfn);
        }
        else if (replicas.length >= 2)
        {
            monitor.logPrintf("File has more then 1 replica: cannot write to this location\n");

            // replece replicas[0], just open stream
            // mv replicas[0] to replicas[1-n]
            throw new ResourceWriteAccessDeniedException("Sorry, writing to multiple replicas currently not supported");
        }

        // if this point is reached it means that replica should exist. But...
        // if the replica was deleted without updating the catalogue we could
        // never get in/out streams
        VRL sfnVRL = new VRL(sfn);
        VFile replica = null;

        debug("New replica file object in: " + sfnVRL);
        // replica file to write to: Just create the object.
        replica = getVFSClient().newFile(sfnVRL);

        monitor.logPrintf("LFC: Will write to replica:" + sfnVRL + "\n");

        // LFCServer server = createServer();
        LFCOutputStream out = new LFCOutputStream(monitor, file, replica, this, false);
        return out;// getVFSClient().openFileSystem(sfnVRL).createOutputStream(sfnVRL);

        // if (seFile instanceof VStreamWritable)
        // {
        // // In the case of SRM the SRM will update the contents if
        // // this replica after the stream has closed !
        // return ((VStreamWritable) seFile).getOutputStream();
        // }
        // else
        // {
        // throw new nl.uva.vlet.exception.InterfaceMismatchException(
        // " Replica doesn't support OutputStreams:" + seFile);
        // }
    }

    // private FileDescWrapper getDesc(ILFCLocation lfn, LFCServer
    // server,boolean resolveLink)
    // throws VlException
    // {
    // FileDesc fileDesc = null;
    // FileDescWrapper wrapp = new FileDescWrapper();
    // String path=lfn.getPath();
    //        
    // try
    // {
    // // check wether to do a normal stat or link stat:
    // if (resolveLink)
    // fileDesc = server.fetchFileDesc(path);
    // else
    // fileDesc=server.fetchLinkDesc(path);
    //            
    // if (fileDesc == null)
    // {
    // throw new VlException("Failed to get file description for: "
    // + path);
    // }
    //
    // wrapp.setFileDesc(fileDesc);
    // wrapp.setIsLinkStat((resolveLink==false));//reverse the polarity capt'n
    //            
    // if (fileDesc != null && fileDesc.getFileName() == null)
    // {
    // wrapp = setNameAndPath(wrapp, path);
    // }
    //
    // }
    // catch (LFCException e)
    // {
    // // throw new LFCExceptionWapper("Couldn't get description for: "
    // // + path, e);
    // throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
    // }
    // return wrapp;
    // }

    // /** Get File Description. Auto resolves links */
    // public FileDescWrapper getDesc(ILFCLocation path) throws VlException
    // {
    // LFCServer server = createServer();
    // try
    // {
    // return this.qgetDesc(path, server,true);// auto resolve links
    // }
    // catch (ResourceNotFoundException e)
    // {
    // // try again but do not resolve as link:
    // FileDescWrapper desc = getDesc(path,server,false);
    // // is linkstat works: probalby borken link:
    // if (desc.getFileDesc().isSymbolicLink())
    // {
    // // inform caller:
    // String target=this.getLinkTarget(path.getPath());
    // throw new
    // ResourceIsBorkenLinkException("Could not resolve symbolic link. Broken link?\n"+
    // "link target="+target,e);
    // }
    // else
    // throw e; // throw original exception
    // }
    // }

    // /** Return UNresolved Link Description ! */
    // public FileDescWrapper getLinkDesc(ILFCLocation path) throws VlException
    // {
    // LFCServer server = createServer();
    // return getDesc(path, server,false);// do not resolve links
    // }

    // really deletes LFC directory. If recurse=false directories will always be
    // removed
    // without checking and deleting the contents !
    private boolean rmDir(ITaskMonitor monitor, LFCServer server, ILFCLocation path, boolean recurse, boolean force)
            throws VlException
    {
        // delete replicas
        if (recurse)
        {
            return recurseDelete(monitor, path, force);
        }
        else
        {
            return unregister(monitor, path, recurse);
        }
    }

    public boolean rmDir(ITaskMonitor monitor, ILFCLocation path, boolean recurse, boolean force) throws VlException
    {
        LFCServer server = createServer();
        return rmDir(monitor, server, path, recurse, force);
    }

    private boolean unregister(ITaskMonitor monitor, ILFCLocation lfn, boolean recursive, LFCServer server)
            throws VlException
    {
        FileDescWrapper desc = lfn.getWrapperDesc();

        String path = lfn.getPath();

        try
        {
            // debug("Full attr: "+desc.getAllAttributesAsString());
            if (lfn.isSymbolicLink())
            {
                monitor.logPrintf("LFC: Unregistering alias :" + path + "\n");
                debug("Will unregister (symlinked) file: " + path);
                server.unregister(path);
            }
            else if (lfn.isFile())
            {
                monitor.logPrintf("LFC: Unregistering file:" + path + "\n");
                debug("Will unregister file: " + path);
                server.deleteFile(lfn.getGUID(), path);
            }
            else if (lfn.isDir())
            {
                monitor.logPrintf("LFC: Unregistering directory:" + path + "\n");

                debug("Will unregister dir: " + path);
                server.rmdir(path);
            }
            else
            {
                monitor.logPrintf("LFC: Unkown entry type. Cannot unregister:" + path);

                debug(path + " was neither file or dir");
                if (!exists(path, new BooleanHolder()))
                {
                    debug(path + " was not there");
                    return true;
                }
                else
                {
                    throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Couldn't delete " + path);
                }

            }
        }
        catch (LFCException e)
        {
            VlException ex = LFCExceptionWapper.getVlException(e.getErrorCode(), e);

            if (ex instanceof ResourceAlreadyExistsException && recursive)
            {
                ILFCLocation[] nodes = list(lfn);

                if (nodes == null || nodes.length <= 0)
                {
                    throw new VlException("LFC Inconcistancy", "LFC service for " + path + " lists " + nodes.length
                            + " nodes but num of childern is: " + desc.getFileDesc().getULink());
                }

                debug(path + " not empty");
                for (ILFCLocation node : nodes)
                {
                    debug(" geting node: " + node.getPath());
                    unregister(monitor, node, recursive);
                }

                return unregister(monitor, lfn, recursive, server);
            }
            else
            {
                throw ex;
            }
        }
        return true;
    }

    public boolean unregister(ITaskMonitor monitor, ILFCLocation path, boolean recursive) throws VlException
    {
        LFCServer server = createServer();
        boolean val=unregister(monitor, path, recursive, server);
        dispose(server);
        return val;
    }

    /**
     * Resolve Link: returns NULL if there is no link target
     * 
     * @throws VlException
     */
    public String getLinkTarget(String path) throws VlException
    {
        LFCServer server=createServer(); 

        try
        {
            String linkPath=server.readLink(path);
            dispose(server);
            return linkPath; 
        }
        catch (LFCException e)
        {
            info("Exception while resolving link:" + e);
        }
        finally
        {   
            dispose(server);
        }
        // no error handling here return null to indicate no link target
        return null;
    }

    /** Update LFC Record */
    public void setFileSize(LFCFile file, long size) throws VlException
    {
        FileDesc desc = file.getWrapperDesc().getFileDesc();
        LFCServer server = createServer();
        setFileSize(desc, size, server);
        file.getWrapperDesc().clearMetaData();
        dispose(server); 
    }

    private void setFileSize(FileDesc desc, long size, LFCServer server) throws VlException
    {
        try
        {
            server.setFileSize(desc.getGuid(), size, desc.getChkSumType(), desc.getChkSumValue());
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
    }

    // public long updateGetFileLength(LFCFile file) throws VlException
    // {
    // VNode node = null;
    // try
    // {
    // node = getVNodeFromBestReplica(file.getPath());
    // }
    // catch (ResourceException e)
    // {
    // // no replicas
    // return 0;
    // }
    // FileDescWrapper desc = file.getWrapperDesc();
    //
    // MLFCServer serv = createServer();
    //
    // boolean update = true;
    // long size = desc.getFileDesc().getFileSize();
    // if (update)
    // {
    // size = ((VFile) node).getLength();
    // try
    // {
    // serv.setFileSize(desc.getFileDesc().getGuid(), size, desc
    // .getFileDesc().getChkSumType(), desc.getFileDesc()
    // .getChkSumValue());
    // }
    // catch (LFCException e)
    // {
    // throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
    //
    // }
    // }
    //
    // return size;
    // }
   
    public LFCFile createSymLink(ILFCLocation orgPath, VRL newPath) throws VlException
    {
        LFCServer server = this.createServer();

        try
        {
            FileDesc linkDesc = server.createSymLink(orgPath.getPath(), newPath.getPath());
            FileDescWrapper fwrap = new FileDescWrapper();
            fwrap.setFileDesc(linkDesc);
            fwrap.setNameAndPath(newPath.getPath());
            dispose(server); 
            
            return new LFCFile(this.lfcServerNode, fwrap);
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
        finally
        {
            dispose(server); 
        }
    }

    public boolean checkTransferLocation(VRL remoteLocation, nl.uva.vlet.data.StringHolder explanation,
            boolean source2target)
    {
        String scheme = remoteLocation.getNormalizedScheme();
        explanation.value = "LFC Supports 3rd party transfers for scheme:" + scheme;
        debug("Checking 3rd party transfer for:" + remoteLocation);

        if (VRS.isGFTP(scheme))
            return true;

        if (VRS.isSRM(scheme))
            return true;

        if (VRS.isLFN(scheme))
            return true;

        explanation.value = "Third party transactions not supported for scheme:" + scheme;
        debug("Checking 3rd party transfer:" + explanation.value);

        return false;
    }

    public VFile doTransfer(ITaskMonitor monitor, LFCFile sourceFile, VRL remoteTargetLocation) throws VlException
    {

        VFile targetFile = this.getVFSClient().newFile(remoteTargetLocation);

        // Check LFC -> LFC transfer (slightely more complicated)
        if (targetFile instanceof LFCFile)
        {
            return this.doLFC2LFCTransfer(monitor, sourceFile, (LFCFile) targetFile);
        }

        int numReplicaRetries = this.lfcServerNode.getReplicasNrOfTries();
        
        // check LFC -> {SRM,GFTP}
        return doReplicaTransfer(monitor, sourceFile, targetFile,numReplicaRetries);
    }

    /**
     * Handle transfer from source Location to target LFC File
     */
    public VFile doTransfer(ITaskMonitor monitor, VRL remoteSourceLocation, LFCFile targetFile) throws VlException
    {

        VFile sourceFile = this.getVFSClient().newFile(remoteSourceLocation);
        // Check LFC -> LFC transfer (slightely more complicated)
        if (sourceFile instanceof LFCFile)
        {
            return this.doLFC2LFCTransfer(monitor, (LFCFile) sourceFile, targetFile);
        }
        // LFC File may exist (registered) but not have any replicas yet !
        if (targetFile.exists())
        {
            // block writing to existing LFC files
            VRL reps[] = targetFile.getReplicas();

            if ((reps != null) && (reps.length > 0))
            {
                throw new nl.uva.vlet.exception.ResourceCreationFailedException(
                        "Will not do third party transfer to LFC File with existing replicas!:" + targetFile);
            }
        }

        // ===
        // {SRM,GFTP} -> LFC
        // ===

        int maxNrTries = this.lfcServerNode.getReplicasNrOfTries();
        Throwable lastEx = null;

        // ===
        // Do Create new Replica+getOutputStream loop
        // ===
        for (int tryNr = 0; tryNr < maxNrTries; tryNr++)
        {
            try
            {
                // new (target) replica:
                VFile replicaTargetFile = targetFile.generateNewReplicaInSE(monitor, tryNr);
                monitor.logPrintf("LFC: try #" + tryNr + ": Creating new replica (mode="
                        + this.lfcServerNode.getReplicaCreationMode() + ") for writing:\n - "
                        + replicaTargetFile.getVRL() + "\n");
                StringHolder reasonH = new StringHolder();

                // One of them MUST support active transfers
                if ((sourceFile instanceof VFileActiveTransferable)
                        || (replicaTargetFile instanceof VFileActiveTransferable))
                {
                    // Delegete to TransferManager!
                    boolean result = this.getVRSContext().getTransferManager().doActiveFileTransfer(monitor,
                            sourceFile, replicaTargetFile, reasonH);
                    if (result == false)
                    {
                        monitor.logPrintf("LFC: try#" + tryNr + " Failed. Reason=" + reasonH.value);
                        // Continue!
                    }
                    else
                    {
                        targetFile.create();
                        // ===
                        // Finalize LFC File: Use Target LFCClient !!
                        // ===
                        targetFile.getLFCClient().addReplica(monitor, targetFile, replicaTargetFile, true);
                        monitor.logPrintf("LFC: Created new file:\n - " + targetFile + "\n");

                        return targetFile;
                    }

                }
                else
                {
                    // should already have been checked:
                    debug("*** Error: resolved sourceFile or target doesn't implement VFileActiveTransferable:"
                            + sourceFile);
                    throw new nl.uva.vlet.exception.ResourceTypeMismatchException(
                            "Third party transfer not supported (Interface Mismatch) for source:" + sourceFile);
                }

            }
            catch (Throwable t)
            {
                monitor.logPrintf("LFC: try#" + tryNr + ": Transfer Failed:" + t + "\n");
                debug("***Warning, doTransfer(): Exception:" + t);
                lastEx = t;
            }
        } // for loop

        throw new ResourceCreationFailedException("Transfer failed.", lastEx);

        // throw new
        // nl.uva.vlet.exception.ResourceTypeNotSupportedException("Third party transfer not supported for:"+
        // remoteSourceLocation);
    }

    private VFile doLFC2LFCTransfer(ITaskMonitor monitor, LFCFile sourceFile, LFCFile targetFile) throws VlException
    {
        int numReadTries = this.lfcServerNode.getReplicasNrOfTries();
        // should work: get replica from source LFCFile and transfer to remote
        // LFC location.
        return this.doReplicaTransfer(monitor, sourceFile, targetFile,numReadTries);
    }

    /**
     * Loop over replicas and try to transfer replica to remote location. The
     * first replica that succeeds is returned. This method can be used as
     * 'master' copy method for an LFC File to any LFC,SRM or GridFTP location.
     * The returned file might not be the same type or VRL as specified in
     * targetFile !
     * 
     * @throws ResourceTypeNotSupportedException
     * @throws ResourceCreationFailedException
     * @throws VlInterruptedException
     */
    protected VFile doReplicaTransfer(ITaskMonitor monitor, LFCFile sourceFile, VFile targetFile,int numReplicaReadTries) throws VlException
    {
        // VRL sourceVRL=sourceFile.getVRL();
        // VRL targetVRL=targetFile.getVRL();

        String errorText = "";
        Exception lastEx = null;
        
        // Loop over replicas and transfer to remoteTargetLocation:
        for (int trynr = 0; trynr < numReplicaReadTries; trynr++)
        {
            if (monitor.isCancelled())
                throw new nl.uva.vlet.exception.VlInterruptedException("Transfer interrupted");

            VRL replicaVRL = null;
            
            try
            {
                // ============================
                // Select Replica to read from:
                // ============================
                replicaVRL = sourceFile.getSelectedReplicaVRL(monitor, trynr);
                VNode sourceReplicaNode = this.getVNodeFrom(replicaVRL);

                StringHolder reasonH = new StringHolder();

                // One of them MUST support active transfers
                if ((sourceReplicaNode instanceof VActiveTransferable) || (targetFile instanceof VActiveTransferable))
                {
                    // Delegete to TransferManager!
                    boolean result = this.getVRSContext().getTransferManager().doActiveFileTransfer(monitor,
                            sourceReplicaNode, targetFile, reasonH);
                    if (result == false)
                    {
                        monitor.logPrintf("LFC: ActiveFileTransfer try#" + trynr + " failed! Reason=" + reasonH.value);
                        // Continue!
                    }
                    else
                    {
                        return targetFile; // Target File !
                    }
                }
                else
                {
                    // should already have been checked:
                    debug("*** Error: resolved sourceFile or target doesn't implement VFileActiveTransferable:"
                            + sourceFile);
                    throw new nl.uva.vlet.exception.ResourceTypeMismatchException(
                            "Third party transfer not supported (Interface Mismatch) for source:" + sourceFile);
                }

                // continue: try next
            }
            catch (Exception e)
            {
                // create exception text
                String text = createReplicaErrorText(replicaVRL, e);
                monitor.logPrintf("LFC: replica transfer try#" + trynr + " failed!\n" + text);
                errorText+=text; // cumulative error text 
                lastEx = e;
            }

        }// end for all replicas 

        // FAIL
        throw new nl.uva.vlet.exception.ResourceCreationFailedException(
                "Third party copying to failed for all replicas to:\n - " + targetFile + "\n" + errorText + "\n",
                lastEx);
    }

    public boolean registerReplicas(ITaskMonitor monitor, LFCFile file, VRL[] vrls) throws VlException
    {
        boolean success = true;
        VlException lastEx = null;

        for (VRL vrl : vrls)
        {
            // try
            // {
            this.addReplica(monitor, file, vrl);
            // }
            // catch (VlException e)
            // {
            // lastEx=e;
            // success=false;
            // }
        }

        return success;
    }

    public boolean unregisterReplicas(ITaskMonitor monitor, LFCFile file, VRL[] vrls) throws VlException
    {
        LFCServer server = createServer();
        return unregisterReplicas(server, monitor, file, vrls);
    }

    protected boolean unregisterReplicas(LFCServer server, ITaskMonitor monitor, LFCFile file, VRL[] vrls)
            throws VlException
    {

        // ReplicaDesc[] repDescs = file.getReplicaDescriptions();
        //        
        // // create hashmap using Storage Element hostname as key:
        // Map<String,ReplicaDesc> repMap=new Hashtable<String,ReplicaDesc>();
        //        
        // for (ReplicaDesc rep:repDescs)
        // {
        // VRL repVRL=new VRL(rep.getSfn());
        // repMap.put(repVRL.getHostname(),rep);
        // }

        String guid = file.getGUID();
        for (VRL vrl : vrls)
        {
            debug("Unregistering replica:" + vrl);
            try
            {
                server.delReplica(guid, vrl.toURI());
            }
            catch (LFCException e)
            {
                throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
            }
            monitor.logPrintf("LFC: Replica unregistered:" + vrl + "\n");
        }

        file.clearCachedReplicas();

        return true;

    }

    /**
     * Return Logical VRL of file using it's GUID
     * 
     * @return
     * @throws VlException
     */
    public VRL getLFNVRL(LFCFile file) throws VlException
    {
        ArrayList<String> links = this.getLinksTo(file);

        if ((links != null) && (links.size() > 0))
        {
            String orgPath = links.get(0);
            return createPathVRL(orgPath);
        }
        // existing file should always return at least the original file name !
        throw new nl.uva.vlet.exception.ResourceNotFoundException("Query didn't result into any links or alias for:"
                + file);
    }

    public ArrayList<String> getLinksTo(LFCFile file) throws VlException
    {
        LFCServer server = createServer();
        String guid = file.getGUID();

        try
        {
            ArrayList<String> links = server.listLinks(guid);
            dispose(server); 
            return links;
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
        finally
        {
            dispose(server); 
        }
    }

    private String getDefaultVOHome() throws VlException
    {
        String vo = this.getVRSContext().getVO();
        // get storage area path suitable for writing by this VO.
        if (StringUtil.isEmpty(vo))
            throw new nl.uva.vlet.exception.VlAuthenticationException(
                    "No VO specified in current context. Please enable VO authentication. ");

        return "/grid/"+vo; 
//        
//        debug("server VRL: " + new VRL(serverUri));
//
//        String voPath = getVRSContext().getBdiiService().getLFCVoPathFor(this.getHostname(), vo);
//
//        return voPath;
    }

    protected void setComment(String path, String comment) throws VlException
    {
        LFCServer server = createServer();
        try
        {
            server.setComment(path, comment);
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
    }

    public String getComment(String path) throws VlException
    {

        LFCServer server = createServer();
        try
        {
            return server.getComment(path);
        }
        catch (LFCException e)
        {
            if (!exists(path, new BooleanHolder()))
            {
                throw new nl.uva.vlet.exception.ResourceNotFoundException("Resource Not Found in: " + path);
            }
            else
            {
                // throw new LFCExceptionWapper(e.getErrorString(),e);
                return null;
            }
        }
    }

    /**
     * Replicate files to list of PreferredSEs
     * 
     * @throws VlException
     */
    public void replicate(ITaskMonitor monitor, VRL[] vrls, List<String> listSEs) throws VlException
    {
        // empty selection,etc;
        if ((listSEs == null) || (listSEs.size() <= 0))
        {
            throw new VlException("LFCException", "No Preferred Storage Elements specified for replication");
        }

        // empty selection,etc;
        if (vrls == null)
        {
            throw new VlException("LFCException", "No files selected (vrrss==null) for replication");
        }

        int numSEs = listSEs.size();
        int numFiles = vrls.length;
        int done = 0;
        int todo = numSEs * numFiles;

        if (monitor == null)
        {
            monitor = ActionTask.getCurrentThreadTaskMonitor(
                    "Replicating (and verify entries) to preferred Storage Elements", todo);
        }

        monitor.startTask("Replicating (and verify entries) to preferred Storage Elements ", todo);

        // empty selection,etc;
        if (listSEs.size() <= 0)
        {
            monitor.logPrintf("No Preferred Storage Elements specified for replication\n");
            return;
        }

        // empty selection,etc;
        if (vrls.length <= 0)
        {
            monitor.logPrintf("No file specified for replication\n");
            return;
        }

        for (VRL vrl : vrls)
        {
            if (monitor.isCancelled())
                throw new VlInterruptedException("Interrupted");

            monitor.logPrintf("LFC: Updating replicas: (" + vrl.getHostname() + ") " + vrl.getBasename() + "\n");

            LFCFile file = this.lfcServerNode.openFile(vrl);

            ReplicaDesc[] reps = file.getReplicaDescriptions();

            if (reps == null)
            {
                // be robuust:
                monitor.logPrintf("LFC: WARNING: Skipping file, file doesn't have any replicas(!) :" + file + "\n");
                continue;
            }

            StringList currentSEs = new StringList();

            // Verify current replica FIRST before adding more:
            for (ReplicaDesc rep : reps)
            {
                if (monitor.isCancelled())
                    throw new VlInterruptedException("Interrupted");

                boolean verified = false;
                String se = rep.getHost();

                try
                {
                    // verify stats or delete replica entry
                    verified = verifyReplica(monitor, file, se, true);
                    if (verified)
                    {
                        monitor.logPrintf("LFC: Verified existing replica. Keeping replica at Storage Element:" + se
                                + "\n");
                        currentSEs.add(rep.getHost()); // keep
                    }
                    // else replica was deleted !
                }
                catch (Exception e)
                {
                    monitor.logPrintf("LFC: WARNING: Could not verify status of replica at host (keeping entry!):" + se
                            + "\n");
                    monitor.logPrintf("LFC: Exception =" + e + "\n");
                    verified = false;
                    // keep entry !
                    currentSEs.add(se);
                }
            }

            // ===
            // Parallel copy possible here !
            // ===

            for (String se : listSEs)
            {
                if (monitor.isCancelled())
                    throw new VlInterruptedException("Interrupted");

                if (currentSEs.contains(se))
                {
                    monitor.updateWorkDone(++done);
                    continue;
                }

                monitor.logPrintf("LFC: Adding new replica at Storage Element:" + se + "\n");
                this.replicateFile(monitor, file, se);
                monitor.updateWorkDone(++done);
            }
        }

        if (todo == done)
        {
            monitor.logPrintf("LFC: Replicating suceeded for all files.\n");
        }
        else
        {
            monitor.logPrintf("LFC: Replicating suceeded for some files.\n");
        }

        monitor.endTask("Replicating to preferred Storage Elements");
    }

    /**
     * Verify replica of LFCFile at storageElement, if delete==true, the replica
     * will be deleted if NOT consistant anymore. Return true if replica is
     * verified, false if replica doesn't exists or other inconsistancy.
     * <p>
     * Will only throw exceptions if unknown conditions occured. File not found
     * exceptions will result in a returned value of FALSE.
     * 
     * @throws VlException
     * 
     */
    public boolean verifyReplica(ITaskMonitor monitor, LFCFile file, String storageElement, boolean delete)
            throws VlException
    {
        String repSfn = null;
        long lfcSize = -1;
        String checksum = null;

        monitor.startSubTask("Verfying replica at:" + storageElement, 3);

        try
        {
            // size as reported in LFC:
            lfcSize = file.getLength();

            // use CACHED value, make sure cached value is updated after replica
            // maninpulations!
            ReplicaDesc rep = file.getReplicaDescription(storageElement);
            if (rep == null)
            {
                monitor.updateSubTaskDone(3);
                return false;// no replica at storage element
            }

            // check replica:
            repSfn = rep.getSfn();
        }
        catch (VlException e)
        {
            // pre stage: LFC error: cannot determine replica status:
            throw new nl.uva.vlet.exception.VlServerException("Verify error: Couldn't check information about"
                    + "lfc file:" + file + ". Status Unknown.", e);
        }
        monitor.updateSubTaskDone(1);
        if (monitor.isCancelled())
            throw new VlInterruptedException("Interrupted");

        // =====================
        // continue with replica
        // =====================

        VRL repVRL = null;

        try
        {
            repVRL = new VRL(repSfn);
        }
        catch (VRLSyntaxException e)
        {
            // internal error: should not happen:
            throw e;
        }

        // File object: might not exist.
        VFile repFile = this.getVFSClient().newFile(repVRL);

        // If exists throws an exception, it is unsure whether the file still
        // exists or not.
        // exists() implemention returns false if it can determine the file
        // really doesn't exist!
        if (repFile.exists() == false)
        {
            if (delete)
            {
                monitor.logPrintf("LFC: Replica doesn't exist anymore. Unregistering replica at host:" + storageElement
                        + "\n");
                this.unregisterReplicas(monitor, file, new VRL[]
                { repVRL });
            }
            monitor.updateSubTaskDone(3);
            return false;
        }

        monitor.updateSubTaskDone(2);
        if (monitor.isCancelled())
            throw new VlInterruptedException("Interrupted");

        // file exists: check length:
        if (repFile.getLength() != lfcSize)
        {
            if (delete)
            {
                monitor.logPrintf("LFC: Replica length does not match LFC length! Deleting replica at host:"
                        + storageElement);
                repFile.delete();
                this.unregisterReplicas(monitor, file, new VRL[]
                { repVRL });
            }
            monitor.updateSubTaskDone(3);
            return false;
        }

        // ====
        // Check Checksum ?
        // ====

        // checks succeeded.
        monitor.updateSubTaskDone(3);
        monitor.endSubTask("Verfying replica at:" + storageElement);
        return true;
    }

    /**
     * Replicate file and return new Replica (SRM) File. Current LFC File must
     * already have at least one replica !
     * @param monitor TaskMonitor
     * @param file the LFCFile to replicate, Must have at least one replica. 
     * @param storageElemen the StorageElement to replica to. 
     * @throws VlInterruptedException
     */
    public VFile replicateFile(ITaskMonitor monitor, LFCFile file, String storageElement)
            throws ResourceCreationFailedException, VlInterruptedException
    {
        int numReplicaTries = this.lfcServerNode.getReplicasNrOfTries();
        
        if (monitor.isCancelled())
            throw new VlInterruptedException("Interrupted");

        try
        {
            // ==================================================
            // Create new File at specified Storage Element !
            // set tryNr to 1 as there is only one Storage Element. 
            // ==================================================
            // creates file object but doesn't create actual replica file yet: 
            VFile newReplica = generateNewReplica(monitor, storageElement, file.getBasename(), 1);
            monitor.logPrintf("LFC: Will try replica for writing:\n - " + newReplica + "\n");

            // ========================================================
            // Try Replica read loop, iterates over available Replicas!
            // ========================================================
            
            // should be new Replica File (update monitor!)
            VFile resultFile = this.doReplicaTransfer(monitor, file, newReplica,numReplicaTries);
            this.addReplica(monitor, file, resultFile.getVRL());
            monitor.logPrintf("LFC: Success: Registered new replica:\n - " + resultFile + "\n");
            // ==========================  
            // Check Checksums/File size ???
            // ==========================
            return resultFile;
        }
        catch (Throwable t)
        {
            monitor.logPrintf("LFC: Failed to create replica:" + t);
            debug("***Warning. replicateFile(): Exception:" + t);
        
            throw new ResourceCreationFailedException("Couldn't create new replica at storage element:"
                + storageElement + "\n" + t.getMessage(), t);
        }
    }

    public void replicateDirectory(ITaskMonitor monitor, LFCDir dir, List<String> listSEs) throws VlException
    {

        // empty selection,etc;
        if ((listSEs == null) || (listSEs.size() <= 0))
        {
            throw new VlException("LFCException", "No Preferred Storage Elements specified for replication");
        }

        // empty selection,etc;
        if (dir == null)
        {
            throw new VlException("LFCException", "No Directory selected (directory==null) for replication");
        }

        LongHolder totalSize = new LongHolder();
        Vector<VNode> heap = new Vector<VNode>();

        monitor.startSubTask("Scanning directory:" + dir, -1);
        monitor.logPrintf("Scanning directory:" + dir + "\n");

        // build heap:
        this.getVRSContext().getTransferManager().listRecursive(monitor, dir, heap, totalSize, true);
        monitor.endSubTask("Scanning directory:" + dir);

        monitor.logPrintf("Total files to replicate=" + heap.size() + " (" + totalSize.value + ")\n");

        Vector<VRL> vrls = new Vector<VRL>();
        for (VNode node : heap)
        {

            // filter LFC Files only:
            if (node instanceof LFCFile)
                vrls.add(node.getVRL());
        }

        VRL vrlArr[] = new VRL[vrls.size()];
        vrlArr = vrls.toArray(vrlArr);
        this.replicate(monitor, vrlArr, listSEs);

    }

    public void setLFCConfig(LFCConfig config)
    {
        this.lfcConfig = config;
    }

    public void setMode(String path, int mode) throws VlException
    {
        LFCServer server = createServer();

        try
        {
            server.setMode(path, mode);
            dispose(server); 
        }
        catch (LFCException e)
        {
            throw LFCExceptionWapper.getVlException(e.getErrorCode(), e);
        }
        finally
        {
            dispose(server); 
        }

    }

    public boolean recurseDelete(LFCFile lfcFile, boolean forceDelete) throws VlException
    {
        return this.recurseDelete(null,lfcFile,forceDelete); 
    }

}
