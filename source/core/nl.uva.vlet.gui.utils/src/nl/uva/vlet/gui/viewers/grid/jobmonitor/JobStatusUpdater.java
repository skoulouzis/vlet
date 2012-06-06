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

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.EventType;
import nl.uva.vlet.vrs.ResourceEvent;

public class JobStatusUpdater implements ProxyResourceEventListener
{
    private static ClassLogger logger; 
    {
        logger=ClassLogger.getLogger(JobStatusUpdater.class);
        //logger.setLevelToDebug(); 
    }
    // ========================================================================
    
    private JobStatusDataModel jobStatusModel =null; 
    private ActionTask updateTask=null;
    private ActionTask updateAttrsTask=null; 

    private boolean stopUpdateTasks=false;
    private JobUtil jobUtil=null;
	private JobMonitorController controller=null; 
    
    public JobStatusUpdater(JobMonitorController jobMonitorController, JobStatusDataModel model)
    {
    	this.controller=jobMonitorController;
        this.jobStatusModel=model; 
        // register as event listener: 
        ProxyVRSClient.getInstance().addResourceEventListener(this); 
    }

    /** Start update in background */ 
    public void doUpdate(final boolean fullUpdate)
    {
        logger.infoPrintf(">>> JobStatusUpdater.doUpdate(): starting! <<<\n"); 
        
        this.stopUpdateTasks=false; 
        
        this.updateTask=new ActionTask(null,"JobStatusUpdater.updateTask()")
        {
            @Override
            protected void doTask() throws VlException
            {
                bgUpdate(fullUpdate); 
            }

            @Override
            public void stopTask()
            {
                stopUpdateTasks=true; 
            }
        };
        
        this.updateTask.startTask(); 
        
    }
  
    /** Start update in background */ 
    public void doUpdateAttributes(final String[] attributeNames)
    {
        logger.infoPrintf(">>> doUpdateAttributes: starting! <<<\n"); 
        this.stopUpdateTasks=false; 
        
        this.updateAttrsTask=new ActionTask(null,"JobStatusUpdater.doUpdateAttributes()")
        {
            @Override
            protected void doTask() throws VlException
            {
                bgUpdate(false,attributeNames); 
            }

            @Override
            public void stopTask()
            {
                stopUpdateTasks=true; 
            }
        };
        
        this.updateAttrsTask.startTask(); 
    }
    
    private void bgUpdate(boolean fullUpdate)
    {
        String attrNames[]=null; 
        if (fullUpdate)
            attrNames=this.jobStatusModel.getHeaders();
        bgUpdate(true,attrNames);
    }
    
    private void bgUpdate(boolean fullUpdate,String attrNames[])
    {   
        StringList ids=jobStatusModel.getJobIds();
        
        for (String id:ids)
        {
            if (stopUpdateTasks)
            {
                logger.warnPrintf(" *** Interrrupted: Must Stop *** \n");
                break;
            }
            
            logger.debugPrintf("Updating status of: %s\n",id); 
            
            // Pre Fetch: 
            this.jobStatusModel.setQueryBusy(id,true);
            if (jobStatusModel.isStatusUnknown(id))
                    this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_UPDATING);
            this.jobStatusModel.setQueryBusy(id,false); 
            
            try
            {
                String newStatus=getJobUtil().getStatus(id,fullUpdate);
                VRL vrl=getJobUtil().getJobVRL(id); 
                
                logger.infoPrintf(" - new status of '%s'=%s\n",id,newStatus);
                this.jobStatusModel.setStatus(id,newStatus);
                
                this.jobStatusModel.setValue(id,JobStatusDataModel.ATTR_JOBVRL,vrl.toString());
                
                // Auto Update Attribute Names (Update All Headers)  
                String newAttrNames[]=getJobUtil().getJobAttrNames(id); 
                // update headers
                jobStatusModel.addExtraHeaders(newAttrNames); 
                
                // Update all VAttribute currently shown in table.
                if (attrNames!=null)
                {
                    VAttribute attrs[]=getJobUtil().getAttributes(id,attrNames);
                    jobStatusModel.updateJobAttributes(id,attrs);
                }
               
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR,e,"Couldn't update status of job:%s\n",id); 
                this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_ERROR);
                this.jobStatusModel.setErrorText(id,e.getMessage());
            }
            
            this.jobStatusModel.setQueryBusy(id,false);
            
        }
    }

    public JobUtil getJobUtil()
    {
        // implementation independed Job Status Util
        if (jobUtil==null)
        {
        	// get context JoUtil
            this.jobUtil=JobUtil.getJobUtil(UIGlobal.getVRSContext());  
        }
        
        return jobUtil; 
        
    }

	public VRL getJobVrl(String jobid) throws VlException 
	{
		return getJobUtil().getJobVRL(jobid);
	}

	@Override
	public void notifyProxyEvent(ResourceEvent event) 
	{
		logger.debugPrintf(">>> Got Event:%s\n",event); 
		EventType type = event.getType(); 
		VRL source = event.getSource();
		
		//filter out jobs
		if (getJobUtil().isJobVRL(source)) //source.hasScheme(VRS.LB_SCHEME))
		{
			// search fro job using the VRL
			String jobid=jobStatusModel.getJobIdByVrl(source); 
			
			if (jobid!=null)
			{
				if (type==EventType.SET_ATTRIBUTES)
				{
					VAttribute[] attrs = event.getAttributes(); 
					VAttributeSet attrSet=new VAttributeSet(attrs);
					// update table
				}
			}
		}
	}
	

	public void stopAll() 
	{
		stopUpdateTasks=true; 
	}
    
}
