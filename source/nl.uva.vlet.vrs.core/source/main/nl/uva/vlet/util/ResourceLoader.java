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
 * $Id: ResourceLoader.java,v 1.19 2011-06-20 14:06:18 ptdeboer Exp $  
 * $Date: 2011-06-20 14:06:18 $
 */ 
// source: 

package nl.uva.vlet.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.ResourceReadAccessDeniedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VStreamReadable;
import nl.uva.vlet.vrs.io.VStreamWritable;

/**
 * Generic ResourceLoader class which supports VRLs, URIs and URLs. 
 * <p>
 * If the (VRS) Registry and the URLStreamFactory are properly initialized the URL
 * class can handle VRLs.
 * This class can only be used after a successful Registry initialization. 
 *   
 * @author Piter.T. de Boer
 */
public class ResourceLoader
{
	/** Default UTF-8 */
    public static final String CHARSET_UTF8     = "UTF-8";
    
    /** Legacy UTF-16 Big Endian */ 
    public static final String CHARSET_UTF16BE  = "UTF-16BE";
    
    /** Legacy UTF-16 Little Endian */ 
    public static final String CHARSET_UTF16LE  = "UTF-16LE";
    
    /** 7-bits (US) ASCII, mother of all ASCII's */
    public static final String CHARSET_US_ASCII = "US-ASCII";
    
    /** 8-bits US/Euro 'standard' encoding */  
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    
    /** Latin is an alias for ISO-8859-1 (All Roman/Latin languages) */ 
    public static final String CHARSET_LATIN      = "ISO-8869-1";

    /** Old EXTEND (US) ASCII Code Page 437 */  
    public static final String CHARSET_CP437      = "CP437";
    
    /** Default is UTF-8 */ 
    public static final String DEFAULT_CHARSET = CHARSET_UTF8;
    
    public static final String charEncodings[]=
    	{
    		CHARSET_UTF8,
    		CHARSET_UTF16BE,
    		CHARSET_UTF16LE,
    		CHARSET_US_ASCII,
    		CHARSET_ISO_8859_1,
    		CHARSET_LATIN,
    		CHARSET_CP437
    	};
    
    private static ResourceLoader instance;

    private static ClassLogger logger;

    private static String default_brokenimage_url= "default/brokenimage.png"; 
    
    // =================================================================
    // Static methods 
    // ================================================================= 
    
    static
    {
        logger = ClassLogger.getLogger(ResourceLoader.class);
        //logger.setLevelToDebug();
    }
    
	public static String[] getCharEncodings()
	{
        return charEncodings; 
	}
	
	public static ResourceLoader getDefault()
	{
		if (instance==null)
			instance=new ResourceLoader(VRSContext.getDefault(),null);
		
		return instance; 
	}
    
    // =================================================================
    // Object methods 
    // =================================================================
	
	protected String charEncoding=DEFAULT_CHARSET; 
    
	VRSContext context=null;

    private VRSClient vrsClient=null;

    private ClassLoader classLoader=null; 

    public ResourceLoader(VRSContext vrsContext)
    {
        this.context=vrsContext; 
    }
    
    /**
     * Initialize ResourceLoader with extra URL search path. 
     * When resolving a relative URL this path will be search as well. 
     * 
     * @param context  VRSContext
     * @param urls  URL search path 
     */
    public ResourceLoader(VRSContext contxt,URL urls[]) 
    {
    	this.context=contxt;
    	init(null,urls);
    }
    
    /**
     * Initialize ResourceLoader with extra URL search path. 
     * When resolving a relative URL this path will be search as well. 
     * 
     * @param context  VRSContext
     * @param parentClassLoader Parent class loader for hierarchical class loading.
     * @param urls  URL search path 
     */
    public ResourceLoader(VRSContext contxt,ClassLoader parentClassLoader,URL urls[]) 
    {
        this.context=contxt;
        init(parentClassLoader,urls);
    }
    
    private void init(ClassLoader parentClassLoader,URL urls[])
    {
        if (urls!=null)
        {
            if (parentClassLoader==null) 
                parentClassLoader=Thread.currentThread().getContextClassLoader(); 
            
            classLoader= new URLClassLoader(urls,parentClassLoader);
        }
        else if (parentClassLoader!=null)
        {
            classLoader=parentClassLoader; 
        }
        
    }

