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
 * $Id: File.java,v 1.7 2011-06-07 16:12:50 ptdeboer Exp $  
 * $Date: 2011-06-07 16:12:50 $
 */
// source: 

package nl.uva.vlet.io.javafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrl.VRLUtil;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRenamable;

/**
 * The nl.uva.vlet.ui.javafile.File is a java.io.File equivalent class. Most
 * methods are the same except that for path strings VRL or URI strings can be
 * used. Resolving relative paths might be different as URI resolving mechanisms
 * are used.
 * <p>
 * The java.io.File class is nothing more than a class specifying a path on the
 * local file system. This class does the same except it wraps around an URI (or
 * VRL in this case). <br>
 * Implementation notes: <br>
 * Setting File permissions are currently not implemented as are file system
 * space methods.<br>
 * Not all methods and possible combinations have been tested.<br>
 * <p>
 * The classes in this package are under construction and any volunteer willing
 * to test this class is welcome.
 * 
 * @author Piter T. de Boer
 */
public class File implements Serializable, Comparable<File>, Cloneable
{
    private static final long serialVersionUID = -5564471676154170368L;

    /**
     * VRL Separator char, uses URI separator ! (Forward Slash) Separator chars
     * will be translated to local (OS depended) separator if needed.
     */
    public static final char separatorChar = VRL.SEP_CHAR;

    public static final String separator = "" + separatorChar;

    /**
     * Normalized VRL path separator, uses colon ":" as default, will be
     * translated to local OS depended path separator char if needed.
     */
    public static final char pathSeparatorChar = ':';

    public static final String pathSeparator = "" + pathSeparatorChar;

    // static VFS Client for global file access !
    private static VFSClient staticVFS;

    private static Random randomizer = new Random(System.currentTimeMillis());

    /**
     * Specify global VFSClient which is used as VFSNode factory,. Only use
     * setStaticVFSClient() once at the startup of your application.
     */
    public static synchronized void setStaticVFSClient(VFSClient vfsClient)
    {
        staticVFS = vfsClient;
    }

    /**
     * Returns global VFSClient used to access the VFS. The wrapper class
     * javafile.File doesn't hold any state regarding the VFSClient nor does is
     * keep a cached copy of VFSClient. This because the java.io.File is just an
     * 'abstract' file which is nothing more than a path or in this case a (VRL)
     * location object. <br>
     * Each method which needs the VFSClient call this methods. <br>
     * If a setStaticVFSClient() is called between javafile.File methods
     * different results could be returned depending of the state of the
     * VFSClient and/or VRSContext associated witht the VFSClient. Only use
     * setStaticVFSClient() once at the startup of your application. It is used
     * as global VFSNode factory.
     */
    public static synchronized VFSClient getStaticVFSClient()
    {
        // lazy initialization !
        if (staticVFS == null)
        {
            staticVFS = new VFSClient(VFS.getDefaultVRSContext());
        }

        return staticVFS;
    }

    /** Convertor method to convert anarray of VNodes to Files */
    public static File[] toFiles(VNode[] childs)
    {
        File files[] = new File[childs.length];

        for (int i = 0; i < files.length; i++)
        {
            files[i] = new File(childs[i].getVRL());
        }

        return files;
    }

    // ====================================================================
    // Instance
    // ====================================================================

    /** Is not muteable ! */
    private final VRL location;

    /**
     * Constructs (abstract) File from relative or absolute path or URI
     * location.
     * 
     * @param pathname
     *            A location string relative or absolute URI/VRL
     * @throws NullPointerException
     *             If the <code>pathname</code> argument is <code>null</code>
     */
    public File(String pathOrURI)
    {
        if (pathOrURI == null)
        {
            throw new NullPointerException();
        }

        // Warning: can be relative !
        try
        {
            this.location = new VRL(pathOrURI);
        }
        catch (VRLSyntaxException e)
        {
            throw new Error("URISyntax Exception for:" + pathOrURI, e);
        }
    }

    /** Resolve optional relative path against current working directory */
    public VRL cwdResolve(String name)
    {
        try
        {
            return getStaticVFSClient().getWorkingDir().resolvePath(name);
        }
        catch (VRLSyntaxException e)
        {
            throw new Error("URI Syntax exception", e);
        }
    }

