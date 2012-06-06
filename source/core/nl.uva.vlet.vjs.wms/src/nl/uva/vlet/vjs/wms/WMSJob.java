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
 * $Id: WMSJob.java,v 1.4 2011-06-07 15:14:33 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:33 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.ResourceException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.OutputInfo;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.WMSUtil;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vjs.VJob;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.jdl.JobAd;
import org.glite.wsdl.types.lb.Event;
import org.glite.wsdl.types.lb.JobStatus;
import org.glite.wsdl.types.lb.StatName;
import org.glite.wsdl.types.lb.StateEnterTimesItem;

public class WMSJob extends VJob
{

    // ========================================================================
    // Instance
    // ========================================================================

    private static ClassLogger logger;

    private LBResource lbResource;

    /** Actual WMS Job URI */
    private java.net.URI jobUri;

    private Map<VRL, VNode> childNodes;

    // private Event[] jobEvents;

    private JobStatus cachedJobStatus;

    static
    {
        logger = ClassLogger.getLogger(WMSJob.class);
//        logger.setLevelToDebug();
    }

    public WMSJob(LBResource lbRes, VRL jobVrl, URI jobid)
    {
        super(lbRes.getContext(), jobVrl);
        this.lbResource = lbRes;
        this.jobUri = jobid;
    }

    protected WMSJob(LBResource lbResource, VRL jobVrl, URI jobid, JobStatus stat)
    {
        super(lbResource.getContext(), jobVrl);
        this.lbResource = lbResource;
        this.jobUri = jobid;
        this.cachedJobStatus = stat;
    }

    // Override to return 'logical' parent !
    // This result in that a "Browse Up" from the vbrowser jumps back to MyJobs
    // node!
    public VRL getParentLocation()
    {
        return this.getParent().getLocation();
    }

    // Override to return 'logical' parent !
    // This result in that a "Browse Up" from the vbrowser jumps back to MyJobs
    // node!
    public VNode getParent()
    {
        if (this.logicalParent != null)
            return this.logicalParent;

        // default to LBResource
        return getLBResource();
    }

    public LBResource getLBResource()
    {
        return this.lbResource;
    }

    @Override
    public String getType()
    {
        return VJS.TYPE_VJOB;
    }

    public String getJobId()
    {
        return this.jobUri.toString();
    }

    public URI getJobUri()
    {
        return jobUri;
    }

    /** Return Key part of Job URI,stripping https://blahblah/... */
    public String getJobKey()
    {
        return new VRL(jobUri).getBasename();
    }

    public String[] getAttributeNames()
    {
        // merge attributes
        String[] attrs = super.getAttributeNames(); 
        StringList list=new StringList(attrs); 
        list.add(getJobAttributeNames()); 
        return list.toArray(); 
    }
    
    // explicit WMS attributes:
    public String[] getJobAttributeNames()
    {
        // get VJob:  
        StringList names = new StringList(super.getJobAttributeNames()); // get list from VJob.
        // add WMS:
        names.add(getWMSJobAttributeNames()); 
        return names.toArray();
    }
    
 // explicit WMS attributes:
    public String[] getWMSJobAttributeNames()
    {
        StringList names = new StringList(); 
        
        names.add(WMSConstants.ATTR_WMS_REASON);
        //names.add(WMSConstants.ATTR_WMS_EXPECTED_UPDATE);
        names.add(WMSConstants.ATTR_WMS_DESTINATION);
        names.add(WMSConstants.ATTR_WMS_STATE_ENTERED_TIME);
        names.add(WMSConstants.ATTR_WMS_LAST_UPDATE_TIME);
        names.add(WMSConstants.ATTR_WMS_SERVER_HOSTNAME);

        return names.toArray();
    }
    
