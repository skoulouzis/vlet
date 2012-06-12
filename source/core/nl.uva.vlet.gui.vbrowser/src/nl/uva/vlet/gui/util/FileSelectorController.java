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
 * $Id: FileSelectorController.java,v 1.4 2011-06-10 10:18:00 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:00 $
 */ 
// source: 

package nl.uva.vlet.gui.util;

import javax.swing.JPopupMenu;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.dnd.DropAction;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.ViewModel;
import nl.uva.vlet.gui.viewers.ViewerEvent;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskSource;
import nl.uva.vlet.vrl.VRL;

/** 
 * Simple File Sector.
 *   
 */
public class FileSelectorController implements MasterBrowser, ITaskSource
{
	final FileSelector fileSelector;
	ProxyNode selectionNode;

	static
	{
        Global.init();
        // ProxyVNodeFactory.initPlatform();
	}
	
	public FileSelectorController(FileSelector selector)
	{
		this.fileSelector=selector; 
	}
	
	public void exit()
	{
		Message("openLocation="+this.selectionNode.getVRL().toString()); 	
		this.fileSelector.dispose(); 
	}

	public JPopupMenu getActionMenuFor(VComponent comp)
	{
		return null;
	}

	public ViewModel getViewModel() 
	{
		return ViewModel.getDefault(); 
	}
	

	public void performDragAndDrop(DropAction action) 
	{
	}
	
	public void notifyHyperLinkEvent(ViewerEvent event) {}

	public String getID() 
	{
		return "FileSelector";
	}

	public void handle(Exception e) 
	{
		ExceptionForm.show(this.fileSelector,e,true);		
	}

	public void messagePrintln(String str) 
	{
		
	}

	public void setHasTasks(boolean b)
	{
		
	}

	public void setRoot(final VRL vrl)
	{
		// open location in background: 
		
		ActionTask openTask=new ActionTask(this,"Open location:"+vrl)
		{

			@Override
			protected void doTask() throws VlException
			{
				ProxyNode node;
				
				if (vrl==null)
					node=UIGlobal.getProxyVRS().getProxyNodeFactory().openLocation(UIGlobal.getProxyVRS().getVirtualRootLocation());
				else
					node=UIGlobal.getProxyVRS().getProxyNodeFactory().openLocation(vrl); 
				
				fileSelector.resourceTree.setRootNode(node);
			}

			@Override
			public void stopTask()
			{
				
			}
		};
		openTask.startTask(); 
	}

	public void setRootNode(ProxyNode node) throws VlException 
	{
		this.fileSelector.resourceTree.setRootNode(node); 
	}
	
	public void Message(String msg)
	{
		System.out.println("FileSelector:"+msg); 
	}

	public void startNewWindow(VRL vrl)
	{
		// ignore 
	}
	
	public void refresh() throws VlException
	{
		if(selectionNode!=null)
		{
			_asyncRefresh(selectionNode);
		}
	}
	
	private void _asyncRefresh(final ProxyNode node)
	{
		  // go background: 
	     ActionTask refreshTask=new ActionTask(this,"refresh")
	     {
	     	public void doTask() throws VlException
	     	{
	     		node.refresh(); 
	     	}

	     	@Override
	     	public void stopTask()
	     	{
	     	}
	     };
	     
	     refreshTask.startTask(); 
	}

	public void performSelection(VComponent comp) 
	{
	    if (comp==null)
	    {
	        // clear selection
	        this.selectionNode=null;
	        return; 
	    }
	    
		// resolve logical resource nodes:
		final nl.uva.vlet.gui.data.ResourceRef ref=comp.getResourceRef();
		
		if (ref==null)
		    return;
		
        final VRL vrl=ref.getVRL();
        
        if (vrl==null)
            return; 
        
		ActionTask updateTask=new ActionTask(this,"FileSelection.openLocation")
		{
		    public void doTask()
		    {
		        try
		        {
		            ProxyNode pnode=ProxyNode.getProxyNodeFactory().openLocation(vrl,true);
		            if (pnode!=null)
		                updateSelectionNode(pnode); 
		        }
		        catch (VlException e) 
		        {
		            handle(e); 
		        }   
		    }

            @Override
            public void stopTask()
            {
                
            }
		};
		
		updateTask.startTask();
	}

	protected void updateSelectionNode(ProxyNode pnode)
	{
        this.selectionNode=pnode;           
        String path= pnode.getVRL().toString();
        this.fileSelector.locationTextField.setText(path);
	}
	
	public void performAction(VComponent comp)
	{
		performSelection(comp);
	}

	public void perform2Action(VComponent comp)
	{
		fileSelector.bpvalid.doClick();
	}
	
}
