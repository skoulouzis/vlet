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
 * $Id: ResourceRef.java,v 1.3 2011-04-18 12:27:16 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:16 $
 */ 
// source: 

package nl.uva.vlet.gui.data;

import java.io.Serializable;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.vrl.VRL;

/**
 * ResourceRef is a extended VRL. 
 * It contains type information of the Resource. 
 * This to be able to do action matching when Dragging resources
 * from and to a 'known' VComponent.
 * When a resource is dragged from an external source (FireFox link) 
 * the type and mimetype might be null. 
 * Default type of a resource is VRL URI or String. 
 */
public class ResourceRef implements Serializable
{
	private static final long serialVersionUID = -3943831830772561493L;

	private VRL vrl=null; 
	
	private String resourceType=null; // when null -> default to VRL/URI/String (in that order) 

	private String mimeType=null; 
	
	public ResourceRef(VRL vrlVal, String typeVal,String mimeTypeVal)
	{
		// assert 
		if (vrlVal==null)
			throw new NullPointerException("VRL field in ResourceRef may never be null"); 
		
		this.vrl=vrlVal;
		
	    // VRS is now using typed Resources !  
        if (typeVal==null)
            typeVal=""; // empty -> don't care ? 
        
		this.resourceType=typeVal; 
		this.mimeType=mimeTypeVal;
	}
	
	public String toString()
	{
		String str=""; 
		
		if (StringUtil.isEmpty(resourceType))
			str="<?>:"; 
		else
			str+="<"+resourceType+">:"; 
		
		str+=vrl;
		
		return str; 
	}

	public static VRL[] getVRLs(ResourceRef[] refs)
	{
		if (refs==null)
			return null; 
		
		VRL vrls[]=new VRL[refs.length];
		
		for(int i=0;i<refs.length;i++)
			vrls[i]=refs[i].vrl; 
		
		return vrls; 
	}

	public static String[] getTypes(ResourceRef[] refs)
	{
		if (refs==null)
			return null; 
		
		String types[]=new String[refs.length];
		
		for(int i=0;i<refs.length;i++)
			types[i]=refs[i].resourceType;  
		
		return types; 
	}

	public static String[] getMimeTypes(ResourceRef[] refs)
	{

		if (refs==null)
			return null; 
		
		String types[]=new String[refs.length];
		
		for(int i=0;i<refs.length;i++)
			types[i]=refs[i].mimeType;  
		
		return types; 
	}
	
	public VRL getVRL()
	{
		return this.vrl;
	}
	
	public String getResourceType()
	{
		return this.resourceType; 
	}
	
	public String getMimeType()
	{
		return this.mimeType; 
	}

	
}
