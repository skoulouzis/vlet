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
 * $Id: ResourceEditor.java,v 1.6 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */
// source: 

package nl.uva.vlet.gui.editors;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.panels.attribute.AttributePanel;
import nl.uva.vlet.gui.panels.fields.AttrCheckBoxField;
import nl.uva.vlet.gui.panels.fields.AttrParameterField;
import nl.uva.vlet.gui.panels.fields.AttrPortField;
import nl.uva.vlet.gui.panels.fields.AttrSchemeField;
import nl.uva.vlet.gui.panels.fields.IAttributeField;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * New Resource Editor Form for LinkNodes, ResourcNodes, etc and
 * "Show Properties"
 */
public class ResourceEditor extends javax.swing.JDialog
{

    {
        // // Set Look & Feel
        // try
        // {
        // javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
    }

    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        ResourceEditor inst = new ResourceEditor(frame);
        // inst.setAttributes(getDummyAttrs(),true);
        inst.setVisible(true);
    }

    public static void editProperties(final ProxyNode pnode, final boolean fullConfiguration)
    {
        // always inialize swing component during swing thread !
        Runnable starter = new Runnable()
        {
            public void run()
            {
                ResourceEditor inst = new ResourceEditor(null);
                // java 1.6:!
                // inst.setModalityType(type);
                // inst.setModal(block);
                // center !
                inst.setLocationRelativeTo(null);
                inst.controller.setEnableFullConfiguration(fullConfiguration);
                inst.setNode(pnode);

                inst.setAlwaysOnTop(true);

                // show:
                inst.setVisible(true); // if modal==true; here it blocks;
            }
        };

        UIGlobal.swingInvokeLater(starter);
        // starter.run();

    }

    public static void editAttributes(final ProxyNode pnode)
    {
        // always inialize swing component during swing thread !
        Runnable starter = new Runnable()
        {
            public void run()
            {
                ResourceEditor inst = new ResourceEditor(null);
                // java 1.6:!
                // inst.setModalityType(type);
                // inst.setModal(block);
                // center !
                inst.setLocationRelativeTo(null);
                inst.setPlainAttributeEditor(true);
                inst.setNode(pnode);

                inst.setAlwaysOnTop(true);

                // show:
                inst.setVisible(true); // if modal==true; here it blocks;
            }
        };

        UIGlobal.swingInvokeLater(starter);
        // starter.run();

    }

    // ======================================================================//
    //
    // ======================================================================//
    //
    // ======================================================================//

    private static final long serialVersionUID = -8406717147853530851L;

    private JPanel mainPanel;

    AttrSchemeField schemeSB;

    AttrParameterField hostnameField;

    private JLabel portLabel;

    AttrPortField portField;

    private JLabel pathLabel;

    AttrParameterField pathField;

    private JPanel locationAttrsPnl;

    private JLabel theIcon;

    private JLabel schemeLabel;

    private JLabel typeLabel;

    AttrParameterField typeField;

    private ResourceEditorController controller;

    private JLabel nameLabel;

    AttrParameterField nameField;

    private JLabel hostnameLabel;

    private JPanel locationTopPnl;

    private JPanel tabLocationAttrsPnl;

    JTextField serverSettingUriFld;

    private JLabel serverLocationLbl;

    private JLabel serverSettingTopLbl;

    private JPanel serverConfigTopPnl;

    private JScrollPane serverConfigScrollPane;

    private JLabel propertiesPanelLbl;

    private JPanel propertiesTopPanel;

    private JPanel tabPropertiesPnl;

    JButton serverConfigNewB;

    JButton serverConfigDeleteB;

    AttributePanel serverConfigAttrPanel;

    private JPanel serverConfigButtonPnl;

    private JTextField resourceInfoTextField;

    private JScrollPane propertiesPanelScrollP;

    JTabbedPane tabPane;

    private JLabel iconOptionsLbl;

    private AttrCheckBoxField showShortCutIconX;

    private AttrParameterField iconUrlField;

    private JLabel showShortCutIconLbl;

    private JLabel iconUrlLbl;

    private JPanel tabIconAttrsPnl;

    private AttributePanel propertiesAttrPanel;

    JPanel tabServerConfigPnl;

    private JPanel iconPanel;

    private JPanel topPanel;

    JCheckBox uriAttrEnableCB;

    private JLabel uriAttrsEnableLbl;

    JButton resetB;

    JButton okB;

    JButton cancelB;

    JButton applyB;

    private JPanel buttonPanel;

    AttrParameterField uriFragmentField;

    AttrParameterField uriQueryField;

    private JLabel uriQueryLabel;

    private JLabel uriFragLabel;

    private JLabel jLabel4;

    private JLabel topPanelResourceLbl;

    ArrayList<IAttributeField> attributeFields = new ArrayList<IAttributeField>();

    public ResourceEditor(JFrame frame)
    {
        super(frame);
        init(true);
    }

    /** Default Constructor called by Jigloo ! */
    public ResourceEditor()
    {
        super();
        init(true);
    }

    private void init(boolean initGUI)
    {
        UIPlatform.getPlatform().getWindowRegistry().register(this);

        this.controller = new ResourceEditorController(this);
        if (initGUI)
            initGUI();
        // this.configAttrPanel.addAttributeListener(this.controller);
        // configAttrPanel.setAttributes(null,false);
    }

    private void initGUI()
    {
        try
        {
            {
                this.setSize(433, 606);
            }
            {
                mainPanel = new JPanel();
                BorderLayout mainPanelLayout = new BorderLayout();
                mainPanel.setLayout(mainPanelLayout);
                getContentPane().add(mainPanel, BorderLayout.CENTER);
                mainPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(BevelBorder.RAISED),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                mainPanel.setPreferredSize(new java.awt.Dimension(425, 573));
                // mainPanel.setPreferredSize(new java.awt.Dimension(475, 432));

                // add sub Panels !
                {
                    mainPanel.add(getTopPanel(), BorderLayout.NORTH);
                    mainPanel.add(getButtonPanel(), BorderLayout.SOUTH);
                    mainPanel.add(getTabPane(), BorderLayout.CENTER);
                }

            }
            Dimension prefSize = getPreferredSize();
            prefSize.height += 10;
            prefSize.width += 10;
            // repack
            // this.setSize(prefSize);
            // this.repack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    protected void setPlainAttributeEditor(boolean plain)
    {
        this.controller.setEnableFullConfiguration(plain == false);
    }

    // Do some manual tweaking to resize the dialog.
    public void repack()
    {
        // do some manual tweaking to resize the dialog.
        Dimension tabPrefSize = this.tabPane.getPreferredSize();
        Dimension propPrefSize = propertiesAttrPanel.getPreferredSize();
        Dimension serverPrefSize = this.serverConfigAttrPanel.getPreferredSize();
        Dimension newSize = max(tabPrefSize, propPrefSize);
        newSize = max(newSize, serverPrefSize);

        if ((newSize.width > tabPrefSize.width) || (newSize.height > tabPrefSize.height))
            tabPane.setPreferredSize(newSize);

        this.validate(); // validate now & update
        this.setSize(this.getPreferredSize());
    }

    private Dimension max(Dimension size1, Dimension size2)
    {
        int h = size1.height;
        int w = size1.width;
        if (size2.height > h)
            h = size2.height;
        if (size2.width > w)
            w = size2.width;
        return new Dimension(w, h);
    }

    private AttrPortField getPortField()
    {
        if (portField == null)
        {
            portField = createPortField(0, controller.getAttributeListener());
        }

        return portField;
    }

    private Icon getDummyIcon()
    {
        ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("web/icon.png"));
        return icon;
    }

    private AttrSchemeField createAttrSchemeField(String scheme, String[] values,
            IComboFocusActionListener attributeFocusActionListener)
    {
        AttrSchemeField field = new AttrSchemeField(scheme, values);
        field.addActionListener(attributeFocusActionListener);
        field.addFocusListener(attributeFocusActionListener);
        this.attributeFields.add(field);
        return field;
    }

    private AttrParameterField createAttributeField(String name, String dummyValue,
            IComboFocusActionListener attributeFocusActionListener)
    {
        AttrParameterField field = new AttrParameterField(name, dummyValue);
        field.addActionListener(attributeFocusActionListener);
        field.addFocusListener(attributeFocusActionListener);
        this.attributeFields.add(field);
        return field;
    }

    private AttrPortField createPortField(int dummyValue, IComboFocusActionListener attributeFocusActionListener)
    {
        AttrPortField field = new AttrPortField(ATTR_PORT, dummyValue);
        field.addActionListener(attributeFocusActionListener);
        field.addFocusListener(attributeFocusActionListener);

        this.attributeFields.add(field);
        return field;
    }

    private AttrCheckBoxField createCheckBoxField(String name, boolean value, IComboFocusActionListener listener)
    {
        AttrCheckBoxField field = new AttrCheckBoxField(name, value);
        field.addActionListener(listener);
        field.addFocusListener(listener);
        this.attributeFields.add(field);
        return field;
    }

    public void setNode(ProxyNode pnode)
    {
        this.controller.updateNode(pnode);
    }

    public void setResourceIcon(Icon resourceIcon)
    {
        this.theIcon.setIcon(resourceIcon);
    }

    private JLabel resourceSettingsLbl()
    {
        if (topPanelResourceLbl == null)
        {
            topPanelResourceLbl = new JLabel();
            topPanelResourceLbl.setText("Resource Location Settings");
            topPanelResourceLbl.setFont(new java.awt.Font("Dialog", 1, 12));
            topPanelResourceLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            topPanelResourceLbl.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    if (e.getClickCount() == 2)
                        enableFullConfiguration(true);
                }
            });

        }
        return topPanelResourceLbl;
    }

    public void setTitle(String text)
    {
        super.setTitle(text);
        this.setPanelTitle(text);

    }

    protected void setPanelTitle(String text)
    {
        topPanelResourceLbl.setText(text);
    }

    private JLabel locationLbl()
    {
        if (jLabel4 == null)
        {
            jLabel4 = new JLabel();
            jLabel4.setText("Location:");
            jLabel4.setFont(new java.awt.Font("Dialog", 1, 12));
        }
        return jLabel4;
    }

    public void updateResourceName(String name)
    {
        this.nameField.setText(name);
    }

    public void updateResourceType(String type)
    {
        this.typeField.setText(type);
    }

    private JLabel uriFraqLbl()
    {
        if (uriFragLabel == null)
            uriFragLabel = new JLabel("URI Fragment:");
        return uriFragLabel;
    }

    private JLabel uriQueryLbl()
    {
        if (uriQueryLabel == null)
            uriQueryLabel = new JLabel("URI Query:");

        return uriQueryLabel;
    }

    private AttrParameterField getUriQueryField()
    {
        if (uriQueryField == null)
        {
            uriQueryField = createAttributeField(VAttributeConstants.ATTR_URI_QUERY, "",
                    this.controller.getAttributeListener());

            uriQueryField.setEnabled(false);
        }

        return uriQueryField;
    }

    private AttrParameterField getUriFragmentField()
    {
        if (uriFragmentField == null)
        {
            uriFragmentField = createAttributeField(VAttributeConstants.ATTR_URI_FRAGMENT, "",
                    this.controller.getAttributeListener());

            uriFragmentField.setEnabled(false);
        }
        return uriFragmentField;
    }

    public void updateResourceHostname(String hostname)
    {
        this.hostnameField.setText(hostname);
    }

    public void setResourcePort(String port)
    {
        this.portField.setText(port);
    }

    public void updateResourcePath(String path)
    {
        this.pathField.setText(path);
    }

    // public void updateIconURL(String iconURL)
    // {
    // this.iconUrlField.setText(iconURL);
    // }

    public String getSchemeValue()
    {
        return this.schemeSB.getValue();
    }

    public String getHostnameValue()
    {
        return this.hostnameField.getValue();
    }

    public void setScheme(String scheme)
    {
        this.schemeSB.setValue(scheme);
    }

    public void getPathValue()
    {
        this.getPathField().getValue();
    }

    public void setScheme(String scheme, String[] schemes)
    {
        this.schemeSB.setValues(schemes);
        this.schemeSB.setValue(scheme);
    }

    private JPanel getButtonPanel()
    {
        if (buttonPanel == null)
        {
            buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            buttonPanel.add(getApplyB());
            buttonPanel.add(getResetB());
            buttonPanel.add(getCancelB());
            buttonPanel.add(getOkB());
        }
        return buttonPanel;
    }

    private JButton getApplyB()
    {
        if (applyB == null)
        {
            applyB = new JButton();
            applyB.setText("Apply");
            applyB.addActionListener(this.controller);
            applyB.setEnabled(false);
        }
        return applyB;
    }

    private JButton getCancelB()
    {
        if (cancelB == null)
        {
            cancelB = new JButton();
            cancelB.setText("Cancel");
            cancelB.addActionListener(this.controller);
        }
        return cancelB;
    }

    private JButton getOkB()
    {
        if (okB == null)
        {
            okB = new JButton();
            okB.setText("OK");
            okB.addActionListener(this.controller);
        }
        return okB;
    }

    private JButton getResetB()
    {
        if (resetB == null)
        {
            resetB = new JButton();
            resetB.setText("Reset");
            resetB.setEnabled(false);
            resetB.addActionListener(this.controller);
        }
        return resetB;
    }

    public void dispose()
    {
        super.dispose();
    }

    private JCheckBox getUriAttrEnableCB()
    {
        if (uriAttrEnableCB == null)
        {
            uriAttrEnableCB = new JCheckBox();
            uriAttrEnableCB.setText("enable URI attributes");
            uriAttrEnableCB.setFont(new java.awt.Font("Dialog", 2, 12));
            uriAttrEnableCB.setActionCommand("enableURIAttributesCB");
            uriAttrEnableCB.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
            uriAttrEnableCB.addActionListener(this.controller);
            // disable by default!
            uriAttrEnableCB.setEnabled(false);
        }
        return uriAttrEnableCB;
    }

    // public void setShowLinkIcon(boolean value)
    // {
    // this.showShortCutCB.setSelected(value);
    //
    // }

    public IAttributeField getAttributeField(String name)
    {
        for (IAttributeField field : this.attributeFields)
            if (StringUtil.equals(field.getName(), name))
                return field;

        return null;
    }

    private JLabel uriAttrsEnableLbl()
    {
        if (uriAttrsEnableLbl == null)
        {
            uriAttrsEnableLbl = new JLabel();
            uriAttrsEnableLbl.setText("URI Attributes:");
            uriAttrsEnableLbl.setFont(new java.awt.Font("Dialog", 1, 12));
        }
        return uriAttrsEnableLbl;
    }

    private JPanel getTopPanel()
    {
        if (topPanel == null)
        {
            topPanel = new JPanel();

            FormLayout topPanelLayout = new FormLayout(
                    "5dlu, 5dlu, 47dlu, 5dlu, max(p;30dlu), 5dlu, 40dlu, 10dlu:grow, 7dlu, 4dlu",
                    "4dlu, max(p;12dlu), 4dlu, max(p;8dlu), max(p;8dlu), max(p;8dlu), 11dlu, 7dlu, max(p;4dlu)");
            topPanel.setLayout(topPanelLayout);
            topPanel.setPreferredSize(new java.awt.Dimension(403, 121));
            topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            topPanel.add(resourceSettingsLbl(), new CellConstraints("2, 2, 8, 1, default, default"));
            {
                typeLabel = new JLabel();
                topPanel.add(typeLabel, new CellConstraints("5, 5, 1, 1, default, default"));
                typeLabel.setText("type:");
            }
            {
                topPanel.add(getTypeField(), new CellConstraints("7, 5, 2, 1, default, default"));
                typeField.setEditable(false);
            }
            {
                nameLabel = new JLabel();
                topPanel.add(nameLabel, new CellConstraints("5, 6, 1, 1, default, default"));
                nameLabel.setText("name:");
            }
            {
                topPanel.add(getNameField(), new CellConstraints("7, 6, 2, 1, default, default"));
                topPanel.add(getIconPanel(), new CellConstraints("3, 4, 1, 4, default, default"));
            }
        }
        return topPanel;
    }

    private JComponent getNameField()
    {
        if (nameField == null)
        {
            nameField = createAttributeField(VAttributeConstants.ATTR_NAME, "<name>",
                    this.controller.getAttributeListener());
        }
        return nameField;
    }

    private JComponent getTypeField()
    {
        if (typeField == null)
        {
            typeField = createAttributeField(VAttributeConstants.ATTR_TYPE, "<TYPE>",
                    this.controller.getAttributeListener());
        }
        return typeField;
    }

    private JPanel getIconPanel()
    {
        if (iconPanel == null)
        {
            iconPanel = new JPanel();
            BorderLayout jPanel1Layout = new BorderLayout();
            iconPanel.setLayout(jPanel1Layout);
            iconPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            {
                theIcon = new JLabel();
                iconPanel.add(theIcon);
                theIcon.setIcon(getDummyIcon());
                BorderLayout theIconLayout = new BorderLayout();
                theIcon.setLayout(theIconLayout);
                theIcon.setPreferredSize(new java.awt.Dimension(92, 66));
            }
        }
        return iconPanel;
    }

    private AttrParameterField getPathField()
    {
        if (pathField == null)
        {
            pathField = createAttributeField(VAttributeConstants.ATTR_PATH, "<PATH>",
                    this.controller.getAttributeListener());
        }
        return pathField;
    }

    private AttrSchemeField getSchemeSB()
    {
        if (schemeSB == null)
        {
            schemeSB = createAttrSchemeField(VAttributeConstants.ATTR_SCHEME, new String[]
            { "<SCHEME1>", "<SCHEME2>" }, this.controller.getAttributeListener());
        }

        return schemeSB;

    }

    private AttrParameterField getHostnameField()
    {
        if (hostnameField == null)
            hostnameField = createAttributeField(VAttributeConstants.ATTR_HOSTNAME, "<HOSTNAME>",
                    this.controller.getAttributeListener());
        return hostnameField;
    }

    boolean hasComponent(Container container, Component comp)
    {
        Component[] comps = container.getComponents();
        if (comps != null)
        {
            for (Component c : comps)
                if (c.equals(comp))
                    return true;
        }

        return false;
    }

    private JPanel getServerConfigPanel()
    {
        if (tabServerConfigPnl == null)
        {
            tabServerConfigPnl = new JPanel();
            BorderLayout serverConfigPanelLayout = new BorderLayout();
            tabServerConfigPnl.setLayout(serverConfigPanelLayout);
            tabServerConfigPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            tabServerConfigPnl.add(getServerConfigButtonPnl(), BorderLayout.SOUTH);
            tabServerConfigPnl.add(getServerConfigScrollPane(), BorderLayout.CENTER);
            tabServerConfigPnl.add(getServerConfigTopPnl(), BorderLayout.NORTH);
            // getServerConfigHeaderPnl();
            tabServerConfigPnl.addFocusListener(this.controller.getPanelListener());
        }
        return tabServerConfigPnl;
    }

    private AttributePanel getConfigAttrsPanel()
    {
        if (propertiesAttrPanel == null)
        {
            propertiesAttrPanel = new AttributePanel();
            propertiesAttrPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            propertiesAttrPanel.addAttributeListener(this.controller.getAttributeListener());
        }
        return propertiesAttrPanel;
    }

    private JPanel getTabIconAttrsPnl()
    {
        if (tabIconAttrsPnl == null)
        {
            tabIconAttrsPnl = new JPanel();
            FormLayout iconAttrsPanelLayout = new FormLayout("5dlu, 36dlu, max(p;5dlu), 54dlu, 75dlu:grow, 8dlu",
                    "5dlu, max(p;15dlu), 5dlu, max(p;5dlu), max(p;5dlu)");
            tabIconAttrsPnl.setLayout(iconAttrsPanelLayout);
            tabIconAttrsPnl.setPreferredSize(new java.awt.Dimension(395, 263));
            tabIconAttrsPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            tabIconAttrsPnl.add(getIconUrlLbl(), new CellConstraints("2, 4, 1, 1, right, default"));
            tabIconAttrsPnl.add(getShowShortCutIconLbl(), new CellConstraints("2, 5, 1, 1, right, default"));
            tabIconAttrsPnl.add(getIconUrlField(), new CellConstraints("4, 4, 2, 1, default, default"));
            tabIconAttrsPnl.add(getShowShortCutIconX(), new CellConstraints("4, 5, 2, 1, default, default"));
            tabIconAttrsPnl.add(getIconOptionsLbl(), new CellConstraints("2, 2, 4, 1, default, default"));
        }
        return tabIconAttrsPnl;
    }

    private JLabel getIconUrlLbl()
    {
        if (iconUrlLbl == null)
        {
            iconUrlLbl = new JLabel();
            iconUrlLbl.setText("iconURl:");
        }
        return iconUrlLbl;
    }

    private JLabel getShowShortCutIconLbl()
    {
        if (showShortCutIconLbl == null)
        {
            showShortCutIconLbl = new JLabel();
            showShortCutIconLbl.setText("iconOption:");
        }
        return showShortCutIconLbl;
    }

    private AttrCheckBoxField getShowShortCutIconX()
    {
        if (showShortCutIconX == null)
        {
            showShortCutIconX = createCheckBoxField(VAttributeConstants.ATTR_SHOW_SHORTCUT_ICON, true,
                    this.controller.getAttributeListener());
            showShortCutIconX.setText("show shortcut icon");
        }
        return showShortCutIconX;
    }

    private AttrParameterField getIconUrlField()
    {
        if (iconUrlField == null)
            iconUrlField = createAttributeField(VAttributeConstants.ATTR_ICONURL, "",
                    this.controller.getAttributeListener());
        return iconUrlField;
    }

    public void setConfigPanelAttributes(VAttributeSet attrs, boolean editable)
    {
        this.propertiesAttrPanel.setAttributes(attrs, editable);

    }

    private JLabel getIconOptionsLbl()
    {
        if (iconOptionsLbl == null)
        {
            iconOptionsLbl = new JLabel();
            iconOptionsLbl.setText("Icon options:");
            iconOptionsLbl.setFont(new java.awt.Font("Dialog", 1, 12));
            iconOptionsLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        return iconOptionsLbl;
    }

    private JTabbedPane getTabPane()
    {
        if (tabPane == null)
        {
            tabPane = new JTabbedPane();

            tabPane.setPreferredSize(new java.awt.Dimension(406, 265));
            tabPane.addTab("Location", null, this.getTabLocationAttrsPnl(), null);
            tabPane.addTab("Properties", null, getTabPropertiesPnl(), null);
            tabPane.addTab("Server Settings", null, getServerConfigPanel(), null);
            tabPane.addTab("Icon Properties", null, getTabIconAttrsPnl(), null);
            tabPane.addChangeListener((ChangeListener) this.controller.getPanelListener());
        }
        return tabPane;
    }

    private JScrollPane getConfigAttrsScrollPane()
    {
        if (propertiesPanelScrollP == null)
        {
            propertiesPanelScrollP = new JScrollPane();
            propertiesPanelScrollP.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            propertiesPanelScrollP.setViewportView(getConfigAttrsPanel());
        }
        return propertiesPanelScrollP;
    }

    public void setEnableServerConfigPanel(boolean val)
    {
        if (val == false)
            this.tabPane.remove(tabServerConfigPnl);

    }

    public void setEnableIconAttrsPanel(boolean val)
    {
        if (val == false)
            this.tabPane.remove(tabIconAttrsPnl);

    }

    public void setEnableLocationAttrsPanel(boolean val)
    {
        if (val == false)
            this.tabPane.remove(this.tabLocationAttrsPnl);

    }

    public void setEnablePropertiesPanel(boolean val)
    {
        if (val == false)
            this.tabPane.remove(this.tabPropertiesPnl);
    }

    protected void enableFullConfiguration(boolean enable)
    {
        if (enable)
        {
            this.controller.setEnableFullConfiguration(true);
            // if already added, tab will skip them.
            tabPane.addTab("Location", null, this.getTabLocationAttrsPnl(), null);
            tabPane.addTab("Properties", null, getTabPropertiesPnl(), null);
            tabPane.addTab("Server Settings", null, getServerConfigPanel(), null);
            tabPane.addTab("Icon Properties", null, getTabIconAttrsPnl(), null);
        }
    }

    private JTextField getResourceInfoTextField()
    {
        if (resourceInfoTextField == null)
        {
            resourceInfoTextField = new JTextField(10);
            resourceInfoTextField.setText("Location Properties");
            resourceInfoTextField.setEditable(false);
            resourceInfoTextField.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            resourceInfoTextField.setFont(new java.awt.Font("Dialog", 1, 12));
        }
        return resourceInfoTextField;
    }

    private JPanel getServerConfigButtonPnl()
    {
        if (serverConfigButtonPnl == null)
        {
            serverConfigButtonPnl = new JPanel();
            serverConfigButtonPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            serverConfigButtonPnl.add(getServerConfigNewB());
            serverConfigButtonPnl.add(getServerConfigDeleteB());
        }
        return serverConfigButtonPnl;
    }

    private JPanel getServerConfigAttrPanel()
    {
        if (serverConfigAttrPanel == null)
        {
            serverConfigAttrPanel = new AttributePanel();
            serverConfigAttrPanel.setPreferredSize(new java.awt.Dimension(378, 128));
            serverConfigAttrPanel.addAttributeListener(this.controller.getServerAttributeListener());
        }
        return serverConfigAttrPanel;
    }

    private JButton getServerConfigDeleteB()
    {
        if (serverConfigDeleteB == null)
        {
            serverConfigDeleteB = new JButton();
            serverConfigDeleteB.setText("Delete");
            serverConfigDeleteB.addActionListener(this.controller);
        }
        return serverConfigDeleteB;
    }

    private JButton getServerConfigNewB()
    {
        if (serverConfigNewB == null)
        {
            serverConfigNewB = new JButton();
            serverConfigNewB.setText("New");
            serverConfigNewB.setActionCommand("NewServerConfig");
            serverConfigNewB.addActionListener(this.controller);
        }
        return serverConfigNewB;
    }

    private JPanel getTabPropertiesPnl()
    {
        if (tabPropertiesPnl == null)
        {
            tabPropertiesPnl = new JPanel();
            BorderLayout propertiesPanelLayout = new BorderLayout();
            tabPropertiesPnl.setLayout(propertiesPanelLayout);
            tabPropertiesPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            tabPropertiesPnl.add(getPropertiesTopPanel(), BorderLayout.NORTH);
            tabPropertiesPnl.add(getConfigAttrsScrollPane(), BorderLayout.CENTER);
        }
        return tabPropertiesPnl;
    }

    private JPanel getPropertiesTopPanel()
    {
        if (propertiesTopPanel == null)
        {
            propertiesTopPanel = new JPanel();
            FormLayout propertiesTopPanelLayout = new FormLayout("max(p;5dlu), 63dlu, 98dlu:grow, max(p;5dlu)",
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu), max(p;5dlu)");
            propertiesTopPanel.setLayout(propertiesTopPanelLayout);
            propertiesTopPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            propertiesTopPanel.add(getPropertiesPanelLbl(), new CellConstraints("2, 2, 2, 1, default, default"));
        }
        return propertiesTopPanel;
    }

    public void setServerConfigAttributes(VAttributeSet attrSet, boolean editable)
    {
        this.serverConfigAttrPanel.setAttributes(attrSet, editable);

    }

    private JLabel getPropertiesPanelLbl()
    {
        if (propertiesPanelLbl == null)
        {
            propertiesPanelLbl = new JLabel();
            propertiesPanelLbl.setText("Properties");
            propertiesPanelLbl.setFont(new java.awt.Font("Dialog", 1, 12));
            propertiesPanelLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        return propertiesPanelLbl;
    }

    private JScrollPane getServerConfigScrollPane()
    {
        if (serverConfigScrollPane == null)
        {
            serverConfigScrollPane = new JScrollPane();
            serverConfigScrollPane.setPreferredSize(new java.awt.Dimension(398, 114));
            serverConfigScrollPane.setViewportView(getServerConfigAttrPanel());
        }
        return serverConfigScrollPane;
    }

    public Component getActiveTab()
    {
        return tabPane.getSelectedComponent();
    }

    private JPanel getServerConfigTopPnl()
    {
        if (serverConfigTopPnl == null)
        {
            serverConfigTopPnl = new JPanel();
            FormLayout serverConfigTopPnlLayout = new FormLayout("max(p;5dlu), 42dlu, max(p;5dlu), 67dlu, 69dlu, 5dlu",
                    "max(p;5dlu), 11dlu, 7dlu, max(p;15dlu), max(p;5dlu)");
            serverConfigTopPnl.setLayout(serverConfigTopPnlLayout);
            serverConfigTopPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            serverConfigTopPnl.add(getServerSettingTopLbl(), new CellConstraints("2, 2, 4, 1, default, default"));
            serverConfigTopPnl.add(getServerLocationLbl(), new CellConstraints("2, 4, 1, 1, right, default"));
            serverConfigTopPnl.add(getServerSettingUriFld(), new CellConstraints("4, 4, 2, 1, default, default"));
        }
        return serverConfigTopPnl;
    }

    private JLabel getServerSettingTopLbl()
    {
        if (serverSettingTopLbl == null)
        {
            serverSettingTopLbl = new JLabel();
            serverSettingTopLbl.setText("Server Settings");
            serverSettingTopLbl.setFont(new java.awt.Font("Dialog", 1, 12));
            serverSettingTopLbl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        return serverSettingTopLbl;
    }

    private JLabel getServerLocationLbl()
    {
        if (serverLocationLbl == null)
        {
            serverLocationLbl = new JLabel();
            serverLocationLbl.setText("server URI:");
        }
        return serverLocationLbl;
    }

    private JTextField getServerSettingUriFld()
    {
        if (serverSettingUriFld == null)
        {
            serverSettingUriFld = new JTextField();
            serverSettingUriFld.setText("<Server Location>");
        }
        return serverSettingUriFld;
    }

    private JPanel getTabLocationAttrsPnl()
    {
        if (tabLocationAttrsPnl == null)
        {
            tabLocationAttrsPnl = new JPanel();
            BorderLayout tabLocationAttrsPnlLayout = new BorderLayout();
            tabLocationAttrsPnl.setLayout(tabLocationAttrsPnlLayout);
            tabLocationAttrsPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            {
                locationAttrsPnl = new JPanel();
                tabLocationAttrsPnl.add(getLocationTopPnl(), BorderLayout.NORTH);
                tabLocationAttrsPnl.add(locationAttrsPnl, BorderLayout.CENTER);

                FormLayout topPanelFormLayout = new FormLayout(
                        "5dlu, 50dlu, 5dlu, 70dlu, 10dlu:grow, 5dlu",
                        "max(p;5dlu), max(p;8dlu), 5dlu, max(p;8dlu), max(p;8dlu), max(p;8dlu), max(p;8dlu), max(p;8dlu), 5dlu, max(p;8dlu), max(p;8dlu), max(p;8dlu)");
                locationAttrsPnl.setLayout(topPanelFormLayout);

                locationAttrsPnl.setPreferredSize(new java.awt.Dimension(479, 177));
                locationAttrsPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                // resourceConfigPanel.setPreferredSize(new
                // java.awt.Dimension(472, 227));

                {
                    schemeLabel = new JLabel();
                    locationAttrsPnl.add(schemeLabel, new CellConstraints("2, 5, 1, 1, right, default"));
                    schemeLabel.setText("scheme:");
                }
                {
                    locationAttrsPnl.add(getSchemeSB(), new CellConstraints("4, 5, 2, 1, default, default"));
                    schemeSB.addActionListener(this.controller);
                    // schemeCB = new JComboBox();

                    // schemeCB.setPreferredSize(new java.awt.Dimension(119,
                    // 21));
                }
                {
                    hostnameLabel = new JLabel();
                    locationAttrsPnl.add(hostnameLabel, new CellConstraints("2, 6, 1, 1, right, default"));
                    hostnameLabel.setText("hostname:");
                }
                {
                    portLabel = new JLabel();
                    locationAttrsPnl.add(portLabel, new CellConstraints("2, 7, 1, 1, right, default"));
                    portLabel.setText("port:");
                }
                {
                    pathLabel = new JLabel();
                    locationAttrsPnl.add(pathLabel, new CellConstraints("2, 8, 1, 1, right, default"));
                    pathLabel.setText("path:");
                }
                {
                    locationAttrsPnl.add(getHostnameField(), new CellConstraints("4, 6, 2, 1, default, default"));
                    locationAttrsPnl.add(getPortField(), new CellConstraints("4, 7, 2, 1, default, default"));
                }
                {

                    locationAttrsPnl.add(locationLbl(), new CellConstraints("2, 4, 4, 1, default, default"));
                    locationAttrsPnl.add(getPathField(), new CellConstraints("4, 8, 2, 1, default, default"));

                    locationAttrsPnl.add(uriFraqLbl(), new CellConstraints("2, 12, 1, 1, default, default"));
                    locationAttrsPnl.add(uriQueryLbl(), new CellConstraints("2, 11, 1, 1, right, default"));
                    locationAttrsPnl.add(uriAttrsEnableLbl(), new CellConstraints("2, 10, 2, 1, default, default"));

                    locationAttrsPnl.add(getUriAttrEnableCB(), new CellConstraints("4, 10, 2, 1, default, default"));
                    locationAttrsPnl.add(getUriQueryField(), new CellConstraints("4, 11, 2, 1, default, default"));
                    locationAttrsPnl.add(getUriFragmentField(), new CellConstraints("4, 12, 2, 1, default, default"));

                }
            }
        }
        return tabLocationAttrsPnl;
    }

    private JPanel getLocationTopPnl()
    {
        if (locationTopPnl == null)
        {
            locationTopPnl = new JPanel();
            FormLayout locationTopPnlLayout = new FormLayout("max(p;5dlu), 66dlu, 115dlu:grow, max(p;5dlu)",
                    "max(p;5dlu), max(p;5dlu), max(p;5dlu)");
            locationTopPnl.setLayout(locationTopPnlLayout);
            locationTopPnl.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            locationTopPnl.add(getResourceInfoTextField(), new CellConstraints("2, 2, 2, 1, default, default"));
        }
        return locationTopPnl;
    }

    public void setDailogText(String dialogText)
    {
        // this.getTopDialogTextPane().setText(dialogText);
    }

    public void clearFields(boolean enableEditable)
    {
        getHostnameField().setText("");
        getPortField().setText("");
        getPathField().setText("");
        getSchemeSB().setValue("");

        getHostnameField().setEditable(enableEditable);
        getPortField().setEditable(enableEditable);
        getPathField().setEditable(enableEditable);
        getSchemeSB().setEditable(enableEditable);

    }

    public void setEnableURIAttributes(boolean supported, boolean selected)
    {
        uriFragmentField.setEnabled(supported);
        uriFragmentField.setEditable(selected);

        uriQueryField.setEnabled(supported);
        uriQueryField.setEditable(selected);

        uriAttrEnableCB.setEnabled(supported);
        uriAttrEnableCB.setSelected(selected);

    }

}
