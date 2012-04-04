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
 * $Id: ResourceEventListener.java,v 1.5 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

/** 
 * Interface for listeners which want to receive Resource Events. 
 * An event listener implementing this interface has to register itself
 * using the method addResourceEventListener() at the used VRSContext. <br>
 * <p> 
 * @see VRSContext#addResourceEventListener(ResourceEventListener)
 * @see ResourceEvent
 */

public interface ResourceEventListener
{
	/** 
	 * Is called when a ResourceEvent has been fired. 
	 * It is recommend to handle the event as quickly as possible
	 * or start a new Thread which handles the ResourceEvent in the background. 
	 * 
	 * @param event The ResourceEvent. 
	 */
    public void notifyResourceEvent(ResourceEvent event);
}
