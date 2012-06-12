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
 * $Id: ProxyTNodeFactory.java,v 1.7 2011-04-27 12:45:05 ptdeboer Exp $  
 * $Date: 2011-04-27 12:45:05 $
 */ 
// source: 

package nl.uva.vlet.gui.proxynode.impl.direct;

import java.util.Enumeration;
import java.util.Hashtable;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

public class ProxyVNodeFactory  implements ProxyNodeFactory
{
    private static ProxyVNodeFactory instance=null; 
    
    public static void initPlatform()
    {
        ProxyVRSClient.getInstance().setProxyNodeFactory(getInstance()); 
    } 
    
    public static synchronized ProxyVNodeFactory getInstance()
    {
        if (instance==null)
            instance=new ProxyVNodeFactory(); 

        return instance; 
    }
    
    /** 
     * HashMap for cached ProxyNodes.
     * This object is also used a mutex to synchronize for example 
     * openLocation methods. The time during this mutex operations
     * must be as short a possible as this is a class object! 
     * First get/create a proxyNode and use THAT node to do
     * further synchronization but only if really needed. 
     */
	// no weak references yet: the node are discared to fast ! 
	//private Hashtable<String, ProxyTNode.WeakProxyTNodeRef> nodeHash = new Hashtable<String, ProxyTNode.WeakProxyTNodeRef>();
	private Hashtable<VRL, ProxyVNode> nodeHash = new Hashtable<VRL, ProxyVNode>();
    
	/** Get from hash or creat new one ProxyTNode */
    public ProxyVNode openLocation(VRL loc) throws VlException
    {
        return openLocation(loc, false);
    }
    
    /**
     * Main openLocation method. May only be called by other openLocation methods
     * Check ProxyNode chache for location or creates new ProxyNode for the
     * location and stores it in the Cache.
     * Use this method to open new 'VNode'. 
     * 
     * @throws VlException
     */
    
    public ProxyVNode openLocation(VRL loc, boolean resolveLinks)
       throws VlException
    {
        ProxyVNode pnode = null;
        
        if (loc==null)
        	throw new NullPointerException("VRL can not be NULL");
        
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
        	//WeakProxyTNodeRef ref = nodeHash.get(loc.toString());
            //if (ref!=null)
            //	pnode=ref.get(); 
            pnode=nodeHash.get(loc);
            
            // store VRL which is used to open this node ! 
             
            
            if (pnode==null)
            {
            	//
                // Create New ProxyTNode with NULL vnode !
            	//
                pnode=new ProxyVNode();
                pnode.setAliasVRL(loc);    
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
            
            //          need to open this location:
            if ((pnode.vnode==null) || (pnode.mustrefresh))
            {
                // if this happens, this can deadlock the gui. 
                ProxyVNode.assertNotGuiThread(loc); 
                
                //
                // Note that there are only two method which create
                // VNodes, this one and getChilds! 
                // 
                try
                {
                	pnode.vnode = UIGlobal.getVRSContext().openLocation(loc);
                }
                catch (Throwable t)
                {
                	// remove from hash 
                	nodeHash.remove(loc);
                	if (t instanceof VlException)
                	    throw ((VlException)t); 
                	
                	throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't get node:"+loc
                	        +"\n"+t.getMessage(),t);
                }
                
                if (pnode.vnode==null)
                	throw new nl.uva.vlet.exception.ResourceNotFoundException("Got NULL Node. Couldn't get node:"+loc); 
                
            }
            else
            {
                // double openLocation:
                // pnode.vnode already opened:
            }
            
            if (pnode.vnode.getVRL()==null)
            	throw new NullPointerException("VRL of VNode is NULL"); 
            
            // resolve one link when 'Opening' a link
            if ((pnode.isResourceLink()) && (resolveLinks))
            {
                pnode = pnode.getTargetPNode();
                
                if (pnode.vnode==null)
                	throw new nl.uva.vlet.exception.ResourceNotFoundException("Couldn't resolve LinkNode:"+loc); 
            }
            
            pnode.mustrefresh=false;// clear flag 
            
            // load selection of attributes, etc: 
            pnode.prefetchAttributes(); 
            
        }// sychronized(pnode)
        
