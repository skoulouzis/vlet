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
 * $Id: ChecksumUtil.java,v 1.4 2011-04-18 12:00:40 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:40 $
 */ 
// source: 

package nl.uva.vlet.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;

public class ChecksumUtil
{
   
    
    public static String calculateMD5Checksum(InputStream in)
            throws VlException
    {

        byte[] hexChecksum = createMD5Checksum(in);
        String checksum = StringUtil.getHexString(hexChecksum);
        return checksum;

    }

    public static String calculateAdler32Checksum(InputStream in)
            throws VlException
    {

        long longChecksum = createAdler32Checksum(in);
        String checksum = Long.toHexString(longChecksum);
        return checksum;

    }

    private static long createAdler32Checksum(InputStream in)
            throws VlException
    {
        byte[] buffer = new byte[1024];
        long checksum;

        CheckedInputStream cis = null;
        try
        {

            Adler32 adler = new Adler32();
            cis = new CheckedInputStream(in, adler);
            int numRead = 0;
            while (numRead >=0 )
            {
                numRead = cis.read(buffer);
                // bug: 
                if (numRead==0)
                {
                    // microsleep
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        ClassLogger.getLogger(ChecksumUtil.class).logException(ClassLogger.ERROR,e,"Sleep interrupted!\n");
                    } 
                }
            }
            checksum = cis.getChecksum().getValue();
        }
        catch (IOException e)
        {
            throw new nl.uva.vlet.exception.VlIOException(e);
        }
        finally
        {
            try
            {
                cis.close();
            }
            catch (IOException e)
            {
                //ignore and continue
                ClassLogger.getLogger(ChecksumUtil.class).logException(ClassLogger.WARN,e,"Exception when closing stream.\n");
            }
        }
        return checksum;
    }

    public static byte[] createMD5Checksum(InputStream in) throws VlException
    {

        byte[] buffer = new byte[1024];
        int numRead = 0;

        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            java.security.DigestInputStream d = new java.security.DigestInputStream(in,md5);
            
            while (numRead != -1)
            {
                numRead = in.read(buffer);
                if (numRead >= 0)
                {
                    md5.update(buffer, 0, numRead);
                }
                
                // bug: 
                if (numRead==0)
                {
                    // microsleep
                    try
                    {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e)
                    {
                        ClassLogger.getLogger(ChecksumUtil.class).logException(ClassLogger.ERROR,e,"Sleep interrupted!\n");
                    } 
                }
                
                
            }

            return md5.digest();
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new nl.uva.vlet.exception.VlException(
                    "NoSuchAlgorithmException", ex);
        }
        catch (IOException e)
        {
            throw new nl.uva.vlet.exception.VlIOException(e);
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                throw new nl.uva.vlet.exception.VlIOException(e);
            }
        }
    }

    public static String calculateChecksum(InputStream in, String algorithm)
            throws VlException
    {
        if (algorithm.equalsIgnoreCase("MD5"))
        {
            return calculateMD5Checksum(in);
        }
        else if (algorithm.equalsIgnoreCase("Adler32"))
        {
            return calculateAdler32Checksum(in);
        }
        throw new nl.uva.vlet.exception.NotImplementedException(algorithm
                + " Checksum algorithm is not implemented ");
    }

}
