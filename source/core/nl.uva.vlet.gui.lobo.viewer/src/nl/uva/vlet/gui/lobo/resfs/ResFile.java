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
 * $Id: ResFile.java,v 1.3 2011-04-18 12:27:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:36 $
 */ 
// source: 

package nl.uva.vlet.gui.lobo.resfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VRandomReadable;
import nl.uva.vlet.vrs.io.VStreamReadable;

import org.lobobrowser.main.ExtensionManager;

public class ResFile extends VNode implements VRandomReadable,VStreamReadable
{
	
	
	public ResFile(ResResourceSystem resResourceSystem, VRL vrl)
	{
		super(resResourceSystem.getVRSContext(), vrl);
		// TODO Auto-generated constructor stub
		
	}


	@Override
	public boolean exists() throws VlException
	{
		// TODO Auto-generated method stub
		return true;
	}

	

	public InputStream getInputStream() throws VlException 
	{
		URL url=getURL();
    	String host = url.getHost();
    	ClassLoader classLoader;
    	
    	if(host == null) 
    	{
    		classLoader = this.getClass().getClassLoader();
    	}
    	else 
    	{
    		classLoader = ExtensionManager.getInstance().getClassLoader(host);
    		if(classLoader == null) 
    		{
    			classLoader = this.getClass().getClassLoader();
    		}
    	}
        String file = url.getPath();
        InputStream in = classLoader.getResourceAsStream(file);
        if(in == null) 
        {
            if(file.startsWith("/")) 
            {
                file = file.substring(1);
                in = classLoader.getResourceAsStream(file);
                if(in == null) 
                {
                    throw new ResourceNotFoundException("Resource " + file + " not found in " + host + ".");
                }
            }
        }
        return in;
    }
	

	public OutputStream getOutputStream() throws VlException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean renameTo(String newName, boolean renameFullPath)
			throws VlException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean delete() throws VlException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int readBytes(long fileOffset, byte[] buffer, int bufferOffset,
			int nrBytes) throws VlException
	{
		// TODO Auto-generated method stub
		return 0;
	}

   

    @Override
    public String getType()
    {
        // TODO Auto-generated method stub
        return null;
    }


   // @Override
    public long getLength() throws VlException
    {
        return -1;
    }



}
