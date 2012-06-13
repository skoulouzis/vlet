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
 * $Id: ReplicaController.java,v 1.5 2011-06-07 15:15:08 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:08 $
 */
// source: 

package nl.uva.vlet.gui.viewers.grid.replicaviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.editors.ResourceEditor;
import nl.uva.vlet.gui.panels.list.StatusStringListField;
import nl.uva.vlet.gui.panels.monitoring.TaskMonitorDialog;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTable;
import nl.uva.vlet.gui.panels.resourcetable.ResourceTableModel.RowData;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.vbrowser.VBrowserFactory;
import nl.uva.vlet.gui.widgets.NavigationBar;
import nl.uva.vlet.gui.widgets.NavigationBar.NavigationAction;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

public class ReplicaController implements ActionListener, ListSelectionListener, ListDataListener
{
    private ReplicaEditor editor = null;

    private ActionTask fetchTask = null;

    private boolean showTransportURIs = false;

    private boolean showPreferredSEsOnly = false;

    private ReplicaManager replicaUtil = null;

    private ActionTask updateTask = null;

    private StringList storageElements = new StringList(); // empty!

    private long lfcFileSize = 0;

    private ActionTask lfcUpdateTask;

    public ReplicaController(ReplicaEditor editor)
    {
        this.editor = editor;
        this.replicaUtil = new ReplicaManager(UIGlobal.getVRSContext());
    }

    public VRL getVRL()
    {
        return this.editor.getVRL();
    }

    public void setVRL(VRL loc)
    {
        this.editor.setVRL(loc);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        String actionStr = e.getActionCommand();
        String strs[] = actionStr.split(":");
        String cmdStr = strs[0];
        // optional argument
        String arg1 = null;
        if (strs.length > 1)
            arg1 = strs[1];

        NavigationAction navAction = NavigationBar.getNavigationCommand(cmdStr);

        if (navAction != null)
        {
            switch (navAction)
            {
                case LOCATION_CHANGED:
                {
                    String txt = editor.getLocationText();
                    try
                    {
                        this.updateLocation(new VRL(txt));
                    }
                    catch (VRLSyntaxException e1)
                    {
                        handle(e1);
                    }
                }
                case REFRESH:
                {
                    this.doRefresh();
                }
            }
        }
        else if (StringUtil.equals(cmdStr, ReplicaEditor.ACTION_UPDATE_REPLICAS))
        {
            applyChanges();

        }
        else if (StringUtil.equals(cmdStr, ReplicaEditor.ACTION_REFRESH_REPLICAS))
        {
            this.doRefresh();
        }
        else if (StringUtil.equals(cmdStr, ReplicaEditor.ACTION_CLOSE))
        {
            stop();
            dispose();
        }
        else if (StringUtil.equals(cmdStr, ReplicaEditor.ACTION_ADD_SE_FROM_SELIST))
        {
            addNewSEfromSEList();
        }
        else if (StringUtil.equals(cmdStr, ReplicaEditor.ACTION_REMOVE_SE_FROM_SELIST))
        {
            removeSEfromReplicaList();
        }
        else if ((arg1 != null) && (StringUtil.equals(cmdStr, ReplicaPopupMenu.DELETE)))
        {
            deleteReplica(arg1, true);
        }
        else if ((arg1 != null) && (StringUtil.equals(cmdStr, ReplicaPopupMenu.UNREGISTER)))
        {
            unregisterReplica(arg1, true);
        }
        else if ((arg1 != null) && (StringUtil.equals(cmdStr, ReplicaPopupMenu.KEEP)))
        {
            deleteReplica(arg1, false);
        }
        else if ((arg1 != null) && (StringUtil.equals(cmdStr, ReplicaPopupMenu.OPEN_PARENT)))
        {
            openParent(arg1);
        }
        else if ((arg1 != null) && (StringUtil.equals(cmdStr, ReplicaPopupMenu.SHOW_PROPERTIES)))
        {
            showReplicaProps(arg1);
        }
        else if (StringUtil.equals(cmdStr, ReplicaPopupMenu.DELETE_SELECTION))
        {
            deleteSelection(true);
        }
        else if (StringUtil.equals(cmdStr, ReplicaPopupMenu.KEEP_SELECTION))
        {
            deleteSelection(false);
        }
    }

