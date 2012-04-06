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
 * $Id: VOGroupsNode.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.grid;

import java.util.ArrayList;

import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceTypeNotSupportedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.vdriver.vrs.infors.CompositeServiceInfoNode;
import nl.uva.vlet.vdriver.vrs.infors.InfoConstants;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ResourceFolder;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class VOGroupsNode extends CompositeServiceInfoNode<VONode>
{
    private StringList cachedVOs;

    public VOGroupsNode(GridNeighbourhood parent, VRSContext vrsContext)
    {
        super(vrsContext, parent.createChildVRL(InfoConstants.VOGROUPS_FOLDER_NAME));
        this.logicalParent = parent; // setLogicalParent(parent);
    }

    @Override
    public String getType()
    {
        return InfoConstants.VOGROUPS_FOLDER_TYPE;
    }

    public String getIconURL()
    {
        return "vogroups-128.png";
    }

    public String getMimeType()
    {
        return null;
    }

    public String[] getAttributeNames()
    {
        StringList list = new StringList(); // super.getAttributeNames());
        // remove mime type:
        // list.remove(VAttributeConstants.ATTR_MIMETYPE);
        list.add(InfoConstants.ATTR_CONFIGURED_VOS);

        return list.toArray();
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        if (name.equals(InfoConstants.ATTR_CONFIGURED_VOS))
        {
            return new VAttribute(name, this.getConfiguredVOs());
        }

        return super.getAttribute(name);
    }

    public synchronized VONode[] getNodes() throws VlException
    {
        // Alway update:
        updateVOGroups();

        return this._getNodes();
    }

    protected VONode[] _getNodes() throws VlException
    {
        if ((childNodes == null) || (childNodes.size() <= 0))
            return null;

        synchronized (childNodes)
        {
            VONode arr[] = new VONode[childNodes.size()];
            // todo cannot convert to T[] array
            return childNodes.toArray(arr);
        }
    }

    private synchronized void updateVOGroups()
    {
        // checkVOs:
        StringList newVOs = getConfiguredVOs();

        if ((cachedVOs != null) && cachedVOs.compare(newVOs) == 0)
            return;

        // recreate:
        VONode voGroups[] = new VONode[newVOs.size()];

        for (int i = 0; i < newVOs.size(); i++)
        {
            VONode group = VONode.createVOGroup(this, newVOs.get(i));
            voGroups[i] = group;
        }

        this.setChilds(voGroups);
        this.cachedVOs = newVOs;

    }

    public String[] getResourceTypes()
    {
        return new String[] { InfoConstants.VO_TYPE };
    }

    public synchronized StringList getConfiguredVOs()
    {
        // check context:
        String voStr = this.vrsContext.getStringProperty(GlobalConfig.PROP_USER_CONFIGURED_VOS);
        StringList voList = new StringList();

        if (voStr != null)
        {
            String[] strs = voStr.split(",");

            if (strs != null)
                voList.add(strs);
        }

        // initialize user default:
        String currentVO = this.vrsContext.getVO();
        if (currentVO != null)
        {
            boolean added = voList.add(currentVO, true);
            // update VO
            if (added)
                this.vrsContext.setUserProperty(GlobalConfig.PROP_USER_CONFIGURED_VOS, voList.toString(","));
        }

        return voList;
    }

    protected synchronized void setConfiguredVOs(StringList vos)
    {
        this.vrsContext.setUserProperty(GlobalConfig.PROP_USER_CONFIGURED_VOS, vos.toString(","));
    }

    protected synchronized VONode addVO(String vo)
    {
        if (vo == null)
            return null;

        VONode voGroup = findVOGroup(vo);

        if (voGroup != null)
            return voGroup;

        // check context:
        String voStr = this.vrsContext.getStringProperty(GlobalConfig.PROP_USER_CONFIGURED_VOS);
        StringList voList = new StringList();

        if (voStr != null)
        {
            String[] strs = voStr.split(",");

            if (strs != null)
                voList.add(strs);
        }

        boolean added = voList.add(vo, true);
        // update VO
        if (added)
            this.vrsContext.setUserProperty(GlobalConfig.PROP_USER_CONFIGURED_VOS, voList.toString(","));

        this.updateVOGroups();

        return findVOGroup(vo);
    }

    public synchronized VONode findVOGroup(String vo)
    {
        for (VONode vogroup : this.childNodes)
        {
            if (StringUtil.equalsIgnoreCase(vogroup.getName(), vo))
                return vogroup;
        }

        return null;
    }

    public synchronized ResourceFolder createSEFolderForVO(VRL logicalParent, String vo) throws VlException
    {
        infoPrintf("createSEFolderForVO(): For vo:%s\n", vo);
        
        BdiiService bdii = this.vrsContext.getBdiiService();
        ArrayList<StorageArea> sas = bdii.getSRMv22SAsforVO(vo);

        infoPrintf(" - got # %d Storage Areas for vo:%s\n", sas.size(), vo);

        VRL logVrl = logicalParent.append("StorageElements (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);

        resF.setName(logVrl.getBasename());

        for (StorageArea sa : sas)
        {
            infoPrintf(" - checking storage area: %s\n", sa);

            ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Populating StorageElements folder for VO:"
                    + vo, -1);

            VRL seLoc = sa.getVOStorageLocation();
            // resourceFolder will update logical location

            if (seLoc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action aborted:%s\n", monitor.getSubTask());
                    break;
                }

                VAttributeSet set;
                
                ServiceInfo srm = sa.getSRMV22Service();

                // get/check service attributes. 
                //set = bdii.getServiceAttributes(srm); 
                set=srm.getInfoAttributes();

                infoPrintf(" - - number of info attributes: %d\n",set.size());

                // attr = new VAttribute("serviceType", srm.getServiceType());
                // set.put(attr);

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext,
                        logVrl.append(sa.getHostname()), 
                        seLoc,
                        false);
                
                seNode.setName(sa.getHostname() + ":" + sa.getStoragePath());
                                // Not Editable !
                seNode.setEditable(false);
                seNode.setInfoAttributes(set);
                resF.addNode(seNode);

            }
        }

        resF.setIconURL("servercluster.png");
        resF.setEditable(false);
        resF.setPresentation(getSRMPresentation());

        return resF;
    }

    protected Presentation getSRMPresentation()
    {
        return getDefaultPresentation();
    }

    protected Presentation getDefaultPresentation()
    {
        // First Get Defaults!:
        Presentation pres = Presentation.getPresentationFor(VRS.SRM_SCHEME, null, VRS.RESOURCE_INFO_TYPE);
        // Mess around:
        String names[] = { VAttributeConstants.ATTR_ICON, VAttributeConstants.ATTR_TYPE,
                VAttributeConstants.ATTR_HOSTNAME, VAttributeConstants.ATTR_PORT, VAttributeConstants.ATTR_PATH, };

        pres.setChildAttributeNames(names);
        return pres;
    }

    public synchronized ResourceFolder createLFCFolderForVO(VRL logicalParent, String vo) throws VlException
    {
        BdiiService bdii = this.vrsContext.getBdiiService();
        ArrayList<ServiceInfo> lfcs = bdii.getLFCsforVO(vo);

        VRL logVrl = logicalParent.append("Logical File Catalogs (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (lfcs == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%s Storage LFC servers for vo: %s\n", lfcs.size(), vo);
        for (ServiceInfo lfc : lfcs)
        {
            ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Populating StorageElements folder for VO:"
                    + vo, -1);

            VRL lfcLoc = lfc.toVRL().copyWithNewPath("/grid");
            // resourceFolder will update logical location

            if (lfcLoc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getSubTask());
                    break;
                }
                
                VAttributeSet set = new VAttributeSet();
                set = lfc.getInfoAttributes();
                // attr = new VAttribute("serviceType", lfc.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.append(lfcLoc.getHostname()),
                        lfcLoc, false);
                seNode.setName("LFC " + lfcLoc.getHostname());
                seNode.setInfoAttributes(set);

                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());
        return resF;
    }

    public ResourceFolder createWMSFolderForVO(VRL logicalParent, String vo) throws VlException
    {
        BdiiService bdii = this.vrsContext.getBdiiService();
        ArrayList<ServiceInfo> wmss = bdii.getWMSServiceInfos(vo);

        VRL logVrl = logicalParent.append("WMS Services (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (wmss == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%d WMS services for vo:%s\n", wmss.size(), vo);
        for (ServiceInfo wms : wmss)
        {
            ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Populating WMS folder for VO:" + vo, -1);

            VRL loc = wms.toVRL();
            // resourceFolder will update logical location

            if (loc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getSubTask());
                    break;
                }
                
                VAttributeSet set = new VAttributeSet();

                // attr = new VAttribute("serviceType", wms.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);
                set = wms.getInfoAttributes();

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.append(loc.getHostname()), loc,
                        false);
                seNode.setInfoAttributes(set);
                seNode.setName("WMS " + loc.getHostname());
                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());

        return resF;
    }

    public ResourceFolder createLBFolderForVO(VRL logicalParent, String vo) throws VlException
    {
        BdiiService bdii = this.vrsContext.getBdiiService();
        ArrayList<ServiceInfo> lbss = bdii.getLBServiceInfosForVO(vo);

        VRL logVrl = logicalParent.append("LB Services (" + vo + ")");
        ResourceFolder resF = new ResourceFolder(vrsContext, logVrl);
        resF.setName(logVrl.getBasename());

        if (lbss == null)
        {
            resF.setEditable(false);
            return resF;
        }

        infoPrintf("Got #%d WMS services for vo:%s\n", lbss.size(), vo);
        for (ServiceInfo lbs : lbss)
        {
            ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Populating WMS folder for VO:" + vo, -1);

            VRL loc = lbs.toVRL();
            // resourceFolder will update logical location

            if (loc != null)
            {
                if (monitor.isCancelled() == true)
                {
                    infoPrintf("Action Aborted:%s\n", monitor.getSubTask());
                    break;
                }
                
                VAttributeSet set = new VAttributeSet();

                // attr = new VAttribute("serviceType", wms.getServiceType());
                // set.put(attr);
                // attr = new VAttribute("serviceVersion", "?");
                // set.put(attr);
                set = lbs.getInfoAttributes();

                InfoNode seNode = InfoNode.createServerInfoNode(vrsContext, logVrl.append(loc.getHostname()), loc,
                        false);
                seNode.setInfoAttributes(set);
                seNode.setName("LB " + loc.getHostname());
                resF.addNode(seNode);
            }
        }

        resF.setEditable(false);
        resF.setIconURL("servercluster.png");
        resF.setPresentation(getDefaultPresentation());

        return resF;
    }

    private void infoPrintf(String msg, Object... args)
    {
        Global.infoPrintf(this, msg, args);
    }

    @Override
    public synchronized VNode createNode(String type, String name, boolean force)
            throws ResourceTypeNotSupportedException
    {
        if ((type == null) || (type.compareTo(InfoConstants.VO_TYPE) != 0))
            throw new nl.uva.vlet.exception.ResourceTypeNotSupportedException("Can not create resource type:" + type);

        return this.addVO(name);
    }

    public synchronized boolean deleteVOGroup(VONode groupNode)
    {
        return _delete(groupNode);
    }

    private boolean _delete(VNode node)
    {
        synchronized (childNodes)
        {
            this.delSubNode(node);

            // Update VOs:
            StringList newVos = new StringList();

            // new configured vo list:
            for (VONode vogroup : childNodes)
                newVos.add(vogroup.getName());

            this.setConfiguredVOs(newVos);

            return true;
        }
    }

    @Override
    public synchronized boolean delNode(VNode node)
    {
        return _delete(node);
    }

}
