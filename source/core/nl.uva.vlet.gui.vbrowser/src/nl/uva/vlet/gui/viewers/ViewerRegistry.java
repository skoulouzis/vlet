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
 * $Id: ViewerRegistry.java,v 1.12 2011-06-07 14:31:56 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:56 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.util.Properties;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.OrdenedHashtable;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.viewers.IViewer;
import nl.uva.vlet.util.PluginLoader;
import nl.uva.vlet.util.PluginLoader.PluginInfo;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSFactory;

/**
 * The Viewer Registry.<br>
 * Load and register the IViewer plugins + their mimetypes. 
 * Note that is it allowed to let viewers register mimetypes which have been 
 * registered already. In that case the one registered the last will be 
 * the default to be used when viewing an object of that mimetype.
 * This last registered viewer is the first one to show up (together
 * with the other Viewers) in the Viewer With menu. 
 * 
 * @author P.T. de Boer
 */
public class ViewerRegistry
{
	/** Vector of Viewers */
    public static class ViewerList extends Vector<ViewerInfo> {}; 
    
	public static final String VIEWERSETTINGS_FILE="viewerconf.prop";
	
	/**
	 * Default viewers to register. 
	 * Last viewer registered is the first used. 
	 */  
    public static String defaultViewers[] =
    {
    	"nl.uva.vlet.gui.viewers.HexViewer",
        "nl.uva.vlet.gui.viewers.TextViewer", 
        "nl.uva.vlet.gui.viewers.ImageViewer",
        "nl.uva.vlet.gui.viewers.VHTMLViewer",
        // "nl.uva.vlet.gui.viewers.DefaultImageViewer",
        // Separate plugin(s): 
    	//"nl.uva.vlet.gui.viewers.JPedalPDFViewer",
        "nl.uva.vlet.gui.viewers.grid.replicaviewer.ReplicaEditor", 
    	"nl.uva.vlet.gui.viewers.external.JavaWebStarter",
        "nl.uva.vlet.gui.viewers.x509viewer.ViewerX509",
        "nl.uva.vlet.gui.viewers.VLTermStarter"
    };

   
    /** Single Class Object: single registry per class */
    private static ViewerRegistry _viewerRegistry = new ViewerRegistry();

    public static ViewerRegistry getRegistry()
    {
    	return _viewerRegistry; 
    }
    
    
    // ==================
    // instance 
    // ==================
    
    public ViewerList getAllViewers()
    {
        return viewers;
    }
    
    /** 
     * Get list of viewer which can be standalone tools 
     */ 
    public ViewerList getAllTools()
    {
        ViewerList tools=new ViewerList(); 
        
        for (ViewerInfo viewer:viewers)
        {
            if (viewer.isTool()) 
                tools.add(viewer);
        }
       
        return tools;
    }
    

    public ViewerList getViewerListForMimetype(String mimetype)
    {
        // NiNo
        if (mimetype == null)
            return null;
        
        String prefclassname=getPreferredViewerFor(mimetype);
        
        ViewerInfo prefviewer=getViewerInfoForClass(prefclassname);
        
        ViewerList list=registeredViewers.get(mimetype);
        
        if (prefviewer!=null)
        {
        	if (list==null)
        	{
        		list=new ViewerList();
        	}
        	
        	// remove pref viewer
        	if (list.contains(prefviewer)) 
        		list.remove(prefviewer);
        	
        	// instert as first viewer: 
        	list.insertElementAt(prefviewer,0); 
        }
        
        return list; 
    }

    
    // ==================================================================
    // Methods to produce a Viewer
    // ==================================================================
    
    public ViewerPlugin getViewerInstance(String viewerClass)
    {
        Class<? extends ViewerPlugin> viewerC = getViewerClassFor(null,viewerClass);
        if (viewerC==null)
            return null; 
        return this.instanciateViewer(viewerC); 
    }
    
