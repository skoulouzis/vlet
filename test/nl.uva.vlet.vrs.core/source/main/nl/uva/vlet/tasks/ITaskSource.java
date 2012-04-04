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
 * $Id: ITaskSource.java,v 1.3 2011-04-18 12:00:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:34 $
 */ 
// source: 

package nl.uva.vlet.tasks;

/** 
 * This interface is created so that the ActionTask class 
 * can be part of vrs.core/main, so that class can be reused as
 * Thread spawn class.
 * If an implementation want to keep track of the ActionTasks it spawns 
 * (BrowserController) is can implement this interface.   
 * When creating a new ActionTask the ITaskSource can be given as parent
 * of that task. 
 * @author P.T. de Boer
 * @see nl.uva.vlet.tasks.ActionTask
 */
public interface ITaskSource
{
    /** Method to update the TaskSource if it has tasks running*/  
    public abstract void setHasTasks(boolean b);

    /** Optional ID to use by the ActionTask */ 
    public abstract String getID();

    /** Message printer for the ActionTask to print messages and/or errors */
    public abstract void messagePrintln(String str);
    
    /** Handle Exception thrown by the action task */ 
    public abstract void handle(Exception e);
}
