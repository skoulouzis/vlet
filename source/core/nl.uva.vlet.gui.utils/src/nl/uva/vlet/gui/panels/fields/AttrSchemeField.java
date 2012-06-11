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
 * $Id: AttrSchemeField.java,v 1.2 2011-04-18 12:27:12 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:12 $
 */
// source: 

package nl.uva.vlet.gui.panels.fields;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.vrs.VRS;

public class AttrSchemeField extends AttrEnumField
{
    private static final long serialVersionUID = 1688960537562990298L;

    public AttrSchemeField()
    {
        super();
        init();
    }

    public AttrSchemeField(String name, String values[])
    {
        super(name, values);
        init();
    }

    protected void init()
    {
        setInputVerifier(new InputVerifier()
        {
            public boolean verify(JComponent input)
            {
                if (!(input instanceof AttrSchemeField))
                    return true; // give up focus
                return ((AttrSchemeField) input).isEditValid();
            }
        });
    }

    protected boolean isEditValid()
    {
        String schemes[] = VRS.getRegistry().getDefaultSchemeNames();
        String scheme = VRS.getRegistry().getDefaultScheme(getValue());

        if (StringList.hasEntry(schemes, scheme) == false)
        {
            boolean keep = UIGlobal.getMasterUI().askYesNo("Not supported scheme",
                    "The scheme: '" + scheme + "' is not recognised. Keep it anyway  ? ", false);

            if (keep)
                return true;
            else
                return false; // try again
        }
        else
            return true;

    }

}
