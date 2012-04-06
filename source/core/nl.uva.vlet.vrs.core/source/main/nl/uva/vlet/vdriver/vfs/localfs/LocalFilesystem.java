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
 * $Id: LocalFilesystem.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vfs.localfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.exec.LocalExec;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import sun.awt.shell.ShellFolder;

/**
 * Implementation of the LocalFilesystem.
 */
public class LocalFilesystem extends FileSystemNode
{
    public static final String DEFAULT_LOCALFS_SERVERID="localfs";

    private static Boolean hasUxStatCmd=null; 
   
    static private void checkHasUxStat() 
    {
        // Method is not synchronized. duplicate initialized is not a problem. 
        if (hasUxStatCmd!=null)
            return; 
        
        // Default ! 
        hasUxStatCmd=new Boolean(false);  

        // Bug: #278
        if (Global.isWindows())
        {
            // todo: cygwin !
            Global.infoPrintf(Global.class,"checkHasUxStat()==false. Skipping stat check for Windows!\n"); 
            return;
        }

        try
        {
            // try stat command, might work on recent MacOs!  
            String cmds[]=
                {   
                    "stat",
                    "--format",
                    "%A%N",
                    "/bin/sh"
                };

            String result[]=LocalExec.simpleExecute(cmds);
            checkExitStatus(result);

            // if no exception, continue: 
            hasUxStatCmd=new Boolean(true);  
            // uxStatWorks=true; 
        }
        catch (Exception e)
        {
            Global.logException(ClassLogger.WARN,LocalFilesystem.class,e,"Stat command failed. Not UX compatible filesystem:%s\n",e); 
        }
    }

    // ========================================================================
    // Instance
    // ========================================================================

    public LocalFilesystem(VRSContext context, ServerInfo info, VRL location)
    {
        // Use one ServerInfo for all ! 
        super(context,context.getServerInfoRegistry().getServerInfoFor(new VRL("file",null,"/"),true));
        
        // static method!
        checkHasUxStat();
    }

    @Override
    public VFSNode openLocation(VRL location) throws VlException
    {
        // cannot handle location other then for localhost,
        // so location MUST be a local file path 
        Debug("path="+location.getPath()); 
        return getPath(location.getPath()); 
    }
    
    @Override
    public void connect() throws VlException 
    {
    }
    
    @Override
    public void disconnect() throws VlException
    {
    }
    
    @Override
    public final String getHostname()
    {
        return VRS.LOCALHOST;
    }
    
    @Override
    public String getID() 
    {
        return DEFAULT_LOCALFS_SERVERID;
    }
    
    @Override
    public int getPort() 
    {
        return 0;
    }
    
    @Override
    public String getScheme() 
    {
        return VFS.FILE_SCHEME; 
    }
  
    @Override
    public VRL getServerVRL() 
    {
        return new VRL("file",null,"/");  
    }
    
    @Override
    public boolean isConnected() 
    {
        return true;
    }
    
    @Override
    public VFSNode getPath(String path) throws VlException
    {
        if (path==null)
            throw new VlInternalError("Path is NULL");  

        //System.setSecurityManager(null);    
        // resolve ~:
        if (path.startsWith("~")) 
        {
            path=Global.getProperty("user.home")+VRL.SEP_CHAR_STR+path.substring(1); 
        }
        else if (path.startsWith("/~"))
        {
            path=Global.getProperty("user.home")+VRL.SEP_CHAR_STR+path.substring(2); 
        }

        File file=new File(path);

        if (file.exists()==true)
        {
            if (file.isFile())
            {
                return new LFile(this,file);
            }
            else if (file.isDirectory())
            {
                LDir dir=new LDir(this,file);
                return dir;   
            }
            else
            {
                throw new ResourceNotFoundException("Couldn't handle unknown resource type:"+path);
            }
        }
        
        // ===================
        // Unix Softlink hack
        // ===================
        try
        {
            StatInfo stat = this.stat(file); 
            // could be non existing soft link:
            if ((stat!=null) && stat.isSoftLink())
            {
                LFile lfile=new LFile(this,file);
                lfile.setStatInfo(stat); // kep stat info.
                return lfile; 
            }
        }
        catch (Exception e)
        {
            ; // ignore file really doesn't exists! 
        }
        
        // ===================================================
        // Windows hack. The drive path  "/A:/" doesn't exists 
        // if there is no floppy in the drive! 
        // Check normalized windows drive path: "/C:/" (or "/A:/") 
        // If "A:" then return as (existing) Directory !
        // ===================================================
        
        if ((path.length()==4)	&& (path.substring(2).compareTo(":/")==0)) 
        {
            LDir dir=new LDir(this,file);
            return dir;  
        }
        
        throw new ResourceNotFoundException("Couldn't locate path:"+path);
    }

