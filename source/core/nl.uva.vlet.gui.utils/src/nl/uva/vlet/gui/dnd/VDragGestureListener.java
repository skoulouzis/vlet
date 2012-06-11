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
 * $Id: VDragGestureListener.java,v 1.3 2011-04-18 12:27:14 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:14 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.Cursor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.uva.vlet.Global;

/**
 * Drag Gesture listnener which receives a Drag Recognized event 
 * from the Global Drag Gesture Recoginizer.
 */
public class VDragGestureListener implements DragGestureListener
{
    public void dragGestureRecognized(DragGestureEvent dge)
    {
    	// DnD Stuff: 
        Global.debugPrintln(this, "dragGestureRecognized:" + dge);
        int action=dge.getDragAction(); 
        // Use DragSource ? 
    
        InputEvent trigger=dge.getTriggerEvent();
        JComponent comp= (JComponent)dge.getComponent();
        
        Global.debugPrintln(this, "source comp="+comp); 
        
       	// Swing way to initiate a Drag: 
       	TransferHandler trans = comp.getTransferHandler(); 
       	// default to copy:
      	trans.exportAsDrag(comp,trigger, DnDConstants.ACTION_COPY);
     }
    
    private Cursor selectCursor(int action)
    {
        return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop
                : DragSource.DefaultCopyDrop;
    }
    
}
