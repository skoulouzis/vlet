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
 * $Id: IMasterUI.java,v 1.3 2011-04-18 12:00:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:38 $
 */ 
// source: 

package nl.uva.vlet.vrs.ui;

import nl.uva.vlet.data.StringHolder;

/**
 * The MasterGui interface provides an abstraction  
 * for Resources to interact with.
 * <p> 
 * An MasterGui might be present or not or might by a 'dummy' GUI
 * which returns defaults when a resource asks for user interaction
 * and the application runs in batch mode.    
 * A (Web) Service might provide an user interaction interface which calls back
 * to the user.  
 * @author Piter T. de Boer
 */
public interface IMasterUI
{
    /** Whether user interaction is possible */ 
    public boolean isEnabled();
    
	/** Display message dialog or print to console */ 
	void showMessage(String message);
	
    /**
     * Simple Yes/No prompter 
	 * @param defaultValue value to return is there is no UI present 
	 *        or it is currently disabled. */ 
	boolean askYesNo(String title,String message, boolean defaultValue);

	/**
	 * Simple Yes/No/Cancel prompter. 
	 * Returns JOptionPane.CANCEL_OPTION if no UI present
	 */ 
    int askYesNoCancel(String title,String message);

	/** Ask for password, passphrase or other 'secret' String */ 
	boolean askAuthentication(String message,
	        StringHolder secret); 
	
	/**
	 * Simple formatted Input Dialog. Method is wrapper for JOptionPane ! 
	 * See	JOptionPane.showConfirmDialog() for options.
	 * @return JOptionPane.OK_OPTION if successful. 
	 *         Parameter inputFields can contain modified (Swing) objects.  
	 */ 
	int askInput(String title, Object[] inputFields, int jOpentionPaneOption);
	

}
