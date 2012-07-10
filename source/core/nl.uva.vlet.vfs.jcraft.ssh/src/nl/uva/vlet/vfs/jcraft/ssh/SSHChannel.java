package nl.uva.vlet.vfs.jcraft.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.data.StringHolder;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.io.VShellChannel;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * SSH Shell Channel. 
 *  
 */
public class SSHChannel implements VShellChannel
{
    public static class SSHChannelOptions
    {
        public boolean xforwarding = true;
        public String xhost = "localhost";
        public int xport = 6000;
        public boolean compression=false;
    }
    
    // ========================================================================
    
    // ========================================================================
    
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(SSHChannel.class);
        logger.setLevelToDebug(); 
    }
    
    public static SSHChannel createSSHChannel(VRSContext context,String user,String host,int port, SSHChannelOptions options) throws VlException
    {
        return new SSHChannel(context,user,host,port,options); 
    }
    
    // ========================================================================
    
    // ========================================================================
    
    public class MyUserInfo implements UserInfo
    {
        String passwd = null;
        String passphrase = null;

        public String getPassword()
        {
            return passwd;
        }

        public String getPassphrase()
        {
            return passphrase;
        }

     // this object has no acces to the gui... 
        public boolean promptPassword(String message)
        {
            passwd=promptSecret(message);
            return (passwd!=null);
        }

        public boolean promptPassphrase(String message)
        {
            passphrase=promptSecret(message);
            return (passphrase!=null);
        }

        public String promptSecret(String message)
        {
            StringHolder secretHolder=new StringHolder(null); 
            
            boolean result=vrsContext.getUI().askAuthentication(message,
                    secretHolder); 
             
            return secretHolder.value; 
        }


        public void showMessage(String message)
        {
           vrsContext.getUI().showMessage(message); 
        }
        
        public boolean promptYesNo(String message)
        {
            return vrsContext.getUI().askYesNo("Yes or No?",message, false); 
            
        }
    }
   
    // ========================================================================
    
    // ========================================================================
    
    private Object waitForObject=new Object(); 
    
    private Session jschSession=null; 
    
    private ChannelShell jschChannel=null; 
    
    private String termType="xterm"; 
    
    private String host = "localhost";

    private String user = "user";

    private int port = 22;

    private SSHChannelOptions sshOptions=new SSHChannelOptions();
  
    private OutputStream stdin=null; 

    private InputStream stdout=null;

    private VRSContext vrsContext;

    private JCraftClient jcraftClient; 
    
    public SSHChannel(VRSContext context, String user, String host,int port,SSHChannelOptions options) throws VlException
    {
        this.vrsContext=context; 
        this.user=user; 
        this.port=port; 
        this.host=host; 
        this.sshOptions=options;
        
        if (sshOptions==null) 
            sshOptions=new SSHChannelOptions(); //defaults 
        
        try
        {
            this.jcraftClient=new JCraftClient();
        }
        catch (JSchException e1)
        {
            throw new VlException("Could get/create JCraftClient",e1); 
        } 
        
        try
        {
            if (StringUtil.isNonWhiteSpace(this.sshOptions.xhost)==false)
                this.sshOptions.xhost = InetAddress.getLocalHost().getCanonicalHostName();
            
            if (this.sshOptions.xport<=0)
                this.sshOptions.xport=6000; 
            
        }
        catch (UnknownHostException e)
        {
            logger.logException(ClassLogger.WARN,e,"Couldn't determine local host name: %s\n",e); 
        }
    }
    
    public void connect() throws IOException
    {
        try
        {
         
            jschSession=createSession(user,host,port,sshOptions,new MyUserInfo()); 
        }
        catch (JSchException e)
        {
           throw new IOException("Coudn't create Session to:"+this,e);   
        }
        
        connectTo(jschSession);
    }
    
    
    /** Connect using already authentication JSCHSession */ 
    void connectTo(Session jschSession) throws IOException
    {
        try
        {
        
            // =============================
            // Shell Channel 
            // =============================
            
            jschChannel = (ChannelShell) jschSession.openChannel("shell");
            jschChannel.setPtyType(this.termType);
            
            if (sshOptions.xforwarding)
            {
                jschSession.setX11Host(sshOptions.xhost);
                jschSession.setX11Port(sshOptions.xport);
                jschChannel.setXForwarding(true);
            }

            this.stdin = jschChannel.getOutputStream();
            this.stdout  = jschChannel.getInputStream();
            
            jschChannel.connect();
        }
        catch (IOException e)
        {
            throw e; 
        }
        catch (JSchException e)
        {
           throw new IOException("Coudn't connect",e);  
        }
    }

    protected Session createSession(String sesUser, String sesHost,int sesPort,SSHChannelOptions sshOptions, MyUserInfo ui) throws JSchException
    {
      // use VRSContext settings: 
      //String sshdir=Global.getUserHome()+"/.ssh";
      //String id1=sshdir+"/id_rsa";
      
      if (sesPort <= 0)
          sesPort = 22;
      
      Session session = jcraftClient.getSession(sesUser, sesHost, sesPort);
      session.setUserInfo(ui);
      
      java.util.Properties config = new java.util.Properties();

      if (sshOptions.compression == false)
      {
          config.put("compression.s2c", "none");
          config.put("compression.c2s", "none");
      }
      else
      {
          config.put("compression.s2c", "zlib,none");
          config.put("compression.c2s", "zlib,none");
      }

      session.setConfig(config);
      session.connect();
      
      return session; 
      
    }

    @Override
    public OutputStream getStdin()
    {
        return this.stdin; 
    }

    @Override
    public InputStream getStdout()
    {
        return this.stdout; 
    }

    @Override
    public InputStream getStderr()
    {
        return null;
    }

    @Override
    public void disconnect()
    {
        if (this.jschChannel!=null)
            this.jschChannel.disconnect();
        
        if (this.jschSession!=null)
            this.jschSession.disconnect();
        
        this.jschChannel=null;
        
        this.jschSession=null;
        
        
        synchronized(this.waitForObject)
        {
            this.waitForObject.notifyAll();
        }
        
    }
    
    public String toString()
    {
        return "SSHChannel://"+user+"@"+host+":"+port; 
    }
    
    public void setXForwarding(boolean enable)
    {
        this.sshOptions.xforwarding=enable; 
        
    }
    
    public void setXForwarding(boolean enable,String host,int port)
    {
        this.sshOptions.xforwarding=enable; 
        this.sshOptions.xhost=host;
        this.sshOptions.xport=port; 
    }
    
    public void setPtyType(String type)
    {
        this.termType=type; 
        if (this.jschChannel!=null)
            this.jschChannel.setPtyType(type);
        else
            logger.errorPrintf("setPtyType(): NOT connected!\n"); 
    }
    
    public void setCompression(boolean enable)
    {
        this.sshOptions.compression=enable; 
    }

    public void setPtySize(int col,int row,int wp,int hp)
    {
        if (this.jschChannel!=null)
            this.jschChannel.setPtySize(col,row,wp,hp);
        else
            logger.errorPrintf("setPtySize(): NOT connected!\n"); 
    }

    @Override
    public String getTermType()
    {
        return this.termType;  
    }

    @Override
    public boolean setTermType(String type)
    {
        this.setPtyType(type); 
        return true; 
    }

    @Override
    public boolean setTermSize(int col, int row, int wp, int hp)
    {
        this.setPtySize(col, row, wp, hp);
        return true;
    }

    @Override
    public int[] getTermSize()
    {
        return null;
        // must use ctrl sequence
    }

    @Override
    public void waitFor() throws InterruptedException
    {
        boolean wait=true;
        while(wait)
        {
            this.waitForObject.wait(30*1000);
            if (this.jschChannel.isClosed()==true)
                wait=false; 

            // throw new InterruptedException(); 
        }
    }

    @Override
    public int exitValue()
    {
        return jschChannel.getExitStatus(); 
    }

    public void setCWD(String path)
    {
        
        String cmd="cd \""+path+"\"; clear; echo 'VLTerm started...' ; \n"; 
        
        try
        {
            this.stdin.write(cmd.getBytes());
            this.stdin.flush(); 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }
    
}
