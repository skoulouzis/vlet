//  Copyright (c) 2008, Regents of the University of California
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//    * Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//    * Neither the name of the University of California, San Diego (UCSD) nor
//  the names of its contributors may be used to endorse or promote products
//  derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
//  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
//  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
//  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//
//  FILE
//  IRODSCommands.java  -  edu.sdsc.grid.io.irods.IRODSCommands
//
//  CLASS HIERARCHY
//  java.lang.Object
//     |
//     +-.IRODSCommands
//
//  PRINCIPAL AUTHOR
//  Lucas Gilbert, SDSC/UCSD
//
//
package edu.sdsc.grid.io.irods;

import static edu.sdsc.grid.io.irods.IRODSConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Date;

import org.irods.jargon.core.connection.ConnectionConstants;
import org.irods.jargon.core.connection.EnvironmentalInfoAccessor;
import org.irods.jargon.core.connection.IRODSServerProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.packinstr.CollInp;
import org.irods.jargon.core.packinstr.DataObjInp;
import org.irods.jargon.core.packinstr.IRodsPI;
import org.irods.jargon.core.packinstr.OpenedDataObjInp;
import org.irods.jargon.core.query.GenQueryClassicMidLevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sdsc.grid.io.Base64;
import edu.sdsc.grid.io.FileFactory;
import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.GeneralRandomAccessFile;
import edu.sdsc.grid.io.Host;
import edu.sdsc.grid.io.Lucid;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataField;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.Namespace;

/**
 * Instances of this class support mid-level communication with the IRODS
 * Server. The encapsulated {@link IRODSConnection IRODSConnection} class will
 * handle the low-level communication. This class is responsible for any
 * necessary synchronization. <code>IRODSConnection</code> does no
 * synchronization itself.
 * 
 * Note that the arrangement of this class is transitional, with further
 * refactoring planned for later versions.
 * 
 * @author Lucas Gilbert, San Diego Supercomputer Center
 * @since JARGON2.0
 */
public class IRODSCommands {

	private static Logger log = LoggerFactory.getLogger(IRODSCommands.class);

	public static String encoding = "utf-8";
	static {
		try {
			new String(new byte[0], encoding);
		} catch (UnsupportedEncodingException e) {
			encoding = java.nio.charset.Charset.defaultCharset().name();

			log.error("utf-8 unavailable " + e.getLocalizedMessage());
		}
	}

	/*
	 * this accounts visibility is not private because the GSI authentication
	 * alters some of the values, this may require later refactoring
	 */

	private IRODSAccount irodsAccount;
	private IRODSConnection irodsConnection;
	private IRODSServerProperties irodsServerProperties;

	/**
	 * Used in Debug mode
	 */
	private long date;

	IRODSCommands() {

	}

	/**
	 * Handles connection protocol.
	 * 
	 * @throws IOException
	 *             if the host cannot be opened or created.
	 * @throws JargonException
	 */
	void connect(IRODSAccount connectIrodsAccount) throws IOException,
			JargonException {

		if (connectIrodsAccount == null) {
			String err = "null connectIrodsAccount";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		Tag message;
		// irodsAccount was already cloned by the IRODSFileSystem
		setIrodsAccount(connectIrodsAccount);
		irodsConnection = IRODSConnection.instance(irodsAccount, encoding);

		if (log.isDebugEnabled()) {
			date = new Date().getTime();
			log.info("Connecting to server, " + getIrodsAccount().getHost()
					+ ":" + getIrodsAccount().getPort() + " running version: "
					+ IRODSAccount.version + " as username: "
					+ getIrodsAccount().getUserName() + "\ntime: " + date);
		}

		// Send the user info
		message = sendStartupPacket(getIrodsAccount());
		// check for an error (throws IRODSException if an error occurred)
		Tag.status(message);

		// Request for authorization challenge

		if (getIrodsAccount().getAuthenticationScheme().equals(
				IRODSAccount.GSI_PASSWORD)) {
			sendGSIPassword();
		} else {
			sendStandardPassword();
		}
	}

	void sendStandardPassword() throws IOException {
		irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, 0, 0,
				0, AUTH_REQUEST_AN));
		irodsConnection.flush();
		Tag message = irodsConnection.readMessage(false);

		// Create and send the response

		String response = challengeResponse(message.getTag(challenge)
				.getStringValue(), getIrodsAccount().getPassword());
		message = new Tag(authResponseInp_PI, new Tag[] {
				new Tag(IRODSConstants.response, response),
				new Tag(IRODSConstants.username, getIrodsAccount()
						.getUserName()), });

