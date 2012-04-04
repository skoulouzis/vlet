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
 * $Id: LabelIcon.java,v 1.9 2011-06-10 10:17:59 ptdeboer Exp $  
 * $Date: 2011-06-10 10:17:59 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.FontMetrics;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.actions.KeyMappings;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.font.FontInfo;
import nl.uva.vlet.gui.icons.IconViewType.Orientation;
import nl.uva.vlet.gui.view.VComponent;
import nl.uva.vlet.gui.view.VContainer;
import nl.uva.vlet.gui.view.ViewNode;
import nl.uva.vlet.vrl.VRL;

/**
 * Extended class of the JLabel 
 * Renders an Icon and a Label. 
 * 
 * @author P.T. de Boer
 */
public class LabelIcon extends JLabel implements VComponent, FocusListener
{
    private static final long serialVersionUID = 6350675255301863886L;
    private MasterBrowser browserController;
    private boolean isSelected;
    //private String basename;
    //private ImageIcon normalIcon;
    //private ImageIcon selectedIcon;
    private JLabel textLabel;
    private JLabel iconLabel;
  
    private FontInfo fontInfo;
    private boolean highlighted;
    private int max_icon_width=80;
    private int max_icon_height=80; 
    private IconViewType iconView;
    private IconsPanel iconsPanel;
	private ViewNode iconItem;

    //private ProxyNode proxyNode; 
    
    /** One To Handle Them All */ 
       
    public LabelIcon(MasterBrowser browser, IconsPanel panel,ViewNode pnode, int default_max_iconlabel_width)
    {
        this.browserController=browser;
        this.iconView=panel.getIconViewType();
        this.iconsPanel=panel; 
        
        if (iconView.labelOrientation==Orientation.VERTICAL)
        {
        	// square icon fields: A little bit higher then wide.  
            max_icon_width=iconView.iconSize*2;
            max_icon_height=(int)(iconView.iconSize*2.5); 
        }
        else
        {
        	// alow wider icons but limit height to actual icon height
            max_icon_width=25*iconView.iconSize; // this is the maximum column width 
            max_icon_height=iconView.iconSize; 
        }
        
        init(pnode);
        initDND(this);
    }
    
    
    protected static void initDND(LabelIcon icon)
    {
    	// One For All: Transfer Handler: 
        icon.setTransferHandler(VTransferHandler.getDefault());
        
        // reuse draglistener from iconsPanel:
        DragSource dragSource=DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(
                icon, DnDConstants.ACTION_COPY_OR_MOVE, icon.iconsPanel.dragListener );
       
        // Specify DROP target: 
        icon.setDropTarget(new NodeDropTarget(icon));
        // Have to set Keymapping to my component 
        KeyMappings.addCopyPasteKeymappings(icon); 
    }

    private void init(ViewNode item2) 
    {
        this.setOpaque(false);
        // use HTML for auto LAYOUT ! 
        this.setText(""); 
      
       // this.setLayout(layout=new BorderLayout());
        this.setSize(max_icon_width,max_icon_height); // this.getPreferredSize());
     
        iconLabel=this; 
        textLabel=this;
        
        fontInfo=FontInfo.getFontInfo(FontInfo.FONT_ICON_LABEL);
        textLabel.setForeground(this.fontInfo.getForeground());
        this.setFont(fontInfo.createFont());
        // attributes:
        
        if (this.iconView.labelOrientation==Orientation.VERTICAL)
        {
             if (iconView.showIcon)
                 this.setIconTextGap(8);
             else
                 this.setIconTextGap(4);
             
            this.setVerticalAlignment(JLabel.TOP);
            this.setHorizontalAlignment(JLabel.CENTER);

            this.setVerticalTextPosition(JLabel.BOTTOM);
            this.setHorizontalTextPosition(JLabel.CENTER);
         }
        else
        { 
            this.setIconTextGap(4); 
            this.setVerticalAlignment(JLabel.CENTER);
            this.setHorizontalAlignment(JLabel.LEFT);

            this.setVerticalTextPosition(JLabel.CENTER);
            this.setHorizontalTextPosition(JLabel.RIGHT);
        }

        updateIconItem(item2);
        
        this.setVisible(true);
        this.setFocusable(true); 
        this.addFocusListener(this); 
    }
    
    public void updateIconItem(ViewNode item)
    {
        this.iconItem=item; 
        
        this.iconItem=item; //new IconItem(pnode,iconView.iconSize); 
        setLabelName(iconItem.getName(),false);
        
        if (iconView.showIcon)
        	iconLabel.setIcon(iconItem.getDefaultIcon()); 
        
        // use full VRL for JLabel Component name !  (Not displayed named) 
        this.setName(iconItem.getVRL().toString());
        
        this.setToolTipText(item.getName());
        
    }
    
