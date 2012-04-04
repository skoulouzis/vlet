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
 * $Id: testLocalFS.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;

public class testLocalFS
{

    static void testErrors()
    {
        VFSClient vfs = new VFSClient();

        // try
        /*
         * catch (VlException e) {
         * System.out.println("--- Exception 1 for getting wronge service\n" +
         * e); // TODO Auto-generated catch block
         * System.out.println("--- StackTrace VlException---");
         * e.printStackTrace();// note: starts with "\n"...
         * System.out.println("--- StackTrace System Exception---");
         * e.printSystemStackTrace();// note: starts with "\n"...
         * 
         * }
         */

        // Copy non-existing file

        try
        {
            vfs.copy(vfs.getFile(new VRL("file:///tmp/doesnotexist")), vfs.getDir(new VRL("file:///tmp")));
        }
        catch (VlException e)
        {
            System.out.println("--- Exception 2 for copying non-existing file\n" + e);
            System.out.println("--- StackTrace VlException---");
            e.printStackTrace(); // note: starts with "\n"...
            System.out.println("--- StackTrace System Exception---\n" + VlException.getChainedStackTraceText(e));
        }
        // copy to non-existing dir
        try
        {

            vfs.copy(vfs.getFile(new VRL("file:///etc/hosts")), vfs.getDir(new VRL("file:///tmp/DoesNotExists123")));
        }
        catch (VlException e)
        {
            System.out.println("--- Exception 3 for copying to non-existing directory =\n" + e);
            System.out.println("--- StackTrace VlException---");
            e.printStackTrace();// note: starts with "\n"...
            System.out.println("--- StackTrace System Exception---" + VlException.getChainedStackTraceText(e));
        }

        // write permissions

        try
        {
            vfs.copy(vfs.getFile(new VRL("file:///etc/hosts")), vfs.getDir(new VRL("file:///root")));
        }
        catch (VlException e)
        {
            System.out.println("--- Exception 4: no write permission\n" + e);
            System.out.println("--- StackTrace VlException---");
            e.printStackTrace();
            System.out.println("--- StackTrace System Exception---" + VlException.getChainedStackTraceText(e));
        }

        // create dir in /root

        try
        {

            VDir rootDir = vfs.getDir(new VRL("file:///root"));
            rootDir.createDir("/root/hoi");
        }
        catch (VlException e)
        {
            System.out.println("--- Exception 6: no creation permission for Dir \n" + e);
            System.out.println("--- StackTrace VlException---");
            // e.printStackTrace();
            System.out.println("--- StackTrace System Exception---");
            // e.printSystemStackTrace();
        }
        // create file in /root

        try
        {

            VDir rootDir = vfs.getDir(new VRL("file:///root"));
            rootDir.createFile("hoi");
        }
        catch (VlException e)
        {
            System.out.println("--- Exception 7 no create permission for File \n" + e);
            // System.out.println("--- StackTrace VlException---");
            // e.printStackTrace();
            // System.out.println("--- StackTrace System Exception---");
            // e.printSystemStackTrace();
        }
    }

    static VRSContext vrs = new VRSContext();

    // Tester:
    public static void main(String[] args)
    {
        // VFSClient.init(); // Not needed due to static initialization

        // fs.setMaxChunkSize(4); // nice for testing;

        try
        {
            // VFS fs1 = VFSClient.getDriver("localfs");

            VRSFactory fs1 = (VRSFactory) vrs.getRegistry().getVRSFactoryForScheme(VRS.FILE_SCHEME);

            // fs1 =
            // (VFSFactory)vrs.getRegistry().getVRSFactory(VRS.FILE_SCHEME,null);

            VFSClient vfs = new VFSClient();

            /*
             * VFS fs2 = VFSClient.getDriverForHost("localhost");
             * 
             * if (fs1.getName().compareTo(fs2.getName()) == 0) { fs = fs1; }
             * else {
             * System.out.println("*** Ooopsy VFS driver not the same ***");
             * Main.exit(-1); fs = null; // To please eclipse, fs has to be
             * initialized ! }
             */

            // A VNode has not much methods which can be used
            String home = Global.getUserHome();
            VDir homeDir = (VDir) vfs.openLocation("file:///" + home);

            VDir d = homeDir.createDir("testVRS.LocalFS", true);

            VFile f = vfs.getFile(new VRL("file:///" + home + "/.vlamrc"));

            System.out.println("homeDir    =" + homeDir);
            System.out.println("dir        =" + d);
            System.out.println("dir parent =" + d.getParent());
            System.out.println("file=" + f);

            System.out.println("file parent=" + f.getParent());
            VAttribute[] attrs = f.getAttributes();

            for (int i = 0; i < attrs.length; i++)
                System.out.println("file attribute[" + i + "]=" + attrs[i]);

            VAttribute[] dattrs = d.getAttributes();

            for (int i = 0; i < dattrs.length; i++)
                System.out.println("dir attribute[" + i + "]=" + dattrs[i]);

            VNode cts[] = d.getNodes();

            for (int i = 0; (cts != null) && (i < cts.length); i++)
                System.out.println("dir contents[" + i + "]=" + cts[i]);

            // ToDo: getContentsAsString:

            String s = f.getContentsAsString(null); // read all

            System.out.println(">>> file contents=\n" + s);
            System.out.println("<<< file contents\n");

            // Test VlException

            d.createDir("testdir");

            VFile f1 = f.copyTo(d);

        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Now tests errors
        testErrors();
    }

}
