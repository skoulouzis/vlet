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
 * $Id: testAuthenticationDialog.java,v 1.3 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */ 
// source: 

package tests;

import java.util.Properties;

import nl.uva.vlet.gui.dialog.AuthenticationDialog;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;

public class testAuthenticationDialog
{

    public static void main(String[] args)
    {
        ServerInfo info = AuthenticationDialog.askAuthentication(
                "Testing authentication Dailog\n.Authentication is Needed\n " +
                "This is a long sentence to tests the autowrap function. 1.2.3.4.5.6.7.8.9.10. ",
                
                null);

        if (info == null)
        {
            System.out.println("*** Action Cancelled ***");
        }
        else
        {
            System.out.println("User      ="
                    + info.getUsername()); 
            System.out.println("passwd    ="
                    + info.getPassword()); 
            System.out.println("passphrase="
                    + info.getPassphrase()); 
        }
    }

}
