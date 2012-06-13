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
 * $Id: ProxyWrapNode.java,v 1.10 2011-04-18 12:27:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:30 $
 */ 
// source: 

package nl.uva.vlet.gui.proxynode.impl.proxy;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICONURL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISCOMPOSITE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISEDITABLE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_RESOURCE_TYPES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SHOW_SHORTCUT_ICON;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TARGET_IS_COMPOSITE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;

import javax.swing.Icon;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.icons.IconProvider;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.view.ViewFilter;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.proxy.vrs.ProxyVRS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.VLogicalResource;
import nl.uva.vlet.vrms.VResourceLink;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VComposite;

public class ProxyWrapNode extends ProxyNode
{
    public class Cache
    {
        String resourcetype=null; 
        String mimetype=null; 
        Boolean isComposite=null; 
        String name=null; 
        ProxyWrapNode childNodes[]=null; 
        String[] attrNames=null;
        public VRL targetVRL;
        public ProxyWrapNode targetPNode;
        VAttributeSet cachedAttrs=new VAttributeSet(); 
     
        public void clear()
        {
            this.resourcetype=null; 
            this.mimetype=null; 
            this.isComposite=null; 
            this.name=null;
            this.childNodes=null;
            this.attrNames=null; 
            this.targetPNode=null; 
            this.cachedAttrs.clear(); 
        }


        public void addAttributes(VAttribute[] attrs)
        {
            if (attrs==null)
                return; 
            for (VAttribute attr:attrs)
            {
                if (attr==null)
                    return;  
                
                cachedAttrs.put(attr); 
                String name=attr.getName();
                String value=attr.getStringValue(); 
                
                //  update direct attributes! 
                if (name.equals(ATTR_MIMETYPE))
                    this.mimetype=value;
                else if (name.equals(ATTR_TYPE)) 
                    this.resourcetype=value; 
                else if (name.equals(ATTR_ISCOMPOSITE))
                    this.isComposite=attr.getBooleanValue(); 
                else if (name.equals(ATTR_NAME))
                    this.name=value;
            }
        }
        
        /** Returns attribute is in cache */ 
        public VAttributeSet getAttributes(String names[])
        {
            return cachedAttrs.getAttributes(names); 
        }
        
    }

    private ProxyWrapNodeFactory factory=null;
    private ProxyVRS invoker=null; 
    protected boolean mustrefresh=false; 
    private Cache cache=new Cache();
    // will Not change during lifetime of the object! 
    private VRL vrl=null; 
    private Class<?> vrsClass=null; 
    
    public ProxyWrapNode(ProxyWrapNodeFactory factory, VRL location) throws VlException
    {
        this.factory=factory;
        this.invoker=factory.getInvoker(); 
        this.vrl=location; 
        open(); 
    }

    protected void open() throws VlException
    {
        String className;
        try
        {
            className = this.invoker.getClass(vrl);
            if (className==null)
                throw new VlInternalError("Couldn't get VRS Class of:"+vrl);
            
            this.vrsClass=this.getClass().getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new VlInternalError("Remote Class is not compatible (Class not found!)",e); 
        }
    }
    
    @Override
    public ProxyNode create(String resourceType, String newName)
            throws VlException
    {
        VRL newVrl=this.invoker.create(vrl,resourceType,newName,false); 
        this.fireChildAdded(vrl,newVrl); 
        return this.factory.openLocation(newVrl,false); 
    }

    @Override
    public VAttribute[] createACLRecord(VAttribute entityAttr,
            boolean writeThrough) throws VlException
    {
        fixme("createACLRecord():"+vrl);
        return null;
    }

    @Override
    public void createLinkTo(ProxyNode pnode) throws VlException
    {
        fixme("createLinkTo():"+vrl);
    }

    @Override
    public boolean delete(boolean compositeDelete) throws VlException
    {
        this.invoker.delete(vrl, compositeDelete); 
        // no error -> ok. 
        return true; 
    }

    @Override
    public VAttribute[][] getACL() throws VlException
    {
        return this.invoker.getACL(vrl); 
    }

    @Override
    public VAttribute[] getACLEntities() throws VlException
    {   
        return this.invoker.getACLEntities(vrl); 
    }

    @Override
    public String[] getAttributeNames()
    {
        if (this.cache.attrNames!=null)
            return this.cache.attrNames; 
        
        try
        {
            this.cache.attrNames=this.invoker.getAttributeNames(vrl);
        }
        catch (VlException e)
        {
            fixme("getAttributesNames:"+e);
            fixme(e); 
        } 
        return this.cache.attrNames; 
    }

    @Override
    public VAttribute[] getAttributes(String[] attrNames) throws VlException
    {
        debug("getAttributes:"+vrl);
    
        if (attrNames==null)
            return null; 
        
        VAttributeSet cachedSet = this.cache.getAttributes(attrNames); 
        StringList notCached=new StringList(); 
        for (String attr:attrNames)
            if (cachedSet.containsKey(attr)==false)
                notCached.add(attr); 
 
        if (notCached.size()>0)
        {
            VAttribute[] newAttrs = this.invoker.getAttributes(vrl, notCached.toArray());
            this.cache.addAttributes(newAttrs); 
            cachedSet.add(newAttrs);
        }
        
        return cachedSet.toArray(); 
    }
   
