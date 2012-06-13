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
 * $Id: TablePanel.java,v 1.7 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dnd.VDragGestureListener;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.table.VTableController.TablePos;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewFilter;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.vrl.VRL;

/**
 * The Table View Panel.
 *  
 * Uses VRSTableModel as table custom model. 
 * Note that the tablePanel can have a different column order
 * as used in the DataModel. 
 * 
 * Todo: TablePanelController  
 * 
 * @see VRSTableModel
 * @see HeaderPopupMenu
 * @see TabelPanelListener
 * @author Piter T. de Boer
 *
 */
public class TablePanel extends JTable implements VContainer,VPresentable
{
    /**  */
	private static final long serialVersionUID = 8205670384033614004L;
	
	// == instance == // 
	
    private VRSTableModel tableModel;
    public VTableController tableController;
    /** Listener for module/column events */ 
    private TableModelEventHandler tableModelListener;
    private VAttributeCellRenderer defaultCellRenderer;
    private String sortColumnName;
    private boolean reversedSort;
    MasterBrowser browserController;
    private TableDataProducer dataProducer;
    private Presentation presentation=null;
	private DragSource dragSource;
	private VDragGestureListener dgListener;

	private String resourceType;
    
    /*
     * dummy Contructor used by Jigloo !
     * 
     */
    public TablePanel()
    {
        super();
        TableModel tableTabModel = new DefaultTableModel(
                new String[][]
                  {
                      { "object-1", "attribute-1" },
                      { "object-2", "attribute-2" }
                  }, 
                new String[] 
                  { "Object", "Attribute"}
               );
        
        setModel(tableTabModel);
        
        init(null); 
    }

    private void init(BrowserController bc)
    {
        this.browserController = bc;
        
        setName("TablePanel");
        
        // current Event model is that TablePanels listen to ALL ProxyNode
        // Event:

        // behave as list: 
        setColumnSelectionAllowed(false);
        this.setRowSelectionAllowed(true); 
        
        setAutoCreateColumnsFromModel(true);
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // add listener to table 
        tableController=new VTableController(this,bc);
        addMouseListener(tableController); 
        addMouseMotionListener(tableController);
      
        // add listeners to table header: 
        JTableHeader header = this.getTableHeader(); 
        
        header.addMouseListener(this.tableController);
        // this.setDefaultRenderer(String.class,NodeTableCellRenderer.class);
        
        initDND();
    }
    
    private void initDND()
    {
        this.setTransferHandler(VTransferHandler.getDefault()); 
        
        this.dragSource = DragSource.getDefaultDragSource();
        this.dgListener = new VDragGestureListener();
        //this.dsListener = MyDragSourceListener.getDefault(); 
        // component, action, listener
        this.dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, this.dgListener );
    }

    public TablePanel(BrowserController bc)
    {
        super();
        init(bc);
    }
    
   
    // setters getters:
    
    VRSTableModel getVRSTableModel()
    {   
        TableModel model = getModel();
        
        if (model instanceof VRSTableModel)
            return (VRSTableModel)getModel();
        else
        {
            // asynchronous access: table not yet properly initialized"
           Global.warnPrintf(this,"Table not yet initialized ???\n"); 
           return null; 
        }
    }
 // =======================================================================
 // Table Methods 
 // =======================================================================
    
    /**
     * When the autoCreateColumns is on, default cell 
     * renderers are creates. This method overrides them */  
    void updateCellRenderers()
    {
        TableColumn column=null; 
        
        for (int i=0;i<columnModel.getColumnCount();i++)
        {
            column=columnModel.getColumn(i);
            //column.setWidth(getDefaultWidth(column.getHeaderValue().toString()));
            column.setCellRenderer(defaultCellRenderer);
        }
    }
    
    // when I have to create my own columns: 
    
    private TableColumnModel createColumns(String headers[])
    {
        TableColumnModel columnModel=new DefaultTableColumnModel();

        for (int i=0;i<headers.length;i++)
        {
            TableColumn column = createColumn(i,headers[i]);
            columnModel.addColumn(column); 
        }
        return columnModel; 
    }
    
    private TableColumn createColumn(int modelIndex,String headerName)
    {
        // one renderer per column 
        VAttributeCellRenderer renderer= new VAttributeCellRenderer(this);
        
        TableColumn column = new TableColumn(modelIndex,10,renderer, null);
        column.setHeaderValue(headerName);
        column.setCellRenderer(renderer); 
        column.setWidth(column.getPreferredWidth());
        
        return column; 
    }
    
