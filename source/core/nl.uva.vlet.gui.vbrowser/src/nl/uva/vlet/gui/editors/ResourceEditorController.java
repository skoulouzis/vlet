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
 * $Id: ResourceEditorController.java,v 1.14 2011-04-18 12:27:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:27 $
 */ 
// source: 

package nl.uva.vlet.gui.editors;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_DIRNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ICONURL;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SHOW_SHORTCUT_ICON;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_FRAGMENT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_URI_QUERY;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.Messages;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.dialog.ExceptionForm;
import nl.uva.vlet.gui.panels.fields.IAttributeField;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskSource;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;


public class ResourceEditorController implements ActionListener, WindowListener, ITaskSource
{
	// Lister for the tab panels'
	public class PanelListener implements FocusListener,ChangeListener
	
	{
		Object lastFocusComp=null;
		
		public void focusGained(FocusEvent e)
		{
			lastFocusComp=e.getSource(); 
			panelFocusGained(e.getSource(),e); 
		}

		public void focusLost(FocusEvent e)
		{
			 panelFocusLost(e.getSource(),e); 
		}

		public void stateChanged(ChangeEvent e) 
		{
			debug("CHANGE: "+e); 

			if (e.getSource()!=resourceForm.tabPane) 
				return; 
			
			Component comp = resourceForm.getActiveTab(); 
			
			debug("New Selected TAB:"+comp);
			
			if (comp!=resourceForm.tabServerConfigPnl)
				return; 
			
			ResourceEditorController.this.doServerPanelTabSwitch(true); 
			//panelFocusGained(e); 
		}
		
	}
	
	public class ServerAttributeListener implements IAttributeFocusActionListener
	{
		Object lastFocusComp=null;
		
		public void actionPerformed(ActionEvent e) 
		{
			// server header attribute field
			updateServerAttributeFromField(e.getSource()); 
		}
		
		public void focusGained(FocusEvent e) 
		{
			lastFocusComp=e.getSource(); 
		}

		public void focusLost(FocusEvent e) 
		{
			// server header attribute field
			updateServerAttributeFromField(e.getSource()); 
		}
		
		// notification from server attribute panel 
		public void notifyAttributeChanged(VAttribute attr) 
		{
			// Event from config attrs panel
			updateServerConfigAttribute(attr); 
			
		}
	}
	
	public class AttributeListener implements IAttributeFocusActionListener
	{
		Object lastFocusComp; 
		
		public void actionPerformed(ActionEvent e) 
		{
			updateAttributeFromField(e.getSource()); 
		}

		public void focusGained(FocusEvent e) 
		{
			lastFocusComp=e.getSource(); 	
		}

		public void focusLost(FocusEvent e) 
		{
			updateAttributeFromField(e.getSource()); 
		}
		
		// notification from config attribute panel 
		public void notifyAttributeChanged(VAttribute attr) 
		{
			// Event from config attrs panel
			putResourceAttribute(attr); 
			
		}
	}
	
	public static String resourceHeaderAttrNames[]=
		{
			ATTR_TYPE,
			ATTR_NAME
		};

	public static String iconAttrNames[]=
		{
			ATTR_ICONURL,
			ATTR_SHOW_SHORTCUT_ICON
		};


	public static String locationAttrNames[]=
		{
			ATTR_SCHEME, 
			ATTR_HOSTNAME,
			ATTR_PORT,
			ATTR_PATH,
			ATTR_URI_QUERY,
			ATTR_URI_FRAGMENT,
			ATTR_LOCATION,
			ATTR_DIRNAME,
		};
	
	public static String serverHeaderAttrNames[]=
	{
		ATTR_SCHEME, 
		ATTR_HOSTNAME,
		ATTR_PORT,
		ATTR_PATH,
		ServerInfo.ATTR_SERVER_NAME,
		ServerInfo.ATTR_SERVER_ID
	};
	

