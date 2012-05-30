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

import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;



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
