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
 * $Id: VomsProxyCredential.java,v 1.8 2011-05-03 15:02:47 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:47 $
 */ 
// source: 

package nl.uva.vlet.grid.voms;

/* 
 * ***
 * Modified by Piter T. de Boer
 * - Cleaned up the code, added initializer and organized the constructors.
 * - disable 'verify()' code is clearly not ready. 
 * - removed exception handling, exceptions are exposed to caller 
 *   and nested to provide better information to end user.   
 * ***
 * 
 * This class is a rewrite of the classes VOMSClient and
 * MyGlobusCredentialUtils 
 *
 * Original source from:  
 *      Gidon Moont 
 *      Imperial College London
 *
 * Most of the changes involve exception handling and logging.  
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.Hashtable;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlServerException;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.X509ExtensionSet;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.bc.BouncyCastleX509Extension;
import org.globus.gsi.gssapi.GSSConstants;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.GlobusGSSManagerImpl;
import org.globus.gsi.gssapi.auth.Authorization;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;
import org.globus.gsi.gssapi.net.GssSocket;
import org.globus.gsi.gssapi.net.GssSocketFactory;
import org.gridforum.jgss.ExtendedGSSContext;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;

/**
 * The actual credential that is build of a GlobusCredential and an
 * AttributeCertificate. The AttributeCertificate is sent by the VOMS server
 * after sending one of this commands:
 * 
 * <br>
 * A - this means get everything the server knows about you <br>
 * G/group - This means get group informations. /group should be /vo-name. <br>
 * This is the default request used by voms-proxy-init <br>
 * Rrole - This means grant me the specified role, in all groups in which <br>
 * you can grant it. <br>
 * Bgroup:role - This means grant me the specified role in the specified group.
 * 
 * Most of the code that does the voms magic is written by Gidon Moont from
 * Imperial College London, http://gridportal.hep.ph.ic.ac.uk/
 * 
 * @author Gidon Moont, modified by Piter T. de Boer (exception handling, logging)
 */
public class VomsProxyCredential
{
	// === Class Stuff === //
	
	public static class VomsInfo extends Hashtable<String,Object> {};
	
	// === Instance Stuff === //
	
	//private static boolean use_legacy_cog_1_4_extensions=false;
	
	private GlobusCredential plainProxy = null;

	private GlobusCredential vomsProxy = null;

	private AttributeCertificate ac = null;

	private VOMSAttributeCertificate vomsac = null;

	private String command = null;

	private long lifetimeInSeconds = 60*60; // default to one hour.

	private VO vo = null;


	/**
	 * Added constructor for use with a GSSCredential
	 * 
	 * @param globusCred2
	 * @param vo
	 * @param command
	 * @param lifetime_in_hours
	 * @throws Exception
	 */

	public VomsProxyCredential(GlobusCredential globusCred, VO vo,
			String command, long lifetime) throws Exception
	{
		init(globusCred, vo, command, lifetime);
	}

	/**
	 * The default constructor. Creates a VomsProxyCredential.
	 * 
	 * @param plainProxy
	 *            a X509 proxy (can be the local proxy or a myproxy proxy
	 *            credential.
	 * @param vo
	 *            the VO
	 * @param command
	 *            the command to send to the VOMS server
	 * @param lifetime_in_hours
	 *            the lifetime of the proxy in hours
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws GSSException
	 * @throws Exception
	 *             if something fails, obviously
	 * 
	 * @author Modified by Piter T. de Boer
	 */
	public VomsProxyCredential(GlobusCredential gridProxy, VO vo,
			String command, int lifetimeInSeconds) throws Exception
	{
		init(gridProxy, vo, command, lifetimeInSeconds);
	}

	/**
	 * Generic initializer (all-for-one).
	 * 
	 * @author Added by Piter T. de Boer
	 */

