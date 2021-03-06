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
 * $Id: testSRMAMC.java,v 1.7 2011-05-10 15:05:25 ptdeboer Exp $  
 * $Date: 2011-05-10 15:05:25 $
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
 * Test SRB case
 * 
 * TestSuite uses testVFS class to tests SRM implementation.
 * 
 * @author P.T. de Boer
 */
public class testSRMAMC extends testSRM
{
    public static VRL getTestLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_SRM_AMC_LOCATION);
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return getTestLocation(); 
    }

    public VRL getOtherRemoteLocation()
    {
        // Use SRM Nikhef as "other"
        return TestSettings.getTestLocation(TestSettings.VFS_SRM_DPM_NIKHEF_LOCATION);
    }

    boolean getTestEncodedPaths()
    {
        return false;
    }

    // ====
    // MAIN
    // ===

    public static Test suite()
    {

        testVFS.staticCheckProxy();
        // Global.setPassiveMode(true);

        // Create ServerInfo registry
        ServerInfo info = null;

        try
        {
            info = VRSContext.getDefault().getServerInfoFor(getTestLocation(), true);
            info.store();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (info == null)
            Global.errorPrintf("testSRM", "Warning: No SRM Server configuration found for:%s\n",getTestLocation()); 
      
        return new TestSuite(testSRMAMC.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

}
