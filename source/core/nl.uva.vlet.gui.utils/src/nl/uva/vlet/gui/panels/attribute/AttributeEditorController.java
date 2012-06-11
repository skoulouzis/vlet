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
 * $Id: AttributeEditorController.java,v 1.2 2011-04-18 12:27:13 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:13 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.attribute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;

public class AttributeEditorController implements ActionListener, WindowListener
{
    JFrame standAloneFrame = null;

    private AttributeEditorForm attrEditorDialog = null;
    
    /** Whether the attribute list may be extended */
    
    boolean extendable=true; 
    
    public boolean isOk=true;
        
    /** optionally save new nl.uva.vlet.gui.utils.proxy */

    public AttributeEditorController(AttributeEditorForm srbDialog) 
    {
        this.attrEditorDialog=srbDialog;    
        //      keep original
    }
   
    public void update()
    {
        // get attribute from attribute panel and update attribute object 
        VAttribute attrs[]=attrEditorDialog.infoPanel.getAttributes();
        
        if (attrs==null) 
        {
            Global.debugPrintln(this,"AttributeEditorController: null Attributes");
            return; 
        }
        for (int i=0; i <attrs.length;i++)
        {
            Global.debugPrintln(this, "Attr["+i+"]"+attrs[i]);
        }
        
        // srbInfo.setAttributes(attrs); 
    }

    public void actionPerformed(ActionEvent e)
    {
        {
            if (e.getSource() == this.attrEditorDialog.okButton)
            {
                // store but do not save 
                update();
                
                isOk=true;
                Exit();
 
            }
            else if (e.getSource() == this.attrEditorDialog.cancelButton)
            {
                isOk=false; 
                Exit();
            } 
            else if (e.getSource() == this.attrEditorDialog.resetButton)
            {
                attrEditorDialog.setAttributes(this.attrEditorDialog.originalAttributes);
            }
        }
    }

    public synchronized void Exit()
    {
        attrEditorDialog.Exit(); 
        
        // need to dispose of standalone frame also. 

        if (standAloneFrame != null)
            standAloneFrame.dispose();
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        Exit();
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

}
