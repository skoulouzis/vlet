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
 * $Id: CnsUnlinkRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
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
import org.glite.lfc.internal.CnsCreatGResponse;
import org.glite.lfc.internal.CnsLinkStatResponse;
import org.glite.lfc.internal.CnsUnlinkResponse;


/**
 * <p>
 * Encapsulates LFC server UNLINK command request. Then receives and returns
 * response. Use this command instead of DELETE to remove files. DELETE command
 * is for CASTOR entries only
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * 
 * @see CnsCreatGResponse
 */
public class CnsUnlinkRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String path;

  /**
   * Creates request for deleting new LFC entry.
   * 
   * @param path of the deleting entry
   */
  public CnsUnlinkRequest( final String path ) {
    this.path = path;
    this.uid = 1003;
    this.gid = 513;
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
  public CnsUnlinkResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending UNLINK: " + this.path  ); 
    this.sendHeader( out,                       // header [12b]
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_UNLINK,
                     29 + IOUtil.byteSize(path)); // this.path.length() ); // FIXME set the message size 
    
    out.writeInt( this.uid );  // user id [4b]
    out.writeInt( this.gid );  // group id [4b]
    out.writeLong( this.cwd ); // I have no idea what is this [8b]
//    if ( this.path.length() != 0 ) {
//      out.write( this.path.getBytes(), 0, this.path.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for path [1b]
//    
    IOUtil.writeString(out,path); 
    
    out.flush();
    
    CnsUnlinkResponse result = new CnsUnlinkResponse();
    result.readFrom( in );
    return result;
    
  }
}
