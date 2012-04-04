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
 * $Id: testGFTP.java,v 1.2 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

/**
 * Implementation of testGFTP
 * 
 * @author P.T. de Boer
 */
public class testGFTP
{

    public static void main(String args[])
    {
        Debug("starting...");
        /*
         * try {
         * 
         * // perform default VFS tests...
         * 
         * doVFSTests();
         * 
         * 
         * // === // Testing STREAM IO // ===
         * 
         * // === remote root +parent
         * 
         * Debug("Getting remote ROOT directory"); GridFTPFS gftp=new
         * GridFTPFS(); // own object;
         * 
         * VDir
         * vfsdir=(VDir)gftp.getLocation("fs2.das2.nikhef.nl","/home1/ptdeboer/vfs"
         * );
         * 
         * 
         * VFSNode list[]=vfsdir.list();
         * 
         * for (VFSNode node:list) { Debug("vfs:"+node); }
         * 
         * 
         * // tests upload ! VFile source= new
         * LFile("/home/ptdeboer/vfs2/big/sealmp3.mp3");
         * 
         * VFile remotefile=source.copyTo(vfsdir);
         * 
         * if (source.getLength()!=remotefile.getLength()) {
         * Global.errorPrintln("testGFTP","*** LENGTHS STILL DON'T MATCH!");
         * return; }
         * 
         * 
         * VFiletfile=(VFile)gftp.getLocation("fs2.das2.nikhef.nl",
         * "/home1/ptdeboer/vfs/.vlamrc"); VDir
         * newDir=vfsdir.createDir("copyTo",true); VDir
         * sdir=vfsdir.createDir("source",true);
         * 
         * VFile emptyfile=sdir.createFile("emptyFile",true);
         * 
         * tfile.copyTo(newDir); // copy file sdir.copyTo(newDir); // copy dir
         * 
         * VDir dir=vfsdir.createDir("testIO",true); Debug("Created:"+dir);
         * 
         * // fetch remote file: VFile
         * rfile=(VFile)gftp.getLocation("fs2.das2.nikhef.nl","/etc/mtab");
         * String rstr=rfile.getContentsAsString("ASCII");
         * Debug("Contents of remote /etc/mtab:"+rstr);
         * 
         * Debug("/etc/mtab.length="+rfile.getLength());
         * Debug("/etc/mtab.ModificationTime="+rfile.getModificationTime());
         * 
         * VFile file=dir.createFile("writer.txt",true);
         * 
         * // == Testing Write Stream Debug("=== Testing Manual Write === ");
         * 
         * String testStr="Testing Testing";
         * 
         * 
         * 
         * 
         * 
         * OutputStream ostr=((VStreamWritable)file).getOutputStream();
         * 
         * byte buf[]={'a','a','p',0};
         * 
         * try { ostr.write(buf,0,3); ostr.write(buf,0,3); ostr.write(buf);
         * ostr.flush(); ostr.close(); } catch (IOException e) {
         * System.out.println("***Error: Exception:"+e); e.printStackTrace(); }
         * String str=file.getContentsAsString("ASCII");
         * 
         * 
         * Debug("Contents(1)="+str);
         * 
         * 
         * // trying syncRead:
         * 
         * byte buf2[]=new byte[99]; GftpServer
         * server=((GftpFile)file).getServer(); try { int
         * nrRead=server.syncRead(file.getPath(),0,buf2,0,buf2.length);
         * Debug("syncRead nr read=:"+nrRead);
         * 
         * for (int i=0;i<nrRead;i++) { Debug("read:["+i+"]="+buf2[i]); }
         * 
         * } catch (VlException e) {
         * System.out.println("***Error: Exception:"+e); e.printStackTrace(); }
         * 
         * // tests 2 file=dir.createFile("writer2.txt",true);
         * 
         * file.setContents(testStr);
         * 
         * str=file.getContentsAsString("ASCII");
         * 
         * Debug("Contents(2)="+str);
         * 
         * 
         * if (str.compareTo(testStr)!=0)
         * System.err.println("***ERROR: read did not procude testString"); else
         * System.err.println("Ok: reread written string is ok");
         * 
         * // === // Testing Getting remote Parent and Root directory // ===
         * 
         * Debug("Getting remote ROOT directory");
         * 
         * VDir dir1=(VDir)gftp.getLocation("fs2.das2.nikhef.nl","/home1");
         * 
         * Debug("Got:"+dir1);
         * 
         * VDir dir2=dir1.getParentDir();
         * 
         * Debug("Got parent:"+dir2);
         * 
         * VFSNode[] list2 = dir2.list();
         * 
         * for (VFSNode node:list2) { Debug("child:"+node); }
         * 
         * //==== Testing VFile interface
         * 
         * VDir
         * remoteDir=(VDir)gftp.getLocation("fs2.das2.nikhef.nl","/home1/ptdeboer"
         * );
         * 
         * VDir testDir=remoteDir.createDir("testFTP",true);
         * 
         * VFile pwdFile=new LFile("/etc/passwd");
         * 
         * VFile newFile=pwdFile.copyTo(testDir);
         * 
         * 
         * if (newFile.exists()==true) { System.err.println("GFTP Copy ok"); }
         * else { System.err.println("*** ERROR: GFTP Copy NOT ok");
         * 
         * }
         * 
         * 
         * } catch (VlException e) {
         * System.out.println("***Error: Exception:"+e); e.printStackTrace(); }
         */
    }

    static VRSContext vrs = VRSContext.getDefault();

    public static void doVFSTests()
    {
        VFSTester tester = new VFSTester();

        if (vrs.getGridProxy().isValid() == false)
        {

        }

        tester.setVerbose(1);

        try
        {
            VDir tmpdir = (VDir) vrs.openLocation(new VRL("file:///tmp"));
            VDir dir1 = tmpdir.createDir("vfstest", true);
            VDir dir2 = (VDir) vrs.openLocation(new VRL("gftp://fs2.das2.nikhef.nl/~"));
            dir2 = dir2.createDir("vfstest", true);

            tester.doTests(dir1, dir2);
            tester.doTests(dir2, dir1);

        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    static void Debug(String str)
    {
        Global.debugPrintln(testGFTP.class, str);
    }
}
