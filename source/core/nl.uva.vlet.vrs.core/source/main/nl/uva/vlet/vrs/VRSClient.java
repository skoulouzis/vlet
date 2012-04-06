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
 * $Id: VRSClient.java,v 1.10 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;

/** 
 * VRSClient is the main client class to the VRS services. 
 * It provides methods to the different VRS interfaces and implementations.
 * <p>
 * Use  method <code>openLocation()</code> to get any resource  
 * anywhere on the grid.
 * First create your local VRS handler object which interacts with the VRS services.
 * 
 * @see nl.uva.vlet.vfs.VFSClient VFSClient for VFS methods 
 * 
 * @author P.T. de Boer 
 */
public class VRSClient
{
	// ===
	// Class
	// ===
	
	private static VRSClient defaultVRSClient=new VRSClient(); 

	/** Returns default class object */ 
    public static VRSClient getDefault()
    {
        return defaultVRSClient;
    }
    
    
	/** Resolve relativeVRL to baseVRL */ 
	public static VRL resolve(VRL baseVRL, String relativeVRL)
			throws VRLSyntaxException
	{
		return baseVRL.resolvePath(relativeVRL);
	}

	// ===
	// Instance
	// ===

	private VRSContext vrsContext;
	
    private ResourceLoader resourceLoader;

	public VRSClient()
	{
		init();
		// will trigger initialization of VRS 
		this.vrsContext=VRS.getDefaultVRSContext();  
	}

	public VRSClient(VRSContext context)
	{
		this.vrsContext = context;
		init();
	}

	private void init()
	{
		// already done in super: 
		// Global.init();
		Global.debugPrintf(this,"--- INIT ---\n"); 
	}

	/**
	 * Returns Resource Context associated with this client. If non was
	 * specified during creating, this method returns the Global VRSContext. <br>
	 * Use setVRSContext(new VRSContext) to customize your VRSContext.
	 * 
	 * @return Global (default) VRSContext.
	 */

	final public VRSContext getVRSContext()
	{
		return this.vrsContext;
	}

	/**
	 * Sets new Resource Context associated with this client. Note that this
	 * context only aplies for NEW created VNodes. Other created resources
	 * still will use the old context. Preferably set this context before doing
	 * any other VRS calls !
	 */
	final public void setVRSContext(VRSContext context)
	{
		this.vrsContext = context;
	}

        /** Open remote location and return VNode */ 
	public VNode openLocation(VRL location) throws VlException
	{
		return this.vrsContext.openLocation(location);
	}

	public VNode openLocation(String locationString) throws VlException
	{
		return this.vrsContext.openLocation(locationString);
	}

	/**
	 * Returns VNode associated with remote location. This method is mostly used
	 * by the other get() methods so they can check the implementation type
	 */
	public VNode getNode(VRL location) throws VlException
	{
		return vrsContext.openLocation(location);
	}

	/**
	 * Set specified property for this context. returns previous value.
	 */
	public Object setProperty(String name, String value)
	{
		return this.vrsContext.setProperty(name, value);
	}

	/**
	 * Set specified property for this context. returns previous value.
	 */
	public Object setProperty(String name, int value)
	{
		return this.vrsContext.setProperty(name, value);
	}

	public Object getProperty(String name)
	{
		return this.vrsContext.getProperty(name);
	}

	public String getStringProperty(String name)
	{
		return this.vrsContext.getStringProperty(name);
	}

	/**
	 * Set specified property for this context. returns previous value.
	 */
	public Object setProperty(String name, Object value)
	{
		return this.vrsContext.setProperty(name, value);
	}

	/** 
	 * Generic InputStream Factory method. 
	 * @throws VlException if stream could not be opened or resource doesn't support InputStreams. 
	 */ 
	public InputStream openInputStream(VRL loc) throws VlException
	{
		// check new StreamProducer interface !  
		VResourceSystem rs = openResourceSystem(loc); 
		if (rs instanceof VInputStreamProducer)
		{
			return ((VInputStreamProducer)rs).openInputStream(loc); 
		}
		
		// use default method: 
		VNode node=openLocation(loc);
		
		if (node instanceof VStreamReadable) 
			return ((VStreamReadable)node).getInputStream();
			
		throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource doesn't support InputStream method(s) (VNode is not StreamReadable):"+loc);
	}
	
	/**
	 * Generic OutputStream Factory method 
	 * @throws VlException if stream could not be opened or resource doesn't support OutputStreams.  
	 */
	public OutputStream openOutputStream(VRL loc) throws VlException
	{
		// check new StreamProducer interface !  
		VResourceSystem rs = openResourceSystem(loc); 
		if (rs instanceof VOutputStreamProducer)
		{
			return ((VOutputStreamProducer)rs).openOutputStream(loc); 
		}
		
		// use default method: 
		VNode node=openLocation(loc);
		
		if (node instanceof VStreamWritable) 
			return ((VStreamWritable)node).getOutputStream();
			
		throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Resource doesn't support OutputStream method(s) (VNode is not StreamWritable):"+loc);
	}

	/** 
	 * Returns ResourceSystem for the remote location 
	 * @throws VlException 
	 */ 
	public VResourceSystem openResourceSystem(VRL loc) throws VlException
	{
		return vrsContext.openResourceSystem(loc); 
	}

	
	/**
	 * VRS Method to Query BDII Service for specified ServiceInfoType and VO.  
	 * @throws VlException 
	 */ 
	public ArrayList<ServiceInfo> queryServiceInfo(String vo, ServiceInfo.ServiceInfoType type) throws VlException
	{
	    BdiiService bdii = this.vrsContext.getBdiiService();
	    return bdii.queryServiceInfo(vo,type); 
	}

	/** 
	 * Returns Resource Loader utility associated with this VRSClient. 
	 */
    public ResourceLoader getResourceLoader()
    {
        if (this.resourceLoader==null)
            this.resourceLoader=new ResourceLoader(this.vrsContext);
        
        return this.resourceLoader; 
    }


    /** 
     * Generic method to list the contents of a resource.
	 * If resource is NOT composite, NULL will be returned. 
	 */ 
    public VNode[] list(VRL theVrl) throws VlException 
    { 
        VNode node=this.getNode(theVrl);
        
        if (node instanceof VComposite)
            return ((VComposite)node).getNodes(); 
        
        return null;
    }
	
    /** 
     * Close and dispose all resources associated with this VRSClient. 
     */ 
    public void dispose()
    {
        this.vrsContext.dispose(); 
    }
    
}
