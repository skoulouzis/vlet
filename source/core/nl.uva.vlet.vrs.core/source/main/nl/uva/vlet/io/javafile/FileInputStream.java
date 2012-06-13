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
 * $Id: FileInputStream.java,v 1.4 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */
// source: 

package nl.uva.vlet.io.javafile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vrl.VRL;

/**
 * java.io.FileInputStream compatible class.
 * <p>
 * The classes in this package are under construction and any volunteer willing
 * to test this class is welcome.
 * 
 * @author Piter T. de Boer
 */
public class FileInputStream extends InputStream
{
    VRL location = null;

    private InputStream input;

    public FileInputStream(String name) throws FileNotFoundException
    {
        VFSClient vfs = File.getStaticVFSClient();
        try
        {
            location = vfs.resolve(name);
        }
        catch (VRLSyntaxException e)
        {
            throw new FileNotFoundException("Couldn't open file. URI Syntax exception for:" + "\nException=" + e);
        }

        open(location);

    }

    private void open(VRL vrl) throws FileNotFoundException
    {
        try
        {
            VFSClient vfs = File.getStaticVFSClient();
            this.location = vrl;
            input = vfs.openInputStream(location);
        }
        catch (VlException e)
        {
            throw new FileNotFoundException("Couldn't open file:" + location + "\nException=" + e);
        }
    }

    public FileInputStream(File file) throws FileNotFoundException
    {
        open(file.toVRL());
    }

    public int read(byte b[]) throws IOException
    {
        return this.input.read(b, 0, b.length);
    }

    public int read(byte b[], int off, int len) throws IOException
    {
        return this.input.read(b, off, len);
    }

    public long skip(long n) throws IOException
    {
        return this.input.skip(n);
    }

    public int available() throws IOException
    {
        return input.available();
    }

    public void close() throws IOException
    {
        this.input.close();
    }

    public int read() throws IOException
    {
        return this.read();
    }

}
