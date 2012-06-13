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
 * $Id: PluginLoader.java,v 1.7 2011-06-07 15:13:52 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:52 $
 */ 
// source: 

package nl.uva.vlet.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalUtil;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Load plugin jar or plugin directory.
 * Used by VBrowser (IViewer plugins) and Registry (VRS plugins). PluginLoader
 * scans a plugin directory and add the jars+directories to the custom (plugin)
 * classloader.
 */
public class PluginLoader
{
    private static  ClassLogger logger=null; 
    
    static
    {
        logger=ClassLogger.getLogger(PluginLoader.class);
        // logger.setLevelToDebug(); 
    }
    
	// === Class === //

    public static final String PLUGIN_ENABLED_PROP = "plugin.enabled";
    
    public static final String PLUGIN_DEPENDENCIES_PROP = "plugin.dependencies";
    
    public static final String VDRIVER_NAME_PROP = "vdriver.name";

    public static final String VDRIVER_PACKAGE_PROP = "vdriver.package";

    public static final String VDRIVER_FACTORY_PROP = "vdriver.factory";
    
    public static final String VIEWER_NAME_PROP = "viewer.name";

    public static final String VIEWER_PACKAGE_PROP = "viewer.package";
    
    public static final String VIEWER_CLASS_PROP = "viewer.mainclass";


	/** default class object */
	private static PluginLoader instance;

	/** Plugin Info Object */
	public static class PluginInfo
	{
		/** Short name */
		public String name;

		/** Package name namespace */
		public String packageName;

		/**
		 * Classname (super type) used to initialize class. Might differ from
		 * actual classname which might be a subclass of specified class.
		 */
		public String className;

		/** Loaded Class with own classloader */
		public Class<?> actualClass;

		/** Used classloader which contains private classes for this plugin */
		public PluginClassLoader classLoader;

		/** Location of plugin */
		public URL pluginLocation;

		public boolean isValid;

        public boolean isViewer;

        public boolean enabled;

        public StringList dependencies=null; // null -> no dependencies
	}

	/** Returns default PluginLoader */
	public static synchronized PluginLoader getDefault()
	{
		if (instance == null)
			instance = new PluginLoader();

		return instance;
	}

	// === Instance == //

	/** Default Plugin loader */
	public PluginLoader()
	{

	}

	/** Load plugin from local path */
	public PluginInfo loadLocalPlugin(String localPath)
	{
		// normalize localpath !
		localPath = VRL.uripath(localPath, true, File.separatorChar);
		logger.infoPrintf(" Loading local plugin:%s\n",localPath);

		PluginInfo info = new PluginInfo();

		String subdirName = VRL.basename(localPath);
		String jarSuffix = ".jar";

		// strip .jar from single jar plugin:
		if (localPath.endsWith(".jar"))
		{
			subdirName = subdirName.substring(
			        0, 
			        subdirName.indexOf(jarSuffix,
			                           subdirName.length() - jarSuffix.length())
					);
		}

		PluginClassLoader classLoader = createLocalClassLoader(localPath, true,true);

		Properties pluginProps = readPluginProps(localPath);

		info.classLoader = classLoader;
		info.className = subdirName; // default use subdir name as VRSFactory class name:
		info.isValid = false;
		info.isViewer=false; 
		info.enabled=true; 
		
		if (pluginProps != null)
		{
		    // check VDriver: 
            String pluginClass = pluginProps.getProperty(VDRIVER_FACTORY_PROP);
            String name = pluginProps.getProperty(VDRIVER_NAME_PROP);
            String packageName = pluginProps.getProperty(VDRIVER_PACKAGE_PROP);
            String enabledStr=pluginProps.getProperty(PLUGIN_ENABLED_PROP);
            String depStr=pluginProps.getProperty(PLUGIN_DEPENDENCIES_PROP);
            StringList deps=null;
            if (depStr!=null)
            {
                deps=StringList.createFrom(StringUtil.stripWhiteSpace(depStr),"[ ,]");
                info.dependencies=deps; 
            }
                
            if (enabledStr!=null)
                info.enabled=Boolean.parseBoolean(enabledStr); 
            
            // no vdriver class check viewer:     
            if (StringUtil.isEmpty(pluginClass)==true) 
            {
 
                name = pluginProps.getProperty(VIEWER_NAME_PROP);
                packageName = pluginProps.getProperty(VIEWER_PACKAGE_PROP);
                pluginClass = pluginProps.getProperty(VIEWER_CLASS_PROP);
                
                if (pluginClass!=null)
                    info.isViewer=true;
            }
            
            logger.infoPrintf(" Reading plugin.prop info for:%s\n",localPath);
            logger.infoPrintf(" - Plugin type    = %s\n", (info.isViewer?"Viewer":"VDriver")); 
			logger.infoPrintf(" - Plugin name    = %s\n", name);
			logger.infoPrintf(" - Plugin package = %s\n", packageName);
			logger.infoPrintf(" - Plugin factory = %s\n", pluginClass);

			// update info from plugin.prop:
			info.packageName = packageName;
			info.name = name;

			// use VRSFactory class from plugin.prop !
			if (pluginClass != null)
				info.className = pluginClass;
			else
				logger.warnPrintf("Warning. plugin.prop file does NOT specify Plugin Main class (vdriver.factory= OR viewer.mainclass=):%s\n",
				                  localPath);
		}

		try
		{
			Class<?> pluginClass = classLoader.loadClass(info.className,false);

			if (pluginClass != null)
			{
				info.isValid = true;
				info.actualClass = pluginClass;
			}
			else
			{
				logger.errorPrintf("*** Error: plugin loading failed for:%s\n",info.className);
			}

		}
		catch (ClassNotFoundException e)
		{
			logger.logException(ClassLogger.ERROR,e,"*** Error: Class Not Found in private class scope (check class name?). Plugin loading failed for:%s\n",
							   info.className);
		}

		return info;
	}

