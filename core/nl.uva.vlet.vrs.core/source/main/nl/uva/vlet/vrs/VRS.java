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
 * $Id: VRS.java,v 1.12 2011-04-18 12:00:30 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:30 $
 */ 
// source: 

package nl.uva.vlet.vrs;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Virtual Resource System (VRS) Global class. 
 * <p>
 * It holds Resource Constants and other static references.  
 * Use this class to get global (static) instances of the VRS Classes. 
 * <p>
 * Initialization dependency: VRS -> {VRSContext, Registry, Global} 
 * 
 * @author P.T. de Boer 
 */
public class VRS
{
	// =======================================================================
	// CLASS 
	// =======================================================================
	public static final String VRL_TYPE = "VRL";
	
	public static final String MYVLE_TYPE  = "MyVLe";
    
	public static final String SERVER_TYPE = "Server";
    
	public static final String LINK_TYPE   = "Link";
    
	public static final String FILE_TYPE   = "File";
    
	public static final String DIR_TYPE    = "Dir";

	public static final String TABLE_TYPE = "Table"; 

    /** Single location */ 
    public static final String RESOURCE_LOCATION_TYPE = "ResourceLocation";
    
    /** Resource/Server Description! (bdii,etc.) */ 
    public static final String RESOURCE_INFO_TYPE = "ResourceInfo";

    /** Location Group or Folder */ 
    public static final String RESOURCEFOLDER_TYPE = "ResourceFolder";
    
    /** ".vlink" for saved shortcut/link nodes */ 
    public static final String VLINK_EXTENSION = "vlink";
    
    /** ".vrsx" for saved resource descriptions */ 
    public static final String VRESX_EXTENSION = "vrsx";
    
    /** 'localhost' constant */  
    public static final String LOCALHOST="localhost";
  
    // public defined schemes:
    public static final String FILE_SCHEME="file"; 
    public static final String HTTP_SCHEME="http"; 
    public static final String HTTPS_SCHEME="https"; 
    public static final String SRB_SCHEME="srb"; 
    public static final String GFTP_SCHEME="gsiftp"; 
    public static final String SFTP_SCHEME="sftp";
	public static final String SSH_SCHEME = "ssh"; 
    public static final String MYVLE_SCHEME="myvle";
	public static final String FTP_SCHEME = "ftp"; 
	public static final String ANY_SCHEME = "any"; 
	public static final String VFS_SCHEME = "vfs"; 
	public static final String VJS_SCHEME = "vjs"; 
	public static final String LFN_SCHEME = "lfn"; 
	public static final String SRM_SCHEME = "srm"; 
    public static final String GUID_SCHEME = "guid";
    public static final String WMS_SCHEME = "wms"; 
    public static final String LB_SCHEME = "lb"; 
    public static final String INFO_SCHEME = "info";;

    /** 
     * The 'rfts' scheme isn't a real protocol, but currently used 
     * as experimental 'service' 
     */
    public static final String RFTS_SCHEME="rfts";

    public static final String RFTS_JOB_TYPE = "RFTSJob";
    
    public static final String RFTS_SERVER_TYPE = "RFTServer";
    
    public static final String RFTS_TRANSFER_TYPE = "RFTSTransfer";

    // === INFO Resource System === // 
    
    public static final String INFO_GRID_NEIGHBOURHOOD =  "GridNeighbourhood";

    public static final String INFO_LOCALSYSTEM = "LocalSystem"; 

    
    /** 
     * Maximum file size to read at once into memory when using
     * getContents() 
     * 
     */
    public static final long MAX_CONTENTS_READ_SIZE = 1*1024*1024*1024;
    
    /** Default read size when performing stream copy */ 
    public static int DEFAULT_STREAM_WRITE_CHUNK_SIZE=32*1024; //32k should be enough for everybody !
    
    /** Default write size when performing stream copy */ 
    public static int DEFAULT_STREAM_READ_CHUNK_SIZE=32*1024; //32k should be enough for everybody ! 

    /** Default buffer size when performing stream copy */ 
	public static int DEFAULT_STREAM_COPY_BUFFER_SIZE=1*1024*1024; 
	
	public static final int DEFAULT_GRIDFTP_PORT = 2811;

	public static final int DEFAULT_SSH_PORT = 22;
	
	public static final int DEFAULT_HTTP_PORT = 80;
	
	public static final int DEFAULT_HTTPS_PORT = 443;

