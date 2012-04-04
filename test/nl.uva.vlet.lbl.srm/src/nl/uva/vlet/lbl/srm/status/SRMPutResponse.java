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
 * $Id: SRMPutResponse.java,v 1.9 2011-09-21 11:14:32 ptdeboer Exp $  
 * $Date: 2011-09-21 11:14:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmPrepareToPutResponse</code>,
 * returned from <coed>srmPrepareToPut</code> from the SRM service, in a common
 * interface for all srm responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMPutResponse implements ISRMResponse
{
    private SrmPrepareToPutResponse prepareToPutResponse;

    private org.apache.axis.types.URI[] suris;

    /**
     * Creates an instance of an
     * <code>nl.uva.vlet.lbl.srm.status.SRMPutResponse</code>.
     * 
     * @param responce
     *            the put response obtained from from
     *            <code>srmPrepareToPut</code> in <code>ISRM</code>
     */
    public SRMPutResponse(SrmPrepareToPutResponse prepareToPutResponse, org.apache.axis.types.URI[] suris)
    {
        this.prepareToPutResponse = prepareToPutResponse;
        this.suris = suris;
    }

    @Override
    public String getRequestToken()
    {
        return prepareToPutResponse.getRequestToken();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return prepareToPutResponse.getReturnStatus();
    }

    @Override
    public URI[] getSURIs()
    {
        return this.suris;
    }

    // @Override
    // public void setSURLs(org.apache.axis.types.URI[] suris)
    // {
    // this.suris = suris;
    // }

}