        // check & store alias:
        synchronized (nodeHash)
        {
            // IMPORTANT: add  resolved location to cache as well, 
            // as the implementation might have chaged the location. 
            // For example in the case of "~" expansion :. 
            
            // must added resolved location to cache as well: 
            VRL alias=pnode.vnode.getLocation(); 
            
            // must compare string representations: 
            if (alias.toString().compareTo(loc.toString())!=0) 
            {
            	//nodeHash.put(alias.toString(),new WeakProxyTNodeRef(pnode));
                nodeHash.put(alias,pnode);
                //Global.messagePrintln(ProxyTNode.class,">>> putting alias:"+alias); 
            } 
        }
       
        return pnode;
    }
    
    
    /** Fetch from cache or return null if not in cache */ 
    public ProxyVNode getFromCache(VRL loc) 
    {
    	if (loc==null) 
    		return null; // garbage in garbage out 
    	
    	//WeakProxyTNodeRef ref = nodeHash.get(loc.toString());
    	ProxyVNode pnode = nodeHash.get(loc);
    	
    	// Fetching ProxyNode during an open location ! 
    	if ((pnode==null) || (pnode.vnode==null))
    		return null;
        
        return pnode; 
    }
    
    void hashRemove(ProxyVNode node)
    {
        VRL loc=node.getVRL();
        
        //  loc==null: can happen when exiting (disposeClass());
        if (loc!=null)
            nodeHash.remove(loc.toString());
    }
    
    /** Create new ProxyNot and put it in the cache */ 
    
    public ProxyVNode createProxyTNode(VNode node)
    { 
        ProxyVNode pnode=null;
        //ProxyTNode.WeakProxyTNodeRef prev=null; 
        ProxyVNode prev=null;
        
        if (node.getVRL()==null)
        	throw new NullPointerException("Cannot create new ProxyNode with NULL location"); 
        
        // use nodeHash as mutex
        synchronized (nodeHash)
        {
            //prev=nodeHash.get(node.getLocation().toString());
            // put new one: 
            {
                pnode=new ProxyVNode();
                pnode.vnode=node;
                //prev=nodeHash.put(node.getLocation().toString(),new WeakProxyTNodeRef(pnode));
                prev=nodeHash.put(node.getVRL(),pnode);
              
            }
            
            if (prev!=null)
            {
                // is allowed: cache write through/update
                Global.debugPrintf(ProxyVNode.class,"*** Warning: Node already in cache:%s\n",pnode); 
            }
        }
        
        pnode.prefetchAttributes(); 
        
        return pnode; 
    }
    
    public void clearNodeHash()
    {
        for (Enumeration<VRL> keys =nodeHash.keys();keys.hasMoreElements();) 
        {
        	VRL key=keys.nextElement();
            //WeakProxyTNodeRef ref = nodeHash.get(key);
            ProxyVNode pnode =  nodeHash.get(key); 
            
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
     * Use this method when as an 'assert' method when an ProxyTNode must be known 
     */  
    public ProxyVNode assertGetNode(VRL loc) throws VlException 
    {
    	// this is an 'assert' method: null pointer not allowed. 
    	
    	if (loc==null) 
    		throw new VlException("assertGetNode(): null pointer"); 
    			
    
    	//WeakProxyTNodeRef ref = nodeHash.get(loc.toString());
    	   
        ProxyVNode node =  nodeHash.get(loc); 
        
        //if (ref!=null)
        //	node=ref.get(); 
 
        if (node==null) 
        	throw new VlException("assertGetNode: Node not in cache:"+loc); 
      
        return node; 
    }

    /** Used for debugging/testing purposes ! */
    public ProxyNode createFrom(VNode node)
    {   
        ProxyVNode pnode=new ProxyVNode();
        pnode.vnode=node;
        if (node.getVRL()==null)
            throw new NullPointerException("VRL of node is NULL");
        
        this.nodeHash.put(node.getVRL(),pnode);
        return pnode;
    }

    @Override
    public void reset()
    {
        resetCache();
    }
    
    protected void resetCache()
    {
        ProxyVNode nodes[]; 
        
        synchronized(this.nodeHash)
        {
            nodes=new ProxyVNode[nodeHash.size()];   
            nodes=nodeHash.values().toArray(nodes);
            nodeHash.clear(); 
        }
        
        // clear nodes: 
        for (ProxyVNode node:nodes)
            node.dispose(); 
    }

}
