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
 * $Id: LfcCommand.java,v 1.2 2011-04-18 12:30:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:40 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import nl.uva.vlet.glite.lfc.LFCConfig;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;

import org.globus.gsi.GlobusCredential;

public class LfcCommand
{
    // === Static Class Stuff ===

    public static final String PROXY_OPT = "-proxy";

    public static boolean verbose = false;

    public static boolean debug = true;

    public static boolean linkstat = false;

    // ===
    // Command Instance
    // ===

    /** Full URI of LFC host, port and path */
    public URI lfcUri = null;

    public boolean longList = false;

    public boolean printGuid = false;

    public String proxyFile = null;

    public void parseArgs(String args[])
    {
        for (int index=0;index<args.length;index++)
        {
            String arg=args[index];
            
            if (arg.startsWith("--") == true)
            {
                if (arg.compareTo("--help") == 0)
                {
                    printUsageAndExit(1);
                }
                else
                {
                    // double dash
                    error("Invalid argument:" + arg);
                    printUsageAndExit(1);
                }
            }
            // single dash
            else if (arg.startsWith("-") == true)
            {
                if (arg.compareTo("-h") == 0)
                {
                    printUsageAndExit(1);
                }
                else if (arg.compareTo("-l") == 0)
                {
                    longList = true;
                }
                else if (arg.compareTo("-guid") == 0)
                {
                    printGuid = true;
                }
                else if (arg.compareTo("-v") == 0)
                {
                    verbose = true;
                }
                else if (arg.compareTo("-noresolve") == 0)
                {
                    linkstat = true;
                }
                else if (arg.compareTo("-lstat") == 0)
                {
                    linkstat = true;
                }
                else if (arg.compareTo("-debug") == 0)
                {
                    verbose = true;
                    debug = true;
                }
                else if (arg.startsWith(PROXY_OPT))
                {
                    // Old -proxy=<file>  option 
                    
                    String assignStr=PROXY_OPT+"="; 
                    
                    if (arg.startsWith(assignStr))
                    {
                        proxyFile = arg.substring(assignStr.length());
                    }
                    
                    // new option: -proxy <file>  
                    if (arg.equals(PROXY_OPT))
                    {
                        if (index+1<args.length)
                        {
                            proxyFile=new String(args[index+1]);
                            index++; // SHIFT ARGUMENT !
                        }
                        else
                        {
                            error("Proxy file argument missing after -proxy");
                            exit(1); 
                        }
                    }
                }
                else
                {
                    error("Invalid argument:" + arg);
                    exit(1);
                }
            }
            else
            {
                // first non optional argument is LFC URI

                if (lfcUri != null)
                {
                    error("URI argument already specified:" + arg);
                    printUsageAndExit(1);
                }
                try
                {
                    lfcUri = new URI(arg);
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                    error("Invalid URI:" + arg);
                    exit(1);
                }
            }
        }

        if (lfcUri == null)
        {
            error("Must supply LFC URI!");
            printUsageAndExit(1);
        }
    }

    private void printUsageAndExit(int val)
    {
        System.err
                .println("usage: lfcfs: [-l [-guid]] [-noresolve] [-proxy proxyFile] <URI>");
        exit(val);
    }

    public GlobusCredential getValidProxy() throws Exception
    {
        GlobusCredential cred = null;

        // custom proxy
        if (proxyFile != null)
        {
            debug("Using proxy from:" + proxyFile);
            cred = new GlobusCredential(proxyFile);
        }
        else
        {
            debug("Using default proxy file.");
            cred = GlobusCredential.getDefaultCredential();
        }

        if (cred == null)
            throw new Exception("Couldn't find valid proxy");

        if (cred.getTimeLeft() <= 0)
            throw new Exception("Expiried Credential detected.");

        debug("proxy timeleft=" + cred.getTimeLeft());

        return cred;
    }

