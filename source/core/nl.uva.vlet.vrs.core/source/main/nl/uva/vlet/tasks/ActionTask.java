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
 * $Id: ActionTask.java,v 1.13 2011-04-18 11:58:56 ptdeboer Exp $  
 * $Date: 2011-04-18 11:58:56 $
 */ 
// source: 

package nl.uva.vlet.tasks;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.error.InitializationError;
import nl.uva.vlet.exception.VlException;

/** 
 * Simple Task class which extends the Runnable class. 
 * This class creates it's own thread and monitors it. 
 * The main purpose is to manage all the 'Task' threads 
 * in the Multi Threaded Gui. Tasks (+their threads) are linked
 * to a BrowserController which implements the ITaskSource interface. 
 * Update: 
 * This class is now part of nl.uva.vlet.Main to provide a single thread+task
 * class for easier multithreaded programming.
 * <p>
 * TODO: better thread management. Now each task creates it's own thread. 
 * 
 * @author P.T. de Boer
 */
public abstract class ActionTask implements Runnable
{
    // ======================================================================== 
    // Class Stuff 
    // ========================================================================
    
	private static ActionTaskRegistry taskRegistry=new ActionTaskRegistry(); 
	
	private static ClassLogger logger=null; 
	
	static
	{
	    logger=ClassLogger.getLogger(ActionTask.class); 
	}
    
    /** print out the task list to the debugStream */ 
    final public static void debugPrintTasks()
    {
    	taskRegistry.debugPrintTasks(); 
    }
    
    /** Returns true if the ITaskSource has tasks running */ 
    final public static boolean hasTasksRunning(ITaskSource bc)
    {
    	return taskRegistry.hasTasksRunning(bc); 
    
    }
    
    /** Send a stop signal to all tasks originating from the ITaskSource */ 
    public static void stopActionsFor(ITaskSource source, boolean all)
    {
    	taskRegistry.stopActionsFor(source,all); 
    }
    
    public static void disposeClass()
    {
    	taskRegistry.dispose(); 
    }
    
    /**
     * If the current thread was started from an
     * ActionTask this method will return the ActionTask
     * that was started. 
     * Since each ActionTask has it's own thread and no two
     * ActionTasks can be simultaneous be started in the same
     * Thread there will only be one ActionTask active. 
     * This method returns null if the current thread was not started
     * from within an ActionTask   
     *  
     * @return Current ActionTask or null
     */
    public static ActionTask getCurrentThreadActionTask()
    {
    	return taskRegistry.findActionTaskForThread(Thread.currentThread()); 
    }
    
//    /**
//     * Static method to get the subtask monitor of the current thread, if this 
//     * thread is part of an ActionTask. 
//     * Might return null !
//     * 
//     * @return subTask monitor of this thread or null.
//     */
//    public static ISubTaskMonitor getCurrentThreadSubTaskMonitor()
//    {
//    	ActionTask task=taskRegistry.findActionTaskForThread(Thread.currentThread());
//    	
//    	if (task==null)
//    		return null; 
//    
//    	return task.getSubTaskMonitor(); 
//    }
    // ======================================================================== 
    // Instance  
    // ========================================================================
    
    /** is true when a task is interrupted and 'should' terminate */  
    private volatile boolean isCancelled=false;
    
    // =======================================
    // MUTEX PROTECT Threads Array!!!! 
    private Object threadsMutex=new Object(); 
    // =======================================
    
    private Thread threads[]=null; // For Parallel Execution ! 

    private String taskName=null;

    private ITaskSource taskSource=null;

    private boolean hasStarted;

	private ITaskMonitor taskMonitor;

	private String parallelID=null; 
    
    /** Construct a new ActionTask origination from the taskSource source 
     * with the human readable name taskName */ 
    public ActionTask(ITaskSource source,String taskName)
    {
        this.taskName=taskName; 
        this.taskSource=source;
        
        taskRegistry.addTask(this);
        
        // default EMPTY taskMonitor; 
        initTaskMonitor(); 
    }
    
    final public String getTaskName()
    {
    	return this.taskName; 
    }
    
    /**
     *  Starts this task. Spawns a new thread an invokes the start method 
     *  of the thread. 
     *   
     */ 
    final public void startTask()
    {
        Thread thread=initThread(this.toString()); 
        
        // -----------
        // Concurrency Bug: invoke setHasTasks BEFORE starting new thread.
        // -----------
        this.hasStarted=true; 
        if (taskSource!=null)
            this.taskSource.setHasTasks(true);
        
        thread.start(); // thread calls methods run()
    }
    
    
    // Initalize single thread
    private Thread initThread(String string)
    {
        synchronized(this.threadsMutex)
        {
            this.threads=new Thread[1];
            this.threads[0]=createNewThread(this);
            
            this.threads[0].setName(this.toString());
            return this.threads[0]; 
        }
    }
    
    final private void initTaskMonitor()
    {
    	this.taskMonitor=new DefaultTaskMonitor();
    }
    
