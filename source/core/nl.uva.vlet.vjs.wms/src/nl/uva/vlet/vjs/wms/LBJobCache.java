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
 * $Id: LBJobCache.java,v 1.4 2011-06-07 15:14:33 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:33 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.LBClient;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.WMSException;
import nl.uva.vlet.glite.WMSUtil;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vrs.VRSContext;

import org.glite.wsdl.types.lb.JobStatus;
import org.glite.wsdl.types.lb.StatName;

/**
 * Asynchronous WMS Job Cache. Optimized for multi threaded access. Multiple
 * WMSResources can share LB Services. This class caches the query requests.
 * <p>
 * All asynchronous updates and event notifications are done in
 * WMSJobCacheWatcher
 * 
 * @see WMSJobCacheWatcher WMSJobCacheWatcher
 */
public class LBJobCache
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(LBJobCache.class);
        //logger.setLevelToDebug();
    }

    /**
     * Static but VRSContext bound job cache. LBJobcache may be shared amongst
     * LB and WMS Resources, but must be separated in different VRSContext's.
     */
    private static Map<String, LBJobCache> lbJobCaches = new Hashtable<String, LBJobCache>();

    /**
     * Get VRSContext bound WMSJobCache
     * 
     * @param string
     */
    protected static LBJobCache createCache(VRSContext context, String hostname)
    {
        synchronized (lbJobCaches)
        {
            String idstr = "" + context.getID() + ":" + hostname.toLowerCase();
            LBJobCache cache = lbJobCaches.get(idstr);
            if (cache == null)
            {
                logger.debugPrintf(">>> NEW LBJOBCACHE for:%s <<<\n", hostname);
                cache = new LBJobCache(context, idstr, hostname);
                lbJobCaches.put(idstr, cache);
            }
            return cache;
        }
    }

    public static void stopAndDisposeAll()
    {
        synchronized (lbJobCaches)
        {
        	// create thread save Vector 
        	Vector<LBJobCache> lbcs=new Vector<LBJobCache>(); 
            for (LBJobCache cache : lbJobCaches.values())
            {
            	lbcs.add(cache); 
            }
            
            // loop over vector to avoid concurrency problems. 
            for (LBJobCache cache:lbcs)
            {
                cache.stopWatcher();
                cache.dispose();
            }
        }

    }

    /** Status Element */
    public class JobElement
    {
        private long lastUpdateTime = 0;

        private JobStatus jobStatus = null;

        private boolean queryRunning = false;

        private Object queryWaitMutex = new Object();

        /** Allow only one active thread to do the query */
        private long queryThreadID = -1;

        public JobElement(JobStatus status)
        {
            jobStatus = status;
            this.update();
        }

        public void update()
        {
            lastUpdateTime = System.currentTimeMillis();
        }

        public long getLastUpdateTime()
        {
            return this.lastUpdateTime;
        }

        public void updateStatus(JobStatus status)
        {
            this.jobStatus = status;
            this.update();
        }

        public JobStatus getStatus()
        {
            return (this.jobStatus);
        }

        /**
         * A Nill Object is be used as placeholder object in the cache while
         * fetching the Job Status.
         */
        public boolean isNill()
        {
            return (this.jobStatus == null);
        }

        protected void assertNotNill()
        {
            if (this.jobStatus == null)
                throw new Error("<<<NILL OBJECT>>>");
        }

        public String getJobId()
        {
            assertNotNill();

            return this.jobStatus.getJobId();
        }

        public java.net.URI getJobUri() throws URISyntaxException
        {
            assertNotNill();

            return new java.net.URI(this.jobStatus.getJobId());
        }

        public boolean hasWmsHostname(String wmsHostname) throws URISyntaxException
        {
            assertNotNill();

            if (wmsHostname == null)
                return false;

            return wmsHostname.equals(getWmsHostname());
        }

        public String getWmsHostname() throws URISyntaxException
        {
            assertNotNill();

            URI uri = this.getWmsUri();
            if (uri == null)
                return null;
            return uri.getHost();
        }

        public URI getWmsUri() throws URISyntaxException
        {
            assertNotNill();

            String wmsStr = this.jobStatus.getNetworkServer();
            if (wmsStr == null)
                return null;
            return new java.net.URI(wmsStr);
        }

        public boolean hasTerminated()
        {
            if (isNill())
                return false;

            return WMSUtil.hasTerminated(this.jobStatus.getState());
        }

        public boolean hasState(StatName state)
        {
            if (isNill())
                return false;

            return this.jobStatus.getState().equals(state);
        }

        public boolean hasStatus(String status)
        {
            if (this.jobStatus == null)
                throw new Error("Trying to fetch Status from NILL");

            return this.jobStatus.getState().getValue().equals(status);
        }

        public boolean isQueryRunning()
        {
            synchronized (this.queryWaitMutex)
            {
                return queryRunning;
            }
        }

        public void setQueryRunning(boolean value)
        {
            synchronized (this.queryWaitMutex)
            {
                if (value == true)
                {
                    if (this.queryRunning == true)
                        throw new Error("Duplicate Query Error. Query is already running");

                    if (this.queryThreadID >= 0)
                        throw new Error("Duplicate Thread Error. Current thread ID=" + this.queryThreadID);

                    this.queryThreadID = Thread.currentThread().getId();
                    this.queryRunning = value;
                }
                else
                {
                    if (this.queryThreadID != Thread.currentThread().getId())
                        throw new Error("Wrong Thread has released the Mutex!");

                    this.queryThreadID = -1;
                    this.queryRunning = value;

                    queryWaitMutex.notifyAll();
                }
            }

        }

        public void waitForQuery()
        {
            synchronized (this.queryWaitMutex)
            {
                // Possible if the status has changed inbetween!
                if (this.queryRunning == false)
                    return;

                try
                {
                    queryWaitMutex.wait(60000);
                }
                catch (InterruptedException e)
                {
                    handle("Interrupted while waiting for JobElement.queryWaitMutex", e);
                }
            }
        }

        public boolean needUpdate(int timeoutInMillis)
        {
            long current = System.currentTimeMillis();
            long time = lastUpdateTime;
            long delta = current - time;

            return (delta > timeoutInMillis);
        }

        public void setState(StatName state)
        {
            if (jobStatus != null)
                jobStatus.setState(state);
        }

        public void needsUpdate()
        {
            this.lastUpdateTime = 0;// will trigger update!
        }

    }

    // ========================================================================
    // Instance
    // ========================================================================

    // === Settings === //

    private int lbUserJobQueryUpdateWaitTime = 999000; // Default (LB) query
                                                       // update

    // after 60 seconds.
    private long lbUserJobQueryLastUpdateTime = 0;

    private int jobStatusUpdateWaitTime = 60000; // Default status update after

    // === Cache+Mappings === //

    /**
     * Job Key (string) to JobStatus hash. Map is LinkedHashMap so that the
     * order is kept in which jobs are added
     */
    private Map<String, JobElement> jobs = new LinkedHashMap<String, JobElement>();

    /**
     * Index Hash: WMS Hostname to Job Key vector hash. Jobs are added to
     * hashtable which maps a WMS Hostname to an array of Job Keys. Set is
     * LinkedHashMap to keep the order in which the keys are added!
     */
    private Map<String, Set<String>> wmsJobs = new Hashtable<String, Set<String>>();

    private Vector<JobStatusListener> jobListeners = new Vector<JobStatusListener>();

    private String _lbHostname = null;
    
    private LBJobCacheWatcher watcher = null;

    private String idstr;

    // === Cached fields === */

    private LBClient _lbClient = null;

    private VRSContext vrsContext;

    // ========================================================================
    // Constuctor/Initializors
    // ========================================================================

    /** Protected Constructor */
    protected LBJobCache(VRSContext context, String idstr, String hostname)
    {
        this.vrsContext = context;
        this._lbHostname = hostname;
        this.idstr = idstr;
        startWatcher();
    }

    public String getID()
    {
        return idstr;
    }

    protected synchronized void startWatcher()
    {
        // / (re)start only of new or the old one died.
        if ((this.watcher == null) || (watcher.isAlive() == false))
        {
            this.watcher = new LBJobCacheWatcher(this);
            this.watcher.startWatcher();
        }
    }

    protected synchronized void stopWatcher()
    {
        // / (re)start only of new or the old one died.
        if ((this.watcher == null) || (watcher.isAlive() == false))
        {
            return;
        }
        this.watcher.stopWatcher();

    }

    /**
     * Stop threads, cleanup and unregister. In theory this object could be
     * reused after a dispose, but it is not recommended
     */
    protected void dispose()
    {
        logger.debugPrintf(">>> DISPOSING WMSJobCache:%s <<<\n", idstr);

        if (this.watcher != null)
        {
            this.watcher.stopWatcher();
            this.watcher.disposeWatcher();
            this.watcher = null;
        }

        synchronized (lbJobCaches)
        {
            lbJobCaches.remove(this.getID());
        }

        // speed up garbage collection;
        this.jobListeners.clear();
        this.jobs.clear();
        this.wmsJobs.clear();
    }

    /** Return the wait time before a job status will be updated. */
    public int getJobStatusUpdateTime()
    {
        return this.jobStatusUpdateWaitTime;
    }

    public long getLBQueryWaitTime()
    {
        return this.lbUserJobQueryUpdateWaitTime;
    }

    public String getHostname()
    {
        return _lbHostname;
    }
    
    // ========================================================================
    // Public Interface Methods
    // ========================================================================

    /**
     * Return iterator which can be used to iterate of the JobID KeySet. Using
     * this keySet Iterator should be thread safe. This has not been tested.
     */
    public Iterator<String> getJobIDIterator()
    {
        return this.jobs.keySet().iterator();
    }

    /**
     * Return jobIds from cache for the LB.
     */
    public List<String> getCachedJobIDs()
    {
        // if the mapping is correctly updates: this should contain
        // the jobis for this hostname:

        synchronized (this.jobs)
        {
            Set<String> keys = this.jobs.keySet();
            return new StringList(keys);
        }
    }

    /**
     * Returns status directly from cache.
     */
    public JobStatus getCachedJobStatus(String jobid)
    {
        // if the mapping is correctly updates: this should contain
        // the jobis for this hostname:

        synchronized (this.jobs)
        {
            JobElement jobEl = this.jobs.get(jobid);

            if ((jobEl == null) || (jobEl.isNill()))
                return null;

            return jobEl.getStatus();
        }
    }

    // ========================================================================
    // Update Query methods
    // ========================================================================

    /**
     * Asynchronous method. Returns Directly: true if query already finished,
     * false if it is still running or hasn' finished
     */
    public boolean queryUserJobs(ITaskMonitor monitor, boolean fullQuery) throws VlException
    {
        // synchronized schedule:
        return this._queryUserJobs(monitor, fullQuery);
    }

    // ========================================================================
    // Update Query methods
    // ========================================================================

    private Object _updateUserJobsMutex = new Object();

    private boolean _updateUserJobsRunning = false;

    /** Wait until current query has finished */
    public void waitForQueryUserJobs()
    {
        long tid = Thread.currentThread().getId();
        // debug("waitForUpdateUserJobs: (I) Entering for Thread #" + tid);

        synchronized (_updateUserJobsMutex)
        {
            if (_updateUserJobsRunning == false)
            {
                // debug("waitForUpdateUserJobs: (II) no query running: Leaving for Thread #"
                // + tid);
                return;
            }
            else
            {
                try
                {
                    // debug("waitForUpdateUserJobs: (III) Waiting for Thread #"
                    // + tid);
                    _updateUserJobsMutex.wait(60000); // Max User Job Wait Time
                    // !
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // debug("waitForUpdateUserJobs: (IV) Wakeup for Thread #" +
                // tid);
            }
        }
    }

    private ActionTask _updateUserJobsTask = null;

    /**
     * Requery userjobs and their statuses. If fullquery=false, will only update
     * statusses if the cache time has expired. If fullqurey=true, all user jobs
     * will be queried again.
     * 
     * @param monitor
     * @param fullQuery
     * @return
     * @throws VlException
     */
    protected boolean _queryUserJobs(final ITaskMonitor monitor, final boolean fullQuery) throws VlException
    {
        // Only one thread may enter:
        synchronized (_updateUserJobsMutex)
        {
            if (_updateUserJobsRunning == true)
            {
                logger.debugPrintf("\n>>>\n[#%d]:_queryUserJobs() for Query already running for:%s\n<<<\n", Thread
                        .currentThread().getId(), getHostname());

                return false;
            }

            logger.debugPrintf("\n>>>\n[#%d]:_queryUserJobs(): Starting new query for:%s\n<<<\n", Thread
                    .currentThread().getId(), getHostname());

            // block!
            _updateUserJobsRunning = true;
        }

        // ===
        // PAR:
        // ===
        final LBClient lbClient = this.getLBClient();

        this._updateUserJobsTask = new ActionTask(this.watcher, "Quering LB Service:" + lbClient)
        {
            public void doTask()
            {
                try
                {
                    _updateUserJobsFrom(monitor, lbClient, fullQuery);
                }
                finally
                {
                    synchronized (_updateUserJobsMutex)
                    {
                        _updateUserJobsRunning = false;
                        _updateUserJobsMutex.notifyAll();
                    }
                }
            }

            public void stopTask()
            {

            }
        };

        _updateUserJobsTask.startTask();

        return false;
    }

    private void _updateUserJobsFrom(ITaskMonitor monitor, LBClient lbClient, boolean fullQuery)
    {
        JobStatus[] lbjobs = null;
        String taskstr = null;
        taskstr = "JobCache: Quering LB Service:" + lbClient+" (T#[" + Thread.currentThread().getId() + "])";

        String lbstr="LB:"+lbClient.getHostname()+":"+lbClient.getPort()+":(T#"+ Thread.currentThread().getId()+"]):"; 
        
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lbUserJobQueryLastUpdateTime;

        // debug(taskstr + " >>> Checking LBClient:" + lbClient);
        // debug(taskstr + " - last query time =" +
        // lbUserJobQueryLastUpdateTime);
        // debug(taskstr + "  - current time    =" + currentTime);
        
        if ((fullQuery == false) && (delta < this.lbUserJobQueryUpdateWaitTime))
        {
            monitor.endSubTask(taskstr);
            monitor.logPrintf("%s: - using cache for LB Service:%s\n",lbstr,lbClient);
            // debug(taskstr + " -> Skipping already queried LB Client:" +
            // lbClient);
            return;
        }

        monitor.startSubTask(taskstr, 1);
        monitor.logPrintf("%s\n", taskstr);

        try
        {
            // AssertOneThread("updateUserJobsFrom:"+lbClient);
            // logger.infoPrintf("Performing NEW User Jobs Query for LBClient:%s\n",lbClient);
            // debug(taskstr + " -> Fetching NEW jobs.");

            lbjobs = lbClient.getUserJobStatusses();
            lbUserJobQueryLastUpdateTime = System.currentTimeMillis();// Done!

            String numstr = ""+(lbjobs != null ? lbjobs.length : "0");
            // debug(taskstr + logStr );
            monitor.logPrintf("%s: -> got #%s user jobs\n",lbstr,numstr);
            monitor.endSubTask(lbstr);
            // info("Finished User Jobs Query for LBClient:" + lbClient);

        }
        catch (Exception e)
        {
            // Wrap exception
            WMSException wmsx = WMSUtil.convertException("Failed to query LB Server:" + lbClient, e);

            //
            // Update Query time also in the case of an error
            // to prevent re querying each time
            // 
            lbUserJobQueryLastUpdateTime = System.currentTimeMillis();
            monitor.endSubTask(taskstr);
            
            String txt=""+wmsx.getFaultDescription();
            if (txt.contains("matching jobs found but authorization failed"))
            {
                // Ignore. No jobs at current LB Resource. 
                monitor.logPrintf("%s: Failed to query user jobs\n",lbstr); 
                monitor.logPrintf("- Reason='%s'\n",txt);
                logger.warnPrintf("%s: Failed to query user jobs. Reason=%s\n",lbstr,txt); 
            }
            else
            {
                monitor.logPrintf("%s: - *** Exception when querying LB Service ***\n",lbstr);
                monitor.logPrintf("*** Exception Message ***\n" + wmsx.getMessage() + "\n***\n");
                monitor.setException(wmsx);
                handle(taskstr + ": Exception when getting user jobs from:" + lbClient, wmsx);
                addException(wmsx);
               
            }
            
            return;
        }

        // ===
        // POST: register jobs
        // ===
        Vector<String> jobIds = new Vector<String>();

        if ((lbjobs == null) || (lbjobs.length == 0))
            return;

        for (int j = 0; j < lbjobs.length; j++)
        {
            // Robust programming
            if (lbjobs[j] == null)
                continue;

            _queryJobStatus(lbjobs[j], false); // Don't fire now
            jobIds.add(lbjobs[j].getJobId());
        }

        // Fire now all at once (to spare the VBrowser to many updates)

        fireUpdateUserJobs(jobIds);
    }

    Vector<Exception> exceptions = new Vector<Exception>();

    protected void addException(Exception e)
    {
        synchronized (exceptions)
        {
            exceptions.add(e);
        }
        if (exceptions.size() > 100)
            popException();
    }

    /** Get One Exception from exception stack and return it */
    public Exception popException()
    {
        synchronized (exceptions)
        {
            Exception e;
            if (exceptions.size() <= 0)
                return null;

            e = exceptions.get(0);

            exceptions.remove(0);
            return e;
        }

    }

    /**
     * Update all statusses in the job cache if cache time has expired. Use
     * fullUpdate to update all status diregarding cache time.
     * 
     */
    public boolean queryJobStatuses(ITaskMonitor monitor, String optWmsHostname, boolean fullUpdate)
    {
        // synchronized schedule:
        return _queryJobStatuses(monitor, optWmsHostname, fullUpdate);
    }

    private Object _updateJobStatusesMutex = new Object();

    private boolean _updateJobStatusesRunning = false;

    private boolean _queryJobStatuses(ITaskMonitor monitor, String optWmsHostname, boolean fullUpdate)
    {
        String taskstr = "Updating Job Statuses\n";
        monitor.startSubTask(taskstr, 1);
        monitor.logPrintf(taskstr + "\n");

        // only one thread may enter:

        synchronized (this._updateJobStatusesMutex)
        {
            if (this._updateJobStatusesRunning == true)
            {
                monitor.endSubTask(taskstr);
                monitor.logPrintf("Background task already running.\n");

                // debug("\n>>>\n>>>_updateJobStatuses(): Query already running! <<<\n<<<");
                return false;
            }

            // debug("\n>>>\n>>> _updateJobStatuses(): Starting Query <<<\n<<<");

            // block further threads:
            this._updateJobStatusesRunning = true;
        }

        try
        {
            // Use multi thread save iterator.
            int terminated = 0;
            int updated = 0;
            int total = 0;

            Iterator<String> iterator = this.getJobIDIterator();

            while (iterator.hasNext())
            {
                String jobid = iterator.next();
                JobElement job = this.jobs.get(jobid);

                // filter for specified WMS Hostname !
                if (optWmsHostname != null)
                {
                    if (job.hasWmsHostname(optWmsHostname) == false)
                    {
                        // debug("_updateJobStatuses() (Ib) not for this (specified) WMS host:"
                        // + optWmsHostname + " <> "
                        // + job.getWmsHostname());
                        continue;
                    }
                }

                total++;

                // Is also checked in updateJobStatus, but for statistics
                // purposes
                // check here and skip.

                if (job.hasTerminated())
                {
                    terminated++;
                    // debug("queryJobStatus() (Ib) Job already terminated:" +
                    // jobid);
                    continue;
                }

                // really update!
                try
                {
                    // debug("_updateJobStatuses() (II) updateing status for:" +
                    // jobid);
                    this.queryJobStatus(jobid, fullUpdate);
                    updated++;
                }
                catch (Throwable e)
                {
                    handle("_updateJobStatuses(_) (III): Couldn't update status of:" + jobid, e);
                }

            }
            String txt = "Updated #" + updated + " out of #" + total + " (#" + terminated + " terminated) jobs.";
            // debug("_updateJobStatuses()" + txt);
            monitor.logPrintf(txt);
            return true;
        }
        catch (Throwable t)
        {
            this.handle("Couldn't update _udateJobStatuses()", t);
            return false;
        }
        finally
        {
            monitor.endSubTask(taskstr);
            monitor.logPrintf("Finished updating statuses.\n");

            synchronized (this._updateJobStatusesMutex)
            {
                // unblock
                this._updateJobStatusesRunning = false;
            }
        }
    }

    // Update or Put new JobStatus into cache
    private boolean _queryJobStatus(JobStatus jobStatus, boolean fireEvent)
    {
        if (jobStatus == null)
            return false;

        try
        {
            boolean newStatus = false;
            boolean newJob = false;

            String jobid = jobStatus.getJobId();

            synchronized (this.jobs)
            {
                JobElement jobEl = this.jobs.get(jobid);

                if (jobEl == null)
                {
                    // put in cache
                    jobEl = new JobElement(jobStatus);
                    this.jobs.put(jobid, jobEl);
                    newStatus = true;
                    newJob = true;
                }
                else
                {
                    // keep sync time as short as possible:
                    synchronized (jobEl)
                    {
                        if (jobEl.isNill())
                        {
                            newStatus = true;
                            newJob = true;
                        }
                        else
                        {
                            JobStatus prevStat = jobEl.jobStatus;
                            // check new status:
                            newStatus = (prevStat.getState() != jobStatus.getState());
                        }

                        // update:
                        jobEl.updateStatus(jobStatus);
                    }
                }
            }

            // get/parse WMShost

            String wmsService = jobStatus.getNetworkServer();
            String wmsHost = "";
            if ((wmsService != null) && (wmsService.equals("") == false))
            {
                java.net.URI wmsUri = new URI(wmsService);
                wmsHost = wmsUri.getHost();
            }

            // *** Concurrency Note:
            // call addWMSMapping OUTSIDE above 'synchronized' code to avoid
            // deadlock
            // Better not lock two mutices at the same time.

            addWMSMapping(wmsHost, jobid);

            // *** Concurrency Note:
            // fire events outside synchronized statements!
            if (fireEvent)
            {
                if (newJob == true)
                    fireNewJob(jobid);

                // old job new status:
                if ((newJob == false) && (newStatus == true))
                    fireStatusChanged(jobStatus);
            }

            return newStatus;
        }
        catch (Exception e)
        {
            handle("Couldn't add new job to cache:", e);
        }
        return false;
    }

    /**
     * If a WMSHost for a Job is know, added to the internal hashmap.
     */
    private void addWMSMapping(String wmsHost, String jobid)
    {
        // debug("addWMSMapping() " + wmsHost + " -> " + jobid);

        synchronized (this.wmsJobs)
        {
            // Get Job Key set for WMS Host
            Set<String> jobV = this.wmsJobs.get(wmsHost);

            if (jobV == null)
            {
                // debug("addWMSMapping() Creating new JobID Set for wmshost:" +
                // wmsHost);
                // new WMS to Job Mapping set
                jobV = new LinkedHashSet<String>();
                this.wmsJobs.put(wmsHost, jobV);
            }
            // add without LinkedHashSet does checking
            jobV.add(jobid);
        }
    }

    /**
     * Create new or return cached LBClient.
     */
    protected LBClient getLBClient() throws VlException
    {
    	try
    	{
    		if (this._lbClient == null)
    		{
    			_lbClient=WMSUtil.createLBClient(getHostname(), 
    					WMLBConfig.LB_DEFAULT_PORT,
    					this.vrsContext.getGridProxy().getProxyFilename()); 
    		}
    		return _lbClient; 
    	}
    	catch (Exception e)
    	{
    		throw new VlException("LBException", "Couldn't create LBClient for host:" + getHostname(), e);
    	}
    	
    }

    /**
     * Main method to query for a Job Status. Optionally returns cached value or
     * performs a Job Status query. If fullUpdate==true or the job status is not
     * in the cache, the status will be queried.
     */
    public JobStatus queryJobStatus(String jobId, boolean fullUpdate) throws VlException
    {
    	logger.debugPrintf(">>>ENTER:queryJobStatus for:%s\n",jobId); 
    	
        JobElement jobEl = null;

        synchronized (this.jobs)
        {
            // check cached jobId:
            jobEl = this.jobs.get(jobId);

            // place nill object to use as Query Mutex for this job Id !
            if (jobEl == null)
            {
                // info("queryJobStatus() Registering NEW Job:" + jobId);
                // debug("queryJobStatus() Not In Cache. Starting Query for:" +
                // jobId);

                jobEl = new JobElement(null); // NILL Object !
                this.jobs.put(jobId, jobEl);
            }
        }

        // ===
        // Synchronize around JobElement to avoid multiple threads performing a
        // query
        // for the same job id.
        // ===

        // First check cached status: Sync Time should be short enough

        synchronized (jobEl)
        {
            if (jobEl.isNill() == false)
            {
                // Only update status if not terminated
                // (aborted,finished,cancelled)
                if (jobEl.hasTerminated())
                {
                    // debug("queryJobStatus() Job already terminated:" +
                    // jobId);
                    return jobEl.jobStatus;
                }

                if (fullUpdate == false)
                {
                    // debug("queryJobStatus() Returning cached status of:" +
                    // jobId);
                    return jobEl.jobStatus;
                }
                else
                {
                    // Micro cache time: Do not update if (new) status is only 1
                    // second old!
                    if (jobEl.needUpdate(1000) == false)
                    {
                        // debug("queryJobStatus() Current status less then 1s old! Returning cached status of:"
                        // + jobId);
                        return jobEl.jobStatus;
                    }
                }
            }
        }

        // =================================
        // Must Update Status:
        // unlocked: Threads can gather here
        // =================================

        boolean doQuery = false; // only one thread can claim this

        // keep sync'd code as short as possible:
        synchronized (jobEl)
        {
            if (jobEl.isQueryRunning() == false)
            {
                doQuery = true;

                // Update Status INSIDE sync'd code to block other threads!
                jobEl.setQueryRunning(true);
            }
        }

        Throwable ex = null;

        // =====================================
        // Let only one thread peform the query!
        // =====================================

        if (doQuery == false)
        {
            // debug("queryJobStatus() HALT. Query already runnnig. Waiting for:"
            // + jobId);
            // this thread must wait:
            jobEl.waitForQuery();
            // debug("queryJobStatus() DONE. Waiting for query:" + jobId);
        }
        else
        {
            // one thread should enter here:
            try
            {
                // info("queryJobStatus() Performing JobStatus Query for:" +
                // jobId);

                java.net.URI jobUri = new URI(jobId);
                String lbHost = jobUri.getHost();
                LBClient lbclient = getLBClient();

                JobStatus status = lbclient.getStatus(jobUri);
                _queryJobStatus(status, true);

                info("queryJobStatus() FINISHED JobStatus Query for  :" + jobId);
            }
            catch (Throwable e)
            {
                ex = WMSUtil.convertException("Failed to query Job Status for:" + jobId, e);
            }
            finally
            {
                // RELEASE!
                synchronized (jobEl)
                {
                    jobEl.setQueryRunning(false);
                }
            } // try

            if (ex != null)
            {
                // debug("queryJobStatus() ***Exception for:" + jobId);

                if (ex instanceof URISyntaxException)
                    throw new nl.uva.vlet.exception.VRLSyntaxException("Wrong job URI:" + jobId,
                            (URISyntaxException) ex);

                throw new VlException("LBClientException", "Couldn't query job status:" + jobId, ex);
            }
        }

        return jobEl.getStatus();
    }

    protected void registerNewJob(String jobid)
    {
        try
        {
            // Query new UNREGISTERED status!
            this.queryJobStatus(jobid, false);
        }
        catch (VlException e)
        {
            handle("Register New Job:" + jobid, e);
        }
    }

    /** Call by Update to check and update all */
    public void updateAll(ITaskMonitor monitor)
    {
        // debug("***  Updating job statusses ***");
        this.queryJobStatuses(monitor, null, false);
    }

    // ========================================================================
    // Actual Private Query methods
    // ========================================================================

    // ========================================================================
    // Events/Listeners
    // ========================================================================

    public void addJobListener(JobStatusListener listener)
    {
        synchronized (this.jobListeners)
        {
            if (this.jobListeners.contains(listener) == false)
                this.jobListeners.add(listener);
        }
    }

    public void removeJobListener(JobStatusListener listener)
    {
        synchronized (this.jobListeners)
        {
            this.jobListeners.remove(listener);
        }
    }

    protected void fireStatusChanged(JobStatus jobStatus)
    {
        this.watcher.fireEvent(JobEvent.createStatusEvent(jobStatus));
    }

    protected void fireUpdateUserJobs(List<String> userJobs)
    {
        String jobarr[] = new String[userJobs.size()];
        jobarr = userJobs.toArray(jobarr);

        this.watcher.fireEvent(JobEvent.createUpdateUserJobs(jobarr));
    }

    protected JobStatusListener[] getJobListenersArray()
    {
        JobStatusListener lArr[] = null;

        // private copy:
        synchronized (this.jobListeners)
        {
            lArr = new JobStatusListener[this.jobListeners.size()];
            lArr = this.jobListeners.toArray(lArr);
        }

        return lArr;
    }

    protected void fireNewJob(String jobid)
    {
        this.watcher.fireEvent(JobEvent.createNewJobEvent(jobid));
    }

    // ========================================================================
    // Misc/Debugging/etc/
    // ========================================================================

    // Asynchronous Exception handling:
    private void handle(String msg, Throwable e)
    {
        if (msg == null)
            msg = "*** Exception ***";

        logger.logException(ClassLogger.ERROR,e,"LB:%s (T#%d):%s\n",
                getHostname(),
                Thread.currentThread().getId(),
                msg); 
    }
   
    private void info(String msg, Object... args)
    {
        logger.infoPrintf(":" + getHostname() + "#" + Thread.currentThread().getId() + ":" + msg + "\n", args);
    }
 
    /** Mark job as purged */
    public boolean jobPurged(String jobId)
    {
        JobElement jobEl = this.jobs.get(jobId);
        if ((jobEl == null) || (jobEl.isNill()))
            return false;
        info("Set status to PURGED for:" + jobId);
        // update manually !
        jobEl.setState(StatName.PURGED);
        jobEl.needsUpdate(); // trigger refetch at later stage!
        this.fireStatusChanged(jobEl.getStatus());

        return true;
    }

}
