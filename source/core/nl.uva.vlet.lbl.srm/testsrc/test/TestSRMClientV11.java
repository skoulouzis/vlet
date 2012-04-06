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
 * $Id: TestSRMClientV11.java,v 1.6 2010-04-13 14:23:54 ptdeboer Exp $  
 * $Date: 2010-04-13 14:23:54 $
 */ 
// source: 

package test;

import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;

import java.util.ArrayList;

import nl.uva.vlet.lbl.srm.SRMClientV1;
import nl.uva.vlet.lbl.srm.SRMException;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

public class TestSRMClientV11
{

    public static void main(String args[])
    {
        try
        {

            boolean connect = true;
            String se = "httpg://srm.grid.sara.nl:8443/srm/managerv1";
            java.net.URI srmUri = new java.net.URI(se);
            SRMClientV1 client = new SRMClientV1(srmUri, connect);

            testPing(client);

            testQueryPath(client);

            // testMkdir(client);

            // testRmDir(client);

            // testRm(client);

            // testCP(client);

            testGetTransportURI(client);

            // testPutRequst(client);
            //            
            // testMV(client);

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void testMV(SRMClientV1 client) throws SRMException
    {
        String source = "/dpm/nikhef.nl/home/pvier/skoulouz/test_VFS_plugin_dpm/testDir5";
        String dest = "/dpm/nikhef.nl/home/pvier/skoulouz/test_VFS_plugin_dpm/NewtestDir5";
    }

    private static void testPutRequst(SRMClientV1 client) throws MalformedURIException, SRMException
    {

    }

    private static void testGetTransportURI(SRMClientV1 client) throws SRMException, MalformedURIException
    {

        // String dir =
        // "srm://carme.htc.biggrid.nl:8446/dpm/htc.biggrid.nl/home/pvier/skoulouz/dir1/dir2";
        String dir = "srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/pvier/spiros/test2.txt";
        URI[] sourceSURLs = new org.apache.axis.types.URI[] { new org.apache.axis.types.URI(dir) };

        client.getTransportURIs(sourceSURLs);

    }

    private static void testCP(SRMClientV1 client) throws SRMException, MalformedURIException
    {

    }

    private static void testRm(SRMClientV1 client) throws SRMException, MalformedURIException
    {

    }

    private static void testRmDir(SRMClientV1 client) throws SRMException, MalformedURIException
    {

    }

    private static void testMkdir(SRMClientV1 client) throws MalformedURIException, SRMException
    {

    }

    private static void testQueryPath(SRMClientV1 client) throws MalformedURIException, SRMException
    {
        org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[1];
        uris[0] = new org.apache.axis.types.URI(
                "srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/pvier/spiros/test2.txt");
        // uris[1] = new org.apache.axis.types.URI(
        // "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/piter");

        ArrayList<TMetaDataPathDetail> details = client.srmQueryPaths(uris);

        for (int i = 0; i < details.size(); i++)
        {
            debug("Paths : " + details.get(i).getPath());
        }
    }

    private static void testPing(SRMClientV1 client) throws SRMException
    {
        debug("Ping was sucess? " + client.srmPing());

    }

    private static void debug(String msg)
    {
        System.err.println(TestSRMClientV11.class.getName() + ": " + msg);
    }

}
