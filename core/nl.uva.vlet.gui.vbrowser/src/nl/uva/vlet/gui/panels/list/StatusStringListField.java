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
 * $Id: StatusStringListField.java,v 1.3 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.panels.list;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JList;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;

/** Selectable Host List with optional status */ 
public class StatusStringListField extends JList
{
    private static final long serialVersionUID = 7414325688160172011L;
    
    public static final String STATUS_ORIGINAL="Original"; 
    public static final String STATUS_NEW="New";
    public static final String STATUS_REMOVED="Removed";
    
    // =========================//
    
    
    public StatusStringListField()
    {
        super(new StatusStringListModel()); 
        init(); 
    }

    private void init()
    {
        this.setCellRenderer(new StringListCellRenderer()); 
    }
    
    public StatusStringListModel getModel()
    {
        return (StatusStringListModel)super.getModel(); 
    }
    
    public void setListData(StringList list, boolean isOriginal)
    {
        getModel().setList(list); 

        if (isOriginal)
        {
            for (String s:list)
                this.getModel().setStatus(s,STATUS_ORIGINAL);
        }

    }
    
    public boolean isOriginal(String value)
    {
        String stat=this.getModel().getStatus(value); 
        if (stat==null)
            return false; 
        
        return StringUtil.equals(stat,STATUS_ORIGINAL);  
    }
    
    public boolean isNew(String value)
    {
        String stat=this.getModel().getStatus(value); 
        if (stat==null)
            return false; 
        
        return StringUtil.equals(stat,STATUS_NEW);  
    }
    
    public boolean isRemoved(String value)
    {
        String stat=this.getModel().getStatus(value); 
        if (stat==null)
            return false; 
        
        return StringUtil.equals(stat,STATUS_REMOVED);  
    }
    
    public String[] getSelectedValues()
    {
        Object objs[]=super.getSelectedValues(); 
        
        String strs[]=new String[objs.length];
        // convert *any* to string 
        for (int i=0;i<objs.length;i++)
            strs[i]=objs[i].toString();
        
        return strs;
    }
    
    public void addStrings(List<String> extra,boolean uniqueOnly)
    {
        getModel().addStrings(extra,uniqueOnly); 
    }
    
    /** Get Values from List model */ 
    public String[] getValues()
    {
        return getModel().getValues(); 
    }
    
    public void setStatus(String entry,String status)
    {
        this.getModel().setStatus(entry,status); 
    }
    
    public String getStatus(String entry)
    {
        return this.getModel().getStatus(entry); 
    }

    public void addElement(String el)
    {
        this.getModel().addElement(el); 
    }

    public boolean hasElement(String el)
    {
        return getModel().hasElement(el); 
    }

    public void removeElement(String se)
    {
        getModel().removeElement(se); 
    }
    
    public StringList getStringList()
    {
        return this.getModel().getStringList(); 
    }
    
   
}
