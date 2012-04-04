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
 * $Id: testVAttribute.java,v 1.1 2011-12-06 09:59:17 ptdeboer Exp $  
 * $Date: 2011-12-06 09:59:17 $
 */ 
// source: 

package test.data;

import junit.framework.Assert;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;

import org.junit.Test;

public class testVAttribute
{
    @Test 
	public void testConstructors() throws VRLSyntaxException
	{
        testConstructor(VAttributeType.STRING,"name","value");
        testConstructor(VAttributeType.INT,"name","1");
        testConstructor(VAttributeType.LONG,"name","10000000000");
     
        testConstructor(VAttributeType.BOOLEAN,"name","true");
        testConstructor(VAttributeType.BOOLEAN,"name","false");
        testConstructor(VAttributeType.BOOLEAN,"name","True");
        testConstructor(VAttributeType.BOOLEAN,"name","False");
        testConstructor(VAttributeType.BOOLEAN,"name","TRUE");
        testConstructor(VAttributeType.BOOLEAN,"name","FALSE");
        
        // watch out for rounding errors from decimal to IEEE floats/doubles ! 
        testConstructor(VAttributeType.FLOAT,"name","-1.125");
        testConstructor(VAttributeType.DOUBLE,"name","-1.1234");
        
        long millies=System.currentTimeMillis(); 
        testConstructor(VAttributeType.TIME,"name",Presentation.createNormalizedDateTimeString(millies));  
        
        testConstructor(VAttributeType.VRL,"name","file://user@host.domain:1234/Directory/A File/");
               
	}
    
    public void testConstructor(VAttributeType type,String name,String value) 
    {
        // basic constructor tests 
        VAttribute attr=new VAttribute(type,name,value);
        // check type,name and value 
        Assert.assertEquals("Type must be:"+type,type,attr.getType());
        Assert.assertEquals("String values must match for type:"+type,value,attr.getStringValue()); 
        Assert.assertEquals("Attribute name must match",name,attr.getName()); 
        
        Assert.assertTrue("isType() must return true for attr:"+attr,attr.isType(type));  
        
  
	}
    

}
