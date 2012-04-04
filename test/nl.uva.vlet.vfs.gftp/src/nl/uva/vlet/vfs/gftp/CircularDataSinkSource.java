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
 * $Id: CircularDataSinkSource.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;
/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: CircularDataSinkSource.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */ 
// source: 

//package nl.uva.vlet.vfs.gridftpfs;
//import java.io.IOException;
//import java.util.Vector;
//
//import org.globus.ftp.Buffer;
//import org.globus.ftp.DataSink;
//import org.globus.ftp.DataSource;
//
///**
// * Circular buffer to read data from and write data to.
// * <p>
// * The implementation is quite simple. 
// * For each write, store the buffer, and for each read, 
// * return the buffer on a first in, first out principle! 
// * 
// * @author P.T. de Boer
// */
//
//public class CircularDataSinkSource implements DataSink, DataSource
//{
//    private Vector<Buffer> buffers=new Vector<Buffer>();
//    private int bufferCount=0;
//    
//    public CircularDataSinkSource()
//    {
//        
//    }
//    public void write(Buffer buffer) throws IOException
//    {
//        synchronized(buffers)
//        {
//            System.err.println("Adding buffer count ="+bufferCount); 
//            System.err.println("Adding buffers size ="+buffers.size()); 
//            System.err.println("Adding buffer length="+buffer.getLength()); 
//            System.err.println("Adding buffer offset="+buffer.getOffset()); 
//            
//            buffers.add(buffer);
//            bufferCount++; 
//        }
//        
//        readMutex.notify(); // notify ONE 
//    }
//
//    public void close() throws IOException
//    {
//        readMutex.notifyAll(); // notify ALL waiting readers; 
//        System.err.println("Upon close, nr of buffers="+buffers.size()); 
//    }
//    
//    Boolean readMutex=new Boolean(true); 
//
//    public Buffer read() throws IOException
//    {
//        boolean wait=false;
//        
//        synchronized(buffers)
//        {
//            // nothing to read; 
//            if (buffers.size()==0)
//            {
//                wait=true; 
//            }
//        }
//        
//        if (wait) synchronized(readMutex)
//        {
//            try
//            {
//                 readMutex.wait();
//            }
//            catch (InterruptedException e)
//            {
//                 System.out.println("***Error: Exception:"+e); 
//                 e.printStackTrace();
//            }
//        }
//
//                
//        synchronized(buffers)
//        {
//            System.err.println("Return bufferm, buffers size ="+buffers.size()); 
//            Buffer buffer=buffers.elementAt(0); 
//            buffers.remove(0);  
//            return buffer; 
//        }
//    }
//
//    public long totalSize() throws IOException
//    {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//
//}
