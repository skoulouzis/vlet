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
 * $Id: VResizable.java,v 1.6 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.vrs.io;

import nl.uva.vlet.exception.VlException;

/**
 * Interface for (file) resources which length can be manipulated. 
 *  
 * @author Piter T. de Boer 
 */

public interface VResizable extends VZeroSizable, VSize
{
    // Explicit inheritance from  VSize 
    public long getLength() throws VlException; 

    // Explicit inheritance from  VZeroSizable 
    public void setLengthToZero()  throws VlException;
    
	public void setLength(long newLength)  throws VlException;
	
}

