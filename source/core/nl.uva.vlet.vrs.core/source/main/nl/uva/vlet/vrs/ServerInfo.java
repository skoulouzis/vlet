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
 * $Id: ServerInfo.java,v 1.21 2011-12-07 10:20:52 ptdeboer Exp $  
 * $Date: 2011-12-07 10:20:52 $
 */ 
// source: 

package nl.uva.vlet.vrs;


import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PASSIVE_MODE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_USERNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_VO_NAME;
import static nl.uva.vlet.data.VAttributeConstants.AUTH_SCHEME;
import nl.uva.vlet.Global;
import nl.uva.vlet.MessageStrings;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.SecretStore;
import nl.uva.vlet.vrms.SecretStore.Secret;

/** 
 * Server (Resource) Info class to keep record of Server Resources.
 * 
 * A ServerInfo object describes one Server Resource or Server Account (+ settings). 
 * To describe multiple (Server) Resources, multiple objects have to be created and 
 * stored into the ServerInfo Registry.
 * The ServerInfoRestry might save them in the file: ~/.vletrc/myvle/serverreg.srvx
 * A ServerInfo record is storeed using {SCHEME,HOST,PORT,USERINFO} as key. 
 * Any field which is NULL or Empty is regarded a wild card!.   
 * @see ServerInfoRegistry 
 * 
 * @author P.T. de Boer
 */
public class ServerInfo
{
    // =======================================================================
    // Class field
    // =======================================================================


    /** Unique key used in server info registry ! */ 
    public static final String ATTR_SERVER_ID = "serverID";
    
    public static final String ATTR_PROFILE_ID = "profileID";

    public static final String ATTR_SERVER_NAME = "serverName";

    public static final String ATTR_DEFAULT_PATH = "defaultPath";
    
    public static final String ATTR_ROOT_PATH = "rootPath";
    
    /** "id_rsa" or "id_dsa" file in ~/.ssh */ 
    public static final String ATTR_SSH_IDENTITY = "sshIdentity";
    
    public static final String ATTR_SSH_USE_PROXY    = "sshUseProxy";
    
    public static final String ATTR_SSH_PROXY_HOSTNAME = "sshProxyHostname";

    public static final String ATTR_SSH_PROXY_PORT = "sshProxyPort";
    
    public static final String ATTR_SSH_LOCAL_PROXY_PORT = "sshLocalProxyPort";
    
    public static final String ATTR_SSH_PROXY_USERNAME = "sshProxyUsername";

    public static final String ATTR_NEED_PORT="needPort"; 
    
    public static final String ATTR_NEED_HOSTNAME="needHostname"; 

    public static final String ATTR_NEED_USERINFO = "needUserinfo"; 

    public static final String ATTR_SUPPORT_URI_ATTRS = "supportURIAttributes";
    
    // This is not an attribute, but an attribute value 
    public static final String PASSWORD_AUTH = "Password";

    public static final String PASSWORD_OR_PASSPHRASE_AUTH = "PasswordOrPassPhrase";

    public static final String PASSPHRASE_AUTH = "Passphrase";

    public static final String ATTR_DEFAULT_YES_NO_ANSWER = "defaultYesNoAnswer";

    //  This is not an attribute, bute an attribute value 
    public static final String GSI_AUTH = "GSI_AUTH";

    //  This is not an attribute, bute an attribute value 
	public static final String NO_AUTH = "NO_AUTH";
	
    public static final String authSchemes[] =
           { GSI_AUTH, PASSWORD_AUTH };

    /**
     * Meta Attributes control settings of this Server Info. 
     * These do not appear as configurable ServerInfo attributes. 
     */
    public static final String metaAttributeNames[]=
          {
            ATTR_SUPPORT_URI_ATTRS,
            ATTR_NEED_HOSTNAME,
            ATTR_NEED_PORT,
            ATTR_NEED_USERINFO
          };
    
    // =======================================================================
    // Class Methods / Factory Methods
    // =======================================================================

    // == Factory Methods == // 

    public static ServerInfo createFor(VRSContext context, VRL vrl)
    {
        if (vrl==null)
            throw new NullPointerException("ServerInfo.createFor(): NULL pointer Exception (VRL=null)");
        return new ServerInfo(context,vrl); 
    }
    
    // =======================================================================
    // Instance fields
    // =======================================================================
    // 
    protected VAttributeSet _serverAttributes = new VAttributeSet();

