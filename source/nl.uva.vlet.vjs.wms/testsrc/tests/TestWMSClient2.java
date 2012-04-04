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
 * $Id: TestWMSClient2.java,v 1.8 2011-04-18 12:28:51 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:51 $
 */ 
// source: 

package tests;

import java.util.ArrayList;

import nl.uva.vlet.glite.OutputInfo;
import nl.uva.vlet.glite.WMLBConfig;
import nl.uva.vlet.glite.WMSClient;
import nl.uva.vlet.glite.WMLBConfig.WMSConfig;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrl.VRL;

import org.glite.wms.wmproxy.ProxyInfoStructType;

public class TestWMSClient2
{
    public static void main(String args[])
    {
        test1();
        // testStatus("https://wms.grid.sara.nl:9000/_NviF0z2EF8mXlXMwedwyQ");
    }

    public static void test1()
    {
        // Global.setDebug(true);
        // Spiros: Dead variable
        // GridProxy prox = GridProxy.getDefault();

        try
        {
            WMSClient client = getClient();

            message("WMS version = " + client.getVersion());

            client.doDelegation("test-del-id");

            String jobid = "https://wmslb2.grid.sara.nl:9000/-vFtFSJ9wIFdGFoMcRd12Q";

            boolean check = true;

            while (check)
            {
                try
                {
                    ArrayList<OutputInfo> list = client.getJobOutputs(new java.net.URI(jobid));

                    if (list != null)
                    {
                        for (OutputInfo file : list)
                        {
                            System.out.println(" - " + file.getFileURI() + " #" + file.getSize());
                        }
                        check = false;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    Thread.sleep(250);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static WMSClient getClient() throws Exception
    {
        VRL wmsUri = new VRL("https://wmslb2.grid.sara.nl:7443/glite_wms_wmproxy_server");

        GridProxy prox = GridProxy.getDefault();

        WMSConfig config = WMLBConfig.createWMSConfig("wmslb2.grid.sara.nl", 7443);

        WMSClient cl2 = new WMSClient(config);

        return cl2;

    }

    private static void print(ProxyInfoStructType status)
    {
        System.out.println("---- Job Info ---\n" + "\n subject    = " + status.getSubject() + "\n type       = "
                + status.getType() + "\n start time = " + status.getStartTime() + "\n end time   = "
                + status.getEndTime() + "\n--- ---\n");
    }

    private static void message(String msg)
    {
        System.err.println("TestGlite:" + msg);
    }
}
