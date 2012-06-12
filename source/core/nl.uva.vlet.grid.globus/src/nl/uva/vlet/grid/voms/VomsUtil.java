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
 * $Id: VomsUtil.java,v 1.9 2011-04-18 12:08:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:08:39 $
 */ 
// source: 

package nl.uva.vlet.grid.voms;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlAuthenticationException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.grid.voms.VomsProxyCredential.VomsInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrl.VRL;
import nl.vlet.uva.grid.globus.GlobusCredentialWrapper;
import nl.vlet.uva.grid.globus.GlobusUtil;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.globus.gsi.GlobusCredential;
import org.ietf.jgss.GSSException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Static utility class for VomsProxyCredential.
 * 
 * @author P.T. de Boer
 */
public class VomsUtil
{
    private static ClassLogger logger; 
	// ==================
	// Registry
	// ==================

	private static Hashtable<String, VO> vomsRegistry = new Hashtable<String, VO>();

	public static final String CERT_VOMS_EXTENSION_OID="1.3.6.1.4.1.8005.100.100.5"; 
	
	static
	{
		initVoms();
		logger=ClassLogger.getLogger(VomsUtil.class); 
		//logger.setLevelToDebug(); 
		
	}

	private static void initVoms()
	{
		// nop 
		/* for now: use: VO.loadFromXMLFile(...) to scan voms.xml file. 
		addVO("pvier", "voms.grid.sara.nl", 30000,
				"/O=dutchgrid/O=hosts/OU=sara.nl/CN=voms.grid.sara.nl");

		addVO("vlemed", "voms.grid.sara.nl", 30003,
				"/O=dutchgrid/O=hosts/OU=sara.nl/CN=voms.grid.sara.nl");
		*/
	}

	// ==================
	// Utily methods
	// ==================

	/**
	 * Create VomsProxyCredential from GlobusCredentail using the information
	 * provided by the VO.
	 * 
	 * @throws VlException
	 */
	public static VomsProxyCredential createVomsCredential(
			GlobusCredential globusCred, VO vo, long lifetime)
			throws VlException
	{
	    if (vo==null)
            throw new NullPointerException("VO object can not be null");  
        
		VomsProxyCredential vomscred;

		try
		{
			vomscred = new VomsProxyCredential(globusCred, vo, "G/"
					+ vo.getVoName(), lifetime);
			return vomscred;
		}
		// Exception Nesting:
		catch (Exception e)
		{
		    throw convertException("Couldn't create VOMS proxy for:"+vo.getVoName(),e);
		}
	}

	private static VlException convertException(String message,Exception e)
	{
	    // Filter standard Globus exceptions: 
	    
	    VlException ex=GlobusUtil.checkException(message, e); 
	    if (ex!=null)
	        return ex;
	     
		// Exception nesting:
		if (e instanceof VlException)
		{
			return ((VlException) e); // keep my own;
		}
		else if (e instanceof GeneralSecurityException)
		{
			return new VlAuthenticationException(message
			        +"\nReason="+e.getMessage(),
					e);
		}
		else if (e instanceof GSSException)
		{
		    return new VlAuthenticationException(message
                    +"\nReason="+e.getMessage(),
                    e);
		}
		else if (e instanceof IOException)
		{
		    return new VlIOException(message+"\nReason="+e.getMessage(), e);
		}
		
		// default: 
		return new VlException(e.getClass().getSimpleName(),
					e.getMessage(), e);

	}

