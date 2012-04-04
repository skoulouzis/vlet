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
 * $Id: GlobalUtil.java,v 1.13 2011-06-24 13:06:04 ptdeboer Exp $  
 * $Date: 2011-06-24 13:06:04 $
 */ 
// source: 

package nl.uva.vlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;


import nl.uva.vlet.exception.ResourceReadAccessDeniedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.io.StreamUtil;
import nl.uva.vlet.vrl.VRL;

/** 
 * Global Helpers method outside Global to avoid Global initialization!
 * <p> 
 * The methods in this class can be used during initialization to avoid
 * circular dependencies.
 * Also at startup the ResourceLoader can not be used since this needs an initialized
 * VRS Registry!
 * In applet or service mode, some methods might not work depending on the security context. 
 */
public class GlobalUtil
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(GlobalUtil.class); 
    }
    
    /** Load properties from this URL */ 
    public static Properties loadPropertiesFromURL(URL url) throws VlException
    {
        Properties props = new Properties();

        try
        {
            logger.debugPrintf("Loading classpath properties from:%s\n",url);

            InputStream inputs=null;
            if (url!=null) 
                inputs=url.openConnection().getInputStream();

            if (inputs!=null)
                props.load(inputs);
        }
        catch (IOException e)
        {
            throw new VlIOException(e.getMessage(), e);
        }
        // in the case of applet startup: Not all files are 
        // Accessible, wrap exception for graceful exception handling. 
        catch (java.security.AccessControlException ex)
        {
            throw new ResourceReadAccessDeniedException("Security Exception: Permission denied for:"+url,ex); 
        }

        return props;
    }

    /**
     * Load a property file specified on the classpath or URL. 
     */
    public static Properties loadPropertiesFromClasspath(String urlstr) throws VlException 
    {
        //      check default classpath: 
        URL url=GlobalUtil.class.getClassLoader().getResource(urlstr); 

        if (url==null)
            //  check context classpath 
            url = Thread.currentThread().getContextClassLoader().getResource(urlstr);

        if (url==null)
            return new Properties(); 

        return loadPropertiesFromURL(url); 
    }
   
    /** Load properties using URL stream readers */ 
    public static Properties staticLoadProperties(VRL vrl) throws VlException
    {
        try
        {
            URL url=vrl.toURL();
            InputStream inps = url.openConnection().getInputStream(); 
            Properties props=new Properties(); 
            props.load(inps);
            return props;
        }
        catch (Exception e)
        {
            throw new VlException("Properties Exception","Couldn't load properties from:"+vrl,e);
        }
    }
    
    /** 
     * Save properties using URL stream readers. Only works for file:/// URL!
     */ 
    public static void staticSaveProperties(VRL vrl,String optComments,Properties props) throws VlException
    {
        if (optComments==null)
            optComments="";
        
        try
        {
            OutputStream outps=getFileOutputStream(vrl.getPath()); 
            props.store(outps,optComments); 
            outps.close(); 
        }
        catch (Exception e)
        {
            throw new VlException("Properties Exception","Couldn't load properties from:"+vrl,e);
        }
    }

    public static boolean existsPath(String path)
    {
        return new java.io.File(VRL.uripath(path)).exists(); 
    }
    
    /** Pref VFS initialization file copy. */  
    public static void copyFile(String source,String destination) throws FileNotFoundException, VlException
    {
        java.io.File sourceFile=new java.io.File(VRL.uripath(source)); 
        FileInputStream finput=new FileInputStream(sourceFile); 
        FileOutputStream foutput=new FileOutputStream(VRL.uripath(destination)); 
        
        StreamUtil.copyStreams(finput,foutput);
        
        try { finput.close(); } catch (Exception e) { ; }   
        try { foutput.close(); } catch (Exception e) { ; }  
        
        return; 
    }
    
    /** Whether paths exists and is a file */ 
    public static boolean existsFile(String filePath,boolean mustBeFileType)
    {
        if (filePath==null)
            return false;
        
        java.io.File file=new java.io.File(VRL.uripath(filePath)); 
        if (file.exists()==false)
            return false; 
                
        if (mustBeFileType)
            if (file.isFile())
                return true;
            else
                return false; 
        else
            return true; 
    }

    /** Whether paths exists and is a directory  */ 
    public static boolean existsDir(String dirPath)
    {
        if (dirPath==null)
            return false;
        
        java.io.File file=new java.io.File(VRL.uripath(dirPath)); 
        if (file.exists()==false)
            return false; 
                
        if (file.isDirectory())
            return true;
            
        return false; 
    }

    /** list directory: returns (URI) normalized paths  */ 
    public static String[] list(String dirPath)
    {
        java.io.File file=new java.io.File(VRL.uripath(dirPath)); 
        if (file.exists()==false)
            return null; 
                
        if (file.isDirectory()==false)
            return null;
        
        String strs[]=file.list();
        if ((strs==null) || (strs.length<=0)) 
            return null; 
        
        // sanitize: 
        for (int i=0;i<strs.length;i++)
            strs[i]=VRL.uripath(dirPath+"/"+strs[i]); 
    
        return strs; 
    }
    
    public static boolean deleteFile(String filename)
    {
        java.io.File file = new java.io.File(VRL.uripath(filename)); 
        if (file.exists()==false)
            return false; 
        return file.delete();
    }

    /** Open local file and return inputstream to read from. */ 
    public static FileInputStream getFileInputStream(String filename) throws FileNotFoundException
    {
        return new FileInputStream(new java.io.File(VRL.uripath(filename))); 
    }

    /** Open local file and return outputstream to write to */ 
    public static FileOutputStream getFileOutputStream(String filename) throws FileNotFoundException
    {
        return new FileOutputStream(new java.io.File(VRL.uripath(filename))); 
    }

    /** 
     * Read plain ASCII file and return as String. 
     * File may not be bigger then 1 MiB.
     */  
    public static String readText(String filename) throws Exception
    {
        java.io.File file=new java.io.File(VRL.uripath(filename)); 
        int len=(int)file.length();
        if (len>1024*1024)
            len=1024*1204; 
        byte buffer[]=new byte[len+1];
            
        FileInputStream finps=new FileInputStream(file);        
        int numRead=StreamUtil.syncReadBytes(finps,0,buffer,0,len);
        // truncate buffer in the case of a read error: 
        buffer[numRead]=0; 
        
        try {finps.close(); } catch (Exception e) { ; } ; 
         
        return new String(buffer,"ASCII"); 
    }

    public static void mkdir(String dirpath)
    {
        java.io.File file=new java.io.File(VRL.uripath(dirpath)); 
        file.mkdir();
    }
    
    public static void mkdirs(String dirpath)
    {
        java.io.File file=new java.io.File(VRL.uripath(dirpath)); 
        file.mkdirs(); 
    }

    
}

