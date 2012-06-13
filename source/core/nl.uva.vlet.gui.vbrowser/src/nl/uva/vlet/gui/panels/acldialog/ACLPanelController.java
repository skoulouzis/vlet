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
 * $Id: ACLPanelController.java,v 1.4 2011-04-18 12:27:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:30 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.acldialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;

public class ACLPanelController implements ActionListener, WindowListener
{
    private ACLPanel aclPanel=null; 

    public ACLPanelController(ACLPanel panel)
    { 
        this.aclPanel=panel; 
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd=e.getActionCommand(); 
        Component comp=(Component)e.getSource(); 
        
        //Global.debugPrintln("ACLPanelController","cmd="+cmd);
        if ((cmd.length()>4) && (cmd.substring(0,4).compareTo("add:")==0))
        {
            String entityName=cmd.substring(4,cmd.length());
            addEntity(entityName); 
        }
        else if (cmd.compareTo(ACLPanel.ADD)==0)
        {
            JPopupMenu menu=createEntityMenu(); 
            menu.show(comp,5,5); 
        }
        else if (cmd.compareTo(ACLPanel.DELETE)==0)
        {
            deleteSelectedRows(); 
        }
        else if (cmd.compareTo(ACLPanel.APPLY)==0)
        {
            apply(); 
        }
        else if (cmd.compareTo(ACLPanel.REREAD)==0)
        {
            reread(); 
        }
        else if (cmd.compareTo(ACLPanel.ACCEPT)==0)
        {
            apply(); 
            close();
        }
        else if (cmd.compareTo(ACLPanel.CANCEL)==0)
        {
            close();
        }
    }

    private void deleteSelectedRows()
    {
        int rows[]=aclPanel.getTable().getSelectedRows();
        aclPanel.getModel().delRows(rows);
    }

    private void reread()
    {
        try
        {
            VAttribute[][] acl;
            acl = this.aclPanel.getNode().getACL();
            this.aclPanel.getModel().setACL(acl);
            this.aclPanel.getTable().initColumns(); 
        }
        catch (VlException e)
        {
            handle(e); 
        }
    }

    private void close()
    {
        aclPanel.close(); 
    }

    private void apply()
    {
        try
        {
            VAttribute[][] acl = aclPanel.getModel().getACL();
            this.aclPanel.getNode().setACL(acl);
            
            reread();
        }
        catch (VlException e)
        {
           handle(e); 
        }
    }

    private JPopupMenu createEntityMenu()
    {
        JPopupMenu popupmenu=new JPopupMenu();
        JComponent menu=popupmenu; 
        
        VAttribute ents[]=aclPanel.getACLEntities();
        JMenuItem mitem=null;
        
        int maxMenuItems=30; 
        int itemnr=0; 
        
        if (ents==null)
        {
            menu.add(mitem=createMenuItem(this,"None",null));
            mitem.setEnabled(false); 
        }
        else
        {
          for (VAttribute attr:ents)
          {
              String name=attr.getStringValue(); 
              menu.add(mitem=createMenuItem(this,name,"add:"+name)); 
              itemnr++; 
              
              if (itemnr>maxMenuItems)
              {
                  mitem=new JMenu("More");
                  menu.add(mitem); 
                  menu=mitem;
                  itemnr=0; 
              }
          }
        }
        
        return popupmenu;
    }
    

    private static JMenuItem createMenuItem(ACLPanelController listener,String name,String cmd)
    {
        JMenuItem mitem = new JMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmd); 
        mitem.addActionListener(listener); 
        return mitem; 
    }
    
    public void addEntity(String entityName)
    {
        VAttribute entityAttr=null; 
        
        VAttribute[] aclEntities = aclPanel.getACLEntities(); 
        
        for (int i=0;i<aclEntities.length;i++) 
        {
            if (aclEntities[i].getStringValue().compareTo(entityName)==0) 
            {
                entityAttr=aclEntities[i]; 
            }
        }
        
        if (entityAttr==null) 
        {
            Global.errorPrintf(this,"Couldn't find entity:%s\n",entityName); 
            return; 
        }
        
        ProxyNode node=aclPanel.getNode();
        
        try
        {
            VAttribute record[]=node.createACLRecord(entityAttr,false);
            // Add data row obly (No Row Object!)
            aclPanel.getModel().addACLRecord(record); 
            
        }
        catch (VlException e)
        {
           handle(e); 
        } 
        
        //add record:
    }

    private void handle(VlException e)
    {
        aclPanel.handle(e); 
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        close(); 
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
    

}
