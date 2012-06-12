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
 * $Id: IconProvider.java,v 1.19 2011-06-20 12:52:12 ptdeboer Exp $  
 * $Date: 2011-06-20 12:52:12 $
 */ 
// source: 

package nl.uva.vlet.gui.icons;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.gui.GuiSettings;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.image.ImageSequence;
import nl.uva.vlet.gui.image.ImageUtil;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.LogicalResourceNode;
import nl.uva.vlet.vrs.VNode;

/** 
 * Simple Icon provider class which searched for icons in 
 * both user and installation directories. 
 * Also checks and searches for mimetype icons. 
 */ 
public class IconProvider
{
    private static JFrame source=null;    
    private static IconProvider instance=null;
    private static ClassLogger logger;

    static
    {
       logger=ClassLogger.getLogger(IconProvider.class);
       //logger.setLevelToDebug();
    }
    
    public static synchronized IconProvider getDefault()
    {
        if (source==null)
            source=new JFrame(); 
        
        if (instance==null)
            instance=new IconProvider(source); 
            
        return instance; 
    }
    
	// ==========================================================================
	// Class
	// ==========================================================================

	/** Private IconHash to REALLY speed up browsing */
	private Hashtable<String,Image> iconHash=new Hashtable<String,Image>();

	/** path prefix for the mimetype icons: <theme>/<size>/<type>  */ 
	private String mime_icons_theme_path="gnome/48x48/mimetypes";

	/** default file icon */ 
	private String default_icon_url="default/file.png"; 

	/** default folder icon */ 
	private String default_folder_icon_url="default/folder.png";

	/** default home folder icon */
	private String home_folder_icon_url="default/home_folder.png";

	private VRL iconsPath[]=null;

	private ImageRenderer iconRenderer=null;

    private ResourceLoader resourceLoader=null; 
    
	//private static IconRenderer iconRenderer=new IconRenderer();

	/** IconProvider with optional AWT Image Source */ 
	public IconProvider(Component source) 
	{
	    // Icon Search Path: use URLs as extra classpath: 
	    VRL vrls[]=
	    {
	            GuiSettings.getUserIconsDir(), // ~/.vletrc/icons/
	            GuiSettings.getInstallationIconsDir(), // VLET_INSTALL/icons/
	            // already appended by creatIcon:
	            //GuiSettings.getUserIconsDir().append("mimetypes"),
	            //GuiSettings.getInstallationIconsDir().append("mimetypes"),
	            //GuiSettings.getInstallationIconsDir().append(mime_icons_theme_path)
	    }; 
	    
	    URL urls[]=null; 
	    
	    try
	    {
	        urls=VRL.toURLs(vrls);
	        // convert to DIR urls (mandatory for URL classloader)
	        for (int i=0;i<urls.length;i++)
	            urls[i]=new URL(urls[i]+"/"); 
	    }
	    catch (Exception e)
	    {  
	        logger.errorPrintf("Could initialize Icon Search path!\n"); 
	        logger.errorPrintf("Exception =%s\n",e); 
	    }
	    
        this.resourceLoader=new ResourceLoader(UIGlobal.getVRSContext(),urls);
        this.iconRenderer=new ImageRenderer(resourceLoader,source);
	}

	/** Specify default Icon url to use in the case an icon cannot be found. */  
	public void setDefaultIconURL(String iconUrl)
	{
		this.default_icon_url=iconUrl;  
	}
	
	/** Specify default Icon url for composite resource to use in the case an icon cannot be found. */  
	public void setDefaultCompositeIconURL(String iconUrl)
	{
		this.default_folder_icon_url=iconUrl;  
	}
	
	/** Specify default Icon url to use in the case an icon cannot be found. */  
	public void setLinkIconURL(String iconUrl)
	{
		this.iconRenderer.setLinkImageUrl(iconUrl);   
	}
	
