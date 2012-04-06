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
 * $Id: VFile.java,v 1.9 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.ResourceToBigException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.io.StreamUtil;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.io.VRandomAccessable;
import nl.uva.vlet.vrs.io.VRandomReadable;
import nl.uva.vlet.vrs.io.VSize;
import nl.uva.vlet.vrs.io.VStreamAccessable;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;


/**
 * The Virtual File Interface. An abstract representation of a File.
 * Exposes common File methods.  
 * <p>
 * To get an VFile object, use a {@link VFSClient} and use {@link VFSClient#getFile(VRL)}.
 * 
 * @see VFSClient
 * @see VFSNode
 * @see VDir
 * @see VFileSystem
 * 
 * @author P.T. de Boer
 */
public abstract class VFile extends VFSNode implements VSize,VStreamAccessable //, VRandomAccessable
{
  
    // ========================================================================
    // Instance Stuff 
    // ========================================================================
       
//    /** @deprecated Will switch to VFSNode(VFileSystem,...) ! */
//    public VFile(VRSContext context,VRL vrl)
//    {
//        super(context,vrl);
//    }

    public VFile(VFileSystem vfs,VRL vrl)
    {
        super(vfs,vrl);
    }

    /** Returns type String of this VNode */ 
    public String getType()
    {
        return VFS.FILE_TYPE;
    }

    @Override
    public VRL getHelp()
    {
        return Global.getHelpUrl("VFile"); 
    }

    /**
     * Returns true.
     * @see VFSNode#isFile 
     */
    public boolean isFile()
    {
        return true;
    };

    /**
     * Returns false. 
     * @see VFSNode#isDir
     */
    public boolean isDir()
    {
        return false;
    };

    // *** File Read Methods ***

    /**
     * Read the whole contents and return in byte array. Current implementation
     * is to let readBytes to return an array this might have to change to some
     * ByteBuffer placeholder class.
     * 
     * @throws VlException
     */
    public byte[] getContents() throws VlException
    {
        long len = getLength();
        // 2 GB files cannot be read into memory !

        // zero size optimization ! 

        if (len==0) 
        {
            return new byte[0]; // empty buffer ! 
        }

        if (len > ((long) VRS.MAX_CONTENTS_READ_SIZE))
            throw (new ResourceToBigException(
                    "Cannot read complete contents of a file greater then:"
                    + VRS.MAX_CONTENTS_READ_SIZE));

        int intLen = (int) len;
        return getContents(intLen);

    }

    /** Reads first <code>len</cod> bytes into byte array */
    public byte[] getContents(int len) throws VlException
    {
        byte buffer[] = new byte[len];

        // Warning: reading more then max int bytes
        // is impossible, but a file can be greater then that !
        // TODO: Check for out-of-memory etc ...

        int ret = read(0, buffer,0,len); 

        if (ret != len)
            throw new VlIOException(
                    "Couldn't read requested number of bytes (read,requested)="
                    + ret + "," + len);

        return buffer;
    }

    /** Reads first <code>len</cod> bytes into byte array */
    public byte[] getContents(long offset, int len) throws VlException
    {
        byte bytes[] = new byte[len];

        int ret = read(offset, bytes,0,len); 

        // since a specific amount a bytes is requested, return only that
        // amount if it can be read

        if (ret != len)
            throw new VlIOException("Couldn't read requested number of bytes");

        return bytes;
    }

    /**
     * Read contents and return as single String. This method will fail if the
     * VFile doesn't implement the VStreamReadable interface !
     * 
     * @throws VlException
     */
    public String getContentsAsString(String charSet) throws VlException
    {
        byte contents[] = getContents();

        String str;

        try
        {
            if (charSet==null)
                str = new String(contents); // use default charSet
            else
                str = new String(contents, charSet);
        }
        catch (UnsupportedEncodingException e)
        {
            Global.errorPrintf(this,"Exception:%s\n",e);
            Global.debugPrintStacktrace(e); 

            throw (new VlException("charSet enconding:'"+charSet+"' not supported", e));
        }

        return str;
    }

    /**
     * Return contents as String. Used default Character set (utf-8)
     * to decode the contents. 
     * 
     * @return Contents as String. 
     * @throws VlException
     */
    public String getContentsAsString() throws VlException
    {
        return getContentsAsString(getCharSet());
    }

    /** 
     * @see #copyToFile(VRL) 
     */
    final public VFile copyTo(VRL destinationVrl) throws VlException
    {
        return copyToFile(destinationVrl);
    }

    /**
     * Copy this file to new destination location. 
     * The destinationVrl specifies the full path of the new File. 
     * @param destinationVrl
     * @return new VFile
     * @throws VlException
     */

    final public VFile copyToFile(VRL destinationVrl) throws VlException
    {
        return (VFile)this.getTransferManager().doCopyMove(this,destinationVrl,false); 
    }

