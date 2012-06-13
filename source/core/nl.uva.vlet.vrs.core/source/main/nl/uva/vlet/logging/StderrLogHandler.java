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
 * $Id: StderrLogHandler.java,v 1.2 2011-04-18 12:00:41 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:41 $
 */
// source: 

package nl.uva.vlet.logging;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler for stderr output.
 */
public class StderrLogHandler extends Handler
{
    PrintStream stderrStream = null;

    public StderrLogHandler(PrintStream err)
    {
        stderrStream = err;
    }

    @Override
    public void close() throws SecurityException
    {

    }

    @Override
    public void flush()
    {
        stderrStream.flush();
    }

    @Override
    public void publish(LogRecord record)
    {
        Level level = record.getLevel();
        String name = record.getLoggerName();
        String format = record.getMessage();
        Object args[] = record.getParameters();

        String lvlStr;

        // Convert some Level to more appropriate names:
        if (level == Level.FINE)
            lvlStr = "DEBUG";
        else if (level == Level.FINER)
            lvlStr = "DEBUG2";
        else if (level == Level.FINEST)
            lvlStr = "DEBUG3";
        else if (level == Level.WARNING)
            lvlStr = "WARN ";
        else if (level == Level.SEVERE)
            lvlStr = "ERROR";
        else
            lvlStr = level.toString();

        // has message ?
        if (format != null)
            stderrStream.printf(lvlStr + ":" + name + ":" + format, args);

        // / print stacktrace of Exception
        Throwable t = record.getThrown();
        if (t != null)
        {
            // stderrStream.println("--- StackTrace ---");
            t.printStackTrace(stderrStream);
        }

    }

}
