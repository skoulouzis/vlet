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
 * $Id: LFCExceptionWapper.java,v 1.2 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.glite.lfc.LFCException;

/**
 * Mapping of LFC error codes to VlExceptions.  
 * The error codes seem POSIX error codes. Some might never ocure.
 * 
 * Updated by Piter T. de Boer.  
 */
public class LFCExceptionWapper 
{

//    private static final void initErrorMap()
//	{
//
//		lfcErrors.put(1, new VlException("Operation not permitted"));
//		lfcErrors.put(2, new nl.uva.vlet.exception.ResourceNotFoundException(
//				"No such file or directory"));
//		lfcErrors.put(3, new nl.uva.vlet.exception.VlException(
//				"No such process"));
//		lfcErrors.put(4, new nl.uva.vlet.exception.VlInterruptedException(
//				"Interrupted system call"));
//		lfcErrors.put(5, new nl.uva.vlet.exception.VlIOException("I/O error"));
//		lfcErrors.put(6, new nl.uva.vlet.exception.VlException(
//				"No such device or address"));
//		lfcErrors.put(7, new nl.uva.vlet.exception.VlException(
//				"Argument list too long"));
//		lfcErrors.put(8, new nl.uva.vlet.exception.VlException(
//				"Exec format error"));
//		lfcErrors.put(9, new nl.uva.vlet.exception.ResourceException(
//				"Bad file number"));
//		lfcErrors.put(10, new nl.uva.vlet.exception.VlException(
//				"No child processes"));
//		lfcErrors.put(11, new nl.uva.vlet.exception.VlException("Try again"));
//		lfcErrors.put(12,
//				new nl.uva.vlet.exception.VlException("Out of memory"));
//		lfcErrors.put(13, new nl.uva.vlet.exception.ResourceAccessDeniedException(
//				"Permission denied"));
//		lfcErrors.put(14, new nl.uva.vlet.exception.VlException("Bad address"));
//		lfcErrors.put(15, new nl.uva.vlet.exception.VlException(
//				"Block device required"));
//		lfcErrors.put(16, new nl.uva.vlet.exception.VlException(
//				"Device or resource busy"));
//		lfcErrors.put(17,
//				new nl.uva.vlet.exception.ResourceAlreadyExistsException(
//						"File/Directory exists or Directory is not empty"));
//		lfcErrors.put(18, new nl.uva.vlet.exception.VlException(
//				"Cross-device link"));
//		lfcErrors.put(19, new nl.uva.vlet.exception.VlException(
//				"No such device"));
//		lfcErrors.put(20,
//				new nl.uva.vlet.exception.ResourceTypeMismatchException(
//						"Not a directory"));
//		lfcErrors.put(21,
//				new nl.uva.vlet.exception.ResourceTypeMismatchException(
//						"Is a directory"));
//		lfcErrors.put(22, new nl.uva.vlet.exception.VlException(
//				"Invalid argument"));
//		lfcErrors.put(23, new nl.uva.vlet.exception.VlException(
//				"File table overflow"));
//		lfcErrors.put(24, new nl.uva.vlet.exception.VlIOException(
//				"Too many open files"));
//		lfcErrors.put(25, new nl.uva.vlet.exception.VlException(
//				"Not a typewriter"));
//		lfcErrors.put(26, new nl.uva.vlet.exception.ResourceException(
//				"Text file busy"));
//		lfcErrors.put(27, new nl.uva.vlet.exception.ResourceToBigException(
//				"File too large"));
//		lfcErrors.put(28, new nl.uva.vlet.exception.VlIOException(
//				"No space left on device"));
//		lfcErrors.put(29, new nl.uva.vlet.exception.VlIOException(
//				"Illegal seek"));
//		lfcErrors.put(30, new nl.uva.vlet.exception.ResourceException(
//				"Read-only file system"));
//		lfcErrors.put(31, new nl.uva.vlet.exception.VlIOException(
//				"Too many links"));
//		lfcErrors.put(32,
//				new nl.uva.vlet.exception.VlIOException("Broken pipe"));
//		lfcErrors.put(33, new nl.uva.vlet.exception.VlException(
//				"Math argument out of domain of func"));
//		lfcErrors.put(34, new nl.uva.vlet.exception.VlException(
//				"Math result not representable"));
//
//	}

	
	public static VlException getVlException(int code,LFCException e)
	{
		VlException ex=null;
		String msgstr=e.getMessage(); 
		
		// cast some LFCExceptions into known VlExceptions: 
		
		switch (code)
		{
			case 2: 
				ex=new nl.uva.vlet.exception.ResourceNotFoundException(msgstr,e); // No such file or directory 
				break; 
			case 4:
				ex=new nl.uva.vlet.exception.VlInterruptedException(msgstr,e); // Interrupted proces 
				break; 
			case 5:
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e);  // I/O Error 
				break; 
			case 6:
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e);  // No such device or address 
				break; 
			case 9:
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // Bad File number
				break;
            case 11:
                ex=new nl.uva.vlet.exception.VlException("LFCException","Bacon not ready. Try again",e);  // Try again ? 
                break;
            case 13:
                ex=new nl.uva.vlet.exception.ResourceAccessDeniedException(msgstr,e); // Permission denied
                break;
			case 15: 
				ex=new nl.uva.vlet.exception.VlIOException("Block device required\n."+msgstr,e);
				break; 
			case 16:
				ex=new nl.uva.vlet.exception.VlIOException("Device or resource busy.\n"+msgstr,e);
				break; 
			case 17: 
				ex=new nl.uva.vlet.exception.ResourceAlreadyExistsException(
						"File/Directory exists or Directory is not empty.\n"+msgstr,e);
				break; 
			case 18: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // Cross-device link
				break; 
			case 19: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e) ; // No such device
				break; 
			case 20: 
				ex=new nl.uva.vlet.exception.ResourceTypeMismatchException(msgstr,e); // Not a directory
				break; 
			case 21: 
				ex=new nl.uva.vlet.exception.ResourceTypeMismatchException(msgstr,e); // Is a directory 
				break;  
			case 24: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // Too many open files
				break;
			case 25: 
				ex=new nl.uva.vlet.exception.VlException("LFCException","Not a typewriter? o.O.\n"+msgstr,e);
				break; 
			case 26: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // Text file busy
				break; 
			case 27: 
				ex=new nl.uva.vlet.exception.ResourceToBigException(msgstr,e); // File to large
				break; 
			case 28: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // No space left on device 
				break; 
			case 29:  
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); //Illegal Seek ? 
				break; 
			case 30: 
				ex=new nl.uva.vlet.exception.ResourceException("Read-only file system.\n"+msgstr,e);
				break; 
			case 31: 
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // too many links
				break; 
			case 32:
				ex=new nl.uva.vlet.exception.VlIOException(msgstr,e); // broken pipe
				break;
			case 34: 
				ex=new nl.uva.vlet.exception.VlException("Math result not representable (or you can't understand it).\n"+msgstr,e);
				break;
			default:
				ex=new VlException("LFCException",e.getMessage(),e); 
				break;
		}
		return ex; 
	}
   

}
