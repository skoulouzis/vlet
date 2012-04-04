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
 * $Id: BDIIQueryTest.java,v 1.3 2011-06-07 15:15:26 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:26 $
 */ 
// source: 

package test.junit.BDII;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;
import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.util.bdii.BDIIQuery;
import nl.uva.vlet.util.bdii.info.glue.GlueConstants;
import nl.uva.vlet.util.bdii.info.glue.GlueObject;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

import test.junit.TestSettings;

public class BDIIQueryTest extends TestCase
{
    private static ClassLogger logger;

    private static String[] VOS = { "pvier" };

    private static String[] SERVICE_HOSTS = { "" };

    public static final String[] ALL_SERVICE_TYPES = { "org.glite.wms.NetworkServer", "org.glite.lb.Server",
            "org.glite.lb.ServerWS", "org.glite.ce.CREAM", "org.glite.ce.Monitor", "org.glite.rgma.LatestProducer",
            "org.glite.rgma.StreamProducer", "org.glite.rgma.DBProducer", "org.glite.rgma.CanonicalProducer",
            "org.glite.rgma.Archiver", "org.glite.rgma.Consumer", "org.glite.rgma.Registry", "org.glite.rgma.Schema",
            "org.glite.rgma.Browser", "org.glite.rgma.PrimaryProducer", "org.glite.rgma.SecondaryProducer",
            "org.glite.rgma.OnDemandProducer", "org.glite.RTEPublisher", "org.glite.voms", "org.glite.voms-admin",
            "org.glite.AMGA", "org.glite.FiremanCatalog", "org.glite.SEIndex", "org.glite.Metadata",
            "org.glite.ChannelManagement", "org.glite.FileTransfer", "org.glite.FileTransferStats",
            "org.glite.ChannelAgent", "org.glite.Delegation", "org.glite.KeyStore", "org.glite.FAS",
            "org.glite.gliteIO", "SRM", "srm_v1", "lcg-file-catalog", "lcg-local-file-catalog",
            "org.glite.wms.WMProxy", "local-data-location-interface", "data-location-interface", "gsiftp",
            "org.edg.local-replica-catalog", "org.edg.replica-metadata-catalog", "org.edg.SE", "org.edg.gatekeeper",
            "it.infn.GridICE", "gridice", "MyProxy", "GUMS", "gridmap-file", "GridCat", "edu.caltech.cacr.monalisa",
            "OpenSSH", "MDS-GIIS", "BDII", "bdii_site", "bdii_top", "VOBOX", "msg.broker.rest", "msg.broker.stomp",
            "Nagios", "RLS", "pbs.torque.server", "pbs.torque.maui", "other" };

    public static final String[] EXISTING_SERVICES = { "SRM", "lcg-file-catalog", "lcg-local-file-catalog",
            "org.glite.wms.WMProxy", "data-location-interface", "org.glite.lb.Server", "org.glite.AMGA",
            "org.glite.FileTransfer", "org.glite.Delegation", "srm_v1", "VOBOX" };

    static
    {
        logger = ClassLogger.getLogger(BDIIQueryTest.class);
        logger.setLevelToDebug();
    }

    // @BeforeClass
    // public static void setUpBeforeClass() throws Exception
    // {
    // logger.debugPrintf("setUpBeforeClass \n");
    // }
    //
    // @AfterClass
    // public static void tearDownAfterClass() throws Exception
    // {
    // }
    //

    // @Before
    // public void setUp() throws Exception
    // {
    // logger.debugPrintf("--------------Setup\n");
    // }

    // @After
    public void tearDown() throws Exception
    {
        System.gc();
    }

