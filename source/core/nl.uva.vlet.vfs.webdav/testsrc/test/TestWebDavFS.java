package test;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

public class TestWebDavFS
{

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(TestWebDavFS.class);
        logger.setLevelToDebug();
    }

    private static final String TEST_CONTENTS = ">>> This is a testfile used for the VFS unit tests  <<<\n"
            + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\n" + "0123456789@#$%*()_+\n"
            + "Strange characters:...<TODO>\n" + "UTF8:<TODO>\n" + "\n --- You Can Delete this File ---\n";

    public static void main(String[] args)
    {
        try
        {

            Global.init();
            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.webdavfs.WebdavFSFactory.class);

            // testList();

            // testDelete();

            // testRename();

            // testACL();

            testGetLen();

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            VRS.exit();
        }
    }

    private static void testGetLen() throws VRLSyntaxException, VlException
    {

        VFSClient client = new VFSClient();
        VRL vrl;

        vrl = new VRL("webdav://localhost:8008/");

        VDir testDir = client.createDir(vrl.append("Test"), true);

        VFileSystem fs = testDir.getFileSystem();
        VFile newFile = fs.newFile(testDir.resolvePathVRL("testFile7"));

        newFile.setContents(TEST_CONTENTS);

        long newLen = newFile.getLength();
        logger.debugPrintf("After setting contents, size may NOT be zero %s \n", newLen);

    }

    private static void testACL() throws VRLSyntaxException
    {
        VFSClient client = new VFSClient();
        VRL vrl;

        vrl = new VRL("webdav://localhost:8008/");

    }

    private static void testRename() throws VlException
    {
        VFSClient client = new VFSClient();
        VRL vrl;

        vrl = new VRL("webdav://localhost:8008/");

        VDir testDir = client.createDir(vrl.append("Test"), true);

        VDir newDir = testDir.createDir("NewDir", true);

        boolean result = newDir.renameTo("newDirName", false);

        logger.debugPrintf("Rename must return true????%s\n", result);

        result = testDir.existsDir("newDirName");

        logger.debugPrintf("New VDir doesn't exist:????%s\n", result);

        VDir renamedDir = testDir.getDir("newDirName");
        logger.debugPrintf("After rename, new VDir is NULL:????%s\n", renamedDir);

        // cleanup:
        renamedDir.delete();

    }

    private static void testList() throws VRLSyntaxException
    {
        VFSClient client = new VFSClient();
        VRL vrl;

        vrl = new VRL("webdav://localhost:8008/");

        // VNode node = client.getNode(vrl);

        // if (node instanceof VDir)
        // {
        // WebdavDir dir = (WebdavDir) node;
        //
        // VFSNode[] files = dir.list();
        //
        // for (int i = 0; i < files.length; i++)
        // {
        // logger.debugPrintf("files[%s] %s\n", i, files[i].getVRL());
        // }
        // }

    }

    private static void testDelete() throws VlException
    {

        VFSClient client = new VFSClient();
        VRL vrl;

        vrl = new VRL("webdav://localhost:8008/");

        VDir testDir = client.createDir(vrl.append("Test"), true);

        VFile newFile = testDir.createFile(("testFile"));

        logger.debugPrintf("newFile is NULL????%s\n", newFile);

        logger.debugPrintf("Len  is 0????%s\n", newFile.getLength());

        newFile.delete();

        logger.debugPrintf("newFile exists????%s\n", newFile.exists());

        newFile = testDir.newFile("testFile1b");
        newFile.create(); // use default creat();

        // sftp created 1-length new files !
        logger.debugPrintf("newFile is NUL????%s\n", newFile);
        logger.debugPrintf("Len  is 0????%s\n", newFile.getLength());

        newFile.delete();
        logger.debugPrintf("newFile exists????%s\n", newFile.exists());
    }
}
