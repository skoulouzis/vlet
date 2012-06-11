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
 * $Id: JythonExecutor.java,v 1.3 2011-04-18 12:27:11 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:11 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRSContext;

public class JythonExecutor
{
	VRSContext vrsContext=null; 
	VRL jythonVRL=null; 
	
	public JythonExecutor(VRL vrl)
	{
		jythonVRL=vrl;
	}

	public JythonExecutor(VRSContext context)
	{
		init(context); 
	}

	private void init(VRSContext context)
	{
		this.vrsContext=context; 
	}

	public void start()
	{		
	}

	public void execute(VRL vrl)
	{
		this.jythonVRL=vrl;
		start(); 
	}
}
