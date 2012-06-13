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
 * $Id: TaskMonitorDialog.java,v 1.8 2011-04-18 12:27:26 ptdeboer Exp $  
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

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.tasks.ITaskMonitorListener;
import nl.uva.vlet.tasks.MonitorEvent;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vrs.VRSTaskWatcher;

public class TaskMonitorDialog extends javax.swing.JDialog 
    implements ActionListener,WindowListener, ITaskMonitorListener
{
	
    private static final long serialVersionUID = -6758655504973209472L;

  
   
    // ========================================================================
    //
    // ========================================================================
    
    private TaskMonitorPanel monitorPanel;
    private JTextArea logText;
    private JScrollPane logSP;
    private JPanel LogPanel;
    //private BrowserController browserController;
    private JPanel buttonPanel;
    private JButton okButton;
    private JButton cancelButton;
    private ActionTask actionTask;
    private DockingPanel dockingPanel;
    private JScrollPane dockinSP;
    private int delay=-1;
    
    public TaskMonitorDialog(JFrame frame) 
    { 
        super(frame);
        UIPlatform.getPlatform().getWindowRegistry().register(this);
        initGUI();
    }
    
    public TaskMonitorDialog(JFrame frame, ActionTask actionTask)
    {
        super(frame);
        UIPlatform.getPlatform().getWindowRegistry().register(this);
    	this.actionTask=actionTask; 
    	
        initGUI();
        initMonitoring(); 
    }
    
    private void initMonitoring()
    {
        this.monitorPanel.setMonitor(actionTask.getTaskMonitor());
        actionTask.getTaskMonitor().addMonitorListener(this); 
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
                LogPanel = new JPanel();
                getContentPane().add(LogPanel, BorderLayout.CENTER);
                BorderLayout LogPanelLayout = new BorderLayout();
                LogPanel.setLayout(LogPanelLayout);
                LogPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                {
                    logSP = new JScrollPane();
                    LogPanel.add(logSP, BorderLayout.CENTER);
                    {
                        // Warning: do NOT set size of LogText/ScrollPAne to allow auto-resize!
                        logText = new JTextArea();
                        logSP.setViewportView(logText);
                        logText.setText("");
                    }
                }
            }
            
            {
                buttonPanel = new JPanel();
                getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
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
            {
                //dockinSP = new JScrollPane();
                //getContentPane().add(dockinSP, BorderLayout.NORTH);

                {
                    dockingPanel=new DockingPanel();
                    //dockinSP.setViewportView(dockingPanel);
                    getContentPane().add(dockingPanel,BorderLayout.NORTH); 
                    {
                        monitorPanel=new TaskMonitorPanel(); 
                        monitorPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                        dockingPanel.add(monitorPanel); 
                    }
                }
            }

            pack();
        }
        catch (Exception e)
        {
            Global.errorPrintStacktrace(e); 
        }
        
        this.addWindowListener(this); 
        
    }
    
    // === Static === 
    
  
    public static TaskMonitorDialog showTaskMonitorDialog(JFrame frame,ActionTask task,int delayMillis)
    {
        // for very short transfers do not show the dialog: 
        TaskMonitorDialog dialog = new TaskMonitorDialog(frame,task);
        dialog.setLocationRelativeTo(frame); 
        dialog.setDelay(delayMillis); 
        dialog.start(); // start monitoring, do not show yet. 
        //dialog.setVisible(true);
        return dialog; 
    }
    
    public void setDelay(int delay)
    {
        this.delay=delay; 
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource()==this.okButton)
        {
            close(); 
        }
        
        if (e.getSource()==this.cancelButton)
        {
            cancel(); 
        }
    }

    public void cancel()
    {
        this.actionTask.isAlive();
        this.actionTask.signalTerminate();
    }

    public void close()
    {
        stop(); 
        this.dispose(); 
    }
    
    ActionTask updateTask = new ActionTask(VRSTaskWatcher.getDefault(),"Monitor Updater Task") 
    {
         
        
        public void doTask() 
        {
            while (actionTask.isAlive())
            {
               update();
            
               if (actionTask.getTaskMonitor().isDone())
               {
                   break; 
               }
               try
               {
                   Thread.sleep(100);// 10fps 
               }
               catch (InterruptedException e)
               {
                   Global.errorPrintf(this,"Interrupted!"); 
                } 
            }
            
            okButton.setEnabled(true);
            cancelButton.setEnabled(false); 
            
            // task done: final update
            update();
        }

        @Override
        public void stopTask()
        {
            // vfstransfer must stop the transfer. 
            //task.setMustStop();
        }
    };
    
    private int prevHeight=-1;
    
    private long startTime=0;
   
    public void start()
    {
        this.startTime=System.currentTimeMillis(); 
        startUpdater(); 
    }
    
    protected void startUpdater()
    {
        updateTask.startTask(); 
    }
    
    public void stop()
    {
        if (this.updateTask.isAlive())
        {
            this.updateTask.signalTerminate();
        }
    }

    protected void update()
    {
        if (delay>=0)
        {
            if (this.isVisible()==false)
            {
                if (this.startTime+delay<System.currentTimeMillis())
                     this.setVisible(true); 
            }
        }
        
        this.monitorPanel.update();
        updateLog(); 
        JPanel panels[]=this.dockingPanel.getPanels(); 
        for (JPanel panel:panels)
        {
            if (panel instanceof TransferMonitorPanel)
            {
                ((TransferMonitorPanel)panel).update(); 
            }
            else if (panel instanceof TaskMonitorPanel)
            {
                ((TaskMonitorPanel)panel).update(); 
            }
            // if dockingpanel has an new monitor: resize Dialog!
            int newHeight=this.dockingPanel.getPreferredSize().height; 
            if (newHeight>prevHeight)
            {
                Dimension newSize=this.getPreferredSize(); 
                // do not shrink width. 
                if (newSize.width<this.getWidth()) 
                    newSize.width=this.getWidth(); 
                this.setSize(newSize); 
                this.prevHeight=newHeight; 
            }
            
            // skip
        }
    }

    public void updateLog()
    {
        // only do incremental updates: 

        String newText=this.actionTask.getTaskMonitor().getLogText(true);
        
        if (StringUtil.isEmpty(newText)==false) 
        {
            this.logText.append(newText); 
            //this.logText.revalidate(); not needed?
            //this.logSP.revalidate(); // trigger update of scrollbars!
        }
    }
    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e)
    {
        stop(); 
        close(); 
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e){}
    
    public void addSubMonitor(VFSTransfer transfer)
    {
        this.dockingPanel.add(new TransferMonitorPanel(transfer));
    }
    
    public void addSubMonitor(ITaskMonitor monitor)
    {
        this.dockingPanel.add(new TaskMonitorPanel(monitor));
    }

    @Override
    public void notifyMonitorEvent(MonitorEvent event)
    {
        switch (event.type)
        {
            case ChildMonitorAdded:
            {
                if (event.childMonitor instanceof VFSTransfer)
                {
                    this.addSubMonitor((VFSTransfer)event.childMonitor); 
                }
                else
                    this.addSubMonitor(event.childMonitor); 
            }
        }
        
    }

  
}