    public Class<? extends ViewerPlugin> getViewerClass(String viewerClass)
    {
        return getViewerClassFor(null,viewerClass); 
    }
    /** 
     * Find viewer class for mimetype and optional use
     * the proved viewer Class Name 
     */   
    public Class<? extends ViewerPlugin> getViewerClassFor(String mimetype,
            String optionalClassName)
    {
        if ( (optionalClassName == null) && (mimetype==null) )
                return null ;
        
        Class<ViewerPlugin> viewerClass = null;
        ViewerInfo viewerClassInfo=null;
        
        if (optionalClassName == null)
        {
            // get Class for mimetype
            viewerClassInfo=getViewerInfoForMimetype(mimetype); 
        }
        else
        {
            // fetch classname
            viewerClassInfo = getViewerInfoForClass(optionalClassName);
        }
        
        if (viewerClassInfo==null)
        {
            Global.infoPrintf(ViewerRegistry.class,"Couldn't find viewerClass for mimetype:%s (prefClass='%s')\n",mimetype,optionalClassName);
            return null; 
        }

        debug("Found viewerclass:"+viewerClassInfo.getClassName()); 
        debug("viewerclass.urlloader ="+viewerClassInfo.getClassLoader());  
        debug("viewerclass.name      ="+viewerClassInfo.getName()); 
        debug("viewerclass.isTool    ="+viewerClassInfo.isTool()); 
        
        try
        {
            Class<?> vclass;
            
            // update current thread class loader: 
            if (viewerClassInfo.getClassLoader()!=null) 
            {
                // *** DYNAMIC CLASS LOADING *** 
                // Get custom URLClassloader associated with this viewer
                // so that custom class can be loaded. 
                // since this might be another thread, we cannot 
                // use the default classloader from this thread. 
                Thread.currentThread().setContextClassLoader(viewerClassInfo.getClassLoader());
            }
            
            // use thread context class loader:
            vclass = Thread.currentThread().getContextClassLoader()
                        .loadClass(viewerClassInfo.getClassName());
                
            //vclass=viewerRegistry.getClass().getClassLoader().loadClass(viewerClassInfo.classname);
            
            
            viewerClass = (Class<ViewerPlugin>) vclass;
        }
        catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (viewerClass == null)
        {
            debug("couldn't create viewerclass:" + viewerClassInfo.getClassName()); 
            return null;
        }
        
        return viewerClass;
    }
    
    public ViewerPlugin instanciateViewer(Class<? extends ViewerPlugin> viewerClass)
    {
        ViewerPlugin viewer=null; 
        
        try
        {
            viewer = viewerClass.newInstance();
        }
        catch (Exception e)
        {
            Global.errorPrintStacktrace(e); 
        }

        return viewer;
    }

    
    // =======================================================================
    // === Instance Stuff ===
    // =======================================================================
    /** Hastable registers per mimetype a list of viewers */
    private OrdenedHashtable<String, ViewerList> registeredViewers = new OrdenedHashtable<String, ViewerList>();

	private PluginLoader pluginLoader;

    private  ViewerList viewers = new ViewerList();

    /** Private Constructor for Class Object */
    private ViewerRegistry()
    {
        init();
    }
    
    private void init()
    {
    	pluginLoader=new PluginLoader(); 
    	
    	loadPreferredViewers(); 
    	
    	// register default viewers using global classLoader: 
        for (String className : defaultViewers)
            registerViewer(Thread.currentThread().getContextClassLoader(),className);

        VRL extraviewers = Global.getInstallationPluginDir(); 
        
        loadViewerPlugins(extraviewers);

        VRL userviewers = Global.getUserPluginDir();
        loadViewerPlugins(userviewers);
    }

    private void loadPreferredViewers()
	{
    	VRL vrl=Global.getUserConfigDir().appendPath(VIEWERSETTINGS_FILE); 

		try
		{
			this.preferredViewers=UIGlobal.loadProperties(vrl);
			debug("Loaded properties from:"+vrl); 
		}
		catch (VlException e)
		{
			debug("Couldn't load viewer settings:"+vrl); 
		} 
	}

