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
 * $Id: VGridCredential.java,v 1.6 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.util.cog;

import java.security.PrivateKey;
import java.security.cert.Certificate;

import nl.uva.vlet.exception.VlException;

/**
 * Abstract interface for Globus Credential (Grid Proxy) object types.
 * This interface is needed to be able to support multiple proxy (credential) types. 
 */  
public interface VGridCredential
{
    // === Attributes === //
    
    /** Returns time left in seconds. -1 is not Valid (anymore)  */ 
    public long getTimeLeft();

    /** Returns Issuer ID */ 
    public String getIssuer();

    /** User DN */ 
    public String getUserDN();
    
    /** User Subject*/ 
    public String getUserSubject();

    public boolean isType(String credentialType);

    // === Factory === // 
    public VGridCredentialProvider getProvider();
    
    /** Save credential and update filename. */ 
    public boolean saveCredentialTo(String path) throws VlException;

    // === Attributes === //
    
    /**
     * User Certificate used to create this proxy (usercert.pem).
     * Can be null if credential isn't valid. 
     */
    public String getUserCertFile();

    /** 
     * User Key used to create this proxy (userkey.pem).
     * Can be null if credential isn't valid.
     */ 
    public String getUserKeyFile();

    /**
     * Optional filename which contains this proxy credential. 
     * Is null if the proxy hasn't been saved! 
     */   
    public String getCredentialFilename();

    // === Proxy Stuff === // 
    
    /** Return Credential (proxy) private key part which is the signed proxy. */   
    public PrivateKey getProxyPrivateKey() throws VlException;
    
    /** Return Credential (proxy) certificate chain which are use to sign the proxy. */  
    public Certificate[] getProxyCertificateChain() throws VlException; 

    // === VOMS === // 

    /**
     * Return VO name if this proxy is VOMS enabled. 
     * Returns null if credential has no VOMS attribute. 
     */  
    public String getVOName();
    
    public String getVOGroup();
    
    public String getVORole();

}
