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
 * $Id: BrowserInteractiveActions.java,v 1.9 2011-05-11 17:44:01 ptdeboer Exp $  
 * $Date: 2011-05-11 17:44:01 $
 */ 
// source: 

package nl.uva.vlet.gui.vbrowser;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.Messages;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.actions.TransferWatcher;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dialog.CopyDialog;
import nl.uva.vlet.gui.dialog.CopyDialog.CopyOption;
import nl.uva.vlet.gui.dialog.SimpleDialog;
import nl.uva.vlet.gui.dialog.SimpleNButtonDialog;
import nl.uva.vlet.gui.editors.ResourceEditor;
import nl.uva.vlet.gui.panels.monitoring.TransferMonitorDialog;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.util.proxy.GridProxyDialog;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VRSTransferManager;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.ui.ICopyInteractor;

/**
 * New Delegation class to handle more complex
 * actions and to move code from the VBrowserController
 * to this class since that class (and ProxyTNode) are
 * getting a little bit crowded.   
 * <p>
 * All methods are assumed to be executed during the Swing GUI 
 * event thread and will start background tasks if needed but after
 * the 'interactive' part has been done.  
 * These methods start with 'async' and are private. 
 * 
 * @author P.T. de Boer
 */
public class BrowserInteractiveActions
{
    public class CopyInteractor implements ICopyInteractor
    {
        BooleanHolder overWriteAll=null;
        BooleanHolder skipAll=null; 
        
        private TransferMonitorDialog transferDialog=null;
        private boolean suspended;  
        
        public CopyInteractor()
        {
            overWriteAll=new BooleanHolder(false); 
            skipAll=new BooleanHolder(false); 
        }
        
        @Override
        public InteractiveAction askTargetExists(String message,
				VRL source,
				VAttribute sourceAttrs[], 
				VRL target, 
				VAttribute targetAttrs[],
				StringHolder optNewName) 
		{
            // already checked "Skip All"  or "Overwrite All"! 
            InteractiveAction action = checkSkipOrOverWriteAll();
            
            if (action!=null)
                return action; 
            
            suspendTransfer(); 
            
            InteractiveAction result=doAsk(message,
            		source,
            		sourceAttrs,
            		target,
            		targetAttrs,
            		optNewName);
            
            resumeTransfer();
            
            return result; 
        }
        
        private synchronized void suspendTransfer()
        {
            this.suspended=true;
            
            if (transferDialog!=null)
                transferDialog.stop();
        }
        
        private synchronized void resumeTransfer()
        {
            this.suspended=false; 
            
            if (transferDialog!=null)
                transferDialog.start();
        }
        
        public synchronized void setDialog(TransferMonitorDialog dialog)
        {
            // Set Dialog might occur after the Copy Interactor already popped up a dailog.  
            // 
            if (suspended)
                if  ((transferDialog==null) && (dialog!=null))
                    dialog.stop();
            
            this.transferDialog=dialog; 
        }
        
        // already asked ? 
        protected InteractiveAction checkSkipOrOverWriteAll()
        {
            // overwrite all! 
            if ((overWriteAll!=null) && (overWriteAll.value==true))
            {
                // update for this copy action 
                return InteractiveAction.CONTINUE;
            }
            
            // skip all! 
            if ((skipAll!=null) && (skipAll.value==true))
            {
                // update for this copy action 
                return InteractiveAction.SKIP; 
            }
            
            return null;
        }
        
