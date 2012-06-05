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
 * $Id: VNode.java,v 1.12 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ATTRIBUTE_NAMES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHARSET;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICONURL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISCOMPOSITE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISEDITABLE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISVLINK;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_RESOURCE_CLASS;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_RESOURCE_TYPES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_FRAGMENT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_QUERY;

import java.net.URI;
import java.net.URL;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.MimeTypes;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;

/**
 * The VNode class, the super class of all resource nodes in the VRS package.    
 * It can be seen as a handler object, for example a reference to 
 * a (remote) file or directory or other generic resource.
 * Every VNode is associated with a VRL. 
 *  
 * @author P.T. de Boer
 * @see VFSNode
 * @see VFile
 * @see VDir
 */
public abstract class VNode //implements IVNode
{
    /**
     * Class object counter. 
     * Currently used for debugging (see vnodeid)
     */
    private static long vnodecounter=0;

    static private String[] attributeNames=
    {
            ATTR_TYPE,
            ATTR_NAME,
            ATTR_SCHEME,
            ATTR_HOSTNAME,
            ATTR_PORT,
            ATTR_ICONURL,
            ATTR_PATH,
            ATTR_MIMETYPE,
            ATTR_LOCATION
    };
    
    // ========================================================================
    
    // ========================================================================
    
    /** The URI Compatable VRL or which specified the resource location */ 
    private VRL _nodeVRL=null;  
    
    /** 
     * Object ID. 
     * Currently used for debugging, but can be used 
     * to uniquely identify VNode objects in memory 
     */
    private long vnodeid=vnodecounter++;

    /**
     * The *NEW* VRSContext to ensure shared environments !
     * Is FINAL, once set it may never be changed. 
     */ 
	protected final VRSContext vrsContext;
    
   // protected ResourceManager resourceManager;

  
    //  ========================================================================
    //  Constuctors/Initializers 
    //  ========================================================================
    
    /** Block empty constructor */ 
    @SuppressWarnings("unused")
	private VNode()
    {
    	vrsContext=null;
    }
    
    //  ========================================================================
    //  Field Methods  
    //  ========================================================================
    
    /**
     * 
     */ 
    public VNode(VRSContext context,VRL vrl)
	{
		this.vrsContext=context;
        setLocation(vrl); 
	}  
  
	/**
     * See getVRL() 
     * @see getVRL() 
     */ 
    final public VRL getLocation() 
    {
        return _nodeVRL; 
    }

    /** Returns extension part of VRL */ 
	final public String getExtension()
	{
		return this.getLocation().getExtension();  
	}

	
    /** Returns VRSContext which whas used to create this node */
	final public VRSContext getVRSContext()
	{
		return this.vrsContext; 
	}
    /** 
     * Returns Virtual Resource Locator (VRL) of this object. 
     * This is an URI compatible class but with more (URL like) features. 
     * 
     * @see VRL 
     * @see java.net.URI 
     */ 
    final public VRL getVRL() 
    {
        return _nodeVRL; 
    }
    
    /** 
     * Returns URI (java.net.URI) of this object. 
     * This method is the same as getVRL().toURI();  
     * <p>
     * @see VRL 
     * @see java.net.URI
     */ 
    final public URI getURI() throws VlException 
    {
    	if (_nodeVRL==null)
    		return null;
    	
        return _nodeVRL.toURI(); 
    }
    
    /** 
     * Returns URL (java.net.URL) of this object. 
     * This method is the same as getVRL().toURL();  
     * <p>
     * @see VRL 
     * @see java.net.URI
     */ 
    final public URL getURL() throws VlException 
    {
    	if (_nodeVRL==null)
    		return null;
    	
		return _nodeVRL.toURL();
    }
    
    
    /** Get unique VNode id. Currently used for debugging */ 
    public long getNodeID()
    {
        return vnodeid; 
    }
    
    /** 
     * Returns the short name of the resource.<br> 
     * The default is the basename of the resource or the last part
     * of the path part in the URI. 
     * To use another name, subclassses must
     * overide this method.
      */ 
    public String getName()
    {
    	if (_nodeVRL==null)
    		return null;
    	
        return _nodeVRL.getBasename(); // default= last part of path
    }
    
    /** Returns logical path of this resource */ 
    public String getPath()
    {
    	if (_nodeVRL==null)
    		return null;
    	
        return _nodeVRL.getPath();
    }

