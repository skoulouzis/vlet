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
 * $Id: VomsTest.java,v 1.1 2010-06-09 14:29:22 ptdeboer Exp $  
 * $Date: 2010-06-09 14:29:22 $
 */ 
// source: 

package voms;

import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Set;

import org.globus.gsi.GlobusCredential;


import nl.uva.vlet.Global;
import nl.uva.vlet.grid.voms.VO;
import nl.uva.vlet.grid.voms.VomsProxyCredential;
import nl.uva.vlet.grid.voms.VomsUtil;
import nl.uva.vlet.grid.voms.VomsProxyCredential.VomsInfo;
import nl.uva.vlet.util.cog.GridProxy;
import nl.vlet.uva.grid.globus.GlobusUtil;

public class VomsTest
{
	public static void main(String args[])
	{
		try
		{
			
		Global.init(); 
		
		GridProxy prox = GridProxy.getDefault();
		Global.setDebug(true); 
		
		if (prox.isValid()==false)
		{
			Error("proxy not valid:create please");
			return;
		}
		
		GlobusCredential globusCred = GlobusUtil.getGlobusCredential(prox); 
		
		//String vourl="https://voms.grid.sara.nl:8443/voms/vlemed/webui/admin";
		// pvierVO=VomsUtil.getVO("vlemed");  	
		 VO pvierVO=nl.uva.vlet.grid.voms.VomsUtil.getVO("pvier"); // VlePvierVO();
		 
		 if (pvierVO==null) 
		 {
			 System.err.println("***Errpr: Couldn't get VO:pvier");
			 return; 
		 }
 
		 
		 long lifetime=prox.getTimeLeft(); 
		 
		VomsProxyCredential vomscred = VomsUtil.createVomsCredential(globusCred,pvierVO,lifetime); 

		VomsInfo info = vomscred.getVomsInfo(); 
		
		if (info!=null)
		{
			for (Enumeration<String> keys = info.keys(); keys.hasMoreElements();)
			{
				String key=keys.nextElement();
				Message("vomsinfo."+key+"="+info.get(key));
			}
		}
		else
		{
			Error("NULL voms info:");
		}

		
		GlobusCredential vomsProx = vomscred.getVomsProxy();
		Message("New voms proxy ID="+ vomsProx.getIdentity());
		
		Message("voms proxy identity ="+vomsProx.getIdentity());
		
		// update: 
		prox.setGlobusCredential(vomsProx); 
		prox.saveProxyTo("/tmp/x509_vomsprox"); 
		
		inspect(prox); 
		

		/*
		AttributeCertificate attrCert = vomscred.getAttributeCertificate();
		AttributeCertificateInfo attrInfo = attrCert.getAcinfo(); 
		
		ASN1Sequence attrs = attrInfo.getAttributes();
		
		for (int i=0;i<attrs.size();i++)
		{
			Object obj=attrs.getObjectAt(i); 
			System.out.println("-"+i+"=<"+obj.getClass()+">="+obj);
			
			if (obj instanceof ASN1Sequence)
			{
				ASN1Sequence aseq = (ASN1Sequence)obj; 
				for (int j=0;j<aseq.size();j++)
				{
					DEREncodable obj2 = aseq.getObjectAt(j);
				
					System.out.println("- - "+j+" = "+ obj2);
				}
			}
		}*/
		
		}
		catch(Exception e)
		{
			Error("Exception:"+e);
			Error("--- stacktrace ---");
			e.printStackTrace();
		}
	
	}

	private static void inspect(GridProxy prox)
	{
		GlobusCredential glob = GlobusUtil.getGlobusCredential(prox);  
		X509Certificate cert = glob.getIdentityCertificate();
		
		Set<String> extSet = cert.getCriticalExtensionOIDs(); 
		
		for (String ext:extSet)
		{
			byte[] val = cert.getExtensionValue(ext);
			System.out.println("ext="+ext+" = "+new String(val));
			
		}
		
	}

	private static void Error(String str)
	{
		Global.errorPrintf(VomsTest.class,"%s\n", str);
		
	}

	private static void Message(String str)
	{
		Global.infoPrintf(VomsTest.class,"%s\n",str);
		
	}
}
