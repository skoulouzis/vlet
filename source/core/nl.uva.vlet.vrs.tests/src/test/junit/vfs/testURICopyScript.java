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
 * $Id: testURICopyScript.java,v 1.5 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.TestCase;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import test.junit.TestSettings;

public class testURICopyScript extends TestCase
{

    // ========================================================================
    // Instance
    // ========================================================================

    private VFSClient vfs = null;

    private VDir remoteTestDir = null;

    private VDir localTempDir = null;

    /**
     * Override this method if the local test dir has to have a different
     * location
     */
    public VRL getLocalTempDir()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION); 
    }

    Boolean setupMutex = true;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     * 
     * @throws VlException
     */
    protected void setUp() throws VlException
    {
        Global.setDebug(true);

        synchronized (setupMutex)
        {
            if (vfs == null)
                vfs = new VFSClient();

            // create/get only if VDir hasn't been fetched/created before !
            if (localTempDir == null)
            {
                VRL localdir = getLocalTempDir();

                if (vfs.existsDir(localdir))
                {
                    localTempDir = vfs.getDir(localdir);
                    // localTempDir.delete(true);
                }
                else
                {
                    // create complete path !
                    localTempDir = vfs.mkdir(localdir);

                    verbose(1, "created new local test location:" + localTempDir);
                }
            }
        }
    }

    public static String URICOPYSH = "uricopy.sh";

    public static String TEST_CONTENTS = "This is an example text.";

    public void testRunScript() throws VlException
    {
        try
        {
            VFile testfile = localTempDir.createFile("sourceFile.txt", true);
            testfile.setContents(TEST_CONTENTS);

            VDir destdir = localTempDir.createDir("destination", true);

            VRL result = doUriCopy(testfile.getVRL(), destdir.getVRL(), true);
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
        // nl.uva.vlet.LocalExec.simpleExec(cmds)
    }

    private VRL doUriCopy(VRL source, VRL destdir, boolean force) throws VlException
    {

        VRL binvrl = Global.getInstallationBinDir();
        // String binpath=binvrl.getPath();
        VDir bindir = vfs.getDir(binvrl);
        bindir.existsFile(URICOPYSH);
        assertTrue("Couldn't find uricopy.sh script!", bindir.existsFile(URICOPYSH));
        VFile uricopyfile = bindir.getFile(URICOPYSH);

        String cmds[] = new String[4];
        cmds[0] = uricopyfile.getPath();
        cmds[1] = source.toURIString();
        cmds[2] = destdir.toURIString();

        if (force)
            cmds[3] = "-force";
        else
            cmds[3] = "";

        String outs[] = nl.uva.vlet.exec.LocalExec.simpleExecute(cmds);

        verbose(2, "stderr=" + outs[1]);
        verbose(2, "stdout=" + outs[0]);

        // todo
        return null;
    }

    private void verbose(int level, String msg)
    {
        System.out.println(msg);
    }

}
