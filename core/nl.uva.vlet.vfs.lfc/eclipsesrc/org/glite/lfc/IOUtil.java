package org.glite.lfc;

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
		  
		  if(string!=null)
		  {
			  bytes=string.getBytes(defaultEncoding);
		  }
		  else
		  {
			  bytes=new byte[0]; 
		  }
		  
		  int numBytes=bytes.length; 
		  
		  if (bytes.length>0) 
		  {
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
	
}