	/**
	 * Convert GlobusCredential to VOMS enbled GlobusCredential, using the VO
	 * information and lifetime (in seconds)
	 * @param vo VO INfo Object 
     * @param optVoGroup if the groupname differs from the VOname specify this value  
     * @param optVoRole optional VO Role 
	 */
	public static VomsProxyCredential vomsify(GlobusCredential orgCred,
	        VO vo,
	        String optVoGroup, 
			String optVoRole, long lifetimeInSeconds) throws VlException
	{
	    if (vo==null)
	        throw new NullPointerException("VO object can not be null");  
	       
		logger.infoPrintf("vomsify(): VO=%s, group=%s, role=%s\n",vo,optVoGroup,optVoRole); 
		
		String voprefix=optVoGroup; // group name: VOName/VOGroup 
		
		if (voprefix==null)
		    voprefix=vo.getVoName(); //plain name
		
		// construct VOName/Group:Role command 
		String cmd="B/"+voprefix;  
		
		if ((optVoRole!=null) && (optVoRole.equals("")==false))
		    cmd=cmd+":"+optVoRole;  
		
		try
		{
			VomsProxyCredential vomscred = new VomsProxyCredential(orgCred,
			        vo,
					cmd, 
					lifetimeInSeconds);

			return vomscred;
		}
		// Exception Nesting:
		catch (Exception e)
		{
		    throw convertException("Couldn't create VOMS proxy for:"+vo.getVoName(),e);
		}
	}

//	/**
//	 * Vomsify (VLET) GridProxy object and overwrite previous proxy with
//	 * vomsified proxy.
//	 */
//	public static VomsProxyCredential vomsify(GridProxy prox, VO vo,
//			long lifetimeInSeconds, boolean saveNewProxy) throws VlException
//	{
//	    if (vo==null)
//            throw new NullPointerException("VO object can not be null");  
//	    
//		logger.infoPrintf("vomsify(): Using proxy:"+prox.getProxyFilename()); 
//	
//		try
//		{
//			VomsProxyCredential vomscred = new VomsProxyCredential(prox,
//			        vo, "G/" + vo.getVoName(),
//					lifetimeInSeconds);
//			prox.setGlobusCredential(vomscred.getVomsProxy());
//
//			// save and overwrite:
//			if (saveNewProxy)
//			{
//				logger.infoPrintf("vomsify(): Saving new proxy to:"+prox.getProxyFilename()); 
//				prox.saveProxy();
//			}
//
//			return vomscred;
//		}
//		// Exception Nesting:
//		catch (Exception e)
//		{
//			throw convertException("Couldn't create VOMS proxy for:"+vo.getVoName(),e);
//		}
//	}

	public static VomsProxyCredential vomsify(GlobusCredential cred,
	        String voName,
            long lifeTimeInSeconds) throws VlException
    {
	    return vomsify(cred,voName,null,null,lifeTimeInSeconds);
    }
	
	/**
	 * Vomsify (VLET) GridProxy object and overwrite previous proxy with
	 * vomsified proxy. The VO 'voName' must be know in the registry.
	 * <p>
     * As of 1.3.0 the voCommand can be VOName/Group:VORole.
	 * Parameters voGroup and voRole are optional. 
	 * Only specify voGroup if the fullgroup name is different then the VO Name.
	 * For example: VO Name="pvier", full group name= "pvier/test"
	 * @param voName shortname for the VO, for example "pvier"
	 * @param optional VO Group name, if different then VOName, for example "pvier/subgroup" 
	 * @param optional role, for example "tester"
	 */
	public static VomsProxyCredential vomsify(GlobusCredential cred,
	        String voName,
	        String voGroup,
	        String voRole,
			long lifetimeInSeconds) throws VlException
	{
	    
		Exception except=null;
		VO vo=null; 
		
		try
		{
			vo = getVO(voName);
		}
		catch (Exception e) 
		{
			except=e; 
		}

		if (vo == null)
			throw new VlException("VOMSProxyException","VO not known or configuration missing for vo:" + voName
					+"\nPlease add VO server information to voms.xml",except);

		//String voRole=getVORole(voCommand);
		
		//logger.debugPrintf("vomsify: vo=%s, voRole=%s\n",vo.getVoName(),voRole); 
	       
		return vomsify(cred, vo, voGroup,voRole, lifetimeInSeconds);
	}

	

	/** Return list of VO names (aliases) to choose from. */
	public static String[] getVOs()
	{
		Set<String> keys = vomsRegistry.keySet();
		String names[] = new String[keys.size()];
		names = keys.toArray(names);
		return names;
	}

