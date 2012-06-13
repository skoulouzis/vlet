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
 * $Id: ResourceTreeCellRenderer.java,v 1.6 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import nl.uva.vlet.gui.view.ViewNode;

//import sun.security.krb5.internal.i;

public class ResourceTreeCellRenderer extends DefaultTreeCellRenderer
{
	public static Color COLOR_LIGHT_BLUE=new Color(128,128,255); 
	
    /** */
    private static final long serialVersionUID = -2947025684699355279L;
	//private JTree tree;

    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) 
    {
        // let DefaultTreeCellRender do the main work 
    	Component c=super.getTreeCellRendererComponent(
            tree, value, selected,
            expanded, leaf, row,
            hasFocus);

    	// ===
    	// Assert: Component 'c' should be equal to >>>this<<<
    	// ===
    	
        ResourceTreeNode node = (ResourceTreeNode)value;
        ViewNode item=node.getViewItem();
        this.setIcon(item.getDefaultIcon());
        this.setEnabled(item.isBusy()==false);
       
        // Must create duplicate for single imageobserver:
        // animated gifs do not work for scaled images. Todo scaled animated gifs! 
        // skip: 
        //icon=IconRenderer.duplicate(icon); 
        //icon.setImageObserver(new CellAnimator(tree, path));
       
       
		// if (hasFocus) // focus already handled in super method  
		//    this.setText("<html><u>"+node.getName()+"</u></html");
		// else
		//    this.setText(node.getName());
		// 
        
        // Add mouse over animation: 
        int mouseOverRow=((ResourceTree)tree).getMouseOverRow(); 
       
        if(row == mouseOverRow)
        { 
        	// use HTML make up
           //setBackgroundNonSelectionColor(Color.YELLOW);
    	   this.setText("<html><u>"+node.getName()+"</u></html");
        }
        else
        {
    	   //setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
    	   this.setText(node.getName());
        }
        
        return this;
    }
    
    public boolean imageUpdate2(Image img, int infoflags,
            int x, int y, int w, int h) 
    {
    	//Global.errorPrintf(this,"Received imageUpdate:%s\n",infoflags); 
    	
    	return super.imageUpdate(img, infoflags, x, y, w, h);
    	
    }
    

    
}
