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
 * $Id: GSSCredentialWrapper.java,v 1.6 2011-05-03 15:02:46 ptdeboer Exp $  
 * $Date: 2011-05-03 15:02:46 $
 */ 
// source: 

package nl.vlet.uva.grid.globus;

import java.security.PrivateKey;
import java.security.cert.Certificate;

import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.util.cog.VGridCredential;
import nl.uva.vlet.util.cog.VGridCredentialProvider;

import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class GSSCredentialWrapper implements VGridCredential
{
    private GSSCredential credential;
    private GlobusCredentialProvider provider;
    
    public GSSCredentialWrapper(GlobusCredentialProvider provider, GlobusGSSCredentialImpl gssCred)
    {   
        this.credential=gssCred;
        this.provider=provider; 
    }


    @Override
    public String getIssuer()
    {
        return null; 
    }

    @Override
    public long getTimeLeft()
    {
        try
        {
            return credential.getRemainingLifetime();
        }
        catch (GSSException e)
        {
            return -1; 
            // e.printStackTrace();
        }
    }

    @Override
    public boolean isType(String credentialType)
    {
        return StringUtil.endsWith(credentialType,GridProxy.GSS_CREDENTIAL_TYPE);
    }

    @Override
    public boolean saveCredentialTo(String path) throws VlException
    {
        throw new nl.uva.vlet.exception.NotImplementedException("Can't save a GSS Credential"); 
    }

    @Override
    public String getUserDN()
    {
        return null;
    }

    @Override
    public String getCredentialFilename()
    {
        return null;
    }

    @Override
    public VGridCredentialProvider getProvider()
    {
        return this.provider;  
    }

    @Override
    public String getUserCertFile()
    {
        return null;
    }

    @Override
    public String getUserKeyFile()
    {
        return null;
    }

    @Override
    public String getUserSubject()
    {
        return null;
    }

    @Override
    public String getVOName()
    {
        return null;
    }
    
    @Override
    public String getVORole()
    {
        return null;
    }
    
    @Override
    public String getVOGroup()
    {
        return null;
    }

    public GSSCredential getGSSCredential()
    {
        return this.credential; 
    }


    @Override
    public Certificate[] getProxyCertificateChain() throws VlException
    {
        return null;
    }


    @Override
    public PrivateKey getProxyPrivateKey() throws VlException
    {
        return null;
    }

}
