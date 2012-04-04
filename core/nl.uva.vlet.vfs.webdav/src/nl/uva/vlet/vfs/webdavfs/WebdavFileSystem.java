package nl.uva.vlet.vfs.webdavfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeConstants;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlIOException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.presentation.Presentation;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.ResourceSystemNode;
import nl.uva.vlet.vrs.VRSContext;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.Status;
import org.apache.jackrabbit.webdav.client.methods.CopyMethod;
import org.apache.jackrabbit.webdav.client.methods.DeleteMethod;
import org.apache.jackrabbit.webdav.client.methods.MkColMethod;
import org.apache.jackrabbit.webdav.client.methods.MoveMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.client.methods.PutMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.xerces.dom.DeferredElementNSImpl;

/**
 * WebdavFileSystem
 * 
 * @author S. Koulouzis
 */
public class WebdavFileSystem extends FileSystemNode
{

    private static ClassLogger logger;

    private boolean connected;

    private HostConfiguration hostConfig;

    private MultiThreadedHttpConnectionManager connectionManager;

    private HttpConnectionManagerParams httpConnectionParams;

    private HttpClient client;

    private static final int MAX_HOST_CONNECTIONS = 30;

    private static final String COLLECTION = "collection";

    static
    {
        logger = ClassLogger.getLogger(WebdavFileSystem.class);
        logger.setLevelToDebug();
    }

    /**
     * Creates a WebdavFileSystem. Most of the interaction with a server happens
     * through this class
     * 
     * @param context
     * @param info
     * @param location 
     */
    public WebdavFileSystem(VRSContext context, ServerInfo info, VRL location)
    {
        super(context, info);
    }

    @Override
    public VFSNode openLocation(VRL vrl) throws VlException
    {
        connect();

        VRL newVRL = vrl;
        if (vrl.getPath().endsWith("~"))
        {
            newVRL = vrl.copyWithNewPath("/");
        }

        ArrayList<VFSNode> nodes = propFind(newVRL, DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_0);

        VFSNode[] nodesArray = new VFSNode[nodes.size()];
        nodesArray = nodes.toArray(nodesArray);

        return nodesArray[0];
    }

