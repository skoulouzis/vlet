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
 * $Id: ProxyNode.java,v 1.12 2011-06-10 10:17:58 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:58 $
 */ 
// source: 

package nl.uva.vlet.gui.proxynode;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.error.InitializationError;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;

import nl.uva.vlet.gui.proxymodel.IProxyNodeEventListener;
import nl.uva.vlet.gui.proxymodel.ProxyNodeEventNotifier;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrms.LogicalResourceNode;
//import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.ResourceEventListener;
import nl.uva.vlet.vrs.VDeletable;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRenamable;

/**
 * A ProxyNode is a the GUI side representation of a VNode or Resource Node.  
 * <p>
 * This class is a First attempt to create interface for ProxyNodes
 * to allow multiple 'factories' instead of the direct implementation 
 * which uses the VRS api.   
 * Also this class is intended as a stable ProxyNode interface.
 *  
 * Current Implementations are ProxyDirectNode and ProxyWSNode
 * 
 * @see ProxyNodeFactory 
 * 
 * @author P.T. de Boer 
 *
 */
public abstract class ProxyNode
{
	// === static fields ===
	private static long objectCounter = 0; // for debugging

	private static ProxyNodeFactory _proxyNodeFactory=null;  
    
    // =======================================================================
    // Class Methods 
    // =======================================================================
    
    
    public static void setProxyNodeFactory(ProxyNodeFactory factory)
    {
    	_proxyNodeFactory=factory;  
    }
    
    public static ProxyNodeFactory getProxyNodeFactory()
    {
    	if (_proxyNodeFactory==null)
    		throw new InitializationError("Initiliziation error. Proxy Node Factory not set. Use ProxyTNode.init() first !!!"); 
    	
    	return _proxyNodeFactory; 
    }
    
    public static void fireGlobalEvent(ResourceEvent event)
    {
        // use class notifier:
    	ProxyNodeEventNotifier.getDefault().fireEvent(event); 
    }
    
    /**
     * Add Proxy Event listener. 
     * Any listener who registers which this method is
     * assured to receive ResoureEvents DURING Swing's
     * main event thread so that GUI components
     * can be handled safely. 
     * 
     * @param listener
     */
    public static void addProxyEventListener(IProxyNodeEventListener listener)
    {
    	ProxyNodeEventNotifier.getDefault().addResourceEventListener(listener);
    	
    }
    
    public static void removeProxyEventListener(IProxyNodeEventListener listener)
    {
    	ProxyNodeEventNotifier.getDefault().removeResourceEventListener(listener); 
    }

	public static void disposeClass()
	{
		
	}
   
	public static ProxyNode getVirtualRoot() throws VlException
	{
		 return getProxyNodeFactory().openLocation(UIGlobal.getVRSContext().getVirtualRootLocation()); 
	}	

    public static ProxyNode[] toArray(List<ProxyNode> nodes)
    {
        ProxyNode arr[]=new ProxyNode[nodes.size()];
        arr=nodes.toArray(arr);
        return arr;
    }

	    
    // ========================================================================
    // === object fields === 
    // ========================================================================
    
    private long id=-1;
    
    /** Experimental ref count for smart pointers */ 
	private int refCount=0;
    
	// === constructors === 
	
    /**
     * Private Constructor, do not use this one, but use static createProxyTNode
     * to ensure cache consistency.
     * 
     * @see createProxyTNode
     */
    protected ProxyNode()
    {
        this.id = objectCounter++;
    }
    
	
	public synchronized void increaseReferenceCount()
	{
		this.refCount++;
		debugPrintf(">>> Increased ref count to:%d of %s\n",refCount,this);
	}
	

	public synchronized void decreaseReferenceCount()
	{
		this.refCount--;
		Global.debugPrintln(this,">>> Decreased ref count to:"+refCount+" of:"+this);

		if (this.refCount<0) 
			Global.errorPrintln(this,"*** Error: Negative reference count for:"+this); 
		
		if (this.refCount<=0)
		{
			// DISPOZE !!! (not yet)
			debugPrintf(">>> DISPOSE:%s\n <<<",this);
		} 
		
	}

