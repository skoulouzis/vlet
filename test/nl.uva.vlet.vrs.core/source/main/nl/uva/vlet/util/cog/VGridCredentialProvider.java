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
 * $Id: VGridCredentialProvider.java,v 1.10 2011-04-18 12:00:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:37 $
 */ 
// source: 

package nl.uva.vlet.util.cog;

import java.security.PrivateKey;
import java.util.List;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.grid.voms.VO;


/** 
 * VGridCredential factory interface.
 * Different proxy (credentials) providers may be registered. 
 * This interface is common between them.    
 */
public interface VGridCredentialProvider
{
    /** List of supported credential types */ 
    public  String[] getCredentialTypes();

    /** Check whether type is supported */ 
    public boolean canCreateCredentialType(String type);
    
    /** Check whether source credential can be converted into desired credential type */ 
    public boolean canCreateCredential(VGridCredential sourceCred,String type);

    /** Create credential using the passprhase string */ 
    public VGridCredential createCredential(String passwdstr)  throws VlException;
    
    /** Convert or create Credential from the source Credential 
     * @throws VlException */ 
    public VGridCredential convertCredential(VGridCredential sourceCred,String type) throws VlException;
    
    /** Convert credential string, for example proxy string, to a credential */ 
    public VGridCredential createCredentialFromString(String proxyString) throws VlException;
    
    /** Read file and convert to (proxy) credential */ 
    public VGridCredential createCredentialFromFile(String proxypath) throws VlException;

    /** Create from credential "Object" (can be any type) */ 
    public VGridCredential createFromObject(Object globusCredential) throws VlException;

    // === Attributes === // 
    
    /** Return the default location of the user (pivate) key file (userkey.pem) */ 
    public String getDefaultUserKeyLocation();
   
    /** Return the default location of the user certificate file (usercert.pem) */ 
    public String getDefaultUserCertLocation();

    /**
     * Set default User Certificate location (usercert.pem). Use path==NULL to reset
     * value to implementation default. */  
    public void setDefaultUserCertFile(String usercertFilename);
    
    /**
     * Set default User Key location (userkey.pem). Use path==NULL to reset
     * value to implementation default. */  
    public void setDefaultUserKeyFile(String userkeyFilename);

    /** List of directories containing Root (CA) Certificates. */ 
    public void setRootCertificateLocations(List<String> directories);
    
    /** Return location which directories will be searched for root) CA certificates */ 
    public List<String> getRootCertificateLocations();

    /** Return Limetime in Hours! */ 
    public int getDefaultLifetime();

    /** Specificy default life time in Hours! */ 
    public void setDefaultLifetime(int time);
    
    /**
     * Returns default path where proxy files are created. 
     */
    public String getDefaultProxyFilename();

    /** 
     * Set default Credential (proxy) filename. Use filename==NULL to reset value 
     * to implementation default.
     * This path only effects new created proxy files.   
     */  
    public void setDefaultProxyFilename(String filename);

    /**
     * Set VO Name with optional groupname but WITHOUT leading "/"
     * @param voName value="&lt;VOName&gt;[/&lt;VOGroup&gt;]"
     */
    public void setDefaultVOName(String voName);
    
    /** Set optional VO Role */ 
    public void setDefaultVORole(String voName);
    
    /**
     * Returns VOName with optional groupname but WITHOUT leading "/"
     * @return value = &lt;VOName&gt;[/&lt;VOGroup&gt;]
     */
    public String getDefaultVOName();
    
    /** Get optional VO Role */ 
    public String getDefaultVORole();

    /** Enable/disable VOMS proxy creation. */ 
    public void setEnableVoms(Boolean boolean1);

    /** Whether VOMS proxy creation is enabled */ 
    public boolean getEnableVoms();

    /** Tries to resolve voName and return VO Info Object.  */  
    public VO getVO(String voName) throws VlException; 
    
    /** If possible return DECRYPTED PrivateKey */  
    public PrivateKey getUserPrivateKey(String passphrase) throws VlException;

    
}
