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
 * $Id: VQueue.java,v 1.3 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.vjs;

import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;


public abstract class VQueue extends VCompositeNode
{
		
	
	public VQueue(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

	public String getType()
	{
		return VJS.TYPE_VQUEUE; 
	}
	
	public VNode getNode(String name) throws VlException
	{
		return getJob(name); 
	}
	
	public VNode[] getNodes() throws VlException
	{
		return getJobs(); 
	}
	
	public boolean hasNode(String name) throws VlException
	{
		return hasJob(name);
	}
	
	public boolean hasJob(String name) throws VlException
	{
		VJob jobs[]=getJobs();
		
		if (jobs==null) 
			return false; 
		
		for (VJob job:jobs) 
		{
			if (job.getName().compareTo(name)==0) 
				return true; 
		}
		
		return false; 
	}
	
	public boolean isAccessable()
	{
		return true;
	}

	public boolean isDeletable() throws VlException
	{
		return false;
	}	
	
	public VAttribute[][] getNodeAttributes(String[] jobNames, String[] attrNames) throws VlException
	{
		return getJobAttributes(jobNames,attrNames); 
	}

	public VAttribute[][] getNodeAttributes(String[] attrNames) throws VlException
	{
		return getJobAttributes(attrNames); 
	}
	
	/** Get job by Name */ 
	public VJob getJob(String name) throws VlException
	{
		VJob jobs[]=getJobs();
		
		if (jobs==null) 
			return null;
		
		for (VJob job:jobs) 
		{
			if (job.getName().compareTo(name)==0) 
				return job; 
		}
		
		return null; 
	}
	
	public VJob addNode(VNode node, boolean isMove) throws VlException
	{
		if ((node instanceof VJob)==false)  
			throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+node); 
			
		return schedule((VJob)node); 
	}

	public VJob addNode(VNode node, String newName, boolean isMove) throws VlException
	{
		if ((node instanceof VJob)==false)  
			throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+node); 
		
		return schedule((VJob)node); 
	}

	public VJob[] addNodes(VNode[] nodes, boolean isMove) throws VlException
	{
		if (nodes==null) 
			return null; 
		
		if ((nodes instanceof VJob[])==false) 
			throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+nodes); 
		
		VJob jobs[]=new VJob[nodes.length];
		
		int index=0;
		
		for (VNode node:nodes)	
			jobs[index++]=(VJob)addNode(node,false);
		
		return jobs; 
	}

	public VJob createNode(String type, String name, boolean force) throws VlException
	{
		throw new nl.uva.vlet.exception.NotImplementedException("Cannot create empty job"); 
	}
	
	/** Delete Node = abort job */
	public boolean delNode(VNode node) throws VlException
	{
		return false;
	}
	
	/** Delete Nodes = abort jobs */
	public boolean delNodes(VNode[] nodes) throws VlException
	{
		return false;
	}

	public boolean delete(boolean recurse) throws VlException
	{
		return false;
	}
	
	//=========================================================================
	// Abstract interface 
	//=========================================================================
	
	public abstract VAttribute[][] getJobAttributes(String[] attrNames) throws VlException; 
	
	public abstract VAttribute[][] getJobAttributes(String[] jobNames, String[] attrNames) throws VlException;
	
	public abstract VJob[] getJobs() throws VlException; 

	public abstract VJob schedule(VJob job) throws VlException; 
}
