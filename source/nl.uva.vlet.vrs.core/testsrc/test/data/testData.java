/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: testData.java,v 1.1 2011-12-06 09:59:17 ptdeboer Exp $  
 * $Date: 2011-12-06 09:59:17 $
 */ 
// source: 

package test.data;

import java.util.Enumeration;
import java.util.Hashtable;

import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vrl.VRL;

public class testData
{

	public static void main(String args[])
	{
		try
		{
			testVRLHash();
		}
		catch (VRLSyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testVRLHash() throws VRLSyntaxException
	{
		Hashtable<VRL,String> table=new Hashtable<VRL,String>(); 
		
		VRL  vrl=new VRL("aap://noot/mies");
		
		 
	
		table.put(vrl,"Value1");
		 vrl=new VRL("aap://noot/mies");
		table.put(vrl,"Value2"); 
		 vrl=new VRL("aap://noot/mies");
		table.put(vrl,"Value3"); 
		 vrl=new VRL("aap://noot/mies");
		table.put(vrl,"Value4"); 

		Enumeration<VRL> keys = table.keys(); 
		
		VRL key=null;
		for (;keys.hasMoreElements()&&(key=keys.nextElement())!=null;)
		{
			System.err.println("key="+key);
		}
	}
}
