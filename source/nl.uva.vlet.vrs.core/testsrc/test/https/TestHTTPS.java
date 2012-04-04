package test.https;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.Registry;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSClient;

public class TestHTTPS
{

    public static void main(String args[])
    {
        Global.getLogger().setLevelToDebug();    
        
        Global.init(); 
        
        Registry reg = VRS.getRegistry();
        
        try
        {
            test();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void test() throws Exception
    {
        VRSClient vrs=new VRSClient();
        
        VNode node = vrs.openLocation(new VRL("https://gforge.vl-e.nl:443/")); 
        
        System.out.println("Got Node:"+node);
        
        VRS.exit(); 
    }
    
}
