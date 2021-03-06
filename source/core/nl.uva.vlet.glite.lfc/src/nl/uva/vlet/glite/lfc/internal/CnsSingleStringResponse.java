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

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;




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
