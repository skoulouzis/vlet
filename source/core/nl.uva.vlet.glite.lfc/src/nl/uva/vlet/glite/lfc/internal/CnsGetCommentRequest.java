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
 * GetComment Request. 
 * 
 * @author Piter T. de Boer 
 */

public class CnsGetCommentRequest
{
  private int uid=0;
  private int gid=0;
  private long cwd=0;
  private String path=null; 


  
  /**
   * Creates request for  get commnent.
   */
  public CnsGetCommentRequest( final String path) 
  {
    this.path = path;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }


  public CnsSingleStringResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  
    {
         LFCServer.staticLogIOMessage(  "Sending GetComment information request for: " + this.path );
         
         CnsMessage msg= CnsMessage.createSendMessage(CnsConstants.CNS_MAGIC,
                 CnsConstants.CNS_GETCOMMENT);
         
         DataOutputStream dataOut = msg.createBodyDataOutput(4096); 
     
         dataOut.writeInt( this.uid ); //+4 
         dataOut.writeInt( this.gid ); //+4
         dataOut.writeLong(this.cwd);  //+8
                                       //+=28 
         IOUtil.writeString(dataOut,path);  //+1+length()
         // no need to flush databuffer not close it.
         
         // finalize and send ! 
         int numSend=msg.sendTo(output);
         output.flush(); // sync 
         msg.dispose(); 
         
         CnsSingleStringResponse response=new CnsSingleStringResponse(); 
         // output.flush();
         response.readFrom( input );
         return response; 
  }
  
}
