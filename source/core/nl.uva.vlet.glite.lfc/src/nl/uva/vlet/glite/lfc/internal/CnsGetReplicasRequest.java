package nl.uva.vlet.glite.lfc.internal;
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

//package org.glite.lfc.internal;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//
//import org.glite.lfc.IOUtil;
//import org.glite.lfc.LFCServer;
//import org.glite.lfc.internal.AbstractCnsRequest;
//import org.glite.lfc.internal.CnsConstants;
//import org.glite.lfc.internal.CnsListReplicaResponse;
//
//
///**
// * GetReplica Request. Modified from ListReplicaRequest. 
// * New request as listReplicas is an deprecated method ! 
// * 
// * @author Piter T. de Boer 
// */
//
//public class CnsGetReplicasRequest extends AbstractCnsRequest 
//{
//  private int uid;
//  private int gid;
//  private long cwd;
//  private String path=null; 
//  private String guid = null;
//  private String se = null; // new parameter in getReplicas()! 
//  /**
//   * Creates request for list replica command
//   * 
//   * @param guid Global unique ID of the file which replica information will be retrieved 
// * @param isGuid 
//   */
//  public CnsGetReplicasRequest( final String guidOrPath, boolean isGuid ) 
//  {
//    this.se=null; 
//    if (isGuid)
//    {
//        this.guid = guidOrPath;
//        this.path = null;
//    }
//    else
//    {
//        this.guid = null; 
//        this.path = guidOrPath;
//    }
//    this.uid = 0;
//    this.gid = 0;
//    this.cwd = 0;
//  }
//
//
//  /**
//   * <p>Sends prepared request to the output stream and then fetch the response</p>
//   * 
//   * @param output output stream to which request will be written
//   * @param input input stream from which response will be read
//   * @return object that encapsulates response
//   * @throws IOException in case of any I/O problem
//   * @see CnsOpenDirResponse
//   */
//  public CnsListReplicaResponse sendTo( final DataOutputStream output, final DataInputStream input )
//    throws IOException  
//    {
//    
//    CnsListReplicaResponse result = new CnsListReplicaResponse();
//    int bytes = 0;
//
//    LFCServer.staticLogIOMessage(  "Sending replica information request for: " + this.guid );     
//    this.sendHeader( output,
//                     CnsConstants.CNS_MAGIC, // MAGIC not MAGIC2 used in listreplica
//                     CnsConstants.CNS_GETREPLICAS,
//                     IOUtil.byteSize(guid,se) + 35 );
//    
//    output.writeInt( this.uid ); 
//    output.writeInt( this.gid );
//    // NOT IN C Code: output.writeLong( this.cwd );
//    output.writeInt( 1 );//1296 ); // size of nbguids -> Nr of GUIDS ? 
//  
//    IOUtil.writeString(output,se); 
//    IOUtil.writeString(output,guid); // could be list of GUIDS ! 
//    
//    // output.writeShort( 1 ); // NO BOL in GetReplica !(begin of list)
//    
//    output.flush();
//    result.readFrom( input );
//    return result;
//    
//  }
//}
