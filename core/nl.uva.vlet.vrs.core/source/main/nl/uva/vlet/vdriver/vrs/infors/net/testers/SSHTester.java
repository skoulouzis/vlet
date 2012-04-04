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
 * $Id: SSHTester.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net.testers;

import nl.uva.vlet.vdriver.vrs.infors.net.ProtocolTester;

/**
 * SSH tester. 
 */
public class SSHTester extends ProtocolTester
{

    public SSHTester(String scheme)
    {
        super("SSHTester",false); // initial socket is NOT SSL!
        this.setScheme(scheme); 
    }
    
    protected byte[] getReponseChallenge()
    {
        // Isnot correct. 
        return "HELO SSH\n".getBytes();  
    }
    
    // Check HTTP/HTML response: any is ok. 
    protected boolean checkResponse(byte[] bytes)
    {
        try
        {
            String string=new String(bytes); 
        
            if (string.contains("OpenSSH"))
                return true;
                    
            if (string.contains("SSH"))
                return true;
        
            return false;
        }
        catch (Throwable t)
        {
            setException(t); 
            return false; 
        }  
    }
 
    
}
