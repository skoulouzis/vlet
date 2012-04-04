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
 * $Id: RunJunitTest.java,v 1.5 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import nl.uva.vlet.GlobalConfig;
import test.junit.vfs.testGFTP_passive;
import test.junit.vfs.testVFS;

/**
 * Wrapper to start Junit Test
 * 
 * @author ptdeboer
 */

public class RunJunitTest
{
    static boolean runAll = true;

    public static class TestHandler implements TestListener
    {

        public void addError(Test arg0, Throwable arg1)
        {
            Error("Error in test:" + arg0 + "=" + arg1);
        }

        public void addFailure(Test arg0, AssertionFailedError arg1)
        {
            Error("Failure in test:" + arg0 + "=" + arg1);
        }

        public void endTest(Test arg0)
        {
            Message("--- Ending test:" + arg0);

        }

        public void startTest(Test arg0)
        {
            Message("--- Starting test:" + arg0);
        }

    }

    public static String classlist[] =
    { testGFTP_passive.class.getCanonicalName() };

    public static void main(String args[])
    {
        if (runAll)
        {
            for (String className : classlist)
            {
                runJunit(className);
            }
        }

    }

    public static void runJunit(String className)
    {
        try
        {
            Class<TestCase> junitClass = (Class<TestCase>) RunJunitTest.class.getClassLoader().loadClass(className);
            TestCase testCase = junitClass.newInstance();

            testVFS.staticCheckProxy();
            GlobalConfig.setSystemProperty("gat.adaptor.path",
                    "/home/ptdeboer/workspace/mbuild/dist/lib/auxlib/javagat/lib/adaptors");

            // junit.textui.TestRunner.run(suite);
            // is short for:
            // ////////

            TestSuite suite = new TestSuite(junitClass);
            junit.textui.TestRunner runner = new junit.textui.TestRunner();
            TestResult result = runner.doRun(suite);

            // /////////
            // 
            // SWING (?Does not work)
            // 
            // junit.swingui.TestRunner runner=new junit.swingui.TestRunner();
            // runner.setSuite(className);
            // runner.runSuite();

            Message("--- Test Results ---");
            Message(" JUnit test className: " + className);
            Message("");
            Message(" nr of test cases    =" + testCase.countTestCases());
            Message(" nr of runs          =" + result.runCount());
            Message(" nr of errors        =" + result.errorCount());
            Message(" nr of errors        =" + result.failureCount());
            Message("---result---\n" + result);
        }
        catch (ClassNotFoundException e)
        {
            Error("Class not found:" + className);
            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            Error("Class not found:" + className);
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            Error("Internal Error: Couldn't instanciate:" + className);
            e.printStackTrace();
        }
    }

    private static void Message(String msg)
    {
        System.out.println(msg);
    }

    private static void Error(String msg)
    {
        System.err.println(msg);
    }

}
