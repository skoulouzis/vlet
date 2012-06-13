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
 * $Id: VActiveTransferable.java,v 1.4 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VFileActiveTransferable;
import nl.uva.vlet.vrl.VRL;

/** 
 * Super interface for VFileActiveTransferable and VDirActiveTransferable.  
 * "Active Transferable Resources" can perform the copy themself in a more efficient
 * way then the VRS. 
 * By default the VRS will use stream copy as main copy methods which might not always
 * be the best way to perform a copy.  
 * If a resource implements a more efficient way to copy, for example bulk methods or third
 * party copy mechanics, this interface can be used.
 *  
 * @see nl.uva.vlet.vfs.VFileActiveTransferable
 * @see nl.uva.vlet.vfs.VDirActiveTransferable
 */
public interface VActiveTransferable
{
    /** @see nl.uva.vlet.vfs.VFileActiveTransferable#canTransferTo(VRL, StringHolder) */
    boolean canTransferTo(VRL remoteLocation,StringHolder explanation) throws VlException;
    
    /** @see VFileActiveTransferable#canTransferFrom(VRL, StringHolder) */
    boolean canTransferFrom(VRL remoteLocation,StringHolder explanation) throws VlException;
    
    /** @see VFileActiveTransferable#activePartyTransferTo(ITaskMonitor, VRL) */
    VNode activePartyTransferTo(ITaskMonitor monitor,VRL remoteLocation) throws VlException;
    
    /** @see VFileActiveTransferable#activePartyTransferFrom(ITaskMonitor, VRL) */
    VNode activePartyTransferFrom(ITaskMonitor monitor,VRL remoteLocation) throws VlException; 
}
