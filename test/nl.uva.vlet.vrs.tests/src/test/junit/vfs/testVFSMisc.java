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
 * $Id: testVFSMisc.java,v 1.3 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import junit.framework.Assert;
import nl.uva.vlet.data.BooleanHolder;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSClient;
import test.junit.VTestCase;

/**
 * Experimental Unit Class Tester
 * 
 * @author P.T. de Boer
 */
public class testVFSMisc extends VTestCase
{
    // VAttribute attribute=null;

    private VFSClient vfsClient = null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected synchronized void setUp()
    {
        if (vfsClient == null)
            vfsClient = new VFSClient();
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    /** Check unix mode to string and back */
    public void testUnixModes() throws VlException
    {
        BooleanHolder isDir = new BooleanHolder(), isLink = new BooleanHolder();

        String str = "-rwxrwxrwx";
        int mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 0777);

        str = "-rwxr-xr--";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 0754);

        str = "-rwxrwxrw-";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 0776);

        str = "-rwxrwxrwt";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 01777);

        str = "-rwxrwxrwT";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 01776);

        str = "-rwsrwsrwt";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 07777);

        str = "-rwSrwSrwT";
        mode = VFS.parseUnixPermissions(str, isDir, isLink);
        Assert.assertEquals("Parsed unix mode doesn' matched.", mode, 07666);

    }

    /** Check unix mode to string and back */
    public void testUnixModeFile() throws VlException
    {
        for (int o = 0000; o <= 07777; o++)
        {
            BooleanHolder isDir = new BooleanHolder(), isLink = new BooleanHolder();

            String permstr = VFS.modeToString(o, false);
            int mode = VFS.parseUnixPermissions(permstr, isDir, isLink);

            String octStr = Integer.toOctalString(o);
            debug("octal mode=" + octStr + ",unix mode string=" + permstr);

            Assert.assertEquals("Unix mode to string and back doesn't match.", mode, o);

        }

    }

    /** Check unix mode to string and back */
    public void testUnixModeDir() throws VlException
    {
        for (int o = 0000; o <= 07777; o++)
        {
            BooleanHolder isDir = new BooleanHolder(), isLink = new BooleanHolder();

            String permstr = VFS.modeToString(o, true);
            int mode = VFS.parseUnixPermissions(permstr, isDir, isLink);

            String octStr = Integer.toOctalString(o);
            debug("octal mode=" + octStr + ",unix mode string=" + permstr);

            Assert.assertEquals("Unix mode to string and back doesn't match.", mode, o);
            Assert.assertTrue("isDir must be true for:" + permstr, isDir.value);

        }

    }

}
