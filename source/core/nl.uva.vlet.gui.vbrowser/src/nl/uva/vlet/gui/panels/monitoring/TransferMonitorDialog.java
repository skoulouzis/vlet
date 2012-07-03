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
 * $Id: TransferMonitorDialog.java,v 1.6 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.monitoring;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.MonitorStats;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSTaskWatcher;


public class TransferMonitorDialog extends javax.swing.JDialog implements ActionListener
{
    private static final long serialVersionUID = -584814412020406751L;
    
    private JPanel buttonPanel;
    private JButton okButton;
    private TransferMonitorPanel transferPanel;
    private JButton cancelButton;
    private JTextArea logText;
    private VFSTransfer vfsTransferInfo;
    private JScrollPane logScrollPane;
    private long delay;
    
    private boolean suspended=false;
    ActionTask updateTask = null; 
    
    // Not yet: 
    // private ITaskSubTaskMonitor vfsTransferInfo; 
    private BrowserController browserController;

    Presentation presentation=Presentation.getDefault();
    // new monitor statistics object to move shared 
    // code between VFSTransfer and default Task monitor 
    private MonitorStats monitorStats=null;  
    
    public TransferMonitorDialog(JFrame frame) 
    { 
        super(frame);
        init();
    }
       
    public TransferMonitorDialog(BrowserController bc, VFSTransfer transfer)
    {
        super(BrowserController.getMasterFrame());
        this.vfsTransferInfo=transfer;
        this.monitorStats=new MonitorStats(transfer);
        this.browserController=bc; 
        
        init();
    }
    
    private void init()
    {
        UIPlatform.getPlatform().getWindowRegistry().register(this);
    	this.setLocationRelativeTo(null); 
    	initGUI();
    	// initial update: 
        update(); 
        //
        initUpdateTask();
    }
    
  
    
    protected void initUpdateTask()
    {
        updateTask=new ActionTask(VRSTaskWatcher.getDefault(),"TransferMonitorDialog.updateTask") 
        {
            public void doTask() 
            {
                while (vfsTransferInfo.isDone()==false)
                {
                    if (suspended==true)
                        return; 
                        
                    // if (isBackground()==false)
                    {
                       update();
                    
                       // delayed dialog: 
                       if ((vfsTransferInfo.getTime()>delay) && (isVisible()==false)) 
                       {
                           setVisible(true);
                       }
                    
                       if (vfsTransferInfo.isDone())
                       {
                          
                           if (vfsTransferInfo.hasError())
                           {
                               browserController.handle(vfsTransferInfo.getException());
                           }
                        
                           okButton.setEnabled(true);
                           cancelButton.setEnabled(false); 
                       }
                    }
                    
                       
                   try
                   {
                       Thread.sleep(100);// 10fps 
                   }
                   catch (InterruptedException e)
                   {
                       System.out.println("***Error: Exception:"+e); 
                       e.printStackTrace();
                    } 
                }
                // task done: final update
                update();
            }
    
            @Override
            public void stopTask()
            {
                // stop THIS task! (must use start() again to continue!) 
                suspended=true; 
            }
        };
        
    }    
  
    /** Restart the update task */ 
    public synchronized void start()
    {
        String title="Transfering:"+vfsTransferInfo.getSourceText()+" to "+vfsTransferInfo.getDestination().getHostname()
                ;//+":"+vfsTransferInfo.getStatus(); 
            
        this.setTitle(title);
            
        suspended=false; 
        updateTask.startTask(); 
    }
    
    
    protected void update()
    {
        this.transferPanel.update(); 

        // only do incremental updates: 
        String newText=vfsTransferInfo.getLogText(true); 
        if (StringUtil.isEmpty(newText)==false)  
        {
            this.logText.append(newText);  
            //this.logText.revalidate(); 
            //this.logScrollPane.revalidate(); // trigger update of scrollbars!
        }
        
        if (this.vfsTransferInfo.isDone())
        {
          this.cancelButton.setEnabled(false); 
          this.okButton.setEnabled(true);
        }
        
    }
    
