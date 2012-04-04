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
 * $Id: SRMConstants.java,v 1.2 2011-04-18 12:21:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:29 $
 */ 
// source: 

package nl.uva.vlet.vfs.srm;

import gov.lbl.srm.v22.stubs.TFileStorageType;

public class SRMConstants 
{
	public static final String ATTR_SRM_RETENTION_POLICY = "srmRetentionPolicy";
	
    public static final String ATTR_SRM_STORAGE_TYPE = "srmStorageType";

    public static final String STORAGE_TYPE_PERMANENT =   TFileStorageType._PERMANENT;
    
    public static final String STORAGE_TYPE_DURABLE =   TFileStorageType._PERMANENT;
    
    public static final String STORAGE_TYPE_VOLATILE =   TFileStorageType._VOLATILE;
	
}
