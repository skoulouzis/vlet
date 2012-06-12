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
 * $Id: testProxyNode.java,v 1.3 2011-05-02 13:28:49 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:49 $
 */ 
// source: 

package test.junit.vbrowser;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;
import nl.uva.vlet.gui.proxyvrs.ProxyNode;
import nl.uva.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ResourceEvent;
import nl.uva.vlet.vrs.ResourceEventListener;

/**
 * As the ProxyNode is the workhorse of the VBrowser, I started to create JUnit
 * tests for it as well.
 * 
 * @author P.T. de Boer
 * 
 */
public class testProxyNode extends TestCase
{
    VRL tmpdir = null;

    VFSClient vfs = null;

    VDir testdir;

    static
    {
        ProxyVNodeFactory.initPlatform();
    }

    private ProxyNodeFactory proxyFactory;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     * 
     * @throws VlException
     */
    protected synchronized void setUp() throws VlException
    {
        Global.init();
        if (tmpdir == null)
            tmpdir = new VRL("file:///tmp/" + Global.getUsername() + "/testProxydir");

        if (vfs == null)
            vfs = new VFSClient();

        if (testdir == null)
            testdir = vfs.mkdir(tmpdir);

        proxyFactory = ProxyNode.getProxyNodeFactory();

    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {
        // clear cache each time:
        // ProxyNode.clearNodeHash();
    }

    public void testCache1() throws VlException
    {
        VRL etcDir = new VRL("file:///~");

        ProxyNode pnode = proxyFactory.getFromCache(etcDir);

        Assert.assertNull("ProxyNode should not already be in cache:" + etcDir, pnode);

        pnode = proxyFactory.openLocation(etcDir);
        Assert.assertNotNull("OpenLocation must succeed location:" + etcDir, pnode);

        pnode = proxyFactory.getFromCache(etcDir);
        Assert.assertNotNull("ProxyNode should  be in cache:" + etcDir, pnode);
    }

    // not applicable:
    // public void testAliasResolvingLocalFS() throws VlException
    // {
    // VRL aliasVRL=new VRL("file://localhost/~");
    //        	
    // // must not be in cache already!
    // ProxyNode pnode=proxyFactory.getFromCache(aliasVRL);
    // Assert.assertNull("ProxyNode should not already be in cache:"+aliasVRL,pnode);
    //
    // // open location (store in cache)
    // pnode=proxyFactory.openLocation(aliasVRL);
    // Assert.assertNotNull("OpenLocation must succeed for the test to be succesful for location:"+aliasVRL,pnode);
    //            
    // // test whether resolved pnode is 'logical equivalent'
    // boolean val=pnode.locationEquals(aliasVRL,false);
    // Assert.assertTrue("After opening an location, resolved location must be logical equivalents\n"
    // +"alias="+aliasVRL+"\n"
    // +"pnode="+pnode.getVRL(),val);
    //            
    // // compare with actual user.home:
    // VRL homeVRL=new VRL("file://localhost/"+System.getProperty("user.home"));
    // val=pnode.locationEquals(homeVRL,false);
    // Assert.assertTrue("Resolving localfile system's file://localhost/~, must resolve to:"+homeVRL
    // +" pnode vrl="+pnode.getVRL(),val);
    //                        
    // // get alias:
    // pnode=proxyFactory.getFromCache(aliasVRL);
    // Assert.assertNotNull("ProxyNode should  be in cache:"+aliasVRL,pnode);
    //            
    // // get real path:
    // ProxyNode pnode2=proxyFactory.getFromCache(homeVRL);
    // Assert.assertNotNull("Resolved ProxyNode should be in cache:"+homeVRL,pnode2);
    //            
    // // alias and resolved node must be same objects !
    // Assert.assertTrue("Both alias and resolved ProxyNode must be same object!",pnode==pnode2);
    //            
    //            
    // // compare with actual user.home: WIHTOUT localhost !
    //            
    // VRL homeVRL2=new
    // VRL("file://localhost/"+System.getProperty("user.home"));
    // val=pnode.locationEquals(homeVRL2,false);
    // Assert.assertTrue("Resolving localfile system's file:///~, must resolve to file:///${user.home}"
    // +"pnode="+pnode.getVRL(),val);
    //           
    // }

    public class Receiver implements ResourceEventListener
    {
        public boolean gotSetChildsEvent = false;

        public VRL[] childs = null;

        public boolean gotSetAttributesEvent = false;

        VAttribute attrs[] = null;

        public void notifyResourceEvent(ResourceEvent e)
        {
            switch (e.getType())
            {
                case SET_CHILDS:
                    gotSetChildsEvent = true;
                    childs = e.getChilds();

                    break;
                case SET_ATTRIBUTES:
                    this.gotSetAttributesEvent = true;
                    this.attrs = e.getAttributes();
                    break;
                default:
                    // gottit=false;
                    break;
            }
            Debug("GotEvent:" + e);
            synchronized (this)
            {
                notifyAll();
            }
        }

    };

    /*
     * public void testAsyncGetChilds() throws VlException { ProxyNode pnode=
     * proxyFactory.openLocation(new VRL("file:///"));
     * 
     * Receiver receiver = new Receiver();
     * 
     * pnode.asyncGetChilds(null, receiver);
     * 
     * try { synchronized(receiver) { receiver.wait(2*1000); }
     * 
     * } catch (InterruptedException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); } // alias and resolved node must be same objects !
     * Assert
     * .assertTrue("Receive object didn't recieve SET_CHILDS event",receiver
     * .gotSetChildsEvent); int index=0; for (VRL child:receiver.childs) {
     * Debug("child["+(index++)+"]="+child); } }
     */

    /*
     * public void testAsyncGetAttributes() throws VlException { ProxyNode
     * pnode= proxyFactory.openLocation(new VRL("file:///"));
     * 
     * Receiver receiver = new Receiver();
     * 
     * pnode.asyncGetAttributes(null,pnode.getAttributeNames(), receiver);
     * 
     * try { synchronized(receiver) { receiver.wait(2*1000); }
     * 
     * } catch (InterruptedException e1) { // TODO Auto-generated catch block
     * e1.printStackTrace(); } // alias and resolved node must be same objects !
     * Assert
     * .assertTrue("Receive object didn't recieve SET_ATTRIBUTES event",receiver
     * .gotSetAttributesEvent); int index=0; for (VAttribute
     * attr:receiver.attrs) { Debug("attr["+(index++)+"]="+attr); } }
     */

    private void Debug(String str)
    {
        System.err.println("Debug:" + str);

    }

}
