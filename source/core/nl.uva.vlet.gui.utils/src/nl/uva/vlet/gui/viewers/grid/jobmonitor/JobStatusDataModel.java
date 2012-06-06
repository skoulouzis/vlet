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
 * $Id: JobStatusDataModel.java,v 1.3 2011-06-07 15:15:08 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:08 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ERROR_TEXT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_INDEX;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_JOBID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_JOB_STATUS_INFORMATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_STATUS;
import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTableModel;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTableModel.RowData;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

public class JobStatusDataModel extends ResourceTableModel
{
    //
    private static final long serialVersionUID = -3186714948599037988L;
    //
    public static final String ATTR_SELECTION  = "selection"; 
    public static final String ATTR_LBHOSTNAME = "lbHostname"; 
    public static final String ATTR_LBPORT     = "lbPort"; 
    public static final String ATTR_JOBNAME    = "jobName";
    public static final String ATTR_JOBVRL    = "jobVrl";
    public static final String ATTR_JOBSTATUS   = ATTR_STATUS;
    public static final String ATTR_JOBSTATUSINFO   = ATTR_JOB_STATUS_INFORMATION;
    
    public static final String STATUS_UNKNOWN   = "Unknown"; 
    public static final String STATUS_UPDATING = "Updating";
    public static final String STATUS_ERROR    = "Error";
    
    private static final String ATTR_IS_BUSY = "isBusy"; 
    
    // ========================================================================
    //
    // ========================================================================
    
    private StringList jobIds;
    private JobMonitorController jobController;
    private Presentation presentation;
    private ActionTask updateTask;
    // private JobStatusUpdater statusUpdater; 
    
    public JobStatusDataModel(JobMonitorController controller)
    {
        super(); 
        this.jobController=controller; 
        init(); 
        
    }
    
    public void init()
    {
        StringList headers=new StringList(); 
        
        headers.add(ATTR_INDEX);
        headers.add(ATTR_LBHOSTNAME);
        headers.add(ATTR_JOBNAME);

        headers.add(ATTR_JOBSTATUS);
        headers.add(ATTR_JOB_STATUS_INFORMATION);
        headers.add(ATTR_ERROR_TEXT);
        headers.add(ATTR_SELECTION);
        
        // Update header to be viewed only: Presentation is part of "View" 
        this.setHeaders(headers.toArray()); 
        
        // Add extra header to data model:
        headers.add(ATTR_LOCATION);
        headers.add(ATTR_JOBID);
        headers.add(ATTR_JOBVRL);
        headers.add(ATTR_LBPORT);
        headers.add(ATTR_IS_BUSY);
        
        // Set potential headers: 
        this.setAllHeaders(headers);
        // clear dummy data
        this.clearData();
        
       
    }

    /** Remove old and update with new JobIds list */ 
    public void setJobids(StringList ids)
    {
        this.jobIds=ids; 
        this.clearData();
        
        for (String id:ids)
        {
            try
            {
                addRow(id);
            }
            catch (VRLSyntaxException e)
            {
                handle("Couldn't add Job:"+id,e); 
            }
        }
        
      
    }
    
    

    private void handle(String msg, VRLSyntaxException e)
    {
        Global.logException(ClassLogger.ERROR,this,e,"%s\n",msg); 
    }

    public void addRow(String jobid) throws VRLSyntaxException
    {   
        VAttributeSet attrs=new VAttributeSet();
        
        VRL jobvrl=JobUtil.createJobVrl(jobid); 
        // actual job id as string. This is also the key! 
        attrs.set(ATTR_JOBID,jobid); 
        attrs.set(ATTR_JOBVRL,jobvrl); 
        attrs.set(ATTR_JOBNAME,jobvrl.getBasename()); 
        attrs.set(ATTR_LBHOSTNAME,jobvrl.getHostname()); 
        attrs.set(ATTR_LBPORT,jobvrl.getPort());    
        attrs.set(ATTR_JOBSTATUS,"?");
        
        int index=this.addRow(jobid, attrs);
        // update index FIRST  
        this.setValue(jobid,ATTR_INDEX,""+index);
    }