    public LFCServer createLFCServer() throws Exception
    {
        LFCConfig config = new LFCConfig();
        config.globusCredential = getValidProxy();
        LFCServer lfcServer = new LFCServer(config, lfcUri);

        return lfcServer;
    }

    public void ls() throws Exception
    {
        LFCServer lfcServer = createLFCServer();

        ArrayList<FileDesc> entries = lfcServer.listDirectory(lfcUri.getPath());

        if (entries == null)
            return;

        for (FileDesc entry : entries)
        {
            StringBuilder sb = new StringBuilder();
            // Send all output to the Appendable object sb
            Formatter formatter = new Formatter(sb, Locale.UK);

            if (longList == false)
            {
                System.out.print(entry.getFileName() + " ");
            }
            else
            {
                if (printGuid == false)
                {
                    formatter
                            .format("%s %4d %6d %6d %s",
                                    entry.getPermissions(), entry.getULink(),
                                    entry.getUid(), entry.getGid(), entry
                                            .getFileName());
                }
                else
                {
                    // standard UUID is 36 characters wide:
                    formatter.format("%s %4d %6d %6d ('%36s') %s", entry
                            .getPermissions(), entry.getULink(),
                            entry.getUid(), entry.getGid(), entry.getGuid(),
                            entry.getFileName());

                }

                System.out.println(sb);

            }
        }

        if (longList == false)
        {
            System.out.println("\n");
        }
    }

    public void stat() throws Exception
    {
        LFCServer lfcServer = this.createLFCServer();

        FileDesc entry;
        if (linkstat)
            entry = lfcServer.fetchFileDesc(lfcUri.getPath());
        else
            entry = lfcServer.fetchLinkDesc(lfcUri.getPath());

        String typeStr = "File";

        if (entry.isDirectory())
            typeStr = "Directory";

        if (entry.isSymbolicLink())
            typeStr = "Symbolic Link";

        System.out.println(" File           = '" + lfcUri + "'");
        System.out.println(" Type           = " + typeStr);
        System.out.println(" Size           = " + entry.getFileSize());
        System.out.println(" FileID         = " + entry.getFileId());
        System.out.println(" Guid           = '" + entry.getGuid() + "'");
        System.out.println(" Mode           = "
                + Integer.toOctalString(entry.getFileMode()));
        System.out.println(" Permissions    = " + entry.getPermissions());

        System.out.println(" Uid            = " + entry.getUid());
        System.out.println(" Gid            = " + entry.getGid());

        System.out.println(" Access         = " + entry.getADate() + "\t("
                + entry.getATime() + ")");
        System.out.println(" Modify         = " + entry.getMDate() + "\t("
                + entry.getMTime() + ")");
        System.out.println(" Change         = " + entry.getCDate() + "\t("
                + entry.getCTime() + ")");
        // extra lfc info:
        System.out.println(" Comment        = " + entry.getComment());
        System.out.println(" ULink          = " + entry.getULink());
        System.out.println(" File Class     = " + entry.getFileClass());
        System.out.println(" Status         = '" + (char) entry.getStatus()
                + "'");
        System.out.println(" CheckSum Type  = " + entry.getChkSumType());
        System.out.println(" CheckSum Value = " + entry.getChkSumValue());

    }

    // =======================================================================
    // Static Interface
    // =======================================================================

    public static void doLS(String[] args)
    {
        LfcCommand lfcCom = new LfcCommand();
        lfcCom.parseArgs(args);

        try
        {
            lfcCom.ls();
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

    public static void doSTAT(String[] args)
    {
        LfcCommand lfcCom = new LfcCommand();
        lfcCom.parseArgs(args);

        try
        {
            lfcCom.stat();
        }
        catch (Exception e)
        {
            error("Command lfcstat failed");
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
        System.err.println("*** Error:" + msg);
    }

    private static void debug(String msg)
    {
        if (verbose)
            System.out.println(msg);
    }

}
