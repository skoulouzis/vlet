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
 * $Id: testVRL.java,v 1.8 2011-12-01 14:50:52 ptdeboer Exp $  
 * $Date: 2011-12-01 14:50:52 $
 */ 
// source: 

package test.junit.global;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrl.VRLUtil;
import nl.uva.vlet.vrs.VRS;

/**
 * Test VRLs
 * @author P.T. de Boer
 */
public class testVRL extends TestCase
{
    // VAttribute attribute=null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected void setUp()
    {
        // VAttribute=new VAttribute((String)null,(String)null,(String)null);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    // some check to ensure URI<->VRL consistancy !
    public void testURICompatibility() throws Exception
    {
        // scheme/host/path/fragment
        URI uri = new URI("file", "", "/etc", (String) null);
        VRL vrl = new VRL("file", "", "/etc", (String) null);

        if (vrl.toURI().compareTo(uri) != 0)
            Assert.assertEquals("VRL not similar to URI", vrl.toString(), uri.toString());

        // scheme/host/path/fragment
        uri = new URI("file", null, "/etc", (String) null);
        vrl = new VRL("file", null, "/etc", (String) null);

        if (vrl.toURI().compareTo(uri) != 0)
            Assert.assertEquals("VRL not similar to URI", vrl.toString(), uri.toString());

    }

    public void testConstructors() throws VlException
    {
        // SRB Example
        String vrlStr = "srb://srbserver:1234/path?srbparemeter=value";
        VRL newVRL = new VRL(vrlStr);
        Assert.assertEquals("create VRL does not match it's original string", vrlStr, newVRL.toString());

        // Constency allowing both file:/path and file:///path
        vrlStr = "gfile:/path";
        newVRL = new VRL(vrlStr);
        Assert.assertEquals("create VRL does not match it's original string", vrlStr, newVRL.toString());

        // Constency allowing both file:/path and file:///path
        vrlStr = "gfile://HOST:1234/path";
        newVRL = new VRL(vrlStr);
        Assert.assertEquals("create VRL does not match it's original string", vrlStr, newVRL.toString());

        VRL local = new VRL("file", null, "/etc");
        // TBD:
        // should be either file:/// or file://localhost/
        // if (local.toString().compareTo("file://localhost/etc")!=0)
        Assert.assertEquals("Constructor VRL(\"file\",null,\"/etc\") does not match", "file:/etc", local.toString());

        // === RELATIVE PATH
        // Relative URL ! keep 'etc' as 'etc' !
        local = new VRL("file", null, "etc");
        // if (local.toString().compareTo("file://localhost/etc")!=0)
        Assert.assertEquals("Constructor VRL(\"file\",null,\"/etc\") does not match", "file:etc", local.toString());

        local = new VRL("file", null, "dirname/etc");

        // if (local.toString().compareTo("file://localhost/etc")!=0)
        Assert.assertEquals("Constructor VRL(\"file\",null,\"/etc\") does not match", "file:dirname/etc", local
                .toString());

        local = new VRL("file", null, null);
        Assert.assertEquals("Constructor VRL(\"file\",null,null) does not match", "file:", local.toString());

        local = new VRL("file:///");
        Assert.assertEquals("local file VRL does not match", "file:/", local.toString());

        local = new VRL("file:/");
        Assert.assertEquals("local file VRL does not match", "file:/", local.toString());

        // test scheme with ":" appended
        local = new VRL("file:", null, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("added colon is not ignored.", "file:/etc", local.toString());

        // negative port must be filtered out.
        local = new VRL("file", null, -1, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Negative port number is not ignored.", "file:/etc", local.toString());

        // zero port must be filtered out.
        local = new VRL("file", null, 0, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Zero port number is not ignored.", "file:/etc", local.toString());
    }

    public void testGetParent() throws VlException
    {
        VRL local = new VRL("file", null, "/etc");
        VRL parent = local.getParent();

        Assert.assertEquals("Method getParent does not return root path:" + parent, "/", parent.getPath());

        parent = parent.getParent();

        Assert.assertEquals("Method isRootPath of root should return true.", true, parent.isRootPath());
    }

    public void testNewLocationFromLocalTildeExpansion() throws VlException
    {
        VRL loc = new VRL("file:/~");
    }

    public void testMyVLe() throws VlException
    {
        VRL loc = new VRL("myvle:");

        Assert.assertEquals("MyVLe location must have MyVLE scheme type", VRS.MYVLE_SCHEME, loc.getScheme());

        loc = new VRL("myvle:/");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", VRS.MYVLE_SCHEME, loc.getScheme());

        loc = new VRL("myvle://");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", VRS.MYVLE_SCHEME, loc.getScheme());

        loc = new VRL("myvle:///");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", VRS.MYVLE_SCHEME, loc.getScheme());
    }

    public void testNewLocationFromNullString() throws VlException
    {
        VRL loc = new VRL(null, null, (String) null);

        // Assert.assertEquals("NULL location, shoud have NULL hostname",
        // "localhost",
        // loc.getHostname());

        Assert.assertEquals("NULL location, shoud have NULL userinfo", null, loc.getUserinfo());

        Assert.assertEquals("NULL location, shoud have 0 port", true, loc.getPort() <= 0);

        Assert.assertEquals("NULL location, should have empty path", "", loc.getPath());
    }

    public void testRelative() throws VlException, URISyntaxException
    {
        VRL relvrl = new VRL("../relative directory");
        Assert.assertTrue("VRL should be relative", relvrl.isRelative());
        Assert.assertTrue("VRL should be relative", relvrl.isAbsolute() == false);

        relvrl = new VRL("/absolute path/but relative URI!");
        Assert.assertTrue("VRL should be relative", relvrl.isRelative());
        Assert.assertTrue("VRL should be relative", relvrl.isAbsolute() == false);

        relvrl = new VRL("/absolute path/but relative URI!");
        Assert.assertTrue("VRL should be relative", relvrl.isRelative());
        // url with scheme without host= absolute

        relvrl = new VRL("file:///aap");
        Assert.assertTrue("VRL should be absolute", relvrl.isAbsolute());
        Assert.assertTrue("VRL should be absolute", relvrl.isRelative() == false);

        relvrl = new VRL("subdir");
        VRL parent = new VRL("gftp://hostname/parentpath");

        VRL loc2 = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved VRL turned out wrong", "gftp://hostname/parentpath/subdir", loc2.toString());
        relvrl = new VRL("subdir");
        parent = new VRL("gftp://hostname/parentpath/base.html");

        loc2 = parent.resolve(relvrl);
        Assert.assertEquals("resolved VRL turned out wrong", "gftp://hostname/parentpath/subdir", loc2.toString());

        // index
        relvrl = parent.resolve("#index");
        Assert.assertEquals("resolved VRL turned out wrong", "gftp://hostname/parentpath/base.html#index", relvrl
                .toString());

        //
        // DOS relative paths
        // 

        parent = new VRL("file:///C:");
        relvrl = new VRL("subdir");
        VRL resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = new VRL("file://WinHost/C:");
        relvrl = new VRL("subdir");
        resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file://WinHost/C:/subdir", resolved.toString());

        parent = new VRL("file:/C:");
        relvrl = new VRL("subdir");
        resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = VRL.createDosVRL("file:///C:\\");
        relvrl = VRL.createDosVRL(".\\subdir");
        resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = VRL.createDosVRL("file:///C:\\Windos XP\\Stuffdir\\");
        relvrl = VRL.createDosVRL(".\\subdir\\subsubdir");
        resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/Windos XP/Stuffdir/subdir/subsubdir",
                resolved.toString());

        parent = VRL.createDosVRL("file:/C:\\Windos XP\\Stuffdir\\");
        relvrl = VRL.createDosVRL(".\\subdir\\subsubdir");
        resolved = parent.resolvePath(relvrl);
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/Windos XP/Stuffdir/subdir/subsubdir",
                resolved.toString());

        parent = VRL.createDosVRL("file:///C:\\subdir\\");
        resolved = parent.resolvePath("subsubdir");
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/subdir/subsubdir", resolved.toString());

        resolved = parent.resolvePath("../twindir");
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/twindir", resolved.toString());

        resolved = parent.resolvePath("./../twindir2");
        Assert.assertEquals("resolved DOS VRL turned out wrong", "file:/C:/twindir2", resolved.toString());

        // TODO:
        // query+index
        // loc=parent.resolve("?query");
        // Assert.assertEquals("resolved VRL turned out wrong","file://hostname/parentpath/base.html?query#index",loc.toString());

    }

    public void testLocalHosts()
    {
        VRL localVrl = new VRL("file", null, null);
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());

        localVrl = new VRL("file", "localhost", "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());

        localVrl = new VRL("file", "", "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());

        // current hardcoded alias for localhosts
        localVrl = new VRL("file", "127.0.0.1", "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());

        localVrl = new VRL("file", null, "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());

        localVrl = new VRL("file", Global.getHostname(), "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalHostname());
    }

    /*
     * public void testForException() { try { //Object o = emptyList.get(0);
     * fail("Should raise an IndexOutOfBoundsException"); } catch
     * (IndexOutOfBoundsException success) { } }
     */

    /**
     * Regressions unitTests for addPath:
     * 
     * @throws VlException
     */
    public void testLocationAddPath() throws VlException
    {
        VRL loc = new VRL("myvle:");
        VRL newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path not expected path", "/testpath", newLoc.getPath());

        // extra slashes:
        loc = new VRL("myvle:///");
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path not expected path", "/testpath", newLoc.getPath());

        // tests with null path
        loc = new VRL(null, null, (String) null);
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path to <null> path not expected path", "/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRL("myvle:////parent");
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("extra slashes I: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRL("myvle:///parent");
        newLoc = loc.appendPath("/testpath");
        Assert.assertEquals("extra slashes II: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRL("myvle:///parent");
        newLoc = loc.appendPath("//testpath");
        Assert.assertEquals("extra slashes II: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRL("myvle:///parent");
        newLoc = loc.appendPath("/testpath/");
        Assert.assertEquals("extra slashes III: path is not expected path", "/parent/testpath", newLoc.getPath());

    }

    /**
     * Regressions unitTests for windows locations:
     * 
     * @throws VlException
     */
    public void testWinDosLocations() throws VlException
    {
        // make relative paths absolute
        VRL loc = new VRL("file:///C:");
        Assert.assertEquals("added path didn't result in expected path", "/C:/", loc.getPath());

        // make relative paths absolute
        loc = new VRL("file:///c:hoi");
        Assert.assertEquals("added path didn't result in expected path", "/c:/hoi", loc.getPath());

        // backaslashes to slashes:
        loc = VRL.createDosVRL("file:///c:\\hoi\\");
        Assert.assertEquals("added path to <null> didn't result in expected path", "/c:/hoi", loc.getPath());

        // make relative paths absolute
        loc = VRL.createDosVRL("file:///a:");
        VRL newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path didn't result in expected path", "/a:/testpath", newLoc.getPath());

        // make relative paths absolute
        loc = VRL.createDosVRL("file:///a:");
        newLoc = loc.appendPath("\\testpath");
        Assert.assertEquals("added path didn't result in expected path", "/a:/testpath", newLoc.getPath());

    }

    public void testEncoding() throws VlException
    {
        // Test encoded String to decoded path:
        VRL vrl = new VRL("http://www.science.uva.nl/Hoi%20Piepeloi");
        String path = vrl.getPath(); // decoded path
        Assert.assertEquals("Encoded path does not decode correctly.", "/Hoi Piepeloi", path);

        // Test decoded String to decoded path:
        vrl = new VRL("http://www.science.uva.nl/Hoi Piepeloi");
        path = vrl.getPath(); // decoded path
        Assert.assertEquals("Encoded path does not decode correctly.", "/Hoi Piepeloi", path);

        // test non encoded constructor to Decoded String representation
        // Also a relative path will be cast to an absolute path.
        vrl = new VRL("sftp", "dummyhost", 6666, "Spaced Relative Path");

        // VRL to string returns DECODED URI
        String vrlstr = vrl.toString();
        Assert.assertEquals("VRL constructor does not have a decoded path.",
                "sftp://dummyhost:6666/Spaced Relative Path", vrlstr);

        // URI string returns ENCODED URI
        // Currently this is my (VRL) definitation.
        String uristr = vrl.toURI().toString();
        Assert.assertEquals("URI string encoded path.", "sftp://dummyhost:6666/Spaced%20Relative%20Path", uristr);
    }

    public void testPathEncoding() throws VlException, URISyntaxException
    {
        // Special character to test.
        // All should be encoded except '?#&'. They should be kept 'as-is'.
        // 
        String chars = "`'\"~!$%^*()_+-={}[]|;:'<>,.@?#&";
        // String chars="`'\"~!%^*()_+-={}[]|;:'<>,.@?#&";

        for (int i = 0; i < chars.length(); i++)
        {
            char c = chars.charAt(i);
            // System.out.println("Char="+c+",str="+estr);

            // check URI encoding with VRL encoding

            try
            {
                // Make sure VRL and URI use same encoding:
                URI uri = new URI("aap", "noot", "/" + c, null);
                VRL vrl = new VRL("aap", "noot", "/" + c, null);

                // if c is in "#?&" then it will be recognised as query of
                // fragment seperator
                VRL vrl2;
                if ((c == '#') || (c == '?') || (c == '&'))
                    vrl2 = new VRL("aap://noot/" + VRL.encode("" + c));
                else
                    vrl2 = new VRL("aap://noot/" + c);

                VRL vrl3 = new VRL("aap://noot/" + VRL.encode("" + c));

                Assert.assertEquals("encoded URI does not match VRL", uri.toString(), vrl.toURIString());

                Assert.assertEquals("Decoded VRL path does not match. ", "/" + c, vrl.getPath());

                Assert.assertEquals("Decoded URI path does not match. ", "/" + c, uri.getPath());

                Assert.assertEquals("VRL constructors do not match.", vrl.toString(), vrl2.toString());
                Assert.assertEquals("VRL constructors do not match.", vrl.toString(), vrl3.toString());

            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
                throw e;
            }

        }

    }

    public void testRelativePaths() throws VRLSyntaxException
    {
        // base url;
        VRL vrl = new VRL("file:///home/");

        //
        // paths resolving !
        // 

        // resolve absolute path:
        VRL newvrl = vrl.resolvePath("/etc");
        // allow both change of localhost names:
        String host = newvrl.getHostname();

        // must be "file:///etc" or file:/etc
        if (StringUtil.compare(newvrl.toString(), "file:/etc") != 0)
            Assert.assertEquals("new absolute VRL does not match", "file:///etc", newvrl.toString());

        // resolve absolute path:
        newvrl = vrl.resolvePath("etc");
        // must be "file:///etc"
        Assert.assertEquals("new absolute VRL does not match", newvrl.toString(), "file:/home/etc");

    }

    public void testURIReferences() throws VRLSyntaxException
    {
        String guidStr = "guid:aapnootmies";
        VRL guiVRL = new VRL(guidStr);
        Assert.assertEquals("Simple GUID VRL does not match original string", guidStr, guiVRL.toString());

        guidStr = "guid:10293847565647382910";
        guiVRL = new VRL(guidStr);
        Assert.assertEquals("Simple GUID VRL does not match original string", guidStr, guiVRL.toString());

        guidStr = "guid:/AAPNOOTMIES";
        guiVRL = new VRL(guidStr);
        Assert.assertEquals("Simple GUID VRL does not match original string", guidStr, guiVRL.toString());

        guidStr = "guid:1234567890-ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-abcdefghijklmnopqrstuvwxyz";
        guiVRL = new VRL(guidStr);
        Assert.assertEquals("GUID VRL does not match original string", guidStr, guiVRL.toString());

    }

    /**
     * Regression for query encoding bugs
     * 
     * @throws VRLSyntaxException
     */
    public void testQueryEncoding() throws VRLSyntaxException
    {
        String qstr = "LFN=hoipiepeloi";

        VRL vrl = new VRL("scheme:?" + qstr);
        Assert.assertEquals("Returned query string doens't match", qstr, vrl.getQuery());
        vrl = new VRL("scheme:/?" + qstr);
        Assert.assertEquals("Returned query string doens't match", qstr, vrl.getQuery());
        vrl = new VRL("scheme://host/?" + qstr);
        Assert.assertEquals("Returned query string doens't match", qstr, vrl.getQuery());
        // vrl=new VRL("scheme:reference?"+qstr);
        // Assert.assertEquals("Returned query string doens't match",qstr,vrl.getQuery());

    }

    /** Test Duplicate Path VRLs */
    public void testDuplicatePath() throws VRLSyntaxException
    {
        //
        // Regression bug !
        // 
        String prefix = "scheme://host:1234";
        VRL path = new VRL(prefix + "/absolutepath");

        // starting a path without an "/" creates wrong VRLs

        String relPath = "relative";
        VRL newvrl = path.copyWithNewPath(relPath);

        // between host:port and relpath there must be an extra slash !
        Assert.assertEquals("Extra slash isn't inserted.", newvrl.toString(), prefix + "/" + relPath);

        //
        // test duplicate equals hand hashcode !
        //

        VRL dupl = path.duplicate();
        Assert.assertEquals("Duplicate doesn't return same VRL.", path.toString(), dupl.toString());
        // test hashcode of duplicate
        Assert.assertEquals("Hashcode Failure !", path.hashCode(), dupl.hashCode());
        // test equals of duplicate
        Assert.assertTrue("equals() method Failure !", path.equals(dupl));

    }

    /** Test Duplicate Reference VRLs */
    public void testDuplicateReference() throws VRLSyntaxException
    {
        //
        // Regression bug !
        // 
        String prefix = "scheme:reference?hello";
        VRL ref1 = new VRL(prefix);

        Assert.assertEquals("Wrong reference VRL.", ref1.toString(), "scheme:reference?hello");

        // starting a path without an "/" creates wrong VRLs

        String relPath = "relative";
        VRL newvrl = ref1.copyWithNewPath(relPath);

        // duplicate path should insert extra slash (or else it isn't a path);
        Assert.assertEquals("Relative path is kept relative.", newvrl.toString(), "scheme:relative?hello");

        //
        // test duplicate equals hand hashcode !
        // 

        VRL dupl = ref1.duplicate();
        Assert.assertEquals("Duplicate doesn't return same VRL.", ref1.toString(), dupl.toString());
        // test hashcode of duplicate
        Assert.assertEquals("Hascode Failure !", ref1.hashCode(), dupl.hashCode());
        // test equals of duplicate
        Assert.assertTrue("equals() method Failure !", ref1.equals(dupl));

    }

    public void testDefaultPorts() throws VRLSyntaxException
    {
        VRL vrl1 = new VRL("sftp://elab/path1");
        VRL vrl2 = new VRL("sftp://elab:22/path2");

        Assert.assertTrue("VRL with missing SFTP port must match against default port (I)", VRLUtil.hasSameServer(vrl1,vrl2));
        Assert.assertTrue("VRL with missing SFTP port must match against default port (II)", VRLUtil.hasSameServer(vrl2,vrl1));

        vrl1 = new VRL("http://www.vl-e.nl/path1");
        vrl2 = new VRL("http://www.vl-e.nl:80/path2");

        Assert.assertTrue("VRL with missing HTTP port must match against default port (I)", VRLUtil.hasSameServer(vrl1,vrl2));
        Assert.assertTrue("VRL with missing HTTP port must match against default port (II)", VRLUtil.hasSameServer(vrl2,vrl1));

        vrl1 = new VRL("gftp://elab/path1");
        vrl2 = new VRL("gftp://elab:2811/path2");

        Assert.assertTrue("VRL with missing GFTP port must match against default port (I)", VRLUtil.hasSameServer(vrl1,vrl2));
        Assert.assertTrue("VRL with missing GFTP port must match against default port (II)", VRLUtil.hasSameServer(vrl2,vrl1));

        vrl1 = new VRL("gftp://elab/path1");
        vrl2 = new VRL("gsiftp://elab:2811/path2");

        Assert.assertTrue("VRL with missing GFTP port must match against default port (I)", VRLUtil.hasSameServer(vrl1,vrl2));
        Assert.assertTrue("VRL with missing GFTP port must match against default port (II)", VRLUtil.hasSameServer(vrl2,vrl1));

    }
    
    public void testEquals() throws VRLSyntaxException
    {
       	// Test whether port<0 equals port ==0!  
    	VRL vrl1 = new VRL("file:///localpath");
        VRL vrl2 = new VRL("file:/localpath"); 
        Assert.assertEquals("Both triple and single slashed VRLs must match" ,vrl1,vrl2); 
        
       	// Test whether port<0 equals port ==0!  
        vrl1 = new VRL("gftp","elab" ,-1,"/path");
        vrl2 = new VRL("gftp","elab",0,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against port numbers equal to 0!",vrl1,vrl2);
        
        vrl1 = new VRL("gftp","elab",-1,"/path");
        vrl2 = new VRL("gftp","elab",-2,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against any other ports<0 ",vrl1,vrl2);

        vrl1 = new VRL("gftp","elab",-1,"/path");
        vrl2 = new VRL("gftp","elab",-1,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against any itself",vrl1,vrl2); 
    }
    
    public void testLocalhostEqualsNULLHost() throws VRLSyntaxException
    {
    	// check triple vs single slash. 
    	VRL vrl1 = new VRL("file:///localpath");
        VRL vrl2 = new VRL("file:/localpath"); 
        Assert.assertEquals("Both triple and single slashed VRLs must match" ,vrl1,vrl2); 
        
      
        vrl1 = new VRL("gftp","localhost" ,0,"/path");
        vrl2 = new VRL("gftp",null,0,"/path");
        Assert.assertEquals("localhost must match NULL hostname:",vrl1,vrl2);
    }
    
    public void testUserinfo()  throws VRLSyntaxException
    {
        VRL vrl=new VRL("file://user@server/path");
        
        Assert.assertEquals("getUserinfo() must return user part.","user",vrl.getUserinfo()); 
        Assert.assertEquals("getUsername() must return user part.","user",vrl.getUsername()); 
        Assert.assertEquals("getPassword() must return NULL.",null,vrl.getPassword());
        
        vrl=new VRL("file://user:password@server/path");
        
        Assert.assertEquals("getUserinfo() must return user:password part.","user:password",vrl.getUserinfo()); 
        Assert.assertEquals("getUsername() must return user part.","user",vrl.getUsername()); 
        Assert.assertEquals("getPassword() must return password part.","password",vrl.getPassword());
        
        vrl=new VRL("file://user.domain:password@server/path");
        
        Assert.assertEquals("getUserinfo() must return user.domain:password part.","user.domain:password",vrl.getUserinfo()); 
        Assert.assertEquals("getUsername() must return user.domain part.","user.domain",vrl.getUsername()); 
        Assert.assertEquals("getPassword() must return password part.","password",vrl.getPassword());
        
        vrl=new VRL("file://:password@server/path");
        
        Assert.assertEquals("getUserinfo() must return :password part.",":password",vrl.getUserinfo()); 
        Assert.assertEquals("getUsername() must return null userpart.",null,vrl.getUsername()); 
        Assert.assertEquals("getPassword() must return password part.","password",vrl.getPassword()); 
        
    }
}
