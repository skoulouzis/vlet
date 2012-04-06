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
 * $Id: testSRBServerContext.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

//package test.misc;
//
//import test.junit.VTestCase;
//import junit.framework.Assert;
//import junit.framework.TestCase;
//import nl.uva.vlet.Global;
//import nl.uva.vlet.GlobalConfig;
//import nl.uva.vlet.data.StringUtil;
//import nl.uva.vlet.data.VAttribute;
//import nl.uva.vlet.data.VAttributeConstants;
//import nl.uva.vlet.data.VAttributeType;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vfs.VFSClient;
//import nl.uva.vlet.vfs.srbfs.SrbConfig;
//import nl.uva.vlet.vfs.srbfs.SrbFSFactory;
//
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.Registry;
//import nl.uva.vlet.vrs.ServerInfo;
//import nl.uva.vlet.vrs.VRSContext;
//
//public class testSRBServerContext extends VTestCase
//{
//    VFSClient vfs = null;
//
//    static
//    {
//        Global.getLogger().addDebugClass(SrbConfig.class);
//        Global.getLogger().addDebugClass(ServerInfo.class);
//        Global.getLogger().addDebugClass(SrbFSFactory.class);
//    }
//
//    /**
//     * Sets up the tests fixture. (Called before every tests case method.)
//     */
//    protected void setUp()
//    {
//        SrbConfig.setUseSRBPropertiesFile(false);
//
//        Global.init();
//
//        /*
//         * try { if (Registry.getDefault().getServiceForScheme("lfc")==null) {
//         * message("registering LFC driver");
//         * Registry.getDefault().addVRSDriverClass
//         * (nl.uva.vlet.vfs.lfcfs.LFC_FS.class); }
//         * 
//         * 
//         * } catch (Exception e) { e.printStackTrace(); }
//         */
//    }
//
//    /**
//     * Tears down the tests fixture. (Called after every tests case method.)
//     */
//    protected void tearDown()
//    {
//
//    }
//
//    public void testSRBUriAttributes() throws VlException
//    {
//        String srbHost = "srb.grid.sara.nl";
//        int port = 50000;
//        String user = "piter.de.boer";
//        String domain = "vlenl";
//        String zone = "VLENL";
//        String defResource = "dummyResource";
//
//        // CLEAN context !
//        VRSContext vrsCtxt = new VRSContext();
//
//        String srbUrl = "srb:///home/" + zone + "?" + "srb.hostname=" + srbHost + "&srb.port=" + port
//                + "&srb.username=" + user + "&srb." + ServerInfo.ATTR_MDASDOMAINNAME + "=" + domain + "&srb."
//                + ServerInfo.ATTR_DEFAULTRESOURCE + "=" + defResource;
//        ;
//        VRL srbVrl = new VRL(srbUrl);
//
//        vrsCtxt.openLocation(srbVrl);
//
//        // after 'opening' SRB Should have create a new ServerInfo object
//        ServerInfo infos[] = vrsCtxt.getServerInfosFor("srb", srbHost, -1, null);
//
//        Assert.assertNotNull("Returned ServerInfos are NULL for location:" + srbVrl, infos);
//        Assert.assertFalse("ServerRegistr didn' return any server info for:" + srbVrl, infos.length <= 0);
//
//        ServerInfo info = infos[0];
//
//        message("srb INFO=" + info);
//
//        Assert.assertEquals("Attribute hostname from URI is not used", srbHost, info.getHostname());
//        Assert.assertEquals("Attribute user from URI is not used", user, info.getUsername());
//        Assert.assertEquals("Attribute port from URI is not used", port, info.getPort());
//        Assert.assertEquals("Attribute domain from URI is not used", domain, info.getMdasDomainName());
//        Assert.assertEquals("Attribute defaultResource from URI is not used", defResource, info.getDefaultResource());
//    }
//
//    public void testSRBServerInfo() throws VlException
//    {
//        VAttribute[] defaultAttributes;
//        ServerInfo srbInfo = new ServerInfo(VRSContext.getDefault(), (VRL) null);
//        defaultAttributes = srbInfo.getAllAttributes();
//
//        VRL srb = new VRL("srb:///");
//        SrbConfig.setUseSRBPropertiesFile(false);
//
//        VRSContext vrsContext1 = VRSContext.getDefault();
//        VRSContext vrsContext2 = new VRSContext();
//
//        for (VAttribute defAttr : defaultAttributes)
//        {
//            String name = defAttr.getName();
//            System.out.println("name=" + name);
//
//            String srbname = "srb." + name;
//            String defaultVal = defAttr.getValue();
//            String value1 = null;
//            String value2 = null;
//
//            if (defAttr.getType() == VAttributeType.INT)
//            {
//                value1 = "111";
//                value2 = "222";
//            }
//            else
//            {
//                value1 = defAttr.getName() + "-1";
//                value2 = defAttr.getName() + "-2";
//            }
//
//            vrsContext1.setProperty(srbname, value1);
//            vrsContext2.setProperty(srbname, value2);
//        }
//
//        // create but do not store !
//        ServerInfo info1 = vrsContext1.getServerInfoFor(srb, true);
//        ServerInfo info2 = vrsContext2.getServerInfoFor(srb, true);
//
//        for (VAttribute defAttr : defaultAttributes)
//        {
//            String name = defAttr.getName();
//            String defaultVal = defAttr.getValue();
//
//            String value1 = null;
//            String value2 = null;
//
//            if (defAttr.getType() == VAttributeType.INT)
//            {
//                value1 = "111";
//                value2 = "222";
//            }
//            else
//            {
//                value1 = defAttr.getName() + "-1";
//                value2 = defAttr.getName() + "-2";
//            }
//
//            if (defAttr.hasName(VAttributeConstants.AUTH_SCHEME))
//            {
//                ; // skip no unit test
//            }
//            else if (defAttr.hasName(VAttributeConstants.ATTR_PASSIVE_MODE))
//            {
//                ; // skip no unit test
//            }
//            else
//            {
//                // may not return default value !
//                Assert.assertNotSame("ServerInfo1 should return non default " + name + " property from vrscontext",
//                        info1.getAttribute(name), defaultVal);
//                Assert.assertNotSame("ServerInfo2 should return non default " + name + " property from vrscontext",
//                        info2.getAttribute(name), defaultVal);
//
//                // must return config value ! s
//                Assert.assertEquals("ServerInfo1 should return " + name + " property from vrscontext", value1, info1
//                        .getAttribute(name).getValue());
//                Assert.assertEquals("ServerInfo2 should return " + name + " property from vrscontext", value2, info2
//                        .getAttribute(name).getValue());
//            }
//        }
//    }
//
//    public void testSRBGlobalSettings() throws VlException
//    {
//        String names[] = SrbConfig.getDefaultSRBAttributesNames();
//
//        String globalValues[] = new String[names.length];
//
//        int index = 0;
//        int len = globalValues.length;
//
//        for (String name : names)
//        {
//            String value = name + "-" + index;
//            globalValues[index] = value;
//            GlobalConfig.setSystemProperty(name, value);
//            index++;
//        }
//
//        // context should reflect Global settings:
//        VRSContext context = new VRSContext();
//
//        for (index = 0; index < len; index++)
//        {
//            String name = names[index];
//
//            String value = context.getProperty(name).toString();
//
//            Assert.assertEquals("Global specified property is NOT the same in context", value, globalValues[index]);
//
//            // message("Context property:"+name+"="+value);
//
//        }
//
//        // Check context properties
//        String contextValues[] = new String[names.length];
//        for (index = 0; index < len; index++)
//        {
//            String name = names[index];
//            String value = name + "-CONTEXT-" + index;
//            contextValues[index] = value;
//            context.setProperty(name, value);
//        }
//
//        for (index = 0; index < len; index++)
//        {
//            String name = names[index];
//
//            String value = context.getProperty(name).toString();
//
//            Assert.assertEquals("Context specified property is NOT the same in context", value, contextValues[index]);
//
//            // message("Context property:"+name+"="+value);
//        }
//
//        // context should reflect Global settings:
//        VRSContext contextNew = new VRSContext();
//        for (index = 0; index < len; index++)
//        {
//            String name = names[index];
//
//            String value = contextNew.getProperty(name).toString();
//
//            Assert.assertEquals("Global specified property is NOT the same in NEW context", value, globalValues[index]);
//
//            // message("Context property:"+name+"="+value);
//        }
//    }
//
//}
