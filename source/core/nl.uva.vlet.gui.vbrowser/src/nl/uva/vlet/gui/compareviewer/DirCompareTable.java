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
 * $Id: DirCompareTable.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.table.VAttributeCellRenderer;

public class DirCompareTable extends JTable
{
    private static final long serialVersionUID = -831514324114171199L;
    // ---
    private TableCellRenderer defaultCellRenderer=null; 
	
	public DirCompareTable(DirCompareTableModel model)
	{
		init();
		this.setModel(model); 
	}
	
	public DirCompareTable()
	{
		init(); 
	}

	private void init()
	{
		defaultCellRenderer=new VAttributeCellRenderer(this);
		//updateCellRenderers();
		this.setAutoCreateColumnsFromModel(true); 
		this.setShowGrid(false);
		
	}
	
	public void updateTable()
	{
		debugPrintf("updateTable():%s\n",this);

		updateHeaders();
		updateCellRenderers();
		//updateColumns();
		this.setModel(getModel()); 
		
		this.revalidate();
		this.repaint(); 
	}

	 void updateCellRenderers()
	    {
	        TableColumn column=null; 
	        
	        for (int i=0;i<columnModel.getColumnCount();i++)
	        {
	            column=columnModel.getColumn(i);
	        	debugPrintf("Handling column:%d\n",column);
	            //column.setWidth(getDefaultWidth(column.getHeaderValue().toString()));
	            column.setCellRenderer(defaultCellRenderer);
	        }
	    }
	 
	 // returns one-for-all 
	 public TableCellRenderer getCellRenderer(int row, int column)
	 {
		 return this.defaultCellRenderer;  
	 }
	 
	 void updateHeaders()
	 {
	        TableColumn column=null; 
	        
	        for (int i=0;i<columnModel.getColumnCount();i++)
	        {
	            column=columnModel.getColumn(i);
	            //column.setWidth(getDefaultWidth(column.getHeaderValue().toString()));
	            column.setHeaderValue("aap"); 
	        }
	 }
	 
	void updateColumns()
	{
		TableColumnModel cmodel = this.getColumnModel(); 
		int n=cmodel.getColumnCount(); 
		for (int i=0;i<n;i++)
		{
			TableColumn col = cmodel.getColumn(i);
			col.setHeaderValue(" H  ");
			col.setCellRenderer(this.defaultCellRenderer);
		}
	}

	private void debugPrintf(String format,Object... args)
	{
	    Global.debugPrintf(this,format,args);  
	}
    
}
