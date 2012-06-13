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
 * $Id: VO.java,v 1.3 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.grid.voms;

import nl.uva.vlet.vrl.VRL;

/** 
 * VO Information Holder class. 
 */
public class VO
{
	private String voName = null;
	
	private VOServer voServers[]=null; 
	
	/*private String hosts[] = null;
	private int ports[] = null; 
	private String certs[]=null;
	private String hostDNs[] = null;*/
	
	private String admin;
	
	private String vomsXmlFile;
	
	/** Optional VO Roles */ 
	private String[] voRoles; 
	// P.T. de Boer not needed ? 
	//private String voms_url = null;

    /**
     * @param voName the name of the VO
     * @param host the host of the VOMS/VOMRS server of this VO
     * @param port the port of this VO on the VOMS server
     * @param hostDN the host dn
     */
    public VO (String voName, String host, int port, String hostDN)
    {
        init(voName,null,host,port,hostDN); 
    }
    
    /**
     * @param voName the name of the VO
     * @param voRoles optional roles for this VO 
     * @param host the host of the VOMS/VOMRS server of this VO
     * @param port the port of this VO on the VOMS server
     * @param hostDN the host dn
     */
    public VO (String voName, String voRoles[], String host, int port, String hostDN)
    {
        init(voName,voRoles,host,port,hostDN); 
    }

	protected VO(String name)
	{
		this.voName=name; 
	}

	private void init(String voName,String roles[], String host, int port, String hostDN)
	{
		this.voName=voName; 
		this.voRoles=roles; 
		this.voServers=VOServer.createList(host,port,null,hostDN); 
	}
	
	/** Returns first server name for this VO */ 
	public String getDefaultHost()
	{
		if ((this.voServers==null) || (this.voServers.length<1)) 
			return null; 
			 
		return this.voServers[0].host;  
	}
	
	public String getDefaultHostDN() 
	{
		if ((this.voServers==null) || (this.voServers.length<1)) 
			return null; 
			 
		return this.voServers[0].hostDN;   
	}

	public int getDefaultPort()
	{		
		if ((this.voServers==null) || (this.voServers.length<1)) 
			return -1;  
	 
		return this.voServers[0].port;    

	}

	public String getVoName()
	{
		return voName;
	}
	
	/**
	 * Returns path to voms.xml file if used to create this VO.
	 * Could be null.   
	 */ 
	public String getVomsXMFile()
	{
		return this.vomsXmlFile; 
	}
	
	/** 
	 * Returns path to parent directory of voms.xml file
	 * if used to create this VO. Could be null. 
	 * This method assumed that the parent directory of voms.xml 
	 * is also the 'vomsdir'. 
	 */ 
	public String getVomsDir()
	{
		if (this.vomsXmlFile==null)
			return null;
		
		return VRL.dirname(this.vomsXmlFile);  
	}

	public String toString() 
	{
		String vostr="{<VO>"+getVoName(); 
		for (VOServer serv:voServers)
		{
			vostr+=serv.toString(); 
		}
		vostr+="}";
		return vostr; 

	}
	
	/** Get VO Server list for this VO */ 
	public VOServer[] getServers()
	{
		return this.voServers; 
	}
	
	/** Set VO Server list for this VO */ 
	public void setServers(VOServer servers[])
	{
		this.voServers=servers; 
	}

    public void setAdmin(String adminstr)
    {
        this.admin=adminstr; 
    }

    public String getAdmin()
    {
        return admin; 
    }
    
    public void setVOMSXmlFile(String vomsFilename)
    {
        this.vomsXmlFile=vomsFilename; 
    }

    /** Optional roles if defined for this VO */ 
    public String[] getRoles()
    {
        return this.voRoles; 
    }

}
