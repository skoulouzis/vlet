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
 * $Id: VRSTableModel.java,v 1.6 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.util.QSort;
import nl.uva.vlet.vrl.VRL;

/**
 * Simple TableModel for the TablePanel. 
 * It consists of a matrix of attributes. 
 *  
 */

public class VRSTableModel extends AbstractTableModel // implements IProxyModel
{
    // === class ===
    private static final long serialVersionUID = -1721698802548910305L;
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRSTableModel.class); 
        logger.setLevelToDebug();
    }
    
    // need better data structure 
    public static Vector<Object> attr2vector(Object[] objarr) 
    {
    	Vector<Object> objs=new Vector<Object>();
    	
    	for (Object obj:objarr)
    	{
    		objs.add(obj); 
    	}
    	
		return objs;
	}
    
   // private static long rowIDCounter=1;
    
    public class RowObject
    {
    	//private long id=rowIDCounter++; 
    	
    	ProxyNode pnode; 
    	
    	Vector<Object> rowData=new Vector<Object>(1);
    	
    	/** Row Object with OPTIONAL node */ 
    	public RowObject(ProxyNode node)
    	{
    		pnode=node;
    		rowData=new Vector<Object>(); 
    	}
    	
    	Object get(int colNr)
    	{
    		if ((colNr<0) || (colNr>=rowData.size()))
    		{
    			logger.warnPrintf("Column number out of bounds:%d\n",colNr); 
    		}
    		return rowData.get(colNr); 
    	}

		public void set(int i, Object obj) 
		{
			rowData.set(i,obj); 
		}

		public void insertElementAt(int index, Object obj) 
		{
			rowData.insertElementAt(obj, index);
			
		}

		public void remove(int num) 
		{
			rowData.remove(num); 
		}

		public void setProxyNode(ProxyNode node)
		{
			pnode=node; 
		}

		public ProxyNode getProxyNode() 
		{
			return pnode; 
		}

		public boolean hasVRL(VRL vrl)
		{
			if (pnode!=null)
				if (pnode.getVRL().equals(vrl)) 
					return true; 
			
			return false;
		}

		public void setData(Object[] values)
		{
			this.rowData=new Vector<Object>(values.length);
			
			for (Object val:values)
				rowData.add(val); 
		}

		public int size() 
		{
			return rowData.size(); 
		}

		public VAttribute getAttribute(int j) 
		{
			if ((j<0) || (j>=rowData.size()))
			{
				logger.warnPrintf("column index out of bounds:%s\n",j);
				return null; 
			}
			
			Object obj=rowData.elementAt(j);
			
			if (obj instanceof VAttribute)
			{
				return (VAttribute)obj;
			}
			else
			{
				Global.errorPrintf(this,"Cell is NOT an VAttribute:%s\n",obj);
				return null; 
			}
		}
    }
    
    // === object === 
    // Todo: remove reference to tablePanel: One model can be viewed
    // by many viewers ! 
    private final TablePanel tablePanel;

    
    Vector<String> headers=null;
    
    // is used as mutex as well ! 
    private Vector<RowObject> rowObjects; 

    public boolean isCellEditable(int row, int col) 
    {
        // Only attribute are editable: 
        VAttribute attr=getAttribute(row,col);
        boolean val=false; 
        
        if (attr!=null) 
            val=attr.isEditable();
        
        //Global.debugPrintln(this,"IsEditable:"+row+","+col+"=attr"+attr+"="+val);
        
        return val; 
    }
    
    public RowObject getRow(int row)
    {
    	synchronized(rowObjects)
    	{
    		if ((row<0) || (row>=rowObjects.size()))
    		{
    			logger.warnPrintf("Row out of bounds (row)%d>(size)%d\n",row,rowObjects.size());
    			return null; 
    		}
    		
    	   	return this.rowObjects.get(row);
    	}
    }
    
	private RowObject getRowByVRL(VRL vrl) 
	{
		synchronized(rowObjects)
    	{
			for (RowObject row:rowObjects)
			{
				if (row.hasVRL(vrl)) 
					return row; 
			}
    	}
		
		return null; 
	}

	
    private VAttribute getAttribute(int row, int col)
    {
    	RowObject rowObj = getRow(row); 
    	if (rowObj==null)
    		return null; 
    	
        Object obj=rowObj.get(col); 
        
        if (obj instanceof VAttribute) 
            return (VAttribute)obj; 
            
        return null;
    }

    public VRSTableModel(TablePanel panel, Object[][] body, String[] headerNames,ProxyNode rowobjects[])
    {
        tablePanel = panel;
        // super(body, header);
        setData(body,headerNames,rowobjects); 
    }
    
    public VRSTableModel(TablePanel panel)
    {
        tablePanel = panel;
    }
    
    public void setData(Object body[][],String headerNames[],ProxyNode rowNodes[])
    {
        headers=new Vector<String>();  
        
        int rows=body.length;
        int columns=headerNames.length; 
        
        // set headers:
        headers.setSize(columns);
        
        for (int i=0;i<columns;i++)
        {
            headers.set(i,headerNames[i]); 
        }
            
        rowObjects=new Vector<RowObject>(rows); 
        
        for (int i=0;i<rows;i++)
        {	
        	int cols=body[i].length;
        	//preset size ? 
        	rowObjects.setSize(rows); 
        	// Row Node are Optional: ACL editor doesn't use them ! 
        	if (rowNodes!=null)
        		rowObjects.set(i,new RowObject(rowNodes[i])); // new column vector 
        	else
        		rowObjects.set(i,new RowObject(null));
        	RowObject rowObject=rowObjects.get(i);
        	// set row data: 
        	rowObject.setData(body[i]); 
            	
        }
        
        this.fireTableStructureChanged();
    }

    // PdB: example from internjet:
    // This method returns the Class object of the first
    // cell in specified column in the table model.
    // Unless this method is overridden, all values are
    // assumed to be the type Object.

    public Class getColumnClass(int columnIndex)
    {
        Object o = getValueAt(0, columnIndex);
        if (o == null)
        {
            return Object.class;
        }
        else
        {
            return o.getClass();
        }
    }
    
    public String getColumnName(int column) 
    { 
        return headers.elementAt(column);  
    }
    
    public int getColumnNr(String name)  
    { 
        int i=0; 
        
        for (String colName:headers)
        {
            if (colName.compareTo(name)==0) 
                return i;
            i++; 
        }
        
        return -1; 
    }

    public int getRowCount()
    {
        return this.rowObjects.size(); 
        
    }

    public int getColumnCount()
    {
        return headers.size(); 
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
    	RowObject row = getRow(rowIndex); 
    	
    	if (row==null)
    		return null;
    	
    	return row.get(columnIndex); 
    }
    
    /** Sets  value */ 
    
    public void setValueAt(Object value,int rowNr, int colNr)
    {
    	RowObject row = getRow(rowNr);
    	
    	if (row==null)
    	{
    		logger.warnPrintf("Index out of bound. ignoring setData(row,col)[%d,%d]='%s'\n",rowNr,colNr,value); 
    		return;
    	}
        
        // Current (default) CellEditors use String as value 
        
        if (value instanceof String)
        {
           VAttribute attr=getAttribute(rowNr,colNr);
        
           if (attr!=null)
           {
        	   attr.setValue((String)value);
           }
           
        }
        else 
        {
           row.set(colNr,value); 
        }
        
        // optimization note: table will collect multiple events
        // and do the drawing at once. 
        
        this.fireTableCellUpdated(rowNr,colNr); 
        
    }
    
