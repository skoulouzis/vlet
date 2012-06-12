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
 * $Id: LogicalFolderNode.java,v 1.4 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import java.util.Vector;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;


/**
 * Logical Folder node. Super class for ResourceFolder(s); 
 * Contains thread save folder which holds <T extends VNode> Tree structure in memory. 
 * 
 * @author Piter T. de Boer 
 */
public abstract class LogicalFolderNode<T extends VNode> extends VCompositeNode implements VLogicalFolder
{
	 /** Thread save Vector -> use as mutex during manipulation */  
	protected Vector<T> childNodes=new Vector<T>(); 
	
	protected LogicalFolderNode<? extends VNode> logicalParent;

    private boolean isEditable=true; 
    
    protected VAttributeSet attributes=new VAttributeSet(); 

	public LogicalFolderNode(VRSContext context, VRL vrl) 
	{
		super(context, vrl);
	}

	public LogicalFolderNode(VRSContext context) 
	{
		super(context, null);
	}
	
	public String[] getAttributeNames()
	{
	    StringList list=new StringList(super.getAttributeNames()); 
	    list.merge(attributes.getAttributeNames());
	    // No MimeTypes for folders!
	    list.remove(VAttributeConstants.ATTR_MIMETYPE); 
	    return list.toArray(); 
	}
	
	@Override
    public String getIconURL()
    {
        return this.attributes.getStringValue(VAttributeConstants.ATTR_ICONURL); 
    }
    
    public void setIconURL(String iconUrl)
    {
        this.attributes.set(VAttributeConstants.ATTR_ICONURL,iconUrl);  
    }
	
	public VAttribute getAttribute(String name) throws VlException
	{
	    VAttribute attr=this.attributes.get(name);
	    
	    if (attr!=null)
	    {
	        attr.setEditable(this.isEditable); 
	        return attr;
	    }
	    
	    return super.getAttribute(name); 
	}
	
	
	public boolean setAttribute(VAttribute attr) throws VlException
    {
	    return setAttribute(attr,false); 
    }
	
	/**
	 * Set attribute if the attribute is already in the AttributeSet. 
	 * If setIfNoteSet==true the value will always be updated. 
	 */
	public boolean setAttribute(VAttribute attr, boolean setIfNotSet) throws VlException
	{
	    // only set if already specified in attributes; 
	    if ((setIfNotSet==true) || (this.attributes.containsKey(attr.getName())))
	    {
	        this.attributes.put(attr);
	        return true; 
	    }
	    
	    return false; 
	    // super.setAttribute(attr); 
    }
	