    /** 
     *  Load (a)synchronously an image specified by VRL URI.
     *  Support URLs and VRLs as well as (CLASSPATH) relative URLs 
     *  (for example: "icons/myicon.gif").
     *  <p> 
     *  Checks whether 'url' is on of the following: <br>
     *  I) relative URL from context classpath <br>
     *  II) relative URL from default classpath <br>
     *  III) global URL <br>
	 * @throws VlException 
     *  
     */
    
    public Image getImage(VRL location) throws VlException
    {
        return getImage(location.toURL()); 
    }
    
    public Image getImage(String url) throws VlException
    {
        try
        {
            URL resolvedURL=this.resolveUrl(null,url);
            
            if (resolvedURL!=null)
                return this.getImage(resolvedURL);
            
            throw new ResourceNotFoundException("Couldn't resolve url:"+url); 
           
        }
        catch (MalformedURLException e)
        {
            throw new VlIOException("Couldn't resolve URL:"+url); 
        }
  
    }

    public Image getImage(URL url) throws VlException
    {
        // .ico support ! 
      if (new VRL(url).getExtension().toLowerCase().compareTo("ico")==0)
      {
          logger.debugPrintf("getImage: >>> Loading ico file:%s\n",url);
          return getIcoImage(url); 
      }
      
      try
      {
            Image image;
            image = ImageIO.read(url);
            return image;
      }
      catch (IOException e)
      {
            throw new VlIOException("Failed to read image:"+url,e); 
      } 
    } 

    
    /**
     * Just fetch uncached icon from specified (relative) url or full path. 
     * This class does not do any caching or scaling.  
     * Use (gui.util) IconRender.getIcon() class for rendered&cached icons.
     * 
     * @param vrl
     * @return
     * @throws VlException 
     * @throws VlException
     */
	public  ImageIcon getIcon(String urlstr) throws VlException 
    {
	    try
	    {
	        return getIcon(resolveUrl(null,urlstr));
	    }
	    catch (MalformedURLException e)
	    {
	        throw new VlIOException("Couldn't resolve URL:"+urlstr); 
	    }
    }
	
    public ImageIcon getIcon(URL url) throws VlException
    {
        Image image = getImage(url); 
        
        if (image!=null) 
            return new ImageIcon(image);
        
        return null; 
    }

    /**
     *  Tries to load resource from relative or absolute url:  <br>
     *  - I) get current classLoader to resource 'urlstr'<br>
     *  - II) get thread classload to resolve 'urlstr'<br>
     *  - III) tries if urlstr is an absolute url and performs openConnection().getInputStream()<br> 
     *    
     * @param urlstr can be boths relative (classpath) url or URI
     * @return InputStream
     * @throws VlIOException 
     * @throws VlIOException
     */
    public InputStream getInputStream(String urlstr) throws VlIOException
    {
        try
        {
            return getInputStream(resolveUrl(null,urlstr));
        }
        catch (MalformedURLException e)
        {
          throw new VlIOException("Couldn't resolve URL:"+urlstr); 
        }
        
    }

   /**
    * Returns an inputstream from the specified URI.
    * 
    * @param uri
    * @return
    * @throws VlException
    */

    public  InputStream getInputStream(URL url)
            throws VlIOException
    {
        if (url==null)
            return null; 
        
        try 
        {
			return url.openConnection().getInputStream();
        }
        catch (IOException e)
        { 
			throw new VlIOException("Cannot get inputstream from:"+url+"\n"+e.getMessage(),e); 
		} 
        
    }

    public InputStream getInputStream(VRL vrl)
    throws VlIOException, VRLSyntaxException
    {
        try 
        {
            // use URL compatible method 
            return vrl.toURL().openConnection().getInputStream();
        }
        catch (IOException e)
        {
            // use VRS instead ?  
            throw new VlIOException("Cannot get inputstream from"+vrl+"\n"+e.getMessage(),e); 
        }    
 
    }
    
	public  String getText(VRL location, String charset) throws VlException
	{
		 InputStream inps=getInputStream(location); 
	     return getText(inps,charset); 
	}
	
