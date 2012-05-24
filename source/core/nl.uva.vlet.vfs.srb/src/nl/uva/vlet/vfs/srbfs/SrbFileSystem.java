/*
 * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: SrbFileSystem.java,v 1.4 2011-11-25 13:43:43 ptdeboer Exp $  
 * $Date: 2011-11-25 13:43:43 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PASSIVE_MODE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_USERNAME;
import static nl.uva.vlet.vfs.srbfs.SrbConfig.ATTR_DEFAULTRESOURCE;
import static nl.uva.vlet.vfs.srbfs.SrbConfig.ATTR_MCATZONE;
import static nl.uva.vlet.vfs.srbfs.SrbConfig.ATTR_MDASCOLLECTIONHOME;
import static nl.uva.vlet.vfs.srbfs.SrbConfig.ATTR_MDASDOMAINHOME;
import static nl.uva.vlet.vfs.srbfs.SrbConfig.ATTR_MDASDOMAINNAME;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlAuthenticationException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VlServerException;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.UserMetaData;
import edu.sdsc.grid.io.local.LocalFile;
import edu.sdsc.grid.io.srb.SRBAccount;
import edu.sdsc.grid.io.srb.SRBException;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;
import edu.sdsc.grid.io.srb.SRBMetaDataSet;

/**
 * This class represents an SRB FileSystem.
 *   
 * @author P.T. de Boer
 */

public class SrbFileSystem extends FileSystemNode 
{
	// =========================================================================
	// Class
	// =========================================================================


	//private static Hashtable<String,SRBServer> servers=new Hashtable<String,SRBServer>(); 

	public static final String ACCESS_READ="read";
	public static final String ACCESS_WRITE="write";
	public static final String ACCESS_ANNOTATE="annotate";
	public static final String ACCESS_ALL="all";
	public static final String ACCESS_NONE="none";

	public static final String accesContraints[]=
	{
		ACCESS_READ,ACCESS_WRITE,ACCESS_ANNOTATE,ACCESS_ALL,ACCESS_NONE 
	};


	// =========================================================================
	// Instance 
	// =========================================================================


	/** SRB Information object which holds all the (default) data for this server */ 
	//private SRBServerInfo srbInfo=null;

	private SRBFileSystem srbFS=null; // already authentication srb fs for speed ! 

	private SRBAccount srbAccount;

	private String srbServerID; 

	public SrbFileSystem(VRSContext context,ServerInfo srbInfo) throws VlException
	{
		super(context, srbInfo);

		init(srbInfo);
	}

	private void init(ServerInfo srbInfo) throws VlException
	{

		String host=srbInfo.getHostname();

		if ((host==null) ||  (host.compareTo("")==0))
		{
			throw new VlServerException("Hostname is not specified");
		}

		int port=srbInfo.getPort(); 

		if (port<=0) 
		{
			// no default port available
			throw new VlServerException("Invalid port number");
		}

		try
		{
			// jargon wil decrease version when not compatible

			try
			{
				SRBAccount.setVersion(SRBAccount.SRB_VERSION_3_4); 
			}
			catch (Exception e)
			{
				Global.errorPrintln(this,"Exception when setting version:"+e);
			}

			// create new account object 
			srbAccount= SrbFileSystem.createSRBAccount(this.vrsContext,srbInfo);

			connect(); 

			int portRange[]=Global.getIncomingFirewallPortRange(); 

			if (portRange!=null)
			{
				Debug("Firewall Portrange="+portRange[0]+"-"+portRange[1]);
				srbFS.setFirewallPorts(portRange[0],portRange[1]);
			}

		}
		// analyse exceptions for better feedback then Jargon does :-(
		catch (NullPointerException e)
		{
			// Error 
			throw new VlException("SRB NullPointerException ",e.getMessage(),e); 
		}

		catch (java.lang.SecurityException e)
		{
			// Check ? 
			throw new VlAuthenticationException("SecurityException\n"+e.getMessage(), e);
		}
		catch (Exception e)
		{
			throw convertException(e); 
		}
		// catch VlException -> let pass:
	}

	public void connect() throws VlException 
	{
		try
		{
			srbFS = new SRBFileSystem(srbAccount);
			Debug("connected to SRB FS:"+srbFS);
		}
		catch (SRBException e)
		{
			throw convertException(e); 

		}
		catch (IOException e)
		{
			// try to figure out/ 
			//  if (serverInfo.useGSIAuth()==false) is also true for GSI authentication
			{
				// sadly when an password authentication fails, SRB throws this error: 

				String msgstr=e.getMessage();

				if (msgstr.startsWith("read() -- couldn't read complete packet"))              
				{
					// error during connect: 
					getServerInfo().setHasValidAuthentication(false);

					// Piter Says:
					throw new VlAuthenticationException(
							"IOException occured when setting up connection to remote server.\n"
							+"Remote server closed connection without a reason.\n" 
							+"This could be an authentication failure.\n"
							+"Original error message="+e.getMessage(),e); 
				}
			}
			throw new VlIOException(e.getMessage(), e);
		}
	}



