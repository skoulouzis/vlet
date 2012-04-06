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
	public String linkToPath=null; 
  
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
    LFCServer.staticLogIOMessage( "receiving READLINK response..." ); //$NON-NLS-1$
    
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
    	linkToPath=IOUtil.readString(input);
    }
    
    LFCServer.staticLogIOMessage( "Read SymLink path="+linkToPath);  

  }
  
  
}