	/**
	 * icons search path (including mimetype search path): 
	 * $HOME/.vletrc/icons/
	 * $INSTALL/lib/icons /
	 * === 
	 * Mime type search paths already appended by createMimeTypeIcon...
	 * $HOME/.vletrc/icons/mimeypes/
	 * $INSTALL/lib/icons/mimetypes/
	 * $INSTALL/lib/icons/gnome/48x48/mimetypes/
	 */

	/**
	 * getDefaultIcon() tries to create an icon (mimetype or otherwise). 
	 * 
	 * + Non Mimetype: 
	 *   - $HOME/.vletrc/icons/				  ; Custom user icons 
	 *   - $VLET_INSTALL/icons/               ; Is on CLASSPATH, thus covered by ResouceLoader.getIcon())
	 *   
	 * + Mimetype icons:  
	 * - $HOME/.vletrc/icons/mimetypes        ; For custom user mimetype icons 
	 * - $VLET_INSTALL/lib/icons/mimetypes/ 	  ; For custom system mimetype icons
	 * - $VLET_INSTALL/lib/icons/<mimetype theme path>/ ; Mimetype Icons from 'theme' 
	 * 
	 * + Resource (GUI/menu/swing components, use ResourceLoader directly !): 
	 * - $CLASSPATH                           ; For resource icons, is covered by ResourceLoader.getIcon() 
	 * - URL/VRL: 'file:///..'			      ; Is covered by ResourceLoader.getIcon() 
	 * 
	 * I) linknode/resourcenode with (optional) private iconURL
	 * II) mimetype 
	 * III) Default/backup icon
	 * IV) broken image 
	 * 
	 * @param preferredSize 
	 *            try to find icon with this size. Return bigger icon if
	 *            preferred size is not found !
	 * @return the prefed Icon, size, might not match.
	 * @throws VlException
	 */
	public Icon createDefaultIcon(VNode vnode,int size,boolean greyOut)
	{
		logger.debugPrintf("createDefaultIcon [%d,%b] for: %s\n",size,greyOut,vnode); 

		if (vnode==null)
		{
		    logger.warnPrintf("*** Warning: createDefaultIcon():got NULL VNode\n");
			return null;
		}
		// for plugins, must use classLoader of plugin class ! 
		ClassLoader nodeClassLoader=vnode.getClass().getClassLoader(); 

		// =============================================================================
		// LinkNodes  
		// =============================================================================

		boolean showAsLink = false;
		boolean isComposite=false; 
		String mimetype=null;
		String iconURL=null;  
		//VRL targetVRL=null; 

		// Resolve LogicalResourceNodes: 
		if (vnode instanceof LogicalResourceNode)
		{
			try
			{
				// get LogicalResourceNode attributes: 
				LogicalResourceNode lnode=(LogicalResourceNode)vnode; 
				showAsLink=lnode.getShowShortCutIcon();
				vnode=lnode;

				isComposite=lnode.getTargetIsComposite(true);
				//targetVRL=lnode.getTargetVRL(); 

				// be reboost when getting linktarget attributes: 

				try
				{
					mimetype=lnode.getTargetMimeType();
				}
				catch (Exception e)
				{
				    logger.warnPrintf("Couldn't get target mimetype of linknode:%s\n",lnode); 
					logger.warnPrintf("Exception=%s\n",e); 
				}
				
				try
				{
					iconURL=lnode.getIconURL(size); 
				}
				catch (Exception e)
				{
				    logger.warnPrintf("Couldn't get target mimetype of linknode:%s\n",lnode); 
					logger.warnPrintf("Exception=%s\n",e); 
				}
				// Debug("target Is Composite="+isComposite+"for:"+lnode.getTargetVRL());
			}
			catch (Exception e)
			{
			    logger.errorPrintf("Error getting LogicalNode from:%s\n",vnode); 
				logger.errorPrintf("Exception=%s\n",e); 
			}
		}
		else
		{
			// use vnode defaults: 
			isComposite=vnode.isComposite();
			//targetVRL=vnode.getVRL(); 

			try
			{
				mimetype=vnode.getMimeType();
			}
			catch (VlException e)
			{
				logger.warnPrintf("Could not get Mimetype of:%s\n",vnode); 
				logger.warnPrintf("Exception =%s\n",e); 

				e.printStackTrace();
			}
			iconURL=vnode.getIconURL(size); 
		}
		
		if (StringUtil.isEmpty(iconURL)==true)
			iconURL=null; // set empty string "" to null 
		
		if (StringUtil.isEmpty(mimetype)==true)
			mimetype=null; // set empty string "" to null 
	
		// =============================================================================
		// Resolve Icon URL   
		// =============================================================================

		if (StringUtil.isEmpty(iconURL)==false) 
		{
			logger.debugPrintf("createDefaultIcon: I)\n");
			
			Icon icon=renderIcon(nodeClassLoader,iconURL,showAsLink,size,greyOut);
		 
			if	(icon!=null)
			{
				logger.debugPrintf("createDefaultIcon: I) found:%s\n",iconURL);
				return icon;
			}
			logger.debugPrintf("createDefaultIcon: I)  NULL for:%s\n",iconURL);
		}

		// =============================================================================
		// MimeType Icons 
		// =============================================================================

		//
		// The mimetype: "application/octet-stream" is not allowed as 
		// default mimetype for Composite nodes.
		// Set to null to trigger 'default' icons further in this method: 
		// 
		if ( (mimetype!=null) && (isComposite) 
				&& (mimetype.compareToIgnoreCase("application/octet-stream")==0) 
		   )
		{
			mimetype=null;
		}

		if ((iconURL==null) && (mimetype!=null))
		{
			iconURL=createMimeTypeIconURLSuffix(mimetype);
		}

		if (iconURL!=null)
		{
			logger.debugPrintf("createDefaultIcon: using mimetype IIa):%s\n",iconURL);
		
			// try ./mimetypes/iconURL subpath 
			Icon icon = renderIcon(nodeClassLoader,"mimetypes/"+iconURL,showAsLink,size,greyOut);
			
			if (icon!=null)
				return icon;
		}
		
		
		if (iconURL!=null)
		{
			logger.debugPrintf("createDefaultIcon: using theme mimetype IIb):%s\n",iconURL);
		
			// try again using full (theme) mimetype path: ./<themes path>/iconURL
			Icon icon = renderIcon(nodeClassLoader,mime_icons_theme_path+"/"+iconURL,showAsLink,size,greyOut);
			
			if (icon!=null)
				return icon;
		}
		
		// =============================================================================
		// Default Resource Icons (File,Folder,...) 
		// =============================================================================

		logger.debugPrintf("createDefaultIcon: III):%s\n",iconURL);
		
		if (isComposite)
		{
			if (vnode instanceof VDir)
			{
				if (UIGlobal.getProxyVRS().getConfigManager().getUserHomeLocation().compareTo(vnode.getVRL()) == 0)
					iconURL = home_folder_icon_url;
				else
					iconURL = default_folder_icon_url;
			}
			else
			{
				iconURL = default_folder_icon_url;
			}
		}
		else
		{
			iconURL = default_icon_url;
		}
		
		logger.debugPrintf("createDefaultIcon: IV):%s\n",iconURL);
		
		Icon icon = renderIcon(nodeClassLoader,iconURL,showAsLink,size,greyOut);

		if (icon!=null)
			return icon;
		
		return renderIcon(nodeClassLoader,default_icon_url,showAsLink,size,greyOut);
	}
	
