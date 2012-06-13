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
 * $Id: BrowserController.java,v 1.20 2011-06-10 10:18:00 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:00 $
 */ 
// source: 

package nl.uva.vlet.gui.vbrowser;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.gui.GuiPropertyName;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.actions.ActionCommand;
import nl.uva.vlet.gui.actions.ActionCommandType;
import nl.uva.vlet.gui.actions.ActionMenu;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.dialog.SimpleDialog;
import nl.uva.vlet.gui.dnd.DropAction;
import nl.uva.vlet.gui.icons.IconViewType;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.util.proxy.GridProxyDialog;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewFilter;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.viewers.DefaultNodeViewer;
import nl.uva.vlet.gui.viewers.IMimeViewer;
import nl.uva.vlet.gui.viewers.ViewContext;
import nl.uva.vlet.gui.viewers.ViewerEvent;
import nl.uva.vlet.gui.viewers.ViewerManager;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.gui.viewers.ViewerRegistry;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.util.cog.GridProxyListener;
import nl.uva.vlet.util.vlterm.VLTerm;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.EventType;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VRSFactory;

/**
 * The BrowserControlller is the Controller class in the MVC paradigm which
 * controls the VBrowser class. 
 * <p>
 * All the main functionality of the VBrowser is in this class.
 * <p>
 * Note that this BrowserController delegates the 'Listener' functionality to
 * separate Listeners. These callback to the BrowserController when an event has
 * happened.
 * <p>
 * The (current) order of events is:
 * <li> BrowserController &rarr; VBrowser &rarr; {Listeners} &rarr; Browsercontroller <br>
 * <p>
 * Implementation note: This class still contains 2 ProxyTNode references. 
 *           
 * @author P.T. de Boer
 */
