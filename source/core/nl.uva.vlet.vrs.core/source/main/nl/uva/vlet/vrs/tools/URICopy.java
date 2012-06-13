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
 * $Id: URICopy.java,v 1.9 2011-04-18 12:00:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:38 $
 */ 
// source: 

package nl.uva.vlet.vrs.tools;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;

/** 
 * URI 'cp' command. 
 * Parse command line arguments and do the copy.
 * This class is called from the uricopy script.
 *  
 */
public class URICopy
{
    static int verbose=0; 
    static boolean copyDir = false;
    static String sourceVrl = null;
    static String destVrlStr = null;
    static boolean isMove = false;
    static boolean force=false;    // protect dummy user
    static boolean mkdirs=false;  // same
    static String proxyFile=null; 
    
    /** Wheter to printout the resulting vrl */ 
    private static boolean resultvrl=false;
    
    private static boolean checksum=false; 
    
    private static String checksumType=null;
    
    public static void Usage()
    {
        error("Usage: [-r] [-v [-v]] [-move] [-debug|-info|-warn] [-force] [-result] [-D<prop>=<value] <sourceVRL> [-file] <destinationVRL>");
        error(
                 "Arguments:\n"
                +"  <sourceVRL>            : source file or directory\n"
                +"  <destinationVRL>       : target parent directory (which must exist)!\n"
                +"Options:\n"
                +"  -proxy <proxyfile>     : Optional proxy file\n"
                +"  -r,-dir                : recursive (needed for directories)\n"
                +"  -v                     : be verbose (use twice for more)\n"
                +"  -move                  : delete source after copy\n"
                +"  -mkdirs                : create target directory if non existant\n"
                +"  -f|-force              : overwrite existing destination\n"
                +"  -result                : print resulting vrl (result=...)\n"
//                +"  -file                  : the destination URL is the actual full path of the file\n"
//                +"                           that should be created instead of the target directory.\n"
//                +"  -dir                   : the destination URL is the actual full path of the directory\n"
//                +"                           that should be created instead the target directory.\n"
//                +"                           this only applies when copying a directory.\n"
//                +"  Note:\n"
//                +"    The '-file' and '-dir' arguments are there for disambiguation whether the target VRL\n"
//                +"    is the parent directory or the new full path of the destition.\n" 
//                +"\n"
                +"Advanced Features: "
                +"  -checksum <TYPE>       : print checksum of resulting file, if supported."
                );
    }

    public static void error(String str)
    {
        System.err.println(str);
    }

    public static void exit(int stat)
    {
        System.exit(stat);
    }

