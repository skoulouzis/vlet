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
 * $Id: LogicalResourceNode.java,v 1.13 2011-06-09 16:10:21 ptdeboer Exp $  
 * $Date: 2011-06-09 16:10:21 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICONURL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SHOW_SHORTCUT_ICON;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TARGET_IS_COMPOSITE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TARGET_MIMETYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_FRAGMENT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_QUERY;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_USERNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_VO_NAME;
import static nl.uva.vlet.vrs.VRS.LINK_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.xml.VPersistance;
import nl.uva.vlet.data.xml.XMLData;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.ResourceNotEditableException;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlXMLDataException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.util.MimeTypes;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrl.VRLUtil;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.ServerInfoRegistry;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VDeletable;
import nl.uva.vlet.vrs.VDuplicatable;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRenamable;
import nl.uva.vlet.vrs.io.VStreamAccessable;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;



/**
 * Super Class of LinkNode and ServerConfig, ResourceFolder and ResourceLocation.  
 * 
 * The LogicalResourceNode describes a resource, which can be a Link or a Server. 
 * Instead of being the resource (File,Directory) itself, like VFile and VDir. 
 * the ResourceNode has a 'Target' which is the linked-to resource or the 
 * Remote Server.<br>
 * This "target" typically is a remote (file) server or service. 
 * <p>
 * ServerConfig Resource: <br> 
 * If this Resource is of "ServerConfig" type the Resource Information is stored
 * into the ServerInfoRegistry as ServerInfo. 
 * <p>
 * A ResourceNode can be stored as a (V)File into its decriptionLocation.
 * In that case it is saved as a VLink (LinkNode type).  
 * 
 * @see ServerInfo 
 * @see LinkNode
 * @see ServerInfoRegistry
 * 
 */  