    /**
     * 
     * The PROPFIND method retrieves properties defined on the resource
     * identified by the Request-VRL, if the resource does not have any internal
     * members, or on the resource identified by the Request-URI and potentially
     * its member resources, if the resource is a collection that has internal
     * member URIs.
     * 
     * @param vrl
     * @param requestPropType
     *            DavConstants.PROPFIND_ALL_PROP,
     *            DavConstants.PROPFIND_BY_PROPERTY
     *            ,DavConstants.PROPFIND_ALL_PROP_INCLUDE
     * @param depth
     *            DavConstants.DEPTH_0, DavConstants.DEPTH_1,
     *            DavConstants.DEPTH_INFINITY
     * @return
     * @throws VlException
     */
    protected ArrayList<VFSNode> propFind(VRL vrl, int requestPropType, int depth) throws VlException
    {
        PropFindMethod method = null;

        try
        {
            method = new PropFindMethod(vrlToUrl(vrl).toString(), requestPropType, depth);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        return executePropFind(method);
    }

    /**
     * 
     * The PROPFIND method retrieves properties defined on the resource
     * identified by the Request-VRL, if the resource does not have any internal
     * members, or on the resource identified by the Request-URI and potentially
     * its member resources, if the resource is a collection that has internal
     * member URIs.
     * 
     * @param vrl
     * @param requestPropType
     *            DavConstants.PROPFIND_ALL_PROP,
     *            DavConstants.PROPFIND_BY_PROPERTY
     *            ,DavConstants.PROPFIND_ALL_PROP_INCLUDE
     * @param depth
     *            DavConstants.DEPTH_0, DavConstants.DEPTH_1,
     *            DavConstants.DEPTH_INFINITY
     * @return
     * @throws VlException
     */
    protected ArrayList<VFSNode> propFind(VRL vrl, DavPropertyNameSet propNameSet, int depth) throws VlException
    {
        PropFindMethod method = null;

        try
        {
            method = new PropFindMethod(vrlToUrl(vrl).toString(), propNameSet, depth);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        return executePropFind(method);
    }

    /**
     * Executes the <code>PropFindMethod</code>
     * 
     * @param method
     *            the PropFindMethod
     * @return <code>ArrayList<VFSNode></code>
     * @throws VlException
     */
    private ArrayList<VFSNode> executePropFind(PropFindMethod method) throws VlException
    {
        ArrayList<VFSNode> node = new ArrayList<VFSNode>();
        int code;
        try
        {
            code = client.executeMethod(method);

            if (code == HttpStatus.SC_NOT_FOUND)
            {
                return null;
            }

            MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();

            MultiStatusResponse[] responses = multiStatus.getResponses();

            VFSNode currNode = null;

            for (int i = 0; i < responses.length; i++)
            {
                currNode = createVFSNode(responses[i]);
                node.add(currNode);
            }

        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        catch (DavException e)
        {
            throw new VlException(e);
        }
        finally
        {
            method.releaseConnection();
        }
        return node;

    }

    /**
     * Used to get the properties of a resource
     * 
     * @param vrl
     * @return the properties of that resource
     * @throws VlException
     */
    protected DavPropertySet getProperties(VRL vrl) throws VlException
    {

        URL uri = vrlToUrl(vrl);

        PropFindMethod method = null;
        MultiStatusResponse[] responses;
        try
        {
            method = new PropFindMethod(uri.toString(), DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_0);

            int code = client.executeMethod(method);

            if (code == HttpStatus.SC_NOT_FOUND)
            {
                return null;
            }

            MultiStatus multiStatus = method.getResponseBodyAsMultiStatus();

            responses = multiStatus.getResponses();
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        catch (DavException e)
        {
            throw new VlException(e);
        }
        finally
        {
            method.releaseConnection();
        }
        return getProperties(responses[0]);
    }

    /**
     * Extracts properties from a server responce
     * 
     * @param statusResponse
     * @return the properties
     */
    private DavPropertySet getProperties(MultiStatusResponse statusResponse)
    {
        Status[] status = statusResponse.getStatus();

        DavPropertySet allProp = new DavPropertySet();
        for (int i = 0; i < status.length; i++)
        {
            DavPropertySet pset = statusResponse.getProperties(status[i].getStatusCode());
            allProp.addAll(pset);
        }
        
        return allProp;
    }

    /**
     * Instantiates a
     * <code>VFSNode</> based on the properties received from the server
     * 
     * @param statusResponse
     * @return the node (File of Dir)
     * @throws VRLSyntaxException
     * @throws MalformedURLException
     */
    private VFSNode createVFSNode(MultiStatusResponse statusResponse) throws VRLSyntaxException,
            MalformedURLException
    {

        DavPropertySet allProp = getProperties(statusResponse);

        DavPropertyIterator iter = allProp.iterator();

        while (iter.hasNext())
        {
            // DavProperty<?> porp = iter.nextProperty();
            DavProperty<?> prop = iter.next();
            // logger.debugPrintf("%s : %s\n", prop.getName().getName(),
            // prop.getValue());
        }
        // logger.debugPrintf("------------------------\n");

        DavProperty<?> resourceType = allProp.get(DavProperty.PROPERTY_RESOURCETYPE);

        Object value = null;
        if (resourceType != null)
        {
            value = resourceType.getValue();
        }

        String name = null;
        if (value != null && value instanceof org.apache.xerces.dom.DeferredElementNSImpl)
        {
            DeferredElementNSImpl element = (DeferredElementNSImpl) value;
            name = element.getLocalName();
        }

        VFSNode node;
        if (name != null && name.equals(COLLECTION))
        {
            node = new WebdavDir(this, urlToVrl(statusResponse.getHref()), allProp);
        }
        else
        {
            node = new WebdavFile(this, urlToVrl(statusResponse.getHref()), allProp);
        }
        return node;
    }

    private VRL urlToVrl(String href) throws VRLSyntaxException
    {
        VRL vrl = new VRL(href);
        return new VRL(WebdavFSFactory.schemes[0], vrl.getHostname(), vrl.getPort(), vrl.getPath());
    }

    private URL vrlToUrl(VRL vrl) throws VRLSyntaxException
    {
        return new VRL("http", vrl.getHostname(), vrl.getPort(), vrl.getPath()).toURL();
    }

    @Override
    public VDir newDir(VRL dirVrl) throws VlException
    {
        DavPropertySet props = new DavPropertySet();
        return new WebdavDir(this, dirVrl, props);
    }

    @Override
    public VFile newFile(VRL fileVrl) throws VlException
    {
        DavPropertySet props = new DavPropertySet();
        return new WebdavFile(this, fileVrl, props);
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }

    public String getUsername()
    {
        VAttribute attr = this.getServerInfo().getAttribute(VAttributeConstants.ATTR_USERNAME);
        if (attr != null)
            return attr.getStringValue();

        return Global.getUsername();
    }

    @Override
    public void connect() throws VlException
    {
        if (!isConnected())
        {
            if (hostConfig == null)
            {
                hostConfig = new HostConfiguration();
            }
            if (connectionManager == null)
            {
                connectionManager = new MultiThreadedHttpConnectionManager();
            }
            if (httpConnectionParams == null)
            {
                httpConnectionParams = new HttpConnectionManagerParams();
            }
            hostConfig.setHost(getHostname(), getPort());

            String timeOut = (String) vrsContext.getProperty(GlobalConfig.TCP_CONNECTION_TIMEOUT);
            httpConnectionParams.setConnectionTimeout(Integer.valueOf(timeOut));
            // httpConnectionParams.setMaxConnectionsPerHost(hostConfig,
            // MAX_HOST_CONNECTIONS);
            connectionManager.setParams(httpConnectionParams);

            client = new HttpClient(connectionManager);

            client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

            // StringHolder secret = new StringHolder();
            // getContext().getUI().askAuthentication("Password for " +
            // getUsername() + " user:", secret);

            // Credentials creds = new
            // UsernamePasswordCredentials(getUsername(), secret.value);

            Credentials creds = new UsernamePasswordCredentials("", "");

            client.getState().setCredentials(AuthScope.ANY, creds);
            client.setHostConfiguration(hostConfig);

            creds = null;

            connected = true;
        }
    }

    @Override
    public void disconnect() throws VlException
    {
        connected = false;

    }

//    public static WebdavFileSystem getClientFor(VRSContext context, VRL location) throws VlException
//    {
//        String serverID = ServerNode.createServerID(location);
//
//        WebdavFileSystem webdavClient = (WebdavFileSystem) context.getServerInstance(serverID, WebdavFileSystem.class);
//
//        if (webdavClient == null)
//        {
//            // store new client
//            ServerInfo srmInfo = context.getServerInfoFor(location, true);
//            webdavClient = new WebdavFileSystem(context, srmInfo);
//            webdavClient.setID(serverID);
//            context.putServerInstance(webdavClient);
//        }
//
//        return webdavClient;
//
//    }

    public static void clearClass()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Creates Date from a string. The format is Day, NumOfDay Month Year
     * hh:mm:ss timeZone example: 'Thu, 18 Feb 2010 07:54:48 GMT'
     * 
     * @param value
     * @return
     * @throws VlException
     */
    protected Date createDateFromString(String value) throws VlException
    {
        if (value == null || value.equals("null"))
            return null;

        if (value.length() < 29)
        {
            throw new VlException("Date parsing error. The date string should have a lenght of 29 instead got:  "
                    + value.length());
        }

        String[] strs = value.split("[ ,:]");

        int day = new Integer(strs[2]);
        int month = new Integer(Presentation.getMonthNumber(strs[3]));
        int year = new Integer(strs[4]);
        int hours = new Integer(strs[5]);
        int minutes = new Integer(strs[6]);
        int seconds = new Integer(strs[7]);

        TimeZone storedTimeZone = TimeZone.getTimeZone(strs[8]);

        GregorianCalendar now = new GregorianCalendar();
        TimeZone localTMZ = now.getTimeZone();

        now.setTimeZone(storedTimeZone);
        now.set(year, month, day, hours, minutes, seconds);
        // now.set(GregorianCalendar.MILLISECOND, millis); // be precize!
        // convert timezone back to 'local'
        now.setTimeZone(localTMZ);

        return now.getTime();
    }

    protected InputStream getInputStream(VRL vrl) throws VlException
    {

        URLConnection conn;
        try
        {
            URL url = vrlToUrl(vrl);
            conn = url.openConnection();

            return conn.getInputStream();
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
    }

    /**
     * We are going to cheat for now and not really open a stream to the remote
     * file. Instead download the file open the stream change its contents and
     * on close stream uploaded it again
     * 
     * @param vrl
     * @return
     * @throws VlException
     */
    protected OutputStream getOutputStream(VRL vrl) throws VlException
    {
        ArrayList<VFSNode> list = propFind(vrl, DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_0);
        WebdavFile targetFile;
        if (list == null || list.isEmpty())
        {
            targetFile = (WebdavFile) newFile(vrl);
        }
        else
        {
            targetFile = (WebdavFile) list.get(0);
        }

        WebdavOutputStream webdavOS = new WebdavOutputStream(this, targetFile);
        return webdavOS;
    }

    public VDir createDir(VRL vrl, boolean ignoreExisting) throws VlException
    {

        WebdavDir dir;
        MkColMethod mkCol = new MkColMethod(vrlToUrl(vrl).toString());
        try
        {
            int code = client.executeMethod(mkCol);

            if (code != HttpStatus.SC_CREATED && !ignoreExisting)
            {
                throw new VlException("Could not create " + vrl + " " + mkCol.getStatusText());
            }

            ArrayList<VFSNode> nodes = propFind(vrl, DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_0);
            dir = (WebdavDir) nodes.get(0);
        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            mkCol.releaseConnection();
        }

        return dir;
    }

    @Override
    public boolean existsFile(VRL fileVrl) throws VlException
    {
        return existsPath(fileVrl);
    }

    public WebdavFile createFile(VRL vrl, boolean ignoreExisting) throws VlException
    {

        // Since the call is not creating any exception we have to check if file
        // exists
        if (!ignoreExisting)
        {
            if (existsPath(vrl))
            {
                throw new ResourceAlreadyExistsException("File " + vrl + " already exists.");
            }
        }
        WebdavFile file;

        org.apache.jackrabbit.webdav.client.methods.PutMethod put = new org.apache.jackrabbit.webdav.client.methods.PutMethod(
                vrlToUrl(vrl).toString());
        try
        {
            int code = client.executeMethod(put);

            ArrayList<VFSNode> nodes = propFind(vrl, DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_0);
            file = (WebdavFile) nodes.get(0);

        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            put.releaseConnection();
        }

        return file;
    }

    protected boolean delete(VRL vrl, boolean recurse) throws VlException
    {
        DeleteMethod del = new DeleteMethod(vrlToUrl(vrl).toString());
        try
        {
            client.executeMethod(del);

        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            del.releaseConnection();
        }

        return true;
    }

    public VRL nameToVRL(String name)
    {
        return new VRL(WebdavFSFactory.schemes[0], getHostname(), getPort(), getVRL().getPath() + "/" + name);
    }

    public VRL move(VRL source, VRL destination, boolean overwrite) throws VlException
    {
        URL sourceUri = vrlToUrl(source);
        URL destinationUri = vrlToUrl(destination);
        MoveMethod move = new MoveMethod(sourceUri.toString(), destinationUri.toString(), overwrite);
        try
        {
            int code = client.executeMethod(move);

            if (code != HttpStatus.SC_OK || code != HttpStatus.SC_CREATED)
            {
                String message = "Moving " + source + " to: " + destination + " failed. " + move.getStatusText();
                if (!overwrite && existsPath(destination))
                {
                    return destination;
                    // throw new ResourceAlreadyExistsException(message);
                }
                else
                {
                    throw new VlException(message);
                }
            }

            return destination;
        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            move.releaseConnection();
        }

    }

    @Override
    public boolean existsPath(VRL path) throws VlException
    {
        ArrayList<VFSNode> result = propFind(path, DavConstants.PROPFIND_PROPERTY_NAMES, DavConstants.DEPTH_0);
        return (result != null && !result.isEmpty());
    }

    @Override
    public boolean existsDir(VRL path) throws VlException
    {
        return this.existsPath(path);
    }

    protected boolean copy(VRL source, VRL destination, boolean overwrite, boolean recursive)
            throws ResourceAlreadyExistsException, VlException
    {

        URL sourceUri = vrlToUrl(source);
        URL destinationUri = vrlToUrl(destination);

        CopyMethod copy = new CopyMethod(sourceUri.toString(), destinationUri.toString(), overwrite, recursive);

        try
        {
            int code = client.executeMethod(copy);
            if (code != HttpStatus.SC_OK || code != HttpStatus.SC_CREATED)
            {
                String message = "Copying " + source + " to: " + destination + " failed. " + copy.getStatusText();
                if (!overwrite && existsPath(destination))
                {
                    throw new ResourceAlreadyExistsException(message);
                }
                else
                {
                    throw new VlException(message);
                }
            }
            return true;
        }
        catch (HttpException e)
        {
            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            copy.releaseConnection();
        }
    }

    protected void upload(VRL sorce, VRL destination) throws VlException
    {

        PutMethod put = null;
        try
        {
            URL uri = vrlToUrl(destination);

            put = new PutMethod(uri.toString());

            VFSClient Vclient = new VFSClient(getContext());

            VFile file = Vclient.getFile(sorce);

            RequestEntity requestEntity = new InputStreamRequestEntity(file.getInputStream());

            put.setRequestEntity(requestEntity);

            client.executeMethod(put);
        }
        catch (HttpException e)
        {

            throw new VlException(e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
        finally
        {
            put.releaseConnection();
        }
    }

    protected void getACL(VRL vrl) throws VlException
    {
        // URL uri = vrlToUrl(vrl);
        //
        // Principal principal = Principal.getAllPrincipal();
        // Privilege[] privileges = new Privilege[1];
        // privileges[0] = Privilege.PRIVILEGE_ALL;
        // boolean invert = false;
        // boolean isProtected = false;
        // AclResource inheritedFrom = null;// new DavResourceImpl();
        // Ace ace = AclProperty.createGrantAce(principal, privileges, invert,
        // isProtected, inheritedFrom);
        // Ace[] accessControlElements = new Ace[1];
        // accessControlElements[0] = ace;
        // AclProperty aclProp = new AclProperty(accessControlElements);
        //
        // AclMethod acl = null;
        // try
        // {
        // acl = new AclMethod(uri.toString(), aclProp);
        //
        // int code = client.executeMethod(acl);
        //
        // logger.debugPrintf("Code : %s sttus: %s\n", code,
        // acl.getStatusText());
        //
        // MultiStatus status = acl.getResponseBodyAsMultiStatus();
        //
        // }
        // catch (IOException e)
        // {
        // throw new VlIOException(e);
        // }
        // catch (DavException e)
        // {
        // throw new VlException(e);
        // }
        // finally
        // {
        // acl.releaseConnection();
        // }
    }

}
