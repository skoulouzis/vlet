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
 * $Id: ResourceTreeModel.java,v 1.10 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.vrl.VRL;

/**
 * @author P.T. de Boer
 */

public class ResourceTreeModel extends DefaultTreeModel // implements IProxyModel
{
    private static final long serialVersionUID = -8867159295018514115L;

    // === Instance === 
    
    private ViewModel viewModel = null;

    private Map<VRL, ResourceTreeNode> nodeCache=new Hashtable<VRL,ResourceTreeNode>();  
    
    public ResourceTreeModel(ViewModel _viewModel, TreeNode root, boolean asksForAllowedChildren)
    {
        super(root, asksForAllowedChildren);
        this.viewModel = _viewModel;
    }

    public ViewModel getViewModel()
    {
        return this.viewModel;
    }

    public  ResourceTreeNode getRoot()
    {
        return (ResourceTreeNode)super.getRoot(); 
    }
    
    public ViewNode getRootViewItem()
    {
        return getRoot().getViewItem();
    }

    // ========================================================================
    // Child manipulation methods 
    // ========================================================================
    
    public void setChilds(ResourceTreeNode targetNode, ViewNode[] items)
    {
        updateChilds(targetNode, items, false);
    }

    public void addChilds(ResourceTreeNode targetNode, ViewNode[] items)
    {
        updateChilds(targetNode, items, true);
    }

    protected synchronized void updateChilds(ResourceTreeNode targetNode, ViewNode childs[], boolean append)
    {
        // possible background thread: 

        ResourceTreeNode[] childNodes = null;
        int childIndices[]=null; 
        
        boolean changed = false;

        if ((targetNode.isPopulated() == false) || (append == false))
        {
            clearNode(targetNode,false);
            changed = true;
        }

        if ( (childs == null) || (childs.length<=0)) 
        {
            targetNode.setPopulated(true);
            // redraw: 
            this.uiFireStructureChanged(targetNode);
            return; // nodes already cleared  
        }
        
        int len=childs.length;
        
        childNodes = new ResourceTreeNode[len]; 
        childIndices =new int[len]; 
        // Process the directories
        for (int i = 0; (childs != null) && (i < childs.length); i++)
        {
            ViewNode iconItem = childs[i];
            if (iconItem != null)
            {
                VRL childLoc = iconItem.getVRL();
                ResourceTreeNode rtnode = null;

                // If child already added:
                // merge the two subsequent calls to setChilds,
                // just update the pnode with the same name !

                if ((rtnode = targetNode.getNode(childLoc)) != null)
                {
                    rtnode.setViewItem(iconItem);
                    continue; // child already exists;
                }

                try
                {
                    ResourceTreeNode newNode = new ResourceTreeNode(targetNode.getResourceTree(),iconItem);
                    
                    // add node, but do not fire event yet; 
                    childNodes[i] = newNode;
                    childIndices[i] = addNode(targetNode, newNode,false); 
                    
                    changed = true;
                }
                catch (Exception e)
                {
                    //debug("Exception:" + e);
                    e.printStackTrace();
                    childNodes[i]=null; 
                    childIndices[i]=-1; 
                }
            }
        }

        targetNode.setPopulated(true);
        
        if (changed)
        {
            if (append==true)
                this.uiFireNodesInserted(targetNode, childIndices); // insert
            else
                this.uiFireStructureChanged(targetNode); // redraw 
        }
    }
    
    /** Master method to add a (sub) node 
     * @param b */
    public int addNode(ResourceTreeNode parent, ResourceTreeNode node, boolean fireEvent)
    {
        // it now has at least one node: 
        parent.setAllowsChildren(true);
        int index=parent.addNode(node);
        
        if (fireEvent)
            uiFireNodeInserted(parent,index); 
        
        return index; 
    }

    public void clearNode(ResourceTreeNode node, boolean fireEvent)
    {
        // remove previous children, might alread been removed
        node.removeAllChildren();
        
        if (fireEvent)  
            uiFireStructureChanged(node); 
    }
   
    public int deleteNode(ResourceTreeNode node, boolean fireEvent)
    {
        // update model 
        ResourceTreeNode parent = node.getParent();
        int index=parent.removeNode(node); 
        
        // fire event: 
        if ((index>=0) && (fireEvent))
            this.uiFireNodeRemoved(parent,index,node);
        
        return index;
    }
    
    // ========================================================================
    // Fire Events: 
    // ========================================================================
    
    public void uiFireStructureChanged(final ResourceTreeNode node)
    {
      // Check UI Thread: 
      if (UIGlobal.isGuiThread() == false)
      {
          Runnable createTask = new Runnable()
          {
              public void run()
              {
                  uiFireStructureChanged(node);
              }
          };

          UIGlobal.swingInvokeLater(createTask);
          return;
      }
      
      // Update Tree. nodeStuctureChanged will post an Event !
      nodeStructureChanged(node);
      ResourceTree resourceTree = node.getResourceTree();      
      // also the tree size has been changed
      resourceTree.notifySizeChange();
      
    }
    
