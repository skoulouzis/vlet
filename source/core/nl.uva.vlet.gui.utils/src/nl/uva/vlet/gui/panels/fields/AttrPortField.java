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
 * $Id: AttrPortField.java,v 1.2 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */
// source: 

package nl.uva.vlet.gui.panels.fields;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import nl.uva.vlet.gui.util.Messages;

public class AttrPortField extends AttrIntField implements FocusListener
{
    private static final long serialVersionUID = 7696454286584865802L;

    public AttrPortField()
    {
        super();
        super.setText("<AttrPortField>"); // dummy
        // Make Sure I AM the first listener
        this.addFocusListener(this);
        init();
    }

    public AttrPortField(String name, int i)
    {
        super(name, "" + i);
        init();
    }

    public AttrPortField(String name, String value)
    {
        super(name, 0);
        setText(value);
        init();
    }

    protected void init()
    {
        setInputVerifier(new InputVerifier()
        {
            public boolean verify(JComponent input)
            {
                if (!(input instanceof AttrIntField))
                    return true; // give up focus
                return ((AttrPortField) input).isEditValid();
            }
        });
    }

    protected boolean isEditValid()
    {
        String txt = getText();

        if (isInteger(txt) == false)
        {
            int val = JOptionPane.showConfirmDialog(this.getRootPane(),
                    String.format(Messages.M_value_is_not_valid_integer, txt), "Invalid Integer",
                    JOptionPane.OK_CANCEL_OPTION);
            if (val == JOptionPane.CANCEL_OPTION)
            {
                this.setText("0");
                return true; // reset
            }
            else
            {
                return false; // try again
            }
        }

        int i = getIntValue();

        if ((i < -1) || (i > 65535))
        {
            String text[] = new String[1];
            text[0] = "Port number must be between 0 and 65535";

            // int
            // val=UIGlobal.getMasterUI().askInput("Invalid Port",text,JOptionPane.OK_CANCEL_OPTION);
            int val = JOptionPane.showConfirmDialog(this.getRootPane(),
                    String.format(Messages.M_portvalue_is_not_valid_range, i), "Invalid port",
                    JOptionPane.OK_CANCEL_OPTION);

            if (val == JOptionPane.CANCEL_OPTION)
            {
                setActualText("0");
                return true; // reset
            }
            else
            {
                return false; // try again
            }
        }

        return true;

    }

    public void focusGained(FocusEvent e)
    {
        String txt = this.getText();
        // clear !
        if (isEditable())
            if (isInteger(txt) == false)
                super.setText("");
    }

    public void focusLost(FocusEvent e)
    {

    }
}
