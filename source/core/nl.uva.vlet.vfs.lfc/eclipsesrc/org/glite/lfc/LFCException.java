package org.glite.lfc;

import java.io.IOException;

import org.ietf.jgss.GSSException;


/**
 * LFCException.
 * 
 * @author Piter T. de Boer, Spiros Koulouzis  
 */

public class LFCException extends Exception
{
	/** generated id */ 
	private static final long serialVersionUID = 7875496601935511425L;
	
	private int errorCode=0; 
	
	public LFCException(String message)
	{
		super(message);  
	}

	public LFCException(String message, Exception ex) 
	{
		super(message,ex); 
	}
	
	public LFCException(String message, int errornr, Exception ex) 
	{
		super(message+"\n"+LFCError.getMessage(errornr),ex);
		this.errorCode=errornr; 
	}
	
	public LFCException(String message, int errornr) 
	{
		super(message+"\n"+LFCError.getMessage(errornr));
		this.errorCode=errornr; 
	}

	/** Returns LFC Error Code */ 
	public int getErrorCode()
	{
		return this.errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	/** Returns LFC Error Message */ 
	public String getErrorString()
	{
		return LFCError.getMessage(errorCode); 
	}
	
}