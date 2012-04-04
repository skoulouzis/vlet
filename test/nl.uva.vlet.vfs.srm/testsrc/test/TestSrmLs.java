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
 * $Id: TestSrmLs.java,v 1.4 2011-09-26 14:07:58 ptdeboer Exp $  
 * $Date: 2011-09-26 14:07:58 $
 */ 
// source: 

package test;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.lbl.srm.SRMClientV2;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.srm.SRMFSFactory;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class TestSrmLs
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
		    
		    VRL dirVrl=new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier/ptdeboer/bigdir/"); 
		    
		    if (vfs.existsDir(dirVrl)==false)
		    {
                System.out.println("Creating dir:"+dirVrl); 
		        vfs.mkdirs(dirVrl); 
		    }
		    
		    int numFiles=1000; 
		    String filePref="test_file";
		    
		    boolean createFiles=false;  
            // boolean createFiles=true; 
		    
		    if (createFiles) 
		        for (int i=300;i<numFiles;i++)
		        {  
    		        VRL fileVrl=dirVrl.append(filePref+i); 
    		        
    		        if (vfs.existsFile(fileVrl))
    		        {
                        System.out.println("keeping file:"+fileVrl);
    		        }
    		        else
    		        {
                        System.out.println("creating file:"+fileVrl);
                        VFile result = vfs.createFile(fileVrl,true);
    		        }
	            }
		    
		    // single thread:
		    testLS(vfs,dirVrl); 
		    
		    // 10 threads: 
		    //for (int i=0;i<10;i++)
		    //    bgTestLS(vfs,dirVrl); 
           
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("=== END ===");
		
	}

	static void bgTestLS(final VFSClient vfs,final VRL dirVrl)
	{
	    Runnable task=new Runnable()
	    {
	        public void run()
	        {
	            try
                {
                    testLS(vfs,dirVrl);
                }
                catch (VlException e)
                {
                    e.printStackTrace();
                } 
	        }
	    };
	    
	    Thread thread=new Thread(task); 
	    thread.start(); 
	    
	}
	
    private static void testLS(final VFSClient vfs,final VRL dirVrl) throws VlException
    {
        VFSNode[] nodes = vfs.list(dirVrl);     
        System.out.printf(" - num nodes=%d\n",nodes.length); 
    
        //for (VFSNode node:nodes)
        {
          //  System.out.println(" - node:"+node); 
        }
    }

	
}