	public ClassLoader getClassLoader()
	{
	    return Thread.currentThread().getContextClassLoader(); 
	}
	
	/**
     * Old one. Need to be merged with other createDefaultIcon
     */
    public Icon createDefaultIcon(String iconUrl,
            boolean isComposite,
            boolean isLink,
            String mimetype,
            int size,
            boolean greyOut)
    {
        logger.debugPrintf("createDefaultIcon [%d,%b,%b,%b]\n",size,greyOut,isComposite,isLink); 

        // for plugins, must use classLoader of plugin class ! 
        ClassLoader classLoader=getClassLoader(); 

        
        // custom Icon URL: 
        if (StringUtil.isEmpty(iconUrl)==false) 
        {
            logger.debugPrintf("createDefaultIcon: I)");
            
            Icon icon=renderIcon(classLoader,iconUrl,isLink,size,greyOut);
         
            if  (icon!=null)
            {
                logger.debugPrintf("createDefaultIcon: I) not NULL\n");
                return icon;
            }
            logger.debugPrintf("createDefaultIcon: I)  NULL\n");
        }

        // default mimetype for Composite nodes.
        // Set to null to trigger 'default' icons further in this method: 
        // 
        if ( (mimetype!=null) && (isComposite) 
                && (mimetype.compareToIgnoreCase("application/octet-stream")==0) 
           )
        {
            mimetype=null;
        }

        
        if (mimetype!=null)
            iconUrl=createMimeTypeIconURLSuffix(mimetype);
        
        // =============================================================================
        // MimeType Icons 
        // =============================================================================

        //
        // The mimetype: "application/octet-stream" is not allowed as 
        // default mimetype for Composite nodes.
        // Set to null to trigger 'default' icons further in this method: 
        // 
        if ( (mimetype!=null) && (isComposite) 
                && (mimetype.compareToIgnoreCase("application/octet-stream")==0) 
           )
        {
            mimetype=null;
        }

        if ((iconUrl==null) && (mimetype!=null))
        {
            iconUrl=createMimeTypeIconURLSuffix(mimetype);
        }
        
        if (iconUrl!=null)
        {
            logger.debugPrintf("createDefaultIcon: using theme mimetype IIb):%s\n",iconUrl);
        
            // try again using full (theme) mimetype path: ./<themes path>/iconURL
            Icon icon = renderIcon(classLoader,mime_icons_theme_path+"/"+iconUrl,isLink,size,greyOut);
            
            if (icon!=null)
                return icon;
        }
                
        // =============================================================================
        // Default Resource Icons (File,Folder,...) 
        // =============================================================================

        logger.debugPrintf("createDefaultIcon: III):%s\n",iconUrl);
        
        if (isComposite)
        {
            iconUrl = default_folder_icon_url;
        }
        else
        {
            iconUrl = default_icon_url;
        }
        
        logger.debugPrintf("createDefaultIcon: IV):%s\n",iconUrl);
        
        Icon icon = renderIcon(classLoader,iconUrl,isLink,size,greyOut);

        if (icon!=null)
            return icon;
        
        return renderIcon(classLoader,default_icon_url,isLink,size,greyOut);
    }
   	
