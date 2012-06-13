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
 * $Id: ResourceEventNotifier.java,v 1.6 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskSource;

/**
 * Multi threaded VRS ResourceEvent notifier.
 */
public class ResourceEventNotifier implements ITaskSource 
{
	public static long defaultNotifyWaitTime=1*1000;  
	
	protected class Notifier implements Runnable
	{
		public static final long DEFAULT_WAITTIME=30*1000;
		
		private boolean mustStop;
		
		public Notifier()
		{
			
		}
		
		public void run()
		{
			Message("Starting notifier in thread"+Thread.currentThread().getId());
			// when restarted: mustStop must be reset !
			mustStop=false;
			 
			while(mustStop==false)
			{
				if (hasEvent()==false)
				{
					try
					{
						synchronized(this)
						{
							//Debug(">>> Waiting <<<");
							this.wait(DEFAULT_WAITTIME);
						}
					}
					catch (InterruptedException e)
					{
					    if (mustStop==false)
					    {
					        Global.logException(ClassLogger.ERROR,this,e,"wait(): Interrupted!");  
					        
					    }
					    Global.infoPrintf(this,"Stopping notifier.n");
					} 
				}
				
				// get next event out queue: 
				
				final ResourceEvent event = getNextEvent(); 
				if (event!=null)
				{
					Global.infoPrintf(this," +++ Notifying:%s\n",event); 
					notifyListeners(event); 
				}
				else
				{
					Global.infoPrintf(this,"--- Wake up: No Events ---\n");
				}
				
				// check if a new thread has started in case the current took to long: 
				if (Thread.currentThread()!=notifierThread)
				{
					// null means stop: 
					if (notifierThread==null)
						Global.warnPrintf(this,"*** Dispose(): Notifier Thread stopping:%s\n",Thread.currentThread());
					else
						Global.errorPrintf(this,"*** Duplicate Notifier Thread: Stopping current:%s\n",Thread.currentThread());
										
					mustStop=true; 
				}
			}// while
			
			Message("Stopping notifier in thread"+Thread.currentThread().getId());
			
		}
			
		void notifyListeners(final ResourceEvent event)
		{
			// get listeners in private array
			
			ResourceEventListener[] listeners = getListeners(); 
			
			for (ResourceEventListener listener:listeners)
			{
				final ResourceEventListener finalListener=listener; 
				
				ActionTask notifyTask=new ActionTask(ResourceEventNotifier.this,"Notifying event:"+event)
				{
					public void doTask()
					{
						try
						{
							notifyStartTime=System.currentTimeMillis(); 
							Debug("--- notifying listener ---\n"
								   +"listener  ="+finalListener+"\n" 
								   +"event     ="+event+"\n---"
							       +"starttime ="+Presentation.createNormalizedDateTimeString(notifyStartTime)
							       +"\n---"
								   ); 
							
							finalListener.notifyResourceEvent(event);
							//notifyEndTime=System.currentTimeMillis();
						} 
						catch (Throwable t)
						{
							Global.logException(ClassLogger.ERROR,this,t,"Exception during event notification\n");
						}
					}
					
					public void stopTask()
					{
						
					}
				};
				
				// notify in background !
				notifyTask.startTask();
				
				try
				{
					notifyTask.waitFor(defaultNotifyWaitTime);
				}
				catch (InterruptedException e)
				{
					Global.logException(ClassLogger.ERROR,this,e,"waitFor(): Interrupted!\n"); 
				}  			
			}
		}

		public void stop()
		{
			this.mustStop=true;
			// wake up:
			synchronized(this)
			{
				this.notifyAll();
			}
		}
		
		private void Message(String msg)
		{
			Global.infoPrintf(this,"%s\n",msg); 
		}	
	}
	
	private Vector<ResourceEvent> eventQueue = new Vector<ResourceEvent>();

	private Vector<ResourceEventListener>resourceEventListeners=new Vector<ResourceEventListener>();

	private Notifier notifier=null;

	private Thread notifierThread=null; 
 
	private long notifyStartTime=0;
	
	//private long notifyEndTime=0; 
		
	public ResourceEventNotifier()
	{
		this.notifier=new Notifier(); 
		start();
		// startTestMessages(); 
	}
	
