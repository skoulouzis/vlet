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
 * $Id: VJob.java,v 1.3 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.vjs;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VRSContext;


/** 
 * Abstract VJob interface.
 * <p>
 * <b>Job Status:</b><br>
 * 
 * A Status of a VJob is simplified and is limited to only one of tree:<br>
 * <ul> 
 * <li>Submitted: Pre execution, job is being submitted to the queue or waiting in the queue.  
 * <li>Running: Job is executing. The job is actually running on the Job (sub) System. 
 * <li>Terminated: Post execution. Job has succeeded, has an error or is cancelled.  
 * </ul>
 * This simplification is done because not all JobManager systems support all kind 
 * of statuses.  
 * A Job is terminated if it already executed or has failed. It will never will reach the status of Running. 
 * This could also mean that a job was Canceled or Aborted or the execution has failed. 
 * If it is not Running or not Terminated it is assumed the job has been Submitted
 * and is waiting for execution or is in the process of submission. 
 *  
 * @author Piter T. de Boer 
 *
 */
public abstract class VJob extends VCompositeNode implements VEditable
{
    // job only attribute names: 
    static protected StringList _jobAttrNames; 
    
    static
    {
        _jobAttrNames=new StringList(new String[]{
                VAttributeConstants.ATTR_STATUS,
                VAttributeConstants.ATTR_JOB_IS_RUNNING,
                VAttributeConstants.ATTR_JOB_HAS_TERMINATED,
                VAttributeConstants.ATTR_JOB_HAS_ERROR,
                VAttributeConstants.ATTR_ERROR_TEXT,
                VAttributeConstants.ATTR_JOB_STATUS_INFORMATION
        }); 
        
    }
    
    //=========================================================================
    // VNode -> VJob
    //=========================================================================
	
	protected String id; 
	
	
	public VJob(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

//	VTerminatable ==> public void terminate() throws VlException
//	{
//		throw new nl.uva.vlet.exception.NotImplementedException("Not implemented: terminate:"+this); 
//	}
	
//	VSuspendable ==> public void resume() throws VlException
//	{
//		throw new nl.uva.vlet.exception.NotImplementedException("Not implemented: resume:"+this); 
//	}
	
	public boolean sync() throws VlException
	{
		return super.sync();
	}
	
	// === VEdtiable interface ===
	
	public boolean setAttributes(VAttribute[] attrs) throws VlException
	{
		boolean result=true; 
		for (VAttribute attr:attrs)
		{
			boolean val=setAttribute(attr); 
			result=result&val; 
		}
		
		return result;
	}
	
	/** Job ID */ 
	public String getJobId()
	{
		return id; 
	}
	
	/** protected setJobId */ 
	protected String setJobId(String idstr)
	{
		return id=idstr; 
	}

	/** Optional Job Group ID. Returns null is this Job doesn't have an ID */
	public String getGroupId()
	{
		return null;
	}
	
    @Override
    public String[] getResourceTypes()
    {
        // possible resource types:  JDL child, Job Output folder.
        return null;
    }
	
    public VAttribute getAttribute(String name) throws VlException
    {
        // Generic VJob Attributes: 
        if (StringUtil.equals(name, VAttributeConstants.ATTR_STATUS))
        {
            return new VAttribute(name, this.getStatus());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_IS_RUNNING))
        {
            return new VAttribute(name, this.isRunning());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_HAS_TERMINATED))
        {
            return new VAttribute(name, this.hasTerminated());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_HAS_ERROR))
        {
            return new VAttribute(name,this.hasError());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_ERROR_TEXT))
        {
            return new VAttribute(name,this.getErrorText());
        }
        
        
        return super.getAttribute(name); 
    }
    
    public String[] getAttributeNames()
    {
        // merge attributes
        String[] attrs = super.getAttributeNames(); 
        StringList list=new StringList(attrs); 
        list.add(getJobAttributeNames()); 
        return list.toArray(); 
    }
    
    public String[] getResourceAttributeNames()
    {
        return getJobAttributeNames();
    }
    
    /**
     * Return Job Specific Attribute names. 
     * Override for implementation specific job attribute names.
     *  
     * @return String array of attribute names
     */
    public String[] getJobAttributeNames()
    {
        return _jobAttrNames.toArray(); 
    }
    
    // === VJob Outputs === 
    
    public boolean hasOutputs() throws VlException
    {
    	return false; 
    }
    
    /** 
     * Return job outputs after job has successfully finished. 
     * Only if supported by the implementation.
     */
    public VRL[] getOuputVRLs() throws VlException
    {
    	return null; 
    }
    
	// === VJob interface === 
	
//	/** Get list of Status string this Job supports */ 
//	public abstract String getPossibleStatuses() throws VlException;
	
	/**
	 * Returns status String. This status string is implementation depended. 
	 * Use the isRunnig(), isTerminated() and hasError() methods for explicit status checking. 
	 * Current status supported at the VJOb level is {Running,Terminated,Error} 
	 */
	public abstract String getStatus() throws VlException; 
	
	/** Whether job is currently executing. */  
	public abstract boolean isRunning() throws VlException; 
	
	/** 
	 * Terminated means either successful execution or terminated with error
	 * or it was cancelled. 
	 * If hasTerminated==true it has finished or won't be running.  
	 */ 
	public abstract boolean hasTerminated() throws VlException;  

	/**
	 * Returns whether the job has terminated with an error. 
	 * If the job is hasn't ran or is still running, this method might 
	 * return null. 
	 */  
	public abstract boolean hasError() throws VlException;  
	
	/**
	 * Returns error text, return null if no error has been encountered 
	 * or if job is still running.
	 */
	public abstract String getErrorText() throws VlException;  

	
}
