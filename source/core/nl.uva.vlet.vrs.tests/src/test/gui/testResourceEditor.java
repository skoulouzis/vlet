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
 * $Id: testResourceEditor.java,v 1.5 2011-05-02 13:28:46 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:46 $
 */ 
// source: 

package test.gui;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.editors.ResourceEditor;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.LogicalResourceNode;
import nl.uva.vlet.vrms.ResourceFolder;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class testResourceEditor
{
    public static void main(String args[])
    {
        // Global.setDebug(true);
        UIGlobal.init();
        ProxyVNodeFactory.initPlatform();

        try
        {
            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.lfc.LFCFSFactory.class);
            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.srm.SRMFSFactory.class);

            VRSContext context = VRSContext.getDefault();

            LogicalResourceNode node = null;

            StringList idList = new StringList(UIGlobal.getVRSContext().getServerInfoRegistry().getServerIDs());

            System.out.println("Current ID list=" + idList);

            VRL lfcVRL = new VRL("lfn://lfc.grid.sara.nl:5010/grid/");

            context.getServerInfoRegistry().reload();

            ServerInfo info = context.getServerInfoRegistry().getServerInfoFor(lfcVRL, true);
            info.store();

            node = LinkNode.createServerNode(context, new VRL("myvle:/0"), lfcVRL);

            node.saveAtLocation(new VRL("file:///" + Global.getUserHome() + "/resourceNode1.vlink"));

            ProxyNode pnode = ((ProxyVNodeFactory) ProxyNode.getProxyNodeFactory()).createFrom(node);
            ResourceEditor.editProperties(pnode, true);

            node = LinkNode.loadFrom(context, new VRL("file:///" + Global.getUserHome() + "/.vletrc/myvle/001.vlink"));
            pnode = ((ProxyVNodeFactory) ProxyNode.getProxyNodeFactory()).createFrom(node);

            ResourceEditor.editProperties(pnode, false);
            ResourceEditor.editProperties(pnode, true);

            ResourceFolder rfnode = ResourceFolder.createFrom(context.openLocation("file:///" + Global.getUserHome()
                    + "/.vletrc/myvle/000.vrsx"));

            rfnode.setLogicalLocation(new VRL("myvle", null, "xxxx"));

            pnode = ((ProxyVNodeFactory) ProxyNode.getProxyNodeFactory()).createFrom(rfnode);
            ResourceEditor.editProperties(pnode, true);

            VNode vnode = context.openLocation(new VRL("file:///" + Global.getUserHome() + "/.vletrc/myvle"));
            pnode = ((ProxyVNodeFactory) ProxyNode.getProxyNodeFactory()).createFrom(vnode);
            ResourceEditor.editProperties(pnode, false);
            ResourceEditor.editProperties(pnode, true);

            System.out.println("--- Dialog Ended ---");

            VAttribute[] attrs = node.getAttributes();

            int i = 0;
            for (VAttribute a : attrs)
            {
                System.out.println("Attrs[" + i++ + "]=" + a);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
