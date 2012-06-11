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
 * $Id: ProxyNodeEventNotifier.java,v 1.3 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.proxyvrs;

import java.util.Vector;

import javax.swing.SwingUtilities;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.ResourceEventListener;
import nl.uva.vlet.vrs.VRSContext;

/**
 * This class buffers ResourceEvents and sends them to listeners
 * DURING the main swing event thread. 
 * This notifier synchronizes muli threaded events to one single thread,
 * since the GUI has only one event thread.
 * 
 * Concurrency note: This method uses the SwingUtils.invokeLater method so
 * that the ResourceListener event will be send DURING the Main GUI Event thread.
 * Access to the event buffer must sychronized. 
 * This buffer is also used as MutEx. 
 * The fireEvent method() can be used multi threaded and can be called
 * from other threads then the GUI thread. 
 */ 
public class ProxyResourceEventNotifier implements Runnable, ResourceEventListener
{
	// ========================================================================
	// Static
	// ========================================================================
	private static ClassLogger logger; 
	
	static
	{
		logger=ClassLogger.getLogger(ProxyResourceEventNotifier.class);
	}
	
	// ========================================================================
	// 
	// ========================================================================
    /**
     * Event Buffer.
     * Note: must synchronize access to buffer:
     */ 
     private Vector<ResourceEvent> events=new Vector<ResourceEvent>();
     
     /** Whether Notifier is running or scheduled for a run */ 
     private boolean notifierRunning=false;
   
     /**
      * Class proxyListeners vector.<br>
      * ProxyEventListeners in this vector will receive all events for all
      * ProxyTNodes
      */
     private Vector<ProxyResourceEventListener> proxyEventListeners = new Vector<ProxyResourceEventListener>();

     protected ProxyResourceEventNotifier(VRSContext context)
     {
    	 // ProxyModel listener listens to VRS Resource Events ! 
    	 context.addResourceEventListener(this); 
     }
     
    /**
     * Notify all listenerd that an event has occured. 
     * During the 'run' new events can be added to the events buffer. 
     * 
     */
    public void run()
    {
       ResourceEvent event=null;
       boolean haveMore=false;
       int nrEvents=0; 
        
        do
        {
          //get first from vector and consume:
          synchronized (events)
          {
              if (events.size()>0)
              {
                event=events.get(0);
                events.remove(0);
                nrEvents++;
              }
          }
        
          //assert:
          if ((event!=null) && (proxyEventListeners != null))
          {
              // callback event:
              if (event.getTargetListener()!=null)
              {
                  event.getTargetListener().notifyResourceEvent(event);
              }
              else
              //synchronized (proxyEventListeners)
              {
                  for (ProxyResourceEventListener l : proxyEventListeners)
                  {
                    logger.debugPrintf(" - notifying:%s\n",l);
                    // TermGlobal.messagePrintln(this,">>> Notifying:"+l); 
                    l.notifyProxyEvent(event);
                  }
              }
          }         
          // check if (new) events are pending in the buffer:
          // more events could be added
          synchronized(events)
          {
              haveMore=events.size()>0;
              // important: already set to false
              // since this was the last event
              notifierRunning=false;
          }
        }while(haveMore==true);
        
        logger.debugPrintf("Stopping notifier: nr events processed=%d\n",nrEvents);
    }

    /**
     * Appends event to event buffer and starts the notifier. 
     * This notifier will be started during Swings main event thread
     * 
     * @param event
     */
    public void fireEvent(ResourceEvent event)
    {
        // BrowserController.notifyGlobalProxyEvent(e);
        //TermGlobal.messagePrintln(ProxyTNode.class,"fireGlobalEvent:"+event);
        logger.debugPrintf("fireGlobalEvent:%s\n",event);
        boolean startNotifier=false;
        
        // sychronized access: 
        synchronized (events)
        {
            events.add(event);
            if (notifierRunning==false)
            {
                // block further invocations:
                notifierRunning=true;
                startNotifier=true;
            }
        }
        
        if (startNotifier==false)
        {
            Global.debugPrintf(this,"Notifier already running\n");
            return;
        }
        // Important: Since this might NOT be the Swing GUI Thread,
        // use invokeLater:
        logger.debugPrintf("Starting notifier\n");
        SwingUtilities.invokeLater(this); 
    }

    public void removeResourceEventListener(ProxyResourceEventListener listener)
    {
        synchronized (proxyEventListeners)
        {
            proxyEventListeners.remove(listener);
        }
    }
    
    public void addResourceEventListener(ProxyResourceEventListener listener)
    {
        synchronized (proxyEventListeners)
        {
            proxyEventListeners.add(listener);
        }
    }

    public void fireEventToListener(ResourceEventListener receiver, ResourceEvent event)
    {
        // specify listener so that during the event notificiation 
        // only that listeners is targeted:
        event.setTarget(receiver); 
        fireEvent(event); 
    }

    /** 
     * Receive VRS ResourceEvent and forward it to the ProxyModel 
     * even listeners 
     */
	public void notifyResourceEvent(ResourceEvent event)
	{
		fireEvent(event); 
	}
	
	public void dispose()
	{
		clear(); 
	}
	
	public void clear() 
	{
		synchronized(events)
		{
			events.clear(); 
			proxyEventListeners.clear();
		}
	}
}
