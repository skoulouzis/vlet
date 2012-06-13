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
 * $Id: IconsPanel.java,v 1.15 2011-06-10 10:29:49 ptdeboer Exp $  
 * $Date: 2011-06-10 10:29:49 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.util.Collection;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VRLList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.actions.KeyMappings;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.proxynode.ViewNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.vbrowser.BrowserJPanel;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewFilter;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;


/** 
 * Icons Panel 
 * 
 * Simple icons JPanel. 
 * 
 * @author Piter T. de Boer 
 * 
 */

public class IconsPanel extends BrowserJPanel implements ProxyResourceEventListener,
        VContainer
{
	private static final long serialVersionUID = 5989073867988758790L;
	
    private Dimension targetSize = null;

    /** Master Browser Controller */
 
    MasterBrowser masterBrowser = null;

    LabelIconListener buttonIconListener;

    ProxyNode rootNode=null;
    
    /** Hashtable for the proxyNodes */ 
    //private Hashtable<String,ProxyNode> proxyNodes = new Hashtable<String,ProxyNode>();
     
    DropTarget dropTarget=null;

	private IconsPanelListener iconsPanelListener;

	NodeDropTarget defaultDropTarget=new NodeDropTarget(this);

	/** current component which has focus */ 
    private VComponent focusComponent;

	IconsDragGestureListener dragListener;

	Vector<LabelIcon> labelIcons=new Vector<LabelIcon>();
	
	private IconsLayoutManager iconsLayoutManager;



    private LabelIcon lastSelected;
  
    // === 
    // Object Contructor 
    // ===

    /**
     * To Please Jigloo, a public default constructor must exist ! 
     * Note that when using this constructor, the browsercontroller is NULL ! 
     */

    public IconsPanel()
    {
        super(); 
        initGui();
    }

    public void initGui()
    {
    	// default icons layout manager: 48 size 
        
        this.iconsLayoutManager=new IconsLayoutManager(IconViewType.ICONS48); 
        this.setLayout(iconsLayoutManager); 
        
        setName("IconsPanel"); 
        
        this.setBackground(UIGlobal.getGuiSettings().getDefaultPanelBGColor()); 
        this.setForeground(UIGlobal.getGuiSettings().getDefaultPanelFGColor());
        
        // Add Copy/Paste Menu shortcuts to this component:
        KeyMappings.addCopyPasteKeymappings(this); 
        KeyMappings.addSelectionKeyMappings(this); 
        
        // Important: 
        // IconsPanel is dragsource ffor ALL icons it contains 
        // this to support multiple selections!
        
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragListener = new IconsDragGestureListener(this);
        //this.dsListener = MyDragSourceListener.getDefault(); 
        // component, action, listener
        dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, dragListener );
        
    }

    public IconsPanel(MasterBrowser bc)
    {
        super();
        initGui();
        
        this.masterBrowser = bc;
        //this.dropTargetListener=new NodeDropTargetListener();
        this.iconsPanelListener=new IconsPanelListener(this); 
        this.addMouseListener(iconsPanelListener);
        this.addFocusListener(iconsPanelListener); 
        
        // CTRL-mousewheel=zoom in/out ? 
        //this.addMouseWheelListener(mouseListener);
        
        // IconPanel canvas can receive contents !
        this.setTransferHandler(VTransferHandler.getDefault());

        ProxyVRSClient.getInstance().addResourceEventListener(this);
        // one buttonIconListener for all Icons
        this.buttonIconListener = new LabelIconListener(this,bc);
        this.setFocusable(true); 
        this.setDropTarget(defaultDropTarget);
    }

    /** Set ViewType does NOT realign icons */ 
    public void setViewType(IconViewType type)
    {
    	debugPrintf("setViewType:%s\n",type);
    	this.iconsLayoutManager.setIconViewType(type);
    }
    
    public void setRootNode(ProxyNode pnode) throws VlException
    {
        debugPrintf("setRootNode():%s\n",pnode); 
    	
        this.rootNode = pnode;
        
        // clear;
        clearContents(); 

        if (pnode == null)
        {
            // this.setPreferredSize(new Dimension(0, 0));
            return;
        }
        
        this.setToolTipText(pnode.getVRL().toString());

        if (pnode.isComposite() == false)
        {
            // this.setPreferredSize(new Dimension(0, 0));
            return;
        }

        // perform asynchronous update 

       asyncUpdateContents(pnode);

        // jTarget.setSize(600,100); // default size ?
    }

    /** 
     * Set Childs is the actual updater, populateWith just requests
     * for a SET_CHILDS event.
     * Note that setChilds should be executed during the main
     * GUI event thread!
     *  
     * (which it will when receiving the Notify Event!) 
     * 
     * @param locations
     */
    protected void setChilds(VRL childLocs[])
    {
    	debugPrintf("setChilds(VRL[])\n"); 
    	
    	Vector<VRL>locs=new Vector<VRL>();
    	
    	for (VRL vrl:childLocs)
    		locs.add(vrl); 
    	
    	asyncSetChilds(locs);  
    }
 
    /** 
     * Recreates the ButtonIcons from locations vector.  
     * After this method buttonIconsList is in sync with locations vector.  
     * 
     */
    
    protected void asyncUpdateContents(final ProxyNode parent)
    {
    	debugPrintf("asyncUpdateContents():%s\n",parent); 
    	
    	this.rootNode=parent;
    	
    	final ViewFilter filter=getViewModel().getViewFilter(); 
    	final IconsPanel iconsPanel=this; 
        final MasterBrowser finalMaster=this.masterBrowser; 
        final int iconSize=this.getIconViewType().iconSize; 
        
    	ActionTask getTask=new ActionTask(this.masterBrowser,"Get Childs nodes")
    	{
    		boolean mustStop=false; 
    		
    		public void doTask() 
    		{ 
    			
				try
				{
					final ProxyNode childs[] = parent.getChilds(filter);
					
	    			// update within GUI thread:
					if (mustStop==true)
						return;
					
					debugPrintf("***\n*** SwingInvoke Later: syncSetPChilds(ProxyNode[])\n***\n");
					ViewNode items[]=ViewNodeFactory.createFrom(null,childs,iconSize); 
					uiSetIconItems(items); 
				}
				catch (VlException e)
				{
					finalMaster.handle(e); 
					return; 
				} 
    		 
    		}

			@Override
			public void stopTask()
			{
				mustStop=true; 
			}
    	};
    	
    	getTask.startTask();
    	
    }
    

    public ViewModel getViewModel()
    {
        return this.masterBrowser.getViewModel();
    }

    /** 
     * Recreates the ButtonIcons from locations vector.  
     * After this method buttonIconsList is in sync with locations vector.  
     * 
     */
    
    protected void asyncSetChilds(final Vector<VRL> childsLocs)
    {  
    	debugPrintf("asyncSetChilds(VRL[]) START\n");
    	
    	clearContents(); 
    	asyncAddChilds(childsLocs);
    	
    	debugPrintf("asyncSetChilds(VRL[]) END\n");
    }
    
    protected void asyncAddChilds(final Collection<VRL> childLocs)
    {
        debugPrintf("asyncAddChilds(VRL[]) START\n");
    	
    	final ViewFilter filter=getViewModel().getViewFilter(); 
    	final IconsPanel iconsPanel=this; 
        final MasterBrowser finalMaster=this.masterBrowser;
        
        if (childLocs==null) 
        	return; 

        final Collection<VRL> newChilds=new Vector<VRL>();
        
        // filter out existing ! 
        
        for (VRL vrl:childLocs)
        	if (this.hasLocation(vrl)==false)
        		newChilds.add(vrl);
        
        final int iconSize=this.getIconViewType().iconSize; 
        
    	ActionTask getTask=new ActionTask(this.masterBrowser,"Get Childs nodes")
    	{
    		boolean mustStop=false; 
    		
    		public void doTask() 
    		{ 
    			final Vector<ProxyNode> childs=new Vector<ProxyNode>();
    			
    			for (VRL vrl:newChilds)
    			{
    				try
    				{
    					ProxyNode pnode=ProxyNode.getProxyNodeFactory().openLocation(vrl,false);
    					childs.add(pnode); 
    				}
    				catch (Exception e)
    				{
    					finalMaster.handle(e); 
    				}
    				
				} // for
    			
    			final ViewNode items[] = ViewNodeFactory.createFrom(childs,iconSize); 
    			uiAddIconItems(items); 
    			
    		}

			@Override
			public void stopTask()
			{
				mustStop=true; 
			}
    	};
    	
    	getTask.startTask();
    	
    	debugPrintf("asyncAddChilds(VRL[]) END (backgrounded)\n");
    }
    
    /** Run synchronized with Gui thread */ 
    protected synchronized void syncSetPChilds(ViewNode[] items)
    {
        uiSetIconItems(items); 
    }

    /** Run synchronized with Gui thread */ 
    protected synchronized void uiSetIconItems(final ViewNode[] items)
	{
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable updateChilds=new Runnable() {
                public void run() {
                    uiSetIconItems(items); 
                }
            };
        // update during gui thread (should use list model!) 
           UIGlobal.swingInvokeLater(updateChilds);
           return;
        }
        
        //items==null => clear only; 
    	clearContents();
    	if (items!=null)
    	    uiAddIconItems(items);
	}
    
    /** Run synchronized with Gui thread */
    protected synchronized void uiAddIconItems(final ViewNode[] items) 
    {
        if (UIGlobal.isGuiThread()==false)
        {
            debugPrintf("***\n*** SwingInvoke Later (II): syncSetPChilds(ProxyNode[])\n");
        
            Runnable updateChilds=new Runnable() {
                public void run() {
                    uiAddIconItems(items); 
                }
            };
        // update during gui thread (should use list model!) 
           UIGlobal.swingInvokeLater(updateChilds);
           return;
        }
        
    	
    	if (items==null)
    	{
    		return;
    	}
    	
        // iterate over 'locations' 
        for (ViewNode pnode:items) // Enumeration<String> keys = proxyNodes.keys();keys.hasMoreElements();) // keys.nextElement()) // int i = 0; i < numNodes; i++)
        {
            debugPrintf("+++ Adding new child:%s\n",pnode); 
            LabelIcon bicon=null;
            
            // not in cache, refresh:
            if (pnode==null)
            {
                // add/set Child should have checked this :
                Global.infoPrintf(this,"*** Warning: child node not in cache, fetching childs of:%s\n",rootNode);
            }
            else
            {
            	// extra filter: 
            	if (this.hasLocation(pnode.getVRL())==false) 
            	{
            		bicon= new LabelIcon(this.masterBrowser,this,
            				pnode,UIGlobal.getGuiSettings().default_max_iconlabel_width);
               
            		// DnD Note: The mouse listeners detect drag and export them 
            		bicon.setListener(this.buttonIconListener);
               
            		// keep LabelIcons in array 
            		addLabelIcon(bicon); 
            	}
            	
            }
        }
        
        // do layout NOW 
        this.doLayout(); 

        // defer validation +repaint; 
        this.revalidate();
        this.repaint(); 
        
		debugPrintf("syncAddPChilds() END: done\n");
    }

    
    private void addLabelIcon(LabelIcon bicon)
	{
		this.labelIcons.add(bicon); 
        this.add(bicon);
        this.repaint(); 
	}
    
    private void deleteLabelIcon(LabelIcon bicon)
	{
        bicon.setVisible(false);
		this.labelIcons.remove(bicon); 
        this.remove(bicon);
        this.repaint(); 
	}
    
    private void clearContents()
    {
    	this.labelIcons.clear(); 
        this.removeAll(); 
        this.repaint(); //request repaint: 
    }
    
	/** Does a getComponents() on JPanel but return LabelIcon only */ 
    public Vector<LabelIcon> getLabelIcons()
	{
    	return labelIcons;  
	}

	public Vector<VRL> getChildLocs()
    {
    	Vector<VRL> locs=new Vector<VRL>();
    	
    	Vector<LabelIcon> iconItems = getLabelIcons();
    
    	synchronized(iconItems)
    	{
    		for (LabelIcon icon:iconItems)
    		{
    			locs.add(icon.getVRL());
    		}
    	}
    	
    	return locs; 
    }
   
    
    private void debugPrintf(String format,Object ... args)
    {
        Global.debugPrintf(this,format,args); 
    }
    
    public void notifyProxyEvent(ResourceEvent e)
    {
        debugPrintf("ResourceEvent:%s\n",e);

        if (this.rootNode == null)
            return; // not viewing anything at this moment. 

        boolean isForRoot=false; 
        boolean isForChild=false; 
        
        LabelIcon child=null;
        
        if ((e.getSource()!=null) && (e.getSource().compareTo(this.rootNode.getVRL()) == 0))
        {
            isForRoot=true;
            debugPrintf("is for root:%s\n",e); 
        }
        else
        {
        	child = this.getLabelIcon(e.getSource()); 
        	if (child != null)
        	{
        		isForChild=true;
        		debugPrintf("is for child:%s\n",e);
        	}
        }
        
        if ((isForRoot==false) && (isForChild==false)) 
        {
            debugPrintf("Not for root or child:%s",e);
            return; // not for me or child
        }
        
        try
        {
            switch (e.getType())
            {
                case REFRESH:
                	// reread root node and update 
                    if (isForRoot)
                        refresh(); 
                    else
                    	refreshChild(e.getSource()); 
                    
                    break; 
                case SET_ATTRIBUTES:
                {
                	// check support attributes: Icon & Name: 
                	updateAttributes(e.getSource(),e.getAttributes());
                	break;
                }
                case DELETE:
                    if (isForRoot)
                        setRootNode(null); // CLEAR
                    if (isForChild)
                        deleteLabelIcon(child);
                    break;
                case CHILDS_DELETED:
                    if (isForRoot)
                       for (VRL loc : e.getChilds())
                          deleteLabelIcon(loc);
                    break;
                case CHILDS_ADDED:
                    if (isForRoot)
                       addChilds(new VRLList(e.getChilds()));
                    break;
                case RENAME:
                    if (isForRoot)
                    {
                        this.rootNode=ProxyNode.getProxyNodeFactory().getFromCache(e.getNewVRL()); 
                        // special case current viewed node is renamed ! 
                        refresh();
                    }
                    else if (isForChild)
                    {
                        refreshLabelIcon(e.getSource(), e.getNewVRL());
                    }
                    break;
                case SET_CHILDS:
                {
                    if (isForRoot)
                       setChilds(e.getChilds());
                }
                default:
                    break;
            }
        }
        catch (VlException e1)
        {
           handle(e1); 
        }
    }


	
	/** Refresh icon+label. (Only ICON and NAME attribute are refreshed) */ 
    
    private void refreshChild(VRL loc)
	{
    	// refresh name & icon 
    	 refreshLabelIcon(loc,loc); 
	}

	private void handle(VlException e1) 
    {
    	this.masterBrowser.handle(e1); 
	}

	private void addChilds(Collection<VRL> childs)
    {
        this.asyncAddChilds(childs); 
    } 

    public boolean hasLocation(VRL vrl)
    {
    	return (this.getLabelIconIndex(vrl)>=0); 
    }
    
    /** Returns index number in locations Vector */ 
    public int getLabelIconIndex(VRL vrl)
    {
        for (int i=0;i<labelIcons.size();i++)
        {
            if (labelIcons.elementAt(i).hasLocation(vrl))
                return i;
        }
            
        return -1;     
    }
    
    

    public void refresh() throws VlException
    {
        // just repopulate 
        this.setRootNode(rootNode);
    }

    /** fetches ButtonIcon from buttonIcon list */ 
    public LabelIcon getLabelIcon(VRL loc)
    {
    	if ((loc==null) || (labelIcons==null) || (labelIcons.size()<=0))
    		return null; 
    		
    	for (LabelIcon licon:this.labelIcons) 
    	{
    		if (licon.getVRL().equals(loc)) 
    			return licon; 
    	}
                
    	return null; 
    }

    public void deleteLabelIcon(VRL nodeLocation)
    {
        LabelIcon bicon = getLabelIcon(nodeLocation);
        
        if (bicon == null)
        {
            debugPrintf("Node not found:%s\n",nodeLocation);
        }
        else
        {
            this.deleteLabelIcon(bicon);
        }
    }

    private void refreshLabelIcon(VRL oldLoc, VRL newLoc)
    {
        // update label of ButtonIcon: 
        LabelIcon bicon = getLabelIcon(oldLoc);

        if (bicon == null)
        {
            debugPrintf("Node not found:%s\n",newLoc);
            return;
        }
        
        bgRefreshLabelIcon(bicon,oldLoc,newLoc);
    }
    
    private void bgRefreshLabelIcon(final LabelIcon bicon,final VRL oldLoc, final VRL newLoc)
    {
        if (UIGlobal.isGuiThread()==true)
        {
            new ActionTask(this.getMasterBrowser(),"Refresh location:"+oldLoc) {
                public void doTask() {
                    bgRefreshLabelIcon(bicon,oldLoc,newLoc); 
                }
                public void stopTask() {} 
            }.startTask(); 
            return; 
        }
        
        ProxyNode newNode=null;
        
        // update bicons list:
        try
        {
            newNode = ProxyNode.getProxyNodeFactory().openLocation(newLoc,false);
        }
        catch (VlException e)
        {
           handle(e);
           return;
        } 
        
        if (newNode==null) 
        {
            // todo caching
        	Global.errorPrintf(this,"Warning, new node NOT found:%s\n",newLoc);
        	return; 
        }
                
        // just update the node
        bicon.setIconItem(ViewNodeFactory.createViewNode(newNode)); 
         
        // do layout again:
       // this.revalidate();
     }

    private void updateAttributes(VRL loc,VAttribute[] attrs)
    {
        if ((attrs==null) || (loc==null))
            return; 
        
        LabelIcon bicon = getLabelIcon(loc); 
        
        if (bicon==null)
            return; // asynchronized update: icon removed.
            
        for (VAttribute attr:attrs)
        {
            if (attr.hasName(VAttributeConstants.ATTR_ICONURL))
            {
                bicon.updateIconURL(attr.getStringValue()); 
            }
        }
        // else icon removed!
    }

    
    /** Help Garbage collector by nullifying object references */ 
    public void dispose()
    {
        this.masterBrowser=null;
        // Clean up proxy nodes ! only when ALL proxynodes are gone 
        // that class will be finalized ! 
        this.rootNode=null;
        this.clearContents();
        ProxyVRSClient.getInstance().removeResourceEventListener(this); 
    }


    public VRL getVRL()
    {
        if (this.rootNode == null)
            return null;

        return rootNode.getVRL();
    }
    
    public ResourceRef getResourceRef()
	{
		return this.rootNode.getResourceRef();    
	}

    public MasterBrowser getMasterBrowser()
    {
        return this.masterBrowser;
    }
    
    public void toggleSelection(VRL loc,boolean combine) 
    {
        // toggle: 
        LabelIcon bicon = this.getLabelIcon(loc);
        this.setSelected(loc,combine,bicon.isSelected()==false); 
    }
    
	public void setSelected(VRL vrl, boolean combine, boolean value)
	{
		 LabelIcon bicon = this.getLabelIcon(vrl);
		 
		 if (bicon==null)
		 {
		     Global.debugPrintf(this,"setSelected(): Couln't find icon for:%s\n",vrl); 
		     return; 
         }
		 
		 // Check potential first click for a SHIFT-RANGE Selection:
		 if (value==true)
		     this.lastSelected=bicon; 
		 
		 if (combine==true) 
		 {	
		     bicon.setSelected(value);
		 }
		 else
		 {
		     // single selection: 
		     selectAll(false); 
		     lastSelected=bicon;  
			 bicon.setSelected(value);
		 }
	}
    

    public void selectAll(boolean value)
    {
        // unselect others, select this one: 
        Vector<LabelIcon> iconItems = this.getLabelIcons(); 
        
        for (LabelIcon icon:iconItems)
            if (icon!=null)
                icon.setSelected(value); 

        if (value==false)
            lastSelected=null; 
    }
    
    
    public void selectRange(VRL vrl, boolean isFirst)
    {
        if ((isFirst) || (this.lastSelected==null)) 
        {
            this.setSelected(vrl,false,true);
            return; 
        }
        
        Vector<LabelIcon> iconItems = this.getLabelIcons(); 
        LabelIcon last = this.getLabelIcon(vrl);
        if (last==null)
        {
            Global.debugPrintf(this,"selectRange(): Couln't find 2d selection marker:%s\n",vrl); 
            return; 
        }
            
        boolean mark1=false;
        boolean mark2=false; 
        
        for (LabelIcon icon:iconItems)
        {
            boolean edge=false; 
            // check start and end mark. 
            // lastSelected could be at the end of the vector !
            if (icon.equals(this.lastSelected))
            {
                mark1=true; //passed mark 1
                edge=true;
            }
        
            if (icon.equals(last))
            {
                mark2=true;// passed mark 2
                edge=true;
            }
            
            // when between marks, update value: 
            boolean value=( edge
                            || (mark1==true) && (mark2==false)
                            || (mark1==false) && (mark2==true) ); 
            
            icon.setSelected(value); 
         
        }
    }
    
	public ResourceRef[] getSelection()
	{
		Vector<ResourceRef> vrls=new Vector<ResourceRef>(); 
		Vector<LabelIcon> iconItems = this.getLabelIcons(); 
		 
		for (LabelIcon icon:iconItems)
		{
			//System.err.println("isSelected:"+icon.isSelected()+" for:"+icon.getVRL()); 
			if ((icon!=null) && (icon.isSelected()))
				vrls.add(new ResourceRef(icon.getVRL(),icon.getResourceType(),icon.getMimeType()));
		}
		
		if (vrls.size()<=0)
			return null;
				
		ResourceRef[] _refs=new ResourceRef[vrls.size()];
		
		_refs=vrls.toArray(_refs); 
		return _refs;  
	}

	public VComponent getFocusComponent()
	{
		return null;
	}

	public boolean hasSelection()
	{
		 Vector<LabelIcon> iconItems = this.getLabelIcons(); 
		 
		for (LabelIcon icon:iconItems)
		{
			if (icon.isSelected())
				return true; // second selection -> multiple selections
		}
		
		return false; 
	}

	public VContainer getVContainer()
	{
		return null;  
	}

	public String getResourceType()
	{
		return this.rootNode.getType();  
	}

	public IconViewType getIconViewType()
	{
		return this.iconsLayoutManager.getIconViewType();
	}

	public String toString()
	{
		return "{IconsPanel:"+this.getVRL()+"}"; 
	}

	public VComponent getVComponent(ResourceRef ref)
	{
		return this.getLabelIcon(ref.getVRL()); 
	}


}
