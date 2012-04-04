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
 * $Id: TestReplicaRegistration.java,v 1.4 2011-06-07 15:15:10 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:10 $
 */ 
// source: 

package test;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VReplicatable;
import nl.uva.vlet.vfs.lfc.LFCFSFactory;

import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

public class TestReplicaRegistration
{
    public static void main(String args[])
    {
        try
        {
            Global.init();
//            VRS.getRegistry().addVRSDriverClass(LFCFSFactory.class);
//            VRS.getRegistry().addVRSDriverClass(SRMFSFactory.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        VRS.exit();
        
        //testVReplicatable();
       
//        VRS.exit();
       
    }
    
    public static void testVReplicatable()
    {
        VFSClient vfs=new VFSClient(); 
        
        try
        {
            VRL rep1=new VRL("srm://hello.world/bogus/1"); 
            VRL rep2=new VRL("srm://hello.world/bogus/2");
            
            VFile file=vfs.newFile(new VRL("lfn://lfc.grid.sara.nl/grid/pvier/piter/testRegistration"));
            
            if (file.exists()==false)
                file.create();
 
            if (file instanceof VReplicatable)
            {
                VReplicatable repFile=((VReplicatable)file);  
                VRL reps[]=repFile.getReplicas();
                println("current replicas (should empty)"); 
                println(reps);
                
                if (reps!=null)
                {
                    try
                    {
                        repFile.unregisterReplicas(repFile.getReplicas());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                VRL newReps[]=new VRL[1]; 
                newReps[0]=rep1; 
                repFile.registerReplicas(newReps); 
                reps=repFile.getReplicas();
                println("-- Added on replica ---"); 
                println(reps);
                 
                newReps[0]=rep2; 
                repFile.registerReplicas(newReps);
                reps=repFile.getReplicas(); 
                println("-- Added another replica ---"); 
                println(reps);
                
                VRL delReps[]=new VRL[2]; 
                delReps[0]=rep1; 
                delReps[1]=rep2;
                
                repFile.unregisterReplicas(delReps); 
                reps=repFile.getReplicas(); 
                println("-- Deleted replicas ---"); 
                println(reps);
                
            }
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
        
    }

    private static void println(VRL[] reps)
    {
       for (VRL rep:reps)
       {
           System.out.println("Replica : "+rep); 
       }
        
    }
   
    
    public static void println(String str)
    {
        System.out.println(str); 
    }
}
