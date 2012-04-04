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
 * $Id: FileReaderToInputStream.java,v 1.3 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.io;

import java.io.IOException;
import java.io.InputStream;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrs.io.VRandomReadable;

/**
 * This class uses the (VRandomAccessable) method readBytes() method on 
 * a VFile to simulate an input stream. 
 * It is not an efficient implementation but can be used 
 * where InputStreams are not possible, but remote read() 
 * methods are possible. 
 * <p>
 * This class supports marking, since only a fileIndex is being 
 * kept as 'state', which can be reset. 
 * TODO: buffering of inputstream since this class does not buffer anything. 
 */ 

public class FileReaderToInputStream extends InputStream implements Cloneable
{	
	// File to read from
	VRandomReadable vfile;
	// offset into (remote) file 
	long fileIndex=0;  
    // mark position 
	private long markPosition=0; 
	
	
	public FileReaderToInputStream(VRandomReadable file)
	{
		this.vfile=file; 
	}

	@Override
	public int read() throws IOException
	{
		byte buffer[]=new byte[1]; 
		
		try
		{
			int numread = vfile.readBytes(fileIndex,buffer,0,1);
			
			// EOF: return -1 (do not throw Exception!)
			if (numread<0)  
				return -1; 
			 
			if (numread>1)  
				throw new IOException("Wrong nr. of bytes (expecting 1) returned, when reading from:"+vfile);
			
			fileIndex++; 
			return buffer[0]; 
		}
		catch (VlException e)
		{
			// chain into IO exception: 
			throw new IOException("Error when reading from:"+vfile
					+"\n"+e);  
		} 
	}

	@Override
	public int read(byte buffer[],int bufferOffset,int len) throws IOException
	{
		try
		{
			int numread=vfile.readBytes(fileIndex,buffer,bufferOffset, len);
			
			if (numread<0)
				return -1;
			
			fileIndex+=numread; 
			return numread; 
		}
		catch (VlException e)
		{
			// chain into IO exception:
			//e.printStackTrace();
			// IOException doesn't allow chainging, include stacktrace in 
			// error message (..)
			throw new IOException("Error when reading from:"+vfile
					+"\n"+e.toStringPlusStacktrace());  
		}
	}
	
	@Override
	public long skip(long val)
	{
		fileIndex+=val; 
		return fileIndex; 
	}
	
	/**
	 * Always returns 0
	 * FileInputStream doesn't buffer (yet) !
	 * TODO: a read ahead buffer could increased performance.  
	 */
	
	@Override
	public int available()
	{
		return 0;  
	}
	
	// File reader supports marks!
	@Override
	public boolean markSupported()
	{
		return true; 
	}
	
	@Override
	public void mark(int readLimit)
	{
		this.markPosition=this.fileIndex; 
	}
	
	// Reset file index to last mark position
	@Override
	public void reset()
	{
		this.fileIndex=markPosition; 
	}
	
	/** 
	 * Return clone of this FileInputStream. The file offset into
	 * this stream is reset to 0. 
	 */
	public FileReaderToInputStream clone()
	{
		return new FileReaderToInputStream(this.vfile);
	}
	

}