    /**
     * Tries to resolve child location against parent location. If parent==null
     * then child location is used "as is".
     * 
     * @param parent
     *            The parent location string (VRL or URI)
     * @param child
     *            The child location string (VRL or URI)
     * @throws NullPointerException
     *             If <code>child</code> is <code>null</code>
     */
    public File(String parent, String child)
    {
        if (child == null)
        {
            throw new NullPointerException();
        }

        try
        {
            if (parent != null)
            {
                VRL parentVrl = new VRL(parent);
                this.location = parentVrl.resolvePath(child);
            }
            else
            {
                // can be relative !
                this.location = new VRL(child);
            }
        }
        catch (VRLSyntaxException e)
        {
            throw new Error("JavaFile URI Exception:" + e.getMessage(), e);
        }
    }

    /**
     * Creates a new <code>File</code> instance from a parent location and a
     * child location string.
     * 
     * <p>
     * If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> location string.
     * 
     * @param parent
     *            The parent File
     * @param child
     *            The child pathname string
     * @throws NullPointerException
     *             If <code>child</code> is <code>null</code>
     */
    public File(File parent, String child)
    {
        if (child == null)
        {
            throw new NullPointerException();
        }

        try
        {

            if (parent != null)
            {
                this.location = parent.getAbsoluteVRL().resolvePath(child);
            }

            else
            {
                this.location = new VRL(child);
            }
        }
        catch (VRLSyntaxException e)
        {
            throw new Error("URI Syntax exception for resolving:'" + this + "' + '" + child + "'", e);
        }
    }

    /** Resolve relative location against this file's location */
    public VRL resolve(String child)
    {
        try
        {
            return getAbsoluteVRL().resolvePath(child);
        }
        catch (VRLSyntaxException e)
        {
            throw new Error("JavaFile URI Exception:" + e.getMessage(), e);
        }
    }

    /** Construct abstract file from specified URI */
    public File(URI uri)
    {
        this.location = new VRL(uri);
    }

    /** Construct abstract file from specified VRL */
    public File(VRL vrl)
    {
        this.location = vrl.duplicate();
    }

    /** Construct abstract File from VNode. */
    public File(VNode node)
    {
        this.location = node.getVRL().duplicate();
    }

    /** Returns last part of path or basename part of VRL. */
    public String getName()
    {
        return this.location.getBasename();
    }

    /** Return parents directory name or dirname part of VRL. */
    public String getParent()
    {
        String str = this.location.getDirname();
        if (StringUtil.isEmpty(str))
            return null; // stay compatible with java.o.File
        return str;
    }

    /** Return parent VRL of this location. */
    public VRL getParentVRL()
    {
        return this.location.getParent();
    }

    /** Get parent directory of this file location. Doesn' resolve the location. */
    public File getParentFile()
    {
        String parentPath = VRL.dirname(location.getPath());
        // java.io.File compatibility: parent of relative file is NULL

        if (StringUtil.isEmpty(parentPath))
            return null;

        // could be relative
        return new File(parentPath);
    }

    /** Returns relative or absolute path part of VRL */
    public String getPath()
    {
        return location.getPath();
    }

    /**
     * Returns path as string array where each element is a path element
     * matching the (sub) directory name of the path without any slash or
     * backslash.
     */
    public String[] getPathElements()
    {
        return location.getPathElements();
    }

    /**
     * Whether location is absolute. Method calls VRL.isAbsolute() of VRL
     * location of this File.
     */
    public boolean isAbsolute()
    {
        return location.isAbsolute();
    }

    /** Returns resolved path of File VRL */
    public String getAbsolutePath()
    {
        return getAbsoluteVRL().getPath();
    }

    /**
     * Returns resolved File VRL. Since there is no difference in VRLs (nor
     * URIs) between canonical and absolute paths a relative paths will always
     * be matched against the current working directory and not between OS
     * specific 'Drives' or paths like "c:<relative path>".
     */
    public VRL getAbsoluteVRL()
    {
        if (location.isAbsolute() == false)
        {
            return cwdResolve(location.getPath());
        }
        else
        {
            return location;
        }
    }

    /**
     * Returns resolved JavaFile. Calls new JavaFile(getAbsoluteVRL());
     */
    public File getAbsoluteFile()
    {
        return new File(this.getAbsoluteVRL());
    }

    /**
     * Returns the canonical pathname string of this abstract pathname. resolves
     * VRL and returns paths. This method behaves the same as getAbsolutePath()
     * since for VRLs there is no difference between canonical and absolute
     * VRLs.
     */
    public String getCanonicalPath() throws IOException
    {
        return getCanonicalVRL().getPath();
    }

