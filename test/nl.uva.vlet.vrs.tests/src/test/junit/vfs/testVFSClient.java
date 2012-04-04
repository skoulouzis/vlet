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
 * $Id: testVFSClient.java,v 1.4 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;

/**
 * Test VFSClient specific methods not tested in VFSTest! 
 * 
 * @author P.T. de Boer
 */
public class testVFSClient extends TestCase
{
    // VAttribute attribute=null;

    private VFSClient vfsClient = null;

    private VDir testDir = null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     * 
     * @throws VlException
     */
    protected synchronized void setUp() throws VlException
    {
        if (vfsClient == null)
            vfsClient = new VFSClient();

        if (testDir == null)
        {
            testDir = vfsClient.createUniqueTempDir("testVFSClient", "junit");
            Message("new testDir:" + testDir);
        }

    }

    private void Message(String msg)
    {
        System.out.println(msg);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     * 
     * @throws VlException
     */
    protected void tearDown() throws VlException
    {

    }

    protected void finalize()
    {
        Message("deleting:" + testDir);

        try
        {
            if (testDir != null)
                testDir.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        testDir = null;
    }

    // check /tmp dir and relative access into /tmp dir
    public void testTempDir() throws VlException
    {
        VDir tmpdir = vfsClient.getTempDir();

        Assert.assertTrue("Temp dir MUST exist!:" + tmpdir, tmpdir.exists());

        // must have write access:
        VDir newdir = tmpdir.createDir("subdir");
        Assert.assertTrue("Temp dir MUST exist!:" + tmpdir, newdir.exists());

        // must be deletable
        newdir.delete();
        Assert.assertFalse("Temp dir wasn't deleted:" + tmpdir, newdir.exists());

        // create file
        VFile file1 = tmpdir.createFile("file1");

        // check relative access using current working directory
        vfsClient.setWorkingDir(tmpdir);
        VFile file2 = vfsClient.getFile("file1");
        Assert.assertEquals("relative file names do not match", file1.getVRL().toString(), file2.getVRL().toString());

        file1.delete();
        Assert.assertFalse("Temp file was not deleted:" + file1, newdir.exists());

    }

    public void testMkdir() throws VlException
    {
        VDir subDir = vfsClient.mkdir(testDir.getVRL().append("/aap/noot/mies"), false);
        Assert.assertTrue("New subdirectory doesn't exists:" + subDir, subDir.exists());

        Message("Created:" + subDir);
        subDir.delete();
    }

    // Check user home VRL 
    public void testHomeDir() throws VlException
    {
        VRL homeVrl = vfsClient.getUserHomeLocation();
        
        String path=Global.getUserHome();
        
        Assert.assertEquals("Home path and UserHomeLocation must be the same",path,homeVrl.getPath());
    }
    

        
}
