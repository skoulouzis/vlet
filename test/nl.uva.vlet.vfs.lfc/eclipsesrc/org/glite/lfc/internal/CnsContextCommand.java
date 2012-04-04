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

import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;


/**
 *  This CNS command is used to receive GSSContext command, which appears
 *  after every REQUEST-RESPONSE pair.<br>
 *  It's constructed with header only
 *  @see CnsConstants
 */
public class CnsContextCommand extends AbstractCnsResponse {

  private boolean resetContext;
  
  /*
   * (non-Javadoc)
   * 
   * @see eu.geclipse.efs.lgp.internal.AbstractCnsResponse#readFrom(java.io.DataInputStream)
   */
  @Override
  public void readFrom( final DataInputStream input ) throws IOException {
    
    this.resetContext = false;
    this.receiveHeader( input );
    
    if( this.type == CnsConstants.CNS_RC ) {
      LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_reset_context, 
                                           new Integer( this.size ) ) );
      this.resetContext = true;
    }
    if( this.type == CnsConstants.CNS_IRC ) {
      LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_keep_context, 
                                           new Integer( this.size ) ) );
      this.resetContext = false;
    }
  }

  /**
   * @return whenever context should be reset
   */
  public boolean isResetContext() {
    return this.resetContext;
  }
}