	public static final int DEFAULT_LFC_PORT = 5010;

	private static final int DEFAULT_SRB_PORT = 50000;

	
	// ========================================================================
	// Static (Class) Methods 
	// ========================================================================

	private static VRSContext defaultContext=null;

    private static Registry defaultRegistry=null; 

    public static void init()
    {
        // dummy method triggers static{...} initialization code. 
    }
    
	static
	{
		initClass(); 
	}
	
	private static void initClass()
	{
	    // Correct Order of initialization: 
	    // Other classes must obey this order: s
	    Global.init(); 
	    //
 		defaultRegistry = Registry.getInstance();
 		//
        defaultContext = VRSContext.getDefault(); 	
	}
	
	public static Registry getRegistry()
	{
	    if (defaultRegistry==null)
	        defaultRegistry=Registry.getInstance(); 
	    
	    return defaultRegistry;
	}
	
	public static VRSContext getDefaultVRSContext()
	{
	    if (defaultContext==null)  
	        defaultContext = VRSContext.getDefault();
	    
		return defaultContext; 
	}

    /** 
     * Clear VRS and cleanup running threads etc.
     */
    public static void exit()
    {
        VRS.getRegistry().dispose(); 
    }

	// ========================================================================
	// Class Helper methods 
	// ========================================================================
	
	/** 
	 * Helper function to get protocol default port. 
	 * <br> 
	 * ssh : 22 <br> 
	 * http : 80 <br> 
	 * https : 443 <br> 
	 * gsiftp : 2811 <br>
	 * <br>
	 * returns 0 if not known. 
	 */ 
	public static int getSchemeDefaultPort(String scheme)
	{
	    scheme=VRS.getDefaultScheme(scheme); 
	    
		if (scheme.compareTo(VRS.SFTP_SCHEME)==0)
			return VRS.DEFAULT_SSH_PORT; 

		if (scheme.compareTo(VRS.GFTP_SCHEME)==0)
			return VRS.DEFAULT_GRIDFTP_PORT;

		if (scheme.compareTo(VRS.HTTP_SCHEME)==0)
			return VRS.DEFAULT_HTTP_PORT;

		if (scheme.compareTo(VRS.HTTPS_SCHEME)==0)
			return VRS.DEFAULT_HTTPS_PORT;

		if (scheme.compareTo(VRS.LFN_SCHEME)==0)
			return VRS.DEFAULT_LFC_PORT;

		if (scheme.compareTo(VRS.SRB_SCHEME)==0)
		    return VRS.DEFAULT_SRB_PORT;
		
		return 0; 
	}

	public static String getDefaultScheme(String scheme)
	{
		return VRS.getRegistry().getDefaultScheme(scheme); 
	}

	/** 
	 * Returns ResourceSystem for the remote location 
	 * @throws VlException 
	 */ 
	public static VResourceSystem openResourceSystem(VRSContext context,VRL loc) throws VlException
	{
	    if (context==null)
	        throw new NullPointerException("VRSContext is NULL!"); 
	    
		return context.openResourceSystem(loc); 
	}
	
	/** Create new VRSClient to access the Virtual Resource System */ 
	public static VRSClient createVRSClient(VRSContext context)
	{
		return new VRSClient(context); 
	}

    public static boolean isGFTP(String scheme)
    {
        return (StringUtil.compareIgnoreCase(VRS.GFTP_SCHEME,VRS.getDefaultScheme(scheme))==0);  
    }
    
    public static boolean isSRM(String scheme)
    {
        return (StringUtil.compareIgnoreCase(VRS.SRM_SCHEME,scheme)==0);  
    }
    
    public static boolean isLFN(String scheme)
    {
        return (StringUtil.compareIgnoreCase(VRS.LFN_SCHEME,scheme)==0);  
    }

    public static boolean isGUID(String scheme)
    {
        return (StringUtil.compareIgnoreCase(VRS.GUID_SCHEME,scheme)==0);
    }
    

    public static String schemeToFactoryName(String scheme)
    {
        VRSFactory vrs = getRegistry().getVRSFactory(scheme,null); 
        if (vrs!=null)
            return vrs.getName();
        return scheme; 
    }

    /* Global Cross VRSContext Task Watcher */ 
    public static VRSTaskWatcher getTaskWatcher()
    {
        return VRSTaskWatcher.getDefault();
    }
	
}
