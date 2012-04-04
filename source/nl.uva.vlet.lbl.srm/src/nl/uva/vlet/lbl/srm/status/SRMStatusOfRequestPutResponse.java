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
 * $Id: SRMStatusOfRequestPutResponse.java,v 1.8 2011-09-21 11:14:32 ptdeboer Exp $  
 * $Date: 2011-09-21 11:14:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse;
import gov.lbl.srm.v22.stubs.TPutRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse</code> ,
 * returned from <coed>srmStatusOfPutRequest</code> from the SRM service, in a
 * common interface for all srm request responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMStatusOfRequestPutResponse implements ISRMStatusOfRequestResponse
{
    private SrmStatusOfPutRequestResponse putResponse;

    private String requestToken;

    /**
     * Creates an instance of a
     * <code>nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestPutResponse</code>
     * 
     * @param putResponse
     *            the
     *            <code>gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse</code>
     */
    public SRMStatusOfRequestPutResponse(SrmStatusOfPutRequestResponse putResponce)
    {
        this.putResponse = putResponce;
    }

    @Override
    public Integer getRemainingTotalRequestTime()
    {
        return putResponse.getRemainingTotalRequestTime();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return putResponse.getReturnStatus();
    }

    @Override
    public IFileStatus[] getStatusArray()
    {
        TPutRequestFileStatus[] putRequestFileStatus = putResponse.getArrayOfFileStatuses().getStatusArray();
        PutRequestFileStatus[] satatusArray = new PutRequestFileStatus[putRequestFileStatus.length];
        for (int i = 0; i < satatusArray.length; i++)
        {
            satatusArray[i] = new PutRequestFileStatus(putRequestFileStatus[i]);
        }
        return satatusArray;
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