    public static void main(String args[])
    {
        args=Global.parseArguments(args);

        for (int i = 0; i < args.length; i++)
        {
            String arg=args[i];
            String arg2=null; 
                
            if ((i+1)<args.length)
                arg2=args[i+1];
            
            // Double Argument options 
            if ((i+1)<args.length)
            {
                if (args[i].equalsIgnoreCase("-checksum"))
                {
                    checksum=true;
                    checksumType=args[i+1];
                    // extra shift!
                    i++;
                    continue; 
                }
            }
            
            
            if (arg.startsWith("-D"))
            {  
               // ignore property argument. 
            }
            else if (arg.equalsIgnoreCase("-r"))
            {
                copyDir = true;
            }
            else if (arg.equalsIgnoreCase("-dir"))
            {
                copyDir = true;
            }
            else if (arg.equalsIgnoreCase("-v"))
            {
                verbose++;
                int v=verbose-2;
                
                // set Global verbose 2 levels back to VFSCopy verbose 
                // (Verbose>2 triggers Global Verbose)
                if (v<=0) 
                   v=0; 
            }
            else if (arg.equalsIgnoreCase("-move"))
            {
                isMove = true;
            }
            else if (arg.equalsIgnoreCase("-f"))
            {
                force = true;
            }
            else if (arg.equalsIgnoreCase("-mkdirs"))
            {
                mkdirs = true;
            }
            else if (arg.equalsIgnoreCase("-force"))
            {
                force = true;
            }
            else if (arg.equalsIgnoreCase("-result"))
            {
                resultvrl=true; 
            }
            else if (arg.equalsIgnoreCase("-debug"))
            {
                Global.getLogger().setLevelToDebug();
            }
            else if (arg.equalsIgnoreCase("-info"))
            {
                Global.getLogger().setLevelToInfo(); 
            }
            else if (arg.equalsIgnoreCase("-warn"))
            {
                Global.getLogger().setLevelToWarn(); 
            }
            else if (arg.startsWith("-proxy="))
            {
                proxyFile=arg.substring("-proxy=".length());
            }
            else if (arg.compareTo("-proxy")==0) 
            {
                proxyFile=arg2;
                i++; //SHIFT!
            }
            /*else  if ((i+1<args.length) && ()) 
             {
             
             // double command line arguments 
             }*/
            else
            {
                if (sourceVrl == null)
                    sourceVrl = arg; // first argument
                else if (destVrlStr == null)
                    destVrlStr = arg; // second argument
                else
                {
                    // extra argument 
                    error("*** Error: Invalid argument:" + arg);
                    Usage();
                    exit(1);
                }
            } // if(args[i])
        } // for 

        
        // POST Option Checks
        
        if (destVrlStr == null)
        {
            // not enough arguments 
            Usage();
            exit(1);
        }
        
        if ((checksum) && (StringUtil.isEmpty(checksumType)))
        {
            error("*** Error: NULL checksumtype");
            exit(3); 
        }
 
        // Start copying. 
        
        try
        {
            VFSClient vfs=new VFSClient(); // My Client
            
            if (proxyFile!=null)
            {
                GridProxy proxy=GridProxy.loadFrom(proxyFile);
                vfs.getVRSContext().setGridProxy(proxy); 
            }
            
            VFSNode sourceNode = vfs.openLocation(sourceVrl);
            
            VFileSystem targetFS = vfs.openFileSystem(new VRL(destVrlStr));

            VDir sourceDir = null;
            VFile sourceFile = null;
          
            // ===========
            // Check Source: 
            // ===========
            if (sourceNode.exists()==false)
            {
                error("*** Error: Could not locate source:"+sourceNode);
                exit(1);
            }
            
            if (sourceNode.isDir())
            {
                if (copyDir == false)
                {
                    error("*** Warning: Skipping directory copy (specify -r to copy directory):"+sourceNode);
                    exit(-1);
                }
                else
                {
                    if (sourceNode instanceof VDir)
                    {
                        sourceDir = (VDir) sourceNode;
                    }
                    else
                    {
                        error("*** Error: Is not a directory:"+sourceNode);
                        exit(1);
                    }
                }
            }
            else if (sourceNode instanceof VFile)
            {
                copyDir=false; 
                sourceFile = (VFile) sourceNode;
            }
            else
            {
                error("*** Error: Can't handle unknown source:(" + sourceNode.getClass()
                        + "):" + sourceNode);
                exit(1);
            }
            
            // ===========
            // Check target  
            // ===========
            VRL destVRL=new VRL(destVrlStr); 
            VDir destDir=null; 
            
            if (vfs.existsDir(destVrlStr)==true)
            {
                destDir=targetFS.newDir(destVRL);
            }
            else 
            {
                if (mkdirs==true)
                {
                    destDir=vfs.mkdirs(destVRL);     
                }
                else 
                {
                    error("*** Error: Target location must be an existing directory (use -mkdirs to autocreate)");
                    exit(1);
                }
            }
                       
           if (sourceNode.isDir())
           {
               // default behaviour of copyTo is to overwrite !
               if ((destDir.hasDir(sourceNode.getName())) && (force==false))
               {
                   error("*** Error: Destination (sub)directory already exists. Use -force to overwrite existing destination"); 
                   exit(1);
               }
           }
           else if (sourceNode.isFile())
           {
               // default behaviour of copyTo is to overwrite !
               if ((destDir.hasFile(sourceNode.getName())) && (force==false))
               {
                   error("*** Error: Destination file already exists. Use -force to overwrite existing destination"); 
                   exit(1);
               }
           }
            
           VFSNode result = null;

           String cmStr = null;

           if (isMove == true)
           {
               cmStr = "Moving";
           }
           else
           {
               cmStr = "Copying";
           }   
           Message(2,"--- parameters --- ");
           Message(2,"source  ="+ sourceFile); 
           Message(2,"dest    ="+ destDir);
           Message(2,"force   ="+ force);
           Message(2,"method  ="+ cmStr);
           Message(2,"--- config --- ");
           Message(2,"passiveMode       ="+Global.getPassiveMode()); 
           Message(2,"firewallPortRange ="+Global.getFirewallPortRangeString()); 
           
           // now the copy :
           if (copyDir == true)
           {
                Message(1,cmStr + " directory:" + sourceDir + "' -> '"
                        + destDir+"'");
                
                if (isMove) 
                    result = sourceDir.moveTo(destDir);
                else
                    result = sourceDir.copyTo(destDir);
            }
            else
            {
                Message(1,cmStr + " file:" + sourceFile + "' -> '"
                        + destDir+"'");

                if (isMove) 
                    result = sourceFile.moveTo(destDir);
                else
                    result = sourceFile.copyTo(destDir);
            }
            // printout clean resulting VRL for scripting purposes
            
            if (resultvrl)
            {
                Message(0,"result=" + result);
            }
            
            if (checksum)
                printChecksum(result,checksumType);
            
        }// try
        catch (VlException e1)
        {
            error("*** Exception=" + e1.getName());
            error("*** Exception message=" + e1.getMessage());
            //
            Global.logException(ClassLogger.WARN,URICopy.class,e1,"*** Exception ***\n"); 
            
            exit(1);
        }

        exit(0); // ok (?) 
    }
    
    private static void printChecksum(VFSNode result,String checksumType)
    {
        if (checksumType==null)
            error("*** Error: NULL checksumtype");  
        
        try
        {
            if (result instanceof VChecksum)
            {
                String str=((VChecksum)result).getChecksum(checksumType); 
                Message(0,"checksum="+str); 
            }
            else
            {
                error("Checkum interface not supported for:"+result); 
            }
        }
        catch(Exception e)
        {
            error("*** Error: failed to fetch checksum type"+checksumType+" for:"+result);
            error("*** Exception message ="+e.getMessage());
            e.printStackTrace(System.err); 
        }
    }

    public static void Message(int level,String str)
    {
        if (level<=verbose)
            System.out.println(str); 
    }

}
