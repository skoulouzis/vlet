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
 * $Id: VOServer.java,v 1.2 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.grid.voms;


/** VO Server information Holders */ 
public class VOServer
{
	public String host=null;  
	public int port=0; 
	public String certFile=null;
	public String hostDN=null;
	
	public VOServer(String hostv,int portv,String certf,String hostDNv)
	{
		this.host=hostv;
		this.port=portv;
		this.certFile=certf; 
		this.hostDN=hostDNv; 
	}
	
	public String toString()
	{
		return "{"+host+":"+port+","+hostDN+"}"; 
	}
	
	public String getHostname()
	{
		return host; 
	}
	
	public int getPort()
	{
		return port; 
	}
	
	//public java.net.URI getServerURI()
	//{
	//	return new java.net.URI("https://"+host+":"+port+"/"); 
	//}
	
	/** Static List factory method to create list from one server definition */ 
	public static VOServer[] createList(String hostv, int portv, String certf,String hostDNv)
	{
		VOServer servers[]=new VOServer[1];
		servers[0]=new VOServer(hostv,portv,certf,hostDNv); 
		return servers;
	}
}

	
