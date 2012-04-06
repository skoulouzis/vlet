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
 * $Id: VzipFile.java,v 1.6 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.vrs.io.VRandomReadable;
import nl.uva.vlet.vrs.io.VRandomReader;

import org.apache.tools.zip.ZipEntry;

public class VzipFile implements Archive
{
    private static final int WORD = 4;

    private static final int SHORT = 2;

    private static final int MIN_EOCD_SIZE =
    /* end of central dir signature */WORD
    /* number of this disk */+ SHORT
    /* number of the disk with the */
    /* start of the central directory */+ SHORT
    /* total number of entries in */
    /* the central dir on this disk */+ SHORT
    /* total number of entries in */
    /* the central dir */+ SHORT
    /* size of the central directory */+ WORD
    /* offset of start of central */
    /* directory with respect to */
    /* the starting disk number */+ WORD
    /* zipfile comment length */+ SHORT;

    private static final int MAX_EOCD_SIZE = MIN_EOCD_SIZE
    /* maximum length of zipfile comment */+ 0xFFFF;

    private static final int POS_0 = 0;

    private static final int POS_1 = 1;

    private static final int POS_2 = 2;

    private static final int POS_3 = 3;

    private static final int CFD_LOCATOR_OFFSET =
    /* end of central dir signature */WORD
    /* number of this disk */+ SHORT
    /* number of the disk with the */
    /* start of the central directory */+ SHORT
    /* total number of entries in */
    /* the central dir on this disk */+ SHORT
    /* total number of entries in */
    /* the central dir */+ SHORT
    /* size of the central directory */+ WORD;

    private static final int CFH_LEN =
    /* version made by */SHORT
    /* version needed to extract */+ SHORT
    /* general purpose bit flag */+ SHORT
    /* compression method */+ SHORT
    /* last mod file time */+ SHORT
    /* last mod file date */+ SHORT
    /* crc-32 */+ WORD
    /* compressed size */+ WORD
    /* uncompressed size */+ WORD
    /* filename length */+ SHORT
    /* extra field length */+ SHORT
    /* file comment length */+ SHORT
    /* disk number start */+ SHORT
    /* internal file attributes */+ SHORT
    /* external file attributes */+ WORD
    /* relative offset of local header */+ WORD;

    private static final int BYTE_SHIFT = 8;

    private static final int NIBLET_MASK = 0x0f;

    /**
     * Number of bytes in local file header up to the &quot;length of
     * filename&quot; entry.
     */
    private static final long LFH_OFFSET_FOR_FILENAME_LENGTH =
    /* local file header signature */WORD
    /* version needed to extract */+ SHORT
    /* general purpose bit flag */+ SHORT
    /* compression method */+ SHORT
    /* last mod file time */+ SHORT
    /* last mod file date */+ SHORT
    /* crc-32 */+ WORD
    /* compressed size */+ WORD
    /* uncompressed size */+ WORD;

    private VRandomReader archive;

    /**
     * The zip encoding to use for filenames and the file comment.
     */
    private final ZipEncoding zipEncoding;

    private Map<String, ArchiveEntry> entries = new HashMap<String, ArchiveEntry>();

    private long startOfDirectoryRecord;

    private VZipEntry currentEntry;

    private static final byte[] CFH_SIG = ZipLong.getBytes(0X02014B50L);

    /**
     * General purpose flag, which indicates that filenames are written in
     * utf-8.
     */
    public static final int UFT8_NAMES_FLAG = 1 << 11;

    /**
     * local file header signature
     * 
     */
    protected static final byte[] LFH_SIG = ZipLong.getBytes(0X04034B50L);