	public void disconnect() throws VlException 
	{
		if (srbFS==null)
			return; 

		try
		{
			srbFS.close();
			Debug("disconnected to SRB FS:"+srbFS);
			srbFS=null;
		}
		catch (IOException e) 
		{
			srbFS=null; // nullify !
			throw convertException(e);  
		}
	}

	public void setFirewallPorts(int min,int max) 
	{
		this.srbFS.setFirewallPorts(min,max); 
	}

	public boolean isConnected()
	{
		if (srbFS==null) 
			return false; 

		return srbFS.isConnected();
	}

	public String getHomeDirectory()
	{
		return this.srbFS.getHomeDirectory();
	}

	/** Returns the SRBFileSystem object of this SRB Server */ 
	public SRBFileSystem getSRBFileSystem()
	{
		return this.srbFS;
	}


	// =========================================================================
	// Class
	// =========================================================================


	// todo: better integration with ServerInfo 
	private static SRBAccount createSRBAccount(VRSContext context, ServerInfo srbInfo) throws VlAuthenticationException
	{
		//try
		{
			SRBAccount acc=null;

			// Get Context GridProxy:
			GridProxy proxy = context.getGridProxy();


			Debug("srbUser       ="+srbInfo.getProperty(ATTR_USERNAME));
			Debug("srbDomainHome ="+srbInfo.getProperty(ATTR_MDASDOMAINHOME));
			Debug("srbDomainName ="+srbInfo.getProperty(ATTR_MDASDOMAINNAME));

			// SDSC SRBAcount info object:
			acc = new SRBAccount(srbInfo.getProperty(ATTR_HOSTNAME),
					srbInfo.getIntProperty(ATTR_PORT,50000),
					srbInfo.getProperty(ATTR_USERNAME),
					(String)null, // passwd
					srbInfo.getProperty(ATTR_MDASDOMAINHOME),
					srbInfo.getProperty(ATTR_MDASDOMAINNAME),
					srbInfo.getProperty(ATTR_DEFAULTRESOURCE),
					srbInfo.getProperty(ATTR_MCATZONE)
			);

			acc.setHomeDirectory(srbInfo.getProperty(ATTR_MDASCOLLECTIONHOME));

			if (srbInfo.useGSIAuth())
			{
				if (proxy.isValid()==false)
				{
					// must have valid proxy for GSI authentication
					throw new VlAuthenticationException("Invalid Grid PRoxy"); 
				}

				// use GSI authentication
				acc.setOptions(SRBAccount.GSI_AUTH);
				// use grid proxy 
				String pfname = proxy.getProxyFilename();
				acc.setPassword(pfname);
				//System.err.println("proxy path="+pfname); 
				Debug("using proxy file="+pfname);
			}
			else
			{
				// use (Encrypted) Password authentication 
				acc.setOptions(getAuthSchemeInt(srbInfo));
				String passwd=srbInfo.getPassword(); 

				if ((passwd==null) || passwd.compareTo("")==0)  
				{
					throw new VlAuthenticationException("Must supply password");
				}

				acc.setPassword(passwd); 
			}

			return acc; 
		}

	}

	/** Return SRBAccount compatible constant  */

	private static int getAuthSchemeInt(ServerInfo info)
	{
		String str=info.getAuthScheme();

		if (str.compareTo(ServerInfo.PASSWORD_AUTH)==0) 
			return SRBAccount.ENCRYPT1;

		if (str.compareTo(ServerInfo.GSI_AUTH)==0) 
			return SRBAccount.GSI_AUTH;

		// default to: 
			return SRBAccount.GSI_AUTH;
	}

	public static void Debug(String msg)
	{
		//Global.errorPrintln(SRBServer.class,msg); 
		Global.debugPrintln(SrbFileSystem.class,msg);
	}

	private static Object newServerMutex=new Object(); 

	public static SrbFileSystem createServerFor(VRSContext context,ServerInfo srbInfo) throws VlException
	{
		Global.debugPrintln(SrbFileSystem.class,"\n---\ncreateServerFor(): Using serverInfo:"+srbInfo); 

		String serverid=createServerID(context,srbInfo); 

		synchronized (newServerMutex)
		{
			SrbFileSystem server = (SrbFileSystem)context.getServerInstance(serverid,SrbFileSystem.class); 

			if (server==null)
			{
				// create new server
				server=new SrbFileSystem(context,srbInfo);

				// use CUSTOM SRB ID 
				server.setID(serverid); 

				// Hashtable is 
				//servers.put(serverid,server); 
				context.putServerInstance(server); 
			}

			return server;
		}
	}



	private static String createServerID(VRSContext context,ServerInfo info)
	{
		String user=info.getUsername();
		String domain=info.getAttributeValue(ATTR_MDASDOMAINNAME); 
		String host=info.getHostname();

		// hack: to support multiple default resources, 
		// just create a server per defaultResource
		// This paremeter should be set at the SRB Filesystem level, 
		// but for now is it used to specify a SRB Server too. 

		String defaultResource=info.getAttributeValue(ATTR_DEFAULTRESOURCE);  
		int port=info.getPort();

		// Service Security: add User Subject as well :
		return "srb:"+user+"@"+host+":"+port+"/"+defaultResource; 
	}

	// ===
	// ACL interface 
	// === 

