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
 * $Id: InfoNode.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.grid;

import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.LogicalResourceNode;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;

/**
 * Info node is a logical resource node providing some (non editable)
 * information about a remote resource.
 */
public class InfoNode extends LogicalResourceNode
{
    /**
     * Creates new InfoNode object with logical location and specified
     * linkTarget. Set resolve==true to resolve the location and add (optional)
     * extra attributes acquired from target location.
     */
    public static InfoNode createServerInfoNode(VRSContext context, VRL logicalLocation, VRL targetVRL, boolean resolve)
            throws VlException
    {
        InfoNode lnode = new InfoNode(context, logicalLocation);
        lnode.init(logicalLocation, targetVRL, resolve);
        lnode.setShowShortCutIcon(false);
        lnode.setTargetIsComposite(true);
        lnode.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        return lnode;
    }

    private VAttributeSet infoAttrs;

    public InfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
        // Default to Resource Location;
        this.setType(VRS.RESOURCE_INFO_TYPE);
        setEditable(false);
    }

    public String getMimeType()
    {
        return null;
    }

    public InfoNode duplicate()
    {
        InfoNode infoNode = new InfoNode(vrsContext, getVRL());
        infoNode.copyFrom(this);
        if (this.infoAttrs != null)
        {
            infoNode.infoAttrs = this.infoAttrs.duplicate();
        }
        return infoNode;
    }

    public InfoNode clone()
    {
        return duplicate();
    }

    /**
     * Info nodes are not editable, their attributes are reflected by remote
     * information services like BDII
     */
    public boolean isEditable()
    {
        return false;
    }

    public LinkNode toLinkNode() throws NotImplementedException
    {
        LinkNode lnode = new LinkNode(this);
        return lnode;
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        VAttribute attr = null;

        if (infoAttrs != null)
        {
            attr = infoAttrs.get(name);
            if (attr != null)
                return attr;
        }

        return super.getAttribute(name);

    }

    public String[] getAttributeNames()
    {
        StringList names = new StringList(super.getAttributeNames());
        if (infoAttrs != null)
            names.add(infoAttrs.getAttributeNames());
        return names.toArray();
    }

    // extra information
    public void setInfoAttributes(VAttributeSet attrs)
    {
        this.infoAttrs = attrs;
    }

    public void setPresentation(Presentation pres)
    {
        super.setPresentation(pres);

    }
}
