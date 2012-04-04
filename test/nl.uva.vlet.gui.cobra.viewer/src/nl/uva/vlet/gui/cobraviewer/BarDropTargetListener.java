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
 * $Id: BarDropTargetListener.java,v 1.3 2011-04-18 12:27:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:34 $
 */ 
// source: 

package nl.uva.vlet.gui.cobraviewer;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import nl.uva.vlet.Global;
import nl.uva.vlet.vrl.VRL;

/**
 * DropTargetListener, handles 'drop' on Node Components. 
 * Install this DropTargetListener to support drops on the component. 
 *  
 */

public  class BarDropTargetListener implements  DropTargetListener//, DragSourceListener,
{
    /** Main Controller */ 
    private CobraHTMLViewer viewerController;


    public BarDropTargetListener(CobraHTMLViewer controller)
    {
        this.viewerController=controller;
    }
   
    public void dragEnter(DropTargetDragEvent dtde)
    {
        //Global.debugPrintln(this, "dragEnter:" + dtde);
        Component source = dtde.getDropTargetContext().getComponent(); 
    }
    
    public void dragOver(DropTargetDragEvent dtde)
    {
        Component source = dtde.getDropTargetContext().getComponent(); 
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        //Global.debugPrintln("MyDragHandler", "dropActionChanged:" + dtde);
    }

    public void dragExit(DropTargetEvent dte)
    {
        Component source = dte.getDropTargetContext().getComponent(); 
       // Global.debugPrintln("MyDragHandler", "dragExit:" + dte);
    }

    public void drop(DropTargetDropEvent dtde)
    {
        // currently this method does all the work when a drop is performed
        
        try
        {
            Transferable t = dtde.getTransferable();
            DropTargetContext dtc = dtde.getDropTargetContext();
            Component comp = dtc.getComponent();
            
            int action = dtde.getDropAction();
            
            // check dataFlavor 
            
            /* if (t.isDataFlavorSupported (DataFlavor.javaFileListFlavor))
            {
                // Handle Java File List 
                
                dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
                
                java.util.List fileList = (java.util.List)
                    t.getTransferData(DataFlavor.javaFileListFlavor);
                Iterator iterator = fileList.iterator();
                
                int len=fileList.size();
                
                VRL sourceLocs[]=new VRL[len];
                int index=0;
                
                dtde.getDropTargetContext().dropComplete(true);
                
                if (len<=0) 
                    return; 
                
                while (iterator.hasNext())
                {
                  java.io.File file = (File)iterator.next();
                  
                  Debug("name="+file.getName());
                  Debug("url="+file.toURL().toString());
                  Debug("path="+file.getAbsolutePath());
                  
                  sourceLocs[index++]=new VRL("file",null,file.getAbsolutePath()); 
                }
                
                viewerController.handleDropEvent(comp,sourceLocs[0].toString()); 
                
                boolean isMove=(action==DnDConstants.ACTION_MOVE);
                
          }
          else*/
            
          // String types for now: 
            
          if (t.isDataFlavorSupported (DataFlavor.stringFlavor))
          { 
                dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
            
                String txt=  (String) 
                t.getTransferData(DataFlavor.stringFlavor);
                Debug("Tranferable string="+txt);
                
                dtde.getDropTargetContext().dropComplete(true);
                
                //viewerController.handleDropEvent(comp,txt); 
           }

            
        }
        catch (Exception e)
        {
            Global.errorPrintStacktrace(e); 
        }
    }
   
    private void Debug(String string)
    {
        Global.debugPrintf(this,"%s\n",string);
    }
    
}
