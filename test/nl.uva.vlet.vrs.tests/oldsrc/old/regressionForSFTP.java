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
 * $Id: regressionForSFTP.java,v 1.3 2011-09-23 13:40:14 ptdeboer Exp $  
 * $Date: 2011-09-23 13:40:14 $
 */ 
// source: 

package old;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import test.junit.TestSettings;
import test.junit.vfs.testSFTP_elab;

/**
 * Regression Tests for bugs
 * 
 * Implementation of regressionForSFTP => Moved to testVFS
 * 
 * @author P.T. de Boer
 */

public class regressionForSFTP extends TestCase
{
    VDir sftpTestDir = null;

    static VFSClient vfs = new VFSClient();

    protected synchronized void setUp() throws VlException
    {
        if (sftpTestDir == null)
        {
            // authenticate for the SFTP test location:
            testSFTP_elab.authenticate();
            sftpTestDir = vfs.getDir(TestSettings.getTestLocation(TestSettings.VFS_SFTP_ELAB_LOCATION)); 
        }

    }

    /**
     * When writing to a file, last write must be 32k or else the write will not
     * complete...
     * 
     * @param targetSize
     * @throws VlException
     */
    public void testStreamWrite32KBug() throws VlException
    {
        testStreamWrite(32000); // this works
        testStreamWrite(1024 * 1024); // this won't
    }

    private void testStreamWrite(int targetSize) throws VlException
    {
        VFile file = sftpTestDir.createFile("streamWrite");
        // write 1MB buffer:
        byte[] buffer = new byte[targetSize];
        file.streamWrite(buffer, 0, buffer.length);

        long size = file.getLength();
        Assert.assertEquals("testing write > 32k bug: Size of file after streamWrite not correct:" + size, size,
                targetSize);
        file.delete();
    }

}
