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
 * $Id: VRSContext.java,v 1.25 2011-06-07 15:41:29 ptdeboer Exp $  
 * $Date: 2011-06-07 15:41:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.OrdenedHashtable;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.error.InitializationError;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vfs.VRSTransferManager;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrl.VRLUtil;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrms.MyVLe;
import nl.uva.vlet.vrs.ui.IMasterUI;


/**
 * VRS Context class.
 * <p> 
 * All resources are linked to a (user) VRSContext. 
 * <p> 
 * Also, for use in Grid Services, one context per user must be used to ensure
 * user space separation of authenticated resources.  
 * For example, an authenticated SSH Session may not be shared by other users!  
 * <p>
 * Initialization Dependency: VRSContext -> {Registry,Global}  
 *  
 * @author P.T. de Boer
 */
public class VRSContext implements Serializable 
{
    //Serializable
    private static final long serialVersionUID = -1983193298093366604L;

    ///** Class object for global (=default) context */ 
    //private static VRSContext instance=null;

    private static int instanceCounter=0;

    /** Singleton or default class instance */ 
    private static VRSContext instance=null;

    /**
     * Get default VRS Context environment. It is recommended to share 
     * one VRSContext between classes in the same Application. 
     * Customized VRSContexts are currently used for multi user
     * environments in web and grid services. 
     * 
     * @return The Global VRSContext object from VRS.getDefaultVRSContext() 
     * @throws VlException 
     */
    public static synchronized VRSContext getDefault() 
    {
        if (instance==null)
            instance=new VRSContext();
        return instance; 
         
    }
    
    /** @deprecated: will disappear use instance method fireEvent */  
    public static void staticFireEvent(ResourceEvent event)
    {
        VRS.getRegistry().fireEvent(event); 
    }
    
    // =======================================================================

    // =======================================================================
    
    private int contextID=instanceCounter++; 
    
    private GridProxy _gridProxy=null;

    // static getRegistry() has been moved to VRS 
    //private Registry getRegistry()=null; 

    private VNode virtualRoot=null;
    
    ConfigManager configManager=null; 
    
    // Context Properties ! 
    Properties properties=new Properties();

    /** Alternative User Home */ 
    protected VRL userHomeLocation=null;

    /** Alternative User Home */ 
    protected VRL currentWorkingDir=null;

    protected ServerInfoRegistry serverInfoRegistry=null;  

    /** 
     * Connected ResourceSystems ands Servers. 
     * VRS implementations can store their ResourceSystems 
     * object into this VRSContext for runtime management and caching 
     * of (connected) servers. 
     */ 
    protected OrdenedHashtable<String,VResourceSystem> resourceSystemInstances=new OrdenedHashtable<String,VResourceSystem>();

    /** New Copy Manager to centralize VFS Transfers */  
    private VRSTransferManager vrsTransferManager=null; 

    // =======================================================================
    // Constructor 
    // =======================================================================
    
    /**
     * Create new VRSContext. 
     * This will also initialize the Core VRS class Registry and GridProxy
     * if not initialized alreay.  
     * <p>   
     * @throws VlException 
     */
    public VRSContext() 
    {
        init(); 
    }

    /**
     * This constructor can be used to create a VRSContext without 
     * any default initialization. Use Setters methods to configure 
     * the context manually.
     * <p>
     * @throws VlException 
     */
    public VRSContext(boolean initialize)
    {
        if (initialize==false)
            init(); 
    }

    public int getID()
    {
        return this.contextID; 
    }
    
    protected synchronized void init()
    {   
        // One initilazor For All!
        // When creating a VRSContext. Registry et all must already be initialized
        // to avoid initialization problems. 
        VRS.init(); 
        
        // ============================
        // FIRST create config manager!
        // ============================
        configManager=new ConfigManager(this);
        
        // get Default trigger default initialization
        //getRegistry()  = VRS.getRegistry(); 
        serverInfoRegistry=new ServerInfoRegistry(this);

        vrsTransferManager=new VRSTransferManager(this); 
    }
 
