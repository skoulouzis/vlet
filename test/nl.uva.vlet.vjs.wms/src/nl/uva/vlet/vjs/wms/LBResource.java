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
 * $Id: LBResource.java,v 1.5 2011-06-07 15:14:33 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:33 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.net.URI;
import java.util.List;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vjs.VJob;
import nl.uva.vlet.vjs.VJobMonitorResource;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.ResourceSystemNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wsdl.types.lb.JobStatus;

/**
 * Standalone LB Resource. Doesn't have any WMS associated with it
 */
public class LBResource extends ResourceSystemNode implements IJobStatusListener, 
    VPresentable, VJobMonitorResource
{
    private static ClassLogger logger = null;

    public static String[] lbresource_AttributeNames = 
        { 
            VAttributeConstants.ATTR_ICON,
            VAttributeConstants.ATTR_TYPE,
            VAttributeConstants.ATTR_NAME,
            WMSConstants.ATTR_JOB_SUBMISSION_TIME,
            WMSConstants.ATTR_WMS_SERVER_HOSTNAME,
            VAttributeConstants.ATTR_STATUS, WMSConstants.ATTR_REASON 
         };

    static
    {
        logger = ClassLogger.getLogger(LBResource.class);
        //logger.setLevelToDebug();
    }

    private static Object classMutex = new Object();

    public static LBResource getFor(VRSContext context, VRL loc) throws VlException
    {

        synchronized (classMutex)
        {
            String serverID = ResourceSystemNode.createServerID(loc);

            LBResource lb = (LBResource) context.getServerInstance(serverID, LBResource.class);

            if (lb == null)
            {
                // store new client
                ServerInfo srmInfo = context.getServerInfoFor(loc, true);
                lb = new LBResource(context, srmInfo);
                lb.setID(serverID);
                context.putServerInstance(lb);
                lb.connect();
            }
            else
            {
                if (lb.isConnected() == false)
                    lb.connect();
            }
            return lb;
        }
    }

    private LBJobCache lbJobCache = null;

    private JobStatus cachedJobStatus = null;

    private WMSResource wms;

    // Initialize default object: also used as mutex:
    private Vector<WMSJob> cachedJobNodes = new Vector<WMSJob>();

    private Presentation presentation;

    public LBResource(VRSContext context, ServerInfo info)
    {
        super(context, info);
        this.lbJobCache = LBJobCache.getCache(context, info.getHostname());
        this.lbJobCache.addJobListener(this);
        logger.debugPrintf(">>> New LBResource for:%s <<<\n", info.getHostname());
    }

    public String getType()
    {
        return WMSConstants.TYPE_LBRESOURCE;
    }

    public Presentation getPresentation()
    {
        if (this.presentation == null)
        {
            this.presentation = createDefaultPresentation();
        }
        return this.presentation;
    }

    public static Presentation createDefaultPresentation()
    {
        Presentation pres = new Presentation();
        pres.setChildAttributeNames(lbresource_AttributeNames);

        pres.setAttributePreferredWidth(VAttributeConstants.ATTR_TYPE, 40);
        pres.setAttributePreferredWidth(VAttributeConstants.ATTR_NAME, 220);
        pres.setAttributePreferredWidth(WMSConstants.ATTR_JOB_SUBMISSION_TIME, 160);
        pres.setAttributePreferredWidth(WMSConstants.ATTR_WMS_SERVER_HOSTNAME, 140);
        pres.setAttributePreferredWidth(VAttributeConstants.ATTR_STATUS, 80);
        pres.setAttributePreferredWidth(WMSConstants.ATTR_REASON, 200);
        
        //Enable Presention level sorting: sort using jobSubmissionTime 
        pres.setAutoSort(true); 
        pres.setSortFields(new String[]{WMSConstants.ATTR_JOB_SUBMISSION_TIME}); 
        
        return pres;
    }

    @Override
    public WMSJob[] getNodes() throws VlException// throws VlException
    {
        WMSJob jobs[] = queryUserJobs(false);

        if (jobs != null)
        {
            synchronized (cachedJobNodes)
            {
                cachedJobNodes.clear();
                cachedJobNodes.setSize(jobs.length + 10);
                for (WMSJob job : jobs)
                {
                    cachedJobNodes.add(job);
                    job.setLogicalParent(this);
                }
            }
        }

        return jobs;

    }

    public void addNewJob(WMSJob job) throws VlException// throws VlException
    {
        if (cachedJobNodes == null)
            cachedJobNodes = new Vector<WMSJob>();

        synchronized (cachedJobNodes)
        {
            cachedJobNodes.add(job);
        }
    }

    protected WMSJob[] queryUserJobs(boolean fullUpdate) throws VlException
    {
        logger.debugPrintf("getNodes(): for %s\n", getHostname());

        ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("LBResource.getNodes()", 1);
        monitor.startSubTask("query user jobs for:" + this.getHostname(), 1);

        boolean done = lbJobCache.queryUserJobs(monitor, fullUpdate);

        // Synchronized with asynchronous method
        if (done == false)
        {
            // logger.debugPrintf("waitForQueryUserJobs(): for %s\n",
            // getHostname());
            lbJobCache.waitForQueryUserJobs();
            // logger.debugPrintf("waitForQueryUserJobs(): for %s: done\n",
            // getHostname());
        }

        // Update (re) query jobs for specific LB:
        List<String> ids = lbJobCache.getCachedJobIDs();
        if ((ids == null) || (ids.size() == 0))
            return null;
        //

        Vector<WMSJob> nodes = new Vector<WMSJob>();
        for (int i = 0; i < ids.size(); i++)
        {
            // Wrap Job:
            java.net.URI jobUri = new VRL(ids.get(i)).toURI();
            VRL jobVrl = createJobVrl(jobUri);
            WMSJob job = new WMSJob(this, jobVrl, jobUri);
            String parentID = job.getLBJobStatus().getParentJob();
            if (parentID == null)
            {
                nodes.add(job);
            }
            // logger.debugPrintf("The paret of %s is %s\n", getVRL(),parentID);
        }

        monitor.updateSubTaskDone(1);
        monitor.endSubTask("query user jobs for:" + this.getHostname());

        WMSJob[] nodesArr = new WMSJob[nodes.size()];
        nodesArr = nodes.toArray(nodesArr);

        return nodesArr;
    }

    VRL createJobVrl(URI jobUri)
    {
        return this.getVRL().append(jobUri.getPath());
    }

    private java.net.URI createJobUri(VRL jobVrl) throws VRLSyntaxException
    {
        return new VRL(VRS.HTTPS_SCHEME, jobVrl.getHostname(), 9000, jobVrl.getPath()).toURI();
    }

    @Override
    public String[] getResourceTypes()
    {
        return new String[] { VJS.TYPE_VJOB };
    }

    public WMSJob getJob(VRL jobVrl) throws VlException
    {
        java.net.URI jobUri = this.createJobUri(jobVrl);
        JobStatus stat = this.lbJobCache.queryJobStatus(jobUri.toString(), false);

        String[] children = stat.getChildren();

        if (stat != null)
        {
            WMSJob job = new WMSJob(this, jobVrl, jobUri, stat);
            if (children != null)
            {
                job.setIsCollectionOrDAG(true);
            }

            return job;
        }

        throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't query job:" + jobUri);
    }

    public WMSJob getJobByJobID(String jobId) throws VlException
    {
        try
        {
            // use cache:
            java.net.URI jobUri = new java.net.URI(jobId);
            JobStatus stat = this.lbJobCache.queryJobStatus(jobUri.toString(), false);

            if (stat != null)
                return new WMSJob(this, this.createJobVrl(jobUri), jobUri, stat);
        }
        catch (Exception e)
        {
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't resolve Job:" + jobId, e);
        }

        throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't query job:" + jobId);
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public void connect() throws VlException
    {

    }

    @Override
    public void disconnect() throws VlException
    {

    }

    @Override
    public VNode openLocation(VRL vrl) throws VlException
    {
        VRL newVRL = vrl;

        String path = newVRL.getPath();

        if (StringUtil.isEmpty(path) || StringUtil.equals(path, "/"))
        {
            return this;
        }

        String[] pathParts = vrl.getPathElements();

        String jobid = pathParts[0];
        VRL jobVrl = vrl.copyWithNewPath(jobid);

        WMSJob job = (WMSJob) this.getJob(jobVrl);

        if (pathParts.length == 1)
            return job;
        
        if (job.getVRL().isParentOf(newVRL)) 
            return job.findChild(newVRL);
        else
            throw new ResourceNotFoundException("This resource is not a job resource : "+newVRL);
    }

    public JobStatus queryStatus(URI jobUri, boolean updateCache) throws VlException
    {
        // Micro cache:
        this.cachedJobStatus = this.lbJobCache.queryJobStatus(jobUri.toString(), updateCache);
        return this.cachedJobStatus;
    }

    public static VRL createLBVrlForHost(String hostname)
    {
        return new VRL(VRS.LB_SCHEME, hostname.toLowerCase(), 9000, "/");
    }

    public void updateJobStatuses(ITaskMonitor monitor, boolean fullUpdate)
    {
        this.lbJobCache.queryJobStatuses(monitor, null, fullUpdate);
    }

    public boolean jobPurged(String jobId)
    {
        return this.lbJobCache.jobPurged(jobId);
    }

    public WMSJob[] getUserJobs(boolean fullUpdate) throws VlException
    {
        logger.debugPrintf("getUserJobs fullUpdate=%s for:%s\n",this,(fullUpdate?"true":"false"));
        return this.queryUserJobs(fullUpdate);
    }

    // / package protected: my only be used by WMS Resource
    protected void setWMS(WMSResource wmsResource)
    {
        this.wms = wmsResource;
    }

    public WMSResource getWMS()
    {
        return this.wms;
    }

    @Override
    public void notifyJobEvent(JobEvent event)
    {
        // logger.debugPrintf("Received Event:%s\n", event);

        try
        {
            switch (event.getType())
            {
                case NEW_JOB:
                    this.notifyNewJob(event.getJobID());
                    break;
                case UPDATE_USERJOBS:
                    // this.notifyUpdateUserJobs(event.getUserJobs());
                    break;
                case UPDATE_STATUS:
                    WMSJob job = this.getJobByJobID(event.getJobID());
                    job.fireStatusChanged(event.getJobStatus());
                    break;
                default:
                    logger.errorPrintf("Unknown Job Event:%s\n", event.getType());
                    break;
            }
        }
        catch (Exception ex)
        {
            logger.logException(ClassLogger.ERROR, ex, "Exception during event handling:%s\n", event);
        }
    }

    public void notifyNewJob(String jobid)
    {
        try
        {
            WMSJob job = this.getJobByJobID(jobid);
            JobStatus jobStat = job.getLBJobStatus();

            if ((this.cachedJobNodes == null) || (cachedJobNodes.size() <= 0))
            {
                // First Job: bug in resourceTree, initialize tree by firing a
                // setChilds!
                this.addNewJob(job);
                this.fireSetChilds(new VRL[] { job.getVRL() });
            }
            else
            {
                this.addNewJob(job);
                this.fireChildAdded(job.getVRL());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // ==== VJobManagerResource === // 
    public VJob getJob(String jobid) throws VlException
    {
        return this.getJobByJobID(jobid); 
    }
    
    public VJob[] getJobs(String jobids[]) throws VlException
    {
        VJob jobs[]=new VJob[jobids.length]; 
        for (int i=0;i<jobids.length;i++)
            jobs[i]=this.getJobByJobID(jobids[i]);
        
        return jobs; 
    }

    public String getJobStatus(String jobid) throws VlException
    {
        return this.getJobByJobID(jobid).getStatus(); 
    }

    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub
        
    }

}