	/**
	 * Add VO information to the global registry.2 Overwrites previous
	 * information if already added.
	 */
	public static void addVO(String voName, String host, int port, String DN)
	{
		addVO(voName, voName, host, port, DN);
	}

	/**
	 * Add VO information to the global registry. Overwrites previous
	 * information if already added. Note that 'alias' is used to store the VO
	 * object, but 'name' is the actual VO name used when contacting the voms
	 * server. Use getVO(alias) to retrieve the VO object.
	 */
	public static void addVO(String alias, String voName, String host,
			int port, String DN)
	{
		// check voName, other fields can be set in a later stage (null is
		// allowed).
		if (voName == null)
			throw new NullPointerException("voName can not be null");

		if (alias == null)
			alias = voName;

		vomsRegistry.put(alias, new VO(voName, host, port, DN));
	}

	/** Static main for command line interaction */
	static int verboseLevel = 0;

	public static void main(String args[])
	{
		String proxyFile = null;
		String newProxyFile = null;
		// new argument array for parsed/shifted arguments
		String shiftedArgs[] = new String[args.length];
		String voname = null;

		int index = 0;
		
		String[] newArgs = Global.parseArguments(args); 
		
		for (int i = 0; i < newArgs.length; i++)
		{
			// parse double argument options:
			if ((args[i].startsWith("-") && (i + 1 < args.length)))
			{
				if (args[i].startsWith("-f"))
					proxyFile = args[i + 1];
				else if (args[i].startsWith("-o"))
					newProxyFile = args[i + 1];
				else
					// unrecognized argment
					Usage();
				
				// shift extra argument! 
				i++; 
			}
			else
				// keep argument
				shiftedArgs[index++] = args[i];
		}

		// use new args array.
		args = shiftedArgs;
		VO vo = null;

		// first argument must be vo name
		if (index > 0)
			voname = args[0];

		if (index < 4)
		{
			// non null VO name => query registry
			if (voname == null)
			{
				Usage();
			}
			else
			{
				try
				{
					vo = getVO(voname);
				}
				catch (VlException e)
				{
					System.err.println("*** Exception:"+e); 
					e.printStackTrace();
				}
				
				if (vo == null)
				{
					Exit("Unknown VO. please specify contact details for VO:" + voname, 3);
				}
			}
		}
		else
		{
			String host = args[1];
			int port=-1; 
			try
			{
				port = new Integer(args[2]);
			}
			catch (java.lang.NumberFormatException e)
			{
				Exit("Port argument is not a number:"+args[2],5);
			}
		
			String dnname = args[3];
			vo = new VO(voname, host, port, dnname);
		}

		GridProxy prox=null; 
		
		//Make sure new Grid Proxy setting are NOT saved ! 
		GlobalConfig.setUsePersistantUserConfiguration(false); 
		
		
		if (proxyFile==null)
		{
			prox = GridProxy.getDefault();

		}
		else
		{
			try
			{
				prox=GridProxy.loadFrom(proxyFile);
			}
			catch (VlException e)
			{
				if (verboseLevel>1)
				{
					System.err.println("*** Exception:"+e);
					e.printStackTrace();
				}
				Exit("*** Error loading proxy from:"+proxyFile
						+"\n"+"*** Reason:"+e.getMessage(),4); 
			} 
			
			prox.setDefaultProxyLocation(proxyFile);
		}

		Message(1,"Using proxyfile:"+prox.getProxyFilename());
		VomsProxyCredential vomsCred = null;
		
		GlobusCredentialWrapper credWrapper = (GlobusCredentialWrapper)prox.getCredential(GridProxy.GLOBUS_CREDENTIAL_TYPE);
		
		try
		{
			vomsCred = vomsify(credWrapper.getGlobusCredential(), vo, null,null,prox.getTimeLeft());
		}
		catch (Exception e)
		{
			System.err.println("Exception:" + e);
			e.printStackTrace();
			System.exit(1);
		}

		if (prox.isValid() == false)
		{
			Exit("Error: Invalid proxy. Please create valid proxy first", 2);
		}
		// 0 = important message 
		// 1 = informational messages
		// 2 = detailed information 
		// 3+ = debugging 
		
		if (verboseLevel >= 1)
		{
			VomsInfo info=null;
			
			try
			{
				info = vomsCred.getVomsInfo();
			}
			catch (Exception e)
			{
				System.err.println("Exception:"+e); 
				e.printStackTrace(); 
			}
			
			if (info!=null)
			{
				for (Enumeration<String> keys = info.keys(); keys.hasMoreElements();)
				{
					String key=keys.nextElement();
					Message(1,"vomsinfo."+key+"="+info.get(key));
				}
			}
			else
			{
				System.err.println("***Error: NULL voms info:");
			}

		}
		// all ok ?
		Exit(0);
	}

