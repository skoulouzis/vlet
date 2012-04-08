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
 * $Id: CnsChmodRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsListReplicaResponse;


/**
 * chmod Request.
 * 
 * @author S. Koulouzis
 */

public class CnsChmodRequest
{
    private int uid = 0;
    private int gid = 0;
    private long cwd = 0;
    private String path = null;
    private int mode;

    /**
     * Creates request for chmod.
     */
    public CnsChmodRequest(final String path, int mode)
    {
        this.path = path;
        this.uid = 0;
        this.gid = 0;
        this.cwd = 0;
        this.mode = mode;
    }

    public CnsChmodResponse sendTo(final DataOutputStream output,
            final DataInputStream input) throws IOException
    {
        LFCServer
                .staticLogIOMessage("Sending chmod request for: "
                        + this.path);
        
        // Build request header
        CnsMessage msg = CnsMessage.createSendMessage(CnsConstants.CNS_MAGIC,
                CnsConstants.CNS_CHMOD);

        DataOutputStream dataOut = msg.createBodyDataOutput(4096);
        mode &= 07777;

        //Build request body
        dataOut.writeInt(this.uid); // +4
        dataOut.writeInt(this.gid); // +4
        dataOut.writeLong(this.cwd); // +8
        IOUtil.writeString(dataOut, path); // +1+length()
        dataOut.writeInt(this.mode);
        // no need to flush databuffer not close it.
        
        // finalize and send !
        int numSend = msg.sendTo(output);
        output.flush(); // sync
        msg.dispose();

        CnsChmodResponse result = new CnsChmodResponse();
        
        result.readFrom( input );
        return result;

    }

}
