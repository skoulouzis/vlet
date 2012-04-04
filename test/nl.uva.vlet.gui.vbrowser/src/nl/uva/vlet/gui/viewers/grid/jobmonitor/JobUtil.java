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
 * $Id: JobUtil.java,v 1.4 2011-06-07 14:31:56 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:56 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vjs.VJob;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;

public class JobUtil
{
    private VRSClient vrsClient;

    public JobUtil(VRSContext context)
    {
        this.vrsClient=new VRSClient(context); 
    }

    public String getStatus(String jobid, boolean fullUpdate) throws VlException
    {
        VRL vrl=new VRL(jobid);
        
        // replace https -> LB scheme 
        if (vrl.hasScheme("https"))
            vrl=vrl.copyWithNewScheme(VRS.LB_SCHEME); 
        
        VNode jobNode = vrsClient.openLocation(vrl); 
         
        if ((jobNode instanceof VJob)==false)
        {
            
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("URI is not a job URI:"+jobid
                    +"\n. Resource Type="+jobNode.getType() );   
        }
        
        VJob job=(VJob)jobNode; 
        String stat=job.getStatus();
       
        return stat; 
    }
    
}