	public long getID()
	{
		return this.id; 
	}
    
	public void fireEventTo(final ResourceEventListener receiver,
            final ResourceEvent event)
    {
		ProxyNodeEventNotifier.getDefault().fireEventToListener(receiver,event); 
    }
    
	
	public boolean equalsType(ProxyNode node)
    {
        if (node==null) 
            return false; 
        
        return StringUtil.equals(this.getType(),node.getType());
    }
	
	private void debugPrintf(String format,Object ... args)
	{
		Global.debugPrintf(this,format, args); 
	}

	/** Resource Reference. Combination of VRL+Type+(resolved)MimeType */ 
	public ResourceRef getResourceRef()
	{
		return new ResourceRef(getVRL(),getType(),getMimeType());  
	}

	// ========================================================================
	// === Proxy Model Event. Reuses ResourceEvent but will sent them 
	// === To ProxyModel Listeners ! 
	// ========================================================================
	

	/** Fire Proxy Model Event : Deleted ! */ 
	public void fireNodeDeleted(ProxyNode node)
	{
		// Create Delete event with this Node as parameter
		ResourceEvent event = ResourceEvent.createDeletedEvent(node.getVRL());
		fireGlobalEvent(event);
	}
    

	public void fireChildAdded(ProxyNode parent, ProxyNode child)
	{
		fireChildAdded(parent.getVRL(),child.getVRL());
	}
	
	public void fireChildAdded(VRL parent, VRL child)
	{
        ResourceEvent event = ResourceEvent.createChildAddedEvent(parent, child);
        fireGlobalEvent(event);
	}
	
	/** 
	 * Fire rename event. 
	 * 
	 * When a rename has occured the VRL might have changed so both VRLs 
	 * have to be supplied.   
	 * 
	 * @param oldLocation
	 * @param newLocation
	 */
	public void fireRenameEvent(VRL oldLocation,VRL newLocation,String name) 
	{
		fireGlobalEvent(ResourceEvent.createRenameEvent(oldLocation,newLocation,name));
	}
	
	public boolean isMyVLe()
	{
		VRL vrl=getVRL();
		
		if (StringUtil.compare(vrl.getScheme(),VRS.MYVLE_SCHEME)!=0)
			return false; 
		
		// check for empty or root path 
		
		if (StringUtil.isEmpty(vrl.getPath())) 
			return true;
		
		if (StringUtil.compare("/",vrl.getPath())==0) 
			return true; 
		
		return false; 
	}


	
	 /** Creat in this container a new LinkNode to the specified VRL */
	 public void createLinkTo(VRL target) throws VlException
	 {
		 // need info:
		 ProxyNode pnode=ProxyNode.getProxyNodeFactory().openLocation(target); 
		 createLinkTo(pnode); 
	 }
	 
	 public VAttribute[] getAttributes() throws VlException
	 {
		 return getAttributes(getAttributeNames());
	 }
	   
	 /** get Ordened Attribute set in the specified order */ 
	 public VAttributeSet getAttributeSet(String[] names) throws VlException
	 {
		return new VAttributeSet(getAttributes(names));
	 }

	 public VAttributeSet getAttributeSet() throws VlException
	 {
		return new VAttributeSet(getAttributes());
	 }
	 
	 /**  
	 * @deprecated NOT efficient way to get child node. */   
	 public ProxyNode getChild(String basename) throws VlException
	 {
	     ProxyNode childs[]=this.getChilds(null);
	     if (childs==null)
	         return null; 
	         
	     for (ProxyNode child:childs)
	     {
	         if (StringUtil.equals(child.getName(),basename))
	             return child;  
	     }
	     
	     return null;  
    }
	 
	public boolean isComposite()
	{
	    return isComposite(true);
	}
	
	
    /** 
     * If this node represents a Resource Link, return the mimetype
     * of the target resource. 
     */
    public String getTargetMimeType() throws VlException
    {
        VAttribute attr=this.getAttribute(VAttributeConstants.ATTR_TARGET_MIMETYPE);
        if (attr!=null)
            return attr.getStringValue(); 
        return null; 
    }
    
