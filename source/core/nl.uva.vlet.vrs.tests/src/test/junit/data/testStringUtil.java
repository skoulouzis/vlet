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

package test.junit.data;

import nl.uva.vlet.data.StringUtil;

import org.junit.Assert;
import org.junit.Test;

public class testStringUtil 
{

    @Test
    public void testStringUtl_IsNonWhiteSpace()
    {
        Assert.assertFalse("isNonWhiteSpace: NULL String should return FALSE",StringUtil.isNonWhiteSpace(null));
        Assert.assertFalse("isNonWhiteSpace: Empty String should return FALSE",StringUtil.isNonWhiteSpace("")); 
        Assert.assertFalse("isNonWhiteSpace: Empty String should return FALSE",StringUtil.isNonWhiteSpace(" ")); 
        Assert.assertFalse("isNonWhiteSpace: Single Tab String should return FALSE",StringUtil.isNonWhiteSpace("\t")); 
        Assert.assertFalse("isNonWhiteSpace: Single NewLine String should return FALSE",StringUtil.isNonWhiteSpace("\n")); 
        Assert.assertFalse("isNonWhiteSpace: Double Tab String should return FALSE",StringUtil.isNonWhiteSpace("\t\t")); 
        Assert.assertFalse("isNonWhiteSpace: Double NewLine String should return FALSE",StringUtil.isNonWhiteSpace("\n\n"));
        Assert.assertFalse("isNonWhiteSpace: Double NewLine String should return FALSE",StringUtil.isNonWhiteSpace("\t \n \n "));
        Assert.assertTrue("isNonWhiteSpace: Single char should return TRUE",StringUtil.isNonWhiteSpace("a"));
        Assert.assertTrue("isNonWhiteSpace: Spaced Single char should return TRUE",StringUtil.isNonWhiteSpace(" a"));
        Assert.assertTrue("isNonWhiteSpace: Spaced Single char should return TRUE",StringUtil.isNonWhiteSpace("a "));
               
    }
    
    @Test
    public void testStringUtil_Compare()
    {   
        doStringUtilCompare("aap","noot"); 
        doStringUtilCompare("noot","aap");
        doStringUtilCompare("aap","aap"); 
        doStringUtilCompare("",""); 
        doStringUtilCompare("","aap"); 
        doStringUtilCompare("aap","");
        doStringUtilCompare(null,""); 
        doStringUtilCompare("",null); 
        doStringUtilCompare(null,null); 
        doStringUtilCompare(null,"aap"); 
        doStringUtilCompare("aap",null);
        //
        doStringUtilCompare("aap","Aap"); 
        doStringUtilCompare("noot","nooT");
        doStringUtilCompare("Aap","aap"); 
        doStringUtilCompare("nooT","noot");
    }
    
    public void doStringUtilCompare(String s1,String s2)
    {
        int v1;
        int vign1; 
        
        // =================================
        // value defined by this Unit Test!
        // =================================
        
        if (s1==null) 
        {
            if (s2==null)
            {
                v1=0;
                vign1=0; 
            }
            else
            {
                v1=-1;  // NULL < NOT NULL
                vign1=-1;
            }
        }
        else
        {
            if (s2==null)
            {
                v1=1;  // NOT NULL > NULL
                vign1=1; 
            }
            else
            {
                v1=s1.compareTo(s2); 
                vign1=s1.compareToIgnoreCase(s2); 
            }
        }
        // =================================
        // value defined by this Unit Test!
        // =================================
        
        int v2=StringUtil.compare(s1,s2); 
        Assert.assertEquals("StringUtil.compare('"+s1+"','"+s2+"') must be equal to String.compare()",v1,v2);
        int vign2=StringUtil.compare(s1,s2,true); 
        Assert.assertEquals("StringUtil.compare('"+s1+"','"+s2+"') must be equal to String.compare()",vign1,vign2);
               
    }
}