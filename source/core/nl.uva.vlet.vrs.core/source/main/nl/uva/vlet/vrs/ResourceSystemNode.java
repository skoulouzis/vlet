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
 * $Id: ResourceSystemNode.java,v 1.3 2011-06-07 14:30:45 ptdeboer Exp $  
 * $Date: 2011-06-07 14:30:45 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * ResourceSystemNode is a VResourceSystem adaptor class.  
 * It is a 'browsable' node (VCompositeNode) which is linked to an 
 * actual ResourceSystem implementation. 
 * It is recommended that VResourceSystem implementations extends this ResourceSystemNode.
 * For VFileSystem implementation see the VFileSystemNode adaptor class. 
 *   
 * @author P.T. de Boer
 */
public abstract class ResourceSystemNode extends VCompositeNode implements VResourceSystem
{
	public static final String ATTR_SERVERID="serverID";
	
	public static final String ATTR_ISCONNECTED="isConnected"; 
	
	private static String[] serverAttributeNames=
	{
		ATTR_SERVERID,
		ATTR_ISCONNECTED
	};

	/** Creates simple scheme+host+port ID */ 
	public static String createServerID(String scheme, String hostname, int port)
	{
		return "serverid:" + scheme + "@" + hostname + ":" + port;
	} 

	/** Creates scheme+host+port ID from VRL */
	public static String createServerID(VRL loc)
	{
		return createServerID(loc.getScheme(),loc.getHostname(),loc.getPort()); 
	}
	
	// =======================================================================
	//
	// =======================================================================
	
	/** Unique ResourceSystym ID, used for caching purposes */ 
	protected String serverID=null; 
	
	/** ServerInfo associated with this ResourceSystem */ 
	private ServerInfo serverInfo;
	
	/** Extra instance attributes */ 
	protected VAttributeSet instanceAttributes=new VAttributeSet(); 
	
	public String getType()
	{
		return "Server"; 
	}
	
    public ResourceSystemNode(VRSContext context, ServerInfo info)
	{
		super(context, info.getServerVRL());
		
		// final ! 
		this.serverInfo=info;
	}
    
    /** Default implementation is to browse the remote server home  */ 
    public VNode[] getNodes() throws VlException
    {
    	VNode node=this.openLocation(getServerHomeVRL());
    	
    	if (node instanceof VComposite)
    		return ((VComposite)node).getNodes(); 
    	
    	VNode nodes[]=new VNode[0]; 
    	nodes[0]=node;
    	
    	return nodes;
    }
    
    /** Returns Server VRL including home path if defined */ 
    public VRL getServerHomeVRL()
    {
		if (serverInfo==null)
			return null; 
		
    	String path=this.serverInfo.getDefaultPath();
        VRL vrl=this.getServerVRL().copyWithNewPath(path);
        return vrl; 
    }
	
	public String[] getResourceTypes()
	{
		return null;
	}

	/**
	 * Return updated ServerInfo object. 
	 * This object might be changed during calls if the configuration changed. 
	 * Be careful not to cache this object, but always use getServerInfo
	 * 
	 */
	public ServerInfo getServerInfo()
	{
		// Current bug in ServerInfo: update to get new info object. 
		ServerInfo newInfo=getContext().getServerInfoRegistry().getServerInfo(serverInfo.getID());
	    if (newInfo!=null)
	    	 this.serverInfo=newInfo; 

	     return serverInfo; 
	}

	public VRSContext getContext()
	{
		return this.vrsContext;
	}
	
	public String getName()
	{
		return getScheme()+"://"+this.getHostname()+":"+this.getPort();  
	}
	
	/** Returns ServerInfo VRL */ 
	public VRL getServerVRL()
	{
		if (serverInfo==null)
			return null; 
		
		return this.serverInfo.getServerVRL(); 
	}
	
	public String getID()
	{ 
		if (serverID==null)
		{
			// auto init with ServerInfo ! 
			serverID=this.getServerInfo().getID(); 
		}
		
		return serverID; 
	}
	
	public String setID(String id)
	{
		return serverID=id; 
	}
	
	public String[] getAttributeNames()
	{
		String names[]=super.getAttributeNames(); 
		StringList list=new StringList(names);
		
		if (serverInfo!=null)
		{
			list.merge(serverInfo.getAttributeNames()); 
		}
		
		list.merge(serverAttributeNames);
		list.merge(this.instanceAttributes.getAttributeNames()); 
		
		return list.toArray(); 
	}
	
	public VAttribute getAttribute(String name) throws VlException 
	{
		if  (StringUtil.compare(name, ATTR_SERVERID)==0) 
		{
			return new VAttribute(name,getID());
		}	
		else if  (StringUtil.compare(name, ATTR_ISCONNECTED)==0) 
		{
			return new VAttribute(name,isConnected());
		}	
		
		VAttribute attr=this.serverInfo.getAttribute(name);
		
		if (attr!=null)
			return attr;
		
		// check instance attributes !
		
		attr=this.instanceAttributes.get(name);
		
		if (attr!=null) 
			return attr; 
			
		return super.getAttribute(name);
	}
	
	protected void putInstanceAttribute(VAttribute attr)
	{
		this.instanceAttributes.put(attr.getName(),attr); 
	}
	
	protected void putInstanceAttribute(String name, String value) 
	{
		this.instanceAttributes.put(new VAttribute(name,value));
	}
	
	protected VAttribute getInstanceAttribute(String name)
	{
		return this.instanceAttributes.get(name);  
	}
	
	//=== Abstract Methods === 
	abstract public VNode openLocation(VRL vrl) throws VlException;
	
	// ===
	// Explicit declarations to allow for 1.5 @Override directive.
	// ===
	
    public abstract boolean isConnected(); 
    
    public abstract void connect() throws VlException;
    
    public abstract void disconnect() throws VlException; 
    
}
