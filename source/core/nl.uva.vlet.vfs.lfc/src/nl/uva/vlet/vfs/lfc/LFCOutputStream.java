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
 * $Id: LFCOutputStream.java,v 1.2 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import java.io.IOException;
import java.io.OutputStream;


import nl.uva.vlet.Global;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.lfc.LFCException;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VReplicatable;

public class LFCOutputStream extends OutputStream
{

    private OutputStream outputStream;
    private LFCFile lfcFile; // resolved file to write to
    private VFile replica;
    private LFCClient client;
    private boolean isNewReplica;
    private ITaskMonitor monitor;

    public LFCOutputStream(ITaskMonitor monitor, LFCFile lfcFile,
            VFile replica, LFCClient client, boolean isNewRepl)
            throws VlException
    {
        super();
        this.lfcFile = lfcFile;
        this.replica = replica;

        info("Opening OutputStream to replica:" + replica);
        this.outputStream = replica.getOutputStream();
        this.isNewReplica = isNewRepl;
        this.monitor = monitor;
        debug("Replica should be created in: " + replica.getVRL());

        this.client = client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int arg0) throws IOException
    {
        this.outputStream.write(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        this.outputStream.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] arg0, final int arg1, final int arg2)
            throws IOException
    {
        this.outputStream.write(arg0, arg1, arg2);
        debug("Wrote from " + arg1 + " to " + arg2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] arg0) throws IOException
    {
        this.outputStream.write(arg0);
    }

    @Override
    public void close() throws IOException
    {
        try
        {
            outputStream.close();
        }
        catch (Exception e)
        {
            Global.warnPrintln(this,
                    "Couldn't close outputstream. Already closed ?:" + e);
        }

        try
        {
            // after closing a stream we can register the replica
            // together with a new LFC entry:
            if (lfcFile.exists() == false)
            {
                monitor.startSubTask("LFC: Registering new File entry", -1);
                monitor.logPrintf("LFC: Registering new File entry:\n - "
                        + lfcFile + "\n");
                lfcFile.create(); // register new netry without replicas
                monitor.endSubTask("LFC: Registering new File entry");
            }

            // as soon as the replica is created register it
            if (isNewReplica)
            {
                monitor.startSubTask("LFC: Registering new replica", -1);
                monitor.logPrintf("LFC: Registering new replica:\n - "
                        + replica.getVRL() + "\n");
                this.client.addReplica(monitor, lfcFile, replica, true);

                monitor.endSubTask("LFC: Registering new replica");
            }
            else
            {
                // if this is an existing replica just update metadata (checksum
                // , size). This also fixes the bug of getting the correct size
                // after writing twice
                monitor.startSubTask("LFC: Updating replica metadata", -1);
                monitor.logPrintf("LFC: Updating metadata for: \n - "
                        + replica.getVRL() + "\n");
                client.updateReplicaMetaData(monitor, lfcFile, replica);
                
                monitor.endSubTask("LFC: Updating replica metadata");
            }

            // LinkHandling new or existing file should be resolved link:
            FileDescWrapper wrapperDesc = client.queryPath(lfcFile.getPath(),
                    true);

            lfcFile.setWrapperDesc(wrapperDesc);

            debug("-------------LFC file is:        " + lfcFile.getLength()
                    + " bytes");
            debug("-------------Replica file is      " + replica.getLength()
                    + " bytes");

            if (lfcFile.getLength() != replica.getLength())
            {
                Global.errorPrintln(this,
                        "LFC file and replica file sizes don't match!!! LFC file is "
                                + lfcFile.getLength() + " bytes and replica "
                                + replica.getLength() + " bytes");
            }

            monitor.logPrintf("LFC: Finalizing entry: setting new file size (updated from replica) to:"
                            + wrapperDesc.getFileDesc().getFileSize() + "\n");

            info("Closing OutputStream. Finalizing upload to:" + lfcFile);

        }
        catch (Exception e)
        {
            IOException ex = new IOException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
        finally
        {

        }

    }

    private static void debug(String msg)
    {
        Global.debugPrintln(LFCOutputStream.class, msg);
    }

    private static void info(String msg)
    {
        Global.infoPrintln(LFCOutputStream.class, msg);
    }

}
