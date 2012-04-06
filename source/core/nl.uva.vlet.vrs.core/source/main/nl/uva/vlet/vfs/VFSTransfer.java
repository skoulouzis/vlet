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
 * $Id: VFSTransfer.java,v 1.8 2011-04-18 12:00:27 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:27 $
 */ 
// source: 

package nl.uva.vlet.vfs;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.tasks.DefaultTaskMonitor;
import nl.uva.vlet.tasks.ITaskMonitor;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;

/**
 * VFSTransfer class. Keeps record of ongoing transfers. 
 * VFSS classes should update this transfer information when 
 * given as an argument when requested to perform an (file) transfer. 
 * <p>
 * The minimum use is: <pre> 
 *   startTask("Task") // Start Stransfer <br> 
 *   // dostuff...<br> 
 *   endTask()  // End Transfer <br>
 * </pre>
 * It is recommend to set and update the current transfer size
 * to allow transfer statistics to be calculated. <br>
 * Update the transfer info as follows:<pre>
 *   startTask("Task") // Start Stransfer <br> 
 *   setTotalWorkTodo(fileSize)
 *   while(transferDone==false)
 *   {
 *   	// do transfer ..<br>
 *      updateWorkDone(bytesTransferred)
 *   }
 *   endTask() // End Transfer <br>
 * </pre>
 * 
 * @author P.T. de Boer
 */
public class VFSTransfer extends DefaultTaskMonitor
{    
    // ========================================================================
    // instance
    // ========================================================================

    // VFS Transfer Fields 
    private String resourceType="";
    
    /** root source or parent directory */ 
    private VRL currentSource;
    
    private final VRL destination; // destination PARENT directory, not target VRL
    
    /** Whether this transfer is move */  
    private final boolean isMove;
    
    /** Destination node to which has been copied */ 
    private VNode resultNode;
    
    /** Total sources to be done */ 
    private int totalSources=-1; // default is unknown; 
    
    private int sourcesDone=0;

    private boolean multiTransfer=false;

    private VFSActionType actionType=VFSActionType.UNKNOWN; 
    
   // private ICopyInteractor interactor; 
    
    // instance methods
    public VFSTransfer(ITaskMonitor parentMonitor, String resourceType,VRL source, VRL destination,boolean isMove)
    {
        setParent(parentMonitor); // add this transfer to parent monitor 
        this.resourceType=resourceType; 
        this.currentSource=source; 
        this.destination=destination; 
        this.isMove=isMove; 
        this.totalSources=1; 
    }

    public void setTotalWorkTodo(long size)
    {
       super.setTotalWorkTodo(size); 
    }
   
    /**
     * Set transfer size of current transfer
     */
    public void setSubTaskTodo(long size)
    {
        super.setSubTaskTodo(size); 
    }
    
    /** 
     * Return type of action: Rename,Delete or 3rd Party copy. 
     * Use isMove() to determine whether this is a copy or a move action 
     * (If applicable).  
     * @return
     */
    public VFSActionType getActionType()
    {
        return this.actionType; 
    }

    /** Specify what kinf of action this transfer is */ 
    public void setTransferType(VFSActionType type)
    {
        this.actionType=type; 
        
    }
    
    // =======================================================================
    // VFSTransfer methods 
    // =======================================================================
    
    public void printReport(ClassLogger logger)
    {
        logger.debugPrintf("--- VFSTransfer report ---");
        logger.debugPrintf(" Exception     =%s\n",(this.getException()==null?getException():"no")); 
        logger.debugPrintf(" transfer ID   =%s\n",this.getId());
        logger.debugPrintf(" source        =%s\n",this.currentSource);
        logger.debugPrintf(" destination   =%s\n",this.destination); 
        logger.debugPrintf(" type          =%s\n",this.actionType); 
        logger.debugPrintf(" is move       =%s\n",StringUtil.boolString(this.isMove));  
        logger.debugPrintf(" transfertime  =%f\n",(this.getStopTime()-getStartTime())/1000.0); 
        logger.debugPrintf(" transfer size =%d\n",+getSubTaskTodo()); 
    }
       
