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
 * $Id: Global.java,v 1.24 2011-06-07 15:41:29 ptdeboer Exp $  
 * $Date: 2011-06-07 15:41:29 $
 */
// source: 

package nl.uva.vlet;

import java.io.File;
import java.net.InetAddress;
import java.util.Vector;
import java.util.logging.Level;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

/**
 * Class for Global system variables and runtime environment. To configure the
 * Global Environment use GlobalConfig.
 * <p>
 * <b>Important</b>: To avoid Chicken&Egg initialization problems: <br>
 * - Use GlobalConfig methods to configure pre-initialization properties and
 * Global methods after initialization (Global.init()) <br>
 * <br>
 * As a rule "configurable" properties are in GlobalConfig. Static properties
 * are in Global.
 * <p>
 * Developers notes:<br>
 * - Do not call Registry methods from this class(Chicken and Egg
 * initialization)! Registry calls Global methods !<br>
 * 
 * @see nl.uva.vlet.GlobalConfig
 * 
 * @author P.T. de Boer
 */
public class Global
{
    private static ClassLogger logger = null;

    /**
     * Private class initiliazer: use to initialize this class. Must not call
     * Registry class as that class references THIS class.
     */
    private static void initClass()
    {
        logger.infoPrintf("--- Global Init ---\n");

        // extra class initialization code.
        // be very carefull what to put here:...
        // System.err.println("--- Global Init ---");

        // Global.addDebugClass(Global.class.getCanonicalName());
        // Global.addDebugClass("nl.uva.vlet.vrs.Registry");

        // Check whether debug must be enabled during startup
        // this code is always executed before parseArguments so to
        // really get all debug output specify -DDEBUG=true

        // some verbose level 2 (=configuration) messages.
        logger.infoPrintf(" VLET_INSTALL       =%s\n", Global.getProperty("VLET_INSTALL"));
        logger.infoPrintf(" user.name          =%s\n", Global.getUsername());
        logger.infoPrintf(" user.home          =%s\n", Global.getUserHomeLocation());
        logger.infoPrintf(" hostname           =%s\n", Global.getHostname());
        logger.infoPrintf(" install base dir   =%s\n", Global.getInstallBaseDir());
        logger.infoPrintf(" install config dir =%s\n", Global.getInstallationConfigDir());
        logger.infoPrintf(" install lib dir    =%s\n", Global.getInstallationLibDir());
        logger.infoPrintf(" VLET_DEBUG         =%s\n", Global.getProperty("VLET_DEBUG"));
        logger.infoPrintf(" GLOBUS_LOCATION    =%s\n", Global.getProperty("GLOBUS_LOCATION"));
        logger.infoPrintf(" --- Configuration Settings--- \n");
        logger.infoPrintf(" isApplet               =%s\n", GlobalConfig.isApplet());
        logger.infoPrintf(" isService              =%s\n", GlobalConfig.isService());
        logger.infoPrintf(" init URL factory       =%s\n", GlobalConfig.getInitURLStreamFactory());
        logger.infoPrintf(" persistant user config =%s\n", GlobalConfig.getUsePersistantUserConfiguration());

        GlobalConfig.globalInitialized();
    }

    static
    {
        logger = ClassLogger.getLogger(Global.class);
    }

    /** Explicit initializer */
    public static void init()
    {
        // method trigger initClass() method !
        logger.debugPrintf("--- init() called ---\n");
        initClass();
    }

    // ========================================================================
    // Deprecated devbug/login methods. 
    // ========================================================================

    /** @deprecated use ClasLogger.getLogger(...).debugPrintf(...) */
    public static void debugPrintln(Object obj, String msg)
    {
        logger.logPrintf(ClassLogger.DEBUG, obj, "%s\n", msg);
    }
    /** @deprecated use getLogger().infoPrintf */
    public static void infoPrintln(Object obj, String msg)
    {
        logger.logPrintf(ClassLogger.INFO, obj, "%s\n", msg);
    }

    /** @deprecated use getLogger().getLevel() */
    public static boolean getDebug()
    {
        return logger.hasEffectiveLevel(ClassLogger.DEBUG);
    }
    
