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
 * $Id: TestScanElab.java,v 1.1 2011-12-06 09:59:18 ptdeboer Exp $  
 * $Date: 2011-12-06 09:59:18 $
 */ 
// source: 

package test.misc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.vdriver.vrs.infors.net.NetUtil;

public class TestScanElab
{

    public static void main(String args[])
    {
        String addrName="elab"; 
        try
        {
            NetUtil net=NetUtil.getDefault();
            InetAddress ipaddr = java.net.InetAddress.getByName(addrName);
            byte ips[]=ipaddr.getAddress();
            
            for (int i=0;i<256;i++)
            {
                ips[3]=(byte)(i&0x00ff); 
                try
                {
                    String ipstr=NetUtil.ip2string(ips);  
                    
                    System.out.println(" IP address      ="+ipstr);
                    System.out.println(" - existsAddress ="+net.existsAddress(ips)); 
                    String host=net.resolve(ips);
                    System.out.println(" - DNS hostname  ="+host);
                    System.out.println(" - existsHost    ="+net.existsHost(host)); 
                    
                    StringList names=net.lookupNames(host); 
                    System.out.println(" - DNS hostnames ="+names.toString(","));
                    
                    StringList adrs=net.lookupAddresses(host); 
                    System.out.println(" - DNS addresses ="+adrs.toString(",")); 
                    
                    //System.out.println(" - is reachable ="+newaddr.isReachable(5*1000)); 
                    //System.out.println(" - hostname     ="+newaddr.getCanonicalHostName());
                }
                catch (UnknownHostException e)
                {
                    System.out.println(" - *** Unknown host:"+NetUtil.ip2string(ips)); 
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Exception:"+e);   
        }
    }
}