	public VAttribute[][] getACL(SRBFile srbnode,boolean isDir) throws VlIOException 
	{
		return getACL(srbnode,true,isDir); 
	}

	public VAttribute[][] getACL(SRBFile srbnode, boolean allUsers,boolean isDir) throws VlIOException 
	{
		MetaDataRecordList[] acl=null;

		try
		{
			/*
            // My Code: 
			 ******************************************************************

        	String path=srbnode.getAbsolutePath(); 
        	String dirname=VRL.dirname(path);
    		MetaDataCondition userCond=null; 

        	if (isDir)
        	{

   			    MetaDataSelect[] selects = {
   			    		MetaDataSet.newSelection(SRBMetaDataSet.USER_NAME)
     	       			,MetaDataSet.newSelection(SRBMetaDataSet.USER_DOMAIN)
     	       			,MetaDataSet.newSelection(SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT)
     	       			//
      					// Strange: Select ACCESS_DIRECTORY_NAME as well to avoid duplicates! 
     	       			//
      					,MetaDataSet.newSelection(SRBMetaDataSet.ACCESS_DIRECTORY_NAME)
      					//,MetaDataSet.newSelection(SRBMetaDataSet.ZONE_NAME)  
      					// ,MetaDataSet.newSelection(SRBMetaDataSet.FILE_NAME)   					 
   			    	};

     			// MetaDataCondition[] conditions = {
   				//	   MetaDataSet.newCondition(SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL,"piter.de.boer" ),
   			    //	};

   			    if (allUsers==true)
   			    {
   			    	MetaDataCondition[] conditions = 
   			    	{
     					 MetaDataSet.newCondition(SRBMetaDataSet.ACCESS_DIRECTORY_NAME, MetaDataCondition.EQUAL,path )
     					 // ,MetaDataSet.newCondition(SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,dirname )  
   			    	};

   			    	acl = srbnode.query(conditions,selects);
   			    }
   			    else // single user 
   			    {
   			    	MetaDataCondition[] conditions = 
   			    	{
     					MetaDataSet.newCondition(SRBMetaDataSet.ACCESS_DIRECTORY_NAME, MetaDataCondition.EQUAL,path )
     					,MetaDataSet.newCondition(SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL,this.srbAccount.getUserName())
                        //,MetaDataSet.newCondition(SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,dirname )  
   			    	};

   			    	acl = srbnode.query(conditions,selects);
   			    }
        	}
        	else
        	{
        	    MetaDataSelect[] selects = {
   			    		MetaDataSet.newSelection(SRBMetaDataSet.USER_NAME)
     	       			,MetaDataSet.newSelection(SRBMetaDataSet.USER_DOMAIN)
     	       			,MetaDataSet.newSelection(SRBMetaDataSet.ACCESS_CONSTRAINT)
     	       			//
      					// Strange: Select ACCESS_DIRECTORY_NAME as well to avoid duplicates! 
     	       			//
      					//,MetaDataSet.newSelection(SRBMetaDataSet.ACCESS_DIRECTORY_NAME)
      					//,MetaDataSet.newSelection(SRBMetaDataSet.ZONE_NAME)  
      					// ,MetaDataSet.newSelection(SRBMetaDataSet.FILE_NAME)   					 
   			    	};

     			// MetaDataCondition[] conditions = {
   				//	   MetaDataSet.newCondition(SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL,"piter.de.boer" ),
   			    //	};



     			if (allUsers==false)
     			{
     				MetaDataCondition[] conditions = 
     					{
     						MetaDataSet.newCondition(SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL,this.srbAccount.getUserName()) 
     						//domainCond, 
     						//MetaDataSet.newCondition(SRBMetaDataSet.ACCESS_DIRECTORY_NAME, MetaDataCondition.EQUAL,path )
     						// ,MetaDataSet.newCondition(SRBMetaDataSet.PARENT_DIRECTORY_NAME, MetaDataCondition.EQUAL,dirname )
     					};

     				acl = srbnode.query(conditions,selects);
     			}
     			else
     			{
     				acl = srbnode.query(selects);
     			}
        	}
			 ******************************************************************
			 */ 
			// Code from new Jargon (which now as tested on 22-08-2007 works :-/) 

			acl=srbnode.getPermissions(allUsers);

			// copy of code: 
			// acl=this.getPermissions(srbnode, isDir, allUsers);

		}
		catch (IOException e)
		{
			throw new VlIOException(e); 
		}



		if (acl==null) 
		{
			Global.warnPrintln(SrbFileSystem.class,"NULL ACL for:"+srbnode);
			Debug("NULL ACL for:"+srbnode);
			return null;
		} 

		int nrEntities=acl.length; 

		VAttribute attrs[][]=new VAttribute[nrEntities][]; 


		for (int i=0;i<nrEntities;i++)
		{
			int nrfields=acl[i].getFieldCount();

			// truncate to first 3 fields (user,group and access) 
			if (nrfields>3)
				nrfields=3; 

			attrs[i]=new VAttribute[nrfields];
			int accesIndex=0; 

			for (int j=0;j<nrfields;j++)
			{
				String fieldName=acl[i].getFieldName(j); 
				Object value=acl[i].getValue(j);

				Debug("acl["+i+"]."+fieldName+"="+value);


				VAttribute attr=null; 
				String valueStr=value.toString();
				//String attrName=MetaData.metaDataName2AttributeName(fieldName);
				//if (attrName==null) 
				//     attrName=fieldName;

				// MetaData Returned for dir: USER_NAME,DOMAIN_NAME,DIRECTORY_ACCESS_CONTRAINT
				// MetaData Returned for file: USER_NAME,DOMAIN_NAME,ACCESS_CONTRAINT
				// optional ACCESS_DIRECTORY_NAME which should be 4th or higher field ! 

				if ( (fieldName.compareTo(SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT)==0)
						|| (fieldName.compareTo(SRBMetaDataSet.ACCESS_CONSTRAINT)==0) )
				{
					// convert accces constraint to enum type 
					attr=new VAttribute(fieldName,accesContraints,valueStr);
					attr.setEditable(true); 
					accesIndex=j; 
				}
				else
				{
					attr= new VAttribute(fieldName,valueStr);
					attr.setEditable(false); 
				} 

				attrs[i][j]=attr; 
			}

			// hack, ACCESS_CONTRAINT should be 3rd row not 2d row. 

			if (accesIndex==1) 
			{
				// swap attributes: 
				VAttribute attr=attrs[i][2]; 
				attrs[i][2]=attrs[i][1];
				attrs[i][1]=attr; 
			}
		}

		return attrs; 

	}