    protected Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);

    protected Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  
	
	// ========================================================================
	// Instance 
	// =======================================================================
	
    private static int idCounter=1; 
    
    private int id=0;  
    
    //private JFrame standAloneFrame = null;

    private ResourceEditor resourceForm = null;

	private VRSContext context;

    private ProxyNode resourceNode=null;

    private boolean busySavingSettings;

    private Icon resourceIcon;

    private boolean enableActionListener=false; // start DISABLED 

	private boolean isLogicalResource;

	private PanelListener panelListener;

	private AttributeListener attributeListener;

	private ServerAttributeListener serverAttributeListener;

	private String type;

	private boolean enableServerConfig;

	private boolean isEditable=true;

	private boolean isPlainPropertiesEditor=false;
	
	// ============================================//
	// === Properties and Server Configuration === // 
	// ============================================//
	
    private VAttributeSet resourceAttributes=new VAttributeSet();
	
	private ServerInfo serverInfo;

	private VAttributeSet serverConfigurationAttrs=null;

	private boolean busyLoading;

    private VRL locationVrl;

    
    /** optionally save new nl.uva.vlet.gui.utils.proxy */

    public ResourceEditorController(ResourceEditor form) 
    {
        this.resourceForm=form;
    	this.context=UIGlobal.getVRSContext();
        this.attributeListener=new AttributeListener();
        this.panelListener=new PanelListener(); 
    
    	init(); 
	}
    

	private  void init()
    {
        id=++idCounter;
    }
    
    public IAttributeFocusActionListener getAttributeListener()
    {
    	if (attributeListener==null)
    		attributeListener=new AttributeListener(); 
    	return this.attributeListener; 
    }
    
	public IAttributeFocusActionListener getServerAttributeListener()
	{
		if (this.serverAttributeListener==null)
			this.serverAttributeListener=new ServerAttributeListener();
		
		return this.serverAttributeListener;
	}
    
    public void actionPerformed(ActionEvent e)
    {
        debug("ActionPerformed"+e);
        
        if (this.enableActionListener==false)
            return; 
        
        Object source=e.getSource();
        
        if (source==resourceForm.applyB)
        {
            doApply(false); 
        }
        else if (source==resourceForm.resetB)
        {
            doReset(); 
        }
        else if (source==resourceForm.cancelB)
        {
            doCancel(); 
        }
        else if (source==resourceForm.okB)
        {
            doOK(); 
        }
        else if (source==resourceForm.uriAttrEnableCB)
        {
            // if the event is already enabled, reenable and set check box:
         	setEnableURIAttributes(true,resourceForm.uriAttrEnableCB.isSelected());
        }
        else if (source==resourceForm.serverConfigNewB)
        {
        	doNewServerConfig(); 
        }
        else if (source==resourceForm.serverConfigDeleteB)
        {
        	doDeleteServerConfig(); 
        }
        else if (source instanceof IAttributeField)
        {
        	updateAttributeFromField(source); 
        }
    } 
    
    protected void updateUriFields()
    {
        String fraq=resourceAttributes.getStringValue(ATTR_URI_FRAGMENT); 
        String query=resourceAttributes.getStringValue(ATTR_URI_QUERY); 
        
        boolean hasUriAttrs=( StringUtil.notEmpty(fraq) || StringUtil.notEmpty(query)); 
        boolean supported=false; 
        
        ServerInfo info=getServerInfo(false);
        if (info!=null)
            supported=info.getSupportURIAttributes();

        // System.err.printf(">>> uri supported="+supported+",hasUri="+hasUriAttrs+",?query#fraq=?%s#%s\n",query,fraq); 

        this.setEnableURIAttributes(supported,hasUriAttrs);
        
        if (supported)
        {
            this.resourceForm.uriFragmentField.setText(fraq);
            this.resourceForm.uriQueryField.setText(query); 
        }
    }

    // supported = possible to use, enabled= really use. 
    protected void setEnableURIAttributes(boolean supported,boolean enabled)
    {
        debug("Enable URI attribute supported/selected:"+supported+"/"+enabled);
        
        // update form 
        this.resourceForm.setEnableURIAttributes(supported,enabled);
        
        // update attributes
    	if (enabled && supported)
    	{
    		// recreate: but use optional text still stored in the the TextFields ! :
    		if (this.resourceAttributes.get(ATTR_URI_FRAGMENT)==null)
    			this.resourceAttributes.put(new VAttribute(ATTR_URI_FRAGMENT,resourceForm.uriFragmentField.getText()));
    		if (this.resourceAttributes.get(ATTR_URI_QUERY)==null)
    			this.resourceAttributes.put(new VAttribute(ATTR_URI_QUERY,resourceForm.uriQueryField.getText())); 
    		
    	}
    	else
    	{
    		// remove attributes, but keep optional text in UI fields !
    		this.resourceAttributes.set(ATTR_URI_FRAGMENT,""); // .remove(ATTR_URI_FRAGMENT);
    		this.resourceAttributes.set(ATTR_URI_QUERY,"");
    	}
	}
    
    private void doOK()
    {
        doApply(true); 
        exit(); 
    }

    private void doCancel()
    {
        exit(); 
    }

    public void exit()
    {
    	this.resourceForm.setVisible(false); 
        this.resourceForm.dispose(); 
    }
    
    // reset and reload: 
    private void doReset()
    {
    	this.bgUpdateNode(resourceNode); 
    }

    private void doApply(final boolean singnalRefresj)
    {
        if (resourceNode==null)
        	return; 
        
        this.setBusySaving(true); 
        
        this.resourceForm.applyB.setEnabled(false); 
          
        final VAttributeSet attrs=this.resourceAttributes; 
        
        // ================================================================
        // Update Location with server username if needed by serverInfo
        // This links this user+location to the specified ServerInfo!
        // ================================================================
        if (this.enableServerConfig && this.serverConfigurationAttrs!=null) // && (this.originalServerInfo.getNeedUserinfo()))
        { 
            // just copy it if it has the attribute: 
            VAttribute attr = this.serverConfigurationAttrs.get(VAttributeConstants.ATTR_USERNAME);
            if (attr!=null)
                attrs.put(attr); 
        }
        
        ActionTask saveTask=new ActionTask(this,"Saving resource attribute for:"+resourceNode)
        	{
				@Override
				protected void doTask() throws VlException
				{
					try
					{
						
						// apply attribute and signal refresh ! 
						for (VAttribute attr:attrs)
						{
							debug("applying attribute:"+attr); 
						}
						
						resourceNode.setAttributes(attrs.toArray(),singnalRefresj);
						
						if (enableServerConfig)
						    saveServerConfig();
						
						_bgUpdateNode(resourceNode); 
						
						postApplyUpdate(true); 
					
					}
					catch (Exception e)
					{
						handle(e);
						postApplyUpdate(false); 
					} 
					
				}

				@Override
				public void stopTask() {} 
        	};
        
        	saveTask.startTask(); 
        
    }
    
    protected void setBusySaving(boolean busy)
    {
    	this.busySavingSettings=busy; 
    }
    
   
    
    private void postApplyUpdate(boolean succesFul)
    {
        this.doUpdateServerConfig(); 
        this.uiUpdateServerConfigFieldsFromAttributes(); 
    	
    	if (succesFul)
    	{
	    	setHasChanged(false); 
    	}
    	else
    	{
    	}
    	
    	setBusySaving(false); 
    }
  
    
   
	/** Update backing attribute */ 
    protected void updateAttributeFromField(Object source)
    {
        // JIGLOO: gui dummy mode;
    	if (resourceAttributes==null)
    	    return; 
    	
        if (source instanceof IAttributeField)
        {
            IAttributeField field=(IAttributeField)source; 
            
            String name=field.getName(); 
            if (name==null)
            {
                error("Got NULL name for attribute field:"+source);
                return; 
            }
            
            VAttribute attr = this.resourceAttributes.get(name); 
            if (attr!=null) 
            {
            	attr.setValue(field.getValue()); // update attribute 
                resourceAttributes.put(attr); //store/update
            }
            else
            	// put as is: 
            	this.resourceAttributes.put(new VAttribute(field.getVAttributeType(),name,field.getValue()));
           
            // New Scheme -> enable host+port 
            if (name.compareToIgnoreCase(ATTR_SCHEME)==0)
            {
            	this.resourceForm.hostnameField.setEditable(true); 
            	this.resourceForm.portField.setEditable(true); 
            }
            
            this.setHasChanged(true);  
        }
    }
  
    private void setHasChanged(boolean hasChanged)
    {
  		this.resourceForm.applyB.setEnabled(hasChanged);
		this.resourceForm.resetB.setEnabled(hasChanged);
    }
    
    private void error(String msg)
    {
        Global.errorPrintf(this,"Error:%s\n",msg); 
    }

    public void windowClosing(WindowEvent e)
    {
    	exit();
    }

    public void windowOpened(WindowEvent e){}

    public void windowClosed(WindowEvent e){}

    public void windowIconified(WindowEvent e){}

    public void windowDeiconified(WindowEvent e){}

    public void windowActivated(WindowEvent e){}

    public void windowDeactivated(WindowEvent e){}

    
	private void debug(String msg)
	{
		Global.debugPrintf(this,msg+"\n");
	}

	/** Stores copy of attribute set to edit */ 
	public void setAttributes(VAttributeSet attrs, boolean editable)
    {
	    // Keep duplicate !
	    this.resourceAttributes=attrs.duplicate();
	    this.isEditable=editable; 
	}
	
	public void uiUpdateFieldsFromAttributes(final boolean newNode)
    {
	    debug("--- uiUpdateFieldsFromAttributes (IVa) ---");
	    
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable run=new Runnable()
            {  
               public void run()
               {
                   uiUpdateFieldsFromAttributes(newNode); 
               }
            };
            UIGlobal.swingInvokeLater(run);
            return; 
        }

        debug("--- uiUpdateFieldsFromAttributes (IVb) ---");
        
	    this.enableActionListener=false; 
	    
		updateLocationFields(); 
		updatePropertiesPanelAttrs(); 
		// plain properties/attribute editor
		
		resourceForm.repack(); 
		
	    this.enableActionListener=true; 
	}
	
	protected void updateLocationFields()
	{
	    VAttributeSet attrs=this.resourceAttributes; 
	 
	 	if (attrs==null)
		{
		    resourceForm.updateResourceType("<ERROR: NULL ATTRIBUTES"); 
		    return; 
		}
	     // Mandatory attributes!

		String name=attrs.getStringValue(ATTR_NAME);
		String targetScheme=attrs.getStringValue(ATTR_SCHEME);
	 	StringList schemes=new StringList(context.getRegistry().getDefaultSchemeNames()); 
	 	
	 	if (targetScheme==null)
	 		targetScheme="";
	 	
	 // scheme might not be supported, add anyway !
        schemes.add(targetScheme,true); 
        
        resourceForm.setScheme(targetScheme,schemes.toArray()); 
	 	resourceForm.setResourceIcon(this.resourceIcon);
	 	
	 	// ==========================
	 	// Editable/Enabled attributes 
	 	// =========================

	 	for (VAttribute attr:resourceAttributes)
	 	{
	 		IAttributeField field=this.resourceForm.getAttributeField(attr.getName());
	 		if (field!=null)
	 		{
	 			field.updateFrom(attr);
	 			field.setEditable(attr.isEditable()&&this.isEditable);
	 		}
	 		else
	 		{
	 			//error("*** ResourceEditor Dialog doesn't have [I]AttributeField:"+attr); 
	 		}
	 	}
	 	
	 	// Uri Fields:
	 	updateUriFields(); 
	     
	 	resourceForm.typeField.setEditable(false); 
	 	
	 	if (isLogicalResource==false)
	 	{
	 		// Location attributes are not editable; 
	 		resourceForm.schemeSB.setEditable(false,false); 
	 		resourceForm.portField.setEditable(false);
	 		resourceForm.hostnameField.setEditable(false); 
	 		resourceForm.pathField.setEditable(false); 
	 	}
	 	else
	 	{
	 		// Update Host+Port attributes for File scheme: 
	 		boolean isFileScheme=StringUtil.equals(targetScheme,VFS.FILE_SCHEME); 
	 		
	 		if (isEditable)
	 		{
		 		resourceForm.portField.setEditable(isFileScheme==false);
		 		resourceForm.hostnameField.setEditable(isFileScheme==false);
	 		}
	 	}
	 	
	 	// ===========================================================
	 	// Misc. Options 
	 	// ===========================================================
	 	
	 	// file:// might not have hostname+port; 
	 	if (!attrs.containsKey(ATTR_PORT))
	 	{
	 	   resourceForm.portField.setEditable(false);
	 	   resourceForm.portField.setText(""); // clear
	 	}
		if (!attrs.containsKey(ATTR_HOSTNAME))
		{
            resourceForm.hostnameField.setEditable(false);
            resourceForm.hostnameField.setText("");  // clear 
		}
	}
   

    private void updatePropertiesPanelAttrs()
    {
		// update configuration panel with attribute which are not yeat displayed. 
		VAttributeSet propsAttrs = resourceAttributes.duplicate(); 

		// remove header attributes
		for (String attr:resourceHeaderAttrNames)
			propsAttrs.remove(attr);
		
		if (this.isPlainPropertiesEditor==false)
    	{
    		// remove location attributes
    		for (String attr:locationAttrNames)
    			propsAttrs.remove(attr);
    		// remove location attributes
    		for (String attr:iconAttrNames)
    			propsAttrs.remove(attr);
    	}
    	
		// strip sections 
		String[] keys = propsAttrs.getOrdenedKeyArray(); 
		if (keys!=null)
			for (String key:keys) 
				if (key.startsWith("["))
					propsAttrs.remove(key);  
		
		this.resourceForm.setConfigPanelAttributes(propsAttrs,this.isEditable);
    }
    
    @Override
    public String getID()
    {
        return "ResourceEditor-"+id;
    }

    @Override
    public void handle(Exception e)
    {
        ExceptionForm.show(e);
    }

    @Override
    public void messagePrintln(String msg)
    {
        //  log banner:
        Global.infoPrintf(this,"%s\n",msg);
    }

    //@Override
    public void setHasTasks(final boolean val)
    {
    	Runnable mouseUpdater=new Runnable() {
    		public void run(){
    	    	if (val) resourceForm.setCursor(busyCursor); 
    	    	else resourceForm.setCursor(defaultCursor); 
    		}
    	};
    	
    	UIGlobal.swingInvokeLater(mouseUpdater); 
    }
    
    public void updateNode(ProxyNode pnode)
    {
    	this.type=pnode.getType();
        this.resourceNode=pnode;
        this.isLogicalResource=false;
        this.enableServerConfig=false;
        
        this.resourceForm.clearFields(false); 
        
        try
        {
            this.isLogicalResource=pnode.isLogicalNode();
        }
        catch (VlException e)
        {
            handle(e); 
        }
        
        String scheme=pnode.getVRL().getScheme();
        // defaults:
        boolean enableIconsPnl=false; 
        this.isLogicalResource=false; 
        this.enableServerConfig=false;
        
        // old 0.9 style: complete properties dump. 
        if (this.isPlainPropertiesEditor)
		{
         	this.resourceForm.setTitle("View Properties"); 
         	this.resourceForm.setDailogText(Messages.D_view_or_edit_properties);
            this.enableServerConfig=false;
    		resourceForm.setEnableLocationAttrsPanel(false); 
		}
        else if (StringUtil.equals(type,VRS.SERVER_TYPE))
    	{
    		this.resourceForm.setTitle("Server Configuration");
    	 	this.resourceForm.setDailogText(Messages.D_edit_server_configuration);
    	 	
			this.enableServerConfig=true;
			this.isLogicalResource=true;
			
			this.resourceForm.setEnablePropertiesPanel(false); 
		}
    	else if (StringUtil.equals(type,VRS.RESOURCEFOLDER_TYPE))
        {
        	this.resourceForm.setTitle("Resource Folder Properties"); 
    	 	this.resourceForm.setDailogText(Messages.D_view_resource_folder_properties); 
      
        	this.enableServerConfig=false;
            this.isLogicalResource=true;
            
    		resourceForm.setEnableLocationAttrsPanel(false); 
    		resourceForm.setEnablePropertiesPanel(false); 
        }
    	else if (StringUtil.equals(type,VRS.LINK_TYPE))
        {
    		this.resourceForm.setTitle("Link Properties"); 
    		this.resourceForm.setDailogText(Messages.D_edit_link_properties);

            this.enableServerConfig=false;
            this.isLogicalResource=true;
            
            resourceForm.setEnablePropertiesPanel(false);
        }
        else if (StringUtil.equals(type,VRS.RESOURCE_LOCATION_TYPE))
        {
            this.resourceForm.setTitle("Resource Location Properties");
            
            this.enableServerConfig=true;
            this.isLogicalResource=true;
            enableIconsPnl=true;
            
            resourceForm.setEnablePropertiesPanel(false); 
        }
        else if (StringUtil.equals(type,VRS.RESOURCE_INFO_TYPE))
        {
            this.resourceForm.setTitle("Resource Information Properties");
            
            this.enableServerConfig=true;
            enableIconsPnl=false; //fixed icons
            this.isLogicalResource=true;
            
            resourceForm.setEnablePropertiesPanel(false); 
        }
        else if (StringUtil.isEmpty(scheme) 
                 || StringUtil.equals(scheme,VRS.MYVLE_SCHEME)
                 || StringUtil.equals(scheme,VRS.INFO_SCHEME) )
            
        {
        	this.resourceForm.setTitle("Configuration Properties");
        	
        	this.enableServerConfig=false;
        	this.isLogicalResource=true;
        	enableIconsPnl=false; //fixed icons
        	
        	resourceForm.setEnableLocationAttrsPanel(false); 
        }
    	else
    	{
        	// Plain resource (Dir/File,etc); 
    		this.resourceForm.setTitle("Resource Properties for: "+type);
    		
    		this.enableServerConfig=false;
    		this.isLogicalResource=false;
    		enableIconsPnl=false;
    	}
    	
        // updates: 
        resourceForm.setEnableIconAttrsPanel(enableIconsPnl);
        resourceForm.setEnableServerConfigPanel(enableServerConfig);

        // do background stuff which potentially can block the gui 
        bgUpdateNode(pnode); 
    }
    
    private void bgUpdateNode(final ProxyNode pnode)
    {
        // ===
        // Pre Fill Attributes in fiels to show location, etc when doing
        // background fetching. 
        // ===
        VAttributeSet attrs=new VAttributeSet(); 
        this.locationVrl=pnode.getVRL(); 
        
        attrs.set(ATTR_NAME,pnode.getName());
        attrs.set(ATTR_TYPE,pnode.getType());
        attrs.set(ATTR_HOSTNAME,locationVrl.getHostname()); 
        attrs.set(ATTR_PATH,locationVrl.getPath()); 
        attrs.set(ATTR_PORT,locationVrl.getPort()); 
        attrs.set(ATTR_SCHEME,locationVrl.getScheme()); 
        this.setAttributes(attrs,false);
        
        uiUpdateFieldsFromAttributes(true); 
        
        
        debug("--- bgUpdateNode (I) ---");       
    	this.setBusyLoading(true); 
    	
    	this.serverInfo=null;
    	this.serverConfigurationAttrs=null; 
    	
        ActionTask updateTask=new ActionTask(this,"Updating attributes from node:"+resourceNode)
            {
                @Override
                protected void doTask() throws VlException
                {
                    _bgUpdateNode(pnode); 
                }

                @Override
                public void stopTask()
                {
                }
            };
            
        updateTask.startTask();
    }
    
    private void _bgUpdateNode(ProxyNode pnode)
    {
        debug("--- _bgUpdateNode (II) ---");
    	// ============================================
    	// Get Attributes which might block GUI ! 
    	// ============================================
        // pre fill with defaults in case of exceptio !
    	
        
        try
        {
            resourceIcon=pnode.getDefaultIcon(48,false);
            VAttributeSet attrs = pnode.getAttributeSet();
            this.isEditable=pnode.isEditable(); 
            
         // apply attribute and signal refresh ! 
            for (VAttribute attr:attrs)
            {
                debug("New (re)read attribute:"+attr); 
            }
            
            // todo: check type+name from getAttributes() ! 
            if (attrs.get(ATTR_TYPE)==null)
            	attrs.set(ATTR_TYPE,pnode.getType());
            if (attrs.get(ATTR_NAME)==null)
            	attrs.set(ATTR_NAME,pnode.getName()); 
            
            // is editable in this resource editor ! 
            this.setAttributes(attrs,isEditable);
            
            // prefetch server info if available. Do not autocreate !
            this.serverInfo=null; //clear;
            this.serverConfigurationAttrs=null; //clear also; 
 
            // prefetch but do not create if missing!
            this.getServerInfo(false); 
 
        }
        catch (Exception e)
        {
            handle(e);
        }
        
        postUpdateNode();
    }
    
    protected void postUpdateNode()
    {
        debug("--- postUpdateNode (III) ---");
        
    	this.setBusyLoading(false); 
    	this.setHasChanged(false); 
    	
        uiUpdateFieldsFromAttributes(true);
    }
    
    protected void setBusyLoading(boolean val)
    {
    	this.busyLoading=val; 
	}

	protected void setEnableFullConfiguration(boolean fullMode) 
	{
	    this.enableServerConfig=true;
		this.isPlainPropertiesEditor=(fullMode==false);
	}

	public FocusListener getPanelListener()
	{
		return this.panelListener; 
	}
	
	public void putResourceAttribute(VAttribute attr)
	{
		debug("update Configuration Attribute:"+attr);
	    	
		VAttribute confAttr=this.resourceAttributes.get(attr.getName());
		
		if (confAttr==null)
		{
			error("*** Couldn't find configuration attribute:"+attr); 
			return;
		}
	    	
		this.setHasChanged(true);
	    	 
		confAttr.setValue(attr.getValue()); 
		resourceAttributes.put(confAttr);
    }
	
    public void panelFocusLost(Object source,FocusEvent e) 
    {
	}

	public void panelFocusGained(Object source, FocusEvent e)
	{
	}
	
	/** Get (Optional edited) scheme from form*/ 
	protected String getCurrentScheme() 
    {
        return this.resourceForm.schemeSB.getValue(); 
    }
    
	/** Get (Optional edited) port from form*/
    protected int getCurrentPort()
    {
        return resourceForm.portField.getIntValue(); 
    }
    
    /** Get (Optional edited) hostn from form*/
    protected String getCurrentHost()
    {
        return resourceForm.hostnameField.getValue(); 
    }
    
    protected String getUserinfo()
    {
        VAttribute attr= this.resourceAttributes.get(VAttributeConstants.ATTR_USERNAME); 
        if (attr==null)
             return null; 
         
        return attr.getStringValue(); 
    }
    
    // ========================================================================
    // Server Configuration
    // ========================================================================
    
    /** Update backing attribute */ 
    protected boolean updateServerAttributeFromField(Object source)
    {
    	debug("Server Property change:"+source); 
    	
        // JIGLOO: gui dummy mode;
    	if (this.serverConfigurationAttrs==null)
    	    return false;
    	
        if (source instanceof IAttributeField)
        {
            IAttributeField field=(IAttributeField)source; 
            
            String name=field.getName(); 
            if (name==null)
            {
                error("*** Got NULL name for attribute field:"+source);
                return false; 
            }
            
            VAttribute attr = this.serverConfigurationAttrs.get(name); 
            if (attr!=null) 
            {
            	attr.setValue(field.getValue()); // update attribute 
            	serverConfigurationAttrs.put(attr); //store/update
            }
            else
            {
            	// put as is: 
            	this.serverConfigurationAttrs.put(new VAttribute(field.getVAttributeType(),name,field.getValue()));
            }
            
            // if is header field: 
            doUpdateServerConfig();
            
            this.setHasChanged(true);  
            return true;
        }
        
        return false; 
    }
   
   
    public void updateServerConfigAttribute(VAttribute attr)
    {
        if (this.serverConfigurationAttrs==null)
            return; 
        
    	debug("update Configuration Attribute:"+attr);
    	
    	VAttribute confAttr=this.serverConfigurationAttrs.get(attr.getName());
    	
    	if (confAttr==null)
    	{
    		error("*** Couldn't find Server Configuration attribute:"+attr); 
    		return;
    	}
    	
    	this.setHasChanged(true);
    	 
    	confAttr.setValue(attr.getValue()); 
    	serverConfigurationAttrs.put(confAttr); 
    	
    }
    
    protected void doDeleteServerConfig() 
    {
    	if (this.serverInfo!=null)
    	{
    		this.serverInfo.persistentDelete(); 
    		this.serverInfo=null;
    		this.serverConfigurationAttrs=null;
    		uiUpdateServerConfigFieldsFromAttributes();
    	}
    }

    protected void doNewServerConfig() 
	{
		VAttributeSet attrs=null; 
		this.serverInfo=null;
		this.serverConfigurationAttrs=null; 
		String scheme=this.getCurrentScheme(); 
		String host=this.getCurrentHost(); 
		int port=this.getCurrentPort(); 
		
		if (port<=0) 
		{
			JOptionPane.showMessageDialog(this.resourceForm.getRootPane(),
					String.format(Messages.M_can_not_create_new_server_config_invalid_port,port),
					"Invalid Port",
					JOptionPane.OK_OPTION); 
			return;  
		}
		
		if (StringUtil.isEmpty(host))
		{
			JOptionPane.showMessageDialog(this.resourceForm.getRootPane(),
					"Can not create new Server Configuration: Need hostname."); 
			return;
		}
		
		// overwrite current (should be null); 
		this.serverInfo = this.getServerInfo(true); 
		this.serverConfigurationAttrs=null;
		
		// update does the rest: 
		this.doUpdateServerConfig();
	}

	
	
	
	protected ServerInfo getServerInfo(boolean autoCreate)
	{
	    if (this.serverInfo==null)
	        this.serverInfo=fetchServerInfo(autoCreate); 
	    
	    return serverInfo; 
	}
	
	protected  ServerInfo fetchServerInfo(boolean autoCreate) 
    {
	    // get UI fields ! 
		String scheme=getCurrentScheme();
		if (StringUtil.isEmpty(scheme))
		{
		    Global.warnPrintf(this,"***Warning: Empty scheme for:%s!\n",this.locationVrl);
		    return null; 
		}
		
		int port=getCurrentPort(); 
		String host=getCurrentHost();
		
		// Optional, might be zero: 
		String userInf=getUserinfo();
		
    	debug("Checking ServerInfo:"+userInf+"@"+scheme+"://"+host+":"+port); 
    	
    	// jigloo 'pure gui' patch: 
    	if (scheme==null)
    		scheme="<SCHEME>";
    	
    	ServerInfo info=null; 
    	// fetch existing or initialize new: Does not stored it in ServerRegistry yet !
    	VRL vrl=new VRL(scheme,userInf,host,port,null); 
    	try
    	{
    	    info=UIGlobal.getVRSContext().getServerInfoFor(vrl,autoCreate);
    	} 
    	catch (Exception e)
    	{
    	    handle(e); 
    	}
    	
    	if (info==null)
    	{
    	    // System.err.println("*** No ServerInfo for:"+vrl); 
    	}
    	return info; 
    }
    
	// switch to server configuration: Update Server Settings. 
	private void doServerPanelTabSwitch(boolean enabled)
	{
	    String scheme=this.getCurrentScheme();
	    
	    if (this.serverInfo!=null)
	    {
	        if (StringUtil.equals(scheme,serverInfo.getScheme())==false)
	        {
	            boolean doit=UIGlobal.getMasterUI().askYesNo("New Server Configuration",
	                    "Changing the server scheme will reset the current server configuration."
	                    +"Do you want to create new configuration ?",false);
	            if (doit)
	            {
	                // triger recreate: 
	                this.serverInfo=null;
	                this.serverConfigurationAttrs=null; 
	            }
            }
	    }
	    
	    doUpdateServerConfig();
	}

	private void resetServerConfig()
	{
		// reset current server configuration 
		// and copy from resource attributes. 
		this.serverInfo=null;
		this.serverConfigurationAttrs=null;
		doUpdateServerConfig();
	}

	// dipsplay ServerInfo or reset fields: 
    private void doUpdateServerConfig() 
    {
        if (this.enableServerConfig==false)
            return; 
        
        if (this.serverConfigurationAttrs==null)
        {
            ServerInfo info=this.getServerInfo(true);  // get create server info
            
            if (info!=null)
            {
                // COpy of conifigurable server attributes: 
                this.serverConfigurationAttrs=new VAttributeSet(info.getAttributes()); 
            }
        }
        
        if (this.serverConfigurationAttrs!=null)
        {
            // === 
            // remove key and internal attributes ! (not public editable yet !) 
            // ===
            this.serverConfigurationAttrs.remove(ATTR_HOSTNAME); 
            this.serverConfigurationAttrs.remove(ATTR_PORT); 
            this.serverConfigurationAttrs.remove(ServerInfo.ATTR_SERVER_ID); 
            this.serverConfigurationAttrs.remove(ServerInfo.ATTR_SERVER_NAME);
        }
        
        uiUpdateServerConfigFieldsFromAttributes();
    		
    }
    
    private void uiUpdateServerConfigFieldsFromAttributes()
    {
    	 if (UIGlobal.isGuiThread()==false)
         {
             Runnable run=new Runnable() {  
                public void run() {
                	uiUpdateServerConfigFieldsFromAttributes(); 
                }
             };
             UIGlobal.swingInvokeLater(run);
             return; 
         }

    	
   		if (this.serverConfigurationAttrs!=null)
   		{	
   			VAttributeSet attrSet=this.serverConfigurationAttrs.duplicate();
   			
   			String userInf=this.getUserinfo(); 
   			
   			VRL serverUri=new VRL(getCurrentScheme(),
   			        userInf,
   			        getCurrentHost(), 
   			        getCurrentPort(),"/"); 

   			// update header: 
   			
    		// strip header attributes already shown in top panel:
    		attrSet.remove(ATTR_SCHEME); 
    		attrSet.remove(ATTR_HOSTNAME); 
       		attrSet.remove(ATTR_PORT); 
       		//attrSet.remove(ServerInfo.ATTR_SERVER_NAME); 
       		resourceForm.serverSettingUriFld.setText(serverUri.toString());
       		resourceForm.serverSettingUriFld.setEditable(false); 
    		this.resourceForm.setServerConfigAttributes(attrSet,true);
    		// block server configuraiton for file: 
    		this.resourceForm.serverConfigNewB.setEnabled(false); 
    		this.resourceForm.serverConfigDeleteB.setEnabled(true); 
    	}
    	else
    	{
    		resourceForm.serverSettingUriFld.setText("<No Configuration>");
       		resourceForm.serverSettingUriFld.setEditable(false); 
   			this.resourceForm.setServerConfigAttributes(null,false);
   			
    		boolean isFile=StringUtil.equalsIgnoreCase(getCurrentScheme(),VRS.FILE_SCHEME); 
   			
   			this.resourceForm.serverConfigNewB.setEnabled(isFile==false); 
    		this.resourceForm.serverConfigDeleteB.setEnabled(false); 
    	}
    }
    
    private void saveServerConfig()
    {
    	if (serverConfigurationAttrs!=null)
    	{
    	    // check, but do not autocreate: 
    		if (this.getServerInfo(false)==null)
    			return;
    		
    		// update server URI fields 
    		this.serverInfo.setScheme(getCurrentScheme()); 
    		this.serverInfo.setHostname(getCurrentHost()); 
    		this.serverInfo.setPort(getCurrentPort()); 
    		// store server configuration 
    		this.serverInfo.updateServerAttributesFrom(serverConfigurationAttrs,true);
    		
    		this.serverInfo=this.serverInfo.store(); 
    		
    		// trigger refetch 
    		//this.originalServerInfo=null;  
    		this.serverConfigurationAttrs=null;  
    	}
   }


}
