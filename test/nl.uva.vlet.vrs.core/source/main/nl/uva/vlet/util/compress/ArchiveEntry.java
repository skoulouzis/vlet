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
 * $Id: ArchiveEntry.java,v 1.5 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

/**
 * 
 * Interface for zip and tar entries
 * 
 * @author S. Koulouzis
 * 
 */
public interface ArchiveEntry
{
    /**
     * 
     * @return the path
     */
    public String getPath();

    /**
     * 
     * @return the uncompressed size
     */
    public long getSize();

    /**
     * 
     * @return the Modification Time
     */
    public long getModificationTime();

    public boolean isDirectory();

    /**
     * Returns a pointer pointing at the start if this entrie's header
     * 
     * @return
     */
    public long getHeaderOffset();

    /**
     * Returns a pointer pointing at the start if this entrie's data
     * 
     * @return
     */
    public long getDataOffset();

    /**
     * The headerOffset points at the start of the header of this entry.
     * 
     * @param headerOffset
     */
    public void setHeaderOffest(long headerOffset);

    /**
     * The dataOffset point at the start of the data of this entry.
     * 
     * @param headerOffset
     */
    public void setDataOffest(long dataOffset);

}
