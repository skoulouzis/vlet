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
 * $Id: ActionMenuConstants.java,v 1.3 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.actions;

public class ActionMenuConstants
{
    // =======================================================================
    // Selection Modes. Can be logically combined (OR'd)
    // =======================================================================

    /** any number or selection may be done (0 or more) */
    public static final int SELECTION_DONT_CARE = 0x0000;

    /** Explicit One Selection may be done (==1) */
    public static final int SELECTION_ONE = 0x0001 << 1;

    /** Explicit Two Selections may be done (==2) */
    public static final int SELECTION_TWO = 0x0001 << 2;

    // /** Excplit 3 items may be selected (==3)*/
    // static final int SELECTION_THREE =0x0001<<3;

    // /** Excplit 4 items may be selected (==4) */
    // static final int SELECTION_FOUR =0x0001<<4;

    /** Excplit two or more (>=2) */
    public static final int SELECTION_TWO_OR_MORE = 0x0001 << 5;

    /** Excplit one or more (>=1) */
    public static final int SELECTION_ONE_OR_MORE = SELECTION_ONE
            | SELECTION_TWO_OR_MORE;

    /** Explicit No Selection may be done (==0) */
    public static final int SELECTION_NONE = 0x0001 << 10;

    /** Allow Dragged Selection as input */
    public static final int SELECTION_TYPE_DRAG_AND_DROP = 0x0001 << 11;

    /** Allow Clipboard (Copy/Cut) selections */
    public static final int SELECTION_TYPE_CLIPBOARD = 0x0001 << 12;

    /** Allow both of the above */
    public static final int SELECTION_TYPE_ALL = SELECTION_TYPE_DRAG_AND_DROP
            & SELECTION_TYPE_CLIPBOARD;

    //
    // Default Actions : Recognised by the VBrowser.
    //

    public static final String DEFAULT_COPYDROP = "DefaultCopyDrop";

    public static final String DEFAULT_MOVEDROP = "DefaultMoveDrop";

    public static final String DEFAULT_LINKDROP = "DefaultLinkDrop";

    public static final String DEFAULT_COPYSELECTION = "DefaultCopySelection";

    public static final String DEFAULT_CUTSELECTION = "DefaultCutSelection";

    public static final String DEFAULT_COPYNPASTE = "DefaultCopyAndPaste";

    public static final String DEFAULT_CUTNPASTE = "DefaultCutAndPaste";

    public static final String DEFAULT_LINKPASTE = "DefaultLinkPaste";

    // =======================================================================
    // Menu Options
    // =======================================================================

    // Menu Options can be logically combined (OR'd)

    // Show menu item is default, alternatives
    // are GREY_OUT and DONT_SHOW.
    // public static final int MENU_INACTIVE_NONE = 0x0000;
    //
    // public static final int MENU_INACTIVE_SHOW = 0x0001<<1;

    public static final int MENU_INACTIVE_GREY_OUT = 0x0001 << 2;

    public static final int MENU_INACTIVE_DONT_SHOW = 0x0001 << 3;

    /** Apear in Item Action menu (== right/action click ON a node) */
    public static final int MENU_ITEM_ACTION = 0x0001 << 4;

    /** Appear in Canvas Menu (== right/action click on the container canvas) */
    public static final int MENU_CANVAS_ACTION = 0x0001 << 5;

    /** Appear ONLY in Drag and Drop mini menu ! */
    public static final int MENU_DRAG_AND_DROP_MINI_MENU = 0x0001 << 6;

    // currently determined by subMenuName !
    // /** Option to show menu is own submenu */
    // public static final int MENU_IS_SUBMENU = 0x0001<<7;

    /** if MENU_IS_SUBMENU=FALSE, place menus between seperators */
    public static final int MENU_USE_SEPERATOR = 0x0001 << 8;

    // /** Show SubMenu Name above list of actions or not */
    // public static final int MENU_SHOW_SUBMENU_NAME = 0x0001<<9;

}