    public VAttribute getAttribute(String name) throws VlException
    {   
        VAttribute attrs[]=getAttributes(new String[]{name});
        
        if ((attrs==null) || (attrs.length<=0))
            return null; 
        
        return attrs[0];
     }
    
    public boolean isRenamable()
    {
        return this.instanceOf(VRenamable.class); 
    }
    
    public boolean isDeletable()
    {
        return this.instanceOf(VDeletable.class);
    }  

    
   

    
	// ========================================================================
	// Abstract Interface 
	// ========================================================================
	 
	abstract public VRL getVRL(); 
		
    /** Return all (alias) VRLs this ProxyNode is equivalent for ! */ 
	abstract public VRL[] getAliasVRLs();
	
	// Inspection methods 
	abstract public boolean isComposite(boolean resolveLink);

	// ===
	// Logical Resource/Resource Link Methods 
	// ===
	
	/**
	 * Return whether this ProxyNode represents a LogicalNode.   
	 * If this node represent a vlink (or.vrsx) file, it is NOT a LogicalNode
	 * only when loaded it is a LogicalNode (like MyVle and ResourceFolders). 
	 * @throws VlException 
	 */  
	abstract public boolean isLogicalNode() throws VlException;
	
	/**
	 * Whether this ProxyNode represents a Resource Link for example a Server Description 
	 * or a VLink.  
	 * @throws VlException 
	 */  
	abstract public boolean isResourceLink(); 

	/**
	 * If this node represents a Resource Link, resolve the target 
	 * and return the new ProxyNode 
	 */
	abstract public ProxyNode getTargetPNode() throws VlException;

	/**
	 * Java reflection method to check the VRS Class of the wrapped VNode
	 */
	abstract public boolean instanceOf(Class<?> classOrInterface); 
	
	/**
	 * Returns Target VRL if node is a ResourceLink. Null otherwise.  
	 */ 		
	abstract public VRL getTargetVRL() throws VlException; 

	abstract public void createLinkTo(ProxyNode pnode) throws VlException; 
	
	// ===
	// Resource Attribute  Interface 
    // ===
	
	abstract public String getName();
	
	abstract public String getType();
	
	/** Gets Effective Mime type */ 
	abstract public String getMimeType() ;
	
	abstract public String[] getResourceTypes();
	
	abstract public Icon getDefaultIcon(int size, boolean isSelected);

	abstract public String[] getAttributeNames();
	
	abstract public String getIconURL() throws VlException; 
	
	abstract public VAttribute[] getAttributes(String[] attrNames) throws VlException;
	
	abstract public void setAttributes(VAttribute[] attrs,boolean refresh) throws VlException ; 
	
	abstract public Presentation getPresentation();
	
	abstract public VAttribute[][] getACL() throws VlException;

	abstract public void setACL(VAttribute[][] acl) throws VlException;

	abstract public VAttribute[] getACLEntities() throws VlException;

	abstract public VAttribute[] createACLRecord(VAttribute entityAttr, boolean writeThrough) throws VlException;

	// 
	// Logical Tree Stucture + Composite Modification Methods: 
	//
	
	abstract public VRL renameTo(String name, boolean nameIsPath) throws VlException;

    abstract public ProxyNode getParent() throws VlException;
	
    abstract public ProxyNode[] getChilds(nl.uva.vlet.gui.view.ViewFilter filter) throws VlException; 
 
	// abstract public int getNrOfChilds(nl.uva.vlet.gui.view.ViewFilter filter) throws VlException;

	abstract public boolean delete(boolean compositeDelete) throws VlException;
	
	abstract public ProxyNode create(String resourceType, String newName) throws VlException; 

	// 
	// Misc 
	//
	
	abstract public boolean isBusy();
	
	/**
	 * Refresh node. May not throw VlException, goes to background if necessary 
	 */
	
	abstract public void refresh();

	///**@deprecated ProxyTNode implementation uses cached ProxyNodes to check links */ 
	//abstract public boolean locationEquals(VRL loc, boolean checkLinkTargets);

	abstract public boolean isEditable() throws VlException;

	
	
}