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
 * $Id: JDLNode.java,v 1.2 2011-04-18 12:28:50 ptdeboer Exp $  
 * $Date: 2011-04-18 12:28:50 $
 */ 
// source: 

package nl.uva.vlet.vjs.wms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VStreamReadable;

public class JDLNode extends VNode implements VStreamReadable
{
    private String jdl;

    public JDLNode(VRSContext context, VRL vrl, String jdl)
    {
        super(context, vrl);
        this.jdl = jdl;
    }

    @Override
    public boolean exists() throws VlException
    {
        return true;
    }

    @Override
    public String getType()
    {
        return WMSConstants.JDL_TYPE;
    }

    @Override
    public String getMimeType()
    {
        return WMSConstants.GLITE_JDL_MIMETYPE;
    }

    @Override
    public InputStream getInputStream() throws VlException
    {
        try
        {
            // Assume UTF-8 ?
            InputStream sis = new ByteArrayInputStream(jdl.getBytes("UTF-8"));
            return sis;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new VlException("UnsupportedEncoding", e.getMessage(), e);
        }
    }

    public String getText()
    {
        return jdl;
    }

    private void debug(String msg)
    {
        Global.errorPrintln(this, msg);

    }

}
