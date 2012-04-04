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
 * $Id: testAttributeEditor.java,v 1.4 2011-06-07 15:15:07 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:07 $
 */ 
// source: 

package test;


import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.panels.attribute.AttributeEditorForm;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;


public class testAttributeEditor
{
    public static void main(String args[])
    {
        Global.setDebug(true);
        int len=20; 
        
        VAttribute attrs[]=new VAttribute[len];
        
        ServerInfo srbInfo=null;
        try
        {
            srbInfo = new ServerInfo(VRSContext.getDefault(),new VRL("srb://piter.de.boer@srb.grid.sara.nl/home/"));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
        
        VAttribute srbAttrs[]=srbInfo.getAttributes(); 
        
        for (int i=0;i<len;i++)
        {
            if ((i<srbAttrs.length) && (srbAttrs[i]!=null))
            {
                attrs[i]=srbAttrs[i];
            }
            else
            {
                attrs[i]=new VAttribute("Field:"+i,"Value"+i);
                attrs[i].setEditable((i%2)==0);
            }
        }
         
        attrs=AttributeEditorForm.editAttributes("Test AttributeForm",attrs,true); 
        
        System.out.println("--- Dialog Ended ---"); 
        
        int i=0;
        for(VAttribute a:attrs)
        {
            System.out.println("Attrs["+i++ +"]="+a);
        }
        
        
    }
}