    /**
     * Returns the resolved absolute VRL. Similar to getAbsoluteVRL() since for
     * VRLs there is no difference between canonical and absolute VRLs.
     */
    public VRL getCanonicalVRL() throws IOException
    {
        return getAbsoluteVRL();
    }

    /**
     * Returns canonicalFile. Similar to getAbsoluteFile() since for VRLs there
     * is no difference between canonical and absolute VRLs.
     */
    public File getCanonicalFile() throws IOException
    {
        return new File(this.getCanonicalVRL());
    }

    public URL toURL() throws MalformedURLException
    {
        return this.toURI().toURL();
    }

    /**
     * Returns URI of this file. If the file can be resolved to an (existing)
     * directory, the URI has a slash ('/') appended.
     */
    public URI toURI()
    {
        try
        {
            if (isDirectory())
                return VRLUtil.toDirURL(getAbsoluteVRL()).toURI();// append '/'
            else
                return getAbsoluteVRL().toURI();
        }
        catch (Exception e)
        {
            throw new Error("JavaFile URI Exception:" + e.getMessage(), e);
        }
    }

    /**
     * Tests whether this location is readable by the current user.
     * 
     * @throws Error
     *             if the location doesn't exist, or some other exception
     *             occurred.
     */
    public boolean canRead()
    {
        try
        {
            VFSNode node = getStaticVFSClient().getVFSNode(getAbsoluteVRL());
            return node.isReadable();
        }
        catch (Exception e)
        {
            throw new Error("canRead(): Couldn't access location:" + location, e);
        }
    }

    /**
     * Tests whether this location is writable by the current user.
     * 
     * @throws Error
     *             if the location doesn't exist, or some other exception
     *             occurred.
     */
    public boolean canWrite()
    {
        try
        {
            VFSNode node = getStaticVFSNode();
            return node.isWritable();
        }
        catch (Exception e)
        {
            throw new Error("canWrite(): Couldn't access location:" + location, e);
        }
    }

    /**
     * Tests whether the path denoted by this abstract location is a file or
     * directory and it exists.
     * 
     * @throws Error
     *             if it couldn't be determined whether the path exists or not.
     */
    public boolean exists()
    {
        try
        {
            // need Exists !
            if (getStaticVFSClient().existsPath(getAbsoluteVRL()))
                return true;
            return false;
        }
        catch (Exception e)
        {
            throw new Error("exists(): Couldn't access location:" + location, e);
        }
    }

    /**
     * Tests whether the directory denoted by this abstract location is a normal
     * directory and it exists.
     * 
     * @throws Error
     *             if it couldn't be determined whether the directory exists or
     *             not.
     */
    public boolean isDirectory()
    {
        try
        {
            if (getStaticVFSClient().existsDir(getAbsoluteVRL()))
                return true;
            return false;
        }
        catch (Exception e)
        {
            throw new Error("Couldn't access location:" + location, e);
        }
    }

    /**
     * Tests whether the file denoted by this abstract location is a normal file
     * and exists.
     * 
     * @throws Error
     *             if it couldn't be determined whether the file exists or not.
     */
    public boolean isFile()
    {
        try
        {
            if (getStaticVFSClient().existsFile(getAbsoluteVRL()))
                return true;
            return false;
        }
        catch (Exception e)
        {
            throw new Error("Couldn't access:" + location, e);
        }
    }

    public boolean isHidden()
    {
        return false;
    }

    public long lastModified()
    {
        try
        {
            VFSNode node = getStaticVFSNode(); // getStaticVFSClient().getVFSNode(this.location);
            return node.getModificationTime();
        }
        catch (Exception e)
        {
            throw new Error("Couldn't access:" + location, e);
        }
    }

    /**
     * Returns the length of the file denoted by this abstract pathname. Returns
     * 0 if this location denotes a directory.
     */
    public long length()
    {
        try
        {
            VNode node = getStaticVNode();
            if (node instanceof VFile)
            {
                return ((VFile) node).getLength();
            }

            return 0;
        }
        catch (Exception e)
        {
            throw new Error("Couldn't access:" + location, e);
        }
    }

    /** Create new File. Ignores optional existing file. */
    public boolean createNewFile() throws IOException
    {
        try
        {
            VFile vfile = getStaticVFSClient().newFile(this.location);
            return vfile.create(true);
        }
        catch (Exception e)
        {
            throw VlException.createIOException("Couldn't create new file:" + location, e);
        }
    }