	/* Scan directory for viewer implementations */
    private void loadViewerPlugins(VRL viewersdir)
    {
        try
        {
            VFSClient vfs = new VFSClient();
            
            if (vfs.existsDir(viewersdir)==false)
            {
                Global.debugPrintln(this,"No viewerdirs:"+viewersdir);
                return; 
            }
            
            VDir vdir = vfs.getDir(viewersdir);
            VFSNode nodes[] = vdir.list();

            // Check if file is a jar or directory contains
            // implementation.
            // Either the filename is the full package name
            // or the directory is
            if ((nodes==null) || (nodes.length<=0))
                return; 
            
            for (VFSNode node : nodes)
            {
                try
                {
                	ViewerInfo vinfo=null;
                	// use plugin loader: 
                	PluginInfo pluginInfo = pluginLoader.loadLocalPlugin(node.getPath());
                	
                	// use isAssignableFrom to check subclass/interface type of Class: 
                	if (pluginInfo.actualClass!=null)
                	{
                	    // ===
                	    // Currently must be ViewerPlugin
                	    // IMimeType interface not yet complete ! 
                	    // ====
                	    
                		if (ViewerPlugin.class.isAssignableFrom((pluginInfo.actualClass)))
                		{
                			debug("+++ adding ViewerPlugin plugin:"+pluginInfo.className);
                			vinfo = addViewerPlugin(pluginInfo); 
                		}
                		else if (VRSFactory.class.isAssignableFrom(pluginInfo.actualClass))  
                		{
                			debug("Ignoring VRS plugin :"+pluginInfo.className);
                		}
                		else
                		{
                			Global.errorPrintln(this,"*** Error: Unknown plugin (not VRS Class nor ViewerPlugin Class):"+pluginInfo.className);
                    	}
                	}
                	else 
                	{
                		Global.errorPrintln(this,"*** Error: Unknown plugin loading failed:"+pluginInfo.className);
                	}
                	
                }
                catch (Exception e)
                {
                    Global.errorPrintln(this, "Error loading/initializing viewer:" + node);
                    Global.errorPrintln(this, "Exception = " + e);
                    Global.errorPrintStacktrace(e); 
                }
                catch (Throwable e)
                {
                    Global.errorPrintln(this, "Internal Error loading/initializing viewer:" + node);
                    Global.errorPrintln(this, "Exception = " + e);
                    Global.errorPrintStacktrace(e); 
                }

            } // for 

        }
        catch (VlException e)
        {
            Global.errorPrintln(this, "Error reading viewersdir:" + viewersdir);
            Global.errorPrintln(this, "Exception = " + e);
        }
    }
 
    /**
     * Add Plugin if plugin is of IViwer type. 
     * Will return null if it could add the plugin. 
     * 
     * @param plugin
     * @return
     */
    private ViewerInfo addViewerPlugin(PluginInfo plugin)
    {
    	if (plugin==null)
    		return null; 
    	
        String className=plugin.className; 
          
        return registerViewer(plugin.classLoader,className);
    }
    
    public void registerViewer (Class<? extends ViewerPlugin> clss)
    {
    	registerViewer(clss.getClassLoader(),clss.getCanonicalName().toString());
    }

	public void registerViewer(String clazzname)
	{
		registerViewer(Thread.currentThread().getContextClassLoader(),clazzname); 
	}
	
