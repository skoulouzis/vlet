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
 * $Id: LBJobCacheWatcher.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.tasks.ITaskSource;

public class LBJobCacheWatcher implements ITaskSource
{
    // =======================================================================
    // Class
    // =======================================================================

    private static Vector<LBJobCacheWatcher> watchers = new Vector<LBJobCacheWatcher>();
    private static ClassLogger logger; 
    
    static
    {
    	logger=ClassLogger.getLogger(LBJobCacheWatcher.class);
    	//logger.setLevelToDebug();
    }
    
    protected static void register(LBJobCacheWatcher w)
    {
        synchronized (watchers)
        {
            if (watchers.contains(w))
            {
                logger.warnPrintf("*** Warning: watcher already registerd for:%s\n" + w.getID());
            }

            watchers.add(w);
        }
    }

    protected static void unregister(LBJobCacheWatcher w)
    {
        synchronized (watchers)
        {
            watchers.remove(w);
        }
    }

    protected static void stopAndDisposeAll()
    {
        synchronized (watchers)
        {
            for (LBJobCacheWatcher w : watchers)
            {
                if (w.isAlive())
                    w.stopWatcher();
            }

            watchers.clear();
        }
    }

    // =======================================================================
    // Instance
    // =======================================================================

    ActionTask watcherTask;

    private LBJobCache lbCache;

    private boolean hasTasks = false;

    private Vector<JobEvent> jobEvents = new Vector<JobEvent>();

    public LBJobCacheWatcher(LBJobCache lbJobCache)
    {
        this.lbCache = lbJobCache;
        register(this);
    }

    public String getID()
    {
        return "lbwatcher:" + lbCache.getID();
    }

    public void startWatcher()
    {

        // theTask = new ActionTask(VFS.getTaskWatcher(),"The TransferTask")
        watcherTask = new ActionTask(this, "JobCacheWatcher task for LBJobCache:" + this.lbCache.getID())
        {
            boolean mustStop = false;

            public void doTask()
            {
                ITaskMonitor monitor = this.getTaskMonitor();
                try
                {
                    logger.debugPrintf(" The task has been started\n");

                    while (mustStop == false)
                    {
                        updateJobs(monitor);

                        try
                        {
                            // wait
                            Thread.sleep(lbCache.getJobStatusUpdateTime());
                        }
                        catch (InterruptedException e)
                        {
                            logger.logException(ClassLogger.WARN,e,">>> Interupted <<<\n");
                        }
                    }
                }
                catch (Exception e)
                {
                    handle(e);
                }

                logger.debugPrintf("LBJobCacheWatcher has stopped:%s\n",getID());
            }

            @Override
            public void stopTask()
            {
            	logger.debugPrintf("Stopping LBJobCacheWatcher:%s\n",getID());
                mustStop = true;
            }
        };

        watcherTask.startTask();

        this.startNotifier();
    }

    private void updateJobs(ITaskMonitor monitor) throws VlException
    {
        // schedule update:
        lbCache.updateAll(monitor);
    }

    public boolean isAlive()
    {
        if ((this.watcherTask != null) && (this.watcherTask.isAlive()))
            return true;

        return false;
    }

    public void stopWatcher()
    {
        if (this.watcherTask != null)
            this.watcherTask.signalTerminate();
    }

    /** Stop threads, unregister and cleanup */
    public void disposeWatcher()
    {
    	logger.debugPrintf(">>> DISPOSING LBJobCacheWatcher for #%s\n",this.lbCache.getID());
        // Call All background tasks!!!
        ActionTask.stopActionsFor(this, false);

        // stop and dispose tasks,etc.
        this.stopWatcher();
        if (this.notifierTask != null)
            this.notifierTask.signalTerminate();
        this.notifierTask = null;
        this.watcherTask = null;
        unregister(this);
    }

    @Override
    public void messagePrintln(String msg)
    {
        logger.debugPrintf("Message:%s\n",msg);
        Global.infoPrintf(this, msg);
    }

    @Override
    public void setHasTasks(boolean val)
    {
        logger.debugPrintf("Has tasks=%s\n","" + val);
        this.hasTasks = val;
    }

    public void fireEvent(JobEvent event)
    {
        synchronized (this.jobEvents)
        {
            jobEvents.add(event);
            // signal wakeup!
            jobEvents.notifyAll();
        }

        checkNotifier();
    }

    /** Private Event notifier for asynchronous updates. */
    private ActionTask notifierTask = null;

    private void startNotifier()
    {
        if ((this.notifierTask != null) && (notifierTask.isAlive()))
        {
            warnPrintf("Notifier task already running. Will start duplicate notifier!\n");
            notifierTask.stopTask();// Set current to stop but do not signal!
            // start new one anyway. JobEvents Vector is synchronized.
        }

        ActionTask task = new ActionTask(this, "Job Event Notifier")
        {
            boolean mustStop = false;

            boolean notifying = false;

            private long notifyStartTime = -1;

            public void doTask()
            {
                while (mustStop == false)
                {
                    JobEvent event = null;

                    synchronized (jobEvents)
                    {
                        // fetch first in list:
                        if (jobEvents.size() > 0)
                        {
                            event = jobEvents.elementAt(0);
                            jobEvents.remove(0);
                        }
                    }

                    if (event == null)
                    {
                        logger.debugPrintf("--- JobNotifier No Events ---\n");

                        try
                        {
                            synchronized (jobEvents)
                            {
                                // wait, will receive notify from schedule
                                // method
                                jobEvents.wait(60000);
                            }

                            logger.debugPrintf("--- JobNotifier Wakeup ---\n");
                        }
                        catch (InterruptedException e)
                        {
                        	// warning. 
                            errorPrintf(">>> Interrupted <<<\n"); 
                        }

                    }
                    else
                    {
                    	logger.debugPrintf("Notifying event:%s\n",event);

                        notifying = true;
                        notifyStartTime = System.currentTimeMillis();

                        try
                        {
                            notifyEvent(event);
                        }
                        finally
                        {
                            notifying = false;
                        }

                    }
                }

                warnPrintf("--- JobNotifier STOPPED ---\n");
            }

            @Override
            public void stopTask()
            {
                this.mustStop = true;
                // action task will send 'interrupt' signal!
                // jobEvents.notifyAll();
            }
        };

        // Start new Task
        this.notifierTask = task;
        this.notifierTask.startTask();

    }

    private void notifyEvent(JobEvent event)
    {
        JobStatusListener[] listeners = this.lbCache.getJobListenersArray();

        if (listeners == null)
            return;

        for (JobStatusListener listener : listeners)
        {
            try
            {
                listener.notifyJobEvent(event);
            }
            catch (Throwable t)
            {
                handle("*** Exception while notifying event:" + event, t);
            }
        }
    }

    private void checkNotifier()
    {
        if ((this.notifierTask == null) || (notifierTask.isAlive() == false))
        {
            warnPrintf("Notifier is dead. Starting new one.\n");
            this.startNotifier();
        }

        // check time ?

    }

    // =============
    // Miscellaneous
    // =============

    private void warnPrintf(String msg, Object... args)
    {
    	logger.warnPrintf(msg, args);
    }

    private void errorPrintf(String msg, Object... args)
    {
    	logger.errorPrintf(msg, args);
    }

    private void handle(String msg, Throwable e)
    {
    	logger.logException(ClassLogger.ERROR,e,"%s\n", msg);
    }

    public void handle(Exception e)
    {
    	logger.logException(ClassLogger.ERROR,e,"Exception:%s\n", e);
    }

}