    /** Set Task Monitor before starting the task */ 
    public void setTaskMonitor(ITaskMonitor monitor)
    {
        if (this.hasStarted==true)
            throw new InitializationError("Cannot set the task monitor after the task has started:"+this); 
        
        this.taskMonitor=monitor; 
    }

    /**
     * Set TaskSource/Task Watcher to (new) watcher.
     * This is done by the gui to tranfer responsibility to
     * (for example) a backgrounded transfer watcher. 
     * This will make it appear the GUI isn't busy anymore, freeing the 
     * GUI, so the user can continue.  
     * 
     * @param newWatcher
     */
    final protected void setTaskSource(ITaskSource newSource)
    {
        ITaskSource oldSource = this.taskSource; 
        
        this.taskSource=newSource; 
        
        // check wheter old source has task running and update status 
        if ((oldSource!=null) && (hasTasksRunning(oldSource)==false))
        {
            oldSource.setHasTasks(false); 
        }
    }

    
    // Create new thread.
    private Thread createNewThread(Runnable runTask)
    {
        return new Thread(runTask); 
    }

    /** 
     * This method is called by thread.start() to start
     * the runnable. The ActionTask object will execute
     * the 'doTask' method which is implemented by the subclass.
     */ 
    final public void run()
    {
        consolePrintln("Starting task:"+this);
        
        if (taskSource!=null)
           this.taskSource.setHasTasks(true); 
        
        // default amount of work is not known ! 
        this.taskMonitor.startTask(this.taskName,-1); 
        
        try
        {
           doTask();
        }
        // Catch all possible exceptions so that the actiontask can be disposed of 
        // gracefully ! 
        catch(VlException e)
        {
            // Keep VlException 
            setException(e);
        }
        catch (Throwable e)
        {
            // ===
            // SYSTEM ERROR 
            // ===
            logger.logException(ClassLogger.ERROR,e,"System Error performing task:%s\n",this); 
            
            VlException vlex=new VlException(e.getClass().getSimpleName(),e.getMessage(),e);
            setException(vlex);
        }
        
        // Finally will always be executed since all Throwables are catched ! 
        finally 
        {
        	if (hasException())
        		this.taskMonitor.logPrintf("=== Task finished with Exception ===\n");
        	else 
        		this.taskMonitor.logPrintf("=== Task finished ===\n");
        	  
            this.taskMonitor.endTask(taskName);
            
            if (getException()==null)
            {
                consolePrintln("Task finished:"+this);
            }
            else
            {
            	Throwable e=getException(); 
            	
                String exName="";
                if (e instanceof VlException)
                {
                	exName=":"+((VlException)e).getName(); // human readable name 
                }
                
                // VBrowser console: 
                consolePrintln("Task terminated with Exception:"+this);
                consolePrintln("Exception="+e.getClass().getSimpleName()+exName);
                
                // TaskMonitor: 
                this.taskMonitor.logPrintf("--- Exception ---\n");
                this.taskMonitor.logPrintf("Exception = "+e.getClass().getSimpleName()+exName+"\n"); 
                this.taskMonitor.logPrintf(e.getMessage()); 
            }
            
           // myDispose(); 
           // inform TaskSource: 
            
           if (hasException())
           {
                if (this.taskSource!=null)
                    this.taskSource.handle(getException());
                
           }
           
           // dispose 
           taskRegistry.removeTask(this);
           
           clearThreads(); 
        }
    }
    
  
    
    private void clearThreads()
    {
        synchronized(threadsMutex)
        {
            // trigger release resources held by threads:
            if (this.threads!=null)
                for (int i=0;i<threads.length;i++)
                    threads[i]=null; // nullify ! 
             
            this.threads=null;
        }
    }

    final public boolean hasTerminated()
    {
        synchronized(threadsMutex)
        {
    		if (threads==null)
    			return true; 
    		
        	for (Thread thread:threads)
        		if ( (thread!=null) && (thread.isAlive()==false) )
        			return false;
        }
        
		return true;  
    }
    
	final public boolean hasThread(Thread t) 
	{
	    synchronized(threadsMutex)
        {
    		if (threads==null)
    			return false; 
    		
    		for (Thread thread:threads)
        		if ( (thread!=null) && (thread.getId()==t.getId()) )
        			return true; 
        }
	    
		return false;   
	}
	
    /** Store exception for later use */ 
    final protected void setException(Throwable e)
    {
    	taskMonitor.setException(e);  
    }

    /**
     * Print a message in the ActionTasks console window
     * This window or panel is owned by the VBrowser and 
     * is not part of a TaskMonitor.
     */ 
    final public void consolePrintln(String msg)
    {
        if (taskSource!=null)
            taskSource.messagePrintln(msg);
        
       logger.infoPrintf("CONSOLE:%s\n",msg);  
    }

    /** Checks whether the worker thread is alive */ 
    final public boolean isAlive()
    {
        synchronized(this.threadsMutex)
        {
        	if (threads==null)
        		return false; 
    	
        	for (Thread thread:threads)
        		if ( (thread!=null) && (thread.isAlive()) )
        			return true;
        }
        
		return false; 
    }
    
    /** Returns whether this action task is terminating */ 
    final public boolean isCancelled()
    {
        return isCancelled; 
    }

