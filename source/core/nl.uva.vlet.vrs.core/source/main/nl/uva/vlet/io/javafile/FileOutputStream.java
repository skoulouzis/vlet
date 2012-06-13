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
 * $Id: FileOutputStream.java,v 1.4 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */
// source: 

package nl.uva.vlet.io.javafile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vrl.VRL;

/**
 * java.io.FileOutputStream compatible class.
 */
public class FileOutputStream extends OutputStream
{

    private VRL location;

    private OutputStream output;

    public FileOutputStream(String name) throws FileNotFoundException
    {
        try
        {
            this.location = File.getStaticVFSClient().resolve(name);
        }
        catch (VRLSyntaxException e)
        {
            throw new FileNotFoundException("Couldn't resolve:" + name + "\nException=" + e);
        }
        open(location);
    }

    // public FileOutputStream(String name, boolean append)
    // throws FileNotFoundException
    // {
    // this(name != null ? new File(name) : null, append);
    // }

    public FileOutputStream(File file) throws FileNotFoundException
    {
        this.location = file.toVRL();
        open(location);
    }

    public FileOutputStream(VRL vrl) throws FileNotFoundException
    {
        this.location = vrl;
        open(location);
    }

    private void open(VRL vrl) throws FileNotFoundException
    {
        try
        {
            VFSClient vfs = File.getStaticVFSClient();
            this.location = vrl;
            output = vfs.openOutputStream(location);
        }
        catch (VlException e)
        {
            throw new FileNotFoundException("Couldn't open file:" + location + "\nException=" + e);
        }
    }

    public void write(int b) throws IOException
    {
        output.write(b);
    }

    public void write(byte b[]) throws IOException
    {
        this.output.write(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException
    {
        this.output.write(b, off, len);
    }

    public void close() throws IOException
    {
        this.output.close();
    }

    public void flush() throws IOException
    {
        this.output.flush();
    }

}
