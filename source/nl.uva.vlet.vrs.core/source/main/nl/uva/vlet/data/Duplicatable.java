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
 * $Id: Duplicatable.java,v 1.3 2011-04-18 12:00:34 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:34 $
 */ 
// source: 

package nl.uva.vlet.data;

/** 
 * Alternative to clone(). 
 *  
 * @author ptdeboer
 */ 
public interface Duplicatable<T> 
{
	/** 
	 * Return whether shallow copies are supported. 
	 * If shallow copies are supported, the duplicate(true) method
	 * will return a shallow copy. 
	 */  
	public boolean shallowSupported(); 
	
	/** Return copy (clone) of object */ 
	public T duplicate(); 
	
	/**
	 * Returns copy of object. Specify whether shallow copy is allowed. 
	 * If shallow==true, the duplicate method might still return 
	 * a non shallow copy or throw an exception if shallowSupported()==false. 
	 */ 
	public T duplicate(boolean shallow);
	
}
