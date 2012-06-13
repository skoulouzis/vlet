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
 * $Id: VMetaResource.java,v 1.3 2011-04-18 12:00:31 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:31 $
 */ 
// source: 

package nl.uva.vlet.metadata;

/**
 * A Meta Resource is a File stored inside a File. 
 * For example a File stored inside a Zip Archive. 
 * This means a translation has to be done to either
 * read or write the file to the Storage Location. 
 * Or a Meta Resource provides an extra layer on top of an 
 * existing resource. 
 * For example a media type or an encryption layer. 
 * Multiple (meta) resource can be stacked ontop (or inside) of each other.
 *   
 * @author Piter T. de Boer
 *
 */
public interface VMetaResource 
{
    /** Whether this file is a composite meta file */ 
    boolean isComposite(); 
    
    /**
     * Return the Composite Meta File type of this file.
     * Use isComposite() to check wether this (Meta) file is composite. 
     */  
    VCompositeMetaResource getComposite();    
    
}
