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
 * $Id: ResourceTableModel.java,v 1.6 2011-06-10 10:18:00 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:00 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.resourcetable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.gui.view.ViewNode;


public class ResourceTableModel extends AbstractTableModel implements Iterable<ResourceTableModel.RowData>  
{
    private static final long serialVersionUID = 1987175001277362948L;
    private static ClassLogger logger; 

    static
    {
    	logger=ClassLogger.getLogger(ResourceTableModel.class);    	
    }
    
    public static ResourceTableModel createDefault() 
    {
        return new ResourceTableModel(); 
    }
    
    /** Resource Row Data */ 
    
	public class RowData
	{
	    // OPTIONAL vrl (if resource row has VRL) 
		private String rowKey; 
		
        private VAttributeSet rowAttributes=new VAttributeSet(); 

        private ViewNode viewItem; 
        
        public RowData(String rowKey, VAttributeSet attrs)
		{
			this.rowKey=rowKey;  

			if (attrs!=null)
			    this.rowAttributes=attrs.duplicate(); // empty set!
		}
        
        public String getKey()
        {
            return rowKey; 
        }
        
		public int getNrAttributes()
		{
		    return rowAttributes.size(); 
		}

        public VAttribute getAttribute(String name)
        {
            return rowAttributes.get(name); 
        }
        
        public VAttribute getAttribute(int nr)
        {
            String key=rowAttributes.getKey(nr);
            if (key==null)
                return null;
            return rowAttributes.get(key); 
        }

        public void updateData(VAttributeSet data, boolean merge)
        {
            if (merge==false)
            {
                this.rowAttributes=data.duplicate(); 
                return; 
            }
            
            // dump:
            this.rowAttributes.putAll(data); 
        }

        public void setValue(String attrName, String value)
        {
           this.rowAttributes.set(attrName,value); 
        }

        public void setValues(VAttribute[] attrs)
        {
            if (attrs==null)
                return; 
            
            for (VAttribute attr:attrs)
                this.rowAttributes.put(attr); 
        }

        public void removeValue(String attr)
        {
            this.rowAttributes.remove(attr); 
        }

        public int getIndex()
        {
            // check entry inside Table Rows !
            synchronized(ResourceTableModel.this.rows)
            {   
                return ResourceTableModel.this.rows.indexOf(this); 
            }
        }

        public String[] getAttributeNames()
        {
            return this.rowAttributes.getAttributeNames(); 
        }
        
        /** Returns optional viewItem */ 
        public ViewNode getViewItem()
        {
            return viewItem; 
        }
        
        public void setViewItem(ViewNode item)
        {
            this.viewItem=item; 
        }
        
	}
	
	// Synchronized Vector, default empty NOT null 
	protected Vector<RowData> rows=new Vector<RowData>();
	
	/** Mapping of Key String to row Index number */  
    private Map<String,Integer> rowKeyIndex=new Hashtable<String,Integer>();
    
	// all headers: default empty, NOT null 
    private HeaderModel headerModel=new HeaderModel(); 
    
    /** All potential headers */ 
    private StringList allHeaders=null;  
    
	public ResourceTableModel(String[] headers)
    {
        super(); 
        this.headerModel=new HeaderModel(headers); 
    }
	
	public ResourceTableModel()
	{
		super(); 
		String names[]={"Dummy1","Dummy2"}; 
		headerModel=new HeaderModel(names); 
		VAttributeSet dummySet=new VAttributeSet(); 
		dummySet.set("Dummy1","value1"); 
		dummySet.set("Dummy2","value2"); 
        
		this.rows.add(new RowData("row1",dummySet)); 
		this.rows.add(new RowData("row2",dummySet)); 
        
		// allow EMPTY table model: init with defaults!
	}
    
    public void setHeaders(String[] headers)
    {
        this.headerModel.setValues(headers); 
        this.fireTableStructureChanged();
    }
    
    public void setHeaders(StringList headers)
    {
        this.headerModel.setValues(headers);    
        this.fireTableStructureChanged(); 
    }

    
    @Override
	public int getColumnCount()
	{
        return headerModel.getSize();  
	}

	@Override
	public int getRowCount() 
	{
		return rows.size(); 
	}