    /** Deletes file. */
    public boolean delete()
    {
        try
        {
            VFile vfile = getStaticVFSClient().newFile(getAbsoluteVRL());
            return vfile.delete();
        }
        catch (Exception e)
        {
            throw new Error("Couldn't delete file:" + location, e);
        }
    }

    /**
     * @deprecated Not Implemented ! Deletion of remote files can take forever...
     */
    public void deleteOnExit()
    {
        throw new Error("Not implemented");
    }

    /**
     * Returns list of child if this file is a directory. Returns NULL if this
     * file is not a directory
     * 
     * @return
     */
    public String[] list()
    {
        try
        {
            VNode[] nodes = this.listNodes();
            if (nodes == null)
                return null;

            String list[] = new String[nodes.length];

            for (int i = 0; i < list.length; i++)
            {
                list[i] = nodes[i].getBasename();
            }

            return list;
        }
        catch (Exception e)
        {
            throw new Error("Couldn't list location:" + location, e);
        }
    }

    public VNode[] listNodes() throws IOException
    {
        try
        {
            VNode node = getStaticVNode();
            if (node instanceof VComposite)
            {
                return ((VComposite) node).getNodes();
            }
            return null;
        }
        catch (Exception e)
        {
            throw VlException.createIOException("Couldn't list location:" + location, e);
        }
    }

    /** Return contents of directory. */
    public String[] list(FilenameFilter filter)
    {
        String names[] = list();
        if ((names == null) || (filter == null))
        {
            return names;
        }
        ArrayList<String> v = new ArrayList<String>();
        for (int i = 0; i < names.length; i++)
        {
            if (filter.accept(this, names[i]))
            {
                v.add(names[i]);
            }
        }
        return (String[]) (v.toArray(new String[v.size()]));
    }

    /** Return contents of directory. */
    public File[] listFiles()
    {
        VNode nodes[];
        try
        {
            nodes = listNodes();
        }
        catch (IOException e)
        {
            throw new Error("Couldn't read contents of:" + location, e);
        }
        if (nodes == null)
            return null;

        return toFiles(nodes);
    }

    /**
     * Returns filtered contents of this location is this location is a
     * directory. Returns NULL otherwise.
     */
    public File[] listFiles(FilenameFilter filter)
    {
        String ss[] = list();
        if (ss == null)
            return null;

        ArrayList<File> v = new ArrayList<File>();

        for (int i = 0; i < ss.length; i++)
        {
            if ((filter == null) || filter.accept(this, ss[i]))
            {
                try
                {
                    v.add(new File(getAbsoluteVRL().resolve(ss[i])));
                }
                catch (VRLSyntaxException e)
                {
                    throw new Error("URI Syntax exception :" + ss[i], e);
                }
            }
        }
        return (File[]) (v.toArray(new File[v.size()]));
    }

    public File[] listFiles(FileFilter filter)
    {
        String ss[] = list();
        if (ss == null)
            return null;
        ArrayList<File> v = new ArrayList<File>();
        for (int i = 0; i < ss.length; i++)
        {
            File f;
            try
            {
                f = new File(getAbsoluteVRL().resolve(ss[i]));
            }
            catch (VRLSyntaxException e)
            {
                throw new Error("URI Syntax exception :" + ss[i], e);
            }
            if ((filter == null) || filter.accept(f))
            {
                v.add(f);
            }
        }

        return (File[]) (v.toArray(new File[v.size()]));
    }

