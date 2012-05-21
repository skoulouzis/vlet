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
 * $Id: ActionTaskRegistry.java,v 1.4 2011-04-18 12:00:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:34 $
 */ 
// source: 

package nl.uva.vlet.tasks;

import java.util.Vector;

import nl.uva.vlet.ClassLogger;

public class ActionTaskRegistry
{
	// === static === //
	
	private static ClassLogger logger; 
	{
		logger=ClassLogger.getLogger(ActionTaskRegistry.class); 
	}
	// ==== instance === // 
	
    /** Current actionTasks running in this JVM */ 
	private Vector<ActionTask> actionTasks=new Vector<ActionTask>();
	
	public ActionTaskRegistry()
	{
		
	}
	
	public void addTask(ActionTask actionTask)
	{
        synchronized(actionTasks)
        {
           actionTasks.add(actionTask);
        }
	}
	
	public void removeTask(ActionTask actionTask)
	{
        synchronized(actionTasks)
        {
           actionTasks.remove(actionTask);
        }
        
        ITaskSource taskSource = actionTask.getTaskSource();
        
        if ((taskSource!=null) && (hasTasksRunning(taskSource)==false))
        {
        	// notify TaskSource ! 
            taskSource.setHasTasks(false); 
        }
       
	}
	
	public void debugPrintTasks()
	{
        logger.infoPrintf("%s\n","ActionTask","--- Running Tasks ---"); 

        for (ActionTask task:actionTasks)
        {
        	Thread t=task.getFirstThread(); 
        	
            logger.infoPrintf("%s\n","ActionTask","--- "+task); 
            logger.infoPrintf("%s\n","ActionTask"," isAlive= "+task.isAlive());
            if (t==null)
            {
            	 logger.infoPrintf("%s\n","ActionTask"," No Threads!");
            }
            else
            {
	            logger.infoPrintf("%s\n","ActionTask"," thread state ="+t.getState());
	            StackTraceElement[] stack = t.getStackTrace(); 
	            for (StackTraceElement element:stack)
	            {
	                logger.infoPrintf("%s\n","ActionTask"," - "+element);
	            }
            }
        }
	}

	public void dispose()
	{
        synchronized(actionTasks)
        {
            for (ActionTask task:actionTasks)
            {
                if (task.isAlive())
                {
                   logger.infoPrintf("%s\n",ActionTask.class,"Stopping:"+task);
                   task.stopTask();
                   task.interrupt();
                }
                
            }
        }
        
        actionTasks.clear();
	}

	public boolean hasTasksRunning(ITaskSource bc)
	{
        // synchronized loop over all actiontasks:
    	synchronized(actionTasks)
    	{
           for (ActionTask task:actionTasks)
           {
               if (task.getTaskSource()==bc)
                  if ( (task.isAlive()) && (task.isCancelled()==false) )
                      return true; 
           }
    	}
        
        return false; 

	}

	/** Return a private copy of the task list, for thread save operations */
    final private ActionTask[] getTaskArray()
    {
        synchronized (actionTasks)
        {
            ActionTask tasks[]=new ActionTask[actionTasks.size()];
            tasks=actionTasks.toArray(tasks);
            return tasks;
        }
    }

	public void stopActionsFor(ITaskSource source, boolean all)
	{
        ActionTask tasks[]=getTaskArray();
        
        // send stop signal first: 
        for (ActionTask task:tasks)
        {
            if ( (all==true)
                 || ((task.getTaskSource()!=null) && (task.getTaskSource()==source))
                 )
            {
                task.signalTerminate(); 
            }
        }
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            logger.logException(ClassLogger.ERROR,e,"***Error: Exception:"+e); 
            e.printStackTrace();
        } 
        
        // now send interrupt:   
        for (ActionTask task:tasks)
        {
            if ((task.getTaskSource()!=null) && (task.getTaskSource()==source))
            {
                if (task.isAlive())
                    task.interrupt();  
            }
        }
        // check already stopped tasks: 
        if (this.hasTasksRunning(source))
        {
            source.setHasTasks(false); 
        }
        // thats all we can do. 
	}

	/**
	 * Find ActionTask with thread id. 
	 * Since all ActionTask are currently started in their
	 * own thread, this method will find the actionTask
	 * currently executed in the specified thread. 
	 * 
	 * @param thread Thread which started an ActionTask 
	 * @return 
	 */
	public ActionTask findActionTaskForThread(Thread thread)
	{
	    if (thread==null)
	        return null; 
	    
	    ActionTask tasks[]=getTaskArray();
		
	    for (ActionTask task:tasks)
	    {
	        if ((task!=null) && (task.hasThread(thread)) )
        		return task;
	    }
		 
	    return null; 
	}
}
