package nl.uva.vlet.gui.proxyvrs;

import nl.uva.vlet.error.InitializationError;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VRSContext;

/**
 * ProxyVRSClient. 
 * Client to the ProxyVRS. 
 * Under construction. All direct access to the VRS/VFS should be wrapped. 
 */
public class ProxyVRSClient 
{
	private static ProxyVRSClient instance=null; 

	// one instance for now: 
	public static synchronized ProxyVRSClient getInstance()
	{
		if (instance==null)
			instance=new ProxyVRSClient(UIGlobal.getVRSContext());
		
		return instance; 
	}
	
	// ========================================================================
	//
	// ========================================================================

	private VRSContext context;
	private ProxyResourceEventNotifier proxyEventNotifier=null;
	private ProxyNodeFactory _proxyNodeFactory=null;  
	
	protected ProxyVRSClient(VRSContext vrsContext)
	{
		this.context=vrsContext; 
		init();
	}

	protected void init()
	{
		proxyEventNotifier=new ProxyResourceEventNotifier(context); 
	}
	
	public void dispose() 
	{
		proxyEventNotifier.dispose(); 
		
		if (_proxyNodeFactory!=null)
			_proxyNodeFactory.reset(); 
	}

    // =======================================================================
    // ProxyNode Factory interface 
    // =======================================================================
    
    /** Set default ProxyNode Factory. Currently only one per instance supported */ 
    public void setProxyNodeFactory(ProxyNodeFactory factory)
    {
    	_proxyNodeFactory=factory;  
    }
    
    /** Get default ProxyNode Factory. Currently only one per instance supported */ 
    public ProxyNodeFactory getProxyNodeFactory()
    {
    	if (_proxyNodeFactory==null)
    		throw new InitializationError("Initiliziation error. Proxy Node Factory not set. Use ProxyTNode.init() first !!!"); 
    	
    	return _proxyNodeFactory; 
    }
    
    // ========================================================================
    // VRS 
    // ========================================================================
    
    public VRL getVirtualRootLocation() throws VlException
    {
        return context.getVirtualRootLocation();
    }
    
	// ========================================================================
	// ProxyResourceEvent interface
	// ========================================================================

	public ProxyResourceEventNotifier getProxyResourceEventNotifier()
	{
		return proxyEventNotifier; 
	}

	public void addResourceEventListener(ProxyResourceEventListener listener) 
	{
		proxyEventNotifier.addResourceEventListener(listener);		
	}
	
	public void removeResourceEventListener(ProxyResourceEventListener listener) 
	{
		proxyEventNotifier.removeResourceEventListener(listener);		
	}
	
	public void fireEvent(ResourceEvent event)
    {
		proxyEventNotifier.fireEvent(event); 
    }

    public ConfigManager getConfigManager()
    {
        return context.getConfigManager();
    }
}
