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
 * $Id: LFCExample.java,v 1.18 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vfs;

import java.util.ArrayList;

import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VReplicatable;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/**
 * LFC Example. 
 * 
 * @author P.T. de Boer
 */

public class LFCExample
{
    public static void main(String args[]) 
    {
        try
        {
            // Example of pre initialization of "Global" properties as can be specified
            // in "~/.vletrc/vletrc.prop" or in VLET_INSTALL/etc/vletrc.prop
        	
            // 
        	// Disable VBrowser auto configuration 
            // does not save to nor load from user ~/.vletrc configuration files!
            // 
            GlobalConfig.setUsePersistantUserConfiguration(false); 
            GlobalConfig.setCACertificateLocations("/home/ptdeboer/certs/,/etc/certificates/"); 
            
            // Custom context or use VRSContext.getDefault();  
            VRSContext context=new VRSContext();

            // ===
            // Specify context settings ! 
            // See GlobalConfig.* for configurable Properties ! 
            // === 
            String voName="pvier"; 
            
            context.setProperty("bdii.hostname"," bdii.grid.sara.nl , bdii2.grid.sara.nl ");
            // default port:
            context.setProperty("bdii.port","21701"); 
            context.setProperty("tcp.connection.timeout","30000");
            context.setProperty("grid.proxy.lifetime","12"); 
            context.setProperty("grid.proxy.location","/tmp/myProxy"); 
            context.setProperty("grid.certificate.location",Global.getUserHome()+"/myglobus"); 
            context.setProperty("grid.proxy.voName",voName); 
            
            VRL lfcVrl=new VRL("lfn://lfc.grid.sara.nl:5010/"); 

            boolean useDefaultProxy=true;
            GridProxy proxy;
            
            if (useDefaultProxy)
            {
                proxy=context.getGridProxy();
            }
            else
            {
                // or: 
                proxy=GridProxy.loadFrom(context,"/tmp/x509_u601"); 
                context.setGridProxy(proxy); 
            }
            
            if (proxy.isValid()==false)
            {
                proxy.createWithPassword("Secret");
                
                if (proxy.isValid()==false)
                    throw new Exception("Coudn't create Valid Grid Proxy, please create one manually."); 
                // or: 
                // proxy.createWithPassword("password");
            }
            
            message("=== BDII configuration ==="); 
            
            // List available LFCs: 
            message(" - Using BDII service:"+context.getBdiiService().getURI());
            ArrayList<ServiceInfo> lfcInfos = context.getBdiiService().getLFCsforVO(voName);
            for (ServiceInfo info:lfcInfos)
                message(" - LFC Info=lfc://"+info.getHost()+":"+info.getPort());
            
            // List available Storage Areas:
            ArrayList<StorageArea> seInfos = context.getBdiiService().getSRMv22SAsforVO(voName);
            for (StorageArea info:seInfos)
                message(" - SA Info=srm://"+info.getHostname()+"/"+info.getStoragePath()); 
            
            // Autocreate configuration if there is no server configuration
            // The actual ServerInfo is stored inside VRSContext. 
            ServerInfo lfcConfig = context.getServerInfoFor(lfcVrl,true);
            
            // print out config. 
            String names[]=lfcConfig.getAttributeNames();
            message("--- Current LFC Configuration ---"); 
            for (String name:names)
            {
                // VAttributes are {Name,Type,Value} triples.
                VAttribute attr=lfcConfig.getAttribute(name); 
                message("LFC property "+name+"='"+attr.getValue());// +"' <"+attr.getType()+">"); 
            }

            // You can use the same settings here from the "Server Settings" tab from
            // the Properties Dialog from the VBrowser:
            
            // comma separated list of storage elements
            lfcConfig.setAttribute("listPreferredSEs","srm.grid.sara.nl,tbn18.nikhef.nl");
            lfcConfig.setAttribute("replicaSelectionMode","Preferred"); // See Dialog for options. 
            lfcConfig.setAttribute("replicaCreationMode","Preferred"); // See Dialog for options.
            	
            // Update Config! This to make sure VRSContext uses new Configuration!
            lfcConfig.store(); 
            
            message("--- Updated LFC Configuration ---"); 
            for (String name:names)
            {
                VAttribute attr=lfcConfig.getAttribute(name); 
                message("LFC property "+name+"='"+attr.getValue()+"'"); 
            }
            
            // create custom client: 
            VFSClient vfs=new VFSClient(context); 
            
            VRL destVrl = lfcVrl.append("/grid/pvier/ptdeboer/file.txt");
            
            VRL parentVrl=destVrl.getParent();
            
            // List parent directory: 
            VFSNode[] childs = vfs.list(parentVrl);
            message("Listing contents of parent:"+parentVrl); 
            for (VFSNode node:childs)
            {
            	message(" - "+node); 
            }
            
            VFSNode sourceNode = vfs.openLocation("file:/home/ptdeboer/file.txt");
            message("Trying to upload:"+sourceNode); 
            
            if (vfs.existsFile(destVrl))
            {
            	throw new Exception("*** Warning. Destination already exists:"+destVrl); 
            	// Or delete file:
            	// vfs.getFile(destVrl).delete(); 
            }
            
            VFile result = ((VFile)sourceNode).copyToFile(destVrl);
            System.out.println("result="+result); 
            
            VReplicatable replicatable=((VReplicatable)result); 
            
            VRL vrls[]=replicatable.getReplicas();
            message(" Replicas: "); 
            for (VRL vrl:vrls)
            {
                message(" - "+vrl); 
            }
            

            // ==========================
            // Example to do monitoring: 
            // ==========================
            
            DefaultTaskMonitor monitor = new DefaultTaskMonitor(); 
            monitor.startTask("Replicating file",1);
            
            // background thread can optionally monitor progress using "monitor" object 
            replicatable.replicateTo(monitor,"tbn18.nikhef.nl");
            monitor.endTask("Replicating file"); 
            
            vrls=replicatable.getReplicas();
            message(" Updated Replicas: "); 
            for (VRL vrl:vrls)
            {
                message(" - "+vrl); 
            }
            
        }        
        catch (Exception e)
        {
            System.err.println("*** Error:"+e); 
            e.printStackTrace(); 
        }
        
        // cleanup resources and exit; 
		VRS.exit();
    }
 
    public static void message(String str)
    {
        System.out.println(str); 
    }
    
}