    public String getText(URL url) throws VlException
    {
        return getText(new VRL(url)); 
    }

    public  String getText(VRL loc) throws VlException
    {
		 InputStream inps=getInputStream(loc); 
	     return getText(inps,null);  
    }
    

    public byte[] getBytes(VRL loc) throws VlException
    {
        InputStream inps=getInputStream(loc); 
        return getBytes(inps);
    }
    
    public byte[] getBytes(String pathOrUrl) throws VRLSyntaxException, VlIOException
    {
        InputStream inps=getInputStream(pathOrUrl); 
        return getBytes(inps);
    }   
    

    public  String getText(VNode node) throws VlException
    {
    	return getText(node,null);
    }
    
	public String getText(VNode vnode, String charset) throws VlException
	{
        if (vnode==null) 
            return null; 
        
        
        InputStream inps = null;
        
        if (charset==null)
        {
        	charset = vnode.getCharSet();
        }
        
        String txt=null; 
        
        if (vnode instanceof VStreamReadable)
        {
        	
        	try
        	{
            	inps = ((VStreamReadable) vnode).getInputStream();
        		txt=getText(inps,charset);
        		return txt;
        	}
        	finally
        	{
        		// Must always close InputStream
        		// Some VRS implementations depend on this call ! 
        		// 
        		if (inps!=null)
        		{
					try
					{
						inps.close();
					}
					catch (IOException e)
					{
						; //ignore
					}
        		}
        	}
        	
        }
        else
        {
            throw new NotImplementedException("Cannot get InputStream from node:" + vnode);
        }
 
    }
  
    /**
     * Read text from input stream in encoding 'charset'. (Default is utf-8)  
     * Does this line by line.
     * Line seperators might be changed!
     *  
     *  
     * @param inps
     * @param charset
     * @return
     * @throws VlIOException
     */
   	public  String getText(InputStream inps,String charset) throws VlIOException
	{
   	   if (charset==null) 
   		   charset=charEncoding; 

   	   // just read all: 
   	   try
   	   {
   		   byte bytes[]=getBytes(inps);
   		   return new String(bytes,charset); 
   	   }
   	   catch (UnsupportedEncodingException e1)
   	   {
   		   throw new VlIOException("***Error: Exception:", e1);
   	   }
        
    }
    
    // ========================================================================
    // Misc Functions 
    // ========================================================================
   	
   	/** Read all bytes from inputstream */ 
 	public byte[] getBytes(InputStream inps) throws VlIOException  
	{
 		ByteArrayOutputStream bos = new ByteArrayOutputStream();
 		
		byte[] buf = new byte[32*1024]; // typical TCP/IP packet size: 
		int len=0;
		
		try
		{
			while ((len = inps.read(buf)) > 0) 
			{
				bos.write(buf, 0, len);
			}
		}
		catch (IOException e)
		{
			throw new VlIOException("Couldn't read from input stream",e); 
		}
		
		byte[] data = bos.toByteArray();
		return data; 
	}
 	
    public  OutputStream getOutputStream(String locstr) throws VlException
    {
        return getOutputStream(new VRL(locstr));
    }
    
    public OutputStream getOutputStream(VRL vrl) throws VlException
    {
        return this.getVRSClient().openOutputStream(vrl); 
    }
    
    public synchronized VRSClient getVRSClient()
    {
        if (vrsClient==null)
            this.vrsClient=new VRSClient(context);
        
        return vrsClient;  
    }
    
    /** 
     * Returns Icon or in case of an exeption :  'default/brokenimage.png'. 
     * Always returns something and doesn not throw an exception. 
     */  
    public Icon getIconOrDefault(String url)  
	{
		ImageIcon icon=null;
		
		try
		{
		    icon = getIcon(url);
		}
		catch (Exception e)
		{
		    logger.warnPrintf("Failed loading icon:%s\n",url); 
		    logger.warnPrintf("Exception %s\n",e); 
		}
		
		if (icon==null) 
			icon=getBrokenIcon(); 
		
		return icon;
	}
	
    
    /** Return broken image icon */ 
    public ImageIcon getBrokenIcon()
    {
        // return direct created icon: don't use other methods as some error occured ! 
        return new ImageIcon(
                Thread.currentThread().getContextClassLoader()
                    .getResource(default_brokenimage_url));
    }
    
