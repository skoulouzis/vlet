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
 * $Id: ProxyExample.java,v 1.10 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vfs;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nl.uva.vlet.Global;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * GridProxy example
 *  
 * @author Piter T. de Boer
 */

public class ProxyExample 
{

    public static void main(String args[]) 
    {
        String vo="pvier";
        boolean enableVO=true; 
        // Get OS default "tmp" dir: work for both linux and windows: 
        String proxyFile=Global.getDefaultTempDir().getPath()+"/myProxy";

        System.out.println("Will create grid proxy at:"+proxyFile); 

        try
        {
            // Create custom context (or use VRSContext.getDefault())  
            VRSContext context=new VRSContext();

            // VRSContext settings ! 
            context.setProperty("grid.proxy.lifetime","4"); 
            context.setProperty("grid.proxy.location",proxyFile); 
            // Default to $HOME/.globus 
            context.setProperty("grid.certificate.location",Global.getUserHome()+"/myglobus"); 
            context.setProperty("grid.proxy.voName",vo);


            boolean useDefaultProxy=true;
            GridProxy proxy;

            if (useDefaultProxy)
            {
                // get/create proxy using VRSContext settings ! (proxy might be invalid) 
                proxy=context.getGridProxy();
            }  
            else
            {
                // or: 
                proxy=GridProxy.loadFrom(context,proxyFile); 
                context.setGridProxy(proxy); 
            }

            if (proxy.isValid()==false)
            {
                proxy.setEnableVOMS(enableVO); 
                proxy.setDefaultVOName(vo); 

                // throw new Exception("Invalid Grid Proxy, please create first"); 
                String pwd=askPassphrase("Please enter passphrase.");
                System.out.println("--- Creating proxy ---");
                proxy.createWithPassword(pwd);

                if (proxy.isValid()==false)
                    throw new Exception("Created Proxy is not Valid!"); 
            }

            System.out.println("--- Valid Grid Proxy ---");
            System.out.println(" - proxy filename     ="+proxy.getProxyFilename());
            System.out.println(" - proxy timeleft     ="+proxy.getTimeLeftString()); 
            System.out.println(" - proxy VOMS enabled ="+proxy.getEnableVOMS());  
            System.out.println(" - proxy VO           ="+proxy.getVOName()); 

        }

        catch (Exception e)
        {
            System.err.println("*** Error:"+e); 
            e.printStackTrace(); 
        }
        // Cleanup:
        VRS.exit(); 
    }


    public static String askPassphrase(String message)
    {
        JTextField passwordField = new JPasswordField(20);
        Object[] inputFields =  {message, passwordField};

        int result=JOptionPane.showConfirmDialog(null, 
                inputFields, 
                "Authentication Required",
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION)
        {
            return passwordField.getText(); 
        }

        return null;  
    }

}
