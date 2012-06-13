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
 * $Id: LocalExec.java,v 1.4 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */
// source: 

package nl.uva.vlet.exec;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;

/**
 * Helper class for local execution of script/commands etc. Is Factory class for
 * LocalProcess.
 * 
 * @author P.T. de Boer
 */
public class LocalExec
{

    /**
     * Direct execute command and return result as String array.
     * <p>
     * The String array cmds[] holds the command and argument to execute.
     * cmds[0] is the command to execute and cmds[1]...cmds[n] are the
     * arguments. Method blocks until process has terminated!
     * <p>
     * Methods returns String array result[] which has at:
     * <li>result[0] complete output of stdout
     * <li>result[1] complete output of stderr;
     * <li>result[2] has the exit value in String value.
     * <p>
     * This method assumes no big output of text. Resulting String array (or
     * array elements) might be null upon error.
     */
    public static String[] execute(String cmds[]) throws VlException
    {
        Global.debugPrintf(LocalExec.class, "Executing command:%s\n", cmds[0]);
        String result[] = new String[3];

        // try
        {
            // new empty process:
            LocalProcess proc = new LocalProcess();
            // capture stderr, stdout
            proc.setCaptureOutput(true, true);
            // execute command and wait:
            proc.execute(cmds, true);
            // get stdout,stderr
            String stdout = proc.getStdout();
            String stderr = proc.getStderr();

            // Global.debugPrintf(LocalExec.class,
            // "After command, stdout=%s\n",stdout);
            // Global.debugPrintf(LocalExec.class,
            // "After command, sterr=%s\n",stderr);

            // no postprocessing, this is simpleExec (this also speeds up
            // processing!):
            /*
             * proc.waitFor(); int exit=proc.exitValue();
             * 
             * if (exit!=0) {
             * Global.errorPrintln(Global.class,"Warning: Command:"
             * +cmds[0]+"exit status="+exit);
             * Global.errorPrintln(Global.class,"Warning: Command: stderr="
             * +result[1]); }
             */

            int exit = proc.getExitValue();

            result[0] = stdout;
            result[1] = stderr;
            result[2] = "" + exit;

            dispose(proc);

            return result;
        }
    }

    private static void dispose(LocalProcess proc)
    {
        try
        {
            proc.getStdinStream().close();
        }
        catch (Exception e)
        {
        }
        try
        {
            proc.getStdoutStream().close();
        }
        catch (Exception e)
        {
        }
        try
        {
            proc.getStderrStream().close();
        }
        catch (Exception e)
        {
        }

        proc.destroy();
    }

    /**
     * @throws VlException
     * @see LocalExec#execute(String[])
     */
    static public String[] simpleExecute(String cmds[]) throws VlException
    {
        return execute(cmds);
    }

    /**
     * Execute cmds[0] and return Process object.
     * 
     * Returns Process object of terminated process or when wait=false the
     * Process object of running process.
     * 
     * @param wait
     *            : wait until process completes.
     */
    public static LocalProcess execute(String cmds[], boolean wait) throws VlException
    {
        Global.debugPrintf(LocalExec.class, "Executing command:%s\n", cmds[0]);

        // try
        {
            // new empty process:
            LocalProcess proc = new LocalProcess();
            // capture stderr, stdout
            proc.setCaptureOutput(true, true);
            // execute command and wait:
            proc.execute(cmds, wait);

            return proc;
        }
   }

}
