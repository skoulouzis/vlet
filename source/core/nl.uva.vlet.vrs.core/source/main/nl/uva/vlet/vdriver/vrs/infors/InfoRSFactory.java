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
 * $Id: InfoRSFactory.java,v 1.1 2011-11-25 13:40:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:48 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

/**
 * nl.uva.vlet.vrs.info.InfoRSFactory; 
 * 
 * @author Piter T. de Boer 
 */
public class InfoRSFactory extends VRSFactory
{
    public static String[] schemes={ VRS.INFO_SCHEME,"grid" } ;

    public static String[] types={ "InfoType" } ;

    @Override
    public void clear()
    {
    }

    @Override
    public String getName()
    {
        return "Info Service"; 
    }
    
    @Override
    public String[] getResourceTypes()
    {
        return types; 
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes;
    }
   
    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
    {
        info=super.updateServerInfo(context,info,loc); 
        
        info.setNeedHostname(false); 
        info.setNeedPort(false); 
        info.setSupportURIAtrributes(false); 
        
        return info;  
    }
    
	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context,
			ServerInfo info, VRL location) throws VlException 
	{
		// should be one singleton instance per Context ! 
		return new InfoResourceSystem(context); 
	}
}
