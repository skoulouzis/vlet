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
 * $Id: PrivateKeyReader.java,v 1.2 2011-04-18 12:08:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:08:39 $
 */ 
// source: 

package nl.uva.vlet.grid.ssl;


import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JCERSAPrivateCrtKey;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.util.encoders.Base64;

/** 
 * Private Key Reader. Based on GLite code 
 */ 
public class PrivateKeyReader
{

    static 
    {
        if(Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    public PrivateKeyReader()
    {
    }

    public static PrivateKey read(BufferedInputStream bin, PasswordFinder finder)
    throws IOException
    {
        PrivateKey privateKey;
        try
        {
            byte b[] = new byte[1000];
            bin.mark(10000);
            PEMReader pemReader = new PEMReader(new InputStreamReader(bin), finder, "BC");
            Object o = pemReader.readObject();
            KeyPair pair = (KeyPair)o;
            privateKey = pair.getPrivate();
            bin.reset();
            String line;
            int num;
            do
            {
                num = bin.read(b);
                line = new String(b);
                bin.reset();
                skipLine(bin);
                bin.mark(1000);
            } while(num > 0 && !line.startsWith("-----END"));
            bin.mark(10000);
            return privateKey;
        }
        catch (IOException e)
        {
            //LOGGER.error((new StringBuilder()).append("Error while reading private key from a file. Exception: ").append(e.getClass().getName()).append(" message:").append(e.getMessage()).toString());
            throw e;
        }
    }

    public static PrivateKey read(BufferedInputStream bin)
    throws IOException
    {
        return read(bin, (PasswordFinder)null);
    }

    // public static PrivateKey read(BufferedInputStream bin, String passwd)
    //     throws IOException
    // {
    //     if(passwd == null)
    //         return read(bin, (PasswordFinder)null);
    //     else
    //         return read(bin, ((PasswordFinder) (new Password(passwd.toCharArray()))));
    // }

    public static void skipLine(BufferedInputStream stream)
    throws IOException
    {
        byte b[] = new byte[1000];
        stream.mark(1002);
        int num = 0;
        do
        {
            if(stream.available() <= 0)
                break;
            num = stream.read(b);
            int i;
            for(i = 0; i < num && b[i] != 13 && b[i] != 10; i++);
            stream.reset();
            if(b[i] == 13 || b[i] == 10)
            {
                stream.skip(i);
                stream.mark(1002);
                break;
            }
            stream.skip(1000L);
            stream.mark(1002);
        } while(true);
        num = stream.read(b);
        if(b[0] != 13 && b[0] != 10)
        {
            //sLOGGER.error("No newline char found when trying to skip line");
            throw new IOException("No newline char found when trying to skip line");
        }
        stream.reset();
        if(b[1] == 13 || b[1] == 10 && b[0] != b[1])
            stream.skip(2L);
        else
            stream.skip(1L);
        stream.mark(1002);
    }

    public static byte[] getEncoded(PrivateKey inKey)
    {
        JCERSAPrivateCrtKey key;
        if(inKey instanceof JCERSAPrivateCrtKey)
            key = (JCERSAPrivateCrtKey)inKey;
        else
            throw new IllegalArgumentException((new StringBuilder()).append("Argument was:").append(inKey).append(" Expected: JCERSAPrivateCrtKey").toString());
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
        RSAPrivateKeyStructure info = new RSAPrivateKeyStructure(key.getModulus(), key.getPublicExponent(), key.getPrivateExponent(), key.getPrimeP(), key.getPrimeQ(), key.getPrimeExponentP(), key.getPrimeExponentQ(), key.getCrtCoefficient());
        try
        {
            dOut.writeObject(info);
            dOut.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException("Error encoding RSA public key");
        }
        return bOut.toByteArray();
    }

    public static String getPEM(PrivateKey inKey)
    {
        byte bytes[] = getEncoded(inKey);
        StringBuffer buffer = new StringBuffer();
        if(inKey instanceof RSAPrivateKey)
        {
            buffer.append("-----BEGIN RSA PRIVATE KEY-----\n");
            String keyPEM = new String(Base64.encode(bytes));
            for(int i = 0; i < keyPEM.length(); i += 64)
            {
                if(keyPEM.length() < i + 64)
                    buffer.append(keyPEM.substring(i, keyPEM.length()));
                else
                    buffer.append(keyPEM.substring(i, i + 64));
                buffer.append("\n");
            }

            buffer.append("-----END RSA PRIVATE KEY-----\n");
            return buffer.toString();
        }
        else
        {
            throw new IllegalArgumentException((new StringBuilder()).append("Trying to get PEM format string of non-RSA private key, while only RSA is supported. Class was: ").append(inKey.getClass().getName()).toString());
        }
    }
}
