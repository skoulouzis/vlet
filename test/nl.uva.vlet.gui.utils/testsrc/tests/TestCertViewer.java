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
 * $Id: TestCertViewer.java,v 1.1 2011-05-18 13:03:29 ptdeboer Exp $  
 * $Date: 2011-05-18 13:03:29 $
 */ 
// source: 

package tests;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.viewers.x509viewer.ViewerX509;
import nl.uva.vlet.vrl.VRL;

public class TestCertViewer
{

    public static void main(String[] args)
    {
        try
        {
            VRL vrl=new VRL("file:///home/ptdeboer/certs/test.crt"); 
        
            ViewerX509 tv = new ViewerX509();
            //tv.setViewStandalone(true);
            tv.startAsStandAloneApplication(vrl); 
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
    }

}
