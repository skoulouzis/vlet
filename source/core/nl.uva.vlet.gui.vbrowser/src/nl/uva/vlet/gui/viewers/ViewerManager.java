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
 * $Id: ViewerManager.java,v 1.5 2011-05-09 14:26:34 ptdeboer Exp $  
 * $Date: 2011-05-09 14:26:34 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskSource;
import nl.uva.vlet.vrl.VRL;

public class ViewerManager
{
    private static ViewerWatcher externalViewerWatcher=new ViewerWatcher();
    
    /** Default framelistener handle window close events */ 
    public class ViewerFrameListener extends WindowAdapter implements ComponentListener
    {
        JFrame frame=null; 
        ViewerManager manager;
        
        public ViewerFrameListener(JFrame frame,ViewerManager manager)
        {
            this.manager=manager;
            this.frame=frame;  
        }
        
        public void windowClosing(WindowEvent e)
        {
            manager.handleWindowClosingEvent(e); 
        }
        
        public void windowOpened(WindowEvent e){}
        public void windowClosed(WindowEvent e){}
        public void windowIconified(WindowEvent e){}
        public void windowDeiconified(WindowEvent e){}
        public void windowActivated(WindowEvent e){}
        public void windowDeactivated(WindowEvent e){}
        // @Override
        public void componentHidden(ComponentEvent e){}
        //@Override
        public void componentMoved(ComponentEvent e){}
       // @Override
        public void componentResized(ComponentEvent e){}
        // @Override
        public void componentShown(ComponentEvent e){}
    }

    // =======================================================================
    // Static Helpers methods 
    // ========================================================================
    public static Frame getViewerFrame(IMimeViewer viewr)
    {
        Component comp = viewr.getViewComponent(); 
        if (comp instanceof JComponent) 
        {
            Container topComp=((JComponent)comp).getTopLevelAncestor(); 
            if (topComp instanceof Frame)
            {
                return ((Frame)topComp);  
            }
        }
     
        return null;
    }

    // =======================================================================
    // Instance  
    // ========================================================================
    
	//private MasterBrowser browserController=null; 
	private ITaskSource taskSource=null; 
	//private ViewerPlugin viewer=null; 
	private IMimeViewer viewer=null;
	private boolean isStopping=false;
    private ActionTask stopTask=null;
    private ActionTask startTask=null;

	public ViewerManager(MasterBrowser controller,ViewContext context, ViewerPlugin viewer)
	{
		//this.browserController=controller;
		this.taskSource=controller;
		this.viewer=viewer; 
		if (context==null)
		    throw new NullPointerException("ViewContext can not be null");
		viewer.setViewContext(context); 
	}

	 /**
     * This method is called by the frame listener when a window closing 
     * event is received.
     * Default implementation is to perform signalFinalize(); 
     * Override this method if the Viewer cannot be disposed.  
     */
	public void handleWindowClosingEvent(WindowEvent e)
    {
	    //debug("handleWindowClosingEvent:"+e);
	    // finalize in background: 
	    signalFinalize();
    }

    public boolean hasViewer() 
	{
		return (viewer!=null); 
	}

	public IMimeViewer getViewer() 
	{
		return viewer;
	}
	
	public boolean hasViewer(Class<? extends ViewerPlugin> otherClass) 
	{
		if (viewer==null)
			return false; 
		
		return viewer.getClass().equals(otherClass);
	}

	public void startStandAlone(VRL location) throws VlException 
	{
		viewer.getViewContext().setStartInStandaloneWindow(true);
		// start viewer, use external viewer watcher but provide MasterBrowser.
		startForLocation(location);
	}
	
	public void asyncUpdateLocation(final VRL location) 
	{
		viewer.setVRL(location);
		// asynchronous update ! 
	
		final IMimeViewer fviewer = viewer;

		ActionTask updateTask = new ActionTask(taskSource, "updateTask for location:" + location)
		{
			public void doTask() throws VlException
			{
				fviewer.updateLocation(location);
			}

			@Override
			public void stopTask()
			{
				fviewer.stopViewer();
			}
		};

		updateTask.startTask();
	}

		
	public void signalStop()
	{
		if (viewer==null)
			return;
	
		// block double stop requests: 
		
		if (isStopping==false)
			isStopping=true;
		else
			return;
		// run stop in a seperate thread:
		stopTask = new ActionTask(taskSource,"Stopping viewer:" + this)
		{
			public void doTask()
			{
				try
				{
					viewer.stopViewer();
					// transition from isStopping => hasStopped 
					isStopping=false; 
				}
				catch (Exception e)
				{
					handle(e); 
				}
			}

			public void stopTask()
			{
			}; // nothing to stop/dispose:this IS already a stop/dispose task!!!  
		};

		stopTask.startTask();
	}
	
	private void handle(Exception e) 
	{
		taskSource.handle(e); 
	}

	public void startForLocation(VRL location) throws VlException
	{
	    startFor(location,null,null); 
	}
	  
