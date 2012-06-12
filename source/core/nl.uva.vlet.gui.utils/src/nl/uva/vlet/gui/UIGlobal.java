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
 * $Id: UIGlobal.java,v 1.10 2011-05-11 14:34:20 ptdeboer Exp $  
 * $Date: 2011-05-11 14:34:20 $
 */ 
// source: 

package nl.uva.vlet.gui;

import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.dialog.SimpleDialog;
import nl.uva.vlet.gui.icons.IconProvider;
import nl.uva.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.uva.vlet.net.ssl.CertificateStore;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.util.MimeTypes;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrs.Registry;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.ui.IMasterUI;

/** 
 * Shared Global environment for the UI objects (VBrowser, Viewers, etc.). 
 * 
 * @author P.T. de Boer
 */
public class UIGlobal
{
	// Single GUI Application statics: 
	
	private static VFSClient vfs; 
	private static VRSContext vrsContext; 
	private static GuiSettings guiSettings;
	private static ResourceLoader _resourceLoader;
	
	/** Frame to store graphics and other AWT resources */ 
	private static JFrame globalFrame=new JFrame(); 
	private static IconProvider iconProvider;
	
	/**
	 * Initialized Global VRSContext and GUI Configuration. 
	 */
	public static void init()
	{
		Global.init(); 
		Global.infoPrintf(UIGlobal.class,"-- UIGlobal.init() ---\n");
		
		// context and context configuration 
		VRSContext ctx = getVRSContext();
		ConfigManager confMan = ctx.getConfigManager();
		
		// Enable UI and persistant ServerInfoRegistry 
        //confMan.setPersistantUserConfiguration(true);
        confMan.loadPersistantServerConfiguration(); 
        confMan.setHasUI(true);		
	}
	
	public static String[] init(String args[])
	{
		args=Global.parseArguments(args);
		Global.init(); 
		
		Global.infoPrintf(UIGlobal.class,"-- UIGlobal.init() ---\n");
		return args; 
	}
	
	
	public static VRSContext getVRSContext() 
	{
		if (vrsContext==null)
			vrsContext=VRSContext.getDefault(); 
		
		return vrsContext; 
	}
	
	/** Return global user certificate store: ~/.vletrc/cacerts 
	 * @throws Exception */ 
	public static CertificateStore getUserCertificateStore() throws Exception
	{
	    return getVRSContext().getConfigManager().getCertificateStore();   
	}
	
	public static VFSClient getVFSClient()
	{
		if (vfs==null)
			vfs=new VFSClient(getVRSContext());
		
		return vfs; 
	}
	
	public static GuiSettings getGuiSettings() 
	{
		if (guiSettings==null)
			guiSettings=GuiSettings.getDefault(); 
		
		return guiSettings;
	}
	
	/** Return Registry associated with current VRSContext */ 
	public static Registry getRegistry() 
	{
		return getVRSContext().getRegistry(); 
	}
	
	public static JFrame getGlobalFrame() {return globalFrame;}
		
	/** Call this to dispose and flush all global resources */ 
	public static void shutdown()
	{
	    if (vfs!=null) 
	        vfs.close();
		iconProvider.clearCache();
		globalFrame.dispose();
	    VRS.exit();
		//ProxyNode.disposeClass();
	}

	public static boolean isGuiThread()
	{
		 return (SwingUtilities.isEventDispatchThread()==true);
	}

	public static GridProxy getGridProxy()
	{
		return getVRSContext().getGridProxy();
	}

	public static void saveProperties(VRL loc, Properties props) throws VlException
	{
		getResourceLoader().saveProperties(loc, props);
	}

	public static Properties loadProperties(VRL guiSettingsLocation) throws VlException
	{
	    return getResourceLoader().loadProperties(guiSettingsLocation);
	}

	public static ResourceLoader getResourceLoader()
	{
		if (_resourceLoader==null)
			_resourceLoader=new ResourceLoader(getVRSContext());
		
		return _resourceLoader;
	}

	/** Return icon or 'Broken Image' icon */ 
	public static Icon getIconOrBroken(String url)
	{
		return getIconProvider().createIconOrBroken(null,url);
	}

	/** Cached icon factory */ 
	public static IconProvider getIconProvider()
	{
		if (iconProvider==null)
			iconProvider=new IconProvider(globalFrame);
		
		return iconProvider; 
	}

	public static VRL getVirtualRootLocation() throws VlException
	{
		return getVRSContext().getVirtualRootLocation(); 
	}

	public static void displayEventMessage(ResourceEvent e)
	{
		SimpleDialog.displayMessage(null,e.getMessage()); 
	}

	public static void displayErrorMessage(String message)
	{ 
		SimpleDialog.displayErrorMessage(message);
	}

	/** Static method to call SwingUtil.*/
	
	public static void swingInvokeLater(Runnable task)
	{
		SwingUtilities.invokeLater(task); 
	}

	/** Static method to call SwingUtil.*/
	public static void swingInvokeLater(ActionTask task)
	{
		SwingUtilities.invokeLater(task); 
	}

	public static void assertNotGuiThread(String msg) throws VlInternalError
	{
		assertGuiThread(false,msg);
	}
	
	public static void assertGuiThread(String msg) throws VlInternalError
	{
		assertGuiThread(true,msg); 
	}

	public static void assertGuiThread(boolean mustBeGuiThread,String msg) throws VlInternalError
	{
        // still happens when trying to read/acces link targets of linknodes 
        if (mustBeGuiThread!=UIGlobal.isGuiThread())
        {
            Global.infoPrintf(UIGlobal.class, "\n>>>\n    *** Swing GUI Event Assertion Error *** !!!\n>>>\n");
            throw new  VlInternalError("Internal Error. Cannot perform this "
            						+(mustBeGuiThread?"during":"outside")+"during the Swing GUI Event thread.\n"+msg);
        }
	}

    public static void showMessage(String title, String message)
    {
        SimpleDialog.displayMessage(null,title,message); 
    }

    public static void showError(String title, String message)
    {
        SimpleDialog.displayMessage(null,title,message);
    }

	public static IMasterUI getMasterUI()
	{
	    // should be VBrowser
		return UIGlobal.getVRSContext().getUI();
	}

    public static void showException(final Throwable ex)
    {
     // asynchronous exception 
        // Do No Invoke Outside Event Thread: 
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable run=new Runnable()
            {
                public void run()
                {
                    showException(ex);  
                }
            };

            SwingUtilities.invokeLater(run); 
 
            return; 
        }

        ExceptionForm.show(ex);
    }

    public static String getJGridStartLocation()
    {
        return GlobalConfig.getProperty("vlet.jgridstart.location");  
    }

    public static MimeTypes getMimeTypes()
    {
        return MimeTypes.getDefault(); 
    }

    public static void resetContext()
    {
         VRS.getRegistry().reset(); 
         getVRSContext().reset();
    }

    public static ProxyVRSClient getProxyVRS()
    {
        return ProxyVRSClient.getInstance();
    }	
}
