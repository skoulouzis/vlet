package test.ssl;

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
