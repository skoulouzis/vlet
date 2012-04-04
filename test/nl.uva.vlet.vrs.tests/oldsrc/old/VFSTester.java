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
 * $Id: VFSTester.java,v 1.2 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;

/**
 * Integration (non unit) Tester for the VFS classes.
 * <p>
 * VFS Tester class, tests various methods on supplied (VFS) Resources. This
 * class can be used by any VFS implementation to tests the implementation. Just
 * create a VFSTester and supply source and destination directories to tests the
 * interface. Method doTests() does them all. Note that this tester is not a
 * unit tests, but an integration tests.
 */

public class VFSTester
{

    public static String TEST_CONTENTS_STRING_SMALL = "This is a simple tests string for small files";

    private String testName = null;

    private int verboseLevel = 0;

    private int testnr = 0;

    public VFSTester()
    {

    }

    public void newTest(String name)
    {
        testName = name;

        if (verboseLevel > 0)
        {
            testnr++;
            Logger("newtest:" + testnr + "=" + testName);
        }
    }

    public void Logger(String message)
    {
        System.out.println("[" + testnr + "]=" + message);
    }

    public void Warning(String message)
    {
        String msg = "[" + testnr + "]= *** WARNING:" + message;

        System.out.println(msg);
        System.err.println(msg);
    }

    public void setVerbose(int i)
    {
        verboseLevel = i;
    }

    /**
     * <pre>
     *   Functional tests method which tests basic create/copy/move/delete:  
     *   - create default tests file in sourceDir. 
     *   - copy from source to destination.
     *   - copy from destination back to source. 
     *   - delete recreated file to cleanup testdirectory 
     *   
     *   No exception should occure ! 
     *   
     *   @param sourceDir empty source directory 
     *   @param destDir   empt destination directory 
     *   @throws VlException 
     *   @throws VlException
     * @throws VlException
     * 
     */
    public VFile testCreateCopyMoveDelete(VDir sourceDir, VDir destDir) throws VlException
    {
        String testFileName = "testFile";
        VFile testfile = null;

        newTest("testCreate remote file:" + testFileName);
        testfile = testCreateFile(destDir, testFileName);

        newTest("cleanup remote file:" + testfile);
        testfile.delete();

        newTest("testCreate local file:" + testFileName);
        testfile = testCreateFile(sourceDir, testFileName);

        // newTest("testCreate remote directories:" + testFileName);
        // testDirs(destDir);

        // perform subtest
        newTest("testCopy:" + testfile);
        testCopy(testfile, sourceDir, destDir);

        // perform subtest
        newTest("testMove:" + testfile);
        testMove(testfile, sourceDir, destDir);

        return testfile;

    }

    public VFile testCreateFile(VDir remoteDir, String testFileName) throws VlException
    {

        // testfile must not exist:
        newTest("delete if exist:" + testFileName);

        if (remoteDir.existsFile(testFileName))
            remoteDir.getFile(testFileName).delete();

        // testfile must not exist:
        newTest("createFile:" + testFileName);

        VFile testfile = null;

        try
        {
            testfile = remoteDir.createFile(testFileName, false);
        }
        catch (VlException e)
        {
            // error on windows: notify and continue
            Error(e);
            Logger("Continuing after exception:" + e.getName());
        }

        if (testfile == null)
        {
            // retry
            newTest("createFile (ignoreExisting==true):" + testFileName);
            testfile = remoteDir.createFile(testFileName, true);
        }

        // set contents
        newTest("setContents:" + testfile);

        testfile.setContents(TEST_CONTENTS_STRING_SMALL);
        String str1 = testfile.getContentsAsString();

        if ((str1 == null) || (str1.compareTo(TEST_CONTENTS_STRING_SMALL) != 0))
        {
            throw new VlException("String contents of new file is missing:" + str1);
        }

        return testfile;
    }

    private void Error(Exception e)
    {

        String message = "*** Test failed [" + this.testnr + "]:Exception ***\n" + " method            = " + testName
                + "\n" + " Exception         = " + e + "\n" + " Exception Message = " + e.getMessage();

        Logger(message);
        System.err.println(message + "\n--- StackTrace ---");
        e.printStackTrace(System.err);
    }

