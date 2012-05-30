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

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;




/**
 *  Abstraction of the LFC communication response, <br>
 *  all Cns*Response extends this class
 *  @see AbstractCnsRequest 
 *  @see CnsConstants
 */
public abstract class AbstractCnsResponse
{
  protected int head=0;   
  protected int size=0;
  public int type=0;
  
  /**
   * Note that this value is associated with low level protocol implementation
   * and should be used to debug/trace only.
   *  
   * @return the header value
   */
  public int getHeader() {
    return this.head;
  }
  
  /**
   * Note that this value is associated with low level protocol implementation
   * and should be used to debug/trace only.
   *  
   * @return type of the response, {@link CnsConstants}
   */
  public int getType() {
    return this.type;
  }
  /**
   * Note that this value is associated with low level protocol implementation
   * and should be used to debug/trace only.
   *  
   * @return the size of link description
   */
  public int getSize() {
    return this.size;
  }

  /**
   * Return error code. Note that this contains meaningful information after
   * full response is received.
   * 
   * @return error code of the message
   */
  public int getErrorCode() 
  {
	 // PdB: if an error occured the 'size' field holds the error number ! 
     return this.size;
  }
  /**
   * Receives header from specified stream 
   * @param input stream where header is stored 
   * @return size of expected response
   * @throws IOException
   */
  public int receiveHeader( final DataInputStream input ) throws IOException {
    int timeout = 8;
    int delay = 100;
    while ( ( input.available() < 12 ) && timeout-- > 0 ) {
      try {
        LFCServer.staticLogIOMessage( "\t HEAD: waiting "+delay+" [ms]... AVAIL=" + input.available() + ", EXP=12" );
        Thread.sleep( delay );
        delay*=2;
      } catch( InterruptedException exc ) {
       LFCServer.staticLogIOException( exc );
      }
    }
    if ( input.available() < 12 ) {
      throw new IOException( "Connection timeout during receiving header." );
    }
    
    this.head = input.readInt();
    this.type = input.readInt();
    this.size = input.readInt();
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_header,  
                                         Integer.toHexString( this.head ),
                                         Integer.toHexString( this.type ),
                                         new Integer( this.size ),
                                         CnsConstants.getResponseType( this.type  ) ) );
    return this.size;
  }
  
  /**
   * Reads the response from the binary data and store it in more convenient way 
   * 
   * @param input stream where data is stored
   * @throws IOException
   */
  public void readFrom( final DataInputStream input ) throws IOException {
    //Header
    this.size = this.receiveHeader( input );
  }
 
  
}
