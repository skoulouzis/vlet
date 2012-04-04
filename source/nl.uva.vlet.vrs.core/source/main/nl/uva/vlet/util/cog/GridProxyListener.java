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
 * $Id: GridProxyListener.java,v 1.5 2011-05-11 14:32:30 ptdeboer Exp $  
 * $Date: 2011-05-11 14:32:30 $
 */ 
// source: 

package nl.uva.vlet.util.cog;

import nl.uva.vlet.data.VAttribute;

public interface GridProxyListener
{
    /** 
     *Method is called when the Credentials Validity has changed
     * @param gridProxy 
     */   
    public void notifyProxyValidityChanged(GridProxy gridProxy, boolean newValidity); 
    
    /** 
     * Called when the user certificate store has been changed. 
     * If alias==null the complete store has been synchronized/updated. 
     */ 
    public void notifyCACertStoreUpdated(String alias); 
    
    /** Called when an attribute of the proxy has changed, for example the VO */ 
    public void notifyProxyAttributesChanged(GridProxy gridProxy,VAttribute attrs[]);  
    
}