    /**
     * When a Tranfer is done, this method can be used to 
     * store the resulting VFS Node. 
     */
    public void setResultNode(VNode node)
    {
        this.resultNode=node; 
    }
    
    /** 
     * If the transfer method has stored the Resulting VFS node,
     * this method will return the node. 
     * Return NULL if setResultNode() has been used by the transfer
     * method.
     * @return
     */
    public VNode getResultNode()
    {
        return resultNode; 
    }
    
    public VAttribute[] getAttributes()
    {
        VAttribute attrs[]= 
          {
                new VAttribute("transferID",getId()),  
                new VAttribute("type",resourceType),  
                new VAttribute("method",(isMove?"Move":"Copy")),
                new VAttribute("source",currentSource),  
                new VAttribute("destination",destination),  
                new VAttribute("done",isDone()),  
                new VAttribute("exception",getException().toString())  
          };
        
        return attrs; 
        
    }
    
    /**
     * @return Returns the destination.
     */
    public VRL getDestination()
    {
        return destination;
    }

   
    
    public String toString()
    {
        return 
         "transferID  ="+this.getId()+"\n"
        +"resourceType="+this.resourceType+"\n"
        +"method      ="+(isMove?"move":"copy")+"\n"
        +"source      ="+this.currentSource+"\n"
        +"destination ="+this.destination+"\n"
        +"exception   ="+(this.getException()==null?"none":getException().getClass().getName());
    }
    
    public int getSourcesDone()
    {
        return this.sourcesDone; 
    }
    
    public void setSourcesDone(int val) 
    {
        this.sourcesDone=val; 
    }

    public int getTotalSources()
    {
        return this.totalSources; 
    }
    
    /**
     * Set the total nr of sources to be tranferred.
     * This is the number of files+directories. 
     */
    
    public void setTotalSources(int nr)
    {
        this.totalSources=nr;
    }
    
    /**
     * @return Returns the current source
     */
    public VRL getSource()
    {
        return currentSource;
    }
    
    public void setCurrentSource(VRL vrl)
    {
        currentSource=vrl;
    }
    
    /**
     * @return Returns the root source.
     */
    public VRL getRootDestination()
    {
        return this.destination;
    }
    
    /**
     * @return Returns the isMove.
     */
    public boolean isMove()
    {
        return isMove;
    }
    
    /** Return current transfer speed string in KB/s */
    public String getCurrentSpeedString()
    {
        String speedstr="";
        
        // only print current transfer is have it:
        
        if (getSubTaskDone()<=0) 
            speedstr+="";
        else
            speedstr+=getSubTaskDone()/(getSubTaskDoneLastUpdateTime()-getStartTime()+1)+"/";
        
        if (getTotalWorkDone()<=0) 
            speedstr+="(?)KB/s";
        else
            speedstr+=getTotalWorkDone()/(getTotalWorkDoneLastUpdateTime()-getStartTime()+1)+"KB/s"; 
        
                
        return speedstr; 
    }
    
    public void endTask(String taskName)
    {
        Global.warnPrintf(this,"Ending VRS Transfer:%s\n",taskName); 
        
        super.endTask(taskName); 
        // bug: after recursive directory copy: the stats 
        // might be wrong 
        if (this.getTotalWorkTodo()<=0)
        {
            long size=this.getTotalWorkDone(); 
            this.setTotalWorkTodo(size);
            this.setTotalWorkDone(size);
        }
    }

    public void setMultiTransfer(boolean value)
    {
        this.multiTransfer=value; 
    }
    
    public boolean isMultiTransfer()
    {
        return multiTransfer;  
    }

    /** Return information about the current source */ 
    public String getSourceText()
    {
        String source="?";
        
        if (isMultiTransfer())
            source="(Multi) "; 
        else
            source="";
         
        if (this.currentSource!=null)
            source+=currentSource;    
       
        return source; 
    }

    public void addSourcesDone(int i)
    {
       this.sourcesDone++; 
    }


//    public void setInteractor(ICopyInteractor interactor)
//    {
//       this.interactor=interactor; 
//    }
   
}
