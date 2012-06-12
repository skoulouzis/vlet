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
 * $Id: EmulatorListener.java,v 1.3 2011-04-18 12:27:10 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:10 $
 */ 
// source: 

package nl.uva.vlet.util.vlterm;

public interface EmulatorListener 
{
	/** When and Xterm Graph Mode update was received */ 
	void notifyGraphMode(int type, String arg); 

	/** When the Characted has changed */ 
	void notifyCharSet(String charSet);

	/**
	 * Is send AFTER the terminal has been resized. 
	 * CharPanel already has updated it size 
	 */ 
	void notifyResized(int columns,int rows); 
	
}
