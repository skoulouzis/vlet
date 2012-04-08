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
 * $Id: AbstractCnsResponse.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;




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
