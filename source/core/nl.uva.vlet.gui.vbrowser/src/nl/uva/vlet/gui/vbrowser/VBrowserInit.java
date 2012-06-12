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
 * $Id: VBrowserInit.java,v 1.4 2011-04-18 12:27:22 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:22 $
 */ 
// source: 

package nl.uva.vlet.gui.vbrowser;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.UIPlatform;
import nl.uva.vlet.gui.aboutrs.AboutRSFactory;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNode;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;

public class VBrowserInit
{

    public static UIPlatform initPlatform() throws VlException
    {
        // todo: better platform initialization. 
        // current order to initialize: 
        UIGlobal.init();

        // ========
        // Poxy VRS/ProxyNode (before UI Platform) 
        // ========
        
        ProxyVNodeFactory.initPlatform(); 
 
        AboutRSFactory.initPlatform(); 
        // prefetch MyVLe, during startup:
        ProxyVNode.getVirtualRoot();     
        
        // ==========
        // UIPlatform
        // ==========
        
        // Register VBrowser platform 
        UIPlatform plat=UIPlatform.getPlatform();
        plat.registerBrowserFactory(VBrowserFactory.getInstance());
        
        // Option --native ? :
        plat.startCustomLAF(); 
        
        return plat;
    }

}
