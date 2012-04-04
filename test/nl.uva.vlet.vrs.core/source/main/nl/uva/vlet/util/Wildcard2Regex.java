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
 * $Id: Wildcard2Regex.java,v 1.3 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.util;

import java.util.regex.Pattern;
/** 
 * Source code from: http://www.rgagnon.com/javadetails/java-0515.html
 * <p>
 * If you find this article useful, consider making a small donation
 * to show your support for this Web site and its content.	
 * <p>
 * Written and compiled by R�al Gagnon �1998-2005
 * <p>
 * 
 */
public class Wildcard2Regex 
{
	    public Wildcard2Regex() {  }
	    
	    public static void main(String[] args) 
	    {
	        String test = "123ABC";
	        System.out.println(test);
	        System.out.println(Pattern.matches(wildcardToRegex("1*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("?2*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("??2*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("*A*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("*Z*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("123*"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("123"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("*ABC"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("*abc"), test));
	        System.out.println(Pattern.matches(wildcardToRegex("ABC*"), test));
	        /*
	           output :
	           123ABC
	            true
	            true
	            false
	            true
	            false
	            true
	            false
	            true
	            false
	            false
	        */
	        
	    }
	    
	    public static String wildcardToRegex(String wildcard)
	    {
	        StringBuffer s = new StringBuffer(wildcard.length());
	        s.append('^');
	        for (int i = 0, is = wildcard.length(); i < is; i++) 
	        {
	            char c = wildcard.charAt(i);
	            switch(c) 
	            {
	                case '*':
	                    s.append(".*");
	                    break;
	                case '?':
	                    s.append(".");
	                    break;
	                    // escape special regexp-characters
	                case '(': case ')': case '[': case ']': case '$':
	                case '^': case '.': case '{': case '}': case '|':
	                case '\\':
	                    s.append("\\");
	                    s.append(c);
	                    break;
	                default:
	                    s.append(c);
	                    break;
	            }
	        }
	        
	        s.append('$');
	        return(s.toString());
	    }
} // class

