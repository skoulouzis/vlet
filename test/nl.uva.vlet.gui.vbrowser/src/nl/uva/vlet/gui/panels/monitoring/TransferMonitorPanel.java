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
 * $Id: TransferMonitorPanel.java,v 1.5 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.monitoring;

import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.MonitorStats;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vrl.VRL;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TransferMonitorPanel extends JPanel
{
    private static final long serialVersionUID = -8105023008570959471L;
    
 // === Static === 
    
    /**
     * Auto-generated main method to display this JDialog
     */
     public static void main(String[] args)
     {
         JFrame frame = new JFrame();
         // dimmy:
         VFSTransfer transfer=new VFSTransfer(null,
                 "Transfer", 
                 new VRL("file","host","/source"),
                 new VRL("file","host","/dest"),
                 true);
         
         TransferMonitorPanel inst = new TransferMonitorPanel(transfer);
        
         frame.add(inst); 
         frame.pack(); 
         frame.setVisible(true); 
         
         
         int max=1000*1024; 
         int dif=50*1024;
         int step=1024; 
         
         transfer.startTask("TransferTask",max); 
         transfer.setTotalSources(max/dif); 
         
         for (int i=0;i<=max;i+=step)
         {
             if (transfer.isCancelled())
             {
                 transfer.logPrintf("\n*** CANCELLED ***\n"); 
                 break; 
             }
                
             if ((i%dif)==0)
             {
                 transfer.startSubTask("Transfer #"+i/dif, dif); 
                 transfer.logPrintf("--- New Transfer ---\n -> nr="+i/dif+"\n");  
             }
             
             transfer.updateWorkDone(i);
             transfer.setSourcesDone(i/dif); 
             transfer.updateSubTaskDone(i%dif); 
             // do update self!
             inst.update(); 
             
             try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
         }
         
         transfer.endTask();
         
     }
     
     // =======================================================================
     
    private JTextField titleTextField;
    private JLabel destLabel;
   
    private JTextField destTF;
    private JTextField sourceTF;
    private JLabel sourceLabel;
   
    // private JTextArea logText;
    private VFSTransfer vfsTransferInfo;
    private JPanel currentPanel;
    private JLabel currentLbl;
    private JTextField currentTF;
    private ProgresPanel subProgresPanel;
    private ProgresPanel progresPanel;
    // Not yet: 
    // private ITaskSubTaskMonitor vfsTransferInfo; 
    private BrowserController browserController;

    Presentation presentation=Presentation.getDefault();
    // new monitor statistics object to move shared 
    // code between VFSTransfer and default Task monitor 
    private MonitorStats monitorStats=null;  
    
    public TransferMonitorPanel() 
    { 
        super();
        init();
    }
    
    public TransferMonitorPanel(LayoutManager layoutMgr) 
    { 
        super(layoutMgr);
    }
       
    public TransferMonitorPanel(VFSTransfer transfer)
    {
        super(); 
        this.vfsTransferInfo=transfer;
        this.monitorStats=new MonitorStats(transfer);
        init();
    }
    
    private void init()
    {
    	initGUI();
        update(); 
    }
      
    
    void update()
    {
        String task=this.vfsTransferInfo.getTask(); 
        String subTask=this.vfsTransferInfo.getSubTask(); 
        
        sourceTF.setText(StringUtil.toString(vfsTransferInfo.getSourceText())); 
        destTF.setText(StringUtil.toString(vfsTransferInfo.getDestination())); 
        //statusTF.setText(monitorStats.getStatus()); 
        //currentTF.setText(getCurrentProgress()); 
        //progressTF.setText(getTotalProgress()); 
        // logText.setText(vfsTransferInfo.getLogText());
        //logText.revalidate(); 


        //timeTF.setText(getTimers()); 
        
        // total: 
        this.progresPanel.setProgress(monitorStats.getTotalProgress()); 
        this.progresPanel.setProgressText(this.getTotalProgressText()); 
        this.progresPanel.setTimeText(getTotalTimeText()); 
        
        // current: 
        this.currentTF.setText(subTask); 
        this.subProgresPanel.setProgress(monitorStats.getSubTaskProgress());
        this.subProgresPanel.setProgressText(this.getCurrentProgressText()); 
        this.subProgresPanel.setTimeText(getSubTimeText());

        String title="?";
        // darn null pionters: 
        if ((this.vfsTransferInfo.getSource()!=null) && (this.vfsTransferInfo.getDestination()!=null))
            title="Transfering:"+(this.vfsTransferInfo.getSource().getBasename())
                 +" to "+vfsTransferInfo.getDestination().getHostname()
                 +" (method="+vfsTransferInfo.getActionType()+")"; 
        	 
        
        this.titleTextField.setText(title);
        //this.setTitle(title);
    }

    private String getTotalTimeText()
    {
        String timestr=presentation.timeString(vfsTransferInfo.getTime(),false);
        
        long eta=this.monitorStats.getETA();
        
        if (eta<0)
        	timestr+=" (?)";
        else if (eta==0) 
        	timestr+= " (done)";
        else
        	timestr+=" ("+presentation.timeString(eta,false)+")";
        
        return timestr; 
        
    }


    private String getSubTimeText()
    {
        String timestr=presentation.timeString(monitorStats.getSubTaskDeltaTime(),false);
        
        long eta=this.monitorStats.getSubTaskETA(); 
        
        if (eta<0)
            timestr+=" (?)";
        else if (eta==0) 
            timestr+= " (done)";
        else
            timestr+=" ("+presentation.timeString(eta,false)+")";
        
        return timestr; 
        
    }
    private void initGUI() 
    {
        
        try 
        {
//            BorderLayout thisLayout = new BorderLayout();
//            thisLayout.setHgap(8);
//            thisLayout.setVgap(8);
//            this.setLayout(thisLayout);
            FormLayout thisLayout = new FormLayout(
                    "max(p;5dlu), max(p;16dlu), 5dlu, max(p;160dlu):grow, center:p:grow, max(p;5dlu)", 
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;15dlu), 5dlu, max(p;11dlu), max(p;15dlu), p:grow, 0px, 8px");
            this.setLayout(thisLayout);
            //transferInfo.setPreferredSize(new java.awt.Dimension(330, 80));
            {
                titleTextField = new JTextField();
                this.add(titleTextField, new CellConstraints("2, 2, 3, 1, default, default"));
                titleTextField.setText("Transfer Title");
                titleTextField.setBackground(new java.awt.Color(
                        229,
                        229,
                        229));
            }
            {
                sourceLabel = new JLabel();
                this.add(sourceLabel, new CellConstraints("2, 4, 1, 1, right, default"));
                    sourceLabel.setText("source:");
                }
                {
                    destLabel = new JLabel();
                    this.add(destLabel, new CellConstraints("2, 5, 1, 1, right, default"));
                    destLabel.setText("dest:");
                }
                {
                    sourceTF = new JTextField();
                    this.add(sourceTF, new CellConstraints("4, 4, 2, 1, default, default"));
                    sourceTF.setText("Source"); 
                }
                {
                    destTF = new JTextField();
                    this.add(destTF, new CellConstraints("4, 5, 2, 1, default, default"));
                    destTF.setText("Destination");
                }
            {
                this.add(getProgresPanel(), new CellConstraints("2, 6, 4, 1, default, default"));
                this.add(getSubProgresPanel(), new CellConstraints("2, 9, 4, 1, default, default"));
                this.add(getCurrentTF(), new CellConstraints("4, 8, 2, 1, default, default"));
                this.add(getCurrentLbl(), new CellConstraints("2, 8, 1, 1, default, default"));
                this.add(getCurrentPanel(), new CellConstraints("3, 8, 1, 1, default, default"));
            }

        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
   
     
      /** return progress information */ 
    public String getTotalProgressText()
    {
        VFSTransfer info=vfsTransferInfo;
        
        String progstr="";
        
        if (info.getTotalSources()>0) 
            progstr+="Transfer "+info.getSourcesDone()+" of "+info.getTotalSources(); 
        
        String speedStr=sizeString((int)monitorStats.getTotalSpeed())+"B/s"; 
        String amountStr=sizeString(info.getTotalWorkDone())+"B (of "+sizeString(info.getTotalWorkTodo())+"B)";
        
        if (info.isDone())
        {
            if (info.hasError())
            {
                return "Error!"; 
            }
            
            // Final Times. no progress strings 
            String finalStr="Done:"+amountStr; 
            
            finalStr+=" ("+speedStr+")"; 
            
            finalStr+=" in "+presentation.timeString(monitorStats.getTotalDoneTime(),false);
            
            return finalStr; 
        }
       
        progstr+=", "+amountStr; 
        
        // TransferSpeed ONLY for VFS Transfers !
        progstr+=" ("+speedStr+")";
        
        return progstr;
    }
    
    /** return progress information */ 
    public String getCurrentProgressText()
    {
        VFSTransfer info=vfsTransferInfo;
        
        String progstr="";
        
        if (info.isDone())
        {
            return "Done.";
        }
        
        // Print Current transfer info:
        
        String doneStr="?"; 
        String todoStr="?";
          
        if (info.getSubTaskTodo()>=0)
        {
            todoStr=""+sizeString(info.getSubTaskTodo()); 
        }
          
        if (info.getSubTaskDone()>=0)
        {
            doneStr=""+sizeString(info.getSubTaskDone()); 
        }
          
        progstr= doneStr+"B (of "+todoStr+"B)";
        progstr+=" ("+sizeString((int)this.monitorStats.getSubTaskSpeed())+"B/s)";  
                 
        return progstr;
    }


    private String sizeString(long size)
    {
    	if (size<0) 
    		return "?";
    	
        return presentation.sizeString(size,true,1,1);
    }
    
    
    public void dispose()
    {
 
    }
    
    private ProgresPanel getProgresPanel() 
    {
        if(progresPanel == null) 
        {
            progresPanel = new ProgresPanel();
            progresPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        return progresPanel;
    }
    
    private ProgresPanel getSubProgresPanel() 
    {
        if(subProgresPanel == null) 
        {
            subProgresPanel = new ProgresPanel();
            subProgresPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        return subProgresPanel;
    }
    
    private JTextField getCurrentTF() {
        if(currentTF == null) {
            currentTF = new JTextField();
            currentTF.setText("Current: ...");
        }
        return currentTF;
    }
    
    private JLabel getCurrentLbl() {
        if(currentLbl == null) {
            currentLbl = new JLabel();
            currentLbl.setText("Current:");
        }
        return currentLbl;
    }
    
    private JPanel getCurrentPanel() {
        if(currentPanel == null) {
            currentPanel = new JPanel();
        }
        return currentPanel;
    }

}
