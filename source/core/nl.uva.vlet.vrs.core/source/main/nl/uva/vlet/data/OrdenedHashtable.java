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
 * $Id: OrdenedHashtable.java,v 1.6 2011-04-18 12:00:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:34 $
 */ 
// source: 

package nl.uva.vlet.data;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;

/** 
 * Synchronized Ordened Hashtable. 
 * The order in which elements are added (put) are kept in a keyVector. 
 * <p>
 * UNDER CONSTRUCTION. <br>
 * <p>
 * The methods keys() and keySet() return the unordered keyset from 
 * the underlying Hashtable. As well as the other Enumeration methods. 
 * The problem is that Set and Enumeration implemententations do not allow 
 * ordening of their elements since the are implemented as a linked list based
 * on the hashcode of the keys. 
 * <p>
 * Use methods: getOrdenedKeyArray() and getOrdenedKeyVector() to get the key
 * array or key vector. <br>
 * Use setKeyOrder() to (re)arrange the order of keys.   
 * <p>
 * TODO:<br>
 * - sorting of KeySet: If the key class <TK> implements the Comparable
 *    interface, the order can be sorted. 
 *  
 * @author Piter T. de Boer
 *
 * @param <TK> Key Object
 * @param <TV> Value Object 
 */
public class OrdenedHashtable<TK, TV> extends Hashtable<TK,TV>
	implements Iterable<TV>
{
	private static final long serialVersionUID = -6484877189331142210L;
	
    /** Set to true: only for debug purposes. */  
    protected static boolean checkKeys=true;

    private static ClassLogger logger=null; 
    
    static
    {
        logger=ClassLogger.getLogger("nl.uva.vlet.data.OrdenedHashtable"); 
    }
    
	protected Vector<TK>ordenedKeys=new Vector<TK>(); 

	
	/** 
	 * This is a costly operation, but current OrdenededHashtable arn't that big. 
	 */
	protected synchronized void checkKeys()
	{
		if (checkKeys==false)
			return; 
		
		boolean update=false; 
		
		// UNORDERED Key set: 
		Set<TK> set = this.keySet(); 
		
		// check if all key in orderedSet are in the KeySet 
		for (TK key:ordenedKeys)
		{
			if (set.contains(key)==false)
			{
				logger.errorPrintf("*** Internal Error; ordened key vector auto of sync:%s",key);
				update=true; 
 
			}
		}
		
		// check if all keys in the key set are in the ordered set. 
		for (TK key:set)
		{
			if (ordenedKeys.contains(key)==false)
			{
				logger.errorPrintf("*** Internal Error: key from hashtable set NOT in orderedKeys:%s\n",key); 
				update=true; 
			}
		}
		
		// restore key vector
		if (update)
		{
			
			this.updateKeysFromHashtable();
		}
		
	}

	// =======================================================================
	// Ordered Methods 
	// =======================================================================
	
	synchronized public TV put(TK key, TV value)
	{
		TV prev=super.put(key,value);
		
		// logger.debugPrintf("putting K,V: %s,%s\n",key,value); 
		
		// add NEW key: ! 
		if (prev==null)
			ordenedKeys.add(key);
		
		return prev; 
	}

	public synchronized TV remove(Object key)
	{
		logger.debugPrintf("removing <K>:%s\n",key); 
		
		TV val=super.remove(key);
		
		// the equals() method MUST be implemented 
		// for similar key objects (in terms of hashcode) to actually
		// be removed; 
		
		ordenedKeys.remove(key);
		
		return val ;
	}
	
	
	/** Return Keys in ordered Vector ! */
	public Vector<TK> getOrdenedKeyVector()
	{
		checkKeys(); 
		return this.ordenedKeys; 
	}
	
	/** Return Keys in ordered Vector ! */
	@SuppressWarnings("unchecked")
	public TK[] getOrdenedKeyArray()
	{
		checkKeys();
		TK keys[];
		
		int size=ordenedKeys.size();
		
		if (size<=0) 
			return null;
		
		// need instance to get class: 
		TK dummy=ordenedKeys.get(0); 
		
		// code from Vector.toArray() 
		TK[] newInstance = (TK[])java.lang.reflect.Array.newInstance(
                   dummy.getClass(), size);
		keys = newInstance;

		keys=ordenedKeys.toArray(keys); 
        return keys;  
	}
	
	public boolean containsKey(Object key)
	{
		boolean result1=super.containsKey(key);
		boolean result2=this.ordenedKeys.contains(key);
		
		// simple constancy checking ! 
		if (result1!=result2)
			throw new Error("Ordered key error: KeySet ("+result1+") and Ordered Key Vector ("+result2+") disagree about key:"+key); 
		// the Hashtable store is master! 
		return result1; 
	}
	
	/** Returns key object at specified index */ 
	public TK getKey(int index)
	{
		return this.ordenedKeys.get(index); 
	}
	
	/** Adds All entries to current map */ 
    public synchronized void putAll(Map<? extends TK, ? extends TV> map) 
    {
        {
            super.putAll(map);
            updateKeysFromHashtable();
        }
    }
    
    /** Put selection from Map map into this Hashtable */ 
    public synchronized void put(Map<? extends TK, ? extends TV> map,Iterable<TK> keys) 
    {
        for (TK key:keys)
        {
            put(key,map.get(key)); 
        }
    }
    /** Put selection from Map map into this Hashtable */ 
    public synchronized void put(Map<? extends TK, ? extends TV> map,TK keys[]) 
    {
        for (TK key:keys)
        {
            put(key,map.get(key)); 
        }
    }
    
	/** Check key entries from hashtable store and update ordened key vector. */ 
	protected synchronized boolean updateKeysFromHashtable()
	{
		boolean notsync=false; 
		// UNORDERED Key set: 
		Set<TK> keys = this.keySet(); 
		
		// check if all key in orderedSet are in the KeySet 
		for (TK key:keys) 
		{
			// add new keys: 
			if (this.ordenedKeys.contains(key)==false)
			{
				logger.debugPrintf("updateKeys: adding key from hashtable to key vector:%s\n",key);
				this.ordenedKeys.add(key);
				notsync=true; 
			}
		}
		
		// remove keys not in hashtable from key vector.  
		for (TK key:this.ordenedKeys)
		{
			if (this.containsKey(key)==false)
			{
				logger.debugPrintf("updatKeys: removing key from key vector:%s\n",key);
				this.ordenedKeys.remove(key);
				notsync=true;
			}
		}
		// return whether key vector was sync. 
		return notsync; 
	}
	
	/** 
	 * Order keys according to newKeys array.
	 * <p> 
	 * Will arrange (sort) the key vector according the order in the
	 * provided key array.  
	 * <p>
	 * Calls method: setOrderedKeyNames(newKeys,true) 
	 * @see setKeyOrder. 
	 */
	public synchronized void setKeyOrder(TK newKeys[])
	{
		this.setKeyOrder(newKeys,true); 
	}
	
	/**
	 * Set new key order according to the order in the newKeys array. 
	 * If a key is in the newKeys array but it is not present in this
	 * Hashtable, the key will be ignored independent of the strict settings.  
	 * <p> 
	 * If strict==true then entries in the Hashtable which are NOT in the newKeys array 
	 * will be removed.  <br>
	 * Use strict=false to order the keys using a template key list which contains 
	 * keys which may or may not be present in the Hashtable.  
	 *  
	 * @param newKeys   New keyset
	 * @param strict	Whether to remove old entries NOT in the new keys[] list. 
	 */
	public synchronized void setKeyOrder(TK newKeys[],boolean strict)
	{
		// pass one: just add new names before old entries;
		Vector<TK> oldKeys = this.ordenedKeys;
		
		// initialization: empty/null key list; 
		if ((newKeys==null) || (newKeys.length==0)) 
		{
		    if ((oldKeys==null) || (oldKeys.size()==0))
		        return;
		    else if (strict)
		        oldKeys.clear(); //update to match EMPTY key order !
		    else
		        return; // empty keys list, non strict=> nothing todo!
		}
		
		//new empty key vector: 
		this.ordenedKeys=new Vector<TK>(newKeys.length+this.size());
		if (newKeys!=null)
		{
		    for (TK key:newKeys)
    		{
    			// current hashtable MUST already have key ! 
    			if (super.containsKey(key)==true)
    			{
    				// only to new key list it is not already present. 
    				if (ordenedKeys.contains(newKeys)==false)
    					ordenedKeys.add(key);
    				else
    					logger.debugPrintf("*** Warning: Duplicate new key in setKeyOrder:%s",key);
    								
    			}
    			else
    				logger.debugPrintf("*** Warning: New key in setKeyOrder list is not present as old key:%s\n",key);
    		}
		}
		
		//
		// Add old keys if not already added. 
		// remove old key+value from hashtable if strict=true and the key is
		// not in the newKeys array
		//
	
		for (TK key:oldKeys)
		{
			// should be true: this is the old keySet from the current Hashtable 
			if (super.containsKey(key)==true) 
			{
				if (strict==true)
				{
					// remove old (key,value) pair if NOT in new key set: 
					if (ordenedKeys.contains(key)==false)
						super.remove(key); // remove from super ! 
				}
				else
				{
					// not strict: add old key values: 
					// new key vector may not already contain key: 
					if (ordenedKeys.contains(key)==false)
						ordenedKeys.add(key);
					else
						logger.debugPrintf("Old key already added as new key:%s\n",key);
				}
			}
			else
				logger.debugPrintf("*** Synchronisation Error: key should be present:%s",key);
		}
		
		// checkKeys ! 
		checkKeys();  
		
	}
 
	/** 
	 * Returns Ordered Vector. 
	 */
    public synchronized Vector<TV> toVector()
    {
        Vector<TV>  attrs=new Vector<TV>(this.size()); 
        TK names[]=this.getOrdenedKeyArray();
         
        for (TK name:names)  
        {
            attrs.add(this.get(name)); 
        }
        
        return attrs; 
    }
    
    /** 
	 * Returns Ordered Object Vector. 
	 */
    public synchronized Vector<Object> toObjectVector()
    {
        Vector<Object>  attrs=new Vector<Object>(this.size()); 
        TK names[]=this.getOrdenedKeyArray();
         
        for (TK name:names)  
        {
            attrs.add(this.get(name)); 
        }
        
        return attrs; 
    }
    
    /**
     * Return Values as Ordened Array.  
     * Uses ordenedKeysArray to determine order. 
     */ 
   
    public synchronized TV[] toArray()
    {
    	return toArray(null); 
    }
    
    /** Type safety: Provide target class of returned Array */  
   
	public synchronized <U> TV[] toArray(Class<?> cellClass)
    {
    	 if (size()<=0)
    		 return null;
    	 
    	 int size=size(); 
    	 
    	 TK keys[]=this.getOrdenedKeyArray();
    	 
    	// need instance to get class: 
    	Object dummy=this.get(keys[0]);
    	
    	// target class:
    	
    	if (cellClass==null)
    		cellClass=dummy.getClass(); 
    		
    	// code from Vector.toArray()
    	@SuppressWarnings("unchecked")
    	// Todo: Allocate correct Array Type
    	TV values[] = (TV[])java.lang.reflect.Array.newInstance(
    			cellClass, size);

    	int index=0; 
    	
    	for (TK key:keys)
    		values[index++]=this.get(key); 
    	
    	return values; 
    }
    
    public synchronized void clear()
    {
    	super.clear(); 
    	this.ordenedKeys.clear(); 
    }
 
    /** 
     * Protected Method: Set Key Vector only. 
     * If update==true the key vector will be synchronized against the Hashtable keySet. 
     */  
    @SuppressWarnings("unchecked")
	protected void setKeyVector(Vector<TK> keys,boolean update)
	{
		
		this.ordenedKeys=(Vector<TK>) keys.clone();
		if (update)
			this.updateKeysFromHashtable(); 
	}
	
	 public synchronized TV elementAt(int i)
	 {
		 TK key=this.ordenedKeys.elementAt(i);
		 return get(key); 
	 }
	    
	 public int getIndexOfKey(TK currentKey)
	 {
		 synchronized(ordenedKeys)
		 {
			return this.ordenedKeys.indexOf(currentKey); 
		 }
	 }
	 
	 public Iterator<TV> iterator()
	 {
			return new OrdenedHashtableIterator<TK,TV>(this); 
	 }

}