    /** 
     * Copy this file to new remote directory. 
     * The destinationVrl is the parent directory of the destination file.
     * The new Path of the resulting VFile will be destinationVrl+'/'+this.getBasename() 
     * @param directoryVRL
     * @return new VFile 
     * @throws VlException
     */
    final public VFile copyToDir(VRL directoryVRL) throws VlException
    {
        VRL destVRL= directoryVRL.append(getBasename()); 
        return (VFile)this.getTransferManager().doCopyMove(this,destVRL,false);
    }        

    /**
     * Copy this file to the remote directory. Method will overwrite existing destination file.  
     * @throws VlException
     */
    final public VFile copyTo(VDir parentDir) throws VlException
    {
      //return (VFile)doCopyMoveTo(parentDir,null,false /*,options*/);
        return (VFile)this.getTransferManager().doCopyMove(this,parentDir,null,false); 
    }
    
    /**
     * Copy this file to the designated TargetFile. 
     * @throws VlException
     */
    final public VFile copyTo(VFile targetFile) throws VlException
    {
      //return (VFile)doCopyMoveTo(parentDir,null,false /*,options*/);
        return (VFile)this.getTransferManager().doCopyMove(this,targetFile,false);  
    }

    /**
     * Move this file to the designated TargetFile. 
     * @throws VlException
     */
    final public VFile moveTo(VFile targetFile) throws VlException
    {
        return (VFile)this.getTransferManager().doCopyMove(this,targetFile,true);  
    }

    
    /**
     * Copy to remote directory. Method will overwrite existing destination file.  
     * Parameter newName is optional new name of remote file.
     * @throws VlException
     */
    final public VFile copyTo(VDir parentDir,String newName) throws VlException
    {
        return (VFile)this.getTransferManager().doCopyMove(this,parentDir,newName,false);
        //return (VFile)doCopyMoveTo(parentDir,newName,false /*,options*/);
    }

    /**
     * Move files to remote directory. Will overwrite existing files. 
     * @throws VlException
     */
    final public VFile moveTo(VDir parentDir) throws VlException
    {
        return (VFile)this.getTransferManager().doCopyMove(this,parentDir,null,true); 
        //return (VFile)doCopyMoveTo(parentDir, null,true);
    }

    /**
     * Move file top remote directory.  Method will overwrite existing destination file.  
     * Parameter newName is optional new name of remote file.
     * @throws VlException
     */
    final public VFile moveTo(VDir parentDir,String newName) throws VlException
    {
        return (VFile)this.getTransferManager().doCopyMove(this,parentDir,newName,true); 
        //return (VFile)doCopyMoveTo(parentDir, newName,true);
    }


    /**
     * Write buffer to (remote) File. An offset can be specified into the file 
     * as well into the buffer. 
     * <p> 
     * Use isRandomAccessable() first to determine whether this file can be
     * randomly written to. <br>
     * <br>
     * 
     * @see VRandomAccessable
     */

    public void write(long offset, byte buffer[], int bufferOffset, int nrOfBytes) throws VlException
    {

        // writing as a single stream usually is faster:
        if (offset==0) 
        {
            this.streamWrite(buffer,bufferOffset,nrOfBytes); 
        }
        else if (this instanceof VRandomAccessable)
        {
            ((VRandomAccessable) this).writeBytes(offset, buffer, bufferOffset,
                    nrOfBytes);
        }
        else
        {
            throw new NotImplementedException(
                    "This resource is not Random Accessable (interface VRandomAccessable not implemented):"
                    + this);
        }
    }

    /** Write complete buffer to beginning of file */ 
    public void write(byte buffer[], int bufferOffset,int nrOfBytes) throws VlException
    {
        write(0,buffer,bufferOffset,nrOfBytes);
    }

    /** Write specified number of bytes from buffer to the beginning of the file. */ 
    public void write(byte buffer[],int nrOfBytes) throws VlException
    {
        write(0,buffer,0,nrOfBytes);
    }

    /**
     * Uses OutputStream to write to method i.s.o. RandomAccesFile methods. 
     * For some implementations this is faster. 
     * No offset is supported. 
     */
    public void streamWrite(byte[] buffer,int bufferOffset,int nrOfBytes) throws VlException
    {
        if (this instanceof VStreamWritable)
        {
            VStreamWritable wfile = (VStreamWritable) (this);
            OutputStream ostr = wfile.getOutputStream(); // do not append

            try
            {
                ostr.write(buffer, bufferOffset, nrOfBytes);
                ostr.flush(); 
                ostr.close(); // Close between actions !
            }
            catch (IOException e)
            {
                throw new VlIOException("Failed to write to file:" + this, e);
            }
        }
        else
        {
            throw new nl.uva.vlet.exception.NotImplementedException("File type does not support (remote) write access");
        }
    }


    /**
     * Set contents using specified String and encoding.
     * 
     * @param contents :
     *            new Contents
     * @param encoding :
     *            charset to use
     * @throws VlException if contents can not be set somehow
     */
    public void setContents(String contents, String encoding)
    throws VlException
    {
        byte[] bytes;

        try
        {
            bytes = contents.getBytes(encoding);
            setContents(bytes);
            return;
        }
        catch (UnsupportedEncodingException e)
        {
            Global.errorPrintf(this,"***Error: Exception:%s\n",e);
            e.printStackTrace();

            throw (new VlException("Encoding not supported:" + encoding, e));
        }

    }