	/**
	 * Return list of possible ACL entities (user/group,etc) as VAttribute Array
	 * Entity name is the VAttribyte name
	 *  
	 * @throws VlIOException
	 */  

	public static VAttribute[] getACLEntities(SRBFileSystem srbfs) throws VlIOException
	{
		String fields[]=new String[2]; 
		// query ever user which has a domain
		fields[0]=SRBMetaDataSet.USER_NAME;  
		fields[1]=SRBMetaDataSet.USER_DOMAIN;  

		VAttributeSet[] sets = SrbQuery.simpleQuery(srbfs,fields);  

		if (sets==null) 
			return null;

		int numSets=sets.length; 

		VAttribute entities[]=new VAttribute[numSets];

		Debug("getACLEntity:numSets="+numSets); 

		for (int i=0;i<numSets;i++)
		{
			VAttribute attr=sets[i].get(fields[0]); 

			entities[i]=attr.duplicate();  
		}


		return entities;    
	}

	public static VAttribute[] createACLRecord(SRBFileSystem srbfs,VAttribute entity,boolean isDir) throws VlIOException 
	{
		VAttribute attrs[]=new VAttribute[3]; 
		attrs[0]=new VAttribute(entity); 
		attrs[1]=getUserDomainAttribute(srbfs,entity.getStringValue());

		if (isDir)
		{
			attrs[2]=new VAttribute(
					SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT,
					accesContraints,
					"none");
		}
		else
		{
			attrs[2]=new VAttribute(
					SRBMetaDataSet.ACCESS_CONSTRAINT,
					accesContraints,
					"none");
		}

		attrs[2].setEditable(true); 

		return attrs;     
	}

	public static String getUserDomain(SRBFileSystem srbfs,String user) throws VlIOException
	{
		VAttribute attr=getUserDomainAttribute(srbfs,user);

		if (attr!=null) 
			return attr.getStringValue(); 

		return null; 

	}
	public static VAttribute getUserDomainAttribute(SRBFileSystem srbfs,String user) throws VlIOException
	{
		Debug("Getting Domain Attribute for user:"+user); 

		String fields[]=new String[2];
		String conds[][]=new String[1][]; 
		conds[0]=new String[2]; 

		fields[0]=SRBMetaDataSet.USER_NAME; 
		fields[1]=SRBMetaDataSet.USER_DOMAIN; 


		conds[0][0]=SRBMetaDataSet.USER_NAME; ;
		conds[0][1]=user; 

		VAttributeSet[] sets = SrbQuery.simpleQuery(srbfs,fields,conds);

		if (sets==null)
		{
			Debug("GetUserDomain query resulting in NULL entries");
			return null; 
		}

		if (sets.length>1) 
		{
			Global.errorPrintln("SRBServer","Warning: GetUserDomain query resulting multiple entries for user:"+user);

		} 

		VAttribute domainAttr=sets[0].get(SRBMetaDataSet.USER_DOMAIN); 

		Debug("Domain Attribute="+domainAttr); 
		return domainAttr; 

	}

