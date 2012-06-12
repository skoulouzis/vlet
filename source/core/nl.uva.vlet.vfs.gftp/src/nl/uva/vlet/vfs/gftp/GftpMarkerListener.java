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
 * $Id: GftpMarkerListener.java,v 1.2 2011-04-18 12:05:09 ptdeboer Exp $  
 * $Date: 2011-04-18 12:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.gftp;

import nl.uva.vlet.Global;

import org.globus.ftp.Marker;
import org.globus.ftp.MarkerListener;

public class GftpMarkerListener implements MarkerListener 
{
	// Not used yet: 
	public void markerArrived(Marker arg) 
	{
		Global.infoPrintf(this,">>> Marker Received. Ignoring:%s\n",arg); 
		Global.infoPrintf(this,">>> Marker class   =%s\n",arg.getClass());		
	}

}
