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
 * $Id: ViewContext.java,v 1.5 2011-06-07 15:14:35 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:35 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import java.util.Properties;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.tasks.ITaskSource;
import nl.uva.vlet.vrl.VRL;

/**
 * The ViewContext class provides the environment and settings which 
 * were used to start the viewer. 
 * 
 * @author P.T. de Boer
 */
public class ViewContext
{
	/** Started as default action (double click */ 
	public static final String STARTED_AS_DEFAULT="startedAsDefaultAction";
	
	///** started with open with-><viewer>  */ 
	//public static final String STARTED_SPECIFIC="startedSpecific";   
	
	/** All the Resources which where selected when the viewer was started */ 
	public static final String SELECTED_URIS="selectedURIs";
	
	/** 
	 * Whether the preferred action was to start in a new window. 
	 * This property may be overriden by the viewers getStartInNewWindow() method.
	 */
	public static final String START_IN_STANDALONE_WINDOW = "startInStandaloneWindow";
	
	/** VBrowser internal: an explicit viewer class was selected */ 
	public static final String PREFERRED_VIEWER_CLASS = "preferredViewerClass";
	
	/** The VRL (or URI) that iniatilly was used to start the viewer. 
	 * After updateLocation() this value does not match the 'current'
	 * viewed location. 
	 */
	public static final String STARTUP_VRL = "startupVRL"; 

    public static final String GUI_SHOW_ERRORS = "guiShowErrors"; 
    
    public static final String DYNAMIC_ACTION_METHOD="dynamicActionMethod"; 
    

	// =======================================================================
	// 
	// =======================================================================
	
	Properties properties=new Properties();
	
	private MasterBrowser masterBrowser=null;

    private ActionContext actionContext;
	
	public ViewContext(MasterBrowser browser)
	{
		this.masterBrowser=browser;  
	}

	/** Get stored properties */ 
	public Properties getProperties()
	{
		return properties; 
	}
	
	/** Get property */ 
	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	/** Check property */ 
    public boolean hasProperty(String name)
    {
        return StringUtil.isNonWhiteSpace(properties.get(name)); 
    }
    
	/** Set property, return previous setting.*/ 
	public Object setProperty(String name,Object value)
	{
		// set to null => remove property ! 
		if (value==null)
			return properties.remove(name); 
		
		return properties.put(name,value); 
	}

	/** Return boolean value of property or defaultValue if 
	 * property not set 
	 */ 
	public boolean getBoolProperty(String name,boolean defaultValue)
	{
		Object val=properties.get(name);
		if (val==null)
			return defaultValue; 
		if (val instanceof Boolean)
			return (Boolean)val; 
		else if (val instanceof String)
			return new Boolean(val.toString()); 

		return defaultValue; 
	}
	
	/**
	 * Return boolean value of property. 
	 * Returns false is property is not set. 
	 */
	public boolean getBoolProperty(String name)
	{
		return getBoolProperty(name,false); 
	}	
	
	/**
	 * Return boolean value of property. 
	 * Returns null is property is not set. 
	 */
	public String getStringProperty(String name)
	{
		Object val=properties.getProperty(name); 
		
		if (val==null)
			return null; 
		
		return val.toString(); // String.toString() result in string itself 
	}	
	
	/** Return the resources which were selected when the viewer was started  */  
	public VRL[] getSelection()
	{
		String uristr=getStringProperty(SELECTED_URIS);
		
		try
		{
			return VRL.parseVRLList(uristr);
		}
		catch (VRLSyntaxException e)
		{
			Global.errorPrintln(this,"Exception:e"+e); 
			return null;
		} 
	}
	
	/**
	 * Returns whether the viewer was started as default action when a user
	 * clicked on a resource or not.
	 * Some viewers might want to know whether is should use defaults
	 * when started as default action, or prompt the user for extra configuration 
	 * options when started with the "open with->" menu option. 
	 */
	public boolean getStartedAsDefaultAction()
	{
		return this.getBoolProperty(STARTED_AS_DEFAULT,false); 
	}
	
	/** VBrowser internal : get optional preferred viewer class if pecified by the VBrowser */ 
	public String getPreferredViewerClass()
	{
		return getStringProperty(PREFERRED_VIEWER_CLASS);
	}
	
	/** VBrowser internal : set optional preferred viewer class */ 
	public void setPreferredViewerClass(String className)
	{
		setProperty(PREFERRED_VIEWER_CLASS,className);
	}

	/**
	 * Returns whether the viewer has (wil be) started in standalone window.  
	 * This method is use by the VBrowser. 
	 * If the Viewer returns 'true' from the alwaysStartStandalone() method
	 * this value is ignored and the Viewer is always started 
	 * in a new Window. 
	 */ 
	public boolean getStartInStandaloneWindow()
	{
		return getBoolProperty(START_IN_STANDALONE_WINDOW,false); 
	}

	/** 
	 * Whether the viewer needs to be started in a new window. 
  	 * Note the the ViewerPanel method isEmbedded() is the runtime method 
  	 * to check whether the viewer is embedded or is started in a new window.  
	 * @param val
	 */
	public void setStartInStandaloneWindow(boolean val)
	{
		setProperty(START_IN_STANDALONE_WINDOW,""+val); 
	}

	/** Set whether the viewer was started as 'default' action, 
	 * thus with an action click */  
	public void setStartedAsDefaultAction(boolean value)
	{
		this.setProperty(STARTED_AS_DEFAULT,value); 
	}
	
	/** 
	 * Returns property as Resource Attributes ({name,type,value} triple).
	 * Tries to inspect the stored Object and determine Resource type
	 */ 
	public VAttribute getAttribute(String name)
	{
		Object val=getProperty(name); 
		return VAttribute.createFrom(name,val);
	}
	
	/** Returns properties as (VRS) VAttributeSet */ 
	public VAttributeSet getVAttributeSet()
	{
		return VAttributeSet.createFrom(properties);	
	}
	
	/** Prints out Properties as VAttributeSet */ 
	public String toString()
	{
		return getVAttributeSet().toString(); 
	}
	
	/**
	 * Return MasterBrowser  which has started this 
	 * Viewer. This could be the VBrowser or and other
	 * Browser Adaptor. 
	 * Returns null when no MasterBrowser is present. 
	 * 
	 * @return
	 */
	public MasterBrowser getMasterBrowser()
	{
		return this.masterBrowser; 
	}
	
	public boolean hasMasterBrowser()
	{
		return (this.masterBrowser!=null);
	}
	
	/** 
	 * Returns TaskSource. Default is (V)BrowserController interface.  
	 * Can be null when started in a stand alone context. 
	 * @return
	 */
    public ITaskSource getTaskSource()
    {
        return masterBrowser; 
    }

    public void setStartupLocation(VRL loc)
    {
        this.setProperty(STARTUP_VRL,loc); 
    }
    
    /** Whether to show error popups */ 
    public boolean getShowErrors()
    {
        return this.getBoolProperty(GUI_SHOW_ERRORS,true);  
    }
    
    /** Whether to show error popups */
    public void setShowErrors(boolean val)
    {
        setProperty(GUI_SHOW_ERRORS,""+val);   
    }

    /**
     * Update ViewerContext with Dynamic Action Context which started this viewer.  
     */
    public void setDynamicAction(String methodName, ActionContext actionContext)
    {
        setProperty(ViewContext.DYNAMIC_ACTION_METHOD,methodName);
        this.actionContext=actionContext; 
    }
	
    public boolean hasDynamicAction()
    {
        return this.hasProperty(DYNAMIC_ACTION_METHOD);
    }

}

