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
 * $Id: testServerInfoContext.java,v 1.5 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.vrs;

import junit.framework.Assert;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.lfc.LFCFSConfig;
import nl.uva.vlet.vfs.lfc.LFCFSFactory;
import nl.uva.vlet.vfs.srm.SRMFSFactory;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import test.junit.VTestCase;

public class testServerInfoContext extends VTestCase
{
    VFSClient vfs = null;

    static
    {
//        Global.getLogger().addDebugClass(LFCFSConfig.class);
//        Global.getLogger().addDebugClass(ServerInfo.class);
//        Global.getLogger().addDebugClass(LFCFSFactory.class);

        Global.init();

        try
        {
            VRS.getRegistry().addVRSDriverClass(LFCFSFactory.class);
            VRS.getRegistry().addVRSDriverClass(SRMFSFactory.class);
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
    }

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected void setUp()
    {

        /*
         * try { if (Registry.getDefault().getServiceForScheme("lfc")==null) {
         * message("registering LFC driver");
         * 
         * }
         * 
         * 
         * } catch (Exception e) { e.printStackTrace(); }
         */
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    public void testLFCUriAttributes() throws VlException
    {
        String lfcHost = "lfc.grid.sara.nl";
        int port = 5010;
        String path = "/grid/pvier";
        // for reference only
        String dummySE1 = "elab.science.uva.nl";
        String dummySE2 = "pc-vlab19.science.uva.nl";

        // CLEAN context !
        VRSContext vrsCtxt = new VRSContext();
        // set context properties to wrong values
        vrsCtxt.setProperty("lfc.hostname", "elab.science.uva.nl");
        vrsCtxt.setProperty("lfc.port", "9999");

        //
        // specify ALL Lfc attribute as URI attributes !
        //
        String prefSEs = dummySE1 + "," + dummySE2;
        // lfn:/path style LFNs !
        String lfcUrl = "lfn:" + path + "?" + "lfc.hostname=" + lfcHost + "&lfc.port=" + port + "&lfc."
                + LFCFSConfig.ATTR_PREFERREDSSES + "=" + prefSEs;

        VRL lfcVrl = new VRL(lfcUrl);
        // open resource system only.
        vrsCtxt.openResourceSystem(lfcVrl);

        // After 'opening' LFC Should have created a new ServerInfo object
        // Query for specified LFC Host only, ignore port or userinfo
        ServerInfo infos[] = vrsCtxt.getServerInfosFor("lfn", lfcHost, -1, null);

        Assert.assertNotNull("Returned ServerInfos are NULL for location:" + lfcVrl, infos);
        Assert.assertFalse("ServerRegistr didn' return any server info for:" + lfcVrl, infos.length <= 0);

        ServerInfo info = infos[0];

        message("srb INFO=" + info);

        Assert.assertEquals("Attribute hostname from URI is not used", lfcHost, info.getHostname());
        Assert.assertEquals("Attribute port from URI is not used", port, info.getPort());
        Assert.assertEquals("List preferred SEs do not match", prefSEs, info
                .getStringProperty(LFCFSConfig.ATTR_PREFERREDSSES));

    }

    public void testLFCServerInfo() throws VlException
    {
        VAttribute[] defaultAttributes;

        // create empty:
        VRL lfcVrl = new VRL("lfn:///");
        ServerInfo lfcInfo = new ServerInfo(VRSContext.getDefault(), lfcVrl);
        defaultAttributes = lfcInfo.getAllAttributes();

        VRSContext vrsContext1 = VRSContext.getDefault();
        VRSContext vrsContext2 = new VRSContext();

        for (VAttribute defAttr : defaultAttributes)
        {
            String name = defAttr.getName();
            System.out.println("name=" + name);

            String lfcname = "lfc." + name;
            String defaultVal = defAttr.getStringValue();
            String value1 = null;
            String value2 = null;

            if (defAttr.getType() == VAttributeType.INT)
            {
                value1 = "111";
                value2 = "222";
            }
            else
            {
                value1 = defAttr.getName() + "-1";
                value2 = defAttr.getName() + "-2";
            }

            vrsContext1.setProperty(lfcname, value1);
            vrsContext2.setProperty(lfcname, value2);
        }

        // ===
        // check Context seperation of Server Attributes
        // ===

        // create but do not store !
        ServerInfo info1 = vrsContext1.getServerInfoFor(lfcVrl, true);
        ServerInfo info2 = vrsContext2.getServerInfoFor(lfcVrl, true);

        for (VAttribute defAttr : defaultAttributes)
        {
            String name = defAttr.getName();
            String defaultVal = defAttr.getStringValue();

            String value1 = null;
            String value2 = null;

            if (defAttr.getType() == VAttributeType.INT)
            {
                value1 = "111";
                value2 = "222";
            }
            else
            {
                value1 = defAttr.getName() + "-1";
                value2 = defAttr.getName() + "-2";
            }

            if (StringUtil.compare(name, VAttributeConstants.ATTR_SCHEME) == 0)
            {
                ; // skip unit test
            }
            else if (defAttr.hasName(VAttributeConstants.ATTR_PASSIVE_MODE))
            {
                ; // skip unit test
            }
            else if (defAttr.hasName(VAttributeConstants.ATTR_PATH))
            {
                ; // skip unit test
            }
            else if (defAttr.hasName(ServerInfo.ATTR_DEFAULT_PATH))
            {
                ; // skip unit test
            }
            else if (defAttr.hasName(VAttributeConstants.AUTH_SCHEME))
            {
                Assert.assertEquals("Auth Scheme MUT be GSI auth:", ServerInfo.GSI_AUTH, info1.getStringProperty(name));
                Assert.assertEquals("Auth Scheme MUT be GSI auth:", ServerInfo.GSI_AUTH, info2.getStringProperty(name));
            }
            else
            {
                // may not return default value !
                Assert.assertNotSame("ServerInfo1 should return non default " + name + " property from vrscontext",
                        info1.getAttribute(name), defaultVal);
                Assert.assertNotSame("ServerInfo2 should return non default " + name + " property from vrscontext",
                        info2.getAttribute(name), defaultVal);

                // must return config value ! s
                Assert.assertNotNull("ServerInfo1 should return " + name + " property from vrscontext", info1
                        .getAttribute(name));
                Assert.assertEquals("ServerInfo1 should return " + name + " property from vrscontext", value1, info1
                        .getAttribute(name).getValue());
                Assert.assertEquals("ServerInfo2 should return " + name + " property from vrscontext", value2, info2
                        .getAttribute(name).getValue());
            }
        }
    }

    public void testLFClobalSettings() throws VlException
    {
        VAttributeSet attrs = LFCFSConfig.createDefaultServerAttributes(VRS.getDefaultVRSContext(), null);

        String names[] = attrs.getOrdenedKeyArray();
        int numAttrs = names.length;
        String globalValues[] = new String[numAttrs];

        int index = 0;

        for (String name : names)
        {
            String value = name + "-" + index;
            globalValues[index] = value;
            GlobalConfig.setSystemProperty("lfc." + name, value);
            index++;
        }

        // context should reflect Global settings:
        VRSContext context = new VRSContext();

        for (index = 0; index < numAttrs; index++)
        {
            String value = context.getProperty("lfc." + names[index]).toString();

            Assert.assertEquals("Global specified property is NOT the same in context", value, globalValues[index]);

            // message("Context property:"+name+"="+value);

        }

        // Check context properties
        String contextValues[] = new String[numAttrs];
        for (index = 0; index < numAttrs; index++)
        {
            String name = names[index];
            String value = name + "-CONTEXT-" + index;
            contextValues[index] = value;
            context.setProperty(name, value);
        }

        for (index = 0; index < numAttrs; index++)
        {
            String name = names[index];

            String value = context.getProperty(name).toString();

            Assert.assertEquals("Context specified property is NOT the same in context", value, contextValues[index]);

            // message("Context property:"+name+"="+value);
        }

        // context should reflect Global settings:
        VRSContext contextNew = new VRSContext();
        for (index = 0; index < numAttrs; index++)
        {
            String name = names[index];

            String value = contextNew.getProperty("lfc." + name).toString();

            Assert.assertEquals("Global specified property is NOT the same in NEW context", value, globalValues[index]);

            // message("Context property:"+name+"="+value);
        }
    }

}