    /** 
     * Returns GridProxy (wrapper) object. Use this object to manipulate Grid Proxies
     * The GridProxy class is a wrapper for globus grid proxies. 
     */ 
    public GridProxy getGridProxy()
    {
        if (_gridProxy==null)
            _gridProxy = GridProxy.createProxy(this); 
        
        
        return _gridProxy;
    }
    
    /**
     * Specify custom Grid Proxy to use for this context.
     * Already authenticated resources won't be updated so use this method
     * during initialization time.   
     */
    public void setGridProxy(GridProxy prox)
    {
        this._gridProxy=prox;

        if (prox!=null)
            // update ownership ! 
            prox.setVRSContext(this); 
    }

    /** 
     * Return initialized grid proxy as string. 
     */ 
    public String getProxyAsString() throws VlException
    {
        return this.getGridProxy().getProxyAsString(); 
    }

    /**
     * Get the Registry object that this Context uses. 
     * This is the Global Registry object from VRS. 
     * 
     * @return Global VRS Registry Object 
     */
    public Registry getRegistry()
    {
        // All VRSContext share the same global VRS Registry ! 
        return VRS.getRegistry(); 
    }

    /**
     * Returns Virtual Root VRL of top level Resource Tree. 
     * Currently returns the MyVLe object 
     */ 
    public VRL getVirtualRootLocation() throws VlException
    {
        return getVirtualRoot().getLocation();
    }

    /**
     * Returns Virtual Root of top level Resource Tree. 
     * Currently returns the MyVLe object but as a VNode. 
     */ 
    public synchronized VNode getVirtualRoot() throws VlException
    {
        // intialize MyVle as default virtual root: 
        if (this.virtualRoot==null)
            this.virtualRoot=getMyVLe();
        
        return virtualRoot; 
    }
    
    /** 
     * Return Top Level Resource if it is MyVle.  
     */ 
    public synchronized MyVLe getMyVLe()
    {
        // if current root resource is MyVle, return that one 
        if (this.virtualRoot instanceof MyVLe)
            return (MyVLe)virtualRoot; 
        
        // To avoid circular initialization, use late initialization: 
        // only initialize default MyVle AFTER 
        // VRSContext has been initialized correctly.
        MyVLe myvle=null; 
        
        if (myvle==null)
        {
            try
            {
                myvle=MyVLe.createVLeRoot(this); 
            }
            catch (VlException e)
            {
                Global.logException(ClassLogger.ERROR,this,e,"Exception during initialization:%s\n",e); 
            }
        }
        return myvle; 
    }

    
    /** 
     * Set new virtual root, make sure to do this directly
     * after creating a new VRSContext, before doing any other 
     * calls. 
     */ 
    public void setVirtualRoot(VNode vnode)
    {
        virtualRoot=vnode;
    }

    /**
     * Get ServerInfo object registered to the specified VRL. 
     * <p> 
     * The Scheme, Hostname and Port are use to query the ServerInfo database. 
     * Optionally the UserInfo, if present in the VRL, is used as well. 
     * If more ServerInfo match, the first is returned.
     * If autoCreate==true a new object will be created but not stored in the Registry ! 
     * @see ServerInfoRegistry 
     */
    public ServerInfo getServerInfoFor(VRL loc,boolean autoCreate) throws VlException
    {
        if (loc==null)
            return null; 
        
        // check existing: 
        ServerInfo info=this.serverInfoRegistry.getServerInfoFor(loc,false); 

        if (info!=null)
        {    
            Global.debugPrintf(this,"Found existing ServerInfo for:%s\n",loc); 
        }

        // AutoCreate Default one 
        if ((info==null) && (autoCreate))
        {
            Global.warnPrintf(this,"No ServerInfo found or created. Creating new DEFAULT ServerInfo for:%s\n",loc); 
            info=this.serverInfoRegistry.getServerInfoFor(loc,true);
        }
        
        info=this.updateServerInfo(info); 
        
        return info;  
    }