	/**
	 * Read optional plugin properties from <plugindir>/plugin.prop can be null
	 * if no properties are found
	 */
	public Properties readPluginProps(String plugindir)
	{
		File propfile = new File(plugindir + File.separator + "plugin.prop");

		if (propfile.exists() == false)
		{
			logger.debugPrintf( "No plugin.prop file found for:%s\n",plugindir);
			return null;
		}

		try
		{
			// use URL loader:
			return GlobalUtil.loadPropertiesFromURL(propfile.toURI().toURL());
		}
		catch (Exception ex)
		{
			logger.debugPrintf("No properties or read failed for plugin.prop file:%s\n",
			                    propfile);
			logger.debugPrintf(">>> Exception=%s\n",ex);
			return null;
		}
	}

	/**
	 * Scan directory adding jars to the classloader and optionally add the
	 * directories themself as well. If recurse==true the method will scan
	 * subdirectories as well.
	 */
	public PluginClassLoader createLocalClassLoader(String pluginPath,
			boolean recurse, boolean addDirs)
	{
		// normalize local path:
		pluginPath = VRL.uripath(pluginPath, true, File.separatorChar);
		logger.infoPrintf(" Creating Plugin Classloader for:%s\n",pluginPath);

		// INIT CLASS:
		// since VRS Registry might not be initialized,
		// VFS can NOT be used here:
		File pluginLoc = new File(pluginPath);
		//String classname = pluginLoc.getName();
		Vector<URL> urls = new Vector<URL>();

		if (pluginLoc.isFile())
		{
			// add single jar plugin
			try
			{
				URL url = pluginLoc.toURI().toURL();
				logger.infoPrintf(" > Adding single plugin jar:%s\n", url);
				urls.add(url);
			}
			catch (MalformedURLException e2)
			{
				Global.logException(ClassLogger.INFO,this,e2,"Error adding url:%s\n",pluginLoc);
			}
		}
		else if (pluginLoc.isDirectory())
		{
		    logger.infoPrintf(" > Adding plugin directory:%s\n",pluginLoc);

			// recursive add Jars+Directories
			addJars(pluginLoc, urls, recurse, addDirs);
		}

		PluginClassLoader urlloader = null;
		URL urlarray[] = new URL[urls.size()];
		urlarray = urls.toArray(urlarray);

		// context class loader:
		ClassLoader parent = Thread.currentThread().getContextClassLoader();
		urlloader = new PluginClassLoader(urlarray, parent);

//		if (urlloader == null)
//		{
//			logger.errorPrintf("NULL URLloader for:%s\n",classname);
//		}

		return urlloader;
	}

