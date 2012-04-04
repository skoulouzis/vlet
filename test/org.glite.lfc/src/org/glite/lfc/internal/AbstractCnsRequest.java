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
 * $Id: AbstractCnsRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

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
    
    output.writeInt( magic ); //+4
    output.writeInt( type );  //+4
    output.writeInt( size );  //+4
                              //--- total header size=12
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
