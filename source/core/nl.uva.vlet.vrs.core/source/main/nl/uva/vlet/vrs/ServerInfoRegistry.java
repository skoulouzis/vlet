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
 * $Id: ServerInfoRegistry.java,v 1.10 2011-04-18 12:00:29 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:29 $
 */
// source: 

package nl.uva.vlet.vrs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.GlobalUtil;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.xml.XMLData;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.ResourceLoader;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.ConfigManager;
import nl.uva.vlet.vrms.SecretStore;

/**
 * Registry for the Server (Resource) Info objects. This information can be used
 * to describe all kinds of 'Resources'.
 * <p>
 * If a server provides more services then per service a ServerInfo object has
 * to be registered. For example for ssh and gftp, one object has to be stored
 * for the ssh 'server' and one for the 'gftp' server. The same yields for
 * multiple 'user' accounts. Each account (username) has to have a different
 * 'ServerInfo' object.
 * 
 * Currently the following Server field are use to uniquely identify a
 * ServerInfo object:
 * <p>
 * <li>Scheme : protocol part, for example 'ssh'.
 * <li>Hostname : Fully qualified hostname, for example 'grid.domain.com'
 * <li>Port : Port of server. Use 0 for protocol default.
 * <li>Userinfo : This is the username or the username+domainname information in
 * the case of SRB Resources.
 * <li>VO : If specified this resource description is for this VO only
 * <p>
 * 
 * @see ServerInfo
 */
public class ServerInfoRegistry
{
    private static final String XML_SERVER_CONFIG_HEADER = " VLET ServerRegistry Configuration\n"
            + " Contains Saved Resource Descriptions and configurations ";

    private static final String XML_SERVER_CONFIG_HEADER_TAG = "vlet:ServerInfoRegistry";

    private static ClassLogger logger = null;

    static
    {
        logger = ClassLogger.getLogger(ServerInfoRegistry.class);
    }

    /**
     * Moved hashtable with ServerInfo objects to seperate registry to enable
     * VRSContext dependend server information.
     */
    private Map<String, ServerInfo> serverInfos = new Hashtable<String, ServerInfo>();

    VRSContext context = null;

    @SuppressWarnings("unused")
    private boolean isSaved = false;

    private boolean isLoaded = false;

    private SecretStore secretStore = new SecretStore();

    // package protected constructor !
    public ServerInfoRegistry(VRSContext contxt)
    {
        this.context = contxt;
    }

    /** Use infoID to get the ServerInformation */
    public ServerInfo getServerInfo(String infoID)
    {
        if (infoID == null)
            return null;

        // logger.messagePrintln(ServerInfo.class,"searching for:"+accountID);
        ServerInfo info = serverInfos.get(infoID);
        // keep stored copied private !
        if (info != null)
            return info.duplicate();

        return null;
    }

    /**
     * Return 1st ServerInfo object for the specified Scheme. Is used to find
     * *any* server of the specified protocol.
     * 
     * @param scheme
     * @return ServerInfo object
     */
    public ServerInfo getServerInfoForScheme(String scheme)
    {
        if (scheme == null)
            return null;

        ServerInfo infos[] = this.getServerInfos(scheme, null);

        if ((infos == null) || (infos.length <= 0))
        {
            logger.warnPrintf("Couldn't find server info for scheme:%s\n", scheme);
            return null;
        }

        return infos[0];
    }

    /**
     * Return ServerInfo objects for the specified Scheme. Is used to find *any*
     * server of the specified protocol.
     * 
     * @param scheme
     * @return ServerInfo object
     */
    public ServerInfo[] getServerInfosForScheme(String scheme)
    {
        if (scheme == null)
            return null;

        return getServerInfos(scheme, null, -1, null);
    }

    /**
     * Get All Server descriptions for this host. This allows grouping of
     * Services per host.
     */
    public ServerInfo[] getServerInfosForHost(String host)
    {
        if (host == null)
            return null;

        return this.getServerInfos(null, host, -1, null);
    }

