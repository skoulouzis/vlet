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
 * $Id: CnsRenameRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;



/**
 * <p>
 * Encapsulates LFC server RENAME command request. Then receives and returns
 * response. 
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * 
 * @see CnsRenameResponse
 */
public class CnsRenameRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String oldPath;
  private String newPath;

  /**
   * Creates request for renaming the LFC entry.
   * @param oldPath 
   * @param newPath 
   * 
   */
  public CnsRenameRequest( final String oldPath, final String newPath ) {
    this.oldPath = oldPath;
    this.newPath = newPath;
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
   * @see CnsRenameResponse
   */
  public CnsVoidResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending RENAME: " + this.oldPath  ); 
    this.sendHeader( out,                       // header [12b]
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_RENAME,
                     30 + IOUtil.byteSize(oldPath,newPath));  
    
    out.writeInt( this.uid );  // user id [4b]
    out.writeInt( this.gid );  // group id [4b]
    out.writeLong( this.cwd ); // I have no idea what is this [8b]
//    if ( this.oldPath.length() != 0 ) {
//      out.write( this.oldPath.getBytes(), 0, this.oldPath.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for old path [1b]
//    
//    if ( this.newPath.length() != 0 ) {
//      out.write( this.newPath.getBytes(), 0, this.newPath.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for new path [1b]
    IOUtil.writeString(out,oldPath); 
    IOUtil.writeString(out,newPath);
    
    out.flush();
    
    CnsVoidResponse result = new CnsVoidResponse();
    result.readFrom( in );
    return result;
    
  }
}
