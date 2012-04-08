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
 * $Id: CnsAddReplicaRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsAddReplicaResponse;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;





/**
 * Encapsulates LFC server ADDREPLICA command request. Then receives and returns
 * response.
 * @see CnsAddReplicaResponse
 */
public class CnsAddReplicaRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private String server;
  private String sfn;
  private String guid;
  private byte status = 0x0;
  private byte fileType = 'P';
  
  /**
   * Creates request for add replica command
   */
  public CnsAddReplicaRequest( final String guid, final URI sfn ) {

    this.server = sfn.getHost();
    this.sfn = sfn.toString();
    this.guid = guid;
    this.uid = 0;
    this.gid = 0;
  }

  /**
   * Sets the status of the file
   * @param status the status to set
   */
  public void setStatus( final byte status ) {
    this.status = status;
  }

  /**
   * Sets the file class.
   * V = Volatile,
   * D = Durable,
   * P = Permanent.
   * @param fileClass the file class to set
   */
  public void setFileClass( final byte fileClass ) {
    this.fileType = fileClass;
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
  public CnsAddReplicaResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  {
    
    CnsAddReplicaResponse result = new CnsAddReplicaResponse();
    int bytes = 0;

    LFCServer.staticLogIOMessage(  "Sending request for replication of the file: " + this.guid );
    LFCServer.staticLogIOMessage( "   where replica SFN is: " + this.sfn );
    LFCServer.staticLogIOMessage( "   where server URL is: " + this.server );
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC4,
                     CnsConstants.CNS_ADDREPLICA,
                     37
                         + IOUtil.byteSize(server)
                         + IOUtil.byteSize(guid)
                         + IOUtil.byteSize(sfn)
                   );
    output.writeInt( this.uid ); 
    output.writeInt( this.gid );
    
    output.writeLong( 0 );  // don't use uniqueId
    
//    output.writeBytes( this.guid );
//    output.writeByte( 0x0 ); // terminating 0x0 for guid
    IOUtil.writeString(output,guid); 
    
//    output.writeBytes( this.server );
//    output.writeByte( 0x0 ); // terminating 0x0 for server
    IOUtil.writeString(output,server);
    
//    output.writeBytes( this.sfn );
//    output.writeByte( 0x0 ); // terminating 0x0 for sfn
    IOUtil.writeString(output,sfn);
    
    output.writeByte( this.status );
    output.writeByte( this.fileType);

//    output.writeBytes( "" );
    output.writeByte( 0x0 ); // terminating 0x0 for poolname

//    output.writeBytes( "" );
    output.writeByte( 0x0 ); // terminating 0x0 for fs

    output.writeByte( 0x0 ); // r_type

//    output.writeBytes( "" );
    output.writeByte( 0x0 ); // terminating 0x0 for setname

    output.flush();
    result.readFrom( input );
    return result;
    
  }
}
