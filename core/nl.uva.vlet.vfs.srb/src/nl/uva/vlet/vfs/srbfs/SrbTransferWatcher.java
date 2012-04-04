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
 * $Id: SrbTransferWatcher.java,v 1.2 2010-08-25 14:45:04 ptdeboer Exp $  
 * $Date: 2010-08-25 14:45:04 $
 */ 
// source: 

package nl.uva.vlet.vfs.srbfs;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vrs.VRSTaskWatcher;
import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.local.LocalFile;
import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.srb.SRBFileSystem;

/**
 * SRB Transfer Watcher ActionTask (direct subclass of).  
 * 
 * @author P.T. de Boer
 */

public class SrbTransferWatcher extends ActionTask
{
    private VFSTransfer vfstransfer;
    private Thread transferThread;
    private GeneralFile source;
    private GeneralFile target;
    private boolean isDir;
	private SrbFileSystem srbServer;
	private SRBFileSystem srbFS; 
    
    /**
     * Single transfer watcher which watches the ongoing transfer 
     * and updates the VFSTransfer info
     */ 
    
    SrbTransferWatcher(final VFSTransfer transfer,
    		SrbFileSystem server,
            final GeneralFile source,
            final GeneralFile target, 
            final boolean isDir,
            final Thread transferThread)
    {
        super(VRSTaskWatcher.getDefault(),"SRBTransferWatcher for target:"+target);
        
        long dirTotal;
        this.srbServer=server;
        this.vfstransfer=transfer; 
        this.transferThread=transferThread;
        this.source=source;  
        this.target=target; 
        this.isDir=isDir; 
        
        // Report transfer mode 
 
        if (isDir)
        {
        	// SRB monitoring not support anymore 
            //transfer.addLogText("Scanning source:"+source+"\n"); 
            //long stats[]=SRBServer.cumulateSizes(source);
            //dirTotal=stats[0]; 
            //transfer.addLogText("Total to transfer (bulk mode)="+dirTotal+" ("+stats[1]+")\n");
            //transfer.setTotalWorkTodo(dirTotal);
            
            if (target instanceof LocalFile)
            {
            	srbFS=(SRBFileSystem)source.getFileSystem();
                transfer.logPrintf("Downloading directory (bulkmode).\n");
            }
            else if (source instanceof LocalFile)
            {
            	srbFS=(SRBFileSystem)target.getFileSystem();
                transfer.logPrintf("Uploading directory (bulkmode).\n");
            }
            else 
            {
            	// source FS=target FS;
            	srbFS=(SRBFileSystem)source.getFileSystem();
                transfer.logPrintf("Remote directory duplication (stats might be incorrect).\n");
            }
            
        }
        else
        {
            // update single file transfer (do not update total transfer settings)
            long size=source.length();
            
            String taskStr=""; 
            if (target instanceof LocalFile)
            {
            	srbFS=(SRBFileSystem)source.getFileSystem();
            	taskStr="Downloading single SRB file.";
            }
            else if (source instanceof LocalFile)
            {
            	srbFS=(SRBFileSystem)target.getFileSystem();
                taskStr="Uploading local file.";
            }
            else
            {
            	// source FS = target FS ; 
            	srbFS=(SRBFileSystem)source.getFileSystem();
                taskStr="Remote SRB file duplication (stats might be incorrect)";
            }
            
            // transfer.addLogText(taskStr+"\n");
            transfer.startSubTask(taskStr,size);

        }
    }
    
    boolean mustStop=false; 
    
