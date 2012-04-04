/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: TestActionMapping.java,v 1.1 2011-12-06 09:59:18 ptdeboer Exp $  
 * $Date: 2011-12-06 09:59:18 $
 */ 
// source: 

package test.actions;

import java.util.Vector;

import nl.uva.vlet.actions.ActionMappingFactory;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.actions.ActionMenuMatcher;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

public class TestActionMapping
{
	
	public static void main(String args[])
	{
		ActionMappingFactory factory=new ActionMappingFactory(); 
		Vector<ActionMenuMapping> mappings = factory.createTestMenus(); 
		
		ActionMenuMatcher matcher=new ActionMenuMatcher(mappings); 
		
		VRL fileVRL=new VRL("file",null,0,"/etc/passwd"); 
		
		Vector<ActionMenuMapping> menuMappings; 
		
		//menuMappings=matcher.getMappingsMenusFor(null,null);
		//printMenus("null,null",menuMappings);
		
		matcher.matchForTypeAndVRL("File",null);
		printMenus("Type=File,null",matcher);
		
		matcher.matchForTypeAndVRL("Dir",null);
		printMenus("Type=Dir,null",matcher);
		
		matcher.matchForTypeAndVRL("File",fileVRL);
		printMenus("Type=File,"+fileVRL,matcher);
		
		matcher.matchForTypeAndVRL("Dir",fileVRL);
		printMenus("Type=Dir,"+fileVRL,matcher);
		
		// not allowed:
		//menuMappings=matcher.getMappingsMenusFor(null,fileVRL);
		//printMenus("Type=null,"+fileVRL,menuMappings);

		VRL srbVRL=new VRL("srb",null,0,"/blah"); 
		matcher.matchForTypeAndVRL("File",srbVRL); 
		printMenus("Type=File,"+srbVRL,matcher);

		
	}

	private static void printMenus(String str, ActionMenuMatcher matcher)
	{
		System.out.println(" === Menus for: "+str+" === "); 
		
		if (matcher.getMatchedMappings()==null)
		{
			System.out.println(" - *NULL* ");
			return; 
		}
		
		Vector<ActionMenuMapping> mappings = matcher.getMatchedMappingsForSubMenu(null); 
		for (ActionMenuMapping map:mappings)
		{
			System.out.println(" - "+map.getMenuItemName()+"=>"+map.getActionName()+"()");
		}
		
		StringList subMenus=matcher.getMatchedSubMenus(); 
		 
		for (String subName:subMenus) 
		{
			System.out.println(" - ["+subName+"] => ");
			
			mappings=matcher.getMatchedMappingsForSubMenu(subName); 

			
			for (ActionMenuMapping map:mappings)
			{
				if (StringUtil.compare(subName,map.getSubMenuName())==0)
					System.out.println(" - - "+map.getMenuItemName()+"=>"+map.getActionName()+"()");
			}

		}
		
	}
	
}