    /** Returns Hostname */
    public String getHostname()
    {
    	if (_nodeVRL==null)
    		return null;
    	
        return _nodeVRL.getHostname();  
    }
    
    /** Returns Port. If the value <=0 then the default port must be used. */
    public int getPort()
    {
        return _nodeVRL.getPort();  
    }
   
    /** Returns basename part of the path of a node. */ 
     public String getBasename()
    {
        // Default  implementation: getBasename of path 
       return _nodeVRL.getBasename();  
    }
    
    /** 
     * Returns Mime Type based upon file filename/extension. 
     * For a more robust method, use MimeTypes.getMagicMimeType().
     * 
     * @throws VlException 
     *  
     * @see MimeTypes.getMagicMimeType(byte[]) 
     * @see MimeTypes.getMimeType(String) 
     */
   public String getMimeType() throws VlException
   {
       return MimeTypes.getDefault().getMimeType(this.getPath());
   }
   
   /**
    * Default charset for text resources 
    * @throws VlException 
    */ 
   public String getCharSet() throws VlException
   {
       return ResourceLoader.CHARSET_UTF8;
   }
 
    /**
     * Check whether this VNode implements the VComposite interface. 
     */ 
    public boolean isComposite()
    {
        return (this instanceof VComposite); 
    }
    
    /**
     *Get the names of the all attributes this resource has.
     *To get the subset of resource specific 
     */ 
    public String[] getAttributeNames()
    {
        return attributeNames;
    }
    
    /** 
     * Get the names of the resource specific attributes leaving out default attributes and
     * optional super class attributes this resource has.
     * This typically is the subset of getAttributeNames() minus super.getAttributeNames(); 
     */ 
    public String[] getResourceAttributeNames()
    {
        return null;
    }
    /** 
     * Get all attributes defined by attributeNames 
     * @throws VlException 
     */
    public VAttribute[] getAttributes() throws VlException 
    {
        return getAttributes(getAttributeNames());
    }

    /** 
     * Get all attributes defined by <code>names</code>.<br>
     * Elements in the <code>names</code> array may be null! 
     * It means do not fetch the attribute.
     * This is to speed up fetching of indexed attributes.
     * <br>
     * <b>Developpers note</b>:<br>
     * Subclasses are encouraged to overide this method to 
     * speed up fetching multiple attributes as this method
     * does a getAttribute call per attribute. 
     * @throws VlException 
     */
    public VAttribute[] getAttributes(String names[]) throws VlException 
    {
        VAttribute[] attrs = new VAttribute[names.length];

        for (int i = 0; i < names.length; i++)
        {
            if (names[i]!=null)
                attrs[i] = getAttribute(names[i]);
            else
                attrs[i]=null; 
        }

        return attrs;
    }
    
    /**
     * Same as getAttributes(), but return the attributes in an 
     * (Ordened) Attribute set. 
     * 
     * @param names
     * @return
     * @throws VlException 
     */
    public VAttributeSet getAttributeSet(String names[]) throws VlException
    {
    	return new VAttributeSet(getAttributes(names)); 
    }
   
    /** 
     * Get Non-mutable attribute. This is an attribute which can be derived from the 
     * location or doesn't change during the lifetime of the Object because it 
     * is implicit bound to the object class.   
     * Even if the object doesn't exist the attribute can be determined, for 
     * example the Resource Type of a VFile which doesn't change during 
     * the lifetime of the (VFile) Object as this always must be "File" !   
     */ 
    public VAttribute getNonmutableAttribute(String name) throws VlException
    {
        // by prefix values with "", a NULL value will be convert to "NULL".
        if (name.compareTo(ATTR_TYPE) == 0)
            return new VAttribute(name, ""+getType());
        else if (name.compareTo(ATTR_LOCATION) == 0)
            return new VAttribute(name, getVRL()); 
        else if (name.compareTo(ATTR_NAME) == 0)
            return new VAttribute(name, ""+getName());
        else if (name.compareTo(ATTR_HOSTNAME) == 0)
            return new VAttribute(name, getHostname());
        // only return port attribute if it has a meaningful value 
        else if (name.compareTo(ATTR_PORT) == 0)
            return new VAttribute(name,getPort());
        else if (name.compareTo(ATTR_ICONURL) == 0)
            return new VAttribute(name, getIconURL());
        else if (name.compareTo(ATTR_SCHEME) == 0)
            return new VAttribute(name, getScheme());
        else if (name.compareTo(ATTR_PATH) == 0)
            return new VAttribute(name, getPath());
        else if ( (name.compareTo(ATTR_URI_QUERY) == 0) && getLocation().hasQuery() )
            return new VAttribute(name, getQuery());
        else if  ( (name.compareTo(ATTR_URI_FRAGMENT) == 0) &&  getLocation().hasFragment() )
            return new VAttribute(name, getLocation().getFragment());
        else if (name.compareTo(ATTR_NAME) == 0) 
            return new VAttribute(name, getName());
        else if (name.compareTo(ATTR_LOCATION) == 0)
            return new VAttribute(name, getLocation().toString());
        else if (name.compareTo(ATTR_MIMETYPE) == 0)
            return new VAttribute(name, getMimeType());
        else if (name.compareTo(ATTR_ISVLINK) == 0)
            return new VAttribute(name, getLocation().isVLink());
        else if (name.compareTo(ATTR_CHARSET) == 0)
            return new VAttribute(name, getCharSet());
        
        return null; 
    }
    
