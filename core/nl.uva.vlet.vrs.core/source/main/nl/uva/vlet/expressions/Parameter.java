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
 * $Id: Parameter.java,v 1.3 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */ 
// source: 

package nl.uva.vlet.expressions;

import nl.uva.vlet.data.VAttribute;

public class Parameter extends VAttribute
{
	// 
	private static final long serialVersionUID = 553797913457929690L;
	
	public static final String RESULT = "Result"; 

	
	public Parameter(String name, String value)
	{
		super(name, value);
		enableEditable(); 
	}

	public Parameter(String name, boolean boolVal)
	{
		super(name,boolVal);
		enableEditable(); 

	}

	public Parameter(String name, int intVal)
	{
		super(name,intVal);
		enableEditable(); 

	}
	
	public Parameter(String name, long longVal)
	{
		super(name,longVal);
		enableEditable(); 

	}
	
	/** Set or Enum Parameter type */ 
	public Parameter(String name, String values[])
	{
		super(name,values,(String)null);
		enableEditable(); 
	}

	public Parameter(String name, double value)
	{
		super(name,value);
		enableEditable(); 
	}

	private void enableEditable()
	{
		this.setEditable(true); 
	}
	
	/** 
	 * After this method is called, it is NOT possible
	 * to change the 'const' flag again to protect this Parameter 
	 */
	public void isConst()
	{
		this.setEditable(false); 
	}
	
	
}
