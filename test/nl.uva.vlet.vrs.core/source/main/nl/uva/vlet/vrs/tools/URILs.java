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
 * $Id: URILs.java,v 1.9 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.vrs.tools;

import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VGlobalUniqueID;
import nl.uva.vlet.vfs.VUnixGroupMode;
import nl.uva.vlet.vfs.VUnixUserMode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;

/**
 * URI 'ls' tool. 
 * Is called from urils script. 
 * 
 * @since VLET 1.2  
 */

public class URILs
{
	// === Static Class Stuff === 
	
	public static boolean verbose=false; 
		
	public static boolean linkstat=false; 
	
	// ===
	// Command Instance 
	// ===
	
	/** Full URI of LFC host, port and path */  
	public VRL theVrl=null;
	
	public boolean optLongList=false;
	
	public boolean optPrintGuid=false;
	
	public String proxyFile=null;

    private VRSClient vrsClient; 
    
	private boolean optPrintVrls=false;
	
	public void parseArgs(String args[])
	{
		for (int i=0;i<args.length;i++) 
		{
		    String arg=args[i];
		    String arg2=null; 
		    
		    if ((i+1)<args.length)
		        arg2=args[i+1]; 
		    
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
					optLongList=true; 
				}
				else if (arg.compareTo("-guid")==0)
				{
					optPrintGuid=true; 
				}
				else if (arg.compareTo("-v")==0)
				{
					verbose=true;  
				}
                else if (arg.compareTo("-noresolve")==0)
                {
                    linkstat=true;  
                }
                else if (arg.compareTo("-lstat")==0)
                {
                    linkstat=true;  
                }
                else if (arg.compareTo("-vrls")==0)
                {
                    optPrintVrls=true;  
                }
                else if (arg.compareTo("-debug")==0)
                {
                    Global.getLogger().setLevelToDebug(); 
                    verbose=true;  
                }
                else if (arg.compareTo("-warn")==0)
                {
                    Global.getLogger().setLevelToWarn(); 
                    verbose=true;  
                }
                else if (arg.compareTo("-info")==0)
                {
                    Global.getLogger().setLevelToInfo(); 
                    verbose=true;  
                }
				// OLD style parameter 
                else if (arg.startsWith("-proxy="))
                {
                    proxyFile=arg.substring("-proxy=".length());
                }
                else if (arg.compareTo("-proxy")==0) 
                {
                    proxyFile=arg2;
                    i++; //SHIFT!
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

				if (theVrl!=null)
				{
					error("URI argument already specified:"+arg);	
					printUsageAndExit(1);   
				}
				try
				{
					theVrl=new VRL(arg);
				}
                catch (VRLSyntaxException e)
                {
                    e.printStackTrace();
                    error("Invalid URI:"+arg);   
                    exit(1); 
                }
			}
		}
		
