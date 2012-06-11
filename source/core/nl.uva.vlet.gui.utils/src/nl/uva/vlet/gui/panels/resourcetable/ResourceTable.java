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
 * $Id: ResourceTable.java,v 1.5 2011-04-18 12:27:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:29 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.resourcetable;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.presentation.Presentation;

/** 
 * Generic Resource Table. 
 * Rewrite of VBrowser Table. 
 * @author Piter T. de Boer 
 *
 */
public class ResourceTable extends JTable
{
	private static final long serialVersionUID = -8190587704685619938L;
  
    // default presentation
    private Presentation presentation=null; 
    private TablePopupMenu popupMenu=null;
    private boolean isEditable=true;
    protected int defaultColumnWidth=80;
    
    private MouseListener mouseListener; 
	
	public ResourceTable()
	{
	    // defaults 
	    super(new ResourceTableModel()); 
	    init(); 
	}
	
    public ResourceTable(ResourceTableModel dataModel)
    {
        // defaults 
        super(dataModel); 
        init(); 
    }
    
    public ResourceTable(ResourceTableModel dataModel,Presentation presentation)
    {
        // defaults 
        super(dataModel);
        this.presentation=presentation; 
        init(); 
    }

    public ResourceTableModel getModel()
	{
	   return (ResourceTableModel)super.getModel(); 
	}
	
	public void setDataModel(ResourceTableModel dataModel)
	{
	    this.setModel(dataModel); 
	    initColumns(); 
	}
	
	public void refreshAll()
	{
	    init(); 
	}

	private void init()
	{
	    this.setAutoCreateColumnsFromModel(false);
	    //this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	    
	    this.setColumnSelectionAllowed(true);
 	    this.setRowSelectionAllowed(true);

 	    initColumns();
 	    initListeners();
	}
	
	protected void initListeners()
	{
 	    // Listeners ! 
        JTableHeader header = this.getTableHeader();
        mouseListener = new TableMouseListener(this);
        header.addMouseListener(mouseListener);
        this.addMouseListener(mouseListener);
        // default popup menu
        this.popupMenu=new TablePopupMenu();
	}
	
	/** (re)Created columns from headers taken from DataModel */ 
    public void initColumns()
    {
        String headers[]=getModel().getHeaders(); 
        this.getPresentation().setChildAttributeNames(headers);
        // Use order from presentation 
        initColumns(getPresentation().getChildAttributeNames());
    }
    
