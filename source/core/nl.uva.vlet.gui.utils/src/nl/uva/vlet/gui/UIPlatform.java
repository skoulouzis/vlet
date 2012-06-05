package nl.uva.vlet.gui;

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
	//
	// ========================================================================
	
	
	protected UIPlatform()
	{
		
	}
	
}
