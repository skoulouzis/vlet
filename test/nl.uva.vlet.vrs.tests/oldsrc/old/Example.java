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
 * $Id: Example.java,v 1.3 2011-05-02 13:28:48 ptdeboer Exp $  
 * $Date: 2011-05-02 13:28:48 $
 */ 
// source: 

package old;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

public class Example
{
    public static void main(String args[]) throws VlException
    {
        VRL vrl = new VRL("gsiftp://pc-vlab19.science.uva.nl/home/ptdeboer/test.txt");
        VFSClient vfs = new VFSClient();

        if (vfs.existsFile(vrl) == false)
        {
            showError("File does not exist:" + vrl);
            return;
        }

        VDir tmpDir = vfs.getDir("/tmp");
        // create local dir if not existing:
        tmpDir = tmpDir.createDir("sageViewer-" + Global.getUsername(), true);
        VFile remoteFile = vfs.getFile(vrl);

        // start background transfer:
        VFile localFile;

        VFSTransfer transferInfo = vfs.asyncCopy(remoteFile, tmpDir);

        // synchronous copy:
        // localFile=remoteFile.copyTo(tmpDir);

        while (transferInfo.isDone() != false)
        {
            double progress = transferInfo.getProgress();
            // show progress bar,
            System.err.println("progress=" + progress);

            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                ;
            } // sleep 0.1 sec.
        }

        // resulting node
        VNode node = transferInfo.getResultNode();
        // local file should be available:// TODO Auto-generated method stub

        // check whether new file really exists:
        localFile = tmpDir.getFile(remoteFile.getBasename());
        System.err.println("local file=" + localFile);
    }

    private static void showError(String str)
    {
        System.err.println("Error:" + str);

    }
}
