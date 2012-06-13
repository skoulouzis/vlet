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
 * $Id: ProcessTaskSource.java,v 1.4 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.exec;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.tasks.ITaskSource;

/** 
 * Simple ProcessTasksource listener. 
 * This is not a TaskSource for actual started processes, but
 * for the Tasks started in the Java Process objects which watch
 * the started process. For example the streamReader task. 
 */ 
public class ProcessTaskSource implements ITaskSource 
{
    private static ClassLogger logger; 
    {
        logger=ClassLogger.getLogger(ProcessTaskSource.class); 
    }
    
	// === Class === // 
	
	static private ProcessTaskSource instance=new ProcessTaskSource(); 
	
	public static ProcessTaskSource getDefault()
	{
		return instance; 
	}
	
	// === Instance === // 
	
	private boolean hasTasks;

	public String getID()
	{
		return "ProcessTaskSource"; 
	}

	public void handle(Exception e)
	{
	    logger.logException(ClassLogger.ERROR,e,"ProcessTaskSource Exception:%s\n",this);  
	}

	public void messagePrintln(String msg)
	{
	    logger.infoPrintf("console:%s",msg); // no newline, this is a println message!
	}

	public void setHasTasks(boolean val)
	{
		hasTasks=val;
	}
	
	public boolean getHasTasks()
	{
		return hasTasks; 
	}
}
