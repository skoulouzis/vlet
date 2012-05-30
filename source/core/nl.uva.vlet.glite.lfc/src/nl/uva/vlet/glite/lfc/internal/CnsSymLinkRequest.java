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
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;



/**
 * Request creation of a Symbolic Link. 
 * Uses new CnsMessage class.
 *  
 * @author Piter T. de Boer 
 */
public class CnsSymLinkRequest  // extends AbstractCnsRequest 
{
  private int uid;
  private int gid;
  private long cwd;
  private String sourcePath;
  private String newLinkPath;

  /**
   * Creates request for renaming the LFC entry.
   * @param oldPath 
   * @param newPath 
   * 
   */
  public CnsSymLinkRequest( final String source, final String newlink)
  {
    this.sourcePath = source;
    this.newLinkPath = newlink;
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
  public CnsVoidResponse sendTo( final DataOutputStream output, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( "sending SymLink: " + this.sourcePath +"<="+this.newLinkPath ); 
    
    CnsMessage sendMsg=CnsMessage.createSendMessage(
                    CnsConstants.CNS_MAGIC,    
                    CnsConstants.CNS_SYMLINK); 

    DataOutputStream bodyOut = sendMsg.createBodyDataOutput(4096); // auto expands 
    
    bodyOut.writeInt( this.uid );  // user id [4b]
    bodyOut.writeInt( this.gid );  // group id [4b]
    bodyOut.writeLong( this.cwd ); // I have no idea what is this [8b] 
    IOUtil.writeString(bodyOut,this.sourcePath); // 1+length in bytes 
    IOUtil.writeString(bodyOut,this.newLinkPath); // 1+length in bytes 
   
    // Send
    sendMsg.sendTo(output); 
    output.flush();
    sendMsg.dispose(); // help garbage collection ! 
    
    // Receive: 
    CnsVoidResponse result = new CnsVoidResponse();
    result.readFrom( in );
    return result;
    
  }
  
//  /**
//   * <p>Sends prepared request to the output stream and then fetch the response</p>
//   * 
//   * @param out output stream to which request will be written
//   * @param in input stream from which response will be read
//   * @return object that encapsulates response
//   * @throws IOException in case of any I/O problem
//   * @see CnsRenameResponse
//   */
//  public CnsVoidResponse sendTo( final DataOutputStream out, final DataInputStream in )
//    throws IOException  {
//    
//    LFCServer.staticLogIOMessage( "sending SymLink: " + this.sourcePath +"<="+this.newLinkPath ); 
//    
//    int messageSize= 30 + IOUtil.byteSize(sourcePath,newLinkPath); 
//    
//    this.sendHeader( out,                       // header [12b]
//                     CnsConstants.CNS_MAGIC,    
//                     CnsConstants.CNS_SYMLINK,    
//                     messageSize); 
//    
//    out.writeInt( this.uid );  // user id [4b]
//    out.writeInt( this.gid );  // group id [4b]
//    out.writeLong( this.cwd ); // I have no idea what is this [8b] 
//    IOUtil.writeString(out,this.sourcePath); // 1+length in bytes 
//    IOUtil.writeString(out,this.newLinkPath); // 1+length in bytes 
//    
//    out.flush();
//    
//    // PdB: New message, added check: 
//    this.assertMessageLength(out, messageSize);
//    // reuse void response: 
//    CnsVoidResponse result = new CnsVoidResponse();
//    result.readFrom( in );
//    return result;
//    
//  }
}
