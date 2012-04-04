package nl.uva.vlet.vdriver.vfs.localfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.ClassLogger;
import nl.uva.vlet.Global;
import nl.uva.vlet.vrs.io.VShellChannel;

/**
 * 
 * Open BASH Shell Channel to local filesystem 
 */
public class BASHChannel implements VShellChannel
{
    // ========================================================================
    
    // ========================================================================
    
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(BASHChannel.class);
        logger.setLevelToDebug(); 
    }
    
    public static BASHChannel create()
    {
        return new BASHChannel(); 
        
    }
    
    // ========================================================================
    
    // ========================================================================
    private Process shellProcess=null;
    private InputStream inps=null;
    private OutputStream outps=null;
    private InputStream errs=null;

    @Override
    public OutputStream getStdin()
    {
        return outps;
    }

    @Override
    public InputStream getStdout()
    {
        return inps;
    }

    @Override
    public InputStream getStderr()
    {
        return errs;
    }

    @Override
    public void connect() throws IOException
    {
     
        this.shellProcess = null;

        try
        {
            boolean plainbash = false;
            String cmds[] = null;

            // pseudo tty which invokes bash.

            if (Global.isLinux())
            {
                cmds = new String[1];
                // linux executable .lxe :-)
                cmds[0] = Global.getInstallBaseDir().getPath()+"/bin/ptty.lxe"; 
            }
            else if (Global.isWindows())
            {
                cmds = new String[1];
                cmds[0] = Global.getInstallBaseDir().getPath()+"/bin/ptty.exe"; 
            }
            else
            {
                Global.errorPrintf(this,"exec bash: Can't determine OS:%s\n",Global.getOSName());
                return; 
            }

            shellProcess = Runtime.getRuntime().exec(cmds);
            inps = shellProcess.getInputStream();
            outps = shellProcess.getOutputStream();
            errs = shellProcess.getErrorStream();

            // final PseudoTtty ptty;

            if (plainbash)
            {
                errs = shellProcess.getErrorStream();
                // ptty=new PseudoTtty(inps,outps,errs);
                // inps=ptty.getInputStream();
                // outps=ptty.getOutputStream();
            }
            else
            {
                // ptty=null;
            }
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"Couldn't initialize bash session:%s\n",e);
        }
    }

    @Override
    public void disconnect() 
    {
        if (this.shellProcess!=null)
            this.shellProcess.destroy(); 
        this.shellProcess=null; 
    }

    @Override
    public String getTermType()
    {
        return null;
    }

    @Override
    public boolean setTermType(String type)
    {
        logger.warnPrintf("Can't set TERM type to:%s\n",type);
        return false;
    }

    @Override
    public boolean setTermSize(int col, int row, int wp, int hp)
    {
        logger.warnPrintf("Can't set TERM type to:%dx%dx%dx%d\n",col,row,wp,hp); 
        return false;
    }

    @Override
    public int[] getTermSize()
    {
        return null;
    }

    public void waitFor() throws InterruptedException
    {
        this.shellProcess.waitFor(); 
    }

    public int exitValue()
    {
        return this.shellProcess.exitValue(); 
    }

}