        private InteractiveAction doAsk(String message, 
                VRL source, 
                VAttribute[] sourceAttrs, 
                VRL target,
                VAttribute[] targetAttrs, 
                StringHolder optNewName)
        {
        	
            InteractiveAction action=checkSkipOrOverWriteAll();
            if (action!=null)
                return action; 
            
        	// link to VBrowser frame!
        	JFrame frame = BrowserInteractiveActions.this.browserController.getFrame(); 
        	
        	CopyDialog dailog = CopyDialog.showCopyDialog(frame,
        			source,
        			new VAttributeSet(sourceAttrs),
        			target,
        			new VAttributeSet(targetAttrs),
        			true); 
        
        	
        	CopyOption option = dailog.getCopyOption();
        	
        	if (option==null || option==CopyOption.Cancel)
            {
        		return InteractiveAction.CANCEL; 
            }
        	
    		if (option==CopyOption.Skip)
            {
        		if (dailog.getSkipAll())
        		{
        			skipAll.value=true;
        		}
            	return InteractiveAction.SKIP;
            }
        	
        	if (option==CopyOption.Overwrite)
            {
        		if (dailog.getOverwriteAll())        		
        		{
        			overWriteAll.value=true;
        		}
            	return InteractiveAction.CONTINUE; 
            }
        	
        	if (option==CopyOption.Rename)
            {
                 optNewName.value="Copy Of "+target.getBasename(); 
                 
                 optNewName.value= JOptionPane.showInputDialog("Specify new name", optNewName.value);
                 
                 if (optNewName.value==null)
                     return InteractiveAction.CANCEL; 
                 else
                     return InteractiveAction.RENAME; 
             }
        	
        	// Default: 
        	return InteractiveAction.CANCEL; 
        }
    }   
    
    // === //
    
    final private BrowserController browserController;  
    
    public BrowserInteractiveActions(BrowserController controller)
    {
        this.browserController=controller; 
    }

    public boolean interactiveDelete(ProxyNode node,BooleanHolder forceRecurseDelete) throws VlException
    {
        boolean compositeDelete = false;

        // check composite nodeLocations, do not use a viewfilter    
        if ((node.isResourceLink() == false) && (node.isComposite()))
        {
            if ((forceRecurseDelete==null) || (forceRecurseDelete.value==false))
            {
                //if (node.getNrOfChilds(null) > 0) 
                {
                    String options1[]={"Delete","Cancel"}; 
                    String options2[]={"Delete All","Cancel"}; 

                    String options[];

                    if (forceRecurseDelete==null)
                        options=options1; 
                    else 
                        options=options2;

                    String result=null;

                    String message= Messages.Q_do_you_want_to_recursive_delete_resource
                    +" "+node.getType()+": '"+node.getName()+"' ?\n"; 

                    result = SimpleNButtonDialog.showDialog(null,"Delte Resource(s) ?",message,options);

                    if (result.equals("Cancel"))
                        return false; 

                    if (result.equals("Delete All"))
                    {
                        //Global.infoPrintln(this,"Delete ALL Selected"); 
                        forceRecurseDelete.value=true;
                    }

                }
            }
            // delete'm all:
            compositeDelete=true;
        }
        else
        {
            compositeDelete = false;// not composite
        }

        // perform background delete: 
        asyncDelete(node, compositeDelete);

        return true; 
    }

    public void interactiveDeleteAll(ProxyNode parentNode, ResourceRef[] deleteSelection)
    {
        BooleanHolder deletemAll=new BooleanHolder(false);

        for (ResourceRef ref:deleteSelection)
        {
            try 
            {
                boolean cont=interactiveDelete(ref,deletemAll);
                if (cont==false)
                {
                    Global.infoPrintf(this,"Cancel deleteAll\n");
                    return;
                }
            }
            catch (VlException e) 
            {
                browserController.handle(e); 
            } 
        }
    }


    public boolean interactiveDelete(ResourceRef ref,BooleanHolder forceRecurseDelete) throws VlException 
    {
        ProxyNode node=browserController.getProxyNodeFactory().getFromCache(ref.getVRL());

        if (node!=null)
            return interactiveDelete(node,forceRecurseDelete); 
        else
            Global.errorPrintf(this,"Node not in cache:%s\n",ref);

        return true; 
    }

    /** Interactive rename, will aks for new name */

    public void interactiveRename(ProxyNode node, String name) throws VlException
    {
        String oldName = node.getName();

        if (name == null)
        {
            name = JOptionPane.showInputDialog("Specify new name", oldName);
        }

        // go background:
        if (name!=null)
            asyncRenameTo(node,name, false);
    }

