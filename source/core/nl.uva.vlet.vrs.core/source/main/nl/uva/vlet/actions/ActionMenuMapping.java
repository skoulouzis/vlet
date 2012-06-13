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
 * $Id: ActionMenuMapping.java,v 1.4 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.actions;

import static nl.uva.vlet.actions.ActionMenuConstants.MENU_INACTIVE_DONT_SHOW;
import static nl.uva.vlet.actions.ActionMenuConstants.MENU_ITEM_ACTION;

import java.util.Vector;
import java.util.regex.Pattern;

import nl.uva.vlet.vrs.VRSFactory;

/** 
 * Action Menu Mapping class.
 *  
 * Used by the VBrowser to dynamically determine which 'action' may 
 * apear on the Action Menu/Canvas Menu. <br>
 * When a right mouse button click is performed (or <i>alt</i> mouse button click) ON 
 * an item, the Item Menu will appear. 
 * A right mouse button click (or <i>alt</i> mouse button click) on the canvas
 * of a composite node (directory) will show the Canvas Menu.<br>
 *  
 * @author P.T. de Boer 
 */
public class ActionMenuMapping
{
	public static final int DEFAULT_MENU_OPTIONS=MENU_INACTIVE_DONT_SHOW&MENU_ITEM_ACTION;

	// =======================================================================
	// 
	// =======================================================================
	
	/** Action use as action parameter */ 
	private String actionName=null; 
	
	/** Action to show up in menu, if NULL, use actionName */ 
	private String actionMenuName=null; 
	
	/** Action Sub menu Name (or menu toplevel name) */ 
	private String actionSubMenuName=null; 
	
	/** VRS which provides this method */ 
	
	VRSFactory vrs=null; 
	
	/**
	 * Action Patterns which allows this action. Action is matched if any 
	 * of the resourcePatterns match. 
 	 */  
 	private Vector<ResourceMappingPatterns> resourcePatterns=new Vector<ResourceMappingPatterns>();
 			
	private int menuOptions=DEFAULT_MENU_OPTIONS;
	
	private String iviewerClassName;

	private boolean isVRSAction;

	private boolean isViewerAction;
	
	/** Action which will be invoked on the VRS */ 
	public String getActionName() { return actionName; } 
	
	/** Action name that will appear in the action menu */ 
	public String getMenuItemName() { return actionMenuName; }
	
	/** Get action submenu. If null no submenu is specified */ 
	public String getSubMenuName() { return actionSubMenuName; } 

	/** Menu option used by the VBrowser */ 
	public int getMenuOptions(){return menuOptions;} 
	
	
	/** 
	 * Create Action MenuMapping.
	 * 
	 * @param actionName action name which will be supplied when invoking VRS.doAction()
	 * @param actionMenuItemName the name to appear in the action menu
	 * @param options Menu Options 
	 */ 
	public ActionMenuMapping(String actionName,String actionMenuItemName,int options)
	{
		init(actionName,actionMenuItemName,null,options); 
	}
	
	/** 
	 * Create Action MenuMapping.
	 * 
	 * @param actionName action name which will be supplied when invoking VRS.doAction()
	 * @param actionMenuItemName the name to appear in the action menu
	 * @param subMenuName optional submenu name. 
	 * @param options Menu Options 
	 */ 
	public ActionMenuMapping(String actionName,String actionMenuItemName,String subMenuName,int options)
	{
		init(actionName,actionMenuItemName,subMenuName,options); 
	}
	
	/** 
	 * Create Action MenuMapping.
	 * 
	 * @param actionName action name which will be supplied when invoking VRS.doAction()
	 * @param actionMenuItemName the name to appear in the action menu
	 */ 
	public ActionMenuMapping(String actionName,String actionMenuItemName)
	{
		init(actionName,actionMenuItemName,null,DEFAULT_MENU_OPTIONS); 
	}
	
	public ActionMenuMapping(String actionName,String actionMenuItemName,String subMenuName)
	{
		init(actionName,actionMenuItemName,subMenuName,DEFAULT_MENU_OPTIONS); 
	}
	
   /*	public void setMenuSettings(String actionName,String actionMenuName,int options)
	{
		init(actionName,actionMenuName,null,options); 
	}
	
	public void setMenuSettings(String actionName,String actionMenuName,String subMenuName,int options)
	{
		init(actionName,actionMenuName,subMenuName,options); 
	}*/
	