    private void initGUI() 
    {
        try 
        {
            BorderLayout thisLayout = new BorderLayout();
            thisLayout.setHgap(8);
            thisLayout.setVgap(8);
            getContentPane().setLayout(thisLayout);
            {
                transferPanel = new TransferMonitorPanel(this.vfsTransferInfo); 
                getContentPane().add(transferPanel, BorderLayout.NORTH);
            }
            {
                logScrollPane = new JScrollPane();
                getContentPane().add(logScrollPane, BorderLayout.CENTER);
                {
                    logText = new JTextArea();
                    logScrollPane.setViewportView(logText);
                    logText.setText("Logger");
                    //logText.setPreferredSize(new java.awt.Dimension(343, 58));
                    logText.setEditable(false);
                    logText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    //NOT logText.setPreferredSize(new java.awt.Dimension(306, 19));
                }
            }
            {
                buttonPanel = new JPanel();
                getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                {
                    okButton = new JButton();
                    buttonPanel.add(okButton);
                    okButton.setText("OK");
                    okButton.addActionListener(this);
                    okButton.setEnabled(false); 
                }
                {
                    cancelButton = new JButton();
                    buttonPanel.add(cancelButton);
                    cancelButton.setText("Cancel");
                    cancelButton.addActionListener(this);
                }
            }
            
            this.pack();
            this.setSize(new Dimension(600,400)); 
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        this.addWindowListener(new DialogCloseListener()); 
    }
    
    public class DialogCloseListener implements WindowListener
    {
        @Override
        public void windowActivated(WindowEvent e) {}
        
        @Override
        public void windowClosed(WindowEvent e) {}

        @Override
        public void windowClosing(WindowEvent e)
        {
            TransferMonitorDialog.this.stop(); 
        }

        @Override
        public void windowDeactivated(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowOpened(WindowEvent e) {}
        
    }
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
         
         TransferMonitorDialog inst = new TransferMonitorDialog(null,transfer);
         inst.setModal(false); 
         inst.setVisible(true);
         inst.start();
         
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
     
     
    public static TransferMonitorDialog showTransferDialog(BrowserController bc,VFSTransfer transfer,long delay)
    {
        // for very short transfers do not show the dialog: 
        
        TransferMonitorDialog dialog = new TransferMonitorDialog(bc,transfer);
        dialog.setDelay(delay); 
        
        if (delay<=0)
        {
            dialog.setVisible(true);
            dialog.requestFocus(); 
        }
        
        dialog.start();
        return dialog; 
    }

    private void setDelay(long millis)
    {
        this.delay=millis;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource()==this.okButton)
        {
            dispose();
        }
        
        if (e.getSource()==this.cancelButton)
        {
        	// stop already initiated
        	if (vfsTransferInfo.getMustStop()==true)
        		cancelButton.setEnabled(false); 
        	
            this.vfsTransferInfo.setIsCancelled();
        }
    }

//    /** return progress information */ 
//    public String getTotalProgressText()
//    {
//        VFSTransfer info=vfsTransferInfo;
//        
//        String progstr="";
//        
//        if (info.getTotalSources()>0) 
//            progstr+="Transfer "+info.getSourcesDone()+" of "+info.getTotalSources(); 
//        
//        String speedStr=sizeString((int)monitorStats.getTotalSpeed())+"B/s"; 
//        String amountStr=sizeString(info.getTotalWorkDone())+"B (of "+sizeString(info.getTotalWorkTodo())+"B)";
//        
//        if (info.isDone())
//        {
//            if (info.hasError())
//            {
//                return "Error!"; 
//            }
//            
//            // Final Times. no progress strings 
//            String finalStr="Done:"+amountStr; 
//            
//            finalStr+=" ("+speedStr+")"; 
//            
//            finalStr+=" in "+presentation.timeString(monitorStats.getTotalDoneTime(),false);
//            
//            return finalStr; 
//        }
//       
//        progstr+=", "+amountStr; 
//        
//        // TransferSpeed ONLY for VFS Transfers !
//        progstr+=" ("+speedStr+")";
//        
//        return progstr;
//    }
    
//    /** return progress information */ 
//    public String getCurrentProgressText()
//    {
//        VFSTransfer info=vfsTransferInfo;
//        
//        String progstr="";
//        
//        if (info.isDone())
//        {
//            return "Done.";
//        }
//        
//        // Print Current transfer info:
//        
//        String doneStr="?"; 
//        String todoStr="?";
//        
//        if (info.getSubTaskTodo()>=0)
//        {
//            todoStr=""+info.getSubTaskTodo();
//        }
//        
//        if (info.getSubTaskDone()>=0)
//        {
//            doneStr=""+info.getSubTaskDone();
//        }
//        
//        progstr= doneStr+"B ("+todoStr+"B)";
//        
//        progstr+=" ("+sizeString((int)this.monitorStats.getSubTaskSpeed())+"B/s)";  
//                 
//        return progstr;
//    }


    private String sizeString(long size)
    {
    	if (size<0) 
    		return "?";
    	
        return presentation.sizeString(size,true,1,1);
    }
    
    /**
     * Stop the update task. The running Thread is stopped. Use start() again to
     * restart the update task */ 
    public synchronized void stop()
    {
        suspended=true;
        
        if (this.updateTask!=null)
        {
            if (this.updateTask.isAlive())  
                this.updateTask.signalTerminate(); 
        }
    }
    
    public void dispose()
    {
        stop();
        this.transferPanel.dispose(); 
        super.dispose(); 
    }
    
}
