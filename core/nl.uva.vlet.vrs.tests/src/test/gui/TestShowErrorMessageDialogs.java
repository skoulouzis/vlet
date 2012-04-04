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
 * $Id: TestShowErrorMessageDialogs.java,v 1.2 2011-05-02 13:28:46 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:46 $
 */ 
// source: 

package test.gui;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.dialog.ExceptionForm;

public class TestShowErrorMessageDialogs
{
    public static void main(String args[])
    {
        testException1();
        testMessage1(); 
    }

    public static void testException1()
    {
        
        VlException e1=new VlException("Original Exception","This is the original Exception");
        VlException e2=new VlException("Nested Exception","This is the longer Nested Exception were the message text, if to big to fit into one line"+
            "will be wrapped around. The dailog should also be big enough to show all text. Newline:\n This is a new line.",e1);
    
        ExceptionForm.show(null,e2,true);
        
    }
    
    public static void testMessage1()
    {
        UIGlobal.showMessage("Test Message","Short informative message.\n"); 
    }
    
    
}