    /** @deprecated Use GlobalLogger.setLevel() */
    public static void setDebug(boolean val)
    {
        if (val == true)
            logger.setLevel(ClassLogger.DEBUG);
        else
            logger.setLevel(ClassLogger.ERROR);
    }

    /** @deprecated use ClassLogger.getLogger(...).errorPrintf(...) */
    public static void errorPrintln(Object obj, String msg)
    {
        logger.logPrintf(ClassLogger.ERROR, obj, "%s\n", msg);
    }

    /**
     * @deprecated Will move to GlobalLogger() ! Use GlobalLogger().getDefault()
     *             for more logging method
     */
    public static void debugPrint(Object obj, String msg)
    {
        logger.logPrintf(ClassLogger.DEBUG, obj, msg);
    }

    /** @deprecated use getLogger().warnPrintf */
    public static void warnPrintln(Object obj, String msg)
    {
        logger.logPrintf(ClassLogger.WARN, obj, "%s\n", msg);
    }

    // ========================================================================
    // Static logger methods: implementation moved to GlobalLogger class !
    // ========================================================================

    /** Global logger. Use this one, if no class logger has been initialized */
    public static ClassLogger getLogger()
    {
        return ClassLogger.getRootLogger();
    }

    /** Get specific ClassLogger. Call ClassLogger.getLogger(...) */
    public static ClassLogger getClassLogger(Class<?> clazz)
    {
        return ClassLogger.getLogger(clazz.toString());
    }

    /**
     * Global method to print a DEBUG message. This is for classes which do not
     * want to instantiate a ClassLogger. Note that the overhead is slightly
     * higher since extra checking might occur. Use ClassLogger().getLogger(...)
     * to instantiate a default Logger which is faster.
     */
    public static void debugPrintf(Object obj, String format, Object... args)
    {
        logger.logPrintf(ClassLogger.DEBUG, obj, format, args);
    }  

    /**
     * Global method to print an INFO message. This is for classes which do not
     * want to instantiate a ClassLogger. Note that the overhead is slightly
     * higher since extra checking might occur. Use ClassLogger().getLogger(...)
     * to instantiate a default Logger which is faster.
     */
    public static void infoPrintf(Object obj, String format, Object... args)
    {
        logger.logPrintf(ClassLogger.INFO, obj, format, args);
    }

    /**
     * Global method to print an WARN message. This is for classes which do not
     * want to instantiate a ClassLogger. Note that the overhead is slightly
     * higher since extra checking might occur. Use ClassLogger().getLogger(...)
     * to instantiate a default Logger which is faster.
     */
    public static void warnPrintf(Object obj, String format, Object... args)
    {
        logger.logPrintf(ClassLogger.WARN, obj, format, args);
    }

    /** Log an message with "ERROR" loglevel. */
    public static void errorPrintf(Object obj, String format, Object... args)
    {
        logger.logPrintf(ClassLogger.ERROR, obj, format, args);
    }

    /** Log exception and dump stacktrace to error output */
    public static void errorPrintStacktrace(Throwable e)
    {
        logger.logStacktrace(ClassLogger.ERROR, e);
    }

    /**
     * Log Exception e with specified logging Level and optional message.
     * Calling classed must specify a 'source' object which could be the calling
     * instance.
     */
    public static void logException(Level level, Object source, Throwable e, String format, Object... args)
    {
        logger.logException(level, source, e, format, args);
    }

    /** Dump stacktrace to global logger. */
    public static void debugPrintStacktrace(Throwable e)
    {
        logger.logStacktrace(ClassLogger.DEBUG, e);
    }

    /** Print exception to global logger. */
    public static void debugPrintException(Object obj, Throwable t)
    {
        logger.logPrintf(ClassLogger.DEBUG, obj, "Exception = %s", t);
    }

    // =======================================================================
    // Global Configuration
    // =======================================================================

    /**
     * Get user property from $HOME/.vletrc/vletrc.prop Will always reload
     * ~/.vletrc/vletrc.prop file. Return null value is file doesn't exist.
     */
    public static String getUserProperty(String name)
    {
        return GlobalConfig.getUserProperty(name);
    }

    /**
     * Return property from INSTALLDIR/etc/vletrc.prop or vletrc.prop found on
     * class path (backup properties).
     * 
     * @param name
     * @return
     */
    private static String getInstallationProperty(String name)
    {
        return GlobalConfig.getInstallationProperty(name);
    }

