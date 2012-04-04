/*****************************************************************************
 * Copyright (c) 2007, 2008 g-Eclipse Consortium 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *****************************************************************************/

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsAddReplicaResponse;
import org.glite.lfc.internal.CnsConstants;




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
                         + this.server.length()
                         + this.guid.length()
                         + this.sfn.length()
                   );
    output.writeInt( this.uid ); 
    output.writeInt( this.gid );
    
    output.writeLong( 0 );  // don't use uniqueId
    
    output.writeBytes( this.guid );
    output.writeByte( 0x0 ); // terminating 0x0 for guid
    
    output.writeBytes( this.server );
    output.writeByte( 0x0 ); // terminating 0x0 for server

    output.writeBytes( this.sfn );
    output.writeByte( 0x0 ); // terminating 0x0 for sfn

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
