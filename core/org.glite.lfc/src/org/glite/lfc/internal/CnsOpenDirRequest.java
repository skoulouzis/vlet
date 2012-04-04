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
 * $Id: CnsOpenDirRequest.java,v 1.2 2011-04-18 12:30:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:39 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCServer;
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsRequest;
import org.glite.lfc.internal.CnsConstants;



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
