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
 * $Id: ReplicaEditor.java,v 1.2 2011-04-18 12:27:25 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:25 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.replicaviewer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.panels.list.StatusStringListField;
import nl.uva.vlet.gui.panels.list.StatusStringListModel;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.gui.widgets.NavigationBar;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/** 
 * Internal Replica Editor/Viewer 
 */ 
public class ReplicaEditor extends ViewerPlugin
{
    private static final long serialVersionUID = -7696038824028543650L;

    public static final String ACTION_ADD_SE_FROM_SELIST="addSEFromSEList";
    
    public static final String ACTION_REMOVE_SE_FROM_SELIST = "removeSEFromReplicaList"; 

    public static final String ACTION_UPDATE_REPLICAS = "updateReplicas";

    public static final String ACTION_SELECT_SHOW_PREFFERED_ONLY = "showPreferredOnly";

    public static final String ACTION_VERIFY_CHECKSUMS = "verifyChecksums";

    public static final String ACTION_CLOSE = "close";

    static final String ACTION_REFRESH_REPLICAS = "refreshReplicaInfo";

    // Check box text also used in Error message !
    public static final String UPDATE_SIZE_FROM_REPLICA = "Update size from Replicas";
    
    public static String schemes[] = { VRS.LFN_SCHEME,VRS.GUID_SCHEME };
    
    // ========================================================================
    // Instance
    // ========================================================================

    private JButton verifyBut;
    private JButton addReplicasBut;
    private JPanel replicaOptButtonPnl;
    private StatusStringListField storageElementHostList;
    private JScrollPane selectionSEHostsSP;
    private JButton seSelAddBut;
    private StatusStringListField replicaHostsList;
    private JLabel replicaOptionsTopLbl;


  
    private JPanel topPanel;
    private JScrollPane scrollPane;
    private NavigationBar locationToolbar;
    private JButton closeBut;
    private JButton refreshReplicaBut;
    private JTextField lfcFileSizeTF;
    private JLabel lfcFileSize;
    private JCheckBox updateMetaDataLbl;
    private JPanel buttonPanel;
    private JScrollPane replicaScrollPane;
    private JPanel mainPanel;
    private ReplicaController controller;
    private ResourceTable replicaTable;
 
    @Override
    public String getName()
    {
        return "ReplicaViewer"; 
    }


    // No MimeTypes ! 
    @Override
    public String[] getMimeTypes()
    {
        return null; 
    }
    
    public void initGui()
    {
        {
            // JPanel has aldready a layout
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout(thisLayout);
            this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
            this.setPreferredSize(new java.awt.Dimension(900, 300));
        
            {
                topPanel=new JPanel();
                this.add(topPanel,BorderLayout.NORTH);
                BoxLayout topPanelLayout = new BoxLayout(topPanel, javax.swing.BoxLayout.X_AXIS);
                topPanel.setLayout(topPanelLayout);
                topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                {
                    locationToolbar=new NavigationBar(NavigationBar.LOCATION_ONLY);
                    topPanel.add(locationToolbar);
                }
            }
            {   
                this.add(getMainPanel(),BorderLayout.CENTER);
                this.add(getButtonPanel(), BorderLayout.SOUTH);
            }
        }

        // === Add listeners after creation === // 
        this.getReplicaTable().getResourceTableModel().addHeaderModelListener(this.controller); 
    }

    @Override
    public void initViewer()
    {
        this.controller=new ReplicaController(this); 
        initGui();
        
        this.locationToolbar.addTextFieldListener(controller);
    }

    @Override
    public void stopViewer()
    {
        controller.stop();
    }

    @Override
    public void disposeViewer()
    {
        controller.dispose(); 
    }
    
    @Override
    public void updateLocation(VRL loc) 
    {
        this.controller.updateLocation(loc);
    }
    
    void clearAll()
    {
    }

    public Vector<ActionMenuMapping> getActionMappings()
    {
        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        String[] fileTypes={"File"};
        
        // LFC File mappings:
        ActionMenuMapping mapping;         
        
        // single selection action: 
        mapping=new ActionMenuMapping("editReplicas","Edit Replicas","replicas");
        // only LFN files ! 
        mapping.addTypeSchemeMapping("File","lfn"); 
        mapping.addSelectionTypeSchemeMapping("File","lfn"); 
        //mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_ONE);        
        mappings.add(mapping);
        
        return mappings; 
    }
    
    protected ReplicaController getController()
    {
        return controller; 
    }


    /** Perform Dynamic Action Method */
    public void doMethod(String methodName, ActionContext actionContext)
            throws VlException
    {
        this.controller.doMethod(methodName,actionContext); 
    }

   
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance()
	{
		return new ReplicaEditor(Boolean.FALSE);
	}
	
