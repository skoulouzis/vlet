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
 * $Id: BDIIQuery.java,v 1.23 2011-06-07 15:13:51 ptdeboer Exp $  
 * $Date: 2011-06-07 15:13:51 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlServerException;
import nl.uva.vlet.util.bdii.info.glue.GlueConstants;
import nl.uva.vlet.util.bdii.info.glue.GlueObject;

/**
 * @author S. Koulouzis
 * 
 */
public class BDIIQuery
{
    // private Map<String, String> voPathCache = new HashMap<String, String>();

    // private Map<String, StringList> seUIDCache = new HashMap<String,
    // StringList>();

    // private Map<String, ArrayList<GlueObject>> wmsCache = new HashMap<String,
    // ArrayList<GlueObject>>();

    // private Map<String, ArrayList<GlueObject>> voInfoOrSACache = new
    // HashMap<String, ArrayList<GlueObject>>();

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(BDIIQuery.class);
//         logger.setLevelToDebug();
    }

    // === instance === //

    private URI bdiiUri;

    private int queryCount;

    // private Map<String, String> allPaths;

    private StringList seUidList;

    private HashMap<String, StringList> seUIDCahche;

    /**
     * @param bdii
     *            the URI endpoint of the bdii to query
     */
    public BDIIQuery(java.net.URI bdiiUriVal)
    {
        this.bdiiUri = bdiiUriVal;
    }

    /**
     * 
     * @param bdiiUriString
     *            the URI endpoint of the bdii to query
     * @throws VRLSyntaxException
     */
    public BDIIQuery(String bdiiUriString) throws VRLSyntaxException
    {
        try
        {
            this.bdiiUri = new java.net.URI(bdiiUriString);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException(e);
        }
    }

    /**
     * Returns services e.g. wms, lfc, srm, etc. All of the parameters except
     * wildcards.
     * 
     * @param VO
     *            the V.O. Name
     * @param serviceType
     *            optional service type. (Examples: SRM, srm_v1 (obsolete),
     *            org.edg.gatekeeper, lcg-file-catalog or with wildcards:
     *            org.glite.wms.* )
     * @param optVersion
     *            optional, service version (Examples with wildcards:2.* )
     * @return the glue service as a GlueObject object.
     * @throws VlException
     */
    public ArrayList<GlueObject> getServices(String VO, String serviceType, String optVersion) throws VlException
    {
        return getServices(VO, null, serviceType, optVersion);
    }

    /**
     * Returns services e.g. wms, lfc, srm, etc. All of the parameters except
     * wildcards.
     * 
     * @param VO
     *            the V.O. Name
     * @param optHost
     *            optional host name (Examples: srm.grid.sara.nl or with
     *            wildcards *grid.sara.nl)
     * @param serviceType
     *            optional service type. (Examples: SRM, srm_v1 (obsolete),
     *            org.edg.gatekeeper, lcg-file-catalog or with wildcards:
     *            org.glite.wms.* )
     * @param optVersion
     *            optional, service version (Examples with wildcards:2.* )
     * @return the glue service as a GlueObject object
     * @throws VlException
     */
    public ArrayList<GlueObject> getServices(String VO, String optHost, String serviceType, String optVersion)
            throws VlException
    {
        String type = GlueConstants.SERVICE;

        String searchPhrase = "(&(objectClass=" + type + ")";

        if (StringUtil.notEmpty(VO))
        {
            searchPhrase += "(" + GlueConstants.SERVICE_ACCESS_CONTROL_RULE + "=" + VO + ")";
        }

        if (StringUtil.notEmpty(serviceType))
        {
            searchPhrase += "(" + GlueConstants.SERVICE_TYPE + "=" + serviceType + ")";
        }

        if (StringUtil.notEmpty(optVersion))
        {
            searchPhrase += "(" + GlueConstants.SERVICE_VERSION + "=" + optVersion + ")";
        }
        if (StringUtil.notEmpty(optHost))
        {
            // Could it also be in service URI or access point???
            // So for example we make the query: searchPhrase += "(|(" +
            // GlueConstants.SERVICE_ENDPOINT + "=*" + optHost + "*)(" +
            // GlueConstants.SERVICE_ACCESS_POINT_URL + "=*" + optHost + "*)(" +
            // GlueConstants.SERVICE_URI + "=*" + optHost + "*))";
            searchPhrase += "(" + GlueConstants.SERVICE_ENDPOINT + "=*" + optHost + "*)";
        }

        searchPhrase += ")";

        return queryGlueObjects(type, searchPhrase);
    }

    /**
     * 
     * @param type
     *            , the type of the object we're looking for (e.g. GlueSE). This
     *            parameter may be null.
     * @param query
     *            , the ldap query for the service (e.g. get srm services
     *            accessible by dteam
     *            (&(objectClass=GlueService)(GlueServiceAccessControlRule
     *            =dteam)(GlueServiceType=srm*)(GlueServiceVersion=*)(
     *            GlueServiceEndpoint=***)) )
     * @return the result
     * @throws VlException
     */
    private ArrayList<GlueObject> queryGlueObjects(String type, String query) throws VlException
    {
        ArrayList<GlueObject> glueObjects = null;

        try
        {
            queryCount++;
            logger.debugPrintf("query Num: %s\n", queryCount);
            logger.debugPrintf("query: %s\n", query);

            ArrayList<SearchResult> res = query(query);

            glueObjects = new ArrayList<GlueObject>();

            for (int i = 0; i < res.size(); i++)
            {
                javax.naming.directory.SearchResult theRes = res.get(i);
                Attributes attr = theRes.getAttributes();
                NamingEnumeration<? extends Attribute> allAttr = attr.getAll();

                GlueObject gObject = new GlueObject(type, allAttr);
                glueObjects.add(gObject);
            }

        }

        catch (NamingException e)
        {
            throw new VlException(e);
        }

        return glueObjects;
    }

    /**
     * This method queries the bdii set in the constructor
     * 
     * @param searchPhrase
     *            the search phrase
     * @return an array list of SearchResult objects.
     * @throws BdiiException
     * @throws VlServerException
     */
    private ArrayList<SearchResult> query(final String searchPhrase) throws BdiiException, VlServerException
    {
        boolean hasError = false;
        ArrayList<SearchResult> resultsList = new ArrayList<SearchResult>();
        String bindDN = "o=grid";
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "" + this.bdiiUri);

        String timeoutstr = Global.getProperty(GlobalConfig.TCP_CONNECTION_TIMEOUT);

        if (timeoutstr != null)
        {
            env.put("com.sun.jndi.ldap.read.timeout", timeoutstr);
            env.put("com.sun.jndi.ldap.connect.timeout", timeoutstr);
            env.put("com.sun.jndi.ldap.connect.pool.timeout", timeoutstr);
        }

        if (!hasError)
        {
            try
            {
                /* get a handle to an Initial DirContext */
                DirContext dirContext = new InitialDirContext(env);

                /* specify search constraints to search subtree */
                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

                // specify the elements to return
                // if (this.getAttributes().size() > 0)
                // {
                // constraints.setReturningAttributes(this.getAttributes().toArray(
                // new String[this.getAttributes().size()]));
                // }

                // Perform the search
                NamingEnumeration<SearchResult> results = dirContext.search(bindDN, searchPhrase, constraints);
                resultsList = java.util.Collections.list(results);

            }
            catch (javax.naming.CommunicationException e)
            {
                Throwable cause = e.getCause();

                if (cause instanceof java.net.UnknownHostException)
                    throw new VlServerException("Couldn't resolve ldap service:" + bdiiUri, cause);
                else
                    throw new VlServerException("Error contacting service:" + bdiiUri, e);
            }
            catch (NamingException e)
            {
                throw new BdiiException("Query Error (Naming Error)", e);
            }
        }
        return resultsList;
    }

    /**
     * Returns GlueSites for a set of Glue Objects. If the Glue Objects have a
     * GlueForeignKey that exists in a GlueSite (Ususlly GlueSiteUniqueID), then
     * the query is done so as to get back only the objects that match these
     * attributes. In simple words the query will be
     * "Give me all sites that have uniqueID1 or uniqueID2 or ...uniqueIDN".
     * This query works only with Glue Objects that have as a foreign key
     * GlueSiteUniqueID (e.g. SE, services, etc.)
     * 
     * @param glueObject
     *            the Glue Object that contains the correct GlueForeignKey
     * @return the GlueSite that matches the GlueForeignKey contained in the
     *         Glue Objects
     * @throws VlException
     */
    public ArrayList<GlueObject> getSitesForGlueObjects(ArrayList<GlueObject> glueObject) throws VlException
    {
        StringList attributes = new StringList();
        String key = null;
        Vector<Object> res;
        String strRes;

        for (int i = 0; i < glueObject.size(); i++)
        {
            if (glueObject.get(i) == null)
            {
                continue;
            }

            res = glueObject.get(i).getExtAttribute(GlueConstants.FOREIN_KEY);// e.g.GlueClusterUniqueID=creamce.gina.sara.nl
            // or
            // GlueSiteUniqueID=RUG-CIT
            for (Object objAttr : res)
            {
                strRes = (String) objAttr;
                if (!StringUtil.isEmpty(strRes))
                {
                    // Separate key and value
                    String[] tmp = strRes.split("=");
                    key = tmp[0];
                    if (tmp.length <= 1)
                    {
                        continue;
                    }
                    if (!StringUtil.isEmpty(tmp[1]))
                    {
                        attributes.addUnique(tmp[1]);
                    }
                }

            }
        }

        return getSites(key, attributes);
    }

    /**
     * Returns GlueSites for a set of keyValues. If the keyValues have a key
     * that exists in a GlueSite, then the query is done so as to get back only
     * the objects that match these attributes. In simple words the query will
     * be"Give me all sites that have GlueAttribute1=value1 or GlueAttribute1=value1 or ...GlueAttribute1=valueN"
     * 
     * @param keyType
     *            the key type (e.g.GlueSiteUniqueID or GlueLocation, etc )
     * @param keyValues
     *            the values to be matched (e.g. RUG-CIT or creamce.gina.sara.nl
     *            or any other value N)
     * @return the GlueSites that match the have the values contained in the
     *         keyValues.
     * @throws VlException
     */
    private ArrayList<GlueObject> getSites(String keyType, StringList keyValues) throws VlException
    {
        String glueType = GlueConstants.SITE;
        StringBuffer searchPhrase = new StringBuffer();

        searchPhrase.append("(&(objectClass=" + glueType + ")");

        // match all se with unique id
        searchPhrase.append("(|");
        for (int i = 0; i < keyValues.size(); i++)
        {
            searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");

        }
        searchPhrase.append(")");

        searchPhrase.append(")");

        return this.queryGlueObjects(glueType, searchPhrase.toString());
    }

    /**
     * Prints all attributes.
     * 
     * @param glueObject
     */
    public void printAllAtributes(GlueObject glueObject)
    {
        Set<String> keySet = glueObject.getExtGlueTypes();
        Iterator<String> iter = keySet.iterator();

        while (iter.hasNext())
        {
            String key = iter.next();
            Vector<Object> extAttr = glueObject.getExtAttribute(key);
            logger.debugPrintf("%s\n", key);
            for (Object attr : extAttr)
            {
                logger.debugPrintf("\t%s\n", attr);
            }
        }
        logger.debugPrintf("----------------------\n");
    }

    /**
     * Returns all SE for this VO
     * 
     * @param vo
     *            the VO's name
     * @return the SE that belong to this VO
     * @throws VlException
     */
    public ArrayList<GlueObject> getStorageElements(String vo) throws VlException
    {
        StringList seUids = null;
        if (seUIDCahche == null)
        {
            seUIDCahche = new HashMap<String, StringList>();
        }

        seUids = seUIDCahche.get(vo);
        if (seUids == null || seUids.isEmpty())
        {
            seUids = getSEUids(vo);
            seUIDCahche.put(vo, seUids);
        }

        return getStorageElemnts(GlueConstants.SE_UNIQUE_ID, seUids);
    }

    /**
     * Robust method for getting all the SE unique IDs
     * 
     * @param vo
     *            the VO's name
     * @return the list of the SE Uids name
     * @throws VlException
     */
    public StringList getSEUids(String vo) throws VlException
    {
        if (seUidList == null)
        {
            // ArrayList<GlueObject> infoGobj = getVoInfoOrSA(vo, null, null);
            ArrayList<GlueObject> infoGobj = getStorageAreas(vo, null, null);
            if (infoGobj == null || infoGobj.isEmpty())
            {
                infoGobj = getVoInfos(vo, null, null);
            }

            seUidList = extractSEUids(infoGobj);

            // Map<String, String> allPaths = extractVOPath(infoGobj);
        }
        return seUidList;
    }

    /**
     * Returns GlueVOInfo object. VO and the keyType and keyValues are
     * collectively exhaustive. If VO is null both keyType and keyValues must be
     * specified or if keyType or keyValues are null VO must be specified if
     * 
     * @param vo
     *            the VO's name
     * @param keyType
     *            the type (e.g. GlueChunkKey)
     * @param keyValues
     *            the values (e.g. GlueSEUniqueID=gb-se-emc.erasmusmc.nl,
     *            GlueSEUniqueID=tbn18.nikhef.nl, ....etc)
     * @return
     * @throws VlException
     */
    private ArrayList<GlueObject> getVoInfos(String vo, String keyType, StringList keyValues) throws VlException
    {
        StringBuffer searchPhrase = new StringBuffer();

        searchPhrase.append("(&");

        if (StringUtil.isEmpty(vo))
        {
            searchPhrase.append("(objectClass=" + GlueConstants.VO_INFO + ")");
        }
        else
        {
            searchPhrase.append("(& (objectClass=" + GlueConstants.VO_INFO + ")" + "("
                    + GlueConstants.VO_INFO_ACCSESS_CONTROL_BASE_RULE + "=*" + vo + "))");
        }

        if (!StringUtil.isEmpty(keyType))
        {
            searchPhrase.append("(|");
            for (int i = 0; i < keyValues.size(); ++i)
            {
                searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");
            }
            searchPhrase.append(")");
        }

        searchPhrase.append(")");

        return queryGlueObjects(null, searchPhrase.toString());
    }

    /**
     * Returns GlueSA object. VO and the keyType and keyValues are collectively
     * exhaustive. If VO is null both keyType and keyValues must be specified or
     * if keyType or keyValues are null VO must be specified if
     * 
     * @param vo
     *            the VO's name
     * @param keyType
     *            the type (e.g. GlueChunkKey)
     * @param keyValues
     *            the values (e.g. GlueSEUniqueID=gb-se-emc.erasmusmc.nl,
     *            GlueSEUniqueID=tbn18.nikhef.nl, ....etc)
     * @return
     * @throws VlException
     */
    private ArrayList<GlueObject> getStorageAreas(String vo, String keyType, StringList keyValues) throws VlException
    {
        StringBuffer searchPhrase = new StringBuffer();

        searchPhrase.append("(&");

        if (StringUtil.isEmpty(vo))
        {
            searchPhrase.append("(objectClass=" + GlueConstants.SA + ")");
        }
        else
        {
            searchPhrase.append("(& (objectClass=" + GlueConstants.SA + ")" + "("
                    + GlueConstants.SA_ACCESS_CONTROL_BASE_RULE + "=*" + vo + "))");
        }

        if (!StringUtil.isEmpty(keyType))
        {
            searchPhrase.append("(|");
            for (int i = 0; i < keyValues.size(); ++i)
            {
                searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");
            }
            searchPhrase.append(")");
        }

        searchPhrase.append(")");

        return queryGlueObjects(null, searchPhrase.toString());
    }

    // /**
    // * Returns either GlueVOInfo or GlueSA object or both. VO and the keyType
    // * and keyValues are collectively exhaustive. If VO is null both keyType
    // and
    // * keyValues must be specified or if keyType or keyValues are null VO must
    // * be specified if
    // *
    // * @param vo
    // * the VO's name
    // * @param keyType
    // * the type (e.g. GlueChunkKey)
    // * @param keyValues
    // * the values (e.g. GlueSEUniqueID=gb-se-emc.erasmusmc.nl,
    // * GlueSEUniqueID=tbn18.nikhef.nl, ....etc)
    // * @return
    // * @throws VlException
    // */
    // public ArrayList<GlueObject> getVoInfoOrSA(String vo, String keyType,
    // StringList keyValues) throws VlException
    // {
    // StringBuffer searchPhrase = new StringBuffer();
    // // get any of...
    // searchPhrase.append("(|");
    // // ..VOinfo (unless the objectClass is not specified the query is too
    // // slow on sara)
    // searchPhrase.append("(&");
    //
    // if (StringUtil.isEmpty(vo))
    // {
    // searchPhrase.append("(objectClass=" + GlueConstants.VO_INFO + ")");
    // }
    // else
    // {
    // searchPhrase.append("(& (objectClass=" + GlueConstants.VO_INFO + ")" +
    // "("
    // + GlueConstants.VO_INFO_ACCSESS_CONTROL_BASE_RULE + "=*" + vo + "))");
    // }
    //
    // if (!StringUtil.isEmpty(keyType))
    // {
    // searchPhrase.append("(|");
    // for (int i = 0; i < keyValues.size(); ++i)
    // {
    // searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");
    // }
    // searchPhrase.append(")");
    // }
    //
    // searchPhrase.append(")");
    //
    // // ...or SA
    // searchPhrase.append("(&");
    //
    // if (StringUtil.isEmpty(vo))
    // {
    // searchPhrase.append("(objectClass=" + GlueConstants.SA + ")");
    // }
    // else
    // {
    // searchPhrase.append("(& (objectClass=" + GlueConstants.SA + ")" + "("
    // + GlueConstants.SA_ACCESS_CONTROL_BASE_RULE + "=*" + vo + "))");
    // }
    //
    // if (!StringUtil.isEmpty(keyType))
    // {
    // searchPhrase.append("(|");
    // for (int i = 0; i < keyValues.size(); ++i)
    // {
    // searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");
    // }
    // searchPhrase.append(")");
    // }
    //
    // searchPhrase.append(")");
    //
    // searchPhrase.append(")");
    //
    // // For example for UID the query should be something like:
    // // (|
    // // (& (objectClass=GlueVOInfo)
    // // (| (GlueChunkKey=GlueSEUniqueID=uid1)
    // // (GlueChunkKey=GlueSEUniqueID=uid2)...
    // // (GlueChunkKey=GlueSEUniqueID=uidn) )
    // // )
    // // (& (objectClass=GlueSA)
    // // (| (GlueChunkKey=GlueSEUniqueID=uid2)
    // // (GlueChunkKey=GlueSEUniqueID=uid2)...
    // // (GlueChunkKey=GlueSEUniqueID=uidn) )
    // // )
    // // )
    //
    // ArrayList<GlueObject> voOrSA = this.queryGlueObjects(null,
    // searchPhrase.toString());
    //
    // return voOrSA;
    // }

    /**
     * Extracts the VOPath from a list of GlueObject that must be either VOInfo
     * or SA, and MUST be queried for a given VO
     * 
     * @param voPathMap
     * 
     * @param voOrSA
     * @return a map that the key is the SE UID, and the value is the VOPath.
     */
    private Map<String, String> extractVOPaths(ArrayList<GlueObject> voOrSA)
    {
        Map<String, String> voPathMap = new HashMap<String, String>();

        Vector<Object> attributes;
        String voPath = null;
        String tmp[];
        String seUid = null;

        for (GlueObject gObj : voOrSA)
        {
            // First get UIDs
            attributes = gObj.getExtAttribute(GlueConstants.CHUNK_KEY);
            if (attributes != null)
            {
                for (Object attr : attributes)
                {
                    String strAttr = (String) attr;
                    tmp = strAttr.split("=");
                    if (tmp[0].equals(GlueConstants.SE_UNIQUE_ID))
                    {
                        seUid = tmp[1];
                        break;
                    }
                }

            }

            // Now get paths
            voPath = extractPath(gObj);
            if(!StringUtil.isEmpty(voPath))
                voPathMap.put(seUid, voPath);
        }
        return voPathMap;
    }

    /**
     * Extracts the vo path from a VOInfo or a SA object, by looking for the
     * SAPath or VOInfoPath. If not found it looks for the vo infoPath
     * 
     * @param gObj
     * @return the path
     */
    private String extractPath(GlueObject gObj)
    {
        // Get all the sapath attributes
        Vector<Object> attributes = gObj.getExtAttribute(GlueConstants.SA_PATH);
        String voPath = null;
        if (attributes != null)
        {
            // loop over them
            for (Object attr : attributes)
            {
                voPath = (String) attr;
                // if not empty return it....
                if (!StringUtil.isEmpty(voPath))
                {
                    return voPath;
                }
            }
        }

        // ...else we didn't find it as SAPath, look for VOInfoPath
        attributes = gObj.getExtAttribute(GlueConstants.VO_INFO_PATH);
        if (attributes != null)
        {
            for (Object attr : attributes)
            {
                voPath = (String) attr;
                if (StringUtil.isEmpty(voPath))
                {
                    return voPath;
                }
            }
        }
        return voPath;
    }

    /**
     * Extracts SE UIDs for a given list of GlueObject that must be either
     * VOInfo or SA
     * 
     * @param voOrSA
     *            the list of objects
     * @return the list of UIDs
     */
    private StringList extractSEUids(ArrayList<GlueObject> voOrSA)
    {
        Vector<Object> attributes;
        String[] tmp;
        String seUid = null;
        StringList seUidList = new StringList();

        for (GlueObject gObj : voOrSA)
        {
            // Get the chunkkey from VOInfo or SA (they both have that attribute
            // name)
            attributes = gObj.getExtAttribute(GlueConstants.CHUNK_KEY);
            if (attributes != null)
            {
                for (Object attr : attributes)
                {
                    String strAttr = (String) attr;
                    tmp = strAttr.split("=");
                    // look for seUid
                    if (tmp[0].equals(GlueConstants.SE_UNIQUE_ID))
                    {
                        seUid = tmp[1];
                        break;
                    }
                }
                seUidList.addUnique(seUid);
            }
        }
        return seUidList;
    }

    /**
     * Returns all SE that match the keyType contained in keyValues. Example:
     * getSE(GlueSEUniqueID,[tbn18.nikhef.nl,gb-se-amc.amc.nl,...]) or
     * getSE(GlueForeignKey
     * ,[GlueSite=siteUID1,GlueSite=siteUID2,...,GlueSite=siteUIDN])
     * 
     * 
     * @param keyType
     *            the type (e.g. GlueSEUniqueID)
     * @param keyValues
     *            the values (e.g. tbn18.nikhef.nl,gb-se-amc.amc.nl,... )
     * @return the SE
     * @throws VlException
     */
    private ArrayList<GlueObject> getStorageElemnts(String keyType, StringList keyValues) throws VlException
    {
        String type = GlueConstants.SE;

        StringBuffer searchPhrase = new StringBuffer();
        searchPhrase.append("(&(objectClass=" + type + ")");
        // match all se with unique id
        searchPhrase.append("(|");
        for (int i = 0; i < keyValues.size(); ++i)
        {
            searchPhrase.append("(" + keyType + "=" + keyValues.get(i) + ")");
        }

        searchPhrase.append(")");
        searchPhrase.append(")");

        return this.queryGlueObjects(type, searchPhrase.toString());
    }

    /**
     * Gets storage path for a VO.
     * 
     * @param vo
     *            the VOs name
     * @return a map containing the path allowed to read/write for this SE.The
     *         key is the SE hostname
     * @throws VlException
     */
    public Map<String, String> getVoInfoPaths(String vo) throws VlException
    {
        // StringList keyList = new StringList();
        // keyList.add(GlueConstants.SE_UNIQUE_ID + "=" + seUid);

        // ArrayList<GlueObject> saOrVOinfo = getVoInfoOrSA(vo, null, null);

        // First try the VOinfo
        ArrayList<GlueObject> saOrVOinfos = getVoInfos(vo, null, null);
        
        // Now extract the path from the object's attributes
        return extractVOPaths(saOrVOinfos);
    }

    public Map<String, String> getSAPaths(String vo, StringList missingHostnames) throws VlException
    {
        ArrayList<GlueObject> saOrVOinfos = getStorageAreas(vo, GlueConstants.CHUNK_KEY, missingHostnames);

//        for (GlueObject g : saOrVOinfos)
//        {
//            if (g.getUid().contains("srm.grid.rug.nl"))
//            {
//                logger.setLevel(Level.ALL);
//                printAllAtributes(g);
//                logger.setLevel(Level.OFF);
//            }
//        }
        
        return extractVOPaths(saOrVOinfos);
    }

    /**
     * Returns the Logging and Bookkeeping Glue Objects for a given VO.
     * 
     * @param vo
     *            the VO's name
     * @return the LB Glue Objects
     * @throws VlException
     */
    public ArrayList<GlueObject> getLBServices(String vo) throws VlException
    {
        return getServices(vo, GlueConstants.LB_SERVER, "*");
    }

    /**
     * Returns the Workload Management Subsystem Glue Objects for a given VO.
     * 
     * @param vo
     *            the VO's name
     * @return the WMS Glue Objects
     * @throws VlException
     */
    public ArrayList<GlueObject> getWMProxy(String VO) throws VlException
    {
        return getServices(VO, GlueConstants.WMPROXY, "*");
    }

    /**
     * Returns all the CE for a given VO.
     * 
     * @param VO
     *            the VO's name
     * @return the CE Glue Objects
     * @throws VlException
     */
    public ArrayList<GlueObject> getComputingElements(String VO) throws VlException
    {
        String searchPhrase;
        String glueType = GlueConstants.CE;
        if (VO == null || VO.equals(""))
        {
            searchPhrase = "(&(objectClass=" + glueType + "))";
        }
        else
        {
            searchPhrase = "(&(objectClass=" + glueType + ")(" + GlueConstants.CE_ACCESS_CONTROL_BASE_RULE + "=*" + VO
                    + "))";
        }

        return queryGlueObjects(glueType, searchPhrase);
    }

    /**
     * Returns LB services that match the keyType contained in keyValues
     * 
     * @param keyType
     *            the type (e.g. GlueForeignKey)
     * @param keyValues
     *            the values (e.g. GlueForeignKey:
     *            GlueSiteUniqueID=NIKHEF-ELPROD,... )
     * @return the LB services.
     * @throws VlException
     */
    private ArrayList<GlueObject> getLBServices(String keyType, StringList keyValues) throws VlException
    {
        String glueType = GlueConstants.SERVICE;
        String searchPhrase = "(&(objectClass=" + glueType + ")";
        searchPhrase += "(GlueServiceType=" + GlueConstants.LB_SERVER + ")";
        // Match all lb with unique id
        searchPhrase += "(|";
        for (int i = 0; i < keyValues.size(); i++)
        {
            searchPhrase += "(" + keyType + "=" + keyValues.get(i) + ")";
        }
        searchPhrase += ")";

        searchPhrase += ")";

        return queryGlueObjects(glueType, searchPhrase);
    }

    /**
     * Guess the LB services for a given VO. This is because LB is not connected
     * with any VO info.
     * 
     * @param vo
     *            , the VO name
     * @return the LB services
     * @throws VlException
     */
    public ArrayList<GlueObject> guessLBServices(String vo) throws VlException
    {
        // First get the wms
        ArrayList<GlueObject> wms = getWMProxy(vo);

        StringList attributes = getWMSForeinKeys(wms);

        return getLBServices(GlueConstants.FOREIN_KEY, attributes);
    }

    /**
     * Guess the LB services for a given hostname.
     * 
     * @param hostname
     *            , the hostname
     * @return the LB services
     * @throws VlException
     */
    public ArrayList<GlueObject> guessLBServicesForWMSHostname(String hostname) throws VlException
    {
        ArrayList<GlueObject> wms = getServices(null, hostname, GlueConstants.WMPROXY, "*");

        StringList attributes = getWMSForeinKeys(wms);

        return getLBServices(GlueConstants.FOREIN_KEY, attributes);
    }

    /**
     * Returns the attributes of WMS for GlueForeignKey
     * 
     * @param wms
     * @return
     */
    private StringList getWMSForeinKeys(ArrayList<GlueObject> wms)
    {
        StringList attributes = new StringList();
        Vector<Object> attr = null;
        String strAttr = null;
        for (int i = 0; i < wms.size(); i++)
        {
            attr = wms.get(i).getExtAttribute(GlueConstants.FOREIN_KEY);
            for (Object objAttr : attr)
            {
                strAttr = (String) objAttr;

                if (strAttr.startsWith(GlueConstants.SITE_UNIQUE_ID))
                {
                    break;
                }
            }
            // GlueForeignKey: GlueSiteUniqueID=NIKHEF-ELPROD
            attributes.addUnique(strAttr);
        }
        return attributes;
    }

    /**
     * Returns an SA for a given VO and hostname.
     * 
     * @param vo
     *            the VO name.
     * @param optHostname
     *            The host name (Optional, can be null or a wildcard )
     * @return the result
     * @throws VlException
     */
    public ArrayList<GlueObject> getSAforVoAndHost(String vo, String optHostname) throws VlException
    {
        String type = GlueConstants.SA;

        StringBuffer searchPhrase = new StringBuffer();

        if ((StringUtil.isEmpty(vo)) && (StringUtil.isEmpty(optHostname)))
        {
            searchPhrase.append("objectClass=" + type);
        }
        else
        {
            searchPhrase.append("(&(objectClass=" + type + ")");

            if (StringUtil.notEmpty(vo))
                searchPhrase.append("(" + GlueConstants.SA_ACCESS_CONTROL_BASE_RULE + "=" + vo + ")");

            if (StringUtil.notEmpty(optHostname))
                searchPhrase.append("(" + GlueConstants.CHUNK_KEY + "=" + GlueConstants.SE_UNIQUE_ID + "="
                        + optHostname + ")");

            searchPhrase.append(")");

        }
        Global.debugPrintf(this, "Query:%s\n", searchPhrase.toString());

        return queryGlueObjects(type, searchPhrase.toString());
    }

    /**
     * Returns a CE provided it's UID
     * 
     * @param ceUID
     *            , the CE UID (e.g. stremsel.nikhef.nl)
     * @return the GlueObject representing the CE
     * @throws VlException
     */
    public GlueObject getComputingElementsByUID(String ceUID) throws VlException
    {
        String glueType = GlueConstants.CE;
        StringBuffer searchPhrase = new StringBuffer();
        searchPhrase.append("(&(objectClass=" + glueType + ")");

        // match all se with unique id
        searchPhrase.append("(&");
        searchPhrase.append("(" + GlueConstants.CE_UNIQUE_ID + "=" + ceUID + ")");

        searchPhrase.append(")");

        searchPhrase.append(")");

        ArrayList<GlueObject> res = queryGlueObjects(glueType, searchPhrase.toString());

        return res.get(0);
    }

    /**
     * Returns a SE provided it's UID
     * 
     * @param seUID
     *            , the SE UID (e.g. tbn18.nikhef.nl)
     * @return the GlueObject representing the SE
     * @throws VlException
     */
    public GlueObject getSEByUID(String seUID) throws VlException
    {
        String glueType = GlueConstants.SE;
        StringBuffer searchPhrase = new StringBuffer();
        searchPhrase.append("(&(objectClass=" + glueType + ")");

        // match all se with unique id
        searchPhrase.append("(&");
        searchPhrase.append("(" + GlueConstants.SE_UNIQUE_ID + "=" + seUID + ")");

        searchPhrase.append(")");

        searchPhrase.append(")");

        Global.debugPrintf(this, "Query:%s\n", searchPhrase.toString());

        ArrayList<GlueObject> res = queryGlueObjects(glueType, searchPhrase.toString());
        if (res.isEmpty())
        {
            return null;
        }

        return res.get(0);
    }

    public static void setDebug()
    {
        logger.setLevelToDebug();
    }

    public void stopDebug()
    {
        logger.setLevelToError();

    }
}
