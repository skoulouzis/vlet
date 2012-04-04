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
 * $Id: NodeDropTarget.java,v 1.3 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.dnd.VTransferData;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.view.VComponent;

/**
 * 
 * NodeDropTargetListener, handles 'drop' on Node Components. 
 * Install this DropTargetListener to support drops on the component. 
 * 
 * Swing/AWT Compatible DnD Support. 
 * The Swing DnD isn't completely implemented.
 * Currently the MyTransferHandler is broken for non VComponents and the DropTarget.drop() methods 
 * do the action. 
 * 
 * @author P.T. de Boer. 
 *
 */

public  class NodeDropTarget extends DropTarget //, DragSourceListener,
{
    private static final long serialVersionUID = 1985854014807809151L;

    public NodeDropTarget(Component comp) 
    {
		this.setComponent(comp); 
	}

	public void dragEnter(DropTargetDragEvent dtde)
    {
        Global.debugPrintf(this, "dragEnter:%s\n",dtde);

        Component source = dtde.getDropTargetContext().getComponent(); 
        
        if (source instanceof VComponent)
        {
            //  update GUI: select the JButton
            //((VComponent)source).setMouseOver(true);
            // one of my component, so I accept this drop:
            dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
            //DropTargetContext dtc = dtde.getDropTargetContext();
            
            // Accept all DnD actions: 
            //dtde.acceptDrag(dtde.getSourceActions());
            //showUnderDrag(true);
            
        }
        else
        {
        	
        }
    }
    
    public void dragOver(DropTargetDragEvent dtde)
    {
    	Global.debugPrintf(this, "dragOver:%s\n",dtde);
        Component source = dtde.getDropTargetContext().getComponent();
        
        source.requestFocus(); 
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        Global.debugPrintf(this, "dropActionChanged:%s\n",dtde);
    }

    public void dragExit(DropTargetEvent dte)
    {
        //Component source = dte.getDropTargetContext().getComponent(); 
        Global.debugPrintf(this, "dragExit:%s\n",dte);
    }

    public void drop(DropTargetDropEvent dtde)
    {
    	Global.debugPrintf(this,"drop:%s\n",dtde);
    	
    	DropTargetContext dtc = dtde.getDropTargetContext();
    	Component comp = dtc.getComponent();
    	Point p=dtde.getLocation();
    	
        // II : get data: 
    	Transferable data = dtde.getTransferable();
    	
    	// check dropped data: 
    	if (VTransferData.canConvertToVRLs(data))
    	{
    		// I: accept drop: 
           dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);

            // Implementation now in VTransferHandler: 
            VTransferHandler.getDefault().interActiveDrop(comp,p,data); 
            
            // III: complete the drag ! 
            dtde.getDropTargetContext().dropComplete(true);
    	}
    	else
    	{
    		dtde.rejectDrop(); 
    	}
    	
    }
   
}
