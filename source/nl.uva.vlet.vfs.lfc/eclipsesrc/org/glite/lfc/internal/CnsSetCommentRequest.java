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
import org.glite.lfc.internal.CnsLinkStatResponse;
import org.glite.lfc.internal.CnsSetCommentResponse;


/**
 * <p>
 * Encapsulates LFC server FILESIZE command request. Then receives and returns
 * response.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsLinkStatResponse
 */
public class CnsSetCommentRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String path; 
  private String comment;

  /**
   * Creates request for link detailed information.
   * @param path path to the file 
   */
  public CnsSetCommentRequest( final String path ) {
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    this.path = path;
    this.comment = CnsConstants.EMPTY_STRING;
  }

  /**
   * <p> Sets local user id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * 
   * @param uid local user id
   */
  public void setUid( final int uid ) {
    this.uid = uid;
  }

  /**
   * <p> Sets local group id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * @param gid local group id
   */
  public void setGid( final int gid ) {
    this.gid = gid;
  }
  
  /**
   * <p>Sets cwd parameter</p>
   * <p>I have no idea what it's for<br>
   * Use 0 (zero)</p>
   * 
   * @param cwd CWD - Change Working Directory? I have no idea.
   */
  public void setCwd( final long cwd ) {
    this.cwd = cwd;
  }
  
  /**
   * <p>Sets comment value</p>
   * @param comment new comment value 
   */
  public void setComment( final String comment) {
    this.comment = comment != null ? comment : CnsConstants.EMPTY_STRING;
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
  public CnsSetCommentResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage(  
     "Changing comment of " + this.path + ", to: " + this.comment );  //$NON-NLS-1$ //$NON-NLS-2$
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_SETCOMMENT,
                     30 + this.comment.length() 
                        + this.path.length() );
    
    out.writeInt( this.uid );  // user id
    out.writeInt( this.gid );  // group id

    out.writeLong( this.cwd );
    
    out.write( this.path.getBytes(), 0, this.path.length() );
    out.writeByte( 0x0 );
    
    out.write( this.comment.getBytes(), 0, this.comment.length() );
    out.writeByte( 0x0 );
    
    out.flush();
    
    CnsSetCommentResponse result = new CnsSetCommentResponse();
    result.readFrom( in );
    return result;
    
  }  
}
