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
 * $Id: NodeTableProducer.java,v 1.13 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */
// source: 

package nl.uva.vlet.gui.table;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxymodel.ProxyDataProducer;
import nl.uva.vlet.gui.proxynode.ViewNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.EventType;
import nl.uva.vlet.vrs.ResourceEvent;

/**
 * Produces table data. Todo:Use tablemodel and update model instead of creation
 * complete table each time.
 * 
 */
public class NodeTableProducer extends ProxyDataProducer implements TableDataProducer, ProxyResourceEventListener
{

    // ===

    private ProxyNode rootNode = null;

    /** ProxyNodes shown in table panel */
    // private Vector<ProxyNode> proxyNodes = null;

    private ActionTask fetchTask = null;

    private TablePanel tablePanel;

    private String[] attributeNames;

    // private Presentation presentation;

    // ===
    // Constructors/Initializers
    // ===

    private synchronized void init(TablePanel panel)
    {
        this.tablePanel = panel;
        ProxyVRSClient.getInstance().addResourceEventListener(this);
    }

    public NodeTableProducer(TablePanel panel, ProxyNodeFactory factory, VRSTableModel model)
    {
        super(panel.getMasterBrowser(), factory, model.getViewModel());

        init(panel);
    }

    private MasterBrowser getMasterBrowser()
    {
        return tablePanel.browserController;
    }

    public void backgroundCreateTable()
    {
        // clear
        if (rootNode == null)
        {
            tablePanel.removeAll();
            return;
        }

        // Header + Body string

        if (rootNode.isComposite())
        {
            // send request for children, setChilds will be called...
            backgroundUpdateChilds();
        }
    }

    protected void backgroundUpdateChilds()
    {
        bgGetChildsFor(rootNode.getVRL());
    }

    synchronized public void setChilds(ProxyNode nodes[], boolean append)
    {
        createTable1(this.rootNode, nodes);
    }

    /** Create new table for the specified ProxyNodes */
    protected synchronized void createTable1(ProxyNode rootNode, ProxyNode nodes[])
    {
        //
        // Todo: Use TableModel and let table model update Table !
        //

        this.rootNode = rootNode;

        if (attributeNames == null)
        {
            attributeNames = getPresentation().getChildAttributeNames();
        }

        int rows = 0;

        if (nodes != null)
            rows = nodes.length;

        Presentation pres = getPresentation();
        String names[] = pres.getChildAttributeNames();

        // no custom names, use all:

        if (names == null)
        {
            names = this.getAllHeaderNames();
            pres.setChildAttributeNames(names);// update with default !
        }

        int columns = names.length;

        String headers[] = new String[names.length];

        for (int i = 0; i < columns; i++)
        {
            headers[i] = names[i];
        }

        //
        // be robuust: scan array for null entries:
        //
        for (int i = 0; i < rows; i++)
        {
            if (nodes[i] == null)
                rows--;
        }

        Object body[][] = new Object[rows][];

        int i = 0;

        if (nodes != null)
            for (ProxyNode node : nodes)
            {
                if (node == null)
                    continue;

                body[i] = procuceDefaultRowBody(node, names);

                i++;
            }

        uiCreateTable2(body, headers, nodes);
    }

    // Produce default Row Data from ProxyNode:
    protected Object[] procuceDefaultRowBody(ProxyNode node, String[] names)
    {
        Object bodyRow[] = new Object[names.length];

        for (int j = 0; j < names.length; j++)
        {
            // already fill in name and icon:
            if (names[j].compareTo(VAttributeConstants.ATTR_ICON) == 0)
            {
                bodyRow[j] = node.getDefaultIcon(16, false);
            }
            // fill in name and icon:
            else if (names[j].compareTo(VAttributeConstants.ATTR_NAME) == 0)
            {
                bodyRow[j] = node.getName();
            }
            else
            {
                // fill in empty for now
                bodyRow[j] = null;
            }
        }

        return bodyRow;
    }

    // Update TablePanel during UI thread
    protected void uiCreateTable2(final Object[][] body, final String[] headers, final ProxyNode[] rowobjects)
    {
        // ===
        // Must update during ui thread:
        // ===

        if (UIGlobal.isGuiThread() == false)
        {
            debugPrintf("uiCreateTable: (IIa) is not gui invoke later.\n");
            Runnable createTask = new Runnable()
            {
                public void run()
                {
                    uiCreateTable2(body, headers, rowobjects);
                }

            };

            UIGlobal.swingInvokeLater(createTask);
            return;
        }

        tablePanel.uiCreateTable(body, headers, rowobjects);

        // go background again: update atttributes:

        asyncUpdateTableNodeAttributes();
    }

