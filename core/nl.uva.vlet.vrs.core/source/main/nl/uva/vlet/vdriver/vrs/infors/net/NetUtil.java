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
 * $Id: NetUtil.java,v 1.1 2011-11-25 13:40:47 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:47 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import nl.uva.vlet.data.StringList;
import nl.uva.vlet.net.ssl.SslUtil;
import sun.net.spi.nameservice.dns.DNSNameService;

public class NetUtil
{
    private static NetUtil instance=null; 
    
    public static NetUtil getDefault()
    {
        if (instance==null)
            instance=new NetUtil();
        
        return instance; 
    }
    
   // === Instance === /// 
    
   private DNSNameService dns=null;
   
   /** One IP address might point to multiple hostnames. 
    * IP addresses are stored as dotted decimals */
   private Map<String,StringList> ipadress2host=new HashMap<String,StringList>();
   
   /** One hostname might have multiple ipaddresses. 
    * IP addresses are stored as dotted decimals */ 
   private Map<String,InetAddress[]> host2ipaddress=new HashMap<String,InetAddress[]>();

private Scanner scanner; 
   
   private NetUtil()
   {
       try
       {
           dns=new DNSNameService();
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
   }
   
   /** Clears cache */ 
   public void clear()
   {
       this.ipadress2host.clear(); 
       this.host2ipaddress.clear(); 
   }
   
   
   /**
    *  Look up IP address and return host name.  
    * @throws UnknownHostException 
    */ 
   public String resolve(byte ip[]) throws UnknownHostException
   {
       // CACHE DNS requests!
       synchronized(this.ipadress2host)
       {
           String ipstr=ip2string(ip); 
       
           StringList hostList= this.ipadress2host.get(ipstr); 
           if (hostList!=null)
               if (hostList.size()>0)
                    return hostList.get(0); 
       
           String hostname=dns.getHostByAddr(ip);
           if (hostname==null)
               return null; // throw ? 
           this.ipadress2host.put(ipstr,new StringList(hostname)); 
           return hostname;
       }
   }
   
   /** Returns hostnames and aliases */ 
   public StringList lookupNames(String name) throws UnknownHostException
   {
       StringList names=new StringList();  
       
       // cached lookup 
       InetAddress[] addres = lookup(name); 
        
       for (InetAddress addr:addres)
       {
           // only add unique hostnames
           names.add(addr.getHostName(),true); // getHostName()); 
       }
       
       return names; 
   }
   
   /** Returns IP addresses of hostname */ 
   public StringList lookupAddresses(String name) throws UnknownHostException
   {
       StringList names=new StringList();  
       // cached lookup 
       InetAddress[] addres = lookup(name); 
        
       for (InetAddress addr:addres)
       {
           // only add unique address
           names.add(addr.getHostAddress(),true); // getHostName()); 
       }
       
       return names; 
   }
   
   /** Performs a cached resolve of a hostname */ 
   public InetAddress[] lookup(String name) throws UnknownHostException
   {
       InetAddress[] addrs=null;
       
       synchronized(this.host2ipaddress)
       {
           addrs=this.host2ipaddress.get(name);
       
           if (addrs==null)
           {
               addrs= dns.lookupAllHostAddr(name);
               if (addrs!=null)
                   host2ipaddress.put(name,addrs); 
           }
       }
       
       synchronized(ipadress2host)
       {

           // store reverse ass well !
           for (InetAddress addr:addrs)
           {
               String ipstr=ip2string(addr.getAddress()); 
               StringList hostlist=this.ipadress2host.get(ipstr);
               if (hostlist==null)
                   hostlist=new StringList(); 

               hostlist.addUnique(name); 
               this.ipadress2host.put(ipstr,hostlist); 
           }
       }
       
       return addrs;
   }
   
   /** Whether HOSTname exists by qyering DNS */ 
   public boolean existsHost(String name)
   {
       try
       {
           return (lookup(name)!=null); 
       }
       catch (UnknownHostException e)
       {
           return false; 
       }
   }
   
   /** Whether HOSTname exists by qyering DNS */ 
   public boolean existsAddress(byte ip[])
   {
       try
       {
           return (resolve(ip)!=null); 
       }
       catch (UnknownHostException e)
       {
           return false; 
       }
   }
   
   public void scan(InetAddress start, InetAddress end)
   {
       byte[] ipStart = start.getAddress();
       byte[] ipEnd = end.getAddress(); 

       // network order means ip[0] is Highest netwerk adress (Big Endian). 
       
       if (ipStart.length!=ipEnd.length) 
           throw new Error("IP Adress missmatch. network adresses are not similar. Length "+ipStart.length+"<>"+ipEnd.length);
   
       //byte ip[]=new byte[ipStart.length]; 
       
   }
   
   

   public static String ip2string(byte[] ips)
   {
       int i=0; 
       String ipstr=""; 
       for (i=0;i<ips.length-1;i++)
           ipstr+=(ips[i]&0x00ff)+".";
       ipstr+=ips[i]&0x00ff;
       
       return ipstr; 
   }

   public static byte[] parseIpaddress(String ipAddr) throws java.net.UnknownHostException
   {
       String strs[]; 
       
       if (ipAddr.contains(":")) 
           strs=ipAddr.split(":");
       else
           strs=ipAddr.split("[.]");
       
       if ((strs==null) || (strs.length<4)) 
           throw new UnknownHostException("Invalid IP Adress. Not enough numbers:"+ipAddr); 
       
       byte ips[]=new byte[strs.length];
       try
       {
           for (int i=0;i<strs.length;i++)
               ips[i]=(byte)Integer.parseInt(strs[i]);
       }
       catch (Error e)
       {
           throw new UnknownHostException("Invalid IP Adress:"+ipAddr+"\n Error="+e.getMessage()); 
       }
       
       return ips; 
   }

   /** 
    * Returns network address using nr. of maskbits as mask. 
    * For example 192.168.1.123/8 returns 192.168.1.0 
    * 192.168.1.31/4 returns 192.168.1.16
    * @param ip
    * @param maskbits
    * @return
    */
   public static byte[] mask(byte[] ip, int maskbits)
   {
       int nrbytes=(maskbits+7)/8; // Upper(bits/8)  
       int len=ip.length; 
       
       for (int i=0;i<nrbytes;i++)
       {
           int mask=0; 
           // first byte, start with lowest byte (last one)
           if (maskbits>=8)
               mask=0x0000; // erase network bits  
           else
           {
               mask=(0x0001<<maskbits)-1; // 0xffff....
               mask=0x00ff-(mask&0x00ff); // reverse mask; 
           }
           
           ip[len-i-1]=(byte)(ip[len-i-1]&mask);
           maskbits-=8; // next byte   
       }
       
       return ip; 
   }

   public static void add(byte[] ip, int val)
   {
       int newval=0; 
       for (int i=ip.length-1;(i>=0)&&(val>0);i--)
       {
           newval=ip[i]+val;
           ip[i]=(byte)(newval&0x00ff);  // lower 8 bits 
           // carry remainder: 
           newval=newval>>8; 
           if (newval>0) 
           {
               val=newval; // shift and add remainder to higher byte;
               continue; 
           }
           break; //done
       }
   }

   public Socket createSocket(String host,int port,int timeout) throws IOException
   {
       Socket socket = new Socket(); 
       socket.connect(new InetSocketAddress(host, port),timeout);  
       return socket; 
   }
   
   public Socket createSSLSocket(String host,int port,int timeout) throws Exception
   {
       Socket socket =  SslUtil.openSSLSocket(host,port,timeout); 
       return socket; 
   }

   
   public Scanner getScanner()
   {
      if (this.scanner==null)
          this.scanner=new Scanner(this); 
    
      return scanner; 
   }

   public boolean isIPAddress(String value)
   {
       try
       {
           return (parseIpaddress(value)!=null); 
       }
       catch (Throwable e)
       {
           return false; 
       }
   }
   
}