    /**
     * Creates the directory named by this abstract location.
     */
    public boolean mkdir()
    {
        try
        {
            return (getStaticVFSClient().mkdir(getAbsoluteVRL()) != null);
        }
        catch (Exception e)
        {
            throw new Error("Couldn't delete file:" + location, e);
        }
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories. Note that if this operation
     * fails it may have succeeded in creating some of the necessary parent
     * location.
     */
    public boolean mkdirs()
    {
        try
        {
            return (getStaticVFSClient().mkdirs(getAbsoluteVRL()) != null);
        }
        catch (Exception e)
        {
            throw new Error("Couldn't delete file:" + location, e);
        }
    }

    /**
     * Renames the file denoted by this abstract pathname.
     */
    public boolean renameTo(File dest)
    {
        return (rename(dest) != null);
    }

    public VRL rename(File dest)
    {
        if (VRLUtil.hasSameServer(this.getAbsoluteVRL(), dest.getAbsoluteVRL()) == false)
        {
            throw new Error("Cannot rename file which are on different filesystems!:" + this);
        }

        try
        {
            VNode node = getStaticVFSClient().getNode(this.location);
            if (node instanceof VRenamable)
            {
                return ((VRenamable) node).rename(dest.getAbsoluteVRL().getPath(), true);
            }

            throw new Error("Location is not renamable:" + location);

        }
        catch (Exception e)
        {
            throw new Error("Couldn't rename location:" + location, e);
        }
    }

    /** @deprecated Not Implemented ! */
    public boolean setLastModified(long time)
    {
        if (time < 0)
            throw new IllegalArgumentException("Negative time");

        throw new Error("Not implemented");
    }

    /** @deprecated Not Implemented ! */
    public boolean setReadOnly()
    {
        throw new Error("Not Implemented!");
    }

    /** @deprecated Not Implemented ! */
    public boolean setWritable(boolean writable, boolean ownerOnly)
    {
        throw new Error("Not Implemented!");
    }

    /** @deprecated Not Implemented ! */
    public boolean setWritable(boolean writable)
    {
        throw new Error("Not Implemented!");
    }

    /** @deprecated Not Implemented ! */
    public boolean setReadable(boolean readable, boolean ownerOnly)
    {
        throw new Error("Not Implemented!");
    }

    /** @deprecated Not Implemented ! */
    public boolean setReadable(boolean readable)
    {
        return setReadable(readable, true);
    }

    /** @deprecated Executables do not makes sense in distributed environment */
    public boolean setExecutable(boolean executable, boolean ownerOnly)
    {
        return false;
    }

    /** @deprecated Executables do not makes sense in a distributed environment */
    public boolean setExecutable(boolean executable)
    {
        return false;
    }

    /** @deprecated Executables do not makes sense in a distributed environment */
    public boolean canExecute()
    {
        return false;
    }

    /** Lists virtual roots. These are the child nodes from "My Vle" */
    public static File[] listRoots()
    {
        try
        {
            VNode vnode = getStaticVFSClient().getVRSContext().getVirtualRoot();
            VNode childs[] = ((VComposite) vnode).getNodes();
            return toFiles(childs);
        }
        catch (Exception e)
        {
            throw new Error("Couldn't get nodes from rootnode", e);
        }
    }

    /** @deprecated Not implemented ! Virtual file systems don't have space ! */
    public long getTotalSpace()
    {
        return 0;
    }

    /** @deprecated Not implemented ! Virtual file systems don't have space ! */
    public long getFreeSpace()
    {
        return 0;
    }

    /** @deprecated Not implemented ! Virtual file systems don't have space ! */
    public long getUsableSpace()
    {
        return 0;
    }

    /**
     * <p>
     * Creates a new empty file in the specified directory, using the given
     * prefix and suffix strings to generate its name.
     */
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException
    {
        if (prefix == null)
            prefix = "vtempFile";

        if (suffix == null)
            suffix = ".tmp";

        VFSClient vfs = getStaticVFSClient();

        VDir tempDir = null;
        try
        {
            if (directory != null)
            {
                tempDir = vfs.getDir(directory.getAbsoluteVRL());
            }
            else
            {
                tempDir = vfs.getTempDir();
            }

            VFile tmpFile = null;
            do
            {
                long n = randomizer.nextLong();
                if (n == Long.MIN_VALUE)
                {
                    n = 0; // corner case
                }
                else
                {
                    n = Math.abs(n);
                }

                VRL pathVRL = tempDir.resolvePathVRL(prefix + Long.toString(n) + suffix);
                tmpFile = vfs.newFile(pathVRL);
            } while (tmpFile.exists() == true);

            tmpFile.create();
            return new File(tmpFile);
        }
        catch (VlException e)
        {
            throw VlException.createIOException("Couldn't create new temp file in directory:" + directory, e);
        }

    }

    /**
     * Creates an empty file in the default temporary-file directory, using the
     * given prefix and suffix to generate its name. Invoking this method is
     * equivalent to invoking <code>{@link #createTempFile(java.lang.String,
     * java.lang.String, java.io.File)
     * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
     * 
     * @param prefix
     *            The prefix string to be used in generating the file's name;
     *            must be at least three characters long
     * 
     * @param suffix
     *            The suffix string to be used in generating the file's name;
     *            may be <code>null</code>, in which case the suffix
     *            <code>".tmp"</code> will be used
     * 
     * @return An abstract pathname denoting a newly-created empty file
     * 
     * @throws IllegalArgumentException
     *             If the <code>prefix</code> argument contains fewer than three
     *             characters
     * 
     * @throws IOException
     *             If a file could not be created
     * 
     * @throws SecurityException
     *             If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *             method does not allow a file to be created
     * 
     * @since 1.2
     */
    public static File createTempFile(String prefix, String suffix) throws IOException
    {
        return createTempFile(prefix, suffix, null);
    }

    /**
     * Test whether the absolute and normalized VRLs are equivalent. See
     * VRL.compareTo();
     */
    public int compareTo(File other)
    {
        return this.getAbsoluteVRL().compareTo(other.getAbsoluteVRL());
    }

    /**
     * Test whether the normalized VRLs are equivalent. See VRL.compareTo();
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof File))
        {
            return compareTo((File) obj) == 0;
        }

        return false;
    }

    /** Returns hashcode of absolute and normalized VRL: VRL.hashCode() */
    public int hashCode()
    {
        return this.getAbsoluteVRL().hashCode();
    }

    /** Return String representation of this location VRL. */
    public String toString()
    {
        return this.location.toString();
    }

    // ========================================================================
    // VRS/VFS Methods !
    // ========================================================================

    /**
     * Returns VRL of this File. Doesn't do any resolving if this file a
     * relative path ! use getAbsoluteVRL() to get resolved VRL
     */
    public VRL toVRL()
    {
        return this.location.duplicate();
    }

    /**
     * Open Location using specified context and returns VNode. To determine the
     * type of the VNode the location must point to an existing resource ! Use
     * <code>toVFile()</code> or <code>toVDir()</code> to explicitly choose the
     * type and instantiate an non existing File or Directory.
     */
    public VNode toNode(VRSContext context) throws VlException
    {
        return context.openLocation(getAbsoluteVRL());
    }

    // helper method to return EXISTING VFSNode.
    private VFSNode getStaticVFSNode()
    {
        VRL vrl = getAbsoluteVRL();
        try
        {
            return getStaticVFSClient().getVFSNode(vrl);
        }
        catch (VlException e)
        {
            throw new Error("Couldn't fetch VFSNode:" + vrl, e);
        }

    }

    // helper method to returns VNode
    private VNode getStaticVNode()
    {
        VRL vrl = getAbsoluteVRL();
        try
        {
            return getStaticVFSClient().openLocation(vrl);
        }
        catch (VlException e)
        {
            throw new Error("Couldn't fetch VNode:" + vrl, e);
        }

    }

    /**
     * Open Location using specified VRSContext and return context enabled VFile
     * !
     */
    public VFile toVFile(VRSContext context) throws VlException
    {
        return new VFSClient(context).newFile(this.getAbsoluteVRL());
    }

    /**
     * Open Location using specified VRSContext and returns context enabled VDir
     * ! If the location exsists but is not a directory an Exception might be
     * thrown.
     */
    public VDir toVDir(VRSContext context) throws VlException
    {
        return new VFSClient(context).newDir(getAbsoluteVRL());
    }

    /**
     * Convenience method to directly create an InputStream using the specified
     * VRSContext.
     * 
     * @throws VlException
     */
    public InputStream openInputStream(VRSContext context) throws VlException
    {
        return new VFSClient(context).openInputStream(getAbsoluteVRL());
    }

    /**
     * Convenience method to directly create an OutputStream using the specified
     * VRSContext.
     * 
     * @throws VlException
     */
    public OutputStream openOutputStream(VRSContext context) throws VlException
    {
        return new VFSClient(context).openOutputStream(getAbsoluteVRL());
    }

    /**
     * Convenience method to directly create an InputStream using the static
     * VFS.
     * 
     * @throws VlException
     */
    public InputStream openInputStream() throws VlException
    {
        return getStaticVFSClient().openInputStream(getAbsoluteVRL());
    }

    /**
     * Convenience method to directly create an OutputStream using the static
     * VFS.
     * 
     * @throws VlException
     */
    public OutputStream openOutputStream() throws VlException
    {
        return getStaticVFSClient().openOutputStream(getAbsoluteVRL());
    }

    public File clone()
    {
        return duplicate();
    }

    public File duplicate()
    {
        return new File(location);
    }
}
