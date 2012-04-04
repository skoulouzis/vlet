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
 * $Id: Archive.java,v 1.5 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import nl.uva.vlet.exception.VlException;

import org.apache.tools.tar.TarEntry;

public interface Archive
{

    /**
     * Returns all entries.
     * 
     * @return all entries as {@link TarEntry} instances
     * @throws VlException
     * @throws IOException
     */
    public Enumeration<ArchiveEntry> getEntries() throws VlException;

    /**
     * Returns one entry.
     * 
     * @return One entry
     * @throws VlException
     * @throws IOException
     */
    public ArchiveEntry getEntry(String name) throws VlException;

    /**
     * Returns an inputstream that points at the start of the data the entry
     * defines. This stream should return -1 when the data block reaches it's
     * end
     * 
     * @param entry
     * @return
     * @throws VlException
     */
    public InputStream getInputStream(ArchiveEntry entry) throws VlException;
}
