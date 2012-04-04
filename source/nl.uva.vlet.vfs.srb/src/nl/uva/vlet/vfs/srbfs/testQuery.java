/*
 * Copyright 2006-2009 The Virtual Laboratory for e-Science (VL-e) 
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
 * $Id: testQuery.java,v 1.1 2010-01-11 11:05:09 ptdeboer Exp $  
 * $Date: 2010-01-11 11:05:09 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;
import edu.sdsc.grid.io.srb.SRBFileSystem;

public class testQuery 
{

	 public static void main(String args[])
	    {
	        Global.init(); 
	        
	    	MetaData.printMaps(System.out); 
	    	Global.setDebug(true); 
	    	
	    	ServerInfo info=VRSContext.getDefault().getServerInfoRegistry().getServerInfoForScheme(VRS.SRB_SCHEME); 
	    	   
	        VFSClient vfs=new VFSClient(); 
	        VDir srbHome=null;
	        
	        try
	        {
	            srbHome = (VDir)vfs.openLocation("srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/?defaultResource=vleMatrixStore");
	            SrbDir dir=(SrbDir)srbHome;
	            VDir pdir= (VDir)vfs.openLocation("srb://piter.de.boer.vlenl@mu2.matrix.sara.nl:50000/VLENL/home/piter.de.boer.vlenl?defaultResource=vleMatrixStore");
	            
	            
	            // Global.setSystemProperty("srb.userName","piter.de.boer"); 
	            
	            
	            SRBFileSystem srbfs = (SRBFileSystem)dir.srbnode.getFileSystem(); 
	            
	            // Query All: ! 
	            String path="/VLENL/home";
	            VAttributeSet[] attrSets=SrbQuery.listDirs(srbfs,path);
	            
	            if (attrSets!=null)
	            	for (int i=0;i<attrSets.length;i++)
	            	{
	            		VAttribute attrs[]=attrSets[i].toArray(); 
	                
	            		for (int j=0;j<attrs.length;j++)
	            			Debug("dirs attrs["+i+","+j+"]="+attrs[j]); 
	            	}
	            else
	            	Debug("NULL file attributeSets for:"+path); 
	            
	           
	            attrSets=SrbQuery.listFiles(srbfs,path);

	            if (attrSets!=null)
	            	for (int i=0;i<attrSets.length;i++)
	            	{
	            		VAttribute attrs[]=attrSets[i].toArray(); 
	                
	            		for (int j=0;j<attrs.length;j++)
	            			Debug("files atrs["+i+","+j+"]="+attrs[j]); 
	            	}
	            else
	            	Debug("NULL file attributeSets for:"+path);
	            
	            path="/VLENL/home/piter.de.boer.vlenl";            
	            attrSets=SrbQuery.listFiles(srbfs,path);

	            if (attrSets!=null)
	            	for (int i=0;i<attrSets.length;i++)
	            	{
	            		VAttribute attrs[]=attrSets[i].toArray(); 
	                
	            		for (int j=0;j<attrs.length;j++)
	            			Debug("files atrs["+i+","+j+"]="+attrs[j]); 
	            	}
	            else
	            	Debug("NULL file attributeSets for:"+path);
	            
	            // piter's acl: 
	            
	            VAttribute[][] acls = pdir.getACL(); 
	            if (acls!=null)
	              for (int i=0;i<acls.length;i++)
	         	  {
	        		VAttribute useracl[]=acls[i]; 
	            
	        		for (int j=0;j<useracl.length;j++)
	        			Debug("User dir ACL ["+i+","+j+"]="+useracl[j]); 
	         	  }
	            else
	            {
	            	Debug("NULL acl list for:"+pdir);
	            }
	            
	            VFile pfile=pdir.getFile("file.txt");
	            acls = pfile.getACL();
	            
	            if (acls!=null)
	              for (int i=0;i<acls.length;i++)
	         	  {
	        		VAttribute useracl[]=acls[i]; 
	            
	        		for (int j=0;j<useracl.length;j++)
	        			Debug("User file ACL ["+i+","+j+"]="+useracl[j]); 
	         	  }
	            else
	            {
	            	Debug("NULL acl list for:"+pdir);
	            }
	            
	            VFile file=pdir.createFile("testQueryFile",true);
	            
	            attrSets=SrbQuery.Query(srbfs,pdir.getPath(),null,false,file.getAttributeNames(),null);
	            if (attrSets==null)
	            {
	            	Error("Directory/File Query returned NULL for :"+pdir);
	            }
	            else for (int i=0;i<attrSets.length;i++)
	            {
	                VAttribute attrs[]=attrSets[i].toArray(); 
	                
	                for (int j=0;j<attrs.length;j++)
	                   Debug("Dir1 Attrs["+i+","+j+"]="+attrs[j]); 
	            }
	            
	            attrSets=SrbQuery.Query(srbfs,pdir.getPath(),null,true,null,null);
	            if (attrSets==null)
	            {
	            	Error("Directory/directory Query returned NULL for :"+pdir);
	            }
	            else for (int i=0;i<attrSets.length;i++)
	            {
	                VAttribute attrs[]=attrSets[i].toArray(); 
	                
	                for (int j=0;j<attrs.length;j++)
	                   Debug("Dir2 Attrs["+i+","+j+"]="+attrs[j]); 
	            }
	            
	            attrSets=SrbQuery.Query(srbfs,pdir.getPath(),file.getName(),false,file.getAttributeNames(),null);
	            if (attrSets==null)
	            {
	            	Error("File Query returned NULL for :"+pdir);
	            }
	            else for (int i=0;i<attrSets.length;i++)
	            {
	                VAttribute attrs[]=attrSets[i].toArray(); 
	                
	                for (int j=0;j<attrs.length;j++)
	                Debug("File Attrs["+i+","+j+"]="+attrs[j]); 
	            }

	         
	            String[][] query=new String[1][]; 
	            query[0]=new String[2];
	            
	            query[0][0]="owner";
	            query[0][1]="piter.de.boer"; 
	            
	            attrSets=SrbQuery.Query(srbfs,pdir.getPath(),file.getName(),false,file.getAttributeNames(),query);
	            if (attrSets==null)
	            {
	            	Error("File Query returned NULL for :"+file);
	            }
	            else for (int i=0;i<attrSets.length;i++)
	            {
	                VAttribute attrs[]=attrSets[i].toArray(); 
	                
	                for (int j=0;j<attrs.length;j++)
	                Debug("File query attrs["+i+","+j+"]="+attrs[j]); 
	            }
	            // Query single dir: 
	            attrSets=SrbQuery.Query(srbfs,dir.getLocation().getDirname(),dir.getName(),true,dir.getAttributeNames(),null);
	            // Query single dir: 
	            attrSets=SrbQuery.queryDir(srbfs,dir.getPath(),dir.getAttributeNames());
	            //TODO: check attributes
	            for (int i=0;i<attrSets.length;i++)
	            {
	                VAttribute attrs[]=attrSets[i].toArray(); 
	                
	                for (int j=0;j<attrs.length;j++)
	                Debug("Single dir attrs["+i+","+j+"]="+attrs[j]); 
	            }
	        }
	        catch (VlException e)
	        {
	            System.out.println("***Error: Exception:"+e); 
	            e.printStackTrace();
	        } 
	    }

	private static void Error(String str)
	{
		Global.errorPrintln(testQuery.class,str); 
	}

	private static void Debug(String str) 
	{
		Global.debugPrintln(testQuery.class,str); 
	}
	 
}
