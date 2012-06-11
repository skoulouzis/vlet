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
 * $Id: VTransferData.java,v 1.5 2011-06-07 15:14:35 ptdeboer Exp $  
 * $Date: 2011-06-07 15:14:35 $
 */ 
// source: 

package nl.uva.vlet.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

/** 
 * 
 * Util class which handles Transfers and TranferData 
 * Implementation of TransferData
 */
public class VTransferData
{
	public static class ResourceRefList extends Vector<VRL>{};
	
    // === class === // 
	public static DataFlavor ResourceRefDataFlavor=new DataFlavor(VRL.class, "ResourceRef class");
	public static DataFlavor ResourceRefListDataFlavor=new DataFlavor(ResourceRefList.class, "Array of VRL");
    
     // Not Used: String is good enough is serial data type
     // use location itself as the tranferable object. This is possible since VRL is 
     // serializable ! 
     //public static DataFlavor locationInputStreamDataFlavor=new DataFlavor(InputStream.class, "locationInputStream");
    
    /** Todo: download remote resource through 'octet stream' */ 
    public static DataFlavor octetStreamDataFlavor=new DataFlavor("application/octet-stream;class=java.io.InputStream","octetStream");
    
    /** In KDE this is a newline seperators list of URIs */ 
    public static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String","uri list");

    /** One ore more URIstrings seperated by a ';' */
    public static DataFlavor stringFlavor=DataFlavor.stringFlavor; 
    
    public static DataFlavor javaFileListFlavor=DataFlavor.javaFileListFlavor;
    
    // === Object === // 

    /**
     *  DataFlavors from which VRL(s) can be imported  !  
     */

    public static DataFlavor[] dataFlavorsVRL = new DataFlavor[]
       {
            ResourceRefDataFlavor,
            ResourceRefListDataFlavor,
            javaFileListFlavor,
            uriListFlavor,             
            stringFlavor,
       };

    /** Handle Java File List 
     * @throws IOException 
     * @throws UnsupportedFlavorException */

    public static ResourceRef[] getJavaFileListRefs(Transferable t) throws UnsupportedFlavorException, IOException
    {
        java.util.List<File> fileList = (java.util.List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
        Iterator<File> iterator = fileList.iterator();
        
        int len=fileList.size();
        
        ResourceRef vrls[]=new ResourceRef[len];
        int index=0; 
        
        while (iterator.hasNext())
        {
          java.io.File file = (File)iterator.next();
          
          Debug("name="+file.getName());
          Debug("url="+file.toURI().toString());
          Debug("path="+file.getAbsolutePath());
          
          VRL vrl=new VRL("file",null,file.getAbsolutePath());
          String type=(file.isDirectory()?VRS.DIR_TYPE:VRS.FILE_TYPE);
          String mimeType=UIGlobal.getMimeTypes().getMimeType(vrl.getPath()); 
          
          vrls[index]=new ResourceRef(vrl, type, mimeType);
          index++; 
        }

        return vrls; 
    }

    private static void Debug(String msg)
    {
        Global.debugPrintf(VTransferData.class,"%s\n",msg); 
    }

    public static boolean canConvertToVRLs(Transferable t)
    {
        //DataFlavor flavors[]=t.getTransferDataFlavors(); 

        for (DataFlavor flav : dataFlavorsVRL)
            if (t.isDataFlavorSupported(flav))
                return true;

        // return true; 
        return false;
    }
 
    public static ResourceRef[] getRefsFrom(Transferable t) throws UnsupportedFlavorException, IOException, VRLSyntaxException
    { 
    	// Known URI/File type  flavors: 

        if (t.isDataFlavorSupported (VTransferData.ResourceRefListDataFlavor))
        {
            // II: get data: 
        	ResourceRef refs[]= (ResourceRef[]) t.getTransferData(VTransferData.ResourceRefListDataFlavor);
            return refs; 
        }
        else if (t.isDataFlavorSupported (VTransferData.ResourceRefDataFlavor))
        {
            // II: get data: 
        	ResourceRef refs[]=new ResourceRef[1];
        	refs[0] = (ResourceRef) t.getTransferData(VTransferData.ResourceRefDataFlavor);
            return refs; 
        }
        // drops from Windows create these objects (thanks to swing) !:
        else if (t.isDataFlavorSupported (DataFlavor.javaFileListFlavor))
        {
        	ResourceRef refs[]=VTransferData.getJavaFileListRefs(t);
        	
            return refs; 
      
      }
      // 
      // DnD Support for KDE:  drops uri lists ! (Yay for Ke-De-Yay)
      //
      else if (t.isDataFlavorSupported(VTransferData.uriListFlavor)) 
      {
            String urilist = (String)t.getTransferData(VTransferData.uriListFlavor);
            
            Scanner scanner = new Scanner(urilist.trim());
            
           Vector<VRL> vLocs=new Vector<VRL>(); 
           
            while (scanner.hasNextLine()) 
            {                 
                try
                {
                    vLocs.add(new VRL(scanner.nextLine()));
                }
                catch (VlException e)
                {
                    Global.errorPrintln("URI list parser","***Error: Exception:"+e); 
                    // continue; 
                } 
            }
            
            ResourceRef refs[]=new ResourceRef[vLocs.size()];
            
            for (int i=0;i<vLocs.size();i++)
            {
            	refs[i]=new ResourceRef(vLocs.elementAt(i),null,null); 
            }

            return refs;
      }
        //
        //  Default MimeTypes : 
        //  Todo: DataStreams, etc: 
        
      else if (t.isDataFlavorSupported (DataFlavor.stringFlavor))
      {
          String str=(String)t.getTransferData(DataFlavor.stringFlavor);
          ResourceRef refs[]=new ResourceRef[1];
          refs[0]=new ResourceRef(new VRL(str),null,null); 
          return refs; 
    
    }
        
      throw new UnsupportedFlavorException(t.getTransferDataFlavors()[0]); 
    }

    public static boolean hasMyDataFlavor(DataFlavor[] flavors)
    {
        DataFlavor[] dataFlavors = VTransferData.dataFlavorsVRL;
        
        for (int i = 0; i < flavors.length; i++) 
        {
            for (int j=0;j<dataFlavors.length;j++)
                
            if (dataFlavors[j].equals(flavors[i])) 
            {
                return true;
            }
            
        }
        return false;
        //return true;
    }

	public static VRL[] getVRLsFrom(Transferable t) throws VRLSyntaxException, UnsupportedFlavorException, IOException
	{
		return ResourceRef.getVRLs(getRefsFrom(t)); 
	}
    
}
    
