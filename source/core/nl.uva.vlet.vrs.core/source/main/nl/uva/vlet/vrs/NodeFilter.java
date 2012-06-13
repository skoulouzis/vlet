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
 * $Id: NodeFilter.java,v 1.3 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.uva.vlet.util.Wildcard2Regex;
import nl.uva.vlet.vfs.VFSNode;

/** 
 * java.io.FileFilter compatible NodeFilter. 
 * Use by VDir.list(...) methods. 
 * @author P.T. de Boer 
 */
public class NodeFilter implements FileFilter
{
	private Pattern pattern;
	private boolean usePath=false;
	
	/** Create a new filter from the specified Wildcard expression. 
	 * Note that there is a difference between Wildcard expressions, 
	 * "*.txt" and Regular expressions: ".*txt". 
	 * <br>
	 * The latter or more powerfull in matching. 
	 * Standard Unix Scripts (like bash) use wildcarding! 
	 */ 
	public NodeFilter(String patternStr)
	{
		init(patternStr); 
	}
	
	private void init(String patternStr)
	{
		pattern=Pattern.compile(patternStr); 
	}
	private void init(Pattern patVal)
	{
		pattern=patVal; 
	}

	/** Create a new filter from the pattern. 
	 * Specify whether the pattern is a Regular Expression (.*txt 
	 * or a WildCard (*.txt) pattern. 
	 */ 
	public NodeFilter(String pattern,boolean isRE)
	{
		if (isRE==false) 
			init(Wildcard2Regex.wildcardToRegex(pattern));
		else
			init(pattern); 
	}
	
	public NodeFilter(Pattern pattern2) 
	{
		init(pattern);
	}

	public boolean accept(VNode node)
	{
		String name;
		
		if (usePath==true)
			name=node.getPath();
		else
			name=node.getName();
		
		return accept(name);
	}
	
	//from java.io.FileFilter 
	
	public boolean accept(File file)
	{
		String name;
	
		if (usePath) 
			name=file.getAbsolutePath();
		else
			name=file.getName();
		
		return accept(name);
	}
	
	public boolean accept(String name)
	{
		Matcher m=pattern.matcher(name);  
		return m.matches(); 
	}
	
	/** Whether to match full path or basename only */ 
	public void setUsePath(boolean value)
	{
		this.usePath=value;
	}
	
	// =======================================================================
	// Factory methods: 
	// ========================================================================
	
	/** Create filter which matches wildcard patterns: "*.txt" for ".txt" */ 
	public static NodeFilter createWildcardPattern(String pattern)
	{
		return new NodeFilter(pattern,false); 
	}
	/** Create filter which matches RE patterns: ".*\.txt" for ".txt" */
	public static NodeFilter createREPattern(String pattern)
	{	
		return new NodeFilter(pattern,true); 
	}

	public static VNode[] filterNodes(VNode[] nodes, String filter, boolean isRE)
	{
		return filterNodes(nodes,new NodeFilter(filter,isRE)); 
	}

	public static VFSNode[] filterNodes(VFSNode[] nodes, NodeFilter filter)
	{
		// nothing to filter
    	if (nodes==null)
    		return null;
    	
	    // no filter 	
    	if (filter==null)
    		return nodes; 
	    	
    	Vector<VFSNode>filtered=new Vector<VFSNode>();
    	
    	for (VFSNode node:nodes) 
    		if (filter.accept(node))
    			filtered.add(node); 

    	VFSNode _nodes[]=new VFSNode[filtered.size()];
    	_nodes=filtered.toArray(_nodes); 
    	return _nodes; 
	}
	
	public static VNode[] filterNodes(VNode[] nodes, NodeFilter filter)
	{
    	if (nodes==null)
    		return null;
	    	
    	if (filter==null)
    		return nodes; 
	    	
    	Vector<VNode>filtered=new Vector<VNode>();
    	
    	for (VNode node:nodes) 
    		if (filter.accept(node))
    			filtered.add(node); 

    	VNode _nodes[]=new VNode[filtered.size()];
    	_nodes=filtered.toArray(_nodes); 
    	return _nodes; 
	}
	
}
