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
 * $Id: TestSubmit.java,v 1.8 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vjs;

import java.util.ArrayList;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.ServiceInfo.ServiceInfoType;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vjs.wms.WMSJob;
import nl.uva.vlet.vjs.wms.WMSResource;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSClient;

/**
 * Example how to submit JDL files using WMS.
 */
public class TestSubmit
{
    static String jdl[] = 
        {// String array of idl lines (newlines are added in array2string method).
            "Executable          =\"/bin/bash\";",
            "Arguments           =\"-c 'echo Test Submit; echo ; env' \";",
            "Stdoutput           =\"message.txt\";",
            "StdError = \"stderror\";",
            "OutputSandbox       ={\"message.txt\",\"stderror\"};",
            "virtualorganisation =\"pvier\";",
            "requirements        =other.GlueCEName == \"express\";",
            "rank                =-other.GlueCEStateEstimatedResponseTime;",
            // If you get "hit job shallow retry count", add this: 
            "DefaultNodeShallowRetryCount = 3;",
            "RetryCount = 3;"
        };


    private static VRSClient vrsClient=null; 
    
    public static void main(String args[])
    {
        String vo="pvier"; 
        
        try
        {
            // Make sure WMS has been initialized (Only necessary in Eclipse environment)  
            nl.uva.vlet.vrs.VRS.getRegistry().initVDriver(nl.uva.vlet.vjs.wms.WMSFactory.class);
     
            // Check Proxy: 
            GridProxy proxy=GridProxy.getDefault();
            if (proxy.isValid()==false)
                throw new VlException("Grid Proxy is Invalid");
            
            // Init VRS  
            vrsClient=new VRSClient(); 
         
            // Qyert Info Service for WMS Resources: 
            ArrayList<ServiceInfo> wmsInfos = vrsClient.queryServiceInfo(vo,ServiceInfoType.WMS); 
            message("Allowed WMS services:");
            for (ServiceInfo info:wmsInfos)
               messagePrintf(" - WMS allowed for vo:%s =%s\n",vo,info.getServiceVRL()); 
            
            // get first: 
            ServiceInfo wms = wmsInfos.get(0);
            VRL wmsVrl = wms.getServiceVRL(); 
            
            doSubmit(wmsVrl,array2string(jdl,"\n"));
        }
        catch (Exception e)
        {
            error("Exception:"+e); 
        }
    }
  
    public static String array2string(String[] stringArray, String endlineChar)
    {
        StringBuilder builder=new StringBuilder(); 
        
        for(String string:stringArray)
        {
            builder.append(string); 
            builder.append(endlineChar);
        }
        return builder.toString();
    }

    public static void doSubmit(VRL wmsVrl,String jdl) throws VlException
    {
        message("Submitting to:"+wmsVrl); 
        
        // get WMS Resource: 
        WMSResource wmsClient=(WMSResource)vrsClient.openResourceSystem(wmsVrl);
        wmsClient.doDelegation("delegation-n00b"); 
        WMSJob wmsJob = wmsClient.submitJdlString(jdl); 
        
        message("Job ID="+wmsJob.getJobId());
        
        String prevStatus=null;
        boolean newline=true; 
        int waitCount=0;
        while(wmsJob.hasTerminated()==false)
        {
            String status=wmsJob.getStatus(true);
            if (status.equals(prevStatus))
            {
            	if ((newline) || (waitCount>=80)) 
            	{
            	    if (waitCount>0)// skip first line 
            	        messagePrintf("\n"); 
            	    
               		messagePrintf("Waiting:.");
               		waitCount=0;
               		newline=false; 
            	}
            	else
            	{               	 
            		messagePrintf(".");
            		waitCount++; 
            	}
            }
            else
            {
                messagePrintf("\n>>> NEW Status of job=%s<<<\n",status);
                prevStatus=status;
                newline=true;
                waitCount=0; 
            }
            sleep(1000); 
        } 
        
        VRL vrls[] = wmsJob.getOutputVRLs();
        for (VRL vrl:vrls)
        {
        	message(">>> Output VRL=="+vrl);
            String txt=vrsClient.getResourceLoader().getText(vrl); 
            message(">>> Output txt=\n-----------\n"+txt+"\n--------------");
        }
    }

    private static boolean sleep(int millis)
    {
        try 
        {
            Thread.sleep(millis);
            return true;
         }
        catch (InterruptedException e)
        {
            error("Interupted:"+e); 
        }
        return false; 
    }

    static void messagePrintf(String format,Object... args)
    {
    	System.out.printf(format,args);  
    }
    
    static void message(String msg)
    {
    	System.out.println(msg); 
    }
    
    static void error(String msg)
    {
    	System.err.println(msg); 
    }
}
