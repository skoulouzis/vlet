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
 * $Id: TestSRMClientV22_Sara.java,v 1.1 2010-04-19 14:53:47 ptdeboer Exp $  
 * $Date: 2010-04-19 14:53:47 $
 */ 
// source: 

package test;

import gov.lbl.srm.v22.stubs.ArrayOfString;
import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.SrmPingResponse;
import gov.lbl.srm.v22.stubs.TAccessLatency;
import gov.lbl.srm.v22.stubs.TDirOption;
import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TOverwriteMode;
import gov.lbl.srm.v22.stubs.TRetentionPolicy;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;
import gov.lbl.srm.v22.stubs.TTransferParameters;

import java.util.ArrayList;

import nl.uva.vlet.lbl.srm.SRMClientV2;
import nl.uva.vlet.lbl.srm.SRMConstants;
import nl.uva.vlet.lbl.srm.SRMException;
import nl.uva.vlet.lbl.srm.status.SRMPutRequest;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;

public class TestSRMClientV22_Sara
{

    public static void main(String args[])
    {
        try
        {

            boolean connect = true;
            String se = "httpg://srm.grid.sara.nl:8443/srm/managerv2";

            java.net.URI srmUri = new java.net.URI(se);
            SRMClientV2 client = new SRMClientV2(srmUri, connect);

            // testPing(client);

            // testQueryPath(client);

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

    private static void testMV(SRMClientV2 client) throws SRMException, MalformedURIException
    {
        String source = "/dpm/nikhef.nl/home/pvier/skoulouz/test_VFS_plugin_dpm/testDir5";
        String dest = "/dpm/nikhef.nl/home/pvier/skoulouz/test_VFS_plugin_dpm/NewtestDir5";
        client.mv(source, dest);
    }

    private static void testPutRequst(SRMClientV2 client) throws MalformedURIException, SRMException
    {

        URI[] sourceSURLs = new org.apache.axis.types.URI[] { new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/test3.txt") };

        TTransferParameters transferParameters = new TTransferParameters();
        transferParameters
                .setArrayOfTransferProtocols(new ArrayOfString(new String[] { SRMConstants.GSIFTP_PROTOCOL }));

        SRMPutRequest req = client.srmCreatePutRequest(sourceSURLs, TOverwriteMode.ALWAYS, transferParameters);

        for (int i = 0; i < req.getTURLs().getUrlArray().length; i++)
        {
            debug("URI: " + req.getTURLs().getUrlArray()[i]);
        }

        client.abortSRMRequests(req.getToken());
        // client.srmCreatePutRequest(sourceSURLs, TOverwriteMode.ALWAYS);

    }

    private static void testGetTransportURI(SRMClientV2 client) throws SRMException, MalformedURIException
    {

        // String dir =
        // "srm://carme.htc.biggrid.nl:8446/dpm/htc.biggrid.nl/home/pvier/skoulouz/dir1/dir2";
        // String dir =
        // "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/test2.txt";
        String dir = "srm://srm.grid.sara.nl:8443/pnfs/grid.sara.nl/data/pvier/piter/test.txt";
        URI[] sourceSURLs = new org.apache.axis.types.URI[] { new org.apache.axis.types.URI(dir) };

        // client.srmGetTransportURIs(sourceSURLs,
        // TAccessPattern.TRANSFER_MODE,SRMConstants.GSIFTP_PROTOCOL);

        client.getTransportURIs(sourceSURLs);

    }

    private static void testCP(SRMClientV2 client) throws SRMException, MalformedURIException
    {

        org.apache.axis.types.URI[] arrayOfSourceSURLs = new org.apache.axis.types.URI[] { new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/test2.txt") };
        org.apache.axis.types.URI[] arrayOfTargetSURLs = new org.apache.axis.types.URI[] { new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/Copytest2.txt") };

        TDirOption option = new TDirOption();
        option.setAllLevelRecursive(false);
        option.setIsSourceADirectory(false);
        option.setNumOfLevels(1);
        TDirOption[] dirOptions = new TDirOption[] { option };

        TRetentionPolicyInfo targetFileRetentionPolicyInfo = new TRetentionPolicyInfo();
        targetFileRetentionPolicyInfo.setAccessLatency(TAccessLatency.ONLINE);
        targetFileRetentionPolicyInfo.setRetentionPolicy(TRetentionPolicy.OUTPUT);

        client.srmCp(arrayOfSourceSURLs, arrayOfTargetSURLs, dirOptions, TOverwriteMode.ALWAYS,
                targetFileRetentionPolicyInfo, TFileStorageType.PERMANENT);

    }

    private static void testRm(SRMClientV2 client) throws SRMException, MalformedURIException
    {
        org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[1];
        uris[0] = new org.apache.axis.types.URI("srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/newFolder");
        client.srmRm(uris);

    }

    private static void testRmDir(SRMClientV2 client) throws SRMException, MalformedURIException
    {
        // String dir =
        // "srm://carme.htc.biggrid.nl:8446/dpm/htc.biggrid.nl/home/pvier/skoulouz/dir1/";
        String dir = "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/skoulouz/test_VFS_plugin_dpm/dir1";
        org.apache.axis.types.URI surl = new org.apache.axis.types.URI(dir);
        boolean recursive = true;
        boolean res = client.srmRmdir(surl, recursive);
        debug("Sucess?" + res);
    }

    private static void testMkdir(SRMClientV2 client) throws MalformedURIException, SRMException
    {
        org.apache.axis.types.URI surl = new org.apache.axis.types.URI(
                "srm://tbn18.nikhef.nl:8446/dpm/nikhef.nl/home/pvier/spiros/newFolder/subFolder");
        boolean res = client.srmMkdir(surl);

        debug("Sucess?" + res);
    }

    private static void testQueryPath(SRMClientV2 client) throws MalformedURIException, SRMException
    {
        boolean allLevelRecursive = true;
        // not sure if it does something
        int numOfLevels = 0;
        boolean fullDetails = true;

        org.apache.axis.types.URI[] uris = new org.apache.axis.types.URI[1];
        uris[0] = new org.apache.axis.types.URI(
                "srm://gb-se-amc.amc.nl:8446/dpm/amc.nl/home/pvier/skoulouz/test_VFS_plugin_dpm");
        // uris[0] = new
        // org.apache.axis.types.URI("srm://gb-se-amc.amc.nl:8446/dpm/amc.nl/home/pvier/skoulouz/");

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
        System.err.println(TestSRMClientV22_Sara.class.getName() + ": " + msg);
    }

}