    /**
     * Get All Server descriptions for this host. This allows grouping of
     * Services per host.
     * 
     */
    public ServerInfo[] getServerInfosForHost(String host, int port)
    {
        if (host == null)
            return null;

        return this.getServerInfos(null, host, port, null);
    }

    /** Use Logical Name */
    public ServerInfo getServerInfoByName(String name)
    {
        String ids[] = this.getServerIDs();
        for (String id : ids)
        {
            ServerInfo info = this.serverInfos.get(id);
            if (StringUtil.equals(info.getName(), name))
                return info.duplicate();
        }

        return null;
    }

    /**
     * Main method to search for a Server Info object. When a search field is
     * null it means "don't care". Use port number less then 0 for a don't care.
     * Use port number EQUAL to 0 for default port ! Returns multiple matching
     * ServerInfo descriptions or NULL when it can't find any matching server
     * descriptions.
     * 
     * @param scheme
     *            if NULL then don't care (match any)
     * @param hostname
     *            if NULL then don't care (match any)
     * @param port
     *            if port==-1 => don't care, if port==0 => default and if port>0
     *            match explicit port number
     * @param userInfo
     *            if NULL then don't care (match any)
     */
    public ServerInfo[] getServerInfos(String scheme, String host, int port, String optUserInfo)

    {
        logger.debugPrintf(">>> find {scheme,host,port,user}=%s,%s,%s,%s\n", scheme, host, port, optUserInfo);
        // if (scheme.compareTo("srb")==0)
        // {
        // logger.debugPrintln(this,"SRB"); // breakpoint
        // }
        ServerInfo infoArr[] = new ServerInfo[serverInfos.size()];
        infoArr = this.serverInfos.values().toArray(infoArr);

        Vector<ServerInfo> result = new Vector<ServerInfo>();

        for (ServerInfo info : infoArr)
        {
            logger.debugPrintf(" - comparing:%s", info);

            if (info.matches(scheme, host, port, optUserInfo))
            {
                logger.debugPrintf(" - adding:%s\n", info);
                // =========================
                // Add Duplicate !
                // ==========================
                result.add(info.duplicate());
            }
        }

        if (result.size() <= 0)
        {
            logger.debugPrintf("<<< Returning NULL ServerInfos\n");
            return null;
        }

        ServerInfo arr[] = new ServerInfo[result.size()];
        arr = result.toArray(arr);
        logger.debugPrintf("<<< Returning #%d ServerInfos\n", arr.length);

        return arr;
    }

    public void removeAll()
    {
        synchronized (this.serverInfos)
        {
            this.isSaved = false;
            this.isLoaded = false;

            logger.debugPrintf(">>> ServerInfos.clear() ! <<<\n");
            serverInfos.clear();
        }
    }

    /**
     * Stored new ServerInfo and writes to file (if persistant) Returnes updated
     * serverInfo
     */
    public ServerInfo store(ServerInfo info)
    {
        logger.debugPrintf("store(): attrs=%s\n", info.getAttributeSet());

        synchronized (this.serverInfos)
        {
            // ===
            // Before adding new Info: check whether persistant database is
            // loaded !
            // ===
            checkIsLoaded();
            // remove before put!
            remove(info);
            // synchronized put: UPDATES SERVERINFO KEY!
            put(info);
            // enters mutex again
            save();
            
            // do NOT return reference to object into ServerRegistry
            return info.duplicate(); 
        }
    }
    
    /** Update Actual ServerInfo ID */ 
    protected void updateServerInfoID(ServerInfo info)
    {
        synchronized (this.serverInfos)
        {
            // Check whether muliple user IDs are allowed!
            String userInf = "";

            if (info.getNeedUserinfo() || StringUtil.isNonWhiteSpace(info.getUserinfo()))
            {
                // use explicit userinfo
                userInf = info.getUserinfo() + "@";
            }

            // Normalize capitals.

            String fixedID = userInf + info.getScheme().toUpperCase() + "-"
                    + StringUtil.noNull(info.getHostname()).toUpperCase() + ":" + info.getPort();
            info.setID(fixedID);
        }
    }