    public ResourceTable getTable()
    {
        return this.editor.getReplicaTable();
    }

    private void deleteSelection(boolean delete)
    {
        int rows[] = this.getTable().getSelectedRows();
        for (int row : rows)
        {
            String host = this.getModel().getRowKey(row);
            deleteReplica(host, delete);
        }
    }

    private void deleteReplica(String host, boolean delete)
    {
        getModel().markToBeDeleted(host, delete);
        this.storageElements.add(host);
    }

    private void unregisterReplica(String host, boolean toggle)
    {
        getModel().markToBeUnregistered(host, toggle);
        this.storageElements.add(host);
    }

    private void applyChanges()
    {
        doUpdateAll();
    }

    private void doUpdateAll()
    {
        String newSEs[] = this.getModel().getNewSEs();
        String deleteSEs[] = this.getModel().getDeletedSEs();
        String unregSEs[] = this.getModel().getUnregisteredSEs();

        bgUpdateReplicas(newSEs, deleteSEs, unregSEs);

    }

    private void bgUpdateReplicas(final String[] newSEs, final String[] deleteSEs, final String[] unregSEs)
    {
        int todo = 0;
        if (newSEs != null)
            todo += newSEs.length;
        if (deleteSEs != null)
            todo += deleteSEs.length;
        if (unregSEs != null)
            todo += unregSEs.length;

        if (editor.getUpdateSizeFromReplica())
            todo++;

        if (todo <= 0)
            return;

        // pre emptive abort !
        if ((this.updateTask != null) && (updateTask.isAlive()))
        {
            this.updateTask.signalTerminate();
        }
        final int fTodo = todo;

        this.updateTask = new ActionTask(null, "Update Replicas:" + this.editor.getVRL())
        {
            public void doTask()
            {
                if (this.isCancelled() == true)
                    return;

                setBusy(true);
                editor.getUpdateReplicasBut().setEnabled(false);

                ITaskMonitor monitor = this.getTaskMonitor();

                monitor.startTask("Updating replicas", fTodo);
                int done = 0;

                // First Unregister before adding new Replica's!
                // This to eliminate bogus replica's
                for (String se : unregSEs)
                {
                    monitor.logPrintf("Unregistering Replica at:" + se + "\n");

                    if (monitor.isCancelled())
                    {
                        monitor.logPrintf("Cancelled!");
                        return;
                    }
                    try
                    {
                        replicaUtil.unregisterReplica(monitor, se);
                    }
                    catch (VlException e)
                    {
                        monitor.logPrintf("***Exception:" + e + "\n");
                    }
                    monitor.updateWorkDone(++done);
                }

                // Now Replicate
                for (String se : newSEs)
                {
                    if (this.isCancelled() == true)
                        break;

                    monitor.logPrintf("Adding replica at:" + se + "\n");
                    if (monitor.isCancelled())
                    {
                        monitor.logPrintf("Cancelled!");
                        return;
                    }

                    try
                    {
                        replicaUtil.addReplica(monitor, se);
                    }
                    catch (VlException e)
                    {
                        monitor.logPrintf("***Exception:" + e + "\n");
                    }
                    monitor.updateWorkDone(++done);
                }

                // Delete Replicas
                for (String se : deleteSEs)
                {
                    monitor.logPrintf("Deleting Replica at:" + se + "\n");

                    if (monitor.isCancelled())
                    {
                        monitor.logPrintf("Cancelled!");
                        return;
                    }
                    try
                    {
                        replicaUtil.deleteReplica(monitor, se);
                    }
                    catch (VlException e)
                    {
                        monitor.logPrintf("***Exception:" + e + "\n");
                    }
                    monitor.updateWorkDone(++done);
                }

                try
                {
                    boolean val = checkUpdateLFCSizeFromReplicas();
                    if (val)
                        monitor.logPrintf("LFC File Size has been updated!\n");
                    monitor.updateWorkDone(++done);
                }
                catch (Exception e)
                {
                    handle(e);
                }

                monitor.endTask("Updating replicas");

                setBusy(false);
                editor.getUpdateReplicasBut().setEnabled(true);
                doRefresh();
            }

            @Override
            public void stopTask()
            {
                // monitor.isCancelled() is True !
            }
        };

        this.updateTask.startTask();

        TaskMonitorDialog.showTaskMonitorDialog(null, updateTask, 0);
    }

