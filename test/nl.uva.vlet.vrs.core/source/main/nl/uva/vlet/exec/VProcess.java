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
 * $Id: VProcess.java,v 1.3 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.exec;

import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;

/** 
 * Simple abstract process class (Under construction)
 * for local and remote command execution. 
 * 
 * @author P.T. de Boer. 
 */
public interface VProcess
{
	/**
	 * Returns stdout String after execution. 
	 * Do a waitFor() to make sure all input is read 
	 */
	public String getStdout();
	
	/**
	 * Returns stderr String after execution. 
	 * Do a waitFor() to make sure all input is read 
	 */	
	public String getStderr(); 
	
	/**
	 * Wait for the process to terminate.  
	 * @throws VlException */ 
	public void waitFor() throws VlException; 

	/**
	 * Terminate process immediately. 
	 * It is recommended to do a waitFor() after this command
	 * before issuing a getStderr(), getStdout() or getExitValue()
	 * so the process stats are updated correctly. 
	 */
	public void terminate();

	/**
	 * Check whether process still is running or not. 
	 *  
	 * @return true if process has ended. 
	 */
	public boolean isTerminated(); 
	
	/**
	 * Get exit value. 
	 * Process must have terminated before this method can be called.  
	 * Use isTerminated() first to check this.  
	 */
	public int getExitValue(); 
	
	/** Get Stdin OutputStream to write to: */ 
	public OutputStream getStdinStream()throws VlIOException;
	
	/** Get Stdout InputStream to read from. */ 
	public InputStream getStdoutStream() throws VlIOException; 
	
	/** Get Stderr InputStream to read from.*/ 
	public InputStream getStderrStream()throws VlIOException; 
	
	// public void addProcessListener(ProcessListener listener); 
	
}
