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
 * $Id: AATextArea.java,v 1.4 2011-04-18 12:27:16 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:16 $
 */ 
// source: 

package nl.uva.vlet.gui.font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextArea;

public class AATextArea extends JTextArea 
{
	private boolean isAntiAliased=true;

	public void setAntiAliased(boolean aa)
	{
		this.isAntiAliased=aa;
	}
	
	public void paint(Graphics g)
	{
       Graphics2D g2d = (Graphics2D) g;
       setAA(g2d,isAntiAliased);
       super.paint(g2d); 
	}
	
	public Graphics getComponentGraphics(Graphics g)
	{
		 Graphics2D g2d = (Graphics2D) g;
	     setAA(g2d,isAntiAliased);
	     return g2d;
	}
	
	public static void setAA(Graphics2D g2d, boolean useAA) 
	{
       if (useAA)
       {
           g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                                 RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
           g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                                 RenderingHints.VALUE_RENDER_QUALITY );
	        // if wanted to smooth geometric shapes too
	            // g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	           // RenderingHints.VALUE_ANTIALIAS_ON );
       }
       else
       {
    	   g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                   RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
    	   g2d.setRenderingHint( RenderingHints.KEY_RENDERING,
                   RenderingHints.VALUE_RENDER_DEFAULT);
	   } 	
	}

	
	 
}
