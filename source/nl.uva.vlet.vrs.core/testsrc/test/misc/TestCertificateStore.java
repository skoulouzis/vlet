package test.misc;

import java.util.List;

import nl.uva.vlet.Global;
import nl.uva.vlet.net.ssl.CertificateStore;

public class TestCertificateStore
{
 // ====
    // ====
    // ====
    
    public static void main(String args[])
    {
        try
        {
            //cacertOptions.interactive=false; 
            //cacertOptions.alwaysAccept=false;
            Global.getLogger().setLevelToDebug(); 
            CertificateStore certStore = CertificateStore.getDefault(); 
            
            certStore.loadDERCertificate("/home/ptdeboer/.vletrc/certificates/16da7552.crt"); 
            certStore.loadPEMCertificate("/home/ptdeboer/.vletrc/certificates/16da7552.0"); 
            
            certStore.installCert("gforge.vl-e.nl:443",null);
            //certStore.installCert("voms.grid.sara.nl:8443",null);
            //certStore.installCert("wms.grid.sara.nl:9000",null);
            
            System.out.println("--- calling again ---");
            System.out.println("--- calling again ---");
            System.out.println("--- calling again ---");
            
            String host="gforge.vl-e.nl:443";
            boolean val=certStore.installCert(host,null);
            if (val==true)
                System.out.println("OK. host:"+host+". Accepted"); 
            
//          host="voms.grid.sara.nl:8443";
//          val=certStore.installCert(host,null);
//          if (val==true)
//              System.out.println("OK. host:"+host+". Accepted");
//          
//          host="wms.grid.sara.nl:9000";
//          val=certStore.installCert(host,null);
//          if (val==true)
//              System.out.println("OK. host:"+host+". Accepted"); 
//          
            List<String> alss = certStore.getAliases();
            for (String alias:alss)
            {
                System.out.println(" - "+alias); 
            }
            
            certStore.delete(); 
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