	 /**
     * Save properties file to specified location.
     * @see #saveProperties(VRL,String,Properies)  
     */
    public void saveProperties(VRL loc,Properties props)
            throws VlException
    {
    	saveProperties(loc,"VLET properties file",props); 
    }
    
    /**
     * Save properties file to specified location. 
     * When the VRS environment is configured, any VRL can be used. 
     * But at startup these locations might be limitid to local locations.  
     */
    public void saveProperties(VRL loc, String comments, Properties props)
                throws VlException
    {
        try
        {
        	// Use URLConnection.getOutputStream since VRS now
        	// should maskerade VRL/VRS OutputStreams ! 
        
            // Get *current* VRSContext. 
            // during startup/boostrap this context might change!!! 
             
            VNode parent = this.context.openLocation(loc.getParent());
            String name=loc.getBasename(); 
            
            // create if not exist: 
            if (parent instanceof VDir)
            {	
            	VDir dir=(VDir)parent; 
            	if (dir.existsFile(name)==false)	 
            		dir.createFile(name); 
            	
            }

            VNode file = this.context.openLocation(loc);

            if ((file instanceof VStreamWritable)==false)
            	throw new nl.uva.vlet.exception.ResourceTypeMismatchException("Protocol does not support OutputStreams:"+loc); 
            
            
            OutputStream outps=((VStreamWritable)file).getOutputStream(); 
            props.store(outps, comments);
            

        }
        catch (IOException e)
        {
            throw new VlIOException(e.getMessage(), e);
        }
    }
	
    /**
     *  Load properties file from specified location.<br> 
     *  <b>IMPORTANT</b>: When this method is used and the URL 
     *  stream factory handler HAS NOT BEEN SET, only 
     *  default url schemes can be used ! (file:/,http://). 
     */
   
   public Properties loadProperties(VRL loc) throws VlException 
   {
    	// must use URL so it works during bootstrap ! 
    	// (After bootstap new schemes will be possible)  
    	URL url=loc.toURL();
        
    	return loadProperties(url); 
   }
    
   public  Properties loadProperties(URL url) throws VlException 
   {
         Properties props = new Properties();

        try
        {
            InputStream inputs = this.getInputStream(url); 
            props.load(inputs);
            logger.debugPrintf("Read properties from:%s\n",url); 
        }
        catch (IOException e)
        {
            throw new VlIOException(e.getMessage(), e);
        }
        // in the case of applet startup: Not all files are 
        // accessable, wrap exception for gracfull exception handling. 
        catch (java.security.AccessControlException ex)
        {
            throw new ResourceReadAccessDeniedException("Security Exception: Permission denied for:"+url,ex); 
        }
        
	    for (Enumeration<Object> keys =props.keys();keys.hasMoreElements();) 
	    {
	        String key=(String)keys.nextElement(); 
	        String value=props.getProperty(key);
	        logger.debugPrintf("Read property='%s'='%s'\n",key,value);
	    }
        
        return props;
	}

	/** Writes String contents to remote location */ 
	public void setContents(VRL vrl, String txt) throws VlException
	{
	    setContents(vrl,txt,this.getCharEncoding()); 
	}

	/**
	 * Writes String contents to remote location using optional encoding
	 * Tries to truncate resource or delete original resource
	 */ 
	public void setContents(VRL vrl, String txt,String encoding) throws VlException
	{
		VNode node = this.context.openLocation(vrl); 
		OutputStream outps=null; 
		
		if (encoding==null)
			encoding=this.getCharEncoding(); 
		
        if (node instanceof VStreamWritable)
        {
        	outps=((VStreamWritable) node).getOutputStream();
        }
        else
        {
        	throw new NotImplementedException("Cannot get OutputStream from location:" + vrl);
        }
        
		OutputStreamWriter writer;
		
		try
		{
			writer = new OutputStreamWriter(outps, encoding);
			writer.write(txt);
			writer.close();
		}
		catch (UnsupportedEncodingException e)
		{
			throw new VlIOException("UnsupportedEncoding!", e);
		}
		catch (IOException e)
		{
			throw new VlIOException(e);
		}
	}

