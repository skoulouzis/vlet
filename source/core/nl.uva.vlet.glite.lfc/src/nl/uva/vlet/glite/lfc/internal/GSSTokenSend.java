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

import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;




/**
 *  Encapsulates sending part of GSS context negotiations
 */
public class GSSTokenSend extends AbstractCnsRequest {
  private int magic = CnsConstants.CSEC_TOKEN_MAGIC_1;
  private int type = 0x1;
  private byte[] token;
  
  
  
  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param token wrapped token
   */
  public GSSTokenSend( final byte[] token ) {
   this.token = token;
  }

  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param magic overwrite Magic Number 
   * @param type overwrite Message Type
   */
  public GSSTokenSend( final int magic, final int type) {
   this.magic = magic;
   this.type = type;
  }
  
  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param magic overwrite Magic Number 
   * @param type overwrite Message Type
   * @param token wrapped token
   */
  public GSSTokenSend( final int magic, final int type, final byte[] token ) {
   this.magic = magic;
   this.type = type;
   this.token = token;
  }
  
  /**
   * Sets new magic number value
   * @param magic new magic number 
   */
  public void setMagic( final int magic ) {
    this.magic = magic;
  }

  /**
   * Sets new message type value
   * @param type new message type
   */
  public void setType( final int type ) {
    this.type = type;
  }

  /**
   * Sets new token byte array
   * @param token new token
   */
  public void setToken( final byte[] token ) {
    this.token = token;
  }

  /**
   * Send GSS Token to the output stream
   * @param output stream where token will be written to
   * @throws IOException in case of any I/O problem
   */
  public void send( final DataOutputStream output ) throws IOException {
    
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_token, 
                                         new Integer( this.token.length ) ) );
    this.sendHeader( output, this.magic, this.type, this.token.length );
    output.write( this.token, 0, this.token.length );
    output.flush();

  }
}
