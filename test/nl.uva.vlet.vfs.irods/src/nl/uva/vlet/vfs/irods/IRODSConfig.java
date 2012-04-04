package nl.uva.vlet.vfs.irods;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants; 
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * Irods Configuration class. 
 */
public class IRODSConfig
{
 
//    Hello potential iRODS user, 
//
//    A iRODS data storage account has been created for you.
//
//    username : piter_de_boer
//
//    A WEBDAV interface is available at:
//
//    https://irods.grid.sara.nl/~ 
//
//    For documentation and other clients check:
//
//    https://www.irods.org/
//
//    For icommands the ~/.irods/.irodsEnv file should look like :
//
//    irodsHost 'irods.grid.sara.nl'
//    irodsPort 50000
//    # Default storage resource name:
//    irodsDefResource 'ams_els'
//    # Home directory in iRODS:
//    irodsHome '/SARA_BIGGRID/home/your_username'
//    # Current directory in iRODS:
//    irodsCwd '/SARA_BIGGRID/home/your_username'
//    # Account name:
//    irodsUserName 'your_username'
//    # Zone:
//    irodsZone 'SARA_BIGGRID'
//    #For GSI authentication enable the next line
//    #irodsAuthScheme=GSI
//

    // =================
    // Global Properties 
    // =================

    public static final String PROP_IRODS_DEFAULT_USERNAME = "irods.default.username"; 
    
    public static final String PROP_IRODS_DEFAULT_HOSTNAME = "irods.default.host"; 

    public static final String PROP_IRODS_DEFAULT_PORT = "irods.default.port"; 

    public static final String PROP_IRODS_DEFAULT_RESOURCE = "irods.default.resource";
    
    public static final String PROP_IRODS_DEFAULT_ZONE = "irods.default.zone"; 
    
    /** Home prefix is the home path without the username appended to it */ 
    public static final String PROP_IRODS_DEFAULT_HOMES_PREFIX = "irods.default.homesdir";
    
    // =================
    // Global Attributes  
    // =================
    public static final String ATTR_IRODS_USERNAME =  VAttributeConstants.ATTR_USERNAME; 

    public static final String ATTR_IRODS_HOSTNAME  =  VAttributeConstants.ATTR_HOSTNAME;
    
    public static final String ATTR_IRODS_PORT  =  VAttributeConstants.ATTR_PORT;

    public static final String ATTR_IRODS_DEFAULT_RESOURCE = "irodsDefaultResource";
    
    public static final String ATTR_IRODS_ZONE = "irodsZone"; 

    public static final String ATTR_IRODS_HOMES_PREFIX  =  "irodsHomesdir";   

    public static final String ATTR_IRODS_AUTH_SCHEME = VAttributeConstants.AUTH_SCHEME; 

    public static String irodsAttributeNames[]=
    {
    	ATTR_IRODS_HOSTNAME, 
        ATTR_IRODS_PORT,
        ATTR_IRODS_USERNAME, 
        ATTR_IRODS_ZONE,
        ATTR_IRODS_DEFAULT_RESOURCE,
        ATTR_IRODS_HOMES_PREFIX,
        ATTR_IRODS_AUTH_SCHEME
    };

    public static void updateServerInfo(VRSContext context, ServerInfo info, VRL loc)
    {
        // ServerInfo attribute meta data:  
        info.setSupportURIAtrributes(true); 
        info.setNeedHostname(true); 
        info.setNeedPort(true); 
        info.setNeedUserinfo(true); 
        
        // create defaults from context:
        IRODSConfig defConfig = createFrom(context); 
        
        // use defaults to update ServerInfo
        for (VAttribute attr:defConfig.getAttributes())
        {
        	info.setIfNotSet(attr, attr.isEditable()); 
        }
         
        // Update Authentication Scheme
        String authScheme=info.getAuthScheme();
        
        if ( (info.useNoAuth()) )
        {
            info.setUseGSIAuth();
        }
        else
        {
            VAttribute attr=new VAttribute(ATTR_IRODS_AUTH_SCHEME,ServerInfo.authSchemes, authScheme);
            // not changeable now (no password support) 
            attr.setEditable(false);         
            info.setAttribute(attr);
        }
    }
    
    public static IRODSConfig createFrom(VRSContext context)
    {
    	return new IRODSConfig(context);
    }
    
    // ========================================================================
    // Instance
    // ========================================================================
    
    protected VAttributeSet config=new VAttributeSet();  
    
    protected IRODSConfig()
    {
    	
    }

    protected IRODSConfig(VRSContext context)
    {
    	init(context); 
    }

    protected void init(VRSContext context)
    {
    	
    	 // Set/check default configuration. Use environment property or default if not set. 
        initAttribute(context,ATTR_IRODS_USERNAME, PROP_IRODS_DEFAULT_USERNAME,Global.getUsername());  
        initAttribute(context,ATTR_IRODS_HOSTNAME, PROP_IRODS_DEFAULT_HOSTNAME,"IRODSHOST"); 
        initAttribute(context,ATTR_IRODS_PORT,     PROP_IRODS_DEFAULT_PORT,"50000");  
       
        initAttribute(context,ATTR_IRODS_ZONE,             PROP_IRODS_DEFAULT_ZONE,"SARA_BIGGRID"); 
        initAttribute(context,ATTR_IRODS_DEFAULT_RESOURCE, PROP_IRODS_DEFAULT_RESOURCE,"ams_els");  
        initAttribute(context,ATTR_IRODS_HOMES_PREFIX,     PROP_IRODS_DEFAULT_HOMES_PREFIX,"/homes");  
    
        
        VAttribute attr=new VAttribute(ATTR_IRODS_AUTH_SCHEME,ServerInfo.authSchemes, ServerInfo.GSI_AUTH);
        // not changeable now (no password support) 
        attr.setEditable(false);     
        this.config.set(attr); 
    }    


	protected void initAttribute(VRSContext context, String attrName,String propName, String defVal)
	{
		 String val=context.getStringProperty(propName);
	        
		 IrodsFS.logger.debugPrintf("IRODSConfig.set(): Default property    : %s='%s'\n",attrName,defVal);
		 IrodsFS.logger.debugPrintf("IRODSConfig.set(): Environment property: %s='%s'\n",propName,val); 
		 	
    	if (StringUtil.isNonWhiteSpace(val)==false)
    		val=defVal; 
    	
    	config.set(attrName,val); 
	}    
    
	public VAttribute[] getAttributes()
	{
		return this.config.toArray();
	}
}