	/**
	 * Returns Icon or broken image icon.
	 * Creates ImageIcon directly from URL, works with animated GIFs as
	 * the icon is not changed
	 */ 
	public Icon createIconOrBroken(ClassLoader optClassLoader,String url)
	{
		logger.debugPrintf("createIconOrDefault:%s\n",url); 
		// Find image and create icon. No Rendering!
		
		URL resolvedUrl;
		
        try
        {
            resolvedUrl = resourceLoader.resolveUrl(optClassLoader,url);
            
            Icon icon=null;
            if (resolvedUrl!=null)
                icon=new ImageIcon(resolvedUrl); 

            if (icon!=null)
                return icon;
        }
        catch (MalformedURLException e)
        {
            logger.debugPrintf("Failed to resolve url:%s\n",url);
        } 

		logger.debugPrintf("Returning broken icon for:%s\n",url);
		return getBrokenIcon(); 
	}
	
	public ImageIcon getBrokenIcon()
	{
		return resourceLoader.getBrokenIcon(); 
	}
    
    /** Returns 'AnimatedIcon' instead of plain 'Icon' */ 
    public AnimatedIcon getAnimatedIcon(String url) throws VlIOException
    {
        try
        {
            return getAnimatedIcon(this.resourceLoader.resolveUrl(null,url));
        }
        catch (MalformedURLException e)
        {
            throw new VlIOException("Failed to resolve url:"+url,e); 
        }
    }
    
