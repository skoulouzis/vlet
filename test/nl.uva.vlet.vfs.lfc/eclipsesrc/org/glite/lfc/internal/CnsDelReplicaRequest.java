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

import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsDelReplicaResponse;
import org.glite.lfc.internal.CnsLinkStatResponse;


/**
 * <p>
 * Encapsulates LFC server DELETE REPLICA command request. Then receives and
 * returns response. It removes replica information from catalogue, data stored
 * on Storage Element stays intact.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * 
 * @see CnsDelReplicaResponse
 */
public class CnsDelReplicaRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
   private String sfn;
  private String guid;
  private long id;

  /**
   * Creates request for deleting new LFC entry.
   * @param guid Global unique ID of the file
   * @param fileid local file ID
   * @param sfn link to replica
   * 
   */
  public CnsDelReplicaRequest( final String guid, final long fileid, final String sfn ) {
    this.guid= guid;
    this.sfn = sfn;
    this.uid = 0;
    this.gid = 0;
    this.id = fileid;
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param out output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsLinkStatResponse
   */
  public CnsDelReplicaResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending DELETE REPLICA: " + this.sfn  );
    LFCServer.staticLogIOMessage( "      \\______  GUID   : " + this.guid  );
    this.sendHeader( out,                       // header [12b]
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_DELREPLICA,
                     30 + this.guid.length() + this.sfn.length() ); // FIXME set the message size 
    
    out.writeInt( this.uid );  // user id [4b]
    out.writeInt( this.gid );  // group id [4b]
    out.writeLong( this.id ); // (server) unique id, better to use global unique ID [8b]
    if ( this.guid.length() != 0 ) {
      out.write( this.guid.getBytes(), 0, this.guid.length() );  // [size b]
    }
    out.writeByte( 0x0 ); // trailing 0 for guid [1b]

    if ( this.sfn.length() != 0 ) {
      out.write( this.sfn.getBytes(), 0, this.sfn.length() );  // [size b]
    }
    out.writeByte( 0x0 ); // trailing 0 for sfn [1b]
    
    out.flush();
    
    CnsDelReplicaResponse result = new CnsDelReplicaResponse();
    result.readFrom( in );
    return result;
    
  }
}
