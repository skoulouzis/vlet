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
 * $Id: ResourceTree.java,v 1.12 2011-06-10 10:29:49 ptdeboer Exp $  
 * $Date: 2011-06-10 10:29:49 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dnd.VDragGestureListener;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;

/**
 * The ResourceTree presents an abstract representation of a Resource which is
 * ordered in a tree like structure (for example a File System). This is a Subclassed
 * Swing bean which combines the abstract VNode interface and the feature rich
 * GUI component JTree (=View)
 * 
 * It handles both the GUI component (JTree) as well as the underlaying
 * resource. All the specified VNode/VRL stuff is done in ResourceTreeNode. 
 * 
 * @see javax.swing.JTree,nl.uva.vlet.vrs.VNode,nl.uva.vlet.ResourceHandler
 * 
 * @author P.T. de Boer
 */

public class ResourceTree extends JTree implements VContainer
{
    /** Instance counter for statistics and debuggering */
    private static long resourceTreeCounter = 0;

    private static final long serialVersionUID = 200173418039849226L;
    
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(ResourceTree.class);
        // logger.setLevelToDebug(); 
    }
    // ================
    // === Instance ===
    // ================
    
    /** this instance's id */
    private long id = resourceTreeCounter++;

    private MasterBrowser masterBrowser;

    /** Always keep root node visible */

    private boolean keepRootVisible = true;

    private ResourceTreeListener resourceTreeListener;

	private DragSource dragSource;

	private VDragGestureListener dgListener;

	private AutoScoller autoScroller;

	private int mouseOverRow;

	//private ResourceTreeNode focusNode;
  
	private ResourceTreeDataProducer treeDataProducer;

    /**
     * Set the new root, but expand the childs until the pathNode is found. 
     * This method is used when creating a new Resource Tree with 
     * a rootNode which must show the subtree until pathNode 
     */

    public void setRootNode(ProxyNode rootNode, VRL viewedLocation)
            throws VlException
    {
        if (rootNode != null)
            // first set new root: 
            setRootNode(rootNode);

        // expand childs until the pathNode is found 
        asyncFindAndSelectPath(viewedLocation);
    }
    
    
    /**
     * 
     * Asynchronous method to expand the current tree until
     * the pathNode is found. Currently used to recreate a 
     * (sub)tree when opening a node in a new Window. 
     */
    private void asyncFindAndSelectPath(final VRL viewVrl)
    {
        logger.debugPrintf("asyncFindAndSelectPath:%s\n",viewVrl); 

        if (viewVrl==null)
        {
            return; // unselect current ? 
            
        }
        ResourceTreeNode current = this.getSelectedNode();
 
        if (current != null)
        {
            VRL currentLoc = current.getVRL();
         
            //logger.debugPrint("currentLoc=" + currentLoc);
            //debug("find pathLoc=" + pathLoc);

            // Already viewed specified location 
            if (currentLoc.compareTo(viewVrl) == 0)
            {
                return;
            }
        }

        final ResourceTreeNode rootNode = getRootNode();
        final ResourceTree tree = this;

        // first wait until the rootnode is populated (in the case of a new tree) 
        ActionTask task = new ActionTask(this.masterBrowser,
                "asyncFindAndSelectPath:"+viewVrl)
        {

            boolean dontstop = true;

            @Override
            public void doTask()
            {
                // init: root might not be populated yet:
                rootNode.waitForPopulated();

                Vector<VRL> parentLocs = new Vector<VRL>();

                VRL loc = viewVrl;
                VRL prevLoc = null;

                // add node itself to end of path
                parentLocs.insertElementAt(loc, 0);

                ResourceTreeNode lastNode = null;

                // browse up to root path (and avoid deadloc) 
                while ((loc.isRootPath() == false)
                        && (loc.compareTo(prevLoc) != 0))
                {
                    prevLoc = loc;
                    loc = loc.getParent(); // insert parentLoc  
                    parentLocs.insertElementAt(loc, 0);

                    // stop the search if the parent node is found in the current tree: 
                    if (findNodeWithLocation(loc, true) != null)
                        break;
                }

                // now loop from top to down and expand the path   
                for (VRL pLoc : parentLocs)
                {
                    logger.debugPrintf("Auto Expand path:%s\n",pLoc); 
                    // check if parent node is visible: 

                    ResourceTreeNode node = tree.findNodeWithLocation(pLoc,
                            true);

                    if (dontstop == false)
                        return;

                    // set and expand: 

                    if (node != null)
                    {
                        if (node.isPopulated() == false)
                            asyncPopulate(node);

                        // wait until it is populated: 
                        node.waitForPopulated();

                        //tree.setSelection(node,false); 

                        logger.debugPrintf("findPath:setSelection to:%s\n",node);
                        lastNode = node; // keep last update node 
                    }
                    else
                    {
                        logger.debugPrintf("findPath:couldn't find:%s\n",pLoc);
                    }

                }

                if (lastNode != null)
                {
                    final ResourceTreeNode finalNode = lastNode;

                    finalNode.waitForPopulated();

                    // after last populate: set selection 
                    Runnable runT = new Runnable()
                    {
                        public void run()
                        {

                            tree.updateSelection(finalNode, true);
                        }
                    };

                    SwingUtilities.invokeLater(runT);

                }

            }

            public void stopTask()
            {
                dontstop = false;
            }
        };

        task.startTask();

    }

    /** Set New Root Resource */

    public void setRootNode(ProxyNode pnode) throws VlException
    {
        logger.debugPrintf(">>> New Root Node:%s\n",pnode); 
    	
        if (pnode == null)
            return;
        
        // resolve links ! 

        if (pnode.isResourceLink())
        {
        	pnode = pnode.getTargetPNode();
        }
        
        this.treeDataProducer.updateRootnode(pnode); 
    }
        
    protected void setTreeDataProducer(
			ResourceTreeDataProducer producer)
	{
    	this.treeDataProducer=producer;
	}
    
    protected ResourceTreeDataProducer getTreeDataProducer()
	{
    	return treeDataProducer;
	}

	/**
     * Keep jigloo happy: define void contructor with PUBLIC Modifier so
     * that Jigloo is able to create a dummy one !
     * @param controller 
     */
    public ResourceTree()
    {
        super((TreeModel) null);// Create the JTree itself
        init(null);
    }

    public ResourceTree(MasterBrowser controller)
    {
        super((TreeModel) null);// Create the JTree itself
        this.masterBrowser = controller;
        init(controller);
    }
    /** Set default settings */
    public void init(MasterBrowser controller)
    {
    	//
    	// Set ResourceTree properties 
    	//

        // Use horizontal and vertical lines
        putClientProperty("JTree.lineStyle", "Angled");
        // nr of clicks to c
        this.setToggleClickCount(3);
        
        // some settings: 
        this.setScrollsOnExpand(true);
        this.setExpandsSelectedPaths(true);
        // Keep generating scroll events even when draggin out of window ! 
        this.setAutoscrolls(true); 
        this.setExpandsSelectedPaths(true);
        this.setRootVisible(this.keepRootVisible); 
        
        TreeSelectionModel selectionModel=new DefaultTreeSelectionModel(); 
        
        this.setSelectionModel(selectionModel); 
        // DND
        initDND();
        
        // empty model !
        ResourceTreeModel treeModel=new ResourceTreeModel(controller.getViewModel(),null,true);
        this.setModel(treeModel);
        
        ResourceTreeDataProducer dataProducer=new ResourceTreeDataProducer(this,ProxyNode.getProxyNodeFactory(),treeModel);
        setTreeDataProducer(dataProducer);
        
        ResourceTreeCellRenderer renderer = new ResourceTreeCellRenderer();
        setCellRenderer(renderer);

        //
        // LAST: initialize listeners: 
        // 
        resourceTreeListener = new ResourceTreeListener(controller,this);
        
        // Listen for Tree Selection Events
        addTreeExpansionListener(new TreeExpansionHandler());


        this.addFocusListener(this.resourceTreeListener);
        this.addMouseListener(resourceTreeListener);
        this.addMouseMotionListener(resourceTreeListener);
        this.setFocusable(true); 

        //this.setBorder(new BevelBorder(BevelBorder.RAISED));
    }
    
    private void initDND()
    {

        //  DropTarget AND Tranferhandler need to be set ! 
        this.setDropTarget(new ResourceTreeDropTarget(this)); 
        
        this.setTransferHandler(VTransferHandler.getDefault());
        
        this.dragSource = DragSource.getDefaultDragSource();
        this.dgListener = new VDragGestureListener();
        //this.dsListener = MyDragSourceListener.getDefault(); 
        // component, action, listener
        this.dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, this.dgListener );
    }
    
    /**
     * @return Returns the rootVNode. Never returns null.
     */
    public ViewNode getRootViewItem()
    {
    	return getModel().getRootViewItem(); 
    }

    public ResourceTreeModel getModel()
    {
    	return (ResourceTreeModel)super.getModel(); 
    }
    
    // return single path element of node pointer by complete TreePath path 
    public String getPathName(TreePath path)
    {
        Object o = path.getLastPathComponent();

        if (o instanceof ProxyNode)
        {
            // return ((VNode) o).getLocation();
            return ((ProxyNode) o).getName();
        }
        return null;
    }

    public ResourceTreeNode getNode(TreePath path)
    {
        Object o = path.getLastPathComponent();
        
        if (o instanceof ResourceTreeNode)
        {
            return (ResourceTreeNode) o;
        }
        else
        {
            logger.debugPrintf("NULL node or node is not a ResourceTreeNode for TreePath:%s\n",path); 
        }
        
        return null;
    }

    /**
     * Inner class that handles Tree Expansion Events
     */
    protected class TreeExpansionHandler implements TreeExpansionListener
    {

        public void treeExpanded(TreeExpansionEvent evt)
        {
            TreePath path = evt.getPath();// The expanded path JTree tree =
            JTree tree = (JTree) evt.getSource();// The tree

            // Get the last component of the path and
            // arrange to have it fully populated.
            ResourceTreeNode node = (ResourceTreeNode) path
                    .getLastPathComponent();

            if (node.isPopulated() == false)
                asyncPopulate(node);
            else
                notifySizeChange(); // resync is enough
        }
        
        public void treeCollapsed(TreeExpansionEvent evt)
        {
            // Nothing to do
            notifySizeChange();
        }
    }

    /**
     * Explicitly update size. 
     * Somehow the parent container doens't see the size changes. PreferredSize
     * is used by the ViewPort to determine the ViewPort size, but the JTRee
     * only updates maximumsize.
     * 
     */
    protected void notifySizeChange()
    {
        setPreferredSize(getMaximumSize());
    }
    
    public void asyncPopulate(ResourceTreeNode node)
    {
        logger.debugPrintf("asyncPopulate:%s\n",node);  

        // claim();
        if (node.isPopulated() == false)
        {
            // remove dummy/invalid children
        	node.removeAllChildren();
        }
       
        final VRL vrl =node.getVRL(); 
       
        if (vrl == null)
        {
            logger.warnPrintf("*** Ooopsy: ResourceTreeNode has no VRL!:%s\n",node);
            return; 
            // release();
        }
         
        final MasterBrowser bc=getMasterBrowser();
        
        if (bc instanceof BrowserController)
        {
        	if (((BrowserController)bc).interactiveCheckAuthenticationFor(vrl)==false)
        		return;
        }
        
         // update in background using Data Producer ! 
        
        treeDataProducer.bgGetChildsFor(node.getVRL()); 
    
    }
    
       /**
     * @return
     */
    public ResourceTreeNode getRootNode()
    {
        if (getModel() == null)
            return null;

        return (ResourceTreeNode) getModel().getRoot();
    }

    /**
     * Set selection of ResourceTee, if node NOT found, update rootNode if resourceTree
     * @param node
     * @param newroot    ; set this node as root is not found in tree
     * @throws VlException 
     * @throws VlException 
     */
    public void updateSelection(ProxyNode pnode, boolean newRoot)
            throws VlException
    {
        logger.debugPrintf("UpdateSelection (I) (newRoot=%s):%s\n",newRoot,pnode);  
        
        ResourceTreeNode currentSel = this.getSelectedNode(); 
        
        // filter out selection updates on current node; 
        if (currentSel!=null && currentSel.getVRL().equals(pnode.getVRL()))
        {
            return; 
        }
        
        // tricky, ProxyNode has to be found in current resource tree 
        // first without sime links to speed up the search: 
        List<ResourceTreeNode> nodes = this.findNodesWithLocation(pnode.getVRL(), false);

        if ((nodes == null) || (nodes.size()<=0)) 
            nodes = findNodesWithLocation(pnode.getVRL(), true); // check symlinks as well 

        // if node is NOT found in current tree, completely update resourcetree
        
        if ((nodes == null) || (nodes.size()<=0))
            return;
        
        // get first occurance: 
        ResourceTreeNode node = nodes.get(0); 
        
        if ((node == null) && (newRoot == true) && (keepRootVisible == false))
        {
            try
            {
                this.setRootNode(pnode);
            }
            catch (VlException e)
            {
                Global.errorPrintStacktrace(e); 
            }
        }

        updateSelection(node, true);
    }
    

    /**
     * @deprecated multiple nodes can exists with the same VRL, so use findNodes instead 
     */
    public ResourceTreeNode findNodeWithLocation(VRL loc,
            boolean checkLinkTargets)
    {
        // ResourceTreeNode node = _findNodeWithLocation(level,loc,checkLinkTargets);v
        
    	List<ResourceTreeNode> nodes = findNodesWithLocation(loc,true); 
    	
    	for (ResourceTreeNode node:nodes)
    	{
    	    logger.debugPrintf(" - FOUND :"+node); 
    	}
    	
    	if (nodes.size()>0)
    	    return nodes.get(0);
    	    
    	return null; 
    }
    
    public List<ResourceTreeNode> findNodesWithLocation(VRL loc,
            boolean checkLinkTargets)
    {
        return this.getModel().findNodes(loc,true); 
    }
    
