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
 * Encapsulates LFC server UNLINK command request. Then receives and returns
 * response. Use this command instead of DELETE to remove files. DELETE command
 * is for CASTOR entries only
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * 
 * @see CnsCreatGResponse
 */
public class CnsUnlinkRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String path;

  /**
   * Creates request for deleting new LFC entry.
   * 
   * @param path of the deleting entry
   */
  public CnsUnlinkRequest( final String path ) {
    this.path = path;
    this.uid = 1003;
    this.gid = 513;
    this.cwd = 0;
    
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param out output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsLinkStatResponse
   */
  public CnsUnlinkResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending UNLINK: " + this.path  ); 
    this.sendHeader( out,                       // header [12b]
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_UNLINK,
                     29 + IOUtil.byteSize(path)); // this.path.length() ); // FIXME set the message size 
    
    out.writeInt( this.uid );  // user id [4b]
    out.writeInt( this.gid );  // group id [4b]
    out.writeLong( this.cwd ); // I have no idea what is this [8b]
//    if ( this.path.length() != 0 ) {
//      out.write( this.path.getBytes(), 0, this.path.length() );  // [size b]
//    }
//    out.writeByte( 0x0 ); // trailing 0 for path [1b]
//    
    IOUtil.writeString(out,path); 
    
    out.flush();
    
    CnsUnlinkResponse result = new CnsUnlinkResponse();
    result.readFrom( in );
    return result;
    
  }
}
