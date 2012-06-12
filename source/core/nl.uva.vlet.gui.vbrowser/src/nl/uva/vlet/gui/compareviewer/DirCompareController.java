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
 * $Id: DirCompareController.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.vrl.VRL;

/**
 *  Controller
 */
public class DirCompareController implements TableModelListener
{
	private DirCompareDataProvider dataProvider;
	private DirCompareTable compareTable;

	public DirCompareController(DirCompareTable table,DirCompareDataProvider provider)
	{
		this.compareTable=table;
		this.dataProvider=provider; 
	}

	public void compareLocations(VRL source1,VRL source2)
	{ 
		dataProvider.setLocations(source1,source2); 
		try
		{
			dataProvider.update();
		}
		catch (Exception e)
		{
			handle(e);
		} 
	}

	void handle(Exception e)
	{
		Global.logException(ClassLogger.ERROR,this,e,"Exception!\n"); 
	}

	public void tableChanged(TableModelEvent e)
	{
		Global.debugPrintf(this,"tableChanged:%s\n",e);
		this.compareTable.updateTable(); 
	}
	
	public MasterBrowser getMasterBrowser()
	{
		return null; 
	}
	
}
