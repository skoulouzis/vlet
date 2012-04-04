package test.ssl;

import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.net.ssl.CertificateStore;
import nl.uva.vlet.net.ssl.SslUtil;

public class TestChangePasswdCacerts
{
    public static void main(String args[])
    {
        SslUtil.init();
        
        CertificateStore certStore;
        
        try
        {
            certStore = CertificateStore.getDefault();
            certStore.changePassword("changeit","dummyvalue"); 
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
