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
 * $Id: ActionMenu.java,v 1.6 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.actions;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.actions.ActionMenuMatcher;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.Messages;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.viewers.ViewerInfo;
import nl.uva.vlet.gui.viewers.ViewerRegistry;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSFactory;


/**
 *  
 * Custom ActionMenu for ProxyNode.
 * 
 * @author P.T. de Boer
 */
public class ActionMenu extends JPopupMenu
{
    /** Needed by  swing */  
    private static final long serialVersionUID = 8847551755720038352L;
	private static ActionMenuMatcher actionMenuMatcher;

    private static JMenuItem createMenuItem(BrowserController bc,VComponent vcomp,String name,ActionCommandType cmd)
    {
        JMenuItem mitem = new JMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmd.toString()); 
        mitem.addActionListener(new PopupListener(bc, vcomp));

        return mitem; 
    }
    

	private static JMenuItem createMenuItem(BrowserController bc, VComponent vcomp, String name, ActionCommand cmd)
	{
		JMenuItem mitem = new JMenuItem();
		mitem.setText(name); 
		mitem.setActionCommand(cmd.toString()); 
		mitem.addActionListener(new PopupListener(bc, vcomp));
		
		return mitem; 
	}

    
    public static JMenu createNewSubMenu(BrowserController bc, VComponent vcomp)
    {
        JMenu menu = new JMenu(); 
        menu.setText("New");
       
        String types[]=null;
        ProxyNode pnode=null; 
    	boolean multipleSelections=hasMultipleSelections(vcomp);
    	
        if (vcomp!=null)
        {
        	pnode=getPNodeFrom(vcomp); //ProxyNode.getProxyNodeFactory().getFromCache(vcomp.getResourceRef().getVRL()); 
        	// check multiple selection from VContainer: 
        }
        
        if (pnode!=null) 
            types=pnode.getResourceTypes();

        if ((types==null) || (multipleSelections==true))
        {
            JMenuItem mitem = new JMenuItem();
            mitem.setText("(None)"); 
            mitem.setEnabled(false); 
            menu.add(mitem);
            menu.setEnabled(false); 
            return menu;
        }

        
        for (int i=0;(types!=null) && (i<types.length);i++)
        {
            JMenuItem mitem = new JMenuItem();
            mitem.setText(types[i]);  
            mitem.setActionCommand(ActionCommand.createAction(ActionCommandType.CREATE,types[i]).toString()); 
            mitem.addActionListener(new PopupListener(bc,vcomp));
            menu.add(mitem); 
        }
        
        return menu;
    }
    
    /*private static JMenu createLinkSubMenu(BrowserController bc, VComponent vcomp)
    {
        JMenu menu = new JMenu(); 
        menu.setText("Link"); 
        JMenuItem mitem=null;
        
        menu.add(mitem=createMenuItem(bc,vcomp,"Edit Link", ActionCommandType.EDITLINK));
        
        menu.add(mitem=createMenuItem(bc,vcomp,"Properties", ActionCommandType.EDITPROPERTIES));
        
        return menu;
    }*/
    
    private static JMenu createViewersSubMenu(BrowserController bc, VComponent vcomp)
    {
        JMenu menu = new JMenu(); 
        menu.setText("View with..."); 
        String mimetype=null; 
        
        ProxyNode pnode=getPNodeFrom(vcomp);
        
        if (pnode==null)
        {
        	 menu.add(new JMenuItem("NULL:"+vcomp.getResourceRef().getVRL()));
        	 return menu; 
        }
       /* if (pnode.isComposite()) 
        {
            menu.setEnabled(false); // no viewers for composite: 
            return menu;
        }*/
        
        if (pnode.isResourceLink())
        {
            try
            {
                mimetype=pnode.getTargetMimeType();
            }
            catch (VlException e)
            {
                Global.errorPrintf(ActionMenu.class,"Couldn't resolve:%s\n",pnode); 
                // e.printStackTrace(TermGlobal.debugStream);
                mimetype=pnode.getMimeType();
            }
        }
        else
           mimetype=pnode.getMimeType();
        
        // Disable for NULL mimetype'd composite node (VDir,etc)
        if ((mimetype==null) && (pnode.isComposite()==true))
        {
        	menu.setEnabled(false); 
        	return menu;
        }
        
        
        ViewerRegistry.ViewerList vlist=ViewerRegistry.getRegistry().getViewerListForMimetype(mimetype); 
        JMenuItem mitem=null;
        
        if (vlist==null)
        {
            menu.add(mitem=new JMenuItem("(no default)")); 
            mitem.setEnabled(false); 
            // return menu; 
            
            /*JMenuItem mitem = new JMenuItem();
            mitem.setText("(No Viewer for Mimetype:"+mimetype+")"); 
            mitem.setEnabled(false); 
            menu.add(mitem); 
            
            return menu;*/ 
        }
        else for (ViewerInfo vinfo:vlist)
        {
            //ViewerRegistry.ViewerInfo vinfo=vlist.elementAt(i);
            mitem = new JMenuItem();
            mitem.setText(vinfo.getName());  
            mitem.setActionCommand(ActionCommand.createStartViewerAction(vinfo.getClassName()).toString()); 
            mitem.addActionListener(new PopupListener(bc, vcomp));
            menu.add(mitem); 
        }
        
        menu.add(new JSeparator());
        
        // Add all viewers under Other... 
        JMenu allmenu = new JMenu();
        allmenu.setText("Other...");
        menu.add(allmenu); 
        vlist=ViewerRegistry.getRegistry().getAllViewers();
        
        for (ViewerInfo vinfo:vlist)
        {
            //ViewerRegistry.ViewerInfo vinfo=vlist.elementAt(i);
            mitem = new JMenuItem();
            mitem.setText(vinfo.getName());
            mitem.setActionCommand(ActionCommand.createStartViewerAction(vinfo.getClassName()).toString()); 
            mitem.addActionListener(new PopupListener(bc, vcomp));
            allmenu.add(mitem); 
        }
        
        return menu; 
    }

    
    /** Create Default VNode menu */ 
    
    public static ActionMenu createFor(BrowserController bc,VComponent vcomp)
    {
        ActionMenu menu = new ActionMenu();
        JMenuItem mitem=null;
        
        boolean canvasMenu=(vcomp instanceof VContainer); 
        
        // 
        // default browser menu 
        // 
        
        if (canvasMenu==false)
        {
          menu.add(createMenuItem(bc,vcomp,"Open", ActionCommandType.OPEN)); 
          menu.add(createMenuItem(bc,vcomp,"Open in New Window", ActionCommandType.OPENINNEWWINDOW));
        
        
          //menu.add(mitem=createMenuItem(bc,vcomp,"Open in New Tab", ActionCommandType.OPENINTAB));
          //mitem.setEnabled(false); 

          menu.add(new JSeparator());
        }
        
        addMenuItems(menu,bc,vcomp,canvasMenu);
        
        //
        // Resource Interface Menu
        // menu action which can be derived from the VRSFactory Object Type 
        //
        menu.add(new JSeparator());
        
        // other 
        menu.add(createMenuItem(bc,vcomp,"Refresh", ActionCommandType.REFRESH)); 
        menu.add(createResourceSubMenu(bc,vcomp));
                
        menu.add(new JSeparator());
        
        menu.add(mitem=createMenuItem(bc,vcomp,"Properties", ActionCommandType.EDITPROPERTIES));
        mitem.setToolTipText("View and/or edit resource properties");
        
        return menu; 
    }

    private static Vector<JMenuItem> createDynamicActionMenuItemsFor(BrowserController bc, VComponent vcomp, boolean canvasMenu)
	{
    	ActionContext selContext=bc.createSelectionContext(vcomp); 
    		
   		ActionMenuMatcher matcher = getActionMenuMatcher(); 
   		matcher.matchFor(selContext);
    	//
   		
    	// 
    	// create non-submenu items: 
    	// 
    	Vector<JMenuItem> menuItems=new Vector<JMenuItem>(); 
    
    	// ===
    	// toplevel menu items:
    	// ===
    	Vector<JMenuItem> items = createDynamicMenuItemsFor(bc,vcomp,canvasMenu,matcher,null);
    	
    	if ((items!=null) && (items.size()>0))
    		menuItems.addAll(items); 
    	
    	// ===
    	// add all submenus: 
    	// ===
    	
    	StringList subMenus=matcher.getMatchedSubMenus(); 
    	
    	if (subMenus!=null)
    	{
    		for (String subName:subMenus)
    		{
    			Global.infoPrintf(ActionMenu.class," Creating extra submenu:%s\n",subName); 
    			
    			// toplevel menu items: 
    			Vector<JMenuItem> subMenuItems = createDynamicMenuItemsFor(bc,vcomp,canvasMenu,matcher,subName);
    			
    			// create subMenu: 
    			if ((subMenuItems!=null) && (subMenuItems.size()>0))
    			{
    				JMenu subMenu=new JMenu(subName);
    				
    				// no bling bling yet
    				//int menuOptions=matcher.getSubMenuOptions(subName); 
    				
    				for (JMenuItem subMenuItem:subMenuItems)
    				{
    					subMenu.add(subMenuItem);
    				}
    			
    				menuItems.add(subMenu); 
    			}
    			else
    			{
    				Global.warnPrintf(ActionMenu.class,"Warning: No dynamic submenu items for MATCHED submenu:%s\n",subName); 
    			}
    		}
    	}
    	else
    		Global.debugPrintf(ActionMenu.class,"--- No Matched Submenus ---"); 
    	
    	return menuItems; 
	}

	private static Vector<JMenuItem> createDynamicMenuItemsFor(BrowserController bc, VComponent vcomp, boolean canvasMenu,ActionMenuMatcher matcher,String subMenu)
	{
		Vector<ActionMenuMapping> actionItems = matcher.getMatchedMappingsForSubMenu(subMenu); 
		
		if ((actionItems==null) || (actionItems.size()<=0))
		{
			//
			// matcher.getMatchedSubMenus() returned the submenu name, so this means an error:
			//
			
			Global.infoPrintf(ActionMenu.class,"Warning: Matcher didn't return any menu items for matched submenu:%s\n",subMenu); 
			return null;
		}
		
		Vector<JMenuItem> menuItems=new Vector<JMenuItem>(); 
		
		for (ActionMenuMapping actionItem:actionItems)
    	{
    		String name=actionItem.getMenuItemName(); 
    		String actionName=actionItem.getActionName();

			Global.debugPrintf(ActionMenu.class," - Adding :"+"subMenu"+"/"+name+"(actionName())\n");

    		// dynamic action invocation.
    		VRSFactory vrs=actionItem.getVRS(); 
    		String viewerClass=actionItem.getIViewerClassname();
    		
    		ActionCommand cmd=null; 
    		
    		if (vrs!=null)
				cmd=ActionCommand.createDynamicActionCommand(vrs,actionName); 
    		else if (viewerClass!=null)
				cmd=ActionCommand.createDynamicViewerActionCommand(viewerClass,actionName); 
    		else
    		{
    			Global.errorPrintf(ActionMenu.class,"Either VRS or IViewer must be defined for Dynamic Acion:%s\n",actionName);
    			return null; 
    		}
    		
    		JMenuItem mitem;
			menuItems.add(mitem=ActionMenu.createMenuItem(bc, vcomp, name, cmd)); 
 
			String ttxt="";
			
			if (vrs!=null)
				ttxt+=vrs.getName()+":";
			
			ttxt+=actionName;
			
			mitem.setToolTipText(ttxt);
    	}
		
		Global.debugPrintf(ActionMenu.class,"Returning #subMenuItems:"+menuItems.size()+" for subMenu:"+subMenu+"\n"); 
		
		return menuItems; 
	}

	public static ActionMenuMatcher getActionMenuMatcher()
	{
		if (actionMenuMatcher==null)
		{
			Vector<ActionMenuMapping> mappings = UIGlobal.getRegistry().getActionMappings(); 
			Vector<ActionMenuMapping> guiMappings=ViewerRegistry.getRegistry().collectActionMappings();
			
			if (guiMappings!=null)
				mappings.addAll(guiMappings); 
			
			actionMenuMatcher=new ActionMenuMatcher(mappings);
		}
		
		return actionMenuMatcher; 
	}


	@SuppressWarnings("deprecation")
    static ProxyNode getPNodeFrom(VComponent vcomp)
    {
    	 // for additional menu options:
        ProxyNode pnode=null; 
        
        if (vcomp!=null) 
        {
        	VRL vrl=vcomp.getResourceRef().getVRL();
        	pnode=ProxyVRSClient.getInstance().getProxyNodeFactory().getFromCache(vrl); 
        }
        
        return pnode; 
    }
    
    static void addMenuItems(JComponent menu,BrowserController bc,VComponent vcomp,boolean canvasMenu)
    {
        JMenuItem mitem=null;
        // check multiple selection from VContainer: 
        boolean multipleSelections=hasMultipleSelections(vcomp);
        
        // put NEW ontop if canvas menu ! 
        if (canvasMenu==true)
        {
        	menu.add(createNewSubMenu(bc,vcomp));
        	menu.add(new JSeparator());
        }

        // =============================================================
        // Dynamic Action Menu
        // =============================================================
       
        {
        	Vector<JMenuItem> dynMenuItems=createDynamicActionMenuItemsFor(bc,vcomp,canvasMenu); 
		
        	if ((dynMenuItems!=null) && (dynMenuItems.size()>0))
        	{
        		for (JMenuItem item:dynMenuItems)
        		{
        			menu.add(item);
        		}
        		// only add seperator if menu not empty 
        		//menu.add(new JSeparator());        
        	}
        }
        
        // =============================================================
        // With Width -> ...  
        // =============================================================
        
        menu.add(createViewersSubMenu(bc,vcomp));
        menu.add(new JSeparator());
		
        // =============================================================
        // New -> Menu 
        // =============================================================
		
        if (canvasMenu==false)
        {
        	menu.add(createNewSubMenu(bc,vcomp));
        	menu.add(new JSeparator());
        }
        
        // =============================================================
        // Cut,Copy,Paste,etc. 
        // =============================================================
         
        // Cut
        if (canvasMenu==true)
        {
        	if (multipleSelections)
        		menu.add(mitem=createMenuItem(bc,vcomp,"Cut All", ActionCommandType.CLIPBOARD_CUTSELECTION));
        	else
        		menu.add(mitem=createMenuItem(bc,vcomp,"Cut", ActionCommandType.CLIPBOARD_CUTSELECTION));
        }
        else
        {
        	 // Cut 
            menu.add(mitem=createMenuItem(bc,vcomp,"Cut", ActionCommandType.CLIPBOARD_CUTSELECTION)); 
            mitem.setEnabled(canvasMenu==false);  
        }
        
        // Copy
        if (canvasMenu==true)
        {
        	if (multipleSelections)
        		menu.add(mitem=createMenuItem(bc,vcomp,"Copy All", ActionCommandType.CLIPBOARD_COPYSELECTION));
        	else
        		menu.add(mitem=createMenuItem(bc,vcomp,"Copy", ActionCommandType.CLIPBOARD_COPYSELECTION));
        }
        else
        {
        	menu.add(mitem=createMenuItem(bc,vcomp,"Copy", ActionCommandType.CLIPBOARD_COPYSELECTION));
        	mitem.setEnabled(canvasMenu==false);
        }
        // Paste
        menu.add(mitem=createMenuItem(bc,vcomp,"Paste", ActionCommandType.CLIPBOARD_PASTE));
        mitem.setEnabled(bc.haveClipboardSelection());
        String selectionStr=""; 
        
        if (bc.haveClipboardSelection())
        {
        	ResourceRef refs[]=bc.getClipBoardSelection(); 
        	
        	for (ResourceRef ref:refs)
        		if (ref!=null)	
        			selectionStr+=ref.toString()+";\n"; 
        	
            mitem.setToolTipText("Paste:"+selectionStr);
        }

        
        {
            JMenu pasteMenu=new JMenu();
            pasteMenu.setText("Paste Special");
            menu.add(pasteMenu);
            
            // Paste as Link 
            pasteMenu.add(mitem=createMenuItem(bc,vcomp,"Paste as VLink", ActionCommandType.CLIPBOARD_LINKDROP));
            // only enable linkdrop for SINGLE resources ! 
            mitem.setEnabled(bc.haveSingleClipboardSelection());
            
            pasteMenu.setEnabled(bc.haveClipboardSelection());
            pasteMenu.setVisible(bc.haveClipboardSelection());
            if (bc.haveSingleClipboardSelection())
            {
                mitem.setToolTipText("Paste as VLink:"+selectionStr);
            }
            // RFT SCHEDULE
            {
                mitem=new JMenuItem(); 

                mitem.setText("Reliable File Transfer (Paste Here)"); 
                mitem.setActionCommand(ActionCommandType.RFT_COPY_TRANSFER.toString()); 
                mitem.addActionListener(new PopupListener(bc, vcomp));
                mitem.setToolTipText(Messages.TT_SCHEDULE_RFT_COPY); 
                mitem.setEnabled(bc.haveClipboardSelection());
                pasteMenu.add(mitem); 
            }
            // add selection
            {
               JMenu selectionMenu=new JMenu(); 
               pasteMenu.add(selectionMenu); 
               selectionMenu.setText("Current Selection:");
               
               // uri
               mitem=new JMenuItem(); 
               selectionMenu.add(mitem); 
               if (bc.haveClipboardSelection())
               {
                   mitem.setText("uri="+selectionStr);
                   selectionMenu.setEnabled(true);
               }
               else
               {
                   selectionMenu.setEnabled(false);
               }
               mitem.setEnabled(false);
            }
        }
        
        menu.add(mitem=createMenuItem(bc,vcomp,"Create VLink To", ActionCommandType.CREATELINKTO));
        mitem.setEnabled(canvasMenu==false); 
             
        menu.add(new JSeparator());

        boolean deletable=false; 
        boolean renamable=false; 
        
        ProxyNode pnode=getPNodeFrom(vcomp);
        
        if ((pnode!=null) && (pnode.isDeletable()))
            deletable=true;
        
        if ((pnode!=null) && (pnode.isRenamable()))
            renamable=true; 

        if (deletable)
        {
            if (canvasMenu==true)
            {
             	if (multipleSelections)
             		menu.add(mitem=createMenuItem(bc,vcomp,"Delete All", ActionCommandType.DELETE_ALL));
             	else
             		menu.add(mitem=createMenuItem(bc,vcomp,"Delete", ActionCommandType.DELETE));
            }
            else
            {
             	menu.add(mitem=createMenuItem(bc,vcomp,"Delete", ActionCommandType.DELETE));
            }
        }
        
        if ( (renamable==true) && (canvasMenu==false) )
        {
           	menu.add(mitem=createMenuItem(bc,vcomp,"Rename", ActionCommandType.RENAME));
           	mitem.setEnabled(renamable); 
           	//mitem.setEnabled(true); // blocks gui: node.isRenamable());
        }
    }

    /*private static Boolean hasRenameMethod(VComponent vcomp)
	{
    	ProxyNode pnode = getPNodeFrom(vcomp);
    	
    	if (pnode==null)
    		return null; 
    	
    	return pnode.isVRenamble(); 
	}


	private static Boolean hasDeleteMethod(VComponent vcomp, boolean defVal)
	{
		ProxyNode pnode = getPNodeFrom(vcomp);
    	
    	if (pnode==null)
    		return null; 
    	
    	return pnode.isVDeletable(); 
	}*/


	private static boolean hasMultipleSelections(VComponent vcomp)
	{
       	if (vcomp instanceof VContainer)
    	{
    		VContainer vcont=(VContainer)vcomp; 
    		ResourceRef[] refs=vcont.getSelection();
    		
    		if ((refs!=null) && (refs.length>1))
    			return true; 
    	}
       	return false; 
	}

	private static JMenuItem createResourceSubMenu(BrowserController bc, VComponent vcomp)
    {
        JMenu menu = new JMenu(); 
        menu.setText("Resource"); 
        JMenuItem mitem=null;
        
        menu.add(mitem=createMenuItem(bc,vcomp,"Set as Root in ResourceTree", ActionCommandType.SETASRESOURCEROOT));
        mitem.setToolTipText(Messages.TT_SET_AS_ROOT_TEXT);
        
        menu.add(mitem=createMenuItem(bc,vcomp,"edit ACL", ActionCommandType.EDITACL));
        mitem.setToolTipText(Messages.TT_EDIT_ACL);
        
        {
          JMenu vrlMenu=new JMenu(); 
          vrlMenu.setText("VLR:");
          menu.add(vrlMenu);
        
          vrlMenu.add(mitem=createMenuItem(bc,vcomp,""+vcomp.getResourceRef(),ActionCommandType.CLIPBOARD_COPYSELECTION)); 
          mitem.setToolTipText("Copy this location"); 
          mitem.setToolTipText(Messages.TT_COPYLOCATION_TEXT);
        }
        
        return menu;
    }

	

}
