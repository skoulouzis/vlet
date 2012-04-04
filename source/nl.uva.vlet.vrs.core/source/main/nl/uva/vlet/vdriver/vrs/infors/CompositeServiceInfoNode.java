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
 * $Id: CompositeServiceInfoNode.java,v 1.1 2011-11-25 13:40:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:48 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.LogicalFolderNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * Non generic super class for Grid Info Nodes 
 */ 
public abstract class CompositeServiceInfoNode<T extends VNode> extends LogicalFolderNode<T>
{
    public CompositeServiceInfoNode(VRSContext context, VRL vrl)
    {
        super(context, vrl);
    }

    public String getMimeType() { return null;} 
    
    public boolean unlinkNode(VNode node) {return false;} 

    public boolean unlinkNodes(VNode nodes[]) {return false;} 
    
    public boolean save() {return false;} 
    
    public VRL getStorageLocation() throws VlException
    {
        return null; // doesn't have one
    }

    public void setLogicalLocation(VRL newRef) throws VlException
    {
        throw new VlInternalError("Can not set logical location of this node:"+this);  
    }    

}
