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
 * $Id: VBrowser.java,v 1.15 2011-04-27 14:40:55 ptdeboer Exp $  
 * $Date: 2011-04-27 14:40:55 $
 */ 
// source: 

package nl.uva.vlet.gui.vbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.TooManyListenersException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.gui.GuiPropertyName;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.Messages;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.actions.ActionCommand;
import nl.uva.vlet.gui.actions.ActionCommandType;
import nl.uva.vlet.gui.dialog.AboutDialog;
import nl.uva.vlet.gui.icons.IconsPanel;
import nl.uva.vlet.gui.table.TablePanel;
import nl.uva.vlet.gui.tree.ResourceTree;
import nl.uva.vlet.gui.viewers.ViewerInfo;
import nl.uva.vlet.gui.viewers.ViewerRegistry;
import nl.uva.vlet.gui.viewers.ViewerRegistry.ViewerList;
import nl.uva.vlet.gui.widgets.NavigationBar;

/**
 * Main VBrowser Frame <br>
 * It is a composite panel managing the various gui components. 
 * It is controlled by the BrowserController.
 * 
 * @see BrowserController
 */
public class VBrowser extends javax.swing.JFrame
{
    private static final long serialVersionUID = -4261195481107309909L;

    /** Browser ID class counter */
    private static int browsercounter = 0;

    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(VBrowser.class); 
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    /** Browser number */
    private int browserId = browsercounter++;

    // ==================================
    // Menu Stuff
    // ==================================
    
    private JMenu helpMenu;
    JMenuItem pasteMenuItem;
    JMenuItem copyMenuItem;
    JMenuItem cutMenuItem;
    private JMenu editMenu;
    private JMenuItem exitMenuItem;
    private JMenuItem createNoneMenuItem;
    JMenu createMenuItem;
    private JMenuItem viewRefreshAll;
    private JMenuItem viewNewWindowMenuItem;
    private JMenu view_menu;
    JMenuItem createLinkToMenuItem;
    private JLabel globalFolderMenuItem;
    JCheckBoxMenuItem globalShowLogWindowMenu;
    JCheckBoxMenuItem globalFilterHiddenFilesMenu;
    JCheckBoxMenuItem globalShowResourceTreeMenu;
    private JMenuItem forceDeleteMenuItem;
    private JRadioButtonMenuItem viewAsIconsRB;
    private JRadioButtonMenuItem viewAsListRB;
    private JMenu customTools;
    private JMenu windowsMenu;
    private JMenuItem gridstartMenuItem;
    JMenuItem pasteAsLinkMenuItem;
    private JMenu cogUtilsMenu;
    private JMenu toolsMenu;
    private JMenuItem cogProxyInitMenuItem;
    private JMenuItem proxyDialogMenuItem;
    private JMenu proxyMenu;
    JCheckBoxMenuItem singleClickActionMenuItem;
    private JMenu preferencesMenu;

    private JMenuItem closeMenuItem;
    private JMenuItem showTasksMenuItem;
    private JMenu debugMenu;
    private JMenu mainMenu;
    private JMenuBar main_menubar;
    private JMenu lafMenu ;
    private JMenuItem nativeLAFmenuItem ;
    private JMenuItem aboutMenuItem;
    private JMenuItem metalLAFmenuItem;
    private JMenuItem gtkLAFmenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem openInWinMenuItem;
    JMenuItem renameMenuItem;
    JMenuItem deleteMenuItem;
    
    // ==================================
    // Buttons
    // ==================================
    JButton credentialButton;
    private JButton viewAsListBut;
    private JButton viewAsIconRows;
    private JButton viewAsIconsBut;
    JButton stopButton;
    
    // ==================================
    // Browser SubPanels
    // ==================================
    
    BrowserSplitPane splitPaneManager;
    private BrowserScrollPane treeResourceScrollPane;
    BrowserTabPane resourceTabbedPane;
    BrowserTabPane mangedTabPane;
    JPanel viewTabPane;
    private JToolBar specialToolbar;
    private JToolBar viewToolBar;
    private JPanel buttonPanel;
    private JPanel navigationPanel;
    JTextArea messageTextArea;
    JSplitPane verticalSplitPane;
    JScrollPane messageScrollPane;
    BrowserScrollPane iconsScrollPane;
    BrowserScrollPane tableScrollPane;
    JScrollPane viewerScrollPane;
    private JPanel topPanel;
    private JLabel globalPreferencesMenuItem;
    JLabel busyIcon;
    private BrowserJPanel managerPanel;
    
    // ======================================
    // Main Functional Panels
    // ======================================
    
    private NavigationBar navigationToolBar;
    IconsPanel iconsPanel;
    ResourceTree resourceTree;
    TablePanel tablePanel ;

    // ========================= 
    // Settings/properties 
    // ========================= 

