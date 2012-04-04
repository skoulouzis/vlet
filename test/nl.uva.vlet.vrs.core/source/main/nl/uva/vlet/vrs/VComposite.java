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
 * $Id: VComposite.java,v 1.7 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.data.IntegerHolder;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;


/**
 * The Composite interface for VNodes which have 'child nodes' 
 * (for example: VDir). 
 * <p>
 * The method VNode.isComposite() can be used to check whether a node is composite 
 * (it can have child nodes) and implements this interface.  
 * <p>
 * @author P.T. de Boer 
 */
public interface VComposite 
{
    /** 
     * Returns allowed resource types which this node can 
     * have as child and/or create.
     * Only if a Resource is Composite and the source type is in this list the node can be 
     * added to this resource using addNode().
     * <p>  
     * <strong>VBrowser notes:</strong><br>
     * This method is also used by the VBrowser to check for valid 'drop' targets.
     * Types "File" and "Directory"  can be dropped on "Directory" types since these are the
     * valid ResourceTypes.
     * @see nl.uva.vlet.data.VAttributeConstants#ATTR_RESOURCE_TYPES   
     */ 
    public String[] getResourceTypes();
    
    /** 
     * Returns number of child nodes.  
     * @throws VlException */ 
    public long getNrOfNodes() throws VlException; /// Tree,Graph, Composite etc.

    /**
     * Returns Child Nodes. 
     * <p>
     * <b>implementation note:</b><br>
     * For large Composite nodes, override method {@link #getNodes(int, int, IntegerHolder)} as well.  
     * @throws VlException 
     */
    public VNode[] getNodes() throws VlException; /// Tree,Graph, Composite etc.

    /**
     * Returns range of Child Nodes starting at offset upto maxNodes. 
     * The IntegerHolder totalNumNodes returns the total amount of 
     * nodes in this resource. 
     * Multiple getNodes(...) calls may occure. It is up to the resource 
     * to make sure the index of the each returned node matches 
     * the actual stored nodes.  
     *   
     * @throws VlException */
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException; /// Tree,Graph, Composite etc.
    
    /**
     * Returns Child Node. 
     * @throws VlException */
    public VNode getNode(String name) throws VlException; /// Tree,Graph, Composite etc.
    
    /**
     * Add a node to the underlying Resource.
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems or on the same SRB Server.  
     * 
     * @param node
     * @param isMove
     * @return new created VNode
     * @throws VlException
     */
    public VNode addNode(VNode node,boolean isMove) throws VlException;
    
    /**
     * Add a node to the underlying Resource with a optional new name. 
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems or on the same SRB Server.  
     *  
     * @param node
     * @param isMove
     * @return new created VNode
     * @throws VlException
     */
  
    public VNode addNode(VNode node,String newName,boolean isMove) throws VlException;
    
    /**
     * Add specified nodes to the Resource. A drag and drop from the user interface
     * might call this method to add or 'drop' resource nodes to this composite
     * Node. 
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems. 
     */
    
    public VNode[] addNodes(VNode[] nodes,boolean isMove) throws VlException;
    
    /** 
     * VRS method to delete specified resource Node.
     * The method delNode() unlinks the child node from this composite node. 
     * To avoid circular calling of child.delete() -> parent.delNode() and back, 
     * the recommended way is to delete the child node first by calling child.delete() 
     * and the child calls the parent: (this)getParent().delNode(this).
     */ 
    public boolean delNode(VNode node) throws VlException;
    
    /** VRS method to delete specified resource Nodes */
    public boolean delNodes(VNode[] nodes) throws VlException;

    // Child Methods     
    
    /**
     * VNode method to create new Child.
     * 
     * @param type must be on of the types getResourceTypes() returns.  
     * @param name may be null. The implementation might choose a default name 
     *        or prompt the user. 
     * @param force means to create the child even if it already exists. 
     * @throws VlException  
     * */
    public VNode createNode(String type,String name,boolean force) throws VlException;
     
    /** 
     * Recursive delete. If recurse==true, then delete all child nodes then delete itself. 
     * If is recurse==false delete this resource only if it has no child nodes. 
     *   
     * @param recurse whether to delete its children also. 
     * @return Returns true upon success.  
     * 
     * @throws VlException  
     */
    //public boolean delete(boolean recurse) throws VlException;

    //public boolean isDeletable() throws VlException;
    
    /** Checks whether this node has a child with the specified name */ 
    public boolean hasNode(String name) throws VlException;
   
    /**
     * Return attribute matrix for given childs. 
     * The matrix should be in the form: VAttribute[childName][attrname].<br>
     * <b>Developers note:</b><br>
     * Override this method for a faster getall attributes. 
     * Also allow for entries in the name and node list to be null !
     * This is for attribute list merging and optimization ! 
     * 
     * @param childNames  list of child names 
     * @param attrNames   list of attribute names 
     * @return
     * @throws VlException 
     * @throws VlException
     */
   
    public VAttribute[][] getNodeAttributes(String[] childNames, String[] attrNames) throws VlException;
    
    /** Returns attributes for all childs */ 
    public VAttribute[][] getNodeAttributes(String attributeNames[]) throws VlException;
    
    /**
     * Override this method if your directory can be accessible
     * but not be 'readable'. 
     * For example a the contents of unix directory may not
     * be 'read' (list() will fails), but each individual 
     * file may be accessible (mode= --x). 
     *   
     * @throws VlException 
     */
    public boolean isAccessable() throws VlException;
   
    //public abstract VAttribute[][] getChildAttributes(VNode[] nodesSet, String[] attrNames); 
}
