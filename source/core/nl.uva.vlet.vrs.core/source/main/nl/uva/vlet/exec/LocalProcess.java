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
 * $Id: LocalProcess.java,v 1.4 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.tasks.ActionTask;

/** 
 * Process Information wrapper
 */ 
public class LocalProcess implements VProcess
{
	private Process process=null;

	private OutputStream stdinStream=null; // output to process 'stdin' 
	private InputStream stdoutStream=null; 
	private InputStream stderrStream=null; 

	private String stdoutString=null; 
	private String stderrString=null; 

	private boolean captureStdout=false;
	private boolean captureStderr=false;

	private String[] commands;

	private ActionTask streamReaderTask=null;

	private boolean isTerminated=false;
	
	public LocalProcess(Process process)
	{
		this.process=process; 
	}

	public LocalProcess()
	{
		
	}
	
	public void captureOutput(boolean captureOut,boolean captureErr) 
	{
		this.captureStdout=captureOut; 
		this.captureStderr=captureErr; 
	}

	public void waitFor() throws VlException
	{
		try
		{
			this.process.waitFor();
			
			// wait for completion of streamreader also ! 
			if (streamReaderTask!=null) 
				streamReaderTask.waitFor();
			
		}
		catch (InterruptedException e)
		{
			throw new VlException("InterruptedException",e); 
		}
		finally
		{
			isTerminated=true;
		}
	}

	public void setCaptureOutput(boolean captureStdout, boolean captureStderr)
	{
		this.captureStdout=captureStdout;
		this.captureStderr=captureStderr;
	}
	
	public void execute(String[] cmds) throws VlException
	{
		execute(cmds,true); 
	}
	
	public void execute(String[] cmds, boolean syncWait) throws VlException
	{
		setCommands(cmds); 
	
		if (commands==null)
			throw new VlException("Command string is empty !");

		try
		{
			this.process=Runtime.getRuntime().exec(commands);
		}
		catch (IOException e)
		{
			throw new VlIOException(e);   
		}
		
		// check termination directly after execute 
		isTerminated(); 
		
		// 
		// Optimation: 
		// when doing a (synchonized) wait, 
		// start streamReader in current thread ! 
		// this to avoid extra (thread) overhead when syncrhonized command
		// execution.
		
		if ((captureStdout==true) || (captureStderr==true))
		{
			startStreamWatcher(syncWait);
		}
		
		if (syncWait)
			waitFor();
	}

	// start streamreaders to read from stderr,stdout. 
	protected void startStreamWatcher(boolean syncWait) throws VlIOException
	{
		if ((this.captureStderr==false) && (this.captureStdout==false))
			return; // nothing to be done.  

		if (this.captureStdout)
			stdoutStream = process.getInputStream(); 

		if (this.captureStderr)
			stderrStream = process.getErrorStream();

		// start backgrounded stream reader 
		
		streamReaderTask=new ActionTask(ProcessTaskSource.getDefault(),"StreamWatcher")
		{
			boolean stop=false; 
			
			public void doTask()
			{
			
				
				// buf size is initial. Will Autoextend.
				StringWriter stdoutWriter=new StringWriter(1024); 
				StringWriter stderrWriter=new StringWriter(1024);

				int val1=-1,val2=-1; 

				try
				{
					do
					{   
						// alternate read from stdout and stderr: 
						if (stdoutStream!=null)
						{
							val1=stdoutStream.read();

							if (val1>=0)
							{
								stdoutWriter.write(val1);  
							}
						}

						if (stderrStream!=null)
						{
							val2=stderrStream.read();

							if (val2>=0)
							{
								stderrWriter.write(val2);  
							}
						}
					} while ((stop==false) && ( (val1>=0) || (val2>=0))); //continue until EOF on both streams
				}
				catch (IOException e)
				{
					this.setException(e);  
				}

				stdoutString=stdoutWriter.toString(); 
				stderrString=stderrWriter.toString();
			}

			@Override
			public void stopTask()
			{
				stop=true; 
			}
		};
		
		if (syncWait==false) 
			streamReaderTask.startTask();// background
		else
			streamReaderTask.run();// call run() directly 
		
	}

	void setCommands(String[] cmds)
	{
		this.commands=cmds; 
	}

	/**
	 * Returns stdout of terminated process. 
	 * If this method is called during execution of a process
	 * this method will return null. 
	 */
	public String getStdout()
	{
		return stdoutString;
	}
	/**
	 * Returns stderr of terminated process. 
	 * If this method is called during execution of a process
	 * this method will return null. 
	 */
	public String getStderr()
	{
		return stderrString;  
	}
	
	public int getExitValue()
	{
		return process.exitValue();
	}
	
	public void terminate()
	{
		process.destroy();
	}
	
	public boolean isTerminated()
	{
		// process has already terminated 
		if (isTerminated==true)
			return true;
		
		// dirty way to check whether proces hasn't exited 
		try
		{
			this.process.exitValue();
			this.isTerminated=true; 
		}
		catch (IllegalThreadStateException e)
		{
			this.isTerminated=false; 
		}
		
		return isTerminated;
	}

	public OutputStream getStdinStream()
	{
		stdinStream=process.getOutputStream(); 
		return stdinStream;  
	}
	
	public InputStream getStderrStream()
	{
		stderrStream=process.getErrorStream(); 
		return stderrStream; 
	}
	
	public InputStream getStdoutStream()
	{
		stdoutStream=process.getInputStream();	
		return stdoutStream; 
	}

	public void destroy()
	{
	    process.destroy(); 
	}
	
	/*public void addProcessListener(ProcessListener listener)
	{
		Todo
	}*/
}
