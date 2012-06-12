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
 * $Id: VRL.java,v 1.20 2011-12-01 14:52:00 ptdeboer Exp $  
 * $Date: 2011-12-01 14:52:00 $
 */ 
// source: 

package nl.uva.vlet.vrl;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.URLUTF8Encoder;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;


/**
 * The Virtual Resource Locator (VRL) class is an URI compatible class. 
 * It specifies the location of a resource allowing 'virtual' schemes 
 * which  do not necessarily map to physical protocols like "ftp" or "srm" but allow
 * for virtual schemes like "rfts" (Reliable File Transfer), "vfs" (logical VFS names) 
 * or other (web/grid) services which can specify their own 'scheme'.  
 * <p> 
 * This object is not mutable: Its fields may never be changed, so they can be used
 * as atomic identifiers in for example Hashtables. The methods hashcode() and equals() 
 * use the normalized String representation to compare with. <br> 
 * Use duplicators and/or constructors to modify VRLs!
 * <p><b> URI/URL Compatibility</b><br>
 * This class is also compatible with the URL class because the URLHandlerFactory 
 * is set to support VRL schemes as well. <br>
 * Use method VRL.toURL() to create (java) URL objects.<br>
 * Use method VRL.toURI() to create (java) URI objects.<br> 
 * <p><b>Relative VRLs</b><br>
 * Relative VRLs are like relative URIs, resource locators which <i>do not have a scheme</i>.<br>
 * for example "./relative/path"  or "#relativeLink". <br>
 * Relative VRLs can be resolved (like relative URIs) against an absolute VRL or "base location". 
 * Use VRL.resolve(String) to resolve relative VRLs. 
 * <p> <b>Normalization and decoding:</b><br>
 * VRLs are normalized and always decoded to provide an absolute reference
 * to a location (if not relative).  <br>
 * For example the URL: "file:/A%20File" is decoded and normalized to the URI: "file:/A File/".<br>
 * Also extras slashes like in "file:////A File" will be normalized to "file:/A File".<br>
 * This to make sure VRLs can be compared using their String representation.<br>
 * <p>
 * Also Windows paths are normalized for example the URI "file://C:\Windows\path" becomes 
 * the normalized URI: "file:/C:/Windows/Path".  
 * Current used (URI compatible) syntax is: <br>
 * <pre> 
 * VRL ::=  &lt;scheme&gt;:[//&lt;AuthenticationInfo&gt;][/&lt;path&gt;][?&lt;QueryString&gt;][#&lt;fragment&gt;]
 *
 * &lt;AuthenticationInfo&gt;  ::= [&lt;UserInfo&gt;@]&lt;Hostname&gt;[:Port]
 *    
 * &lt;UserInfo&gt;  ::=  &lt;UserName&gt;[:&lt;Password&gt;]
 * 
 * &lt;QueryString&gt;  ::=  &lt;Property&gt;[&amp;&lt;QueryString&gt;]
 * </pre>
 * Absolute URI Examples: <br>
 * <pre>
 *  - "guid:abcdefg-hiklmnop-qrstu-vwxyz" : Reference type URI (no slash after scheme) 
 *  - "file:/localpath/file" :  path type URI without Authorization path (single slash after scheme) 
 *  - "gsiftp://server:port/absolute/path" full URI with Authorization (double slash after scheme) 
 * </pre>
 * Relative URI examples: 
 * <pre>
 *  -  "./relative/path" 
 *  -  "#labelReference"
 *  -  "?queryParamer1=foo;par2=bar;"
 * </pre>  
 * Other compatibility remarks: <br>
 * <ul>  
 * <li> To specify relative paths in a VRL, use the '~' (tilde) before the path to specify 
 *  it is a relative path. Support of this character may depend on the protocol implementation.
 *  for example the URL: "gftp://remotehost/~/mystuff".  <br>
 *  Most VRS and VFS implementation support this character. This is tested by the VRS Unit tests!
 * <li> The usage of double slashes in Globus URLs in for example the URL: "file://host//absolute/path" as 
 *  to distinguish between "file://host/relative-path-to-users-home"    
 *  in Globus URLs is not a standard recognized by (most) URI implementations.
 *  Better is to use the '~' although this isn't a standard either it makes relative path handling consistant. 
 * <li> The usage of (again in Globus URLs) to end a Directory URL with a slash ('/') as
 *  in for example the URL: "file://host/path/to/remote/directory/"  
 *  also is not a standard and most VRS/VFS implementation will dynamically check whether the 
 *  specified path  is a directory or a file. 
 * <br>
 * @author P.T. de Boer
 * 
 * @see VRLStreamHandler
 * @see VRLConnection
 * @see VRLStreamHandlerFactory
 * @see java.net.URI
 * @see java.net.URL
 */

