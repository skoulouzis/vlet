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
 * $Id: TestWMSResource.java,v 1.19 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package tests;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.LBClient;
import nl.uva.vlet.glite.OutputInfo;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.LBClient.LBJobHolder;
import nl.uva.vlet.glite.WMLBConfig.LBConfig;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vjs.wms.WMSJob;
import nl.uva.vlet.vjs.wms.WMSResource;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wms.wmproxy.ProxyInfoStructType;
import org.glite.wsdl.types.lb.Event;
import org.glite.wsdl.types.lb.EventAbort;
import org.glite.wsdl.types.lb.EventAccepted;
import org.glite.wsdl.types.lb.EventCancel;
import org.glite.wsdl.types.lb.EventCollectionState;
import org.glite.wsdl.types.lb.EventTransfer;
import org.glite.wsdl.types.lb.Timeval;

public class TestWMSResource
{
    static String jdl = "Executable = \"/bin/echo\";\n" + "Arguments = \"Hello World\";\n"
            + "Stdoutput = \"message.txt\";\n" + "StdError = \"stderror\";\n"
            + "OutputSandbox = {\"message.txt\",\"stderror\"};\n" + "virtualorganisation = \"pvier\";\n"
            + "requirements = other.GlueCEName == \"express\";\n" + "rank =- other.GlueCEStateEstimatedResponseTime;\n"
            + "RetryCount = 3;\n";

    private static BdiiService infoService;

    public static void main(String args[])
    {
        // WMSClient.initTransport();

        // AxisUtil.getGT4_AxisEngine();
        // AxisUtil.getOGSA_AxisEngine();

        // testSubmit1();
        // testStatus("https://wms.grid.sara.nl:9000/_NviF0z2EF8mXlXMwedwyQ");

        // test2();

        // test3();

        // test4();

         testGetCollectionTemplate();

        // testGetJOBEvents("https://graskant.nikhef.nl:9000/aPUhMQFCTLgSa4DlrWKWgg");

        // testGetTodaysUserJobs("https://grasveld.nikhef.nl:9003/");
//        testGetTodaysUserJobs("https://graskant.nikhef.nl:9003/");
        // testGetTodaysUserJobs("https://wmslb2.grid.sara.nl:9003/");
        // testGetTodaysUserJobs("https://wms.grid.sara.nl:9003/");

        // testGetTodaysUserJobs("https://graskant.nikhef.nl:9003/");

        VRS.exit();

        // System.exit(0);
    }

