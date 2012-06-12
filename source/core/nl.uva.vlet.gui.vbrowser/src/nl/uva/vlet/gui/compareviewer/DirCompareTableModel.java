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
 * $Id: DirCompareTableModel.java,v 1.3 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;

public class DirCompareTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = -512689734071402151L;

    public static class DCRowRecord
	{ 
		DCTableRecord left; 
		DCTableRecord right; 
		
		public DCRowRecord(DCTableRecord rec1, DCTableRecord rec2)
		{
			left=rec1;
			right=rec2; 
		}

		Object getRowEntry(int i)
		{
			switch(i)
			{
				case 0:
				case 1:
				case 2:
					return left.getRowEntry(i);
				case 3:
					return "<==>"; 
				case 4:
				case 5:
				case 6:
					return left.getRowEntry(i-4);
				default:
					Global.errorPrintf(this,"Record has no entry at index:%d\n",i);
					return null; 
			}
		}

        public static DCRowRecord createRecord(VAttributeSet leftAttrs,VAttributeSet rightAttrs) 
		{
			DCTableRecord rec1 = new DCTableRecord(leftAttrs); 
			DCTableRecord rec2 = new DCTableRecord(rightAttrs);
			
			return new DCRowRecord(rec1,rec2); 			
		}
		
		public String toString()
		{
			return "["+left+"|"+right+"]";
		}

		public String getHeaderName(int index)
		{
			Object obj = getRowEntry(index);
			
			if (obj instanceof VAttribute)
			{
				return ((VAttribute)obj).getName(); 
			}
			else
			{
				return obj.toString();
			}
		}
	}
	
	// ===
	// instance 
	// === 
	
	Vector<DCRowRecord> rows=new Vector<DCRowRecord>();
	
	public DirCompareTableModel() 
	{
	}

	public int getColumnCount()
	{
		return 7;
	}

	public int getRowCount()
	{
		return rows.size(); 
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if ((rowIndex<0) || (rowIndex>=rows.size()))
		{
			return null; 
		}
		
		DCRowRecord record = rows.get(rowIndex);
		
		if (record!=null)
			return record.getRowEntry(columnIndex);
		
		return null; 
	}

	public void clear()
	{
		rows.clear();
	}

	public void addRecord(DCRowRecord record)
	{
	    debugPrintf("+++ Add record:%s\n",record); 
		//int num=rows.size(); 
		rows.add(record); 
		//this.fireTableRowsUpdated(num,num); 
	}

	public String getColumnName(int index)
	{
		if (rows.size()<=0) 
			return ""+index; 
		
		DCRowRecord record = rows.get(0);
		
		String str=record.getHeaderName(index);
		
		return str; 
	}
	
//    private void errorPrintf(String format,Object... args)
//    {
//        Global.errorPrintf(this,format,args);
//    }
    
    private void debugPrintf(String format,Object... args)
    {
        Global.debugPrintf(this,format,args);
    }
}
