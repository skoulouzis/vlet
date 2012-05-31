///*
// * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: testIrodsSara.java,v 1.3 2011-05-31 14:48:43 ptdeboer Exp $  
// * $Date: 2011-05-31 14:48:43 $
// */ 
//// source: 
//
//package test.junit.vfs;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//import nl.uva.vlet.vfs.VFSClient;
//import nl.uva.vlet.vfs.irods.IRODSConfig;
//import nl.uva.vlet.vfs.irods.IrodsFSFactory;
//import nl.uva.vlet.vfs.lfc.LFCFSConfig;
//import nl.uva.vlet.vfs.lfc.LFCFSConfig.ReplicaCreationMode;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.ServerInfo;
//import nl.uva.vlet.vrs.VRS;
//import nl.uva.vlet.vrs.VRSContext;
//import test.junit.TestSettings;
//
///**
// * Test SRB case
// * 
// * TestSuite uses testVFS class to tests SRB implementation.
// * 
// * @author P.T. de Boer
// */
//public class testIrodsSara extends testVFS
//{
//    @Override
//    public VRL getRemoteLocation()
//    {
//        return sgetRemoteLocation(); 
//    }
//    
//    public static VRL sgetRemoteLocation()
//    {
//        return TestSettings.testIrodsSaraLocation;
//    }
//
//    public static Test suite()
//    {
//        try
//        {
//            VRS.getRegistry().addVRSDriverClass(IrodsFSFactory.class);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        
//        testVFS.staticCheckProxy();
//        // Global.setPassiveMode(true);
//
//        VRSContext context = VFSClient.getDefault().getVRSContext();
//        VRL remoteLoc=sgetRemoteLocation(); 
//        ServerInfo info = ServerInfo.createFor(context,remoteLoc ); 
//        // Use SARA IRODS
//        info.setAttribute(IRODSConfig.ATTR_IRODS_DEFAULT_RESOURCE,"ams_els"); 
//        info.setAttribute(IRODSConfig.ATTR_IRODS_ZONE,"SARA_BIGGRID");
//        info.setAttribute(IRODSConfig.ATTR_IRODS_USERNAME,"piter_de_boer");
//        String startPath=remoteLoc.getDirdirname(); 
//        info.setAttribute(IRODSConfig.ATTR_IRODS_HOMES_PREFIX,startPath); // use parent
//        
//        // there is not root path on irods, start with "homes": 
//        info.setRootPath(startPath); 
//        
//        //
//        // info.setServerAttribute(attr);
//        //
//
//        info.store();
//
//        return new TestSuite(testIrodsSara.class);
//    }
//
//    public static void main(String args[])
//    {
//        junit.textui.TestRunner.run(suite());
//    }
//
//}
