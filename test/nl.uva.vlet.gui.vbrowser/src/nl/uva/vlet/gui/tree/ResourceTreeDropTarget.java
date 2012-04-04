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
 * $Id: ResourceTreeDropTarget.java,v 1.4 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.dnd.VTransferData;
import nl.uva.vlet.gui.dnd.VTransferHandler;

/**
 * Swing/AWT Compatible DnD Support. 
 * The Swing DnD isn't completely implemented.
 *  
 * Currently the MyTransferHandler is broken and the DropTarget.drop() methods 
 * do the action. 
 * 
 * @author P.T. de Boer. 
 *
 */
public class ResourceTreeDropTarget extends DropTarget
{
	ResourceTree resourceTree; 
	
	public ResourceTreeDropTarget(ResourceTree tree)
	{
		this.resourceTree=tree; 
		setComponent(tree);
	}


	public void dragOver(DropTargetDragEvent dtde) 
	{
		Debug("dragOver:" + dtde);

		Component source = dtde.getDropTargetContext().getComponent();

		if ((source instanceof ResourceTree) == false) 
		{
			Global.errorPrintf(this, "Drag Source object not a ResourceTree!!!\n");
			return;
		}

		ResourceTree tree=(ResourceTree)source; 
		Point p = dtde.getLocation();
		ResourceTreeNode node=getRTNode(source,p);
		tree.scrollTo(p); // autoscroll
		tree.setMouseOverPoint(p);
		//tree.setFocusNode(node); 	

 
		if (node == null)
		{
			// === No ResourceTreeNode under pointer ! ===
			// Don't reject: once a reject is issued the whole resourceTree is
			// considered Rejected ! (This is a potentiel CanvasDrop) 
			// dtde.rejectDrag();
			
			// Do NOT unselect selection paths:: tree.setSelectionPath(null);
			return;
		}

		TreeNode[] treeNodes = node.getPath();

		TreePath treePath = new TreePath(treeNodes);
		// make sure selectio is visible 
		//tree.setSelectionPath(treePath);
		
		// accept in dragEnter
		// dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
		
	}
	
	public void dragEnter(DropTargetDragEvent dtde)
    {
        Debug("dragEnter:" + dtde);

        Component source = dtde.getDropTargetContext().getComponent();
    	Point p = dtde.getLocation();
		ResourceTreeNode node=getRTNode(source,p);  
		
        dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE); 
    }
      
	/** Drop On Node ! */ 
	
    public void drop(DropTargetDropEvent dtde)
    {
        // Global.debugPrintln(this,"Dropping:"+dtde); 
    	
    	DropTargetContext dtc = dtde.getDropTargetContext();
    	Component comp = dtc.getComponent();
    	Point p=dtde.getLocation();
    	Transferable data = dtde.getTransferable();
    	
    	ResourceTreeNode rtnode = getRTNode(comp,p); 
    	
    	// check dropped data: 
    	if (VTransferData.canConvertToVRLs(data))
    	{
    		 // I: accept drop: 
            dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);

        	// supply ResourceTree as (J)Component but TreeNode as VComponent ! 
        	VTransferHandler.getDefault().interActiveDrop(comp,rtnode,p,data);
            // III: complete the drag ! 
            dtde.getDropTargetContext().dropComplete(true);
    	}
    	else
    		dtde.rejectDrop(); 
    	
    }
    
    public void dropActionChanged(DropTargetDropEvent dtde)
    {
    	Debug("dropActionChanged:"+dtde); 
    }
    
    private void Debug(String msg)
	{
		Global.debugPrintf(this,"%s\n",msg); 
		//Global.errorPrintln(this,msg); 
	}


	static ResourceTreeNode getRTNode(Component comp,Point p)
	{
		if ((comp instanceof ResourceTree) == false) 
		{
			Global.errorPrintf(ResourceTreeDropTarget.class, "Source object not a ResourceTree!!!\n");
			return null;
		}
		
		ResourceTree tree = ((ResourceTree) comp);

		ResourceTreeNode node = tree.getNodeUnderPoint(p);
		return node; 
	}
    
}