	private void init(String actionName,String actionMenuItemName,String subMenuName,int options)
	{
		this.actionName=actionName;
		this.actionMenuName=actionMenuItemName; 
		this.actionSubMenuName=subMenuName; 
		this.menuOptions=options;
	}
	
	public String toString()
	{
		return actionSubMenuName+"/"+actionMenuName+":"+actionName+"()";
	}
	
	public Vector<ResourceMappingPatterns> getResourceTypeSchemePatterns()
	{
		return this.resourcePatterns; 
	}
	
	/**
	 * By setting this value, the action menu item will appear in a submenu
	 * named by this name. 
	 * The VBrowser will automatically create the named submenu and add all
	 * ActionMappings hich have the same submenu name merging all
	 * actions into the same (named) submenu.  
	 */
	public void setSubMenuName(String subMenuName)
	{
		this.actionSubMenuName=subMenuName; 
	}

	public void addTypeMapping(Pattern type)
	{
		Pattern types[]=null;
		
		if (type!=null)
		{
			types=new Pattern[1]; 
			types[0]=type;
		}
		
		this.addResourceMapping(types,null,null,null,0);
	}
	
	public void addMimeTypeMapping(Pattern mimeTypes[])
	{
		this.addMimeTypeResourceMapping(mimeTypes,null,0);
	}
	
	public void addMimeTypeMapping(Pattern mimeType)
	{
		Pattern mimeTypes[]=null;
		
		if (mimeType!=null)
		{
			mimeTypes=new Pattern[1]; 
			mimeTypes[0]=mimeType;
		}
		
		this.addMimeTypeResourceMapping(mimeTypes,null,0);
	}
	
	public void addTypeMapping(Pattern types[])
	{
		this.addResourceMapping(types,null,null,null,0);
	}
	
	/**
	 * Add Mapping for the speficied Resource Type.
	 * If the selected resource has the specified type, this 
	 * menu mapping will be activated.  
	 */  
	public void addTypeMapping(String type)
	{
		String types[]=null;
		if (type!=null)
		{
			types=new String[1]; 
			types[0]=type;
		}
		
		this.resourcePatterns.add(new ResourceMappingPatterns(this,types,null,null,null,ActionMenuConstants.SELECTION_DONT_CARE)); 
	}
	
	/**
	 *@see #addSelectionTypeMapping(Pattern) 
	 */
	public void addSelectionTypeMapping(String type)
	{
		String types[]=null;
		
		if (type!=null)
		{
			types=new String[1]; 
			types[0]=type;
		}
		
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,null,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
    /**
     * Add Selection + Type mapping. 
     * IF the VBrowser has an active selection which contains 
     * the specified type, this menu mapping will be activated. 
     * 
     * @param type VRS Type as RE Pattern the selected resource must have. 
     */
	public void addSelectionTypeMapping(Pattern type)
	{
		Pattern types[]=null;
		
		if (type!=null)
		{
			types=new Pattern[1]; 
			types[0]=type;
		}
		
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,null,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	public void addSelectionTypeMapping(Pattern types[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,null,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	public void addSelectionTypeSchemeMapping(String type,String scheme)
	{
		String types[]=new String[1]; 
		types[0]=type;
		
		String schemes[]=new String[1]; 
		schemes[0]=scheme; 
				
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,null,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	public void addSelectionTypeMapping(String types[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,null,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	public void addSelectionTypeSchemeMapping(String types[],String schemes[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,schemes,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	public void addSelectionTypeSchemeMapping(String types[],String schemes[],int selectionOption)
    {
        this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,schemes,selectionOption));  
    }
	
	public void addSelectionTypeSchemeMapping(Pattern types[],Pattern schemes[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,null,null,types,schemes,ActionMenuConstants.SELECTION_ONE_OR_MORE)); 
	}
	
	/** 
	 * Add Action Menu Mapping for the specifed resource context. 
	 * The 'source' is the node clicked on, and the 'selections' are 
	 * optional nodes selected by the VBrowser or resources in the ClipBoard (copy&paste).
	 *   
	 * @param sourceTypes      selected resource must have one of these types.
	 *                          If null all types are matched (Don't care). 
	 * @param sourceSchemes    selected resource must have on of these schemes. 
	 *                          If null all schemes are matched (Don't care). 
	 * @param selectionTypes   allowed types which may occur in a VBrowser "selection". 
	 *                          Currently all selected resources must has a type specified
	 *                          by this parameter. Leave to null if types don't matter. 
	 * @param selectionSchemes allowed schemes in the current selection. Leave null for don't care
	 * @param selectionMode    extra condition and settings about the selection mode.                    	
	 */
	
	public void addResourceMapping(String sourceTypes[],String sourceSchemes[],
			String selectionTypes[],String selectionSchemes[],int selectionMode)
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,sourceTypes,sourceSchemes,selectionTypes,selectionSchemes,selectionMode)); 
	}

	
	public void addResourceMapping(Pattern sourceTypes[],Pattern sourceSchemes[],
			Pattern selectionTypes[],Pattern selectionSchemes[],int selectionMode)
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,sourceTypes,sourceSchemes,selectionTypes,selectionSchemes,selectionMode)); 
	}
	
