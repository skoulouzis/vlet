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
 * $Id: FontComboBoxRenderer.java,v 1.3 2011-04-18 12:27:16 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:16 $
 */ 
// source: 

package nl.uva.vlet.gui.font;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import nl.uva.vlet.gui.GuiSettings;

/**
 * Implementation of FontComboBoxRenderer.
 * Renders the text in the ComboBox with the font name specified. 
 * Is special component in the FontToolBar.

 */
public class FontComboBoxRenderer  extends JLabel implements ListCellRenderer
{
    private static final long serialVersionUID = -2462866413990104352L;
    //===
    boolean antiAliasing=true;
    private FontToolBar fontToolBar; 

    public FontComboBoxRenderer(FontToolBar bar) 
    {
        this.fontToolBar=bar; 
        
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
    }
        
   public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            if (isSelected) 
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else 
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            FontInfo info=fontToolBar.getFontInfo();
            setFont(new Font((String)value,info.getFontStyle(),14)); 
            setText((String)value);
            GuiSettings.setAntiAliasing(this,info.getAntiAliasing()); 
           
            return this;
        }
    
}
