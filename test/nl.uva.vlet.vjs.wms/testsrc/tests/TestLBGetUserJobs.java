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
 * $Id: TestLBGetUserJobs.java,v 1.8 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package tests;

import java.net.URI;

import nl.uva.vlet.glite.LBClient;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.LBClient.LBJobHolder;
import nl.uva.vlet.glite.WMLBConfig.LBConfig;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wsdl.types.lb.JobStatus;
import org.glite.wsdl.types.lb.QueryAttr;

public class TestLBGetUserJobs
{
    public static void main(String args[])
    {
        // testGetIndexedAttributes("graskant.nikhef.nl");
        // testGetIndexedAttributes("grasveld.nikhef.nl");
        // testGetIndexedAttributes("wmslb2.grid.sara.nl");
        // testGetIndexedAttributes("wms.grid.sara.nl");

        // testGetUserJobs("graskant.nikhef.nl");
        testGetUserJobs("wms3.grid.sara.nl");
        // testGetUserJobs("wmslb2.grid.sara.nl");
        // testGetUserJobs("wms.grid.sara.nl");

    }

    private static void testGetIndexedAttributes(String lbHost)
    {
        LBConfig lbconf;
        try
        {
            lbconf = WMLBConfig.createLBConfig(lbHost, WMLBConfig.LB_DEFAULT_PORT);
            lbconf.setProxyfilename(VRSContext.getDefault().getGridProxy().getProxyFilename());

            LBClient lb = new LBClient(lbconf);

            QueryAttr[] attr = lb.getIndexedAtrributes();

            for (int i = 0; i < attr.length; i++)
            {
                System.out.println(attr[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            nl.uva.vlet.vrs.VRS.exit(); // stop threads;
            // System.exit(0);
        }

    }

    public static void testGetStatus(String jobid)
    {
        GridProxy prox = GridProxy.getDefault();

        if (prox.isValid() == false)
        {
            System.err.print("*** Error: Invalid Proxy *** ");
        }

        try
        {

            LBClient lb = LBClient.createServiceFromJobUri(new URI(jobid));
            JobStatus stat = lb.getStatus(new URI(jobid));
            LBClient.printStatus(stat, System.out);

            String status = LBClient.jobStatusToString(stat);

            System.out.println("Job status: " + status);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            nl.uva.vlet.vrs.VRS.exit(); // stop threads;
            System.exit(0);
        }
    }

    public static void testGetUserJobs(String lbHost)
    {
        System.out.println("=== getting user jobs from LB server:" + lbHost);

        try
        {

            // Spiros: Dead variables
            // infoService = vrsContext.getBdiiService();
            // String VO = vrsContext.getVO();
            // VRL[] vrls = infoService.getLBServerEndpoints(VO);

            // https://wms01.egee.cesga.es:9003
            // https://wmslb2.grid.sara.nl:9003
            LBConfig lbconf = WMLBConfig.createLBConfig(lbHost, WMLBConfig.LB_DEFAULT_PORT);
            lbconf.setProxyfilename(VRSContext.getDefault().getGridProxy().getProxyFilename());
            LBClient lb = new LBClient(lbconf);

            // LBClient lb = LBClient.createService(lbHost, 9003);
            System.out.println("- LB Version=" + lb.getVersion());

            LBJobHolder[] jobSts = lb.getUserJobs();
            if (jobSts == null)
                System.out.println("- Got 0 jobs!");
            else
            {
                System.out.println("- Nr of jobs=" + jobSts.length);

                for (int i = 0; i < jobSts.length; i++)
                {
                    System.out.println(jobSts[i].jobID);
                         
                    if(jobSts[i].jobStatus == null)
                    {
                        jobSts[i].jobStatus = lb.getStatus(new URI(jobSts[i].jobID));
                    }
                    
                    System.out.println("jobid="+jobSts[i].jobID+" = "+status(jobSts[i].jobStatus));
                    
                    
                    if ((jobSts[i].jobStatus!=null) && (jobSts[i].jobStatus.getChildrenNum() > 0))
                    {
                        String[] childrenID = jobSts[i].jobStatus.getChildren();
                        for(int j=0;j<childrenID.length;j++){
                            System.out.println("    Children : "+childrenID[j]);        
                        }
                    }
                    
                    
                }
            }
        }
        catch (Exception e)
        {
            // test exception wrapping also!
            // Exception ex=WMSUtil.convertException("getUserJobs()",e);
            e.printStackTrace();
        }

        nl.uva.vlet.vrs.VRS.exit(); // stop threads;
    }

	private static String status(JobStatus jobStatus)
	{
		if (jobStatus==null)
			return "<NULL>";
		
		return jobStatus.getState().toString(); 
	}
}
