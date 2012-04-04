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
 * $Id: AttributeType.java,v 1.2 2011-04-18 12:00:35 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:35 $
 */ 
// source: 

//package nl.uva.vlet.data;
//
///** 
// * Basic Attribute Types.  
// */
//public enum AttributeType
//{
//	// NativeJava Types  
//	NULL("NULL"), // null type become nill;  
//	OBJECT("Object"), 
//    STRING("String"),
//    BOOLEAN("Boolean"), 
//    INT("Integer"), 
//    LONG("Long"), 
//    FLOAT("Float"), 
//    DOUBLE("Double"),
//    /** Store Enumaretes as String */ 
//    ENUM("Enum"),
//    /** Store date time string as unified string */ 
//    DATETIME("Time");
//	
//    public final String typeName;
//	
//	private AttributeType(String name)
//	{
//		this.typeName=name;
//	}
//
////	/** Resolve type of object. If unknown, return "Object"  */ 
////	public static AttributeType getType(Object object,AttributeType defaultType)
////	{
////		if (object==null)
////			return defaultType;
////		
////		AttributeType type = VAttributeTypeMap.getMap();  
////		
////		if (type!=null)
////			return type; 
////		
////		return defaultType;  
////	}
//}