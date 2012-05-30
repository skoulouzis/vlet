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
package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;



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
