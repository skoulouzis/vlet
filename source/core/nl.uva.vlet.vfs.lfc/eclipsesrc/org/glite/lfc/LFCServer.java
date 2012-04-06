/*****************************************************************************
 * Copyright (c) 2007, 2008 g-Eclipse Consortium 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis - initial API and implementation
 *    
 * Modifications:
 *    Piter T. de Boer - Stripped away GEclipse dependencies. 
 *                     - further improvements and extra methods. 
 *    Spiros Koulouzis - further improvements and extra methods.
 *****************************************************************************/

package org.glite.lfc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;

import org.glite.lfc.internal.*;
import org.globus.common.CoGProperties;
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
	
	/**
	 *  Package protected class LFCLogger. 
	 *  Is also used by the Cns... communication objects to log IO message and exceptions.  
	 */
	protected static LFCLogger logger = new LFCLogger();

	/** IO expeption logger */ 
	public static void staticLogIOException(final Exception ex) 
	{
		logger.ioLogException(ex); 
	}

	/** IO message logger */ 
	public static void staticLogIOMessage(final String msg) 
	{
		logger.ioLogMessage(msg); 
	}
	
	/**
	 * Set Custom Class Logger. 
	 * Parameter newLogger is instance or subclass of LFCLogger. 
	 * (subclass LFCLogger for more control over the logging). 
	 * @param newLogger the new class logger to use.
	 */
	public static void setLogger(LFCLogger newLogger)
	{
		logger=newLogger; 
	}
	
	/** Return Class Logger */ 
	public static LFCLogger getLogger()
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
			logger.logMessage(String.format(Messages.lfc_log_connect, this.uri
					.getHost(), new Integer(this.uri.getPort())));

			this.socket = new Socket(this.uri.getHost(), this.uri.getPort());

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
	}

	/**
	 * Authenticates client/server via Extended GSS mechanism
	 * 
	 * @throws GSSException
	 *             in case of authentication problems
	 * @throws IOException
	 * @throws IOException
	 *             in case of any I/O problem
	 */
	private boolean authenticate() throws GSSException, LFCException,
			IOException
	{
		boolean result = false;
		logger.logMessage(Messages.lfc_log_authenticate);

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
				e.printStackTrace();
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
				logger.logMessage(Messages.lfc_log_authenticated);
				result = true;
		}
		catch (IOException ioExc)
		{
			ioExc.printStackTrace();

			logger.logException(ioExc);
			
			throw ioExc;//rethrow  
		}
		
		return result;
	}
	
	/**
	 * Disconnects from the LFC server. Clears socket, context and all data
	 * streams
	 * 
	 * @throws LFCException
	 *             If an I/O-problem occurs.
	 */
	public void disconnect() throws LFCException
	{
		try
		{
			logger.logMessage(String.format(Messages.lfc_log_disconnect,
					this.uri.getHost(), new Integer(this.uri.getPort())));

			if (this.context != null)
			{
				this.context.dispose();
			}
			if (this.socket != null)
			{
				this.input.close();
				this.output.close();
				this.socket.close();
			}
		}
		catch (IOException ioExc)
		{
			throw new LFCException("eu.geclipse.core.problem.io.unspecified",
					ioExc);
		}
		catch (GSSException gssExc)
		{
			throw new LFCException("eu.geclipse.core.problem.auth.loginFailed", //$NON-NLS-1$
					gssExc);
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
		this.CLOSEDIR();

		return result;
	}

	/**
	 * Retrieves list of replicas for a given guid.
	 * 
	 * @param guid
	 *            Global (Grid) Unique ID of the file
	 * @return list of {@link ReplicaDesc} describing replicas
	 * @throws LFCException
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

			this.checkResetSecurityContext("listReplicas", response);
			
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/LISTREPLICAS IO Problem.", ex);
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

			this.checkResetSecurityContext("setFileSize of:+ guid",response);  
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
	 * Stat a link: Added by Piter T. de Boer 
	 * 
	 * Fetches new file description from the server. Seems to return the same
	 * information as fetchFileDesc
	 * 
	 * @param path
	 *            logical path of the file or directory
	 * @return this resource LFC specific meta-data class
	 * @throws LFCException
	 */

	public FileDesc fetchLinkDesc(final String path) throws LFCException
	{
		Assert.isTrue(path != null && path.length() != 0,
				Messages.LFCServer_empty_path);
		FileDesc result = null;

		// retrieve details
		return LINKSTAT(path);
	}

	/**
	 * Registers new entry in the catalogue, does not fill the replica
	 * information
	 * <p>
	 * Modification made by P.T. de Boer:<br> - Must throw exception when the
	 * registration fails.
	 * 
	 * @param path
	 *            path in the catalogue where new entry should appear
	 * @throws LFCException
	 */

	public void registerEntry(final String path) throws LFCException
	{
		this.CREATG(path);
	}

//	/**
//	 * Registers new catalogue entry specified by the path, then registers new
//	 * replica URI for that entry. It's a caller's duty to fill this replica
//	 * with real data.
//	 * 
//	 * @param path
//	 *            path to the catalogue entry
//	 * @return URI of the newly registered replica
//	 */
//	public URI registerAndPrepareCopy(final String path)
//	{
//		URI result = null;
//		try
//		{
//			logger.consoleLog("Registering new entry: " + path); //$NON-NLS-1$
//			registerEntry(path);
//
//			FileDesc desc = fetchFileDesc(path);
//
//			// === PDB ORIGINAL CODE ===
//
//			// URI replicaURI = URI.create( ( ( IReplicableFileSystem
//			// )EFS.getFileSystem( "lfn" ) ).getDefaultReplica(
//			// this.token.getVos()[0] ) //$NON-NLS-1$
//			// + "file" //$NON-NLS-1$
//			// + desc.getGuid() );
//
//			// === PDB CHANGED CODE START ===
//
//			// MLFCFileSystem lfcfs=new MLFCFileSystem();
//
//			URI replicaURI = URI
//					.create("srm://srm.grid.sara.nl:8443/grid/pvier/spiros/generated/replica_"
//							+ this.hashCode() + "/");
//
//			// === PDB CHANGED CODE END ===
//
//			logger.consoleLog("Creating new replica: " + replicaURI.toString()); //$NON-NLS-1$
//			addReplica(desc, replicaURI);
//			result = replicaURI;
//
//		}
//		catch (LFCException exc)
//		{
//			logger.logException(exc);
//		}
//
//		return result;
//	}

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
		ArrayList<ReplicaDesc> replicas = this.listReplicas(guid);
		// I don't know why, but this looks to be necessary
		this.disconnect();

		for (ReplicaDesc replica : replicas)
		{
			this.DELREPLICA(guid, replica.getSfn());
		}

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
	 * Creates Symbolic Link: UNDER CONSTRUCTION (By P.T. de Boer).
	 * <p>
	 * @param sourcePath original path 
	 * @param newLinkPath  new link path which points to sourcePath 
	 * @return FileDesc - metainformation of the newly created link 
	 * @throws LFCException
	 */

	public FileDesc createSymLink(final String sourcePath,
			final String newLinkPath) throws LFCException
	{
		this.SYMLINK(sourcePath, newLinkPath);
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

			this.checkResetSecurityContext("LFC Rename", response); 
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/CREATG IO Problem.", ex);
		}
	}

	protected void CREATG(final String path) throws LFCException
	{
		try
		{
			this.checkReConnected(); 
			
			CnsCreatGRequest request = new CnsCreatGRequest(path);
			CnsCreatGResponse response = request
					.sendTo(this.output, this.input);

			this.checkResetSecurityContext("CREATG", response);
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/IO Error while performing CREATG for path:"+path, ex);
		}
	}

	protected void ADDREPLICA(final FileDesc fileDesc, final URI replica)
			throws LFCException
	{
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
			
			this.checkResetSecurityContext("ADDREPLICA", response);
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

			this.checkResetSecurityContext("DELREPLICA",response); 
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

			this.checkResetSecurityContext("UNLINK", response);
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
			
			this.checkResetSecurityContext("OPENDIR", response); 
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

			checkResetSecurityContext("READDIR", response);
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

			this.checkResetSecurityContext("CLOSEDIR", response);  
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

			this.checkResetSecurityContext("MKDIR:"+path, response); 
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

			this.checkResetSecurityContext("RMDIR:"+path,response);
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

			this.checkResetSecurityContext("SYMLINK",response); 
			
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
			
			checkResetSecurityContext("STATG", response);

			result = response.getFileDesc();
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/STATG IO Problem.", ex);
		}
		return result;
	}

	/**
	 * Added by Piter T. de Boer It seems LinkStat returns the same information
	 * as STATG.
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

			checkResetSecurityContext("LINKSTAT", response);

			result = response.getFileDesc();
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/STATG IO Problem.",ex);
		}
		return result;
	}

	/**
	 * Added by Piter T. de Boer
	 * 
	 * Resolve the target of a Link ? Doesn't work: got Error 22
	 * 
	 * @param response
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
			result = response.linkToPath;

			checkResetSecurityContext("READLINK", response);
			
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

			CnsSetCommentResponse response = request.sendTo(this.output,
					this.input);

			this.checkResetSecurityContext("SETCOMMENT", response); 
		}
		catch (IOException ex)
		{
			throw new LFCException("LFC/IO error while performing SETCOMMENT",ex); 
		}
	}

	// ========================================================================
	// Helpers method added by Piter T. de Boer 
	// ========================================================================
	
	/**
	 * Added by Piter T. de Boer
	 * 
	 * Helper method to check and reset the security context if needed.
	 * 
	 * @param response
	 * @throws LFCException
	 */
	protected void checkResetSecurityContext(String action,
			AbstractCnsResponse response) throws LFCException
	{
		if (response.type == CnsConstants.CNS_RC)
		{
			logger.logMessage(">>>" + action
					+ ": Received Security Reset Context Response ! <<<");

			this.disconnect();

			if (response.getErrorCode() != 0)
			{
				throw new LFCException("Error while performing:"+action,response.getErrorCode()); 
			}
		}
	}
	
	/**
	 * Check if this server is  connected and reconnect if necessary. 
	 * Added by P.T. de Boer. 
	 * @throws LFCException
	 */
	protected void checkReConnected() throws LFCException
	{
		if (!this.isConnected())
		{
			connect();
		}
	}

}
