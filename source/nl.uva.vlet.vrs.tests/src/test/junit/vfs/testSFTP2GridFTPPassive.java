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
 * $Id: testSFTP2GridFTPPassive.java,v 1.4 2011-09-23 13:38:06 ptdeboer Exp $  
 * $Date: 2011-09-23 13:38:06 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.vrl.VRL;
import test.junit.TestSettings;

/**
 * Test Stream Copy or First Party Transfers!
 * 
 * TestSuite uses testVFS class to stream copy between GFTP as "local" 
 * an SFTP as "remote"
 * 
 * @author P.T. de Boer
 */
public class testSFTP2GridFTPPassive extends testVFS
{
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_SFTP_ELAB_LOCATION);
    }

    public VRL getLocalTempDirVRL()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_GFTP_LOCATION);
    }

    public static Test suite()
    {
        testVFS.staticCheckProxy();
        GlobalConfig.setPassiveMode(true);

        return new TestSuite(testSFTP2GridFTPPassive.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

}
