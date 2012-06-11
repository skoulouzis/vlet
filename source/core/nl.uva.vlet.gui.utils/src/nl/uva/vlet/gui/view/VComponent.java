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
 * $Id: VComponent.java,v 1.2 2011-06-10 10:29:51 ptdeboer Exp $  
 * $Date: 2011-06-10 10:29:51 $
 */ 
// source: 

package nl.uva.vlet.gui.view;

import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.data.ResourceRef;

/** 
 * Component interface to detect my components from 
 * the JComponents.  
 * A VComponent (ViewComponent) is a component which points to a resource
 */
public interface VComponent
{
  /** Typically the BrowserController but could be another (Master)Browser interface */ 
  public MasterBrowser getMasterBrowser();
  
  /** a VComponent must be embedded in a VContainer ! */ 
  public VContainer getVContainer();

  /** Resource reference (instead of VRL) */ 
  public ResourceRef getResourceRef();
  
}
