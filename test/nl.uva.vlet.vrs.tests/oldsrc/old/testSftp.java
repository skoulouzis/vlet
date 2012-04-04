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
 * $Id: testSftp.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

//package old;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.PipedOutputStream;
//
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vfs.VDir;
//import nl.uva.vlet.vfs.VFSNode;
//import nl.uva.vlet.vfs.VFile;
//import nl.uva.vlet.vfs.sshfs.SftpFileSystem;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrs.ServerInfo;
//import nl.uva.vlet.vrs.VRSContext;
//
//public class testSftp
//{
//    public static void simpleTest()
//    {
//        // Global.setShowDebug(true);
//
//        try
//        {
//            ServerInfo info = new ServerInfo(VRSContext.getDefault(), new VRL(
//                    "sftp://vletest@pc-vlab17.science.uva.nl:22/"));
//
//            // password of the day:
//            info.setPassword("");
//
//            // AuthenticationDialog.askAuthentication("Password for vletest@pc-vlab17.science.uva.nl",info);
//
//            SftpFileSystem server = new SftpFileSystem(VRSContext.getDefault(), info);
//            VDir dir = (VDir) server.getPath("/home/vletest");
//            VFile file = dir.createFile("writefile", true);
//
//            testOutputStream(file, 30000, 11);
//            testOutputStream(file, 60000, 11);
//            testOutputStream(file, 90000, 11);
//            testOutputStream(file, 900000, 11);
//
//            VFSNode nodes[] = dir.list();
//            VFSNode nodes2[] = null;
//
//            if (false)
//            {
//                if (false)
//                    for (VFSNode node1 : nodes)
//                    {
//                        System.out.println("entry 1=" + node1);
//                        if (node1.isDir())
//                        {
//                            nodes2 = ((VDir) node1).list();
//                            for (VFSNode node2 : nodes2)
//                            {
//                                System.out.println("entry 2=" + node2);
//                            }
//
//                        }
//                    }
//            }
//
//            dir = dir.getDir("subdir1");
//
//            nodes = dir.list();
//
//            for (VFSNode node : nodes)
//            {
//                System.out.println("entry 2=" + node);
//            }
//
//        }
//        catch (VlException e)
//        {
//            System.out.println("***Error: Exception:" + e);
//            System.out.println("Message=" + e.getMessage());
//
//            e.printStackTrace();
//        }
//    }
//
//    public static void testOutputStream(VFile file, int bufsize, int nrWrites) throws VlException
//    {
//
//        int nrWritten = 0;
//
//        byte buffer[] = new byte[bufsize];
//
//        /**
//         * for (int i=0;i<bufsize*nrWrites;i+=bufsize) {
//         * file.writeBytes(i,buffer,0,bufsize);
//         * //file.streamWrite(buffer,0,bufsize); }
//         */
//
//        OutputStream outps = file.getOutputStream();
//
//        try
//        {
//
//            for (int i = 0; i < bufsize * nrWrites; i += bufsize)
//            {
//                int last = 0;
//
//                if (i + 1 == nrWrites)
//                    last = 1;
//
//                outps.write(buffer, 0, buffer.length - last);
//                nrWritten += bufsize;
//                // file.writeBytes(i,buffer,0,bufsize);
//                // file.streamWrite(buffer,0,bufsize);
//            }
//
//            // write closing byte
//            outps.write(buffer, buffer.length - 1, 1);
//
//            if (outps instanceof PipedOutputStream)
//            {
//                PipedOutputStream pops = (PipedOutputStream) outps;
//            }
//
//            // outps.write(nullbuffer);
//            outps.flush();
//            outps.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println("***Error: Exception:" + e);
//            e.printStackTrace();
//        }
//
//        System.out.println("nr written        =" + nrWritten);
//        System.out.println("Length after write=" + file.getLength());
//
//        byte newcontents[] = file.getContents();
//
//        System.out.println("size of getcontents=" + newcontents.length);
//        file.delete();
//
//    }
//
//    public static void main(String args[])
//    {
//        simpleTest();
//
//    }
//
//}