    public boolean isEditable()
    {
        return isEditable;
    }
     
    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }
    
    private void updateCellEditors()
    {
        // set cell editors:
        TableColumnModel cmodel = getColumnModel(); 
        
        int nrcs=cmodel.getColumnCount(); 

        for (int i=0;i<nrcs;i++)
        {
           TableColumn column = cmodel.getColumn(i);
           Object obj=getModel().getValueAt(0,i); 
           
           if (obj instanceof VAttribute)
           {
               VAttribute attr=(VAttribute)obj;
               
               if (attr.isEditable()==true)
               {
                   switch(attr.getType())
                   {
                       // both boolean and enum use same select box 
                       case ENUM: 
                       case BOOLEAN:
                       {
                          debug("setting celleditor to EnumCellEditor of columnr:"+i);
                          column.setCellEditor(new EnumCellEditor(attr.getEnumValues()));
                          break;
                       }
                       case STRING: 
                       {
                           column.setCellEditor(new DefaultCellEditor(new JTextField())); 
                       }
                       default: 
                           break; 
                   }                
               }
           }
        }
    }
    
	/** Return DEFAULT Table Mouse Listener */ 
	public MouseListener getMouseListener()
	{
	    return this.mouseListener; 
	}
	
	public ResourceTableModel getResourceTableModel()
	{
	    TableModel model = super.getModel(); 
	        
	    if (model instanceof ResourceTableModel)
	    {
	        return (ResourceTableModel)model; 
        }
	        
        throw new Error("Resource Table NOT initialized with compatible Table Model!:"+model.getClass()); 
	}

	   
    private void initColumns(String headers[])
    {
        setAutoResizeMode(getPresentation().getColumnsAutoResizeMode());
                
        TableColumnModel columnModel=new DefaultTableColumnModel();

        for (int i=0;i<headers.length;i++)
        {
            debug("Creating new column:"+headers[i]);
            TableColumn column = createColumn(i,headers[i]);
            // update column width from presentation
            Integer prefWidth=getPresentation().getAttributePreferredWidth(headers[i]);
            if (prefWidth==null)
                prefWidth=headers[i].length()*10;// 10 points font ? 
            
            column.setPreferredWidth(prefWidth);
            
            if (prefWidth<50) 
                column.setResizable(false); 
            
            columnModel.addColumn(column); 
        }
        
        this.setColumnModel(columnModel);
        if (this.isEditable)
            this.updateCellEditors(); 
    }
    
    private TableColumn createColumn(int modelIndex,String headerName)
    {
        // one renderer per column 
        ResourceTableCellRenderer renderer= new ResourceTableCellRenderer();
        
        TableColumn column = new TableColumn(modelIndex,10,renderer, null);
        column.setIdentifier(headerName); 
        column.setHeaderValue(headerName);
        column.setCellRenderer(renderer); 
        // update presentation
        Integer size=getPresentation().getAttributePreferredWidth(headerName);
        if (size!=null)
            column.setWidth(size); 
        else
            column.setWidth(defaultColumnWidth); 
        
        return column; 
    }
     
    
    private void debug(String msg) 
    {
       Global.debugPrintln(this,msg); 
    }
    
    /** Returns headers as defined in the DATA model */ 
    public String[] getDataModelHeaders()
    {
        return getModel().getHeaders(); 
    }

    public int getDataModelHeaderIndex(String name)
    {
        return getModel().getHeaderIndex(name); 
    }

    /**
     * Get the header names as shown, thus in the order 
     * as used in the VIEW model  (Not dataModel).   
     */
    public StringList getColumnHeaders()
    {
        // get columns headers as currently shown in the VIEW model 
        TableColumnModel colModel = this.getColumnModel(); 
        int len=colModel.getColumnCount(); 
        StringList names=new StringList(len); 
        
        for (int i=0;i<len;i++)
            names.add(colModel.getColumn(i).getHeaderValue().toString()); 
        
        return names; 
    }
    
    /** 
     * Insert new column after specified 'headerName'. 
     * This will insert a new headername but use the current header as viewed as new
     * order so the new headers and column order is the same as currently viewed. 
     * This because the user might have switched columns in the VIEW order of the table. 
     */   
    public void insertColumn(String headerName, String newName, boolean insertBefore)
    {
        if (this.getHeaderModel().isEditable()==false)
            return; 
        
        // remove column but use order of columns as currently viewed ! 
        StringList viewHeaders=this.getColumnHeaders(); 
        if (insertBefore)   
            viewHeaders.insertBefore(headerName,newName); 
        else
            viewHeaders.insertAfter(headerName,newName);
        
        // insert empty column and fire change event. This will update the table. 
        this.getModel().setHeaders(viewHeaders); 
    }

    public void removeColumn(String headerName)
    {
        if (this.getHeaderModel().isEditable()==false)
            return; 
        
        // remove column but use order of columns as currently viewed ! 
        StringList viewHeaders=this.getColumnHeaders(); 
        viewHeaders.remove(headerName); 
        
        // triggers restructure, and KEEP the current view order of Columns. 
        this.getModel().setHeaders(viewHeaders);
        this.presentation.setChildAttributeNames(viewHeaders.toArray());
        this.getModel().fireTableStructureChanged(); 
    }
    
    public HeaderModel getHeaderModel()
    {
        return this.getModel().getHeaderModel(); 
    }
    
    public void tableChanged(TableModelEvent e)
    {   
        if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW)
        {
            initColumns(); 
        }
        super.tableChanged(e); 
    }

    /** Has visible column (Must exist in columnmodel!) */  
    public boolean hasColumn(String headerName)
    {
        Enumeration enumeration = getColumnModel().getColumns();
        TableColumn aColumn;
        int index = 0;

        while (enumeration.hasMoreElements()) 
        {
            aColumn = (TableColumn)enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (StringUtil.equals(headerName,aColumn.getHeaderValue().toString()))
                return true;
            index++;
        }
        
        return false; 
    }
    
    /** Has visible column (Must exist in columnmodel!) */  
    public TableColumn getColumnByHeader(String headerName)
    {
        Enumeration enumeration = getColumnModel().getColumns();
        
        while (enumeration.hasMoreElements()) 
        {
            TableColumn col = (TableColumn)enumeration.nextElement();
            // Compare them this way in case the column's identifier is null.
            if (StringUtil.equals(headerName,col.getHeaderValue().toString()))
                return col; 
        }
        
        return null;  
    }

    public TablePopupMenu getPopupMenu(MouseEvent e, boolean canvasMenu) 
    {
        if (popupMenu!=null)
            popupMenu.updateFor(this,e,canvasMenu); 
        return popupMenu; 
    }

    public void setPopupMenu(TablePopupMenu menu)
    {
       this.popupMenu=menu; 
    }

    /** Return row Key under Point point. Might return NULL */ 
    public String getKeyUnder(Point point)
    {
        if (point==null)
            return null; 
        int row=rowAtPoint(point); 
        if (row<0)
            return null; 
        return getModel().getRowKey(row); 
    }

    public Presentation getPresentation()
    {
        if (this.presentation==null)
            presentation=new Presentation(); 
        
       return this.presentation; 
    }

    public void dispose()
    {
        
    }

    public void setPresentation(Presentation newPresentation)
    {
        this.presentation=newPresentation;
        this.refreshAll();
    }

    public void sortColumn(String name)
    {
        ; // 
    }
 
}