    public void testDirs(VDir sourceDir) throws VlException
    {

        // VDir.createDir
        newTest("sourceDir.createDir (gnore existing):subdir");
        VDir newdir = sourceDir.createDir("subdir", true);

        if (newdir.exists() == false)
        {
            throw new VlException("Couldn't create subdir:" + newdir);
        }

        // remove directory since it can contain previous tests stuff
        newTest("delete recursive subdir");
        boolean result = newdir.delete(true);

        if (result == false)
        {
            throw new VlException("recursive deletion failed. Method returned false for delete:" + newdir);
        }

        if (newdir.exists() == true)
        {
            throw new VlException("recursive deletion failed, directory still exists:" + newdir);
        }

        // VDir.createDir
        newTest("create tests dir, do NOT ignore existing:subdir");
        VDir newdir2 = sourceDir.createDir("subdir", false);
        if (newdir2.exists() == false)
        {
            throw new VlException("Couldn't create subdir:" + newdir2);
        }

        // VDir.createDir ignore existing
        newTest("create tests dir, ignore existing:subdir");
        VDir newdir3 = sourceDir.createDir("subdir", true);
        if (newdir3.exists() == false)
        {
            throw new VlException("Couldn't recreate existing subdir:" + newdir3);
        }

        // VDir.createDir
        newTest("create subsub directory");
        VDir newdir4 = newdir3.createDir("subdir", true);

        if (newdir4.exists() == false)
        {
            throw new VlException("Couldn't create subdirectory:" + newdir4);
        }
        Logger("created new subdirectory: " + newdir4);

        // VDir.renameTo
        String orgName = newdir4.getBasename();
        String newName = "renamedsubdir";
        newTest("rename subsub directory");
        boolean val = newdir4.renameTo(newName, false);

        if (val == false)
        {
            throw new VlException("renameTo returned false for:" + newdir3);
        }
        /*
         * Currently the rename effect the VDir alsoif (newdir3.exists() ==
         * true) { throw new
         * VlException("old renamed subdirectory still exists:"+newdir3); }
         */
        Logger("renamed new directory to new name:" + newName);

        // VDir.hasChild
        newTest("hasChild:" + newName);
        val = newdir3.hasNode(newName);
        if (val == false)
        {
            throw new VlException("parent hasChild reported false for new child:" + newName);
        }

        // VDir.getDir
        newTest("getDir:" + newName);
        VDir dir1 = newdir3.getDir(newName);
        if (dir1 == null)
        {
            throw new VlException("VDir.getDir returned null subdir for getDir:" + newName);
        }

        // VDir.getFile: method should not complain but return null !
        newTest("getFile:" + newName);
        VFile file1 = newdir3.getFile(newName);
        if (file1 != null)
        {
            throw new VlException("VDir.getDir returned NON null VFile  for getFile:" + newName);
        }

        // VDir.delete(recursive=true)
        newTest("endtest: recursive deletion of subdir:" + newdir3);
        newdir3.delete(true);
        if (newdir3.exists() == true)
        {
            throw new VlException("Couldn't recursive delete subdir:" + newdir3);
        }
        Logger("Recursive deleted:" + newdir3);

    }