    // ==========================================================================
    // Asynchronous methods.  
    // ==========================================================================

    /**
     * Perform rename on underlaying resource
     * 
     * @param nameIsPath
     * @throws VlException
     */
    private void asyncRenameTo(final ProxyNode pnode,final String name, final boolean nameIsPath)
    throws VlException
    {
        if (name == null)
            return;

        // go background: 
        ActionTask renameTask=new ActionTask(this.browserController,"async RenameTo Task")
        {
            public void doTask() throws VlException
            {
                pnode.renameTo(name,nameIsPath);
            }

            @Override
            public void stopTask() {}; 
        };

        // go background: 
            renameTask.startTask(); 
    }


    /** Perform delete in backgroud:  */ 
    private void asyncDelete(final ProxyNode node, final boolean compositeDelete)
    {
        ActionTask task = new ActionTask(this.browserController, "Deleting:"+node.getVRL())
        { //
            public void stopTask()
            {
            }

            public void doTask()
            {
                try
                {
                    node.delete(compositeDelete);
                }
                catch (VlException e)
                {
                    browserController.handle(e);
                }
            }
        };
        
        // Show default task monitor: 
        task.startTask();
        nl.uva.vlet.gui.panels.monitoring.TaskMonitorDialog.showTaskMonitorDialog(this.browserController.getFrame(), task,500);
    }



//    /** Perform delete in backgroud:  */ 
//    private void asyncDelete(final ResourceRef ref, final boolean compositeDelete)
//    {
//        ActionTask task = new ActionTask(this.browserController, this.toString() + ".delete()")
//        { //
//            public void stopTask()
//            {
//            }
//
//            public void doTask()
//            {
//                try
//                {
//                    // (re)open location:
//                    ProxyNode node=browserController.getProxyNodeFactory().openLocation(ref.getVRL());
//                    node.delete(compositeDelete);
//                }
//                catch (VlException e)
//                {
//                    browserController.handle(e);
//                }
//            }
//        };
//
//        // start in background!
//        task.startTask();
//    }


    /**
     * This method will ask the user for authentication during the 
     * GUI thread (InvokeLater) and wait for user input. 
     * Assumes this method is called from a background thread.
     * First a new Task is created which asks the user for authentication. 
     * A next thread is created to wait for the user in the background. 
     * All this threading is need so the GUI won't be blocked. 
     * TODO: better threading 
     */ 

    // Will invoke dialog in GUI thread. 
    // return true if authentication has been provided, false if
    // action was cancelled. 
    public boolean interactiveCheckAuthenticationFor(VRL location)  
    {
        VRL actualVrl=location;
        
        // check cache (todo: better mechanisme for resolvink LinkNodes) 
        ProxyNode pnode=ProxyNode.getProxyNodeFactory().getFromCache(location); 

        if (pnode!=null) 
        {
            //
            // Direct Access: 
            // Node already opened: use ProxyNode and 
            // optional resolve  LinkTarget and use that one !
            // (Resolve 1 level here ) 
            //

            if (pnode.isResourceLink())
            {
                try
                {
                    actualVrl=pnode.getTargetVRL();
                }
                catch(VlException e)
                {
                    handle(e); 
                }
            }
        }
        
        return _interactiveCheckAuthenticationFor(actualVrl); 
    }

