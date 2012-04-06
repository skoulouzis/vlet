package test;

import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.srm.SRMFSFactory;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;

public class TestSRMSetProxy
{
    
 public static void main(String args[])
 {
   {
       
       try
       {
           GlobalConfig.setUsePersistantUserConfiguration(false); 
           
           VRS.getRegistry().initVDriver(SRMFSFactory.class);
           
           VRSContext context=new VRSContext(); 
           VFSClient vfs=new VFSClient(context);
           GridProxy proxy=new GridProxy(context);
                      
           VFile tmpProxy=vfs.newFile("file:///tmp/testproxy.tmp"); 
               
           // copy original to temporary proxy!  
           vfs.copy(vfs.getFile("file:///tmp/testproxy.org"),tmpProxy); 
           
           // load temporary proxy 
           proxy.load(tmpProxy.getPath());
           
           // delete temporary proxy ! 
           tmpProxy.delete();
           
           context.setGridProxy(proxy); 
           
           VNode node=vfs.openLocation("srm://srm.grid.sara.nl:8443/"); 
           
           System.out.printf("---\nGot node:%s\n---\n",node); 
           
       }
       catch (Exception e)
       {
           e.printStackTrace();
       }
 

   }
    
 }
 
}
