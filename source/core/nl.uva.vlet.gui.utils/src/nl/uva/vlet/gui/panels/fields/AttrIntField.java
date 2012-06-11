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
 * $Id: AttrIntField.java,v 1.2 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */
// source: 

package nl.uva.vlet.gui.panels.fields;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.gui.util.Messages;

public class AttrIntField extends AttrParameterField
{
    private static final long serialVersionUID = 7696454286584865802L;

    public AttrIntField()
    {
        super("<AttrIntField>"); // dummy jigloo object
        init();
    }

    public AttrIntField(String name, String value)
    {
        super();
        this.setName(name);
        this.setText(value);
        init();
    }

    public AttrIntField(String name, int dummyValue)
    {
        super();
        this.setName(name);
        this.setText("" + dummyValue);
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
                return ((AttrIntField) input).isEditValid();
            }
        });
    }

    // Set text without checking !
    protected void setActualText(String txt)
    {
        super.setText(txt);
    }

    public boolean isInteger(String txt)
    {
        try
        {
            Integer.parseInt(txt);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    protected boolean isEditValid()
    {
        if (isInteger(getText()))
            return true;

        int val = JOptionPane.showConfirmDialog(this.getRootPane(), Messages.M_value_is_not_valid_integer,
                "Invalid Integer", JOptionPane.OK_CANCEL_OPTION);
        if (val == JOptionPane.CANCEL_OPTION)
        {
            this.setActualText("0");
            return true; // reset
        }
        else
        {
            return false; // try again
        }
    }

    public VAttributeType getVAttributeType()
    {
        return VAttributeType.INT;
    }

    public void setText(String txt)
    {
        try
        {
            int i = Integer.parseInt(txt);
            setValue(i);
        }
        catch (NumberFormatException e)
        {
            setActualText("");
        }
    }

    public void setValue(int val)
    {
        setActualText("" + val);
    }

    public int getIntValue()
    {
        try
        {
            int i = Integer.parseInt(getText());
            return i;
        }
        catch (NumberFormatException e)
        {
            setActualText("");
        }
        return -1; // N/A
    }

}
