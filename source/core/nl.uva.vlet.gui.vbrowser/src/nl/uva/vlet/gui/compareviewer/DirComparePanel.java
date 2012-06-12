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
 * $Id: DirComparePanel.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.vrl.VRL;

public class DirComparePanel extends ViewerPlugin
{
    private static final long serialVersionUID = 3028988443518415099L;
    // ---
    private JPanel comparePanel;
	private DirCompareTable compareTabel;
	private DirCompareController compareController;
	private JScrollPane scrollPane;
	private MasterBrowser masterBrowser;

	public void initGUI()
	{
		{
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(622, 168));
			{
				comparePanel = new JPanel();
				BorderLayout comparePanelLayout = new BorderLayout();
				comparePanel.setLayout(comparePanelLayout);
				this.add(comparePanel, BorderLayout.CENTER);
				{
					scrollPane = new JScrollPane();
					comparePanel.add(scrollPane, BorderLayout.CENTER);
					{
						
						scrollPane.setViewportView(compareTabel);

					}
				}
			}
		}

	}
	
	private void init()
	{
		//
		// construct model provider viewer controller
		// 
		// I) Model
		// II) Provider -> Model
		// III) Table -> Model
		// IV) Controller -> { Table,Provider } 
		// 
		// Listeners: 
		// 
		DirCompareTableModel compareModel = new  DirCompareTableModel();
	    DirCompareDataProvider dataProvider = new DirCompareDataProvider(masterBrowser,compareModel);
	    
		compareTabel = new DirCompareTable();
		compareTabel.setModel(compareModel); 

		compareController=new DirCompareController(compareTabel,dataProvider);
		compareModel.addTableModelListener(compareController); 
		
	}
	
	@Override
	public String[] getMimeTypes()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Resource Comparer";
	}

	@Override
    public void initViewer()
	{
	    init(); 	
	}

	@Override
    public void stopViewer()
	{
		
	}

	@Override
	public void updateLocation(VRL loc) throws VlException
	{
		
	}

	@Override
	public void disposeViewer()
	{

	}
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static DirComparePanel getGUIBuilderInstance()
	{
		return new DirComparePanel(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public DirComparePanel(Boolean initGUI) 
	{
		super();
		init();
		
		if (initGUI) 
		 	initGUI(); 
	}
	
	
	

	public DirCompareController getController()
	{
		return this.compareController; 
	}
}