    private static void testGetTodaysUserJobs(String LBServer)
    {
        try
        {

            Calendar cal = Calendar.getInstance();

            LBConfig lbconf = WMLBConfig.createLBConfig(new URL(LBServer).getHost(), WMLBConfig.LB_DEFAULT_PORT);
            lbconf.setProxyfilename(VRSContext.getDefault().getGridProxy().getProxyFilename());
            LBClient lb = new LBClient(lbconf);

            // till today
            Timeval to = new Timeval(0, cal.getTimeInMillis(), null);

            // 3 days ago
            cal.add(Calendar.DATE, -5);
            Timeval from = new Timeval(0, cal.getTimeInMillis(), null);

            LBJobHolder[] jobs = lb.getUserJobs(from, to);
            // LBJobHolder[] jobs = lb.getUserJobs();

            System.err.println("Found " + jobs.length + " jobs");
            for (int i = 0; i < jobs.length; i++)
            {
                System.err.println("State [" + i + "] " + jobs[i].jobID);
                // LBClient.printStatus(jobs[i], System.err);

            }
        }
        catch (URISyntaxException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void testGetJOBEvents(String jobID)
    {
        try
        {
            LBConfig lbconf = WMLBConfig
                    .createLBConfig(new URL(jobID).getHost().toString(), WMLBConfig.LB_DEFAULT_PORT);
            lbconf.setProxyfilename(VRSContext.getDefault().getGridProxy().getProxyFilename());

            LBClient lb = new LBClient(lbconf);

            Event[] events = lb.getAllJobEvents(new java.net.URI(jobID));

            message("events len:" + events.length);

            for (Event event : events)
            {
                message("-------------------");
                if (event.getAbort() != null)
                {
                    EventAbort eventAbort = event.getAbort();
                    message("eventAbort : " + eventAbort);
                    if (eventAbort.getArrived() != null)
                    {
                        message("eventAbort.getArrived() : " + eventAbort.getArrived());
                    }
                    if (eventAbort.getHost() != null)
                    {
                        message("eventAbort.getHost() : " + eventAbort.getHost());
                    }
                    if (eventAbort.getJobId() != null)
                    {
                        message("eventAbort.getJobId() : " + eventAbort.getJobId());
                    }
                    if (eventAbort.getLevel() != null)
                    {
                        message("eventAbort.getLevel() : " + eventAbort.getLevel().getValue());
                    }
                    if (eventAbort.getReason() != null)
                    {
                        message("eventAbort.getReason() : " + eventAbort.getReason());
                    }
                    if (eventAbort.getSeqcode() != null)
                    {
                        message("eventAbort.getSeqcode() : " + eventAbort.getSeqcode());
                    }
                    if (eventAbort.getSource() != null)
                    {
                        message("eventAbort.getSource() : " + eventAbort.getSource().getValue());
                    }
                    if (eventAbort.getSource() != null)
                    {
                        message("eventAbort.getTimestamp() : " + eventAbort.getTimestamp());
                    }
                }
                if (event.getCollectionState() != null)
                {
                    EventCollectionState state = event.getCollectionState();
                    message("state.getState(); : " + state.getState());
                }

                EventAccepted eventAccepted = event.getAccepted();
                if (eventAccepted != null)
                {
                    if (eventAccepted.getArrived() != null)
                    {
                        message("eventAccepted.getArrived() : " + eventAccepted.getArrived().getTvSec());
                        message("eventAccepted.getArrived() : " + eventAccepted.getArrived().getTvUsec());
                    }
                    if (eventAccepted.getHost() != null)
                    {
                        message("eventAccepted.getHost() : " + eventAccepted.getHost());
                    }
                    if (eventAccepted.getJobId() != null)
                    {
                        message("eventAccepted.getJobId() : " + eventAccepted.getJobId());
                    }
                    if (eventAccepted.getLevel() != null)
                    {
                        message("eventAccepted.getLevel() : " + eventAccepted.getLevel().getValue());
                    }
                    if (eventAccepted.getUser() != null)
                    {
                        message("eventAccepted.getUser() : " + eventAccepted.getUser());
                    }
                    if (eventAccepted.getSeqcode() != null)
                    {
                        message("eventAccepted.getSeqcode() : " + eventAccepted.getSeqcode());
                    }
                    if (eventAccepted.getSource() != null)
                    {
                        message("eventAccepted.getSource() : " + eventAccepted.getSource().getValue());
                    }
                    if (eventAccepted.getFrom() != null)
                    {
                        message("eventAccepted.getFrom() : " + eventAccepted.getFrom().getValue());
                    }
                    if (eventAccepted.getFromHost() != null)
                    {
                        message("eventAccepted.getFromHost() : " + eventAccepted.getFromHost());
                    }
                    if (eventAccepted.getFromInstance() != null)
                    {
                        message("eventAccepted.getFromInstance() : " + eventAccepted.getFromInstance());
                    }
                    if (eventAccepted.getLocalJobid() != null)
                    {
                        message("eventAccepted.getLocalJobid() : " + eventAccepted.getLocalJobid());
                    }
                    message("eventAccepted.getPriority() : " + eventAccepted.getPriority());
                    if (eventAccepted.getSeqcode() != null)
                    {
                        message("eventAccepted.getSeqcode() : " + eventAccepted.getSeqcode());
                    }
                    if (eventAccepted.getSrcInstance() != null)
                    {
                        message("eventAccepted.getSrcInstance() : " + eventAccepted.getSrcInstance());
                    }
                    if (eventAccepted.getTimestamp() != null)
                    {
                        message("eventAccepted.getTimestamp() : " + eventAccepted.getTimestamp().getTvSec());
                        message("eventAccepted.getTimestamp() : " + eventAccepted.getTimestamp().getTvUsec());
                    }
                    if (eventAccepted.getUser() != null)
                    {
                        message("eventAccepted.getUser() : " + eventAccepted.getUser());
                    }
                }
                EventCancel eventCancel = event.getCancel();
                if (eventCancel != null)
                {
                    if (eventCancel.getArrived() != null)
                    {
                        message("eventCancel : " + eventCancel.getArrived());
                    }
                    if (eventCancel.getHost() != null)
                    {
                        message("eventCancel.getHost() : " + eventCancel.getHost());
                    }
                    if (eventCancel.getJobId() != null)
                    {
                        message("eventCancel.getJobId() : " + eventCancel.getJobId());
                    }
                    if (eventCancel.getLevel() != null)
                    {
                        message("eventCancel.getLevel() : " + eventCancel.getLevel().getValue());
                    }
                    if (eventCancel.getUser() != null)
                    {
                        message("eventCancel.getUser() : " + eventCancel.getUser());
                    }
                    if (eventCancel.getSeqcode() != null)
                    {
                        message("eventCancel.getSeqcode() : " + eventCancel.getSeqcode());
                    }
                    if (eventCancel.getSource() != null)
                    {
                        message("eventCancel.getSource() : " + eventCancel.getSource().getValue());
                    }
                    message("eventCancel.getPriority() : " + eventCancel.getPriority());
                    if (eventCancel.getSeqcode() != null)
                    {
                        message("eventCancel.getSeqcode() : " + eventCancel.getSeqcode());
                    }
                    if (eventCancel.getSrcInstance() != null)
                    {
                        message("eventCancel.getSrcInstance() : " + eventCancel.getSrcInstance());
                    }
                    if (eventCancel.getTimestamp() != null)
                    {
                        message("eventCancel.getTimestamp() : " + eventCancel.getTimestamp().getTvSec());
                        message("eventCancel.getTimestamp() : " + eventCancel.getTimestamp().getTvUsec());
                    }
                    if (eventCancel.getUser() != null)
                    {
                        message("eventCancel.getUser() : " + eventCancel.getUser());
                    }
                    if (eventCancel.getArrived() != null)
                    {
                        message("eventCancel.getUser() : " + eventCancel.getArrived().getTvSec());
                        message("eventCancel.getUser() : " + eventCancel.getArrived().getTvUsec());
                    }
                    if (eventCancel.getReason() != null)
                    {
                        message("eventCancel.getReason() : " + eventCancel.getReason());
                    }
                    if (eventCancel.getStatusCode() != null)
                    {
                        message("eventCancel.getStatusCode() : " + eventCancel.getStatusCode().getValue());
                    }
                }
                EventTransfer eventTrans = event.getTransfer();
                if (eventTrans != null)
                {
                    if (eventTrans.getArrived() != null)
                    {
                        message("eventTrans : " + eventTrans.getArrived());
                    }
                    if (eventTrans.getHost() != null)
                    {
                        message("eventTrans.getHost() : " + eventTrans.getHost());
                    }
                    if (eventTrans.getJobId() != null)
                    {
                        message("eventTrans.getJobId() : " + eventTrans.getJobId());
                    }
                    if (eventTrans.getLevel() != null)
                    {
                        message("eventTrans.getLevel() : " + eventTrans.getLevel().getValue());
                    }
                    if (eventTrans.getUser() != null)
                    {
                        message("eventTrans.getUser() : " + eventTrans.getUser());
                    }
                    if (eventTrans.getSeqcode() != null)
                    {
                        message("eventTrans.getSeqcode() : " + eventTrans.getSeqcode());
                    }
                    if (eventTrans.getSource() != null)
                    {
                        message("eventTrans.getSource() : " + eventTrans.getSource().getValue());
                    }
                    message("eventTrans.getPriority() : " + eventTrans.getPriority());
                    if (eventTrans.getSeqcode() != null)
                    {
                        message("eventTrans.getSeqcode() : " + eventTrans.getSeqcode());
                    }
                    if (eventTrans.getSrcInstance() != null)
                    {
                        message("eventTrans.getSrcInstance() : " + eventTrans.getSrcInstance());
                    }
                    if (eventTrans.getTimestamp() != null)
                    {
                        message("eventTrans.getTimestamp() : " + eventTrans.getTimestamp().getTvSec());
                        message("eventTrans.getTimestamp() : " + eventTrans.getTimestamp().getTvUsec());
                    }
                    if (eventTrans.getUser() != null)
                    {
                        message("eventTrans.getUser() : " + eventTrans.getUser());
                    }
                    if (eventTrans.getArrived() != null)
                    {
                        message("eventTrans.getUser() : " + eventTrans.getArrived().getTvSec());
                        message("eventTrans.getUser() : " + eventTrans.getArrived().getTvUsec());
                    }
                    if (eventTrans.getReason() != null)
                    {
                        message("eventTrans.getReason() : " + eventTrans.getReason());
                    }
                    if (eventTrans.getDestHost() != null)
                    {
                        message("eventTrans.getDestHost() : " + eventTrans.getDestHost());
                    }
                    if (eventTrans.getDestination() != null)
                    {
                        message("eventTrans.getDestination() : " + eventTrans.getDestination());
                    }
                    if (eventTrans.getDestJobid() != null)
                    {
                        message("eventTrans.getDestJobid() : " + eventTrans.getDestJobid());
                    }
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void testGetCollectionTemplate()
    {
        VRSContext vrsContext = UIGlobal.getVRSContext();

        try
        {
            infoService = vrsContext.getBdiiService();
            String VO = vrsContext.getVO();
            message("Using VO:" + VO);

            VRL[] vrls = getWMSEndpoints(infoService,VO);

            WMSResource client;
            message("Allowed WMS=" + vrls[0]);
            client = getClient(vrls[0]);

            client.doDelegation("del-id");

            int jobNumber = 3;
            String requirements = "other.GlueCEName == \"express\"";
            String rank = "other.GlueCEStateEstimatedResponseTime";
            String template = client.getCollectionTemplate(jobNumber, requirements, rank);

            String[] jdlTemp = template.split(";");

            for (String job : jdlTemp)
            {
                System.err.println(job);
            }

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void test4()
    {
        VRSContext vrsContext = UIGlobal.getVRSContext();

        try
        {
            infoService = vrsContext.getBdiiService();
            String VO = vrsContext.getVO();
            message("Using VO:" + VO);

            VRL[] vrls = getWMSEndpoints(infoService,VO);
            
            WMSResource client;
            for (VRL vrl : vrls)
            {
                message("Allowed WMS=" + vrl);
                client = getClient(vrl);

                client.doDelegation("del-id");

                VRL jdlFile = new VRL("file://localhost/home/skoulouz/jdls/TestJiob5.jdl");
                WMSJob job = client.submitJdlFile(jdlFile);

                String jobid = job.getJobId();

                message(">>>>Job ID=" + jobid);

                boolean check = true;

                while (check)
                {
                    String status = job.getStatus();

                    System.out.println(" - checking state = " + status);
                    System.out.println(" [attrs] \n" + " - isRunning      :" + job.isRunning() + "\n"
                            + " - hasTerminated  :" + job.hasTerminated() + "\n" + " - hasError       :"
                            + job.hasError() + "\n" + " - getErrorText   :" + job.getErrorText() + "\n");

                    // System.out.println("LB status (2): "+
                    // lb.getStatus(job.getJobID()));

                    if (job.hasTerminated())
                        check = false;
                    else
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }

                break;

            }

        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//    private static void test3()
//    {
//        VRSContext vrsContext = UIGlobal.getVRSContext();
//
//        try
//        {
//            infoService = vrsContext.getBdiiService();
//            String VO = vrsContext.getVO();
//            message("Using VO:" + VO);
//
////            VRL[] vrls = infoService.getLBServerEndpoints(VO);
//
//            JobStatus[] statuses;
//            for (VRL vrl : vrls)
//            {
//                message("LB: " + vrl);
//                // LBClient lb = LBClient.createService(vrl.toURL());
//                //                
//                //                
//                // statuses = lb.userJobs();
//                //                
//                // for (JobStatus stat:statuses){
//                // message("Status: "+stat.getState().getValue());
//                // }
//
//            }
//        }
//        catch (VlException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (Exception e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

    private static void test2()
    {
        VRSContext vrsContext = UIGlobal.getVRSContext();

        try
        {
            infoService = vrsContext.getBdiiService();
            String VO = vrsContext.getVO();
            message("Using VO:" + VO);

            VRL[] vrls = getWMSEndpoints(infoService,VO);

            for (VRL vrl : vrls)
            {
                message("Allowed WMS=" + vrl);
            }

            WMSResource client = getClient(vrls[0]);

            message("Using WMS:" + vrls[0]);

            message("WMS version = " + client.getVersion());

            client.doDelegation("del-id");

            // VRL jdlFile = new
            // VRL("file://localhost/home/skoulouz/jdls/TestJiob.jdl");
            // WMSJob job = client.submitJdlFile(jdlFile);

            // https://graszode.nikhef.nl:7443/glite_wms_wmproxy_server
            WMSJob job = client.getJob("https://grasveld.nikhef.nl:9000/wZYiDrttycTQCZDcFUiWaA");

            String jobid = job.getJobId();

            message("Job ID=" + jobid);

            String status = job.getStatus();
            System.out.println(" - checking state = " + status);
            System.out.println(" [attrs] \n" + " - isRunning      :" + job.isRunning() + "\n" + " - hasTerminated  :"
                    + job.hasTerminated() + "\n" + " - hasError       :" + job.hasError() + "\n"
                    + " - getErrorText   :" + job.getErrorText() + "\n");

            // https://grasveld.nikhef.nl:9000/wZYiDrttycTQCZDcFUiWaA
            // LBClient lb =
            // LBClient.createServiceFromJobID("https://grasveld.nikhef.nl:9003/");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void testSubmit1()
    {
        // Global.setDebug(true);
        GridProxy.getDefault();
        VRSContext vrsContext = UIGlobal.getVRSContext();

        try
        {
            infoService = vrsContext.getBdiiService();

            String VO = vrsContext.getVO();
            message("Using VO:" + VO);

            VRL[] vrls = getWMSEndpoints(infoService,VO);

            for (VRL vrl : vrls)
            {
                message("Allowed WMS=" + vrl);
            }

            int num = 0;
            VRL wmsVrl = new VRL("https://wms.grid.sara.nl:7443/glite_wms_wmproxy_server");

            WMSResource client = getClient(wmsVrl);

            message("Using WMS:" + wmsVrl);

            message("WMS version = " + client.getVersion());

            client.doDelegation("test-del-piter");

            WMSJob job = client.submitJdlString(jdl);

            // WMSJob job = client.submitJdlFile(new
            // VRL("file:///home/ptdeboer/jobs/jtest.jdl"));

            message("Job ID=" + job.getJobId());
            String jobid = job.getJobId();
            // Spiros: Dead variable
            // vrls = infoService.getLBServerEndpoints(VO);

            // LBClient lb = LBClient.createService(new
            // URL("https://grasveld.nikhef.nl:9003/"));
            // https://grasveld.nikhef.nl:9003/
            // LBClient lb = LBClient.createServiceFromJobID(jobid,9003);

            boolean check = true;

            while (check)
            {
                String status = job.getStatus();

                System.out.println(" - checking state = " + status);
                System.out.println(" [attrs] \n" + " - isRunning      :" + job.isRunning() + "\n"
                        + " - hasTerminated  :" + job.hasTerminated() + "\n" + " - hasError       :" + job.hasError()
                        + "\n" + " - getErrorText   :" + job.getErrorText() + "\n");

                // System.out.println("LB status (2): "+
                // lb.getStatus(job.getJobID()));

                if (job.hasTerminated())
                    check = false;
                else
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }

            LBClient.printStatus(job.getLBJobStatus(), System.out);

            check = true;

            if (check)
            {
                try
                {

                    System.out.println(" --- checking output --- ");

                    ArrayList<OutputInfo> outputs = client.getJob(jobid).getOutputs();

                    if (outputs != null)
                    {
                        for (OutputInfo output : outputs)
                        {
                            System.out.println(" - " + output.getFilename() + " #" + output.getSize());
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                Thread.sleep(250);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    // public static void testGetProxyInfo(String jobID)
    // {
    // // Global.setDebug(true);
    //
    // try
    // {
    // WMSResource client = getClient(null);
    //
    // message("WMS version getVersion() = " + client.getVersion());
    //
    // // delegation already done:
    // client.setDelegationId("test-del-id-1234567");
    //
    // ProxyInfoStructType status = client.getJob(jobID).getJobProxyInfo();
    //
    // print(status);
    //
    // }
    // catch (VlException e)
    // {
    // e.printStackTrace();
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    //
    // }

    private static WMSResource getClient(VRL wmsEndpoint) throws VlException
    {
        // VRL wmsUri=new VRL(wmsEndpoint);

        VRL wmsVrl = new VRL("wms://" + wmsEndpoint.getHostname() + ":" + wmsEndpoint.getPort());
        // VRL wmsVrl=new VRL("wms://wms.grid.sara.nl:7443/");

        VRSContext context = VRSContext.getDefault();
        ServerInfo info = ServerInfo.createFor(context, wmsVrl);

        WMSResource client = new WMSResource(context, wmsVrl, info);
        client.connect();

        return client;

    }

    private static void print(ProxyInfoStructType status)
    {
        System.out.println("---- Job Info ---\n" + "\n subject    = " + status.getSubject() + "\n type       = "
                + status.getType() + "\n start time = " + status.getStartTime() + "\n end time   = "
                + status.getEndTime() + "\n--- ---\n");
    }

    private static void message(String msg)
    {
        System.err.println("TestWMS:" + msg);
    }
    
    public static VRL[] getWMSEndpoints(BdiiService bdii, String vo) throws VlException
    {
        ArrayList<ServiceInfo> infos = bdii.getWMSServiceInfos(vo);
        
        VRL[] endpoint = new VRL[infos.size()];
        for (int i = 0; i < infos.size(); i++)
        {
            endpoint[i] = infos.get(i).getServiceVRL();
        }

        return endpoint;
    }
}
