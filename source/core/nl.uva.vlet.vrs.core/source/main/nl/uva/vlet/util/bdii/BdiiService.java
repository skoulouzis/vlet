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
 * $Id: BdiiService.java,v 1.36 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.naming.NamingException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.SSHUtil;
import nl.uva.vlet.util.bdii.ServiceInfo.ServiceInfoType;
import nl.uva.vlet.util.bdii.info.glue.GlueConstants;
import nl.uva.vlet.util.bdii.info.glue.GlueObject;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrs.VRSContext;

/**
 * Bdii service for GLUE (ldap) database.
 * 
 * Caches queries
 * 
 */

public class BdiiService
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(BdiiService.class);
        //logger.setLevel(Level.ALL);
    }

    public static BdiiService createService(VRSContext context) throws VlException
    {
        ConfigManager conf = context.getConfigManager();

        URI uri = conf.getBdiiServiceURI();
        URI proxyUri = null;
        String bdiiHost = uri.getHost();
        int bdiiPort = uri.getPort();

        if (conf.getUseSSHTunnel("bdii", bdiiHost, bdiiPort))
        {
            int lport = SSHUtil.createSSHTunnel(context, "bdii", bdiiHost, bdiiPort);

            try
            {
                proxyUri = new URI("ldap://localhost:" + lport);
            }
            catch (URISyntaxException e)
            {
                Global.errorPrintStacktrace(e);
            }
        }

        logger.infoPrintf("Using BDII uri:%s\n", uri);
        return new BdiiService(uri, proxyUri);
    }

    // ========================================================================
    // Fields
    // ========================================================================

    private BDIIQuery query;

    private java.net.URI bdiiUri = null;

    private boolean useCaching = true;

    // Hashmap of services with as key "<host>-<serviceType>"
    private Map<String, ServiceInfo> _cachedServices = new Hashtable<String, ServiceInfo>();

    // Mapping of VO to ArrayList of StorageAreas including ServiceInfos !
    private Map<String, ArrayList<StorageArea>> cachedVOStorageAreas = new Hashtable<String, ArrayList<StorageArea>>();

    // Mapping of VO to allowed LFCs
    private Map<String, ArrayList<ServiceInfo>> cachedVOLFCs = new Hashtable<String, ArrayList<ServiceInfo>>();

    // private Map<String, ArrayList<GlueObject>> cachedVOCE = new
    // Hashtable<String, ArrayList<GlueObject>>();

    // private Map<String, ArrayList<GlueObject>> cachedGridSites = new
    // Hashtable<String, ArrayList<GlueObject>>();

    // private Map<String, ArrayList<GlueObject>> cachedClusters = new
    // Hashtable<String, ArrayList<GlueObject>>();

    //private Map<String, ServiceInfo> cachedSRMV11Services = new Hashtable<String, ServiceInfo>();

    // ===============================================================================
    // Constuctors/initializers/Setters/Getters
    // ===============================================================================

    public BdiiService(URI bdiiUri)
    {
        init(bdiiUri, null);
    }

    public BdiiService(URI bdiiUri, URI proxyUri)
    {
        init(bdiiUri, proxyUri);
    }

    private void init(URI bdiiUri, URI proxyUri)
    {
        this.bdiiUri = bdiiUri;
        if (proxyUri == null)
            proxyUri = bdiiUri;
        query = new BDIIQuery(proxyUri);
    }

    public BdiiService(String host, int port)
    {
        try
        {
            this.bdiiUri = new URI("ldap", null, host, port, null, null, null);
            query = new BDIIQuery(bdiiUri);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    /** Returns service URI */
    public URI getURI()
    {
        return this.bdiiUri;
    }

    public void setUseCaching(boolean use)
    {
        this.useCaching = use;
    }

    public void clearCache()
    {
        this._cachedServices.clear();
        this.cachedVOLFCs.clear();
        this.cachedVOStorageAreas.clear();

    }

    // ===============================================================================
    // Generic (VRS) Query API
    // ===============================================================================

    /**
     * Generic method to query for a specified service type and VO. Returns NULL
     * if no match was found. Currently works for: SRMV22.,LFC ,WMS and LB.
     * 
     * @throws URISyntaxException
     */
    public ArrayList<ServiceInfo> queryServiceInfo(String vo, ServiceInfoType type) throws VlException
    {
        switch (type)
        {
            case SRMV22:
            {
                ArrayList<ServiceInfo> infos = new ArrayList<ServiceInfo>();
                ArrayList<StorageArea> sas = getSRMv22SAsforVO(vo);
                for (StorageArea sa : sas)
                {
                    infos.add(sa.getSRMV22Service());
                }
                return infos;
            }
            case WMS:
                return getWMSServiceInfos(vo);
            case LFC:
                return getLFCsforVO(vo);
            case LB:
                return getLBServiceInfosForVO(vo);
            default:
                return null;
        }
    }

    /** Returns SRM V2.2 Service for specified hostname or NULL */
    public ServiceInfo getSRMv22ServiceForHost(String host) throws VlException, NamingException
    {
        logger.debugPrintf("getSRMv22SEforHost:%s\n", host);

        if (useCaching)
        {
            ServiceInfo service=this.getFromServiceCache(ServiceInfoType.SRMV22,host); 
            if (service!=null)
            {
                logger.infoPrintf("+++ Returning cached V2.2 SRM service for %s\n", host);
                return service;
            }
        }

        // Query single hostname but for all VOs to match any hostname
        // and not those allowed for the current User's VO !
        ServiceInfo srm = _querySRMv22SEForHost(host);

        if ((srm != null) && (useCaching))
        {
            putInServiceCache(srm);
        }

        logger.infoPrintf("+++ Returning cached V2.2 SRM service for :%s => %s\n", host, srm);
        return srm;
    }

    public ArrayList<StorageArea> getSRMv22SAsforVO(String vo) throws VlException
    {
        return getVOStorageAreas(vo, null, false);
    }

    /**
     * Returns srm V 1.1 SA for a given VO.
     * 
     * @param vo
     *            the VO's name
     * @return the SA V1.1
     * @throws VlException
     * @throws URISyntaxException
     */
    public ArrayList<StorageArea> getSRMv11SAsforVO(String vo) throws VlException, URISyntaxException
    {
        ArrayList<StorageArea> allSAs = getVOStorageAreas(vo, null, true);

        ArrayList<StorageArea> srmv11 = new ArrayList<StorageArea>();

        for (StorageArea sa : allSAs)
        {
            if (sa.getSRMV11Service() != null)
            {
                srmv11.add(sa);
            }
        }

        return srmv11;
    }

    public ArrayList<ServiceInfo> getLFCsforVO(String vo) throws VlException
    {
        // block other queries:
        synchronized (this.cachedVOLFCs)
        {
            ArrayList<ServiceInfo> lfcs = this.cachedVOLFCs.get(vo);
            if (lfcs != null)
                return lfcs;

            try
            {
                lfcs = _queryLFCServicesForVO(vo);
                // NULL result!
                if (lfcs == null)
                    lfcs = new ArrayList<ServiceInfo>(0);

                this.cachedVOLFCs.put(vo, lfcs);
                return lfcs;
            }
            catch (NamingException e)
            {
                throw new BdiiException("NamingException. Couldn't query Server:" + this, e);
            }
        }
    }

    public ArrayList<StorageArea> getVOStorageAreas(String vo, String optHostname, boolean includeSRMV1)
            throws VlException
    {
        //todo: cleanup,restructure. 
        
        ArrayList<StorageArea> sas = null;
        
        String cacheid=vo+"-"+includeSRMV1; 
        
        synchronized (this.cachedVOStorageAreas)
        {
            if (!useCaching)
                return null;
            
            if (vo == null)
                return null;
            
            sas = cachedVOStorageAreas.get(cacheid);
            
            if (sas != null)
            {
                logger.debugPrintf("Returing cached StorageAreas for VO:%s\n", vo);
            
                // filter out optHostname 
                if (optHostname==null)
                    return sas; 
                else
                    return filterSAsForHost(sas,optHostname);
            }
        }
        
        // Not in cache so let's start
        String versionStr = "*";
        if (includeSRMV1 == false)
            versionStr = "2.2*";

        ArrayList<GlueObject> srmServices = query.getServices(vo, "srm*", versionStr);

        // All the vo paths for a vo. The key for this map is the SE hostname.
        Map<String, String> voPaths = query.getVoInfoPaths(vo);

        // todo:  move this query in different method. This are only needed when
        // browsing LFC,  this take more time. 
        // This method is not getting SEs one by one it performs ONE
        // query and gets back ALL sites. We'll need them later for adding
        // location and coordinate attributes. 
        ArrayList<GlueObject> sites = query.getSitesForGlueObjects(srmServices);

        // We also need them later for adding backend implementations (DPM,etc)
        // free space etc.
        ArrayList<GlueObject> ses = query.getStorageElements(vo);

        ServiceInfo srm = null;
        URI endpointURI;
        String path;
        StorageArea sa;

        StringList missingHostnames = new StringList();
        sas = new ArrayList<StorageArea>();
        for (int i = 0; i < srmServices.size(); i++)
        {

            // // Get Service endpoint...
            // endpointURI = getEndpointURI(srmServices.get(i));
            // // .. and version
            // serviceVersion = getServiceVersion(srmServices.get(i));
            //
            // // clean up string
            // serviceVersion = serviceVersion.replaceAll("[^a-zA-Z0-9]", "");
            //
            // // to determine type
            // if (serviceVersion.startsWith("11"))
            // {
            // serviceType = ServiceInfoType.SRMV11;
            // }
            // else if (serviceVersion.startsWith("22"))
            // {
            // serviceType = ServiceInfoType.SRMV22;
            // }
            //
            // if (StringUtil.isEmpty(endpointURI.toString()) ||
            // StringUtil.isEmpty(endpointURI.getHost()))
            // {
            // logger.warnPrintf("URI is empty for service %s \n",
            // srmServices.get(i).getUid());
            // }

            srm = createSRMServiceInfo(srmServices.get(i));

            // 2st part of the constructor is created
            path = voPaths.get(srm.getHost());

            if (StringUtil.isEmpty(path) || path.contains("/UNDEFINEDPATH"))
            {
                // Gather all missing paths. We'll deal with them later
                missingHostnames.addUnique(GlueConstants.SE_UNIQUE_ID + "=" + srm.getHost());
                // For now skip this
                continue;
            }

            // Get the service attributes...
            VAttribute[] attrs = getServiceVattributes(srmServices.get(i));
            srm.addInfoAttributes(attrs);

            // ...the SE backend attributes
            // seGObj = query.getSEByUID(srm.getHost());

            // this more efficient. For pvier it took about 50 queries to get
            // back all the info with this approach (make all queries before not
            // one by one) it takes about 24
            for (GlueObject se : ses)
            {
                // Assume that hostname and uid match
                if (StringUtil.equals(se.getUid(), srm.getHost()))
                {
                    attrs = getSEBackendVattributes(se);
                    srm.addInfoAttributes(attrs);
                    break;
                }
            }

            // ..the location of services attributes
            // Apparently this is the best way to get back site attributes. All
            // other methods make VBrowser crash.
            VAttribute[] siteAttrs = getSiteAttributes(sites, (String) (String) srmServices.get(i).getExtAttribute(
                    GlueConstants.FOREIN_KEY).get(0));

            srm.addInfoAttributes(siteAttrs);

            sa = new StorageArea(srm, vo, path);

            sas.add(sa);

        }

        if (!missingHostnames.isEmpty())
        {
            // // Fill in missing paths
            voPaths = query.getSAPaths(vo, missingHostnames);

            for (int i = 0; i < srmServices.size(); i++)
            {
                endpointURI = getEndpointURI(srmServices.get(i));

                path = voPaths.get(endpointURI.getHost());
                if (!StringUtil.isEmpty(path))
                {
                    srm = createSRMServiceInfo(srmServices.get(i));
                    // Get the service attributes...
                    VAttribute[] attrs = getServiceVattributes(srmServices.get(i));
                    srm.addInfoAttributes(attrs);

                    // ...the SE backend attributes
                    // seGObj = query.getSEByUID(srm.getHost());

                    // this more efficient. For pvier it took about 50 queries
                    // to
                    // get
                    // back all the info with this approach (make all queries
                    // before
                    // not
                    // one by one) it takes about 24
                    for (GlueObject se : ses)
                    {
                        // Assume that hostname and uid match
                        if (StringUtil.equals(se.getUid(), srm.getHost()))
                        {
                            attrs = getSEBackendVattributes(se);
                            srm.addInfoAttributes(attrs);
                            break;
                        }
                    }

                    // ..the location of services attributes
                    // Apparently this is the best way to get back site
                    // attributes.
                    // All
                    // other methods make VBrowser crash.
                    VAttribute[] siteAttrs = getSiteAttributes(sites, (String) (String) srmServices.get(i)
                            .getExtAttribute(GlueConstants.FOREIN_KEY).get(0));

                    srm.addInfoAttributes(siteAttrs);

                    sa = new StorageArea(srm, vo, path);

                    sas.add(sa);
                }
            }
        }
        
        if (optHostname==null)
            return sas;
        
        return filterSAsForHost(sas,optHostname); 
    }

    private ArrayList<StorageArea> filterSAsForHost(ArrayList<StorageArea> sas, String optHostname)
    {
        if (optHostname==null)
            return sas;
        
        ArrayList<StorageArea> sassel=new ArrayList<StorageArea>(); 
        
        for (int i=0;i<sas.size();i++)
            if (sas.get(i).getHostname().compareToIgnoreCase(optHostname)==0) 
                sassel.add(sas.get(i)); 
        
        return sassel; 
        
    }

    private ServiceInfo createSRMServiceInfo(GlueObject srmService) throws VlException
    {
        URI endpointURI = getEndpointURI(srmService);

        String serviceVersion = getServiceVersion(srmService);
        // clean up string
        serviceVersion = serviceVersion.replaceAll("[^a-zA-Z0-9]", "");

        ServiceInfoType serviceType = null;
        // to determine type
        if (serviceVersion.startsWith("11"))
        {
            serviceType = ServiceInfoType.SRMV11;
        }
        else if (serviceVersion.startsWith("22"))
        {
            serviceType = ServiceInfoType.SRMV22;
        }

        // URI endpointURI = null;
        if (StringUtil.isEmpty(endpointURI.toString()) || StringUtil.isEmpty(endpointURI.getHost()))
        {
            logger.warnPrintf("URI is empty for service %s \n", srmService.getUid());
        }
        ServiceInfo srm = ServiceInfo.createFrom(endpointURI, serviceType);
        if (srm.getHost() == null)
        {
            // logger.warnPrintf("Could not create StorageArea form %s \n",
            // endpointURI);
            throw new VlException("Could not create StorageArea form " + endpointURI);
        }
        else
        {
            return srm;
        }

    }

    public ArrayList<ServiceInfo> getWMSServiceInfos(String vo) throws VlException
    {

        ArrayList<GlueObject> wms = query.getWMProxy(vo);

        // All the sites. This is not getting them one by one it performs ONE
        // query and gets back ALL sites. We'll need them later for adding
        // location and coordinate attributes
        ArrayList<GlueObject> sites = query.getSitesForGlueObjects(wms);

        ArrayList<ServiceInfo> wmsServices = new ArrayList<ServiceInfo>();

        for (int i = 0; i < wms.size(); i++)
        {
            String wmsUri = getEndpointURI(wms.get(i)).toString();
            try
            {
                ServiceInfo serviceInfo = ServiceInfo.createWMService(wmsUri);

                VAttribute[] attrs = getServiceVattributes(wms.get(i));
                serviceInfo.addInfoAttributes(attrs);
                wmsServices.add(serviceInfo);

                // TODO Service Location ---- Move this to getSiteInfoAttributes
                VAttribute[] siteAttrs = getSiteAttributes(sites, (String) wms.get(i).getExtAttribute(
                        GlueConstants.FOREIN_KEY).get(0));

                serviceInfo.addInfoAttributes(siteAttrs);

            }
            catch (URISyntaxException e)
            {
                logger.errorPrintf("Could't parse URI:%s %s\n", wmsUri, e);
            }
        }

        return wmsServices;
    }

    public ArrayList<ServiceInfo> getLBServiceInfosForVO(String vo) throws VlException
    {
        return this.guessLBServiceInfos(vo);
    }

    public ArrayList<ServiceInfo> getLBServiceInfosForWMSHost(String hostname) throws VlException
    {
        ArrayList<ServiceInfo> infos = this.guessLBServiceInfoForWMS(hostname);

        return infos;
    }

    /**
     * Return SRM v1 service. Return NULL when not found. Exception otherwise
     * 
     * @throws URISyntaxException
     */
    public ServiceInfo getSRMV11ServiceForHost(String hostname) throws VlException, URISyntaxException
    {
        ServiceInfo info = this.getFromServiceCache(ServiceInfoType.SRMV11,hostname); 
        if (info!=null)
            return info; 
        
        try
        {
            info = _querySRMv1SEForHost(hostname);
            
            if (info != null)
            {
                this.putInServiceCache(info);
            }
            else
            {
                // block further requests: put nill object.
                info=ServiceInfo.createNill(ServiceInfoType.SRMV11,hostname);
                this.putInServiceCache(info);
            }
        }
        catch (NamingException e)
        {
            throw new BdiiException("NamingException. Couldn't query SRM V1:" + hostname, e);
        }

        return info;
    }

    // ===============================================================================
    // Private/Protected Implementation methods (helper methods for BDII itself)
    // ===============================================================================

    private ArrayList<ServiceInfo> guessLBServiceInfoForWMS(String hostname) throws VlException
    {
        ArrayList<GlueObject> lbs = query.guessLBServicesForWMSHostname(hostname);

        // All the sites. This is not getting them one by one it performs ONE
        // query and gets back ALL sites. We'll need them later for adding
        // location and coordinate attributes
        ArrayList<GlueObject> sites = query.getSitesForGlueObjects(lbs);

        ArrayList<ServiceInfo> lbServices = new ArrayList<ServiceInfo>();

        for (int i = 0; i < lbs.size(); i++)
        {
            String lbUri = (String) lbs.get(i).getExtAttribute(GlueConstants.SERVICE_ENDPOINT).get(0);
            try
            {
                ServiceInfo serviceInfo = ServiceInfo.createLBService(lbUri);
                VAttribute[] attrs = getServiceVattributes(lbs.get(i));
                serviceInfo.addInfoAttributes(attrs);
                lbServices.add(serviceInfo);

                VAttribute[] siteAttrs = getSiteAttributes(sites, (String) lbs.get(i).getExtAttribute(
                        GlueConstants.FOREIN_KEY).get(0));
                serviceInfo.addInfoAttributes(siteAttrs);

            }
            catch (URISyntaxException e)
            {
                logger.errorPrintf("Could't parse URI:%s %s\n", lbUri, e);
            }
        }

        return lbServices;
    }

    private ArrayList<ServiceInfo> guessLBServiceInfos(String vo) throws VlException
    {
        ArrayList<GlueObject> lbs = this.query.guessLBServices(vo);

        // All the sites. This is not getting them one by one it performs ONE
        // query and gets back ALL sites. We'll need them later for adding
        // location and coordinate attributes
        ArrayList<GlueObject> sites = query.getSitesForGlueObjects(lbs);

        ArrayList<ServiceInfo> lbServices = new ArrayList<ServiceInfo>();

        for (int i = 0; i < lbs.size(); i++)
        {
            URI lbUri = getEndpointURI(lbs.get(i));

            try
            {
                ServiceInfo serviceInfo = ServiceInfo.createLBService(lbUri.toString());

                VAttribute[] attrs = getServiceVattributes(lbs.get(i));
                serviceInfo.addInfoAttributes(attrs);
                lbServices.add(serviceInfo);

                VAttribute[] siteAttrs = getSiteAttributes(sites, (String) lbs.get(i).getExtAttribute(
                        GlueConstants.FOREIN_KEY).get(0));
                serviceInfo.addInfoAttributes(siteAttrs);
            }
            catch (URISyntaxException e)
            {
                logger.errorPrintf("Could't parse URI:%s %s\n", lbUri, e);
            }
        }

        return lbServices;
    }

    private VAttribute[] getLocationVattributes(GlueObject seLocationInfo)
    {
        return getGlueVattributes(seLocationInfo, GlueConstants.GRID_SITE_ATTRIBUTES);
    }

    private void putInServiceCache(ServiceInfo srm)
    {
        if (!useCaching)
            return;
        
        String id=srm.getServiceType()+ "-" +srm.getHost();
        
        synchronized (this._cachedServices)
        {
            _cachedServices.put(id,srm); 
        }
    }
    
    private ServiceInfo getFromServiceCache(ServiceInfo.ServiceInfoType type,String host)
    {
        if (!useCaching)
            return null;
        
        String id=type+"-"+host; 

        synchronized (this._cachedServices)
        {
            return  _cachedServices.get(id); 
        }
    }

    private VAttribute[] getGlueVattributes(GlueObject seBackEndInfo, String[] attrConst)
    {

        // logger.debugPrintf("----------------getGlueVattributes------------------\n");

        Vector<Object> glueAtrrbutes;
        ArrayList<VAttribute> vAtrrList = new ArrayList<VAttribute>();
        StringBuffer glueAtrr = new StringBuffer();
        VAttribute[] vAtrr = new VAttribute[attrConst.length];

        for (int i = 0; i < attrConst.length; i++)
        {
            if (seBackEndInfo != null)
            {
                glueAtrrbutes = seBackEndInfo.getExtAttribute(attrConst[i]);
                if (glueAtrrbutes != null)
                {
                    for (int j = 0; j < glueAtrrbutes.size(); j++)
                    {
                        glueAtrr.append((String) glueAtrrbutes.get(j) + ", ");
                    }
                }
            }
            String attrName = null;
            String attrValue = null;
            
            // skip NULL attributes ! 
            if (glueAtrr != null)
            {
                attrName = attrConst[i].substring(4);
                attrValue=glueAtrr.toString(); 
            
                vAtrr[i] = new VAttribute(attrName, attrValue);
                glueAtrr.delete(0, glueAtrr.capacity());
                // logger.debugPrintf(" %s : %s \n",
                // attrName,glueAtrr);
                vAtrrList.add(vAtrr[i]);
            }
        }

        VAttribute[] vAttr = new VAttribute[vAtrrList.size()];
        vAttr = vAtrrList.toArray(vAttr);

        // logger.debugPrintf("----------------------------------------------------\n");

        return vAttr;
    }

    private VAttribute[] getSEBackendVattributes(GlueObject seBackEndInfo)
    {
        return getGlueVattributes(seBackEndInfo, GlueConstants.SE_ATTRIBUTES);
    }

    private VAttribute[] getServiceVattributes(GlueObject seBackEndInfo)
    {
        return getGlueVattributes(seBackEndInfo, GlueConstants.SERVICE_ATTRIBUTES);
    }

    private java.net.URI getEndpointURI(GlueObject glueService) throws VlException
    {
        String serviceUri = null;

        try
        {
            // logger.debugPrintf("-----------------------getEndpointURI----------------------------\n");
            URI endpointURI = null;
            // String srmEndpoint = (String)
            // glueService.getExtAttribute(GlueConstants.SERVICE_ENDPOINT).get(0);

            Vector<Object> endpoints = glueService.getExtAttribute(GlueConstants.SERVICE_ENDPOINT);
            if (endpoints != null)
            {
                for (Object ob : endpoints)
                {
                    // logger.debugPrintf(GlueConstants.SERVICE_ENDPOINT+": %s \n",
                    // ob);

                    if (ob != null)
                    {
                        serviceUri = (String) ob;
                        if (!StringUtil.isEmpty(serviceUri) && !StringUtil.equals(serviceUri, "httpg://:"))
                        {
                            endpointURI = new URI(serviceUri);
                            break;
                        }
                    }
                }
            }

            // not all glue object seem to agree:
            if (endpointURI == null)
            {
                endpoints = glueService.getExtAttribute(GlueConstants.SERVICE_URI);
                if (endpoints != null)
                {
                    for (Object ob : endpoints)
                    {
                        // logger.debugPrintf(GlueConstants.SERVICE_URI+": %s \n",
                        // ob);

                        if (ob != null)
                        {
                            serviceUri = (String) ob;
                            if (!StringUtil.isEmpty(serviceUri) && !StringUtil.equals(serviceUri, "httpg://:"))
                            {
                                endpointURI = new URI(serviceUri);
                                break;
                            }
                        }
                    }
                }
            }

            if (endpointURI == null)
            {
                endpoints = glueService.getExtAttribute(GlueConstants.SERVICE_ACCESS_POINT_URL);
                if (endpoints != null)
                {
                    for (Object ob : endpoints)
                    {
                        // logger.debugPrintf(GlueConstants.SERVICE_ACCESS_POINT_URL+": %s \n",
                        // ob);

                        if (ob != null)
                        {
                            serviceUri = (String) ob;
                            if (!StringUtil.isEmpty(serviceUri) && !StringUtil.equals(serviceUri, "httpg://:"))
                            {
                                endpointURI = new URI(serviceUri);
                                break;
                            }
                        }
                    }
                }
            }

            // One last try in the UID
            if (endpointURI == null)
            {
                logger.warnPrintf("**** Warning: Couldn't get valid endpoint from Glue Service description\n");
                //endpointURI = new URI(glueService.getUid());
                return null;
            }
            else
            {
                return endpointURI;
            }

        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Invalid URI:" + serviceUri, e);
        }
    }

    private String getServiceVersion(GlueObject glueObject)
    {
        Object valObj = glueObject.getExtAttribute(GlueConstants.SERVICE_VERSION);
        if (valObj != null)
            return valObj.toString();
        return null;
    }

    // =======================================================================
    // Actual BDII Queries: Unsynchronized and Uncached !
    // =======================================================================

    // Do actual uncached unsynchronized query;
    private ServiceInfo _querySRMv22SEForHost(String hostname) throws NamingException, VlException
    {
        ArrayList<GlueObject> srmServices = query.getServices("*", hostname, "srm*", "2.2*");

        if ((srmServices == null) || (srmServices.isEmpty()))
            return null;

        String srmEndpoint = (String) srmServices.get(0).getExtAttribute(GlueConstants.SERVICE_ENDPOINT).get(0);
        URI endpointURI;

        try
        {
            endpointURI = new URI(srmEndpoint);
        }
        catch (URISyntaxException e)
        {
            throw new nl.uva.vlet.exception.VRLSyntaxException("URI Syntax error:" + srmEndpoint, e);
        }
        return ServiceInfo.createFrom(endpointURI, ServiceInfoType.SRMV22);
    }

    // Do actual uncached unsynchronized query;
    private ServiceInfo _querySRMv1SEForHost(String hostname) throws NamingException, VlException, URISyntaxException
    {
        ArrayList<GlueObject> srmServices = query.getServices("*", hostname, "srm*", "1*");

        if ((srmServices == null) || (srmServices.isEmpty()))
        {
            logger.warnPrintf("SRM v1 query resulted in NULL for:%s\n", hostname);
            return null;
        }

        URI endpointURI = getEndpointURI(srmServices.get(0));

        return ServiceInfo.createFrom(endpointURI, ServiceInfoType.SRMV11);
    }

    // uncached BDII query
    private ArrayList<ServiceInfo> _queryLFCServicesForVO(String vo) throws NamingException, VlException
    {
        // query for lcg-file-catalog and skip version !
        ArrayList<GlueObject> services = query.getServices(vo, GlueConstants.FILE_CATALOG, null);

        if ((services == null) || (services.isEmpty()))
            return null;

        // All the sites. This is not getting them one by one it performs ONE
        // query and gets back ALL sites. We'll need them later for adding
        // location and coordinate attributes
        ArrayList<GlueObject> sites = query.getSitesForGlueObjects(services);

        ArrayList<ServiceInfo> lfcs = new ArrayList<ServiceInfo>(services.size());

        for (GlueObject service : services)
        {

            URI endpointURI;
            URI endP = null;
            try
            {
                // No port number ? Gah.
                endP = getEndpointURI(service);
                endpointURI = new URI("lfn://" + endP.toString() + ":5010/");

                ServiceInfo lfcInfo = ServiceInfo.createFrom(endpointURI, ServiceInfoType.LFC);

                VAttribute[] attrs = getServiceVattributes(service);
                lfcInfo.addInfoAttributes(attrs);

                VAttribute[] siteAttrs = getSiteAttributes(sites, (String) service.getExtAttribute(
                        GlueConstants.FOREIN_KEY).get(0));
                lfcInfo.addInfoAttributes(siteAttrs);

                lfcs.add(lfcInfo);
            }
            catch (URISyntaxException e)
            {
                throw new nl.uva.vlet.exception.VRLSyntaxException("URI Syntax error:" + endP, e);
            }
        }

        return lfcs;
    }

    private VAttribute[] getSiteAttributes(ArrayList<GlueObject> sites, String serviceKey)
    {
        for (GlueObject gSite : sites)
        {
            String siteUID = gSite.getUid();

            String[] foreinKey = serviceKey.split("=");
            if (foreinKey.length == 2)
            {
                if (StringUtil.equals(foreinKey[1], siteUID))
                {
                    return getLocationVattributes(gSite);
                }
            }
        }

        // Give somthing back, not null
        return null;// new VAttribute[0];
    }

}