	/** Returns char encoding which is used when reading text. */ 
	public String getCharEncoding()
	{
		return charEncoding; 
	}
	
	/** Specify char encoding which is used when reading text. */ 
	public void setCharEncoding(String encoding)
	{	
		 charEncoding=encoding;  
	}

   
	/** Read PROPRIATIARY: .ico file and return Icon Image */ 
    public BufferedImage getIcoImage(URL iconurl) throws VlIOException
    {
        try
        {
            InputStream inps=getInputStream(iconurl);  
            ImageInputStream in = ImageIO.createImageInputStream(inps);

            nl.ikarus.nxt.priv.imageio.icoreader.obj.ICOFile f;
            f = new  nl.ikarus.nxt.priv.imageio.icoreader.obj.ICOFile(in);

            // iterate over bitmaps: 
            Iterator<?> it = f.getEntryIterator();

            Vector<BufferedImage> bitmaps=new Vector<BufferedImage>();
            
            BufferedImage biggestImage=null; 
            int biggestSize=0; 
            
            while(it.hasNext()) 
            {
                nl.ikarus.nxt.priv.imageio.icoreader.obj.IconEntry ie = (nl.ikarus.nxt.priv.imageio.icoreader.obj.IconEntry) it.next();

                try
                {
                    BufferedImage img = ie.getBitmap().getImage();
                    bitmaps.add(img); 
                    int size= img.getWidth()*img.getHeight(); 
                    if (size>biggestSize)
                    {
                        biggestSize=size; 
                        biggestImage=img; 
                    }
                            //System.err.println(" - width="+img.getWidth());  
                    //System.err.println(" - height="+img.getHeight());  

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return  biggestImage;  
            
        }
        catch (IOException e)
        {
            throw new VlIOException("Couldn't read .ico link:"+iconurl,e); 
        }
    }

    public String getText(String url) throws VRLSyntaxException, VlIOException
    {
        return this.getText(this.getInputStream(url),this.getCharEncoding());  
    }


    /**
     * Resolve relative resource String and return absolute URL.  
     * The URL String can be matched against the optional ClassLoader in the case
     * the URL points to a resource loaded by a custom ClasLoad in for exampl a plugin 
     * nd is not visible from the ResourceLoader. 
     * If the ResourceLoader has been initialized with extra (ClassPath) URLs, these
     * will be searched also.  
     *   
     * @param optClassLoader Optional ClassLoader from Plugin Class Loader 
     * @param url relative URL String, might be absolute but then there is nothing to 'resolve'.  
     * @return resolved Absolute URL 
     * @throws MalformedURLException 
     */
    public URL resolveUrl(ClassLoader optClassLoader,String url) throws MalformedURLException
    {
        URL resolvedUrl=null;
        
        logger.debugPrintf("resolveIconUrl:%s\n",url);

        if (url==null)
            throw new NullPointerException("URL can not be null!");  

        // (I) First optional Class Loader ! 
        if (optClassLoader!=null)
        {
            resolvedUrl=optClassLoader.getResource(url);
            if (resolvedUrl!=null)
                logger.debugPrintf("resolveUrl() I: Icon using extra class loader:%s\n",resolvedUrl);
        }
        
        // (II) Use Reource Classloader  
        if ((resolvedUrl==null) && (this.classLoader!=null))
        {
            resolvedUrl=this.classLoader.getResource(url);
            if (resolvedUrl!=null)
                logger.debugPrintf("resolveURL() II:found Icon using resource classloader:%s\n",resolvedUrl); 
        }
        
        // (III) Check default (global) classloader for icons which are on the classpath 
        if (resolvedUrl==null)
        {
            resolvedUrl=this.getClass().getClassLoader().getResource(url);
            if (resolvedUrl!=null)
                logger.debugPrintf("resolveURL() III:found Icon using global classloader:%s\n",resolvedUrl); 
        }

        if (resolvedUrl==null)
            resolvedUrl=new URL(url); 

        return resolvedUrl;    
    }

//    /** Returns current search path */ 
//    public URL[] getSearchPath()
//    {
//        URL urls[]=null;
//        
//        if (this.classLoader!=null)
//            urls=this.classLoader.getURLs();
//        
//        return urls;
//    }


   
}