    /**
     * tests getAttributesNames, getAttribute and getAttributes. Also the order
     * and names returned by getAttributes, must equal the order of the
     * attribute Names supplied
     * 
     * @param node
     * @throws VlException
     */
    public void testAttributes(VNode node) throws VlException
    {
        newTest("getAttributesNames of:" + node);
        String attrNames[] = node.getAttributeNames();

        for (String name : attrNames)
        {
            this.newTest("getAttribute:" + name);

            VAttribute attr = node.getAttribute(name);
            if (attr == null)
            {
                // throw new
                // VlException("Resource returns null attribute for an attribute it claims to have:"+name);
                Warning("Resource returns null attribute for an attribute it claims to have:" + name);
            }
        }

        newTest("getAttributes(attrNames) of:" + node);
        VAttribute attrs[] = node.getAttributes(attrNames);

        int len = attrNames.length;

        for (int i = 0; i < len; i++)
        {
            if (i >= attrs.length)
            {
                // throw new
                // VlException("Number of attributes returned are not equal to number of attributes asked:"+i+">="+len);
                Warning("Number of attributes returned are not equal to number of attributes asked:" + i + ">=" + len);
                break;
            }

            if (attrs[i] == null)
            {
                // throw new
                // VlException("Resource returns null attribute for an attribute it claims to have:"+attrNames[i]);
                Warning("Resource returns null attribute for an attribute it claims to have:" + attrNames[i]);
            }
            else if (attrs[i].getName().compareTo(attrNames[i]) != 0)
            {
                // throw new
                // VlException("Attibute Name doesn't match requested attribute name:"
                // + attrs[i].getName() +"!=" +attrNames[i]);
                Warning("Attribute Name doesn't match requested attribute name:" + attrs[i].getName() + "!="
                        + attrNames[i]);
            }

        }

    }

    public void testCopy(VFile testfile, VDir sourceDir, VDir destDir) throws VlException
    {

        String str1 = testfile.getContentsAsString();
        // copy to destDir
        newTest("copy to destination directory:" + destDir);
        VFile remotefile = testfile.copyTo(destDir);
        Logger("new remotefile=" + remotefile);

        // check existance:
        if (remotefile.exists() == false)
            throw new VlException("remote file does not exist");

        // copy back to local overwriting local file
        newTest("copy to source directory (overwriting original):" + sourceDir);
        VFile newLocalFile = remotefile.copyTo(sourceDir);
        Logger("new local file=" + newLocalFile);

        // delete original local file
        newTest("delete local tests file:" + testfile);
        boolean val = testfile.delete();

        if (val == false)
            throw new VlException("deletion returns false for file:" + testfile);

        Logger("deleted file:" + testfile);

        // check if new local file 'sees' the deletion of old file
        if (newLocalFile.exists() == true)
            throw new VlException("deleted file should not exist:" + newLocalFile);

        // copy back to local recreating local file
        newTest("copy to source directory (recreating oldfile):" + sourceDir);
        VFile newLocalFile2 = remotefile.copyTo(sourceDir);
        Logger("new local file 2:" + newLocalFile2);

        // check if new local file 'sees' the deletion of old file
        if (testfile.exists() == false)
            throw new VlException("recreated file should exist:" + testfile);

        newTest("get contents as String:" + newLocalFile2);
        String str2 = newLocalFile2.getContentsAsString();

        if ((str2 == null) || (str2.length() <= 0) || (str1.compareTo(str2) != 0))
        {
            throw new VlException("String contents of copied files are not the same:" + str2);
        }

        Logger("Contents=" + str2);

        // ===
        // testing copy + rename !
        // ===

        String newName = "Renamed_" + testfile.getBasename();
        String newLocalName = "LocalRenamed_" + testfile.getBasename();

        // copy to destDir
        newTest("copy+rename to destination directory:" + destDir);
        remotefile = testfile.copyTo(destDir, newName);
        Logger("new renamed remotefile=" + remotefile);

        // check existance:
        if (remotefile.exists() == false)
            throw new VlException("remote file does not exist:" + remotefile);

        if (remotefile.getBasename().compareTo(newName) != 0)
            throw new VlException("remote copied file does not have new name:" + remotefile);

        // create remote copy:
        String remoteNewName = "Remotecopyof+" + newName;

        newTest("remote copy:" + destDir);
        VFile newRemotefile = remotefile.copyTo(destDir, remoteNewName);
        Logger("created new remote copy:" + newRemotefile);

        // check existance:
        if (newRemotefile.exists() == false)
            throw new VlException("new remote file does not exist:" + newRemotefile);

        if (newRemotefile.getBasename().compareTo(remoteNewName) != 0)
            throw new VlException("remote copied file does not have new name:" + newRemotefile);

        // copy back to local renaming it again
        newTest("copy+rename to source directory:" + sourceDir);
        newLocalFile = remotefile.copyTo(sourceDir, newLocalName);
        Logger("new renamed local file=" + newLocalFile);

        if (newLocalFile.getBasename().compareTo(newLocalName) != 0)
            throw new VlException("new local file does not have new name:" + newLocalFile);

        // delete original local file
        newTest("delete local tests file:" + testfile);
        val = testfile.delete();

        if (val == false)
            throw new VlException("deletion returns false for file:" + testfile);

        // check deletion
        if (testfile.exists() == true)
            throw new VlException("deleted file should not exist:" + newLocalFile);

        Logger("deleted file:" + testfile);

        // copy back to local recreating local file
        newTest("copy to source directory (renameing to oldfile):" + sourceDir);
        newLocalFile2 = remotefile.copyTo(sourceDir, testfile.getBasename());
        Logger("new renamed (to original testfile) local file 2=" + newLocalFile2);

        // check if new local file 'sees' the deletion of old file
        if (newLocalFile2.exists() == false)
            throw new VlException("new newLocalFile2 doesn't exist:" + newLocalFile2);

        // check if new local file 'sees' recreation of the old file
        if (testfile.exists() == false)
            throw new VlException("recreated testfile file should exist:" + testfile);

        newTest("get contents as String:" + newLocalFile2);
        str2 = newLocalFile2.getContentsAsString();
        Logger("contents =" + str2);

        if ((str2 == null) || (str2.length() <= 0) || (str1.compareTo(str2) != 0))
        {
            throw new VlException("String contents of copied files are not the same:" + str2);
        }
    }

