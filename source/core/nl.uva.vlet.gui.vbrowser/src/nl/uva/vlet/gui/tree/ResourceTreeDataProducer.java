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
 * $Id: ResourceTreeDataProducer.java,v 1.14 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.util.List;

import javax.swing.tree.TreePath;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.proxymodel.ProxyDataProducer;
import nl.uva.vlet.gui.proxynode.ViewNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;

/** 
 * Under construction: Move all updating methods to this class 
 * and use ProxyModel+ProxyDataProducer ! 
 * 
 * @author ptdeboer
 *
 */
public class ResourceTreeDataProducer extends ProxyDataProducer 
       implements ProxyResourceEventListener
{
	private int defaultIconSize=16; 
	
	private ResourceTree resourceTree;

	private ResourceTreeModel treeModel;

	public ResourceTreeDataProducer(ResourceTree tree,ProxyNodeFactory factory,ResourceTreeModel model)
	{
		super(tree.getMasterBrowser(),factory,model.getViewModel()); 
		this.resourceTree=tree;
		this.treeModel=model; 

		// Listeners 
		ProxyVRSClient.getInstance().addResourceEventListener(this);
	}

	public ResourceTreeModel getModel()
	{
		return treeModel;
	}

	ResourceTree getTree()
	{
		return resourceTree;
	}


	MasterBrowser getMasterBrowser()
	{
		return resourceTree.getMasterBrowser(); 
	}

	/**
	 * Process underlying resource changes. 
	 * Is Called by ProxyNode.  
	 * Currently the ResourceTree receives the Event. 
	 * Nicer method would be to implement own TreeModel. 
	 * This suffices for now ! 
	 * 
	 * @param e
	 */

	public void notifyProxyEvent(ResourceEvent e)
	{
		//debug("Event:" + e);

		List<ResourceTreeNode>  nodes = findNodes(e.getSource(),true); 

        if ((nodes == null) || (nodes.size()<0) )
        {
            //debug("Node node for:" + e.getSource());
            return;
        }
        
        // handle each node: 
        
        for (ResourceTreeNode node:nodes)
        {
    		// filter events:
    		switch (e.getType())
    		{
    			case SET_ATTRIBUTES:
    			{
    				VAttribute[] attrs = e.getAttributes();
    				for (VAttribute attr:attrs)
    				{
    					// check name:
    
    					if ((attr!=null) && (attr.hasName(VAttributeConstants.ATTR_NAME)))
    					{
    						// name attribute change, but it is NOT a rename! 
    						// (OID hasn't changed) 
    					    node.updateName(attr.getStringValue()); 
                            
    					}
    					else if (attr.hasName(VAttributeConstants.ATTR_ICONURL))
                        {
                            // name attribute change, but it is NOT a rename! 
                            // (OID hasn't changed) 
    					    node.updateIconURL(attr.getStringValue()); 
                        }
    				}
    				break; 
    			}
    			case SET_CHILDS:
    				bgGetNodesFor(node.getVRL(),e.getChilds(),false); //handleSetChildsFor(targetNode,e.childs);
    				break;
    			case REFRESH:
    				refresh(node); //node.rescan(true); 
    				break;
    			case DELETE:
    				getModel().deleteNode(node,true);
    				break;
    			case CHILDS_ADDED:
    				// optimization: 
    				// childs need not to be added to unpopulated node 
    				if (node.isPopulated() == false)
    					break;
    
    				backgroundAddChildsTo(node,e.getChilds());
    
    				break;
    			case RENAME:
    
    				doRename(node,e.getNewVRL());
    				break;
    			case SET_BUSY:
    			    node.getViewItem().setBusy(e.getBoolVal());
    				getModel().valueForPathChanged(new TreePath(node.getPath()),
    				        node.getViewItem());
    				node.notifyBusyEvent(e.getBoolVal());
    				break;
    			default:
    				//debug("***Warning: Unknown EventType:" + e);
    			break;
    		}
    		
        } // for all nodes: 
	}

	private void doRename(ResourceTreeNode node, VRL newLocation)
	{
		//
		// TODO: proper model rename without using cache. 
		// 
		
		// I) name has changed but location hasn't
		if (node.getVRL().equals(newLocation))
		{
			//todo: proper update ! 
			ProxyNode pnode=ProxyNode.getProxyNodeFactory().getFromCache(newLocation);

			if (pnode!=null)
			{
				
				//Update new Name only  
				ViewNode item=node.getViewItem();
				item.setName(pnode.getName());

				getModel().fireValueChanged(node,item); 
			}

			return;
		}

		// II) Use cache for speed. 
		ResourceTreeNode parent=node.getParent();

		ProxyNode pnode=ProxyNode.getProxyNodeFactory().getFromCache(newLocation);

		if (pnode!=null)
		{
			ViewNode item=createIconItem(pnode); 
			
			node.setViewItem(item);
			getModel().fireValueChanged(node,item);

			// reload !

			refresh(node); 
			return; 
		}

		// NOT IN CACHE: 
		//
		// Lazy Rename: Just delete+refetch !
		// the whole subtree has to be updated ! 
		//

		this.getModel().deleteNode(node,true); 
		VRL vrls[]=new VRL[1];
		vrls[0]=newLocation; 

		this.backgroundAddChildsTo(parent,vrls); 
	}

	protected ViewNode createIconItem(ProxyNode pnode)
	{
		return ViewNodeFactory.createViewNode(pnode,this.defaultIconSize);
	}
	
	protected ViewNode[] createIconItems(ProxyNode pnodes[])
	{
		if (pnodes==null)
			return null; 
		
		ViewNode items[]=new ViewNode[pnodes.length]; 
		
		for (int i=0;i<pnodes.length;i++)
		{
			items[i]=createIconItem(pnodes[i]); 
		}
		
		return items; 
	}

	private void backgroundAddChildsTo(ResourceTreeNode targetNode, VRL[] childs)
	{
		this.bgGetNodesFor(targetNode.getVRL(), childs,true); 
	}

	protected List<ResourceTreeNode> findNodes(VRL location, boolean checkLinkTargets)
	{
		// move to model ? 
		return resourceTree.findNodesWithLocation(location, checkLinkTargets);
	}


	public void updateChildNodesFor(ProxyNode parent, ProxyNode[] childs,boolean cumulative)
	{
	    List<ResourceTreeNode>  nodes = this.findNodes(parent.getVRL(), true); 
	    

		if ((nodes==null) || (nodes.size()<=0))
		{
			// debug("***Warning. updateChildNodesFor(): Couldn't find nodeRecieved update for node in tree:"+parent);
		}
		else
		{
		    for (ResourceTreeNode node:nodes)
		    {
		        // create set of new ViewItems per RTNode: 
		        ViewNode items[]=createIconItems(childs); 
			
		        getModel().updateChilds(node,items,cumulative); // cumulative==true => append
		    }
		}
	}

	public void refresh(ResourceTreeNode node) 
	{
        if (checkAccess(node.getVRL())==false)
            return; 
        
		this.bgGetChildsFor(node.getVRL()); 
	}

	public boolean checkAccess(VRL vrl)
    {
        final MasterBrowser bc=getMasterBrowser();
        
        if (bc instanceof BrowserController)
        {
            return ((BrowserController)bc).interactiveCheckAuthenticationFor(vrl);
        }
        
        return true; 
    }
   
	public void dispose()
	{
		ProxyVRSClient.getInstance().removeResourceEventListener(this); 
	}

	public void updateRootnode(ProxyNode pnode)
	{
		ResourceTreeNode rootNode=new ResourceTreeNode(resourceTree,ViewNodeFactory.createViewNode(pnode,defaultIconSize));
		 
		this.getModel().setRoot(rootNode);

		// fetch children: 
		bgGetChildsFor(pnode.getVRL());
	}

}	
