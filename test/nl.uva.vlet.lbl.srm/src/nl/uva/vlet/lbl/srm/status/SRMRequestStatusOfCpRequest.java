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
 * $Id: SRMRequestStatusOfCpRequest.java,v 1.8 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * Implementation of Request status of copy request. It's used as a container
 * object for constructing
 * <code>gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestRequest</code> (mainly for
 * holding the return status and request token)
 * 
 * @author S. Koulouzis
 * 
 */
public class SRMRequestStatusOfCpRequest implements ISRMRequestStatusOfRequest
{

    private SRMCpResponse responce;

    /**
     * Creates an instance of
     * <code>nl.uva.vlet.lbl.srm.status.SRMRequestStatusOfCpRequest</code>.
     * 
     * @param responce
     *            the <code>nl.uva.vlet.lbl.srm.status.SRMCpResponse</code>
     *            constructed when getting back
     *            <code>gov.lbl.srm.v22.stubs.SrmCopyResponse</code> from the
     *            SRM service.
     */
    public SRMRequestStatusOfCpRequest(SRMCpResponse responce)
    {
        this.responce = responce;
    }

    @Override
    public String getRequestToken()
    {
        return responce.getRequestToken();
    }

    public URI[] getSourceSURLs()
    {
        return responce.getSURIs();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return responce.getReturnStatus();
    }

    @Override
    public URI[] getSURIs()
    {
        return responce.getSURIs();
    }

}