    /** Key which as used to store key */  
    protected String serverKey;

    protected VRSContext vrsContext = null;

    // New Read Only config! 
    protected boolean isEditable=true; 
    
    /** Protected constructor ! */
    protected ServerInfo()
    {

    }

    /** Copy Constructor */
    public ServerInfo(ServerInfo info)
    {
        // empty attribute set 
        this.copyFrom(info);
    }

    /**
     * Create ServerInfo Description.
     * Uses {scheme,host,port,userInfo).
     * Does not store into ServerRegistry.  
     */
    public ServerInfo(VRSContext context, String scheme, String host, int port,
            String userInfo)
    {
        init(context, new VRL(scheme, userInfo,host, port, null));
    }

    /**
     * Constructs default ServerInfo from VRL.
     * Does not store into ServerRegistry.
     */
    public ServerInfo(VRSContext context, VRL location)
    {    
        init(context, location); 
    }

    public ServerInfo(VRSContext context, VAttributeSet set)
    {
        init(context,null); 
        this._serverAttributes=set.duplicate();  
        // update key 
        String value=set.getStringValue(ATTR_SERVER_ID);
        
        if (StringUtil.isEmpty(value))
        {
        	Global.errorPrintf(this,"Server ID Is EMPTY for: %s\n",this.toString()); 
        }
        else
        {
        	this.setID(value);	
        }
    }

    protected void clear()
    {
        this._serverAttributes.clear(); 
        this.serverKey=null;
    }

    // Initialize
    private void init(VRSContext context, VRL vrl)
    {
        if (context==null)
            throw new NullPointerException("Context may NOT be null"); 
        
        this.vrsContext = context;

        initServerVRL(vrl); 

        //this.setUseGSIAuth();  
        // default to NO authentication ! 
        this.setUseNoAuth();
    }
    
    //
    // Set Server field from VRL, note that this effects the ID 
    // of this ServerInfo !!
    // 
    protected void initServerVRL(VRL vrl)
    {
        if (vrl==null) 
            return;
        
        String host=vrl.getHostname();
        int port=vrl.getPort();
        String scheme=vrl.getScheme();
        String userInfo=vrl.getUserinfo(); 
        String path=vrl.getPath(); 
        
        // VRL with empty schemes are relative VRLs 
        if (StringUtil.isNonWhiteSpace(scheme))
            setScheme(scheme);
        else
            throw new Error("Cannot create ServerInfo with empty scheme"); 
        
        if (StringUtil.isNonWhiteSpace(userInfo))
            this.setUserinfo(userInfo);
        
        if (StringUtil.isNonWhiteSpace(host))
            setHostname(host);
        else
            setHostname(""); 
        
        if (port>0)
            setPort(port);
        
        if (StringUtil.isEmpty(path)==false)
            setDefaultHomePath(path);
       
    }

    /** Returns VRS Context associated with this Server Description */
    public VRSContext getContext()
    {
        return this.vrsContext;
    }

    /** Deep Copy All attributes and fields */ 
    public void copyFrom(ServerInfo source)
    {
        this.vrsContext = source.vrsContext;
        this.isEditable=source.isEditable;
        // Duplicate Key ! 
        this.serverKey=source.serverKey; 
        
        // this._serverAttributes = source._serverAttributes.duplicate();
        // Do a one by one copy (allows for checking !) 
        for (VAttribute attr:source._serverAttributes)
        {
            // must duplicate !
            setAttribute(attr.duplicate()); 
        }
    }

    public ServerInfo duplicate()
    {
        return new ServerInfo(this);
    }

     // === 
     // Core setters/getters 
     // === 
    
    public boolean isEditable()
    {
        return isEditable; 
    }
    
    public VAttribute getAttribute(String name)
    {
        VAttribute attr= _serverAttributes.get(name);
        if (attr!=null)
            return attr.duplicate(); 
        return null; 
    }
    
    public String getAttributeValue(String name)
    {
        VAttribute attr= _serverAttributes.get(name);
        if (attr==null)
        	return null;
        return attr.getStringValue(); 
    }
    
    /**
     * Returns Server Configuration attribute names, but without
     * meta attributed/hidden attribute names! 
     */
    public String[] getAttributeNames()
    {
        StringList nameList=this._serverAttributes.getOrdenedKeyList();
        // remove hidden/meta attributes: 
        nameList.remove(ServerInfo.metaAttributeNames);
        return nameList.toArray(); 
    }
    
