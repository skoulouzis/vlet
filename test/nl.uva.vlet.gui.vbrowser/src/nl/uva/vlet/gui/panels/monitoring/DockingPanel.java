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
 * $Id: DockingPanel.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.monitoring;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.BevelBorder;

/**
 * Docks panels in vertical Boxed JPanel container
 */
public class DockingPanel extends JPanel
{
    private static final long serialVersionUID = -1629058136015889725L;
    
    public DockingPanel()
    {
        super(); 
        initGUI();
    }
    
    public void add(JPanel panel)
    {
        super.add(panel); 
        this.revalidate(); 
    }

    public JPanel[] getPanels()
    {
        Component[] comps = this.getComponents();
        if (comps==null)
            return null; 
        
        Vector<JPanel> panels=new Vector<JPanel>(); 
        
        for (Component comp:comps)
            if (comp instanceof JPanel)
                panels.add((JPanel)comp); 
        
        JPanel arr[]=new JPanel[panels.size()]; 
        arr=panels.toArray(arr); 
        return arr; 
    }
    
    private void initGUI() 
    {
        try 
        {
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }

}
