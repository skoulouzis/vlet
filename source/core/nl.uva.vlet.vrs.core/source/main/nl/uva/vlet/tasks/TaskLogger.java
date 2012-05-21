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
 * $Id: TaskLogger.java,v 1.2 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.tasks;

import java.util.logging.Level;

import nl.uva.vlet.logging.FormattingLogger;
import nl.uva.vlet.logging.RecordingLogHandler;

/** 
 * Custom logger class for the TaskMonitor.
 * Use java.logging compatible (sub)class. 
 */
public class TaskLogger extends FormattingLogger
{
    private RecordingLogHandler handler;
    
    private Level defaultLevel=INFO; 
    
    public TaskLogger(String name)
    {
        super(name);
        this.handler=new RecordingLogHandler(); 
        this.addHandler(handler);
        // default level=info, but default logPrintf uses "ALL" anyway: 
        this.setLevel(defaultLevel);  
    } 

    /** Default logPrintf for TaksLogger */ 
    public void logPrintf(String format, Object... args)
    {
        // Default log level for task logger is INFO. 
        log(defaultLevel,format,args); 
    }

    public String getLogText(boolean incremental)
    {
        return handler.getLogText(incremental); 
    }
    
}
