package nl.uva.vlet.vfs.jcraft.ssh;

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
        public String sshConfigDir = null;    // default to $HOME/.ssh 
        public String sshKnownHostsFile = null;  // default to $HOME/.ssh/known_hosts
        public String sshIds[]=null;          // defaul to {"id_rsa"}
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
            this.sshConfig=new SSHConfig();
            // auto init
            this.sshConfig.sshConfigDir=this.getSSHConfigDir();
            // auto init
            this.sshConfig.sshKnownHostsFile=this.getKnownHostsFile();
            // auto init
            this.sshConfig.sshIds=new String[]{SSH_DEFAULT_ID_RSA};
        }
        
        logger.infoPrintf(" - sshConfigDir = %s\n",this.sshConfig.sshConfigDir); 
        logger.infoPrintf(" - known_hosts  = %s\n",this.sshConfig.sshKnownHostsFile); 
        
        this.jschInstance.setKnownHosts(sshConfig.sshKnownHostsFile);
        this.setSSHIdentities(sshConfig.sshConfigDir,sshConfig.sshIds,false);    
    }
    
    /** Resolve identities and merge with identiry registry */ 
    public void mergeSSHIdentities(String[] idNames) throws JSchException
    {
        String sshDir=getSSHConfigDir(); 
        setSSHIdentities(sshDir,idNames,true);
    }

    /** 
     * Specify SSH Identities. Use names only. 
     * The Actual SSH Key files must exists in the SSH Config Dir 
     */
    public void setSSHIdentities(String idNames[]) throws JSchException
    {
        String sshDir=getSSHConfigDir(); 
        setSSHIdentities(sshDir,idNames,false);
    }
    
    public void setSSHIdentities(String sshConfigDir,String idNames[],boolean mergeIDs) throws JSchException
    {
        Set<String> existingIDs=new HashSet<String>(); 
        
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
        
        for (String id:idNames)
        {
            String idFilePath=id;
            // relative path: 
            if (id.startsWith("/")==false)
                idFilePath=sshConfigDir+"/"+id;
            
            java.io.File file=new java.io.File(idFilePath);
            
            if (file.exists()==false)
            {
                logger.errorPrintf("SSH: Identity doesn't exists:%s\n",idFilePath); 
                file=null;    
            }
            
            if (file!=null) 
            {
                // resolve path:
                idFilePath=file.getAbsolutePath(); 
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
        
        return getSSHConfigDir()+"/known_hosts"; 
    }
    
    public void setSSHConfigDir(String sshDir)
    {
        this.sshConfig.sshConfigDir=sshDir;
    }
    
    public String getSSHConfigDir()
    {
        if (sshConfig.sshConfigDir!=null)
            return this.sshConfig.sshConfigDir; 
        
        return getUserHomePath()+"/.ssh"; 
    }
    
    public String getUserHomePath()
    {
        return Global.getUserHomeLocation().getPath();
    }

    public void setKnownHostsFile(String knownHostsFile) throws JSchException
    {
        this.sshConfig.sshKnownHostsFile=knownHostsFile;
        jschInstance.setKnownHosts(knownHostsFile);
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


}
