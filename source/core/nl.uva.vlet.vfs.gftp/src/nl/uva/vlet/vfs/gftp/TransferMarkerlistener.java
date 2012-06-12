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
 * $Id: TransferMarkerlistener.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */
// source: 

package nl.uva.vlet.vfs.gftp;

import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;

import org.globus.ftp.GridFTPRestartMarker;
import org.globus.ftp.Marker;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.PerfMarker;

public class TransferMarkerlistener implements MarkerListener
{

    public TransferMarkerlistener(GftpFileSystem gftpFileSystem)
    {

    }

    public void markerArrived(Marker marker)
    {
        try
        {
            if (marker instanceof PerfMarker)
            {
                PerfMarker perfMarker = (PerfMarker) marker;
                // Global.errorPrintln(this,"PerfMarker:"+perfMarker);
                // Global.errorPrintln(this,"PerfMarker.timestamp       :"+perfMarker.getTimeStamp());
                // Global.errorPrintln(this,"PerfMarker.stripIndex      :"+perfMarker.getStripeIndex());
                // Global.errorPrintln(this,"PerfMarker.totalStipeCount :"+perfMarker.getTotalStripeCount());
            }
            else if (marker instanceof GridFTPRestartMarker)
            {
                Vector vec = ((GridFTPRestartMarker) marker).toVector();
                // Global.errorPrintln(this,"GridFTPRestartMarker:"+((GridFTPRestartMarker)marker).toVector()
                // );

                if (vec != null)
                {
                    // for (Object obj:vec)
                    // {
                    // Global.errorPrintln(this,"GridFTPRestartMarker: obj"+obj);
                    // }
                }
            }
            else
            {
                // Global.errorPrintln(this,"Marker:"+marker.getClass());
            }

        }
        catch (Exception e)
        {
            Global.logException(ClassLogger.ERROR,this,e,"markerArrived() Exception\n"); 
        }

    }

}
