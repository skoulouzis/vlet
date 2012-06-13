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
 * $Id: ResourceTreeNode.java,v 1.10 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;


import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

/**
 * Class that represents a node in the ResourceTree. 
 */
public class ResourceTreeNode implements MutableTreeNode, VComponent
{
    private static final long serialVersionUID = -6432485709319244412L;

    private static int nodeCounter = 0;

    // ===
    // Instance 
    // ===
    
    // Owner of this node 
    private ResourceTree resourceTree = null;
 
    /** Keep reference to parent! */ 
    private ResourceTreeNode parent=null;
    
    private Vector<ResourceTreeNode> childs=new Vector<ResourceTreeNode>(); 
   
    // true if node is populated
    private boolean populated = false;

    private boolean allowsChildren=false; 

    // User Object
    private ViewNode viewItem;

    // statistics 
    private int id = nodeCounter++;

    public ResourceTreeNode(ResourceTree tree, ViewNode iconItem)
    {
    	init(tree,iconItem);  
    	//debug(">>> NEW NODE:"+this);
    }
    
    private void init(ResourceTree tree,ViewNode item)
    {
        this.resourceTree = tree;

        // debug.println("isDir:"+pnode.getName()+"="+isComposite);

        // Important!: Hold the ProxyTNode as the user object.
        init(item); 
    }
 
    private void init(ViewNode item)
	{
        setUserObject(item);
      
        // set whether this is a composite node (VDir) or net.
        this.allowsChildren=item.isComposite(); 

        //this.allowsChildren=pnode.isComposite();
	}
    

    @Override
    public void insert(MutableTreeNode child, int index)
    {
        synchronized(this.childs)
        {
            this.childs.insertElementAt((ResourceTreeNode) child,index); 
        }
    }
    
    protected int addNode(ResourceTreeNode node) 
    {
        synchronized(this.childs)
        {
            int index=this.childs.size(); 
            this.childs.add(node);
            node.setParent(this);
            return index; 
        }
    }

    @Override
    public void remove(int index)
    {
        synchronized(this.childs)
        {
            ResourceTreeNode node=this.childs.get(index); 
            this.childs.remove(index);
            node.dispose();
        }
    }

    @Override
    public void remove(MutableTreeNode node)
    {
        removeNode((ResourceTreeNode)node); 
    }
    
    public int removeNode(ResourceTreeNode node)
    {
        synchronized(this.childs)
        {
            int index=this.childs.indexOf(node); 
            if (index>=0) 
                this.childs.remove(index);
            
            node.dispose();
            
            return index; 
        }
        
    }

    @Override
    public void removeFromParent()
    {
        if (parent!=null)
        {
            parent.remove(this);
            this.parent=null;
        }
        
        dispose(); 
    }


    @Override
    public void setParent(MutableTreeNode newParent)
    {
        //debug("new parent for:"+this); 
        //debug("new parent is:"+newParent); 
        
        // Assert!
        if (this.equals(newParent))
            throw new Error("*** Parent is myself!");
        
        this.parent=(ResourceTreeNode)newParent; 
    }

    @Override
    public void setUserObject(Object object)
    {
        this.viewItem=(ViewNode)object;
    }
   

    @Override
    public boolean getAllowsChildren()
    {
        return this.allowsChildren; 
    }

    @Override
    public TreeNode getChildAt(int index)
    {
        if ((index<0) || (index>childs.size()) )
                throw new IllegalArgumentException("No child at index:"+index); 
    
        return this.childs.get(index);
    }

    @Override
    public int getChildCount()
    {   
        return this.childs.size(); 
    }

    @Override
    public int getIndex(TreeNode node)
    {
        return childs.indexOf(node); 
    }

    @Override
    public boolean isLeaf()
    {
        return (this.childs.size()==0);
    }
    
    public boolean isRoot()
    {
        return this.equals(resourceTree.getRootNode());
    }
    
    public ViewNode getViewItem()
    {
 	   return this.viewItem;
    }
    
    public void setViewItem(ViewNode item)
    {
    	this.setUserObject(item); 
    }
  
    public VRL getVRL()
    {
        return getViewItem().getVRL();
    }
    
    public ResourceRef getResourceRef()
    {
        return getViewItem().getResourceRef();
    }

    public ResourceTreeNode getRoot()
    {
    	return this.resourceTree.getRootNode();  
    }
    
    public String getName()
    {
    	if (this.isRoot())
    		if (getVRL().hasScheme(VRS.MYVLE_SCHEME)==false)
    			return this.getRoot().getVRL().toString(); 
    	
    	return getViewItem().getName(); 
    }
 
    Boolean populatedMutex = new Boolean(true);
   
    public String toString()
    {
    	return "{ResourceTreeNode:"+getVRL()+"}"; 
    }

    public MasterBrowser getMasterBrowser()
    {
        return this.resourceTree.getMasterBrowser();
    }

