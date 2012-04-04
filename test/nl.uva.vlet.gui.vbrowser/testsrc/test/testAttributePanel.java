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
 * $Id: testAttributePanel.java,v 1.5 2011-06-07 15:15:07 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:07 $
 */ 
// source: 

package test;

import javax.swing.JFrame;


import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.panels.attribute.AttributePanel;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class testAttributePanel
{

    public static void main(String args[])
    {
        ClassLogger.getRootLogger().setLevelToDebug();  
        
        // jigloo panel: 
        AttributePanel panel=new AttributePanel();
        panel.setVisible(true); 
        
        // create SRBServerInfo panels:
        ServerInfo irodsInfo=null;
        try
        {
            irodsInfo = new ServerInfo(VRSContext.getDefault(),new VRL("irods://piter_de_boer@irods.grid.sara.nl/SARA_BIGGRID/piter_de_boer/"));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
        
        VAttribute srbAttrs[]=irodsInfo.getAttributes(); 
        
        VAttributeSet attrs=irodsInfo.getAttributeSet();
        AttributePanel.showEditor(attrs); 
        
        // tests asynchtonous setAttributes ! 
        
        JFrame frame=new JFrame();
        panel=new AttributePanel();
       
        frame.add(panel);
        //frame.pack();
        frame.setVisible(true);
        
        panel.setAttributes(attrs,true);
        
        // update frame: 
        frame.setSize(frame.getPreferredSize());
        
        // panel.setSize(panel.getLayout().preferredLayoutSize(panel)); 
    }

}
