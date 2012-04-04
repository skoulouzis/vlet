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
 * $Id: testGAT_GFTP_V1_0.java,v 1.2 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.vfs;


import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
/**
 * Test SRB case 
 * 
 * TestSuite uses testVFS class to tests SRB implementation. 
 * 
 * @author P.T. de Boer
 */

public class testGAT_GFTP_V1_0
{

    public static final String GFTP_MU2_LOCATION="gat.gsiftp://mu2.matrix.sara.nl//home/pvier006/testVFS"; 
    
      public static class testVFS_GFTP extends testVFS
      {
          @Override
          public VRL getRemoteLocation()
          {
              try
            {
                return new VRL(GFTP_MU2_LOCATION);
            }
            catch (VlException e)
            {
                System.out.println("***Error: Exception:"+e); 
                e.printStackTrace();
            } 
            
            return null;
            
          }
      }
    
    
      public static Test suite()
      {
          testVFS.staticCheckProxy();
          
          Global.setSystemProperty("gat.adaptor.path","/home/ptdeboer/workspace/mbuild/dist/lib/auxlib/javagat/adaptors"); 
          return new TestSuite(testVFS_GFTP.class);
      }

      public static void main(String args[]) 
      {

        junit.textui.TestRunner.run(suite());

      }

}