    /**
     * end of central dir signature
     * 
     * @since 1.1
     */
    protected static final byte[] EOCD_SIG = ZipLong.getBytes(0X06054B50L);

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(VzipFile.class);
        logger.setLevelToDebug();
    }

    public VzipFile(VRandomReadable source)
    {
        archive = new VRandomReader((source));
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(null);
    }

    public Enumeration<ArchiveEntry> getEntries() throws VlException
    {
        if (entries.isEmpty())
        {
            populateZipEntries();
        }

        return Collections.enumeration(entries.values());
    }

    private void populateZipEntries() throws VlException
    {
        positionAtCentralDirectory();
        byte[] cfh = new byte[CFH_LEN];

        byte[] signatureBytes = new byte[WORD];
        archive.readFully(signatureBytes);

        long sig = ZipLong.getValue(signatureBytes);

        final long cfhSig = ZipLong.getValue(CFH_SIG);

        if (sig != cfhSig && startsWithLocalFileHeader())
        {
            throw new VlIOException("Central directory is empty, can't expand. Probably corrupt archive.");
        }

        while (sig == cfhSig)
        {
            archive.readFully(cfh);
            sig = parseCentralDirectory(cfh);
        }

        addOffsets();

    }

    private long parseCentralDirectory(byte[] cfh) throws VlException
    {
        int off = 0;

        int versionMadeBy = ZipShort.getValue(cfh, off);
        off += SHORT;
        int platform = (versionMadeBy >> BYTE_SHIFT) & NIBLET_MASK;

        off += SHORT; // skip version info

        final int generalPurposeFlag = ZipShort.getValue(cfh, off);
        final boolean hasUTF8Flag = (generalPurposeFlag & UFT8_NAMES_FLAG) != 0;
        final ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : zipEncoding;

        off += SHORT;
        int method = ZipShort.getValue(cfh, off);

        off += SHORT;

        // FIXME this is actually not very cpu cycles friendly as we are
        // converting from
        // dos to java while the underlying Sun implementation will
        // convert
        // from java to dos time for internal storage...
        long time = dosToJavaTime(ZipLong.getValue(cfh, off));

        off += WORD;

        long crc = ZipLong.getValue(cfh, off);
        off += WORD;

        long compressedSize = ZipLong.getValue(cfh, off);
        off += WORD;

        long size = ZipLong.getValue(cfh, off);
        off += WORD;

        int fileNameLen = ZipShort.getValue(cfh, off);
        off += SHORT;

        int extraLen = ZipShort.getValue(cfh, off);
        off += SHORT;

        int commentLen = ZipShort.getValue(cfh, off);
        off += SHORT;

        off += SHORT; // disk number

        int internalAttributes = ZipShort.getValue(cfh, off);
        off += SHORT;

        long externalAttributes = ZipLong.getValue(cfh, off);
        off += WORD;

        byte[] fileName = new byte[fileNameLen];
        archive.readFully(fileName);

        String name;
        try
        {
            name = entryEncoding.decode(fileName);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        long headerOffset = ZipLong.getValue(cfh, off);

        // skip them for now
        // byte[] cdExtraData = new byte[extraLen];
        // archive.readFully(cdExtraData);
        archive.skipBytes(extraLen);

        byte[] byteComment = new byte[commentLen];
        archive.readFully(byteComment);

        String comment;
        try
        {
            comment = entryEncoding.decode(byteComment);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        org.apache.tools.zip.ZipEntry entry = new org.apache.tools.zip.ZipEntry(name);
        entry.setComment(comment);
        entry.setCompressedSize(compressedSize);

        // entry.setComprSize( compressedSize );
        entry.setCrc(crc);
        entry.setExternalAttributes(externalAttributes);
        // entry.setExtraFields(fields);
        entry.setInternalAttributes(internalAttributes);

        entry.setMethod(method);

        entry.setSize(size);
        entry.setTime(time);
        // entry.setUnixMode(mode);

        currentEntry = new VZipEntry(entry);
        currentEntry.setHeaderOffest(headerOffset);

        entries.put(name, currentEntry);

        byte[] signatureBytes = new byte[WORD];
        archive.readFully(signatureBytes);
        long sig = ZipLong.getValue(signatureBytes);
        //
        // if (!hasUTF8Flag && useUnicodeExtraFields)
        // {
        // noUTF8Flag.put(ze, new NameAndComment(fileName, byteComment));
        // }

        return sig;
    }

    /**
     * Walks through all recorded entries and adds the data available from the
     * local file header.
     * 
     * <p>
     * Also records the offsets for the data to read from the entries.
     * </p>
     * 
     * @throws VlException
     */
    private void addOffsets() throws VlException
    {
        Enumeration<ArchiveEntry> e = getEntries();

        while (e.hasMoreElements())
        {
            currentEntry = (VZipEntry) e.nextElement();

            long offset = currentEntry.getHeaderOffset();

            long fp = offset + LFH_OFFSET_FOR_FILENAME_LENGTH;
            archive.seek(fp);

            byte[] b = new byte[SHORT];

            archive.skipBytes(b.length);
            int fileNameLen = currentEntry.getPath().getBytes().length;// ZipShort.getValue(b);

            archive.readFully(b);
            int extraFieldLen = ZipShort.getValue(b);

            int lenToSkip = fileNameLen;
            while (lenToSkip > 0)
            {
                int skipped = archive.skipBytes(lenToSkip);
                if (skipped <= 0)
                {
                    throw new RuntimeException("failed to skip file name in" + " local file header");
                }
                lenToSkip -= skipped;
            }

            byte[] localExtraData = new byte[extraFieldLen];
            archive.readFully(localExtraData);
            currentEntry.setExtra(localExtraData);
            long dataOffset = offset + LFH_OFFSET_FOR_FILENAME_LENGTH + SHORT + SHORT + fileNameLen + extraFieldLen;

            currentEntry.setDataOffest(dataOffset);

            entries.put(currentEntry.getPath(), currentEntry);

            // if (entriesWithoutUTF8Flag.containsKey(ze))
            // {
            // setNameAndCommentFromExtraFields(ze, (NameAndComment)
            // entriesWithoutUTF8Flag.get(ze));
            // }
        }

    }

    /*
     * Converts DOS time to Java time (number of milliseconds since epoch).
     */
    private static long dosToJavaTime(long dosTime)
    {
        Calendar cal = Calendar.getInstance();
        // CheckStyle:MagicNumberCheck OFF - no point
        cal.set(Calendar.YEAR, (int) ((dosTime >> 25) & 0x7f) + 1980);
        cal.set(Calendar.MONTH, (int) ((dosTime >> 21) & 0x0f) - 1);
        cal.set(Calendar.DATE, (int) (dosTime >> 16) & 0x1f);
        cal.set(Calendar.HOUR_OF_DAY, (int) (dosTime >> 11) & 0x1f);
        cal.set(Calendar.MINUTE, (int) (dosTime >> 5) & 0x3f);
        cal.set(Calendar.SECOND, (int) (dosTime << 1) & 0x3e);
        // CheckStyle:MagicNumberCheck ON
        return cal.getTime().getTime();
    }

    /**
     * Checks whether the archive starts with a LFH. If it doesn't, it may be an
     * empty archive.
     * 
     * @throws VlException
     */
    private boolean startsWithLocalFileHeader() throws VlException
    {
        archive.seek(0);
        final byte[] start = new byte[WORD];
        archive.readFully(start);
        for (int i = 0; i < start.length; i++)
        {
            if (start[i] != LFH_SIG[i])
            {
                return false;
            }
        }
        return true;
    }

    public InputStream getInputStream(ArchiveEntry entry) throws VlException
    {

        if (entries.isEmpty())
        {
            populateZipEntries();
        }
        if (entry.getDataOffset() == -1)
        {
            addOffsets();
        }

        VZipEntry ze = (VZipEntry) entry;

        if (ze == null)
        {
            return null;
        }

        InputStream bis = new ArchiveInputStream(null,ze, archive,ArchiveInputStream.ZIP);

        switch (ze.getMethod())
        {
            case ZipEntry.STORED:
                return bis;
            case ZipEntry.DEFLATED:
                return new ArchiveInputStream(new Inflater(true), ze, archive,ArchiveInputStream.ZIP);
            default:
                throw new VlException("Found unsupported compression method " + ze.getMethod());
        }
    }

    public void close() throws VlIOException
    {
        archive.close();
    }

    /**
     * Searches for the &quot;End of central dir record&quot;, parses it and
     * positions the stream at the first central directory record.
     * 
     * @throws VlException
     * 
     */
    private void positionAtCentralDirectory() throws VlException
    {
        boolean found = false;
        long off = archive.length() - MIN_EOCD_SIZE;
        long stopSearching = Math.max(0L, archive.length() - MAX_EOCD_SIZE);

        if (off >= 0)
        {
            archive.seek(off);

            byte[] sig = EOCD_SIG;

            int curr = archive.read();

            while (off >= stopSearching && curr != -1)
            {
                if (curr == sig[POS_0])
                {
                    curr = archive.read();

                    if (curr == sig[POS_1])
                    {
                        curr = archive.read();
                        if (curr == sig[POS_2])
                        {
                            curr = archive.read();
                            if (curr == sig[POS_3])
                            {
                                found = true;
                                break;
                            }
                        }
                    }
                }
                archive.seek(--off);

                curr = archive.read();
            }
        }
        if (!found)
        {
            throw new VlException("Archive is probably not a ZIP archive");
        }
        archive.seek(off + CFD_LOCATOR_OFFSET);
        byte[] cfdOffset = new byte[WORD];
        archive.readFully(cfdOffset);

        startOfDirectoryRecord = ZipLong.getValue(cfdOffset);
        archive.seek(startOfDirectoryRecord);
    }

    public ArchiveEntry getEntry(String name) throws VlException
    {
        return entries.get(name);
    }

}
