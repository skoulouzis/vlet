package voms;

import org.globus.gsi.GlobusCredential;

import nl.uva.vlet.grid.voms.VomsUtil;
import nl.uva.vlet.util.cog.GridProxy;
import nl.vlet.uva.grid.globus.GlobusUtil;

public class ParseProxy 
{
	public static void main(String args[])
	{
		GlobusUtil.init(); 
		
		GridProxy proxy=GridProxy.getDefault(); 
		
		GlobusCredential cred = GlobusUtil.getGlobusCredential(proxy); 
		
		try 
		{
			String log=VomsUtil.parse(cred.getCertificateChain());
			System.out.printf("--- VomsUtils Parse Log ---\n%s-----\n",log);
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
			
		  
		
	}
}
