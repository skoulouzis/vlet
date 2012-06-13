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
 * $Id: LFile.java,v 1.1 2011-11-25 13:40:45 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:45 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vfs.localfs;

import static nl.uva.vlet.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.ResourceCreationFailedException;
import nl.uva.vlet.exception.ResourceNotFoundException;
import nl.uva.vlet.exception.ResourceReadAccessDeniedException;
import nl.uva.vlet.exception.ResourceWriteAccessDeniedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.util.ChecksumUtil;
import nl.uva.vlet.vfs.VChecksum;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.VUnixFileAttributes;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.io.VRandomAccessable;
import nl.uva.vlet.vrs.io.VResizable;
import nl.uva.vlet.vrs.io.VStreamAccessable;
import nl.uva.vlet.vrs.io.VStreamAppendable;

/**
 * Local LFile System implementation of the VFile class
 */
public class LFile extends VFile implements VStreamAccessable, VStreamAppendable, 
        VRandomAccessable, VUnixFileAttributes, VResizable, VChecksum
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(LFile.class); 
    }
    
    // ** local LFile implementation
    private String path = null;
    private java.io.File _file = null;
    private LocalFilesystem localfs;
    private StatInfo statInf; 
    
    // =================================================================
    // Constructors
    // =================================================================

    /**
     * Public constructor to create new LFile.
     * 
     * @param path
     * @throws VlException
     */
    public LFile(LocalFilesystem localFS, String path) throws VlException
    {
        super(localFS, new VRL("file:///"
                + VRL.uripath(path, true, java.io.File.separatorChar)));
        this.localfs = localFS;

        // windows hack: 'c:' is a relative path
        if (path.charAt(path.length() - 1) == ':')
        {
            path = path + VRL.SEP_CHAR; // make absolute !
        }
        // Note: on windows the file path gets converted to use BACKSLASHES
        // for URI' use VRL.sepChar, for java Files use File.seperatorChar
        java.io.File file = new File(path);
        init(file);
    }

    /** Initiliaze with Java File object */
    private void init(java.io.File file) throws VlException
    {
        logger.debugPrintf("init():new file:%s\n",file); 
        
        String path = file.getAbsolutePath();

        //
        // Forward Flip backslashes !
        // Do this ONLY for the local filesystem !
        //

        if (File.separatorChar != VRL.SEP_CHAR)
            path = VRL.uripath(path, true, File.separatorChar);

        this.setLocation(new VRL(VRS.FILE_SCHEME,null, path));
        this.path = path;
        this._file = file;

    }

    /** Construct new LocalFS File object from Java File */
    public LFile(LocalFilesystem localFS, java.io.File file) throws VlException
    {
        super(localFS, new VRL(file.toURI()));
        this.localfs = localFS;
        init(file);
    }

    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (localfs.isUnixFS())
        {
            StringList list = new StringList(superNames);
            list.add(LocalFSFactory.unixFSAttributeNames);

            return list.toArray();
        }

        return superNames;
    }

    private StatInfo getStat() throws VlException
    {
        synchronized(_file)
        {
            if (statInf==null)
            {
                statInf=this.localfs.stat(_file); 
            }
        }
        
        return statInf; 
    }

    /**
     * Returns single atttribute triplet
     * 
     * @throws VlException
     */
    public VAttribute getAttribute(String name) throws VlException
    {
        // slowdown: logger.debugPrintf("getAttribute '%s' for:%s\n",name,this); 
        
        if (name == null)
            return null;

        // Check if super class has this attribute
        VAttribute supervalue = super.getAttribute(name);

        // Super class has this attribute, and since I do not overide
        // any attribute, return this one:
        if (supervalue != null)
            return supervalue;

        // unix attributes

        if (name.compareTo(ATTR_UNIX_FILE_MODE) == 0)
            return new VAttribute(name, Integer.toOctalString(getMode()));

        return null;
    }

    public VDir getParentDir() throws VlException
    {
        String pstr = _file.getParent();
        VDir dir = new LDir(localfs, pstr);

        return dir;
    }

    public long getSize() throws VlException
    {
        return getStat().getSize(); 
    }

    public String toString()
    {
        return getLocation().toString();
    }

    public boolean exists()
    {
        // Must exist and must be a file 
        return (_file.isFile()&&_file.exists()); 
    }

    public boolean isReadable()
    {
        // a file is readable, it can not read...
        return _file.canRead(); 
    }

    /** returns true is 'file is writable' */
    public boolean isWritable()
    {
        // a file is writable, it can not write ...
        return _file.canWrite();
    }

    public boolean create() throws VlException
    {
        boolean result = create(true);

        return result;
    }

    public boolean create(boolean force) throws VlException
    {
        try
        {
            if (_file.exists())
            {
                if (this._file.isDirectory())
                {
                    throw new ResourceAlreadyExistsException(
                            "path already exists but is a directory:" + this);
                }

                if (force == false)
                {
                    throw new ResourceAlreadyExistsException(
                            "LFile already exists:" + this);
                }
                else
                {
                    logger.debugPrintf("Warning: Not creating existing file, but truncating:%s\n",this);
                    this.delete();
                }
            }

            // check parent:
            if (_file.getParentFile().exists() == false)
            {
                throw new ResourceCreationFailedException(
                        "Parent directory doesn't exist for file:" + path);
            }

            return _file.createNewFile();
        }
        catch (IOException e)
        {
            throw new ResourceCreationFailedException("Couldn't create file:"
                    + path, e);
        }
    }

    public boolean delete() throws ResourceWriteAccessDeniedException,
            ResourceNotFoundException
    {
        // _File.delete doesn't provide much information
        // so precheck delete conditions:

        if (this._file.exists() == false)
        {
            throw new ResourceNotFoundException("File doesn't exist:" + this);
        }
        else if (this.isWritable() == false)
        {
            throw new ResourceWriteAccessDeniedException(
                    "No permissions to delete this file:" + this);
        }
        return _file.delete();
    }

    public VRL rename(String newname, boolean nameIsPath)
            throws VlException
    {
        File newFile = localfs.renameTo(this.getPath(), newname, nameIsPath);

        if (newFile!=null) 
        {
            return new VRL(newFile.toURI());   
        }
        
        return null;  
    }

    public long getLength() throws VlException
    {
        // do not cache 
        return _file.length(); 
        //return this.getStat().getSize(); 
    }

    public long getModificationTime() throws VlException
    {
        return this.getStat().getModTime(); 
    }

    public boolean isHidden()
    {
        return _file.isHidden();
    }

    /** Local File is local */ 
    public boolean isLocal()
    {
        return true;
    }

    public InputStream getInputStream() throws ResourceNotFoundException,
            ResourceReadAccessDeniedException
    {
        try
        {
            return new FileInputStream(this._file);
        }
        catch (FileNotFoundException e)
        {
            if ((this.exists() == true) && (this.isReadable() == false))
            {
                throw new ResourceReadAccessDeniedException(
                        "Could not read file:" + this, e);
            }

            throw new ResourceNotFoundException("File not found:" + this, e);
        }
    }

    public OutputStream getOutputStream()
            throws ResourceReadAccessDeniedException, VlException
    {
        return getOutputStream(false);
    }

    public OutputStream getOutputStream(boolean append) throws VlException
    {
        try
        {
            return new FileOutputStream(this._file, append);
        }
        catch (FileNotFoundException e)
        {
            if (_file.canWrite() == false)
                throw new ResourceReadAccessDeniedException(
                        "No write permissions for:" + this, e);
            else
                throw new ResourceNotFoundException(
                        "File not found or open error for:" + this, e);
        }
    }

    // Method from VRandomAccessable:
    public void setLength(long newLength) throws VlException
    {
        RandomAccessFile afile = null;

        try
        {
            afile = new RandomAccessFile(this._file, "rw");
            afile.setLength(newLength);
            this.statInf=null; 
            return;
        }
        catch (FileNotFoundException e)
        {
            throw new ResourceNotFoundException(
                    "Could open location for reading:" + this, e);
        }
        catch (IOException e)
        {
            throw new VlIOException("Could open location for reading:" + this,
                    e);
        }

    }

    // Method from VRandomAccessable:
    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset,
            int nrBytes) throws VlException
    {
        RandomAccessFile afile = null;

        try
        {
            afile = new RandomAccessFile(this._file, "r");
            afile.seek(fileOffset);
            int nrRead = afile.read(buffer, bufferOffset, nrBytes);

            // MUST CLOSE !!!
            afile.close();

            return nrRead;
        }
        catch (FileNotFoundException e)
        {
            throw new ResourceNotFoundException(
                    "Could open location for reading:" + this, e);
        }
        catch (IOException e)
        {
            throw new VlIOException("Could open location for reading:" + this,
                    e);
        }

    }

    // Method from VRandomAccessable:
    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset,
            int nrBytes) throws VlException
    {
        RandomAccessFile afile = null;

        try
        {
            afile = new RandomAccessFile(this._file, "rw");
            afile.seek(fileOffset);
            afile.write(buffer, bufferOffset, nrBytes);
            afile.close(); // MUST CLOSE !
            // if (truncate)
            // afile.setLength(fileOffset+nrBytes);
            return;// if failed, some exception occured !
        }
        catch (FileNotFoundException e)
        {
            throw new ResourceNotFoundException(
                    "Could open location for reading:" + this, e);
        }
        catch (IOException e)
        {
            throw new VlIOException("Could open location for reading:" + this,
                    e);
        }

    }

    public void setLengthToZero() throws VlException
    {
        setLength(0);
        sync();
    }

    @Override
    public String getSymbolicLinkTarget() throws VlException
    {
        if (isSymbolicLink() == false)
        {
            logger.debugPrintf("*** WARNING: getLinkTarget:not a link:%s\n",this);
            return null;
        }

        // windows lnk or shortcut (.lnk under *nix is also windows link!)
        if ((Global.isWindows()) || (getPath().endsWith(".lnk")))
            return localfs.getWindowsLinkTarget(this._file);
        else if (localfs.isUnixFS())
            return localfs.getSoftLinkTarget(this.getPath());

        logger.debugPrintf("*** WARNING: getLinkTarget: could not resolv local filesystem's link:%s\n",this);

        return null;
    }

    public boolean isSymbolicLink() throws VlException
    {
        // only Ux style soft links supported. 
        return this.getStat().isUxSofLink(); 
    };

    public void setMode(int mode) throws VlException
    {
        this.localfs.setMode(getPath(), mode);
    }

    public boolean sync()
    {
        this.statInf=null;
        return true; 
    }
    
    public String getChecksum(String algorithm) throws VlException
    {
        String[] types = getChecksumTypes();
        for (int i = 0; i < types.length; i++)
        {
            if (algorithm.equalsIgnoreCase(types[i]))
            {
                InputStream in = this.getInputStream();
                algorithm = algorithm.toUpperCase();
                return ChecksumUtil.calculateChecksum(in, algorithm);
            }
        }
        throw new nl.uva.vlet.exception.NotImplementedException(algorithm
                + " Checksum algorithm is not implemented ");

    }

    public String[] getChecksumTypes()
    {
        return new String[] { VChecksum.MD5, VChecksum.ADLER32 };
    }

    public String getGid() throws VlException
    {
        return this.getStat().getGroupName(); 
    }

    public String getUid() throws VlException
    {
        return this.getStat().getUserName(); 
    }

    public int getMode() throws VlException
    {
        return this.getStat().getMode(); 
    }
    
    public String getPermissionsString() throws VlException
    {
        return this.getStat().getPermissions();  
    }

    protected void setStatInfo(StatInfo stat)
    {
        this.statInf=stat; 
    }
  
}