    /**
     * Update ServerInfo (or Resource Description). 
     * Calls VRS Implementation updateServerInfo().  
     * This is the only public method to call as ServerInfo is context dependent. 
     *  
     * @throws VlException 
     */
    public ServerInfo updateServerInfo(ServerInfo info) throws VlException
    {
        if (info==null)
            return null; 
        
        String scheme  = info.getScheme();
        VRSContext ctx = this; 
        VRSFactory vrs = ctx.getRegistry().getVRSFactoryForScheme(scheme);

        if (vrs == null)
        {
            Global.warnPrintf(this,"Warning: couldn't get VRS implementation for:%s\n", info);
            return info;
        }

        info=vrs.updateServerInfo(ctx, info, null);
        
        info.store(); 
        
        return info;
    }
    
    /**
     * Forwards ServerInfo query to ServerInfo getRegistry() associated with this Context. 
     * @see ServerInfoRegistry#getServerInfos(String, String, int, String) 
     */ 
    public ServerInfo[] getServerInfosFor(String scheme, String host, int port, String userinfo) 
    {
        return this.serverInfoRegistry.getServerInfos(scheme,host,port,userinfo);  
    }
    
    /** 
     * Perform openLocation using this VRSContext.
     * Replaces static Registry.openLocation(); 
     * @throws VlException */ 
    public VNode openLocation(VRL vrl) throws VlException
    {
        return getRegistry().openLocation(this,vrl); 
    }

    /** 
     * Perform openLocation using this VRSContext.
     * Replaces static Registry.openLocation(); 
     * @throws VlException */ 
    public VNode openLocation(String vrlStr) throws VlException
    {
        return getRegistry().openLocation(this,new VRL(vrlStr)); 
    }

    /** Resolves scheme to unified scheme (gftp->gsiftp) */ 
    public String resolveScheme(String scheme)
    {
        return getRegistry().getDefaultScheme(scheme);  
    }

    /** 
     * Check VRSContext property, if the property hasn't been defined
     * as VRSContext property, GlobalConfig.getProperty() will be called.
     * To check whether the property has been set at the VRSContext only,
     * use getProperty(name,false);  
     * @param name - the property name
     */
    public Object getProperty(String name)
    {
        return getProperty(name,true); 
    }

    /** 
     * Check VRSContext property. If checkGlobal==true the 
     * Global environment will be checked. 
     * If this isn't desired, set checkGlobal to false.
     * @param name - the property name
     * @param checkGlobal - whether global properties can be checked.  
     */
    public Object getProperty(String name,boolean checkGlobal)
    {
        Object val=this.properties.get(name);

        if ((val==null) && (checkGlobal)) 
            return GlobalConfig.getProperty(name);

        return val; 
    }

    /** 
     * Set specified property for this context.
     * Returns previous value. 
     */
    public Object setProperty(String name,Object value)
    {
        return this.properties.put(name,value); 
    }

    /** 
     * Set Persistant User Property. 
     * Saves property in  $HOME/.vletrc/vletrc.prop. 
     */ 
    public void setUserProperty(String name,String value)
    {
        this.getConfigManager().setUserProperty(name,value); 
    }

    /** 
     * Return property as String or returns String representation 
     * of property. 
     * If Property is not defined in this context, Global.getPropery() is called.
     */
    public String getStringProperty(String name)
    {
        Object obj=getProperty(name);
        if (obj==null)
            return null;
        String strval; 

        if (obj instanceof String) 
            strval=(String)obj;
        else 
            strval=obj.toString();

        if (StringUtil.isEmpty(strval))
            return null;

        return strval; 
    }

