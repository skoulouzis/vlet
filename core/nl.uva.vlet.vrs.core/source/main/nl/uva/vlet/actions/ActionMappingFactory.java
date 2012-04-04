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
 * $Id: ActionMappingFactory.java,v 1.3 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.actions;

import static nl.uva.vlet.actions.ActionMenuConstants.*;

import java.util.Vector;

public class ActionMappingFactory
{
	public static Vector<ActionMenuMapping> createTestMenus()
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping("doDummy1","topLevelDummy", 0);
		//mapping.setMenuOptions(MENU_ITEM_ACTION);
		mappings.add(mapping);
		mapping.addTypeMapping((String)null); // add match all 
		
		mapping=new ActionMenuMapping("doSubDummy2","SubDummy1","Test SubMenu", 0);
		mappings.add(mapping);
		
		mapping=new ActionMenuMapping("doDir1","Dir action","Test SubMenu", 0);
		mappings.add(mapping);
		mapping.addTypeMapping("Dir"); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE);
		
		mapping=new ActionMenuMapping("doFile1","File action","Test SubMenu", 0);
		mappings.add(mapping);
		mapping.addTypeMapping("File"); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE);
		
		mapping=new ActionMenuMapping("doDirFile1","DirFile action","Test SubMenu", 0);
		mappings.add(mapping);
		String typs[]={"File","Dir"};
		mapping.addTypeMapping(typs); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE); 
		
		// === local file scheme menu === :
		mapping=new ActionMenuMapping("doLocalDir","LocalDir action","Test Local SubMenu", 0);
		mappings.add(mapping);
		mapping.addTypeSchemeMapping("Dir","file"); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE);
		
		mapping=new ActionMenuMapping("doLocalFile","LocalFile action","Test Local SubMenu", 0);
		mappings.add(mapping);
		mapping.addTypeSchemeMapping("File","file"); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE);
		
		mapping=new ActionMenuMapping("doLocalDirFile","LocalDirFile action","Test Local SubMenu", 0);
		mappings.add(mapping);
		String vfsTypes[]={"File","Dir"}; 
		String schemes[]={"file"}; 
		mapping.addTypeSchemeMapping(vfsTypes,schemes); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE);
		
		mapping=new ActionMenuMapping("doSRB","SRB action","SRB SubMenu", 0);
		mappings.add(mapping);
		 
		String schemes2[]={"srb"}; 
		mapping.addTypeSchemeMapping(vfsTypes,schemes2); //,null,null,ActionMenuConstants.SELECTION_DONT_CARE); 
		
				
		mapping=new ActionMenuMapping("doFileSelection","FileSelection","Selection", 0);
		mappings.add(mapping);
		mapping.addSelectionTypeMapping("File");
		
		mapping=new ActionMenuMapping("doFileDirSelection","FileDirSelection","Selection", 0);
		mappings.add(mapping);
		mapping.addSelectionTypeMapping(vfsTypes); 
		
		return mappings; 
	}
	
	/**
	 * Create simple Type based actionMenuMapping where actionName is the 
	 * action performed, actionMenuItem the item name appearing in the action menu, 
	 * and the 'type' is the specified type of the resource. 
	 * 
	 * @param actionName          Action Performed
	 * @param actionMenuItemName  Menu Item name which will appear in the menu
	 * @param type	              Resource Type the node must have for this action menu item. 
	 * @return                    ActionMenuMapping  
	 */
	public ActionMenuMapping createTypeMenuMapping(
			String actionName,
			String actionMenuItemName,
			String type)
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping(actionName,actionMenuItemName, 0);
		mapping.setMenuOptions(MENU_INACTIVE_DONT_SHOW & MENU_ITEM_ACTION);
		mappings.add(mapping);
		mapping.addTypeMapping(type); 
		return mapping; 
	}
	
	/**
	 * Create simple Type based ActionMenuMapping where actionName is the 
	 * action performed and actionMenuItem the item name appearing in the action menu.  
	 * This menu mapping will match all node which type is one of 'types'.  
	 * 
	 * @param actionName          Action Performed.
	 * @param actionMenuItemName  Menu Item name which will appear in the menu.
	 * @param types	              Resource Types the node must have for this action menu item. 
	 * @return                    ActionMenuMapping.  
	 */
	public ActionMenuMapping createTypeMenuMapping(
			String actionName,
			String actionMenuItemName,
			String types[])
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping(actionName,actionMenuItemName, 0);
		mapping.setMenuOptions(MENU_INACTIVE_DONT_SHOW & MENU_ITEM_ACTION);
		mappings.add(mapping);
		mapping.addTypeMapping(types); 
		return mapping; 
	}
	
	/**
	 * Create simple Type+Scheme based ActionMenuMapping where actionName is the 
	 * action performed and actionMenuItem the item name appearing in the action menu.  
	 * This menu mapping will match all node which type is one of 'types'.  
	 * 
	 * @param actionName          Action Performed.
	 * @param actionMenuItemName  Menu Item name which will appear in the menu.
	 * @param types	              One of the allowed Resource Types for this action menu item.
	 * @param scheme              Resource Scheme the node must have.  
	 * @return                    ActionMenuMapping.
	 */
	public ActionMenuMapping createTypeMenuMapping(
			String actionName,
			String actionMenuItemName,
			String types[],
			String scheme)
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping(actionName,actionMenuItemName, 0);
		mapping.setMenuOptions(MENU_INACTIVE_DONT_SHOW & MENU_ITEM_ACTION);
		mappings.add(mapping);
		String schemes[]=new String[1]; 
		schemes[0]=scheme;
		mapping.addTypeSchemeMapping(types,schemes); 
		return mapping; 
	}
	/**
	 * Create simple Type based ActionMenuMapping where actionName is the 
	 * action performed and actionMenuItem the item name appearing in the action menu.  
	 * This menu mapping will match all node which type is one of 'types'.  
	 * 
	 * @param actionName          Action Performed
	 * @param actionMenuItemName  Menu Item name which will appear in the menu
	 * @param types	              One of the allowed Resource Types for this action menu item.
	 * @return                    ActionMenuMapping  
	 */
	public ActionMenuMapping createTypeSchemeMenuMapping(
			String actionName,
			String actionMenuItemName,
			String types[],
			String schemes[])
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping(actionName,actionMenuItemName, 0);
		mapping.setMenuOptions(MENU_INACTIVE_DONT_SHOW & MENU_ITEM_ACTION);
		mappings.add(mapping);
		mapping.addTypeSchemeMapping(types,schemes); 
		return mapping; 
	}
	
	/**
	 * Create simple Type based ActionMenuMapping where actionName is the 
	 * action performed and actionMenuItem the item name appearing in the action menu.  
	 * This menu mapping will match all node which type is one of 'types'.  
	 * 
	 * @param actionName          Action Performed
	 * @param actionMenuItemName  Menu Item name which will appear in the menu
	 * @param types	              One of the allowed Resource Types for this action menu item.
	 * @param selectionMode       Extra selection conditions which determine menu mapping.  
	 * @return                    ActionMenuMapping  
	 */
	public ActionMenuMapping createTypeSchemeMenuMapping(
			String actionName,
			String actionMenuItemName,
			String types[],
			String schemes[],
			int selectionMode)
	{
		Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 

		// Dummy mappings: No matching criteria  
		ActionMenuMapping mapping;
		
		mapping=new ActionMenuMapping(actionName,actionMenuItemName, 0);
		mappings.add(mapping);
		mapping.setMenuOptions(selectionMode);
		mapping.addTypeSchemeMapping(types,schemes); 
		return mapping; 
	}
}
