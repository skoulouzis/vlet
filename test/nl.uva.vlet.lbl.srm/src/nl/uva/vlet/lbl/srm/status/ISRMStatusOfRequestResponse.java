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
 * $Id: ISRMStatusOfRequestResponse.java,v 1.8 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * Interface of SRM Responses. An abstract representation of all SRM request
 * responses. Created as an interface for all srm request responses. For example
 * when sending an
 * <code>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest</code> (that is
 * requesting the status of a Get request ), the SRM services replies with a
 * <code>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse</code> (that is the
 * response for the status of the Get request). This interface wraps all
 * RequestResponse (Get, Put, Copy)
 * 
 * @author S. Koulouzis
 * 
 */
public interface ISRMStatusOfRequestResponse
{

    /**
     * 
     * Gets the status of the response.
     * 
     * @return the response status.
     */
    public TReturnStatus getReturnStatus();

    /**
     * Gets the remaining total request time. It indicates how long the
     * desiredTotalRequestTime is left.
     * 
     * @return the remaining total request time.
     */
    public Integer getRemainingTotalRequestTime();

    /**
     * Gets the token for this response. It's a string uniquely identifying each
     * response.
     * 
     * @return the response token.
     */
    public String getToken();

    /**
     * Gets an array containing the status of files.
     * 
     * @return the files status
     */
    public IFileStatus[] getStatusArray();

    /**
     * Sets the request token.
     * 
     * @param requestToken
     *            the request token
     */
    public void setToken(String requestToken);

}
