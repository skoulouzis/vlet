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
 * $Id: SSHUtil.java,v 1.3 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.util;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VResourceSystem;
import nl.uva.vlet.vrs.net.VOutgoingTunnelCreator;

public class SSHUtil
{

    public static int createSSHTunnel(VRSContext context, 
            String scheme,
            String host, 
            int port) throws VlException
    {
        
        String tunnelHost=context.getConfigManager().getSSHTunnelRemoteHost(scheme,host,port); 
        
        VRL sshProxyHost=new VRL("sftp://"+tunnelHost+"/");
        
        VResourceSystem rs = context.openResourceSystem(sshProxyHost); 
        if (rs instanceof VOutgoingTunnelCreator)
        {
            int lPort=((VOutgoingTunnelCreator)rs).createOutgoingTunnel(host,port); 
            return lPort;
        }
        else
            throw new nl.uva.vlet.exception.NotImplementedException("Resource cannot create tunnel(s):"+rs); 
    }        

}
