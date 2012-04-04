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
import java.io.IOException;

import org.glite.lfc.LFCError;
import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsCloseDirRequest;
import org.glite.lfc.internal.CnsConstants;


/**
 *  Encapsulates LFC server response to requested CLOSEDIR command.<br>
 *  Receives 12 byte header and then integer value
 *  @see CnsCloseDirRequest
 */
public class CnsCloseDirResponse extends AbstractCnsResponse {

  private int response;

  /**
   * @return response code
   */
  public int getResponse() {
    return this.response;
  }

  @Override
  public void readFrom( final DataInputStream input ) throws IOException {
    
    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_closedir ); 
    // Header
    super.readFrom( input );
    
    // check for response type
    if( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
    } else {
      // Data
      this.response = input.readInt();
      this.size = super.receiveHeader( input );
    }
  }
  
}
