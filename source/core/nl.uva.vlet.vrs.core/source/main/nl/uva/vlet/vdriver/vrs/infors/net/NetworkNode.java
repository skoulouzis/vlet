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
 * $Id: NetworkNode.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import static nl.uva.vlet.vdriver.vrs.infors.InfoConstants.ATTR_NETWORK_ADRESS;
import static nl.uva.vlet.vdriver.vrs.infors.InfoConstants.NETWORK_INFO;

import java.net.UnknownHostException;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.exception.NotImplementedException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.CompositeServiceInfoNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.LinkNode;
import nl.uva.vlet.vrs.VEditable;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public class NetworkNode extends CompositeServiceInfoNode implements VEditable
{
    private static NetUtil netUtil = NetUtil.getDefault();

    private boolean rescan = true;

    public NetworkNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
        init();
    }

    private void init()
    {
        // Default to Resource Location;
        // this.setType(InfoConstants.NETWORK_INFO);
        // this.setName(getVRL().getBasename());
        try
        {
            this.setAddress("127.0.0.0/0");
        }
        catch (VlException e)
        {
            Global.errorPrintStacktrace(e);
        }
        setEditable(true);

        // setShowShortCutIcon(false);
        // setTargetIsComposite(true);
        setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
    }

    public String getMimeType()
    {
        return null;
    }

    // public NetworkNode duplicate()
    // {
    // NetworkNode infoNode=new NetworkNode(vrsContext,getVRL());
    // infoNode.copyFrom(this);
    // return infoNode;
    // }
    //
    // public InfoNode clone()
    // {
    // return duplicate();
    // }
    //
    public boolean isEditable()
    {
        return true;
    }

    public LinkNode toLinkNode() throws NotImplementedException
    {
        throw new nl.uva.vlet.exception.NotImplementedException("Cannot cast to link node");
    }

    public String[] getAttributeNames()
    {
        StringList names = new StringList(super.getAttributeNames());
        names.add(ATTR_NETWORK_ADRESS);
        return names.toArray();
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        VAttribute attr = super.getAttribute(name);
        return attr;
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getType()
    {
        return NETWORK_INFO;
    }

    public String getNetworkAddress()
    {
        String str = this.attributes.getStringValue(ATTR_NETWORK_ADRESS);
        if (str == null)
            return null;

        String strs[] = str.split("/");

        return strs[0];
    }

    /** Returns nr. of UNsignificant bits in network address range. */
    public int getNetworkBits()
    {
        String str = this.attributes.getStringValue(ATTR_NETWORK_ADRESS);
        if (str == null)
            return 0x00ff;

        String strs[] = str.split("/");
        if (strs.length > 1)
            return Integer.parseInt(strs[1]);

        // single address network!
        return 0;
    }

    public boolean setAttribute(VAttribute attr) throws VlException
    {
        if (attr.getName().equals(ATTR_NETWORK_ADRESS))
        {
            return setAddress(attr.getStringValue());
        }
        else
        {
            return super.setAttribute(attr);
        }
    }

    public boolean setAddress(String addrStr) throws VlException
    {
        this.childNodes.clear();
        this.rescan = true;
        // default
        VAttribute attr = new VAttribute(ATTR_NETWORK_ADRESS, addrStr);

        String networkBitStr = "0";

        String strs[] = addrStr.split("/");
        if ((strs != null) || (strs.length > 0))
        {
            addrStr = strs[0];
            if (strs.length > 1)
                networkBitStr = strs[1];
        }

        // auto lookup/resolve logical hostname
        if (netUtil.isIPAddress(addrStr) == false)
        {
            try
            {
                StringList ips;
                ips = netUtil.lookupAddresses(addrStr);
                if ((ips != null) && (ips.size() > 0))
                {
                    addrStr = ips.get(0);
                    if (addrStr != null)
                        attr = new VAttribute(ATTR_NETWORK_ADRESS, addrStr + "/" + networkBitStr);
                }
            }
            catch (UnknownHostException e)
            {
                throw new nl.uva.vlet.exception.VlException("Invalid Value",
                        "Not a valid IP Address of resolvable host:" + addrStr);
            }
        }

        return super.setAttribute(attr, true); // store in super
    }

    public VNode[] getNodes() throws VlException
    {
        if (rescan == true)
        {
            scanNodes();
        }
        return _getNodes();
    }

    private void scanNodes() throws VlException
    {
        // use childNodes as mutex !
        synchronized (this.childNodes)
        {
            // already done by previous thread !
            if (rescan == false)
                return;

            this.childNodes.clear();

            String ipAddr = this.getNetworkAddress();
            int maskbits = this.getNetworkBits();

            // hard coded limit!
            if (maskbits > 16)
                maskbits = 16;

            try
            {
                byte ip[] = NetUtil.parseIpaddress(ipAddr);
                ip = NetUtil.mask(ip, maskbits);

                for (int i = 0; i < (1 << maskbits); i++)
                {
                    try
                    {
                        resolveAndAddNode(ip, true);
                    }
                    catch (Exception e)
                    {
                        // info("Skipping:"+NetUtil.ip2string(ip)+"\nError="+e);
                    }

                    NetUtil.add(ip, 1);
                }
            }
            catch (UnknownHostException e)
            {
                throw new nl.uva.vlet.exception.VlConnectionException("Network Address Error", e);
            }

            rescan = false;
        }
    }

    private void resolveAndAddNode(byte[] ip, boolean scan) throws VlException
    {
        String ipstr = NetUtil.ip2string(ip);
        // info("Checking host:"+ipstr);
        String hostname = null;

        if (netUtil.existsAddress(ip) == false)
        {
            // info("No hostname for:"+ipstr);
            return;
        }

        try
        {
            hostname = netUtil.resolve(ip);
        }
        catch (UnknownHostException e)
        {
            // info("Couldn't resolve address:"+ipstr);
            return;
        }

        // use IP string a unique part in VRL:
        VRL vrl = this.resolvePathVRL(ipstr);

        HostInfoNode node = new HostInfoNode(this, vrl);
        // info("New node:"+node);

        node.setDNSHostname(hostname);
        node.setIPAdress(ip);
        this.addSubNode(node);

        // Asynchronous updates!
        this.fireChildAdded(vrl);
    }

}
