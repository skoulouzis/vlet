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

import org.glite.lfc.LFCError;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsRmdirRequest;


/**
 * Standard LFC Response for methods with return no value (void methods). 
 * <p>
 * Created by Piter T. de Boer as an attempt to cleanup the code. 
 * 
 * Receives 12 byte header with return code
 */
public class CnsVoidResponse extends AbstractCnsResponse 
{
	int errorCode=0; 

//	/** Construct from InputStream ! 
//	 * @throws IOException */ 
//	public CnsVoidResponse(DataInputStream input) throws IOException
//	{
//		readFrom(input);  // 12 B header 
//	}
	/**
	 * @return received error code if an error occurred. 
	 */
	public int getErrorCode() 
	{
		return this.errorCode;
	}

	@Override
	public void readFrom( final DataInputStream input ) throws IOException 
	{
		//int result=0; 

		LFCServer.staticLogIOMessage( "Receiving VOID response..." );

		// receive header from Header only message ! 
		super.receiveHeader( input );

		// check for response type 
		if ( this.type == CnsConstants.CNS_RC ) 
		{
			// received RESET CONTEXT request we have an error
			LFCServer.staticLogIOMessage( "ERROR in receiving VOID Response: " + LFCError.getMessage( this.size ) );
			// get error code from size field
			errorCode=this.size; 
		}

		//return result; 
	}
}