    public void doTask() throws VlException
    {
        long transferred=0; 
        long offset=0; 
        long prev=0; 
        long lastupdated=0; 
        long sleeptime=10; 
        long updatetime=1000;
        
        Debug("doTask():"+this);
        
        try
        {
            while (mustStop==false) // vfstransfer.isDone()==false)
            {
            	// check complete transfer: 
            	if (vfstransfer.isDone()==true) 
            	{
            		mustStop=true; // stop watcher on next iteration
            		break; 
            	}
            	
            	// check whether actual transfer must be stopped: 
                if ((vfstransfer.getMustStop()==true) && (srbFS!=null))
                {
                	
                	vfstransfer.logPrintf("\n***\nCan't stop transfer:"+srbFS+"\n***\n");
                	srbFS=null; // block further stop events 
                	
                	/* Still doesn't work:	
                 
                	try
                	{
                		Global.errorPrintln(this,"Interrupt Transfer: Disconnecting from:"+srbServer);
                       	srbFS.close();
                       
                       	srbFS=null;  // guard the FS is closed !  
                       	
                		// srbServer.disconnect();
                	}
                	catch (Error e) 
                	{
                		Global.debugPrintln(this,"disconnect returned Error:"+e); 
                	}
                	catch (Exception e) 
                	{
                		Global.debugPrintln(this,"disconnect returned Exception:"+e); 
                	}
                	// reconnect:
                	//srbServer.connect(); 
                	// vfstransfer.setDone(); 
                	
                	// return;  // break out of while (Stops watcher !) 
                	 
                	 */
                }    
                
                // small update timer: see KLUDGE (I)
                if (mustStop==false)
                	Thread.sleep(sleeptime);
                
                // bulkmode transfer: the just scan target directory:
                
                /*
                 if (isDir)
                 {
                 // bulkupload reports wrong fileCopyStatus
                  // scan target directory for now:
                   //total=cumulateSizes(target);
                    //transfer.setTotalTransferred(total);
                     //transfer.setCurrentTransferred(0);
                      }
                      else                        
                      //  update current anyway
                       */
                
                {
                    prev=transferred;
                    // which file does the transfer ? Uhh. 
                    if (target instanceof SRBFile)
                    {
                        transferred=((SRBFile)target).fileCopyStatus();
                        //System.err.println("target transferred="+transferred+"\n");
                    }
                    
                    if (transferred>0)
                        vfstransfer.updateSubTaskDone(transferred);
                    
                    // either source or target is SRBFile which as fileCopyStatus() 
                    
                    if (source instanceof SRBFile)
                    {
                        transferred=((SRBFile)source).fileCopyStatus();
                        //System.err.println("source transferred="+transferred+"\n");
                    }
                    
                    if (transferred>0)
                        vfstransfer.updateSubTaskDone(transferred);
                    
                    /*length() method blocks while single-tranfering a file 
                     * if (transferred<=0)
                     {
                     transferred=target.length(); // cannot get file copy status :-( ...
                     System.err.println("target length="+transferred+"\n");
                     }
                     
                     if (transferred>0)
                     transfer.setCurrentTransferred(transferred);
                     */   
                    
                    if (isDir)
                    {
                        Debug("kludge:SRB Started new transfer!"+this);
                        // KLUDGE: (I): 
                        // new file transfer started, add previous to offset
                        // TODO:SUBMIT BUG REPORT: 
                        if (transferred<prev) 
                        {
                            offset+=prev;
                        }
                        
                        // bulk mode tranfser: current transfer=total. 
                        vfstransfer.updateWorkDone(transferred+offset);
                    }
                    
                    // provide some progress information. 
                    long newtime=vfstransfer.getTime();  
                    if (newtime>(lastupdated+updatetime))
                    {
                        lastupdated=newtime; 
                        vfstransfer.logPrintf(".");
                        Global.debugPrintln(this,this+":"+">>>active<<<"); 
                    }
                    
                } // while not done
            }
           
            Debug("\n");
            Debug("finished:"+this);
            
            // done:
            if (isDir) 
            {
                long total=SrbFileSystem.cumulateSizes(target)[0];
                vfstransfer.logPrintf("\nTotal transferred (destination size)="+total+"\n"); 
                vfstransfer.updateWorkDone(total);  
            }
            else
            {
                Debug("before getLength():"+this);
                // vfstransfer.setCurrentTransferred(target.length());
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        }  
        Debug("returning from doTask():"+this);
    }
    
    private void Debug(String str)
    {
        Global.debugPrintln(this,str);
    }

    @Override
    public void stopTask()
    {
    	// stop the WATCHER, not the actual transfer:
    	this.mustStop=true; 
    	// here we don't know whether transfer was successfull 
    	// but this method is called by doopyMove after the (successfull) 
    	// transfer
    	vfstransfer.logPrintf("\nDone.\n");
    }
}
