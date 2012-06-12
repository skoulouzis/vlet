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
 * $Id: TransferWatcher.java,v 1.3 2011-04-18 12:27:28 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:28 $
 */ 
// source: 

package nl.uva.vlet.gui.actions;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.tasks.ITaskSource;

/** 
 * TransferWatcher is a dedicated File Transfer watcher
 * It monitor ongoing (backgrounded) transfer, so the main 
 * gui thread, and aktion tasks, are not bothered 
 * by ongoing transfers. 
 * 
 * @author P.T. de Boer
 */
public class TransferWatcher implements ITaskSource
{
    static private TransferWatcher backgroundTransferWatcher=new TransferWatcher(); 
    
    /** Global background task watcher for ALL VBrowser instances ! */ 
    public static TransferWatcher getBackgroundWatcher()
    {
        return backgroundTransferWatcher;
    }

    private boolean haveTasks=false;
    

    public void setHasTasks(boolean val)
    {
        this.haveTasks=val; 
    }
    
    public boolean getHasTasks()
    {
        return haveTasks;
    }
    public String getID()
    {
        return "VBrowser Transfer Manager";
    }

    public void messagePrintln(String str)
    {
        Global.infoPrintf(this,"%s\n",str);
    }

    public void handle(Exception e)
    {
        Global.logException(ClassLogger.ERROR,this,e,"Exception!\n");
    }

}
