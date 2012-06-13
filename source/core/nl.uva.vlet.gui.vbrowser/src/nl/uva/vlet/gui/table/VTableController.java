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
 * $Id: VTableController.java,v 1.3 2011-04-18 12:27:23 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:23 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.actions.ActionCommand;
import nl.uva.vlet.gui.actions.ActionCommandType;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.panels.attribute.AttributeViewer;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.viewers.ViewerEvent;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vrl.VRL;

/**
 *  
 * Table Controller 
 * 
 * @author P.T. de Boer
 *
 */
public class VTableController implements MouseListener, MouseMotionListener
{
    private TablePanel tablePanel=null;
    private BrowserController masterBrowser=null;
    private MouseEvent firstMouseEvent=null;
    
    public static class TablePos{public int row; public int column;

    public String columnName;}; 
    
    public VTableController(TablePanel panel,BrowserController bc)
    {
        tablePanel=panel;
        masterBrowser=bc; 
    }

    public void mousePressed(MouseEvent e)
    {
        Component comp=(Component)e.getSource();
        Point clickPoint=e.getPoint(); 
        
        TablePos tablePos=getTablePositionOf(e);
       
        //Global.debugPrintln(this,"MouseClicked:"+e);
        
        boolean ctrl=((e.getModifiersEx() & e.CTRL_DOWN_MASK) !=0); 
        
        if ((comp instanceof JTableHeader) && (GuiSettings.isPopupTrigger(e)) && (tablePos!=null))
        {
        	// Click on TableHeader: 
        	this.tablePanel.showHeaderMenuIn(comp,tablePos.columnName,e.getX(),e.getY());
            return;
        }
        else if ((comp instanceof JScrollPane) &&  (GuiSettings.isPopupTrigger(e)))
        {
        	// Click on scrollpane is Canvas click: 
        	masterBrowser.showNodeMenu(comp,tablePanel,e.getX(),e.getY());
        	return;
        }
        else if ((comp instanceof TablePanel) &&  (GuiSettings.isPopupTrigger(e)))
        {
        	ResourceRef vrls[]=null;
        	
    		
        	// menu click on table on one row or one selected row:  
    		if (ctrl==false) 
    		{
    			// clear previous selection and select current: 
    			tablePanel.changeSelection(tablePos.row,tablePos.column,false,false);
    			
        		ProxyNode node=tablePanel.getPNodeAt(clickPoint);
        		
        		if (node!=null)
        		{
               		// TODO: row objects:
            		// use Dummy Row Object as VComponent

        			VDummyRowObject dummy=new VDummyRowObject(tablePanel,this.masterBrowser,node.getResourceRef()); 
        			masterBrowser.showNodeMenu(comp,dummy,e.getX(),e.getY());
        		}

    		}
    		else
    		{
    			// select current as well: 
       			//tablePanel.changeSelection(tablePos.row,tablePos.column,true,false);
       			vrls=this.tablePanel.getSelection(); 

    			masterBrowser.showNodeMenu(comp,e.getX(),e.getY());
    	    }
        }
        // else
    }

    public void mouseClicked(MouseEvent e)
    {
        Component comp=(Component)e.getSource();
        TablePos pos=getTablePositionOf(e);
        
        if (pos==null) 
        	return; 
        
        VAttribute attr=null;
        
        //Global.debugPrintln("TableMouseEventHandler","click on component:"+comp);
         
        if (comp instanceof JTableHeader)
        { 
          //  click on header
          if (GuiSettings.isSelection(e))
           {
               if ((tablePanel.getSortColumnName()!=null) && (pos.columnName.compareTo(tablePanel.getSortColumnName())==0))
                   tablePanel.reverseSortOrder(); 
               else
                   tablePanel.setReverseSort(false); 
               
               tablePanel.setSortColumnName(pos.columnName); 
               tablePanel.sortBy(pos.columnName,tablePanel.getReverseSort());
                 
               return;
           }
        }
        
        if (pos==null)
            return; 
        
        Object value = tablePanel.getVRSTableModel().getValueAt(pos.row,pos.column);
          
        if (value instanceof VAttribute)
        {
              attr=(VAttribute)value; 
        }
    
//        Global.debugPrintln("TableMouseEventHandler","rowNr="+pos.row);
//        Global.debugPrintln("TableMouseEventHandler","columnNr="+pos.column);
//        Global.debugPrintln("TableMouseEventHandler","columnName="+pos.columnName);
        
        if (pos.row<0) 
            return;
         
        if ( (GuiSettings.isAction(e)) && (comp instanceof JTable) )
        {
            // only start new browser for click on Name or Icon column
            if ( (pos.columnName!=null) 
               &&
                 ( (pos.columnName.compareTo(VAttributeConstants.ATTR_NAME)==0) 
                   ||(pos.columnName.compareTo(VAttributeConstants.ATTR_ICON)==0) )
               )
            {
            	 // check click on table cell  
            	ProxyNode node=this.tablePanel.getPNodeAt(e.getPoint());
            	VDummyRowObject dummyRow=new VDummyRowObject(tablePanel,this.masterBrowser,node.getResourceRef()); 
            	
                ActionCommand viewCmd=new ActionCommand(ActionCommandType.ACTIONCLICK); 
                masterBrowser.performAction(dummyRow,viewCmd);
            }
            // special case: click on VRL: !
            else if ((attr!=null) && (attr.getType()==VAttributeType.VRL))
            {
                try
                {
                    VRL vrl=attr.getVRL();
                
                    // send browser controller hyperlink event
                    ViewerEvent event=ViewerEvent.createHyperLinkEvent(null,vrl); 
                
                   masterBrowser.notifyHyperLinkEvent(event);
                }
                catch (Exception ex)
                {
                    masterBrowser.handle(ex); 
                }
   
            }
            else
            {
                // action click on BIG attribute text, show:  
                if ((attr!=null) &&(attr.getStringValue().length()>Presentation.getBigStringSize())) 
                    AttributeViewer.viewAttribute(attr);
            }
            
        }
       
    }

    private TablePos getTablePositionOf(MouseEvent e)
    {
        TablePos pos=new TablePos(); 
        
        pos.row=tablePanel.rowAtPoint(e.getPoint());
        
        // note that the actual comlumn order might be different, because
        // the 'view' allowes reordering of columns ! 
        TableColumnModel columnModel = tablePanel.getColumnModel();
        
        if (tablePanel.contains(e.getX(),e.getY())==false) 
            return null; 
        
        pos.column=columnModel.getColumnIndexAtX(e.getX());
        
        if (pos.column<0) 
        	return null; 
        
        TableColumn column = columnModel.getColumn(pos.column); 
        pos.columnName=(String)column.getHeaderValue();
        
        return pos;
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
        
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
        TablePos pos = getTablePositionOf(e); 
        this.tablePanel.onMouseOver(pos); 
    }

}
