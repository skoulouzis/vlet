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
 * $Id: SimpelUI.java,v 1.5 2011-04-18 12:00:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:38 $
 */ 
// source: 

package nl.uva.vlet.vrs.ui;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringHolder;


/** 
 * Simple UI Object. 
 */ 
public class SimpelUI implements IMasterUI
{
	private boolean enabled=true; 
	
	public SimpelUI()
	{
		
	}
	
	public void setEnabled(boolean enable)
	{
		this.enabled=enable; 
	}
	
	public boolean isEnabled()
	{
	    return enabled; 
	}
	
	public void showMessage(String message)
	{
		if (enabled==false)
		{
			Global.infoPrintf(this,"UI Message:%s\n",message); 
			return;
		}
		
		JOptionPane.showMessageDialog(null, message);
	}

	public boolean askYesNo(String title,String message,boolean defaultValue)
    {
		if (enabled==false)
			return defaultValue;  
		
        int result = JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, null,JOptionPane.NO_OPTION);
        
        return (result == JOptionPane.YES_OPTION);
	}
	
	/**
	 * Simple Yes,No,Cancel dialog. 
	 * @return JOptionPane.YES_OPTION JOptionPane.NO_OPTION or JOptionPane.CANCEL_OPTION
	 */
	public int askYesNoCancel(String title,String message)
    {
        if (enabled==false)
            return JOptionPane.CANCEL_OPTION; 
        
        int result = JOptionPane.showOptionDialog(null, message, title,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, null, JOptionPane.CANCEL_OPTION);
        
        return result;
    }
	
	public int askInput(String title, Object[] inputFields,int optionPaneOption) 
	{
		if (enabled==false)
			return -1; 
   	 
		return JOptionPane.showConfirmDialog(null, 
				inputFields, 
				title,
				optionPaneOption);
	}
	
	
	// Wrapper for JOptionPane. 
	 public int showOptionDialog(String title, 
			 	Object message, 
		        int optionType, 
		        int messageType,
		        Icon icon, 
		        Object[] options, 
		        Object initialValue)
	 {
		 return JOptionPane.showOptionDialog(null,
				 	message, 
				 	title,
				 	optionType,
				 	messageType,
			        icon,
			        options, 
			        initialValue); 
	 }

    public boolean askAuthentication(String message,
            StringHolder secret)
    {
        // Thanks to Swing's serialization, we can send Swing Components ! 
        JTextField passwordField = new JPasswordField(20);
        Object[] inputFields =  {message, passwordField};
        
        int result=JOptionPane.showConfirmDialog(null, 
                inputFields, 
                "Authentication Required",
                JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION)
        {
            secret.value=passwordField.getText(); 
            return true;
        }
        
        return false; 
    }


    
}
