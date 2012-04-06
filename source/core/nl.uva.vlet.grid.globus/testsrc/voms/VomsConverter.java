/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: VomsConverter.java,v 1.1 2010-06-09 14:29:22 ptdeboer Exp $  
 * $Date: 2010-06-09 14:29:22 $
 */ 
// source: 

package voms;


import java.util.Enumeration;

import nl.uva.vlet.grid.voms.VO;
import nl.uva.vlet.grid.voms.VomsProxyCredential;
import nl.uva.vlet.grid.voms.VomsProxyCredential.VomsInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.vlet.uva.grid.globus.GlobusUtil;

import org.globus.gsi.GlobusCredential;



public class VomsConverter
{

	private String vomsHost = "voms.grid.sara.nl";

	private int port = 30000;

	private String vomsDN = "/O=dutchgrid/O=hosts/OU=sara.nl/CN=voms.grid.sara.nl";

	private String command = "G/";

	private int lifetime_in_hours;

	public void setVomsHost(String host)
	{
		vomsHost = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setVomsDN(String vomsDN)
	{
		this.vomsDN = vomsDN;
	}

	public void setLifetime_in_hours(int lifetime)
	{
		this.lifetime_in_hours = lifetime;

	}

	public void convert(GlobusCredential cred, String voname)
	{
		command = command + voname;
		VO vo = new VO(voname, vomsHost, port, vomsDN);
		try
		{
			VomsProxyCredential vomsCred = new VomsProxyCredential(cred, vo,
					command, lifetime_in_hours);
			
			VomsInfo info = vomsCred.getVomsInfo(); 
			
			if (info!=null)
			{
				for (Enumeration<String> keys = info.keys(); keys.hasMoreElements();)
				{
					String key=keys.nextElement();
					System.out.println("vomsinfo."+key+"="+info.get(key));
				}
			}
			else
			{
				System.err.println("NULL voms info:");
			}
			
			GlobusCredential vomsProx = vomsCred.getVomsProxy();
			
			//Debug("New coms proxy ID="+ vomsProx.getIdentity(); 
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String args[])
	{
		GlobusCredential glob = GlobusUtil.getGlobusCredential(GridProxy.getDefault()); 
		
		VomsConverter conv=new VomsConverter(); 
		conv.convert(glob,"pvier"); 
		
		
	}
}