	public void setACL(SRBFile file, VAttribute[][] acl, boolean isDir) throws VlException
	{
		// SRBQuery.setMetaData(file,acl);
		if ((acl==null) || (file==null))  
			return;

		// the SRB interface is not consistent here. 
		// getACL returns a metadata list, but 
		// to set permissions, changePermissions must be used.
		//
		//   o.O 
		//  


		int numRecords=acl.length; 

		for (int i=0;i<numRecords;i++)
		{
			VAttributeSet set=new VAttributeSet(acl[i]);
			String accStr=null; 
			Boolean recursive=false; 

			if (isDir==false)
			{
				accStr=set.getStringValue(SRBMetaDataSet.ACCESS_CONSTRAINT);
			}
			else
			{
				accStr=set.getStringValue(SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT);
			}
			// recursive=set.getBooleanValue(VAttribute.ATTR_RECURSIVE,recursive); 
			/*
			 * Changing the permissions on a file.
			 * null = none, "r" = read, "w" = write, "rw" = all
			 * Equivalent to Schmod in the SRB Scommands command-line tools.
			 */

			String permStr="";

			if (accStr.compareTo(ACCESS_READ)==0)
				permStr="r";
			else if (accStr.compareTo(ACCESS_ANNOTATE)==0)
				permStr="t";
			else if (accStr.compareTo(ACCESS_WRITE)==0)
				permStr="w";
			else if (accStr.compareTo(ACCESS_ALL)==0)
				permStr="rw";
			else if (accStr.compareTo(ACCESS_NONE)==0)
				permStr="n";

			String userName=set.getStringValue(SRBMetaDataSet.USER_NAME); 
			String domainName=set.getStringValue(SRBMetaDataSet.USER_DOMAIN);

			Debug("setting permission of:"+file.getPath()+" user="+userName+","
					+" domainName="+domainName
					+" perm="+permStr); 

			// Piter Says: 
			// when the owner's permission is set to "none", the file
			// will disappear from the user's directory and cannot 
			// be recovered again. 

			if (userName.compareTo(srbAccount.getUserName())==0)
			{
				// Kamel Exception ? 
				if (accStr.compareTo(ACCESS_NONE)==0)
					throw new VlException("Illegal Value Exception","Can not set owner's permissions to 'none'. This will make the file invisible.");
			}

			try
			{ 
				// Jargon 1.5: file.changePermissions(permStr,userName,domainName,recursive); */ 
				file.changePermissions(permStr,userName,domainName);
			}
			catch (IOException e)
			{
				throw new VlIOException(e);
			}
		}

	}

	/**
	 * To delete an ACL entry, just set the permissions to "n" which means "none".
	 */ 

	public static boolean deleteACLEntry(SRBFile file, VAttribute entity) throws VlIOException
	{
		String userName=entity.getName(); 
		String domainName=getUserDomain((SRBFileSystem)file.getFileSystem(),userName);

		Debug("deleting permission of:"+file.getPath()+" user="+userName+","
				+" domainName="+domainName
				+" perm= n");

		try
		{
			file.changePermissions("n",userName,domainName);

			// currenlty NO WAY to check whether it succeeded ! 

			return true; 
		}
		catch (IOException e)
		{
			throw new VlIOException(e);
		}
	}

	public int getNrACLEntries(SRBFile srbnode,boolean isDir) throws VlIOException
	{
		VAttribute[][] acl = getACL(srbnode,isDir); 

		if (acl==null)
			return 0;

		return acl.length;

	}

	public boolean getUsePassiveMode()
	{
		// Check Global System property AND Context property
		boolean val=vrsContext.getBoolProperty(ATTR_PASSIVE_MODE,false);

		// Global passiveMode==true override ServerInfo attribute ! 
		if (val==false)
		{
			val=getServerInfo().getUsePassiveMode(false);

		}

		// update instance info: 
		getServerInfo().setAttribute("instance."+ATTR_PASSIVE_MODE, val);

		Debug("getUsePassiveMode="+val); 

		return val; 
	}


	/**
	 * Single transfer watcher which watches the ongoing transfer 
	 * and updates the VFSTransfer info
	 */ 

	SrbTransferWatcher createProgressWatcher(final VFSTransfer transfer,
			final GeneralFile source,
			final GeneralFile target, 
			final boolean isDir,
			final Thread transferThread)
	{
		return new SrbTransferWatcher(transfer,
				this,
				source,
				target,
				isDir,
				transferThread); 
	}

	/** Light weight implementiaton to recurse directories and accumulate sizes*/  
	static long[] cumulateSizes(GeneralFile target)
	{
		Debug(">>> cumulateSizes entering for:"+target);

		long stats[]=new long[2]; 

		long total=0;
		int nrSources=0; 
		nrSources++;  // this directory

		String[] childs = target.list(); 

		if (childs!=null) 
			for (String child:childs)
			{

				nrSources++; // this child 

				GeneralFile file;

				if (target instanceof SRBFile) 
					file=new SRBFile((SRBFile)target,child);
				else
					file=new LocalFile((LocalFile)target,child);

				if (file.isDirectory())
				{
					//enter recursion 
					long childstats[]=cumulateSizes(file);
					total+=childstats[0]; 
					nrSources+=childstats[1];
				}
				else
				{
					// get file length:
					total+=file.length();
				}
			}

		stats[0]=total; 
		stats[1]=nrSources; 

		Debug("<<< cumulateSizes leaving for:"+target);


		return stats; 
	}

	static long timeStringToMillie(String valstr)
	{
		//  value is in YYYY-MM-DD-hh.mm.ss
		int YYYY=Integer.valueOf(valstr.substring(0,4)); 
		int MM=Integer.valueOf(valstr.substring(5,7)); 
		int DD=Integer.valueOf(valstr.substring(8,10)); 
		int hh=Integer.valueOf(valstr.substring(11,13)); 
		int mm=Integer.valueOf(valstr.substring(14,16)); 
		int ss=Integer.valueOf(valstr.substring(17,18));


		// GMT TIMEZONE: 
			TimeZone gmtTZ = TimeZone.getTimeZone("GMT-0:00");

			Calendar cal=new GregorianCalendar(gmtTZ);
			// O-be-1-kenobi: month nr in GregorianCalendar is zero-based 

			cal.set(YYYY, MM-1, DD,hh,mm,ss);
			// TimeZone localTZ=Calendar.getInstance().getTimeZone();
			// cal.setTimeZone(localTZ); 

			return cal.getTimeInMillis(); 
	}

