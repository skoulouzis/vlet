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
 * $Id: startWrapNodeVBrowser.java,v 1.3 2011-04-18 12:27:25 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:25 $
 */
// source: 

package test;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNode;
import nl.uva.vlet.gui.proxynode.impl.proxy.ProxyWrapNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.vbrowser.VBrowserFactory;

// test ProxyWrap node browser:
public class startWrapNodeVBrowser
{

    public static void main(String args[])
    {
        try
        {
            ClassLogger.getRootLogger().setLevelToDebug();
            args = Global.parseArguments(args);

            // custom platform instance ! -> use wrap node
            UIPlatform plat = UIPlatform.getPlatform();

            // Option --native ? :
            // GuiSettings.setNativeLookAndFeel();

            // shiny swing metal look:
            plat.startCustomLAF();

            ProxyVRSClient.getInstance().setProxyNodeFactory(ProxyWrapNodeFactory.getDefault());

            println("GLOBUS_LOCATION        =" + Global.getProperty("GLOBUS_LOCATION"));
            println("env var 'VLET_INSTALL' =" + Global.getProperty("VLET_INSTALL"));
            println("Base installation      =" + Global.getInstallBaseDir());

            // prefetch MyVLe, during startup:
            ProxyVNode.getVirtualRoot();

            // start browser(s)
            {
                int urls = 0;

                for (String arg : args)
                {
                    println("arg=" + arg);

                    // assume that every non-option is a VRL:

                    if (arg.startsWith("-") == false)
                    {
                        // urls specfied:
                        urls++;
                        VBrowserFactory.getInstance().createBrowser(arg);
                    }
                    else
                    {
                        if (arg.compareTo("-debug") == 0)
                            Global.getLogger().setLevelToDebug();
                        // disable busy wait:
                        // else if (arg.compareTo("-noblock")==0)
                        // BrowserController.karma=3;
                    }
                }

                // no urls specified, open default window:
                if (urls == 0)
                {
                    // get home LOCATION: Can also be gftp/srb/....
                    // BrowserController.performNewWindow(TermGlobal.getUserHomeLocation());

                    VBrowserFactory.getInstance().createBrowser(UIGlobal.getProxyVRS().getVirtualRootLocation());
                }

                // BrowserController.performNewWindow("file:///home/ptdeboer/vfs2");
                // BrowserController.performNewWindow("gftp://fs2.das2.nikhef.nl/home1/ptdeboer/vfs");
                // bc.newWindow("srb:///");
            }

        }
        catch (VlException e)
        {
            Global.logException(ClassLogger.ERROR, startWrapNodeVBrowser.class, e, "***Error: Exception:" + e);
            ExceptionForm.show(e);
        }
    }

    public static void println(String msg)
    {
        Global.getLogger().debugPrintf("%s\n", msg);
    }
}
