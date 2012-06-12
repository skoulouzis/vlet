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
 * $Id: MyVLe.java,v 1.6 2011-06-07 14:30:45 ptdeboer Exp $  
 * $Date: 2011-06-07 14:30:45 $
 */
// source: 

package nl.uva.vlet.vrms;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;

import java.io.File;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.ResourceWriteAccessDeniedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VRenamable;

/**
 * MyVLe is the toplevel object for all resources, like My Computer is on
 * Windows. It's childs are configured locations and settings, etc.
 * 
 * @author Piter T. de Boer
 * 
 */
final public class MyVLe extends VCompositeNode implements VEditable, VLogicalResource, VRenamable, VLogicalFolder
{
    // ========================================================================
    // Class Stuff
    // ========================================================================
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(MyVLe.class);
    }

    public static final String MYVLE_TYPE = VRS.MYVLE_TYPE;

    // private static String resourceTypes[]=null;

    // customiable name

    public final static String MYVLE_SUBDIR_NAME = "myvle";

    /**
     * Get "myvle:///" location. This since myvle hasn't got a VRS location
     * factory
     */
    public static VNode openLocation(VRSContext vrs, VRL location) throws VlException
    {
        // instance currently available in VRSContext ? (move?)

        MyVLe myvle = (MyVLe) vrs.getVirtualRoot();
        return myvle.findNode(location, true);
    }

    /** Use static method ! */
    public static MyVLe createVLeRoot(VRSContext vrsContext) throws VlException
    {
        // Must by created using correct VRSContext !
        return new MyVLe(vrsContext);
    }

    /** Extra windows attributes */

    public static String windowsAttributeNames[] =
    { GlobalConfig.PROP_SKIP_FLOPPY_SCAN };

    // ========================================================================
    // instance Stuff
    // ========================================================================

    // private Vector<VRL> rootLocations=new Vector<VRL>();
    /**
     * @uml.property name="rootNodes"
     * @uml.associationEnd multiplicity="(0 -1)"
     *                     elementType="nl.uva.vlet.vrs.LinkNode"
     */
    // private Vector<LogicalResourceNode> rootNodes=null;
    private Vector<VNode> rootNodes = null;

    // under construction: use ResourceFolder as Node container:
    // private ResourceFolder rootGroup=null;

    /**
     * The MyVLe object
     * 
     * @param vrsContext
     *            The VRSContext to use.
     * @throws VlException
     */
    private MyVLe(VRSContext context)
    {
        super(context, new VRL(VRS.MYVLE_SCHEME, null, null));
        // rootGroup=new ResourceFolder(null,context,new
        // VRL(VRS.MYVLE_SCHEME,null,null));

        try
        {
            initChilds();
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Exception during initialization!\n");
        }
    }

    // === VNode methods ===
    static String resourcePostfix = "Location";

    public synchronized String[] createResourceTypes()
    {
        VRSFactory vrss[] = this.vrsContext.getRegistry().getServices();

        StringList names = new StringList();

        names.add(VRS.RESOURCEFOLDER_TYPE);

        for (VRSFactory vrs : vrss)
        {
            String vrsName = vrs.getName();
            String name2 = vrsName.toLowerCase();

            // filter
            if ((name2.startsWith("http")) || (name2.startsWith("gat")) || (name2.startsWith("info")))
            {
                ; // skip
            }
            else
            {
                names.add(vrsName + " " + resourcePostfix);
            }
        }

        names.add(VRS.LINK_TYPE);

        return names.toArray();
    }

    @Override
    public String getIconURL()
    {
        return "vle-world.png";
    }

    @Override
    public String getName()
    {
        String val = Global.getUserProperty("myvle.name");

        if (val == null)
            val = "My Vle";
        return val;
    }

    @Override
    public String getType()
    {
        return MYVLE_TYPE;
    }

    @Override
    public VRL getHelp()
    {
        return Global.getHelpUrl(MYVLE_TYPE);
    }

    private String attrNames[] =
    { ATTR_NAME, ATTR_TYPE, "[User]", GlobalConfig.PROP_USER_CONFIGURED_VOS, "[FireWall Settings]",
            GlobalConfig.PROP_INCOMING_FIREWALL_PORT_RANGE, GlobalConfig.PROP_PASSIVE_MODE, "[BDII Service]",
            GlobalConfig.PROP_BDII_HOSTNAME, GlobalConfig.PROP_BDII_PORT, "[Installation]",
            GlobalConfig.PROP_VLET_VERSION, GlobalConfig.PROP_VLET_INSTALL, GlobalConfig.PROP_VLET_SYSCONFDIR,
            GlobalConfig.PROP_VLET_LIBDIR,

    // Global.JAVA_OS_ARCH,
    // Global.JAVA_OS_NAME,
    // Global.JAVA_OS_VERSION,
    };

    //
    // if this boolean is set, a reset is pending !
    //

    private boolean mustReset = false;

    public String[] getAttributeNames()
    {
        return attrNames;
    }

    public VAttribute getAttribute(String name)
    {
        if (name.startsWith("["))
            return new VAttribute(name, "");

        // debug
        if (name.startsWith("-["))
            return new VAttribute(name, "");

        VAttribute attr = null;

        if (name.compareTo(ATTR_TYPE) == 0)
        {
            attr = new VAttribute(name, getType());
            attr.setEditable(false);
            return attr;
        }

        if (name.compareTo(ATTR_NAME) == 0)
        {
            attr = new VAttribute(name, getName());
            attr.setEditable(true);
            return attr;
        }

        ConfigManager manager = getConfigManager();

        try
        {
            if ((attr = manager.getAttribute(name)) != null)
            {
                return attr;
            }
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Configuration exception!\n");
        }

        return null;
    }

    public boolean setAttribute(VAttribute attr) throws VlException
    {
        if (attr == null)
            return false;

        String name = attr.getName();
        String strval = attr.getStringValue();

        if (name == null)
            return false;

        ConfigManager manager = getConfigManager();
        BooleanHolder refreshConfig = new BooleanHolder();

        // check Configuration attributes:
        if (manager.setAttribute(attr, refreshConfig))
        {
            this.mustReset = refreshConfig.value;
            return true;
        }

        if (name.compareTo(ATTR_NAME) == 0)
        {
            this.renameTo(strval, false);
        }

        else
        {
            logger.warnPrintf("MyVle.setAttribute():unknown attribute:%s\n", attr);
        }

        return false;
    }

    private synchronized ConfigManager getConfigManager()
    {
        return vrsContext.getConfigManager();
    }

    // @Override
    public boolean delete() throws VlException
    {
        throw new ResourceWriteAccessDeniedException("Can not remove toplevel resource.");
    }

    public String[] getResourceTypes()
    {
        return createResourceTypes();
    }

    public long getNrOfNodes() throws VlException
    {

        return rootNodes.size();
    }

    public synchronized VNode[] getNodes() throws VlException
    {
        // if (rootNodes==null)
        // rescan:
        initChilds();

        VNode nodes[] = new VNode[rootNodes.size()];

        for (int i = 0; i < rootNodes.size(); i++)
        {
            nodes[i] = rootNodes.elementAt(i);
        }

        return nodes;
    }

    private synchronized void initChilds() throws VlException
    {
        checkReset();

        if (rootNodes == null)
            rootNodes = new Vector<VNode>();
        else
            rootNodes.clear();

        // Grid neighbourhood info resource
        {
            VRL targetLoc = new VRL("info:/" + VRS.INFO_GRID_NEIGHBOURHOOD);
            // LinkNode lnode =
            // LinkNode.createLinkNode(vrsContext,(VRL)null,targetLoc);
            VNode node = this.getVRSContext().openLocation(targetLoc);
            addSubNode(node);
        }

        // Local system neighbourhood info resource
        {
            VRL targetLoc = new VRL("info:/" + VRS.INFO_LOCALSYSTEM);

            // LinkNode lnode =
            // LinkNode.createLinkNode(vrsContext,(VRL)null,targetLoc);
            VNode node = this.getVRSContext().openLocation(targetLoc);

            addSubNode(node);
        }

        // add stored nodes: autoinitialize if no environment
        if (GlobalConfig.isApplet())
        {
            readChilds(false);
        }
        else
        {
            readChilds(true);
        }
    }

    private void checkReset()
    {
        if (mustReset)
        {
            this.vrsContext.getRegistry().reset();
            this.mustReset = true;
        }
    }

    private void readChilds(boolean autoinit) throws VlException
    {
        // readChilds reads Server Info again: clean & update ServerInfo class!

        this.vrsContext.getServerInfoRegistry().removeAll(); // clean ServerInfo
        this.vrsContext.getServerInfoRegistry().reload(); // reload first

        VDir dir = getRootResourceDir(autoinit);

        if (dir == null)
            return;

        VFSNode childs[] = dir.listSorted(true, true);

        if ((childs == null) || (childs.length <= 0))
            return;

        for (VFSNode child : childs)
        {
            try
            {
                // ***
                // try each child (be robuust!)
                // ***

                if (child instanceof VFile)
                {
                    VFile file = (VFile) child;

                    if (file.getLocation().isVLink())
                    {
                        LinkNode lnode = LinkNode.loadFrom(vrsContext, file);

                        // === LEGACY CODE ===
                        // auto-update
                        // when a link in an old format is detected/corrupted:
                        // it will be deleted
                        if (lnode.getAttribute(ATTR_SCHEME) == null)
                        {
                            logger.warnPrintf("Warning: deleting old or corrupt resource specification:%s\n", lnode);
                            lnode.delete();
                        }
                        else
                        {
                            registerNewResourceNode(lnode, false);
                        }
                    }
                    else if (StringUtil.compare(file.getExtension(), VRS.VRESX_EXTENSION) == 0)
                    {
                        // new ResourceFolder
                        ResourceFolder groupNode = ResourceFolder.readFromXMLStream(vrsContext, file);
                        addResourceNode(groupNode, false);
                    }
                    // else other types not yet supported.
                }
            }
            catch (Exception e)
            {
                Global.logException(ClassLogger.ERROR, this, e, "Error reading child resource:%s\n", child);
            }
            // else directories not supported yet
        }

    }

    private VDir getRootResourceDir(boolean autoinit) throws VlException
    {
        VRL loc = getMyVleLocation();
        // private client;

        VFSClient vfs = new VFSClient(vrsContext);

        if ((vfs.existsDir(loc) == false) && (autoinit == false))
            return null;

        VDir dir = null;

        // first create mandatory configuration path:
        if (vfs.existsDir(loc) == false)
        {
            try
            {
                dir = vfs.mkdirs(loc);
                createCustomEnvironment(dir);
            }
            catch (VlException e)
            {
                logger.errorPrintf("Couldn't initialize myvle!:%s\n", loc);
            }
        }
        else
        {
            dir = vfs.getDir(loc);
        }

        return dir;

    }

    private VRL getMyVleLocation()
    {
        return this.vrsContext.getConfigManager().getMyVLeLocation();
    }

    /*
     * Index nr of added resource, this is NOT the index into the vector since
     * any manipulation of the vector will change the order and invaliday the
     * logical VLRs stored in the ResourceNodes
     */
    int resourceNodeIndex = 0;

    private void addSubNode(VNode node) throws VlException
    {
        synchronized (rootNodes)
        {
            rootNodes.add(node);
        }
    }

    private void registerNewResourceNode(VLogicalResource vlnode, boolean save) throws VlException
    {
        VNode node = (VNode) vlnode;

        if ((save) && (node instanceof LogicalResourceNode))
        {
            // only save in persistant environment.
            if (this.vrsContext.getConfigManager().getUsePersistantUserConfiguration())
            {
                saveNewNode((LogicalResourceNode) node);
            }
        }

        synchronized (rootNodes)
        {
            rootNodes.add(node);

            String ref = null;
            VRL loc = vlnode.getStorageLocation();
            // change location path to MyVle//<index>
            if (loc != null)
            {
                // use unique basename as persitant link:
                ref = loc.getBasename(false);
            }
            else
            {
                // non persistant (violatile) node:
                ref = "v" + (rootNodes.size() - 1);
            }

            // append reference:
            VRL linkPath = this.getLocation().append(ref);
            vlnode.setLogicalLocation(linkPath);

        }
    }

    /**
     * Adds ResourceNode to rootNodes and optionally saves the configuration.
     * 
     * @param lnode
     * @param save
     * @throws VlException
     */

    private void addResourceNode(ResourceFolder gnode, boolean saveNewNode) throws VlException
    {
        if (gnode == null)
            return;

        synchronized (rootNodes)
        {
            rootNodes.add(gnode);

            String ref = null;

            VRL saveLoc = null;

            // New Node => New Save Location
            if (saveNewNode)
                saveLoc = this.createNewStorageLocation(VRS.VRESX_EXTENSION);
            else
                saveLoc = gnode.getStorageLocation();

            // change location path to MyVle//<index>
            if (saveLoc != null)
            {
                // use unique basename as persistant link:
                ref = saveLoc.getBasename(false);
            }
            else
            {
                // non persistant (violatile) node:
                ref = "v" + (rootNodes.size() - 1);
            }

            // append reference:
            VRL linkPath = this.getLocation().append(ref);
            gnode.setLogicalLocation(linkPath);
            // do NOT Set parent:
            // gnode.setParent(this);

            // save AFTER update !

            if (saveNewNode)
            {
                gnode.saveAsXmlTo(saveLoc);
            }
        }
    }

    /** Initializes a default environment in the specified directory */
    private void createCustomEnvironment(VDir dir)
    {
        // default SRB server
        // do not auto create SRB Server!
        // addSRBServer();

        // optional Window My.. directories...
        addWindowsStuff();
    }

    /** Scan filesystem root and add them to the rootNodes */
    protected void addFilesystemRoots() throws VlException
    {
        File roots[] = null;

        // for windows this method returns the drives:
        if (Global.isWindows() == true)
        {

            // alt get drives to avoid annoying pop-up
            roots = Global.getWindowsDrives();
        }
        else
        {
            // should trigger initialize Security Context
            @SuppressWarnings("unused")
            SecurityManager sm = System.getSecurityManager();
            // disable ?
            System.setSecurityManager(null);

            roots = java.io.File.listRoots();
        }

        VRL targetLoc;
        LinkNode lnode;

        for (int i = 0; i < roots.length; i++)
        {
            if (roots[i].exists())
            {
                String path = roots[i].getAbsolutePath();
                targetLoc = new VRL("file", null, path);
                // linkPath=this.getLocation().addPath(""+index++);
                lnode = LinkNode.createLinkNode(vrsContext, (VRL) null, targetLoc);
                lnode.setName("localhost:" + targetLoc.getPath());
                // do not show the shortcut image
                lnode.setShowShortCutIcon(false);
                lnode.setIconURL("default/folder.png");
                lnode.setLogicalParent(this);

                // hard coded node:
                lnode.setEditable(false);
                registerNewResourceNode(lnode, false);
            }
        }
    }

    /** Check and add some favorite windows locations */
    private void addWindowsStuff()
    {
        // For windows users add their windows favorites
        for (String subpath : new String[]
        { "Favorites", "My Documents" })
        {
            // use local home. might be tmp dir.
            String localPath = vrsContext.getLocalUserHome() + File.separator + subpath;

            try
            {

                File file = new File(localPath);

                if (file.exists())
                {
                    String path = file.getAbsolutePath();
                    VRL targetLoc = new VRL("file", null, path);

                    LinkNode lnode = LinkNode.createLinkNode(vrsContext, (VRL) null, targetLoc);
                    lnode.setName("Windows " + subpath);
                    // do not show the shortcut image
                    // lnode.setShowShortCutIcon(false);
                    lnode.setIconURL("default/folder.png");
                    lnode.setLogicalParent(this);
                    lnode.setShowShortCutIcon(true);

                    registerNewResourceNode(lnode, true);
                    // rootLocations.add(targetLoc);
                }
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.ERROR, e, "Couldn't add Windows Resource:%s\n", localPath);
            }
        }
    }

    /** Stores new node in the config dir */
    private void saveNewNode(LogicalResourceNode vlnode) throws VlException
    {
        VRL loc = createNewStorageLocation(VRS.VLINK_EXTENSION);
        vlnode.saveAtLocation(loc);
    }

    Object storageCreationMutex = new Object();

    private VRL createNewStorageLocation(String suffix) throws VlException
    {
        VDir dir = getRootResourceDir(true);

        if (dir == null)
        {
            throw new ResourceCreationFailedException("Could not get resource directory:" + this.getMyVleLocation());
        }

        synchronized (storageCreationMutex)
        {
            VFSNode nodes[] = dir.list();

            boolean nameExists = true;
            int index = 0;
            String newName = null;
            String newNameWithExt = null;

            while (nameExists == true)
            {
                newName = "" + StringUtil.toZeroPaddedNumber(index, 3);
                newNameWithExt = newName + "." + suffix;
                nameExists = false;

                // check existing names:
                for (int i = 0; (i < nodes.length) && (nameExists == false); i++)
                {
                    String nodeName = nodes[i].getBasename(false);
                    // String nodeNameWithExt=nodes[i].getBasename(true);

                    // basename may not be the same ! (ignore extension)
                    if (StringUtil.compare(nodeName, newName) == 0)
                        nameExists = true;

                    // already covered
                    // if
                    // (StringUtil.compare(nodeNameWithExt,newNameWithExt)==0)
                    // nameExists=true;
                }

                index++; // next name:
            }
            // newName doesn't exist:
            // Debug("new node name="+newNameWithExt);
            // creat new empty file !
            dir.createFile(newNameWithExt);
            return dir.getVRL().append(newNameWithExt);
        }

    }

    /**
     * Auto linkdrops node on this resource, returned node is alway of LinkNode
     * type. Will fail isMove==true to avoid deleting of the Dropped Node.
     * 
     */
    public VNode addNode(VNode sourceNode, String optNewName, boolean isMove) throws VlException
    {

        // =========
        // Drop of ResourceFolder of ResourceFolder Description ! (.vrsx )
        // =========

        if ((sourceNode instanceof ResourceFolder) || (ResourceFolder.isResourceFolderDescription(sourceNode)))
        {
            ResourceFolder newNode;

            // store duplicate ! (copy)

            if (sourceNode instanceof ResourceFolder)
            {
                newNode = ((ResourceFolder) sourceNode).duplicate();
            }
            else
            {
                // load description !
                newNode = ResourceFolder.createFrom(sourceNode);
            }

            // name in ReosurceLinkNode is stored as attribute, so rename can be
            // done after save()
            if (optNewName != null)
                newNode.renameTo(optNewName, false);

            addResourceNode(newNode, true); // updates implementation & VRL !

            // delete old resource:

            if (isMove)
                ((ResourceFolder) sourceNode).delete(true);

            return newNode;
        }
        else if (sourceNode instanceof LogicalResourceNode)
        {
            // create copy

            LogicalResourceNode lnode;
            // if (sourceNode instanceof InfoNode)
            // {
            // // change read only InfoNode to LinkNode:
            // lnode=new LinkNode((LogicalResourceNode)sourceNode);
            // }
            // else
            // always copy node!
            {
                lnode = ((LogicalResourceNode) sourceNode).duplicate();
            }

            // =======================================
            // MyVle root contains only locations.
            // ServerConfigs -> ServerRegistry !
            // =======================================

            if (lnode.isServerConfigType())
            {
                lnode.setType(VRS.RESOURCE_LOCATION_TYPE);
            }

            // cast Non editable "INFO" type to editable Resource Location Type
            // !
            if (lnode.getType().equals(VRS.RESOURCE_INFO_TYPE))
            {
                lnode.setType(VRS.RESOURCE_LOCATION_TYPE);
            }

            // duplicate InfoNode ! > set editable to true;

            // Clear Logical Attributes/reset paremeters:
            lnode.setEditable(true);
            lnode.setLogicalLocation(null);
            lnode.setLogicalParent(null); // clear !

            registerNewResourceNode(lnode, true); // updates implementation &
                                                  // VRL !

            // rename after addResourcNode (to allow auto save !)
            if (optNewName != null)
                lnode.renameTo(optNewName, false);

            if (isMove)
                ((LogicalResourceNode) sourceNode).delete();

            return lnode;
        }

        if (isMove)
            throw new nl.uva.vlet.exception.NotImplementedException(
                    "Cannot move this kind of resource. Use Copy or Link instead:" + sourceNode);

        //
        // Default Action: Create New LinkNode to dropped resource !
        //
        LinkNode lnode = null;

        // do not create links to links but copy linknode itself.
        if (sourceNode.getLocation().isVLink() == true)
        {
            lnode = LinkNode.loadFrom(vrsContext, sourceNode);
        }
        else
        {
            // VRL linkLoc=this.getNewLinkLocation();
            lnode = LinkNode.createLinkNode(vrsContext, (VRL) null, sourceNode.getLocation());
        }

        // first set parent&save
        lnode.setLogicalParent(this);
        registerNewResourceNode(lnode, true); // updates implementation & VRL !

        // name in LinkNode is stored as attribute, so rename can be done after
        // save()
        if (optNewName != null)
            lnode.renameTo(optNewName, false);

        return lnode;
    }

    public boolean delNode(VNode node)
    {
        return unlinkNode(node);
    }

    public boolean unlinkNode(VNode node)
    {
        // Debug("delNode:"+node);

        for (int i = 0; i < rootNodes.size(); i++)
        {
            if (rootNodes.elementAt(i).compareTo(node) == 0)
            {
                rootNodes.remove(i);
                return true;
            }
        }

        // Debug("child not found:"+node);

        return false;
    }

    public VNode createNode(String type, String name, boolean force) throws VlException
    {
        // New Link
        if (type.compareTo(VRS.LINK_TYPE) == 0)
        {
            LinkNode lnode = LinkNode.createLinkNode(vrsContext, (VRL) null, new VRL("file:///"), false);
            // add default attributes to show up in Attributes
            lnode.setShowShortCutIcon(true);
            lnode.setIconURL("");

            if (name != null)
                lnode.setName(name);
            else
                lnode.setName("New Link");
            // will update logical location
            this.registerNewResourceNode(lnode, true);
            return lnode;
        }
        // ResourceFolder
        else if (type.compareTo(VRS.RESOURCEFOLDER_TYPE) == 0)
        {
            // ResourceFolder gnode=new
            // ResourceFolder(this.rootGroup,this.vrsContext,null);
            ResourceFolder gnode = new ResourceFolder(null, this.vrsContext, null);

            if (name != null)
                gnode.setName(name);
            else
                gnode.setName("New Resource Folder");

            this.addResourceNode(gnode, true);
            return gnode;

        }
        // BoomarkFolder = ResourceFolder containg links
        // else if (type.compareTo(BOOKMARKFOLDER_TYPE)==0)
        // {
        // // ResourceFolder gnode=new
        // ResourceFolder(this.rootGroup,this.vrsContext,null);
        // ResourceFolder gnode=new ResourceFolder(null,this.vrsContext,null);
        // if (name!=null)
        // gnode.setName(name);
        // else
        // gnode.setName("Bookmarks");
        //
        // gnode.setIconURL("bookmarkfolder.png");
        //
        // this.addResourceNode(gnode,true);
        // return gnode;
        //
        // }

        //
        // Check New <ResourceType>
        //
        String vrsName = null;
        int i = type.indexOf(" " + resourcePostfix);

        if (i >= 0)
            vrsName = type.substring(0, i);
        else
            vrsName = type;

        VRSFactory vrs = this.vrsContext.getRegistry().getVRSFactoryWithName(vrsName);

        // Create New LogicalResource for VRS specification
        if (vrs != null)
        {
            VRL targetLoc = null;
            String scheme = vrs.getSchemeNames()[0];
            targetLoc = new VRL(scheme, "", VRS.getSchemeDefaultPort(scheme), "/~");
            LinkNode lnode = LinkNode.createResourceNode(vrsContext, (VRL) null, targetLoc, false);

            if (name != null)
                lnode.setName(name);
            else
                lnode.setName("New " + vrsName);

            // do not show the shortcut image
            lnode.setShowShortCutIcon(false);
            lnode.setLogicalParent(this);

            // copy/create default attributes:
            lnode.initializeServerAttributes();
            // copy back!
            targetLoc = lnode.getTargetVRL();

            // custom icons:

            if (StringUtil.endsWith(lnode.getTargetHostname(), "sara.nl"))
            {
                lnode.setIconURL("custom/sara_server.png");
            }
            else if (lnode.getTargetScheme().compareTo(VRS.SRB_SCHEME) == 0)
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("custom/srb_server.png");
            }
            else
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
            }

            registerNewResourceNode(lnode, true);
            return lnode;
        }

        throw new ResourceTypeNotSupportedException("Resource type not supported:" + type);
    }

    public boolean delete(boolean recurse) throws VlException
    {
        return false;
    }

    public boolean hasNode(String name) throws VlException
    {
        for (VNode node : rootNodes)
            if (node.getName().compareTo(name) == 0)
                return true;

        return false;
    }

    public boolean setAttributes(VAttribute[] attrs) throws VlException
    {
        boolean result = true;

        for (int i = 0; i < attrs.length; i++)
        {
            Boolean res2 = setAttribute(attrs[i]);
            result = result && res2;
        }

        return result;
    }

    // === MyVLe is never deletable ! ===
    public boolean isDeletable() throws VlException
    {
        return false;
    }

    public boolean isEditable() throws VlException
    {
        return true;
    }

    public VNode findNode(VRL childVRL, boolean recurse) throws VlException
    {
        return getSubNode(childVRL, recurse);
    }

    protected VNode getSubNode(VRL location, boolean recurse) throws VlException
    {
        // Debug("openLocation:"+location);

        if ((location == null) || (location.getScheme().compareTo(VRS.MYVLE_SCHEME) != 0))
            throw new nl.uva.vlet.exception.ResourceTypeNotSupportedException("Location is NOT a myvle:// location:"
                    + location);

        String path = location.getPath();

        // root node = me !
        if ((path == null) || (path.equalsIgnoreCase("")) || path.equalsIgnoreCase(VRL.SEP_CHAR_STR))
        {
            return this;
        }

        // myvle:///<node>

        String els[] = location.getPathElements();

        if ((els == null) || (els.length == 0))
        {
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Unknown location:" + location);
        }

        String ref = els[0];
        if (ref.charAt(0) == VRL.SEP_CHAR)
        {
            ref = ref.substring(1);
        }

        VNode node = null;
        // get child or first parent of path:
        if (els.length >= 1)
            node = getNode(ref);

        if (els.length == 1)
            return node;

        if (node instanceof VLogicalFolder)
        {
            // tree walk resource folder:
            return ((VLogicalFolder) node).findNode(location, true);
        }

        // Finally:
        throw new nl.uva.vlet.exception.ResourceNotFoundException("Unknown location:" + location);
    }

    public VNode getNode(String ref) throws ResourceNotFoundException
    {
        synchronized (rootNodes)
        {
            for (int i = 0; i < rootNodes.size(); i++)
            {
                VNode node = rootNodes.elementAt(i);

                if ((node != null) && (node.getLocation().getBasename().compareTo(ref) == 0))
                    return node;
            }
        }

        throw new ResourceNotFoundException("Could find reference:" + ref);
    }

    public VRL getStorageLocation()
    {
        return null;
    }

    public boolean isRenamable() throws VlException
    {
        return true;
    }

    public boolean renameTo(String newName, boolean nameIsPath) throws VlException
    {
        return (rename(newName, nameIsPath) != null);
    }

    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        vrsContext.setUserProperty("myvle.name", newName);
        return this.getVRL();
    }

    public void setLogicalLocation(VRL newRef) throws NotImplementedException
    {
        throw new nl.uva.vlet.exception.NotImplementedException("Cannot set logical location of 'MyVLe'");
    }

    public void setLogicalParent(VNode node) throws ResourceTypeMismatchException
    {
        throw new ResourceTypeMismatchException("MyVle cannot be stored in any parent:" + node);
    }

    public boolean unlinkNodes(VNode[] node) throws VlException
    {
        throw new VlInternalError("MyVle cannot unlink: Use delete node");
    }

}