	/**
	 *  Load animated image and return as explicit AnimatedIcon class. 
	 *  Currently supports animated gifs only.
	 * @throws VlIOException 
	 */ 
	public AnimatedIcon getAnimatedIcon(URL url) throws VlIOException
	{
	    ImageSequence image;
	    
	    try
	    {
	        image = ImageUtil.loadAnimatedGif(url);
	        return new AnimatedIcon(image);
	    }
	    catch (Exception e)
	    {
	        throw new VlIOException("Failed to load icon url:"+url,e); 
	    }
	}
	
    /**
     * Render a scaled icon and cache it. 
     * Warning: Method does NOT yet work with animated Icons ! 
     */
    public Icon renderIcon(String iconurl, int size)
    {
        return this.renderIcon(null,
                iconurl,
                false, 
                new Dimension(size,size),
                false); 
    }
    
    /**
     * Render a scaled icon and cache it. 
     * Warning: Method does NOT yet work with animated Icons ! 
     */
	public Icon renderIcon(ClassLoader optClassLoader,
	        String iconURL,
			boolean showAsLink,
			int size,
			boolean greyOut)
	{
		return renderIcon(optClassLoader,
		        iconURL,
		        showAsLink,
		        new Dimension(size,size),
		        greyOut);
	}
	
    /**
     * Render a scaled icon and optionally add a linkicon or perform
     * a grey out. 
     * Warning: Method does NOT yet work with animated Icons ! 
     */
	public Icon renderIcon(ClassLoader optClassLoader,
			String iconURL,
			boolean showAsLink,
			Dimension prefSize,
			boolean greyOut)
	{
		logger.debugPrintf("createIcon: %s{%b,%s,%b}\n",iconURL,showAsLink,prefSize,greyOut);

		if (iconURL==null)
			return null;

		Image image=getImageFromHash(iconURL,showAsLink,prefSize,greyOut);

		if (image!=null)
		{
			logger.debugPrintf("Returning hashed icon: %s{%b,%s,%b}\n",iconURL,showAsLink,prefSize,greyOut);
			return new ImageIcon(image);  
		}

		image=findImage(optClassLoader,iconURL);

		// get default broken icon ?
		if (image==null)
		{
			logger.debugPrintf("createIcon: null icon for:%s\n",iconURL); 
			return null; 
		}

		image=iconRenderer.renderIconImage(image,showAsLink,prefSize,greyOut);
		
		if (image!=null)
			putImageToHash(image,iconURL,showAsLink,prefSize,greyOut);
		else
			logger.debugPrintf("createIcon: *** Error: renderIcon failed for non null icon:%s\n",iconURL); 

		return new ImageIcon(image);  
	}
    
	/**
	 * Try to find a specified image, but do no throw an exception if it can't be found. 
	 * Method will return 'null' if the case an image can't be found.
	 */
    public Image findImage(ClassLoader extraLoader,String iconURL)
    {
		URL resolvedurl;
		
        try
        {
            resolvedurl = resourceLoader.resolveUrl(extraLoader,iconURL);
        }
        catch (MalformedURLException e1)
        {
            logger.debugPrintf("Couldn't resolve URL:%s\n",iconURL);
            return null; 
        } 
       
		// return url 
		if (resolvedurl!=null)
		{
			logger.debugPrintf("findIcon:found Icon:%s\n",resolvedurl);

			// Basic checks whether the icon is a valid icon ?  
			try
			{
    			Image image=loadImage(resolvedurl,true);
    			
    			if (image!=null)
    			{
    				logger.debugPrintf("findIcon:returning non null icon:%s\n",resolvedurl);
    				return image; 
    			}
 			}
			catch (Exception e)
			{
			    logger.debugPrintf("findImage(): Exception when constructing icon from:%s\n",iconURL);
			}
		}

		logger.debugPrintf("Did NOT find Icon:%s\n",iconURL);
		return null; 
	}
	
