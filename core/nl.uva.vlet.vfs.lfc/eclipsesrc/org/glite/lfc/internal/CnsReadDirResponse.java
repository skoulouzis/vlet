/*****************************************************************************
 * Copyright (c) 2007, 2008 g-Eclipse Consortium 
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
import org.glite.lfc.Messages;
import org.glite.lfc.internal.AbstractCnsResponse;
import org.glite.lfc.internal.CnsConstants;
import org.glite.lfc.internal.FileDesc;


/**
 *  Encapsulates LFC server response to requested READDIR command.
 *  
 *  Note: file description without comments is expected, use with 
 *        long list (@link eu.geclipse.efs.lgp.internal.CnsReadDirRequest.java)
 *          
 *  Receives 12 byte header and then files descriptions
 */
public class CnsReadDirResponse extends AbstractCnsResponse {
 
  private short items;      // number of items described
  private ArrayList<FileDesc> files; // files descriptions
  private short eod;        // have no idea! maybe it's End-Of-Data?
 
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException {

    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_readdir ); 
    // Header
    super.readFrom( input );
    
    // check for response type
    if( this.type == CnsConstants.CNS_RC ) {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) );
    } else {
      // Data
      // read number of descriptions first
      this.items = input.readShort();
      this.files = new ArrayList<FileDesc> ( this.items );
      LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_items, new Integer( this.items ) ) ); 
      
      for( int i=0; i<this.items; i++ ) {
        FileDesc item = FileDesc.getFromStream( input, true, true, true, true );
  
        this.files.add( item );
        LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_readdir_filename, item.getFileName() ) ); 
        LFCServer.staticLogIOMessage( "\t\t\tComment: " + item.getComment() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tGUID: " + item.getGuid()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tFilename: " + item.getFileName()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum type: " + item.getChkSumType()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tChecksum value: " + item.getChkSumValue() ); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tmodified: " + item.getMDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\tcreated:  " + item.getCDate().toString()); //$NON-NLS-1$
        LFCServer.staticLogIOMessage( "\t\t\taccessed: " + item.getADate().toString()); //$NON-NLS-1$
      }
      
      this.eod = input.readShort();
      this.size = super.receiveHeader( input );
    } 
    
  }

  /**
   * @return returns previously received array of file descriptions
   */
  public ArrayList<FileDesc> getFileDescs() {
    return this.files;
  }
  
  /**
   * @return last 16 bit word, have no idea what is its purpose 
   */
  public short getEod() {
    return this.eod;
  }
  
}
