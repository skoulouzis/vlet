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
 * $Id: HexViewController.java,v 1.6 2011-06-10 10:18:03 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:03 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.UnsupportedEncodingException;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.dnd.VTransferData;
import nl.uva.vlet.vrl.VRL;

public class HexViewController implements AdjustmentListener, KeyListener, ActionListener
{
    public  static class ViewerDropTargetListener implements DropTargetListener 
    {
        /** Main Controller */ 
        private HexViewController controller;


        public ViewerDropTargetListener(HexViewController controller)
        {
            this.controller=controller;
        }
       
        public void dragEnter(DropTargetDragEvent dtde)
        {
            Global.debugPrintln(this, "dragEnter:" + dtde);
            Component source = dtde.getDropTargetContext().getComponent(); 
        }
        
        public void dragOver(DropTargetDragEvent dtde)
        {
            Component source = dtde.getDropTargetContext().getComponent(); 
        }

        public void dropActionChanged(DropTargetDragEvent dtde)
        {
            Global.debugPrintln("MyDragHandler", "dropActionChanged:" + dtde);
        }

        public void dragExit(DropTargetEvent dte)
        {
            Component source = dte.getDropTargetContext().getComponent(); 
            Global.debugPrintln("MyDragHandler", "dragExit:" + dte);
        }

        public void drop(DropTargetDropEvent dtde)
        {
            // currently this method does all the work when a drop is performed
            
            try
            {
                 
                Transferable data = dtde.getTransferable();
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
              if (VTransferData.canConvertToVRLs(data))   
              { 
                    dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
                
                    VRL vrls[]=VTransferData.getVRLsFrom(data); 
                    controller.handleDrop(vrls[0]); 
               }
              else if (data.isDataFlavorSupported (DataFlavor.stringFlavor))
              { 
                    dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
                
                    String txt=  (String) 
                    data.getTransferData(DataFlavor.stringFlavor);
                    dtde.getDropTargetContext().dropComplete(true);
                    controller.setContents(txt); 
               }                
            }
            catch (Exception e)
            {
                Global.errorPrintStacktrace(e); 
            }
        }
    }
    
	private HexViewer hexViewer;

	public HexViewController(HexViewer viewer) 
	{
		this.hexViewer=viewer; 
	}

	public void setContents(String txt)
    {
        try
        {
            hexViewer.setContents(txt.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            hexViewer.setContents(txt.getBytes()); 
        } 
    }

    public void handleDrop(VRL vrl)
    {
	    this.hexViewer.updateLocation(vrl); 
    }

    public void adjustmentValueChanged(AdjustmentEvent e)
	{
		
		debug("adjustment event+"+e);
		debug(">>> new value="+e.getValue());
		int val=e.getValue(); 
		
		//val=val-(val%hexViewer.nrBytesPerLine); 
 		this.hexViewer.moveToOffset(val); 
 		hexViewer.redrawContents(); 
 		
 		if (true) 
 			return; 
 		
		
		long prev=hexViewer.getOffset();
		// round step to nrBytesPerLine 
		long diff=val-prev;
		long nrbpl=hexViewer.getNrBytesPerLine();
		
		debug("diff="+diff+", nrbpl="+nrbpl);
		// micro increments! make sure minimum is nr  bytes per line 
		
		if ((diff>-nrbpl) && (diff<nrbpl))
		{
			if (diff<0) 
				diff=-nrbpl;
			else if (diff>=0)
				diff=nrbpl;
			//else diff=0 
		}
		// minus remainder to make sure increment is whole multiplication of nrBytesPerLine
		if (diff>0) 
			diff=diff-(diff%nrbpl);
		else if (diff<0)
			diff=diff+(diff%nrbpl);
		
		debug("diff="+diff);
		
		//val=val-(val%hexViewer.nrBytesPerLine); 
 		this.hexViewer.moveToOffset(prev+diff); 
 		hexViewer.redrawContents(); 
	}

	public void keyTyped(KeyEvent e) 
	{		
	}
	
	public void keyPressed(KeyEvent e)
	{
		int kchar=e.getKeyChar(); 
		int kcode=e.getKeyCode();
		int mods=e.getModifiers(); 
		
		String kstr=KeyEvent.getKeyText(kcode);
		hexViewer.debug("kstr="+kstr);
		hexViewer.debug("kchar="+kchar);

		

		if ((mods & KeyEvent.CTRL_MASK) >0)
		{
			if (kstr.compareToIgnoreCase("B")==0)
			{
				hexViewer.setWordSize(1); 
				hexViewer.redrawContents(); 
			}
			// [T]oolbar  (CTRL-F) = find
			else if (kstr.compareToIgnoreCase("T")==0)
			{
				hexViewer.toggleFontToolBar(); 
			}
			else if (kstr.compareToIgnoreCase("B")==0)
			{
				hexViewer.setWordSize(1); 
				hexViewer.redrawContents(); 
			}
			else if (kstr.compareTo("1")>=0  && (kstr.compareTo("8")<=0)) 
			{
				hexViewer.setWordSize(new Integer(kstr)); 
				System.err.println("wordSize="+hexViewer.getWordSize());
				hexViewer.redrawContents(); 
			}
			if (kstr.compareToIgnoreCase("Right")==0)
			{
				hexViewer.setMinimumBytesPerLine(hexViewer.getMinimumBytesPerLine()
						+ hexViewer.getWordSize()); 
				
				hexViewer.redrawContents(); 
			}
			else if (kstr.compareToIgnoreCase("Left")==0)
			{
				hexViewer.setMinimumBytesPerLine(hexViewer.getMinimumBytesPerLine()
						- hexViewer.getWordSize()); 
							
				hexViewer.redrawContents(); 
			}
			
		}
		else if (kstr.compareToIgnoreCase("Page Down")==0)
		{
			hexViewer.addOffset(hexViewer.nrBytesPerView);			
			hexViewer.redrawContents(); 
		}
		else if (kstr.compareToIgnoreCase("Page Up")==0)
		{
			hexViewer.addOffset(-hexViewer.nrBytesPerView);			
			hexViewer.redrawContents(); 
		}
		else if (kstr.compareToIgnoreCase("Right")==0)
		{
			hexViewer.addOffset(1); 			
			hexViewer.redrawContents(); 
		}
		else if (kstr.compareToIgnoreCase("Left")==0)
		{
			hexViewer.addOffset(-1);			
			hexViewer.redrawContents(); 
		}
		else if (kstr.compareToIgnoreCase("Up")==0)
		{
			hexViewer.addOffset(-hexViewer.nrBytesPerLine);  			
			hexViewer.redrawContents(); 
		}
		else if (kstr.compareToIgnoreCase("Down")==0)
		{
			hexViewer.addOffset(hexViewer.nrBytesPerLine);  			
			hexViewer.redrawContents(); 
		}
	}
	
	public void keyReleased(KeyEvent e) 
	{
	}
	
	void debug(String msg) 
	{
		//Global.errorPrintln(this,msg); 
		Global.debugPrintln(this,msg); 
	}

	public void actionPerformed(ActionEvent e) 
	{
		Object source=e.getSource(); 
		
		if (source==this.hexViewer.offsetField)
		{
			String txt=this.hexViewer.offsetField.getText();
			hexViewer.moveToOffset(Long.decode(txt));
			hexViewer.redrawContents();
		}
	}
	
}
