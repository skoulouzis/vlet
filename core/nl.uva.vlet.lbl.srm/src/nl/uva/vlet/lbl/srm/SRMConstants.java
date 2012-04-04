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
 * $Id: SRMConstants.java,v 1.9 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm;

public class SRMConstants
{
    /** Default srm v1.1 path */
    public static final String SRM_SERVICE_V1_URL_PATH = "/srm/managerv1";

    /** Default srm v2.2 path */
    public static final String SRM_SERVICE_V2_URL_PATH = "/srm/managerv2";

    /** Default srm port number */
    public static final int DEFAULT_SRM_PORT = 8443;

    /** GridFTP (default) transport protocol */
    public static final String GSIFTP_PROTOCOL = "gsiftp";

    /** rfio transport protocol */
    public static final String RFIO_PROTOCOL = "rfio";

    /** Implementation backends for srm 2.2 */
    public static String BESTMAN_BACKEND_NAME = "BESTMAN";

    public static String CASTOR_BACKEND_NAME = "CASTOR";

    public static String DCACHE_BACKEND_NAME = "DCACHE";

    public static String DPM_BACKEND_NAME = "DPM";

    public static String STORM_BACKEND_NAME = "STORM";

    public static final String[] BACKEND_NAMES = { BESTMAN_BACKEND_NAME, CASTOR_BACKEND_NAME, DCACHE_BACKEND_NAME,
            DPM_BACKEND_NAME, STORM_BACKEND_NAME };

}
