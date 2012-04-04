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
 * $Id: StatusStringListModel.java,v 1.4 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.list;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import nl.uva.vlet.data.StringList;

public class StatusStringListModel extends DefaultComboBoxModel
{
    private static final long serialVersionUID = -5468761074875875543L;

    Map<String,String> statuses=new Hashtable<String,String>(); 
    
    public StatusStringListModel()
    {
        // empty list
        super(new String[]{""});
    }

    public StatusStringListModel(String[] hosts)
    {
        super(hosts);
    }
    
    public void setStatus(String entry, String status)
    {
        statuses.put(entry,status);
        int i=this.getIndexOf(entry);
        if (i>=0)
            this.fireContentsChanged(this,i,i); 
        
    }
    
    public String getStatus(String entry)
    {
        return statuses.get(entry);  
    }

    public void setList(List<String> list)
    {
        this.removeAllElements();
        // Note: will fire event per addElement();
        for(String s:list)
            this.addElement(s); 
    }
    
    /** Warning: does linear search */ 
    public boolean hasElement(String el)
    {   
        for (int i=0;i<this.getSize();i++)
            if (this.getElementAt(i).equals(el))
                return true;
        
        return false; 
    }
    
    public String getElementAt(int i)
    {
        Object obj=super.getElementAt(i);
        
        if (obj instanceof String)
            return (String)obj; 
        else 
            return obj.toString(); 
    }
    
    /** Returns values as StringList */ 
    public StringList getStringList()
    {
        return new StringList(getValues()); 
    }
    
    public boolean addStrings(List<String> extra,boolean uniqueOnly)
    {
        // get complete list: 
        StringList list = getStringList(); 
        boolean added=false;
        
        for(String s:extra)
            added=added|list.add(s,uniqueOnly);
        
        // update all at once (if changed) 
        if (added)
            this.setList(list);
        
        return added; 
    }

    public String[] getValues()
    {
        int len=getSize(); 
        String strs[]=new String[this.getSize()];
        
        for (int i=0;i<len;i++)
        {
            strs[i]=getElementAt(i); 
        }
        
        return strs; 
    }

}