    // *** Local package methods to be used by VFile and VDir ***

    /** calls java.io.File.delete() */
    static boolean delete(java.io.File _file)
    {
        return _file.delete();
    }

    /** calls java.io.File.canRead() */
    static boolean isReadable(java.io.File _file)
    {
        return _file.canRead();
    }

    /** calls java.io.File.canWrite() */
    static boolean isWritable(java.io.File _file)
    {
        return _file.canWrite();
    }

    private static void Debug(String str)
    {
        Global.debugPrintf(LocalFilesystem.class,"%s\n",str); 
    }

    public File renameTo(String filepath, String newname, boolean nameIsPath)
    {
        String fullname=null;
        // local filesystem path, use File.seperatorChar
        Debug("Renaming:"+filepath+" to: (nameIsPath="+nameIsPath+")"+newname); 


        if (nameIsPath)
        {
            fullname=newname;
        }
        else
        {
            fullname=VRL.dirname(filepath)+VRL.SEP_CHAR+newname;
        }

        Debug("newfilename="+fullname);  
        java.io.File newfile=new java.io.File(fullname);
        java.io.File _file=new java.io.File(filepath);
        Debug("newfilename.absolute ="+newfile.getAbsolutePath()); 
        try 
        {
            Debug("newfilename.canonical="+newfile.getCanonicalPath());
        }
        catch (IOException e) 
        {
            Global.logException(ClassLogger.ERROR,this,e,"IOException:%s\n",e); 
        } 

        // Again nothing is throw if it failes. 
        _file.renameTo(newfile);

        if (newfile.exists())
            return newfile;
        else
        {
            // ?
            return null; 
        }   
    }

    /** 
     * Resolve Windows .lnk (shortcut) 
     * @param file */ 
    public String getWindowsLinkTarget(File file) throws VlException
    {

        try
        {
            ShellFolder linkFile = ShellFolder.getShellFolder(file);
            File linkTo=linkFile.getLinkLocation();

            if (linkTo==null) 
            {
                Debug("***OOPS: getWindowsLinkTarget: null linkLocation!");
                return null;
            }

            return linkTo.getCanonicalPath(); 

        }
        catch (FileNotFoundException e)
        {
            throw new VlException("FileNotFoundException","for:"+file+"\n"+e.getMessage(),e); 
        }
        catch (IOException e)
        {
            throw new VlException("IOException","for:"+file+"\n"+e.getMessage(),e); 
        }
        catch (Exception e)
        {
            throw new VlException("Exception","for:"+file+"\n"+e.getMessage(),e); 
        }

    }

    /** Get Windows icon of local file */ 
    public ImageIcon getWindowsIcon(File file) throws VlException 
    {
        if (Global.isWindows())
        {
            try
            {
                // propriatary ? 
                ShellFolder shellfile = ShellFolder.getShellFolder(file);
                /**
			            File linkTo=linkFile.getLinkLocation();

			            if (linkTo==null) 
			            {
			                Debug("***OOPS: getWindowsLinkTarget: null linkLocation!");
			                return null;
			            }*/

                ImageIcon icon = new ImageIcon(shellfile.getIcon(true), shellfile.getFolderType());

                return icon;  

            }
            catch (FileNotFoundException e)
            {
                throw new VlException("FileNotFoundException","for:"+file+"\n"+e.getMessage(),e); 
            }
            catch (Exception e)
            {
                throw new VlException("Exception","for:"+file+"\n"+e.getMessage(),e); 
            }

        }


        return null;
    }

    public boolean hasStatCmd()
    {	
        return hasUxStatCmd; 
    }

    public void setMode(String path, int mode) throws VlException
    {
        if (hasStatCmd())
        {
            String modeStr=Integer.toOctalString(mode); 
            // use arg list to support SPACES in path !  (engfeh)
            String cmds[]=
                {   
                    "chmod",
                    modeStr,
                    path
                };

            String result[]=LocalExec.simpleExecute(cmds);

            checkExitStatus(result); 

            //return result[0]; 
        }
        else
            return;
    }


