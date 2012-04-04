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
 * $Id: testGlobal.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import nl.uva.vlet.Global;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrs.VRSContext;

public class testGlobal
{

    private static VRSContext vrsContext;

    public static void main(String args[])
    {
        // this shoud not be neccesary for a normal application,
        // since the first class reference to Global should
        // call the initialiser.

        Global.init();
        vrsContext = VRSContext.getDefault();

        Global.infoPrintln("testContext", "Installation dir =" + Global.getInstallBaseDir());
        Global.infoPrintln("testContext", "Arch type        =" + Global.getArch());
        Global.infoPrintln("testContext", "OS name          =" + Global.getOSName());
        Global.infoPrintln("testContext", "OS version       =" + Global.getOSVersion());

        Global.infoPrintln("testContext", "isLinux          =" + Global.isLinux());
        Global.infoPrintln("testContext", "isWindows        =" + Global.isWindows());

        testProxy();

    }

    public static void testProxy()
    {
        GridProxy proxy = vrsContext.getGridProxy();

        Message("Proxy is valid   ='" + proxy.isValid() + "'");
        Message("Certificates loc ='" + proxy.getUserCertificateDirectory() + "'");
        Message("Proxy filename   ='" + proxy.getProxyFilename() + "'");
        Message("User Keycert     ='" + proxy.getUserKeyFilename() + "'");

    }

    public static void Message(String msg)
    {
        Global.infoPrintln("testContext", msg);
    }

}
