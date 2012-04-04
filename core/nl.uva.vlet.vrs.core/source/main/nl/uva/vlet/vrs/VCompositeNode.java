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
 * $Id: VCompositeNode.java,v 1.6 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.IntegerHolder;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.error.ParameterError;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Super Class of all "Composite" Nodes. Extends VNode class by adding 
 * VComposite methods. 
 * 
 * @author P.T. de Boer
 */
public abstract class VCompositeNode extends VNode implements VComposite// ,VCompositeDeletable
{
    public VCompositeNode(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

    @Override
    public VNode[] getParents() throws VlException
    {
        VNode parents[]=new VNode[1]; 
        parents[1]=getParent();  
        return parents; 
    }

    public long getNrOfNodes() throws VlException
    {
        VNode nodes[] = getNodes();

        if (nodes != null)
            return nodes.length;

        return 0;
    }

    public VNode getNode(String name) throws VlException
    {
        if (name == null)
            return null;

        VNode nodes[] = getNodes();

        if (nodes == null)
            return null;

        for (VNode node : nodes)
            if (name.compareTo(node.getName()) == 0)
                return node;

        return null;
    }
    
    public VNode addNode(VNode node) throws VlException
    {
        return addNode(node, null, false);
    }
    
    public VNode addNode(VNode node, boolean isMove) throws VlException
    {
        return addNode(node, null, isMove);
    }

    public VNode[] addNodes(VNode[] nodes, boolean isMove) throws VlException
    {
        if (nodes == null)
            return null;

        VNode[] newNodes = new VNode[nodes.length];

        for (int i = 0; i < nodes.length; i++)
        {
            newNodes[i] = addNode(nodes[i], isMove);
        }

        return newNodes;

    }

    public boolean delNode(VNode node) throws VlException
    {
        if (node instanceof VDeletable)
            return ((VDeletable) node).delete();
        else
            throw new NotImplementedException("Resource cannot be deleted:"
                    + node);

    }

    public boolean delNodes(VNode[] nodes) throws VlException
    {
        boolean status = true;

        for (VNode node : nodes)
            status &= delNode(node);

        return status;
    }

    public boolean hasNode(String name) throws VlException
    {
        if (getNode(name) != null)
            return true;

        return false;
    }

    public VAttribute[][] getNodeAttributes(String childNames[],
            String names[]) throws VlException
    {
        VNode nodes[] = new VNode[childNames.length];

        VAttribute attrs[][] = new VAttribute[nodes.length][];

        for (int i = 0; i < childNames.length; i++)
        {
            nodes[i] = getNode(childNames[i]);

            if (nodes[i] != null)
                attrs[i] = nodes[i].getAttributes(names);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    public VAttribute[][] getNodeAttributes(String names[]) throws VlException
    {
        VNode nodes[] = getNodes();

        VAttribute attrs[][] = new VAttribute[nodes.length][];

        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] != null)
                attrs[i] = nodes[i].getAttributes(names);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    public boolean isDeletable() throws VlException
    {
        return true;
    }
    
    public boolean delete() throws VlException
    {
        return delete(false); 
    }

    //@Override
    public boolean isAccessable() throws VlException
    {
        return true;
    }

    @Override
    public boolean exists() throws VlException
    {
        return true;
    }
    
    public VNode createNode(String type, String name, boolean force)
            throws VlException
    {
        throw new NotImplementedException("Cannot create child type:" + type);
    }

    public boolean delete(boolean recurse) throws VlException
    {
        throw new NotImplementedException("Delete not implemented for Resource:"+this); 
    }
    
    @Override
    public VRL getHelp()
    {
        // return root help url
        return Global.getHelpUrl(getType());
    }

    public VNode addNode(VNode node, String newName, boolean isMove)
            throws VlException
    {
        throw new NotImplementedException("Not Implemented. Can not add node:"+node); 
    }

    /*public VNode[] getNodes() throws VlException
    {
    	return getNodes(0,-1,new IntegerHolder()); 
    }*/
    
    /**
     * Returns subSet of VNode array starting from offset upto offset+maxNodes. 
     * Total length of returned array might be smaller or equal to maxNodes; 
     * Override this method for optimized contents query. 
     * <p> 
     * Resource which have child nodes > 1000 should override this method !  
     *
     * @param offset         Starting offset
     * @param maxNodes       Maximum length of returned array. Actual size might be smaller  
     * @param totalNumNodes  Size of total contents. 
     * @return subSet of VNode[] array. 
     */
    
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException /// Tree,Graph, Composite etc.
    {
    	VNode allNodes[]=getNodes(); 
    	return nodesSubSet(allNodes,offset,maxNodes,totalNumNodes); 
    }
    
    
    /**
     * Returns subSet of VNode[] array starting from offset to offset+maxNodes. 
     * Total length of returned array is smaller or equal to maxNodes; 
     * 
     * @param nodes          Full VNode array
     * @param offset         Starting offset
     * @param maxNodes       Maximum length of returned array. If -1 = return ALL !   
     * @param totalNumNodes  Size of original VNode array 
     * @return
     */
    public static VNode[] nodesSubSet(VNode nodes[],int offset,int maxNodes,IntegerHolder totalNumNodes)
    {
    	// NiNo: 
    	if (nodes==null)
    		return null; 

    	if (offset<0) 
    		throw new ParameterError("Offset can not be negative:"+offset); 
    	
    	// original length 
    	int len=nodes.length; 
    	
    	// if maxNodes==-1 return ALL in array ! 
    	if (maxNodes<0)
    	   maxNodes=len; 
    	
    	// set VAR totalNumNodes: 
    	if (totalNumNodes!=null)
    		totalNumNodes.value=len;
    	
    	int start=offset;         // start in source array
    	int end=offset+maxNodes;  // end in source array
    	
    	// start beyond real length: 
    	if (start>=len)
    		return null; 
    
    	// truncate end; 
    	if (end>=len) 
    		end=len; 
    	
    	// start beyond truncated end 
    	if (start>=end) 
    		return null;
    	
    	// realsize 
    	int size=end-start; 
    	
    	//
    	// Dynamic Array allocation :
    	// Allocate new Array with most Specific Class Type
    	// to allow for downcasting to original (subclass) VNode type !
    	// 
    	VNode subNodes[] = (VNode[]) java.lang.reflect.Array.newInstance(
						nodes[0].getClass(), size);
    	
    	for (int i=0;i<size;i++)
    	{
    		subNodes[i]=nodes[offset+i]; 
    	}
    	
    	return subNodes; 
    }
    
    protected void fireChildAdded(VRL vrl)
    {
        this.vrsContext.fireEvent(ResourceEvent.createChildAddedEvent(this.getVRL(),vrl)); 
    }
    
    protected void fireSetChilds(VRL vrls[])
    {
        this.vrsContext.fireEvent(ResourceEvent.createSetChildsEvent(this.getVRL(),vrls)); 
    }
    
    protected void fireChildDeleted(VRL vrl)
    {
        this.vrsContext.fireEvent(ResourceEvent.createDeletedEvent(vrl)); 
    }
    
    protected void fireRefresh()
    {
        this.vrsContext.fireEvent(ResourceEvent.createRefreshEvent(this.getVRL())); 
    }
    

}
