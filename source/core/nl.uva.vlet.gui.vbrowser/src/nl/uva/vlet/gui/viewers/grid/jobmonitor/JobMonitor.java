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
 * $Id: JobMonitor.java,v 1.4 2011-06-07 15:15:08 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:08 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.gui.widgets.NavigationBar;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;

public class JobMonitor extends ViewerPlugin
{
    public static String ACTION_REFRESH="refresh"; 
    public static String ACTION_START="start"; 
    public static String ACTION_STOP="stop"; 
       
    /** .vljids File Type */ 
    public static String mimetypes[]=
        {   
            // typos: 
            "application/vlemed/jobids",
            "application/vlemed-jobids",
            // new non vlemed: 
            "application/glite-jobids",
        };
    private JMenuBar menuBar;
    private JScrollPane jobTableSP;
    private JMenu helpMenu;
    private JMenuItem stopMi;
    private JMenuItem startMi;
    private JMenuItem refeshMi;
    private JMenu viewMenu;
    private JPanel jobMonitorPanel;
    private JPanel toolTopPanel;
    private JPanel menuBarPanel;
    private JMenu mainJobMenu;
    private JPanel topPanel;
    private NavigationBar locationToolbar;

    private JobMonitorController controller;
    private ResourceTable jobTable;

    @Override
    public String[] getMimeTypes() 
    {
        return mimetypes;
    }

    
    @Override
    public void disposeViewer()
    {
        if (this.controller!=null)
            this.controller.dispose();
        
        this.controller=null;
        this.jobTable=null; 
    }

    @Override
    public String getName()
    {
        return "JobMonitor"; 
    }

    @Override
    public void initViewer()
    {
        
        initGui(); 
    }

    @Override
    public void stopViewer()
    {
        this.controller.stopViewer(); 
    }

    @Override
    public void updateLocation(VRL loc) throws VlException
    {
        this.controller.updateLocation(loc); 
    }

    protected void initGui()
    {
        // JPanel has aldready a layout
        BorderLayout thisLayout = new BorderLayout();
        this.setLayout(thisLayout);
        this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
        this.setPreferredSize(new java.awt.Dimension(900, 300));

        {
            topPanel=new JPanel();
            this.add(topPanel,BorderLayout.NORTH);
            BorderLayout topPanelLayout = new BorderLayout();
            topPanel.setLayout(topPanelLayout);
            topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            topPanel.setPreferredSize(new java.awt.Dimension(896, 61));
            {
                {
                    menuBarPanel = new JPanel();
                    BoxLayout menuBarPanelLayout = new BoxLayout(menuBarPanel, javax.swing.BoxLayout.X_AXIS);
                    menuBarPanel.setLayout(menuBarPanelLayout);
                    topPanel.add(menuBarPanel, BorderLayout.NORTH);
                    menuBar = new JMenuBar();
                    menuBarPanel.add(menuBar);
                    {
                        mainJobMenu = new JMenu();
                        menuBar.add(mainJobMenu);
                        mainJobMenu.setText("JobMonitor");
                        {
                            startMi = new JMenuItem();
                            mainJobMenu.add(startMi);
                            startMi.setText("start");
                            startMi.setActionCommand(ACTION_START);
                            startMi.addActionListener(controller);  
                        }
                        {
                            stopMi = new JMenuItem();
                            mainJobMenu.add(stopMi);
                            stopMi.setText("stop");
                            stopMi.setActionCommand(ACTION_STOP);
                            stopMi.addActionListener(controller); 
                        }
                    }
                    {
                        viewMenu = new JMenu();
                        menuBar.add(viewMenu);
                        viewMenu.setText("View");
                        {
                            refeshMi = new JMenuItem();
                            viewMenu.add(refeshMi);
                            refeshMi.setText("refresh");
                            refeshMi.setActionCommand(ACTION_REFRESH);
                            refeshMi.addActionListener(this.controller); 
                        }
                    }
                    {
                        helpMenu = new JMenu();
                        menuBar.add(helpMenu);
                        helpMenu.setText("Help");
                    }

                }
                {
                    toolTopPanel = new JPanel();
                    BoxLayout toolTopPanelLayout = new BoxLayout(toolTopPanel, javax.swing.BoxLayout.X_AXIS);
                    toolTopPanel.setLayout(toolTopPanelLayout);
                    topPanel.add(toolTopPanel, BorderLayout.CENTER);
                    {
                        locationToolbar=new NavigationBar(NavigationBar.LOCATION_ONLY);
                        toolTopPanel.add(locationToolbar);
                        locationToolbar.addTextFieldListener(controller); 
                        //no up down back, etc. 
                        locationToolbar.setEnableNagivationButtons(false); 
                        locationToolbar.addNavigationButtonsListener(controller);  
                     }
                }
            }
        }
        {
            jobMonitorPanel = new JPanel();
            BorderLayout jobMonitorPanelLayout = new BorderLayout();
            jobMonitorPanel.setLayout(jobMonitorPanelLayout);
            this.add(jobMonitorPanel, BorderLayout.CENTER);
            {
                jobMonitorPanel.add(getJobTableSP(), BorderLayout.CENTER);
            }
        }

    }
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new JobMonitor(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public JobMonitor(Boolean initGUI) 
	{
		super();
		
		this.controller=new JobMonitorController(this);

		if (initGUI)
		    initGui(); 
	}
	
	public static void main(String args[])
	{
	    try
        {
	        
	        JobMonitor jobMonitor=new JobMonitor(true);
	        jobMonitor.startAsStandAloneApplication(new VRL("file:/home/ptdeboer/jobs/test.vljids")); 
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
	    
	}
	
	 public ResourceTable getJobTable()
	    {
	        if (jobTable==null)
	        {
	            // empty model: 
	            JobStatusDataModel model=new JobStatusDataModel(controller);
	            Presentation pres=model.getPresentation();
	            jobTable = new ResourceTable(model,pres);
	             
	            //jobTable.setPresentation(model.getPresentation()); 
	            
	            //jobTable.setPopupMenu(new ReplicaPopupMenu(this.controller));
	            // Presentation pres = replicaTable.getPresentation(); 
	        }
	        
	        return this.jobTable;
	    }
	 
	 private JScrollPane getJobTableSP() 
	 {
	     if(jobTableSP == null) 
	     {
	         jobTableSP = new JScrollPane();
             jobTableSP.setViewportView(this.getJobTable());
	     }
	     return jobTableSP;
	 }


    public void updateLocationBar(VRL loc)
    {
        this.locationToolbar.setLocationText(""+loc); 
        
    }


    public JobStatusDataModel getJobMonitorDataModel()
    {
        return (JobStatusDataModel)this.jobTable.getModel(); 
    }
}