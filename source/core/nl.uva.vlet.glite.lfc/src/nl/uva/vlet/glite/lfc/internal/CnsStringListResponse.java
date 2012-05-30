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
import java.util.ArrayList;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;

public class CnsStringListResponse extends AbstractCnsResponse
{
    private ArrayList<String> stringList=null;
    private short eol; 
    
    @Override
    public void readFrom( final DataInputStream input ) throws IOException
    {
      LFCServer.staticLogIOMessage( "Receiving STRINGLIST response..." ); 
       
      int nrItems=0; 
      
      // Header
      super.readFrom( input );
      
      // Data
      // check for response type 
      if ( this.type == CnsConstants.CNS_RC )
      {
        // received RESET CONTEXT request!
        // we have an error!
        LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
      }
      else 
      {
          nrItems = input.readShort();
        
          if ( nrItems > 0 ) 
          {    
              this.stringList = new ArrayList<String>( nrItems );
          }
        
          int index=0;
          LFCServer.staticLogIOMessage( " -  Size of StringList:"+nrItems); 
          
          while ( index<nrItems )
          {
              String str= IOUtil.readString(input); 
              this.stringList.add( str );
              LFCServer.staticLogIOMessage( " - String [#"+index+"]:='" + str+"'" );
              index++; 
          }
        
        eol = input.readShort();
        LFCServer.staticLogIOMessage( "End of List:" +eol ); 
        this.size = super.receiveHeader( input );
      }
    }

    public ArrayList<String> getStringList() 
    {
      return this.stringList;
    }
    
    /** End Of List: More entries are available */ 
    public int getEOL()
    {
        return eol; 
    }

}
