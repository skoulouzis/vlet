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
 * $Id: testStreamCopy.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.gftp.GftpFSFactory;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

public class testStreamCopy
{

    public static void main(String args[])
    {
        Debug("starting...");

        Global.setDebug(true);

        testStreamCopy();
        testSRB2GFTP();
    }

    public static void testStreamCopy()
    {
        VFSClient vfs = new VFSClient();

        try
        {
            VDir dir = vfs.getDir("file:///tmp");
            VDir testdir = dir.createDir("ptdeboer-vfsstreamtest", true);
            VFile file = vfs.getFile("file:///etc/passwd");
            VFile file2 = file.copyTo(testdir);
            String str = file2.getContentsAsString();
            System.err.println("contents=" + str);

        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static VRSContext vrs = VRSContext.getDefault();

    public static void testSRB2GFTP()
    {
        GftpFSFactory gftp = new GftpFSFactory(); // own object;

        try
        {
            // VFile file=(VFile)Registry.openLocation(new
            // VRL("gftp://ds2a.das2.nikhef.nl/project/ptdeboer/testFile.jpg"));
            // VDir srbdir=(VDir)Registry.openLocation(new
            // VRL("srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/piter.de.boer.vlenl/testvfs"));
            // file.copyTo(srbdir);

            VFile gftpFile = (VFile) vrs.openLocation(new VRL(
                    "gftp://ds2a.das2.nikhef.nl/project/ptdeboer/testBigFile.mp3"));
            VDir srbDir = (VDir) vrs.openLocation(new VRL(
                    "srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/piter.de.boer.vlenl/testvfs"));
            // and back:
            VFile newFile = gftpFile.copyTo(srbDir);

            newFile.copyTo(gftpFile.getParent(), "bigTestBack.mp3");

        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    private static void Debug(String msg)
    {
        Global.debugPrintln("testStreamCopy", msg);
    }

}
