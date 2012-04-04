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
 * $Id: CnsVoidResponse.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

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
 * Uses new CnsMessage class 
 * @author Piter T. de Boer
 */
public class CnsVoidResponse  
{
	int errorCode=0;
	
    private CnsMessage voidMessage;
    
	/**
	 * @return received error code if an error occurred. 
	 */
	public int getErrorCode() 
	{
		return this.errorCode;
	}

	public void readFrom( final DataInputStream input ) throws IOException 
	{
		//int result=0; 
		LFCServer.staticLogIOMessage( "Receiving VOID response..." );
		voidMessage=new CnsMessage(); 
		
		voidMessage.readHeader(input);
		if (voidMessage.size()!=0)
		{
		    LFCServer.staticLogIOMessage( "Warning VOID message is not EMPTY:size="+voidMessage.size());
		    voidMessage.readBody(input); // should be NULL; 
		}

		// check for response type 
		if ( voidMessage.isResetSecurityContext() ) 
		{
			// received RESET CONTEXT request we have an error
			LFCServer.staticLogIOMessage( "ERROR in receiving VOID Response: " + LFCError.getMessage( voidMessage.error())); 
			// get error code from size field
			errorCode=voidMessage.error();  
		}

		//return result; 
	}

    public CnsMessage getMessage()
    {
           return voidMessage; 
    }
}
