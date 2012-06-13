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
 * $Id: Registry.java,v 1.25 2011-11-25 13:40:51 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:51 $
 */ 
// source: 

package nl.uva.vlet.vrs;

// Keep the dependencies of Registry and VRSContext as small as possible !  
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.exception.VlUnsupportedSchemeException;
import nl.uva.vlet.util.PluginLoader;
import nl.uva.vlet.util.PluginLoader.PluginInfo;
import nl.uva.vlet.vdriver.vrs.http.HTTPFactory;
import nl.uva.vlet.vdriver.vrs.http.HTTPSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrl.VRLStreamHandlerFactory;
import nl.uva.vlet.vrms.MyVLe;
import nl.uva.vlet.vrs.ui.IMasterUI;
import nl.uva.vlet.vrs.ui.SimpelUI;


/**
 * Registry to query 'Resources' and/or remote resource systems. 
 * <p>
 * Main registry which holds the registered schemes and associated VRSFactory instances.
 * Per JVM there should be only one Registry.  All access to this class must 
 * be used with a {@link VRSContext}. 
 * Handlers and other factory implementations must use properties and settings from 
 * that context. 
 * <p>
 * The Registry returned registered VRSFactories associated with one or more schemes. 
 * (See {@link VRSFactory}). A VRSFactory can be used to get/create VResourceSystem 
 * instances (See {@link VResourceSystem}) which is a VNode factory class which 
 * produces VNodes ({See {@link VNode}). 
 * <p>
 * Important: This Class also sets the URL.setURLStreamHandlerFactory so 
 * that ALL Supported VRLs can be used as URL. 
 * This way the default Java Resource loaders and streamreaders will use
 * the (default) Registry as stream handlers ! 
 * <br>
 *  @see VRSContext 
 *  @see nl.uva.vlet.vrs.VRSFactory 
 *  @see nl.uva.vlet.vrs.VResourceSystem
 *  @see nl.uva.vlet.vrs.VNode 
 *  @see nl.uva.vlet.vfs.VFileSystem
 *  @see nl.uva.vlet.vfs.VFSFactory 
 *  @see nl.uva.vlet.vfs.VFSNode
 *  @author P.T. de Boer
 */
final public class Registry
{
    private static ClassLogger logger;
    
    /** Class instance ! */ 
    private static Registry instance=null; 
  
    private static boolean globalURLStreamFactoryInitialed=false;

    static
    {
        logger=ClassLogger.getLogger(Registry.class);
        // logger.setLevelToDebug();
        // Class instance initialization is done during (Singleton) Constructor
        // to avoid mutual initialization conflicts at startup!
    }
    
    //private static Registry instance=null;
    
    //===============================================================
    //Class Definiations and fields. 
    //===============================================================

    /** 
     * A SchemeFactoryElement is a {Scheme, VRSFactory} tuple
     * which links a registered scheme part, like "lfn:" or "gftp:", 
     * to the associated VRSFactory. 
     */ 
    static class SchemeFactoryElement
    {
        /** default scheme (VRS might have more!) */ 
        String schemePrefix = null;

        /**
         * Singleton VRSFactory adaptor (VDriver). 
         * At runtime there will only exist one instance per VRSFactory 
         * implementation. 
         */ 
        VRSFactory vrsFactory = null;

        /**
         * Whether this implementation is allowed to be used
         * for *this* scheme. Allows runtime configuration of used schemes. 
         * Since a VRS might have more scheme names, all should be disabled when 
         * a complete VRS adaptor must be disabled! 
         */
        boolean enabled=true;  

        SchemeFactoryElement(String name, VRSFactory implementation)
        {
            this.enabled=true; 
            this.schemePrefix = name;
            this.vrsFactory = implementation;
        }

        /** 
         * @return VRSFactory. Does not return null 
         */
        VRSFactory getImplementation() 
        {
            return vrsFactory;
        }

        String getName()
        {
            return schemePrefix;
        }
    }
    
//    ====================================================================
//    Instance Methods  
//    ====================================================================
  
    /** Cross Context Event notifier! */ 
    private ResourceEventNotifier resourceEventNotifier=null;

    /** 
     * Registered service schemes. Mapping of scheme to registered VRSFactory instance. 
     * The actual mapping is an ArrayList of registered VRSFactory
     * instances to allow multiple mappings to the same scheme.
     * By default the first registered instance is returned from the list. 
     */ 
    private Map <String,ArrayList<SchemeFactoryElement>> registeredSchemes = new HashMap<String,ArrayList<SchemeFactoryElement>>();

