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
 * Encapsulates LFC server OPENDIR command request. Then receives and returns
 * response.
 * 
 * Modified by P.T. de Boer: Use default String writers. 
 * 
 * @see CnsOpenDirResponse
 */
public class CnsOpenDirRequest extends AbstractCnsRequest 
{
  private int uid;
  private int gid;
  private long cwd;
  private String path = null;
  private String guid = null;

  
  /**
   * Creates request for open directory command
   * 
   * @param path path to the directory which should be open
   */
  public CnsOpenDirRequest( final String path ) 
  {
    this.path = path;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }

  /** 
   * Specify Guid. 
   * Added By P.T de Boer 
   * @param _guid The GUID 
   */
  public void setGuid(String _guid)
  {
	this.guid=_guid;   
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
  public CnsLongResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  
  {
	  CnsLongResponse result = new CnsLongResponse();

    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_opendir, this.path ) );
    
    // save calculate string lengths excluding terminating byte 
    int messageSize=30+IOUtil.byteSize(path,guid);
    
    this.sendHeader( output,
                       CnsConstants.CNS_MAGIC2,
                       CnsConstants.CNS_OPENDIR,
                       messageSize);  
                       
    output.writeInt( this.uid ); // not used
    output.writeInt( this.gid ); // not used 
    output.writeLong( this.cwd ); // not used 
    // write strings includes null terminating character
    IOUtil.writeString(output, path); 
    IOUtil.writeString(output, guid);
    
    output.flush();
    
    assertMessageLength(output,messageSize); 
    
    result.readFrom( input );
    return result;
    
  }
}