    public VAttribute getAttribute(String name) throws VlException
    {
        // Check Non-mutable attributes first!
        VAttribute attr = this.getNonmutableAttribute(name);

        if (attr != null)
            return attr;

        if (StringUtil.equals(name, VAttributeConstants.ATTR_STATUS))
        {
            return new VAttribute(name, getStatus());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOBID))
        {
            return new VAttribute(name, getJobId());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOBURI))
        {
            return new VAttribute(name, new VRL(getJobUri()));
        }
        else if ( (StringUtil.equals(name, WMSConstants.ATTR_WMS_REASON))
                 || (StringUtil.equals(name, WMSConstants.ATTR_JOB_STATUS_INFORMATION)) )
        {
            return new VAttribute(name, getLBJobStatus().getReason());
        }
        else if (StringUtil.equals(name, WMSConstants.ATTR_WMS_SERVER_URI))
        {
            // getLBJobStatus().getNetworkServer());
            return new VAttribute(name, this.getWmsServerURI());
        }
        else if (StringUtil.equals(name, WMSConstants.ATTR_WMS_SERVER_HOSTNAME))
        {
            // getLBJobStatus().getNetworkServer());
            return new VAttribute(name, this.getWmsServerHostname());
        }
        else if ( (StringUtil.equals(name, WMSConstants.ATTR_JOB_SUBMISSION_TIME))
                  || (StringUtil.equals(name, WMSConstants.ATTR_WMS_STATE_ENTERED_TIME)) )
        {
            StateEnterTimesItem[] times = getLBJobStatus().getStateEnterTimes(); 

            if ((times==null) || (times.length<=0))
            {
                return VAttribute.createFrom(VAttributeType.STRING,name,null); 
            }
            else
            {
                StateEnterTimesItem date = times[0]; 
                return VAttribute.createDateSinceEpoch(name, date.getTime().getTimeInMillis());
            }
        }
        // VJob "Status Update" time = WMS "
        else if ( (StringUtil.equals(name, WMSConstants.ATTR_JOB_STATUS_UPDATE_TIME)) 
                || (StringUtil.equals(name, WMSConstants.ATTR_WMS_LAST_UPDATE_TIME)) )
        {
            long millis = getLBJobStatus().getLastUpdateTime().getTvSec() * 1000
                    + getLBJobStatus().getStateEnterTime().getTvUsec() / 1000;
            return VAttribute.createDateSinceEpoch(name, millis);
        }
        else if (StringUtil.equals(name, WMSConstants.ATTR_WMS_DESTINATION))
        {
            return new VAttribute(name, getLBJobStatus().getDestination());
        }
