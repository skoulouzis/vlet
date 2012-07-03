/*
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *    Piter T. de boer - Refactoring to standalone API and bugfixing.  
 *    Spiros Koulouzis - Refactoring to standalone API and bugfixing.  
 */ 
// source: 

package nl.uva.vlet.glite.lfc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;

import nl.uva.vlet.glite.lfc.internal.AbstractCnsResponse;
import nl.uva.vlet.glite.lfc.internal.CnsAddReplicaRequest;
import nl.uva.vlet.glite.lfc.internal.CnsAddReplicaResponse;
import nl.uva.vlet.glite.lfc.internal.CnsChmodRequest;
import nl.uva.vlet.glite.lfc.internal.CnsChmodResponse;
import nl.uva.vlet.glite.lfc.internal.CnsCloseDirRequest;
import nl.uva.vlet.glite.lfc.internal.CnsCloseDirResponse;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsCreatGRequest;
import nl.uva.vlet.glite.lfc.internal.CnsCreatGResponse;
import nl.uva.vlet.glite.lfc.internal.CnsDelReplicaRequest;
import nl.uva.vlet.glite.lfc.internal.CnsDelReplicaResponse;
import nl.uva.vlet.glite.lfc.internal.CnsFileSizeRequest;
import nl.uva.vlet.glite.lfc.internal.CnsFileSizeResponse;
import nl.uva.vlet.glite.lfc.internal.CnsGStatRequest;
import nl.uva.vlet.glite.lfc.internal.CnsGStatResponse;
import nl.uva.vlet.glite.lfc.internal.CnsGetCommentRequest;
import nl.uva.vlet.glite.lfc.internal.CnsGetReplicaRequest;
import nl.uva.vlet.glite.lfc.internal.CnsGetReplicaResponse;
import nl.uva.vlet.glite.lfc.internal.CnsLinkStatRequest;
import nl.uva.vlet.glite.lfc.internal.CnsLinkStatResponse;
import nl.uva.vlet.glite.lfc.internal.CnsListLinkRequest;
import nl.uva.vlet.glite.lfc.internal.CnsListReplicaRequest;
import nl.uva.vlet.glite.lfc.internal.CnsListReplicaResponse;
import nl.uva.vlet.glite.lfc.internal.CnsLongResponse;
import nl.uva.vlet.glite.lfc.internal.CnsMessage;
import nl.uva.vlet.glite.lfc.internal.CnsMkdirRequest;
import nl.uva.vlet.glite.lfc.internal.CnsOpenDirRequest;
import nl.uva.vlet.glite.lfc.internal.CnsReadDirRequest;
import nl.uva.vlet.glite.lfc.internal.CnsReadDirResponse;
import nl.uva.vlet.glite.lfc.internal.CnsReadLinkRequest;
import nl.uva.vlet.glite.lfc.internal.CnsRenameRequest;
import nl.uva.vlet.glite.lfc.internal.CnsRmdirRequest;
import nl.uva.vlet.glite.lfc.internal.CnsSetCommentRequest;
import nl.uva.vlet.glite.lfc.internal.CnsSetCommentResponse;
import nl.uva.vlet.glite.lfc.internal.CnsSingleStringResponse;
import nl.uva.vlet.glite.lfc.internal.CnsStringListResponse;
import nl.uva.vlet.glite.lfc.internal.CnsSymLinkRequest;
import nl.uva.vlet.glite.lfc.internal.CnsUnlinkRequest;
import nl.uva.vlet.glite.lfc.internal.CnsUnlinkResponse;
import nl.uva.vlet.glite.lfc.internal.CnsVoidResponse;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.glite.lfc.internal.GSSTokenRecv;
import nl.uva.vlet.glite.lfc.internal.GSSTokenSend;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;

import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.gridforum.jgss.ExtendedGSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;


/**
 * LFC Server Class based upon GEClipse code.  
 * <p>
 * Modifications done by Piter T. de  Boer and Spiros Koulouzis:<br>
 * - Cleanup code: use methods for repeated code <br>
 * - better exception messages. <br>
 * - Added SYMLINK,LINKSTAT and READLINK messages.<br.
 * - Fixed bug in link detection.<br> 
 */
public class LFCServer
{
    // ===
    // Class Stuff
    // ===
    
    public static final int DEFAULT_PORT = 5010;

    /** Default LFC Logger */ 
    protected static java.util.logging.Logger logger = null; 
    
    static
    {
        logger=java.util.logging.Logger.getLogger("org.glite.lfc"); 
        staticLogMessage("org.glite.lfc initialized");
        
    }
    
    /** IO expeption logger */ 
    public static void staticLogIOException(final Exception ex) 
    {
        logger.log(Level.WARNING,"IO: Exception",ex); 
    }
    
    /** IO message logger */ 
    public static void staticLogMessage(final String msg) 
    {
        logger.log(Level.FINE,msg); 
    }