	// @Override
    public boolean setAttributes(VAttribute[] attrs) throws VlException
    {
        boolean all=true; 
        for (VAttribute attr:attrs)
        {
            boolean result=this.setAttribute(attr);
            all=all&&result; 
        }
        
        return all;  
    }
    
    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }
    
    public boolean isEditable()
    {
        return this.isEditable;  
    }
    
	public void setLogicalLocation(VRL newRef) throws VlException
	{
		this.setLocation(newRef); 
	}

	@SuppressWarnings("unchecked")
	public void setLogicalParent(VNode node)
			throws ResourceTypeMismatchException
	{
		if (node instanceof LogicalFolderNode)
			throw new ResourceTypeMismatchException("LogicalNode can only be stored in logical Folders"); 
		
		this.logicalParent=(LogicalFolderNode<VNode>)node; 
	}
	
	 
	public VNode findNode(VRL location,boolean recurse) throws VlException
	{
	    //debug("Find subnode:"+this+" checking: "+location);
		
		VNode[] nodes = this.getNodes(); 
		
		for (VNode node:nodes)
		{
			if (node.getLocation().compareTo(location)==0)
			{
				//debug("Find subnode: Found:"+location); 
				return node;
			}
			else if (recurse && node.getLocation().isParentOf(location))
			{
				  // go into recursion: 
                if (node instanceof VLogicalFolder)
                {
                    return ((VLogicalFolder)node).findNode(location,true); 
                }
                else
                {
                	// recurse myself ?
                }
                
			}
		}
		return null;
	}
	
	public boolean unlinkNodes(VNode nodes[])
	{
		boolean result=true; 
		
		for (VNode node:nodes)
		{
			boolean val=unlinkNode(node);
			result=result&&val;
		}
		
		return result; 
	}
	 
	public VNode[] getNodes() throws VlException 
	{
	    return _getNodes();
	}
	
	protected VNode[] _getNodes() throws VlException
	{
		if ((childNodes==null) || (childNodes.size()<=0))
			return null; 
		
		synchronized(childNodes)
		{
			VNode arr[]=new VNode[childNodes.size()]; 
			//todo cannot convert to T[] array 
			return childNodes.toArray(arr); 
		}
	}
	
	public T[] toArray(T array[])
    {
		if(this.childNodes==null)
			return null;
	
		return this.childNodes.toArray(array); 
	}
	
	/**
	 * Deletes node from current resource group. 
	 * The behaviour is similar to unlink(). 
	 */
	public boolean delNode(VNode node)
	{
		return unlinkNode(node);
	}

	public boolean unlinkNode(VNode node)
	{
		return delSubNode(node);
	}
	
	/** Protected implementation which deletes the specified node from the internal vector */ 
	protected boolean delSubNode(VNode node)
	{
		if (node==null)
			return false; 
		
		boolean hasNode=false; 
		
		synchronized(childNodes)
		{
			hasNode=childNodes.remove(node);
			
		    // check by VRL: equals() method might not return true in all cases!  
			T other=this.getSubNode(node.getVRL()); 
			if (other!=null)
			{
				childNodes.remove(other);
				hasNode=true; 
			}
		}
		
		save();
		
		return hasNode; 
	}
	
	/**
	 * Protected implementation which added the specified node from the internal vector. 
	 * If the exact node (equals()==true) already exists nothing changes. 
	 * If a VNode with the SAME VRL already exists, it will be removed!
	 * @throws VlException 
	 * 
	 */ 
    protected void addSubNode(T node) throws VlException
    {
        if (node==null)
            return; 
        
        boolean hasNode=false; 
        
        synchronized(childNodes)
        {
            // A Vector DOES allow duplicates, but we all node shoulds be unique:
            if (childNodes.contains(node))
            {
                //childNodes.remove(node);
                return; 
            }
            
            // check by VRL: equals() method might not return true in all cases!  
            VNode other=this.getSubNode(node.getVRL()); 
            if (other!=null)
            {
                childNodes.remove(other);
                hasNode=true; 
            }
            
            this.childNodes.add(node); 
        }
        // for persistant nodes: 
        save();
        // return hasNode; 
    }
	
	/** Check internal child node array */ 
	protected T getSubNode(VRL vrl)
	{
		synchronized(this.childNodes)
		{
			for (T child:childNodes)
			{
				if (child.getVRL().equals(vrl))
					return child; 
			}
		}
		
		return null; 
	}

//	public VNode getNode(VRL vrl) throws VlException
//	{
//		// use array to loop over 
//		for (VNode child:getNodes())
//		{
//			if (child.getVRL().compareTo(vrl)==0) 
//				return child; 
//		}
//		return null; 
//	}
	
	/**
	 * Store childs in internal Vector. Current Childs vector is cleared then all
	 * nodes are added. 
	 */
	protected void setChilds(T nodes[])
	{
		synchronized(childNodes)
		{
			this.childNodes.clear(); 
			for (T node:nodes)
			{
				this.childNodes.add(node); 
			}
		}
	}
	
	/** Clear internal childs vector. */ 
	protected void clearChilds() 
	{
		synchronized(childNodes)
		{
			this.childNodes.clear(); 
		}
	}
	
	// =======================================================================
	// Abstract interface 
	// =======================================================================
	
	public boolean save() {return false;} // by default not persistant! 
	
	public VRL getStorageLocation() throws VlException {return null;}  

	// Minimum of Methods ? 
	abstract public String getType();  

	abstract public String[] getResourceTypes(); 	
}
