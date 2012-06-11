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
 * $Id: DropAction.java,v 1.4 2011-06-10 10:18:02 ptdeboer Exp $  
 * $Date: 2011-06-10 10:18:02 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.Component;
import java.awt.Point;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.view.VComponent;

/** 
 * Generic DropAction object.  
 * Instead of using the (limited) Swing DnD interface, Drag 'n Drops
 * between VComponents can be handled by using this 'DropAction' object.
 * <p> 
 * When a drop on a VComponent is performed the MasterBrowser is called 
 * to perform the actual Drop. 
 * 
 * @author P.T. de Boer
 *
 */
public class DropAction
{
	public static final String COPY_ACTION="Copy"; 
	public static final String MOVE_ACTION="Move"; 
	public static final String LINK_ACTION="Link"; 
	
    public ResourceRef sources[]=null;
    
    public VComponent destination=null;
    
    // public boolean isMove=false;
    // public boolean isAltDrop;       // ALT modifier selected.-> use modifiers
    
    /** component to perform interactive popupmenu. If NULL no Interactive Drop !! */
	public Component component=null;
	
	public Point point=null;
	
	public boolean interactive=true;   // defaults
	
	public String dropAction;
    
	public DropAction(VComponent dropTarget,ResourceRef[] dropSources) 
	{
        sources=dropSources; 
        destination=dropTarget;  
	}

	
    public DropAction(VComponent dropTarget,ResourceRef dest)
    {
        sources=new ResourceRef[1]; 
        sources[0]=dest;
        destination=dropTarget;
    }

	public boolean isLink()
	{
		return StringUtil.equals(dropAction,LINK_ACTION); 
	}
	
	public boolean isMove()
	{
		return StringUtil.equals(dropAction,MOVE_ACTION); 
	}
	
	public boolean isCopy()
	{
		return StringUtil.equals(dropAction,MOVE_ACTION); 
	}
    
    /*public DropAction(VRL sources[],VRL dest)
    {
        this.isMove=false; 
        this.sources=sources; 
        this.destination=dest;  
    }

    public DropAction(VRL source,VRL dest,boolean isMove)
    {
        this.isMove=isMove; 
        sources=new VRL[1]; 
        sources[0]=source;
        destination=dest;  
    }*/
}
