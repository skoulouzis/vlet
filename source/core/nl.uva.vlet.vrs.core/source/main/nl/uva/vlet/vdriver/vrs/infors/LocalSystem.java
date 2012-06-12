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
 * $Id: LocalSystem.java,v 1.1 2011-11-25 13:40:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:48 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors;

import java.io.File;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.net.NetworkNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class LocalSystem extends CompositeServiceInfoNode<VNode> implements VEditable
{
	
	public LocalSystem(VRSContext context)
	{
		super(context, new VRL(InfoConstants.INFO_SCHEME,null,"/"+InfoConstants.LOCALSYSTEM_TYPE));
		
		try
        {
            initChilds();
        }
        catch (VlException e)
        {
            Global.logException(ClassLogger.ERROR,this,e,"LocalSystemNode:Failed to initialize childs:"); 
        } 
		
		//	serverInstances=new ServerInstanceGroup(vrsContext); 
		//gridServices=new LoVOGroupsNode(this,vrsContext);       
		//VNode nodes[]=new VNode[1];
	        
		//nodes[0]=gridServices; 
		//nodes[1]=serverInstances;
	        
		// update super class internal array! 
		//setChilds(nodes); 

		this.setEditable(true); 
	} 

    @Override
    public String getType()
    {
        return InfoConstants.LOCALSYSTEM_TYPE; 
    }
    
    @Override
    public String getName()
    {
        return InfoConstants.LOCALSYSTEM_NAME;  
    }

	public String getIconURL()
	{
	    return "system-128.png"; 
	}
	
	public String getMimeType(){return null;} 
	

	public String[] getResourceTypes()
	{
	      return null; 
	}

	public VRL getDescriptionLocation() throws VlException
	{
		return null; 
	}

    public VRL createChildVRL(String childName) 
    {
        return new VRL(this.getScheme(),null,null,-1,getPath()+"/"+childName);
        
    }

    public VNode createNode(String type, String name, boolean force)
        throws VlException
    {
        if (StringUtil.equals(type,InfoConstants.NETWORK_INFO))
        {
            return createNetworkNode(name); 
        }
        
        throw new nl.uva.vlet.exception.ResourceTypeNotSupportedException("Cannot create resource of type:"+type);
    }

    private NetworkNode createNetworkNode(String name) throws VlException
    {
        if ((name==null) || (name.equals("")))
            name="New Network"; 
        
        VRL childVRL=this.createChildVRL(name);
        NetworkNode node=new NetworkNode(this.getVRSContext(),childVRL);
        // add to internal vector 
        this.addSubNode(node); 
        return node; 
        
    }
    
    private void initChilds() throws VlException
    {
    	this.clearChilds(); 
    	
     // start with userHome:
        addPathNode(vrsContext.getUserHomeLocation(),"home","default/home_folder.png");
        // debugging:
        if (Global.getLogger().isLevelDebug()) 
            addPathNode(vrsContext.getConfigManager().getUserConfigDir(),"config","config-folder-48.png");
        addFilesystemRoots(); 
    }

    private void addPathNode(VRL targetPath,String logicalname,String iconUrl) throws VlException
    {
        VRL childVRL=this.createChildVRL(logicalname);
        LinkNode lnode = LinkNode.createLinkNode(vrsContext,childVRL,targetPath); 
        lnode.setName(targetPath.getPath()); 
        // do not show the shortcut image
        lnode.setShowShortCutIcon(false); 
        lnode.setLogicalParent(this); 

        lnode.setIconURL(iconUrl);
        // hardcoded node:
        lnode.setEditable(false); 
        this.addSubNode(lnode); 
    }

    /** Scan filesystem root and add them to the rootNodes */ 
    private void addFilesystemRoots() throws VlException
    {
        File roots[]=null; 

        // for windows this method returns the drives: 
        if (Global.isWindows()==true)
        {
            // alt get drives to avoid annoying pop-up
            roots=Global.getWindowsDrives();
        }
        else 
        {
            // should trigger initialize Security Context
            @SuppressWarnings("unused")
            SecurityManager sm = System.getSecurityManager();
            // disable ? 
            System.setSecurityManager(null);
            roots=java.io.File.listRoots();
        }

        VRL targetLoc;
        LinkNode lnode;
      
        for (int i=0;i<roots.length;i++)
        {
            if (roots[i].exists())
            {
                String path=roots[i].getAbsolutePath(); 
                targetLoc=new VRL("file",null,path);
                //linkPath=this.getLocation().addPath(""+index++); 
                
                VRL childVRL=this.createChildVRL("fs:"+i); 
                
                lnode=LinkNode.createLinkNode(vrsContext,childVRL,targetLoc); 
                lnode.setName("localhost:"+targetLoc.getPath());
                // do not show the shortcut image
                lnode.setShowShortCutIcon(false); 
                lnode.setIconURL("hdd_mount.png");
                lnode.setLogicalParent(this);
                
                // hard coded node: 
                lnode.setEditable(false); 
                this.addSubNode(lnode);    
            }
        }
        
    }
    
    
    public String[] getAttributeNames()
    {
        StringList list=new StringList(super.getAttributeNames());
        list.remove(VAttributeConstants.ATTR_MIMETYPE); 
        list.add(InfoConstants.ATTR_SYSTEMHOSTNAME);
        list.add(InfoConstants.ATTR_SYSTEMOS);
        list.add(InfoConstants.ATTR_JAVAVERSION);
        list.add(InfoConstants.ATTR_JAVAHOME);
        
        if (Global.isWindows())
            list.add(GlobalConfig.PROP_SKIP_FLOPPY_SCAN);
        
        return list.toArray(); 
    }
    
    public VAttribute getAttribute(String name) throws VlException
    {
    	ConfigManager cmgr = this.vrsContext.getConfigManager(); 
    	VAttribute attr=null; 
    	
        if (name.equals(InfoConstants.ATTR_SYSTEMHOSTNAME))
        {
            attr=new VAttribute(name,Global.getHostname());
        }
        else if (name.equals(InfoConstants.ATTR_SYSTEMOS))
        {
        	attr=new VAttribute(name,Global.getOSName()+" "+Global.getOSVersion());
        }
        else if (name.equals(InfoConstants.ATTR_JAVAVERSION))
        {
        	attr=new VAttribute(name,Global.getJavaVersion()); 
        }
        else if (name.equals(InfoConstants.ATTR_JAVAHOME))
        {
        	attr=new VAttribute(name,Global.getJavaHome()); 
        }
        else if (name.equals(GlobalConfig.PROP_SKIP_FLOPPY_SCAN))
        {
        	attr=cmgr.getAttribute(name);
        	attr.setEditable(true); 
        	
        }
        
        if (attr!=null)
        	return attr; 

        return super.getAttribute(name); 
    }
    
    public boolean setAttribute(VAttribute attr) throws VlException
	{
		if (attr==null)
			return false;

		String name=attr.getName();
		
		if (name==null) 
			return false;
		
		ConfigManager manager=vrsContext.getConfigManager();
		boolean result=false;
		BooleanHolder refreshH=new BooleanHolder(false); 
		
		if (name.equals(GlobalConfig.PROP_SKIP_FLOPPY_SCAN))
        {
			// check Configuration attributes: 
			result=manager.setAttribute(attr,refreshH);
			refreshH.value=true; 
        }
		
		if (refreshH.value)
			refresh(); 
		
		return result; 
	}

	private void refresh()
	{
		try 
		{
			this.initChilds();
		}
		catch (VlException e) 
		{
			Global.logException(ClassLogger.ERROR,this,e,"refresh(): got exception\n"); 
		} 
		this.fireRefresh(); 
	}
    
}

