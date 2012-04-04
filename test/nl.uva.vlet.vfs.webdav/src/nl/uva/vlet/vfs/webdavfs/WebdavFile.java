package nl.uva.vlet.vfs.webdavfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;

import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;

/**
 * WebdavFile
 * 
 * @author S. Koulouzis
 */
public class WebdavFile extends VFile
{

    private static ClassLogger logger;

    private DavPropertySet props;

    private WebdavFileSystem webdavFSystem;

    static
    {
        logger = ClassLogger.getLogger(WebdavFile.class);
        logger.setLevelToDebug();
    }

    /**
     * Creates a WebdavFile
     * 
     * @param webdavFSystem
     *            the file system
     * @param vrl
     *            the vrl pointing of the resource
     * @param davPropSet
     *            the property set
     */
    public WebdavFile(WebdavFileSystem webdavFSystem, VRL vrl, DavPropertySet props)
    {
        super(webdavFSystem, vrl);
        this.props = props;
        this.webdavFSystem = webdavFSystem;
    }

    @Override
    public boolean exists() throws VlException
    {
        ArrayList<VFSNode> result = webdavFSystem.propFind(getVRL(), DavConstants.PROPFIND_PROPERTY_NAMES,
                DavConstants.DEPTH_0);
        boolean exists = (result != null && !result.isEmpty());
        if (exists)
        {
            WebdavFile file = (WebdavFile) result.get(0);
            this.props = file.props;
        }

        return exists;
    }

    @Override
    public long getLength() throws VlException
    {
        props = webdavFSystem.getProperties(getVRL());
        String StyLen;
        DavProperty<?> prop = props.get(DavConstants.PROPERTY_GETCONTENTLENGTH);

        prop = props.get(DavConstants.PROPERTY_GETCONTENTLENGTH);

        if (prop == null)
        {
            StyLen = "0";
        }
        else
        {
            StyLen = "" + prop.getValue();
        }

        return new Long(StyLen);

    }

    @Override
    public boolean create(boolean ignoreExisting) throws VlException
    {

        VFile file = webdavFSystem.createFile(getVRL(), ignoreExisting);

        return (file != null);
    }

    @Override
    public long getModificationTime() throws VlException
    {
        String modstr = "" + props.get(DavConstants.PROPERTY_GETLASTMODIFIED).getValue();
        return webdavFSystem.createDateFromString(modstr).getTime();
    }

    @Override
    public boolean isReadable() throws VlException
    {
        return false;
    }

    @Override
    public boolean isWritable() throws VlException
    {
        // TODO Auto-generated method stub
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

    @Override
    public InputStream getInputStream() throws VlException
    {
        return webdavFSystem.getInputStream(getVRL());
    }

    @Override
    public OutputStream getOutputStream() throws VlException
    {

        return webdavFSystem.getOutputStream(getVRL());
    }

    @Override
    public boolean delete() throws VlException
    {
        return webdavFSystem.delete(getVRL(), true);
    }

}
