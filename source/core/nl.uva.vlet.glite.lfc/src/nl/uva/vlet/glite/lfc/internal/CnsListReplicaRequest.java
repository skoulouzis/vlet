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
 * Encapsulates LFC server LISTREPLICA command request. Then receives and returns
 * response.
 * @see CnsListReplicaResponse
 *
 */
public class CnsListReplicaRequest extends AbstractCnsRequest {
  
  
  private int uid;
  private int gid;
  private long cwd;
  private String guid = null;
  /**
   * Creates request for list replica command
   * 
   * @param guid Global unique ID of the file which replica information will be retrieved 
   */
  public CnsListReplicaRequest( final String guid ) {
    this.guid = guid;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }


  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output output stream to which request will be written
   * @param input input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsOpenDirResponse
   */
  public CnsListReplicaResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  {
    
    CnsListReplicaResponse result = new CnsListReplicaResponse();
    int bytes = 0;

    LFCServer.staticLogIOMessage(  "Sending replica information request for: " + this.guid );     
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_LISTREPLICA,
                     IOUtil.byteSize(guid) + 35 );
    
    output.writeInt( this.uid ); 
    output.writeInt( this.gid ); 
    output.writeShort( 0 );//1296 ); // size of nbentry
    output.writeLong( this.cwd );
    
    output.writeByte( 0x0 ); // NO ACTUAL PATH!
    
//    if ( this.guid != null ) {
//      output.writeBytes( this.guid );
//    }
//    output.writeByte( 0x0 ); // terminating 0x0 for guid
    IOUtil.writeString(output,guid); 
    
    output.writeShort( 1 ); // BOL (begin of list)
    
    output.flush();
    result.readFrom( input );
    return result;
    
  }
}
