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
 * $Id: BufferUtil.java,v 1.4 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Buffer Util classes. Based upon code example on the web. 
 * To convert an array of bytes to an InputStream or DataInputStream. 
 * Added some more convenient methods. 
 * 
 * @author Piter T. de Boer
 *
 */
public class BufferUtil 
{
	
	/** ByteArrayStream: create InputStream from Byte Array */ 
	public static final class ByteArrayStream extends ByteArrayInputStream
	{
	    public ByteArrayStream(byte[] bytes)
	    {
	    	super(bytes); 
	    }

	    /** Current read position */ 
	    final int  getIndex() 
	    { 
	    	return pos;
	    }
	    
	    /** Move back into buffer */ 
	    final void unreadByte() throws IOException 
	    { 
	    	if (pos > 0) 
	    		pos--; 
	    	else
	    		throw new IOException("Buffer underflow: trying to read back before beginning of input"); 
	    }
	    
	    final int size()
	    {
	    	return this.buf.length; 
	    }
  	}
	 
	/**
	 * ByteBufferStream: Create an DataInputStream from a Byte Buffer. 
	 *
	 */
	public static final class ByteDataInputStream extends DataInputStream 
	{
	  private ByteArrayStream byte_stream;

	  ByteDataInputStream(byte[] bytes) 
	  { 
	    super(new ByteArrayStream(bytes));
	    byte_stream = (ByteArrayStream)in;
	  }

	  public final int getIndex()   
	  {
		  return byte_stream.getIndex(); 
	  }    
	  
	  public final int size()
	  {
		  return byte_stream.size(); 
	  }
	  
	  public final void unreadByte() throws IOException
	  {
		  byte_stream.unreadByte(); 
	  }
	  
	  /** Wrap ByteBufferDataInputStream around byte buffer */ 
	  public static final ByteDataInputStream wrap(byte bytes[])
	  {
		  return new ByteDataInputStream(bytes); 
	  }
	}
	
	// ========================================================================
	// Static Factory methods.  
	// ========================================================================
	
	public static ByteArrayStream createByteArrayStream(byte bytes[])
	{
		return new ByteArrayStream(bytes); 
	}
	
	public static ByteDataInputStream createByteBufferDataStream(byte bytes[])
	{
		return new ByteDataInputStream(bytes); 
	}
	
	
}



