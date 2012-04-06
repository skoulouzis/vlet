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
 * $Id: ArchiveInputStream.java,v 1.3 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.util.compress;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.vrs.io.VRandomReader;

public class ArchiveInputStream extends InputStream
{

    public static final int ZIP = 0;

    public static final int GZIP = 1;

    public static final int TAR = 2;

    private VRandomReader archive;

    // private ArchiveEntry entry;
    private long dataEnd;

    private long aveliable;

    private Inflater inf;

    private boolean reachEOF;

    private CRC32 crc = new CRC32();

    private int archType;

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(ArchiveInputStream.class);
        logger.setLevelToDebug();
    }

    public ArchiveInputStream(Inflater inflater, ArchiveEntry entry, VRandomReader archive, int archType)
    {
        this.inf = inflater;
        this.archive = archive;

        archive.seek(entry.getDataOffset());
        if (entry instanceof VTarEntry)
        {
            dataEnd = entry.getDataOffset() + entry.getSize();
        }
        if (entry instanceof VZipEntry)
        {
            VZipEntry zipEntry = (VZipEntry) entry;
            dataEnd = entry.getDataOffset() + zipEntry.getCompressedSize();
        }

        this.archType = archType;
    }

    private int readDeflated(byte[] b, int off, int len) throws IOException, VlException
    {
        if (b == null)
        {
            throw new NullPointerException();
        }
        else if (off < 0 || len < 0 || len > b.length - off)
        {
            throw new IndexOutOfBoundsException();
        }
        else if (len == 0)
        {
            return 0;
        }
        try
        {
            int n;
            while ((n = inf.inflate(b, off, len)) == 0)
            {
                if (inf.finished() || inf.needsDictionary())
                {
                    reachEOF = true;
                    return -1;
                }
                if (inf.needsInput())
                {
                    fill(b);
                }
            }
            return n;
        }
        catch (DataFormatException e)
        {
            String s = e.getMessage();
            throw new ZipException(s != null ? s : "Invalid ZLIB data format");
        }
    }

    /**
     * Fills input buffer with more data to decompress.
     * 
     * @exception IOException
     *                if an I/O error has occurred
     * @throws VlException
     */
    private void fill(byte[] buf) throws IOException, VlException
    {
        int len = archive.read(buf, 0, buf.length);
        if (len == -1)
        {
            throw new EOFException("Unexpected end of ZLIB input stream");
        }
        inf.setInput(buf, 0, len);
    }

    @Override
    public int read() throws IOException
    {
        byte[] b = new byte[1];
        if (read(b) != -1)
        {
            return b[1];
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        try
        {

            switch (archType)
            {
                case ZIP:
                    if (this.inf == null)
                    {
                        return readStored(b, off, len);
                    }
                    else
                    {
                        return readDeflated(b, off, len);
                    }

                case GZIP:
                    
                    break;
                case TAR:
                    return readStored(b, off, len);

                default:
                    break;
            }

        }
        catch (VlException e)
        {
            new IOException(e);
        }
        return -1;
    }

    /**
     * Rads uncompressed data into an array of bytes. If <code>len</code> is not
     * zero, the method will block until some input can be decompressed;
     * otherwise, no bytes are read and <code>0</code> is returned.
     * 
     * @param buf
     *            the buffer into which the data is read
     * @param off
     *            the start offset in the destination array <code>b</code>
     * @param len
     *            the maximum number of bytes read
     * @return the actual number of bytes read, or -1 if the end of the
     *         compressed input stream is reached
     * @exception NullPointerException
     *                If <code>buf</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException
     *                If <code>off</code> is negative, <code>len</code> is
     *                negative, or <code>len</code> is greater than
     *                <code>buf.length - off</code>
     * @exception IOException
     *                if an I/O error has occurred or the compressed input data
     *                is corrupt
     * @throws VlException
     */
    private int readGzip(byte[] buf, int off, int len) throws IOException, VlException
    {
        len = readDeflated(buf, off, len);
        if (len == -1)
        {
            readTrailer();
        }
        else
        {
            crc.update(buf, off, len);
        }
        return len;
    }

    /*
     * Reads GZIP member trailer.
     */
    private void readTrailer() throws IOException, VlException
    {
        int n = inf.getRemaining();

        // Uses left-to-right evaluation order
        if ((readUInt() != crc.getValue()) ||
        // rfc1952; ISIZE is the input size modulo 2^32
                (readUInt() != (inf.getBytesWritten() & 0xffffffffL)))
            throw new IOException("Corrupt GZIP trailer");
    }

    /*
     * Reads unsigned integer in Intel byte order.
     */
    private long readUInt() throws IOException, VlException
    {
        long s = readUShort();
        return ((long) readUShort() << 16) | s;
    }

    /*
     * Reads unsigned short in Intel byte order.
     */
    private int readUShort() throws IOException, VlException
    {
        int b = readUByte();
        return ((int) readUByte() << 8) | b;
    }

    /*
     * Reads unsigned byte.
     */
    private int readUByte() throws IOException, VlException
    {
        int b = archive.read();
        if (b == -1)
        {
            throw new EOFException();
        }
        if (b < -1 || b > 255)
        {
            // Report on this.in, not argument in; see read{Header, Trailer}.
            throw new IOException("read() returned value out of range -1..255: " + b);
        }
        return b;
    }

    private int readStored(byte[] b, int off, int len) throws IOException
    {
        this.aveliable = dataEnd - archive.getFilePointer();

        try
        {
            if (aveliable > len)
            {
                return archive.read(b, off, len);
            }
            if (aveliable <= len)
            {
                return archive.read(b, off, (int) aveliable);
            }

        }
        catch (VlException e)
        {
            throw new IOException(e);
        }

        return -1;
    }

    @Override
    public int available()
    {
        return this.available();
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            archive.close();
        }
        catch (VlIOException e)
        {
            throw new IOException(e);
        }
    }

}