	/** Returns private array containing current row objects */  
	public RowData[] getRows() 
	{
	    synchronized(this.rows)
	    {
	        int len=this.rows.size(); 
	        RowData rows[]=new RowData[len];    
	        rows=this.rows.toArray(rows);
	        return rows; 
	    }
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
	    synchronized(rows)
        {
	        if ((rowIndex<0) || rowIndex>=this.rows.size())
                return null;
	        
	        if ((columnIndex<0) || columnIndex>=this.headerModel.getSize())
                return null;
            
    		RowData rowObj = rows.get(rowIndex); 
    		
    		if (rowObj==null)
    		{
    		    logger.warnPrintf("getValueAt: Index Out of bounds! [%s,%d]\n",rowIndex,columnIndex); 
    			return null;
    		}
    		
    		String header=this.headerModel.getElementAt(columnIndex);
    	
    		VAttribute attr=rowObj.getAttribute(header);
    		
    		if (attr==null)
    		    return null;
    		
    		return attr;
        }
	}

	public Object getValueAt(String rowKey,String attrName)
	{
	    synchronized(rows)
        {
	        VAttribute attr=getAttribute(rowKey,attrName); 
	        if (attr==null)
	            return null; 
	        return attr.getValue();
        }
	}
	
	/** Returns VAttribute Value ! use getAttrStringValue for actual string value of attribute */ 
	public Object getValueAt(int rowIndex,String attrName)
    {
	    synchronized(rows)
	    {
	        if ((rowIndex<0) || rowIndex>=this.rows.size())
                return null;
	        
	        VAttribute attr=this.rows.get(rowIndex).getAttribute(attrName);
	        
	        if (attr==null)
	            return null;
	        
	        return attr.getValue();
	    }
    }

	/** Return VAttribute.getValue() of specified row,attributeName */
	public String getAttrStringValue(String rowKey,String attrName)
    {
	    VAttribute attr=getAttribute(rowKey,attrName); 
	    if (attr==null)
	        return null; 
	    return attr.getStringValue(); 
    }
	
	public String getAttrStringValue(int row, String attrName)
	{
	    VAttribute attr=getAttribute(row,attrName); 
        if (attr==null)
            return null; 
        return attr.getStringValue(); 
	}
	
	public boolean setValue(String key,String attrName,String value)
	{
	    synchronized(rows)
        {
    	    int rowIndex=this.getRowIndex(key); 
            if (rowIndex<0)
                return false; 
            
            RowData row = this.rows.get(rowIndex);
            if (row==null)
                return false; 
            row.setValue(attrName,value); 
            this.fireTableRowsUpdated(rowIndex,rowIndex);
            return true;
        }
	}
	
	public boolean setValue(String key,VAttribute attr)
    {
	    return setValues(key,new VAttribute[]{attr});
    }
	
	public boolean setValues(String key,VAttribute attrs[])
    {
	    synchronized(rows)
        {
            int rowIndex=this.getRowIndex(key); 
            if (rowIndex<0)
                return false; 
            
            RowData row = this.rows.get(rowIndex);
            if (row==null)
                return false; 
            row.setValues(attrs);  
            this.fireTableRowsUpdated(rowIndex,rowIndex);
            return true;
        }
    }
	
    public void setValueAt(Object value,int rowNr, int colNr)
    {
        RowData row = getRow(rowNr);
        
        if (row==null)
        {
        	if (value==null)
        		value="<NULL>";
            logger.warnPrintf("setValueAt(): Index out of bound:[%d,%d]='%s'\n",rowNr,colNr,value.toString()); 
            return;
        }
        
        // Let VAttribute figure object out!  
        row.getAttribute(colNr).setValue(value.toString());
        
        // optimization note: table will collect multiple events
        // and do the drawing at once. 
        
        this.fireTableCellUpdated(rowNr,colNr); 
        
    }
	
	public VAttribute getAttribute(String rowKey,String attrName)
    {
	    synchronized(rows)
        {
            int rowIndex=this.getRowIndex(rowKey); 
            if (rowIndex<0)
                return null; 
            return getAttribute(rowIndex,attrName);
        }
    }
	    
	
	public VAttribute getAttribute(int rowIndex,String attrName)
	{
	    synchronized(rows)
        {
	        RowData row = this.rows.get(rowIndex);
            if (row==null)
                return null; 
            return row.getAttribute(attrName);
        }
    }
	
	public VAttribute getAttribute(int row,int col)
    {
        synchronized(rows)
        {
            RowData rowdata = this.rows.get(row);
            if (rowdata==null)
                return null; 
            return rowdata.getAttribute(col);
        }
    }
	
	public String[] getHeaders()
	{
		return headerModel.toArray();  
	}
	
