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
 * $Id: TaskMonitorPanel.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.monitoring;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.Global;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.tasks.MonitorStats;
import nl.uva.vlet.vrs.VRSTaskWatcher;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * Mini Status Panel
 */

public class TaskMonitorPanel extends JPanel implements ActionListener 
{
    private static final long serialVersionUID = -6694878602014623166L;

    private JLabel destLabel;
    private JTextField taskTF;
    private JPanel progresPanel;
    private JProgressBar subProgressBar;
    private JTextField progresPercTF;
    private JTextField currentTF;
    private JProgressBar progressBar;
     private JTextField subTaskTF;
   // private JTextField destTF;
  //  private JTextField sourceTF;
    private JLabel sourceLabel;
    private JPanel transferInfo;

    private Presentation presentation=Presentation.getDefault(); 
    private ITaskMonitor taskMonitor=null;
    private MonitorStats monitorStats; 
    private boolean showTransfersSpeeds=true;
    
    // constructor for Jigloo 
    public TaskMonitorPanel()
    {
        super(); 
        initGUI(); 
    }
    
    public TaskMonitorPanel(ITaskMonitor monitor)
    {
        super();
        initGUI();
        setMonitor(monitor);
    }
    
    public void setMonitor(ITaskMonitor monitor)
    {
        this.taskMonitor=monitor;  
        this.monitorStats=new MonitorStats(taskMonitor);
        // update at start to initialize fields: 
        update(); 
    }

    /** Whether speeds in [GMK]B/s should be shown */ 
    public void setShowTransferSpeed(boolean val)
    {
        this.showTransfersSpeeds=val; 
    }

    
    protected void update()
    {
        if (taskMonitor==null)
            return; 
        
        String task=this.taskMonitor.getTask(); 
        String subTask=this.taskMonitor.getSubTask();

        // Master Task: 
        this.taskTF.setText(task);
        
        long todo=taskMonitor.getTotalWorkTodo();
        
        if (todo<=0)
        {
            this.progresPercTF.setText("?"); 
        }
        else
        {
            double value=(double)taskMonitor.getTotalWorkDone()/(double)todo; 
          
            this.progressBar.setValue((int)(1000*value)); 
            
            // round to 99.99 
            value=Math.round(value*10000.0)/100.0;  
            this.progresPercTF.setText(""+value+"%  ");  
        }
        
        
        if (taskMonitor.isDone()==true)
        {
            this.progresPercTF.setText("Done."); 
 
            if (taskMonitor.hasError())
            {
               this.currentTF.setText("Error!");
            }
            else if (taskMonitor.isCancelled())
            {
               this.currentTF.setText("Cancelled!");
            }
            else
            {
                this.currentTF.setText("Done in:"
                        +this.presentation.timeString(this.monitorStats.getTotalDeltaTime(),false)); 
            }
        }
        else
        {
            // Sub Task if active:  
            if (subTask==null)
            {
                this.currentTF.setText(task);    
            }
            else
            {
                this.currentTF.setText(subTask);
                todo=taskMonitor.getSubTaskTodo();
                if (todo>0)
                {
                    double value=(double)taskMonitor.getSubTaskDone()/(double)todo; 
                    this.subProgressBar.setValue((int)(1000*value));
                }
               // this.statusTF.setText(subTask);
                //this.subTaskProgresPnl.setProgressText(getSubTaskProgressText()); 
                //this.subProgressBar.setProgress(monitorStats.getSubTaskProgress());
            }
        }
        
    }
   
   
    public String getTitle()
    {
        return this.taskTF.getText();
    }

    private String getTimeText()
    {
        String timestr=presentation.timeString(monitorStats.getTotalDoneTime(),false);
        
        long eta=monitorStats.getETA();
        
        if (eta<0)
        	timestr+=" (?)";
        else if (eta==0) 
        	timestr+= " (done)";
        else
        	timestr+=" ("+presentation.timeString(eta,false)+")";
        
        return timestr; 
        
    }

    protected Container getContentPane()
    {
        return this; 
    }

