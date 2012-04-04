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
 * $Id: testJavafile.java,v 1.5 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vrl.VRL;
import test.junit.TestSettings;
import test.junit.VTestCase;

public class testJavafile extends VTestCase
{
    /**
     * Override this method if the local test dir has to have a different
     * location
     */
    public VRL getLocalTempDir()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION); 
    }

    Boolean setupMutex = true;

    private VDir localTempDir;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     * 
     * @throws VlException
     */
    protected void setUp() throws VlException
    {
        // verbose(3,"setUp(): Checking remote test location:"+getRemoteLocation());

        synchronized (setupMutex)
        {
            // create/get only if VDir hasn't been fetched/created before !
            // if (remoteTestDir==null)
            // {
            //                
            // if (getVFS().existsDir(getRemoteLocation()))
            // {
            // remoteTestDir=getVFS().getDir(getRemoteLocation());
            // //remoteTestDir.delete(true);
            // // clean tests dir
            //                    
            // // if
            // (StringUtil.compare(remoteTestDir.getPath(),getRemoteLocation().getPath())!=0)
            // // {
            // // throw new
            // Error("Initialization error. Remote Test Directory is wrong:"+remoteTestDir);
            // // }
            //                    
            // /*VFSNode nodes[]=remoteTestDir.getChilds();
            // if (nodes!=null)
            // for (VFSNode node:nodes)
            // node.delete();
            // */
            // verbose(3,"setUp(): Using remoteDir:"+remoteTestDir);
            //                    
            // }
            // else
            // {
            // // create complete path !
            // try
            // {
            // verbose(1,"creating new remote test location:"+getRemoteLocation());
            //
            // remoteTestDir=getVFS().mkdir(getRemoteLocation());
            //                		
            // verbose(1,"New created remote test directory="+remoteTestDir);
            //
            // }
            // catch (VlException e)
            // {
            // e.printStackTrace();
            // throw e;
            // }
            //                    
            // verbose(1,"created new remote test location:"+remoteTestDir);
            // }
            //                
            // }

            if (localTempDir == null)
            {
                VRL localdir = getLocalTempDir();

                if (getVFS().existsDir(localdir))
                {
                    localTempDir = getVFS().getDir(localdir);
                    // localTempDir.delete(true);
                }
                else
                {
                    // create complete path !
                    localTempDir = getVFS().mkdirs(localdir, true);

                    verbose(1, "created new local test location:" + localTempDir);
                }
            }
        }
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method, so
     * no real cleanup can be done here)
     */
    protected void tearDown()
    {

    }

    public void testPrintInfo()
    {
        message("Current working VFS working dir=" + getVFS().getWorkingDir());
    }

    public void testAbsConstructor1() throws URISyntaxException, IOException
    {
        message("--- Absolute Constructors ---");

        String paths[] =
        { this.localTempDir.getPath(), this.localTempDir.getPath() + "/filedoesnotexist.tmp", };

        for (String path : paths)
        {
            message("abs path=" + path);

            java.io.File jFile = new java.io.File(path);
            nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(path);

            message("java uri=" + jFile.toURI());
            message("vlet uri=" + vFile.toURI());

            checkCompare(jFile, vFile);
            // Use Parent !
            String subName = "file1";
            java.io.File subjFile = new java.io.File(jFile, subName);
            nl.uva.vlet.io.javafile.File subvFile = new nl.uva.vlet.io.javafile.File(vFile, subName);
            Assert.assertEquals("URI of java sub file and vFile don't match", subjFile.toURI(), subvFile.toURI());

        }

    }

    public void testRelConstructor1() throws URISyntaxException, IOException
    {
        message("--- Relative Paths ---");

        String paths[] =
        { "file1", "dir1/file2" };

        for (String path : paths)
        {
            message("-relative path=" + path);

            java.io.File jFile = new java.io.File(path);
            nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(path);

            message("  java uri=" + jFile.toURI());
            message("  vlet uri=" + vFile.toURI());

            message("  java path=" + jFile.getPath());
            message("  vlet path=" + vFile.getPath());

            checkCompare(jFile, vFile);

            // Use Parent !
            String subName = "file1";
            java.io.File subjFile = new java.io.File(jFile, subName);
            nl.uva.vlet.io.javafile.File subvFile = new nl.uva.vlet.io.javafile.File(vFile, subName);
            Assert.assertEquals("URI of java sub file and vFile don't match", subjFile.toURI(), subvFile.toURI());

        }

    }

    public void testUriConstructor1() throws URISyntaxException, IOException
    {
        message("--- URIs ---");

        String paths[] =
        { "file:" + this.localTempDir.getPath(), "file:" + this.localTempDir.getPath() + "/filedoesnotexist.tmp",
                "file://" + this.localTempDir.getPath(),
                "file://" + this.localTempDir.getPath() + "/filedoesnotexist.tmp",

        };

        for (String path : paths)
        {
            message("path=" + path);

            URI uri = new URI(path);
            java.io.File jFile = new java.io.File(uri);
            nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(uri);

            message("java uri=" + jFile.toURI());
            message("vlet uri=" + vFile.toURI());

            checkCompare(jFile, vFile);

            // Use Parent !
            String subName = "file1";
            java.io.File subjFile = new java.io.File(jFile, subName);
            nl.uva.vlet.io.javafile.File subvFile = new nl.uva.vlet.io.javafile.File(vFile, subName);
            Assert.assertEquals("URI of java subfile and subvFile don't match", subjFile.toURI(), subvFile.toURI());

        }

    }

    public void testCreateDelete() throws URISyntaxException, IOException
    {
        String path = "file:" + this.localTempDir.getPath() + "/file1.tmp";

        URI uri = new URI(path);
        java.io.File jFile = new java.io.File(uri);
        nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(uri);

        // ignore and delete previous
        if (vFile.exists())
            vFile.delete();

        Assert.assertFalse("Local testfile already exists!", jFile.exists());
        Assert.assertFalse("Local testfile already exists!", vFile.exists());

        boolean val = vFile.createNewFile();
        Assert.assertTrue("createNewFile must return true", val);

        checkExistsCompareAndDelete(jFile, vFile, false);

    }

    public void testMkdir() throws IOException
    {
        message("--- Mkdir ---");

        String dirpath = this.localTempDir.getPath() + "/newdir";

        java.io.File jFile = new java.io.File(dirpath);
        nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(dirpath);
        // just delete:
        if (vFile.exists())
            vFile.delete();

        Assert.assertFalse("Local test dir already exists!", jFile.exists());
        Assert.assertFalse("Local test dir already exists!", vFile.exists());

        message("PRE java dir uri=" + jFile.toURI());
        message("PRE vlet dir uri=" + vFile.toURI());

        checkCompare(jFile, vFile);

        // create
        Assert.assertTrue("mkdir must return true", vFile.mkdir());
        // existing directories have different URIs ! ('/' appended)
        message("POST java uri=" + jFile.toURI());
        message("POST vlet uri=" + vFile.toURI());

        checkExistsCompareAndDelete(jFile, vFile, true);
    }

    public void testMkdirs() throws IOException
    {
        message("--- Mkdirs ---");

        String dirpath = this.localTempDir.getPath() + "/newdir2/newsubdir";

        java.io.File jFile = new java.io.File(dirpath);
        nl.uva.vlet.io.javafile.File vFile = new nl.uva.vlet.io.javafile.File(dirpath);
        // just delete:
        if (vFile.exists())
            vFile.delete();

        Assert.assertFalse("Local test dir already exists!", jFile.exists());
        Assert.assertFalse("Local test dir already exists!", vFile.exists());

        message("PRE java dir uri=" + jFile.toURI());
        message("PRE vlet dir uri=" + vFile.toURI());

        checkCompare(jFile, vFile);

        // create
        Assert.assertTrue("mkdir must return true", vFile.mkdirs());
        // existing directories have different URIs ! ('/' appended)
        message("POST java uri=" + jFile.toURI());
        message("POST vlet uri=" + vFile.toURI());

        checkExistsCompareAndDelete(jFile, vFile, true);

        // delete parents!
        checkExistsCompareAndDelete(jFile.getParentFile(), vFile.getParentFile(), true);
    }

    private void checkExistsCompareAndDelete(java.io.File jFile, nl.uva.vlet.io.javafile.File vFile, boolean isDir)
            throws IOException
    {
        boolean isFile = (isDir == false);

        Assert.assertTrue("after creation directory (vFile) must exists", vFile.exists());
        Assert.assertTrue("after creation directory (jFile) must exists", jFile.exists());
        Assert.assertEquals("after creation isDirectory (jFile) must be:" + isDir, isDir, jFile.isDirectory());
        Assert.assertEquals("after creation isDirectory (vFile) must be:" + isDir, isDir, vFile.isDirectory());
        Assert.assertEquals("after directory creation isFile (jFile) must be:" + isFile, isFile, jFile.isFile());
        Assert.assertEquals("after directory creation isFile (vFile) must be:" + isFile, isFile, vFile.isFile());

        checkCompare(jFile, vFile);

        // delete
        Assert.assertTrue("delete must return true", vFile.delete());
        Assert.assertFalse("after deletion directory (jFile) must not exist", jFile.exists());
        Assert.assertFalse("after deletion directory (vFile) must not exists", vFile.exists());

    }

    private void checkCompare(java.io.File jFile, nl.uva.vlet.io.javafile.File vFile) throws IOException
    {
        // happens when both parent are NULL !
        if ((jFile == null) && (vFile == null))
            return;

        _checkCompare(jFile, vFile);

        java.io.File jParent = jFile.getParentFile();
        nl.uva.vlet.io.javafile.File vParent = vFile.getParentFile();

        _checkCompare(jParent, vParent);

    }

    private void _checkCompare(java.io.File jFile, nl.uva.vlet.io.javafile.File vFile) throws IOException
    {
        // happens when both parent are NULL !
        if ((jFile == null) && (vFile == null))
            return;

        // both are allowed to be null but not only one of them !
        Assert.assertNotNull("jFile is NULL", jFile);
        Assert.assertNotNull("vFile is NULL", vFile);

        // since an existing directory has an '/' appended to it, check
        // paths,uris again:
        Assert.assertEquals("URI of java file and vFile don't match", jFile.toURI(), vFile.toURI());
        Assert.assertEquals("Relative paths don't match", jFile.getPath(), vFile.getPath());
        Assert.assertEquals("Canonical paths don't match", jFile.getCanonicalPath(), vFile.getCanonicalPath());
        Assert.assertEquals("Absolute paths don't match", jFile.getAbsolutePath(), vFile.getAbsolutePath());

        Assert.assertEquals("implementations disagree about exists()", jFile.exists(), vFile.exists());
        Assert.assertEquals("implementations disagree about isDirectory()", jFile.isDirectory(), vFile.isDirectory());
        Assert.assertEquals("implementations disagree about isFile()", jFile.isFile(), vFile.isFile());
        Assert.assertEquals("implementations disagree about getParent()", jFile.getParent(), vFile.getParent());
    }

}