	public void fire(ResourceEvent event)
	{
		Debug("fire event:"+event);
		schedule(event); 
	}
	
	private void Debug(String msg)
	{
		Global.debugPrintf(this,"%s\n",msg); 
	}

	public boolean hasEvent()
	{
		return (this.eventQueue.size()>0);
	}
	
	private void schedule(ResourceEvent event)
	{
		if (notifier==null)
		{
			Global.errorPrintf(this,"schedule() called after notifier has been disposed\n"); 
			return;
		}
		
		synchronized(eventQueue)
		{
			eventQueue.add(event); 
		}
		// wake up if sleeping :
		synchronized(notifier)
		{
			this.notifier.notifyAll();
		}
	}
	
	private ResourceEvent getNextEvent()
	{
		synchronized(this.eventQueue)
		{
			if (eventQueue.size()<=0)
				return null; 
			
			ResourceEvent event=this.eventQueue.get(0); 
			this.eventQueue.remove(0);
			return event; 
		}	
	}
	
	public void addListener(ResourceEventListener listener)
	{
		synchronized(resourceEventListeners)
		{
			this.resourceEventListeners.add(listener);
		}
	}
	
	public void removeListener(ResourceEventListener listener)
	{
		synchronized(resourceEventListeners)
		{
			this.resourceEventListeners.remove(listener);  
		}
	}
	
	public ResourceEventListener[] getListeners()
	{
		// no listeners may be added/removed during the following code: 
		synchronized (resourceEventListeners)
		{
			ResourceEventListener array[]=new ResourceEventListener[resourceEventListeners.size()];
			array=resourceEventListeners.toArray(array);
			return array; 
		}
	}

	public static void startTestMessages()
	{
		Runnable tester=new Runnable()
		{
			public void run()
			{
				while(true)
				{
					try
					{
						Thread.sleep(10000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					} 
					
					for (int i=1;i<10;i++)
						VRS.getRegistry().fireEvent(ResourceEvent.createMessageEvent(null,"*** TEST MESSAGE ***"));
				}
			}
		};
		
		Thread thread=new Thread(tester);
		thread.start();
	}

	public String getID()
	{
		return "Resource Event Notifier"; 
	}

	public void handle(Exception e)
	{
		Global.warnPrintf(this,"notifier exception:%s\n",e); 
	}

	public void messagePrintln(String msg)
	{
		Global.infoPrintf(this,"Message:%s\n",msg); 
	}

	public void setHasTasks(boolean val)
	{
		Global.infoPrintf(this,"Has tasks running:%s\n",val); 
	}
	
	// ========================================================================
	// Life Cycle Management
	// ========================================================================
	
	/**
	 * Stop and wake up current notifier 
	 */ 
	public synchronized void stop()
	{
	    if (this.notifier==null)
	    {
	        Global.warnPrintf(this,"stop() called after dispose()!\n");
	        return;
	    }
	    
		this.notifier.stop(); 
		
		synchronized(notifierThread)
		{
		    this.notifierThread.notifyAll();
		    this.notifierThread.interrupt();
		}
	}
	
	/**
	 * Start or restart the notifier. 
	 * When called twice, two threads can be started
	 * to notify events. 
	 * This way stopped or block threads can be 'bypassed' by
	 * starting a new thread.  
	 * The notifyer will check if the thread the notifier is running from is the 
	 * master thread. 
	 */
	public synchronized void start()
	{
		if (this.notifier==null)
	    {
	        Global.errorPrintf(this,"start() called after dispose()!\n");
	        return;
	    }
		
		this.notifierThread=new Thread(notifier);
		this.notifierThread.start();
	}
		
	/**
	 * Reset notifier. 
	 * Stop current running notifier, clears the event queue
	 * and restarts the notifier again.  
	 */
	public void reset()
	{
		stop(); 
		this.eventQueue.clear(); 
		start(); 
	}
	
	public void finalize()
	{
		dispose();
	}
	
	public void dispose()
	{
		stop(); 
		
		if (this.notifierThread!=null)
		{
			this.notifierThread.interrupt();
			this.notifierThread=null; 
		}
		
		//notifier should already have been stopped :
		if (notifier!=null)
		{
			this.notifier.mustStop=true;
			this.notifier=null; 
		}
		
	}
}
