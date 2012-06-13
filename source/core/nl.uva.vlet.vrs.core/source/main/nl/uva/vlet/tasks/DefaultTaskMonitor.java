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
 * $Id: DefaultTaskMonitor.java,v 1.13 2011-04-18 12:00:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:34 $
 */ 
// source: 

package nl.uva.vlet.tasks;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlInterruptedException;


/** 
 * Default Implementation of ITaskMonitor. 
 */ 
public class DefaultTaskMonitor implements ITaskMonitor, ISubTaskMonitor
{
    private static int instanceCounter = 0;

    // ==== // 
    Vector<ITaskMonitorListener> listeners=new Vector<ITaskMonitorListener>();
    
    private ITaskMonitor parent;    
    
    private Vector<ITaskMonitor> subMonitors=new Vector<ITaskMonitor>(); 
        
    private boolean mustStop = false; // for pre-emptive abortion

    private final int id = instanceCounter++;

    private Throwable exception = null;

    private boolean isDone = false;

    protected TaskLogger taskLogger; 
    // ===
    // Total Task Time
    // ===

    private long startTime = 0;
    private long stopTime = 0;

    // -1 is unknown/not set
    private long totalWorkTodo = -1; // -1 = unknown
    private long totalWorkDone = 0; // for more then 1 file/directory
    private long totalWorkDoneLastUpdateTime = 0;
    private String taskName=null; 
    
    // ===
    // subtask
    // ===
    private int subTaskLevel=0; //
    private String currentSubTask=null; 
    private boolean logSubTask = false;
    private long currentSubTaskTodo = -1; // 'current' file, -1=unknown !
    private long currentSubTaskDone = 0; // 'current' file,-1=unknown !
    private long currentSubTaskStartTime = 0;
    private long currentSubTaskDoneLastUpdateTime = 0;

    public DefaultTaskMonitor()
    {
        parent=null; 
        init();                
    }
 
    /** Create new TaskMonitor and add to parent Hierarchy */ 
    public DefaultTaskMonitor(ITaskMonitor parent)
    {
        init();
        setParent(parent); 
    }
    
    private void init()
    {
        logSubTask=Global.getLogger().isLevelDebug();
        // Send all output to the Appendable object sb
        this.taskLogger=new TaskLogger("TaskLogger"); 

    }
 
    public String getTask()
    {
        return taskName;
    }
    
    public void addSubMonitor(ITaskMonitor monitor)
    {
        synchronized(subMonitors)
        {
            if (subMonitors.contains(monitor)==false)
                subMonitors.add(monitor); 
        }
        
        fireMonitorAdded(monitor); 
    }
    
    public void removeSubMonitor(ITaskMonitor monitor)
    {
        synchronized(subMonitors)
        {
            subMonitors.remove(monitor);  
        }
        
        fireMonitorRemoved(monitor); 
    }
    
    /** Returns submonitor list in new allocated array */ 
    public ITaskMonitor[] getSubMonitors()
    {
        ITaskMonitor monitors[]; 
        
        synchronized(subMonitors)
        {
            monitors=new ITaskMonitor[subMonitors.size()]; 
            monitors=subMonitors.toArray(monitors); 
        }
        
        return monitors; 
    }
    
    protected void setParent(ITaskMonitor monitor)
    {
        this.parent=monitor;
        if (parent==null)
            return; 
        parent.addSubMonitor(this); 
    }

    /** 
     * Store system time when the transfer started. 
     * Assertion: Can only be called once during the TaskMonitors lifetime ! 
     */
    protected void markStarted()
    {
        if (startTime>0) 
        {
              //throw new CausalityError("Mark Error: markStarted() called twice !");
              Global.warnPrintf(this,"Warning: markStarted() called again!\n");
        }
     
        // mark start
        this.startTime = System.currentTimeMillis();
        // initialize update time: 
        this.totalWorkDoneLastUpdateTime = System.currentTimeMillis();
    }

    /** stop system time when the transfer stopped. */
    protected void markStopped()
    {
        if (stopTime>0) 
              Global.warnPrintf(this,"Mark Error: markStopped() called again!\n"); 
        this.stopTime = System.currentTimeMillis();
    }

    /**
     * @return Returns the exception.
     */
    public Throwable getException()
    {
        return exception;
    }

    /**
     * @return Returns transfer id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return Returns whether the transfer is done. 
     * If an error occurred or the task was canceled isDone() will return true as well. 
     */
    public boolean isDone()
    {
        return isDone;
    }