	public boolean isReadable(SRBFile srbnode,boolean isDir) throws VlException
	{
		Debug("isReadable:getting acl of:"+srbnode);
		VAttribute[][] acl = getACL(srbnode,false,isDir);

		if ((acl==null) || (acl.length==0) || (acl[0].length==0)) 
			return false; 

		String accessStr=acl[0][2].getStringValue();

		Debug("acl[0][2]="+accessStr);

		if (accessStr.compareTo(ACCESS_READ)==0) 
			return true; 

		if (accessStr.compareTo(ACCESS_ALL)==0) 
			return true; 

		return false; 
	}

	public boolean isWritable(SRBFile srbnode,boolean isDir) throws VlException
	{
		Debug("isWritable:getting acl of:"+srbnode);
		VAttribute[][] acl = getACL(srbnode,false,isDir);

		if ((acl==null) || (acl.length==0) || (acl[0].length<2)) 
			//throw new VlException("Server didn't return valid ACL list for:"+srbnode); 
			return false; 

		String accessStr=acl[0][2].getStringValue();

		Debug("acl[0][2]="+accessStr);

		if (accessStr.compareTo(ACCESS_WRITE)==0) 
			return true; 

		if (accessStr.compareTo(ACCESS_ALL)==0) 
			return true; 

		return false; 
	}

	public String getPermissionsString(SRBFile srbnode, boolean isDir) throws VlIOException
	{
		String permStr="-";

		if (isDir) 
			permStr="d"; 

		Debug("getPermissionsString:getting acl of:"+srbnode);
		// disable 'all' users for now: 
		VAttribute[][] acl = getACL(srbnode,false,isDir);

		if ((acl==null) || (acl.length==0) || (acl[0].length<2)) 
			//throw new VlException("Server didn't return valid ACL list for:"+srbnode); 
			return permStr+"??"; 

		int index=0; 

		String accessStr=acl[index][2].getStringValue();
		String commastr="";
		Debug("acl[index][2]="+accessStr);

		if (accessStr.compareTo(ACCESS_NONE)==0) 
			permStr+="-- ["; 
		else if (accessStr.compareTo(ACCESS_READ)==0) 
			permStr+="r- ["; 
		else if (accessStr.compareTo(ACCESS_WRITE)==0) 
			permStr+="-w [";  
		else if (accessStr.compareTo(ACCESS_ALL)==0) 
			permStr+="rw [";  
		else if (accessStr.compareTo(ACCESS_ANNOTATE)==0) 
		{
			permStr+="-- [Annotate";
			commastr=","; // for next attribute 
		}

		// SRB Sayz no hidden files:     
		//if (srbnode.isHidden())
		//{
		//    permStr+=commastr+"H";
		//    commastr=",";
		// }
		permStr+="]";
		// no extra entry for now:
		if (acl.length>1) 
			permStr+="+"; // signal user for extended permissions 

		return permStr; 

	}

	// ==== 
	// New VServer interface 
	// ==== 

	public VRL getServerVRL()
	{
		return new VRL(VRS.SRB_SCHEME,null,getHostname(),getPort(),null);
	}

	public String getScheme() 
	{
		return VRS.SRB_SCHEME;
	}

	public String getHostname() 
	{
		return getServerInfo().getHostname();
	}

	public int getPort() 
	{
		return getServerInfo().getPort();
	}

	public SrbFile getFile(String path) throws ResourceNotFoundException 
	{
		SRBFile file = new SRBFile(this.srbFS,path);

		if (file.exists()==false) 
			throw new ResourceNotFoundException("Could not stat file:"+path); 

		if (file.isFile()==false) 
			throw new ResourceNotFoundException("Path exists, but is not a file:"+path); 

		return new SrbFile(this,file); 

	}

	public SrbDir getDir(String path) throws VlException 
	{
		SRBFile file = new SRBFile(this.srbFS,path);

		if (file.exists()==false) 
			throw new ResourceNotFoundException("Could not stat file:"+path); 

		if (file.isDirectory()==false) 
			throw new ResourceNotFoundException("Path exists, but is not a directory:"+path); 

		return new SrbDir(this,file); 

	}

