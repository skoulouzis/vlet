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
 * $Id: ActionMenuMatcher.java,v 1.3 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.actions;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.OrdenedHashtable;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

/**
 * Optimized ActionMenuMatcher matches ActionMenuMappings against 
 * selected resources. 
 * 
 * This class also create hashmaps for faster matching.
 * 
 * The Matcher is a seperate class so that optimization is possible 
 * if needed since the matching will be done during the menu creation process
 * of the VBrowser.  
 *  
 * @author P.T.de Boer 
 */
public class ActionMenuMatcher
{
	public static class ActionMenuMappingList extends Vector<ActionMenuMapping> 
	{
		private static final long serialVersionUID = 8873295552142124846L;
	}
		
	// === instance === // 
		
	private ActionMenuMappingList mappings=null; 

	/** Fast matching from Type+Scheme to menu mappings which match Type+Scheme */ 
	//OrdenedHashtable<String,ActionMenuMappingList> sourceTypeToMapping;
	//private OrdenedHashtable<String,String> actionNames=new  OrdenedHashtable<String,String>();
	//private OrdenedHashtable<String,String> actionMenuNames=new  OrdenedHashtable<String,String>();
	//private OrdenedHashtable<String,String> actionSubMenuNames=new  OrdenedHashtable<String,String>();

	/**
	 * Action Patterns which allows this action. Action is matched if any 
	 * of the resourcePatterns match. 
	 */  
	private OrdenedHashtable<String,Vector<ResourceMappingPatterns>> resourceTypePatterns= new OrdenedHashtable<String,Vector<ResourceMappingPatterns>>();

	private Vector<ResourceMappingPatterns> resourceSelectionPatterns=new	Vector<ResourceMappingPatterns>();


	// ========================================================================
	// Cached/Derives lists 
	// ========================================================================

	/** Latest matched mappings */ 
	private Vector<ActionMenuMapping>  matchedMappings=null;

	/** Latest matched subMenus, also used as order for matchedSubMenuMappings */ 
	private StringList matchedSubMenus;    

	/** Matches Mappings collected per subMenu */ 
	//private Map<String,Vector<ActionMenuMapping>> matchedSubMenuMappings= new Hashtable<String,Vector<ActionMenuMapping>>();

	public ActionMenuMatcher(ActionMenuMappingList mappings)
	{
		init(mappings); 
	}

	public ActionMenuMatcher(Vector<ActionMenuMapping> mappings)
	{
		init(mappings); 
	}

	private synchronized void init(Vector<ActionMenuMapping> newMappings)
	{
		mappings=new ActionMenuMappingList();
		addMappings(newMappings);
	}

	public synchronized void addMappings(Vector<ActionMenuMapping> newMappings)
	{
		for(ActionMenuMapping mapping:newMappings)
		{
			Debug("Adding mapping:"+mapping);
			
			this.mappings.add(mapping); 
		}
		
		/* (re)create hashmaps */ 
		createHashMaps(); 
	}

	/** After matching create derived lists */
	private synchronized void createHashMaps()
	{
		Debug("createHashMaps()");
		//sourceTypeToMapping=new OrdenedHashtable<String,ActionMenuMappingList>(); 

		//actionNames.clear();
		//actionMenuNames.clear();
		//actionSubMenuNames.clear();
		resourceSelectionPatterns.clear();
		
		// update/clear matchedMappings: 
		matchedMappings=new Vector<ActionMenuMapping>(); 
		
		// sub menu list:
		matchedSubMenus=new StringList(); 

		for(ActionMenuMapping mapping:mappings)
		{
			Debug("processing mapping:"+mapping); 
			
			//String actionName=mapping.getActionName(); 
			//String actionMenuName=mapping.getActionMenuName(); 
			//String actionSubMenuName=mapping.getActionSubMenuName(); 

			//actionNames.put(actionName,actionName); 
			//actionMenuNames.put(actionMenuName,actionName);

			//if (actionSubMenuName!=null)
			//	actionSubMenuNames.put(actionSubMenuName,actionName);

			// collect all ResourceSelectionPatterns: 
			Vector<ResourceMappingPatterns> patterns = mapping.getResourceTypeSchemePatterns();
			resourceSelectionPatterns.addAll(patterns); 

			//
			// Level (I) Hashmapping is by ResourceType (Type) since each 
			// resource has to have a Type ! 
			//

			// all patterns (no hashing) 
			this.resourceSelectionPatterns.addAll(patterns); 

			
			// Create Pattern By Type hash: 
			for (ResourceMappingPatterns pat:patterns)
			{
				Debug(" - Adding pattern:"+pat);
				
				StringList types=pat.getAllowedSources().getAllowedTypes();

				// hashing (I) by Type:
				if (types==null)
				{
					String type="*";//null is not allowed to be used as key :-(

					// get pattern list for this type: 
					Vector<ResourceMappingPatterns> list = resourceTypePatterns.get(type);
					// autocreate: 

					if (list==null)
						list=new Vector<ResourceMappingPatterns>();

					list.add(pat);
					// put updated pattern list: 
					resourceTypePatterns.put(type,list); 
				}
				else	
					for (String type:types)
					{
						// null not allowed in hashtables: 
						if (type==null)
							type="*";

						// get pattern list for this type: 
						Vector<ResourceMappingPatterns> list = resourceTypePatterns.get(type);
						
						// autocreate: 
						if (list==null)
							list=new Vector<ResourceMappingPatterns>();

						list.add(pat);
						// put updated pattern list: 
						resourceTypePatterns.put(type,list); 
					}
			}
			// todo: additional resource type to menu mappings: 
		}
	}

