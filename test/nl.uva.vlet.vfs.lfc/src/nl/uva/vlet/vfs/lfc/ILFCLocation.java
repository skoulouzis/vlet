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
 * $Id: ILFCLocation.java,v 1.2 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/** Shared method between LFCFile and LFCDirectory */ 
public interface ILFCLocation
{
    boolean isFile(); 
    
    boolean isDir(); 
    
    boolean isSymbolicLink() throws VlException;
    
    String getPath();

    FileDescWrapper getWrapperDesc() throws VlException;

    String getName();

    VRL getVRL(); 

    /**
     * Return this file GUID or the GUID from the link target
     * if this file is a symbolic link. 
     * @return
     * @throws VlException
     */
    String getGUID() throws VlException;

 
}