    public ViewerInfo registerViewer(ClassLoader classLoader,String className)
    {
    	Class viewclass = null;
        info("Registering new (I)Viewer plugin:" +className);

        try
        {
            // use thread context class loader:
            viewclass =classLoader.loadClass(className);

            Object o = null;

            try
            {
                o = viewclass.newInstance(); // create new Object
            }
            catch (InstantiationException e)
            {
                Global.errorPrintln(this,"***Error adding Viewer Class (No public constructor maybe?):" + className
                        + ".\nException:" + e);
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
            	Global.errorPrintln(this,"***Error adding Viewer Class (Private Member/Constructor???) :" + className
                        + ".\nException:" + e);
                e.printStackTrace();
            }

            if (o instanceof ViewerPlugin)
            {
                ViewerPlugin viewer = (ViewerPlugin) o;
                String types[] = viewer.getMimeTypes();
                String viewerName = viewer.getName();
                // add viewer

                ViewerInfo vInfo = new ViewerInfo(classLoader,viewerName, className);
                // later registered viewers are first. 
                // This allows custom viewers to show up first 
                // and moves the default viewers down. 
                
                viewers.insertElementAt(vInfo,0);
                vInfo.isTool=viewer.isTool();
                vInfo.setVersion(viewer.getVersion()); 
                vInfo.setAboutText(viewer.getAbout());
                vInfo.setMimeTypes(viewer.getMimeTypes()); 
                
                for (int i = 0; (types != null) && (i < types.length); i++)
                {
                    String mimetype = types[i];

                    info("Registering mimetype: '"+mimetype+"'-> Viewer:" 
                    		  + className + " ('" + viewerName+"')" );

                    ViewerList vlist = registeredViewers.get(mimetype);

                    if (vlist == null)
                    {
                        // create new viewerlist for this mimetype
                        vlist = new ViewerList();
                        // register new mimetype list
                        registeredViewers.put(mimetype, vlist);
                    }

                    // Add this viewer to mimetype list as first viewer
                    // so that laster registere show up first.  
                    vlist.insertElementAt(vInfo,0);
                    //vlist.add(vInfo); 
                }
                
                // 
                // Add ActionMappings
                //
                
                vInfo.setActionMappings(viewer.getActionMappings());  
                
                // verbose 2= configuration/settings:  
                
                if (vInfo!=null) 
                {
                    Global.infoPrintln(this,"Registered viewer:" + viewerName+":"+className); 
                }
                
                //let viewer keep reference 
                viewer.setViewerInfo(vInfo); 
                return vInfo;
            }
            else if (o==null)
            {
            	Global.errorPrintln(this,"***Error: Failed to add Viewer Class:" + className); 
            }
            else
            {
            	Global.errorPrintln(this,"***Error adding Viewer Class:" + className
                        + "class is NOT of (interface) ViewerPlugin type!");
            }
        }
        catch (ClassNotFoundException e)
        {
            showError(className,e); 
            Global.errorPrintln(this,"***Error could not load viewer class:" + className);
            Global.errorPrintln(this,"***Message="+e.getMessage());
            // e.printStackTrace();
        }
        catch(java.lang.UnsatisfiedLinkError e)
        { 
            showError(className,e); 
            Global.errorPrintln(this,"***Unsatified Link errors for class:" + className); 
            Global.errorPrintln(this,"***Message="+e.getMessage());
            e.printStackTrace();   
        }
        // classloader error ?
        catch (Throwable e)
        {
            showError(className,e); 
            Global.errorPrintln(this,"***Internal error for viewer class:" + className);
            Global.errorPrintln(this,"***Message="+e.getMessage());
            e.printStackTrace();
        }
       
        
        return null; 
    }

    private void showError(String className, Throwable e)
    {
        VlException vle=new VlException(""+e.getClass().getCanonicalName(),
                "Couldn't load viewer:"+className+"\n"
                +"Exception="+e.getClass().getCanonicalName()+"\n"
                +"Reason="+e.getMessage(),e); 
        
        nl.uva.vlet.gui.dialog.ExceptionForm.show(vle); 
    }

 
    public ViewerInfo getViewerInfoForClass(String viewerClassname)
    {
    	if (viewerClassname==null) 
    		return null;
    	
        for (ViewerInfo info:viewers)
        {
            if (info.getClassName().compareTo(viewerClassname)==0) 
                return info;
        }
        
        return null;
    }
    
    /** mimetype=classname preferred viewers property (has) map */ 
    Properties preferredViewers=new Properties(); //empty map;
    
    /** Returns default or preferred viewer type  for mimetype */ 
    private ViewerInfo getViewerInfoForMimetype(String mimetype)
    {
    	String viewerclass=getPreferredViewerFor(mimetype); 
    	
    	if (viewerclass!=null)
    	{
    		  // fetch class
            ViewerInfo info = getViewerInfoForClass(viewerclass);
            
            if (info!=null) 
            {
            	return info;
            }
            else
            {
            	Global.errorPrintln(this,"Could not find preferred viewer:"+viewerclass);
            	// use default
            }
    	}
    	
        ViewerList list = registeredViewers.get(mimetype);
        
        if ((list==null) || (list.size()<=0))
            return null; 

        // return first: 
        return list.get(0); 
    }
    
    private String getPreferredViewerFor(String mimetype)
	{
    	String confstr=(String)preferredViewers.get(mimetype);
    	
    	if (confstr!=null)
    	{
    		// command line style classname + arguments configuration line:
    		String argv[]=confstr.split(" "); 
    		return argv[0]; 
    	}
    		
		return null;
	}

	public Vector<ActionMenuMapping> collectActionMappings()
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
	
		for (ViewerInfo info:viewers)
		{
			Vector<ActionMenuMapping>  maps=info.getActionMappings();
			
			if (maps!=null)
				mappings.addAll(maps); 
		}
		return mappings; 
	}


	
    // === Misc ===

	private void debug(String str)
	{
		Global.debugPrintln(this, str);
	}
	   
	private void info(String str)
	{
		Global.infoPrintln(this, str);
	}


 

}
