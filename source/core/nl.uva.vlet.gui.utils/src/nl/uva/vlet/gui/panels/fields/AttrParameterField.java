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
 * $Id: AttrParameterField.java,v 1.2 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */
// source: 

package nl.uva.vlet.gui.panels.fields;

import javax.swing.JTextField;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;

/**
 * An Attribute Parameter Field is an managed Field for an VAttribute
 * 
 * @author ptdeboer
 * 
 */
public class AttrParameterField extends JTextField implements IAttributeField
{
    private static final long serialVersionUID = -7390955302454785863L;

    // needed by jilgloo
    public AttrParameterField()
    {
        super("<AttrParameterField>");
    }

    /** Default is String Parameter */
    public AttrParameterField(String value)
    {
        super(value);
    }

    public AttrParameterField(String name, String value)
    {
        super(value);
        this.setName(name);
    }

    public String getName()
    {
        return super.getName();
    }

    public String getValue()
    {
        return super.getText();
    }

    public VAttributeType getVAttributeType()
    {
        return VAttributeType.STRING;
    }

    public void updateFrom(VAttribute attr)
    {
        setText(attr.getStringValue());
    }

    public void setEditable(boolean editable)
    {
        super.setEditable(editable);
    }

}
