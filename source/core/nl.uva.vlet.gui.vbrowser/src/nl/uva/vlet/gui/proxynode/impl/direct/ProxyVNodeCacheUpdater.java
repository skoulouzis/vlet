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
 * $Id: ProxyTNodeCacheUpdater.java,v 1.3 2011-04-18 12:27:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:29 $
 */ 
// source: 

package nl.uva.vlet.gui.proxynode.impl.direct;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.vrs.ResourceEvent;

/**
 * Since the ProxyCNode uses a Cache, it must listen
 * to ProxyResource events as well.
 *  
 * The default action is just to clear the cache in the case 
 * of a Resource event. 
 */ 
public class ProxyVNodeCacheUpdater implements ProxyResourceEventListener
{
    ProxyVNodeCacheUpdater()
    {
    	// auto register:
        ProxyVRSClient.getInstance().addResourceEventListener(this); 
    }
    
    public void notifyProxyEvent(ResourceEvent e)
    {
		if (e==null)
		{
			return ; 
		}
		
        // check if node is in cache, if not ignore!
        ProxyVNode node = ProxyVNodeFactory.getInstance().getFromCache(e.getSource());
        
		if (node==null)
		{
			Global.infoPrintf(this,"Ignoring event. Node not in cache:%s\n",e.getSource());
			return; 
		}
		
		switch (e.getType())
		{
                case SET_ATTRIBUTES:
                    //update attributes:
                    if (node!=null)
                        node.handleSetAttributesEvent(e.getAttributes());
                    break;
                case SET_CHILDS:
                case CHILDS_DELETED:
                case CHILDS_ADDED:
                    if (node!=null)
                    // do not update, just clear:
                     node.handleChildsEvent(e.getChilds()); 
                    break;
                // others:

                case REFRESH:
                	// handle refresh UPDATE CACHE !
                	node.handleRefresh(); 
                	break; 
                case RENAME:
                	
                case DELETE:
                case SET_BUSY: 
                	break;
                case NO_EVENT:
                case MESSAGE:
                    // use global display since this event must be shown
                    // only once. 
                	// Todo: Move to somewhere else 
                    UIGlobal.displayEventMessage(e);
                	
                    break; 
                default:
                    break; 
        }
    }
	
}