	/**
	 * Scan local filepath, add jars and optionally add Directory if
	 * addDirs==true and recurse into subdirectories if recurse==true.
	 * 
	 * @throws VlException
	 */
	public void addJars(File pluginLoc, Vector<URL> urls, boolean recurse,
			boolean addDirs)
	{
		logger.infoPrintf(" - addJars(): Scanning plugin directory:%s\n",pluginLoc);
		// scan directory for jars and create PluginClassLoader:
		String files[] = pluginLoc.list();

		//
		// Convert local path to VRL location for consistency !
		// 
		String uriPath = VRL.uripath(pluginLoc.getPath(), true,
				File.separatorChar);
		VRL baseLocation = new VRL("file", null, uriPath);

		// Add plugin directory (=base location) to classpath.
		try
		{
			urls.add(baseLocation.toURL());
		}
		catch (VRLSyntaxException e1)
		{
			logger.errorPrintf("*** VlURISyntaxException for:%s\n",baseLocation);
		}

		// add contents:
		if (files != null)
		{
			for (String fileName : files)
			{
				try
				{
					// use VRL as URL factory: use full path
					VRL subvrl = new VRL(baseLocation, fileName, false);

					// list entry (local FILE/URL!):
					File subfile = new File(subvrl.getPath());
					//URL suburl = subvrl.toURL();

				    if (subfile.isDirectory())
					{
						// Go into recursion:
						logger.infoPrintf(" - addJars(): recursing into:%s\n", subfile);
						addJars(subfile, urls, recurse, addDirs);
					}
					else
					{
						logger.infoPrintf(" - addJars(): ignoring unknown entry:%s\n",subfile);
					}
				}
				// be robuust: catch all
				catch (Throwable e)
				{
				    logger.logException(ClassLogger.ERROR,e,"*** Error adding directory entry to classLoader:",e); 
				}
			}
			
			// add files:
			
	        for (String fileName : files)
	        {
	                try
	                {
	                    // use VRL as URL factory: use full path
	                    VRL subvrl = new VRL(baseLocation, fileName, false);

	                    // list entry (local FILE/URL!):
	                    File subfile = new File(subvrl.getPath());
	                    URL suburl = subvrl.toURL();

	                    // add jar:
	                    if (subfile.isFile())
	                    {
	                        if (fileName.endsWith(".jar"))
	                        {
	                            urls.add(suburl);
	                            logger.infoPrintf(" - - addJars(): adding custom plugin jar (URL):%s\n"
	                                    ,suburl);
	                            //System.err.println("+++ Adding custom viewer jar:"
	                            // +url);
	                        }
	                        else
	                        {
	                            logger.infoPrintf(" - - addJars(): ignoring file:%s\n",subfile);
	                        }
	                    }
	                }
	                // be robuust: catch all
	                catch (Throwable e)
	                {
	                    logger.logException(ClassLogger.ERROR,e,"*** Error adding directory entry to classLoader:",e); 
	                }
	            }

		}
		else
		{
			// Empty Plugin Directory:
			// Allowed for debug purposes:
			// This wil trigger the classloading of the pluging class the parent
			// class loader.
			// This allows custom plugins to be loaded from the installation
			// if they are installed, but NOT in this directory !
			logger.debugPrintf("*** Warning: Empty plugin directory:%s\n",pluginLoc);
		}
	}

	/**
	 * Scan directory for plugins, load them and filter the correct Class out of
	 * the loaded plugins
	 * 
	 * @param dirPath
	 *            local location of plugins
	 * @param pluginClass
	 *            return only plugins which match the specified class
	 * @return Vector of loaded plugins
	 */
	public Vector<PluginInfo> loadLocalPlugins(String dirPath, Class<?> pluginClass)
	{
		// sanity local file path:
		dirPath = VRL.uripath(dirPath, true, File.separatorChar);

		// use VRL as URL factory
		VRL dirLocation = new VRL("file", null, dirPath);

		File pluginDir = new File(dirPath);

		if ((pluginDir.exists() == false) || (pluginDir.isDirectory() == false))
			return null;

		String names[] = pluginDir.list();

		if ((names == null) || (names.length < -0))
			return null;

		// Check if file is a jar or directory contains
		// implementation.
		// Either the filename is the full package name
		// or the directory is
		if ((names == null) || (names.length <= 0))
			return null;

		Vector<PluginInfo> plugins = new Vector<PluginInfo>();

		for (String name : names)
		{
			try
			{
				// use VRL as path factory
				VRL pluginLocation = new VRL(dirLocation, name);
				// use plugin loader:
				PluginInfo pluginInfo = loadLocalPlugin(pluginLocation.getPath());

				// use isAssignableFrom to check subclass/interface type of
				// Class:
				if (pluginInfo.isValid == true)
				{
					if (pluginClass.isAssignableFrom((pluginInfo.actualClass)))
					{
						logger.debugPrintf("+++ adding correct pluginClass <%s>:=%s\n",pluginClass,pluginInfo.className);
						plugins.add(pluginInfo);
					}
					else
					{
						logger.debugPrintf( "Skipping filtered pluginClass:<%s>!=%s\n",pluginClass,pluginInfo.className);
					}
				}
				else
				{
					logger.errorPrintf("Invalid plugin. Loading failed:%s\n",pluginInfo.className);
				}

			}
			catch (Throwable e)
			{
			    Global.logException(ClassLogger.ERROR,this,e,"Exception when loading/initializing loading plugin:%s\n",name);
			} // END try
		} // END for

		if (plugins.size() == 0)
			return null;

		return plugins;
	}

}
