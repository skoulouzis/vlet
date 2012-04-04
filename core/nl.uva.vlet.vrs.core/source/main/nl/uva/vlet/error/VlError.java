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
 * $Id: VlError.java,v 1.2 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.error;

/**
 * The VlError cass is for low level errors which do not fit in the Exception paradigm.
 * Similar to the Java Convention to throw java.lang.Error in the case of programming 
 * errors except VlError the the VRS Variant.  
 */
public class VlError extends Error
{
	private static final long serialVersionUID = 6510047961863859404L;

	public VlError(String msg) 
	{
		super(msg); 
	}

	public VlError(String msg, Throwable cause) 
	{
		super(msg,cause); 
	}

}
