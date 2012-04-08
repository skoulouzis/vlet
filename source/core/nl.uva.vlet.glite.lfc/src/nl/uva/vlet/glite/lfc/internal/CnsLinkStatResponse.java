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
 * $Id: CnsLinkStatResponse.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsResponse;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsLinkStatRequest;
import nl.uva.vlet.glite.lfc.internal.FileDesc;




/**
 *  Encapsulates LFC server response to requested LSTAT command.<br>
 *  Receives 12 byte header and then link description
 *  @see CnsLinkStatRequest
 */
public class CnsLinkStatResponse extends AbstractCnsResponse {

  private FileDesc fileDesc;

  /**
   * @return received link description
   * @see FileDesc
   */
  public FileDesc getFileDesc() {
    return this.fileDesc;
  }

  @Override
  public void readFrom( final DataInputStream input ) throws IOException {
    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_lstat ); 
    // Header
    this.size = this.receiveHeader( input );
    // check for response type 
    if ( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
    } else { 
      this.fileDesc = FileDesc.getFromStream( input, false, false, false, false );
      this.size = super.receiveHeader( input );
    }
  }    
}