    protected void asyncUpdateTableNodeAttributes()
    {
        debugPrintf("asyncUpdateNodeAttributes:%s\n", rootNode);

        final ProxyNode viewNode = this.rootNode;
        final String attrNames[] = getModelHeaderNames();

        // stop previous fetch task !!!

        if (this.fetchTask != null)
        {
            this.fetchTask.stopTask();
        }

        this.fetchTask = new ActionTask(getMasterBrowser(), "TablePanel.UpdateAttributes:" + this.rootNode.getVRL())
        {
            private Thread thisThread = null;

            protected void doTask()
            {
                thisThread = Thread.currentThread();
                int numExceptions = 0;

                /**
                 * Note: by using Object instead of string the .toString()
                 * method will be called on the object, giving the Object
                 * control over how it will be render (as string) !
                 */

                int rows = getNrOfRows();
                int columns = attrNames.length;
                VRSTableModel model = getTableModel();

                String header[] = new String[attrNames.length];

                for (int i = 0; i < columns; i++)
                {
                    header[i] = attrNames[i];
                }

                long prevMillis = 0;

                ProxyNode[] nodes = model.getRowObjects();

                // preemptive populate by check the running thread also !
                for (int i = 0; (i < nodes.length) && (thisThread != null); i++)
                {
                    ProxyNode node = nodes[i];
                    // TermGlobal.errorPrintln(this,"fetching attributes node #"+i+"="+node);

                    debugPrintf("fetching attributes node #%d=%s\n", i, node);

                    VAttribute attrs[] = null;

                    if (node == null)
                        continue; // arg darn null nodes

                    try
                    {
                        // for repainting
                        boolean update = false;
                        // merge repaints into once a in while
                        long millis = System.currentTimeMillis();

                        // use synchronized method, we are already in seperate
                        // subthread:
                        updateNodeAttributes(node, attrNames);

                        if ((millis - prevMillis) > 1000)
                        {
                            update = true;
                            prevMillis = millis;
                        }

                    }
                    catch (Exception e)
                    {
                        // Browser Overload Protection !
                        numExceptions++;

                        if (numExceptions < 10)
                            getMasterBrowser().handle(e);
                        else
                        {
                            errorPrintf( "To many exceptions:%s\n", e);
                        }
                    }
                }

                this.thisThread = null;

            }

            @Override
            public void stopTask()
            {
                thisThread = null;
            }
        };

        this.fetchTask.startTask();
    }

    protected String[] getModelHeaderNames()
    {
        return getTableModel().getHeadersAsArray();
    }

    private void setNodeAttribute(ProxyNode pnode, VAttribute attr)
    {
        if (attr == null)
            return;

        String name = attr.getName();

        VRSTableModel model = getTableModel();

        // resolve Icon URLs

        Object obj = null;
        String headerName = attr.getName();

        // for now default icons are used.
        if (attr.getName().compareTo(VAttributeConstants.ATTR_ICON) == 0)
        {
            headerName = VAttributeConstants.ATTR_ICON;
            String url = attr.getStringValue();
            // use default icon for now
            obj = pnode.getDefaultIcon(16, false);
        }
        else if (attr.getName().compareTo(VAttributeConstants.ATTR_ICONURL) == 0)
        {
            // Icon and IconUrl have the same column!
            headerName = VAttributeConstants.ATTR_ICON;
            String url = attr.getStringValue();
            obj = UIGlobal.getIconProvider().renderIcon(url, 16);
        }
        else
        {
            obj = attr;
        }

        int col = model.getHeaderIndex(headerName);
        int row = getNodeRowNumber(pnode.getVRL());

        // guard
        if ((col < 0) || (row < 0))
        {
            // happens when access is not correctly synchronized!
            errorPrintf( "setNodeAttribute: Invalid row/column:%d,%d\n", col, row);
            return;
        }

        // this.setValueAt(obj,rowNr,columnNr);
        if (obj != null)
            model.setValueAt(obj, row, col);

    }

    private void setNodeAttributes(ProxyNode pnode, VAttribute attrs[])
    {
        for (VAttribute attr : attrs)
        {
            // when setting multiple attributes do no repaint
            if (attr != null)
                setNodeAttribute(pnode, attr);
        }
    }

    protected void updateNodeAttributes(ProxyNode node, String attrNames[]) throws VlException
    {
        if (attrNames == null)
            return;

        VAttribute[] attrs = node.getAttributes(attrNames);

        // Testing InvokeLater:

        final VAttribute fAttrs[] = attrs;
        final ProxyNode fNode = node;
        // tablePanel.asyncSetNodeAttributes(fNode,fAttrs);

        setNodeAttributes(fNode, fAttrs);
    }

