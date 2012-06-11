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
 * $Id: WindowRegistry.java,v 1.3 2011-04-18 12:27:14 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:14 $
 */ 
// source: 

package nl.uva.vlet.gui;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;

import nl.uva.vlet.Global;

/**
 * Window registry for Platform opened windows. 
 */
public class WindowRegistry
{
	private Vector<Window> windows=new Vector<Window>();
	private WindowListener windowListener;
	
	private void initWindowsListener()
	{
	    this.windowListener=new WindowListener()
    	{
    		public void windowActivated(WindowEvent arg0) 
    		{
    		}
    
    		public void windowClosed(WindowEvent arg0) 
    		{
    		    Object win = arg0.getSource(); 
    		    Global.debugPrintf(WindowRegistry.class,"Window closed:%s\n",win);
    			windows.remove(win);
    		}
    
    		public void windowClosing(WindowEvent arg0) 
    		{
    		}
    
    		public void windowDeactivated(WindowEvent arg0) 
    		{
    		}
    
    		public void windowDeiconified(WindowEvent arg0) 
    		{
    		}
    
    		public void windowIconified(WindowEvent arg0) 
    		{
    		}
    
    		public void windowOpened(WindowEvent arg0) 
    		{
    		}
    	};
	}

	public WindowRegistry()
	{
	    init();
	}
	
	protected void init()
	{
	    this.initWindowsListener();
	}
	
	public void register(JDialog dialog)
	{
		windows.add(dialog);
		Global.debugPrintf(WindowRegistry.class,"Registered Dialog:%s\n",dialog);
		dialog.addWindowListener(windowListener);
	}

	public void register(JFrame browser) 
	{
		windows.add(browser);
		Global.debugPrintf(WindowRegistry.class,"Registered JFrame:%s\n",browser);
		browser.addWindowListener(windowListener);
	}
	 
	public void showAll()
	{
		for (Window win:windows)
		{
			if (win instanceof JFrame) 
				((JFrame)win).setState(JFrame.NORMAL);
			
			if (win instanceof JDialog)
			{
				//((JDialog)win).set.setState(JDialog.NORMAL);
			}
			
			win.setVisible(true); 
			win.toFront(); 
			//win.requestFocus(); 
		}
	}
}
