/*****************************************************************************
 * Copyright (c) 2007, 2007 g-Eclipse Consortium 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *****************************************************************************/
package org.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.glite.lfc.LFCError;
import org.glite.lfc.LFCServer;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.CnsListReplicaResponse;
import org.glite.lfc.internal.ReplicaDesc;


/**
 *  Encapsulates LFC server response to requested LISTREPLICA command.
 *  
 *  Receives 12 byte header and then replica informations
 */
public class CnsListReplicaResponse extends AbstractCnsResponse {

  /**
   * Empty replica list 
   */
  public static final ArrayList<ReplicaDesc> EMPTY_REPLICAS = new ArrayList<ReplicaDesc>(0);
  
  private ArrayList<ReplicaDesc> replicas;
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException {
    LFCServer.staticLogIOMessage( "Receiving LISTREPLICA response..." ); //$NON-NLS-1$
    this.replicas = CnsListReplicaResponse.EMPTY_REPLICAS;
    int items;
    
    // Header
    super.readFrom( input );
    
    // Data
    // check for response type 
    if ( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
    } else {
      items = input.readShort();
      if ( items > 0 ) { 
        this.replicas = new ArrayList<ReplicaDesc>( items );
      } 
      ReplicaDesc replica;
      while ( items-- > 0 ) {
        replica = ReplicaDesc.getFromStream( input );
        this.replicas.add( replica );
        LFCServer.staticLogIOMessage( "\t\t\t Replica Host: " + replica.getHost() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\t Replica Poolname: " + replica.getPoolName() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\t Replica FS: " + replica.getFs() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\t Replica SFN: " + replica.getSfn() ); //$NON-NLS-1$
      }
      items = input.readShort();
      LFCServer.staticLogIOMessage( "End of List: " + items ); //$NON-NLS-1$
      
      this.size = super.receiveHeader( input );
    }
  }

  public ArrayList<ReplicaDesc> getReplicasArray() {
    return this.replicas;
  }
  
  
}
