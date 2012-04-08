/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: CnsGetReplicaResponse.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCError;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsResponse;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;


import com.sun.org.apache.bcel.internal.util.ByteSequence;

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
