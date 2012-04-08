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
 * $Id: CnsReadDirRequest.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsReadDirResponse;



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
  private boolean moreData=false;

  /**
   * Creates request for READDIR with specified directory unique id
   * @param id directory unique id
   */
  public CnsReadDirRequest( final long id ) 
  {
    this.fileid = id;
    this.uid = 0;
    this.gid = 0;
  }

  /**
   * Creates request for READDIR with specified directory unique id
   * @param id directory unique id
   */
  public CnsReadDirRequest( final long id, boolean moreData) 
  {
    this.fileid = id;
    this.uid = 0;
    this.gid = 0;
    this.moreData=moreData; 
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
    /*
     * lfc-ls              => getattr=0;
     * lfc-ls -l           => getattr=1;
     * lfc-ls --comment    => gettattr=3;
     * lfc-ls -l --comment => gettattr=4;
     */
    output.writeShort( 1 );     // ls -l
    output.writeShort( 50 );    // max 50 ???
    output.writeLong( this.fileid ); 

    // PTdB: Added EOD handling 
    if (moreData)
    	output.writeShort(0); // Follow Up Request Server has more Data ! 
    else
    	output.writeShort(1); // New Request
    output.flush();
    
    result.readFrom( in );
    
    return result;
    
  }
}
