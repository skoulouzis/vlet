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


  
/**
 *  Encapsulates receiving part of GSS context negotiations
 */
public class GSSTokenRecv extends AbstractCnsResponse {

  private byte[] token;

  /**
   * Gets GSS Token
   * @return received bytes array token
   */
  public byte[] getToken() {
    return this.token;
  }
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException 
  {
    // Header
    this.receiveHeader( input );
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_token, 
                                         new Integer( this.size ) ) );
    
    // IO ERROR ! (could be wrong server) 
    if ((size<=0) || (size>1*1024*1024)) 
       throw new IOException("Token ERROR: Wrong size:"+size); 

    this.token = new byte[ this.size ];
    int wait=100; 
    int numWait=5; // 2 pow 5 = 32 * 100 = 3200 milliseconds   
    while (( input.available() < this.size - 12 ) && (numWait-->0)) 
    {
      try 
      {
        LFCServer.staticLogIOMessage( "TOKEN wait..." );
        Thread.sleep(wait);
        wait=wait*2;  
      }
      catch( InterruptedException ex ) 
      {
        // nothing to be done
      }
    }
   
    LFCServer.staticLogIOMessage( "TOKEN read ..." );
    input.read( this.token, 0, this.token.length );
    
  }


}
