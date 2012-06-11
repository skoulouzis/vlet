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
 * $Id: ResourceTransferable.java,v 1.3 2011-04-18 12:27:14 ptdeboer Exp $  
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
import nl.uva.vlet.vrs.Registry;

/**
 * Generic DnD (Drag and Drop) class. 
 * Custom VRL Transferable class
 */
public class ResourceTransferable implements Transferable
{
    ResourceRef ref = null;
    
    public ResourceTransferable(ResourceRef location)
    {
        this.ref = location;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
    {
        if (!isDataFlavorSupported(flavor))
        {
            throw new UnsupportedFlavorException(flavor);
        }
        else if (flavor.equals(VTransferData.ResourceRefDataFlavor))
        {
            return ref;
        }
        else if (flavor.equals(VTransferData.ResourceRefListDataFlavor))
        {
        	ResourceRef refs[]=new ResourceRef[1];
        	refs[0]=ref;
        	return refs;
        }
        //
        // KDE drag and drop: asks for URIs  
        //
        else if (flavor.equals(VTransferData.uriListFlavor))
        {
            //I can export local file 
            if ((Registry.isLocalLocation(ref.getVRL()) == true)
                    && (ref.getVRL().getScheme().compareTo(VFS.FILE_SCHEME) == 0))
            {
                // create local file path: 
                return "file://" + ref.getVRL().getPath();
            }

            else
            {
            	return ref.getVRL().toString(); // toURI().toString();
            }
        }
        else if (flavor.equals(DataFlavor.stringFlavor))
        {
            // always support string flavors
            return  ref.getVRL().toString();
        }

        else if (flavor.equals(DataFlavor.javaFileListFlavor))
        {
            java.util.Vector<File> fileList = new Vector<File>();

            //I can export local file 
            if ((Registry.isLocalLocation(ref.getVRL()) == true)
                    && (ref.getVRL().getScheme().compareTo(VFS.FILE_SCHEME) == 0))
            {
                File file = new File(ref.getVRL().getPath());
                fileList.add(file);
                return fileList;
            }
            else
            {
                Global.errorPrintln(this,"Cannot export remote file as local file flavor:"+ref);
                ;// cannot export remote file as local files ! 
            }

            return null;
        }
        else if (flavor.equals(VTransferData.octetStreamDataFlavor))
        {
            Global.infoPrintln("LocationTransferable","get: octetStreamDataFlavor!!");
        }

        throw new UnsupportedFlavorException(flavor);
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        return VTransferData.dataFlavorsVRL;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        Global.debugPrintln("LocationTransferable", "is flavor supported:"
                + flavor);

        for (DataFlavor flav : VTransferData.dataFlavorsVRL)
            if (flav.equals(flavor))
                return true;

        // return true; 
        return false;
    }

}