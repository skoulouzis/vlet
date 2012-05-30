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
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;



/**
 * <p>
 * Encapsulates LFC server RENAME command request. Then receives and returns
 * response. 
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * 
 * @see CnsRenameResponse
 */
public class CnsRenameRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String oldPath;
  private String newPath;

  /**
   * Creates request for renaming the LFC entry.
   * @param oldPath 
   * @param newPath 
   * 
   */
  public CnsRenameRequest( final String oldPath, final String newPath ) {
    this.oldPath = oldPath;
    this.newPath = newPath;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param out output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsRenameResponse
   */
  public CnsVoidResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending RENAME: " + this.oldPath  ); 
    this.sendHeader( out,                       // header [12b]
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_RENAME,
                     30 + IOUtil.byteSize(oldPath,newPath));  
    
    out.writeInt( this.uid );  // user id [4b]
    out.writeInt( this.gid );  // group id [4b]
    out.writeLong( this.cwd ); // I have no idea what is this [8b]
//    if ( this.oldPath.length() != 0 ) {
//      out.write( this.oldPath.getBytes(), 0, this.oldPath.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for old path [1b]
//    
//    if ( this.newPath.length() != 0 ) {
//      out.write( this.newPath.getBytes(), 0, this.newPath.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for new path [1b]
    IOUtil.writeString(out,oldPath); 
    IOUtil.writeString(out,newPath);
    
    out.flush();
    
    CnsVoidResponse result = new CnsVoidResponse();
    result.readFrom( in );
    return result;
    
  }
}