    private int getNrOfRows()
    {
        return this.tablePanel.getRowCount();
    }

    /** search table for row with speficied location */
    public int getNodeRowNumber(VRL loc)
    {
        VRSTableModel model = getTableModel();

        // Happens when access to table is not correctly synchronized!
        // ignore
        if ((model == null) || (loc == null))
            return -1;

        for (int i = 0; i < model.getRowCount(); i++)
        {
            ProxyNode node = (ProxyNode) model.getRowIndexObject(i);

            if ((node != null) && (loc.compareTo(node.getVRL()) == 0))
                return i;
        }

        return -1;
    }

    public void notifyProxyEvent(ResourceEvent e)
    {
        ProxyNodeFactory factory = ProxyNode.getProxyNodeFactory();

        debugPrintf("ResourceEvent:%s\n", e);

        // TermGlobal.messagePrintln(this,"Event:"+e);

        if (rootNode == null)
        {
            debugPrintf("Ignoring event: Table not populated\n");
            return; // empty canvas
        }
        boolean isForRoot = false;
        int rowNr = -1;

        if (this.rootNode.getVRL().compareTo(e.getSource()) == 0)
        {
            isForRoot = true;
            debugPrintf("Event is for root\n");

        }
        else
        {
            if ((rowNr = this.getNodeRowNumber(e.getSource())) < 0)
            {
                debugPrintf("Event is not for one of my childs\n");
                return; // not for me or my childs
            }
        }

        // TermGlobal.messagePrintln(this,"rowNr="+rowNr);
        // TermGlobal.messagePrintln(this,"isForRoot="+isForRoot);

        ProxyNode node = null;
        // must be in cache (else I don't have it anyway)
        node = factory.getFromCache(e.getSource());

        if (node == null)
        {
            node = factory.getFromCache(e.getNewVRL());
        }

        if ((node == null) && (e.getType() != EventType.DELETE))
        {
            Global.warnPrintf(this, "Warning: Node not in cache:%s\n", e.getSource());
        }

        // try
        {

            switch (e.getType())
            {
                case REFRESH:
                {
                    if (isForRoot)
                    {
                        setNode(factory.getFromCache(this.rootNode.getVRL()));
                        backgroundCreateTable();
                    }
                    else
                    {
                        refresh(node);
                    }

                    break;
                }
                case RENAME:
                {
                    if (isForRoot)
                        setNode(factory.getFromCache(e.getNewVRL()));
                    else
                        renameNode(e.getSource(), e.getNewVRL());
                    break;
                }
                case SET_ATTRIBUTES:
                {
                    if (isForRoot == false)
                        setNodeAttributes(node, e.getAttributes());
                    break;
                }
                case SET_CHILDS:
                {
                    // received asychronous set childs from ProxyNode
                    if (isForRoot)
                        this.bgGetNodesFor(this.rootNode.getVRL(), e.getChilds(), true);
                    // else: is for children's children
                    break;
                }
                case DELETE:
                {
                    if (isForRoot)
                    {
                        setNode(null);
                        backgroundCreateTable(); // empty table
                    }
                    else
                    {
                        VRL locs[] = new VRL[1];
                        locs[0] = e.getSource();
                        deleteChilds(locs);
                    }
                    break;
                }
                case CHILDS_ADDED:
                {
                    if (isForRoot)
                        addChilds(e.getChilds());
                    else
                    {
                        debugPrintf("Childs Added event not for me:%s\n", e);
                    }
                    break;
                }
                case CHILDS_DELETED:
                {
                    if (isForRoot)
                        deleteChilds(e.getChilds());
                    break;
                }
                default:
                    break;
            }
        }
        /*
         * catch (VlException ex) { this.rootNode = null; handle(ex); }
         */
    }

    private void refresh(final ProxyNode node)
    {
        final MasterBrowser bc = this.getMasterBrowser();

        // start refresh in background:

        ActionTask refreshTask = new ActionTask(bc, "Refreshing:" + node)
        {
            public void doTask()
            {

                // update attributes from presentation only:
                try
                {
                    String attrNames[] = getPresentation().getChildAttributeNames();
                    updateNodeAttributes(node, attrNames);
                }
                catch (VlException e)
                {
                    bc.handle(e);
                }
            }

            @Override
            public void stopTask()
            {
                // TODO Auto-generated method stub

            }
        };

        refreshTask.startTask();

    }