public class BrowserController implements WindowListener, GridProxyListener,
   MasterBrowser, ProxyResourceEventListener
{
	// =======================================================================
	// Class Fields
	// =======================================================================

	/** Class Vector to keep record of all the browsers+controllers opened */
	private static Vector<BrowserController> controllers = new Vector<BrowserController>();

	/** TermGlobal Selection for interbrowser copying */
	private static ResourceRef staticCopySelection[] = null;

	/** Whether current copy selection is selected as 'cut' */ 
	private static boolean staticCopySeletectionIsCut=false; 

	//private static VBrowserResourceEventListener resourceEventListener=new VBrowserResourceEventListener(); 

	//private static boolean asynchronousActions = false;

	/**
	 * Master frame for standalone dialogs which can not or 
	 * may not be attached to this VBrowsers frame. 
	 * When closing a VBrowser window, all resources, including
	 * outstanding dialogs will be closed
	 *  
	 * For Transfer dialogs this is fatal
	 */

	private static JFrame masterFrame=null;

    private static ClassLogger logger; 

	static
	{
	    logger=ClassLogger.getLogger(BrowserController.class); 
	    
		try
		{
			masterFrame=new JFrame();
		}
		catch (Throwable t)
		{
			// gracefull shutdown to prevent bootstrap errors
			logger.logException(ClassLogger.FATAL,t," *** Couldn't create Master Frame: No graphical environment?\n");
			System.exit(-1); 
		}
	}

	static int karma = 0;

	static boolean dummyMode = true;

    public static void setDummyMode(boolean val)
    {
        dummyMode=val; 
    }
    
	// =======================================================================
	// Inner Classes
	// =======================================================================
    
    // 
	private static class HistoryElement
	{
		VRL viewedLocation=null; 

		ViewType viewType = null;

		HistoryElement(VRL loc,ViewType viewType)
		{
			this.viewedLocation=loc;  
			this.viewType=viewType; 

		}
	}

	public static enum ViewType 
    	{
    		ICONS, ICONLIST, TABLE, SINGLEITEM
    	};

	// =======================================================================
	// Instance Fields
	// =======================================================================

	/** The VBrowser I am Controlling */
	protected VBrowser vbrowser = null;

	private int browserId = -1;

	// private ResourceTree vbrowser.getResourceTree()=null;

	/** Current node we are viewing in the VBrowser */
	private ProxyNode viewedNode = null;

	/** Current selected node (!=viewedNode) */
	// private VComponent selectionNode = null;

	/** Node in local copy buffer */
	// No local copy // private ProxyNode copySelectionNode = null;
	// No local cut // private boolean sourceIsCut = false;
	/** Simple browser history */
	private Vector<HistoryElement> history = new Vector<HistoryElement>();

	/** always points to 'current' resource */
	private int historyIndex = -1;

	private boolean treeVisible = true;

	private ViewerManager viewerManager = null; // new ViewerManager(null,new ViewContext(this),null);

	private ViewType viewType = ViewType.ICONS;

	private ViewType previousCompositeViewType = ViewType.ICONS;

	private ActionTask currentTask = null;

	//private ExternalViewerWatcher externalViewerListener;

	final private ViewModel viewModel=new ViewModel(); 

	private BrowserInteractiveActions interactiveActionHandler;

	private VContainer lastActiveVContainer;

    private VBrowserFactory factory;

	// =======================================================================
	// Constructor
	// =======================================================================

	/**
	 * Contructor. Needs VBrowser object to control.
	 * 
	 * Default visibility for contructor and other methods are: package
	 * 
	 * @param sbrowser
	 * @param factory 
	 */

	public BrowserController(VBrowser sbrowser, VBrowserFactory factory)
	{
		this.vbrowser = sbrowser;
		this.factory=factory; 
		
		if (sbrowser != null)
			this.browserId = sbrowser.getId();
		else
			this.browserId = -1; // browserless controller: only for testing/debugging !

		synchronized (controllers)
		{
			controllers.add(this);
		}

		UIGlobal.getGridProxy().addGridProxyListener(this);

		//this.externalViewerListener = new ExternalViewerWatcher(this);

		// ResourceEvent are not being forwarded by the ProxyNode Resource
		// event listener: 
		//ResourceEvent.addResourceEventListener(this); 
		this.interactiveActionHandler=new BrowserInteractiveActions(this); 
		 
		initViewFilter(); 
		
		ProxyVRSClient.getInstance().addResourceEventListener(this);
		
	}

	// ===========================================================================
	// Thread Critical Methods
	// ===========================================================================

	/** 
	 * When the text in the location bar has changed, this 
	 * method is called to update the VBrowser. 
	 */  
	public void performLocationBarChanged()
	{
	    String locstr = vbrowser.getLocationText(); 
		performOpenLocation(locstr); 
		
	}

	/** Open the specified location and update the VBrowser panels */ 
	public void performOpenLocation(String locStr)
	{
		try
		{
			VRL vrl=new VRL(locStr); 
			if (vrl.isAbsolute()==false)
			{
				handle(new VlException("Invalid Location","Not a valid VRL:"+vrl));
				return;
				//does not work: 
				// vrl=new VRL("http://www.google.com/search?q="+vrl.toURIString());  
			}
			performOpenLocation(vrl);

		}
		catch (VlException e)
		{
			handle(e);
		}  
	}

	/** Open the specified location and update the VBrowser panels */ 
	public void performOpenLocation(VRL vrl)
	{
		this.setLocationTextField(vrl);

		asyncOpenLocation(vrl); 
	}
	
	
	/** Update text field, add to History, but do nothing */  
    public void followViewerLocation(VRL vrl)
    {
        this.setLocationTextField(vrl);

        //this.addHistory(vrl); // add to typed locations
    }

	/** Get current viewed location */ 
	public VRL getViewedLocation()
	{
		if (viewedNode==null) 
			return null;

		return viewedNode.getVRL();
	}
	
	public ProxyNode getViewPNode()
	{
	    return viewedNode;
	}

	/**
	 * Perform selection on VComponent. 
	 * if selection==null => Clear Selection 
	 */ 
	public void performSelection(VComponent selection)
	{
		// redirect action to browserController:
		ActionCommand cmd=new ActionCommand(ActionCommandType.SELECTIONCLICK); 
		handleAction(selection,cmd,false);
	}
	
	// inhereted from MasterBrowser !
	public void performAction(VComponent vcomp)
	{
		ActionCommand cmd=new ActionCommand(ActionCommandType.ACTIONCLICK);
		handleAction(vcomp,cmd,false); 
	}

	/**
	 * Perform GLOBAL action. There might not be an action Node ! 
	 */
	public void performGlobalMenuAction(ActionCommand cmd)
	{
		handleAction(null, cmd,true);
	}
	
	public void performAction(VComponent vcomp,ActionCommand cmd)
	{
		handleAction(vcomp,cmd,false); 
	}
	
	/**
	 * Main Browser Action handler.
	 * <p>
	 * All actions are forwarded to this action handler. 
	 * The public performAction(...) methods can be called by Component Listeneners 
	 * and controllers to forward GUI event to this method. 
	 *  
	 * <p>
	 * @param vcomp         : Gui Component to use. If null globalAction MUST be true ! 
	 * @param actionCommand : action to perform
	 * @parem globalAction  : is true: perform a global action from the VBrowser menu.               
	 */

	private void handleAction(VComponent vcomp,final ActionCommand actionCommand,boolean globalAction)
	{
		// pre: 
	    logger.debugPrintf("performAction actionCommand =%s\n",actionCommand);
		//Debug("performAction actionNode    ="+actionNode);
	    logger.debugPrintf("performAction vcomp         =%s\n",vcomp);
	    logger.debugPrintf("performAction globalAction  =%s\n",globalAction);
		
		// global actions are perform on the active selection. 
		// other actions need a vcomponent. 
		
		///////////////////////
		// Start 
		///////////////////////
		
		setBusy(true);
		
		ProxyNode performNode; 

		if (vcomp!=null) 
		{
			// I) explicit action on VComponent 'vcomp'
			performNode=ProxyNode.getProxyNodeFactory().getFromCache(vcomp.getResourceRef().getVRL());
			
			if (performNode==null)
			{
			    handle(new VlInternalError("Node not in cache:"+vcomp.getResourceRef())); 
			}
			   
		}
		else
		{
			performNode=null;
		}

		logger.debugPrintf("performNode =%s\n",performNode);

		// Perform non background method:  
		try
		{
			boolean actionDone=true;  

			//
			//  Global Menu Actions 
			//

			switch (actionCommand.actionType)
			{
				case BROWSEUP:
					performBrowseUp();
					break;
				case BROWSEFORWARD:
					performBrowseForward();
					break;
				case BROWSEBACK:
					performBrowseBack();
					break;
				case CLOSEBROWSER:
					performClose();
					break;
				case COGINITPROXY:
				    //performCogUtilConfigureCerts();
				    break;
	            case START_JGRIDSTART:
	                this.performOpenLocation(UIGlobal.getJGridStartLocation());
	               //performCogUtilConfigureCerts();
	               break;
    			case DYNAMIC_ACTION:
				{
					ActionContext selContext = this.createSelectionContext(vcomp); 
					// perform action in this node with optional copy selection:
					performDynamicAction(actionCommand,selContext,vcomp); 
					break;
				}
				case DYNAMIC_VIEWER_ACTION:
				{
					ActionContext selContext = this.createSelectionContext(vcomp); 
					// perform action in this node with optional copy selection:
					performDynamicViewerAction(actionCommand,selContext); 
					break;
				}
				case STARTTOOL:
	            {
	                // can be empty ! 
	                ActionContext selContext = this.createSelectionContext(vcomp); 
	                // perform action in this node with optional selection context. 
	                performStartStandaloneViewer(actionCommand,selContext); 
	                break;
	            }
				case EXITBROWSERS:
					performExitAll();
					break;
				case GLOBAL_SHOW_RESOURCE_TREE:
				{
					// update global settings from preferences menu
					// will only update this VBrowser. 
					boolean stat = vbrowser.globalShowResourceTreeMenu.getState();
					getGuiSettings().setProperty(GuiPropertyName.GLOBAL_SHOW_RESOURCE_TREE, stat);
					vbrowser.setResourceTreeVisible(stat);
					break;
				}
				case GLOBAL_SHOW_LOG_WINDOW:
				{
					boolean stat = vbrowser.globalShowLogWindowMenu.getState();
					getGuiSettings().setProperty(GuiPropertyName.GLOBAL_SHOW_LOG_WINDOW, stat);
					vbrowser.setLogWindowVisible(stat);
					break;
				}
				case GLOBAL_FILTER_HIDDEN_FILES:
				{
					boolean stat = vbrowser.globalFilterHiddenFilesMenu.getState();
					getGuiSettings().setProperty(GuiPropertyName.GLOBAL_FILTER_HIDDEN_FILES, stat);
					
					// update filter view
					this.initViewFilter(); 
					
					this.performRefreshAll();

					break;
				}
				case GLOBAL_SET_SINGLE_ACTION_CLICK:
				{
					boolean stat = vbrowser.singleClickActionMenuItem.getState();
					getGuiSettings().setProperty(GuiPropertyName.SINGLE_CLICK_ACTION, stat);
					break;
				}
				case HELP:
					VRL vrl=Global.getInstallationDocDir().plus("help/index.html");
					performOpenLocation(vrl);
					break;
                case LOCATIONBAR_EDITED: 
                    // 
                    break; 
                case LOCATIONBAR_CHANGED:
					performLocationBarChanged();
					break;
				case LOOKANDFEEL:
					performSwitchLAF(actionCommand.getArgument());
					break;
				case SAVE_LOOKANDFEEL:
					//performSaveLAF(); 
					break;
				case MASTERSTOP:
					performMasterStop();
					break;
				case SAVEPREFERENCES:
					// all properties are currently auto-saved. 
					GuiSettings.saveProperties();
					break;
				case SELECTIONCLICK: 
					// vcomp can be null! 
					setSelection(vcomp); 
					break;
				case SHOW_ALL_WINDOWS:
				{
					// send windows/dialogs to front 
				    UIPlatform.getPlatform().getWindowRegistry().showAll();
					break; 
				}
				case PROXYDIALOG:
					performProxyDialog();
					break;
				case DEBUG_SHOW_TASKS:
					showTasks();
					break;
				case STARTVLTERM: 
					performStartVLTerm();
					break;
				case VIEWASTABLE:
					setView(ViewType.TABLE);
					performViewNode(this.viewedNode, false); // refresh
					// populateViewWith(currentViewedNode);
					break;
				case VIEWASICONS:
					setView(ViewType.ICONS);
					performViewNode(this.viewedNode, false); // refresh
					// populateViewWith(currentViewedNode);
					break;
				case VIEWASICONLIST:
					setView(ViewType.ICONLIST);
					performViewNode(this.viewedNode, false); // refresh
					// populateViewWith(currentViewedNode);
					break;
				case CLIPBOARD_COPYSELECTION:
				case CLIPBOARD_CUTSELECTION:
				{
					staticCopySeletectionIsCut=(actionCommand.actionType==ActionCommandType.CLIPBOARD_CUTSELECTION); 
					
					// when globalAction==true then vcomp==null, but check anyway: 
					if ((globalAction==false) && (vcomp!=null))
					{
						if (vcomp instanceof VContainer) 
						{
							staticCopySelection=((VContainer)vcomp).getSelection(); 
						}
						else
						{
							staticCopySelection=new ResourceRef[1];
							staticCopySelection[0]= performNode.getResourceRef();
						}
					}
					else
					{
						// Global Copy (or Cut) from Menu :
						staticCopySelection=this.getActiveSelection(); 
					}
					
					break;
				}
				case NEWWINDOW:
					// new default window:
					performNewWindow((VRL)null); 
					break;
				case REFRESHALL:
					// master refresh:
					this.performRefreshAll(); 
					break;
				default: 
					actionDone=false;
				    break;
			}
			
			//
			// Check for GlobalAction from VBrowser menu 
			//
			
			if (actionDone==false)
			{
				if (globalAction)
				{
					ResourceRef[] refs = this.getActiveSelection(); 
					
					if (refs==null) 
					{
						UIGlobal.displayErrorMessage("No Active Selection for Menu Action:"+actionCommand.actionType);
					}
					else if (refs.length>1)
					{
						UIGlobal.displayErrorMessage("Multiple Selection currently not implemented for menu action:"+actionCommand.actionType);
					}
					else
						performNode=ProxyNode.getProxyNodeFactory().getFromCache(refs[0].getVRL()); 
				}
				else if (vcomp==null)
				{
					Global.infoPrintf(this,"No resource selected for action:%s\n",actionCommand.actionType);
				}
				
				logger.debugPrintf("handleAction(): performNode%s\n",performNode); 
				
				if (performNode==null)
				{
					// No perform node: (to be investigated) 
					logger.infoPrintf("No resource selected to perform action:%s\n",actionCommand.actionType);
					setBusy(false); 
					return;  
				}
				
				actionDone=true;
				
				switch (actionCommand.actionType)
				{
					case ACTIONCLICK:
					{
						performDefaultAction(performNode); 
						break;
					}
					case CLIPBOARD_COPYDROP:
					{
						// direct copydrop 
						ResourceRef sources[] = getClipBoardSelection();
						performCopyMoveDrop(performNode,sources,false); 
						break;
					}
					case CREATE:
					{
						// interactive create: 
						performCreate(performNode,actionCommand.getArgument());
						break;
					}
					case DELETE:
					{
						this.interactiveActionHandler.interactiveDelete(performNode,new BooleanHolder(false));
						//performNode.asyncDelete(bc, false);
						break;
					}
					case DELETE_ALL:
					{
						ResourceRef[] deleteSelection = ((VContainer)vcomp).getSelection();
						interactiveActionHandler.interactiveDeleteAll(performNode,deleteSelection); 
						break;
					}
					case DND_COPYDROPNODES:
					{
						ResourceRef refs[] = actionCommand.nodeLocations;
						performCopyMoveDrop(performNode,refs,false);
						break;
					}
					case DND_MOVEDROPNODES:
					{
						ResourceRef refs[] = actionCommand.nodeLocations;
						performCopyMoveDrop(performNode,refs, true);
						break;
					}
					case EDITACL:
					{
						this.performEditACL(performNode);  
						break;
					}
					case EDITLINK:
					case EDITPROPERTIES:
					{
						this.interactiveActionHandler.interactiveEditProperties(performNode,true); 
						break;
					}
					case FORCE_DELETE:
					{  
						// delete NOW  !
						this.interactiveActionHandler.interactiveDelete(performNode,new BooleanHolder(true));
						//actionNode.asyncDelete(bc, false);
						break;
					}
					case CLIPBOARD_MOVEDROP:
					{
						// note that ProxyNode.delete also updates the
						// ResourceTrees
						ResourceRef refs[] = getClipBoardSelection();
						performCopyMoveDrop(performNode,refs,true);
						break;
					}
					case OPEN:
						performViewNode(performNode, true);
						break;
					case OPENINNEWWINDOW:
						if (performNode != null)
							performNewWindow(performNode.getVRL());
						else
							// open current location in a new window:
							performNewWindow(getViewedLocation());
						break;

					case CLIPBOARD_PASTE: 
					{
						ResourceRef refs[] = getClipBoardSelection();
						performCopyMoveDrop(performNode,refs,getCopySelectionIsCut());

						// clear copy selection after move-paste  
						if (getCopySelectionIsCut()==true)
							setCopySelection(null); 

						break;
					}
					case REFRESH:
						interactiveActionHandler.performRefresh(performNode);
						break;
					case RENAME:
						this.interactiveActionHandler.interactiveRename(performNode, null);
						break;
					case SETASRESOURCEROOT:
						this.vbrowser.resourceTree.setRootNode(performNode,performNode.getVRL()); 
						break;
				
					case STARTTOOL:
					case STARTVIEWER:
					{
						// Start viewer for performNode:  
						String className = actionCommand.getViewerClassName(); 
						ViewContext context=new ViewContext(this); 
						context.setPreferredViewerClass(className);
						// Menu-> Open With -> start in new Window: 
						context.setStartInStandaloneWindow(true); 
						context.setStartedAsDefaultAction(false); 
						performViewNode(performNode,context,false);  
						//this.performStartViewer(actionNode, className); 
						break;
					}
					case VIEWITEM:
						performViewNode(performNode, true);
						break;
					default:
					    logger.warnPrintf("*** Warning: Unrecognized ActionCommand:%s\n"+actionCommand);
						actionDone=false; 
					break;
				} // switch 
			} // if guiAction
		}
		catch (VlException e)
		{
			handle(e);
		}
		finally // Note: ALL Exception are here 
		{
			// no background task block ... 

		
		}

	    //if (dummyMode == false)
        setBusy(false);
        
		final BrowserController finalBC=this;

		//
		// Backgrounded tasks which have to be converted to gui/synchronized tasks: 
		//
		
		if ((this.currentTask != null) && (this.currentTask.isAlive())
				&& (dummyMode == true))
		{
		    // block double action clicks ! 
			return; // => keep busy ?
		}
		
		final ProxyNode finalNode=performNode;
		
		ActionTask task = new ActionTask(this, ""+actionCommand.getActionMessage()+":"
				+ ((performNode!=null)?performNode.getVRL():"<No Node>"))
		{

			public void doTask()
			{
			    logger.debugPrintf("+++ Starting Action for:%s on %s\n",actionCommand,finalNode);
				try
				{
					switch (actionCommand.actionType)
					{
						case CLIPBOARD_LINKDROP:
						{
							// note that ProxyNode.delete also updates the
							// ResourceTrees
							ResourceRef vrls[]=getClipBoardSelection();

							if (vrls[0]!=null)
							    finalNode.createLinkTo(vrls[0].getVRL());
							break;
						}
						case CREATELINKTO: 
						{
							// create link in parent ! 
							finalNode.getParent().createLinkTo(finalNode);
							break;
						}
						case DND_LINKDROPNODES:
						{
							// DnD linkdrop works only for one link
							VRL loc = actionCommand.nodeLocations[0].getVRL();
							finalNode.createLinkTo(loc);
							break;
						} 
						default: 
						    logger.warnPrintf("*** Warning: Unrecognized ActionCommand:%s\n",actionCommand);
						break;
					}
				}
				catch (VlException ex)
				{
					asyncHandle(ex);
				}
				finally
				{
					// no clean up to be done...
				}

                logger.debugPrintf("--- Finished Starting Action for:%s on %s\n",actionCommand,finalNode);
			}

			public void stopTask()
			{
			}; // nothing to stop/dispose, etc. 

		};

		task.startTask();
		this.currentTask = task;
		// task will update setBusy(); 
	}

	private void performNewWindow(VRL vrl)
    {
	    this.startNewWindow(vrl); 
    }

    private void performStartVLTerm()
	{
		VLTerm.newVLTerm(); 
	}

	private void performCopyMoveDrop(ProxyNode target, ResourceRef sources[], boolean isMove)
	{
	    // moved to interactive action handler: 
	    this.interactiveActionHandler.interactiveCopyMoveDrop(
		        target,
		        sources,
		        isMove); 
	}

	private void performCreate(final ProxyNode performNode, final String resourceType)
	{
		if (performNode.isMyVLe()) 
		{
			// andere koek
			performCreateServer(performNode,resourceType); 
			return; 
		}

		final String name = JOptionPane.showInputDialog("Creating:"+resourceType+"\n"
				+"Specify new name", null);

		if (name==null)
			return; // kankel 

		ActionTask createTask=new ActionTask(this,"Creating new "+resourceType+":"+name)
		{
			public void doTask() throws VlException
			{
				performNode.create(resourceType,name);
			}

			@Override
			public void stopTask()
			{
			}
		};

		createTask.startTask(); 
	}

	private void performCreateServer(ProxyNode performNode, String resourceType)
	{
		this.interactiveActionHandler.interactiveCreateServer(performNode,resourceType);
	}

	private void performEditACL(ProxyNode node)
	{
		try
		{
			nl.uva.vlet.gui.panels.acldialog.ACLPanel.showEditor(this,node);
		}
		catch (VlException e)
		{
			handle(e); 
		} 
	}

	/**
	 * Perform default 'action' for this node, typically a click 
	 * or a double click on an icon. 
	 * Current 'default' action is to view it.  
	 * Future actions might be other then 'view'.  
	 * @param actionNode
	 */
	private void performDefaultAction(ProxyNode actionNode)
	{
		ViewContext context=new ViewContext(this); 
		context.setStartInStandaloneWindow(false); // default action -> embed
		context.setStartedAsDefaultAction(true); // was default action
		performViewNode(actionNode, context,true); 
	}

	/** Perform stop ALL */ 
	public void performMasterStop()
	{
		ActionTask.stopActionsFor(this,false);
	}

	private static void performSwitchLAF(String lafstr)
	{
	    UIPlatform.getPlatform().switchLookAndFeel(lafstr);
	
		synchronized (controllers)
		{
			for (BrowserController bc : controllers)
			{
				SwingUtilities.updateComponentTreeUI(bc.vbrowser);
				bc.vbrowser.pack();
			}
		}
	}

	protected void setBusy(boolean val)
	{
		if (this.vbrowser == null)
			return; // browser already closed ! 

		final VBrowser vb = this.vbrowser;

		vb.setBusy(val);  
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				vb.updateMouseBusy();
			}
		});
	}

	protected void setSemiBusy(final boolean b)
	{
	    if (dummyMode == true)
			setBusy(b);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				vbrowser.stopButton.setEnabled(b);
				vbrowser.busyIcon.setEnabled(b); 
			}
		});
	}

	/** Start background view on location. */ 
	void asyncOpenLocation(final VRL location)
	{
		asyncOpenLocation(location,true,true); 
	}

	/** Start background view on location. */ 
	private void asyncOpenLocation(final VRL location,final boolean addToHistory,final boolean resolveLinks)
	{
		// Check Accessability during gui thread. 
		boolean status=false; 

		status = this.interactiveActionHandler.interactiveCheckAuthenticationFor(location);
		
		if (status==false) 
			return; // don't continue. Exception are already handled. 

		// Open Location in background !
		ActionTask task = new ActionTask(this, "OpenLocation:" + location)
		{
			public void doTask()
			{
				try
				{
					// already update text field: 
					setLocationTextField(location);
					ProxyNode node = openLocation(location);
					// Prefetch Attributes! 
					String mimeType=node.getMimeType(); 
				
					// set Root Resource node and add to History
					if ((node.isResourceLink()==true) && (resolveLinks==true)) 
					{
						node=node.getTargetPNode(); 
					}

					final ProxyNode finalNode=node; 

					Runnable viewTask=new Runnable()
					{
						public void run()
						{
							// Do the View Node in GUI event thread ! 
							performViewNode(finalNode, addToHistory);
						}
					};

					SwingUtilities.invokeLater(viewTask); 
				}
				catch (Throwable e)
				{
					asyncHandle(e);
				}
			}

			public void stopTask()
			{
			}; // nothing to stop/dispose, etc. 
		};

		task.startTask();
	}

	// since a "browse up" might actually trigger a openLocation() perform it in the background!
	protected void asyncBrowseUp()
    {
	    final VRL loc=getViewedLocation(); 
	    
	    ActionTask task = new ActionTask(this, "asyncBrowseUp():" + loc)
        {
            public void doTask()
            {
                try
                {
                    VRL parentLoc;
                   
                    ProxyNode pnode=getViewedNode(); 
        
                    if (pnode.isResourceLink())
                    {
                        // resolve link and browse "up" actual location: 
                        pnode=pnode.getTargetPNode(); 
                    }
                    
                    // get (default) logical location 
                    parentLoc=pnode.getParentLocation(); 
        
                    // root file system: -> browse to MyVle !  
                    if ((loc.isRootPath()==true) && (isEmpty(loc.getQuery())))
                    {
                        parentLoc=ProxyVRSClient.getInstance().getVirtualRootLocation(); 
                    }
                    
                    logger.debugPrintf("browseUp:parentLoc=%s\n",parentLoc); 

                    // Async -> Async ! 
                    asyncOpenLocation(parentLoc);
                }
                catch (VlException e)
                {
                    handle(e);
                }
            }

            @Override
            public void stopTask()
            {
            }
        };
        
        task.startTask(); 
    }
	
	private void performViewNode(ProxyNode node, boolean addToHistory)
	{
		ViewContext context=new ViewContext(this); // default, start embedded, etc.  
		context.setStartInStandaloneWindow(false); 
		context.setStartedAsDefaultAction(true); 
		performViewNode(node,context,addToHistory);  
	}

	/**
	 * Main method to start 'Viewing' a ProxyNode after it has opened. 
	 * Will populate Icons panel, update the Resource Tree and optionally 
	 * start an Item Viewer. 
	 * 
	 * @param actionNode
	 * @param optViewerClass
	 * @param addToHistory
	 * @param startExternalViewer
	 */
	private void performViewNode(ProxyNode actionNode,ViewContext viewContext, boolean addToHistory)
	{
		if (actionNode == null)
			return;

		// VRL vrls[]=this.getCopySelection();   

		boolean composite=false; 
		VRL targetLocation=null;
		//keep unresolved LinkNode for ResourceTree 
		ProxyNode orgNode=actionNode; 
		ProxyNode viewNode=null;

		//
		// Auto resolve link nodes when 'opening' them 
		//

		if (actionNode.isResourceLink())
		{
			try
			{
				// Use Logical Resource !
				updateLocationTextField(actionNode); 

				// Resolve Link: Will trigger new performViewNode()
				VRL target;
				target = actionNode.getTargetVRL();
				this.asyncOpenLocation(target, addToHistory,true);
				return; 
			}
			catch (VlException e)
			{
				handle(e); 
				return; 
			}
		} 

		ViewerPlugin viewer=null;

		try
		{
			targetLocation=actionNode.getVRL();
			viewNode=actionNode;

			//
			// If expliciti viewer is requested,  start external viewer.  
			//

			if ((actionNode.isComposite()==false) || (viewContext.getPreferredViewerClass()!=null))

			{
				composite=false; 
				viewer=getViewer(actionNode,viewContext.getPreferredViewerClass());

				if (viewer!=null)
				{
					viewer.setViewContext(viewContext); // update context
				}

				if ( (viewer!=null) 
						&&((viewer.getAlwaysStartStandalone()==true) || (viewContext.getStartInStandaloneWindow()==true))
				)
				{

					//***
					//***  Start External Viewer:
					//***

					startItemViewer(viewer,targetLocation,true);
					return; 
				}
				// else
				// ***
				// *** populate embedded viewer:
				// ***
			}
			else
			{
				// ***
				// *** populate Table or Icon Panels
				// *** 

				composite=true;
			}
		}
		catch (VlException e)
		{
			handle(e);
			return; // do not continue...
			// keep pnode 
		}

		messagePrintln("Viewing:" +targetLocation);

		//***
		//*** update text field+icon (if not already updated) 
		//***
		this.updateLocationTextField(viewNode); 

		this.viewedNode = viewNode;

		// populate View Panes:
		// populateViewWith(pnode);

		if (composite)
		{
			// switch from single view type to composite view type:
			if (this.viewType == ViewType.SINGLEITEM)
			{
				setView(this.previousCompositeViewType);
				//
				// Stop viewer if running 
				//

				if ((viewerManager!=null) && (this.viewerManager.hasViewer())) 
					this.viewerManager.signalStop();  
			}
			// else nothing keep current view
		}
		else
		{
			// must switch to singleItem:
			setView(ViewType.SINGLEITEM);
		}

		// populate the panels 

		switch (this.viewType)
		{
			case SINGLEITEM:
			{
				try
				{
					startItemViewer(viewer,targetLocation,false); 
				}
				catch (VlException e)
				{
					this.handle(e); 
				}
				break;
			}
			case ICONS:
			case ICONLIST: 
			{
				populateIconsPane(viewNode);
				break;
			}
			case TABLE:
			{
				populateTablePane(viewNode);
				break;
			}
		}

		// Update ResourceTree 

		try
		{
			// Init, when first starting a browser the resourcetree is empty !

			if (this.vbrowser.resourceTree.getRootNode() == null)
			{
				if (vbrowser.resourceTree.isRootVisible()==true)
					// init with MyVLe:  
					vbrowser.resourceTree.setRootNode(getVirtualRootNode(),orgNode.getVRL()); 
				else
					vbrowser.resourceTree.setRootNode(orgNode);
			}
			else
			{
				// update selection with current viewed node ! 
				vbrowser.resourceTree.updateSelection(orgNode, true);
			}
		}
		catch (VlException e)
		{
			handle(e);
		}

		// Finally add to history:

		if (addToHistory == true)
			addHistory();

	}

	/* 
	 * Either start as external viewer or set as embedded viewer in ItemViewer panel 
	 * If viewer.showInNewWindow() returns true the viewer will be always  
	 * shown in an external window. 
	 */



	private void startItemViewer(ViewerPlugin viewer, VRL location,boolean startStandalone) throws VlException
	{
		// spawn external viewer: 
		if ((viewer.getAlwaysStartStandalone()==true) || (startStandalone==true))
		{
			ViewerManager manager = new ViewerManager(this,new ViewContext(this),viewer); 
			manager.startStandAlone(location); 
			return; 
		}
		else
		{
			// reuse or initiliaze new embedded viewer:
		    if ((viewerManager!=null) && (this.viewerManager.hasViewer()))
			{
				// reuse current viewer:
				if (this.viewerManager.hasViewer(viewer.getClass())) 
				{
					viewerManager.asyncUpdateLocation(location); 
					// trigger item panel:
					updateItemViewer(viewerManager.getViewer());
					return;
				}
				else
				{
					// Terminate current Viewer: 
					viewerManager.signalFinalize();
				}
			}

		    // monitor viewers and their events: 
			viewer.addHyperLinkListener(this);
 
			// new ViewerManager: 
			this.viewerManager=new ViewerManager(this,new ViewContext(this),viewer);
			viewerManager.startForLocation(location);
			// ***
			// *** New embedded viewer, perform start in background:
			// *** 

			updateItemViewer(viewer);
			
		}
	}

	/** Return viewer class or Default Viewer for ProxyNode */ 
	private ViewerPlugin getViewer(String viewerClass)
	{
		return getViewer(null,viewerClass); 
	}

	/** Return viewer class or Default Viewer for ProxyNode */ 
	private ViewerPlugin getViewer(ProxyNode pnode, String optViewerClass)
	{
		String mimetype=null; 
		ViewerPlugin viewer=null;

		if (pnode!=null)
			mimetype=pnode.getMimeType(); 

		if ((mimetype==null)  && (optViewerClass==null)) 
		{
			logger.errorPrintf("Cannot determine viewer\n"); 
			mimetype="";
		}

		// do not start binary viewers unless specifically started with 'ViewWith...'
		if ((optViewerClass == null)
				&& (mimetype.compareToIgnoreCase("application/octet-stream") == 0))
		{
			// use default viewer to prevent binary viewers to garbage content
			viewer = new DefaultNodeViewer();
		}
		else
		{
			// find viewer for mimetype, no optional classname
			Class<? extends ViewerPlugin> viewerClass = ViewerRegistry.getRegistry().getViewerClassFor(mimetype, optViewerClass);
			if (viewerClass!=null)
			    viewer=ViewerRegistry.getRegistry().instanciateViewer(viewerClass);
		}

		if (viewer == null)
			viewer = new DefaultNodeViewer();

		// Embedded viewer: Browser controller wants to listen to viewer events 
		// to update the main browser 
		viewer.addHyperLinkListener(this);

		return viewer; 
	}

	private void errorPrintf(String str)
	{
		Global.errorPrintf(this,str); 
	}

    private ProxyNode openLocation(VRL location) throws VlException 
    {
        ProxyNode node=null; 
        //setBusy(true);
        node = ProxyNode.getProxyNodeFactory().openLocation(location,true);
        //setBusy(false);
        return node; 
    }
    
    private void performStartExternalViewer(final VRL location) 
    {
        final ViewContext context=new ViewContext(this); // default, start embedded, etc.
        
        ActionTask task = new ActionTask(this, "performStartExternalViewer:" + location)
        {
            public void doTask()
            {
                try
                {
                
                    ProxyNode node=null; 
                    //setBusy(true);
                    node = ProxyNode.getProxyNodeFactory().openLocation(location,true);
                    //setBusy(false);
                    context.setStartInStandaloneWindow(true); 
                    context.setStartedAsDefaultAction(true); 
                    performViewNode(node,context,false);
                }
                catch (VlException e)
                {
                    handle(e); 
                }
            }

            @Override
            public void stopTask()
            {
            }
        };
        
        task.startTask();
    }

	/**
	 * Update location with VRL. 
	 * Deletes icon +  ResourceType.
	 */
	private void setLocationTextField(VRL loc)
	{
		vbrowser.setLocationIcon(null);
		vbrowser.setLocationText(loc.toString()); 
		vbrowser.setTitle("VBrowser[" + this.browserId + "]:" + loc);
	}

	private void updateLocationTextField(ProxyNode node) 
	{
		if (this.vbrowser == null)
			return;// Oopsy vbrowser already disposed! 

		Icon icon=node.getDefaultIcon(16,false);

		// update icon+resourceRef:

		vbrowser.setLocationIcon(icon);
		vbrowser.setLocationText(node.getVRL().toString()); 

		vbrowser.setTitle("VBrowser[" + this.browserId + "]:" + node.getVRL());
	}

	/** Switch View, does not populate the Panels ! */
	void setView(ViewType vtype)
	{
		synchronized (vbrowser)
		{
			JScrollPane iconspanel = this.vbrowser.iconsScrollPane; 
			JScrollPane tablepanel = this.vbrowser.tableScrollPane; // tablePanel;

			switch (vtype)
			{
				case ICONS:
					this.previousCompositeViewType = vtype;
					// embed in  ScrollPane
					this.vbrowser.setMainViewTabComponent(iconspanel);
					this.vbrowser.iconsPanel.setViewType(IconViewType.ICONS48); 
					break;
				case ICONLIST:
					this.previousCompositeViewType = vtype;
					// embed in  ScrollPane
					this.vbrowser.setMainViewTabComponent(iconspanel);
					this.vbrowser.iconsPanel.setViewType(IconViewType.LIST_VER16); 
					break;
				case TABLE:
					this.previousCompositeViewType = vtype;
					// embed in  ScrollPane
					this.vbrowser.setMainViewTabComponent(tablepanel);
					break;
				case SINGLEITEM:
					// special case: keep previous Viewport and
					// wait until the Viewer update the Viewport.
					// viewtab.setViewportView(itemviewer);
					break;
				default:
					break;
			}
		}

		this.viewType = vtype;

		// update stored viewtype in history
		if (historyIndex >= 0) // can be empty
		{
			HistoryElement hel = (HistoryElement) history
			.elementAt(historyIndex);
			hel.viewType = vtype;
		}
	}

	public int addHistory()
	{
		return addHistory(getViewedLocation());
	}

	public int addHistory(VRL location)
	{
		synchronized (history)
		{
			if (historyIndex != history.size() - 1)
			{
				// truncate history to 'current'
				// currently browsing back: truncate history
				history.setSize(historyIndex + 1);
			}

			history.addElement(new HistoryElement(location,this.viewType));  
			historyIndex = history.size() - 1; // after adding, current
		}
		return historyIndex;
	}

	public void performBrowseUp()
	{
	    asyncBrowseUp();
    }

	private boolean isEmpty(String str)
	{
		if (str==null)
			return true;

		if (str.compareTo("")==0) 
			return true;

		return false;
	}

	public void performBrowseForward()
	{
		if (historyIndex + 1 >= history.size())
			return; // can't go further

		// Increase History Index
		historyIndex++;

		// Fetch History element and restore Browser State:
		HistoryElement hel = (HistoryElement) history.elementAt(historyIndex);

		// Restore View
		setView(hel.viewType); // restore view
		// do not add to history, we are browsing forward.
		ProxyNode pnode=getProxyNodeFactory().getFromCache(hel.viewedLocation);

		if (pnode!=null) 
		{
			performViewNode(pnode, false);
		}
		else
		{
			this.asyncOpenLocation(hel.viewedLocation); 
		}
	}

	public void performBrowseBack()
	{
		//debug("historyIndex= " + historyIndex);

		if (historyIndex <= 0)
			return; // can't go further

		historyIndex--; // PreDecrement: one to point to current, one extra to
		// point to previous !

		HistoryElement hel = (HistoryElement) history.elementAt(historyIndex);

		ProxyNode node = ProxyNode.getProxyNodeFactory().getFromCache(hel.viewedLocation);

		if (node==null)
		{
		    logger.errorPrintf("Oops: couldn't get node (back) from cache:%s\n",hel.viewedLocation);
			// perform browse:
			this.asyncOpenLocation(hel.viewedLocation); 
		}
		else
		{
			try
			{
				vbrowser.resourceTree.updateSelection(node,false); 
			}
			catch (VlException e)
			{
				handle(e);
			}
		}
		
		//debug("history [" + historyIndex + "]=" + node);
		// Restore View
		setView(hel.viewType); // restore view
		//debug("history [" + historyIndex + "].viewtype="+hel.viewType);

		// do not add to history, we are browsing back:
		performViewNode(node, false);
	}

	/** Print message line into the log window. */ 
	public void messagePrint(String str)
	{
		if (vbrowser != null)
		{
			JTextArea textArea = vbrowser.messageTextArea;
			// synchronized write actions to the textArea: 
			synchronized(textArea)
			{
				String orgtxt = textArea.getText();
				textArea.setText(orgtxt + str);
				textArea.setPreferredSize(textArea.getMinimumSize()); // update size: notify GUI
				// update scrolls:
				textArea.setCaretPosition(textArea.getText().length());
			}
		}
		else
		{
			// this is possible when the browser window has closed but a 
			// background job want to print a message: 
		    logger.infoPrintf("VBrowser:%s\n", str);
		}
	}

	// ==========================================================================
	// Non Thread Critical Methods
	// ==========================================================================

	/**
	 * Refreshes all resources and windows
	 * 
	 * @throws VlException
	 */
	private void performRefreshAll() throws VlException
	{
		if (viewedNode!=null) 
		{
			asyncRefresh(viewedNode); 
			
			// must reopen node: 
			this.asyncOpenLocation(viewedNode.getVRL(),false,true);  
		}
	}

	/**
	 * @return
	 */
	public ProxyNode getViewerProxyNode()
	{
		return this.viewedNode;
	}

	/** Close this VBrowser */ 
	public void performClose()
	{
		this.dispose();

		if (controllers.size()==0)
		{
		    logger.infoPrintf("Last browser closed: cleaning up!\n");
			performExitAll();
		}
	}

	/** Disposes all running vbrowsers and exits JVM */ 
	public static void performExitAll()
	{
		synchronized (controllers)
		{
			BrowserController list[] = new BrowserController[controllers.size()];
			controllers.copyInto(list);

			for (BrowserController bc : list)
			{
				bc.dispose();
			}
		}

		logger.debugPrintf("Running threads stage I:%d\n",Thread.activeCount());

		// release all resources hold by class ProxyNode
		ActionTask.disposeClass();
		ProxyNode.disposeClass();
		UIGlobal.shutdown(); 


		logger.debugPrintf("Running threads stage II:%d\n",Thread.activeCount());

		// 3 seconds till countdown for threads to cleanup:
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			System.out.println("***Error: Exception:"+e); 
			e.printStackTrace();
		}

        logger.debugPrintf("Running threads stage III:%d\n",Thread.activeCount());

		System.exit(0);

	}

	/** Help garbage collector and really destroy this object */
	private void dispose()
	{
		/*
		 * Although the Garbage collector should be able to detect disposed
		 * resources, in a multithreaded environment, sleeping threads can hold
		 * resources. You can help the garbage collector, and speed up shutting
		 * down, be nullifing references to objects.
		 */
		synchronized (controllers)
		{
			controllers.remove(this);
		}

		// unregister: 
		UIGlobal.getGridProxy().removeGridProxyListener(this); 
	    ProxyVRSClient.getInstance().removeResourceEventListener(this); 

		if (this.vbrowser!=null)
			this.vbrowser.dispose();

		// To be investigated: break mutual dependency and help garbage
		// collector !
		this.history.clear(); 
		this.history= null; // Big Resource Container !
		this.vbrowser = null;

		ActionTask.debugPrintTasks();

	}

	private void updateItemViewer(IMimeViewer viewer)
	{
//		// dispose previous viewer: 
//		if ((this.itemviewer != null)  && (viewer!=this.itemviewer))
//		{
//			// stop and dispose previous item viewer!!!:
//			this.itemviewer.signalFinalize();
//		}
//
//		// set to new one: 
//		this.itemviewer = viewer;

		Component viewComponent=viewer.getViewComponent(); 
		Component viewPane=viewComponent; 
		if (viewer.haveOwnScrollPane()==false)
		{
			vbrowser.viewerScrollPane.setViewportView(viewComponent);
			vbrowser.viewerScrollPane.setName(viewer.getName()); 
			// use scrollpane: 
			viewPane=vbrowser.viewerScrollPane; 
		}

		// switch to item view mode: 

		this.vbrowser.setMainViewTabComponent(viewPane); 
	}

	/** Print message line + newline into the log window. */
	public void messagePrintln(String str)
	{
		messagePrint(str + "\n");
	}

	private void populateTablePane(ProxyNode pnode)
	{
		try
		{
			this.vbrowser.tablePanel.populateWith(pnode);
		}
		catch (VlException e)
		{
			handle(e); 
		}
	}

	private void populateIconsPane(ProxyNode pnode)
	{
		if (pnode == null)
			pnode = this.viewedNode;

		if (pnode==null) 
			return; // nothing viewed ?  

		try
		{
			vbrowser.iconsScrollPane.setToolTipText(pnode.getVRL().toString());
			vbrowser.iconsPanel.setRootNode(pnode);
		}
		catch (VlException e)
		{
			handle(e);
		}
	}

	public void windowActivated(WindowEvent arg0)
	{
	    // wakeup!
	}

	//Is called AFTER the window has been closed. So when a dispose is called !
	public void windowClosed(WindowEvent arg0)
	{
	}

	/**
	 * Window closing event is also called when user presses the 'x' button in
	 * the topleft.
	 * 
	 * This should have the same effect as Resource->Close
	 */
	public void windowClosing(WindowEvent arg0)
	{
		performClose();
	}

	// sleep: 
	public void windowDeactivated(WindowEvent arg0)
	{
	}

	public void windowDeiconified(WindowEvent arg0)
	{
	}

	public void windowIconified(WindowEvent arg0)
	{
	}

	public void windowOpened(WindowEvent arg0)
	{
	}

	private Throwable lastException=null;  
	private Long handleMutex=new Long(0);  

	// method to satisfy compiler
    public void handle(Exception e)
    {
        _handle(e); 
    }
    
    public void handle(Throwable e)
    {
        _handle(e); 
    }
    
	private void asyncHandle(final Throwable e)
    {
	    // handle synchronizes:
	    _handle(e); 
    }
	
	private void _handle(final Throwable e)
	{
	    if (e==null)
	    {
	        logger.logException(ClassLogger.WARN,e,"*** Error in handle(): Exception==null ***\n"); 
	        return;
        }

		// asynchronous exception 
		// Do No Invoke Outside Event Thread: 
		if (UIGlobal.isGuiThread()==false)
		{
			Runnable run=new Runnable()
	        {
	            public void run()
	            {
	                handle(e); 
	            }
	        };

	        SwingUtilities.invokeLater(run); 
 			return; 
		}

		// Previous code has synchronized possible multiple 
		// threads into on single (GUI Swing) thread. 

		// Since the VBrowser is multithreaded, in rare occasions two (or more) the
		// same exceptions can occure when accessing a resource. 
		// When receiving the same exception within 0.1 second, the 2d one 
		// is dismissed (or possible more within the same time frame). 
		synchronized(handleMutex)
		{
			if ( (lastException!=null) &&  (lastException.getMessage()!=null) )
				if (e.getMessage()==null)
				{
					;//errorMessage("NULL message in exception:"+e); 
				}
				else if (lastException.getMessage().compareTo(e.getMessage())==0) 
					if ((System.currentTimeMillis()-handleMutex)<100)
						return; 

			lastException=e;
			// mutex object might be changed, but that is allowed. 
			handleMutex=System.currentTimeMillis(); 
		}

		logger.logException(ClassLogger.ERROR,e,"Exception!\n");
		ExceptionForm.show(e);
	}

	/** space filler method */
	public static String spaces(int nr)
	{
		char chars[] = new char[nr];
		for (int i = 0; i < chars.length; chars[i++] = ' ')
			;
		// empty body
		String str = new String(chars);
		return str;
	}

	/** Show Grid Proxy Dialog */ 
	public void performProxyDialog()
	{
		GridProxyDialog dialog = new GridProxyDialog(UIGlobal.getVRSContext(),this.vbrowser);
		dialog.setVisible(true);
	}
	
	public ResourceRef[] getClipBoardSelection()
	{
		return staticCopySelection;
	}

	private void setCopySelection(VComponent vcomp) 
	{
		if (vcomp==null)
		{
			staticCopySelection=null;
			staticCopySeletectionIsCut=false;
			return;
		}

		staticCopySelection=new ResourceRef[1];
		staticCopySelection[0]=vcomp.getResourceRef();
	}

	private boolean getCopySelectionIsCut()
	{
		return staticCopySeletectionIsCut;
	}

	// either a selection or an unselection has occured 
	public void setSelection(VComponent selection)
	{
		// update last focus component 
		if (selection==null)
			lastActiveVContainer=null; 
		else if(selection instanceof VContainer)
			lastActiveVContainer=(VContainer)selection; 
		else
			lastActiveVContainer=selection.getVContainer(); 
		
		updateEditMenuFor(selection);
	}

	private void updateEditMenuFor(VComponent vcomp)
	{
		vbrowser.createMenuItem.removeAll();
		
		// populate create menu 
		//ProxyNode node=ProxyNode.getFromCache(vcomp.getVRL()); 

		JMenu menu = ActionMenu.createNewSubMenu(this,vcomp);
		
		boolean hasMultipleSelections=false;  
		VContainer vcontainer=null; 
		
		if (vcomp==null)
		{
			vcontainer=null; 
		}
		else if (vcomp instanceof VContainer) 
		{
			vcontainer=(VContainer)vcomp;
		}
		else
		{
			// click on single component 
			vcontainer=vcomp.getVContainer(); 
		}
		
		ResourceRef[] refs = null;
		boolean haveSelection=false;
		boolean haveSingleSelection=false;
		boolean haveMultipleSelections=false;
		
		if (vcontainer!=null)
			refs=vcontainer.getSelection(); 
			
		if (refs!=null)
		{
			if (refs.length==1)
				haveSingleSelection=true; 
			
			if (refs.length>=1)
				haveSelection=true; 
			
			if (refs.length>=2)
				haveMultipleSelections=true; 
		}
		
		Component[] comps = menu.getMenuComponents();

		for (Component comp:comps)
			this.vbrowser.createMenuItem.add(comp);

		//this.vbrowser.createMenuItem.add(menu);

		vbrowser.deleteMenuItem.setEnabled(haveSingleSelection);
		vbrowser.renameMenuItem.setEnabled(haveSingleSelection); 
		// block gui:
		//vbrowser.deleteMenuItem.setEnabled((node!=null) && (node.isDeletable()));
		//vbrowser.renameMenuItem.setEnabled((node!=null) && (node.isRenamable()));

		// update cut,copy,paste: 
		vbrowser.cutMenuItem.setEnabled(haveActiveSelection()); 
		vbrowser.copyMenuItem.setEnabled(haveActiveSelection()); 
		vbrowser.pasteMenuItem.setEnabled(haveClipboardSelection());
		vbrowser.pasteAsLinkMenuItem.setEnabled(haveClipboardSelection());
		vbrowser.createLinkToMenuItem.setEnabled(haveSingleSelection); 
	}

	public boolean haveClipboardSelection()
	{
		return ((staticCopySelection!=null) && (staticCopySelection.length>0)); 
	}

	// Have only ONE item in selection list ! 
	public boolean haveSingleClipboardSelection()
	{
		return ((staticCopySelection!=null) && (staticCopySelection.length==1));
	}

	public boolean haveActiveSelection() 
	{
		boolean val=(getActiveSelection()!=null);
		
		return val; 
    }
	
	/** Active Selection only from IconsPanel ! */
	public ResourceRef[] getActiveSelection()
	{
		if (this.vbrowser.hasFocus())
		{
			return this.vbrowser.iconsPanel.getSelection();
		}
		else if (this.vbrowser.resourceTree.hasFocus())
		{
			return this.vbrowser.resourceTree.getSelection();
		}
		else if (lastActiveVContainer!=null)
		{
			return lastActiveVContainer.getSelection();
		}
		else
		{
			return null;
		}
	}

	private void setCredentialStatus(boolean stat)
	{
		if (stat == true)
		{
			this.vbrowser.credentialButton.setIcon(new ImageIcon(getClass()
					.getClassLoader().getResource("menu/keys_ok.png")));
		}
		else
		{
			this.vbrowser.credentialButton.setIcon(new ImageIcon(getClass()
					.getClassLoader().getResource("menu/keys_notok.png")));
		}

		this.vbrowser.credentialButton.repaint();
	}

	public boolean checkCredentialStatus()
	{
	    try
	    {
    		boolean stat = UIGlobal.getGridProxy().isValid();
    		setCredentialStatus(stat);
    		return stat;
	    }
	    catch (Throwable t)
	    {
	        this.handle(t); 
	    }
	    
	    return false; 
	}

	public JFrame getFrame()
	{
		return this.vbrowser;
	}

	/** Create ProxyNode menu and show it in the JComponent */
	public void showNodeMenu(Component comp, int x, int y)
	{
		ActionMenu menu = ActionMenu.createFor(this, (VComponent)comp);
		menu.show(comp, x, y);
	}

	/** Create ProxyNode menu from VComponent and show it in the JComponent */
	public void showNodeMenu(Component comp, VComponent vcomp,int x, int y)
	{
		ActionMenu menu = ActionMenu.createFor(this, vcomp);
		menu.show(comp, x, y);
	} 

	private void showTasks()
	{
		ActionTask.debugPrintTasks();
	}

	public String getID()
	{
		return "" + this.browserId;
	}

	public void setHasTasks(boolean val)
	{
        logger.debugPrintf("[%d]:setHasTasks=%s\n",Thread.currentThread().getId(), val);

		if (this.vbrowser == null)
			// oopsy: browser already disposed !
			return;

		setSemiBusy(val);
	}

	public ProxyNode getViewedNode()
	{
		return this.viewedNode;
	}

	// ========================================================================
	// Experimental VComponent interface ! 
	// ========================================================================

	public VRL getVLocation()
	{
		return this.viewedNode.getVRL(); 
	}

	public BrowserController getBrowserController()
	{
		return this; 
	}

	/** 
	 * An event from an (embedded) viewer has occured 
	 */ 
	public void notifyHyperLinkEvent(ViewerEvent event)
	{
		switch (event.type)
		{
			case VIEWER_CLOSED_EVENT:
			{
				// Viewer stopped/closed/
				break; 
			}
            case HYPER_LINK_EVENT:
            {
                // if event is coming from current Viewer,
                // follow the hyper link ! 
                logger.debugPrintf(">>> Hyperlink event:%s\n",event.getVRL());
                
                //IMimeViewer currentViewer = this.viewerManager.getViewer();
                
                if (event.isOpenAsNew())
                {
                    performStartExternalViewer(event.getVRL());
                }
                else
                {
                    // if (event.getViewer()==this.itemviewer)
                    this.performOpenLocation(event.getVRL());
                    //else
                    //  sDebug("Ignoring External Viewer event:"+event);
                }
                
            }
            case LINK_FOLLOWED_EVENT:
            {
                // follow viewer: update locations and history:
                logger.debugPrintf(">>> Follow Link  event:%s\n",event.getVRL());

                // Follow Event ONLY allowed from embedded viewer!
                IMimeViewer source = event.getViewer();
                if (viewerManager!=null)
                {
                    IMimeViewer current = this.viewerManager.getViewer();
                    
                    if ( (source!=null) && source.equals(current))
                    {
                        this.followViewerLocation(event.getVRL());
                    }
                }
            }

			break;
			default:
				break; 
		}
	}

	public ViewModel getViewModel()
	{
		return viewModel; 
	}
	
	private void initViewFilter()
	{
		
		ViewFilter viewFilter=new ViewFilter(); 
		viewFilter.setSortField1(VAttributeConstants.ATTR_TYPE); 
		viewFilter.setSortField2(VAttributeConstants.ATTR_NAME);

		boolean stat=getGuiSettings().getBoolProperty(GuiPropertyName.GLOBAL_FILTER_HIDDEN_FILES);
		viewFilter.setFilterHidden(stat);

		// returned update viewModel; 
		this.viewModel.setViewFilter(viewFilter);
	}
	
	 public void notifyProxyEvent(ResourceEvent e)
	 {
		 VRL currentLoc=this.getViewedLocation(); 
		 
		 boolean isForCurrent=currentLoc.equals(e.getSource());  
		 
		 // check if node is in cache:
	        
		 //if (node!=null)
		 //{
	            
		 // udpate cache:
		 switch (e.getType())
		 {
			 case SET_ATTRIBUTES:
			 case SET_CHILDS:
			 case CHILDS_DELETED:
			 case CHILDS_ADDED:
			 case DELETE:
			 case REFRESH:
			 case RENAME:
	                	
			 case NO_EVENT:
			 case SET_BUSY: 
				 break;
			 case MESSAGE:
				 // use global display since this event must be shown
				 // only once. 
				 handleMessageEvent(e); 
				 break; 
			 default:
				 break; 
			 //}

			 /*try
	            {
	                // node.refresh();
	            }
	            catch (VlException ex)
	            {
	                TermGlobal.errorPrintln(this,"***Error: Exception:"+ex); 
	                TermGlobal.errorPrintStacktrace(ex);
	            }*/
		 }
	 }
	 
	public void handleMessageEvent(ResourceEvent e)
	{

		// current filter: only show message events: 

		if (e.getType()!=EventType.MESSAGE)
			return; 

		String eventMessage=e.getMessage();

		if (eventMessage==null) 
			eventMessage="";

		String message="*** Event:"+e.getType()+"***\n"
		+" Source="+e.getSource()+ "\n";

		if (eventMessage!=null)
		{
			message+=" --- Message  ---\n"; 

			// format message string: check newline and add indentation to it: 
			String indentstr=" >";
			message+=indentstr;  

			for (int i=0;i<eventMessage.length();i++)
			{
				if ((eventMessage.charAt(i)=='\n') && (i<eventMessage.length()-1))
					message+="\n"+indentstr; 
				else
					message+=eventMessage.charAt(i); 
			}
		}       

		messagePrint(message);
		
		displayEventMessage(e);
	}

	/** Display Global Message */ 
	public static void displayEventMessage(ResourceEvent e)
	{
		String message=e.getMessage();
		SimpleDialog.displayMessage(null,"Resource Event Message",message);
	}

	/**
	 * Returns master JFrame from for frameless components. 
	 * This frame isn't shown but is use as widget factory 
	 * or to connect other swing component who need a (J)Frame as parent. 
	 */
	public static JFrame getMasterFrame()
	{
		return masterFrame;
	}

	/**
	 * Perform DnD actions, this method is both used for interactive 
	 * DnDs and non-interactive ones. 
	 */
	public void performDragAndDrop(DropAction drop)
	{
	    logger.debugPrintf("performDragAndDrop:%s\n",drop);
		
		// Interactive DnD ! 

		if (drop.interactive==true) 
		{
			if (drop.component==null) 
			{
			    logger.errorPrintf("NULL component for interactive Drag&Drop menu!\n"); 
				return; 
			}

			JMenuItem item=null; 
			JPopupMenu pop = CopyDropActionMenu.createFor(this,drop);

			if (drop.point==null)
				drop.point=new Point(8,8); 
			else
			{
				drop.point.x-=8;
				drop.point.y-=8;

				if (drop.point.x<1) drop.point.x=1; 
				if (drop.point.y<1)	drop.point.y=1; 
			}

			pop.show(drop.component,drop.point.x,drop.point.y); 
			pop.setVisible(true); 
			pop.requestFocus();

			return;
		}

		ActionCommand cmd = null;

		// Note: 
		// The DragAndDrop can be a inter-browser action. 
		// The browser which performs the action is the one which 'ownes' the DropTarget. 
		// 

		if (drop.isLink()==true)
			cmd = new ActionCommand(ActionCommandType.DND_LINKDROPNODES);
		else if (drop.isMove())
			cmd = new ActionCommand(ActionCommandType.DND_MOVEDROPNODES);
		else
			// default to copy(): 
			cmd = new ActionCommand(ActionCommandType.DND_COPYDROPNODES);

		// cmd.targetLocation=targetLoc;

		cmd.nodeLocations=drop.sources; 

		//  let (target) BrowserController do the action !
		performAction(drop.destination, cmd);
	}

	public JPopupMenu getActionMenuFor(VComponent vcomp)
	{
		ActionMenu menu = ActionMenu.createFor(this, vcomp);

		return menu;
	}

	public ActionContext createSelectionContext(VComponent vcomp)
	{
		ResourceRef clipBoardSelections[] = getClipBoardSelection();
		VContainer vcontainer=null;

		boolean selectionIsClipboard=false; 

		// canvasmenu==true vcontainer=vcomp 
		// canvasmenu==false vconainer=parent of vcomp

		if (vcomp==null)
		{
		    vcontainer=null; // NO component selected !
		}
		else if (vcomp instanceof VContainer)
		{
			vcontainer=(VContainer)vcomp; 
		}
		else 
		{
			vcontainer=vcomp.getVContainer(); 
		}

		ResourceRef selections[]=null; 

		if (vcontainer!=null)
			selections=vcontainer.getSelection();

		if (selections==null)
		{
			selections=clipBoardSelections;
			if (clipBoardSelections!=null)
				selectionIsClipboard=true; 
		}
		
		ActionContext selContext=null; 
		
		// todo ResourceRef data in vrs.core (ActionContext !) 
        if (vcomp==null)
        {
            selContext=new ActionContext(); // EMPTY ACTION CONTEXT 
        }
        else
        {
            // source + selection ActionContext: 
            ResourceRef ref=vcomp.getResourceRef(); 

            String sourceType=ref.getResourceType();
            if (sourceType==null)
                throw new NullPointerException("Type of Resource can not be null:"+vcomp); 

            VRL sourceVRL=ref.getVRL();
            String sourceMimeType=ref.getMimeType(); 

            selContext=new ActionContext(sourceType,sourceVRL,sourceMimeType);
        }
		
        // ===
        // check selections
        // ===
        if (selections!=null)
        {
            String selTypes[]=ResourceRef.getTypes(selections); 
            VRL selVRLs[]=ResourceRef.getVRLs(selections); 
            String selMimeTypes[]=ResourceRef.getMimeTypes(selections);
            selContext.setSelections(selTypes,selVRLs,selMimeTypes,selectionIsClipboard); 
        }


		//selContext.setIsClipboardSelection(selectionIsClipboard); 
		selContext.setIsContainer(vcontainer!=null);
		selContext.setIsMiniDnDMenu(false); 

		return selContext; 
	}

	public void performDynamicAction(final ActionCommand actionCommand, final ActionContext actionContext, final VComponent vcomp)
	{
		final VRSFactory vrs=actionCommand.getVRS();
		//final String iviewerClassName=actionCommand.getViewerClassName(); 

		final String methodName=actionCommand.getDynamicActionName();
		//final String menuMethodName=actionCommand.getActionMessage(); 
		
		logger.infoPrintf("performDynamicAction:PerformActionCommand:%s\n",methodName);
		logger.infoPrintf("performDynamicAction:Selection context = %s\n",actionContext);

		if (vrs==null) 
		{
		    logger.errorPrintf("VRS not defined for action command:%s\n",actionCommand); 
			return;
		}

		final BrowserController bc=this; 

		ActionTask dynamicAction=new ActionTask(bc,"Performing action:"+methodName)
		{
			public void doTask() throws VlException
			{
				// get task monitor of already started task ! 
				ITaskMonitor monitor = this.getTaskMonitor(); 
				
				try
				{
					vrs.performAction(monitor,UIGlobal.getVRSContext(), methodName, actionContext);
				}
				catch (VlException e)
				{
					monitor.setException(e); 
					bc.handle(e); 
				}
			}

			@Override
			public void stopTask()
			{
			}
		};

		dynamicAction.startTask();
		nl.uva.vlet.gui.panels.monitoring.TaskMonitorDialog.showTaskMonitorDialog(this.getFrame(), dynamicAction,1000); 
	}

    public void performDynamicViewerAction(final ActionCommand actionCommand, final ActionContext actionContext) throws VlException
	{
//		String iviewerClassName=actionCommand.getViewerClassName(); 
//		String methodName=actionCommand.getDynamicActionName();
//	
//		Global.infoPrintln(this, "PerformActionCommand = "+methodName);
//		Global.infoPrintln(this, "ViewerClass          = "+iviewerClassName);
//		Global.infoPrintln(this, "Selection context    = "+actionContext);
//
//		if (iviewerClassName==null) 
//		{
//			throw new VlException("ViewerException","Viewer not defined for action command:"+actionCommand); 
//		}
//	    // could be null! 
//        VRL baseLocation=actionContext.getSource(); 
//
//        ViewContext context=new ViewContext(this); // default, start embedded, etc.  
//        context.setStartStandalone(true); 
//        context.setStartedAsDefaultAction(false); 
//        context.setStartupLocation(baseLocation); 
//        
//		ViewerPlugin viewer = this.getViewer(iviewerClassName); 
//	    ViewerManager viewerManager=new ViewerManager(this,viewer); 
//	    viewer.setViewContext(context);  
//	    viewerManager.startForAction(methodName,actionContext); 
	    performStartStandaloneViewer(actionCommand,actionContext);
	}
	
	public void performStartStandaloneViewer(final ActionCommand actionCommand, final ActionContext actionContext) throws VlException
    {
        String iviewerClassName=actionCommand.getViewerClassName(); 
        // can be null! 
        String methodName=actionCommand.getDynamicActionName();
        // could be null! 
        VRL baseLocation=actionContext.getSource(); 

        Global.infoPrintf(this, "PerformActionCommand = %s\n",methodName);
        Global.infoPrintf(this, "ViewerClass          = %s\n",iviewerClassName);
        Global.infoPrintf(this, "Selection context    = %s\n",actionContext);

        if (iviewerClassName==null) 
        {
            throw new VlException("ViewerException","Viewer not defined for action command:"+actionCommand); 
        }

        ViewContext context=new ViewContext(this); // default, start embedded, etc.  
        context.setStartInStandaloneWindow(true); 
        context.setStartedAsDefaultAction(false); 
        context.setStartupLocation(baseLocation); 
        
        ViewerPlugin viewer = this.getViewer(iviewerClassName); 
        ViewerManager viewerManager=new ViewerManager(this,context,viewer);     
        // viewer.setViewContext(context);   
        viewerManager.startFor(baseLocation, methodName, actionContext); 
    }

	// 
	public ProxyNodeFactory getProxyNodeFactory()
	{
		return ProxyNode.getProxyNodeFactory(); 
	}
	  /**
     * Clears all cached attributes. Sent an event that resource listeners
     * should check/refetch their attributes.
     * 
     * @throws VlException
     * 
     */
    public void asyncRefresh(final ProxyNode node)
    {
        // go background: 
        ActionTask refreshTask=new ActionTask(this,"Properties Update Task")
        {
        	public void doTask() throws VlException
        	{
        		node.refresh(); 
        	}

        	@Override
        	public void stopTask()
        	{
        	}
        };
        
        refreshTask.startTask(); 
    }
    
    public boolean interactiveCheckAuthenticationFor(VRL vrl)
	{
    	return this.interactiveActionHandler.interactiveCheckAuthenticationFor(vrl); 
	}
    
	private ProxyNode getVirtualRootNode() throws VlException
	{
		return ProxyNode.getVirtualRoot(); 
	}

    public ClassLogger getLogger()
    {
        return logger;
    }

    // ========================================================================
    // Grid Proxy Events 
    // ========================================================================
    
    @Override
    public void notifyCACertStoreUpdated(String alias)
    {
        logger.infoPrintf("Received CACertStoreUpdated: alias=%s\n",alias);
    }

    @Override
    public void notifyProxyAttributesChanged(GridProxy proxy,VAttribute attrs[])
    {
        int index=0;
        boolean reset=false; 
        boolean validityChanged=false; 
        boolean valid=proxy.isValid(); 
        
        for (VAttribute attr:attrs)
        {
            if (attr.hasName(GridProxy.ATTR_VALIDITY))
                validityChanged=true; 
            if (attr.hasName(GridProxy.ATTR_VOINFO))
                reset=true;
            if (attr.hasName(GridProxy.ATTR_ENDTIME))
                reset=true;
            
            logger.infoPrintf("Received Proxy Attribute event: #%d - %s=%s\n",index++,attr.getName(),attr.getValue());
        }
        
        // ask for a proxy/vrs reset (and perform it):
        //if (reset && (newproxy==false))  
        //    interactiveActionHandler.interactiveDoProxyRefresh(attrs);
        // only refresh if validity still true. 
        if ((valid==true) && (validityChanged==false) && (reset==true))
            interactiveActionHandler.interactiveDoProxyRefresh(attrs);
        
        //if (valid==true)
        //    resetVRS();   
    }
    
    @Override 
    public void notifyProxyValidityChanged(GridProxy gridProxy, boolean newValidity)
    {
        logger.infoPrintf("Received proxy validity=%s\n",newValidity);
        setCredentialStatus(newValidity);
    }

    public void resetVRS()
    {
        UIGlobal.resetContext();
        ProxyNode.getProxyNodeFactory().reset();
        
        try
        {
            this.asyncOpenLocation(UIGlobal.getVirtualRootLocation(),false,false);
        }
        catch (VlException e)
        {
            handle(e); 
        }
    }

    @Override
    public void startNewWindow(VRL vrl)
    {
        factory.createBrowser(vrl); 
    } 

    public VBrowserFactory getFactory()
    {
        return factory;  
    }
    
    public GuiSettings getGuiSettings()
    {
        return GuiSettings.getDefault(); 
    }
}
