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
 * $Id: VFSFactory.java,v 1.7 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

/** 
 * The VFSFactory for VFileSystems classes. 
 * <p>
 * This is the VRSFactory class for {@link VFileSystem} Resources. 
 * It is used by the {@link nl.uva.vlet.vrs.Registry} to open a remote filesystem. 
 * 
 * @author P.T. de Boer
 *
 */
public abstract class VFSFactory extends VRSFactory
{
	// ========================================================================
    // Instance Methods
    // ========================================================================
    /** Enforce public constructor for subclasses ! */ 
    public VFSFactory()
    {
        super();  
    }

    public void init()
    {
    }

    /** 
     * Return types of resources it supports.<br>
     * Overide this method to create custom types.<br>
     * Default the VFS (Virtual File System) should support FILE and DIR types !
     */
    public String[] getResourceTypes()
    {
        return VFS.defaultChildTypes; 
    }
   
	public VResourceSystem openResourceSystem(VRSContext context,VRL location) throws VlException
	{
		return this.openFileSystem(context, location); 
	}
	 
    public VFSNode openLocation(VRSContext context,String location) throws VlException
    {
        return openLocation(context,new VRL(location)); 
    }
    
	public VFSNode openLocation(VRSContext context,VRL location) throws VlException 
	{
		 return openFileSystem(context,location).openLocation(location); 
	}
	
	/**
	 * Open location and return {@link VFileSystem} instance which can handle resources 
	 * associated with the specified VRL. 
	 * 
	 * @param context the VRSContext to use 
	 * @param location actual location
	 * @return new or cached VFileSystem instance 
	 * @throws VlException
	 */
	public VFileSystem openFileSystem(VRSContext context, VRL location) throws VlException
	{
		return (VFileSystem)super.openResourceSystem(context,location); 
	}

	// ---------------------------------------------------------------
	// Super interface implementation to create a ResourceSystem. 
	// Is implemented by calling createNewFileSystem to 
	// so the the instance can be downcasted to VFileSystem. 
	// ---------------------------------------------------------------
	public VFileSystem createNewResourceSystem(VRSContext context,ServerInfo info, VRL location) throws VlException
	{
		return createNewFileSystem(context,info,location); 
	}

	// =======================================================================
	// Abstract Interface 
	// =======================================================================

	/**
	 * Factory method creating a new {@link VFileSystem} instance. 
	 * Will only be called when a new file system is needed. 
	 * Instance will be used for similar locations.
	 */
	public abstract VFileSystem createNewFileSystem(VRSContext context,ServerInfo info, VRL location) throws VlException; 
		 
	
}
