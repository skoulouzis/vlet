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

import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;



/**
 * Abstraction of the LFC communication request,<br>
 * all Cns*Request extends this class. 
 *  
 * Modifications:<br>
 *    Piter T. de Boer - Stripped away GEclipse dependencies and added check methods   
 *                     
 * @see AbstractCnsResponse
 * @see CnsConstants
 */
public abstract class AbstractCnsRequest
{
	private int startCount=0; 
	private int endCount=0; 

/**
   * @param output  stream where header is about to be written
   * @param magic   first 4 bytes / CNS magic number
   * @param type    {@link CnsConstants}
   * @param size    size of the rest of the message
   * @return        not yet specified
   * @throws IOException
   * @see CnsConstants
   */
  public int sendHeader( final DataOutputStream output,
                         final int magic,
                         final int type,
                         final int size ) throws IOException 
  {

    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_header, 
                                         Integer.toHexString( magic ),
                                         Integer.toHexString( type ),
                                         new Integer( size ) ) );
    
    //PTdB: Mark start of message: 
    this.startCount=output.size(); 
    
    output.writeInt( magic );
    output.writeInt( type );
    output.writeInt( size );
    
    return 0;
  }
  
  /**
   * Check the total amount of bytes send to the DataOutputStream  with
   * the calculated one. 
   * Todo: create message body and auto-calculated the message size.  
   */
  protected void assertMessageLength(DataOutputStream output,int targetSize) 
  {
	  endCount=output.size();
	  if ((endCount-startCount)!=targetSize)
	  {
		  throw new Error("Calculated message size and actual message size do not match: calculated,real="+targetSize+","+(endCount-startCount)); 	
	  }
  }
}