    /** List of services. VRSFactories are registered using their class names as key. */ 
    private Map<String,VRSFactory> registeredServices = new HashMap<String,VRSFactory>();
    //private Vector<VRSFactory> registeredServices = new Vector<VRSFactory>();

    /** The Registerd UI. The default UI is a console. */ 
    private IMasterUI masterUI=new SimpelUI(); 

    /**
     * Scheme cache. During profiling it appeared that 
     * resolving to default schemes was done very often. 
     * It is a simple mapping of for example: "gsiftp" -> "gftp".  
     */
    private Hashtable<String, String> defaultSchemes=new Hashtable<String,String>();

    private StringList defaultSchemeList;

    /**
     * Constructs the Registry (class) object. Should be done only once as the
     * Registry is a Singleton class.
     *  
     */
    private Registry()
    {
        logger.infoPrintf(">>> Registry: Singleton instance creation called <<<\n"); 
        initRegistry();
    }

    /** Initializer  */
    private void initRegistry()
    {
        // default notifier: has NO dependencies !!!
        this.resourceEventNotifier=new ResourceEventNotifier(); 
        
        logger.infoPrintf("--- initRegistry() ---\n");
        
        // use default classloader: 
        ClassLoader currentLoader=Thread.currentThread().getContextClassLoader();

        // Initialize default VDriver classes 
        String str=Global.getProperty(GlobalConfig.PROP_INIT_DEFAULT_VDRIVERS);
        if ((str==null) || StringUtil.isTrue(str))
        {
            logger.infoPrintf("Initializing default core vdrivers=%s\n",str);

            // Internal vdrivers: Note the 'vdriver' package !
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vdriver.vrs.infors.InfoRSFactory");
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vdriver.vfs.localfs.LocalFSFactory");
            // Core Implementations: might not work from applet/grid service context !
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vfs.gftp.GftpFSFactory");
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vfs.jcraft.ssh.SftpFSFactory");
            // Internal VFS classes: 
            addVRSDriverClassNoError(currentLoader,HTTPFactory.class.getCanonicalName()); 
            addVRSDriverClassNoError(currentLoader,HTTPSFactory.class.getCanonicalName());
        
            // Duymmy factory which initializes Globus Grid bindings!
            // All Globus dependencies are in a plugin themselves.
            // addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vrs.globusrs.GlobusRSFactory");
           
            // Other VFS/VRS implementations from lib/vdrivers:
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vfs.srm.SRMFSFactory");
            addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vfs.lfc.LFCFSFactory");
            
            // Disabled:
            //addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vfs.srbfs.SrbFSFactory");
            
            // RFTS is now a custom plugin (depends on GT4.1 wscore) !
            //addVRSDriverClassNoError(currentLoader,"nl.uva.vlet.vrs.rfts.RFTSFactory");
        }
        
        // check for additional driver
        str=Global.getProperty(GlobalConfig.PROP_VDRIVERS);
        logger.infoPrintf("Extra vdrivers %s=%s",GlobalConfig.PROP_VDRIVERS,str);

        if ((str==null) || (str.compareTo("")==0))
        {
            logger.debugPrintf("No extra VDrivers from:%s\n",GlobalConfig.PROP_VDRIVERS);
        }
        else
        {
            // use both space and comma to get spaceless driver strings 
            String names[]=str.split("[ ,]");

            for (String name:names)
            {
                logger.infoPrintf("Loading extra VDriver:%s\n",name);
                // method does also error handling:
                addVRSDriverClassNoError(currentLoader,name); 
            }
        }
        
        // now add VRS plugins:
        addLocalVRSPlugins(); 
        //
        // Initialize URL Stream Factory AFTER loading VRS plugins  
        //
        initURLStreamFactory(); 
    }

    private static Object initURLStreamFactoryMutex=new Object(); 
    
    private static void initURLStreamFactory()
    {
        synchronized(initURLStreamFactoryMutex)
        {
            // never initialize twice !  
            if (globalURLStreamFactoryInitialed==true)
                return; 
    
            //  can only ovveride URL factory if NOT in applet mode: 
            if (GlobalConfig.getInitURLStreamFactory()==true)
            {
                //System.err.println("setting URLStreamHandlerFactory ...");
                // After this method, the URL class excepts VRLs as valid URL !
                URL.setURLStreamHandlerFactory(VRLStreamHandlerFactory.getDefault()); 
            }
    
            globalURLStreamFactoryInitialed=true;
        }
    }

