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
 * $Id: ReplicaDesc.java,v 1.2 2011-04-18 12:30:38 ptdeboer Exp $  
 * $Date: 2011-04-18 12:30:38 $
 */ 
// source: 

package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

import org.glite.lfc.IOUtil;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.ReplicaDesc;



/**
 *  Describes replica entries.
 *  
 */
public class ReplicaDesc {
  
  private String poolName;  // name of the pool
  private String guid;      // global unique id
  private String host;      // storage element host
  private String fs;        // filesystem type
  private String sfn;       // sfn value
  
  private Date aDate;       
  private Date pDate;

  private long fileId;      // unique id
  private long nbaccesses;  // number of accesses???
  private long aTime;       // last access time (in C 64 bit timestamp)
  private long pTime;       // pin time (in C 64 bit timestamp)

  private byte status;      // '-' = online, 'm' = migrated (don't know what is this for)
  private byte f_type;      // 
  
  /**
   * @return global unique ID of the file/directory
   */
  public String getGuid() {
    return this.guid;
  }
  
  /**
   * @return fileId of the file/directory
   */
  public long getFileId() {
    return this.fileId;
  }

  /**
   * This is not a Java time!
   * To get Java value use { @link #getADate() }
   * @return last access time, C 64 bit timestamp 
   */
  public long getATime() {
    return this.aTime;
  }

  /**
   * This is not a Java time!
   * To get Java value use { @link #getPDate() }
   * @return replica pin time, C 64 bit timestamp 
   */
  public long getPTime() {
    return this.pTime;
  }
  
  /**
   * @return last access time
   */
  public Date getADate() {
    return this.aDate;
  }
  
  /**
   * @return replica pin time
   */
  public Date getPDate() {
    return this.pDate;
  }
  
  /**
   * @return '-' = online, 'm' = migrated (don't know what is this for)
   */
  public byte getStatus() {
    return this.status;
  }
  
  
  /**
   * @return the poolName
   */
  public String getPoolName() {
    return this.poolName;
  }

  
  /**
   * @return the host
   */
  public String getHost() {
    return this.host;
  }

  
  /**
   * @return the fs
   */
  public String getFs() {
    return this.fs;
  }

  
  /**
   * @return the sfn
   */
  public String getSfn() {
    return this.sfn;
  }

  
  /**
   * @return the nbaccesses
   */
  public long getNbaccesses() {
    return this.nbaccesses;
  }

  
  /**
   * @return the f_type
   */
  public byte getF_type() {
    return this.f_type;
  }

  /**
   * @param input data stream where the data is stored
   * @return new FileDesc class
   * @throws IOException
   */
  public static ReplicaDesc getFromStream( final DataInputStream input )
    throws IOException {
    
    ReplicaDesc result = new ReplicaDesc();
    int i=10;
    // Receive data from data stream
    try {
      while (i > 0 && input.available() < 10) { 
        LFCServer.staticLogIOMessage( "Waiting for data... " ); //$NON-NLS-1$
        Thread.sleep( 250 );
        i--;
      }
      
    } catch( InterruptedException exc ) {
      LFCServer.staticLogIOException( exc );
    }
    
    result.fileId = input.readLong();
    result.nbaccesses = input.readLong();
    result.aTime = input.readLong();
    result.pTime = input.readLong();
    
    // convert C/UNIX 64 bit timestamps to Java Date
    result.aDate = new Date( result.getATime()*1000 );
    result.pDate = new Date( result.getPTime()*1000 );
    result.status = ( input.readByte() );
    result.f_type = ( input.readByte() );
    
    result.poolName = IOUtil.readString( input );
    result.host = IOUtil.readString( input );
    result.fs = IOUtil.readString( input );
    result.sfn = IOUtil.readString( input );

    return result;
  }
 
  public String toString()
  {
	  String str=
		  "ReplicaDesc:"
		  +"\n {"
		  +"\n   SFN      ="+getSfn()
		  +"\n   Guid     ="+getGuid()		   	
		  +"\n   FileId   ="+fileId
		  +"\n   Host     ="+getHost() 
	  	  +"\n   FS       ="+getFs()
	  	  +"\n   FType    ="+getF_type()
	  	  +"\n   Status   ="+status
	  	  +"\n   NBAccess ="+nbaccesses
  	      +"\n   ADate    ="+getADate()+" ("+getATime()+")"
  	      +"\n   PDate    ="+getPDate()+" ("+getPTime()+")"
  	      +"\n   PoolName ="+getPoolName() 
  	      +"\n }\n"; 
  	  
      return str; 
  }
  
}