    final public String toString()
    {
        String bcidstr=null; 
        
        // format of task ID is: (<BrowserID>,<Thread>)
        if (this.taskSource==null)
           bcidstr="<null>"; 
        else
           bcidstr=""+taskSource.getID();
        
        int numTs=0; 
        String tid="<NONE>";
        
        synchronized(this.threadsMutex)
        {
            if (threads!=null)
            	numTs=threads.length;
            if (numTs>0)
            	tid=""+threads[0].getId();
        }
        
        return "ActionTask("+bcidstr+"."+tid+"/"+numTs+"):"+taskName;
    }
    
    /**
     * Sends the running thread an interupt signal.  
     * @see Thread#interrupt()
     */
    final public void interrupt()
    {
        logger.debugPrintf("ActionTask:%s\n","*** Interrupting:"+this);
        
        for (Thread thread:threads)
    		if (thread!=null) 
				thread.interrupt(); 
    }  

    /** 
     * Use the Thread.join() method to join with the running task thread. 
     * Will immediately return if thread already finished. 
     * 
     */
    final public void join() throws InterruptedException
    {
    	join(0);
    }
    
    /** 
     * Use the Thread.join() method to join with the running task thread. 
     * Will immediately return if thread already finished. 
     * 
     */
    final public void join(long timeOutMillis) throws InterruptedException
    {
        Thread _threadz[];
        
        synchronized(this.threadsMutex)
        {
        	if ((threads==null) || (threads.length<=0)) 
        		return; 
        	_threadz=threads;
        }
            
    	for (Thread thread:_threadz)
    		if (thread!=null)
    			thread.join(timeOutMillis);
        
    }
    
    final public Thread getFirstThread()
	{
        synchronized(this.threadsMutex)
        {
        	if (threads==null)
        		return null;
        	
        	for (Thread thread:threads)
        		if (thread!=null)
        			return thread;
        }
        
    	return null; 
	}
    /** 
     * Checks whether the thread has 'stored' an exception for later use
     * @see #setException(VlException)
     * @see #getException() 
     */
    final public boolean hasException()
    {
        if (getException()!=null) 
           return true;
        
        return false; 
    }
    
    /**
     * Returns stored exception. Wraps Throwables and 
     * Errors to Exceptions 
     */ 
    final public Exception getException()
    {
    	Throwable ex=taskMonitor.getException(); 

    	if (ex==null)
    		return null; 
    	
    	if (ex instanceof Exception) 
    		return (Exception)ex;

    	// Exception chaining: 
        return VlException.newChainedException(ex); // "Exception",exception.getMessage(),exception);  
    }
    
    /** Returns unchained throwable */ 
    final public Throwable getThrowable()
    {
   		return this.taskMonitor.getException(); 
    }
   
    final public boolean hasStarted()
    {
        return this.hasStarted; 
    }

	final public void waitFor() throws InterruptedException
	{
		// will return when thread finishes or already has finished.
		waitFor(0);  
	}

	final public void waitFor(long timeOut) throws InterruptedException
	{
		join(timeOut); 
	}

	

	final public ITaskSource getTaskSource()
	{
		return this.taskSource; 
	}
	
	/** Signal this Task to Stop ! */ 
	final public void signalTerminate()
	{
		this.isCancelled=true;
		// forward abort to monitor as well ! 
		this.taskMonitor.setIsCancelled();  
        stopTask();
	}

	/** Returns SubTask monitor for progress monitoring */
	final public ISubTaskMonitor getSubTaskMonitor()
	{
		return this.taskMonitor;
	}
	
	final public ITaskMonitor getTaskMonitor()
	{
		return this.taskMonitor;
	}

	/** Specify parallel task identifier for multitasks */ 
	final public void setParID(String parId) 
	{
		this.parallelID=parId; 
	}
	
	/** Return custom parallel task identifier for multitasks */ 
	final public String getParID() 
	{
		return this.parallelID;  
	}
  
	/*
     * Start task. Method will be executed in own subthread!
     * protected: may only be called by this superclass !
     */ 
    protected abstract void doTask() throws VlException; //  ? 

    /**
     * Default stop method: PLEASE implement this if your ActionTask can be stopped!
     * This is to improve pre-emptive multithreaded tasks !
     */  
    public abstract void stopTask();

	 /**
      * Check VBrowser Action Task Context and get current task
      * monitor or create new one with the specified taskName 
      * and amount of work. <br>
      * Under Construction ... 
      * 
      * @param taskName
      * @param totalTodo
      * @return current task monitor or a new created one. 
      */
    public static ITaskMonitor getCurrentThreadTaskMonitor(String taskName,
            long todo) 
    {
        // check if executed during action task: 
        ActionTask task = ActionTask.getCurrentThreadActionTask();
        ITaskMonitor monitor=null; 
            
        if (task!=null)
        {
            monitor=task.getTaskMonitor();
        }
        
        if (monitor==null)
        {
            monitor=new DefaultTaskMonitor(); 
            monitor.startTask(taskName, todo); 
        }
        
        return monitor;
    }



   
}
