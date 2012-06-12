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
 * $Id: Messages.java,v 1.8 2011-12-07 15:34:31 ptdeboer Exp $  
 * $Date: 2011-12-07 15:34:31 $
 */ 
// source: 

package nl.uva.vlet.gui;

import nl.uva.vlet.Global;

/**
 *  Class to hold the String Messages used in the VBrowser.
 *  Not all messages have been moved to this location yet.
 */ 

public class Messages
{
    // Questions 
    
	public static  String Q_do_you_want_to_recursive_delete_resource 
    	= "Delete resource and contents of";

    public static  String Q_overwrite_existing_resource 
        = "Resource exists. Overwrite existing resource ?";

    public static  String M_gridproxy_is_valid_configure_cog_might_not_be_needed 
        = "You appear to have a valid proxy.\nConfiguring the certificates might not be needed.\n";


    // Messages 
    public static  String M_could_not_add_resource 
        = "Could not add resource.";

    public static  String M_resource_cannot_be_added_to_itself = 
        "One or more resources can not be added to itself";

    public static  String M_resource_cannot_be_added_to_itself_or_its_childs = 
        "Resource can not be added to itself or to one of it's children."
        +"(Source is parent of destination).";

    // (Real) Error Messages: 
    public static  String E_renameble_interface_not_implemented = 
        "This resource cannot be renamed or doesn't implement the rename method";

    public static  String Q_cog_init_wizard_will_exit_proceed = 
        "If you continue, the VBrowser will exit after you have configured the certificates.\n"
        + "Proceed?";

    public static  String M_Resource_bigger_than =  
        "The resource you want to view is larger than:";

    public static  String Q_Do_you_want_to_continue = 
        "Do you want to continue?"; 
    
    // Misc Texts 
    public static String aboutText=
         "<html><body><a name=top>"
        +"<center><h1> About the VL-e Toolkit </h1>"
        +"("+Global.getVersion()+")<br>"
        +"</center>"
        +"<p>"
        +"<center>The VBrowser is part of the VL-e Toolkit.<br>" 
        +"This Toolkit is currently under construction.<br><p>"
        +"This toolkit is licensed under the Apache License, Version 2.0<br>"
        +"<p>"
        +" <table>"
        +"  <tr><td> For more info mailto:</td>"  + "<td> vlet-develop@lists.sourceforge.net</td></tr>"  
        +"  <tr><td> See also:</td>"              + "<td> <a href=http://www.vl-e.nl/vbrowser>www.vl-e.nl/vbrowser</a> </td></tr>"
        +"  <tr><td></td> <td></td> </tr>" 
        +"  <tr><td> This about text:</td>"       + "<td> <a href=\"about:/\">about:/</a> </td></tr>"
        +"  <tr><td> Plugin information:</td>"    + "<td> <a href=\"about:/plugins\">about:/plugins</a> </td></tr>" 
        +" </table>"
        +"</center>"
        +"<p>"
        +"<center>"
        +"  <table>"
        +"    <tr><td align=center width=300 colspan=2><h3>Developers</h3></td></tr>"
        +"    <tr height=16><td width=50></td><td></td></tr>"
        +"    <tr><td width=300 colspan=2>Lead Programmer:</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td width=50></td> <td>Piter T. de Boer</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td colspan=2>Assistent Programmers:</td></tr>"
        +"    <tr><td></td><td>Spiros Koulouzis (SRM/LFC/Cloud)</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td colspan=2>Contributions by:</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td></td><td>Martin Stam (vlemed)</td></tr>"
        +"    <tr><td></td><td>Kamel Boulebiar (LFC/vlemed)</td></tr>"
        +"    <tr><td></td><td>Ketan C. Maheshwari (RFTS/Grid Services)</td></tr>"
        +"    <tr><td></td><td>Kasper van den Berg (AID) </td></tr>"
        +"    <tr><td></td><td>Abdullah Z. &Ouml;zsoy (VTK)</td></tr>"
        +"    <tr><td></td><td>Bart Heupers (SRB example)</td></tr>"
        +"    <tr><td></td><td>Marco Konijnenberg (SFTP example)</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"  </table>"
        +" </center>" 
        +" <center>"
        +"  <table> "
        +"    <tr><td align=center width=300 colspan=2><h3>(L)GPL Third party plugin software.</h3></td></tr>"
        +"    <tr height=16><td width=50></td><td></td></tr>"
        +"    <tr><td colspan=2>Lobo/Cobra HTML rendering toolkit.</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.lobobrowser.org/\">www.lobobrowser.org</a></td></tr>"
        +"    <tr><td></td><td>(GPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td colspan=2>jPedal PDF rendering toolkit</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.jpedal.org\">www.jpedal.org</a></td></tr>"
        +"    <tr><td></td><td>(GPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td colspan=2>Windows Bootstrapper and XTerm emulator: Piter.nl</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.piter.nl/java\">www.piter.nl/java</a></td></tr>"
        +"    <tr><td></td><td>(LGPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
 
        +"  </table>"
        +"</center>"
        +"</body></html>";
 

    //public static  String _I_ACL_INFO_TEXT = "Edit ACL entries. \n";

    // ========================================================================
    // Menu ToolTips 
    // ========================================================================
    
    public static String TT_SET_AS_ROOT_TEXT="Make this location the toplevel location in the Resource Tree Window";
    
    public static String TT_COPYLOCATION_TEXT = "Copy this location to the copy buffer";

    public static String TT_EDIT_ACL = "Edit the Access Control List properties";

    public static String TT_SCHEDULE_RFT_COPY = "Copy file using RFT";

    public static String TT_CLICK_ON_VRL = "Click on the URI to open the location";

    public static String TT_VIEW_AS_ICONS = "View contents as icons (icon view)";
    
    public static String TT_VIEW_AS_TABLE = "View contents as detailed list (table view)";

    public static String TT_TOGGLE_RESOURCE_TREE_VIEW = "Toggle visibility of Resource Tree Panel";
        
    public static String TT_TOGGLE_LOG_VIEW = "Toggle visibility of log Panel";
    
    public static String TT_TOGGLE_HIDDEN_FILE_FILTER = "Toggle hidden file filter";

    // =============
    // Resource Editor Messages  
    // ==============
	
	public static String M_can_not_create_new_server_config_invalid_port =  "Can not create new Server Configurarion. Invalid port number:%d"; 

	public static String D_view_or_edit_properties = "View properties of this resource";
	         
	public static String D_edit_link_properties = "Edit Link properties.";

	public static String D_view_resource_folder_properties = "Viewer Resource Folder properties.";

	public static String D_edit_server_configuration = "Edit Server Configuration. Specify location properties first,"
			+"then edit server configarution.";

}
