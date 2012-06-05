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
 * $Id: WMSConstants.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import nl.uva.vlet.data.VAttributeConstants;

public class WMSConstants
{
    // === 
    // Attribute Constants 
    // === 
    
    // WMS Reason = (VJob) Status Information
    public static final String ATTR_WMS_REASON =  "wmsReason"; 
    public static final String ATTR_JOB_STATUS_INFORMATION=VAttributeConstants.ATTR_JOB_STATUS_INFORMATION;
    
    //public static final String ATTR_WMS_EXPECTED_UPDATE = "wmsExpectedUpdate";
    public static final String ATTR_WMS_DESTINATION = "wmsDestination";
    // WMS "State Entered" = VJob "Submission Time". 
    public static final String ATTR_WMS_STATE_ENTERED_TIME = "wmsStateEntered";
    public static final String ATTR_JOB_SUBMISSION_TIME = VAttributeConstants.ATTR_JOB_SUBMISSION_TIME;
    // WMS "Last Update" =  VJob "Status Update"
    public static final String ATTR_JOB_STATUS_UPDATE_TIME = VAttributeConstants.ATTR_JOB_STATUS_UPDATE_TIME;
    public static final String ATTR_WMS_LAST_UPDATE_TIME = "wmsLastUpdate";
    
    
    // ===
    // Types! 
    // ===
    public static final String TYPE_SUBMITED_JDL = "SubmittedJDL";
    public static final String TYPE_CONDOR_JDL = "CondorJDL";
    public static final String TYPE_MATCHED_JDL = "MatchedJDL";
    public static final String JDL_TYPE = "JDL";
    public static final String GLITE_JDL_MIMETYPE = "application/glite-jdl";
    public static final String ATTR_WMS_SERVER_URI = "wmsServerUri";
    public static final String ATTR_WMS_SERVER_HOSTNAME = "wmsServerHostname";
    public static final String TYPE_LBRESOURCE = "LBServer";
    public static final String TYPE_MYJOBS = "MyJobs";
    public static final String TYPE_EVENT = "Event";
    public static final String TYPE_JDL_COMPOSITE = "jdl";

    // public static final String
    // ATTR_STATUS_UPDATE_WAIT_TIME="statusUpdateWaitTime";
}