    /** @see #endTask(String) */
    public void endTask()
    {
        endTask(null);
    }

    /**
     * Set Transfer status to done. Make sure all the status information has
     * been updated (like setResultNode), since this method will wake up waiting
     * threads !
     */
    public void endTask(String taskName)
    {
        logPrintf("Ended:%s\n",taskName);
        this.isDone = true;
        markStopped();
        //this.updateWorkDone(this.totalWorkTodo);
        
        synchronized (waitMutex)
        {
            waitMutex.notifyAll();
        }
    }
    
    /** Whether an exception has occurred during the file transfer. */ 
    public boolean hasError()
    {
        return (getException() != null);
    }

    /**
     * @param exception set last occurred exception. 
     */
    public void setException(Throwable e)
    {
    	//logText += "*** Exception:" + e.toString() + "\n" + "--- message ---\n"
        //        + e.getMessage() + "\n";

        this.exception = e;
    }

    /**
     * Get Total Progress. <br>
     * - return value &lt; 0.0 is unknown.<br>
     * - return value == 0.0 is just started.<br>
     * - return value (&gt;)= 1.0 is finished.<br
     * 
     * @return
     */
    public double getProgress()
    {
        if (this.totalWorkTodo==0)
            return -1.0; 
        return (double) this.totalWorkDone / (double) this.totalWorkTodo;
    }

    /**
     * Returns current task time time in millis or total time when task is done
     */
    public long getTime()
    {
        if (isDone())
        {
            return this.stopTime - this.startTime;
        }
        else
        {
            return System.currentTimeMillis() - this.startTime;
        }
    }

    /**
     * Set transfer size of current transfer. Will also update
     * currentTransferSize to current as method implies as a new transfer.
     */
    public void startSubTask(String subTaskName, long size)
    {
        subTaskLevel++; 
        this.currentSubTask = subTaskName;
        this.currentSubTaskStartTime = System.currentTimeMillis();
        this.currentSubTaskTodo = size;
        this.currentSubTaskDone = 0;

        if (logSubTask)
            logPrintf("[startSubTask:" + subTaskName + " (todo=" + size + ")]\n");
    }

    /**
     * Set transfer size of current transfer
     */
    protected void setTotalWorkTodo(long size)
    {
        this.totalWorkTodo = size;
        //this.totalWorkDone =0 ; // todo is known set done to 0; 
    }
    
    /**
     * Set transfer size of current transfer
     */
    protected void setSubTaskTodo(long size)
    {
        this.currentSubTaskTodo = size;
    }
    
    protected void setTotalWorkDone(long size)
    {
        this.totalWorkDone =size;  
    }

    /**
     * Set number of steps in subtask (or nr of bytes already transferred in current transfer) 
     */
    public void updateSubTaskDone(long size)
    {
        // buggy update: same size is reported
        // do not set time stamp to avoid 'degrading' of transfer speed

        if (size == currentSubTaskDone)
            return;

        this.currentSubTaskDoneLastUpdateTime = System.currentTimeMillis();
        this.currentSubTaskDone = size;
    }

    public void endSubTask(String subTaskName)
    {
        subTaskLevel--; 
        updateSubTaskDone(this.currentSubTaskTodo);

        if (logSubTask)
            logPrintf("Done.\n[endSubTask:" + subTaskName+"]\n");
    }
    
