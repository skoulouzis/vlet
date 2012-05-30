/*
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *    Piter T. de boer - Refactoring to standalone API and bugfixing.  
 *    Spiros Koulouzis - Refactoring to standalone API and bugfixing.  
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;



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