    public void testRenameTo(VDir remoteDir) throws VlException
    {
        VFile testfile = remoteDir.createFile("fileToBeRenamed", true);

        newTest("renaming:" + testfile);

        String orgname = testfile.getBasename();
        String newName = "renamed_" + testfile.getBasename();

        boolean ret = testfile.renameTo(newName, false);
        Logger("renamed file to:" + testfile);
        if (ret == false)
        {
            throw new VlException("rename failed:" + testfile);
        }

        // rename back:
        newTest("renaming back:" + testfile);
        ret = testfile.renameTo(orgname, false);
        Logger("renamed file to:" + testfile);
        if (ret == false)
        {
            throw new VlException("rename failed:" + testfile);
        }
        testfile.delete();

    }

    public void testMove(VFile testfile, VDir sourceDir, VDir destDir) throws VlException
    {
        String str1 = testfile.getContentsAsString();

        // copy to destDir
        newTest("move to destination directory:" + destDir);
        VFile remotefile = testfile.moveTo(destDir);

        // check if new local file 'sees' the deletion of old file
        if (remotefile.exists() == false)
            throw new VlException("remote file does not exist");

        // check if new local file 'sees' the deletion of old file
        if (testfile.exists() == true)
            throw new VlException("source file wasn't removed after move");

        // move back to local
        newTest("move to source directory:" + sourceDir);
        VFile newLocalFile = remotefile.moveTo(sourceDir);

        // check if new local file 'sees' the deletion of old file
        if (newLocalFile.exists() == false)
            throw new VlException("move back file doesn't exist:" + newLocalFile);

        // check if new local file 'sees' the deletion of old file
        if (testfile.exists() == false)
            throw new VlException("old testfile doesn't see it exists again:" + testfile);

        newTest("get contents as String:" + newLocalFile);
        String str2 = newLocalFile.getContentsAsString();

        if ((str2 == null) || (str2.length() <= 0) || (str1.compareTo(str2) != 0))
        {
            throw new VlException("String contents of copied files are not the same");
        }

        // ==
        // == testing with renames !
        // ==

        String newName = "moved_" + testfile.getBasename();
        String newLocalName = "movedLocal_+" + testfile.getBasename();

        // move/rename to destDir
        newTest("move to destination directory:" + destDir);
        remotefile = testfile.moveTo(destDir, newName);

        // check if new local file 'sees' the deletion of old file
        if (remotefile.exists() == false)
            throw new VlException("remote file does not exist");

        // check if new local file 'sees' the deletion of old file
        if (testfile.exists() == true)
            throw new VlException("source file wasn't removed after move");

        // check new name
        if (remotefile.getBasename().compareTo(newName) != 0)
            throw new VlException("remote moveid file does not have new name:" + remotefile);

        // move back to local
        newTest("move/rename back to source directory:" + sourceDir);
        newLocalFile = remotefile.moveTo(sourceDir, newLocalName);

        // check if new local file 'sees' the deletion of old file
        if (newLocalFile.exists() == false)
            throw new VlException("move back file doesn't exist:" + newLocalFile);

        // check if testfile stil is missing
        if (testfile.exists() == true)
            throw new VlException("old testfile exists again:" + testfile);

        if (newLocalFile.getBasename().compareTo(newLocalName) != 0)
            throw new VlException("new local file does not have new name:" + newLocalFile);

        newTest("get contents as String:" + newLocalFile);
        str2 = newLocalFile.getContentsAsString();

        if ((str2 == null) || (str2.length() <= 0) || (str1.compareTo(str2) != 0))
        {
            throw new VlException("String contents of movedfiles are not the same");
        }

        // move back to local
        newTest("renaming new local file back to original testfile:" + testfile);
        // use local move to rename:

        VFile newLocalFile2 = newLocalFile.moveTo(sourceDir, testfile.getBasename());

        Logger("new local file=" + newLocalFile2);

        // check move/rename
        if (newLocalFile2.exists() == false)
            throw new VlException("move+renamed file doesn't exist:" + newLocalFile2);

        // check if new local file 'sees' the existance of recreated file
        if (testfile.exists() == false)
            throw new VlException("testfile doesn't exist:" + testfile);

    }

