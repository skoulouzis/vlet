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
 * $Id: Test3rdPartyTransfer.java,v 1.7 2011-05-02 13:28:46 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:46 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Assert;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VThirdPartyTransferable;
import nl.uva.vlet.vrl.VRL;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Tests ThirdPartyTransferable interface between GridFTP locations
 * 
 * @author P.T. de Boer
 */
public class Test3rdPartyTransfer extends VTestCase
{
    VDir remoteTestDir1 = null;

    static
    {
        Global.getLogger().setLevelToInfo(); 
    }

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected synchronized void setUp()
    {
        if (remoteTestDir1 == null)
        {
            try
            {
                remoteTestDir1 = getVFS().mkdir(TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION), true);
            }
            catch (VlException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    public void testSameServer() throws VlException
    {
        VRL loc1 =TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);
        VRL loc2 =TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);

        // same location but different names
        testTransfer(loc1, loc2, "file1", "file2");
    }

    public void test2Servers() throws VlException
    {
        VRL loc1 = TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);
        VRL loc2 = TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION2);

        // different locations
        testTransfer(loc1, loc2, "file1", "file2");
    }

    public void test2ServersReverse() throws VlException
    {
        VRL loc1 =TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);
        VRL loc2 = TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION2);
        
        // different locations
        testTransfer(loc2, loc1, "file3", "file4");
    }

    public void testTransfer(VRL sourceDir, VRL targetDir, String sourceFilename, String targetFilename)
            throws VlException
    {
        VDir dir1 = getVFS().mkdir(sourceDir, true);
        VDir dir2 = getVFS().mkdir(targetDir, true);

        String contents = "Test 3rd party transfer";

        VFile file1 = dir1.createFile(sourceFilename);

        file1.setContents(contents);
        VRL remoteLocation = targetDir.append(targetFilename);

        StringHolder explanation = new StringHolder();

        if ((file1 instanceof VThirdPartyTransferable) == false)
        {
            fail("GridFTP file should be VThirdPartyTransferable resources");
        }
        else
        {
            boolean check = ((VThirdPartyTransferable) file1).canTransferTo(remoteLocation, explanation);
            Assert.assertTrue("GrdiFTP should be able to do 3rd party transfers", check);
        }

        // perform transfer
        DefaultTaskMonitor monitor = new DefaultTaskMonitor();
        VFile file2 = ((VThirdPartyTransferable) file1).activePartyTransferTo(monitor, remoteLocation);

        Assert.assertNotNull("Result of 3rd party transfer can not be null", file2);
        Assert.assertTrue("Result file should reports it doesn't exists", file2.exists());
    }

    public void testNotSupported() throws VlException
    {
        VFile file1 = this.remoteTestDir1.createFile("dummyFile123");

        if ((file1 instanceof VThirdPartyTransferable) == false)
        {
            fail("GridFTP file should be VThirdPartyTransferable resource");
        }

        VRL remoteLocation = new VRL("http://www.vl-e.nl/dummypath");
        StringHolder explanation = new StringHolder();

        boolean check = ((VThirdPartyTransferable) file1).canTransferTo(remoteLocation, explanation);
        message("3rd party canTransferTo() method returned:" + explanation.value);
        Assert.assertFalse("GridFTP should be not be able to party transfers to HTTP locations", check);

    }

}