    /** Read local plugin directory (Java File!) and register them */  
    private void addLocalVRSPlugins()
    {
        VRL plugDir=Global.getInstallationPluginDir();
        logger.debugPrintf(">>> Load plugins from:%s\n",plugDir); 

        PluginLoader pluginLoader=PluginLoader.getDefault(); 

        // Load plugins but filter VRSFactory classes !
        Vector<PluginInfo> result = pluginLoader.loadLocalPlugins(plugDir.getPath(), VRSFactory.class);

        if (result==null) 
            return; 

        for (PluginInfo info:result)
        {
            logger.debugPrintf("+++ Registering plugin:%s\n",info.className);

            try
            {
                this.addVRSDriverClass(info.classLoader,info.className);
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR,e,"Error loading plugin:%s\n",info.className);
            } 
        }
    }

    /**
     * Get schemes from the VRSFactory add them to
     * the registry. 
     * <p>
     * <b>is synchronized</b>: Method modifies the  Registry !  
     */
    private synchronized void registerSchemeNames(VRSFactory vrs)
    {
        String schemes[] = vrs.getSchemeNames();
        
        if (schemes==null)
            return; 
        
        for (int i = 0; i < schemes.length; i++)
        {
            logger.infoPrintf("Registering scheme: %s -> %s (%s)\n",schemes[i],vrs.getClass(),vrs.getName());
            ArrayList<SchemeFactoryElement> list = registeredSchemes.get(schemes[i]);
           
            if (list==null)
                list=new ArrayList<SchemeFactoryElement>(); // new list  
            
            list.add(new SchemeFactoryElement(schemes[i], vrs));  
            registeredSchemes.put(schemes[i],list); // reput 
        }
    }

    /** Explicit Initialize VDriver class if not yet registered. */ 
    public void initVDriver(Class<? extends VRSFactory > factoryClass) throws Exception
    {
        synchronized(this.registeredServices)
        {
            // check by class name !
            if (this.registeredServices.containsKey(factoryClass.getCanonicalName())) 
                return; 
            else
                this.addVRSDriverClass(factoryClass); 
        }
    }
    
    /** 
     * Register new VRSFactory (VDriver) class to this Registry 
     */ 
    public synchronized boolean addVRSDriverClass(Class<? extends VRSFactory> factoryClass) throws Exception
    {
        // === Construct the service object === //
        Object o=factoryClass.newInstance();
        
        //String className=cls.getCanonicalName(); 
        
        synchronized(this.registeredServices)
        {
            // if (o instanceof VFSFactory)

            if (o instanceof VRSFactory)
            {
                VRSFactory rs = (VRSFactory) o;
                
                registeredServices.put(rs.getClass().getCanonicalName(),rs); 
                registerSchemeNames(rs);
                
                rs.init();
            }
            else
            {
                String msg="Error implementations other then VRSFactory not yet supported:"+o; 
                logger.errorPrintf("%s\n",msg); 
                throw new VlException("VRSException",msg); 
            }
        }
        
        // update scheme cache:

        this.updateDefaultSchemes();

        logger.infoPrintf("Added VRSFactory class:%s\n", factoryClass.getName());
        return true; 
    }

    /** 
     * Register new VRSFActory (VDriver) class to this Registry. Uses specified classLoader
     * to load the 'classname' and registers that VRSFactory. 
     * Is used by the plugin loader. 
     * <p>
     * <b>is synchronized</b>: Method modifies the static Registry !
     */ 
    public synchronized boolean addVRSDriverClass(ClassLoader classLoader,String classname) throws Exception
    {
        // Do no use systemclassloader, this thread might have extra URLs 
        // added to the classPath !
        //Class vfsclass = ClassLoader.getSystemClassLoader().loadClass(classname);

        Class<?> cls=classLoader.loadClass(classname); 
        // runtime check:
        if  (VRSFactory.class.isAssignableFrom(cls)==false)
            throw new nl.uva.vlet.exception.VlInitializationException("Class is not a VRSFactory:"+cls.getCanonicalName()); 
        
        logger.debugPrintf("Registering class:+%s\n",cls.getName());

        
        return this.addVRSDriverClass((Class<? extends VRSFactory>)cls); 
    }

    /**
     * Adds VRSFactory and registers the schemes, but does not throw an exception. 
     * This is used to load custom plugins which might contain errors. 
     * <p>
     * <b>is synchronized</b>: Method modifies the static Registry !
     * @return returns false if registrations failed, but will continue!   
     */
    private synchronized boolean addVRSDriverClassNoError(ClassLoader classLoader, String classname)
    {
        // Experimental VFS plugin Class Loader 

        try
        {
            return addVRSDriverClass(classLoader,classname); 
        }
        // Check'd and encountered them all I have:
        catch (NoClassDefFoundError e)
        {
            logger.logException(ClassLogger.ERROR,e,"Couldn't init class (failed dependency):%s\n",classname);
        }
        catch (ClassNotFoundException e)
        {
            logger.logException(ClassLogger.ERROR,e,"ClassNotFound exception for:%s\n",classname);
        }
        catch (SecurityException e)
        {
            logger.logException(ClassLogger.ERROR,e,"SecurityException. Probably couldn't access class: %s\n",classname);
        }
        catch (IllegalArgumentException e)
        {
            logger.logException(ClassLogger.ERROR,e,"IllegalArgumentException for class:%s\n",classname);
        }
        catch (IllegalAccessException e)
        {
            logger.logException(ClassLogger.ERROR,e,"IllegalAccesException (private class?) for class:%s\n",classname);
        }
        catch (InstantiationException e)
        {
            logger.logException(ClassLogger.ERROR,e,
                    "InstanttionException (no public constructor?) for class:%s\n",classname);
        }
        catch (Throwable t)
        {
            logger.logException(ClassLogger.ERROR,t,
                    "Error (no public constructor?) for class:%s\n",classname);
        }
        
        return false; 
    }

    /** 
     *Find VRSFActory for the specifed VRL. 
     */
    protected VRSFactory getVRSFactoryFor(VRL loc) throws VlException
    {
        if (loc==null) 
            return null; 

        return getVRSFactory(loc.getScheme(), loc.getHostname());
    }

    /**
     * Searches the registry for the specified scheme. 
     * Note: searches the type registry from last to first, making
     * it possible to overide default types. 
     * 
     * @param schemeStr scheme part in URI to find a factory for. 
     */
    public VRSFactory getVRSFactoryForScheme(String schemeStr)
    {
        return getVRSFactory(schemeStr, null);
    }
    
    /**
     * Get VRSFactory for specified scheme and hostname. 
     * This method allow to find a scheme for a specified hostname
     * if the registry has entries registered specifically 
     * for this hostname.  
     */ 
    protected VRSFactory getVRSFactory(String schemeStr, String hostname)
    {
        // Null in => Null out; 
        if (schemeStr == null)
        {
            logger.debugPrintf("***Warning: NULL Scheme type!\n");
            return null;
        }
        
        // get Registered VRSFactory instances: 
        ArrayList<SchemeFactoryElement> list = null;
        synchronized(this.registeredSchemes)
        {
            list=this.registeredSchemes.get(schemeStr);
        }
        
        if ((list==null) || (list.size()<-0))
        { 
            logger.infoPrintf("No VRSFactory implementation found for scheme:%s", schemeStr);
            return null;
        }
        
        SchemeFactoryElement vrs = list.get(0);// return first registered scheme. 
        logger.debugPrintf("returning scheme '%s' => (VRSFactory)%s\n",schemeStr,vrs.getImplementation().getName());
        return vrs.vrsFactory;
        
    }
    
    /** Returns actual list of VRSFactory instances */
    private VRSFactory[] getRegisteredServices()
    {
        synchronized(this.registeredServices)
        {
            // returns INSTANCES ! 
            Collection<VRSFactory> clsSet = this.registeredServices.values();
            VRSFactory vrsArr[]=new VRSFactory[clsSet.size()];
            vrsArr=clsSet.toArray(vrsArr);
            return vrsArr; 
        }
    }
    
    /** Find registered VRS Implementation by using it's symbolic name like "GridFTP" */ 
    public VRSFactory getVRSFactoryWithName(String name)
    {
        for (VRSFactory vrs:getRegisteredServices())
        {
            if (vrs.getName().compareTo(name)==0)
                return vrs;
        }
        return null;
    }

    /**
     *  Registry openLocation, this is the master openLocation which uses the VRSContext
     *  to find a VRSFactory and returns the resource specified by the VRL. 
     *  <p>
     *  Is protected, may only be called from a valid VRSContext.
     *  use VRSClient or VFSClient to open locations(). 
     */ 
    protected VNode openLocation(VRSContext context, VRL location) throws VlException
    {
        if (location==null)
            throw new nl.uva.vlet.exception.VlInternalError("Location parameter can not be null");
        
        if (context==null)
            throw new nl.uva.vlet.exception.VlInternalError("VRSContext parameter can not be null"); 
        
        String scheme=location.getScheme(); 
        
        if (scheme.compareTo(VRS.MYVLE_SCHEME)==0)
            return MyVLe.openLocation(context,location); // getVLeRoot(); 
    
        VResourceSystem rs = this.openResourceSystem(context, location);
        
        if (rs==null)
            throw new VlInternalError("Couldn't get/create ResourceSystem for:"+location); 
        
        return rs.openLocation(location); 
    }
      
    /**
     * Create or find VResourceSystem instance which can provides resources specified by 
     * the VRL.
     * The VResourceSystem is created by the registered VRSFactory.  
     * For FileSystem schemes this will return a VFileSystem instance. 
     */  
    protected VResourceSystem openResourceSystem(VRSContext context, VRL location) throws VlException
    {
        logger.debugPrintf("Getting location:%s\n", location);

        if (location == null)
            return null;

        // Not The Place: resolve against user's home: 
        //if (location.isRelative()==true)
        // {
        //     VRL home=context.getUserHomeLocation(); 
        //     location=home.resolve(location); 
        // }

        // *** Service Security ***
        // Check whether host and scheme is allowed 
        // Must set Java Security Context as Well !!! 
        // ************************

        String hostname=location.getHostname();

        if (context.isAllowedHost(hostname)==false)
        {
            throw new nl.uva.vlet.exception.ResourceAccessDeniedException(
                    "Blocked Host: Acces to the remote host is denied by security settings:"+hostname);
        }

        String scheme=location.getScheme();
        
        if (scheme==null)
        {
            throw new VlException("Invalid location. No scheme for:"+location);   
        }

        if (context.isAllowedScheme(scheme)==false)
        {
            throw new nl.uva.vlet.exception.ResourceAccessDeniedException(
                    "Blocked Schemes: scheme is denied by security settings:"+scheme);
        }

    
        VRSFactory rsFactory = getVRSFactoryForScheme(scheme);

        if (rsFactory == null)
        {
            throw new VlUnsupportedSchemeException("No service registered for protocol:"
                    + scheme);
        }

        return rsFactory.openResourceSystem(context,location);
    }


    public VFileSystem openFileSystem(VRSContext context,VRL location) throws VlException
    {
        VResourceSystem vrs= this.openResourceSystem(context,location); 
       
        if (vrs instanceof VFileSystem)
        {
            return (VFileSystem)vrs; 
        }
        
        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Location is NOT a fileystem:"+location); 
    }
  
    /**
     * When more then one scheme names are allowed (for example: gftp/gsiftp)
     * this method returns the default scheme for the given name. 
     * The default scheme is the first one which is
     * return from VRSFactory.getSchemes(); 
     * @param scheme or scheme alias
     * @return default scheme
     */
    public String getDefaultScheme(String scheme)
    {
        if (scheme==null)
            return null;  
        
        // use cache for speed: 
        String newscheme=defaultSchemes.get(scheme); 

        if (newscheme==null)
        {
            //Global.errorPrintln(registry,"Warning: scheme not in cache:"+scheme); 
            return scheme;
        }

        return newscheme;
    }

    private void updateDefaultSchemes()
    {
        // use registeredServices as MUTEX: 
        
        synchronized (registeredServices)
        {
            if (defaultSchemes==null)
                defaultSchemes=new Hashtable<String,String>(); 

            if (defaultSchemeList==null)
                defaultSchemeList=new StringList(); 
            else
                defaultSchemeList.clear(); 

            
            for (VRSFactory vrs:getRegisteredServices())
            {
                String schemes[] = vrs.getSchemeNames();
                if (schemes!=null)
                {
                    String defScheme=schemes[0];
                    for (String scheme:schemes)
                        defaultSchemes.put(scheme,defScheme);
                    
                    this.defaultSchemeList.add(defScheme) ; 
                }
            }
        }
    }

    /**
     * Returns array of default scheme names for the registered protocols. 
     * One scheme name per protocol.  
     */
    public String[] getDefaultSchemeNames()
    {
        if (this.defaultSchemeList==null)
            this.updateDefaultSchemes(); 
        
        return this.defaultSchemeList.toArray(); 
    }

    /** Clean up resources. Calls clear() method */ 
    public void dispose()
    {
        this.resourceEventNotifier.dispose(); 
        clear(); 
    }

    /**
     * The clear is similar to 'dispose' except
     * that after the 'cleanup' execution might 
     * continue. 
     */
    public void clear() 
    {
        for (VRSFactory vrs:getRegisteredServices())
        { 
            logger.warnPrintf("*** Clearing VRSFactory: %s\n",vrs);
            vrs.clear(); 
        }

        registeredServices.clear();
        registeredSchemes.clear(); 
        defaultSchemes.clear(); 
    }


    /** Returns array of registered services */ 
    public VRSFactory[] getServices()
    {
        // private implementation:
        return this.getRegisteredServices(); 
    }
    
    /** Collect actionmappings from registered services */ 
    public Vector<ActionMenuMapping> getActionMappings() 
    {
         Vector<ActionMenuMapping> mappings=new  Vector<ActionMenuMapping>();
         
         synchronized(registeredServices)
         {
            for (VRSFactory vrs:getRegisteredServices())
            {        
                Vector<ActionMenuMapping> maps=vrs.getActionMenuMappings();
                
                if (maps!=null)
                {
                    for (ActionMenuMapping map:maps)
                    {
                        map.setVRS(vrs); // update parent VRS !
                        mappings.add(map); 
                    }
                }
            }
        }
        
        return mappings; 
    }
    
    public synchronized void reset()
    {
        clear(); 
        initRegistry(); 
    }

    /**
     * Always returns Master UI object. Can be dummy object in headless
     * or service environments ! 
     */ 
    public IMasterUI getUI() 
    {
        return this.masterUI; 
    }

    /**
     * Get registered VRSFactory. vrsClass is the Canonical Class name
     * of the VRSFactory. 
     */
    public VRSFactory getVRSFactoryClass(String vrsClass)
    {
        return this.registeredServices.get(vrsClass); 
    }

    //===========================================================================
    // Event handlers 
    // ===========================================================================

    public void fireEvent(ResourceEvent event)
    {
        this.resourceEventNotifier.fire(event); 
    }

    public ResourceEventNotifier getResourceEventNotifier()
    {
        return this.resourceEventNotifier; 
    }
    
    //===========================================================================
    // Class (static) methods
    // ===========================================================================

    public static boolean isLocalLocation(VRL location)
    {
        return location.isLocalHostname(); 
    }
    
    /**
     * In the case auto class initialisation does not work, 
     * call this init method to initialize the class
     */ 
    public static void init() 
    {
        Global.init(); 
    }
    
    private static Object singletonLock=new Object();  
    private static boolean instanceCreation=false; 
    
    /**
     * Creates and returns the singlon instance. 
     * Only once instance is allowed.
     *  
     * @return the Singleton Registry Instance. 
     */
    static Registry getInstance()
    {
        try
        {
            synchronized(singletonLock)
            {
                if (instance==null)
                {
                    // ============================================================
                    // This can happen when during initailization time this
                    // method is called again. within the same Thread ! 
                    // Because of the 'synchronised' modifier, 
                    // this must be the same thread, so this is a recursive call!
                    // This must be avoided at all time. 
                    //   
                    // ============================================================
                    if (instanceCreation==true)
                    {   
                        logger.fatal("*** PANIC: Recursive Registry Initialisation. Cannot continue!\n"); 
                        instanceCreation=false; //Release Lock ! 
                        throw new Error("Recursive Creation of Singleton Instance detected"); 
                    }
                    
                    instanceCreation=true; 
                }
                
                instance=new Registry();
                instanceCreation=false; //Release Lock ! 
            }                
        }
        catch (Throwable t)
        {
            logger.logException(ClassLogger.FATAL,t,"*** PANIC: Can not initialise Registry! Exception="+t); 
            t.printStackTrace();
            instanceCreation=false;
        }        
        return instance; 
    }
  
}
