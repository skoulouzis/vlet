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
 * $Id: IFileStatus.java,v 1.8 2011-04-18 12:21:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:32 $
 */ 
// source: 

package nl.uva.vlet.lbl.srm.status;

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.UnsignedLong;

/**
 * Interface of file status. An abstract representation of a file status.
 * Created as an interface for all srm file statues i.e.
 * gov.lbl.srm.v22.stubs.TPutRequestFileStatus,
 * gov.lbl.srm.v22.stubs.TGetRequestFileStatus,
 * gov.lbl.srm.v22.stubs.TCopyRequestFileStatus. It is used in the
 * <coed>getStatusArray()</code> in the
 * nl.uva.vlet.lbl.srm.status.ISRMStatusOfRequestResponse interface.
 * 
 * @author S. koulouzis
 * 
 */
public interface IFileStatus
{

    /**
     * Gets transfer protocol informations. For example additional information
     * for transfer protocols, i.e. access speed and other transfer protocol
     * properties. ( see
     * https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html#_Toc241633070)
     * 
     * @return the array of extra information
     */
    public ArrayOfTExtraInfo getTransferProtocolInfo();

    /**
     * Returns the lifetime on the SRM URL. 0 means it expired, -1 its lifetime
     * is indefinite.
     * 
     * @return the file's remaining life time
     */
    public Integer getRemainingFileLifetime();

    /**
     * Gets the lifetime on the Transport URL. 0 means it expired, -1 its
     * lifetime is indefinite. In other words the time the file will be in
     * cache.
     * 
     * @return the file's remaining pin life time
     */
    public Integer getRemainingPinLifetime();

    /**
     * Returns the estimated wait time. This value is not to be trusted. It is
     * usually null
     * 
     * @return the estimated wait time
     */
    public Integer getEstimatedWaitTime();

    /**
     * Gets the remaining lifetime on the Transport URL. 0 means it expired, -1
     * its lifetime is indefinite.
     * 
     * @return the files remaing pin time.
     */
    public Integer getRemainingPinTime();

    /**
     * Gets the file's size.
     * 
     * @return the file size.
     */
    public UnsignedLong getFileSize();

    /**
     * Gets the return status for a request. see
     * https://sdm.lbl.gov/srm-wg/doc/SRM.v2.2.html#_Toc241633055
     * 
     * @return the return status
     */
    public TReturnStatus getStatus();

    /**
     * Gets the SRM URL. for this file.
     * 
     * @return the SRM URL
     */
    public org.apache.axis.types.URI getSURL();

    /**
     * Gets the Transfer URL for this file
     * 
     * @return Transfer URL.
     */
    public org.apache.axis.types.URI getTransferURL();

}
