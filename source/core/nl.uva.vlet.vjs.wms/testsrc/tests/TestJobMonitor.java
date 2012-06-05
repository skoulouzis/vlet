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
// * $Id: TestJobMonitor.java,v 1.4 2011-04-18 12:28:51 ptdeboer Exp $  
// * $Date: 2011-04-18 12:28:51 $
// */ 
//// source: 
//
//package tests;
//
//import nl.uva.vlet.Global;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.gui.viewers.grid.jobmonitor.JobMonitor;
//import nl.uva.vlet.vjs.wms.WMSFactory;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.VRS;
//
//public class TestJobMonitor
//{
//    public static void main(String args[])
//    {
//        try
//        {
//            Global.init();
//            VRS.getRegistry().addVRSDriverClass(WMSFactory.class);
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        try
//        {
//
//            JobMonitor jobMonitor = new JobMonitor(true);
//            jobMonitor.startAsStandAloneApplication(new VRL("file:/home/ptdeboer/jobs/test.vljids"));
//        }
//        catch (VlException e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void startVBrowser(String args[])
//    {
//        nl.uva.vlet.gui.startVBrowser.main(args);
//    }
//
//}