//  ============================================================================
//  Non Critical Methods (?)  
//  ============================================================================

    void handle(VlException e)
    {
        if (browserController!=null)
        {
            browserController.handle(e);
        }
        else
        {
            // for testing: 
            Global.logException(ClassLogger.ERROR,this,e,"TablePanel Exception!\n");  
        }
    }
    
    public void showHeaderMenuIn(Component comp, String name,int x, int y) 
    {
        HeaderPopupMenu popupMenu=new HeaderPopupMenu(this,name); 
        
        popupMenu.show(comp,x,y);
    }
   
    public void sortBy(String columnName,boolean reverse)
    {
        int mapping[]=tableModel.sortBy(columnName,reverse);
    }

    public void setSortColumnName(String columnName)
    {
        this.sortColumnName=columnName; 
    }

    public String getSortColumnName()
    {
        return this.sortColumnName;
    }

    public void reverseSortOrder()
    {
        reversedSort=(reversedSort==false); 
    }

    public boolean getReverseSort()
    {
        return reversedSort; 
    }

    public void setReverseSort(boolean b)
    {
        reversedSort=b; 
    }
    
    // ========================================================================
    //
    // ========================================================================
   
    public void populateWith(ProxyNode pnode) throws VlException 
    {
        if ((dataProducer instanceof NodeTableProducer)==false)
        {
        	// create empty model, will be populated later !
        	VRSTableModel model=new VRSTableModel(this); 
            dataProducer = new NodeTableProducer(this,ProxyNode.getProxyNodeFactory(),model);
        }
        else
        {
        }
        
        dataProducer.setNode(pnode); 
        dataProducer.backgroundCreateTable(); 
        presentation=dataProducer.getPresentation();
        
        this.resourceType=pnode.getType(); 
    }
       
    /**
     * UI Save Create table from object matrix. 
     * Will autodetect VAttribute matrix, but VAttributes in the same column
     * MUST be of same Type and Name ! 
     * 
     * @param body
     * @param headers
     */
    public void uiCreateTable(final Object[][] body,final String[] headers,final ProxyNode rowobjects[])
    {
        // debug("uiCreateTable: (I):"+Thread.currentThread().getId());
    	// ===
    	// ui thread:
    	// ===
    	
    	if (UIGlobal.isGuiThread()==false)
    	{
    		// debug("uiCreateTable: (IIa) is not gui invoke later ");
    		Runnable createTask=new Runnable()
    		{
    			public void run()
    			{
    				uiCreateTable(body,headers,rowobjects);
    			}

    		};
    		
    		UIGlobal.swingInvokeLater(createTask); 
    		return;
    	}
    	
    	//debug("uiCreateTable: (IIb) is gui thread");
    	// ==
    	
        // keep copy since, even when removed!, the listeners STILL receive change events ! 
        // (And the hashtable get modified)
        
        Presentation pres=this.presentation; 
        this.presentation=null; // block presentation changes during createTable ! 
        
        // remove previous listener to protect it from a overload: 
        if (tableModel!=null) 
            tableModel.removeTableModelListener(tableModelListener);
        
        if (columnModel!=null) 
            columnModel.removeColumnModelListener(tableModelListener);
        
        removeAll();
        
        // auto reize default to off:
        
         // create/update table model: 
          setAutoCreateColumnsFromModel(true);
         //setAutoCreateColumnsFromModel(false);
        
        tableModel = new VRSTableModel(this, body, headers,rowobjects);
        tableModelListener = new TableModelEventHandler(this);  
        tableModel.addTableModelListener(tableModelListener);
       
        this.setModel(tableModel);
        
        // one node table cell renderer for this table: 
        
        this.defaultCellRenderer= new VAttributeCellRenderer(this);
        
        if (this.getAutoCreateColumnsFromModel()==false)
        {
            /** if column needs to be created, but then you have to do EVERYTHING... */ 

            // create headers: 
            columnModel=createColumns(headers); 
            columnModel.addColumnModelListener(tableModelListener);
               setColumnModel(columnModel);
        
            JTableHeader tableHeader=new JTableHeader(columnModel); 
            this.setTableHeader(tableHeader);
        }
        else
        {
            // only update/change the table cell renderers when the table is auto-created. 
            updateCellRenderers();
            columnModel = this.getColumnModel();
            columnModel.addColumnModelListener(tableModelListener);
            
            // update columns with stored width from presentation: 
            if (pres!=null)
            {
              for (int i=0;i<columnModel.getColumnCount();i++) 
              {
                  // restore column width
                  TableColumn column = columnModel.getColumn(i);
                  String name=column.getHeaderValue().toString(); 
                  Integer w=pres.getAttributePreferredWidth(name);
                
                  // Global.debugPrintln(this,"setting column width of "+name+" to:"+w);
                
                  if (w!=null)
                      column.setPreferredWidth(w);
              }
            }  
        }
        
        if ((body==null) || (body.length==0))
            return; // empty table: nothing todo
        
        updateCellEditors();
        this.presentation=pres; // restore (previous) presentation  
    }
        
    public void updateCellEditors()
    {
        // set cell editors:
        TableColumnModel cmodel = getColumnModel(); 
        
        int nrcs=cmodel.getColumnCount(); 

        for (int i=0;i<nrcs;i++)
        {
           TableColumn column = cmodel.getColumn(i);
           Object obj=getVRSTableModel().getValueAt(0,i); 
           
           if (obj instanceof VAttribute)
           {
               VAttribute attr=(VAttribute)obj;
               
               if (attr.isEditable()==true)
               {
                   switch(attr.getType())
                   {
                       case ENUM: 
                           // boolean does autocast to "true" and "false"
                       case BOOLEAN:
                       {
                          //debug("setting celleditor to EnumCellEditor of columnr:"+i);
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
    
    /** Returns list of ALL possible attribute names */ 
    public String[] getAllHeaderNames()
    {
        return dataProducer.getAllHeaderNames();  
    }

    public VRL getNodeVRLAt(Point point)
    {
    	ProxyNode node=getPNodeAt(point); 
    	
    	if (node!=null) 
    		return node.getVRL();
        
        return null; 
    }
    
    public ProxyNode getPNodeAt(Point point)
    {
    	int row = rowAtPoint(point);
    	        
    	if (row >= 0)
    		return (ProxyNode) getVRSTableModel().getRowIndexObject(row);
    	        
        return null; 
    }
    
    public ProxyNode getPNodeWithVRL(VRL vrl)
    {
    	return (ProxyNode) getVRSTableModel().getRowObjectWithVRL(vrl); 
    }
    /**
     * Assumes symmetrical Matrix where all attributes in the same column 
     * have the same type/name 
     * 
     * @param attrs
     */
    public void setData(VAttribute[][] attrs)
    {
        if ((attrs==null) || (attrs[0]==null)) 
        {
            removeAll();
            revalidate();
            return; 
        }
        
        // use first row for headers/column info 
        
        int nrrows=attrs.length;
        int nrcols=attrs[0].length; 
        
        String headers[]=new String[nrcols];
        
        for (int i=0;i<nrcols;i++)
        {
            headers[i]=attrs[0][i].getName(); 
        }
        
        uiCreateTable(attrs,headers,null); 
    }

    public TableDataProducer getDataProducer()
    {
        return this.dataProducer; 
    }

    public void setTableProducer(TableDataProducer producer)
    {
        this.dataProducer=producer; 
    }

    /**
     * Get the header names as shown, thus in the order 
     * as used in the VIEW model  (Not dataModel).  
     * 
     * @return
     */
    public String[] getViewHeaderNames()
    {
        int len=this.getColumnCount();
        String names[]=new String[len]; 
        
        
        for (int i=0;i<len;i++)
        {
            names[i]=this.getColumnName(i); 
        }
        
        return names; 
    }

    public void deleteRows(int[] rows)
    {
       this.tableModel.deleteRows(rows); 
       
    }
    
    /** Insert new column after specified 'headerName' 
     * @throws VlException */
    public void insertColumn(String headerName, String newName)
    {
        // get order of header as currently viewed ! 
        String oldNames[]=getViewHeaderNames(); 
        int len=oldNames.length; 
        String newNames[]=new String[oldNames.length+1]; 
        
        int index=0; 
        
        for (int i=0;i<len;i++)
        {
            if (oldNames[i].compareTo(headerName)==0)
            {
                newNames[index++]=newName;
                newNames[index++]=oldNames[i]; 
            }
            else
            {
                newNames[index++]=oldNames[i];
            }
        }
        
        // Update presentation information: 
        
        if (presentation!=null)
            presentation.setChildAttributeNames(newNames); 
         
           
        // lazy update: recreate complete table: 
        dataProducer.backgroundCreateTable(); 
    }
    
    public void removeColumn(String name)
    {
        // get order of header as currently viewed ! 
        String oldNames[]=getViewHeaderNames(); 
        int len=oldNames.length; 
        String newNames[]=new String[oldNames.length-1]; 
        
        int index=0; 
        
        for (int i=0;i<len;i++)
        {
            if (oldNames[i].compareTo(name)==0)
            {
                ;
            }
            else
            {
                newNames[index++]=oldNames[i];
            }
        }
        
        // copy headers from view model to data producer 
        if (presentation!=null) 
            presentation.setChildAttributeNames(newNames); 
         
           
        // lazy update: recreate complete table: 
        dataProducer.backgroundCreateTable(); 
    }

    public void storeData()
    {
        dataProducer.storeTable(); 
    }

    public void dispose()
    {
        if (dataProducer!=null) 
                dataProducer.dispose();
    }
    
    public void storeColumnWidth(String name, int w)
    {
        if (presentation!=null) 
            presentation.setAttributePreferredWidth(name,w);
    }

    public Presentation getPresentation()
    {
       return presentation; 
    }

    public void onMouseOver(TablePos pos)
    {
    	if (pos==null) 
    		return; 
    	
        //this.changeSelection(pos.row,pos.column,false,false); 
        // no effects yet.
        //Debug("mouse over:"+pos.columnName+"#"+pos.row); 
    }
    
    // ====================================================================
    // VContainer interface 
    // ==================================================================== 

	public MasterBrowser getMasterBrowser()
	{
		return this.browserController; 
	}

	public VRL getVRL()
	{
		if ((dataProducer!=null) && (dataProducer.getNode()!=null)) 
			return this.dataProducer.getNode().getVRL(); 
		
		return null;
	}
	
	public ResourceRef getResourceRef()
	{
		if ((dataProducer!=null) && (dataProducer.getNode()!=null)) 
			return this.dataProducer.getNode().getResourceRef(); 
		
		return null;
	}
	
	public ResourceRef[] getSelection()
	{
		int[] rowsselected=getSelectedRows();
		// rows AND columns -> SpreadSheet export  
        //int[] colsselected=getSelectedColumns();

        if ((rowsselected==null) || (rowsselected.length<=0))
        	return null; // nothing selected 

        ResourceRef vrls[]=new ResourceRef[rowsselected.length];
        	
        int index=0; 
        
        for (int rownr:rowsselected)
        {     
        	ProxyNode node = this.tableModel.getRowIndexObject(rownr);
        	
        	if (node!=null)
        		vrls[index++]=node.getResourceRef();  
        }

		return vrls;  
	}

	/** Table has NO VContainer parent */ 
	public VContainer getVContainer()
	{
		return null;
	}

	public String getResourceType()
	{
		return this.resourceType; 
	}

	public VComponent getVComponent(ResourceRef ref)
	{
	  	ProxyNode node=getPNodeWithVRL(ref.getVRL());
	  	
	  	if (node==null)
	  		return null;
	  	
    	VDummyRowObject dummyRow=new VDummyRowObject(this,this.browserController,node.getResourceRef());
    	
    	return dummyRow; 
    	
	}

	public ViewFilter getViewFilter()
	{
		return this.getMasterBrowser().getViewModel().getViewFilter();
	}

	public ViewModel getViewModel()
	{
		return this.browserController.getViewModel();
	}

    @Override
    public void selectAll(boolean selectValue)
    {
        Global.warnPrintf(this,"Fixme:selectAll()"); 
    }

}
