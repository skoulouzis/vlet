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
 * $Id: WMSResource.java,v 1.7 2011-06-07 15:14:33 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:33 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.naming.directory.InvalidAttributeValueException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlAuthenticationException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.OutputInfo;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.WMLBConfig.WMSConfig;
import nl.uva.vlet.glite.WMSClient;
import nl.uva.vlet.glite.WMSException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vjs.JobManagerNode;
import nl.uva.vlet.vjs.VJDLSubmitter;
import nl.uva.vlet.vjs.VJS;
import nl.uva.vlet.vjs.VJob;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceSystemNode;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.jdl.Jdl;
import org.glite.jdl.JobAd;
import org.glite.jdl.JobAdException;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wsdl.types.lb.Event;
import org.glite.wsdl.types.lb.JobStatus;

/**
 * Helper Class to access the Glite resources...
 * 
 * @author P.T. de Boer.
 * 
 */

public class WMSResource extends JobManagerNode implements VJDLSubmitter // ,
// IJobStatusListener
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(WMSResource.class);
//        logger.setLevelToDebug();
    }

    /** 'job' part in "wms://<host>:<port>/job/<jobid> */
    private static final String MYJOB_VRL_PREFIX = "myjobs";

    private static final Object classMutex = new Object();

    public static void stopAndDisposeAll()
    {

    }

    public static WMSResource getFor(VRSContext context, VRL loc) throws VlException
    {
        synchronized (classMutex)
        {
            String serverID = ResourceSystemNode.createServerID(loc);

            WMSResource wms = (WMSResource) context.getServerInstance(serverID, WMSResource.class);

            if (wms == null)
            {
                // store new client
                ServerInfo srmInfo = context.getServerInfoFor(loc, true);
                wms = new WMSResource(context, loc.copyWithNewPath("/"), srmInfo);
                wms.setID(serverID);
                context.putServerInstance(wms);
                wms.connect();
            }
            else
            {
                if (wms.isConnected() == false)
                    wms.connect();
            }
            return wms;
        }
    }

    /**
     * converts wms:// to https:// optionally stripping path prefix before
     * actual jobid
     */
    public static java.net.URI createJobUri(VRL wmsVrl, String lbpath, String key) throws VlException
    {
        String lbhost = null;
        int lbport = 0;

        if (lbpath != null)
        {
            String strs[] = lbpath.split("-");

            if ((strs == null) || (strs.length < 2))
                throw new ResourceNotFoundException("Wrong lppath syntax:" + lbpath);
            lbhost = strs[0];
            lbport = Integer.parseInt(strs[1]);
        }
        else
        {
            // Copy hostname from (WMS) Vrl!
            lbhost = wmsVrl.getHostname();
            lbport = WMLBConfig.LB_DEFAULT_PORT;
        }

        return new VRL("https", null, lbhost, lbport, key).toURI();
    }

    // convert job id into Job URI:
    public static URI createJobURI(String jobid) throws VlException
    {
        try
        {
            VRL jobID = new VRL(jobid);
            // convert back "wmslb" VRLs
            if (jobID.hasScheme(VJS.LB_SCHEME))
                jobID = jobID.copyWithNewScheme("https");

            return jobID.toURI();
        }
        catch (Exception e)
        {
            throw new VlException("Invalid JobId", "Couldn't create Job URI from:" + jobid, e);
        }
    }

    public static URI[] createJobURIs(String jobids[]) throws VlException
    {
        URI joburis[] = new URI[jobids.length];
        for (int i = 0; i < jobids.length; i++)
        {
            joburis[i] = createJobURI(jobids[i]);
        }
        return joburis;
    }

    // Parse WMS Job VRL and return actual JobURI (==jobid)
    public static URI createJobUriFromJobVrl(VRL vrl) throws VlException
    {
        String paths[] = vrl.getPathElements();
        // path[0]="job"|"myjobs"
        String lbpath = paths[1];
        String key = paths[2];
        // debug("Getting WMS Job,key:" + vrl + "," + key);
        // convert to URI:
        URI jobUri = createJobUri(vrl, lbpath, key);
        return jobUri;
    }

    /**
     * Create WMS Job VRL:<br>
     * - wms://&lt;hostname&gt;:&lt;port&gt;/job/&lt;lbpath&gt;/&lt;key&gt; <br>
     * - where &lt;lbpath&gt; equals "&lt;lbhostname&gt;-&lt;lbport&gt;"
     * 
     * @param jobUri
     * @return
     */
    public static VRL createJobVRLFromJobUri(VRL wmsVrl, URI jobUri)
    {
        String jobHost = jobUri.getHost();
        int jobPort = jobUri.getPort();
        String jobPath = jobUri.getPath();
        // prefix with "/"
        if (jobPath.startsWith("/") == false)
            jobPath = "/" + jobPath;

        VRL jobVrl = new VRL(VRS.WMS_SCHEME, null, wmsVrl.getHostname(), wmsVrl.getPort(), MYJOB_VRL_PREFIX + "/"
                + jobHost + "-" + jobPort + jobPath);

        return jobVrl;
    }
    // ===================================================================
    //
    // ===================================================================

    private WMSClient wmsClient;

    private WMSConfig wmsInfo;

    private ResourceLoader resourceLoader;

    private MyJobsNode myJobsNode;

    private VFSClient vfsClient;

    private Map<String, LBResource> myLBResources = new Hashtable<String, LBResource>();

    public WMSResource(VRSContext context, VRL wmsVrl, ServerInfo info) throws VlException
    {
        super(context, info);
        // logger.debugPrintf(">>> NEW WMSResource for:%s <<<\n",info.getHostname());
        try
        {
            init(context, WMLBConfig.createWMSUri(info.getHostname(), info.getPort()));
            initMyLBs();
        }
        catch (URISyntaxException e)
        {
            throw new nl.uva.vlet.exception.VRLSyntaxException("URIException", e);
        }
    }

    protected void initMyLBs()
    {
        logger.infoPrintf("-- Init My LB services for WMS:%s ---\n", getHostname());

        try
        {
            ArrayList<ServiceInfo> lbInfos = this.getVRSContext().getBdiiService().getLBServiceInfosForWMSHost(
                    this.getHostname());
            for (ServiceInfo info : lbInfos)
            {
                logger.infoPrintf(" - Adding LB Server: %s\n", info.getHost());

                // Prefetch
                LBResource lbServ = this.getLBResource(info.getHost());
                if (lbServ == null)
                    logger.errorPrintf("Got NULL LB Service for ServiceInfo:%s\n", lbServ);
                else
                    lbServ.setWMS(this);
                this.lbNodes.put(lbServ.getHostname(), lbServ);
            }
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN, e, "Couldn't initialized my LB Services:%s");
        }
    }

    /**
     * One-for-all initializer
     * 
     * @throws VlException
     */
    private void init(VRSContext context, java.net.URI uri)
    {
        if (context == null)
            throw new NullPointerException("context can not be null");

        if (uri == null)
            throw new NullPointerException("URL can't be null");

        StringList caList = new StringList();

        VRL[] vrls = GlobalConfig.getCACertificateLocations();
        for (VRL vrl : vrls)
            caList.add(vrl.getDirPath());

        // caList.add(GlobalConfig.getUserCertificatesDir().getDirPath());

        wmsInfo = new WMSConfig(uri, context.getGridProxy().getProxyFilename(), caList);

        // wmsInfo = new WMSConfig(uri,
        // context.getGridProxy().getProxyFilename(), null);
        //        
        // logger.debugPrintf(" --- wmsinfo --- \n");
        // logger.debugPrintf(" wmsinfo.hostname :%s\n",wmsInfo.getHostname());
        // logger.debugPrintf(" wmsinfo.port     :%d\n",wmsInfo.getPort());
        // logger.debugPrintf(" wmsinfo.proxyfile:%s\n",wmsInfo.getProxyFilename());

        resourceLoader = new ResourceLoader(context);
    }

    @Override
    public synchronized void connect() throws VlException
    {
        connect(false);
    }

    public void connect(boolean disconnectFirst) throws VlException
    {
        // logger.debugPrintf("*** CONNECTING to: '%s'***\n",getHostname());

        if ((wmsClient != null) && (disconnectFirst == true))
        {
            disconnect();
            wmsClient = null;
        }

        GridProxy prox = this.getVRSContext().getGridProxy();

        if (prox.isValid() == false)
            throw new VlAuthenticationException("Invalid Grid Proxy. Please create one");

        try
        {
            this.wmsClient = new nl.uva.vlet.glite.WMSClient(wmsInfo);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", "Failed in to create WMS client", e);
        }
    }

    @Override
    public synchronized void disconnect()
    {
        // logger.debugPrintf("*** DISCONNECTING from: %s ***\n",this);

        // stopWatcher();

        if (wmsClient != null)
        {
            wmsClient.disconnect();
            this.wmsClient = null;
        }

    }

    public String getVersion() throws VlException
    {
        try
        {
            return wmsClient.getVersion();
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", e.getMessage(), e);
        }
    }

    /** Submit JDL file. */ 
    public WMSJob submit(VRL jobDescription) throws VlException
    {
        return submitJdlFile(jobDescription);
    }

    /**
     * Submits jdl file specified byt he VRL.
     * 
     * @return
     * @throws.Exception if any error occurs
     */
    public WMSJob submitJdlFile(VRL jdlFile) throws VlException
    {
        String jdl = this.resourceLoader.getText(jdlFile);
        // -------------------------------------------------------
        // 1.Update jdl with new InputSandbox locations (if files are local)
        // jad[0] is the old jdl but now with absolute paths (includes schema).
        // jad[1] is the new jdl with the fearute location of the local files

        // Spiros:Dead variable
        // JobAd[] jad = updateJDL(jdl, jdlFile);
        // -------------------------------------------------------
        // // 2.Register job
        // JobIdStructType jobinfo;
        // try
        // {
        // jobinfo = this.wmsClient.registerJbo(jad[1].toSubmissionString(),
        // "del-id");
        // }
        // catch (WMSException e)
        // {
        // throw new VlException("WMSException", "Job registration failed", e);
        // }
        // catch (JobAdException e)
        // {
        // throw new VlException("WMSException", "Job registration failed", e);
        // }
        // get the id
        // String id = jobinfo.getId();
        // URI jobUri = createJobURI(id);
        // cretae wmsjob
        // WMSJob job = new WMSJob(this,
        // createJobVRLFromJobUri(this.getVRL(),jobUri), jobUri);
        // add new job in cache ??
        // this.wmsCache.registerNewJob(id);

        // -------------------------------------------------------
        // 3.Do the actual prestage
        // prestage(jad);
        // -------------------------------------------------------
        // 4.Run job
        // wmsClient.runJob();
        return submitJdlString(jdl);
    }

    public WMSJob registerAndRunJdlFile(VRL jdlFile) throws VlException
    {
        WMSJob job = null;
        String jdl = this.resourceLoader.getText(jdlFile);
        try
        {
            // -------------------------------------------------------
            // 1.Update jdl with new InputSandbox locations (if files are local)
            // jad[0] is the old jdl but now with absolute paths (includes
            // schema).
            // jad[1] is the new jdl with the future location of the local files
            JobAd[] jad = updateJDL(jdl, jdlFile);
            // -------------------------------------------------------
            // 2.Register job
            JobIdStructType jobinfo;

            // debug("Will toString: "+jad[1].toString());
            // debug("Will toLines: " + jad[1].toLines());
            // debug("Will toSubmissionString: "+jad[1].toSubmissionString());

            jobinfo = this.wmsClient.registerJbo(jad[1].toString(), "del-id");
            // get the id
            String id = jobinfo.getId();
            URI jobUri = createJobURI(id);
            // cretae wmsjob
            job = createNewWMSJobFromUri(jobUri);

            // -------------------------------------------------------
            // 3.Do the actual prestage
            prestage(jad);
            // -------------------------------------------------------
            // 4.Run job
            wmsClient.runJob(id);

        }
        catch (Exception e)
        {
            throw new VlException("WMSException", "Job registration failed", e);
        }
        return job;
    }

    /** Create new WMSJob instance. */ 
    protected WMSJob createNewWMSJobFromUri(java.net.URI jobUri) throws VlException
    {
        String lbHost = jobUri.getHost();
        LBResource lbRes = this.getLBResource(lbHost);
        VRL jobVrl = lbRes.createJobVrl(jobUri);
        return new WMSJob(lbRes, jobVrl, jobUri);
    }

    /** Resolve LB Hostname and return LB Resource */ 
    public LBResource getLBResource(String hostname) throws VlException
    {
        synchronized (this.myLBResources)
        {
            VRL lbVrl = LBResource.createLBVrlForHost(hostname);

            LBResource resource = this.myLBResources.get(hostname);
            if (resource == null)
                resource = LBResource.getFor(getVRSContext(), lbVrl);
            this.myLBResources.put(hostname, resource);

            return resource;
        }
    }

    /** Return LB Resource associated with this WMS. */ 
    public LBResource[] getLBNodes()
    {
        if (lbNodes == null)
            this.initMyLBs();

        if (lbNodes.size() <= 0)
            return null;

        synchronized (this.lbNodes)
        {
            LBResource nodes[] = new LBResource[this.lbNodes.size()];
            nodes = lbNodes.values().toArray(nodes);
            return nodes;
        }
    }

    /** Submit plain JDL String. */ 
    public WMSJob submitJdlString(String jdlString) throws VlException
    {
        JobIdStructType jobinfo;

        try
        {
            jobinfo = this.wmsClient.submitJdlString(jdlString);
            String id = jobinfo.getId();
            URI jobUri = createJobURI(id);

            WMSJob job = createNewWMSJobFromUri(jobUri);
            return job;
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", "Job Submission failed", e);
        }

    }

    @Override
    public VNode openLocation(VRL vrl) throws VlException
    {
        logger.debugPrintf("Get resource: %s\n", vrl);

        // check own wms: VRL scheme:
        if (vrl.hasScheme(VJS.WMS_SCHEME) == false)
        {
            // Use "http[s]://" uri as JobId!
            if ((vrl.hasScheme(VRS.HTTP_SCHEME)) || (vrl.hasScheme(VRS.HTTP_SCHEME)))
                // just try to blind fetch a job:
                return getOrCreateJob(vrl.toURI());
            else if (vrl.hasScheme(VJS.LB_SCHEME))
            {
                LBResource lb = LBResource.getFor(getVRSContext(), vrl);

                String path = vrl.getPath();

                if ((path == null) || (path == "") || path == "/")
                    return lb;
                else
                    return lb.openLocation(vrl);
            }
            else
            {
                throw new ResourceNotFoundException("Not a WMS or Job resource (invalid scheme):" + vrl);
            }
        }

        // find in cache:
        WMSJob job = findJob(vrl);
        if (job != null)
            return job;

        // dissect VRL:
        String paths[] = vrl.getPathElements();

        for (int i = 0; i < paths.length; i++)
        {
            // debug("Paths[" + i + "]:" + paths[i]);
        }

        // this.
        if ((paths == null) || (paths.length <= 0))
        {
            return this;
        }

        // Default: use last part of path as JobId:
        if (paths.length == 1)
        {
            if (StringUtil.equals(paths[0], ""))
                return this;

            if (StringUtil.equals(paths[0], "~"))
                return this;

            if (StringUtil.equals(paths[0], "/"))
                return this;

            // Match wms://.../[job|myjobs]
            if (StringUtil.equals(paths[0], "myjobs") || StringUtil.equals(paths[0], "job"))
                return getMyJobsNode();// should be all jobs (todo)

            // wms://host:port/<KEY>
            return getOrCreateJob(createJobUri(vrl, null, paths[0]));
        }

        else if ((paths.length >= 3) && (StringUtil.equals(paths[0], MYJOB_VRL_PREFIX)))
        {
            // wms://.../myjobs/lbhost-lbport/key[/subpath]
            // get Job:

            URI jobUri = createJobUriFromJobVrl(vrl);
            // debug("Getting WMS Job URI=" + jobUri);
            job = getOrCreateJob(jobUri);

            // return actual job:
            if (paths.length == 3)
                return job;

            return job.findChild(vrl);
        }

        throw new nl.uva.vlet.exception.ResourceNotFoundException("Unknown (Job) resource. Couldn't get:" + vrl);

    }

    private Map<String, LBResource> lbNodes = new Hashtable<String, LBResource>();

    public LBResource getLBResource(VRL vrl) throws VlException
    {
        String lbHost = vrl.getHostname();

        synchronized (this.lbNodes)
        {
            LBResource node = lbNodes.get(lbHost);

            if (node == null)
            {
                // create servcer VRL without Path
                VRL lbVrl = vrl.copyWithNewPath("/");
                node = LBResource.getFor(getVRSContext(), vrl);
                lbNodes.put(lbHost, node);
                return node;
            }

            return node;
        }
    }

    private Object myJobsNodeMutex = new Object();

    // Initialize empty Hashmap: Use 'linked' to keap order
    private LinkedHashMap<VRL, WMSJob> cachedWMSJobs = new LinkedHashMap<VRL, WMSJob>();

    public MyJobsNode getMyJobsNode()
    {
        synchronized (myJobsNodeMutex)
        {
            if (this.myJobsNode == null)
                this.myJobsNode = new MyJobsNode(this);
            return myJobsNode;
        }
    }

    public VNode[] getNodes()
    {
        // create nodes array
        LBResource lbNodes[] = getLBNodes();
        int n = 0;
        if (lbNodes != null)
            n = lbNodes.length;

        VNode nodes[] = new VNode[1 + n];
        nodes[0] = getMyJobsNode(); // auto intializing node;

        if (lbNodes != null)
            for (int i = 0; i < n; i++)
                nodes[1 + i] = lbNodes[i];

        return nodes;
    }

    public WMSJob[] getUserJobs(ITaskMonitor monitor, boolean fullUpdate) throws VlException
    {
        if (monitor == null)
            monitor = ActionTask.getCurrentThreadTaskMonitor("getUserJobs for:" + this, 1);

        BooleanHolder someFailed=new BooleanHolder(); 
        // soft query: update if necessary
        this.updateUserJobs(monitor, fullUpdate,someFailed);

        // create private array
        synchronized (this.cachedWMSJobs)
        {
            // Ordered collection from LinkedHashmap!
            Collection<WMSJob> jobC = this.cachedWMSJobs.values();

            WMSJob jobArr[] = new WMSJob[cachedWMSJobs.size()];
            jobArr = jobC.toArray(jobArr);
            return jobArr;
        }
    }

    @Override
    public boolean isConnected()
    {
        return (wmsClient != null);
    }

    public nl.uva.vlet.glite.WMSClient getWMSClient()
    {
        return this.wmsClient;
    }

    public void setDelegationId(String id)
    {
        this.wmsClient.setDelegationID(id);
    }

    public void doDelegation(String id) throws VlException
    {
        try
        {
            this.wmsClient.doDelegation(id);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", e.getMessage(), e);
        }
    }

    public WMSJob getJob(String jobId) throws VlException
    {
        return getOrCreateJob(createJobURI(jobId));
    }

    

    public String getJobStatus(String jobid) throws VlException
    {
        return getJob(jobid).getStatus();
    }

    public String getJobStatus(String jobid, boolean forceUpdate) throws VlException
    {
        return getJob(jobid).getStatus(forceUpdate);
    }

    /** Get's from local cache or creates new WMSJop Object 
     * @throws VlException */
    protected WMSJob getOrCreateJob(URI jobUri) throws VlException
    {
        synchronized (this.cachedWMSJobs)
        {
            WMSJob job = this.findJob(jobUri);
            if (job != null)
                return job;
            
            // create new Job Instance:
            job=this.createNewWMSJobFromUri(jobUri); 
            this.cachedWMSJobs.put(job.getVRL(), job);
            return job;
        }
    }

    /**
     * Find job in cache using the specified Job ID (must be a job URI string). 
     * Returns NULL if not found. 
     */
    public WMSJob findJob(String jobID) throws VRLSyntaxException
    {
        try
        {
            return this.findJob(createJobVRLFromJobUri(this.getVRL(), new URI(jobID)));
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Not a Job URI:"+jobID,e); 
        } 
    }
    
    /**
     * Find job in cache using the specifed Job VRL. 
     * Returns NULL if not found.
     */
    public WMSJob findJob(VRL jobVrl)
    {
        synchronized (this.cachedWMSJobs)
        {
            return this.cachedWMSJobs.get(jobVrl);
        }
    }

    /** 
     * Find job in cache using job URI. 
     * Returns NULL if not found 
     */
    public WMSJob findJob(URI jobUri)
    {
        // guess VRL and search in local cache
        VRL jobVrl = createJobVRLFromJobUri(this.getVRL(), jobUri);

        synchronized (this.cachedWMSJobs)
        {
            return this.cachedWMSJobs.get(jobVrl);
        }
    }

    @Override
    public VJob[] getJobs(String[] jobids) throws VlException
    {
        VJob jobs[] = new VJob[jobids.length];
        for (int i = 0; i < jobids.length; i++)
            jobs[i] = this.getJob(jobids[i]);

        return jobs;
    }

    public void cancelJob(String jobId) throws VlException
    {
        try
        {
            this.wmsClient.cancelJob(jobId);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", e.getMessage(), e);
        }
    }

    public String getCollectionTemplate(int jobNumber, String requirements, String rank) throws WMSException
    {
        return this.wmsClient.getCollectionTemplate(jobNumber, requirements, rank);
    }

    /** Return Event list from Job */ 
    public Event[] getJobEvents(java.net.URI jobUri) throws VlException
    {
        try
        {
            return this.wmsClient.getJobEvents(jobUri);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", "Couldn't get Job Events:" + e.getMessage(), e);
        }
    }

    /**
     * Fetch Job Outputs. 
     * Can only be called after a job has terminated successfully.   
     */
    public ArrayList<OutputInfo> getJobOutputs(java.net.URI jobUri) throws VlException
    {
        try
        {
            return this.wmsClient.getJobOutputs(jobUri);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", "Couldn't get Job Outputs:" + e.getMessage(), e);
        }
    }

    // public void notifyJobStatusChange(String jobid, JobStatus status)
    // {
    // debug("EVENT:StatusChange of job:" + jobid);
    // debug("EVENT:StatusChange Status=" + status.getState().getValue());
    //
    // try
    // {
    // // check local cache:
    // WMSJob wmsJob = this.findJob(new java.net.URI(jobid));
    // if (wmsJob == null)
    // {
    // debug("JobStatus Event not for me but for wmsHost:" +
    // status.getNetworkServer() + "; jobid=" + jobid);
    // return;
    // }
    //
    // wmsJob.fireStatusChanged(status);
    // }
    // catch (URISyntaxException e)
    // {
    // e.printStackTrace();
    // }
    // }

    // @Override
    // public void notifyJobEvent(JobEvent event)
    // {
    // debug("Received Event:" + event);
    //
    // switch (event.getType())
    // {
    // case NEW_JOB:
    // this.notifyNewJob(event.getJobID());
    // break;
    // case UPDATE_USERJOBS:
    // this.notifyUpdateUserJobs(event.getUserJobs());
    // break;
    // case UPDATE_STATUS:
    // this.notifyJobStatusChange(event.getJobID(), event.getJobStatus());
    // break;
    // default:
    // error("Unknown Job Event:" + event.getType());
    // break;
    // }
    // }

    // public void notifyNewJob(String jobid)
    // {
    // // Job must be present in cache: since I received a new Job Event !
    // JobStatus jobStat;
    //
    // try
    // {
    // java.net.URI jobUri = new URI(jobid);
    // jobStat = this.getLBJobStatus(jobid, false);
    //
    // if (jobStat == null)
    // {
    // error("Recieved New Job Event but job is NOT in cache:" + jobid);
    // return;
    // }
    //
    // String wmsServer = jobStat.getNetworkServer();
    // java.net.URI wmsURI = new java.net.URI(wmsServer);
    //
    // if (!this.getHostname().equals(wmsURI.getHost()))
    // {
    // debug("New Job not for me (" + this.getHostname() + ") but for:" +
    // wmsURI);
    // return;
    // }
    // else
    // {
    // debug("Job for me! (" + this.getHostname() + ") == " + wmsURI);
    // }
    //
    // // update or create new in cache:
    // WMSJob wmsJob = this.getOrCreateJob(jobUri);
    // MyJobsNode myJobs = this.getMyJobsNode();
    //
    // // First Job!
    // if (this.cachedWMSJobs.size() == 1)
    // {
    // // To trigger the ResourceTree in the vbrowser
    // // a node has to be 'populated' first before a
    // // child can be added to it.
    // // So fire a "SET_CHILDS" as first event to trigger
    // // the tree (and other resource) to display the contents
    // // and create the first (initial) icon.
    //
    // debug("--->Fire SET_CHILDS Event:\nparent=" + myJobs + "\nchild=" +
    // wmsJob);
    // VRL vrls[] = getJobVRLs();
    //
    // ResourceEvent event = ResourceEvent.createSetChildsEvent(myJobs.getVRL(),
    // vrls);
    //
    // this.vrsContext.fireEvent(event);
    // }
    // else
    // {
    // // Added more jobs: MyJobs node must fire child event!
    // debug("--->Fire ADD_CHILD Event:\nparent=" + myJobs + "\nchild=" +
    // wmsJob);
    // myJobs.fireChildAdded(wmsJob.getVRL());
    // }
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }

    private JobStatus getLBJobStatus(String jobid, boolean fullUpdate) throws VlException
    {
        return this.getJob(jobid).getLBJobStatus(fullUpdate);
    }

    // helper method to return cached job VRLs as private array 
    private VRL[] _getCachedJobVRLs()
    {
        VRL vrls[] = null;

        // private Array:
        synchronized (this.cachedWMSJobs)
        {
            Collection<WMSJob> collection = this.cachedWMSJobs.values();
            vrls = new VRL[collection.size()];
            int index = 0;
            for (WMSJob job : collection)
                vrls[index++] = job.getVRL();
            return vrls;
        }
    }

    public void updateStatuses(ITaskMonitor monitor, boolean fullUpdate)
    {
        /** Forward to all registered LB Clients */
        for (String key : this.myLBResources.keySet())
        {
        	//only needed when using asynchronous cache. 
            this.myLBResources.get(key).asyncUpdateJobStatuses(monitor, fullUpdate);
        }
    }

    public void updateUserJobs(ITaskMonitor monitor, boolean fullUpdate, BooleanHolder someFailed) throws VlException
    {
        // monitor can be optional! 
        if (monitor == null)
            monitor = ActionTask.getCurrentThreadTaskMonitor("updateUserJobs():(fullUpdate="+fullUpdate+"):"+this.getHostname(),1); 
        
    	VlException lastEx=null;
    	
    	int n=myLBResources.keySet().size();
    	String task="Query UserJobs at "+n+" LB Resources."; 
        monitor.startSubTask(task, n);
        int i=0; 
        
        // Collect Jobs and put them in local cached job array
        for (String key : this.myLBResources.keySet())
        {
        	// try each LB resource: 
        	LBResource lb = this.myLBResources.get(key);
            monitor.logPrintf("Starting UserJobs Query for:%s\n",lb.getHostname()); 

        	try
        	{
        		WMSJob jobs[] = lb.getUserJobs(fullUpdate);
        		if (jobs != null)
        	    {
        			for (WMSJob job : jobs)
        			{
        				this.cachedWMSJobs.put(job.getVRL(), job);
        			}
        			
                    monitor.logPrintf(" - got #%d jobs.\n",jobs.length); 
        	    }
        	}        	
        	catch (VlException e)
        	{
        		logger.logException(ClassLogger.WARN,e,"Failed to query LB:%s\n",lb);
        		lastEx=e;
        		if (someFailed!=null)
        			someFailed.set(true); 
        	}
        	i++;
        	monitor.updateSubTaskDone(i);
        }
        
        monitor.endSubTask(task);
    }

    public void notifyUpdateUserJobs(String[] jobids)
    {
        Vector<WMSJob> newJobs = new Vector<WMSJob>();

        // filter my userjobs:
        for (String jobid : jobids)
        {
            try
            {
                JobStatus stat = this.getLBJobStatus(jobid, false);
                if (stat != null)
                {
                    String wmstr = stat.getNetworkServer();
                    URI wmsUri = new URI(wmstr);

                    if (wmsUri.getHost().equals(getHostname()))
                    {
                        WMSJob job = this.getOrCreateJob(new URI(jobid));
                        newJobs.add(job);
                    }
                }
            }
            catch (Exception e)
            {
                Global.errorPrintf(this, "*** Exception:%s\n", e);
            }
        }

        // nothing added
        if (newJobs.size() <= 0)
            return;

        // Perform Set Childs Event!
        VRL childs[] = this._getCachedJobVRLs();

        // Event source has to by "myjobs":
        this.getMyJobsNode().fireSetChilds(childs);
    }

    /** 
     * Purge Job 
     */
    public void purgelJob(ITaskMonitor monitor, String jobId) throws VlException
    {
        String taskStr = "Purging job:" + jobId;
        monitor.startSubTask(taskStr, 1);

        try
        {
            wmsClient.purgelJob(jobId);
            // notify cache!
            this.getLBResource(new java.net.URI(jobId).getHost()).jobPurged(jobId);

            monitor.logPrintf("Job purged:" + jobId + "\n");
            monitor.updateSubTaskDone(1);
        }
        catch (Exception e)
        {
            monitor.logPrintf("WMSClient:" + this + ": Failed to purge job:" + jobId + "\n");
            monitor.logPrintf("---\n" + e.getMessage() + "---\n");

            throw new VlException("WMSException", "WMSClient:" + this + ": Failed to purge job: " + jobId + "\n"
                    + e.getMessage(), e);
        }
        finally
        {
            monitor.endSubTask(taskStr);
        }

    }

    private JobAd[] updateJDL(String jdlString, VRL baseVrl) throws VlException
    {
        JobAd originalJobAd = new JobAd();
        JobAd updatedlJobAd = new JobAd();

        try
        {
            originalJobAd.fromString(jdlString);
            updatedlJobAd.fromString(jdlString);
            // Get the base URI
            if (originalJobAd.hasAttribute(Jdl.ISBBASEURI))
            {

                baseVrl = new VRL((String) originalJobAd.getStringValue(Jdl.ISBBASEURI).get(0));

            }
            else if (baseVrl == null)
            {
                baseVrl = getVFSClient().getUserHomeLocation().append("dummy.jdl"); // kludge
            }

            if (originalJobAd.hasAttribute(Jdl.INPUTSB))
            {
                Vector<String> originalInpuSandboxPaths = this.toStringVector(originalJobAd.getStringValue(Jdl.INPUTSB)); 

                Vector<VRL> originalInpuSandboxPathVRLs = resolvePaths(originalInpuSandboxPaths, baseVrl);

                originalJobAd.delAttribute(Jdl.INPUTSB);
                originalJobAd.setAttribute(Jdl.INPUTSB, originalInpuSandboxPaths.get(0).toString());
                // debug("-----------Before---------------");
                for (int i = 1; i < originalInpuSandboxPaths.size(); i++)
                {
                    // debug("" + originalInpuSandboxPaths.get(i));
                    originalJobAd.addAttribute(Jdl.INPUTSB, originalInpuSandboxPaths.get(i).toString());
                }

                Vector<VRL> updatedInpuSandboxPaths = local2RemotePaths(originalInpuSandboxPathVRLs, baseVrl);

                updatedlJobAd.delAttribute(Jdl.INPUTSB);
                updatedlJobAd.setAttribute(Jdl.INPUTSB, updatedInpuSandboxPaths.get(0).toString());

                for (int i = 1; i < updatedInpuSandboxPaths.size(); i++)
                {
                    updatedlJobAd.addAttribute(Jdl.INPUTSB, updatedInpuSandboxPaths.get(i).toString());
                }

//                Vector<String> paths = toStringVector(updatedlJobAd.getStringValue(Jdl.INPUTSB)); 
//                // debug("-----------After---------------");
//                for (int i = 0; i < paths.size(); i++)
//                {
//                    // debug("" + paths.get(i));
//                }
            }

        }
        catch (ParseException e)
        {
            throw new VlException("ParseException", "Job prestaging failed", e);
        }
        catch (JobAdException e)
        {
            throw new VlException("JobAdException", "Job prestaging failed", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new VlException("IllegalArgumentException", "Job prestaging failed", e);
        }
        catch (NoSuchFieldException e)
        {
            throw new VlException("NoSuchFieldException", "Job prestaging failed", e);
        }
        catch (InvalidAttributeValueException e)
        {
            throw new VlException("InvalidAttributeValueException", "Job prestaging failed", e);
        }
        return new JobAd[] { originalJobAd, updatedlJobAd };
    }

    // Spiros: check location string and resolve to VRLs 
    private Vector<VRL> resolvePaths(Vector<String> originalInpuSandboxPaths, VRL baseVrl) throws VRLSyntaxException
    {
        Vector<VRL> vrlPaths = new Vector<VRL>();
        String path;
        VRL relVrl;
        VRL absVrl;
        VRL newBaseVrl = baseVrl.appendPath("dummy");
        for (int i = 0; i < originalInpuSandboxPaths.size(); i++)
        {
            path = (String) originalInpuSandboxPaths.get(i);
            // make it local
            if (path.startsWith("/"))
            {
                path = "file:///" + path;
            }

            relVrl = new VRL(path);
            if (relVrl.isAbsolute() == false)
            {
                absVrl = newBaseVrl.resolve(relVrl);
            }
            else
            {
                absVrl = relVrl;
            }
            vrlPaths.add(absVrl);
        }
        return vrlPaths;
    }

    private void prestage(JobAd[] jad) throws VlException
    {
        JobAd oldjad = jad[0];
        JobAd newjad = jad[1];

        try
        {
            if (oldjad.hasAttribute(Jdl.INPUTSB))
            {
                Vector<String> source = toStringVector(oldjad.getStringValue(Jdl.INPUTSB)); 
                Vector<String> destination = toStringVector(newjad.getStringValue(Jdl.INPUTSB));

                if (source.size() != destination.size())
                {
                    throw new VlException("Source and destination vectors must have the samme size!!!");
                }

                VRL vrlSource;
                VRL vrlDestination;
                VFSNode sourceNode;
                VFSNode destinationNode;
                long totalSize = 0;
                long maxSize = this.wmsClient.getMaxInputSandboxSize();
                for (int i = 0; i < source.size(); i++)
                {
                    // debug("Source: "+(String)source.get(i));
                    //                    
                    vrlSource = new VRL((String) source.get(i));
                    sourceNode = getVFSClient().openLocation(vrlSource);

                    totalSize += ((VFile) sourceNode).getLength();

                    if (totalSize > maxSize)
                    {
                        throw new VlException(
                                "Total size of files for input sandbox exide the maximum alowd size. Size up to now: "
                                        + totalSize + ". Maximum allowed: " + maxSize);
                    }

                    vrlDestination = new VRL((String) destination.get(i));
                    destinationNode = getVFSClient().openLocation(vrlDestination.getParent());

                    if (sourceNode.isLocal())
                    {
                        // debug("Copy from: " + vrlSource + " to: " +
                        // vrlDestination.getParent());
                        sourceNode.copyTo((VDir) destinationNode);
                    }
                }

                // debug("Total sandbox size: " + totalSize + " max size: " +
                // maxSize);
            }

        }
        catch (IllegalArgumentException e)
        {
            throw new VlException("IllegalArgumentException", "Job prestaging failed", e);
        }
        catch (NoSuchFieldException e)
        {
            throw new VlException("NoSuchFieldException", "Job prestaging failed", e);
        }
        catch (WMSException e)
        {
            throw new VlException("WMSException", "Job prestaging failed", e);
        }
    }

    // PTdB: Added explicit conversion from generic Vector<?> to Vector<String>.
    private Vector<String> toStringVector(Vector<?> vector)
    {
        if (vector==null)
            return null;
        
        if (vector.size()<=0) 
            return new Vector<String>(0); 

        Vector<String> strVec=new Vector<String>(vector.size()); 
        
        for (int i=0;i<vector.size();i++)
        {
            Object obj=vector.get(i); 
            if (obj instanceof String)
                strVec.add(i,(String)obj);
            else
                strVec.add(i,obj.toString());
        }
        
        return strVec; 
    }

    private Vector<VRL> local2RemotePaths(Vector<VRL> inputSandboxPath, VRL baseVrl) throws VlException
    {

        Vector<VRL> vrlPaths = new Vector<VRL>();
        VRL newBaseVrl = baseVrl.appendPath("dumy");
        VRL vrl;
        for (int i = 0; i < inputSandboxPath.size(); i++)
        {
            vrl = inputSandboxPath.get(i);
            VFSNode node = getVFSClient().openLocation(vrl);
            if (node.isLocal())
            {
                // debug(absVrl+" is local");
                if (!newBaseVrl.isLocalHostname())
                {
                    String[] paths = vrl.getPathElements();
                    vrl = baseVrl.appendPath(paths[paths.length - 1]);
                }
                else
                {

                }
            }
            vrlPaths.add(vrl);
        }
        return vrlPaths;
    }

    private VFSClient getVFSClient()
    {
        if (this.vfsClient == null)
        {
            this.vfsClient = new VFSClient(this.getVRSContext());
        }

        return vfsClient;
    }

    @Override
    public void dispose()
    {
        // TODO Auto-generated method stub
        
    }

}