    /**
     * Perform standard VFS tests. Uses the localTmpDir as local temp dir to
     * create localfiles, and use the remote VFSDir as implemenation directory
     * to tests. To really tests the VFS interface, invoke doTest twice:
     * 
     * <pre>
     *  - doTest(localdir,remotedir) 
     *  - doTests(remotedir,localdir); 
     *  &lt;pre&gt; 
     *  @param localTmpDir
     * @param remoteVFSDir
     * 
     */
    public void doTests(VDir localTmpDir, VDir remoteVFSDir)
    {
        try
        {
            testBugs();

            VFile remoteFile = testCreateFile(remoteVFSDir, "simpleFile");
            testAttributes(remoteFile);
            testAttributes(remoteVFSDir);

            VFile testfile = testCreateCopyMoveDelete(localTmpDir, remoteVFSDir);
            testRenameTo(remoteVFSDir);
            testDirs(remoteVFSDir);
            testStreams(remoteVFSDir);
        }
        catch (VlException e)
        {
            Error(e);
        }
        catch (Exception e)
        {
            Error(e);
        }
        catch (Throwable e)
        {
            System.err.println("*** Test failed:Throwable ***\n" + " method            = " + testName + "\n"
                    + " Exception         = " + e + "\n" + " Exception Message = " + e.getMessage());

            e.printStackTrace(System.err);
        }

    }

    private void testStreams(VDir remoteVFSDir) throws VlException
    {
        String testFileName = "fileoutput.bin";
        VFile outfile = null;

        try
        {
            // createFile
            newTest("createFile:" + testFileName);
            outfile = remoteVFSDir.createFile(testFileName, true);

            testInputOutput(outfile, 65537, 0);
            testInputOutput(outfile, 65534, 65537);

            outfile.delete();

            outfile = remoteVFSDir.createFile(testFileName, true);
            // testInputOutput(outfile,1024*1024*1024,0);
        }
        finally
        {
            if ((outfile != null) && (outfile.exists()))
                outfile.delete();
        }

    }

