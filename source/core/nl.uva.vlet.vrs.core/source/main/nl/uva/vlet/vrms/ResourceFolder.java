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
 * $Id: ResourceFolder.java,v 1.7 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */
// source: 

package nl.uva.vlet.vrms;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICONURL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.xml.VCompositePersistance;
import nl.uva.vlet.data.xml.XMLData;
import nl.uva.vlet.exception.ResourceDeletionFailedException;
import nl.uva.vlet.exception.ResourceTypeMismatchException;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlXMLDataException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.presentation.VPresentable;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VCompositeDeletable;
import nl.uva.vlet.vrs.VDeletable;
import nl.uva.vlet.vrs.VDuplicatable;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VRenamable;
import nl.uva.vlet.vrs.io.VStreamReadable;

/**
 * New composite Resource Folder.
 * <p>
 * Implementation note: this class implements VLogicalResource but it NOT a
 * LogicalResourceNode which is used as parent for VLinks ! (Main difference is
 * that ResourceFolder is composite and LogicalNode is singular)
 */
public class ResourceFolder extends LogicalFolderNode<VNode> implements VCompositePersistance, VEditable, VRenamable,
        VDuplicatable<ResourceFolder>, VDeletable, VCompositeDeletable, VStreamReadable, VPresentable
{
    VAttributeSet attributes = new VAttributeSet();

    private VRL descriptionLocation;

    // unique ID reference counter for child nodes:
    private int childRefCounter = 0;

    static private String[] attributeNames =
    { ATTR_TYPE, ATTR_NAME,
            // ATTR_PATH,
            // ATTR_LOCATION,
            ATTR_ICONURL };

    public ResourceFolder(ResourceFolder parent, VRSContext context, VRL vrl)
    {
        super(context, vrl);
        this.logicalParent = parent;
        init();
    }

    public ResourceFolder(VRSContext context, VRL vrl)
    {
        super(context, vrl);
        init();
    }

    public ResourceFolder(VRSContext context, VAttributeSet attrSet, VRL vrl)
    {
        super(context, vrl);
        init(attrSet);
    }

    public ResourceFolder duplicate() throws VlException
    {
        // VlException lastException;

        // duplicate attributes:
        ResourceFolder group = new ResourceFolder(this.getVRSContext(), this.attributes, (VRL) null);

        VNode nodes[] = this.getNodes();

        if (nodes != null)
        {
            for (VNode node : this.getNodes())
            {
                group.addSubNode(node.duplicate());
            }
        }

        return group;
    }

    private void init(VAttributeSet attrSet)
    {
        // duplicate
        attributes = attrSet.duplicate();
        initDefaultAttributes();
    }

    private void init()
    {
        initDefaultAttributes();
    }

    private void initDefaultAttributes()
    {
        if (attributes == null)
            attributes = new VAttributeSet();

        setIfNotSet(ATTR_ICONURL, "vle-world-folder.png");
        setIfNotSet(ATTR_NAME, "<NO NAME>");
    }

    private void setIfNotSet(String name, String newValue)
    {
        VAttribute attr = attributes.get(name);
        String value = null;

        if (attr != null)
            value = attr.getStringValue();

        if (StringUtil.isEmpty(value))
            attributes.put(new VAttribute(name, newValue));
    }

    public VAttribute[] getAttributes() throws VlException
    {
        return getAttributes(getAttributeNames());
    }

    @Override
    public String getType()
    {
        return VRS.RESOURCEFOLDER_TYPE;
    }

    public String[] getAttributeNames()
    {
        return attributeNames;
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        if (name == null)
            return null;

        // check attribute store first:
        VAttribute attr = this.attributes.get(name);

        // check super (standard or derived attributes, like: scheme,path,etc!)
        if (attr == null)
            attr = super.getAttribute(name);

        if (attr != null)
        {
            if (name.compareTo(ATTR_NAME) == 0)
            {
                attr.setEditable(true);
            }
            else if (name.compareTo(ATTR_ICONURL) == 0)
            {
                attr.setEditable(true);
            }
        }

        return attr;
    }

    public void setIconURL(String str)
    {
        this.attributes.put(new VAttribute(VAttributeConstants.ATTR_ICONURL, str));
    }

    public String getIconURL()
    {
        // concurrent modifications ?
        if (attributes == null)
            return null;
        return this.attributes.getStringValue(VAttributeConstants.ATTR_ICONURL);
    }

    public void setLogicalParent(VNode node) throws ResourceTypeMismatchException
    {
        if ((node instanceof ResourceFolder) == false)
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException(
                    "ResourceGroups can only have ResourceFolder node as parent. Got:" + node);

        this.logicalParent = (ResourceFolder) node;
    }

    public String[] getResourceTypes()
    {
        // ResourceFolder group MyVle Resources.
        return this.vrsContext.getMyVLe().createResourceTypes();
    }

    public VAttributeSet getPersistantAttributes()
    {
        return this.attributes;
    }

    public String getPersistantType()
    {
        return VRS.RESOURCEFOLDER_TYPE;
    }

    public void setName(String name)
    {
        synchronized (attributes)
        {
            this.attributes.set(new VAttribute(ATTR_NAME, name));
        }

    }

    public String getName()
    {
        return attributes.getStringValue(ATTR_NAME);
    }

    public String getMimeType() throws VlException
    {
        return super.getMimeType(); // should be vlet-resourcefolder-xml
    }

    public boolean setAttribute(VAttribute attr) throws VlException
    {
        return setAttribute(attr, true);
    }

    public boolean setAttribute(VAttribute attr, boolean save) throws VlException
    {
        synchronized (attributes)
        {
            this.attributes.set(attr);
        }

        if (save)
            save();

        return true;
    }

    public boolean setAttributes(VAttribute[] attrs) throws VlException
    {
        synchronized (attributes)
        {
            for (VAttribute attr : attrs)
                setAttribute(attr, false);
        }

        save();

        return true;
    }

    public boolean isRenamable() throws VlException
    {
        return isEditable();
    }

    public boolean renameTo(String newName, boolean nameIsPath) throws VlException
    {
        return (rename(newName, nameIsPath) != null);
    }

    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        if (isEditable() == false)
            throw new nl.uva.vlet.exception.ResourceNotEditableException("Cannot rename this folder: Read Only folder");

        setName(newName);
        return this.getVRL();
    }

    public VNode addNode(VNode node) throws VlException
    {
        if (isEditable() == false)
            throw new nl.uva.vlet.exception.ResourceNotEditableException(
                    "Cannot add resource to this folder: Read Only folder");

        return addNode(node, null, false);
    }

    public VNode addNode(VNode node, String newName, boolean isMove) throws VlException
    {
        if (isEditable() == false)
            throw new nl.uva.vlet.exception.ResourceNotEditableException(
                    "Cannot add resource to this folder: Read Only folder");

        // only Logical Resources allowed:
        if ((node instanceof VLogicalResource) == false)
            throw new ResourceTypeNotSupportedException("Resource type not supported:" + node);

        VNode oldParent = node.getParent();

        VNode newNode = node;

        // Currently two concrete types allowed: ResourceFolder and
        // LogicalResourceNode
        if (node instanceof ResourceFolder)
        {
            if (isMove == false)
            {
                newNode = ((ResourceFolder) node).duplicate();
                addSubNode(newNode);
            }
            else
            {
                // delete old storage location (if it has one):
                // this is only the case when moving from MyVle to
                // existing ResourceFolder
                ((ResourceFolder) newNode).deleteDiscriptionLocation();
                addSubNode(newNode);
            }
        }
        else if (node instanceof LogicalResourceNode)
        {
            if (isMove == false)
            {
                newNode = ((LogicalResourceNode) node).duplicate();
                addSubNode(newNode);
            }
            else
            {
                // delete old storage location (if it has one):
                // this is only the case when moving from MyVle to
                // existing ResourceFolder
                ((LogicalResourceNode) newNode).deleteStorageLocation();
                addSubNode(newNode);
            }
        }

        // notify parent of move:
        if (isMove)
        {
            if (oldParent instanceof ResourceFolder)
                ((ResourceFolder) oldParent).unlinkNode(node); // unlink
            else if (oldParent instanceof MyVLe)
                ((MyVLe) oldParent).unlinkNode(node); // unlink
            // default:
            else if (oldParent instanceof VComposite)
                ((VComposite) oldParent).delNode(node); // unlink ?
        }

        save();

        return newNode; // could be same node in case of a move !
    }

    /**
     * Sets logical location of this ResourceFolder, but also updates ALL
     * logical location of all of it's children.
     */
    public void setLogicalLocation(VRL vrl) throws VlException
    {
        this.setLocation(vrl);

        for (VNode child : childNodes)
        {
            this.setSubNodeLogicalLocation((VLogicalResource) child);
        }
    }

    public VNode createNode(String type, String name, boolean force) throws VlException
    {
        //debug("Create new node:(" + type + "):" + name);
        VNode vnode = createSubNode(type, name, force);
        addSubNode(vnode);
        return vnode;
    }

    /**
     * When not stored on a file system, this node must have a VNode parent
     */
    protected void addSubNode(VNode node) throws VlException
    {
        //debug("addSubNode():" + node);

        if ((node instanceof VLogicalResource) == false)
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Node type not supported:" + node);

        synchronized (childNodes)
        {
            childNodes.add(node);
            setSubNodeLogicalLocation((VLogicalResource) node);
            // update parent !
            ((VLogicalResource) node).setLogicalParent(this);

            if (node instanceof LogicalResourceNode)
            {
                ((LogicalResourceNode) node).deleteStorageLocation();
            }
        }

        save();
    }

    public boolean delete() throws VlException
    {
        return delete(false);
    }

    public boolean delete(boolean recursive) throws VlException
    {
        if (isEditable() == false)
            throw new nl.uva.vlet.exception.ResourceNotEditableException("Cannot delete this folder: Read Only folder");

        if ((recursive == false) && (this.childNodes != null) && (this.childNodes.size() > 0))
        {
            throw new ResourceDeletionFailedException("Resource is not empty:" + this);
        }

        VNode parent = this.getParent();

        // unlink myself, childs will be discared automatically !
        if ((parent != null) && (parent instanceof VComposite))
        {
            ((VComposite) parent).delNode(this);
        }

        // delete description location if I have one. (.vrsx file)
        deleteDiscriptionLocation();

        dispose();

        return true;
    }

    private boolean deleteDiscriptionLocation() throws VlException
    {
        if (this.descriptionLocation != null)
        {
            VNode node = this.vrsContext.openLocation(descriptionLocation);

            if (node instanceof VDeletable)
            {
                Global.infoPrintf(this, "Feleting description location:%s\n",node);
                return ((VDeletable) node).delete();
            }
        }

        return false;
    }

    public void dispose()
    {
        this.attributes.clear();
        this.attributes = null;
        this.childNodes.clear();
        this.childNodes = null;
        this.descriptionLocation = null;
        // this.vrsContext=null; is final
        // this.location=null; keep locatio

    }

    private void setSubNodeLogicalLocation(VLogicalResource node) throws VlException
    {
        if (this.getLocation() == null)
        {
            // can happen at initialization:
            Global.warnPrintf(this, "setSubNodeLogicalLocation(): No Logical location (yet?) for:%s\n",this);
            return;
        }

        //
        // To avoid cashing bugs in the VBrowser, continue
        // counting !
        // this assures each new node isn't shadowed by (old) cache values !
        // This stil is a bug in the VBrowser's cache !
        //

        int ref = this.childRefCounter++; // .childNodes.indexOf(node);

        VRL newRef = this.getLocation().append("v" + StringUtil.toZeroPaddedNumber(ref, 3));

        //debug("addSubNode():newRef=" + newRef);
        node.setLogicalLocation(newRef);
    }

    /** New implementation of create Node */
    protected VNode createSubNode(String type, String name, boolean force) throws VlException
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

            return lnode;
        }
        // ResourceFolder
        else if (type.compareTo(VRS.RESOURCEFOLDER_TYPE) == 0)
        {
            ResourceFolder gnode = new ResourceFolder(this, this.vrsContext, null);
            if (name != null)
                gnode.setName(name);
            else
                gnode.setName("New Group");

            return gnode;
        }
        //
        // Check New <ResourceType>
        //
        String vrsName = null;
        int i = type.indexOf(" " + MyVLe.resourcePostfix);

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
            int port = VRS.getSchemeDefaultPort(scheme);
            targetLoc = new VRL(scheme, "", port, "/~");
            LinkNode lnode = LinkNode.createResourceNode(vrsContext, (VRL) null, targetLoc, false);

            if (name != null)
                lnode.setName(name);
            else
                lnode.setName("New " + vrsName);

            // do not show the shortcut image
            lnode.setShowShortCutIcon(false);
            lnode.setLogicalParent(this);

            // copy/create default attributes: will consult VRS for defaults!
            lnode.initializeServerAttributes();

            // custom icons:

            if (StringUtil.endsWith(lnode.getTargetHostname(), "sara.nl"))
            {
                lnode.setIconURL("custom/sara_server.png");
            }
            else if (StringUtil.compare(lnode.getTargetScheme(), VRS.SRB_SCHEME) == 0)
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("custom/srb_server.png");
            }
            else
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
            }

            return lnode;
        }

        throw new ResourceTypeNotSupportedException("Resource type not supported:" + type);

    }

    /**
     * Returns location of XML description of this ResourceFolder and all it's
     * children resources.<br>
     * Only the root of a ResourceFolder has a description location.
     */
    public VRL getStorageLocation() throws VlException
    {
        return descriptionLocation;
    }

    public boolean save()
    {
        if (logicalParent != null)
        {
            //debug("save(): calling save of :" + logicalParent);
            return logicalParent.save();
        }
        else if (this.descriptionLocation != null)
        {
            try
            {
                saveToXml();
            }
            catch (Exception e)
            {
                Global.logException(ClassLogger.ERROR,this,e,"Couldn't save xml description:%s",this);
            }

            return false;
        }
        else
        {

            if (this.logicalParent != null)
                Global.warnPrintf(this, "*** Warning: save() called on node which does not have a parent container:%s\n",
                        this);
            else if (this.descriptionLocation == null)
                Global.warnPrintf(this,
                        "*** Warning: save() called on node which does not have a description location:%s\n",this);

            return false;
        }
    }

    private Object saveMutex = new Object();

    private Presentation presentation;

    private void saveToXml() throws VlException
    {
        //debug("Save to xml:" + this);

        synchronized (saveMutex)
        {

            VRL loc = this.getStorageLocation();
            //debug("Saving location=" + loc);

            if (loc == null)
                throw new nl.uva.vlet.exception.ResourceDeletionFailedException("Description Location is NULL of:"
                        + this);

            /** Create private client */
            VRSClient vrsClient = new VRSClient(this.vrsContext);

            OutputStream outps = vrsClient.openOutputStream(loc);

            this.writeAsXmlTo(outps);

            try
            {
                outps.flush();
                outps.close();
            }
            catch (IOException e)
            {
                Global.logException(ClassLogger.WARN,this,e,"Warning: Exception when closing:%s\n",this);
            }

        }

    }

    public void writeAsXmlTo(OutputStream outp) throws VlXMLDataException, VlException
    {
        synchronized (saveMutex)
        {
            String comments = "VL-e ResourceFolder description :" + this.getName();
            XMLData xmlData = getXMLData();
            xmlData.writeAsXML(outp, this, comments);
        }
    }

    private static XMLData getXMLData()
    {
        XMLData xmlData = new XMLData();
        xmlData.setPersistanteNodeElementName("vlet:resourceNode");
        xmlData.setVAttributeSetElementName("vlet:resourceDescription");
        xmlData.setVAttributeElementName("vlet:resourceProperty");
        return xmlData;
    }

    public void saveAsXmlTo(VRL loc)
    {
        synchronized (saveMutex)
        {
            this.descriptionLocation = loc;
            save();
        }

    }

    public static ResourceFolder readFromXMLStream(VRSContext context, VStreamReadable source) throws VlException
    {
        InputStream stream = source.getInputStream();
        ResourceFolder node = readFromXMLStream(context, stream);

        if (node == null)
            return null;

        if (source instanceof VNode)
            node.descriptionLocation = ((VNode) source).getVRL();

        return node;
    }

    private static ResourceFolder readFromXMLStream(VRSContext context, InputStream stream) throws VlException
    {
        XMLData data = getXMLData();

        ResourceNodeFactory nodeFactory = new ResourceNodeFactory(data, context);

        return (ResourceFolder) data.parsePersistantNodeTree(nodeFactory, stream);

    }

    public InputStream getInputStream() throws VlException
    {
        // outputstream to ByteArray
        ByteArrayOutputStream outps = new ByteArrayOutputStream(1024);
        this.writeAsXmlTo(outps);

        // ByteArray to Input Stream
        ByteArrayInputStream inps = new ByteArrayInputStream(outps.toByteArray());

        return inps;
    }

    public int getOptimalReadBufferSize()
    {
        return -1; // use default
    }

    public static boolean isResourceFolderDescription(VNode sourceNode)
    {
        if (sourceNode == null)
            return false;

        if (sourceNode instanceof ResourceFolder)
            return true;

        if ((sourceNode.getVRL() != null) && (sourceNode.getVRL().hasExtension(VRS.VRESX_EXTENSION)))
            return true;

        return false;
    }

    public static ResourceFolder createFrom(VNode sourceNode) throws VlException
    {
        if (sourceNode instanceof ResourceFolder)
            return ((ResourceFolder) sourceNode).duplicate();

        if (sourceNode instanceof VStreamReadable)
        {
            return readFromXMLStream(sourceNode.getVRSContext(), (VStreamReadable) sourceNode);
        }

        throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Not a ResourceFolder description:" + sourceNode);
    }

    public Presentation getPresentation()
    {
        return this.presentation;
    }

    public void setPresentation(Presentation newPresentation)
    {
        this.presentation = newPresentation;
    }

}
