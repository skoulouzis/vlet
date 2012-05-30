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

import nl.uva.vlet.glite.lfc.LFCServer;



/**
 * <p>
 * Encapsulates LFC server FILESIZE command request. Then receives and returns
 * response.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsLinkStatResponse
 */
public class CnsFileSizeRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long size;
  private long cwd;
  private String guid; // Global Unique ID
  private String csumType;
  private String csumValue; 

  /**
   * Creates request for link detailed information.
   * 
   * @param size new size of the file
   */
  public CnsFileSizeRequest( final long size ) {
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    this.size = size;
    this.guid = CnsConstants.EMPTY_STRING;
    this.csumType = CnsConstants.EMPTY_STRING;
    this.csumValue = CnsConstants.EMPTY_STRING;
    
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
   * <p>Sets checksum type</p>
   * 
   * @param csumType checksum type, eg. MD for MD5
   */
  public void setCsumType( final String csumType) {
    this.csumType = csumType != null ? csumType : CnsConstants.EMPTY_STRING ;
  }  
  /**
   * <p>Sets checksum value</p>
   * 
   * @param csumValue checksum value
   */
  public void setCsumValue( final String csumValue) {
    this.csumValue = csumValue != null ? csumValue : CnsConstants.EMPTY_STRING;
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
  public CnsFileSizeResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage(  "Changing file size / csum of " + this.guid + ", with: " + this.csumValue  );  //$NON-NLS-1$ //$NON-NLS-2$
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC,
                     CnsConstants.CNS_SETFSIZEG,
                     31 + this.csumType.length() 
                        + this.csumValue.length()
                        + this.guid.length() );
    
    out.writeInt( this.uid );  // user id
    out.writeInt( this.gid );  // group id

    out.write( this.guid.getBytes(), 0, this.guid.length() );
    out.writeByte( 0x0 );
    
    out.writeLong( this.size );
    
    out.write( this.csumType.getBytes(), 0, this.csumType.length() );
    out.writeByte( 0x0 );
    out.write( this.csumValue.getBytes(), 0, this.csumValue.length() );
    out.writeByte( 0x0 );
    
    out.flush();
    
    CnsFileSizeResponse result = new CnsFileSizeResponse();
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