    public VAttribute getAttribute(String name) throws VlException
    {
        debug("getAttribute '"+name+"':"+vrl);
        
        if (name==null)
            return null; 
        
        VAttribute attrs[]=this.getAttributes(new String[]{name});
        if ((attrs==null) || (attrs.length<=0)) 
            return null;
        
        return attrs[0]; 
    }
    
    public boolean getBoolAttribute(String name, boolean defaultVal) throws VlException
    {
        debug("getBoolAttribute '"+name+"':"+vrl);
        
        if (name==null)
            return defaultVal; 
        
        VAttribute attrs[]=this.getAttributes(new String[]{name});
        if ((attrs==null) || (attrs.length<=0)) 
            return defaultVal;
        
        return attrs[0].getBooleanValue(); 
    }
    
    Object getChildMutex=new Object(); 
    
    @Override
    public ProxyWrapNode[] getChilds(ViewFilter filter) throws VlException
    {
        synchronized(getChildMutex)
        {
            if (this.cache.childNodes!=null)
                return cache.childNodes; 
            
           VRL vrls[]=this.invoker.getChildVRLs(vrl); 
           this.cache.childNodes=this.factory.createNodes(vrls); 
           return this.cache.childNodes; 
        }
    }

    @Override
    public Icon getDefaultIcon(int size, boolean isSelected)
    {
        IconProvider provider=UIGlobal.getIconProvider();
        
        String iconUrl=null; 
        
        try
        {
            iconUrl=this.getIconURL();
        }
        catch (VlException e)
        {
            warn("Could not get custom icon url for:"+vrl); 
        } 
        
        boolean isLink=isResourceLink(); 
        
        if (isLink)
        {
            try
            {
                VAttribute attr = this.getAttribute(ATTR_SHOW_SHORTCUT_ICON);
                if ((attr!=null) && (attr.getBooleanValue()==false))
                    isLink=false; 
            }
            catch (VlException e)
            {
                warn("Couldn't get attribute"+ATTR_SHOW_SHORTCUT_ICON+"of"+vrl);
            } 
        }
        
        return provider.createDefaultIcon(iconUrl,
                this.isComposite(),
                isLink,
                this.getMimeType(),
                size,
                isSelected); 
    }

    @Override
    public String getIconURL() throws VlException
    {
        VAttribute attr=this.getAttribute(ATTR_ICONURL);
        if (attr==null)
            return null; 

        return attr.getStringValue(); 
    }

    @Override
    public String getMimeType()
    {
        if (this.cache.mimetype!=null)
            return this.cache.mimetype;
            
        try
        {
            this.cache.mimetype=invoker.getMimetype(vrl);
            return this.cache.mimetype; 
        }
        catch (Exception e)
        {
            fixme("getMimeType():"+e); 
            fixme(e); 
        }
        
        // for now mimetypes MUST succeed: 
        return UIGlobal.getMimeTypes().getMimeType(this.getVRL().getPath());
    }

    
    @Override
    public String getName()
    {
        if (this.cache.name!=null)
            return this.cache.name; 
        
        try
        {
            this.cache.name=invoker.getName(vrl);
            return this.cache.name; 
        }
        catch (VlException e)
        {
            error("Couldn't get name of:"+vrl,e); 
        }
        return getVRL().getBasename(); 
    }


//    @Override
//    public int getNrOfChilds(ViewFilter filter) throws VlException
//    {
//        ProxyWrapNode[] nodes = this.getChilds(filter);
//        
//        if (nodes==null)
//            return 0;
//        
//        return nodes.length; 
//    }

    @Override
    public ProxyNode getParent() throws VlException
    {
        return this.factory.openLocation(this.getParentVRL());
    }

    public VRL getParentVRL() throws VlException
    {   
        return this.invoker.getParentVRL(vrl); 
    }

    @Override
    public Presentation getPresentation()
    {
        fixme("getPresentation():"+vrl);
        return Presentation.getPresentationFor(vrl, getType(),true); 
    }

    @Override
    public String[] getResourceTypes() 
    {
        VAttribute attr;
        try
        {
            attr = this.getAttribute(ATTR_RESOURCE_TYPES);
            if (attr==null) 
                return null;
            // use StringList from attr? 
            return new StringList(attr.getStringValue().split(",")).toArray();
        }
        catch (VlException e)
        {
            fixme(e); 
        } 
        
        return null; 
    }

    @Override
    public ProxyWrapNode getTargetPNode() throws VlException
    {
        if (this.cache.targetPNode!=null)
            return this.cache.targetPNode; 
        
        VRL vrl=this.getTargetVRL();
        if (vrl==null)
            return null; 
        this.cache.targetPNode=this.factory.getOrCreateNode(vrl);
        return cache.targetPNode; 
    }