    /**
     * Mutex object to wait for when busy. This object will receive
     * the notify() event, so performing a wait() on this object
     * will result in being waked up when the node is not busy anymore 
     */
    private final Boolean busyWaitMutex = new Boolean(true);

    /** Update buxy status */
    public void notifyBusyEvent(boolean boolVal)
    {
        // notify waiting threads: 

        if (boolVal == false)
            if (isBusy() == false)
            {
                synchronized (busyWaitMutex)
                {
                    busyWaitMutex.notifyAll();
                }
            }

    }

    /** Return whether the ProxyTNode isBusy */
    public boolean isBusy()
    {
        return getViewItem().isBusy();
    }

    /** Wait until the node is populated */
    public void waitForPopulated()
    {
        if (populated == true)
            return;

        try
        {
            synchronized (populatedMutex)
            {
                populatedMutex.wait();
            }
        }
        catch (InterruptedException e)
        {
            // continue:
            Global.errorPrintf(this, "InterruptedException:%s\n",e);
        }

    }

	public VContainer getVContainer()
	{
		return this.resourceTree;
	}

	public String getResourceType()
	{
		return this.getViewItem().getResourceType(); 
	}

	public boolean isPopulated()
	{
		return this.populated;
	}

	public void setPopulated(boolean val)
	{
		this.populated=val;
		
		if (populated)
		{
			synchronized(populatedMutex)
			{
				this.populatedMutex.notifyAll();
			}
		} 
		
	}

	public ResourceTreeNode getParent()
	{
		return this.parent; 
	}

	/** Creates new TreePath to this node */ 
	public TreePath createTreePath()
	{
		return new TreePath(this.getPath()); 
	}

    public ResourceTree getResourceTree()
    {
        return this.resourceTree; 
    }

    public void removeAllChildren()
    {
        this.childs.clear();
    }

    public void setAllowsChildren(boolean value)
    {
       this.allowsChildren=value;
    }

    public ResourceTreeNode getFirstChild()
    {
        if (this.childs.size()<=0) 
            return null;
        
        return this.childs.get(0); 
    }

    /** Used linear search in childs Vector to get to sibling object */ 
    public ResourceTreeNode getNextSibling()
    {
        if (parent==null)
            return null; 
        
        return parent.getNextChild(this); 
    }
    
    /** Used linear search in childs Vector to get to sibling object */
    public ResourceTreeNode getNextChild(ResourceTreeNode child)
    {
        //debug("getNextChild:"+child); 
        
        synchronized(this.childs)
        {
            int index=childs.indexOf(child);  

            if (index<0)
                return null;

            if (index+1>=childs.size())
                return null; 
        
            return childs.elementAt(index+1);
        }
    }
    
    @Override
    public Enumeration<ResourceTreeNode> children()
    {
        return this.childs.elements(); 
    }
    
    public ResourceTreeNode[] getPath()
    {
        Vector<ResourceTreeNode> paths=new Vector<ResourceTreeNode>(); 
        
        ResourceTreeNode current=this; 
        while(current!=null)
        {
            paths.add(current);  
            
            if (current.isRoot())
                break;
            
            ResourceTreeNode prev=current; 
            current=prev.getParent();
            
            if (current==null)
                throw new NullPointerException("*** NULL Parent for non root node:"+this);
            
            // Grr.Arrgg.
            if (current.equals(prev))
                throw new Error("*** Parent points to itself:"+current);
        }
        
        ResourceTreeNode _arr[]=new ResourceTreeNode[paths.size()];
        int len=paths.size(); 
        
        //inverse path
        for (int i=0;i<len;i++)
            _arr[len-i-1]=paths.get(i); 
        return _arr; 
    }

    private void dispose()
    {
        this.childs.clear();
        this.parent=null;
        this.viewItem=null;
    }

    public ResourceTreeNode getNode(VRL vrl)
    {
        // could be done faster!
        for (ResourceTreeNode node:this.childs)
            if (vrl.equals(node.getVRL()))
                return node;
        
        return null; 
    }

    public void updateName(String value)
    {
        getViewItem().setName(value);
        this.resourceTree.getModel().fireValueChanged(this,viewItem); 
        
//        getModel().valueForPathChanged(
//                new TreePath(targetNode.getPath()), targetNode.getViewItem());
//        }
        
    }

    public void updateIconURL(String value)
    {
        getViewItem().updateIconURL(value); 
        this.resourceTree.getModel().fireValueChanged(this,viewItem);
    }

    /** Return private array */
    public ResourceTreeNode[] getChilds()
    {
        ResourceTreeNode arr[]=new ResourceTreeNode[this.childs.size()]; 
        arr= this.childs.toArray(arr);
        return arr; 
   }

    public boolean hasChildren()
    {
        return (this.childs.size()>0);
    }
}
