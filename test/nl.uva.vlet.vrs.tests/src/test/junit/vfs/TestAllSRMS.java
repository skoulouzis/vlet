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
 * $Id: TestAllSRMS.java,v 1.4 2011-05-02 13:28:47 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:47 $
 */ 
// source: 

package test.junit.vfs;

import java.lang.reflect.Method;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.vrs.VRSContext;

/**
 * TestSuite for all SRM locations aveliable for your VO.
 * 
 * Warning!!! This might create TOO MANY TESTS
 * 
 * @author S. Koulouzis
 */
public class TestAllSRMS
{
    
    private static ArrayList<testVFS> allTests = new ArrayList<testVFS>();

    public static Test suite() throws VlException
    {
        testVFS.staticCheckProxy();

        VRSContext context = VRSContext.getDefault();

        ArrayList<StorageArea> Sas = context.getBdiiService().getSRMv22SAsforVO(context.getVO());

        TestSuite suite = new TestSuite();
        boolean encodePaths = false;
        boolean bigTest = false;
        boolean rename = true;
        boolean write = true;

        Method[] allMethods = testVFS.class.getMethods();
        testVFS[] tests = new testVFS[allMethods.length];
        for (int i = 0; i < Sas.size(); i++)
        {

            for (int j = 0; j < allMethods.length; j++)
            {
                
                if (allMethods[j].getName().startsWith("test") && allMethods[j].getParameterTypes().length ==0)
                {
                    tests[j] = new testVFS();
                    tests[j].setName(allMethods[j].getName());
                    tests[j].setRemoteLocation(Sas.get(i).getVOStorageLocation().append(
                            Global.getUsername() + "/test_SRM"));
                    int seIndex = new java.util.Random().nextInt(Sas.size()-1);
                    tests[j].setOtherRemoteLocation(Sas.get( seIndex ).getVOStorageLocation().append(
                            Global.getUsername() + "/test_SRM"));
                    testVFS.setVerbose(2);
                    tests[j].setTestEncodedPaths(encodePaths);
                    tests[j].setTestDoBigTests(bigTest);
                    tests[j].setTestRenames(rename);
                    tests[j].setTestWriteTests(write);
                    allTests.add(tests[j]);
                    suite.addTest(tests[j]);
                }
            }
        }
        
        
                
        return suite;
    }

}
