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
 * $Id: SftpAuthenticationExample.java,v 1.4 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vfs;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nl.uva.vlet.Global;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * Example how to do custom SSH authentication.<br> 
 * This example disables the automatic password pop-up methods from the 
 * SFTP implementation and asks the user in a custom dialog before using VFS methods. 
 *   
 * @author Piter T. de Boer
 */
public class SftpAuthenticationExample
{
	public static void main(String args[])
	{
		Global.init();
		// Global.setDebug(true);
		
		try
		{
			// ====================================
			// Configure VRSContext; 
			// ====================================
			VRSContext context = new VRSContext(); 
			
			// block interactive password popups as this is done by a custom dailog.  
			context.getConfigManager().setAllowUserInteraction(false); 
			
			String locStr=askLocation(); 
			if (locStr==null)
				throw new Exception("No valid sftp location given."); 
			
			// Create ServerInfo Object using default context: 
			VRL vrl=new VRL("sftp://"+locStr);
					
			ServerInfo info = context.getServerInfoFor(vrl,true); 
			
			// Set Authentication information 
			info.setUsePasswordAuth(); 
			info.setPassword(askPassword("Password for:"+vrl)); 
			
			// Excplicit store configuartion in the ServerInfo Registry: 
			info.store();  
			
			
			// =================
			// Create VFSCLient
			// =================
			
			VFSClient vfs=new VFSClient(context); 

			// get directory: 
			VDir dir=vfs.getDir(vrl); 
			 
			// should be valid after authentication. 
			System.out.println("Authentication is valid="+info.hasValidAuthentication());  
			 
			VFSNode nodes[]=dir.list(); 
			for (VFSNode node:nodes)
			{
				System.out.println(" - "+node); 
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// cleanup resources and exit; 
		VRS.exit();
	}
	
	public static String askPassword(String message)
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
	
	public static String askLocation()
	{
		JTextField locField = new JTextField(Global.getUsername()+"@remotehost.domain");
		
        Object[] inputFields =  {"Please specify (SSH) user and host name please",
        		locField}; 
        
        int result=JOptionPane.showConfirmDialog(null, 
                inputFields, 
                "Authentication Required",
                JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION)
        {
        	return locField.getText();   
        }
        
        return null;  
	}
}
