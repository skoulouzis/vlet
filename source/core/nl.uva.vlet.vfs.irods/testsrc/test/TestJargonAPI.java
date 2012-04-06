package test;

import java.io.IOException;
import java.util.Vector;

import org.ietf.jgss.GSSCredential;

import edu.sdsc.grid.io.GeneralMetaData;
import edu.sdsc.grid.io.MetaDataCondition;
import edu.sdsc.grid.io.MetaDataRecordList;
import edu.sdsc.grid.io.MetaDataSelect;
import edu.sdsc.grid.io.MetaDataSet;
import edu.sdsc.grid.io.irods.IRODSAccount;
import edu.sdsc.grid.io.irods.IRODSFile;
import edu.sdsc.grid.io.irods.IRODSFileSystem;
import edu.sdsc.grid.io.irods.IRODSMetaDataSet;
import nl.uva.vlet.Global;

import nl.uva.vlet.util.cog.GridProxy;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.irods.IrodsFS;
import nl.uva.vlet.vfs.irods.IrodsFSFactory;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.Registry;
import nl.uva.vlet.vrs.VRS;
import nl.vlet.uva.grid.globus.GlobusUtil;

public class TestJargonAPI
{
    public static void main(String args[])
    {
        try
        {
            //just in case:
            Global.init();
            VRS.getRegistry().addVRSDriverClass(IrodsFSFactory.class);


            String host="irods.grid.sara.nl"; 
            int port=50000;
            String user="piter_de_boer"; 
            //String zone="ams_els"; 
            //String res="SARA_BIGGRID";
            String res="ams_els"; 
            String zone="SARA_BIGGRID";
            String home="/SARA_BIGGRID/home/"+user; 
            String passwd="";

            printf(" server = irods://%s:%d \n",host,port);  
            printf(" user  = '%s' \n",user); 
            printf(" zone  = '%s' \n",zone); 
            printf(" res   = '%s' \n",res); 
            printf(" home  = '%s' \n",home); 

            GridProxy proxy = GridProxy.getDefault();

            boolean usePasswd=false;
            passwd=""; 
            
            GSSCredential gssCred;

            if (usePasswd) 
            {
                passwd="?"; 
                gssCred=null; 
            }
            else
            {
                if (proxy.isValid()==false)
                    throw new nl.uva.vlet.exception.VlAuthenticationException("Invalid Grid Proxy. Please create one.");

                gssCred = GlobusUtil.createGSSCredential(proxy);

                if (gssCred==null)
                    throw new nl.uva.vlet.exception.VlAuthenticationException("Failed to get GSS Credential. Got null");

                passwd=proxy.getProxyFilename(); 
            }

            IRODSAccount irodsAccount=new IRODSAccount(
                    host,
                    port,
                    user,
                    passwd,
                    home,
                    zone,
                    res); 

            // Method auto enables GSI authentication. 
            // Set to NULL to switch to password authentication. 

            irodsAccount.setGSSCredential(gssCred);
            IRODSFileSystem irodsfs; 

            irodsfs=new IRODSFileSystem(irodsAccount);

            printf("Connected to irods server %s:%d. Server Version='%s'\n",
                    host,port,irodsfs.getVersion());  

            printf(" irodsfs defStorageResource = '%s'\n",irodsfs.getDefaultStorageResource()); 
            printf(" irodsfs home               = '%s'\n",irodsfs.getHomeDirectory()); 
            printf(" irodsfs zone               = '%s'\n",irodsfs.getZone()); 

            IRODSFile dir=new IRODSFile(irodsfs,home);

            printf("Dir exists         = %s\n",dir.exists()); 
            printf("Dir absolute path  = %s\n",dir.getAbsolutePath()); 

            //boolean result=dir.mkdirs(); 
            //if (result==false)
            //    printf("Cannot create directory\n"); 
            
            printf("--- dir list --- \n");  

            String[] files = dir.list(); 
            for (String file:files)
            {
                printf(" - %s\n",file); 
            }

            files=testQuery(irodsfs,home,null); 
            
            for (String file:files)
            {
                printf(" - %s\n",file); 
            }
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


    public static String[] testQuery(IRODSFileSystem irodsfs, String dirpath, MetaDataCondition[] conditions) 
    {
        printf("Query path=%s\n",dirpath); 
        
        boolean completeDirectoryList=true;
        
        MetaDataRecordList[] rl1=null, rl2=null, temp=null;
        Vector<String> list = null;

        MetaDataCondition con[] = null;
        
        if (conditions == null) 
        {
            con = new MetaDataCondition[1];
        }
        else 
        {
            con = new MetaDataCondition[conditions.length + 1];
            System.arraycopy(conditions, 0, con, 1, conditions.length);
        }

        try
        {
            MetaDataSelect selects[] = 
            {
                    MetaDataSet.newSelection(GeneralMetaData.FILE_NAME),
                    MetaDataSet.newSelection(GeneralMetaData.DIRECTORY_NAME), 
            };

            // get all the files
            con[0] = MetaDataSet.newCondition(GeneralMetaData.DIRECTORY_NAME,
                    MetaDataCondition.EQUAL, dirpath);
            
            rl1 = irodsfs.query(con, selects);
            printf(" query(1) rl1 numresults=%s\n",(rl1!=null)?rl1.length:"<null>");
            
            if (completeDirectoryList) 
            {
                rl1 = MetaDataRecordList.getAllResults(rl1);
            }

            // get all the sub-directories
            selects[0] = MetaDataSet.newSelection(IRODSMetaDataSet.DIRECTORY_TYPE);
            con[0] = MetaDataSet.newCondition(
                        IRODSMetaDataSet.PARENT_DIRECTORY_NAME,
                        MetaDataCondition.EQUAL, dirpath);
            
            rl2 = irodsfs.query(con, selects);
            
            printf(" query(1) rl2 numresults=%s\n",(rl2!=null)?rl2.length:"<null>");
            
            if (completeDirectoryList) 
            {
                rl2 = MetaDataRecordList.getAllResults(rl2);
            }

            printf(" query(2) rl1 numresults=%s\n",(rl1!=null)?rl1.length:"<null>"); 
            printf(" query(2) rl2 numresults=%s\n",(rl2!=null)?rl2.length:"<null>");
            
            // change to relative path
            if (rl2 != null) 
            {
                String absolutePath = null;
                String relativePath = null;
                for (int i = 0; i < rl2.length; i++) 
                {
                    // only one record per rl
                    absolutePath = rl2[i].getStringValue(1);
                    relativePath = absolutePath.substring(absolutePath
                            .lastIndexOf("/") + 1);
                    rl2[i].setValue(0, relativePath);
                }
            }
    
        if ( (rl1 != null) && (rl2 != null) ) 
        {
            // length of previous query + (new query - table and attribute
            // names)
            temp = new MetaDataRecordList[rl1.length + rl2.length];
            // copy files
            System.arraycopy(rl1, 0, temp, 0, rl1.length);
            System.arraycopy(rl2, 0, temp, rl1.length, rl2.length);
        } 
        else 
        {
            if (rl1!=null) 
            {
                temp = rl1;
            }
            
            if (rl2!=null) 
            {
                temp = rl2;
            }
            
            else 
            {
                return new String[0];
            }
        }

        list = new Vector<String>();
        
        for (int i = 0; i < temp.length; i++) 
        {
            if (temp[i].getStringValue(0) != null) 
            {
                // only one record per rl
                list.add(temp[i].getStringValue(0));
            }
        }

        return (String[]) list.toArray(new String[0]);
        
        
        } 
        catch (IOException e) 
        {
            printf("io exception is logged and ignored:%s\n", e);
            return null;
        }

    }

}