    /** 
     * Return property as Integer or returns Integer representation 
     * of property. Returns defaultValue is propery is not set 
     * If Property is not defined in this context, Global.getPropery() is called. */
    public int getIntProperty(String name,int defaultVal)
    {
        Object obj=getProperty(name);
        if (obj==null)
            return defaultVal;
        if (obj instanceof Integer) 
            return (Integer)obj;
        else
            // try to parse String:
            return new Integer(obj.toString());  

    }

    /** 
     * Return property as boolean or parses String representation 
     * to boolean
     * 
     * Checks this property store and if not defined in this context, 
     * will check Global property store.
     * 
     * @see #getProperty(String)
     */
    public boolean getBoolProperty(String name,boolean defaultVal)
    {
        Object obj=getProperty(name);

        if (obj==null)
            return defaultVal;
        if (obj instanceof Boolean) 
            return (Boolean)obj;
        else
            // try to parse String:
            return new Boolean(obj.toString()); 
    }

    /**
     * Returns Grid Proxy Subject String. 
     * Returns NULL if proxy hasn't been created  
     */ 
    public String getUserSubject()
    {
        GridProxy prox= getGridProxy();
        
        if (prox!=null)
            return prox.getSubject();

        return null; 
    }

    /** 
     * Whether a host is allowed or blocked. 
     * If not in this list, the parameter 'allowOtherHosts' determines
     * whether non specified hosts are allowed or blocked. 
     */
    Hashtable<String,Boolean> hostAccessList=new Hashtable<String,Boolean>();

    boolean allowOtherHosts=true;

    /** 
     * Whether a scheme is allowed or blocked. 
     * If not in this list, the settings 'allowOtherSchemes' determines
     * whether non specified scheme is allowed or not allowed. 
     */
    Hashtable<String,Boolean> schemeAccessList=new Hashtable<String,Boolean>();

    boolean allowOtherSchemes=true;

    private BdiiService bdiiService;  

    /**
     *  Add host to blocked hosts list in the case allowOtherHosts==true. 
     *  If allowOtherHosts==false, the block list will be ignored and 
     *  all host will be disallowed unless explictly allowed by 
     *  the method allowHost(). 
     *  <p>
     *  Do not trust this method: use Java's own
     *  SecurityManager for secure contexts ! 
     */
    public void blockHost(String hostname)
    {
        if (VRL.isLocalHostname(hostname))
            hostname=VRS.LOCALHOST; 

        hostAccessList.put(hostname,new Boolean(false));
        // store resolved hostname as well ! 
        hostAccessList.put(VRLUtil.resolveHostname(hostname),new Boolean(false)); 
    }

    /**
     *  Add host to allowed hosts list in the case allowOtherHosts==false.
     *  If allowOtherHosts==true, the allow list will be ignored and 
     *  all hosts will be allowed unless explictly blocked by 
     *  the method blockHost(). 
     *  <p>
     *  Do not trust this method: use Java's own
     *  SecurityManager for secure contexts ! 
     */ 
    public void allowHost(String hostname)
    {
        if (VRL.isLocalHostname(hostname))
            hostname=VRS.LOCALHOST; 

        // store alias for faster lookup:
        hostAccessList.put(hostname,new Boolean(true));
        // store resolved hostname as well ! 
        hostAccessList.put(VRLUtil.resolveHostname(hostname),new Boolean(true)); 
    }

    /** 
     * Determines whether hosts not in the block or allow list 
     * are allowed by default. 
     * <br>
     * If allowOtherHosts==true : (Implicit Mode), the block list will be used to
     * determine whether hosts are <strong>blocked</strong> or not.
     * <br> 
     * If allowOtherHosts==false :(Explicit Mode), the allow list will be use 
     * to determine whether hosts are <strong>allowed</strong> or not.
     * <p>
     * Do not trust this method: use Java's own
     * SecurityManager for secure contexts ! 
     */
    public void setAllowOtherHosts(boolean val)
    {
        this.allowOtherHosts=val; 
    }

