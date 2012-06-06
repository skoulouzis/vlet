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
 * $Id: ProxyWrapNodeFactory.java,v 1.4 2011-04-27 12:45:13 ptdeboer Exp $  
 * $Date: 2011-04-27 12:45:13 $
 */ 
// source: 

package nl.uva.vlet.gui.proxynode.impl.proxy;

import java.util.Enumeration;
import java.util.Hashtable;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.proxy.vrs.ProxyVRS;
import nl.uva.vlet.vrl.VRL;

public class ProxyWrapNodeFactory  implements ProxyNodeFactory
{
    public static ProxyWrapNodeFactory getDefault()
    {
        if (factory==null)
             factory=new ProxyWrapNodeFactory(); 
        
        return factory; 
    }

    
    private static ProxyWrapNodeFactory factory;
    /** 
     * HashMap for cached ProxyNodes.
     * This object is also used a mutex to synchronize for example 
     * openLocation methods. The time during this mutex operations
     * must be as short a possible as this is a class object! 
     * First get/create a proxyNode and use THAT node to do
     * further synchronization but only if really needed. 
     */
	// no weak references yet: the node are discared to fast ! 
	//private Hashtable<String, ProxyWrapNode.WeakProxyWrapNodeRef> nodeHash = new Hashtable<String, ProxyWrapNode.WeakProxyWrapNodeRef>();
	private Hashtable<VRL, ProxyWrapNode> nodeHash = new Hashtable<VRL, ProxyWrapNode>();
    private ProxyVRS invoker;
    
	/** Get from hash or creat new one ProxyWrapNode */
    public ProxyWrapNode openLocation(VRL loc) throws VlException
    {
        return openLocation(loc, false);
    }
    
    private ProxyWrapNodeFactory()
    {
        super();
        this.invoker=new ProxyVRS(UIGlobal.getVRSContext()); 
    }
    
    /**
     * Main openLocation method. May only be called by other openLocation methods
     * Check ProxyNode chache for location or creates new ProxyNode for the
     * location and stores it in the Cache.
     * Use this method to open new 'VNode'. 
     * 
     * @throws VlException
     */
    
    public ProxyWrapNode openLocation(VRL loc, boolean resolveLinks)
       throws VlException
    {
        ProxyWrapNode pnode = null;
        
        if (loc==null)
        	throw new NullPointerException("VRL can not be NULL");
        
        if (loc==null) 
            return null; 
        
        // ====================================================================
        // Although a Hastable is thread save, this object is also 
        // used as mutex when creating/manipulating proxynodes.
        // Optimization note: 
        // Keep the synchronized time as short as possible since
        // nodehash is a class object !
        // ====================================================================
        
        // atomic get: 
        synchronized (nodeHash)
        {
        	//WeakProxyWrapNodeRef ref = nodeHash.get(loc.toString());
            //if (ref!=null)
            //	pnode=ref.get(); 
            pnode=nodeHash.get(loc);
            
            if (pnode==null)
            {
            	//
                // Create New ProxyWrapNode with NULL vnode !
            	//
                pnode=new ProxyWrapNode(this,loc);
                
                // already put int the hashcode ! 
                nodeHash.put(loc,pnode);
            } 
        }
        
        // >>> here pnode.vnode *MIGHT* be null <<<  
        
        // ====================================================================
        // Now synchronize on pnode, since all threads arriving at this point
        // should have the same pnode (for same locations) whether it is 
        // opened or not !
        // ====================================================================
        
        synchronized(pnode)
        {
            // asynchronous refresh!: 
            if (pnode.mustrefresh==true)
            {
                pnode.clearCache();
            }
            
            // resolve one link when 'Opening' a link
            if ((pnode.isResourceLink()) && (resolveLinks))
            {
                pnode = pnode.getTargetPNode();
            }
            
            pnode.mustrefresh=false;// clear flag 
            
            // load selection of attributes, etc: 
            pnode.prefetchAttributes(); 
            
        }// sychronized(pnode)
               
        return pnode;
    }
    
    
    /** Fetch from cache or return null if not in cache */ 
    public ProxyWrapNode getFromCache(VRL loc) 
    {
    	if (loc==null) 
    		return null; // garbage in garbage out 
    	
    	//WeakProxyWrapNodeRef ref = nodeHash.get(loc.toString());
    	ProxyWrapNode pnode = nodeHash.get(loc);
        
        return pnode; 
    }
    
    void hashRemove(ProxyWrapNode node)
    {
        VRL loc=node.getVRL();
        
        //  loc==null: can happen when exiting (disposeClass());
        if (loc!=null)
            nodeHash.remove(loc.toString());
    }
    
   
    public void clearNodeHash()
    {
        for (Enumeration<VRL> keys =nodeHash.keys();keys.hasMoreElements();) 
        {
        	VRL key=keys.nextElement();
            //WeakProxyWrapNodeRef ref = nodeHash.get(key);
            ProxyWrapNode pnode =  nodeHash.get(key); 
            
            if (pnode!=null)
            {
           		pnode.dispose();
            }
            
        }
        
        nodeHash.clear();
        
        //proxyEventListeners.clear();
    }
 
    /**
     * Assert getNode (from cache): throws exception when not in cache
     * or when nullpointers are encountered.
     * Use this method when as an 'assert' method when an ProxyWrapNode must be known 
     */  
    public ProxyWrapNode assertGetNode(VRL loc) throws VlException 
    {
    	// this is an 'assert' method: null pointer not allowed. 
    	
    	if (loc==null) 
    		throw new VlException("assertGetNode(): null pointer"); 
    			
    
    	//WeakProxyWrapNodeRef ref = nodeHash.get(loc.toString());
    	   
        ProxyWrapNode node =  nodeHash.get(loc); 
        
        //if (ref!=null)
        //	node=ref.get(); 
 
        if (node==null) 
        	throw new VlException("assertGetNode: Node not in cache:"+loc); 
      
        return node; 
    }

    public ProxyVRS getInvoker()
    {
        return invoker; 

    }

    public ProxyWrapNode[] createNodes(VRL[] vrls) throws VlException
    {
        if (vrls==null)
            return null; 
        
        ProxyWrapNode nodes[]=new ProxyWrapNode[vrls.length];
        for (int i=0;i<vrls.length;i++)
        {
            nodes[i]=this.getOrCreateNode(vrls[i]); 
        }
        
        return nodes; 
    }

    /** Checks cache or create new node */ 
    public ProxyWrapNode getOrCreateNode(VRL vrl) throws VlException
    {
        synchronized(this.nodeHash)
        {
            ProxyWrapNode node = this.nodeHash.get(vrl); 
            if (node!=null)
                return node; 
            return this.createNodeFrom(vrl); 
        }
    }

    public ProxyWrapNode createNodeFrom(VRL vrl) throws VlException
    {
        synchronized(this.nodeHash)
        {
            ProxyWrapNode node=new ProxyWrapNode(this,vrl);
            this.nodeHash.put(vrl,node);
            return node;
        }
    }

    @Override
    public void reset()
    {
        // TODO Auto-generated method stub
        
    }
   

}