    /**
     * Returns Server Configuration attribute names including
     * meta attributes/hidden attribute names! 
     */
    public String[] getAllAttributeNames()
    {
        return this._serverAttributes.getOrdenedKeyArray();
    }

    /** Remove Attribute from attribute hash */
    public void remove(String attrName)
    {
         // never remove scheme
        if (StringUtil.compare(ATTR_SCHEME,attrName)==0)
            return;  
       this._serverAttributes.remove(attrName); 
    }

    /** Set Attribute. All other setter call this one */ 
    public void setAttribute(VAttribute attr)
    {
        if (attr == null)
            return; 
        
        _serverAttributes.put(attr);
    }
   
    /**
     * Compares {scheme,host,port,userInfo} with this Server description.
     * <br>
     * If a field is null (or for port less than 0) this means "don't care". 
     * This method is used by the ServerInfoRegistry to search the ServerInfo 
     * database. 
     * Use port==0 to match DEFAULT protocol port (or 0 itself)  
     * Returns multiple matching ServerInfo descriptions or NULL when 
     * it can't find a matching server description.  
     * <p>
     * This method is different then 'equals' since it allows for wildcard matching! 
     */
    public boolean matches(String scheme, String host, int port, String userInfo)
    {
        // ===
        // Scheme: if null => don't care  
        // ===
        
        if (scheme != null)
            if (StringUtil.compare(scheme,this.getScheme()) != 0)
                return false;

        // ===
        // Hostname: If null => don't care 
        // ===

        if (host != null)
            if (StringUtil.compare(host,this.getHostname()) != 0)
                return false;

        // ===
        // Port: if (port < 0) => Don't care. If (port==0) => match default port !  
        // ===

        // match specific port ! 
        if (port > 0)
        {
            if (port != getPort())
                return false;
        }
        else if (port == 0)
        {
            //  match "0" or default port ! 
            // Only return false if serverPort is not 0 and NOT the default 
            // port ! 

            if ((getPort() != 0)
                    && (VRS.getSchemeDefaultPort(scheme) != getPort()))
                return false;

            // either getServerPort()=0 of getServerPort()=default port 
        }
        
        // ===
        // check userInfo, ignore is NULL
        // ===  

        if (StringUtil.isNonWhiteSpace(userInfo))
            if (StringUtil.compare(userInfo,getUserinfo()) != 0)
                return false;

        return true;
    }
    
    // =======================================================================
    // Special Attributes: handle with care 
    // =======================================================================

    public String getPassword()
    {
    	// use secretstore 
    	Secret sec=getSecret(); 
    	if (sec!=null)
    		return sec.getPassword(); 
    	return null;  
    }

    public String getPassphrase()
    {
    	// use secretstore
    	Secret sec=getSecret(); 
    	if (sec!=null)
    		return sec.getPassphrase(); 
    	return null;  
    }
    
    public String getPassword(String identifier)
    {
        // use secretstore 
        Secret sec=getSecret(identifier); 
        if (sec!=null)
            return sec.getPassword(); 
        return null;  
    }

    public String getPassphrase(String identifier)
    {
        // use secretstore
        Secret sec=getSecret(identifier); 
        if (sec!=null)
            return sec.getPassphrase(); 
        return null;  
    }
    
    protected Secret getSecret()
    {
    	return getSecretStore().getSecret(getScheme(),getUserinfo(),getHostname(),getPort()); 
    }
    
    protected Secret getSecret(String identifier)
    {
        return getSecretStore().getSecret(getScheme(),identifier+"-"+getUserinfo(),getHostname(),getPort()); 
    }
    protected SecretStore getSecretStore()
    {
        return this.vrsContext.getServerInfoRegistry().getSecretStore();
    }

    public void setPassword(String passwd)
    {
    	getSecretStore().storePassword(getScheme(),getUserinfo(),getHostname(),getPort(),passwd); 
    }
    
    public void setPassphrase(String passphrase)
    {
    	getSecretStore().storePassphrase(getScheme(),getUserinfo(),getHostname(),getPort(),passphrase); 
    }

    public void setPassword(String identifier,String passwd)
    {
        getSecretStore().storePassword(getScheme(),identifier+"-"+getUserinfo(),getHostname(),getPort(),passwd); 
    }
    
