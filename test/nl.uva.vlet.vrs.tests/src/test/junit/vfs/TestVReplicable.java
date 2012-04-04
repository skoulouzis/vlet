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
 * $Id: TestVReplicable.java,v 1.6 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VReplicatable;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Tests VCommentable files
 * 
 * @author S. Koulouzis
 */
public class TestVReplicable extends VTestCase
{
    VDir remoteTestDir1 = null;

    static
    {
        Global.getLogger().setLevelToInfo();
    }

    protected synchronized void setUp()
    {

        if (remoteTestDir1 == null)
        {
            try
            {
                remoteTestDir1 = getVFS().mkdir(TestSettings.testLFCJNikhefLocation, true);
            }
            catch (VlException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void testVReplicatable() throws VlException
    {

        String fileName = "testReplicable.txt";
        VFile remoteFile = null;

        // if file has no replic create one
        if (!getRemoteTestDir().existsFile(fileName))
        {
            remoteFile = getRemoteTestDir().createFile(fileName);
            remoteFile.setContents("Test contents");
        }
        else
        {
            remoteFile = getRemoteTestDir().getFile(fileName);
        }

        if ((remoteFile instanceof VReplicatable) == false)
        {
            // delete?
            return;
        }
        VReplicatable replicable = (VReplicatable) remoteFile;
        long len = remoteFile.getLength();

        VRL[] replicas = replicable.getReplicas();
        ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Test VReplicable:", -1);

        // Keep only one replica
        for (int i = 0; i < replicas.length; i++)
        {
            message("Registered replica VRLs: " + replicas[i]);
            // if(i>=1){
            // replicable.deleteReplica(monitor, replicas[i].getHostname());
            // }
        }

        BdiiService service = VRSContext.getDefault().getBdiiService();

        // get all se
        ArrayList<StorageArea> se = service.getSRMv22SAsforVO(VRSContext.getDefault().getVO());

        VFSClient vfs = new VFSClient();

        unregisterEmptyRep(replicable, vfs);

        VRL replicaVRL;
        VFile replicaFile;
        boolean success = false;
        // replacate to all except rug
        // for (int i = 0; i < se.size(); i++)
        for (int i = 0; i < 3; i++)
        {

            if (!se.get(i).getHostname().equals(TestSettings.BLACK_LISTED_SE[0])
                    && !se.get(i).getHostname().equals(TestSettings.BLACK_LISTED_SE[1])
                    && !se.get(i).getHostname().equals(replicas[0].getHostname()))
            {
                message("Storage Element: " + se.get(i).getHostname());
                message("   Replicating");
                replicaVRL = replicable.replicateTo(monitor, se.get(i).getHostname());
                replicaFile = vfs.getFile(replicaVRL);
                message("   Replecated to: " + replicaFile);

                // ----Check if it is correctly replicated-------------
                // is file created ??
                boolean exists = replicaFile.exists();
                message("   Exists?: " + exists);
                assertTrue(exists);
                // is the same size??
                long replicaLen = replicaFile.getLength();
                message("   Original length " + len + " replica lenght: " + replicaLen);
                assertEquals(len, replicaLen);

                // checksum
                if ((replicaFile instanceof VChecksum) && (remoteFile instanceof VChecksum))
                {
                    String replicaChecksome = ((VChecksum) replicaFile).getChecksum(VChecksum.MD5);
                    String remoteFileChecksome = ((VChecksum) remoteFile).getChecksum(VChecksum.MD5);
                    assertEquals(remoteFileChecksome, replicaChecksome);
                }

                // -----------------------DELETE----------------------------
                message("   Deleteing");
                success = replicable.deleteReplica(monitor, se.get(i).getHostname());
                assertTrue(success);
                exists = replicaFile.exists();
                message("   Exists?: " + exists);
                assertFalse(exists);
            }
            else
            {
                message("Ooops it's " + se.get(i).getHostname() + " don't replicate there");
            }
        }

        // test Exception
        try
        {
            replicaVRL = replicable.replicateTo(monitor, "NON-EXISTING-SE");
            message("New replica!!: " + replicaVRL);
            fail("Replicating to UNKNOWN storage element should fail");
        }
        catch (Exception ex)
        {
            if (!(ex instanceof ResourceCreationFailedException))
            {
                fail("Wrong exeption. Should be ResourceCreationFailedException instead got: " + ex);
            }
            else
            {
                message("Got back correct exception: " + ex);
            }
            ex.printStackTrace();
        }

        // get back the replicas VRL
        replicas = replicable.getReplicas();

        message("Listing replicas.........");
        for (int i = 0; i < replicas.length; i++)
        {
            message("Registered replica VRLs: " + replicas[i]);
        }

        // Test Register
        int VRllen = 4;
        VRL[] vrls = new VRL[VRllen];
        // warning!!! if you include port number LFC will remove it
        for (int i = 0; i < VRllen; i++)
        {
            vrls[i] = new VRL("scheme://host.at.some.domain/path/to/file" + i);
        }

        success = replicable.registerReplicas(vrls);
        assertTrue(success);

        // get back the replicas VRL
        replicas = replicable.getReplicas();
        List<VRL> vrlList = Arrays.asList(replicas);

        message("Listing replicas.........");
        for (int i = 0; i < replicas.length; i++)
        {
            message("Registered replica VRLs: " + replicas[i]);
            if (i < vrls.length)
            {
                // message("Is " + vrls[i] + " contained in vrlList?");
                // check if registered vrls are there
                if (!vrlList.contains(vrls[i]))
                {
                    fail("Didn't get back the same VRLs from the service. " + replicas[i]
                            + " is not contained in the registered VRS");
                }
            }

        }

        message("Unregistering replicas.........");
        success = replicable.unregisterReplicas(vrls);
        assertTrue(success);

        replicas = replicable.getReplicas();
        vrlList = Arrays.asList(replicas);

        message("Listing replicas.........");
        for (int i = 0; i < replicas.length; i++)
        {
            message("Registered replica VRLs: " + replicas[i]);
            if (i < vrls.length)
            {
                // check if unregistered vrls are gone
                if (vrlList.contains(vrls[i]))
                {
                    fail("Didn't remove VRLs. " + replicas[i] + " is contained in the registered VRS");
                }
            }

        }

        // clean up
        success = remoteFile.delete();
        assertTrue(success);
    }

    private static void unregisterEmptyRep(VReplicatable rep, VFSClient vfs)
    {
        VRL[] replicas = null;
        if (vfs == null)
        {
            vfs = new VFSClient();
        }
        VFile file = null;
        ArrayList<VRL> emptyRep = new ArrayList<VRL>();
        try
        {
            replicas = rep.getReplicas();
        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        message("Listing replicas.........");
        for (int i = 0; i < replicas.length; i++)
        {

            try
            {
                // file = vfs.newFile(replicas[i]);
                file = vfs.getFile(replicas[i]);
                if (!file.exists())
                {
                    message("Replica VRL: " + replicas[i] + " doesn't exist. Unregistering");
                    emptyRep.add(replicas[i]);
                }
            }
            catch (VlException e)
            {
                message("Replica VRL: " + replicas[i] + " doesn't exist. Unregistering");
                emptyRep.add(replicas[i]);
            }
        }

        VRL[] emptyRepArray = new VRL[emptyRep.size()];
        emptyRepArray = emptyRep.toArray(emptyRepArray);
        try
        {
            rep.unregisterReplicas(emptyRepArray);
            replicas = rep.getReplicas();
        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        message("Listing replicas.........");
        for (int i = 0; i < replicas.length; i++)
        {
            message("Remaing replicas: " + replicas[i]);
        }

    }

    private VDir getRemoteTestDir()
    {
        return remoteTestDir1;
    }
}
