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
 * $Id: HTTPFactory.java,v 1.10 2011-06-21 10:35:11 ptdeboer Exp $  
 * $Date: 2011-06-21 10:35:11 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.http;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.net.ssl.SslUtil;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;
import nl.uva.vlet.vrs.VResourceSystem;

public class HTTPFactory extends VRSFactory
{
	// static code ! 
	
	static
	{
	    // HTTPS/SSL verifier
	    SslUtil.init(); // initSslHostnameVerifier(); 
	    //SslUtil.setMySslValidation();
	    //SslUtil.setNoSslValidation();
	}
	
    String schemes[]={VRS.HTTP_SCHEME,VRS.HTTPS_SCHEME,"httpg"}; 

    @Override
    public String getName()
    {
        return "HTTP";
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes; 
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public void clear()
    {
    }

	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context, ServerInfo info,VRL location)
			throws VlException 
	{
		return HTTPRS.getClientFor(context,info,location); 
	}

	public String getVersion()
    {
	    return super.getVersion();   
    }
	
	public String getAbout()
	{
	    return super.getAbout();   
    }

}