    /**
     * Returns whether access to host is allowed or not. 
     * Do not trust this method: use Java's own
     * SecurityManager for secure contexts ! 
     */ 
    public boolean isAllowedHost(String hostname)
    {
        if (VRL.isLocalHostname(hostname))
            hostname=VRS.LOCALHOST; 

        boolean hasEntry=this.hostAccessList.containsKey(hostname);
        // check access list: 
        if (hasEntry)
        {
            Boolean val=this.hostAccessList.get(hostname);
            return val; 
        }
        else
            // return default: 
            return this.allowOtherHosts; 
    }

    /**
     *  Add scheme to blocked schemes list in the case allowOtherSchemes==true. 
     *  If allowOtherSchemes==false, the block list will be ignored and 
     *  all schemes will be disallowed unless explictly allowed by 
     *  the method allowScheme(). 
     *  <p>
     * Do not trust this method: use Java's own
     * SecurityManager for secure contexts !
     */ 
    public void blockScheme(String scheme)
    {
        schemeAccessList.put(scheme,new Boolean(false));
    }

    /**
     *  Add scheme to allowed scheme list in the case allowOtherSchemes==false.
     *  If allowOtherSchemes==true, the allow list will be ignored and 
     *  all schemes will be allowed unless explicitly blocked by 
     *  the method blockScheme(). 
     *  <p>
     * Do not trust this method: use Java's own
     * SecurityManager for secure contexts !
     */  
    public void allowScheme(String scheme)
    {
        // store alias for faster lookup:
        schemeAccessList.put(scheme,new Boolean(true));
    }

    /**
     * Returns whether scheme is allowed or not
     * <p>
     * Do not trust this method: use Java's own
     * SecurityManager for secure contexts !
     */  
    public boolean isAllowedScheme(String scheme)
    {
        boolean hasEntry=this.schemeAccessList.containsKey(scheme);
        // check access list: 
        if (hasEntry)
        {
            Boolean val=this.schemeAccessList.get(scheme);
            return val; 
        }
        else
            // return default: 
            return this.allowOtherSchemes; 
    }

    /**
     * Returns path to LOCAL user home. 
     * In service contexts, this MIGHT be a temporary local location to
     * store user settings 
     */

    public String getLocalUserHome()
    {
        return Global.getUserHome();
    }



    /**
     * Get User's Home Location as VRL. 
     * The default location is "file:///"+$HOME (${user.home}) 
     * @return user home location as VRL. 
     */ 
    public VRL getUserHomeLocation()
    {
        if (userHomeLocation!=null) 
            return userHomeLocation;  

        try
        {
            return new VRL("file:///"+getLocalUserHome());
        }
        catch (VRLSyntaxException e)
        {
            // fatal: 
            throw new Error("URI Error:"+e); 
        }  
    }


    /**
     * Set Alternative (User) Home Location. 
     * May be called only once during the lifetime of a VRSContext. 
     * This home might be a grid enabled location to store remote 
     * settings.
     * It is currently used in a Service Context Environment.  
     */ 
    public void setUserHomeLocation(VRL location)
    {
        if (this.userHomeLocation!=null)
            throw new InitializationError("Alternative User Home Location already set !"); 

        this.userHomeLocation=location; 
    }

    /**
     * Get Current Working Directory  
     * The default location is "file:///"+$CWD (startup directory) 
     *  
     * @return 'current' working dir or startup directory as VRL. 
     */ 
    public VRL getWorkingDir()
    {
        // check custom: 

        if (this.currentWorkingDir!=null) 
            return currentWorkingDir;      
        // return global 
        return Global.getStartupWorkingDir(); 
    }

    /**
     * Specify alternative 'current working directory' for
     * relative VRLs */ 
    public void setWorkingDir(VRL vrl)
    {
        this.currentWorkingDir=vrl; 
    }

