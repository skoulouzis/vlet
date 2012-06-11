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
 * $Id: ProxyNodeFactory.java,v 1.5 2011-04-27 12:38:43 ptdeboer Exp $  
 * $Date: 2011-04-27 12:38:43 $
 */ 
// source: 

package nl.uva.vlet.gui.proxyvrs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/** Abstract interface for ProxyNode factories */
public interface ProxyNodeFactory
{
    public abstract ProxyNode openLocation(VRL loc) throws VlException ; 
  
    public abstract ProxyNode openLocation(VRL loc, boolean resolveLinks) throws VlException ;
    
    /**
     * @deprecated Caching will not be visible in the future
     * All code will be update to use a proper proxy model instead. 
     */  
	public abstract ProxyNode getFromCache(VRL vrl);

    public abstract void reset(); 
	
}
