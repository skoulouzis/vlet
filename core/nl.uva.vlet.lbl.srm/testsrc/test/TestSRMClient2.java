/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: TestSRMClient2.java,v 1.6 2010-04-13 14:23:54 ptdeboer Exp $  
 * $Date: 2010-04-13 14:23:54 $
 */ 
// source: 

package test;

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.SrmPingResponse;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;

import java.util.ArrayList;

import nl.uva.vlet.lbl.srm.SRMClientV2;
import nl.uva.vlet.lbl.srm.SRMException;

import org.apache.axis.types.URI.MalformedURIException;

public class TestSRMClient2
{
    static String srmHost = "prod-se-01.pd.infn.it:8444";

    static String srmloc = "srm://" + srmHost + "/dteam";

    public static void main(String args[])
    {
        try
        {

            boolean connect = true;
            java.net.URI srmUri = new java.net.URI("httpg://" + srmHost + "/srm/managerv2");
            SRMClientV2 client = new SRMClientV2(srmUri, connect);

            // testPing(client);

            // testMkdir(client);
            testQueryPath(client);

            //            
            // testRmDir(client);
            //            
            // testRm(client);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void testRm(SRMClientV2 client) throws SRMException, MalformedURIException
    {
        org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[1];
        uris[0] = new org.apache.axis.types.URI("srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/newFolder");

        client.srmRm(uris);

    }

    private static void testRmDir(SRMClientV2 client) throws SRMException, MalformedURIException
    {
        org.apache.axis.types.URI surl = new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/newFolder");
        boolean recursive = true;
        client.srmRmdir(surl, recursive);

    }

    private static void testMkdir(SRMClientV2 client) throws MalformedURIException, SRMException
    {
        org.apache.axis.types.URI surl = new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/newFolder");
        client.srmMkdir(surl);

    }

    private static void testQueryPath(SRMClientV2 client) throws MalformedURIException, SRMException
    {
        boolean allLevelRecursive = true;
        // not sure if it does something
        int numOfLevels = 0;
        boolean fullDetails = true;

        // org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[2];
        // uris[0] = new org.apache.axis.types.URI(srmloc);
        // uris[1] = new org.apache.axis.types.URI(srmloc+"piter");

        org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[1];
        uris[0] = new org.apache.axis.types.URI(srmloc);
        // uris[1] = new org.apache.axis.types.URI(srmloc+"piter");

        ArrayList<TMetaDataPathDetail> details = client.queryPaths(numOfLevels, uris, fullDetails, allLevelRecursive);

        client.printDetails(details);

    }

    private static void testPing(SRMClientV2 client) throws SRMException
    {

        // SRMClientV2 client = new SRMClientV2(host, port, connect);

        SrmPingResponse result = client.srmPing();

        System.out.println("Ping; " + result.getVersionInfo());

        ArrayOfTExtraInfo info = result.getOtherInfo();

        for (int i = 0; i < info.getExtraInfoArray().length; i++)
        {

            debug("Ping; " + info.getExtraInfoArray()[i].getKey() + " " + info.getExtraInfoArray()[i].getValue());
        }

    }

    private static void debug(String msg)
    {
        System.err.println(TestSRMClient2.class.getName() + ": " + msg);
    }

}