	private static void Exit(String msg, int val)
	{
		System.err.println(msg);
		System.exit(val);
	}

	private static void Exit(int val)
	{
		System.exit(val);
	}

	private static void Usage()
	{
		System.err.println("usage: <voname> [<host> <port>  <serverdn>]  [-f[ile] proxy] [-o[ut] newproxy");
		System.exit(1);
	}
	
	public static void Message(int vLev, String msg)
	{
		if (verboseLevel>=vLev)
			System.out.println(msg); 
	}
	

    /** 
     * read from vomx.xml file and return vo object. 
     * @param vomsFilename    path of voms.xml file, if null read from "/etc/grid-security/vomsdir/voms.xml".
     * @param voName          the name of the Virtual Organisation.
     * @throws VlException 
     */
    public static VO readFromXML(String vomsFilename,String voName) throws VlException
    {
        // normalize path to URI syntax.  
        vomsFilename=VRL.uripath(vomsFilename); 
        
        if (vomsFilename==null)
        { 
            vomsFilename="/etc/grid-security/vomsdir/voms.xml"; 
        }

        File vomsXMLFile=new File(vomsFilename);

        if (vomsXMLFile.exists()==false)
        {
            throw new ResourceNotFoundException("voms.xml not specified or file does not exist:"+vomsFilename); 
        }       

        // this is a VERY IMPORTANT line - otherwise the DN cannot match against the C client that uses openssl
        org.bouncycastle.asn1.x509.X509Name.DefaultSymbols.put( org.bouncycastle.asn1.x509.X509Name.EmailAddress , "Email" ) ;
        org.bouncycastle.asn1.x509.X509Name.RFC2253Symbols.put( org.bouncycastle.asn1.x509.X509Name.EmailAddress , "Email" ) ;
        org.bouncycastle.asn1.x509.X509Name.DefaultLookUp.put( "Email" , org.bouncycastle.asn1.x509.X509Name.E ) ;

        logger.infoPrintf("parsing voms.xml file:%s\n",vomsXMLFile); 
        
        try
        {

            URL vomsXMLURL = vomsXMLFile.toURI().toURL() ;

            InputStream xmlStream = vomsXMLURL.openStream() ;

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance() ;
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder() ;
            Document doc = docBuilder.parse( xmlStream ) ;

            // normalize text representation
            doc.getDocumentElement().normalize() ;

            NodeList listOfVOs = doc.getElementsByTagName( "vo" ) ;

            int numberOfVOs = listOfVOs.getLength() ;
            
            logger.infoPrintf("parsing total nr. of VOs:%d",numberOfVOs); 
            
            for( int v = 0 ; v < numberOfVOs ; v ++ )
            {

                Node voNode = listOfVOs.item( v ) ;

                Element voElement = (Element)voNode ;

                // name - only one
                NodeList voNameList = voElement.getElementsByTagName( "name" ) ;
                Element nameElement = (Element)voNameList.item( 0 ) ;
                NodeList text = nameElement.getChildNodes() ;
                String name = (String)( (Node)text.item(0) ).getNodeValue().trim() ;

                logger.infoPrintf("Reading voname=%s\n",name); 

                if( name.equals( voName ) )
                {
                    VO vo=new VO(name); //protected constructor; 

                    //this.name = name ;
                    // this.known = true ;

                    // admin - only one
                    NodeList voAdminList = voElement.getElementsByTagName( "admin" ) ;
                    Element adminElement = (Element)voAdminList.item( 0 ) ;
                    text = adminElement.getChildNodes() ;
                    vo.setAdmin(  (String)( (Node)text.item(0) ).getNodeValue().trim() );

                    NodeList voServersList = voElement.getElementsByTagName( "server" ) ;

                    int nrOfservers = voServersList.getLength() ;
                    VOServer[] voServers = new VOServer[nrOfservers];
                    

                    logger.infoPrintf(" - nrOfServers=%d\n",nrOfservers); 
                    
                    for( int i = 0 ; i < nrOfservers ; i ++ )
                    {
  
                        
                        Node serverNode = voServersList.item( i ) ;

                        Element serverElement = (Element)serverNode ;

                        // server name
                        NodeList list = serverElement.getElementsByTagName( "name" ) ;
                        Element element = (Element)list.item( 0 ) ;
                        text = element.getChildNodes() ;
                        String host = (String)( (Node)text.item(0) ).getNodeValue().trim() ;

                        // server port
                        list = serverElement.getElementsByTagName( "port" ) ;
                        element = (Element)list.item( 0 ) ;
                        text = element.getChildNodes() ;
                        int port = Integer.parseInt( (String)( (Node)text.item(0) ).getNodeValue().trim() ) ;

                        // server cert
                        list = serverElement.getElementsByTagName( "cert" ) ;
                        element = (Element)list.item( 0 ) ;
                        text = element.getChildNodes() ;
                        String certFile = (String)( (Node)text.item(0) ).getNodeValue().trim() ;

                        // server dn
                        list = serverElement.getElementsByTagName( "dn" ) ;
                        element = (Element)list.item( 0 ) ;
                        text = element.getChildNodes() ;
                        String hostDN = (String)( (Node)text.item(0) ).getNodeValue().trim() ;

                        voServers[i]=new VOServer(host,port,certFile,hostDN); 
                        
                        logger.infoPrintf(" - VO server=%s:%d\n",host,port);  
                        logger.infoPrintf(" - VO     DN=%s\n",hostDN);  
                         

                    } // end of server tag loop
                    vo.setServers(voServers); 
                    logger.infoPrintf("Returning VO=%s\n",vo);
                    // keep filename; 
                    vo.setVOMSXmlFile(vomsFilename);  
                    
                    return vo;

                } // end of vo test

                // skip other VOs ? 
                
            } // end of vo tag loop

            return null; 
        }
        catch (ParserConfigurationException e)
        {
            logger.debugPrintf("Exception when reading from file:%s\n",vomsFilename); 
            throw new VlException("Parse Error","Couldn't parse:"+vomsFilename,e); 
        }
        catch (IOException e)
        {
            logger.debugPrintf("Exception when reading from file:%s\n",vomsFilename); 
            throw new VlException("Read Error","Couldn't read file:"+vomsFilename,e); 
        }
        catch (SAXException e)
        {
            logger.debugPrintf("Exception when reading from file:%s\n",vomsFilename); 
            throw new VlException("Parse Error","Couldn't parse:"+vomsFilename,e); 
        }
        // others: 
        catch(Exception e )
        {
            logger.debugPrintf("Exception when reading from file:%s\n",vomsFilename); 
            throw new VlException("VOMS Exception",e.getMessage(),e); 
        }

    }
    
