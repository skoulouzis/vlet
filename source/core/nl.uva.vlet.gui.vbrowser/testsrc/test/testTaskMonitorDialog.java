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
 * $Id: testTaskMonitorDialog.java,v 1.2 2011-04-18 12:27:24 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:24 $
 */ 
// source: 

package test;

import nl.uva.vlet.gui.panels.monitoring.TaskMonitorDialog;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.tasks.ITaskMonitor;

public class testTaskMonitorDialog
{
    // === Static === 
    public static ActionTask createTestTask()
    {
        return new ActionTask(null,"StatusPanel background Task")
        {
            public void doTask()
            {
                int max=1000; 
                int dif=10; 
                
                ITaskMonitor monitor = this.getTaskMonitor(); 
                
                monitor.startTask("Main Tester Task",max); 
                                 
                for (int i=0;i<=max;i++)
                {
                    if (this.isCancelled())
                    {
                        monitor.logPrintf("\n***\n***CANCELLED***\n****\n"); 
                        break;  
                    }

                    monitor.logPrintf("<.........[%d]..........>\n",i);
                    
                    if ((i%10)==0)
                    {
                        monitor.startSubTask("Subtask:"+i,dif);
                    }
                    
                    if ( ((i%100)==0) && (i/100<5)) 
                    {
                        monitor.logPrintf("\n<<<loggingText>>>\n");  
                        // Dummy Monitor! 
                        monitor.addSubMonitor(new DefaultTaskMonitor());
                    }
                    
                    monitor.updateSubTaskDone(i%dif); 
                    
                    // panel.setProgress(i); 
                    monitor.updateWorkDone(i); // panel.setProgress((double)i/1000.0); 
                    
                    try
                    {
                        Thread.sleep(60-(i*50)/max);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                monitor.endTask("Main Tester Task"); 
                
            }

            @Override
            public void stopTask()
            {
            }
        };
    }
    
    /**
     * Auto-generated main method to display this JDialog
     */
     public static void main(String[] args)
     {
         ActionTask task=createTestTask(); 
         task.startTask(); 
         TaskMonitorDialog inst = new TaskMonitorDialog(null,task); 
         inst.setDelay(1000);
         inst.start(); 
     }
     
     
    
}
