/*****************************************************************************
 * Copyright (c) 2007, 2007 g-Eclipse Consortium 
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
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsReadDirResponse;


/**
 * Encapsulates LFC server READDIR command request. Then receives and returns
 * response.
 * @see CnsReadDirResponse
 *
 */
public class CnsReadDirRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long fileid;

  /**
   * Creates request for READDIR with specified directory unique id
   * @param id directory unique id
   */
  public CnsReadDirRequest( final long id ) {
    this.fileid = id;
    this.uid = 0;
    this.gid = 0;
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsReadDirResponse
   */
  public CnsReadDirResponse sendTo( final DataOutputStream output, final DataInputStream in )
    throws IOException  {

    CnsReadDirResponse result = new CnsReadDirResponse();
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_readdir, new Long( this.fileid) ) ); 
    this.sendHeader( output, CnsConstants.CNS_MAGIC2, CnsConstants.CNS_READDIR, 34 );

    output.writeInt( this.uid ); 
    output.writeInt( this.gid );
    // PTdB: todo: find out these values. 
    output.writeShort( 4 ); // 1 = full list (w/o comments), 0 = names only
    output.writeShort( 50 ); 
    output.writeLong( this.fileid ); 
    output.writeShort( 1 );
    output.flush();
    
    result.readFrom( in );
    
    return result;
    
  }
}
