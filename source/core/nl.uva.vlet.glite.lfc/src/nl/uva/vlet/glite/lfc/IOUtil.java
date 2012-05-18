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
 * $Id: IOUtil.java,v 1.2 2011-04-18 12:30:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:40 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/** 
 * LFC IO Util methods. 
 * Currently uses UTF-8 as default encoding.
 *  
 * Todo: update all communication to use this class for 
 * safe (java) String reading/writing. 
 * 
 * @author Piter T. de Boer 
 */
public class IOUtil
{
   
	/**
	 * Since LFC stores (non null terminated) raw bytes, 
	 * it should be UTF-8 compatible.  
	 */ 
	public static String STRING_UTF8_ENCODING="UTF-8";
	
	public static String STRING_ISO_8859 = "ISO-8859-1";
	
	/** Global LFC default string encoding */ 
	public static String defaultEncoding=STRING_UTF8_ENCODING; 
	
	/** Read string from data stream */ 
	public static String readString( final DataInputStream input) throws IOException 
	{
	    byte[] name = new byte[ 1025 ];//  
	    int i = 0;
	    
	    while( i < 1025 ) 
	    {
	      name[ i ] = input.readByte();
	      if( name[ i ] == 0x0 ) 
	      {
	         break;
	      }
	      i++;
	    }
	    
	    // String to Big.
	    // todo: use byte buffer if larger strings from LFC are possible. 
	  
	    if (i>=1025) 
	    	throw new IOException("Returned String size to big (>=1025)"); 
	    
	    return new String( name, 0, i , defaultEncoding); 
	  }
	
	/**
	 * Encoding aware string write method.  
	 * Since the number of bytes might not match the length reported
	 * by Java's String.length(). The String must first be converted to a byte array
	 * and calculate actual length !
	 * <p>
	 * Method writes terminating '0' as well.  
	 * @throws IOException 
	 */ 
	public static int writeString(DataOutputStream out, String string) throws IOException
	{
		 byte bytes[]=null; 
		 int numBytes=0; 
		  
		 if(string!=null)
		 {
		     bytes=string.getBytes(defaultEncoding);
		     numBytes=bytes.length; 
		          
		     if (bytes.length>0) 
		         out.write( bytes,0,numBytes); 
		  }
		  
		  out.writeByte( 0x0 ); // trailing 0 for path [1b] 
		  numBytes++;
		  return numBytes;
	}

	/**
	 * Calculate actual byte length of this String EXCLUDING terminating 0
	 * character.  
	 * @throws UnsupportedEncodingException 
	 */ 
	public static int byteSize(String string) throws UnsupportedEncodingException 
	{
		  if (string==null)
			  return 0; 
		  
		  return string.getBytes(defaultEncoding).length;
	}

	/**
	 * Calculate actual byte length of these Strings EXCLUDING terminating 0 
	 * characater  
	 * @throws UnsupportedEncodingException 
	 */ 
	public static int byteSize(String str1,String str2) throws UnsupportedEncodingException 
	{
		return byteSize(str1)+byteSize(str2);  
	}

    public static int byteSize(String str1, String str2, String str3) throws UnsupportedEncodingException
    {
        return byteSize(str1)+byteSize(str2)+byteSize(str3);
    }

    //public static byte[] readBytes(DataInputStream input)
    //{
    //   // TODO Auto-generated method stub
    //    return null; 
    //}
    
    /** Reads exact amount of bytes */
    public static byte[] readBytes(DataInputStream input, int size) throws IOException
    {
        if  (input==null)
            return null; 
        
        byte bytes[]=new byte[size];
        int numRead=0;
        
        // read loop: 
        while(numRead<size)
        {
            int ret=input.read(bytes,numRead,size-numRead);
            if (ret<0)
                throw new IOException("EOF"); 
            
            numRead+=ret; 
        }
        
        return bytes; 
    }
    
    /** 
     * Wrap DataInputStream object around backing byte array.
     * Uses java.nio.* classes for optimized performance. 
     */ 
    public static DataInputStream createDataInputStream(byte buffer[])
    {
        if (buffer==null)  
            throw new NullPointerException("Backing array can not be NULL"); 
        
        // use java.io ! 
        return new DataInputStream(new ByteArrayInputStream(buffer));
    }
  
	
}