	private void init(GlobusCredential gridProxy, VO vo, String command,
			long lifetime) throws Exception
	{
		infoPrintf("Creating new proxy using VO:%s\n",vo); 
		
		this.plainProxy = gridProxy;
		this.vo = vo;
		this.command = command;
		this.lifetimeInSeconds = lifetime;
		createAC();
		generateProxy();
		vomsac = new VOMSAttributeCertificate(ac);
	}

	public void dispose()
	{
		plainProxy = null;
		vomsProxy = null;
		ac = null;
	}
	
	public AttributeCertificate getAttributeCertificate()
	{
		return ac;
	}

	/**
	 * Contacts the VOMS server to get an AttributeCertificate
	 * 
	 * @return true if successful, false if not
	 * @throws GSSException
	 * @throws IOException
	 */
	private boolean createAC() throws Exception
	{
		String hostid = vo.getDefaultHost() + ":" + vo.getDefaultPort();
		
		infoPrintf("Contacting VOMS server [" + hostid + "] for vo:"+vo.getVoName()+"\n");

		// System.out.println("Contacting VOMS server [" + vo.getHost() + " on
		// port "+ vo.getPort()+ " ]...");
		GSSManager manager = new GlobusGSSManagerImpl();

		Authorization authorization = new IdentityAuthorization(vo.getDefaultHostDN());

		GSSCredential clientCreds = (GSSCredential) new GlobusGSSCredentialImpl(
				plainProxy, GSSCredential.INITIATE_ONLY);

		ExtendedGSSContext context = (ExtendedGSSContext) manager
				.createContext(null, GSSConstants.MECH_OID, clientCreds,
						GSSContext.DEFAULT_LIFETIME);

		context.requestMutualAuth(true);
		context.requestCredDeleg(false);
		context.requestConf(true);
		context.requestAnonymity(false);

		context.setOption(GSSConstants.GSS_MODE, GSIConstants.MODE_GSI);
		context.setOption(GSSConstants.REJECT_LIMITED_PROXY,
						new Boolean(false));

		GssSocket socket = null;
		OutputStream out=null;
		InputStream in=null;
		//
		// P.T. de Boer:
		// Nest exception and add usefull information to exception:
		//
		
		try
		{
			socket = (GssSocket) GssSocketFactory.getDefault().createSocket(
					vo.getDefaultHost(), vo.getDefaultPort(), context);
			socket.setWrapMode(GssSocket.GSI_MODE);
			socket.setAuthorization(authorization);

			out = ((Socket) socket).getOutputStream();
			in = ((Socket) socket).getInputStream();
		}
		//
		// NoRoute= wrong port and/or hostname
        catch (java.net.NoRouteToHostException e)
        {
            // Wrap as nested VL Exception and provide better
            // information:
            throw new VlIOException(
                    "Communication Error. Adres or port is wrong or server is not reachable:"
                            + hostid, e);
        }
        catch (java.net.ConnectException e)
        {
            // Wrap as nested VL Exception and provide better
            // information:
            throw new VlIOException(
                    "Connection Error. Adres or port is wrong or server is not reachable:"
                            + hostid, e);
        }
		catch (java.net.SocketException e)
		{
		    // Generic Socket Exception. 
			// Wrap as nested VL Exception and provide better
			// information:
			// when authentication fails, the socket is closed also.
			throw new VlIOException(
					"Communication Error. Either SSL authentication failed or the adres or port is wrong (server not reachable):"
							+ hostid, e);
		}


		/*
		 * if (socket.isConnected()==false) { throw new IOException("Socket not
		 * connected:"+socket.getInetAddress()+":"+socket.getPort()); }
		 */
		if (in == null)
		{
			// VlException
			throw new VlIOException("Couldn't read from socket:"
					+ socket.getInetAddress() + ":" + socket.getPort());

		}
	    String msg = new String(
				"<?xml version=\"1.0\" encoding = \"US-ASCII\"?>" 
		        +"<voms>" 
		           +"<command>"+command + "</command>"
		           +"<lifetime>" + lifetimeInSeconds + "</lifetime>"
		        +"</voms>");	

		debugPrintf("Sending message to:%s\n--- START ---\n%s\n--- END ---\n",hostid,msg);
		
		byte[] outToken = msg.getBytes();

		out.write(outToken);
		out.flush();

		StringBuffer voms_server_answer = new StringBuffer();

		BufferedReader buff = new BufferedReader(new InputStreamReader(in));

		char[] buf = new char[1024];
		int numRead = 0;
		//
		// read loop:
		//
		do
		{
			numRead = buff.read(buf);
			if (numRead > 0)
			{
				voms_server_answer.append(buf, 0, numRead);
			}
		} while (numRead >= 0); // while not EOF

		if (voms_server_answer.length() <= 0)
		{
			errorPrintf("empty or null voms_server_answer\n");

			// P.T. de Boer: Do error checking !
			throw new VlIOException("NULL reply from socket (command=" + command
					+ "):" + socket.getInetAddress() + ":" + socket.getPort());
		}
		// String answer = buff.readLine();

		out.close();
		in.close();
		buff.close();

		String answer = voms_server_answer.toString();

		if (answer.indexOf("<error>") > 0)
		{
			String errormsg = answer.substring(answer.indexOf("<message>") + 9,
					answer.indexOf("</message>"));
			infoPrintf("Received error message from server:%s\n",errormsg);
 
			// P.T. de Boer:
			// This is NOT a warning: myLogger.warn("VOMS server returned an
			// error => " + errormsg);
			// throw error:
			throw new VlServerException("Error when communicating with:"
					+ hostid + ".\nError=" + errormsg);
		}

		String encoded;
		try
		{
			encoded = answer.substring(answer.indexOf("<ac>") + 4, answer
					.indexOf("</ac>"));
		}
		catch (IndexOutOfBoundsException e)
		{
			// P.T. de Boer. This is an error as well: Nest Exception:
			throw new VlServerException(
					"Message Error. Could not find encoded voms proxy in server answer.",
					e);
		}

		// System.out.println(" succes " + encoded);

		try
		{
			byte[] payload = VincenzoBase64.decode(encoded);
			// byte[] payload = Base64Coder.decode(encoded);
			//Debug(4,"Payload="(new String(payload))
			ByteArrayInputStream is = new ByteArrayInputStream(payload);
			ASN1InputStream asnInStream = new ASN1InputStream(is);

			// org.bouncycastle.asn1.BERTaggedObjectParser btp =
			// (org.bouncycastle.asn1.BERTaggedObjectParser)asnInStream.readObject();
			ASN1Sequence acseq = (ASN1Sequence) asnInStream.readObject();
			ac = new AttributeCertificate(acseq);
			return true;
		}
		catch (Exception e)
		{
			// P.T. de Boer nested VlException
			throw new VlException("DecodingError",
					"Couldn't decode server answer\n" + encoded,e);
		}
	}