    /**
     *  Store ServerInfo into the ServerInfoRegistry
     *  @see ServerInfoRegistry 
     */ 
    public ServerInfo storeServerInfo(ServerInfo info)
    {
        return this.serverInfoRegistry.store(info); 
    }

    public ServerInfo removeServerInfo(ServerInfo info)
    {
        return this.serverInfoRegistry.remove(info); 
    }

    /**
     * Registry for the Servers and Server Info descriptions. 
     * Used by MyVLe to create/query Server Objects. */  
    public ServerInfoRegistry getServerInfoRegistry()
    {
        return this.serverInfoRegistry; 
    }

    /** 
     * If a ResourceSystem has been instantiated and stored in the
     * internal instance repository, this method can be used to get that
     * (cached) instance. 
     * The instance must have the specified serverClass. 
     */ 
    public VResourceSystem getServerInstance(String serverid, Class<? extends VResourceSystem> serverClass)
    {
        VResourceSystem server = resourceSystemInstances.get(serverid);

        if (server==null)
            return null; 

        if (serverClass.isInstance(server))     
            return server;
        else
            Global.warnPrintf(this,"Server Mismatch for server '%s'. class<%s>!=<%s> \n",serverid,serverClass,server.getClass());

        return null; 
    }
    
    /** 
     * Return all instances of the specified ResourceSystem. 
     */ 
    public List<VResourceSystem> getServerInstances(Class<? extends VResourceSystem> serverClass)
    {
       List<VResourceSystem> list=new ArrayList<VResourceSystem>(); 
       
       Set<String> keys = resourceSystemInstances.keySet(); 

       for (String key:keys)
       {
           VResourceSystem server = resourceSystemInstances.get(key);
           if (serverClass.isInstance(server))
               list.add(server); 
       }
       return list; 
    }
    
//    public VResourceSystem getServerInstanceFor(VRL resourceVRL)
//    {
//        for (VResourceSystem vrs:this.resourceSystemInstances)
//        {
//            vrs.matches(resourceVRL); 
//        }
//    }

    /** Store server in Context owned instance repository. */ 
    public VResourceSystem putServerInstance(String id,VResourceSystem server)
    {
    	if (this.resourceSystemInstances.get(id)==null)
        	Global.infoPrintf(this, "Storing New Server Instance <%s>:%s \n",server.getClass(),id);
    	else
    		Global.warnPrintf(this, "Warning: Replacing Server Instance <%s>:%s \n",server.getClass(),id);
    	
        return this.resourceSystemInstances.put(id,server); 
    }
   
	public void putServerInstance(VResourceSystem server) 
	{
		this.putServerInstance(server.getID(),server); 
	}
    
    public void removeServerInstance(VResourceSystem server)
    {
        resourceSystemInstances.remove(server); 
    }

    /**
     * Returns normalized or default scheme name for the specifed 
     * scheme. 
     * 
     * @param scheme alias to resolve  
     * @return default scheme 
     */
    public String getDefaultScheme(String scheme)
    {
        return this.getRegistry().getDefaultScheme(scheme);
    }

    /**
     *  Returns all VResourceSystems which have implemented the VNode or VServerNode 
     *  interface and are stored in the Instance registry. 
     *  Currently used for debugging.
     */
    public VNode[] getResourceSystemNodes()
    {
        VResourceSystem[] vsyss = this.resourceSystemInstances.toArray(VResourceSystem.class);
        
        Vector<VNode>nodes=new Vector<VNode>();
        
        if (vsyss!=null)
            for (VResourceSystem vsys:vsyss)
                if (vsys instanceof VNode)
                    nodes.add((VNode)vsys); 
        
        VNode nodeArr[]=new VNode[nodes.size()];
        nodeArr=nodes.toArray(nodeArr);
        return nodeArr; 
    }

    // ===========================================================
    // Resource Factory Methods  
    // ===========================================================
   
