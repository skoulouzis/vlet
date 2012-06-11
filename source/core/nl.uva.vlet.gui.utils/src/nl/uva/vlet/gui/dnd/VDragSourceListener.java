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
 * $Id: VDragSourceListener.java,v 1.3 2011-04-18 12:27:14 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:14 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import nl.uva.vlet.Global;

/**
 * Add this to your component if this component can
 * be a DRAG source. 
 */
public class VDragSourceListener implements  DragSourceListener
{
	  /** jdk recommends one dragsource listener per JVM */ 
	  private static VDragSourceListener dragSourceListener=new VDragSourceListener();
	  
	  public static VDragSourceListener getDefault() 
	  {
		return dragSourceListener;
	  }
	  
	  // === //
	  
	  /** Class Object */ 
	  
	  VDragSourceListener()
	  {
          // PLAF depend drag source. Represents OS 
		  DragSource dragSource = DragSource.getDefaultDragSource();
		  // install one-for-all drag source listener...
		  dragSource.addDragSourceListener(this); 
          
	  }
	  
	  // Is called when ANY drag is initiated 
	  public void dragEnter(DragSourceDragEvent dsde)
      {
	        Global.debugPrintln(this, "dragEnter:" + dsde);
	        //DragSourceContext dse = dsde.getDragSourceContext();
	        //Transferable t = dse.getTransferable();
            //DragSourceContext dsc = dsde.getDragSourceContext();            
      }
	  
	  public void dragOver(DragSourceDragEvent dsde)
	  {
          Global.debugPrintln(this, ">>> dragSource dragOver:" + dsde);
          //DragSourceContext dsc = dsde.getDragSourceContext();
          //dsc.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); 
	  }
	  
	  public void dropActionChanged(DragSourceDragEvent dsde)
	  {
          int mods = dsde.getGestureModifiers();          
          Global.debugPrintln(this, "dropActionChanged:" + dsde);
          Global.debugPrintln(this, "gesture modifiers=:"+mods); 
          DragSourceContext dsc = dsde.getDragSourceContext();
          //dsc.setCursor()
	  }

	  public void dragExit(DragSourceEvent dse)
	  {
	        Global.debugPrintln(this, "dragExit:" + dse);
	  }

	  public void dragDropEnd(DragSourceDropEvent dsde)
	  {
	        Global.debugPrintln(this, "dragDropEnd:" + dsde);
	        DragSourceContext sourceContext = dsde.getDragSourceContext();

	  }

}
