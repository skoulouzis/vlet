/*
 * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: PATCHEDSRBFileInputstream.java,v 1.1 2010-01-11 11:05:09 ptdeboer Exp $  
 * $Date: 2010-01-11 11:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import java.io.IOException;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileInputStream;

/** 
 * Patched SRBFileInputstream which masks an  
 * error in the read() method
 * 
 * @author P.T. de Boer
 */
public class PATCHEDSRBFileInputstream extends SRBFileInputStream //InputStream 
{
    public PATCHEDSRBFileInputstream(SRBFile arg0) throws IOException
    {
        super(arg0);
    }

    
    /* 
     * Patched method from SRBFileInputstream
     * read() method returns signed byte, thus
     * for byte values >127 it returns negative (usigned 128= signed -128)
     * integers. This method has been patched. 
     * See bugreport: 
     *    http://srb.npaci.edu/bugzilla/show_bug.cgi?id=247 
     */
    public int read() throws IOException
    {
        // use single byte bugger to read byte 
        // instead of faulty read() method. 
        
        byte buffer[]=new byte[1]; 
        
        int numread=0;
        
        while(numread==0) 
            numread=read(buffer);
        
        if (numread<=0) 
            return -1; 
            
        // return POSITIVE (unsigned) byte: 
        
        return ((int)buffer[0])&0x00FF;  
        
        
    }
    /*
    public int read(byte b[]) throws IOException 
    {
        orginputs.read(b); 
    }
    
    public int read(byte b[], int off, int len) throws IOException 
    {
        orginputs.read(b,off,len); 
    }
    
    public long skip(long n) throws IOException 
    {
       return orginputs.skip(n);
    }
    
    public int available() throws IOException 
    {
        return orginputs.available();
    }

    public void close() throws IOException 
    {
        orginputs.close(); 
    }

    public synchronized void mark(int readlimit)
    {
        orginputs.mark(readlimit); 
    }
    
    public synchronized void reset() throws IOException
    {
        orginputs.reset(); 
    }
    
    public boolean markSupported() 
    {
        return orginputs.markSupported(); 
    }*/
}
