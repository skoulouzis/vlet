package nl.uva.vlet.vrl;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vrs.VRS;

public class VRLUtil
{
    // hostname cache: 
    static Hashtable<String,String> hostnames=new Hashtable<String,String>();
 
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(VRLUtil.class); 
        //logger.setLevelToDebug(); 
    }
    /**
     * Get fully qualified and <strong>resolved</strong> hostname. 
     * <p>
     * This method will return the resulting hostname
     * by means of reverse DNS lookup.
     * If this hostname is an alias for another hostname, 
     * which could be on a complete other domain, the resulting
     * hostname will be returned ! 
     * <p> 
     * For example "my.internetdomain.org" might result in te 
     * providers domain name "userXYS.provider.com".   
     */ 
    public static String resolveHostname(String name)
    {
        try
        {
            return reverseDNSlookup(name);
        }
        catch (UnknownHostException e)
        {
            logger.warnPrintf("Warning: Unknown host:%s\n",name); 
            return name; 
        } 
    }

    /**
     * Returns fully qualified hostname by means of reverse DNS lookup
     */
    public static String reverseDNSlookup(String name) throws UnknownHostException
    {
        //update 
        name=resolveLocalHostname(name); 
        String newname=null;

        if (name==null)
        {
            logger.warnPrintf("reverseDNSlookup(): NULL Hostname!\n"); 
            return null; 
        }
        
        //check cache:
        synchronized(hostnames)
        {
            if (name!=null)
                if ((newname=hostnames.get(name))!=null)
                {
                    logger.debugPrintf("reverseDNSlookup(): (I) cached '%s' => '%s'\n",name,newname);  
                    return newname;
                }
        }
        
        //java.net.InetAddress.getLocalHost().getCanonicalHostName(); 
         
        InetAddress ipaddr;
         
        //try
        //{
            ipaddr = java.net.InetAddress.getByName(name);
        //}
        //catch (UnknownHostException e)
       // {
        //    Global.errorPrintln("VRL","Exception:"+e); 
       //     e.printStackTrace(Global.getDebugStream());
       ///     return name; // return name as is. 
       // }
        
        newname=ipaddr.getCanonicalHostName();
        
        synchronized(hostnames)
        {
            hostnames.put(name,newname);
        }
        
        logger.debugPrintf("reverseDNSlookup(): (II) Put new '%s' => '%s'\n",name,newname);  

        return newname; 
    }

   
    /**
     * Returns true if and only true if both hostnames point to the
     * same physical host. For example HOST equivelant with HOST.DOMAIN and/or raw IP-adresses !
     * Allows for null and empty hostnames.  
     */
    
    public static boolean hostnamesAreEquivelant(String host1, String host2)
    {
        if (host1==null)
            host1="";
        
        if (host2==null)
            host2="";
        
        // check unresolved hostsname (speedup aliasing)  
        if (host1.compareTo(host2)==0)
            return true; 
        
        // now check unresolved hostnames 
        host1=resolveHostname(host1); 
        host2=resolveHostname(host2); 
        
         // check resolved hostnames 
        if (host1.compareTo(host2)==0)
            return true; 
        
        return false; 
    }
    
    /**
     * Return URL, but with an added "/". 
     * Some implementations, like Globus, specify 
     * that URLs ending with a "/" are (always?) directories !
     * This is a nice feature but not always the rule.  
     * @return URL with as last character a '/'. 
     * @throws MalformedURLException 
     * @throws VRLSyntaxException 
     */
    public static URL toDirURL(VRL vrl) throws VRLSyntaxException, MalformedURLException
    {
        return new URL(vrl.toURIString()+"/");
    }
    /**
     * Check for empty or localhost names aliases and 
     * return 'localhost'. 
     */
    public static String resolveLocalHostname(String hostname)
    {
          if ((hostname==null) || (hostname.compareTo("")==0))
           return VRS.LOCALHOST;
                
       if  (hostname.compareToIgnoreCase(VRS.LOCALHOST)==0) 
           return VRS.LOCALHOST;
       
       // support local loop device: 
       if  (hostname.compareTo("127.0.0.1")==0) 
           return VRS.LOCALHOST;
       
       return hostname; 
    }

    
    public static boolean hasSameServer(VRL vrl1,VRL vrl2)
    {
        if (vrl2==null)
            return false; 
            
        logger.debugPrintf("hasSameServer(): '%s' <==> '%s'\n",vrl1,vrl2); 
        		
        String scheme=vrl1.getScheme(); 
        String scheme2=vrl2.getScheme(); 
        
        if (scheme.compareToIgnoreCase(scheme2)!=0) 
        {
            // check normalized scheme: 
            scheme=VRS.getDefaultScheme(scheme); 
            scheme2=VRS.getDefaultScheme(scheme2); 

            // check normalized schemes 
            if (scheme.compareToIgnoreCase(scheme2)!=0) 
                return false;
        }
        
        String hostname=vrl1.getHostname(); 
        String host2=vrl2.getHostname(); 
        
        // check hostname 
        if (StringUtil.compareIgnoreCase(hostname,host2)!=0)
            return false;
            
        int port=vrl1.getPort(); 
        int port2=vrl2.getPort(); 
        
        if (port<=0) 
            port=VRS.getSchemeDefaultPort(scheme);
                        
        if (port2<=0) 
            port2=VRS.getSchemeDefaultPort(scheme2); 
                
        // check port 
        if (port!=port2) 
            return false;
                
        logger.debugPrintf("hasSameServer: TRUE for: '%s' <==> '%s'\n",vrl1,vrl2); 

        // should be on the same server ! 
        return true;
    }

}
