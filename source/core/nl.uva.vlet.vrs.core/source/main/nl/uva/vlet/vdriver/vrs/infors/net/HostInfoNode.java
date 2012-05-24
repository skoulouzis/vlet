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
 * $Id: HostInfoNode.java,v 1.1 2011-11-25 13:40:47 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:47 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vdriver.vrs.infors.CompositeServiceInfoNode;
import nl.uva.vlet.vdriver.vrs.infors.InfoConstants;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;


public class HostInfoNode extends CompositeServiceInfoNode<VNode>
{
    private static final String ATTR_IP_ADDRESS = "ipAddress";
    private static final String ATTR_DNS_HOSTNAME = "dnsHostname";
    
    // === //
    
    private static NetUtil netUtil=NetUtil.getDefault(); 
    
    // === //
    
    private boolean rescan=true;
    
    private NetworkNode parent=null; 
    
    public HostInfoNode(NetworkNode parent, VRL logicalLocation)
    {
        super(parent.getVRSContext(), logicalLocation);
        this.parent=parent; 
        this.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        this.setEditable(false); 
    }

    public String getName()
    {
        String hostname=this.attributes.getStringValue(ATTR_DNS_HOSTNAME); 
        // String ipaddr=this.attributes.getValue(ATTR_IP_ADDRESS);
        if (hostname==null)
            return getBasename();
        return hostname; 
    }
    
    public void setDNSHostname(String hostname)
    {
        this.attributes.set(ATTR_DNS_HOSTNAME,hostname);
    }
    
    public String getDNSHostname() 
    {
        return this.attributes.getStringValue(ATTR_DNS_HOSTNAME); 
    }

    public void setIPAdress(byte[] ip)
    {
        this.attributes.set(ATTR_IP_ADDRESS,NetUtil.ip2string(ip)); 
    }
    
    public void setIPAdress(String ipAdress)
    {
        this.attributes.set(ATTR_IP_ADDRESS,ipAdress);  
    }
    
    public String getIPAdress() 
    {
        return this.attributes.getStringValue(ATTR_IP_ADDRESS);  
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getType()
    {
        return InfoConstants.HOST_INFO_NODE; 
    }
    
    public VNode[] getNodes() throws VlException
    {
        if (rescan==true)
        {
            scanHost(); 
        }
        
        return super._getNodes(); 
    }

    Object scanMutex=new Object(); 
    
    private void scanHost()
    {
        synchronized(scanMutex)
        {
            if (rescan==false)
                return;// already rescanned by previous thread.
            
            rescan=false;// pre-emptive assertion
            
            Scanner scanner = netUtil.getScanner();
            String host=this.getDNSHostname(); 
            scanner.scanHost(host);
            
            PortInfo[] infos = scanner.getPortInfos(host);
            
            for (PortInfo info:infos)
            {
                try
                {
                    if (info.isValidConnection())
                    {
                        VRL subVrl=this.resolvePathVRL(""+info.port);
                        String scheme=info.getProtocol();   
                        
                        VRL targetVrl=new VRL(scheme,null,host,info.port,"/");
                        
                        ServiceInfoNode node=ServiceInfoNode.createNode(this,subVrl,targetVrl,info); 
                        
                        this.addSubNode(node); 
                    }
                }
                catch (Exception e)
                {
                    error("Failed to add port:"+info.port+":"+e); 
                }
            }
        }
    }

    private void error(String msg)
    {
       Global.errorPrintf(this, msg+"\n");
    }
}
