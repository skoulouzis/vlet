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
 * $Id: regressionForSRB.java,v 1.2 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Regression Tests for bugs
 * 
 * Implementation of regressionForSFTP
 * 
 * => Moved to testVFS
 * 
 * @author P.T. de Boer
 */

public class regressionForSRB extends VTestCase
{
    VDir srbTestDir = null;

    static VFSClient vfs = new VFSClient();

    protected synchronized void setUp() throws VlException
    {
        staticCheckProxy();

        setVerbose(2);

        if (srbTestDir == null)
        {
            srbTestDir = vfs.getDir(TestSettings.test_srb_location);
        }
    }

    /**
     * Regression test for the Jargon API (not mine). The read() method returns
     * NEGATIVE values for bytes >128
     * 
     * => Moved to testVFS
     * 
     * @param targetSize
     * @throws VlException
     */
    public void testSingedStreamReadBug() throws VlException
    {
        int V1 = 0;
        int V2 = 1;
        int V3 = 129; // is negative byte
        int V4 = 130;

        VFile file = srbTestDir.createFile("streamWrite");
        // write 1MB buffer:
        byte[] buffer = new byte[32];
        buffer[0] = (byte) V1;
        buffer[1] = (byte) V2;
        buffer[2] = (byte) V3; // write negative value;
        buffer[3] = (byte) V4; // unsigned value of negative byte

        file.streamWrite(buffer, 0, buffer.length);

        InputStream inps = file.getInputStream();

        byte test = (byte) 129;
        int intTest;

        intTest = test < 0 ? (256 + test) : test;
        // Test some statements which are used in the Jargon implementation:
        verbose(2, "singed byte value of '129' to unsigned int (I) =" + intTest);
        verbose(2, "singed byte value of '129' to long (II)        =" + ((long) test & 0x00FF));
        verbose(2, "singed byte value of '129' <<24 to long        =" + (((long) test & 0x00FF) << 24));
        verbose(2, "singed byte value of '129' <<24 to int         =" + (int) (((long) test & 0x00FF) << 24));

        try
        {
            // SRBInputStream returns NEGATIVE values when reading single byte
            // values > 127.
            // the return type, however, is int, thus values > 127 must be
            // returned.

            int W1 = inps.read();
            int W2 = inps.read();
            int W3 = inps.read();
            int W4 = inps.read();

            Assert.assertEquals("Unsinged read test: Value of byte 1 incorrect:" + W1, V1, W1);
            Assert.assertEquals("Unsinged read test: Value of byte 2 incorrect:" + W2, V2, W2);
            Assert.assertEquals("Unsinged read test: Value of byte 3 incorrect:" + W3, V3, W3);
            Assert.assertEquals("Unsinged read test: Value of byte 4 incorrect:" + W4, V4, W4);
        }
        catch (IOException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

}
