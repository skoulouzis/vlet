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
 * $Id: Bootstrapper.java,v 1.1 2011-11-25 16:15:48 ptdeboer Exp $  
 * $Date: 2011-11-25 16:15:48 $
 */ 
// source: 

package nl.uva.vlet.bootstrap;
// ====================
// NO VLET IMPORTS HERE 
// ====================
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * Universal Bootstrapper class for both Windows and Linux. 
 *  
 * This class must be able work as a standalone class !  
 * Cannot reference other (vlet) classes in this package ! 
 *   
 * @author Piter T. de Boer
 *
 */
// must be java 1.5 compatible: surpress ALL warnings:
@SuppressWarnings("unchecked") 
public class Bootstrapper
{
	public static class BooleanHolder{public boolean value=false;}; 
	
	public static final String PLUGIN_SUBDIR="plugins"; 
	
   
    private static final Class[] MAIN_PARAMS_TYPE =
    	{ String[].class };
   
    // Local Copies of properties, cannot reference Global ! 
    
    public static final String VLETRC_PROP_FILE         = "vletrc.prop";
    
    public static final String VLET_INSTALL_PROP        = "vlet.install";
    
    public static final String VLET_SYSCONFDIR_PROP     = "vlet.install.sysconfdir";

    public static final String VLET_INSTALL_LIBDIR_PROP = "vlet.install.libdir";
    
    public static final String JAVA_LIBRARY_PATH_PROP   = "java.library.path";

    private static final String GLOBUS_LOCATION_PROP = "globus.location";

	
    // Don't use generics here as the bootstrapper must be java 1.4 compatible: 
   
    private ArrayList classpathUrls = new ArrayList();

    /**
     * vlet install or startup dir (minus ./lib or ./bin)
     * Use URL: be applet/webstart compatible
     */
    
    private URL baseUrl=null; 
    
    private static boolean debug=false;  
    
    public Bootstrapper() 
    {
    	// when run with -Ddebug=<whatever>, show debug:
        debug=(System.getProperty("debug")!=null);
        if (!debug)
            debug=(System.getProperty("DEBUG")!=null);
    }

    /** 
     * Checks startup environment and set installation parameters:<br>
     * - Check java 1.5 version.<br>
     * - Add all .jar files from ./lib and ${globus.install}/lib.<br>
     * - Sets skeleton CLASSPATH adding:
     * <pre> 
     * <li> ./    ; VLET_INSTALL    ${vlet.install} 
     * <li> ./etc ; VLET_SYSCONFDIR ${vlet.install.sysconfdir} 
     * <li> ./lib ;                 ${vlet.install.libdir} 
     * <li> ./lib/linux ;           ${vlet.install.libdir}/linux
     * <li> ./lib/win32 ;           ${vlet.install.libdir}/win32
     * </pre>    
     *<p>
      Bootstrap configuration:<br> 
      0) Assume default VLET_INSTALL using environment variable 
         VLET_INSTALL or (if not set) by stripping ./lib or ./bin from 
         startup path.<br> 
      1) check property "vlet.install.sysconfdir" for configuration.<br>
      -1a) if not set, check VLET_INSTALL and specify sysconfdir 
              as ${VLET_INSTALL}/etc<br>
      2) load configuration file from ${vlet.install.sysconfigdir}/vletrc.prop<br>
      -2a) when not found, try to load /etc/vletrc.prop<br> 
      3) (re)set installation location taken from vletrc.prop, overriding 
         previous settings or keep defaults (this allows to keep defaults
         without setting the properties explicitly in vlerc.prop)<br>
      */
    
