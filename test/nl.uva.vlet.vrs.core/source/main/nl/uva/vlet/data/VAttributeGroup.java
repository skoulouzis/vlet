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
 * $Id: VAttributeGroup.java,v 1.3 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

package nl.uva.vlet.data;

import java.util.Vector;

/**
 * An attribute group extends an attribute set by being able to have 
 * several attribute 'groups' as childs. 
 * An attribute group with no child groups is equal to an attribute set.  
 * The VAttributeGroup class is ment to hierarchically structure VAttributeSets.
 *  
 * @author P.T. de Boer
 */
public class VAttributeGroup extends VAttributeSet 
{
    private static final long serialVersionUID = -647182205081679713L;
    
    /** child groups */ 
    protected Vector<VAttributeGroup> groups=new Vector<VAttributeGroup>();
    
    /** parent group, if contained in another VAttributeGroup */ 
    protected VAttributeGroup parent=null; 
    
    public VAttributeGroup(String name)
    {
        super(name); 
    }
    
    /** Create group from set */ 
    public VAttributeGroup(VAttributeSet attrSet)
    {
        super(attrSet.getName());
        
        //duplicate attriubtes 
        
        VAttribute attrs[]=attrSet.toArray(); 
        for (VAttribute attr:attrs)
            this.put(attr.duplicate()); 
    }

    /** Add specified group to the child groups */ 
    public void addGroup(VAttributeGroup group)
    {
        groups.add(group);    
    }
 
    /** return actual (non-copy) vector which contains child groups */ 
    public  Vector<VAttributeGroup> getGroups()
    {
        return groups; 
    }
    
    /** Get group by name */
    public VAttributeGroup getGroup(String name)
    {
        if (name==null) 
            return null; 
        
        for (VAttributeGroup group:groups)
        {
            if (name.compareTo(group.getName())==0)
                return group; 
        }
        
        return null;
    }
    
    /** Return names of groups */ 
    public String[] getGroupNames()
    {
        String[] names=new String[groups.size()];
        
        for (int i=0;i<groups.size();i++)
        {
            names[i]=groups.elementAt(i).getName(); 
        }
        
        return names; 
    }

    public void addGroup(VAttributeSet attrSet)
    {
        addGroup(new VAttributeGroup(attrSet)); 
    }

    /** Set attribute to the specified ones */ 
    public void setAttributes(VAttributeSet attrs) 
    {
        this.clear();

        if (attrs==null) 
            return; 
        
        VAttribute[] attrArray = attrs.toArray();
        
        for (VAttribute attr:attrArray)
            this.put(attr); 
    }
}
