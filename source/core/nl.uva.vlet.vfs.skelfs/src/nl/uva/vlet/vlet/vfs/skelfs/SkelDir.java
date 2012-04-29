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
 * $Id: SKDir.java,v 1.4 2011-05-02 13:36:11 ptdeboer Exp $  
 * $Date: 2011-05-02 13:36:11 $
 */ 
// source: 

package nl.uva.vlet.vlet.vfs.skelfs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;

/**
 * Minimal implementation of the VDir class. 
 * Redirect most methods to the FileSystem object. 
 */
public class SkelDir extends VDir
{

	public SkelDir(SkelFS skelfs, VRL vrl)
	{
		super(skelfs, vrl);
	}
	
	public SkelDir(SkelFS skelfs, String path)  throws VlException
	{
		this(skelfs, skelfs.resolvePathVRL(path));  	
	}
	
	@Override
	public boolean create(boolean force) throws VlException
	{
		return this.getFS().mkdir(this.getPath(),force); 
	}
	
	@Override
	public boolean exists() throws VlException
	{
		return this.getFS().exists(this.getPath(),true); 
	}
	
	@Override
	public VFSNode[] list() throws VlException 
	{
		VFSNode nodes[]=new VFSNode[3];
		
		String[] paths = this.getVRL().getPathElements(); 
		
		nodes[0]=new SkelDir(this.getFileSystem(),this.resolvePathVRL("SK-Dir#"+paths.length)); 
		nodes[1]=new SkelFile(this.getFileSystem(),this.resolvePathVRL("SK-file1")); 
        nodes[2]=new SkelFile(this.getFileSystem(),this.resolvePathVRL("SK-file2")); 
		    	
        return nodes; 
	}
	
	public SkelFS getFileSystem()
	{
	    // Down Cast from VFileSystem inteface to actual FileSystem object. 
	    return (SkelFS)super.getFileSystem(); 
	}
	
	@Override
	public long getModificationTime() throws VlException
	{
		// return current time (for testing), replace with actaul modification time 
		// of file. 
		return System.currentTimeMillis();
	}

	@Override
	public boolean isReadable() throws VlException 
	{
		// check user accessibility. 
		return this.getFS().hasReadAccess(this.getPath()); 
	}

	@Override
	public boolean isWritable() throws VlException
	{
		// check user right to change, write to this file. 
		return this.getFS().hasWriteAccess(this.getPath()); 
	}

	public long getNrOfNodes() throws VlException
	{
		// If there is a faster way the just listing the directory, 
		// re implement this method. 
		VFSNode[] files = this.list();
		
		if (files==null)
			return 0; 
		
		return files.length; 
	}

	public VRL rename(String newName, boolean renameFullPath)
			throws VlException
	{
		// Perform rename. This can be a full path rename. 
		return this.getFS().rename(getPath(),newName,renameFullPath);  
	}

	public boolean delete(boolean recurse) throws VlException
	{
		return this.getFS().delete(this.getPath(),true,recurse);
	}
	
	protected SkelFS getFS()
    {
    	return ((SkelFS)this.getFileSystem());  
    }

}