    public void updateIconURL(String url)
    {
        this.iconItem.updateIconURL(url);
        updateIconItem(iconItem); 
        this.repaint(); 
    }


    public void setIconItem(ViewNode item)
    {
        updateIconItem(item); 
    }

    
	protected void setDefaultIcon(ImageIcon icon)
	{
		this.iconItem.setIcon(ViewNode.DEFAULT_ICON,icon); 
	}
	
	protected void setSelectedIcon(ImageIcon icon)
	{
		this.iconItem.setIcon(ViewNode.SELECTED_ICON,icon); 
	}


	private void setLabelName(String newname, boolean high)
    {
    	if (newname==null)
    		newname=""; 
    	
        this.iconItem.setName(newname); 
        
        this.highlighted=high; 
        
        String htmlText= "<html>";
        
        if (highlighted)
            htmlText+="<u>";
        
        
        // Calculate Font Size 
        FontMetrics fmetric = getFontMetrics(getFont());
        // get 'widest'character
        int charWidth=fmetric.charWidth('w'); 
        //System.err.println("charwidth="+charWidth);
        
        //int lines=0; 
        int len=newname.length();  
        //int width=0; 
        int i=0; 
        
        while(i<len) 
        {
            // html expantion
            int compensate=0; 
            
            String linestr=""; 
            // arg must tweak label size: 
            while ((i<len) && (fmetric.stringWidth(linestr)<(max_icon_width-2*charWidth+compensate))) 
            {
                switch (newname.charAt(i))
                {
                    // filter out special HTML characters: 
                    // only a small set needs to be filtered:
                    case '/':
                    case '!':
                    case '@':
                    case '#': 
                    case '$': 
                    case '%': 
                    case '^':
                    case '&':
                    case '*':
                    case '<':
                    case '>':
                    	// use numerical ASCII value: 
                        String str="&#"+(int)newname.charAt(i)+";"; 
                        linestr+=str; 
                        // compensate for HTML expansion (minus character,plus pixel) 
                        compensate+=(str.length()-1)*charWidth;  
                        break; 
                    default: 
                        linestr+=newname.charAt(i);
                    break;
                }
                
                i++; 
            }
            
            htmlText+=linestr;
            // hard line ?
            if (i<len) 
                htmlText+="<br>";
        }

        if (highlighted)
            htmlText+="</u>"; 
        
        htmlText+="</html>"; 
        
        textLabel.setText(htmlText);
         
  
    }
    

    public VRL getVRL()
    {
        return iconItem.getVRL(); 
    }

    public MasterBrowser getMasterBrowser()
    {
        return this.browserController;
    }
    

    public void setSelected(boolean b)
    {
        this.isSelected=b;
        
        if (b)
        {
            iconLabel.setIcon(iconItem.getSelectedIcon());
            //textLabel.setOpaque(true);
            //prev_label_color=textLabel.getForeground();
            textLabel.setForeground(this.fontInfo.getHighlightedForeground()); 
        }
        else
        {
            iconLabel.setIcon(iconItem.getDefaultIcon());
            //textLabel.setOpaque(false);
            textLabel.setForeground(this.fontInfo.getForeground()); 
        }
        
        this.repaint();
    }
	
	public void animateFocus(boolean hasFocus)
	{
	    setLabelName(iconItem.getName(),hasFocus);
	    repaint();
	}
	
    public void focusGained(FocusEvent e)
	{
        animateFocus(true); 
	}

	public void focusLost(FocusEvent e)
	{
	    animateFocus(false); 
	}

	public void setListener(LabelIconListener listener)
	{
		this.addMouseListener(listener); 
		this.addKeyListener(listener);
	}


	public boolean isSelected()
	{
		return this.isSelected;
	}


	public VContainer getVContainer()
	{
		return this.iconsPanel; 
	}
	
	public String getResourceType()
	{
		return this.iconItem.getResourceType();   
	}
	
	public ResourceRef getResourceRef()
	{
		return this.iconItem.getResourceRef();    
	}

//	private void debug(String msg)
//	{
//	   //Global.errorPrintln("IconsPanel", msg);
//       Global.debugPrintln("IconsPanel", msg);
//	}

	public String getMimeType()
	{
		return this.iconItem.getMimeType();
	}

	public String toString()
	{
		return "{LabelIcon:"+this.getName()+"}";
	}


	public boolean hasLocation(VRL vrl)
	{
		return this.iconItem.getVRL().equals(vrl);
	}


  

}