    static void checkExitStatus(String[] result) throws VlException
    {
        if ((result!=null) && (result.length>2))
        {
            int status=Integer.parseInt(result[2]);

            if (status!=0)
            {
                throw new VlException("Execution Error",
                        "Exit status="+status
                        +"\n. stdout="+result[1]
                                              +"\n. stderr="+result[2]);
            }
        }

    }

    public String getSoftLinkTarget(String path) throws VlException
    {
        if (hasStatCmd())
        {
            // use arg list to support SPACES in path !  (engfeh)
            String cmds[]=
                {   
                    "stat",
                    "--format",
                    "%N",
                    path
                };

            String result[]=LocalExec.simpleExecute(cmds);
            checkExitStatus(result);

            // return AS IS (can not parse '...' -> '...' here !) 
            return result[0];

        }

        return null; 
    }

    public VDir createDir(String path, boolean force) throws VlException
    {
        // TBD: not portable using forward slash!
        String fullpath=resolvePath(path); 

        File dir=new File(fullpath); 

        if (dir.exists())
        {
            if (dir.isDirectory()==false) 
                throw new ResourceCreationFailedException("File path already exists, but is not a directory:"+this);


            if (force==false)
            {
                throw new ResourceAlreadyExistsException("Directory already exists:"+fullpath);
            }
            else
            {
                Debug("Not recreating existing directory:" + dir);
                return new LDir(this,fullpath); // return existing directory
            }
        }

        // check full path : 

        if (!mkfulldir(dir))
            throw new ResourceCreationFailedException("Couldn't create:"+fullpath);

        Debug("Created directory:" + path);

        return new LDir(this,fullpath); 
    }


    /** Create full directory on local filesystem 
     * @throws ResourceCreationFailedException */  
    public boolean mkfulldir(File path) throws ResourceCreationFailedException
    {
        // check parent and create parent 
        File parentDir=path.getParentFile(); 

        if (parentDir.exists()==false)
        {
            mkfulldir(parentDir); 
        }

        // return this path element; 
        return path.mkdir(); 
    }

    @Override
    public VFile createFile(VRL fileVrl, boolean force) throws VlException
    {
        return createFile(fileVrl.getPath(),force); 
    }
    
    public VFile createFile(String name, boolean force) throws VlException 
    {
        // URI: use forward slash: 
        String loc = resolvePath(name); 

        java.io.File f = new File(loc);

        if (f.exists()==true)
        {
            if (f.isFile()==false) 
                throw new ResourceCreationFailedException("File path already exists, but is not a file:"+loc);

            if (force==false)
                throw new ResourceAlreadyExistsException("File path already exists:"+this);

            // delete existing file:! 
            f.delete(); 
        }

        // create file: 

        LFile lfile=new LFile(this,loc); 
        lfile.create(); 

        return lfile; 
    }
    
    @Override
    public boolean existsFile(VRL fileVrl) throws VlException 
    {
        java.io.File f = new File(fileVrl.getPath());
        return (f.exists() && f.isFile()); 
    }
    
    @Override
    public boolean existsDir(VRL fileVrl) throws VlException 
    {
        java.io.File f = new File(fileVrl.getPath());
        return (f.exists() && f.isDirectory()); 
    }

    @Override
    public VDir newDir(VRL dirPath) throws VlException 
    {
        return new LDir(this,dirPath.getPath()); 
    }

    @Override
    public VFile newFile(VRL fileVrl) throws VlException 
    {
        return new LFile(this,fileVrl.getPath()); 
    }

    public boolean isUnixFS()
    {
        // whether UX FS is support depends on 'stat' command 
        return hasUxStatCmd;
    }
   
    /**
     * Peforms unix 'stat' command or create dummy StatInfo from File defaults. 
     * Always returns StatInfo object.   
     */
    public StatInfo stat(File file) throws VlException
    {
        if (hasStatCmd())
        {
            return this.uxStat(file.getAbsolutePath());
        }
        else
        {
            return StatInfo.createFrom(file);  
        }
    }
    
    /** Perform UX file stat to get file attributes */ 
    public StatInfo uxStat(String path) throws VlException
    {
        if (hasStatCmd())
        {
            String statstr=StatInfo.getUxStatString();
            
            // use arg list to support SPACES in path !  (engfeh)
            String cmds[]=
                {   
                    "stat",
                    "--format",
                    statstr, 
                    path
                };

            String result[]=LocalExec.simpleExecute(cmds);
            
            checkExitStatus(result);
            return StatInfo.parseUxStatResult(result[0]); 
        }

        return null; 
    }
}
