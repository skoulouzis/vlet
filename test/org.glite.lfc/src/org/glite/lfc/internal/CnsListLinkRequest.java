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
 * $Id: CnsListLinkRequest.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;

/**
 * ListLinks Request.
 *  
 * Used new CnsMessage class 
 *  
 * Added by P.T de Boer 
 */
public class CnsListLinkRequest // extends AbstractCnsRequest 
{
  private int uid;
  private int gid;
  private long cwd;
  private String guid = null;
  private String path = null;
  private int bol;
  
  /**
   * Creates request for list links command 
   * @param useGuid 
   * @param pathOrGuid path or GUID of file  
   * @param beginOfList if 1 this is a new request, if 0 a followup request.  
   */
  public CnsListLinkRequest(String pathOrGuid, boolean useGuid, int beginOfList) 
  {
      this.uid = 0;
      this.gid = 0;
      this.cwd = 0;
      this.bol=beginOfList; 
      
      if (useGuid)
          setGuid(pathOrGuid); 
      else
          setPath(pathOrGuid);
      
  }
  
  public void setPath(String path)
  {
     this.path=path; 
     this.guid=null;
  }
  
  public void setGuid(String guid)
  {
     this.guid=guid;
     this.path=null; 
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
  public CnsStringListResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  
  {
    CnsStringListResponse result = new CnsStringListResponse();
  
    LFCServer.staticLogIOMessage(  "Sending list links request for: " + this.guid );

    CnsMessage sendMsg=CnsMessage.createSendMessage(
                    CnsConstants.CNS_MAGIC2,
                    CnsConstants.CNS_LISTLINKS); 
    
    DataOutputStream bodyData = sendMsg.createBodyDataOutput(4096); 
    
    bodyData.writeInt(this.uid); 
    bodyData.writeInt(this.gid); 
    bodyData.writeShort(0);//1296 ); // size of nbentry
    bodyData.writeLong(this.cwd);
    
    // Path OR Guid ! 
    IOUtil.writeString(bodyData,path);
    IOUtil.writeString(bodyData,guid); 
    
    bodyData.writeShort(bol); // BOL (begin of list)

    // Send: 
    sendMsg.sendTo(output); 
    output.flush();
    sendMsg.dispose(); 
    // Receive: 
    result.readFrom( input );
    return result;
    
  }
  
//  public CnsStringListResponse sendToOrg( final DataOutputStream output, final DataInputStream input )
//  throws IOException  
//{
//  CnsStringListResponse result = new CnsStringListResponse();
//
//  LFCServer.staticLogIOMessage(  "Sending list links request for: " + this.guid );
//  
//  int messageSize=  IOUtil.byteSize(guid,path) + 34 ;
//  /*
//  codec.marshallLONG(magic);
//  codec.marshallLONG(type);
//  codec.marshallLONG(size);
//  codec.marshallLONG(uid);
//  codec.marshallLONG(gid);
//  codec.marshallSHORT(listentsz);
//  codec.marshallHYPER(cwd);
//  codec.marshallSTRING(actualPath);
//  codec.marshallSTRING(guid);
//  codec.marshallSHORT(bol);
//  */
//  this.sendHeader(output,
//                  CnsConstants.CNS_MAGIC2,
//                  CnsConstants.CNS_LISTLINKS,
//                  messageSize
//                 );
//  
//  output.writeInt(this.uid); 
//  output.writeInt(this.gid); 
//  output.writeShort(0);//1296 ); // size of nbentry
//  output.writeLong(this.cwd);
//  
//  // Path OR Guid ! 
//  IOUtil.writeString(output,path);
//  IOUtil.writeString(output,guid); 
//  
//  output.writeShort(bol); // BOL (begin of list)
//  
//  output.flush();
//  
//  this.assertMessageLength(output, messageSize);
//  
//  result.readFrom( input );
//  return result;
//  
//}
}
