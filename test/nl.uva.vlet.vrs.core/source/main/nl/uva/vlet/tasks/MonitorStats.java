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
 * $Id: MonitorStats.java,v 1.3 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.tasks;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.presentation.Presentation;

/** 
 * Class which calculates statistics from an ITaskMonitor. 
 * Also provides some status methods. 
 * 
 */ 
public class MonitorStats
{
    ITaskMonitor monitor=null; 
    
    Presentation presentation=new Presentation(); 
    
    public MonitorStats(ITaskMonitor monitor)
    {
        this.monitor=monitor; 
    }
    
    public MonitorStats(ITaskMonitor monitor,Presentation presentation)
    {
        this.monitor=monitor; 
        this.presentation=presentation; 
    }
    
    /**
     * Returns current task time time in millis or total time when task is done
     */
    public long getTotalDoneTime()
    {
        if (monitor.isDone())
        {
            return (monitor.getStopTime()-monitor.getStartTime()); 
            
        }
        else
        {
            return System.currentTimeMillis() - monitor.getStartTime(); 
        }
    }
    
    /** 
     * Returns ETA in millis. 
     * Depends on totalTodo/totalDone to calculate ETA.  
     * <pre>
     * -1 = no statistics
     *  0 = done 
     * >0 = estimated finishing time in milli seconds 
     * </pre>
     * @return
     */ 
    public long getETA()
    {
        if (monitor.isDone())
            return 0; // done
        return calcETA(monitor.getTotalWorkDone(),monitor.getTotalWorkTodo(),getTotalSpeed());
    }
    
    public long getSubTaskETA()
    {
        if (monitor.isDone())
            return 0; // done
        
        return calcETA(monitor.getSubTaskDone(),
                monitor.getSubTaskTodo(),
                this.getSubTaskSpeed());
    }
    
    /** return ETA in millis */  
    public long calcETA(long done,long todo, double speed)
    {
        // no statistics ! 
        if (done<=0)
            return -1; //unknown
        
        // nr of bytes/ nr of total work todo   
        long delta=todo-done;
        
        if (speed<=0) 
            return -1; //unknown 
        // return ETA in millis ! 
        return (long)((1000*delta)/speed); 
    }
    
    public String getStatus()
    {
        if (monitor.isDone() == false)
        {
            String subStr = monitor.getSubTask();

            if (StringUtil.isEmpty(subStr))
                return "Busy ...";
            else
                return subStr;
        }

        if (monitor.hasError())
            return "Error!";

        return "Finished!";
    }

    
    /**
     * Return speed total transfer amount of workdone/seconds 
     * for transfer this is bytes/second. 
     * If nr of bytes equals amount of work done.  
     */ 
   public double getTotalSpeed()
   {
       // time is in millis, total work amount in bytes (for transfers) 
       double speed=((double)monitor.getTotalWorkDone())/(double)(getTotalDeltaTime()); 

       // convert from work/millisecond to work/seconds 
       speed=speed*1000.0;
       
       return speed; 
   }

   public double getTotalProgress()
   {
       if (this.monitor.getTotalWorkTodo()<=0)
       {
           return Double.NaN; //   
       }
           
       return ((double)monitor.getTotalWorkDone())/(double)monitor.getTotalWorkTodo(); 
   }
   
   public double getSubTaskProgress()
   {
       if (this.monitor.getTotalWorkTodo()<=0)
       {
           return Double.NaN;   
       }
       
       return ((double)monitor.getSubTaskDone()/(double)monitor.getSubTaskTodo()); 
   }
   
   /**
    * Return speed total transfer amount of workdone/seconds 
    * for transfer this is bytes/second. 
    * If nr of bytes equals amount of work done.  
    */ 
  public double getSubTaskSpeed()
  {
      // time is in millis, total work amount in bytes (for transfers) 
      double speed=((double)monitor.getSubTaskDone())/(double)(getSubTaskDeltaTime()); 

      // convert from work/millisecond to work/seconds 
      speed=speed*1000.0;
      
      return speed; 
  }
  
  /** 
   * Calculated total time busy in millies but uses Last Update Time to prevent 'degrading'
   * performance time ! 
   */
  public long getTotalDeltaTime()
  {
      return monitor.getTotalWorkDoneLastUpdateTime()-monitor.getStartTime();
  }

  /** 
   * Calculated total time busy in millies but uses Last Update Time to prevent 'degrading'
   * performance time ! 
   */
  public long getSubTaskDeltaTime()
  {
      return monitor.getSubTaskDoneLastUpdateTime()-monitor.getSubTaskStartTime();
  }

    

}