	public void checkSetVLeTEnvironment() throws Exception
    {
        // *** 
        // java 1.5 check
        // This class will be compiled as 1.4 jvm binary, 
        // so *here* it is possible to check for java 1.5
        // (TODO: integrated jre1.5 installer !)
		// *** 
		
        String versionStr=System.getProperty("java.version");
        Debug("java version="+versionStr);
        
        // Warning: doing string compare where int compare should be used: 
        if ((versionStr!=null) && (versionStr.compareToIgnoreCase("1.6")<0)) 
        {
        	System.err.println("bootstrapper: Wrong java version. Need at least 1.6. This is:"+versionStr);
        	
            JOptionPane.showMessageDialog(null,
                    "Wrong java version. Need 1.6 or higher.\n"
                    +"If java 1.6 is installed " 
                    +"set your JAVA_HOME to the right location.\n" 
                    +"This version  ="+versionStr+"\n"
                    +"Java location ="+System.getProperty("java.home")+"\nThe Program will try to continue..."
                    ,"Error",JOptionPane.ERROR_MESSAGE);
            
            // Continue!
        }
        
        // java property: -Dvlet.install= 
    	String installDir=System.getProperty(VLET_INSTALL_PROP);
        
        // envvar: VLET_INSTALL=...
    	if (installDir==null) 
            installDir=System.getProperty("VLET_INSTALL");
        
    	String urlstr=null;
        
        // *** 
      	// Get default installation directory as URL 
        // *** 
        
    	if ((installDir==null) && (baseUrl==null)) 
        {
            // *** 
            // Important: URI/URL uses forward slashes, native path must use File.separator ! 
            // *** 
        
            
    		// getProtectionDomain???
        	 URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        	 Debug("source location="+ url); 
             
        	 // parent dir of code base location
        	 // URL has FORWARD slashes! 
             urlstr=url.toString(); 
             
             
             int len=urlstr.length(); 
             int i=0;
             
             // dirname of path:
             for (i=len-1;((i>0) && (urlstr.charAt(i)!='/'));i--); 
            	  // nop; 
             
             urlstr=urlstr.substring(0,i); // 0 to position of path seperator
             
             len=urlstr.length(); 
             
             // auto strip 'lib' from path when started directly from ./lib/...
             if (urlstr.endsWith("/lib"))
             {
            	 urlstr=urlstr.substring(0,len-4);  
             }
             // auto strip 'lib' from path when started directly from ./lib/...
             else if (urlstr.endsWith("/bin"))
             {
            	 urlstr=urlstr.substring(0,len-4);  
             }
             
             // update installDir
             baseUrl=new URL(urlstr); 
         
        }
        else
        {
            // create URL 
            baseUrl=new File(installDir).toURI().toURL(); //unify File location
        }
        
        // get decoded & unified path using URI class: 
        installDir=new URI(baseUrl.toString()).getPath();
        
        Debug("installdir (unified) ="+installDir);  
        
        // ***
        // Check vlet.install.sysconfdir (if speficied) 
        // *** 
         
        String sysconfdir=System.getProperty(VLET_SYSCONFDIR_PROP);
        
        if (sysconfdir==null)
        {
            // NATIVE File seperator:
            sysconfdir=installDir+File.separator+"etc";
        }
        
        // load vlertc.prop from VLET_INSTALL/etc or VLET_SYSCONFDIR or vletrc.prop 
    	Properties vletProps=getVletProperties(sysconfdir);
        
        // 
        // ***
        // Check whether vletrc.prop defines new installation directores!  
        // ***
        //
        
        // alternative vlet.install directory !  
        String val=vletProps.getProperty(VLET_INSTALL_PROP);
        
        if (val!=null) 
            installDir=val; 
        
        // alternative lib directory ! (/usr/share/lib/vlet) 
        String libDir=vletProps.getProperty(VLET_INSTALL_LIBDIR_PROP); 
            
        // default to VLET_INSTALL/lib 
        if (libDir==null) 
            libDir=installDir+File.separator+"lib";
        
        // Warning: this will change sysconfDir if not the same as specified at startup ! 
        String sysconfDir=vletProps.getProperty(VLET_SYSCONFDIR_PROP);
        
        // default to VLET_INSTALL/etc 
        if (sysconfDir==null) 
            sysconfDir=installDir+File.separator+"etc";
        
        
        // Consistency: make sure System property vlet.install.sysconfdir & vlet.install 
        // matches the one dymically configured sysconfDir ! 
        // (is rare, this means a misconfigured system!)
        System.setProperty(VLET_SYSCONFDIR_PROP,sysconfDir);
        System.setProperty(VLET_INSTALL_PROP,installDir);

        Debug("configured installdir ="+installDir);
        Debug("sysconfDir (local)    ="+sysconfDir);  
        Debug("libDir (local)        ="+libDir);
        
        //***
        //  Set GLOBUS_LOCATION (needed for RFT client classes) 
        //***
        
    	//String globusStr=System.getProperty("GLOBUS_LOCATION");
        String globus_location=vletProps.getProperty(GLOBUS_LOCATION_PROP); 
        val=null; 
        
        // whether globus libraries are subdirectory of LIBDIR 
        //boolean globusLocationIsSubDir=false; 
        String abs_globus_location=null;  
        
        // ***
        // GLOBUS_LOCATION/globus.location 
        // ***
        BooleanHolder globusLocationIsSubDir=new BooleanHolder(); 
        
    	if (globus_location!=null)
        {
            Debug("globus.location="+globus_location);
            
            abs_globus_location=resolve(libDir,globus_location,globusLocationIsSubDir); 
            
            // make sure the properties are correct : 
            System.setProperty("GLOBUS_LOCATION",abs_globus_location);
            // make sure the properties are correct : 
            System.setProperty(GLOBUS_LOCATION_PROP,abs_globus_location);
            Debug("using GLOBUS_LOCATION="+abs_globus_location);
        }
    	
        
        // -------------------------------------------------------------------
        /*
         * Current 'skeleton' classpath/
         *   
         * All other classpath directories should start from this skeleton
         * (For example the ./viewers, ./applets, ./icons, directories)
         *  
         * For example to load a viewer specify "viewers/myviewer.jar".  
         * When started from windows, the ./lib/win32/viewers directory should 
         * match also as with ./lib/viewers (for pure java viewers) as with 
         * ./lib/win32/viewers) for windows only viewers !. 
         *  
         * CLASSPATH=$VLET_INSTALL:$VLET_INSTALL/etc:...
         */
        //---------------------------------------------------------------------
        
        // ClassPath directories (defaults): 
        // ./
        // ./etc/
        // ./lib/
 		// ./lib/icons/
        // ./lib/win32
        // ./lib/linux
 	
 		// Add CLASSPATH Environment (bug: new classloader doesn't respect CLASSPATH) 
 		String cpstr=System.getenv("CLASSPATH");
 		if (cpstr!=null) 
 		{
 			String paths[]=cpstr.split(File.pathSeparator);
 			if ((paths!=null) && (paths.length>0))
 				for (int i=0;i<paths.length;i++)
 					classpathUrls.add(new URL("file:///" + paths[i]));
 		}
 			
 		addDirToClasspath(installDir+ "/");  // VLET_INSTALL
          // add directory structure (without jars!)
 		addDirToClasspath(sysconfDir+"/");   // VLET_INSTALL/etc
 		addDirToClasspath(libDir+"/");       // VLET_INSTALL/lib
 		addDirToClasspath(libDir+"/icons");  // VLET_INSTALL/lib/icons
 		addDirToClasspath(libDir+"/win32/"); // if Windows
 		addDirToClasspath(libDir+"/linux/"); // if Linux
        
          // recursive read jars from: 
         addJarsToLibUrls(libDir,true,0);
         
         // add jars from globus if globus location is NOT a subdir from LIBDIR 
         //if (globusLocationIsSubDir==false) 
         //    rescursiveAddJars(abs_globus_location);
         
         // *** 
         // Add java.libary.path  
         // *** 
         setJavaLibraryPath(vletProps); 
    }
   	 
