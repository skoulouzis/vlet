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
 * $Id: testDataAndUtil.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.global;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.data.StringUtil;

public class testDataAndUtil extends TestCase
{

    
    public void testStringIsNonWhiteSpace()
    {
        Assert.assertFalse("isNonWhiteSpace: NULL String should return FALSE",StringUtil.isNonWhiteSpace(null));
        Assert.assertFalse("isNonWhiteSpace: Empty String should return FALSE",StringUtil.isNonWhiteSpace("")); 
        Assert.assertFalse("isNonWhiteSpace: Single Tab String should return FALSE",StringUtil.isNonWhiteSpace("\t")); 
        Assert.assertFalse("isNonWhiteSpace: Single NewLine String should return FALSE",StringUtil.isNonWhiteSpace("\n")); 
        Assert.assertFalse("isNonWhiteSpace: Double Tab String should return FALSE",StringUtil.isNonWhiteSpace("\t\t")); 
        Assert.assertFalse("isNonWhiteSpace: Double NewLine String should return FALSE",StringUtil.isNonWhiteSpace("\n\n"));
        
        Assert.assertTrue("isNonWhiteSpace: Single char should return TRUE",StringUtil.isNonWhiteSpace("a"));
        Assert.assertTrue("isNonWhiteSpace: Spaced Single char should return TRUE",StringUtil.isNonWhiteSpace(" a"));
        Assert.assertTrue("isNonWhiteSpace: Spaced Single char should return TRUE",StringUtil.isNonWhiteSpace("a "));
               
    }
}