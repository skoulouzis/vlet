/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: TestSrmMultiLs.java,v 1.2 2011-09-26 14:07:58 ptdeboer Exp $  
 * $Date: 2011-09-26 14:07:58 $
 */ 
// source: 

package test;

import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.lbl.srm.SRMClientV2;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vfs.srm.SRMFSFactory;
import nl.uva.vlet.vfs.srm.SRMFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class TestSrmMultiLs
{

	public static void main(String args[])
	{
	    ClassLogger srmLogger=ClassLogger.getLogger(SRMClientV2.class); 
        SRMClientV2.setLogger(srmLogger);
        srmLogger.setLevelToDebug();
        
        
		try
		{
			Global.init();
			VRS.getRegistry().addVRSDriverClass(SRMFSFactory.class);
			
		    VRSContext context=new VRSContext(); 
		    VFSClient vfs=new VFSClient(context);
		    
		    VRL dirVrl=new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier"); 
		    
		    if (vfs.existsDir(dirVrl)==false)
		    {
                System.out.println("Creating dir:"+dirVrl); 
		        vfs.mkdirs(dirVrl); 
		    }
		    
		    SRMFileSystem srmFs = (SRMFileSystem)vfs.openFileSystem(dirVrl); 
		    Vector<String> paths=new Vector<String>();
	        paths.add(dirVrl.getPath()); 

	        int level=3; 
	        
	        do
	        {
                System.out.println("===============================================\n"); 
                System.out.println("*** Level="+level+" ***\n"); 
                System.out.println("===============================================\n"); 

                System.out.println(" === Paths === \n");
                
	            String arr[]=new String[paths.size()]; 
	            arr=paths.toArray(arr);
	            for (String path:paths)
	                System.out.println(" - path="+path);
	            
	            System.out.println(" === Nodes === \n");
                
	            VFSNode[] nodes = srmFs.listPaths(arr,true,1,900); 
	            
	            paths.clear(); // clear previous level 
	            
	            if (nodes==null)
	            {
                    System.out.println("*** NULL NODES ***\n");
	            }
	            else
	            {
                   System.out.printf("*** num nodes= #%d ***\n",nodes.length);
	                   
	                for (VFSNode node:nodes)
	                {   
	                    if (node.isDir())
	                    {
	                        paths.add(node.getPath());
	                    }
	                    
	                   //  System.out.println(" - node:"+node); 
	                }
	            } 
	            
	            level--;
	            
	        } while(level>0);  
	        
		    
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		   System.out.println(" === END === \n");
	}


}
