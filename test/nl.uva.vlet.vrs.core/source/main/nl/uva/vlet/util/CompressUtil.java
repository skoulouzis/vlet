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
 * $Id: CompressUtil.java,v 1.4 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.util.compress.ArchiveEntry;
import nl.uva.vlet.util.compress.VtarFile;
import nl.uva.vlet.util.compress.VzipFile;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.io.VRandomReadable;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class CompressUtil
{
    private static ClassLogger logger;

    private static VFSClient vfs = new VFSClient();
    
    static
    {
        logger = ClassLogger.getLogger(CompressUtil.class);
        logger.setLevelToDebug();
    }

    public static void uncopress(VRL source, VRL destination) throws VlException, ZipException, IOException
    {
        VFile file = vfs.newFile(source);
        if (!file.exists())
        {
            throw new nl.uva.vlet.exception.VlIOException("File " + source + " not found");
        }
        if (StringUtil.equals(source.getExtension().toLowerCase(), "zip"))
        {
            unzip(file, destination);
        }
        else if (StringUtil.equals(source.getExtension().toLowerCase(), "gz"))
        {
            gunzip(file, destination);
        }
        else
        {
            throw new nl.uva.vlet.exception.VlException("Could not uncopress " + source);
        }
    }

    /**
     * 
     * @param source
     * @return
     * @throws VlException
     * @throws IOException
     * @throws VlException 
     */
    public static Enumeration<ArchiveEntry> getEntries(VRL source) throws  IOException, VlException
    {

        VFile file = vfs.newFile(source);
        if (!file.exists())
        {
            throw new nl.uva.vlet.exception.VlIOException("File " + source + " not found");
        }
        if (StringUtil.equals(source.getExtension().toLowerCase(), "zip"))
        {
            return new VzipFile((VRandomReadable) file).getEntries();
        }
        else if (StringUtil.equals(source.getExtension().toLowerCase(), "tar"))
        {
            return new VtarFile((VRandomReadable) file).getEntries();
        }
        else if (StringUtil.equals(source.getExtension().toLowerCase(), "gz"))
        {
//            return new VGzFile((VRandomReadable) file).getEntries();
        }
        else
        {
            throw new nl.uva.vlet.exception.VlException("Could not uncopress " + source);
        }
        return null;
    }

    private static void gunzip(VFile sourceFile, VRL destination) throws VlException
    {
        InputStream sins = sourceFile.getInputStream();

        GZIPInputStream gzin;
        try
        {
            gzin = new GZIPInputStream(sins);

            TarInputStream tin = new TarInputStream(gzin);
            TarEntry entry;
            VRL destVRL;
            
            while ((entry = tin.getNextEntry()) != null)
            {
                destVRL = destination.append(entry.getName());
                logger.debugPrintf("Extracting directory:  %s\n", destVRL);
                if (!vfs.existsDir(destVRL))
                {
                    vfs.mkdirs(destVRL, false);
                }
                else
                {
                    VRL parent = destVRL.getParent();
                    if (!vfs.existsDir(parent))
                    {
                        vfs.mkdirs(parent, false);
                    }

                    VFile destFile = vfs.newFile(destVRL);
                    InputStream zins = tin;
                    nl.uva.vlet.io.StreamUtil.copyStreams(zins, destFile.getOutputStream());
                }
            }
            tin.close();
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

    }

    private static void unzip(VFile source, VRL destination) throws VlException
    {

        VzipFile zipFile = new VzipFile((VRandomReadable) source);

        VRL destVRL;
        Enumeration<ArchiveEntry> entries = zipFile.getEntries();
        ArchiveEntry entry;
        while (entries.hasMoreElements())
        {
            entry = entries.nextElement();

            destVRL = destination.append(entry.getPath());
            if (entry.isDirectory())
            {
                logger.debugPrintf("Extracting directory:  %s\n", destVRL);
                if (!vfs.existsDir(destVRL))
                {
                    vfs.mkdirs(destVRL, false);
                }
            }
            else
            {
                logger.debugPrintf("Extracting file:  %s\n", destVRL);
                VRL parent = destVRL.getParent();
                if (!vfs.existsDir(parent))
                {
                    vfs.mkdirs(parent, false);
                }

                VFile destFile = vfs.newFile(destVRL);
                InputStream zins = zipFile.getInputStream(entry);
                nl.uva.vlet.io.StreamUtil.copyStreams(zins, destFile.getOutputStream());
            }
        }

        zipFile.close();

    }

}
