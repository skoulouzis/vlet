/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: LDAPTests.java,v 1.2 2010-10-26 15:22:45 ptdeboer Exp $  
 * $Date: 2010-10-26 15:22:45 $
 */ 
// source: 

package nl.uva.vlet.util.bdii;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlServerException;
import nl.uva.vlet.util.bdii.BDIIQuery;
import nl.uva.vlet.util.bdii.BdiiException;
import nl.uva.vlet.util.bdii.BdiiService;
import nl.uva.vlet.util.bdii.StorageArea;
import nl.uva.vlet.util.bdii.ServiceInfo;
import nl.uva.vlet.util.bdii.info.glue.GlueObject;



public class LDAPTests
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {

        // getSE();
        // getLFC(); 
        // getSchema();
        // testBDIIQuery();
        // testSimpleQuery();
        getSAs("pvier");
        //getAllStorageElem();
     
    }

    public static void testSimpleQuery() throws Exception
    {
        // ldap://localhost:389/o=JNDITutorial
        String type;
        String searchPhrase;
        BDIIQuery q = new BDIIQuery(new URI("ldap://bdii.grid.sara.nl:2170/")); 

        String VO = "pvier";
        type = "GlueSA";
        searchPhrase = "(&(objectClass=" + type
                + ")(GlueSAAccessControlBaseRule=" + VO + "))";

        debug("Query: " + searchPhrase);
        ArrayList<SearchResult> res = null;//q.query(searchPhrase);

        Vector<GlueObject> allOfTheSA = new Vector<GlueObject>();

        for (int i = 0; i < res.size(); i++)
        {
            javax.naming.directory.SearchResult theRes = res.get(i);
            Attributes attr = theRes.getAttributes();
            NamingEnumeration<? extends Attribute> allAttr = attr.getAll();
            try
            {
                // debug("-------------");
                GlueObject GlueSA = new GlueObject(type, allAttr);
                allOfTheSA.add(GlueSA);

            }
            catch (NamingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        type = "GlueSE";
        searchPhrase = "(objectClass=" + type + ")";
        ArrayList<SearchResult> resultsList = null;//q.query(searchPhrase);
        //Spiros:Dead variable
//        Vector<GlueObject> myVOSE = new Vector<GlueObject>();
        for (int i = 0; i < resultsList.size(); i++)
        {
            SearchResult si = resultsList.get(i);

            Attributes attrs = si.getAttributes();

            NamingEnumeration<? extends Attribute> all = attrs.getAll();

            try
            {
                GlueObject GlueSE = new GlueObject(type, all);
                String thisUI = GlueSE.getUid();

                for (int j = 0; j < allOfTheSA.size(); j++)
                {
                    String SEUniqueID = (String) allOfTheSA.get(j).getUid();
                    debug("the uid: " + SEUniqueID);
                    if (thisUI.equals(SEUniqueID))
                    {
                        // myVOSE.add(GlueSE);
                        String host = thisUI;
                        // accessProtocol =
                        debug("MY SE!!!!: " + thisUI);
                    }
                }
            }
            catch (NamingException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
    }

    public static void getSchema()
    {
        String bindDN = "Mds-Vo-name=local,o=grid";
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://bdii.grid.sara.nl:2170/");
        // env.put(Context.OBJECT_FACTORIES, "GlueObjectFactory");

        try
        {
            /* get a handle to an Initial DirContext */
            DirContext dirContext = new InitialDirContext(env);

            // com.sun.jndi.ldap.LdapCtx cntx = (LdapCtx) dirContext
            // .lookup(bindDN);

            // Attributes att = cntx.getAttributes("");

            // NamingEnumeration<? extends Attribute> all = att.getAll();

            // while (all.hasMore())
            // {
            // Attribute atr = all.next();
            // // String id = atr.getID();
            //
            // NamingEnumeration<?> allAtr = atr.getAll();
            //
            // while (allAtr.hasMore())
            // {
            // Object oo = allAtr.next();
            // debug("\t id: " + oo);
            // }
            // DirContext def = atr.getAttributeDefinition();
            //
            // Attributes defAtrr = def.getAttributes("");
            // NamingEnumeration<? extends Attribute> deffAtrrAll = defAtrr
            // .getAll();
            //
            // while (deffAtrrAll.hasMore())
            // {
            // Attribute aa = deffAtrrAll.next();
            //
            // NamingEnumeration<?> aaAll = aa.getAll();
            // while (aaAll.hasMore())
            // {
            // debug("\t\t" + aaAll.next());
            // }
            //
            // }
            //
            // //
            //
            // }

            // Attributes ocAttrs;
            // Get the schema tree root
            DirContext schema = dirContext.getSchema(bindDN);

            String oc = "GlueSETop";
            DirContext se = (DirContext) schema.lookup("ClassDefinition/" + oc);

            Attributes attr = se.getAttributes("");

            NamingEnumeration<? extends Attribute> all = attr.getAll();

            while (all.hasMore())
            {
                Attribute atr1 = all.next();
                debug("id " + atr1.getID());

                NamingEnumeration<?> all2 = atr1.getAll();
                while (all2.hasMore())
                {
                    Object oo = all2.next();
                    // // = all.next().getAll().next();
                    debug("\t" + oo);
                }

            }

            // Attributes ocAttrs = schema.getAttributes("ClassDefinition/" +
            // oc);
            //
            // Attribute must = attr.get("MUST");
            // Attribute may = attr.get("MAY");
            //
            // NamingEnumeration<?> allMust = must.getAll();
            // NamingEnumeration<?> allMay = may.getAll();
            //
            // debug("GlueTOP att: ");
            // while (allMust.hasMore())
            // {
            // debug("" + allMust.nextElement());
            // }
            //
            // while (allMay.hasMore())
            // {
            // debug("" + allMay.nextElement());
            // }

            // NamingEnumeration bindings = dirContext.listBindings(bindDN);

            // debug("Binding : ");
            // while (bindings.hasMore())
            // {
            // Binding bd = (Binding) bindings.next();
            // String name = bd.getName();
            // debug(name);
            // com.sun.jndi.ldap.LdapCtx cntx = (LdapCtx) bd.getObject();
            //                
            // Attributes att = cntx.getAttributes("");
            //                 
            // NamingEnumeration<? extends Attribute> all = att.getAll();
            //                 
            // while(all.hasMore()){
            // Attribute atr = all.next();
            // String id = atr.getID();
            //                     
            // debug("\t id: "+id);

            // NamingEnumeration<?> allofALl = atr.getAll();
            // while(allofALl.hasMore()){
            // Object atrOfAllofAll = allofALl.next();
            // debug("\t\t Finally: "+atrOfAllofAll);
            // }
            //                     

            // DirContext def = atr.getAttributeDefinition();

            // Attributes defAtr = def.getAttributes("");

            // NamingEnumeration<? extends Attribute> allDefAtr =
            // defAtr.getAll();
            // debug("\t schema for "+id);
            // while(allDefAtr.hasMore()){
            // Attribute allSchemaAtrr = allDefAtr.next();
            // NamingEnumeration<?> allSchemaAtrr1 = allSchemaAtrr.getAll();
            // while(allSchemaAtrr1.hasMore()){
            // Object obj = allSchemaAtrr1.next();
            // debug("\t\t more: "+obj);
            // }
            //                         
            // }

            // }

            // NamingEnumeration<? extends Attribute> all = att.getAll();
            //                
            // while(all.hasMore()){
            // bd = (Binding)bindings.next();
            // name = bd.getName();
            // System.out.println("\t"+name);
            // }
            // }

            // // List contents of root
            // NamingEnumeration bds = schema.list("");
            // System.out.println("Schema: ");
            // while (bds.hasMore())
            // {
            // NameClassPair def = ((NameClassPair) (bds.next()));
            // System.out.println(def.getName());
            //
            // }

            // Close the context when we're done
            dirContext.close();

        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
    }

    public static void testBDIIQuery() throws Exception
    {
        BDIIQuery query = new BDIIQuery("ldap://bdii.grid.sara.nl:2170/");
        String VO = "pvier";
        String searchPhrase = "(&(objectClass=GlueSA)(GlueSAAccessControlBaseRule="
                + VO + "))";
        ArrayList<SearchResult> res = null;//query.query(searchPhrase);

        for (int i = 0; i < res.size(); i++)
        {
            javax.naming.directory.SearchResult theRes = res.get(i);
            debug("getName: " + theRes.getName());
        }
    }

    private static void debug(String msg)
    {
        System.err.println(LDAPTests.class.getName() + ": " + msg);

    }

    
    public static void getLFC() throws VlException, URISyntaxException
    {

        
        
        try
        {
            BDIIQuery query = new BDIIQuery("ldap://bdii.grid.sara.nl:2170/");
            
//            query.getSE();
//            query.getSA("pvier");
//            GlueObject[] mySE = query.getSE("pvier");
            
//            for(int i=0;i<mySE.length;i++){
//                debug("UID: "+mySE[i].getUid());
//                debug("UID: "+mySE[i].getAttribute(""));
//            }
            
//            query.getSEAccessProtocol("*");
            //query.getServices("pvier","srm*","2.*");
            query.getServices("pvier","lfc","*");
//            query.getSE("pvier");
            //query.getSAForVO("pvier");
        }
        catch (BdiiException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (VlServerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        String searchPhrase = "(&(objectClass=GlueSA)";
//        String VO = "pvier";
//        searchPhrase += "(GlueSAAccessControlBaseRule=" + VO + "))";
//
//        ArrayList<SearchResult> resultsList = query.query(searchPhrase);
//
//        for (int i = 0; i < resultsList.size(); i++)
//        {
//            SearchResult si = resultsList.get(i);
//
//            debug("Name: " + si.getName());
//            debug("ClassName: " + si.getClassName());
//            debug("NameInNamespace: " + si.getNameInNamespace());
//
//            Attributes attrs = si.getAttributes();

            // GlueSA myGlueSA = new GlueSA();
            // myGlueSA.processGlueRecord(attrs);
            //
            // // debug("ATTT: "+ myGlueSA.allAttr2String());
            //
            // // Ignore values like gelipse:replica:online
            // if (!myGlueSA.getID().contains(":"))
            // {
            // myGlueSA.tableName = "GlueSA";
            // GlueSE myGlueSE = new GlueSE();
            // myGlueSE.glueSAList.add(myGlueSA);

            // debug("DisplayName: "+myGlueSE.getDisplayName());
            // debug("AccessProtocolList: ");
            // for(int j=0;j<myGlueSE.glueSEAccessProtocolList.size();j++){
            // debug((String)myGlueSE.glueSEAccessProtocolList.get(j));
            // }
            // debug("ControlProtocolList: ");
            // for(int j=0;j<myGlueSE.glueSEControlProtocolList.size();j++){
            // debug((String)myGlueSE.glueSEControlProtocolList.get(j));
            // }

            // glueSAList.put( myGlueSA.getID(), myGlueSA );
            // glueSEList.put( myGlueSA.getID().substring(
            // myGlueSA.getID().indexOf( '@' ) + 1 ), myGlueSE );
            // }

//        }
    }


    
    public static void getSE() throws VlException, URISyntaxException
    {
        
        try
        {
            BDIIQuery query = new BDIIQuery("ldap://bdii.grid.sara.nl:2170/");
            
//            query.getSE();
//            query.getSA("pvier");
//            GlueObject[] mySE = query.getSE("pvier");
            
//            for(int i=0;i<mySE.length;i++){
//                debug("UID: "+mySE[i].getUid());
//                debug("UID: "+mySE[i].getAttribute(""));
//            }
            
//            query.getSEAccessProtocol("*");
            //query.getServices("pvier","srm*","2.*");
            query.getServices("pvier","lfc","*");
//            query.getSE("pvier");
            //query.getSAForVO("pvier");
        }
        catch (BdiiException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (VlServerException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
//        String searchPhrase = "(&(objectClass=GlueSA)";
//        String VO = "pvier";
//        searchPhrase += "(GlueSAAccessControlBaseRule=" + VO + "))";
//
//        ArrayList<SearchResult> resultsList = query.query(searchPhrase);
//
//        for (int i = 0; i < resultsList.size(); i++)
//        {
//            SearchResult si = resultsList.get(i);
//
//            debug("Name: " + si.getName());
//            debug("ClassName: " + si.getClassName());
//            debug("NameInNamespace: " + si.getNameInNamespace());
//
//            Attributes attrs = si.getAttributes();

            // GlueSA myGlueSA = new GlueSA();
            // myGlueSA.processGlueRecord(attrs);
            //
            // // debug("ATTT: "+ myGlueSA.allAttr2String());
            //
            // // Ignore values like gelipse:replica:online
            // if (!myGlueSA.getID().contains(":"))
            // {
            // myGlueSA.tableName = "GlueSA";
            // GlueSE myGlueSE = new GlueSE();
            // myGlueSE.glueSAList.add(myGlueSA);

            // debug("DisplayName: "+myGlueSE.getDisplayName());
            // debug("AccessProtocolList: ");
            // for(int j=0;j<myGlueSE.glueSEAccessProtocolList.size();j++){
            // debug((String)myGlueSE.glueSEAccessProtocolList.get(j));
            // }
            // debug("ControlProtocolList: ");
            // for(int j=0;j<myGlueSE.glueSEControlProtocolList.size();j++){
            // debug((String)myGlueSE.glueSEControlProtocolList.get(j));
            // }

            // glueSAList.put( myGlueSA.getID(), myGlueSA );
            // glueSEList.put( myGlueSA.getID().substring(
            // myGlueSA.getID().indexOf( '@' ) + 1 ), myGlueSE );
            // }

//        }
    }
    
    
    public static void getAllStorageElem()
    {
        
        try
        {
            BdiiService ser = new BdiiService(new java.net.URI("ldap://bdii.grid.sara.nl:2170/"));

            nl.uva.vlet.util.bdii.ServiceInfo info = ser.getSRMv22ServiceForHost("srm.grid.sara.nl");
            print(info);
            
            ArrayList<StorageArea> srms = ser.getSRMv22SAsforVO("pvier"); 
            print(srms); 
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getSAs(String vo)
    {
        
        try
        {
            BdiiService ser = new BdiiService(new java.net.URI("ldap://bdii.grid.sara.nl:2170/"));

            nl.uva.vlet.util.bdii.ServiceInfo info = ser.getSRMv22ServiceForHost("srm.grid.sara.nl");
            print(info);
            
            ArrayList<StorageArea> srms = ser.getSRMv22SAsforVO(vo); 
            print(srms); 
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void print(ServiceInfo info)
    {
        System.out.println(" - service type     ="+info.getServiceType()); 
        System.out.println(" - service protocol ="+info.getScheme());
        System.out.println(" - service host     ="+info.getHost());
        System.out.println(" - service port     ="+info.getPort());
    }

    private static void print(ArrayList<StorageArea> sas)
    {
        //ser.getSRMv22SEsforVO("pvier");
        //ser.getSRMv22SEsforVO("*");
        for (StorageArea sa:sas) 
        {
            System.out.println(" host         ="+sa.getHostname()); 
            System.out.println(" vo           ="+sa.getVO());
            System.out.println(" storate path ="+sa.getStoragePath());
            
            ArrayList<ServiceInfo> infos = sa.getServices();
            
            for (ServiceInfo info:infos)
            {
               print(info); 
            }
        }
    }

}
