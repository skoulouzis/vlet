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
 * $Id: AboutRS.java,v 1.6 2011-04-27 12:37:12 ptdeboer Exp $  
 * $Date: 2011-04-27 12:37:12 $
 */ 
// source: 

package nl.uva.vlet.gui.aboutrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VResourceSystem;

public class AboutRS implements VResourceSystem
{
    private VRSContext vrsContext; 
    
    public AboutRS(VRSContext context)
    {
        this.vrsContext=context;
    }

    //@Override
    public String getID()
    {
        return "about-rs"; 
    }

    //@Override
    public VNode openLocation(VRL vrl) throws VlException
    {
        return new AboutNode(this,vrl);
    }

    //@Override
    public VRSContext getVRSContext()
    {
        return vrsContext;
    }

    @Override
    public void connect() throws VlException
    {
    }    

    @Override
    public void disconnect()
    {
        
    }

    @Override
    public void dispose()
    {
    }

     
}
