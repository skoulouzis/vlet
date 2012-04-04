package test.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.util.CompressUtil;
import nl.uva.vlet.util.compress.Archive;
import nl.uva.vlet.util.compress.ArchiveEntry;
import nl.uva.vlet.util.compress.VGzFile;
import nl.uva.vlet.util.compress.VtarFile;
import nl.uva.vlet.util.compress.VzipFile;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.io.VRandomReadable;

public class TestUncompressUtil
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(TestUncompressUtil.class);
        logger.setLevelToDebug();
    }

    public static void main(String args[])
    {
        try
        {
            testGetInputStreamFromArchive();
            
//             testUncompressZip();
//             testUncompressTar();

//             testGetZipEntries();

//            testGetTarEntries();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            VRS.exit();
            System.exit(0);
        }
    }

    private static void testGetInputStreamFromArchive() throws VlException, IOException
    {
        VFile zipFile;
        VFSClient vfs = new VFSClient();
        String fileLoc = "file://localhost/" + System.getProperty("user.home") + "/Downloads/small.tar.gz";
        VRL vrl = new VRL(fileLoc);
        zipFile = vfs.newFile(vrl);
        
        Archive arch = null;
        if (StringUtil.equals(vrl.getExtension().toLowerCase(), "zip")){
            arch = new VzipFile((VRandomReadable) zipFile);
        }
        
        if (StringUtil.equals(vrl.getExtension().toLowerCase(), "tar")){
            arch = new VtarFile((VRandomReadable) zipFile);
        }
        if (StringUtil.equals(vrl.getExtension().toLowerCase(), "gz")){
            arch = new VGzFile(zipFile);
        }
         
        
        Enumeration<ArchiveEntry> entries = arch.getEntries();
        byte[] b = new byte[1024*6];
        while (entries.hasMoreElements())
        {
            ArchiveEntry entry = entries.nextElement();
            logger.debugPrintf("Path: %s   Data Offser: %s\n", entry.getPath(),entry.getDataOffset());
            
            InputStream in = arch.getInputStream(entry);
            
//            in.read(b);
            
//            logger.debugPrintf("Data: %s\n", new String(b));
        }
    }

    private static void testGetTarEntries() throws VlException, IOException
    {
        VFile zipFile;
        VFSClient vfs = new VFSClient();
        String fileLoc = "file://localhost/" + System.getProperty("user.home") + "/Downloads/icons.tar";
        VRL vrl = new VRL(fileLoc);
        zipFile = vfs.newFile(vrl);
        
        Enumeration<ArchiveEntry> entries = CompressUtil.getEntries(zipFile.getVRL());
        
        while (entries.hasMoreElements())
        {
            ArchiveEntry entry = entries.nextElement();
            logger.debugPrintf("Path: %s\n", entry.getPath());
        }
        
    }

    private static void testGetZipEntries() throws VRLSyntaxException, VlException, IOException
    {
        VFile zipFile;
        VFSClient vfs = new VFSClient();
        String fileLoc = "file://localhost/" + System.getProperty("user.home") + "/Downloads/icons.zip";
        VRL vrl = new VRL(fileLoc);
        zipFile = vfs.newFile(vrl);
        Enumeration<ArchiveEntry> entries = CompressUtil.getEntries(zipFile.getVRL());
        
        
        while (entries.hasMoreElements())
        {
            ArchiveEntry entry = entries.nextElement();
            logger.debugPrintf("Path: %s\n", entry.getPath());
        }
    }

    private static void testUncompressTar() throws VlException, ZipException, IOException
    {
        VFile zipFile;
        VFSClient vfs = new VFSClient();
        String fileLoc = "file://localhost/" + System.getProperty("user.home") + "/Downloads/icons.tar.gz";
        VRL vrl = new VRL(fileLoc);
        zipFile = vfs.newFile(vrl);
        CompressUtil.uncopress(zipFile.getVRL(), new VRL("file://localhost/tmp/TEST_ZIP"));
    }

    private static void testUncompressZip() throws VlException, ZipException, IOException
    {
        VFile zipFile;
        VFSClient vfs = new VFSClient();
        String fileLoc = "file://localhost/" + System.getProperty("user.home") + "/Downloads/icons.zip";
        VRL vrl = new VRL(fileLoc);
        zipFile = vfs.newFile(vrl);
        CompressUtil.uncopress(zipFile.getVRL(), new VRL("file://localhost/tmp/TEST_ZIP"));
    }

}