    /**
     * Returns VO information from registry or NULL. Doesn't do any checking.
     * voName can be VO name or an alias used to store the VO object.
     * Current search order is:
     * <pre>
     * - $HOME/.globus/vomsdir/voms.xml          
     * - VLET_INSTALL/etc/vomsdir/voms.xml
     * - /etc/grid-security/vomsdir/voms.xml
     * </pre>
     * When the VO information is found, it is assumed the parent
     * directory of that file is the 'vomsdir' which also must
     * contain the server certificate specified as 'cert' in the voms.xml file. 
     * 
     * @throws VlException 
     */
    public static VO getVO(String voCommand) throws VlException
    {
        // strip optional command: 
        String voName=getVOName(voCommand);
        // String voRole=getVOName(voCommand); 
        logger.debugPrintf("getVO: voCommand = '%s' =>  voName='%s'\n'",voCommand,voName); 
        
        VO vo=null;
        // Always reread file, do not check registry to allow
        // for runtime edition of the voms.xml file. 
        // Else the vbrowser has to be restarted each time 
        // the voms.xml file changes ! 
        //  ---
        //  vo=vomsRegistry.get(voName);
        //  if (vo!=null) 
        //     return vo;
        //  ---
         
        
        // === 
        // search and read voms.xml information . 
        // === 
        // search path: 
        // - user first : $HOME/.vletrc/voms.xml
        // - globus     : $HOME/.globus/vomsdir/voms.xml          
        // - vlet installation : VLET_INSTALL/etc/vomsdir/voms.xml
        // - system installation /etc/grid-security/vomsdir/voms.xml
        
        // get local search paths! 
        String searchPaths[]=
            {
                "/etc/grid-security/vomsdir/voms.xml",
                Global.getInstallationConfigDir().append("vomsdir/voms.xml").getPath(),
                // skip .vletrc/vomsdir/voms.xml for now 
                Global.getUserHomeLocation().append(".globus/vomsdir/voms.xml").getPath(),
                null// null = default location.  
            };
        
        VlException except=null; 
        
        for (String path:searchPaths)
        {
            try
            {
                vo=readFromXML(path, voName);
                if (vo!=null)
                {
                    // store:   
                    vomsRegistry.put(vo.getVoName(),vo); 
                    return vo; 
                }
                
            }
            catch (ResourceNotFoundException e)
            {
                // allowed: file does not exist. 
                logger.infoPrintf("File does not exists:%s\n",path); 
                vo=null;
            }
            catch (VlException e)
            {
                except=e;
                logger.logException(ClassLogger.ERROR,e,"Failed to read path:%s\n",path); 
                
                Throwable cause=null; 
                
                if ((cause=e.getCause())!=null)
                    logger.logException(ClassLogger.ERROR,cause,"cause=%s\n",cause); 
                
            }
        }
        
        // if no vo information could be found and there was an exception
        // inform caller about the (last encountered) exception.
        // Ignore Exceptions if valid 
        // VO information was found. 
        
        if ((vo==null) && (except!=null)) 
            throw except; 
        
        return null; 
    }
    
