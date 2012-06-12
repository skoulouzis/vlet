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
 * $Id: GftpFSFactory.java,v 1.3 2011-06-21 10:35:09 ptdeboer Exp $  
 * $Date: 2011-06-21 10:35:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;


import static nl.uva.vlet.data.VAttributeConstants.ATTR_ALLOW_3RD_PARTY;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GROUP;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_OWNER;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PASSIVE_MODE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIQUE;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import nl.vlet.uva.grid.globus.GlobusUtil;


/**
 * Implementation of VFS Grid FTP.  
 * 
 * Current implementation is rather straightforward with no optimization, focussing on
 * transparency only.  
 * 
 * @author P.T. de Boer 
 */
public class GftpFSFactory extends VFSFactory
{
    // =================================================================
    // Class Fields 
    // =================================================================
    /** Default attributes names for all VFSNodes */

    public static final String[] gftpAttributeNames =
    { 
        ATTR_OWNER, 
        ATTR_GROUP,
        ATTR_UNIQUE
    };
    
    // =================================================================
    // Class Methods 
    // =================================================================
 
    /** current supported type "gftp://" */
    private static final String supportedTypes[] =
         { VFS.GFTP_SCHEME,"gftp","gridftp" };

    // =================================================================
    // Instance Methods
    // =================================================================
   
    public GftpFSFactory()
    {
        // Make sure Globus bindings are initialized 
        GlobusUtil.init(); 
    }
    
    /**
     * Implementation of VFS.getTypes. <br>
     * Returns list 
     * @see nl.uva.vlet.vfs.VFS#getSchemeNames()
     */
    public String[] getSchemeNames()
    {
        return supportedTypes;
    }

    /* (non-Javadoc)
     * @see nl.uva.vlet.vrs.i.VRS#getName()
     */
    public String getName()
    {
        return "GFTP";
    }

    @Override
    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL location)
        throws VlException 
    {
        super.updateServerInfo(context,info,location); 

        info.matchTemplate(getDefaultServerAttributes(),true); 
        
        // always use GSI auth:
        info.setUseGSIAuth();
        
        return info; 
    }
   
 
    
    private VAttributeSet getDefaultServerAttributes() 
    {
    	VAttributeSet attrs=new VAttributeSet(); 
    	// set default server attributes (if not set already) 
        attrs.put(new VAttribute(ATTR_PASSIVE_MODE,true),true);
        // old resource description didn't have this one: 
        attrs.put(new VAttribute(ATTR_ALLOW_3RD_PARTY,true),true);
        attrs.put(new VAttribute(ATTR_HOSTNAME,"GFTPHOST"),true);
        attrs.put(new VAttribute(ATTR_PORT,2811),true);
        
        
        // auto update when in debug mode ! 
        if (Global.getDebug())
        {
            //debug attribute
            attrs.put(new VAttribute(GftpFileSystem.ATTR_GFTP_BLIND_MODE,false),true);   
        }
        
		return attrs; 
	}

	@Override
    public void clear()
    {
      
    }

    @Override
    public VFileSystem createNewFileSystem(VRSContext context, ServerInfo info,VRL location)
            throws VlException 
    {
    	return new GftpFileSystem(context,info,location);
    }
    
    // =================================================================
    // Class Misc. Methods 
    // =================================================================
    
    public String getVersion()
    {
         return "GridFTP VFS Plugin version: "+ Global.getVersion();   
    }
        
    public String getAbout()
    {
        return "<html><body><center>"
        +"<table border=0 cellspacing=4 width=600>"
         + "<tr bgcolor=#c0c0c0><td> <h3> Globus GFTP VRS Plugin. </h3></td></tr>"
         + "<tr bgcolor=#f0f0f0><td>"
                 +"Globus 1.0 & 2.0 compatible Grid FTP Virtual File System plug-in"
                 +"</td></tr>" 
         + "</table></center></body></html>";
        
    }

}
