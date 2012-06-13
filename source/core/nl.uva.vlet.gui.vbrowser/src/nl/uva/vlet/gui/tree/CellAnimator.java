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
 * $Id: CellAnimator.java,v 1.3 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import nl.uva.vlet.Global;

public class CellAnimator implements ImageObserver 
{

	private JTree tree;
	private TreePath path;

	public CellAnimator(JTree tree,TreePath path)
	{
		this.tree=tree;
		this.path=path; 
	}

	public static void animate(JTree tree, TreePath path, ImageIcon icon) 
	{
		if (icon.getImageObserver() instanceof CellAnimator)
		{
			Global.errorPrintf(CellAnimator.class,"CellAnimator already installed:%s\n",path);
			return ; 
		}
		
		icon.setImageObserver(new CellAnimator(tree,path));
	}

	public boolean imageUpdate2(Image img, int infoflags, int x, int y, int width, int height) 
	{
		System.err.println("System update:"+infoflags);
		
		return true; 
	}

   public boolean imageUpdate(Image img, int flags,
	            int x, int y, int w, int h) 
   {
	   System.err.println("System update:"+flags);
	   
	    if ((flags & (FRAMEBITS|ALLBITS)) != 0) 
	   	{
	   		Rectangle rect = tree.getPathBounds(path);
	   		System.err.println("Rect="+rect);
	   		if (rect != null) 
	   		{
	   			// tree.repaint(); 
	   		
	   			tree.repaint(rect);
	   		}
	   	}
	    	
	   	return (flags & (ALLBITS|ABORT)) == 0;
   }
	   
}
