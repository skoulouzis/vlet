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
 * $Id: GftpDir.java,v 1.3 2011-06-07 14:31:44 ptdeboer Exp $  
 * $Date: 2011-06-07 14:31:44 $
 */
// source: 

package nl.uva.vlet.vfs.gftp;

import java.util.Vector;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;

import org.globus.ftp.MlsxEntry;

/**
 * Implementation of GftpDir
 * 
 * @author P.T. de Boer
 */
public class GftpDir extends VDir
{
    // private GFTP handler object to this resource.
    // private GridFTPClient gftpClient = null;

    private MlsxEntry _entry = null;

    // Package protected !:
    GftpFileSystem server = null;

    /**
     * @param client
     * @throws VlException
     */
    GftpDir(GftpFileSystem server, String path, MlsxEntry entry) throws VlException
    {
        super(server, server.getServerVRL().copyWithNewPath(path));
        init(server, path, entry);
    }

    GftpDir(GftpFileSystem server, String path) throws VlException
    {
        this(server, path, null);
    }

    private void init(GftpFileSystem server, String path, MlsxEntry entry) throws VlException
    {
        this._entry = entry;
        this.server = server;
    }

    @Override
    public boolean exists()
    {
        return server.existsDir(this.getPath());
    }

    public boolean create(boolean force) throws VlException
    {
        VDir dir = this.server.createDir(getPath(), force);
        updateEntry();
        return (dir != null);
    }

    /**
     * Reload MLST entry
     * 
     * @throws VlException
     */
    private MlsxEntry updateEntry() throws VlException
    {
        this._entry = this.server.mlst(getPath());
        return _entry;
    }

    @Override
    public boolean isReadable() throws VlException
    {
        return GftpFileSystem._isReadable(getMlsxEntry());
    }

    @Override
    public boolean isAccessable() throws VlException
    {
        return GftpFileSystem._isAccessable(getMlsxEntry());
    }

    @Override
    public boolean isWritable() throws VlException
    {
        return GftpFileSystem._isWritable(getMlsxEntry());
    }

    @Override
    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        String path = server.rename(this.getPath(), newName, nameIsPath);
        return this.resolvePathVRL(path);
    }

    public VDir getParentDir() throws VlException
    {
        return server.getParentDir(this.getPath());
    }

    public long getNrOfNodes()
    {
        try
        {
            Object list[] = list();

            if (list != null)
                return list.length;
        }
        catch (VlException e)
        {
            ;
        }

        return 0;
    }

    public VFSNode[] list() throws VlException
    {
        Vector<?> list = null;

        String path = this.getPath();

        list = server.mlsd(path);

        if (list == null)
            return null;

        Vector<VFSNode> nodes = new Vector<VFSNode>();

        for (Object o : list)
        {
            MlsxEntry entry = ((MlsxEntry) o);
            String name = entry.getFileName();
            name = VRL.basename(name);
            // Debug("fileOnfo=" + fileInfo);

            String remotePath = path + "/" + name;

            if (GftpFileSystem._isFile(entry))
                nodes.add(new GftpFile(server, remotePath, entry));
            else if (GftpFileSystem._isXDir(entry))
            {
                // Skip '.' and '..'
                // nodes[j] = null; // new GftpDir(server,remotePath,entry);
                ;
            }
            else if (GftpFileSystem._isDir(entry))
                nodes.add(new GftpDir(server, remotePath, entry));
            /*
             * else if (fileInfo.isSoftLink()) nodes[i] = new
             * GftpFile(server,remotePath,fileInfo);
             */
            else
            {
                // DEFAULT: add as file could be anything (link?)
                nodes.add(new GftpFile(server, remotePath, entry));
                // ; // nodes[j] = null;
            }
        }

        VFSNode nodeArray[] = new VFSNode[nodes.size()];
        nodeArray = nodes.toArray(nodeArray);

        return nodeArray;
    }

    public boolean delete(boolean recurse) throws VlException
    {
        ITaskMonitor monitor = ActionTask.getCurrentThreadTaskMonitor("Deleting (GFTP) directory:" + this.getPath(), 1);

        // Delete children first:
        if (recurse == true)
            VDir.defaultRecursiveDeleteChildren(monitor, this);

        return server.delete(true, this.getPath());
    }

    /** Check if directory has child */
    public boolean existsFile(String name) throws VlException
    {
        String newPath = resolvePath(name);
        return server.existsFile(newPath);
    }

    public boolean existsDir(String dirName) throws VlException
    {
        String newPath = resolvePath(dirName);
        return server.existsDir(newPath);
    }

    public long getModificationTime() throws VlException
    {
        // doesnot work for directories: return
        // server.getModificationTime(this.getPath());
        return -1;
    }

    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (this.server.protocol_v1)
            return superNames;

        return StringList.merge(superNames, GftpFSFactory.gftpAttributeNames);
    }

    @Override
    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        if (names == null)
            return null;

        VAttribute attrs[] = new VAttribute[names.length];

        // Optimized getAttribute: use single entry for all
        MlsxEntry entry = this.getMlsxEntry();

        for (int i = 0; i < names.length; i++)
        {
            attrs[i] = getAttribute(entry, names[i]);
        }

        return attrs;
    }

    @Override
    public VAttribute getAttribute(String name) throws VlException
    {
        return getAttribute(this.getMlsxEntry(), name);
    }

    /**
     * Optimized method. When fetching multiple attributes, do not refetch the
     * mlsxentry for each attribute.
     * 
     * @param name
     * @param update
     * @return
     * @throws VlException
     */
    public VAttribute getAttribute(MlsxEntry entry, String name) throws VlException
    {
        // is possible due to optimization:

        if (name == null)
            return null;

        // get Gftp specific attribute and update
        // the mslxEntry if needed

        VAttribute attr = GftpFileSystem.getAttribute(entry, name);

        if (attr != null)
            return attr;

        return super.getAttribute(name);
    }

    public MlsxEntry getMlsxEntry() throws VlException
    {
        if (_entry == null)
            _entry = updateEntry();
        return _entry;
    }

}
