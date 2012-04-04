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
 * $Id: VTarEntry.java,v 1.5 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

import org.apache.tools.tar.TarEntry;

public class VTarEntry implements ArchiveEntry
{

    private TarEntry entry;

    private long dataOffset = -1;

    private long headerOffset = -1;

    public VTarEntry(TarEntry entry)
    {
        this.entry = entry;
    }

    public long getModificationTime()
    {
        return entry.getModTime().getTime();
    }

    public String getPath()
    {
        return entry.getName();
    }

    public long getSize()
    {
        return entry.getSize();
    }

    public boolean isDirectory()
    {
        return entry.isDirectory();
    }

    public long getDataOffset()
    {
        return this.dataOffset;
    }

    public long getHeaderOffset()
    {
        return this.headerOffset;
    }

    public void setDataOffest(long dataOffset)
    {
        this.dataOffset = dataOffset;
    }

    public void setHeaderOffest(long headerOffset)
    {
        this.headerOffset = headerOffset;
    }

}
