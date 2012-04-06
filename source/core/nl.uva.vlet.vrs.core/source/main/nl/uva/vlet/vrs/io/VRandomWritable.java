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
 * $Id: VRandomWritable.java,v 1.3 2011-04-18 12:00:33 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:33 $
 */ 
// source: 

package nl.uva.vlet.vrs.io;

import nl.uva.vlet.exception.VlException;

/** 
 * Random Writable interface for atomic writes().   
 * 
 * @author P.T. de Boer 
 *
 */
public interface VRandomWritable extends VZeroSizable
{
    /* Since SRB does not YET support the setLength() call, 
     * this method cannot be implemented. 
     * Instead use setLengtToZero is provided to reset the length. 
     * To make a file longer, simply use 'writeBytes' to extend it. 
     */ 
    
     // public void setLength(long newLength) throws VlException;

     /** 
      * Resets file length to zero.<br>  
      * Currently setLength() is not supported by SRB so this
      * method is currently the only method
      * which can decrease the file size. <br>
      * To extend a file, use writeBytes to append data to a file.  
      */ 
     public void setLengthToZero() throws VlException;
     
   
    /**
     * Writes <code>nrBytes</code> to the file starting  
     * at position fileOffset in the file. Data is  and reading 
     * from byte array buffer[bufferOffset].
     * 
     * @see java.io.RandomAccessFile#writeBytes
     */

    public void writeBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws VlException;
}