	public ReplicaEditor() 
    {
        super();
    }
	
    //Jigloo constructor. 
	public ReplicaEditor(Boolean initGUI) 
	{
		super();
		
		if (initGUI)
		{
		    initGui(); 
		}
	}

	private JPanel getMainPanel() 
	{
	    if(mainPanel == null) 
	    {
	        mainPanel = new JPanel();
	        FormLayout mainPanelLayout = new FormLayout(
	                "max(p;5dlu), max(p;5dlu), 92dlu, 5dlu, 61dlu, 5dlu, 35dlu:grow, 5dlu, max(p;15dlu), 5dlu, 35dlu, 11dlu:grow, max(p;5dlu), 5dlu", 
	                "max(p;5dlu), max(p;5dlu), 5dlu, 23dlu, max(p;15dlu):grow, 5dlu, max(p;15dlu), 5dlu, max(p;15dlu)");
            mainPanel.setLayout(mainPanelLayout);
            mainPanel.setPreferredSize(new java.awt.Dimension(896, 214));
            mainPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            mainPanel.add(getReplicaScrollPane(), new CellConstraints("2, 4, 6, 2, default, default"));
            mainPanel.add(getReplicaOptionsTopLbl(), new CellConstraints("2, 2, 12, 1, default, default"));
            mainPanel.add(getSeSelAddBut(), new CellConstraints("9, 4, 1, 1, default, default"));
            mainPanel.add(getSelectionSEHostsSP(), new CellConstraints("11, 4, 2, 2, default, default"));
            mainPanel.add(getReplicaOptButtonPnl(), new CellConstraints("2, 9, 12, 1, default, default"));
            mainPanel.add(getUpdateSizeFromReplicaCB(), new CellConstraints("7, 7, 5, 1, default, default"));
            mainPanel.add(getLfcFileSize(), new CellConstraints("3, 7, 1, 1, default, default"));
            mainPanel.add(getLfcFileSizeTF(), new CellConstraints("5, 7, 1, 1, default, default"));
	    }
	    return mainPanel;
	}
	
	private JScrollPane getReplicaScrollPane() 
	{
	    if(replicaScrollPane == null) {
	        replicaScrollPane = new JScrollPane();
           // replicaScrollPane.setPreferredSize(new java.awt.Dimension(696, 3));
	        {
                replicaScrollPane.setViewportView(getReplicaTable()); 
	        }
	    }
	    return replicaScrollPane;
	}
	
	private JPanel getButtonPanel() 
	{
	    if(buttonPanel == null) 
	    {
	        buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            buttonPanel.add(getCloseBut());
	    }
	    return buttonPanel;
	}
	
	private JButton getCloseBut() 
	{
	    if(closeBut == null) 
	    {
	        closeBut = new JButton();
	        closeBut.setText("Close");
	        closeBut.setActionCommand(ACTION_CLOSE); 
	        closeBut.addActionListener(this.controller);
	    }
	    return closeBut;
	}
	
	private JLabel getReplicaOptionsTopLbl() 
	{
	    if(replicaOptionsTopLbl == null) 
	    {
	        replicaOptionsTopLbl = new JLabel();
	        replicaOptionsTopLbl.setText("Replica Options");
            replicaOptionsTopLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	    }
	    return replicaOptionsTopLbl;
	}
	private JButton getSeSelAddBut() 
	{
	    if(seSelAddBut == null) 
	    {
	        seSelAddBut = new JButton();
	        seSelAddBut.setText("Add");
	        seSelAddBut.setActionCommand(ACTION_ADD_SE_FROM_SELIST); 
	        seSelAddBut.addActionListener(this.controller); 
	    }
	    return seSelAddBut;
	}
	
	private JScrollPane getSelectionSEHostsSP() 
	{
	    if(selectionSEHostsSP == null) 
	    {
	        selectionSEHostsSP = new JScrollPane();
            selectionSEHostsSP.setViewportView(getStorageElementHostsList());
	    }
	    return selectionSEHostsSP;
	}
	
	private JList getStorageElementHostsList() 
	{
	    if(storageElementHostList == null) 
	    {
	        StatusStringListModel selectionSEHostsListModel = 
	            new StatusStringListModel(
	                    new String[] { "",""});
	        storageElementHostList = new StatusStringListField();
	        storageElementHostList.setModel(selectionSEHostsListModel);
            storageElementHostList.addListSelectionListener(controller);
	    }
	    return storageElementHostList;
	}
	
