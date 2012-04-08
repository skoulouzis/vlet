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
 * $Id: CnsGetReplicaRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package nl.uva.vlet.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.uva.vlet.glite.lfc.IOUtil;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.AbstractCnsRequest;
import nl.uva.vlet.glite.lfc.internal.CnsConstants;
import nl.uva.vlet.glite.lfc.internal.CnsListReplicaResponse;



/**
 * GetReplica Request. Modified from ListReplicaRequest. 
 * New request as listReplicas is an depreciated method ! 
 * Uses New CnsMessage class 
 * 
 * @author Piter T. de Boer 
 */

public class CnsGetReplicaRequest // extends AbstractCnsRequest 
{
  private int uid=0;
  private int gid=0;
  private long cwd=0;
  private String path=null; 
  private String guid = null;
  private String se = null; // new parameter in getReplica()! 
  
  /**
   * Creates request for list replica command
   * 
   * @param guid Global unique ID of the file which replica information will be retrieved 
   * @param isGuid 
   */
  public CnsGetReplicaRequest( final String guidOrPath, boolean isGuid ) 
  {
    this.se=null; 
    if (isGuid)
    {
        this.guid = guidOrPath;
        this.path = null;
    }
    else
    {
        this.guid = null; 
        this.path = guidOrPath;
    }
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }

  public void setSE(String se)
  {
      this.se=se;    
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output output stream to which request will be written
   * @param input input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   */
  public CnsGetReplicaResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  
    {
         LFCServer.staticLogIOMessage(  "Sending GetReplica information request for: " + this.guid );
         
         CnsMessage msg= CnsMessage.createSendMessage(CnsConstants.CNS_MAGIC,
                 CnsConstants.CNS_GETREPLICA);
         
         DataOutputStream dataOut = msg.createBodyDataOutput(4096); 
     
         dataOut.writeInt( this.uid ); //+4 
         dataOut.writeInt( this.gid ); //+4
         dataOut.writeLong(this.cwd);  //+8
                                       //+=28 
         IOUtil.writeString(dataOut,path);  //+1+length()
         IOUtil.writeString(dataOut,guid);  //+1+length()
         IOUtil.writeString(dataOut,se);   //+1+length() == 31+3*length()
         
         // no need to flush databuffer not close it.
         
         // finalize and send ! 
         int numSend=msg.sendTo(output);
         output.flush(); // sync 
         msg.dispose(); 
         
         CnsGetReplicaResponse response=new CnsGetReplicaResponse(); 
         // output.flush();
         response.readFrom( input );
         return response; 
  }
  

//public CnsGetReplicaResponse sendToOrg( final DataOutputStream output, final DataInputStream input )
//  throws IOException  
//  {
//  
//       CnsGetReplicaResponse result = new CnsGetReplicaResponse();
//       LFCServer.staticLogIOMessage(  "Sending replica information request for: " + this.guid );
//  
//       int messageSize=  IOUtil.byteSize(path,guid,se) + 31;
//  
//       this.sendHeader( output,
//                   CnsConstants.CNS_MAGIC, // MAGIC not MAGIC2 used in listreplica
//                   CnsConstants.CNS_GETREPLICA,
//                   messageSize); 
//       // 12
//       output.writeInt( this.uid ); //+4 
//       output.writeInt( this.gid ); //+4
//       output.writeLong(this.cwd);  //+8
//                               // 28 
//       IOUtil.writeString(output,path);  //+1+length()
//       IOUtil.writeString(output,guid);  //+1+length()
//       IOUtil.writeString(output,se);   //+1+length() == 31+3*length()
//   
//       // output.writeShort( 1 ); // NO BOL in GetReplica !(begin of list)
//       this.assertMessageLength(output,messageSize); 
//  
//       output.flush();
//       result.readFrom( input );
//       return result;
//  
//}
}
