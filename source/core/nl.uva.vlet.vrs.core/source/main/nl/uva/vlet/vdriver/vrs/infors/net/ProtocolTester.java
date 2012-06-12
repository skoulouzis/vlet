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
 * $Id: ProtocolTester.java,v 1.1 2011-11-25 13:40:47 ptdeboer Exp $  
 * $Date: 2011-11-25 13:40:47 $
 */ 
// source: 

package nl.uva.vlet.vdriver.vrs.infors.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import nl.uva.vlet.Global;

/**
 * State FULL protocol tester. Keep state of socket connection after the test has been done. 
 * Socket state will have changed after check(Socket) has been called!
 *  
 * Not thread safe, although some methods are synchronised to avoid inconsistencies. 
 * @author P.T. de Boer. 
 */
public class ProtocolTester
{
    // Settings 
    private String name="DefaultPortTester"; 
    private boolean isSSL=false;
    
    private String protocol="tcp";
    
    // === State Mutex === // 
    private Object stateMutex=new Object();

    // === Results === //
    
    private Throwable exception;
    private byte[] responseBytes; 
    int responseTime=-1;
    private boolean validResult=false;  
    
    public ProtocolTester(String name,boolean isSSL)
    {
        this.name=name; 
        this.isSSL=isSSL;
    }
    
    public ProtocolTester(String name) 
    {
        this.name=name; 
    }
   
    /**
     * Check socket and return true if all tests are OK.
     * After the test, the state of the socket is unknown and should be closed. 
     */ 
    public boolean check(Socket sock) throws IOException
    {
        synchronized(stateMutex)
        {
            reset(); // Reset INSIDE mutex 

            // Repsonse Settings: 
            int waitMaxTime=100;
            
            if (this.isSSL==true)
                waitMaxTime=500; 
            
            int waitStartTime=10;
            int waitIncrementTime=10;
            
            InputStream inps=null; 
            OutputStream outps=null; 
            
            try
            {
                inps=sock.getInputStream();
                outps=sock.getOutputStream();
                outps.write(getReponseChallenge()); 
                
                int wait=waitStartTime;
                int totalWaited=0;
                long startTime=System.currentTimeMillis();
                
                int avail=inps.available();
                while ((avail<=0) && (totalWaited<waitMaxTime))
                {
                    try
                    {
                        //info("Waiting for input:"+wait+","+totalWaited+";"); 
                        
                        Thread.sleep(wait);
                        totalWaited+=wait;
                        wait+=waitIncrementTime;
                        if (totalWaited+wait>waitMaxTime)
                            wait=waitMaxTime-totalWaited; 
                    }
                    catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                    
                    avail=inps.available();
                }
                
                if (avail>0)
                {
                    long endTime=System.currentTimeMillis();
                    responseTime=(int)(endTime-startTime); 
                    
                    responseBytes=new byte[avail];
                    inps.read(responseBytes);
                    
                    validResult=checkResponse(responseBytes);
                }
                else
                {
                    // TIME OUT 
                    validResult=false;
                    responseTime=-999; // indicate error;  
                }
            }
            catch (IOException e)
            {
                this.exception=e; 
                validResult=false;
            }
            finally
            {
                if (inps!=null)
                    try {inps.close();} catch (Exception e){;}
                
                if (outps!=null)
                    try {outps.close();} catch (Exception e){;}
            }
            
            return validResult; 
        }
    }

    /** Reset state so this tester can be reused again */ 
    protected void reset()
    {
        synchronized(stateMutex)
        {
            this.responseBytes=null;
            this.responseTime=-1;
            this.exception=null;
            this.validResult=false; 
        }
    }
   
    public Throwable getException()
    {
        return this.exception; 
    }
    
    protected void setException(Throwable t)
    {
        this.exception=t; 
    }
    
    public int getReponseTime()
    {
        return this.responseTime; 
    }
    
    public String getName()
    {
        return this.name; 
    }
    
    public boolean hasValidResult()
    {
        return this.validResult; 
    }
    
    // =============================================================
    // Sub Class Methods 
    // ============================================================
    
    public boolean isSSL()
    {
        return this.isSSL;  
    }
    
    protected void setSSL(boolean val)
    {
        this.isSSL=val; 
    }
    
    protected byte[] getReponseChallenge()
    {
        return "Helo.\n\n\n".getBytes();  
    }
    
    // Default implementation returns TRUE if ANY kind of response whas generated 
    protected boolean checkResponse(byte[] bytes)
    {
        if ((bytes!=null) && (bytes.length>0)) 
            return true;
        
        return false; 
    }
 
    /** Return human readable response String */ 
    public String getReponseString()
    {
        if (responseBytes==null)
            return "<NULL reponse>"; 
        
        try
        {
            return parseResponse(responseBytes);
        }
        catch (Throwable t)
        {
            return "Could parse response:Exception="+t; 
        }
    }
    
    protected String parseResponse(byte response[])
    {
        if (response==null)
            return null; 
        
        return new String(response);  
    }

    public String getScheme()
    {
        return this.protocol; 
    }
    
    protected void setScheme(String scheme)
    {
        this.protocol=scheme; 
    }
   
}
