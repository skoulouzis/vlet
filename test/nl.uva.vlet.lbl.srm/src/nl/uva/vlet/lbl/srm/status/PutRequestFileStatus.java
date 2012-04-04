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
 * $Id: PutRequestFileStatus.java,v 1.8 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.TPutRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;
import org.apache.axis.types.UnsignedLong;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.TPutRequestFileStatus</code>, returned
 * from <coed>gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse </code>, in a
 * common interface for all srm file statuses.
 * 
 * @author S. koulouzis
 * 
 */
public class PutRequestFileStatus implements IFileStatus
{

    private TPutRequestFileStatus putRequestFileStatus;

    /**
     * Creates an instance of
     * <code>nl.uva.vlet.lbl.srm.status.PutRequestFileStatus</code>
     * 
     * @param tPutRequestFileStatus
     *            the the
     *            <code>gov.lbl.srm.v22.stubs.TPutRequestFileStatus</code> ,
     *            obtained from
     *            <coed>gov.lbl.srm.v22.stubs.SrmStatusOfPutRequestResponse
     *            </code>
     */
    public PutRequestFileStatus(TPutRequestFileStatus tPutRequestFileStatus)
    {
        this.putRequestFileStatus = tPutRequestFileStatus;
    }

    @Override
    public Integer getEstimatedWaitTime()
    {
        return putRequestFileStatus.getEstimatedWaitTime();
    }

    @Override
    public UnsignedLong getFileSize()
    {
        return putRequestFileStatus.getFileSize();
    }

    @Override
    public Integer getRemainingFileLifetime()
    {
        return putRequestFileStatus.getRemainingFileLifetime();
    }

    @Override
    public Integer getRemainingPinLifetime()
    {

        return putRequestFileStatus.getRemainingPinLifetime();
    }

    @Override
    public Integer getRemainingPinTime()
    {
        return null;
    }

    @Override
    public URI getSURL()
    {
        return putRequestFileStatus.getSURL();
    }

    @Override
    public TReturnStatus getStatus()
    {
        return putRequestFileStatus.getStatus();
    }

    @Override
    public ArrayOfTExtraInfo getTransferProtocolInfo()
    {
        return putRequestFileStatus.getTransferProtocolInfo();
    }

    @Override
    public URI getTransferURL()
    {
        return putRequestFileStatus.getTransferURL();
    }

}
