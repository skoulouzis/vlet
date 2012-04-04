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
 * $Id: JobMonitorController.java,v 1.3 2011-04-18 12:27:23 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:23 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers.grid.jobmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.widgets.NavigationBar;
import nl.uva.vlet.vrl.VRL;

public class JobMonitorController implements ActionListener
{
    private JobMonitor monitor=null; 

    public JobMonitorController(JobMonitor jobMonitor)
    {
        this.monitor=jobMonitor; 
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {   
        debug("Action:"+e);
        
        String cmdStr=e.getActionCommand(); 
        
        if (StringUtil.equals(cmdStr,JobMonitor.ACTION_REFRESH))
            update(true);
        
        if (StringUtil.equals(cmdStr,NavigationBar.NavigationAction.REFRESH))
            update(true); 

    }

    private static void debug(String msg)
    {
        Global.errorPrintf("JobMonitor","%s\n",msg);
    }

    public void stopViewer()
    {
        
        
    }

    public void updateLocation(VRL loc)
    {
        this.monitor.updateLocationBar(loc); 
        load(loc); 
    }

    public void dispose()
    {
        
    }
    
    private Object loadMutex=new Object();
    
    private boolean isLoading=false; 
    
    protected void load(VRL loc)
    {
        // thread check: 
        synchronized(loadMutex)
        {
            if (this.isLoading==true)
            {
                return;      
            }
            
            this.isLoading=true; 
        }
        
        this.monitor.setViewerTitle("Loading:"+loc);
            
        StringList jobids=new StringList();
        
        try
        {
            String jidlTxt=UIGlobal.getResourceLoader().getText(loc);
            String lines[]=jidlTxt.split("\n");
            
            for (String line:lines)
            {
                if (StringUtil.isEmpty(line))
                    continue; 
                
                if (line.startsWith("#"))
                    continue;
             
                // remove optional fluf and allow tabs and ';' as URI seperators 
                String vals[]=line.split("[ \t;]");
                
                //ony check first string: 
                if ((vals==null) || (vals.length<=0)) 
                    continue;
                
                for (String val:vals)
                    if (StringUtil.isNonWhiteSpace(val))
                        jobids.add(val); 
        
                this.monitor.setViewerTitle("Monitoring:"+loc);
                
            }
            
        }
        catch (Throwable t)
        {
            this.monitor.setViewerTitle("*** Error loading file"); 
            
            handle("Couldn't load Job ID File:"+loc, t); 
        }
        finally
        {
            synchronized(this.loadMutex)
            {
                this.isLoading=false;
            }
        }
        
        updateJobIds(jobids); 
    }
    
    
    public void handle(String action,Throwable t)
    {
        System.err.println("Exception:"+action); 
        t.printStackTrace(System.err); 
    }
    
    protected void updateJobIds(StringList ids)
    {
        this.monitor.getJobMonitorDataModel().updateJobids(ids); 
    }
    
    protected void update(boolean fullUpdate)
    {
        this.monitor.getJobMonitorDataModel().update(fullUpdate);  
    }
}
