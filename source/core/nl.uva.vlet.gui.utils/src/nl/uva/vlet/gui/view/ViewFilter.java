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
 * $Id: ViewFilter.java,v 1.5 2011-06-10 10:18:03 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:03 $
 */ 
// source: 

package nl.uva.vlet.gui.view;

public class ViewFilter
{
	boolean sort=true; 
	
    String sortField1=null; 
    String sortField2=null;
    
    boolean inverseSort1=false; 
    boolean inverseSort2=false; 
    
    boolean ignoreCase1=false; 
    boolean ignoreCase2=false; 
        
    public boolean filterHidden=false;  

    public ViewFilter()
    {        
    }
    
    public void setSortField1(String name)
    {
        sortField1=name; 
    }
    
    public void setSortField2(String name)
    {
        sortField2=name; 
    }
    
    public String[] getSortFields()
    {
        if (sortField1==null)
            return null;
        
        if (sortField2==null)
            return new String[]{sortField1};
        
        return new String[]{sortField1,sortField2}; 
    }
    
     
    public void setFilterHidden(boolean val)
    {
        this.filterHidden=val; 
    } 
    
    public boolean doSort()
    {
    	return sort; 
    }
    
}