    public void setPassphrase(String identifier,String passphrase)
    {
        getSecretStore().storePassphrase(getScheme(),identifier+"-"+getUserinfo(),getHostname(),getPort(),passphrase); 
    }

    // =======================================================================
    // Derived getters,setters and attributes. Use above setters/getters ! 
    // =======================================================================
    
    /** Set Server Attribute. Update editable flag of VAttribute */
    public void setAttribute(VAttribute attr, boolean editable)
    {
        if (attr == null)
            return; 
        attr.setEditable(editable); 
        setAttribute(attr); 
    } 
   
    public String getProperty(String name)   
    {
        return getStringProperty(name); 
    }

    public String getStringProperty(String name)
    {
        VAttribute val = this.getAttribute(name);

        if (val != null)
            return val.getStringValue(); // explicit get StringValue 

        return null;
    }

    public String getStringProperty(String name,String defaultValue)
    {
        VAttribute val = this.getAttribute(name);

        if (val != null)
            return val.getStringValue(); // explicit get StringValue 

        return defaultValue; 
    }

    public int getIntProperty(String name, int defVal)
    {
        VAttribute attr = getAttribute(name);

        if (attr != null)
            return attr.getIntValue();
        else
            return defVal;
    }

    public boolean getBoolProperty(String name, boolean defVal)
    {
        VAttribute attr = getAttribute(name);

        if (attr != null)
            return attr.getBooleanValue();
        else
            return defVal;
    }
    
    /** 
     * Update are create new ServerInfo Attribute.
     * Set editable to true if this attribute may be changed or not. 
     */  
    public void setAttribute(String name, String val, boolean editable)
    {
        VAttribute attr=this.getAttribute(name);
        
        if (attr==null)
            attr=new VAttribute(name,val);
        else
            attr.setValue(val); 
                
        this.setAttribute(attr,editable);  
    }

    public void setAttribute(String name, String val)
    {
         setAttribute(new VAttribute(name,val),true); 
    } 
    
    public void setAttribute(String name, int intVal)
    {
        this.setAttribute(new VAttribute(name, intVal),true);
    }
    
    public void setAttribute(String name, boolean boolVal)
    {
        setAttribute(new VAttribute(name, boolVal),true);
    }
    
    /** 
     * Returns effective server port. 
     * If port is not specified or port<-0 then the default port 
     * is returned. 
     * @see nl.uva.vlet.vrs.VRS#getDefaultScheme(String) 
     */
    public int getPort()
    {
    	int defPort=VRS.getSchemeDefaultPort(getScheme());
        return getIntProperty(ATTR_PORT, defPort);
    }

    public void setPort(int port)
    {
        setAttribute(new VAttribute(ATTR_PORT, port),true);
    }

    public String getHostname()
    {
        return getStringProperty(ATTR_HOSTNAME);
    }

    public void setScheme(String scheme)
    {
        setAttribute(new VAttribute(ATTR_SCHEME, scheme),false); 
    }
    
    public void setHostname(String val)
    {
        setAttribute(new VAttribute(ATTR_HOSTNAME, val),true);
    }

    public void setVOName(String vo)
    {
        setAttribute(new VAttribute(ATTR_VO_NAME, vo),true);
    }
    
    /**
     * Returns real username, without optional domainname or VO name. 
     * To get full userinfo, use getUserinfo(); 
     * @return real username part of userinfo.
     */
    public String getUsername()
    {
        return getStringProperty(ATTR_USERNAME);
    }

    /**
     * Return username  + optional ProfileID  
     */
    public String getUserinfo()
    {
        return createUserinfo(getUsername(),getProfileID());
    }

    /**  
     * Returns userinfo string which consists of a username and 
     * an optional vo name: "&lt;USER&gt;[&lt;ProfileID&gt;]"
     */
    public static String createUserinfo(String userstr,String profileId)
    {
        if (StringUtil.isEmpty(userstr)) 
            if (StringUtil.isEmpty(profileId))
                return null; 
            else
                return "["+profileId+"]";
        else
            if (StringUtil.isEmpty(profileId))
                return userstr; 
            else
                return userstr+"["+profileId+"]";
    }
    
    /** Returns VO Name if configured */ 
    public String getVOName()
    {
        return this.getStringProperty(ATTR_VO_NAME);
    }
    