    /** Returns <SCHEME-#Key> server ID */
    protected String createUniqueID(ServerInfo info)
    {
        String scheme = info.getScheme();

        synchronized (this.serverInfos)
        {
            for (int i = 0; i < 1000; i++)
            {
                String key = scheme.toUpperCase() + "-" + i;
                if (this.serverInfos.get(key) == null)
                {
                    // debug(">>> New Server ID="+key);
                    return key;
                }
            }
        }

        throw new Error("To much server configurations.");
    }

    /** Return's list of Server ID currently stored */
    public String[] getServerIDs()
    {
        StringList idList = new StringList();
        // StringList names=new StringList();

        synchronized (this.serverInfos)
        {
            Set<String> keys = serverInfos.keySet();

            for (String key : keys)
            {
                idList.add(serverInfos.get(key).getID());
            }

            idList.sort(true);

            // for (String id:idList)
            // {
            // names.add(serverInfos.get(id));
            // }
        }

        return idList.toArray();
    }

    /** Put ServerInfo into registry */
    private void put(ServerInfo info)
    {
        synchronized (serverInfos)
        {
            // store private copy !
            info = info.duplicate();

            updateServerInfoID(info);

            logger.debugPrintf("+++ Storing info:%s\n", info);

            ServerInfo prev = this.serverInfos.get(info.getID());

            if (prev == info)
                logger.infoPrintf(">>> updating ServerInfo:%s\n", info);
            else if (prev != null)
                logger.infoPrintf(">>> WARNING: Overwriting previous object with:%s\n", info);
            else
                logger.infoPrintf(">>> storing new ServerInfo:%s\n", info);

            this.serverInfos.put(info.getID(), info);
            // mark dirty:
            this.isSaved = false;
        }
    }

