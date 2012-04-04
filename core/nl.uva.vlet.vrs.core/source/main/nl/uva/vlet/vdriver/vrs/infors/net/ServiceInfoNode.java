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
 * $Id: ServiceInfoNode.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vdriver.vrs.infors.grid.InfoNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

public class ServiceInfoNode extends InfoNode
{

    private PortInfo info;

    public ServiceInfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
    }

    public static ServiceInfoNode createNode(HostInfoNode hostInfoNode, VRL vrl,
            VRL targetVrl, PortInfo info)
    {
        ServiceInfoNode node=new ServiceInfoNode(hostInfoNode.getVRSContext(),vrl);
        node.setInfo(info); 
 
        node.setResourceVRL(targetVrl); 
        node.setShowShortCutIcon(false); 
        node.setTargetIsComposite(true); 
        node.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        String scheme=info.getProtocol(); 
        if (StringUtil.isEmpty(scheme))
            scheme="?";

        node.setName("port:"+info.port+" ["+scheme+"]"); 
        return node; 
    }

    protected void setInfo(PortInfo newInfo)
    {
        this.info=newInfo; 
    }


}
