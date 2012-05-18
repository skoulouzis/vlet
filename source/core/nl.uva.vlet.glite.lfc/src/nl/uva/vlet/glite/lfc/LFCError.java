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
 * $Id: LFCError.java,v 1.3 2011-04-18 12:30:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:40 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc;

import java.util.Hashtable;
import java.util.Map;

/**
 *  Error handling class for LFC communication.
 */
public class LFCError 
{
   /** These are standard POSIX errors from errno.h !  */ 
  final static String[] messages = 
  {
	  "No Error",
	  "Error 01: Operation not permitted",
	  "Error 02: No such file or directory",
	  "Error 03: No such process",
	  "Error 04: Interrupted system call",
	  "Error 05: I/O error",
	  "Error 06: No such device or address", 
	  "Error 07: Argument list too long",
	  "Error 08: Exec format error",
	  "Error 09: Bad file number",
	  "Error 10: No child processes",
	  "Error 11: Try again", 
	  "Error 12: Out of memory",
	  "Error 13: Permission denied",
	  "Error 14: Bad address",
	  "Error 15: Block device required",
	  "Error 16: Device or resource busy",
	  "Error 17: File/Directory exists or Directory is not empty",
	  "Error 18: Cross-device link",
	  "Error 19: No such device",
	  "Error 20: Not a directory",
	  "Error 21: Is a directory",
	  "Error 22: Invalid argument",
	  "Error 23: File table overflow",
	  "Error 24: Too many open files",
	  "Error 25: Not a typewriter",
	  "Error 26: Text file busy",
	  "Error 27: File too large",
	  "Error 28: No space left on device",
	  "Error 29: Illegal seek",
	  "Error 30: Read-only file system",
	  "Error 31: Too many links",
	  "Error 32: Broken pipe", 
	  "Error 33: Math argument out of domain of func",
	  "Error 34: Math result not representable"
  };
  
  private static Map<Integer,String> errorMap=new Hashtable<Integer,String>(); 
  
  static
  {
      initErrorMap(); 
  }
  
  private static void initErrorMap()
  {
      for (int i=0;i<messages.length;i++)
      {
          errorMap.put(new Integer(i),messages[i]); 
      }
      
      // LFC: Now With Extra Errors! 
      errorMap.put(1004,"Error 1004: Time out."); 
      errorMap.put(2703,"Error 2703: Possible Invalid VOMS Credential Error (VOMS Attribute expired?).");
  }
  
  //
  // === instance === 
  //
  
  private int code;

  public LFCError( final int code ) 
  {
    this.code = code;
  }
  
  /**
   * getter for error code
   * @return error code
   */
  public int getCode()
  {
    return this.code;
  }
  
  /**
   * @return human readable explanation of the error code
   */
  public String getMessage()
  {
      return getMessage(code); 
  }

  /**
   * @param number error code number
   * @return human readable explanation of the error code
   */
  static public String getMessage( final int number ) 
  {
      String result = errorMap.get(new Integer(number)); 
      if (result==null)
          return "Error :#"+number; 
      return result;
  }
}