    /**
     * Checks whether the persistent Server Info Registry is loaded and load it
     * if necessary.
     */
    private void checkIsLoaded()
    {
        // use serverInfos as Mutex

        synchronized (this.serverInfos)
        {
            // Check new Configuration Manager !
            ConfigManager confMan = this.context.getConfigManager();

            if (confMan.getUsePersistantUserConfiguration() == false)
                return;

            if (this.isLoaded == true)
                return;

            try
            {
                load();
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.WARN, e, "Couldn't load ServerInfo Registry!\n");
            }
        }
    }

    /** Find matching Server Info descriptions for scheme and host */
    public ServerInfo[] getServerInfos(String scheme, String host)
    {
        // 0 = default, -1 = don't care (return first)
        return getServerInfos(scheme, host, -1, null);
    }

    /** Find matching Server Info descriptions for user at scheme and host:port */
    public ServerInfo[] getServerInfos(String scheme, String host, int port)
    {
        return getServerInfos(scheme, host, port, null);
    }

    /**
     * Find ServerInfos for specified location, this can be 0 or more matches !
     */
    public ServerInfo[] getServerInfosFor(VRL loc)
    {
        String host = loc.getHostname();
        int port = loc.getPort();
        String scheme = loc.getScheme();
        String userInfo = loc.getUserinfo(); // use FULL userinformation

        // no port in VRL mean => use DEFAULT, not DON'T CARE. (-1= DON'T CARE)
        if (port < 0)
            port = 0;

        ServerInfo[] infos = this.getServerInfos(scheme, host, port, userInfo);

        if (infos != null)
            if (infos.length > 1)
                logger.warnPrintf("Warning: matched more then 1 ServerInfo for location:%s\n", loc);
        // else
        // logger.warnPrintf(">>> returning 1 info:"+infos[0]);

        return infos;
    }

    /** Find first matching ServerInfo for location */
    public ServerInfo getServerInfoFor(VRL loc, boolean autoCreate)
    {
        ServerInfo infos[] = getServerInfosFor(loc);

        if ((infos != null) && (infos.length > 0))
            return infos[0];

        if (autoCreate == true)
            return new ServerInfo(context, loc);

        return null;
    }

    public ServerInfo remove(ServerInfo info)
    {
        if (info == null)
            return null;

        logger.infoPrintf("--- Removing ServerInfo:%s\n", info);

        ServerInfo prev = null;

        synchronized (this.serverInfos)
        {
            String key = info.getID(); // return NULL if key hasn't been
                                       // set/info hasn't been stored !
            if (key != null)
                prev = this.getServerInfo(key);
            removeByKey(key);
            return prev;
        }
    }

    /** Return ServerInfo as Vector of VAttributeSets */
    public ArrayList<VAttributeSet> getInfoAttrSets()
    {
        // synchronize !
        synchronized (this.serverInfos)
        {
            Set<String> keys = this.serverInfos.keySet();
            ArrayList<VAttributeSet> sets = new ArrayList<VAttributeSet>(keys.size());

            for (String key : keys)
            {
                ServerInfo info = serverInfos.get(key);
                sets.add(info.getAttributeSet());
            }

            return sets;
        }
    }

    /**
     * Saves the ServerInfo Registry if the current configuration allows a
     * persistant Registry !
     * 
     * By default this is false, but when MyVle is initialized by the VBrowser
     * the persistant ServerInfo Registry will be enabled.
     * 
     */
    public void save()
    {
        // Check new Configuration Manager !
        ConfigManager confMan = this.context.getConfigManager();

        if (confMan.getUsePersistantUserConfiguration() == false)
            return;

        VRL loc = confMan.getServerRegistryLocation();

        // is synchronized will return consistant list of configs

        ArrayList<VAttributeSet> sets = this.getInfoAttrSets();
        // ResourceLoader loader=new ResourceLoader(context);

        XMLData xmlifier = new XMLData();
        xmlifier.setVAttributeElementName("vlet:ServerInfoProperty");
        xmlifier.setVAttributeSetElementName("vlet:ServerInfo");
        // xmlifier.setPersistanteNodeElementName("vlet:ServerInfo2"); // No
        // Nodes!
        try
        {
            // registry must be local path:
            OutputStream outps = GlobalUtil.getFileOutputStream(loc.getPath());
            xmlifier.writeAsXML(outps, XML_SERVER_CONFIG_HEADER_TAG, sets, XML_SERVER_CONFIG_HEADER);
            outps.close();
            this.isSaved = true;
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN, e, "Couldn't save server registry to:%s\n", loc);
            // but continue!
        }
    }

    /**
     * Load saved server configuration.
     * 
     * Currently when MyVLe object is initialized the ServerInfoRegistry will be
     * loaded.
     */
    protected void load() throws VlException
    {
        logger.debugPrintf(">>> load() <<<\n");

        //
        // use serverInfo as mutex !
        //
        synchronized (this.serverInfos)
        {
            // Use new Configuration Manager
            ConfigManager confMan = this.context.getConfigManager();
            VRL loc = confMan.getServerRegistryLocation();

            // is synchronized will return consistant list of configs

            ArrayList<VAttributeSet> sets = this.getInfoAttrSets();
            ResourceLoader loader = new ResourceLoader(context);

            XMLData xmlifier = new XMLData();
            xmlifier.setVAttributeElementName("vlet:ServerInfoProperty");
            xmlifier.setVAttributeSetElementName("vlet:ServerInfo");

            InputStream inps = loader.getInputStream(loc);

            sets = xmlifier.parseVAttributeSets(inps, XML_SERVER_CONFIG_HEADER_TAG);
            // for (VAttributeSet set:sets)
            // {
            // logger.debugPrintln(this,"Adding ServerInfo Set:"+set);
            // }

            // ===
            // Do not clear: just merge current with save ones !
            // serverInfos.clear();
            // ===
            for (VAttributeSet set : sets)
            {
                ServerInfo info = new ServerInfo(context, set);
                logger.debugPrintf("Adding Server Config:%s\n", info);
                // actual store method:
                put(info);
            }

            this.isLoaded = true;
        }
    }

    protected void removeByKey(String serverID)
    {
        _removeByKey(serverID);
    }

    private void _removeByKey(String serverID)
    {
        if (serverID == null)
            return;

        synchronized (this.serverInfos)
        {
            this.serverInfos.remove(serverID);
            save();
        }

    }

    /** Explicit reload of server registry */
    public void reload()
    {
        checkIsLoaded();
    }

    public SecretStore getSecretStore()
    {
        return this.secretStore;
    }

}
