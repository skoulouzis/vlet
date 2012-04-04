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
 * $Id: ConfigurationNode.java,v 1.3 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class ConfigurationNode extends VCompositeNode
{
	public static final String CONFIGURATION_TYPE="Configuration"; 
	
	public ConfigurationNode(VRSContext context) 
	{
		super(context,new VRL(VRS.MYVLE_SCHEME,null,CONFIGURATION_TYPE)); 
	}

	public String getType() 
	{
		return CONFIGURATION_TYPE; 
	}

	public String getName()
	{
		return CONFIGURATION_TYPE; 
	}
	
	public VNode[] getNodes() throws VlException
	{
		return null;
	}

	public String[] getResourceTypes() 
	{
		return null;
	}

}
