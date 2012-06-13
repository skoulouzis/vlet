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
 * $Id: TestACLPanel.java,v 1.3 2011-04-18 12:27:24 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:24 $
 */ 
// source: 

package test;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.panels.acldialog.ACLPanel;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;
import nl.uva.vlet.vrl.VRL;

public class TestACLPanel
{

    public static void main(String args[])
    {
        Global.getLogger().setLevelToDebug(); 
        testFor("file:///home/ptdeboer");
        testFor("srb://piter.de.boer.vlenl@srb.grid.sara.nl/VLENL/home/piter.de.boer.vlenl");
        testFor("sftp://ptdeboer@elab.science.uva.nl/home/ptdeboer"); 
     }
    
    public static void testFor(String loc)
    {
        try
        {
            ProxyVNodeFactory.initPlatform(); 
            ACLPanel.showEditor(null,new VRL(loc));
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        } 
        
    }
}
