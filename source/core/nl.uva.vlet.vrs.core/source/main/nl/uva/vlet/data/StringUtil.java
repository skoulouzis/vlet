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
 * $Id: StringUtil.java,v 1.18 2011-11-25 14:30:45 ptdeboer Exp $  
 * $Date: 2011-11-25 14:30:45 $
 */ 
// source: 

package nl.uva.vlet.data;

// Do not import Global and/or Logger(s) here. 
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;

/**
 * Yet another 'StringUtil'
 * 
 * This supports VRS data types.
 * 
 * @author P.T. de Boer
 * 
 */
public class StringUtil
{
    static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
            (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
            (byte) 'd', (byte) 'e', (byte) 'f' };

    /** Returns true when string is null or empty ("") */
    public static boolean isEmpty(String str)
    {
        if (str == null)
            return true;

        if (str.compareTo("") == 0)
            return true;

        return false;
    }

    public static boolean isEmpty(VRL loc)
    {
        if (loc == null)
            return true;

        String str = loc.toString();

        if (StringUtil.isEmpty(str))
            return true;

        if (StringUtil.equals(str, ":"))
            return true;

        return false;
    }

    public static boolean notEmpty(String str)
    {
        return (isEmpty(str) == false);
    }

    /**
     * Merge 2 String Arrays. null entries are skipped. duplicates are 'merged'.
     * 
     */
    public static String[] mergeLists(String[] list1, String[] list2)
    {
        return StringList.merge(list1, list2);
    }

    /**
     * Merge 3 String Arrays. null entries are skipped. duplicates are 'merged'.
     */
    public static String[] mergeLists(String[] list1, String[] list2,
            String[] list3)
    {
        return StringList.merge(list1, list2, list3);
    }

    /** Returns copy of String or null */
    public static String duplicate(String str)
    {
        if (str == null)
            return null;

        return new String(str);
    }

    public static String findReplace(String source, String pattern,
            String replace)
    {
        if (source == null)
            return null;

        // first occurance
        int i = source.indexOf(pattern);

        // System.err.println("index="+i);

        if (i < 0)
            return new String(source);

        int len = pattern.length();

        return source.substring(0, i) + replace
                + source.substring(i + len, source.length());

    }

    /** NULL proof compare method */
    public static int compare(String s1, String s2)
    {
        return compare(s1, s2, false);
    }

    public static int compareIgnoreCase(String s1, String s2)
    {
        return compare(s1, s2, true);
    }

    /** NULL proof compare method */
    public static int compare(String s1, String s2, boolean ignoreCase)
    {
        // if (s1) < (s2) return negative
        // if (s1) > (s2) return positive 
        
        if (s1 == null)
            if (s2 == null)
                return 0;
            else
                // null < (not)null ?
                return -1;

        if (s2 == null)
            // (not) null > null ?
            return 1;

        if (ignoreCase)
            return s1.compareToIgnoreCase(s2);
        else
            return s1.compareTo(s2);
    }

    /**
     * Create InputStream from specified String to read from
     * 
     * @throws UnsupportedEncodingException
     */
    public static ByteArrayInputStream createStringInputStream(
            String xmlString, String encoding)
            throws UnsupportedEncodingException
    {
        ByteArrayInputStream stream;
        stream = new ByteArrayInputStream(xmlString.getBytes(encoding));
        return stream;
    }

    public static boolean equals(String str1, String str2)
    {
        return (compare(str1, str2) == 0);
    }

    public static boolean equalsIgnoreCase(String str1, String str2)
    {
        return (compareIgnoreCase(str1, str2) == 0);
    }

    /** Return number with zeros padded to the right, like "0013" */
    public static String toZeroPaddedNumber(int number, int paddingSize)
    {
        String intstr = "" + number;
        int dif = paddingSize - intstr.length();
        while ((dif--) > 0)
            intstr = "0" + intstr;

        return intstr;
    }

    /**
     * Null Pointer proof String.endsWith() method. First argument is complete
     * string and second argument is the substring which the first argument
     * should end with. If either strings is NULL, false is returned.
     * 
     * @param fullString
     * @param subString
     * @return true if no argument is NULL and
     *         fullString.endsWith(subString)==true
     */
    public static boolean endsWith(String fullString, String subString)
    {
        if (fullString == null)
            return false;

        if (subString == null)
            return false;

        return fullString.endsWith(subString);
    }

