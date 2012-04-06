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
 * $Id: TransferExample.java,v 1.4 2011-04-18 12:27:07 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:07 $
 */ 
// source: 

package examples.vfs;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;

import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

/** 
 * Example how to do an asynchronous file transfer 
 * 
 * @author P.T. de Boer 
 */
public class TransferExample
{
	public static void main(String args[]) 
	{
		try
		{
			// Use default VRSContext and VFSClient: 
			
			VRSContext context=new VRSContext(); 
			VFSClient vfs=new VFSClient(context);

			VRL vrl=new VRL("gsiftp://elan.science.uva.nl/home/ptdeboer/bigfile");
			
			
			if (vfs.existsFile(vrl)==false)
			{
				showError("File does not exist or resource is not a file:"+vrl);
				return; 
			}

			// create local dir if not existing:
			VDir tmpDir=vfs.getTempDir();
			tmpDir=tmpDir.createDir("temp_for_"+Global.getUsername(),true);
			
			VFile remoteFile=vfs.getFile(vrl);

			VFile localFile=null; 

			//
			// start background transfer:
			//

			VFSTransfer transferInfo = vfs.asyncCopy(remoteFile,tmpDir);

			// OR :
			// synchronous copy: 
			// localFile=remoteFile.copyTo(tmpDir); 
			//

			while(transferInfo.isDone()==false)
			{
				// get current transfer progress
				double current=(double)transferInfo.getSubTaskDone();
				double progress=-1; 

				if (current<0) 
					progress=-1; // transfer not started, current = -1.0  
				else 
					progress = current/(double)transferInfo.getSubTaskTodo(); 

				// show progress bar,  
				if (progress<0)
					System.out.println("progress =  Waiting..."); 
				else
					System.out.println("progress ="
							+ Math.floor(progress*1000)/10+"%"
							+ " ("+Math.floor(current/1000)/1000+" MB)");

				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					; 
				} // sleep 0.1 sec. 
			}

			if (transferInfo.hasError())
			{
				showError("Exception="+transferInfo.getException());  
			}

			VNode node=transferInfo.getResultNode();
			System.out.println("resulting file="+node); 

			// local file should be available:
			localFile=tmpDir.getFile(remoteFile.getBasename()); 
			System.out.println("local file="+localFile);
		}
		catch (VlException e)
		{
			// handle exception:
			
			// print error to global stderr: 
			showError("VFSExample:Exception="+e);
			Global.errorPrintStacktrace(e); 
		}
		
		// cleanup VRS
		VRS.exit(); 
	}

	private static void showError(String str)
	{
		System.err.println("Error:"+str); 

	}
}
