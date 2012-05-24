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
 * $Id: URIStat.java,v 1.10 2011-04-18 12:00:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:38 $
 */ 
// source: 

package nl.uva.vlet.vrs.tools;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CHECKSUM_TYPES;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_CREATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_GID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_ISSYMBOLICLINK;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_SYMBOLICLINKTARGET;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_TYPE;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UID;
import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;
import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSClient;

/** 
 * URI 'stat' command. 
 * Parse command line arguments and do the copy  
 * This class is called from the uristat script. 
 * 
 * @since VLET 1.2
 */
public class URIStat
{
    static int verbose=0;
    static String sourceVrl = null;
    static boolean quoted=true; 
    static boolean allAttributes=true; 
    static boolean checksum=false; 
    static String checksumType="MD5";
    /** List of attribute to display */ 
    static StringList attributeList=null; 
    static String proxyFile=null; 
    
    static String commonFileAttrNames[]=
        {
            ATTR_PATH,
            ATTR_TYPE,
            ATTR_LENGTH,
            ATTR_PERMISSIONS_STRING,
            ATTR_UNIX_FILE_MODE,
            ATTR_CREATION_TIME,
            ATTR_MODIFICATION_TIME,
            ATTR_UID,
            ATTR_GID,
            ATTR_ISSYMBOLICLINK,
            ATTR_SYMBOLICLINKTARGET
        };

       
    public static void Usage()
    {
        System.err.println("Usage: [-v] [-filestat] [-attrs=<attributelist>] [-D<prop>=<value] <sourceURI> ");
        System.err.println(
                 "Arguments:\n"
                +"  <sourceURI>              : source URI \n"
                +"  [ -proxy <proxyfile> ]   : Optional proxy file \n"
                +"  -v [-v]                  : be verbose (use twice for more)\n"
                +"  -file                    : print out common file attributes\n"
                +"  -all                     : print out all resource attributes (default)\n"
                +"  -[no]quotes              : put value between single 'quotes' (or not)\n"
                +"  -checksum <TYPE>         : report checksum (if supported)\n"
                +"  [-attrs=<attributelist>] : list of comma separated VRS Attribute names\n"
                +"  [-D<prop>=<value]        : specify java properties to the JVM\n"
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
        
        attributeList=new StringList(); 
        
        for (int i = 0; i < args.length; i++)
        {
            String arg=args[i];
            String arg2=null; 
            
            if ((i+1)<args.length)
                arg2=args[i+1]; 
            
            // double arguments 
            if (i+1<args.length)
            {
                if (args[i].equalsIgnoreCase("-checksum"))
                {
                    checksum=true;
                    allAttributes=false;
                    checksumType=args[i+1];
                    attributeList.add(ATTR_CHECKSUM);
                    attributeList.add(ATTR_CHECKSUM_TYPE);
                    attributeList.add(ATTR_CHECKSUM_TYPES);
                    // SHIFT
                    i++; 
                    continue; 
                }   
            }
            
            if (arg.equalsIgnoreCase("-nochecksum"))
            {
                checksum=false; 
                attributeList.remove(ATTR_CHECKSUM); 
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
            else if ( (arg.equalsIgnoreCase("-filestat"))
                      ||(arg.equalsIgnoreCase("-file")) )
            {
                allAttributes=false;
                attributeList.merge(commonFileAttrNames); 
            }
            else if (arg.equalsIgnoreCase("-all"))
            {
                allAttributes=true; 
            }
            else if (arg.equalsIgnoreCase("-help"))
            {
                Usage(); 
                exit(0);  
            }
            else if (arg.equalsIgnoreCase("-h"))
            {
                Usage(); 
                exit(0);  
            }
            else if (arg.equalsIgnoreCase("-quotes"))
            {
                quoted=true; 
            }
            else if (arg.equalsIgnoreCase("-noquotes"))
            {
                quoted=false;  
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
            else if (arg.startsWith("-attrs="))
            {
                allAttributes=false; 
                String strs[]=arg.split("=");
                if ((strs!=null) && (strs.length==2))
                {
                    attributeList.merge(StringList.createFrom(strs[1],","));  
                }
            }
            else
            {
                if (sourceVrl == null)
                    sourceVrl = arg; // first argument
                else
                {
                    // extra argument 
                    error("*** Error: Invalid argument:" + arg);
                    Usage();
                    exit(1);
                }
            } // if(args[i])
        } // for 
        
        if (sourceVrl == null)
        {
            // not enough arguments 
            Usage();
            exit(1);
        }

        try
        {
            VRSClient vfs=new VRSClient(); // My Client 
            
            if (proxyFile!=null)
            {
                GridProxy proxy=GridProxy.loadFrom(vfs.getVRSContext(),proxyFile);
                vfs.getVRSContext().setGridProxy(proxy); 
            }
            
            VNode node = vfs.openLocation(sourceVrl);
            
            stat(node,attributeList); 
            
//            // Check Source: 
//            // ===========
//            if (sourceNode.exists()==false)
//            {
//                Error("*** Error: Could not locate source:"+sourceNode);
//                exit(1);
//            }
            
           Message(2,"--- parameters --- ");
           Message(2,"source  ="+ sourceVrl);
           
        }// try
        catch (ResourceNotFoundException e)
        {
            error("*** Resource not found:"+sourceVrl); 
            exit(1); 
        }
        catch (VlException e1)
        {
            error("*** Exception=" + e1.getName());
            error("*** Exception message=" + e1.getMessage());
            // message is included in above message
            //Error("Exception Message:" + e1.getMessage());
            
            // print out full stack trace in WARN mode: 
            Global.logException(ClassLogger.WARN,URIStat.class,e1,"Stat Failed for%s\n",sourceVrl); 
            exit(2);
        }

        exit(0); // ok (?) 
    }

    public static void Message(int level,String str)
    {
        if (level<=verbose)
            System.out.println(str); 
        else
            Global.infoPrintf("URIStat","%s\n",str);  
    }
    
    private static void stat(VNode node, StringList attributeList) throws VlException
    {
        Message(1,"Statting node:"+node);
        
        // Add File Attributes:
        
        VAttributeSet attrs=null; 
        
        if ((allAttributes==true) || (attributeList==null))
        {
            attributeList=new StringList(node.getAttributeNames());
        }
        
        Message(2,"Statting Attributes:"+attributeList.toString(",")); 
        
        attrs=node.getAttributeSet(attributeList.toArray()); 
        
        if (checksum)
        {
             if (node instanceof VChecksum)
             {
                 try
                 {
                     String checkStr=((VChecksum)node).getChecksum(checksumType);
                     attrs.set(new VAttribute(ATTR_CHECKSUM,checkStr));
                     attrs.set(new VAttribute(ATTR_CHECKSUM_TYPE,checksumType));
                 }
                 catch (Exception e)
                 {
                     error("*** Error fetching checksum type:"+checksumType); 
                     error("*** Exception message="+e.getMessage()); 
                 }
                 
             }
        }
        // find maximum 
        int maxWidth=0;  
        for (String name:attributeList)
           if (name.length()>maxWidth)
              maxWidth=name.length();  
      
        for (String name:attributeList)
        {
            VAttribute attr=attrs.get(name);
            String value="";
            if (attr!=null)
                value=attr.getStringValue(); 
            
            if (quoted)
                System.out.printf(" %"+maxWidth+"s='%s'\n",name,value);
            else
                System.out.printf(" %"+maxWidth+"s=%s\n",name,value);
        }
    }


}