//        else if (StringUtil.equals(name, WMSConstants.ATTR_WMS_EXPECTED_UPDATE))
//        {
//            return new VAttribute(name, getLBJobStatus().get())
//        }
        
        return super.getAttribute(name);
    }

    /** Get WMS Hostname as stored in JobStatus! */
    public String getWmsServerHostname() throws VlException
    {
        URI uri = getWmsServerURI();

        if (uri == null)
            return null;
        return uri.getHost();
    }

    /**
     * Get WMS URI as stored in JobStatus! Can be null
     */
    public java.net.URI getWmsServerURI() throws VlException
    {
        String wmsstr = getLBJobStatus().getNetworkServer();

        if (StringUtil.isEmpty(wmsstr))
        {
            String parentID = getLBJobStatus().getParentJob();
            if (parentID != null)
            {
                return lbResource.getJobByJobID(parentID).getWmsServerURI();
            }
            else
            {
                return null;
            }
        }
        try
        {
            return new java.net.URI(wmsstr);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Couldn't get WMS Server Uri:" + wmsstr, e);
        }
    }

    @Override
    public String getStatus() throws VlException
    {
        // method check cached or not:
        return getLBJobStatus(false).getState().toString();
    }

    /**
     * Get Status String from WMJobCache or query status if needed. Set
     * reQuery==true to explicit perform a new Query which updates the status of
     * this job.
     */
    public String getStatus(boolean forceUpdate) throws VlException
    {
        // method check cached or not:
        return getLBJobStatus(forceUpdate).getState().toString();
    }

    /**
     * Get LB Status from WMJobCache or query status if needed. Set
     * reQuery==true to explicit perform a new Query which updates the status of
     * this job.
     */
    public JobStatus getLBJobStatus(boolean forceUpdate) throws VlException
    {
    	if ((this.cachedJobStatus==null) || (forceUpdate)) 
    		this.cachedJobStatus = queryLBJobStatus(forceUpdate);
        return this.cachedJobStatus;
    }

    /**
     * Get LB Status from WMJobCache or query status if needed. Throws exception
     * if status cannot be fetched, so using the construct
     * getLBJobStatus().invokeMethod(...) is nullpointer save.
     */
    public JobStatus getLBJobStatus() throws VlException
    {
        this.cachedJobStatus = queryLBJobStatus(false);

        if (this.cachedJobStatus == null)
            throw new ResourceException("Get NULL Status. Couldn't get LB status of job:" + this);

        return this.cachedJobStatus;
    }

    /**
     * Queries LB Server for job status. if useCache==false =>does not read from
     * cache, but does store new status in status cache. if useCache==true =>
     * returns cached value if wait time has expired.
     */
    public JobStatus queryLBJobStatus(boolean forceUpdate) throws VlException
    {
    	if (cachedJobStatus==null)
    	{
    		cachedJobStatus = this.lbResource.queryJobStatus(jobUri, true);
    	}
    	else if (forceUpdate==true)
    	{
    		cachedJobStatus=this.lbResource.queryJobStatus(jobUri, forceUpdate); 
    	}
    	// else return cachedJobStatus; 
    	
    	return this.cachedJobStatus;
    }

    @Override
    public boolean hasError() throws VlException
    {
        JobStatus stat = this.getLBJobStatus();
        return WMSUtil.hasError(stat);
    }

    @Override
    public boolean hasTerminated() throws VlException
    {
        return WMSUtil.hasTerminated(this.getLBJobStatus().getState());
    }

    @Override
    public String getErrorText() throws VlException
    {
        JobStatus stat = this.getLBJobStatus();
        return WMSUtil.getStatusErrorText(stat);
    }

    @Override
    public boolean isRunning() throws VlException
    {
        JobStatus stat = this.getLBJobStatus();
        return WMSUtil.statusIsRunning(stat.getState());
    }

    @Override
    public boolean exists() throws VlException
    {
        // Whether the job exists not matter which status is has.
        try
        {
            // existing job return status;
            JobStatus stat = queryLBJobStatus(false);

            if (stat == null)
                return false;

            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /** Job Attributes NOT editable ! */
    public boolean isEditable() throws VlException
    {
        return false;
    }

    public boolean setAttribute(VAttribute attr) throws VlException
    {
        // no settable attributes.
        return false;
    }

    private Object initChildMutex = new Object();

    private WMSResource wmsResource;

    private VNode logicalParent;

    private boolean isCllectionOrDAG;

    public String getName()
    {
        String status = "?";

        try
        {
            status = this.getStatus();
        }
        catch (Exception e)
        {
            ;
        }

        return this.getBasename() + " (" + status + ")";
    }

    @Override
    public VNode[] getNodes() throws VlException
    {
        synchronized (initChildMutex)
        {
            if (StringUtil.equals(getJobKey(), WMSConstants.TYPE_SUBMITED_JDL)
                    || StringUtil.equals(getJobKey(), WMSConstants.TYPE_MATCHED_JDL)
                    || StringUtil.equals(getJobKey(), WMSConstants.TYPE_CONDOR_JDL))
            {

                debug("Should do something!!");
            }
            if (childNodes == null)
            {
                initChilds();
            }
            VNode[] nodesArray = null;
            if (childNodes != null)
            {
                nodesArray = new VNode[childNodes.size()];

                nodesArray = childNodes.values().toArray(nodesArray);
            }

            return nodesArray;
        }
    }

    private void initChilds() throws VlException
    {

        if (childNodes != null)
            return;

        debug("Get output info for: " + this.getJobId());
        debug("Get output info for: " + this.getJobKey());
        debug("Get output info for: " + this.getJobUri());

        String jdl = getLBJobStatus().getJdl();
        String matchedJdl = getLBJobStatus().getMatchedJdl();
        String condorJdl = getLBJobStatus().getCondorJdl();

        childNodes = new HashMap<VRL, VNode>();
        VRSContext ctx = this.getVRSContext();

        WMSJob[] jobs = getChildJobs();
        if (jobs != null)
        {
            ChildJobs chNodes = new ChildJobs(ctx, this.getVRL().append("child_jobs"), jobs);
            for (int i = 0; i < jobs.length; i++)
            {
                jobs[i].setLogicalParent(chNodes);
            }
            childNodes.put(chNodes.getVRL(), chNodes);
            isCllectionOrDAG = true;
        }

        ArrayList<JDLNode> jdlNodes = new ArrayList<JDLNode>();
        boolean isJDL;
        if (jdl != null)
        {
            isJDL = true;
            try
            {
                org.glite.jdl.JobAd j = new JobAd(jdl);
            }
            catch (Exception e)
            {
                isJDL = false;
            }
            if (isJDL)
            {
                JDLNode submitedJdl = new JDLNode(ctx, this.getVRL().append(
                        WMSConstants.TYPE_JDL_COMPOSITE + "/" + WMSConstants.TYPE_SUBMITED_JDL), jdl);
                jdlNodes.add(submitedJdl);
            }

        }
        if (matchedJdl != null)
        {
            isJDL = true;
            try
            {
                org.glite.jdl.JobAd j = new JobAd(matchedJdl);
            }
            catch (Exception e)
            {
                isJDL = false;
            }
            if (isJDL)
                jdlNodes.add(new JDLNode(ctx, this.getVRL().append(
                        WMSConstants.TYPE_JDL_COMPOSITE + "/" + WMSConstants.TYPE_MATCHED_JDL), matchedJdl));
            // debug("MatchedJDL: "+matchedJdl);
        }
        if (condorJdl != null)
        {
            isJDL = true;
            try
            {
                org.glite.jdl.JobAd j = new JobAd(matchedJdl);
            }
            catch (Exception e)
            {
                isJDL = false;
            }
            if (isJDL)
                jdlNodes.add(new JDLNode(ctx, this.getVRL().append(
                        WMSConstants.TYPE_JDL_COMPOSITE + "/" + WMSConstants.TYPE_CONDOR_JDL), condorJdl));
            // debug("CondorJDL: "+condorJdl);
        }

        if (jdlNodes != null && !jdlNodes.isEmpty())
        {
            JDLNode[] nodes = new JDLNode[jdlNodes.size()];

            nodes = jdlNodes.toArray(nodes);
            JDLs jdlNode = new JDLs(ctx, this.getVRL().append(WMSConstants.TYPE_JDL_COMPOSITE), nodes);
            logger.debugPrintf(">>>>>New JDL Composite Node; %s\n", jdlNode.getVRL());

            JDLNode[] jdlNodes3 = (JDLNode[]) jdlNode.getNodes();
            for (JDLNode n : jdlNodes3)
            {
                logger.debugPrintf(">>>>>JDL VRLs:%s\n", n.getVRL());
            }

            childNodes.put(jdlNode.getVRL(), jdlNode);
        }

        if (!getIsCollectionOrDAG())
        {
            if ((WMSUtil.statusIsDone(StatName.fromString(getStatus())))
                    && (WMSUtil.statusIsCleared(StatName.fromString(getStatus())) == false))
            {
                JobOutputNode jo = new JobOutputNode(ctx, this.getVRL().append("outputs"), getWMS().getJobOutputs(
                        this.getJobUri()));
                childNodes.put(jo.getVRL(), jo);
            }
        }

        Event[] events = getEvents();
        if (events != null)
        {
            JobEvents jobEvents = new JobEvents(ctx, this.getVRL().append("events"), events);
            childNodes.put(jobEvents.getVRL(), jobEvents);
        }

    }

    public boolean getIsCollectionOrDAG()
    {
        return isCllectionOrDAG;
    }

    protected WMSJob[] getChildJobs() throws VlException
    {

        WMSJob[] childJobs = null;
        try
        {
            JobStatus status = getLBJobStatus();

            if (status.getChildrenNum() > 0)
            {
                childJobs = new WMSJob[status.getChildrenNum()];

                JobStatus[] statuses = status.getChildrenStates();
                String[] ids = status.getChildren();
                for (int i = 0; i < status.getChildrenNum(); i++)
                {
                    VRL vrl = getVRL().append("child_jobs").append(new URI(ids[i]).getPath());
                    childJobs[i] = (new WMSJob(lbResource, vrl, new URI(ids[i]), statuses[i]));
                }
            }
            
//            ChildrenHistItem[] hist = status.getChildrenHist();
//            
//            if (hist != null)
//            {
//                if (hist.length > 0)
//                {
//                    for (int i = 0; i < hist.length; i++)
//                    {
//                        StatName state = hist[i].getState();
//                        logger.debugPrintf(">>> What is this:%s\n", state.getValue());
//                    }
//
//                }
//            }
            
        }
        catch (Exception e)
        {
            throw new VRLSyntaxException(e);
        }
        return childJobs;
    }

    private void debug(String msg, Object... args)
    {
        logger.debugPrintf(msg + "\n", args);
    }

    public VNode getSubNode(String childName) throws VlException
    {
        return getNode(childName);
    }

    @Override
    public boolean delete(boolean rec)
    {
        // Remove From LB Service: Purge?
        return false;
    }

    public VNode getJDL(VRL name) throws VlException
    {
        debug("This job is :" + this.getVRL());
        debug("Looking for: " + name);

        synchronized (initChildMutex)
        {
            if (childNodes == null)
            {
                initChilds();
            }
        }

        for (int i = 0; i < childNodes.size(); i++)
        {
            if (childNodes.get(i).getVRL().equals(name))
            {
                return childNodes.get(i);
            }

        }
        return null;
    }

    public void cancel() throws VlException
    {
        getWMS().cancelJob(getJobId());
    }

    public WMSResource getWMS() throws VlException
    {
        if (this.wmsResource == null)
        {
            this.wmsResource = this.getLBResource().getWMS();

            // ===
            // WMS NOT initialized ! // guess from JobStatus!
            // ===

            if (this.wmsResource == null)
            {
                URI serverURI = this.getWmsServerURI();

                // if (serverURI == null)
                // {
                //
                // serverURI = ((WMSJob) getParent()).getWmsServerURI();
                //
                // }
                // else
                // {
                // logger.debugPrintf("URIIIIIIIIIIIIIIIII: %s\n", serverURI);
                // }

                VRL wmsVrl = WMLBConfig.createWMSVrl(serverURI);

                this.wmsResource = WMSResource.getFor(this.vrsContext, wmsVrl);

                Global.infoPrintf(WMSJob.class, ">>>\n>>> Original WMSResource was not set. Settings to WMS:s\n>>>\n",
                        wmsVrl);
            }

        }

        return this.wmsResource; // if set. can be null !
    }

    @Override
    public String getIconURL()
    {
        try
        {

            return getStatusIcon(this.getLBJobStatus().getState().getValue());
        }
        catch (VlException e)
        {
            return "jobError.png";
        }
    }

    public String getStatusIcon(String status)
    {
        // if (StringUtil.equals(status, StatName.DONE.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Cute-Ball-Go-icon.png";
        // }
        // if (StringUtil.equals(status, StatName.ABORTED.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Cute-Ball-Stop-icon.png";
        // }
        // if (StringUtil.equals(status, StatName.CANCELLED.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Cute-Ball-Stop-icon.png";
        // }
        // if (StringUtil.equals(status, StatName.RUNNING.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Cute-Ball-Logoff-icon.png";
        // }
        // if (StringUtil.equals(status, StatName.SCHEDULED.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Perspective-Button-Time-icon.png";
        // }
        // if (StringUtil.equals(status, StatName.SUBMITTED.getValue()))
        // {
        // return
        // "http://www.iconarchive.com/icons/mazenl77/I-like-buttons-3a/256/Perspective-Button-Logoff-icon.png";
        // }

        String statstr = status.toLowerCase();
        return "jobicons/job" + statstr + ".png";
    }

    public Event[] getEvents() throws VlException
    {
        return getWMS().getJobEvents(getJobUri());
    }

    
    public ArrayList<OutputInfo> getOutputs() throws VlException
    {
        WMSJob[] chJobs = getChildJobs();
        // If the job is a collection or DAG can't get job output
        if (chJobs == null || chJobs.length <= 0)
        {
            return this.getWMS().getJobOutputs(jobUri);
        }
        return null;
    }

    public boolean hasOutputs() throws VlException
    {
    	// only check terminated node. 
    	if (this.hasTerminated()==false)
    		return false; 
    	
    	if (this.hasError())
    		return false;
    	
    	ArrayList<OutputInfo> outputs = this.getOutputs(); 
    	
    	if ((outputs==null) || (outputs.size()<=0))
    		return false;
    	
    	return true; 
    }
   
   
    /**
     * Returns output info as VRL array
     * 
     * @throws URISyntaxException
     */
    public VRL[] getOutputVRLs() throws VlException
    {

        ArrayList<OutputInfo> infos = this.getOutputs();

        VRL vrls[] = new VRL[infos.size()];
        for (int i = 0; i < infos.size(); i++)
        {
            try
            {
                vrls[i] = new VRL(infos.get(i).getFileURI());
            }
            catch (URISyntaxException e)
            {
                throw new VRLSyntaxException("Invalid URI:" + infos.get(i).getFilename(), e);
            }
        }
        return vrls;
    }

    public VNode findChild(VRL vrl) throws VlException
    {
        initChilds();

        // Start looking for child nodes
        // Is it in the first level?
        VNode childNode = childNodes.get(vrl);
        if (childNode != null)
            return childNode;

        logger.debugPrintf("Looking for: %s in %s\n", vrl, getVRL());

        for (VNode node : childNodes.values())
        {
            logger.debugPrintf("childNodes: %s\n", node.getVRL());

            if (node.getVRL().equals(vrl))
            {
                return node;
            }

            if (node.getVRL().isParentOf(vrl))
            {
                if (node instanceof JobEvents)
                {
                    return ((JobEvents) node).findChild(vrl);
                }

                if (node instanceof JDLs)
                {
                    return ((JDLs) node).findChild(vrl);
                }

                if (node instanceof ChildJobs)
                {
                    WMSJob[] wmsJobs = (WMSJob[]) ((ChildJobs) node).getNodes();
                    for (WMSJob job : wmsJobs)
                    {
                        VNode child = job.findChild(vrl);
                        if (child != null)
                        {
                            return child;
                        }
                    }
                    throw new ResourceNotFoundException("Resource not found:" + vrl);
                }
            }

        }
        return null;
    }

    // public method to allow invocation from package class
    public void fireAttributesChanged(VAttribute attrs[])
    {
        super.fireAttributesChanged(attrs);
    }

    // public method to allow invocation from package class
    public void fireAttributeChanged(VAttribute attr)
    {
        super.fireAttributeChanged(attr);
    }

    protected void fireStatusChanged(JobStatus status)
    {
        // String[] attrNames = job.getAttributeNames();
        String attrNames[] = { 
                VAttributeConstants.ATTR_STATUS, 
                VAttributeConstants.ATTR_ICONURL,
                // update both VJob status and WMS status: 
                WMSConstants.ATTR_JOB_STATUS_UPDATE_TIME,
                WMSConstants.ATTR_WMS_LAST_UPDATE_TIME
                };

        try
        {
            VAttribute attrs[] = this.getAttributes(attrNames);
            debug("--->>> Fire Status Changed:" + getStatus());
            this.fireAttributesChanged(attrs);
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
    }

    public void purge(ITaskMonitor monitor) throws VlException
    {
        this.getWMS().purgelJob(monitor, getJobId());
    }

    protected void setWMS(WMSResource wms)
    {
        this.wmsResource = wms;
    }

    public void setLogicalParent(VNode parent)
    {
        this.logicalParent = parent;
    }

    public void setIsCollectionOrDAG(boolean b)
    {
        this.isCllectionOrDAG = b;
    }

    public boolean sync()
    {
    	this.cachedJobStatus=null;
    	return true; 
    }
    
    // public String[] getChildren()
    // {
    // String[] children = cachedJobStatus.getChildren();
    //        
    // debug("---------------------Children: %s\n", children.length);
    //        
    // return null;
    // }

    // public void getUserTags(JobStatus jobStat) throws VlException
    // {
    // TagValue[] tags = jobStat.getUserTags();
    // if (tags!=null)
    // for (TagValue tag:tags)
    // {
    // System.err.println(" -tag :"+tag.getTag()+":"+tag.getValue());
    // }
    // }

}
