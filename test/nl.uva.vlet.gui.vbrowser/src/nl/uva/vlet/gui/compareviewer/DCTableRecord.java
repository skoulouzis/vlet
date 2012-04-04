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
 * $Id: DCTableRecord.java,v 1.3 2011-04-18 12:27:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:26 $
 */ 
// source: 

package nl.uva.vlet.gui.compareviewer;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME; 
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;

public class DCTableRecord
{
	VAttributeSet attrs=new VAttributeSet(); 
	
	private static String attributeNames[]=
		{
			ATTR_NAME,
			ATTR_LENGTH, 
			ATTR_MODIFICATION_TIME
		};
	
	public static String[] getDefaultAttributeNames()
	{
		return attributeNames;
	}
	
	public DCTableRecord(VAttributeSet newAttrs)
	{
		attrs.put(newAttrs.get(ATTR_NAME)); 
		attrs.put(newAttrs.get(ATTR_LENGTH));  
		attrs.put(newAttrs.get(ATTR_MODIFICATION_TIME)); 
	}

	/** Return Attribute Object */ 
	
	VAttribute getRowEntry(int i)
	{
		if (i==0) 
			return attrs.get(ATTR_NAME);
		else if (i==1)
			return attrs.get(ATTR_LENGTH);
		else if (i==2) 
			return attrs.get(ATTR_MODIFICATION_TIME);
		
		return null; 
	}
	
	public String toString()
	{
		return "["+getRowEntry(0)+"|"+getRowEntry(1)+"|"+getRowEntry(2)+"]"; 
	}
}
