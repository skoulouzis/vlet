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
 * $Id: SrbFSFactory.java,v 1.4 2011-06-07 14:31:37 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:37 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_ACCESS_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CREATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_DATA_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_NRACLENTRIES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_OWNER;

import java.io.IOException;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;
/**
 * Implementation of SRBFS. 
 * VFS interface to remote SRB servers. 
 * <p>
 * For SRB URIs see: http://www.sdsc.edu/srb/jargon/SRBURI.html
 * 
 * @author P.T. de Boer
 * 
 */

public class SrbFSFactory extends VFSFactory
{
    //  ==========================================================================
    //  *** Class attributes ***
    //  ==========================================================================
    
    /** Extra SRB Attribute names */ 
    
    static String[] srbDirAttributeNames=
    {
        // ATTR_RESOURCE,  // SRB says not for directories/collections
        ATTR_OWNER,
        ATTR_NRACLENTRIES,
        ATTR_ACCESS_TIME,
        ATTR_CREATION_TIME,
    };
    
    // extra srb attributes (not in VFSNode)
    static String[] srbFileAttributeNames=
    {
    	SrbConfig.ATTR_RESOURCE,
        ATTR_OWNER,
        ATTR_NRACLENTRIES,
        ATTR_ACCESS_TIME,
        ATTR_CREATION_TIME,
        ATTR_DATA_TYPE
        //ATTR_CREATION_TIME_STRING,

    };
    
    final static String schemeNames[]= {VFS.SRB_SCHEME};

	//   static long maxChunkSize=1*1024*1024;
    // Constructors: 

    //  ==========================================================================
    //  Constructors/Initializors 
    //  ==========================================================================

    // must be public for the Registry to be able to create VRS instance ! 
    public SrbFSFactory()
    {
        //init()
    }
    
    /* Custom contructor for standalone client */
    /*
    public SRBFS(SRBServerInfo info)
    {
        SRBServerInfo.setDefault(info);
    }*/

    //  ==========================================================================
    //  Instance Stuff
    //  ==========================================================================


    /*public long getMaxChunkSize()
    {
        return -1;
    }*/

    /*public long getMaxFileSize()
    {
        return -1;
    }*/
   
    public String getName()
    {
        return "SRB";
    }
        
   
    @Override
    public VFileSystem createNewFileSystem(VRSContext context,ServerInfo info,VRL location) throws VlException
    {
    	Debug("openLocation="+location); 
    	
        // check/update (should already have been done by Registry before 
        // calling openLocation() !!! 
        ServerInfo srbInfo=updateServerInfo(context,info,location);
        
        // Extra Update from URL !
        // This allows for reconfiguration but this is dangerous
        SrbConfig.updateAttributesFromVRL(srbInfo,location); 
        srbInfo.store(); // update ! 
        
        // default:invalidate  
        if (srbInfo!=null)
        	srbInfo.setHasValidAuthentication(false); 
        
        // now use updated srbinformation to connect: 
        SrbFileSystem server=SrbFileSystem.createServerFor(context,srbInfo);
        
        // No exception occured: validate location 
        if (srbInfo!=null)
        	srbInfo.setHasValidAuthentication(true); 
        
        return server; 
    }
    
       
	

    public static void Debug(String string)
    {
        nl.uva.vlet.Global.debugPrintln("SRBFS",string);
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemeNames; 
    }

    /** 
     * Srb File/Dir Move implementation 
     * @throws IOException 
     */ 
    
