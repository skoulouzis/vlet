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
 * $Id: IntegerHolder.java,v 1.5 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

package nl.uva.vlet.data;

/** 
 * Integer holder class for VAR Integer types.
 */ 
public class IntegerHolder implements VARHolder<Integer>
{
	public Integer value=null;

	public IntegerHolder(Integer val)
	{
		this.value=val; 
	}

	public IntegerHolder()
	{
	}

	public int intValue()
	{
		if (value!=null)
			return value;
		
		throw new NullPointerException("Value in IntegerHolder is NULL");
		
	}
	
	/** Returns Holder value or defVal if holder does not contain any value */ 
	public int intValue(int defVal)
	{
		if (value!=null)
			return value;
	
		return defVal;  
	}
	
	public boolean isSet()
	{
		return (value!=null);  
	}
	
    public Integer get()
    {
        return this.value; 
    }
    
    public void set(Integer val)
    {
        this.value=val;  
    }

}