    private boolean checkUpdateLFCSizeFromReplicas() throws VlException
    {
        boolean update = (getModel().replicaLengthsEqual(lfcFileSize) == false);

        if (update && this.editor.getUpdateSizeFromReplica())
        {
            // use biggest size:
            long len = getModel().getMaxReplicaLength();
            replicaUtil.updateLFCFileSize(len);
            this.editor.setLFCFileSize("" + len + "(Updated!)", true);
            this.editor.setUpdateSizeFromReplica(false);
            return true;
        }
        return false;
    }

    public ReplicaDataModel getModel()
    {
        return (ReplicaDataModel) this.editor.getReplicaTable().getModel();
    }

    protected void addNewSEfromSEList()
    {
        StatusStringListField seListField = editor.getSEHostListField();
        String ses[] = seListField.getSelectedValues();

        // only add if not added, or restore 'Delete' status
        for (String se : ses)
        {
            this.getModel().addNewReplicaToBe(se);
            storageElements.remove(se);
        }

        // remove storage elements which already have replica:
        uiUpdateSEHostList(storageElements);
    }

    protected void removeSEfromReplicaList()
    {
        // instead of deleting mark as to be removed: set status to "Removed"
        String[] selSEs = this.editor.getReplicaHostSelection();

        StatusStringListField listField = editor.getReplicaHostListField();
        for (String se : selSEs)
        {
            // new entry -> delete from list
            if (listField.isNew(se))
            {
                listField.removeElement(se);
            }
            else
            {
                listField.setStatus(se, StatusStringListField.STATUS_REMOVED);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        ; //
    }

    private void handle(Throwable e)
    {
        UIGlobal.showException(e);
    }

    public void doRefresh()
    {
        updateLocation(getVRL());
    }

    public void updateLocation(VRL loc)
    {
        this.editor.setVRL(loc);
        this.editor.setLocationText(loc.toString());
        this.replicaUtil.setVRL(loc);

        // first fetch LFC meta data
        updateLFCMetaData();
        bgGetReplicas(loc);
        bgGetStorageElements(this.showPreferredSEsOnly);

    }

    private void updateLFCMetaData()
    {
        try
        {
            // Check LFC File Size:
            lfcFileSize = this.replicaUtil.getFileSize();
            this.editor.setLFCFileSize("" + lfcFileSize, true);
        }
        catch (Exception e)
        {
            this.editor.setLFCFileSize("<Error!>", false);
            handle(e);
        }
    }

    private void bgGetStorageElements(boolean prefSEsOnly)
    {
        VRSContext ctx = UIGlobal.getVRSContext();
        String vo = ctx.getVO();

        StringList SElist;
        try
        {
            // no need for BG
            SElist = replicaUtil.getStorageAreasForVo(vo);
            this.storageElements = SElist;
            this.uiUpdateSEHostList(SElist);
        }
        catch (Throwable e)
        {
            handle(e);
        }
    }

    private void bgGetReplicas(final VRL loc)
    {
        if (StringUtil.isEmpty(loc))
        {
            editor.clearAll();
            return;
        }

        setViewerTitle("Reading Replicas:" + loc.getHostname() + ":" + loc.getBasename());
        final ReplicaDataModel dataModel = getModel();

        fetchTask = new ActionTask(null, "fetching replicas:" + loc)
        {
            public void doTask()
            {
                setBusy(true);
                editor.getRefreshReplicasBut().setEnabled(false);
                setViewerTitle("Fetching Replicas:" + loc.getHostname() + ":" + loc.getBasename());

                try
                {
                    StringList repSEs = new StringList();

                    VRL vrls[] = replicaUtil.getReplicaVRLs();

                    dataModel.clearData();

                    for (VRL repVrl : vrls)
                    {
                        dataModel.addReplica(repVrl);
                        storageElements.remove(repVrl.getHostname());
                    }

                    // remove storage elements which already have replica:
                    uiUpdateSEHostList(storageElements);

                    setViewerTitle("Viewing Replicas:" + loc.getHostname() + ":" + loc.getBasename());

                    for (VRL repVrl : vrls)
                    {
                        // check stop/cancel
                        if (this.isCancelled() == true)
                        {
                            Global.warnPrintf(this, "Update Task stopped!\n");
                            break;
                        }

                        try
                        {
                            boolean checksum = getModel().hasHeader(VAttributeConstants.ATTR_CHECKSUM);
                            VAttributeSet attrs = replicaUtil.getReplicaAttributes(repVrl, checksum);
                            dataModel.setReplicaAttributes(repVrl, attrs);
                        }
                        catch (Exception e)
                        {
                            dataModel.setReplicaException(repVrl, e);
                            handle(e);
                        }
                    }
                }
                catch (Exception e)
                {
                    setViewerTitle("Error Reading Replicas:" + loc.getHostname() + ":" + loc.getBasename());
                    handle(e);
                }

                // check stop/cancel
                if (this.isCancelled() == true)
                    return;

                editor.getRefreshReplicasBut().setEnabled(true);
                setBusy(false);

                checkReplicaInfo();

            }

            @Override
            public void stopTask()
            {
            }
        };

        // go background:
        fetchTask.startTask();
    }

    private void checkReplicaInfo()
    {
        ReplicaInfo infos[] = this.getModel().getReplicaInfos();

        if ((infos == null) || (infos.length <= 0))
            return;

        int index = 0;
        boolean error = false;
        String statusStr = "";
        long prevLen = -1;

        for (ReplicaInfo info : infos)
        {
            // Only check confirmed replica info:

            if (info.hasError() == false)
            {
                long size = info.getLength();

                if (size != this.lfcFileSize)
                {
                    error = true;
                    statusStr += "Replica File Size at storage element:" + info.getHostname()
                            + " doesn't match the LFC File Size. Replica size:" + size + " <> " + lfcFileSize + "!\n";
                }

                // check between replicas
                if (prevLen >= 0)
                {
                    if (size != prevLen)
                        statusStr += "Replica File Size at:" + info.getHostname() + " also differs from OTHER Replica."
                                + "Replica's size:" + size + " <> " + prevLen + " !\n";
                }

                prevLen = size;
            }
        }

        if (error)
        {
            // Must Update size:
            this.editor.setUpdateSizeFromReplica(true);
            this.showError("One or more Replica file sizes don't match the reported LFC File Size.\n" + statusStr
                    + " Delete the faulty replica(s) and select:'" + ReplicaEditor.UPDATE_SIZE_FROM_REPLICA + "'");
            this.editor.setLFCFileSize(this.lfcFileSize + " <Mismatch!>", false);
        }
        else
        {
            // disable: no need to update it:
            this.editor.setUpdateSizeFromReplica(false);
        }
    }

    protected void uiUpdateReplicaInfo(final ArrayList<ReplicaInfo> repInfos)
    {
        if (UIGlobal.isGuiThread() == false)
        {
            Runnable runT = new Runnable()
            {
                public void run()
                {
                    uiUpdateReplicaInfo(repInfos);
                }
            };

            SwingUtilities.invokeLater(runT);
            return;
        }
        ;

        StringList repHosts = new StringList();

        if (repInfos != null)
        {
            for (ReplicaInfo info : repInfos)
            {
                repHosts.add(info.getVRL().getHostname());
            }
        }
        editor.getReplicaTable().refreshAll();
        // editor.updateReplicaAttributes(repInfos);
    }

    protected void uiUpdateSEHostList(final StringList ses)
    {
        if (UIGlobal.isGuiThread() == false)
        {
            Runnable runT = new Runnable()
            {
                public void run()
                {
                    uiUpdateSEHostList(ses);
                }
            };

            SwingUtilities.invokeLater(runT);
            return;
        }
        ;

        this.editor.updateSEHostList(ses, true);
    }

    private void setViewerTitle(String str)
    {
        this.editor.setViewerTitle(str);
    }

    private int busyLevel = 0;

    private synchronized void setBusy(boolean val)
    {
        if (val)
            busyLevel++;
        else
            busyLevel--;

        if (busyLevel <= 0)
            busyLevel = 0;

        this.editor.setBusy(busyLevel > 0);
    }

    public void doMethod(String methodName, ActionContext actionContext)
    {
        // one method only: Viewer will already be triggered.
    }

    public void showError(String message)
    {
        UIGlobal.showMessage("Error", message);
    }

    public void stop()
    {
        if (this.fetchTask != null)
        {
            this.fetchTask.signalTerminate();
            this.fetchTask = null; // block further stop() calls
        }

        if (this.updateTask != null)
        {
            this.updateTask.signalTerminate();
            this.updateTask = null; // block further stop() calls
        }
    }

    public void dispose()
    {
        stop();

        if (editor != null)
        {
            this.editor.dispose();
            this.editor = null;
        }

    }

    @Override
    public void contentsChanged(ListDataEvent e)
    {
        updateColumndata();
    }

    @Override
    public void intervalAdded(ListDataEvent e)
    {
        updateColumndata();
    }

    @Override
    public void intervalRemoved(ListDataEvent e)
    {
        // Don't have to do anything. Table View has removed column, keep data.
        //
        // debug("Header ListDataEvent:"+e);
        // updateColumndata();
    }

    private ActionTask attrUpdateTask = null;

    /** Header/Column layout has changed: Either rearranged or columns inserted */
    private void updateColumndata()
    {
        // Terminate previous -> Pre Emptive Abort [I] !
        if ((attrUpdateTask != null) && (attrUpdateTask.isAlive()))
            this.attrUpdateTask.signalTerminate();

        attrUpdateTask = new ActionTask(null, "Update Column Data")
        {
            boolean mustStop = false;

            public void doTask()
            {
                try
                {
                    ReplicaDataModel model = getModel();
                    // only update added columns:
                    String headers[] = model.getHeaders();

                    VRL vrls[] = model.getReplicaVRLs();
                    for (VRL vrl : vrls)
                    {
                        // [I] Pre Emptive abort !
                        if (mustStop == true)
                            break; // throw new
                                   // VlInterruptedException("Interrupted");

                        RowData row = model.getRow(vrl.getHostname());
                        String[] oldAttrs = row.getAttributeNames();
                        StringList attrs = new StringList(headers);
                        attrs.remove(oldAttrs);
                        if (attrs.size() > 0)
                        {
                            try
                            {
                                VAttributeSet newAttrs = replicaUtil.getReplicaAttributes(vrl, attrs);
                                model.setReplicaAttributes(vrl, newAttrs);
                            }
                            catch (VlException e)
                            {
                                model.setReplicaException(vrl, e);
                                handle(e);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    handle(e);
                }
            }

            public void stopTask()
            {
                mustStop = true;
            }
        };

        attrUpdateTask.startTask();
    }

    public void openParent(String hostname)
    {
        final VRL repVrl = this.getModel().getReplicaVRLofSE(hostname);
        if (repVrl == null)
            return;

        ActionTask task = new ActionTask(null, "show properties:" + repVrl)
        {
            public void stopTask()
            {
            }

            public void doTask()
            {
                try
                {
                    VRL parentVrl = repVrl.getParent();
                    // Prefetch Node!
                    ProxyNode pnode = ProxyNode.getProxyNodeFactory().openLocation(parentVrl);
                    VBrowserFactory.getInstance().createBrowser(parentVrl); // open
                                                                            // in
                                                                            // new
                                                                            // window
                }
                catch (VlException e)
                {
                    handle(e);
                }
            }
        };

        task.startTask();
    }

    public void showReplicaProps(String hostname)
    {
        final VRL repVrl = this.getModel().getReplicaVRLofSE(hostname);
        if (repVrl == null)
            return;

        ActionTask task = new ActionTask(null, "show properties:" + repVrl)
        {
            public void stopTask()
            {
            }

            public void doTask()
            {
                try
                {
                    ProxyNode pnode = ProxyNode.getProxyNodeFactory().openLocation(repVrl);
                    ResourceEditor.editAttributes(pnode);
                }
                catch (VlException e)
                {
                    handle(e);
                }
            }
        };

        task.startTask();
    }
}
