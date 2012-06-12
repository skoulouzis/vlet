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
 * $Id: TestCompareViewer.java,v 1.4 2011-06-07 15:15:07 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:07 $
 */ 
// source: 

package test;

import javax.swing.JFrame;

import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.compareviewer.DirCompareController;
import nl.uva.vlet.gui.compareviewer.DirComparePanel;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;
import nl.uva.vlet.vrl.VRL;

public class TestCompareViewer
{
	public static void main(String args[])
	{
		UIGlobal.init(); 
		ProxyVNodeFactory.initPlatform(); 
		
		JFrame frame=new JFrame();
		DirComparePanel dcPanel = new DirComparePanel(true); 
		
		frame.add(dcPanel); 
		frame.pack(); 
		frame.setVisible(true);
		
		DirCompareController controller=dcPanel.getController(); 
		
		try
		{
			controller.compareLocations(new VRL("file:///home/ptdeboer"),new VRL("file:///home/ptdeboer"));
		}
		catch (VRLSyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
