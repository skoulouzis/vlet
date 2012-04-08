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
 * $Id: TestLFCServer.java,v 1.5 2011-06-07 15:15:11 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:11 $
 */ 
// source: 

package test;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.util.ChecksumUtil;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.lfc.LFCFile;
import nl.uva.vlet.vfs.lfc.LFCFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;



public class TestLFCServer
{
	public static void main(String args[])
	{
		try
		{
//			testLfc1();
//		    testComments();
		   // testChecksum();
		    
		    VRS.exit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
//	private static void testChecksum(){
//        VRSContext context = VRSContext.getDefault();
//        VRL host;
//        try
//        {
//            host = new VRL("lfn://lfc.grid.sara.nl:5010");
//            String path= "/grid/pvier/spiros/testFile.txt";
//            
//            
//            LFCFileSystem lfcFS = LFCFileSystem.getServerNodeFor(context,host);
//            lfcFS.connect();
//            
//            VRL vrl = host.append(path);
//            
////            LFCFile file = (LFCFile) lfcFS.createFile(vrl, true);
////            file.setContents("test Data");
//            
//            LFCFile file  = (LFCFile) lfcFS.getFile(vrl);
//            
////            InputStream remoteIn = file.getInputStream();
////            String checksum = ChecksumUtil.calculateChecksum(remoteIn, VChecksum.MD5);
//            
//            
////            System.err.println("Calculated checksum: "+checksum);
////            file.setChecksum(VChecksum.MD5,checksum);
//            
//            System.err.println("Got back: "+file.getChecksum(VChecksum.MD5));
//            
//        }
//        catch (VlURISyntaxException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (VlException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//	}
//	
//    private static void testComments(){
//        try
//        {
//            VRSContext context = VRSContext.getDefault();
//            VRL host = new VRL("lfn://lfc.grid.sara.nl:5010");
//            String path= "/grid/pvier/spiros/testFile.txt";
//            
//            
//            LFCFileSystem lfcFS = LFCFileSystem.getServerNodeFor(context,host);
//            lfcFS.connect();
//            
//            VRL vrl = host.append(path);
//            LFCFile file  = (LFCFile) lfcFS.getFile(vrl);
//                        
//            String comment = file.getComment();
//            System.out.println("Before Comment: "+comment);
//            
//            
//            comment = "java comments";
//                        
//            System.out.println("Setting: "+comment);
//            
//            file.setComment(comment);
//            
//            comment = file.getComment();
//            System.out.println("Comment: "+comment);
//            
//            
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        
//    }
//	
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
		String linkPath=orgPath+"linked-001";
		
		System.out.println(">>> Stat Original:"+orgPath);
		desc = lfc.fetchFileDesc(orgPath);
		print(desc);
		
		System.out.println(">>> SymLink:"+linkPath);
		//desc=lfc.createSymLink(orgPath,linkPath); 
		print(desc);
		
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
