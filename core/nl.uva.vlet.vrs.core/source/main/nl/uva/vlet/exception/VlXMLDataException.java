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
 * $Id: VlXMLDataException.java,v 1.4 2011-04-18 12:00:31 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:31 $
 */ 
// source: 

package nl.uva.vlet.exception;

/**
 * XML Parsing Error. 
 * @author P.T. de Boer
 */
public class VlXMLDataException extends VlInternalError
{
	private static final long serialVersionUID = -5796344121311466193L;

	public VlXMLDataException(String message)
    {
        super("XMLDataException",message);
    }

    /** Constructor which keeps original System Exception */
    public VlXMLDataException(String message, Throwable cause)
    {
        super("XMLDataException",message,cause); 
    }

}