    /** From ITaskMonitor -> Calls setTotalTransferred () */
    public void updateWorkDone(long size)
    {
        // buggy update: same size is reported
        // do not set time stamp to avoid 'degrading' of transfer speed

        if (size == totalWorkDone)
            return;

        this.totalWorkDone = size;
        // update time when this value was last updated ! s
        this.totalWorkDoneLastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Signal that this transfer must Stop!
     */
    public void setIsCancelled()
    {
    	Global.debugPrintf(this,">>> setIsCancelled() Called for:%s\n ",this); 
        this.mustStop = true;
    }
    
    public boolean isCancelled()
    {
		return this.mustStop;   
    }
    
    /** @deprecated use logPrintf(...) */
    public void addLogText(String format, Object ...args)
    {
        logPrintf(format,args); 
    }
    
    /** Add INFORMATIVE (for end user) text without newline to logtext */
    public void logPrintf(String format, Object ...args)
    {
    	// ALREADY sync'd: synchronized(logText)
    	{
    	    if (logSubTask)
    	    {
    	        taskLogger.logPrintf(insertIndentation(format),args);
    	    }
    		else
    		{
    		    //sloppy code!
    		    if (format==null)
    		        format="<NULL FORMAT>";
	            taskLogger.logPrintf(format,args);
    		}
    	}
    }
    
    // add indentation using the subtask level (currently for pretty print debugging only) 
    public String insertIndentation(String orgStr)
    {
	   String str=""; 
       for (int i=0;i<subTaskLevel;i++)
           str+=" - ";

	    String newStr=orgStr.replace("\n","\n"+str); 
        return newStr; 
    }
    
    public String getLogText()
    {
        return getLogText(false); 
    }
    
    public String getLogText(boolean incremental)
    {
        return taskLogger.getLogText(incremental); 
    }

    public boolean getMustStop()
    {
        return this.mustStop;
    }

    Boolean waitMutex = new Boolean(true);

    
    // private String currentTask;

    /**
     * This method will block until the setDone() method is called. 
     * This method is simalar to: {@link java.lang.Object#wait()} 
     */
    public void waitForCompletion() throws VlInterruptedException
    {
        waitForCompletion(0); 
    }
    
    /**
     * This method will block until the setDone() method is called or the specified
     * time has passed. 
     * This method is simalar to: {@link java.lang.Object#wait(long)} 
     */
    public void waitForCompletion(int timeout) throws VlInterruptedException
    {
        synchronized (waitMutex)
        {
            if (isDone())
                return;

            try
            {
                waitMutex.wait(timeout);
            }
            catch (InterruptedException e)
            {
                throw new VlInterruptedException(e);
            }
        }
    }

    public void startTask(String _taskName,long totalTodo)
    {
    	this.taskName=_taskName;
    	this.logPrintf("Started:%s\n",(this.taskName!=null?taskName:""));
        this.setTotalWorkTodo(totalTodo); 
        markStarted();
    }

    public long getTotalWorkDone()
    {
        return this.totalWorkDone;
    }

    public long getTotalWorkTodo()
    {
        return this.totalWorkTodo;
    }

    public long getTotalWorkDoneLastUpdateTime()
    {
        return this.totalWorkDoneLastUpdateTime;
    }

    
    public long getSubTaskDone()
    {
        return this.currentSubTaskDone;
    }
    
    public long getSubTaskTodo()
    {
        return this.currentSubTaskTodo;
    }

    public long getStartTime()
    {
        return this.startTime;
    }

    public long getStopTime()
    {
        return this.stopTime;
    }

    public long getSubTaskDoneLastUpdateTime()
    {
        return this.currentSubTaskDoneLastUpdateTime;
    }

    public long getSubTaskStartTime()
    {
        return this.currentSubTaskStartTime;
    }

    public String getSubTask()
    {
        if (currentSubTask == null)
            return "";

        return this.currentSubTask;
    }
    
    public String toString()
    {
        return
              "{ TaskMonitor:"+getId()+":"+this.taskName+"\n"
              + "  work todo = "+getTotalWorkTodo()+"\n" 
              + "  work done = "+getTotalWorkDone()+"\n"
              + "  sub todo  = "+getSubTaskTodo()+"\n" 
              + "  sub done  = "+getSubTaskDone()+"\n"
              + "  --- "
              + "  exception   ="+(this.getException()==null?"none":getException().getClass().getName())
              + "} ";
    }
    

    
    public void addMonitorListener(ITaskMonitorListener listener)
    {
        listeners.add(listener); 
    }

    public void removeMonitorListener(ITaskMonitorListener listener)
    {
        listeners.remove(listener); 
    }
    
    public void fireMonitorAdded(ITaskMonitor child)
    {
        fireEvent(MonitorEvent.createChildAddedEvent(this,child));  
    }
    
    public void fireMonitorRemoved(ITaskMonitor child)
    {
        fireEvent(MonitorEvent.createChildRemovedEvent(this,child));  
    }

    public void fireEvent(MonitorEvent event)
    {
        ITaskMonitorListener lArr[]=null;
        
        // create private array: 
        synchronized(listeners)
        {
            lArr=new ITaskMonitorListener[listeners.size()]; 
            lArr=listeners.toArray(lArr);
        }
        
        for (ITaskMonitorListener listener:lArr)
        {
            listener.notifyMonitorEvent(event); 
        }
    }
    
    public static void main(String args[])
    {
        DefaultTaskMonitor mon=new DefaultTaskMonitor(); 
     
        mon.logPrintf("Hoi\n"); 
        System.out.println("--- logtext ---\n"+mon.getLogText()+"\n-------\n");  
        
    }
}
