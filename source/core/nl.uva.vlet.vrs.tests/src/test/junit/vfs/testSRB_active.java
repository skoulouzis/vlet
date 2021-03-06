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
 * $Id: testSRB_active.java,v 1.5 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

//package test.junit.vfs;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//import nl.uva.vlet.GlobalConfig;
//import nl.uva.vlet.vfs.srbfs.SrbConfig;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.ServerInfo;
//import nl.uva.vlet.vrs.ServerInfoRegistry;
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
//public class testSRB_active extends testVFS
//{
//    @Override
//    public VRL getRemoteLocation()
//    {
//        return TestSettings.test_srb_location;
//    }
//
//    public static Test suite()
//    {
//        // DO NOT USE SRB DEFAULTS !
//        SrbConfig.setUseSRBPropertiesFile(false);
//
//        testVFS.staticCheckProxy();
//
//        // disable global passive mode !
//        GlobalConfig.setPassiveMode(false);
//
//        VRSContext context = VRSContext.getDefault();
//
//        // Create ServerInfo Object using default context:
//        ServerInfoRegistry reg = context.getServerInfoRegistry();
//
//        VRL srbVRL = TestSettings.test_srb_location;
//
//        ServerInfo info = reg.getServerInfoFor(srbVRL, true);
//
//        info.setUseGSIAuth();
//        // Explicitly disable passive Mode !
//        info.setUsePassiveMode(false);
//
//        // Currently needed:
//        info.setMdasDomainHome("vlenl");
//        info.setMdasDomainName("vlenl");
//        info.setAttribute(ServerInfo.ATTR_DEFAULTRESOURCE, "vleGridStore");
//
//        // Store in the ServerInfo Registry:
//        reg.store(info);
//
//        // check !
//        if (info.getUsePassiveMode(false) == true)
//        {
//            throw new Error("Can't enable active mode !");
//        }
//
//        return new TestSuite(testSRB_active.class);
//    }
//
//    public static void main(String args[])
//    {
//        junit.textui.TestRunner.run(suite());
//    }
//
//}