    private void testInputOutput(VFile outfile, long newSize, long oldSize) throws VlException
    {
        int bufSize = 256 * 256;

        newTest("testing StreamInput and StreamOutput for newSize,oldSize=" + newSize + "," + oldSize);

        if ((outfile instanceof VStreamWritable) == false)
        {
            // VFile must implement VStreamWritable
            throw new VlException("Cannot tests VStreamWritable interface of NON-VStreamWritable object:" + outfile);
        }

        Logger("Created new stream output file:" + outfile);

        // getLength();
        newTest("check getLength()==oldSize:" + oldSize);
        if (outfile.getLength() != oldSize)
        {
            // VFile must implement VStreamWritable
            throw new VlException("New created file should have length of" + oldSize);
        }

        // getOutputStream();
        newTest("getOutputStream");
        OutputStream outs = ((VStreamWritable) outfile).getOutputStream();
        if (outs == null)
        {
            // VFile must implement VStreamWritable
            throw new VlException("Nu outputstream for:" + outfile);
        }

        // write some bytes
        newTest("write bytes to:" + outs);

        byte buffer[] = new byte[bufSize];
        // fill with little endian words
        for (int i = 0; i < bufSize; i += 2)
        {
            buffer[i] = (byte) (i % 256);
            buffer[i + 1] = (byte) ((i / 256) % 256);
        }
        try
        {
            long totalWritten = 0;

            while (totalWritten < newSize)
            {
                long numToWrite = newSize - totalWritten;
                if (numToWrite > bufSize)
                    numToWrite = bufSize;

                outs.write(buffer, 0, (int) numToWrite);

                totalWritten += numToWrite;
            }
            outs.flush();// must sync before getLength() !
            outs.close();
        }
        catch (IOException e)
        {
            throw new VlException("IOException when write buffer", e);
        }

        // writing less bytes to existing file then current length
        // does not truncate it to new length;

        long checkSize = newSize;
        if (newSize < oldSize)
            checkSize = oldSize;

        // getLength();
        newTest("check getLength()==size:" + checkSize);
        long num = -1;

        if ((num = outfile.getLength()) != checkSize)
        {
            if ((num = outfile.getLength()) == newSize)
            {
                // hmm. some file implementations do truncate when new
                // bytes have been written to an existing file.
                // allow for now since the caller is responsable
                // for checking existing old files -> Document this!
                Warning("original file length has been truncated (is allowed) for:" + outfile);
                checkSize = newSize;
            }
            else
            {
                // VFile must implement VStreamWritable
                throw new VlException("file doesn't have correct file size. size=" + num);
            }
        }

        // clear buffer
        for (int i = 0; i < bufSize; i++)
            buffer[i] = 0;

        // getInputStream();
        newTest("getInputStream");
        InputStream inps = ((VStreamReadable) outfile).getInputStream();
        if (inps == null)
        {
            // VFile must implement VStreamWritable
            throw new VlException("No inputStream for:" + outfile);
        }

        // read some bytes
        int nrread = 0;
        int numToRead = bufSize;

        // truncated file: new file length is checkSize
        if (bufSize > checkSize)
            numToRead = (int) checkSize;

        int totalread = 0;
        newTest("read contents of first buffer. size=" + numToRead);

        try
        {
            // read loop: the InputStream.read doesn't always read all
            // the bytes at once !
            while (totalread < numToRead)
            {
                nrread = inps.read(buffer, totalread, numToRead - totalread);

                if (nrread == -1)
                    throw new IOException("End of file reached:" + outfile);

                totalread += nrread;
            }
        }
        catch (IOException e)
        {
            // extra information:
            Logger("Exception at when reading byte nr" + totalread);
            throw new VlException("IOException when reading buffer:" + e, e);
        }

        if (totalread != numToRead)
        {
            // VFile must implement VStreamWritable
            throw new VlException("Number of bytes read doesn't match. number read=" + totalread);
        }

        // check contents:
        newTest("check contents of first buffer");
        for (int i = 0; i < numToRead; i += 2)
        {
            if ((buffer[i] != (byte) (i % 256)) || (buffer[i + 1] != (byte) ((i / 256) % 256)))
            {
                // VFile must implement VStreamWritable
                throw new VlException("read contents doesn't match written contents at byte:" + i);
            }
        }

        newTest("endTest, keeping testfile:" + outfile);

    }

    /** testACL previous bugs: Regression tests */
    public void testBugs()
    {
        // What ? no bugs ??? :-D
    }

}
