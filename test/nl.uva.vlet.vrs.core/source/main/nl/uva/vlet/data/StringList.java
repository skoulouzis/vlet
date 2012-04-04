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
 * $Id: StringList.java,v 1.7 2011-06-21 10:35:12 ptdeboer Exp $  
 * $Date: 2011-06-21 10:35:12 $
 */ 
// source: 

package nl.uva.vlet.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import nl.uva.vlet.util.QSort;


/** 
 * Helper class for StringLists. 
 * Duplicates are allowed as well as NULL elements since this
 * is an ArraList<> generic type.  
 * <p> 
 * Note: ArrayList is an UNsynchronized Array Generic type. 
 */ 

public class StringList extends ArrayList<String> implements Cloneable, Serializable 
{
    // 
    private static final long serialVersionUID = 4174370138730654047L;

    /** Construct String list from string array */ 
    public StringList(String[] strs)
    {
        if (strs==null)
            return; // NIL list
        add(strs); 
    }

    public StringList()
    {
        super();// =default;
    }

    public StringList(String str)
    {
        String list[]=new String[1]; 
        list[0]=str; 
        add(list); 
    }

    /**
     * Creates a StringList with an capacity of num. 
     * Actual reported size() will be 0!
     * Only useful if the size is known in advance and the list size will not change. 
     * @param len
     */
    public StringList(int capacity)
    {
        super(capacity); 
    }
    
    public StringList(Collection<? extends String> list)
    {
        super(list); 
    }
    
    
    /** 
     * Concatenates String Array to one String using '\n' as
     * line seperator. <br>
     * Calls toString("\n");  
     */  
    public String toString()
    {
        return toString(","); 
    }
    
    /** 
     * Concatenates String array using lineSeperator between lines 
     */ 
    public String toString(String lineSeperator)
    {
        return toString(null,null,lineSeperator);
    }
    
    public String toString(String quote, String lineSeperator)
    {
        return toString(quote,quote,lineSeperator);
    }
    
    public String toString(String beginQuote,String endQuote,String lineSeperator)
    {
        // two pass to alloc right size of target string 
        int nrStrs=this.size();
        int totalSize=0; 
        
        // calculate target size 
        for (int i=0;i<nrStrs;i++)
        {
            totalSize+=this.get(i).length();
        }
        
        // allocate target size to speed up allocation. 
        StringBuffer buf=new StringBuffer(totalSize); 
        
        // calculate target size 
        for (int i=0;i<nrStrs;i++)
        {
            if (beginQuote!=null)
                buf.append(beginQuote);
            buf.append(this.get(i));
            if (endQuote!=null)
                buf.append(endQuote);
            
            // add lineseperator, but only BETWEEN strings 
            if (i+1<nrStrs)
                buf.append(lineSeperator);  
        }
        
        return buf.toString();  
    }
    
     public String[] toArray() 
     {
         //downcast: allocate new String Array: 
         String array[]=new String[size()];
         return this.toArray(array);// use super method. 
     }
     
    /**
     *  Uses String.split(regexp) to create StringList
     * 
     * @see #java.lang.String.split(); 
     * @param str
     * @return 
     */ 
    public static StringList createFrom(String str,String regexp)
    {
        if ((str==null) || (regexp==null))
            return null;
        
        String strs[]=str.split(regexp); 
        return new StringList(strs); 
    }

    /** Merge String array into one StringList. Uses merge() */ 
    public static StringList createFrom(String list1[],String list2[])
    {
        StringList list=new StringList(list1); 
        list.merge(list2); 
        return list; 
    }
    
    /** Merge String arrays into one StringList. Uses merge() */ 
    public static StringList createFrom(String list1[],String list2[],String list3[])
    {
        StringList list=new StringList(list1); 
        list.merge(list2); 
        list.merge(list3); 
        return list; 
    }
    
    /**
     * Remove elements from this list.  
     *  
     * Polymorphism note: This method overrides remove(Object o) *only*
     * for String Arrays. 
     */  
    public void remove(String els[])
    {
        if (els==null) 
            return;
        
        for (String el:els)
            this.remove(el); 
    }
    
    /**
     * Add all elements. Does not check for duplicates.
     * NULL objects are omitted!
     */ 
    public void add(String[] els)
    {
        if (els==null)
            return; 
        
        for (String el:els)
        	if (el!=null)
        		this.add(el); 
    }
    
    /** Add element if not in the list already.*/ 
    public void addUnique(String el)
    {
        add(el,true); 
    }
    
    /**
     * Add String, if unique==true the entry won't 
     * be added if it already exists. 
     */
    public boolean add(String str,boolean unique)
    {
        if (unique)
        {
            if (find(str)<0)
            {
                add(str);
                return true; 
            }
            return false; 
        }
        else
        {
            add(str);
            return true; 
        }
    }
    /**
     * Add Elements if NOT already in this list.
     * Returns number of elements really added 
     */ 
    public int merge(String[] els)
    {
        if (els==null)
            return 0; // nothing to merge 
        
        int numAdded=0; 
        
        for (String el:els)
        {
            if (el!=null)
            {
                if (this.contains(el)==false)
                {
                    this.add(el);
                    numAdded++; 
                }
            }
        }
        
        return numAdded; 
    }

