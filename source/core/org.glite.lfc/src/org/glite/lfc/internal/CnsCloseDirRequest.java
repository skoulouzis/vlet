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
 * $Id: CnsCloseDirRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsCloseDirResponse;
import org.glite.lfc.internal.CnsConstants;



/**
 * Encapsulates LFC server CLOSEDIR command request. <br>
 * Then receives and returns response.
 * @see CnsCloseDirResponse  
 */
public class CnsCloseDirRequest extends AbstractCnsRequest {

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output  stream that request will be written to
   * @param in      stream that response will be read from 
   * @return        Response object
   * @throws IOException in case of any I/O problem
   * @see CnsCloseDirResponse
   */
  public CnsCloseDirResponse sendTo( final DataOutputStream output,
                                     final DataInputStream in )
    throws IOException  {

    CnsCloseDirResponse result = new CnsCloseDirResponse();
    LFCServer.staticLogIOMessage( Messages.lfc_log_send_closedir ); 
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_CLOSEDIR,
                     12 );
    
    output.flush();
    result.readFrom( in );
    return result;
    
  }
}