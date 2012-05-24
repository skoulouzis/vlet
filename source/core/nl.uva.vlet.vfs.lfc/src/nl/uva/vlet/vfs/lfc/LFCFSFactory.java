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
 * $Id: LFCFSFactory.java,v 1.3 2011-04-18 12:21:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:21:27 $
 */ 
// source: 

package nl.uva.vlet.vfs.lfc;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.actions.ActionContext;
import nl.uva.vlet.actions.ActionMenuConstants;
import nl.uva.vlet.actions.ActionMenuMapping;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VFSFactory;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;


public class LFCFSFactory extends VFSFactory
{
    public static final String ATTR_LFC_FILEID="lfcFileId";
    public static final String ATTR_LFC_FILECLASS="lfcFileClass";
    public static final String ATTR_LFC_STATUS="lfcStatus";
    public static final String ATTR_LFC_ULINK="lfcULink";
    public static final String ATTR_LFC_COMMENT="lfcComment";
//    public static final String ATTR_LFC_CHECKSUMTYPE="lfcChecksumType";
//    public static final String ATTR_LFC_CHECKSUMVALUE="lfcChecksumValue";
    public static final String ATTR_NUM_REPLICAS = "nrOfReplicas" ;
    public static final String ATTR_LOGICAL_FILENAME = "logicalFileName";
    public static final String ATTR_LFC_REPLICAHOSTS =  "replicaSEHosts";
    
   
    public final static String schemes[] = { VRS.LFN_SCHEME,VRS.GUID_SCHEME };

    public final static String defaultServerAttributes[]=
        {
            VAttributeConstants.ATTR_SCHEME,
            VAttributeConstants.ATTR_HOSTNAME,
            VAttributeConstants.ATTR_PORT,
            ServerInfo.ATTR_DEFAULT_PATH,
            LFCFSConfig.ATTR_PREFERREDSSES, 
            LFCFSConfig.ATTR_GENERATED_DIRNAME,
            LFCFSConfig.ATTR_GENERATED_SUBDIR_DATE_SCHEME,
        };
    
//    @Override
//    public VFileSystem getFileSystem(VRSContext context, VRL location)
//            throws VlException
//    {
//        return getLFCFS(context, location);
//
//    }

    @Override
    public void clear()
    {

    }

    @Override
    public String getName()
    {
        return "LFC";
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes;
    }

    @Override
    public ServerInfo updateServerInfo(VRSContext context, ServerInfo info,
            VRL loc) throws VlException
    {
        // super update: 
        info=super.updateServerInfo(context, info, loc);

        VAttributeSet attrs = null;
        if (loc!=null)
            attrs=loc.getQueryAttributes(); 
        
        // strip old attributes and match default 
        info.matchTemplate(LFCFSConfig.createDefaultServerAttributes(context,attrs),true);
        VAttribute prefSEs=info.getAttribute(LFCFSConfig.ATTR_PREFERREDSSES);
        // Bug in previous version: strip out white spaces: 
        String listStr=StringUtil.stripWhiteSpace(prefSEs.getStringValue()); 
        prefSEs.setValue(listStr); 
        info.setAttribute(prefSEs); // update! 
        
        Global.infoPrintf(this,"List preferred SEs=%s\n",prefSEs); 

        // ====================================================
        // Explicit set <=0 to 5010 !
        // do no keep 0 or -1 into active used ServerInfo  ! 
        // ====================================================
        if (info.getPort() <= 0)
        {
            info.setPort(5010);
        }

        // explicit set/update to use GSI authentication.
        info.setUseGSIAuth();
        // store ! 
        info.store();
        
        return info;
    }

	@Override
	public VFileSystem createNewFileSystem(VRSContext context, ServerInfo info,
			VRL location) throws VlException 
	{
		return new LFCFileSystem(context,info,location);  
	}

	
    @Override
    public Vector<ActionMenuMapping> getActionMenuMappings()
    {
        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        String[] fileTypes={"File"};
        String[] dirTypes={"Dir"};
        String[] bothTypes={"File","Dir"};
        
        // LFC File mappings:
        ActionMenuMapping mapping;    
        // single selection action: 
//        ActionMenuMapping mapping;        
//        mapping=new ActionMenuMapping("singleUnregisterAll","Unregister LFN and Replicas","lfc");        
//        mapping.addTypeSchemeMapping(bothTypes,schemes); 
//        mappings.add(mapping);
//        
//        // for multi selection for only when source of action is LFC ! 
//        mapping=new ActionMenuMapping("selectionUnregisterAll","Unregister LFN and Replicas","lfc");        
//        mapping.addResourceMapping(bothTypes, schemes, bothTypes, schemes, ActionMenuConstants.SELECTION_ONE_OR_MORE);
//        mappings.add(mapping);        
        
        // single selection action: 
        mapping=new ActionMenuMapping("singleDelete","Delete file (including replicas)","lfc");
        mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_NONE);        
        mappings.add(mapping);
        
