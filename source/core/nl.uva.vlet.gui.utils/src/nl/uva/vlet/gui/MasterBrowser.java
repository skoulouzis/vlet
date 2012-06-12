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
 * $Id: MasterBrowser.java,v 1.4 2011-06-10 10:18:03 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:03 $
 */ 
// source: 

package nl.uva.vlet.gui;

import javax.swing.JPopupMenu;

import nl.uva.vlet.gui.dnd.DropAction;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.tasks.ITaskSource;
import nl.uva.vlet.vrl.VRL;

/** 
 * Combined Interface for MasterBrowser and ITaskSource.  
 * 
 * This is mainly an interface for the BrowserController,
 * but created to shield other components from dependency
 * of the BrowserController. 
 * Other mini browsers might implement this interface like the FileSelector.
 * 
 * @author P.T. de Boer
 */
public interface MasterBrowser extends HyperLinkListener, ITaskSource
{
	/** Get current viewfilter */ 
    ViewModel getViewModel();

    /** Notify Drag'n Drop action */ 
    void performDragAndDrop(DropAction action);

    /** Get action menu for specified location */ 
    JPopupMenu getActionMenuFor(VComponent comp);

    /** Selection Event, if resource==null this means an UNSELECT! */ 
    void performSelection(VComponent comp);
    
    /**
     * Perform default Action (user double click's on resource) 
     * What the default actions is, depends on the Selected Resource. 
     * 
     */ 
    void performAction(VComponent comp);
    
    /**
     * Start New (Master) Browser Window if TAB mode is ON
     * the new window will appear as new Tab. 
     */ 
    void startNewWindow(VRL vrl); 
   
}
