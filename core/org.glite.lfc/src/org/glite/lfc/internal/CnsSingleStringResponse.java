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
 * $Id: CnsSingleStringResponse.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCError;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsRmdirRequest;



/**
 * Single String Parameter response. 
 * Used by ReadLinkRequest. 
 *  
 * @author Piter T. de Boer 
 *
 */
public class CnsSingleStringResponse extends AbstractCnsResponse
{
	public  String stringResult=null; 
  
  /**
   * @return received return code
   */
  public int getReturnCode() 
  {
    return this.size;
  }

  @Override
  public void readFrom( final DataInputStream input ) throws IOException 
  {
    LFCServer.staticLogIOMessage( "receiving STRING response..." ); //$NON-NLS-1$
    
    // Header
    super.receiveHeader(input);
    
    // check for response type 
    if ( this.type == CnsConstants.CNS_RC ) 
    {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "ERROR: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
    }
    else
    {
        stringResult=IOUtil.readString(input);
    }
    
    LFCServer.staticLogIOMessage( "Read String result ="+stringResult);  

  }

  public String getString()
  {
      return stringResult;
  }
  
  
}
