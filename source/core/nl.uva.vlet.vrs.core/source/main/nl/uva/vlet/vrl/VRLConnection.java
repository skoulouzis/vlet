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
 * $Id: VRLConnection.java,v 1.3 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */
// source: 

package nl.uva.vlet.vrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;

/**
 * VRL Connection which support VRLs. It extends URLConnection with the
 * supported protocols from the VRS Registry so that VRL can be used as URLs.
 * <p>
 * By suppling an VRLConnection class, VRLs can be converted to URLs and be used
 * in the default Java Stream Reader which use URL.openConnection();
 * 
 * @author P.T. de Boer
 */
public class VRLConnection extends URLConnection
{
    VNode node = null;

    protected VRLConnection(URL url)
    {
        super(url);
    }

    @Override
    public void connect() throws IOException
    {
        try
        {
            node = VRS.getDefaultVRSContext().openLocation(this.getVRL());
            connected = true;
        }
        catch (VlException e)
        {
            throw convertToIO(e);
        }
    }

    public InputStream getInputStream() throws IOException
    {
        if (this.connected == false)
            connect();

        if (node instanceof VStreamReadable)
        {
            try
            {
                return ((VStreamReadable) node).getInputStream();
            }
            catch (VlException e)
            {
                throw convertToIO(e);
            }
        }
        else if (node instanceof VDir)
        {
            // Directories do not have stream read methods.
            // possibly the 'index.html' file is meant, but
            // here we don't know what the caller wants.
            throw new UnknownServiceException("VRS: Location is a directory:" + node);
        }
        else
        {
            throw new UnknownServiceException("VRS: Location is not streamreadable:" + node);
        }
    }

    public OutputStream getOutputStream() throws IOException
    {
        if (this.connected == false)
            connect();

        if (node instanceof VStreamReadable)
        {
            try
            {
                return ((VStreamWritable) node).getOutputStream();
            }
            catch (VlException e)
            {
                throw convertToIO(e);
            }
        }
        else if (node instanceof VDir)
        {
            // Directories do not have stream read methods.
            // possibly the 'index.html' file is meant, but
            // here we don't know what the caller wants.
            throw new UnknownServiceException("VRS: Location is a directory:" + node);
        }
        else
        {
            throw new UnknownServiceException("VRS: location is not streamwritable:" + node);
        }
    }

    private IOException convertToIO(VlException e)
    {
        return new IOException(e.getName() + "\n" + e.getMessage());
    }

    public VRL getVRL() throws VlException
    {
        return new VRL(this.getURL());
    }
}
