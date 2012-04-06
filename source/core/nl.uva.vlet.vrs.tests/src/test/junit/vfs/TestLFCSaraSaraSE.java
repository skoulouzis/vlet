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
 * $Id: TestLFCSaraSaraSE.java,v 1.6 2011-05-02 13:28:46 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:46 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.lfc.LFCFSConfig;
import nl.uva.vlet.vfs.lfc.LFCFSConfig.ReplicaCreationMode;
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
public class TestLFCSaraSaraSE extends testVFS
{
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.testLFCJSaraLocation;
    }

    public static Test suite()
    {
        // try
        // {
        // VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.lfcfs.LFCFSFactory.class);
        // VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.srmfs.SRMFSFactory.class);
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }

        testVFS.staticCheckProxy();
        // Global.setPassiveMode(true);

        VRSContext context = VFSClient.getDefault().getVRSContext();

        ServerInfo info = ServerInfo.createFor(context, TestSettings.testLFCJSaraLocation);
        // Use SARA LFC+SE !
        info.setAttribute(LFCFSConfig.ATTR_PREFERREDSSES, "srm.grid.sara.nl");
        info.setAttribute(LFCFSConfig.ATTR_REPLICA_CREATION_MODE, ReplicaCreationMode.PREFERRED.getValue());
        //
        // info.setServerAttribute(attr);
        //

        info.store();

        return new TestSuite(TestLFCSaraSaraSE.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

}
