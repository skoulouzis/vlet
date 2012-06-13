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
 * $Id: LabelIconListener.java,v 1.7 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPopupMenu;

import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.vrl.VRL;

/** 
 * ButtonIcon Listener: handles mouse event on ButtonIcons. 
 * This handler also detects the drag. 
 * 
 * @author P.T. de Boer
 */

public class LabelIconListener implements ActionListener,
	MouseListener,MouseMotionListener, KeyListener
{
    MasterBrowser browserController=null; 
    
    MouseEvent firstMouseEvent = null;

    private IconsPanel iconsPanel;
    
    LabelIconListener(IconsPanel iconsPanel,MasterBrowser bc)
    {
       this.browserController=bc;
       this.iconsPanel=iconsPanel; 
    }
    
    public void actionPerformed(ActionEvent e)
    {
    	//System.err.println("actionPerformed:"+e); 
        /* Disabled for now 
            
        debug.println("Action:"+e);
        Component comp=(Component)e.getSource();
        String name=comp.getName();
        
        /// Node MUST be in node hash  
        
        ProxyTNode node=ProxyTNode.hashGet(name);
        
        // Customizable Default Action ? 
        
        if (GuiSettings.current.singleClickAction==true)
        {
            
            browserController.performViewNode(node,true);
        }
        else
        {
            browserController.setSelectionNode(node);
        }
        */ 
    }
    
    VRL getVRLFrom(Component comp)
    {
        VComponent vcomp=getVComponent(comp); 
         
        if (vcomp!=null) 
        {
            // event origination from JButton 
            return vcomp.getResourceRef().getVRL(); 
        }
        else
        {
            //Global.errorPrintf(this,"Internal Error:event NOT from VComponent ***");
            return null;  
        }
    }
    
    public void mouseClicked(MouseEvent e)
    {
        // Debug("Action:"+e);
        Component comp=(Component)e.getSource(); 
        VComponent vcomp=(VComponent)comp;
        
        // follow mouse: 
        //comp.requestFocus();  
        
        VRL vrl=getVRLFrom(comp); 
        
        if (vrl==null) 
            return; 

        //Don't bother to drag if there is no image.
        //if (image == null) return;

        //boolean combine= ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK )!=0); 
        //boolean shift = ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)!=0); 
        
        // isAction also block for SHIFT&CTRL modifers ! 
        if (GuiSettings.isAction(e)) 
        {
            // action click is selected click:
            //iconsPanel.setSelected(vrl,combine,true); 
            //MasterBrowser perform Default Action 
            browserController.performAction(vcomp);  
        }
     
    }

    public void mousePressed(MouseEvent e)
    {
        Component comp=(Component)e.getSource(); 
        VComponent vcomp=(VComponent)comp;
        
        // follow mouse: 
        comp.requestFocus();  
        
        VRL vrl=getVRLFrom(comp); 
        
        if (vrl==null) 
            return; 

        //Don't bother to drag if there is no image.
        //if (image == null) return;

        boolean combine= ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK )!=0); 
        boolean shift = ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK)!=0); 
        
        // isAction also blocks for SHIFT&CTRL modifers ! 
//        if (GuiSettings.isAction(e)) 
//        {
//            // Nothing during mouse Pressed!
//            // action click is selected click:
//            //iconsPanel.setSelected(vrl,combine,true); 
//            //MasterBrowser perform Default Action 
//            //browserController.performAction(vcomp);
//            
//        }
//        else
        if (GuiSettings.isSelection(e)) 
        {
            if (shift==true)
            {
                iconsPanel.selectRange(vrl,false); 
            }
            else
            {
                // select:
                iconsPanel.toggleSelection(vrl,combine); 
                browserController.performSelection(vcomp);
            }
        }
        else  if (GuiSettings.isPopupTrigger(e)) 
        {
        	// check modifiers ! 
        	if ((combine==false) && (shift==false))
        	{
        		// unselect before action menu popup !
        		iconsPanel.selectAll(false); // unselect previous
        		browserController.performSelection(null); 
        	}
        	
    		//NOT  make sure resource is selected  
    		//setSelected(comp,combine,true);
    		
        	// when the user selects more then one resource, 
        	// the canvas menu must be shown  
        	if (this.iconsPanel.hasSelection())
        	{
        		Container cont = comp.getParent(); 
        		Point p=comp.getLocation();
        		vcomp=(VComponent)comp.getParent(); // IconPanel; 
        		
            	// redirect to browserController
            	JPopupMenu menu=browserController.getActionMenuFor(vcomp); 
            	menu.show(cont,p.x+e.getX(),p.y+e.getY());
        		
        	}
        	else
        	{
            	// redirect to browserController
            	JPopupMenu menu=browserController.getActionMenuFor(vcomp); 
            	menu.show(comp,e.getX(),e.getY());
        	}
        }
        else
        {
            // potential start for a DnD !
            firstMouseEvent = e;
            e.consume();
        }
        
      

        
    }

  
    public void mouseEntered(MouseEvent e)
    {
    	Component comp = e.getComponent();
    	
    	if (comp instanceof LabelIcon)
    	{
    		((LabelIcon)comp).animateFocus(true); 
    	}
    	// follow mouse (onMouseOver():)
       // e.getComponent().requestFocusInWindow();  
    }

    private VComponent getVComponent(Component comp)
    {
        if (comp instanceof VComponent) 
            return ((VComponent)comp);
            
        return null; 
    }

    public void mouseExited(MouseEvent e)
    {
         Component comp = e.getComponent();
     	
         if (comp instanceof LabelIcon)
 		 {	
     		((LabelIcon)comp).animateFocus(false); 
     	 }
    }
    
    /** 
     * From teh Web:
     * http://java.sun.com/docs/books/tutorial/uiswing/misc/dnd.html#customComp
     */
    public void mouseDragged(MouseEvent e) 
    {
    	
    }
    /* DragGesture listener:
    public void mouseDraggedNOT(MouseEvent e) 
    {

        //Don't bother to drag if the component displays no image.
        //if (image == null) return;

        if (firstMouseEvent != null)
        {
            e.consume();

            //If they are holding down the control key, COPY rather than MOVE
            int ctrlMask = InputEvent.CTRL_DOWN_MASK;
            int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
                  TransferHandler.COPY : TransferHandler.MOVE;

            int dx = Math.abs(e.getX() - firstMouseEvent.getX());
            int dy = Math.abs(e.getY() - firstMouseEvent.getY());
            //Arbitrarily define a 5-pixel shift as the
            //official beginning of a drag.
            
            // ***
            //
            // TODO: Use Swings DnD interface. 
            //
            // *** 
            
            if (dx > GuiSettings.minimal_drag_distance || dy > GuiSettings.minimal_drag_distance) 
            {
                //This is a drag, not a click.
                JComponent c = (JComponent)e.getSource();
                
                //Tell the transfer handler to initiate the drag.
                // == default 
                TransferHandler handler = c.getTransferHandler();
                
                if (handler!=null)
                {
                    // use registered TransferHandler -> MyTransferHandler
                   handler.exportAsDrag(c, firstMouseEvent, action);
                }
                
                firstMouseEvent = null;
            }
        }
    }*/
   

    public void mouseMoved(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{
	}

	public void keyTyped(KeyEvent e)
	{
		//System.err.println("KeyEvent:"+e);
	}

}
