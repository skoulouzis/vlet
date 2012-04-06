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
 * $Id: VlPasswordException.java,v 1.2 2011-04-18 12:00:32 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:32 $
 */ 
// source: 

package nl.uva.vlet.exception;

/**
 * @author P.T. de Boer
 * 
 * Grid Credential Exception(s)
 */
public class VlPasswordException extends VlAuthenticationException
{
    private static final long serialVersionUID = -9218600987549414855L;

    public VlPasswordException(String message)
    {
        super("Invalid Password Exception",message);
    }

    /** Create VlException:  CrendentialException which keeps original System Exception */
    public VlPasswordException(String message, Exception e)
    {
        super("Invalid Password Exception",message,e); 
    }

}
