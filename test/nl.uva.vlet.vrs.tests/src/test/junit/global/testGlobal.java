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
 * $Id: testGlobal.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.global;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.Global;
import nl.uva.vlet.bootstrap.Bootstrapper;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSFactory;

public class testGlobal extends TestCase
{
    VFSClient vfs = null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected void setUp()
    {

        // VAttribute=new VAttribute((String)null,(String)null,(String)null);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    public void testGlobalBoot()
    {
        try
        {
            Assert.assertEquals("Global class initialization failed", Global.checkInitialized(), 0);
        }
        catch (Throwable t)
        {
            fail("Global initialization failed with exception:" + t);
        }
    }

    public void testInit()
    {
        Global.init();
    }

    public void testGetRegistry()
    {
        try
        {
            Assert.assertNotNull("Failed to get VRS Registry. Object is NULL", VRS.getRegistry());
        }
        catch (Throwable t)
        {
            fail("Exception when trying to initialize registry:" + t);
        }
    }

    public void testGetVRSContext()
    {
        try
        {
            Assert.assertNotNull("Failed to get default VRSContext. Object is NULL", VRS.getDefaultVRSContext());
        }
        catch (Throwable t)
        {
            fail("Exception when trying to initialize default VRSContext:" + t);
        }
    }

    public void testLocalFSinitialized()
    {
        try
        {
            VRSFactory fac = VRS.getRegistry().getVRSFactoryForScheme("file");
            Assert.assertNotNull("Coudn't get local file system implementation. Registry returned NULL.", fac);
        }
        catch (Throwable t)
        {
            fail("Failed to get local filesystem implementation. Exception=" + t);
        }
    }

    public void testGetVFSClient()
    {
        vfs = new VFSClient();
    }

    private VFSClient _getVFS()
    {
        if (vfs == null)
            vfs = new VFSClient();

        return vfs;
    }

    public void testGetInstallBaseDir() throws VlException
    {
        VRL vrl = Global.getInstallBaseDir();
        checkDirExists(vrl, "installation location 'vlet.install'");
    }

    public void testGetConfigurationDir() throws VlException
    {
        VRL vrl = Global.getInstallationConfigDir();
        checkDirExists(vrl, "configuration location 'etc'");
    }

    private void checkDirExists(VRL vrl, String dirText)
    {
        Assert.assertNotNull("Location of " + dirText + " is NULL", vrl);

        try
        {
            VDir dir = _getVFS().newDir(vrl);
            Assert.assertNotNull("Fetching " + dirText + " resolved to NULL object for location:" + vrl);
            Assert.assertTrue("Resolved directory " + dirText + " doesn't exists (fails from eclipse environment!):"
                    + dir, dir.exists());
        }
        catch (Exception e)
        {
            fail("Error resolving Configuration directory:" + vrl + "\nException=" + e);
        }
    }

    public void testGetLibDir() throws VlException
    {
        VRL vrl = Global.getInstallationLibDir();
        checkDirExists(vrl, "library location 'lib'");
    }

    public void testGetPluginDir() throws VlException
    {
        VRL vrl = Global.getInstallationPluginDir();
        checkDirExists(vrl, "plugin directory 'plugins'");

    }

    public void testGetUserConfigDir()
    {
        // get location only
        VRL vrl = Global.getUserConfigDir();
        Assert.assertNotNull("User configuration location is NULL", vrl);
    }

    public void testExistsInstallationVletrc()
    {
        VRL propLoc = Global.getInstallationConfigDir().append("/vletrc.prop");
        checkFileExists("vlet configuration file 'vletrc.prop'", propLoc);
    }

    private void checkFileExists(String fileText, VRL fileVrl)
    {
        try
        {
            VFile file = _getVFS().newFile(fileVrl);
            Assert.assertNotNull("Resolving File " + fileText + " object returned NULL for properties file:" + fileVrl,
                    file);
            Assert.assertTrue("Vlet installation file 'vletrc.prop' doesn't exist or couldn't be found:" + fileVrl,
                    file.exists());
        }
        catch (Exception e)
        {
            fail("Error resolving vletrc.prop file location:" + fileVrl + "\nException=" + e);
        }
    }

    public void testBootstrapper()
    {
        Bootstrapper boot = new Bootstrapper();

        try
        {
            String args[] = new String[0];
            // boot.launch(nl.uva.vlet.gui.startVBrowser,args);
            boot.launch("nl.uva.vlet.Global", args);
        }
        catch (Throwable t)
        {
            fail("Exception when starting dummy Global.main():" + t);
        }

    }

    public void testGetTempDir()
    {
        try
        {
            VDir dir = _getVFS().getTempDir();
            Assert.assertNotNull("Resolving temporary directory returned NULL object");
            Assert.assertTrue("Temporary directory doesn't exists:" + dir, dir.exists());
        }
        catch (Throwable t)
        {
            fail("Exception when trying to get temporary directory");
        }
    }

}
