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
 * $Id: CnsReadLinkRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsLinkStatResponse;


/**
 * Encapsulates LFC server READLINK command request. Then receives and returns
 * response.
 * <p>
 * Added By Piter T. de Boer (Based upon similar Request Types) 
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsLinkStatResponse
 */
public class CnsReadLinkRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
 
  private String path;

  /**
   * Creates request for link detailed information.
   * 
   * @param path link for which information is requested
   */
  public CnsReadLinkRequest( final String path ) {
    this.path = path;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    
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
   * (PDB: Obsoleted HMS Current Working Dir). 
   */
  public void setCwd( final long cwd ) {
    this.cwd = cwd;
  }
  
 
  /**
   * <p>Sets new path value</p>
   * <p>This parameter is also set by constructor</p>
   * @param path link for which detailed information will be fetched
   */
  public void setPath( final String path ) {
    this.path = path;
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
  public CnsSingleStringResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_lstat, this.path ) ); 
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_READLINK,
                     29 + IOUtil.byteSize(path) );
    
    out.writeInt( this.uid );  // user id 
    out.writeInt( this.gid );  // group id
    out.writeLong( this.cwd ); // Old HMS Current Working Dir (obsolete) 
    
    IOUtil.writeString(out,path); 
    
    out.flush();
    
    CnsSingleStringResponse result = new CnsSingleStringResponse();
    result.readFrom( in );
    return result;
    
  }
}
