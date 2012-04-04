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
 * $Id: SRMCpResponse.java,v 1.9 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmCopyResponse;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmCopyResponse</code>, returned from
 * <coed>srmCopy</code> from the SRM service, in a common interface for all srm
 * responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMCpResponse implements ISRMResponse
{

    private SrmCopyResponse cpResponce;

    private URI[] suris;

    /**
     * Creates an instance of an
     * <code>nl.uva.vlet.lbl.srm.status.SRMCpResponse</code>.
     * 
     * @param cpResponce
     *            the copy response obtained from <coed>srmCopy</code> in
     *            <code>ISRM</code>
     * @param suris
     *            the SRm URIs
     */
    public SRMCpResponse(SrmCopyResponse cpResponce, org.apache.axis.types.URI[] suris)
    {
        this.cpResponce = cpResponce;
        this.suris = suris;
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return cpResponce.getReturnStatus();
    }

    @Override
    public URI[] getSURIs()
    {
        return this.suris;
    }

    @Override
    public String getRequestToken()
    {
        return cpResponce.getRequestToken();
    }

    // @Override
    // public void setSURLs(URI[] suris)
    // {
    // this.suris = suris;
    // }

}
