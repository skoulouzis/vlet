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
 * $Id: ReplicaPopupMenu.java,v 1.2 2011-04-18 12:27:25 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:25 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.replicaviewer;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.panels.resourcetable.TablePopupMenu;
import nl.uva.vlet.gui.viewers.grid.replicaviewer.ReplicaDataModel.ReplicaStatus;

public class ReplicaPopupMenu extends TablePopupMenu
{
    private static final long serialVersionUID = 4937904778804370242L;
    
    public static final String OPEN_PARENT      = "openParent"; 
    public static final String DELETE           = "delete";
    public static final String UNREGISTER       = "unregister";
    public static final String KEEP             = "keep";
    public static final String DELETE_SELECTION = "deleteSelection";
    public static final String KEEP_SELECTION   = "keepSelection";
    public static final String SHOW_PROPERTIES  = "showProperties"; 
    
    // Multi action item: is updated for Replica status 
    private JMenuItem actionItem;
    private JMenuItem openItem; 
    private JMenuItem propItem; 
    
    public ReplicaPopupMenu(ReplicaController controller)
    {
        super();
        actionItem=new JMenuItem("Delete");
        this.add(actionItem);
        actionItem.addActionListener(controller); 
        
        this.add(new JSeparator()); 
        
        openItem=new JMenuItem("Open Storage Location"); 
        openItem.setActionCommand("openParent");
        openItem.addActionListener(controller);
        this.add(openItem); 
        
        propItem=new JMenuItem("Show replica Properties"); 
        propItem.setActionCommand("showProperties");
        propItem.addActionListener(controller);
        this.add(propItem);
        
    }
    
    @Override
    public void updateFor(ResourceTable table, MouseEvent e,boolean canvasMenu)
    {
        int rows[]=table.getSelectedRows();
        actionItem.setEnabled(true);
        
        if ((rows!=null) && (rows.length>=2)) 
        {
            boolean allDeletes=true;  
            for (int row:rows)
                if (isToBeDeleted(table,row)==false)
                {
                    allDeletes=false;  
                    break;
                }
            
            if (allDeletes==true)
            {
                // Keep All 
                actionItem.setText("Keep Replicas");
                actionItem.setActionCommand(KEEP_SELECTION); 
            }
            else
            {
                // selection menu: 
                actionItem.setText("Delete Replicas");
                actionItem.setActionCommand(DELETE_SELECTION); 
            }
            
        }
        else
        {
            String rowKey=table.getKeyUnder(e.getPoint());
            boolean isnew=isNew(table,table.getModel().getRowIndex(rowKey));
            boolean hasError=hasError(table,table.getModel().getRowIndex(rowKey));
            
            openItem.setActionCommand(OPEN_PARENT+":"+rowKey);
 
            openItem.setEnabled(isnew==false);
            
            propItem.setActionCommand(SHOW_PROPERTIES+":"+rowKey);
            propItem.setEnabled(isnew==false);
            
            if (rowKey!=null)
            {
                if (isToBeDeleted(table,table.getModel().getRowIndex(rowKey)))
                {
                    actionItem.setText("Keep Replica");
                    actionItem.setActionCommand(KEEP+":"+table.getKeyUnder(e.getPoint()));
                }
                else if (isToBeUnregistered(table,table.getModel().getRowIndex(rowKey)))
                {
                    actionItem.setText("Keep Replica");
                    actionItem.setActionCommand(KEEP+":"+table.getKeyUnder(e.getPoint()));
                }
                else
                {
                    if (hasError==false)
                    {
                        actionItem.setText("Delete Replica");
                        actionItem.setActionCommand(DELETE+":"+table.getKeyUnder(e.getPoint()));
                    }
                    else
                    {
                        actionItem.setText("Unregister Replica");
                        actionItem.setActionCommand(UNREGISTER+":"+table.getKeyUnder(e.getPoint()));
                    }
                }
            }
            else
            {
                // miss clicked ? 
                actionItem.setText("?");
                actionItem.setActionCommand("?"); 
                actionItem.setEnabled(false); 
            }
        }
    }

    private boolean isToBeDeleted(ResourceTable table, int row)
    {
        if (row<0)
            return false;
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.DELETE))
            return true;
    
        return false; 
    }
    
    
    private boolean isToBeUnregistered(ResourceTable table, int row)
    {
        if (row<0)
            return false;
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.UNREGISTER))
            return true;
    
        return false; 
    }
    
    private boolean isNew(ResourceTable table, int row)
    {
        if (row<0)
            return false; 
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.NEW))
            return true;
        
        return false; 
    }
    
    private boolean hasError(ResourceTable table, int row)
    {
        if (row<0)
            return false; 
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.ERROR))
            return true;
        
        return false; 
    }
    

}