    /** interactive check acces to VRL 
     * @throws VlException */ 
    private boolean _interactiveCheckAuthenticationFor(final VRL location) 
    {
        // ===
        // check
        // ====

        //BrowserController bc=browserController; 
        VRSContext vrsContext=UIGlobal.getVRSContext(); 

        // auto create so we get defaults for the location:
        ServerInfo info=null; 
        
        try
        {
            info = vrsContext.getServerInfoFor(location,true);
        }
        catch (VlException e)
        {
            handle(e); 
        }

        if (info==null)
        {
            Global.warnPrintf(this,">>> NULL ServerInfo for:%s\n",location);
            //
            // continue: authentication might not be needed. 
            // if needed access will fail in a later stage.  
            // 
            return true;
        }

        Global.warnPrintf(this,"ServerInfo.isAuthenticationNeeded :%s\n",info.isAuthenticationNeeded(vrsContext));
        Global.warnPrintf(this,"ServerInfo.useGSIAuth             :%s\n",info.useGSIAuth()); 
        Global.warnPrintf(this,"ServerInfo.usePassword()          :%s\n",info.usePasswordAuth()); 
        Global.warnPrintf(this,"ServerInfo.isValid                :%s\n",info.hasValidAuthentication()); 
        // ================================================
        // Directly return true without GUI interaction ! 
        // ================================================
        if (info.isAuthenticationNeeded(vrsContext)==false)
            return true; 

        // ask user during gui thread.
        final String message="Authentication needed for :"+location; 

        if (info.usePasswordAuth()==true)
        {
            // currently SSH asks interactive ! 
            // other implementations should do also using 
            //UIGlobal.getVRSContext().getUI().askAuthentication(message, secret); 
            
//        {
//            ServerInfo newinfo = AuthenticationDialog.askAuthentication(message, info);
//
//            // set already to true to avoid multiple 
//            // authentication dialogs. 
//
//            if (newinfo==null) 
//                info.setHasValidAuthentication(false); 
//            else
//                info.setHasValidAuthentication(true); 
//
//            // check isValid whether it is ok to continue 
//            if (info.hasValidAuthentication()==false) 
//            {    
//                debug("Wrong Authentication or action cancelled");
//                return false;
//            }
//
        }
        else if (info.useGSIAuth()==true)
        {
            // blocking wait: 
            if (GridProxyDialog.askInitProxy(message)==false) 
                return false; // cancel 
        }
    
        // 
        // assume provided information is valid, until actual authentication
        // this to prevent multiple windows to popup 'during' 
        // authentication 

        // info.setHasValidAuthentication(false); 
        // update passwords, settings etc: 
        info.store();
        
        return true; 
    }


    public void interactiveEditProperties(final ProxyNode pnode, final boolean refresh)
    {
        // will wait for GUI to finish !
        // New Resource Location 
        if ((true) || pnode.getType().compareTo(VRS.RESOURCE_LOCATION_TYPE)==0)
        {
            // Asynchronous editor ! 
            ResourceEditor.editProperties(pnode,true);
            return; 
        }
        else
        {
            // Asynchronous editor ! 
            ResourceEditor.editProperties(pnode,false);
            return; 
        }

    }

    /**
     * Clears all cached attributes. Sent an event that resource listeners
     * should check/refetch their attributes.
     * 
     * @throws VlException
     * 
     */
    public void performRefresh(final ProxyNode node)
    {
        asyncRefresh(node);
    }
    
