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
 * $Id: SRMStatusOfRequestLsResponse.java,v 1.1 2011-09-21 11:14:20 ptdeboer Exp $  
 * $Date: 2011-09-21 11:14:20 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmStatusOfLsRequestResponse;
import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * 
 * SRMStatusOfRequestLsResponse wraps around a SrmStatusOfLsRequestResponse. 
 * 
 */
public class SRMStatusOfRequestLsResponse implements ISRMStatusOfRequestResponse
{
    private SrmStatusOfLsRequestResponse lsResponse;

    private String requestToken;

    /**
     * Create  SRMStatusOfRequestLsResponse wrapper from SrmStatusOfLsRequestResponse.
     */
    public SRMStatusOfRequestLsResponse(SrmStatusOfLsRequestResponse response)
    {
        this.lsResponse = response;
    }

    @Override
    public Integer getRemainingTotalRequestTime()
    {
        return -1; // not supported;
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return lsResponse.getReturnStatus();
    }

    @Override
    public IFileStatus[] getStatusArray()
    {
        return null;
        
        // ArrayOfTMetaDataPathDetail pathDetails = lsResponse.getDetails(); 
        
    }

    @Override
    public String getToken()
    {
        return this.requestToken;
    }

    @Override
    public void setToken(String requestToken)
    {
        this.requestToken = requestToken;

    }

}
