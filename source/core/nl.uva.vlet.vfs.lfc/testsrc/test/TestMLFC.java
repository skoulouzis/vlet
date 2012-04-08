/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TestMLFC.java,v 1.4 2011-06-07 15:15:10 ptdeboer Exp $  
 * $Date: 2011-06-07 15:15:10 $
 */ 
// source: 

package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;


import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.glite.lfc.LFCException;
import nl.uva.vlet.glite.lfc.LFCServer;
import nl.uva.vlet.glite.lfc.internal.FileDesc;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.lfc.LFCClient;


public class TestMLFC
{

	public static void main(String args[])
	{
//		try
//		{
//			Registry.getDefault().addVRSDriverClass(nl.uva.vlet.vfs.lfcfs.LFC_FS.class);
//		}
//		catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		// Full Runtime ? (TODO)
		// initEclipse();

		// init only for the Modified classes
		initEclipseLFCActivators();

		// todo ?
		// testReadFile
		// testCreateFile
		// testWritefile

		testLFCHandler();

		// Todo:
		// testMLFCStoreReadWrite();

		// testMLFCStoreDir();
	}

	private static void initEclipseRuntime()
	{
//		// BaseAdaptor baseAdaptor=new BaseAdaptor(null);
//		// Framework frameWork=new Framework(baseAdaptor);
//
//		// InternalPlatform pf = InternalPlatform.getDefault();
//		// MyRuntimePlugin myRunTime=new MyRuntimePlugin();
//		// pf.setRuntimeInstance(myRunTime);
//
//		// BundleHost bundleHost = new BundleHost();
//
//		BundleContext bundleContext = new MyBundleContext();
//
//		try
//		{
//
//			// start reporting first:
//			new eu.geclipse.core.reporting.ReportingPlugin()
//					.start(bundleContext);
//
//			// Internal Adaptors.
//
//			{
//				Object service = org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo
//						.getDefault();
//				bundleContext
//						.registerService(
//								org.eclipse.osgi.service.environment.EnvironmentInfo.class
//										.getName(), service, null);
//			}
//
//			{
//				Object service = new org.eclipse.core.runtime.internal.adaptor.BasicLocation(
//						"basiclocation", new java.net.URL(
//								"file:/home/ptdeboer/gtext"), true);
//				bundleContext.registerService(
//						org.eclipse.osgi.service.datalocation.Location.class
//								.getName(), service, null);
//			}
//
//			{
//				Object service = new DummyFrameWorkLog();
//				bundleContext.registerService(
//						org.eclipse.osgi.framework.log.FrameworkLog.class
//								.getName(), service, null);
//			}
//			new eu.geclipse.core.internal.Activator().start(bundleContext);
//			new org.eclipse.core.internal.runtime.Activator()
//					.start(bundleContext);
//			new org.eclipse.core.internal.registry.osgi.Activator()
//					.start(bundleContext);
//			new org.eclipse.core.internal.filesystem.Activator()
//					.start(bundleContext);
//			new eu.geclipse.lfc.internal.Activator().start(bundleContext);
//
//			//
//			// TODO: Register Extentions points !
//			//
//
//			// IExtensionRegistry extReg = RegistryFactory.getRegistry();
//			// extReg.addContribution(inputstream, icontributor, flag, s,
//			// resourcebundle, obj)
//
//			// 
//			// initialize Platform
//			//
//
//			org.eclipse.core.internal.runtime.InternalPlatform.getDefault()
//					.start(bundleContext);
//
//			//
//			// Tests
//			//
//
//			IExtensionPoint point = RegistryFactory.getRegistry()
//					.getExtensionPoint("org.eclipse.core.filesystem",
//							"filesystems");
//
//			if (point == null)
//			{
//				System.err
//						.println("***\n*** Warning:  FileSystem extension point not found! \n***");
//			}
//
//			IExtensionPoint problemReporting = RegistryFactory.getRegistry()
//					.getExtensionPoint(
//							"eu.geclipse.core.reporting.problemReporting");
//
//			if (problemReporting == null)
//			{
//				System.err
//						.println("***\n*** Warning:  ProblemReporting extension point not found! \n***");
//			}
//
//			IRegistryProvider defaultRegistryProvider = RegistryProviderFactory
//					.getDefault();
//
//			if (defaultRegistryProvider == null)
//				System.err.println("*** (I) IRegistryProvider is NULL ***");
//
//			// RegistryProviderOSGI.getRegistry()?
//
//			IExtensionRegistry regTest = RegistryFactory.getRegistry();
//
//			if (regTest == null)
//				System.err
//						.println("*** (II) IExtensionRegistry RegistryFactory.getRegistry() is NULL ***");
//
//		}
//		catch (Throwable e)
//		{
//			Throwable current = e;
//
//			while (current != null)
//			{
//				e.printStackTrace();
//
//				for (StackTraceElement se : e.getStackTrace())
//				{
//					System.err.println(" " + se.getClassName() + "."
//							+ se.getMethodName() + "(" + se.getFileName() + "#"
//							+ se.getLineNumber() + ")");
//				}
//				current = current.getCause();
//			}
//
//		}
//
//		/*
//		 * IExtensionRegistry reg = Platform.getExtensionRegistry();
//		 * IExtensionPoint point =
//		 * reg.getExtensionPoint("org.eclipse.core.filesystem.filesystems");
//		 * IConfigurationElement[] elements = point.getConfigurationElements();
//		 * 
//		 * System.out.println("There are " + elements.length + "
//		 * implementations"); for(int i=0;i<elements.length;i++) {
//		 * System.out.println(elements[i].getAttribute("scheme")); }
//		 */

	}

