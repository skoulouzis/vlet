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
 * $Id: Secret.java,v 1.4 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

package nl.uva.vlet.data;

import java.io.UnsupportedEncodingException;

/**
 * Holds Secret Data like a password.
 * Not used at this moment. 
 */ 
public class Secret
{
	private String name;
	
	// secret store: 
	private byte data[];
	
	/** Encodes UTF-8 String and stores it.*/  
    public Secret(String _name,String value) throws UnsupportedEncodingException
    {
        this.name=_name;
        this.data=value.getBytes("UTF-8"); 
    }
    
    public Secret(String _name,byte value[])
    {
        this.name=_name; 
        this.data=value; 
    }
    
	/** Returns optional name of secret */
	public String getName()
	{
	    return this.name; 
	}
	
	/** Decods secret data and returns as UTF-8 String. */ 
	public String getStringValue()
	{
		try
		{
			return new String(data,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Error("UnsupportedEncodingException:"+e,e); 
		}
	}
	
}
