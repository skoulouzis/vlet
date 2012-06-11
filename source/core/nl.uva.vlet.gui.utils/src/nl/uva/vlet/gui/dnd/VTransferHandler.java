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
 * $Id: VTransferHandler.java,v 1.7 2011-06-10 10:18:02 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:02 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.util.Messages;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.widgets.IconTextField;
import nl.uva.vlet.vrl.VRL;

/**
 * TransferHandler for both Resource Components (Component/
 * VContainer) and other (Swing) components. 
 * <p>
 * Handles the Drag And Drop between the different GUI Components
 * like IconPanel and ResourceTree. Also the 'import' and 'export'
 * of Transfer Data when using Copy-Paste will use this Transfer Handler. 
 * Make sure to comply with Swing's DnD framework! 
 * <p>
 * Note that this TransferHandler is a cross-browser handler !
 * 
 * @author P.T. de Boer
 */
public class VTransferHandler extends TransferHandler
{
    //  === Class Field === //
  
    private static VTransferHandler defaultTransferHandler=new VTransferHandler(); 

    //  === Instance stuff === //
   
   

    /**
     * 
     */
    private static final long serialVersionUID = -4340455803750670641L;

    /** Construct default TransferHandler wich handles VRL Objects */
    public VTransferHandler()
    {
        //dataFlavor=locationDataFlavor;
    }
    
    @Override
    public boolean importData(JComponent comp, Transferable data) 
    {
    	// This method is directory called when performing CTRL-V 
    	Debug(">>> importData called on:"+comp);
    	return pasteData(comp,data); 
    }
    
    /**
     * Paste Data call when for example CTRL-V IS called ! 
     * Supplied components is the Swing Component which has the focus 
     * when CTRL-V was called ! 
     */ 
    public boolean pasteData(JComponent comp, Transferable data)
	{
    	Debug("doDropOn:"+comp);
		
		// miss click: 
		if ((comp==null) || ((comp instanceof VComponent)==false))
			return false;
		
		VComponent vcomp=(VComponent)comp; 
    	MasterBrowser bc = vcomp.getMasterBrowser();
    	VComponent destinationComp=null; 
    	
    	//
    	// drop on VContainer, check selection:
    	//
    	
    	if (vcomp instanceof VContainer) 
    	{
    		ResourceRef refs[]=((VContainer)vcomp).getSelection();
    		
    		if (refs!=null)
    		{
    			if (refs.length!=1)
    			{
    				UIGlobal.displayErrorMessage(Messages.PLEASE_SELECT_ONE_RESOURCE_TO_PASTE_TO); 
    				return false;
    			}
    			else
    			{
    				// Select FIRST and Only ! 
    				destinationComp=((VContainer)vcomp).getVComponent(refs[0]); 
    			}
    		}
    		else
    		// No Selections: Drop on VContainer ! 
    		{
    			destinationComp=vcomp; //.getResourceRef(); // paste into container
    		}
    	}
    	else
    	{
    		// drop on VComponent 
    		destinationComp=vcomp; //.getResourceRef();; // paste into container
    	}
    	
    
    	// check dropped data: 
    	if (VTransferData.canConvertToVRLs(data))
    	{
    		ResourceRef refs[]=null;
    		 
			try
			{
				// perform default Drag and Drop: 
				refs = VTransferData.getRefsFrom(data);
				//System.err.println("doDrop vrls[0]="+vrls[0]);
				
	    		DropAction dropAction=new DropAction(destinationComp,refs);
	    		
	    		dropAction.component=comp;
	   			dropAction.interactive=false;  
	    		dropAction.point=null; 
	    		bc.performDragAndDrop(dropAction);
	    		
	    		return true; 
			}
			catch (Exception e)
			{
				Global.errorPrintln(this,"Exception:"+e); 
				Global.errorPrintStacktrace(e); 
				return false; 
			}
    	}
    	else
    	{
    		// Raw data streams or MimeType Droppings ! 
    		
    		DataFlavor flavors[]=data.getTransferDataFlavors();
    		
    		Global.errorPrintln(this, "--- Import data not supported ---\n Flavors are:\n");
        	
        	for (DataFlavor flav:flavors)
        	{
        		Global.errorPrintln(this, " - "+flav);
        	}
        	
    		// TODO: Non VComponent sources: DataStream and mimetypes:!
    	}
        //int action=ActionCommandType.DNDMOVEDROPNODES; // default is move ! 
        //doImportData(comp,data,action);
        //doImportData(comp,data);
        
        // currently handled by MyDragHandler !!! 
        
        return true;
	}
    
    /** Interactive drop on JComponent */ 
    public boolean interActiveDrop(Component comp, Point point,Transferable data) 
	{
    	Debug(">>> doDropOn called on:"+comp);

    	VComponent vcomp;
    	
		// Must be one of mine, VTransferHandler are ONLY 
    	// installed on VComponents ! 
   		vcomp=getVComponentFrom(comp);
   		
    	if (vcomp==null) 
    	{
    		Global.errorPrintln(this,"doDropOn: Cannot import data to non VComponent:"+comp); 
    		return false;
    	}
    	// next step: 
    	
    	return handleDragAndDropOn(comp,vcomp,point,data); 
	}
    
    /** Interactive drop on Container with child */ 
	public boolean interActiveDrop(Component parent, VComponent child, Point point, Transferable data)
	{
		// interactive true, point!=null => show popup on parent for child 
    	return handleDragAndDropOn(parent,child,point,data); 
	}
	
