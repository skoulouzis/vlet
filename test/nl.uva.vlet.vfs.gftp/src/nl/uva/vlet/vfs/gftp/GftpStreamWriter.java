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
 * $Id: GftpStreamWriter.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;


/**
 * Simulate an OutputStream by writing the bytes 
 * to the file with the writeBytes() method.  
 */
/*
public class GftpStreamWriter extends OutputStream
{
    private String filepath=null; 
    
    // OutputStreamDataSource dataSource = null;
    // private OutputStream outputStream=null;
    // private boolean closeClient=false;
    // Let GftpServer do the synchronized write ! 
    private GftpServer server=null;
    // private TransferState state=null;
    // MarkerListener markerListener=null;
   
    private long fileIndex=0; 
    
    public GftpStreamWriter(GftpServer server,String filepath,boolean append) 
    {
        this.filepath=filepath; 
        this.server=server; 
        //this.dataSource=null;
        
        //this.outputStream=null;  
        //this.closeClient=closeClient;
        //this.markerListener=null;
    }
    
    public void close() throws IOException
    {
        Global.debugPrintln(this,"Closing outputStream:"+filepath);
        // nothing to be done ! 
        
        // state.transferDone(); 
        //this.outputStream.close();
    }
    
    // == Wrapped Methods 
    
    public void write(int b) throws IOException
    {
        byte buffer[]=new byte[1];
        buffer[0]=(byte)b; 
        write(buffer); 
    }
    
    public void write(byte[] buffer) throws IOException
    {
        write(buffer,0,buffer.length);
    }
    
    public void write(byte[] buffer,int off,int len) throws IOException
    {
        Global.debugPrintln(this,"Writing2 bytes:"+buffer.length+" To outputStream:"+filepath);
        
        try
        { 
           this.server.syncWrite(filepath,fileIndex,buffer,off,len);
           fileIndex+=len; 
        }
        catch (VlException e)
        {
            // nice, original exception could be IO exception wrapped 
            // as VlException which is now wrapped again as IO exception...
            throw new IOException("GFTP VlException:"+e.getMessage()); 
        }
     
    }
    
    public void flush() throws IOException 
    {
        ; // nothing to be flushed 
    }
   
   
}
*/