    private void initGUI() 
    {
        try 
        {
            {
                FormLayout transferInfoLayout = new FormLayout(
                        "5dlu, 5dlu, 178dlu:grow, max(p;5dlu)", 
                        "max(p;5dlu), max(p;8dlu), 5dlu, max(p;15dlu), 5dlu");
                this.setLayout(transferInfoLayout);
                
                this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                this.add(getTaskTF(), new CellConstraints("2, 2, 2, 1, default, default"));
                this.add(getProgresPanel(), new CellConstraints("2, 4, 2, 1, default, default"));
            }
            {
                // defaults:            
                this.progressBar.setMaximum(1000); 
                this.subProgressBar.setMaximum(1000);
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {
//        if (e.getSource()==this.okButton)
//            dispose();
//        
//        if (e.getSource()==this.cancelButton)
//        {
//        	// stop already initiated
//        	if (task.isCancelled())
//        		cancelButton.setEnabled(false); 
//        	
//            this.task.signalTerminate();
//        }
    }

    /** return progress information */ 
    public String getTotalProgressText()
    {
        ITaskMonitor info=taskMonitor; 
        
        String progstr="";
        
        String speedStr=sizeString((int)monitorStats.getTotalSpeed())+"B/s"; 
        String amountStr=sizeString(info.getTotalWorkDone())+" (of "+sizeString(info.getTotalWorkTodo())+")";
        
        if (info.isDone())
        {
            // Final Times. no progress strings 
            String finalStr="Done:"+amountStr; 
            
        	if (showTransfersSpeeds)
        	    finalStr+=" ("+speedStr+")"; 
        	
        	finalStr+=" in "+presentation.timeString(monitorStats.getTotalDoneTime(),false);
        	
        	return finalStr; 
        }
       
        progstr+=amountStr; 
        
        // TransferSpeed ONLY for VFS Transfers !
        if (showTransfersSpeeds)
            progstr+=" ("+speedStr+")";
        
     
        return progstr;
    }
    
    /** return progress information */ 
    public String getSubTaskProgressText()
    {
    	ITaskMonitor info=taskMonitor; 
        
        String progstr="";
        
        if (info.isDone())
        {
            return "Done.";
        }
        
        // Print Current transfer info:
        
        if (info.getSubTaskTodo()<=0)
        {
            progstr="(?) ";
        }
        else
        {
            progstr+=percentage3(info.getSubTaskDone(),info.getSubTaskTodo())+"% ";
        }
        
        long delta=info.getSubTaskDoneLastUpdateTime()-info.getSubTaskStartTime();
        
        if (delta<=0) 
            delta=1;
        
        if (showTransfersSpeeds)
        {
            progstr+=" ("+ sizeString((int)monitorStats.getSubTaskSpeed())+"B/s)";   
        }
        
        
        return progstr;
    }

    public String sizeString(long size)
    {
        if (size<0) 
            return "?";
        
        return presentation.sizeString(size,true,1,1);
    }
    

    /** Return percentage in 3 chars */
    private String percentage3(long x,long y) 
    {
        long perc=(x*1000)/y; 
        
        if (perc>=1000) 
            return "100"; 
        
        if (perc>=100)
            return " "+(perc/10);
        
        return (perc/10)+"."+(perc%10);    
        
    }

   
    
    public void dispose()
    {
      
    }

    private JTextField getSubTaskTF() {
        if(subTaskTF == null) {
            subTaskTF = new JTextField();
            subTaskTF.setText("Sub Task");
        }
        return subTaskTF;
    }

    private JTextField getTaskTF() {
        if(taskTF == null) {
            taskTF = new JTextField();
            taskTF.setText("Task");
        }
        return taskTF;
    }
    
    private JProgressBar getProgressBar() {
        if(progressBar == null) {
            progressBar = new JProgressBar();
        }
        return progressBar;
    }
    
    private JTextField getCurrentTF() {
        if(currentTF == null) {
            currentTF = new JTextField();
            currentTF.setText("Current Task");
        }
        return currentTF;
    }
    
    private JTextField getProgresPercTF() {
        if(progresPercTF == null) {
            progresPercTF = new JTextField();
            progresPercTF.setText("999.99%");
        }
        return progresPercTF;
    }
    
    private JProgressBar getSubProgressBar() {
        if(subProgressBar == null) {
            subProgressBar = new JProgressBar();
        }
        return subProgressBar;
    }

    public boolean isDone()
    {
        return this.taskMonitor.isDone(); 
    }
    
    private JPanel getProgresPanel() {
        if(progresPanel == null) {
            progresPanel = new JPanel();
            FormLayout progresPanelLayout = new FormLayout(
                    "max(p;5dlu), max(p;128dlu):grow, max(p;5dlu), max(p;15dlu), 5dlu, max(p;5dlu), 5dlu", 
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");
            progresPanel.setLayout(progresPanelLayout);
            progresPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            progresPanel.add(getProgressBar(), new CellConstraints("2, 2, 3, 1, default, default"));
            progresPanel.add(getCurrentTF(), new CellConstraints("2, 3, 1, 1, default, default"));
            progresPanel.add(getProgresPercTF(), new CellConstraints("6, 2, 1, 1, default, default"));
            progresPanel.add(getSubProgressBar(), new CellConstraints("4, 3, 3, 1, default, default"));
        }
        return progresPanel;
    }

}
