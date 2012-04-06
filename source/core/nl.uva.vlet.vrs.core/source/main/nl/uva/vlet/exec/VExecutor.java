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
 * $Id: VExecutor.java,v 1.3 2011-04-18 12:00:26 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:26 $
 */ 
// source: 

package nl.uva.vlet.exec;

import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.exception.VlException;

/**
 * Under Construction: if a (remote) resource can execute command
 * this interface is implemented. 
 * Both the local and sftp filesystem can execute commands. 
 * 
 * @author Piter T. de Boer
 */
public interface VExecutor 
{
	/**
	 * Checks whether the specified command can be executed. 
	 * Use this method for example to test whether the implementing 
	 * resource can execute "/bin/bash"
	 */ 
	public boolean canExecute(String command,StringHolder explanation); 
	
	/**
	 * Execute command cmds[0] with argument cmds[1] ... cmds[n] and 
	 * return Process object.
	 * 
	 * Returns Process object of terminated process or when wait=false
	 * the Process object of running process. 
	 * 
	 * @param wait: wait until process completes. 
	 */
	public VProcess execute(String cmds[], boolean wait) throws VlException;
	
}
