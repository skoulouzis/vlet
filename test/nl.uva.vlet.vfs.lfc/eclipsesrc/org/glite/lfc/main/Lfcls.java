package org.glite.lfc.main;

/** 
 * Wrapper for 'lfcls' command. 
 * All LFC commands are in LfcCommand.
 *  
 * @author P.T de Boer 
 */
public class Lfcls
{
	public static void main(String args[])
	{
		LfcCommand.doLS(args); 
	}
}
