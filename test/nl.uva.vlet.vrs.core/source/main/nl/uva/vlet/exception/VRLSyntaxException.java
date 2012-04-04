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
 * $Id: VRLSyntaxException.java,v 1.1 2011-06-07 14:54:25 ptdeboer Exp $  
 * $Date: 2011-06-07 14:54:25 $
 */ 
// source: 

package nl.uva.vlet.exception;

public class VRLSyntaxException extends VlURISyntaxException
{
    private static final long serialVersionUID = 1127122143063694760L;

    public VRLSyntaxException(Exception e)
    {
        super("VRLSyntaxException",e.getMessage(),e);  
    }

    public VRLSyntaxException(String msg)
    {
        super("VRLSyntaxException",msg,null);
    }

	public VRLSyntaxException(String message, Exception e)
	{
		super("VRLSyntaxException",message,e);
	}

}
