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
 * $Id: ResourceListTransferable.java,v 1.3 2011-04-18 12:27:14 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:14 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.Registry;

/**
 * 	List of ResourceTransferables !  
 */
public class ResourceListTransferable implements Transferable
{
    ResourceRef refs[] = null;
    
    public ResourceListTransferable(ResourceRef locs[])
    {
        this.refs=locs;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
    {
    	//System.err.println("getTransferData:"+flavor); 
    	
        if (!isDataFlavorSupported(flavor))
        {
            throw new UnsupportedFlavorException(flavor);
        }
        else if (flavor.equals(VTransferData.ResourceRefDataFlavor))
        {
            return refs[0];
        }
        else if (flavor.equals(VTransferData.ResourceRefListDataFlavor))
        {
            return refs;
        }
        //
        // KDE drag and drop: asks for URIs  
        //
        else if ((flavor.equals(VTransferData.uriListFlavor))
        		 || (flavor.equals(DataFlavor.stringFlavor)))
        {
        	// export as newline seperated string 
        	// to mimic KDE's newline seperated uriList flavor ! 
        	String sepStr="\n"; 
        	 
        	// String flavor: use ';' as seperator ! ; 
        	if (flavor.equals(DataFlavor.stringFlavor))
        		sepStr=";"; 
        	
            //I can export local file 
        	String urisstr="";
        	
        	for (ResourceRef ref:refs)
        	{
        		VRL vrl=ref.getVRL(); 
        		
        		if ((Registry.isLocalLocation(vrl) == true)
                    && (vrl.getScheme().compareTo(VFS.FILE_SCHEME) == 0))
        		{
        			// create local file path (leave out hostname!) 
        			urisstr+= "file://" + vrl.getPath()+sepStr;
        		}
        		else
        		{
        			urisstr+=vrl.toString()+";"; // toURI().toString();
        		}
        	}
        	return urisstr; 
        }
     
        else if (flavor.equals(DataFlavor.javaFileListFlavor))
        {
            java.util.Vector<File> fileList = new Vector<File>();

            for (ResourceRef ref:refs)
        	{
            	VRL vrl=ref.getVRL(); 
            	
                //I can export local file 
                if ((Registry.isLocalLocation(vrl) == true)
                        && (vrl.getScheme().compareTo(VFS.FILE_SCHEME) == 0))
                {
                    File file = new File(vrl.getPath());
                    fileList.add(file);
                }
                else
                {
                    Global.errorPrintf(this,"Cannot export remote file as local file flavor:%s\n",vrl);
                    ;// cannot export remote file as local files ! 
                }
        	}
            return fileList;
        }

        return null;
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        return VTransferData.dataFlavorsVRL;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        Global.debugPrintf(this,"isDataFlavorSupported:%s\n",flavor); 

        for (DataFlavor flav : VTransferData.dataFlavorsVRL)
            if (flav.equals(flavor))
                return true;

        // return true; 
        return false;
    }

}