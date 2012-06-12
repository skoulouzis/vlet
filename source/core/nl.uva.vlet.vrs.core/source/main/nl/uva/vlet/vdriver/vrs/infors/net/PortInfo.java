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
 * $Id: PortInfo.java,v 1.1 2011-11-25 13:40:47 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:47 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.vdriver.vrs.infors.net.testers.GFTPTester;
import nl.uva.vlet.vdriver.vrs.infors.net.testers.HTTPSTester;
import nl.uva.vlet.vdriver.vrs.infors.net.testers.HTTPTester;
import nl.uva.vlet.vdriver.vrs.infors.net.testers.SSHTester;

public class PortInfo
{
    /** 
     * Map port to protocols. 
     * Ports and protocols are matched at a first matched base. 
     * Put MOST specific protocol types FIRST, LEAST specific LAST. 
     * For example:<br> 
     *  - {"8443"},{"srm","https"} => Try SRM before HTTPS.<br>
     * <p>  
     * Multiple protocol definitions are merged. 
     */ 
    private static String portMap[][][]=
        {
            {{"21"},{"smtp"}},
            {{"22"},{"sftp","ssh"}},
            {{"23"},{"telnet"}},
            {{"25"},{"ftp"}},
            {{"8443","8444","8445","8446"},{"srm"}},
            {{"443","8443"},{"https"}},
            {{"80","8080"},{"http"}},
            {{"2170","2171","2172"},{"bdii"}},
            {{"2811","2812"},{"gftp"}},
            {{"5010","5011"},{"lfn"}},
            {{"30000","30001","30002","30003","30004","30005","30006","30007","30008","30009",
              "30010","30010","30011","30012","30013","30013","30014","30015","20016"},{"voms"}}
        };
    
    /** Mapping of one port to many protocols (schemes */ 
    private static Map<Integer,StringList> port2protocols=new HashMap<Integer,StringList>(); 
    
    /** Registry for protocol (scheme) testers */ 
    private static Map<String,ProtocolTester> testerRegistry=new HashMap<String,ProtocolTester>(); 
        
    private static ProtocolTester defaultTester=null; 
    
    static
    {
       staticInit(); 
    }
   
   private static void staticInit()
   {
       defaultTester = new ProtocolTester("Default");
       
       for (String line[][]:portMap)
       {
           String portList[]=line[0]; 
           StringList protoList=new StringList(line[1]);
           // register ports 
           for (String port:portList)
           {
               Integer portI=new Integer(port); 
               StringList list=port2protocols.get(portI);
               if (list==null)
                   list=protoList.duplicate(); // new port list 
               else
                   list.merge(protoList);
               port2protocols.put(portI,list); //
               //info("Registering:"+portI+" => "+list);
           }
       }
       
       // Key is LOWER case scheme string:
   
       testerRegistry.put("default",defaultTester);
       testerRegistry.put("tcp",defaultTester);
       testerRegistry.put("http",new HTTPTester());
       testerRegistry.put("https",new HTTPSTester());
       testerRegistry.put("gftp",new GFTPTester());
       testerRegistry.put("sftp",new SSHTester("sftp")); // sftp is more specific then ssh
       testerRegistry.put("ssh",new SSHTester("ssh")); // generic ssh
   }
   
   public static String[] getProtocols(int port)
   { 
       StringList list=port2protocols.get(new Integer(port)); 
       if ((list!=null) && (list.size()>0)) 
           return list.toArray();
       
       return null; 
  }
  
   static ProtocolTester getTesterFor(String scheme)
   {
       ProtocolTester tester = testerRegistry.get(scheme);
       if (tester!=null)
           return tester;
       
       return defaultTester;
   }
   
   // =========================================================================
   // 
   // =========================================================================

   int port=-1;
   
   // Status Information  
   
   /** Whether sockect connect was succesful */ 
   boolean tcpConnectOK=false;
   
   /** Time for initial TCP setup */ 
   int tcpSetupTime=-1; 
   
   /** Time out when TCP setup wasn't succesful */ 
   int tcpTimeout=-1; 
   
   /** Resulting Connection Status */ 
   ConnectionStatus status=ConnectionStatus.UNKNOWN;
   
   /** Human readable message by which the server responded */ 
   String reponseMsg=null;
   
   String possibleProtocol=null; 
   
   /** Total time it took the server to respond after the initial HELO message was send */ 
   int responseTime=-1;

   boolean sslError=false;

   Throwable exception=null;   
       
   public PortInfo(int portVal)
   {
       this.port=portVal; 
   }
   
   public void clear()
   {
       tcpConnectOK=false;
       tcpSetupTime=-1; 
       tcpTimeout=-1; 
       status=ConnectionStatus.UNKNOWN;
       reponseMsg=null;
       possibleProtocol=null; 
       responseTime=-1;
       sslError=false;
       exception=null;   
   }
   
   public ConnectionStatus getStatus()
   {
       return status; 
   }
   
   public boolean isValidConnection()
   {
       return (status==ConnectionStatus.CONNECTED); 
   }
  
   int getResponseTime()
   {
       return responseTime;
   }
   
   /** Returned testers for port, Most specific first ! */ 
   public static List<ProtocolTester> getTestersFor(int port)
   {
       ArrayList<ProtocolTester> testers=new ArrayList<ProtocolTester>(); 
       
       StringList schemeList=port2protocols.get(new Integer(port));
       
       if ((schemeList==null) || (schemeList.size()<=0))
       {
           testers.add(defaultTester); 
           return testers; 
       }
       
       //info("Returning testers for:"+port+"="+schemeList.toString(",")); 
       
       for (String scheme: schemeList)
       {
           ProtocolTester tester = testerRegistry.get(scheme);
           if ((tester!=null) &&  (testers.contains(tester)==false))
               testers.add(tester); 
           // end with default: 
           testers.add(defaultTester); 
       }
       
       return testers; 
   }
       
   public String getProtocol()
   {
       return this.possibleProtocol; 
   }
}
