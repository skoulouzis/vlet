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
import java.util.UUID;

import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsCreatGResponse;
import org.glite.lfc.internal.CnsLinkStatResponse;
import org.glite.lfc.internal.FileDesc;


/**
 * <p>
 * Encapsulates LFC server CREATG command request. Then receives and returns
 * response.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsCreatGResponse
 */
public class CnsCreatGRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String path;
  private String guid; // Global Unique ID

  /**
   * Creates request for registering new LFC entry.
   * 
   * @param path where the entry will be created
   */
  public CnsCreatGRequest( final String path ) {
    this.path = path;
    this.guid = UUID.randomUUID().toString();
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    
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
  public CnsCreatGResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending CREAT-G: " + this.path  ); 
    LFCServer.staticLogIOMessage( "GENERATED GUID: " + this.guid );
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_CREAT,
                     36 + this.path.length() + this.guid.length() ); // FIXME set the message size 
    
    out.writeInt( this.uid ); // user id
    out.writeInt( this.gid ); // group id
    out.writeShort( 0 ); // mask
    out.writeLong( this.cwd ); // I have no idea what is this

    if ( this.path.length() != 0 ) {
      out.write( this.path.getBytes(), 0, this.path.length() );
    }
    out.writeByte( 0x0 ); // trailing 0 for path
    
    out.writeInt(        FileDesc.S_IRGRP
                       | FileDesc.S_IROTH
                       | FileDesc.S_IRUSR
                       | FileDesc.S_IWGRP
                       | FileDesc.S_IWUSR  );  // mode
    
    if ( this.guid.length() != 0 ) {
      out.write( this.guid.getBytes(), 0, this.guid.length() );
    }
    out.writeByte( 0x0 );
    
    out.flush();
    
    CnsCreatGResponse result = new CnsCreatGResponse();
    result.readFrom( in );
    return result;
    
  }

  /**
   * @return the guid
   */
  public String getGuid() {
    return this.guid;
  }

}
