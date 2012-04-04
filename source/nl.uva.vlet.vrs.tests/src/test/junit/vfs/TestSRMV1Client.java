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
 * $Id: TestSRMV1Client.java,v 1.4 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.srm.SRMFileSystem;
import nl.uva.vlet.vrl.VRL;
import test.junit.VTestCase;

public class TestSRMV1Client extends VTestCase
{
    public static VFSClient vfs=null; 
    
    public static Test suite()
    {
        try
        {
            vfs=new VFSClient(); 
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }

        return new TestSuite(TestSRMV1Client.class);
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

    // ===
    // === Actual Tests === 
    // ===
    
    
    public void testSRMV1GetTransportURL()
    {
        try
        {
            String surl="srm://tbn18.nikhef.nl:8443/dpm/nikhef.nl/home/pvier/Piter/test.txt";
            VRL svrl=new VRL(surl); 
           
            VFile srmFile=vfs.newFile(svrl);
            
//            VFSNode[] files = srmFile.getParent().list(); 
//            for (VFSNode node:files)
//                System.out.println(" - node ="+node); 
            
            SRMFileSystem srmfs = (SRMFileSystem)srmFile.getFileSystem(); 
            
             
            
            DefaultTaskMonitor monitor=new DefaultTaskMonitor(); 
            
            System.out.println(">>> Storage VRL="+svrl);
            
            monitor.startTask("Get TransportVRL",1); 
            VRL tvrl=srmfs.getTransportVRL(monitor,svrl.getPath()); 
            
            System.out.println(">>> Transport VRL="+tvrl); 
            
            // VRS.exit(); 
            
        }
        catch (Exception e)
        {
            e.printStackTrace();   
            Assert.fail("Got Exception:"+e); 
        }    
        
        
    }
    
        
}