    /**
     * Strip name from full VO Command which can have an VORole appended. 
     * 
     * @param voCommand  VOName[/voGroup]:VORole
     * @return VOName part 
     */
    public static String getVOName(String voCommand)
    {
         if (voCommand==null)
             return null;
         
         String strs[]=voCommand.split(":");
         
         if ((strs==null) || (strs.length<=0))
             return voCommand;

         // split optional VOName/VOGroup 
         String subStrs[]=strs[0].split("/");
         
         if (subStrs==null)
             return strs[0];
         
         return subStrs[0]; 
    }
    
    /**
     * Strip name from full VO Command which can have an VORole appended. 
     * 
     * @param voCommand  VOName/VORole
     * @return VORole part 
     */
    public static String getVORole(String voCommand)
    {
        if (voCommand==null)
            return null;
        
        String strs[]=voCommand.split(":");
        
        if ((strs==null) || (strs.length<=1))
            return null; 
        
        return strs[1]; 
    }
    
    public static String parse(X509Certificate certs[] ) throws Exception
    {
    	StringBuilder log=new StringBuilder(); 
    	log.append("> Started parsing #"+certs.length+" certificates\n"); 
    	
        for(int i = 0; i < certs.length; i++)
        {
        	log.append("> ["+i+"]: --- Certificate Dump ---<\n");
        	logAppend(log,certs[i]); 
        	log.append("> ["+i+"]: --- END Certificate Dump ---<\n");
        	
        	ArrayList<AttributeCertificate> vomsACs = extractVOMSACs(certs); 
            
            if (vomsACs == null)
            {
            	log.append("> ["+i+"]: *** No VOMS extension in certificate\n"); 
            	continue; 
            }
            else
            {
            	log.append("> ["+i+"]: VOMS extension founds:"+vomsACs.size()+". Information following:\n"); 
            }
            
            for (int j=0;j<vomsACs.size();j++)
            {
            	VOMSAttributeCertificate vomsAC=new VOMSAttributeCertificate(vomsACs.get(j)); 
            	
            	ArrayList<String> strs = vomsAC.getVOMSFQANs();
            	
            	log.append("> ["+i+"]: - VOMS AC["+j+"] = {"+new StringList(strs).toString("'",",")+"}\n"); 
        	}
        }
        
        return log.toString(); 
    }

