//package test;
//
//import java.io.PrintStream;
//import java.net.URI;
//import java.util.ArrayList;
//
//import org.glite.lfc.IOUtil;
//import org.glite.lfc.LFCException;
//import org.glite.lfc.LFCServer;
//import org.glite.lfc.internal.CnsConstants;
//import org.glite.lfc.internal.CnsMessage;
//import org.glite.lfc.internal.FileDesc;
//import org.glite.lfc.internal.ReplicaDesc;
//import org.omg.CORBA.DataOutputStream;
//
//public class Test_DOSx
//{
//    public static void main(String args[])
//    {
//        try
//        {
//            //testBol0();
//            testListReplica1();
//            //testGetReplica1();
//            //testE1();
//            //testRepeatLRs();
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        } 
//    }
//    
//    public static void testRepeatLRs() throws Exception 
//    {
//        // Test Bed Node 03 
//        // URI serverUri = new URI("lfn://lfc.grid.sara.nl:5010");
//        final URI serverUri = new URI("lfn://tbn03.nikhef.nl:5010");
//        //URI serverUri = new URI("lfn://lfc03.nikhef.nl:5010");
//
//        LFCServer lfc = new LFCServer(serverUri);
//        
//        // Check existing file: 
//        // test normal stat first:
//        String path= "/grid/pvier/piter/testFile.txt";
//      
//        System.out.println(">>> Stat:"+path);
//        
//        FileDesc desc = lfc.fetchFileDesc(path);
//        
//        System.out.println("Got file secription ="+(desc!=null));
//        String guid=null; 
//        
//        if (desc!=null)
//        {
//            guid=desc.getGuid();
//            System.out.println(" - guid="+guid);
//        }
//        
//      
//        final String fGuid=guid; 
//        
//        int N=10; 
//        for(int i=0;i<N;i++)
//        {
//            Runnable run=new Runnable()
//            {
//                    public void run()
//                    {
//                        ArrayList<ReplicaDesc> reps;
//                        
//                        try
//                        {
//                            LFCServer lfc = new LFCServer(serverUri);
//                            
//                            System.out.println(">>> STARTING <<<");
//                            reps = lfc.listReplicas(fGuid);
//                            System.out.println("nr of replicas=#"+reps.size());
//                        }
//                        catch (LFCException e)
//                        {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//                    }
//            };
//            
//            Thread thread=new Thread(run); 
//            thread.start();
//        }
//        
//        
//    }
//    
//    public static void testGetReplica1() throws Exception 
//    {
//        // Test Bed Node 03 
//        // URI serverUri = new URI("lfn://lfc.grid.sara.nl:5010");
//        //URI serverUri = new URI("lfn://tbn03.nikhef.nl:5010");
//        URI serverUri = new URI("lfn://lfc03.nikhef.nl:5010");
//
//        LFCServer lfc = new LFCServer(serverUri);
//        
//        // Check existing file: 
//        // test normal stat first:
//        String path= "/grid/pvier/piter/testFile.txt";
//        System.out.println(">>> Stat:"+path);
//        
//        FileDesc desc = lfc.fetchFileDesc(path);
//        
//        System.out.println("Got file secription ="+(desc!=null));
//        String guid=null; 
//        
//        if (desc!=null)
//        {
//            guid=desc.getGuid();
//            System.out.println(" - guid="+guid);
//        }
//        
//        
//       // lfc.disconnect(); 
//      
//        {
//            lfc.checkReConnected(); 
//            java.io.DataOutputStream output=lfc._getOutput();
//            java.io.DataInputStream input=lfc._getInput();
//            
//            // LISTREPLICA header: 
//            output.writeInt(CnsConstants.CNS_MAGIC2); 
//            output.writeInt(CnsConstants.CNS_GETREPLICA); 
//            output.writeInt(IOUtil.byteSize(guid) +35);
//   
//             output.writeInt(0); // uid 
//             output.writeInt(0); // gid 
//             output.writeLong(0); // cwd
//            
//             /// Write guid,path and/or se: 
//             IOUtil.writeString(output,null);
//             IOUtil.writeString(output,guid);
//             IOUtil.writeString(output,null);
//            
//             // Should be: output.writeShort( 1 ); // BOL (begin of list)
//            
//             output.flush();
//            
//            Thread.sleep(100); 
//            
//            System.out.println(">>> Receiving <<<");
//            CnsMessage rec=new CnsMessage();
//            
//            while (true)
//            {
//                rec.readHeader(input);
//
//                System.out.println("head="+rec.head());
//                System.out.println("type="+rec.type());
//                System.out.println("error="+rec.error());
//                System.out.println("size="+rec.size());
//               
//                if (rec.isResetSecurityContext()==true)
//                {
//                    System.out.println("RC:break!");
//                    lfc.disconnect();
//                    break; 
//                }
//                
//                if (rec.type()==4)
//                {
//                    System.out.println("IRC:break"); 
//                    break; 
//                }
//                
//                rec.readBody(input);
//                
//            }
//            
//            lfc.disconnect();
//                
//        }
//        
//    }
//    public static void testListReplica1() throws Exception 
//    {
//        // Test Bed Node 03 
//        // URI serverUri = new URI("lfn://lfc.grid.sara.nl:5010");
//        URI serverUri = new URI("lfn://tbn03.nikhef.nl:5010");
//        //URI serverUri = new URI("lfn://lfc03.nikhef.nl:5010");
//
//        LFCServer lfc = new LFCServer(serverUri);
//        
//        // Check existing file: 
//        // test normal stat first:
//        String path= "/grid/pvier/piter/testFile.txt";
//        System.out.println(">>> Stat:"+path);
//        
//        FileDesc desc = lfc.fetchFileDesc(path);
//        
//        System.out.println("Got file description ="+(desc!=null));
//        String guid=null; 
//        
//        if (desc!=null)
//        {
//            guid=desc.getGuid();
//            System.out.println(" - guid="+guid);
//        }
//        
//        
//       // lfc.disconnect(); 
//    
//       
//        {
//            lfc.checkReConnected(); 
//            java.io.DataOutputStream output=lfc._getOutput();
//            java.io.DataInputStream input=lfc._getInput();
//            
//            output.writeInt(CnsConstants.CNS_MAGIC2); 
//            output.writeInt(CnsConstants.CNS_LISTREPLICA); 
//            output.writeInt(IOUtil.byteSize(guid) +35);
//            
//            output.writeInt(0); //uid
//            output.writeInt(0); //gid
//            output.writeShort(0);//1296 ); // size of nbentry
//            output.writeLong(0); //cwd
//            
//           // for (int i=0;i<10000;i++)
//            //    output.writeChar('a'); 
//            
//            //output.writeByte( 0x0 ); // NO ACTUAL PATH!
//            IOUtil.writeString(output,guid); 
//            output.writeShort( 1 ); // BOL (begin of list)
//            
//             // Should be: output.writeShort( 1 ); // BOL (begin of list)
//            output.flush();
//            
//            Thread.sleep(100); 
//            System.out.println(">>> Receiving <<<");
//
//            int index=0; 
//            CnsMessage rec=new CnsMessage();
//            
//            while (true)
//            {
//                rec.readHeader(input);
//
//                System.out.println("head="+rec.head());
//                System.out.println("type="+rec.type());
//                System.out.println("error="+rec.error());
//                System.out.println("size="+rec.size());
//               
//                if (rec.isResetSecurityContext()==true)
//                {
//                    System.out.println("RC:break!");
//                    lfc.disconnect();
//                    break; 
//                }
//                
//                if (rec.type()==4)
//                {
//                    System.out.println("IRC:break"); 
//                    break; 
//                }
//                
//                rec.readBody(input);
//                
//            }
//            
//            lfc.disconnect();
//        }
//        
//    }
//    
//    public static void testBol0() throws Exception 
//    {
//        // Test Bed Node 03 
//        URI serverUri = new URI("lfn://tbn03.nikhef.nl:5010");
//        //URI serverUri = new URI("lfn://lfc03.nikhef.nl:5010");
//
//        LFCServer lfc = new LFCServer(serverUri);
//        
//        // Check existing file: 
//        // test normal stat first:
//        String path= "/grid";
//        System.out.println(">>> Stat:"+path);
//        
//        FileDesc desc = lfc.fetchFileDesc(path);
//        
//        System.out.println("Got file secription ="+(desc!=null));
//        String guid=null;   
//        long fileid=0;
//        
//        if (desc!=null)
//        {
//            guid=desc.getGuid();
//            System.out.println(" - guid="+guid);
//            fileid=desc.getFileId();
//        }
//        
//        System.out.println("file id="+fileid); 
//        
//         
//        {
//            lfc.checkReConnected(); 
//
//            java.io.DataOutputStream output=lfc._getOutput();
//            java.io.DataInputStream input=lfc._getInput();
//            
//            // OPEN DIR 
//            output.writeInt(CnsConstants.CNS_MAGIC2); 
//            output.writeInt(CnsConstants.CNS_OPENDIR); 
//            output.writeInt(30+IOUtil.byteSize(path,null)); // message size 
//            
//            output.writeInt(0); //uid
//            output.writeInt(0); //guid 
//            output.writeLong(0); //cwd
//                    
//            // write strings includes null terminating character
//            IOUtil.writeString(output, path); 
//            IOUtil.writeString(output, null);
//            
//            // Do not parse result, just continue: 
//            
//            //READDIR: 
//            // READDIR header: 
//            output.writeInt(CnsConstants.CNS_MAGIC2); 
//            output.writeInt(CnsConstants.CNS_READDIR); 
//            output.writeInt(34); // message size 
//   
//            output.writeInt(0); // uid 
//            output.writeInt(0); // gid
//            
//            output.writeShort( 1 );     // ls -l
//            output.writeShort( 50 );    // max 50 ???
//            output.writeLong( fileid );
//            
//            // *** BOL IS 0 ***
//            // Should be: output.writeShort( 1 ); // BOL (begin of list)
//            output.writeShort( 1); // BOL (begin of list)
//            // *** END BOL *** 
//            
//            output.flush();
//            
//            System.out.println(">>> Receiving <<<");
//            int index=0; 
//            CnsMessage rec=new CnsMessage();
//            
//            // receive twice (opendir+readdir) 
//            for (int i=0;i<2;i++)
//             while (true)
//              {
//                rec.readHeader(input); 
//                rec.readBody(input);
//                System.out.println("head="+rec.head());
//                System.out.println("type="+rec.type());
//                System.out.println("error="+rec.error());
//                System.out.println("size="+rec.size());
//
//                if (rec.isResetSecurityContext()==true)
//                {
//                    System.out.println("RC:break!");
//                    lfc.disconnect();
//                    break; 
//                }
//                
//                if (rec.type()==4)
//                {
//                    System.out.println("IRC:break"); 
//                    break; 
//                }
//                
//            }
//            
//            lfc.disconnect();
//        }
//        
//    }
//   
//    
//
//}