	public static void initEclipseLFCActivators()
	{
//		BundleContext bundleContext = new MyBundleContext();
//
//		try
//		{
//			/*
//			 * // start reporting first: new
//			 * eu.geclipse.core.reporting.ReportingPlugin().start(bundleContext); //
//			 * Internal Adaptors. { Object service =
//			 * org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo.getDefault();
//			 * bundleContext.registerService(org.eclipse.osgi.service.environment.EnvironmentInfo.class.getName(),service,null); }
//			 *  { Object service = new
//			 * org.eclipse.core.runtime.internal.adaptor.BasicLocation("basiclocation",new
//			 * java.net.URL("file:/home/ptdeboer/gtext"),true);
//			 * bundleContext.registerService(org.eclipse.osgi.service.datalocation.Location.class.getName(),service,null); } {
//			 * Object service = new DummyFrameWorkLog();
//			 * bundleContext.registerService(org.eclipse.osgi.framework.log.FrameworkLog.class.getName(),service,null); }
//			 * new eu.geclipse.core.internal.Activator().start(bundleContext);
//			 * new
//			 * org.eclipse.core.internal.runtime.Activator().start(bundleContext);
//			 * new
//			 * org.eclipse.core.internal.registry.osgi.Activator().start(bundleContext);
//			 */
//			new org.eclipse.core.internal.filesystem.Activator()
//					.start(bundleContext);
//			new eu.geclipse.lfc.internal.Activator().start(bundleContext);
//		}
//		catch (Throwable e)
//		{
//			Throwable current = e;
//
//			while (current != null)
//			{
//				e.printStackTrace();
//
//				for (StackTraceElement se : e.getStackTrace())
//				{
//					System.err.println(" " + se.getClassName() + "."
//							+ se.getMethodName() + "(" + se.getFileName() + "#"
//							+ se.getLineNumber() + ")");
//				}
//				current = current.getCause();
//			}
//
//		}
	}

	
//	public static void testLFCFileSystem()
//	{
//		MLFCFileSystem lfcfs = new MLFCFileSystem();
//
//		try
//		{
//			URI lfcuri;
//
//			lfcuri = new URI("lfn://lfc.grid.sara.nl:5010/grid");
//			IFileStore lfcStore = lfcfs.getStore(lfcuri);
//
//			SimpleProgressMonitor pmon = new SimpleProgressMonitor();
//
//			pmon.setTaskName("test lfc list()");
//
//			// SubProgressMonitor pmon=new SubProgressMonitor(null, 0);
//
//			// IFileInfo[] childs = lfcStore.childInfos(100, pmon);
//			IFileInfo[] childs = lfcStore.childInfos(100, pmon);
//
//			for (IFileInfo info : childs)
//			{
//				System.out.println("child=" + info);
//			}
//
//		}
//		catch (URISyntaxException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (CoreException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public static void testMLFCServer()
	{
		URI lfcuri;

		try
		{
			lfcuri = new URI("lfn://lfc.grid.sara.nl:5010");
			LFCServer lfcServer = new LFCServer(lfcuri);

			lfcServer.connect();

			ArrayList<FileDesc> files = lfcServer.listDirectory("/grid/");

			for (FileDesc file : files)
			{
				System.err.println(" - " + file.getFileName());
			}

		}
		catch (URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (LFCException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void testMLFCStoreDir()
//	{
//
//		try
//		{
//			URI lfcuri = new URI("lfn://lfc.grid.sara.nl:5010/grid");
//
//			MLFCStore store = new MLFCStore(lfcuri);
//
//			SimpleProgressMonitor pmon = new SimpleProgressMonitor();
//
//			pmon.setTaskName("test lfc list()");
//
//			int options = 0;
//
//			String[] names = store.childNames(options, pmon);
//
//			/*
//			 * ArrayList<FileDesc> files = lfcServer.listDirectory("/grid/");
//			 * 
//			 * for (FileDesc file:files) { System.err.println(" -
//			 * "+file.getFileName() ); }
//			 */
//
//			for (String name : names)
//			{
//				message(" - " + name);
//			}
//
//			// ADD trailing slash "/" to indicate Directory !
//
//			URI testStoreURI = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piter/");
//			MLFCStore testStore = new MLFCStore(testStoreURI);
//
//			IFileInfo[] infos = testStore.childInfos(options, pmon);
//
//			for (IFileInfo info : infos)
//			{
//				System.err.println(" - " + info);
//			}
//
//			// directory might not exist:
//			IFileStore subDir = testStore.getChild("testdir1");
//			IFileInfo subDirInfo = subDir.fetchInfo();
//
//			message(" I) testdir1.exists() before creation:"
//					+ subDirInfo.exists());
//
//			IFileStore result = subDir.mkdir(options, pmon);
//			subDirInfo = subDir.fetchInfo();
//
//			message("Result of mkdir testdir1=" + result);
//			message(" II) testdir1.exists() after creation:"
//					+ subDirInfo.exists());
//
//			subDir.delete(0, pmon);
//			subDirInfo = subDir.fetchInfo();
//
//			// refetch info:
//			message(" III) testdir1.exists() after deletion:"
//					+ subDirInfo.exists());
//
//			lfcuri = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piter/testdir1");
//			MLFCStore newSubDir = new MLFCStore(lfcuri);
//
//			subDirInfo = newSubDir.fetchInfo();
//
//			message(" IV) testdir1.exists() after deletion AND recreating LFCStore:"
//					+ subDirInfo.exists());
//
//			//
//			// Test InputStream :
//			//
//
//			URI inputFileURI = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piterb/ff.tgz");
//
//			// first check if it has replicas:
//
//			MLFCStore inputFileStore = new MLFCStore(inputFileURI);
//
//			// MUST CALL FETCH FIRST
//			inputFileStore.fetchFileDesc();
//
//			URI[] replicas = inputFileStore.listReplicas();
//
//			for (URI uri : replicas)
//			{
//				message(" - replica=" + uri);
//			}
//
//		}
//		catch (URISyntaxException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ProblemException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (CoreException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

//	public static void testMLFCStoreReadWrite()
//	{
//
//		try
//		{
//			SimpleProgressMonitor pmon = new SimpleProgressMonitor();
//
//			pmon.setTaskName("test lfc read/write()");
//			int options = 0;
//
//			// ADD trailing slash "/" to indicate Directory !
//
//			URI testStoreURI = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piter/");
//			FileStore testStore = new MLFCStore(testStoreURI);
//
//			//
//			// Test InputStream :
//			//
//
//			URI inputFileURI = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piterb/ff.tgz");
//
//			// first check if it has replicas:
//
//			MLFCStore inputFileStore = new MLFCStore(inputFileURI);
//
//			// MUST CALL FETCH FIRST
//			inputFileStore.fetchFileDesc();
//
//			URI[] replicas = inputFileStore.listReplicas();
//
//			for (URI uri : replicas)
//			{
//				message(" - replica=" + uri);
//			}
//
//			// 
//			// Test InputStream:
//			//
//
//			try
//			{
//				InputStream inps = inputFileStore
//						.openInputStream(options, pmon);
//
//				byte buffer[] = new byte[1024];
//
//				int numRead = inps.read(buffer, 0, 1024);
//
//				if (numRead > 0)
//					message("yeah read bytes ! nrread=" + numRead);
//				else
//				{
//					message("*** Error: Nothing read from:" + inputFileURI);
//				}
//
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			//
//			// Test OutputStream
//			//
//
//			// get not created file
//			URI outputFileURI = new URI(
//					"lfn://lfc.grid.sara.nl:5010/grid/pvier/piterb/testwrite.txt");
//
//			// first check if it has replicas:
//			MLFCStore outputFileStore = new MLFCStore(outputFileURI);
//
//			// create/Register ?
//			// Call fetchFile also ?
//			FileDesc info = outputFileStore.fetchFileDesc();
//
//			if (info == null)
//				message("FileDesc is null ");
//
//			// List Replicas first before opening ?
//			outputFileStore.listReplicas();
//
//			// Failes:???
//
//			OutputStream outps = outputFileStore.openOutputStream(0, pmon);
//
//			String testStr = "Hello LFC";
//			byte buf1[] = testStr.getBytes();
//
//			try
//			{
//				// write to stream
//				outps.write(buf1);
//				outps.close();
//
//				//
//				// Now reread stream
//				// 
//
//				InputStream inps = outputFileStore.openInputStream(0, pmon);
//
//				byte buf2[] = new byte[buf1.length];
//				int numRead = inps.read(buf2, 0, buf1.length);
//
//				String resultStr = new String(buf2);
//				message("Reread string:" + resultStr);
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		catch (URISyntaxException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ProblemException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (CoreException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static void testLFCHandler()
	{
			LFCClient c = null;
			
			try
			{
				c = new LFCClient("lfn://lfc.grid.sara.nl:5010/");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			try
			{
				c.connect();
			}
			catch (VlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			String path = "/grid/pvier/";
			
			
//			try
//			{
//				VFSNode[] nodes = c.list(path);
//				for (VFSNode node : nodes)
//				{
//					System.err.println(" - " + node.getPath());
//				}
//			}
//			catch (VlException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			ArrayList<FileDesc> files = null;
//			try
//			{
//				files = ser.listDirectory(path);
//			}
//			catch (LFCException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

//			for (FileDesc file : files)
//			{
//				System.err.println(" - " + file.getFileName());
//			}
//			
//			try
//			{
//				c.getPath("/grid/pvier/ssdsdds");
//			}
//			catch (VlException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			try
//			{
//				c.mkdir("/grid/pvier/spiros/", true);
//				
//				c.getPath("/grid/pvier/spiros/");
//			}
//			catch (VlException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		

	}

	
	private static void debug(String msg)
	{
		message(msg);
		
	}

	private static void message(String msg)
	{
		// write to stderr to avoid stream mixing
		System.err.println("testMLFC:" + msg);

	}

}
