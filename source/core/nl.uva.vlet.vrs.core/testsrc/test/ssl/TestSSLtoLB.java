package test.ssl;

import java.io.InputStream;
import java.net.Socket;
import java.net.URL;

import nl.uva.vlet.net.ssl.CertificateStore;
import nl.uva.vlet.net.ssl.SslUtil;

public class TestSSLtoLB
{


    public static void main(String args[])
    {
        SslUtil.init();
        
        CertificateStore certStore;
        
        try
        {
            certStore = CertificateStore.getDefault();
            certStore.addPEMCertificate("/home/ptdeboer/.vletrc/certificates/16da7552.0",true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        testSSLv3(); 
        testSSL();
        testURL();
    }
    
    public static void testSSLv3()
    {
        try
        {
            Socket socket = SslUtil.openSSLv3Socket("wms.grid.sara.nl",9000);
            InputStream inps=socket.getInputStream();
            inps.close(); 
            
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public static void testSSL()
    {
        try
        {
            Socket socket = SslUtil.openSSLSocket("wms.grid.sara.nl",9000); 
            InputStream inps=socket.getInputStream();
            inps.close(); 
            
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         
    }
    
    public static void testURL()
    {
        try 
        {
            URL url=new URL("https://wms.grid.sara.nl:9000");  
            url.openConnection().connect(); 
            Object cont = url.getContent(); 
         
            System.out.println("URL Connection ok?\ncontent="+cont); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         
    }
}
