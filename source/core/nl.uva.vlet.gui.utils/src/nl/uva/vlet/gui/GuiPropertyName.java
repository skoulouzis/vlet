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
 * $Id: GuiPropertyName.java,v 1.3 2011-04-18 12:27:13 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:13 $
 */ 
// source: 

package nl.uva.vlet.gui;

import java.awt.event.MouseEvent;

import nl.uva.vlet.data.VAttributeType;

/**
 * Enum class of the PERSISTENT property gui names&types 
 * The triple exists of: 
 *  - type : VAttribute type (needed to restore the attribute)<br> 
 *  - name : persistant property name ! (may not change) <br>
 *  - message : Human readable information about the property<br>
 *  - default value : 
 */
public enum GuiPropertyName
{
    MOUSE_SELECTION_BUTTON(VAttributeType.INT,"mouseSelectionButton","Mouse selection button",MouseEvent.BUTTON1),  
    MOUSE_POPUP_BUTTON(VAttributeType.INT,"mousePopupButton","Mouse menu pop-up button",MouseEvent.BUTTON3),     
    MOUSE_ALT_BUTTON(VAttributeType.INT,"mouseAltButton","Mouse Alt (right) button",MouseEvent.BUTTON3),      
    SINGLE_CLICK_ACTION(VAttributeType.BOOLEAN,"singleClickAction","Single click action",true), 
    GLOBAL_SHOW_LOG_WINDOW(VAttributeType.BOOLEAN,"showLogWindow","Default show log window",false), 
    GLOBAL_FILTER_HIDDEN_FILES(VAttributeType.BOOLEAN,"filterHiddenFiles","Default filter hidden files and directories",true),
    GLOBAL_SHOW_RESOURCE_TREE(VAttributeType.BOOLEAN,"showResourceTree","Default show resource tree",true),
    GLOBAL_USE_WINDOWS_ICONS(VAttributeType.BOOLEAN,"useWindowsIcons","Use windows icons",false),
    GLOBAL_LOOK_AND_FEEL(VAttributeType.STRING,"defaultLookAndFeel","Platform Look and Feel",null)
    ;
    
    /** Attribute type is used when getting/setting VAttributes */
    private VAttributeType type; 
    private String message; 
    private String name; // Property name used for storage/Attribute type 
    private String defaultValue; 
    
    private GuiPropertyName(VAttributeType type,String name,String messagestr, int defaultVal)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=""+defaultVal; 
    }
    
    private GuiPropertyName(VAttributeType type,String name,String messagestr, boolean defaultVal)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=""+defaultVal; 
    }
    
    private GuiPropertyName(VAttributeType type,String name,String messagestr, String valstr)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=valstr;  
    }
    
    public String getName()
    {
        return name; 
    }
    
    public String getMessage()
    {
        return message; 
    }
    
    public String getDefaultValue()
    {
        return defaultValue; 
    }
    
    public VAttributeType getType()
    {
        return type; 
    }

    public String getDefault()
    {
        return defaultValue;
    }
}