//    public void removeRow(int rowNr)
//    {
//    	synchronized(rowIndexObjects)
//    	{
//    		data.remove(rowNr); 
//    		this.rowIndexObjects.remove(rowNr); 
//    	}
//    	
//        // [from,to] inclusive 
//        this.fireTableRowsDeleted(rowNr,rowNr); 
//    }
   

    public void addColumn(String attrName)
    {
        insertColumn(getColumnCount()-1,attrName);
    }
    
    /**
     * Inserts new column after header index 'index'. 
     * Again this method use the header index. 
     * Actual column nr as show might differ since 
     * the use might have move the column. 
     * TODO: update header list after a column move. 
     */ 
    
    public void insertColumn(int index,String attrName)
    {
        // insert headers: 
        headers.insertElementAt(attrName,index); 
       
        // expand data matrix and fill it with somthing  
        for (RowObject row:rowObjects)
        {
            // insert data 
            row.insertElementAt(index,null);  
        }
        
        this.fireTableStructureChanged();
    }

    /**
     *  Returns Current shown headers in table model order. 
     *  Note that the View might order might be different !  
     *  @return Vector list of header names. 
     */
    public Vector<String> getHeaders()
    {
        return headers; 
    }
    
    /**
     *  Returns Current shown headers in table model order. 
     *  Note that the View might order might be different !  
     *  @return Arraylist of header names. 
     */
    public String[] getHeadersAsArray()
    {
        String arr[]=new String[headers.size()];
        
        for (int i=0;i<headers.size();i++)
            arr[i]=headers.elementAt(i);
        
        return arr; 
    }

    public void removeColumn(String name)
    {
        int num=getHeaderIndex(name); 
        // expand data matrix and fill it with something  
        for (RowObject row:rowObjects)
        {
            row.remove(num); 
        }
        
        headers.remove(num); 
        
        fireTableStructureChanged();
    }
    
    /**
     * This method returns the index of the header as stored 
     * in the headers vector. Actual Column index might differ
     * if the user has moved columns ! (View vs Model !) 
     * Use header names as much as possible. 
     * @param name
     * @return
     */
    public int getHeaderIndex(String name)
    {
        for (int i=0;i<getColumnCount();i++) 
            
            if (headers.elementAt(i).compareTo(name)==0)
                return i;
        
        return -1; 
    }
    
    public int[] sortBy(String name, boolean reverse)
    {
    	//debug("sortBy:"+name+", reverse="+reverse); 
    	
        int colnr=getHeaderIndex(name);    
        
        if (colnr<0) 
            return null; 
        
    	//debug("sortBy column number="+colnr);
    	
        TableRowComparer comparer=new TableRowComparer(colnr,reverse); 
        QSort sorter=new QSort(comparer); 
       	
        int mapping[];
       	
        synchronized(rowObjects)
        {
        	// in memory sort ! 
        	mapping=sorter.sort(rowObjects);
        }
        
        this.fireTableDataChanged();
        return mapping; 
    }
    
    
    /** Sets custom object associated with the row */ 
    public void setRowIndexObject(int rowNr,ProxyNode node)
    {
    	RowObject row=getRow(rowNr); 
    	row.setProxyNode(node); 
 
        this.fireTableDataChanged();
    }
    
    /** Gets the custom object associated with the row */ 
    public ProxyNode getRowIndexObject(int rowNr)
    {
		RowObject row=getRow(rowNr);
		
		if (row==null)
			return null;
		
		return row.getProxyNode();
    }

    /** Return ProxyNode with specified VRL or null */ 
	public ProxyNode getRowObjectWithVRL(VRL vrl)
	{
		RowObject row=getRowByVRL(vrl);
		
		if (row==null)
			return null;
		
		return row.getProxyNode();
 	}
   

	/**
	 * Remove multiple rows. 
	 * Make sure row indices are in ascending order ! 
	 * Thus if: y>x then row[y]>row[x] ! 
	 * 
	 * @param rows
	 */
    public void deleteRows(int[] rows)
    {
    	synchronized(rowObjects)
    	{	
           	// start with LAST and move to first ! 
    		for (int i=rows.length-1;i>=0;i--)
    		{
    			this.removeRow(rows[i]); 
    		}
    	}
    	
        fireTableStructureChanged();
    }

    private boolean removeRow(int i) 
    {
    	synchronized(rowObjects)
    	{
    		if ((i<0) || i >=rowObjects.size())
    		{
    			logger.warnPrintf("removeRow: Index out of bounds:%d\n",i);
    			return false;
    		}
    	
    		rowObjects.remove(i);
    	}
    	
		this.fireTableRowsDeleted(i,i);
		return true; 
	}
   
    /**
     * Add new row with specfied data. 
     * If node alread exist new row isn't added 
     * @param rowNode
     * @param attrs
     */
   public boolean addNodeRow(ProxyNode node,Object attrs[])
   {
       //debug("New proxynode row:"+node);
       // Use VAttributeSet as factory 
	
       int rownr=0; 
    	    
       synchronized(this.rowObjects)
       {
    		RowObject row = this.getRowByVRL(node.getVRL()); 

    		if (row!=null)
    		{
    			//Global.debugPrintln(this,"Ignoring existing rowNode:"+node); 
    			return false;  
    		}
        	
    		row=new RowObject(node); 
    		row.setData(attrs); 
    		rowObjects.add(row); 
    	}
    	
    	fireTableRowsInserted(rownr, rownr);
    	
    	return true; 
   }

	public VAttribute[][] getData()
    {
        int nrRows=getRowCount(); 
        
        VAttribute attrs[][]=new VAttribute[nrRows][]; 
        
        synchronized(rowObjects)
        {
        	for (int i=0;i<nrRows;i++)
        	{
        		RowObject row=rowObjects.get(i); 
        		
        		int nrFields=row.size(); 
            
        		attrs[i]=new VAttribute[nrFields]; 
            
        		for (int j=0;j<nrFields;j++)
        		{
        			attrs[i][j]=(VAttribute)row.getAttribute(j); 
        		}
        	}
        }
        
        return attrs;
    }
    
	public ViewModel getViewModel()
	{
		return tablePanel.getViewModel(); 
	}

	public ViewNode getRootViewItem()
	{
		return this.tablePanel.getDataProducer().getRootViewItem(); 
	}

	public ProxyNode[] getRowObjects()
	{
		synchronized(this.rowObjects)
		{
			ProxyNode nodes[]=new ProxyNode[this.rowObjects.size()];
			for (int i=0;i<rowObjects.size();i++) 
				nodes[i]=rowObjects.get(i).pnode;   
			return nodes; 
		}
		
	}

	public boolean removeNode(VRL vrl) 
	{
		boolean removed=false; 
		int index=-1; 
		
		synchronized(this.rowObjects)
		{
			for (int i=0;i<rowObjects.size()&&removed==false;i++) 	
			{
				if (rowObjects.get(i).hasVRL(vrl))
				{
					rowObjects.remove(i);
					//debug("removeNode: removed:"+vrl);
 					removed = true;
 					index=i; 
					break; 
				}
			}
		}
		
		if (removed)
		{
			this.fireTableRowsDeleted(index,index);  
		}
		else
		{	
			logger.warnPrintf("removeNode: couldn't find node:%s\n",vrl);
		}
		
		return removed;  
	}
   
}