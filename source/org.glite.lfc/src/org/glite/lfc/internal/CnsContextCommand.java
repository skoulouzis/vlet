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
 * $Id: CnsContextCommand.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

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
