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
 * $Id: JobStatusUpdater.java,v 1.2 2011-04-18 12:27:23 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:23 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.tasks.ActionTask;

public class JobStatusUpdater
{
    private JobStatusDataModel jobStatusModel =null; 
    private ActionTask updateTask=null; 
    private boolean stopUpdateTask=false;
    private JobUtil jobUtil; 
    
    public JobStatusUpdater(JobStatusDataModel model)
    {
        this.jobStatusModel=model; 
    }

    /** Start update in background */ 
    public void doUpdate(final boolean fullUpdate)
    {
        debug("JobStatusUpdater: starting!"); 
        
        this.stopUpdateTask=false; 
        
        this.updateTask=new ActionTask(null,"JobStatusUpdater.updateTask()")
        {
            @Override
            protected void doTask() throws VlException
            {
                update(fullUpdate); 
            }

            @Override
            public void stopTask()
            {
                stopUpdateTask=true; 
            }
        };
        
        this.updateTask.startTask(); 
        
    }
    
    private void update(boolean fullUpdate)
    {   
        StringList ids=jobStatusModel.getJobIds();
        
        for (String id:ids)
        {
            
            if (stopUpdateTask)
            {
                debug(" *** Interrrupted: Must Stop *** ");
                break;
            }
            
            debug(" - updating status of job:"+id); 
            
            // Pre Fetch: 
            this.jobStatusModel.setQueryBusy(id,true);
            if (jobStatusModel.isStatusUnknown(id))
                    this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_UPDATING);
            this.jobStatusModel.setQueryBusy(id,false); 
            
            try
            {
                String newStatus=getJobUtil().getStatus(id,fullUpdate);
                debug(" -  new status:"+newStatus);
                this.jobStatusModel.setStatus(id,newStatus);
                
            }
            catch (Exception e)
            {
                debug(" -  *** Exception:"+e);
                this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_ERROR);
                this.jobStatusModel.setErrorText(id,e.getMessage());
            }
            
            this.jobStatusModel.setQueryBusy(id,false);
            
        }
    }

    private void debug(String msg)
    {
        Global.errorPrintf(this,"%s\n",msg); 
    }

    public JobUtil getJobUtil()
    {
        if (jobUtil==null)
        {
            this.jobUtil=new JobUtil(UIGlobal.getVRSContext());  
        }
        
        return jobUtil; 
        
    }
    
}
