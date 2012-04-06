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
 * $Id: VServer.java,v 1.6 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */ 
// source: 

//package nl.uva.vlet.vrs;
//
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vrl.VRL;
//
///**
// * Generic VServer interface for Server implementations. 
// * VServer objects are stored in a VRSContext so that Connected servers are 
// * stored in a User depended context. 
// * <p>
// *  
// * @author P.T. de Boer
// */
//
//public interface VServer 
//{
////	/** Return hostname */ 
////	public String getHostname();
////	
////	/** Return port */ 
////	public int getPort(); 
////	
////	/** Return (default) scheme as protocol string */  
////	public String getScheme();
////	
////	/** Return identification String for this server */ 
////	public String getID(); 
////	
////	/** 
////	 * Returns VRL which identifies this server. 
////	 * Path part can be '/' or the default (home) path, 
////	 * for example sftp://user@remotehost.domain/localhome/
////	 */ 
////	public VRL getServerVRL();
////	
////	// === 
////	// Connection Methods 
////	// ===
////	/**
////	 * Returns if server is connected to remote server 
////	 */
////	public boolean isConnected();
////	
////	/** 
////	 * Connect to remote server. Open connection an 
////	 * keep connection alive  
////	 */
////	public void connect() throws VlException;
////	
////	/** 
////	 * Disconnect server object. 
////	 * Close open connections. After a disconnect a connect() 
////	 * may occur. 
////     */
////	public void disconnect() throws VlException; 
////	
////	
////	/** 
////	 * Get ServerInfo object associated with this server  */
////	public ServerInfo getServerInfo();
//	
//}
