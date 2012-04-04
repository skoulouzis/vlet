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
 * $Id: ISubTaskMonitor.java,v 1.5 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.tasks;

/**
 * Interface Methods for subtasks. 
 * This interface is separated so that subtask
 * cannot call the outer task monitor methods. 
 * 
 * @author P.T. de Boer 
 */
public interface ISubTaskMonitor
{
	/**
	 * Set single line describing the current task and the amount
	 * of work it has todo. 
	 * Explicitly stopping a subTask is not necessary. When calling startSubTask again
	 * it is assumed the previous has stopped. When the (super interface) method
	 * stopTask() is called it is assumed the current subTask has ended as well. 
	 */ 
	public void startSubTask(String taskName,long amountTodo);
	
	/** Signal that the task has ended. taskName is optional */ 
	public void endSubTask(String taskName);  

	/** Return a single line describing the current SubTask */ 
	public String getSubTask(); 
	
	/** Returns amount of work done in current subtask */ 
    public long getSubTaskDone();
    
	/** Returns amount of work todo done in current subtask as specified in startSubTask */ 
    public long getSubTaskTodo();
    
	/** updates amount of work donne in current subtask */ 
	public void updateSubTaskDone(long workDone);
	
    public long getSubTaskStartTime();

    public long getSubTaskDoneLastUpdateTime();
    
	/**
	 * Add human readable log text intended for users to read. 
	 * Does not append a newline character.  
	 */ 
    public void logPrintf(String format, Object ...args); 
	
}