public class LogicalResourceNode extends VNode implements 
   VEditable,VDeletable,Cloneable,VRenamable,VStreamAccessable, 
   VLogicalResource,VResourceLink,VPersistance,VDuplicatable<LogicalResourceNode>,VPresentable
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(LogicalResourceNode.class);
    }
    
    /** Default value when not specified */ 
    public static final boolean default_show_shortcut_icon = true;

    public static final String PERSISTANT_TYPE = "LogicalResourceNode"; 
    
    public static final String SECTION_GUI_CONFIG="GUI Config";
    
    public static final String SECTION_SEVER_CONFIG="Server Config";
            
    public static final String SECTION_URI_ATTRIBUTES="URI Attributes";
       
    public static final String SERVER_CONFIG_TYPE="ServerConfig";
   
 
    
    // deprecated
    //public static final String SERVER_TYPE="Server"; 

    /**
     * Mandatory Resource Attributes    
     */ 
    protected static String[] resourceAttributeNames=
    {
        ATTR_TYPE,
        ATTR_SCHEME,
        ATTR_NAME,
        //Make sure username matches ServerInfo !  
        ATTR_USERNAME,
        ATTR_HOSTNAME,
        ATTR_PORT,
        ATTR_PATH,
    };

    /**
     * Optional Extra Link/URI attributes    
     */ 
    protected static String[] uriAttributeNames=
    {
    		ATTR_URI_QUERY,
    		ATTR_URI_FRAGMENT      
    };
    
    /** Extra gui presentation attributes. */  
    protected static String[] guiAttributeNames=
    {
        ATTR_ICONURL,
        ATTR_SHOW_SHORTCUT_ICON
    };

    // All allowed attribute names 
    protected static StringList allAttributeNames=StringList.createFrom(
            resourceAttributeNames,
            uriAttributeNames, // optional ! 
            guiAttributeNames); 
   
    // All allowed attribute names 
    protected static StringList linkAttributeNames=StringList.createFrom(
            resourceAttributeNames,
            uriAttributeNames, // optional ! 
            guiAttributeNames); 
   
    /** 
     * Presentation: Order of returned attribute names ! 
     * Beatification for the VBrowser
     */ 
    protected static String[] defaultServerInfoAttrNames=
    {
        ATTR_TYPE,
        ATTR_SCHEME,
        ServerInfo.ATTR_SERVER_ID,
        ServerInfo.ATTR_SERVER_NAME,
        ATTR_NAME,
        ATTR_USERNAME, // username might be deleted 
        ATTR_HOSTNAME, // hostname might be deleted 
        ATTR_PORT,	// port might be deleted
    };

    /**
     * Link target attributes. Can not be set. 
     * Are taken from target resource in method updateTargetAttributes()
     * @see updateTargetAttributes  
     */ 
    protected static String[] targetAttributeNames=
    {
        ATTR_TARGET_MIMETYPE,
        ATTR_TARGET_IS_COMPOSITE
    };

    // =======================================================================
    // Instance 
    // =======================================================================

    /**
     * Contains ALL attributes. Also contains copies from ServerInfo 
     */   
    protected VAttributeSet resourceAttributes=new VAttributeSet();

    /** Custom Presentation */ 
    private Presentation presentation;
    
    /** Optional parent for in-memory resourceNode (=logical parent) */ 
    protected VNode parent=null;

    /** 
     * The location where this ResourceNode is stored. 
     * Typically a link File (.vlink) 
     * If null, this ResourceNode exists only in memory and is 'true' virtual. 
     */ 
    protected VNode storageNode=null; 
    
    // use XMLData interface as default (does auto-update) ! 
    private boolean useXML=true;

    private boolean isEditable=true;

    //private ServerInfo serverInfo;

    public static final boolean useMergedServerConfig=true; 

    // === 
    // Constructors, duplicate,intializers 
    // === 
    
    public LogicalResourceNode(VRSContext context, VRL logicalVRL)
    {
        super(context, logicalVRL);
    }
    

    public LogicalResourceNode(VRSContext context, VAttributeSet attrSet, VRL logicalVRL) throws VlException
    {
        super(context, logicalVRL);
        this.resourceAttributes=attrSet.duplicate(); 
    }

    public LogicalResourceNode duplicate() throws VlException
    {
        LogicalResourceNode newNode=new LogicalResourceNode(this.getVRSContext(),this.resourceAttributes,(VRL)null);
        return newNode; 
    }

    
    // basic initializer: one for all!  
    protected void init(VRL logicalVRL,VRL targetVRL,boolean resolveLink) throws VlException
    {
        this.setLocation(logicalVRL); 
        setResourceVRL(targetVRL);
        setName("link:"+targetVRL);

        // update isComposite and other Target Attributes
        updateTargetAttributes(resolveLink); 

        debugPrintln("logicalVRL ="+this.getVRL());
        debugPrintln("targetVRL   ="+targetVRL);
    }

    /** Duplicate: Initializer */ 
    protected void copyFrom(LogicalResourceNode source)
    {
        // do not create a link to a link, but copy parameters ! 
        this.resourceAttributes=source.resourceAttributes.duplicate(); 

        // reset others to null

        //implementation=source.implementation;
        this.storageNode=null; // never not evar share save location ! 
        this.parent=source.parent; // they may share parents; 
        this.isEditable=source.isEditable;

    }

    // ===
    // Getters // Setters
    // ===
    /** Currently SERVER type or LINK type */ 
    public String getType()
    {
        VAttribute attr=resourceAttributes.get(ATTR_TYPE);

        if (attr!=null) 
            return attr.getStringValue();

        return null; 
    }

    protected void setType(String type)
    {
        resourceAttributes.put(ATTR_TYPE,type);
    }
    // ===========================
    // VNode interface 
    // =========================== 

    @Override
    public String getName() 
    {
        String name=resourceAttributes.getStringValue(ATTR_NAME);

        if (name==null) 
            name=getBasename(); 

        return name;   
    }

    /** 
     * A ResourcNode is an already resolved node (data object) in 
     * memory so as an 'object' it already exists.
     * The storage location might not exist (yet). 
     */
    @Override
    public boolean exists() 
    {
        return true; 
    }

    /** Returns 'Logical' Parent. For example another virtual 
     * node or the 'MyVle' object */ 
    @Override
    public VNode getParent() throws VlException
    {
        return (VNode)parent; 
    }

    /**
     * Returns logical parent location of this node. 
     * By default this method returns getVRL().getParent(); 
     * If an implementation has another 'logical' parent then just
     * the dirname of the current location, override this method. 
     */
    public VRL getParentLocation()
    {
        return this.parent.getVRL(); 
    }

    // ========================================================================
    // VEditable interface 
    // ========================================================================

    //== LinkNode itself should be deletable
    public boolean isEditable() 
    {
        return this.isEditable; 
    }

    public boolean isDeletable() throws VlException
    {
        if (storageNode!=null) 
        {   
            if (this.storageNode instanceof VDeletable)
            {
                return ((VDeletable)this.storageNode).isDeletable();
            }
            else
            {
                return false;
            }
        }
        // not editable means not PERSISTANT Editable: changes must be stored! 
        /*if (parent!=null) 
        {
            // no implementation, but has parent: logical element in parent object
            return true; 
        }*/

        return false; 
    }

    // =======================================================================
    // Resource Interface 
    // =======================================================================

    /** 
     * VNode's method 'setLocation' is protected as it may be set only once,
     * preferably when calling the (default) constructor. 
     * To accomodate for the creation of LinkNodes or Virtual Resource Nodes, the
     * location might be set at a later time use this method. 
     *  
     * @param loc
     */

    public synchronized void setLogicalLocation(VRL loc) 
    {
        //if (getLocation()!=null)
        //        throw new Error("Can not change (Logical) Location once it has been set");

        this.setLocation(loc);
    }

    public void setTargetHostname(String val)
    {
        resourceAttributes.put(ATTR_HOSTNAME,val); 
    }

    protected void setTargetPort(int val)
    {
        resourceAttributes.put(ATTR_PORT,val); 
    }
    
    /**
     * Since the username is OPTIONAL setting this value to null 
     * will remove the username attribute from the linktarget attribute
     * list, so it will not appear in the Attribute list ! 
     * (When doing a getAttributes() for example).
     * When the resource has an username, make sure it matches against the username
     * used in the ServerInfo Registry! 
     */
    public void setTargetUsername(String val)
    {
        // empty username means: ignore/delete
        if ((val==null) || (val.compareTo("")==0))
            resourceAttributes.remove(ATTR_USERNAME); 
        else
            resourceAttributes.put(ATTR_USERNAME,val); 
    }
    
    /**
     * Set optional VO name for this resource. This is used for VO specific
     * locations and resource attributes. 
     */
    public void setTargetVOname(String val)
    {
        // empty username means: ignore/delete
        if ((val==null) || (val.compareTo("")==0))
            resourceAttributes.remove(ATTR_VO_NAME); 
        else
            resourceAttributes.put(ATTR_VO_NAME,val); 
    }

    public String getResourceType() 
    {
        return resourceAttributes.getStringValue(ATTR_TYPE); 
    }

    protected void setTargetPath(String val)
    {
        if (val!=null)
            resourceAttributes.put(ATTR_PATH,val); 
    }

    private void setTargetQuery(String val)
    {
        // only set it when not null 
        if (val!=null) 
            resourceAttributes.put(ATTR_URI_QUERY,val); 
    }

    private void setTargetFragment(String val)
    {
        // only set it when not null 
        if (val!=null) 
            resourceAttributes.put(ATTR_URI_FRAGMENT,val); 
    }

    protected void setTargetScheme(String scheme)
    {
        if (scheme!=null) 
            // linkAttributes.set(new VAttribute(ATTR_SCHEME,Registry.getDefaultSchemeNames(),scheme)); 
            resourceAttributes.put(ATTR_SCHEME,scheme); 
    }

    public boolean save()
    {
        try
        {
            // default save: 
            return doSave(); 
        }
        catch (Exception e)
        {
            // Default save not implemented or possible for all types: 
            Global.logException(ClassLogger.WARN,this,e,"Could not save:%s\n",this);
            return false; 
        }
    }
    
    /** Default save() for subclasses */ 
    protected boolean doSave() throws VlException
    {
        // logical node: might be part of MyVLe. 
         
        
        if (storageNode==null)  
        {
            if (this.parent instanceof ResourceFolder)
            {
                debugPrintln("saving to parent:"+parent); 
                return ((ResourceFolder)parent).save();
            }
            else if (parent==null)
            {
                throw new VlException("Storage Error","Both description Location as Parent are set to NULL"); 
            }
            else
            {
                Global.debugPrintf(this,"_save() called, but can't invoke save on unknown parent:"+parent);
                throw new NotImplementedException("Resource doesn't have a storage location:"+this);
            }
        }
        
        debugPrintln("saving to:"+storageNode);
        
        if ((storageNode instanceof VStreamWritable)==false)
        {
            throw new ResourceTypeMismatchException("Created storage node does not support write methods:"+storageNode);
        }

        OutputStream outp=((VStreamWritable)storageNode).getOutputStream(); 
        
        try
        { 
            //BUUGGY this.properties.storeToXML(outp,"VRS.LinkNode");
            //if (useXML)
                writeAsXmlTo(outp); 
            //else
            //    resourceAttributes.store(outp,"VL-e Resource description of type:"+this.getType());
            
            try 
            { 
                outp.flush(); 
                outp.close();
            }
            catch (IOException e) 
            {
                warnPrintln("Exception when closing outputstream:"+e);
            }
            
            return true; 
            
        }    
        finally
        {
            try { outp.close();  } catch (IOException e) {}
        }
    }
    
    
    public void writeAsXmlTo(OutputStream outp) throws VlXMLDataException
    {
        String comments="VL-e Resource description of type:"+this.getType();
        XMLData xmlData=getXMLData(); 
        // Server Attributes moved to ServerReg !
        VAttributeSet attrs=this.resourceAttributes; // this.getResourceAttributeSet(useServerRegistry);
        
        xmlData.writeAsXML(outp,attrs,comments);
    }
   
    /** XMLData factory */ 
    protected XMLData getXMLData()
    {
        XMLData xmlData=new XMLData();
        xmlData.setVAttributeSetElementName("vlet:ResourceDescription"); 
        xmlData.setVAttributeElementName("vlet:ResourceProperty");
        return xmlData;
    }

    /** The VNode which stores the LinkNode object, usually a VFile */ 
    public VNode getStorageNode()
    {
        return storageNode;
    }

    /** The VRL which points to the (persistant) storage location */ 
    public VRL getStorageLocation()
    {
        if (storageNode==null) 
            return null; 

        return storageNode.getVRL();
    }

    /** sets setIconURL of this linknode  */ 
    public void setIconURL(String url) 
    {
        resourceAttributes.put(new VAttribute(ATTR_ICONURL,url)); 
    }

    /** Returns mimetype of link target */ 
    public String getTargetMimeType() 
    {
        if (this.isServerConfigType())
            return null; 

        return MimeTypes.getDefault().getMimeType(resourceAttributes.getStringValue(ATTR_PATH)); 
    }

    /** Override: Get TARGET MimeType.  */ 
    @Override 
    public String getMimeType()
    {
        if (this.isServerConfigType())
            return null; 

        return getTargetMimeType(); 
    }

    public String getIconURL()
    {
        return getTargetIconURL(); 
    }
    
    public String getTargetIconURL()
    {
        String val=resourceAttributes.getStringValue(ATTR_ICONURL);

        return val; 
    }

    public boolean isServerConfigType()
    {
        String ltype=getResourceType();

        if (ltype==null) 
            return false; 

        return StringUtil.equalsIgnoreCase(SERVER_CONFIG_TYPE,ltype); 
    }
    
    public boolean isLinkType()
    {
        String ltype=getResourceType();

        if (ltype==null) 
            return false; 

        return StringUtil.equalsIgnoreCase(LINK_TYPE,ltype);
    }

    public void setShowShortCutIcon(boolean b)
    {
        resourceAttributes.put(ATTR_SHOW_SHORTCUT_ICON,b); 
    }

    public boolean getShowShortCutIcon()
    {
        return resourceAttributes.getBooleanValue(ATTR_SHOW_SHORTCUT_ICON,true);
    }

    // interface VEditable 
    public boolean setAttributes(VAttribute[] attrs,boolean store) throws VlException
    {
        boolean result = true;

        for (int i = 0; i < attrs.length; i++)
        {
            Boolean res2=setAttribute(attrs[i],false);
            result =result && res2;  
        }

        // Update persistant server registry ! 
        if (store && isServerConfigType() && useMergedServerConfig)
        {
            ServerInfo info=this.getServerInfo(); 
                
            for (VAttribute attr:attrs)
            {
                info.setAttribute(attr); 
            }
            updateAndStoreServerInfo(info);

        }

        if (store)
        	save(); 
        
        return result;
    }
    // interface VEditable 
    public boolean setAttributes(VAttribute[] attrs) throws VlException
    {
        return setAttributes(attrs,true); 
    }

    public void setLogicalParent(VNode node)
    {
        this.parent=node;
    }

    //@Override
    public boolean setAttribute(VAttribute attr) throws VlException 
    {
        return setAttribute(attr,true); 
    }

    //@Override
    public boolean setAttribute(VAttribute attr, boolean store) throws VlException 
    {
        debugPrintln("setAttribute:"+attr); 
        
        if (attr==null) 
            return false; 
        
        String name=attr.getName(); 
        
        //Only store allowed Attribute ! 
        if (allAttributeNames.contains(name))
        { 
            resourceAttributes.put(attr);
        }
       
        if (store)
            save();

        return true;
    }
   
    protected ServerInfo getServerInfo()
    {
//        // do not cache serverinfo ?
//        if (serverInfo!=null)
//            return serverInfo; 
        
        // ===
        // stay  old style compatible for now: 
        // ===
        boolean autoInit=true; 
        
        ServerInfo info=null; 
    	
    	// query registry for matching ServerInfo: 
    	
        String scheme=getTargetScheme();
        String host=getTargetHostname();
        int port=getTargetPort();
        
        // Can be NULL ! 
        String userInf=this.getTargetUserInfo(); 
        
        ServerInfoRegistry infoReg = vrsContext.getServerInfoRegistry();
        
        ServerInfo infos[]=infoReg.getServerInfos(scheme,host,port,userInf); 
        if ((infos!=null) && (infos.length>0))
        {
            info=infos[0];
        }
        else if (autoInit==true)
        {
            VRL vrl=new VRL(scheme,host,port,null);
            
            logger.infoPrintf("Creating new ServerInfo for:%s\n",vrl); 
            
            try
            {
                info=this.vrsContext.getServerInfoFor(vrl,true);
            }
            catch (Throwable e)
            {
                warnPrintln("*** Warning, couldn't create new ServerInfo for:"+vrl);  
                warnPrintln("*** Exception ="+e);
            }
        }
        
        // serverInfo=info; 
        
        return info; 
    }
    
    private String[] getServerInfoAttributeNames()
    {
        ServerInfo info=getServerInfo();
        if (info!=null)
            return info.getAttributeNames(); 
        return null; 
    }
    
    public void setName(String val)
    {
        resourceAttributes.put(ATTR_NAME,val); 
    }
    
    public boolean renameTo(String newName, boolean nameIsPath) throws VlException
    {
        return (rename(newName,nameIsPath)!=null); 
    }

    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        if (this.isEditable==false)
            throw new nl.uva.vlet.exception.ResourceNotEditableException("Cannot rename this resource"); 

        this.setAttribute(new VAttribute(ATTR_NAME,newName));
        
        return this.getVRL(); 
    }

    public boolean isRenamable() throws VlException
    {
        return true; 
    }

    public VRL getTargetLocation() throws VlException 
    {
        return this.getTargetVRL(); 
    }

    public String getTargetScheme()
    {
        return resourceAttributes.getStringValue(ATTR_SCHEME); 
    }
    

    public int getTargetPort() 
    {
        try
        {
            return resourceAttributes.getIntValue(ATTR_PORT);
        }
        catch (Throwable e)
        {
            // regression 
            logger.logException(ClassLogger.ERROR,e,"*** InternalError:%s\n",e);    
        }
        return -1; 
    }

    /**s
     * This method returns the username without optional domain name info or VO name 
     */ 
    public String getTargetUsername()
    {
        return this.resourceAttributes.getStringValue(VAttributeConstants.ATTR_USERNAME); 
    }  
    
    /**
     * This method returns optional VO name used for this location  
     */ 
    public String getTargetVO()
    {
        return this.resourceAttributes.getStringValue(VAttributeConstants.ATTR_VO_NAME);
    }  

    /**
     * Returns userinfo string which consists of a username and 
     * an optional vo name as follows: "&lt;USER&gt;[&lt;VO&gt;]"
     * @see ServerInfo#createUserinfo(String, String) 
     */ 
    public String getTargetUserInfo()
    {
        String userstr=resourceAttributes.getStringValue(ATTR_USERNAME);
        String vostr=resourceAttributes.getStringValue(VAttributeConstants.ATTR_VO_NAME);

        // Make sure to create ServerInfo compatible User Info Strings !*/ 
        return ServerInfo.createUserinfo(userstr,vostr); 
    }  

    public String getTargetHostname()
    {
        return resourceAttributes.getStringValue(ATTR_HOSTNAME); 
    }

    public String getTargetPath()
    {
        return resourceAttributes.getStringValue(ATTR_PATH); 
    }

    public String getTargetQuery()
    {
        return resourceAttributes.getStringValue(ATTR_URI_QUERY); 
    }

    public String getTargetFragment()
    {
        return resourceAttributes.getStringValue(ATTR_URI_FRAGMENT); 
    }    
    /**
     * Checks whether remote resource is composite or not.
     * This method might auto update the isComposite attribute
     * of the (remote) target is this isn't set yet!   
     *  
     * @param resolve
     * @return
     * @throws VlException
     */
    public boolean getTargetIsComposite(boolean defaultVal) throws VlException
    {
        // config node ! 
        //check again: 
        this.updateTargetAttributes(false); 

        String val=resourceAttributes.getStringValue(ATTR_TARGET_IS_COMPOSITE);

        if (val!=null)
        {
            return new Boolean(val); 
        }
        
        return defaultVal;
    }
    
    public void setTargetIsComposite(boolean val)
    {
    	resourceAttributes.put(new VAttribute(ATTR_TARGET_IS_COMPOSITE,val)); 
   }

    /**
     * Update stored Target Attributes: {isComposite, mimetype} 
     * Note that IconURL is an attribute stored by the VBrowser!
     * This method can also be used to (auto) update/check Link/Server nodes
     * with new attributes. 
     * @return
     */
    protected boolean updateTargetAttributes(boolean connectIfNotConnected)
    {
        logger.infoPrintf("updateTargetAttributes(%s):\n",connectIfNotConnected); 
        
        try
        {
            VRL target=this.getTargetVRL();

            // ====
            // Check whether remote server already has been instantiated !
            // This is an optimization. If Server instance is already there
            // the resource can be resolved. If not, do not create/contact
            // remote server. 
            // ====
            VNode[] resNodes = this.vrsContext.getResourceSystemNodes();
            boolean contacted=false;
            
            for (VNode resNode:resNodes)
            {
                // scheme,host and port. 
                if (VRLUtil.hasSameServer(resNode.getVRL(),target))
                {
                    contacted=true; 
                    break; 
                }
            }
            
            if (contacted==false)
            {
                if (connectIfNotConnected==false)
                {
                    logger.debugPrintf("updateTargetAttributes(): --- Not contacted: Will NOT update attributes for:%s\n",target); 
                    return false; 
                }
                else
                {
                    logger.debugPrintf("updateTargetAttributes(): +++ Not contacted: Will update attributes for:%s\n",target);
                }
            }
            else
            {
                logger.debugPrintf("updateTargetAttributes(): >>> Contacted == true. Will update attributes: %s\n",target);
            }
            
            logger.infoPrintf("updateTargetAttributes(): >>> Reconnecting: Updating Attributes: isComposite of:%s\n",target);
            
            VNode vnode=vrsContext.openLocation(this.getTargetVRL());

            // composite node: 
            boolean isComposite=(vnode instanceof VComposite);
            resourceAttributes.set(ATTR_TARGET_IS_COMPOSITE,isComposite);

            // mimetype: 
            String mime=vnode.getMimeType(); 
            if (mime==null)
                 mime=""; // unknown .. 
            resourceAttributes.set(ATTR_TARGET_MIMETYPE,mime); 
           
            this.save();

            return true; // all OK. 
        }
        // autoupdate must be silent: 
        catch (Exception e)
        {
            logger.logException(ClassLogger.DEBUG,e,"Couldn't autoupdate this LinkNode:%s\n",this); 
            return false; // Error ! 
        }
    }

    /** 
     * Returns Resource Target Location this Logical Resource "points" to.  
     *  
     * @throws VlException 
     * @throws VlException 
     */

    public VRL getTargetVRL() throws VlException 
    {
        // before resolving a link, make sure extra Server Properties 
        // are stored 
        VRL vrl=null;

        String scheme=getTargetScheme();

        if (scheme==null)
        {
            logger.infoPrintf("LinkNode:NULL scheme part in Link Location: No target Link!");
            return null;
        }

        vrl=new VRL(
                getTargetScheme(),
                getTargetUserInfo(),
                getTargetHostname(), 
                getTargetPort(),
                getTargetPath(),
                getTargetQuery(),   // may be null
                getTargetFragment() // may be null
        );            

        return vrl; 
    }

    protected void setResourceVRL(VRL linkTarget)
    {
        if (linkTarget==null) 
            return; 

        //this.setLinkName(linkTarget.toString()); 
        this.setTargetScheme(linkTarget.getScheme());
        this.setTargetHostname(linkTarget.getHostname());
        this.setTargetPort(linkTarget.getPort());
        this.setTargetUsername(linkTarget.getUserinfo());
        this.setTargetPath(linkTarget.getPath());
        this.setTargetQuery(linkTarget.getQuery());
        this.setTargetFragment(linkTarget.getFragment());
    }


    /** 
     * Factory method to load stored ResourceNode. 
     * Currently only .vlink file are supported. 
     */  
    public void loadFrom(VNode vnode) throws VlException
    {
        debugPrintln("loading from:"+vnode); 
        
        //VlException except1=null; 

        if ((vnode instanceof VStreamReadable)==false)
        {
            throw new ResourceTypeMismatchException("Remote resource is not (stream)readable:"+vnode);
        }

        // set location to loaded location 
        this.storageNode=vnode; 
        this.setLocation(vnode.getLocation()); 

        InputStream inps=null;
        resourceAttributes=null;
        
        
        //
        // New: read from XML file: 
        // 
        if (useXML)
        {
            try 
            {
                inps=((VStreamReadable)vnode).getInputStream();
                XMLData data=getXMLData(); 
                resourceAttributes=data.parseVAttributeSet(inps);
            }
            catch (VlXMLDataException ex)
            {
                //except1=ex;

                debugPrintln("Not an XML file:"+vnode+" ???");

                try
                {
                    inps.close();
                }
                catch (IOException e)
                {
                    debugPrintln("Ignoring Error when closing inputstream:"+e); 
                }
            }
        }

        if (resourceAttributes==null)
        {
            importOldV09config(vnode);  
            save();
        }

        if ((resourceAttributes==null) && (resourceAttributes.size()==0))
        {
            throw new VlException("ReadError","Empty resource:"+this);
        }

        //update target attributes (if possible)  
        {
            // Legacy Bug: 
            // Type of attribute wasn't always stored or stored as Boolean Type.    
            // should be fixed now XML is used (still changeable though) 
            String str=resourceAttributes.getStringValue(ATTR_TARGET_IS_COMPOSITE); 

            Boolean value; 

            if (str!=null)
            {
                resourceAttributes.remove(ATTR_TARGET_IS_COMPOSITE);
                value=new Boolean(str); 
            }
            else
            {
                value=true; 
            }
            // add as BOOLEAN attribute: 
            resourceAttributes.put(new VAttribute(ATTR_TARGET_IS_COMPOSITE,value)); 
        }
        
        // Update later added attributes: 
        if (this.resourceAttributes.get(ATTR_SHOW_SHORTCUT_ICON)==null)
        {
        	if (this.isLinkType())
        		this.resourceAttributes.set(ATTR_SHOW_SHORTCUT_ICON,true); 
        	else
        		this.resourceAttributes.set(ATTR_SHOW_SHORTCUT_ICON,false);
        }
        // Update later added attributes: 
        if (this.resourceAttributes.get(ATTR_ICONURL)==null)
        {
        	if (this.isLinkType())
        		this.resourceAttributes.set(ATTR_ICONURL,""); 
        	else
        		this.resourceAttributes.set(ATTR_ICONURL,"");
        }
       
        // ====================================================================
        // Server -> ResourceLocation 
        // 
        // Prev version 1.0 update
        // Default location is "ResourceLocation". "Server" type is reserved. 
        // ====================================================================
        if (StringUtil.compareIgnoreCase(getType(),"Server")==0) 
            this.setType(VRS.RESOURCE_LOCATION_TYPE);
        
        // ====================================================================
        // Version 1.1.4 -> 1.2.0 Update! 
        // Auto-Repair ResourceInfo to ResourceLocation !
        // ResourceInfo is dynamically created, may be be stored.
        // ====================================================================
        if (StringUtil.compareIgnoreCase(getType(),VRS.RESOURCE_INFO_TYPE)==0) 
            this.setType(VRS.RESOURCE_LOCATION_TYPE); 
            
    }
        

    private void importOldV09config(VNode vnode) throws VlException
    {
        // 
        // open old file 
        // reopen: 
        //
        InputStream inps = ((VStreamReadable)vnode).getInputStream(); 
        resourceAttributes=VAttributeSet.readFrom(inps);
        try
        {
            inps.close();
        }
        catch (IOException e)
        {
            debugPrintln("Ignoring Error when closing inputstream:"+e); 
        }
        
        // auto rewrite OLD properties file ! 
        if (useXML)
        {
            warnPrintln("Rewriting old properties file to new XML file:"+this); 
          
        }
       
        
        inps=null; 

        // Legacy update Pre 0.9 update: 
        {
            // remove <0.9 attributes: 
            String str=resourceAttributes.getStringValue("serverAttributes");
            if (str!=null)
                resourceAttributes.remove("serverAttributes");

            str=resourceAttributes.getStringValue("extraAttributes");
            if (str!=null)
                resourceAttributes.remove("extraAttributes");
        }

        // ===
        // copy server configuration into ServerRegistry:  
        // === 
        
        if (this.isServerConfigType())
        {
            ServerInfo info = getServerInfo();
            if (info!=null)
            {
                info.updateServerAttributesFrom(resourceAttributes,true); 
                info.store();
            }
        }
    }


    @Override
    public VAttribute getAttribute(String name) throws VlException  
    {
        VAttribute attr=null; 

        if (name==null)
            return null; 

        if (name.startsWith("[")) 
            return new VAttribute(name,""); 
        
        // allowed resource attribute: Filter ! 
        if (allAttributeNames.contains(name))
        { 
            attr=resourceAttributes.get(name);
        }
        else if (useMergedServerConfig==true)
        { 
            // old style: merge attributes with ServerInfo    
            if ((isServerConfigType()) && (getServerInfo()!=null)) 
                attr=getServerInfo().getAttribute(name); 
        }
        
        if (attr==null) 
            attr=super.getAttribute(name); 

        attr=checkAttribute(attr);
        debugPrintln("getAttribute, returning:"+attr);
        return attr; 
    }

    
    private String[] getRegisteredSchemes()
    {
        return this.vrsContext.getRegistry().getDefaultSchemeNames(); 
    }

    //  @Override
    public boolean delete() throws VlException
    {
        if (deleteStorageLocation()==false) 
            return false; 
       
        if (this.isServerConfigType())
        {
        	ServerInfo info=this.getServerInfo(); 
        	if (info!=null)
        		info.persistentDelete(); 
        }

     // Notify Parent ! 
        if (parent instanceof VLogicalFolder)
            return ((VLogicalFolder)parent).unlinkNode(this);
        
        return true; 
    }

    /** 
     * Returns Resource Attribute Names. Does sorting as well. 
     */
    
    @Override 
    public String[] getAttributeNames()
    {
        // return linkAttributeNames; 
        // As the VBrowser displays the names in the order it 
        // receives them, sort them in the way as they should be displayed
        StringList list=new StringList(resourceAttributeNames);
        
        if (this.isServerConfigType())
        {
            // === Server Section === //
            list.add("["+SECTION_SEVER_CONFIG+"]"); 
            if (getServerInfo()!=null)
            {
                StringList attrNames=new StringList(getServerInfo().getAttributeNames());
                // do some sorting before returning them so the same 
                // attribute always appear in the same order.  
                attrNames.orden(defaultServerInfoAttrNames);
                list.merge(attrNames);
                // Todo: better ServerInfo integration.  
                if (attrNames.contains(ATTR_USERNAME)==false)
                    list.remove(ATTR_USERNAME);
                if (attrNames.contains(ATTR_HOSTNAME)==false)
                    list.remove(ATTR_HOSTNAME);
                if (attrNames.contains(ATTR_PORT)==false)
                    list.remove(ATTR_PORT);
            }
        }
        
        {
            // === Link Section === // 
            list.add("["+SECTION_URI_ATTRIBUTES+"]"); 
            list.add(uriAttributeNames); //optional and extra links attributes
        }
        
        // remove hidden attributes: 
        list.remove(targetAttributeNames);
        
        // Arrgg could already be in ServerAttributes
        list.remove(guiAttributeNames);
    
        // add gui attributes as last: 
        list.add("["+SECTION_GUI_CONFIG+"]");
        list.add(guiAttributeNames);
    
        return list.toArray();
    }

    /**
     * Returns Resource Attributes defined for this ResourceNode. 
     * Strips GUI attributes.
     */ 
    public String[] getResourceAttributeNames()
    {
        // return linkAttributeNames; 
        // As the GUI displays the names in the order it 
        // receives them, sort them in the way as they should be displayed
        StringList list=new StringList(this.resourceAttributes.getOrdenedKeyArray()); 

        // remove gui attributes 
        list.remove(guiAttributeNames); 
        return list.toArray();
    }

    public InputStream getInputStream() throws VlException
    {
        if (this.storageNode instanceof VStreamReadable)
            return ((VStreamReadable)this.storageNode).getInputStream();

        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Couldn't get InputStream from:"+storageNode);
    }


    public OutputStream getOutputStream() throws VlException
    {
        if (this.storageNode instanceof VStreamWritable)
            return ((VStreamWritable)this.storageNode).getOutputStream();

        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Couldn't get InputStream from:"+storageNode);
    }
   
    /**
     * Factory method, resolving resource desciption and return a LogicalResource 
     * @throws VlException 
     * 
     */

    public static LogicalResourceNode loadFrom(VRSContext vrsContext, VNode node) throws VlException
    {
        // Use linknode: 
        return LinkNode.loadFrom(vrsContext,node); 
    }

    /** Store Resource node. This method updates the used storage location */
    public void saveAtLocation(VRL saveLocation) throws VlException
    {
        _saveTo(saveLocation);
    }
    
    // Actual Save implementation, updates 'storageNode' 
    private void _saveTo(VRL saveLocation) throws VlException
    {
        VNode parentNode=this.getVRSContext().openLocation(saveLocation.getParent()); 
        String baseName=saveLocation.getBasename(); 
        
        if ((parentNode instanceof VComposite)==false)
        {
            throw new ResourceTypeMismatchException("Storage location cannot have child nodes:"+parentNode);
        }

        VComposite parent=(VComposite)parentNode; 
        
        // MyVle hack: Copy LinkNode as-is into MyVLe. 
        if (parent instanceof MyVLe)
        {
            LogicalResourceNode newNode =  (LogicalResourceNode) parent.addNode(this,baseName,false);
            // update implementation ! 

            this.copyFrom(newNode); // copy all (name+implementation)
            // Warning!: copy constructor does not copy implementation 
            // (For consistancy reasons) 
            this.storageNode=newNode.storageNode;
            // copy Logical Location as Well ! 
            this.setLocation(newNode.getVRL()); 
            //this.parent=(MyVLe)parent; 
            return; 
        }
        else if (parent instanceof ResourceFolder)
        {
            // MOVE into ResourceGroup ! 
            LogicalResourceNode newnode =(LogicalResourceNode) ((ResourceFolder)parent).addNode(this,baseName,true);
            
            // Assert ! 
            if (newnode.equals(this)==false)    
                throw new VlException("Internal Error","LogicalResourceNode:save() at ResourceGroup must return same node"); 
          
            return; 
            
        }

        // =======================
        // Default: save to (V)File !
        // Logical Location is Storage location. 
        // =======================
        
        //  auto-append .vlink when saving as file ! 
        if (VRL.extension(baseName).compareTo(VRS.VLINK_EXTENSION)!=0) 
        {
            baseName=baseName+"."+VRS.VLINK_EXTENSION; 
        }

        // default way to store LinkNode is stored is VFile !
        VNode file=parent.createNode(VFS.FILE_TYPE,baseName,true);

        // not all VFile are StreamWritable: 
        if ((file instanceof VStreamWritable)==false)
        {
            throw new ResourceTypeMismatchException("Created storage node does not support write methods:"+file);
        }

        // update implementation object +location ! 
        this.storageNode=file;
        doSave(); //actual save  
    }
    
    public void dispose()
    {
        // cleanup/unlink ! 
        this.resourceAttributes.clear(); 
        this.resourceAttributes=null; 
        this.setLocation(null); 
        this.parent=null;
        this.storageNode=null; 
    }

    /** Whether configuration attributes are editable */ 
    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }
        
   public VAttributeSet getPersistantAttributes() throws VlException
   {
        return this.resourceAttributes.duplicate(); 
   }

   public String getPersistantType()
   {
        return PERSISTANT_TYPE;  
   }

    public boolean deleteStorageLocation() throws VlException
    {
        if (storageNode!=null)
        {
            if (storageNode instanceof VDeletable)
            {        
                boolean status=((VDeletable)storageNode).delete();
                // clear ! 
                this.storageNode=null;
                return status; 
            }
            else
                throw new ResourceNotEditableException("Description location is not deletable:"+storageNode);
        }
        
        return false; 
    }
    
    
    // =======================================================================
    // Server Config Node <==> ServerInfo Integration 
    // =======================================================================
    
    
    /**
     * When this ResourceNode is a Server Description, call 
     * VRSFactory.updateServerInfo get the (default) Server Attributes. 
     */
    public void initializeServerAttributes()
    {
        try
        {
            VRL serverVRL=this.getTargetLocation(); 
            // Create new ServerInfo: 
            ServerInfo info = this.vrsContext.getServerInfoFor(serverVRL,true);
            // copy ServerAttribute into ResourceDescription ! 
            info=vrsContext.updateServerInfo(info);
            // check already existing configuration !: 
            ServerInfo oldInfo=this.vrsContext.getServerInfoFor(info.getServerVRL(),false); 
            if (oldInfo!=null) 
            	info=oldInfo; 
            	
            storeServerInfo(info); 
 
        }
        catch (VlException e)
        {
            warnPrintln("*** Warning:Couldn't update Server Attribute for:"+this); 
        }
    }
    
    private void storeServerInfo(ServerInfo info)
    {
        info.store(); 
    }


    /** Extra hardcoded checks */ 
    public VAttribute checkAttribute(VAttribute attr) 
    {
        if (attr==null)
            return null;
        
        attr=attr.duplicate(); 
        
        String name=attr.getName();        
        boolean isLink=this.isLinkType(); 
        
        // ===
        // "file:" scheme has has no host+port+user
        // ===
        if (StringUtil.equals(this.getTargetScheme(),VRS.FILE_SCHEME))
        {
            // filter out host+port from "file://" resources. 
            if (attr.getName().compareTo(ATTR_HOSTNAME)==0) 
                return null; 
            else if (attr.getName().compareTo(ATTR_PORT)==0) 
                return null;
            else if (attr.getName().compareTo(ATTR_USERNAME)==0) 
                return null;
        }
        
        boolean editable=attr.isEditable(); 
        
        // ===
        // Set Default Editable attributes : 
        // ===
        if (name.compareTo(ATTR_TARGET_IS_COMPOSITE)==0)
        {
            editable=false;  
        }
        else if (name.compareTo(ATTR_TARGET_MIMETYPE)==0)
        {
            editable=false;  
        }
        else if (name.compareTo(ATTR_TYPE)==0)
        {
            editable=false;    
        }
        else if (name.compareTo(ATTR_SCHEME)==0)
        {
            // Schemes of Stored Servers are NOT editable 
            if (this.isServerConfigType()==true)
            {
                editable=Global.getDebug(); // when in debug mode, make this editable ! 
            }
            else
            {
                // Link Schemes are editable but must be updated 
                // to reflect 'current' registered schemes !
                editable=true;
                String value=attr.getStringValue(); 
                // Create Schemes Enumerate 
                String schemes[]=this.getRegisteredSchemes();
                attr=VAttribute.createEnumerate(name,schemes,value); 
            }

            // scheme is now editable:
            //  editable=false;  
        }
        else
        {
        	if (allAttributeNames.contains(name))
        	{
        		// other Resource Attribute are editable by default:
        		editable=true;
        	}
        }
        
          if (this.isServerConfigType())
        {
             ServerInfo.checkServerAttribute(this.vrsContext,attr);
        }

        if (this.isEditable()==false) 
            attr.setEditable(false); 
        else
            attr.setEditable(editable); 
        
        return attr; 
    }

    /**
     * Check the Server Attributes with the VRS implementation 
     * and store in the ServerInfo registry 
     * @param info 
     */
    protected void updateAndStoreServerInfo(ServerInfo info) throws VlException
    {
        debugPrintln(">>> Storing Server Type Info");
        // synchronize with VRS Implementation and store.   
        info=vrsContext.updateServerInfo(info);
        storeServerInfo(info);  
    } 
   
    public Presentation getPresentation()
    {
        if (presentation!=null)
            return presentation;
        
        // Return Defaults: 
        return Presentation.getPresentationFor(
                getScheme(),
                getHostname(),
                getType(),
                true);
    }
    
    protected void setPresentation(Presentation newPresentation)
    {
        this.presentation=newPresentation; 
    }
    
    private void debugPrintln(String msg)
    {
        logger.debugPrintf("%s\n",msg); 
    }
    
    private void warnPrintln(String msg)
    {
        logger.warnPrintf("%s\n",msg); 
    }
    
    private void errorPrintln(String msg)
    {
        logger.errorPrintf("%s\n",msg); 
    }

}
