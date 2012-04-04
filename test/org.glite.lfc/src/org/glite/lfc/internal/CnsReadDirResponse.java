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
 * $Id: CnsReadDirResponse.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.glite.lfc.LFCError;
import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.FileDesc;


/**
 *  Encapsulates LFC server response to requested READDIR command.
 *  
 *  Note: file description without comments is expected, use with 
 *        long list (@link eu.geclipse.efs.lgp.internal.CnsReadDirRequest.java)
 *          
 *  Receives 12 byte header and then files descriptions
 */
public class CnsReadDirResponse extends AbstractCnsResponse {
 
  private short items;      // number of items described
  private ArrayList<FileDesc> files; // files descriptions
  private short eod=1;      // End Of Data. If eod==1 no more data is avialable... 
 
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException {

    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_readdir ); 
    // Header
    super.readFrom( input );
    
    // check for response type
    if( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) );
    } else {
      // Data
      // read number of descriptions first
      this.items = input.readShort();
      this.files = new ArrayList<FileDesc> ( this.items );
      LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_items, new Integer( this.items ) ) ); 
      
      for( int i=0; i<this.items; i++ ) {
        FileDesc item = FileDesc.getFromStream( input, true, true, true, false );
  
        this.files.add( item );
        LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_filename, item.getFileName() ) ); 
        LFCServer.staticLogIOMessage( "\t\t\tComment: " + item.getComment() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tGUID: " + item.getGuid()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tFilename: " + item.getFileName()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum type: " + item.getChkSumType()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum value: " + item.getChkSumValue() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tmodified: " + item.getMDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tcreated:  " + item.getCDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\taccessed: " + item.getADate().toString()); //$NON-NLS-1$
      }
      
      this.eod = input.readShort();
      this.size = super.receiveHeader( input );
       LFCServer.staticLogIOMessage("Received EOD = "+this.eod); 
    } 
    
  }

  /**
   * @return returns previously received array of file descriptions
   */
  public ArrayList<FileDesc> getFileDescs() {
    return this.files;
  }
  
  /**
   * @return last 16 bit word, have no idea what is its purpose 
   */
  public short getEod() {
    return this.eod;
  }
  
}
