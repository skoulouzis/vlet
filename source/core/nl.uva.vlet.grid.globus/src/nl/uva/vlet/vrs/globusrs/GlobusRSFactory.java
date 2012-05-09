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
 * $Id: GlobusRSFactory.java,v 1.3 2011-04-18 12:08:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:08:39 $
 */ 
// source: 

package nl.uva.vlet.vrs.globusrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;
import nl.vlet.uva.grid.globus.GlobusUtil;

/** 
 * Globus Information System. 
 * 
 * By creating a Globus Resource Factory, the Grid-Globus bindings will be initialized
 * when this factory is created!
 * The current resource system provides information about the Globus implementation. 
 */
public class GlobusRSFactory extends VRSFactory
{
	// "grid" is covered by the info system, as "grid" is generic, and "globus" is 
	// implementation specific. 
	// " voms" shouldbe covered by GridInfosystem, this (minimal) resource system
	// is only for globus stuff. 
	
	private static String schemes[]={"globus"};  
	
    static 
    {
        // Static Initializer! -> registers Globus Bindings
        GlobusUtil.init(); 
    }
    
    @Override
    public void clear()
    {
        
    }

    @Override
    public String getName()
    {
        return "GlobusRS"; 
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes; 
    }

	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context,
			ServerInfo info, VRL location) throws VlException 
	{
		return new GlobusInfoSystem(context,location); 
	}

}