public final class VRL implements Cloneable, Serializable,
    Comparable<VRL>
{
    // === 
    // Class stuff
    // === 
    

    /** serial ID */ 
    private static final long serialVersionUID = -2053510137022543863L;

    /** Path seperator character for URIs  = '/' */ 
    public final static char SEP_CHAR='/';
    
    /** Windows backslash or '\\' */
    public final static char ANTI_SEP_CHAR='\\';
    
    /** Seperator character but as String ( "/" ) */
    public final static String SEP_CHAR_STR=""+SEP_CHAR;
    
    /** Default URI attribute seperator '&amp;' in URL: "http://../?ArgumentA=1&amp;ArgumentB=2" */ 
    public static final String ATTRIBUTE_SEPERATOR = "&";
    
    /** URI list seperator ';' used to parse (create) URI string representations */ 
    public static final String URI_LIST_SEPERATOR = ";"; 

 // =======================================================================
    // Class Methods
    // =======================================================================

    public static String uripath(String orgpath) 
    {
        return uripath(orgpath,true); 
    }
   
    public static String uripath(String orgpath,boolean makeAbsolute)
    {
        return uripath(orgpath,makeAbsolute,java.io.File.separatorChar); 
    }
    
    /**
     * Produce URI compatible path and do
     * other normalization. :
     * 
     * <li> Flip <code>localSepChar</code> to (URI compatible) forward slashes 
     * <li> changes DOS paths into absolute DOS paths:  for example: 'c:'  into '/c:/'
     * <li> prefixes all paths with '/' to make it absolute , unless makeAbsolute=false
     * 
     * @param orgpath      : original path. 
     * @param makeAbsolute : prefix optional relative paths with '/' to make them absolute. 
     * @param localSepChar : seperator char to 'flip' to URI seperator char '/'   
     */
    
    public static String uripath(String orgpath,boolean makeAbsolute,char localSepChar)
    {
        if (orgpath==null) 
          return ""; // default path="";  
       
        if (orgpath.length()==0) 
          return ""; // default path="";

        //
        // Ia) If platform seperator char if '\', replace with URI seperator '/'.
        //
        
        String newpath=orgpath;
        // For now convert ALL backslashes: 
        newpath=orgpath.replace('\\',SEP_CHAR); 
     
        //
        // Ib) strip optional double slashes "c:\subdir\/path" =>  "c:/subdir//path" =>  c:/subdir/path
        //
         
        newpath=newpath.replaceAll(SEP_CHAR+"+",SEP_CHAR_STR);
      
        //
        // II) Convert relative path to absolute by inserting '/' 
        //     c:/subdir/path => /c:/subdir/path
      
      if (makeAbsolute==true)
      {
          if (newpath.charAt(0)!=SEP_CHAR)
          {
              newpath=SEP_CHAR+newpath;
          }
      }

      // 
      // III) Windows conversion 
      // "C:" => /C:/" is always absolute ! 
      
      // Canonical paths vs. Absolute paths:
      /* Yet another windows relative path hack: 
       * Windows interprets  "C:..."  also as relative
       * if the current directory on "C:" is 'C:/windows', the path 
       * "C:subdir" will result in "C:/windows/subdir"
       *   
       * Add extra '/' to make it absolute C:/ 
       * Note that java (under windows) accepts paths like  '/c:/dir'  ! 
       */
      
      //newpath=newpath.replaceAll("(/[a-zA-Z]:)([^/])","\1/\2"); 

      //
      // IIIa insert '/' if path starts with "C:" or "[a-zA-Z]:"
      // IIIb convert "/c:path => /c:/path"
      
      String dosPrefixRE="[/]*[a-zA-Z]:.*";
      
      // detect DOS path: 
      if ( (newpath.length()>=2)  && (newpath.matches(dosPrefixRE)) )
      {
          // prefix with  '/' to normalize absolute DOS path :  
             if (newpath.charAt(0)!='/')
                 newpath="/"+newpath;
             
             // insert  "/" between ":" and path: 
          // "/C:<path>" => "/C:/<path>"
          if ((newpath.length()>=4) && (newpath.charAt(2)==':') && (newpath.charAt(3)!=SEP_CHAR))
          {
              newpath="/"+newpath.charAt(1)+":/"+newpath.substring(3);
          }
      }
     
      // convert: "/C:" => "/C:/"  
      if ((newpath.length()==3) && (newpath.charAt(2)==':'))
      {
          newpath=newpath+SEP_CHAR;
      }
      else if ((newpath.length()==4) && (newpath.charAt(3)==SEP_CHAR))
      {
          // keep "/C:/..." 
      }
      else if ((newpath.length()>1)&&(newpath.charAt(newpath.length()-1)==SEP_CHAR))
      {
          // Strip last '/' if it isn't an absolute (Windows) drive path path
          // like example: '/C:/'   

          newpath=newpath.substring(0,newpath.length()-1);
      }
      
      // finally: now strip multiple slashes '/' to normalise the path
      newpath=newpath.replaceAll("/+","/");
      
      //Debug("uri path="+newpath);   
      return newpath; 
    }
    
    public static String encode(String string)
    {
        String encoded=URLUTF8Encoder.encode(string);
        return encoded;  
    }

    public static String extension(String name)
    {
        if (name==null)
            return null;
        
        int index=name.length(); 
        index--;
        
        // scan last part of path 
        
        while ((index>=0) && (name.charAt(index)!='.'))
        {
            index--; 
        }
        
        // index now points to '.'  char or is -1; (before beginning of the name)
        index++; // skip '.'; 
        
        return name.substring(index,name.length());
    }
    
    /** returns basename part (last part) of path String. */ 
    public static String basename(String path)
    {
        // default cases: null,empty and root path: 
        
        if (path==null)
            return null;
        
        if (path.equalsIgnoreCase(""))  
                return "";

        int index=0; 
        int strlen=path.length(); 
        
        index=strlen-1;// start at end of string  

        if (path.equalsIgnoreCase(SEP_CHAR_STR))
                return SEP_CHAR_STR;
        
        // special case, path ENDS with '/' which must be ignored
        
        if (path.charAt(index)==SEP_CHAR)
        {
            index--; 
            strlen--; 
        }
        
        while ((index>=0) && (path.charAt(index)!=SEP_CHAR))
        {
            index--; 
        }
        index++;
        // index points to character after '/' or is zero
        
        return path.substring(index,strlen);
    }
    
    
    public static VRL[] parseVRLList(String uristr) throws VRLSyntaxException
    {
        if (uristr==null)
            return null; 
        
        String strs[]=uristr.split(URI_LIST_SEPERATOR); 
        VRL vrls[]=new VRL[strs.length];
        int index=0;
        for (String str:strs)
        {
            VRL vrl;
            vrl = new VRL(str);
            
            vrls[index++]=vrl;
        }
        
        return vrls; 
    }
    
    
    /**
     * Returns the dirname part of the URI compatbile path ! 
     * (parent directory path) of path.
     * Note: use VRL.uripath to sanitize and normalize a path! 
     * <p>
     * Special cases:
     * <li>dirname of null is null 
     * <li>dirname of the empty string "" is ""
     * <li>The dirname of "/" = "/"
     *      
     * @see basename
     */
    public static String dirname(String path)
    {
        // NULL path: 
        if (path==null) 
            return null; 
        
        int index=0; 
        int strlen=path.length(); 
        
        index=strlen-1;// start at end of string  
 
        // Empty and root path:
        
        // relative path, cannot return dirname 
        if (path.equalsIgnoreCase("")) 
            return "";
        
        // root path of root is root itself 
        if (path.equalsIgnoreCase(SEP_CHAR_STR))      
            return SEP_CHAR_STR;
        
               
        // special case, path ENDS with '/' which must be ignored
        if (path.charAt(index)==SEP_CHAR)
        {
            index--; 
            strlen--; 
        }

        // move backwards to first seperator 
        while ((index>=0) && (path.charAt(index)!=SEP_CHAR))
        {
            index--; 
        }
        
        if (index==0)
        {
            // first char (path[0]) == '/' => special case root dir encountered:
            return SEP_CHAR_STR; 
        }
        else if (index<0)
        {
            //  no seperator found: this means this is a RELATIVE path with no parent dirname !  
            return ""; 
        }
        
        // index points to character '/'. 
        
        return path.substring(0,index);
    }
    
    public static String combinePaths(String parent, String child)
    {
        if ((parent==null) && (child!=null))  
            return child;
        
        if ((parent!=null) && (child==null)) 
            return parent;
        
        if ((parent==null) && (child==null)) 
            return null; 
        
            
        return uripath(parent+VRL.SEP_CHAR+child);
    }

    /**
     * Static method to check for empty or localhost names 
     * and aliases (127.0.0.1) 
     */ 
    public static boolean isLocalHostname(String host)
    {
        if (host==null)
            return true;
            
        if (host.compareTo("")==0)
            return true;
        
        if (host.compareTo(VRS.LOCALHOST)==0)
            return true;
        
        if (host.compareTo("127.0.0.1")==0)
            return true; 
        
        return false;
    }


    /**
     * Calculates relative path of childPath starting from parentPath. 
     * ParentPath MUST be real parent of childpath. 
     * If this is NOT the case, the value NULL is returned. 
     * (This method can also be used to check this). 
     * Paths are normalized and returned in URI format (foward slashes). 
     */ 
    public static String relativePath(String parentPath, String childPath)
    {
        if ((childPath==null) || (parentPath==null))
            return null; 
        
        if (childPath.startsWith(parentPath)==false) 
            return null;
        
        // use NORMALIZED paths ! 
        parentPath=uripath(parentPath);
        
        childPath=uripath(childPath); 
        
        String relpath=childPath.substring(parentPath.length(),childPath.length());
        // strip path seperator 
        if (relpath.startsWith("/")) 
            relpath=relpath.substring(1);
        return relpath; 
    }

    public static String stripExtension(String name)
    {
        if (name==null)
            return null;
        
        int index=name.length(); 
        index--;
        
        // scan last part of path 
        
        while ((index>=0) && (name.charAt(index)!='.'))
        {
            index--; 
        }
        
        // index now points to '.'  char or is -1; (before beginning of the name)
         
        if (index<0)
                return name; // no dot => NO extension ! 
        
        return name.substring(0,index);
    }
    
    /**
     * Resolve ref against parent path parent.  
     * For example resolve("/home/dexter",".globus" returns 
     * unified path /home/dexter/.globus 
     */
    public static String resolvePaths(String parent, String ref)
    {
        if (ref==null)
            return parent; 
        
        String result; 
        
        // absolute (*nix) path: 
          if (ref.startsWith("/")==true)
          {
              result=VRL.uripath(ref);  
          }
          // Sanitize absolute windoze path: 'C:/blah/...' 
          else if ((ref.length()>1) && (ref.charAt(1)==':'))
          {
              result=VRL.uripath(ref);
          }
          else
          {
              result=VRL.uripath(parent+VRL.SEP_CHAR+ref);
          }
          
          return result; 
    }
    
    /** Checks whether VRL is an absolute (fully qualified) VRL */ 
    public static boolean isVRL(String uristr)
    {
        try
        {
            VRL vrl=new VRL(uristr);
            // basic checks: 
            if (vrl.isRelative()==true)
                return false;
            
            if (vrl.getScheme()==null) 
                return false; 
            
            if (vrl.getScheme().compareTo("")==0) 
                return false;  
            
            return true; 
        }
        catch (Exception e)
        {
            ;
        }
        return false;
    }

    
    public static URL[] toURLs(VRL[] vrls) throws VRLSyntaxException
    {
        if (vrls==null)
            return null;
        
        URL urls[]=new URL[vrls.length];  
        for (int i=0;i<vrls.length;i++)
            urls[i]=vrls[i].toURL(); 
            
        return urls; 
    }

    
    /** 
     * Explicit factory to create VRL from dospath. 
     * Will allways forward flip backslashes.
     * Is Currently used for testing purposes but can be used 
     * under WinDos environments as well. 
     * 
     * @throws VRLSyntaxException 
     */  
    
    public static VRL createDosVRL(String vrlstr) throws VRLSyntaxException
    {
        String newStr=vrlstr.replace('\\','/'); 
        // constructor might change ! 
        return new VRL(newStr); 
    }
   
    // =======================================================================
    // Instance stuff
    // =======================================================================
    
    /** Mandatory scheme (file://, srb://) part of VRL URI */ 
    private String scheme=null;  
    
    /** Optional UserInfo */ 
    private String userInfo=null;
   
    /** Hostname */  
    private String hostname=null;
    
    /** Optional port number of server */ 
    private int port=0;
    
    /** 
     * <b>Decoded</b> path or reference component of VRL: 
     * paths always starts with '/' end never ends with one.
     * References are kept 'as-is' and typical don't have a starting "/". 
     */  
    private String pathOrReference=null; 
    
    /** Query part after "?" if URI has one. Can be null.*/  
    private String query=null; 
    
    /** Fragment part after "#" if URI has one. Can be null.*/
    private String fragment=null;

    /** Whether URI has //&lt;Authority&gt; Part */  
    private boolean hasAuthorityPart=false;

    // =======================================================================
    // Constructors/Initializers
    // =======================================================================
    
    /** Block empty constructor */ 
    private VRL()
    {
        
    }
    
    /** 
     * Construct new VRL from URI.<br>
     * All fields from the URI are copied. 
     * The URI fields are <i>decoded</i> and the path is normalized. 
     * Does extra checking and URI field parsing for URI consistancy. 
     * Note that java.net.URI doesn't parse non URL correctly. 
     * This method compensates for that. 
     */
    public VRL(URI uri)
    {
        init(uri); 
    }
  
//    No reference to apache axis URI here! Use String constructors  
//    public VRL(org.apache.axis.types.URI axisUri) throws VRLSyntaxException
//    {
//        init(axisUri.toString()); 
//    }

    
    /** 
     * Try to parse locationString as VRL. 
     * This is a more flexible initializer then new URI(String).
     * Checks for MyVle specified URIs and checks whether 
     * the string is %-encoded or decoded.
     * Paths,hostnames, etc are checked and normalized.  
     */ 
    public VRL(String locationString) throws VRLSyntaxException
    {
        // Debug(">>> New VRL from:"+locationString); 
        init(locationString);
    }
    
    /** Constructs VRL with scheme,hostname,path,fragment */  
    public VRL(String scheme, String host, String path, String frag)
    {
        init(scheme,null,host,-1,path,frag,null);
    }
    
    /** Create new VRL with scheme, hostname, port and path */ 
    public VRL(String scheme, String newhost,int newport, String newpath)
    {
        init(scheme,null,newhost,newport,newpath,null,null); 
    }
    
    /** Create new VRL with scheme, userinformation, hostname, port and path */ 
    public VRL(String scheme, String user, String host, int newPort, String newPath)
    {
        init(scheme,user,host,newPort,newPath,null,null); 
    }
    
    public VRL(String newScheme, String newUserinfo, String newHost, 
            int newPort, 
            String newPath, 
            String newQuery, 
            String newFragment)
    {
        init(newScheme,newUserinfo,newHost,newPort,newPath,newQuery,newFragment); 
    }
    
    /** Create new VRL with scheme,hostname and path */ 
    public VRL(String scheme, String hostname, String path)
    {
        init(scheme,null,hostname,-1,path,null,null); 
    }
    
 
    private void init(String uriStr) throws VRLSyntaxException
    {
        if (uriStr==null) 
            throw new VRLSyntaxException("VRL String can not be null");
        
        // ===
        // TODO: other URI parsing class !
        // URI parse bug. Java.net.URI doesn't allow empty schemespecific
        // nor missing authority parts after "://"
        // ===
        
        int index=uriStr.indexOf(':');
        
        String sspStr=null;
        
        if (index>=0) 
            sspStr=uriStr.substring(index+1,uriStr.length()); 
        
        if (sspStr!=null)
            if (StringUtil.isEmpty(sspStr) 
                || StringUtil.equals(sspStr,"/")
                || StringUtil.equals(sspStr,"//"))
        {
            // Parse: "scheme:", "scheme:/" "scheme://". 
            // create scheme only URI 
            this.scheme=uriStr.substring(0,index); 
            this.hasAuthorityPart=false; 
            return; 
        }
        
        try
        {
            URI uri=new URI(uriStr);
            init(uri); // use URI initializer: contains patches for URI parsing ! (better use initializors!)
            
        }
        catch (URISyntaxException e)
        {
            // Be Flexible: could be unencoded string: try to encode it first 
            // There is no way to detect whether the string is encoded if it contains a valid '%'. 
            try
            {
                // VRL strings should be URI compatible so let the URI 
                // class do the parsing ! 
                // to be save encode the uriStr!
                //Debug("Trying to encode:"+uriStr);
                URI uri=new URI(encode(uriStr));
                //Debug("encoded URI:"+uri);

                init(uri); // use URI initializer 
            }
            catch (URISyntaxException e2)
            {
               throw new nl.uva.vlet.exception.VRLSyntaxException(e2);
            }
        } 
    }
    
    /**
     * Initialize VRL URI using provided URI object.
     * Note that is the MAIN initializer. URI constructor is used
     * then the URI is normalized, then the part are stored seperately
     * 
     */
    private void init(URI uri)
    {
        uri.normalize(); // a VRL is a stricter implementation then an URI ! 

        // scheme: 
        String newScheme=uri.getScheme();

        boolean hasAuth=StringUtil.notEmpty(uri.getAuthority()); 
        
        String newUserInf=uri.getUserInfo();
        String newHost=uri.getHost(); // Not: resolveLocalHostname(uri.getHost()); // resolveHostname(uri.getHost());
        int newPort=uri.getPort();
        
        // ===========
        // Hack to get path,Query and Fragment part from SchemeSpecific parth if the URI is a reference
        // URI. 
        // The java.net.URI doesn't parse "file:relativePath?query" correctly
        // ===========
        String pathOrRef=null;
        String newQuery=null; 
        String newFraq=null; 
        
        if ((hasAuth==false) && (uri.getRawSchemeSpecificPart().startsWith("/")==false)) 
        {
            // Parse Reference URI or Relative URI which is not URL compatible: 
            // hack to parse query and fragment from non URL compatible 
            // URI 
            try
            {
                // check: "ref:<blah>?query#frag"
                URI tempUri=null ;
                
                tempUri = new URI(newScheme+":/"+uri.getRawSchemeSpecificPart());
                String path2=tempUri.getPath(); 
                // get path but skip added '/': 
                // set Path but keep as Reference ! (path is reused for that) 
                
                pathOrRef=path2.substring(1,path2.length()); 
                newQuery=tempUri.getQuery(); 
                newFraq=tempUri.getFragment(); 
            }
            catch (URISyntaxException e)
            {
            	Global.errorPrintStacktrace(e);
                // Reference URI:   ref:<blahblahblah>  without starting '/' ! 
                // store as is without parsing: 
            	pathOrRef=uri.getRawSchemeSpecificPart();  
            }
        }
        else
        {
            // plain PATH URI: get Path,Query and Fragment.  
            pathOrRef=uri.getPath(); 
            newQuery=uri.getQuery(); 
            newFraq=uri.getFragment();
        }
        
        // ================================
        // use normalized initializer
        // ================================
        init(newScheme,newUserInf,newHost,newPort,pathOrRef,newQuery,newFraq); 
    }
    
    /**
     * Master Initializer. 
     * All fields are given and no exception is thrown. 
     * Fields should be decoded.
     * 
     * Path can be relative or absolute but must be normalized with forward slashes.
     *  
     */ 
    private void init(String newscheme,String userinf,String newhost,int newport,String newpath,String newquery,String newfrag)
    {
    	// ============================
    	// Cleanup URI initialization. 
    	// ============================
    	
    	// must be null or uri will add empty values 
        if (StringUtil.isEmpty(newhost))
        	newhost=null; // null => no hostname

        if (StringUtil.isEmpty(userinf))
        	userinf=null; // null => no userinfo
        
        if (StringUtil.isEmpty(newquery))
        	newquery=null; // null => no userinfo
        
        if (StringUtil.isEmpty(newfrag))
        	newfrag=null; // null => no userinfo
        
        // port value of -1 leaves out port in URI !
        // port==0 is use protocol default, SSH => 22, etc. 
        if (newport<=0) 
            newport=-1;
        
        // ===
        // AUTHORITY  ::=  [ <userinfo> '@' ] <hostname> [ ':' <port> ] 
        // ===
        this.hasAuthorityPart=StringUtil.notEmpty(userinf)
    						|| StringUtil.notEmpty(newhost) 
    					    || (port>0); 
    	
    	// ===
    	// Feature: Strip ':' after scheme
    	// ===
        if ((newscheme!=null) && (newscheme.endsWith(":")))
            newscheme=newscheme.substring(0,newscheme.length()-1);
      
        // ===
    	// Relative URI: does NOT have a scheme nor Authority !
        // examples: "dirname/tmp", "#label", "?query#fragment","local.html",
        // ===
    	boolean isRelativeURI=StringUtil.isEmpty(newscheme) && (hasAuthorityPart==false);
    	
    	// === 
    	// PATH 
    	// Sanitize path, but keep relative paths or reference paths intact
    	// if there is no authority ! 
    	// ====
    	newpath=uripath(newpath,hasAuthorityPart); 
    	

        // store duplicates (or null) ! 
        this.scheme=StringUtil.duplicate(newscheme); 
        this.userInfo=StringUtil.duplicate(userinf);  
        this.hostname=StringUtil.duplicate(newhost); // Keep hostname as-is: // resolveHostname(hostname);
        this.port=newport;
        this.pathOrReference=StringUtil.duplicate(newpath);   
        this.query=StringUtil.duplicate(newquery); 
        this.fragment=StringUtil.duplicate(newfrag);
    }
 
    /**
     * Creates new VRL, resolves relative Uri String to baseLocation. 
     * Typically this concats 'relativeUri' to 'baseLocation' and is similar to
     * new VRL(base+"/"+relpath);  
     *   
     * @throws VlException 
     */
    
    public VRL(VRL base, String relpath) throws VRLSyntaxException 
    {
        this(base,relpath,false); 
    }

    /**
     * Constructs new VRL using baseLocation to resolve the String relativeUri. 
     * Since an URI treats 'baseLocation' as a file, set useDirname=false to 
     * use the fullpath of baseLocation instead of the dirname of the location.<br>
     * For example:<br> 
     * <li> new VRL("http://server.domain/help","index.html",false) = <br>
     *       "http://server.domain/help/index.html"
     * <li> new VRL("http://server.domain/help/index.html","#label",false) = <br>
     *       "http://server.domain/help/index.html#label"
     * <li> new VRL("http://server.domain/help/index.html","other.html",true) = <br>
     *       "http://server.domain/help/other.html"
     *    
     * @param baseLocation base location to resolve against 
     * @param relativeUri  realitive path, #&lt;fragment&gt; or ?&lt;query&gt;  
     * @param useDirname   wether the URI is a directory and not a file like ../index.html
     * @throws VRLSyntaxException 
     */
    public VRL(VRL baseLocation, String relativeUri, boolean useDirname) throws VRLSyntaxException
    {
        String newpath=null; 
        URI uri=null;
        
        if (StringUtil.isEmpty(relativeUri))
        {
            init(baseLocation.toURI()); 
            return; 
        }
           
        // If reference is NOT a fragment into current 
        // and current location is NOT a dirname:, get parent path of document: 
        if ((relativeUri.charAt(0)!='#') && (useDirname==true)) 
        {
            baseLocation=baseLocation.getParent(); 
        }
        
        // strip '../'
        while(relativeUri.startsWith("../"))
        {
            relativeUri=relativeUri.substring(3);
            baseLocation=baseLocation.getParent();
        }
        
        try
        {
            uri = new URI(relativeUri);
        }
        catch (URISyntaxException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }
        
        if (uri.isAbsolute())
        {
            init(uri);
            return; 
        }
        
        // resolve relative path to baseLocation; 
                
        init(baseLocation.toURI());
        
        // get path part from relative URI:
        
        String relPath=uri.getPath();
        
        
        if (relPath.startsWith("./"))
        {
            newpath=getPath()+VRL.SEP_CHAR_STR+relPath.substring(1);  // remove dot
        }
        else if (relPath.startsWith("/"))
        {
            newpath=relPath; // Assume absolute path 
        }
        else
        {
            // does not start with '/'
            newpath=getPath()+VRL.SEP_CHAR_STR+relPath;
        }
           
        // new absolute normalized path: 
        this.setPath(newpath,true);   
        
        // copy fragments and query parts: 
        setFragment(uri.getFragment());
        setQuery(uri.getQuery());
        
    }

    private void setQuery(String qstr)
    {
        if ((qstr!=null) && ((qstr.compareTo("")==0)))
        {
           query=null;
        }
        else
        {
            query=qstr;
        }
    }

    public VRL(URL url) throws VlException
    {
        if (url==null)
        {
            Global.errorPrintf(this,"*** Error: null URL in constructor!\n");
            return; 
        }
        // use normalized string representation: 
        try
        {
            init (url.toURI());
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Not a valid URI:"+url,e); 
        }
    }


    private void setFragment(String frac)
    {
        if ((frac!=null) && ((frac.compareTo("")==0)))
        {
           fragment=null; 
        }
        else
        {
            fragment=frac; 
        }
    }
    
    /** Return part afer '#' */ 
    public String getFragment()
    {
        return fragment; 
    }

    /**
     * Returns URI compatible object of VRL.<br>
     * Here path components <b><i>might</i></b> be encoded in %XX format!<br>
     * use toString for unencoded VRL String.  
     * @throws VlException 
     * @see toString
     * @see URI 
     */ 
    public URI toURI() throws VRLSyntaxException
    {
        try
        { 
            if (this.hasAuthorityPart())
            {
                return new URI(this.scheme,
                        this.userInfo,
                        this.hostname,
                        this.port,
                        this.getPath(),
                        this.query,
                        this.fragment); 
                        
            }
            else 
            {
                // create Reference URI 
                return new URI(this.scheme,
                        null,
                        null,
                        -1,
                        this.getReference(),
                        this.query,
                        this.fragment); 
                
            }
            //else
            //{
            //    return new URI(encode(this.toString()));
            //}
        }
        catch (URISyntaxException e)
        {
           throw new nl.uva.vlet.exception.VRLSyntaxException(e);
        }  
    }
    
  
    /**
     * Returns the hashcode of the String representation of this VRL.  
     * @see String.hashCode(); 
     */
    public int hashCode()
    {
        return toString().hashCode(); 
    }
    
    // @see #toNormalizedString()
    public String toString()
    {
        return toNormalizedString();
    }

    /**
     * This method returns the DECODED URI string. 
     * For an URI compatible string (with %XX encodeing) 
     * use toURI().toString() or toURIString() ! 
     *  
     * Also, Locations are URI compatible but not all URI Strings 
     * are VRL Strings! <br>

     * @return
     */
    public String toNormalizedString()
    {
    	// suport relative VRLs!
    	String str="";

    	if (this.isAbsolute()==true)
    		str+=scheme+":";

        if (this.hasAuthorityPart)
        {
            str+=SEP_CHAR_STR+SEP_CHAR_STR;
        
            if ((userInfo!=null) && (userInfo.compareTo("")!=0)) 
                str+=userInfo+"@"; 
        
            if (hostname!=null)
            {
                str+=hostname; 
                if (port>0) 
                    str+=":"+port; 
            }
        }
        
 
        // path could start without "/" ! 
        if (pathOrReference!=null)
            str+=pathOrReference; 
        else
            str+=SEP_CHAR; // still end with '/' for consistancy ! 
        
        if (query!=null)
            str+="?"+query;
        
        if (fragment!=null)
            str+="#"+fragment;  
        
        return str; 
    }
    
    /** 
     * Get Scheme or service type like: "gftp" from "gftp://" or "srb" from "srb://..."<br>
     */ 
    public String getScheme()
    {
        return scheme; 
    }
    
    /** 
     * Get Normalized sheme: gftp->gsiftp 
     */
    public String getNormalizedScheme()
    {
        return VRSContext.getDefault().resolveScheme(scheme); 
        
    }
    
    /** Returns hostname part of VRL */ 
    public String getHostname()
    {
        return hostname;
    }

    /**
     * @return decoded path component of URI
     */
    public String getPath()
    {
        return pathOrReference; 
    }
    
    /**
     * @return decoded reference component of URI
     */
    public String getReference()
    {
        return pathOrReference; 
    }
    
    private void setPath(String newPath)
    {
    	setPath(newPath,hasAuthorityPart()); 
    }

    /** Set new Path and format it to default URI path */ 
    private void setPath(String path,boolean makeAbsolute)
    {
        // decode and normalize path 
        this.pathOrReference=uripath(path,makeAbsolute);
    }
    
    /** Set as reference and do not do any normalization */ 
    private void setReference(String refPath)
    {
        this.pathOrReference=refPath; 
    }
        
    /** Returns complete userinfo part of the VRL */ 
    public String getUserinfo()
    {
    	return this.userInfo;
    }
    
    /** Returns username part from userinfo if it as one */ 
    public String getUsername()
    {
        String info=this.userInfo;
        
        if (info==null) 
            return null; 
        
        // strip password: 
        String parts[]=info.split(":");
   
        if ((parts==null) || (parts.length==0)) 
            return info;
   
        if (parts[0].length()==0)
            return null;
        
        return parts[0]; 
    }
    
    /**
     *  Returns password part (if specified !) from userInfo string 
     *  @deprecated It is NOT save to use clear text password in any URI! 
     */ 
    public String getPassword()
    {
        String info=userInfo;
        
        if (info==null) 
            return null; 
        
        String parts[]=info.split(":");
        
        if ((parts==null) || (parts.length<2)) 
            return null;
        
        return parts[1]; 
    }
    
    /**
     * @return decoded query component of URI (part after '?')
     */
    public String getQuery()
    {
        return query; 
    }
    
    /**
     * Returns list of expressions seperated by a '&amp;'
     */   
    public String[] getQueryParts()
    {
        if (query==null) 
            return null; 
        
        return this.query.split(ATTRIBUTE_SEPERATOR);
    }

    /**
     * Returns (decoded) parent directory part of path 
     * @see dirname 
     * @see basename 
     */
    public String getDirname()
    {
        return dirname(getPath()); 
    }

    /** 
     * Returns granparent dirname. Calls dirname on dirname result 
     */ 
    public String getDirdirname()
    {
        return dirname(dirname(getPath())); 
    }

    
    /** Get last part of filename starting from a '.' */ 
    public String getExtension()
    {
        return extension(getPath());
    }
     
  
    /** 
     * returns basename part (last part) of this location.
     * @param withExtension whether to keep the extension. 
     */
    
    public String getBasename(boolean withExtension)
    {
        String name=basename(getPath());
        
        if (withExtension==true)
            return name;           
        else
            return stripExtension(name);
    }
    
    public String getBasename()
    {
        return getBasename(true); 
    }

   
    /**
     *Needed for sloppy URI encoders (Jargon) 
     *Currently ONLY encodes spaces !
     *Need a encoder/decoe
     */ 
    
    /*public static String encode(String string)
    {
       return URLUTF8Encoder.encode(string); 
    }*/

    /** 
     * check whether URI (and path) is a parent location of <code>subLocation</code>.  
     * 
     * @param subLocation
     * @return
     */
    public boolean isParentOf(VRL subLocation)
    {
        String pathStr=toString(); 
        String subPath=subLocation.toString(); 
        
        // Current implementation is based on simple string comparison.
        // For this to work, both VRL strings must be normalized ! 
        // Debug("isSubPath:"+pathStr+","+subPath);
        
        if (subPath.startsWith(pathStr)==true)
        {
            // To prevent that paths like '<..>/dir123' appear to be subdirs of '<..>/dir' 
            // last part of subpath after '<..>/dir' must be '/' 
            // Debug("subPath.charAt="+subPath.charAt(pathStr.length()));
            
            if ((subPath.length()>pathStr.length()) && (subPath.charAt(pathStr.length())==SEP_CHAR)) 
                return true; 
        }
        
        return false; 
    }

    /** 
     * Compares this location to loc. 
     * Note that Hostname aliases are NOT checked here.
     * Make sure that fully qualified hostnames are present in the VRL. 
     *   
     * @see toString
     * */
    public int compareTo(VRL loc)
    {
         //  return this.toString().compareTo(loc.toString());
        
        if (loc==null) 
            return 1; // this > null 
        
        // MUST normalize scheme names ! (use static registry) 
        String scheme1= VRS.getDefaultScheme(this.scheme); 
        String scheme2= VRS.getDefaultScheme(loc.scheme); 
 
        // Scheme compare: cany be null in the case of relative VRLs !  
        int val=StringUtil.compareIgnoreCase(scheme1,scheme2); 
        if (val!=0)
            return val;
       
        String host1=this.hostname; // resolveHostname(this.hostname);  
        String host2=loc.hostname;  //resolveHostname(loc.hostname);
        
        // normalize hostnames: null,empty and 'localhost' are equivalent! 
        if ((host1==null) ||  (host1.compareTo("")==0))
            host1=VRS.LOCALHOST;
        if ((host2==null) ||  (host2.compareTo("")==0))
            host2=VRS.LOCALHOST; 
        
        //logical compare ? 
        //host1=resolveHostname(host1);  
        //host2=resolveHostname(host2); 
        
        val=StringUtil.compareIgnoreCase(host1,host2); 
        
        if (val!=0)
            return val;
        
        int p1=this.port;
        int p2=loc.port; 
        if (p1<0)
        	p1=0; 
        if (p2<0)
        	p2=0;
        
        if (p1<p2)
            return -1;
    
        if (p1>p2) 
            return 1; 
        
        // case SENSITIVE path: 
        val=StringUtil.compare(pathOrReference,loc.pathOrReference); 
        if (val!=0)
            return val;
        
        // case sensitive query:
        val=StringUtil.compare(query,loc.query);
        if (val!=0)
           return val;

        val=StringUtil.compare(fragment,loc.fragment);
        if (val!=0)
           return val;

        return 0; 
    }
    
    /**
     * Same as 'clone()' 
     * Method is here for historical reasons. 
     */
    public VRL duplicate()  
    {
        VRL loc=new VRL();
        
        // field by field copy: 
        loc.scheme=StringUtil.duplicate(this.scheme);  
        loc.userInfo=StringUtil.duplicate(userInfo);
        loc.hostname=StringUtil.duplicate(hostname);
        loc.port=this.port; 
        loc.pathOrReference=StringUtil.duplicate(pathOrReference); 
        loc.query=StringUtil.duplicate(query); 
        loc.fragment=StringUtil.duplicate(fragment);
        loc.hasAuthorityPart=this.hasAuthorityPart; 

        return loc; 
    }
    
    public VRL clone()
    {
        return duplicate(); 
    }
    
   

    /** Get port part of URI or 0 if non specified/default must be used */ 
    public int getPort()
    {
        return this.port; 
    }

    /** Returns new location which *could* be the parent location */ 
    public VRL getParent() 
    {
        String parentPath=dirname(this.getPath()); 
        return this.copyWithNewPath(parentPath);  
    }
    
    /** Add path and return new VRL */ 
    public VRL plus(String path)
    {
        return appendPath(path); 
    }
    
    /**
     * Returns NEW VRL with appended path. 
     * Does NOT modify current VRL 
     */ 
    public VRL append(String subpath)
    {
        return this.appendPath(subpath); 
    }
    
    /** Creates new location by appending path to this one */ 
    public VRL appendPath(String dirname)
    {
        VRL newLoc=this.duplicate();
        
        String oldpath=newLoc.getPath(); 
        String newpath=null; 
       
        if (oldpath==null) 
            oldpath=""; 
        
        if (StringUtil.isEmpty(dirname)) 
            return duplicate(); // nothing to append
        
           else if (dirname.charAt(0)==SEP_CHAR)
            newpath=oldpath+dirname; 
        else if (dirname.charAt(0)==ANTI_SEP_CHAR)
            newpath=oldpath+SEP_CHAR+dirname.substring(1);
        else
            newpath=oldpath+SEP_CHAR+dirname;
        
        // sanitize path: 
        newLoc.setPath(newpath,true);  
        
        return newLoc; 
    }

    public boolean isVLink()
    {
        String ext=getExtension();
        
        if (ext!=null) 
            return (ext.compareTo(VRS.VLINK_EXTENSION)==0);
            
        return false; 
    }
   
    /**
     * Checks wether hostname is either 'localhost' or
     * matches the hostname of the current host. 
     * <p> 
     * This method also tries to resolve the hostnames into
     * the fully qualified hostnames and compares those. 
     * If, for example, the hostname in this VRL is "pc-vlab17" and current 
     * hostname is "pc-vlab17.science.uva.nl" this method will 
     * return true. 
     */
    public boolean isLocalHostname() 
    {
         if (StringUtil.isEmpty(hostname))
             return true; 
         
        // Check localhostname aliases 
        String resolved=VRLUtil.resolveLocalHostname(hostname);
        if (resolved.compareToIgnoreCase(VRS.LOCALHOST)==0)
            return true;

        // Check unresolved hostname (alias)  
        String currentHost=VRLUtil.resolveHostname(Global.getHostname());
        if (resolved.compareToIgnoreCase(currentHost)==0) 
            return true;
        
        // Reverse DNS for matching based on fully qualified name. 
        // Works when hosts have fully qualified hostnames.
        currentHost=VRLUtil.resolveHostname(currentHost);
        resolved=VRLUtil.resolveHostname(hostname);
         
        if (resolved.compareTo(currentHost)==0)
            return true;
         
        return false; 
    }
    
    public boolean isRootPath()
    {
        // normalize path: 
        String upath=uripath(this.getPath());
        //Debug("isRootPath(): uri path="+upath); 
        
        if (StringUtil.isEmpty(upath))
            return true; 
        
        // "/"
        if (upath.compareTo(SEP_CHAR_STR)==0) 
            return true; 
        
        // uripath normalized windosh root "/X:/" 
        if (upath.length()==4)
            if ((upath.charAt(0)==SEP_CHAR) && (upath.substring(2,4).compareTo(":/")==0))
                return true; 
        
        return false; 
    }
   
    public boolean isURL()
    {
        URL url=null; 
        
        try
        {
            // check if it can be tranformed to an URL. 
           url=toURL();
        }
        catch (VlException e)
        {
            return false; 
        } 
        
        return (url!=null);   
         
    }
    
    /**
     * Create URL. 
     * @throws VlException
     */
    public URL toURL() throws VRLSyntaxException
    {
        URL url=null;
        
        try
        {
            // Official way to create URLs, first to URI, THEN to URL. 
            url=toURI().toURL();
            return url; 
        }
        catch (MalformedURLException e)
        {
            throw new nl.uva.vlet.exception.VRLSyntaxException("Not an URL:"+this,e); 
        }
    }
    
    
    /**
     * Returns base VRL without Query ("?...") or Fragment ("#...") part 
     */ 
    public VRL getBaseLocation()
    {
        VRL loc=duplicate();
        loc.fragment=null;
        loc.query=null;
        return loc; 
    }
    
    /**
     * If a query has a set of properties in the form "?name=value&name2=..."
     * return the set of properties as a VAtributeSet. 
     * The property seperator is '&' (ampersand). 
     * 
     * @return
     */
    public VAttributeSet getQueryAttributes()
    {
       String qstr=getQuery();
       
       // no query 
       if (qstr==null) 
           return null;
       
       // split in '&' parts 
       String stats[]=getQueryParts();        
       // empty list 
       if ((stats==null) || (stats.length<=0))
           return null; 
       
       VAttributeSet aset=new VAttributeSet(); 
       
       for (String stat:stats)
       {
           VAttribute attr=VAttribute.createFromAssignment(stat);
           //Debug("+ adding attribute="+attr); 
           if (attr!=null)
               aset.put(attr); 
       }
       return aset; 
    }

    /**
     * Changing a port in a Location object is not allowed due to the 
     * immutable paradigm of Location object (similar to URIs), so to change a port
     * a new object must be created with the changed value. 
     * 
     * @param val
     * @return
     */
    public VRL copyWithNewPort(int val)
    {
        VRL loc=duplicate(); 
        loc.port=val;
        loc.checkHasAuthority(); // update
        return loc; 
    }
    
    private void checkHasAuthority()
    {
        if (StringUtil.isEmpty(hostname) 
            && (port<=0) 
            && (StringUtil.isEmpty(this.userInfo)))
        {
            this.hasAuthorityPart=false;
        }
        else
        {
            this.hasAuthorityPart=true;
        }
        
        
    }

    /**
     * Changing a port in a Location object is not allowed due to the 
     * immutable paradigm of Location object (similar to URIs), so to change 
     * the hostname, a new object must be created with the changed value. 
     * 
     * @param val
     * @return
     */
    public VRL copyWithNewHostname(String host)
    {
        VRL loc=duplicate(); 
        loc.hostname=host;
        loc.checkHasAuthority(); // update 
        return loc; 
    }
    
    /** Create duplicate, but setpath to newPath */ 
    public VRL copyWithNewPath(String newPath)
    {
        VRL loc=duplicate();
        // format path ! make absolute if has authority part:
        loc.setPath(newPath);
        return loc; 
    }
    
    /** Used for scheme subtitution */ 
    public VRL copyWithNewScheme(String newscheme)
    {
        VRL vrl=this.duplicate(); 
        vrl.scheme=newscheme; 
        return vrl; 
    }
    
    /** Duplicate VRL but create new Query */
    public VRL copyWithNewQuery(String str)
    {
        VRL vrl=this.duplicate();
        vrl.query=str;
        return vrl; 
    }
    
    /**
     * Returns array of path elements. 
     * For example "/dev/zero" = {"dev","zero"}
     */ 
    public String[] getPathElements()
    {
        String path=getPath();
        
        if (path==null)
            return null; 
        
        // strip starting '/' to avoid empty path as first path element
        if (path.startsWith(SEP_CHAR_STR))
            path=path.substring(1); 
        
        return path.split(SEP_CHAR_STR); 
    }

    public VRL copyWithNewBasename(String optNewName)
    {
        // convenience method for optional new names in copy/move methods
        if (optNewName==null) 
            return this.duplicate();
        
        return this.getParent().appendPath(optNewName); 
    }
    
    /**
     * Resolves optional relative URI using this URI 
     * as base location. 
     * Note: the last part (basename) of the URI is stripped, 
     * assuming the URI starts from a file, for example 
     * "http://myhost/index.html", base location = "http://myhost/".   
     * Any relative url starts from "http://myhost/" NOT "http://myhost/index.html"  
     * Also the supplied string must match URI paths (be %-encoded).
     * @see java.net.URI#resolve(String) 
     * @throws VlException
     */
    public VRL resolve(String reluri) throws VRLSyntaxException 
    { 
        // resolve must have ENCODED URI paths...
        URI uri=toURI().resolve(reluri);
        //Debug("resolve:"+this+","+reluri+"="+uri); 
        VRL newVRL=new VRL(uri);
        // authority update  bug. keep either file:/// or file:/
        newVRL.hasAuthorityPart=this.hasAuthorityPart;
        return newVRL; 
    }
    
    /** 
     * Resolves relative VRL again this location 
     *  @see java.net.URI#resolve(String) 
     * @throws VRLSyntaxException 
     */ 
    public VRL resolve(VRL loc) throws VRLSyntaxException
    {
        // Absolute VRL cannot be resolved. It is already absolute. 
        if (loc.isAbsolute()==true)
            return loc;
        // delegate to URI resolve methods ! 
        VRL newVrl=new VRL(this.toURI().resolve(loc.toURI()));
        newVrl.hasAuthorityPart=this.hasAuthorityPart;
        return newVrl; 
    }
    /**
     * Resolve DECODED (no %-chars) relative path against this URI, assuming
     * this URI is a (directory) PATH and not a file or url location. 
     * The default URI.resolve() method strips the last part 
     * (basename) of an URI and uses that as base URI (for example the 
     * "index.html" part). Also the relative URIs must be %-coded 
     * when they contain spaces ! 
     * 
     * This method doesn't strip the last part of the URI. 
     * 
     * @param relpath
     * @return
     * @throws VlException
     */
    public VRL resolvePath(String relpath) throws VRLSyntaxException 
    { 
        // add dummy html, and use URI 'resolve' (which expects .html file)
        VRL kludge=this.appendPath("dummy.html"); 
        return kludge.resolve(encode(VRL.uripath(relpath,false))); 
    }
 
    /** Resolves relatice URL again this location 
     * @throws VRLSyntaxException */ 
    public VRL resolvePath(VRL loc) throws VRLSyntaxException
    {
        return resolvePath(loc.getPath()); 
    }
    
    
    

    /** 
     * Return 'raw' path as specfied by URI.getRawPath() 
     * @see URI.getRawPath()
     * @return raw path (undecoded string) of uri.
     */
    public String getRawPath()
    {
        // encode to URI and get encoded ('raw') path. 
        try
        {
            return this.toURI().getRawPath();
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
            return this.getPath();
        }
    }
    
    /**
     * Returns URI compatible and optionally encoded string, 
     * use toString() for default (non-encoded) VRL String! 
     * @throws VlException 
     */
    public String toURIString() throws VRLSyntaxException
    {
        return toURI().toString(); 
    }
    
    /**
     * A relative URI is an URI without a scheme.
     * for example in "href="../subdir"> 
     * @see URI.isAbsolute
     */ 
    public boolean isRelative()
    {
        return (isEmpty(scheme)==true);
    }
    
    /**
     * From the URI definition: an URI is absolute if and only if 
     * the scheme is not defined 
     * @see URI.isAbsolute 
     */
    public boolean isAbsolute()
    {
        return (isEmpty(scheme)==false);
    }
    
    // Whether URI/VRL: has a //<AuthorityPart> in it. 
    
    public boolean hasAuthorityPart()
    {
        return this.hasAuthorityPart; 
    }
    
    private boolean isEmpty(String str)
    {
        if (str==null) 
            return true; 
        if (str.compareTo("")==0) 
            return true;
        
        return false;  
    }
    
    /** Return port as integer */ 
    public int getPortInteger()
    {
        return new Integer(getPort()); 
    }
    
    // Must be overriden to comply with hashCode() requirements ! 
    public boolean equals(Object obj)
    {
        if (obj instanceof VRL)
            return equals((VRL)obj);
        else
            return super.equals(obj); // should be false;
    }
    
    /** 
     * Compare to another VRL. 
     * The method calls compareTo(). 
     * @see compareTo()
     */ 
    public boolean equals(VRL vrl)
    {
        return (compareTo(vrl)==0);      
    }
    
    public boolean hasScheme(String scheme)
    {
        String s1=this.getScheme(); 
        return StringUtil.equals(s1,scheme);
    }
    
    /** Compare hostnames, ignores case! */ 
    public boolean hasHostname(String host)
    {
        return (StringUtil.compareIgnoreCase(getHostname(),host)==0); 
    }


    /** Checks whether paths ends with the specified extension. 
      * String ext should NOT contain a dot 
      */  
    public boolean hasExtension(String ext2)
    {
        String ext=this.getExtension();
        return StringUtil.equals(ext,ext2); 
    }

    /**
     * Returns path with explicit "/" at the end. 
     * This is mandatory for some Globus implementations */ 
    public String getDirPath()
    {
        return this.getPath()+"/";
    }

	/** Returns true if this VRL has a non empty fragment part ('?...') in it. */ 
	public boolean hasQuery()
	{
		return (StringUtil.isEmpty(getQuery())==false); 
	}

	/** Returns true if this VRL has a non empty fragment part ('#...') in it. */ 
	public boolean hasFragment()
	{
		return (StringUtil.isEmpty(getFragment())==false); 
	}

	
}
