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
 * $Id: InfoConstants.java,v 1.1 2011-11-25 13:40:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:48 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors;

import nl.uva.vlet.vrs.VRS;

public class InfoConstants
{
    // schemes 
    
    public static final String INFO_SCHEME = "info";
    
    // Types 
    
    public static final String GRID_NEIGHBOURHOOD_NAME="Grid Neighbourhood";
    
    public static final String GRID_NEIGHBOURHOOD_TYPE=VRS.INFO_GRID_NEIGHBOURHOOD;  // "GridNeighbourhood";

    // Generic non editable info type: 
    public static final String INFONODE_TYPE="InfoNode";

    public static final String VO_TYPE = "VO";
    
    // names,also used as info path!  
    
    public static final String STORAGE_INFO_NAME="Storage";
    
    public static final String COMPUTATIONAL_INFO_NAME="Computional";
    
    public static final String FILECATALOGS_INFO_NAME="FileCatalogs";

    public static final String VOGROUPS_FOLDER_NAME = "VOs";
    
    public static final String VOGROUPS_FOLDER_TYPE = "VOGroupsFolder";
    
    // ids, other consts  
    
    public static final String INFO_INSTANCE_ID="info-resourcesystem-id";
    
    public static final String NETWORK_INFO = "NetworkInfo"; 

    public static final String HOST_INFO_NODE = "HostInfo";

    // === Attributes === 
    public static final String ATTR_NETWORK_ADRESS="networkAddress";

    public static final String ATTR_TCP_CONNECTION_TIMEOUT = "tcpConnectionTimeout";

    public static final String LOCALSYSTEM_TYPE = VRS.INFO_LOCALSYSTEM; // "LocalSystem";
    
    public static final String LOCALSYSTEM_NAME = "Local System";

    public static final String ATTR_CONFIGURED_VOS = "configuredVOs";

    public static final String ATTR_SYSTEMHOSTNAME = "systemHostname"; ;
    
    public static final String ATTR_SYSTEMOS = "systemOS";

    public static final String ATTR_JAVAVERSION = "javaVersion";

    public static final String ATTR_JAVAHOME = "javaHome";   
    

}