    /**
     * Read from a (remote) VFile.<br>
     * Method tries to use the RandomAccessable interface 
     * or the  InputStream from VStreamReasable to read from.
     * Both can be used, but which method is more efficient depends
     * on the implementation and the usage. 
     * @param offset  - offset into file
     * @param nrOfBytes - nr of bytes to read
     * @param bufferOffset -  offset into buffer 
     * @param buffer - byte buffer to read in
     * @return number of read bytes 
     * @throws VlException
     *             if interface does not support remote read access.
     */
    public int read(long offset, byte buffer[],int bufferOffset,int nrOfBytes)
    throws VlException
    {
        boolean forceUseStreamRead=false; //true; // default value  

//        // when reading the first bytes, streamread is faster 
//        if (offset==0) 
//            forceUseStreamRead=true; 

        // Try Random Accessable Interface ! 
        if ((this instanceof VRandomReadable) && (forceUseStreamRead==false))
        {
            // use Sync Read ! 
            return StreamUtil.syncReadBytes((VRandomReadable)this,offset,buffer,bufferOffset,nrOfBytes);
        }
        // else try StreamReadable interface 
        else if (this instanceof VStreamReadable)
        {
            //sync stream Read ! 
            return streamRead(offset,buffer,bufferOffset,nrOfBytes);
        }
        else
        {
            throw new nl.uva.vlet.exception.ResourceTypeMismatchException(
            "File type does not support (remote) read access");
        }
    }

    /** 
     * @see #read(long,byte[],int,int)
     */
    public int read(byte buffer[],int bufferOffset,int nrOfBytes)
    throws VlException
    {
        return read(0,buffer,bufferOffset,nrOfBytes); 
    }

    /** 
     * @see #read(long,byte[],int,int)
     */
    public int read(byte buffer[],int nrOfBytes)
    throws VlException
    {
        return read(0,buffer,0,nrOfBytes); 
    }

    /**
     * Use InputStream to read bytes, not the RandomAcces method readBytes. 
     * For some implemenations this is faster. 
     * It creates a new Input Stream, skip [offset] nr of bytes and then 
     * tries to read nrOfBytes. 
     */
    public int streamRead(long offset, byte[] buffer, int bufferOffset, int nrOfBytes) throws VlException
    {
        VStreamReadable rfile = (VStreamReadable) (this);
        InputStream istr = rfile.getInputStream();

        if (istr==null)
            return -1; 

        // implementation move to generic StreamUtil: 
        return nl.uva.vlet.io.StreamUtil.syncReadBytes(istr,offset,buffer,bufferOffset,nrOfBytes); 

    }

    /**
     * Replace or create File contents with data from the bytes array. The new
     * file length will match the byte array lenth thus optionally truncating or
     * extend an existing file.
     */
    public void setContents(byte bytes[]) throws VlException
    {
        this.streamWrite(bytes,0,bytes.length); 

        return;
    }

    /**
     * Set contents using specified String. Note that the default encoding
     * format is 'UTF-8'.
     * 
     * @param contents :
     *            new Contents String
     * @throws VlException
     * @see #setContents(String contents, String encoding) to specify the coding
     */

    public void setContents(String contents) throws VlException
    {
        setContents(contents, getCharSet());
        return;
    }

   
    /** 
     * Default method to upload a file from a local location. 
     * Override this method if the implementation can provide a optimized
     * upload method (striped upload and/or bulk mode transfers) 
     * 
     * @param transferInfo
     * @param localSource   local file to upload from.  
     * @throws VlException
     */
    protected void uploadFrom(VFSTransfer transferInfo, VFile localSource) throws VlException 
    {
        // copy contents into this file. 
        vrsContext.getTransferManager().doStreamCopy(transferInfo, localSource,this); 
    }

    /**
     * Default method to download this file to a (the) local destination. 
     * <br>
     * Sub classes are encouraged to override this method if they have
     * their own (better) methods. for example bulk mode or striped file
     * transfers. 
     *
     * @see VFile#uploadFrom(VFSTransfer, VFile)
     * @param targetLocalFile The local destination file. 
     */ 

    protected void downloadTo(VFSTransfer transfer,VFile targetLocalFile)
    throws VlException
    {
        // copy contents into local file:
        vrsContext.getTransferManager().doStreamCopy(transfer,this,targetLocalFile);  
    }

    // ========================================================================
    // Extra VFile Abstract Interface Methods 
    // ========================================================================

    // ===
    // explicit inheritance definitions from VFSNode ! 
    // === 
    abstract public boolean exists() throws VlException; 

    /** 
     * Returns size (length) of files. Directories may return storage size
     * needed to store the directory entries. 
     * @return size of file or directory. Returns -1 if size is unknown.   
     */
    public abstract long getLength() throws VlException;


}
