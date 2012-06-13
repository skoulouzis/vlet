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
 * $Id: HeaderPopupMenu.java,v 1.3 2011-04-18 12:27:23 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:23 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;

/** Table Header popup menu + popup listener */ 
public class HeaderPopupMenu extends JPopupMenu
{
    /** */
    private static final long serialVersionUID = -3678190881308127063L;
    
    // ========================================================================
    // Class Stuff
    // ========================================================================
    
    public enum HeaderCommand
    { 
         ADD_COLUMN,DELETE_COLUMN,AUTO_FIT_COLUMNS_ON,AUTO_FIT_COLUMNS_OFF
    }; 
        
    /** Non static ActionListener so that it can access outer class members */ 
    public class ANPopupListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            String cmdstr=e.getActionCommand();
            String vals[]=cmdstr.split(":");
            cmdstr=vals[0];
            String argstr=null; 
            
            if (vals.length>1) 
                argstr=vals[1]; 
            
            HeaderCommand cmd=HeaderCommand.valueOf(cmdstr);
            
            switch (cmd) 
            {
                case ADD_COLUMN:
                    tablePanel.insertColumn(headerName,argstr);
                    break; 
                case DELETE_COLUMN:
                    tablePanel.removeColumn(argstr);
                    break;
                case AUTO_FIT_COLUMNS_OFF:
                    tablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    break; 
                case AUTO_FIT_COLUMNS_ON: 
                    tablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    break;
            }
        }

    }
    
    // ========================================================================
    // Object Stuff
    // ========================================================================
   
    private ActionListener popupListener=new ANPopupListener();

    private TablePanel tablePanel;

    /** Header name that was clicked on */ 
    private String headerName; 
    
    public HeaderPopupMenu(TablePanel panel,String header)
    {
        this.tablePanel=panel; 
        this.headerName=header; 
        
        this.add(createAddAttrNamesSubMenu());
        this.add(createMenuItem("Delete column",HeaderCommand.DELETE_COLUMN+":"+header));
        add(new JSeparator());
        // add to current menu
        addHeaderOptionsTo(this); 
        // create submenu:
        //this.add(createHeaderOptionsMenu());
    }
    
  
    private JMenuItem createMenuItem(String name, String cmdString)
    {
        JMenuItem mitem = new JMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmdString); 
        mitem.addActionListener(popupListener);
        
        return mitem; 
    }
    private JMenuItem createCheckBoxMenuItem(String name, String cmdString,boolean selected)
    {
        JCheckBoxMenuItem mitem = new JCheckBoxMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmdString); 
        mitem.addActionListener(popupListener);
        mitem.setSelected(selected);
        return mitem; 
    }
    
    
    
    private JMenuItem createOptionMenuItem(String name, String cmdString,boolean state)
    {
        JCheckBoxMenuItem mitem=  new JCheckBoxMenuItem();
        
        mitem.setState(state);
        mitem.setText(name); 
        mitem.setActionCommand(cmdString); 
        mitem.addActionListener(popupListener);
        return mitem; 
    }
    
    private JMenu createAddAttrNamesSubMenu()
    {
        
        JMenu menu = new JMenu(); 
        menu.setText("Attributes");
         
        String names[]=tablePanel.getAllHeaderNames();
        
        JMenuItem mitem=null;
        
        VRSTableModel model=tablePanel.getVRSTableModel();
        
        if (names!=null)
        {
           for (String name:names)
           {
              boolean present=(model.getHeaderIndex(name)>=0); 
              if (present==false)
                  menu.add(mitem=createCheckBoxMenuItem(name,""+HeaderCommand.ADD_COLUMN+":"+name,present));
              else
                  menu.add(mitem=createCheckBoxMenuItem(name,""+HeaderCommand.DELETE_COLUMN+":"+name,present));
              
           }
        }
        
        return menu;
    }
    
    private JMenuItem createHeaderOptionsMenu()
    {
        JMenu menu = new JMenu(); 
        
        menu.setText("Column properties");
        addHeaderOptionsTo(menu); 
        return menu; 
    }
    
    // JPopupMenu and JMenu only have JComponent as shared parent...
    private void addHeaderOptionsTo(JComponent menu)
    {
        JMenuItem mitem;
        
         //boolean state=(tablePanel.getAutoResizeMode()==JTable.AUTO_RESIZE_OFF);
        boolean state=(tablePanel.getAutoResizeMode()==JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        if (state==false)
            menu.add(mitem=createCheckBoxMenuItem("Auto fit columns",""+HeaderCommand.AUTO_FIT_COLUMNS_ON,state));
        else
            menu.add(mitem=createCheckBoxMenuItem("Auto fit columns",""+HeaderCommand.AUTO_FIT_COLUMNS_OFF,state));
        
        
        /*if (state==true)
            menu.add(createMenuItem("auto fit columns",""+HeaderCommand.AUTO_FIT_COLUMNS_ON));
        else
            menu.add(createMenuItem("expand columns",""+HeaderCommand.AUTO_FIT_COLUMNS_OFF));
            */
        
        //return menu;
    }

}
