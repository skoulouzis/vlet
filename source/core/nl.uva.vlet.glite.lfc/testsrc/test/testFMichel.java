package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.ReplicaDesc;


public class testFMichel
{

    public static void main(String args[])
    {
        LFCServer lfcServer;
        try
        {
            lfcServer = new LFCServer(new URI("lfn://lfc-biomed.in2p3.fr"));
            ArrayList<ReplicaDesc> replicas = lfcServer.getReplicasByPath("/grid/biomed/fmichel/test2.txt");
            
            for (ReplicaDesc replica:replicas)
            {
                System.out.println(replica.toString()); 
            }

        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