    private void asyncRefresh(final ProxyNode node)
    {
        // go background: 
        ActionTask refreshTask=new ActionTask(this.browserController,"Properties Update Task")
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

    /** Create new server Resource */

    public void interactiveCreateServer(final ProxyNode pnode, final String resourceType)
    {
        asyncCreateServer(pnode,resourceType);
    }
    
    private void asyncCreateServer(final ProxyNode pnode, final String resourceType)
    {
        ActionTask createTask=new ActionTask(browserController,"Create new:"+resourceType)
        {
            @Override
            protected void doTask() throws VlException
            {
                try
                {
                    // Do Not Create DURING GUI THREAD!

                    ProxyNode newNode = pnode.create(resourceType,"New "+resourceType);
                    // create but do not refresh which starts authentication.
                    // call sync methiod, already in background task: 
                    interactiveEditProperties(newNode,false); 

                }
                catch (VlException e)
                {
                    handle(e);  
                } 
            }

            @Override
            public void stopTask() {} 
        };
        // go background: 
        createTask.startTask(); 
    }

    protected void handle(Throwable t)
    {
        this.browserController.handle(t); 
    }
   
    public void interactiveCopyMoveDrop(final ProxyNode target, 
            final ResourceRef sources[], 
            final boolean isMove)
    {
        // ====
        // Pre: Check CopyDrop type: 
        // ==== 
        boolean vfsDrop=true; 
        
        //VDir supports almost *any* type now . 
        if (target.getResourceRef().getResourceType().equals(VFS.DIR_TYPE)) 
        {   
            vfsDrop=true; 
        }
        else
        {
            vfsDrop=false; 
        }
        
        //
        // Optimized VFS resource only drop, use VFSTransfer Dialogs!
        //
        if (vfsDrop)
        {
            bgDoVFSCopyDrop(target,sources,isMove); 
            return; 
        }
        else
        {
            bgDoAnyCopyDrop(target,sources,isMove); 
        }
    }
    
    private void bgDoAnyCopyDrop(final ProxyNode targetPNode, 
            final ResourceRef[] sources,
            final boolean isMove)
    {
        // =======================
        // Any Drop: 
        // =======================
        
        ActionTask task = new ActionTask(browserController, "CopyMoveDrop #"+sources.length+" sources to:"+targetPNode.getVRL() )
        {
            public void doTask() 
            {
                // once started: disconnect from browserController : 
                this.setTaskSource(TransferWatcher.getBackgroundWatcher());
                ProxyNode resolvedTarget=targetPNode;
                
                ITaskMonitor monitor = this.getTaskMonitor(); 
                // Default Copy Move Drop!
                
                try
                {
                    // ===========
                    // Resolve Target: Drop on target not link itself 
                    // ===========
                    
                    if (targetPNode.isResourceLink())
                    {
                        resolvedTarget=targetPNode.getTargetPNode();
                        if (resolvedTarget==null)
                            resolvedTarget=targetPNode; 
                    }

                    // =======
                    // OPEN !
                    // =======
                    
                    // Parent = Composite Node  
                    VNode destNode=UIGlobal.getVRSContext().openLocation(resolvedTarget.getVRL()); 
                    
                    if (destNode instanceof VComposite)
                    {
                        VComposite destCNode;
                        destCNode=(VComposite)destNode;
                        for (ResourceRef ref:sources)
                        {
                            VNode sourceNode=UIGlobal.getVRSContext().openLocation(ref.getVRL());  
                            
                            String actionStr=(isMove)?"Move":"Copy";
                            monitor.logPrintf("Performing "+actionStr+" of '"+sourceNode.getName()+"' to: "+destNode.getHostname()+"\n");
                            
                            // Synchronous copy/move (but in background) !
                            VNode resultNode=destCNode.addNode(sourceNode,null,isMove);
                            // 
                            
                            UIGlobal.getVRSContext().fireEvent(
                                    ResourceEvent.createChildAddedEvent(destNode.getVRL(),resultNode.getVRL()));
                            
                            if (isMove)
                                UIGlobal.getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(sourceNode.getVRL())); 
                        }
                    }
                    else
                    {
                        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Cannot perform drop on: target destination:"+destNode);
                    }
                }
                catch (Throwable t)
                {
                    handle(t);
                }
            }

            @Override
            public void stopTask()
            {
                
            }
        };
       
       // Show default task monitor: 
       task.startTask();
       nl.uva.vlet.gui.panels.monitoring.TaskMonitorDialog.showTaskMonitorDialog(this.browserController.getFrame(), task,500);
       
    }
    
