package test;

import nl.uva.vlet.Global;

import nl.uva.vlet.vfs.irods.IrodsFSFactory;
import nl.uva.vlet.vrs.VRS;

public class TestIrodsBrowser
{
	public static void main(String args[])
	{
		try
		{
			Global.init();
			VRS.getRegistry().addVRSDriverClass(IrodsFSFactory.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		nl.uva.vlet.gui.startVBrowser.main(args);
	}


}
