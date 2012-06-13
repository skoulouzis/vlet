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
 * $Id: GlueObject.java,v 1.7 2011-04-18 12:00:41 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:41 $
 */ 
// source: 

package nl.uva.vlet.util.bdii.info.glue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

/**
 * Generic Glue Object
 * 
 * @author Spiros Koulouzis, Piter T. de Boer
 * 
 */
public class GlueObject
{

    private String type;

    // private NamingEnumeration attributes;
//    private Map<String, Object> glueAttributes;

    private Map<String, Vector<Object>> extGlueAttributes;

    private String Uid;

    public GlueObject(String type, NamingEnumeration<? extends Attribute> attributes) throws NamingException
    {
        this.type = type;
        // this.attributes = attributes;
//        glueAttributes = new HashMap<String, Object>();

        extGlueAttributes = new HashMap<String, Vector<Object>>();

        initGlueObject(attributes);

    }

    private void initGlueObject(NamingEnumeration<? extends Attribute> attributes) throws NamingException
    {
        Vector<Object> glueValues;
        while (attributes.hasMore())
        {
            Attribute attrr = attributes.next();
            String id = attrr.getID();
            Object value = attrr.get();

            NamingEnumeration<?> enumer = attrr.getAll();
            glueValues = new Vector<Object>();
            // debug("All attributes for " + id);
            while (enumer.hasMoreElements())
            {
                //
                Object attr = enumer.next();
                // debug("\t\t\t\t" + attr);
                if (attr != null)
                {
                    glueValues.add(attr);
                }
            }
            if (!glueValues.contains(value))
            {
                glueValues.add(value);
            }

            extGlueAttributes.put(id, glueValues);

            // glueAttributes.put(id, value);
            if (id.endsWith("UniqueID"))
            {
                Uid = (String) value;
            }
            // sa don't really have UIDs. In this case to fix it we add the
            // chunkkey
            else if (id.endsWith("GlueChunkKey"))
            {
                String[] tmp = ((String) value).split("=");
                Uid = tmp[tmp.length - 1];
            }
            // debug(id + ": " + value);
        }
        // debug("----------------------");

    }

//    public Object getAttribute(String glueType)
//    {
//        return glueAttributes.get(glueType);
//    }

    public Vector<Object> getExtAttribute(String glueType)
    {
        return extGlueAttributes.get(glueType);
    }

    public Set<String> getExtGlueTypes()
    {
        return extGlueAttributes.keySet();
    }

    // public Set<String> getGlueTypes()
    // {
    // return glueAttributes.keySet();
    // }

    public String getType()
    {
        return this.type;
    }

    public String getUid()
    {
        return this.Uid;
    }

}
