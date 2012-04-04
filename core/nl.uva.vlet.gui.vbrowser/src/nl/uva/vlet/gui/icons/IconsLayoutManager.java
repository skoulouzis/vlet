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
 * $Id: IconsLayoutManager.java,v 1.3 2011-04-18 12:27:25 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:25 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import javax.swing.JViewport;

/** 
 * Simple icons layout manager. 
 * Starts icons in upper right and adds icons either 
 * in top down (fitting in window) and expand left to right order
 * (Horizotal Orientation).
 * Or left right (fitting in window) and expanding top - down 
 * in vertical direction (Vertical Orientation).
 *  
 * @author Piter T. de Boer.
 */
public class IconsLayoutManager implements LayoutManager
{
	IconViewType iconView = IconViewType.ICONS48;
	
	public IconsLayoutManager(IconViewType iconView)
	{
		this.iconView=iconView; 
	}

	public void setIconViewType(IconViewType type)
	{
		this.iconView=type; 
	}

	public IconViewType getIconViewType()
	{
		return this.iconView; 
	}

	public void addLayoutComponent(String name, Component comp)
	{
		// huh ? 
	}

	public void layoutContainer(Container parent)
	{
		alignIcons(parent);
	}

	public Dimension minimumLayoutSize(Container parent)
	{
		return alignIcons(parent); 
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		return alignIcons(parent);
	}
	
	public void removeLayoutComponent(Component comp)
	{
		
	}
	
	/**
     * Custom Layout method.
     *  
     * Important: Is executed during (SWing) object lock.
     * Do not trigger new resize events to prevent an endless aligniIcons loop !  
     */

    protected Dimension alignIcons(Container container)
    {
        int row = 0;
        int column = 0;
        int xpos = iconView.cell_hgap; // start with offset 
        int ypos = iconView.cell_vgap; // start with offset 
        int maxy = 0;
        int maxx=0; 
        
        
        Dimension targetSize=getTargetSize(container);  

        // get max width and max height 
        int cellMaxWidth=0; 
        int cellMaxHeight=0;
        
        Component[] childs = container.getComponents();
        
        // BODY 
        if ((childs==null) || (childs.length==0)) 
        {
            return new Dimension(0,0); 
        }
        
        // PASS I) Scan buttons for grid width and height 
        for (Component comp : childs)
        {
            if (comp==null)
                continue;
            Dimension size = comp.getPreferredSize(); 
            
            if (size.width>cellMaxWidth)
               cellMaxWidth=comp.getWidth(); 
            if (size.height>cellMaxHeight)
                cellMaxHeight=comp.getHeight();
            
        }
        
        
        // scan button for grid width and height 
        for (Component comp : childs)
        {
            if (comp==null)
                continue;
            
            // I) place IconLabel
            Point pos=null;
            
            // set position and add to panel
            if (iconView.labelOrientation==IconViewType.Orientation.VERTICAL)
                 pos = new Point(xpos+cellMaxWidth/2-comp.getSize().width/2, ypos);
            else
                 pos = new Point(xpos, ypos); // align to left
            
            comp.setLocation(pos);// set new location since GridLayout Somehow doesn't work ! 

            // use GridLayoutManager: STIL DOESN'T WORK 
            //c.gridx=column; 
            //c.gridy=row; 
            //this.add(bicon,c);
            
            // II) Current layout stats  
            int bottom = pos.y + comp.getSize().height;

        	if (bottom > maxy)
        		maxy = bottom; // lowest y coordinate of this row
        	
        	int right = pos.x + comp.getSize().width;

        	if (right > maxx)
        		maxx = right; // rightmost x coordinate of this column

        	//III) new position 
            if (this.iconView.iconPlacement==IconViewType.Orientation.HORIZONTAL)
            {
            	xpos+=cellMaxWidth+iconView.cell_hgap;
            	column++; //next column

            	// check next position
            	if (xpos+cellMaxWidth >= targetSize.width)
            	{
            		// reset to xpos to left margin, increase new ypos. 
            		xpos = iconView.cell_hgap;;// reset to defaul offset
            		ypos = maxy + iconView.cell_vgap; // next row
            		//ypos += celly + browser_icon_gap_width; // next row
            		column = 0;
            		row++;
            	}
            }
            else
            {
            	ypos+=cellMaxHeight+iconView.cell_vgap;
            	row++;
            	//  check next position
            	if (ypos+cellMaxHeight  > targetSize.height)
            	{
            		// reset to ypos to top margin, increase new xpos. 
            		ypos = iconView.cell_vgap;;// reset to defaul offset
            		xpos = maxx + iconView.cell_hgap; // next row
            		//ypos += celly + browser_icon_gap_width; // next row
            		row = 0;
            		column++;
            	}
            }
            
            if (comp.isVisible()==false)
            	comp.setVisible(true);
            
            // parent may setSize of child! 
            comp.setSize(comp.getPreferredSize()); 

            
        }// *** END for node:nodeLocations
       
        
        // IV) update sizes 
        // IMPORTANT: alignIcons is called during doLayout, so 
        // NO setSize may be called, since this retriggers a doLayout ! 
        // ScrollPane Update: 
        //  when setting the preferredSize, the ParentScrollPane 
        //  will be informed about the new size
        
        //update with real size
        Dimension size = new Dimension(maxx,maxy);
        
        //container.setSize(lastSize); 
        container.setPreferredSize(size);
        return size; 
    }

	private Dimension getTargetSize(Container container)
	{
        Dimension targetSize=null;
     
        // get parent: 
        Container parent=container.getParent(); 
        
        // Panel is embedded in a ScrollPane or similar widget. 
		if (parent instanceof JViewport)
        {
			// get VISIBLE part of ViewPort as target size. 
		    //Container gran = container.getParent(); 
		    //if (gran instanceof JScrollPane)
        	JViewport vport=(JViewport)parent; 
            targetSize=vport.getExtentSize();
    
            //System.err.println("targetSize="+targetSize);
            //System.err.println("viewOffset="+viewOffset);
        }
        else 
        {
           targetSize = container.getSize();
        }
		
		return targetSize; 
	}

}