		if (theVrl==null)
		{
			error("Must supply LFC URI!");
			printUsageAndExit(1);   
		}
	}
	
	private void printUsageAndExit(int val)
	{
        System.err.println("usage: urils: <options> [-proxy <proxyFile>] <URI>\n"
                    +" Where <options> can be:\n"
                    +"  -l                 : long list like 'ls -l'.\n"
                    +"  -guid              : print GUID if the resource has it.\n"
                    +"  -noresolve         : do not resolve links.\n"
                    +"  -vrls              : printout full VRLs instead of resourcename.\n"
                    +"  -proxy <proxyFile> : alternative proxy file.\n");
		exit(val); 
	}
	
	private void init() throws Exception
	{
	    assertValidProxy(); 
	}
	
	public void assertValidProxy() throws Exception
	{
		VRSContext ctx=VRSContext.getDefault();
		GridProxy proxy=ctx.getGridProxy();
		
		// custom proxy
		if (proxyFile!=null)
		{
		    verbosePrintln(" - using proxy from:"+proxyFile);
		    proxy=GridProxy.loadFrom(ctx,proxyFile); 
		    ctx.setGridProxy(proxy); 
 		}
		else
		{
			verbosePrintln(" - using default proxy file:"+proxy.getProxyFilename());
		}
		 
		
		//if (proxy.exists()==false) 
        //    throw new Exception("No valid proxy found at location:"+proxy.getProxyFilename()); 
		
	    if (proxy.getTimeLeft()<=0)
	        throw new Exception("Expiried Credential detected:"+proxy.getProxyFilename()); 
        
        if (proxy.isValid()==false) 
            throw new Exception("Invalid proxy:"+proxy.getProxyFilename()); 
        
		// init VRSClient: 
		vrsClient = new VRSClient(ctx); 
	      
		return; 
	}
	
	
	public void ls() throws Exception
	{
	    verbosePrintln("Performing: urils "+theVrl); 
		init(); 
		
		VNode source=vrsClient.getNode(theVrl); 
		VNode[] nodes;
		
		if (source.isComposite())
		{
		    if (source instanceof VDir)
		    {
		        nodes=((VDir)source).list();
                verbosePrintln(" - resource is directory (VDir) type."); 
		    }
		    else
		    {
		        nodes = ((VComposite)source).getNodes();
		        verbosePrintln(" - resource is composite type:"+source.getType()); 
		    }
		}
		else
		{
            verbosePrintln(" - resource is non-composite type:"+source.getType());
		    // list node itself like: ls -l <file> 
		    nodes=new VNode[1];
		    nodes[0]=source; 
		}
		
		if (nodes==null) 
			return;
		
		   
		for (VNode node:nodes) 
		{
		    boolean isFile=false; 
		    //boolean isDir=false;
			StringBuilder sb = new StringBuilder();
			// Send all output to the Appendable object sb
			Formatter formatter = new Formatter(sb, Locale.UK);
			String uid="-"; 
			String gid="-"; 
			String guidstr="?";
			String modstr="?"; 
			String sizestr="?"; 
            String permstr="";
            
			VFSNode vfsnode=null; 
			
			if (node instanceof VFSNode)
			{
			    vfsnode=(VFSNode)node; 
                isFile=vfsnode.isFile();  
                //isDir=(isFile==false);// file is anything NOT a dir  
			}
			
			if (isFile)
			{
			    long size=((VFile)node).getLength();
			    sizestr=Presentation.createSizeString(size,true,2,1); 
			}
			else
			{
			    // print something:
			    sizestr="0"; 
			}
			
			// Other File/Resource Attributes 
            if (node instanceof VUnixGroupMode)
                gid=((VUnixGroupMode)vfsnode).getGid();
        
            if (node instanceof VUnixUserMode)
                uid=((VUnixUserMode)vfsnode).getUid(); 
        
            if (node instanceof VGlobalUniqueID)
                guidstr=((VGlobalUniqueID)vfsnode).getGUID();
            
			// get vfs specific attributes; 
			if (vfsnode!=null)
			{
			    long modtime=vfsnode.getModificationTime();
			    
			    if (modtime>0)
			        modstr=createTimeStr(modtime); 
			    
			    permstr=vfsnode.getPermissionsString(); 
			}
			else
			{    
			    boolean w=false; 
			    
			    if (node instanceof VEditable)
			        w=((VEditable)node).isEditable();
			    
			    permstr="dr"+(w?"w":"-")+"-";
			    
			}
			
            //if (node instanceof VACL)
            //    permstr+="(+)";
			
            // ---
            // current combinations 
            // ---
			String nodeName=node.getBasename(); 
			if (optPrintVrls)
			    nodeName=node.getVRL().toString(); 
			    
			if ((optLongList==false) && (optPrintGuid==false))
			{
				formatter.format("%s",nodeName);
			}
			else if ((optLongList==false) && (optPrintGuid==true))
			{
	                    // standard UUID is 36 characters wide: 
	                    formatter.format("%36s %s",
	                            guidstr,
	                            nodeName);
			}
			else if ((optLongList==true) && (optPrintGuid==false)) 
			{
					formatter.format("%s %9s %9s %9s %16s %s",
					    permstr,
					    uid,
						gid,
						sizestr,
						modstr,
						nodeName);
			}
			else if ((optLongList==true) && (optPrintGuid==true))
			{
					// standard UUID is 36 characters wide: 
					formatter.format("%s %9s %9s %9s %16s %36s %s",
					        permstr,
							uid,
							gid,
							sizestr,
							modstr,
							guidstr,
							nodeName);
			}
				
			println(sb.toString()); 
		}
		
		if (optLongList==false) 
		{
			println("\n");
		}
	}
	   
	public String createTimeStr(long time)
	{
	    Date date = Presentation.createDate(time);
	    GregorianCalendar gmtTime=new GregorianCalendar();
        gmtTime.setTime(date);
        
        int year=gmtTime.get(GregorianCalendar.YEAR);
        int month=1+gmtTime.get(GregorianCalendar.MONTH); // January=0! 
        int day=gmtTime.get(GregorianCalendar.DAY_OF_MONTH);
        int hours=gmtTime.get(GregorianCalendar.HOUR_OF_DAY);
        int minutes=gmtTime.get(GregorianCalendar.MINUTE);
        
        return ""+year
                +"-"+Presentation.to2decimals(month)
                +"-"+Presentation.to2decimals(day)
                +" "+Presentation.to2decimals(hours)
                +":"+Presentation.to2decimals(minutes);
 
	}
	// =======================================================================
	// Static Interface 
	// =======================================================================

	public static void main(String[] args)
	{
		URILs uricom=new URILs(); 
		uricom.parseArgs(args);
		 
		try
		{
			uricom.ls();
		}
		catch (Exception e)
		{
			error("Command urils failed.");
			error("Exception:"+e.getMessage());
			if (verbose) 
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
    
    public static void println(String msg)
    {
        System.out.println(msg); 
    }
    
    public static void verbosePrintln(String msg)
    {
        if (verbose)
            System.out.println(msg); 
    }


}
