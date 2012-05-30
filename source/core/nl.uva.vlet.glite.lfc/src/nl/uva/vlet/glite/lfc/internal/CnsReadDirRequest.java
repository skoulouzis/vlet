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
import nl.uva.vlet.glite.lfc.Messages;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsReadDirResponse;



/**
 * Encapsulates LFC server READDIR command request. Then receives and returns
 * response.
 * @see CnsReadDirResponse
 *
 */
public class CnsReadDirRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long fileid;
  private boolean moreData=false;

  /**
   * Creates request for READDIR with specified directory unique id
   * @param id directory unique id
   */
  public CnsReadDirRequest( final long id ) 
  {
    this.fileid = id;
    this.uid = 0;
    this.gid = 0;
  }

  /**
   * Creates request for READDIR with specified directory unique id
   * @param id directory unique id
   */
  public CnsReadDirRequest( final long id, boolean moreData) 
  {
    this.fileid = id;
    this.uid = 0;
    this.gid = 0;
    this.moreData=moreData; 
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsReadDirResponse
   */
  public CnsReadDirResponse sendTo( final DataOutputStream output, final DataInputStream in )
    throws IOException  {

    CnsReadDirResponse result = new CnsReadDirResponse();
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_readdir, new Long( this.fileid) ) ); 
    this.sendHeader( output, CnsConstants.CNS_MAGIC2, CnsConstants.CNS_READDIR, 34 );

    output.writeInt( this.uid ); 
    output.writeInt( this.gid );
    /*
     * lfc-ls              => getattr=0;
     * lfc-ls -l           => getattr=1;
     * lfc-ls --comment    => gettattr=3;
     * lfc-ls -l --comment => gettattr=4;
     */
    output.writeShort( 1 );     // ls -l
    output.writeShort( 50 );    // max 50 ???
    output.writeLong( this.fileid ); 

    // PTdB: Added EOD handling 
    if (moreData)
    	output.writeShort(0); // Follow Up Request Server has more Data ! 
    else
    	output.writeShort(1); // New Request
    output.flush();
    
    result.readFrom( in );
    
    return result;
    
  }
}