	@Override
	public VFSNode openLocation(VRL loc) throws VlException
	{
		if (isConnected()==false)
		{
			Global.errorPrintln(this,"Server not connected. Reconnecting:"+this);
			connect(); 
			// throw new VlServerException("No Connection. Invalid Authentication ?"); 
		}

		// SRBAccount.setVersion(SRBAccount.SRB_VERSION_3_0_2);
		// SRBAccount.setVersion(SRBAccount.SRB_VERSION_3_3); 
		// SRBAccount.setVersion(SRBAccount.SRB_VERSION_3_3_1);

		// only in jargon: v1.4.11

		String path=loc.getPath();
		
		String query=loc.getQuery(); 

		// create default home directory if no path specified

		// construct: 
		//String defaultHome="/"+srbAccount.getMcatZone()+"/home/"+srbAccount.getUserName()+"."+srbAccount.getDomainName();

		String defaultHome=getHomeDirectory(); 

		if (query==null)
		{
			if ((path==null) || (path.equalsIgnoreCase("")==true))
			{
				// use defaulthome
				path=defaultHome;
				// path="/"+srbAccount.getMcatZone()+"/"+srbfs.getHomeDirectory(); 

			}
		}

		// *** Expand '~' to default User Home *** 

		if (path.startsWith("~"))
		{
			// after ~ there should be a '/' (~/...) 
			path=defaultHome+((path.length()>1)?path.substring(1):"");
		}
		else if (path.startsWith("/~")) 
		{
			// after /~ there should be a '/' (/~/...) 
			path=defaultHome+((path.length()>2)?path.substring(2):"");
		}
		
		Debug("Fetching path:"+path);

		SRBFileSystem srbfs=getSRBFileSystem(); 

		SRBFile gfile = new SRBFile(srbfs,path);

		if (query!=null)
		{
			// get URL Query 
			//VAttributeSet aset = location.getQueryAttributes(); 
			String qstr=null;
			qstr=loc.getQuery(); 


			// path is optional, can be used for the query
			return new SrbDir(this,path,qstr,loc);
		}
		else if (gfile.isFile())
		{
			return new SrbFile(this,gfile);
		}

		// Unknown: Store as directory, path might be queryable: 
		// This is necessary for browsing 'up' as these directories
		// return false when performing the 'exists()' or isDir() method! 
		//else if (gfile.isDirectory())
		{
			return new SrbDir(this,path);
		}
	}

	/** Convert Exception and add extra error information. */

	private VlException convertException(Exception e)
	{
		return convertException(null,e); 
	}

	public static VlException convertException(String extrainfo, Exception e)
	{
		String name="VlException"; 

		// extra message information: 
		if ((extrainfo!=null) && (extrainfo.endsWith("\n")==false))
		{
			extrainfo+="\n";
		}

		if (extrainfo==null) 
			extrainfo=""; 

		// keep VlExceptions; 
		if (e instanceof VlException) 
		{
			if (extrainfo.compareTo("")==0)
				return (VlException)e;

			// Chain exception to add extra info to Exception 
			return new VlException(((VlException)e).getName(),extrainfo+e.getMessage(),e); 
		}

		String srbstr=null; 
		String message="";  

		if (e instanceof SRBException)
		{
			SRBException srbe=(SRBException)e;
			srbstr=srbe.getStandardMessage();
		}

		if ((e.getMessage()!=null) && (e.getMessage().compareTo("")!=0))
			message="Message="+e.getMessage()+"\n";

		if (srbstr!=null) 
			message+="SRB Error="+srbstr;

		// Piter says: explain cryptic SRB Error messages to user.  
		if (srbstr!=null)
		{
			if (srbstr.startsWith("USER_NAME_NOT_FOUND"))
			{
				message=extrainfo+"Unknown user.\n---\n"+message; 
				return new nl.uva.vlet.exception.VlAuthenticationException(message,e);
			}

			String errStr="SVR_TO_SVR_CONNECT_ERROR"; 
			CharSequence errCS=errStr.subSequence(0,errStr.length()); 

			// be more informative then the confuziling Server to Server error. 
			if (srbstr.contains(errCS)) 
			{
				message=extrainfo
				+ "Client to Server or Server to Client connection failed."
				+ "Maybe active transfer mode failed." 
				+ "\n---\n"+message; 
				return new nl.uva.vlet.exception.VlServerException(message,e);
			}

			errStr="OBJ_ERR_RES_NOT_REG"; 
			errCS=errStr.subSequence(0,errStr.length()); 

			// be more informative then the confuziling Server to Server error. 
			if (srbstr.contains(errCS)) 
			{
				message=extrainfo
				+ "Resource could not be registered. The target resource might be incorrect"
				+ "\n----\n"+message; 

				return new nl.uva.vlet.exception.VlIOException(message,e);
			}

			errStr="NO_ACCS_TO_USER_IN_COLLECTION"; 
			errCS=errStr.subSequence(0,errStr.length()); 

			// be more informative then the confuziling Server to Server error. 
			if (srbstr.contains(errCS)) 
			{
				message=extrainfo
				+ "User has no access rights to this location."
				+ "\n---\n"+message; 
				return new nl.uva.vlet.exception.ResourceAccessDeniedException(message,e);
			}
		}

		// default exception: 
		if (e instanceof IOException)
		{
			message=extrainfo+message; 
			return new VlIOException(message,e);
		}
		else
		{
			message=extrainfo+message;
			return new VlException(name,message,e);
		}
	}