	/** Create new Rows with empty Row Data */ 
    public void setRows(List<String> rowKeys)
    {
        synchronized(rows)
        {
            this.rows.clear();
            this.rowKeyIndex.clear(); 
            for (String key:rowKeys)
            {
                // add to internal data structure only
                _addRow(key,new VAttributeSet()) ; 
            }
        }
        
        this.fireTableDataChanged();
    }
    
    /** Add new Row and return index to row */ 
    public int addRow(String key,VAttributeSet attrs)
    {
        int index=this._addRow(key, attrs);
        this.fireTableRowsInserted(index,index);
        return this.rows.size()-1; 
    }
    
    public int addEmptyRow(String key)
    {
        return this.addRow(key,new VAttributeSet()); 
    }
    
    public int delRow(String key)
    {
        int index=this._delRow(key);
        if (index<0)
            return -1; 
        
        this.fireTableRowsDeleted(index,index);
        return index; 
    }
    
    /**
     * Deletes Row. 
     * Performance note: 
     * Since a delete triggers an update for the used Key->Index mapping. 
     * This method takes O(N) time.  
     * @param index
     * @return
     */
    public boolean delRow(int index)
    {
        boolean result=this._delRow(index); 
        this.fireTableRowsDeleted(index,index);
        return result; 

    }
    
    /**
     * Deletes Row. 
     * Performance note: 
     * Since a delete triggers an update for the used Key->Index mapping. 
     * This method takes O(N) time.  (Where N= nr of rows in table) 
     * @param index
     * @return
     */
    public boolean delRows(int indices[])
    {
        // multi delete to avoid O(N*N) rekeying of key mapping ! 
        boolean result=this._delRows(indices);
        for (int i=0;i<indices.length;i++)
        {
            this.fireTableRowsDeleted(indices[i],indices[i]); 
        }
            
        return result; 
    }
    
    // add row to internal data structure 
    private int _addRow(String key,VAttributeSet attrs)
    {
        synchronized(rows)
        {
            int index=rows.size(); 
            this.rows.add(new RowData(key,attrs)); 
            this.rowKeyIndex.put(key,new Integer(index));
            return index;
        }
    }
    
    // delete row from internal data structure 
    private int _delRow(String key)
    {
        // synchronized for ROWS and rowKeyIndex as well ! 
        synchronized(rows)
        {
            Integer index=this.rowKeyIndex.get(key); 
            if (index==null)
            {
                logger.warnPrintf("_delRow: Invalid Row Key:%s\n",key); 
                return -1;
            }
            
            this._delRow(index);            
            return index;
        }
    }
    
    /**
     * Delete row from internal data structure. 
     * Performance note: here the internal key mapping is regenerated. 
     * This take O(N) time.   
     * @param index
     * @return
     */
    private boolean _delRow(int index)
    {
        synchronized(rows)// sync for both rows and rowKeyIndex!
        {
            if ((index<0) || (index>=rows.size()))
            {
                return false; 
            }
            
            RowData rowObj=rows.get(index); 
            String key=rowObj.getKey(); 
            rows.remove(index); 
            this.rowKeyIndex.remove(key); 

            // update indices: start from 'index'  
            //index=0; 
            //rowKeyIndex.clear(); 
            for (int i=index;i<rows.size();i++)
            {
                this.rowKeyIndex.put(rows.get(i).getKey(),new Integer(i)); 
            }
            
            return true; 
        }
    }
    
    /**
     * Multi delete rows from internal data structure. 
     * Performance note: here the internal key mapping is regenerated. 
     * This take O(N) time.   
     * @param index
     * @return
     */
    private boolean _delRows(int indices[])
    {
        synchronized(rows)// sync for both rows and rowKeyIndex!
        {
            for (int index:indices)
            {
                if ((index<0) || (index>=rows.size()))
                    continue;
                        
                RowData rowObj=rows.get(index); 
                String key=rowObj.getKey(); 
                rows.remove(index); 
                this.rowKeyIndex.remove(key); 
            }
            
            // regenerate key mapping   
            rowKeyIndex.clear(); 
            for (int i=0;i<rows.size();i++)
            {
                this.rowKeyIndex.put(rows.get(i).getKey(),new Integer(i)); 
            }
            
            return true; 
        }
    }

    /** Search key and return row index */ 
    public int getRowIndex(String key)
    {
        if (key==null)
            return -1; 
        
        Integer index=this.rowKeyIndex.get(key); 
        
        if (index==null)
            return -1;
        
        return index; 
    }
    
    /** Return Key String of (Model) Row index. */  
    public String getRowKey(int index)
    {
        synchronized(this.rows)
        {
            if ((index<0) || (index>=this.rows.size()))
                return null;
        
            return this.rows.get(index).getKey();
        }
    }
    
