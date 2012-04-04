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
 * $Id: CnsCreatGRequest.java,v 1.2 2011-04-18 12:30:37 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:37 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.glite.lfc.IOUtil;
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
  public CnsCreatGRequest( final String path ) 
  {
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
                     36 + IOUtil.byteSize(path,guid) ); // FIXME set the message size 
    
    out.writeInt( this.uid ); // user id
    out.writeInt( this.gid ); // group id
    out.writeShort( 0 ); // mask
    out.writeLong( this.cwd ); // I have no idea what is this

//    if ( this.path.length() != 0 ) {
//      out.write( this.path.getBytes(), 0, this.path.length() );
//    }
//    out.writeByte( 0x0 ); // trailing 0 for path
    IOUtil.writeString(out,path); 
    
    out.writeInt(        FileDesc.S_IRGRP
                       | FileDesc.S_IROTH
                       | FileDesc.S_IRUSR
                       | FileDesc.S_IWGRP
                       | FileDesc.S_IWUSR  );  // mode
    
//    if ( this.guid.length() != 0 ) {
//      out.write( this.guid.getBytes(), 0, this.guid.length() );
//    }
//    out.writeByte( 0x0 );
    
    IOUtil.writeString(out,guid);
    
    out.flush();
    
    CnsCreatGResponse result = new CnsCreatGResponse();
    result.readFrom( in );
    return result;
    
  }

  /**
   * @return the guid
   */
  public String getGuid() 
  {
    return this.guid;
  }
 
  /** 
   * By explictly setting the GUID in a CREATG Request, aliases can be created 
   * to existing LFC Entries. 
   * Added by P.T de Boer. 
   */
  public void setGuid(String gstr)
  {
     this.guid=gstr;
  }
}