	private String createMimeTypeIconURLSuffix(String mimetype)
	{
		// tranform mimetype "<type>/<subtype>" into filename
		// "<type>-<subtype>"

		String iconname="";

		for (int j = 0; j < mimetype.length(); j++)
		{
			if (mimetype.charAt(j) == '/')
				iconname += "-";
			else
				iconname += mimetype.charAt(j);
		}

		iconname += ".png"; // .gif ? 
		return iconname;
	}
    
    public Image loadImage(URL url) throws VlIOException
    {
        return loadImage(url,true); 
    }
	
	/** 
	 * Load imageIcon, optionally uses cache.
	 * Use this method only for relative small icons and NOT for big images.
	 *  
	 * @throws VlIOException 
	 */
	public Image loadImage(URL url, boolean useCache) throws VlIOException
    {
	    // get/create "raw" icon from cache;  
	    Image image=null;
	    
	    if (useCache)
	    {
	        image=this.getImageFromHash("raw-"+url.toString(),false,null,false); 
	        if (image!=null)
	            return image;
	    }
	    
	    try
	    {
	        logger.infoPrintf("+++ loading imageicon:%s\n",url); 
	        String urlStr=url.toString().toLowerCase();
	        // Direct .ico support: do not use resource loader to resolve icons
	        if (urlStr.endsWith(".ico"))
	        {
                image=resourceLoader.getIcoImage(url);
	        }
            else
            {
                    // ---
	            // Use Java 1.5 ImageIO! 
	            // ---
	            image=ImageIO.read(url);
            }
	        
	        if (image!=null)
	        {
	            if (useCache)
	                this.putImageToHash(image,"raw-"+url.toString(),false,null,false);
	            
	            return image;
	        }
	        else
	        {
	            throw new VlIOException("ImageIO Returned NULL image for:"+url);
	        }
	    }
	    catch (Exception ex)
	    {
	        throw new VlIOException("Could load image:"+url,ex); 
	    }
    }

	private void putImageToHash(Image image,String iconURL, boolean showAsLink, Dimension size, boolean greyOut)
	{
		if (image==null)
			return; 

		synchronized(this.iconHash)
        {
    		String id=createHashID(iconURL,showAsLink,size,greyOut); 
    		this.iconHash.put(id,image);
        }
	}

	private String createHashID(String iconURL, boolean showAsLink,Dimension size, boolean greyOut)
	{
		String sizeStr="-";
		if (size!=null)
			sizeStr=size.height+"-"+size.width;
		
		return iconURL+"-"+showAsLink+"-"+sizeStr+"-"+greyOut; 
	}

	private Image getImageFromHash(String iconURL, boolean showAsLink, Dimension size, boolean greyOut)
	{
	    synchronized(this.iconHash)
	    {
    		String id=createHashID(iconURL,showAsLink,size,greyOut);
    		Image image=this.iconHash.get(id);
    		logger.debugPrintf("> getIconFromHash:%s for '%s'\n",((image!=null)?"HIT":"MISS"),id);
    		return image; 
	    }
	}

	/** Clear Icon Cache */ 
	public void clearCache()
	{
		this.iconHash.clear(); 
	}

	/** Returns all icons found on the icon path */ 
	public StringList getIconList()
	{
		StringList list=new StringList(); 
		VFSClient vfs=UIGlobal.getVFSClient();

		for (VRL path:iconsPath)
		{
			try
			{
				logger.debugPrintf("icon path    = %s\n",path); 

				VFSNode[] nodes = vfs.list(path);

				for (VFSNode node:nodes)
				{
					//String ext=node.getExtention(); 

					if (node.isFile())
					{
						VRL iconVRL=path.append(node.getBasename()); 
						list.add(iconVRL.toString()); 
						logger.debugPrintf(" - adding icon:%s\n",iconVRL); 
					}
				}
			}
			catch (Exception e)
			{
				logger.debugPrintf("***Error: Couldn't list icon path:%s\n",path); 
			}
		}

		return list; 
	}
}