    private void debugPrintf(String format,Object... args)
    {
        Global.debugPrintf(this, format,args);
    }
    
    private void infoPrintf(String format,Object... args)
    {
        Global.infoPrintf(this, format,args); 
    }
    
	private void errorPrintf(String format,Object... args)
	{
		Global.errorPrintf(this, format,args); 
	}    
	
	/**
	 * Gathers information in the attribute certificate into an ArrayList
	 * 
	 * @return the equivalent of a commandline voms-proxy-info --all / null if
	 *         something's not right
	 * @throws Exception
	 */
	public VomsInfo getVomsInfo() throws Exception
	{
		
		VomsInfo info=new VomsInfo();
		
		//TODO: String Contstants/Attributes 
		info.put("issuer",vomsac.getIssuer());
		
		boolean checked = vomsac.verify();

		if (checked)
		{
			info.put("verified","true");
			info.put("verifiedInfo","Signature of AC is OK."); 

		}
		else
		{
			info.put("verified","false"); 
			info.put("verifiedInfo","Couldn't verify signature of AC"); 

		}

		long milliseconds = vomsac.getTime();
		info.put("timeLeftMillies",new Long(milliseconds)); 
		
		if (milliseconds > 0)
		{
			int hours = new Long(milliseconds / (1000 * 3600)).intValue();
			int minutes = new Long((milliseconds - hours * 1000 * 3600)
					/ (1000 * 60)).intValue();
			int seconds = new Long(
					(milliseconds - (hours * 1000 * 3600 + minutes * 1000 * 60)) / 1000)
					.intValue();
			
			String timeStr=""+hours+":"+((minutes<10)?"0":"")+minutes
				+":"+((seconds<10)?"0":"")+seconds;
			
			info.put("timeLeftString",timeStr);  
	
		}
		else
		{
			info.put("timeLeftString","No time left!");
		}
		info.put("holder",vomsac.getHolder());
		info.put("version",vomsac.getVersion());
		info.put("algorithm",vomsac.getAlgorithmIdentifier());
		info.put("serialNumber",vomsac.getSerialNumberIntValue());
		
		int index=0; 
		for (String line : vomsac.getVOMSFQANs())
		{
			info.put("attribute["+index+"]",line);
			index++; 
		}

		
		String key; 
		
		for (Enumeration<String> keys = info.keys(); keys.hasMoreElements();)
		{
			key=keys.nextElement();
			infoPrintf("VOMSinfo: %s=%s\n",key,info.get(key));
		}
		
		return info;
	}

