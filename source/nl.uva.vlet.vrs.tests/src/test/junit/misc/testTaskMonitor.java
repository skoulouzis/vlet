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
 * $Id: testTaskMonitor.java,v 1.4 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit.misc;

import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.tasks.MonitorStats;
import test.junit.VTestCase;

/**
 * Experimental Unit Class Tester
 * 
 * @author P.T. de Boer
 */
public class testTaskMonitor extends VTestCase
{
    // VAttribute attribute=null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected void setUp()
    {
        // VAttribute=new VAttribute((String)null,(String)null,(String)null);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    public void testTaskMonitor1()
    {
        DefaultTaskMonitor tmon = new DefaultTaskMonitor();

        try
        {
            // proper way to start a task
            // tmon.setTotalWorkTodo(1000);
            tmon.startTask("Task1", 1000);

            // 50%
            Thread.sleep(1000);
            tmon.updateWorkDone(500);
            MonitorStats monstat = new MonitorStats(tmon);

            message(" delta work done =" + (tmon.getTotalWorkDone()));
            message(" delta time      =" + (tmon.getTotalWorkDoneLastUpdateTime() - tmon.getStartTime()));
            message(" speed (1)       =" + monstat.getTotalSpeed());

            // 100%
            Thread.sleep(1000);
            tmon.updateWorkDone(1000);
            message(" delta work done =" + (tmon.getTotalWorkDone()));
            message(" delta time      =" + (tmon.getTotalWorkDoneLastUpdateTime() - tmon.getStartTime()));
            message(" speed (2)       =" + monstat.getTotalSpeed());

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}
