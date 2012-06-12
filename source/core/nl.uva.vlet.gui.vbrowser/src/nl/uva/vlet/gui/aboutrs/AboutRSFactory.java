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
 * $Id: AboutRSFactory.java,v 1.6 2011-04-18 12:27:31 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:31 $
 */ 
// source: 

package nl.uva.vlet.gui.aboutrs;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

/** 
 * Prototype "about:" resource system. 
 */

public class AboutRSFactory extends VRSFactory
{
    private AboutRS aboutRS;
    
	@Override
	public String getName()
	{
		return "AboutRS";	
	}

	private String[] schemes=
		{
			"about",
	        "About"
		};
	
	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}

	
	@Override
	public void clear()
	{
		
	}
  

    @Override
    public VResourceSystem createNewResourceSystem(VRSContext context, ServerInfo info,VRL location)
            throws VlException
    {
        if (aboutRS==null)
            aboutRS=new AboutRS(context);
        
        return aboutRS; 
    }


    @Override
    public String[] getResourceTypes()
    {
        String types[]=new String[1];
        types[0]="About";
        return types; 
    }


    public static void initPlatform()
    {
        try
        {
            VRS.getRegistry().addVRSDriverClass(AboutRSFactory.class);
        }
        catch (Exception e)
        {
            Global.errorPrintf(AboutRSFactory.class,"*** Exception:%s\n",e); 
            Global.errorPrintStacktrace(e); 
        }
        
    }

}