    public static SRBFile srbMove(SRBFile source, SRBFile destDir,String optNewName) throws ResourceCreationFailedException
    {
        String newName=optNewName;
        
        if (newName==null)
            newName=source.getName();
        
        
        {
            // Create object to be renamed. 
            // Cannot use this.srbnode since this would lead to inconsistencies !  
            
            SRBFile oldSrbObj=new SRBFile((SRBFileSystem)source.getFileSystem(),source.getPath()); 
            SRBFile newSrbObj=new SRBFile(destDir,newName);  
             
            // ((SrbDir)this.getParent()).srbnode,this.srbnode.getName()); 
            
            // Apparently the Java File API considers this the way to 'move' a FileSystem object: 
            // if (oldSrbObj.isFile())
            //    newSrbObj.createNewFile();
           
            
            boolean result=oldSrbObj.renameTo(newSrbObj);
    
            if (result==true) // && (newSrbObj.exists()))
            {
                return newSrbObj; 
            }
            else
            {
                throw new ResourceCreationFailedException("SRB move failed (no reason given by server):"+newSrbObj);
            }
        }
    }

    public static SRBFile srbRename(SRBFile source, String newName,boolean nameIsPath) throws ResourceCreationFailedException, IOException
    {
        SRBFile newSrbObj=new SRBFile((SRBFileSystem)source.getFileSystem(),source.getPath());
        
        if (nameIsPath)
        {
            newSrbObj=new SRBFile((SRBFileSystem)source.getFileSystem(),newName); 
        }
        else
        {
            String fullpath=source.getParent()+VRL.SEP_CHAR+newName;
            newSrbObj=new SRBFile((SRBFileSystem)source.getFileSystem(),fullpath); 
        }
            
        boolean result=source.renameTo(newSrbObj);
    
        if (result==true) // && (newSrbObj.exists()))
        {
                return newSrbObj; 
        }
        else
        {
             throw new ResourceCreationFailedException("SRB rename failed:"+newSrbObj);
        }
    }
 
    @Override
    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL location) throws VlException
    {
        info=super.updateServerInfo(context, info, location);
        
        info.matchTemplate(getDefaultServerAttributes(context,location), true); 
        
        // ====
        // Regression bug!:
        // ===
        if ((info.useGSIAuth()==false) && (info.usePasswordAuth()==false))
        {
            info.setUseGSIAuth(); 
        }
        Global.infoPrintln(this,"After updateServerInfo hasPassiveMode="+info.hasAttribute(VAttributeConstants.ATTR_PASSIVE_MODE)); 
        Global.infoPrintln(this,"After updateServerInfo usePassiveMode="+info.getUsePassiveMode(false)); 
        
//        Debug(">>> useGSIauth       ="+info.useGSIAuth());
//        Debug(">>> Using ServerInfo ="+info);
//        Debug(">>> defaultResource   ="+info.getAttribute(SrbConfig.ATTR_DEFAULTRESOURCE); 
//        
        // store again SRBServerInfo might be a new object ! 
        info.store(); 
        return info; 
    }
  
  

    @Override
    public void clear()
    {
    	; 
    }
    
    public void debug(String str)
    {
        Global.debugPrintln(this,str); 
    }
    
    /**
     * Create Default SRB Attributes and update SRB Properties
     * from context and location.
     * 
     * @param context
     * @param location
     * @return
     */
    private VAttributeSet getDefaultServerAttributes(VRSContext context,
            VRL location)
    {
        // create defaults: 
        VAttributeSet set = SrbConfig.createDefault();
        String[] names = SrbConfig.srbAttributeNames;
        VAttributeSet uriAttrs=null;
        if (location!=null)
            uriAttrs=location.getQueryAttributes(); 
        
        for (String name:names)
        {
            String srbname="srb."+name;  
            debug("Checking attribute:"+srbname); 
            String value=null; 
            
            // check URI attribute: 
            if (uriAttrs!=null)
                value=uriAttrs.getStringValue(srbname); 
                
            // check context: (including vletrc.prop settings!) 
            if (StringUtil.isEmpty(value)==true)
                value=context.getStringProperty(srbname);
            
            VAttribute orgAttr=set.get(name);
            if (orgAttr!=null)
            {
                if (StringUtil.isEmpty(value)==false)
                {
                    orgAttr.setValue(value); 
                }
                orgAttr.setEditable(true);
            }

        }
        
        return set; 
    }
 
}
