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

/**
 * New GetReplicas Cns Message. 
 * 
 * @author: Piter T. de Boer
 *  
 */
public class CnsGetReplicaResponse 
{
    private CnsMessage message=null; 
    
  /**
   * Empty replica list 
   */
  public static final ArrayList<ReplicaDesc> EMPTY_REPLICAS = new ArrayList<ReplicaDesc>(0);
  
  private ArrayList<ReplicaDesc> replicas;
  
  public void readFrom( final DataInputStream input ) throws IOException 
  {
    LFCServer.staticLogIOMessage( "Receiving GETREPLICA response..." ); //$NON-NLS-1$
    this.replicas = CnsGetReplicaResponse.EMPTY_REPLICAS;
    
    message=new CnsMessage(); 
    message.readHeader(input); 
    
    //items=input.readInt();
    // System.err.println(">>> #items="+items);
  
    // check for response type 
    if ( message.isResetSecurityContext()) 
    {
        // received RESET CONTEXT request!
        // we have an error!
        LFCServer.staticLogIOMessage( "RC RESPONSE: " + LFCError.getMessage( message.error() ) ); 
        return; 
    }
    
    // Reply Body: 
    {
        // As refactored from  the Original C Code: 
        // File: send2snd.c
        // Method: send2nsdx(...) 
        
        int numItems=0;
  
        // read complete message body:
        message.readBody(input); 
        // Arg: Must stay clear from VLET ! 
        //ByteSequence bodyInput=nl.uva.vlet.BufferUtil.createByteDataInputStream(bytes); 
        // Wrap DataInput interface around InputStream subclass around ByteBuffer instance.
        // Maybe candidate for combined InputStream+DataSink interface around <ANY>Buffer ? 
        
        // while more replicas available!
        if (message.type()!=CnsConstants.MSG_REPLIC)
        {
            LFCServer.staticLogIOMessage("No Replica message or 0 replicas in response. Got type:"+CnsConstants.getResponseType(message.type())); 
        }
        else
        {
            this.replicas = new ArrayList<ReplicaDesc>(10); //initial capacity
            // Use optimized Java.nio.ByteBuffer clas
            
            DataInputStream bodyInput = message.getBodyDataInput();
            
            while(bodyInput.available()>0)
            {
                LFCServer.staticLogIOMessage("Reading Replica #"+numItems); 
         
                ReplicaDesc replica;
                replica = ReplicaDesc.getFromStream( bodyInput );
                this.replicas.add( replica );
                LFCServer.staticLogIOMessage( "\t\t\t FileID          : " + replica.getFileId()); 
                LFCServer.staticLogIOMessage( "\t\t\t Replica Host    : " + replica.getHost() ); 
                LFCServer.staticLogIOMessage( "\t\t\t Replica Poolname: " + replica.getPoolName() );
                LFCServer.staticLogIOMessage( "\t\t\t Replica FS      : " + replica.getFs() ); 
                LFCServer.staticLogIOMessage( "\t\t\t Replica SFN     : " + replica.getSfn() );

                // replica size should match exactly
                boolean hasMore=bodyInput.available()>0; 
                numItems++;
            }
        }
        
        // end message ! 
        message.readHeader(input);
        
        // superfluous data ? TBI!
        // new SEND2SNDX methods seem to end with a MSG_DATA before the CNS_RC 
        // message.
        // just read both and keep the CNC_RC message as last. 
        
        while( message.type() == CnsConstants.MSG_DATA )
        {
            message.readBody(input);
            message.readHeader(input); 
        }
        // current message should be last message 
        
        LFCServer.staticLogIOMessage( "End of List: " + numItems ); //$NON-NLS-1$
     }
      
    
  }

  public ArrayList<ReplicaDesc> getReplicasArray() 
  {
    return this.replicas;
  }
  
  /** Returns last message read by this reponse */ 
  public CnsMessage getMessage() 
  {
      return message; 
  }
  
}