	public synchronized void matchForTypeAndVRL(String sourceType,VRL sourceVRL)
	{
		ActionContext context=new ActionContext(sourceType,sourceVRL,null,null,null,null,false); 
		matchFor(context); 
	}
	
	public void matchFor(ActionContext selContext)
	{
		String sourceType=selContext.type; 
		
		
		synchronized(matchedMappings)
		{
			matchedMappings.clear();

			Vector<ResourceMappingPatterns> patternList=null; 
			//patternList=this.resourceSelectionPatterns;

			// sourceType is used as hash entry, since each VNode MUST have a type! 
			if (sourceType==null)
				throw new NullPointerException("sourceType can not be null"); 

			// hashlist (I): use hashed list by type: 
			patternList=this.resourceTypePatterns.get(sourceType);

			// get match *any* patterns as well:  
			Vector<ResourceMappingPatterns> patternList2 = this.resourceTypePatterns.get("*");
			
			if (patternList==null)
				patternList=patternList2; 
			else if (patternList2!=null)
				patternList.addAll(patternList2); 

			// no mappings: 
			if (patternList==null)
			{
				matchedMappings.clear();
				return; 
			}

			// Search reduced Pattern List (Based on hashed Type list) 
			for (ResourceMappingPatterns pattern:patternList) 
			{
				if (pattern.matches(selContext))
				{
					//get (parent) mapping  of resource pattern: 
					ActionMenuMapping mapping=pattern.getActionMenuMapping();

					// add mapping if not in vector already: 
					if (matchedMappings.contains(mapping)==false)
					{
						matchedMappings.add(mapping);
						Debug("+++ Adding mapping:"+mapping); 
					}
				}
			}
			// INSIDE synchronized ! 
			createMatchedSubmenus();
		}// synchronized
	}

	/** Return Matched mapping since latest match */ 
	public Vector<ActionMenuMapping>  getMatchedMappings()
	{
 		return this.matchedMappings; 
	}

	/** Get Submenu names which match curren selection */ 
	public StringList getMatchedSubMenus()
	{
		return this.matchedSubMenus;
	}

	private void createMatchedSubmenus()
	{
		
		synchronized(matchedMappings)
		{
			synchronized(matchedSubMenus)
			{
				this.matchedSubMenus.clear(); 

				for (ActionMenuMapping map:matchedMappings)
				{
					String subName=map.getSubMenuName(); 

					if ((subName!=null) && (matchedSubMenus.contains(subName)==false))
					{
						Global.debugPrintf(this,"adding submenu:%s\n",subName); 
						
						matchedSubMenus.add(subName);
					}
				}
			} // synchronized,synchronized 
		}
	}

	/** Get actions for subMenu. subMenu==null mean toplevel menu */

	public StringList getMatchedActionMenuNamesForSubMenu(String subMenu)
	{
		StringList actions=new StringList();  

		for (ActionMenuMapping map:matchedMappings)
		{
			String name=map.getSubMenuName(); 

			if (StringUtil.compare(subMenu,name)==0) 
				actions.add(map.getMenuItemName());
		}

		return actions; 
	}

	public Vector<ActionMenuMapping> getMatchedMappingsForSubMenu(String subMenu)
	{
		Debug("getMatchedMappingsForSubMenu:"+subMenu);
		
		Vector<ActionMenuMapping> mappings=new  Vector<ActionMenuMapping>();

		for (ActionMenuMapping map:matchedMappings)
		{
			String name=map.getSubMenuName();
			
			Debug("getMatchedMappingsForSubMenu checking submenu:"+name); 
			
			if (StringUtil.compare(subMenu,name)==0) 
			{
				Debug("getMatchedMappingsForSubMenu adding submenu map:"+map);
				mappings.add(map);
			}
		}
		
		if (mappings.size()==0)
		{
			Global.warnPrintf(this,"Warning: Empty Submenu for:%s\n",subMenu);
			return null; 
		}
		
		return mappings;
	}

	

	public int getSubMenuOptions(String subMenu)
	{
		if (subMenu==null)
			return -1; 
		
		for (ActionMenuMapping map: this.matchedMappings)
		{
			if (map.getSubMenuName().compareTo(subMenu)==0)
			{
				return map.getMenuOptions(); 
			}
		}
		
		return -1; 
	}
	
	private void Debug(String msg)
	{
		//Global.errorPrintln(this,msg);
		Global.debugPrintf(this,"%s\n",msg); 
	}

}
