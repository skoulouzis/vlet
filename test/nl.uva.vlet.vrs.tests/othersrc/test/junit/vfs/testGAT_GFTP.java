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
 * $Id: testGAT_GFTP.java,v 1.2 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.vfs;


import test.junit.TestSettings;
import junit.framework.Test;
import junit.framework.TestSuite;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.GlobalLogger;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.vfs.gatvfs.*;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.Registry;
import nl.uva.vlet.vrs.VRS;
/**
 * Test SRB case 
 * 
 * TestSuite uses testVFS class to tests SRB implementation. 
 * 
 * @author P.T. de Boer
 */

public class testGAT_GFTP extends testVFS 
{
	@Override
	public VRL getRemoteLocation()
	{
		try
		{
			return new VRL("gat."+TestSettings.test_gftp_location);
		}
		catch (VlURISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
    
	public static Test suite()
	{
		// Pre Init configuration ! 
        GlobalConfig.setSystemProperty("gat.adaptor.path","/home/ptdeboer/workspace/vfs.gatvfs/dist/nl.uva.vlet.vfs.gatvfs/javagat/lib/adaptors/"); 
        Global.getLogger().setVerbose(GlobalLogger.VERBOSE_INFO);
        Global.init();   
       
		try
		{
			VRS.getRegistry().addVRSDriverClass(GatFactory.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		
		testVFS.staticCheckProxy();
 
		return new TestSuite(testGAT_GFTP.class);
	}

	public static void main(String args[]) 
	{
		junit.textui.TestRunner.run(suite());
	}	

}

