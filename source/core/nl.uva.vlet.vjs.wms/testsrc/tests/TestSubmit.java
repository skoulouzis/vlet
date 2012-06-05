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
 * $Id: TestSubmit.java,v 1.12 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package tests;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.LBClient;
import nl.uva.vlet.glite.OutputInfo;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vjs.wms.WMSJob;
import nl.uva.vlet.vjs.wms.WMSResource;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class TestSubmit
{
    static String jdl = "Executable = \"/bin/bash\";\n" 
            + "Arguments = \"-c 'echo Hello World'; echo ; env \";\n"
            + "Stdoutput = \"message.txt\";\n" + "StdError = \"stderror\";\n"
            + "OutputSandbox = {\"message.txt\",\"stderror\"};\n" 
            + "virtualorganisation = \"pvier\";\n"
            // + "requirements = other.GlueCEName == \"express\";\n" 
            + " Requirements = (other.GlueCEPolicyMaxWallClockTime >=30 );\n"
            + "rank =- other.GlueCEStateEstimatedResponseTime;\n"
            + "RetryCount = 3;\n";

    private static BdiiService infoService;

    public static void main(String args[])
    {
        // Global.init();

        //TestWMSBrowser.startVBrowser(new String[] { "lb://wms3.grid.sara.nl:9003/" });

        // TestWMSBrowser.startVBrowser(null);
        try
        {
            Thread.sleep(30);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // testSubmit("graspol.nikhef.nl",jdl,1);
        // testSubmit("graszode.nikhef.nl",jdl,1);

        testSubmit("wms3.grid.sara.nl", jdl, 50);
    }

    public static void testSubmit(String wmsHost, String jdl, int num)
    {
        // Global.setDebug(true);
        GridProxy.getDefault();
        VRSContext vrsContext = UIGlobal.getVRSContext();
        VFSClient vfs=new VFSClient(vrsContext); 
                
        try
        {
            VDir homedir=vfs.getUserHome(); 
            
            VFile jidfile=homedir.newFile("myjobs.jids"); 
            
            infoService = vrsContext.getBdiiService();

            String VO = vrsContext.getVO();
            message("Using VO:" + VO);

            ArrayList<ServiceInfo> infos =  infoService.getWMSServiceInfos(VO);
            
            VRL wmsVrl = null;

            for (ServiceInfo info:infos)
            {
                message("Allowed WMS=" + info.getServiceVRL());
                if (info.getServiceVRL().hasHostname(wmsHost))
                    wmsVrl = info.getServiceVRL(); 
            }

            if (wmsVrl == null)
                throw new Exception("Couldn't match WMS Server:" + wmsHost);

            // VRL wmsVrl =new
            // VRL("https://wms.grid.sara.nl:7443/glite_wms_wmproxy_server");

            WMSResource client = getClient(wmsVrl);

            message("Using WMS:" + wmsVrl);

            message("WMS version = " + client.getVersion());

            client.doDelegation("test-del-piter");

            WMSJob jobs[] = new WMSJob[num];
            for (int i = 0; i < num; i++)
            {
                jobs[i] = client.submitJdlString(jdl);

                // WMSJob job = client.submitJdlFile(new
                // VRL("file:///home/ptdeboer/jobs/jtest.jdl"));

                message("Job ID=" + jobs[i].getJobId());
            }
            
            jidfile.create(true);
            OutputStream outps = jidfile.getOutputStream(); 
            PrintWriter prout=new PrintWriter(outps); 
            
            for (int i = 0; i < num; i++)
            {
                String jobid = jobs[i].getJobId();
                prout.printf("%s\n",jobid); 
            }                
            prout.flush();
            prout.close(); 
            
            // Spiros: Dead variable
            // vrls = infoService.getLBServerEndpoints(VO);

            // LBClient lb = LBClient.createService(new
            // URL("https://grasveld.nikhef.nl:9003/"));
            // https://grasveld.nikhef.nl:9003/
            // LBClient lb = LBClient.createServiceFromJobID(jobid,9003);

            boolean someActive = true;
            String prevStatus[] = new String[num];
            int dots = 0;

            while (someActive)
            {
                someActive = false;

                for (int i = 0; i < num; i++)
                {
                    String status = jobs[i].getStatus(true); // TRUE = REQUERY !

                    if (status.equals(prevStatus[i]))
                    {
                        System.out.print(".");
                        dots++;
                        if (dots >= 100)
                        {
                            System.out.println("");
                            dots = 0;
                        }
                    }
                    else
                    {
                        if (dots > 0)
                        {
                            System.out.println();
                            dots = 0;
                        }

                        prevStatus[i] = status;
                        System.out.println(" - checking state job #" + i + " = " + status);
                        System.out.println(" [attrs] \n" + " - isRunning      :" + jobs[i].isRunning() + "\n"
                                + " - hasTerminated  :" + jobs[i].hasTerminated() + "\n" + " - hasError       :"
                                + jobs[i].hasError() + "\n" + " - getErrorText   :" + jobs[i].getErrorText() + "\n");

                        // System.out.println("LB status (2): "+
                        // lb.getStatus(job.getJobID()));
                    }

                    if (jobs[i].hasTerminated() == false)
                        someActive = true;
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

            for (int i = 0; i < num; i++)
            {
                LBClient.printStatus(jobs[i].getLBJobStatus(), System.out);

                try
                {

                    System.out.println(" --- checking output for job #" + i + " --- ");

                    ArrayList<OutputInfo> outputs = client.getJob(jobs[i].getJobId()).getOutputs();

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

    private static void message(String msg)
    {
        System.err.println("TestWMS:" + msg);
    }
}
