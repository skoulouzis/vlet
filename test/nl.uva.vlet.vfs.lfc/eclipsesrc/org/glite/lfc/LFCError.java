/*****************************************************************************
 * Copyright (c) 2008, 2008 g-Eclipse Consortium 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *    Spiros Koulouzis     - added more errors codes 
 *****************************************************************************/

package org.glite.lfc;

import nl.uva.vlet.exception.VlException;


/**
 *  Error handling class for LFC communication.
 */
public class LFCError 
{
   /** These seem like standard POSIX errors from errno.h !  */ 
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
  
  static protected String defaultMessage = "Error" ;
  
  //
  // === instance === 
  //
  
  private int code;

  LFCError( final int code ) 
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
    String result = defaultMessage;
    if ( this.code < messages.length ) 
    {
      result = messages[ this.code ];
    }
    return result;
  }

  /**
   * @param number error code number
   * @return human readable explanation of the error code
   */
  static public String getMessage( final int number ) 
  {
    String result = defaultMessage + ": "+number;
    if ( number < messages.length )
    {
        result = messages[ number ];
    }
    return result;
  }
}
