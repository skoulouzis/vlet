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
 * $Id: Scanner.java,v 1.1 2011-11-25 13:40:46 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:46 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.Global;

/** Multithreaded port scanner */ 
public class Scanner
{
    private static Scanner instance=null; 
    
    public static int[] defaultPorts=
        {
            7,
            21,
            22,
            23,
            25,
            80,
            443,
            2170,
            2171,
            2811,
            2812, 
            5010,
            8080,
            8443,
            8444,
            8445,
            8446,
            30000,
            30001,
            30002,
            30003,
            30004,
            30005,
            30006,
            30007,
            30008,
            30009,
            30010, 
            50000, 
            50001, 
        };
    
    static
    {
        
    }
    
    // === Instance === // 
    private int quickConnectTimeOut=50;
    
    private int connectedWaitForInputTime=500; 
    
    Vector<String> scheduledHosts=new Vector<String>();
    
    Vector<String> runningHosts=new Vector<String>();
    
    /** Map hostname to Vector of PortResults */ 
    Map<String,Map<Integer,PortInfo>> scanResult=new HashMap<String,Map<Integer,PortInfo>>();

    private NetUtil netUtil;

    
    Scanner(NetUtil netUtil)
    {
        this.netUtil=netUtil; 
    }
    
    public void scheduleHost(String hostname)
    {
        synchronized(scheduleMutex)
        {
            scheduledHosts.add(hostname); 
        }
     
        String host=nextHost();
        scanHost(host);
    }
    
    
    protected void scanHost(String host)
    {
        //info("> Scanning host:"+host); 
        
        // ===
        // private stateFULL port tester // 
        // ===
        
        // Phase I: Do  a quick scan!
        for (int i=0;i<defaultPorts.length;i++)
        {
            int port=defaultPorts[i]; 
            //info("> - Scanning host+port:"+host+":"+port);
            
            long startTime=0; 
            long endTime=0;
            // Protocol testers: Most specific first: First matched is used. 
            List<ProtocolTester> testers = PortInfo.getTestersFor(port); // new ProtocolTester("DefaultTester");
            boolean testerResult=false; 
             
            
            for (ProtocolTester portTester:testers)
            {
                //info("> - - Scanning protocol:"+host+":"+port+" for:"+portTester.getScheme());
                
                // new instance: 
                PortInfo info=new PortInfo(port);
                // .clear();
                // preset time out value: 
                info.tcpTimeout=quickConnectTimeOut;
                
              try
              { 
                  startTime=System.currentTimeMillis();
                  info.tcpConnectOK=false; // CLEAR
                  info.status=ConnectionStatus.CONNECTING;
                  Socket sock;
                  if (portTester.isSSL()==false)  
                      sock=netUtil.createSocket(host,info.port,info.tcpTimeout);
                  else
                  {
                      info.sslError=true; 
                      sock=netUtil.createSSLSocket(host,info.port,info.tcpTimeout);
                      info.sslError=false; 
                  }
                    
                  endTime=System.currentTimeMillis();
                  //info("port connected:"+host+":"+port);
                  info.status=ConnectionStatus.CONNECTED;  
                  info.tcpSetupTime=(int)(endTime-startTime);  
                  info.tcpConnectOK=true;
                    
                  // === 
                  // Check Port! 
                  // ===
                  {
                      testerResult=portTester.check(sock); 
                      info.reponseMsg=portTester.getReponseString(); 
                      info.responseTime=portTester.getReponseTime(); 
//                      if (testerResult==true)
//                      {
//                          info("> - - +++ Protocol matched:"+portTester.getScheme()+" (for:"+host+":"+port+")");
//                      }
                  }
                    
                  if (sock.isConnected())     
                      try {sock.close();} catch (Exception e) {;} // ignore close exceptions  
                      
                }
                catch (java.net.ConnectException e)
                {
                    String msg=e.getMessage();
                    if (msg.contains("Connection refused"))
                    {
                        //info("> - - *** Socket refused:"+host+":"+port);
                        info.status=ConnectionStatus.REFUSED;
                    }
    		    }
                catch (java.net.SocketTimeoutException e)
                {
                    String msg=e.getMessage();
                    if (msg.contains("connect timed out"))
                    {
                        // info("> - - *** Socket timed out:"+host+":"+port);
                        info.status=ConnectionStatus.TIMED_OUT;
                    }
                } 
                catch (java.net.NoRouteToHostException e)
                {
                    String msg=e.getMessage();
    //                if (msg.contains("No route to host"))
    //                {
                        //info("> - - *** Error: NO ROUTE:"+host+":"+port);
                        info.status=ConnectionStatus.ROUTE_BLOCKED;
    //                }
                } 
                // ================================================================
                // SSL
                // ================================================================
                catch (CertificateException e)
                {
                    //info("CertificateException for:"+host+":"+port);
                    // For certificate error: there was a SSL handshake 
                    info.status=ConnectionStatus.CONNECTED;
                    info.sslError=true;
                    info.exception=e; // keep
                    e.printStackTrace();
                }
                //
                // Must be handled :
                //
                catch (IOException e)
                {
                    //info("IOException for:"+host+":"+port);
                    info.status=ConnectionStatus.UNKNOWN;   
                    info.exception=e; // keep
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    //info("Exception for:"+host+":"+port);
                    info.status=ConnectionStatus.UNKNOWN;   
                    info.exception=e; // keep
                    e.printStackTrace();
                }
                
                boolean stop=false;
                // Analyse: 
                if (info.tcpConnectOK==true)
                {
                    if (testerResult==true)
                    {
                        info.possibleProtocol=portTester.getScheme();
                        stop=true; // found matching protocol
                    }
                    else
                    {
                        stop=false; // continue with next protocol; 
                    }
                }
                else
                {
                    stop=true; // TCP connection Error; 
                }
                
                // store overwrite previous !
                storePortInfo(host,info);
                
                if(stop==true)
                    break; 
            }// FOR loop;
            
        }
    }
    