    private int verticalDividerSize;
    private int verticalDividerLocation;
    private int horizontalDividerSize = 100;
    private int horizontalDividerLocation = 100;

    Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);

    // ========================= 
    // Listeners 
    // =========================
    
    BrowserControllerActionListener bcActionListener = null;
    BrowserController browserController;
    
    // ============== State ============ //

    private boolean treeVisible = true;
    private boolean logVisible = true;
    private boolean isBusy;
    
    VBrowser(VBrowserFactory factory)
    {
        super();
        
        logger.debugPrintf("New BrowserFrame!\n");

        // First initialize the Main BrowserController and then its delegate
        // helpers: The Listeners:
        browserController = new BrowserController(this,factory);
        this.addWindowListener(browserController);

        UIPlatform.getPlatform().getWindowRegistry().register(this);

        // Since multiple menuItems and ToolbarItems use one
        // instance of a the same listener, these listeners must be
        // initialized before the GUI components are created:
        bcActionListener = new BrowserControllerActionListener(browserController);

        // listeners are used/set duing initGUI():
        initGUI();

        // set check stuff:
        browserController.checkCredentialStatus();
    }
    
    BrowserController getBrowserController()
    {
        return browserController;
    }

    public int getId()
    {
        return browserId;
    }

    GuiSettings getGuiSettings()
    {
        return this.browserController.getGuiSettings(); 
    }
    
    private void initGUI()
    {
        try
        {
            this.setSize(800, 600);
            {
                // ///////////////////////////////////////////////////////////
                // Main Menu
                // ///////////////////////////////////////////////////////////

                main_menubar = createMenuBar();
                setJMenuBar(main_menubar);

                // ///////////////////////////////////////////////////////////
                // Main VBrowser panels:
                // ///////////////////////////////////////////////////////////

                {
                    BorderLayout thisLayout = new BorderLayout();
                    this.getContentPane().setLayout(thisLayout);
                    {
                        topPanel = new JPanel();
                        this.getContentPane().add(topPanel, BorderLayout.NORTH);
                        BoxLayout jPanel1Layout = new BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS);
                        topPanel.setLayout(jPanel1Layout);
                        {
                            navigationPanel = new JPanel();
                            BoxLayout toolPanel1Layout = new BoxLayout(navigationPanel, javax.swing.BoxLayout.X_AXIS);
                            navigationPanel.setLayout(toolPanel1Layout);
                            topPanel.add(navigationPanel);

                            {
                                navigationToolBar = new NavigationBar();
                                navigationPanel.add(navigationToolBar);

                                navigationToolBar.addNavigationButtonsListener(this.bcActionListener);
                                navigationToolBar.addTextFieldListener(this.bcActionListener);
                                navigationToolBar.setEnableNagivationButtons(true); 
                                
                                // ---
                                // Custom Drop Target, redirect drop to browsercontroller
                                // ---
                                {
                                    DropTarget dt1=new DropTarget(); 
                                    DropTarget dt2=new DropTarget(); 

                                    // enable toolbar and icontext field:  
                                    navigationToolBar.setDropTarget(dt1);
                                    navigationToolBar.getTextField().setDropTarget(dt2); 
                                    
                                    try
                                    {
                                        dt1.addDropTargetListener(new BarDropTargetListener(this.browserController));
                                        dt2.addDropTargetListener(new BarDropTargetListener(this.browserController));
                                    }
                                    catch (TooManyListenersException e)
                                    {
                                        logger.logException(ClassLogger.ERROR,e,"***Error: Exception:%s\n",e); 
                                    }
                                }
                                
                                // *** ADD BUSY ICON ***
                                {

                                    busyIcon = new JLabel();
                                    navigationToolBar.add(busyIcon);
                                    // compoundIcon.add(busyIcon);
                                    // busyIcon.setIcon(IconRenderer.getIcon("default/vle_animate48.gif"));

                                    // busyIcon.setIcon(IconProvider.getDefault().getAnimatedIcon("default/vle_animated_blue32.gif"));
                                    busyIcon.setIcon(loadIcon("default/vle_animated_blue32.gif"));
                                    busyIcon.setDisabledIcon(loadIcon("default/vle_animated_blue_disabled32.gif"));
                                    // busyIcon.setBorder(new
                                    // EtchedBorder(EtchedBorder.LOWERED));
                                    busyIcon.setEnabled(false);
                                    busyIcon.setSize(32, 32); // fixed size!

                                }
                            }
                        }

                        {
                            buttonPanel = new JPanel();
                            topPanel.add(buttonPanel);
                            {
                                specialToolbar = new JToolBar();
                                buttonPanel.add(specialToolbar);
                                {
                                    credentialButton = new JButton();
                                    specialToolbar.add(credentialButton);
                                    // credentialButton.setText("GC");

                                    credentialButton.setActionCommand(ActionCommandType.PROXYDIALOG.toString());
                                    credentialButton.addActionListener(bcActionListener);

                                    credentialButton.setIcon(loadIcon("menu/keys_notok.png"));
                                    credentialButton.setToolTipText("set/check Grid Credentials");

                                }
                            }
                            {
                                viewToolBar = new JToolBar();
                                buttonPanel.add(viewToolBar);
                                {
                                    viewAsIconsBut = new JButton();
                                    viewToolBar.add(viewAsIconsBut);
                                    // viewAsIconsBut.setText("IC");
                                    viewAsIconsBut.setIcon(loadIcon("menu/viewasicons.png"));
                                    viewAsIconsBut.setActionCommand(ActionCommandType.VIEWASICONS.toString());
                                    viewAsIconsBut.addActionListener(bcActionListener);
                                    viewAsIconsBut.setToolTipText(Messages.TT_VIEW_AS_ICONS);
                                }
                                {
                                    viewAsIconRows = new JButton();
                                    viewToolBar.add(viewAsIconRows);
                                    // viewAsIconRows.setText("ICR");
                                    viewAsIconRows.setIcon(loadIcon("menu/viewasiconlist.png"));
                                    viewAsIconRows.setActionCommand(ActionCommandType.VIEWASICONLIST.toString());
                                    viewAsIconRows.addActionListener(bcActionListener);
                                    viewAsIconRows.setEnabled(true);
                                }
                                {
                                    viewAsListBut = new JButton();
                                    viewToolBar.add(viewAsListBut);
                                    // viewAsListBut.setText("AL");
                                    viewAsListBut.setActionCommand(ActionCommandType.VIEWASTABLE.toString());
                                    viewAsListBut.addActionListener(bcActionListener);

                                    viewAsListBut.setIcon(loadIcon("menu/viewastablelist.png")); 
                                    viewAsListBut.setToolTipText(Messages.TT_VIEW_AS_TABLE);
                                }
                                /*
                                 * { viewAsIconListBut = new JButton();
                                 * jToolBar1.add(viewAsIconListBut);
                                 * viewAsIconListBut.setText("ICL"); }
                                 */
                            }
                            {
                                stopButton = new JButton();
                                buttonPanel.add(stopButton);
                                stopButton.setIcon(loadIcon("menu/stop.gif"));
                                stopButton.setEnabled(false);
                                stopButton.setActionCommand(ActionCommandType.MASTERSTOP.toString());
                                stopButton.addActionListener(bcActionListener);
                                stopButton.setCursor(defaultCursor);
                            }
                        }
                    }

                    // ////////////////////////////////////////////////////////////////
                    // Browser Panel
                    // ////////////////////////////////////////////////////////////////

                    {
                        verticalSplitPane = new JSplitPane();
                        this.getContentPane().add(verticalSplitPane, BorderLayout.CENTER);
                        verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        verticalSplitPane.setResizeWeight(1.0);

                        {
                            managerPanel = new BrowserJPanel();
                            managerPanel.setLayout(new BorderLayout());
                            verticalSplitPane.add(managerPanel, JSplitPane.TOP);
                        }
                        {
                            splitPaneManager = new BrowserSplitPane();
                            managerPanel.add(splitPaneManager, BorderLayout.CENTER);
                            splitPaneManager.setPreferredSize(new java.awt.Dimension(744, 113));
                            splitPaneManager.setDividerLocation(200);
                            {
                                resourceTabbedPane = new BrowserTabPane();
                                splitPaneManager.add(resourceTabbedPane, JSplitPane.LEFT);
                                resourceTabbedPane.setPreferredSize(new java.awt.Dimension(200, 384));
                                resourceTabbedPane.setAutoscrolls(true);
                                resourceTabbedPane.setDoubleBuffered(true);
                                resourceTabbedPane.setFocusCycleRoot(true);
                                {
                                    /*
                                     * jigloo note: Jiglo once move this down,
                                     * but then the
                                     * setViewportView(resourceTree) addes a
                                     * null pointer
                                     */
                                    resourceTree = new ResourceTree(this.browserController); 
                                    resourceTree.setPreferredSize(new java.awt.Dimension(62, 1000));
                                }
                                {
                                    treeResourceScrollPane = new BrowserScrollPane();
                                    resourceTabbedPane.addTab("Resource", null, treeResourceScrollPane, null);
                                    treeResourceScrollPane.setPreferredSize(new java.awt.Dimension(129, 329));
                                    treeResourceScrollPane.setViewportView(resourceTree);
                                    // resourceTree.setEditable(true);
                                }
                            }
                            //
                            {
                                {
                                    mangedTabPane = new BrowserTabPane();
                                    splitPaneManager.add(mangedTabPane, JSplitPane.RIGHT);

                                    // default IconsPanel
                                    {
                                        iconsPanel = new IconsPanel(this.browserController);
                                    }

                                    // embed the IconsPanel into a ScrollPane
                                    {
                                        iconsScrollPane = new BrowserScrollPane();
                                        iconsScrollPane.setViewportView(iconsPanel);

                                        // tabbedPane.addTab("icons", null,
                                        // iconsScrollPane, null);
                                        iconsScrollPane.setName(iconsPanel.getName());
                                        this.setMainViewTabComponent(iconsScrollPane);
                                        // set default scroll to default icons
                                        // size of 48:
                                        iconsScrollPane.getVerticalScrollBar().setUnitIncrement(48 / 2);
                                        // a mouse whell scroll is twice this
                                        // size !
                                    }
                                }
                            }
                        }
                        {
                            messageScrollPane = new JScrollPane();
                            verticalSplitPane.add(messageScrollPane, JSplitPane.BOTTOM);
                            {
                                messageTextArea = new JTextArea();
                                messageScrollPane.setViewportView(messageTextArea);
                                messageTextArea.setText("---Message Area---\n");
                                messageTextArea.setEditable(false);
                                messageTextArea.setPreferredSize(new java.awt.Dimension(785, 71));
                            }
                        }
                    }
                }

                // ///////////////////////////////////////////////////////////////////
                // parentless components ! :
                // ///////////////////////////////////////////////////////////////////

                {
                    tablePanel = new TablePanel(browserController);
                }
                {
                    tableScrollPane = new BrowserScrollPane();
                    tableScrollPane.setViewportView(tablePanel);
                    tableScrollPane.setName(tablePanel.getName());
                    // to receive mouse click under/near besides table
                    tableScrollPane.addMouseListener(tablePanel.tableController);
                }

                // Optional JScrollPane to embed viewers in:
                {
                    this.viewerScrollPane = new JScrollPane();
                }
                this.setLogWindowVisible(getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_SHOW_LOG_WINDOW));
                this.setResourceTreeVisible(getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_SHOW_RESOURCE_TREE));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Icon loadIcon(String urlstr)
    {
        return new ImageIcon(getClass().getClassLoader().getResource(urlstr)); 
    }

    private JMenuBar createMenuBar()
    {
        main_menubar = new JMenuBar();

        {
            mainMenu = new JMenu();
            main_menubar.add(mainMenu);
            mainMenu.setText("Location");
            mainMenu.setMnemonic(KeyEvent.VK_L);
            {
                viewNewWindowMenuItem = new JMenuItem();
                mainMenu.add(viewNewWindowMenuItem);
                viewNewWindowMenuItem.setText("New Window");
                viewNewWindowMenuItem.setMnemonic(KeyEvent.VK_W);
                viewNewWindowMenuItem.addActionListener(bcActionListener);
                viewNewWindowMenuItem.setActionCommand(ActionCommandType.NEWWINDOW.toString());
            }
            {
                openMenuItem = new JMenuItem();
                mainMenu.add(openMenuItem);
                openMenuItem.setText("Open");
                openMenuItem.setMnemonic(KeyEvent.VK_O);
                openMenuItem.addActionListener(bcActionListener);
                openMenuItem.setActionCommand(ActionCommandType.OPEN.toString());
            }
            {
                openInWinMenuItem = new JMenuItem();
                mainMenu.add(openInWinMenuItem);
                openInWinMenuItem.setText("Open in new Window");
                openInWinMenuItem.setMnemonic(KeyEvent.VK_N);
                openInWinMenuItem.addActionListener(bcActionListener);
                openInWinMenuItem.setActionCommand(ActionCommandType.OPENINNEWWINDOW.toString());
            }

            JSeparator jSeparator = new JSeparator();
            mainMenu.add(jSeparator);

            {
                proxyMenu = new JMenu();
                mainMenu.add(proxyMenu);
                proxyMenu.setText("Grid Proxy");
                {
                    proxyDialogMenuItem = new JMenuItem();
                    proxyMenu.add(proxyDialogMenuItem);
                    proxyDialogMenuItem.setText("Init Proxy Dialog");
                    proxyDialogMenuItem.setMnemonic(KeyEvent.VK_P);
                    proxyDialogMenuItem.setActionCommand(ActionCommandType.PROXYDIALOG.toString());
                    proxyDialogMenuItem.addActionListener(bcActionListener);
                }
            }
            
            {
                JSeparator sep = new JSeparator();
                mainMenu.add(sep);
            }
            
            {
                JSeparator sep = new JSeparator();
                mainMenu.add(sep);
            }
            {
                closeMenuItem = new JMenuItem();
                mainMenu.add(closeMenuItem);
                closeMenuItem.setText("Close");
                closeMenuItem.addActionListener(bcActionListener);
                closeMenuItem.setActionCommand(ActionCommandType.CLOSEBROWSER.toString());
            }
            {
                exitMenuItem = new JMenuItem();
                mainMenu.add(exitMenuItem);
                exitMenuItem.setText("Exit all Browsers");
                exitMenuItem.addActionListener(bcActionListener);
                exitMenuItem.setActionCommand(ActionCommandType.EXITBROWSERS.toString());
            }
        }
        {
            // *** [Edit] Menu ***

            editMenu = new JMenu();
            main_menubar.add(editMenu);

            editMenu.setText("Edit");
            editMenu.setMnemonic(KeyEvent.VK_E);
            editMenu.setActionCommand(ActionCommandType.EDIT.toString());

            // ActionMenu.addEditItems(editMenu,this.browserController,null,true);

            {
                cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
                cutMenuItem.setText("Cut");
                cutMenuItem.setMnemonic(KeyEvent.VK_T);
                cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

                editMenu.add(cutMenuItem);
                cutMenuItem.addActionListener(bcActionListener);
                cutMenuItem.setActionCommand(ActionCommandType.CLIPBOARD_CUTSELECTION.toString());
                cutMenuItem.setEnabled(false); // default disabled
            }
            {
                copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
                editMenu.add(copyMenuItem);
                copyMenuItem.setText("Copy");
                copyMenuItem.setMnemonic(KeyEvent.VK_C);
                copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

                copyMenuItem.addActionListener(bcActionListener);
                copyMenuItem.setActionCommand(ActionCommandType.CLIPBOARD_COPYSELECTION.toString());
                copyMenuItem.setEnabled(false); // default disabled
            }
            {
                pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
                editMenu.add(pasteMenuItem);
                pasteMenuItem.setText("Paste");
                pasteMenuItem.setMnemonic(KeyEvent.VK_P);
                pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

                pasteMenuItem.addActionListener(bcActionListener);
                pasteMenuItem.setActionCommand(ActionCommandType.CLIPBOARD_PASTE.toString());
                pasteMenuItem.setEnabled(false); // default disabled
            }
            {
                pasteAsLinkMenuItem = new JMenuItem();

                editMenu.add(pasteAsLinkMenuItem);
                pasteAsLinkMenuItem.setText("Paste as VLink");
                pasteAsLinkMenuItem.setMnemonic(KeyEvent.VK_L);

                pasteAsLinkMenuItem.addActionListener(bcActionListener);
                pasteAsLinkMenuItem.setActionCommand(ActionCommandType.CLIPBOARD_LINKDROP.toString());
                pasteAsLinkMenuItem.setEnabled(false); // default disabled
            }
            {
                createLinkToMenuItem = new JMenuItem();

                editMenu.add(createLinkToMenuItem);
                createLinkToMenuItem.setText("Create VLink to");
                // pasteAsLinkMenuItem.setMnemonic(KeyEvent.VK_L);

                createLinkToMenuItem.addActionListener(bcActionListener);
                createLinkToMenuItem.setActionCommand(ActionCommandType.CREATELINKTO.toString());
                createLinkToMenuItem.setEnabled(false); // default disabled
            }
            {
                JSeparator sep = new JSeparator();
                editMenu.add(sep);
            }
            {
                createMenuItem = new JMenu();
                editMenu.add(createMenuItem);
                createMenuItem.setText("New");
                createMenuItem.addActionListener(bcActionListener);
                {
                    // default menu: is Populated when a Resource Is Selected !
                    createNoneMenuItem = new JMenuItem();
                    createMenuItem.add(createNoneMenuItem);
                    createNoneMenuItem.setText("(none)");
                    createNoneMenuItem.setEnabled(false);
                }
            }
            {
                JSeparator sep = new JSeparator();
                editMenu.add(sep);
            }
            {
                deleteMenuItem = new JMenuItem();
                editMenu.add(deleteMenuItem);
                deleteMenuItem.setText("Delete");
                deleteMenuItem.setMnemonic(KeyEvent.VK_D);
                deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK));

                deleteMenuItem.addActionListener(bcActionListener);
                deleteMenuItem.setActionCommand(ActionCommandType.DELETE.toString());
                deleteMenuItem.setEnabled(false);
            }
            {
                forceDeleteMenuItem = new JMenuItem();
                editMenu.add(forceDeleteMenuItem);
                forceDeleteMenuItem.setText("Force Delete");
                forceDeleteMenuItem.setMnemonic(KeyEvent.VK_D);
                forceDeleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, ActionEvent.CTRL_MASK
                        | ActionEvent.SHIFT_MASK));

                forceDeleteMenuItem.addActionListener(bcActionListener);
                forceDeleteMenuItem.setActionCommand(ActionCommandType.FORCE_DELETE.toString());
                // always true, as the force delte tries to delete everything
                // always as much as possible
                forceDeleteMenuItem.setEnabled(true);
                forceDeleteMenuItem.setVisible(false); // hidden
            }
            {
                renameMenuItem = new JMenuItem();
                editMenu.add(renameMenuItem);
                renameMenuItem.setText("Rename");
                renameMenuItem.setMnemonic(KeyEvent.VK_R);
                renameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
                renameMenuItem.addActionListener(bcActionListener);
                renameMenuItem.setActionCommand(ActionCommandType.RENAME.toString());
                renameMenuItem.setEnabled(false);
            }

        }
        // *** [View] *** customized view for THIS VBrowser
        {
            view_menu = new JMenu();
            main_menubar.add(view_menu);
            view_menu.setText("View");
            view_menu.setMnemonic(KeyEvent.VK_V);

            ButtonGroup viewAsButtonGroup = new ButtonGroup();

            {
                viewAsIconsRB = new JRadioButtonMenuItem("View as Icons", true);
                view_menu.add(viewAsIconsRB);
                viewAsButtonGroup.add(viewAsIconsRB);
                viewAsIconsRB.setActionCommand(ActionCommandType.VIEWASICONS.toString());
                viewAsIconsRB.addActionListener(bcActionListener);
                viewAsIconsRB.setEnabled(true);
                viewAsIconsRB.setToolTipText(Messages.TT_VIEW_AS_ICONS);
            }
            {
                viewAsListRB = new JRadioButtonMenuItem("View as List (details)");
                view_menu.add(viewAsListRB);
                viewAsButtonGroup.add(viewAsListRB);
                viewAsListRB.setActionCommand(ActionCommandType.VIEWASTABLE.toString());

                viewAsListRB.addActionListener(bcActionListener);
                viewAsListRB.setEnabled(true);
                viewAsListRB.setToolTipText(Messages.TT_VIEW_AS_TABLE);
            }
            {
                view_menu.add(new JSeparator());
            }
            {
                viewRefreshAll = new JMenuItem();
                view_menu.add(viewRefreshAll);
                viewRefreshAll.setText("Refresh");
                viewRefreshAll.setMnemonic(KeyEvent.VK_R);
                viewRefreshAll.addActionListener(bcActionListener);
                viewRefreshAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

                viewRefreshAll.setActionCommand(ActionCommandType.REFRESH.toString());
            }
            /*
             * { viewRefreshSelectionMenuItem = new JMenuItem();
             * view_menu.add(viewRefreshSelectionMenuItem);
             * viewRefreshSelectionMenuItem .setText("Refresh Selection");
             * viewRefreshSelectionMenuItem
             * .addActionListener(bcActionListener); }
             */
            /*
             * { viewItem = new JMenuItem(); view_menu.add(viewItem);
             * viewItem.setText("View Item");
             * viewItem.addActionListener(bcActionListener); viewItem. }
             */
            {
                view_menu.add(new JSeparator());
            }

            // Preferences --> For ALL VBrowsers !!!
            {
                preferencesMenu = new JMenu();
                view_menu.add(preferencesMenu);
                preferencesMenu.setText("Preferences");
                {
                    globalPreferencesMenuItem = new JLabel();
                    preferencesMenu.add(globalPreferencesMenuItem);
                    globalPreferencesMenuItem.setText("- Global Preferences -");
                    globalPreferencesMenuItem.setForeground(Color.GRAY);

                }

                {
                    singleClickActionMenuItem = new JCheckBoxMenuItem();
                    preferencesMenu.add(singleClickActionMenuItem);
                    singleClickActionMenuItem.setText("Single Click Action");
                    singleClickActionMenuItem.setActionCommand(ActionCommandType.GLOBAL_SET_SINGLE_ACTION_CLICK
                            .toString());
                    singleClickActionMenuItem
                            .setState(getGuiSettings().getBoolProperty(GuiPropertyName.SINGLE_CLICK_ACTION));
                    singleClickActionMenuItem.addActionListener(bcActionListener);
                }
                {
                    preferencesMenu.add(new JSeparator());
                }
                {
                    globalFolderMenuItem = new JLabel();
                    preferencesMenu.add(globalFolderMenuItem);
                    globalFolderMenuItem.setText("- Global Folder Preferences -");
                    globalFolderMenuItem.setForeground(Color.GRAY);

                }
                {
                    globalShowLogWindowMenu = new JCheckBoxMenuItem();
                    preferencesMenu.add(globalShowLogWindowMenu);
                    globalShowLogWindowMenu.setText("Show log window");
                    globalShowLogWindowMenu.setActionCommand(ActionCommandType.GLOBAL_SHOW_LOG_WINDOW.toString());
                    globalShowLogWindowMenu.setState(getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_SHOW_LOG_WINDOW));
                    globalShowLogWindowMenu.addActionListener(bcActionListener);
                }
                {
                    globalShowResourceTreeMenu = new JCheckBoxMenuItem();
                    preferencesMenu.add(globalShowResourceTreeMenu);
                    globalShowResourceTreeMenu.setText("Show resource tree");
                    globalShowResourceTreeMenu.setActionCommand(ActionCommandType.GLOBAL_SHOW_RESOURCE_TREE.toString());
                    globalShowResourceTreeMenu.setState(getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_SHOW_RESOURCE_TREE));
                    globalShowResourceTreeMenu.addActionListener(bcActionListener);
                }
                {
                    preferencesMenu.add(new JSeparator());
                }
                {
                    globalFilterHiddenFilesMenu = new JCheckBoxMenuItem();
                    preferencesMenu.add(globalFilterHiddenFilesMenu);
                    globalFilterHiddenFilesMenu.setText("Filter hidden files");
                    globalFilterHiddenFilesMenu.setActionCommand(ActionCommandType.GLOBAL_FILTER_HIDDEN_FILES
                            .toString());
                    globalFilterHiddenFilesMenu.setState(getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_FILTER_HIDDEN_FILES));
                    globalFilterHiddenFilesMenu.addActionListener(bcActionListener);
                }
                {
                    preferencesMenu.add(new JSeparator());
                }
                {
                    lafMenu = new JMenu();
                    lafMenu.setText("Look and Feel");
                    preferencesMenu.add(lafMenu);

                    /***
                     * { defaultLAFmenuItem = new JMenuItem();
                     * lafMenu.add(defaultLAFmenuItem);
                     * defaultLAFmenuItem.setText("Default");
                     * 
                     * ActionCommand cmd=new
                     * ActionCommand(ActionCommandType.LOOKANDFEEL);
                     * cmd.addArgument
                     * (GuiSettings.LookAndFeels.DEFAULT.toString());
                     * 
                     * defaultLAFmenuItem .setActionCommand(cmd.toString());
                     * defaultLAFmenuItem .addActionListener(bcActionListener);
                     * }
                     ***/
                    // Windows LAF doesn't work on Linux :-D
                    {
                        metalLAFmenuItem = new JMenuItem();
                        lafMenu.add(metalLAFmenuItem);
                        metalLAFmenuItem.setText("Metal (Default)");

                        ActionCommand cmd = ActionCommand.createAction(ActionCommandType.LOOKANDFEEL,
                                GuiSettings.LookAndFeelType.METAL.toString());

                        metalLAFmenuItem.setActionCommand(cmd.toString());
                        metalLAFmenuItem.addActionListener(bcActionListener);
                    }
                    {
                        nativeLAFmenuItem = new JMenuItem();
                        lafMenu.add(nativeLAFmenuItem);
                        nativeLAFmenuItem.setText("Native");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.LOOKANDFEEL);
                        cmd.setArgument(GuiSettings.LookAndFeelType.NATIVE.toString());

                        nativeLAFmenuItem.setActionCommand(cmd.toString());
                        nativeLAFmenuItem.addActionListener(bcActionListener);
                    }
                    
                    if (Global.isWindows()==true)
                    {
                        nativeLAFmenuItem = new JMenuItem();
                        lafMenu.add(nativeLAFmenuItem);
                        nativeLAFmenuItem.setText("Windows");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.LOOKANDFEEL);
                        cmd.setArgument(GuiSettings.LookAndFeelType.WINDOWS.toString());

                        nativeLAFmenuItem.setActionCommand(cmd.toString());
                        nativeLAFmenuItem.addActionListener(bcActionListener);
                    }

                    {
                        gtkLAFmenuItem = new JMenuItem();
                        lafMenu.add(gtkLAFmenuItem);
                        gtkLAFmenuItem.setText("GTK");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.LOOKANDFEEL);
                        cmd.setArgument(GuiSettings.LookAndFeelType.GTK.toString());

                        gtkLAFmenuItem.setActionCommand(cmd.toString());
                        gtkLAFmenuItem.addActionListener(bcActionListener);
                    }
                    {
                        JMenuItem lafMenuItem = new JMenuItem();
                        lafMenu.add(lafMenuItem);
                        lafMenuItem.setText("Plastic 3D");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.LOOKANDFEEL);
                        cmd.setArgument(GuiSettings.LookAndFeelType.PLASTIC_3D.toString());

                        lafMenuItem.setActionCommand(cmd.toString());
                        lafMenuItem.addActionListener(bcActionListener);
                    }
                    {
                        JMenuItem lafMenuItem = new JMenuItem();
                        lafMenu.add(lafMenuItem);
                        lafMenuItem.setText("Plastic XP");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.LOOKANDFEEL);
                        cmd.setArgument(GuiSettings.LookAndFeelType.PLASTIC_XP.toString());

                        lafMenuItem.setActionCommand(cmd.toString());
                        lafMenuItem.addActionListener(bcActionListener);
                    }
                    lafMenu.add(new JSeparator());
                    {
                        JMenuItem lafMenuItem = new JMenuItem();
                        lafMenu.add(lafMenuItem);
                        lafMenuItem.setText("Save Look and Feel");

                        ActionCommand cmd = new ActionCommand(ActionCommandType.SAVE_LOOKANDFEEL);

                        lafMenuItem.setActionCommand(cmd.toString());
                        lafMenuItem.addActionListener(bcActionListener);
                    }

                }
            }
            // Look and Feel ->
        }
        {
            toolsMenu = new JMenu();
            main_menubar.add(toolsMenu);
            toolsMenu.setText("Tools");
            {
                cogUtilsMenu = new JMenu();
                toolsMenu.add(cogUtilsMenu);
                cogUtilsMenu.setText("Grid Utils");
                {
                    gridstartMenuItem = new JMenuItem();
                    cogUtilsMenu.add(gridstartMenuItem);

                    gridstartMenuItem.setText("Start JGridStart");
                    gridstartMenuItem.setActionCommand(ActionCommandType.START_JGRIDSTART.toString());

                    gridstartMenuItem.addActionListener(bcActionListener);
                }
                {
                    cogProxyInitMenuItem = new JMenuItem();
                    cogUtilsMenu.add(cogProxyInitMenuItem);

                    cogProxyInitMenuItem.setText("COG Configure Certificates");
                    cogProxyInitMenuItem.setActionCommand(ActionCommandType.COGINITPROXY.toString());

                    cogProxyInitMenuItem.addActionListener(bcActionListener);
                }
            }
            // Tools->VLTerm
            {
                JMenuItem vltermMenu = new JMenuItem();
                toolsMenu.add(vltermMenu);
                vltermMenu.setText("VLTerm");
                vltermMenu.setActionCommand(ActionCommandType.STARTVLTERM.toString());
                vltermMenu.addActionListener(bcActionListener);
            }

            toolsMenu.add(new JSeparator());
            {
                customTools = new JMenu();
                toolsMenu.add(customTools);
                customTools.setText("custom");
                this.addToolsTo(customTools);
            }
        }
        // Windows Menu
        {
            windowsMenu = new JMenu();
            main_menubar.add(windowsMenu);
            windowsMenu.setText("Windows");
            {
                JMenuItem showAll = new JMenuItem();
                windowsMenu.add(showAll);
                showAll.setText("Show All Windows");
                showAll.setActionCommand(ActionCommandType.SHOW_ALL_WINDOWS.toString());
                showAll.addActionListener(bcActionListener);
            }
        }

        {
            helpMenu = new JMenu();
            main_menubar.add(helpMenu);
            helpMenu.setText("Help");
            {
                aboutMenuItem = new JMenuItem();
                helpMenu.add(aboutMenuItem);
                aboutMenuItem.setText("About");
                aboutMenuItem.setActionCommand("About");

                aboutMenuItem.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        AboutDialog.showMe(VBrowser.this.browserController);
                    }
                });

            }
            // help menu
            {
                JMenuItem item = new JMenuItem();
                helpMenu.add(item);
                item.setText("Help");
                item.setActionCommand("" + ActionCommandType.HELP);
                item.addActionListener(bcActionListener);
            }
            {
                JSeparator sep = new JSeparator();
                helpMenu.add(sep);
            }
            // debug menu 
            {
                debugMenu = new JMenu();
                helpMenu.add(debugMenu);
                debugMenu.setText("Debug");
                {
                    showTasksMenuItem = new JMenuItem();
                    debugMenu.add(showTasksMenuItem);
                    showTasksMenuItem.setText("Show Tasks");
                    showTasksMenuItem.addActionListener(bcActionListener);
                    showTasksMenuItem.setActionCommand(ActionCommandType.DEBUG_SHOW_TASKS.toString());

                }
                {
                    JMenuItem verboseSubMenu = new JMenu();
                    debugMenu.add(verboseSubMenu);
                    verboseSubMenu.setText("Debug Log Level");
                    {
                        JMenuItem item = new JMenuItem("ERROR");
                        verboseSubMenu.add(item);
                        item.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                Global.getLogger().setLevelToError();
                            }
                        });
                    }
                    {
                        JMenuItem item = new JMenuItem("WARN");
                        verboseSubMenu.add(item);
                        item.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                Global.getLogger().setLevelToWarn();
                            }
                        });
                    }
                    {
                        JMenuItem item = new JMenuItem("INFO");
                        verboseSubMenu.add(item);
                        item.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                Global.getLogger().setLevelToInfo();
                            }
                        });
                    }
                    {
                        JMenuItem item = new JMenuItem("DEBUG");
                        verboseSubMenu.add(item);
                        item.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                Global.getLogger().setLevelToDebug();
                            }
                        });
                    }
                }

                {
                    JMenuItem clearRegistry = new JMenuItem();
                    debugMenu.add(clearRegistry);
                    clearRegistry.setText("Reset VRS");
                    clearRegistry.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            getBrowserController().resetVRS();
                           
                        }
                    });
                }
 
                {
                    JMenuItem clearRegistry = new JMenuItem();
                    debugMenu.add(clearRegistry);
                    clearRegistry.setText("Reset Icon Cache");
                    clearRegistry.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            UIGlobal.getIconProvider().clearCache();
                        }
                    });
                }

                {
                    JMenuItem item = new JMenuItem();
                    debugMenu.add(item);
                    item.setText("Busy Mouse ON");

                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            BrowserController.dummyMode = true;
                        }
                    });
                }
                {
                    JMenuItem item = new JMenuItem();
                    debugMenu.add(item);
                    item.setText("Busy Mouse OFF");

                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent evt)
                        {
                            BrowserController.dummyMode = false;
                        }
                    });
                }
            }
        }

        return main_menubar;
    }

    protected void addToolsTo(JMenu toolsMenu)
    {
        ViewerList vlist = ViewerRegistry.getRegistry().getAllTools();

        // noppes nada:
        if (vlist == null)
            return;

        for (ViewerInfo vinfo : vlist)
        {
            // ViewerRegistry.ViewerInfo vinfo=vlist.elementAt(i);
            JMenuItem mitem = new JMenuItem();
            mitem.setText(vinfo.getName());
            mitem.setActionCommand(ActionCommand.createStartToolCommand(vinfo.getClassName()).toString());
            mitem.addActionListener(this.bcActionListener);
            toolsMenu.add(mitem);
        }
    }

    /**
     * Specify component to view in main ViewTab.
     * 
     * @param comp
     * @param inScrollPane
     */
    public void setMainViewTabComponent(Component comp)
    {
        // preset components size to viewport size !
        // comp.setSize(viewTabPane.getViewport().getSize());
        // current one tab is allowed

        // for multitabbing: remove:
        mangedTabPane.removeAll();

        String tt;
        if (comp instanceof JComponent)
            tt = ((JComponent) comp).getToolTipText();
        else
            tt = comp.getName();

        mangedTabPane.addTab(comp.getName(), null, comp, tt);
        mangedTabPane.setSelectedComponent(comp);

    }

    /**
     * Returns component which is currently in the main tab
     * 
     * @return
     */
    public Component getMainViewTabComponent()
    {
        Component comp = mangedTabPane.getSelectedComponent();

        if (comp instanceof JScrollPane)
            comp = ((JScrollPane) comp).getViewport().getView();
        
        return comp;
    }

    /** whether main component in active tab is the iconspanel */
    boolean isIconsPanelVisible()
    {
        return getMainViewTabComponent().equals(this.iconsPanel);
    }

    /**
     * Help Garbage collector by nullifying object references. Really destroys
     * window.
     */
    public void dispose()
    {
        // dispose main panels: 
        this.resourceTree.dispose();
        this.resourceTree = null;
        this.tablePanel.dispose();
        this.tablePanel = null;
        this.iconsPanel.dispose();
        this.iconsPanel = null;

        super.dispose();
    }

    public void setBusy(boolean val)
    {
        this.isBusy = val;
    }

    public boolean isBusy()
    {
        return this.isBusy;
    }

    public void updateMouseBusy()
    {
        if (isBusy())
            this.setCursor(busyCursor);
        else
            this.setCursor(defaultCursor);
    }

    void setResourceTreeVisible(boolean visible)
    {
        if (visible == treeVisible)
            return; // don't do anything !

        if (visible == false)
        {
            // keep divider settings
            horizontalDividerSize = splitPaneManager.getDividerSize();
            horizontalDividerLocation = splitPaneManager.getDividerLocation();
            splitPaneManager.setDividerSize(0);
            resourceTabbedPane.setVisible(false);

            /***
             * // switch horizontalSplitPane.remove(tabbedPane);
             * 
             * verticalSplitPane.remove(horizontalSplitPane);
             * verticalSplitPane.add(tabbedPane, JSplitPane.TOP);
             * 
             * 
             * // horizontalSplitPane.removeAll();
             ***/

            treeVisible = false;

        }
        else
        {
            splitPaneManager.setDividerSize(horizontalDividerSize);
            splitPaneManager.setDividerLocation(horizontalDividerLocation);
            resourceTabbedPane.setVisible(true);

            /***
             * verticalSplitPane.remove(tabbedPane);
             * horizontalSplitPane.add(tabbedPane, JSplitPane.RIGHT);
             * //horizontalSplitPane
             * .add(this.resourceTabbedPane,JSplitPane.LEFT);
             * verticalSplitPane.add(horizontalSplitPane, JSplitPane.TOP);
             * horizontalSplitPane.setDividerLocation(200);
             * 
             * 
             * // apperantly this needs to be readded (TBI!)
             * //tabbedPane.validate(); verticalSplitPane.validate();
             ***/

            treeVisible = true;
        }
    }

    void setLogWindowVisible(boolean visible)
    {
        if (visible == logVisible)
            return; // don't do anything !

        if (visible == false)
        {
            // keep divider settings
            verticalDividerSize = verticalSplitPane.getDividerSize();
            verticalDividerLocation = verticalSplitPane.getDividerLocation();
            verticalSplitPane.setDividerSize(0);
            messageScrollPane.setVisible(false);

            logVisible = false;

        }
        else
        {
            verticalSplitPane.setDividerSize(verticalDividerSize);
            verticalSplitPane.setDividerLocation(verticalDividerLocation);
            messageScrollPane.setVisible(true);

            logVisible = true;

        }
    }

    public void setLocationText(String txt)
    {
        this.navigationToolBar.setLocationText(txt);
    }

    public String getLocationText()
    {
        return this.navigationToolBar.getLocationText();
    }

    public void setLocationIcon(Icon icon)
    {
        this.navigationToolBar.setIcon(icon);
    }

    public NavigationBar getNavigationBar()
    {
        return this.navigationToolBar;
    }
}
