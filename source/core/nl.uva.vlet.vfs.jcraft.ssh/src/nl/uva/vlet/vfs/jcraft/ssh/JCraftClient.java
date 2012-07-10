package nl.uva.vlet.vfs.jcraft.ssh;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringUtil;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JCraftClient
{
    public static final String SSH_KNOWN_HOSTS="known_hosts"; 
    public static final String SSH_CONFIG_SIBDUR=".ssh"; 
    public static final String SSH_DEFAULT_ID_RSA="id_rsa";
    public static final int SSH_DEFAULT_LOCAL_PORT = 6666 ; 
    
    private static ClassLogger logger;

    static
    {
        logger=ClassLogger.getLogger(JCraftClient.class);
    }
    
    static ClassLogger getLogger()
    {
        return logger; 
    }
    
    public static class SSHConfig
    {
        public String sshKnownHostsFile = null;  // default to $HOME/.ssh/known_hosts
        public String sshConfigDir = null; 
        public String sshIdFiles[]=null;         // default to {"$HOME/.ssh/id_rsa"}
    }
    
    /*
    public static final int SSH_FX_OK = 0;
    public static final int SSH_FX_EOF = 1;
    public static final int SSH_FX_NO_SUCH_FILE = 2;
    public static final int SSH_FX_PERMISSION_DENIED = 3;
    public static final int SSH_FX_FAILURE = 4;
    public static final int SSH_FX_BAD_MESSAGE = 5;
    public static final int SSH_FX_NO_CONNECTION = 6;
    public static final int SSH_FX_CONNECTION_LOST = 7;
    public static final int SSH_FX_OP_UNSUPPORTED = 8;
    */
   private static String[] errorStringsList =
       {       
           "SSH:OK",                   // 0=No error SSH_FX_OK 
           "SSH:End of file reached",  // 1=SSH_FX_EOF
           "SSH:No such file",         // 2=SSH_FX_NO_SUCH_FILE
           "SSH:Permission denied",    // 3=...
           "SSH:General failure",      // 4
           "SSH:Bad message",          // 5
           "SSH:No connection",        // 6
           "SSH:Connection lost",      // 7
           "SSH:Unsupported operation" // 8
       };

   public static String getJschErrorString(int id)
   {
       // nr matches index in error list 
       if ((id >= 0) && (id <  errorStringsList.length))
           return errorStringsList[id];
   
       return "Unknown Error";
   }
   
   // =========================================================================
   //
   // ========================================================================= 
   private static Random portRandomizer = new Random();

   private static Set<Integer> usedLocalPorts = new HashSet<Integer>();
   
   static int _getFreeLocalPort(boolean registerPort)
   {
       // use randomizer for now: (is much faster!)
       // todo: add to config options.
       int offset = 10000;
       int max = 65535;
       int port = 0;

       for (int i = 0; i < 100; i++)
       {
           // synchronized inside for loop
           // port clash still possible between _get and _register
           port = offset + portRandomizer.nextInt(max - offset);
           // Integer(A) equals Integer(B) when (int)A==(int)B.
           synchronized(usedLocalPorts)
           {
               if (usedLocalPorts.contains(new Integer(port)) == false)
               {
                   if (registerPort)
                       _registerUsedLocalPort(port);
                   return port;
               }
           }
       }
       
       return -1;
   }
   
   static void _registerUsedLocalPort(int port)
   {
       // synchronized only to avoid concurrent modification errors. 
       synchronized(usedLocalPorts)
       {
           usedLocalPorts.add(new Integer(port)); 
       }
   }
   
   static void _freeUsedLocalPort(int port)
   {
       // synchronized only to avoid concurrent modification errors. 
       synchronized(usedLocalPorts)
       {
           usedLocalPorts.remove(new Integer(port)); 
       }
   }
   
   static boolean _hasFreeLocalPort(int port)
   {
       synchronized(usedLocalPorts)
       {
           if (usedLocalPorts.contains(new Integer(port)))
               return false;
           else
               return true; 
       }
   }
   
   /**
    * Create local port based on a hash code from a remote user+host+port combination. 
    * This way always the same local port will be used for the same remote user+host+port combination. 
    * @return
    */
   static int _createLocalProxyHashPort(String user,String host,int port,boolean registerPort)
   {
       String hashStr=user+"@"+host+":"+port;
       
       int hash=hashStr.hashCode(); 
       int min=10000;
       int max=65535;
       int range=max-min; 
       
       if (hash<0)
           hash=-hash; // negative hash. 
       
       for (int i=0;i<32;i++)
       {
           // check for hash collision 
           int lport=min+((hash+i)%range); 

           if (_hasFreeLocalPort(port))
           {
               if (registerPort)
                   _registerUsedLocalPort(lport);
               
               return lport;
           }
       }
       
       // return whatever: 
       return _getFreeLocalPort(registerPort); 
   }

   // =========================================================================
   //
   // ========================================================================= 
   
    private JSch jschInstance=null;

    private SSHConfig sshConfig=null; 
    
    public JCraftClient() throws JSchException
    {
        init(null); 
    }
    
    public JCraftClient(SSHConfig sshConfig) throws JSchException
    {
        init(sshConfig); 
    } 
    
    private void init(SSHConfig optConfig) throws JSchException
    {
        logger.infoPrintf(">>> JCraftClient INIT <<<\n");
    
        this.jschInstance = new JSch();

        // default settings: 
        if (optConfig!=null)
        {
            this.sshConfig=optConfig;
        }
        else
        {
            // init defaults: 
            this.sshConfig=new SSHConfig();
            // auto init
            this.sshConfig.sshConfigDir=getUserHomePath()+"/"+SSH_CONFIG_SIBDUR;
            // auto init
            this.sshConfig.sshKnownHostsFile=sshConfig.sshConfigDir+"/"+SSH_KNOWN_HOSTS;
            // auto init
            this.sshConfig.sshIdFiles=new String[]{sshConfig.sshConfigDir+"/"+SSH_DEFAULT_ID_RSA};
        }
        
        logger.infoPrintf(" - known_hosts  = %s\n",this.sshConfig.sshKnownHostsFile); 
        
        if (sshConfig.sshKnownHostsFile!=null)
            this.setKnownHostsFile(sshConfig.sshKnownHostsFile);
        if (sshConfig.sshIdFiles!=null)
            this.setSSHIdentityFiles(sshConfig.sshIdFiles,false);    
    }
    
    /** 
     * Resolve identities and merge with identity registry 
     */ 
    public boolean mergeSSHIdentities(String[] idFiles) throws JSchException
    {
        return setSSHIdentityFiles(idFiles,true);
    }

    /** 
     * Specify SSH Identities. Use names only. 
     * The Actual SSH Key files must exists in the SSH Config Dir 
     * @throws FileNotFoundException 
     */
    public boolean setSSHIdentityFiles(String idFiles[]) throws JSchException
    {
        return setSSHIdentityFiles(idFiles,false);
    }
    
    public boolean setSSHIdentityFiles(String idFiles[],boolean mergeIDs) throws JSchException
    {
        Set<String> existingIDs=new HashSet<String>(); 
        boolean allSet=true; 
        
        if (mergeIDs==false)
        {
            jschInstance.removeAllIdentity(); 
        }
        else
        {
            Vector<?> ids = jschInstance.getIdentityNames(); 
            if ((ids!=null) && (ids.size()>0)) 
            {
                int n=ids.size();
                for (int i=0;i<n;i++)
                {
                    Object id=ids.get(i); 
                    existingIDs.add(ids.get(i).toString()); 
                    logger.debugPrintf(" - existing ID=%s\n",id); 
                }
            }
        }
        
        String sshConfigDir=getUserHomePath()+"/"+SSH_CONFIG_SIBDUR; 
        
        for (String id:idFiles)
        {
            String idFilePath=id;
            // relative path: 
            if (id.startsWith("/")==false)
                idFilePath=sshConfigDir+"/"+id;
            
            idFilePath=resolvePath(idFilePath); 
            if (existsFile(idFilePath)==false)
            {
                logger.warnPrintf("SSH: Ignoring non existing identity file:%s\n",idFilePath); 
                //throw new FileNotFoundException("Can not add non existing (identity) file:"+idFilePath);
                allSet=false; 
            }
            else
            {
                if (existingIDs.contains(idFilePath))
                {
                    logger.infoPrintf("SSH: skipping already registered identity:%s\n",idFilePath); 
                }
                else
                {
                    jschInstance.addIdentity(idFilePath); 
                    logger.infoPrintf("SSH: adding existing identity:%s\n",idFilePath); 
                }
            }
        }
        
        return allSet; 
    }

    public String getSSHConfigDir()
    {
        if (sshConfig.sshConfigDir!=null)
            return this.sshConfig.sshConfigDir; 
        
        return getUserHomePath()+"/"+SSH_CONFIG_SIBDUR; 
    }
    
    public String[] getSSHIndentities() throws JSchException
    {  
        Vector<?> names = this.jschInstance.getIdentityNames(); 
        if (names==null)
            return null;
        
        int n=names.size(); 
        String namesArr[]=new String[n]; 
        
        for (int i=0;i<n;i++)
            namesArr[i]=names.get(i).toString(); 

        return namesArr; 
    }
    
    public String getKnownHostsFile()
    {
        if (this.sshConfig.sshKnownHostsFile!=null)
            return this.sshConfig.sshKnownHostsFile; 
        
        return getSSHConfigDir()+"/"+SSH_KNOWN_HOSTS;
    }
    
    public String getUserHomePath()
    {
        return Global.getUserHomeLocation().getPath();
    }

    public boolean setKnownHostsFile(String knownHostsFile) throws JSchException
    {
        // jcraft needs existing files: 
        if (existsFile(resolvePath(knownHostsFile))==false)
        {
            //throw new FileNotFoundException("File doesn't exists:"+knownHostsFile);
            return false; 
        }
        
        this.sshConfig.sshKnownHostsFile=knownHostsFile;
        jschInstance.setKnownHosts(knownHostsFile);
        return true; 
    }

    public Session getSession(String username, String hostname, int port) throws JSchException
    {
        return this.jschInstance.getSession(username, hostname,port); 
    }
    
    public int getFreeLocalPort(boolean registerPort)
    {
        return _getFreeLocalPort(registerPort);
    }

    public void registerUsedLocalPort(int port)
    {
        _registerUsedLocalPort(port);
    }
    
    public boolean hasFreeLocalPort(int port)
    {
        return _hasFreeLocalPort(port);
    }
    
    public int createLocalProxyHashPort(String user,String host,int port, boolean registerPort)
    {
        return _createLocalProxyHashPort(user,host,port,registerPort);
    }

    public void createOutgoingTunnel(Session session, int localPort, String remoteHost, int remotePort) throws JSchException
    {
        String optStr = "" + localPort + ":" + remoteHost.toLowerCase() + ":" + remotePort;
        logger.debugPrintf("createOutgoingTunnel(): %s\n",optStr); 
        
        String[] list = session.getPortForwardingL();
        
        for (String s : list)
        {
            logger.debugPrintf(" - checking existing portforwarding:%s\n",s);
            if (StringUtil.equalsIgnoreCase(optStr, s))
            {
                logger.warnPrintf(" -> Outgoing port forwarding already exists:%s\n", s);
                return;
            }
        }

        // jsch doesn it all:
        session.setPortForwardingL(localPort, remoteHost, remotePort);
        logger.infoPrintf("New SSH tunnel=%s\n", optStr);
    }

    public void createIncomingTunnel(Session session, int remotePort, String localHost, int localPort) throws JSchException
    {
        // jsch doesn it all:
        session.setPortForwardingR(remotePort, localHost, localPort);
    }


    public String resolvePath(String relativePath)
    {
        // use VFS ?
        java.io.File file=new java.io.File(relativePath); 
        return file.getAbsolutePath(); 
    }
    
    public boolean existsFile(String filePath)
    {
        // use VFS ? 
        java.io.File file=new java.io.File(filePath);
        return file.exists(); 
    }

}
