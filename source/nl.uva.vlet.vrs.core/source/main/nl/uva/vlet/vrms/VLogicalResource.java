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
 * $Id: VLogicalResource.java,v 1.3 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

/** 
 * Interface for 'logical' resources which may or may not
 * point to an actual physical resource like: LinkNodes, ServerNodes
 * and ResourceFolders including MyVle.
 *   
 * This interface is used to make the distinction between normal VNodes
 * and special (Logical) Resources within the VRS. 
 * All LogicalResource nodes can exchange child nodes. 
 * Most of the implementation classes are now in the vrms package and this 
 * interface is (kind of) used to detected those classes. 
 * The StorageLocation typically is file with .vrsx (Folder) or .vlink (single resource)
 * as extension. 
 *  
 * @author P.T. de Boer 
 */

public interface VLogicalResource
{
	/**
	 * The StorageLocation where the Resource <i>Description</i> is stored.
	 * Typically this is a file or NULL when the ResourceNode isn't persistent. 
	 * For VLink this could be a ".vlink" file.
	 *  
	 * ResourceFolder use .vrsx files to store a complete resource group. 
	 */
	public VRL getStorageLocation() throws VlException;
	 
	/** 
	 * Sets the Logical Location VRL.  
	 * This method sets the location of the node, but to make the distinction
	 * <p><b>Warning:</b> 
	 * This methods breaks the paradigm that VNode should not change their Location during
	 * the lifetime of the object and my only be used to update the Logical Node structure
	 * for example when reading a node or resource group from a file. 
	 */ 
	public void setLogicalLocation(VRL newRef) throws VlException;
	
	/**
	 * Set logical parent node. As Logical Node can move between parents, 
	 * their location and parent can change during the life time of an object. 
	 * VNode.getParent() returns this parent  
	 */ 
	public void setLogicalParent(VNode node) throws ResourceTypeMismatchException;
	
	
	
}
