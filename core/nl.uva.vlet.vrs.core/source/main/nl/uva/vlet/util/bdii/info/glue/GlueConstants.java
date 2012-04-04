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
 * $Id: GlueConstants.java,v 1.10 2011-04-18 12:00:41 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:41 $
 */ 
// source: 

package nl.uva.vlet.util.bdii.info.glue;

public class GlueConstants
{
    // Glue Service constants
    public static final String SERVICE_ENDPOINT = "GlueServiceEndpoint";

    public static final String SERVICE_TYPE = "GlueServiceType";

    public static final String SERVICE = "GlueService";

    public static final String SERVICE_VERSION = "GlueServiceVersion";

    public static final String SERVICE_ACCESS_CONTROL_RULE = "GlueServiceAccessControlRule";

    public static final String SERVICE_STATUS_INFO = "GlueServiceStatusInfo";

    public static final String SERVICE_URI = "GlueServiceURI";

    public static final String SERVICE_ACCESS_POINT_URL = "GlueServiceAccessPointURL";

    public static final String[] SERVICE_ATTRIBUTES = { SERVICE, SERVICE_ENDPOINT, SERVICE_TYPE, SERVICE_VERSION,
            SERVICE_STATUS_INFO, SERVICE_URI, SERVICE_ACCESS_POINT_URL, "GlueServiceName", "GlueServiceStatus",
            "GlueServiceStartTime", "GlueServiceWSDL", "GlueServiceSemantics", "GlueServiceAccessControlRule","GlueServiceOwner"};

    // Service types
    public static final String WMPROXY = "org.glite.wms.WMProxy";

    public static final String LB_SERVER = "org.glite.lb.Server";

    public static final String LB_SERVER_WS = "org.glite.lb.ServerWS";

    // # org.glite.wms.NetworkServer
    // # org.glite.ce.CREAM
    // # org.glite.ce.Monitor
    // # org.glite.rgma.LatestProducer
    // # org.glite.rgma.StreamProducer
    // # org.glite.rgma.DBProducer
    // # org.glite.rgma.CanonicalProducer
    // # org.glite.rgma.Archiver
    // # org.glite.rgma.Consumer
    // # org.glite.rgma.Registry
    // # org.glite.rgma.Schema
    // # org.glite.rgma.Browser
    // # org.glite.rgma.PrimaryProducer
    // # org.glite.rgma.SecondaryProducer
    // # org.glite.rgma.OnDemandProducer
    // # org.glite.RTEPublisher
    // # org.glite.voms
    // # org.glite.voms-admin
    // # org.glite.AMGA
    // # org.glite.FiremanCatalog
    // # org.glite.SEIndex
    // # org.glite.Metadata
    // # org.glite.ChannelManagement
    // # org.glite.FileTransfer
    // # org.glite.FileTransferStats
    // # org.glite.ChannelAgent
    // # org.glite.Delegation
    // # org.glite.KeyStore
    // # org.glite.FAS
    // # org.glite.gliteIO
    // # SRM
    // # srm_v1 (obsolete)
    // # gsiftp
    // # org.edg.local-replica-catalog
    // # org.edg.replica-metadata-catalog
    // # org.edg.SE
    // # org.edg.gatekeeper
    // # it.infn.GridICE
    // # gridice (currently in use)
    // # MyProxy
    // # GUMS
    // # gridmap-file
    // # GridCat
    // # edu.caltech.cacr.monalisa
    // # OpenSSH
    // # MDS-GIIS
    // # BDII
    // # bdii_site
    // # bdii_top
    // # VOBOX
    // # msg.broker.rest
    // # msg.broker.stomp
    // # Nagios
    // # RLS
    // # data-location-interface
    // # local-data-location-interface
    public static final String FILE_CATALOG = "lcg-file-catalog";

    // # lcg-local-file-catalog
    // # pbs.torque.server
    // # pbs.torque.maui
    // # other

    // Glue SA constants
    public static final String SA_ACCESS_CONTROL_BASE_RULE = "GlueSAAccessControlBaseRule";

    public static final String SA_PATH = "GlueSAPath";

    public static final String SA = "GlueSA";

    // Glue Grid Site
    public static final String SITE = "GlueSite";

    public static final String SITE_LOCATION = "GlueSiteLocation";

    public static final String SITE_UNIQUE_ID = "GlueSiteUniqueID";

    public static final String[] GRID_SITE_ATTRIBUTES = { "GlueSiteDescription", SITE_LOCATION, "GlueSiteLongitude",
            "GlueSiteLatitude" };

    // Glue Cluster
    public static final String CLUSTER = "GlueCluster";

    // Glue keys
    public static final String FOREIN_KEY = "GlueForeignKey";

    public static final String CHUNK_KEY = "GlueChunkKey";

    public static final String SE_ACCESS_PROTOCOL = "GlueSEAccessProtocol";

    // Glue CE
    public static final String CE_ACCESS_CONTROL_BASE_RULE = "GlueCEAccessControlBaseRule";

    public static final String CE = "GlueCE";

    public static final String CE_UNIQUE_ID = "GlueCEUniqueID";

    // Glue SE
    public static final String SE_UNIQUE_ID = "GlueSEUniqueID";

    public static final String SE = "GlueSE";

    public static final String[] SE_ATTRIBUTES = { "GlueSEName", "GlueSEPort", "GlueSEHostingSL",
            "GlueInformationServiceURL", "GlueSEArchitecture", "GlueSEStatus", "GlueSEImplementationName",
            "GlueSETotalNearlineSize", "GlueSchemaVersionMinor", "GlueSEUsedNearlineSize",
            "GlueSEImplementationVersion", "GlueSchemaVersionMajor", "GlueSEUniqueID", "GlueSETotalOnlineSize",
            "GlueSESizeFree", "GlueSEUsedOnlineSize", "GlueSESizeTotal" };

    // VO View
    public static final String VO_INFO = "GlueVOInfo";

    public static final String VO_INFO_PATH = "GlueVOInfoPath";

    // other
    public static final String ACCESS_POINT_URL = "GlueAccessPointURL";

    public static final String OBJECT_CLASS = "objectClass";

    public static final String TOP = "GlueTop";

    public static final String VO_INFO_ACCSESS_CONTROL_BASE_RULE = "GlueVOInfoAccessControlBaseRule";

}
