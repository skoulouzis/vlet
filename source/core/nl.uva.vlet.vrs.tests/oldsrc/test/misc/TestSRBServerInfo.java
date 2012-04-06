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
 * $Id: TestSRBServerInfo.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.misc;

import nl.uva.vlet.Global;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.ServerInfoRegistry;
import nl.uva.vlet.vrs.VRSContext;
import test.junit.TestSettings;

/**
 * 
 * Example connect to LFC server using ServerInfo object.
 * 
 * @author Piter T. de Boer
 * 
 */
public class TestSRBServerInfo
{
    public static void main(String args[])
    {
        Global.init();
        // optional
        // Global.setDebug(true);

        try
        {
            // use default VFS Context:
            VFSClient vfs = new VFSClient();
            VRSContext context = vfs.getVRSContext();

            // Create ServerInfo Object using default context:
            ServerInfoRegistry reg = context.getServerInfoRegistry();

            VRL srbVRL = TestSettings.test_srb_location;

            ServerInfo info = reg.getServerInfoFor(srbVRL, true);
            info.setUseGSIAuth();
            info.setUsePassiveMode(false);

            // Store in the ServerInfo Registry:
            reg.store(info);
            // info.store();

            VDir dir = vfs.getDir(srbVRL);

            // should be valid after authentication.

            System.out.println("I) getUsePassiveMode=" + info.getUsePassiveMode(false));

            // refetch:
            info = reg.getServerInfoFor(srbVRL, true);

            System.out.println("II) getUsePassiveMode=" + info.getUsePassiveMode(false));

            VFSNode nodes[] = dir.list();

            for (VFSNode node : nodes)
            {
                System.out.println(" - " + node);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
