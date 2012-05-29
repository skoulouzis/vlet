/*
 * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: SrbConfig.java,v 1.4 2011-06-07 14:31:37 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:37 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PASSIVE_MODE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_USERNAME;
import static nl.uva.vlet.data.VAttributeConstants.AUTH_SCHEME;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;


public class SrbConfig
{
 // ========================================================================
    // Class 
    // ========================================================================
    /** Used for debug purposes */ 
    private static boolean _debugUseSRBPropertiesFile=true; 

    // TBI: got some strange bugs when useing string constants which are the same !:
    // jargon probably does some strange things to shared Strings ! 

    public static String default_mdasDomainName = new String("");

    public static String default_mdasDomainHome = new String("");

    public static String default_srbHost = new String("");

    public static int default_srbPort = 0; 

    public static String default_resource = new String(""); // vleMatrixStore";

    public static String default_mcatZone = new String(""); 

    /** Currently disabled: it is not recommend using plain text password fields in 'memory' 
     */
    public static String default_passwd = null;  

    public static String default_authScheme = ServerInfo.GSI_AUTH;

    public static String default_mdasCollectionHome = "/DOMAIN/home/";

    static final String SRBSETTINGS_FILE = "srbsettings.prop";

    static final String ATTR_MDASDOMAINHOME = "mdasDomainHome"; 
    
    static final String ATTR_MDASCOLLECTIONHOME = "mdasCollectionHome";
    
    static final String ATTR_MDASDOMAINNAME = "mdasDomainName";

    static final String ATTR_MCATZONE = "matZone"; 
    
	static final String ATTR_DEFAULTRESOURCE = "defaultResource"; 
	
    /** 
     * Default user space SRB Settings file Prefix can 
     * either be VLET_INSTALL or VLET_SYSCONF_DIR 
     */

    /** SRB Server Attributes */ 
    public static final String srbAttributeNames[] = 
    { 
        ATTR_USERNAME, 
        ATTR_HOSTNAME, 
        ATTR_PORT, 
        ATTR_MDASDOMAINNAME, 
        ATTR_MDASDOMAINHOME,
        ATTR_MDASCOLLECTIONHOME, 
        ATTR_DEFAULTRESOURCE, 
        ATTR_MCATZONE, 
        AUTH_SCHEME, 
        ATTR_PASSIVE_MODE 
    };

	public static final String ATTR_RESOURCE = "Resource"; 

    /** SYSCONF_DIR/etc/srbsettings.prop */ 
    public static VRL getSRBSettingsFile()
    {
        return Global.getInstallationConfigDir().appendPath(SRBSETTINGS_FILE); 
    }

    public static String getUserSRBConfigDir()
    {
        return Global.getUserHome() + "/.srb";
    }
    
    /** Construct empty list with server defaults */ 
    public static VAttributeSet createDefault() 
    {
        VAttributeSet set=new VAttributeSet("SRBConfiguration");
        // indicate editable 
        
        set.put(ATTR_USERNAME,Global.getUsername()); 
        set.put(ATTR_HOSTNAME,default_srbHost); 
        set.put(ATTR_PORT,default_srbPort); 
        set.put(ATTR_MDASDOMAINNAME,default_mdasDomainName); 
        set.put(ATTR_MDASDOMAINHOME,default_mdasDomainHome); 
        set.put(ATTR_DEFAULTRESOURCE,default_resource); 
        set.put(new VAttribute(AUTH_SCHEME,ServerInfo.authSchemes, ServerInfo.GSI_AUTH));
        set.put(ATTR_MDASCOLLECTIONHOME,default_mdasCollectionHome);
        set.put(ATTR_MCATZONE,default_mcatZone);
        set.put(ATTR_PASSIVE_MODE,Global.getPassiveMode());
        
        return set; 
    }
    
  
    /** Used for testing purposes */
    public static void setUseSRBPropertiesFile(boolean val)
    {
        _debugUseSRBPropertiesFile=val; 
    }
    
    /**
     * Update/initialize ServerInfo attribute from VRL including 
     * VRL (Query) Attributes like, ?attribute=value&attribute2=value2&...
     *  
     * Might change ServerInfo ID ! 
     * @param location
     */
    public static void  updateAttributesFromVRL(ServerInfo info,VRL location)
    {
        if (location==null)
        {
            sDebug("updateAttributeFromVRL: NULL location");
            return;
        }
        
        // 
        // Try to parse user +domain information from Location 
        // 
        
        // ***
        // *** Strip URL and update (Optional) ServerAttributes. 
        // ***
        
        String host = location.getHostname();
         
         // use host from URI: 
//         if ((host!=null) && (!host.equalsIgnoreCase("")))
//             info.setServerHostname(host);  

         int port =location.getPort(); 

         // use port from URI: 
         if (port>0) 
             info.setPort(port); 
         
        // *** 
        // *** ENHANCED URL for SRB: 
        // *** get extra Attributes which might be specied in URL
        // *** 
         
        VAttributeSet attrSet=location.getQueryAttributes();
        
        if (attrSet!=null)
        {
            sInfo(" SRB: Using VRL properties:"+attrSet); 
        
            for (String attrName:srbAttributeNames)
            {
                VAttribute attr=null;
 
                if ((attr=attrSet.get("srb."+attrName))!=null)
                {
                    // strip "srb." from attrname 
                    String attrValue=attr.getStringValue(); 
                    
                    VAttribute orgAttr = info.getAttribute(attrName);
                    VAttribute newAttr; 
                    // update original value 
                    if (orgAttr!=null)
                    {
                        sInfo("Updating Property from URI:"+attr); 
                        orgAttr.setValue(attr.getValue());
                        newAttr=orgAttr; 
                    }
                    else
                    {
                        sInfo("Setting new Property from URI:"+attr);     
                        newAttr=new VAttribute(attrName,attrValue); 
                    }
                    info.setAttribute(newAttr); 
                }
            }
        }
        
        
        // passwd authentication
        // Backward compatibility: Update Password if password is used
        String passwd=null;
        
        if ((passwd=location.getPassword())!=null)
        {
            // passwd in URI overrides auth settings:
            info.setAuthScheme(ServerInfo.PASSWORD_AUTH); 
            info.setPassword(passwd); 
            sDebug("*** Using password from URI ***");
        }
        else if (info.useGSIAuth()==false)
        {
            // non GSI authentication, passwd must be supplied in AthenticationStore
            /* passwd=getPassword(); 
              
              if (passwd!=null)
                ; ? code missing 
            */
        }
        
    }

    private static void sDebug(String msg)
    {
        Global.debugPrintln(SrbConfig.class,msg); 
        //Global.errorPrintln(SRBConfig.class,msg); 
    }
    
    private static void sInfo(String msg)
    {
        Global.infoPrintln(SrbConfig.class,msg); 
     
    }

    public static String[] getDefaultSRBAttributesNames()
    {
        return SrbConfig.srbAttributeNames; 
    }

}