    /** 
     * This is the single method a Node has to implement so that attributes can be fetched.  
     * subclasses can override this method and do a super.getAttribute first to
     * check whether the superclass provides an attribute name. 
     *  
     * @throws VlException */
    public VAttribute getAttribute(String name) throws VlException
    {
        if (name==null)
            return null;

        // Check Non-mutable attributes first! 
        VAttribute attr=this.getNonmutableAttribute(name); 
        
        if (attr!=null)
            return attr; 

        // ===
        // VAttribute Interface for remote invokation
        // ===
        else if (name.compareTo(ATTR_ISCOMPOSITE) == 0)
            return new VAttribute(name, (this instanceof VComposite));
        else if (name.compareTo(ATTR_RESOURCE_CLASS) == 0)
            return new VAttribute(name, this.getClass().getCanonicalName());
        
        else if (name.compareTo(ATTR_RESOURCE_TYPES) == 0)
        {
            if (this instanceof VComposite)
            {
                String types[]=((VComposite)this).getResourceTypes(); 
                StringList list=new StringList(types);
                return new VAttribute(name,list.toString(",")); 
            }
            else
                return null; 
        } 
        else if (name.compareTo(ATTR_ISEDITABLE) == 0)
        {
            if (this instanceof VEditable)
                return new VAttribute(name,((VEditable)this).isEditable());
            else
                return new VAttribute(name,false); 
        }
        else if (name.compareTo(ATTR_ATTRIBUTE_NAMES) == 0)
        {
            StringList attrL=new StringList(this.getAttributeNames());
            return new VAttribute(name,attrL.toString(","));
        }
        
        return null;   
    }

    /** Return Query part of VRL */ 
    public String getQuery()
	{
		VRL loc=getLocation();
		
		if (loc==null)
			return null;
		
		return loc.getQuery();
	}

	/**
     * Return this node's location as String representation.<br>
     * Note that special characters are not encoded.
      */  
    public String toString()
    {
        return "("+getType()+")"+getLocation(); 
    }

    /** @see setVRL(VRL loc) */
	protected void setLocation(VRL loc)
	{
		this._nodeVRL=loc;  
	}
    
    /**
     * Only subclasses may change the location. 
     * Note that the location should be immutable:
     * It may not change during the lifetime of the object!
     * 
     */ 
    protected void setVRL(VRL loc)
    {
        this._nodeVRL=loc; 
    }
    
    /** Compares whether the nodes represent the same location */ 
    public int compareTo(VNode other)
    {
        if (other==null) 
            return 1; // this > null 
        
        return _nodeVRL.compareTo(other.getLocation()); 
    }

    /**
     * Returns optional icon URL given the preferred size. 
     * Default implementation is to call getIconURL(). 
     * This method allows resources to return different icons
     * for different sizes. 
     * The actual displayed size in the vbrowser may differ
     * from the given size and the preferredSize should be regarded as an indication. 
     * It is recommended to provide optimized icons for sizes less
     * than or equal to 16. 
     */ 
    public String getIconURL(int preferredSize)
    {
    	return getIconURL();  
    }

    
    /** Returns optional icon url */ 
    public String getIconURL()
    {
        return null; 
    }
    
    
   
    /**
     * Returns simple text file or complete HTML page. Method should point to
     * installed documentation.
     * Default is to return help about this type. 
     */
    public VRL getHelp()
    {
        return Global.getHelpUrl(this.getType()); 
    }

    //public abstract String getType(); // type (File) or class