	private void generateProxy() throws GeneralSecurityException
	{

		// Extension 1
		DERSequence seqac = new DERSequence(this.ac);
		DERSequence seqacwrap = new DERSequence(seqac);
		BouncyCastleX509Extension ace = new BouncyCastleX509Extension(
				VomsUtil.CERT_VOMS_EXTENSION_OID, seqacwrap);

		// Extension 2
		KeyUsage keyUsage = new KeyUsage(KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment | KeyUsage.dataEncipherment);
		BouncyCastleX509Extension kue = new BouncyCastleX509Extension(
				"2.5.29.15", keyUsage.getDERObject());

		// ==================================================================
		// Warning does not work with cog-jglobus-1.5 and higher,
		// but is necessary for now ! 
		// Else WMS doesn't recognize the VOMS information in the Proxy. 
		//
		X509ExtensionSet globusExtensionSet = null;
		//if (use_legacy_cog_1_4_extensions)
		{
			globusExtensionSet=new X509ExtensionSet();
			globusExtensionSet.add(ace);
			globusExtensionSet.add(kue);	
		}
        
        // ================================================================== 
		// generate new VOMS proxy
		BouncyCastleCertProcessingFactory factory = BouncyCastleCertProcessingFactory
				.getDefault();
		vomsProxy = factory.createCredential(plainProxy.getCertificateChain(),
				plainProxy.getPrivateKey(), plainProxy.getStrength(),
				(int) plainProxy.getTimeLeft(), GSIConstants.DELEGATION_FULL,
				globusExtensionSet);

		infoPrintf("generateProxy() done.\n");
	}

	/**
	 * @return the voms enabled proxy
	 */
	public GlobusCredential getVomsProxy()
	{
		return vomsProxy;
	}


	public void printInfo(PrintStream out)
	{
		VomsInfo info;
		
		try
		{
			info = getVomsInfo();
		}
		catch (Exception e)
		{
			out.println("Exception:"+e);
			return;
		} 

		if (info!=null)
		{
			for (Enumeration<String> keys = info.keys(); keys.hasMoreElements();)
			{
				String key=keys.nextElement();
				out.println("vomsinfo."+key+"="+info.get(key));
			}
		}
		else
		{
			out.println("NULL voms info:");
		}
	}
	
}
