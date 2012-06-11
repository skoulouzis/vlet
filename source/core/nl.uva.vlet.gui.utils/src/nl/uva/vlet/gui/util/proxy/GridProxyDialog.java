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
 * $Id: GridProxyDialog.java,v 1.4 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
 */ 
// source: 

package nl.uva.vlet.gui.util.proxy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.vrs.VRSContext;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/** 
 * Grid Proxy initialization Dialog 
 */
public class GridProxyDialog extends javax.swing.JDialog
{
    private static final long serialVersionUID = -735019418581906785L;
    
    // ===
    
    private JPanel proxyTabPanel;

    JTextField proxyLocationTextField;
    private JLabel proxyLocationLabel;
    private JLabel certificateNameLabel;
    private JLabel timeLeftLabel;
    JTextField proxyLifetimeField;
    private JLabel defaultLifeTimeLabel;
    private JPanel gridProxyValues;
    JTextField certificateLocationField;
    private JLabel certLocLabel;
    private JLabel optionsLabel;
    private JLabel proxyValidLabel=null;

    JTextField proxyValidityTextField=null;
   // JPanel proxyInfoPanel=null;
    private JPanel topPanel;
    private JPanel configTabPanel;
    private JTabbedPane mainTabPanel;
    JButton deleteButton;
    JTextField proxyTimeleftTextField;
    JTextField proxyCNTextField;
    JTextField topLabelTextField;
 
    JButton createButton;
    JPasswordField passwordTextField;

    private JLabel passwordLabel;
    private JPanel passwordPanel;
    private JButton cancelButton;

    JButton okButton;

    private JPanel buttonPanel;
    private GridProxyDialogController proxyController;
    private String message="Create Grid Proxy.";
    private JPanel gridProxyOptions;
    private JLabel statusLabel;
	private JLabel voSupportLabel;

	JCheckBox voSupportTB;
	private JLabel voNameLabel;
	JTextField voNameTF;
	JTextField vomsACField;
    private JLabel voRoleLbl;
    JTextField voRoleTF;

