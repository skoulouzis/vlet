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
 * $Id: CopyRequestFileStatus.java,v 1.8 2011-04-18 12:21:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:33 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.TCopyRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;
import org.apache.axis.types.UnsignedLong;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.TCopyRequestFileStatus</code>, returned
 * from <coed>gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse </code>, in a
 * common interface for all srm file statuses.
 * 
 * @author S. koulouzis
 * 
 */
public class CopyRequestFileStatus implements IFileStatus
{

    private TCopyRequestFileStatus tCopyRequestFileStatus;

    /**
     * Creates an instance of
     * <code>nl.uva.vlet.lbl.srm.status.CopyRequestFileStatus</code>
     * 
     * @param tCopyRequestFileStatus
     *            the <code>gov.lbl.srm.v22.stubs.TCopyRequestFileStatus</code>
     *            obtained from
     *            <code>gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse</code>
     */
    public CopyRequestFileStatus(TCopyRequestFileStatus tCopyRequestFileStatus)
    {
        this.tCopyRequestFileStatus = tCopyRequestFileStatus;
    }

    @Override
    public Integer getEstimatedWaitTime()
    {
        return tCopyRequestFileStatus.getEstimatedWaitTime();
    }

    @Override
    public UnsignedLong getFileSize()
    {
        return tCopyRequestFileStatus.getFileSize();
    }

    @Override
    public Integer getRemainingFileLifetime()
    {
        return tCopyRequestFileStatus.getRemainingFileLifetime();
    }

    @Override
    public Integer getRemainingPinLifetime()
    {
        return null;
    }

    @Override
    public Integer getRemainingPinTime()
    {
        return null;
    }

    @Override
    public URI getSURL()
    {
        return null;
    }

    @Override
    public TReturnStatus getStatus()
    {
        return tCopyRequestFileStatus.getStatus();
    }

    @Override
    public ArrayOfTExtraInfo getTransferProtocolInfo()
    {
        return null;
    }

    public URI getSourceSURL()
    {
        return tCopyRequestFileStatus.getSourceSURL();
    }

    public URI getgetTargetSURL()
    {
        return tCopyRequestFileStatus.getTargetSURL();
    }

    @Override
    public URI getTransferURL()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
