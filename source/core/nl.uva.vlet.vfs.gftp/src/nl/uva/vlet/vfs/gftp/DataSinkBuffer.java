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
 * $Id: DataSinkBuffer.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;

import java.io.IOException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;

import org.globus.ftp.Buffer;
import org.globus.ftp.DataSink;


/**
 * 
 * Simple DataSink buffer for GFTP transactions. 
 * Creates a DataSink buffer to write to.
 * This class also records transfer statistics. 
 * @author P.T. de Boer
 */
public class DataSinkBuffer implements DataSink
{
    private static int classCounter=0; 
    
    private static ClassLogger logger=null; 
    
    static 
    {
        logger=ClassLogger.getLogger(DataSinkBuffer.class); 
        //logger.setLevelToDebug(); 
    }
    
    /// =================   ///
    // 
    /// =================   ///
    
    private int instanceNr=classCounter++; 
    
    private byte[] bytes=null; // the byte buffer to write to 
    
    private int totalBytesWritten=0;
    
    // expectedNrOfBytes nr of bytes to be written to this DataSink. Might be less the buffer.length!  
    private int expectedNrOfBytes=0;  
    
    /** Starting offset in bytes[]  */ 
    private int offset=0;

    private int expectedEnd=0;

    private long currentThread=-1;
    /**
     * Create simple buffer to act as datasink. After it
     * is full no data can be written!  
     */ 
    public DataSinkBuffer(int size)
    {
        bytes=new byte[size]; 
        expectedNrOfBytes=size; 
    }
    
    /** 
     * Creates DataSinkBuffer around byte array.
     */ 
    public DataSinkBuffer(byte[] buffer)
    {
        bytes=buffer;  
        expectedNrOfBytes=buffer.length; 
    }
    /** 
     * Creates DataSinkBuffer around byte array.
     * Start to fill buffer at offsetInBuffer, expects expectedNB bytes.  
     */ 
    public DataSinkBuffer(byte[] buffer, int offsetInBuffer, int expectedNB) throws VlException
    {
        logger.debugPrintf(">>> New Buffer: size(+offset)==%d(+%d)\n",expectedNB,offsetInBuffer); 
        
        this.bytes=buffer;                 // store bytes here   
        this.offset=offsetInBuffer;        // starting place to write in buffer
        this.expectedNrOfBytes=expectedNB; 
        this.expectedEnd=offsetInBuffer+expectedNB;  
        
        if (expectedEnd>buffer.length)
        {
            throw new VlException("Buffer Init Error","DataSinkBuffer: buffer array is to small."
                    +"buffer.length="+buffer.length+","
                    +"offsetInBuffer="+offsetInBuffer+",expecedNrOfBytes="+expectedNrOfBytes); 
        }
    }
    
    /**
     * Write Buffer buf to my Buffer.
     * Note that the buffer.getOffset() notes an extra offset
     * as read from the source.  For streaming read this is not used. 
     * Succesful write calls will append each buffer to this datasink's buffer. 
     */
    public void write(Buffer buf) throws IOException
    {
        
        // check Thread ID, but do not Synchronize
        // assertSetThread(-1,Thread.currentThread().getId()); 
        
//        logger.errorPrintf("+++ Write [%d.T#%d]: %9d - %9d (#%d+%d) \n",
//                instanceNr, 
//                Thread.currentThread().getId(),
//                offset,
//                offset+buf.getLength(),
//                buf.getLength(), 
//                buf.getOffset()); 
        
        byte[] data=buf.getBuffer(); // get actual byte array
        long sourceOffset=buf.getOffset(); // extra offset this buffer represents
        int datalen=buf.getLength();
              
        if (sourceOffset<0)
        {
            //logger.debugPrintf("Negative offset (= no offset): %d\n",sourceOffset); 
            // if sourceOffset==-1, offset is not supported !    
            sourceOffset=-1;   
        }
        else
        {
            // TODO: DataSink buffer could receive offset notifications 
            //logger.errorPrintf("*** Buffer provides Source Offset:%d",sourceOffset);
            throw new IOException("Asynchronous writing to buffer not supported"); 
        }  
         
      
        //System.out.printf("Write:[%d]:#%d(+%d)\n",instanceNr,datalen,buf.getOffset()); 
        
        int start=(int)(offset); // start of Buffer
        int end=(int)(offset+datalen); 
       
        // Buffers can be bigger the the requested data.
        // (GridFTPFS send buffers in 2048 size 
        // Do not store more dan then is requested ! 
        
        if (end>expectedEnd)
        {
            // Allowed.  Buffers usually come in chunks of 1024 or 2048. 
            //logger.warnPrintf("*** Buffer overflow: end of buffer reached ( end > expected): %d > %d\n",end,expectedEnd);
            end=expectedEnd; 
            datalen=end-start; // update datalen
            //throw new IOException("Buffer Overflow. Trying to write beyond end of buffer:"+end+">"+bytes.length+"!"); 
        }

        // loop:
        //for (int i=start;(i<end);i++) 
        //    bytes[i]=data[i-start];
        // mem/array copy !
        System.arraycopy(data, 0,bytes,start,datalen); 
        
        totalBytesWritten+=datalen;
        
        // start next write after this one: 
        if(sourceOffset==-1)
            offset+=datalen; // update location for next buffer
        
        // check whether current thread still is the one writing to it. 
        //assertSetThread(Thread.currentThread().getId(),-1);
    }

    private Object threadMutex=new Object(); 
    
    private void assertSetThread(long oldValue,long newValue) 
    {
        synchronized(threadMutex)
        {
            if (this.currentThread!=oldValue) 
                throw new Error("DataSinkBuffer:Thread Assertion Failed.");
            
            this.currentThread=newValue;
        }
    }
    
    public void close() throws IOException
    {
    }

    /** Returns byte array */
    public byte[] getBytes()
    {
        return bytes;
    }
    
    public boolean isDone()
    {
        return (this.totalBytesWritten>=this.expectedNrOfBytes);    

    }

    public int getNrOfBytesWritten()
    {
        return this.totalBytesWritten;
    }

    

}