    /** IO message logger */ 
    public static void staticLogIOMessage(final String msg) 
    {
        logger.log(Level.FINER,"IO:"+msg); 
    }

    /**
     * Set Custom Class Logger. 
     * Parameter newLogger is instance or subclass of LFCLogger. 
     * (subclass LFCLogger for more control over the logging). 
     * @param newLogger the new class logger to use.
     */
    public static void setLogger(java.util.logging.Logger newLogger)
    {
        logger=newLogger; 
    }
    
    /** Return Class Logger */ 
    public static java.util.logging.Logger getLogger()
    {
        return logger; 
    }
    
    /**
     * Token for authentication handshake initialisation
     */
    private static final byte[] nullToken = { (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x47, (byte) 0x53, (byte) 0x49, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };


    
    
    // ========================================================================
    // Instance Stuff
    // ========================================================================
    
    /**
     * stores server endpoint
     */
    private URI uri;

    /**
     * secure communication socket
     */
    private Socket socket;

    private DataInputStream input;

    private DataOutputStream output;

    private ExtendedGSSContext context;

    private LFCConfig lfcConfig = new LFCConfig();

    /**
     * Constructs new LFC server wrapper
     * 
     * @param serverUri
     *            server endpoint
     */
    public LFCServer(final URI serverUri)
    {
        Assert.isNotNull(serverUri, Messages.LFCServer_null_URI);
        this.uri = serverUri;
    }

    public LFCServer(LFCConfig config, final URI serverUri)
    {
        Assert.isNotNull(serverUri, Messages.LFCServer_null_URI);
        this.uri = serverUri;
        setConfig(config);
    }

    public void setConfig(LFCConfig config)
    {
        this.lfcConfig = config;
    }

    public LFCConfig getConfig()
    {
        return this.lfcConfig;
    }

    /**
     * Tries to connect to the LFC server using the credential from manager.
     * 
     * I/O-problems or because of authentication problems.
     * 
     * @throws LFCException
     */
    public void connect() throws LFCException
    {
        try
        {
            // 
            // OpenJDK doesn't like it if to many socket/file descriptors are open!
            // cleanup/dispose previous socket!
            if (socket!=null)
                disconnect();
            
            staticLogMessage(String.format(Messages.lfc_log_connect, this.uri
                    .getHost(), new Integer(this.uri.getPort())));
            
            int port=uri.getPort();
            
            if (port<=0) 
                port=LFCServer.DEFAULT_PORT; 
            
            this.socket = new Socket(); 
            socket.connect(new InetSocketAddress(this.uri.getHost(), port),this.lfcConfig.timeout);

            if (!this.authenticate())
            {
                throw new LFCException("Error while (re) connecting to:"+uri+".\n"+Messages.LFCServer_token_canceled);
            }

        }
        catch (GSSException gssExc)
        {
            throw new LFCException("GSS Authentication Exception while connecting to:"+uri+".\n"+gssExc.getMessage(), gssExc);
        }
        catch (ConnectException conExc)
        {
            throw new LFCException("Connection Failed Exception while connecting to:"+uri+".\n"+conExc.getMessage(),conExc);
        }
        catch (IOException ioExc)
        {
            throw new LFCException("IO Exception while connecting to:"+uri+".\n"+ioExc.getMessage(),ioExc); 
        }
        catch (GlobusCredentialException e)
        {
            throw new LFCException("Globus Credential Exception",e);  
	
		}
    }

    /**
     * Authenticates client/server via Extended GSS mechanism
     * 
     * @throws GSSException
     *             in case of authentication problems
     * @throws IOException
     * @throws IOException
     *             in case of any I/O problem
     * @throws GlobusCredentialException 
     */
    private boolean authenticate() throws GSSException, LFCException,
            IOException, GlobusCredentialException
    {
        boolean result = false;
        staticLogMessage(Messages.lfc_log_authenticate);

        CertUtil.init();

        // use from configuration
        GlobusCredential cred = this.lfcConfig.globusCredential;

        // if null get default from globus:
        if (cred == null)
        {
            try
            {
                // cred=new GlobusCredential("/tmp/x509up_u601");
                cred = GlobusCredential.getDefaultCredential();
            }
            catch (GlobusCredentialException e)
            {
            	throw e; 
            }

        }// import org.eclipse.core.filesystem.EFS;

        this.input = new DataInputStream(new BufferedInputStream(
                this.socket.getInputStream()));
        this.output = new DataOutputStream(new BufferedOutputStream(
                    this.socket.getOutputStream()));

            GSSCredential gssCred = new GlobusGSSCredentialImpl(cred,
                    GSSCredential.INITIATE_AND_ACCEPT);
            try
            {
                // *** PDB Changed ***
                new GSSTokenSend(LFCServer.nullToken).send(this.output);

                // FIXME Check the response, now assume it's OK
                GSSTokenRecv returnToken = new GSSTokenRecv();
                returnToken.readFrom(this.input);

                GSSManager manager = new GlobusGSSManagerImpl();
                this.context = (ExtendedGSSContext) manager.createContext(
                        null,
                        GSSConstants.MECH_OID, 
                        gssCred, 
                        86400);

                this.context.requestMutualAuth(true); // true
                this.context.requestCredDeleg(false);
                this.context.requestConf(false); // true
                this.context.requestAnonymity(false);

                this.context.setOption(
                        GSSConstants.GSS_MODE,
                        GSIConstants.MODE_GSI);
                this.context.setOption(
                        GSSConstants.REJECT_LIMITED_PROXY,
                        Boolean.FALSE);

                byte[] inToken = new byte[0];

                // Loop while there still is a token to be processed
                while (!this.context.isEstablished())
                {
                    byte[] outToken = this.context.initSecContext(inToken, 0,
                            inToken.length);
                    // send the output token if generated
                    if (outToken != null)
                    {
                        /** PDB CHANGED * */
                        new GSSTokenSend(CnsConstants.CSEC_TOKEN_MAGIC_1, 3,
                                outToken).send(this.output);

                        // gssTokenSend(this.output, );
                        /** PDB CHANGED * */
                        this.output.flush();
                    }
                    if (!this.context.isEstablished())
                    {
                        GSSTokenRecv gsstoken = new GSSTokenRecv();
                        gsstoken.readFrom(this.input);

                        inToken = gsstoken.getToken();
                    }
                }
                staticLogMessage(Messages.lfc_log_authenticated);
                result = true;
        }
        catch (IOException ioExc)
        {
            ioExc.printStackTrace();

            staticLogIOException(ioExc);
            
            throw ioExc;//rethrow  
        }
        
        return result;
    }
    
    /**
     * Disconnects from the LFC server. Clears socket, context and all data
     * streams.
     * No exception is thrown. 
     * Always disconnects, closes sockets and ignores possible exceptions. 
     * 
     */
    public void disconnect() 
    {
        // try to close and cleanup everything. 
        try
        {
            staticLogMessage(String.format(Messages.lfc_log_disconnect,
                    this.uri.getHost(), new Integer(this.uri.getPort())));
    
            if (this.context != null)
            {
                try{this.context.dispose();}catch (Throwable t) {}; 
            }
                
            if (input!=null)
                try{this.input.close();}catch (Throwable t) {}; 
                
            if (output!=null)
                try{this.output.close();}catch (Throwable t) {}; 
                
            if (this.socket != null)
                try{socket.close();}catch (Throwable t) {}; 
        }
        finally
        {
            this.context = null;
            this.input = null;
            this.output = null;
            this.socket = null;
        }
    }

    /**
     * Test if the connection to the server is established.
     * 
     * @return True if the connection is established, false otherwise.
     */
    public boolean isConnected()
    {
        return (this.socket != null && this.socket.isConnected());
    }

    /**
     * Lists the specified directory.
     * 
     * @param path
     *            path to the directory
     * @return list of the {@link FileDesc} containing information about items
     *         beneath.
     * @throws LFCException
     */

    public ArrayList<FileDesc> listDirectory(final String path)
            throws LFCException
    {
        ArrayList<FileDesc> result = null;

        FileDesc fileDesc = this.STATG(path, false);
        this.OPENDIR(path);
        result = this.READDIR(fileDesc.getFileId());
        
        // PTdB: only send CLOSEDIR if still connected ! 
        if (this.isConnected())
        {
            try
            {
                this.CLOSEDIR();
            }
            catch (Exception e)
            {
                // if READDIR succeed, just continue. 
                staticLogMessage("Warning: Exception during CLOSEDIR"); 
                // disconnect! 
                try {this.disconnect();} catch (Exception e2){}
            }
        }
        
        return result;
    }

    /**
     * Retrieves list of replicas for a given guid.
     * 
     * @param guid
     *            Global (Grid) Unique ID of the file
     * @return list of {@link ReplicaDesc} describing replicas
     * @throws LFCException
     * @deprecated Use GetReplicas ! 
     */

    public ArrayList<ReplicaDesc> listReplicas(final String guid)
            throws LFCException
    {
        Assert.isTrue(guid != null && guid.length() != 0,
                Messages.LFCServer_empty_guid);

        ArrayList<ReplicaDesc> result = null;

        try
        {
            this.checkReConnected();
            
            CnsListReplicaRequest request = new CnsListReplicaRequest(guid);
            CnsListReplicaResponse response = request.sendTo(this.output,
                    this.input);
            result = response.getReplicasArray();

            this.checkRCOrError("listReplicas", response);
            
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/LISTREPLICAS IO Problem.", ex);
        }
        return result;
    }
    
    /**
     *
     */
    public ArrayList<ReplicaDesc> getReplicas(final String guid)
    throws LFCException
    {
        return getReplicas(guid,true); 
    }

    /**
     *
     */
    public ArrayList<ReplicaDesc> getReplicasByPath(final String path)
    throws LFCException
    {
       return getReplicas(path,false); 
    }

    public ArrayList<ReplicaDesc> getReplicas(final String guid,boolean isGuid)
            throws LFCException
    {
        Assert.isTrue(guid != null && guid.length() != 0,
                Messages.LFCServer_empty_guid);

        ArrayList<ReplicaDesc> result = null;

        try
        {
            this.checkReConnected();
            
            CnsGetReplicaRequest request = new CnsGetReplicaRequest(guid,isGuid);
            CnsGetReplicaResponse response = request.sendTo(this.output,
                    this.input);
            result = response.getReplicasArray();

            this.checkRCOrError("getReplicas", response.getMessage());
            
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/GETTREPLICAS IO Problem.", ex);
        }
        return result;
    }

  

    /**
     * Sets the new file size and checksum values.
     * 
     * @param guid
     *            Global (Grid) Unique ID of the file.
     * @param size
     *            new size of the file
     * @param newCsType
     *            new check sum type, two letter indicator. Mostly <strong>MD</strong>
     *            for MD5 checksums.
     * @param newCsValue
     *            new check sum value
     * @throws LFCException
     */

    public void setFileSize(final String guid, final long size,
            final String newCsType, final String newCsValue)
            throws LFCException
    {
        Assert.isTrue(guid != null && guid.length() != 0,
                Messages.LFCServer_empty_guid);
        try
        {
            this.checkReConnected(); 

            CnsFileSizeRequest request = new CnsFileSizeRequest(size);
            request.setCsumType(newCsType);
            request.setCsumValue(newCsValue);
            request.setGuid(guid);

            CnsFileSizeResponse response = request.sendTo(this.output,
                    this.input);

            this.checkRCOrError("setFileSize of:+ guid",response);  
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/SETFILESIZE IO Problem.", ex);
        }
    }

    /**
     * Sets a comment to the file or directory. This overwrites the former
     * comment.
     * 
     * @param path
     *            path to the file or directory
     * @param newComment
     *            new comment of the entry
     * @throws LFCException
     */
    public void setComment(final String path, final String newComment)
            throws LFCException
    {
        Assert.isTrue(path != null && path.length() != 0,
                Messages.LFCServer_empty_path);
        this.SETCOMMENT(path, newComment);
    }
    
    
    /**
     * Sets unix mode to the file or directory.
     * 
     * @param path
     *            path to the file or directory
     * @param mode in int 
     * @throws LFCException 
     * @throws LFCException
     */
    public void setMode(String path, int mode) throws LFCException
    {
        Assert.isTrue(path != null && path.length() != 0,
                Messages.LFCServer_empty_path);
        this.CHMODE(path,mode);
    }
    
    protected void CHMODE(String path, int mode) throws LFCException
    {
        
        try
        {
            this.checkReConnected(); 
            
            CnsChmodRequest request = new CnsChmodRequest(path,mode);
            
            CnsChmodResponse response = request.sendTo(this.output, this.input);
            
            this.checkRCOrError("CHMODE", response);             
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/IO error while performing CHMODE",ex); 
        }
    }

    /**
     * Gets a comment to the file or directory. 
     * Added by P.T. de Boer. 
     *  
     * @param path
     *            path to the file or directory
     * @param newComment
     *            new comment of the entry
     * @throws LFCException
     */
    public String getComment(final String path) throws LFCException
    {
        Assert.isTrue(path != null && path.length() != 0,
                Messages.LFCServer_empty_path);
        return this.GETCOMMENT(path); 
    }
    
    /**
     * Adds new replica to the file
     * 
     * @param fileDesc
     *            description of the file, guid and status must contain real
     *            data
     * @param replica
     *            URI of the replica
     * @throws LFCException
     */

    public void addReplica(final FileDesc fileDesc, final URI replica)
            throws LFCException
    {
        this.ADDREPLICA(fileDesc, replica);
    }
    
    /**
     * Fetches new file description from the server.
     * 
     * @param path
     *            path of the file or directory
     * @return this resource LFC specific meta-data class
     * @throws LFCException
     */

    public FileDesc fetchFileDesc(final String path) throws LFCException
    {
        return fetchFileDesc(path, false);
    }

    /**
     * Fetches new file description from the server.
     * <p>
     * Modified by P.T. de Boer: enable use of GUID.
     * 
     * @param path
     *            path of the file or directory
     * @return this resource LFC specific meta-data class
     * @throws LFCException
     * @throws LFCException
     */

    public FileDesc fetchFileDesc(String guidOrPath, boolean isGuid)
            throws LFCException
    {
        Assert.isTrue(guidOrPath != null && guidOrPath.length() != 0,
                Messages.LFCServer_empty_path);
        FileDesc result = null;

        // retrieve details
        result = this.STATG(guidOrPath, isGuid);
        return result;
    }

    /** 
     * New method to resolve a link path. 
     * Added by Piter T. de Boer. 
     * @param symlinkPath path to resolve 
     * @return resolved link path. 
     * @throws LFCException
     */
    public String readLink(final String symlinkPath) throws LFCException
    {
        return this.READLINK(symlinkPath);
    }

    /**
     * Fetch Link Description. Should not resolve the path and return 
     * the actual link description !  
     * (Under construction) 
     * Added by Piter T. de Boer
     * @param path
     *            logical path of the file or directory
     * @return resolves link file description 
     * @throws LFCException
     */

    public FileDesc fetchLinkDesc(final String path) throws LFCException
    {
        Assert.isTrue(path != null && path.length() != 0,
                Messages.LFCServer_empty_path);

        // retrieve details
        return LINKSTAT(path);
    }

    /**
     * Registers new entry in the catalogue, does not fill the replica
     * information
     * <p>
     * Modification made by P.T. de Boer:<br> 
     * - Must throw exception when the registration fails.<br>
     * - Added GUID as parameter for customized GUID.<br>
     * @param path
     *            path in the catalogue where new entry should appear
     * @param guid 
     *            custom or pre generated GUID 
     * @throws LFCException
     */

    public void registerEntry(final String path,final String guid) throws LFCException
    {
        this.CREATG(path,guid);
    }

    public void registerEntry(final String path) throws LFCException
    {
        this.CREATG(path);
    }

//    /**
//     * Registers new catalogue entry specified by the path, then registers new
//     * replica URI for that entry. It's a caller's duty to fill this replica
//     * with real data.
//     * 
//     * @param path
//     *            path to the catalogue entry
//     * @return URI of the newly registered replica
//     */
//    public URI registerAndPrepareCopy(final String path)
//    {
//        URI result = null;
//        try
//        {
//            logger.consoleLog("Registering new entry: " + path); //$NON-NLS-1$
//            registerEntry(path);
//
//            FileDesc desc = fetchFileDesc(path);
//
//            // === PDB ORIGINAL CODE ===
//
//            // URI replicaURI = URI.create( ( ( IReplicableFileSystem
//            // )EFS.getFileSystem( "lfn" ) ).getDefaultReplica(
//            // this.token.getVos()[0] ) //$NON-NLS-1$
//            // + "file" //$NON-NLS-1$
//            // + desc.getGuid() );
//
//            // === PDB CHANGED CODE START ===
//
//            // MLFCFileSystem lfcfs=new MLFCFileSystem();
//
//            URI replicaURI = URI
//                    .create("srm://srm.grid.sara.nl:8443/grid/pvier/spiros/generated/replica_"
//                            + this.hashCode() + "/");
//
//            // === PDB CHANGED CODE END ===
//
//            logger.consoleLog("Creating new replica: " + replicaURI.toString()); //$NON-NLS-1$
//            addReplica(desc, replicaURI);
//            result = replicaURI;
//
//        }
//        catch (LFCException exc)
//        {
//            logger.logException(exc);
//        }
//
//        return result;
//    }

    /**
     * Registers new entry in the catalogue, and specified replica as well
     * <p>
     * Modifications by P.T. de Boer: <br> - throw exception when something
     * fails
     * 
     * @param file
     * @param path
     * @param size
     * @throws LFCException
     */
    public void register(final URI file, final String path, final long size)
            throws LFCException
    {
        // register new entry
        registerEntry(path);

        FileDesc desc = fetchFileDesc(path);

        // adding first replica
        addReplica(desc, file);

        // setting file size
        setFileSize(desc.getGuid(), size, desc.getChkSumType(), desc
                .getChkSumValue());
    }

    /**
     * Unregisters the replica from the replica list of the file specified by
     * guid
     * <p>
     * Modification by P.T. de Boer:<br> - do not catch exceptions but let
     * method throw to up the call stack !
     * 
     * @param guid
     *            Global Unique ID of the file
     * @param replicaURI
     *            Replica URI
     * @throws LFCException
     */

    public void delReplica(final String guid, final URI replicaURI)
            throws LFCException
    {
        this.DELREPLICA(guid, replicaURI.toString());
    }

    /**
     * Removes file from the catalogue. Note that all replica information will
     * be removed as well, but replica files will be kept on the storage
     * elements.
     * 
     * @param guid
     *            global unique ID of the file to be removed
     * @param path
     *            location of the file
     * @throws LFCException
     */

    public void deleteFile(final String guid, final String path)
            throws LFCException
    {
        // first, remove all the replicas
        ArrayList<ReplicaDesc> replicas = this.getReplicas(guid);
        // I don't know why, but this looks to be necessary
        this.disconnect();

        for (ReplicaDesc replica : replicas)
        {
            this.DELREPLICA(guid, replica.getSfn());
        }

        this.UNLINK(path);
    }
    
    /** Unregister entry only: Does NOT delete replicas ! */ 
    public void unregister(String path) throws LFCException
    {
        this.UNLINK(path);
    }
    
    /**
     * Creates new directory in the catalogue.
     * 
     * @param path
     *            path of the directory.
     * @return FileDesc - metainformation of the newly created directory.
     * @throws LFCException
     */

    public FileDesc mkdir(final String path) throws LFCException
    {
        this.MKDIR(path);
        return this.STATG(path, false);
    }

    /**
     * Creates Symbolic Link .
     * <p>
     * @param sourcePath original path 
     * @param newLinkPath  new link path which points to sourcePath 
     * @return FileDesc - meta information of the newly created link 
     * @throws LFCException
     */

    public FileDesc createSymLink(final String sourcePath,
            final String newLinkPath) throws LFCException
    {
        this.SYMLINK(sourcePath, newLinkPath);
        // must disconnect ? 
        this.disconnect(); 
        // Must stat the link itself, not the link target ! 
        FileDesc result = this.LINKSTAT(newLinkPath);
        return result; 
    }

    /**
     * Removes the directory from the catalogue. Directory must be empty.
     * 
     * @param path
     *            path of the directory.
     * @throws LFCException
     */

    public void rmdir(final String path) throws LFCException
    {
        this.RMDIR(path);
    }

    /**
     * Moves and/or renames catalogue entry. Only path is changed, GUID stays
     * the same.
     * 
     * @param oldPath old path to the entry
     * @param newPath new path to the entry
     * @throws LFCException
     */

    public void move(final String oldPath, final String newPath)
            throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            CnsRenameRequest request = new CnsRenameRequest(oldPath, newPath);
            // PTdB: replace with void response:
            CnsVoidResponse  response = request.sendTo(this.output, this.input);

            this.checkRCOrError("LFC Rename", response.getMessage()); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/CREATG IO Problem.", ex);
        }
    }

    protected void CREATG(final String path) throws LFCException
    {
         CREATG(path,null);
    }

    /** 
     * Make new LFC File entry. 
     * Use path as new path and optional guid as new GUID. 
     * IF guid==null then a new GUID will be generated. 
     */ 
    protected void CREATG(final String path,final String guid) throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            CnsCreatGRequest request = new CnsCreatGRequest(path);
            // PTdB: specify custom GUID !
            if (guid!=null)
              request.setGuid(guid); 

            CnsCreatGResponse response = request
                    .sendTo(this.output, this.input);

            this.checkRCOrError("CREATG", response);
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/IO Error while performing CREATG for path:"+path, ex);
        }
    }

    protected void ADDREPLICA(final FileDesc fileDesc, final URI replica)
            throws LFCException
    {
        // Assert not LINK ! 
        if (fileDesc.isSymbolicLink())
            throw new LFCException("Cannot add replicas to link, must resolve link first."); 
        
        try
        {
            this.checkReConnected(); 
            String guid = fileDesc.getGuid();
            if(guid==null || guid.equals("")){
                throw new LFCException("Guid can't be null or empty!!");
            }
            CnsAddReplicaRequest request = new CnsAddReplicaRequest(fileDesc
                    .getGuid(), replica);
            request.setStatus(fileDesc.getStatus());

            CnsAddReplicaResponse response = request.sendTo(this.output,
                    this.input);
            
            this.checkRCOrError("ADDREPLICA", response);
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/IO Error while performing ADDREPLICA to:"+fileDesc.getGuid(),ex); 
        }
    }

    protected void DELREPLICA(final String guid, final String sfn)
            throws LFCException
    {
        try
        {
            this.checkReConnected(); 

            CnsDelReplicaRequest request = new CnsDelReplicaRequest(guid, 0x0,sfn);
            CnsDelReplicaResponse response = request.sendTo(this.output,this.input);

            this.checkRCOrError("DELREPLICA",response); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/DELREPLICA IO Problem.", ex); //$NON-NLS-1$
        }
    }

    protected void UNLINK(final String path) throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            // remove the directory entry
            CnsUnlinkRequest request = new CnsUnlinkRequest(path);
            CnsUnlinkResponse response = request
                    .sendTo(this.output, this.input);

            this.checkRCOrError("UNLINK", response);
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/UNLINK IO Problem.", ex);
        }
    }

    protected void OPENDIR(final String path) throws LFCException
    {
        try
        {
            this.checkReConnected();
            
            CnsOpenDirRequest request = new CnsOpenDirRequest(path);
            CnsLongResponse response = request.sendTo(this.output,
                    this.input);
            
            this.checkRCOrError("OPENDIR", response); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/OPENDIR IO Problem.", ex);
        }
    }

    protected ArrayList<FileDesc> READDIR(final long fileId)
            throws LFCException
    {
        ArrayList<FileDesc> result;

        try
        {
            checkReConnected();

            CnsReadDirRequest request = new CnsReadDirRequest(fileId);
            CnsReadDirResponse response = request.sendTo(this.output,
                    this.input);

            result = response.getFileDescs();
            
            LFCServer.staticLogIOMessage("READDIR: Read #"+result.size()+" entries)");
            
            checkRCOrError("READDIR (1)", response);

            //PTdB: Check for More Data ! 
            while (response.getEod()!=1)
            {
                LFCServer.staticLogIOMessage("READDIR: More data available !"); 
                request = new CnsReadDirRequest(fileId,true);
                response = request.sendTo(this.output, this.input);


                ArrayList<FileDesc> result2 = response.getFileDescs();
                
                LFCServer.staticLogIOMessage("READDIR: Read #"+result2.size()+" more entries)");
                
                result.addAll(result2); 
            
                checkRCOrError("READDIR (2)", response);
            }
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/READDIR IO Problem.", ex);
        }
        return result;
    }

    protected void CLOSEDIR() throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            CnsCloseDirRequest request = new CnsCloseDirRequest();
            CnsCloseDirResponse response = request.sendTo(this.output,
                    this.input);

            this.checkRCOrError("CLOSEDIR", response);  
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/CLOSEDIR IO Problem.", ex);
        }
    }

    protected void MKDIR(final String path) throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            CnsMkdirRequest request = new CnsMkdirRequest(path);
            // PdB: Use New VoidResponse Type: 
            CnsVoidResponse response = request.sendTo(this.output, this.input);

            this.checkRCOrError("MKDIR:"+path, response.getMessage()); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/MKDIR IO Problem.");
        }
    }

    protected void RMDIR(final String path) throws LFCException
    {
        Assert.isTrue(path != null && path.length() != 0,
                Messages.LFCServer_empty_path);
        try
        {
            this.checkReConnected();

            CnsRmdirRequest request = new CnsRmdirRequest(path);
            CnsVoidResponse response = request.sendTo(this.output, this.input);

            this.checkRCOrError("RMDIR:"+path,response.getMessage()); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/RMDIR IO Problem.", ex); 
        }
    }

    /** Added by P.T de Boer. Create link or alias  */ 
    protected void SYMLINK(final String sourcePath, final String newLinkPath)
            throws LFCException
    {
        Assert.isTrue(sourcePath != null && sourcePath.length() != 0,
                " Symlink: source path is empty");

        try
        {
            this.checkReConnected();

            CnsSymLinkRequest request = new CnsSymLinkRequest(sourcePath,
                    newLinkPath);
            CnsVoidResponse response = request.sendTo(this.output, this.input);

            this.checkRCOrError("SYMLINK",response.getMessage()); 
            
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/RMDIR IO Problem.", ex);
        }
    }

    protected FileDesc STATG(final String path) throws LFCException
    {
        return STATG(path, false);
    }

    /**
     * Stat file using either the path or the guid.
     * <p>
     * Modification by P.T. de Boer:<br> - Added useGuid parameter to determine
     * whether to use the path of guid.
     * 
     * @param pathOrGuid
     * @param useGuid
     *            if useGuid==true the pathOrGuid string means Guid.
     * @return File Description
     * @throws LFCException
     */
    protected FileDesc STATG(final String pathOrGuid, boolean useGuid)
            throws LFCException
    {
        FileDesc result = null;
        try
        {
            checkReConnected();

            CnsGStatRequest request = new CnsGStatRequest("");

            if (useGuid)
            {
                request.setPath(""); // empty path (cannot be null)
                request.setGuid(pathOrGuid);
            }
            else
            {
                request.setPath(pathOrGuid);
                request.setGuid("");
            }

            CnsGStatResponse response = request.sendTo(this.output, this.input);
            
            checkRCOrError("STATG", response);

            result = response.getFileDesc();
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/STATG IO Problem.", ex);
        }
        return result;
    }

    /**
     * LINKSTAT: resolves a link and peforms a stat on the link TARGET. 
     * 
     * Added by Piter T. de Boer. 
     * 
     * @param path link path to 'stat'. 
     * @return File Description 
     * @throws LFCException
     */
    protected FileDesc LINKSTAT(final String path) throws LFCException
    {
        FileDesc result = null;

        try
        {
            checkReConnected();

            CnsLinkStatRequest request = new CnsLinkStatRequest(path);
            CnsLinkStatResponse response = request.sendTo(this.output,
                    this.input);

            checkRCOrError("LINKSTAT", response);

            result = response.getFileDesc();
            return result;
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/STATG IO Problem.",ex);
        }

    }

    /**
     * Added by Piter T. de Boer.
     *  
     * @param path 
     * @throws LFCException
     */

    protected String READLINK(final String path) throws LFCException
    {
        String result = null;

        try
        {
            checkReConnected();

            CnsReadLinkRequest request = new CnsReadLinkRequest(path);
            CnsSingleStringResponse response = request.sendTo(this.output,this.input);
            result = response.getString(); 

            checkRCOrError("READLINK", response);
            
            // === PTdB === 
            // It seems that after creating a link the 
            // server must disconnect before doing a new stat ! 
            this.disconnect(); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/READLINK IO Problem.", ex); 
        }
        return result;
    }

  

    protected void SETCOMMENT(final String path, final String comment)
            throws LFCException
    {
        try
        {
            this.checkReConnected(); 
            
            CnsSetCommentRequest request = new CnsSetCommentRequest(path);
            request.setComment(comment);
            
            CnsSetCommentResponse response = request.sendTo(this.output, this.input);
                        
            this.checkRCOrError("SETCOMMENT", response); 
            
//            debug("LFC responce. Type: "+response.getType()+" "+CnsConstants.getResponseType(response.getType()));
            
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/IO error while performing SETCOMMENT",ex); 
        }
    }

    protected String GETCOMMENT(final String path) throws LFCException
    {
        try
        {
            this.checkReConnected(); 

            CnsGetCommentRequest request = new CnsGetCommentRequest(path);
            CnsSingleStringResponse response = request.sendTo(this.output,this.input);

            this.checkRCOrError("GETCOMMENT", response); 
    
            return response.getString(); 
    
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/IO error while performing GETCOMMENT",ex); 
        }
    }
    
    private void debug(String msg)
    {
//        Global.debugPrintln(this, msg);
        System.err.println(this.getClass().getName()+": "+msg);
    }

    /**
     * Added by Piter T. de Boer
     * List Links of a path of guid.
     *  
     * @param response
     * @throws LFCException
     */
    protected ArrayList<String> LISTLINKS(final String pathOrGuid,boolean useGuid) throws LFCException
    {
        ArrayList<String>  result = null;

        try
        {
            checkReConnected();

            CnsListLinkRequest request = new CnsListLinkRequest(pathOrGuid,useGuid,1);
            CnsStringListResponse response = request.sendTo(this.output,this.input);
            result = response.getStringList(); 
            
            checkRCOrError("READLINKS", response);
            
            // more data ? 
            while (response.getEOL()==0)
            {
                request = new CnsListLinkRequest(pathOrGuid,useGuid,0);
                response = request.sendTo(this.output,this.input);
                result.addAll(response.getStringList()); 
            }
            
            this.disconnect(); 
        }
        catch (IOException ex)
        {
            throw new LFCException("LFC/READLINK IO Problem.", ex); 
        }
        return result;
    }
    
    /**
     *  List Links using either GUID of Path. 
     *  Added by P.T. de Boer. 
     *  @throws LFCException
     */ 
    public ArrayList<String> listLinksByGuid(String guidOrPath,boolean useGuid) throws LFCException
    {
        return this.LISTLINKS(guidOrPath,useGuid); 
    }
    
    /** 
     * List Links using GUID.
     *  
     * Returns list of links. First entry should be original file. 
     * 
     * Added by P.T. de Boer.
     * 
     * @throws LFCException 
     */ 
    public ArrayList<String> listLinks(String guid) throws LFCException
    {
        return this.LISTLINKS(guid,true); 
    }

    
    // ========================================================================
    // Helpers method added by Piter T. de Boer 
    // ========================================================================
    
    /**
     * Helper method to check and reset the security context or detect
     * other error. 
     * 
     * Added by Piter T. de Boer.
     * 
     * @param response
     * @throws LFCException
     */
    protected void checkRCOrError(String action,
            AbstractCnsResponse response) throws LFCException
    {
        if (response.type == CnsConstants.CNS_RC)
        {
            staticLogMessage(">>>" + action
                    + ": Received Security Reset Context Response ! <<<");

            this.disconnect();

            if (response.getErrorCode() != 0)
            {
                throw new LFCException("Error while performing:"+action,response.getErrorCode()); 
            }
        }
    }
    
    /** New Method */ 
    protected void checkRCOrError(String action, CnsMessage message) throws IOException, LFCException
    {
        if (message.type() == CnsConstants.CNS_RC)
        {
            staticLogMessage(">>>" + action
                    + ": Received Security Reset Context Response ! <<<");

            this.disconnect();

            if (message.error() != 0)
            {
                throw new LFCException("Error while performing:"+action,message.error()); 
            }
        }
    }
    
    /**
     * Check if this server is  connected and reconnect if necessary. 
     * Added by P.T. de Boer. 
     * @throws LFCException
     */
    public void checkReConnected() throws LFCException
    {
        if (!this.isConnected())
        {
            connect();
        }
    }
    
    public void dispose()
    {
        disconnect();
    }
    
    public void finalize()
    {
        disconnect();
    }
      

//    public DataOutputStream _getOutput()
//    {
//       return this.output; 
//    }
//
//    public DataInputStream _getInput()
//    {
//        return this.input; 
//    }
  

   
}
