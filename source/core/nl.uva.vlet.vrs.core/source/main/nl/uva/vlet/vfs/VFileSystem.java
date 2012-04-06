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
 * $Id: VFileSystem.java,v 1.7 2011-06-22 14:41:13 ptdeboer Exp $  
 * $Date: 2011-06-22 14:41:13 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VResourceSystem;
import nl.uva.vlet.vrs.VStreamProducer;


/**
 * VFileSystem is a factory class for VFSNodes. 
 * Its purpose is to create new VFile or VDir objects. 
 * The interface is limited to factory methods. Use the returned VFile and VDir object(s)
 * for further file and directory manipulation. 
 * <br>
 * Use:<br>
 * <lu>
 *  <li> - openFile() and openDir() to fetch existing files or directories.  
 *  <li> - newFile and newDir() to create new VFile and VDir objects which may 
 *       point to existing locations or not. 
 *  <li> - Use exists() and/or create() method from VDir and VFile to check existance
 *       and/or creation of the actual resources. 
 * </lu>
 *  
 * @author P.T. de Boer 
 */
public interface VFileSystem extends VResourceSystem, VStreamProducer
{
    // same as VResourceSystem but returns VFSNode type.  
    abstract public VFSNode openLocation(VRL vrl) throws VlException;
    
    /**
     * Open filesystem path and return new VFile object. 
     * The (remote) file must exist.
     */
    abstract public VFile openFile(VRL fileVRL) throws VlException;
    
    /**
     * Open filesystem path and return new VDir object. 
     * The (remote) directory must exist.
     */
    abstract public VDir openDir(VRL dirVRL) throws VlException;
  
    /**
     * Generic VFile (object) constructor: Create new VFile object linked to this resource system. 
     * The actual file may or may not exist on the remote filesystem.<br>
     * Use VFile.exists() to check whether it exists or VFile.create() to create the actual
     * file on the (remote) resource. 
     */
    abstract public VFile newFile(VRL fileVRL) throws VlException;

    /**
     * Generic VDir constructor: Create new VDir object linked to this (remote) filesystem. 
     * This object may exist or may not exist on the remote resource,<br>
     * Use openDir() to get an existing directory.  
     */
    abstract public VDir newDir(VRL dirVRL) throws VlException;
    
    // Explicit declaration from VInputStreamProducer
    abstract public InputStream openInputStream(VRL location) throws VlException; 

    // Explicit declaration from VOutputStreamProducer
    abstract public OutputStream openOutputStream(VRL location) throws VlException; 

}