    protected void renameNode(VRL location, VRL newLocation)
    {
        int row = this.getNodeRowNumber(location);

        // manually update TableModel:
        // update row object:
        ProxyNode newNode = ProxyNode.getProxyNodeFactory().getFromCache(newLocation);
        // model will signal update:
        getTableModel().setRowIndexObject(row, newNode);

        int col = this.getTableModel().getColumnNr(VAttributeConstants.ATTR_NAME);

        if ((col < 0) || (newNode == null))
            return; // name not an attribute in this table

        String name = newNode.getName();
        getTableModel().setValueAt(new VAttribute(VAttributeConstants.ATTR_NAME, name), row, col);
    }

    protected void deleteChilds(VRL[] childs)
    {
        for (VRL childLoc : childs)
        {
            this.getTableModel().removeNode(childLoc);
        }

    }

    private VRSTableModel getTableModel()
    {
        return tablePanel.getVRSTableModel();

    }

    protected void addChilds(VRL[] allChilds)
    {
        VRSTableModel model = getTableModel();

        Vector<VRL> newChilds = new Vector<VRL>();

        // filter out childs:
        for (VRL vrl : allChilds)
        {
            if (model.getRowObjectWithVRL(vrl) == null)
            {
                debugPrintf("Adding new child:%s\n", vrl);
                newChilds.add(vrl);
            }
            else
            {
                debugPrintf("SKipping already existing child:%s\n", vrl);
            }
        }

        // try
        {
            ProxyNode newNodes[] = new ProxyNode[newChilds.size()];

            for (int i = 0; i < newChilds.size(); i++)
            {
                VRL childLoc = newChilds.elementAt(i);
                //
                // do not resolve links!
                newNodes[i] = ProxyNode.getProxyNodeFactory().getFromCache(childLoc);

                // TODO: Remove caching:
                // Should go background and update table model.
                //

                if (newNodes[i] == null)
                {
                    warnPrintf("Warning: addChilds(): New location not in proxy cache cache:%s\n", childLoc);

                    // cache not up to date !
                    // need to reread node's children:
                    // recreate table (will do a asyncGetChilds)

                    this.backgroundCreateTable();
                    return; // exit loop
                }

                String headerNames[] = getModelHeaderNames();
                // producd default row entry. leave attributes for now.
                Object rowBody[] = this.procuceDefaultRowBody(newNodes[i], headerNames);

                // model does the rest:
                model.addNodeRow(newNodes[i], rowBody);
            }
        }
        // update table data:
        asyncUpdateTableNodeAttributes();
    }

    // ===================================================
    // Table Data Producer
    // ==================================================
    /*
     * public void produceColumnData(String name) { }
     * 
     * public void produceRowData(int rownr) { }
     */

    public void storeTable()
    {
    }

    public void setNode(ProxyNode node)
    {

        // TODO: Check stored view/presentation information

        if ((rootNode != null) && (rootNode.equalsType(node) == false))
        {
            attributeNames = null; // clear attributeNames

        }

        this.rootNode = node;
    }

    public ProxyNode getNode()
    {
        return this.rootNode;
    }

    public ViewNode getRootViewItem()
    {
        return ViewNodeFactory.createViewNode(rootNode);
    }

    public void dispose()
    {
        this.rootNode = null;
        ProxyVRSClient.getInstance().removeResourceEventListener(this);
    }

    public String[] getAllHeaderNames()
    {
        // return private array:
        ProxyNode rows[] = getTableModel().getRowObjects();

        StringList allNames = new StringList();

        if ((rows != null) && (rows.length > 0))
        {
            for (ProxyNode row : rows)
            {
                String[] names = row.getAttributeNames();
                allNames.merge(names);
            }
            return allNames.toArray();
        }
        // just return attributes of rootnode
        else
        {
            return rootNode.getAttributeNames();
        }
    }

    public Presentation getPresentation()
    {
        return this.rootNode.getPresentation();

        // current presentation is stored for Scheme/Host/ResourceType
        // return Presentation.getPresentationFor(
        // this.rootNode.getVRL().getScheme(),
        // this.rootNode.getVRL().getHostname(),
        // this.rootNode.getType());
    }

    @Override
    public void updateChildNodesFor(ProxyNode parent, ProxyNode[] childs, boolean cumulative)
    {
        // cumulative=true => append==true.
        if (this.getNode().equals(parent) == false)
        {
            errorPrintf( "Received update NOT for me:%s\n",parent);
            return;
        }

        this.setChilds(childs, cumulative);
    }

    protected void debugPrintf(String format, Object... args)
    {
        Global.debugPrintf(this, format, args);
    }

    protected void warnPrintf(String format, Object... args)
    {
        Global.warnPrintf(this, format, args);
    }
    
    protected void errorPrintf(String format, Object... args)
    {
        Global.errorPrintf(this, format, args);
    }
}
