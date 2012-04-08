package test;


import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;



public class TestLinks
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
		LFCServer lfc = new LFCServer(serverUri);
		
		LFCServer.getLogger().setLevel(Level.FINER);  
	      
		// not neede lfc server auto (re)connects if necessary 
		// lfc.connect(); 

//		// test normal stat first:
		String path= "/grid/pvier/piter/testFile2.txt";
		
		System.out.println(">>> Stat:"+path);
		FileDesc desc;
		 
		try
		{
		    desc = lfc.fetchFileDesc(path);
		}
		catch(Exception e)
		{
		    lfc.registerEntry(path); 
		    desc = lfc.fetchFileDesc(path);
		}
		
		print(desc);
		String guid=desc.getGuid(); 
		 
		for (int i=0;i< 4;i++)
		{
		    // Test New SymLink command ! 
		    String orgPath=path;  
		    String linkPath=orgPath+"-linked-"+i;
		    FileDesc ldesc = lfc.createSymLink(orgPath,linkPath); 
		    print(ldesc); 
		}
		
		ArrayList<String>list=lfc.listLinks(guid);
		
		for (String str:list)
		{
		    System.out.println(" Link: "+str); 
		}
		
		// lfc.deleteFile(null, linkPath); 
		
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