    private String resolve(String libDir,String rel_path,BooleanHolder bool) 
    {
        String path=null;
        
        if (bool!=null)
        	bool.value=false; // default 
        
		if (rel_path.startsWith("/"))
        {
            path=rel_path; 
        }
		// Arg: Windows hack, detect 'c:/blah'
        else if (rel_path.charAt(1)==':')
        {
            path=rel_path; 
        }
        else if (rel_path.startsWith("lib"))
        {
            // Replace "./lib"  with "${vlet.install.libdir} ! 
            path=libDir
                 +File.separator
                 +rel_path.substring(4,rel_path.length());
        }
        else
        {
            // relative path (without "lib" prefix), but starting from LIBDIR !
            path=libDir+File.separator+rel_path;
            
            if (bool!=null)
            	bool.value=true;
        }
		
        return path;
    }

    private void setJavaLibraryPath(Properties props)
    {
        // check system: 
        String java_library_path=System.getProperty(JAVA_LIBRARY_PATH_PROP);
        
        if (java_library_path==null) 
            java_library_path=""; 
        else
        {
            // use platform specific path seperator ! 
            java_library_path+=File.pathSeparator; 
        }

        String val=props.getProperty(JAVA_LIBRARY_PATH_PROP);
        if (val!=null) 
            java_library_path+=val;
        
        // Setting the java.library.path here still doesn't work ! 
        // Keeping the code, anyway. 
        // java.library.path or LD_LIBRARY_PATH has to be specified
        // at JVM startup. :-(. 
        
        System.setProperty(JAVA_LIBRARY_PATH_PROP,java_library_path);
        
        Debug(">>>"+JAVA_LIBRARY_PATH_PROP+"="+java_library_path); 
    }
    
