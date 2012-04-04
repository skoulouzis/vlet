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
 * $Id: MyX509KeyManager.java,v 1.2 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.net.ssl;


import javax.net.ssl.X509KeyManager;

import nl.uva.vlet.ClassLogger;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Implementation of  X509KeyManager, which always returns one pair of a private key 
 * and certificate chain.
 * It manages "My Key" which is a private key.  
 */
public class MyX509KeyManager implements X509KeyManager 
{
   static ClassLogger log = null;
   
   static
   {
       log=ClassLogger.getLogger(MyX509KeyManager.class);
       //log.setLevelToDebug(); 
   }
   
   private final X509Certificate[] certChain;
   private final PrivateKey key;

   public MyX509KeyManager(Certificate[] cchain, PrivateKey key)
   {
       this.certChain = new X509Certificate[cchain.length];
       System.arraycopy(cchain, 0, this.certChain, 0, cchain.length);
       this.key = key;
   }

   //not used
   public String[] getClientAliases(String string, Principal[] principals) 
   {
       log.debugPrintf("getClientAliases()\n");
       return null;
   }


   // Intented to be implemented by GUI for user interaction, but we have only one key.
   public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) 
   {
       if (log.isLoggable(ClassLogger.DEBUG))
       {
           log.debugPrintf("chooseClientAlias()\n");
           for (int i = 0; i < keyType.length; i++) log.debugPrintf("keyType[" + i + "]=" + keyType[i]+"\n");
           for (int i = 0; i < issuers.length; i++) log.debugPrintf("issuers[" + i + "]=" + issuers[i]+"\n");
       }
       
       return "thealias";
   }

   //not used on a client
   public String[] getServerAliases(String string, Principal[] principals) 
   {
       log.debugPrintf("getServerAliases()\n");
       return null;
   }

   //not used on a client
   public String chooseServerAlias(String string, Principal[] principals, Socket socket) 
   {
       log.debugPrintf("chooseServerAlias()\n");
       return null;
   }

   public X509Certificate[] getCertificateChain(String alias) 
   {
       log.debugPrintf("getCertificateChain()\n");
       return certChain;
   }

   public PrivateKey getPrivateKey(String alias) 
   {
       log.debugPrintf("getPrivateKey()\n");
       return key;
   }
}
