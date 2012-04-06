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
 * $Id: testGFTP_active.java,v 1.9 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import test.junit.TestSettings;

/**
 * Test SRB case
 * 
 * TestSuite uses testVFS class to tests SRB implementation.
 * 
 * @author P.T. de Boer
 */

public class testGFTP_active extends testVFS
{

    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);
    }

    public static Test suite()
    {
        GlobalConfig.setSystemProperty(GlobalConfig.PROP_PERSISTANT_USER_CONFIGURATION,"false");
        GlobalConfig.setPassiveMode(false);
        GlobalConfig.setIncomingFirewallPortRange(25000, 25500);
        
        setVerbose(VERBOSE_INFO);
        // Global.getLogger().setVerboseLevel(GlobalLogger.VERBOSE_INFO);

        // Make sure to match this with firewall settings!
        int ints[] = Global.getIncomingFirewallPortRange();
        Assert.assertNotNull("Returned firewall portrange may NOT be NULL", ints);
        Assert.assertEquals("Returned firewall portrange must be array of two integers", 2, ints.length);
        message("Portrange=" + ints[0] + "," + ints[1]);

        ServerInfo info;
        
        // Explicit Active mode for test server:
        try
        {
            info = VRSContext.getDefault().getServerInfoFor(TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION), true);
            info.setAttribute(GlobalConfig.PROP_PASSIVE_MODE, false);
            info.store();

        }
        catch (VlException e)
        {
            e.printStackTrace();
        }

        testVFS.staticCheckProxy();

        return new TestSuite(testGFTP_active.class);
    }

    public static void main(String args[])
    {

        junit.textui.TestRunner.run(suite());

    }

}
