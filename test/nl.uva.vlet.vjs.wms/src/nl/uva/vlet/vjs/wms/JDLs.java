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
 * $Id: JDLs.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class JDLs extends VCompositeNode
{

    private VRSContext context;

    private VRL vrl;

    private JDLNode[] nodes;

    public JDLs(VRSContext ctx, VRL vrl, JDLNode[] nodes)
    {
        super(ctx, vrl);
        this.context = ctx;
        this.vrl = vrl;
        this.nodes = nodes;
    }

    @Override
    public String getType()
    {
        return "JDLs";
    }

    public VNode[] getNodes() throws VlException
    {
        return nodes;
    }

    public String[] getResourceTypes()
    {
        return null;
    }

    public JDLNode findChild(VRL vrl)
    {
        for (int i = 0; i < this.nodes.length; i++)
        {
            if (nodes[i].getVRL().equals(vrl))
            {
                return nodes[i];
            }
        }

        return null;
    }

}
