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
 * $Id: ISRMResponse.java,v 1.9 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * Interface of SRM responses. An abstract representation of all SRM responses.
 * Created as an interface for all srm responses (i.e.
 * gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse,
 * gov.lbl.srm.v22.stubs.SrmPrepareToGetRequest).
 * 
 * @author S. Koulouzis
 * 
 */
public interface ISRMResponse
{
    /**
     * Gets the SRM URIs. It's the set of files that the response is about.
     * 
     * @return the SRM URIs
     */
    public org.apache.axis.types.URI[] getSURIs();

    /**
     * Gets the status of the response.
     * 
     * @return the response status.
     */
    public TReturnStatus getReturnStatus();

    /**
     * Gets the request token for this request. It's a string uniquely
     * identifying each response.
     * 
     * @return the request token.
     */
    public String getRequestToken();

}
