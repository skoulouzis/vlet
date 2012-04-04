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
 * $Id: TestSrmDteamCountLs.java,v 1.1 2011-09-26 14:07:58 ptdeboer Exp $  
 * $Date: 2011-09-26 14:07:58 $
 */ 
// source: 

package test;

import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vfs.srm.SRMFSFactory;
import nl.uva.vlet.vfs.srm.SRMFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class TestSrmDteamCountLs
{

	public static void main(String args[])
	{
	    
		try
		{
			Global.init();
			VRS.getRegistry().addVRSDriverClass(SRMFSFactory.class);
			
		    VRSContext context=new VRSContext(); 
		    VFSClient vfs=new VFSClient(context);
		    
		    VRL dirVrl=new VRL("srm://srm.ciemat.es:8443/pnfs/ciemat.es/data/dteam/generated"); 
		    
		    SRMFileSystem srmFs = (SRMFileSystem)vfs.openFileSystem(dirVrl);
		    
		    testCountLS(srmFs,dirVrl,1,900); 
		    
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		System.out.println(" === END === \n");
	}

    private static void testCountLS(SRMFileSystem srmFs, VRL dirVrl, int offset, int count) throws VlException
    {
        
        VFSNode[] nodes = srmFs.list(dirVrl.getPath(),offset,count); 
        
        if (nodes==null)
        {
            System.out.println("*** NULL NODES ***\n");
        }
        else
        {
            System.out.printf("*** num nodes= #%d ***\n",nodes.length);
            
            int index=0; 
            
            for (VFSNode node:nodes)
            {   
                System.out.println(" -[#"+(index++)+"] node:"+node); 
            }
        } 
    }


}