    protected void storePortInfo(String name,PortInfo info)
    {
        synchronized(this.scanResult)
        {
            Map<Integer,PortInfo> list=this.scanResult.get(name); 
            if (list==null)
            {
                list=new HashMap<Integer,PortInfo>();
            }
            list.put(new Integer(info.port),info);  
            this.scanResult.put(name,list);
        }
    }

    /** Returns copy of current port results in private Array. */ 
    public PortInfo[] getPortInfos(String host)
    {
        synchronized(this.scanResult)
        {
            Map<Integer, PortInfo> infos = this._getPortInfos(host); 
        
            if ((infos==null) || (infos.size()<=0)) 
                return null;  
            
            int len=infos.size(); 
        
            PortInfo infoArr[]=new PortInfo[len];
            int index=0; 
            
            for (int i=0;i<defaultPorts.length;i++)
            {
                PortInfo info = infos.get(new Integer(defaultPorts[i])); 
                if (info!=null)
                    infoArr[index++]=info; 
            }
            
            return infoArr; 
        }
    }
    /** Returns copy of current port results in private Array. */ 
    public Map<Integer, PortInfo> _getPortInfos(String host)
    {
        synchronized(this.scanResult)
        {
            Map<Integer, PortInfo> infos = this.scanResult.get(host);
            if ((infos==null) || (infos.size()<=0)) 
                    return null; 
            return infos;
        }
    }
    
    /** Returns copy of current port results in private Array. */ 
    public PortInfo getPortInfo(String host,int port)
    {
        synchronized(this.scanResult)
        {
            Map<Integer, PortInfo> infos = this._getPortInfos(host); 
            
            if (infos==null)
                return null;; 
                
            return infos.get(new Integer(port));  
        }
    }

    Object scheduleMutex=new Object(); 
    
    protected String nextHost()
    {
        synchronized(scheduleMutex)
        {
            int len=this.scheduledHosts.size();
            
            if (len<=0)
                return null;
            
           String host=this.scheduledHosts.get(len-1);
           this.scheduledHosts.setSize(len-1); 
           this.runningHosts.add(host);
           return host; 
        }
    }
    
  

    public void printResults(String host)
    {
        Map<Integer, PortInfo> resultMap = this.scanResult.get(host);
        if (resultMap==null)
        {
            System.out.println("No result for:"+host); 
            return; 
        }
        
        for (int i=0;i<defaultPorts.length;i++)
        {
            PortInfo info = resultMap.get(new Integer(defaultPorts[i])); 
            if (info!=null)
            {
                System.out.print("port #:"+info.port+"="+info.status); 
                System.out.println(","
                        +info.possibleProtocol +"',"
                        +( (info.sslError==true)?"<SSLERROR>":"" )
                        +info.tcpSetupTime+"ms,"
                        +info.getResponseTime()+"ms;\treponse="+info.reponseMsg);  
            }
        }
    }
    
}