    /**
     * Returns "user.home" system property ($HOME under unix). Can be overriden
     * if in the installation property the value 'user.home' has been defined
     * which may be a relative path to the installion for personalized
     * installation settings.
     * <p>
     * See INSTALLDIR/etc/vletrc.prop, value "user.home".
     */
    public static final String getUserHome()
    {
        return GlobalConfig.getUserHome();
    }

    /**
     * Get VFS compatible User Home VRL. By avoiding location file paths and
     * (re)use the VFS interface, file and configuration can be stored anywhere
     * ! Use this one instead of getUserHome().
     * 
     * @return
     */
    public static final VRL getUserHomeLocation()
    {
        return GlobalConfig.getUserHomeLocation();
    }

    /**
     * Get VFS compatible User Configuration VRL. By avoiding location file
     * paths and (re)use the VFS interface, file and configuration information
     * can be stored anywhere ! Default is $HOME/.vletrc
     * 
     * @return
     */
    public static final VRL getUserConfigDir()
    {
        return GlobalConfig.getUserConfigDir();
    }

    /** Returns "user.name" system property. $USER under unix */
    public static final String getUsername()
    {
        // use java's property user.name
        return GlobalConfig.getSystemProperty(GlobalConfig.JAVA_USER_NAME);
    }

    /**
     * Returns base dir of installed software a.k.a VLET_INSTALL. Dynamically
     * tries to figure out where it is started from.
     * <p>
     * If VLET_INSTALL is set, it will return this value as VRL. else the
     * startup directory will be used.
     */
    public static final VRL getInstallBaseDir()
    {
        return GlobalConfig.getInstallBaseDir();
    }

    /**
     * Returns vlet.install.sysconfdir property or (default) VLET_INSTALL/etc
     */
    public static VRL getInstallationConfigDir()
    {
        return GlobalConfig.getInstallationConfigDir();
    }

    /** Returns vlet.install.libdir property or (default) VLET_INSTALL/lib */
    public static VRL getInstallationLibDir()
    {
        return GlobalConfig.getInstallationLibDir();
    }

    /**
     * Returns vlet.install.libdir property or (default) VLET_INSTALL/lib
     */
    public static VRL getInstallationDocDir()
    {
        return GlobalConfig.getInstallationDocDir();
    }

    /**
     * Returns vlet.install.bindir property or (default) VLET_INSTALL/bin
     */
    public static VRL getInstallationBinDir()
    {
        String bindir = getInstallationProperty(GlobalConfig.PROP_VLET_BINDIR);

        if (bindir != null)
            return new VRL("file", null, bindir); // scheme,host,path

        // return VLET_INSTALL/lib
        return Global.getInstallBaseDir().appendPath("bin");
    }

    /**
     * Returns fully qualified hostname or 'localhost' if hostname can't be
     * determined.
     */
    public static String getHostname()
    {
        try
        {
            String hostname = InetAddress.getLocalHost().getCanonicalHostName();
            return hostname;
        }
        catch (Exception ex)
        {
            Global.getLogger().logException(ClassLogger.WARN, ex, MessageStrings.TXT_COULDNT_GET_LOCALHOSTNAME + "\n");
        }

        String val = GlobalConfig.getSystemEnv("HOSTNAME");

        if ((val != null) && (val.equals("") == false))
            return val;

        return "localhost";
    }

