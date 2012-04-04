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
 * $Id: TestVCommentable.java,v 1.6 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import java.util.Random;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrs.VCommentable;
import test.junit.TestSettings;
import test.junit.VTestCase;

/**
 * Tests VCommentable files
 * 
 * @author S. Koulouzis
 */
public class TestVCommentable extends VTestCase
{
    VDir remoteTestDir1 = null;

    static
    {
        Global.getLogger().setLevelToInfo();
    }

    protected synchronized void setUp()
    {

        try
        {
            remoteTestDir1 = getVFS().mkdirs(TestSettings.test_vCommentable_SaraLocation, true);

            // remoteTestDir1 =
            // getVFS().getDir(TestSettings.test_vCommentable_SaraLocation);
        }
        catch (VlException e)
        {
            e.printStackTrace();
        }

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

    public void testSetGetComments() throws VlException
    {

        VFile file = remoteTestDir1.createFile("testFile");

        // VFile file = remoteTestDir1.getFile("testFile");

        if (file instanceof VCommentable)
        {

            VCommentable commentableFile = (VCommentable) file;

            Random r = new Random();
            String expected = "test VCommentable " + r.nextInt();

            message("Setting: " + expected);
            commentableFile.setComment(expected);

            String actual = commentableFile.getComment();

            message("Got back: " + actual);

            assertEquals(expected, actual);

            file.delete();

            try
            {
                // getting comments from non-existing file
                actual = commentableFile.getComment();
            }
            catch (Exception ex)
            {
                if (!(ex instanceof ResourceNotFoundException))
                {
                    fail("Didn't caught expected Exception:" + ex);
                }
                else
                {
                    Global.debugPrintln(this, "Caught expected Exception:" + ex);
                    Global.debugPrintStacktrace(ex);
                }
            }
        }
        // file.delete();
    }
}
