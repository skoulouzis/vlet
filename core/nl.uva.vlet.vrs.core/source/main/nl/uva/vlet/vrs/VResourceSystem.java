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
 * $Id: VResourceSystem.java,v 1.8 2011-04-27 12:29:11 ptdeboer Exp $  
 * $Date: 2011-04-27 12:29:11 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;


/**
 * 
 * VResourceSystem is a factory class for VNodes. 
 * 
 * @author P.T. de Boer 
 *
 */
public interface VResourceSystem
{
	/** 
	 * Return unique identifier for this Resource System.
	 * This is used in the VRSContext to manage resource system
	 * instances.    
	 * @return Unique identifier String 
	 */
	abstract public String getID();  
	
	/**
	 * Main VNode factory method. 
	 * Open the location and return the resource from this ResourceSystem specified
	 * by the VRL. 
	 */
	abstract public VNode openLocation(VRL vrl) throws VlException;
	
	/** 
	 * Returns VRSContext associated with this ResourceSystem  
	 */ 
	abstract public VRSContext getVRSContext();

	/**
	 * Setup connection if not already connected. 
	 * Multiple connect() calls may occur. If already connected ignore 
	 * any successive invocations.  
	 * @throws VlException
	 */
	abstract public void connect() throws VlException ;
	
	/** 
	 * Disconnected and close/cleanup all resources associated with this 
	 * resource system.
	 * After this call a connect() may be called. 
	 */
    abstract public void disconnect() throws VlException ;

    /** 
     * Dispose all resources, object may not be used anymore.
     */
    public abstract void dispose();
}
