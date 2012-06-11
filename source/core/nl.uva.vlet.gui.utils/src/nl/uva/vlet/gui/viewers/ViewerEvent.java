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
 * $Id: ViewerEvent.java,v 1.4 2011-04-18 12:27:11 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:11 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import nl.uva.vlet.vrl.VRL;

/** 
 * Viewer event are events origination from Viewer classes to notify
 * the MasterBrowser.
 */
public class ViewerEvent
{
    public enum ViewerEventType
    {
       HYPER_LINK_EVENT,          // explicit open/update location event
       LINK_FOLLOWED_EVENT ,      // Update Location Text field and history
       FRAME_LINK_FOLLOWED_EVENT, // Update Location Text field and history
       VIEWER_CLOSED_EVENT       // viewer has closed.  
    }
    
    public enum ViewOpenType
    {
       SAME,        // Keep in same Viewer if possible  
       OPEN_NEW     // open in new Tab/Window
    }
    
    public IMimeViewer viewer=null;
    public ViewerEventType type=null;
    public VRL location=null;
    private ViewOpenType openType;
    private Object frameLocation;
    
    public ViewerEvent(IMimeViewer sourceViewer, ViewerEventType etype)
    {
    	viewer=sourceViewer;
        type=etype; 
    }
    
    public ViewerEvent(ViewerEventType etype)
    {
    	viewer=null; 
        type=etype; 
    }
   
    public IMimeViewer getViewer()
    {
        return viewer;
    }   
    
    public String toString()
    {
        return ("ViewerEvent:"+type+",vrl="+location); 
    }
    
    public VRL getVRL()
    {
        return this.location;
    }
 
    public boolean isConsumed()
    {
        return false;
    }
 
    /**
     * Create HyperLink/UpdateLocation event. 
     * Is fired, for example, when a user clicks on a HyperLink 
     */ 
    public static ViewerEvent createHyperLinkEvent(IMimeViewer viewer,VRL location)
    {
        ViewerEvent event=new ViewerEvent(viewer,ViewerEventType.HYPER_LINK_EVENT);
        event.location=location; 
         
        return event;  
    }
    
    /**
     * Create HyperLink/UpdateLocation event. 
     * Is fired, for example, when a user clicks on a HyperLink 
     */ 
    public static ViewerEvent createLinkFollowedEvent(IMimeViewer viewer,VRL location)
    {
        ViewerEvent event=new ViewerEvent(viewer,ViewerEventType.LINK_FOLLOWED_EVENT);
        event.location=location; 
        return event;  
    }
    
    /**
     * Create HyperLink/UpdateLocation event. 
     * Is fired, for example, when a user clicks on a HyperLink 
     */ 
    public static ViewerEvent createHyperLinkEvent(VRL location)
    {
        ViewerEvent event=new ViewerEvent(ViewerEventType.HYPER_LINK_EVENT);
        event.location=location; 
         
        return event;  
    }
    
    /**
     * Creates Viewer Closed Event  
     */ 
    public static ViewerEvent createViewerClosedEvent(IMimeViewer viewer,VRL location)
    {
        ViewerEvent event=new ViewerEvent(viewer,ViewerEventType.VIEWER_CLOSED_EVENT);
        event.location=location; 
        return event;  
    }
    
    public static ViewerEvent createHyperLinkEvent(IMimeViewer viewer, VRL vrl, ViewOpenType openType)
    {
        ViewerEvent event=new ViewerEvent(viewer,ViewerEventType.HYPER_LINK_EVENT);
        event.location=vrl;
        event.openType=openType; 
        return event;  
    }

    public static ViewerEvent createFrameLinkFollowedEvent(ViewerPlugin viewer, VRL docVrl, VRL frameVrl)
    {
        ViewerEvent event=new ViewerEvent(viewer,ViewerEventType.FRAME_LINK_FOLLOWED_EVENT);
        event.location=docVrl;
        event.frameLocation=frameVrl;
        event.openType=ViewOpenType.SAME; 
        return event;  
    }

    public boolean isOpenAsNew()
    {
        if (openType==null)
            return false;
        
        return (openType.equals(ViewOpenType.OPEN_NEW)); 
    }
    
    public boolean isOpenInSameWindow()
    {
        if (openType==null)
            return false;
        
        return (openType.equals(ViewOpenType.SAME)); 
    }

}