    /** 
     * Check for vlerrc.prop in SYSCONFDIR.
     * 
     * (Might be VLET_INSTALL/etc/vlerc.prop or VLET_SYSCONFDIR/etc/vletrc.prop
     * As a last resort, will try to load /etc/vletrc.prop
     * @returns property set if loaded or EMPTY property set when failed !
     *          (This to avoid null pointer checking)  
     *  
     */ 
    
    private Properties getVletProperties(String sysconfdir)
    {
        Properties props=new Properties();
       
        // use NATIVE File.sepator ! 
        // Default $VLET_INSTALL/etc/vletrc.prop 
        String vletrcprop=sysconfdir+File.separator+VLETRC_PROP_FILE;
        
        try
        {
            File rcfile=new File(vletrcprop);
            FileInputStream inpfs;

            
            if (rcfile.exists()==true) 
            {
                inpfs = new FileInputStream(rcfile);
                props.load(inpfs);
            }
            else
            {
                // last resort: Check /etc/vletrc.prop under unix style path :/etc/
                rcfile=new File("/etc/"+VLETRC_PROP_FILE); 
                if (rcfile.exists()==true) 
                {
                    inpfs = new FileInputStream(rcfile);
                    props.load(inpfs);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.err.println("***Error: Exception:"+e); 
            e.printStackTrace();
        } 
      
        return props; 
    }

    private File getDirectory(String dirstr) throws Exception 
    {
    	// if dir is a remote url,  FILE will complain: 
    	
        File dir = new File(dirstr);
        
        // do some sanity checks: 
        
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead())
        {
        	 JOptionPane.showMessageDialog(null,
                     "Cannot find directory:'"+dirstr+"' "
                     +"Installation might be corrupt or misconfigured.",
                     "Error",JOptionPane.ERROR_MESSAGE);
            throw new Exception("BootrapException:Directory does not exist or is unreadable: "+dirstr); 
        }
        try
        {
            return dir.getCanonicalFile();
        }
        catch (IOException e)
        {
            throw new Exception("IOException:"+
                    "Failed to get the canonical path of of " + dir);
        }
    }

    public void addDirToClasspath(String dir)
    {
    	//globus convention
        if (dir.endsWith("/") == false)
            dir = dir + "/";

        Debug("Adding directory:" + dir);
        
        try
        {
            this.classpathUrls.add(new URL("file:///" + dir));
        }
        catch (MalformedURLException e)
        {
            System.err.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    protected void Debug(String msg) 
    {
        if (debug==true)
            System.err.println("Bootstrapper:DEBUG:"+msg); 
    }
    
    protected void Message(String msg) 
    {
         System.err.println("Bootstrapper:"+msg); 
    }


    public void addJarsToLibUrls(String libDir,boolean recurse, int dirLevel)
            throws Exception
    {
        File dir = getDirectory(libDir);

        if (!dir.exists() || !dir.isDirectory() || !dir.canRead())
        {
            throw new Exception("Exception: directory does not exists or is unreadable:"+dir); 
        }
       
        try
        {
            File[] files = dir.listFiles(); 
            for (int i = 0; i < files.length; i++)
            {
            	String fileName=files[i].getName();  
                String filepath = files[i].getPath();
                
                //Debug("Checking:"+filepath);
                // skip ./lib/plugins (but only onlevel 0) 
                if ((dirLevel==0) && (fileName.compareToIgnoreCase(PLUGIN_SUBDIR)==0))
                {
                	Debug("Ignoring custom plugin directory:"+filepath);
                }
                else if (filepath.endsWith(".jar")==true)
                {
                	// Globus says: must first convert it to URI and then to URL
                	// it probably uses it to normalize (relative) File URL()... 
                	
                	URL url = files[i].toURI().toURL();
                	this.classpathUrls.add(url);
                	Debug("Adding jarurl:" + url);
                	
                }
                else if ((files[i].isDirectory()==true) && (recurse=true)) 
                {
                	Debug("Entering recursion for:"+files[i]); 
                	addJarsToLibUrls(files[i].getPath(),recurse,dirLevel+1);  
                }
            }
        }
        catch (IOException e)
        {

            throw new Exception("BootrapException:"
                    + "Error during startup processing, jar=" + libDir, e);
        }
    }

    public void launch(String launchClass, String[] launchArgs)
            throws Exception
    {	
    	// set the whole VLET environment: 
    	
    	this.checkSetVLeTEnvironment(); 
    	
        URL[] urlJars = new URL[this.classpathUrls.size()];
        urlJars = (URL[]) this.classpathUrls.toArray(urlJars);

        //ClassLoader parent=ClassLoader.getSystemClassLoader(); 
        // Keep current classloader so current classpath which was used
        // to load the bootloader is respected:
        ClassLoader parent=Thread.currentThread().getContextClassLoader(); 
        
        // context class loader:
        URLClassLoader loader = new URLClassLoader(urlJars,parent);
        Thread.currentThread().setContextClassLoader(loader);

        // start main class
        try
        {

            Class mainClass = loader.loadClass(launchClass);

            Method mainMethod = mainClass.getMethod("main", MAIN_PARAMS_TYPE);

            mainMethod.invoke(null, new Object[]{ launchArgs });
        }
        catch (ClassNotFoundException e)
        {
            throw new Exception("ClassNotFoundException:"+"Class '"
                    + launchClass + "'.\n"+e, e);
        }
        catch (NoSuchMethodException e)
        {

            throw new Exception("NoSuchMethodException: main() method not found in class '"
                    + launchClass + "'\n"+e,e);
        }
        catch (InvocationTargetException e)
        {
            throw new Exception("InvocationTargetException:"+e,e); 
        }
        catch (IllegalAccessException e)
        {
            throw new Exception("IllegalAccessException:"+e,e); 
        }
    }

   
    public static void main(String args[])
    {
    	Bootstrapper boot = new Bootstrapper();
    
    	if ((args==null) || (args.length<1))
    	{
    		System.err.println("Usage: java [JVM options] -jar bootstrapper.jar [bootstrapper options] <main class> [application options]");
    		return; 
    	}
    	// First argument MUST be starting class: 
    	
    	String startclass=args[0];
    	String newargs[]=new String[args.length-1];  
    	
    	for (int i=0;i<newargs.length;i++)
    	{
    		String arg=args[i+1]; 
    		
    		if (arg.compareTo("-debug")==0)
    			debug=true;
    		
    		// pass argument to launch class 
    		newargs[i]=args[i+1];
    	}
    		     
    	try
    	{
    	    boot.launch(startclass,newargs);
    	}
    	catch (Exception e) 
		{
			System.err.println("***Error: Exception:" + e);
	        e.printStackTrace();
		} 
    }

    public void setBaseURL(URL baseurl)
    {
        baseUrl=baseurl;
    }

}