	public MetaDataRecordList[] getPermissions(SRBFile file,boolean isDir, boolean allUsers )
	throws IOException
	{
		SRBFileSystem srbFileSystem= (SRBFileSystem) file.getFileSystem();

		if (allUsers)
		{
			if (isDir)
			{
				MetaDataCondition conditions[] = 
				{
						null, //getZoneCondition(),
						MetaDataSet.newCondition(
								SRBMetaDataSet.ACCESS_DIRECTORY_NAME, MetaDataCondition.EQUAL,
								file.getAbsolutePath() ),
				};
				MetaDataSelect selects[] = {
						MetaDataSet.newSelection( SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT ),
						MetaDataSet.newSelection( UserMetaData.USER_NAME ),
						MetaDataSet.newSelection( SRBMetaDataSet.USER_DOMAIN )
				};
				return file.getFileSystem().query( conditions, selects );
			}
			else {
				MetaDataSelect selects[] = {
						MetaDataSet.newSelection( SRBMetaDataSet.ACCESS_CONSTRAINT ),
						MetaDataSet.newSelection( UserMetaData.USER_NAME ),
						MetaDataSet.newSelection( SRBMetaDataSet.USER_DOMAIN ),
				};
				return file.query( selects );
			}
		}
		else {
			String userName = srbFileSystem.getUserName();
			String userDomain = srbFileSystem.getDomainName();

			if (isDir) 
			{
				MetaDataCondition conditions[] = 
				{
						null, //getZoneCondition(),
						MetaDataSet.newCondition(
								SRBMetaDataSet.ACCESS_DIRECTORY_NAME, MetaDataCondition.EQUAL,
								file.getAbsolutePath() ),
								MetaDataSet.newCondition(
										SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL, userName ),
										MetaDataSet.newCondition(
												SRBMetaDataSet.USER_DOMAIN, MetaDataCondition.EQUAL, userDomain ),
				};
				MetaDataSelect selects[] = {
						MetaDataSet.newSelection( SRBMetaDataSet.ACCESS_DIRECTORY_NAME ),
						MetaDataSet.newSelection( SRBMetaDataSet.DIRECTORY_ACCESS_CONSTRAINT ),
						MetaDataSet.newSelection( UserMetaData.USER_NAME ),
						MetaDataSet.newSelection( SRBMetaDataSet.USER_DOMAIN ),
				};
				return srbFileSystem.query( conditions, selects );
			}
			else {
				MetaDataCondition conditions[] = {
						MetaDataSet.newCondition(
								SRBMetaDataSet.USER_NAME, MetaDataCondition.EQUAL, userName ),
								MetaDataSet.newCondition(
										SRBMetaDataSet.USER_DOMAIN, MetaDataCondition.EQUAL, userDomain ),
				};
				MetaDataSelect selects[] = {
						MetaDataSet.newSelection( SRBMetaDataSet.ACCESS_CONSTRAINT ),
						MetaDataSet.newSelection( UserMetaData.USER_NAME ),
						MetaDataSet.newSelection( SRBMetaDataSet.USER_DOMAIN ),
				};
				return file.query( conditions, selects );
			}
		}
	}

	public boolean isServerAttribute(String fieldName)
	{
		return (getServerInfo().getAttribute(fieldName)!=null); 
	}

//	public LocalFilesystem getLocalFS() throws VlException
//	{
//		return LocalFilesystem.createServerFor(this.getContext(),new VRL("file:///")); 
//	}

	public String getUserAndDomain()
	{
		return getServerInfo().getUsername()+"."+getServerInfo().getAttributeValue(ATTR_MDASDOMAINNAME); 
	}
	
	@Override
	public VDir newDir(VRL path) throws VlException 
	{	
		return new SrbDir(this,path.getPath()); 
	}

	@Override
	public VFile newFile(VRL path) throws VlException 
	{
		return new SrbFile(this,path.getPath()); 
	}

	public VDir createDir(String name, boolean force) throws VlException
	{
		String fullPath=resolvePath(name); 

		SRBFile newDir = new SRBFile(this.srbFS, fullPath);

		if (newDir.exists() && (force==false))
		{
			throw new ResourceAlreadyExistsException("Directory already exists:"+newDir); 
		}

		if (newDir.exists() && (newDir.isDirectory()==false))
		{
			throw new ResourceCreationFailedException("File path already exists but is not a directory:"+newDir); 
		}	

		if ((newDir.exists()==true) && (force==true))
			return new SrbDir(this,newDir); // return already existing dir ! 


		return new SrbDir(this,newDir);
	}


	public VFile createFile(String fileName, boolean force) throws VlException
	{
		String fullPath=resolvePath(fileName); 
		SRBFile newFile = new SRBFile(this.srbFS,fullPath);

		if (newFile.exists())
		{
			if (force==false)
			{
				throw new ResourceAlreadyExistsException("File already exists:"+this);
			}

			if (newFile.isFile()==false)
			{
				throw new ResourceAlreadyExistsException("path already exists but is not a file:"+newFile.getAbsolutePath());
			}

			newFile.delete(true); // delete previous !\
		}

		try
		{
			if (newFile.createNewFile() == false)
			{
				throw new ResourceCreationFailedException(
						"Could not create srb file:" + newFile + "\n"
						+ "No reason given by server");
			}

			return new SrbFile(this,newFile);  
		}
		catch (Exception e)
		{
			throw SrbFileSystem.convertException("Could not create file:"+newFile,e); 
		}
	}


}
