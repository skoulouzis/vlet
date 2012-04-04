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
 * $Id: CnsListReplicaRequest.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsListReplicaResponse;



/**
 * Encapsulates LFC server LISTREPLICA command request. Then receives and returns
 * response.
 * @see CnsListReplicaResponse
 *
 */
public class CnsListReplicaRequest extends AbstractCnsRequest {
  
  
  private int uid;
  private int gid;
  private long cwd;
  private String guid = null;
  /**
   * Creates request for list replica command
   * 
   * @param guid Global unique ID of the file which replica information will be retrieved 
   */
  public CnsListReplicaRequest( final String guid ) {
    this.guid = guid;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }


  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output output stream to which request will be written
   * @param input input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsOpenDirResponse
   */
  public CnsListReplicaResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  {
    
    CnsListReplicaResponse result = new CnsListReplicaResponse();
    int bytes = 0;

    LFCServer.staticLogIOMessage(  "Sending replica information request for: " + this.guid );     
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_LISTREPLICA,
                     IOUtil.byteSize(guid) + 35 );
    
    output.writeInt( this.uid ); 
    output.writeInt( this.gid ); 
    output.writeShort( 0 );//1296 ); // size of nbentry
    output.writeLong( this.cwd );
    
    output.writeByte( 0x0 ); // NO ACTUAL PATH!
    
//    if ( this.guid != null ) {
//      output.writeBytes( this.guid );
//    }
//    output.writeByte( 0x0 ); // terminating 0x0 for guid
    IOUtil.writeString(output,guid); 
    
    output.writeShort( 1 ); // BOL (begin of list)
    
    output.flush();
    result.readFrom( input );
    return result;
    
  }
}
