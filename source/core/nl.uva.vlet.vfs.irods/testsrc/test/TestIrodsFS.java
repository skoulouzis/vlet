package test;

import edu.sdsc.grid.io.irods.IRODSFileSystem;
import nl.uva.vlet.Global;

import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.irods.IrodsFS;
import nl.uva.vlet.vfs.irods.IrodsFSFactory;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VRS;

public class TestIrodsFS
{
    public static void main(String args[])
    {
        try
        {
            Global.init();
            VRS.getRegistry().addVRSDriverClass(IrodsFSFactory.class);
            
            String host="irods.grid.sara.nl"; 
            int port=50000;
            String user="piter_de_boer"; 
            String res="ams_els"; 
            String zone="SARA_BIGGRID";
            String home="/SARA_BIGGRID/home/"+user; 
            
            IrodsFS irodsfs = IrodsFS.createFS(host,
                    port,
                    user,
                    home,
                    zone,
                    res); 
            
            VRL vrl=irodsfs.getServerVRL().resolve(home); 

            printf(" opening dir: %s\n",vrl);
            
            VDir dir=irodsfs.newDir(vrl); 
  
            printf(" dir exists: %s\n",dir.exists()); 
            
            VFSNode[] nodes = dir.list();
            
            for (VFSNode node:nodes)
            {
                printf(" - node =%s\n",node);  
            }
            
            IRODSFileSystem ifs = irodsfs.getIRODSFileSystem();
            
            printf(" irodsfs defStorageResource  ='%s'\n",ifs.getDefaultStorageResource());
            printf(" irodsfs home                ='%s'\n",ifs.getHomeDirectory()); 
            printf(" irodsfs zone                ='%s'\n",ifs.getZone()); 
             
            ifs.close(); 
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
	
    public static void printf(String format,Object... args)
    {
        System.err.printf(format,args); 

    }
}