    /** Return copy of current keys as array */  
    public String[] getRowKeys()
    {
        synchronized(this.rows)
        {
            String keys[]=new String[this.rows.size()];
            for (int i=0;i<this.rows.size();i++)
                keys[i]=rows.elementAt(i).getKey();
            return keys;
        }
    }

    public int getHeaderIndex(String name)
    {
        return this.headerModel.indexOf(name); 
    }
  
    /** Clear Data information. Keeps header information */ 
    public void clearData()
    {
        synchronized(rows) // for rows and keys 

        {
            this.rows.clear();
            this.rowKeyIndex.clear(); 
        }
        
        this.fireTableDataChanged(); 
    }
    
    public RowData getRow(int index)
    {
        synchronized(rows)
        {
            if ((index<0) || (index>=rows.size()))
                return null;
            return this.rows.get(index);
        }
    }
    
    public RowData getRow(String key)
    {
        synchronized(rows)
        {
            int index=this.getRowIndex(key); 
            if ((index<0) || (index>=rows.size()))
                return null;
            return this.rows.get(index);
        }
    }

   
   public boolean isCellEditable(int row, int col)
   {
       Object obj=this.getValueAt(row,col); 
       if (obj instanceof VAttribute)
       {
           return ((VAttribute)obj).isEditable(); 
       }
       return false; 
   }
   
   /**
    * Return COPY of Data as VAttribute matrix. 
    * Returns: VAttribute[row][col].  
    */ 
   public VAttribute[][] getAttributeData() 
   {
       synchronized(rows)
       {
           int nrRows=this.rows.size();
           if (nrRows<=0)
               return null; 
           
           // assume symmetrical: 
           int nrCols=rows.get(0).getNrAttributes(); 
           if  (nrCols<=0)
               return null; 
           
           VAttribute attrs[][]=new VAttribute[nrRows][]; 
           for (int row=0;row<nrRows;row++)
           {
               attrs[row]=new VAttribute[nrCols]; 
               for (int col=0;col<nrCols;col++)
               {
                   attrs[row][col]=this.getAttribute(row,col).duplicate(); 
               }
           }
           
           return attrs;  
       }
   }

  public String[] getAllHeaders()
  {
     if (allHeaders==null)
         return this.headerModel.toArray(); 
     
     return this.allHeaders.toArray(); 
  }
  
  /**
   * Set all allowed headers. Won't change current table layout.
   * "All Headers" show up in the header popup menu.    
   */ 
  public void setAllHeaders(StringList list)
  {
      this.allHeaders=list.duplicate();  
      // no event. This only changes the header menu, not the headers themselves 
  }

  public HeaderModel getHeaderModel()
  {
     return this.headerModel; 
  }

  /** 
   * Removes header and fires TableStructureChanged event.
   * Actual column data is kept in the model to avoid null pointer bugs. 
   *  
   * Method fires TableStructureChanged event which update the actual table. 
   * Only after the TableStructureChanged event has been handled.*/ 
  public void removeHeader(String headerName)
  {
      this.headerModel.remove(headerName); 
      this.fireTableStructureChanged();
  }

  /** 
   * Inserts new header into the headermodel after or before 'headerName'.
   * Method fires TableStructureChanged event which updates the Table. 
   * Only after the TableStructureChanged event has been handled, the table
   * column model has added the new Column ! 
   * This Table Data Model can already be updated asynchronously after the new header 
   * has been added. 
   */ 
  public int insertHeader(String headerName, String newName, boolean insertBefore)
  {
      // update Table Structure
      int index= this.headerModel.insertHeader(headerName, newName, insertBefore);
      this.fireTableStructureChanged();
      return index; 
  }
   
  /** 
   * Add listener to header list model, which controls the column headers. 
   * Not that due to the asynchronous nature of Swing Events, the Header Model 
   * might already have changed, but the Viewed column model use by Swing might not have. 
   */ 
  public void addHeaderModelListener(ListDataListener listener)
  {
      this.headerModel.addListDataListener(listener); 
  }
  
  public void removeHeaderModelListener(ListDataListener listener)
  {
      this.headerModel.removeListDataListener(listener); 
  }

  @Override
  public RowIterator iterator()
  {
      return new RowIterator(this); 
  }

  public void removeRow(int index)
  {
     this.rows.remove(index);    
     this.fireTableRowsDeleted(index,index); 
  }

  public boolean hasHeader(String name) 
  {
      return getHeaderModel().contains(name); 
  }

}
