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

import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.Messages;



/**
 *  Encapsulates LFC server response to requested READDIR command.
 *  
 *  Note: file description without comments is expected, use with 
 *        long list (@link eu.geclipse.efs.lgp.internal.CnsReadDirRequest.java)
 *          
 *  Receives 12 byte header and then files descriptions
 */
public class CnsReadDirResponse extends AbstractCnsResponse {
 
  private short items;      // number of items described
  private ArrayList<FileDesc> files; // files descriptions
  private short eod=1;      // End Of Data. If eod==1 no more data is avialable... 
 
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException {

    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_readdir ); 
    // Header
    super.readFrom( input );
    
    // check for response type
    if( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) );
    } else {
      // Data
      // read number of descriptions first
      this.items = input.readShort();
      this.files = new ArrayList<FileDesc> ( this.items );
      LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_items, new Integer( this.items ) ) ); 
      
      for( int i=0; i<this.items; i++ ) {
        FileDesc item = FileDesc.getFromStream( input, true, true, true, false );
  
        this.files.add( item );
        LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_filename, item.getFileName() ) ); 
        LFCServer.staticLogIOMessage( "\t\t\tComment: " + item.getComment() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tGUID: " + item.getGuid()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tFilename: " + item.getFileName()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum type: " + item.getChkSumType()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum value: " + item.getChkSumValue() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tmodified: " + item.getMDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tcreated:  " + item.getCDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\taccessed: " + item.getADate().toString()); //$NON-NLS-1$
      }
      
      this.eod = input.readShort();
      this.size = super.receiveHeader( input );
       LFCServer.staticLogIOMessage("Received EOD = "+this.eod); 
    } 
    
  }

  /**
   * @return returns previously received array of file descriptions
   */
  public ArrayList<FileDesc> getFileDescs() {
    return this.files;
  }
  
  /**
   * @return last 16 bit word, have no idea what is its purpose 
   */
  public short getEod() {
    return this.eod;
  }
  
}
