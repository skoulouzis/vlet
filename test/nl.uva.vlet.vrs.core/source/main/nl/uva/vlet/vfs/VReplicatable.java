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
 * $Id: VReplicatable.java,v 1.5 2011-04-18 12:00:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:28 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vrl.VRL;

/**
 * Replica Interface for files which support replicas (LFC).   
 */ 
public interface VReplicatable
{
    /**
     * List all replicas. 
     */ 
	public VRL[] getReplicas() throws VlException;
	
	/**
	 * Register Replica URIs. 
	 * This method does not create any replicas and does not do any checking whether VRLs are 
	 * valid ! 
	 */
	public boolean registerReplicas(VRL vrls[]) throws VlException;

	/** 
	 * Unregister Replicas URIs.
	 * Does not delete them and does not do any checking, it just removed
	 * matching VRLs from the LFC registry! 
	 */
	public boolean unregisterReplicas(VRL vrls[]) throws VlException; 
	
	/**
	 * Replicate to specified Storage Element, returns new Replica VRL. 
	 * Implementation should update subTask fields of monitor ! 
	 */ 
    public VRL replicateTo(ITaskMonitor monitor, String storageElement) throws VlException;

    /** Delete Replica and unregister. */ 
    public boolean deleteReplica(ITaskMonitor monitor, String storageElement) throws VlException; 
}
