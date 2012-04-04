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
 * $Id: ITaskMonitor.java,v 1.7 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.tasks;


/**
 * New TaskMonitor Interface.
 *  
 * Use startTask() to indicate a start, use updateTotalWorkDone() during 
 * execution and use endTask() to indicate the task has completed.
 * Use the subTask interface for more granularity for big tasks.  
 * 
 * @author P.T. de Boer
 *
 */
public interface ITaskMonitor extends ISubTaskMonitor
{
	/**
	 * Call this method before starting a task. Optional specifying a taskName.
	 * The time this method is called is used as startTime.  
	 * After the task has finished call endTask(). The time that method 
	 * has been called will be used as end time. 
	 * @param totalAmountTodo amount of work. Set to -1 if unknown. 
	 */
	public void startTask(String taskName,long totalAmountTodo); 
	
	/** Task Name */ 
	public String getTask(); 
	
	/** Returns whether the task has finished. */ 
	public boolean isDone(); 
	
	/** 
	 * Ends task or subtask with the same name.
	 * The name is optional but can be used to check consistency between
	 * startTask and endTask() calls.  
	 */
	public void endTask(String taskName);
	
	// ===
	// Work Done/Todo 
	// ===
	
	
	/** Get total amount of work todo */ 
	public long getTotalWorkTodo(); 
	
	/**
	 * Update current amount of work done 
	 * Should be less than getTotalWorkTodo(). 
	 * This method updates the timers as well. 
	 */  
	public void updateWorkDone(long currentDone); 
	
	/** 
	 * Returns amount of work currently done. 
	 * Should be less than getTotalWorkTodo() 
	 */ 
	public long getTotalWorkDone();  
	
	// === 
	// Add User Readable text ! 
	// === 
	
	/**
	 * Add human readable log text intended for users to read. 
	 * Does not append a newline character. 
	 * Use in the same way as printf(...) 
	 */ 
    public void logPrintf(String format, Object ...args); 

    /** @deprecated: use logPrintf */ 
    public void addLogText(String format, Object ...args); 
    
    /** 
     * Return full text of log. Contains all occured log messages. 
     * For incremental monitoring, use getLogText(true) 
     */ 
    public String getLogText();
    
    /**
     * Returns log text. If incremental==true this method returns only the
     * log events which have happened since the last getLogText(true) call. 
     * For tasks which produce a lot of logging text or methods which take a long time 
     * and produce a lot of logging text, use this method. 
     */   
    public String getLogText(boolean incremental); 
    
	// ===
	// Exceptions 
	// === 

	/** Return whether the task has encountered an Error */  
	public boolean hasError();
	
	/** Get Exception (for backgrounded tasks) */ 
	public Throwable getException();
	
	/** Set Current encountered Exception (for backgrounded tasks) */
	public void setException(Throwable t);
	
	// === 
	// SubTask 
	// ===

	/**
	 * Set single line describing the current task and the amount
	 * of work it has todo. 
	 * Explicitly stopping a subTask is not necessary. When calling startSubTask again
	 * it is assumed the previous has stopped. When the (super interface) method
	 * stopTask() is called it is assumed the current subTask has ended as well. 
	 */ 
	public void startSubTask(String taskString,long amountTodo); 

	/** Return a single line describing the current SubTask */ 
	public String getSubTask(); 
	
	/** Returns amount of work done in current subtask */ 
    public long getSubTaskDone();
    
	/** Returns amount of work todo done in current subtask as specified in startSubTask */ 
    public long getSubTaskTodo();
    
	/** updates amount of work donne in current subtask */ 
	public void updateSubTaskDone(long workDone);
   
    public void addSubMonitor(ITaskMonitor childMonitor);
    
    /** Returns subMonitors in a private array */ 
    public ITaskMonitor[] getSubMonitors();

    /** signal that the task is terminating or should be stopped ASAP */ 
    public void setIsCancelled();
    
    public boolean isCancelled(); 
    // === timers === // 
    
    public long getSubTaskStartTime();

    public long getSubTaskDoneLastUpdateTime();

    public long getStartTime();

    public long getStopTime();
    
    public long getTotalWorkDoneLastUpdateTime();

    public void addMonitorListener(ITaskMonitorListener listener);
    
    public void removeMonitorListener(ITaskMonitorListener listener);

}
