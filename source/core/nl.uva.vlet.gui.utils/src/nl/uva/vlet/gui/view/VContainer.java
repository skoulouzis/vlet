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
 * $Id: VContainer.java,v 1.1 2011-06-10 10:18:03 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:03 $
 */ 
// source: 

package nl.uva.vlet.gui.view;

import nl.uva.vlet.gui.data.ResourceRef;

/** 
 * Component interface to detect my components from 
 * the JComponents.  
 * A VContainer is a component which contains one or more 
 * Resources, like a ResourceTree or IconsPanel 
 */
public interface VContainer extends VComponent
{
  /** get selected resources from this (GUI) container */ 
  public ResourceRef[] getSelection();

  /** Returns VComponent which matches ResourceRef */
  public VComponent getVComponent(ResourceRef ref);

  public void selectAll(boolean selectValue);	  
  
}
