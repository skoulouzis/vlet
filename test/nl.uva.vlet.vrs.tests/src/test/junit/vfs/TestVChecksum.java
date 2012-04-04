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
 * $Id: TestVChecksum.java,v 1.8 2011-11-25 13:44:48 ptdeboer Exp $  
 * $Date: 2011-11-25 13:44:48 $
 */ 
// source: 

package test.junit.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.ChecksumUtil;
import nl.uva.vlet.vdriver.vfs.localfs.LFile;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Tests VCommentable files
 * 
 * @author S. Koulouzis
 */
public class TestVChecksum extends VTestCase
{
    private VDir remoteTestDir = null;

    private VFile remoteFile = null;

    private VDir localTempDir;

    static
    {
        Global.getLogger().setLevelToInfo();
    }

    protected synchronized void setUp()
    {

        // if (remoteTestDir1 == null)
        // {
        try
        {

            // VDir localTestDir1 =
            // getVFS().mkdirs(TestSettings.localTempDirLocation, true);
            // localFile = localTestDir1.createFile("DLELETE_ME_TestFile");
            //                
            //                
            // byte[] data = new byte[1024*1024];
            // Random r= new Random();
            //                
            // r.nextBytes(data);
            // int size=100;
            //                
            // OutputStream out = localFile.getOutputStream();
            // for(int i=0;i<size;i++){
            // out.write(data);
            // }
            //                                
            // out.flush();
            // out.close();
            //                
            //                
            //                
            // remoteTestDir =
            // getVFS().mkdirs(TestSettings.test_gftp_location2,true);

            // remoteTestDir =
            // getVFS().mkdirs(TestSettings.testSRMNikhefLocation, true);

            remoteTestDir = getVFS().mkdirs(TestSettings.getTestLocation(TestSettings.VFS_SRM_DCACHE_SARA_LOCATION), true);

            remoteFile = remoteTestDir.createFile("DLELETE_ME_TestFile.txt");

            localTempDir = getVFS().mkdirs(TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION), true);
            // remoteFile = localFile.copyToDir(remoteTestDir1.getVRL());
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }
        // }
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {
        // try
        // {
        // remoteTestDir1.delete();
        // }
        // catch (VlException e)
        // {
        // e.printStackTrace();
        // }
    }

    public void testVChecksum() throws VlException
    {
        VFile remoteFile = getRemoteTestDir().createFile("testChecksum.txt");

        remoteFile.setContents("Test contents");

        if (remoteFile instanceof VChecksum)
        {

            VChecksum checksumRemoteFile = (VChecksum) remoteFile;
            String[] types = checksumRemoteFile.getChecksumTypes();
            InputStream remoteIn = null;
            String calculated;
            String feached;
            for (int i = 0; i < types.length; i++)
            {
                message("Testing: " + types[i]);
                // get the same checksum from remote file and inputstream
                remoteIn = remoteFile.getInputStream();
                calculated = ChecksumUtil.calculateChecksum(remoteIn, types[i]);
                feached = checksumRemoteFile.getChecksum(types[i]);
                message("-----------------calculated Checksum: " + calculated + " feached Checksum: " + feached);
                assertEquals(calculated, feached);

                // now change the file and check if the checksum has also
                // chanded
                String initialChecksum = calculated;
                remoteFile.setContents("Changed contents");
                remoteFile = getRemoteTestDir().getFile("testChecksum.txt");

                feached = checksumRemoteFile.getChecksum(types[i]);
                assertNotSame(feached, initialChecksum);

                // download the file and compare it aginst the remote
                VFile result = remoteFile.copyTo(localTempDir);
                LFile localFile = (LFile) result;

                if (localFile instanceof VChecksum)
                {
                    VChecksum checksumLocalFile = (VChecksum) localFile;
                    checksumRemoteFile = (VChecksum) remoteFile;

                    message("Getting " + types[i] + " from local file");
                    String localChecksum = checksumLocalFile.getChecksum(types[i]);
                    message("Getting " + types[i] + " from remote file");
                    String remoteChecksum = checksumRemoteFile.getChecksum(types[i]);

                    message("localChecksum: " + localChecksum + " remoteChecksum: " + remoteChecksum);
                    assertEquals(localChecksum, remoteChecksum);
                }
                // Check exception
                try
                {
                    String remoteChecksum = checksumRemoteFile.getChecksum("NON EXISTING ALGORITHM");

                    message("Checksum: " + remoteChecksum);
                }
                catch (Exception ex)
                {
                    if (!(ex instanceof nl.uva.vlet.exception.NotImplementedException))
                    {
                        fail("Should throw NotImplementedException. Instead got back " + ex.getMessage());
                    }
                    else
                    {
                        message("Correct exeption!!: " + ex.getMessage());
                    }
                }
                localFile.delete();
            }
        }

    }

    public VDir getRemoteTestDir()
    {
        return remoteTestDir;
    }

    private void writeRandomData(VFile file, int size)
    {
        byte[] data = new byte[1024 * 1024];
        Random r = new Random();

        r.nextBytes(data);

        OutputStream out;
        try
        {
            out = file.getOutputStream();
            for (int i = 0; i < size; i++)
            {
                out.write(data);
            }

            out.flush();
            out.close();
        }
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