    private void initGUI()
    {
        try
        {

            BorderLayout thisLayout = new BorderLayout();
            this.getContentPane().setLayout(thisLayout);
            {
                topPanel = new JPanel();
                BorderLayout topPanelLayout = new BorderLayout();
                topPanel.setLayout(topPanelLayout);
                getContentPane().add(topPanel, BorderLayout.NORTH);
                
                {
                    topLabelTextField = new JTextField();
                    topPanel.add(topLabelTextField, BorderLayout.NORTH);
                    topLabelTextField.setText("Grid Proxy Dialog");
                    topLabelTextField.setEditable(false);
                    topLabelTextField.setFocusable(false);
                    topLabelTextField.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                    topLabelTextField.setFont(new java.awt.Font("Lucida Sans", 1,
                            14));
                    topLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
                }
            }
            {
                mainTabPanel = new JTabbedPane();
                getContentPane().add(mainTabPanel, BorderLayout.CENTER);
                {
                    proxyTabPanel = new JPanel();
//                    FormLayout mainPanelLayout = new FormLayout(
//                            "10px, center:max(d;10px):grow, 100px, 100px, 1px, 10px", 
//                            "p, 5px, max(p;10px), 5px, max(p;10px), 5px, p, 5px, p, 10px, max(p;15dlu), 5dlu:grow, max(p;5dlu), 5dlu");
                    FormLayout mainPanelLayout = new FormLayout(
                            "10px, center:max(d;10px):grow, 100px, 100px, 1px, 10px", 
                            "p, 5px, max(p;10px), 5px, p, 5px, p, 5px, p, 10px, max(p;15dlu), 5dlu:grow, max(p;5dlu), 5dlu");
                    mainTabPanel.addTab("Grid Proxy", null, proxyTabPanel, null);
//                    FormLayout mainPanelLayout = new FormLayout(
//                            "10px, center:max(d;10px):grow, 100px, 100px, 1px, 10px", 
//                            "10px, p, 5px, max(p;10px), 5px, p, 5px, p, 5px, p, 10px, max(p;15dlu), 5dlu:grow, max(p;5dlu), 5dlu");
                    proxyTabPanel.setLayout(mainPanelLayout);
                    //proxyInfoPanel.setPreferredSize(new java.awt.Dimension(503, 220));
                    proxyTabPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                    
                    //                FormLayout proxyInfoPanelLayout = new FormLayout(
                            //                        "10px, center:p:grow, 100px, 100px, 1px, 10px", 
                            //                        "10px, p, 5px, p, 5px, p, 5px, p, 5px, p, 10px, max(p;15dlu), 5dlu, max(p;5dlu)");
                    //                FormLayout proxyInfoPanelLayout = new FormLayout(
                            //                        "10px, 147dlu, 100px:grow, 100px, 1px, 10px", 
                            //                        "10px, p, 5px, p, 5px, p, 5px, p, 5px, p, 10px, max(p;15dlu), 5dlu, max(p;15dlu), 5dlu");

                    //proxyTabPanel.setLayout(mainPanelLayout1);
                    // proxyTabPanel.setPreferredSize(new java.awt.Dimension(450, 200));
                    {
                        //                    FormLayout proxyInfoPanelLayout = new FormLayout(
                                //                            "10px, center:p:grow, 100px, 100px, 1px, 10px", 
                                //                            "10px, p, 5px, p, 5px, p, 5px, p, 5px, p, 10px, max(p;15dlu), max(p;15dlu), 5dlu");
                        //proxyTabPanel.setPreferredSize(new java.awt.Dimension(498, 421));
                        //proxyTabPanel.setPreferredSize(new java.awt.Dimension(579, 402));
                        {
                            gridProxyValues = new JPanel();
                            proxyTabPanel.add(gridProxyValues, new CellConstraints("2, 5, 3, 1, default, default"));
                            FormLayout gridProxyValuesLayout = new FormLayout(
                                    "5px, right:p, 5px, left:p:grow, 1px, 40px, 5px", 
                                    "5px, p, p, p, p, p, 4dlu");
                            gridProxyValues.setLayout(gridProxyValuesLayout);
                            gridProxyValues.setBorder(BorderFactory
                                    .createEtchedBorder(BevelBorder.LOWERED));
                            {
                                certificateNameLabel = new JLabel();
                                gridProxyValues.add(
                                        certificateNameLabel,
                                        new CellConstraints(
                                                "2, 2, 1, 1, default, default"));
                                
                                certificateNameLabel.setText("Certificate Name:");
                                certificateNameLabel.setBorder(BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0));
                            }
                            {
                                statusLabel = new JLabel();
                                proxyTabPanel.add(statusLabel, new CellConstraints("2, 3, 2, 1, center, default"));
                                statusLabel.setText("Status");
                                statusLabel.setFont(new java.awt.Font("Dialog",2,14));
                                // statusLabel.setPreferredSize(new java.awt.Dimension(192, 16));
                            }
                            {
                                proxyCNTextField = new JTextField();
                                gridProxyValues.add(
                                        proxyCNTextField,
                                        new CellConstraints(
                                                "4, 2, 2, 1, default, default"));
                                proxyCNTextField.setEditable(false);
                                proxyCNTextField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                proxyCNTextField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                proxyCNTextField.setText("CERTIFICATE_NAME");
                            }
                            {
                                proxyValidLabel = new JLabel();
                                gridProxyValues.add(
                                        proxyValidLabel,
                                        new CellConstraints(
                                                "2, 3, 1, 1, default, default"));
                                
                                proxyValidLabel.setText("Validity:");
                            }
                            {
                                proxyValidityTextField = new JTextField("VALIDITY");
                                gridProxyValues.add(proxyValidityTextField, new CellConstraints("4, 3, 2, 1, default, default"));
                                proxyValidityTextField.setEditable(false);
                                proxyValidityTextField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                proxyValidityTextField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                            }
                            
                            {
                                timeLeftLabel = new JLabel();
                                gridProxyValues.add(
                                        timeLeftLabel,
                                        new CellConstraints(
                                                "2, 4, 1, 1, default, default"));
                                timeLeftLabel.setText("Timeleft:");
                            }
                            {
                                proxyTimeleftTextField = new JTextField();
                                gridProxyValues.add(
                                        proxyTimeleftTextField,
                                        new CellConstraints(
                                                "4, 4, 2, 1, default, default"));
                                
                                proxyTimeleftTextField.setEditable(false);
                                proxyTimeleftTextField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                proxyTimeleftTextField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                proxyTimeleftTextField.setText("TIMELEFT");
                            }
                            
                            {
                                JLabel vomsACInfoLabel = new JLabel();
                                gridProxyValues.add(vomsACInfoLabel, new CellConstraints("2, 5, 1, 1, default, default"));
                                vomsACInfoLabel.setText("VOMS AC Info:");
                            }
                            {
                                vomsACField = new JTextField();
                                gridProxyValues.add(vomsACField, new CellConstraints("4, 5, 2, 1, default, default"));
                                
                                vomsACField.setEditable(false);
                                vomsACField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                vomsACField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                vomsACField.setText("VOMS AC INFO");
                            }
                        }
                        {
                            optionsLabel = new JLabel();
                            proxyTabPanel.add(optionsLabel, new CellConstraints("2, 7, 2, 1, center, default"));
                            optionsLabel.setText("Options");
                            optionsLabel.setFont(new java.awt.Font("Dialog",2,14));
                            //optionsLabel.setPreferredSize(new java.awt.Dimension(222, 16));
                        }
                        {
                            gridProxyOptions = new JPanel();
                            proxyTabPanel.add(gridProxyOptions, new CellConstraints("2, 9, 3, 1, default, default"));
                            FormLayout gridProxyValuesLayout = new FormLayout(
                                    "5px,right:p, 5px,left:pref:grow,1px,40px,5px",
                                    "5px,center:pref,center:pref,center:pref,5px,center:pref,center:pref,center:pref,5px,center:pref,5px");
                            gridProxyOptions.setLayout(gridProxyValuesLayout);
                            gridProxyOptions.setBorder(BorderFactory
                                    .createEtchedBorder(BevelBorder.LOWERED));
                            {
                                certLocLabel = new JLabel();
                                gridProxyOptions.add(
                                        certLocLabel,
                                        new CellConstraints(
                                                "2, 2, 1, 1, default, default"));
                                certLocLabel.setText("Certificate Location:");
                            }
                            
                            {
                                proxyLocationLabel = new JLabel();
                                gridProxyOptions
                                .add(proxyLocationLabel, new CellConstraints(
                                        "2, 3, 1, 1, right, bottom"));
                                proxyLocationLabel.setText("Proxy location:");
                                proxyLocationLabel.setBorder(BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0));
                                //proxyLocationLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                            }
                            {
                                certificateLocationField = new JTextField();
                                gridProxyOptions.add(
                                        certificateLocationField,
                                        new CellConstraints(
                                                "4, 2, 2, 1, default, default"));
                                certificateLocationField.setText("CERTIFICATE LOCATION");
                                
                                certificateLocationField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                certificateLocationField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                certificateLocationField.setEditable(true);//  
                                certificateLocationField.addFocusListener(this.proxyController); 
                                certificateLocationField.addActionListener(this.proxyController);
                            }
                            
                            {
                                proxyLocationTextField = new JTextField();
                                gridProxyOptions.add(
                                        proxyLocationTextField,
                                        new CellConstraints(
                                                "4, 3, 2, 1, default, default"));
                                
                                proxyLocationTextField.setText("PROXY LOCATION");
                                proxyLocationTextField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                proxyLocationTextField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                proxyLocationTextField.setEditable(true);//  
                                proxyLocationTextField.addFocusListener(this.proxyController); 
                                proxyLocationTextField.addActionListener(this.proxyController);
                                
                            }
                            {
                                defaultLifeTimeLabel = new JLabel();
                                gridProxyOptions.add(
                                        defaultLifeTimeLabel,
                                        new CellConstraints(
                                                "2, 4, 1, 1, default, default"));
                                defaultLifeTimeLabel.setText("Default Lifetime");
                            }
                            {
                                proxyLifetimeField = new JTextField();
                                proxyLifetimeField.setEditable(false); 
                                gridProxyOptions.add(
                                        proxyLifetimeField,
                                        new CellConstraints(
                                                "4, 4, 2, 1, default, default"));
                                proxyLifetimeField.setText("DEFAULT_LIFETIME");
                                proxyLifetimeField
                                .setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                proxyLifetimeField
                                .setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                proxyLifetimeField.setEditable(true);//  
                                proxyLifetimeField.addFocusListener(this.proxyController); 
                                proxyLifetimeField.addActionListener(this.proxyController);
                                
                            }
                            
                            
                            
                            {
                                voSupportLabel = new JLabel();
                                gridProxyOptions.add(
                                        voSupportLabel,
                                        new CellConstraints(
                                                "2, 6, 1, 1, default, default"));
                                voSupportLabel.setText("Enable VOMS proxy:");
                            }
                            {
                                voSupportTB = new JCheckBox();
                                gridProxyOptions.add(
                                        voSupportTB,
                                        new CellConstraints(
                                                "4, 6, 2, 1, default, default"));
                                
                                voSupportTB.addActionListener(this.proxyController);
                                
                            }
                            
                            {
                                voNameLabel = new JLabel();
                                gridProxyOptions.add(voNameLabel, new CellConstraints("2, 7, 1, 1, default, default"));
                                voNameLabel.setText("VO Name:");
                                voNameLabel.setBorder(BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0));
                                //proxyLocationLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                            }
                            
                            {
                                voNameTF = new JTextField();
                                gridProxyOptions.add(voNameTF, new CellConstraints("4, 7, 2, 1, default, default"));
                                
                                voNameTF.setText("VO NAME");
                                voNameTF.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                voNameTF.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                voNameTF.setEditable(true);//  
                                voNameTF.addFocusListener(this.proxyController); 
                                voNameTF.addActionListener(this.proxyController);
                                
                            }
                            {
                                voRoleLbl = new JLabel();
                                gridProxyOptions.add(voRoleLbl, new CellConstraints(
                                        "2, 8, 1, 1, default, default"));
                                voRoleLbl.setText("VO Role:");
                                voRoleLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                                //proxyLocationLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                            }
                            
                            {
                                voRoleTF = new JTextField();
                                gridProxyOptions.add(voRoleTF,new CellConstraints(
                                                "4, 8, 2, 1, default, default"));
                                
                                voRoleTF.setText("VO ROLE");
                                voRoleTF.setBackground(UIGlobal.getGuiSettings().textfield_editable_background_color);
                                voRoleTF.setForeground(UIGlobal.getGuiSettings().textfield_editable_foreground_color);
                                
                                voRoleTF.setEditable(true);//  
                                voRoleTF.addFocusListener(this.proxyController); 
                                voRoleTF.addActionListener(this.proxyController);
                                
                            }
                            
                        }
                        {
                            passwordPanel = new JPanel();
                            proxyTabPanel.add(passwordPanel, new CellConstraints("2, 11, 3, 1, default, default"));
                            passwordPanel.setPreferredSize(new java.awt.Dimension(479, 39));
                            passwordPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                            {
                                passwordLabel = new JLabel("Password: ");
                                passwordPanel.add(passwordLabel);
                                passwordLabel.setText("Password: ");
                            }
                            {
                                passwordTextField = new JPasswordField();
                                passwordPanel.add(passwordTextField);
                                passwordTextField.setColumns(15);
                                passwordTextField.addActionListener(proxyController);
                            }
                            {
                                createButton = new JButton();
                                passwordPanel.add(createButton);
                                createButton.setText("Create");
                                createButton.setToolTipText("Create a new Grid Prox");
                                createButton.setActionCommand("Create");
                                createButton.setPreferredSize(new java.awt.Dimension(89, 21));
                                createButton.addActionListener(proxyController);
                            }
                            {
                                deleteButton = new JButton();
                                passwordPanel.add(deleteButton);
                                deleteButton.setText("Destroy");
                                deleteButton.setActionCommand("Destroy");
                                deleteButton.setToolTipText("Create a new Grid Prox");
                                deleteButton.addActionListener(proxyController);
                                deleteButton.setEnabled(true); 
                                deleteButton.setPreferredSize(new java.awt.Dimension(97, 21));
                                //deleteButton.setPreferredSize(new java.awt.Dimension(96, 21));
                                // delete doesn't work yet: 
                                    //deleteButton.setVisible(false); 
                            }
                        }
                        {
                            buttonPanel = new JPanel();
                            proxyTabPanel.add(buttonPanel, new CellConstraints("2, 13, 3, 1, default, default"));
                            buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                            {
                                okButton = new JButton();
                                buttonPanel.add(okButton);
                                okButton.setText("Ok");
                                okButton.addActionListener(proxyController);
                                
                                // check nl.uva.vlet.gui.utils.proxy: -> moved to AttributeEditorController.update() 
                                
                                //if (proxyController.gridProxy.valid == false)
                                    //    okButton.setEnabled(false);
                            }
                            {
                                cancelButton = new JButton();
                                buttonPanel.add(cancelButton);
                                cancelButton.setText("Cancel");
                                cancelButton.addActionListener(proxyController);
                                
                            }
                        }
                        
                    }
                }
//                {
//                    configTabPanel = new JPanel();
//                    mainTabPanel.addTab("CA Certificates", null, configTabPanel, null);
//                    configTabPanel.setEnabled(false);
//                    configTabPanel.setVisible(false);
//                    
//                }
            }

            //System.err.println("dailog     pref:"+this.getPreferredSize()); 
            //System.err.println("mainpanel  pref:"+this.mainPanel.getPreferredSize()); 
            //System.err.println("info panel pref:"+this.proxyInfoPanel.getPreferredSize());
            
            // use preferred size: buggy 
            Dimension size = this.getPreferredSize();
            
            if (size.width<500)
            	size.width=500;
            if (size.height<450)
            	size.height=450;

            //this.setSize(500, 450);
            pack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }   

//  ==========================================================================
//  Constructor 
//  ==========================================================================
    
    public GridProxyDialog(String message)
    {
        super();
        init(VRSContext.getDefault(),message);
    }
    
    public GridProxyDialog(VRSContext context,String message)
    {
        super();
        init(context,message);
    }

    public GridProxyDialog(VRSContext context,JFrame frame)
    {
        super(frame);
        init(context,null);
    }
    
    private void init(VRSContext context,String newMessage)
    {
    	if (newMessage!=null)	
    		this.message=newMessage;
        
        if (message==null) 
            message="Create Grid Proxy.";
    
      
        // Must first create ActionListener since it is used in initGui...

        this.proxyController = new GridProxyDialogController(context,this);
        initGUI();
        
        // update Gui Components depenend on the Proxy Status.

        proxyController.update();
     
        // relocate AFTER updatinf fields !  
        Rectangle rect = GuiSettings.getOptimalWindow(this); 
        this.setLocation(rect.x,rect.y);
        // No auto-show: this.setVisible(true);
    }
    
  
    public boolean getIsOK()
    {
        return this.proxyController.isOk; 
    }

    public void waitForUserInput()
	{
		synchronized(this)
		{
			try
			{
				this.wait(100*1000);
			}
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
		}		
	}
       
//  ==========================================================================
//  main 
//  ==========================================================================
    
    /**
     * Main method to tests the dialog
     */
    public static void main(String[] args)
    {
       askInitProxy("Testing GridProxyDialog");
    }

    /** blocking ask for input */ 
    public static boolean askInitProxy(String message)
    {
        GridProxyDialog dialog=new GridProxyDialog(message);
        
        if (GlobalConfig.isApplet()==false)
        	dialog.setAlwaysOnTop(true);
        
        dialog.setModal(true);
        // if modal is enabled, this method blocks until window is closed !
        dialog.setVisible(true);     
                 
        return dialog.getIsOK(); 
    }
    
    /** blocking ask for input */ 
    public static boolean askInitProxy(VRSContext context,String message)
    {
        GridProxyDialog dialog=new GridProxyDialog(context,message);
        
        if (GlobalConfig.isApplet()==false)
        	dialog.setAlwaysOnTop(true);
        
        dialog.setModal(true);
        // if modal is enabled, this method blocks until window is closed !
        dialog.setVisible(true);     
                 
        return dialog.getIsOK(); 
    }

    public static boolean askInitProxy()
    {
        return askInitProxy("Create Proxy."); 
    }
	
}
