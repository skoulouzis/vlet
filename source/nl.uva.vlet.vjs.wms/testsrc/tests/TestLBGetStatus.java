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
 * $Id: TestLBGetStatus.java,v 1.8 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package tests;

import java.net.MalformedURLException;
import java.net.URI;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.LBClient;
import nl.uva.vlet.glite.LBClient.LBJobHolder;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wsdl.types.lb.JobStatus;

public class TestLBGetStatus
{
    public static void main(String args[])
    {
        testGetStatus("https://grasveld.nikhef.nl:9000/Lfuvmaj9ZqnRJbdW4aZn2w");

        testGetUserJobs();
    }

    public static void testGetStatus(String jobid)
    {
        //
        // Global.setDebug(true);
        // NEEDED: initialized default GridProxy + Globus Proxy/SSL Stack +
        // Utils
        // 

        GridProxy prox = GridProxy.getDefault();

        // Spiros: Dead variable
        // VRSContext vrsContext = UIGlobal.getVRSContext();

        if (prox.isValid() == false)
        {
            System.err.print("*** Error: Invalid Proxy *** ");
        }

        try
        {
            // Spiros: Dead variable
            // BdiiService infoService = vrsContext.getBdiiService();
            // String VO = vrsContext.getVO();

            LBClient lb = LBClient.createServiceFromJobUri(new URI(jobid));
            JobStatus stat = lb.getStatus(new URI(jobid));
            LBClient.printStatus(stat, System.out);

            String status = LBClient.jobStatusToString(stat);

            System.out.println("Job status: " + status);

            // LBClient lb2 = LBClient.createServiceFromJobID(jobid,9003);
            // JobStatus stat2 = lb2.getStatus(jobid);
            // LBClient.printStatus(stat2,System.out);

            // LBClient lb2 = LBClient.createService(new URL(
            // "https://grasveld.nikhef.nl:9003/lb"));
            // JobStatus stat2 = lb2.getStatus(jobid);
            // LBClient.printStatus(stat2, System.out);

            // LBClient lb2 = LBClient.createService(vrls[0].toURL());
            // JobStatus stat2 = lb2.getStatus(jobid);
            // LBClient.printStatus(stat2, System.out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void testGetUserJobs()
    {

        VRSContext vrsContext = UIGlobal.getVRSContext();
        BdiiService infoService;
        try
        {

            // Spiros: Dead variable
            // infoService = vrsContext.getBdiiService();
            // String VO = vrsContext.getVO();
            // VRL[] vrls = infoService.getLBServerEndpoints(VO);

            // https://wms01.egee.cesga.es:9003
            // https://wmslb2.grid.sara.nl:9003
            LBClient lb = LBClient.createService("wmslb2.grid.sara.nl", 9003);

            // JobStatus[] usrStsus = lb.getUserJobs();
            LBJobHolder[] usrStsus = lb.getUserJobs();
            for (int i = 0; i < usrStsus.length; i++)
            {
                LBClient.printStatus(usrStsus[i].jobStatus, System.out);
            }
        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
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

}
