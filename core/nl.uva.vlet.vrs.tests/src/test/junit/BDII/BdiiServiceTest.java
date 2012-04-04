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
 * $Id: BdiiServiceTest.java,v 1.2 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package test.junit.BDII;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import junit.framework.TestCase;
import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.util.bdii.ServiceInfo.ServiceInfoType;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

//import org.junit.After;
//import org.junit.Test;

import test.junit.TestSettings;

public class BdiiServiceTest extends TestCase
{

    private static VRSContext context = VRSContext.getDefault();

    private static final String[] VOS = { "pvier" };

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(BdiiServiceTest.class);
        logger.setLevel(Level.ALL);
    }

    // @BeforeClass
    // public static void setUpBeforeClass() throws Exception
    // {
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
    // }
    //
//    @After
    public void tearDown() throws Exception
    {
        System.gc();
    }

    //@Test
    public final void testCreateService() throws VlException
    {
        BdiiService service = BdiiService.createService(context);
        assertNotNull(service);

    }

    //@Test
    public final void testBdiiServiceURI() throws VlException, URISyntaxException
    {
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
        }
    }

    //@Test
    public final void testBdiiServiceStringInt() throws URISyntaxException
    {
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            int port = new URI(uri).getPort();
            String host = new URI(uri).getHost();
            service = new BdiiService(host, port);
            assertNotNull(service);
        }
    }

    //@Test
    public final void testGetURI() throws URISyntaxException
    {
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
            assertNotNull(service.getURI());
        }
    }

    //@Test
    public final void testQueryServiceInfo() throws URISyntaxException, VlException
    {
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);

            ArrayList<ServiceInfo> info;
            for (String vo : VOS)
            {
                info = service.queryServiceInfo(vo, ServiceInfoType.LB);

                for (ServiceInfo i : info)
                {
                    checkInfo(i);
                    // logger.debugPrintf("Info: %s\n", i.getHost());
                }

                info = service.queryServiceInfo(vo, ServiceInfoType.LFC);

                for (ServiceInfo i : info)
                {
                    checkInfo(i);
                    // logger.debugPrintf("Info: %s\n", i.getHost());
                }

                info = service.queryServiceInfo(vo, ServiceInfoType.SRMV11);
                if (info != null)
                {
                    for (ServiceInfo i : info)
                    {
                        checkInfo(i);
                        // logger.debugPrintf("Info: %s\n", i.getHost());
                    }
                }

                info = service.queryServiceInfo(vo, ServiceInfoType.SRMV21);
                if (info != null)
                {
                    for (ServiceInfo i : info)
                    {
                        checkInfo(i);
                        // logger.debugPrintf("Info: %s\n", i.getHost());
                    }
                }

                info = service.queryServiceInfo(vo, ServiceInfoType.SRMV22);
                if (info != null)
                {
                    for (ServiceInfo i : info)
                    {
                        checkInfo(i);
                        // logger.debugPrintf("Info: %s\n", i.getHost());
                    }
                }

                info = service.queryServiceInfo(vo, ServiceInfoType.WMS);
                if (info != null)
                {
                    for (ServiceInfo i : info)
                    {
                        checkInfo(i);
                        // logger.debugPrintf("Info: %s\n", i.getHost());
                    }
                }
            }

        }
    }

    private void checkInfo(ServiceInfo info)
    {
        assertNotNull(info);
        assertNotNull("Info has no host ", info.getHost());
//        VAttributeSet attributes = info.getInfoAttributes();
//        if (attributes != null)
//        {
//            String[] names = attributes.getAttributeNames();
//            if (names != null)
//            {
//                for (String name : names)
//                {
//                    String value = attributes.getValue(name);
//                    assertNotNull(value);
//                }
//            }
//        }

        int port = info.getPort();
        assertNotSame(0, port);
        // logger.debugPrintf("Port: %s\n", port);

        String scheme = info.getScheme();
        assertNotNull(scheme);
        // logger.debugPrintf("Scheme: %s\n", scheme);

        ServiceInfoType type = info.getServiceType();
        assertNotNull(type);
        // logger.debugPrintf("Type: %s\n", type);

        VRL vrl = info.getServiceVRL();
        assertNotNull(vrl);
        // logger.debugPrintf("VRL: %s\n", vrl);

    }

    //@Test
    public final void testGetSRMv22SAsforVO() throws VlException, URISyntaxException
    {
        ArrayList<StorageArea> saList;
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);

            for (String vo : VOS)
            {
                saList = service.getSRMv22SAsforVO(vo);
                for (StorageArea sa : saList)
                {
                    checkSA(sa, vo);
                }

            }
        }

    }

    //@Test
    public final void testGetLFCsforVO() throws VlException, URISyntaxException
    {
        ArrayList<ServiceInfo> infoList;
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);

            for (String vo : VOS)
            {
                infoList = service.getLFCsforVO(vo);
                for (ServiceInfo info : infoList)
                {
                    checkInfo(info);
                }
            }
        }

    }

    //@Test
    public final void testGetVOStorageAreas() throws VlException, URISyntaxException
    {
        ArrayList<StorageArea> saList;
        BdiiService service;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);

            for (String vo : VOS)
            {
                saList = service.getVOStorageAreas(vo, "*", true);
                assertNotNull(saList);

                for (StorageArea sa : saList)
                {
                    checkSA(sa, vo);
                }
            }
        }

    }

    //@Test
    public final void testGetWMSServiceInfos() throws VlException, URISyntaxException
    {
        BdiiService service;
        ArrayList<ServiceInfo> wmsInfo;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
            for (String vo : VOS)
            {
                wmsInfo = service.getWMSServiceInfos(vo);

                for (ServiceInfo info : wmsInfo)
                {
                    checkInfo(info);
                }
            }
        }
    }

    //@Test
    public final void testGetLBServiceInfosForVO() throws URISyntaxException, VlException
    {
        BdiiService service;
        ArrayList<ServiceInfo> lbInfo;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
            for (String vo : VOS)
            {
                lbInfo = service.getLBServiceInfosForVO(vo);
                for (ServiceInfo info : lbInfo)
                {
                    checkInfo(info);
                }
            }
        }
    }

    //@Test
    public final void testGetLBServiceInfosForWMSHost() throws URISyntaxException, VlException
    {
        BdiiService service;
        ArrayList<ServiceInfo> lbInfo;
        ArrayList<ServiceInfo> wmsInfo;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
            for (String vo : VOS)
            {

                wmsInfo = service.getWMSServiceInfos(vo);

                // Pick a random hostname
                Random random = new Random();
                int pick = random.nextInt(wmsInfo.size());

                ServiceInfo wms = wmsInfo.get(pick);

                lbInfo = service.getLBServiceInfosForWMSHost(wms.getHost());
                for (ServiceInfo info2 : lbInfo)
                {
                    checkInfo(info2);
                }
            }
        }
    }

    //@Test
    public final void testGetSRMV11ServiceForHost() throws URISyntaxException, VlException
    {
        BdiiService service;

        ArrayList<StorageArea> srmV11;
        StorageArea sa;
        for (String uri : TestSettings.BDII_LOCATIONS)
        {
            service = new BdiiService(new URI(uri));
            assertNotNull(service);
            for (String vo : VOS)
            {

                srmV11 = service.getSRMv11SAsforVO(vo);

                // Pick a random hostname
                Random random = new Random();
                int pick = random.nextInt(srmV11.size());

                sa = srmV11.get(pick);

                checkSA(sa, vo);
            }
        }
    }

    private void checkSA(StorageArea sa, String vo)
    {
        assertNotNull(sa);
        assertNotNull(sa.getHostname());

        String path = sa.getStoragePath();

        assertFalse(StringUtil.isEmpty(path));

        if (StringUtil.isEmpty(path))
        {
            fail("StorageArea: " + sa.getHostname() + " has no storage path ");
            logger
                    .warnPrintf(
                            "%s has no usable path. BDII didn't give it. To verify you can run 'lcg-info --vo %s --list-se --attrs Path' on a UI machine \n",
                            sa.getHostname(), vo);

        }

        ArrayList<ServiceInfo> services = sa.getServices();

        assertNotNull(services);

        for (ServiceInfo info : services)
        {
            checkInfo(info);
        }

        ServiceInfo srmV1 = sa.getSRMV11Service();
        ServiceInfo srmV2 = sa.getSRMV22Service();
        if (srmV1 == null)
        {
            checkInfo(srmV2);
        }
        else
        {
            checkInfo(srmV1);
        }
        assertNotNull(sa.getVO());
    }

}