    // //@Test
    public void testBDIIQueryURI()
    {
        try
        {
            BDIIQuery q;
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(new URI(uri));
                assertNotNull(q);
            }

        }
        catch (URISyntaxException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testBDIIQueryString()
    {

        BDIIQuery q;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {

                q = new BDIIQuery(uri);

                assertNotNull(q);
            }

        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetSEUidsString() throws VlException
    {
        for (String uri : TestSettings.BDII_LOCATIONS)
        {

            BDIIQuery q = new BDIIQuery(uri);
            assertNotNull(q);

            for (String vo : VOS)
            {
                StringList uids = q.getSEUids(vo);

                assertNotNull(uids);

                if (uids.size() <= 0)
                {
                    fail(uri + " returned no UIDs for " + vo);
                }
            }
        }
    }

    // @Test
    public void testGetServicesStringStringString() throws VlException
    {
        BDIIQuery q;
        ArrayList<GlueObject> services;
        logger.warnPrintf("-------------testGetServices--------------\n");
        for (String uri : TestSettings.BDII_LOCATIONS)
        {

            q = new BDIIQuery(uri);
            assertNotNull(q);

            for (String vo : VOS)
            {
                for (String serviceType : ALL_SERVICE_TYPES)
                {
                    services = q.getServices(vo, serviceType, "*");
                    if (services.isEmpty())
                    {
                        logger.warnPrintf("BDII: %s returned 0 services of type %s\n", uri, serviceType);
                    }
                }

            }

        }
    }

    // @Test
    public void testGetServicesStringStringStringString()
    {
        BDIIQuery q;
        ArrayList<GlueObject> services;
        String host = "nikhef";
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    for (String serviceType : EXISTING_SERVICES)
                    {
                        services = q.getServices(vo, "*" + host + "*", serviceType, "*");

                        for (GlueObject gObj : services)
                        {
                            getServiceURI(gObj, host);
                        }
                    }

                }

            }

        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    private String getServiceURI(GlueObject gObj, String host)
    {
        String servicesURI = null;

        Vector<Object> uris = gObj.getExtAttribute(GlueConstants.SERVICE_ACCESS_POINT_URL);
        if (uris != null)
        {
            for (Object uriObj : uris)
            {
                servicesURI = (String) uriObj;
                if (!StringUtil.isEmpty(servicesURI))
                {
                    break;
                }
            }
        }

        // servicesURI = empty or contains host
        if (StringUtil.isEmpty(servicesURI))
        {
            uris = gObj.getExtAttribute(GlueConstants.SERVICE_ENDPOINT);
            if (uris != null)
            {
                for (Object uriObj : uris)
                {
                    servicesURI = (String) uriObj;
                    if (!StringUtil.isEmpty(servicesURI))
                    {
                        break;
                    }
                }
            }

        }

        if (StringUtil.isEmpty(host) == false)
        {
            if (StringUtil.isEmpty(servicesURI))
            {
                fail("Failed to get service URI ");
            }
            else if (!servicesURI.contains(host))
            {
                fail("URI " + servicesURI + " does not contain host: " + host);
            }
        }
        else
        {
            if (servicesURI == null)
            {
                fail("Failed to get service URI");
            }
        }
        return servicesURI;
    }

    // @Test
    public void testGetSiteForGlueObject()
    {
        ArrayList<GlueObject> se;
        BDIIQuery q;
        ArrayList<GlueObject> sites;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    se = q.getStorageElements(vo);
                    if (se.isEmpty())
                    {
                        fail(vo + " has no SEs");
                    }

                    sites = q.getSitesForGlueObjects(se);

                    if (sites.isEmpty())
                    {
                        fail("NO Site location while quering " + uri + " for VO: " + vo);
                    }
                }

            }

        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (NotImplementedException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetSE()
    {
        ArrayList<GlueObject> se;
        BDIIQuery q;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {

                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    se = q.getStorageElements(vo);
                    if (se.isEmpty())
                    {
                        fail(vo + " has no SE!");
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetSEUids()
    {
        StringList se;
        BDIIQuery q;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {

                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    se = q.getSEUids(vo);
                    if (se.size() <= 0)
                    {
                        fail(vo + " has no SE!");
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetVoPath() throws VlException
    {
        StringList seUids;
        BDIIQuery q;
        Map<String, String> allPaths;
        // logger.warnPrintf("---------testGetVoPath-----------\n");
        for (String uri : TestSettings.BDII_LOCATIONS)
        {

            q = new BDIIQuery(uri);
            assertNotNull(q);
            for (String vo : VOS)
            {
                seUids = q.getSEUids(vo);
                if (seUids.size() <= 0)
                {
                    fail(vo + " has no SE!");
                }

                allPaths = q.getVoInfoPaths(vo);

                for (int i = 0; i < seUids.size(); i++)
                {

                    String path = allPaths.get(seUids.get(i));
                    // logger.debugPrintf("%s : %s\n", seUids.get(i), path);

                    if (StringUtil.isEmpty(path))
                    {
                        StringList missingHostnames = new StringList();
                        if (((String) seUids.get(i)).contains("tbn18.nikhef.nl"))
                        {
                            logger.debugPrintf("\n");
                        }
                        missingHostnames.add(GlueConstants.SE_UNIQUE_ID + "=" + seUids.get(i));
                        Map<String, String> saPaths = q.getSAPaths(vo, missingHostnames);
                        path = saPaths.get(seUids.get(i));
                        
                        if (StringUtil.isEmpty(path))
                        {
                            logger.warnPrintf("%s did not return a path for the '%s' SE  and %s VO\n", uri, seUids
                                    .get(i), vo);
                        }
                    }

                    assertFalse(
                            uri + " did not return a path a path for '" + seUids.get(i) + "' SE and " + vo + " VO ",
                            StringUtil.isEmpty(path));
                }
            }
        }
    }

    // @Test
    public void testGetLBServer()
    {
        BDIIQuery q;
        ArrayList<GlueObject> lb;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    lb = q.getLBServices(vo);
                    for (GlueObject gObj : lb)
                    {
                        getServiceURI(gObj, null);
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetWMProxy()
    {
        BDIIQuery q;
        ArrayList<GlueObject> wms;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    wms = q.getWMProxy(vo);
                    for (GlueObject gObj : wms)
                    {
                        getServiceURI(gObj, null);
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetCE()
    {
        ArrayList<GlueObject> se;
        BDIIQuery q;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {

                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    se = q.getComputingElements(vo);
                    if (se.isEmpty())
                    {
                        fail(vo + " has no CE!");
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetWMProxyAsList()
    {
        BDIIQuery q;
        ArrayList<GlueObject> wms;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    wms = q.getWMProxy(vo);
                    for (GlueObject gObj : wms)
                    {
                        getServiceURI(gObj, null);
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGuessLBServicesForWMSHostname()
    {
        BDIIQuery q;
        ArrayList<GlueObject> wms;
        ArrayList<GlueObject> lb;
        String servicesURI;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    wms = q.getWMProxy(vo);
                    for (GlueObject gObj : wms)
                    {
                        servicesURI = getServiceURI(gObj, null);

                        String hostName = new VRL(servicesURI).getHostname();

                        lb = q.guessLBServicesForWMSHostname(hostName);
                        if (lb == null || lb.isEmpty())
                        {
                            // fail("Could not guess LB services from wms: " +
                            // hostName);
                            logger.warnPrintf("Could not guess LB services from wms:  %s\n", hostName);
                        }
                    }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGuessLBServices()
    {
        BDIIQuery q;
        ArrayList<GlueObject> lb;
        try
        {
            for (String uri : TestSettings.BDII_LOCATIONS)
            {
                q = new BDIIQuery(uri);
                assertNotNull(q);
                for (String vo : VOS)
                {
                    lb = q.guessLBServices(vo);

                    if (lb == null || lb.isEmpty())
                    {
                        // fail("Could not guess LB services for VO: " + vo);
                        logger.warnPrintf("Could not guess LB services for VO:  %s\n", vo);
                    }

                    // for (GlueObject gObj : lb)
                    // {
                    // logger.debugPrintf("LB: %s \n", gObj.getUid());
                    // }
                }
            }
        }
        catch (VRLSyntaxException e)
        {
            fail(e.getMessage());
        }
        catch (VlException e)
        {
            fail(e.getMessage());
        }
    }

    // @Test
    public void testGetSAforVoAndHost() throws VlException, URISyntaxException
    {
        BDIIQuery q;
        ArrayList<GlueObject> sa;
        String host = null;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            q = new BDIIQuery(uri);
            assertNotNull(q);
            for (String vo : VOS)
            {
                // First get all of them
                sa = q.getSAforVoAndHost(vo, null);
                if (sa.isEmpty())
                {
                    logger.warnPrintf("%s has no SA for the %s VO\n", uri, vo);
                    // fail(uri + " has no SA for the" + vo + "VO");
                    break;
                }

                // Pick one random
                Random random = new Random();
                int pick = random.nextInt(sa.size());

                host = getSAHostName(sa.get(pick));

                // .. and see what happens
                sa = q.getSAforVoAndHost(vo, host);
                if (sa == null || sa.isEmpty())
                {
                    // fail("Could not guess LB services for VO: " + vo);
                    logger.warnPrintf("Could not get SA for VO:  %s, and host: %s \n", vo, host);
                }
            }
        }
    }

    private String getSAHostName(GlueObject glueObject)
    {
        Vector<Object> att = glueObject.getExtAttribute(GlueConstants.CHUNK_KEY);
        String uID = null;
        String temp;
        if (att != null)
        {
            for (Object o : att)
            {
                temp = (String) o;
                if (!StringUtil.isEmpty(temp) && temp.startsWith(GlueConstants.SE_UNIQUE_ID))
                {
                    uID = temp.split("=")[1];
                    break;
                }
            }
        }
        return uID;
    }

    private void printAllAtributes(GlueObject glueObject)
    {
        Set<String> keySet = glueObject.getExtGlueTypes();
        Iterator<String> iter = keySet.iterator();

        while (iter.hasNext())
        {
            String key = iter.next();
            Vector<Object> extAttr = glueObject.getExtAttribute(key);
            logger.debugPrintf("%s\n", key);
            for (Object attr : extAttr)
            {
                logger.debugPrintf("\t%s\n", attr);
            }
        }
        logger.debugPrintf("----------------------\n");
    }
}
