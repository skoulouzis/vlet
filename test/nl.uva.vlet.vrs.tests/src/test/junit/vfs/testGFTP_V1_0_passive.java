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
 * $Id: testGFTP_V1_0_passive.java,v 1.6 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.vrl.VRL;
import test.junit.TestSettings;

/**
 * Test SRB case
 * 
 * TestSuite uses testVFS class to tests SRB implementation.
 * 
 * @author P.T. de Boer
 */

public class testGFTP_V1_0_passive
{
    public static class testVFS_GFTP extends testVFS
    {
        @Override
        public VRL getRemoteLocation()
        {
            return TestSettings.getTestLocation(TestSettings.VFS_GFTP_V1_LOCATION);
        }
    }

    public static Test suite()
    {
        GlobalConfig.setPassiveMode(true);
        // Global.setDebug(true);

        testVFS.staticCheckProxy();

        return new TestSuite(testVFS_GFTP.class);
    }

    public static void main(String args[])
    {

        junit.textui.TestRunner.run(suite());

    }

}
