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
 * $Id: testIconProvider.java,v 1.5 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */ 
// source: 

package tests;

import java.net.MalformedURLException;
import java.net.URL;

import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.icons.IconProvider;

public class testIconProvider
{
	public static void main(String args[]) throws MalformedURLException
	{
		//use icons path from mbuild! 
		GlobalConfig.setBaseLocation(new URL("file:///home/ptdeboer/workspace/mbuild/dist/dummy.html")); 
		
		IconProvider provider = UIGlobal.getIconProvider(); 
	
//		StringList list=provider.getIconList(); 
//		
//		for (String url:list)
//		{
//			System.out.println("-"+url); 
//		}
	}
}