        // single selection action: 
        mapping=new ActionMenuMapping("singleForceDelete","Force delete file and unregister all","lfc");
        mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_NONE);        
        mappings.add(mapping);
        
        mapping=new ActionMenuMapping("singleDelete","Delete directory contents (including replicas)","lfc");
        mapping.addResourceMapping(dirTypes,schemes,null,null,ActionMenuConstants.SELECTION_NONE);        
        mappings.add(mapping);
        
        mapping=new ActionMenuMapping("singleForceDelete","Force delete directory and all contents","lfc");
        mapping.addResourceMapping(dirTypes,schemes,null,null,ActionMenuConstants.SELECTION_NONE);        
        mappings.add(mapping);

        // for multi selection for only when source of action is LFC ! 
        mapping=new ActionMenuMapping("selectionDelete","Delete selection (contents including replicas)","lfc");
        mapping.addResourceMapping(null, null, bothTypes, schemes,  ActionMenuConstants.SELECTION_ONE_OR_MORE);
        mappings.add(mapping);
        
        mapping=new ActionMenuMapping("selectionForceDelete","Force delete selection and unregister all","lfc");
        mapping.addResourceMapping(null, null, bothTypes, schemes,  ActionMenuConstants.SELECTION_ONE_OR_MORE);
        mappings.add(mapping);
        
        // trigger paste as alias if current selection or clipboard hold a LFC File or Directory. 
        
        mapping=new ActionMenuMapping("pasteAsLink","Paste as (LFC) Link");
        mapping.addResourceMapping(bothTypes, 
                                   schemes, 
                                   bothTypes, 
                                   schemes, 
                                   ActionMenuConstants.SELECTION_TYPE_CLIPBOARD | ActionMenuConstants.SELECTION_ONE_OR_MORE);
	    // Skip mapping For Now: Do NOT encourage users to create links !!
        //mappings.add(mapping);
        
        // ===
        // Replica to Preferred !
        // === 
        
        // single click action for one 'File' type(s) and LFC schemes but block selections ! 
        mapping=new ActionMenuMapping("singleReplicateToPref","Replicate file to Preferred SEs","replicas");
        mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_NONE); 
        mappings.add(mapping);
     
        // pattern which matches a multi FILE selection in the VBrowser: 
        mapping=new ActionMenuMapping("selectionReplicateToPref","Replicate selected files to preferred SEs","replicas");
        mapping.addSelectionTypeSchemeMapping(fileTypes,schemes); 
        mappings.add(mapping);

        // pattern which matches directories selection(s) in the VBrowser: 
        mapping=new ActionMenuMapping("recursiveReplicateToPref","Replicate directory (recursive!) to preferred SEs","replicas");
        mapping.addTypeSchemeMapping(dirTypes,schemes); 
        mappings.add(mapping);
        
        return mappings; 
    }
     
	  
    protected LFCFileSystem getLFCFS(VRSContext context,VRL source) throws VlException
    {
    	  VFileSystem vfs = super.openFileSystem(context,source);
    	  if (vfs instanceof LFCFileSystem)
    	  {
    		  return (LFCFileSystem)vfs; 
    	  }
    	  
    	  throw new ResourceNotFoundException("Can't get LFC FileSystem for:"+source); 
    }
 

    @Override
    public void performAction(ITaskMonitor monitor,VRSContext vrsContext, String methodName, ActionContext actionContext) throws VlException
    {
    	// The Actual Selection do perform the action with ! 
    	VRL selections[]=actionContext.getSelections();
    	
    	// ===
    	// The source is NOT part of the selection. It is the origin
    	// of the Action Click in the VBrowser. 
    	// ===
    	VRL source=actionContext.getSource(); 
         
    	if(methodName==null)
             return;
           
         // todo: check if VRLs are on the same server 
         if (methodName.compareTo("singleDelete") == 0)
         {
        	  
             LFCFileSystem server = getLFCFS(vrsContext,source); // (LFCFileSystem) super.getFileSystem(vrsContext,source); //getLFCFS(vrsContext,source); 
             server.recurseDelete(monitor,source,false); 
         }
         else if (methodName.compareTo("singleForceDelete") == 0)
         {
             LFCFileSystem server = getLFCFS(vrsContext,source);
             server.recurseDelete(monitor,source,true);
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("selectionDelete") == 0)
         {
             LFCFileSystem server = getLFCFS(vrsContext,selections[0]); 
             server.recurseDelete(monitor,selections,false);  
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("selectionForceDelete") == 0)
         {
             LFCFileSystem server = getLFCFS(vrsContext,selections[0]); 
             server.recurseDelete(monitor,selections,true);  
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("pasteAsLink") == 0)
         {
             // paste selection into destination (=source of paste action) 
             LFCFileSystem server = getLFCFS(vrsContext,source); 
             server.pastAsLink(monitor,source,selections);  
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("singleReplicateToPref") == 0)
         {
             // paste selection into destination (=source of paste action) 
             LFCFileSystem server = getLFCFS(vrsContext,source); 
             VRL vrls[]=new VRL[1]; 
             vrls[0]=source; // one source only !
             server.replicateToPreferred(monitor,vrls);  
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("recursiveReplicateToPref") == 0)
         {
             // paste selection into destination (=source of paste action) 
             LFCFileSystem server = getLFCFS(vrsContext,source); 
             server.recursiveReplicateToPreferred(monitor,source);  
         }
         // todo: check if VRLs really are on the same server 
         else if (methodName.compareTo("selectionReplicateToPref") == 0)
         {
             // paste selection into destination (=source of paste action) 
        	 // Warning: Source can either be parent directory (canvas click) or file which was right-clicked on 
             LFCFileSystem server = getLFCFS(vrsContext,source); 
             server.replicateToPreferred(monitor,selections);
         }
         else
         {
             // todo: 
             Global.warnPrintf(this,"Unknown LFC Action:%s\n",methodName); 
         }
      }

}