    /** Returns ProfileID if this ServerInfo is part of a specific profile */ 
    public String getProfileID()
    {
        return this.getStringProperty(ATTR_PROFILE_ID);
    }
    
    public boolean hasScheme(String scheme)
    {
        if (scheme==null)
            return false;

        String scheme2 = this.getScheme();
        
        if (scheme2==null)
            return false; 
         
        return StringUtil.equals(scheme,scheme2);  
    }
    
    /** Splits optional username and VO name and stores them */ 
    public void setUserinfo(String userinf)
    {
        String strs[]=ServerInfo.splitUserinfo(userinf);
        
        if (strs==null)
            return; 
        
        // first is username
        if ((strs.length>=1) && StringUtil.isEmpty(strs[0])==false)  
            setAttribute(new VAttribute(ATTR_USERNAME, strs[0]),true);
        
        // second is vo name 
        if ((strs.length>=2)  && StringUtil.isEmpty(strs[1])==false) 
            setAttribute(new VAttribute(ATTR_PROFILE_ID, strs[1]),true);
        
        if (userinf==null) 
            return; 
    }
    
    /**
     * Splits userinfo into array containg the (optional) username and (optional)
     * ProfileID name. 
     * First string is always the username,second string always
     * the ProfileID name, but values can be null if not specified. 
     */
    private static String[] splitUserinfo(String userinf)
    {
        String strs[]=null;
        
        String result[]=new String[2]; 
        
        // split user[VO] information 
        if (userinf.endsWith("]"))
        {
            strs=userinf.split("[");
            // must have two parts: 
            if ((strs!=null) && (strs.length>=2))
            {
                result[0]=strs[0]; // can be null or empty;
                String profidStr=strs[1];
                if ((profidStr!=null) && profidStr.length()>0) 
                    result[1]=profidStr.substring(0,profidStr.length()-1); // remove ']'
                
                return result; 
            } 
        }
        
        result[0]=userinf;
        result[1]=null;
        
        return result; 
    }

    public void setUsername(String user)
    {
        setAttribute(new VAttribute(ATTR_USERNAME, user),true);
    
    }
    
    protected void setProfileID(String id)
    {
        setAttribute(new VAttribute(ATTR_PROFILE_ID,id),false);    
    }
    
    public String getScheme()
    {
        return getStringProperty(ATTR_SCHEME);
    }

   
    public boolean usePasswordAuth()
    {
        String authScheme = getStringProperty(AUTH_SCHEME);

        if (authScheme == null)
            return false;
        // only return true if attribute has been set to PASSWORD_AUTH!
        if (authScheme.compareToIgnoreCase(PASSWORD_AUTH) == 0)
            return true;

        return false;
    }

    public void setUsePasswordAuth()
    {
    	setAttribute(AUTH_SCHEME, PASSWORD_AUTH,false);
    }

    /**
     *  Only return server attributes without authentication attributes
     */
    public VAttribute[] getAttributes()
    {
        // No server Attribute names defined 
        String[] attrNames = getAttributeNames();

        int len = attrNames.length;

        VAttribute attrs[] = new VAttribute[attrNames.length];

        for (int i = 0; i < len; i++)
        {
            if (attrNames[i] != null)
            {
                attrs[i] = getAttribute(attrNames[i]);
            }
            else
            {
                Global.warnPrintf(this, "**** Oopsy: Server attrName[%d]=NULL!\n",i); 
            }
        }

        return attrs;
    }

    /**
     * Returns complete attribute set as Array. 
     * Includes both server properties as well as (hidden) meta attributes. 
     */ 
    public VAttribute[] getAllAttributes()
    {
        return this._serverAttributes.toArray(); 
    }

    public boolean hasAttribute(String name)
    {
        return (this._serverAttributes.get(name)!=null);
    }
    
    /** Return INTERNAL Set of attributes */ 
    public VAttributeSet getAttributeSet()
    {
        return this._serverAttributes; 
    }
   
    /** 
     * Stores this object in the ServerInfo hash 
     * @return previous object.  
     */
    public ServerInfo store()
    {
        debug("store()"); 
        return this.vrsContext.storeServerInfo(this);
    }

    /** Remove matching ServerInfo from persistent ServerInfoRegistry ! */ 
    public void persistentDelete()
    {	
    	this.vrsContext.getServerInfoRegistry().remove(this);
    }
   
