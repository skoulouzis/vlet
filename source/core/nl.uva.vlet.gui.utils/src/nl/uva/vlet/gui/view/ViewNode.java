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
 * $Id: ViewNode.java,v 1.1 2011-06-10 10:18:03 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:03 $
 */ 
// source: 

package nl.uva.vlet.gui.view;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;

import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.vrl.VRL;

/**
 * Presents a Viewed Resource Node.  
 * This Resource Node can either be a single Icon or a row in table view. 
 * This is just a container class to hold (cached) GUI properties for a 'Viewed' Resource.  
 */
public class ViewNode
{
	// === Class Constants  ===
	public static final int DEFAULT_ICON_SIZE=48; 
	
	public static final String DEFAULT_ICON="defaultIcon"; 
	
	public static final String SELECTED_ICON="selectedIcon"; 
    
	// =======================================================================
    // Instance
    // =======================================================================

	private VRL vrl=null; 
	
	private String resourceType=null;
	
	private String mimeType=null; 
	
	private String name=null;
	
    private boolean isComposite=true;
    
    //private IProxyModel proxyModel;
    
    private boolean isBusy=false; 
    
    // Optional resolved Taget VRL (if it has one) 
    //private VRL aliasVrl=null;  
    
    private Map<String,Icon> iconMapping=new Hashtable<String,Icon>();

    private int iconSize; 
   
    private VRL targetVrl=null;

    private boolean isResourceLink=false;
	
    public ViewNode(VRL vrl,String type,boolean isComposite)
    {
        this.vrl=vrl;
        this.isComposite=isComposite;
        this.resourceType=type; 
    }
    
    public void updateIconURL(String iconUrl)
    {
        // Update icon. bypass ProxyNode: 
        Icon defIcon = UIGlobal.getIconProvider().renderIcon(null,
                iconUrl,false,getIconSize(),false);  
        
        Icon selIcon = UIGlobal.getIconProvider().renderIcon(null,
                iconUrl,false,getIconSize(),true); 
   
        if ((defIcon!=null) && (selIcon!=null))
        {
            setIcon(ViewNode.DEFAULT_ICON,defIcon); 
            setIcon(ViewNode.SELECTED_ICON,selIcon);
        }
    }
    
    public VRL getVRL()
    {
        return vrl;  
    }
    
    public String getName()
    {
        if (name!=null)
            return name; 
        
        // default will change: 
        
        return getVRL().toString();  
    }

    public void setName(String nameval)
    {
        this.name=nameval; 
    }
    
    public String getResourceType()
    {
        return this.resourceType; 
    }

    public ResourceRef getResourceRef()
    {
        return new ResourceRef(vrl,resourceType,mimeType); 
    }

    public String getMimeType()
    {
        return mimeType; 
    }

    public boolean isComposite()
    {
        return isComposite; 
    }
    
    public boolean isBusy()
    {
        return isBusy; 
    }
    
    public void setBusy(boolean val)
    {
        this.isBusy=val; 
    }
	
	public Icon getIcon(String name)
	{
		return this.iconMapping.get(name); 
	}
	
	public void setIcon(String iconName, Icon icon)
	{
		if ((iconName==null) || (icon==null))
			return; 
		this.iconMapping.put(iconName,icon); 
	}

	public Icon getSelectedIcon()
	{
		return iconMapping.get(SELECTED_ICON);
	}
	
	public Icon getDefaultIcon()
	{
		return iconMapping.get(DEFAULT_ICON);
	}
	
	/** Only if it has one: VLink or LogicalResourceLink */ 
	public VRL getTargetVRL()
	{
	    return this.getTargetVrl(); 
	}
	
	/** Only if it has one: VLink or LogicalResourceLink */ 
    public boolean isResourceLink()
    {
        return this.isResourceLink;
    }
    
	public boolean hasIcon(String name)
	{
		return this.iconMapping.containsKey(name);
	}
	
	public String toString()
	{
		return "<ViewItem>{"+getVRL()+",["
		    +(isBusy()?"B":"-")
		    +(isComposite()?"C":"-")
		    +"]}";	
	}

    public boolean equalsLocation(VRL loc, boolean checkLinkTarget)
    {
        if (loc==null)
            return false;
        
        if (loc.equals(getVRL()))
            return true; 
        
        if ((checkLinkTarget) && (this.getTargetVrl()!=null) ) 
            return loc.equals(this.getTargetVrl());
        
        return false; 
    }

    public void setComposite(boolean isComposite)
    {
        this.isComposite = isComposite;
    }

    public void setResourceLink(boolean isResourceLink)
    {
        this.isResourceLink = isResourceLink;
    }

    public void setIconSize(int iconSize)
    {
        this.iconSize = iconSize;
    }

    private int getIconSize()
    {
        return iconSize;
    }

    public void setTargetVrl(VRL targetVrl)
    {
        this.targetVrl = targetVrl;
    }

    private VRL getTargetVrl()
    {
        return targetVrl;
    }

    public void setMimeType(String mtype)
    {
        this.mimeType=mtype; 
    }

   
}
