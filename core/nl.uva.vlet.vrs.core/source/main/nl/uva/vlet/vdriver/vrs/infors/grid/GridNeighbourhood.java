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
 * $Id: GridNeighbourhood.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.grid;

import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.CompositeServiceInfoNode;
import nl.uva.vlet.vdriver.vrs.infors.InfoConstants;
import nl.uva.vlet.vdriver.vrs.infors.net.NetworkNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class GridNeighbourhood extends CompositeServiceInfoNode 
    implements VEditable // editable methods already defined in super class hierarchy  
{
    // === // 
    
    private VOGroupsNode gridServices=null;
    
    // === // 
    
	public GridNeighbourhood(VRSContext context)
	{
		super(context, new VRL(InfoConstants.INFO_SCHEME,null,"/"+InfoConstants.GRID_NEIGHBOURHOOD_TYPE));
		initChilds(); 
	}
	
	private void initChilds()
	{
		//	serverInstances=new ServerInstanceGroup(vrsContext); 
		gridServices=new VOGroupsNode(this,vrsContext);       
		VNode nodes[]=new VNode[1];
	        
		nodes[0]=gridServices; 
		//nodes[1]=serverInstances;
	        
		// update super class internal array! 
		setChilds(nodes);

        this.setEditable(true); 

	} 

	@Override
    public String getName()
    {
        return InfoConstants.GRID_NEIGHBOURHOOD_NAME;  
    }
	
	@Override
	public String getType()
	{
		return InfoConstants.GRID_NEIGHBOURHOOD_TYPE;
	}

	public String getIconURL()
	{
	    return "world-128.png"; 
	}
	
	public String getMimeType(){return null;} 
	

	public String[] getResourceTypes()
	{
	      return new String[] { InfoConstants.NETWORK_INFO}; 
	}

	public String[] getAttributeNames()
    {
        StringList list = new StringList(); 
        list.add(GlobalConfig.PROP_BDII_HOSTNAME);
        list.add(GlobalConfig.PROP_BDII_PORT);
        return list.toArray();
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        ConfigManager cmgr = this.vrsContext.getConfigManager(); 
        
        VAttribute attr=null; 
        
        if (name.equals(GlobalConfig.PROP_BDII_HOSTNAME))
        {
            // As of vlet 1.4 return comma seperated host:port list! 
            attr=new VAttribute(name, cmgr.getBdiiHostInfo());
            attr.setEditable(true); 
        }
        else if (name.equals(GlobalConfig.PROP_BDII_PORT))
        {
            attr=new VAttribute(name, cmgr.getBdiiServiceURI().getPort());
            attr.setEditable(true);
        }
        
        if (attr!=null)
            return attr;
        
        return super.getAttribute(name);
    }

    public boolean setAttribute(VAttribute attr) throws VlException
    {
        ConfigManager cmgr = this.vrsContext.getConfigManager(); 
        
        String name=attr.getName();
        
        BooleanHolder mustRefresh=new BooleanHolder(false);  
        boolean result=false; 
        
        if (name.equals(GlobalConfig.PROP_BDII_HOSTNAME))
        {
            cmgr.setAttribute(attr, mustRefresh);
            result=true; 
        }
        else if (name.equals(GlobalConfig.PROP_BDII_PORT))
        {
            cmgr.setAttribute(attr, mustRefresh);
            result=true; 
        }
        
        if (mustRefresh.value)
            refresh(); 
        
        return result;  
    }
    
    protected void refresh()
    {
        initChilds(); 
        this.fireRefresh();
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
}
