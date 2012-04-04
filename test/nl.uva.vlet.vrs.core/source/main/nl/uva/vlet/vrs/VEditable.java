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
 * $Id: VEditable.java,v 1.5 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;

/**
 * This interface provides some methods for nodes or resources which attributes
 * are 'editable'.   
 * 
 * @author P.T. de Boer
 */
public interface VEditable
{
    /** 
     * returns true if the caller has the permissions to edit this resource. 
     * This means the setAttribute(s) method(s) are allowed. 
     * The default implementation for a VFSNode is to check whether
     * it is writable
     */
    public boolean isEditable() throws VlException;
    
    /**
     * Sets a list of attributes. Returns true if all attributes could be set.
     */ 
    public boolean setAttributes(VAttribute[] attrs) throws VlException;

    /** Set single attribute. Return true if attribute was set. */  
    public boolean setAttribute(VAttribute attr) throws VlException;
    
}
