package test.misc;

import nl.uva.vlet.ClassLogger;

public class TestJavaLogger
{

    public static void main(String args[])
    {
        // Set ParentLevel!
        ClassLogger.getRootLogger().setLevelToError(); 

        ClassLogger logger=ClassLogger.getLogger("test.logger");

        logger.setLevelToError();

        logger.debugPrintf("1 DEBUG NOT VISIBLE: Hello test '%s'\n","test");
        logger.warnPrintf("1 WARN NOT VISIBLE: Hello test '%s'\n","test");
        logger.infoPrintf("1 INFO NOT VISIBLE: Hello test '%s'\n","test");
        logger.errorPrintf("1 ERROR VISIBLE: Hello test '%s'\n","test");
        
        logger.setLevelToDebug();
        
        logger.debugPrintf("2 DEBUG VISIBLE: Hello test '%s'\n","test");
        logger.warnPrintf("2 WARN VISIBLE: Hello test '%s'\n","test");
        logger.infoPrintf("2 INFO VISIBLE: Hello test '%s'\n","test");
        logger.errorPrintf("2 ERROR VISIBLE: Hello test '%s'\n","test");
        
        logger.setLevelToParent(); 
        
        logger.debugPrintf("3 DEBUG NOT VISIBLE: Hello test '%s'\n","test");
        logger.warnPrintf("3 WARN NOT VISIBLE: Hello test '%s'\n","test");
        logger.infoPrintf("3 INFO NOT VISIBLE: Hello test '%s'\n","test");
        logger.errorPrintf("3 ERROR VISIBLE: Hello test '%s'\n","test");
        
        // Set ParentLevel!
        ClassLogger.getRootLogger().setLevelToDebug(); 
        
        logger.debugPrintf("4 DEBUG VISIBLE: Hello test '%s'\n","test");
        logger.warnPrintf("4 WARN VISIBLE: Hello test '%s'\n","test");
        logger.infoPrintf("4 INFO VISIBLE: Hello test '%s'\n","test");
        logger.errorPrintf("4 ERROR VISIBLE: Hello test '%s'\n","test");
        
    }
}
