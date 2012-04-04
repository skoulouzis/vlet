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
 * $Id: testSFTP_elab.java,v 1.1 2011-09-23 13:38:06 ptdeboer Exp $  
 * $Date: 2011-09-23 13:38:06 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.dialog.AuthenticationDialog;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import test.junit.TestSettings;

/**
 * Test SRB case
 * 
 * TestSuite uses testVFS class to tests SRB implementation.
 * 
 * @author P.T. de Boer
 */
public class testSFTP_elab extends testVFS
{
    static private ServerInfo info;

    public testSFTP_elab()
    {
        // this.doRename=false;
        // this.doWrites=false;
    }

    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_SFTP_ELAB_LOCATION); 
    }

    public static void authenticate() throws VlException
    {
        if (info == null)
            info = TestSettings.getServerInfoFor(TestSettings.getTestLocation(TestSettings.VFS_SFTP_ELAB_LOCATION), true);

        info.store();

        if (info.hasValidAuthentication() == false)
        {
            ServerInfo ans = AuthenticationDialog.askAuthentication("Password for:" + info.getUsername() + "@"
                    + info.getHostname(), info);

            if (ans == null)
            {
                // fail("Authentication Failed!!!");
            }

            ans.store(); // store in ServerInfo database !
        }
    }

    public static Test suite()
    {
        try
        {
            authenticate();
        }
        catch (VlException e)
        {
            e.printStackTrace();

        }

        return new TestSuite(testSFTP_elab.class);
    }

    public static void main(String args[])
    {

        /**
         * VRL vrl=TestConfiguration.testSFTPLocation;
         * 
         * ServerInfo info = new ServerInfo(vrl);
         * 
         * AuthenticationDialog.askAuthentication("Password for:"
         * +info.getUsername()+"@"+info.getHostname(),info);
         * 
         * info.store(); // store in ServerInfo database !
         **/
        // TermGlobal.setDebug(true);

        junit.textui.TestRunner.run(suite());

    }

}