    public VRSFactory getResourceFactoryFor(VRL vrl)
    {
        return this.getRegistry().getVRSFactory(vrl.getScheme(),vrl.getHostname());
    }

    public VResourceSystem openResourceSystem(VRL loc) throws VlException
    {
        return this.getRegistry().openResourceSystem(this,loc); 
    }
    
    public VFileSystem openFileSystem(VRL location) throws VlException
    {
        return this.getRegistry().openFileSystem(this,location);  
    }

    // ===========================================================
    // Context Event Handling 
    // ===========================================================
    
    /** Fire a resource event from this context. Use ResourceEvent class to create one. */ 
    public void fireEvent(ResourceEvent event)
    {
        this.getRegistry().getResourceEventNotifier().fire(event); 
    }

    public void addResourceEventListener(ResourceEventListener listener)
    {
        this.getRegistry().getResourceEventNotifier().addListener(listener);
    }

    public void removeResourceEventListener(ResourceEventListener listener)
    {
        this.getRegistry().getResourceEventNotifier().removeListener(listener); 
    }

    /** Returns Global Resource Event notofier. */ 
    public ResourceEventNotifier getResourceEventNotifier()
    {
        return this.getRegistry().getResourceEventNotifier();  
    }
    
    /**
     * Returns Default VO for this Context. 
     * @return Default VO from the Grid Proxy Object. 
     */
    public String getVO()
    {
        return this.getGridProxy().getVOName();  
    }
    
    private Object bdiiMutex=new Object();
    
    /** 
     * Returns BDII Service for this context 
     * @throws VlException
     */ 
    public BdiiService getBdiiService() throws VlException
    {
        synchronized(bdiiMutex)
        {
            boolean newService=false; 
            
            if (this.bdiiService==null)
                 newService=true;
            else 
            {
                // auto update !
                java.net.URI newURI=getConfigManager().getBdiiServiceURI();
                
                if (!StringUtil.equals(newURI.toString(),bdiiService.getURI().toString()))
                    newService=true; 
            }
            
            if (newService)
                this.bdiiService=BdiiService.createService(this);
            
            return bdiiService;
        }
    }

    public String getSystemEnv(String envVar)
    {
        // no context overrided Environment Variables: 
        return GlobalConfig.getSystemEnv(envVar); 
    }
    

    public synchronized ConfigManager getConfigManager()
    {
        if (this.configManager==null)
        {
            configManager=new ConfigManager(this); 
        }
        return configManager; 
    }

    /**
     * Returns UI for this Context. If no UI is configured, a 'Dummy' UI 
     * will be returned. 
     * 
     * @return VBrowser UI or a 'dummy' UI (in the case of a headless environment).  
     */
	public IMasterUI getUI() 
	{
		// get runtime UI ! 
		return VRS.getRegistry().getUI(); 
	}

    public VRSTransferManager getTransferManager()
    {
       return this.vrsTransferManager; 
    }
    
    public void dispose()
    {
        reset(); 
    }
    
    public void reset()
    {
        disposeResourceSystems(); 
    }
    
    /**
     * Dispose and remove registered ResourceSystems 
     */ 
    protected void disposeResourceSystems()
    {
        Set<String> keySet = resourceSystemInstances.keySet();
        String keys[]=new String[keySet.size()]; 
        
        keys=keySet.toArray(keys); 
        
        for (String key:keys)
        {
            VResourceSystem server = resourceSystemInstances.get(key);
            Global.debugPrintf(this,"Disconnecting ResourceSystem:%s::%s\n",key,server); 

            if (server!=null)
                try
                {
                    server.disconnect();
                    server.dispose(); 
                    resourceSystemInstances.remove(key); 
                }
                catch (Exception e)
                {
                    Global.logException(ClassLogger.DEBUG,this,e,"Exception when disconnecting resource server %s\n",server);  
                }
        }
        
    }


}
