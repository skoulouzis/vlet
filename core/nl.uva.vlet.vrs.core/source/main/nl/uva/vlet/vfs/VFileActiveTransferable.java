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
 * $Id: VFileActiveTransferable.java,v 1.4 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VActiveTransferable;
import nl.uva.vlet.tasks.ITaskMonitor;

/**
 * The ActiveTransferables contain methods to indicate that
 * this resource can be the active party when doing Resource Transfers 
 * for example third party file copying or some other form of optimized
 * file transfer (using proxy copying or reliable file transfers).  
 * <br>
 * Use this interface to indicate that this resource wants to do 
 * the transfer itself instead of the default VRS/VFS copy mechanism which
 * is based upon stream read and write method.  
 * This allows for optimal transfers between different resources.
 * <br> 
 * If this resource supports "true" 3rd party transfer use the 
 * VThirdPartyTransferable interface to indicate this. 
 * 
 * @author P.T. de Boer
 * 
 * @see VThirdPartyTransferable 
 */
public interface VFileActiveTransferable extends VActiveTransferable
{
    /** 
     * Checks whether this resource can perform an optimized (third party) transfer
     * to the remote location. 
     * The StringHolder explanation might hold the reason why it can or can't
     * perform the transfer. 
     * 
     * @param  remoteLocation remote destination file to copy to.  
     * @param  explanation  extra information why it can or can't do the transfer. 
     * @return true if this method has an optimized way to do the transfer.
     * @see    #canTransferFrom(VRL, StringHolder)
     */
    boolean canTransferTo(VRL remoteLocation,StringHolder explanation) throws VlException; 

    /**
     *Similar to canTransferTo but with the active and passive parties reversed.
     *
     * @param  remoteLocation remote source file to copy from
     * @param  explanation  extra information why it can or can't do the transfer. 
     * @return true if this method has an optimized way to do the transfer.
     * @see    #canTransferTo(VRL, StringHolder)
     */ 
    boolean canTransferFrom(VRL remoteLocation,StringHolder explanation) throws VlException; 

    /**
     * Perform Active Transfer. Remote location is new File location. 
     * Implementation might choose to create parent directory as well.
     * @param  monitor TaskMonitor: Use startSubTask() and updateSubTaskDone() to update 
     *                 current transfer statistics as this file can be a subTask in a larger
     *                 transfer action (directory copy).  
     * @param  remoteLocation remote destination file to copy to.  
     * @return new created VFile
     */ 
    VFile activePartyTransferTo(ITaskMonitor monitor,VRL remoteLocation) throws VlException; 
    
    /**
     * Perform Active Transfer. Remote location is source File. 
     * Implementation might choose to create parent directory as well.
     * If this File doesn't exist yet it will be created by copying the
     * remoteLocation to this File. 
     * <p>
     * Since this method might update this VFile, call this method as follows:<br>
     * <code> VFile targetFile=targetFile.activePartyTransferFrom(monitor,vrl); </code> 
     * <br>
     * @param  monitor TaskMonitor: Use startSubTask() and updateSubTaskDone() for transfer 
     *                 current transfer statistics as this file can be a subTask in a larger
     *                 transfer action (directory copy).   
     * @param  remote source file to copy from 
     * @return new created VFile which should match the VFile implementing this interface ! 
     *         Although the implementation might choose to create a new one. 
     */ 
    VFile activePartyTransferFrom(ITaskMonitor monitor,VRL remoteLocation) throws VlException; 
}
