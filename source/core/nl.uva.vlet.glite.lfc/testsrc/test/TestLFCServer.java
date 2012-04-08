package test;


import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;



public class TestLFCServer
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
		
		// not neede lfc server auto (re)connects if necessary 
		// lfc.connect(); 

//		// test normal stat first:
		String path= "/grid/pvier/piter/testFile.txt";
		System.out.println(">>> Stat:"+path);
		FileDesc desc = lfc.fetchFileDesc(path);
		print(desc);
		
		//FileDesc desc=null; 
		
		// Test New SymLink command ! 
		String orgPath="/grid/pvier/piter/testFile.txt"; 
		String linkPath=orgPath+"linked-002";
		
		System.out.println(">>> Stat Original:"+orgPath);
		desc = lfc.fetchFileDesc(orgPath);
		print(desc);
		
		try
		{
		    System.out.println(">>> Try LinkStat original!:"+orgPath);
		    desc = lfc.fetchLinkDesc(orgPath);
		    print(desc);
		}
        catch (Exception e)
        {
            System.err.println("***Exception:"+e); 
        }
        
		try
		{
		    System.out.println(">>> SymLink:"+linkPath);
		    desc=lfc.createSymLink(orgPath,linkPath); 
		    print(desc);
		}
		catch (Exception e)
		{
		    System.err.println("***Exception:"+e); 
		}
		
		// check link: Does not work always   
		System.out.println(">>> (I) ReadLink:"+linkPath);
		String resultStr=lfc.readLink(linkPath); 
		System.out.println("  + readLink resultStr="+resultStr);
		 resultStr=lfc.readLink(linkPath); 
		System.out.println("  + readLink resultStr="+resultStr);
		 resultStr=lfc.readLink(linkPath); 
		System.out.println("  + readLink resultStr="+resultStr);
		 resultStr=lfc.readLink(linkPath); 
		System.out.println("  + readLink resultStr="+resultStr);
		 resultStr=lfc.readLink(linkPath); 
		System.out.println("  + readLink resultStr="+resultStr);
		
		//lfc.disconnect(); 
		
		// reconnect ! 
		//lfc = new MLFCServer(serverUri);
		
		System.out.println(">>> (II) StatG SymLink:"+linkPath);
		desc = lfc.fetchFileDesc(linkPath);
		print(desc);
		
		System.out.println(">>> (III) LinkStat SymLink:"+linkPath);
		desc = lfc.fetchLinkDesc(linkPath);
		print(desc);
//		
//		String kamelLink="lfn://lfc.grid.sara.nl:5010/grid/vlemed/kboulebiar/sdds/outputTest9"; 
//		
//		// New Method LinkStat: seems to return same information as stat
//		System.out.println(">>> ReadLink        "+kamelLink); 
//		resultStr=lfc.readLink(kamelLink); 
//		System.out.println(" - readLink resultStr="+resultStr);
//		
		// stat using GUID:
		String guid="c6cbd339-53b5-4a03-9d00-eb69ea4d71d5";
		System.out.println("Get File By GUID:"+guid);  
		desc = lfc.fetchFileDesc(guid,true); 
		
		print(desc);
		
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
