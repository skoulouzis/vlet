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
 * $Id: ActionCommandType.java,v 1.6 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.actions;



/**
 * Enum Class ActionCommandType. 
 * List of Action Command Types. 
 * 
 * @author P.T. de Boer
 */

public enum ActionCommandType // implements Action
{
    // Mouse Actions:  
    SELECTIONCLICK("Selection click"), // single click 
    ACTIONCLICK("Action click"),   // single click or double click 
    // Enum list: 
    EDIT("Edit"),
    CREATE("Create"),
    DELETE("Delete"),
    /** Explicit Delete ALL to indicate mutliple selection ! */ 
    DELETE_ALL("Delete All"),
    FORCE_DELETE("Force Delete"),
    VIEW("Open view"),
    OPEN("Open"),
    RENAME("rename"),
    // GLOBALCOPY,GLOBALPASTE, GLOBALCUT,
    CLIPBOARD_CUTSELECTION("Cut"),
    CLIPBOARD_PASTE("Paste"),
    CLIPBOARD_COPYSELECTION("Copy"),
    CLIPBOARD_COPYDROP("CopyDrop clipboard"), 
    CLIPBOARD_MOVEDROP("MoveDrop clipboard"),
    CLIPBOARD_LINKDROP("LinkDrop clipboard"),
    OPENINTAB("Open in tab"),
    NEWWINDOW("New window"),
    OPENINNEWWINDOW("Open in new window"),
    REFRESH("Refresh"), 
    REFRESHALL("Refresh all"),
    VIEWITEM("View item"),
    PROXYDIALOG("Proxy dialog"), 
    COGINITPROXY("Init grid proxy"), 
    EXITBROWSERS("Exit all browsers"),
    HELP("Help"),
    
    BROWSEUP("Browse up"),
    BROWSEFORWARD("Browse forward"), 
    BROWSEBACK("Browse back"), 
    LOCATIONBAR_CHANGED("Location bar changed"),
    LOCATIONBAR_EDITED("Location bar edited"),
    VIEWASTABLE("View as table"), 
    VIEWASICONS("View as icons"),
    VIEWASICONLIST("View as icon list"),
    DEBUG_SHOW_TASKS, 
    STARTVIEWER("Start viewer"),
    DYNAMIC_VIEWER_ACTION("Dynamic Viewer Action"),
    STARTTOOL("Start tool"),
    CLOSEBROWSER("Close browser"),
     
    LOOKANDFEEL("Change look and feel"), 
    MASTERSTOP("Stop all actions"), 
    DND_COPYDROPNODES("Dragged CopyDrop nodes"), 
    DND_MOVEDROPNODES("Dragged MoveDop nodes"), 
    DND_LINKDROPNODES("Dragged LinkDrop node"),
    SAVEPREFERENCES("Save preferences"), 
    CREATELINKTO("Create link to"),
    EDITLINK("Edit link"),
    EDITPROPERTIES("Edit properties"),
    //LINKPROPERTIES("link properties"),
    //SHOWPROPERTIES("Properties"),
    SETASRESOURCEROOT("Set as root in resourcetree window"), 
    GLOBAL_SET_SINGLE_ACTION_CLICK("Global settings: set single click action"),
    GLOBAL_SHOW_LOG_WINDOW("Global settings: show log window"),
    GLOBAL_FILTER_HIDDEN_FILES("Global preference: show hidden resources"),
    GLOBAL_SHOW_RESOURCE_TREE("Global preference:  show resource tree"),
    FILTER_HIDDEN_FILES("Window preference: toggle show hidden resources"),
    EDITACL("Edit ACL list properties"),
    // ACTION_METHOD("Action method"),
    DYNAMIC_ACTION("Dynamic Action"),
    
    STARTVLTERM("Start VLTerm"),
    NONE("None"), SHOW_ALL_WINDOWS, 
    RFT_COPY_TRANSFER("RFT Copy Transfer"), 
    SAVE_LOOKANDFEEL, 
    START_JGRIDSTART("Start JGridStart"),   
    ;
    
    /** human readable action instead of UPPERCASE ENUM STRING... */
    String actionString=null;
    
    private ActionCommandType(String actionstr)
    {
        actionString=actionstr; 
    }

    private ActionCommandType()
    {
        actionString=this.toString();  
    }

    /** Returns human readable description */ 
    public String getActionMessage()
    {
        return actionString; 
    }

//	public void addPropertyChangeListener(PropertyChangeListener listener)
//	{
//		
//	}
//
//	public Object getValue(String key) 
//	{
//		return null;
//	}
//
//	public boolean isEnabled() 
//	{
//		return false;
//	}
//
//	public void putValue(String key, Object value)
//	{
//		
//	}
//
//	public void removePropertyChangeListener(PropertyChangeListener listener)
//	{
//		
//	}
//
//	public void setEnabled(boolean b) 
//	{
//	}
//
//	public void actionPerformed(ActionEvent e) 
//	{
//		
//	}
   
}
