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
 * $Id: testLocalExec.java,v 1.3 2011-05-02 13:28:46 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:46 $
 */ 
// source: 

package test.junit.vfs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exec.LocalExec;
import nl.uva.vlet.exec.LocalProcess;

public class testLocalExec
{
    public static void main(String args[])
    {
        testExecLS();
        testExecFF();

    }

    public static void testExecLS()
    {
        String cmds[] = new String[2];
        cmds[0] = "/bin/ls";
        cmds[1] = "-lrat";

        String result[];
        String lsout1 = null;
        String lserr1 = null;

        try
        {
            result = LocalExec.simpleExecute(cmds);

            lsout1 = result[0];
            lserr1 = result[1];

            verbose(1, "Stdout    =" + lsout1);
            verbose(1, "Stderr    =" + lserr1);
            verbose(1, "exit value=" + result[2]);
        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // again but now in background:

        LocalProcess proc;

        try
        {
            proc = LocalExec.execute(cmds, false);

            verbose(1, "after spawn: isTerminated=" + proc.isTerminated());
            proc.waitFor();
            verbose(1, "after waitFor(): isTerminated=" + proc.isTerminated());
            String lsout2 = proc.getStdout();
            String lserr2 = proc.getStderr();

            verbose(1, "Stdout    =" + lsout2);
            verbose(1, "Stderr    =" + lserr2);
            verbose(1, "exit value=" + proc.getExitValue());

        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void testExecFF()
    {
        String cmds[] = new String[2];
        cmds[0] = "/usr/bin/firefox";
        cmds[1] = "http://www.science.uva.nl";

        LocalProcess proc;

        try
        {
            proc = LocalExec.execute(cmds, false);

            verbose(1, "after spawn: isTerminated=" + proc.isTerminated());

            proc.waitFor();

            verbose(1, "Stdout    =" + proc.getStdout());
            verbose(1, "Stderr    =" + proc.getStderr());
            verbose(1, "exit value=" + proc.getExitValue());

        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void verbose(int i, String str)
    {
        System.out.println(str);
    }
}
