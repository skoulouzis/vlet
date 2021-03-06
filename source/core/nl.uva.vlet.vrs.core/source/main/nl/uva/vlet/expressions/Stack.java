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
 * $Id: Stack.java,v 1.3 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */ 
// source: 

package nl.uva.vlet.expressions;

import java.util.Vector;


public class Stack
{
	Vector<Parameter> parameterStack=new Vector<Parameter>();

	/** Get first matching parameter from stack */ 
	public synchronized Parameter getParameter(String name)
	{
		for (int i=parameterStack.size()-1;i>=0;i++)
		{
			if (parameterStack.elementAt(i).getName().compareTo(name)==0) 
				return parameterStack.elementAt(i);
		}
		
		return null; 
	}
	
	/** Push parameter on stack, return index of Parameter on stack */ 
	public synchronized int push(Parameter par)
	{
		this.parameterStack.add(par);
		return this.parameterStack.size()-1; 
	}
	
	public synchronized Parameter pop()
	{
		Parameter par; 
		
		par=parameterStack.lastElement(); 
		parameterStack.remove(this.parameterStack.size()-1); 
		return par; 
	}
	
	
}
