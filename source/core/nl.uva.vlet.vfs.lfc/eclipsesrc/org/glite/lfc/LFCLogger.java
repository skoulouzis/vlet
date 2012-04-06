package org.glite.lfc;

import java.io.PrintStream;

/**
 * LFC Logging class. 
 * <p>
 * Override this class with your own logger code, or set instance fields  
 * to configure logging. 
 */
public class LFCLogger 
{
	public PrintStream logStream=System.err; 
	
	public PrintStream errorStream=System.err; 
	
	public boolean printLog=false;

	public boolean printException=true;
	
	public boolean printIOLog=false; 
	
	public boolean printIOException=true;
	
	/** Default Logger Constructor */ 
	public LFCLogger()
	{
		// allow public subclass constructor. 
	}
	
	/** Create new Logger and use specified output and error streams */ 
	public LFCLogger(PrintStream stdout,PrintStream stderr)
	{
		this.errorStream=stderr; 
		this.logStream=stdout; 
	}
	
	public void logMessage(String msg)
	{
		if ((printLog) && (logStream!=null)) 	
			 logStream.println("LFCLogger:"+msg);
	}
	
	public void logException(Exception exc)
	{
		if ((printException) && (logStream!=null)) 
		{
			errorStream.println("LFCLogger: Exception:"+exc);
			exc.printStackTrace(errorStream); 
		}
	}
	public void ioLogMessage(String msg)
	{
		if ((printIOLog) && (logStream!=null)) 	
			 logStream.println("LFCLogger (IO):"+msg);
	}
	
	public void ioLogException(Exception exc)
	{
		if ((printIOException) && (errorStream!=null)) 
		{
			errorStream.println("LFCLogger (IO): Exception:"+exc);
			exc.printStackTrace(errorStream); 
		}
	}
}