    private void bgDoVFSCopyDrop(final ProxyNode targetPNode, 
            final ResourceRef[] sources,
            final boolean isMove)
    {
        ActionTask task = new ActionTask(browserController, "CopyMoveDrop #"
                +sources.length+" sources to:"+targetPNode.getVRL() )
        {
            private VFSTransfer vfsTransfer=null;

            public void doTask() 
            {
                try
                {
                    ProxyNode resolvedTarget=targetPNode;
                    
                    // drop on target not link itself 
                    if (targetPNode.isResourceLink())
                    {
                        resolvedTarget=targetPNode.getTargetPNode();  
                    }
                    
                    // ====
                    // Once started: disconnect from browserController, this allow this 
                    // thread to wait in background and monitor the transfer. 
                    // ====
                    this.setTaskSource(TransferWatcher.getBackgroundWatcher());
                    ITaskMonitor monitor = this.getTaskMonitor(); 
                    
                    VRL targetDirVrl=resolvedTarget.getVRL(); // PARENT of dropped sources 
                    VRL vrls[]=ResourceRef.getVRLs(sources); 
                    
                    VRSTransferManager transferMngr = UIGlobal.getVRSContext().getTransferManager();
                    // Delegate to TransferManager:
                    CopyInteractor copi=new CopyInteractor();
                    if (vrls.length>1)
                    {
                        vfsTransfer=transferMngr.asyncMultiCopyMove(monitor,
                                vrls,
                                targetDirVrl,
                                isMove,
                                copi);
                    }
                    else
                    {
                        // Single Drop!
                        vfsTransfer=transferMngr.asyncCopyMove(monitor,
                                vrls[0],
                                targetDirVrl, 
                                null,
                                isMove,
                                copi); 
                    }
                    //nl.uva.vlet.gui.panels.monitordialog.TaskMonitorDialog.showTaskMonitorDialog(browserController,transfer); 
                
                    TransferMonitorDialog dialog = nl.uva.vlet.gui.panels.monitoring.TransferMonitorDialog.showTransferDialog(browserController,vfsTransfer,1000);
                    copi.setDialog(dialog); 
                    
                    //
                    // Wait In Background!!!
                    //
                    vfsTransfer.waitForCompletion(); 
                    //
                    //
                    //
                    
                }
                catch (Throwable t)
                {
                    handle(t);
                }
            }

            @Override
            public void stopTask()
            {
                if (vfsTransfer!=null)
                {
                    vfsTransfer.setIsCancelled(); 
                }
            }
        };
        
       task.startTask();
      
    }  
    