    /**
     * If this string consists of multiple lines each newline will be prepended
     * by the identStr; If the String is a non newline terminated String, only
     * the String itself will be prepended.
     * 
     * @param orgStr
     * @param indentStr
     * @return
     */
    public static String insertIndentation(String orgStr, String indentStr)
    {
        if (orgStr == null)
            return null;

        if (indentStr == null)
            return orgStr;

        String lines[] = orgStr.split("\n");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++)
        {
            // do not prepend empty lines !
            if (isEmpty(lines[i]) == false)
            {
                builder.append(indentStr);
                builder.append(lines[i]);
            }
            // has more lines ? Add newline BETWEEN lines
            if (i < (lines.length - 1))
                builder.append("\n");
        }
        return builder.toString();
    }

    /** Return String str or "" if String str is null */
    public static String noNull(String str)
    {
        if (str == null)
            return "";

        return str;
    }

    /** Returns true if the String strVal represents the string value "true" */
    public static boolean isTrue(String strVal)
    {
        if (strVal == null)
            return false;

        try
        {
            Boolean val = Boolean.parseBoolean(strVal);
            return val;
        }
        catch (Exception e)
        {
            return false;
        }

    }

    /** Returns true if the String strVal represents the string value "false" */
    public static boolean isFalse(String strVal)
    {
        if (strVal == null)
            return false;

        try
        {
            Boolean val = Boolean.parseBoolean(strVal);
            return (val == false);
        }
        catch (Exception e)
        {
            return false;
        }

    }

    public static String getHexString(byte[] raw) throws VlException
    {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw)
        {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        try
        {
            return new String(hex, "ASCII");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new nl.uva.vlet.exception.VlException("UnsupportedEncodingException",e);
        }
    }
    
    /** Null Pointer Save toString() method */ 
    public static String toString(Object obj)
    {
        if (obj==null)
            return "<NULL>";
        else
            return obj.toString(); 
    }

    /** Compares Object based on STRING representation */ 
    public static boolean equalsIgnoreCase(Object obj1,Object obj2)
    {
        if (obj1==null)
        {
            if (obj2==null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (obj2==null)
            {
                return false;
            }
            else
            {
                return equalsIgnoreCase(obj1.toString(),obj2.toString());
            }
        }
    }
    
    /** Compares Object based on STRING representation */ 
    public static boolean equals(Object obj1,Object obj2)
    {
        if (obj1==null)
        {
            if (obj2==null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (obj2==null)
            {
                return false;
            }
            else
            {
                return equals(obj1.toString(),obj2.toString());
            }
        }
    }

    /** Whether string contains non white space characters */  
    public static boolean isNonWhiteSpace(String str)
    {
        if ((str==null) || (str.equals("")))
            return false;
        
        // strip all whitespace; 
        str=str.replaceAll("[ \t\n]*","");
        
        if (str.equals(""))
            return false; 
        
        // Add more checks ? 
        return true;
    }

    public static String boolString(boolean val)
    {
        return boolString(val,"true","false"); 
    }
    
    /** Convenience method for functional statements */ 
    public static String boolString(boolean val,String trueValue,String falseValue)
    {
        return val?trueValue:falseValue; 
    }

    /** Remove spaces, newlines and tabs. */ 
    public static String stripWhiteSpace(String val)
    {
        if ((val==null) || (val==""))
            return null;
        
        return val.replaceAll("[ \t\n]",""); 
    }

    public static boolean parseBoolean(String valstr, boolean defVal)
    {  
        if (StringUtil.isNonWhiteSpace(valstr)==false)
            return defVal;
        
        try
        {
            return Boolean.parseBoolean(valstr);
        }
        catch (Throwable t)
        {
            ;
        }
        return defVal; 
    }

    public static boolean isNonWhiteSpace(Object val)
    {  
        if (val==null) 
            return false; 
        
        return isNonWhiteSpace(val.toString()); 
    }
    
    public static int parseInt(String value, int defaultValue)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) 
        {
            return defaultValue; 
        }
    }

    public static long parseLong(String value, long defaultValue)
    {
        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            return defaultValue; 
        }
    }

}