	/** 
     * Create or update view panel with VRL.<br>
     * Starts in new thread.
     * This is the method called by the main browser to start the viewer.
     * <p>  
     * This method first calls initViewer() in the main swing event thread so 
     * subclassed viewers MUST return from this method. 
     * Then in a seperate thread the startViewer() method is called.  
     * Subclassed viewers don't have to return from this thread or can done
     * background tasks.  
     *   
     * <br>  
     * @throws VlException */
    final public void startFor(final VRL location,final String optionalMethodName,final ActionContext actionContext)
            throws VlException
    {
        // === 
        // Pre: Context: Startup environmnet 
        // === 
        
        if (this.startTask!=null)
        {
            throw new nl.uva.vlet.exception.VlException("Viewer Exception","Cannot start viewer twice"); 
        }
        
        // check viewer context: 
        if (viewer.getViewContext()==null) 
        {
            Global.errorPrintf(this,"Warning: Viewer context not set: creating default for:%s\n",this); 
            viewer.setViewContext(new ViewContext(null));  
        }
        
        // update viewer and context whith startup VRL
        if (location!=null) 
            viewer.setVRL(location);
        viewer.getViewContext().setStartupLocation(location);
        
        // update dynamic action context into viewer context 
        if (optionalMethodName!=null)
        {
            // put dynamic action + action context in ViewerContext.
            viewer.getViewContext().setDynamicAction(optionalMethodName,actionContext); 
            
        }
        
        // ======
        // Initialize Viewer during CURRENT thread 
        // ======
        
        initViewer();
        
        if (viewer.getViewContext().getStartInStandaloneWindow()==true)
        {
            // Disconnect from MasterBrowser ActionTasks and redirect to (external) 
            // viewer watcher 
            this.taskSource=getExternalViewerWatcher(); 
        }
        // ===
        // Start Viewer in background task ! 
        // ===
        
        this.startTask = new ActionTask(taskSource, "Starting viewer for:"
                + location)

        {
            public void doTask()
            {
                try
                {
                    // 
                    // Really start viewer :
                    //
                    
                    if (StringUtil.isEmpty(optionalMethodName)==false)
                    {
                        viewer.startViewer(location,optionalMethodName,actionContext); 
                    }
                    else
                    {
                        viewer.startViewer(location,null,null);
                    }
                    //
                    // Dynamic Action ! 
                    //
                }
                catch (Error e)
                {
                    Global.logException(ClassLogger.ERROR,this,e,"Exception\n"); 
                }
                catch (VlException e)
                {
                    handle(e);
                }
            }

            public void stopTask()
            {
                // applet: stop();
                // applet: destroy(); 
                viewer.stopViewer(); 
            };
        };

        startTask.startTask();
    }
    
    private ViewerWatcher getExternalViewerWatcher()
    {
        return externalViewerWatcher; 
    }

    /**
     * Invokes finalizeViewer() in a separate thread. 
     * @see #finalizeViewer(); 
     */

    final synchronized public void signalFinalize()
    {

        // run stop in a seperate thread:
        //System.err.println("--- Signal finalize ---");
        
        ActionTask stopTask = new ActionTask(this.taskSource,
                "Stopping viewer:" + this)
        {
            public void doTask()
            {
                try
                {
                    finalizeViewer(); 
                }
                catch (Error e)
                {
                    System.out.println("***Error: Exception:" + e);
                    e.printStackTrace();
                    // setText("*** Exception occured ***\n"+e.toString());
                }
            }

            public void stopTask()
            {
            }; // nothing to stop/dispose:this IS already a stop/dispose task!!!  
        };

        stopTask.startTask();
    }
    
   

    /** 
     * Initialize viewer and invoke initViewer() method of subclassed viewer.
     * This method will be performed during the GUI Event thread, 
     * so the Viewer can initialize it's GUI components.  
     * <p>
     * IViewer can be embedded as follows: 
     * <ul>
     * <li> Standalone in a JScrollPane in a JFrame
     * <li> Standalone in a JFrame. 
     * <li> Embedded in JScrollPane in the VBrowsers multitab panel. 
     * <li> Embedded in the VBrowser's multitab panel. 
     * </ul> 
     */
    final void initViewer()
    {
        if (viewer.getViewContext().getStartInStandaloneWindow() == true)
        {
            
            JFrame frame=null; 
            // JFrame
            {
                frame = new JFrame();
                frame.setSize(new Dimension(800, 600));
                GuiSettings.placeToCenter(frame);
            }

            if (viewer.haveOwnScrollPane() == false)
            {
                // embed viewer in scrollPane 
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(viewer.getViewComponent());
                frame.add(scrollPane);
            }
            else
            {
                frame.add(viewer.getViewComponent());
            }
            
            // Viewer
           // viewer.setFrame(frame); 
            viewer.initViewer();
            
            ViewerFrameListener listener = new ViewerFrameListener(frame,this);
            frame.addWindowListener(listener);
            frame.addComponentListener(listener); 
            
            frame.pack();

            frame.setVisible(true);
           // viewer.setFrame(frame); 
        }
        else
        {
            viewer.initViewer();
        }
    }
   
    
    /** Interrupt this viewer by interrupting the Viewer Task */
    final protected void interrupt()
    {
        if (startTask != null)
            startTask.interrupt();
    }

    /**
     * 
     * Calls stopViewer and dispoveViewer. 
     * Also tries to stop running thread and dispose the 
     * optional frame if the Viewer was started in a separate Frame. 
     */ 
    public void finalizeViewer()
    {
        viewer.stopViewer();

        // check whether star tasks still is running ! 
        if (startTask.isAlive() == true)
            startTask.interrupt(); // send interrupt signal
        
        // Wait 1s. 
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        viewer.disposeViewer();
        
        // check parent frame:
        if (viewer.getViewContext().getStartInStandaloneWindow()==true)
        {
            Frame frame=getViewerFrame(viewer); 
        
            if (frame != null)
            {
                frame.dispose();
            }
        }
    }
 
}
