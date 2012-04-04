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
 * $Id: VRenamable.java,v 1.5 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/** 
 * Interface for rename methods. <br> 
 * If a resource can be renamed it implements this interface.  
 * Both the full path of a resource or the "basename" can be used in the rename method. 
 * @author P.T. de Boer
 */
public interface VRenamable
{
    // Removed: 
    // public boolean renameTo(String newName,boolean renameFullPath) throws VlException;

    /** 
     * Rename this resource.<br>
     * Since for some implementations (<code>java.io.File</code>) a full path
     * is needed. The parameter renameFullPath specifies whether only 
     * the basename (last part of path) or the full path is given 
     * as new name to avoid this ambiguity !<br>
     * For <see>java.io.file</see> a renameTo can also be used as a move ! 
     * <p>
     * In the case that the target name already exists, the method should fail. 
     * So if rename is implemented with move, the the overwrite option should be false.     
     * <p>
     * Method returns the new VRL of the renamed resource. 
     * Not all renamed resources have their VRL changed but some might, 
     * like file and directories!.
     * @param newName new (base)name or full path.   
     * @param nameIsPath : whether new name specified full path or only the (new) basename. 
     * @return new VRL or similar if the name changed didn't effect the VRL. 
     * @throws VlException
     */
    public VRL rename(String newName,boolean renameFullPath)  throws VlException;
    
    /**
     * Returns true is the resource can be renamed  with the current credentials.
     * When false is returned, this doesn't mean this resource can't be renamed  
     * in general (if not, this interface wouldn't be implemented)
     * just that the current permission prohibit it. 
     * Default implementation for a VFSNode is to check whether it is writable.
     *  
     * @see VFSNode.isRenamable(); 
     * 
     * @return true if the current user can rename this resource. 
     * @throws VlException 
     */ 
    public boolean isRenamable() throws VlException; 
    
}
