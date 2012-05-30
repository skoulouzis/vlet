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
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;




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