    /** 
     * Sort stringlist according to newOrder array. 
     * Moves elements NOT in the newOrder array up to 
     * after the elements in specified newOrder array.  
     * (Does not remove entries not in order array). 
     */
    
    public void orden(String[] newOrder)
    {
        int p=0; 
        int dir=0; 
        
        boolean moveUp=true;         
        
        if (moveUp==true)
        {
            p=0; // begin 
            dir=+1; // move up 
        }
        else
        {
            p=this.size()-1; // end of list 
            dir=-1;  // move down. 
        }
        
        // O(M):: Sort element according template list
        for (int i=0;i<newOrder.length;i++)
        {
            String el=newOrder[i]; 
            int index=indexOf(el);
            
            if (index>=0) 
            {
                // swap p <=> index; 
                String org=get(p); 
                set(p,get(index)); // move up 
                set(index,org); // move org to old place:
                p+=dir; // move to next position. 
            }
        }
    }
    
    /**
     * Factory method to merge two array. 
     * Duplicates are removed. 
     */
    public static String[] merge(String[] arr1, String[] arr2)
    {
        StringList list=new StringList(arr1); 
        list.merge(arr2); 
        return list.toArray(); 
    }
    
    /**
     * Factory method to merge tree array. 
     * Duplicates are removed. 
     */
    public static String[] merge(String[] arr1, String[] arr2,String arr3[])
    {
        StringList list=new StringList(arr1); 
        list.merge(arr2); 
        list.merge(arr3); 
        return list.toArray(); 
    }

    /** Helper method to find a entry in a String Array */ 
    public static int find(String[] list, String value)
    {
       if ((list==null) || (list.length==0)) 
           return -1; 
        
       for (int i=0;i<list.length;i++)
       {
           if (value==null)
           {        

               // null proof: allow ? 
               if (list[i]==null)
                   return i;
           }
           else if (list[i].compareTo(value)==0)
           {
               return i;
           }
       }
       
       return -1; 
    }

    public static boolean hasEntry(String[] list,String val) 
    {    
        return (find(list,val)>=0);  
    }

    public int find(String key)
    {
        return indexOf(key); 
    }

    /** 
     * Add elements from otherList if not already in this list. 
     * Returns number of elements really added. 
     */ 
    public int merge(StringList otherList)
    {
        if ((otherList==null) || (otherList.size()<=0))
            return 0;  
        
        int numAdded=0; 
        
        for (String el:otherList)
        {
            if (this.contains(el)==false)
            {
                this.add(el);
                numAdded++; 
            }
        }
        
        return numAdded; 
    }

    public int[] sort()
    {
        return QSort.sortStringList(this, false); 
    }
    
    /** 
     * Returns sorted copy.
     * Uses duplicate().sort() */ 
    public StringList createSorted()
    {
        StringList list = this.duplicate(); 
        list.sort();
        return list; 
    }
    
    public int[] sort(boolean ignoreCase)
    {
        return QSort.sortStringList(this, ignoreCase); 
    }

    public StringList duplicate()
    {
        return new StringList(this.toArray()); 
    }
    
    public StringList clone()
    {
        return duplicate(); 
    }
    
    /** Insert new value at position. */  
    public void insert(int insertPosition, String value)
    {
        this.add(insertPosition,value);
    }

    public void insert(int insertPosition, String[] names)
    {
        for (int i=0;i<names.length;i++)
        {
            this.add(i,names[i]);
        }
    }
    
    public int insertBefore(String header, String newHeader)
    {
        int index=this.indexOf(header);
        if (index<0)
            return -1; 
        
        insert(index,newHeader);
        
        return index; 
    }
    
    public int insertAfter(String header, String newHeader)
    {
        int index=this.indexOf(header);
        if (index<0)
            return -1; 
        
        insert(index+1,newHeader);
        
        return index; 
    }

    public int compare(StringList otherList)
    {
        int len=this.size();
        
        if (otherList.size()<len)
            len=otherList.size();
        
        for (int i=0;i<len;i++)
        {
            int val=get(i).compareTo(otherList.get(i)); 
            if (val!=0)
                return val; 
        }
        
        return this.size()-otherList.size(); 
    }

    
    /**
     * Removes all superfluous white space from String values. 
     * If this list was created from a comma seperated string list, 
     * but the list contains spaces, this method removes them.
     * 
     * @see StringUtil#stripWhiteSpace(String) 
     * @return whether any whitespace was removed. 
     */
    public void stripWhiteSpace()
    {
        for (int i=0;i<this.size();i++)
            this.set(i,StringUtil.stripWhiteSpace(get(i))); 
    }

}