	public void addMimeTypeResourceMapping(Pattern sourceMimeTypes[],Pattern selectionMimeTypes[],int selectionMode)
	{
		this.resourcePatterns.add(ResourceMappingPatterns.createMimeTypeMappings(this,sourceMimeTypes,selectionMimeTypes,selectionMode)); 
	}
	
	/** 
	 * Add Mapping for the specified resource Type+Scheme. 
	 * If the resource has the specified type and scheme, this 
	 * menu mapping will be activated.  
	 */ 
	public void addTypeSchemeMapping(String type,String scheme)
	{
		String types[]=new String[1]; 
		types[0]=type;
		String schemes[]=new String[1]; 
		schemes[0]=scheme; 
		
		this.resourcePatterns.add(new ResourceMappingPatterns(this,types,schemes,null,null,ActionMenuConstants.SELECTION_DONT_CARE)); 
	}
	
	/** 
	 * Add Resource Mapping for the specified Resource Types. 
	 * If the resource has one of the specified types, this menu mapping
	 * will be activted.  
	 */ 
	public void addTypeMapping(String types[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,types,null,null,null,ActionMenuConstants.SELECTION_DONT_CARE)); 
	}
	
	/** 
	 * Add Resource Mapping for the specified Resource Types and schemes. 
	 * If the Resource is equal to on of the specified Types and has one
	 * of the specified schemes, this menu mapping will be activated. 
	 * 
	 */ 
	public void addTypeSchemeMapping(String types[],String schemes[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,types,schemes,null,null,ActionMenuConstants.SELECTION_DONT_CARE)); 
	}
	
	/** 
	 * Add Resource Mapping for the specified Resource Types and schemes. 
	 * If the Resource is equal to on of the specified Types and has one
	 * of the specified schemes, this menu mapping will be activated. 
	 * 
	 */ 
	public void addTypeSchemeMapping(Pattern types[],Pattern schemes[])
	{
		this.resourcePatterns.add(new ResourceMappingPatterns(this,types,schemes,null,null,ActionMenuConstants.SELECTION_DONT_CARE)); 
	}
	
	public void setMenuOptions(int options)
	{
		this.menuOptions=options; 
	}
	
	/** Used by registry to set ownership of mapping */  
	public void setVRS(VRSFactory vrsVal)
	{
		this.isVRSAction=true; 
		this.isViewerAction=false; 
		
		this.vrs=vrsVal; 
	}

	/**
	 * get VRS which supplies the action menu. 
	 * If NULL, then check IViewerClassName ! 
	 */
	public VRSFactory getVRS()
	{
		return vrs; 
	}

	/** Set viewer for the specifed action */ 
	public void setIViewer(String className)
	{
		this.isVRSAction=false; 
		this.isViewerAction=true;  

		this.iviewerClassName=className; 
	}
	
	/** Get viewer which is associated with the specified action */ 
	public String getIViewerClassname()
	{
		return this.iviewerClassName;  
	}

	public boolean isViewerAction()
	{
		return this.isViewerAction; 
	}
	
	public boolean isVRSAction()
	{
		return this.isVRSAction; 
	}
	
}
