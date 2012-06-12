package nl.uva.vlet.gui;

import nl.uva.vlet.gui.GuiSettings.LookAndFeelType;

/** 
 * UI Platform. Typically one instance per application. 
 */
public class UIPlatform 
{
	private static UIPlatform instance; 
	
	public static synchronized UIPlatform getPlatform()
	{
		if (instance==null)
			instance=new UIPlatform();
		
		return instance; 
	}
	
	// ========================================================================
	// Instance 
	// ========================================================================
	
	private BrowserFactory browserFactory=null;
    private WindowRegistry windowRegistry=null;
	private boolean appletMode=false;
	
	// ========================================================================
    // Pre INIT 
    // ========================================================================
	
	protected UIPlatform()
	{
		init(); 
	}
	
	protected void init()
	{
	    windowRegistry=new WindowRegistry(); 
	}
	
	/** Set applet mode. Must be one of the first method to be called. */ 
    public void setAppletMode(boolean val)
    {
       this.appletMode=val; 
    }
    
    public boolean getAppletMode()
    {
        return appletMode; 
    }
    
	public void registerBrowserFactory(BrowserFactory factory)
	{
	    if (browserFactory!=null)
	        throw new Error("registerBrowserFactory(): Sorry can only register one browserFactory per (UI)Platform"); 
	    this.browserFactory=factory;
	}
	
	// ========================================================================
	// Post INIT 
	// ========================================================================
	
	public BrowserFactory getBrowserFactory()
	{
	    return browserFactory; 
	}
	
	public WindowRegistry getWindowRegistry()
	{
	    return windowRegistry; 
	}	
	
	// ========================================================================
	// UI Stuff 
	// ========================================================================
	
	public GuiSettings getGlobalGuiSettings()
	{
	    return GuiSettings.getDefault(); 
	}

    public void switchLookAndFeel(String lafstr)
    {
        switchLookAndFeel(LookAndFeelType.valueOf(lafstr),true); 
    }   

	/** Switch LAF */ 
	public void switchLookAndFeel(LookAndFeelType newtype,boolean permanent)
	{
	    //String oldval=getGlobalGuiSettings().getProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL); 
	    GuiSettings.switchLookAndFeelType(newtype); // switch
	    if (permanent)
	        getGlobalGuiSettings().setProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL,newtype.toString()); 
	}       
    
	/** Start Custom Look and Feel is defined */ 
	public void startCustomLAF()
    {
        String lafstr=getGlobalGuiSettings().getProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL);
        
        if (lafstr!=null)   
            GuiSettings.switchLookAndFeelType(lafstr); 
    }

	
}