    public Presentation getPresentation()
    {
        if (this.presentation==null)
        {
            this.presentation=new Presentation();
            
            presentation.setAttributePreferredWidth(ATTR_JOBID,100,200,300); 
            presentation.setAttributePreferredWidth(ATTR_JOBNAME,100,200,300); 
            presentation.setAttributePreferredWidth(ATTR_LBHOSTNAME,100,150,250); 
            presentation.setAttributePreferredWidth(ATTR_LBPORT,40,40,40); 
            presentation.setAttributePreferredWidth(ATTR_JOBSTATUS,100,120,160);
            presentation.setAttributePreferredWidth(ATTR_JOBSTATUSINFO,100,120,200); 
            presentation.setAttributePreferredWidth(ATTR_INDEX,40,40,40); 
            presentation.setAttributePreferredWidth(ATTR_ERROR_TEXT,40,120,-1); 
        }
        
        return this.presentation; 
    }

    private void bgUpdateJobStatus()
    {
        this.updateTask=new ActionTask(null,"JobMonitorUpdateTask")
            {
                @Override
                protected void doTask() throws VlException
                {
                    
                }
    
                @Override
                public void stopTask()
                {
                    
                }
        };
    }

    /** Returns copy of current ID list */
    public StringList getJobIds()
    {
        return this.jobIds; 
    }

    public void setQueryBusy(String id, boolean b)
    {
        this.setValue(id,new VAttribute(ATTR_IS_BUSY,b)); 
    }

    public boolean isStatusUnknown(String id)
    {
        RowData row = this.getRow(id); 
        if (row==null)
            return true;
        
        VAttribute attr=row.getAttribute(ATTR_STATUS);
        if (attr==null)
            return true; 
        
        String txt=attr.getStringValue(); 
        
        if (StringUtil.equals(txt,STATUS_UNKNOWN))
            return true;
        
        return false;
    }

    public void setStatus(String id, String status)
    {
        this.setValue(id,ATTR_STATUS,status);
    }

    public void setErrorText(String id, String message)
    {   
        this.setValue(id,ATTR_ERROR_TEXT,message); 
    }

    public void updateJobAttributes(String id, VAttribute[] attrs)
    {
        if (attrs==null)
        {
            ; //clear?
            return; 
        }
        
        // Auto Extend: check for extra Attributes! 
        StringList extraAttrsNames=new StringList(); 
        for (int i=0;i<attrs.length;i++)
        {
            // possible due unknown VAttribute!
            if (attrs[i]!=null)
                extraAttrsNames.add(attrs[i].getName()); 
        }
        
        // update Headers 
        if (extraAttrsNames.size()>0)
            addExtraHeaders(extraAttrsNames.toArray());
        this.setValues(id, attrs); 
    }

    public void addExtraHeaders(String[] newNames)
    {
        String[] names = this.getAllHeaders();   
        StringList list=new StringList(names);
        int n=list.size();
        
        // O(N*N) check and add to header list (inefficient) 
        for (String name:newNames)
            if (list.contains(name)==false)
                list.add(name);
        
        // add extra header attributes 
        if (list.size()>n)
            this.setAllHeaders(list);
    }

	public VRL getJobVRL(String jobid) throws VRLSyntaxException 
	{
		RowData row = this.getRow(jobid);
		if (row==null)
			return null; 
		VAttribute attr = row.getAttribute(ATTR_JOBVRL);
		if (attr==null)
			return null; 
		return attr.getVRL(); 
	}

	public boolean hasJobVRL(VRL source) 
	{
		return (getJobIdByVrl(source)!=null);
	}

	public String getJobIdByVrl(VRL vrl) 
	{
		// first guess jobid using vrl with "https" as scheme: 
		String jobid=JobUtil.guessJobIdFromJobVrl(vrl); 
		RowData row = this.getRow(jobid); 
		if (row!=null)
			return row.getKey();
		
		return null;
	}       
    
    
}