	private JPanel getReplicaOptButtonPnl() {
	    if(replicaOptButtonPnl == null) {
	        replicaOptButtonPnl = new JPanel();
            replicaOptButtonPnl.add(getUpdateReplicasBut());
            replicaOptButtonPnl.add(getRefreshReplicasBut());
            replicaOptButtonPnl.add(getCheckChecksumsBut());
	    }
	    return replicaOptButtonPnl;
	}
	
	protected JButton getUpdateReplicasBut() 
	{
	    if(addReplicasBut == null) {
	        addReplicasBut = new JButton();
	        addReplicasBut.setText("Apply Changes");
	        addReplicasBut.addActionListener(this.controller);
	        addReplicasBut.setActionCommand(ACTION_UPDATE_REPLICAS); 
	     }
	    return addReplicasBut;
	}

	private JButton getCheckChecksumsBut() {
	    if (verifyBut == null) 
	    {
	        verifyBut = new JButton();
	        verifyBut.setText("Verify");
	        verifyBut.addActionListener(controller); 
	        verifyBut.setActionCommand(ACTION_VERIFY_CHECKSUMS);
	        verifyBut.setEnabled(false); 
	    }
	    return verifyBut;
	}

    public String getLocationText()
    {
        return this.locationToolbar.getLocationText(); 
    }

    public StatusStringListField getReplicaHostListField()
    {
        return replicaHostsList; 
    }
    
    public StatusStringListField getSEHostListField()
    {
        return storageElementHostList; 
    }
    
    public void updateReplicaHostList(StringList list,boolean isOriginal)
    {
        this.replicaHostsList.setListData(list,isOriginal); 
    }

    public void updateSEHostList(StringList list,boolean isOriginal)
    {
        this.storageElementHostList.setListData(list,isOriginal); 
    }


    public String[] getSESelection()
    {
        return this.storageElementHostList.getSelectedValues(); 
    }
    
    public String[] getReplicaHostSelection()
    {
        return this.replicaHostsList.getSelectedValues(); 
    }
    
    public String[] getReplicaHostList()
    {
        return this.replicaHostsList.getValues();  
    }

    public void setLocationText(String txt)
    {
        this.locationToolbar.setLocationText(txt); 
    }


    public void dispose()
    {
        disposeJFrame();  
    }
    
    JButton getRefreshReplicasBut() {
        if(refreshReplicaBut == null) {
            refreshReplicaBut = new JButton();
            refreshReplicaBut.setText("Refresh");
            refreshReplicaBut.addActionListener(controller); 
            refreshReplicaBut.setActionCommand(ACTION_REFRESH_REPLICAS);
        }
        return refreshReplicaBut;
    }
    
    public void setReplicaDataModel(ReplicaDataModel model)
    {
        this.replicaTable.setDataModel(model);  
    }
    
    public ResourceTable getReplicaTable()
    {
        if (replicaTable==null)
        {
            // empty model: 
            ReplicaDataModel model=new ReplicaDataModel(); 
            replicaTable = new ResourceTable(model);
            replicaTable.setPopupMenu(new ReplicaPopupMenu(this.controller));
            // Presentation pres = replicaTable.getPresentation(); 
        }
        
        return this.replicaTable;
    }

    protected JCheckBox getUpdateSizeFromReplicaCB() 
    {
        if(updateMetaDataLbl == null) 
        {
            updateMetaDataLbl = new JCheckBox();
            updateMetaDataLbl.setSelected(true); 
            updateMetaDataLbl.setText(UPDATE_SIZE_FROM_REPLICA);
        }
        return updateMetaDataLbl;
    }
    
    public boolean getUpdateSizeFromReplica()
    {
        return this.updateMetaDataLbl.isSelected(); 
    }
    
    public void setUpdateSizeFromReplica(boolean val)
    {
        this.updateMetaDataLbl.setSelected(val); 
    }
    
    private JLabel getLfcFileSize() 
    {
        if(lfcFileSize == null) 
        {
            lfcFileSize = new JLabel();
            lfcFileSize.setText("LFC File Size");
        }
        return lfcFileSize;
    }
    
    private JTextField getLfcFileSizeTF() 
    {
        if(lfcFileSizeTF == null) {
            lfcFileSizeTF = new JTextField();
            lfcFileSizeTF.setText("?");
            lfcFileSizeTF.setEditable(false); 
        }
        return lfcFileSizeTF;
    }
    
    protected void setLFCFileSize(String sizeTxt, boolean isOk)
    {
        this.lfcFileSizeTF.setText(sizeTxt);
        
        if (isOk)
        {
            this.lfcFileSizeTF.setForeground(Color.BLACK);
        }
        else
        {
            this.lfcFileSizeTF.setForeground(Color.RED);
        }
         
    }
    
}
