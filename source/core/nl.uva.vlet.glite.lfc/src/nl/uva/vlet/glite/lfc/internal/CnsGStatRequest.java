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
import nl.uva.vlet.glite.lfc.Messages;



/**
 * <p>
 * Encapsulates LFC server GSTAT command request. Then receives and returns
 * response.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsLinkStatResponse
 */
public class CnsGStatRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private String path;
  private String guid; // Global Unique ID

  /**
   * Creates request for link detailed information.
   * 
   * @param path link for which information is requested
   */
  public CnsGStatRequest( final String path ) {
    this.path = path;
    this.guid = new String();
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    
  }

  /**
   * <p> Sets local user id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * 
   * @param uid local user id
   */
  public void setUid( final int uid ) {
    this.uid = uid;
  }

  /**
   * <p> Sets local group id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * @param gid local group id
   */
  public void setGid( final int gid ) {
    this.gid = gid;
  }
  
  /**
   * <p>Sets cwd parameter</p>
   * <p>I have no idea what it's for<br>
   * Use 0 (zero)</p>
   * 
   * @param cwd CWD - Change Working Directory? I have no idea.
   */
  public void setCwd( final long cwd ) {
    this.cwd = cwd;
  }
  
  /**
   * <p>Sets new path value</p>
   * <p>This parameter is also set by constructor</p>
   * @param path link for which detailed information will be fetched
   */
  public void setPath( final String path ) {
    this.path = path;
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
  public CnsGStatResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_statg, this.path ) );
    
    int messageLen=30+IOUtil.byteSize(path,guid);
    
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_STATG,
                     messageLen );
    
    out.writeInt( this.uid );  // user id
    out.writeInt( this.gid );  // group id
    out.writeLong( this.cwd ); // I have no idea what is this

    IOUtil.writeString(out,path); 
    IOUtil.writeString(out,guid);
    
//    if ( this.path.length() != 0 ) {
//      out.write( this.path.getBytes(), 0, this.path.length() );
//    }
//    out.writeByte( 0x0 );
//    if ( this.guid.length() != 0 ) {
//      out.write( this.guid.getBytes(), 0, this.guid.length() );
//    }
//    out.writeByte( 0x0 );
    
    out.flush();
    
    CnsGStatResponse result = new CnsGStatResponse();
    result.readFrom( in );
    return result;
    
  }

  
  /**
   * @return the guid
   */
  public String getGuid() {
    return this.guid;
  }

  /**
   * @param guid the guid to set
   */
  public void setGuid( final String guid ) {
    this.guid = guid;
  }
}
