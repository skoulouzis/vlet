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

import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;



/**
 * Encapsulates LFC server response which returns a (java) long value. 
 *  
 * Receives 12 byte header and then a long value as 8 bytes Big Endian.
 * Created by P.T de Boer and based upon eclipse examples. 
 */
public class CnsLongResponse extends AbstractCnsResponse 
{
  private long longValue = 0 ;

  
  /**
   * @return opened directory id (as file id)
   */
  public long getFileid() 
  {
    return this.longValue;
  }

  @Override
  public void readFrom(final DataInputStream input ) throws IOException
  {
    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_longvalue ); 
    // Pre Header
    super.receiveHeader( input );
    
    // check for response type
    if (this.type == CnsConstants.CNS_RC ) 
    {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) );
    }
    else 
    {
      // Read Data: 8 bytes long Big Endian (=java native endianity) 
      this.longValue = input.readLong();
      // Post Header: 
      this.size = super.receiveHeader( input );
    } 
}


  
  
}