    /** 
     * Get Parent Node (if any).<br>
     * Default implementation is to open the location provided
     * by getParentLocation(). Override that method to provide
     * the parent location of this node.  
     * Overide this method to provide a more eficient way 
     * to return a VNode that is the (logical) parent of this. 
     * 
     * @see #getParents()
     * @see #getParentLocation()
     * @return Parent VNode or null. 
     * @throws VlException
     */
  
    public VNode getParent() throws VlException
    {
        VRL pvrl=getParentLocation(); 
        
        if (pvrl==null)
        	return null; 
        
        return vrsContext.openLocation(getParentLocation());
    }
    
    /**
     * Returns logical parent location of this node. 
     * By default this method returns getVRL().getParent(); 
     * If an implementation has another 'logical' parent then just
     * the dirname of the current location, override this method. 
     */
    public VRL getParentLocation()
    {
    	if (this.getVRL()==null)
    		return null; 
    	
    	return this._nodeVRL.getParent();
    }
    /** 
     * Get Parents if the Node is part of a Graph.
     * <br>
     * Returns one parent if Node is part of a Tree or null if
     * Node has no parents. 
     * @throws VlException
     */
    public VNode[] getParents() throws VlException // for Graph
    {
        VNode parent=getParent();
        
        if (parent==null)
            return null; 
        
        VNode nodes[]=new VNode[1]; 
        nodes[0]=parent; 
        return nodes;
    }
    
    public String getScheme()
    {
        return this.getLocation().getScheme(); 
    }

    /**
     * Like cloneable, to use this method, implement the VDuplicatable interface 
     * and override this method to return a copy. 
     * By default the object should create a deep copy of it's contents
     * and it's children.
     * This default behaviour is different then clone().  
     */ 
	public VNode duplicate() throws VlException
	{
		throw new nl.uva.vlet.exception.NotImplementedException("Duplicate method not implemented"); 
	}


	/**
     * New Action Method version I. (Under Construction).
     * The ActionMappings are taken from the VRS.getActionMappings();
     * @see ActionMenuMapping class.  
     */
    //public void performAction(String name, ActionContext actionContext,VRL selections[]) throws VlException
    //{
    //    throw new NotImplementedException("Node doesn't support actions:"+getName()); 
    // }
	
	/** 
	 * Resolve relative or absolute path against this resource. 
	 * Uses VRL.resolvePaths(this.getPath(),subPath) as default 
	 * implementation.  
	 */ 
    public String resolvePath(String subPath)
	{
		String result=VRL.resolvePaths(this.getPath(),subPath);
		
		return result; 
	}
    
    /** Resolve path against this VRL and return resolved VRL */  
	public VRL resolvePathVRL(String path) throws VRLSyntaxException
	{
		return getLocation().resolvePath(path);  
	}
	
	/** 
	 * Status String for nodes which implemented Status. 
	 * Returns NULL if not supported. 
	 * This method is exposed in the toplevel VNode interface even if not supported.  
	 * @return
	 * @throws VlException 
	 */
	public String getStatus() throws VlException
	{
		return null; 
	}
	
	/** 
	 * Synchronized cached attributes and/or refresh (optional) cached attributes
	 * from remote resource. 
	 * This is an import method in the case that an resource caches resource attributes, like
	 * file attributes. 
	 * @since VLET 1.2 
	 * @return - false : not applicable/not implemented for this resource.<br>
	 *         - true : synchronize/refresh is implemented and was successful.  
	 * @throws VlException when resource synchronisation wasn't successful   
	 */
	public boolean sync() throws VlException 
	{
	    return false; 
	}
	
	/** Fire attribute(s) changed event with this resource as even source.*/ 
	protected void fireAttributesChanged(VAttribute attrs[])
	{
		ResourceEvent event=ResourceEvent.createAttributesChangedEvent(getVRL(),attrs); 
		this.vrsContext.getResourceEventNotifier().fire(event); 
	}
	
	/** Fire attribute changed event with this resource as even source.*/ 
	protected void fireAttributeChanged(VAttribute attr)
	{
		ResourceEvent event=ResourceEvent.createAttributesChangedEvent(getVRL(),
				new VAttribute[]{attr}); 
		this.vrsContext.getResourceEventNotifier().fire(event); 
	}
    // ========================================================================
    // Abstract Interface 
    // ========================================================================
    
    /** Returns resource type, if it has one */ 
    public abstract String getType();
    
    /** Whether this node (still) exists 
     * @throws VlException */
    public abstract boolean exists() throws VlException;

	
    
}
