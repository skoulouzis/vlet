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
 * $Id: ResourceEvent.java,v 1.7 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import java.io.Serializable;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.vrl.VRL;

/**
 *  VRS Events.
 */ 
public class ResourceEvent implements Serializable
{
    private static final long serialVersionUID = -4607498098228764849L;
    
    // ==============================================================================
    // Static Event Producers  
    // ==============================================================================

    public static ResourceEvent createChildAddedEvent(VRL node, VRL child)
    {
        ResourceEvent e=new ResourceEvent(EventType.CHILDS_ADDED);
        
        if (node==null) 
            throw new NullPointerException("Parent VRL can not be null!");
        
        if (child==null) 
            throw new NullPointerException("Child VRL can not be null");  
        
        e.location=node; 
        e.argumentVRLs=new VRL[1];
        e.argumentVRLs[0]=child;
        
        return e; 
    }
    
    public static ResourceEvent createNewResourceEvent(VRL resource) 
    {
        return createNewResourceEvent(resource,null); 
    }
    
    /**
     * Create resource event with an optional logical Parent VRL.
     * Use this factory method if the logical parent VRL differs from (VRL)resource.getParent()!
     */ 
    public static ResourceEvent createNewResourceEvent(VRL resource,VRL optParent) 
    {
        ResourceEvent e=new ResourceEvent(EventType.NEW_RESOURCE);
        
        e.location=resource;
        
        if (optParent!=null)
        {
            e.argumentVRLs=new VRL[1];
            e.argumentVRLs[0]=optParent;
        }
        
        return e; 
    }
    
    public static ResourceEvent createChildsAddedEvent(VRL node, VRL[] childs)
    {
        ResourceEvent e=new ResourceEvent(EventType.CHILDS_ADDED);
        
        if (node==null) 
            throw new NullPointerException("Parent VRL can not be null!");
        
        if (childs==null) 
            throw new NullPointerException("Child VRLs can not be null");
        
        if (childs.length<=0)  
            throw new NullPointerException("Child VRL array can not be empty");  
        
        
        e.location=node; 
        e.argumentVRLs=childs;
        
        return e; 
    }
    
    public static ResourceEvent createSetChildsEvent(VRL node, VRL[] childs)
    {
        
        if (node==null) 
            throw new NullPointerException("Parent VRL can not be null!");
        
        if (childs==null) 
            throw new NullPointerException("Child VRLs can not be null");
        
        if (childs.length<=0)  
            throw new NullPointerException("Child VRL array can not be empty");  
        
        ResourceEvent e=new ResourceEvent(EventType.SET_CHILDS);
        
        e.location=node; 
        e.argumentVRLs=childs;
        
        return e; 
    }

    public static ResourceEvent createDeletedEvent(VRL node)
    {
        return new ResourceEvent(EventType.DELETE,node);
    }
    
    public static ResourceEvent createDeletedEvent(VRL node,VRL optParentVRL)
    {
        ResourceEvent event=new ResourceEvent(EventType.DELETE,node);
        event.argumentVRLs=new VRL[]{optParentVRL};  
        return event; 
    }

    public static ResourceEvent createRefreshEvent(VRL node2)
    {
        return new ResourceEvent(EventType.REFRESH,node2);
    }

    /**
     * Create Rename event. Both old and new VRLs must be supplies
     * as well a the new name, since the name might not be part of the new VRL! 
     * @return
     */
    public static ResourceEvent createRenameEvent(VRL oldLocation, VRL newLocation,String newName) 
    {
        ResourceEvent e=new ResourceEvent(EventType.RENAME);
        
        e.location=oldLocation; 
        e.newLocation=newLocation;
        e.attributes=new VAttribute[1];
        e.attributes[0]=new VAttribute(VAttributeConstants.ATTR_NAME,newName); 
        
        return e; 
    }

    public static ResourceEvent createAttributesChangedEvent(VRL node, VAttribute[] attrs)
    {
        if (node==null)  
            throw new NullPointerException("Source node can not be NULL!");
        
        if ((attrs==null) || (attrs.length<=0)) 
            throw new NullPointerException("Attribute list cannot be null or be an empty list!");
        
        // Check for misbehaving resource implementations! 
        for (VAttribute attr:attrs)
            if (attr==null) 
                throw new NullPointerException("Attribute list cannot have NULL members!");
        
        ResourceEvent e=new ResourceEvent(EventType.SET_ATTRIBUTES);
        e.location=node;  
        e.attributes=attrs; 
        
        return e; 
    }

    public static ResourceEvent createSetAttributeEvent(VRL source, String name, String value)
    {
        VAttribute attrs[]=new VAttribute[1]; 
        attrs[0]=new VAttribute(name,value); 
        
        return createAttributesChangedEvent(source,attrs); 
    }

    public static ResourceEvent createBusyEvent(VRL node, boolean val)
    {
        ResourceEvent e=new ResourceEvent(EventType.SET_BUSY);
        e.boolVal=val;
        e.location=node;
        return e;
    }
    
