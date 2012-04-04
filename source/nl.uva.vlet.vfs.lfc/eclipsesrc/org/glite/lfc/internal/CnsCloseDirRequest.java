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
import org.glite.lfc.internal.CnsCloseDirResponse;
import org.glite.lfc.internal.CnsConstants;



/**
 * Encapsulates LFC server CLOSEDIR command request. <br>
 * Then receives and returns response.
 * @see CnsCloseDirResponse  
 */
public class CnsCloseDirRequest extends AbstractCnsRequest {

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output  stream that request will be written to
   * @param in      stream that response will be read from 
   * @return        Response object
   * @throws IOException in case of any I/O problem
   * @see CnsCloseDirResponse
   */
  public CnsCloseDirResponse sendTo( final DataOutputStream output,
                                     final DataInputStream in )
    throws IOException  {

    CnsCloseDirResponse result = new CnsCloseDirResponse();
    LFCServer.staticLogIOMessage( Messages.lfc_log_send_closedir ); 
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_CLOSEDIR,
                     12 );
    
    output.flush();
    result.readFrom( in );
    return result;
    
  }
}
