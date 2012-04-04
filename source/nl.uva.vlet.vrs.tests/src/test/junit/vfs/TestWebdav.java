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
 * $Id: TestWebdav.java,v 1.4 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.Global;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import test.junit.TestSettings;

/**
 *  
 * @author S. Koulouzis
 */
public class TestWebdav extends testVFS
{
    public static VRL getTestLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_WEBDEV_LOCATION_1);
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return getTestLocation(); 
    }

    public VRL getOtherRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_WEBDEV_LOCATION_2);
    }

    
    boolean getTestEncodedPaths()
    {
        return true;
    }

    // ====
    // MAIN
    // ===

    public static Test suite()
    {
        Global.init();
        
        testVFS.staticCheckProxy();
        
        // Create ServerInfo registry
        ServerInfo info = null;

        try
        {
            // Initialize webdav plugin:
            //VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.webdavfs.WebdavFSFactory.class);
            
            info = VRSContext.getDefault().getServerInfoFor(getTestLocation(), true);
            info.store();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (info == null)
            Global.errorPrintf("TestWebdav", "Warning: No Webdav Server configuration found for:%s\n",getTestLocation()); 
        
        return new TestSuite(TestWebdav.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

}
