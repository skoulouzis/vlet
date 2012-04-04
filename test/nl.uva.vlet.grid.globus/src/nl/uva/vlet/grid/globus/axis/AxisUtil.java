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
 * $Id: AxisUtil.java,v 1.5 2011-05-03 15:02:47 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:47 $
 */ 
// source: 

package nl.uva.vlet.grid.globus.axis;

//
//import java.io.InputStream;
//
//import nl.uva.vlet.Global;
//
//import org.apache.axis.AxisEngine;
//import org.apache.axis.EngineConfiguration;
//import org.apache.axis.SimpleTargetedChain;
//import org.apache.axis.client.AxisClient;	//$NON-NLS-1$
//import org.apache.axis.configuration.FileProvider;
//import org.apache.axis.configuration.SimpleProvider;
//import org.apache.axis.transport.http.HTTPSender;
//
///** 
// * Axis Engine Util. 
// * Needed to get different configured Axis Engines to be able 
// * to talk to both grid (globus) and non grid web services. 
// * 
// * @author P.T. de Boer 
// */
//
//public class AxisUtil
//{
//	
//	/** Client Configuration for gt4.x compatible grid services */
//	public static final String GT4_CLIENT_CONFIG_FILE="gt4_1_client-config.wsdd";
//	
//	private static AxisEngine ogsaAxisEngine=null; 
//
//	/**	//$NON-NLS-1$
//	 * Static method to retrieve GT4 configured Axis Engine. 
//	 * This method used the Axis Engine from GlobusDelegation
//	 * which is configured with a gt4.x compatible client-config.wsdd file. 
//	 *  
//	 * @return
//	 */
//	
//	public static AxisEngine getGT4_AxisEngine()
//	{
//		return GlobusDelegation.getAxisEngine(); 
//	}
//	/**
//	 * Return GT4 Axis Engine configuration which can be used to inialize a Service Locator. 
//	 * For Example: <br>
//	 * <pre>
//	 * EngineConfiguration engineConfig=AxisUtil.getGT4EngineConfig(); 
//	 * DelegationFactoryServiceLocator delegationFactoryServicelocator =  new DelegationFactoryServiceLocator(engineConfig);
//	 * </pre>
//	 * <br> 
//	 * @return
//	 */
//	public static EngineConfiguration getGT4EngineConfig()
//	{
//		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(GT4_CLIENT_CONFIG_FILE);
//		
//		if (is==null)
//		{
//			Global.errorPrintln(GlobusDelegation.class, "Couldn't read:"+GT4_CLIENT_CONFIG_FILE);
//			return null;
//		}
//		
//		Global.debugPrintln(AxisEngine.class, "useing: "+GT4_CLIENT_CONFIG_FILE);
//		
//		EngineConfiguration engineConfig = new FileProvider(is);
//		
//		return engineConfig; 
//	}
//	
////	/** Creates new OGSA compatible Axis Engine */ 
////	public static AxisEngine createOGSA_AxisEngine()
////	{
////		SimpleProvider provider = new SimpleProvider();
////		SimpleTargetedChain chain = new SimpleTargetedChain( new HTTPSSender() );
////		provider.deployTransport( "https", chain ); 
////		chain = new SimpleTargetedChain( new HTTPSender() );
////		provider.deployTransport( "http", chain ); 
////		chain = new SimpleTargetedChain( new GSIHTTPSender() );
////		provider.deployTransport( "httpg", chain ); 
////
////		AxisEngine axisClient = new AxisClient(provider);
////
////		return axisClient; 
////	}
//	
////	/** returns default Global OGSA Compatible Axis Engine */ 
////	public static synchronized AxisEngine getOGSA_AxisEngine()
////	{
////		if (ogsaAxisEngine==null)
////		{
////			ogsaAxisEngine=createOGSA_AxisEngine(); 
////		}
////		
////		return ogsaAxisEngine; 
////	}
//	
//	
//	
//}
