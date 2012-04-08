package test;


import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;



public class TestGetReplica
{
	public static void main(String args[])
	{
		try
		{
			testLfc1();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	public static void testLfc1() throws Exception 
	{
	    URI serverUri = new URI("lfn://lfc.grid.sara.nl:5010");
	    //String path="/grid/pvier/piter/testFile.txt";
	    String path="/grid/vlemed/matthan/data/emc_proc.mat"; 

        //URI serverUri = new URI("lfn://lfc03.nikhef.nl:5010");
        //String path= "/grid/pvier/piter/testFile2.txt";
        
		LFCServer lfc = new LFCServer(serverUri);
		
		// not neede lfc server auto (re)connects if necessary 
		// lfc.connect(); 

//		// test normal stat first:
		System.out.println(">>> Stat:"+path);
		FileDesc desc = lfc.fetchFileDesc(path);
		print(desc);
		String guid=desc.getGuid(); 
		    
		guid="b83ee344-4da4-49c7-af7a-a5aedf774164"; 
		
		ArrayList<ReplicaDesc> reps;
	
		LFCServer.getLogger().setLevel(Level.FINER);  
	        
		System.out.println("===\n=== GETREPLICA by guid===\n==="); 
	        
		reps = lfc.getReplicas(guid);
	           
		for (ReplicaDesc rep:reps)
		{
		    System.out.println(" --- Replica --- "); 
		    System.out.println(" - SFN="+rep.getSfn());
	                
		}
            
		lfc.disconnect();
            
		System.out.println("===\n=== GETREPLICA by path===\n==="); 
	        
		//reps = lfc.getReplicas(guid);
		reps = lfc.getReplicas(path,false);
		
		for (ReplicaDesc rep:reps)
		{
		    System.out.println(" --- Replica --- "); 
		    System.out.println(" - SFN="+rep.getSfn());
	                
	        }
	        
	        lfc.disconnect(); 
	        
	        reps = lfc.listReplicas(guid);
	        
	        System.out.println("===\n=== LISTREPLICA ===\n==="); 
	        
	        
	        for (ReplicaDesc rep:reps)
	        {
	            System.out.println(" --- Replica --- "); 
	            System.out.println(" - SFN="+rep.getSfn());
	            
	        }
	        
	    

		System.out.println("===\n=== END GETREPLICA ===\n==="); 
	    

	}
	
	public static void print(FileDesc fileDesc)
	{
		PrintStream outStream = System.out; 
		
		outStream.println(" --- file description ---");
		
		if (fileDesc==null)
			outStream.println(" *** NULL File Description *** "); 
		else
		{
			outStream.println(
					  " filename         = "+fileDesc.getFileName()
       				+"\n file guid        = "+fileDesc.getGuid()
					+"\n file isFile      = "+fileDesc.isFile()
					+"\n file isDir       = "+fileDesc.isDirectory()
					+"\n file is symLink  = "+fileDesc.isSymbolicLink()
					+"\n file class       = "+fileDesc.getFileClass()
					+"\n file mode        = "+Integer.toOctalString(fileDesc.getFileMode())
					+"\n file permissions = "+fileDesc.getPermissions()
					+"\n file id          = "+fileDesc.getFileId()
					+"\n filename status  = "+fileDesc.getStatus()
					+"\n filename ulink   = "+fileDesc.getULink()
					+"\n filename comment = "+fileDesc.getComment()
					+"\n filename ADate   = "+fileDesc.getADate()
					+"\n filename MDate   = "+fileDesc.getMDate()
					+"\n filename CDate   = "+fileDesc.getCDate()

					); 
			
		}
	}
}