    public static ResourceEvent createMessageEvent(VRL node, String message)
    {
        ResourceEvent e=new ResourceEvent(EventType.MESSAGE);
        e.location=node;
        e.message=message;
        return e;
    }
    
    // ==============================================================================
    // ResourceEvent   
    // ==============================================================================
    
    /** The Event Type */ 
    protected EventType eventType=EventType.NO_EVENT;  
    
    /** Single node or list of nodes for which this event applies */ 
    protected VRL location=null; 
    
    /** newLocation for rename events (old location=this.location)  */  
    protected VRL newLocation;  
    
    /** child locations if applicable */ 
    protected VRL argumentVRLs[]=null;
    
    /** Parent VRL if Applicable */ 
    // public VRL parent=null;   
    // For VAttribute Event: 
    
    //public VAttribute attribute=null;

    protected VAttribute[] attributes=null;
    
    protected boolean boolVal=false;
    
    protected String message=null;

    // Event is targeted to specific listener:
    protected ResourceEventListener specificListener;

    private String customType;

    private long eventTime=-1;
        
    private ResourceEvent(EventType type)
    {
        eventType=type; 
        this.eventTime=System.currentTimeMillis(); 
    }
    
    private ResourceEvent(EventType type,VRL sourceLocation)
    {
        this.location=sourceLocation; 
        eventType=type; 
        this.eventTime=System.currentTimeMillis();
    }

    /** Get Message String */ 
    public String getMessage()
    {
        return message; 
    }
    
    public EventType getType()
    {
        return eventType;
    }

    /** Time of Event */ 
    public long getTime()
    {
        return this.eventTime; 
    }
    
    public boolean isEvent(EventType otherType)
    {
        return (this.eventType==otherType);  
    }
    
    /**
     * Source of the Event. In case of a rename this is the OLD VRL, 
     * The new Location can be fetched using getNewVRL().
     */ 
    public VRL getSource()
    {
        return location; 
    }

    public String toString()
    {
        String str="{ResourceEvent:"+eventType; 
        
        if (location==null) 
        {
            str+=",<null node>"; 
        }
        else 
        {
            str+=",{nodeLocation="+location+"}"; 
        }
        
        if (this.argumentVRLs!=null) 
        {
            str+="{vrls=";
            
            int i=0;
            // do not print more then 3:
            for (i=0;((i<this.argumentVRLs.length)&& (i<3));i++)
            {
                if (i>0) 
                    str+=","; 
                
                str+=argumentVRLs[i];
            }
            
            if (argumentVRLs.length>i) 
                str+=",..."; 
            
            str+="}";
        }
        
        //if (parent!=null) str+=",{parent="+parent+"}"; 
        if (newLocation!=null) str+=",{newLocation="+newLocation+"}"; 
        
        str+="}";
        
        return str; 
    } 
    
    /** Return specific listener to which listener this event was targetted */    
    public ResourceEventListener getTargetListener()
    {
        return this.specificListener; 
    }
    
    /**
     * Returns Child VRLs in the case of  CHILD_ADDED or CHILD_DELETED event. 
     * Returns NULL otherwise */ 
    public VRL[] getChilds()
    {
    	// only return Child VRLs for the correct Event Type 
        switch(this.eventType)
        {
        	case CHILDS_ADDED:
        	case CHILDS_DELETED:
        	case SET_CHILDS: 
        		return argumentVRLs;
    		default: 
    			return argumentVRLs;
        }
    }
    
    /**
     * Returns Optional Parent VRL is specified. 
     * If this is a CHILD_ADDED or CHILD_DELETE Event the parent VRL 
     * can be fetched from getSource() and the child VRLs from getChilds().  
     * Returns NULL otherwise */ 
    public VRL getParent()
    {
        if ((argumentVRLs==null) || (argumentVRLs.length<=0))
            return null; 
            
        if (isEvent(EventType.CHILDS_ADDED) || isEvent(EventType.CHILDS_DELETED)) 
            return null; 
        
        if (isEvent(EventType.NEW_RESOURCE)) 
            return argumentVRLs[0]; 
        
        if (isEvent(EventType.DELETE)) 
            return argumentVRLs[0]; 
        
        return null;
    }
    
    /** Return the new VRL in the case of a RENAME event */ 
    public VRL getNewVRL()
    {
        return this.newLocation; 
    }

    /** Returns changed Attributes in the case of an ATTRIBUTE event. */  
    public VAttribute[] getAttributes()
    {
        return this.attributes;
    }
    
    /** Helper method to fetch specific Attribute */   
    public String getAttributeValue(String name)
    {
        if (this.attributes!=null)
            for (VAttribute attr:attributes)
                if (attr.hasName(name))
                    return attr.getStringValue();
        return null; 
    }
    
    public boolean getBoolVal()
    {
        return this.boolVal; 
    }

    /** Specify listener for which this event is meant */ 
    public void setTarget(ResourceEventListener receiver)
    {
        this.specificListener=receiver; 
    }

    /** Set custom type string for other custom resource event types */ 
    public void setCustomType(String typeStr)
    {
        this.customType=typeStr;
    }
    
    /** Return custom type string */ 
    public String getCustomType()
    {
        return customType; 
    }
    
    
}