//    private ResourceTreeNode _findNodeWithLocationOld(int level,VRL loc,boolean checkLinkTargets)
//    {
//        debug(">> find:"+loc);  
//        
//     	ResourceTreeNode selected = this.getSelectedNode();
//
//    	if (selected != null)
//    	{
//    	    if (compareLocations(selected,loc,checkLinkTargets)) 
//    	    {
//    	        debug(" - found (I):"+selected); 
//    			return selected;
//    	    }
//    	}
//        
//
//        ResourceTreeNode current = this.getRootNode();// start with root 
//        ResourceTreeNode node = null;
//        int childindex = 0;
//
//        // parent heap for depth-first searching the tree
//        Vector<ResourceTreeNode> heap = new Vector<ResourceTreeNode>();
//        int depth = 0;
//               
//        while (current != null)
//        {
//            
//            if (compareLocations(current,loc,checkLinkTargets)) 
//    		{
//                debug(" - found (II):"+current); 
//    		    node = current; // FOUND !
//                current = null;
//                break;
//            }
//
//            // go into subtree  
//            if (current.isLeaf() == false)
//            {
//                // keep parent, go into subtree 
//                if (depth >= heap.size())
//                {
//
//                    heap.add(current); // increment depth level
//                    depth = heap.size();
//                }
//                else
//                {
//                    heap.set(depth++, current);
//                }
//
//                current = (ResourceTreeNode) current.getFirstChild();
//            }
//            else
//            {
//                // next sibling, or go up, and keep going up if node has no next sibling 
//                //              fetch next sibling
//                current = (ResourceTreeNode) current.getNextSibling();
//
//                while ((current == null) && (depth > 0))
//                {
//                    // fetch parent sibling (uncle?)
//                    current = (ResourceTreeNode) heap.elementAt(--depth)
//                            .getNextSibling();
//                }
//
//            }
//
//        }
//        
//        if (node==null)
//            debug("NOT found:"+loc); 
//        //System.err.println("find Node with location returning:"+node); 
//
//        return node;
//    }

   

    public void updateSelection(ResourceTreeNode node, boolean expand)
    {
        logger.debugPrintf("Update Selection:%s\n",node);
        
        // NiNo
        if (node == null)
            return;

        TreeNode[] treeNodes = node.getPath();
        TreePath treePath = new TreePath(treeNodes);
        this.setSelectionPath(treePath);
        
        if (node.isPopulated())
        {
            asyncPopulate(node);
        }

        this.setExpandsSelectedPaths(true);

        if (expand)
            expandPath(treePath); // expand
        else
            makeVisible(treePath); // show path

        this.setSelectionPath(treePath);
        // scroll to node: 
        scrollRowToVisible(getRowForPath(treePath));

    }

    public ResourceTreeNode getSelectedNode()
    {
        TreePath path = getSelectionPath();

        if (path == null)
            return null;

        return (ResourceTreeNode) path.getLastPathComponent();
    }

    public MasterBrowser getMasterBrowser()
    {
        return this.masterBrowser;
    }

    public void dispose()
    {
    	if (this.treeDataProducer!=null)
    	{
    		this.treeDataProducer.dispose();
    		this.treeDataProducer=null; 
    	}
    	
    }

    /** Used for Mouse Events : */
    public ResourceTreeNode getNodeUnderPoint(Point p)
    { 
    	if (p==null)
    		return null;
    	
        int clickRow = getRowForLocation(p.x, p.y);

        TreePath path = getPathForLocation(p.x, p.y);

        if ((path == null) || (clickRow < 0))
        {
            // no node under mouse click 
            return null;
        }

        return getNode(path);
    }
    
    // ========================================================
    // VContainer Interface
    // ========================================================
    
	public ResourceRef[] getSelection()
	{
		TreePath paths[] = getSelectionPaths();
		if (paths==null) 
			return null; // no selection 
		ResourceRef vrls[]=new ResourceRef[paths.length];
		
		int index=0; 
		for (TreePath path:paths)
		{
			ResourceTreeNode node = this.getNode(path);
			String type=node.getResourceType(); 
			
			if (node!=null)
				vrls[index++]=node.getResourceRef(); 
		}
		
		return vrls; 
	}
	
	public VRL getVRL()
	{
		// nullpointer; 
		if (getRootNode()==null)
			return null; 
		
		return this.getRootViewItem().getVRL(); 
	}

	public ResourceRef getResourceRef()
	{
		return this.getRootViewItem().getResourceRef(); 
	}

	public VComponent getFocusComponent()
	{
		return this.getSelectedNode(); 
	}
	
	/** Resource Tree itself is not embedded in a VContainer itself */ 
	public VContainer getVContainer()
	{
		return null;
	}

	public String getResourceType()
	{
		return this.getRootViewItem().getResourceType(); 
	}
	
	class AutoScoller implements ActionListener
	{
		ResourceTree tree; 
		Point speed=new Point(0,0); 
		private Timer timer=null;  
		int delay=20; // 50 update/s ! 
		
		public AutoScoller(ResourceTree tree, Point p)
		{
			this.tree=tree;
			setTargetPoint(p); 
		}
		
		public void setTargetPoint(Point p)
		{
			int dx=16;
			int dy=24;
			
			Rectangle outer=tree.getVisibleRect(); 
			
			if (p.y<(outer.y+dy))
				speed.y=p.y-(outer.y+dy);
			else if (p.y>(outer.y+outer.height-dy))
				speed.y=p.y-(outer.y+outer.height-dy);
			else
				speed.y=0;
			
			if (p.x<(outer.x+dx))
				speed.x=p.x-(outer.x+dx);
			else if (p.x>(outer.x+outer.width-dx))
				speed.x=p.x-(outer.x+outer.width-dx);
			else
				speed.x=0;
		}
		
		public void start()
		{
			if (timer==null)
			{
				timer=new Timer(delay,this);
				timer.start();
			}
		}
		
		public void stop()
		{
			if (timer!=null)
			{
				timer.stop();
				timer=null; 
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			 
			Rectangle rect = tree.getVisibleRect(); 
			
			if ((speed.x!=0) || (speed.y!=0))
			{
				rect.x+=speed.x; 
				rect.y+=speed.y; 
				tree.scrollRectToVisible(rect);
			}
			else
			{
				stop();
			}
		}
	}
	
	public void scrollTo(Point p)
	{
		startAutoScroller(p); 
	}

	
	private void startAutoScroller(Point p)
	{
		if (autoScroller==null)
		{
			autoScroller=new AutoScoller(this,p);
		}
		
		autoScroller.setTargetPoint(p);
		autoScroller.start();
	}

//	/** Update focus node and request redraw */
//	public void setFocusNode(ResourceTreeNode node)
//	{
//		ResourceTreeNode prevNode = this.focusNode; 
//		this.focusNode=node; 	 
//	
//		// redraw: 
//    	if (node!=null) 
//    		repaintNode(node);
//    	
//    	if (prevNode!=null) 
//    		repaintNode(prevNode); 
//	}
	
	public void repaintNode(ResourceTreeNode node)
	{
		if (node==null)
			return; 

		 getResourceTreeModel().nodeChanged(node);
	}
	
	public DefaultTreeModel getResourceTreeModel()
	{
		return (ResourceTreeModel)this.getModel(); 
	}

//	public ResourceTreeNode getFocusNode()
//	{
//		return focusNode; 
//	}

	public void stopAutoScroller()
	{
		if (this.autoScroller!=null)
			this.autoScroller.stop(); 
	}
 
	public String toString()
	{
		return "{ResourceTree:"+getVRL()+"}";
	}

	public VComponent getVComponent(ResourceRef ref)
	{
		return findNodeWithLocation(ref.getVRL(),false); 
	}
	
	// Used for Mouse Over Effects 
	public void setMouseOverPoint(Point p)
	{
	    int newRow=this.mouseOverRow; 
	    
	    if (p==null)
	    {
	        newRow=-1;
	    }
	    else
	    {
   	 	     newRow = getClosestRowForLocation((int)(p.getX()),(int)(p.getY()));
	    }
	    
	    if(newRow != this.mouseOverRow)
	    {
	        this.mouseOverRow=newRow; 
            repaint();
        }
	}
	
	public int getMouseOverRow()
	{
		return mouseOverRow;
	}

    @Override
    public void selectAll(boolean selectValue)
    {
        
        
    }
	

}
