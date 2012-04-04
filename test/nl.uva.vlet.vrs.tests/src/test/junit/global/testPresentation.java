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
 * $Id: testPresentation.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.global;

import java.util.Date;

import junit.framework.Assert;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vfs.VFSClient;
import test.junit.VTestCase;

/**
 * Experimental Unit Class Tester
 * 
 * @author P.T. de Boer
 */
public class testPresentation extends VTestCase
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

    public void testCurrent()
    {
        testTimes(System.currentTimeMillis());
        testTimes((long) 1);
        // epoch !
        testTimes((long) 0);
        // time unkown ! (null pointers etc.)
        testTimes((long) -1);
    }

    public void testTimes(long millis)
    {
        // Date millis2date=Presentation.createDate(millis);

        // === Normalized Time ===
        // millis -> Normalized Time -> millis
        String normalizedTime = Presentation.createNormalizedDateTimeString(millis);
        message("time string=" + normalizedTime);

        long newMillis = Presentation.createMillisFromNormalizedDateTimeString(normalizedTime);

        // check Minutes/seconds
        Assert.assertEquals("Normalized Time: recreated minutes not the same!", millis / 60000, newMillis / 60000);
        Assert.assertEquals("Normalized Time: recreated seconds not the same!", millis / 1000, newMillis / 1000);
        // check millies:
        Assert.assertEquals("Normalized Time: recreated millies not the same!", millis, newMillis);

        // === Date Object ===
        // millis -> Date -> millis
        Date date = Presentation.createDate(millis);

        if (date == null)
            newMillis = -1;
        else
            newMillis = date.getTime();

        // check Minutes/seconds
        Assert.assertEquals("Normalized Time: recreated minutes not the same!", millis / 60000, newMillis / 60000);
        Assert.assertEquals("Normalized Time: recreated seconds not the same!", millis / 1000, newMillis / 1000);
        // check millies:
        Assert.assertEquals("recreated millies not the same!", millis, newMillis);

    }

}
