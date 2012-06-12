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
 * $Id: DirCompareDataProvider.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;

import javax.swing.SwingUtilities;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.compareviewer.DirCompareTableModel.DCRowRecord;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;

public class DirCompareDataProvider
{
	private DirCompareTableModel dataModel;
	private VRL leftSource;
	private VRL rightSource;
	private ProxyNode leftNode;
	private ProxyNode rightNode;
	private MasterBrowser masterBrowser;
	
	public DirCompareDataProvider(MasterBrowser masterBrowser,DirCompareTableModel model) 
	{
		this.dataModel=model;
		this.masterBrowser=masterBrowser; 
	}

	public void setLocations(VRL source1, VRL source2)
	{
		leftSource=source1; 
		rightSource=source2; 
	}
	
	/** Update/create DataModel */ 
	
	public void update() throws VlException
	{
		mustStop=false; 
		asyncUpdate();
	}
	
	private boolean mustStop=false; 
	
	private void asyncUpdate()
	{
		ActionTask updateTask=new ActionTask(masterBrowser, "Update Compare Window")
		{
			public void doTask()
			{
				try
				{
					_doUpdate();
				}
				catch (Exception e)
				{
					handle(e); 
					
				} 
			}

			@Override
			public void stopTask()
			{
				mustStop=true; 
			}
		};
		
		updateTask.run(); 
		//updateTask.startTask();
		
	}
	
	private void _doUpdate() throws VlException
	{
		leftNode=ProxyNode.getProxyNodeFactory().openLocation(leftSource); 
		rightNode=ProxyNode.getProxyNodeFactory().openLocation(rightSource);
		
		ProxyNode[] leftChilds = null;
		
		if (leftNode!=null)
			leftChilds=leftNode.getChilds(null);
		
		ProxyNode[] rightChilds = null;
		
		if (rightNode!=null)
			rightChilds=rightNode.getChilds(null);
		
		dataModel.clear();
		
		int leftN=leftChilds.length;
		int rightN=rightChilds.length;
		int numNodes=leftN; 
		
		if (rightN>numNodes)
			numNodes=rightN;
		
		for (int i=0;i<numNodes;i++)
		{
			if (mustStop==true)
				return; 
			
			ProxyNode left=null; 
			ProxyNode right=null;
			
			VAttributeSet leftAttrs=null; 
			VAttributeSet rightAttrs=null; 
			
			if ((leftChilds!=null) && (i<leftChilds.length))
			{
				left=leftChilds[i];
				leftAttrs = new VAttributeSet(left.getAttributes(DCTableRecord.getDefaultAttributeNames()));  
			}
			
			if ((rightChilds!=null) && (i<rightChilds.length))
			{
				right=rightChilds[i];
				rightAttrs= new VAttributeSet(right.getAttributes(DCTableRecord.getDefaultAttributeNames())); 
			}
			
			dataModel.addRecord(DCRowRecord.createRecord(leftAttrs,rightAttrs)); 
		}
		

		// complete redraw: 
		this.notifyTableStructureChanged(); 
		
	}
	
	/** Synchronize with gui event thread */	
	private void notifyTableStructureChanged()
	{
		Runnable notify=new Runnable()
		{
			public void run()
			{
				dataModel.fireTableStructureChanged();				
			}
		}; 

		SwingUtilities.invokeLater(notify); 
		
	}

	public void handle(Exception e)
	{
		// stand alone provider:
		if (masterBrowser==null)
		{	
			Global.logException(ClassLogger.ERROR,this,e,"Exception!\n");
		}
		else
			masterBrowser.handle(e); 
	}
}
