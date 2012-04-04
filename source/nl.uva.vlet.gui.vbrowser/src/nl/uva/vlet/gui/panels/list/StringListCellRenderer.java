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
 * $Id: StringListCellRenderer.java,v 1.3 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.list;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import nl.uva.vlet.gui.font.FontInfo;
import nl.uva.vlet.gui.font.FontUtil;

public class StringListCellRenderer extends DefaultListCellRenderer
{
    private static final long serialVersionUID = -2683180719420573202L;
    
    private Font defaultFont;
    private Font newFont;
    Color foreground; 
    Color background; 
    Color newColor;
    private Color removedColor; 
    
    StringListCellRenderer()
    {
        defaultFont = this.getFont();  
        foreground=this.getForeground(); 
        background=this.getBackground();
        
        FontInfo fontInfo=FontUtil.createFontInfo(defaultFont);
        fontInfo.setBold(false);        
        fontInfo.setItalic(true); 
        newFont=fontInfo.createFont();
        
        fontInfo.setBold(true);
        fontInfo.setItalic(false); 
        defaultFont=fontInfo.createFont();
        
        this.newColor=new Color(foreground.getRed(),240,foreground.getBlue());  
        this.removedColor=Color.RED; 
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus)
    {
        // thiz component should be *this*
        Component thiz=super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus); 
        
        StatusStringListField stringList=null;
        boolean isNew=false; 
        boolean isRemoved=false; 
        
        String strValue=value.toString(); // all values in StringList are Strings 
        
        if (list instanceof StatusStringListField)
        {
            stringList=(StatusStringListField)list;
            isNew=stringList.isNew(strValue);
            isRemoved=stringList.isRemoved(strValue); 
            //this.setBackground(Color.RED);
        }
        
        this.setText(value.toString());
        
        if (isNew==true)
        {
            this.setFont(newFont);
            this.setForeground(newColor);
        }
        else if (isRemoved==true)
        {
            this.setFont(defaultFont); 
            this.setForeground(removedColor); 
        }
        else
        {
            this.setFont(defaultFont);
            this.setForeground(foreground);
        }
            
        return this;
    }

}