	private static void logAppend(StringBuilder log, X509Certificate cert) 
	{
		log.append(" - Type             :"+cert.getType()
			     + "\n - Version          :"+cert.getVersion()
				 + "\n - Issuer dn        :"+cert.getIssuerDN()
				 + "\n - Issuer unique id :"+cert.getIssuerUniqueID()
				 + "\n - Not before       :"+cert.getNotBefore()
				 + "\n - Not after        :"+cert.getNotAfter()
				 + "\n - Subject dn       :"+cert.getSubjectDN());  
		
		Set<String> oids1 = cert.getCriticalExtensionOIDs();
		
		log.append("\n - Criticial Oids   :{"+toString(oids1,",")+"}"); 
		
		Set<String> oids2 = cert.getNonCriticalExtensionOIDs();
		log.append("\n - Non Critical Oids:{"+toString(oids2,",")+"}");  
		log.append("\n");  
	}
	
	private static String toString(Set<String> set,String sepString)
	{
		if (set==null)
			return "";
		StringList list=new StringList(set);
		return list.toString(sepString);
	}
	
	/**
	 * Static method that returns all included AttributesCertificates of a
	 * GlobusCredential. In general we are only interested in the first one.
	 * 
	 * @param vomsProxy
	 *            the voms enabled proxy credential
	 * @return all AttributeCertificates
	 */
	public static ArrayList<AttributeCertificate> extractVOMSACs(X509Certificate[] x509s)
	{

		// the aim of this is to retrieve all VOMS ACs
		ArrayList<AttributeCertificate> acArrayList = new ArrayList<AttributeCertificate>();

		for (int x = 0; x < x509s.length; x++)
		{
			logger.debugPrintf(" - Checking certificate["+x+"]\n" );
			
			try
			{

				byte[] payload = x509s[x].getExtensionValue(VomsUtil.CERT_VOMS_EXTENSION_OID); 
				if (payload==null)
				{
					logger.debugPrintf(" - #%d: No VOMS AC extension.\n",x);
					continue; 
				}
				else
					logger.debugPrintf(" - #d: Found VOMS AC extension.\n",x); 
								
				// Octet String encapsulation - see RFC 3280 section 4.1
				payload = ((ASN1OctetString) new ASN1InputStream(
						new ByteArrayInputStream(payload)).readObject())
						.getOctets();

				ASN1Sequence acSequence = (ASN1Sequence) new ASN1InputStream(
						new ByteArrayInputStream(payload)).readObject();

				for (Enumeration e1 = acSequence.getObjects(); e1
						.hasMoreElements();)
				{

					ASN1Sequence seq2 = (ASN1Sequence) e1.nextElement();

					for (Enumeration e2 = seq2.getObjects(); e2
							.hasMoreElements();)
					{
						AttributeCertificate ac = new AttributeCertificate(
								(ASN1Sequence) e2.nextElement());

						acArrayList.add(ac);
					}
				}
			}
			catch (Exception pe)
			{
				logger.logException(ClassLogger.DEBUG,pe," - #%d: This part of the chain has no AC\n",x);
			}
		}

		return acArrayList;
	}
    
}
