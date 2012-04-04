package org.glite.lfc.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import org.glite.lfc.LFCException;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.FileDesc;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;


public class LfcCommand
{
	// === Static Class Stuff === 
	
	public static final String PROXY_OPT="-proxy="; 
	
	public static boolean verbose=false; 
	
	public static boolean debug=true; 
	
	// ===
	// Command Instance 
	// ===
	
	/** Full URI of LFC host, port and path */  
	public URI lfcUri=null;
	
	public boolean longList=false;
	
	public boolean printGuid=false;
	
	public String proxyFile=null; 
	
	public void parseArgs(String args[])
	{
		for (String arg:args)
		{
			if (arg.startsWith("--")==true)
			{
				if (arg.compareTo("--help")==0)
				{
					printUsageAndExit(1);   
				}
				else
				{
					// double dash 
					error("Invalid argument:"+arg);	 
					printUsageAndExit(1);  
				}
			}
			// single dash
			else if (arg.startsWith("-")==true)
			{
				if (arg.compareTo("-h")==0)
				{
					printUsageAndExit(1);   
				}
				else if (arg.compareTo("-l")==0)
				{
					longList=true; 
				}
				else if (arg.compareTo("-guid")==0)
				{
					printGuid=true; 
				}
				else if (arg.compareTo("-v")==0)
				{
					verbose=true;  
				}
				else if (arg.compareTo("-debug")==0)
				{
					verbose=true;  
					debug=true; 
				}
				else if (arg.startsWith(PROXY_OPT))
				{
					proxyFile=arg.substring(PROXY_OPT.length());
				}
				else
				{
					error("Invalid argument:"+arg);	 
					exit(1); 
				}
			}
			else
			{
				// first non optional argument is LFC URI  

				if (lfcUri!=null)
				{
					error("URI argument already specified:"+arg);	
					printUsageAndExit(1);   
				}
				try
				{
					lfcUri=new URI(arg);
				}
				catch (URISyntaxException e)
				{
					e.printStackTrace();
					error("Invalid URI:"+arg);	 
					exit(1); 
				} 
			}
		}
		
		if (lfcUri==null)
		{
			error("Must supply LFC URI!");
			printUsageAndExit(1);   
		}
	}
	
	private void printUsageAndExit(int val)
	{
		System.err.println("usage: lfcfs: [-l [-guid]] [-proxy proxyFile] <URI>");
		exit(val); 
	}

	public void assertValidProxy() throws Exception
	{
		GlobusCredential cred=null; 
		
		// custom proxy
		if (proxyFile!=null)
		{
			debug("Using proxy from:"+proxyFile);
			cred=new GlobusCredential(proxyFile);
		}
		else
		{
			debug("Using default proxy file.");
			cred=GlobusCredential.getDefaultCredential();
		}
		
		if (cred==null) 
			throw new Exception("Couldn't find valid proxy");
		
		if (cred.getTimeLeft()<=0)
			throw new Exception("Expiried Credential detected."); 
		
		debug("proxy timeleft="+cred.getTimeLeft());
	
		return; 
	}
	
	
	public void doLS() throws Exception
	{
		assertValidProxy(); 
		
		LFCServer lfcServer=new LFCServer(lfcUri);
		
		ArrayList<FileDesc> entries = lfcServer.listDirectory(lfcUri.getPath());
		
		if (entries==null) 
			return;
		
		   
		for (FileDesc entry:entries) 
		{
			StringBuilder sb = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter = new Formatter(sb, Locale.UK);
			
			if (longList==false)
			{
				System.out.print(entry.getFileName()+" ");
			}
			else
			{
				if (printGuid==false)
				{
					formatter.format("%s %4d %6d %6d %s",
						entry.getPermissions(),
						entry.getULink(),
						entry.getUid(),
						entry.getGid(),
						entry.getFileName());
				}
				else
				{
					// standard UUID is 36 characters wide: 
					formatter.format("%s %4d %6d %6d ('%36s') %s",
							entry.getPermissions(),
							entry.getULink(),
							entry.getUid(),
							entry.getGid(),
							entry.getGuid(),
							entry.getFileName());
						
				}
				
				System.out.println(sb); 
				
			}
		}
		
		if (longList==false) 
		{
			System.out.println("\n");
		}
	}
	
	
	// =======================================================================
	// Static Interface 
	// =======================================================================

	public static void doLS(String[] args)
	{
		LfcCommand lfcCom=new LfcCommand(); 
		lfcCom.parseArgs(args);
		 
		try
		{
			lfcCom.doLS();
		}
		catch (Exception e)
		{
			error("Command lfcls failed"); 
			e.printStackTrace();
			exit(1); 
		}
		
		// explicit exit ok
		exit(0);
	}

	
	public static void exit(int value)
	{
		System.exit(value); 
	}
	
	public static void error(String msg)
	{
		System.err.println("*** Error:"+msg); 
	}

	private static void debug(String msg)
	{
		if (verbose)
			System.out.println(msg); 
	}

}