    /**
     * Returns true whether interactive authentication is needed 
     * using the specified VRS Context (which contains proxy 
     * and passwords). Use by VBrowser to show authentication dialog 
     * before connecting to a remote resource. 
     *  
     * This is the case for:<br> 
     * - useGSIAuth=true and the current proxy is invalid<br>
     * - usePassword==true and no password or passphrase is specified<br>
     */

    public boolean isAuthenticationNeeded(VRSContext context)
    {
        if (useGSIAuth() == true)
        {
            if (context.getGridProxy().isValid() == false)
                return true;
        }

        if (usePasswordAuth() == true)
        {
            // Already authenticated ? 
            if ((StringUtil.notEmpty(getPassword()))
                    || (StringUtil.notEmpty(getPassphrase())))
            {
                return (this.hasValidAuthentication()==false);
            }
            
            // SSH Identity mathiching: Use passphrase authentication 
            // Delay VBrowser's Authentication dialog. 
            // SSH will use Interactive UI dialog from VRSContext.getUI(); 
            if (!StringUtil.isEmpty(this.getStringProperty(ServerInfo.ATTR_SSH_IDENTITY)))
                return false; 

            return true;
        }

        //Default: no password or GSI authentication needed: 
        return false;
    }

	public void setHasValidAuthentication(boolean val)
    {
    	Secret sec=this.getSecret();
    	
    	if (sec!=null)
    		sec.setValidated(val); 
    	// else create nil secret ? 
    }

    /**
     * Returns whether authentication information is valid. 
     * This means a succesfull authentication has been performed. 
     * When the authentication fails, the isValid will be set to false, 
     * to indicate a non-valid authentication. 
     */
    public boolean hasValidAuthentication()
    {
    	Secret sec=getSecret();
    	if (sec!=null)
    		return sec.getValidated();
    	// no secret => maybe not needed! (This is not the place to check)
    	return true; 
    }

    /**
     * Returns key which is used to store this ServerInfo. 
     * Will return NULL if the ServerInfo hasn't been stored yet ! 
     * If this method return NULL, first call store() ! 
     * @return persistante ServerInfo ID ! 
     */
    public String getID()
    {
    	if (serverKey==null) 
    		return null; 
    	
    	// auto update if NOT null  
        return this.setID(serverKey);
    }
    
    private void debug(String msg)
    {
       // Global.errorPrintln(this, msg);
        Global.debugPrintf(this,"%s\n",msg);
    }

    public boolean getUsePassiveMode(boolean defVal)
    {
        return getBoolProperty(ATTR_PASSIVE_MODE, defVal);
    }

    public void setUsePassiveMode(boolean val)
    {
        setAttribute(new VAttribute(ATTR_PASSIVE_MODE,val),true); 
    }
    
    public void setAuthScheme(String authStr)
    {
        setAttribute(AUTH_SCHEME, authStr,true);
    }

    /**
     * Set authentication to GSI.
     */
    public void setUseGSIAuth()
    {
    	VAttribute attr = new VAttribute(AUTH_SCHEME,GSI_AUTH);
    	setAttribute(attr,false);
    }

    public void setUseNoAuth()
    {
        // only return true if attribute has been set to GSI_AUTH!
        setAttribute(AUTH_SCHEME, NO_AUTH,false);
    }
    
    /** Return true if no authentication scheme has been set. */
    public boolean useNoAuth()
    {
        String authScheme = getAuthScheme();
        if (StringUtil.isEmpty(authScheme)) 
            return true; 

        if (authScheme.compareToIgnoreCase(NO_AUTH) == 0)
            return true;

        return false;

    }

    public String getAuthScheme()
    {
        return getStringProperty(AUTH_SCHEME);
    }

    public boolean useGSIAuth()
    {
        String authScheme = getAuthScheme();
        if (authScheme == null)
            return false;

        if (authScheme.compareToIgnoreCase(GSI_AUTH) == 0)
            return true;

        return false;
    }

    /**
     * Returns default Server path if defined. 
     * Not all servers have a 'default' path.
     * This default path is not the same as the user's home location !  
     * Use A '~' in path prefix to optionally get an user home location ! 
     */
    public String getDefaultPath()
    {
        return getStringProperty(ATTR_DEFAULT_PATH);
    }
    
    /** Default Server (Home) Path */ 
    public void setDefaultHomePath(String path)
    {
        setAttribute(ATTR_DEFAULT_PATH,path,true);
    }
    