    /** check destination and optionall ask the user what to do with a CopyDrop */ 
    protected boolean checkValidTarget(ProxyNode target, 
                ResourceRef source,
                boolean isMove,
                StringHolder optNewName,
                BooleanHolder overWriteExisting,
                BooleanHolder overWriteAll,
                BooleanHolder cancelAll)
        throws VlException 
    {
        
        // Cleanup ? 
        
        if ((cancelAll!=null) && (cancelAll.value==true)) 
            return false; // Cancel All has already been selected!
            
        if ((source==null) || (source.getVRL()==null))
        {
            Global.errorPrintf(this,"***ERROR: NULL pointer checkCopyMoveDrop:%s\n",this); 
            return false; 
        }
        // cannot handle non composite node (yet) !
        if (target.isComposite() == false)
        {
            UIGlobal.displayErrorMessage(Messages.M_could_not_add_resource); 
            
            return false; 
        }
        // check dropped sources:
        
        if (target.getVRL().compareTo(source.getVRL()) == 0)
        {
            SimpleDialog.displayErrorMessage(Messages.M_resource_cannot_be_added_to_itself);
            return false;
        }
            
        if (source.getVRL().isParentOf(target.getVRL()))
        {
            SimpleDialog.displayErrorMessage(Messages.M_resource_cannot_be_added_to_itself_or_its_childs);
            return false; 
        }
        
        ProxyNode resolvedTarget=target; 
        
        if (target.isResourceLink())
        {
            resolvedTarget=target.getTargetPNode();  
        }
        
        overWriteExisting.value =false; 
        optNewName.value=null;
        final ProxyNodeFactory proxyFactory=ProxyNode.getProxyNodeFactory(); 
        
        String basename=source.getVRL().getBasename();
        {
            // when copying a resource node, the implementation link is used.
            ProxyNode sourcePNode=proxyFactory.getFromCache(source.getVRL()); 

            if ((sourcePNode!=null) && (sourcePNode.isLogicalNode()))
            {
                // use file name of Storage Location instead of ResourceDescription 
                basename=uriname(sourcePNode.getName());  
                 
            }
        }
        
        ProxyNode child=resolvedTarget.getChild(basename);
        
        if (child!=null)
        {
            // overwrite all! 
            if ((overWriteAll!=null) && (overWriteAll.value==true))
            {
                // update for this copy action 
                overWriteExisting.value = true;
                return true;
            }
            
            boolean noOverwrite=false; 
            
            // check whether source is destination; 
            
            if (child!=null) 
                if (source.getVRL().compareTo(child.getVRL())==0)
                    noOverwrite=true; // source is destination ! 
            
            //boolean ans = nl.uva.vlet.gui.dialogs.SimpleDialog.askConfirmation(
            //        Messages.Q_overwrite_existing_resource, false);
            
            String OPT_CREATE_COPY="Create copy";
            String OPT_OVERWRITE="Overwrite";
            String OPT_OVERWRITE_ALL="Overwrite all";
            String OPT_CANCEL="Cancel";
            String OPT_CANCEL_ALL="Cancel all";
            String result=null;
            
            String actionStr=(isMove)?"moving":"copying"; 
            
            String msgStr=""; 
            String options[]=new String[5];
            int index=0; 
            
            // construct dialog: 
            options[index++]=OPT_CREATE_COPY;
            
            if (noOverwrite==false)
            {
                msgStr= "Resource exists: "+source.getVRL().getBasename()+"\n" 
                    +"Destination location ="+target.getVRL()+" \n" 
                    +"Replace it, create a Copy or Cancel ?";
                
                options[index++]=OPT_OVERWRITE;
                
                if (overWriteAll!=null) 
                    options[index++]=OPT_OVERWRITE_ALL; 
            }
            else
            {
                msgStr= "Cannot copy resource to itself: "+source.getVRL().getBasename()+"\n" 
                +"Do you want to create a Copy or Cancel ?";
            }
            // add multiple options: 
            
            
            options[index++]=OPT_CANCEL;
                
            if (cancelAll!=null) 
                options[index++]=OPT_CANCEL_ALL; 
                
            // MODEL DIALOG
            result = SimpleNButtonDialog.showDialog(null,"Choose Option",msgStr,
                        options);
            
            //        Messages.Q_overwrite_existing_resource, false);
            if ((result==null) || (result.compareTo(OPT_CANCEL)==0) ) 
                return false;
            
            // Cancel All ? 
            if (cancelAll!=null)
                if (result.compareTo(OPT_CANCEL_ALL)==0) 
                {
                    cancelAll.value=true;
                    return false;
                }
            
            // OverWrite All ? 
            if (overWriteAll!=null)
                if (result.compareTo(OPT_OVERWRITE_ALL)==0) 
                {
                    // overwrite this and others: 
                    overWriteExisting.value = true;
                    overWriteAll.value=true;
                    return true;
                }
            
            if (result.compareTo(OPT_CREATE_COPY)==0)
            {
                optNewName.value="Copy Of "+basename;
                
                optNewName.value= JOptionPane.showInputDialog("Specify new name", optNewName.value);
                
                if (optNewName.value==null)
                    return false; // abort
                else
                    return true; 
            }
            else if (result.compareTo(OPT_OVERWRITE)==0)
            {
                overWriteExisting.value = true;
                return true; 
            }
        }
        
        return true;
    } 
  
    private String uriname(String name)
    {
        return name.replaceAll("[/\\;:!#$%*]*","_"); 
    }

    public void interactiveDoProxyRefresh(VAttribute[] attrs)
    {
        boolean val=SimpleDialog.askConfirmation("Your proxy has changed. Current connections need to be reset "
        		+"for the changes to take effect.\nProceed?"); 
        
        if (val)
            this.browserController.resetVRS();  
    }

   
    
}