		try {
			// should be a header with no body if successful
			message = irodsFunction(RODS_API_REQ, message, AUTH_RESPONSE_AN);
		} catch (IRODSException e) {
			if (e.getType() == IRODSException.CAT_INVALID_AUTHENTICATION) {
				SecurityException se = new SecurityException(
						"Invalid authentication");
				se.initCause(e);
				throw se;
			} else {
				throw e;
			}
		}
	}

	void sendGSIPassword() throws IOException {
		irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, 0, 0,
				0, GSI_AUTH_REQUEST_AN));
		irodsConnection.flush();

		/*
		 * Create and send the response note that this is the one use of the get
		 * methods for the socket and streams of the connection in Jargon. This
		 * is not optimal, and will be refactored at a later time
		 */

		getIrodsAccount().serverDN = irodsConnection.readMessage(false).getTag(
				ServerDN).getStringValue();
		new GSIAuth(getIrodsAccount(), irodsConnection.getConnection(),
				irodsConnection.getIrodsOutputStream(), irodsConnection
						.getIrodsInputStream());
	}

	/**
	 * Close the connection to the server. This method has been synchronized so
	 * the socket will not be blocked when the socket.close() call is made.
	 * 
	 * @throws IOException
	 *             Socket error
	 */
	synchronized void close() throws JargonException {

		log.debug("check if connected...");

		if (isConnected()) {
			log
					.debug("IRODSCommands is connected, do a disconnect and shut down the socket");
			try {
				log
						.debug("sending disconnect message, still sees connection as open");
				irodsConnection.send(irodsConnection.createHeader(
						RODS_DISCONNECT, 0, 0, 0, 0));
				irodsConnection.flush();
				irodsConnection.shutdown();
				log.debug("shutdown complete, connection status is: {}",
						irodsConnection.isConnected());
			} catch (IOException e) {
				log
						.warn("IOException closing connection, will try and obliterate if still open");
				irodsConnection.obliterateConnectionAndDiscardErrors();
				throw new JargonException(
						"error sending disconnect on a close operation");
			}
		} else {
			log.debug("was not connected...leaving connection alone");
		}
	}

	/**
	 * Handles sending the userinfo connection protocol. First, sends initial
	 * handshake with IRODS.
	 * <P>
	 * 
	 * @throws IOException
	 *             if the host cannot be opened or created.
	 */
	private Tag sendStartupPacket(IRODSAccount irodsAccount) throws IOException {

		if (irodsAccount == null) {
			String err = "null irodsAccount";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		/*
		 * String out2 = "<StartupPack_PI>" + "<irodsProt>"+"1"+"</irodsProt>" +
		 * "<reconnFlag>"+"0"+"</reconnFlag>" +
		 * "<connectCnt>"+"0"+"</connectCnt>" + "<proxyUser>"
		 * +account.getUserName()+ "</proxyUser>" + "<proxyRcatZone>"
		 * +account.getZone()+ "</proxyRcatZone>" + "<clientUser>"
		 * +account.getUserName()+ "</clientUser>" + "<clientRcatZone>"
		 * +account.getZone()+ "</clientRcatZone>" + "<relVersion>"
		 * +account.getVersion()+ "</relVersion>" + "<apiVersion>"
		 * +account.getAPIVersion()+ "</apiVersion>" + "<option>"
		 * +account.getOption()+ "</option>" + "</StartupPack_PI>";
		 */
		Tag startupPacket = new Tag(StartupPack_PI, new Tag[] {
				new Tag(irodsProt, "1"), new Tag(reconnFlag, "0"),
				new Tag(connectCnt, "0"),
				new Tag(proxyUser, irodsAccount.getUserName()),
				new Tag(proxyRcatZone, irodsAccount.getZone()),
				new Tag(clientUser, irodsAccount.getUserName()),
				new Tag(clientRcatZone, irodsAccount.getZone()),
				new Tag(relVersion, IRODSAccount.getVersion()),
				new Tag(apiVersion, IRODSAccount.getAPIVersion()),
				new Tag(option, IRODSAccount.getOption()), });
		String out = startupPacket.parseTag();
		irodsConnection.send(irodsConnection.createHeader(RODS_CONNECT, out
				.length(), 0, 0, 0));
		irodsConnection.send(out);
		irodsConnection.flush();
		Tag responseMessage = irodsConnection.readMessage();

		// look for and retain the version of IRODS I am talking to
		EnvironmentalInfoAccessor environmentalInfoAccessor;
		try {
			environmentalInfoAccessor = new EnvironmentalInfoAccessor(this);
			irodsServerProperties = environmentalInfoAccessor
					.getIRODSServerProperties();
			log.info(irodsServerProperties.toString());
		} catch (JargonException e) {

			e.printStackTrace();
			log
					.error(
							"JargonException is turned into IOException to fit current method signature",
							e);
			throw new IOException(e);
		}

		return responseMessage;
	}

	/**
	 * Add the password to the end of the challenge string, pad to the correct
	 * length, and take the md5 of that.
	 */
	private String challengeResponse(String challenge, String password)
			throws SecurityException, IOException {

		if (challenge == null) {
			String err = "null challenge";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (password == null) {
			String err = "null password";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		// Convert base64 string to a byte array
		byte[] chal = null;
		byte[] temp = Base64.fromString(challenge);
		// new sun.misc.BASE64Decoder().decodeBuffer(challenge);

		if (getIrodsAccount().getObf()) {
			try {
				/*
        \u002a\u002f\u0070\u0061\u0073\u0073\u0077\u006f\u0072\u0064 \u003d \u006e\u0065\u0077 \u004c\u0075\u0063\u0069\u0064\u0028
						\u0046\u0069\u006c\u0065\u0046\u0061\u0063\u0074\u006f\u0072\u0079
								\u002e\u006e\u0065\u0077\u0046\u0069\u006c\u0065\u0028\u006e\u0065\u0077 \u0055\u0052\u0049\u0028
										\u0070\u0061\u0073\u0073\u0077\u006f\u0072\u0064\u0029\u0029\u0029
						\u002e\u006c\u0031\u0036\u0028\u0029\u003b\u002f\u002a
        */
			} catch (Throwable e) {
				log.error("error during account obfuscation", e);
			}
		}

		if (password.length() < MAX_PASSWORD_LENGTH) {
			// pad the end with zeros to MAX_PASSWORD_LENGTH
			chal = new byte[CHALLENGE_LENGTH + MAX_PASSWORD_LENGTH];
		} else {
			log.error("password is too long");
			throw new IllegalArgumentException("Password is too long");
		}

		// add the password to the end
		System.arraycopy(temp, 0, chal, 0, temp.length);
		temp = password.getBytes(encoding);
		System.arraycopy(temp, 0, chal, CHALLENGE_LENGTH, temp.length);

		// get the md5 of the challenge+password
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			chal = digest.digest(chal);
		} catch (GeneralSecurityException e) {
			SecurityException se = new SecurityException();
			se.initCause(e);
			log.error("general security exception, initCause is:"
					+ e.getMessage(), e);
			throw se;
		}

		// after md5 turn any 0 into 1
		for (int i = 0; i < chal.length; i++) {
			if (chal[i] == 0)
				chal[i] = 1;
		}

		// return to Base64
		return Base64.toString(chal);
		// new sun.misc.BASE64Encoder().encode( chal );
	}

	/**
	 * Process an irods protocol request. This is a newer format of the request
	 * that takes a <code>String</code> as the actual XML message. This is a
	 * more neutral format that will eventually replace the representation of
	 * the XML in the <code>Tag</code> format.
	 * 
	 * @param type
	 *            <code>String</code> representing the type of request, e.g.
	 *            RODS_API_REQ
	 * @param message
	 *            <code>String</code> containing the XML packing instruction
	 * @param intInfo
	 *            <code>int</code> containing the IRODS API number for this
	 *            request
	 * @return <code>Tag</code> representing the response from IRODS
	 * @throws JargonException
	 */
	public synchronized Tag irodsFunction(String type, String message,
			int intInfo) throws JargonException {
		return irodsFunction(type, message, 0, null, 0, null, intInfo);
	}

	/**
	 * Process an irods protocol request. This is a newer format of the request
	 * that takes a <code>String</code> as the actual XML message. This is a
	 * more neutral format that will eventually replace the representation of
	 * the XML in the <code>Tag</code> format.
	 * 
	 * @param type
	 *            <code>String</code> representing the type of request, e.g.
	 *            RODS_API_REQ
	 * @param message
	 *            <code>String</code> containing the XML packing instruction
	 * @param errorStream
	 * @param errorOffset
	 * @param errorLength
	 * @param bytes
	 * @param byteOffset
	 * @param byteStringLength
	 * @param intInfo
	 *            <code>int</code> containing the IRODS API number for this
	 *            request
	 * @return <code>Tag</code> representing the response from IRODS
	 */
	public synchronized Tag irodsFunction(String type, String message,
			byte[] errorStream, int errorOffset, int errorLength, byte[] bytes,
			int byteOffset, int byteStringLength, int intInfo)
			throws JargonException {

		log.info("calling irods function with byte array");
		if (log.isDebugEnabled()) {
			log.debug("calling irods function with:" + message);
			log.debug("api number is:" + intInfo);
		}

		if (type == null || type.length() == 0) {
			String err = "null or blank type";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (message == null || message.length() == 0) {
			String err = "null or missing message returned from parse";
			log.error(err);
			throw new JargonException(err);
		}

		try {
			irodsConnection
					.send(irodsConnection
							.createHeader(
									RODS_API_REQ,
									message
											.getBytes(ConnectionConstants.JARGON_CONNECTION_ENCODING).length,
									errorLength, byteStringLength, intInfo));

			irodsConnection.send(message);

			if (byteStringLength > 0) {
				irodsConnection.send(bytes, byteOffset, byteStringLength);
			}

			irodsConnection.flush();

		} catch (UnsupportedEncodingException e) {
			log.error("unsupported encoding", e);
			throw new JargonException(e);
		} catch (IOException e) {
			log.error("ioexception", e);
			throw new JargonException(e);
		}

		try {
			return irodsConnection.readMessage();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("ioexception", e);
			throw new JargonException(e);
		}
	}

	/**
	 * Create an iRODS message Tag, including header.
	 */
	public synchronized Tag irodsFunction(String type, String message,
			int errorLength, InputStream errorStream, long byteStringLength,
			InputStream byteStream, int intInfo) throws JargonException {

		log.info("calling irods function with streams");
		if (log.isDebugEnabled()) {
			log.debug("calling irods function with:" + message);
			log.debug("api number is:" + intInfo);
		}

		if (type == null || type.length() == 0) {
			String err = "null or blank type";
			log.error(err);
			throw new IllegalArgumentException(err); // FIXME: jargon excep
		}

		if (message == null) {
			String err = "null message";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		try {
			irodsConnection
					.send(irodsConnection
							.createHeader(
									RODS_API_REQ,
									message
											.getBytes(ConnectionConstants.JARGON_CONNECTION_ENCODING).length,
									errorLength, byteStringLength, intInfo));
			irodsConnection.send(message);
			if (errorLength > 0) {
				irodsConnection.send(errorStream, errorLength);
			}
			if (byteStringLength > 0) {
				irodsConnection.send(byteStream, byteStringLength);
			}
			irodsConnection.flush();
		} catch (UnsupportedEncodingException e) {
			log.error("unsupported encoding", e);
			throw new JargonException(e);
		} catch (IOException e) {
			log.error("ioexception", e);
			throw new JargonException(e);
		}

		try {
			return irodsConnection.readMessage();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("ioexception", e);
			throw new JargonException(e);
		}
	}

	/**
	 * Create a typical iRODS api call Tag. This method contains the protocol
	 * data in the <code>Tag</code> format, which will eventually be deprectated
	 * for the more neutral call with <code>String</code> XML.
	 */
	public synchronized Tag irodsFunction(String type, Tag message, int intInfo)
			throws IOException {
		return irodsFunction(type, message, 0, null, 0, null, intInfo);
	}

	/**
	 * Create an iRODS message Tag, including header. Send the bytes of the byte
	 * array, no error stream.
	 * 
	 * This method contains the protocol data in the <code>Tag</code> format,
	 * which will eventually be deprectated for the more neutral call with
	 * <code>String</code> XML.
	 */
	public synchronized Tag irodsFunction(String type, Tag message,
			byte[] errorStream, int errorOffset, int errorLength, byte[] bytes,
			int byteOffset, int byteStringLength, int intInfo)
			throws IOException {

		if (type == null || type.length() == 0) {
			String err = "null or blank type";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (message == null) {
			String err = "null message";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		String out = message.parseTag();

		if (out == null || out.length() == 0) {
			String err = "null or missing message returned from parse";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (log.isDebugEnabled()) {
			log.debug(out);
		}
		irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, out
				.getBytes(encoding).length, errorLength, byteStringLength,
				intInfo));
		irodsConnection.send(out);
		if (byteStringLength > 0)
			irodsConnection.send(bytes, byteOffset, byteStringLength);
		irodsConnection.flush();
		return irodsConnection.readMessage();
	}

	/**
	 * Create an iRODS message Tag, including header. Send the bytes of the byte
	 * array, no error stream.
	 */
	// TODO: test
	public synchronized Tag irodsFunction(IRodsPI irodsPI, byte[] errorStream,
			int errorOffset, int errorLength, byte[] bytes, int byteOffset,
			int byteStringLength) throws JargonException {

		if (irodsPI == null) {
			String err = "null irodsPI";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		String out = irodsPI.getParsedTags();

		if (out == null || out.length() == 0) {
			String err = "null or missing message returned from parse";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (log.isDebugEnabled()) {
			log.debug(out);
		}

		try {
			irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, out
					.getBytes(encoding).length, errorLength, byteStringLength,
					irodsPI.getApiNumber()));
			irodsConnection.send(out);

			if (byteStringLength > 0) {
				irodsConnection.send(bytes, byteOffset, byteStringLength);
			}

			irodsConnection.flush();
			return irodsConnection.readMessage();

		} catch (UnsupportedEncodingException e) {
			log.error("unsupported encoding", e);
			throw new JargonException(e);
		} catch (IOException e) {
			log.error("io exception sending irods command", e);
			throw new JargonException(e);
		}

	}

	/**
	 * Create an iRODS message Tag, including header. This convenience method is
	 * suitable for operations that do not require error or binary streams, and
	 * will set up empty streams for the method call.
	 */
	// TODO: test
	public synchronized Tag irodsFunction(IRodsPI irodsPI)
			throws JargonException {

		if (irodsPI == null) {
			String err = "null irodsPI";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		return irodsFunction(RODS_API_REQ, irodsPI.getParsedTags(), irodsPI
				.getApiNumber());
	}

	/**
	 * Create an iRODS message Tag, including header.
	 */
	public synchronized Tag irodsFunction(String type, Tag message,
			int errorLength, InputStream errorStream, long byteStringLength,
			InputStream byteStream, int intInfo) throws IOException {

		if (type == null || type.length() == 0) {
			String err = "null or blank type";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (message == null) {
			String err = "null message";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		String out = message.parseTag();

		if (out == null || out.length() == 0) {
			String err = "null or missing message returned from parse";
			log.error(err);
			throw new IllegalArgumentException(err);
		}

		if (log.isDebugEnabled()) {
			log.debug(out);
		}
		irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, out
				.getBytes(encoding).length, errorLength, byteStringLength,
				intInfo));
		irodsConnection.send(out);
		if (errorLength > 0)
			irodsConnection.send(errorStream, errorLength);
		if (byteStringLength > 0)
			irodsConnection.send(byteStream, byteStringLength);
		irodsConnection.flush();
		return irodsConnection.readMessage();
	}

	/*
	 * Functions which call irodsFunction(...) then send or recieve more bytes,
	 * such as get, put, fileRead, or really any time the message header
	 * bytesLength > 0 Must be synchronized for to be thread safe.
	 */

	/**
	 * Get misc server info about the connected iRODS server.
	 * 
	 * @return <code>String</code> with various server information
	 * @throws java.io.IOException
	 */
	public String miscServerInfo() throws IOException {
		irodsConnection.send(irodsConnection.createHeader(RODS_API_REQ, 0, 0,
				0, GET_MISC_SVR_INFO_AN));
		irodsConnection.flush();
		Tag message = irodsConnection.readMessage();
		return message.parseTag();
		/*
		 * <MiscSvrInfo_PI> <serverType>1</serverType>
		 * <serverBootTime>1225230863</serverBootTime>
		 * <relVersion>rods1.1</relVersion> <apiVersion>d</apiVersion>
		 * <rodsZone>tempZone</rodsZone> </MiscSvrInfo_PI> I RCAT_ENABLED
		 * relVersion=rods1.1 apiVersion=d rodsZone=tempZone up 3 days, 1:26
		 */
	}

	void chmod(IRODSFile file, String permission, String user, String zoneName,
			boolean recursive) throws IOException {
		Tag message = new Tag(modAccessControlInp_PI,
				new Tag[] { new Tag(recursiveFlag, recursive ? 1 : 0),
						new Tag(accessLevel, permission),
						new Tag(userName, user), new Tag(zone, zoneName),
						new Tag(path, file.getAbsolutePath()), });

		irodsFunction(RODS_API_REQ, message, MOD_ACCESS_CONTROL_AN);
	}

	/**
	 * Copy the source file to the destination file.
	 * 
	 * @param source
	 * @param destination
	 * @param overwriteFlag
	 * @throws IOException
	 */
	void copy(IRODSFile source, IRODSFile destination, boolean overwriteFlag)
			throws IOException {
		String[][] keyword = new String[2][2];
		String resource = destination.getResource();
		if (overwriteFlag) {
			keyword[0] = new String[] { IRODSMetaDataSet.FORCE_FLAG_KW, "" };
		}
		if (resource != null && !resource.equals("")) {
			keyword[1] = new String[] { IRODSMetaDataSet.DEST_RESC_NAME_KW,
					resource };
		}

		Tag message = new Tag(DataObjCopyInp_PI, new Tag[] {
		// define the source
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, source.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, source.length()),
						new Tag(numThreads, 0), new Tag(oprType, COPY_SRC),
						Tag.createKeyValueTag(null), }),
				// define the destination
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, destination.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, 0),
						new Tag(numThreads, 0), new Tag(oprType, COPY_DEST),
						Tag.createKeyValueTag(keyword), }), });

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_COPY_AN);
	}

	/**
	 * Delete the given collection from IRODS.
	 * 
	 * @param file
	 *            {@link IRODSFile IRODSFile} that is a collection to be deleted
	 * @param force
	 *            <code>boolean</code> indicates Immediate removal of
	 *            data-objects without putting them in trash
	 * @throws IOException
	 */
	void deleteDirectory(IRODSFile file, boolean force) throws IOException {
		String[][] keyword = null;
		if (force) {
			keyword = new String[][] {
					new String[] { IRODSMetaDataSet.FORCE_FLAG_KW, "" },
					new String[] { IRODSMetaDataSet.RECURSIVE_OPR__KW, "" }, };
		} else {
			keyword = new String[][] { new String[] {
					IRODSMetaDataSet.RECURSIVE_OPR__KW, "" }, };
		}
		Tag message = new Tag(CollInp_PI, new Tag[] {
				new Tag(collName, file.getAbsolutePath()),
				Tag.createKeyValueTag(keyword), });

		if (log.isDebugEnabled()) {
			log.debug("delete directory with pi of:" + message.parseTag());
		}

		Tag reply = irodsFunction(RODS_API_REQ, message, RM_COLL_AN);

		// may be a status reply if more than n files exist in the collection,
		// these need to be responded to

		processClientStatusMessages(reply);

	}

	/**
	 * Respond to client status messages for an operation until exhausted.
	 * 
	 * @param reply
	 *            <code>Tag</code> containing status messages from IRODS
	 * @throws IOException
	 */
	private void processClientStatusMessages(Tag reply) throws IOException {

		boolean done = false;
		Tag ackResult = reply;

		while (!done) {
			if (ackResult.getLength() > 0) {
				if (ackResult.tagName.equals(CollOprStat_PI)) {
					// formulate an answer status reply

					// if the total file count is 0, then I will continue and
					// send
					// the coll stat reply, otherwise, just ignore and
					// don't send the reply.

					Tag totalFilesTag = ackResult.getTag("totalFileCnt");
					int totalFiles = Integer.parseInt((String) totalFilesTag
							.getValue());
					Tag fileCountTag = ackResult.getTag("filesCnt");
					int fileCount = Integer.parseInt((String) fileCountTag
							.getValue());

					if (fileCount < SYS_CLI_TO_SVR_COLL_STAT_SIZE) {
						done = true;
					} else {

						irodsConnection
								.sendInNetworkOrder(SYS_CLI_TO_SVR_COLL_STAT_REPLY);
						irodsConnection.flush();
						ackResult = irodsConnection.readMessage();
					}
				}
			}
		}

	}

	void deleteFile(IRODSFile file, boolean force) throws IOException {
		String[][] keyword = null;
		if (force) {
			keyword = new String[][] { new String[] {
					IRODSMetaDataSet.FORCE_FLAG_KW, "" } };
		}
		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, file.getAbsolutePath()),
				new Tag(createMode, 0), new Tag(openFlags, 0),
				new Tag(offset, 0), new Tag(dataSize, 0),
				new Tag(numThreads, 0), new Tag(oprType, 0),
				Tag.createKeyValueTag(keyword), });

		if (log.isDebugEnabled()) {
			log.debug("delete file with pi of:" + message.parseTag());
		}

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_UNLINK_AN);
	}

	// POSIX commands
	int fileCreate(IRODSFile file, boolean read, boolean write)
			throws IOException {
		int rw = 0;
		if (read && write)
			rw = 2;
		else if (write)
			rw = 1;

		String resource = file.getResource();
		String[][] keyword = {
				{ IRODSMetaDataSet.DATA_TYPE_KW, file.getDataType() }, null };
		if (resource != null && !resource.equals("")) {
			keyword[1] = new String[] { IRODSMetaDataSet.DEST_RESC_NAME_KW,
					resource };
		}
		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, file.getAbsolutePath()),
				new Tag(createMode, 488), // octal for 750 owner has rwx, group?
				// has r+x
				new Tag(openFlags, rw), new Tag(offset, 0),
				new Tag(dataSize, -1), new Tag(numThreads, 0),
				new Tag(oprType, 0), Tag.createKeyValueTag(keyword), });

		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_CREATE_AN);
		if (message != null)
			return message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue();

		return -1;
	}

	void fileClose(int fd) throws IOException {
		Tag message = new Tag(dataObjCloseInp_PI, new Tag[] {
				new Tag(l1descInx, fd), new Tag(bytesWritten, 0), });

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_CLOSE_AN);
	}

	int fileOpen(IRODSFile file, boolean read, boolean write)
			throws IOException {
		int rw = 0;
		if (read && write)
			rw = 2;
		else if (write)
			rw = 1;

		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, file.getAbsolutePath()),
				new Tag(createMode, 0), // can ignore on open
				new Tag(openFlags, rw), new Tag(offset, 0),
				new Tag(dataSize, 0), new Tag(numThreads, 0),
				new Tag(oprType, 0), Tag.createKeyValueTag(null), });

		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_OPEN_AN);
		if (message != null)
			return message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue();

		return -1;
	}

	/**
	 * Read a file to the given stream.
	 */
	synchronized int fileRead(int fd, OutputStream destination, long length)
			throws IOException {

		// shim code for Bug 40 - IRODSCommands.fileRead() with length of 0
		// causes null message from irods
		if (length == 0) {
			length = 1;
		}

		if (fd == 0 || destination == null) {
			throw new IllegalArgumentException(
					"invalid parameters for fileRead");
		}

		// length param is unused
		Tag message = new Tag(dataObjReadInp_PI, new Tag[] {
				new Tag(l1descInx, fd), new Tag(len, length), });

		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_READ_AN);
		// Need the total dataSize
		length = message.getTag(MsgHeader_PI).getTag(bsLen).getIntValue();

		// read the message byte stream into the local file
		irodsConnection.read(destination, length);
		return message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue();
	}

	/**
	 * Read a file into the given byte array.
	 */
	synchronized int fileRead(int fd, byte buffer[], int offset, int length)
			throws IOException {

		Tag message = new Tag(dataObjReadInp_PI, new Tag[] {
				new Tag(l1descInx, fd), new Tag(len, length), });

		// FIXME: bug here? length is always max
		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_READ_AN);
		// Need the total dataSize
		if (message == null)
			return -1;

		length = message.getTag(MsgHeader_PI).getTag(bsLen).getIntValue();

		// read the message byte stream into the local file

		int read = irodsConnection.read(buffer, offset, length);

		if (read == message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue()) {
			return read;
		} else {
			throw new ProtocolException("Bytes read mismatch");
		}
	}

	/**
	 * Set the file position for the IRODS file to the specified position
	 * 
	 * @param fd
	 *            <code>int</code> with the file descriptor created by the
	 *            {@link #fileOpen(IRODSFile, boolean, boolean) fileOpen} method
	 * @param seek
	 *            <code>long</code> that is the offset value
	 * @param whence
	 *            <code>int</code> that specifies the postion to compute the
	 *            offset from
	 * @return <code>long</code with the new offset.
	 * @throws IOException
	 */
	long fileSeek(int fd, long seek, int whence) throws IOException {

		Tag message;
		try {
			OpenedDataObjInp openedDataObjInp = OpenedDataObjInp.instanceForFileSeek(seek, fd, whence);
			message = irodsFunction(openedDataObjInp);
		} catch (JargonException e) {
			log.error("JargonException in file seek, will be rethrown to current contract IOException", e);
			throw new IOException(e);
		}
		return message.getTag(offset).getLongValue();
	}

	/**
	 * Write a file into the given InputStream.
	 */
	int fileWrite(int fd, InputStream source, long length) throws IOException {
		Tag message = new Tag(dataObjWriteInp_PI, new Tag[] {
				new Tag(dataObjInx, fd), new Tag(len, length), });

		message = irodsFunction(RODS_API_REQ, message, 0, null, length, source,
				DATA_OBJ_WRITE_AN);
		return message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue();
	}

	/**
	 * Write a file into the given byte array.
	 */
	int fileWrite(int fd, byte buffer[], int offset, int length)
			throws IOException {
		Tag message = new Tag(dataObjWriteInp_PI, new Tag[] {
				new Tag(dataObjInx, fd), new Tag(len, length), });

		message = irodsFunction(RODS_API_REQ, message, null, 0, 0, buffer,
				offset, length, DATA_OBJ_WRITE_AN);
		return message.getTag(MsgHeader_PI).getTag(intInfo).getIntValue();
	}

	/**
	 * Get a file from IRODS and store it in the given destination. NOTE: this
	 * does not seem to work for collections, and is not recursive, this should
	 * be addressed in later releases
	 * 
	 * @param source
	 * @param destination
	 * @param resource
	 * @throws IOException
	 */
	synchronized void get(IRODSFile source, GeneralFile destination,
			String resource) throws IOException {

		/*
		 * #define DataObjInp_PI "str objPath[MAX_NAME_LEN]; int createMode; int
		 * openFlags; double offset; double dataSize; int numThreads; int
		 * oprType; struct *SpecColl_PI; struct KeyValPair_PI;"
		 */

		if (log.isInfoEnabled()) {
			log.info("get of source:" + source.getAbsolutePath()
					+ " into dest:" + destination.getAbsolutePath()
					+ " with resource:" + resource);
		}

		Tag rescKeyValueTag;

		if (resource == null || resource.length() == 0) {
			rescKeyValueTag = Tag.createKeyValueTag(null);
		} else {
			String[][] kvArray = { { IRODSMetaDataSet.RESC_NAME_KW, resource } };
			rescKeyValueTag = Tag.createKeyValueTag(kvArray);
		}

		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, source.getAbsolutePath()),
				new Tag(createMode, 0), new Tag(openFlags, 0),
				new Tag(offset, 0), new Tag(dataSize, 0),
				new Tag(numThreads, 0), new Tag(oprType, GET_OPR),
				rescKeyValueTag, });

		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_GET_AN);

		// irods file doesn't exist
		if (message == null) {
			log
					.warn("irods file does not exist, null was returned from the get, return with no update done");
			return;
		}

		// Need the total dataSize
		Tag temp = message.getTag(MsgHeader_PI);
		if (temp == null) {
			// length is zero
			log.info("create a new file, length is zero");
			destination.createNewFile();
			return;
		}
		temp = temp.getTag(bsLen);
		if (temp == null) {
			log.info("no size returned, return from put with no update done");
			return;
		}
		long length = temp.getIntValue();

		log.info("transfer length is:: {}", length);

		// if length == zero, check for multiple thread copy
		if (length == 0) {
			int threads = message.getTag(numThreads).getIntValue();
			log.info("number of threads for this transfer = {} ", threads);
			if (threads > 0) {
				log.info("parallel transfer for this get");

				String host = message.getTag(PortList_PI).getTag(hostAddr)
						.getStringValue();
				int port = message.getTag(PortList_PI).getTag(portNum)
						.getIntValue();
				int pass = message.getTag(PortList_PI).getTag(cookie)
						.getIntValue();

				Thread[] transferThreads = new Thread[threads];
				TransferThread[] transfer = new TransferThread[threads];
				for (int i = 0; i < threads; i++) {
					transfer[i] = new TransferThread(host, port, pass,
							FileFactory.newRandomAccessFile(destination, "rw"));
					transferThreads[i] = new Thread(transfer[i]);
					if (log.isInfoEnabled()) {
						log.info("created a transfer thread number:" + i
								+ " with thread name:"
								+ transferThreads[i].getName());
					}
				}
				for (int i = 0; i < threads; i++) {
					if (log.isDebugEnabled()) {
						log.debug("started thread #" + i);
					}
					transferThreads[i].start();
				}

				try {
					for (int i = 0; i < threads; i++) {
						if (transferThreads[i].isAlive())
							transferThreads[i].join();
					}
				} catch (InterruptedException e) {
					if (log.isWarnEnabled()) {
						log
								.warn("interrupted exception, this is logged and ignored");
						e.printStackTrace();
					}
				}
				log.info("closing threads");
				for (int i = 0; i < threads; i++) {
					transfer[i].close();
				}
				log.info("parallel transfer complete");
			}
		} else {
			log.info("normal file transfer started");
			// read the message byte stream into the local file
			irodsConnection.read(FileFactory.newRandomAccessFile(destination,
					"rw"), length);
			log.info("transfer is complete");
		}

	}

	synchronized void get(IRODSFile source, GeneralFile destination)
			throws IOException {

		get(source, destination, "");

	}

	/**
   *
   */
	void mkdir(IRODSFile irodsFile, boolean recursiveOperation)
			throws IOException {
		if (irodsFile == null) {
			log.error("directory path cannot be null");
			throw new NullPointerException("Directory path cannot be null");
		}
		if (log.isInfoEnabled()) {
			log.info("making dir for:" + irodsFile.getAbsolutePath());
		}

		try {
			CollInp collInp = CollInp.instance(irodsFile.getAbsolutePath(),
					recursiveOperation);

			Tag response = irodsFunction(CollInp.PI_TAG, collInp
					.getParsedTags(), CollInp.MKDIR_API_NBR);

			if (response != null) {
				log
						.warn("expected null response to mkdir, logged but not an error, received:"
								+ response.parseTag());
			}
		} catch (JargonException e) {
			log.error("Jargon exception in mkdir operation", e);
			throw new IOException(e);
		}
	}

	void put(GeneralFile source, IRODSFile destination, boolean overwriteFlag)
			throws IOException {

		String resource = destination.getResource();

		long length = source.length();

		if (length > TRANSFER_THREAD_SIZE) {
			if (log.isInfoEnabled()) {
				log.info("put operation will use parallel transfer, size:"
						+ length
						+ " is greater that the TRANSFER_THREAD_SIZE setting");
			}
			String[][] keyword = {
					{ IRODSMetaDataSet.DATA_TYPE_KW, destination.getDataType() },
					{ null }, { null } };
			if (overwriteFlag) {
				keyword[1] = new String[] { IRODSMetaDataSet.FORCE_FLAG_KW, "" };
			}
			if (resource != null && !resource.equals("")) {
				keyword[2] = new String[] { IRODSMetaDataSet.DEST_RESC_NAME_KW,
						resource };
			}
			Tag message = new Tag(DataObjInp_PI,
					new Tag[] {
							new Tag(objPath, destination.getAbsolutePath()),
							new Tag(createMode, 448), // octal for 700 owner has
							// rw
							new Tag(openFlags, 1), new Tag(offset, 0),
							new Tag(dataSize, length), new Tag(numThreads, 0),
							new Tag(oprType, PUT_OPR),
							Tag.createKeyValueTag(keyword), });

			message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_PUT_AN);

			if (message == null) {
				log
						.warn("send of put returned null, currently is ignored and null is returned from put operation");
				return;
			}

			int threads = message.getTag(numThreads).getIntValue();
			if (log.isInfoEnabled()) {
				log.info("tranfer will be done using " + threads + " threads");
			}
			if (threads > 0) {
				InputStream[] inputs = new InputStream[threads];
				for (int i = 0; i < threads; i++) {
					inputs[i] = FileFactory.newFileInputStream(source);
				}

				synchronized (this) {
					String host = message.getTag(PortList_PI).getTag(hostAddr)
							.getStringValue();
					int port = message.getTag(PortList_PI).getTag(portNum)
							.getIntValue();
					int pass = message.getTag(PortList_PI).getTag(cookie)
							.getIntValue();
					long transferLength = length / threads;

					Thread[] transferThreads = new Thread[threads];
					TransferThread[] transfer = new TransferThread[threads];
					for (int i = 0; i < threads - 1; i++) {
						transfer[i] = new TransferThread(host, port, pass, // connection
								// info
								inputs[i], // sourceFile
								transferLength * i, // offset
								transferLength // length
						);
						transferThreads[i] = new Thread(transfer[i]);
						if (log.isInfoEnabled()) {
							log.info("creating transfer thread number:" + i
									+ "with thread name:"
									+ transferThreads[i].getName());
						}

					}
					// last thread is a little different
					transfer[threads - 1] = new TransferThread(host, port,
							pass, // connection info
							inputs[threads - 1], // sourceFile
							transferLength * (threads - 1), // offset
							(int) (length - transferLength * (threads - 1)) // length
					);
					transferThreads[threads - 1] = new Thread(
							transfer[threads - 1]);
					if (log.isInfoEnabled()) {
						log.info("creating final tranfer thread as:"
								+ transferThreads[threads - 1].getName());
					}

					for (int i = 0; i < threads; i++) {
						transferThreads[i].start();
					}

					try {
						for (int i = 0; i < threads; i++) {
							if (transferThreads[i].isAlive())
								transferThreads[i].join();
						}
					} catch (InterruptedException e) {
						if (log.isWarnEnabled()) {
							log
									.warn("interrupted exception, this is logged and ignored");
							e.printStackTrace();
						}
					}
					log.info("closing threads");
					for (int i = 0; i < threads; i++) {
						if (transferThreads[i].isAlive())
							transfer[i].close();
					}

					log.info("transfer is complete");

					// return complete( file descriptor )
					operationComplete(message.getTag(l1descInx).getIntValue());
				}
			}
		} else {
			log.info("transfer done without parallel mode");
			String[][] keyword = {
					{ IRODSMetaDataSet.DATA_TYPE_KW, destination.getDataType() },
					{ IRODSMetaDataSet.DATA_INCLUDED_KW, "" }, { null },
					{ null } };
			if (overwriteFlag) {
				keyword[2] = new String[] { IRODSMetaDataSet.FORCE_FLAG_KW, "" };
			}
			if (resource != null && !resource.equals("")) {
				keyword[3] = new String[] { IRODSMetaDataSet.DEST_RESC_NAME_KW,
						resource };
			}
			Tag message = new Tag(DataObjInp_PI,
					new Tag[] {
							new Tag(objPath, destination.getAbsolutePath()),
							new Tag(createMode, 448), // octal for 700 owner has
							// rw
							new Tag(openFlags, 1), new Tag(offset, 0),
							new Tag(dataSize, length), new Tag(numThreads, 0),
							new Tag(oprType, PUT_OPR),
							Tag.createKeyValueTag(keyword), });
			// send the message, no result expected.
			// exception thrown on error.
			irodsFunction(RODS_API_REQ, message, 0, null, length, FileFactory
					.newFileInputStream(source), DATA_OBJ_PUT_AN);
			log.info("transfer complete");
		}
	}

	/**
	 * Add or update an AVU value for a data object or collection
	 * 
	 * @param file
	 *            {@line edu.sdsc.grid.io.irods.IRODSFile IRODSFile} describing
	 *            the object or collection
	 * @param values
	 *            <code>String[]</code> containing an AVU in the form (attrib
	 *            name, attrib value) or (attrib name, attrib value, attrib
	 *            units)
	 * @throws IOException
	 */
	void modifyMetaData(IRODSFile file, String[] values) throws IOException {

		if (file == null) {
			throw new IllegalArgumentException("irods file must not be null");
		}

		if (values.length < 2 || values.length > 3) {
			log
					.error("metadata length must be 2 (name and value) or 3 (name, value, units) ");
			throw new IllegalArgumentException(
					"metadata length must be 2 (name and value) or 3 (name, value, units) ");
		}

		Tag message = new Tag(ModAVUMetadataInp_PI, new Tag[] { new Tag("arg0",
				"add"), });
		if (file.isDirectory()) {
			message.addTag("arg1", "-c");
		} else {
			message.addTag("arg1", "-d");
		}

		message.addTag("arg2", file.getAbsolutePath());

		for (int i = 0, j = 0; i < 7; i++) {
			j = i + 3;
			if (i < values.length) {
				message.addTag("arg" + j, values[i]);
			} else {
				message.addTag("arg" + j, "");
			}
		}

		irodsFunction(RODS_API_REQ, message, MOD_AVU_METADATA_AN);
	}

	void deleteMetaData(IRODSFile file, String[] values) throws IOException {
		Tag message = new Tag(ModAVUMetadataInp_PI, new Tag[] { new Tag("arg0",
				"rmw"), });
		if (file.isDirectory()) {
			message.addTag("arg1", "-c");
		} else {
			message.addTag("arg1", "-d");
		}

		message.addTag("arg2", file.getAbsolutePath());

		for (int i = 0, j = 0; i < 7; i++) {
			j = i + 3;
			if (i < values.length) {
				message.addTag("arg" + j, values[i]);
			} else {
				message.addTag("arg" + j, "");
			}
		}

		irodsFunction(RODS_API_REQ, message, MOD_AVU_METADATA_AN);
	}

	/**
	 * Add or update an AVU value for a resource
	 * 
	 * @param resourceName
	 *            <code>String</code> with the name of the resource
	 * @param values
	 *            <code>String[]</code> containing an AVU in the form (attrib
	 *            name, attrib value) or (attrib name, attrib value, attrib
	 *            units)
	 * @throws IOException
	 */
	void modifyResourceMetaData(String resourceName, String[] values)
			throws IOException {

		if (resourceName == null || resourceName.length() == 0) {
			throw new IllegalArgumentException("resourceName is null or blank");
		}

		if (values.length < 2 || values.length > 3) {
			log
					.error("metadata length must be 2 (name and value) or 3 (name, value, units) ");
			throw new IllegalArgumentException(
					"metadata length must be 2 (name and value) or 3 (name, value, units) ");
		}

		Tag message = new Tag(ModAVUMetadataInp_PI, new Tag[] { new Tag("arg0",
				"add"), });
		message.addTag("arg1", "-R");

		message.addTag("arg2", resourceName);

		for (int i = 0, j = 0; i < 7; i++) {
			j = i + 3;
			if (i < values.length) {
				message.addTag("arg" + j, values[i]);
			} else {
				message.addTag("arg" + j, "");
			}
		}

		irodsFunction(RODS_API_REQ, message, MOD_AVU_METADATA_AN);
	}

	/**
	 * Delete AVU metadata for the given resource
	 * 
	 * @param resourceName
	 *            <code>String</code> with the name of the target resource
	 * @param values
	 *            <code>String[]</code> with the AVU triple to delete
	 * @throws IOException
	 */
	void deleteResourceMetaData(String resourceName, String[] values)
			throws IOException {
		Tag message = new Tag(ModAVUMetadataInp_PI, new Tag[] { new Tag("arg0",
				"rmw"), });

		message.addTag("arg1", "-R");

		message.addTag("arg2", resourceName);

		for (int i = 0, j = 0; i < 7; i++) {
			j = i + 3;
			if (i < values.length) {
				message.addTag("arg" + j, values[i]);
			} else {
				message.addTag("arg" + j, "");
			}
		}

		irodsFunction(RODS_API_REQ, message, MOD_AVU_METADATA_AN);
	}

	void renameFile(IRODSFile source, IRODSFile destination) throws IOException {
		Tag message = new Tag(DataObjCopyInp_PI, new Tag[] {
		// define the source
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, source.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, 0),
						new Tag(numThreads, 0),
						new Tag(oprType, RENAME_DATA_OBJ),
						Tag.createKeyValueTag(null), }),
				// define the destination
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, destination.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, 0),
						new Tag(numThreads, 0),
						new Tag(oprType, RENAME_DATA_OBJ),
						Tag.createKeyValueTag(null), }), });

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_RENAME_AN);
	}

	void renameDirectory(IRODSFile source, IRODSFile destination)
			throws IOException {
		Tag message = new Tag(DataObjCopyInp_PI, new Tag[] {
		// define the source
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, source.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, 0),
						new Tag(numThreads, 0), new Tag(oprType, RENAME_COLL),
						Tag.createKeyValueTag(null), }),
				// define the destination
				new Tag(DataObjInp_PI, new Tag[] {
						new Tag(objPath, destination.getAbsolutePath()),
						new Tag(createMode, 0), new Tag(openFlags, 0),
						new Tag(offset, 0), new Tag(dataSize, 0),
						new Tag(numThreads, 0), new Tag(oprType, RENAME_COLL),
						Tag.createKeyValueTag(null), }), });

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_RENAME_AN);
	}

	void physicalMove(IRODSFile source, IRODSFile destination)
			throws IOException {
		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, source.getAbsolutePath()),
				new Tag(createMode, 0),
				new Tag(openFlags, 0),
				new Tag(offset, 0),
				new Tag(dataSize, 0),
				new Tag(numThreads, 0),
				new Tag(oprType, PHYMV_OPR),
				Tag.createKeyValueTag(IRODSMetaDataSet.DEST_RESC_NAME_KW,
						destination.getResource()) });

		Tag response = irodsFunction(RODS_API_REQ, message, DATA_OBJ_PHYMV_AN);
	}

	/**
	 * Replicate the file to the given resource
	 * 
	 * @param file
	 *            <code>IRODSFile<code> to be replicated.
	 * @param newResource
	 *            <code>String</code> with the name of the new resource
	 * @throws IOException
	 */
	void replicate(IRODSFile file, String newResource) throws IOException {
		try {
			DataObjInp dataObjInp = DataObjInp.instanceForReplicate(file
					.getAbsolutePath(), newResource);
			irodsFunction(dataObjInp);
		} catch (JargonException e) {
			log.error(
					"JargonException in replication, rethrown as IOException",
					e);
			throw new IOException(e);
		}
	}

	void deleteReplica(IRODSFile file, String resource) throws IOException {
		// NOTE: add num copies option and fix test in IRODSFileCommandsTest
		Tag message = new Tag(DataObjInp_PI,
				new Tag[] {
						new Tag(objPath, file.getAbsolutePath()),
						new Tag(createMode, 0),
						new Tag(openFlags, 0),
						new Tag(offset, 0),
						new Tag(dataSize, 0),
						new Tag(numThreads, 0),
						new Tag(oprType, 0),
						Tag.createKeyValueTag(IRODSMetaDataSet.RESC_NAME_KW,
								resource), });

		irodsFunction(RODS_API_REQ, message, DATA_OBJ_TRIM_AN);
	}

	String[] stat(IRODSFile file) throws IOException {
		String[] data;
		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, file.getAbsolutePath()),
				new Tag(createMode, 0), new Tag(openFlags, 0),
				new Tag(offset, 0), new Tag(dataSize, 0),
				new Tag(numThreads, 0), new Tag(oprType, 0),
				Tag.createKeyValueTag(null), });

		irodsFunction(RODS_API_REQ, message, OBJ_STAT_AN);

		/*
		 * <RodsObjStat_PI> <objSize>0</objSize> <objType>2</objType>
		 * <numCopies>0</numCopies> <dataId>10548</dataId> <chksum></chksum>
		 * <ownerName>rods</ownerName> <ownerZone>tempZone</ownerZone>
		 * <createTime>1207730866</createTime>
		 * <modifyTime>1207730866</modifyTime> <SpecColl_PI> <class>2</class>
		 * <type>0</type> <collection>/tempZone/home/rods/lee</collection>
		 * <objPath></objPath> <resource>demoResc</resource>
		 * <phyPath>/tmp/lee</phyPath> <cacheDir></cacheDir>
		 * <cacheDirty>0</cacheDirty> <replNum>0</replNum> </SpecColl_PI>
		 * </RodsObjStat_PI>
		 */
		data = null;

		return data;
	}

	/**
	 * Take an existing IRODS collection and create a tar file in irods from the
	 * objects in the collection
	 * 
	 * @param tarFile
	 *            {@link edu.sdsc.grid.io.irods.IRODSFile IRODSFile} that will
	 *            be the destination <code>.tar</code> file
	 * @param directory
	 *            {@link edu.sdsc.grid.io.irods.IRODSFile IRODSFile} that is the
	 *            collection to be tar'd
	 * @param resource
	 *            <code>String</code> with the resource for the
	 *            <code>.tar</code> file.
	 * @throws IOException
	 */
	void createBundle(IRODSFile tarFile, IRODSFile directory, String resource)
			throws IOException {

		if (tarFile == null || directory == null || resource == null
				|| resource.length() == 0) {
			throw new IllegalArgumentException(
					"Null values not allowed for parameters");
		} else if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
					"Directory must refer to an IRODS Collection");
		}

		String[][] keyword = new String[1][2];
		keyword[0] = new String[] { IRODSMetaDataSet.DEST_RESC_NAME_KW,
				resource };

		Tag message = new Tag(StructFileExtAndRegInp_PI, new Tag[] {
				new Tag(objPath, tarFile.getAbsolutePath()),
				new Tag(collection, directory.getAbsolutePath()),
				new Tag(oprType, 0), new Tag(flags, 0),
				Tag.createKeyValueTag(keyword) });

		irodsFunction(RODS_API_REQ, message, STRUCT_FILE_BUNDLE_AN);
	}

	void extractBundle(IRODSFile tarFile, IRODSFile directory)
			throws IOException {
		Tag message = new Tag(StructFileExtAndRegInp_PI, new Tag[] {
				new Tag(objPath, tarFile.getAbsolutePath()),
				new Tag(collection, directory.getAbsolutePath()),
				new Tag(oprType, 0), new Tag(flags, 0),
				Tag.createKeyValueTag(null) });

		irodsFunction(RODS_API_REQ, message, STRUCT_FILE_BUNDLE_AN);
	}

	synchronized InputStream executeCommand(String command, String args,
			String hostAddress) throws IOException {
		Tag message = new Tag(ExecCmd_PI, new Tag[] { new Tag(cmd, command),
				new Tag(cmdArgv, args), new Tag(execAddr, hostAddress),
				new Tag(hintPath, ""), new Tag(addPathToArgv, 0),
				Tag.createKeyValueTag(null) });
		String buffer = "";

		try {
			message = irodsFunction(RODS_API_REQ, message, EXEC_CMD_AN);
		} catch (NullPointerException e) {
			NullPointerException x = new NullPointerException(
					"Can occur if the command output is too long");
			x.initCause(e);
			throw x;
		}
		if (message != null) {
			// message
			int length = message.getTag(BinBytesBuf_PI, 0).getTag(buflen)
					.getIntValue();
			if (length > 0) {
				buffer += message.getTag(BinBytesBuf_PI, 0).getTag(buf)
						.getStringValue();
			}

			// error
			length = message.getTag(BinBytesBuf_PI, 1).getTag(buflen)
					.getIntValue();
			if (length > 0) {
				buffer += message.getTag(BinBytesBuf_PI, 1).getTag(buf)
						.getStringValue();
			}
		}

		return new java.io.ByteArrayInputStream(Base64.fromString(buffer));
	}

	String checksum(IRODSFile file) throws IOException {
		Tag message = new Tag(DataObjInp_PI, new Tag[] {
				new Tag(objPath, file.getAbsolutePath()),
				new Tag(createMode, 0), new Tag(openFlags, 0),
				new Tag(offset, 0), new Tag(dataSize, 0),
				new Tag(numThreads, 0), new Tag(oprType, 0),
				Tag.createKeyValueTag(null) });

		message = irodsFunction(RODS_API_REQ, message, DATA_OBJ_CHKSUM_AN);
		if (message != null)
			return message.getTag(Rule.myStr).getStringValue();

		return null;
	}

	/**
	 * Execute an IRODS rule and return the result as a <code>Tag</code>. Note
	 * that the result in <code>Tag</code> format can be processed by
	 * {@link edu.sdsc.grid.io.irods.Rule#readResult(IRODSFileSystem, Tag)
	 * edu.sdsc.grio.io.irods.Rule.readResult(IRODSFileSystem, Tag)}
	 * 
	 * Note that this method currently can return null. This behavior will be
	 * corrected in upcoming versions of Jargon. The Rule.readResult() method
	 * was updated to return an empty Parameter[] and to tolerate a null input
	 * to ensure that NullPointerExceptions do not occur. These are interim
	 * fixes...this entire arrangement will be reconsidered.
	 * 
	 * @param rule
	 *            <code>String</code> with the text of the rule to be executed
	 * @param input
	 *            {@link edu.sdsc.grid.io.irods.Parameter Parameter[]} for
	 *            inputs to the rule
	 * @param output
	 *            {@link edu.sdsc.grid.io.irods.Parameter Parameter[]}
	 *            containing rule output
	 * @return {@link edu.sdsc.grid.io.irods.Tag Tag} containing the response
	 *         from IRODS for the rule invocation.
	 * @throws IOException
	 */
	Tag executeRule(String rule, Parameter[] input, Parameter[] output)
			throws IOException {
		// create the rule tag
		Tag message = new Tag(ExecMyRuleInp_PI,
				new Tag[] {
						new Tag(myRule, rule),
						new Tag(RHostAddr_PI, new Tag[] {
								new Tag(hostAddr, ""), new Tag(rodsZone, ""),
								new Tag(port, 0), new Tag(dummyInt, 0), }),
						Tag.createKeyValueTag(null), });

		// add output parameter tags
		// They get cat together seperated by '%'
		if (output != null) {
			String temp = "";
			for (Parameter out : output)
				temp += out.getUniqueName() + "%";
			// should this % be here?

			message.addTag(new Tag(outParamDesc, temp.substring(0, temp
					.length() - 1)));
		}

		// add input parameter tags
		if (input != null) {
			Tag paramArray = new Tag(MsParamArray_PI, new Tag[] {
					new Tag(paramLen, input.length), new Tag(oprType, 0) });
			for (Parameter in : input) {
				paramArray.addTag(in.createMsParamArray());
			}
			message.addTag(paramArray);
		}

		// send rule tag
		message = irodsFunction(RODS_API_REQ, message, EXEC_MY_RULE_AN);

		if (message == null || message.getTag(paramLen).getIntValue() <= 0) {
			return null;
		}
		return message;
	}

	/**
	 *send when certain rules are finished?
	 */
	void operationComplete(int status) throws IOException {
		Tag message = new Tag(Rule.INT_PI, new Tag[] { new Tag(Rule.myInt,
				status), });
		irodsFunction(RODS_API_REQ, message, OPR_COMPLETE_AN);
	}

	// Admin methods

	/**
	 * General iRODS Admin commands. See also iadmin
	 */
	Tag admin(String[] args) throws IOException {

		if (args == null || args.length <= 0) {
			throw new IllegalArgumentException(
					"no arguments passed to the admin command");
		} else if (args.length != 10) {
			String[] temp = new String[10];
			System.arraycopy(args, 0, temp, 0, args.length);
			args = temp;
		}

		Tag message = new Tag(generalAdminInp_PI, new Tag[] {
				new Tag(arg0, args[0] != null ? args[0] : ""),
				new Tag(arg1, args[1] != null ? args[1] : ""),
				new Tag(arg2, args[2] != null ? args[2] : ""),
				new Tag(arg3, args[3] != null ? args[3] : ""),
				new Tag(arg4, args[4] != null ? args[4] : ""),
				new Tag(arg5, args[5] != null ? args[5] : ""),
				new Tag(arg6, args[6] != null ? args[6] : ""),
				new Tag(arg7, args[7] != null ? args[7] : ""),
				new Tag(arg8, args[8] != null ? args[8] : ""),
				new Tag(arg9, args[9] != null ? args[9] : ""), });

		Tag messageResult = irodsFunction(RODS_API_REQ, message,
				GENERAL_ADMIN_AN);
		return messageResult;
	}

	/**
	 * Made before the general query was available. Allowed queries:
	 * "select token_name from r_tokn_main where token_namespace = 'token_namespace'"
	 * , "select token_name from r_tokn_main where token_namespace = ?" ,
	 * "select * from r_tokn_main where token_namespace = ? and token_name like ?"
	 * , "select resc_name from r_resc_main",
	 * "select * from r_resc_main where resc_name=?",
	 * "select zone_name from r_zone_main",
	 * "select * from r_zone_main where zone_name=?",
	 * "select user_name from r_user_main where user_type_name='rodsgroup'" ,"select user_name from r_user_main, r_user_group where r_user_group.user_id=r_user_main.user_id and r_user_group.group_user_id=(select user_id from r_user_main where user_name=?)"
	 * , "select * from r_data_main where data_id=?","select data_name, data_id, data_repl_num from r_data_main where coll_id =(select coll_id from r_coll_main where coll_name=?)"
	 * , "select coll_name from r_coll_main where parent_coll_name=?",
	 * "select * from r_user_main where user_name=?",
	 * "select user_name from r_user_main where user_type_name != 'rodsgroup'" ,"select r_resc_group.resc_group_name, r_resc_group.resc_id, resc_name, r_group.create_ts, r_resc_group.modify_ts from r_resc_main, r_resc_group where r_resc_main.resc_id = r_resc_group.resc_id and resc_group_name=?"
	 * , "select distinct resc_group_name from r_resc_group",
	 * "select coll_id from r_coll_main where coll_name = ?" *
	 */
	String[] simpleQuery(String statement, String arg) throws IOException {
		Tag message = null;

		if (arg == null) {
			message = new Tag(simpleQueryInp_PI, new Tag[] {
					new Tag(sql, statement), new Tag(arg1, ""),
					new Tag(arg2, ""), new Tag(arg3, ""), new Tag(arg4, ""),
					new Tag(control, 0), new Tag(form, 1),
					new Tag(maxBufSize, 1024), });
		} else {
			message = new Tag(simpleQueryInp_PI, new Tag[] {
					new Tag(sql, statement), new Tag(arg1, arg),
					new Tag(arg2, ""), new Tag(arg3, ""), new Tag(arg4, ""),
					new Tag(control, 0), new Tag(form, 1),
					new Tag(maxBufSize, 1024), });
		}

		message = irodsFunction(RODS_API_REQ, message, SIMPLE_QUERY_AN);
		if (message == null) {
			return null;
		}
		String output = message.getTag(outBuf).getStringValue();
		return output.split("\n");
	}

	/**
	 * Send a query to iRODS, defaulting to the 'select distinct' option.
	 * 
	 * @param conditions
	 *            {@link edu.sdsc.grid.io.MetaDataCondition MetaDataCondition}
	 *            containing the query conditions
	 * @param selects
	 *            {@link edu.sdsc.grid.io.MetaDataSelect MetaDataSelect}
	 *            containing the fields to query
	 * @param numberOfRecordsWanted
	 *            <code>int</code> containing the number of records to return
	 *            (per request). Note that <code>MetaDataRecordList</code> has
	 *            the facility to re-query for more results
	 * @param namespace
	 *            (@link edu.sdsc.grid.io.Namespace Namespace} that describes
	 *            the particular object type (e.g. Resource, Collection, User)
	 *            being queried
	 * @param distinctQuery
	 *            <code>boolean</code> that will cause the query to eith0er
	 *            select 'distinct' or select all. A <code>true</code> value
	 *            will select distinct.
	 * @return {@link edu.sdsc.grid.io.MetaDataRecordList MetaDataRecordList}
	 *         containing the results, and the ability to requery.
	 * @throws IOException
	 */
	synchronized MetaDataRecordList[] query(MetaDataCondition[] conditions,
			MetaDataSelect[] selects, int numberOfRecordsWanted,
			Namespace namespace) throws IOException {
		return query(conditions, selects, numberOfRecordsWanted, namespace,
				true);
	}

	/**
	 * Send a query to iRODS
	 * 
	 * @param conditions
	 *            {@link edu.sdsc.grid.io.MetaDataCondition MetaDataCondition}
	 *            containing the query conditions
	 * @param selects
	 *            {@link edu.sdsc.grid.io.MetaDataSelect MetaDataSelect}
	 *            containing the fields to query
	 * @param numberOfRecordsWanted
	 *            <code>int</code> containing the number of records to return
	 *            (per request). Note that <code>MetaDataRecordList</code> has
	 *            the facility to re-query for more results
	 * @param namespace
	 *            (@link edu.sdsc.grid.io.Namespace Namespace} that describes
	 *            the particular object type (e.g. Resource, Collection, User)
	 *            being queried
	 * @param distinctQuery
	 *            <code>boolean</code> that will cause the query to either
	 *            select 'distinct' or select all. A <code>true</code> value
	 *            will select distinct.
	 * @return {@link edu.sdsc.grid.io.MetaDataRecordList MetaDataRecordList}
	 *         containing the results, and the ability to requery.
	 * @throws IOException
	 */
	synchronized MetaDataRecordList[] query(MetaDataCondition[] conditions,
			MetaDataSelect[] selects, int numberOfRecordsWanted,
			Namespace namespace, boolean distinctQuery) throws IOException {

		log.debug("getting GenQueryClassicMidLevelService to process query");
		try {
			GenQueryClassicMidLevelService genQueryMidLevelService = GenQueryClassicMidLevelService
					.instance(this);
			log.debug("processing query in mid level service");
			return genQueryMidLevelService.query(conditions, selects,
					numberOfRecordsWanted, namespace, distinctQuery);
		} catch (JargonException e) {
			log.error(
					"jargon exception in query rethrown as runtime exception",
					e);
			throw new JargonRuntimeException(e);
		}
	}

	MetaDataRecordList[] getMoreResults(int continuationIndex,
			int numberOfRecordsWanted) throws IOException {
		Tag message = new Tag(GenQueryInp_PI, new Tag[] {
				new Tag(maxRows, numberOfRecordsWanted),
				new Tag(continueInx, continuationIndex),
				new Tag(partialStartIndex, 0), // not sure
				new Tag(options, 32), // not sure 32?
				Tag.createKeyValueTag(null), });

		int j = 1;
		MetaDataSelect[] selects = new MetaDataSelect[] { MetaDataSet
				.newSelection("file name") };
		Tag[] subTags = new Tag[selects.length * 2 + 1];
		subTags[0] = new Tag(iiLen, selects.length);
		for (int i = 0; i < selects.length; i++) {
			subTags[j] = new Tag(inx, IRODSMetaDataSet.getID(selects[i]
					.getFieldName()));
			j++;
		}
		for (int i = 0; i < selects.length; i++) {
			// New for loop because they have to be in a certain order...
			subTags[j] = new Tag(ivalue, selects[i].getOperation());
			j++;
		}
		message.addTag(new Tag(InxIvalPair_PI, subTags));

		message.addTag(new Tag(InxValPair_PI, new Tag(isLen, 0)));

		message = irodsFunction(RODS_API_REQ, message, GEN_QUERY_AN);

		if (message == null) {
			// query had no results
			return null;
		}

		int rows = message.getTag(rowCnt).getIntValue();
		int attributes = message.getTag(attriCnt).getIntValue();
		int continuation = message.getTag(continueInx).getIntValue();

		String[] results = new String[attributes];
		MetaDataField[] fields = new MetaDataField[attributes];
		MetaDataRecordList[] rl = new MetaDataRecordList[rows];
		for (int i = 0; i < attributes; i++) {

			fields[i] = IRODSMetaDataSet.getField(message.tags[4 + i].getTag(
					attriInx).getStringValue());
		}
		for (int i = 0; i < rows; i++) {
			for (j = 0; j < attributes; j++) {

				results[j] = message.tags[4 + j].tags[2 + i].getStringValue();
			}
			if (continuation > 0) {
				rl[i] = new IRODSMetaDataRecordList(this, fields, results,
						continuation);
			} else {
				// No more results, don't bother with sending the IRODSCommand
				// object
				rl[i] = new IRODSMetaDataRecordList(null, fields, results,
						continuation);
			}
		}
		return rl;
	}

	int incThread = 0;

	class TransferThread implements Runnable {
		// Need to use GeneralRandomAccessFile
		// for a way to skip bytes according to the offset in the header.
		GeneralRandomAccessFile local;

		Socket s;
		InputStream in;
		OutputStream out;

		long transferLength;
		long offset;
		int which;

		/**
		 * Used by client parallel transfer get
		 */
		TransferThread(String host, int port, int cookie,
				GeneralRandomAccessFile destination) throws IOException {
			local = destination;
			s = new Socket(host, port);
			byte[] outputBuffer = new byte[4];
			Host.copyInt(cookie, outputBuffer);
			in = s.getInputStream();
			s.getOutputStream().write(outputBuffer);
			which = incThread;
			incThread++;
			if (log.isInfoEnabled()) {
				log.info("transfer thread details:");
				log.info("    host:" + host);
				log.info("    port:" + port);
				log.info("    destination:"
						+ destination.getFile().getAbsolutePath());
			}
		}

		/**
		 * Used by client parallel transfer put
		 * 
		 * @param host
		 * @param port
		 * @param cookie
		 * @param in
		 *            file to upload
		 * @param offset
		 *            offset into the file
		 * @param length
		 *            number of bytes this thread should transfer
		 * @throws java.io.IOException
		 */
		TransferThread(String host, int port, int cookie, InputStream in,
				long offset, long length) throws IOException {
			this.in = in;
			transferLength = length;
			if (offset > 0)
				in.skip(offset);
			this.offset = offset;
			s = new Socket(host, port);
			out = s.getOutputStream();
			// write the cookie
			byte b[] = new byte[4];
			Host.copyInt(cookie, b);
			out.write(b);
			which = incThread;
			incThread++;
		}

		protected void finalize() throws Throwable {
			if (local != null) {
				local.close();
				local = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}

			super.finalize();
		}

		int readInt() throws IOException {
			byte[] b = new byte[4];
			int read = in.read(b);
			if (read != 4) {
			}
			return Host.castToInt(b);
		}

		long readLong() throws IOException {
			// length comes down the wire as an signed long long in network
			// order
			byte[] b = new byte[8];

			int read = in.read(b);
			if (read != 8) {
				log.error("did not read 8 bytes for long");
				throw new RuntimeException(
						"unable to read all the bytes for an expected long value");
			}

			return Host.castToLong(b);
		}

		public void run() {
			try {
				if (local != null) {
					log.info("transfer is a get operation");
					get();
				} else {
					log.info("transfer is a put operation");
					put();
				}
			} catch (Throwable e) {// IOException e) {
				log.error("io exception in thread", e);
				e.printStackTrace();
				throw new RuntimeException("IOException in thread.", e);
			}
		}

		void close() {
			// garbage collector can be too slow
			if (out != null) {
				try {

					out.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException in thread.", e);
				}
				out = null;
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException in thread.", e);
				}
				in = null;
			}
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw new RuntimeException("IOException in thread.", e);
				}
				s = null;
			}
		}

		void put() throws IOException {
			// Holds all the data for transfer
			byte[] buffer = null;
			int read = 0;

			// begin transfer
			if (transferLength <= 0)
				return;
			else {
				// length has a max of 8mb?
				buffer = new byte[IRODSConnection.OUTPUT_BUFFER_LENGTH];
			}

			while (transferLength > 0) {
				// need Math.min or reads into what the other threads are
				// transferring
				read = in.read(buffer, 0, (int) Math.min(
						IRODSConnection.OUTPUT_BUFFER_LENGTH, transferLength));
				if (read > 0) {
					transferLength -= read;
					out.write(buffer, 0, read);
				} else if (read < 0) {
					throw new IOException("oops");
				}
			}
		}

		/**
		 * Read the data from the socket set up for this thread. See
		 * sendTranHeader() in rcPortalOpr.c for the IRODS side of sending
		 * length info to this method.
		 * 
		 * @throws IOException
		 */
		void get() throws IOException {
			log.info("parallel transfer get");

			// read the header
			int operation = readInt();
			if (log.isInfoEnabled()) {
				log.info("   operation:" + operation);
			}

			// read the flags
			int flags = readInt();
			if (log.isInfoEnabled()) {
				log.info("   flags:" + flags);
			}
			// Where to seek into the data
			long offset = readLong();
			if (log.isInfoEnabled()) {
				log.info("   offset:" + offset);
			}
			// How much to read/write
			long length = readLong();
			if (log.isInfoEnabled()) {
				log.info("   length:" + length);
			}

			// Holds all the data for transfer
			byte[] buffer = null;
			int read = 0;

			if (operation != GET_OPR) {
				if (log.isDebugEnabled())
					log.warn("Parallel transfer expected GET, "
							+ "server requested " + operation);
				return;
			}

			if (offset < 0) {
				log
						.warn("offset < 0 in transfer get() operation, return from get method");
				return;
			} else if (offset > 0) {
				local.seek(offset);
			}

			if (length <= 0)
				return;
			else {
				// length has a max of 8mb?
				buffer = new byte[IRODSConnection.OUTPUT_BUFFER_LENGTH];
			}

			while (length > 0) {
				if (log.isDebugEnabled()) {
					log.debug("in read loop, the length of the data is:"
							+ length);
				}
				read = in.read(buffer, 0, Math.min(
						IRODSConnection.OUTPUT_BUFFER_LENGTH, (int) length));
				if (read > 0) {
					log.debug("    result of read > 0");
					length -= read;
					if (length == 0) {
						log.debug("    length == 0, write local");
						local.write(buffer, 0, read);

						// read the next header
						operation = readInt();
						 flags = readInt();
						offset = readLong();
						length = readLong();
						log.debug("    reading next header");
						if (operation == DONE_OPR) {
							log.debug("    done");
							return;
						}

						// probably unnecessary
						local.seek(offset, GeneralRandomAccessFile.SEEK_START);

						// subtract the status message, an int = 9999, and a
						// bunch of 0's
					} else if (length < 0) {
						log.error("    length < 0, throwing ProtocolException");
						throw new ProtocolException();
					} else {
						log.debug("    length > 0, writing to local");
						local.write(buffer, 0, read);
					}
				} else {
					log
							.error("intercepted a loop condition on parallel file get, length is > 0 but I just read and got nothing");
					throw new RuntimeException(
							"possible loop condition in parallel file get");
				}
			}
		}
	}

	public boolean isConnected() {
		return irodsConnection.isConnected();
	}

	public IRODSServerProperties getIrodsServerProperties() {
		return irodsServerProperties;
	}

	protected IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

	protected void setIrodsAccount(IRODSAccount irodsAccount) {
		this.irodsAccount = irodsAccount;
	}

	protected void setIrodsServerProperties(
			IRODSServerProperties irodsServerProperties) {
		this.irodsServerProperties = irodsServerProperties;
	}

	/**
	 * Replicate the given file to all files in the given resource group. This is analagous to an irepl -a command.
	 * @param irodsFile <code>IRODSFile</code> that should be replicated.
	 * @param resourceGroup <code>String<code> that contains the resource group that the file will be replicated to.  The file will replicate to all
	 * members of that resource group.
	 * @throws JargonException
	 */
	protected void replicateToResourceGroup(IRODSFile irodsFile,
			String resourceGroup) throws JargonException {
		DataObjInp dataObjInp = DataObjInp.instanceForReplicateToResourceGroup(
				irodsFile.getAbsolutePath(), resourceGroup);
		irodsFunction(dataObjInp);
	}

}
