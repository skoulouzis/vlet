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
 * $Id: ProxyJobSubmitTest.java,v 1.3 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package tests;

import org.glite.jdl.JobAd;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wms.wmproxy.WMProxyAPI;

public class ProxyJobSubmitTest
{
    ProxyJobSubmitTest()
    {

    }

    /**
     * Prints the results
     */
    private static void printResult(JobIdStructType entry)
    {
        JobIdStructType children[] = null;
        int size = 0;
        if (entry != null)
        {
            // id
            System.out.println("jobID	= [" + entry.getId() + "]");
            // name
            System.out.println("name	= [" + entry.getName() + "]");

            // children
            children = (JobIdStructType[]) entry.getChildrenJob();
            if (children != null)
            {
                size = children.length;
                System.out.println("number of children = [" + size + "]");
                if (size > 0)
                {
                    for (int i = 0; i < size; i++)
                    {
                        System.out.println("child n. " + (i + 1));
                        System.out.println("--------------------------------------------");
                        printResult(children[i]);
                    }
                }
            }
            else
                System.out.println("no children");
        }
    }

    /*
     * Starts the test
     * 
     * @param url service URL
     * 
     * @param jdlFile the path location of the JDL file
     * 
     * @param delegationID the id to identify the delegation
     * 
     * @param propFile the path location of the configuration file
     * 
     * @param proxyFile the path location of the user proxy file
     * 
     * @param certsPath the path location of the directory containing all the
     * Certificate Authorities files
     * 
     * @throws.Exception if any error occurs
     */
    public static void runTest(String url, String proxyFile, String delegationId, String jdlFile, String certsPath)
            throws java.lang.Exception
    {
        // jdl
        String jdlString = "";
        // output results
        JobIdStructType result = null;
        WMProxyAPI client = null;
        // reads jdl
        JobAd jad = new JobAd();
        jad.fromFile(jdlFile);
        jdlString = jad.toString();

        // Prints out the input parameters
        System.out.println("TEST : JobSubmitr");
        System.out
                .println("************************************************************************************************************************************");
        System.out.println("WS URL	 		= [" + url + "]");
        System.out
                .println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("proxy			= [" + proxyFile + "]");
        System.out
                .println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("DELEGATION-ID		= [" + delegationId + "]");
        System.out
                .println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("JDL-FILE		= [" + jdlFile + "]");
        System.out
                .println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("JDL			= [  " + jdlString + "  ]");
        System.out
                .println("--------------------------------------------------------------------------------------------------------------------------------");
        if (certsPath.length() > 0)
        {
            System.out.println("CAs path		= [" + certsPath + "]");
            System.out
                    .println("--------------------------------------------------------------------------------------------------------------------------------");
            client = new WMProxyAPI(url, proxyFile, certsPath);
        }
        else
        {
            client = new WMProxyAPI(url, proxyFile);
        }
        // test
        System.out.println("Testing ....");
        result = client.jobSubmit(jdlString, delegationId);
        // test results
        if (result != null)
        {
            System.out.println("RESULT:");
            System.out.println("=======================================================================");
            System.out.println("Your job has been successfully submitted:");
            printResult(result);
            System.out.println("=======================================================================");
        }
        // end
        System.out.println("End of the test");
    }

    public static void main(String[] args) throws Exception
    {
        String url = "";
        String jdlFile = "";
        String proxyFile = "";
        String delegationID = "";
        String certsPath = "";
        // Reads the input arguments
        if ((args == null) || (args.length < 4))
        {
            throw new Exception(
                    "error: some mandatory input parameters are missing (<WebServices URL> <delegationID> <proxyFile> <JDL-FIlePath>  [CAs paths (optional)])");
        }
        else if (args.length > 5)
        {
            throw new Exception(
                    "error: too many parameters\nUsage: java <package>.<class> <WebServices URL> <delegationID> <proxyFile> <JDL-FIlePath>  [CAs paths (optional)]");
        }
        url = args[0];
        delegationID = args[1];
        jdlFile = args[2];
        proxyFile = args[3];
        if (args.length == 5)
        {
            certsPath = args[4];
        }
        else
        {
            certsPath = "";
        }

        System.setProperty("X509_PROXY_FILE", proxyFile);

        // Launches the test
        runTest(url, proxyFile, delegationID, jdlFile, certsPath);
    }
}
