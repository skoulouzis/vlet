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
 * $Id: TestVUnixFileMode.java,v 1.6 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VUnixFileMode;
import nl.uva.vlet.vrs.VNode;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Tests VCommentable files
 * 
 * @author S. Koulouzis
 */
public class TestVUnixFileMode extends VTestCase
{
    private VDir remoteTestDir = null;

    private VFile remoteFile = null;

    private VDir localTempDir;

    static
    {
        Global.getLogger().setLevelToInfo();
    }

    protected synchronized void setUp()
    {
        try
        {

            remoteTestDir = getVFS().mkdirs(TestSettings.testLFCJNikhefLocation, true);

            // remoteFile = remoteTestDir.createFile("DLELETE_ME_TestFile.txt");
            // remoteFile = localFile.copyToDir(remoteTestDir1.getVRL());
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {
        // try
        // {
        // remoteTestDir1.delete();
        // }
        // catch (VlException e)
        // {
        // e.printStackTrace();
        // }
    }

    public void testVUnixMode() throws VlException
    {
        remoteFile = getRemoteTestDir().createFile("testUnixFileMode.txt");

        remoteFile.setContents("Test contents");

        int[] fileModes =
        { 33206, 33279, 33152 };
        // testVUnixNode(remoteFile,fileModes);

        // same with dir
        int[] dirModes =
        { 16740, 16884, 16820 };
        testVUnixNode(getRemoteTestDir(), dirModes);

    }

    private void testVUnixNode(VNode node, int[] testModes) throws VlException
    {
        if (node instanceof VUnixFileMode)
        {
            VUnixFileMode unixFile = (VUnixFileMode) node;

            message("Mode Octal:\t\t\t" + Integer.toOctalString(unixFile.getMode()) + "\t\t\t int: "
                    + unixFile.getMode() + " ");

            message("Setting new mode...Ocatl:\t\t\t" + Integer.toOctalString(testModes[0]) + "\t\t\t int: "
                    + testModes[0]);
            unixFile.setMode(testModes[0]);

            int actualMode = unixFile.getMode();
            message("Got back mode.. Octal:\t\t\t" + Integer.toOctalString(unixFile.getMode()) + "\t\t\t int: "
                    + actualMode + " ");

            assertEquals(testModes[0], actualMode);

            // test next node
            message("Setting new mode...Ocatl:\t\t\t" + Integer.toOctalString(testModes[1]) + "\t\t\t int: "
                    + testModes[1]);
            unixFile.setMode(testModes[1]);
            actualMode = unixFile.getMode();
            message("Got back mode.. Octal:\t\t\t" + Integer.toOctalString(unixFile.getMode()) + "\t\t\t int: "
                    + actualMode + " ");

            assertEquals(testModes[1], actualMode);

            // set r---
            message("Setting new mode...Ocatl:\t\t\t" + Integer.toOctalString(testModes[2]) + "\t\t\t int: "
                    + testModes[2]);
            unixFile.setMode(testModes[2]);

            actualMode = unixFile.getMode();
            message("Got back mode.. Octal:\t\t\t" + Integer.toOctalString(unixFile.getMode()) + "\t\t\t int: "
                    + actualMode + " ");

            // now somehow test if another user can read the file/dir
            if (node instanceof VDir && testModes[2] == 16820)
            {
                VDir dir = (VDir) node;

                try
                {
                    // should get permition denied
                    dir.createFile("someFile.txt");
                }
                catch (Exception ex)
                {
                    if (!(ex instanceof nl.uva.vlet.exception.ResourceAccessDeniedException))
                    {
                        fail("should get permition denied  exeption instead got: " + ex);
                    }
                }
                finally
                {
                    // set rwx rw- r--
                    int mode = 16884;
                    message("Setting new mode...Ocatl:\t\t\t" + Integer.toOctalString(mode) + "\t\t\t int: " + mode);
                    unixFile.setMode(mode);

                    actualMode = unixFile.getMode();
                    message("Got back mode.. Octal:\t\t\t" + Integer.toOctalString(unixFile.getMode()) + "\t\t\t int: "
                            + actualMode + " ");

                    assertEquals(mode, actualMode);
                }
            }
        }
    }

    public VDir getRemoteTestDir()
    {
        return remoteTestDir;
    }

}