    /**
     * Parses property arguments in the style: -Dname=val.
     * 
     * When the JVM is given a -Dprop=val argument, it is already filtered, but
     * when the argument is given after the start class, the argument is in the
     * args list.
     * 
     * @return the subset of String arguments which are not parsed.
     */
    public static String[] parseArguments(String[] args)
    {
        if (args == null)
            return null;

        StringList remainingArgs = new StringList();

        for (String arg : args)
        {
            infoPrintf(Global.class, "Checking arg:%s\n", arg);

            boolean parsed = false;

            if (arg.startsWith("-D"))
            {
                // debugPrintln("Global","arg="+arg);

                // property argument
                String propDef = arg.substring(2);
                String strs[] = propDef.split("[ ]*=[ ]*");

                if ((strs != null) && (strs.length > 1) && (strs[0] != null))
                {
                    System.setProperty(strs[0], strs[1]);
                    parsed = true;
                }
                else
                {
                    Global.errorPrintf(Global.class, "Couldn't parse property:%s\n", arg);
                }
            }
            else if (arg.equalsIgnoreCase("-debug"))
            {
                logger.setLevelToDebug();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-warn"))
            {
                logger.setLevelToWarn();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-info"))
            {
                logger.setLevelToInfo();
                // Global.setDebug(true);
                parsed = true;
            }
            else if (arg.startsWith("-loglevel="))
            {
                String levelstr = arg.substring("-loglevel=".length());

                if (levelstr.compareToIgnoreCase("NONE") == 0)
                {
                    logger.setLevelToNone();
                    parsed = true;
                }
                else if (levelstr.compareToIgnoreCase("FATAL") == 0)
                {
                    logger.setLevelToFatal();
                    parsed = true;
                }
                else if (levelstr.compareToIgnoreCase("ERROR") == 0)
                {
                    logger.setLevelToError();
                    parsed = true;
                }
                else if (levelstr.compareToIgnoreCase("WARN") == 0)
                {
                    logger.setLevelToWarn();
                    parsed = true;
                }
                else if (levelstr.compareToIgnoreCase("INFO") == 0)
                {
                    logger.setLevelToInfo();
                    parsed = true;
                }
                else if (levelstr.compareToIgnoreCase("DEBUG") == 0)
                {
                    logger.setLevelToDebug();
                    parsed = true;
                }
                else
                    Global.errorPrintf(Global.class, "unrecognized log level=%s\n", arg);
            }

            if (parsed == false)
                remainingArgs.add(arg);
        }

        return remainingArgs.toArray();
    }

    /**
     * Get global property.
     * 
     * @see GlobalConfig#getProperty(String)
     */
    public static String getProperty(String name)
    {
        return GlobalConfig.getProperty(name);
    }

    /** Returns base location of installed code of Global class */
    public static VRL getCodeBaseLocation()
    {
        return GlobalConfig.getBaseLocation();
    }

    /**
     * Returns value or 'vlet.version'
     */
    public static String getVersion()
    {
        return GlobalConfig.getInstallationProperty(GlobalConfig.PROP_VLET_VERSION);
    }

    /** Whether operation system is "Linux". */
    public static boolean isLinux()
    {
        String arch = getOSName();

        if ((arch != null) && (arch.compareToIgnoreCase(GlobalConfig.JAVA_OS_LINUX) == 0))
            return true;

        return false;
    }

    /**
     * Whether OS has an UX compatible FS. Currently only returns 'true' when
     * operating system is Linux. Mac OS has an UX like FS too.
     * 
     * @deprecated Doesn't work correctly.
     */
    public static boolean hasUXFS()
    {
        if (isLinux())
            return true;

        return false;
    }

    /** Whether operation system is "Windows". */
    public static boolean isWindows()
    {
        String osname = getOSName();

        debugPrintf(Global.class, "OSName=%s\n", osname);

        if (osname == null)
            return false; // applet ?

        osname.toLowerCase();
        String winStr = new String(GlobalConfig.JAVA_OS_WINDOWS);
        winStr.toLowerCase();

        if ((osname != null) && (osname.startsWith(winStr)))
            return true;

        return false;
    }

    /** Return value for java "os.arch" property. */
    public static String getArch()
    {
        return GlobalConfig.getSystemProperty(GlobalConfig.JAVA_OS_ARCH);
    }

    /** Return value for java "os.name" property. */
    public static String getOSName()
    {
        return GlobalConfig.getSystemProperty(GlobalConfig.JAVA_OS_NAME);
    }

    /** Return value for java "os.version" property. */
    public static String getOSVersion()
    {
        return GlobalConfig.getSystemProperty(GlobalConfig.JAVA_OS_VERSION);
    }

    public static VRL getHelpUrl(String string)
    {
        return GlobalConfig.getBaseLocation().appendPath("doc/help/index.html");
    }

    /**
     * @deprecated Configuration of (system) properties has been moved to
     *             GlobalConfig
     */
    public static void setSystemProperty(String name, String value)
    {
        GlobalConfig.setSystemProperty(name, value);
    }

    // ========================================================================
    // Derived Properties
    // ========================================================================

    /** @see GlobalConfig#getFirewallPortRangeString() */
    public static String getFirewallPortRangeString()
    {
        return GlobalConfig.getFirewallPortRangeString();

    }

    /** @see GlobalConfig#getIncomingFirewallPortRange() */
    public static int[] getIncomingFirewallPortRange()
    {
        return GlobalConfig.getIncomingFirewallPortRange();
    }

    /** Returns boolean property or the default value */
    public static boolean getBoolProperty(String name, boolean defVal)
    {
        String valstr = getProperty(name);

        if (valstr == null)
            return defVal;

        return new Boolean(valstr);
    }

    /** Returns integer property or the default value */
    public static int getIntegerProperty(String name, int defVal)
    {
        String valstr = getProperty(name);

        if (valstr == null)
            return defVal;

        return new Integer(valstr);
    }

    /**
     * When true only passive connections are allowed. This means no incoming
     * tcp/ip connections are allowed or possible.
     * 
     * @return
     */
    public static boolean getPassiveMode()
    {
        GlobalConfig.passiveModeOnly = Global.getBoolProperty(GlobalConfig.PROP_PASSIVE_MODE,
                GlobalConfig.passiveModeOnly);
        return GlobalConfig.passiveModeOnly;
    }

    /** Returns Current Working Dir */
    public static VRL getStartupWorkingDir()
    {
        return GlobalConfig.getStartupWorkingDir();
    }

    /** Returns System Default tempdir */
    public static VRL getDefaultTempDir()
    {
        return GlobalConfig.getDefaultTempDir();
    }

    public static VRL getInstallationPluginDir()
    {
        return GlobalConfig.getInstallationLibDir().appendPath(GlobalConfig.PLUGIN_SUBDIR);
    }

    public static VRL getUserPluginDir()
    {
        return GlobalConfig.getUserConfigDir().appendPath(GlobalConfig.PLUGIN_SUBDIR);
    }

    public static VRL getUserPropertiesLocation()
    {
        return GlobalConfig.getUserPropertiesLocation();
    }

    public static boolean useHttpProxy()
    {
        String val = GlobalConfig.getProperty(GlobalConfig.HTTP_PROXY_ENABLED);
        return StringUtil.isTrue(val);
    }

    public static String getProxyHost()
    {
        return GlobalConfig.getProperty(GlobalConfig.HTTP_PROXY_HOST);
    }

    public static int getProxyPort()
    {
        return getIntegerProperty(GlobalConfig.HTTP_PROXY_PORT, -1);
    }

    // ===
    // Misc
    // ===

    /** jUnit tests for global initialization and bootstrapping */
    public static int checkInitialized()
    {
        return 0;
    }

    // Currenlty used for testing purposed only
    public static void main(String args[])
    {
        if ((args == null) || (args.length <= 0))
            System.exit(checkInitialized());
    }

    /**
     * Returns all root partition on this system. On Windows, this will be the
     * A: through Z: drives.
     */
    public static File[] getWindowsDrives()
    {
        Vector<File> rootsV = new Vector<File>();

        // update system property
        boolean skipFloppy = Global.getBoolProperty(GlobalConfig.PROP_SKIP_FLOPPY_SCAN, true);

        // Create the A: drive whether it is mounted or not
        if (skipFloppy == false)
        {
            String drivestr = "A:\\";
            rootsV.addElement(new File(drivestr));
        }

        // Run through all possible mount points and check
        // for their existance.
        for (char c = 'C'; c <= 'Z'; c++)
        {
            char device[] =
            { c, ':', '\\' };
            String deviceName = new String(device);
            File deviceFile = new File(deviceName);

            if ((deviceFile != null) && (deviceFile.exists()))
            {
                rootsV.addElement(deviceFile);
            }
        }

        File[] roots = new File[rootsV.size()];
        rootsV.copyInto(roots);

        return roots;
    }

    public static String getJavaVersion()
    {
        return Global.getProperty("java.version");
    }

    public static String getJavaHome()
    {
        return Global.getProperty("java.home");
    }

}