    /** If interactive==true, isMove is ignored and a popup is shown */ 
	private boolean handleDragAndDropOn(Component comp,VComponent vcomp, Point point,
    		Transferable data)
    {
		Debug("doDropOn:"+vcomp);
		
		// miss click: 
		if (vcomp==null)
			return false; 
	
    	MasterBrowser bc = vcomp.getMasterBrowser();
    	
    	VComponent destinationComp=null; 
    	
    	destinationComp=vcomp;
    
    	// check dropped data: 
    	if (VTransferData.canConvertToVRLs(data))
    	{
    		ResourceRef refs[]=null;
    		 
			try
			{
				// perform default Drag and Drop: 
				refs = VTransferData.getRefsFrom(data);
				//System.err.println("doDrop vrls[0]="+vrls[0]);
				
	    		DropAction dropAction=new DropAction(destinationComp,refs);
	    		
	    		dropAction.component=comp;
	    		//dropAction.isMove=false; 
	   			dropAction.interactive=true; 
	    		dropAction.point=point; 
	    		bc.performDragAndDrop(dropAction);
	    		
	    		return true; 
			}
			catch (Exception e)
			{
				Global.errorPrintln(this,"Exception:"+e); 
				Global.errorPrintStacktrace(e); 
				return false; 
			}
    	}
    	else
    	{
    		// Raw data streams or MimeType Droppings ! 
    		
    		DataFlavor flavors[]=data.getTransferDataFlavors();
    		
    		Global.errorPrintln(this, "--- Import data not supported ---\n Flavors are:\n");
        	
        	for (DataFlavor flav:flavors)
        	{
        		Global.errorPrintln(this, " - "+flav);
        	}
        	
    		// TODO: Non VComponent sources: DataStream and mimetypes:!
    	}
        //int action=ActionCommandType.DNDMOVEDROPNODES; // default is move ! 
        //doImportData(comp,data,action);
        //doImportData(comp,data);
        
        // currently handled by MyDragHandler !!! 
        
        return true;
    }
    
     @Override
     public void exportDone(JComponent comp,
            Transferable data,
            int action)
     {
    	 // this method is called when the export of the Transferable is done. 
    	 // The actual DnD is NOT finished.
    	 
       	 Debug("exportDone"+data);
    	 Debug("exportDone action="+action);
     }  
 
    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors) 
    {
    	for (DataFlavor flav:flavors)
    		Debug("canImport:"+flav);
        return VTransferData.hasMyDataFlavor(flavors); 
    }
    
    private void Debug(String msg)
	{
		Global.debugPrintln(this,msg); 
		//Global.errorPrintln(this,msg); 
	}

	@Override
    protected Transferable createTransferable(JComponent c)
    {
        Debug("Create Transferable:"+c);
        
        Container parent=c.getParent();
        
        ResourceRef[] sources=getSelectedSourcesFrom(c); 

        if (sources!=null)
        { 
        	// Single Item
        	if (sources.length==1)
        		return new ResourceTransferable(sources[0]);
        	// Aggregate 
        	else
        		return new ResourceListTransferable(sources); 
        }
        else
        {
            Debug("Tranfer source not recognised:"+c); 
        }
        
        return null; 
       
    }

    @Override
    public void exportAsDrag(JComponent comp,
            InputEvent e,
            int action)
     {
        Debug("exportAsDrag:"+e);
        super.exportAsDrag(comp,e,action);
     }  
    
    public void exportToClipboard(JComponent comp, Clipboard clipboard,int action)
    {
    	 Debug("exportToClipboard:"+comp);
    	super.exportToClipboard(comp, clipboard, action);
    }
    
    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;
    }
    
    
    public ResourceRef[] getSelectedSourcesFrom(Component c)
    {
    	//System.err.println("Get source from:"+c);
    	
    	VComponent vcomp=getVComponentFrom(c);
    	
    	Container parent=c.getParent();
    	
    	if (vcomp instanceof VContainer)
        {
          	return  ((VContainer)vcomp).getSelection();
        }
    	
    	if (parent instanceof VContainer)
    	{
    		// PATCH:
    		// If parent has a selection the selection from the parent
    		// has to exported. 
    		// This is the case for IconsPanel when an import is called 
    		// on a single IconsPanel (TODO: better import mechanism). 
    		ResourceRef[] refs=((VContainer)parent).getSelection();
    		
    		if (refs!=null)
    		{
        		Debug("Warning: fetching selected resource from PARENT of VComponent"+parent);
    			return refs;
    		}
    	
    	}
    	
        if (vcomp!=null) 
        {
          	ResourceRef ref=vcomp.getResourceRef();  
          	if (ref==null) 
          		return null;
          	
          	ResourceRef refs[]=new ResourceRef[1];
          	refs[0]=ref;
          	return refs;
        }
        
        if (c instanceof IconTextField)
        {
            String refText=((IconTextField)c).getText();
         
            ResourceRef refs[]=new ResourceRef[1];
            
            try
            {
                refs[0]=new ResourceRef(new VRL(refText),null,null);
                return refs; 
            }
            catch (VRLSyntaxException e)
            {
            } 
        }
        
        return null;
        
    }
    
    private static VComponent getVComponentFrom(Component c)
    {
    	return getVComponentFrom(c,null); 
    }
    
    private static VComponent getVComponentFrom(Component c, Point point)
    {
        Container parent = c.getParent();
        
        // check Containers like JTree: 
        
        if (point!=null)
        {
        	Component newcomp=c.getComponentAt(point);
        	if (newcomp!=null)
        		c=newcomp; // use Child ! 
        }
        
        if (c instanceof VComponent)
        {
            return ((VComponent) c);
        }
        
        return null; 
    }

    /** Global Transfer Handler */ 
    public static VTransferHandler getDefault()
    {
        return defaultTransferHandler;
    }
    
}
