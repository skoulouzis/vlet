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
 * $Id: GlobusDelegation.java,v 1.6 2011-05-03 15:02:47 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:47 $
 */ 
// source: 

package nl.uva.vlet.grid.globus.axis;

//import java.io.IOException;
//import java.io.StringReader;
//import java.net.URL;
//import java.rmi.RemoteException;
//import java.security.GeneralSecurityException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.cert.X509Certificate;
//
//import javax.xml.namespace.QName;
//import javax.xml.rpc.Stub;
//
//import nl.uva.vlet.Global;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.util.cog.GridProxy;
//import nl.vlet.uva.grid.globus.GlobusUtil;
//
//import org.apache.axis.AxisEngine;
//import org.apache.axis.EngineConfiguration;
//import org.apache.axis.encoding.AnyContentType;
//import org.apache.axis.message.MessageElement;
//import org.apache.axis.message.addressing.EndpointReference;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.ws.security.WSSConfig;
//import org.apache.ws.security.message.token.PKIPathSecurity;
//import org.globus.axis.util.Util;
//import org.globus.delegation.DelegationConstants;
//import org.globus.delegation.DelegationException;
//import org.globus.delegation.DelegationUtil;
//import org.globus.delegation.service.DelegationResource;
//import org.globus.delegationService.CertType;
//import org.globus.delegationService.DelegationFactoryPortType;
//import org.globus.delegationService.DelegationFactoryServiceAddressingLocator;
//import org.globus.delegationService.DelegationFactoryServiceLocator;
//import org.globus.gsi.GSIConstants;
//import org.globus.gsi.GlobusCredential;
//import org.globus.gsi.X509ExtensionSet;
//import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
//import org.globus.util.I18n;
//import org.globus.ws.trust.RequestSecurityTokenResponseType;
//import org.globus.ws.trust.RequestSecurityTokenType;
//import org.globus.wsrf.encoding.DeserializationException;
//import org.globus.wsrf.encoding.ObjectDeserializer;
//import org.globus.wsrf.encoding.ObjectSerializer;
//import org.globus.wsrf.encoding.SerializationException;
//import org.globus.wsrf.impl.security.SecurityManagerImpl;
//import org.globus.wsrf.impl.security.authentication.Constants;
//import org.globus.wsrf.impl.security.authentication.ContextCrypto;
//import org.globus.wsrf.impl.security.authorization.HostAuthorization;
//import org.globus.wsrf.impl.security.descriptor.ClientSecurityDescriptor;
//import org.globus.wsrf.security.SecurityManager;
//import org.globus.wsrf.utils.AddressingUtils;
//import org.globus.wsrf.utils.XmlUtils;
//import org.oasis.wsrf.properties.GetResourcePropertyResponse;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
///** 
// * Customized Delegation Utility class.
// * <p> 
// * This class initializes the delegation client with an custom client-config.wsdd 
// * file. This code is needed so that from a single client both gt4.x grid services 
// * and (non globus) web services can be accessed. 
// * It uses the DelegationUtil class from Globus but some methods are overridden
// * to allow the custom configuration. 
// * Use 'getAxisEngine' to reuse the correct configured axis engine to the same
// * Globus Service Container.  
// * 
// * @author P.T. de Boer 
// */
//public class GlobusDelegation
//{ 
//	//=== Class Constants ===// 
//	public static int maxCredentialDelegationTime=7*24*60*60; // 7 days in seconds;
//
//	public static final String WSRF_BASE_SERVICE_PATH = "/wsrf/services/";
//
//	public static final String PROTOCOL_ENDPOINT_TYPE="https";
//
//	//=== Class privates ===// 
//
//	/** Custom Axis Engine. */ 
//	private static AxisEngine axisEngine=null; 
//
//	/** Custom initialized Delegation Service Locator (unspecified, can 
//	 * be used for any server) */
//	private static DelegationFactoryServiceLocator delegationFactoryServicelocator=null; 
//
//	private static I18n i18n =  I18n.getI18n("org.globus.delegation.errors",
//			GlobusDelegation.class.getClassLoader());
//
//	static
//	{
//	    // initAxisEngine(); 
//	    
//	}
//	
//	private synchronized static void initAxisEngine()
//	{
//		if (axisEngine==null)
//		{
//			Util.registerTransport();
//			//
//			// Use gt4.x client configuration file: 
//			//
//
//			EngineConfiguration engineConfig = AxisUtil.getGT4EngineConfig(); 
//			// global ServiceLocation can be used for other Delegation Servers // 
//			delegationFactoryServicelocator =  new DelegationFactoryServiceLocator(engineConfig);
//
//			// original code: 
//			//ReliableFileTransferFactoryServiceLocator locatorService = new ReliableFileTransferFactoryServiceLocator(); 
//
//			axisEngine = delegationFactoryServicelocator.getEngine();
//			Global.infoPrintln(GlobusDelegation.class,"--- Globus Delegation Initialized ---"); 
//		}
//	}
//
//	/** 
//	 * Get Axis engine used to communicate to (gt4.x) compatible
//	 * grid service. 
//	 * 
//	 */ 
//
//	public static AxisEngine getAxisEngine()
//	{
//		debug("GetAxisEngine()");
//
//		if (axisEngine==null)
//			initAxisEngine();
//		else
//			debug("Axis engine already initialized");
//
//		return axisEngine;
//	}
//
//	/**
//	 * Modified code from org.globus.delegation to use customized Axis egine !. 
//	 * <p>
//	 * Original comments: 
//	 *************************************************************************
//	 * Store the request token (delegated credential) on the
//	 * delegation service. 
//	 *
//	 * @param delegationServiceUrl
//	 *        Address of delegation service
//	 * @param issuingCred
//	 *        Credential issuing the proxy
//	 * @param certificate
//	 *        The public certificate of the new proxy
//	 * @param lifetime
//	 *        Lifetime of the new proxy in seconds
//	 * @param fullDelegation
//	 *        Indicates whether full delegation is required.
//	 * @param desc
//	 *        Client security descriptor with relevant security properties.
//	 */
//	public static EndpointReferenceType delegate(String delegationServiceUrl,
//			GlobusCredential issuingCred,
//			X509Certificate certificate,
//			int lifetime,
//			boolean fullDelegation,
//			ClientSecurityDescriptor desc)
//	throws DelegationException 
//	{
//		RequestSecurityTokenType token = getTokenToDelegate(issuingCred,
//				certificate,
//				lifetime,
//				fullDelegation);
//		// P.T. de Boer:  
//			// (re)initialize axis engine 
//			// 
//
//			initAxisEngine(); 
//
//		//
//		// configure client and use custom Axis Engine: 
//			// 
//
//			EndpointReference ref =  null;
//			try
//			{
//				URL url = new URL(delegationServiceUrl);
//				DelegationFactoryPortType delegationPort =
//					delegationFactoryServicelocator.getDelegationFactoryPortTypePort(url);
//
//				// P.T. de Boer: 
//					// update/reuse AXIS engine !! 
//				// Already done in initAxis(): delegationFactoryServicelocator.setEngine(getAxisEngine());
//
//				if (desc != null) {
//					((Stub)delegationPort)
//					._setProperty(Constants.CLIENT_DESCRIPTOR, desc);
//				}
//
//				RequestSecurityTokenResponseType response =
//					delegationPort.requestSecurityToken(token);
//
//				// Extract EPR from response
//				MessageElement elem[] = response.get_any();
//
//				ref = new EndpointReference(elem[0].getAsDOM());
//
//			} catch (Exception exp) {
//				error(exp);
//				throw new DelegationException(exp);
//			}
//			return ref;
//	}
//
//	/**
//	 * Create a new proxy with said lifetime, signed by issuing
//	 * credential. Return the proxy as a security token.
//	 *
//	 * @param issuerCertificateChain
//	 *        First certificate in this chain is used as issuing
//	 *        certificate
//	 * @param issuerKey
//	 *        New proxy will be signed with this key
//	 * @param publicKey
//	 *        The public key of the new proxy
//	 * @param lifetime
//	 *        Lifetime of the new proxy in seconds
//	 * @param fullDelegation
//	 *        Indicates whether full delegation is required.
//	 * @return RequestSecurityTokenType
//	 *         The new proxy as a security token.
//	 */
//	public static RequestSecurityTokenType
//	getTokenToDelegate(X509Certificate[] issuerCertificateChain,
//			PrivateKey issuerKey, PublicKey publicKey,
//			int lifetime, boolean fullDelegation)
//	throws DelegationException {
//
//		// Uses first certificate in chain  (cert[0])
//		BouncyCastleCertProcessingFactory certFactory =
//			BouncyCastleCertProcessingFactory.getDefault();
//		X509Certificate newCert = null;
//		int delegType = GSIConstants.DELEGATION_LIMITED;
//		if (fullDelegation)
//			delegType = GSIConstants.DELEGATION_FULL;
//
//		try {
//			newCert = certFactory
//			.createProxyCertificate(issuerCertificateChain[0],
//					issuerKey,
//					publicKey, lifetime, delegType,
//					(X509ExtensionSet)null, null);
//		} catch (GeneralSecurityException exp) {
//			error(i18n.getMessage("createDelegCred"), exp);
//			throw new DelegationException(i18n.getMessage("createDelegCred"),
//					exp);
//		}
//
//		X509Certificate[] newChain =
//			new X509Certificate[issuerCertificateChain.length + 1];
//		newChain[0] = newCert;
//		System.arraycopy(issuerCertificateChain, 0, newChain, 1,
//				issuerCertificateChain.length);
//
//		debug("New delegated chain");
//		for (int i=0; i<newChain.length; i++) {
//			debug(newChain[i].getSubjectDN());
//		}
//
//		PKIPathSecurity token = getPKIToken(newChain, false);
//
//		// New chain
//		debug("New certificate chain");
//		for (int i=0; i<newChain.length; i++) {
//			debug(newChain[i].getSubjectDN());
//		}
//
//		MessageElement msgElem =
//			new MessageElement(token.getElement());
//		RequestSecurityTokenType requestToken =
//			new RequestSecurityTokenType();
//		requestToken.set_any(new MessageElement[] { msgElem });
//
//		return requestToken;
//	}
//
//
//	/**
//	 * Create a new proxy with said lifetime, using the public key of
//	 * certificate and signed by issuing credential. Return the proxy
//	 * as a security token.
//	 *
//	 * @param issuingCred
//	 *        Credential issuing the proxy
//	 * @param certificate
//	 *        The public certificate of the new proxy
//	 * @param lifetime
//	 *        Lifetime of the new proxy in seconds
//	 * @param fullDelegation
//	 *        Indicates whether full delegation is required.
//	 * @return RequestSecurityTokenType
//	 *         The new proxy as a security token.
//	 * @see #getTokenToDelegate(X509Certificate[], PrivateKey,
//	 * PublicKey, int, boolean)
//	 */
//	public static RequestSecurityTokenType
//	getTokenToDelegate(GlobusCredential issuingCred,
//			X509Certificate certificate,
//			int lifetime, boolean fullDelegation)
//	throws DelegationException {
//		return getTokenToDelegate(issuingCred.getCertificateChain(),
//				issuingCred.getPrivateKey(),
//				certificate.getPublicKey(), lifetime,
//				fullDelegation);
//	}
//
//	private static PKIPathSecurity getPKIToken(X509Certificate[] certChain,
//			boolean reverse) throws DelegationException    
//			{
//
//		PKIPathSecurity token = null;
//		try 
//		{
//			Document doc = XmlUtils.newDocument();
//			token = new PKIPathSecurity(WSSConfig.getDefaultWSConfig(), doc);
//			token.setX509Certificates(certChain, reverse, new ContextCrypto());
//		} 
//		catch (Exception exp) 
//		{
//			error(exp);
//			throw new DelegationException(exp);
//		}
//		return token;
//
//			}
//
//
//
//	/** Returns WSRF complaint Delegation Factory Url String */ 
//	public static String createFactoryUrl(String host, String port)
//	{	
//		String delegationFactoryUrl = PROTOCOL_ENDPOINT_TYPE + "://"
//		+ host + ":" + port + WSRF_BASE_SERVICE_PATH
//		+ DelegationConstants.FACTORY_PATH;
//
//		return delegationFactoryUrl; 
//	}
//
//	/**
//	 * Modified code from Globus: Added use of custom Axis Engine a
//	 * and correct chaining of Exceptions. 
//	 * <hr>  
//	 * Retrieve certificate chain from resource property on Delegation
//	 * Factory Service. The class it deserializes into should contain
//	 * a <code>BinarySecurity</code> token.
//	 *
//	 * @param epr
//	 *        Endpoint reference to delegation factory service
//	 * @param qName
//	 *        QName of the resource property
//	 * @param rpClass
//	 *        Class to deserialize it as
//	 * @param desc
//	 *        Client security descriptor with relevant security
//	 *        properties.
//	 * @return X509Certificate[]
//	 *        Certificate chain contained in the token.
//	 */
//	protected static X509Certificate[]
//	                                 getCertificateChainRP(EndpointReferenceType epr, QName qName,
//	                                		 Class<?> rpClass, ClientSecurityDescriptor desc)
//	throws Exception {
//
//		DelegationFactoryPortType delegationPort = null;
//
//		try 
//		{
//			DelegationFactoryServiceAddressingLocator locator =
//				new DelegationFactoryServiceAddressingLocator();
//
//			// ===
//				// P.T. de Boer: resuse Axis Engine !  
//			locator.setEngine(getAxisEngine()); 
//			// === 
//			delegationPort = locator.getDelegationFactoryPortTypePort(epr);
//		}
//		catch (Exception exp) 
//		{
//			error(exp);
//			throw new VlException("DelegationException",exp);
//		}
//
//		if (desc != null) 
//		{
//			((Stub)delegationPort)._setProperty(Constants.CLIENT_DESCRIPTOR, 
//					desc);
//		}
//
//		GetResourcePropertyResponse response = null;
//		try 
//		{
//			response = delegationPort.getResourceProperty(qName);
//		}
//		catch (RemoteException exp) 
//		{
//			error(exp);
//			throw new VlException("DelegationException",exp);
//		}
//
//		PKIPathSecurity token = null;
//		try 
//		{
//			MessageElement elem[] = response.get_any();
//			AnyContentType certType =
//				(AnyContentType)ObjectDeserializer.toObject(elem[0], rpClass);
//
//			elem = certType.get_any();
//			token = new PKIPathSecurity(WSSConfig.getDefaultWSConfig(),
//					elem[0].getAsDOM());
//		}
//		catch (Exception exp) 
//		{
//			error(exp);
//			throw new VlException("DelegationException",exp);
//		}
//
//		X509Certificate[] certChain = null;
//		// certChain[0] is end cert.
//		try
//		{
//			certChain = token.getX509Certificates(false,
//					new ContextCrypto());
//		}
//		catch (IOException exp) 
//		{
//			error(exp);
//			throw new VlException("DelegationException",exp);
//		}
//
//		// Verify this chain, create temp credential
//		GlobusCredential credential = new GlobusCredential(null, certChain);
//		try 
//		{
//			credential.verify();
//		}
//		catch (Exception exp) 
//		{
//			throw new VlException("DelegationException",exp);
//		}
//
//		return certChain;
//	}
//
//	/**
//	 * Modified code from Globus: Added use of custom Axis Engine a
//	 * and correct chaining of Exceptions. 
//	 * <p>
//	 * Retrieve certificate chain from resource property on Delegation
//	 * Factory Service.
//	 *
//	 * @param epr
//	 *        Endpoint reference to delegation factory service
//	 * @param desc
//	 *        Client security descriptor with relevant security
//	 *        properties.
//	 * @return X509Certificate[]
//	 *        Certificate chain contained in the token.
//	 */
//	public static X509Certificate[] getCertificateChainRP(EndpointReferenceType epr,ClientSecurityDescriptor desc)
//	throws Exception 
//	{
//		QName certChainRp =  new QName(DelegationConstants.NS, "CertificateChain");
//		return getCertificateChainRP(epr, certChainRp, CertType.class, desc);
//	}
//
//	/**
//	 * Server Side code to get the delegated Credential from the EPR. 
//	 * 
//	 * @author Kamel Boulebiar (updated by Piter T. de Boer)
//	 *  
//	 */
//	public static GlobusCredential getDelegatedCredential(String eprRepresentation) 
//	throws DeserializationException, DelegationException
//	{
//		// Check Caller ID:
//
//		SecurityManager man= SecurityManagerImpl.getManager();
//		String callerID=man.getCaller();
//
//		Debug("Received Delegation Credential from:"+callerID);
//
//		InputSource is=new InputSource(new StringReader(eprRepresentation));
//		EndpointReferenceType endpoint = (EndpointReferenceType) ObjectDeserializer.deserialize(is, EndpointReferenceType.class);
//
//		DelegationResource delRes = DelegationUtil.getDelegationResource(endpoint);
//		GlobusCredential gc = delRes.getCredential();	        
//		Debug("Caller Subject from Delegation = "+ gc.getSubject()+"\n");
//		return gc; 
//	}
//
//	/**
//	 *  Create String representation from Reference
//	 *  <br>
//	 *  Deprecated: Do not serialize and deserialize EndpointReferenceType but
//	 *              use native (WSDL) type: wsa:EndpointReferenceType.<br> 
//	 * <br>
//	 * WSDL is:<br>
//	 * 	<code>&lt;xsd:element name="delegationEPR" type="wsa:EndpointReferenceType"/&gt;</code> 
//	 * </pre>
//	 * <br>
//	 */
//	public static String delegationEPR2String(EndpointReferenceType epr) throws SerializationException
//	{
//		String str = ObjectSerializer.toString(epr, new QName("", "DelegatedEPR"));
//
//		// hack: Stil wrong Axis configurations ! 
//
//		//String eprStr=StringUtil.findReplace(str,
//		//		 "xsi:type=\"xsd:anyURI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"",
//		//		 "xsi:type=\"ns1:AttributedURI\"");
//
//		return str; //eprStr; 
//	}
//
//
//	/**
//	 * Create Endpoint ReferenceType from String Representation
//	 * <br>
//	 * Deprecated: Do not serialize and deserialize EndpointReferenceType but
//	 *             use native (WSDL) type: wsa:EndpointReferenceType.<br> 
//	 * <br>
//	 * WSDL is:<br>
//	 * 	<code>&lt;xsd:element name="delegationEPR" type="wsa:EndpointReferenceType"/&gt;</code> 
//	 * </pre>
//	 * <br> 
//	 */ 
//	public static EndpointReferenceType string2EPR(String eprString) throws DeserializationException 
//	{
//		InputSource is=new InputSource(new StringReader(eprString));
//		EndpointReferenceType endpoint = (EndpointReferenceType) ObjectDeserializer.deserialize(is, EndpointReferenceType.class);
//		return endpoint;
//	}
//
//
//	/** 
//	 * Custom delegation: Uses Customized Axis enginge to delegate 
//	 * Credentials. Uses Full Delegation and sets Credential life time
//	 * to match Proxy's lifetime 
//	 */ 
//
//	public static EndpointReferenceType gt4Delegate(String host,int port,
//			GlobusCredential credential) throws Exception
//	{
//		return gt4Delegate(host,port,true,minInt(credential.getTimeLeft(),maxCredentialDelegationTime),credential);
//	}
//	
//	/**
//	 * Returns smallest integer value but not bigger than Integer.MAX_VALUE 
//	 */ 
//	public static int minInt(long val1,int val2) 
//	{
//		long result=Math.min(val1,val2);
//		
//		if (result<Integer.MAX_VALUE)
//			return (int)result;
//		else
//		{
//			Global.warnPrintln(GlobusDelegation.class,"*** Warning: Integer Overflow:"+result); 
//			return Integer.MAX_VALUE;
//		}
//	}
//
//	/**@deprecated use gt4Delegate() */	
//	public static EndpointReferenceType customDelegate(String host,int port,
//			GlobusCredential credential) throws	/**@deprecated use gt4Delegate() */ Exception
//	{
//		return gt4Delegate(host,port,true,minInt(credential.getTimeLeft(),maxCredentialDelegationTime),credential);
//	}
//
//	/**@deprecated use gt4Delegate() */ 
//	public static EndpointReferenceType customDelegate(String host,int port,
//			boolean fullLifeTime,int lifetimeInSeconds,GridProxy gridProxy) throws Exception
//	{
//		return gt4Delegate(host,port,fullLifeTime,lifetimeInSeconds,GlobusUtil.getGlobusCredential(gridProxy)); 
//	} 
//	
//	/** 
//	 * Custom gt4 delegation: Uses gt4 configured Axis engine to delegate 
//	 * credentials. 
//	 */ 
//	public static EndpointReferenceType gt4Delegate(String host,int port,
//			boolean fullLifeTime,int lifetimeInSeconds,GlobusCredential credential) throws Exception
//	{
//
//		Global.infoPrintln(GlobusDelegation.class,"Custom Globus Delegation to:"+host+":"+port); 
//		
//		String delegationFactoryUrl=GlobusDelegation.createFactoryUrl(host,""+port); 
//
//		//Util.registerTransport();
//
//		
//		// Delegate Credentials: 
//		// ====================================================================
//		//getting endpoint reference for the delagation factory service
//		EndpointReferenceType delegEpr =
//			AddressingUtils.createEndpointReference(delegationFactoryUrl,null);
//		Debug("delegEpr="+delegEpr); 
//
//		Debug("delegEpr serialized="+GlobusDelegation.delegationEPR2String(delegEpr));
//
//		// Set client security parameters for services stubs
//		// ==================================================
//		ClientSecurityDescriptor secDesc =  new ClientSecurityDescriptor();
//		
//		// =================================================================
//		// Globus 4.0/4.1 fix
//		// GT4.1 Method: ? 
//		secDesc.setGSISecureTransport(Constants.SIGNATURE);
//	    // GT4.0 method ? : 
//		// secDesc.setGSITransport(Constants.SIGNATURE);
//	    // ==================================================================
//	    
//	    secDesc.setAuthz(HostAuthorization.getInstance());
//
//		//generate an array of X509Certificates from the delegfactory service.
//		X509Certificate[] cert =  getCertificateChainRP(delegEpr,secDesc);
//
//
//		//the client needs the first certificate on the chain to be delegated later
//		X509Certificate certToSign = cert[0];
//
//
//		//create a security token (delegated credential) that is stored by the delegfactory service.
//		EndpointReferenceType credentialEndpoint =
//
//			GlobusDelegation.delegate (delegationFactoryUrl,credential,certToSign,lifetimeInSeconds,fullLifeTime,secDesc);
//		return credentialEndpoint;
//	}
//
//	/**
//	 * Default Delegation. Assumes default properties and delegates credential
//	 * to remote service container running at host:port. 
//	 * Use the returned EPR to set the delegation EPR on the (remote) service. 
//	 */
////	private static EndpointReferenceType defaultDelegationOld(String host,int port,boolean fullDelegation,int lifetimeInSeconds,GlobusCredential credential) throws Exception
////	{
////		Util.registerTransport();
////
////
////		// 3. Set client security parameters for services stubs
////
////		String delegationFactoryUrl=GlobusDelegation.createFactoryUrl(host,""+port); 
////
////		ClientSecurityDescriptor secDesc =  new ClientSecurityDescriptor();
////		secDesc.setGSISecureTransport(Constants.SIGNATURE);
////		secDesc.setAuthz(HostAuthorization.getInstance());
////
////		// 4. Delegate Credentials: for use by RFT and GridFTP to do file staging
////		// ====================================================================
////
////
////		//getting endpoint reference for the delagation factory service
////		EndpointReferenceType delegEpr =
////			AddressingUtils.createEndpointReference(delegationFactoryUrl,null);
////
////
////		//generate an array of X509Certificates from the delegfactory service.
////
////		X509Certificate[] cert =
////			DelegationUtil.getCertificateChainRP(delegEpr,secDesc);
////
////
////		//the client needs the first certificate on the chain to be delegated later
////
////		X509Certificate certToSign = cert[0];
////		//create a security token (delegated credential) that is stored by the delegfactory service.
////
////		EndpointReferenceType credentialEndpoint =
////
////			DelegationUtil.delegate (delegationFactoryUrl,credential,certToSign,lifetimeInSeconds,fullDelegation,secDesc);
////		return credentialEndpoint;
////	}
//
//	//
//	// === Class Misc. ===
//	// 
//
//	private static void error(Exception exp)
//	{
//		Global.errorPrintln(GlobusDelegation.class,"Exception:"+exp); 
//	}
//
//	private static void debug(Object msg)
//	{
//		Global.debugPrintln(GlobusDelegation.class,msg.toString()); 
//	}
//
//	private static void error(String msg, Exception exp)
//	{
//		Global.errorPrintln(GlobusDelegation.class,msg); 
//		Global.errorPrintln(GlobusDelegation.class,"Exception="+exp);  
//	}
//
//	private static void Debug(String msg) 
//	{
//		Global.debugPrintln(GlobusDelegation.class,msg); 
//	}
//}