    protected void fireValueChanged(ResourceTreeNode node, ViewNode item)
    {   
        // this.fireTreeNodesChanged(node,node.createTreePath(),, path, childIndices, children);
        
        // fire name change event :
        valueForPathChanged(node.createTreePath(), item);
    }

 // Fire node changed event: updates node itself, not the structure.  
    protected void uiFireNodeChanged(final ResourceTreeNode node)
    { 
        // Check UI Thread: 
        if (UIGlobal.isGuiThread() == false)
        {
            Runnable createTask = new Runnable()
            {
                public void run()
                {
                    uiFireNodeChanged(node);
                }
            };

            UIGlobal.swingInvokeLater(createTask);
            return;
        }
        
        this.nodeChanged(node);
    }
   
    // Fire node changed event: updates node itself, not the structure.  
    protected void uiFireNodeRemoved(final ResourceTreeNode parent,int childIndex,final ResourceTreeNode child)
    {
        ResourceTreeNode childs[]=new ResourceTreeNode[1] ;
        int[] removedChildren=new int[1]; 
        removedChildren[0]=childIndex; 
        childs[0]=child;
        
        uiFireNodesRemoved(parent,removedChildren,childs); 
    }
    
    protected void uiFireNodesRemoved(final ResourceTreeNode parent,final int childIndices[],final ResourceTreeNode childs[])
    {
        // Check UI Thread: 
        if (UIGlobal.isGuiThread() == false)
        {
            Runnable createTask = new Runnable()
            {
                public void run()
                {
                    uiFireNodesRemoved(parent,childIndices,childs);
                }
            };

            UIGlobal.swingInvokeLater(createTask);
            return;
        }
        
        this.nodesWereRemoved(parent, childIndices, childs);
    }
      
    protected void uiFireNodeInserted(final ResourceTreeNode parent,int childIndex) 
    {
        int[] removedChildren=new int[1]; 
        removedChildren[0]=childIndex;
        uiFireNodesInserted(parent,removedChildren); 
    }
    
    protected void uiFireNodesInserted(final ResourceTreeNode parent,final int childIndices[]) //,final ResourceTreeNode childs[])
    {
        // Check UI Thread: 
        if (UIGlobal.isGuiThread() == false)
        {
            Runnable createTask = new Runnable()
            {
                public void run()
                {
                    uiFireNodesInserted(parent,childIndices); // ,childs);
                }
            };

            UIGlobal.swingInvokeLater(createTask);
            return;
        }
        
        this.nodesWereInserted(parent, childIndices); // , childs);
        
    }
    
    // ========================================================================
    // Search methods 
    // ========================================================================
    
    
    /**
     * Find nodes which have the specified ProxyLocator. 
     * Method peforms a depth-first recursive tree walk. 
     */
    public List<ResourceTreeNode> findNodes(VRL locator, boolean checkLinkTargets)
    {
        ResourceTreeNode current=this.getRoot();
        Vector<ResourceTreeNode> nodes=new Vector<ResourceTreeNode>(); 
        return findNodes(nodes,current,locator,checkLinkTargets); 
    }

    protected List<ResourceTreeNode> findNodes(List<ResourceTreeNode> nodes, 
            ResourceTreeNode node, VRL locator, boolean checkLinkTargets)
    {
        
        // Todo: Caching optimizing, etc: 
        
        // check parent: 
        if (node.getVRL().equals(locator))
            nodes.add(node); 
        
        ResourceTreeNode[] childs = node.getChilds();
        for (ResourceTreeNode child:childs)
        {
            // peak ahead: skip one recursion if child is leaf:  
            if (child.hasChildren()==false)
            {
                if (compareLocations(child,locator,checkLinkTargets)) 
                    nodes.add(child); 
            }
            else
            {
                // go into recursion: 
                findNodes(nodes,child,locator,checkLinkTargets); 
            }
        }
        
        return nodes; 
    }

    static boolean compareLocations(ResourceTreeNode node, VRL loc, boolean checkLinkTargets)
    {
        ViewNode item=node.getViewItem(); 
        // plain VRL compare:
        if (item.equalsLocation(loc,checkLinkTargets)==true)
            return true;
        
        // Todo Fix: Cached method to resolve VRL aliases !  
        // For example target Vrl (=alias) could be : gftp://host/~/path"
        // but actual (resolveD) target Vrl could be: gftp://host/home/user/path" 
        // or even a 'full' hostname like           : gftp://host.domain.org/home/user/path
        // If an node is openened which has an alias the cached ProxyNode has these VRLs! 
        
        if (checkLinkTargets==false)
            return false; 
        
        // Check cache for 'resolved' aliases 
        ProxyNode pnode = ProxyNode.getProxyNodeFactory().getFromCache(item.getTargetVRL());
        
        if (pnode!=null)
        {
            // get ALL 'equivalent' VRLs ! 
            VRL vrls[]=pnode.getAliasVRLs();
            if (vrls==null) 
                return false;
            
            // check all aliases ! 
            for (VRL vrl:vrls)
               if (loc.equals(vrl))
                   return true;
        }
        
        return false;  
    }
   
}