    @Override
    public VRL getTargetVRL() throws VlException
    {
        if (isResourceLink()==false)
            return null; 
        
        if (this.cache.targetVRL!=null)
            return this.cache.targetVRL; 
        
        this.cache.targetVRL=invoker.resolveLink(vrl);
        return this.cache.targetVRL; 
    }

    @Override
    public String getType()
    {
        debug("isType:"+vrl); 
        
        if (this.cache.resourcetype!=null)
            return this.cache.resourcetype; 
           
        try
        {
            this.cache.resourcetype=invoker.getResourceType(vrl);
            return this.cache.resourcetype; 
        }
        catch (VlException e)
        {
            fixme("getType(): Handle Exception:"+e);
            fixme(e);  
            return "?"; 
        } 
    }

    private void fixme(String msg)
    {
        Global.errorPrintf(this,"FIXME:"+msg); 
    }
    
    private void debug(String msg)
    {
        Global.errorPrintf(this,"%\n",msg); 
    }
    
    private void fixme(Exception e)
    {
        Global.errorPrintStacktrace(e);  
    }

    @Override
    public VRL getVRL()
    {
        return this.vrl; 
    }

    @Override
    public boolean isBusy()
    {
        return false;
    }

    @Override
    public boolean isComposite(boolean resolve)
    {
        debug("isComposite:"+vrl); 
        try
        {
            if ((resolve==false) || this.isResourceLink()==false)
            {
                if (this.cache.isComposite!=null)
                {
                    return this.cache.isComposite;
                }
                else
                {
                    this.cache.isComposite=this.instanceOf(VComposite.class); 
                    return this.cache.isComposite;
                }
            }
            else
            {
                VAttribute attr=this.getAttribute(ATTR_TARGET_IS_COMPOSITE); 
                if (attr==null)
                    return false; 
                return attr.getBooleanValue(); 
            }
        }
        catch (VlException e)
        {
            fixme("isComposite():"+e);
            fixme(e); 
            return false;  
        }
 
    }

    @Override
    public boolean isEditable() throws VlException
    {
        return getBoolAttribute(ATTR_ISEDITABLE,true); 
    }

    @Override
    public boolean isLogicalNode() throws VlException
    {
        return this.instanceOf(VLogicalResource.class); 
    }

    public boolean instanceOf(Class<?> className) 
    {
        if ((this.vrsClass==null) || (className==null)) 
            return false;
        
        return className.isAssignableFrom(this.vrsClass);
    }

    @Override
    public boolean isResourceLink() 
    {
        return this.instanceOf(VResourceLink.class); 
    }

//    @Override
//    public boolean locationEquals(VRL loc, boolean checkLinkTargets)
//    {
//        if (this.vrl.equals(loc))
//            return true; 
//        
//        try
//        {
//            if (checkLinkTargets==true)
//            {
//                if (this.isResourceLink())
//                {
//                    VRL target=this.getTargetVRL(); 
//                    if (target!=null)
//                        return target.equals(loc); 
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            fixme("locationEquals:"+e); 
//            return false; 
//        }
//        
//        debug("Locations do NOT match:"+vrl+","+loc);
//        return false; 
//    }

    @Override
    public void refresh()
    {
         this.cache.clear();
         
         fireGlobalEvent(ResourceEvent.createRefreshEvent(vrl)); 
    }

    @Override
    public VRL renameTo(String name, boolean nameIsPath) throws VlException
    {
        VRL newVRL=this.invoker.rename(vrl,name,nameIsPath);
        this.fireRenameEvent(vrl,newVRL,VRL.basename(name));
        return newVRL; 
    }

    @Override
    public void setACL(VAttribute[][] acl) throws VlException
    {
        invoker.setACL(vrl,acl); 
    }

    @Override
    public void setAttributes(VAttribute[] attrs, boolean refresh)
            throws VlException
    {
        this.invoker.setAttributes(vrl,attrs);
        
        if (refresh)
            refresh(); 
    }

    public void clearCache()
    {
        this.cache.clear(); 
    }

    public void prefetchAttributes()
    {
        // try to prefetch, return silently if failed!
        String attrNames[]=
                {
                    ATTR_MIMETYPE,
                    ATTR_TYPE,
                    ATTR_ISCOMPOSITE,
                    ATTR_NAME,
                    ATTR_RESOURCE_TYPES
                }; 
        
        try
        {
            VAttribute attrs[]=this.invoker.getAttributes(vrl,attrNames);
            this.cache.addAttributes(attrs); 
        }
        catch(Exception e)
        {
            warn("Couldn't prefetch attribute for:"+vrl);
            warn("Exception ="+e); 
        }
        
    }

    
    public void dispose()
    {
        this.cache.clear(); 
    }

    // ===================//
    // === Error/Fixme ===//
    // ===================//
    
    private void warn(String msg)
    {
        Global.errorPrintf(this,"%s\n",msg); 
    }
      
    private void error(String msg, Exception e)
    {
        Global.logException(ClassLogger.ERROR,this,e,"%s\n",msg); 
    }

    public VRL[] getAliasVRLs()
    {
        return new VRL[]{getVRL()};
    }

}
