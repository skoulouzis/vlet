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
 * $Id: StorageArea.java,v 1.10 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.util.ArrayList;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.bdii.ServiceInfo.ServiceInfoType;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

/**
 * tbn18.nikhef.nl,gb-se-amc.amc.nl Storage Area description for a VO. A Storage
 * Area is linked to a VO and a Storage Element. For each VO on the same Storage
 * Element there is a different StorageArea with a storagePath where that VO may
 * write.
 */

public class StorageArea
{
    /** VO owner of this StorageArea at the specified storage element */
    protected String vo;

    /** Hostname of storage element */
    protected String seHostname;

    /** Actual writable path for storage location for this VO */
    protected String storagePath;

    /**
     * List of SRMServices. Typically a V1 and/or a V2.2. SRM Service
     */
    protected ArrayList<ServiceInfo> srmServices = null;

    /**
     * Create StorageArea with one Storage Service
     * 
     * @throws VlException
     */
    protected StorageArea(String vo, ServiceInfoType type, String protocol, String host, int port, String path)
            throws VlException
    {
        if (host == null)
        {
            throw new VlException("Host can't be null. Info details: \n \t VO: " + vo + " \n \t Type: " + type
                    + "\n \t protocol: " + protocol + "\n \t port: " + port);
        }
        this.seHostname = host;
        this.vo = vo;
        this.storagePath = path;
        srmServices = new ArrayList<ServiceInfo>(1);
        srmServices.add(new ServiceInfo(type, protocol, host, port));
    }

    // /**
    // * Create StorageArea Triple form StorageElement host, VO and
    // storageLocation
    // *
    // * @param storageElementHostname
    // * Storage Element hostname.
    // * @param VO
    // * VO for which this StorageAread applies.
    // * @param saPath
    // * actual storage area path.
    // */
    // public StorageArea(String storageElementHostname,String VO, String
    // saPath)
    // {
    // this.seHostname = storageElementHostname;
    // this.vo = VO;
    // this.storagePath=saPath;
    // srmServices=new ArrayList<ServiceInfo>(2);
    // }
    //    
    /**
     * Create StorageArea Triple form StorageElement host, VO and
     * storageLocation
     * 
     * @param srmInfo
     *            SRM Service Information.
     * @param VO
     *            VO for which this StorageAread applies.
     * @param saPath
     *            actual storage area path.
     * @throws VlException
     */
    public StorageArea(ServiceInfo srmInfo, String VO, String saPath) throws VlException
    {
        this.seHostname = srmInfo.getHost();
        this.vo = VO;

        if (StringUtil.isEmpty(saPath))
        {
            throw new VlException("saPath is Null!!!!");
        }

        this.storagePath = saPath;
        srmServices = new ArrayList<ServiceInfo>(2);
        srmServices.add(srmInfo);
    }

    // /** @deprecated. Use only one service at the constructor.
    // * Only use this one to add a SRM v1 service. */
    // public void addService(ServiceInfo se)
    // {
    // srmServices.add(se);
    // }

    public ArrayList<ServiceInfo> getServices()
    {
        return this.srmServices;
    }

    public ServiceInfo getSRMV22Service()
    {
        for (ServiceInfo info : srmServices)
        {
            if (info.isSRMV22())
            {
                return info;
            }
        }

        return null;
    }

    public ServiceInfo getSRMV11Service()
    {
        for (ServiceInfo info : srmServices)
        {
            if (info.isSRMV11())
            {
                return info;
            }
        }

        return null;
    }

    // LFC's don't have StorageArea
    // public ServiceInfo getLFCService()
    // {
    // for (ServiceInfo info:serviceInfos)
    // {
    // if (info.isLFC())
    // {
    // return info;
    // }
    // }
    //        
    // return null;
    // }

    public String getStoragePath()
    {
        return storagePath;
    }

    public String getVO()
    {
        return vo;
    }

    public String getHostname()
    {
        return this.seHostname;
    }

    /**
     * Returns SE Storage Location where VO is allowed to write. Might return
     * NULL if no valid SE is associated with this Storage Area
     */
    public VRL getVOStorageLocation()
    {
        ServiceInfo srm = this.getSRMV22Service();
        if (srm == null)
            return null;

        return new VRL(VRS.SRM_SCHEME, srm.getHost(), srm.getPort(), this.getStoragePath());
    }

    public String toString()
    {
        int len = 0;
        if (this.srmServices != null)
            len = this.srmServices.size();

        return "{<StorageArea>[" + this.vo + "] -> '" + this.storagePath + "'" + "#" + len + "}";
    }

    /**
     * Create StorageArea with one SRM V2.2 Storage Service from Storage Area
     * Location which is writable for the specified VO
     * 
     * @throws VlException
     */
    public static StorageArea createSRM22StorageArea(VRL saLocationVrl, String vo) throws VlException
    {
        StorageArea sa = new StorageArea(vo, ServiceInfoType.SRMV22, saLocationVrl.getScheme(), saLocationVrl
                .getHostname(), saLocationVrl.getPort(), saLocationVrl.getPath());
        return sa;
    }

}