    /** Default Root Path, if different then "/" */ 
    public void setRootPath(String path)
    {
        setAttribute(ATTR_ROOT_PATH,path,true);
    }
    
    /**
     * Convenience method to set a Server Attribute if this attribute is 
     * not set Already. 
     * Method will ignore new value if the specified (Server) Attribute is already set. 
     * 
     */
    public void setIfNotSet(String name, boolean value)
    {
	// set editable server attributes: 
        this.setIfNotSet(new VAttribute(name, value),true);
    }

    /**
     * Convenience method to set a Server Attribute if this attribute is 
     * not set Already. 
     * Method will ignore new value if the specified (Server) Attribute is already set. 
     * 
     */
    public void setIfNotSet(String name, String value)
    {
        // set boolean server attribute 
        this.setIfNotSet(new VAttribute(name, value),true);
    }

    /**
     * Set int attribute if not already set. 
     */
    public void setIfNotSet(String name, int value)
    {
       // add new boolean attribute 
        this.setIfNotSet(new VAttribute(name, value),true);
    }
 
    /** 
     * Set attribute and update editable flag. Will update editable 
     * flag from existing attribute if it already exists. 
     */ 
    public void setIfNotSet(VAttribute attr,boolean editable)
    {
        VAttribute oldAttr=getAttribute(attr.getName()); 
        if (oldAttr!=null)
            attr=oldAttr;

        // update editable: 
        attr.setEditable(editable); 
 
        this.setAttribute(attr,true);
    }

    public VRL getServerVRL()
    {
        return new VRL(this.getScheme(), this.getHostname(), this
                .getPort(), this.getDefaultPath());
    }
    
    public String toString()
    {
        return "{ServerInfo:serverid=" + serverKey+ ",{" + getScheme()
                + "," + getHostname() + "," + getPort() + ","
                + getUserinfo() + "}";
    }
   
    /** Do some basic hardcoded checks on the server Attribute */ 
    public static void checkServerAttribute(VRSContext context, VAttribute attr)
    {
        if (attr == null)
            return;

        // SCHEME part in ServerInfo is never editable 
        if (attr.hasName(AUTH_SCHEME))
        {
        	attr.setEditable(false); 
        }
        
        if (attr.hasName(VAttributeConstants.ATTR_PASSIVE_MODE))
        {
            // Global set PASSIVE_MODE overrides server settings! 

            if (Global.getPassiveMode() == true)
            {
                if (attr.isEditable())
                    attr.setValue(true);
                
                attr.setEditable(false);
                attr.setHelpText(MessageStrings.TXT_PASSIVE_MODE_OVERRIDDEN);
            }
        }
    }

   /** 
    * Update ServerInfo: Remove Attributes if the name is not in the specified name list 
    * @see nl.uva.vlet.data.VAttributeSet#remoteIfNotIn(StringList)
    */ 
   public void removeAttributesIfNotIn(StringList attrNames)
   {
       StringList list=attrNames.duplicate();
       String names[]=getMandatoryAttributeNames(); 
       list.add(names); 
       
       this._serverAttributes.removeIfNotIn(attrNames); 
   }

   private String[] getMandatoryAttributeNames()
   {
       StringList list = new StringList();
       list.add(VAttributeConstants.ATTR_SCHEME); 
       list.add(ATTR_SERVER_ID); 
       list.add(ATTR_SERVER_NAME); 
       return list.toArray(); 
   }
       
   /** 
    * Update ServerInfo: Match VAttibutes with template. 
    * Copy fields and (enum) type from template but keep value. 
    * @see nl.uva.vlet.data.VAttributeSet#matchTemplate(VAttributeSet,boolean) 
    */ 
   public void matchTemplate(VAttributeSet templateSet,boolean removeOthers)
   {
      // check: 
      
      VAttributeSet  set=new VAttributeSet();
      // add defaults first: 
      set.put(new VAttribute(VAttributeConstants.ATTR_SCHEME,getScheme()),false); 
      set.put(new VAttribute(ATTR_SERVER_ID,this.getID()),false); 
      set.put(new VAttribute(ATTR_SERVER_NAME,getName()),true);
      
      // keep order :
      set.put(templateSet,templateSet.getOrdenedKeyArray()); 
      
      this._serverAttributes.matchTemplate(set,removeOthers); 
   } 

