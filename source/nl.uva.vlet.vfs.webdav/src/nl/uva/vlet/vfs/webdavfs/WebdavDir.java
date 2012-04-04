package nl.uva.vlet.vfs.webdavfs;

import java.util.ArrayList;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

/**
 * WebdavDir
 * 
 * @author S. Koulouzis
 */
public class WebdavDir extends VDir
{

    private static ClassLogger logger;

    private DavPropertySet davPropSet;

    private WebdavFileSystem webdavFSystem;

    static
    {
        logger = ClassLogger.getLogger(WebdavDir.class);
        logger.setLevelToDebug();
    }

    /**
     * Creates a WebdavDir
     * 
     * @param webdavFSystem
     *            the file system
     * @param vrl
     *            the vrl pointing of the resource
     * @param davPropSet
     *            the property set
     */
    public WebdavDir(VFileSystem webdavFSystem, VRL vrl, DavPropertySet davPropSet)
    {
        super(webdavFSystem, vrl);
        this.davPropSet = davPropSet;
        this.webdavFSystem = (WebdavFileSystem) webdavFSystem;
    }

    @Override
    public VFSNode[] list() throws VlException
    {

        ArrayList<VFSNode> nodes = webdavFSystem.propFind(getVRL(), DavConstants.PROPFIND_ALL_PROP_INCLUDE,
                DavConstants.DEPTH_1);

        // get rid of this node
        nodes.remove(0);

        VFSNode[] nodesArray = new VFSNode[nodes.size()];
        nodesArray = nodes.toArray(nodesArray);
        return nodesArray;
    }

    @Override
    public boolean create(boolean ignoreExisting) throws VlException
    {
        VDir dir = webdavFSystem.createDir(getVRL(), ignoreExisting);

        return (dir != null);
    }

    // @Override
    // public WebdavFile createFile(String fileName,boolean ignoreExisting)
    // throws VlException
    // {
    // WebdavFile file = webdavFSystem.createFile(getVRL().append(fileName),
    // ignoreExisting);
    //        
    // return file;
    // }

    @Override
    public boolean exists() throws VlException
    {
        ArrayList<VFSNode> result = webdavFSystem.propFind(getVRL(), DavConstants.PROPFIND_PROPERTY_NAMES,
                DavConstants.DEPTH_0);

        return (result != null && !result.isEmpty());
    }

    @Override
    public long getModificationTime() throws VlException
    {
        String modstr = "" + davPropSet.get(DavConstants.PROPERTY_GETLASTMODIFIED).getValue();

        return webdavFSystem.createDateFromString(modstr).getTime();
    }

    @Override
    public boolean isReadable() throws VlException
    {
        // davPropSet.get(DavConstants.property_);
        return false;
    }

    @Override
    public boolean isWritable() throws VlException
    {
        webdavFSystem.getACL(getVRL());

        return false;
    }

    @Override
    public VRL rename(String newNameOrPath, boolean nameIsPath) throws VlException
    {
        VRL destination = null;
        if (nameIsPath || (newNameOrPath.startsWith("/")))
        {
            destination = getVRL().copyWithNewPath(newNameOrPath);
        }
        else
        {
            destination = getVRL().getParent().append(newNameOrPath);
        }

        return webdavFSystem.move(getVRL(), destination, false);
    }

    public long getNrOfNodes() throws VlException
    {
        ArrayList<VFSNode> result = webdavFSystem.propFind(getVRL(), DavConstants.PROPFIND_ALL_PROP_INCLUDE,
                DavConstants.DEPTH_1);

        // get rid of this node
        result.remove(0);

        return result.size();
    }

    @Override
    public boolean delete(boolean recurse) throws VlException
    {
        return webdavFSystem.delete(getVRL(), recurse);
    }

}
