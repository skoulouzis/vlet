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
 * $Id: VFSExample.java,v 1.3 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vfs;

import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/**
 * VFS Example. 
 * 
 * @author P.T. de Boer
 */

public class VFSExample
{
    public static void main(String args[]) 
    {
        try
        {
        	// =================================
            // Check Context and configuration 
            // =================================
          
            // Custom context or use VRSContext.getDefault();  
            VRSContext context=new VRSContext();

            boolean useDefaultProxy=true;
            GridProxy proxy;
            String proxyPath="/tmp/x509_u601"; 
            
            if (useDefaultProxy)
            {
            	// use default user settings: 
                proxy=context.getGridProxy();
            }
            else
            {
            	proxy=GridProxy.loadFrom(context,proxyPath); 
            	context.setGridProxy(proxy); 
            }
            
            // create proxy 
            if (proxy.isValid()==false)
            {
                proxy.createWithPassword("Secret");
                
                if (proxy.isValid()==false)
                    throw new Exception("Coudn't create Valid Grid Proxy, please create one manually."); 
                // or: 
                // proxy.createWithPassword("password");
            }
            
            message("Using BDII service :"+context.getBdiiService().getURI());
            message("Using proxy file   :"+proxy.getProxyFilename()); 
            message(" -  proxy lifetime :"+proxy.getTimeLeft()); 
            message(" -  proxy VO       :"+proxy.getVOName());  
             
            // =================================
            // Create VFSClient and copy to file 
            // =================================
            
            VFSClient vfs  = new VFSClient(context); 
            VRL sourceVrl  = new VRL("lfn://lfc.grid.sara.nl:5010/grid/pvier/ptdeboer/file.txt");
            VRL destDirVrl = vfs.getUserHomeLocation().append("download/"); 
            
            message("Trying to download:"+sourceVrl); 
            
            if (vfs.existsFile(sourceVrl)==false)
            {
            	throw new Exception("Couldn't find source file:"+sourceVrl);  
            	// Or delete file:
            	// vfs.getFile(destVrl).delete(); 
            }
            
            if (vfs.existsDir(destDirVrl)==false)
            {
            	message("Creating directory:"+destDirVrl);  
            	// create full destination: 
            	vfs.mkdirs(destDirVrl); 
            }
            
        	message("Dowloading to:"+destDirVrl);  
            
            VDir destDir=vfs.getDir(destDirVrl); 
            VFile source=vfs.getFile(sourceVrl); 
            
            VFile result = source.copyTo(destDir); 
            
            message("result="+result); 
        }
                 
        catch (Exception e)
        {
            System.err.println("*** Exception:"+e); 
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

