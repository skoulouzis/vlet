package test;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.vrs.VRS;

public class TestWebdavBrowser
{

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(TestWebdavBrowser.class);
        logger.setLevelToDebug();
    }

    public static void main(String args[])
    {

        try
        {
            Global.init();

            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.webdavfs.WebdavFSFactory.class);

            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.slide.webdavfs.WebdavFSFactory.class);

            nl.uva.vlet.gui.startVBrowser.main(args);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            // VRS.exit();
        }
    }

}
