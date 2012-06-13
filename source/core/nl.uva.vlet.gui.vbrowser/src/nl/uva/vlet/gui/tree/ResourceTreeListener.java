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
 * $Id: ResourceTreeListener.java,v 1.5 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPopupMenu;

import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.data.ResourceRef;


/**
 * Implementation of TreeNodeClickListener. 
 * 
 * Also listenes for drag events as these must be handled in a different way
 * in JTrees.
 * 
 * Default action: Redirect action to BrowserController.
 *  
 * @author P.T. de Boer  
 */
public class ResourceTreeListener implements MouseListener,MouseMotionListener, 
	FocusListener// , DragSourceListener 
{
    ResourceTree resourceTree = null;
    MasterBrowser browserController=null; 
    int browserid=-1;
    
    public ResourceTreeListener(MasterBrowser bc,ResourceTree resourceTree)
    {
        this.resourceTree=resourceTree;  
        this.browserController=bc;
    }

    public void mousePressed(MouseEvent e)
    {
        //Debug("mousePressed"+e);
        
        ResourceTreeNode rtnode=resourceTree.getNodeUnderPoint(e.getPoint()); 
        boolean canvasclick=false; 
    
        if (rtnode==null)
        {
            // no node under mouse click
            rtnode=resourceTree.getRootNode();
            canvasclick=true;
        }
        
      //  boolean shift=((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) !=0);
        
        boolean combine= ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK ) !=0);
        boolean shift=((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);

        // Right click on node without modifiers is unselects all! 
        if (canvasclick==false)
        {
            if ((combine==false) && (shift==false) && GuiSettings.isPopupTrigger(e) )
            {
                // clear selection BEFORE menu popup!
                this.resourceTree.clearSelection(); 
            }
        }
        
        ResourceRef[] refs = this.resourceTree.getSelection();
        
        // check whether more then one nodes are selected
        // If two nodes are selected use Canvas Menu for Multiple Selections ! 
        if ( (refs!=null) && (refs.length>1) && (combine==true))
       	{
        	canvasclick=true; 
       	}


        // right click -> Popup
        if (GuiSettings.isPopupTrigger(e))
        {
             if (canvasclick==false)
             {
                 JPopupMenu menu=browserController.getActionMenuFor(rtnode);
                 menu.show((Component)e.getSource(),e.getX(),e.getY());
             }
             else
             {
                 JPopupMenu menu=browserController.getActionMenuFor(this.resourceTree);
                 
                 if (menu!=null)
                	 menu.show((Component)e.getSource(),e.getX(),e.getY());
             }
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        //Debug("mouseClicked"+e);
        
        ResourceTreeNode rtnode = resourceTree.getNodeUnderPoint(e.getPoint());
        
        boolean canvasClick=false; 
        
        if (rtnode==null)
        {
           canvasClick=true; 
        }
        
        boolean shift=((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) !=0);
        boolean combine= ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK ) !=0); 
        
        // When pressed down, no selection is made
        // When clicked, selection is made: 
    	if  (GuiSettings.isSelection(e))
    	{
    		if (canvasClick)
    		{
    			if (combine==false)
    			{
    				this.resourceTree.clearSelection();
    				// unselect !
    				browserController.performSelection(null);
    			}
    			else
    			{
    				// ignore: 
    			}
        	}
        	else
        	{
        		// handled mouseClicked if NO multi combo click !  
        		if ((combine==false) && (shift==false))
        			browserController.performSelection(rtnode);
        		else
        		{
        			// already done by Jtree itself: 
        			 // consume:
        			//TreePath path = rtnode.createTreePath(); 
        			//this.resourceTree.addSelectionPath(path);
        		}
        	}
        	 
        }
    	
    	if ((combine==false) && (shift==false))
    	{
    	    if (GuiSettings.isAction(e)) 
    	    {
    	        // redirect action to browserController:
    	        // ActionCommand cmd=new ActionCommand(ActionCommandType.ACTIONCLICK); 
    	        browserController.performAction(rtnode);                   
    	    }
    	}
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	//Debug("mouseReleased:"+e);  
    	this.resourceTree.stopAutoScroller(); 
    }

    public void mouseEntered(MouseEvent e)
    {
    	//ResourceTreeNode comp = this.resourceTree.getNodeUnderPoint(e.getPoint()); 
        //this.resourceTree.repaint(); 
    }
    
    public void mouseExited(MouseEvent e)
    {
    	this.resourceTree.stopAutoScroller();
        resourceTree.setMouseOverPoint(null);  
    }
    
    /** Auto Scroll */ 
    public void mouseDragged(MouseEvent e)
    {
    	//Debug("mouseDragged:"+e); 
    	
    	Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
    	this.resourceTree.scrollRectToVisible(r);
    }

    public void mouseMoved(MouseEvent e)
    {
    	//Debug("mouseMoved:"+e);
    	
        resourceTree.setMouseOverPoint(e.getPoint()); 
    	
 //    	
//    	ResourceTreeNode node = this.resourceTree.getNodeUnderPoint(e.getPoint());
//    	resourceTree.setFocusNode(node); 
//    	
    }

	public void focusGained(FocusEvent e)
	{
		//this.resourceTree.setBackground(new Color(255,128,128)); 
	}

	public void focusLost(FocusEvent e)
	{
		//this.resourceTree.setBackground(new Color(128,128,128)); 
		this.resourceTree.stopAutoScroller();
		resourceTree.setMouseOverPoint(null);  
		//this.resourceTree.clearSelection(); 
	}
	
/*
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
	}

	public void dragEnter(DragSourceDragEvent dsde)
	{
	}

	public void dragExit(DragSourceEvent dse)
	{
	}

	public void dragOver(DragSourceDragEvent dsde)
	{
	}

	public void dropActionChanged(DragSourceDragEvent dsde)
	{
	}
*/

}
