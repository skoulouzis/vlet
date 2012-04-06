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
 * $Id: Licence.java,v 1.2 2011-04-18 12:27:17 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:17 $
 */ 
// source: 

package com.cloudgarden.jigloo;

public class Licence
{
    private static final String hex[] = 
        {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
            "A", "B", "C", "D", "E", "F"
        };
        

    public static void main(String args[])
    {
        test(); 
    }
    public static void test()
    {
        String licStr = encryptToHex("Jigloo v3.0.0 Prof", "GH6tfjUtf7");
        System.err.printf(" lic = '%s'\n",licStr); 
    }
    
    public static String encryptToHex(String str, String key)
    {
        return bytesToHex(encrypt(str.getBytes(), key.getBytes()));
    }
    
    public static String bytesToHex(byte bytes[])
    {
        String rep = "";
        for(int i = 0; i < bytes.length; i++)
        {
            int b = bytes[i];
            if(b < 0)
                b += 256;
            rep = rep + hex[b / 16] + hex[b % 16];
            if(i % 20 == 19)
                rep = rep + "\n";
        }

        return rep;
    }


    public static byte[] encrypt(byte data[], byte pass[])
    {
        return encrypt1(encrypt1(data, pass, false), pass, true);
    }


    public static  byte[] encrypt1(byte data[], byte pass[], boolean rev)
    {
        byte en[] = new byte[data.length];
        System.arraycopy(data, 0, en, 0, data.length);
        if(!rev)
        {
            for(int i = 0; i < en.length; i++)
            {
                int j = i % pass.length;
                if(i > 0)
                    en[i] = (byte)(en[i] + en[i - 1] + pass[j]);
                else
                    en[i] = (byte)(en[i] + pass[j]);
            }

        } else
        {
            for(int i = en.length - 1; i >= 0; i--)
            {
                int j = i % pass.length;
                if(i != en.length - 1)
                    en[i] = (byte)(en[i + 1] + en[i] + pass[j]);
                else
                    en[i] = (byte)(en[i] + pass[j]);
            }

        }
        return en;
    }
}
