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
 * $Id: ClassLogger.java,v 1.19 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */
// source: 

package nl.uva.vlet;

// Only allow Default Java Imports. ALL the classes refer to this class!
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uva.vlet.logging.FormattingLogger;
import nl.uva.vlet.logging.StderrLogHandler;

/**
 * java.util.logging.Logger compatible subclass for class logging.
 * <p>
 * Class Initialization: Do NOT refer to Global, Global call uses this class
 * during initialization!
 */
public class ClassLogger extends FormattingLogger
{
    // ==================
    // Class Fields
    // ==================

    public static final String VLET_RESOURCEBUNDLENAME = "nl.uva.vlet";

    private static Map<String, ClassLogger> classLoggers = new Hashtable<String, ClassLogger>();

    private static ClassLogger rootLogger = null;

    // ==================
    // Class Methods
    // ==================

    public static synchronized ClassLogger getLogger(String name)
    {
        synchronized (classLoggers)
        {
            ClassLogger logger = classLoggers.get(name);
            // Logger javaLogger = LogManager.getLogManager().getLogger(name);
            //
            // if (javaLogger instanceof ClassLogger)
            // logger=(ClassLogger)logger;

            if (logger == null)
            {
                logger = new ClassLogger(name);
                classLoggers.put(name, logger);
                // ===
                // Important: Since loggers are hierarchical set parent
                // of new logger to the root logger for default log messages.
                // ===
                logger.setParent(rootLogger);
                // Store on global LogManager:
                // LogManager.getLogManager().addLogger(logger);
            }

            return logger;
        }
    }

    public static ClassLogger getLogger(Class<?> clazz)
    {
        return getLogger(clazz.getCanonicalName());
    }

    static
    {
        // Toplevel Logging!
        Logger javaRootLogger = Logger.getLogger("");
        javaRootLogger.setLevel(Level.SEVERE);

        // java.util.logging.LogManager.getLogManager().getLogger(null).setLevel(ERROR);
        rootLogger = new ClassLogger(VLET_RESOURCEBUNDLENAME);
        rootLogger.setLevel(ERROR);

        // ==============================================================
        // Setup default Logging handler to stderr!
        // ---
        // Default root handler which prints out messages to STDERR:
        rootLogger.addHandler(new StderrLogHandler(System.err));
        // ===============================================================

        // Check VLET_DEBUG
        Level lvl = GlobalConfig.getVletDebugLevel();

        if (lvl != null)
            rootLogger.setLevel(lvl);
    }

    public static ClassLogger getRootLogger()
    {
        return rootLogger;
    }

    // ==================
    // Instance
    // ==================

    protected ClassLogger(String name, String resourceBundleName)
    {
        super(name, resourceBundleName);
    }

    protected ClassLogger(String name)
    {
        // todo: resourcebundle names
        super(name, null);
    }

    // ==========================================================================================
    // Backward compatible methods/legacy
    // ==========================================================================================

    // Old style object class formatter
    private String object2classString(Object obj)
    {
        String source;

        if (obj == null)
        {
            source = "[NULL]";
        }
        else
        {
            if (obj instanceof String)
            {
                // Object is already in string form
                source = (String) obj;
            }
            else
            {
                Class<?> clazz = null;

                if (obj instanceof Class<?>)
                {
                    // Object is class name: is a call
                    // use classname
                    clazz = (Class<?>) obj;
                }
                else
                {
                    // instance call from object: get class of object:
                    clazz = obj.getClass();
                }

                if (clazz.isAnonymousClass())
                {
                    Class<?> supC = clazz.getSuperclass();
                    source = "[Anon]" + clazz.getEnclosingClass().getSimpleName() + ".<? extends "
                            + supC.getSimpleName() + ">";
                }
                else
                {
                    source = clazz.getSimpleName();
                }
            }
        }

        return source;
    }

    /**
     * Warning: this method is a legacy method which takes the source object as
     * argument. This method is slower then the recommend logging methods.
     */
    public void logPrintf(Level level, Object sourceObject, String format, Object... args)
    {
        if (this.isLoggable(level) == false) // slow check!
            return;

        String source = this.object2classString(sourceObject);
        log(level, source + ":" + format, args);
    }

    /**
     * Warning: this method is a legacy method which takes the source object as
     * argument. This method is slower then the recommend logging methods.
     */
    public void logException(Level level, Object sourceObject, Throwable e, String format, Object[] args)
    {
        if (this.isLoggable(level) == false)
            return;

        String srcstr = this.object2classString(sourceObject);
        this.logException(level, e, srcstr + ":" + format, args);
    }

}
