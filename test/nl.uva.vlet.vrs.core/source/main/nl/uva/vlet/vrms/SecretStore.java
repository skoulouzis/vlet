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
 * $Id: SecretStore.java,v 1.3 2011-04-18 12:00:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:36 $
 */ 
// source: 

package nl.uva.vlet.vrms;

import java.util.Hashtable;
import java.util.Map;



/**
 * SecretStore which contains passwords and passphrases.
 */ 
public class SecretStore 
{
	public class Secret
	{
		// todo: secure strings ? 
		private String passWord=null;  
		private String passPhrase=null;
		private boolean validated=true;  
		
		public Secret(String word, String phrase)
		{
			this.passWord=word;
			this.passPhrase=phrase;
		}

		public void setPassword(String passstr)
		{
			passWord=new String(passstr);   
		}

		public void setPassphrase(String passstr)
		{
			passPhrase=passstr; 
		}
		
		public String getPassword()
		{
			return passWord; 
		}
		
		public String getPassphrase()
		{
			return passPhrase; 
		}
		
		public void setValidated(boolean val)
		{
			this.validated=val; 
		}

		public boolean getValidated()
		{
			return this.validated; 
		}

	}
	
	public static String createID(String scheme,String user,String host,int port)
	{
		return "key-"+scheme+"-"+user+"-"+host+"-"+port; 
	}
	
	private Map<String,Secret> secrets=new Hashtable<String,Secret>(); 
	
	
	public String getPassword(String scheme,String user,String host,int port)
	{
		Secret sec=secrets.get(createID(scheme,user,host,port));
		if (sec!=null)
			return sec.getPassword();
		
		return null;
	}
	
	public String getPassphrase(String scheme,String user,String host,int port)
	{
		Secret sec=secrets.get(createID(scheme,user,host,port));
		if (sec!=null)
			return sec.getPassphrase();
		
		return null; 
	}
	
	public void storePassword(String scheme,String user,String host,int port,String passtr)
	{
		Secret sec=new Secret(passtr,null);
		String id=createID(scheme,user,host,port); 
		
		secrets.put(id,sec); 
	}
	
	public void storePassphrase(String scheme,String user,String host,int port,String passphrase)
	{
		Secret sec=new Secret(null,passphrase);
		String id=createID(scheme,user,host,port); 
		
		secrets.put(id,sec); 
	}

	public void setIsValid(String scheme,String user,String host,int port,boolean val)
	{
		Secret sec=getSecret(scheme,user,host,port); 
		if (sec!=null)
		{
			sec.setValidated(val); 
		}
	}

	public Secret getSecret(String scheme,String user, String host,int port)
	{
		String id=createID(scheme,user,host,port); 
		//System.err.println("SecretStore:has secret for"+id+"="+(secrets.get(id)!=null));
		return secrets.get(id); 
	}
}
