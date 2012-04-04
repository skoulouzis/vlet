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
 * $Id: VTableNode.java,v 1.4 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vdbs;

import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public abstract class VTableNode extends VNode implements VTable,VPresentable
{
	protected Presentation presentation=null; 
	
	// ========================================================================
	// Contructors/Initializors 
	// ========================================================================

	public VTableNode(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

	// ========================================================================
	// VTable Methods 
	// ========================================================================

	public String[] getColumnNames()
	{
		int nrColumns=this.getColumnCount(); 
		
		String names[]=new String[nrColumns];
		
		for (int i=0;i<nrColumns;i++)
			names[i]=getColumnName(i); 
		
		return names; 
	}
	
	public String getType()
	{
		return VRS.TABLE_TYPE; 
	}
	
	public String[] getAttributeNames()
	{
		return this.getColumnNames(); 
	}
	
	// ========================================================================
	// TableModel methods  
	// ========================================================================

	/** Returns all (column) values from a single row */ 
	public Object[] getRowValues(int rowIndex)
	{
		int nrCols=this.getColumnCount();
		
		Object values[]=new Object[nrCols];
		
		for (int i=0;i<nrCols;i++)
		{
			values[i]=getValueAt(rowIndex,i); 
		}
		
		return values; 
	}
	
	/** Returns all specified column values from a single row */ 
	public Object[] getRowValues(int rowIndex,int columnIndices[])
	{
		int nrCols=columnIndices.length; 
		
		Object values[]=new Object[nrCols];
		
		for (int i=0;i<nrCols;i++)
		{
			values[i]=getValueAt(rowIndex,columnIndices[i]); 
		}
		
		return values; 
	}
	
	/** Return specified row values from a single column */ 
	
	public Object[] getColumnValues(int rowIndices[],int columnIndex)
	{
		int nrRows=rowIndices.length;
		
		Object values[]=new Object[nrRows];
		
		for (int i=0;i<nrRows;i++)
		{
			// getValueAt does range checking :
			values[i]=getValueAt(rowIndices[i],columnIndex);
		}
		
		return values; 
	}
	
	// ========================================================================
	// Presentation: Tables hold own Presentation 
	// ========================================================================

	private void initPresentation()
	{
		this.presentation=new Presentation();
		this.presentation.setChildAttributeNames(this.getColumnNames()); 
	}

	public Presentation getPresentation()
	{
		// Auto init, but only when requested: 
		if (presentation==null)
		{
			initPresentation(); 
		}
		
		return presentation; 
	}
	
	
	
}
