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
 * $Id: VtarFile.java,v 1.7 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrs.io.VRandomReadable;
import nl.uva.vlet.vrs.io.VRandomReader;

import org.apache.tools.tar.TarEntry;

public class VtarFile implements Archive
{

    private VRandomReader archive;

    private Map<String, ArchiveEntry> entries = new HashMap<String, ArchiveEntry>();

    private boolean hasHitEOF;

    private TarEntry currEntry;

    private boolean v7Format;

    private long entrySize;

    private double blockSize = 512.0;

    private byte headerBuf[];

    private int numOfBlocks;

    private VTarEntry currVEntry;

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(VtarFile.class);
        logger.setLevelToDebug();
    }

    public VtarFile(VRandomReadable file)
    {
        archive = new VRandomReader(file);
        headerBuf = new byte[(int) blockSize];
    }

    public VtarFile(VRandomReadable file, int blockSize)
    {
        archive = new VRandomReader(file);
        this.blockSize = blockSize;
        headerBuf = new byte[(int) blockSize];
    }

    public Enumeration<ArchiveEntry> getEntries() throws VlException
    {
        if (entries.isEmpty())
        {
            while (getNextEntry() != null)
                ;
        }

        return Collections.enumeration(entries.values());
    }

    private TarEntry getNextEntry() throws VlException
    {
        if (hasHitEOF)
            return null;

        // SP: Calculate how many blocks we have to skip to reach the next
        // header
        if (currEntry != null)
        {
            numOfBlocks = numOfBlocks + (int) (Math.ceil((entrySize / blockSize)) + 1);
            archive.seek((long) (numOfBlocks * blockSize));
        }
        // SP: Read the entry header (default size 512)
        archive.readFully(headerBuf);

        if (headerBuf == null)
        {
            hasHitEOF = true;
        }
        else if (isEOFRecord(headerBuf))
        {
            hasHitEOF = true;
        }
        if (hasHitEOF)
        {
            currEntry = null;
        }
        else
        {
            currEntry = new TarEntry(headerBuf);
            currVEntry = new VTarEntry(currEntry);

            // set the pointers
            currVEntry.setHeaderOffest((long) (archive.getFilePointer() - blockSize));
            currVEntry.setDataOffest(archive.getFilePointer());

            entries.put(currVEntry.getPath(), currVEntry);

            logger.debugPrintf("Entry: %s: Header: %s    Data: %s\n", currVEntry.getPath(), currVEntry
                    .getHeaderOffset(), currVEntry.getDataOffset());

            if (headerBuf[257] != 117 || headerBuf[258] != 115 || headerBuf[259] != 116 || headerBuf[260] != 97
                    || headerBuf[261] != 114)
                v7Format = true;

            entrySize = currEntry.getSize();
        }

        // SP: Not sure what this is supposed to be
        if (currEntry != null && currEntry.isGNULongNameEntry())
        {
            StringBuffer longName = new StringBuffer();
            byte buffer[] = new byte[256];

            for (int length = 0; (length = archive.read(buffer)) >= 0;)
                longName.append(new String(buffer, 0, length));

            getNextEntry();

            if (longName.length() > 0 && longName.charAt(longName.length() - 1) == 0)
                longName.deleteCharAt(longName.length() - 1);

            currEntry.setName(longName.toString());
        }
        return currEntry;
    }

    private boolean isEOFRecord(byte record[])
    {
        int i = 0;
        for (int sz = (int) blockSize; i < sz; i++)
            if (record[i] != 0)
                return false;

        return true;
    }

    public ArchiveEntry getEntry(String name) throws VlException
    {
        return entries.get(name);
    }

    public InputStream getInputStream(ArchiveEntry entry) throws VlException
    {
        return new ArchiveInputStream(null,entry, archive,ArchiveInputStream.TAR);
    }
}