   /** Set editable flag of VAttributes */ 
   public void setEditable(String name,boolean val)
   {
      this._serverAttributes.setEditable(name,val); 
   }

   String setID(String id)
   {
	   if (StringUtil.isEmpty(id))
	   	  throw new Error("Server id cannot be NULL or empty"); 
	   
	   //check ID
       debug(">>> New Server ID="+id); 
       this._serverAttributes.put(new VAttribute(ATTR_SERVER_ID,id));
       this.serverKey=id; 
       return id; 
   }

   public String getName()
   {
       String name=getStringProperty(ATTR_SERVER_NAME);
       
       // auto init default: 
       if (StringUtil.isEmpty(name)) 
       {
    	   name=getScheme().toUpperCase()+" Configuration for:'"+getHostname()+":"+getPort()+"'"; 
    	   setName(name); 
       }
       return name; 
   }
   
   /** Set Server configuration name */ 
   public void setName(String val)
   {
       this.setAttribute(ATTR_SERVER_NAME,val);
   }

   /**
    * Stores copy of  VAttributeSet as-is !
    * Does not do any checking ! 
    */
   public void setServerAttributes(VAttributeSet attrs)
   {
      this._serverAttributes=attrs.duplicate(); 
   }

   public void updateServerAttributesFrom(VAttributeSet resourceAttributes,boolean append)
   {
       // iterate over new attributes: 
       for (VAttribute attr:resourceAttributes)
       {
           String name=attr.getName(); 
           if (this._serverAttributes.containsKey(name))
           {
               VAttribute orgAttr=this.getAttribute(name);
               orgAttr.setValue(attr.getValue()); 
               this._serverAttributes.put(orgAttr); 
           }
           else if (append)
           {
               // add new VAttribute:
               this._serverAttributes.put(attr); 
           }//else skip attribute
       }
   }
   
   /** Whether ServerInfo needs a hostname. */ 
   public void setNeedHostname(boolean val)
   {
     this.setAttribute(ServerInfo.ATTR_NEED_HOSTNAME,val); 
   }
   
   /** Whether Locataion VRL for this service/server needs a hostname. */ 
   public boolean getNeedHostname()
   {
       return this.getBoolProperty(ServerInfo.ATTR_NEED_HOSTNAME,true); 
   }
   

   /** Whether Location VRL needs a port. */ 
   public void setNeedPort(boolean val)
   {
     this.setAttribute(ServerInfo.ATTR_NEED_PORT,val);
   }
   
   /**
    * Whether the location needs Userinfo (username) for authentication
    * purposes.
    *  
    * @see #getNeedUserinfo()
    */ 
   public void setNeedUserinfo(boolean val)
   {
     this.setAttribute(ServerInfo.ATTR_NEED_USERINFO,val);
   }
   
   /**
    * Whether the location needs Userinfo (username) for authentication
    * purposes. This also means that per Userinfo a Server configuration
    * must be stored to allow for multiple accounts on the same server!
    */ 
   public boolean getNeedUserinfo()
   {
       return this.getBoolProperty(ServerInfo.ATTR_NEED_USERINFO,false);
   }
   
   /**
    * Whether Location VRL needs a port. 
    * The default, if not specified is true. */ 
   public boolean getNeedPort()
   {
       return this.getBoolProperty(ServerInfo.ATTR_NEED_PORT,true);    
   }
   
   
   /**
    * Whether URI attributes like ?reference or #fragment are supported. 
    */
   public void setSupportURIAtrributes(boolean val)
   {
       this.setAttribute(ServerInfo.ATTR_SUPPORT_URI_ATTRS,val);
   }
   
   /**
    * Whether URI attributes like ?reference or #fragment are supported in URIs.
    * Default (if not specified) is false.  
    */
   public boolean getSupportURIAttributes()
   {
       return getBoolProperty(ATTR_SUPPORT_URI_ATTRS,false); 
   }

   /** 
    * Default returns "/". 
    * Root path might be different if logical paths and/or file queries are used. 
    * 
    * @return
    */
   public VRL getRootPath()
   {
       String rootPath=getStringProperty(ATTR_ROOT_PATH);
       if (rootPath==null)
           rootPath="/";
       
       VRL rootVrl=getServerVRL().copyWithNewPath(rootPath); 
       
       return rootVrl;
   }


}
