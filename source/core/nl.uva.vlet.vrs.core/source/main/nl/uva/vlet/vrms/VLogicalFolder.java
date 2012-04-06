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
 * $Id: VLogicalFolder.java,v 1.3 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;

public interface VLogicalFolder extends VComposite, VLogicalResource
{
	/** Unlink from internal set, Does not call 'delete' on the VNode bute
	 * removes it from the internal vector only */ 
	boolean unlinkNode(VNode node) throws VlException;
	
	/** Unlink nodes from internal set
	 * Does not call 'delete' on the VNodes but
     * removes it from the internal vector only 
	 * @throws VlException */ 
	boolean unlinkNodes(VNode node[]) throws VlException;

	/** Find a child in this resource matching the VRL */ 
	public VNode findNode(VRL childVRL,boolean recurse) throws VlException; 
}
