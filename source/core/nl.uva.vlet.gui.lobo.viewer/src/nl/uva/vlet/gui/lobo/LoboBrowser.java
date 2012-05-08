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
 * $Id: LoboBrowser.java,v 1.15 2011-06-24 12:07:40 ptdeboer Exp $  
 * $Date: 2011-06-24 12:07:40 $
 */ 
// source: 

package nl.uva.vlet.gui.lobo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.viewers.ViewerPlugin;
import nl.uva.vlet.net.ssl.SslUtil;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;

import org.lobobrowser.main.PlatformInit;


public class LoboBrowser extends ViewerPlugin 
{
    private static final long serialVersionUID = 3652578919344065278L;

    static String mimetypes[] =
       { "text/html" };

    static
    {
        try
        {
            VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.gui.lobo.resfs.ResFS.class);
        }
        catch (Exception e)
        {
            Global.errorPrintStacktrace(e); 
        }
       
        try
        {
            PlatformInit instance = PlatformInit.getInstance();
            //disable logging BEFORE initializing Lobo
            instance.initLogging(false);
            instance.initExtensions();      
            /* 
             * Piter T. de Boer: 
             *   This method set the URLHandlerFactory. 
             *   Since the VRS has it's own URLHandlerFactory, this code may NOT be called. 
             *   By initializing the extensions, but not the procotols, the LoboBrowser panel 
             *   can be used. 
             */
            //PlatformInit.getInstance().addPrivilegedPermission(new RuntimePermission("*"));
            //PlatformInit.getInstance().addPrivilegedPermission(new FilePermission("*","write,execute,delete"));
            //PlatformInit.getInstance().addPrivilegedPermission(new SecurityPermission("*",null));
            //PlatformInit.getInstance().initProtocols();
            
            // TODO: Security context 
            //PlatformInit.getInstance().initSecurity();
            //instance.init(false,false); 
        } 
        catch (Exception e)
        {
            Global.errorPrintf(LoboBrowser.class,"***Exception:"+e); 
            Global.errorPrintStacktrace(e); 
        }
        
        shutuplogging();
    }
    // === //
    
    private LoboBrowserPanel loboBrowserPanel;

    private LoboPanelController loboController;
    
	public void initGUI()
	{	
	    loboController=new LoboPanelController(this); 
	    
	    {
	        setLayout(new BorderLayout());
	        add(getLoboBrowserPanel(), BorderLayout.CENTER);
	        if (this.isStandalone())
	        {
	            this.setPreferredSize(new Dimension(800,600));
	        }
	    }
	}

	private static void shutuplogging()
    {
	    //Not needed: Use VLET_INSTALL/etc/properties/logging.properties file ! 
	    
//	    // Log4J!
//	    org.apache.log4j.Logger.getLogger((org.lobobrowser.main.PlatformInit.class).getName()).setLevel(Level.FATAL);
//        
//	    Vector<String> loggers=new Vector<String>(); 
//	    
//	    loggers.add(org.lobobrowser.html.style.CSSUtilities.class.getName());
//	    loggers.add(com.steadystate.css.parser.SACParser.class.getName());
//	    loggers.add(org.lobobrowser.html.js.Executor.class.getName());
//	    
//	    for (String classname:loggers)
//	    {
//    	    java.util.logging.Logger logger=java.util.logging.Logger.getLogger(classname);
//            while(logger!=null)
//    	    {
//  
//    	        // System.err.println("logger='"+logger.getName()+"'");
//    	        logger.setLevel(java.util.logging.Level.SEVERE);
//    	        logger=logger.getParent();
//            }
//	    }
	    
    }

    private LoboBrowserPanel getLoboBrowserPanel()
	{
	    if (loboBrowserPanel==null)
	    {
	        this.loboController=new LoboPanelController(this); 
	        loboBrowserPanel = new LoboBrowserPanel(loboController,isStandalone());
	    }
	    
		return loboBrowserPanel;
	}

	@Override
	public String[] getMimeTypes()
	{
		return mimetypes;
	}

	@Override
	public String getName()
	{
	    return "LoboBrowser";
	}

	@Override
    public void initViewer()
	{
	    shutuplogging();
	    
		initGUI();
	}
	

	@Override
    public void stopViewer()
	{
	    loboBrowserPanel.stop(); 
	}

	@Override
	public void disposeViewer()
	{
	    loboBrowserPanel.dispose();	    
	}
	
	@Override
	public void updateLocation(VRL vrl) throws VlException
	{
        debugPrintf("Update location:%s\n",vrl);
        
        try
        {
            if (location.hasScheme(VRS.HTTPS_SCHEME))
                SslUtil.installCert(location.getHostname(),location.getPort());
        }
        catch (Exception e1)
        {
            throw new VlException("SSLException","SSLException:"+e1.getMessage(),e1);
        } 
        
		try
		{
		    // Really navigate: 
		    loboBrowserPanel.doNavigate(vrl); 			
		}
		catch (MalformedURLException e)
		{
		    throw new VRLSyntaxException(e.getMessage(), e);
		}

	}

	// Browser panel manages it's own scroll pane. 
	public boolean haveOwnScrollPane()
	{
	    return true; 
	}
	
	@Override
	public boolean isTool()
	{
		return true;
	}


    protected void debugPrintf(String format,Object ...args)
    {
        Global.debugPrintf(this,format,args);
        
    }
    
    public static void viewStandAlone(VRL loc)
    {
        LoboBrowser tv = null;
        tv = new LoboBrowser();
        //tv.setViewStandalone(true);

        try
        {
            tv.startAsStandAloneApplication(loc);

        } catch (VlException e)
        {
            Global.debugPrintException(LoboBrowser.class, e);
        }
    }
//
//    public boolean checkFollow(URL url) throws VlException
//    {
//        // check whether lobo browser should follow this url or 
//        // that the MasterBrowser should follow the url otherwise. 
//        
//        // todo: more efficient way: 
//        String mimeType=this.getVNode(new VRL(url)).getMimeType();
//        
//        boolean val=this.isMyMimeType(mimeType); 
//        debugPrintf("checkFollow:%s=%s\n",url,StringUtil.boolString(val));
//        return val; 
//    }

       
//    protected void fireViewEvent(java.net.URL url)
//    {
//        try
//        {
//            fireViewEvent(new VRL(url),false);
//        }
//        catch (VlException e)
//        {
//             handle("Invalid URL:"+url,e); 
//        }     
//    }
//    
    
   
    
    protected VNode getVNode(VRL vrl) throws VlException
    {
        return super.getVNode(vrl); 
    }
    
    /**
     * Perform Dynamic Action Method. 
     * When user starts this viewer from the 'tools' menu, this method is called also
     */
    public void doMethod(String methodName, ActionContext actionContext)
            throws VlException
    {
        Global.debugPrintf(this,"doMethod:%s\n",methodName); 
    }
    
  
    public String getVersion()
    {
        return "LoboBrowser plugin version 2.0"; 
    }
    
    public String getAbout()
    {
        return "<html><body><center>"
            +"<table width=400>"
            +"<tr bgcolor=#c0c0c0><td> <h3>LoboBrowser ViewerPlugin</h3></td></tr>"
            +"<tr><td></td></tr>"
            +"<tr><td> The LoboBrowser plugin uses the Lobo Toolkit <br><p> </td></tr>"
            +"<tr><td></td></tr>"
            +"<tr><td> See: <a href=\"http://www.lobobrowser.org/\">www.lobobrowser.org</a></td></tr>"
         +"</body></html>";
    }

}
